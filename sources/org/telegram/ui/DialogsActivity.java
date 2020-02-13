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
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScrollerCustom;
import androidx.recyclerview.widget.RecyclerView;
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
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.MenuDrawable;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.DialogsAdapter;
import org.telegram.ui.Adapters.DialogsSearchAdapter;
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
import org.telegram.ui.Components.RecyclerAnimationScrollHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.DialogsActivity;

public class DialogsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int ARCHIVE_ITEM_STATE_HIDDEN = 2;
    private static final int ARCHIVE_ITEM_STATE_PINNED = 0;
    private static final int ARCHIVE_ITEM_STATE_SHOWED = 1;
    private static final int archive = 105;
    private static final int block = 106;
    private static final int clear = 103;
    private static final int delete = 102;
    public static boolean[] dialogsLoaded = new boolean[3];
    /* access modifiers changed from: private */
    public static ArrayList<TLRPC.Dialog> frozenDialogsList = null;
    private static final int mute = 104;
    private static final int pin = 100;
    private static final int read = 101;
    public static float viewOffset;
    private ArrayList<View> actionModeViews = new ArrayList<>();
    private String addToGroupAlertString;
    /* access modifiers changed from: private */
    public float additionalFloatingTranslation;
    private boolean afterSignup;
    /* access modifiers changed from: private */
    public boolean allowMoving;
    /* access modifiers changed from: private */
    public boolean allowSwipeDuringCurrentTouch;
    private boolean allowSwitchAccount;
    private ActionBarMenuSubItem archiveItem;
    /* access modifiers changed from: private */
    public int archivePullViewState;
    private AnimatedArrowDrawable arrowDrawable;
    private boolean askAboutContacts = true;
    private BackDrawable backDrawable;
    private ActionBarMenuSubItem blockItem;
    private int canClearCacheCount;
    private int canMuteCount;
    private int canPinCount;
    private int canReadCount;
    private int canReportSpamCount;
    /* access modifiers changed from: private */
    public boolean canShowHiddenArchive;
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
    public DialogsAdapter dialogsAdapter;
    /* access modifiers changed from: private */
    public DialogsItemAnimator dialogsItemAnimator;
    /* access modifiers changed from: private */
    public boolean dialogsListFrozen;
    /* access modifiers changed from: private */
    public DialogsSearchAdapter dialogsSearchAdapter;
    /* access modifiers changed from: private */
    public int dialogsType;
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
    private int hasPoll;
    /* access modifiers changed from: private */
    public ItemTouchHelper itemTouchhelper;
    /* access modifiers changed from: private */
    public int lastItemsCount;
    /* access modifiers changed from: private */
    public boolean lastSearchScrolledToTop;
    /* access modifiers changed from: private */
    public LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public DialogsRecyclerView listView;
    /* access modifiers changed from: private */
    public MenuDrawable menuDrawable;
    private int messagesCount;
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
    private ActionBarMenuItem passcodeItem;
    private AlertDialog permissionDialog;
    private ActionBarMenuItem pinItem;
    /* access modifiers changed from: private */
    public int prevPosition;
    /* access modifiers changed from: private */
    public int prevTop;
    /* access modifiers changed from: private */
    public RadialProgressView progressView;
    private ProxyDrawable proxyDrawable;
    /* access modifiers changed from: private */
    public ActionBarMenuItem proxyItem;
    /* access modifiers changed from: private */
    public boolean proxyItemVisisble;
    public PullForegroundDrawable pullForegroundDrawable;
    private ActionBarMenuSubItem readItem;
    private boolean resetDelegate = true;
    private RecyclerAnimationScrollHelper scrollHelper;
    /* access modifiers changed from: private */
    public boolean scrollUpdated;
    /* access modifiers changed from: private */
    public boolean scrollingManually;
    /* access modifiers changed from: private */
    public AnimatorSet searchAnimator;
    private long searchDialogId;
    /* access modifiers changed from: private */
    public EmptyTextProgressView searchEmptyView;
    /* access modifiers changed from: private */
    public RecyclerListView searchListView;
    private TLObject searchObject;
    /* access modifiers changed from: private */
    public String searchString;
    /* access modifiers changed from: private */
    public boolean searchWas;
    /* access modifiers changed from: private */
    public boolean searching;
    /* access modifiers changed from: private */
    public LinearLayoutManager searchlayoutManager;
    private String selectAlertString;
    private String selectAlertStringGroup;
    private NumberTextView selectedDialogsCountTextView;
    private RecyclerView sideMenu;
    /* access modifiers changed from: private */
    public DialogCell slidingView;
    /* access modifiers changed from: private */
    public long startArchivePullingTime;
    /* access modifiers changed from: private */
    public SwipeController swipeController;
    /* access modifiers changed from: private */
    public ActionBarMenuItem switchItem;
    /* access modifiers changed from: private */
    public UndoView[] undoView = new UndoView[2];
    /* access modifiers changed from: private */
    public boolean updatePullAfterScroll;
    /* access modifiers changed from: private */
    public boolean waitingForScrollFinished;

    public interface DialogsActivityDelegate {
        void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList<Long> arrayList, CharSequence charSequence, boolean z);
    }

    static /* synthetic */ boolean lambda$createView$2(View view, MotionEvent motionEvent) {
        return true;
    }

    static /* synthetic */ int access$1408(DialogsActivity dialogsActivity) {
        int i = dialogsActivity.lastItemsCount;
        dialogsActivity.lastItemsCount = i + 1;
        return i;
    }

    static /* synthetic */ int access$1410(DialogsActivity dialogsActivity) {
        int i = dialogsActivity.lastItemsCount;
        dialogsActivity.lastItemsCount = i - 1;
        return i;
    }

    private class ContentView extends SizeNotifierFrameLayout {
        private int inputFieldHeight;

        public boolean hasOverlappingRendering() {
            return false;
        }

        public ContentView(Context context) {
            super(context, SharedConfig.smoothKeyboard);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            int size = View.MeasureSpec.getSize(i);
            int size2 = View.MeasureSpec.getSize(i2);
            setMeasuredDimension(size, size2);
            int paddingTop = size2 - getPaddingTop();
            measureChildWithMargins(DialogsActivity.this.actionBar, i, 0, i2, 0);
            int keyboardHeight = SharedConfig.smoothKeyboard ? 0 : getKeyboardHeight();
            int childCount = getChildCount();
            if (DialogsActivity.this.commentView != null) {
                measureChildWithMargins(DialogsActivity.this.commentView, i, 0, i2, 0);
                Object tag = DialogsActivity.this.commentView.getTag();
                if (tag == null || !tag.equals(2)) {
                    this.inputFieldHeight = 0;
                } else {
                    if (keyboardHeight <= AndroidUtilities.dp(20.0f) && !AndroidUtilities.isInMultiwindow) {
                        paddingTop -= DialogsActivity.this.commentView.getEmojiPadding();
                    }
                    this.inputFieldHeight = DialogsActivity.this.commentView.getMeasuredHeight();
                }
                if (SharedConfig.smoothKeyboard && DialogsActivity.this.commentView.isPopupShowing()) {
                    DialogsActivity.this.fragmentView.setTranslationY((float) DialogsActivity.this.getCurrentPanTranslationY());
                    DialogsActivity.this.listView.setTranslationY(0.0f);
                    DialogsActivity.this.searchListView.setTranslationY(0.0f);
                }
            }
            for (int i3 = 0; i3 < childCount; i3++) {
                View childAt = getChildAt(i3);
                if (!(childAt == null || childAt.getVisibility() == 8 || childAt == DialogsActivity.this.commentView || childAt == DialogsActivity.this.actionBar)) {
                    if (childAt == DialogsActivity.this.listView || childAt == DialogsActivity.this.searchListView || childAt == DialogsActivity.this.progressView || childAt == DialogsActivity.this.searchEmptyView) {
                        childAt.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), (paddingTop - this.inputFieldHeight) + AndroidUtilities.dp(2.0f)), NUM));
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
        /* JADX WARNING: Removed duplicated region for block: B:35:0x0092  */
        /* JADX WARNING: Removed duplicated region for block: B:42:0x00ac  */
        /* JADX WARNING: Removed duplicated region for block: B:50:0x00d0  */
        /* JADX WARNING: Removed duplicated region for block: B:51:0x00e7  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onLayout(boolean r11, int r12, int r13, int r14, int r15) {
            /*
                r10 = this;
                int r11 = r10.getChildCount()
                org.telegram.ui.DialogsActivity r0 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.Components.ChatActivityEnterView r0 = r0.commentView
                if (r0 == 0) goto L_0x0017
                org.telegram.ui.DialogsActivity r0 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.Components.ChatActivityEnterView r0 = r0.commentView
                java.lang.Object r0 = r0.getTag()
                goto L_0x0018
            L_0x0017:
                r0 = 0
            L_0x0018:
                boolean r1 = org.telegram.messenger.SharedConfig.smoothKeyboard
                r2 = 0
                if (r1 == 0) goto L_0x001f
                r1 = 0
                goto L_0x0023
            L_0x001f:
                int r1 = r10.getKeyboardHeight()
            L_0x0023:
                r3 = 2
                if (r0 == 0) goto L_0x0047
                java.lang.Integer r4 = java.lang.Integer.valueOf(r3)
                boolean r0 = r0.equals(r4)
                if (r0 == 0) goto L_0x0047
                r0 = 1101004800(0x41a00000, float:20.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                if (r1 > r0) goto L_0x0047
                boolean r0 = org.telegram.messenger.AndroidUtilities.isInMultiwindow
                if (r0 != 0) goto L_0x0047
                org.telegram.ui.DialogsActivity r0 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.Components.ChatActivityEnterView r0 = r0.commentView
                int r0 = r0.getEmojiPadding()
                goto L_0x0048
            L_0x0047:
                r0 = 0
            L_0x0048:
                r10.setBottomClip(r0)
            L_0x004b:
                if (r2 >= r11) goto L_0x00fa
                android.view.View r1 = r10.getChildAt(r2)
                int r4 = r1.getVisibility()
                r5 = 8
                if (r4 != r5) goto L_0x005b
                goto L_0x00f6
            L_0x005b:
                android.view.ViewGroup$LayoutParams r4 = r1.getLayoutParams()
                android.widget.FrameLayout$LayoutParams r4 = (android.widget.FrameLayout.LayoutParams) r4
                int r5 = r1.getMeasuredWidth()
                int r6 = r1.getMeasuredHeight()
                int r7 = r4.gravity
                r8 = -1
                if (r7 != r8) goto L_0x0070
                r7 = 51
            L_0x0070:
                r8 = r7 & 7
                r7 = r7 & 112(0x70, float:1.57E-43)
                r8 = r8 & 7
                r9 = 1
                if (r8 == r9) goto L_0x0084
                r9 = 5
                if (r8 == r9) goto L_0x007f
                int r8 = r4.leftMargin
                goto L_0x008e
            L_0x007f:
                int r8 = r14 - r5
                int r9 = r4.rightMargin
                goto L_0x008d
            L_0x0084:
                int r8 = r14 - r12
                int r8 = r8 - r5
                int r8 = r8 / r3
                int r9 = r4.leftMargin
                int r8 = r8 + r9
                int r9 = r4.rightMargin
            L_0x008d:
                int r8 = r8 - r9
            L_0x008e:
                r9 = 16
                if (r7 == r9) goto L_0x00ac
                r9 = 48
                if (r7 == r9) goto L_0x00a4
                r9 = 80
                if (r7 == r9) goto L_0x009d
                int r4 = r4.topMargin
                goto L_0x00b8
            L_0x009d:
                int r7 = r15 - r0
                int r7 = r7 - r13
                int r7 = r7 - r6
                int r4 = r4.bottomMargin
                goto L_0x00b6
            L_0x00a4:
                int r4 = r4.topMargin
                int r7 = r10.getPaddingTop()
                int r4 = r4 + r7
                goto L_0x00b8
            L_0x00ac:
                int r7 = r15 - r0
                int r7 = r7 - r13
                int r7 = r7 - r6
                int r7 = r7 / r3
                int r9 = r4.topMargin
                int r7 = r7 + r9
                int r4 = r4.bottomMargin
            L_0x00b6:
                int r4 = r7 - r4
            L_0x00b8:
                org.telegram.ui.DialogsActivity r7 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.Components.ChatActivityEnterView r7 = r7.commentView
                if (r7 == 0) goto L_0x00f1
                org.telegram.ui.DialogsActivity r7 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.Components.ChatActivityEnterView r7 = r7.commentView
                boolean r7 = r7.isPopupView(r1)
                if (r7 == 0) goto L_0x00f1
                boolean r4 = org.telegram.messenger.AndroidUtilities.isInMultiwindow
                if (r4 == 0) goto L_0x00e7
                org.telegram.ui.DialogsActivity r4 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.Components.ChatActivityEnterView r4 = r4.commentView
                int r4 = r4.getTop()
                int r7 = r1.getMeasuredHeight()
                int r4 = r4 - r7
                r7 = 1065353216(0x3var_, float:1.0)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
                int r4 = r4 + r7
                goto L_0x00f1
            L_0x00e7:
                org.telegram.ui.DialogsActivity r4 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.Components.ChatActivityEnterView r4 = r4.commentView
                int r4 = r4.getBottom()
            L_0x00f1:
                int r5 = r5 + r8
                int r6 = r6 + r4
                r1.layout(r8, r4, r5, r6)
            L_0x00f6:
                int r2 = r2 + 1
                goto L_0x004b
            L_0x00fa:
                r10.notifyHeightChanged()
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.ContentView.onLayout(boolean, int, int, int, int):void");
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            int actionMasked = motionEvent.getActionMasked();
            if ((actionMasked == 1 || actionMasked == 3) && DialogsActivity.this.actionBar.isActionModeShowed()) {
                boolean unused = DialogsActivity.this.allowMoving = true;
            }
            return super.onInterceptTouchEvent(motionEvent);
        }
    }

    public class DialogsRecyclerView extends RecyclerListView {
        private boolean firstLayout = true;
        private boolean ignoreLayout;

        public DialogsRecyclerView(Context context) {
            super(context);
        }

        public void setViewsOffset(float f) {
            View findViewByPosition;
            DialogsActivity.viewOffset = f;
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                getChildAt(i).setTranslationY(f);
            }
            if (!(this.selectorPosition == -1 || (findViewByPosition = DialogsActivity.this.layoutManager.findViewByPosition(this.selectorPosition)) == null)) {
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
            PullForegroundDrawable pullForegroundDrawable = DialogsActivity.this.pullForegroundDrawable;
            if (!(pullForegroundDrawable == null || DialogsActivity.viewOffset == 0.0f)) {
                pullForegroundDrawable.drawOverScroll(canvas);
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

        /* access modifiers changed from: protected */
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

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            if (!(DialogsActivity.this.dialogRemoveFinished == 0 && DialogsActivity.this.dialogInsertFinished == 0 && DialogsActivity.this.dialogChangeFinished == 0) && !DialogsActivity.this.dialogsItemAnimator.isRunning()) {
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
                    DialogsActivity.this.listView.smoothScrollBy(0, dialogCell.getMeasuredHeight() + dialogCell.getTop(), CubicBezierInterpolator.EASE_OUT);
                    if (z) {
                        boolean unused2 = DialogsActivity.this.updatePullAfterScroll = true;
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
            int unused = DialogsActivity.this.archivePullViewState = SharedConfig.archiveHidden ? 2 : 0;
            DialogsActivity dialogsActivity = DialogsActivity.this;
            PullForegroundDrawable pullForegroundDrawable = dialogsActivity.pullForegroundDrawable;
            if (pullForegroundDrawable != null) {
                if (dialogsActivity.archivePullViewState != 0) {
                    z = true;
                }
                pullForegroundDrawable.setWillDraw(z);
            }
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            int findFirstVisibleItemPosition;
            if (this.animationRunning || DialogsActivity.this.waitingForScrollFinished || DialogsActivity.this.dialogRemoveFinished != 0 || DialogsActivity.this.dialogInsertFinished != 0 || DialogsActivity.this.dialogChangeFinished != 0) {
                return false;
            }
            int action = motionEvent.getAction();
            if (action == 0) {
                DialogsActivity.this.listView.setOverScrollMode(0);
            }
            if ((action == 1 || action == 3) && !DialogsActivity.this.itemTouchhelper.isIdle() && DialogsActivity.this.swipeController.swipingFolder) {
                boolean unused = DialogsActivity.this.swipeController.swipeFolderBack = true;
                if (DialogsActivity.this.itemTouchhelper.checkHorizontalSwipe((RecyclerView.ViewHolder) null, 4) != 0) {
                    toggleArchiveHidden(false, (DialogCell) null);
                }
            }
            boolean onTouchEvent = super.onTouchEvent(motionEvent);
            if ((action == 1 || action == 3) && DialogsActivity.this.archivePullViewState == 2 && DialogsActivity.this.hasHiddenArchive() && (findFirstVisibleItemPosition = DialogsActivity.this.layoutManager.findFirstVisibleItemPosition()) == 0) {
                View findViewByPosition = DialogsActivity.this.layoutManager.findViewByPosition(findFirstVisibleItemPosition);
                int dp = (int) (((float) AndroidUtilities.dp(SharedConfig.useThreeLinesLayout ? 78.0f : 72.0f)) * 0.85f);
                int top = findViewByPosition.getTop() + findViewByPosition.getMeasuredHeight();
                if (findViewByPosition != null) {
                    long currentTimeMillis = System.currentTimeMillis() - DialogsActivity.this.startArchivePullingTime;
                    if (top < dp || currentTimeMillis < 200) {
                        DialogsActivity.this.listView.smoothScrollBy(0, top, CubicBezierInterpolator.EASE_OUT_QUINT);
                        int unused2 = DialogsActivity.this.archivePullViewState = 2;
                    } else if (DialogsActivity.this.archivePullViewState != 1) {
                        if (DialogsActivity.this.listView.getViewOffset() == 0.0f) {
                            DialogsActivity.this.listView.smoothScrollBy(0, findViewByPosition.getTop(), CubicBezierInterpolator.EASE_OUT_QUINT);
                        }
                        if (!DialogsActivity.this.canShowHiddenArchive) {
                            boolean unused3 = DialogsActivity.this.canShowHiddenArchive = true;
                            DialogsActivity.this.listView.performHapticFeedback(3, 2);
                            PullForegroundDrawable pullForegroundDrawable = DialogsActivity.this.pullForegroundDrawable;
                            if (pullForegroundDrawable != null) {
                                pullForegroundDrawable.colorize(true);
                            }
                        }
                        ((DialogCell) findViewByPosition).startOutAnimation();
                        int unused4 = DialogsActivity.this.archivePullViewState = 1;
                    }
                    if (DialogsActivity.this.listView.getViewOffset() != 0.0f) {
                        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{DialogsActivity.this.listView.getViewOffset(), 0.0f});
                        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                                DialogsActivity.DialogsRecyclerView.this.lambda$onTouchEvent$0$DialogsActivity$DialogsRecyclerView(valueAnimator);
                            }
                        });
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
            return onTouchEvent;
        }

        public /* synthetic */ void lambda$onTouchEvent$0$DialogsActivity$DialogsRecyclerView(ValueAnimator valueAnimator) {
            DialogsActivity.this.listView.setViewsOffset(((Float) valueAnimator.getAnimatedValue()).floatValue());
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (this.animationRunning || DialogsActivity.this.waitingForScrollFinished || DialogsActivity.this.dialogRemoveFinished != 0 || DialogsActivity.this.dialogInsertFinished != 0 || DialogsActivity.this.dialogChangeFinished != 0) {
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

    class SwipeController extends ItemTouchHelper.Callback {
        private RectF buttonInstance;
        private RecyclerView.ViewHolder currentItemViewHolder;
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

        SwipeController() {
        }

        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
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
                            TLRPC.Dialog dialog = DialogsActivity.this.getMessagesController().dialogs_dict.get(dialogId);
                            if (!DialogsActivity.this.allowMoving || dialog == null || !dialog.pinned || DialogObject.isFolderDialogId(dialogId)) {
                                return 0;
                            }
                            DialogCell unused = DialogsActivity.this.movingView = (DialogCell) viewHolder.itemView;
                            DialogsActivity.this.movingView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                            return ItemTouchHelper.Callback.makeMovementFlags(3, 0);
                        } else if (DialogsActivity.this.allowSwipeDuringCurrentTouch && dialogId != ((long) DialogsActivity.this.getUserConfig().clientUserId) && dialogId != 777000 && !DialogsActivity.this.getMessagesController().isProxyDialog(dialogId, false)) {
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
                org.telegram.tgnet.TLRPC$Dialog r5 = (org.telegram.tgnet.TLRPC.Dialog) r5
                if (r5 == 0) goto L_0x0046
                boolean r5 = r5.pinned
                if (r5 == 0) goto L_0x0046
                boolean r5 = org.telegram.messenger.DialogObject.isFolderDialogId(r2)
                if (r5 == 0) goto L_0x0029
                goto L_0x0046
            L_0x0029:
                int r5 = r6.getAdapterPosition()
                int r6 = r7.getAdapterPosition()
                org.telegram.ui.DialogsActivity r7 = org.telegram.ui.DialogsActivity.this
                org.telegram.ui.Adapters.DialogsAdapter r7 = r7.dialogsAdapter
                r7.notifyItemMoved(r5, r6)
                org.telegram.ui.DialogsActivity r5 = org.telegram.ui.DialogsActivity.this
                r5.updateDialogIndices()
                org.telegram.ui.DialogsActivity r5 = org.telegram.ui.DialogsActivity.this
                r6 = 1
                boolean unused = r5.movingWas = r6
                return r6
            L_0x0046:
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
                    DialogsActivity.this.listView.toggleArchiveHidden(false, dialogCell);
                    return;
                }
                DialogCell unused = DialogsActivity.this.slidingView = dialogCell;
                int adapterPosition = viewHolder.getAdapterPosition();
                $$Lambda$DialogsActivity$SwipeController$p1WvioWe4QIXpl_h9JruXCBarGg r1 = new Runnable(DialogsActivity.this.dialogsAdapter.fixPosition(adapterPosition), DialogsActivity.this.dialogsAdapter.getItemCount(), adapterPosition) {
                    private final /* synthetic */ int f$1;
                    private final /* synthetic */ int f$2;
                    private final /* synthetic */ int f$3;

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
                        DialogsActivity dialogsActivity = DialogsActivity.this;
                        PacmanAnimation unused2 = dialogsActivity.pacmanAnimation = new PacmanAnimation(dialogsActivity.listView);
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

        public /* synthetic */ void lambda$onSwiped$1$DialogsActivity$SwipeController(int i, int i2, int i3) {
            RecyclerView.ViewHolder findViewHolderForAdapterPosition;
            if (DialogsActivity.frozenDialogsList != null) {
                TLRPC.Dialog dialog = (TLRPC.Dialog) DialogsActivity.frozenDialogsList.remove(i);
                int i4 = dialog.pinnedNum;
                DialogCell unused = DialogsActivity.this.slidingView = null;
                DialogsActivity.this.listView.invalidate();
                boolean z = false;
                int addDialogToFolder = DialogsActivity.this.getMessagesController().addDialogToFolder(dialog.id, DialogsActivity.this.folderId == 0 ? 1 : 0, -1, 0);
                int findLastVisibleItemPosition = DialogsActivity.this.layoutManager.findLastVisibleItemPosition();
                if (findLastVisibleItemPosition == i2 - 1) {
                    DialogsActivity.this.layoutManager.findViewByPosition(findLastVisibleItemPosition).requestLayout();
                }
                if (!(addDialogToFolder == 2 && i3 == 0)) {
                    DialogsActivity.this.dialogsItemAnimator.prepareForRemove();
                    DialogsActivity.access$1410(DialogsActivity.this);
                    DialogsActivity.this.dialogsAdapter.notifyItemRemoved(i3);
                    int unused2 = DialogsActivity.this.dialogRemoveFinished = 2;
                }
                if (DialogsActivity.this.folderId == 0) {
                    if (addDialogToFolder == 2) {
                        DialogsActivity.this.dialogsItemAnimator.prepareForRemove();
                        if (i3 == 0) {
                            int unused3 = DialogsActivity.this.dialogChangeFinished = 2;
                            DialogsActivity.this.setDialogsListFrozen(true);
                            DialogsActivity.this.dialogsAdapter.notifyItemChanged(0);
                        } else {
                            DialogsActivity.access$1408(DialogsActivity.this);
                            DialogsActivity.this.dialogsAdapter.notifyItemInserted(0);
                            if (!SharedConfig.archiveHidden && DialogsActivity.this.layoutManager.findFirstVisibleItemPosition() == 0) {
                                DialogsActivity.this.listView.smoothScrollBy(0, -AndroidUtilities.dp(SharedConfig.useThreeLinesLayout ? 78.0f : 72.0f));
                            }
                        }
                        DialogsActivity.frozenDialogsList.add(0, DialogsActivity.getDialogsArray(DialogsActivity.this.currentAccount, DialogsActivity.this.dialogsType, DialogsActivity.this.folderId, false).get(0));
                    } else if (addDialogToFolder == 1 && (findViewHolderForAdapterPosition = DialogsActivity.this.listView.findViewHolderForAdapterPosition(0)) != null) {
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
                    DialogsActivity.this.getUndoView().showWithAction(dialog.id, z ? 2 : 3, (Runnable) null, new Runnable(dialog, i4) {
                        private final /* synthetic */ TLRPC.Dialog f$1;
                        private final /* synthetic */ int f$2;

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
                    DialogsActivity.this.listView.setEmptyView((View) null);
                    DialogsActivity.this.progressView.setVisibility(4);
                }
            }
        }

        public /* synthetic */ void lambda$null$0$DialogsActivity$SwipeController(TLRPC.Dialog dialog, int i) {
            boolean unused = DialogsActivity.this.dialogsListFrozen = true;
            DialogsActivity.this.getMessagesController().addDialogToFolder(dialog.id, 0, i, 0);
            boolean unused2 = DialogsActivity.this.dialogsListFrozen = false;
            ArrayList<TLRPC.Dialog> dialogs = DialogsActivity.this.getMessagesController().getDialogs(0);
            int indexOf = dialogs.indexOf(dialog);
            if (indexOf >= 0) {
                ArrayList<TLRPC.Dialog> dialogs2 = DialogsActivity.this.getMessagesController().getDialogs(1);
                if (!dialogs2.isEmpty() || indexOf != 1) {
                    int unused3 = DialogsActivity.this.dialogInsertFinished = 2;
                    DialogsActivity.this.setDialogsListFrozen(true);
                    DialogsActivity.this.dialogsItemAnimator.prepareForRemove();
                    DialogsActivity.access$1408(DialogsActivity.this);
                    DialogsActivity.this.dialogsAdapter.notifyItemInserted(indexOf);
                }
                if (dialogs2.isEmpty()) {
                    dialogs.remove(0);
                    if (indexOf == 1) {
                        int unused4 = DialogsActivity.this.dialogChangeFinished = 2;
                        DialogsActivity.this.setDialogsListFrozen(true);
                        DialogsActivity.this.dialogsAdapter.notifyItemChanged(0);
                        return;
                    }
                    DialogsActivity.frozenDialogsList.remove(0);
                    DialogsActivity.this.dialogsItemAnimator.prepareForRemove();
                    DialogsActivity.access$1410(DialogsActivity.this);
                    DialogsActivity.this.dialogsAdapter.notifyItemRemoved(0);
                    return;
                }
                return;
            }
            DialogsActivity.this.dialogsAdapter.notifyDataSetChanged();
        }

        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int i) {
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
                AndroidUtilities.runOnUIThread(new Runnable(DialogsActivity.this.movingView) {
                    private final /* synthetic */ View f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void run() {
                        this.f$0.setBackgroundDrawable((Drawable) null);
                    }
                }, DialogsActivity.this.dialogsItemAnimator.getMoveDuration());
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
            this.dialogsType = this.arguments.getInt("dialogsType", 0);
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
            getMessagesController().loadUserInfo(UserConfig.getInstance(this.currentAccount).getCurrentUser(), false, this.classGuid);
            getContactsController().checkInviteText();
            getMediaDataController().loadRecents(2, false, true, false);
            getMediaDataController().checkFeaturedStickers();
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
        int i;
        Context context2 = context;
        this.searching = false;
        this.searchWas = false;
        this.pacmanAnimation = null;
        AndroidUtilities.runOnUIThread(new Runnable(context2) {
            private final /* synthetic */ Context f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                Theme.createChatResources(this.f$0, false);
            }
        });
        ActionBarMenu createMenu = this.actionBar.createMenu();
        if (!this.onlySelect && this.searchString == null && this.folderId == 0) {
            this.proxyDrawable = new ProxyDrawable(context2);
            this.proxyItem = createMenu.addItem(2, (Drawable) this.proxyDrawable);
            this.proxyItem.setContentDescription(LocaleController.getString("ProxySettings", NUM));
            this.passcodeItem = createMenu.addItem(1, NUM);
            updatePasscodeButton();
            updateProxyButton(false);
        }
        ActionBarMenuItem actionBarMenuItemSearchListener = createMenu.addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            public void onSearchExpand() {
                boolean unused = DialogsActivity.this.searching = true;
                if (DialogsActivity.this.switchItem != null) {
                    DialogsActivity.this.switchItem.setVisibility(8);
                }
                if (DialogsActivity.this.proxyItem != null && DialogsActivity.this.proxyItemVisisble) {
                    DialogsActivity.this.proxyItem.setVisibility(8);
                }
                if (DialogsActivity.this.listView != null) {
                    if (DialogsActivity.this.searchString != null) {
                        DialogsActivity.this.listView.hide();
                        DialogsActivity.this.searchListView.show();
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
                boolean unused = DialogsActivity.this.searching = false;
                boolean unused2 = DialogsActivity.this.searchWas = false;
                if (DialogsActivity.this.listView != null) {
                    DialogsActivity.this.listView.setEmptyView(DialogsActivity.this.folderId == 0 ? DialogsActivity.this.progressView : null);
                    if (!DialogsActivity.this.onlySelect) {
                        DialogsActivity.this.floatingButtonContainer.setVisibility(0);
                        boolean unused3 = DialogsActivity.this.floatingHidden = true;
                        float unused4 = DialogsActivity.this.floatingButtonTranslation = (float) AndroidUtilities.dp(100.0f);
                        float unused5 = DialogsActivity.this.floatingButtonHideProgress = 1.0f;
                        DialogsActivity.this.updateFloatingButtonOffset();
                    }
                    DialogsActivity.this.showSearch(false, true);
                }
                DialogsActivity.this.updatePasscodeButton();
                if (DialogsActivity.this.menuDrawable != null) {
                    DialogsActivity.this.actionBar.setBackButtonContentDescription(LocaleController.getString("AccDescrOpenMenu", NUM));
                }
            }

            public void onTextChanged(EditText editText) {
                String obj = editText.getText().toString();
                if (obj.length() != 0 || (DialogsActivity.this.dialogsSearchAdapter != null && DialogsActivity.this.dialogsSearchAdapter.hasRecentRearch())) {
                    boolean unused = DialogsActivity.this.searchWas = true;
                    if (DialogsActivity.this.listView.getVisibility() == 0) {
                        DialogsActivity.this.showSearch(true, true);
                    }
                }
                if (DialogsActivity.this.dialogsSearchAdapter != null) {
                    boolean unused2 = DialogsActivity.this.lastSearchScrolledToTop = false;
                    DialogsActivity.this.dialogsSearchAdapter.searchDialogs(obj);
                }
            }
        });
        actionBarMenuItemSearchListener.setClearsTextOnSearchCollapse(false);
        actionBarMenuItemSearchListener.setSearchFieldHint(LocaleController.getString("Search", NUM));
        actionBarMenuItemSearchListener.setContentDescription(LocaleController.getString("Search", NUM));
        if (this.onlySelect) {
            this.actionBar.setBackButtonImage(NUM);
            if (this.dialogsType == 3 && this.selectAlertString == null) {
                this.actionBar.setTitle(LocaleController.getString("ForwardTo", NUM));
            } else {
                this.actionBar.setTitle(LocaleController.getString("SelectChat", NUM));
            }
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
                this.actionBar.setTitle("Telegram Beta");
            } else {
                this.actionBar.setTitle(LocaleController.getString("AppName", NUM));
            }
            if (this.folderId == 0) {
                this.actionBar.setSupportsHolidayImage(true);
            }
        }
        this.actionBar.setTitleActionRunnable(new Runnable() {
            public final void run() {
                DialogsActivity.this.lambda$createView$1$DialogsActivity();
            }
        });
        if (this.allowSwitchAccount && UserConfig.getActivatedAccountsCount() > 1) {
            this.switchItem = createMenu.addItemWithWidth(1, 0, AndroidUtilities.dp(56.0f));
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
            BackupImageView backupImageView = new BackupImageView(context2);
            backupImageView.setRoundRadius(AndroidUtilities.dp(18.0f));
            this.switchItem.addView(backupImageView, LayoutHelper.createFrame(36, 36, 17));
            TLRPC.User currentUser = getUserConfig().getCurrentUser();
            avatarDrawable.setInfo(currentUser);
            backupImageView.getImageReceiver().setCurrentAccount(this.currentAccount);
            backupImageView.setImage(ImageLocation.getForUser(currentUser, false), "50_50", (Drawable) avatarDrawable, (Object) currentUser);
            for (int i2 = 0; i2 < 3; i2++) {
                if (AccountInstance.getInstance(i2).getUserConfig().getCurrentUser() != null) {
                    AccountSelectCell accountSelectCell = new AccountSelectCell(context2);
                    accountSelectCell.setAccount(i2, true);
                    this.switchItem.addSubItem(i2 + 10, accountSelectCell, AndroidUtilities.dp(230.0f), AndroidUtilities.dp(48.0f));
                }
            }
        }
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
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
                    SharedConfig.appLocked = !SharedConfig.appLocked;
                    SharedConfig.saveConfig();
                    DialogsActivity.this.updatePasscodeButton();
                } else if (i == 2) {
                    DialogsActivity.this.presentFragment(new ProxyListActivity());
                } else if (i < 10 || i >= 13) {
                    if (i == 100 || i == 101 || i == 102 || i == 103 || i == 104 || i == 105 || i == 106) {
                        DialogsActivity.this.perfromSelectedDialogsAction(i, true);
                    }
                } else if (DialogsActivity.this.getParentActivity() != null) {
                    DialogsActivityDelegate access$7000 = DialogsActivity.this.delegate;
                    LaunchActivity launchActivity = (LaunchActivity) DialogsActivity.this.getParentActivity();
                    launchActivity.switchToAccount(i - 10, true);
                    DialogsActivity dialogsActivity = new DialogsActivity(DialogsActivity.this.arguments);
                    dialogsActivity.setDelegate(access$7000);
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
        ActionBarMenu createActionMode = this.actionBar.createActionMode();
        this.selectedDialogsCountTextView = new NumberTextView(createActionMode.getContext());
        this.selectedDialogsCountTextView.setTextSize(18);
        this.selectedDialogsCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.selectedDialogsCountTextView.setTextColor(Theme.getColor("actionBarActionModeDefaultIcon"));
        createActionMode.addView(this.selectedDialogsCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 72, 0, 0, 0));
        this.selectedDialogsCountTextView.setOnTouchListener($$Lambda$DialogsActivity$pwzbQ3D6N1rpnIvtZZSb6O4Np6g.INSTANCE);
        this.pinItem = createActionMode.addItemWithWidth(100, NUM, AndroidUtilities.dp(54.0f));
        this.muteItem = createActionMode.addItemWithWidth(104, NUM, AndroidUtilities.dp(54.0f));
        this.deleteItem = createActionMode.addItemWithWidth(102, NUM, AndroidUtilities.dp(54.0f), LocaleController.getString("Delete", NUM));
        ActionBarMenuItem addItemWithWidth = createActionMode.addItemWithWidth(0, NUM, AndroidUtilities.dp(54.0f), LocaleController.getString("AccDescrMoreOptions", NUM));
        this.archiveItem = addItemWithWidth.addSubItem(105, NUM, (CharSequence) LocaleController.getString("Archive", NUM));
        this.readItem = addItemWithWidth.addSubItem(101, NUM, (CharSequence) LocaleController.getString("MarkAsRead", NUM));
        this.clearItem = addItemWithWidth.addSubItem(103, NUM, (CharSequence) LocaleController.getString("ClearHistory", NUM));
        this.blockItem = addItemWithWidth.addSubItem(106, NUM, (CharSequence) LocaleController.getString("BlockUser", NUM));
        this.actionModeViews.add(this.pinItem);
        this.actionModeViews.add(this.muteItem);
        this.actionModeViews.add(this.deleteItem);
        this.actionModeViews.add(addItemWithWidth);
        ContentView contentView = new ContentView(context2);
        this.fragmentView = contentView;
        this.listView = new DialogsRecyclerView(context2);
        this.listView.setPivotY(0.0f);
        this.dialogsItemAnimator = new DialogsItemAnimator() {
            public void onRemoveStarting(RecyclerView.ViewHolder viewHolder) {
                super.onRemoveStarting(viewHolder);
                if (DialogsActivity.this.layoutManager.findFirstVisibleItemPosition() == 0) {
                    View findViewByPosition = DialogsActivity.this.layoutManager.findViewByPosition(0);
                    if (findViewByPosition != null) {
                        findViewByPosition.invalidate();
                    }
                    if (DialogsActivity.this.archivePullViewState == 2) {
                        int unused = DialogsActivity.this.archivePullViewState = 1;
                    }
                    PullForegroundDrawable pullForegroundDrawable = DialogsActivity.this.pullForegroundDrawable;
                    if (pullForegroundDrawable != null) {
                        pullForegroundDrawable.doNotShow();
                    }
                }
            }

            public void onRemoveFinished(RecyclerView.ViewHolder viewHolder) {
                if (DialogsActivity.this.dialogRemoveFinished == 2) {
                    int unused = DialogsActivity.this.dialogRemoveFinished = 1;
                }
            }

            public void onAddFinished(RecyclerView.ViewHolder viewHolder) {
                if (DialogsActivity.this.dialogInsertFinished == 2) {
                    int unused = DialogsActivity.this.dialogInsertFinished = 1;
                }
            }

            public void onChangeFinished(RecyclerView.ViewHolder viewHolder, boolean z) {
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
        this.listView.setItemAnimator(this.dialogsItemAnimator);
        this.listView.setVerticalScrollBarEnabled(true);
        this.listView.setInstantClick(true);
        this.layoutManager = new LinearLayoutManager(context2) {
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i) {
                if (!DialogsActivity.this.hasHiddenArchive() || i != 1) {
                    LinearSmoothScrollerCustom linearSmoothScrollerCustom = new LinearSmoothScrollerCustom(recyclerView.getContext(), 0);
                    linearSmoothScrollerCustom.setTargetPosition(i);
                    startSmoothScroll(linearSmoothScrollerCustom);
                    return;
                }
                super.smoothScrollToPosition(recyclerView, state, i);
            }

            /* JADX WARNING: Removed duplicated region for block: B:106:0x0265  */
            /* JADX WARNING: Removed duplicated region for block: B:49:0x0123  */
            /* JADX WARNING: Removed duplicated region for block: B:50:0x0127  */
            /* JADX WARNING: Removed duplicated region for block: B:56:0x0142  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public int scrollVerticallyBy(int r13, androidx.recyclerview.widget.RecyclerView.Recycler r14, androidx.recyclerview.widget.RecyclerView.State r15) {
                /*
                    r12 = this;
                    org.telegram.ui.DialogsActivity r0 = org.telegram.ui.DialogsActivity.this
                    org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r0.listView
                    int r0 = r0.getScrollState()
                    r1 = 0
                    r2 = 1
                    if (r0 != r2) goto L_0x0010
                    r0 = 1
                    goto L_0x0011
                L_0x0010:
                    r0 = 0
                L_0x0011:
                    org.telegram.ui.DialogsActivity r3 = org.telegram.ui.DialogsActivity.this
                    org.telegram.ui.DialogsActivity$DialogsRecyclerView r3 = r3.listView
                    androidx.recyclerview.widget.RecyclerView$Adapter r3 = r3.getAdapter()
                    org.telegram.ui.DialogsActivity r4 = org.telegram.ui.DialogsActivity.this
                    org.telegram.ui.Adapters.DialogsAdapter r4 = r4.dialogsAdapter
                    r5 = 2
                    r6 = 1065353216(0x3var_, float:1.0)
                    if (r3 != r4) goto L_0x00fd
                    org.telegram.ui.DialogsActivity r3 = org.telegram.ui.DialogsActivity.this
                    int r3 = r3.dialogsType
                    if (r3 != 0) goto L_0x00fd
                    org.telegram.ui.DialogsActivity r3 = org.telegram.ui.DialogsActivity.this
                    boolean r3 = r3.onlySelect
                    if (r3 != 0) goto L_0x00fd
                    org.telegram.ui.DialogsActivity r3 = org.telegram.ui.DialogsActivity.this
                    int r3 = r3.folderId
                    if (r3 != 0) goto L_0x00fd
                    if (r13 >= 0) goto L_0x00fd
                    org.telegram.ui.DialogsActivity r3 = org.telegram.ui.DialogsActivity.this
                    org.telegram.messenger.MessagesController r3 = r3.getMessagesController()
                    boolean r3 = r3.hasHiddenArchive()
                    if (r3 == 0) goto L_0x00fd
                    org.telegram.ui.DialogsActivity r3 = org.telegram.ui.DialogsActivity.this
                    int r3 = r3.archivePullViewState
                    if (r3 != r5) goto L_0x00fd
                    org.telegram.ui.DialogsActivity r3 = org.telegram.ui.DialogsActivity.this
                    org.telegram.ui.DialogsActivity$DialogsRecyclerView r3 = r3.listView
                    r3.setOverScrollMode(r1)
                    org.telegram.ui.DialogsActivity r3 = org.telegram.ui.DialogsActivity.this
                    androidx.recyclerview.widget.LinearLayoutManager r3 = r3.layoutManager
                    int r3 = r3.findFirstVisibleItemPosition()
                    if (r3 != 0) goto L_0x0080
                    org.telegram.ui.DialogsActivity r4 = org.telegram.ui.DialogsActivity.this
                    androidx.recyclerview.widget.LinearLayoutManager r4 = r4.layoutManager
                    android.view.View r4 = r4.findViewByPosition(r3)
                    if (r4 == 0) goto L_0x0080
                    int r4 = r4.getBottom()
                    int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
                    if (r4 > r7) goto L_0x0080
                    r3 = 1
                L_0x0080:
                    if (r0 != 0) goto L_0x00ab
                    org.telegram.ui.DialogsActivity r4 = org.telegram.ui.DialogsActivity.this
                    androidx.recyclerview.widget.LinearLayoutManager r4 = r4.layoutManager
                    android.view.View r4 = r4.findViewByPosition(r3)
                    boolean r7 = org.telegram.messenger.SharedConfig.useThreeLinesLayout
                    if (r7 == 0) goto L_0x0093
                    r7 = 1117519872(0x429CLASSNAME, float:78.0)
                    goto L_0x0095
                L_0x0093:
                    r7 = 1116733440(0x42900000, float:72.0)
                L_0x0095:
                    int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
                    int r7 = r7 + r2
                    int r4 = r4.getTop()
                    int r4 = -r4
                    int r3 = r3 - r2
                    int r3 = r3 * r7
                    int r4 = r4 + r3
                    int r3 = java.lang.Math.abs(r13)
                    if (r4 >= r3) goto L_0x00fd
                    int r3 = -r4
                    goto L_0x00fe
                L_0x00ab:
                    if (r3 != 0) goto L_0x00fd
                    org.telegram.ui.DialogsActivity r4 = org.telegram.ui.DialogsActivity.this
                    androidx.recyclerview.widget.LinearLayoutManager r4 = r4.layoutManager
                    android.view.View r3 = r4.findViewByPosition(r3)
                    int r4 = r3.getTop()
                    float r4 = (float) r4
                    int r3 = r3.getMeasuredHeight()
                    float r3 = (float) r3
                    float r4 = r4 / r3
                    float r3 = r4 + r6
                    int r4 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1))
                    if (r4 <= 0) goto L_0x00ca
                    r3 = 1065353216(0x3var_, float:1.0)
                L_0x00ca:
                    org.telegram.ui.DialogsActivity r4 = org.telegram.ui.DialogsActivity.this
                    org.telegram.ui.DialogsActivity$DialogsRecyclerView r4 = r4.listView
                    r4.setOverScrollMode(r5)
                    float r4 = (float) r13
                    r7 = 1055286886(0x3ee66666, float:0.45)
                    r8 = 1048576000(0x3e800000, float:0.25)
                    float r3 = r3 * r8
                    float r7 = r7 - r3
                    float r4 = r4 * r7
                    int r3 = (int) r4
                    r4 = -1
                    if (r3 <= r4) goto L_0x00e3
                    r3 = -1
                L_0x00e3:
                    org.telegram.ui.DialogsActivity r4 = org.telegram.ui.DialogsActivity.this
                    org.telegram.ui.Components.UndoView[] r4 = r4.undoView
                    r4 = r4[r1]
                    int r4 = r4.getVisibility()
                    if (r4 != 0) goto L_0x00fe
                    org.telegram.ui.DialogsActivity r4 = org.telegram.ui.DialogsActivity.this
                    org.telegram.ui.Components.UndoView[] r4 = r4.undoView
                    r4 = r4[r1]
                    r4.hide(r2, r2)
                    goto L_0x00fe
                L_0x00fd:
                    r3 = r13
                L_0x00fe:
                    org.telegram.ui.DialogsActivity r4 = org.telegram.ui.DialogsActivity.this
                    org.telegram.ui.DialogsActivity$DialogsRecyclerView r4 = r4.listView
                    float r4 = r4.getViewOffset()
                    r7 = 0
                    int r4 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
                    if (r4 == 0) goto L_0x0132
                    if (r13 <= 0) goto L_0x0132
                    if (r0 == 0) goto L_0x0132
                    org.telegram.ui.DialogsActivity r3 = org.telegram.ui.DialogsActivity.this
                    org.telegram.ui.DialogsActivity$DialogsRecyclerView r3 = r3.listView
                    float r3 = r3.getViewOffset()
                    int r3 = (int) r3
                    float r3 = (float) r3
                    float r4 = (float) r13
                    float r3 = r3 - r4
                    int r4 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
                    if (r4 >= 0) goto L_0x0127
                    int r3 = (int) r3
                    r4 = r3
                    r3 = 0
                    goto L_0x0128
                L_0x0127:
                    r4 = 0
                L_0x0128:
                    org.telegram.ui.DialogsActivity r8 = org.telegram.ui.DialogsActivity.this
                    org.telegram.ui.DialogsActivity$DialogsRecyclerView r8 = r8.listView
                    r8.setViewsOffset(r3)
                    r3 = r4
                L_0x0132:
                    org.telegram.ui.DialogsActivity r4 = org.telegram.ui.DialogsActivity.this
                    int r4 = r4.archivePullViewState
                    if (r4 == 0) goto L_0x0265
                    org.telegram.ui.DialogsActivity r4 = org.telegram.ui.DialogsActivity.this
                    boolean r4 = r4.hasHiddenArchive()
                    if (r4 == 0) goto L_0x0265
                    int r14 = super.scrollVerticallyBy(r3, r14, r15)
                    org.telegram.ui.DialogsActivity r15 = org.telegram.ui.DialogsActivity.this
                    org.telegram.ui.Components.PullForegroundDrawable r15 = r15.pullForegroundDrawable
                    if (r15 == 0) goto L_0x014e
                    r15.scrollDy = r14
                L_0x014e:
                    org.telegram.ui.DialogsActivity r15 = org.telegram.ui.DialogsActivity.this
                    androidx.recyclerview.widget.LinearLayoutManager r15 = r15.layoutManager
                    int r15 = r15.findFirstVisibleItemPosition()
                    r4 = 0
                    if (r15 != 0) goto L_0x0165
                    org.telegram.ui.DialogsActivity r4 = org.telegram.ui.DialogsActivity.this
                    androidx.recyclerview.widget.LinearLayoutManager r4 = r4.layoutManager
                    android.view.View r4 = r4.findViewByPosition(r15)
                L_0x0165:
                    r8 = 0
                    if (r15 != 0) goto L_0x023a
                    if (r4 == 0) goto L_0x023a
                    int r15 = r4.getBottom()
                    r10 = 1082130432(0x40800000, float:4.0)
                    int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
                    if (r15 < r10) goto L_0x023a
                    org.telegram.ui.DialogsActivity r15 = org.telegram.ui.DialogsActivity.this
                    long r10 = r15.startArchivePullingTime
                    int r15 = (r10 > r8 ? 1 : (r10 == r8 ? 0 : -1))
                    if (r15 != 0) goto L_0x018a
                    org.telegram.ui.DialogsActivity r15 = org.telegram.ui.DialogsActivity.this
                    long r7 = java.lang.System.currentTimeMillis()
                    long unused = r15.startArchivePullingTime = r7
                L_0x018a:
                    org.telegram.ui.DialogsActivity r15 = org.telegram.ui.DialogsActivity.this
                    int r15 = r15.archivePullViewState
                    if (r15 != r5) goto L_0x019b
                    org.telegram.ui.DialogsActivity r15 = org.telegram.ui.DialogsActivity.this
                    org.telegram.ui.Components.PullForegroundDrawable r15 = r15.pullForegroundDrawable
                    if (r15 == 0) goto L_0x019b
                    r15.showHidden()
                L_0x019b:
                    int r15 = r4.getTop()
                    float r15 = (float) r15
                    int r7 = r4.getMeasuredHeight()
                    float r7 = (float) r7
                    float r15 = r15 / r7
                    float r15 = r15 + r6
                    int r7 = (r15 > r6 ? 1 : (r15 == r6 ? 0 : -1))
                    if (r7 <= 0) goto L_0x01ad
                    r15 = 1065353216(0x3var_, float:1.0)
                L_0x01ad:
                    long r7 = java.lang.System.currentTimeMillis()
                    org.telegram.ui.DialogsActivity r9 = org.telegram.ui.DialogsActivity.this
                    long r9 = r9.startArchivePullingTime
                    long r7 = r7 - r9
                    r9 = 1062836634(0x3var_a, float:0.85)
                    int r9 = (r15 > r9 ? 1 : (r15 == r9 ? 0 : -1))
                    if (r9 <= 0) goto L_0x01c6
                    r9 = 220(0xdc, double:1.087E-321)
                    int r11 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
                    if (r11 <= 0) goto L_0x01c6
                    r1 = 1
                L_0x01c6:
                    org.telegram.ui.DialogsActivity r2 = org.telegram.ui.DialogsActivity.this
                    boolean r2 = r2.canShowHiddenArchive
                    if (r2 == r1) goto L_0x01ee
                    org.telegram.ui.DialogsActivity r2 = org.telegram.ui.DialogsActivity.this
                    boolean unused = r2.canShowHiddenArchive = r1
                    org.telegram.ui.DialogsActivity r2 = org.telegram.ui.DialogsActivity.this
                    int r2 = r2.archivePullViewState
                    if (r2 != r5) goto L_0x01ee
                    org.telegram.ui.DialogsActivity r2 = org.telegram.ui.DialogsActivity.this
                    org.telegram.ui.DialogsActivity$DialogsRecyclerView r2 = r2.listView
                    r7 = 3
                    r2.performHapticFeedback(r7, r5)
                    org.telegram.ui.DialogsActivity r2 = org.telegram.ui.DialogsActivity.this
                    org.telegram.ui.Components.PullForegroundDrawable r2 = r2.pullForegroundDrawable
                    if (r2 == 0) goto L_0x01ee
                    r2.colorize(r1)
                L_0x01ee:
                    org.telegram.ui.DialogsActivity r1 = org.telegram.ui.DialogsActivity.this
                    int r1 = r1.archivePullViewState
                    if (r1 != r5) goto L_0x022a
                    int r3 = r3 - r14
                    if (r3 == 0) goto L_0x022a
                    if (r13 >= 0) goto L_0x022a
                    if (r0 == 0) goto L_0x022a
                    org.telegram.ui.DialogsActivity r0 = org.telegram.ui.DialogsActivity.this
                    org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r0.listView
                    float r0 = r0.getViewOffset()
                    int r1 = org.telegram.ui.Components.PullForegroundDrawable.getMaxOverscroll()
                    float r1 = (float) r1
                    float r0 = r0 / r1
                    float r6 = r6 - r0
                    org.telegram.ui.DialogsActivity r0 = org.telegram.ui.DialogsActivity.this
                    org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r0.listView
                    float r0 = r0.getViewOffset()
                    float r13 = (float) r13
                    r1 = 1045220557(0x3e4ccccd, float:0.2)
                    float r13 = r13 * r1
                    float r13 = r13 * r6
                    float r0 = r0 - r13
                    org.telegram.ui.DialogsActivity r13 = org.telegram.ui.DialogsActivity.this
                    org.telegram.ui.DialogsActivity$DialogsRecyclerView r13 = r13.listView
                    r13.setViewsOffset(r0)
                L_0x022a:
                    org.telegram.ui.DialogsActivity r13 = org.telegram.ui.DialogsActivity.this
                    org.telegram.ui.Components.PullForegroundDrawable r0 = r13.pullForegroundDrawable
                    if (r0 == 0) goto L_0x025f
                    r0.pullProgress = r15
                    org.telegram.ui.DialogsActivity$DialogsRecyclerView r13 = r13.listView
                    r0.setListView(r13)
                    goto L_0x025f
                L_0x023a:
                    org.telegram.ui.DialogsActivity r13 = org.telegram.ui.DialogsActivity.this
                    long unused = r13.startArchivePullingTime = r8
                    org.telegram.ui.DialogsActivity r13 = org.telegram.ui.DialogsActivity.this
                    boolean unused = r13.canShowHiddenArchive = r1
                    org.telegram.ui.DialogsActivity r13 = org.telegram.ui.DialogsActivity.this
                    int unused = r13.archivePullViewState = r5
                    org.telegram.ui.DialogsActivity r13 = org.telegram.ui.DialogsActivity.this
                    org.telegram.ui.Components.PullForegroundDrawable r13 = r13.pullForegroundDrawable
                    if (r13 == 0) goto L_0x025f
                    r13.resetText()
                    org.telegram.ui.DialogsActivity r13 = org.telegram.ui.DialogsActivity.this
                    org.telegram.ui.Components.PullForegroundDrawable r15 = r13.pullForegroundDrawable
                    r15.pullProgress = r7
                    org.telegram.ui.DialogsActivity$DialogsRecyclerView r13 = r13.listView
                    r15.setListView(r13)
                L_0x025f:
                    if (r4 == 0) goto L_0x0264
                    r4.invalidate()
                L_0x0264:
                    return r14
                L_0x0265:
                    int r13 = super.scrollVerticallyBy(r3, r14, r15)
                    return r13
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.AnonymousClass4.scrollVerticallyBy(int, androidx.recyclerview.widget.RecyclerView$Recycler, androidx.recyclerview.widget.RecyclerView$State):int");
            }
        };
        this.layoutManager.setOrientation(1);
        this.listView.setLayoutManager(this.layoutManager);
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        contentView.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                DialogsActivity.this.lambda$createView$3$DialogsActivity(view, i);
            }
        });
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListenerExtended) new RecyclerListView.OnItemLongClickListenerExtended() {
            public boolean onItemClick(View view, int i, float f, float f2) {
                DialogsActivity dialogsActivity = DialogsActivity.this;
                return dialogsActivity.onItemLongClick(view, i, f, f2, dialogsActivity.dialogsAdapter);
            }

            public void onLongClickRelease() {
                DialogsActivity.this.finishPreviewFragment();
            }

            public void onMove(float f, float f2) {
                DialogsActivity.this.movePreviewFragment(f2);
            }
        });
        this.swipeController = new SwipeController();
        this.progressView = new RadialProgressView(context2);
        this.progressView.setPivotY(0.0f);
        this.progressView.setVisibility(8);
        contentView.addView(this.progressView, LayoutHelper.createFrame(-2, -2, 17));
        this.itemTouchhelper = new ItemTouchHelper(this.swipeController);
        this.itemTouchhelper.attachToRecyclerView(this.listView);
        if (this.searchString != null) {
            i = 2;
        } else {
            i = !this.onlySelect ? 1 : 0;
        }
        this.dialogsSearchAdapter = new DialogsSearchAdapter(context2, i, this.dialogsType) {
            public void notifyDataSetChanged() {
                super.notifyDataSetChanged();
                if (!DialogsActivity.this.lastSearchScrolledToTop && DialogsActivity.this.searchListView != null) {
                    DialogsActivity.this.searchListView.scrollToPosition(0);
                    boolean unused = DialogsActivity.this.lastSearchScrolledToTop = true;
                }
            }
        };
        this.dialogsSearchAdapter.setDelegate(new DialogsSearchAdapter.DialogsSearchAdapterDelegate() {
            public void searchStateChanged(boolean z) {
                if (DialogsActivity.this.searching && DialogsActivity.this.searchWas && DialogsActivity.this.searchEmptyView != null) {
                    if (z) {
                        DialogsActivity.this.searchEmptyView.showProgress();
                    } else {
                        DialogsActivity.this.searchEmptyView.showTextView();
                    }
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
                } else if (DialogsActivity.this.validateSlowModeDialog(j)) {
                    if (DialogsActivity.this.dialogsAdapter.hasSelectedDialogs()) {
                        DialogsActivity.this.findAndUpdateCheckBox(j, DialogsActivity.this.dialogsAdapter.addOrRemoveSelectedDialog(j, (View) null));
                        DialogsActivity.this.updateSelectedCount();
                        DialogsActivity.this.actionBar.closeSearchField();
                        return;
                    }
                    DialogsActivity.this.didSelectResult(j, true, false);
                }
            }

            public void needRemoveHint(int i) {
                TLRPC.User user;
                if (DialogsActivity.this.getParentActivity() != null && (user = DialogsActivity.this.getMessagesController().getUser(Integer.valueOf(i))) != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) DialogsActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("ChatHintsDeleteAlertTitle", NUM));
                    builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("ChatHintsDeleteAlert", NUM, ContactsController.formatName(user.first_name, user.last_name))));
                    builder.setPositiveButton(LocaleController.getString("StickersRemove", NUM), new DialogInterface.OnClickListener(i) {
                        private final /* synthetic */ int f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            DialogsActivity.AnonymousClass7.this.lambda$needRemoveHint$0$DialogsActivity$7(this.f$1, dialogInterface, i);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    AlertDialog create = builder.create();
                    DialogsActivity.this.showDialog(create);
                    TextView textView = (TextView) create.getButton(-1);
                    if (textView != null) {
                        textView.setTextColor(Theme.getColor("dialogTextRed2"));
                    }
                }
            }

            public /* synthetic */ void lambda$needRemoveHint$0$DialogsActivity$7(int i, DialogInterface dialogInterface, int i2) {
                DialogsActivity.this.getMediaDataController().removePeer(i);
            }

            public void needClearList() {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) DialogsActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("ClearSearchAlertTitle", NUM));
                builder.setMessage(LocaleController.getString("ClearSearchAlert", NUM));
                builder.setPositiveButton(LocaleController.getString("ClearButton", NUM).toUpperCase(), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        DialogsActivity.AnonymousClass7.this.lambda$needClearList$1$DialogsActivity$7(dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                AlertDialog create = builder.create();
                DialogsActivity.this.showDialog(create);
                TextView textView = (TextView) create.getButton(-1);
                if (textView != null) {
                    textView.setTextColor(Theme.getColor("dialogTextRed2"));
                }
            }

            public /* synthetic */ void lambda$needClearList$1$DialogsActivity$7(DialogInterface dialogInterface, int i) {
                if (DialogsActivity.this.dialogsSearchAdapter.isRecentSearchDisplayed()) {
                    DialogsActivity.this.dialogsSearchAdapter.clearRecentSearch();
                } else {
                    DialogsActivity.this.dialogsSearchAdapter.clearRecentHashtags();
                }
            }
        });
        this.searchListView = new RecyclerListView(context2);
        this.searchListView.setPivotY(0.0f);
        this.searchListView.setAdapter(this.dialogsSearchAdapter);
        this.searchListView.setVerticalScrollBarEnabled(true);
        this.searchListView.setInstantClick(true);
        this.searchListView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        RecyclerListView recyclerListView = this.searchListView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context2, 1, false);
        this.searchlayoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        contentView.addView(this.searchListView, LayoutHelper.createFrame(-1, -1.0f));
        this.searchListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                DialogsActivity.this.lambda$createView$4$DialogsActivity(view, i);
            }
        });
        this.searchListView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListenerExtended) new RecyclerListView.OnItemLongClickListenerExtended() {
            public boolean onItemClick(View view, int i, float f, float f2) {
                DialogsActivity dialogsActivity = DialogsActivity.this;
                return dialogsActivity.onItemLongClick(view, i, f, f2, dialogsActivity.dialogsSearchAdapter);
            }

            public void onLongClickRelease() {
                DialogsActivity.this.finishPreviewFragment();
            }

            public void onMove(float f, float f2) {
                DialogsActivity.this.movePreviewFragment(f2);
            }
        });
        this.searchEmptyView = new EmptyTextProgressView(context2);
        this.searchEmptyView.setPivotY(0.0f);
        this.searchEmptyView.setShowAtCenter(true);
        this.searchEmptyView.setTopImage(NUM);
        this.searchEmptyView.setText(LocaleController.getString("SettingsNoResults", NUM));
        contentView.addView(this.searchEmptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.searchListView.setEmptyView(this.searchEmptyView);
        this.floatingButtonContainer = new FrameLayout(context2);
        this.floatingButtonContainer.setVisibility((this.onlySelect || this.folderId != 0) ? 8 : 0);
        contentView.addView(this.floatingButtonContainer, LayoutHelper.createFrame((Build.VERSION.SDK_INT >= 21 ? 56 : 60) + 20, (float) ((Build.VERSION.SDK_INT >= 21 ? 56 : 60) + 14), (LocaleController.isRTL ? 3 : 5) | 80, LocaleController.isRTL ? 4.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 4.0f, 0.0f));
        this.floatingButtonContainer.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                DialogsActivity.this.lambda$createView$5$DialogsActivity(view);
            }
        });
        this.floatingButton = new ImageView(context2);
        this.floatingButton.setScaleType(ImageView.ScaleType.CENTER);
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("chats_actionBackground"), Theme.getColor("chats_actionPressedBackground"));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable mutate = context.getResources().getDrawable(NUM).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorCircleDrawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            createSimpleSelectorCircleDrawable = combinedDrawable;
        }
        this.floatingButton.setBackgroundDrawable(createSimpleSelectorCircleDrawable);
        this.floatingButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_actionIcon"), PorterDuff.Mode.MULTIPLY));
        this.floatingButton.setImageResource(NUM);
        if (Build.VERSION.SDK_INT >= 21) {
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
        this.floatingButtonContainer.addView(this.floatingButton, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56 : 60, (float) (Build.VERSION.SDK_INT >= 21 ? 56 : 60), 51, 10.0f, 0.0f, 10.0f, 0.0f));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            private boolean wasManualScroll;

            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1) {
                    this.wasManualScroll = true;
                    boolean unused = DialogsActivity.this.scrollingManually = true;
                } else {
                    boolean unused2 = DialogsActivity.this.scrollingManually = false;
                }
                if (i == 0) {
                    this.wasManualScroll = false;
                    if (DialogsActivity.this.waitingForScrollFinished) {
                        boolean unused3 = DialogsActivity.this.waitingForScrollFinished = false;
                        if (DialogsActivity.this.updatePullAfterScroll) {
                            DialogsActivity.this.listView.updatePullState();
                            boolean unused4 = DialogsActivity.this.updatePullAfterScroll = false;
                        }
                    }
                }
            }

            /* JADX WARNING: Code restructure failed: missing block: B:30:0x00d9, code lost:
                if (java.lang.Math.abs(r0) > 1) goto L_0x00e7;
             */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onScrolled(androidx.recyclerview.widget.RecyclerView r6, int r7, int r8) {
                /*
                    r5 = this;
                    org.telegram.ui.DialogsActivity r7 = org.telegram.ui.DialogsActivity.this
                    androidx.recyclerview.widget.LinearLayoutManager r7 = r7.layoutManager
                    int r7 = r7.findFirstVisibleItemPosition()
                    org.telegram.ui.DialogsActivity r0 = org.telegram.ui.DialogsActivity.this
                    androidx.recyclerview.widget.LinearLayoutManager r0 = r0.layoutManager
                    int r0 = r0.findLastVisibleItemPosition()
                    int r0 = r0 - r7
                    int r0 = java.lang.Math.abs(r0)
                    r1 = 1
                    int r0 = r0 + r1
                    org.telegram.ui.DialogsActivity r2 = org.telegram.ui.DialogsActivity.this
                    org.telegram.ui.Components.DialogsItemAnimator r2 = r2.dialogsItemAnimator
                    int r8 = -r8
                    r2.onListScroll(r8)
                    if (r0 <= 0) goto L_0x0082
                    org.telegram.ui.DialogsActivity r8 = org.telegram.ui.DialogsActivity.this
                    androidx.recyclerview.widget.LinearLayoutManager r8 = r8.layoutManager
                    int r8 = r8.findLastVisibleItemPosition()
                    org.telegram.ui.DialogsActivity r0 = org.telegram.ui.DialogsActivity.this
                    int r0 = r0.currentAccount
                    org.telegram.ui.DialogsActivity r2 = org.telegram.ui.DialogsActivity.this
                    int r2 = r2.dialogsType
                    org.telegram.ui.DialogsActivity r3 = org.telegram.ui.DialogsActivity.this
                    int r3 = r3.folderId
                    org.telegram.ui.DialogsActivity r4 = org.telegram.ui.DialogsActivity.this
                    boolean r4 = r4.dialogsListFrozen
                    java.util.ArrayList r0 = org.telegram.ui.DialogsActivity.getDialogsArray(r0, r2, r3, r4)
                    int r0 = r0.size()
                    int r0 = r0 + -10
                    if (r8 < r0) goto L_0x0082
                    org.telegram.ui.DialogsActivity r8 = org.telegram.ui.DialogsActivity.this
                    org.telegram.messenger.MessagesController r8 = r8.getMessagesController()
                    org.telegram.ui.DialogsActivity r0 = org.telegram.ui.DialogsActivity.this
                    int r0 = r0.folderId
                    boolean r8 = r8.isDialogsEndReached(r0)
                    r8 = r8 ^ r1
                    if (r8 != 0) goto L_0x007a
                    org.telegram.ui.DialogsActivity r0 = org.telegram.ui.DialogsActivity.this
                    org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                    org.telegram.ui.DialogsActivity r2 = org.telegram.ui.DialogsActivity.this
                    int r2 = r2.folderId
                    boolean r0 = r0.isServerDialogsEndReached(r2)
                    if (r0 != 0) goto L_0x0082
                L_0x007a:
                    org.telegram.ui.-$$Lambda$DialogsActivity$10$ZmbzK1COxw_WcLYjhVXaatEQtBQ r0 = new org.telegram.ui.-$$Lambda$DialogsActivity$10$ZmbzK1COxw_WcLYjhVXaatEQtBQ
                    r0.<init>(r8)
                    org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
                L_0x0082:
                    boolean r8 = r5.wasManualScroll
                    if (r8 == 0) goto L_0x0112
                    org.telegram.ui.DialogsActivity r8 = org.telegram.ui.DialogsActivity.this
                    android.widget.FrameLayout r8 = r8.floatingButtonContainer
                    int r8 = r8.getVisibility()
                    r0 = 8
                    if (r8 == r0) goto L_0x0112
                    int r8 = r6.getChildCount()
                    if (r8 <= 0) goto L_0x0112
                    r8 = 0
                    android.view.View r0 = r6.getChildAt(r8)
                    androidx.recyclerview.widget.RecyclerView$ViewHolder r6 = r6.findContainingViewHolder(r0)
                    org.telegram.ui.DialogsActivity r2 = org.telegram.ui.DialogsActivity.this
                    boolean r2 = r2.hasHiddenArchive()
                    if (r2 == 0) goto L_0x00b3
                    if (r6 == 0) goto L_0x0112
                    int r6 = r6.getAdapterPosition()
                    if (r6 == 0) goto L_0x0112
                L_0x00b3:
                    if (r0 == 0) goto L_0x00ba
                    int r6 = r0.getTop()
                    goto L_0x00bb
                L_0x00ba:
                    r6 = 0
                L_0x00bb:
                    org.telegram.ui.DialogsActivity r0 = org.telegram.ui.DialogsActivity.this
                    int r0 = r0.prevPosition
                    if (r0 != r7) goto L_0x00dc
                    org.telegram.ui.DialogsActivity r0 = org.telegram.ui.DialogsActivity.this
                    int r0 = r0.prevTop
                    int r0 = r0 - r6
                    org.telegram.ui.DialogsActivity r2 = org.telegram.ui.DialogsActivity.this
                    int r2 = r2.prevTop
                    if (r6 >= r2) goto L_0x00d4
                    r2 = 1
                    goto L_0x00d5
                L_0x00d4:
                    r2 = 0
                L_0x00d5:
                    int r0 = java.lang.Math.abs(r0)
                    if (r0 <= r1) goto L_0x00e8
                    goto L_0x00e7
                L_0x00dc:
                    org.telegram.ui.DialogsActivity r0 = org.telegram.ui.DialogsActivity.this
                    int r0 = r0.prevPosition
                    if (r7 <= r0) goto L_0x00e6
                    r2 = 1
                    goto L_0x00e7
                L_0x00e6:
                    r2 = 0
                L_0x00e7:
                    r8 = 1
                L_0x00e8:
                    if (r8 == 0) goto L_0x0103
                    org.telegram.ui.DialogsActivity r8 = org.telegram.ui.DialogsActivity.this
                    boolean r8 = r8.scrollUpdated
                    if (r8 == 0) goto L_0x0103
                    if (r2 != 0) goto L_0x00fe
                    if (r2 != 0) goto L_0x0103
                    org.telegram.ui.DialogsActivity r8 = org.telegram.ui.DialogsActivity.this
                    boolean r8 = r8.scrollingManually
                    if (r8 == 0) goto L_0x0103
                L_0x00fe:
                    org.telegram.ui.DialogsActivity r8 = org.telegram.ui.DialogsActivity.this
                    r8.hideFloatingButton(r2)
                L_0x0103:
                    org.telegram.ui.DialogsActivity r8 = org.telegram.ui.DialogsActivity.this
                    int unused = r8.prevPosition = r7
                    org.telegram.ui.DialogsActivity r7 = org.telegram.ui.DialogsActivity.this
                    int unused = r7.prevTop = r6
                    org.telegram.ui.DialogsActivity r6 = org.telegram.ui.DialogsActivity.this
                    boolean unused = r6.scrollUpdated = r1
                L_0x0112:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.AnonymousClass10.onScrolled(androidx.recyclerview.widget.RecyclerView, int, int):void");
            }

            public /* synthetic */ void lambda$onScrolled$0$DialogsActivity$10(boolean z) {
                DialogsActivity.this.getMessagesController().loadDialogs(DialogsActivity.this.folderId, -1, 100, z);
            }
        });
        this.searchListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1 && DialogsActivity.this.searching && DialogsActivity.this.searchWas) {
                    AndroidUtilities.hideKeyboard(DialogsActivity.this.getParentActivity().getCurrentFocus());
                }
            }

            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                int abs = Math.abs(DialogsActivity.this.searchlayoutManager.findLastVisibleItemPosition() - DialogsActivity.this.searchlayoutManager.findFirstVisibleItemPosition()) + 1;
                int itemCount = recyclerView.getAdapter().getItemCount();
                if (DialogsActivity.this.searching && DialogsActivity.this.searchWas && abs > 0 && DialogsActivity.this.searchlayoutManager.findLastVisibleItemPosition() == itemCount - 1 && !DialogsActivity.this.dialogsSearchAdapter.isMessagesSearchEndReached()) {
                    DialogsActivity.this.dialogsSearchAdapter.loadMoreSearchMessages();
                }
            }
        });
        this.archivePullViewState = SharedConfig.archiveHidden ? 2 : 0;
        if (this.pullForegroundDrawable == null && this.folderId == 0) {
            this.pullForegroundDrawable = new PullForegroundDrawable(LocaleController.getString("AccSwipeForArchive", NUM), LocaleController.getString("AccReleaseForArchive", NUM)) {
                /* access modifiers changed from: protected */
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
            this.dialogsAdapter = new DialogsAdapter(context, this.dialogsType, this.folderId, this.onlySelect) {
                public void notifyDataSetChanged() {
                    int unused = DialogsActivity.this.lastItemsCount = getItemCount();
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
        }
        this.listView.setEmptyView(this.folderId == 0 ? this.progressView : null);
        if (this.searchString != null) {
            showSearch(true, false);
            this.actionBar.openSearchField(this.searchString, false);
        } else {
            showSearch(false, false);
        }
        if (!this.onlySelect && this.dialogsType == 0) {
            FragmentContextView fragmentContextView = new FragmentContextView(context2, this, true);
            contentView.addView(fragmentContextView, LayoutHelper.createFrame(-1, 39.0f, 51, 0.0f, -36.0f, 0.0f, 0.0f));
            FragmentContextView fragmentContextView2 = new FragmentContextView(context2, this, false);
            contentView.addView(fragmentContextView2, LayoutHelper.createFrame(-1, 39.0f, 51, 0.0f, -36.0f, 0.0f, 0.0f));
            fragmentContextView2.setAdditionalContextView(fragmentContextView);
            fragmentContextView.setAdditionalContextView(fragmentContextView2);
        } else if (this.dialogsType == 3) {
            ChatActivityEnterView chatActivityEnterView = this.commentView;
            if (chatActivityEnterView != null) {
                chatActivityEnterView.onDestroy();
            }
            this.commentView = new ChatActivityEnterView(getParentActivity(), contentView, (ChatActivity) null, false);
            this.commentView.setAllowStickersAndGifs(false, false);
            this.commentView.setForceShowSendButton(true, false);
            this.commentView.setVisibility(8);
            contentView.addView(this.commentView, LayoutHelper.createFrame(-1, -2, 83));
            this.commentView.setDelegate(new ChatActivityEnterView.ChatActivityEnterViewDelegate() {
                public void didPressedAttachButton() {
                }

                public /* synthetic */ boolean hasScheduledMessages() {
                    return ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$hasScheduledMessages(this);
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

                public void onSendLongClick() {
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
                    ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$openScheduledMessages(this);
                }

                public /* synthetic */ void scrollToSendingMessage() {
                    ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$scrollToSendingMessage(this);
                }

                public void onMessageSend(CharSequence charSequence, boolean z, int i) {
                    if (DialogsActivity.this.delegate != null) {
                        ArrayList<Long> selectedDialogs = DialogsActivity.this.dialogsAdapter.getSelectedDialogs();
                        if (!selectedDialogs.isEmpty()) {
                            DialogsActivity.this.delegate.didSelectDialogs(DialogsActivity.this, selectedDialogs, charSequence, false);
                        }
                    }
                }
            });
        }
        for (int i3 = 0; i3 < 2; i3++) {
            this.undoView[i3] = new UndoView(context2) {
                public void setTranslationY(float f) {
                    super.setTranslationY(f);
                    if (this == DialogsActivity.this.undoView[0] && DialogsActivity.this.undoView[1].getVisibility() != 0) {
                        float measuredHeight = ((float) (getMeasuredHeight() + AndroidUtilities.dp(8.0f))) - f;
                        if (!DialogsActivity.this.floatingHidden) {
                            DialogsActivity.this.updateFloatingButtonOffset();
                        }
                        float unused = DialogsActivity.this.additionalFloatingTranslation = measuredHeight;
                    }
                }

                /* access modifiers changed from: protected */
                public boolean canUndo() {
                    return !DialogsActivity.this.dialogsItemAnimator.isRunning();
                }
            };
            contentView.addView(this.undoView[i3], LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
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
        this.scrollHelper = new RecyclerAnimationScrollHelper(this.listView, this.layoutManager);
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$1$DialogsActivity() {
        hideFloatingButton(false);
        scrollToTop();
    }

    public /* synthetic */ void lambda$createView$3$DialogsActivity(View view, int i) {
        onItemClick(view, i, this.dialogsAdapter);
    }

    public /* synthetic */ void lambda$createView$4$DialogsActivity(View view, int i) {
        onItemClick(view, i, this.dialogsSearchAdapter);
    }

    public /* synthetic */ void lambda$createView$5$DialogsActivity(View view) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("destroyAfterSelect", true);
        presentFragment(new ContactsActivity(bundle));
    }

    /* access modifiers changed from: protected */
    public void onPanTranslationUpdate(int i) {
        if (this.listView != null) {
            ChatActivityEnterView chatActivityEnterView = this.commentView;
            if (chatActivityEnterView == null || !chatActivityEnterView.isPopupShowing()) {
                float f = (float) i;
                this.listView.setTranslationY(f);
                this.searchListView.setTranslationY(f);
                return;
            }
            this.fragmentView.setTranslationY((float) i);
            this.listView.setTranslationY(0.0f);
            this.searchListView.setTranslationY(0.0f);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:31:0x0056  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x00dc  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x0161  */
    /* JADX WARNING: Removed duplicated region for block: B:81:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onResume() {
        /*
            r9 = this;
            super.onResume()
            org.telegram.ui.Adapters.DialogsAdapter r0 = r9.dialogsAdapter
            if (r0 == 0) goto L_0x000e
            boolean r1 = r9.dialogsListFrozen
            if (r1 != 0) goto L_0x000e
            r0.notifyDataSetChanged()
        L_0x000e:
            org.telegram.ui.Components.ChatActivityEnterView r0 = r9.commentView
            if (r0 == 0) goto L_0x0015
            r0.onResume()
        L_0x0015:
            boolean r0 = r9.onlySelect
            if (r0 != 0) goto L_0x0025
            int r0 = r9.folderId
            if (r0 != 0) goto L_0x0025
            org.telegram.messenger.MediaDataController r0 = r9.getMediaDataController()
            r1 = 4
            r0.checkStickers(r1)
        L_0x0025:
            org.telegram.ui.Adapters.DialogsSearchAdapter r0 = r9.dialogsSearchAdapter
            if (r0 == 0) goto L_0x002c
            r0.notifyDataSetChanged()
        L_0x002c:
            boolean r0 = r9.afterSignup
            r1 = 1
            r2 = 0
            if (r0 != 0) goto L_0x003e
            int r0 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            org.telegram.tgnet.TLRPC$TL_help_termsOfService r0 = r0.unacceptedTermsOfService
            if (r0 != 0) goto L_0x0040
            r0 = 1
            goto L_0x0041
        L_0x003e:
            r9.afterSignup = r2
        L_0x0040:
            r0 = 0
        L_0x0041:
            r3 = 2131624192(0x7f0e0100, float:1.8875557E38)
            java.lang.String r4 = "AppName"
            if (r0 == 0) goto L_0x00dc
            boolean r0 = r9.checkPermission
            if (r0 == 0) goto L_0x00dc
            boolean r0 = r9.onlySelect
            if (r0 != 0) goto L_0x00dc
            int r0 = android.os.Build.VERSION.SDK_INT
            r5 = 23
            if (r0 < r5) goto L_0x00dc
            android.app.Activity r0 = r9.getParentActivity()
            if (r0 == 0) goto L_0x014e
            r9.checkPermission = r2
            java.lang.String r5 = "android.permission.READ_CONTACTS"
            int r6 = r0.checkSelfPermission(r5)
            if (r6 == 0) goto L_0x0068
            r6 = 1
            goto L_0x0069
        L_0x0068:
            r6 = 0
        L_0x0069:
            java.lang.String r7 = "android.permission.WRITE_EXTERNAL_STORAGE"
            int r8 = r0.checkSelfPermission(r7)
            if (r8 == 0) goto L_0x0073
            r8 = 1
            goto L_0x0074
        L_0x0073:
            r8 = 0
        L_0x0074:
            if (r6 != 0) goto L_0x0078
            if (r8 == 0) goto L_0x014e
        L_0x0078:
            if (r6 == 0) goto L_0x00a0
            boolean r6 = r9.askAboutContacts
            if (r6 == 0) goto L_0x00a0
            org.telegram.messenger.UserConfig r6 = r9.getUserConfig()
            boolean r6 = r6.syncContacts
            if (r6 == 0) goto L_0x00a0
            boolean r5 = r0.shouldShowRequestPermissionRationale(r5)
            if (r5 == 0) goto L_0x00a0
            org.telegram.ui.-$$Lambda$DialogsActivity$ixGY6tW9u89eRkYy5hpeTgvlhnM r3 = new org.telegram.ui.-$$Lambda$DialogsActivity$ixGY6tW9u89eRkYy5hpeTgvlhnM
            r3.<init>()
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = org.telegram.ui.Components.AlertsCreator.createContactsPermissionDialog(r0, r3)
            org.telegram.ui.ActionBar.AlertDialog r0 = r0.create()
            r9.permissionDialog = r0
            r9.showDialog(r0)
            goto L_0x014e
        L_0x00a0:
            if (r8 == 0) goto L_0x00d8
            boolean r5 = r0.shouldShowRequestPermissionRationale(r7)
            if (r5 == 0) goto L_0x00d8
            org.telegram.ui.ActionBar.AlertDialog$Builder r5 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r5.<init>((android.content.Context) r0)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r5.setTitle(r0)
            r0 = 2131626217(0x7f0e08e9, float:1.8879664E38)
            java.lang.String r3 = "PermissionStorage"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            r5.setMessage(r0)
            r0 = 2131625882(0x7f0e079a, float:1.8878984E38)
            java.lang.String r3 = "OK"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            r3 = 0
            r5.setPositiveButton(r0, r3)
            org.telegram.ui.ActionBar.AlertDialog r0 = r5.create()
            r9.permissionDialog = r0
            r9.showDialog(r0)
            goto L_0x014e
        L_0x00d8:
            r9.askForPermissons(r1)
            goto L_0x014e
        L_0x00dc:
            boolean r0 = r9.onlySelect
            if (r0 != 0) goto L_0x014e
            boolean r0 = org.telegram.messenger.XiaomiUtilities.isMIUI()
            if (r0 == 0) goto L_0x014e
            int r0 = android.os.Build.VERSION.SDK_INT
            r5 = 19
            if (r0 < r5) goto L_0x014e
            r0 = 10020(0x2724, float:1.4041E-41)
            boolean r0 = org.telegram.messenger.XiaomiUtilities.isCustomPermissionGranted(r0)
            if (r0 != 0) goto L_0x014e
            android.app.Activity r0 = r9.getParentActivity()
            if (r0 != 0) goto L_0x00fb
            return
        L_0x00fb:
            android.content.SharedPreferences r0 = org.telegram.messenger.MessagesController.getGlobalNotificationsSettings()
            java.lang.String r5 = "askedAboutMiuiLockscreen"
            boolean r0 = r0.getBoolean(r5, r2)
            if (r0 == 0) goto L_0x0108
            return
        L_0x0108:
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r5 = r9.getParentActivity()
            r0.<init>((android.content.Context) r5)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = r0.setTitle(r3)
            r3 = 2131626218(0x7f0e08ea, float:1.8879666E38)
            java.lang.String r4 = "PermissionXiaomiLockscreen"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = r0.setMessage(r3)
            r3 = 2131626216(0x7f0e08e8, float:1.8879662E38)
            java.lang.String r4 = "PermissionOpenSettings"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            org.telegram.ui.-$$Lambda$DialogsActivity$Ys7qGIUPADYpBa6FCsYGmcB-1Hc r4 = new org.telegram.ui.-$$Lambda$DialogsActivity$Ys7qGIUPADYpBa6FCsYGmcB-1Hc
            r4.<init>()
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = r0.setPositiveButton(r3, r4)
            r3 = 2131624769(0x7f0e0341, float:1.8876727E38)
            java.lang.String r4 = "ContactsPermissionAlertNotNow"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            org.telegram.ui.-$$Lambda$DialogsActivity$2eTq7kt5gE_pbpNd6rTumxkHW30 r4 = org.telegram.ui.$$Lambda$DialogsActivity$2eTq7kt5gE_pbpNd6rTumxkHW30.INSTANCE
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = r0.setNegativeButton(r3, r4)
            org.telegram.ui.ActionBar.AlertDialog r0 = r0.create()
            r9.showDialog(r0)
        L_0x014e:
            int r0 = r9.archivePullViewState
            r3 = 2
            if (r0 != r3) goto L_0x0166
            androidx.recyclerview.widget.LinearLayoutManager r0 = r9.layoutManager
            int r0 = r0.findFirstVisibleItemPosition()
            if (r0 != 0) goto L_0x0166
            boolean r0 = r9.hasHiddenArchive()
            if (r0 == 0) goto L_0x0166
            androidx.recyclerview.widget.LinearLayoutManager r0 = r9.layoutManager
            r0.scrollToPositionWithOffset(r1, r2)
        L_0x0166:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.onResume():void");
    }

    public /* synthetic */ void lambda$onResume$6$DialogsActivity(int i) {
        this.askAboutContacts = i != 0;
        MessagesController.getGlobalNotificationsSettings().edit().putBoolean("askAboutContacts", this.askAboutContacts).commit();
        askForPermissons(false);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:?, code lost:
        return;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:4:0x000e */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$onResume$7$DialogsActivity(android.content.DialogInterface r2, int r3) {
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.lambda$onResume$7$DialogsActivity(android.content.DialogInterface, int):void");
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

    /* access modifiers changed from: protected */
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

    /* access modifiers changed from: private */
    public void showSearch(final boolean z, boolean z2) {
        if (this.dialogsType != 0) {
            z2 = false;
        }
        AnimatorSet animatorSet = this.searchAnimator;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.searchAnimator = null;
        }
        float f = 0.9f;
        float f2 = 0.0f;
        float f3 = 1.0f;
        if (z2) {
            if (z) {
                this.searchListView.setVisibility(0);
            } else {
                this.listView.setVisibility(0);
            }
            setDialogsListFrozen(true);
            this.listView.setVerticalScrollBarEnabled(false);
            this.searchListView.setVerticalScrollBarEnabled(false);
            this.searchListView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            this.searchAnimator = new AnimatorSet();
            AnimatorSet animatorSet2 = this.searchAnimator;
            Animator[] animatorArr = new Animator[12];
            DialogsRecyclerView dialogsRecyclerView = this.listView;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = z ? 0.0f : 1.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(dialogsRecyclerView, property, fArr);
            DialogsRecyclerView dialogsRecyclerView2 = this.listView;
            Property property2 = View.SCALE_X;
            float[] fArr2 = new float[1];
            fArr2[0] = z ? 0.9f : 1.0f;
            animatorArr[1] = ObjectAnimator.ofFloat(dialogsRecyclerView2, property2, fArr2);
            DialogsRecyclerView dialogsRecyclerView3 = this.listView;
            Property property3 = View.SCALE_Y;
            float[] fArr3 = new float[1];
            fArr3[0] = z ? 0.9f : 1.0f;
            animatorArr[2] = ObjectAnimator.ofFloat(dialogsRecyclerView3, property3, fArr3);
            RadialProgressView radialProgressView = this.progressView;
            Property property4 = View.ALPHA;
            float[] fArr4 = new float[1];
            fArr4[0] = z ? 0.0f : 1.0f;
            animatorArr[3] = ObjectAnimator.ofFloat(radialProgressView, property4, fArr4);
            RadialProgressView radialProgressView2 = this.progressView;
            Property property5 = View.SCALE_X;
            float[] fArr5 = new float[1];
            fArr5[0] = z ? 0.9f : 1.0f;
            animatorArr[4] = ObjectAnimator.ofFloat(radialProgressView2, property5, fArr5);
            RadialProgressView radialProgressView3 = this.progressView;
            Property property6 = View.SCALE_Y;
            float[] fArr6 = new float[1];
            if (!z) {
                f = 1.0f;
            }
            fArr6[0] = f;
            animatorArr[5] = ObjectAnimator.ofFloat(radialProgressView3, property6, fArr6);
            RecyclerListView recyclerListView = this.searchListView;
            Property property7 = View.ALPHA;
            float[] fArr7 = new float[1];
            fArr7[0] = z ? 1.0f : 0.0f;
            animatorArr[6] = ObjectAnimator.ofFloat(recyclerListView, property7, fArr7);
            RecyclerListView recyclerListView2 = this.searchListView;
            Property property8 = View.SCALE_X;
            float[] fArr8 = new float[1];
            fArr8[0] = z ? 1.0f : 1.05f;
            animatorArr[7] = ObjectAnimator.ofFloat(recyclerListView2, property8, fArr8);
            RecyclerListView recyclerListView3 = this.searchListView;
            Property property9 = View.SCALE_Y;
            float[] fArr9 = new float[1];
            fArr9[0] = z ? 1.0f : 1.05f;
            animatorArr[8] = ObjectAnimator.ofFloat(recyclerListView3, property9, fArr9);
            EmptyTextProgressView emptyTextProgressView = this.searchEmptyView;
            Property property10 = View.ALPHA;
            float[] fArr10 = new float[1];
            if (z) {
                f2 = 1.0f;
            }
            fArr10[0] = f2;
            animatorArr[9] = ObjectAnimator.ofFloat(emptyTextProgressView, property10, fArr10);
            EmptyTextProgressView emptyTextProgressView2 = this.searchEmptyView;
            Property property11 = View.SCALE_X;
            float[] fArr11 = new float[1];
            fArr11[0] = z ? 1.0f : 1.05f;
            animatorArr[10] = ObjectAnimator.ofFloat(emptyTextProgressView2, property11, fArr11);
            EmptyTextProgressView emptyTextProgressView3 = this.searchEmptyView;
            Property property12 = View.SCALE_Y;
            float[] fArr12 = new float[1];
            if (!z) {
                f3 = 1.05f;
            }
            fArr12[0] = f3;
            animatorArr[11] = ObjectAnimator.ofFloat(emptyTextProgressView3, property12, fArr12);
            animatorSet2.playTogether(animatorArr);
            this.searchAnimator.setDuration(z ? 200 : 180);
            this.searchAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            if (!z) {
                this.searchAnimator.setStartDelay(20);
            }
            this.searchAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (DialogsActivity.this.searchAnimator == animator) {
                        DialogsActivity.this.setDialogsListFrozen(false);
                        if (z) {
                            DialogsActivity.this.listView.hide();
                            DialogsActivity.this.searchListView.show();
                        } else {
                            DialogsActivity.this.searchEmptyView.setScaleX(1.1f);
                            DialogsActivity.this.searchEmptyView.setScaleY(1.1f);
                            DialogsActivity.this.searchListView.hide();
                            DialogsActivity.this.listView.show();
                            DialogsActivity.this.dialogsSearchAdapter.searchDialogs((String) null);
                            if (!DialogsActivity.this.onlySelect) {
                                DialogsActivity.this.hideFloatingButton(false);
                            }
                        }
                        DialogsActivity.this.listView.setVerticalScrollBarEnabled(true);
                        DialogsActivity.this.searchListView.setVerticalScrollBarEnabled(true);
                        DialogsActivity.this.searchListView.setBackground((Drawable) null);
                        AnimatorSet unused = DialogsActivity.this.searchAnimator = null;
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (DialogsActivity.this.searchAnimator == animator) {
                        if (z) {
                            DialogsActivity.this.listView.hide();
                            DialogsActivity.this.searchListView.show();
                        } else {
                            DialogsActivity.this.searchListView.hide();
                            DialogsActivity.this.listView.show();
                        }
                        AnimatorSet unused = DialogsActivity.this.searchAnimator = null;
                    }
                }
            });
            this.searchAnimator.start();
            return;
        }
        setDialogsListFrozen(false);
        if (z) {
            this.listView.hide();
            this.searchListView.show();
        } else {
            this.listView.show();
            this.searchListView.hide();
        }
        this.listView.setAlpha(z ? 0.0f : 1.0f);
        this.listView.setScaleX(z ? 0.9f : 1.0f);
        this.listView.setScaleY(z ? 0.9f : 1.0f);
        this.progressView.setAlpha(z ? 0.0f : 1.0f);
        this.progressView.setScaleX(z ? 0.9f : 1.0f);
        RadialProgressView radialProgressView4 = this.progressView;
        if (!z) {
            f = 1.0f;
        }
        radialProgressView4.setScaleY(f);
        this.searchListView.setAlpha(z ? 1.0f : 0.0f);
        float f4 = 1.1f;
        this.searchListView.setScaleX(z ? 1.0f : 1.1f);
        this.searchListView.setScaleY(z ? 1.0f : 1.1f);
        EmptyTextProgressView emptyTextProgressView4 = this.searchEmptyView;
        if (z) {
            f2 = 1.0f;
        }
        emptyTextProgressView4.setAlpha(f2);
        this.searchEmptyView.setScaleX(z ? 1.0f : 1.1f);
        EmptyTextProgressView emptyTextProgressView5 = this.searchEmptyView;
        if (z) {
            f4 = 1.0f;
        }
        emptyTextProgressView5.setScaleY(f4);
    }

    /* access modifiers changed from: private */
    public void findAndUpdateCheckBox(long j, boolean z) {
        int childCount = this.listView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.listView.getChildAt(i);
            if (childAt instanceof DialogCell) {
                DialogCell dialogCell = (DialogCell) childAt;
                if (dialogCell.getDialogId() == j) {
                    dialogCell.setChecked(z, true);
                    return;
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:93:0x0171 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x0172  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void onItemClick(android.view.View r11, int r12, androidx.recyclerview.widget.RecyclerView.Adapter r13) {
        /*
            r10 = this;
            android.app.Activity r0 = r10.getParentActivity()
            if (r0 != 0) goto L_0x0007
            return
        L_0x0007:
            org.telegram.ui.Adapters.DialogsAdapter r0 = r10.dialogsAdapter
            r1 = 1
            r2 = 32
            r3 = 0
            r5 = 0
            if (r13 != r0) goto L_0x00e2
            org.telegram.tgnet.TLObject r12 = r0.getItem(r12)
            boolean r0 = r12 instanceof org.telegram.tgnet.TLRPC.User
            if (r0 == 0) goto L_0x0020
            org.telegram.tgnet.TLRPC$User r12 = (org.telegram.tgnet.TLRPC.User) r12
            int r12 = r12.id
        L_0x001d:
            long r6 = (long) r12
            goto L_0x016b
        L_0x0020:
            boolean r0 = r12 instanceof org.telegram.tgnet.TLRPC.Dialog
            if (r0 == 0) goto L_0x005a
            org.telegram.tgnet.TLRPC$Dialog r12 = (org.telegram.tgnet.TLRPC.Dialog) r12
            boolean r0 = r12 instanceof org.telegram.tgnet.TLRPC.TL_dialogFolder
            if (r0 == 0) goto L_0x004c
            org.telegram.ui.ActionBar.ActionBar r11 = r10.actionBar
            boolean r11 = r11.isActionModeShowed()
            if (r11 == 0) goto L_0x0033
            return
        L_0x0033:
            org.telegram.tgnet.TLRPC$TL_dialogFolder r12 = (org.telegram.tgnet.TLRPC.TL_dialogFolder) r12
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
        L_0x004c:
            long r6 = r12.id
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            boolean r0 = r0.isActionModeShowed()
            if (r0 == 0) goto L_0x016b
            r10.showOrUpdateActionMode(r12, r11)
            return
        L_0x005a:
            boolean r0 = r12 instanceof org.telegram.tgnet.TLRPC.TL_recentMeUrlChat
            if (r0 == 0) goto L_0x0064
            org.telegram.tgnet.TLRPC$TL_recentMeUrlChat r12 = (org.telegram.tgnet.TLRPC.TL_recentMeUrlChat) r12
            int r12 = r12.chat_id
        L_0x0062:
            int r12 = -r12
            goto L_0x001d
        L_0x0064:
            boolean r0 = r12 instanceof org.telegram.tgnet.TLRPC.TL_recentMeUrlUser
            if (r0 == 0) goto L_0x006d
            org.telegram.tgnet.TLRPC$TL_recentMeUrlUser r12 = (org.telegram.tgnet.TLRPC.TL_recentMeUrlUser) r12
            int r12 = r12.user_id
            goto L_0x001d
        L_0x006d:
            boolean r0 = r12 instanceof org.telegram.tgnet.TLRPC.TL_recentMeUrlChatInvite
            if (r0 == 0) goto L_0x00b5
            org.telegram.tgnet.TLRPC$TL_recentMeUrlChatInvite r12 = (org.telegram.tgnet.TLRPC.TL_recentMeUrlChatInvite) r12
            org.telegram.tgnet.TLRPC$ChatInvite r0 = r12.chat_invite
            org.telegram.tgnet.TLRPC$Chat r6 = r0.chat
            if (r6 != 0) goto L_0x0081
            boolean r6 = r0.channel
            if (r6 == 0) goto L_0x0091
            boolean r6 = r0.megagroup
            if (r6 != 0) goto L_0x0091
        L_0x0081:
            org.telegram.tgnet.TLRPC$Chat r6 = r0.chat
            if (r6 == 0) goto L_0x00ad
            boolean r6 = org.telegram.messenger.ChatObject.isChannel(r6)
            if (r6 == 0) goto L_0x0091
            org.telegram.tgnet.TLRPC$Chat r6 = r0.chat
            boolean r6 = r6.megagroup
            if (r6 == 0) goto L_0x00ad
        L_0x0091:
            java.lang.String r11 = r12.url
            r12 = 47
            int r12 = r11.indexOf(r12)
            if (r12 <= 0) goto L_0x00a0
            int r12 = r12 + r1
            java.lang.String r11 = r11.substring(r12)
        L_0x00a0:
            org.telegram.ui.Components.JoinGroupAlert r12 = new org.telegram.ui.Components.JoinGroupAlert
            android.app.Activity r13 = r10.getParentActivity()
            r12.<init>(r13, r0, r11, r10)
            r10.showDialog(r12)
            return
        L_0x00ad:
            org.telegram.tgnet.TLRPC$Chat r12 = r0.chat
            if (r12 == 0) goto L_0x00b4
            int r12 = r12.id
            goto L_0x0062
        L_0x00b4:
            return
        L_0x00b5:
            boolean r11 = r12 instanceof org.telegram.tgnet.TLRPC.TL_recentMeUrlStickerSet
            if (r11 == 0) goto L_0x00dd
            org.telegram.tgnet.TLRPC$TL_recentMeUrlStickerSet r12 = (org.telegram.tgnet.TLRPC.TL_recentMeUrlStickerSet) r12
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
        L_0x00dd:
            boolean r11 = r12 instanceof org.telegram.tgnet.TLRPC.TL_recentMeUrlUnknown
            if (r11 == 0) goto L_0x00e1
        L_0x00e1:
            return
        L_0x00e2:
            org.telegram.ui.Adapters.DialogsSearchAdapter r0 = r10.dialogsSearchAdapter
            if (r13 != r0) goto L_0x016a
            java.lang.Object r0 = r0.getItem(r12)
            org.telegram.ui.Adapters.DialogsSearchAdapter r6 = r10.dialogsSearchAdapter
            boolean r12 = r6.isGlobalSearch(r12)
            boolean r6 = r0 instanceof org.telegram.tgnet.TLRPC.User
            if (r6 == 0) goto L_0x0103
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC.User) r0
            int r6 = r0.id
            long r6 = (long) r6
            boolean r8 = r10.onlySelect
            if (r8 != 0) goto L_0x016c
            r10.searchDialogId = r6
            r10.searchObject = r0
            goto L_0x016c
        L_0x0103:
            boolean r6 = r0 instanceof org.telegram.tgnet.TLRPC.Chat
            if (r6 == 0) goto L_0x0116
            org.telegram.tgnet.TLRPC$Chat r0 = (org.telegram.tgnet.TLRPC.Chat) r0
            int r6 = r0.id
            int r6 = -r6
            long r6 = (long) r6
            boolean r8 = r10.onlySelect
            if (r8 != 0) goto L_0x016c
            r10.searchDialogId = r6
            r10.searchObject = r0
            goto L_0x016c
        L_0x0116:
            boolean r6 = r0 instanceof org.telegram.tgnet.TLRPC.EncryptedChat
            if (r6 == 0) goto L_0x0129
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = (org.telegram.tgnet.TLRPC.EncryptedChat) r0
            int r6 = r0.id
            long r6 = (long) r6
            long r6 = r6 << r2
            boolean r8 = r10.onlySelect
            if (r8 != 0) goto L_0x016c
            r10.searchDialogId = r6
            r10.searchObject = r0
            goto L_0x016c
        L_0x0129:
            boolean r6 = r0 instanceof org.telegram.messenger.MessageObject
            if (r6 == 0) goto L_0x0141
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            long r6 = r0.getDialogId()
            int r0 = r0.getId()
            org.telegram.ui.Adapters.DialogsSearchAdapter r8 = r10.dialogsSearchAdapter
            java.lang.String r9 = r8.getLastSearchString()
            r8.addHashtagsFromMessage(r9)
            goto L_0x016d
        L_0x0141:
            boolean r6 = r0 instanceof java.lang.String
            if (r6 == 0) goto L_0x0168
            java.lang.String r0 = (java.lang.String) r0
            org.telegram.ui.Adapters.DialogsSearchAdapter r6 = r10.dialogsSearchAdapter
            boolean r6 = r6.isHashtagSearch()
            if (r6 == 0) goto L_0x0155
            org.telegram.ui.ActionBar.ActionBar r6 = r10.actionBar
            r6.openSearchField(r0, r5)
            goto L_0x0168
        L_0x0155:
            java.lang.String r6 = "section"
            boolean r6 = r0.equals(r6)
            if (r6 != 0) goto L_0x0168
            org.telegram.ui.NewContactActivity r6 = new org.telegram.ui.NewContactActivity
            r6.<init>()
            r6.setInitialPhoneNumber(r0)
            r10.presentFragment(r6)
        L_0x0168:
            r6 = r3
            goto L_0x016c
        L_0x016a:
            r6 = r3
        L_0x016b:
            r12 = 0
        L_0x016c:
            r0 = 0
        L_0x016d:
            int r8 = (r6 > r3 ? 1 : (r6 == r3 ? 0 : -1))
            if (r8 != 0) goto L_0x0172
            return
        L_0x0172:
            boolean r3 = r10.onlySelect
            if (r3 == 0) goto L_0x01a1
            boolean r12 = r10.validateSlowModeDialog(r6)
            if (r12 != 0) goto L_0x017d
            return
        L_0x017d:
            org.telegram.ui.Adapters.DialogsAdapter r12 = r10.dialogsAdapter
            boolean r12 = r12.hasSelectedDialogs()
            if (r12 == 0) goto L_0x019c
            org.telegram.ui.Adapters.DialogsAdapter r12 = r10.dialogsAdapter
            boolean r11 = r12.addOrRemoveSelectedDialog(r6, r11)
            org.telegram.ui.Adapters.DialogsSearchAdapter r12 = r10.dialogsSearchAdapter
            if (r13 != r12) goto L_0x0197
            org.telegram.ui.ActionBar.ActionBar r12 = r10.actionBar
            r12.closeSearchField()
            r10.findAndUpdateCheckBox(r6, r11)
        L_0x0197:
            r10.updateSelectedCount()
            goto L_0x0251
        L_0x019c:
            r10.didSelectResult(r6, r1, r5)
            goto L_0x0251
        L_0x01a1:
            android.os.Bundle r11 = new android.os.Bundle
            r11.<init>()
            int r1 = (int) r6
            long r2 = r6 >> r2
            int r3 = (int) r2
            if (r1 == 0) goto L_0x01dd
            if (r1 <= 0) goto L_0x01b5
            java.lang.String r2 = "user_id"
            r11.putInt(r2, r1)
            goto L_0x01e2
        L_0x01b5:
            if (r1 >= 0) goto L_0x01e2
            if (r0 == 0) goto L_0x01d6
            org.telegram.messenger.MessagesController r2 = r10.getMessagesController()
            int r3 = -r1
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r3)
            if (r2 == 0) goto L_0x01d6
            org.telegram.tgnet.TLRPC$InputChannel r3 = r2.migrated_to
            if (r3 == 0) goto L_0x01d6
            java.lang.String r3 = "migrated_to"
            r11.putInt(r3, r1)
            org.telegram.tgnet.TLRPC$InputChannel r1 = r2.migrated_to
            int r1 = r1.channel_id
            int r1 = -r1
        L_0x01d6:
            int r1 = -r1
            java.lang.String r2 = "chat_id"
            r11.putInt(r2, r1)
            goto L_0x01e2
        L_0x01dd:
            java.lang.String r1 = "enc_id"
            r11.putInt(r1, r3)
        L_0x01e2:
            if (r0 == 0) goto L_0x01ea
            java.lang.String r12 = "message_id"
            r11.putInt(r12, r0)
            goto L_0x01fe
        L_0x01ea:
            if (r12 != 0) goto L_0x01f0
            r10.closeSearch()
            goto L_0x01fe
        L_0x01f0:
            org.telegram.tgnet.TLObject r12 = r10.searchObject
            if (r12 == 0) goto L_0x01fe
            org.telegram.ui.Adapters.DialogsSearchAdapter r0 = r10.dialogsSearchAdapter
            long r1 = r10.searchDialogId
            r0.putRecentSearch(r1, r12)
            r12 = 0
            r10.searchObject = r12
        L_0x01fe:
            boolean r12 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r12 == 0) goto L_0x021d
            long r0 = r10.openedDialogId
            int r12 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r12 != 0) goto L_0x020f
            org.telegram.ui.Adapters.DialogsSearchAdapter r12 = r10.dialogsSearchAdapter
            if (r13 == r12) goto L_0x020f
            return
        L_0x020f:
            org.telegram.ui.Adapters.DialogsAdapter r12 = r10.dialogsAdapter
            if (r12 == 0) goto L_0x021d
            r10.openedDialogId = r6
            r12.setOpenedDialogId(r6)
            r12 = 512(0x200, float:7.175E-43)
            r10.updateVisibleRows(r12)
        L_0x021d:
            java.lang.String r12 = r10.searchString
            if (r12 == 0) goto L_0x023f
            org.telegram.messenger.MessagesController r12 = r10.getMessagesController()
            boolean r12 = r12.checkCanOpenChat(r11, r10)
            if (r12 == 0) goto L_0x0251
            org.telegram.messenger.NotificationCenter r12 = r10.getNotificationCenter()
            int r13 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r0 = new java.lang.Object[r5]
            r12.postNotificationName(r13, r0)
            org.telegram.ui.ChatActivity r12 = new org.telegram.ui.ChatActivity
            r12.<init>(r11)
            r10.presentFragment(r12)
            goto L_0x0251
        L_0x023f:
            org.telegram.messenger.MessagesController r12 = r10.getMessagesController()
            boolean r12 = r12.checkCanOpenChat(r11, r10)
            if (r12 == 0) goto L_0x0251
            org.telegram.ui.ChatActivity r12 = new org.telegram.ui.ChatActivity
            r12.<init>(r11)
            r10.presentFragment(r12)
        L_0x0251:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.onItemClick(android.view.View, int, androidx.recyclerview.widget.RecyclerView$Adapter):void");
    }

    /* access modifiers changed from: private */
    public boolean onItemLongClick(View view, int i, float f, float f2, RecyclerView.Adapter adapter) {
        String str;
        int i2;
        TLRPC.Chat chat;
        if (getParentActivity() == null) {
            return false;
        }
        if (!this.actionBar.isActionModeShowed() && !AndroidUtilities.isTablet() && !this.onlySelect && (view instanceof DialogCell)) {
            DialogCell dialogCell = (DialogCell) view;
            if (dialogCell.isPointInsideAvatar(f, f2)) {
                long dialogId = dialogCell.getDialogId();
                Bundle bundle = new Bundle();
                int i3 = (int) dialogId;
                int messageId = dialogCell.getMessageId();
                if (i3 == 0) {
                    return false;
                }
                if (i3 > 0) {
                    bundle.putInt("user_id", i3);
                } else if (i3 < 0) {
                    if (!(messageId == 0 || (chat = getMessagesController().getChat(Integer.valueOf(-i3))) == null || chat.migrated_to == null)) {
                        bundle.putInt("migrated_to", i3);
                        i3 = -chat.migrated_to.channel_id;
                    }
                    bundle.putInt("chat_id", -i3);
                }
                if (messageId != 0) {
                    bundle.putInt("message_id", messageId);
                }
                if (this.searchString != null) {
                    if (getMessagesController().checkCanOpenChat(bundle, this)) {
                        getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                        presentFragmentAsPreview(new ChatActivity(bundle));
                    }
                } else if (getMessagesController().checkCanOpenChat(bundle, this)) {
                    presentFragmentAsPreview(new ChatActivity(bundle));
                }
                return true;
            }
        }
        DialogsSearchAdapter dialogsSearchAdapter2 = this.dialogsSearchAdapter;
        if (adapter == dialogsSearchAdapter2) {
            dialogsSearchAdapter2.getItem(i);
            return false;
        }
        ArrayList<TLRPC.Dialog> dialogsArray = getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, this.dialogsListFrozen);
        int fixPosition = this.dialogsAdapter.fixPosition(i);
        if (fixPosition < 0 || fixPosition >= dialogsArray.size()) {
            return false;
        }
        TLRPC.Dialog dialog = dialogsArray.get(fixPosition);
        if (this.onlySelect) {
            if (this.dialogsType != 3 || !validateSlowModeDialog(dialog.id)) {
                return false;
            }
            this.dialogsAdapter.addOrRemoveSelectedDialog(dialog.id, view);
            updateSelectedCount();
        } else if (dialog instanceof TLRPC.TL_dialogFolder) {
            BottomSheet.Builder builder = new BottomSheet.Builder(getParentActivity());
            boolean z = dialog.unread_count != 0;
            int[] iArr = new int[2];
            iArr[0] = z ? NUM : 0;
            iArr[1] = SharedConfig.archiveHidden ? NUM : NUM;
            CharSequence[] charSequenceArr = new CharSequence[2];
            charSequenceArr[0] = z ? LocaleController.getString("MarkAllAsRead", NUM) : null;
            if (SharedConfig.archiveHidden) {
                i2 = NUM;
                str = "PinInTheList";
            } else {
                i2 = NUM;
                str = "HideAboveTheList";
            }
            charSequenceArr[1] = LocaleController.getString(str, i2);
            builder.setItems(charSequenceArr, iArr, new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    DialogsActivity.this.lambda$onItemLongClick$9$DialogsActivity(dialogInterface, i);
                }
            });
            showDialog(builder.create());
            return false;
        } else if (this.actionBar.isActionModeShowed() && dialog.pinned) {
            return false;
        } else {
            showOrUpdateActionMode(dialog, view);
        }
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:7:0x001f, code lost:
        if (r4.isFolderCell() != false) goto L_0x0023;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$onItemLongClick$9$DialogsActivity(android.content.DialogInterface r3, int r4) {
        /*
            r2 = this;
            r3 = 1
            if (r4 != 0) goto L_0x000b
            org.telegram.messenger.MessagesStorage r4 = r2.getMessagesStorage()
            r4.readAllDialogs(r3)
            goto L_0x0028
        L_0x000b:
            if (r4 != r3) goto L_0x0028
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r4 = r2.listView
            r0 = 0
            android.view.View r4 = r4.getChildAt(r0)
            r0 = 0
            boolean r1 = r4 instanceof org.telegram.ui.Cells.DialogCell
            if (r1 == 0) goto L_0x0022
            org.telegram.ui.Cells.DialogCell r4 = (org.telegram.ui.Cells.DialogCell) r4
            boolean r1 = r4.isFolderCell()
            if (r1 == 0) goto L_0x0022
            goto L_0x0023
        L_0x0022:
            r4 = r0
        L_0x0023:
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r2.listView
            r0.toggleArchiveHidden(r3, r4)
        L_0x0028:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.lambda$onItemLongClick$9$DialogsActivity(android.content.DialogInterface, int):void");
    }

    /* access modifiers changed from: private */
    public void updateFloatingButtonOffset() {
        this.floatingButtonContainer.setTranslationY(this.floatingButtonTranslation - (this.additionalFloatingTranslation * (1.0f - this.floatingButtonHideProgress)));
    }

    /* access modifiers changed from: private */
    public boolean hasHiddenArchive() {
        return this.listView.getAdapter() == this.dialogsAdapter && !this.onlySelect && this.dialogsType == 0 && this.folderId == 0 && getMessagesController().hasHiddenArchive();
    }

    /* access modifiers changed from: private */
    public boolean waitingForDialogsAnimationEnd() {
        return (!this.dialogsItemAnimator.isRunning() && this.dialogRemoveFinished == 0 && this.dialogInsertFinished == 0 && this.dialogChangeFinished == 0) ? false : true;
    }

    /* access modifiers changed from: private */
    public void onDialogAnimationFinished() {
        this.dialogRemoveFinished = 0;
        this.dialogInsertFinished = 0;
        this.dialogChangeFinished = 0;
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                DialogsActivity.this.lambda$onDialogAnimationFinished$10$DialogsActivity();
            }
        });
    }

    public /* synthetic */ void lambda$onDialogAnimationFinished$10$DialogsActivity() {
        ArrayList<TLRPC.Dialog> arrayList;
        if (this.folderId != 0 && ((arrayList = frozenDialogsList) == null || arrayList.isEmpty())) {
            this.listView.setEmptyView((View) null);
            this.progressView.setVisibility(4);
            finishFragment();
        }
        setDialogsListFrozen(false);
        updateDialogIndices();
    }

    /* access modifiers changed from: private */
    public void hideActionMode(boolean z) {
        this.actionBar.hideActionMode();
        if (this.menuDrawable != null) {
            this.actionBar.setBackButtonContentDescription(LocaleController.getString("AccDescrOpenMenu", NUM));
        }
        this.dialogsAdapter.getSelectedDialogs().clear();
        MenuDrawable menuDrawable2 = this.menuDrawable;
        if (menuDrawable2 != null) {
            menuDrawable2.setRotation(0.0f, true);
        } else {
            BackDrawable backDrawable2 = this.backDrawable;
            if (backDrawable2 != null) {
                backDrawable2.setRotation(0.0f, true);
            }
        }
        int i = 0;
        this.allowMoving = false;
        if (this.movingWas) {
            getMessagesController().reorderPinnedDialogs(this.folderId, (ArrayList<TLRPC.InputDialogPeer>) null, 0);
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
        ArrayList<TLRPC.Dialog> dialogs = getMessagesController().getDialogs(this.folderId);
        int size = dialogs.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            TLRPC.Dialog dialog = dialogs.get(i2);
            if (!(dialog instanceof TLRPC.TL_dialogFolder)) {
                long j = dialog.id;
                if (dialog.pinned) {
                    i++;
                } else if (!getMessagesController().isProxyDialog(dialog.id, false)) {
                    break;
                }
            }
        }
        return i;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x02ea  */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x02ef  */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x02f5  */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x0330  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void perfromSelectedDialogsAction(int r31, boolean r32) {
        /*
            r30 = this;
            r7 = r30
            r2 = r31
            android.app.Activity r0 = r30.getParentActivity()
            if (r0 != 0) goto L_0x000b
            return
        L_0x000b:
            org.telegram.ui.Adapters.DialogsAdapter r0 = r7.dialogsAdapter
            java.util.ArrayList r0 = r0.getSelectedDialogs()
            int r1 = r0.size()
            r3 = 105(0x69, float:1.47E-43)
            r6 = 4
            r8 = 0
            r9 = 1
            r10 = 0
            if (r2 != r3) goto L_0x009f
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>(r0)
            org.telegram.messenger.MessagesController r11 = r30.getMessagesController()
            int r0 = r7.folderId
            if (r0 != 0) goto L_0x002c
            r13 = 1
            goto L_0x002d
        L_0x002c:
            r13 = 0
        L_0x002d:
            r14 = -1
            r15 = 0
            r16 = 0
            r12 = r1
            r11.addDialogToFolder(r12, r13, r14, r15, r16)
            r7.hideActionMode(r10)
            int r0 = r7.folderId
            if (r0 != 0) goto L_0x0081
            android.content.SharedPreferences r0 = org.telegram.messenger.MessagesController.getGlobalMainSettings()
            java.lang.String r2 = "archivehint_l"
            boolean r3 = r0.getBoolean(r2, r10)
            if (r3 != 0) goto L_0x004c
            boolean r3 = org.telegram.messenger.SharedConfig.archiveHidden
            if (r3 == 0) goto L_0x004d
        L_0x004c:
            r10 = 1
        L_0x004d:
            if (r10 != 0) goto L_0x005a
            android.content.SharedPreferences$Editor r0 = r0.edit()
            android.content.SharedPreferences$Editor r0 = r0.putBoolean(r2, r9)
            r0.commit()
        L_0x005a:
            if (r10 == 0) goto L_0x0066
            int r0 = r1.size()
            if (r0 <= r9) goto L_0x0064
            r4 = 4
            goto L_0x0070
        L_0x0064:
            r4 = 2
            goto L_0x0070
        L_0x0066:
            int r0 = r1.size()
            if (r0 <= r9) goto L_0x006f
            r5 = 5
            r4 = 5
            goto L_0x0070
        L_0x006f:
            r4 = 3
        L_0x0070:
            r11 = r4
            org.telegram.ui.Components.UndoView r8 = r30.getUndoView()
            r9 = 0
            r12 = 0
            org.telegram.ui.-$$Lambda$DialogsActivity$Y2g4lm8qZJd5f-vrI40MwSBnWag r13 = new org.telegram.ui.-$$Lambda$DialogsActivity$Y2g4lm8qZJd5f-vrI40MwSBnWag
            r13.<init>(r1)
            r8.showWithAction(r9, r11, r12, r13)
            goto L_0x009e
        L_0x0081:
            org.telegram.messenger.MessagesController r0 = r30.getMessagesController()
            int r1 = r7.folderId
            java.util.ArrayList r0 = r0.getDialogs(r1)
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x009e
            org.telegram.ui.DialogsActivity$DialogsRecyclerView r0 = r7.listView
            r0.setEmptyView(r8)
            org.telegram.ui.Components.RadialProgressView r0 = r7.progressView
            r0.setVisibility(r6)
            r30.finishFragment()
        L_0x009e:
            return
        L_0x009f:
            r3 = 102(0x66, float:1.43E-43)
            r11 = 100
            if (r2 != r11) goto L_0x0165
            int r13 = r7.canPinCount
            if (r13 == 0) goto L_0x0165
            org.telegram.messenger.MessagesController r13 = r30.getMessagesController()
            int r14 = r7.folderId
            java.util.ArrayList r13 = r13.getDialogs(r14)
            int r14 = r13.size()
            r15 = 0
            r16 = 0
            r17 = 0
        L_0x00bc:
            if (r15 >= r14) goto L_0x00ed
            java.lang.Object r18 = r13.get(r15)
            r4 = r18
            org.telegram.tgnet.TLRPC$Dialog r4 = (org.telegram.tgnet.TLRPC.Dialog) r4
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_dialogFolder
            if (r5 == 0) goto L_0x00cb
            goto L_0x00e7
        L_0x00cb:
            long r11 = r4.id
            int r12 = (int) r11
            boolean r11 = r4.pinned
            if (r11 == 0) goto L_0x00da
            if (r12 != 0) goto L_0x00d7
            int r17 = r17 + 1
            goto L_0x00e7
        L_0x00d7:
            int r16 = r16 + 1
            goto L_0x00e7
        L_0x00da:
            org.telegram.messenger.MessagesController r11 = r30.getMessagesController()
            long r5 = r4.id
            boolean r4 = r11.isProxyDialog(r5, r10)
            if (r4 != 0) goto L_0x00e7
            goto L_0x00ed
        L_0x00e7:
            int r15 = r15 + 1
            r6 = 4
            r11 = 100
            goto L_0x00bc
        L_0x00ed:
            r4 = 0
            r5 = 0
            r6 = 0
        L_0x00f0:
            if (r4 >= r1) goto L_0x011a
            java.lang.Object r11 = r0.get(r4)
            java.lang.Long r11 = (java.lang.Long) r11
            long r13 = r11.longValue()
            org.telegram.messenger.MessagesController r11 = r30.getMessagesController()
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r11 = r11.dialogs_dict
            java.lang.Object r11 = r11.get(r13)
            org.telegram.tgnet.TLRPC$Dialog r11 = (org.telegram.tgnet.TLRPC.Dialog) r11
            if (r11 == 0) goto L_0x0117
            boolean r11 = r11.pinned
            if (r11 == 0) goto L_0x010f
            goto L_0x0117
        L_0x010f:
            int r11 = (int) r13
            if (r11 != 0) goto L_0x0115
            int r5 = r5 + 1
            goto L_0x0117
        L_0x0115:
            int r6 = r6 + 1
        L_0x0117:
            int r4 = r4 + 1
            goto L_0x00f0
        L_0x011a:
            int r4 = r7.folderId
            if (r4 == 0) goto L_0x0125
            org.telegram.messenger.MessagesController r4 = r30.getMessagesController()
            int r4 = r4.maxFolderPinnedDialogsCount
            goto L_0x012b
        L_0x0125:
            org.telegram.messenger.MessagesController r4 = r30.getMessagesController()
            int r4 = r4.maxPinnedDialogsCount
        L_0x012b:
            int r5 = r5 + r17
            if (r5 > r4) goto L_0x0133
            int r6 = r6 + r16
            if (r6 <= r4) goto L_0x026d
        L_0x0133:
            r0 = 2131626254(0x7f0e090e, float:1.887974E38)
            java.lang.Object[] r1 = new java.lang.Object[r9]
            java.lang.String r2 = "Chats"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r2, r4)
            r1[r10] = r2
            java.lang.String r2 = "PinToTopLimitReached"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            org.telegram.ui.Components.AlertsCreator.showSimpleAlert(r7, r0)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.pinItem
            r1 = 1073741824(0x40000000, float:2.0)
            org.telegram.messenger.AndroidUtilities.shakeView(r0, r1, r10)
            android.app.Activity r0 = r30.getParentActivity()
            java.lang.String r1 = "vibrator"
            java.lang.Object r0 = r0.getSystemService(r1)
            android.os.Vibrator r0 = (android.os.Vibrator) r0
            if (r0 == 0) goto L_0x0164
            r1 = 200(0xc8, double:9.9E-322)
            r0.vibrate(r1)
        L_0x0164:
            return
        L_0x0165:
            if (r2 == r3) goto L_0x016b
            r4 = 103(0x67, float:1.44E-43)
            if (r2 != r4) goto L_0x023f
        L_0x016b:
            if (r1 <= r9) goto L_0x023f
            if (r32 == 0) goto L_0x023f
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r4 = r30.getParentActivity()
            r0.<init>((android.content.Context) r4)
            if (r2 != r3) goto L_0x01ae
            r3 = 2131624879(0x7f0e03af, float:1.887695E38)
            java.lang.Object[] r4 = new java.lang.Object[r9]
            java.lang.String r5 = "ChatsSelected"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r5, r1)
            r4[r10] = r1
            java.lang.String r1 = "DeleteFewChatsTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4)
            r0.setTitle(r1)
            r1 = 2131624239(0x7f0e012f, float:1.8875652E38)
            java.lang.String r3 = "AreYouSureDeleteFewChats"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r0.setMessage(r1)
            r1 = 2131624858(0x7f0e039a, float:1.8876908E38)
            java.lang.String r3 = "Delete"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            org.telegram.ui.-$$Lambda$DialogsActivity$4jeFfbPa00Dvar_qyJvj0h0oI_X4 r3 = new org.telegram.ui.-$$Lambda$DialogsActivity$4jeFfbPa00Dvar_qyJvj0h0oI_X4
            r3.<init>(r2)
            r0.setPositiveButton(r1, r3)
            goto L_0x0219
        L_0x01ae:
            int r3 = r7.canClearCacheCount
            if (r3 == 0) goto L_0x01e6
            r3 = 2131624706(0x7f0e0302, float:1.88766E38)
            java.lang.Object[] r4 = new java.lang.Object[r9]
            java.lang.String r5 = "ChatsSelectedClearCache"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r5, r1)
            r4[r10] = r1
            java.lang.String r1 = "ClearCacheFewChatsTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4)
            r0.setTitle(r1)
            r1 = 2131624228(0x7f0e0124, float:1.887563E38)
            java.lang.String r3 = "AreYouSureClearHistoryCacheFewChats"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r0.setMessage(r1)
            r1 = 2131624710(0x7f0e0306, float:1.8876607E38)
            java.lang.String r3 = "ClearHistoryCache"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            org.telegram.ui.-$$Lambda$DialogsActivity$alhJl2oxrWyqDQR9VMC4_CoynGI r3 = new org.telegram.ui.-$$Lambda$DialogsActivity$alhJl2oxrWyqDQR9VMC4_CoynGI
            r3.<init>(r2)
            r0.setPositiveButton(r1, r3)
            goto L_0x0219
        L_0x01e6:
            r3 = 2131624708(0x7f0e0304, float:1.8876603E38)
            java.lang.Object[] r4 = new java.lang.Object[r9]
            java.lang.String r5 = "ChatsSelectedClear"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r5, r1)
            r4[r10] = r1
            java.lang.String r1 = "ClearFewChatsTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4)
            r0.setTitle(r1)
            r1 = 2131624230(0x7f0e0126, float:1.8875634E38)
            java.lang.String r3 = "AreYouSureClearHistoryFewChats"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r0.setMessage(r1)
            r1 = 2131624709(0x7f0e0305, float:1.8876605E38)
            java.lang.String r3 = "ClearHistory"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            org.telegram.ui.-$$Lambda$DialogsActivity$1j3JEkAThEHgG6O1BGBF0RX0na8 r3 = new org.telegram.ui.-$$Lambda$DialogsActivity$1j3JEkAThEHgG6O1BGBF0RX0na8
            r3.<init>(r2)
            r0.setPositiveButton(r1, r3)
        L_0x0219:
            r1 = 2131624479(0x7f0e021f, float:1.8876139E38)
            java.lang.String r2 = "Cancel"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setNegativeButton(r1, r8)
            org.telegram.ui.ActionBar.AlertDialog r0 = r0.create()
            r7.showDialog(r0)
            r1 = -1
            android.view.View r0 = r0.getButton(r1)
            android.widget.TextView r0 = (android.widget.TextView) r0
            if (r0 == 0) goto L_0x023e
            java.lang.String r1 = "dialogTextRed2"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setTextColor(r1)
        L_0x023e:
            return
        L_0x023f:
            r4 = 106(0x6a, float:1.49E-43)
            if (r2 != r4) goto L_0x026d
            if (r32 == 0) goto L_0x026d
            if (r1 != r9) goto L_0x025e
            java.lang.Object r2 = r0.get(r10)
            java.lang.Long r2 = (java.lang.Long) r2
            long r2 = r2.longValue()
            org.telegram.messenger.MessagesController r4 = r30.getMessagesController()
            int r3 = (int) r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r8 = r4.getUser(r2)
        L_0x025e:
            int r2 = r7.canReportSpamCount
            if (r2 == 0) goto L_0x0263
            goto L_0x0264
        L_0x0263:
            r9 = 0
        L_0x0264:
            org.telegram.ui.-$$Lambda$DialogsActivity$xYc-xNMCPfSpE4V8ESnuh56itnM r2 = new org.telegram.ui.-$$Lambda$DialogsActivity$xYc-xNMCPfSpE4V8ESnuh56itnM
            r2.<init>(r0)
            org.telegram.ui.Components.AlertsCreator.createBlockDialogAlert(r7, r1, r9, r8, r2)
            return
        L_0x026d:
            r4 = 0
            r6 = 0
        L_0x026f:
            if (r4 >= r1) goto L_0x046e
            java.lang.Object r5 = r0.get(r4)
            java.lang.Long r5 = (java.lang.Long) r5
            long r13 = r5.longValue()
            org.telegram.messenger.MessagesController r5 = r30.getMessagesController()
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r5 = r5.dialogs_dict
            java.lang.Object r5 = r5.get(r13)
            r11 = r5
            org.telegram.tgnet.TLRPC$Dialog r11 = (org.telegram.tgnet.TLRPC.Dialog) r11
            if (r11 != 0) goto L_0x028e
        L_0x028a:
            r5 = 103(0x67, float:1.44E-43)
            goto L_0x032c
        L_0x028e:
            int r15 = (int) r13
            r5 = 32
            long r8 = r13 >> r5
            int r5 = (int) r8
            if (r15 == 0) goto L_0x02b5
            if (r15 <= 0) goto L_0x02a5
            org.telegram.messenger.MessagesController r5 = r30.getMessagesController()
            java.lang.Integer r8 = java.lang.Integer.valueOf(r15)
            org.telegram.tgnet.TLRPC$User r5 = r5.getUser(r8)
            goto L_0x02d7
        L_0x02a5:
            org.telegram.messenger.MessagesController r5 = r30.getMessagesController()
            int r8 = -r15
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            org.telegram.tgnet.TLRPC$Chat r5 = r5.getChat(r8)
            r8 = r5
            r9 = 0
            goto L_0x02d9
        L_0x02b5:
            org.telegram.messenger.MessagesController r8 = r30.getMessagesController()
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$EncryptedChat r5 = r8.getEncryptedChat(r5)
            if (r5 == 0) goto L_0x02d2
            org.telegram.messenger.MessagesController r8 = r30.getMessagesController()
            int r5 = r5.user_id
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$User r5 = r8.getUser(r5)
            goto L_0x02d7
        L_0x02d2:
            org.telegram.tgnet.TLRPC$TL_userEmpty r5 = new org.telegram.tgnet.TLRPC$TL_userEmpty
            r5.<init>()
        L_0x02d7:
            r9 = r5
            r8 = 0
        L_0x02d9:
            if (r8 != 0) goto L_0x02de
            if (r9 != 0) goto L_0x02de
        L_0x02dd:
            goto L_0x028a
        L_0x02de:
            if (r9 == 0) goto L_0x02ef
            boolean r5 = r9.bot
            if (r5 == 0) goto L_0x02ef
            boolean r5 = org.telegram.messenger.MessagesController.isSupportUser(r9)
            if (r5 != 0) goto L_0x02ef
            r5 = 100
            r19 = 1
            goto L_0x02f3
        L_0x02ef:
            r5 = 100
            r19 = 0
        L_0x02f3:
            if (r2 != r5) goto L_0x0330
            int r8 = r7.canPinCount
            if (r8 == 0) goto L_0x0311
            boolean r8 = r11.pinned
            if (r8 == 0) goto L_0x02fe
            goto L_0x028a
        L_0x02fe:
            org.telegram.messenger.MessagesController r20 = r30.getMessagesController()
            r23 = 1
            r24 = 0
            r25 = -1
            r21 = r13
            boolean r8 = r20.pinDialog(r21, r23, r24, r25)
            if (r8 == 0) goto L_0x028a
            goto L_0x0329
        L_0x0311:
            boolean r8 = r11.pinned
            if (r8 != 0) goto L_0x0317
            goto L_0x028a
        L_0x0317:
            org.telegram.messenger.MessagesController r20 = r30.getMessagesController()
            r23 = 0
            r24 = 0
            r25 = -1
            r21 = r13
            boolean r8 = r20.pinDialog(r21, r23, r24, r25)
            if (r8 == 0) goto L_0x028a
        L_0x0329:
            r5 = 103(0x67, float:1.44E-43)
            r6 = 1
        L_0x032c:
            r9 = 2
        L_0x032d:
            r12 = 1
            goto L_0x0468
        L_0x0330:
            r5 = 101(0x65, float:1.42E-43)
            if (r2 != r5) goto L_0x036b
            int r5 = r7.canReadCount
            if (r5 == 0) goto L_0x035c
            org.telegram.messenger.MessagesController r5 = r30.getMessagesController()
            r5.markMentionsAsRead(r13)
            org.telegram.messenger.MessagesController r20 = r30.getMessagesController()
            int r5 = r11.top_message
            int r8 = r11.last_message_date
            r26 = 0
            r27 = 0
            r28 = 1
            r29 = 0
            r21 = r13
            r23 = r5
            r24 = r5
            r25 = r8
            r20.markDialogAsRead(r21, r23, r24, r25, r26, r27, r28, r29)
            goto L_0x028a
        L_0x035c:
            org.telegram.messenger.MessagesController r20 = r30.getMessagesController()
            r23 = 0
            r24 = 0
            r21 = r13
            r20.markDialogAsUnread(r21, r23, r24)
            goto L_0x028a
        L_0x036b:
            if (r2 == r3) goto L_0x03c5
            r5 = 103(0x67, float:1.44E-43)
            if (r2 != r5) goto L_0x0375
            r5 = 1
            r11 = 4
            r12 = 3
            goto L_0x03c8
        L_0x0375:
            r5 = 104(0x68, float:1.46E-43)
            if (r2 != r5) goto L_0x03c2
            r5 = 1
            if (r1 != r5) goto L_0x0391
            int r8 = r7.canMuteCount
            if (r8 != r5) goto L_0x0391
            android.app.Activity r0 = r30.getParentActivity()
            android.app.Dialog r0 = org.telegram.ui.Components.AlertsCreator.createMuteAlert(r0, r13)
            org.telegram.ui.-$$Lambda$DialogsActivity$ho7ONqjbjyuGWlCPEGzcIqYTqLY r1 = new org.telegram.ui.-$$Lambda$DialogsActivity$ho7ONqjbjyuGWlCPEGzcIqYTqLY
            r1.<init>()
            r7.showDialog(r0, r1)
            return
        L_0x0391:
            int r5 = r7.canUnmuteCount
            if (r5 == 0) goto L_0x03ab
            org.telegram.messenger.MessagesController r5 = r30.getMessagesController()
            boolean r5 = r5.isDialogMuted(r13)
            if (r5 != 0) goto L_0x03a1
            goto L_0x02dd
        L_0x03a1:
            org.telegram.messenger.NotificationsController r5 = r30.getNotificationsController()
            r11 = 4
            r5.setDialogNotificationsSettings(r13, r11)
            goto L_0x028a
        L_0x03ab:
            r11 = 4
            org.telegram.messenger.MessagesController r5 = r30.getMessagesController()
            boolean r5 = r5.isDialogMuted(r13)
            if (r5 == 0) goto L_0x03b8
            goto L_0x02dd
        L_0x03b8:
            org.telegram.messenger.NotificationsController r5 = r30.getNotificationsController()
            r12 = 3
            r5.setDialogNotificationsSettings(r13, r12)
            goto L_0x028a
        L_0x03c2:
            r11 = 4
            goto L_0x028a
        L_0x03c5:
            r11 = 4
            r12 = 3
            r5 = 1
        L_0x03c8:
            if (r1 != r5) goto L_0x03f2
            r5 = 103(0x67, float:1.44E-43)
            if (r2 != r5) goto L_0x03d0
            r11 = 1
            goto L_0x03d1
        L_0x03d0:
            r11 = 0
        L_0x03d1:
            if (r15 != 0) goto L_0x03d6
            r17 = 1
            goto L_0x03d8
        L_0x03d6:
            r17 = 0
        L_0x03d8:
            org.telegram.ui.-$$Lambda$DialogsActivity$ZIbUAHN43var_nOGYeb2G-iiCfE r10 = new org.telegram.ui.-$$Lambda$DialogsActivity$ZIbUAHN43var_nOGYeb2G-iiCfE
            r0 = r10
            r1 = r30
            r2 = r31
            r3 = r8
            r4 = r13
            r6 = r19
            r0.<init>(r2, r3, r4, r6)
            r0 = r30
            r1 = r11
            r2 = r8
            r3 = r9
            r4 = r17
            r5 = r10
            org.telegram.ui.Components.AlertsCreator.createClearOrDeleteDialogAlert(r0, r1, r2, r3, r4, r5)
            return
        L_0x03f2:
            r5 = 103(0x67, float:1.44E-43)
            if (r2 != r5) goto L_0x0404
            int r9 = r7.canClearCacheCount
            if (r9 == 0) goto L_0x0404
            org.telegram.messenger.MessagesController r8 = r30.getMessagesController()
            r9 = 2
            r8.deleteDialog(r13, r9, r10)
            goto L_0x032d
        L_0x0404:
            r9 = 2
            if (r2 != r5) goto L_0x0411
            org.telegram.messenger.MessagesController r8 = r30.getMessagesController()
            r15 = 1
            r8.deleteDialog(r13, r15, r10)
            goto L_0x032d
        L_0x0411:
            if (r8 == 0) goto L_0x0440
            boolean r8 = org.telegram.messenger.ChatObject.isNotInChat(r8)
            if (r8 == 0) goto L_0x0421
            org.telegram.messenger.MessagesController r8 = r30.getMessagesController()
            r8.deleteDialog(r13, r10, r10)
            goto L_0x0450
        L_0x0421:
            org.telegram.messenger.MessagesController r8 = r30.getMessagesController()
            org.telegram.messenger.UserConfig r15 = r30.getUserConfig()
            int r15 = r15.getClientUserId()
            java.lang.Integer r15 = java.lang.Integer.valueOf(r15)
            org.telegram.tgnet.TLRPC$User r8 = r8.getUser(r15)
            org.telegram.messenger.MessagesController r15 = r30.getMessagesController()
            long r11 = -r13
            int r12 = (int) r11
            r11 = 0
            r15.deleteUserFromChat(r12, r8, r11)
            goto L_0x0450
        L_0x0440:
            org.telegram.messenger.MessagesController r8 = r30.getMessagesController()
            r8.deleteDialog(r13, r10, r10)
            if (r19 == 0) goto L_0x0450
            org.telegram.messenger.MessagesController r8 = r30.getMessagesController()
            r8.blockUser(r15)
        L_0x0450:
            boolean r8 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r8 == 0) goto L_0x032d
            org.telegram.messenger.NotificationCenter r8 = r30.getNotificationCenter()
            int r11 = org.telegram.messenger.NotificationCenter.closeChats
            r12 = 1
            java.lang.Object[] r15 = new java.lang.Object[r12]
            java.lang.Long r13 = java.lang.Long.valueOf(r13)
            r15[r10] = r13
            r8.postNotificationName(r11, r15)
        L_0x0468:
            int r4 = r4 + 1
            r8 = 0
            r9 = 1
            goto L_0x026f
        L_0x046e:
            r4 = 100
            r12 = 1
            if (r2 != r4) goto L_0x047f
            org.telegram.messenger.MessagesController r0 = r30.getMessagesController()
            int r1 = r7.folderId
            r8 = 0
            r4 = 0
            r0.reorderPinnedDialogs(r1, r4, r8)
        L_0x047f:
            if (r6 == 0) goto L_0x0487
            r7.hideFloatingButton(r10)
            r30.scrollToTop()
        L_0x0487:
            r0 = 100
            if (r2 == r0) goto L_0x048e
            if (r2 == r3) goto L_0x048e
            r10 = 1
        L_0x048e:
            r7.hideActionMode(r10)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.perfromSelectedDialogsAction(int, boolean):void");
    }

    public /* synthetic */ void lambda$perfromSelectedDialogsAction$11$DialogsActivity(ArrayList arrayList) {
        getMessagesController().addDialogToFolder(arrayList, this.folderId == 0 ? 0 : 1, -1, (ArrayList<TLRPC.TL_inputFolderPeer>) null, 0);
    }

    public /* synthetic */ void lambda$perfromSelectedDialogsAction$12$DialogsActivity(int i, DialogInterface dialogInterface, int i2) {
        getMessagesController().setDialogsInTransaction(true);
        perfromSelectedDialogsAction(i, false);
        getMessagesController().setDialogsInTransaction(false);
        MessagesController.getInstance(this.currentAccount).checkIfFolderEmpty(this.folderId);
        int i3 = this.folderId;
        if (i3 != 0 && getDialogsArray(this.currentAccount, this.dialogsType, i3, false).size() == 0) {
            this.listView.setEmptyView((View) null);
            this.progressView.setVisibility(4);
            finishFragment();
        }
    }

    public /* synthetic */ void lambda$perfromSelectedDialogsAction$13$DialogsActivity(int i, DialogInterface dialogInterface, int i2) {
        perfromSelectedDialogsAction(i, false);
    }

    public /* synthetic */ void lambda$perfromSelectedDialogsAction$14$DialogsActivity(int i, DialogInterface dialogInterface, int i2) {
        perfromSelectedDialogsAction(i, false);
    }

    public /* synthetic */ void lambda$perfromSelectedDialogsAction$15$DialogsActivity(ArrayList arrayList, boolean z, boolean z2) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            ArrayList arrayList2 = arrayList;
            long longValue = ((Long) arrayList.get(i)).longValue();
            int i2 = (int) longValue;
            if (z) {
                getMessagesController().reportSpam(longValue, getMessagesController().getUser(Integer.valueOf(i2)), (TLRPC.Chat) null, (TLRPC.EncryptedChat) null, false);
            }
            if (z2) {
                getMessagesController().deleteDialog(longValue, 0, true);
            }
            getMessagesController().blockUser(i2);
        }
        hideActionMode(false);
    }

    public /* synthetic */ void lambda$perfromSelectedDialogsAction$17$DialogsActivity(int i, TLRPC.Chat chat, long j, boolean z, boolean z2) {
        int i2;
        int i3 = i;
        TLRPC.Chat chat2 = chat;
        long j2 = j;
        hideActionMode(false);
        if (i3 != 103 || !ChatObject.isChannel(chat) || (chat2.megagroup && TextUtils.isEmpty(chat2.username))) {
            boolean z3 = z2;
            if (i3 == 102 && (i2 = this.folderId) != 0 && getDialogsArray(this.currentAccount, this.dialogsType, i2, false).size() == 1) {
                this.progressView.setVisibility(4);
            }
            getUndoView().showWithAction(j2, i3 == 103 ? 0 : 1, (Runnable) new Runnable(i, j, z2, chat, z) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ long f$2;
                private final /* synthetic */ boolean f$3;
                private final /* synthetic */ TLRPC.Chat f$4;
                private final /* synthetic */ boolean f$5;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r5;
                    this.f$4 = r6;
                    this.f$5 = r7;
                }

                public final void run() {
                    DialogsActivity.this.lambda$null$16$DialogsActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
                }
            });
            return;
        }
        getMessagesController().deleteDialog(j2, 2, z2);
    }

    public /* synthetic */ void lambda$null$16$DialogsActivity(int i, long j, boolean z, TLRPC.Chat chat, boolean z2) {
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
            getMessagesController().deleteUserFromChat((int) (-j), getMessagesController().getUser(Integer.valueOf(getUserConfig().getClientUserId())), (TLRPC.ChatFull) null);
        }
        if (AndroidUtilities.isTablet()) {
            getNotificationCenter().postNotificationName(NotificationCenter.closeChats, Long.valueOf(j));
        }
        MessagesController.getInstance(this.currentAccount).checkIfFolderEmpty(this.folderId);
    }

    public /* synthetic */ void lambda$perfromSelectedDialogsAction$18$DialogsActivity(DialogInterface dialogInterface) {
        hideActionMode(true);
    }

    private void scrollToTop() {
        int findFirstVisibleItemPosition = this.layoutManager.findFirstVisibleItemPosition() * AndroidUtilities.dp(SharedConfig.useThreeLinesLayout ? 78.0f : 72.0f);
        boolean hasHiddenArchive = hasHiddenArchive();
        if (((float) findFirstVisibleItemPosition) >= ((float) this.listView.getMeasuredHeight()) * 1.2f) {
            this.scrollHelper.setScrollDirection(1);
            this.scrollHelper.scrollToPosition(hasHiddenArchive, 0, false, true);
            return;
        }
        this.listView.smoothScrollToPosition(hasHiddenArchive ? 1 : 0);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00e0, code lost:
        if (r5.getBoolean("dialog_bar_report" + r14, true) != false) goto L_0x00e4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateCounters(boolean r20) {
        /*
            r19 = this;
            r0 = r19
            r1 = 0
            r0.canUnmuteCount = r1
            r0.canMuteCount = r1
            r0.canPinCount = r1
            r0.canReadCount = r1
            r0.canClearCacheCount = r1
            r0.canReportSpamCount = r1
            if (r20 == 0) goto L_0x0012
            return
        L_0x0012:
            org.telegram.ui.Adapters.DialogsAdapter r2 = r0.dialogsAdapter
            java.util.ArrayList r2 = r2.getSelectedDialogs()
            int r3 = r2.size()
            org.telegram.messenger.UserConfig r4 = r19.getUserConfig()
            int r4 = r4.getClientUserId()
            android.content.SharedPreferences r5 = r19.getNotificationsSettings()
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
        L_0x002f:
            if (r6 >= r3) goto L_0x01b6
            org.telegram.messenger.MessagesController r13 = r19.getMessagesController()
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r13 = r13.dialogs_dict
            java.lang.Object r14 = r2.get(r6)
            java.lang.Long r14 = (java.lang.Long) r14
            long r14 = r14.longValue()
            java.lang.Object r13 = r13.get(r14)
            org.telegram.tgnet.TLRPC$Dialog r13 = (org.telegram.tgnet.TLRPC.Dialog) r13
            if (r13 != 0) goto L_0x0051
            r20 = r2
            r16 = r3
            r17 = r6
            goto L_0x01ad
        L_0x0051:
            long r14 = r13.id
            boolean r1 = r13.pinned
            r20 = r2
            int r2 = r13.unread_count
            r16 = r3
            if (r2 != 0) goto L_0x0064
            boolean r2 = r13.unread_mark
            if (r2 == 0) goto L_0x0062
            goto L_0x0064
        L_0x0062:
            r2 = 0
            goto L_0x0065
        L_0x0064:
            r2 = 1
        L_0x0065:
            org.telegram.messenger.MessagesController r3 = r19.getMessagesController()
            boolean r3 = r3.isDialogMuted(r14)
            if (r3 == 0) goto L_0x0078
            int r3 = r0.canUnmuteCount
            r17 = r6
            r6 = 1
            int r3 = r3 + r6
            r0.canUnmuteCount = r3
            goto L_0x0080
        L_0x0078:
            r17 = r6
            r6 = 1
            int r3 = r0.canMuteCount
            int r3 = r3 + r6
            r0.canMuteCount = r3
        L_0x0080:
            if (r2 == 0) goto L_0x0087
            int r2 = r0.canReadCount
            int r2 = r2 + r6
            r0.canReadCount = r2
        L_0x0087:
            int r2 = r0.folderId
            if (r2 != r6) goto L_0x008e
            int r9 = r9 + 1
            goto L_0x00a7
        L_0x008e:
            long r2 = (long) r4
            int r6 = (r14 > r2 ? 1 : (r14 == r2 ? 0 : -1))
            if (r6 == 0) goto L_0x00a7
            r2 = 777000(0xbdb28, double:3.83889E-318)
            int r6 = (r14 > r2 ? 1 : (r14 == r2 ? 0 : -1))
            if (r6 == 0) goto L_0x00a7
            org.telegram.messenger.MessagesController r2 = r19.getMessagesController()
            r3 = 0
            boolean r2 = r2.isProxyDialog(r14, r3)
            if (r2 != 0) goto L_0x00a7
            int r10 = r10 + 1
        L_0x00a7:
            int r2 = (int) r14
            r3 = 32
            r18 = r9
            r6 = r10
            long r9 = r14 >> r3
            int r3 = (int) r9
            if (r2 <= 0) goto L_0x00ea
            if (r2 != r4) goto L_0x00b5
            goto L_0x00ea
        L_0x00b5:
            org.telegram.messenger.MessagesController r9 = r19.getMessagesController()
            java.lang.Integer r10 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r9 = r9.getUser(r10)
            boolean r9 = org.telegram.messenger.MessagesController.isSupportUser(r9)
            if (r9 == 0) goto L_0x00c8
            goto L_0x00ea
        L_0x00c8:
            if (r2 == 0) goto L_0x00e3
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "dialog_bar_report"
            r9.append(r10)
            r9.append(r14)
            java.lang.String r9 = r9.toString()
            r10 = 1
            boolean r9 = r5.getBoolean(r9, r10)
            if (r9 == 0) goto L_0x00ec
            goto L_0x00e4
        L_0x00e3:
            r10 = 1
        L_0x00e4:
            int r9 = r0.canReportSpamCount
            int r9 = r9 + r10
            r0.canReportSpamCount = r9
            goto L_0x00ec
        L_0x00ea:
            int r12 = r12 + 1
        L_0x00ec:
            boolean r9 = org.telegram.messenger.DialogObject.isChannel(r13)
            if (r9 == 0) goto L_0x0143
            org.telegram.messenger.MessagesController r3 = r19.getMessagesController()
            int r2 = -r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r2 = r3.getChat(r2)
            org.telegram.messenger.MessagesController r3 = r19.getMessagesController()
            long r9 = r13.id
            r13 = 1
            boolean r3 = r3.isProxyDialog(r9, r13)
            if (r3 == 0) goto L_0x0112
            int r1 = r0.canClearCacheCount
            int r1 = r1 + r13
            r0.canClearCacheCount = r1
            goto L_0x013e
        L_0x0112:
            if (r1 == 0) goto L_0x0117
            int r11 = r11 + 1
            goto L_0x011c
        L_0x0117:
            int r1 = r0.canPinCount
            int r1 = r1 + r13
            r0.canPinCount = r1
        L_0x011c:
            if (r2 == 0) goto L_0x0136
            boolean r1 = r2.megagroup
            if (r1 == 0) goto L_0x0136
            java.lang.String r1 = r2.username
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 == 0) goto L_0x012e
            int r8 = r8 + 1
            goto L_0x01aa
        L_0x012e:
            int r1 = r0.canClearCacheCount
            r9 = 1
            int r1 = r1 + r9
            r0.canClearCacheCount = r1
            goto L_0x01aa
        L_0x0136:
            r9 = 1
            int r1 = r0.canClearCacheCount
            int r1 = r1 + r9
            r0.canClearCacheCount = r1
            goto L_0x01aa
        L_0x013e:
            r10 = r6
            r9 = r18
            goto L_0x01ad
        L_0x0143:
            r9 = 1
            if (r2 >= 0) goto L_0x014a
            if (r3 == r9) goto L_0x014a
            r9 = 1
            goto L_0x014b
        L_0x014a:
            r9 = 0
        L_0x014b:
            if (r9 == 0) goto L_0x0159
            org.telegram.messenger.MessagesController r10 = r19.getMessagesController()
            int r13 = -r2
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            r10.getChat(r13)
        L_0x0159:
            if (r2 != 0) goto L_0x017e
            org.telegram.messenger.MessagesController r2 = r19.getMessagesController()
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$EncryptedChat r2 = r2.getEncryptedChat(r3)
            if (r2 == 0) goto L_0x0178
            org.telegram.messenger.MessagesController r3 = r19.getMessagesController()
            int r2 = r2.user_id
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r2 = r3.getUser(r2)
            goto L_0x0193
        L_0x0178:
            org.telegram.tgnet.TLRPC$TL_userEmpty r2 = new org.telegram.tgnet.TLRPC$TL_userEmpty
            r2.<init>()
            goto L_0x0193
        L_0x017e:
            if (r9 != 0) goto L_0x0192
            if (r2 <= 0) goto L_0x0192
            r9 = 1
            if (r3 == r9) goto L_0x0192
            org.telegram.messenger.MessagesController r3 = r19.getMessagesController()
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r2 = r3.getUser(r2)
            goto L_0x0193
        L_0x0192:
            r2 = 0
        L_0x0193:
            if (r2 == 0) goto L_0x019d
            boolean r3 = r2.bot
            if (r3 == 0) goto L_0x019d
            boolean r2 = org.telegram.messenger.MessagesController.isSupportUser(r2)
        L_0x019d:
            if (r1 == 0) goto L_0x01a2
            int r11 = r11 + 1
            goto L_0x01a8
        L_0x01a2:
            int r1 = r0.canPinCount
            r2 = 1
            int r1 = r1 + r2
            r0.canPinCount = r1
        L_0x01a8:
            int r8 = r8 + 1
        L_0x01aa:
            int r7 = r7 + 1
            goto L_0x013e
        L_0x01ad:
            int r6 = r17 + 1
            r2 = r20
            r3 = r16
            r1 = 0
            goto L_0x002f
        L_0x01b6:
            r16 = r3
            r1 = 8
            r2 = r16
            if (r7 == r2) goto L_0x01c4
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.deleteItem
            r3.setVisibility(r1)
            goto L_0x01ca
        L_0x01c4:
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.deleteItem
            r4 = 0
            r3.setVisibility(r4)
        L_0x01ca:
            int r3 = r0.canClearCacheCount
            if (r3 == 0) goto L_0x01d0
            if (r3 != r2) goto L_0x01d4
        L_0x01d0:
            if (r8 == 0) goto L_0x01da
            if (r8 == r2) goto L_0x01da
        L_0x01d4:
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r3 = r0.clearItem
            r3.setVisibility(r1)
            goto L_0x0201
        L_0x01da:
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r3 = r0.clearItem
            r4 = 0
            r3.setVisibility(r4)
            int r3 = r0.canClearCacheCount
            if (r3 == 0) goto L_0x01f3
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r3 = r0.clearItem
            r4 = 2131624710(0x7f0e0306, float:1.8876607E38)
            java.lang.String r5 = "ClearHistoryCache"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r3.setText(r4)
            goto L_0x0201
        L_0x01f3:
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r3 = r0.clearItem
            r4 = 2131624709(0x7f0e0305, float:1.8876605E38)
            java.lang.String r5 = "ClearHistory"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r3.setText(r4)
        L_0x0201:
            if (r9 == 0) goto L_0x021b
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r3 = r0.archiveItem
            r4 = 2131626894(0x7f0e0b8e, float:1.8881037E38)
            java.lang.String r5 = "Unarchive"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r5 = 2131165713(0x7var_, float:1.794565E38)
            r3.setTextAndIcon(r4, r5)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r3 = r0.archiveItem
            r4 = 0
            r3.setVisibility(r4)
            goto L_0x023a
        L_0x021b:
            r4 = 0
            if (r10 == 0) goto L_0x0235
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r3 = r0.archiveItem
            r5 = 2131624199(0x7f0e0107, float:1.887557E38)
            java.lang.String r6 = "Archive"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r6 = 2131165648(0x7var_d0, float:1.794552E38)
            r3.setTextAndIcon(r5, r6)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r3 = r0.archiveItem
            r3.setVisibility(r4)
            goto L_0x023a
        L_0x0235:
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r3 = r0.archiveItem
            r3.setVisibility(r1)
        L_0x023a:
            int r3 = r0.canPinCount
            int r3 = r3 + r11
            if (r3 == r2) goto L_0x0246
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.pinItem
            r2.setVisibility(r1)
            r3 = 0
            goto L_0x024c
        L_0x0246:
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.pinItem
            r3 = 0
            r2.setVisibility(r3)
        L_0x024c:
            if (r12 == 0) goto L_0x0254
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r2 = r0.blockItem
            r2.setVisibility(r1)
            goto L_0x0259
        L_0x0254:
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r1 = r0.blockItem
            r1.setVisibility(r3)
        L_0x0259:
            int r1 = r0.canUnmuteCount
            if (r1 == 0) goto L_0x0274
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.muteItem
            r2 = 2131165715(0x7var_, float:1.7945655E38)
            r1.setIcon((int) r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.muteItem
            r2 = 2131624680(0x7f0e02e8, float:1.8876547E38)
            java.lang.String r3 = "ChatsUnmute"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setContentDescription(r2)
            goto L_0x028a
        L_0x0274:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.muteItem
            r2 = 2131165684(0x7var_f4, float:1.7945592E38)
            r1.setIcon((int) r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.muteItem
            r2 = 2131624660(0x7f0e02d4, float:1.8876506E38)
            java.lang.String r3 = "ChatsMute"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setContentDescription(r2)
        L_0x028a:
            int r1 = r0.canReadCount
            if (r1 == 0) goto L_0x02a0
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r1 = r0.readItem
            r2 = 2131625523(0x7f0e0633, float:1.8878256E38)
            java.lang.String r3 = "MarkAsRead"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r3 = 2131165679(0x7var_ef, float:1.7945582E38)
            r1.setTextAndIcon(r2, r3)
            goto L_0x02b1
        L_0x02a0:
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r1 = r0.readItem
            r2 = 2131625524(0x7f0e0634, float:1.8878258E38)
            java.lang.String r3 = "MarkAsUnread"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r3 = 2131165680(0x7var_f0, float:1.7945584E38)
            r1.setTextAndIcon(r2, r3)
        L_0x02b1:
            int r1 = r0.canPinCount
            if (r1 == 0) goto L_0x02cc
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.pinItem
            r2 = 2131165691(0x7var_fb, float:1.7945606E38)
            r1.setIcon((int) r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.pinItem
            r2 = 2131626253(0x7f0e090d, float:1.8879737E38)
            java.lang.String r3 = "PinToTop"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setContentDescription(r2)
            goto L_0x02e2
        L_0x02cc:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.pinItem
            r2 = 2131165716(0x7var_, float:1.7945657E38)
            r1.setIcon((int) r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.pinItem
            r2 = 2131626908(0x7f0e0b9c, float:1.8881065E38)
            java.lang.String r3 = "UnpinFromTop"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setContentDescription(r2)
        L_0x02e2:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.updateCounters(boolean):void");
    }

    /* access modifiers changed from: private */
    public boolean validateSlowModeDialog(long j) {
        int i;
        TLRPC.Chat chat;
        ChatActivityEnterView chatActivityEnterView;
        if ((this.messagesCount <= 1 && ((chatActivityEnterView = this.commentView) == null || chatActivityEnterView.getVisibility() != 0 || TextUtils.isEmpty(this.commentView.getFieldText()))) || (i = (int) j) >= 0 || (chat = getMessagesController().getChat(Integer.valueOf(-i))) == null || ChatObject.hasAdminRights(chat) || !chat.slowmode_enabled) {
            return true;
        }
        AlertsCreator.showSimpleAlert(this, LocaleController.getString("Slowmode", NUM), LocaleController.getString("SlowmodeSendError", NUM));
        return false;
    }

    private void showOrUpdateActionMode(TLRPC.Dialog dialog, View view) {
        this.dialogsAdapter.addOrRemoveSelectedDialog(dialog.id, view);
        ArrayList<Long> selectedDialogs = this.dialogsAdapter.getSelectedDialogs();
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
                View view2 = this.actionModeViews.get(i);
                view2.setPivotY((float) (ActionBar.getCurrentActionBarHeight() / 2));
                AndroidUtilities.clearDrawableAnimation(view2);
                arrayList.add(ObjectAnimator.ofFloat(view2, View.SCALE_Y, new float[]{0.1f, 1.0f}));
            }
            animatorSet.playTogether(arrayList);
            animatorSet.setDuration(200);
            animatorSet.start();
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
        } else if (selectedDialogs.isEmpty()) {
            hideActionMode(true);
            return;
        }
        updateCounters(false);
        this.selectedDialogsCountTextView.setNumber(selectedDialogs.size(), z);
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
                this.dialogsSearchAdapter.putRecentSearch(this.searchDialogId, tLObject);
                this.searchObject = null;
                return;
            }
            return;
        }
        this.closeSearchFieldOnHide = true;
    }

    /* access modifiers changed from: protected */
    public RecyclerListView getListView() {
        return this.listView;
    }

    /* access modifiers changed from: protected */
    public RecyclerListView getSearchListView() {
        return this.searchListView;
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
        if (this.proxyDrawable != null) {
            boolean z2 = false;
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
            boolean z3 = sharedPreferences.getBoolean("proxy_enabled", false) && !TextUtils.isEmpty(sharedPreferences.getString("proxy_ip", ""));
            if (z3 || (getMessagesController().blockedCountry && !SharedConfig.proxyList.isEmpty())) {
                if (!this.actionBar.isSearchFieldVisible()) {
                    this.proxyItem.setVisibility(0);
                }
                ProxyDrawable proxyDrawable2 = this.proxyDrawable;
                int i = this.currentConnectionState;
                if (i == 3 || i == 5) {
                    z2 = true;
                }
                proxyDrawable2.setConnected(z3, z2, z);
                this.proxyItemVisisble = true;
                return;
            }
            this.proxyItem.setVisibility(8);
            this.proxyItemVisisble = false;
        }
    }

    /* access modifiers changed from: private */
    public void updateSelectedCount() {
        if (this.commentView != null) {
            if (!this.dialogsAdapter.hasSelectedDialogs()) {
                if (this.dialogsType == 3 && this.selectAlertString == null) {
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
                    this.listView.requestLayout();
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
            this.actionBar.setTitle(LocaleController.formatPluralString("Recipient", this.dialogsAdapter.getSelectedDialogs().size()));
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
                            DialogsActivity.this.lambda$askForPermissons$19$DialogsActivity(i);
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
            }
        }
    }

    public /* synthetic */ void lambda$askForPermissons$19$DialogsActivity(int i) {
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
                    char c = 65535;
                    int hashCode = str.hashCode();
                    if (hashCode != NUM) {
                        if (hashCode == NUM && str.equals("android.permission.READ_CONTACTS")) {
                            c = 0;
                        }
                    } else if (str.equals("android.permission.WRITE_EXTERNAL_STORAGE")) {
                        c = 1;
                    }
                    if (c != 0) {
                        if (c == 1 && iArr[i2] == 0) {
                            ImageLoader.getInstance().checkMediaPaths();
                        }
                    } else if (iArr[i2] == 0) {
                        getContactsController().forceImportContacts();
                    } else {
                        SharedPreferences.Editor edit = MessagesController.getGlobalNotificationsSettings().edit();
                        this.askAboutContacts = false;
                        edit.putBoolean("askAboutContacts", false).commit();
                    }
                }
            }
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        int i3;
        DialogsAdapter dialogsAdapter2;
        if (i == NotificationCenter.dialogsNeedReload) {
            if (!this.dialogsListFrozen) {
                DialogsAdapter dialogsAdapter3 = this.dialogsAdapter;
                if (dialogsAdapter3 != null) {
                    if (dialogsAdapter3.isDataSetChanged() || objArr.length > 0) {
                        this.dialogsAdapter.notifyDataSetChanged();
                    } else {
                        updateVisibleRows(2048);
                    }
                }
                DialogsRecyclerView dialogsRecyclerView = this.listView;
                if (dialogsRecyclerView != null) {
                    try {
                        dialogsRecyclerView.setEmptyView(this.folderId == 0 ? this.progressView : null);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
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
            Integer num = objArr[0];
            updateVisibleRows(num.intValue());
            if ((num.intValue() & 4) != 0 && (dialogsAdapter2 = this.dialogsAdapter) != null) {
                dialogsAdapter2.sortOnlineContacts(true);
            }
        } else if (i == NotificationCenter.appDidLogout) {
            dialogsLoaded[this.currentAccount] = false;
        } else if (i == NotificationCenter.encryptedChatUpdated) {
            updateVisibleRows(0);
        } else if (i == NotificationCenter.contactsDidLoad) {
            if (!this.dialogsListFrozen) {
                if (this.dialogsType != 0 || !getMessagesController().getDialogs(this.folderId).isEmpty()) {
                    updateVisibleRows(0);
                    return;
                }
                DialogsAdapter dialogsAdapter4 = this.dialogsAdapter;
                if (dialogsAdapter4 != null) {
                    dialogsAdapter4.notifyDataSetChanged();
                }
            }
        } else if (i == NotificationCenter.openedChatChanged) {
            if (this.dialogsType == 0 && AndroidUtilities.isTablet()) {
                boolean booleanValue = objArr[1].booleanValue();
                long longValue = objArr[0].longValue();
                if (!booleanValue) {
                    this.openedDialogId = longValue;
                } else if (longValue == this.openedDialogId) {
                    this.openedDialogId = 0;
                }
                DialogsAdapter dialogsAdapter5 = this.dialogsAdapter;
                if (dialogsAdapter5 != null) {
                    dialogsAdapter5.setOpenedDialogId(this.openedDialogId);
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
            DialogsSearchAdapter dialogsSearchAdapter2 = this.dialogsSearchAdapter;
            if (dialogsSearchAdapter2 != null) {
                dialogsSearchAdapter2.loadRecentSearch();
            }
        } else if (i == NotificationCenter.replyMessagesDidLoad) {
            updateVisibleRows(32768);
        } else if (i == NotificationCenter.reloadHints) {
            DialogsSearchAdapter dialogsSearchAdapter3 = this.dialogsSearchAdapter;
            if (dialogsSearchAdapter3 != null) {
                dialogsSearchAdapter3.notifyDataSetChanged();
            }
        } else if (i == NotificationCenter.didUpdateConnectionState) {
            int connectionState = AccountInstance.getInstance(i2).getConnectionsManager().getConnectionState();
            if (this.currentConnectionState != connectionState) {
                this.currentConnectionState = connectionState;
                updateProxyButton(true);
            }
        } else if (i != NotificationCenter.dialogsUnreadCounterChanged) {
            if (i == NotificationCenter.needDeleteDialog) {
                if (this.fragmentView != null && !this.isPaused) {
                    long longValue2 = objArr[0].longValue();
                    TLRPC.User user = objArr[1];
                    $$Lambda$DialogsActivity$fZUyn9euSpFZbUpzei0wVegAb9g r3 = new Runnable(objArr[2], longValue2, objArr[3].booleanValue()) {
                        private final /* synthetic */ TLRPC.Chat f$1;
                        private final /* synthetic */ long f$2;
                        private final /* synthetic */ boolean f$3;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r5;
                        }

                        public final void run() {
                            DialogsActivity.this.lambda$didReceivedNotification$20$DialogsActivity(this.f$1, this.f$2, this.f$3);
                        }
                    };
                    if (this.undoView[0] != null) {
                        getUndoView().showWithAction(longValue2, 1, (Runnable) r3);
                    } else {
                        r3.run();
                    }
                }
            } else if (i == NotificationCenter.folderBecomeEmpty && (i3 = this.folderId) == objArr[0].intValue() && i3 != 0) {
                finishFragment();
            }
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$20$DialogsActivity(TLRPC.Chat chat, long j, boolean z) {
        if (chat == null) {
            getMessagesController().deleteDialog(j, 0, z);
        } else if (ChatObject.isNotInChat(chat)) {
            getMessagesController().deleteDialog(j, 0, z);
        } else {
            getMessagesController().deleteUserFromChat((int) (-j), getMessagesController().getUser(Integer.valueOf(getUserConfig().getClientUserId())), (TLRPC.ChatFull) null, false, z);
        }
        MessagesController.getInstance(this.currentAccount).checkIfFolderEmpty(this.folderId);
    }

    /* access modifiers changed from: private */
    public void setDialogsListFrozen(boolean z) {
        if (this.dialogsListFrozen != z) {
            if (z) {
                frozenDialogsList = new ArrayList<>(getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, false));
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

    public static ArrayList<TLRPC.Dialog> getDialogsArray(int i, int i2, int i3, boolean z) {
        ArrayList<TLRPC.Dialog> arrayList;
        if (z && (arrayList = frozenDialogsList) != null) {
            return arrayList;
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
        if (i2 == 6) {
            return messagesController.dialogsGroupsOnly;
        }
        return null;
    }

    public void setSideMenu(RecyclerView recyclerView) {
        this.sideMenu = recyclerView;
        this.sideMenu.setBackgroundColor(Theme.getColor("chats_menuBackground"));
        this.sideMenu.setGlowColor(Theme.getColor("chats_menuBackground"));
    }

    /* access modifiers changed from: private */
    public void updatePasscodeButton() {
        if (this.passcodeItem != null) {
            if (SharedConfig.passcodeHash.length() == 0 || this.searching) {
                this.passcodeItem.setVisibility(8);
                return;
            }
            this.passcodeItem.setVisibility(0);
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
                    DialogsActivity.this.lambda$hideFloatingButton$21$DialogsActivity(valueAnimator);
                }
            });
            animatorSet.playTogether(new Animator[]{ofFloat});
            animatorSet.setDuration(300);
            animatorSet.setInterpolator(this.floatingInterpolator);
            this.floatingButtonContainer.setClickable(!z);
            animatorSet.start();
        }
    }

    public /* synthetic */ void lambda$hideFloatingButton$21$DialogsActivity(ValueAnimator valueAnimator) {
        this.floatingButtonHideProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.floatingButtonTranslation = ((float) AndroidUtilities.dp(100.0f)) * this.floatingButtonHideProgress;
        updateFloatingButtonOffset();
    }

    /* access modifiers changed from: private */
    public void updateDialogIndices() {
        int indexOf;
        DialogsRecyclerView dialogsRecyclerView = this.listView;
        if (dialogsRecyclerView != null && dialogsRecyclerView.getAdapter() == this.dialogsAdapter) {
            ArrayList<TLRPC.Dialog> dialogsArray = getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, false);
            int childCount = this.listView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof DialogCell) {
                    DialogCell dialogCell = (DialogCell) childAt;
                    TLRPC.Dialog dialog = getMessagesController().dialogs_dict.get(dialogCell.getDialogId());
                    if (dialog != null && (indexOf = dialogsArray.indexOf(dialog)) >= 0) {
                        dialogCell.setDialogIndex(indexOf);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateVisibleRows(int i) {
        if (this.listView != null && !this.dialogsListFrozen) {
            int i2 = 0;
            while (i2 < 2) {
                RecyclerView recyclerView = i2 == 0 ? this.listView : this.searchListView;
                int childCount = recyclerView.getChildCount();
                for (int i3 = 0; i3 < childCount; i3++) {
                    View childAt = recyclerView.getChildAt(i3);
                    if (childAt instanceof DialogCell) {
                        if (recyclerView.getAdapter() != this.dialogsSearchAdapter) {
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
                                ArrayList<Long> selectedDialogs = this.dialogsAdapter.getSelectedDialogs();
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
                        for (int i4 = 0; i4 < childCount2; i4++) {
                            View childAt2 = recyclerListView.getChildAt(i4);
                            if (childAt2 instanceof HintDialogCell) {
                                ((HintDialogCell) childAt2).update(i);
                            }
                        }
                    }
                }
                i2++;
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
                TLRPC.Chat chat = getMessagesController().getChat(Integer.valueOf(i2));
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
                TLRPC.User user = getMessagesController().getUser(Integer.valueOf(getMessagesController().getEncryptedChat(Integer.valueOf(i4)).user_id));
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
                    private final /* synthetic */ long f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        DialogsActivity.this.lambda$didSelectResult$22$DialogsActivity(this.f$1, dialogInterface, i);
                    }
                });
                builder3.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                showDialog(builder3.create());
            } else if (i3 > 0) {
                TLRPC.User user2 = getMessagesController().getUser(Integer.valueOf(i3));
                if (user2 != null && this.selectAlertString != null) {
                    str5 = LocaleController.getString("SendMessageTitle", NUM);
                    str4 = LocaleController.formatStringSimple(this.selectAlertString, UserObject.getUserName(user2));
                    str2 = LocaleController.getString("Send", NUM);
                } else {
                    return;
                }
            } else {
                TLRPC.Chat chat2 = getMessagesController().getChat(Integer.valueOf(-i3));
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
                private final /* synthetic */ long f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    DialogsActivity.this.lambda$didSelectResult$22$DialogsActivity(this.f$1, dialogInterface, i);
                }
            });
            builder3.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            showDialog(builder3.create());
        }
    }

    public /* synthetic */ void lambda$didSelectResult$22$DialogsActivity(long j, DialogInterface dialogInterface, int i) {
        didSelectResult(j, false, false);
    }

    public ThemeDescription[] getThemeDescriptions() {
        $$Lambda$DialogsActivity$mvzIaF_qlcwu3LJwuuqy4Fz3zUg r10 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                DialogsActivity.this.lambda$getThemeDescriptions$23$DialogsActivity();
            }
        };
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        DialogCell dialogCell = this.movingView;
        if (dialogCell != null) {
            arrayList.add(new ThemeDescription(dialogCell, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        }
        if (this.folderId == 0) {
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
            arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
            arrayList.add(new ThemeDescription(this.searchListView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, new Drawable[]{Theme.dialogs_holidayDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSearch"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSearchPlaceholder"));
        } else {
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultArchived"));
            arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultArchived"));
            arrayList.add(new ThemeDescription(this.searchListView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultArchived"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultArchivedIcon"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, new Drawable[]{Theme.dialogs_holidayDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultArchivedTitle"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultArchivedSelector"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultArchivedSearch"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSearchArchivedPlaceholder"));
        }
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarActionModeDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarActionModeDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_TOPBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarActionModeDefaultTop"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarActionModeDefaultSelector"));
        arrayList.add(new ThemeDescription(this.selectedDialogsCountTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarActionModeDefaultIcon"));
        $$Lambda$DialogsActivity$mvzIaF_qlcwu3LJwuuqy4Fz3zUg r7 = r10;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "actionBarDefaultSubmenuBackground"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "actionBarDefaultSubmenuItem"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "actionBarDefaultSubmenuItemIcon"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "dialogButtonSelector"));
        arrayList.add(new ThemeDescription(this.searchEmptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "emptyListPlaceholder"));
        arrayList.add(new ThemeDescription(this.searchEmptyView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
        arrayList.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionIcon"));
        arrayList.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionBackground"));
        arrayList.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionPressedBackground"));
        int i = 0;
        while (i < 2) {
            View view = i == 0 ? this.listView : this.searchListView;
            arrayList.add(new ThemeDescription(view, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
            arrayList.add(new ThemeDescription(view, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
            arrayList.add(new ThemeDescription(view, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.avatar_savedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
            arrayList.add(new ThemeDescription(view, 0, new Class[]{DialogCell.class}, Theme.dialogs_countPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_unreadCounter"));
            arrayList.add(new ThemeDescription(view, 0, new Class[]{DialogCell.class}, Theme.dialogs_countGrayPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_unreadCounterMuted"));
            arrayList.add(new ThemeDescription(view, 0, new Class[]{DialogCell.class}, Theme.dialogs_countTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_unreadCounterText"));
            arrayList.add(new ThemeDescription(view, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_lockDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_secretIcon"));
            arrayList.add(new ThemeDescription(view, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_groupDrawable, Theme.dialogs_broadcastDrawable, Theme.dialogs_botDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_nameIcon"));
            arrayList.add(new ThemeDescription(view, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_scamDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_draft"));
            arrayList.add(new ThemeDescription(view, 0, new Class[]{DialogCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_pinnedDrawable, Theme.dialogs_reorderDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_pinnedIcon"));
            TextPaint[] textPaintArr = Theme.dialogs_namePaint;
            arrayList.add(new ThemeDescription(view, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (String[]) null, new Paint[]{textPaintArr[0], textPaintArr[1], Theme.dialogs_searchNamePaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_name"));
            TextPaint[] textPaintArr2 = Theme.dialogs_nameEncryptedPaint;
            arrayList.add(new ThemeDescription(view, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (String[]) null, new Paint[]{textPaintArr2[0], textPaintArr2[1], Theme.dialogs_searchNameEncryptedPaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_secretName"));
            arrayList.add(new ThemeDescription(view, 0, new Class[]{DialogCell.class}, Theme.dialogs_messagePaint[1], (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_message_threeLines"));
            arrayList.add(new ThemeDescription(view, 0, new Class[]{DialogCell.class}, Theme.dialogs_messagePaint[0], (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_message"));
            arrayList.add(new ThemeDescription(view, 0, new Class[]{DialogCell.class}, Theme.dialogs_messageNamePaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_nameMessage_threeLines"));
            arrayList.add(new ThemeDescription(view, 0, new Class[]{DialogCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_draft"));
            arrayList.add(new ThemeDescription(view, 0, new Class[]{DialogCell.class}, (String[]) null, (Paint[]) Theme.dialogs_messagePrintingPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionMessage"));
            arrayList.add(new ThemeDescription(view, 0, new Class[]{DialogCell.class}, Theme.dialogs_timePaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_date"));
            arrayList.add(new ThemeDescription(view, 0, new Class[]{DialogCell.class}, Theme.dialogs_pinnedPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_pinnedOverlay"));
            arrayList.add(new ThemeDescription(view, 0, new Class[]{DialogCell.class}, Theme.dialogs_tabletSeletedPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_tabletSelectedOverlay"));
            arrayList.add(new ThemeDescription(view, 0, new Class[]{DialogCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_checkDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_sentCheck"));
            arrayList.add(new ThemeDescription(view, 0, new Class[]{DialogCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_checkReadDrawable, Theme.dialogs_halfCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_sentReadCheck"));
            arrayList.add(new ThemeDescription(view, 0, new Class[]{DialogCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_clockDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_sentClock"));
            arrayList.add(new ThemeDescription(view, 0, new Class[]{DialogCell.class}, Theme.dialogs_errorPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_sentError"));
            arrayList.add(new ThemeDescription(view, 0, new Class[]{DialogCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_errorDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_sentErrorIcon"));
            arrayList.add(new ThemeDescription(view, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_verifiedCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_verifiedCheck"));
            arrayList.add(new ThemeDescription(view, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_verifiedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_verifiedBackground"));
            arrayList.add(new ThemeDescription(view, 0, new Class[]{DialogCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_muteDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_muteIcon"));
            arrayList.add(new ThemeDescription(view, 0, new Class[]{DialogCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_mentionDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_mentionIcon"));
            arrayList.add(new ThemeDescription(view, 0, new Class[]{DialogCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_archivePinBackground"));
            arrayList.add(new ThemeDescription(view, 0, new Class[]{DialogCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_archiveBackground"));
            arrayList.add(new ThemeDescription(view, 0, new Class[]{DialogCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_onlineCircle"));
            arrayList.add(new ThemeDescription(view, 0, new Class[]{DialogCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
            arrayList.add(new ThemeDescription(view, ThemeDescription.FLAG_CHECKBOX, new Class[]{DialogCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
            arrayList.add(new ThemeDescription(view, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{DialogCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxCheck"));
            arrayList.add(new ThemeDescription(view, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
            arrayList.add(new ThemeDescription(view, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_offlinePaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
            arrayList.add(new ThemeDescription(view, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_onlinePaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText3"));
            arrayList.add(new ThemeDescription(view, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_graySectionText"));
            arrayList.add(new ThemeDescription(view, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "graySection"));
            arrayList.add(new ThemeDescription(view, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{HashtagSearchCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription(view, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
            arrayList.add(new ThemeDescription(view, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
            arrayList.add(new ThemeDescription(view, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText2"));
            i++;
        }
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{DialogsEmptyCell.class}, new String[]{"emptyTextView1"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_nameMessage_threeLines"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{DialogsEmptyCell.class}, new String[]{"emptyTextView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_message"));
        $$Lambda$DialogsActivity$mvzIaF_qlcwu3LJwuuqy4Fz3zUg r72 = r10;
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
        if (SharedConfig.archiveHidden) {
            arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveAvatarDrawable}, "Arrow1", "avatar_backgroundArchivedHidden"));
            arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveAvatarDrawable}, "Arrow2", "avatar_backgroundArchivedHidden"));
        } else {
            arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveAvatarDrawable}, "Arrow1", "avatar_backgroundArchived"));
            arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveAvatarDrawable}, "Arrow2", "avatar_backgroundArchived"));
        }
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveAvatarDrawable}, "Box2", "avatar_text"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveAvatarDrawable}, "Box1", "avatar_text"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_pinArchiveDrawable}, "Arrow", "chats_archiveIcon"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_pinArchiveDrawable}, "Line", "chats_archiveIcon"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_unpinArchiveDrawable}, "Arrow", "chats_archiveIcon"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_unpinArchiveDrawable}, "Line", "chats_archiveIcon"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveDrawable}, "Arrow", "chats_archiveBackground"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveDrawable}, "Box2", "chats_archiveIcon"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_archiveDrawable}, "Box1", "chats_archiveIcon"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_unarchiveDrawable}, "Arrow1", "chats_archiveIcon"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_unarchiveDrawable}, "Arrow2", "chats_archivePinBackground"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_unarchiveDrawable}, "Box2", "chats_archiveIcon"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{DialogCell.class}, new RLottieDrawable[]{Theme.dialogs_unarchiveDrawable}, "Box1", "chats_archiveIcon"));
        $$Lambda$DialogsActivity$mvzIaF_qlcwu3LJwuuqy4Fz3zUg r73 = r10;
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
        arrayList.add(new ThemeDescription((View) this.sideMenu, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{DrawerProfileCell.class}, new String[]{"darkThemeView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
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
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        $$Lambda$DialogsActivity$mvzIaF_qlcwu3LJwuuqy4Fz3zUg r8 = r10;
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r8, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r8, "windowBackgroundWhiteBlueText"));
        arrayList.add(new ThemeDescription(this.sideMenu, 0, new Class[]{DividerCell.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription(this.progressView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
        DialogsAdapter dialogsAdapter2 = this.dialogsAdapter;
        RecyclerListView recyclerListView = null;
        arrayList.add(new ThemeDescription((View) dialogsAdapter2 != null ? dialogsAdapter2.getArchiveHintCellPager() : null, 0, new Class[]{ArchiveHintInnerCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_nameMessage_threeLines"));
        DialogsAdapter dialogsAdapter3 = this.dialogsAdapter;
        arrayList.add(new ThemeDescription((View) dialogsAdapter3 != null ? dialogsAdapter3.getArchiveHintCellPager() : null, 0, new Class[]{ArchiveHintInnerCell.class}, new String[]{"imageView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_unreadCounter"));
        DialogsAdapter dialogsAdapter4 = this.dialogsAdapter;
        arrayList.add(new ThemeDescription((View) dialogsAdapter4 != null ? dialogsAdapter4.getArchiveHintCellPager() : null, 0, new Class[]{ArchiveHintInnerCell.class}, new String[]{"headerTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_nameMessage_threeLines"));
        DialogsAdapter dialogsAdapter5 = this.dialogsAdapter;
        arrayList.add(new ThemeDescription((View) dialogsAdapter5 != null ? dialogsAdapter5.getArchiveHintCellPager() : null, 0, new Class[]{ArchiveHintInnerCell.class}, new String[]{"messageTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_message"));
        DialogsAdapter dialogsAdapter6 = this.dialogsAdapter;
        arrayList.add(new ThemeDescription(dialogsAdapter6 != null ? dialogsAdapter6.getArchiveHintCellPager() : null, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultArchived"));
        DialogsSearchAdapter dialogsSearchAdapter2 = this.dialogsSearchAdapter;
        arrayList.add(new ThemeDescription(dialogsSearchAdapter2 != null ? dialogsSearchAdapter2.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, Theme.dialogs_countPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_unreadCounter"));
        DialogsSearchAdapter dialogsSearchAdapter3 = this.dialogsSearchAdapter;
        arrayList.add(new ThemeDescription(dialogsSearchAdapter3 != null ? dialogsSearchAdapter3.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, Theme.dialogs_countGrayPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_unreadCounterMuted"));
        DialogsSearchAdapter dialogsSearchAdapter4 = this.dialogsSearchAdapter;
        arrayList.add(new ThemeDescription(dialogsSearchAdapter4 != null ? dialogsSearchAdapter4.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, Theme.dialogs_countTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_unreadCounterText"));
        DialogsSearchAdapter dialogsSearchAdapter5 = this.dialogsSearchAdapter;
        arrayList.add(new ThemeDescription(dialogsSearchAdapter5 != null ? dialogsSearchAdapter5.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, Theme.dialogs_archiveTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_archiveText"));
        DialogsSearchAdapter dialogsSearchAdapter6 = this.dialogsSearchAdapter;
        arrayList.add(new ThemeDescription((View) dialogsSearchAdapter6 != null ? dialogsSearchAdapter6.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        DialogsSearchAdapter dialogsSearchAdapter7 = this.dialogsSearchAdapter;
        if (dialogsSearchAdapter7 != null) {
            recyclerListView = dialogsSearchAdapter7.getInnerListView();
        }
        arrayList.add(new ThemeDescription(recyclerListView, 0, new Class[]{HintDialogCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_onlineCircle"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "inappPlayerBackground"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{FragmentContextView.class}, new String[]{"playButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "inappPlayerPlayPause"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "inappPlayerTitle"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_FASTSCROLL, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "inappPlayerPerformer"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{FragmentContextView.class}, new String[]{"closeButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "inappPlayerClose"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "returnToCallBackground"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "returnToCallText"));
        int i2 = 0;
        while (true) {
            UndoView[] undoViewArr = this.undoView;
            if (i2 < undoViewArr.length) {
                arrayList.add(new ThemeDescription(undoViewArr[i2], ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_background"));
                arrayList.add(new ThemeDescription((View) this.undoView[i2], 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
                arrayList.add(new ThemeDescription((View) this.undoView[i2], 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
                arrayList.add(new ThemeDescription((View) this.undoView[i2], 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
                arrayList.add(new ThemeDescription((View) this.undoView[i2], 0, new Class[]{UndoView.class}, new String[]{"subinfoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
                arrayList.add(new ThemeDescription((View) this.undoView[i2], 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
                arrayList.add(new ThemeDescription((View) this.undoView[i2], 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
                arrayList.add(new ThemeDescription((View) this.undoView[i2], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "info1", "undo_background"));
                arrayList.add(new ThemeDescription((View) this.undoView[i2], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "info2", "undo_background"));
                arrayList.add(new ThemeDescription((View) this.undoView[i2], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luCLASSNAME", "undo_infoColor"));
                arrayList.add(new ThemeDescription((View) this.undoView[i2], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luCLASSNAME", "undo_infoColor"));
                arrayList.add(new ThemeDescription((View) this.undoView[i2], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luCLASSNAME", "undo_infoColor"));
                arrayList.add(new ThemeDescription((View) this.undoView[i2], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc9", "undo_infoColor"));
                arrayList.add(new ThemeDescription((View) this.undoView[i2], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc8", "undo_infoColor"));
                arrayList.add(new ThemeDescription((View) this.undoView[i2], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc7", "undo_infoColor"));
                arrayList.add(new ThemeDescription((View) this.undoView[i2], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc6", "undo_infoColor"));
                arrayList.add(new ThemeDescription((View) this.undoView[i2], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc5", "undo_infoColor"));
                arrayList.add(new ThemeDescription((View) this.undoView[i2], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc4", "undo_infoColor"));
                arrayList.add(new ThemeDescription((View) this.undoView[i2], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc3", "undo_infoColor"));
                arrayList.add(new ThemeDescription((View) this.undoView[i2], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc2", "undo_infoColor"));
                arrayList.add(new ThemeDescription((View) this.undoView[i2], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "luc1", "undo_infoColor"));
                arrayList.add(new ThemeDescription((View) this.undoView[i2], 0, new Class[]{UndoView.class}, new String[]{"leftImageView"}, "Oval", "undo_infoColor"));
                i2++;
            } else {
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
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_placeholder"));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_placeholderBackground"));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_button"));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_buttonActive"));
                return (ThemeDescription[]) arrayList.toArray(new ThemeDescription[0]);
            }
        }
    }

    public /* synthetic */ void lambda$getThemeDescriptions$23$DialogsActivity() {
        RecyclerListView innerListView;
        int i = 0;
        while (i < 2) {
            ViewGroup viewGroup = i == 0 ? this.listView : this.searchListView;
            if (viewGroup != null) {
                int childCount = viewGroup.getChildCount();
                for (int i2 = 0; i2 < childCount; i2++) {
                    View childAt = viewGroup.getChildAt(i2);
                    if (childAt instanceof ProfileSearchCell) {
                        ((ProfileSearchCell) childAt).update(0);
                    } else if (childAt instanceof DialogCell) {
                        ((DialogCell) childAt).update(0);
                    } else if (childAt instanceof UserCell) {
                        ((UserCell) childAt).update(0);
                    }
                }
            }
            i++;
        }
        DialogsSearchAdapter dialogsSearchAdapter2 = this.dialogsSearchAdapter;
        if (!(dialogsSearchAdapter2 == null || (innerListView = dialogsSearchAdapter2.getInnerListView()) == null)) {
            int childCount2 = innerListView.getChildCount();
            for (int i3 = 0; i3 < childCount2; i3++) {
                View childAt2 = innerListView.getChildAt(i3);
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
        PullForegroundDrawable pullForegroundDrawable2 = this.pullForegroundDrawable;
        if (pullForegroundDrawable2 != null) {
            pullForegroundDrawable2.updateColors();
        }
        ActionBar actionBar = this.actionBar;
        if (actionBar != null) {
            actionBar.setPopupBackgroundColor(Theme.getColor("actionBarDefaultSubmenuBackground"), true);
            this.actionBar.setPopupItemsColor(Theme.getColor("actionBarDefaultSubmenuItem"), false, true);
            this.actionBar.setPopupItemsColor(Theme.getColor("actionBarDefaultSubmenuItemIcon"), true, true);
            this.actionBar.setPopupItemsSelectorColor(Theme.getColor("dialogButtonSelector"), true);
        }
    }
}
