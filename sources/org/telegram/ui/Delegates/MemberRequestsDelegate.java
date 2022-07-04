package org.telegram.ui.Delegates;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MemberRequestsController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.AvatarPreviewPagerIndicator;
import org.telegram.ui.Cells.MemberRequestCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ProfileGalleryView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.StickerEmptyView;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.ProfileActivity;

public class MemberRequestsDelegate implements MemberRequestCell.OnClickListener {
    /* access modifiers changed from: private */
    public final Adapter adapter = new Adapter();
    private final ArrayList<TLRPC.TL_chatInviteImporter> allImporters = new ArrayList<>();
    private final long chatId;
    private final MemberRequestsController controller;
    private final int currentAccount;
    /* access modifiers changed from: private */
    public final List<TLRPC.TL_chatInviteImporter> currentImporters = new ArrayList();
    private StickerEmptyView emptyView;
    /* access modifiers changed from: private */
    public final BaseFragment fragment;
    /* access modifiers changed from: private */
    public boolean hasMore;
    private TLRPC.TL_chatInviteImporter importer;
    public final boolean isChannel;
    private boolean isDataLoaded;
    private boolean isFirstLoading = true;
    /* access modifiers changed from: private */
    public boolean isLoading;
    public boolean isNeedRestoreList;
    private boolean isSearchExpanded;
    /* access modifiers changed from: private */
    public boolean isShowLastItemDivider = true;
    private final FrameLayout layoutContainer;
    /* access modifiers changed from: private */
    public final RecyclerView.OnScrollListener listScrollListener = new RecyclerView.OnScrollListener() {
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (MemberRequestsDelegate.this.hasMore && !MemberRequestsDelegate.this.isLoading && layoutManager != null) {
                if (MemberRequestsDelegate.this.adapter.getItemCount() - layoutManager.findLastVisibleItemPosition() < 10) {
                    MemberRequestsDelegate.this.loadMembers();
                }
            }
        }
    };
    private FlickerLoadingView loadingView;
    private PreviewDialog previewDialog;
    private String query;
    private RecyclerListView recyclerView;
    private FrameLayout rootLayout;
    private StickerEmptyView searchEmptyView;
    private int searchRequestId;
    private Runnable searchRunnable;
    private final boolean showSearchMenu;
    /* access modifiers changed from: private */
    public final LongSparseArray<TLRPC.User> users = new LongSparseArray<>();

    public MemberRequestsDelegate(BaseFragment fragment2, FrameLayout layoutContainer2, long chatId2, boolean showSearchMenu2) {
        this.fragment = fragment2;
        this.layoutContainer = layoutContainer2;
        this.chatId = chatId2;
        int currentAccount2 = fragment2.getCurrentAccount();
        this.currentAccount = currentAccount2;
        this.isChannel = ChatObject.isChannelAndNotMegaGroup(chatId2, currentAccount2);
        this.showSearchMenu = showSearchMenu2;
        this.controller = MemberRequestsController.getInstance(currentAccount2);
    }

    public FrameLayout getRootLayout() {
        if (this.rootLayout == null) {
            FrameLayout frameLayout = new FrameLayout(this.fragment.getParentActivity());
            this.rootLayout = frameLayout;
            frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray", this.fragment.getResourceProvider()));
            FlickerLoadingView loadingView2 = getLoadingView();
            this.loadingView = loadingView2;
            this.rootLayout.addView(loadingView2, -1, -1);
            StickerEmptyView searchEmptyView2 = getSearchEmptyView();
            this.searchEmptyView = searchEmptyView2;
            this.rootLayout.addView(searchEmptyView2, -1, -1);
            StickerEmptyView emptyView2 = getEmptyView();
            this.emptyView = emptyView2;
            this.rootLayout.addView(emptyView2, LayoutHelper.createFrame(-1, -1.0f));
            LinearLayoutManager layoutManager = new LinearLayoutManager(this.fragment.getParentActivity());
            RecyclerListView recyclerListView = new RecyclerListView(this.fragment.getParentActivity());
            this.recyclerView = recyclerListView;
            recyclerListView.setAdapter(this.adapter);
            this.recyclerView.setLayoutManager(layoutManager);
            this.recyclerView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new MemberRequestsDelegate$$ExternalSyntheticLambda9(this));
            this.recyclerView.setOnScrollListener(this.listScrollListener);
            this.recyclerView.setSelectorDrawableColor(Theme.getColor("listSelectorSDK21", this.fragment.getResourceProvider()));
            this.rootLayout.addView(this.recyclerView, -1, -1);
        }
        return this.rootLayout;
    }

    public void setShowLastItemDivider(boolean showLastItemDivider) {
        this.isShowLastItemDivider = showLastItemDivider;
    }

    public Adapter getAdapter() {
        return this.adapter;
    }

    public FlickerLoadingView getLoadingView() {
        if (this.loadingView == null) {
            FlickerLoadingView flickerLoadingView = new FlickerLoadingView(this.fragment.getParentActivity(), this.fragment.getResourceProvider());
            this.loadingView = flickerLoadingView;
            flickerLoadingView.setAlpha(0.0f);
            if (this.isShowLastItemDivider) {
                this.loadingView.setBackgroundColor(Theme.getColor("windowBackgroundWhite", this.fragment.getResourceProvider()));
            }
            this.loadingView.setColors("windowBackgroundWhite", "windowBackgroundGray", (String) null);
            this.loadingView.setViewType(15);
        }
        return this.loadingView;
    }

    public StickerEmptyView getEmptyView() {
        String str;
        int i;
        String str2;
        int i2;
        if (this.emptyView == null) {
            StickerEmptyView stickerEmptyView = new StickerEmptyView(this.fragment.getParentActivity(), (View) null, 2, this.fragment.getResourceProvider());
            this.emptyView = stickerEmptyView;
            TextView textView = stickerEmptyView.title;
            if (this.isChannel) {
                i = NUM;
                str = "NoSubscribeRequests";
            } else {
                i = NUM;
                str = "NoMemberRequests";
            }
            textView.setText(LocaleController.getString(str, i));
            TextView textView2 = this.emptyView.subtitle;
            if (this.isChannel) {
                i2 = NUM;
                str2 = "NoSubscribeRequestsDescription";
            } else {
                i2 = NUM;
                str2 = "NoMemberRequestsDescription";
            }
            textView2.setText(LocaleController.getString(str2, i2));
            this.emptyView.setAnimateLayoutChange(true);
            this.emptyView.setVisibility(8);
        }
        return this.emptyView;
    }

    public StickerEmptyView getSearchEmptyView() {
        if (this.searchEmptyView == null) {
            StickerEmptyView stickerEmptyView = new StickerEmptyView(this.fragment.getParentActivity(), (View) null, 1, this.fragment.getResourceProvider());
            this.searchEmptyView = stickerEmptyView;
            if (this.isShowLastItemDivider) {
                stickerEmptyView.setBackgroundColor(Theme.getColor("windowBackgroundWhite", this.fragment.getResourceProvider()));
            }
            this.searchEmptyView.title.setText(LocaleController.getString("NoResult", NUM));
            this.searchEmptyView.subtitle.setText(LocaleController.getString("SearchEmptyViewFilteredSubtitle2", NUM));
            this.searchEmptyView.setAnimateLayoutChange(true);
            this.searchEmptyView.setVisibility(8);
        }
        return this.searchEmptyView;
    }

    public void setRecyclerView(RecyclerListView recyclerView2) {
        this.recyclerView = recyclerView2;
        recyclerView2.setOnItemClickListener((RecyclerListView.OnItemClickListener) new MemberRequestsDelegate$$ExternalSyntheticLambda9(this));
        final RecyclerView.OnScrollListener currentScrollListener = recyclerView2.getOnScrollListener();
        if (currentScrollListener == null) {
            recyclerView2.setOnScrollListener(this.listScrollListener);
        } else {
            recyclerView2.setOnScrollListener(new RecyclerView.OnScrollListener() {
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    currentScrollListener.onScrollStateChanged(recyclerView, newState);
                    MemberRequestsDelegate.this.listScrollListener.onScrollStateChanged(recyclerView, newState);
                }

                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    currentScrollListener.onScrolled(recyclerView, dx, dy);
                    MemberRequestsDelegate.this.listScrollListener.onScrolled(recyclerView, dx, dy);
                }
            });
        }
    }

    public void onItemClick(View view, int position) {
        if (view instanceof MemberRequestCell) {
            if (this.isSearchExpanded) {
                AndroidUtilities.hideKeyboard(this.fragment.getParentActivity().getCurrentFocus());
            }
            AndroidUtilities.runOnUIThread(new MemberRequestsDelegate$$ExternalSyntheticLambda4(this, (MemberRequestCell) view), this.isSearchExpanded ? 100 : 0);
        }
    }

    /* renamed from: lambda$onItemClick$1$org-telegram-ui-Delegates-MemberRequestsDelegate  reason: not valid java name */
    public /* synthetic */ void m1635xf2d2b3f6(MemberRequestCell cell) {
        TLRPC.TL_chatInviteImporter importer2 = cell.getImporter();
        this.importer = importer2;
        TLRPC.User user = this.users.get(importer2.user_id);
        if (user != null) {
            this.fragment.getMessagesController().putUser(user, false);
            if (user.photo == null || (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y)) {
                this.isNeedRestoreList = true;
                this.fragment.dismissCurrentDialog();
                Bundle args = new Bundle();
                ProfileActivity profileActivity = new ProfileActivity(args);
                args.putLong("user_id", user.id);
                args.putBoolean("removeFragmentOnChatOpen", false);
                this.fragment.presentFragment(profileActivity);
            } else if (this.previewDialog == null) {
                PreviewDialog previewDialog2 = new PreviewDialog(this.fragment.getParentActivity(), (RecyclerListView) cell.getParent(), this.fragment.getResourceProvider(), this.isChannel);
                this.previewDialog = previewDialog2;
                previewDialog2.setImporter(this.importer, cell.getAvatarImageView());
                this.previewDialog.setOnDismissListener(new MemberRequestsDelegate$$ExternalSyntheticLambda0(this));
                this.previewDialog.show();
            }
        }
    }

    /* renamed from: lambda$onItemClick$0$org-telegram-ui-Delegates-MemberRequestsDelegate  reason: not valid java name */
    public /* synthetic */ void m1634x1290dd7(DialogInterface dialog) {
        this.previewDialog = null;
    }

    public boolean onBackPressed() {
        PreviewDialog previewDialog2 = this.previewDialog;
        if (previewDialog2 == null) {
            return true;
        }
        previewDialog2.dismiss();
        return false;
    }

    public void setSearchExpanded(boolean isExpanded) {
        this.isSearchExpanded = isExpanded;
    }

    public void setQuery(String query2) {
        if (this.searchRunnable != null) {
            Utilities.searchQueue.cancelRunnable(this.searchRunnable);
            this.searchRunnable = null;
        }
        int i = 0;
        if (this.searchRequestId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.searchRequestId, false);
            this.searchRequestId = 0;
        }
        this.query = query2;
        if (!this.isDataLoaded || !this.allImporters.isEmpty()) {
            if (TextUtils.isEmpty(query2)) {
                this.adapter.setItems(this.allImporters);
                setViewVisible(this.recyclerView, true, true);
                setViewVisible(this.loadingView, false, false);
                StickerEmptyView stickerEmptyView = this.searchEmptyView;
                if (stickerEmptyView != null) {
                    stickerEmptyView.setVisibility(4);
                }
                if (query2 == null && this.showSearchMenu) {
                    ActionBarMenuItem item = this.fragment.getActionBar().createMenu().getItem(0);
                    if (this.allImporters.isEmpty()) {
                        i = 8;
                    }
                    item.setVisibility(i);
                }
            } else {
                this.adapter.setItems(Collections.emptyList());
                setViewVisible(this.recyclerView, false, false);
                setViewVisible(this.loadingView, true, true);
                DispatchQueue dispatchQueue = Utilities.searchQueue;
                MemberRequestsDelegate$$ExternalSyntheticLambda2 memberRequestsDelegate$$ExternalSyntheticLambda2 = new MemberRequestsDelegate$$ExternalSyntheticLambda2(this);
                this.searchRunnable = memberRequestsDelegate$$ExternalSyntheticLambda2;
                dispatchQueue.postRunnable(memberRequestsDelegate$$ExternalSyntheticLambda2, 300);
            }
            if (query2 != null) {
                StickerEmptyView stickerEmptyView2 = this.emptyView;
                if (stickerEmptyView2 != null) {
                    stickerEmptyView2.setVisibility(4);
                }
                StickerEmptyView stickerEmptyView3 = this.searchEmptyView;
                if (stickerEmptyView3 != null) {
                    stickerEmptyView3.setVisibility(4);
                    return;
                }
                return;
            }
            return;
        }
        setViewVisible(this.loadingView, false, false);
    }

    public void loadMembers() {
        TLRPC.TL_messages_chatInviteImporters firstImporters;
        boolean isNeedShowLoading = true;
        if (this.isFirstLoading && (firstImporters = this.controller.getCachedImporters(this.chatId)) != null) {
            isNeedShowLoading = false;
            this.isDataLoaded = true;
            onImportersLoaded(firstImporters, (String) null, true, true);
        }
        AndroidUtilities.runOnUIThread(new MemberRequestsDelegate$$ExternalSyntheticLambda5(this, isNeedShowLoading));
    }

    /* renamed from: lambda$loadMembers$5$org-telegram-ui-Delegates-MemberRequestsDelegate  reason: not valid java name */
    public /* synthetic */ void m1633xbbbcvar_(boolean needShowLoading) {
        TLRPC.TL_chatInviteImporter lastInvitedUser;
        boolean isEmptyQuery = TextUtils.isEmpty(this.query);
        boolean isEmptyOffset = this.currentImporters.isEmpty() || this.isFirstLoading;
        String lastQuery = this.query;
        this.isLoading = true;
        this.isFirstLoading = false;
        MemberRequestsDelegate$$ExternalSyntheticLambda1 memberRequestsDelegate$$ExternalSyntheticLambda1 = (!isEmptyQuery || !needShowLoading) ? null : new MemberRequestsDelegate$$ExternalSyntheticLambda1(this);
        if (isEmptyQuery) {
            AndroidUtilities.runOnUIThread(memberRequestsDelegate$$ExternalSyntheticLambda1, 300);
        }
        if (isEmptyQuery || this.currentImporters.isEmpty()) {
            lastInvitedUser = null;
        } else {
            List<TLRPC.TL_chatInviteImporter> list = this.currentImporters;
            lastInvitedUser = list.get(list.size() - 1);
        }
        MemberRequestsDelegate$$ExternalSyntheticLambda1 memberRequestsDelegate$$ExternalSyntheticLambda12 = memberRequestsDelegate$$ExternalSyntheticLambda1;
        this.searchRequestId = this.controller.getImporters(this.chatId, lastQuery, lastInvitedUser, this.users, new MemberRequestsDelegate$$ExternalSyntheticLambda8(this, isEmptyQuery, memberRequestsDelegate$$ExternalSyntheticLambda1, lastQuery, isEmptyOffset));
    }

    /* renamed from: lambda$loadMembers$2$org-telegram-ui-Delegates-MemberRequestsDelegate  reason: not valid java name */
    public /* synthetic */ void m1630xe6CLASSNAMEb8() {
        setViewVisible(this.loadingView, true, true);
    }

    /* renamed from: lambda$loadMembers$4$org-telegram-ui-Delegates-MemberRequestsDelegate  reason: not valid java name */
    public /* synthetic */ void m1632xca134cf6(boolean isEmptyQuery, Runnable showLoadingRunnable, String lastQuery, boolean isEmptyOffset, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new MemberRequestsDelegate$$ExternalSyntheticLambda6(this, isEmptyQuery, showLoadingRunnable, lastQuery, error, response, isEmptyOffset));
    }

    /* renamed from: lambda$loadMembers$3$org-telegram-ui-Delegates-MemberRequestsDelegate  reason: not valid java name */
    public /* synthetic */ void m1631xd869a6d7(boolean isEmptyQuery, Runnable showLoadingRunnable, String lastQuery, TLRPC.TL_error error, TLObject response, boolean isEmptyOffset) {
        this.isLoading = false;
        this.isDataLoaded = true;
        if (isEmptyQuery) {
            AndroidUtilities.cancelRunOnUIThread(showLoadingRunnable);
        }
        setViewVisible(this.loadingView, false, false);
        if (TextUtils.equals(lastQuery, this.query) && error == null) {
            this.isDataLoaded = true;
            onImportersLoaded((TLRPC.TL_messages_chatInviteImporters) response, lastQuery, isEmptyOffset, false);
        }
    }

    private void onImportersLoaded(TLRPC.TL_messages_chatInviteImporters importers, String lastQuery, boolean isEmptyOffset, boolean fromCache) {
        for (int i = 0; i < importers.users.size(); i++) {
            TLRPC.User user = importers.users.get(i);
            this.users.put(user.id, user);
        }
        if (isEmptyOffset) {
            this.adapter.setItems(importers.importers);
        } else {
            this.adapter.appendItems(importers.importers);
        }
        boolean z = false;
        if (TextUtils.isEmpty(lastQuery)) {
            this.allImporters.clear();
            this.allImporters.addAll(importers.importers);
            if (this.showSearchMenu) {
                this.fragment.getActionBar().createMenu().getItem(0).setVisibility(this.allImporters.isEmpty() ? 8 : 0);
            }
        }
        onImportersChanged(lastQuery, fromCache, false);
        if (this.currentImporters.size() < importers.count) {
            z = true;
        }
        this.hasMore = z;
    }

    public void onAddClicked(TLRPC.TL_chatInviteImporter importer2) {
        hideChatJoinRequest(importer2, true);
    }

    public void onDismissClicked(TLRPC.TL_chatInviteImporter importer2) {
        hideChatJoinRequest(importer2, false);
    }

    public void setAdapterItemsEnabled(boolean adapterItemsEnabled) {
        int position;
        if (this.recyclerView != null && (position = this.adapter.extraFirstHolders()) >= 0 && position < this.recyclerView.getChildCount()) {
            this.recyclerView.getChildAt(position).setEnabled(adapterItemsEnabled);
        }
    }

    /* access modifiers changed from: protected */
    public void onImportersChanged(String query2, boolean fromCache, boolean fromHide) {
        boolean isListVisible;
        if (TextUtils.isEmpty(query2)) {
            isListVisible = !this.allImporters.isEmpty() || fromCache;
            StickerEmptyView stickerEmptyView = this.emptyView;
            if (stickerEmptyView != null) {
                stickerEmptyView.setVisibility(isListVisible ? 4 : 0);
            }
            StickerEmptyView stickerEmptyView2 = this.searchEmptyView;
            if (stickerEmptyView2 != null) {
                stickerEmptyView2.setVisibility(4);
            }
        } else {
            isListVisible = !this.currentImporters.isEmpty() || fromCache;
            StickerEmptyView stickerEmptyView3 = this.emptyView;
            if (stickerEmptyView3 != null) {
                stickerEmptyView3.setVisibility(4);
            }
            StickerEmptyView stickerEmptyView4 = this.searchEmptyView;
            if (stickerEmptyView4 != null) {
                stickerEmptyView4.setVisibility(isListVisible ? 4 : 0);
            }
        }
        setViewVisible(this.recyclerView, isListVisible, true);
        if (this.allImporters.isEmpty()) {
            StickerEmptyView stickerEmptyView5 = this.emptyView;
            if (stickerEmptyView5 != null) {
                stickerEmptyView5.setVisibility(0);
            }
            StickerEmptyView stickerEmptyView6 = this.searchEmptyView;
            if (stickerEmptyView6 != null) {
                stickerEmptyView6.setVisibility(4);
            }
            setViewVisible(this.loadingView, false, false);
            if (this.isSearchExpanded && this.showSearchMenu) {
                this.fragment.getActionBar().createMenu().closeSearchField(true);
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean hasAllImporters() {
        return !this.allImporters.isEmpty();
    }

    private void hideChatJoinRequest(TLRPC.TL_chatInviteImporter importer2, boolean isApproved) {
        TLRPC.User user = this.users.get(importer2.user_id);
        if (user != null) {
            TLRPC.TL_messages_hideChatJoinRequest req = new TLRPC.TL_messages_hideChatJoinRequest();
            req.approved = isApproved;
            req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(-this.chatId);
            req.user_id = MessagesController.getInstance(this.currentAccount).getInputUser(user);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MemberRequestsDelegate$$ExternalSyntheticLambda7(this, importer2, isApproved, user, req));
        }
    }

    /* renamed from: lambda$hideChatJoinRequest$7$org-telegram-ui-Delegates-MemberRequestsDelegate  reason: not valid java name */
    public /* synthetic */ void m1629xa1bba2bb(TLRPC.TL_chatInviteImporter importer2, boolean isApproved, TLRPC.User user, TLRPC.TL_messages_hideChatJoinRequest req, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            MessagesController.getInstance(this.currentAccount).processUpdates((TLRPC.TL_updates) response, false);
        }
        AndroidUtilities.runOnUIThread(new MemberRequestsDelegate$$ExternalSyntheticLambda3(this, error, response, importer2, isApproved, user, req));
    }

    /* renamed from: lambda$hideChatJoinRequest$6$org-telegram-ui-Delegates-MemberRequestsDelegate  reason: not valid java name */
    public /* synthetic */ void m1628xb011fc9c(TLRPC.TL_error error, TLObject response, TLRPC.TL_chatInviteImporter importer2, boolean isApproved, TLRPC.User user, TLRPC.TL_messages_hideChatJoinRequest req) {
        String message;
        TLRPC.TL_chatInviteImporter tL_chatInviteImporter = importer2;
        TLRPC.User user2 = user;
        int i = 0;
        if (error == null) {
            TLRPC.TL_updates updates = (TLRPC.TL_updates) response;
            if (!updates.chats.isEmpty()) {
                MessagesController.getInstance(this.currentAccount).loadFullChat(((TLRPC.Chat) updates.chats.get(0)).id, 0, true);
            }
            int i2 = 0;
            while (true) {
                if (i2 >= this.allImporters.size()) {
                    break;
                } else if (this.allImporters.get(i2).user_id == tL_chatInviteImporter.user_id) {
                    this.allImporters.remove(i2);
                    break;
                } else {
                    i2++;
                }
            }
            this.adapter.removeItem(tL_chatInviteImporter);
            onImportersChanged(this.query, false, true);
            if (isApproved) {
                Bulletin.MultiLineLayout layout = new Bulletin.MultiLineLayout(this.fragment.getParentActivity(), this.fragment.getResourceProvider());
                layout.imageView.setRoundRadius(AndroidUtilities.dp(15.0f));
                layout.imageView.setForUserOrChat(user2, new AvatarDrawable(user2));
                String userName = UserObject.getFirstName(user);
                if (this.isChannel) {
                    message = LocaleController.formatString("HasBeenAddedToChannel", NUM, userName);
                } else {
                    message = LocaleController.formatString("HasBeenAddedToGroup", NUM, userName);
                }
                SpannableStringBuilder stringBuilder = new SpannableStringBuilder(message);
                int start = message.indexOf(userName);
                stringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), start, userName.length() + start, 18);
                layout.textView.setText(stringBuilder);
                if (this.allImporters.isEmpty()) {
                    Bulletin.make(this.fragment, (Bulletin.Layout) layout, 2750).show();
                } else {
                    Bulletin.make(this.layoutContainer, (Bulletin.Layout) layout, 2750).show();
                }
            }
            ActionBarMenu menu = this.fragment.getActionBar().createMenu();
            if (TextUtils.isEmpty(this.query) && this.showSearchMenu) {
                ActionBarMenuItem item = menu.getItem(0);
                if (this.allImporters.isEmpty()) {
                    i = 8;
                }
                item.setVisibility(i);
            }
            TLRPC.TL_messages_hideChatJoinRequest tL_messages_hideChatJoinRequest = req;
            return;
        }
        AlertsCreator.processError(this.currentAccount, error, this.fragment, req, new Object[0]);
    }

    /* access modifiers changed from: private */
    public void hidePreview() {
        this.previewDialog.dismiss();
        this.importer = null;
    }

    private void setViewVisible(View view, boolean isVisible, boolean isAnimated) {
        if (view != null) {
            int i = 0;
            boolean isCurrentVisible = view.getVisibility() == 0;
            float targetAlpha = isVisible ? 1.0f : 0.0f;
            if (isVisible != isCurrentVisible || targetAlpha != view.getAlpha()) {
                if (isAnimated) {
                    if (isVisible) {
                        view.setAlpha(0.0f);
                    }
                    view.setVisibility(0);
                    view.animate().alpha(targetAlpha).setDuration(150).start();
                    return;
                }
                if (!isVisible) {
                    i = 4;
                }
                view.setVisibility(i);
            }
        }
    }

    private class Adapter extends RecyclerListView.SelectionAdapter {
        private Adapter() {
        }

        public RecyclerListView.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 1:
                    View view2 = new View(parent.getContext());
                    view2.setBackground(Theme.getThemedDrawable(parent.getContext(), NUM, "windowBackgroundGrayShadow"));
                    view = view2;
                    break;
                case 2:
                    view = new View(parent.getContext()) {
                        /* access modifiers changed from: protected */
                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(52.0f), NUM));
                        }
                    };
                    break;
                case 3:
                    view = new View(parent.getContext());
                    break;
                default:
                    Context context = parent.getContext();
                    MemberRequestsDelegate memberRequestsDelegate = MemberRequestsDelegate.this;
                    MemberRequestCell cell = new MemberRequestCell(context, memberRequestsDelegate, memberRequestsDelegate.isChannel);
                    cell.setBackgroundColor(Theme.getColor("windowBackgroundWhite", MemberRequestsDelegate.this.fragment.getResourceProvider()));
                    MemberRequestCell memberRequestCell = cell;
                    view = cell;
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 0) {
                MemberRequestCell cell = (MemberRequestCell) holder.itemView;
                int position2 = position - extraFirstHolders();
                LongSparseArray access$700 = MemberRequestsDelegate.this.users;
                TLRPC.TL_chatInviteImporter tL_chatInviteImporter = (TLRPC.TL_chatInviteImporter) MemberRequestsDelegate.this.currentImporters.get(position2);
                boolean z = true;
                if (position2 == MemberRequestsDelegate.this.currentImporters.size() - 1) {
                    z = false;
                }
                cell.setData(access$700, tL_chatInviteImporter, z);
            } else if (holder.getItemViewType() == 2) {
                holder.itemView.requestLayout();
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 0;
        }

        public int getItemCount() {
            return extraFirstHolders() + MemberRequestsDelegate.this.currentImporters.size() + extraLastHolders();
        }

        public int getItemViewType(int position) {
            if (MemberRequestsDelegate.this.isShowLastItemDivider) {
                if (position != MemberRequestsDelegate.this.currentImporters.size() || MemberRequestsDelegate.this.currentImporters.isEmpty()) {
                    return 0;
                }
                return 1;
            } else if (position == 0) {
                return 2;
            } else {
                if (position == getItemCount() - 1) {
                    return 3;
                }
                return 0;
            }
        }

        public void setItems(List<TLRPC.TL_chatInviteImporter> newItems) {
            MemberRequestsDelegate.this.currentImporters.clear();
            MemberRequestsDelegate.this.currentImporters.addAll(newItems);
            notifyDataSetChanged();
        }

        public void appendItems(List<TLRPC.TL_chatInviteImporter> newItems) {
            MemberRequestsDelegate.this.currentImporters.addAll(newItems);
            if (MemberRequestsDelegate.this.currentImporters.size() > newItems.size()) {
                notifyItemChanged((MemberRequestsDelegate.this.currentImporters.size() - newItems.size()) - 1);
            }
            notifyItemRangeInserted(MemberRequestsDelegate.this.currentImporters.size() - newItems.size(), newItems.size());
        }

        public void removeItem(TLRPC.TL_chatInviteImporter item) {
            int position = -1;
            int i = 0;
            while (true) {
                if (i >= MemberRequestsDelegate.this.currentImporters.size()) {
                    break;
                } else if (((TLRPC.TL_chatInviteImporter) MemberRequestsDelegate.this.currentImporters.get(i)).user_id == item.user_id) {
                    position = i;
                    break;
                } else {
                    i++;
                }
            }
            if (position >= 0) {
                MemberRequestsDelegate.this.currentImporters.remove(position);
                notifyItemRemoved(extraFirstHolders() + position);
                if (MemberRequestsDelegate.this.currentImporters.isEmpty()) {
                    notifyItemRemoved(1);
                }
            }
        }

        /* access modifiers changed from: private */
        public int extraFirstHolders() {
            return MemberRequestsDelegate.this.isShowLastItemDivider ^ true ? 1 : 0;
        }

        private int extraLastHolders() {
            return (!MemberRequestsDelegate.this.isShowLastItemDivider || !MemberRequestsDelegate.this.currentImporters.isEmpty()) ? 1 : 0;
        }
    }

    private class PreviewDialog extends Dialog {
        private float animationProgress;
        private ValueAnimator animator;
        private BitmapDrawable backgroundDrawable;
        /* access modifiers changed from: private */
        public final TextView bioText;
        /* access modifiers changed from: private */
        public final ViewGroup contentView;
        private BackupImageView imageView;
        private TLRPC.TL_chatInviteImporter importer;
        /* access modifiers changed from: private */
        public final TextView nameText;
        /* access modifiers changed from: private */
        public final AvatarPreviewPagerIndicator pagerIndicator;
        /* access modifiers changed from: private */
        public final Drawable pagerShadowDrawable;
        /* access modifiers changed from: private */
        public final ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout;
        /* access modifiers changed from: private */
        public final int shadowPaddingLeft;
        /* access modifiers changed from: private */
        public final int shadowPaddingTop;
        /* access modifiers changed from: private */
        public final ProfileGalleryView viewPager;

        public PreviewDialog(Context context, RecyclerListView parentListView, Theme.ResourcesProvider resourcesProvider, boolean isChannel) {
            super(context, NUM);
            String str;
            int i;
            Drawable mutate = getContext().getResources().getDrawable(NUM).mutate();
            this.pagerShadowDrawable = mutate;
            TextView textView = new TextView(getContext());
            this.nameText = textView;
            TextView textView2 = new TextView(getContext());
            this.bioText = textView2;
            AnonymousClass3 r3 = new ViewGroup(getContext()) {
                private final Path clipPath = new Path();
                private boolean firstSizeChange = true;
                private final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
                    public boolean onDown(MotionEvent e) {
                        return true;
                    }

                    public boolean onSingleTapUp(MotionEvent e) {
                        if (!(PreviewDialog.this.pagerShadowDrawable.getBounds().contains((int) e.getX(), (int) e.getY()) || (((float) PreviewDialog.this.popupLayout.getLeft()) < e.getX() && e.getX() < ((float) PreviewDialog.this.popupLayout.getRight()) && ((float) PreviewDialog.this.popupLayout.getTop()) < e.getY() && e.getY() < ((float) PreviewDialog.this.popupLayout.getBottom())))) {
                            PreviewDialog.this.dismiss();
                        }
                        return super.onSingleTapUp(e);
                    }
                });
                private final RectF rectF = new RectF();

                public boolean onTouchEvent(MotionEvent event) {
                    return this.gestureDetector.onTouchEvent(event);
                }

                /* access modifiers changed from: protected */
                public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    setWillNotDraw(false);
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                    int minSize = Math.min(getMeasuredWidth(), getMeasuredHeight());
                    double measuredHeight = (double) getMeasuredHeight();
                    Double.isNaN(measuredHeight);
                    int pagerSize = Math.min(minSize, (int) (measuredHeight * 0.66d)) - (AndroidUtilities.dp(12.0f) * 2);
                    int pagerSpec = View.MeasureSpec.makeMeasureSpec(pagerSize, Integer.MIN_VALUE);
                    PreviewDialog.this.viewPager.measure(pagerSpec, pagerSpec);
                    PreviewDialog.this.pagerIndicator.measure(pagerSpec, pagerSpec);
                    int textWidthSpec = View.MeasureSpec.makeMeasureSpec(pagerSize - (AndroidUtilities.dp(16.0f) * 2), NUM);
                    PreviewDialog.this.nameText.measure(textWidthSpec, View.MeasureSpec.makeMeasureSpec(0, 0));
                    PreviewDialog.this.bioText.measure(textWidthSpec, View.MeasureSpec.makeMeasureSpec(0, 0));
                    PreviewDialog.this.popupLayout.measure(View.MeasureSpec.makeMeasureSpec(PreviewDialog.this.viewPager.getMeasuredWidth() + (PreviewDialog.this.shadowPaddingLeft * 2), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(0, 0));
                }

                /* access modifiers changed from: protected */
                public void onLayout(boolean changed, int l, int t, int r, int b) {
                    int top = (getHeight() - PreviewDialog.this.getContentHeight()) / 2;
                    int left = (getWidth() - PreviewDialog.this.viewPager.getMeasuredWidth()) / 2;
                    PreviewDialog.this.viewPager.layout(left, top, PreviewDialog.this.viewPager.getMeasuredWidth() + left, PreviewDialog.this.viewPager.getMeasuredHeight() + top);
                    PreviewDialog.this.pagerIndicator.layout(PreviewDialog.this.viewPager.getLeft(), PreviewDialog.this.viewPager.getTop(), PreviewDialog.this.viewPager.getRight(), PreviewDialog.this.viewPager.getTop() + PreviewDialog.this.pagerIndicator.getMeasuredHeight());
                    int top2 = top + PreviewDialog.this.viewPager.getMeasuredHeight() + AndroidUtilities.dp(12.0f);
                    PreviewDialog.this.nameText.layout(PreviewDialog.this.viewPager.getLeft() + AndroidUtilities.dp(16.0f), top2, PreviewDialog.this.viewPager.getRight() - AndroidUtilities.dp(16.0f), PreviewDialog.this.nameText.getMeasuredHeight() + top2);
                    int top3 = top2 + PreviewDialog.this.nameText.getMeasuredHeight();
                    int i = 8;
                    if (PreviewDialog.this.bioText.getVisibility() != 8) {
                        int top4 = top3 + AndroidUtilities.dp(4.0f);
                        PreviewDialog.this.bioText.layout(PreviewDialog.this.nameText.getLeft(), top4, PreviewDialog.this.nameText.getRight(), PreviewDialog.this.bioText.getMeasuredHeight() + top4);
                        top3 = top4 + PreviewDialog.this.bioText.getMeasuredHeight();
                    }
                    int top5 = top3 + AndroidUtilities.dp(12.0f);
                    PreviewDialog.this.pagerShadowDrawable.setBounds(PreviewDialog.this.viewPager.getLeft() - PreviewDialog.this.shadowPaddingLeft, PreviewDialog.this.viewPager.getTop() - PreviewDialog.this.shadowPaddingTop, PreviewDialog.this.viewPager.getRight() + PreviewDialog.this.shadowPaddingLeft, PreviewDialog.this.shadowPaddingTop + top5);
                    PreviewDialog.this.popupLayout.layout((PreviewDialog.this.viewPager.getRight() - PreviewDialog.this.popupLayout.getMeasuredWidth()) + PreviewDialog.this.shadowPaddingLeft, top5, PreviewDialog.this.viewPager.getRight() + PreviewDialog.this.shadowPaddingLeft, PreviewDialog.this.popupLayout.getMeasuredHeight() + top5);
                    ActionBarPopupWindow.ActionBarPopupWindowLayout access$1300 = PreviewDialog.this.popupLayout;
                    if (PreviewDialog.this.popupLayout.getBottom() < b) {
                        i = 0;
                    }
                    access$1300.setVisibility(i);
                    int radius = AndroidUtilities.dp(6.0f);
                    this.rectF.set((float) PreviewDialog.this.viewPager.getLeft(), (float) PreviewDialog.this.viewPager.getTop(), (float) PreviewDialog.this.viewPager.getRight(), (float) (PreviewDialog.this.viewPager.getTop() + (radius * 2)));
                    this.clipPath.reset();
                    this.clipPath.addRoundRect(this.rectF, (float) radius, (float) radius, Path.Direction.CW);
                    this.rectF.set((float) l, (float) (PreviewDialog.this.viewPager.getTop() + radius), (float) r, (float) b);
                    this.clipPath.addRect(this.rectF, Path.Direction.CW);
                }

                /* access modifiers changed from: protected */
                public void onSizeChanged(int w, int h, int oldw, int oldh) {
                    super.onSizeChanged(w, h, oldw, oldh);
                    if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                        PreviewDialog.super.dismiss();
                    }
                    if (w != oldw && h != oldh) {
                        if (!this.firstSizeChange) {
                            PreviewDialog.this.updateBackgroundBitmap();
                        }
                        this.firstSizeChange = false;
                    }
                }

                /* access modifiers changed from: protected */
                public void dispatchDraw(Canvas canvas) {
                    canvas.save();
                    canvas.clipPath(this.clipPath);
                    super.dispatchDraw(canvas);
                    canvas.restore();
                }

                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    PreviewDialog.this.pagerShadowDrawable.draw(canvas);
                    super.onDraw(canvas);
                }

                /* access modifiers changed from: protected */
                public boolean verifyDrawable(Drawable who) {
                    return who == PreviewDialog.this.pagerShadowDrawable || super.verifyDrawable(who);
                }
            };
            this.contentView = r3;
            setCancelable(true);
            r3.setVisibility(4);
            int backgroundColor = Theme.getColor("actionBarDefaultSubmenuBackground", MemberRequestsDelegate.this.fragment.getResourceProvider());
            mutate.setColorFilter(new PorterDuffColorFilter(backgroundColor, PorterDuff.Mode.MULTIPLY));
            mutate.setCallback(r3);
            Rect paddingRect = new Rect();
            mutate.getPadding(paddingRect);
            this.shadowPaddingTop = paddingRect.top;
            this.shadowPaddingLeft = paddingRect.left;
            ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(context, resourcesProvider);
            this.popupLayout = actionBarPopupWindowLayout;
            actionBarPopupWindowLayout.setBackgroundColor(backgroundColor);
            r3.addView(actionBarPopupWindowLayout);
            AnonymousClass1 r7 = new AvatarPreviewPagerIndicator(getContext(), MemberRequestsDelegate.this) {
                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    if (this.profileGalleryView.getRealCount() > 1) {
                        super.onDraw(canvas);
                    }
                }
            };
            this.pagerIndicator = r7;
            ProfileGalleryView profileGalleryView = new ProfileGalleryView(context, MemberRequestsDelegate.this.fragment.getActionBar(), parentListView, r7);
            this.viewPager = profileGalleryView;
            profileGalleryView.setCreateThumbFromParent(true);
            r3.addView(profileGalleryView);
            r7.setProfileGalleryView(profileGalleryView);
            r3.addView(r7);
            textView.setMaxLines(1);
            textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText", MemberRequestsDelegate.this.fragment.getResourceProvider()));
            textView.setTextSize(16.0f);
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            r3.addView(textView);
            textView2.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText", MemberRequestsDelegate.this.fragment.getResourceProvider()));
            textView2.setTextSize(14.0f);
            r3.addView(textView2);
            ActionBarMenuSubItem addCell = new ActionBarMenuSubItem(context, true, false);
            addCell.setColors(Theme.getColor("actionBarDefaultSubmenuItem", resourcesProvider), Theme.getColor("actionBarDefaultSubmenuItemIcon", resourcesProvider));
            addCell.setSelectorColor(Theme.getColor("dialogButtonSelector", resourcesProvider));
            if (isChannel) {
                i = NUM;
                str = "AddToChannel";
            } else {
                i = NUM;
                str = "AddToGroup";
            }
            addCell.setTextAndIcon(LocaleController.getString(str, i), NUM);
            addCell.setOnClickListener(new MemberRequestsDelegate$PreviewDialog$$ExternalSyntheticLambda1(this));
            actionBarPopupWindowLayout.addView(addCell);
            ActionBarMenuSubItem sendMsgCell = new ActionBarMenuSubItem(context, false, false);
            sendMsgCell.setColors(Theme.getColor("actionBarDefaultSubmenuItem", resourcesProvider), Theme.getColor("actionBarDefaultSubmenuItemIcon", resourcesProvider));
            sendMsgCell.setSelectorColor(Theme.getColor("dialogButtonSelector", resourcesProvider));
            sendMsgCell.setTextAndIcon(LocaleController.getString("SendMessage", NUM), NUM);
            sendMsgCell.setOnClickListener(new MemberRequestsDelegate$PreviewDialog$$ExternalSyntheticLambda2(this));
            actionBarPopupWindowLayout.addView(sendMsgCell);
            ActionBarMenuSubItem dismissCell = new ActionBarMenuSubItem(context, false, true);
            dismissCell.setColors(Theme.getColor("dialogTextRed2", resourcesProvider), Theme.getColor("dialogRedIcon", resourcesProvider));
            dismissCell.setSelectorColor(Theme.getColor("dialogButtonSelector", resourcesProvider));
            dismissCell.setTextAndIcon(LocaleController.getString("DismissRequest", NUM), NUM);
            dismissCell.setOnClickListener(new MemberRequestsDelegate$PreviewDialog$$ExternalSyntheticLambda3(this));
            actionBarPopupWindowLayout.addView(dismissCell);
        }

        /* renamed from: lambda$new$0$org-telegram-ui-Delegates-MemberRequestsDelegate$PreviewDialog  reason: not valid java name */
        public /* synthetic */ void m1636x8a31fab9(View v) {
            TLRPC.TL_chatInviteImporter tL_chatInviteImporter = this.importer;
            if (tL_chatInviteImporter != null) {
                MemberRequestsDelegate.this.onAddClicked(tL_chatInviteImporter);
            }
            MemberRequestsDelegate.this.hidePreview();
        }

        /* renamed from: lambda$new$1$org-telegram-ui-Delegates-MemberRequestsDelegate$PreviewDialog  reason: not valid java name */
        public /* synthetic */ void m1637x892fe98(View v) {
            if (this.importer != null) {
                MemberRequestsDelegate.this.isNeedRestoreList = true;
                super.dismiss();
                MemberRequestsDelegate.this.fragment.dismissCurrentDialog();
                Bundle args = new Bundle();
                args.putLong("user_id", this.importer.user_id);
                MemberRequestsDelegate.this.fragment.presentFragment(new ChatActivity(args));
            }
        }

        /* renamed from: lambda$new$2$org-telegram-ui-Delegates-MemberRequestsDelegate$PreviewDialog  reason: not valid java name */
        public /* synthetic */ void m1638x86var_(View v) {
            TLRPC.TL_chatInviteImporter tL_chatInviteImporter = this.importer;
            if (tL_chatInviteImporter != null) {
                MemberRequestsDelegate.this.onDismissClicked(tL_chatInviteImporter);
            }
            MemberRequestsDelegate.this.hidePreview();
        }

        /* access modifiers changed from: protected */
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getWindow().setWindowAnimations(NUM);
            setContentView(this.contentView, new ViewGroup.LayoutParams(-1, -1));
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.width = -1;
            params.height = -1;
            params.dimAmount = 0.0f;
            params.flags &= -3;
            params.gravity = 51;
            if (Build.VERSION.SDK_INT >= 21) {
                params.flags |= -NUM;
            }
            if (Build.VERSION.SDK_INT >= 28) {
                params.layoutInDisplayCutoutMode = 1;
            }
            getWindow().setAttributes(params);
        }

        public void setImporter(TLRPC.TL_chatInviteImporter importer2, BackupImageView imageView2) {
            this.importer = importer2;
            this.imageView = imageView2;
            this.viewPager.setParentAvatarImage(imageView2);
            this.viewPager.setData(importer2.user_id, true);
            this.nameText.setText(UserObject.getUserName((TLRPC.User) MemberRequestsDelegate.this.users.get(importer2.user_id)));
            this.bioText.setText(importer2.about);
            this.bioText.setVisibility(TextUtils.isEmpty(importer2.about) ? 8 : 0);
            this.contentView.requestLayout();
        }

        public void show() {
            super.show();
            AndroidUtilities.runOnUIThread(new MemberRequestsDelegate$PreviewDialog$$ExternalSyntheticLambda4(this), 80);
        }

        /* renamed from: lambda$show$3$org-telegram-ui-Delegates-MemberRequestsDelegate$PreviewDialog  reason: not valid java name */
        public /* synthetic */ void m1640x8f2a92bf() {
            updateBackgroundBitmap();
            runAnimation(true);
        }

        public void dismiss() {
            runAnimation(false);
        }

        private void runAnimation(boolean show) {
            final boolean z = show;
            ValueAnimator valueAnimator = this.animator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            int[] location = new int[2];
            this.imageView.getLocationOnScreen(location);
            float f = 1.0f;
            final float fromScale = (((float) this.imageView.getWidth()) * 1.0f) / ((float) getContentWidth());
            float fromRadius = (((float) this.imageView.getWidth()) / 2.0f) / fromScale;
            float xFrom = (float) (location[0] - (this.viewPager.getLeft() + ((int) ((((float) getContentWidth()) * (1.0f - fromScale)) / 2.0f))));
            float yFrom = (float) (location[1] - (this.viewPager.getTop() + ((int) ((((float) getContentHeight()) * (1.0f - fromScale)) / 2.0f))));
            int popupLayoutTranslation = (-this.popupLayout.getTop()) / 2;
            float[] fArr = new float[2];
            fArr[0] = z ? 0.0f : 1.0f;
            if (!z) {
                f = 0.0f;
            }
            fArr[1] = f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
            this.animator = ofFloat;
            int[] iArr = location;
            MemberRequestsDelegate$PreviewDialog$$ExternalSyntheticLambda0 memberRequestsDelegate$PreviewDialog$$ExternalSyntheticLambda0 = r0;
            MemberRequestsDelegate$PreviewDialog$$ExternalSyntheticLambda0 memberRequestsDelegate$PreviewDialog$$ExternalSyntheticLambda02 = new MemberRequestsDelegate$PreviewDialog$$ExternalSyntheticLambda0(this, fromScale, xFrom, yFrom, fromRadius, popupLayoutTranslation);
            ofFloat.addUpdateListener(memberRequestsDelegate$PreviewDialog$$ExternalSyntheticLambda0);
            this.animator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    PreviewDialog.this.contentView.setVisibility(0);
                    if (z) {
                        PreviewDialog.this.contentView.setScaleX(fromScale);
                        PreviewDialog.this.contentView.setScaleY(fromScale);
                    }
                }

                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (!z) {
                        PreviewDialog.super.dismiss();
                    }
                }
            });
            this.animator.setDuration(220);
            this.animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.animator.start();
        }

        /* renamed from: lambda$runAnimation$4$org-telegram-ui-Delegates-MemberRequestsDelegate$PreviewDialog  reason: not valid java name */
        public /* synthetic */ void m1639x49c0d8e2(float fromScale, float xFrom, float yFrom, float fromRadius, int popupLayoutTranslation, ValueAnimator animation) {
            float floatValue = ((Float) animation.getAnimatedValue()).floatValue();
            this.animationProgress = floatValue;
            float scale = ((1.0f - fromScale) * floatValue) + fromScale;
            this.contentView.setScaleX(scale);
            this.contentView.setScaleY(scale);
            this.contentView.setTranslationX((1.0f - this.animationProgress) * xFrom);
            this.contentView.setTranslationY((1.0f - this.animationProgress) * yFrom);
            int roundRadius = (int) ((1.0f - this.animationProgress) * fromRadius);
            this.viewPager.setRoundRadius(roundRadius, roundRadius);
            float alpha = MathUtils.clamp((this.animationProgress * 2.0f) - 1.0f, 0.0f, 1.0f);
            this.pagerShadowDrawable.setAlpha((int) (alpha * 255.0f));
            this.nameText.setAlpha(alpha);
            this.bioText.setAlpha(alpha);
            this.popupLayout.setTranslationY(((float) popupLayoutTranslation) * (1.0f - this.animationProgress));
            this.popupLayout.setAlpha(alpha);
            BitmapDrawable bitmapDrawable = this.backgroundDrawable;
            if (bitmapDrawable != null) {
                bitmapDrawable.setAlpha((int) (this.animationProgress * 255.0f));
            }
            this.pagerIndicator.setAlpha(alpha);
        }

        private Bitmap getBlurredBitmap() {
            int width = (int) (((float) this.contentView.getMeasuredWidth()) / 6.0f);
            int height = (int) (((float) this.contentView.getMeasuredHeight()) / 6.0f);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.scale(1.0f / 6.0f, 1.0f / 6.0f);
            canvas.save();
            ((LaunchActivity) MemberRequestsDelegate.this.fragment.getParentActivity()).getActionBarLayout().draw(canvas);
            canvas.drawColor(ColorUtils.setAlphaComponent(-16777216, 76));
            Dialog dialog = MemberRequestsDelegate.this.fragment.getVisibleDialog();
            if (dialog != null) {
                dialog.getWindow().getDecorView().draw(canvas);
            }
            Utilities.stackBlurBitmap(bitmap, Math.max(7, Math.max(width, height) / 180));
            return bitmap;
        }

        /* access modifiers changed from: private */
        public void updateBackgroundBitmap() {
            int oldAlpha = 255;
            if (this.backgroundDrawable != null && Build.VERSION.SDK_INT >= 19) {
                oldAlpha = this.backgroundDrawable.getAlpha();
            }
            BitmapDrawable bitmapDrawable = new BitmapDrawable(getContext().getResources(), getBlurredBitmap());
            this.backgroundDrawable = bitmapDrawable;
            bitmapDrawable.setAlpha(oldAlpha);
            getWindow().setBackgroundDrawable(this.backgroundDrawable);
        }

        /* access modifiers changed from: private */
        public int getContentHeight() {
            int height = this.viewPager.getMeasuredHeight() + AndroidUtilities.dp(12.0f) + this.nameText.getMeasuredHeight();
            if (this.bioText.getVisibility() != 8) {
                height += AndroidUtilities.dp(4.0f) + this.bioText.getMeasuredHeight();
            }
            return height + AndroidUtilities.dp(12.0f) + this.popupLayout.getMeasuredHeight();
        }

        private int getContentWidth() {
            return this.viewPager.getMeasuredWidth();
        }
    }
}
