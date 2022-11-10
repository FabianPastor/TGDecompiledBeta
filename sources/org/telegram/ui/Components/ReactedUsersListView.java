package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.LongSparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import j$.util.Comparator$CC;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$MessagePeerReaction;
import org.telegram.tgnet.TLRPC$Reaction;
import org.telegram.tgnet.TLRPC$ReactionCount;
import org.telegram.tgnet.TLRPC$TL_availableReaction;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messagePeerReaction;
import org.telegram.tgnet.TLRPC$TL_messages_getMessageReactionsList;
import org.telegram.tgnet.TLRPC$TL_messages_messageReactionsList;
import org.telegram.tgnet.TLRPC$TL_peerUser;
import org.telegram.tgnet.TLRPC$TL_reactionCustomEmoji;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserProfilePhoto;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes3.dex */
public class ReactedUsersListView extends FrameLayout {
    private RecyclerView.Adapter adapter;
    public boolean canLoadMore;
    private int currentAccount;
    ArrayList<TLRPC$InputStickerSet> customEmojiStickerSets;
    ArrayList<ReactionsLayoutInBubble.VisibleReaction> customReactionsEmoji;
    private TLRPC$Reaction filter;
    public boolean isLoaded;
    public boolean isLoading;
    public RecyclerListView listView;
    private FlickerLoadingView loadingView;
    private MessageObject message;
    MessageContainsEmojiButton messageContainsEmojiButton;
    private String offset;
    private OnCustomEmojiSelectedListener onCustomEmojiSelectedListener;
    private OnHeightChangedListener onHeightChangedListener;
    private OnProfileSelectedListener onProfileSelectedListener;
    private LongSparseArray<ArrayList<TLRPC$MessagePeerReaction>> peerReactionMap;
    private int predictiveCount;
    Theme.ResourcesProvider resourcesProvider;
    private List<TLRPC$MessagePeerReaction> userReactions;

    /* loaded from: classes3.dex */
    public interface OnCustomEmojiSelectedListener {
        void showCustomEmojiAlert(ReactedUsersListView reactedUsersListView, ArrayList<TLRPC$InputStickerSet> arrayList);
    }

    /* loaded from: classes3.dex */
    public interface OnHeightChangedListener {
        void onHeightChanged(ReactedUsersListView reactedUsersListView, int i);
    }

    /* loaded from: classes3.dex */
    public interface OnProfileSelectedListener {
        void onProfileSelected(ReactedUsersListView reactedUsersListView, long j, TLRPC$MessagePeerReaction tLRPC$MessagePeerReaction);
    }

    public ReactedUsersListView(final Context context, final Theme.ResourcesProvider resourcesProvider, final int i, MessageObject messageObject, TLRPC$ReactionCount tLRPC$ReactionCount, boolean z) {
        super(context);
        TLRPC$Reaction tLRPC$Reaction;
        this.userReactions = new ArrayList();
        this.peerReactionMap = new LongSparseArray<>();
        this.canLoadMore = true;
        this.customReactionsEmoji = new ArrayList<>();
        this.customEmojiStickerSets = new ArrayList<>();
        this.currentAccount = i;
        this.message = messageObject;
        this.filter = tLRPC$ReactionCount == null ? null : tLRPC$ReactionCount.reaction;
        this.resourcesProvider = resourcesProvider;
        this.predictiveCount = tLRPC$ReactionCount == null ? 6 : tLRPC$ReactionCount.count;
        this.listView = new RecyclerListView(context, resourcesProvider) { // from class: org.telegram.ui.Components.ReactedUsersListView.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
            public void onMeasure(int i2, int i3) {
                MessageContainsEmojiButton messageContainsEmojiButton = ReactedUsersListView.this.messageContainsEmojiButton;
                if (messageContainsEmojiButton != null) {
                    messageContainsEmojiButton.measure(i2, View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i3), 0));
                }
                super.onMeasure(i2, i3);
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
        RecyclerView.Adapter adapter = new RecyclerView.Adapter() { // from class: org.telegram.ui.Components.ReactedUsersListView.2
            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            /* renamed from: onCreateViewHolder */
            public RecyclerView.ViewHolder moNUMonCreateViewHolder(ViewGroup viewGroup, int i2) {
                FrameLayout reactedUserHolderView;
                if (i2 == 0) {
                    reactedUserHolderView = new ReactedUserHolderView(context);
                } else {
                    ReactedUsersListView reactedUsersListView = ReactedUsersListView.this;
                    MessageContainsEmojiButton messageContainsEmojiButton = reactedUsersListView.messageContainsEmojiButton;
                    if (messageContainsEmojiButton == null) {
                        reactedUsersListView.updateCustomReactionsButton();
                    } else if (messageContainsEmojiButton.getParent() != null) {
                        ((ViewGroup) ReactedUsersListView.this.messageContainsEmojiButton.getParent()).removeView(ReactedUsersListView.this.messageContainsEmojiButton);
                    }
                    reactedUserHolderView = new FrameLayout(context);
                    View view = new View(context);
                    view.setBackgroundColor(Theme.getColor("actionBarDefaultSubmenuSeparator", resourcesProvider));
                    reactedUserHolderView.addView(view, LayoutHelper.createFrame(-1, 8.0f));
                    reactedUserHolderView.addView(ReactedUsersListView.this.messageContainsEmojiButton, LayoutHelper.createFrame(-1, -1.0f, 0, 0.0f, 8.0f, 0.0f, 0.0f));
                }
                return new RecyclerListView.Holder(reactedUserHolderView);
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i2) {
                if (viewHolder.getItemViewType() == 0) {
                    ((ReactedUserHolderView) viewHolder.itemView).setUserReaction((TLRPC$MessagePeerReaction) ReactedUsersListView.this.userReactions.get(i2));
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public int getItemCount() {
                return ReactedUsersListView.this.userReactions.size() + ((ReactedUsersListView.this.customReactionsEmoji.isEmpty() || MessagesController.getInstance(i).premiumLocked) ? 0 : 1);
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public int getItemViewType(int i2) {
                return i2 < ReactedUsersListView.this.userReactions.size() ? 0 : 1;
            }
        };
        this.adapter = adapter;
        recyclerListView.setAdapter(adapter);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.ReactedUsersListView$$ExternalSyntheticLambda6
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i2) {
                ReactedUsersListView.this.lambda$new$0(view, i2);
            }
        });
        this.listView.addOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Components.ReactedUsersListView.3
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int i2, int i3) {
                ReactedUsersListView reactedUsersListView = ReactedUsersListView.this;
                if (!reactedUsersListView.isLoaded || !reactedUsersListView.canLoadMore || reactedUsersListView.isLoading || linearLayoutManager.findLastVisibleItemPosition() < (ReactedUsersListView.this.adapter.getItemCount() - 1) - ReactedUsersListView.this.getLoadCount()) {
                    return;
                }
                ReactedUsersListView.this.load();
            }
        });
        this.listView.setVerticalScrollBarEnabled(true);
        this.listView.setAlpha(0.0f);
        addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context, resourcesProvider) { // from class: org.telegram.ui.Components.ReactedUsersListView.4
            @Override // org.telegram.ui.Components.FlickerLoadingView
            public int getAdditionalHeight() {
                MessageContainsEmojiButton messageContainsEmojiButton;
                if (ReactedUsersListView.this.customReactionsEmoji.isEmpty() || (messageContainsEmojiButton = ReactedUsersListView.this.messageContainsEmojiButton) == null) {
                    return 0;
                }
                return messageContainsEmojiButton.getMeasuredHeight() + AndroidUtilities.dp(8.0f);
            }
        };
        this.loadingView = flickerLoadingView;
        flickerLoadingView.setColors("actionBarDefaultSubmenuBackground", "listSelectorSDK21", null);
        this.loadingView.setIsSingleCell(true);
        this.loadingView.setItemsCount(this.predictiveCount);
        addView(this.loadingView, LayoutHelper.createFrame(-1, -1.0f));
        if (!z && (tLRPC$Reaction = this.filter) != null && (tLRPC$Reaction instanceof TLRPC$TL_reactionCustomEmoji) && !MessagesController.getInstance(i).premiumLocked) {
            this.customReactionsEmoji.clear();
            this.customReactionsEmoji.add(ReactionsLayoutInBubble.VisibleReaction.fromTLReaction(this.filter));
            updateCustomReactionsButton();
        }
        this.loadingView.setViewType(this.customReactionsEmoji.isEmpty() ? 16 : 23);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view, int i) {
        OnCustomEmojiSelectedListener onCustomEmojiSelectedListener;
        int itemViewType = this.adapter.getItemViewType(i);
        if (itemViewType == 0) {
            OnProfileSelectedListener onProfileSelectedListener = this.onProfileSelectedListener;
            if (onProfileSelectedListener == null) {
                return;
            }
            onProfileSelectedListener.onProfileSelected(this, MessageObject.getPeerId(this.userReactions.get(i).peer_id), this.userReactions.get(i));
        } else if (itemViewType != 1 || (onCustomEmojiSelectedListener = this.onCustomEmojiSelectedListener) == null) {
        } else {
            onCustomEmojiSelectedListener.showCustomEmojiAlert(this, this.customEmojiStickerSets);
        }
    }

    @SuppressLint({"NotifyDataSetChanged"})
    public ReactedUsersListView setSeenUsers(List<TLRPC$User> list) {
        ArrayList arrayList = new ArrayList(list.size());
        for (TLRPC$User tLRPC$User : list) {
            if (this.peerReactionMap.get(tLRPC$User.id) == null) {
                TLRPC$TL_messagePeerReaction tLRPC$TL_messagePeerReaction = new TLRPC$TL_messagePeerReaction();
                tLRPC$TL_messagePeerReaction.reaction = null;
                TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                tLRPC$TL_messagePeerReaction.peer_id = tLRPC$TL_peerUser;
                tLRPC$TL_peerUser.user_id = tLRPC$User.id;
                ArrayList<TLRPC$MessagePeerReaction> arrayList2 = new ArrayList<>();
                arrayList2.add(tLRPC$TL_messagePeerReaction);
                this.peerReactionMap.put(MessageObject.getPeerId(tLRPC$TL_messagePeerReaction.peer_id), arrayList2);
                arrayList.add(tLRPC$TL_messagePeerReaction);
            }
        }
        this.userReactions.isEmpty();
        this.userReactions.addAll(arrayList);
        Collections.sort(this.userReactions, Comparator$CC.comparingInt(ReactedUsersListView$$ExternalSyntheticLambda3.INSTANCE));
        this.adapter.notifyDataSetChanged();
        updateHeight();
        return this;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$setSeenUsers$1(TLRPC$MessagePeerReaction tLRPC$MessagePeerReaction) {
        return tLRPC$MessagePeerReaction.reaction != null ? 0 : 1;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.isLoaded || this.isLoading) {
            return;
        }
        load();
    }

    /* JADX INFO: Access modifiers changed from: private */
    @SuppressLint({"NotifyDataSetChanged"})
    public void load() {
        this.isLoading = true;
        MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
        TLRPC$TL_messages_getMessageReactionsList tLRPC$TL_messages_getMessageReactionsList = new TLRPC$TL_messages_getMessageReactionsList();
        tLRPC$TL_messages_getMessageReactionsList.peer = messagesController.getInputPeer(this.message.getDialogId());
        tLRPC$TL_messages_getMessageReactionsList.id = this.message.getId();
        tLRPC$TL_messages_getMessageReactionsList.limit = getLoadCount();
        TLRPC$Reaction tLRPC$Reaction = this.filter;
        tLRPC$TL_messages_getMessageReactionsList.reaction = tLRPC$Reaction;
        String str = this.offset;
        tLRPC$TL_messages_getMessageReactionsList.offset = str;
        if (tLRPC$Reaction != null) {
            tLRPC$TL_messages_getMessageReactionsList.flags = 1 | tLRPC$TL_messages_getMessageReactionsList.flags;
        }
        if (str != null) {
            tLRPC$TL_messages_getMessageReactionsList.flags |= 2;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_getMessageReactionsList, new RequestDelegate() { // from class: org.telegram.ui.Components.ReactedUsersListView$$ExternalSyntheticLambda5
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                ReactedUsersListView.this.lambda$load$6(tLObject, tLRPC$TL_error);
            }
        }, 64);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$load$5(final TLObject tLObject) {
        NotificationCenter.getInstance(this.currentAccount).doOnIdle(new Runnable() { // from class: org.telegram.ui.Components.ReactedUsersListView$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                ReactedUsersListView.this.lambda$load$4(tLObject);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$load$6(final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ReactedUsersListView$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                ReactedUsersListView.this.lambda$load$5(tLObject);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$load$4(TLObject tLObject) {
        if (tLObject instanceof TLRPC$TL_messages_messageReactionsList) {
            TLRPC$TL_messages_messageReactionsList tLRPC$TL_messages_messageReactionsList = (TLRPC$TL_messages_messageReactionsList) tLObject;
            Iterator<TLRPC$User> it = tLRPC$TL_messages_messageReactionsList.users.iterator();
            while (it.hasNext()) {
                MessagesController.getInstance(this.currentAccount).putUser(it.next(), false);
            }
            HashSet hashSet = new HashSet();
            for (int i = 0; i < tLRPC$TL_messages_messageReactionsList.reactions.size(); i++) {
                this.userReactions.add(tLRPC$TL_messages_messageReactionsList.reactions.get(i));
                long peerId = MessageObject.getPeerId(tLRPC$TL_messages_messageReactionsList.reactions.get(i).peer_id);
                ArrayList<TLRPC$MessagePeerReaction> arrayList = this.peerReactionMap.get(peerId);
                if (arrayList == null) {
                    arrayList = new ArrayList<>();
                }
                int i2 = 0;
                while (i2 < arrayList.size()) {
                    if (arrayList.get(i2).reaction == null) {
                        arrayList.remove(i2);
                        i2--;
                    }
                    i2++;
                }
                ReactionsLayoutInBubble.VisibleReaction fromTLReaction = ReactionsLayoutInBubble.VisibleReaction.fromTLReaction(tLRPC$TL_messages_messageReactionsList.reactions.get(i).reaction);
                if (fromTLReaction.documentId != 0) {
                    hashSet.add(fromTLReaction);
                }
                arrayList.add(tLRPC$TL_messages_messageReactionsList.reactions.get(i));
                this.peerReactionMap.put(peerId, arrayList);
            }
            if (this.filter == null) {
                this.customReactionsEmoji.clear();
                this.customReactionsEmoji.addAll(hashSet);
                updateCustomReactionsButton();
            }
            Collections.sort(this.userReactions, Comparator$CC.comparingInt(ReactedUsersListView$$ExternalSyntheticLambda4.INSTANCE));
            this.adapter.notifyDataSetChanged();
            if (!this.isLoaded) {
                ValueAnimator duration = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(150L);
                duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
                duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ReactedUsersListView$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        ReactedUsersListView.this.lambda$load$3(valueAnimator);
                    }
                });
                duration.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ReactedUsersListView.5
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
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
            return;
        }
        this.isLoading = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$load$2(TLRPC$MessagePeerReaction tLRPC$MessagePeerReaction) {
        return tLRPC$MessagePeerReaction.reaction != null ? 0 : 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$load$3(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.listView.setAlpha(floatValue);
        this.loadingView.setAlpha(1.0f - floatValue);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCustomReactionsButton() {
        this.customEmojiStickerSets.clear();
        ArrayList arrayList = new ArrayList();
        HashSet hashSet = new HashSet();
        for (int i = 0; i < this.customReactionsEmoji.size(); i++) {
            TLRPC$InputStickerSet inputStickerSet = MessageObject.getInputStickerSet(AnimatedEmojiDrawable.findDocument(this.currentAccount, this.customReactionsEmoji.get(i).documentId));
            if (inputStickerSet != null && !hashSet.contains(Long.valueOf(inputStickerSet.id))) {
                arrayList.add(inputStickerSet);
                hashSet.add(Long.valueOf(inputStickerSet.id));
            }
        }
        if (MessagesController.getInstance(this.currentAccount).premiumLocked) {
            return;
        }
        this.customEmojiStickerSets.addAll(arrayList);
        MessageContainsEmojiButton messageContainsEmojiButton = new MessageContainsEmojiButton(this.currentAccount, getContext(), this.resourcesProvider, arrayList, 1);
        this.messageContainsEmojiButton = messageContainsEmojiButton;
        messageContainsEmojiButton.checkWidth = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateHeight() {
        if (this.onHeightChangedListener != null) {
            int size = this.userReactions.size();
            if (size == 0) {
                size = this.predictiveCount;
            }
            int dp = AndroidUtilities.dp(size * 48);
            MessageContainsEmojiButton messageContainsEmojiButton = this.messageContainsEmojiButton;
            if (messageContainsEmojiButton != null) {
                dp += messageContainsEmojiButton.getMeasuredHeight() + AndroidUtilities.dp(8.0f);
            }
            if (this.listView.getMeasuredHeight() != 0) {
                dp = Math.min(this.listView.getMeasuredHeight(), dp);
            }
            this.onHeightChangedListener.onHeightChanged(this, dp);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getLoadCount() {
        return this.filter == null ? 100 : 50;
    }

    /* loaded from: classes3.dex */
    private final class ReactedUserHolderView extends FrameLayout {
        AvatarDrawable avatarDrawable;
        BackupImageView avatarView;
        View overlaySelectorView;
        BackupImageView reactView;
        SimpleTextView titleView;

        ReactedUserHolderView(Context context) {
            super(context);
            this.avatarDrawable = new AvatarDrawable();
            setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(48.0f)));
            BackupImageView backupImageView = new BackupImageView(context);
            this.avatarView = backupImageView;
            backupImageView.setRoundRadius(AndroidUtilities.dp(32.0f));
            addView(this.avatarView, LayoutHelper.createFrameRelatively(36.0f, 36.0f, 8388627, 8.0f, 0.0f, 0.0f, 0.0f));
            SimpleTextView simpleTextView = new SimpleTextView(context);
            this.titleView = simpleTextView;
            simpleTextView.setTextSize(16);
            this.titleView.setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
            this.titleView.setEllipsizeByGradient(true);
            this.titleView.setImportantForAccessibility(2);
            this.titleView.setWidthWrapContent(true);
            this.titleView.setPadding(0, AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f));
            addView(this.titleView, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388627, 58.0f, 0.0f, 42.0f, 0.0f));
            BackupImageView backupImageView2 = new BackupImageView(context);
            this.reactView = backupImageView2;
            addView(backupImageView2, LayoutHelper.createFrameRelatively(24.0f, 24.0f, 8388629, 0.0f, 0.0f, 12.0f, 0.0f));
            View view = new View(context);
            this.overlaySelectorView = view;
            view.setBackground(Theme.getSelectorDrawable(false));
            addView(this.overlaySelectorView, LayoutHelper.createFrame(-1, -1.0f));
        }

        void setUserReaction(TLRPC$MessagePeerReaction tLRPC$MessagePeerReaction) {
            Drawable drawable;
            TLRPC$User user = MessagesController.getInstance(ReactedUsersListView.this.currentAccount).getUser(Long.valueOf(MessageObject.getPeerId(tLRPC$MessagePeerReaction.peer_id)));
            if (user == null) {
                return;
            }
            this.avatarDrawable.setInfo(user);
            this.titleView.setText(UserObject.getUserName(user));
            Drawable drawable2 = this.avatarDrawable;
            TLRPC$UserProfilePhoto tLRPC$UserProfilePhoto = user.photo;
            if (tLRPC$UserProfilePhoto != null && (drawable = tLRPC$UserProfilePhoto.strippedBitmap) != null) {
                drawable2 = drawable;
            }
            this.avatarView.setImage(ImageLocation.getForUser(user, 1), "50_50", drawable2, user);
            TLRPC$Reaction tLRPC$Reaction = tLRPC$MessagePeerReaction.reaction;
            if (tLRPC$Reaction != null) {
                ReactionsLayoutInBubble.VisibleReaction fromTLReaction = ReactionsLayoutInBubble.VisibleReaction.fromTLReaction(tLRPC$Reaction);
                if (fromTLReaction.emojicon != null) {
                    TLRPC$TL_availableReaction tLRPC$TL_availableReaction = MediaDataController.getInstance(ReactedUsersListView.this.currentAccount).getReactionsMap().get(fromTLReaction.emojicon);
                    if (tLRPC$TL_availableReaction != null) {
                        this.reactView.setImage(ImageLocation.getForDocument(tLRPC$TL_availableReaction.center_icon), "40_40_lastframe", "webp", DocumentObject.getSvgThumb(tLRPC$TL_availableReaction.static_icon.thumbs, "windowBackgroundGray", 1.0f), tLRPC$TL_availableReaction);
                    } else {
                        this.reactView.setImageDrawable(null);
                    }
                } else {
                    this.reactView.setAnimatedEmojiDrawable(new AnimatedEmojiDrawable(0, ReactedUsersListView.this.currentAccount, fromTLReaction.documentId));
                }
                setContentDescription(LocaleController.formatString("AccDescrReactedWith", R.string.AccDescrReactedWith, UserObject.getUserName(user), tLRPC$MessagePeerReaction.reaction));
                return;
            }
            this.reactView.setImageDrawable(null);
            setContentDescription(LocaleController.formatString("AccDescrPersonHasSeen", R.string.AccDescrPersonHasSeen, UserObject.getUserName(user)));
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setEnabled(true);
        }
    }

    public ReactedUsersListView setOnProfileSelectedListener(OnProfileSelectedListener onProfileSelectedListener) {
        this.onProfileSelectedListener = onProfileSelectedListener;
        return this;
    }

    public ReactedUsersListView setOnHeightChangedListener(OnHeightChangedListener onHeightChangedListener) {
        this.onHeightChangedListener = onHeightChangedListener;
        return this;
    }

    public void setPredictiveCount(int i) {
        this.predictiveCount = i;
        this.loadingView.setItemsCount(i);
    }

    /* loaded from: classes3.dex */
    public static class ContainerLinerLayout extends LinearLayout {
        public boolean hasHeader;

        public ContainerLinerLayout(Context context) {
            super(context);
        }

        @Override // android.widget.LinearLayout, android.view.View
        protected void onMeasure(int i, int i2) {
            int i3;
            RecyclerListView recyclerListView = null;
            if (!this.hasHeader) {
                i3 = 0;
                for (int i4 = 0; i4 < getChildCount(); i4++) {
                    if (getChildAt(i4) instanceof ReactedUsersListView) {
                        recyclerListView = ((ReactedUsersListView) getChildAt(i4)).listView;
                        if (recyclerListView.getAdapter().getItemCount() == recyclerListView.getChildCount()) {
                            int childCount = recyclerListView.getChildCount();
                            for (int i5 = 0; i5 < childCount; i5++) {
                                recyclerListView.getChildAt(i5).measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), 0), i2);
                                if (recyclerListView.getChildAt(i5).getMeasuredWidth() > i3) {
                                    i3 = recyclerListView.getChildAt(i5).getMeasuredWidth();
                                }
                            }
                            i3 += AndroidUtilities.dp(16.0f);
                        }
                    }
                }
            } else {
                i3 = 0;
            }
            int size = View.MeasureSpec.getSize(i);
            if (size < AndroidUtilities.dp(240.0f)) {
                size = AndroidUtilities.dp(240.0f);
            }
            if (size > AndroidUtilities.dp(280.0f)) {
                size = AndroidUtilities.dp(280.0f);
            }
            if (size < 0) {
                size = 0;
            }
            if (i3 == 0 || i3 >= size) {
                i3 = size;
            }
            if (recyclerListView != null) {
                for (int i6 = 0; i6 < recyclerListView.getChildCount(); i6++) {
                    recyclerListView.getChildAt(i6).measure(View.MeasureSpec.makeMeasureSpec(i3, NUM), i2);
                }
            }
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(i3, NUM), i2);
        }
    }

    public ReactedUsersListView setOnCustomEmojiSelectedListener(OnCustomEmojiSelectedListener onCustomEmojiSelectedListener) {
        this.onCustomEmojiSelectedListener = onCustomEmojiSelectedListener;
        return this;
    }
}
