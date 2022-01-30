package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.internal.mlkit_language_id.zzdp$$ExternalSyntheticBackport0;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_availableReaction;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messagePeerReaction;
import org.telegram.tgnet.TLRPC$TL_messages_getMessageReactionsList;
import org.telegram.tgnet.TLRPC$TL_messages_messageReactionsList;
import org.telegram.tgnet.TLRPC$TL_peerUser;
import org.telegram.tgnet.TLRPC$TL_reactionCount;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.RecyclerListView;

public class ReactedUsersListView extends FrameLayout {
    /* access modifiers changed from: private */
    public RecyclerView.Adapter adapter;
    /* access modifiers changed from: private */
    public boolean canLoadMore = true;
    /* access modifiers changed from: private */
    public int currentAccount;
    private String filter;
    /* access modifiers changed from: private */
    public boolean isLoaded;
    /* access modifiers changed from: private */
    public boolean isLoading;
    private RecyclerListView listView;
    /* access modifiers changed from: private */
    public FlickerLoadingView loadingView;
    private MessageObject message;
    private String offset;
    private OnHeightChangedListener onHeightChangedListener;
    private OnProfileSelectedListener onProfileSelectedListener;
    private boolean onlySeenNow;
    /* access modifiers changed from: private */
    public List<TLRPC$TL_messagePeerReaction> userReactions = new ArrayList();
    /* access modifiers changed from: private */
    public LongSparseArray<TLRPC$User> users = new LongSparseArray<>();

    public interface OnHeightChangedListener {
        void onHeightChanged(ReactedUsersListView reactedUsersListView, int i);
    }

    public interface OnProfileSelectedListener {
        void onProfileSelected(ReactedUsersListView reactedUsersListView, long j);
    }

    public ReactedUsersListView(final Context context, Theme.ResourcesProvider resourcesProvider, int i, MessageObject messageObject, TLRPC$TL_reactionCount tLRPC$TL_reactionCount, boolean z) {
        super(context);
        String str;
        this.currentAccount = i;
        this.message = messageObject;
        if (tLRPC$TL_reactionCount == null) {
            str = null;
        } else {
            str = tLRPC$TL_reactionCount.reaction;
        }
        this.filter = str;
        this.listView = new RecyclerListView(context, resourcesProvider) {
            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, i2);
                ReactedUsersListView.this.updateHeight();
            }
        };
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        this.listView.setLayoutManager(linearLayoutManager);
        if (z) {
            this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(8.0f));
            this.listView.setClipToPadding(false);
        }
        if (Build.VERSION.SDK_INT >= 29) {
            this.listView.setVerticalScrollbarThumbDrawable(new ColorDrawable(Theme.getColor("listSelectorSDK21")));
        }
        RecyclerListView recyclerListView = this.listView;
        AnonymousClass2 r8 = new RecyclerView.Adapter() {
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                return new RecyclerListView.Holder(new ReactedUserHolderView(context));
            }

            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                ((ReactedUserHolderView) viewHolder.itemView).setUserReaction((TLRPC$TL_messagePeerReaction) ReactedUsersListView.this.userReactions.get(i));
            }

            public int getItemCount() {
                return ReactedUsersListView.this.userReactions.size();
            }
        };
        this.adapter = r8;
        recyclerListView.setAdapter(r8);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new ReactedUsersListView$$ExternalSyntheticLambda5(this));
        this.listView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                if (ReactedUsersListView.this.isLoaded && ReactedUsersListView.this.canLoadMore && !ReactedUsersListView.this.isLoading && linearLayoutManager.findLastVisibleItemPosition() >= (ReactedUsersListView.this.adapter.getItemCount() - 1) - ReactedUsersListView.this.getLoadCount()) {
                    ReactedUsersListView.this.load();
                }
            }
        });
        this.listView.setVerticalScrollBarEnabled(true);
        this.listView.setAlpha(0.0f);
        addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context, resourcesProvider);
        this.loadingView = flickerLoadingView;
        flickerLoadingView.setViewType(16);
        this.loadingView.setIsSingleCell(true);
        this.loadingView.setItemsCount(tLRPC$TL_reactionCount == null ? 6 : tLRPC$TL_reactionCount.count);
        addView(this.loadingView, LayoutHelper.createFrame(-1, -1.0f));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view, int i) {
        OnProfileSelectedListener onProfileSelectedListener2 = this.onProfileSelectedListener;
        if (onProfileSelectedListener2 != null) {
            onProfileSelectedListener2.onProfileSelected(this, MessageObject.getPeerId(this.userReactions.get(i).peer_id));
        }
    }

    @SuppressLint({"NotifyDataSetChanged"})
    public ReactedUsersListView setSeenUsers(List<TLRPC$User> list) {
        ArrayList arrayList = new ArrayList(list.size());
        for (TLRPC$User next : list) {
            if (this.users.get(next.id) == null) {
                this.users.put(next.id, next);
                TLRPC$TL_messagePeerReaction tLRPC$TL_messagePeerReaction = new TLRPC$TL_messagePeerReaction();
                tLRPC$TL_messagePeerReaction.reaction = null;
                TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                tLRPC$TL_messagePeerReaction.peer_id = tLRPC$TL_peerUser;
                tLRPC$TL_peerUser.user_id = next.id;
                arrayList.add(tLRPC$TL_messagePeerReaction);
            }
        }
        if (this.userReactions.isEmpty()) {
            this.onlySeenNow = true;
        }
        this.userReactions.addAll(arrayList);
        this.adapter.notifyDataSetChanged();
        updateHeight();
        return this;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!this.isLoaded && !this.isLoading) {
            load();
        }
    }

    /* access modifiers changed from: private */
    @SuppressLint({"NotifyDataSetChanged"})
    public void load() {
        this.isLoading = true;
        MessagesController instance = MessagesController.getInstance(this.currentAccount);
        TLRPC$TL_messages_getMessageReactionsList tLRPC$TL_messages_getMessageReactionsList = new TLRPC$TL_messages_getMessageReactionsList();
        tLRPC$TL_messages_getMessageReactionsList.peer = instance.getInputPeer(this.message.getDialogId());
        tLRPC$TL_messages_getMessageReactionsList.id = this.message.getId();
        tLRPC$TL_messages_getMessageReactionsList.limit = getLoadCount();
        String str = this.filter;
        tLRPC$TL_messages_getMessageReactionsList.reaction = str;
        String str2 = this.offset;
        tLRPC$TL_messages_getMessageReactionsList.offset = str2;
        if (str != null) {
            tLRPC$TL_messages_getMessageReactionsList.flags = 1 | tLRPC$TL_messages_getMessageReactionsList.flags;
        }
        if (str2 != null) {
            tLRPC$TL_messages_getMessageReactionsList.flags |= 2;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_getMessageReactionsList, new ReactedUsersListView$$ExternalSyntheticLambda4(this), 64);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$load$5(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject instanceof TLRPC$TL_messages_messageReactionsList) {
            TLRPC$TL_messages_messageReactionsList tLRPC$TL_messages_messageReactionsList = (TLRPC$TL_messages_messageReactionsList) tLObject;
            Iterator<TLRPC$User> it = tLRPC$TL_messages_messageReactionsList.users.iterator();
            while (it.hasNext()) {
                TLRPC$User next = it.next();
                MessagesController.getInstance(this.currentAccount).putUser(next, false);
                this.users.put(next.id, next);
            }
            int size = this.userReactions.size();
            ArrayList arrayList = new ArrayList(this.userReactions.size() + tLRPC$TL_messages_messageReactionsList.reactions.size());
            arrayList.addAll(this.userReactions);
            arrayList.addAll(tLRPC$TL_messages_messageReactionsList.reactions);
            if (this.onlySeenNow) {
                Collections.sort(arrayList, ReactedUsersListView$$ExternalSyntheticLambda3.INSTANCE);
            }
            AndroidUtilities.runOnUIThread(new ReactedUsersListView$$ExternalSyntheticLambda2(this, arrayList, size, tLRPC$TL_messages_messageReactionsList));
            return;
        }
        this.isLoading = false;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$load$1(TLRPC$TL_messagePeerReaction tLRPC$TL_messagePeerReaction, TLRPC$TL_messagePeerReaction tLRPC$TL_messagePeerReaction2) {
        int i = 1;
        int i2 = tLRPC$TL_messagePeerReaction.reaction != null ? 1 : 0;
        if (tLRPC$TL_messagePeerReaction2.reaction == null) {
            i = 0;
        }
        return zzdp$$ExternalSyntheticBackport0.m(i2, i);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$load$4(List list, int i, TLRPC$TL_messages_messageReactionsList tLRPC$TL_messages_messageReactionsList) {
        NotificationCenter.getInstance(this.currentAccount).doOnIdle(new ReactedUsersListView$$ExternalSyntheticLambda1(this, list, i, tLRPC$TL_messages_messageReactionsList));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$load$3(List list, int i, TLRPC$TL_messages_messageReactionsList tLRPC$TL_messages_messageReactionsList) {
        this.userReactions = list;
        if (this.onlySeenNow) {
            this.onlySeenNow = false;
            this.adapter.notifyDataSetChanged();
        } else {
            this.adapter.notifyItemRangeInserted(i, tLRPC$TL_messages_messageReactionsList.reactions.size());
        }
        if (!this.isLoaded) {
            ValueAnimator duration = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f}).setDuration(150);
            duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
            duration.addUpdateListener(new ReactedUsersListView$$ExternalSyntheticLambda0(this));
            duration.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    ReactedUsersListView.this.loadingView.setVisibility(8);
                }
            });
            duration.start();
            updateHeight();
            this.isLoaded = true;
        }
        String str = tLRPC$TL_messages_messageReactionsList.next_offset;
        this.offset = str;
        if (str == null) {
            this.canLoadMore = false;
        }
        this.isLoading = false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$load$2(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.listView.setAlpha(floatValue);
        this.loadingView.setAlpha(1.0f - floatValue);
    }

    /* access modifiers changed from: private */
    public void updateHeight() {
        int i;
        if (this.onHeightChangedListener != null) {
            if (this.listView.getMeasuredHeight() != 0) {
                i = Math.min(this.listView.getMeasuredHeight(), AndroidUtilities.dp((float) (Math.min(this.userReactions.size(), 6) * 48)));
            } else {
                i = AndroidUtilities.dp((float) (Math.min(this.userReactions.size(), 6) * 48));
            }
            this.onHeightChangedListener.onHeightChanged(this, i);
        }
    }

    /* access modifiers changed from: private */
    public int getLoadCount() {
        return this.filter == null ? 100 : 50;
    }

    private final class ReactedUserHolderView extends FrameLayout {
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        BackupImageView avatarView;
        View overlaySelectorView;
        BackupImageView reactView;
        TextView titleView;

        ReactedUserHolderView(Context context) {
            super(context);
            setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(48.0f)));
            BackupImageView backupImageView = new BackupImageView(context);
            this.avatarView = backupImageView;
            backupImageView.setRoundRadius(AndroidUtilities.dp(32.0f));
            addView(this.avatarView, LayoutHelper.createFrameRelatively(36.0f, 36.0f, 8388627, 8.0f, 0.0f, 0.0f, 0.0f));
            TextView textView = new TextView(context);
            this.titleView = textView;
            textView.setLines(1);
            this.titleView.setTextSize(1, 16.0f);
            this.titleView.setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
            this.titleView.setEllipsize(TextUtils.TruncateAt.END);
            addView(this.titleView, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388627, 58.0f, 0.0f, 44.0f, 0.0f));
            BackupImageView backupImageView2 = new BackupImageView(context);
            this.reactView = backupImageView2;
            addView(backupImageView2, LayoutHelper.createFrameRelatively(24.0f, 24.0f, 8388629, 0.0f, 0.0f, 12.0f, 0.0f));
            View view = new View(context);
            this.overlaySelectorView = view;
            view.setBackground(Theme.getSelectorDrawable(false));
            addView(this.overlaySelectorView, LayoutHelper.createFrame(-1, -1.0f));
        }

        /* access modifiers changed from: package-private */
        public void setUserReaction(TLRPC$TL_messagePeerReaction tLRPC$TL_messagePeerReaction) {
            TLRPC$TL_availableReaction tLRPC$TL_availableReaction;
            TLRPC$User tLRPC$User = (TLRPC$User) ReactedUsersListView.this.users.get(MessageObject.getPeerId(tLRPC$TL_messagePeerReaction.peer_id));
            this.avatarDrawable.setInfo(tLRPC$User);
            this.titleView.setText(UserObject.getUserName(tLRPC$User));
            this.avatarView.setImage(ImageLocation.getForUser(tLRPC$User, 1), "50_50", (Drawable) this.avatarDrawable, (Object) tLRPC$User);
            if (tLRPC$TL_messagePeerReaction.reaction != null && (tLRPC$TL_availableReaction = MediaDataController.getInstance(ReactedUsersListView.this.currentAccount).getReactionsMap().get(tLRPC$TL_messagePeerReaction.reaction)) != null) {
                this.reactView.setImage(ImageLocation.getForDocument(tLRPC$TL_availableReaction.static_icon), "50_50", "webp", (Drawable) DocumentObject.getSvgThumb(tLRPC$TL_availableReaction.static_icon.thumbs, "windowBackgroundGray", 1.0f), (Object) tLRPC$TL_availableReaction);
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
        }
    }

    public ReactedUsersListView setOnProfileSelectedListener(OnProfileSelectedListener onProfileSelectedListener2) {
        this.onProfileSelectedListener = onProfileSelectedListener2;
        return this;
    }

    public ReactedUsersListView setOnHeightChangedListener(OnHeightChangedListener onHeightChangedListener2) {
        this.onHeightChangedListener = onHeightChangedListener2;
        return this;
    }
}
