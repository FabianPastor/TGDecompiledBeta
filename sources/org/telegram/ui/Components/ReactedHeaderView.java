package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;

public class ReactedHeaderView extends FrameLayout {
    private AvatarsImageView avatarsImageView;
    private int currentAccount;
    private long dialogId;
    private FlickerLoadingView flickerLoadingView;
    private ImageView iconView;
    private boolean ignoreLayout;
    private boolean isLoaded;
    private MessageObject message;
    private BackupImageView reactView;
    private Consumer<List<TLRPC.User>> seenCallback;
    private List<TLRPC.User> seenUsers = new ArrayList();
    private TextView titleView;
    private List<TLRPC.User> users = new ArrayList();

    public ReactedHeaderView(Context context, int currentAccount2, MessageObject message2, long dialogId2) {
        super(context);
        this.currentAccount = currentAccount2;
        this.message = message2;
        this.dialogId = dialogId2;
        FlickerLoadingView flickerLoadingView2 = new FlickerLoadingView(context);
        this.flickerLoadingView = flickerLoadingView2;
        flickerLoadingView2.setColors("actionBarDefaultSubmenuBackground", "listSelectorSDK21", (String) null);
        this.flickerLoadingView.setViewType(13);
        this.flickerLoadingView.setIsSingleCell(false);
        addView(this.flickerLoadingView, LayoutHelper.createFrame(-2, -1.0f));
        TextView textView = new TextView(context);
        this.titleView = textView;
        textView.setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
        this.titleView.setTextSize(1, 16.0f);
        this.titleView.setLines(1);
        this.titleView.setEllipsize(TextUtils.TruncateAt.END);
        addView(this.titleView, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388627, 40.0f, 0.0f, 62.0f, 0.0f));
        AvatarsImageView avatarsImageView2 = new AvatarsImageView(context, false);
        this.avatarsImageView = avatarsImageView2;
        avatarsImageView2.setStyle(11);
        addView(this.avatarsImageView, LayoutHelper.createFrameRelatively(56.0f, -1.0f, 8388629, 0.0f, 0.0f, 0.0f, 0.0f));
        ImageView imageView = new ImageView(context);
        this.iconView = imageView;
        addView(imageView, LayoutHelper.createFrameRelatively(24.0f, 24.0f, 8388627, 11.0f, 0.0f, 0.0f, 0.0f));
        Drawable drawable = ContextCompat.getDrawable(context, NUM).mutate();
        drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("actionBarDefaultSubmenuItemIcon"), PorterDuff.Mode.MULTIPLY));
        this.iconView.setImageDrawable(drawable);
        this.iconView.setVisibility(8);
        BackupImageView backupImageView = new BackupImageView(context);
        this.reactView = backupImageView;
        addView(backupImageView, LayoutHelper.createFrameRelatively(24.0f, 24.0f, 8388627, 11.0f, 0.0f, 0.0f, 0.0f));
        this.titleView.setAlpha(0.0f);
        this.avatarsImageView.setAlpha(0.0f);
        setBackground(Theme.getSelectorDrawable(false));
    }

    public void setSeenCallback(Consumer<List<TLRPC.User>> seenCallback2) {
        this.seenCallback = seenCallback2;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!this.isLoaded) {
            MessagesController ctrl = MessagesController.getInstance(this.currentAccount);
            TLRPC.Chat chat = ctrl.getChat(Long.valueOf(this.message.getChatId()));
            TLRPC.ChatFull chatInfo = ctrl.getChatFull(this.message.getChatId());
            if (chat != null && this.message.isOutOwner() && this.message.isSent() && !this.message.isEditing() && !this.message.isSending() && !this.message.isSendError() && !this.message.isContentUnread() && !this.message.isUnread() && ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - this.message.messageOwner.date < 604800 && (ChatObject.isMegagroup(chat) || !ChatObject.isChannel(chat)) && chatInfo != null && chatInfo.participants_count <= MessagesController.getInstance(this.currentAccount).chatReadMarkSizeThreshold && !(this.message.messageOwner.action instanceof TLRPC.TL_messageActionChatJoinedByRequest)) {
                TLRPC.TL_messages_getMessageReadParticipants req = new TLRPC.TL_messages_getMessageReadParticipants();
                req.msg_id = this.message.getId();
                req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.message.getDialogId());
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new ReactedHeaderView$$ExternalSyntheticLambda5(this, this.message.messageOwner.from_id != null ? this.message.messageOwner.from_id.user_id : 0, chat), 64);
                return;
            }
            loadReactions();
        }
    }

    /* renamed from: lambda$onAttachedToWindow$5$org-telegram-ui-Components-ReactedHeaderView  reason: not valid java name */
    public /* synthetic */ void m1288xe1027737(long fromId, TLRPC.Chat chat, TLObject response, TLRPC.TL_error error) {
        if (response instanceof TLRPC.Vector) {
            List<Long> usersToRequest = new ArrayList<>();
            Iterator<Object> it = ((TLRPC.Vector) response).objects.iterator();
            while (it.hasNext()) {
                Object obj = it.next();
                if (obj instanceof Long) {
                    long l = ((Long) obj).longValue();
                    if (fromId != l) {
                        usersToRequest.add(Long.valueOf(l));
                    }
                }
            }
            usersToRequest.add(Long.valueOf(fromId));
            List<TLRPC.User> usersRes = new ArrayList<>();
            Runnable callback = new ReactedHeaderView$$ExternalSyntheticLambda1(this, usersRes);
            if (ChatObject.isChannel(chat)) {
                TLRPC.TL_channels_getParticipants usersReq = new TLRPC.TL_channels_getParticipants();
                usersReq.limit = MessagesController.getInstance(this.currentAccount).chatReadMarkSizeThreshold;
                usersReq.offset = 0;
                usersReq.filter = new TLRPC.TL_channelParticipantsRecent();
                usersReq.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(chat.id);
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(usersReq, new ReactedHeaderView$$ExternalSyntheticLambda6(this, usersToRequest, usersRes, callback));
                return;
            }
            TLRPC.TL_messages_getFullChat usersReq2 = new TLRPC.TL_messages_getFullChat();
            usersReq2.chat_id = chat.id;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(usersReq2, new ReactedHeaderView$$ExternalSyntheticLambda7(this, usersToRequest, usersRes, callback));
        }
    }

    /* renamed from: lambda$onAttachedToWindow$0$org-telegram-ui-Components-ReactedHeaderView  reason: not valid java name */
    public /* synthetic */ void m1283x41acb31c(List usersRes) {
        this.seenUsers.addAll(usersRes);
        Iterator it = usersRes.iterator();
        while (it.hasNext()) {
            TLRPC.User u = (TLRPC.User) it.next();
            boolean hasSame = false;
            int i = 0;
            while (true) {
                if (i >= this.users.size()) {
                    break;
                } else if (this.users.get(i).id == u.id) {
                    hasSame = true;
                    break;
                } else {
                    i++;
                }
            }
            if (!hasSame) {
                this.users.add(u);
            }
        }
        Consumer<List<TLRPC.User>> consumer = this.seenCallback;
        if (consumer != null) {
            consumer.accept(usersRes);
        }
        loadReactions();
    }

    /* renamed from: lambda$onAttachedToWindow$2$org-telegram-ui-Components-ReactedHeaderView  reason: not valid java name */
    public /* synthetic */ void m1285xb49bce5a(List usersToRequest, List usersRes, Runnable callback, TLObject response1, TLRPC.TL_error error1) {
        AndroidUtilities.runOnUIThread(new ReactedHeaderView$$ExternalSyntheticLambda2(this, response1, usersToRequest, usersRes, callback));
    }

    /* renamed from: lambda$onAttachedToWindow$1$org-telegram-ui-Components-ReactedHeaderView  reason: not valid java name */
    public /* synthetic */ void m1284xfb2440bb(TLObject response1, List usersToRequest, List usersRes, Runnable callback) {
        if (response1 != null) {
            TLRPC.TL_channels_channelParticipants users2 = (TLRPC.TL_channels_channelParticipants) response1;
            for (int i = 0; i < users2.users.size(); i++) {
                TLRPC.User user = users2.users.get(i);
                MessagesController.getInstance(this.currentAccount).putUser(user, false);
                if (!user.self && usersToRequest.contains(Long.valueOf(user.id))) {
                    usersRes.add(user);
                }
            }
        }
        callback.run();
    }

    /* renamed from: lambda$onAttachedToWindow$4$org-telegram-ui-Components-ReactedHeaderView  reason: not valid java name */
    public /* synthetic */ void m1287x278ae998(List usersToRequest, List usersRes, Runnable callback, TLObject response1, TLRPC.TL_error error1) {
        AndroidUtilities.runOnUIThread(new ReactedHeaderView$$ExternalSyntheticLambda3(this, response1, usersToRequest, usersRes, callback));
    }

    /* renamed from: lambda$onAttachedToWindow$3$org-telegram-ui-Components-ReactedHeaderView  reason: not valid java name */
    public /* synthetic */ void m1286x6e135bf9(TLObject response1, List usersToRequest, List usersRes, Runnable callback) {
        if (response1 != null) {
            TLRPC.TL_messages_chatFull chatFull = (TLRPC.TL_messages_chatFull) response1;
            for (int i = 0; i < chatFull.users.size(); i++) {
                TLRPC.User user = chatFull.users.get(i);
                MessagesController.getInstance(this.currentAccount).putUser(user, false);
                if (!user.self && usersToRequest.contains(Long.valueOf(user.id))) {
                    usersRes.add(user);
                }
            }
        }
        callback.run();
    }

    private void loadReactions() {
        MessagesController ctrl = MessagesController.getInstance(this.currentAccount);
        TLRPC.TL_messages_getMessageReactionsList getList = new TLRPC.TL_messages_getMessageReactionsList();
        getList.peer = ctrl.getInputPeer(this.message.getDialogId());
        getList.id = this.message.getId();
        getList.limit = 3;
        getList.reaction = null;
        getList.offset = null;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(getList, new ReactedHeaderView$$ExternalSyntheticLambda4(this), 64);
    }

    /* renamed from: lambda$loadReactions$7$org-telegram-ui-Components-ReactedHeaderView  reason: not valid java name */
    public /* synthetic */ void m1282xd9ccebf9(TLObject response, TLRPC.TL_error error) {
        if (response instanceof TLRPC.TL_messages_messageReactionsList) {
            TLRPC.TL_messages_messageReactionsList list = (TLRPC.TL_messages_messageReactionsList) response;
            post(new ReactedHeaderView$$ExternalSyntheticLambda0(this, list.count, list));
        }
    }

    /* renamed from: lambda$loadReactions$6$org-telegram-ui-Components-ReactedHeaderView  reason: not valid java name */
    public /* synthetic */ void m1281x20555e5a(int c, TLRPC.TL_messages_messageReactionsList list) {
        String str;
        String countStr;
        if (this.seenUsers.isEmpty() || this.seenUsers.size() < c) {
            str = LocaleController.formatPluralString("ReactionsCount", c, new Object[0]);
        } else {
            if (c == this.seenUsers.size()) {
                countStr = String.valueOf(c);
            } else {
                countStr = c + "/" + this.seenUsers.size();
            }
            str = String.format(LocaleController.getPluralString("Reacted", c), new Object[]{countStr});
        }
        this.titleView.setText(str);
        boolean showIcon = true;
        if (this.message.messageOwner.reactions != null && this.message.messageOwner.reactions.results.size() == 1 && !list.reactions.isEmpty()) {
            Iterator<TLRPC.TL_availableReaction> it = MediaDataController.getInstance(this.currentAccount).getReactionsList().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                TLRPC.TL_availableReaction r = it.next();
                if (r.reaction.equals(list.reactions.get(0).reaction)) {
                    this.reactView.setImage(ImageLocation.getForDocument(r.center_icon), "40_40_lastframe", "webp", (Drawable) null, (Object) r);
                    this.reactView.setVisibility(0);
                    this.reactView.setAlpha(0.0f);
                    this.reactView.animate().alpha(1.0f).start();
                    this.iconView.setVisibility(8);
                    showIcon = false;
                    break;
                }
            }
        }
        if (showIcon) {
            this.iconView.setVisibility(0);
            this.iconView.setAlpha(0.0f);
            this.iconView.animate().alpha(1.0f).start();
        }
        Iterator<TLRPC.User> it2 = list.users.iterator();
        while (it2.hasNext()) {
            TLRPC.User u = it2.next();
            if (!(this.message.messageOwner.from_id == null || u.id == this.message.messageOwner.from_id.user_id)) {
                boolean hasSame = false;
                int i = 0;
                while (true) {
                    if (i >= this.users.size()) {
                        break;
                    } else if (this.users.get(i).id == u.id) {
                        hasSame = true;
                        break;
                    } else {
                        i++;
                    }
                }
                if (!hasSame) {
                    this.users.add(u);
                }
            }
        }
        updateView();
    }

    public List<TLRPC.User> getSeenUsers() {
        return this.seenUsers;
    }

    private void updateView() {
        float tX;
        setEnabled(this.users.size() > 0);
        for (int i = 0; i < 3; i++) {
            if (i < this.users.size()) {
                this.avatarsImageView.setObject(i, this.currentAccount, this.users.get(i));
            } else {
                this.avatarsImageView.setObject(i, this.currentAccount, (TLObject) null);
            }
        }
        switch (this.users.size()) {
            case 1:
                tX = (float) AndroidUtilities.dp(24.0f);
                break;
            case 2:
                tX = (float) AndroidUtilities.dp(12.0f);
                break;
            default:
                tX = 0.0f;
                break;
        }
        this.avatarsImageView.setTranslationX(LocaleController.isRTL ? (float) AndroidUtilities.dp(12.0f) : tX);
        this.avatarsImageView.commitTransition(false);
        this.titleView.animate().alpha(1.0f).setDuration(220).start();
        this.avatarsImageView.animate().alpha(1.0f).setDuration(220).start();
        this.flickerLoadingView.animate().alpha(0.0f).setDuration(220).setListener(new HideViewAfterAnimation(this.flickerLoadingView)).start();
    }

    public void requestLayout() {
        if (!this.ignoreLayout) {
            super.requestLayout();
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
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
}
