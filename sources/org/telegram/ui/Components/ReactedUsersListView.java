package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
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
import j$.util.Comparator;
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
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.RecyclerListView;

public class ReactedUsersListView extends FrameLayout {
    public static final int ITEM_HEIGHT_DP = 48;
    public static final int VISIBLE_ITEMS = 6;
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
    private LongSparseArray<TLRPC.TL_messagePeerReaction> peerReactionMap = new LongSparseArray<>();
    private int predictiveCount;
    /* access modifiers changed from: private */
    public List<TLRPC.TL_messagePeerReaction> userReactions = new ArrayList();

    public interface OnHeightChangedListener {
        void onHeightChanged(ReactedUsersListView reactedUsersListView, int i);
    }

    public interface OnProfileSelectedListener {
        void onProfileSelected(ReactedUsersListView reactedUsersListView, long j);
    }

    public ReactedUsersListView(final Context context, Theme.ResourcesProvider resourcesProvider, int currentAccount2, MessageObject message2, TLRPC.TL_reactionCount reactionCount, boolean addPadding) {
        super(context);
        this.currentAccount = currentAccount2;
        this.message = message2;
        this.filter = reactionCount == null ? null : reactionCount.reaction;
        int i = 6;
        this.predictiveCount = reactionCount == null ? 6 : reactionCount.count;
        this.listView = new RecyclerListView(context, resourcesProvider) {
            /* access modifiers changed from: protected */
            public void onMeasure(int widthSpec, int heightSpec) {
                super.onMeasure(widthSpec, heightSpec);
                ReactedUsersListView.this.updateHeight();
            }
        };
        final LinearLayoutManager llm = new LinearLayoutManager(context);
        this.listView.setLayoutManager(llm);
        if (addPadding) {
            this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(8.0f));
            this.listView.setClipToPadding(false);
        }
        if (Build.VERSION.SDK_INT >= 29) {
            this.listView.setVerticalScrollbarThumbDrawable(new ColorDrawable(Theme.getColor("listSelectorSDK21")));
        }
        RecyclerListView recyclerListView = this.listView;
        AnonymousClass2 r4 = new RecyclerView.Adapter() {
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new RecyclerListView.Holder(new ReactedUserHolderView(context));
            }

            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                ((ReactedUserHolderView) holder.itemView).setUserReaction((TLRPC.TL_messagePeerReaction) ReactedUsersListView.this.userReactions.get(position));
            }

            public int getItemCount() {
                return ReactedUsersListView.this.userReactions.size();
            }
        };
        this.adapter = r4;
        recyclerListView.setAdapter(r4);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new ReactedUsersListView$$ExternalSyntheticLambda5(this));
        this.listView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (ReactedUsersListView.this.isLoaded && ReactedUsersListView.this.canLoadMore && !ReactedUsersListView.this.isLoading && llm.findLastVisibleItemPosition() >= (ReactedUsersListView.this.adapter.getItemCount() - 1) - ReactedUsersListView.this.getLoadCount()) {
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
        this.loadingView.setItemsCount(reactionCount != null ? reactionCount.count : i);
        addView(this.loadingView, LayoutHelper.createFrame(-1, -1.0f));
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-ReactedUsersListView  reason: not valid java name */
    public /* synthetic */ void m4281lambda$new$0$orgtelegramuiComponentsReactedUsersListView(View view, int position) {
        OnProfileSelectedListener onProfileSelectedListener2 = this.onProfileSelectedListener;
        if (onProfileSelectedListener2 != null) {
            onProfileSelectedListener2.onProfileSelected(this, MessageObject.getPeerId(this.userReactions.get(position).peer_id));
        }
    }

    public ReactedUsersListView setSeenUsers(List<TLRPC.User> users) {
        List<TLRPC.TL_messagePeerReaction> nr = new ArrayList<>(users.size());
        for (TLRPC.User u : users) {
            if (this.peerReactionMap.get(u.id) == null) {
                TLRPC.TL_messagePeerReaction r = new TLRPC.TL_messagePeerReaction();
                r.reaction = null;
                r.peer_id = new TLRPC.TL_peerUser();
                r.peer_id.user_id = u.id;
                this.peerReactionMap.put(MessageObject.getPeerId(r.peer_id), r);
                nr.add(r);
            }
        }
        if (this.userReactions.isEmpty()) {
            this.onlySeenNow = true;
        }
        this.userReactions.addAll(nr);
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
    public void load() {
        this.isLoading = true;
        MessagesController ctrl = MessagesController.getInstance(this.currentAccount);
        TLRPC.TL_messages_getMessageReactionsList getList = new TLRPC.TL_messages_getMessageReactionsList();
        getList.peer = ctrl.getInputPeer(this.message.getDialogId());
        getList.id = this.message.getId();
        getList.limit = getLoadCount();
        getList.reaction = this.filter;
        getList.offset = this.offset;
        if (this.filter != null) {
            getList.flags = 1 | getList.flags;
        }
        if (this.offset != null) {
            getList.flags |= 2;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(getList, new ReactedUsersListView$$ExternalSyntheticLambda4(this), 64);
    }

    /* renamed from: lambda$load$4$org-telegram-ui-Components-ReactedUsersListView  reason: not valid java name */
    public /* synthetic */ void m4279lambda$load$4$orgtelegramuiComponentsReactedUsersListView(TLObject response) {
        NotificationCenter.getInstance(this.currentAccount).doOnIdle(new ReactedUsersListView$$ExternalSyntheticLambda1(this, response));
    }

    /* renamed from: lambda$load$5$org-telegram-ui-Components-ReactedUsersListView  reason: not valid java name */
    public /* synthetic */ void m4280lambda$load$5$orgtelegramuiComponentsReactedUsersListView(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new ReactedUsersListView$$ExternalSyntheticLambda2(this, response));
    }

    /* renamed from: lambda$load$3$org-telegram-ui-Components-ReactedUsersListView  reason: not valid java name */
    public /* synthetic */ void m4278lambda$load$3$orgtelegramuiComponentsReactedUsersListView(TLObject response) {
        if (response instanceof TLRPC.TL_messages_messageReactionsList) {
            TLRPC.TL_messages_messageReactionsList res = (TLRPC.TL_messages_messageReactionsList) response;
            Iterator<TLRPC.User> it = res.users.iterator();
            while (it.hasNext()) {
                MessagesController.getInstance(this.currentAccount).putUser(it.next(), false);
            }
            for (int i = 0; i < res.reactions.size(); i++) {
                this.userReactions.add(res.reactions.get(i));
                long peerId = MessageObject.getPeerId(res.reactions.get(i).peer_id);
                TLRPC.TL_messagePeerReaction reaction = this.peerReactionMap.get(peerId);
                if (reaction != null) {
                    this.userReactions.remove(reaction);
                }
                this.peerReactionMap.put(peerId, res.reactions.get(i));
            }
            if (this.onlySeenNow != 0) {
                Collections.sort(this.userReactions, Comparator.CC.comparingInt(ReactedUsersListView$$ExternalSyntheticLambda3.INSTANCE));
            }
            if (this.onlySeenNow) {
                this.onlySeenNow = false;
            }
            this.adapter.notifyDataSetChanged();
            if (!this.isLoaded) {
                ValueAnimator anim = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f}).setDuration(150);
                anim.setInterpolator(CubicBezierInterpolator.DEFAULT);
                anim.addUpdateListener(new ReactedUsersListView$$ExternalSyntheticLambda0(this));
                anim.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        ReactedUsersListView.this.loadingView.setVisibility(8);
                    }
                });
                anim.start();
                updateHeight();
                this.isLoaded = true;
            }
            String str = res.next_offset;
            this.offset = str;
            if (str == null) {
                this.canLoadMore = false;
            }
            this.isLoading = false;
            return;
        }
        this.isLoading = false;
    }

    static /* synthetic */ int lambda$load$1(TLRPC.TL_messagePeerReaction o) {
        return o.reaction != null ? 0 : 1;
    }

    /* renamed from: lambda$load$2$org-telegram-ui-Components-ReactedUsersListView  reason: not valid java name */
    public /* synthetic */ void m4277lambda$load$2$orgtelegramuiComponentsReactedUsersListView(ValueAnimator animation) {
        float val = ((Float) animation.getAnimatedValue()).floatValue();
        this.listView.setAlpha(val);
        this.loadingView.setAlpha(1.0f - val);
    }

    /* access modifiers changed from: private */
    public void updateHeight() {
        int h;
        if (this.onHeightChangedListener != null) {
            int count = this.userReactions.size();
            if (count == 0) {
                count = this.predictiveCount;
            }
            if (this.listView.getMeasuredHeight() != 0) {
                h = Math.min(this.listView.getMeasuredHeight(), AndroidUtilities.dp((float) (count * 48)));
            } else {
                h = AndroidUtilities.dp((float) (count * 48));
            }
            this.onHeightChangedListener.onHeightChanged(this, h);
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
        public void setUserReaction(TLRPC.TL_messagePeerReaction reaction) {
            TLRPC.User u = MessagesController.getInstance(ReactedUsersListView.this.currentAccount).getUser(Long.valueOf(MessageObject.getPeerId(reaction.peer_id)));
            if (u != null) {
                this.avatarDrawable.setInfo(u);
                this.titleView.setText(UserObject.getUserName(u));
                this.avatarView.setImage(ImageLocation.getForUser(u, 1), "50_50", (Drawable) this.avatarDrawable, (Object) u);
                if (reaction.reaction != null) {
                    TLRPC.TL_availableReaction r = MediaDataController.getInstance(ReactedUsersListView.this.currentAccount).getReactionsMap().get(reaction.reaction);
                    if (r != null) {
                        this.reactView.setImage(ImageLocation.getForDocument(r.static_icon), "50_50", "webp", (Drawable) DocumentObject.getSvgThumb(r.static_icon.thumbs, "windowBackgroundGray", 1.0f), (Object) r);
                        return;
                    }
                    this.reactView.setImageDrawable((Drawable) null);
                    return;
                }
                this.reactView.setImageDrawable((Drawable) null);
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
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
