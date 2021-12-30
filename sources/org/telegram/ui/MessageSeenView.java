package org.telegram.ui;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
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
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$TL_channelParticipantsRecent;
import org.telegram.tgnet.TLRPC$TL_channels_channelParticipants;
import org.telegram.tgnet.TLRPC$TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_chatFull;
import org.telegram.tgnet.TLRPC$TL_messages_getFullChat;
import org.telegram.tgnet.TLRPC$TL_messages_getMessageReadParticipants;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$Vector;
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
    public ArrayList<TLRPC$User> users = new ArrayList<>();

    public MessageSeenView(Context context, int i, MessageObject messageObject, TLRPC$Chat tLRPC$Chat) {
        super(context);
        this.currentAccount = i;
        this.isVoice = messageObject.isRoundVideo() || messageObject.isVoice();
        FlickerLoadingView flickerLoadingView2 = new FlickerLoadingView(context);
        this.flickerLoadingView = flickerLoadingView2;
        flickerLoadingView2.setColors("actionBarDefaultSubmenuBackground", "listSelectorSDK21", (String) null);
        this.flickerLoadingView.setViewType(13);
        this.flickerLoadingView.setIsSingleCell(false);
        addView(this.flickerLoadingView, LayoutHelper.createFrame(-2, -1.0f));
        TextView textView = new TextView(context);
        this.titleView = textView;
        textView.setTextSize(1, 16.0f);
        this.titleView.setLines(1);
        this.titleView.setEllipsize(TextUtils.TruncateAt.END);
        addView(this.titleView, LayoutHelper.createFrame(-2, -2.0f, 19, 40.0f, 0.0f, 62.0f, 0.0f));
        AvatarsImageView avatarsImageView2 = new AvatarsImageView(context, false);
        this.avatarsImageView = avatarsImageView2;
        avatarsImageView2.setStyle(11);
        addView(this.avatarsImageView, LayoutHelper.createFrame(56, -1.0f, 21, 0.0f, 0.0f, 0.0f, 0.0f));
        this.titleView.setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
        TLRPC$TL_messages_getMessageReadParticipants tLRPC$TL_messages_getMessageReadParticipants = new TLRPC$TL_messages_getMessageReadParticipants();
        tLRPC$TL_messages_getMessageReadParticipants.msg_id = messageObject.getId();
        tLRPC$TL_messages_getMessageReadParticipants.peer = MessagesController.getInstance(i).getInputPeer(messageObject.getDialogId());
        ImageView imageView = new ImageView(context);
        this.iconView = imageView;
        addView(imageView, LayoutHelper.createFrame(24, 24.0f, 19, 11.0f, 0.0f, 0.0f, 0.0f));
        Drawable mutate = ContextCompat.getDrawable(context, this.isVoice ? NUM : NUM).mutate();
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("actionBarDefaultSubmenuItemIcon"), PorterDuff.Mode.MULTIPLY));
        this.iconView.setImageDrawable(mutate);
        this.avatarsImageView.setAlpha(0.0f);
        this.titleView.setAlpha(0.0f);
        long j = 0;
        TLRPC$Peer tLRPC$Peer = messageObject.messageOwner.from_id;
        ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_messages_getMessageReadParticipants, new MessageSeenView$$ExternalSyntheticLambda5(this, tLRPC$Peer != null ? tLRPC$Peer.user_id : j, i, tLRPC$Chat));
        setBackground(Theme.createRadSelectorDrawable(Theme.getColor("dialogButtonSelector"), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f)));
        setEnabled(false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$5(long j, int i, TLRPC$Chat tLRPC$Chat, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new MessageSeenView$$ExternalSyntheticLambda2(this, tLRPC$TL_error, tLObject, j, i, tLRPC$Chat));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$4(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, long j, int i, TLRPC$Chat tLRPC$Chat) {
        if (tLRPC$TL_error == null) {
            TLRPC$Vector tLRPC$Vector = (TLRPC$Vector) tLObject;
            ArrayList arrayList = new ArrayList();
            HashMap hashMap = new HashMap();
            ArrayList arrayList2 = new ArrayList();
            int size = tLRPC$Vector.objects.size();
            for (int i2 = 0; i2 < size; i2++) {
                Object obj = tLRPC$Vector.objects.get(i2);
                if (obj instanceof Long) {
                    Long l = (Long) obj;
                    if (j != l.longValue()) {
                        MessagesController.getInstance(i).getUser(l);
                        arrayList2.add(l);
                        arrayList.add(l);
                    }
                }
            }
            if (arrayList.isEmpty()) {
                for (int i3 = 0; i3 < arrayList2.size(); i3++) {
                    this.peerIds.add((Long) arrayList2.get(i3));
                    this.users.add((TLRPC$User) hashMap.get(arrayList2.get(i3)));
                }
                updateView();
            } else if (ChatObject.isChannel(tLRPC$Chat)) {
                TLRPC$TL_channels_getParticipants tLRPC$TL_channels_getParticipants = new TLRPC$TL_channels_getParticipants();
                tLRPC$TL_channels_getParticipants.limit = 50;
                tLRPC$TL_channels_getParticipants.offset = 0;
                tLRPC$TL_channels_getParticipants.filter = new TLRPC$TL_channelParticipantsRecent();
                tLRPC$TL_channels_getParticipants.channel = MessagesController.getInstance(i).getInputChannel(tLRPC$Chat.id);
                ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_channels_getParticipants, new MessageSeenView$$ExternalSyntheticLambda4(this, i, hashMap, arrayList2));
            } else {
                TLRPC$TL_messages_getFullChat tLRPC$TL_messages_getFullChat = new TLRPC$TL_messages_getFullChat();
                tLRPC$TL_messages_getFullChat.chat_id = tLRPC$Chat.id;
                ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_messages_getFullChat, new MessageSeenView$$ExternalSyntheticLambda3(this, i, hashMap, arrayList2));
            }
        } else {
            updateView();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(int i, HashMap hashMap, ArrayList arrayList, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new MessageSeenView$$ExternalSyntheticLambda1(this, tLObject, i, hashMap, arrayList));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(TLObject tLObject, int i, HashMap hashMap, ArrayList arrayList) {
        if (tLObject != null) {
            TLRPC$TL_channels_channelParticipants tLRPC$TL_channels_channelParticipants = (TLRPC$TL_channels_channelParticipants) tLObject;
            for (int i2 = 0; i2 < tLRPC$TL_channels_channelParticipants.users.size(); i2++) {
                TLRPC$User tLRPC$User = tLRPC$TL_channels_channelParticipants.users.get(i2);
                MessagesController.getInstance(i).putUser(tLRPC$User, false);
                hashMap.put(Long.valueOf(tLRPC$User.id), tLRPC$User);
            }
            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                this.peerIds.add((Long) arrayList.get(i3));
                this.users.add((TLRPC$User) hashMap.get(arrayList.get(i3)));
            }
        }
        updateView();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(int i, HashMap hashMap, ArrayList arrayList, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new MessageSeenView$$ExternalSyntheticLambda0(this, tLObject, i, hashMap, arrayList));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(TLObject tLObject, int i, HashMap hashMap, ArrayList arrayList) {
        if (tLObject != null) {
            TLRPC$TL_messages_chatFull tLRPC$TL_messages_chatFull = (TLRPC$TL_messages_chatFull) tLObject;
            for (int i2 = 0; i2 < tLRPC$TL_messages_chatFull.users.size(); i2++) {
                TLRPC$User tLRPC$User = tLRPC$TL_messages_chatFull.users.get(i2);
                MessagesController.getInstance(i).putUser(tLRPC$User, false);
                hashMap.put(Long.valueOf(tLRPC$User.id), tLRPC$User);
            }
            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                this.peerIds.add((Long) arrayList.get(i3));
                this.users.add((TLRPC$User) hashMap.get(arrayList.get(i3)));
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
    public void onMeasure(int i, int i2) {
        if (this.flickerLoadingView.getVisibility() == 0) {
            this.ignoreLayout = true;
            this.flickerLoadingView.setVisibility(8);
            super.onMeasure(i, i2);
            this.flickerLoadingView.getLayoutParams().width = getMeasuredWidth();
            this.flickerLoadingView.setVisibility(0);
            this.ignoreLayout = false;
            super.onMeasure(i, i2);
            return;
        }
        super.onMeasure(i, i2);
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
        this.avatarsImageView.commitTransition(false);
        if (this.peerIds.size() == 1 && this.users.get(0) != null) {
            this.titleView.setText(ContactsController.formatName(this.users.get(0).first_name, this.users.get(0).last_name));
        } else if (this.peerIds.size() == 0) {
            this.titleView.setText(LocaleController.getString(LocaleController.getString("NobodyViewed", NUM)));
        } else {
            this.titleView.setText(LocaleController.formatPluralString(this.isVoice ? "MessagePlayed" : "MessageSeen", this.peerIds.size()));
        }
        this.titleView.animate().alpha(1.0f).setDuration(220).start();
        this.avatarsImageView.animate().alpha(1.0f).setDuration(220).start();
        this.flickerLoadingView.animate().alpha(0.0f).setDuration(220).setListener(new HideViewAfterAnimation(this.flickerLoadingView)).start();
    }

    public RecyclerListView createListView() {
        RecyclerListView recyclerListView = new RecyclerListView(getContext());
        recyclerListView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
                int childAdapterPosition = recyclerView.getChildAdapterPosition(view);
                if (childAdapterPosition == 0) {
                    rect.top = AndroidUtilities.dp(4.0f);
                }
                if (childAdapterPosition == MessageSeenView.this.users.size() - 1) {
                    rect.bottom = AndroidUtilities.dp(4.0f);
                }
            }
        });
        recyclerListView.setAdapter(new RecyclerListView.SelectionAdapter() {
            public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                return true;
            }

            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                UserCell userCell = new UserCell(viewGroup.getContext());
                userCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                return new RecyclerListView.Holder(userCell);
            }

            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                ((UserCell) viewHolder.itemView).setUser(MessageSeenView.this.users.get(i));
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
            addView(this.nameView, LayoutHelper.createFrame(-2, -2.0f, 19, 59.0f, 0.0f, 13.0f, 0.0f));
            this.nameView.setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44.0f), NUM));
        }

        public void setUser(TLRPC$User tLRPC$User) {
            if (tLRPC$User != null) {
                this.avatarDrawable.setInfo(tLRPC$User);
                this.avatarImageView.setImage(ImageLocation.getForUser(tLRPC$User, 1), "50_50", (Drawable) this.avatarDrawable, (Object) tLRPC$User);
                this.nameView.setText(ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name));
            }
        }
    }
}
