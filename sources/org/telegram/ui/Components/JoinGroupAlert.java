package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatInvite;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_importChatInvite;
import org.telegram.tgnet.TLRPC.Updates;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.JoinSheetUserCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class JoinGroupAlert extends BottomSheet {
    private ChatInvite chatInvite;
    private BaseFragment fragment;
    private String hash;

    /* renamed from: org.telegram.ui.Components.JoinGroupAlert$1 */
    class C11921 implements OnClickListener {
        C11921() {
        }

        public void onClick(View view) {
            JoinGroupAlert.this.dismiss();
        }
    }

    /* renamed from: org.telegram.ui.Components.JoinGroupAlert$2 */
    class C11942 implements OnClickListener {
        C11942() {
        }

        public void onClick(View view) {
            JoinGroupAlert.this.dismiss();
            view = new TL_messages_importChatInvite();
            view.hash = JoinGroupAlert.this.hash;
            ConnectionsManager.getInstance(JoinGroupAlert.this.currentAccount).sendRequest(view, new RequestDelegate() {
                public void run(final TLObject tLObject, final TL_error tL_error) {
                    if (tL_error == null) {
                        MessagesController.getInstance(JoinGroupAlert.this.currentAccount).processUpdates((Updates) tLObject, false);
                    }
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (JoinGroupAlert.this.fragment != null) {
                                if (JoinGroupAlert.this.fragment.getParentActivity() != null) {
                                    if (tL_error == null) {
                                        Updates updates = (Updates) tLObject;
                                        if (!updates.chats.isEmpty()) {
                                            Chat chat = (Chat) updates.chats.get(0);
                                            chat.left = false;
                                            chat.kicked = false;
                                            MessagesController.getInstance(JoinGroupAlert.this.currentAccount).putUsers(updates.users, false);
                                            MessagesController.getInstance(JoinGroupAlert.this.currentAccount).putChats(updates.chats, false);
                                            Bundle bundle = new Bundle();
                                            bundle.putInt("chat_id", chat.id);
                                            if (MessagesController.getInstance(JoinGroupAlert.this.currentAccount).checkCanOpenChat(bundle, JoinGroupAlert.this.fragment)) {
                                                JoinGroupAlert.this.fragment.presentFragment(new ChatActivity(bundle), JoinGroupAlert.this.fragment instanceof ChatActivity);
                                            }
                                        }
                                    } else {
                                        AlertsCreator.processError(JoinGroupAlert.this.currentAccount, tL_error, JoinGroupAlert.this.fragment, view, new Object[0]);
                                    }
                                }
                            }
                        }
                    });
                }
            }, 2);
        }
    }

    private class UsersAdapter extends SelectionAdapter {
        private Context context;

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return 0;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return false;
        }

        public UsersAdapter(Context context) {
            this.context = context;
        }

        public int getItemCount() {
            int i;
            int size = JoinGroupAlert.this.chatInvite.participants.size();
            if (JoinGroupAlert.this.chatInvite.chat != null) {
                i = JoinGroupAlert.this.chatInvite.chat.participants_count;
            } else {
                i = JoinGroupAlert.this.chatInvite.participants_count;
            }
            return size != i ? size + 1 : size;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            viewGroup = new JoinSheetUserCell(this.context);
            viewGroup.setLayoutParams(new LayoutParams(AndroidUtilities.dp(100.0f), AndroidUtilities.dp(90.0f)));
            return new Holder(viewGroup);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            JoinSheetUserCell joinSheetUserCell = (JoinSheetUserCell) viewHolder.itemView;
            if (i < JoinGroupAlert.this.chatInvite.participants.size()) {
                joinSheetUserCell.setUser((User) JoinGroupAlert.this.chatInvite.participants.get(i));
                return;
            }
            if (JoinGroupAlert.this.chatInvite.chat != 0) {
                i = JoinGroupAlert.this.chatInvite.chat.participants_count;
            } else {
                i = JoinGroupAlert.this.chatInvite.participants_count;
            }
            joinSheetUserCell.setCount(i - JoinGroupAlert.this.chatInvite.participants.size());
        }
    }

    public JoinGroupAlert(Context context, ChatInvite chatInvite, String str, BaseFragment baseFragment) {
        Drawable avatarDrawable;
        CharSequence charSequence;
        int i;
        View recyclerListView;
        Context context2 = context;
        ChatInvite chatInvite2 = chatInvite;
        super(context2, false);
        setApplyBottomPadding(false);
        setApplyTopPadding(false);
        this.fragment = baseFragment;
        this.chatInvite = chatInvite2;
        this.hash = str;
        View linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(1);
        linearLayout.setClickable(true);
        setCustomView(linearLayout);
        TLObject tLObject = null;
        if (chatInvite2.chat != null) {
            avatarDrawable = new AvatarDrawable(chatInvite2.chat);
            if (r0.chatInvite.chat.photo != null) {
                tLObject = r0.chatInvite.chat.photo.photo_small;
            }
            charSequence = chatInvite2.chat.title;
            i = chatInvite2.chat.participants_count;
        } else {
            avatarDrawable = new AvatarDrawable();
            avatarDrawable.setInfo(0, chatInvite2.title, null, false);
            if (r0.chatInvite.photo != null) {
                tLObject = r0.chatInvite.photo.photo_small;
            }
            charSequence = chatInvite2.title;
            i = chatInvite2.participants_count;
        }
        View backupImageView = new BackupImageView(context2);
        backupImageView.setRoundRadius(AndroidUtilities.dp(35.0f));
        backupImageView.setImage(tLObject, "50_50", avatarDrawable);
        linearLayout.addView(backupImageView, LayoutHelper.createLinear(70, 70, 49, 0, 12, 0, 0));
        View textView = new TextView(context2);
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setTextSize(1, 17.0f);
        textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        textView.setText(charSequence);
        textView.setSingleLine(true);
        textView.setEllipsize(TruncateAt.END);
        linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, 49, 10, 10, 10, i > 0 ? 0 : 10));
        if (i > 0) {
            textView = new TextView(context2);
            textView.setTextSize(1, 14.0f);
            textView.setTextColor(Theme.getColor(Theme.key_dialogTextGray3));
            textView.setSingleLine(true);
            textView.setEllipsize(TruncateAt.END);
            textView.setText(LocaleController.formatPluralString("Members", i));
            linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, 49, 10, 4, 10, 10));
        }
        if (!chatInvite2.participants.isEmpty()) {
            recyclerListView = new RecyclerListView(context2);
            recyclerListView.setPadding(0, 0, 0, AndroidUtilities.dp(8.0f));
            recyclerListView.setNestedScrollingEnabled(false);
            recyclerListView.setClipToPadding(false);
            recyclerListView.setLayoutManager(new LinearLayoutManager(getContext(), 0, false));
            recyclerListView.setHorizontalScrollBarEnabled(false);
            recyclerListView.setVerticalScrollBarEnabled(false);
            recyclerListView.setAdapter(new UsersAdapter(context2));
            recyclerListView.setGlowColor(Theme.getColor(Theme.key_dialogScrollGlow));
            linearLayout.addView(recyclerListView, LayoutHelper.createLinear(-2, 90, 49, 0, 0, 0, 0));
        }
        recyclerListView = new View(context2);
        recyclerListView.setBackgroundResource(C0446R.drawable.header_shadow_reverse);
        linearLayout.addView(recyclerListView, LayoutHelper.createLinear(-1, 3));
        recyclerListView = new PickerBottomLayout(context2, false);
        linearLayout.addView(recyclerListView, LayoutHelper.createFrame(-1, 48, 83));
        recyclerListView.cancelButton.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        recyclerListView.cancelButton.setTextColor(Theme.getColor(Theme.key_dialogTextBlue2));
        recyclerListView.cancelButton.setText(LocaleController.getString("Cancel", C0446R.string.Cancel).toUpperCase());
        recyclerListView.cancelButton.setOnClickListener(new C11921());
        recyclerListView.doneButton.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        recyclerListView.doneButton.setVisibility(0);
        recyclerListView.doneButtonBadgeTextView.setVisibility(8);
        recyclerListView.doneButtonTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlue2));
        recyclerListView.doneButtonTextView.setText(LocaleController.getString("JoinGroup", C0446R.string.JoinGroup));
        recyclerListView.doneButton.setOnClickListener(new C11942());
    }
}
