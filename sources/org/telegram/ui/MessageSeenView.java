package org.telegram.ui;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.AvatarsImageView;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.HideViewAfterAnimation;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class MessageSeenView extends FrameLayout {
    AvatarsImageView avatarsImageView;
    int currentAccount;
    FlickerLoadingView flickerLoadingView;
    ImageView iconView;
    boolean ignoreLayout;
    boolean isVoice;
    ArrayList<Long> peerIds = new ArrayList<>();
    TextView titleView;
    public ArrayList<TLRPC.User> users = new ArrayList<>();

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public MessageSeenView(Context context, int currentAccount2, MessageObject messageObject, TLRPC.Chat chat) {
        super(context);
        long fromId;
        Context context2 = context;
        MessageObject messageObject2 = messageObject;
        this.currentAccount = currentAccount2;
        this.isVoice = messageObject.isRoundVideo() || messageObject.isVoice();
        FlickerLoadingView flickerLoadingView2 = new FlickerLoadingView(context2);
        this.flickerLoadingView = flickerLoadingView2;
        flickerLoadingView2.setColors("actionBarDefaultSubmenuBackground", "listSelectorSDK21", (String) null);
        this.flickerLoadingView.setViewType(13);
        this.flickerLoadingView.setIsSingleCell(false);
        addView(this.flickerLoadingView, LayoutHelper.createFrame(-2, -1.0f));
        AnonymousClass1 r0 = new TextView(context2) {
            public void setText(CharSequence text, TextView.BufferType type) {
                super.setText(text, type);
            }
        };
        this.titleView = r0;
        r0.setTextSize(1, 16.0f);
        this.titleView.setLines(1);
        this.titleView.setEllipsize(TextUtils.TruncateAt.END);
        addView(this.titleView, LayoutHelper.createFrame(-2, -2.0f, 19, 40.0f, 0.0f, 62.0f, 0.0f));
        AvatarsImageView avatarsImageView2 = new AvatarsImageView(context2, false);
        this.avatarsImageView = avatarsImageView2;
        avatarsImageView2.setStyle(11);
        addView(this.avatarsImageView, LayoutHelper.createFrame(56, -1.0f, 21, 0.0f, 0.0f, 0.0f, 0.0f));
        this.titleView.setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
        TLRPC.TL_messages_getMessageReadParticipants req = new TLRPC.TL_messages_getMessageReadParticipants();
        req.msg_id = messageObject.getId();
        req.peer = MessagesController.getInstance(currentAccount2).getInputPeer(messageObject.getDialogId());
        ImageView imageView = new ImageView(context2);
        this.iconView = imageView;
        addView(imageView, LayoutHelper.createFrame(24, 24.0f, 19, 11.0f, 0.0f, 0.0f, 0.0f));
        Drawable drawable = ContextCompat.getDrawable(context2, this.isVoice ? NUM : NUM).mutate();
        drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("actionBarDefaultSubmenuItemIcon"), PorterDuff.Mode.MULTIPLY));
        this.iconView.setImageDrawable(drawable);
        this.avatarsImageView.setAlpha(0.0f);
        this.titleView.setAlpha(0.0f);
        if (messageObject2.messageOwner.from_id != null) {
            fromId = messageObject2.messageOwner.from_id.user_id;
        } else {
            fromId = 0;
        }
        ConnectionsManager instance = ConnectionsManager.getInstance(currentAccount2);
        MessageSeenView$$ExternalSyntheticLambda5 messageSeenView$$ExternalSyntheticLambda5 = r0;
        MessageSeenView$$ExternalSyntheticLambda5 messageSeenView$$ExternalSyntheticLambda52 = new MessageSeenView$$ExternalSyntheticLambda5(this, fromId, currentAccount2, chat);
        instance.sendRequest(req, messageSeenView$$ExternalSyntheticLambda5);
        setBackground(Theme.createRadSelectorDrawable(Theme.getColor("dialogButtonSelector"), 6, 0));
        setEnabled(false);
    }

    /* renamed from: lambda$new$5$org-telegram-ui-MessageSeenView  reason: not valid java name */
    public /* synthetic */ void m3948lambda$new$5$orgtelegramuiMessageSeenView(long finalFromId, int currentAccount2, TLRPC.Chat chat, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new MessageSeenView$$ExternalSyntheticLambda2(this, error, response, finalFromId, currentAccount2, chat));
    }

    /* renamed from: lambda$new$4$org-telegram-ui-MessageSeenView  reason: not valid java name */
    public /* synthetic */ void m3947lambda$new$4$orgtelegramuiMessageSeenView(TLRPC.TL_error error, TLObject response, long finalFromId, int currentAccount2, TLRPC.Chat chat) {
        int i = currentAccount2;
        TLRPC.Chat chat2 = chat;
        if (error == null) {
            TLRPC.Vector vector = (TLRPC.Vector) response;
            ArrayList<Long> unknownUsers = new ArrayList<>();
            HashMap<Long, TLRPC.User> usersLocal = new HashMap<>();
            ArrayList<Long> allPeers = new ArrayList<>();
            int n = vector.objects.size();
            for (int i2 = 0; i2 < n; i2++) {
                Object object = vector.objects.get(i2);
                if (object instanceof Long) {
                    Long peerId = (Long) object;
                    if (finalFromId != peerId.longValue()) {
                        TLRPC.User user = MessagesController.getInstance(currentAccount2).getUser(peerId);
                        allPeers.add(peerId);
                        unknownUsers.add(peerId);
                    }
                }
            }
            if (unknownUsers.isEmpty() != 0) {
                for (int i3 = 0; i3 < allPeers.size(); i3++) {
                    this.peerIds.add(allPeers.get(i3));
                    this.users.add(usersLocal.get(allPeers.get(i3)));
                }
                updateView();
            } else if (ChatObject.isChannel(chat)) {
                TLRPC.TL_channels_getParticipants usersReq = new TLRPC.TL_channels_getParticipants();
                usersReq.limit = MessagesController.getInstance(currentAccount2).chatReadMarkSizeThreshold;
                usersReq.offset = 0;
                usersReq.filter = new TLRPC.TL_channelParticipantsRecent();
                usersReq.channel = MessagesController.getInstance(currentAccount2).getInputChannel(chat2.id);
                ConnectionsManager.getInstance(currentAccount2).sendRequest(usersReq, new MessageSeenView$$ExternalSyntheticLambda3(this, i, usersLocal, allPeers));
            } else {
                TLRPC.TL_messages_getFullChat usersReq2 = new TLRPC.TL_messages_getFullChat();
                usersReq2.chat_id = chat2.id;
                ConnectionsManager.getInstance(currentAccount2).sendRequest(usersReq2, new MessageSeenView$$ExternalSyntheticLambda4(this, i, usersLocal, allPeers));
            }
        } else {
            updateView();
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-MessageSeenView  reason: not valid java name */
    public /* synthetic */ void m3944lambda$new$1$orgtelegramuiMessageSeenView(int currentAccount2, HashMap usersLocal, ArrayList allPeers, TLObject response1, TLRPC.TL_error error1) {
        AndroidUtilities.runOnUIThread(new MessageSeenView$$ExternalSyntheticLambda0(this, response1, currentAccount2, usersLocal, allPeers));
    }

    /* renamed from: lambda$new$0$org-telegram-ui-MessageSeenView  reason: not valid java name */
    public /* synthetic */ void m3943lambda$new$0$orgtelegramuiMessageSeenView(TLObject response1, int currentAccount2, HashMap usersLocal, ArrayList allPeers) {
        if (response1 != null) {
            TLRPC.TL_channels_channelParticipants users2 = (TLRPC.TL_channels_channelParticipants) response1;
            for (int i = 0; i < users2.users.size(); i++) {
                TLRPC.User user = users2.users.get(i);
                MessagesController.getInstance(currentAccount2).putUser(user, false);
                usersLocal.put(Long.valueOf(user.id), user);
            }
            for (int i2 = 0; i2 < allPeers.size(); i2++) {
                this.peerIds.add((Long) allPeers.get(i2));
                this.users.add((TLRPC.User) usersLocal.get(allPeers.get(i2)));
            }
        }
        updateView();
    }

    /* renamed from: lambda$new$3$org-telegram-ui-MessageSeenView  reason: not valid java name */
    public /* synthetic */ void m3946lambda$new$3$orgtelegramuiMessageSeenView(int currentAccount2, HashMap usersLocal, ArrayList allPeers, TLObject response1, TLRPC.TL_error error1) {
        AndroidUtilities.runOnUIThread(new MessageSeenView$$ExternalSyntheticLambda1(this, response1, currentAccount2, usersLocal, allPeers));
    }

    /* renamed from: lambda$new$2$org-telegram-ui-MessageSeenView  reason: not valid java name */
    public /* synthetic */ void m3945lambda$new$2$orgtelegramuiMessageSeenView(TLObject response1, int currentAccount2, HashMap usersLocal, ArrayList allPeers) {
        if (response1 != null) {
            TLRPC.TL_messages_chatFull chatFull = (TLRPC.TL_messages_chatFull) response1;
            for (int i = 0; i < chatFull.users.size(); i++) {
                TLRPC.User user = chatFull.users.get(i);
                MessagesController.getInstance(currentAccount2).putUser(user, false);
                usersLocal.put(Long.valueOf(user.id), user);
            }
            for (int i2 = 0; i2 < allPeers.size(); i2++) {
                this.peerIds.add((Long) allPeers.get(i2));
                this.users.add((TLRPC.User) usersLocal.get(allPeers.get(i2)));
            }
        }
        updateView();
    }

    public void requestLayout() {
        if (!this.ignoreLayout) {
            super.requestLayout();
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        View parent = (View) getParent();
        if (parent != null && parent.getWidth() > 0) {
            widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), NUM);
        }
        if (this.flickerLoadingView.getVisibility() == 0) {
            this.ignoreLayout = true;
            this.flickerLoadingView.setVisibility(8);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            this.flickerLoadingView.getLayoutParams().width = getMeasuredWidth();
            this.flickerLoadingView.setVisibility(0);
            this.ignoreLayout = false;
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void updateView() {
        setEnabled(this.users.size() > 0);
        for (int i = 0; i < 3; i++) {
            if (i < this.users.size()) {
                this.avatarsImageView.setObject(i, this.currentAccount, this.users.get(i));
            } else {
                this.avatarsImageView.setObject(i, this.currentAccount, (TLObject) null);
            }
        }
        if (this.users.size() == 1) {
            this.avatarsImageView.setTranslationX((float) AndroidUtilities.dp(24.0f));
        } else if (this.users.size() == 2) {
            this.avatarsImageView.setTranslationX((float) AndroidUtilities.dp(12.0f));
        } else {
            this.avatarsImageView.setTranslationX(0.0f);
        }
        int newRightMargin = AndroidUtilities.dp(this.users.size() == 0 ? 8.0f : 62.0f);
        ViewGroup.MarginLayoutParams titleViewMargins = (ViewGroup.MarginLayoutParams) this.titleView.getLayoutParams();
        if (titleViewMargins.rightMargin != newRightMargin) {
            titleViewMargins.rightMargin = newRightMargin;
            this.titleView.setLayoutParams(titleViewMargins);
        }
        this.avatarsImageView.commitTransition(false);
        if (this.peerIds.size() == 1 && this.users.get(0) != null) {
            this.titleView.setText(ContactsController.formatName(this.users.get(0).first_name, this.users.get(0).last_name));
        } else if (this.peerIds.size() == 0) {
            this.titleView.setText(LocaleController.getString("NobodyViewed", NUM));
        } else {
            this.titleView.setText(LocaleController.formatPluralString(this.isVoice ? "MessagePlayed" : "MessageSeen", this.peerIds.size(), new Object[0]));
        }
        this.titleView.animate().alpha(1.0f).setDuration(220).start();
        this.avatarsImageView.animate().alpha(1.0f).setDuration(220).start();
        this.flickerLoadingView.animate().alpha(0.0f).setDuration(220).setListener(new HideViewAfterAnimation(this.flickerLoadingView)).start();
    }

    public RecyclerListView createListView() {
        RecyclerListView recyclerListView = new RecyclerListView(getContext()) {
            /* access modifiers changed from: protected */
            public void onMeasure(int widthSpec, int heightSpec) {
                int height = View.MeasureSpec.getSize(heightSpec);
                int listViewTotalHeight = AndroidUtilities.dp(8.0f) + (AndroidUtilities.dp(44.0f) * getAdapter().getItemCount());
                if (listViewTotalHeight > height) {
                    listViewTotalHeight = height;
                }
                super.onMeasure(widthSpec, View.MeasureSpec.makeMeasureSpec(listViewTotalHeight, NUM));
            }
        };
        recyclerListView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int p = parent.getChildAdapterPosition(view);
                if (p == 0) {
                    outRect.top = AndroidUtilities.dp(4.0f);
                }
                if (p == MessageSeenView.this.users.size() - 1) {
                    outRect.bottom = AndroidUtilities.dp(4.0f);
                }
            }
        });
        recyclerListView.setAdapter(new RecyclerListView.SelectionAdapter() {
            public boolean isEnabled(RecyclerView.ViewHolder holder) {
                return true;
            }

            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                UserCell userCell = new UserCell(parent.getContext());
                userCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                return new RecyclerListView.Holder(userCell);
            }

            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                ((UserCell) holder.itemView).setUser(MessageSeenView.this.users.get(position));
            }

            public int getItemCount() {
                return MessageSeenView.this.users.size();
            }
        });
        return recyclerListView;
    }

    private static class UserCell extends FrameLayout {
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        BackupImageView avatarImageView;
        TextView nameView;

        public UserCell(Context context) {
            super(context);
            BackupImageView backupImageView = new BackupImageView(context);
            this.avatarImageView = backupImageView;
            addView(backupImageView, LayoutHelper.createFrame(32, 32.0f, 16, 13.0f, 0.0f, 0.0f, 0.0f));
            this.avatarImageView.setRoundRadius(AndroidUtilities.dp(16.0f));
            TextView textView = new TextView(context);
            this.nameView = textView;
            textView.setTextSize(1, 16.0f);
            this.nameView.setLines(1);
            this.nameView.setEllipsize(TextUtils.TruncateAt.END);
            this.nameView.setImportantForAccessibility(2);
            addView(this.nameView, LayoutHelper.createFrame(-2, -2.0f, 19, 59.0f, 0.0f, 13.0f, 0.0f));
            this.nameView.setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44.0f), NUM));
        }

        public void setUser(TLRPC.User user) {
            if (user != null) {
                this.avatarDrawable.setInfo(user);
                this.avatarImageView.setImage(ImageLocation.getForUser(user, 1), "50_50", (Drawable) this.avatarDrawable, (Object) user);
                this.nameView.setText(ContactsController.formatName(user.first_name, user.last_name));
            }
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setText(LocaleController.formatString("AccDescrPersonHasSeen", NUM, this.nameView.getText()));
        }
    }
}
