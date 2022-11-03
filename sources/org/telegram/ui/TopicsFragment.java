package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.collection.LongSparseArray;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.util.ObjectsCompat$$ExternalSyntheticBackport0;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.TopicsController;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$ChatParticipants;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$TL_account_getNotifyExceptions;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_forumTopic;
import org.telegram.tgnet.TLRPC$TL_groupCall;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterEmpty;
import org.telegram.tgnet.TLRPC$TL_inputNotifyPeer;
import org.telegram.tgnet.TLRPC$TL_messages_search;
import org.telegram.tgnet.TLRPC$TL_updates;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$messages_Messages;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.FiltersView;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.TopicSearchCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ChatAvatarContainer;
import org.telegram.ui.Components.ChatNotificationsPopupWrapper;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.Forum.ForumUtilities;
import org.telegram.ui.Components.FragmentContextView;
import org.telegram.ui.Components.InviteMembersBottomSheet;
import org.telegram.ui.Components.JoinGroupAlert;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerItemsEnterAnimator;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StickerEmptyView;
import org.telegram.ui.Components.UnreadCounterTextView;
import org.telegram.ui.Delegates.ChatActivityMemberRequestsDelegate;
import org.telegram.ui.GroupCreateActivity;
import org.telegram.ui.TopicsFragment;
/* loaded from: classes3.dex */
public class TopicsFragment extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, FragmentContextView.ChatActivityInterface {
    private static HashSet<Long> settingsPreloaded = new HashSet<>();
    Adapter adapter;
    private ActionBarMenuSubItem addMemberSubMenu;
    boolean animateSearchWithScale;
    ChatAvatarContainer avatarContainer;
    private View blurredView;
    private UnreadCounterTextView bottomOverlayChatText;
    private FrameLayout bottomOverlayContainer;
    private RadialProgressView bottomOverlayProgress;
    private boolean bottomPannelVisible;
    boolean canShowCreateTopic;
    TLRPC$ChatFull chatFull;
    final long chatId;
    private ActionBarMenuSubItem closeTopic;
    SizeNotifierFrameLayout contentView;
    private ActionBarMenuSubItem createTopicSubmenu;
    private ActionBarMenuSubItem deleteChatSubmenu;
    private ActionBarMenuItem deleteItem;
    DialogsActivity dialogsActivity;
    HashSet<Integer> excludeTopics;
    FrameLayout floatingButtonContainer;
    private float floatingButtonHideProgress;
    private float floatingButtonTranslation;
    private boolean floatingHidden;
    private final AccelerateDecelerateInterpolator floatingInterpolator;
    ArrayList<TLRPC$TL_forumTopic> forumTopics;
    FragmentContextView fragmentContextView;
    private ChatObject.Call groupCall;
    boolean isSlideBackTransition;
    private DefaultItemAnimator itemAnimator;
    RecyclerItemsEnterAnimator itemsEnterAnimator;
    private long lastAnimatedDuration;
    LinearLayoutManager layoutManager;
    private boolean loadingTopics;
    private boolean mute;
    private ActionBarMenuItem muteItem;
    OnTopicSelectedListener onTopicSelectedListener;
    private boolean openedForForward;
    private boolean opnendForSelect;
    private ActionBarMenuItem other;
    ActionBarMenuItem otherItem;
    private ChatActivityMemberRequestsDelegate pendingRequestsDelegate;
    private ActionBarMenuItem pinItem;
    private ActionBarMenuSubItem readItem;
    private RecyclerListView recyclerListView;
    private boolean removeFragmentOnTransitionEnd;
    private ActionBarMenuSubItem restartTopic;
    private boolean scrollToTop;
    private float searchAnimationProgress;
    ValueAnimator searchAnimator;
    private SearchContainer searchContainer;
    private ActionBarMenuItem searchItem;
    private boolean searching;
    private boolean searchingNotEmpty;
    private NumberTextView selectedDialogsCountTextView;
    HashSet<Integer> selectedTopics;
    ValueAnimator slideBackTransitionAnimator;
    float slideFragmentProgress;
    ChatActivity.ThemeDelegate themeDelegate;
    private FrameLayout topView;
    private final TopicsController topicsController;
    StickerEmptyView topicsEmptyView;
    private int transitionAnimationGlobalIndex;
    private int transitionAnimationIndex;
    private ActionBarMenuItem unpinItem;
    private boolean updateAnimated;

    /* loaded from: classes3.dex */
    public interface OnTopicSelectedListener {
        void onTopicSelected(TLRPC$TL_forumTopic tLRPC$TL_forumTopic);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$chekActionMode$13(View view, MotionEvent motionEvent) {
        return true;
    }

    @Override // org.telegram.ui.Components.FragmentContextView.ChatActivityInterface
    public /* synthetic */ TLRPC$User getCurrentUser() {
        return FragmentContextView.ChatActivityInterface.CC.$default$getCurrentUser(this);
    }

    @Override // org.telegram.ui.Components.FragmentContextView.ChatActivityInterface
    public /* synthetic */ long getMergeDialogId() {
        return FragmentContextView.ChatActivityInterface.CC.$default$getMergeDialogId(this);
    }

    @Override // org.telegram.ui.Components.FragmentContextView.ChatActivityInterface
    public /* synthetic */ int getTopicId() {
        return FragmentContextView.ChatActivityInterface.CC.$default$getTopicId(this);
    }

    @Override // org.telegram.ui.Components.FragmentContextView.ChatActivityInterface
    public /* synthetic */ boolean openedWithLivestream() {
        return FragmentContextView.ChatActivityInterface.CC.$default$openedWithLivestream(this);
    }

    @Override // org.telegram.ui.Components.FragmentContextView.ChatActivityInterface
    public /* synthetic */ void scrollToMessageId(int i, int i2, boolean z, int i3, boolean z2, int i4) {
        FragmentContextView.ChatActivityInterface.CC.$default$scrollToMessageId(this, i, i2, z, i3, z2, i4);
    }

    @Override // org.telegram.ui.Components.FragmentContextView.ChatActivityInterface
    public /* synthetic */ boolean shouldShowImport() {
        return FragmentContextView.ChatActivityInterface.CC.$default$shouldShowImport(this);
    }

    public TopicsFragment(Bundle bundle) {
        super(bundle);
        this.forumTopics = new ArrayList<>();
        this.adapter = new Adapter();
        this.floatingHidden = false;
        this.floatingInterpolator = new AccelerateDecelerateInterpolator();
        this.bottomPannelVisible = true;
        this.searchAnimationProgress = 0.0f;
        this.selectedTopics = new HashSet<>();
        this.mute = false;
        this.slideFragmentProgress = 1.0f;
        this.chatId = this.arguments.getLong("chat_id", 0L);
        this.opnendForSelect = this.arguments.getBoolean("for_select", false);
        this.openedForForward = this.arguments.getBoolean("forward_to", false);
        this.topicsController = getMessagesController().getTopicsController();
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x0042  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static void prepareToSwitchAnimation(org.telegram.ui.ChatActivity r6) {
        /*
            org.telegram.ui.ActionBar.INavigationLayout r0 = r6.getParentLayout()
            java.util.List r0 = r0.getFragmentStack()
            int r0 = r0.size()
            r1 = 1
            if (r0 > r1) goto L11
        Lf:
            r0 = 1
            goto L40
        L11:
            org.telegram.ui.ActionBar.INavigationLayout r0 = r6.getParentLayout()
            java.util.List r0 = r0.getFragmentStack()
            org.telegram.ui.ActionBar.INavigationLayout r2 = r6.getParentLayout()
            java.util.List r2 = r2.getFragmentStack()
            int r2 = r2.size()
            int r2 = r2 + (-2)
            java.lang.Object r0 = r0.get(r2)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            boolean r2 = r0 instanceof org.telegram.ui.TopicsFragment
            if (r2 == 0) goto Lf
            org.telegram.ui.TopicsFragment r0 = (org.telegram.ui.TopicsFragment) r0
            long r2 = r0.chatId
            long r4 = r6.getDialogId()
            long r4 = -r4
            int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r0 == 0) goto L3f
            goto Lf
        L3f:
            r0 = 0
        L40:
            if (r0 == 0) goto L6a
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            long r2 = r6.getDialogId()
            long r2 = -r2
            java.lang.String r4 = "chat_id"
            r0.putLong(r4, r2)
            org.telegram.ui.TopicsFragment r2 = new org.telegram.ui.TopicsFragment
            r2.<init>(r0)
            org.telegram.ui.ActionBar.INavigationLayout r0 = r6.getParentLayout()
            org.telegram.ui.ActionBar.INavigationLayout r3 = r6.getParentLayout()
            java.util.List r3 = r3.getFragmentStack()
            int r3 = r3.size()
            int r3 = r3 - r1
            r0.addFragmentToStack(r2, r3)
        L6a:
            r6.setSwitchFromTopics(r1)
            r6.finishFragment()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.TopicsFragment.prepareToSwitchAnimation(org.telegram.ui.ChatActivity):void");
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        int i;
        SizeNotifierFrameLayout sizeNotifierFrameLayout = new SizeNotifierFrameLayout(context) { // from class: org.telegram.ui.TopicsFragment.1
            {
                setWillNotDraw(false);
            }

            @Override // android.view.View
            public void draw(Canvas canvas) {
                super.draw(canvas);
                TopicsFragment.this.getParentLayout().drawHeaderShadow(canvas, (int) (((BaseFragment) TopicsFragment.this).actionBar.getY() + (((BaseFragment) TopicsFragment.this).actionBar.getHeight() * ((BaseFragment) TopicsFragment.this).actionBar.getScaleY())));
            }

            @Override // android.widget.FrameLayout, android.view.View
            protected void onMeasure(int i2, int i3) {
                int size = View.MeasureSpec.getSize(i2);
                int size2 = View.MeasureSpec.getSize(i3);
                int i4 = 0;
                for (int i5 = 0; i5 < getChildCount(); i5++) {
                    View childAt = getChildAt(i5);
                    if (childAt instanceof ActionBar) {
                        childAt.measure(i2, View.MeasureSpec.makeMeasureSpec(0, 0));
                        i4 = childAt.getMeasuredHeight();
                    }
                }
                for (int i6 = 0; i6 < getChildCount(); i6++) {
                    View childAt2 = getChildAt(i6);
                    if (!(childAt2 instanceof ActionBar)) {
                        if (childAt2.getFitsSystemWindows()) {
                            measureChildWithMargins(childAt2, i2, 0, i3, 0);
                        } else {
                            measureChildWithMargins(childAt2, i2, 0, i3, i4);
                        }
                    }
                }
                setMeasuredDimension(size, size2);
            }

            /* JADX WARN: Removed duplicated region for block: B:23:0x006f  */
            /* JADX WARN: Removed duplicated region for block: B:32:0x00bd  */
            @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct add '--show-bad-code' argument
            */
            protected void onLayout(boolean r11, int r12, int r13, int r14, int r15) {
                /*
                    r10 = this;
                    int r11 = r10.getChildCount()
                    int r0 = r10.getPaddingLeft()
                    int r14 = r14 - r12
                    int r12 = r10.getPaddingRight()
                    int r14 = r14 - r12
                    int r12 = r10.getPaddingTop()
                    int r15 = r15 - r13
                    int r13 = r10.getPaddingBottom()
                    int r15 = r15 - r13
                    r13 = 0
                    r1 = 0
                L1a:
                    if (r1 >= r11) goto Ld3
                    android.view.View r2 = r10.getChildAt(r1)
                    int r3 = r2.getVisibility()
                    r4 = 8
                    if (r3 == r4) goto Lcf
                    android.view.ViewGroup$LayoutParams r3 = r2.getLayoutParams()
                    android.widget.FrameLayout$LayoutParams r3 = (android.widget.FrameLayout.LayoutParams) r3
                    int r4 = r2.getMeasuredWidth()
                    int r5 = r2.getMeasuredHeight()
                    int r6 = r3.gravity
                    r7 = -1
                    if (r6 != r7) goto L3c
                    r6 = 0
                L3c:
                    int r7 = android.os.Build.VERSION.SDK_INT
                    r8 = 17
                    if (r7 < r8) goto L47
                    int r7 = r10.getLayoutDirection()
                    goto L48
                L47:
                    r7 = 0
                L48:
                    int r7 = android.view.Gravity.getAbsoluteGravity(r6, r7)
                    r6 = r6 & 112(0x70, float:1.57E-43)
                    r7 = r7 & 7
                    r8 = 1
                    if (r7 == r8) goto L5f
                    r8 = 5
                    if (r7 == r8) goto L5a
                    int r7 = r3.leftMargin
                    int r7 = r7 + r0
                    goto L6b
                L5a:
                    int r7 = r14 - r4
                    int r8 = r3.rightMargin
                    goto L6a
                L5f:
                    int r7 = r14 - r0
                    int r7 = r7 - r4
                    int r7 = r7 / 2
                    int r7 = r7 + r0
                    int r8 = r3.leftMargin
                    int r7 = r7 + r8
                    int r8 = r3.rightMargin
                L6a:
                    int r7 = r7 - r8
                L6b:
                    r8 = 16
                    if (r6 == r8) goto Lbd
                    r8 = 80
                    if (r6 == r8) goto Lb8
                    int r3 = r3.topMargin
                    int r3 = r3 + r12
                    org.telegram.ui.TopicsFragment r6 = org.telegram.ui.TopicsFragment.this
                    android.widget.FrameLayout r6 = org.telegram.ui.TopicsFragment.access$400(r6)
                    if (r2 != r6) goto L9d
                    org.telegram.ui.TopicsFragment r6 = org.telegram.ui.TopicsFragment.this
                    android.widget.FrameLayout r6 = org.telegram.ui.TopicsFragment.access$400(r6)
                    org.telegram.ui.TopicsFragment r8 = org.telegram.ui.TopicsFragment.this
                    org.telegram.ui.ActionBar.ActionBar r8 = org.telegram.ui.TopicsFragment.access$500(r8)
                    int r8 = r8.getTop()
                    org.telegram.ui.TopicsFragment r9 = org.telegram.ui.TopicsFragment.this
                    org.telegram.ui.ActionBar.ActionBar r9 = org.telegram.ui.TopicsFragment.access$600(r9)
                    int r9 = r9.getMeasuredHeight()
                    int r8 = r8 + r9
                    r6.setPadding(r13, r8, r13, r13)
                    goto Lca
                L9d:
                    boolean r6 = r2 instanceof org.telegram.ui.ActionBar.ActionBar
                    if (r6 != 0) goto Lca
                    org.telegram.ui.TopicsFragment r6 = org.telegram.ui.TopicsFragment.this
                    org.telegram.ui.ActionBar.ActionBar r6 = org.telegram.ui.TopicsFragment.access$700(r6)
                    int r6 = r6.getTop()
                    org.telegram.ui.TopicsFragment r8 = org.telegram.ui.TopicsFragment.this
                    org.telegram.ui.ActionBar.ActionBar r8 = org.telegram.ui.TopicsFragment.access$800(r8)
                    int r8 = r8.getMeasuredHeight()
                    int r6 = r6 + r8
                    int r3 = r3 + r6
                    goto Lca
                Lb8:
                    int r6 = r15 - r5
                    int r3 = r3.bottomMargin
                    goto Lc8
                Lbd:
                    int r6 = r15 - r12
                    int r6 = r6 - r5
                    int r6 = r6 / 2
                    int r6 = r6 + r12
                    int r8 = r3.topMargin
                    int r6 = r6 + r8
                    int r3 = r3.bottomMargin
                Lc8:
                    int r3 = r6 - r3
                Lca:
                    int r4 = r4 + r7
                    int r5 = r5 + r3
                    r2.layout(r7, r3, r4, r5)
                Lcf:
                    int r1 = r1 + 1
                    goto L1a
                Ld3:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.TopicsFragment.AnonymousClass1.onLayout(boolean, int, int, int, int):void");
            }

            @Override // org.telegram.ui.Components.SizeNotifierFrameLayout
            protected void drawList(Canvas canvas, boolean z) {
                for (int i2 = 0; i2 < TopicsFragment.this.recyclerListView.getChildCount(); i2++) {
                    View childAt = TopicsFragment.this.recyclerListView.getChildAt(i2);
                    if (childAt.getY() < AndroidUtilities.dp(100.0f) && childAt.getVisibility() == 0) {
                        int save = canvas.save();
                        canvas.translate(TopicsFragment.this.recyclerListView.getX() + childAt.getX(), getY() + TopicsFragment.this.recyclerListView.getY() + childAt.getY());
                        childAt.draw(canvas);
                        canvas.restoreToCount(save);
                    }
                }
            }
        };
        this.contentView = sizeNotifierFrameLayout;
        this.fragmentView = sizeNotifierFrameLayout;
        sizeNotifierFrameLayout.needBlur = true;
        this.actionBar.setAddToContainer(false);
        this.actionBar.setCastShadows(false);
        this.actionBar.setClipContent(true);
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setActionBarMenuOnItemClick(new AnonymousClass2(context));
        this.actionBar.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.TopicsFragment$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                TopicsFragment.this.lambda$createView$0(view);
            }
        });
        ActionBarMenu createMenu = this.actionBar.createMenu();
        ActionBarMenuItem actionBarMenuItemSearchListener = createMenu.addItem(0, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() { // from class: org.telegram.ui.TopicsFragment.3
            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchFilterCleared(FiltersView.MediaFilterData mediaFilterData) {
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchExpand() {
                TopicsFragment.this.animateToSearchView(true);
                TopicsFragment.this.searchContainer.setSearchString("");
                TopicsFragment.this.searchContainer.setAlpha(0.0f);
                TopicsFragment.this.searchContainer.emptyView.showProgress(true, false);
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchCollapse() {
                TopicsFragment.this.animateToSearchView(false);
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onTextChanged(EditText editText) {
                TopicsFragment.this.searchContainer.setSearchString(editText.getText().toString());
            }
        });
        this.searchItem = actionBarMenuItemSearchListener;
        actionBarMenuItemSearchListener.setSearchPaddingStart(56);
        this.searchItem.setSearchFieldHint(LocaleController.getString("Search", R.string.Search));
        ActionBarMenuItem addItem = createMenu.addItem(0, R.drawable.ic_ab_other, this.themeDelegate);
        this.other = addItem;
        addItem.addSubItem(1, R.drawable.msg_discussion, LocaleController.getString("TopicViewAsMessages", R.string.TopicViewAsMessages));
        this.addMemberSubMenu = this.other.addSubItem(2, R.drawable.msg_addcontact, LocaleController.getString("AddMember", R.string.AddMember));
        ActionBarMenuItem actionBarMenuItem = this.other;
        int i2 = R.drawable.msg_topic_create;
        int i3 = R.string.CreateTopic;
        int i4 = 3;
        this.createTopicSubmenu = actionBarMenuItem.addSubItem(3, i2, LocaleController.getString("CreateTopic", i3));
        this.deleteChatSubmenu = this.other.addSubItem(11, R.drawable.msg_leave, LocaleController.getString("LeaveMegaMenu", R.string.LeaveMegaMenu), this.themeDelegate);
        ChatAvatarContainer chatAvatarContainer = new ChatAvatarContainer(context, this, false);
        this.avatarContainer = chatAvatarContainer;
        chatAvatarContainer.getAvatarImageView().setRoundRadius(AndroidUtilities.dp(16.0f));
        this.avatarContainer.setOccupyStatusBar(!AndroidUtilities.isTablet());
        this.actionBar.addView(this.avatarContainer, 0, LayoutHelper.createFrame(-2, -1.0f, 51, !this.inPreviewMode ? 56.0f : 0.0f, 0.0f, 86.0f, 0.0f));
        this.recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.TopicsFragment.4
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
            public void onLayout(boolean z, int i5, int i6, int i7, int i8) {
                super.onLayout(z, i5, i6, i7, i8);
                TopicsFragment.this.checkForLoadMore();
            }
        };
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setSupportsChangeAnimations(false);
        defaultItemAnimator.setDelayAnimations(false);
        RecyclerListView recyclerListView = this.recyclerListView;
        this.itemAnimator = defaultItemAnimator;
        recyclerListView.setItemAnimator(defaultItemAnimator);
        this.recyclerListView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.TopicsFragment.5
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int i5, int i6) {
                super.onScrolled(recyclerView, i5, i6);
                TopicsFragment.this.checkForLoadMore();
            }
        });
        this.recyclerListView.setAnimateEmptyView(true, 0);
        RecyclerListView recyclerListView2 = this.recyclerListView;
        RecyclerItemsEnterAnimator recyclerItemsEnterAnimator = new RecyclerItemsEnterAnimator(recyclerListView2, true);
        this.itemsEnterAnimator = recyclerItemsEnterAnimator;
        recyclerListView2.setItemsEnterAnimator(recyclerItemsEnterAnimator);
        this.recyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.TopicsFragment$$ExternalSyntheticLambda15
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i5) {
                TopicsFragment.this.lambda$createView$1(view, i5);
            }
        });
        this.recyclerListView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListenerExtended() { // from class: org.telegram.ui.TopicsFragment$$ExternalSyntheticLambda16
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListenerExtended
            public final boolean onItemClick(View view, int i5, float f, float f2) {
                boolean lambda$createView$2;
                lambda$createView$2 = TopicsFragment.this.lambda$createView$2(view, i5, f, f2);
                return lambda$createView$2;
            }

            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListenerExtended
            public /* synthetic */ void onLongClickRelease() {
                RecyclerListView.OnItemLongClickListenerExtended.CC.$default$onLongClickRelease(this);
            }

            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListenerExtended
            public /* synthetic */ void onMove(float f, float f2) {
                RecyclerListView.OnItemLongClickListenerExtended.CC.$default$onMove(this, f, f2);
            }
        });
        this.recyclerListView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.TopicsFragment.6
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int i5, int i6) {
                super.onScrolled(recyclerView, i5, i6);
                TopicsFragment.this.contentView.invalidateBlur();
            }
        });
        RecyclerListView recyclerListView3 = this.recyclerListView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        this.layoutManager = linearLayoutManager;
        recyclerListView3.setLayoutManager(linearLayoutManager);
        this.recyclerListView.setAdapter(this.adapter);
        this.recyclerListView.setClipToPadding(false);
        this.recyclerListView.addOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.TopicsFragment.7
            int prevPosition;
            int prevTop;

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int i5, int i6) {
                boolean z;
                int findFirstVisibleItemPosition = TopicsFragment.this.layoutManager.findFirstVisibleItemPosition();
                if (findFirstVisibleItemPosition != -1) {
                    RecyclerView.ViewHolder findViewHolderForAdapterPosition = recyclerView.findViewHolderForAdapterPosition(findFirstVisibleItemPosition);
                    boolean z2 = false;
                    int top = findViewHolderForAdapterPosition != null ? findViewHolderForAdapterPosition.itemView.getTop() : 0;
                    int i7 = this.prevPosition;
                    if (i7 == findFirstVisibleItemPosition) {
                        int i8 = this.prevTop;
                        int i9 = i8 - top;
                        z = top < i8;
                        Math.abs(i9);
                    } else {
                        z = findFirstVisibleItemPosition > i7;
                    }
                    TopicsFragment topicsFragment = TopicsFragment.this;
                    if (z || !topicsFragment.canShowCreateTopic) {
                        z2 = true;
                    }
                    topicsFragment.hideFloatingButton(z2, true);
                }
            }
        });
        this.contentView.addView(this.recyclerListView, LayoutHelper.createFrame(-1, -1.0f));
        ((ViewGroup.MarginLayoutParams) this.recyclerListView.getLayoutParams()).topMargin = -AndroidUtilities.dp(100.0f);
        FrameLayout frameLayout = new FrameLayout(getContext());
        this.floatingButtonContainer = frameLayout;
        frameLayout.setVisibility(0);
        SizeNotifierFrameLayout sizeNotifierFrameLayout2 = this.contentView;
        FrameLayout frameLayout2 = this.floatingButtonContainer;
        int i5 = Build.VERSION.SDK_INT;
        int i6 = 60;
        int i7 = i5 >= 21 ? 56 : 60;
        if (i5 >= 21) {
            i6 = 56;
        }
        float f = i6;
        boolean z = LocaleController.isRTL;
        if (!z) {
            i4 = 5;
        }
        sizeNotifierFrameLayout2.addView(frameLayout2, LayoutHelper.createFrame(i7, f, i4 | 80, z ? 14.0f : 0.0f, 0.0f, z ? 0.0f : 14.0f, 14.0f));
        this.floatingButtonContainer.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.TopicsFragment$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                TopicsFragment.this.lambda$createView$3(view);
            }
        });
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("chats_actionBackground"), Theme.getColor("chats_actionPressedBackground"));
        if (i5 < 21) {
            Drawable mutate = ContextCompat.getDrawable(getParentActivity(), R.drawable.floating_shadow).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorCircleDrawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            i = i3;
            createSimpleSelectorCircleDrawable = combinedDrawable;
        } else {
            StateListAnimator stateListAnimator = new StateListAnimator();
            FrameLayout frameLayout3 = this.floatingButtonContainer;
            Property property = View.TRANSLATION_Z;
            i = i3;
            stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(frameLayout3, property, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(4.0f)).setDuration(200L));
            stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButtonContainer, property, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f)).setDuration(200L));
            this.floatingButtonContainer.setStateListAnimator(stateListAnimator);
            this.floatingButtonContainer.setOutlineProvider(new ViewOutlineProvider(this) { // from class: org.telegram.ui.TopicsFragment.8
                @Override // android.view.ViewOutlineProvider
                @SuppressLint({"NewApi"})
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        this.floatingButtonContainer.setBackground(createSimpleSelectorCircleDrawable);
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        rLottieImageView.setImageResource(R.drawable.ic_chatlist_add_2);
        this.floatingButtonContainer.setContentDescription(LocaleController.getString("CreateTopic", i));
        this.floatingButtonContainer.addView(rLottieImageView, LayoutHelper.createFrame(24, 24, 17));
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context);
        flickerLoadingView.setViewType(24);
        flickerLoadingView.setVisibility(8);
        flickerLoadingView.showDate(true);
        final EmptyViewContainer emptyViewContainer = new EmptyViewContainer(this, context);
        emptyViewContainer.textView.setAlpha(0.0f);
        StickerEmptyView stickerEmptyView = new StickerEmptyView(this, context, flickerLoadingView, 0) { // from class: org.telegram.ui.TopicsFragment.9
            @Override // org.telegram.ui.Components.StickerEmptyView
            public void showProgress(boolean z2, boolean z3) {
                super.showProgress(z2, z3);
                float f2 = 0.0f;
                if (z3) {
                    ViewPropertyAnimator animate = emptyViewContainer.textView.animate();
                    if (!z2) {
                        f2 = 1.0f;
                    }
                    animate.alpha(f2).start();
                    return;
                }
                emptyViewContainer.textView.animate().cancel();
                TextView textView = emptyViewContainer.textView;
                if (!z2) {
                    f2 = 1.0f;
                }
                textView.setAlpha(f2);
            }
        };
        this.topicsEmptyView = stickerEmptyView;
        try {
            stickerEmptyView.stickerView.getImageReceiver().setAutoRepeat(2);
        } catch (Exception unused) {
        }
        this.topicsEmptyView.showProgress(this.loadingTopics, this.fragmentBeginToShow);
        this.topicsEmptyView.title.setText(LocaleController.getString("NoTopics", R.string.NoTopics));
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("d");
        ColoredImageSpan coloredImageSpan = new ColoredImageSpan(R.drawable.ic_ab_other);
        coloredImageSpan.setSize(AndroidUtilities.dp(16.0f));
        spannableStringBuilder.setSpan(coloredImageSpan, 0, 1, 0);
        this.topicsEmptyView.subtitle.setText(AndroidUtilities.replaceCharSequence("%s", LocaleController.getString("NoTopicsDescription", R.string.NoTopicsDescription), spannableStringBuilder));
        emptyViewContainer.addView(flickerLoadingView);
        emptyViewContainer.addView(this.topicsEmptyView);
        this.contentView.addView(emptyViewContainer);
        this.recyclerListView.setEmptyView(emptyViewContainer);
        this.bottomOverlayContainer = new FrameLayout(this, context) { // from class: org.telegram.ui.TopicsFragment.10
            @Override // android.view.ViewGroup, android.view.View
            protected void dispatchDraw(Canvas canvas) {
                Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), Theme.chat_composeShadowDrawable.getIntrinsicHeight());
                Theme.chat_composeShadowDrawable.draw(canvas);
                super.dispatchDraw(canvas);
            }
        };
        UnreadCounterTextView unreadCounterTextView = new UnreadCounterTextView(context);
        this.bottomOverlayChatText = unreadCounterTextView;
        this.bottomOverlayContainer.addView(unreadCounterTextView);
        this.contentView.addView(this.bottomOverlayContainer, LayoutHelper.createFrame(-1, 51, 80));
        this.bottomOverlayChatText.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.TopicsFragment.11
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                TopicsFragment.this.joinToGroup();
            }
        });
        RadialProgressView radialProgressView = new RadialProgressView(context, this.themeDelegate);
        this.bottomOverlayProgress = radialProgressView;
        radialProgressView.setSize(AndroidUtilities.dp(22.0f));
        this.bottomOverlayProgress.setProgressColor(getThemedColor("chat_fieldOverlayText"));
        this.bottomOverlayProgress.setVisibility(4);
        this.bottomOverlayContainer.addView(this.bottomOverlayProgress, LayoutHelper.createFrame(30, 30, 17));
        updateChatInfo();
        this.bottomOverlayChatText.setBackground(Theme.createSelectorDrawable(ColorUtils.setAlphaComponent(getThemedColor("chat_fieldOverlayText"), 26), 2));
        rLottieImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_actionIcon"), PorterDuff.Mode.MULTIPLY));
        this.bottomOverlayContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.actionBar.setActionModeColor(Theme.getColor("windowBackgroundWhite"));
        this.actionBar.setBackgroundColor(Theme.getColor("actionBarDefault"));
        SearchContainer searchContainer = new SearchContainer(context);
        this.searchContainer = searchContainer;
        searchContainer.setVisibility(8);
        this.contentView.addView(this.searchContainer);
        this.searchItem.getSearchField();
        this.searchContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.actionBar.setDrawBlurBackground(this.contentView);
        getMessagesStorage().loadChatInfo(this.chatId, true, null, true, false, 0);
        FrameLayout frameLayout4 = new FrameLayout(context);
        this.topView = frameLayout4;
        this.contentView.addView(frameLayout4, LayoutHelper.createFrame(-1, 200, 48));
        TLRPC$Chat currentChat = getCurrentChat();
        if (currentChat != null) {
            ChatActivityMemberRequestsDelegate chatActivityMemberRequestsDelegate = new ChatActivityMemberRequestsDelegate(this, currentChat, new ChatActivityMemberRequestsDelegate.Callback() { // from class: org.telegram.ui.TopicsFragment$$ExternalSyntheticLambda17
                @Override // org.telegram.ui.Delegates.ChatActivityMemberRequestsDelegate.Callback
                public final void onEnterOffsetChanged() {
                    TopicsFragment.this.updateTopView();
                }
            });
            this.pendingRequestsDelegate = chatActivityMemberRequestsDelegate;
            chatActivityMemberRequestsDelegate.setChatInfo(this.chatFull, false);
            this.topView.addView(this.pendingRequestsDelegate.getView(), -1, this.pendingRequestsDelegate.getViewHeight());
        }
        FragmentContextView fragmentContextView = new FragmentContextView(context, this, false, this.themeDelegate) { // from class: org.telegram.ui.TopicsFragment.12
            @Override // org.telegram.ui.Components.FragmentContextView
            public void setTopPadding(float f2) {
                this.topPadding = f2;
                TopicsFragment.this.updateTopView();
            }
        };
        this.fragmentContextView = fragmentContextView;
        this.topView.addView(fragmentContextView, LayoutHelper.createFrame(-1, 38, 51));
        FrameLayout.LayoutParams createFrame = LayoutHelper.createFrame(-1, -2.0f);
        if (this.inPreviewMode && Build.VERSION.SDK_INT >= 21) {
            createFrame.topMargin = AndroidUtilities.statusBarHeight;
        }
        this.contentView.addView(this.actionBar, createFrame);
        checkForLoadMore();
        View view = new View(context) { // from class: org.telegram.ui.TopicsFragment.13
            @Override // android.view.View
            public void setAlpha(float f2) {
                super.setAlpha(f2);
                if (((BaseFragment) TopicsFragment.this).fragmentView != null) {
                    ((BaseFragment) TopicsFragment.this).fragmentView.invalidate();
                }
            }
        };
        this.blurredView = view;
        if (Build.VERSION.SDK_INT >= 23) {
            view.setForeground(new ColorDrawable(ColorUtils.setAlphaComponent(getThemedColor("windowBackgroundWhite"), 100)));
        }
        this.blurredView.setFocusable(false);
        this.blurredView.setImportantForAccessibility(2);
        this.blurredView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.TopicsFragment$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                TopicsFragment.this.lambda$createView$4(view2);
            }
        });
        this.blurredView.setFitsSystemWindows(true);
        this.bottomPannelVisible = true;
        updateChatInfo();
        return this.fragmentView;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.TopicsFragment$2  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass2 extends ActionBar.ActionBarMenuOnItemClick {
        final /* synthetic */ Context val$context;

        AnonymousClass2(Context context) {
            this.val$context = context;
        }

        @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
        public void onItemClick(int i) {
            if (i == -1) {
                if (TopicsFragment.this.selectedTopics.size() > 0) {
                    TopicsFragment.this.clearSelectedTopics();
                    return;
                } else {
                    TopicsFragment.this.finishFragment();
                    return;
                }
            }
            boolean z = false;
            switch (i) {
                case 1:
                    TopicsFragment.this.switchToChat(false);
                    break;
                case 2:
                    TLRPC$ChatFull tLRPC$ChatFull = TopicsFragment.this.chatFull;
                    if (tLRPC$ChatFull != null && tLRPC$ChatFull.participants != null) {
                        LongSparseArray longSparseArray = new LongSparseArray();
                        for (int i2 = 0; i2 < TopicsFragment.this.chatFull.participants.participants.size(); i2++) {
                            longSparseArray.put(TopicsFragment.this.chatFull.participants.participants.get(i2).user_id, null);
                        }
                        final long j = TopicsFragment.this.chatFull.id;
                        Context context = this.val$context;
                        int i3 = ((BaseFragment) TopicsFragment.this).currentAccount;
                        TopicsFragment topicsFragment = TopicsFragment.this;
                        InviteMembersBottomSheet inviteMembersBottomSheet = new InviteMembersBottomSheet(context, i3, longSparseArray, topicsFragment.chatFull.id, topicsFragment, topicsFragment.themeDelegate) { // from class: org.telegram.ui.TopicsFragment.2.1
                            @Override // org.telegram.ui.Components.InviteMembersBottomSheet
                            protected boolean canGenerateLink() {
                                TLRPC$Chat chat = TopicsFragment.this.getMessagesController().getChat(Long.valueOf(j));
                                return chat != null && ChatObject.canUserDoAdminAction(chat, 3);
                            }
                        };
                        inviteMembersBottomSheet.setDelegate(new GroupCreateActivity.ContactsAddActivityDelegate() { // from class: org.telegram.ui.TopicsFragment$2$$ExternalSyntheticLambda4
                            @Override // org.telegram.ui.GroupCreateActivity.ContactsAddActivityDelegate
                            public final void didSelectUsers(ArrayList arrayList, int i4) {
                                TopicsFragment.AnonymousClass2.this.lambda$onItemClick$1(j, arrayList, i4);
                            }

                            @Override // org.telegram.ui.GroupCreateActivity.ContactsAddActivityDelegate
                            public /* synthetic */ void needAddBot(TLRPC$User tLRPC$User) {
                                GroupCreateActivity.ContactsAddActivityDelegate.CC.$default$needAddBot(this, tLRPC$User);
                            }
                        });
                        inviteMembersBottomSheet.show();
                        break;
                    }
                    break;
                case 3:
                    final TopicCreateFragment create = TopicCreateFragment.create(TopicsFragment.this.chatId, 0);
                    TopicsFragment.this.presentFragment(create);
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.TopicsFragment$2$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            TopicCreateFragment.this.showKeyboard();
                        }
                    }, 200L);
                    break;
                case 4:
                case 5:
                    if (TopicsFragment.this.selectedTopics.size() > 0) {
                        TopicsFragment.this.scrollToTop = true;
                        TopicsFragment.this.updateAnimated = true;
                        TopicsController topicsController = TopicsFragment.this.topicsController;
                        TopicsFragment topicsFragment2 = TopicsFragment.this;
                        long j2 = topicsFragment2.chatId;
                        int intValue = topicsFragment2.selectedTopics.iterator().next().intValue();
                        if (i == 4) {
                            z = true;
                        }
                        topicsController.pinTopic(j2, intValue, z);
                    }
                    TopicsFragment.this.clearSelectedTopics();
                    break;
                case 6:
                    Iterator<Integer> it = TopicsFragment.this.selectedTopics.iterator();
                    while (it.hasNext()) {
                        int intValue2 = it.next().intValue();
                        NotificationsController notificationsController = TopicsFragment.this.getNotificationsController();
                        TopicsFragment topicsFragment3 = TopicsFragment.this;
                        notificationsController.muteDialog(-topicsFragment3.chatId, intValue2, topicsFragment3.mute);
                    }
                    TopicsFragment.this.clearSelectedTopics();
                    break;
                case 7:
                    TopicsFragment topicsFragment4 = TopicsFragment.this;
                    topicsFragment4.deleteTopics(topicsFragment4.selectedTopics, new Runnable() { // from class: org.telegram.ui.TopicsFragment$2$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            TopicsFragment.AnonymousClass2.this.lambda$onItemClick$4();
                        }
                    });
                    break;
                case 8:
                    ArrayList arrayList = new ArrayList(TopicsFragment.this.selectedTopics);
                    for (int i4 = 0; i4 < arrayList.size(); i4++) {
                        TLRPC$TL_forumTopic findTopic = TopicsFragment.this.topicsController.findTopic(TopicsFragment.this.chatId, ((Integer) arrayList.get(i4)).intValue());
                        if (findTopic != null) {
                            TopicsFragment.this.getMessagesController().markMentionsAsRead(-TopicsFragment.this.chatId, findTopic.id);
                            TopicsFragment.this.getMessagesController().markDialogAsRead(-TopicsFragment.this.chatId, findTopic.top_message, 0, findTopic.topMessage.date, false, findTopic.id, 0, true, 0);
                            TopicsFragment.this.getMessagesStorage().updateRepliesMaxReadId(TopicsFragment.this.chatId, findTopic.id, findTopic.top_message, 0, true);
                        }
                    }
                    TopicsFragment.this.clearSelectedTopics();
                    break;
                case 9:
                case 10:
                    TopicsFragment.this.updateAnimated = true;
                    ArrayList arrayList2 = new ArrayList(TopicsFragment.this.selectedTopics);
                    for (int i5 = 0; i5 < arrayList2.size(); i5++) {
                        TopicsFragment.this.topicsController.toggleCloseTopic(TopicsFragment.this.chatId, ((Integer) arrayList2.get(i5)).intValue(), i == 9);
                    }
                    TopicsFragment.this.clearSelectedTopics();
                    break;
                case 11:
                    AlertsCreator.createClearOrDeleteDialogAlert(TopicsFragment.this, false, TopicsFragment.this.getMessagesController().getChat(Long.valueOf(TopicsFragment.this.chatId)), null, false, true, false, new MessagesStorage.BooleanCallback() { // from class: org.telegram.ui.TopicsFragment$2$$ExternalSyntheticLambda3
                        @Override // org.telegram.messenger.MessagesStorage.BooleanCallback
                        public final void run(boolean z2) {
                            TopicsFragment.AnonymousClass2.this.lambda$onItemClick$3(z2);
                        }
                    }, TopicsFragment.this.themeDelegate);
                    break;
            }
            super.onItemClick(i);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onItemClick$1(final long j, final ArrayList arrayList, int i) {
            final int size = arrayList.size();
            final int[] iArr = new int[1];
            for (int i2 = 0; i2 < size; i2++) {
                TopicsFragment.this.getMessagesController().addUserToChat(j, (TLRPC$User) arrayList.get(i2), i, null, TopicsFragment.this, new Runnable() { // from class: org.telegram.ui.TopicsFragment$2$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        TopicsFragment.AnonymousClass2.this.lambda$onItemClick$0(iArr, size, arrayList, j);
                    }
                });
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onItemClick$0(int[] iArr, int i, ArrayList arrayList, long j) {
            int i2 = iArr[0] + 1;
            iArr[0] = i2;
            if (i2 == i) {
                BulletinFactory.of(TopicsFragment.this).createUsersAddedBulletin(arrayList, TopicsFragment.this.getMessagesController().getChat(Long.valueOf(j))).show();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onItemClick$3(boolean z) {
            TopicsFragment.this.getMessagesController().deleteDialog(-TopicsFragment.this.chatId, 0);
            TopicsFragment.this.finishFragment();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onItemClick$4() {
            TopicsFragment.this.clearSelectedTopics();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(View view) {
        Bundle bundle = new Bundle();
        bundle.putLong("chat_id", this.chatId);
        presentFragment(new ProfileActivity(bundle, this.avatarContainer.getSharedMediaPreloader()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1(View view, int i) {
        if (this.opnendForSelect) {
            TLRPC$TL_forumTopic tLRPC$TL_forumTopic = this.forumTopics.get(i);
            OnTopicSelectedListener onTopicSelectedListener = this.onTopicSelectedListener;
            if (onTopicSelectedListener != null) {
                onTopicSelectedListener.onTopicSelected(tLRPC$TL_forumTopic);
            }
            DialogsActivity dialogsActivity = this.dialogsActivity;
            if (dialogsActivity != null) {
                dialogsActivity.didSelectResult(-this.chatId, tLRPC$TL_forumTopic.id, true, false);
            }
            this.removeFragmentOnTransitionEnd = true;
        } else if (this.selectedTopics.size() > 0) {
            toggleSelection(view);
        } else {
            ForumUtilities.openTopic(this, this.chatId, this.forumTopics.get(i), 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createView$2(View view, int i, float f, float f2) {
        if (this.opnendForSelect) {
            return false;
        }
        if (!this.actionBar.isActionModeShowed() && !AndroidUtilities.isTablet() && (view instanceof TopicDialogCell)) {
            TopicDialogCell topicDialogCell = (TopicDialogCell) view;
            if (topicDialogCell.isPointInsideAvatar(f, f2)) {
                return showChatPreview(topicDialogCell);
            }
        }
        toggleSelection(view);
        view.performHapticFeedback(0);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$3(View view) {
        presentFragment(TopicCreateFragment.create(this.chatId, 0));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$4(View view) {
        finishPreviewFragment();
    }

    public void switchToChat(boolean z) {
        this.removeFragmentOnTransitionEnd = z;
        Bundle bundle = new Bundle();
        bundle.putLong("chat_id", this.chatId);
        ChatActivity chatActivity = new ChatActivity(bundle);
        chatActivity.setSwitchFromTopics(true);
        presentFragment(chatActivity);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTopView() {
        float f;
        FragmentContextView fragmentContextView = this.fragmentContextView;
        if (fragmentContextView != null) {
            f = Math.max(0.0f, fragmentContextView.getTopPadding()) + 0.0f;
            this.fragmentContextView.setTranslationY(f);
        } else {
            f = 0.0f;
        }
        ChatActivityMemberRequestsDelegate chatActivityMemberRequestsDelegate = this.pendingRequestsDelegate;
        View view = chatActivityMemberRequestsDelegate != null ? chatActivityMemberRequestsDelegate.getView() : null;
        if (view != null) {
            view.setTranslationY(this.pendingRequestsDelegate.getViewEnterOffset() + f);
            f += this.pendingRequestsDelegate.getViewEnterOffset() + this.pendingRequestsDelegate.getViewHeight();
        }
        this.recyclerListView.setTranslationY(Math.max(0.0f, f));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void deleteTopics(HashSet<Integer> hashSet, Runnable runnable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(LocaleController.getPluralString("DeleteTopics", hashSet.size()));
        ArrayList arrayList = new ArrayList(hashSet);
        if (hashSet.size() == 1) {
            builder.setMessage(LocaleController.formatString("DeleteSelectedTopic", R.string.DeleteSelectedTopic, this.topicsController.findTopic(this.chatId, ((Integer) arrayList.get(0)).intValue()).title));
        } else {
            builder.setMessage(LocaleController.getString("DeleteSelectedTopics", R.string.DeleteSelectedTopics));
        }
        builder.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new AnonymousClass14(hashSet, arrayList, runnable));
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), new DialogInterface.OnClickListener(this) { // from class: org.telegram.ui.TopicsFragment.15
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog create = builder.create();
        create.show();
        TextView textView = (TextView) create.getButton(-1);
        if (textView != null) {
            textView.setTextColor(Theme.getColor("dialogTextRed2"));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.TopicsFragment$14  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass14 implements DialogInterface.OnClickListener {
        final /* synthetic */ Runnable val$runnable;
        final /* synthetic */ HashSet val$selectedTopics;
        final /* synthetic */ ArrayList val$topicsToRemove;

        AnonymousClass14(HashSet hashSet, ArrayList arrayList, Runnable runnable) {
            this.val$selectedTopics = hashSet;
            this.val$topicsToRemove = arrayList;
            this.val$runnable = runnable;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            TopicsFragment.this.excludeTopics = new HashSet<>();
            TopicsFragment.this.excludeTopics.addAll(this.val$selectedTopics);
            TopicsFragment.this.updateTopicsList(true, false);
            BulletinFactory of = BulletinFactory.of(TopicsFragment.this);
            String pluralString = LocaleController.getPluralString("TopicsDeleted", this.val$selectedTopics.size());
            Runnable runnable = new Runnable() { // from class: org.telegram.ui.TopicsFragment$14$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    TopicsFragment.AnonymousClass14.this.lambda$onClick$0();
                }
            };
            final ArrayList arrayList = this.val$topicsToRemove;
            final Runnable runnable2 = this.val$runnable;
            of.createUndoBulletin(pluralString, runnable, new Runnable() { // from class: org.telegram.ui.TopicsFragment$14$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    TopicsFragment.AnonymousClass14.this.lambda$onClick$1(arrayList, runnable2);
                }
            }).show();
            TopicsFragment.this.clearSelectedTopics();
            dialogInterface.dismiss();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onClick$0() {
            TopicsFragment topicsFragment = TopicsFragment.this;
            topicsFragment.excludeTopics = null;
            topicsFragment.updateTopicsList(true, false);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onClick$1(ArrayList arrayList, Runnable runnable) {
            TopicsFragment.this.topicsController.deleteTopics(TopicsFragment.this.chatId, arrayList);
            runnable.run();
        }
    }

    private boolean showChatPreview(TopicDialogCell topicDialogCell) {
        topicDialogCell.performHapticFeedback(0);
        final ActionBarPopupWindow.ActionBarPopupWindowLayout[] actionBarPopupWindowLayoutArr = {new ActionBarPopupWindow.ActionBarPopupWindowLayout(getParentActivity(), R.drawable.popup_fixed_alert, getResourceProvider(), 1)};
        final TLRPC$TL_forumTopic tLRPC$TL_forumTopic = topicDialogCell.forumTopic;
        ChatNotificationsPopupWrapper chatNotificationsPopupWrapper = new ChatNotificationsPopupWrapper(getContext(), this.currentAccount, actionBarPopupWindowLayoutArr[0].getSwipeBack(), false, false, new AnonymousClass16(tLRPC$TL_forumTopic), getResourceProvider());
        final int addViewToSwipeBack = actionBarPopupWindowLayoutArr[0].addViewToSwipeBack(chatNotificationsPopupWrapper.windowLayout);
        chatNotificationsPopupWrapper.type = 1;
        chatNotificationsPopupWrapper.lambda$update$11(-this.chatId, tLRPC$TL_forumTopic.id, null);
        if (ChatObject.canManageTopics(getCurrentChat())) {
            ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(getParentActivity(), true, false);
            if (tLRPC$TL_forumTopic.pinned) {
                actionBarMenuSubItem.setTextAndIcon(LocaleController.getString("DialogUnpin", R.string.DialogUnpin), R.drawable.msg_unpin);
            } else {
                actionBarMenuSubItem.setTextAndIcon(LocaleController.getString("DialogPin", R.string.DialogPin), R.drawable.msg_pin);
            }
            actionBarMenuSubItem.setMinimumWidth(160);
            actionBarMenuSubItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.TopicsFragment$$ExternalSyntheticLambda6
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    TopicsFragment.this.lambda$showChatPreview$5(tLRPC$TL_forumTopic, view);
                }
            });
            actionBarPopupWindowLayoutArr[0].addView(actionBarMenuSubItem);
        }
        ActionBarMenuSubItem actionBarMenuSubItem2 = new ActionBarMenuSubItem(getParentActivity(), false, false);
        if (getMessagesController().isDialogMuted(-this.chatId, tLRPC$TL_forumTopic.id)) {
            actionBarMenuSubItem2.setTextAndIcon(LocaleController.getString("Unmute", R.string.Unmute), R.drawable.msg_mute);
        } else {
            actionBarMenuSubItem2.setTextAndIcon(LocaleController.getString("Mute", R.string.Mute), R.drawable.msg_unmute);
        }
        actionBarMenuSubItem2.setMinimumWidth(160);
        actionBarMenuSubItem2.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.TopicsFragment$$ExternalSyntheticLambda8
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                TopicsFragment.this.lambda$showChatPreview$6(tLRPC$TL_forumTopic, actionBarPopupWindowLayoutArr, addViewToSwipeBack, view);
            }
        });
        actionBarPopupWindowLayoutArr[0].addView(actionBarMenuSubItem2);
        if (ChatObject.canManageTopic(this.currentAccount, getCurrentChat(), tLRPC$TL_forumTopic)) {
            ActionBarMenuSubItem actionBarMenuSubItem3 = new ActionBarMenuSubItem(getParentActivity(), false, false);
            if (tLRPC$TL_forumTopic.closed) {
                actionBarMenuSubItem3.setTextAndIcon(LocaleController.getString("RestartTopic", R.string.RestartTopic), R.drawable.msg_topic_restart);
            } else {
                actionBarMenuSubItem3.setTextAndIcon(LocaleController.getString("CloseTopic", R.string.CloseTopic), R.drawable.msg_topic_close);
            }
            actionBarMenuSubItem3.setMinimumWidth(160);
            actionBarMenuSubItem3.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.TopicsFragment$$ExternalSyntheticLambda7
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    TopicsFragment.this.lambda$showChatPreview$7(tLRPC$TL_forumTopic, view);
                }
            });
            actionBarPopupWindowLayoutArr[0].addView(actionBarMenuSubItem3);
            ActionBarMenuSubItem actionBarMenuSubItem4 = new ActionBarMenuSubItem(getParentActivity(), false, true);
            actionBarMenuSubItem4.setTextAndIcon(LocaleController.getString("DeleteTopics_one", R.string.DeleteTopics_one), R.drawable.msg_delete);
            actionBarMenuSubItem4.setIconColor(getThemedColor("dialogRedIcon"));
            actionBarMenuSubItem4.setTextColor(getThemedColor("dialogTextRed"));
            actionBarMenuSubItem4.setMinimumWidth(160);
            actionBarMenuSubItem4.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.TopicsFragment$$ExternalSyntheticLambda5
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    TopicsFragment.this.lambda$showChatPreview$9(tLRPC$TL_forumTopic, view);
                }
            });
            actionBarPopupWindowLayoutArr[0].addView(actionBarMenuSubItem4);
        }
        prepareBlurBitmap();
        Bundle bundle = new Bundle();
        bundle.putLong("chat_id", this.chatId);
        ChatActivity chatActivity = new ChatActivity(bundle);
        ForumUtilities.applyTopic(chatActivity, MessagesStorage.TopicKey.of(-this.chatId, topicDialogCell.forumTopic.id));
        presentFragmentAsPreviewWithMenu(chatActivity, actionBarPopupWindowLayoutArr[0]);
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.TopicsFragment$16  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass16 implements ChatNotificationsPopupWrapper.Callback {
        final /* synthetic */ TLRPC$TL_forumTopic val$topic;

        @Override // org.telegram.ui.Components.ChatNotificationsPopupWrapper.Callback
        public /* synthetic */ void openExceptions() {
            ChatNotificationsPopupWrapper.Callback.CC.$default$openExceptions(this);
        }

        AnonymousClass16(TLRPC$TL_forumTopic tLRPC$TL_forumTopic) {
            this.val$topic = tLRPC$TL_forumTopic;
        }

        @Override // org.telegram.ui.Components.ChatNotificationsPopupWrapper.Callback
        public void dismiss() {
            TopicsFragment.this.finishPreviewFragment();
        }

        @Override // org.telegram.ui.Components.ChatNotificationsPopupWrapper.Callback
        public void toggleSound() {
            SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(((BaseFragment) TopicsFragment.this).currentAccount);
            boolean z = !notificationsSettings.getBoolean("sound_enabled_" + NotificationsController.getSharedPrefKey(-TopicsFragment.this.chatId, this.val$topic.id), true);
            notificationsSettings.edit().putBoolean("sound_enabled_" + NotificationsController.getSharedPrefKey(-TopicsFragment.this.chatId, this.val$topic.id), z).apply();
            TopicsFragment.this.finishPreviewFragment();
            if (BulletinFactory.canShowBulletin(TopicsFragment.this)) {
                TopicsFragment topicsFragment = TopicsFragment.this;
                BulletinFactory.createSoundEnabledBulletin(topicsFragment, !z, topicsFragment.getResourceProvider()).show();
            }
        }

        @Override // org.telegram.ui.Components.ChatNotificationsPopupWrapper.Callback
        public void muteFor(int i) {
            TopicsFragment.this.finishPreviewFragment();
            if (i != 0) {
                TopicsFragment.this.getNotificationsController().muteUntil(-TopicsFragment.this.chatId, this.val$topic.id, i);
                if (!BulletinFactory.canShowBulletin(TopicsFragment.this)) {
                    return;
                }
                TopicsFragment topicsFragment = TopicsFragment.this;
                BulletinFactory.createMuteBulletin(topicsFragment, 5, i, topicsFragment.getResourceProvider()).show();
                return;
            }
            if (TopicsFragment.this.getMessagesController().isDialogMuted(-TopicsFragment.this.chatId, this.val$topic.id)) {
                TopicsFragment.this.getNotificationsController().muteDialog(-TopicsFragment.this.chatId, this.val$topic.id, false);
            }
            if (!BulletinFactory.canShowBulletin(TopicsFragment.this)) {
                return;
            }
            TopicsFragment topicsFragment2 = TopicsFragment.this;
            BulletinFactory.createMuteBulletin(topicsFragment2, 4, i, topicsFragment2.getResourceProvider()).show();
        }

        @Override // org.telegram.ui.Components.ChatNotificationsPopupWrapper.Callback
        public void showCustomize() {
            TopicsFragment.this.finishPreviewFragment();
            final TLRPC$TL_forumTopic tLRPC$TL_forumTopic = this.val$topic;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.TopicsFragment$16$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    TopicsFragment.AnonymousClass16.this.lambda$showCustomize$0(tLRPC$TL_forumTopic);
                }
            }, 500L);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$showCustomize$0(TLRPC$TL_forumTopic tLRPC$TL_forumTopic) {
            Bundle bundle = new Bundle();
            bundle.putLong("dialog_id", -TopicsFragment.this.chatId);
            bundle.putInt("topic_id", tLRPC$TL_forumTopic.id);
            TopicsFragment topicsFragment = TopicsFragment.this;
            topicsFragment.presentFragment(new ProfileNotificationsActivity(bundle, topicsFragment.themeDelegate));
        }

        @Override // org.telegram.ui.Components.ChatNotificationsPopupWrapper.Callback
        public void toggleMute() {
            TopicsFragment.this.finishPreviewFragment();
            boolean z = !TopicsFragment.this.getMessagesController().isDialogMuted(-TopicsFragment.this.chatId, this.val$topic.id);
            TopicsFragment.this.getNotificationsController().muteDialog(-TopicsFragment.this.chatId, this.val$topic.id, z);
            if (BulletinFactory.canShowBulletin(TopicsFragment.this)) {
                TopicsFragment topicsFragment = TopicsFragment.this;
                BulletinFactory.createMuteBulletin(topicsFragment, z ? 3 : 4, z ? Integer.MAX_VALUE : 0, topicsFragment.getResourceProvider()).show();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showChatPreview$5(TLRPC$TL_forumTopic tLRPC$TL_forumTopic, View view) {
        this.scrollToTop = true;
        this.updateAnimated = true;
        this.topicsController.pinTopic(this.chatId, tLRPC$TL_forumTopic.id, !tLRPC$TL_forumTopic.pinned);
        finishPreviewFragment();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showChatPreview$6(TLRPC$TL_forumTopic tLRPC$TL_forumTopic, ActionBarPopupWindow.ActionBarPopupWindowLayout[] actionBarPopupWindowLayoutArr, int i, View view) {
        if (getMessagesController().isDialogMuted(-this.chatId, tLRPC$TL_forumTopic.id)) {
            getNotificationsController().muteDialog(-this.chatId, tLRPC$TL_forumTopic.id, false);
            finishPreviewFragment();
            if (!BulletinFactory.canShowBulletin(this)) {
                return;
            }
            BulletinFactory.createMuteBulletin(this, 4, 0, getResourceProvider()).show();
            return;
        }
        actionBarPopupWindowLayoutArr[0].getSwipeBack().openForeground(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showChatPreview$7(TLRPC$TL_forumTopic tLRPC$TL_forumTopic, View view) {
        this.updateAnimated = true;
        this.topicsController.toggleCloseTopic(this.chatId, tLRPC$TL_forumTopic.id, !tLRPC$TL_forumTopic.closed);
        finishPreviewFragment();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showChatPreview$9(TLRPC$TL_forumTopic tLRPC$TL_forumTopic, View view) {
        HashSet<Integer> hashSet = new HashSet<>();
        hashSet.add(Integer.valueOf(tLRPC$TL_forumTopic.id));
        deleteTopics(hashSet, new Runnable() { // from class: org.telegram.ui.TopicsFragment$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                TopicsFragment.this.lambda$showChatPreview$8();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showChatPreview$8() {
        finishPreviewFragment();
    }

    private void checkLoading() {
        this.loadingTopics = this.topicsController.isLoading(this.chatId);
        if (this.topicsEmptyView != null && this.forumTopics.size() == 0) {
            this.topicsEmptyView.showProgress(this.loadingTopics, this.fragmentBeginToShow);
        }
        updateCreateTopicButton(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void animateToSearchView(final boolean z) {
        this.searching = z;
        ValueAnimator valueAnimator = this.searchAnimator;
        if (valueAnimator != null) {
            valueAnimator.removeAllListeners();
            this.searchAnimator.cancel();
        }
        float[] fArr = new float[2];
        fArr[0] = this.searchAnimationProgress;
        fArr[1] = z ? 1.0f : 0.0f;
        this.searchAnimator = ValueAnimator.ofFloat(fArr);
        AndroidUtilities.updateViewVisibilityAnimated(this.searchContainer, false, 1.0f, true);
        this.animateSearchWithScale = !z && this.searchContainer.getVisibility() == 0 && this.searchContainer.getAlpha() == 1.0f;
        this.searchAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.TopicsFragment$$ExternalSyntheticLambda1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                TopicsFragment.this.lambda$animateToSearchView$10(valueAnimator2);
            }
        });
        if (!z) {
            this.other.setVisibility(0);
        } else {
            this.searchContainer.setVisibility(0);
            AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
            updateCreateTopicButton(false);
        }
        this.searchAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.TopicsFragment.17
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                TopicsFragment.this.updateSearchProgress(z ? 1.0f : 0.0f);
                if (z) {
                    TopicsFragment.this.other.setVisibility(8);
                    return;
                }
                AndroidUtilities.setAdjustResizeToNothing(TopicsFragment.this.getParentActivity(), ((BaseFragment) TopicsFragment.this).classGuid);
                TopicsFragment.this.searchContainer.setVisibility(8);
                TopicsFragment.this.updateCreateTopicButton(true);
            }
        });
        this.searchAnimator.setDuration(200L);
        this.searchAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.searchAnimator.start();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needCheckSystemBarColors, Boolean.TRUE);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$animateToSearchView$10(ValueAnimator valueAnimator) {
        updateSearchProgress(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCreateTopicButton(boolean z) {
        if (this.createTopicSubmenu == null) {
            return;
        }
        int i = 0;
        boolean z2 = !ChatObject.isNotInChat(getMessagesController().getChat(Long.valueOf(this.chatId))) && ChatObject.canCreateTopic(getMessagesController().getChat(Long.valueOf(this.chatId))) && !this.searching && !this.opnendForSelect && !this.loadingTopics;
        this.canShowCreateTopic = z2;
        ActionBarMenuSubItem actionBarMenuSubItem = this.createTopicSubmenu;
        if (!z2) {
            i = 8;
        }
        actionBarMenuSubItem.setVisibility(i);
        hideFloatingButton(!this.canShowCreateTopic, z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSearchProgress(float f) {
        this.searchAnimationProgress = f;
        float f2 = 1.0f - f;
        this.avatarContainer.getTitleTextView().setAlpha(f2);
        this.avatarContainer.getSubtitleTextView().setAlpha(f2);
        if (this.animateSearchWithScale) {
            float f3 = ((1.0f - this.searchAnimationProgress) * 0.02f) + 0.98f;
            this.recyclerListView.setScaleX(f3);
            this.recyclerListView.setScaleY(f3);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void joinToGroup() {
        getMessagesController().addUserToChat(this.chatId, getUserConfig().getCurrentUser(), 0, null, this, false, new Runnable() { // from class: org.telegram.ui.TopicsFragment$$ExternalSyntheticLambda11
            @Override // java.lang.Runnable
            public final void run() {
                TopicsFragment.this.lambda$joinToGroup$11();
            }
        }, new MessagesController.ErrorDelegate() { // from class: org.telegram.ui.TopicsFragment$$ExternalSyntheticLambda13
            @Override // org.telegram.messenger.MessagesController.ErrorDelegate
            public final boolean run(TLRPC$TL_error tLRPC$TL_error) {
                boolean lambda$joinToGroup$12;
                lambda$joinToGroup$12 = TopicsFragment.this.lambda$joinToGroup$12(tLRPC$TL_error);
                return lambda$joinToGroup$12;
            }
        });
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.closeSearchByActiveAction, new Object[0]);
        updateChatInfo();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$joinToGroup$11() {
        updateChatInfo(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$joinToGroup$12(TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null || !"INVITE_REQUEST_SENT".equals(tLRPC$TL_error.text)) {
            return true;
        }
        SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
        edit.putLong("dialog_join_requested_time_" + (-this.chatId), System.currentTimeMillis()).commit();
        JoinGroupAlert.showBulletin(getContext(), this, ChatObject.isChannelAndNotMegaGroup(getCurrentChat()));
        updateChatInfo(true);
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearSelectedTopics() {
        this.selectedTopics.clear();
        this.actionBar.hideActionMode();
        AndroidUtilities.updateVisibleRows(this.recyclerListView);
    }

    private void toggleSelection(View view) {
        int i;
        String str;
        int i2;
        String str2;
        if (view instanceof TopicDialogCell) {
            TopicDialogCell topicDialogCell = (TopicDialogCell) view;
            int i3 = topicDialogCell.forumTopic.id;
            if (!this.selectedTopics.remove(Integer.valueOf(i3))) {
                this.selectedTopics.add(Integer.valueOf(i3));
            }
            topicDialogCell.setChecked(this.selectedTopics.contains(Integer.valueOf(i3)), true);
            TLRPC$Chat chat = getMessagesController().getChat(Long.valueOf(this.chatId));
            int i4 = 8;
            if (this.selectedTopics.size() > 0) {
                chekActionMode();
                this.actionBar.showActionMode(true);
                Iterator<Integer> it = this.selectedTopics.iterator();
                int i5 = 0;
                int i6 = 0;
                int i7 = 0;
                int i8 = 0;
                while (it.hasNext()) {
                    int intValue = it.next().intValue();
                    TLRPC$TL_forumTopic findTopic = this.topicsController.findTopic(this.chatId, intValue);
                    if (findTopic != null) {
                        if (findTopic.unread_count != 0) {
                            i5++;
                        }
                        if (ChatObject.canManageTopic(this.currentAccount, chat, findTopic)) {
                            if (findTopic.pinned) {
                                i8++;
                            } else {
                                i7++;
                            }
                        }
                    }
                    if (getMessagesController().isDialogMuted(-this.chatId, intValue)) {
                        i6++;
                    }
                }
                if (i5 > 0) {
                    this.readItem.setVisibility(0);
                    this.readItem.setTextAndIcon(LocaleController.getString("MarkAsRead", R.string.MarkAsRead), R.drawable.msg_markread);
                } else {
                    this.readItem.setVisibility(8);
                }
                if (i6 != 0) {
                    this.mute = false;
                    this.muteItem.setIcon(R.drawable.msg_unmute);
                    this.muteItem.setContentDescription(LocaleController.getString("ChatsUnmute", R.string.ChatsUnmute));
                } else {
                    this.mute = true;
                    this.muteItem.setIcon(R.drawable.msg_mute);
                    this.muteItem.setContentDescription(LocaleController.getString("ChatsMute", R.string.ChatsMute));
                }
                this.pinItem.setVisibility((i7 == 1 && i8 == 0) ? 0 : 8);
                this.unpinItem.setVisibility((i8 == 1 && i7 == 0) ? 0 : 8);
            } else {
                this.actionBar.hideActionMode();
            }
            this.selectedDialogsCountTextView.setNumber(this.selectedTopics.size(), true);
            Iterator<Integer> it2 = this.selectedTopics.iterator();
            int i9 = 0;
            int i10 = 0;
            int i11 = 0;
            while (it2.hasNext()) {
                TLRPC$TL_forumTopic findTopic2 = this.topicsController.findTopic(this.chatId, it2.next().intValue());
                if (findTopic2 != null) {
                    if (ChatObject.canManageTopics(chat)) {
                        i11++;
                    }
                    if (ChatObject.canManageTopic(this.currentAccount, chat, findTopic2)) {
                        if (findTopic2.closed) {
                            i9++;
                        } else {
                            i10++;
                        }
                    }
                }
            }
            this.closeTopic.setVisibility((i9 != 0 || i10 <= 0) ? 8 : 0);
            ActionBarMenuSubItem actionBarMenuSubItem = this.closeTopic;
            if (i10 > 1) {
                i = R.string.CloseTopics;
                str = "CloseTopics";
            } else {
                i = R.string.CloseTopic;
                str = "CloseTopic";
            }
            actionBarMenuSubItem.setText(LocaleController.getString(str, i));
            this.restartTopic.setVisibility((i10 != 0 || i9 <= 0) ? 8 : 0);
            ActionBarMenuSubItem actionBarMenuSubItem2 = this.restartTopic;
            if (i9 > 1) {
                i2 = R.string.RestartTopics;
                str2 = "RestartTopics";
            } else {
                i2 = R.string.RestartTopic;
                str2 = "RestartTopic";
            }
            actionBarMenuSubItem2.setText(LocaleController.getString(str2, i2));
            ActionBarMenuItem actionBarMenuItem = this.deleteItem;
            if (i11 == this.selectedTopics.size()) {
                i4 = 0;
            }
            actionBarMenuItem.setVisibility(i4);
            this.otherItem.checkHideMenuItem();
        }
    }

    private void chekActionMode() {
        if (this.actionBar.actionModeIsExist(null)) {
            return;
        }
        ActionBarMenu createActionMode = this.actionBar.createActionMode(false, null);
        NumberTextView numberTextView = new NumberTextView(createActionMode.getContext());
        this.selectedDialogsCountTextView = numberTextView;
        numberTextView.setTextSize(18);
        this.selectedDialogsCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.selectedDialogsCountTextView.setTextColor(Theme.getColor("actionBarActionModeDefaultIcon"));
        createActionMode.addView(this.selectedDialogsCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 72, 0, 0, 0));
        this.selectedDialogsCountTextView.setOnTouchListener(TopicsFragment$$ExternalSyntheticLambda9.INSTANCE);
        this.pinItem = createActionMode.addItemWithWidth(4, R.drawable.msg_pin, AndroidUtilities.dp(54.0f));
        this.unpinItem = createActionMode.addItemWithWidth(5, R.drawable.msg_unpin, AndroidUtilities.dp(54.0f));
        this.muteItem = createActionMode.addItemWithWidth(6, R.drawable.msg_mute, AndroidUtilities.dp(54.0f));
        this.deleteItem = createActionMode.addItemWithWidth(7, R.drawable.msg_delete, AndroidUtilities.dp(54.0f), LocaleController.getString("Delete", R.string.Delete));
        ActionBarMenuItem addItemWithWidth = createActionMode.addItemWithWidth(0, R.drawable.ic_ab_other, AndroidUtilities.dp(54.0f), LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
        this.otherItem = addItemWithWidth;
        this.readItem = addItemWithWidth.addSubItem(8, R.drawable.msg_markread, LocaleController.getString("MarkAsRead", R.string.MarkAsRead));
        this.closeTopic = this.otherItem.addSubItem(9, R.drawable.msg_topic_close, LocaleController.getString("CloseTopic", R.string.CloseTopic));
        this.restartTopic = this.otherItem.addSubItem(10, R.drawable.msg_topic_restart, LocaleController.getString("RestartTopic", R.string.RestartTopic));
    }

    private void updateChatInfo() {
        updateChatInfo(false);
    }

    /* JADX WARN: Removed duplicated region for block: B:59:0x0171  */
    /* JADX WARN: Removed duplicated region for block: B:77:0x01bf  */
    /* JADX WARN: Removed duplicated region for block: B:78:0x01c2  */
    /* JADX WARN: Removed duplicated region for block: B:81:0x01ce  */
    /* JADX WARN: Removed duplicated region for block: B:82:0x01d0  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void updateChatInfo(boolean r13) {
        /*
            Method dump skipped, instructions count: 503
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.TopicsFragment.updateChatInfo(boolean):void");
    }

    private void updateSubtitle() {
        String lowerCase;
        TLRPC$ChatParticipants tLRPC$ChatParticipants;
        TLRPC$ChatFull chatFull = getMessagesController().getChatFull(this.chatId);
        TLRPC$ChatFull tLRPC$ChatFull = this.chatFull;
        if (tLRPC$ChatFull != null && (tLRPC$ChatParticipants = tLRPC$ChatFull.participants) != null) {
            chatFull.participants = tLRPC$ChatParticipants;
        }
        this.chatFull = chatFull;
        if (chatFull != null) {
            lowerCase = LocaleController.formatPluralString("Members", chatFull.participants_count, new Object[0]);
        } else {
            lowerCase = LocaleController.getString("Loading", R.string.Loading).toLowerCase();
        }
        this.avatarContainer.setSubtitle(lowerCase);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ActionBar createActionBar(Context context) {
        return super.createActionBar(context);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        getMessagesController().loadFullChat(this.chatId, 0, true);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.topicsDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.dialogsNeedReload);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.groupCallUpdated);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.notificationsSettingsUpdated);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatSwithcedToForum);
        updateTopicsList(false, false);
        SelectAnimatedEmojiDialog.preload(this.currentAccount);
        TLRPC$Chat chat = getMessagesController().getChat(Long.valueOf(this.chatId));
        if (ChatObject.isChannel(chat)) {
            getMessagesController().startShortPoll(chat, this.classGuid, false);
        }
        if (!settingsPreloaded.contains(Long.valueOf(this.chatId))) {
            settingsPreloaded.add(Long.valueOf(this.chatId));
            TLRPC$TL_account_getNotifyExceptions tLRPC$TL_account_getNotifyExceptions = new TLRPC$TL_account_getNotifyExceptions();
            TLRPC$TL_inputNotifyPeer tLRPC$TL_inputNotifyPeer = new TLRPC$TL_inputNotifyPeer();
            tLRPC$TL_account_getNotifyExceptions.peer = tLRPC$TL_inputNotifyPeer;
            tLRPC$TL_account_getNotifyExceptions.flags |= 1;
            tLRPC$TL_inputNotifyPeer.peer = getMessagesController().getInputPeer(-this.chatId);
            getConnectionsManager().sendRequest(tLRPC$TL_account_getNotifyExceptions, new RequestDelegate() { // from class: org.telegram.ui.TopicsFragment$$ExternalSyntheticLambda14
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    TopicsFragment.this.lambda$onFragmentCreate$15(tLObject, tLRPC$TL_error);
                }
            });
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onFragmentCreate$15(final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.TopicsFragment$$ExternalSyntheticLambda12
            @Override // java.lang.Runnable
            public final void run() {
                TopicsFragment.this.lambda$onFragmentCreate$14(tLObject);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onFragmentCreate$14(TLObject tLObject) {
        if (tLObject instanceof TLRPC$TL_updates) {
            getMessagesController().processUpdates((TLRPC$TL_updates) tLObject, false);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        getNotificationCenter().onAnimationFinish(this.transitionAnimationIndex);
        NotificationCenter.getGlobalInstance().onAnimationFinish(this.transitionAnimationGlobalIndex);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.topicsDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.dialogsNeedReload);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.groupCallUpdated);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatSwithcedToForum);
        TLRPC$Chat chat = getMessagesController().getChat(Long.valueOf(this.chatId));
        if (ChatObject.isChannel(chat)) {
            getMessagesController().startShortPoll(chat, this.classGuid, true);
        }
        super.onFragmentDestroy();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTopicsList(boolean z, boolean z2) {
        LinearLayoutManager linearLayoutManager;
        if (!z && (this.updateAnimated || (this.itemAnimator != null && System.currentTimeMillis() - this.lastAnimatedDuration < this.itemAnimator.getMoveDuration()))) {
            z = true;
        }
        if (z) {
            this.lastAnimatedDuration = System.currentTimeMillis();
        }
        this.updateAnimated = false;
        ArrayList<TLRPC$TL_forumTopic> topics = this.topicsController.getTopics(this.chatId);
        if (topics != null) {
            int size = this.forumTopics.size();
            this.forumTopics.clear();
            for (int i = 0; i < topics.size(); i++) {
                HashSet<Integer> hashSet = this.excludeTopics;
                if (hashSet == null || !hashSet.contains(Integer.valueOf(topics.get(i).id))) {
                    this.forumTopics.add(topics.get(i));
                }
            }
            if (this.forumTopics.size() == 1 && this.forumTopics.get(0).id == 1) {
                this.forumTopics.clear();
            }
            int i2 = 0;
            while (true) {
                if (i2 >= this.forumTopics.size()) {
                    break;
                } else if (this.forumTopics.get(i2).pinned) {
                    ArrayList<TLRPC$TL_forumTopic> arrayList = this.forumTopics;
                    arrayList.add(0, arrayList.remove(i2));
                    break;
                } else {
                    i2++;
                }
            }
            RecyclerListView recyclerListView = this.recyclerListView;
            if (recyclerListView != null) {
                DefaultItemAnimator defaultItemAnimator = null;
                if (recyclerListView.getItemAnimator() != (z ? this.itemAnimator : null)) {
                    RecyclerListView recyclerListView2 = this.recyclerListView;
                    if (z) {
                        defaultItemAnimator = this.itemAnimator;
                    }
                    recyclerListView2.setItemAnimator(defaultItemAnimator);
                }
            }
            Adapter adapter = this.adapter;
            if (adapter != null) {
                adapter.notifyDataSetChanged(true);
            }
            int size2 = this.forumTopics.size();
            if (this.fragmentBeginToShow && z2 && size2 > size) {
                this.itemsEnterAnimator.showItemsAnimated(size + 1);
            }
            if (this.scrollToTop && (linearLayoutManager = this.layoutManager) != null) {
                linearLayoutManager.scrollToPosition(0);
                this.scrollToTop = false;
            }
        }
        checkLoading();
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        TLRPC$ChatFull tLRPC$ChatFull;
        if (i == NotificationCenter.chatInfoDidLoad) {
            TLRPC$ChatFull tLRPC$ChatFull2 = (TLRPC$ChatFull) objArr[0];
            TLRPC$ChatParticipants tLRPC$ChatParticipants = tLRPC$ChatFull2.participants;
            if (tLRPC$ChatParticipants != null && (tLRPC$ChatFull = this.chatFull) != null) {
                tLRPC$ChatFull.participants = tLRPC$ChatParticipants;
            }
            if (tLRPC$ChatFull2.id != this.chatId) {
                return;
            }
            updateChatInfo();
            ChatActivityMemberRequestsDelegate chatActivityMemberRequestsDelegate = this.pendingRequestsDelegate;
            if (chatActivityMemberRequestsDelegate == null) {
                return;
            }
            chatActivityMemberRequestsDelegate.setChatInfo(tLRPC$ChatFull2, true);
        } else if (i == NotificationCenter.topicsDidLoaded) {
            if (this.chatId != ((Long) objArr[0]).longValue()) {
                return;
            }
            updateTopicsList(false, true);
            if (objArr.length <= 1 || !((Boolean) objArr[1]).booleanValue()) {
                return;
            }
            checkForLoadMore();
        } else if (i == NotificationCenter.updateInterfaces) {
            int intValue = ((Integer) objArr[0]).intValue();
            if (intValue == MessagesController.UPDATE_MASK_CHAT) {
                updateChatInfo();
            }
            if ((intValue & MessagesController.UPDATE_MASK_SELECT_DIALOG) <= 0) {
                return;
            }
            updateTopicsList(false, false);
        } else if (i == NotificationCenter.dialogsNeedReload) {
            updateTopicsList(false, false);
        } else if (i == NotificationCenter.groupCallUpdated) {
            Long l = (Long) objArr[0];
            if (this.chatId != l.longValue()) {
                return;
            }
            this.groupCall = getMessagesController().getGroupCall(l.longValue(), false);
            FragmentContextView fragmentContextView = this.fragmentContextView;
            if (fragmentContextView == null) {
                return;
            }
            fragmentContextView.checkCall(true);
        } else if (i == NotificationCenter.notificationsSettingsUpdated) {
            updateTopicsList(false, false);
        } else {
            int i3 = NotificationCenter.chatSwithcedToForum;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkForLoadMore() {
        LinearLayoutManager linearLayoutManager;
        if (this.topicsController.endIsReached(this.chatId) || (linearLayoutManager = this.layoutManager) == null) {
            return;
        }
        int findLastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
        if (this.forumTopics.isEmpty() || findLastVisibleItemPosition >= this.adapter.getItemCount() - 5) {
            this.topicsController.loadTopics(this.chatId);
        }
        checkLoading();
    }

    public void setExcludeTopics(HashSet<Integer> hashSet) {
        this.excludeTopics = hashSet;
    }

    @Override // org.telegram.ui.Components.FragmentContextView.ChatActivityInterface
    public ChatObject.Call getGroupCall() {
        ChatObject.Call call = this.groupCall;
        if (call == null || !(call.call instanceof TLRPC$TL_groupCall)) {
            return null;
        }
        return call;
    }

    @Override // org.telegram.ui.Components.FragmentContextView.ChatActivityInterface
    public TLRPC$Chat getCurrentChat() {
        return getMessagesController().getChat(Long.valueOf(this.chatId));
    }

    @Override // org.telegram.ui.Components.FragmentContextView.ChatActivityInterface
    public long getDialogId() {
        return -this.chatId;
    }

    public void setForwardFromDialogFragment(DialogsActivity dialogsActivity) {
        this.dialogsActivity = dialogsActivity;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class Adapter extends RecyclerListView.SelectionAdapter {
        private ArrayList<Integer> hashes;

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        private Adapter() {
            this.hashes = new ArrayList<>();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /* renamed from: onCreateViewHolder */
        public RecyclerView.ViewHolder mo1813onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new RecyclerListView.Holder(new TopicDialogCell(null, viewGroup.getContext(), true, false));
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            TLRPC$TL_forumTopic tLRPC$TL_forumTopic = TopicsFragment.this.forumTopics.get(i);
            int i2 = i + 1;
            TLRPC$TL_forumTopic tLRPC$TL_forumTopic2 = i2 < TopicsFragment.this.forumTopics.size() ? TopicsFragment.this.forumTopics.get(i2) : null;
            TopicDialogCell topicDialogCell = (TopicDialogCell) viewHolder.itemView;
            TLRPC$Message tLRPC$Message = tLRPC$TL_forumTopic.topMessage;
            TLRPC$TL_forumTopic tLRPC$TL_forumTopic3 = topicDialogCell.forumTopic;
            boolean z = false;
            int i3 = tLRPC$TL_forumTopic3 == null ? 0 : tLRPC$TL_forumTopic3.id;
            int i4 = tLRPC$TL_forumTopic.id;
            boolean z2 = i3 == i4 && topicDialogCell.position == i;
            if (tLRPC$Message != null) {
                topicDialogCell.setForumTopic(tLRPC$TL_forumTopic, -TopicsFragment.this.chatId, new MessageObject(((BaseFragment) TopicsFragment.this).currentAccount, tLRPC$Message, false, false), z2);
                topicDialogCell.drawDivider = i != TopicsFragment.this.forumTopics.size() - 1;
                boolean z3 = tLRPC$TL_forumTopic.pinned;
                if (z3 && (tLRPC$TL_forumTopic2 == null || !tLRPC$TL_forumTopic2.pinned)) {
                    z = true;
                }
                topicDialogCell.fullSeparator = z;
                topicDialogCell.setPinForced(z3);
                topicDialogCell.position = i;
            }
            topicDialogCell.setTopicIcon(tLRPC$TL_forumTopic);
            topicDialogCell.setChecked(TopicsFragment.this.selectedTopics.contains(Integer.valueOf(i4)), z2);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return TopicsFragment.this.forumTopics.size();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            this.hashes.clear();
            for (int i = 0; i < TopicsFragment.this.forumTopics.size(); i++) {
                this.hashes.add(Integer.valueOf(TopicsFragment.this.forumTopics.get(i).id));
            }
            super.notifyDataSetChanged();
        }

        public void notifyDataSetChanged(boolean z) {
            final ArrayList arrayList = new ArrayList(this.hashes);
            this.hashes.clear();
            for (int i = 0; i < TopicsFragment.this.forumTopics.size(); i++) {
                this.hashes.add(Integer.valueOf(TopicsFragment.this.forumTopics.get(i).id));
            }
            if (z) {
                DiffUtil.calculateDiff(new DiffUtil.Callback() { // from class: org.telegram.ui.TopicsFragment.Adapter.1
                    @Override // androidx.recyclerview.widget.DiffUtil.Callback
                    public boolean areContentsTheSame(int i2, int i3) {
                        return false;
                    }

                    @Override // androidx.recyclerview.widget.DiffUtil.Callback
                    public int getOldListSize() {
                        return arrayList.size();
                    }

                    @Override // androidx.recyclerview.widget.DiffUtil.Callback
                    public int getNewListSize() {
                        return Adapter.this.hashes.size();
                    }

                    @Override // androidx.recyclerview.widget.DiffUtil.Callback
                    public boolean areItemsTheSame(int i2, int i3) {
                        return ObjectsCompat$$ExternalSyntheticBackport0.m(Adapter.this.hashes.get(i3), arrayList.get(i2));
                    }
                }).dispatchUpdatesTo(this);
            } else {
                super.notifyDataSetChanged();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class TopicDialogCell extends DialogCell {
        private AnimatedEmojiDrawable animatedEmojiDrawable;
        boolean attached;
        private boolean closed;
        public boolean drawDivider;
        private Drawable forumIcon;
        public int position;

        public TopicDialogCell(DialogsActivity dialogsActivity, Context context, boolean z, boolean z2) {
            super(dialogsActivity, context, z, z2);
            this.position = -1;
            this.drawAvatar = false;
            this.messagePaddingStart = 50;
            this.chekBoxPaddingTop = 24.0f;
            this.heightDefault = 64;
            this.heightThreeLines = 76;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.telegram.ui.Cells.DialogCell, android.view.View
        public void onDraw(Canvas canvas) {
            canvas.save();
            canvas.drawColor(TopicsFragment.this.getThemedColor("windowBackgroundWhite"));
            int i = -AndroidUtilities.dp(4.0f);
            this.translateY = i;
            canvas.translate(0.0f, i);
            super.onDraw(canvas);
            canvas.restore();
            if (this.drawDivider) {
                int dp = this.fullSeparator ? 0 : AndroidUtilities.dp(this.messagePaddingStart);
                if (LocaleController.isRTL) {
                    canvas.drawLine(0.0f, getMeasuredHeight() - 1, getMeasuredWidth() - dp, getMeasuredHeight() - 1, Theme.dividerPaint);
                } else {
                    canvas.drawLine(dp, getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, Theme.dividerPaint);
                }
            }
            if (this.animatedEmojiDrawable == null && this.forumIcon == null) {
                return;
            }
            int dp2 = AndroidUtilities.dp(10.0f);
            int dp3 = AndroidUtilities.dp(10.0f);
            int dp4 = AndroidUtilities.dp(28.0f);
            AnimatedEmojiDrawable animatedEmojiDrawable = this.animatedEmojiDrawable;
            if (animatedEmojiDrawable != null) {
                if (LocaleController.isRTL) {
                    animatedEmojiDrawable.setBounds((getWidth() - dp2) - dp4, dp3, getWidth() - dp2, dp4 + dp3);
                } else {
                    animatedEmojiDrawable.setBounds(dp2, dp3, dp2 + dp4, dp4 + dp3);
                }
                this.animatedEmojiDrawable.draw(canvas);
                return;
            }
            if (LocaleController.isRTL) {
                this.forumIcon.setBounds((getWidth() - dp2) - dp4, dp3, getWidth() - dp2, dp4 + dp3);
            } else {
                this.forumIcon.setBounds(dp2, dp3, dp2 + dp4, dp4 + dp3);
            }
            this.forumIcon.draw(canvas);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.telegram.ui.Cells.DialogCell, android.view.ViewGroup, android.view.View
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.attached = true;
            AnimatedEmojiDrawable animatedEmojiDrawable = this.animatedEmojiDrawable;
            if (animatedEmojiDrawable != null) {
                animatedEmojiDrawable.addView(this);
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.telegram.ui.Cells.DialogCell, android.view.ViewGroup, android.view.View
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.attached = false;
            AnimatedEmojiDrawable animatedEmojiDrawable = this.animatedEmojiDrawable;
            if (animatedEmojiDrawable != null) {
                animatedEmojiDrawable.removeView(this);
            }
        }

        public void setAnimatedEmojiDrawable(AnimatedEmojiDrawable animatedEmojiDrawable) {
            AnimatedEmojiDrawable animatedEmojiDrawable2 = this.animatedEmojiDrawable;
            if (animatedEmojiDrawable2 == animatedEmojiDrawable) {
                return;
            }
            if (animatedEmojiDrawable2 != null && this.attached) {
                animatedEmojiDrawable2.removeView(this);
            }
            this.animatedEmojiDrawable = animatedEmojiDrawable;
            if (animatedEmojiDrawable == null || !this.attached) {
                return;
            }
            animatedEmojiDrawable.addView(this);
        }

        public void setForumIcon(Drawable drawable) {
            this.forumIcon = drawable;
        }

        public void setTopicIcon(TLRPC$TL_forumTopic tLRPC$TL_forumTopic) {
            this.closed = tLRPC$TL_forumTopic != null && tLRPC$TL_forumTopic.closed;
            if (tLRPC$TL_forumTopic != null && tLRPC$TL_forumTopic.icon_emoji_id != 0) {
                setForumIcon(null);
                setAnimatedEmojiDrawable(new AnimatedEmojiDrawable(10, ((BaseFragment) TopicsFragment.this).currentAccount, tLRPC$TL_forumTopic.icon_emoji_id));
            } else {
                setAnimatedEmojiDrawable(null);
                setForumIcon(ForumUtilities.createTopicDrawable(tLRPC$TL_forumTopic));
            }
            buildLayout();
        }

        @Override // org.telegram.ui.Cells.DialogCell
        protected boolean drawLock2() {
            return this.closed;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideFloatingButton(boolean z, boolean z2) {
        if (this.floatingHidden == z) {
            return;
        }
        this.floatingHidden = z;
        float f = 1.0f;
        if (this.fragmentBeginToShow && z2) {
            AnimatorSet animatorSet = new AnimatorSet();
            float[] fArr = new float[2];
            fArr[0] = this.floatingButtonHideProgress;
            if (!this.floatingHidden) {
                f = 0.0f;
            }
            fArr[1] = f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.TopicsFragment$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    TopicsFragment.this.lambda$hideFloatingButton$16(valueAnimator);
                }
            });
            animatorSet.playTogether(ofFloat);
            animatorSet.setDuration(300L);
            animatorSet.setInterpolator(this.floatingInterpolator);
            animatorSet.start();
        } else {
            if (!z) {
                f = 0.0f;
            }
            this.floatingButtonHideProgress = f;
            this.floatingButtonTranslation = AndroidUtilities.dp(100.0f) * this.floatingButtonHideProgress;
            updateFloatingButtonOffset();
        }
        this.floatingButtonContainer.setClickable(!z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$hideFloatingButton$16(ValueAnimator valueAnimator) {
        this.floatingButtonHideProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.floatingButtonTranslation = AndroidUtilities.dp(100.0f) * this.floatingButtonHideProgress;
        updateFloatingButtonOffset();
    }

    private void updateFloatingButtonOffset() {
        this.floatingButtonContainer.setTranslationY(this.floatingButtonTranslation);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onBecomeFullyHidden() {
        ActionBar actionBar = this.actionBar;
        if (actionBar != null) {
            actionBar.closeSearchField();
        }
    }

    /* loaded from: classes3.dex */
    private class EmptyViewContainer extends FrameLayout {
        boolean increment;
        float progress;
        TextView textView;

        public EmptyViewContainer(TopicsFragment topicsFragment, Context context) {
            super(context);
            SpannableStringBuilder spannableStringBuilder;
            this.textView = new TextView(context);
            if (LocaleController.isRTL) {
                spannableStringBuilder = new SpannableStringBuilder("  ");
                spannableStringBuilder.setSpan(new ColoredImageSpan(R.drawable.attach_arrow_left), 0, 1, 0);
                spannableStringBuilder.append((CharSequence) LocaleController.getString("TapToCreateTopicHint", R.string.TapToCreateTopicHint));
            } else {
                spannableStringBuilder = new SpannableStringBuilder(LocaleController.getString("TapToCreateTopicHint", R.string.TapToCreateTopicHint));
                spannableStringBuilder.append((CharSequence) "  ");
                spannableStringBuilder.setSpan(new ColoredImageSpan(R.drawable.attach_arrow_right), spannableStringBuilder.length() - 1, spannableStringBuilder.length(), 0);
            }
            this.textView.setText(spannableStringBuilder);
            this.textView.setTextSize(1, 14.0f);
            this.textView.setLayerType(2, null);
            this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText", topicsFragment.getResourceProvider()));
            addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, 81, 86.0f, 0.0f, 86.0f, 32.0f));
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            int i = 1;
            if (this.increment) {
                float f = this.progress + 0.013333334f;
                this.progress = f;
                if (f > 1.0f) {
                    this.increment = false;
                    this.progress = 1.0f;
                }
            } else {
                float f2 = this.progress - 0.013333334f;
                this.progress = f2;
                if (f2 < 0.0f) {
                    this.increment = true;
                    this.progress = 0.0f;
                }
            }
            TextView textView = this.textView;
            float interpolation = CubicBezierInterpolator.DEFAULT.getInterpolation(this.progress) * AndroidUtilities.dp(8.0f);
            if (LocaleController.isRTL) {
                i = -1;
            }
            textView.setTranslationX(interpolation * i);
            invalidate();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean isLightStatusBar() {
        if (this.searchingNotEmpty) {
            return ColorUtils.calculateLuminance(Theme.getColor("windowBackgroundWhite")) > 0.699999988079071d;
        }
        return super.isLightStatusBar();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class SearchContainer extends FrameLayout {
        boolean canLoadMore;
        StickerEmptyView emptyView;
        FlickerLoadingView flickerLoadingView;
        boolean isLoading;
        RecyclerItemsEnterAnimator itemsEnterAnimator;
        LinearLayoutManager layoutManager;
        int messagesEndRow;
        int messagesHeaderRow;
        boolean messagesIsLoading;
        int messagesStartRow;
        RecyclerListView recyclerView;
        int rowCount;
        SearchAdapter searchAdapter;
        ArrayList<MessageObject> searchResultMessages;
        ArrayList<TLRPC$TL_forumTopic> searchResultTopics;
        Runnable searchRunnable;
        String searchString;
        int topicsEndRow;
        int topicsHeaderRow;
        int topicsStartRow;

        public SearchContainer(Context context) {
            super(context);
            this.searchString = "empty";
            this.searchResultTopics = new ArrayList<>();
            this.searchResultMessages = new ArrayList<>();
            RecyclerListView recyclerListView = new RecyclerListView(context);
            this.recyclerView = recyclerListView;
            SearchAdapter searchAdapter = new SearchAdapter();
            this.searchAdapter = searchAdapter;
            recyclerListView.setAdapter(searchAdapter);
            RecyclerListView recyclerListView2 = this.recyclerView;
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            this.layoutManager = linearLayoutManager;
            recyclerListView2.setLayoutManager(linearLayoutManager);
            this.recyclerView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.TopicsFragment$SearchContainer$$ExternalSyntheticLambda3
                @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
                public final void onItemClick(View view, int i) {
                    TopicsFragment.SearchContainer.this.lambda$new$0(view, i);
                }
            });
            this.recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener(TopicsFragment.this) { // from class: org.telegram.ui.TopicsFragment.SearchContainer.1
                @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                    super.onScrolled(recyclerView, i, i2);
                    SearchContainer searchContainer = SearchContainer.this;
                    if (searchContainer.canLoadMore) {
                        int findLastVisibleItemPosition = searchContainer.layoutManager.findLastVisibleItemPosition() + 5;
                        SearchContainer searchContainer2 = SearchContainer.this;
                        if (findLastVisibleItemPosition >= searchContainer2.rowCount) {
                            searchContainer2.loadMessages(searchContainer2.searchString);
                        }
                    }
                    if (TopicsFragment.this.searching) {
                        if (i == 0 && i2 == 0) {
                            return;
                        }
                        AndroidUtilities.hideKeyboard(TopicsFragment.this.searchItem.getSearchField());
                    }
                }
            });
            FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context);
            this.flickerLoadingView = flickerLoadingView;
            flickerLoadingView.setViewType(7);
            this.flickerLoadingView.showDate(false);
            this.flickerLoadingView.setUseHeaderOffset(true);
            StickerEmptyView stickerEmptyView = new StickerEmptyView(context, this.flickerLoadingView, 1);
            this.emptyView = stickerEmptyView;
            stickerEmptyView.title.setText(LocaleController.getString("NoResult", R.string.NoResult));
            this.emptyView.subtitle.setVisibility(8);
            this.emptyView.setVisibility(8);
            this.emptyView.addView(this.flickerLoadingView, 0);
            this.emptyView.setAnimateLayoutChange(true);
            this.recyclerView.setEmptyView(this.emptyView);
            this.recyclerView.setAnimateEmptyView(true, 0);
            addView(this.emptyView);
            addView(this.recyclerView);
            updateRows();
            RecyclerItemsEnterAnimator recyclerItemsEnterAnimator = new RecyclerItemsEnterAnimator(this.recyclerView, true);
            this.itemsEnterAnimator = recyclerItemsEnterAnimator;
            this.recyclerView.setItemsEnterAnimator(recyclerItemsEnterAnimator);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(View view, int i) {
            if (view instanceof TopicSearchCell) {
                TopicsFragment topicsFragment = TopicsFragment.this;
                ForumUtilities.openTopic(topicsFragment, topicsFragment.chatId, ((TopicSearchCell) view).getTopic(), 0);
            } else if (!(view instanceof TopicDialogCell)) {
            } else {
                TopicDialogCell topicDialogCell = (TopicDialogCell) view;
                TopicsFragment topicsFragment2 = TopicsFragment.this;
                ForumUtilities.openTopic(topicsFragment2, topicsFragment2.chatId, topicDialogCell.forumTopic, topicDialogCell.getMessageId());
            }
        }

        public void setSearchString(final String str) {
            if (this.searchString.equals(str)) {
                return;
            }
            this.searchString = str;
            Runnable runnable = this.searchRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.searchRunnable = null;
            }
            AndroidUtilities.updateViewVisibilityAnimated(TopicsFragment.this.searchContainer, !TextUtils.isEmpty(str), 1.0f, true);
            this.messagesIsLoading = false;
            this.canLoadMore = false;
            this.searchResultTopics.clear();
            this.searchResultMessages.clear();
            updateRows();
            if (TextUtils.isEmpty(str)) {
                this.isLoading = false;
                return;
            }
            updateRows();
            this.isLoading = true;
            this.emptyView.showProgress(true, true);
            Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.TopicsFragment$SearchContainer$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    TopicsFragment.SearchContainer.this.lambda$setSearchString$1(str);
                }
            };
            this.searchRunnable = runnable2;
            AndroidUtilities.runOnUIThread(runnable2, 200L);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$setSearchString$1(String str) {
            String lowerCase = str.trim().toLowerCase();
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < TopicsFragment.this.forumTopics.size(); i++) {
                if (TopicsFragment.this.forumTopics.get(i).title.toLowerCase().contains(lowerCase)) {
                    arrayList.add(TopicsFragment.this.forumTopics.get(i));
                    TopicsFragment.this.forumTopics.get(i).searchQuery = lowerCase;
                }
            }
            this.searchResultTopics.clear();
            this.searchResultTopics.addAll(arrayList);
            updateRows();
            if (!this.searchResultTopics.isEmpty()) {
                this.isLoading = false;
                this.itemsEnterAnimator.showItemsAnimated(0);
            }
            loadMessages(str);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void loadMessages(final String str) {
            if (this.messagesIsLoading) {
                return;
            }
            TLRPC$TL_messages_search tLRPC$TL_messages_search = new TLRPC$TL_messages_search();
            tLRPC$TL_messages_search.peer = TopicsFragment.this.getMessagesController().getInputPeer(-TopicsFragment.this.chatId);
            tLRPC$TL_messages_search.filter = new TLRPC$TL_inputMessagesFilterEmpty();
            tLRPC$TL_messages_search.limit = 20;
            tLRPC$TL_messages_search.q = str;
            if (!this.searchResultMessages.isEmpty()) {
                ArrayList<MessageObject> arrayList = this.searchResultMessages;
                tLRPC$TL_messages_search.offset_id = arrayList.get(arrayList.size() - 1).getId();
            }
            this.messagesIsLoading = true;
            ConnectionsManager.getInstance(((BaseFragment) TopicsFragment.this).currentAccount).sendRequest(tLRPC$TL_messages_search, new RequestDelegate() { // from class: org.telegram.ui.TopicsFragment$SearchContainer$$ExternalSyntheticLambda2
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    TopicsFragment.SearchContainer.this.lambda$loadMessages$3(str, tLObject, tLRPC$TL_error);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$loadMessages$3(final String str, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.TopicsFragment$SearchContainer$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    TopicsFragment.SearchContainer.this.lambda$loadMessages$2(str, tLObject);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$loadMessages$2(String str, TLObject tLObject) {
            if (str.equals(this.searchString)) {
                int i = this.rowCount;
                boolean z = false;
                this.messagesIsLoading = false;
                this.isLoading = false;
                if (tLObject instanceof TLRPC$messages_Messages) {
                    TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
                    for (int i2 = 0; i2 < tLRPC$messages_Messages.messages.size(); i2++) {
                        MessageObject messageObject = new MessageObject(((BaseFragment) TopicsFragment.this).currentAccount, tLRPC$messages_Messages.messages.get(i2), false, false);
                        messageObject.setQuery(str);
                        this.searchResultMessages.add(messageObject);
                    }
                    updateRows();
                    if (this.searchResultMessages.size() < tLRPC$messages_Messages.count && !tLRPC$messages_Messages.messages.isEmpty()) {
                        z = true;
                    }
                    this.canLoadMore = z;
                } else {
                    this.canLoadMore = false;
                }
                if (this.rowCount == 0) {
                    this.emptyView.showProgress(this.isLoading, true);
                }
                this.itemsEnterAnimator.showItemsAnimated(i);
            }
        }

        private void updateRows() {
            this.topicsHeaderRow = -1;
            this.topicsStartRow = -1;
            this.topicsEndRow = -1;
            this.messagesHeaderRow = -1;
            this.messagesStartRow = -1;
            this.messagesEndRow = -1;
            this.rowCount = 0;
            if (!this.searchResultTopics.isEmpty()) {
                int i = this.rowCount;
                int i2 = i + 1;
                this.rowCount = i2;
                this.topicsHeaderRow = i;
                this.topicsStartRow = i2;
                int size = i2 + this.searchResultTopics.size();
                this.rowCount = size;
                this.topicsEndRow = size;
            }
            if (!this.searchResultMessages.isEmpty()) {
                int i3 = this.rowCount;
                int i4 = i3 + 1;
                this.rowCount = i4;
                this.messagesHeaderRow = i3;
                this.messagesStartRow = i4;
                int size2 = i4 + this.searchResultMessages.size();
                this.rowCount = size2;
                this.messagesEndRow = size2;
            }
            this.searchAdapter.notifyDataSetChanged();
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public class SearchAdapter extends RecyclerListView.SelectionAdapter {
            private SearchAdapter() {
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            /* renamed from: onCreateViewHolder */
            public RecyclerView.ViewHolder mo1813onCreateViewHolder(ViewGroup viewGroup, int i) {
                View graySectionCell;
                if (i == 1) {
                    graySectionCell = new GraySectionCell(viewGroup.getContext());
                } else if (i == 2) {
                    graySectionCell = new TopicSearchCell(viewGroup.getContext());
                } else if (i == 3) {
                    graySectionCell = new TopicDialogCell(null, viewGroup.getContext(), false, true);
                } else {
                    throw new RuntimeException("unsupported view type");
                }
                graySectionCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                return new RecyclerListView.Holder(graySectionCell);
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                int i2 = 1;
                if (getItemViewType(i) == 1) {
                    GraySectionCell graySectionCell = (GraySectionCell) viewHolder.itemView;
                    if (i == SearchContainer.this.topicsHeaderRow) {
                        graySectionCell.setText(LocaleController.getString("Topics", R.string.Topics));
                    }
                    if (i == SearchContainer.this.messagesHeaderRow) {
                        graySectionCell.setText(LocaleController.getString("SearchMessages", R.string.SearchMessages));
                    }
                }
                boolean z = false;
                if (getItemViewType(i) == 2) {
                    SearchContainer searchContainer = SearchContainer.this;
                    TopicSearchCell topicSearchCell = (TopicSearchCell) viewHolder.itemView;
                    topicSearchCell.setTopic(searchContainer.searchResultTopics.get(i - searchContainer.topicsStartRow));
                    topicSearchCell.drawDivider = i != SearchContainer.this.topicsEndRow - 1;
                }
                if (getItemViewType(i) == 3) {
                    SearchContainer searchContainer2 = SearchContainer.this;
                    MessageObject messageObject = searchContainer2.searchResultMessages.get(i - searchContainer2.messagesStartRow);
                    TopicDialogCell topicDialogCell = (TopicDialogCell) viewHolder.itemView;
                    if (i != SearchContainer.this.messagesEndRow - 1) {
                        z = true;
                    }
                    topicDialogCell.drawDivider = z;
                    int topicId = MessageObject.getTopicId(messageObject.messageOwner);
                    if (topicId != 0) {
                        i2 = topicId;
                    }
                    TLRPC$TL_forumTopic findTopic = TopicsFragment.this.topicsController.findTopic(TopicsFragment.this.chatId, i2);
                    if (findTopic == null) {
                        FileLog.d("cant find topic " + i2);
                        return;
                    }
                    topicDialogCell.setForumTopic(findTopic, messageObject.getDialogId(), messageObject, false);
                    topicDialogCell.setPinForced(findTopic.pinned);
                    topicDialogCell.setTopicIcon(findTopic);
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public int getItemViewType(int i) {
                SearchContainer searchContainer = SearchContainer.this;
                if (i == searchContainer.messagesHeaderRow || i == searchContainer.topicsHeaderRow) {
                    return 1;
                }
                if (i >= searchContainer.topicsStartRow && i < searchContainer.topicsEndRow) {
                    return 2;
                }
                return (i < searchContainer.messagesStartRow || i >= searchContainer.messagesEndRow) ? 0 : 3;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public int getItemCount() {
                SearchContainer searchContainer = SearchContainer.this;
                if (searchContainer.isLoading) {
                    return 0;
                }
                return searchContainer.rowCount;
            }

            @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
            public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                return viewHolder.getItemViewType() == 3 || viewHolder.getItemViewType() == 2;
            }
        }
    }

    public void setOnTopicSelectedListener(OnTopicSelectedListener onTopicSelectedListener) {
        this.onTopicSelectedListener = onTopicSelectedListener;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        getMessagesController().getTopicsController().onTopicFragmentResume(this.chatId);
        Bulletin.addDelegate(this, new Bulletin.Delegate() { // from class: org.telegram.ui.TopicsFragment.19
            @Override // org.telegram.ui.Components.Bulletin.Delegate
            public /* synthetic */ int getTopOffset(int i) {
                return Bulletin.Delegate.CC.$default$getTopOffset(this, i);
            }

            @Override // org.telegram.ui.Components.Bulletin.Delegate
            public /* synthetic */ void onBottomOffsetChange(float f) {
                Bulletin.Delegate.CC.$default$onBottomOffsetChange(this, f);
            }

            @Override // org.telegram.ui.Components.Bulletin.Delegate
            public /* synthetic */ void onHide(Bulletin bulletin) {
                Bulletin.Delegate.CC.$default$onHide(this, bulletin);
            }

            @Override // org.telegram.ui.Components.Bulletin.Delegate
            public /* synthetic */ void onShow(Bulletin bulletin) {
                Bulletin.Delegate.CC.$default$onShow(this, bulletin);
            }

            @Override // org.telegram.ui.Components.Bulletin.Delegate
            public int getBottomOffset(int i) {
                if (TopicsFragment.this.bottomOverlayChatText == null || TopicsFragment.this.bottomOverlayChatText.getVisibility() != 0) {
                    return 0;
                }
                return TopicsFragment.this.bottomOverlayChatText.getMeasuredHeight();
            }
        });
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onPause() {
        super.onPause();
        getMessagesController().getTopicsController().onTopicFragmentPause(this.chatId);
        Bulletin.removeDelegate(this);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void prepareFragmentToSlide(boolean z, boolean z2) {
        if (!z && z2) {
            this.isSlideBackTransition = true;
            setFragmentIsSliding(true);
            return;
        }
        this.slideBackTransitionAnimator = null;
        this.isSlideBackTransition = false;
        setFragmentIsSliding(false);
        setSlideTransitionProgress(1.0f);
    }

    private void setFragmentIsSliding(boolean z) {
        if (SharedConfig.getDevicePerformanceClass() == 0) {
            return;
        }
        SizeNotifierFrameLayout sizeNotifierFrameLayout = this.contentView;
        if (sizeNotifierFrameLayout != null) {
            if (z) {
                sizeNotifierFrameLayout.setLayerType(2, null);
                sizeNotifierFrameLayout.setClipChildren(false);
                sizeNotifierFrameLayout.setClipToPadding(false);
            } else {
                sizeNotifierFrameLayout.setLayerType(0, null);
                sizeNotifierFrameLayout.setClipChildren(true);
                sizeNotifierFrameLayout.setClipToPadding(true);
            }
        }
        this.contentView.requestLayout();
        this.actionBar.requestLayout();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onSlideProgress(boolean z, float f) {
        if (SharedConfig.getDevicePerformanceClass() != 0 && this.isSlideBackTransition && this.slideBackTransitionAnimator == null) {
            setSlideTransitionProgress(f);
        }
    }

    private void setSlideTransitionProgress(float f) {
        if (SharedConfig.getDevicePerformanceClass() == 0) {
            return;
        }
        this.slideFragmentProgress = f;
        View view = this.fragmentView;
        if (view != null) {
            view.invalidate();
        }
        RecyclerListView recyclerListView = this.recyclerListView;
        if (recyclerListView == null) {
            return;
        }
        float f2 = 1.0f - ((1.0f - this.slideFragmentProgress) * 0.05f);
        recyclerListView.setPivotX(0.0f);
        recyclerListView.setPivotY(0.0f);
        recyclerListView.setScaleX(f2);
        recyclerListView.setScaleY(f2);
        this.topView.setPivotX(0.0f);
        this.topView.setPivotY(0.0f);
        this.topView.setScaleX(f2);
        this.topView.setScaleY(f2);
        this.actionBar.setPivotX(0.0f);
        this.actionBar.setPivotY(0.0f);
        this.actionBar.setScaleX(f2);
        this.actionBar.setScaleY(f2);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationStart(boolean z, boolean z2) {
        super.onTransitionAnimationStart(z, z2);
        this.transitionAnimationIndex = getNotificationCenter().setAnimationInProgress(this.transitionAnimationIndex, new int[]{NotificationCenter.topicsDidLoaded});
        this.transitionAnimationGlobalIndex = NotificationCenter.getGlobalInstance().setAnimationInProgress(this.transitionAnimationGlobalIndex, new int[0]);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        View view;
        super.onTransitionAnimationEnd(z, z2);
        if (z && (view = this.blurredView) != null) {
            if (view.getParent() != null) {
                ((ViewGroup) this.blurredView.getParent()).removeView(this.blurredView);
            }
            this.blurredView.setBackground(null);
        }
        getNotificationCenter().onAnimationFinish(this.transitionAnimationIndex);
        NotificationCenter.getGlobalInstance().onAnimationFinish(this.transitionAnimationGlobalIndex);
        if (!z) {
            if (!this.opnendForSelect && !this.removeFragmentOnTransitionEnd) {
                return;
            }
            removeSelfFromStack();
            DialogsActivity dialogsActivity = this.dialogsActivity;
            if (dialogsActivity == null) {
                return;
            }
            dialogsActivity.removeSelfFromStack();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void drawOverlay(Canvas canvas, View view) {
        canvas.save();
        canvas.translate(this.contentView.getX(), this.contentView.getY());
        FragmentContextView fragmentContextView = this.fragmentContextView;
        if (fragmentContextView != null && fragmentContextView.isCallStyle()) {
            canvas.save();
            canvas.translate(this.fragmentContextView.getX(), this.topView.getY() + this.fragmentContextView.getY());
            this.fragmentContextView.setDrawOverlay(true);
            this.fragmentContextView.draw(canvas);
            this.fragmentContextView.setDrawOverlay(false);
            canvas.restore();
            view.invalidate();
        }
        canvas.restore();
    }

    private void prepareBlurBitmap() {
        if (this.blurredView == null || this.parentLayout == null || SharedConfig.useLNavigation) {
            return;
        }
        int measuredWidth = (int) (this.fragmentView.getMeasuredWidth() / 6.0f);
        int measuredHeight = (int) (this.fragmentView.getMeasuredHeight() / 6.0f);
        Bitmap createBitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        canvas.scale(0.16666667f, 0.16666667f);
        this.parentLayout.getView().draw(canvas);
        Utilities.stackBlurBitmap(createBitmap, Math.max(7, Math.max(measuredWidth, measuredHeight) / 180));
        this.blurredView.setBackground(new BitmapDrawable(createBitmap));
        this.blurredView.setAlpha(0.0f);
        if (this.blurredView.getParent() != null) {
            ((ViewGroup) this.blurredView.getParent()).removeView(this.blurredView);
        }
        this.parentLayout.getOverlayContainerView().addView(this.blurredView, LayoutHelper.createFrame(-1, -1.0f));
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onBackPressed() {
        if (!this.selectedTopics.isEmpty()) {
            clearSelectedTopics();
            return false;
        }
        return super.onBackPressed();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationProgress(boolean z, float f) {
        View view = this.blurredView;
        if (view == null || view.getVisibility() != 0) {
            return;
        }
        if (z) {
            this.blurredView.setAlpha(1.0f - f);
        } else {
            this.blurredView.setAlpha(f);
        }
    }
}
