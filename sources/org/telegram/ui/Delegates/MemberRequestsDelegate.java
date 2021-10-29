package org.telegram.ui.Delegates;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Point;
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
import org.telegram.tgnet.TLRPC$TL_chatInviteImporter;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_chatInviteImporters;
import org.telegram.tgnet.TLRPC$TL_messages_hideChatJoinRequest;
import org.telegram.tgnet.TLRPC$TL_updates;
import org.telegram.tgnet.TLRPC$User;
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
    private final ArrayList<TLRPC$TL_chatInviteImporter> allImporters = new ArrayList<>();
    private final long chatId;
    private final MemberRequestsController controller;
    private final int currentAccount;
    /* access modifiers changed from: private */
    public final List<TLRPC$TL_chatInviteImporter> currentImporters = new ArrayList();
    private StickerEmptyView emptyView;
    /* access modifiers changed from: private */
    public final BaseFragment fragment;
    /* access modifiers changed from: private */
    public boolean hasMore;
    private TLRPC$TL_chatInviteImporter importer;
    public final boolean isChannel;
    private boolean isDataLoaded;
    private boolean isFirstLoading = true;
    /* access modifiers changed from: private */
    public boolean isLoading;
    public boolean isNeedRestoreList;
    private boolean isSearchExpanded;
    /* access modifiers changed from: private */
    public boolean isShowLastItemDivider = true;
    /* access modifiers changed from: private */
    public final RecyclerView.OnScrollListener listScrollListener = new RecyclerView.OnScrollListener() {
        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (MemberRequestsDelegate.this.hasMore && !MemberRequestsDelegate.this.isLoading && linearLayoutManager != null) {
                if (MemberRequestsDelegate.this.adapter.getItemCount() - linearLayoutManager.findLastVisibleItemPosition() < 10) {
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
    public final LongSparseArray<TLRPC$User> users = new LongSparseArray<>();

    public MemberRequestsDelegate(BaseFragment baseFragment, long j, boolean z) {
        this.fragment = baseFragment;
        this.chatId = j;
        int currentAccount2 = baseFragment.getCurrentAccount();
        this.currentAccount = currentAccount2;
        this.isChannel = ChatObject.isChannelAndNotMegaGroup(j, currentAccount2);
        this.showSearchMenu = z;
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
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.fragment.getParentActivity());
            RecyclerListView recyclerListView = new RecyclerListView(this.fragment.getParentActivity());
            this.recyclerView = recyclerListView;
            recyclerListView.setAdapter(this.adapter);
            this.recyclerView.setLayoutManager(linearLayoutManager);
            this.recyclerView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new MemberRequestsDelegate$$ExternalSyntheticLambda9(this));
            this.recyclerView.setOnScrollListener(this.listScrollListener);
            this.recyclerView.setSelectorDrawableColor(Theme.getColor("listSelectorSDK21", this.fragment.getResourceProvider()));
            this.rootLayout.addView(this.recyclerView, -1, -1);
        }
        return this.rootLayout;
    }

    public void setShowLastItemDivider(boolean z) {
        this.isShowLastItemDivider = z;
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
            this.loadingView.setColors("windowBackgroundGray", "windowBackgroundWhite", (String) null);
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

    public void setRecyclerView(RecyclerListView recyclerListView) {
        this.recyclerView = recyclerListView;
        recyclerListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new MemberRequestsDelegate$$ExternalSyntheticLambda9(this));
        final RecyclerView.OnScrollListener onScrollListener = recyclerListView.getOnScrollListener();
        if (onScrollListener == null) {
            recyclerListView.setOnScrollListener(this.listScrollListener);
        } else {
            recyclerListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                    super.onScrollStateChanged(recyclerView, i);
                    onScrollListener.onScrollStateChanged(recyclerView, i);
                    MemberRequestsDelegate.this.listScrollListener.onScrollStateChanged(recyclerView, i);
                }

                public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                    super.onScrolled(recyclerView, i, i2);
                    onScrollListener.onScrolled(recyclerView, i, i2);
                    MemberRequestsDelegate.this.listScrollListener.onScrolled(recyclerView, i, i2);
                }
            });
        }
    }

    public void onItemClick(View view, int i) {
        if (view instanceof MemberRequestCell) {
            if (this.isSearchExpanded) {
                AndroidUtilities.hideKeyboard(this.fragment.getParentActivity().getCurrentFocus());
            }
            AndroidUtilities.runOnUIThread(new MemberRequestsDelegate$$ExternalSyntheticLambda4(this, (MemberRequestCell) view), this.isSearchExpanded ? 100 : 0);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onItemClick$1(MemberRequestCell memberRequestCell) {
        TLRPC$TL_chatInviteImporter importer2 = memberRequestCell.getImporter();
        this.importer = importer2;
        TLRPC$User tLRPC$User = this.users.get(importer2.user_id);
        if (tLRPC$User != null) {
            boolean z = false;
            if (tLRPC$User.photo == null) {
                this.isNeedRestoreList = true;
                this.fragment.dismissCurrentDialog();
                Bundle bundle = new Bundle();
                ProfileActivity profileActivity = new ProfileActivity(bundle);
                bundle.putLong("user_id", tLRPC$User.id);
                bundle.putBoolean("removeFragmentOnChatOpen", false);
                this.fragment.presentFragment(profileActivity);
                return;
            }
            Point point = AndroidUtilities.displaySize;
            if (point.x > point.y) {
                z = true;
            }
            if (this.previewDialog == null && !z) {
                PreviewDialog previewDialog2 = new PreviewDialog(this.fragment.getParentActivity(), (RecyclerListView) memberRequestCell.getParent(), this.fragment.getResourceProvider(), this.isChannel);
                this.previewDialog = previewDialog2;
                previewDialog2.setImporter(this.importer, memberRequestCell.getAvatarImageView());
                this.previewDialog.setOnDismissListener(new MemberRequestsDelegate$$ExternalSyntheticLambda0(this));
                this.previewDialog.show();
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onItemClick$0(DialogInterface dialogInterface) {
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

    public void setSearchExpanded(boolean z) {
        this.isSearchExpanded = z;
    }

    public void setQuery(String str) {
        if (this.searchRunnable != null) {
            Utilities.searchQueue.cancelRunnable(this.searchRunnable);
            this.searchRunnable = null;
        }
        if (this.searchRequestId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.searchRequestId, false);
            this.searchRequestId = 0;
        }
        this.query = str;
        if (!this.isDataLoaded || !this.allImporters.isEmpty()) {
            if (TextUtils.isEmpty(str)) {
                this.adapter.setItems(this.allImporters);
                if (str == null && this.showSearchMenu) {
                    this.fragment.getActionBar().createMenu().getItem(0).setVisibility(this.allImporters.isEmpty() ? 8 : 0);
                }
                RecyclerListView recyclerListView = this.recyclerView;
                if (recyclerListView != null) {
                    recyclerListView.setVisibility(0);
                }
                if (this.loadingView != null) {
                    setLoadingVisible(false, false);
                }
                StickerEmptyView stickerEmptyView = this.searchEmptyView;
                if (stickerEmptyView != null) {
                    stickerEmptyView.setVisibility(4);
                }
            } else {
                this.adapter.setItems(Collections.emptyList());
                setLoadingVisible(true, true);
                DispatchQueue dispatchQueue = Utilities.searchQueue;
                MemberRequestsDelegate$$ExternalSyntheticLambda1 memberRequestsDelegate$$ExternalSyntheticLambda1 = new MemberRequestsDelegate$$ExternalSyntheticLambda1(this);
                this.searchRunnable = memberRequestsDelegate$$ExternalSyntheticLambda1;
                dispatchQueue.postRunnable(memberRequestsDelegate$$ExternalSyntheticLambda1, 300);
            }
            if (str != null) {
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
        setLoadingVisible(false, false);
    }

    public void loadMembers() {
        TLRPC$TL_messages_chatInviteImporters cachedImporters;
        boolean z = true;
        if (this.isFirstLoading && (cachedImporters = this.controller.getCachedImporters(this.chatId)) != null) {
            this.isDataLoaded = true;
            onImportersLoaded(cachedImporters, (String) null, true, true);
            z = false;
        }
        AndroidUtilities.runOnUIThread(new MemberRequestsDelegate$$ExternalSyntheticLambda5(this, z));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadMembers$5(boolean z) {
        TLRPC$TL_chatInviteImporter tLRPC$TL_chatInviteImporter;
        boolean isEmpty = TextUtils.isEmpty(this.query);
        boolean z2 = this.currentImporters.isEmpty() || this.isFirstLoading;
        String str = this.query;
        this.isLoading = true;
        this.isFirstLoading = false;
        MemberRequestsDelegate$$ExternalSyntheticLambda2 memberRequestsDelegate$$ExternalSyntheticLambda2 = (!isEmpty || !z) ? null : new MemberRequestsDelegate$$ExternalSyntheticLambda2(this);
        if (isEmpty) {
            AndroidUtilities.runOnUIThread(memberRequestsDelegate$$ExternalSyntheticLambda2, 300);
        }
        if (isEmpty || this.currentImporters.isEmpty()) {
            tLRPC$TL_chatInviteImporter = null;
        } else {
            List<TLRPC$TL_chatInviteImporter> list = this.currentImporters;
            tLRPC$TL_chatInviteImporter = list.get(list.size() - 1);
        }
        this.searchRequestId = this.controller.getImporters(this.chatId, str, tLRPC$TL_chatInviteImporter, this.users, new MemberRequestsDelegate$$ExternalSyntheticLambda8(this, isEmpty, memberRequestsDelegate$$ExternalSyntheticLambda2, str, z2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadMembers$2() {
        setLoadingVisible(true, true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadMembers$4(boolean z, Runnable runnable, String str, boolean z2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new MemberRequestsDelegate$$ExternalSyntheticLambda6(this, z, runnable, str, tLRPC$TL_error, tLObject, z2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadMembers$3(boolean z, Runnable runnable, String str, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, boolean z2) {
        this.isLoading = false;
        this.isDataLoaded = true;
        if (z) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
        }
        setLoadingVisible(false, false);
        if (TextUtils.equals(str, this.query) && tLRPC$TL_error == null) {
            this.isDataLoaded = true;
            onImportersLoaded((TLRPC$TL_messages_chatInviteImporters) tLObject, str, z2, false);
        }
    }

    private void onImportersLoaded(TLRPC$TL_messages_chatInviteImporters tLRPC$TL_messages_chatInviteImporters, String str, boolean z, boolean z2) {
        boolean z3 = false;
        for (int i = 0; i < tLRPC$TL_messages_chatInviteImporters.users.size(); i++) {
            TLRPC$User tLRPC$User = tLRPC$TL_messages_chatInviteImporters.users.get(i);
            this.users.put(tLRPC$User.id, tLRPC$User);
        }
        if (z) {
            this.adapter.setItems(tLRPC$TL_messages_chatInviteImporters.importers);
        } else {
            this.adapter.appendItems(tLRPC$TL_messages_chatInviteImporters.importers);
        }
        if (TextUtils.isEmpty(str)) {
            if (this.allImporters.isEmpty()) {
                this.allImporters.addAll(tLRPC$TL_messages_chatInviteImporters.importers);
            }
            if (this.showSearchMenu) {
                this.fragment.getActionBar().createMenu().getItem(0).setVisibility(this.allImporters.isEmpty() ? 8 : 0);
            }
        }
        onImportersChanged(str, z2);
        if (this.currentImporters.size() < tLRPC$TL_messages_chatInviteImporters.count) {
            z3 = true;
        }
        this.hasMore = z3;
    }

    public void onAddClicked(TLRPC$TL_chatInviteImporter tLRPC$TL_chatInviteImporter) {
        hideChatJoinRequest(tLRPC$TL_chatInviteImporter, true);
    }

    public void onDismissClicked(TLRPC$TL_chatInviteImporter tLRPC$TL_chatInviteImporter) {
        hideChatJoinRequest(tLRPC$TL_chatInviteImporter, false);
    }

    /* access modifiers changed from: protected */
    public void onImportersChanged(String str, boolean z) {
        boolean z2;
        if (TextUtils.isEmpty(str)) {
            z2 = !this.allImporters.isEmpty() || z;
            StickerEmptyView stickerEmptyView = this.emptyView;
            if (stickerEmptyView != null) {
                stickerEmptyView.setVisibility(z2 ? 4 : 0);
            }
            StickerEmptyView stickerEmptyView2 = this.searchEmptyView;
            if (stickerEmptyView2 != null) {
                stickerEmptyView2.setVisibility(4);
            }
        } else {
            z2 = !this.currentImporters.isEmpty() || z;
            StickerEmptyView stickerEmptyView3 = this.emptyView;
            if (stickerEmptyView3 != null) {
                stickerEmptyView3.setVisibility(4);
            }
            StickerEmptyView stickerEmptyView4 = this.searchEmptyView;
            if (stickerEmptyView4 != null) {
                stickerEmptyView4.setVisibility(z2 ? 4 : 0);
            }
        }
        RecyclerListView recyclerListView = this.recyclerView;
        if (recyclerListView != null) {
            recyclerListView.setVisibility(z2 ? 0 : 4);
        }
        if (this.allImporters.isEmpty()) {
            StickerEmptyView stickerEmptyView5 = this.emptyView;
            if (stickerEmptyView5 != null) {
                stickerEmptyView5.setVisibility(0);
            }
            StickerEmptyView stickerEmptyView6 = this.searchEmptyView;
            if (stickerEmptyView6 != null) {
                stickerEmptyView6.setVisibility(4);
            }
            setLoadingVisible(false, false);
            if (this.isSearchExpanded && this.showSearchMenu) {
                this.fragment.getActionBar().createMenu().closeSearchField(true);
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean hasAllImporters() {
        return !this.allImporters.isEmpty();
    }

    private void hideChatJoinRequest(TLRPC$TL_chatInviteImporter tLRPC$TL_chatInviteImporter, boolean z) {
        TLRPC$User tLRPC$User = this.users.get(tLRPC$TL_chatInviteImporter.user_id);
        if (tLRPC$User != null) {
            TLRPC$TL_messages_hideChatJoinRequest tLRPC$TL_messages_hideChatJoinRequest = new TLRPC$TL_messages_hideChatJoinRequest();
            tLRPC$TL_messages_hideChatJoinRequest.approved = z;
            tLRPC$TL_messages_hideChatJoinRequest.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(-this.chatId);
            tLRPC$TL_messages_hideChatJoinRequest.user_id = MessagesController.getInstance(this.currentAccount).getInputUser(tLRPC$User);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_hideChatJoinRequest, new MemberRequestsDelegate$$ExternalSyntheticLambda7(this, tLRPC$TL_chatInviteImporter, z, tLRPC$User, tLRPC$TL_messages_hideChatJoinRequest));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$hideChatJoinRequest$7(TLRPC$TL_chatInviteImporter tLRPC$TL_chatInviteImporter, boolean z, TLRPC$User tLRPC$User, TLRPC$TL_messages_hideChatJoinRequest tLRPC$TL_messages_hideChatJoinRequest, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            MessagesController.getInstance(this.currentAccount).processUpdates((TLRPC$TL_updates) tLObject, false);
        }
        AndroidUtilities.runOnUIThread(new MemberRequestsDelegate$$ExternalSyntheticLambda3(this, tLRPC$TL_error, tLObject, tLRPC$TL_chatInviteImporter, z, tLRPC$User, tLRPC$TL_messages_hideChatJoinRequest));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$hideChatJoinRequest$6(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, TLRPC$TL_chatInviteImporter tLRPC$TL_chatInviteImporter, boolean z, TLRPC$User tLRPC$User, TLRPC$TL_messages_hideChatJoinRequest tLRPC$TL_messages_hideChatJoinRequest) {
        String str;
        int i = 0;
        if (tLRPC$TL_error == null) {
            TLRPC$TL_updates tLRPC$TL_updates = (TLRPC$TL_updates) tLObject;
            if (!tLRPC$TL_updates.chats.isEmpty()) {
                MessagesController.getInstance(this.currentAccount).loadFullChat(tLRPC$TL_updates.chats.get(0).id, 0, true);
            }
            int i2 = 0;
            while (true) {
                if (i2 >= this.allImporters.size()) {
                    break;
                } else if (this.allImporters.get(i2).user_id == tLRPC$TL_chatInviteImporter.user_id) {
                    this.allImporters.remove(i2);
                    break;
                } else {
                    i2++;
                }
            }
            this.adapter.removeItem(tLRPC$TL_chatInviteImporter);
            onImportersChanged(this.query, false);
            if (z) {
                Bulletin.MultiLineLayout multiLineLayout = new Bulletin.MultiLineLayout(this.fragment.getParentActivity(), this.fragment.getResourceProvider());
                multiLineLayout.imageView.setRoundRadius(AndroidUtilities.dp(15.0f));
                multiLineLayout.imageView.setForUserOrChat(tLRPC$User, new AvatarDrawable(tLRPC$User));
                String firstName = UserObject.getFirstName(tLRPC$User);
                if (this.isChannel) {
                    str = LocaleController.formatString("HasBeenAddedToChannel", NUM, firstName);
                } else {
                    str = LocaleController.formatString("HasBeenAddedToGroup", NUM, firstName);
                }
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
                int indexOf = str.indexOf(firstName);
                spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), indexOf, firstName.length() + indexOf, 18);
                multiLineLayout.textView.setText(spannableStringBuilder);
                Bulletin.make(this.fragment, (Bulletin.Layout) multiLineLayout, 2750).show();
            }
            ActionBarMenu createMenu = this.fragment.getActionBar().createMenu();
            if (TextUtils.isEmpty(this.query) && this.showSearchMenu) {
                ActionBarMenuItem item = createMenu.getItem(0);
                if (this.allImporters.isEmpty()) {
                    i = 8;
                }
                item.setVisibility(i);
                return;
            }
            return;
        }
        AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, this.fragment, tLRPC$TL_messages_hideChatJoinRequest, new Object[0]);
    }

    /* access modifiers changed from: private */
    public void hidePreview() {
        this.previewDialog.dismiss();
        this.importer = null;
    }

    private void setLoadingVisible(boolean z, boolean z2) {
        FlickerLoadingView flickerLoadingView = this.loadingView;
        if (flickerLoadingView != null) {
            int i = 0;
            if (z2) {
                flickerLoadingView.setVisibility(0);
                this.loadingView.animate().alpha(z ? 1.0f : 0.0f).setDuration(150).start();
                return;
            }
            if (!z) {
                i = 4;
            }
            flickerLoadingView.setVisibility(i);
        }
    }

    private class Adapter extends RecyclerListView.SelectionAdapter {
        private Adapter() {
        }

        public RecyclerListView.Holder onCreateViewHolder(ViewGroup viewGroup, int i) {
            MemberRequestCell memberRequestCell;
            if (i == 1) {
                View view = new View(viewGroup.getContext());
                view.setBackground(Theme.getThemedDrawable(viewGroup.getContext(), NUM, "windowBackgroundGrayShadow"));
                memberRequestCell = view;
            } else if (i == 2) {
                memberRequestCell = new View(this, viewGroup.getContext()) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int i, int i2) {
                        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(52.0f), NUM));
                    }
                };
            } else if (i != 3) {
                Context context = viewGroup.getContext();
                MemberRequestsDelegate memberRequestsDelegate = MemberRequestsDelegate.this;
                MemberRequestCell memberRequestCell2 = new MemberRequestCell(context, memberRequestsDelegate, memberRequestsDelegate.isChannel);
                memberRequestCell2.setBackgroundColor(Theme.getColor("windowBackgroundWhite", MemberRequestsDelegate.this.fragment.getResourceProvider()));
                memberRequestCell = memberRequestCell2;
            } else {
                memberRequestCell = new View(viewGroup.getContext());
            }
            return new RecyclerListView.Holder(memberRequestCell);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                MemberRequestCell memberRequestCell = (MemberRequestCell) viewHolder.itemView;
                int extraFirstHolders = i - extraFirstHolders();
                LongSparseArray access$600 = MemberRequestsDelegate.this.users;
                TLRPC$TL_chatInviteImporter tLRPC$TL_chatInviteImporter = (TLRPC$TL_chatInviteImporter) MemberRequestsDelegate.this.currentImporters.get(extraFirstHolders);
                boolean z = true;
                if (extraFirstHolders == MemberRequestsDelegate.this.currentImporters.size() - 1) {
                    z = false;
                }
                memberRequestCell.setData(access$600, tLRPC$TL_chatInviteImporter, z);
            } else if (viewHolder.getItemViewType() == 2) {
                viewHolder.itemView.requestLayout();
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 0;
        }

        public int getItemCount() {
            return extraFirstHolders() + MemberRequestsDelegate.this.currentImporters.size() + extraLastHolders();
        }

        public int getItemViewType(int i) {
            if (MemberRequestsDelegate.this.isShowLastItemDivider) {
                if (i != MemberRequestsDelegate.this.currentImporters.size() || MemberRequestsDelegate.this.currentImporters.isEmpty()) {
                    return 0;
                }
                return 1;
            } else if (i == 0) {
                return 2;
            } else {
                return i == getItemCount() - 1 ? 3 : 0;
            }
        }

        @SuppressLint({"NotifyDataSetChanged"})
        public void setItems(List<TLRPC$TL_chatInviteImporter> list) {
            MemberRequestsDelegate.this.currentImporters.clear();
            MemberRequestsDelegate.this.currentImporters.addAll(list);
            notifyDataSetChanged();
        }

        public void appendItems(List<TLRPC$TL_chatInviteImporter> list) {
            MemberRequestsDelegate.this.currentImporters.addAll(list);
            if (MemberRequestsDelegate.this.currentImporters.size() > list.size()) {
                notifyItemChanged((MemberRequestsDelegate.this.currentImporters.size() - list.size()) - 1);
            }
            notifyItemRangeInserted(MemberRequestsDelegate.this.currentImporters.size() - list.size(), list.size());
        }

        public void removeItem(TLRPC$TL_chatInviteImporter tLRPC$TL_chatInviteImporter) {
            int i = 0;
            while (true) {
                if (i >= MemberRequestsDelegate.this.currentImporters.size()) {
                    i = -1;
                    break;
                } else if (((TLRPC$TL_chatInviteImporter) MemberRequestsDelegate.this.currentImporters.get(i)).user_id == tLRPC$TL_chatInviteImporter.user_id) {
                    break;
                } else {
                    i++;
                }
            }
            if (i >= 0) {
                MemberRequestsDelegate.this.currentImporters.remove(i);
                notifyItemRemoved(i + extraFirstHolders());
                if (MemberRequestsDelegate.this.currentImporters.isEmpty()) {
                    notifyItemRemoved(1);
                }
            }
        }

        private int extraFirstHolders() {
            return MemberRequestsDelegate.this.isShowLastItemDivider ^ true ? 1 : 0;
        }

        private int extraLastHolders() {
            return (!MemberRequestsDelegate.this.isShowLastItemDivider || !MemberRequestsDelegate.this.currentImporters.isEmpty()) ? 1 : 0;
        }
    }

    private class PreviewDialog extends Dialog {
        /* access modifiers changed from: private */
        public float animationProgress;
        private ValueAnimator animator;
        private BitmapDrawable backgroundDrawable;
        /* access modifiers changed from: private */
        public final TextView bioText;
        /* access modifiers changed from: private */
        public final ViewGroup contentView;
        private BackupImageView imageView;
        private TLRPC$TL_chatInviteImporter importer;
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

        public PreviewDialog(Context context, RecyclerListView recyclerListView, Theme.ResourcesProvider resourcesProvider, boolean z) {
            super(context, NUM);
            int i;
            String str;
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
                    public boolean onDown(MotionEvent motionEvent) {
                        return true;
                    }

                    public boolean onSingleTapUp(MotionEvent motionEvent) {
                        if (!(PreviewDialog.this.pagerShadowDrawable.getBounds().contains((int) motionEvent.getX(), (int) motionEvent.getY()) || (((float) PreviewDialog.this.popupLayout.getLeft()) < motionEvent.getX() && motionEvent.getX() < ((float) PreviewDialog.this.popupLayout.getRight()) && ((float) PreviewDialog.this.popupLayout.getTop()) < motionEvent.getY() && motionEvent.getY() < ((float) PreviewDialog.this.popupLayout.getBottom())))) {
                            PreviewDialog.this.dismiss();
                        }
                        return super.onSingleTapUp(motionEvent);
                    }
                });
                private final RectF rectF = new RectF();

                @SuppressLint({"ClickableViewAccessibility"})
                public boolean onTouchEvent(MotionEvent motionEvent) {
                    return this.gestureDetector.onTouchEvent(motionEvent);
                }

                /* access modifiers changed from: protected */
                public void onMeasure(int i, int i2) {
                    setWillNotDraw(false);
                    super.onMeasure(i, i2);
                    int min = Math.min(getMeasuredWidth(), getMeasuredHeight());
                    double measuredHeight = (double) getMeasuredHeight();
                    Double.isNaN(measuredHeight);
                    int min2 = Math.min(min, (int) (measuredHeight * 0.66d)) - (AndroidUtilities.dp(12.0f) * 2);
                    int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(min2, Integer.MIN_VALUE);
                    PreviewDialog.this.viewPager.measure(makeMeasureSpec, makeMeasureSpec);
                    PreviewDialog.this.pagerIndicator.measure(makeMeasureSpec, makeMeasureSpec);
                    int makeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(min2 - (AndroidUtilities.dp(16.0f) * 2), NUM);
                    PreviewDialog.this.nameText.measure(makeMeasureSpec2, View.MeasureSpec.makeMeasureSpec(0, 0));
                    PreviewDialog.this.bioText.measure(makeMeasureSpec2, View.MeasureSpec.makeMeasureSpec(0, 0));
                    PreviewDialog.this.popupLayout.measure(View.MeasureSpec.makeMeasureSpec(PreviewDialog.this.viewPager.getMeasuredWidth() + (PreviewDialog.this.shadowPaddingLeft * 2), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(0, 0));
                }

                /* access modifiers changed from: protected */
                public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                    int height = (getHeight() - PreviewDialog.this.getContentHeight()) / 2;
                    int width = (getWidth() - PreviewDialog.this.viewPager.getMeasuredWidth()) / 2;
                    PreviewDialog.this.viewPager.layout(width, height, PreviewDialog.this.viewPager.getMeasuredWidth() + width, PreviewDialog.this.viewPager.getMeasuredHeight() + height);
                    PreviewDialog.this.pagerIndicator.layout(PreviewDialog.this.viewPager.getLeft(), PreviewDialog.this.viewPager.getTop(), PreviewDialog.this.viewPager.getRight(), PreviewDialog.this.viewPager.getTop() + PreviewDialog.this.pagerIndicator.getMeasuredHeight());
                    int measuredHeight = height + PreviewDialog.this.viewPager.getMeasuredHeight() + AndroidUtilities.dp(12.0f);
                    PreviewDialog.this.nameText.layout(PreviewDialog.this.viewPager.getLeft() + AndroidUtilities.dp(16.0f), measuredHeight, PreviewDialog.this.viewPager.getRight() - AndroidUtilities.dp(16.0f), PreviewDialog.this.nameText.getMeasuredHeight() + measuredHeight);
                    int measuredHeight2 = measuredHeight + PreviewDialog.this.nameText.getMeasuredHeight();
                    int i5 = 8;
                    if (PreviewDialog.this.bioText.getVisibility() != 8) {
                        int dp = measuredHeight2 + AndroidUtilities.dp(4.0f);
                        PreviewDialog.this.bioText.layout(PreviewDialog.this.nameText.getLeft(), dp, PreviewDialog.this.nameText.getRight(), PreviewDialog.this.bioText.getMeasuredHeight() + dp);
                        measuredHeight2 = dp + PreviewDialog.this.bioText.getMeasuredHeight();
                    }
                    int dp2 = measuredHeight2 + AndroidUtilities.dp(12.0f);
                    PreviewDialog.this.pagerShadowDrawable.setBounds(PreviewDialog.this.viewPager.getLeft() - PreviewDialog.this.shadowPaddingLeft, PreviewDialog.this.viewPager.getTop() - PreviewDialog.this.shadowPaddingTop, PreviewDialog.this.viewPager.getRight() + PreviewDialog.this.shadowPaddingLeft, PreviewDialog.this.shadowPaddingTop + dp2);
                    PreviewDialog.this.popupLayout.layout((PreviewDialog.this.viewPager.getRight() - PreviewDialog.this.popupLayout.getMeasuredWidth()) + PreviewDialog.this.shadowPaddingLeft, dp2, PreviewDialog.this.viewPager.getRight() + PreviewDialog.this.shadowPaddingLeft, PreviewDialog.this.popupLayout.getMeasuredHeight() + dp2);
                    ActionBarPopupWindow.ActionBarPopupWindowLayout access$1200 = PreviewDialog.this.popupLayout;
                    if (PreviewDialog.this.popupLayout.getBottom() < i4) {
                        i5 = 0;
                    }
                    access$1200.setVisibility(i5);
                    int dp3 = AndroidUtilities.dp(6.0f);
                    this.rectF.set((float) PreviewDialog.this.viewPager.getLeft(), (float) PreviewDialog.this.viewPager.getTop(), (float) PreviewDialog.this.viewPager.getRight(), (float) (PreviewDialog.this.viewPager.getTop() + (dp3 * 2)));
                    this.clipPath.reset();
                    float f = (float) dp3;
                    this.clipPath.addRoundRect(this.rectF, f, f, Path.Direction.CW);
                    this.rectF.set((float) i, (float) (PreviewDialog.this.viewPager.getTop() + dp3), (float) i3, (float) i4);
                    this.clipPath.addRect(this.rectF, Path.Direction.CW);
                }

                /* access modifiers changed from: protected */
                public void onSizeChanged(int i, int i2, int i3, int i4) {
                    super.onSizeChanged(i, i2, i3, i4);
                    Point point = AndroidUtilities.displaySize;
                    if (point.x > point.y) {
                        PreviewDialog.super.dismiss();
                    }
                    if (i != i3 && i2 != i4) {
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
                    if (PreviewDialog.this.animationProgress < 1.0f) {
                        canvas.save();
                        canvas.clipRect(0.0f, ((float) getHeight()) / 2.0f, (float) getWidth(), (float) getHeight());
                    }
                    PreviewDialog.this.pagerShadowDrawable.draw(canvas);
                    if (PreviewDialog.this.animationProgress < 1.0f) {
                        canvas.restore();
                    }
                    super.onDraw(canvas);
                }

                /* access modifiers changed from: protected */
                public boolean verifyDrawable(Drawable drawable) {
                    return drawable == PreviewDialog.this.pagerShadowDrawable || super.verifyDrawable(drawable);
                }
            };
            this.contentView = r3;
            setCancelable(true);
            r3.setVisibility(4);
            int color = Theme.getColor("actionBarDefaultSubmenuBackground", MemberRequestsDelegate.this.fragment.getResourceProvider());
            mutate.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
            mutate.setCallback(r3);
            Rect rect = new Rect();
            mutate.getPadding(rect);
            this.shadowPaddingTop = rect.top;
            this.shadowPaddingLeft = rect.left;
            ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(context, resourcesProvider);
            this.popupLayout = actionBarPopupWindowLayout;
            actionBarPopupWindowLayout.setBackgroundColor(color);
            r3.addView(actionBarPopupWindowLayout);
            AnonymousClass1 r5 = new AvatarPreviewPagerIndicator(this, getContext(), MemberRequestsDelegate.this) {
                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    if (this.profileGalleryView.getRealCount() > 1) {
                        super.onDraw(canvas);
                    }
                }
            };
            this.pagerIndicator = r5;
            ProfileGalleryView profileGalleryView = new ProfileGalleryView(context, MemberRequestsDelegate.this.fragment.getActionBar(), recyclerListView, r5);
            this.viewPager = profileGalleryView;
            profileGalleryView.setCreateThumbFromParent(true);
            r3.addView(profileGalleryView);
            r5.setProfileGalleryView(profileGalleryView);
            r3.addView(r5);
            textView.setMaxLines(1);
            textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText", MemberRequestsDelegate.this.fragment.getResourceProvider()));
            textView.setTextSize(16.0f);
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            r3.addView(textView);
            textView2.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText", MemberRequestsDelegate.this.fragment.getResourceProvider()));
            textView2.setTextSize(14.0f);
            r3.addView(textView2);
            ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(context, true, false);
            actionBarMenuSubItem.setColors(Theme.getColor("actionBarDefaultSubmenuItem", resourcesProvider), Theme.getColor("actionBarDefaultSubmenuItemIcon", resourcesProvider));
            actionBarMenuSubItem.setSelectorColor(Theme.getColor("dialogButtonSelector", resourcesProvider));
            if (z) {
                i = NUM;
                str = "AddToChannel";
            } else {
                i = NUM;
                str = "AddToGroup";
            }
            actionBarMenuSubItem.setTextAndIcon(LocaleController.getString(str, i), NUM);
            actionBarMenuSubItem.setOnClickListener(new MemberRequestsDelegate$PreviewDialog$$ExternalSyntheticLambda2(this));
            actionBarPopupWindowLayout.addView(actionBarMenuSubItem);
            ActionBarMenuSubItem actionBarMenuSubItem2 = new ActionBarMenuSubItem(context, false, false);
            actionBarMenuSubItem2.setColors(Theme.getColor("actionBarDefaultSubmenuItem", resourcesProvider), Theme.getColor("actionBarDefaultSubmenuItemIcon", resourcesProvider));
            actionBarMenuSubItem2.setSelectorColor(Theme.getColor("dialogButtonSelector", resourcesProvider));
            actionBarMenuSubItem2.setTextAndIcon(LocaleController.getString("SendMessage", NUM), NUM);
            actionBarMenuSubItem2.setOnClickListener(new MemberRequestsDelegate$PreviewDialog$$ExternalSyntheticLambda3(this));
            actionBarPopupWindowLayout.addView(actionBarMenuSubItem2);
            ActionBarMenuSubItem actionBarMenuSubItem3 = new ActionBarMenuSubItem(context, false, true);
            actionBarMenuSubItem3.setColors(Theme.getColor("dialogTextRed2", resourcesProvider), Theme.getColor("dialogRedIcon", resourcesProvider));
            actionBarMenuSubItem3.setSelectorColor(Theme.getColor("dialogButtonSelector", resourcesProvider));
            actionBarMenuSubItem3.setTextAndIcon(LocaleController.getString("DismissRequest", NUM), NUM);
            actionBarMenuSubItem3.setOnClickListener(new MemberRequestsDelegate$PreviewDialog$$ExternalSyntheticLambda1(this));
            actionBarPopupWindowLayout.addView(actionBarMenuSubItem3);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(View view) {
            TLRPC$TL_chatInviteImporter tLRPC$TL_chatInviteImporter = this.importer;
            if (tLRPC$TL_chatInviteImporter != null) {
                MemberRequestsDelegate.this.onAddClicked(tLRPC$TL_chatInviteImporter);
            }
            MemberRequestsDelegate.this.hidePreview();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$1(View view) {
            if (this.importer != null) {
                MemberRequestsDelegate.this.isNeedRestoreList = true;
                super.dismiss();
                MemberRequestsDelegate.this.fragment.dismissCurrentDialog();
                Bundle bundle = new Bundle();
                bundle.putLong("user_id", this.importer.user_id);
                MemberRequestsDelegate.this.fragment.presentFragment(new ChatActivity(bundle));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$2(View view) {
            TLRPC$TL_chatInviteImporter tLRPC$TL_chatInviteImporter = this.importer;
            if (tLRPC$TL_chatInviteImporter != null) {
                MemberRequestsDelegate.this.onDismissClicked(tLRPC$TL_chatInviteImporter);
            }
            MemberRequestsDelegate.this.hidePreview();
        }

        /* access modifiers changed from: protected */
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            getWindow().setWindowAnimations(NUM);
            setContentView(this.contentView, new ViewGroup.LayoutParams(-1, -1));
            WindowManager.LayoutParams attributes = getWindow().getAttributes();
            attributes.width = -1;
            attributes.height = -1;
            attributes.dimAmount = 0.0f;
            int i = attributes.flags & -3;
            attributes.flags = i;
            attributes.gravity = 51;
            int i2 = Build.VERSION.SDK_INT;
            if (i2 >= 21) {
                attributes.flags = i | -NUM;
            }
            if (i2 >= 28) {
                attributes.layoutInDisplayCutoutMode = 1;
            }
            getWindow().setAttributes(attributes);
        }

        public void setImporter(TLRPC$TL_chatInviteImporter tLRPC$TL_chatInviteImporter, BackupImageView backupImageView) {
            this.importer = tLRPC$TL_chatInviteImporter;
            this.imageView = backupImageView;
            this.viewPager.setData(tLRPC$TL_chatInviteImporter.user_id, true);
            this.viewPager.setParentAvatarImage(backupImageView);
            this.nameText.setText(UserObject.getUserName((TLRPC$User) MemberRequestsDelegate.this.users.get(tLRPC$TL_chatInviteImporter.user_id)));
            this.bioText.setText(tLRPC$TL_chatInviteImporter.about);
            this.bioText.setVisibility(TextUtils.isEmpty(tLRPC$TL_chatInviteImporter.about) ? 8 : 0);
            this.contentView.requestLayout();
        }

        public void show() {
            super.show();
            AndroidUtilities.runOnUIThread(new MemberRequestsDelegate$PreviewDialog$$ExternalSyntheticLambda4(this), 80);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$show$3() {
            updateBackgroundBitmap();
            runAnimation(true);
        }

        public void dismiss() {
            runAnimation(false);
        }

        private void runAnimation(final boolean z) {
            ValueAnimator valueAnimator = this.animator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            int[] iArr = new int[2];
            this.imageView.getLocationOnScreen(iArr);
            float f = 1.0f;
            final float width = (((float) this.imageView.getWidth()) * 1.0f) / ((float) getContentWidth());
            float width2 = (((float) this.imageView.getWidth()) / 2.0f) / width;
            float f2 = 1.0f - width;
            float left = (float) (iArr[0] - (this.viewPager.getLeft() + ((int) ((((float) getContentWidth()) * f2) / 2.0f))));
            float top = (float) (iArr[1] - (this.viewPager.getTop() + ((int) ((((float) getContentHeight()) * f2) / 2.0f))));
            int i = (-this.popupLayout.getTop()) / 2;
            float[] fArr = new float[2];
            fArr[0] = z ? 0.0f : 1.0f;
            if (!z) {
                f = 0.0f;
            }
            fArr[1] = f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
            this.animator = ofFloat;
            ofFloat.addUpdateListener(new MemberRequestsDelegate$PreviewDialog$$ExternalSyntheticLambda0(this, width, left, top, width2, i));
            this.animator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationStart(Animator animator) {
                    super.onAnimationStart(animator);
                    PreviewDialog.this.contentView.setVisibility(0);
                    if (z) {
                        PreviewDialog.this.contentView.setScaleX(width);
                        PreviewDialog.this.contentView.setScaleY(width);
                    }
                }

                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    if (!z) {
                        PreviewDialog.super.dismiss();
                    }
                }
            });
            this.animator.setDuration(220);
            this.animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.animator.start();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$runAnimation$4(float f, float f2, float f3, float f4, int i, ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.animationProgress = floatValue;
            float f5 = f + ((1.0f - f) * floatValue);
            this.contentView.setScaleX(f5);
            this.contentView.setScaleY(f5);
            this.contentView.setTranslationX(f2 * (1.0f - this.animationProgress));
            this.contentView.setTranslationY(f3 * (1.0f - this.animationProgress));
            int i2 = (int) (f4 * (1.0f - this.animationProgress));
            this.viewPager.setRoundRadius(i2, i2);
            float clamp = MathUtils.clamp((this.animationProgress * 2.0f) - 1.0f, 0.0f, 1.0f);
            this.pagerShadowDrawable.setAlpha((int) (clamp * 255.0f));
            this.nameText.setAlpha(clamp);
            this.bioText.setAlpha(clamp);
            this.popupLayout.setTranslationY(((float) i) * (1.0f - this.animationProgress));
            this.popupLayout.setAlpha(clamp);
            BitmapDrawable bitmapDrawable = this.backgroundDrawable;
            if (bitmapDrawable != null) {
                bitmapDrawable.setAlpha((int) (this.animationProgress * 255.0f));
            }
            this.pagerIndicator.setAlpha(clamp);
        }

        private Bitmap getBlurredBitmap() {
            int measuredWidth = (int) (((float) this.contentView.getMeasuredWidth()) / 6.0f);
            int measuredHeight = (int) (((float) this.contentView.getMeasuredHeight()) / 6.0f);
            Bitmap createBitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            canvas.scale(0.16666667f, 0.16666667f);
            canvas.save();
            ((LaunchActivity) MemberRequestsDelegate.this.fragment.getParentActivity()).getActionBarLayout().draw(canvas);
            canvas.drawColor(ColorUtils.setAlphaComponent(-16777216, 76));
            Dialog visibleDialog = MemberRequestsDelegate.this.fragment.getVisibleDialog();
            if (visibleDialog != null) {
                visibleDialog.getWindow().getDecorView().draw(canvas);
            }
            Utilities.stackBlurBitmap(createBitmap, Math.max(7, Math.max(measuredWidth, measuredHeight) / 180));
            return createBitmap;
        }

        /* access modifiers changed from: private */
        public void updateBackgroundBitmap() {
            BitmapDrawable bitmapDrawable = this.backgroundDrawable;
            int alpha = (bitmapDrawable == null || Build.VERSION.SDK_INT < 19) ? 255 : bitmapDrawable.getAlpha();
            BitmapDrawable bitmapDrawable2 = new BitmapDrawable(getContext().getResources(), getBlurredBitmap());
            this.backgroundDrawable = bitmapDrawable2;
            bitmapDrawable2.setAlpha(alpha);
            getWindow().setBackgroundDrawable(this.backgroundDrawable);
        }

        /* access modifiers changed from: private */
        public int getContentHeight() {
            int measuredHeight = this.viewPager.getMeasuredHeight() + AndroidUtilities.dp(12.0f) + this.nameText.getMeasuredHeight();
            if (this.bioText.getVisibility() != 8) {
                measuredHeight += AndroidUtilities.dp(4.0f) + this.bioText.getMeasuredHeight();
            }
            return measuredHeight + AndroidUtilities.dp(12.0f) + this.popupLayout.getMeasuredHeight();
        }

        private int getContentWidth() {
            return this.viewPager.getMeasuredWidth();
        }
    }
}
