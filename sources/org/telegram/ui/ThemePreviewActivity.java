package org.telegram.ui;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.style.CharacterStyle;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.LayoutParams;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import java.io.File;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_peerChat;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_photo;
import org.telegram.tgnet.TLRPC.TL_photoSize;
import org.telegram.tgnet.TLRPC.TL_pollAnswer;
import org.telegram.tgnet.TLRPC.TL_user;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.MenuDrawable;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate;
import org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate.-CC;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.DialogCell.CustomDialog;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.SizeNotifierFrameLayout;

public class ThemePreviewActivity extends BaseFragment implements NotificationCenterDelegate {
    private ActionBar actionBar2;
    private boolean applied;
    private ThemeInfo applyingTheme;
    private DialogsAdapter dialogsAdapter;
    private View dotsContainer;
    private ImageView floatingButton;
    private RecyclerListView listView;
    private RecyclerListView listView2;
    private MessagesAdapter messagesAdapter;
    private FrameLayout page1;
    private SizeNotifierFrameLayout page2;
    private File themeFile;

    public class DialogsAdapter extends SelectionAdapter {
        private ArrayList<CustomDialog> dialogs = new ArrayList();
        private Context mContext;

        public DialogsAdapter(Context context) {
            this.mContext = context;
            int currentTimeMillis = (int) (System.currentTimeMillis() / 1000);
            CustomDialog customDialog = new CustomDialog();
            customDialog.name = "Eva Summer";
            customDialog.message = "Reminds me of a Chinese prove...";
            customDialog.id = 0;
            customDialog.unread_count = 0;
            customDialog.pinned = true;
            customDialog.muted = false;
            customDialog.type = 0;
            customDialog.date = currentTimeMillis;
            customDialog.verified = false;
            customDialog.isMedia = false;
            customDialog.sent = true;
            this.dialogs.add(customDialog);
            customDialog = new CustomDialog();
            customDialog.name = "Your inner Competition";
            customDialog.message = "hey, I've updated the source code.";
            customDialog.id = 1;
            customDialog.unread_count = 2;
            customDialog.pinned = false;
            customDialog.muted = false;
            customDialog.type = 0;
            customDialog.date = currentTimeMillis - 3600;
            customDialog.verified = false;
            customDialog.isMedia = false;
            customDialog.sent = false;
            this.dialogs.add(customDialog);
            customDialog = new CustomDialog();
            customDialog.name = "Mike Apple";
            customDialog.message = "🤷‍♂️ Sticker";
            customDialog.id = 2;
            customDialog.unread_count = 3;
            customDialog.pinned = false;
            customDialog.muted = true;
            customDialog.type = 0;
            customDialog.date = currentTimeMillis - 7200;
            customDialog.verified = false;
            customDialog.isMedia = true;
            customDialog.sent = false;
            this.dialogs.add(customDialog);
            customDialog = new CustomDialog();
            customDialog.name = "Paul Newman";
            customDialog.message = "Any ideas?";
            customDialog.id = 3;
            customDialog.unread_count = 0;
            customDialog.pinned = false;
            customDialog.muted = false;
            customDialog.type = 2;
            customDialog.date = currentTimeMillis - 10800;
            customDialog.verified = false;
            customDialog.isMedia = false;
            customDialog.sent = false;
            this.dialogs.add(customDialog);
            customDialog = new CustomDialog();
            customDialog.name = "Old Pirates";
            customDialog.message = "Yo-ho-ho!";
            customDialog.id = 4;
            customDialog.unread_count = 0;
            customDialog.pinned = false;
            customDialog.muted = false;
            customDialog.type = 1;
            customDialog.date = currentTimeMillis - 14400;
            customDialog.verified = false;
            customDialog.isMedia = false;
            customDialog.sent = true;
            this.dialogs.add(customDialog);
            customDialog = new CustomDialog();
            customDialog.name = "Kate Bright";
            customDialog.message = "Hola!";
            customDialog.id = 5;
            customDialog.unread_count = 0;
            customDialog.pinned = false;
            customDialog.muted = false;
            customDialog.type = 0;
            customDialog.date = currentTimeMillis - 18000;
            customDialog.verified = false;
            customDialog.isMedia = false;
            customDialog.sent = false;
            this.dialogs.add(customDialog);
            customDialog = new CustomDialog();
            customDialog.name = "Nick K";
            customDialog.message = "These are not the droids you are looking for";
            customDialog.id = 6;
            customDialog.unread_count = 0;
            customDialog.pinned = false;
            customDialog.muted = false;
            customDialog.type = 0;
            customDialog.date = currentTimeMillis - 21600;
            customDialog.verified = true;
            customDialog.isMedia = false;
            customDialog.sent = false;
            this.dialogs.add(customDialog);
            customDialog = new CustomDialog();
            customDialog.name = "Adler Toberg";
            customDialog.message = "Did someone say peanut butter?";
            customDialog.id = 0;
            customDialog.unread_count = 0;
            customDialog.pinned = false;
            customDialog.muted = false;
            customDialog.type = 0;
            customDialog.date = currentTimeMillis - 25200;
            customDialog.verified = true;
            customDialog.isMedia = false;
            customDialog.sent = false;
            this.dialogs.add(customDialog);
        }

        public int getItemCount() {
            return this.dialogs.size();
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return viewHolder.getItemViewType() != 1;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View dialogCell = i == 0 ? new DialogCell(this.mContext, false, false) : i == 1 ? new LoadingCell(this.mContext) : null;
            dialogCell.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(dialogCell);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                DialogCell dialogCell = (DialogCell) viewHolder.itemView;
                boolean z = true;
                if (i == getItemCount() - 1) {
                    z = false;
                }
                dialogCell.useSeparator = z;
                dialogCell.setDialog((CustomDialog) this.dialogs.get(i));
            }
        }

        public int getItemViewType(int i) {
            return i == this.dialogs.size() ? 1 : 0;
        }
    }

    public class MessagesAdapter extends SelectionAdapter {
        private Context mContext;
        private ArrayList<MessageObject> messages;
        private boolean showSecretMessages;

        public boolean isEnabled(ViewHolder viewHolder) {
            return false;
        }

        public MessagesAdapter(Context context) {
            TL_message tL_message;
            this.showSecretMessages = Utilities.random.nextInt(100) <= (BuildVars.DEBUG_VERSION ? 5 : 1);
            this.mContext = context;
            this.messages = new ArrayList();
            int currentTimeMillis = ((int) (System.currentTimeMillis() / 1000)) - 3600;
            if (this.showSecretMessages) {
                TL_user tL_user = new TL_user();
                tL_user.id = Integer.MAX_VALUE;
                tL_user.first_name = "Me";
                TL_user tL_user2 = new TL_user();
                tL_user2.id = NUM;
                tL_user2.first_name = "Serj";
                ArrayList arrayList = new ArrayList();
                arrayList.add(tL_user);
                arrayList.add(tL_user2);
                MessagesController.getInstance(ThemePreviewActivity.this.currentAccount).putUsers(arrayList, true);
                TL_message tL_message2 = new TL_message();
                tL_message2.message = "Guess why Half-Life 3 was never released.";
                int i = currentTimeMillis + 960;
                tL_message2.date = i;
                tL_message2.dialog_id = -1;
                tL_message2.flags = 259;
                tL_message2.id = NUM;
                tL_message2.media = new TL_messageMediaEmpty();
                tL_message2.out = false;
                tL_message2.to_id = new TL_peerChat();
                tL_message2.to_id.chat_id = 1;
                tL_message2.from_id = tL_user2.id;
                this.messages.add(new MessageObject(ThemePreviewActivity.this.currentAccount, tL_message2, true));
                TL_message tL_message3 = new TL_message();
                tL_message3.message = "No.\nAnd every unnecessary ping of the dev delays the release for 10 days.\nEvery request for ETA delays the release for 2 weeks.";
                tL_message3.date = i;
                tL_message3.dialog_id = -1;
                tL_message3.flags = 259;
                tL_message3.id = 1;
                tL_message3.media = new TL_messageMediaEmpty();
                tL_message3.out = false;
                tL_message3.to_id = new TL_peerChat();
                tL_message3.to_id.chat_id = 1;
                tL_message3.from_id = tL_user2.id;
                this.messages.add(new MessageObject(ThemePreviewActivity.this.currentAccount, tL_message3, true));
                TL_message tL_message4 = new TL_message();
                tL_message4.message = "Is source code for Android coming anytime soon?";
                tL_message4.date = currentTimeMillis + 600;
                tL_message4.dialog_id = -1;
                tL_message4.flags = 259;
                tL_message4.id = 1;
                tL_message4.media = new TL_messageMediaEmpty();
                tL_message4.out = false;
                tL_message4.to_id = new TL_peerChat();
                tL_message4.to_id.chat_id = 1;
                tL_message4.from_id = tL_user.id;
                this.messages.add(new MessageObject(ThemePreviewActivity.this.currentAccount, tL_message4, true));
            } else {
                tL_message = new TL_message();
                tL_message.message = "Reinhardt, we need to find you some new tunes 🎶.";
                int i2 = currentTimeMillis + 60;
                tL_message.date = i2;
                tL_message.dialog_id = 1;
                tL_message.flags = 259;
                tL_message.from_id = UserConfig.getInstance(ThemePreviewActivity.this.currentAccount).getClientUserId();
                tL_message.id = 1;
                tL_message.media = new TL_messageMediaEmpty();
                tL_message.out = true;
                tL_message.to_id = new TL_peerUser();
                tL_message.to_id.user_id = 0;
                MessageObject messageObject = new MessageObject(ThemePreviewActivity.this.currentAccount, tL_message, true);
                tL_message = new TL_message();
                tL_message.message = "I can't even take you seriously right now.";
                tL_message.date = currentTimeMillis + 960;
                tL_message.dialog_id = 1;
                tL_message.flags = 259;
                tL_message.from_id = UserConfig.getInstance(ThemePreviewActivity.this.currentAccount).getClientUserId();
                tL_message.id = 1;
                tL_message.media = new TL_messageMediaEmpty();
                tL_message.out = true;
                tL_message.to_id = new TL_peerUser();
                tL_message.to_id.user_id = 0;
                this.messages.add(new MessageObject(ThemePreviewActivity.this.currentAccount, tL_message, true));
                tL_message = new TL_message();
                tL_message.date = currentTimeMillis + 130;
                tL_message.dialog_id = 1;
                tL_message.flags = 259;
                tL_message.from_id = 0;
                tL_message.id = 5;
                tL_message.media = new TL_messageMediaDocument();
                MessageMedia messageMedia = tL_message.media;
                messageMedia.flags |= 3;
                messageMedia.document = new TL_document();
                Document document = tL_message.media.document;
                document.mime_type = "audio/mp4";
                document.file_reference = new byte[0];
                TL_documentAttributeAudio tL_documentAttributeAudio = new TL_documentAttributeAudio();
                tL_documentAttributeAudio.duration = 243;
                tL_documentAttributeAudio.performer = "David Hasselhoff";
                tL_documentAttributeAudio.title = "True Survivor";
                tL_message.media.document.attributes.add(tL_documentAttributeAudio);
                tL_message.out = false;
                tL_message.to_id = new TL_peerUser();
                tL_message.to_id.user_id = UserConfig.getInstance(ThemePreviewActivity.this.currentAccount).getClientUserId();
                this.messages.add(new MessageObject(ThemePreviewActivity.this.currentAccount, tL_message, true));
                tL_message = new TL_message();
                tL_message.message = "Ah, you kids today with techno music! You should enjoy the classics, like Hasselhoff!";
                tL_message.date = i2;
                tL_message.dialog_id = 1;
                tL_message.flags = 265;
                tL_message.from_id = 0;
                tL_message.id = 1;
                tL_message.reply_to_msg_id = 5;
                tL_message.media = new TL_messageMediaEmpty();
                tL_message.out = false;
                tL_message.to_id = new TL_peerUser();
                tL_message.to_id.user_id = UserConfig.getInstance(ThemePreviewActivity.this.currentAccount).getClientUserId();
                MessageObject messageObject2 = new MessageObject(ThemePreviewActivity.this.currentAccount, tL_message, true);
                messageObject2.customReplyName = "Lucio";
                messageObject2.replyMessageObject = messageObject;
                this.messages.add(messageObject2);
                tL_message = new TL_message();
                tL_message.date = currentTimeMillis + 120;
                tL_message.dialog_id = 1;
                tL_message.flags = 259;
                tL_message.from_id = UserConfig.getInstance(ThemePreviewActivity.this.currentAccount).getClientUserId();
                tL_message.id = 1;
                tL_message.media = new TL_messageMediaDocument();
                MessageMedia messageMedia2 = tL_message.media;
                messageMedia2.flags |= 3;
                messageMedia2.document = new TL_document();
                Document document2 = tL_message.media.document;
                document2.mime_type = "audio/ogg";
                document2.file_reference = new byte[0];
                TL_documentAttributeAudio tL_documentAttributeAudio2 = new TL_documentAttributeAudio();
                tL_documentAttributeAudio2.flags = 1028;
                tL_documentAttributeAudio2.duration = 3;
                tL_documentAttributeAudio2.voice = true;
                tL_documentAttributeAudio2.waveform = new byte[]{(byte) 0, (byte) 4, (byte) 17, (byte) -50, (byte) -93, (byte) 86, (byte) -103, (byte) -45, (byte) -12, (byte) -26, (byte) 63, (byte) -25, (byte) -3, (byte) 109, (byte) -114, (byte) -54, (byte) -4, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -29, (byte) -1, (byte) -1, (byte) -25, (byte) -1, (byte) -1, (byte) -97, (byte) -43, (byte) 57, (byte) -57, (byte) -108, (byte) 1, (byte) -91, (byte) -4, (byte) -47, (byte) 21, (byte) 99, (byte) 10, (byte) 97, (byte) 43, (byte) 45, (byte) 115, (byte) -112, (byte) -77, (byte) 51, (byte) -63, (byte) 66, (byte) 40, (byte) 34, (byte) -122, (byte) -116, (byte) 48, (byte) -124, (byte) 16, (byte) 66, (byte) -120, (byte) 16, (byte) 68, (byte) 16, (byte) 33, (byte) 4, (byte) 1};
                tL_message.media.document.attributes.add(tL_documentAttributeAudio2);
                tL_message.out = true;
                tL_message.to_id = new TL_peerUser();
                tL_message.to_id.user_id = 0;
                MessageObject messageObject3 = new MessageObject(ThemePreviewActivity.this.currentAccount, tL_message, true);
                messageObject3.audioProgressSec = 1;
                messageObject3.audioProgress = 0.3f;
                messageObject3.useCustomPhoto = true;
                this.messages.add(messageObject3);
                this.messages.add(messageObject);
                tL_message = new TL_message();
                tL_message.date = currentTimeMillis + 10;
                tL_message.dialog_id = 1;
                tL_message.flags = 257;
                tL_message.from_id = 0;
                tL_message.id = 1;
                tL_message.media = new TL_messageMediaPhoto();
                messageMedia2 = tL_message.media;
                messageMedia2.flags |= 3;
                messageMedia2.photo = new TL_photo();
                Photo photo = tL_message.media.photo;
                photo.file_reference = new byte[0];
                photo.has_stickers = false;
                photo.id = 1;
                photo.access_hash = 0;
                photo.date = currentTimeMillis;
                TL_photoSize tL_photoSize = new TL_photoSize();
                tL_photoSize.size = 0;
                tL_photoSize.w = 500;
                tL_photoSize.h = 302;
                tL_photoSize.type = "s";
                tL_photoSize.location = new TL_fileLocationUnavailable();
                tL_message.media.photo.sizes.add(tL_photoSize);
                tL_message.message = "Bring it on! I LIVE for this!";
                tL_message.out = false;
                tL_message.to_id = new TL_peerUser();
                tL_message.to_id.user_id = UserConfig.getInstance(ThemePreviewActivity.this.currentAccount).getClientUserId();
                messageObject3 = new MessageObject(ThemePreviewActivity.this.currentAccount, tL_message, true);
                messageObject3.useCustomPhoto = true;
                this.messages.add(messageObject3);
            }
            tL_message = new TL_message();
            tL_message.message = LocaleController.formatDateChat((long) currentTimeMillis);
            tL_message.id = 0;
            tL_message.date = currentTimeMillis;
            MessageObject messageObject4 = new MessageObject(ThemePreviewActivity.this.currentAccount, tL_message, false);
            messageObject4.type = 10;
            messageObject4.contentType = 1;
            messageObject4.isDateObject = true;
            this.messages.add(messageObject4);
        }

        public int getItemCount() {
            return this.messages.size();
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View chatMessageCell;
            if (i == 0) {
                chatMessageCell = new ChatMessageCell(this.mContext);
                chatMessageCell.setDelegate(new ChatMessageCellDelegate() {
                    public /* synthetic */ boolean canPerformActions() {
                        return -CC.$default$canPerformActions(this);
                    }

                    public /* synthetic */ void didLongPress(ChatMessageCell chatMessageCell, float f, float f2) {
                        -CC.$default$didLongPress(this, chatMessageCell, f, f2);
                    }

                    public /* synthetic */ void didPressBotButton(ChatMessageCell chatMessageCell, KeyboardButton keyboardButton) {
                        -CC.$default$didPressBotButton(this, chatMessageCell, keyboardButton);
                    }

                    public /* synthetic */ void didPressCancelSendButton(ChatMessageCell chatMessageCell) {
                        -CC.$default$didPressCancelSendButton(this, chatMessageCell);
                    }

                    public /* synthetic */ void didPressChannelAvatar(ChatMessageCell chatMessageCell, Chat chat, int i, float f, float f2) {
                        -CC.$default$didPressChannelAvatar(this, chatMessageCell, chat, i, f, f2);
                    }

                    public /* synthetic */ void didPressHiddenForward(ChatMessageCell chatMessageCell) {
                        -CC.$default$didPressHiddenForward(this, chatMessageCell);
                    }

                    public /* synthetic */ void didPressImage(ChatMessageCell chatMessageCell, float f, float f2) {
                        -CC.$default$didPressImage(this, chatMessageCell, f, f2);
                    }

                    public /* synthetic */ void didPressInstantButton(ChatMessageCell chatMessageCell, int i) {
                        -CC.$default$didPressInstantButton(this, chatMessageCell, i);
                    }

                    public /* synthetic */ void didPressOther(ChatMessageCell chatMessageCell, float f, float f2) {
                        -CC.$default$didPressOther(this, chatMessageCell, f, f2);
                    }

                    public /* synthetic */ void didPressReplyMessage(ChatMessageCell chatMessageCell, int i) {
                        -CC.$default$didPressReplyMessage(this, chatMessageCell, i);
                    }

                    public /* synthetic */ void didPressShare(ChatMessageCell chatMessageCell) {
                        -CC.$default$didPressShare(this, chatMessageCell);
                    }

                    public /* synthetic */ void didPressUrl(MessageObject messageObject, CharacterStyle characterStyle, boolean z) {
                        -CC.$default$didPressUrl(this, messageObject, characterStyle, z);
                    }

                    public /* synthetic */ void didPressUserAvatar(ChatMessageCell chatMessageCell, User user, float f, float f2) {
                        -CC.$default$didPressUserAvatar(this, chatMessageCell, user, f, f2);
                    }

                    public /* synthetic */ void didPressViaBot(ChatMessageCell chatMessageCell, String str) {
                        -CC.$default$didPressViaBot(this, chatMessageCell, str);
                    }

                    public /* synthetic */ void didPressVoteButton(ChatMessageCell chatMessageCell, TL_pollAnswer tL_pollAnswer) {
                        -CC.$default$didPressVoteButton(this, chatMessageCell, tL_pollAnswer);
                    }

                    public /* synthetic */ boolean isChatAdminCell(int i) {
                        return -CC.$default$isChatAdminCell(this, i);
                    }

                    public /* synthetic */ void needOpenWebView(String str, String str2, String str3, String str4, int i, int i2) {
                        -CC.$default$needOpenWebView(this, str, str2, str3, str4, i, i2);
                    }

                    public /* synthetic */ boolean needPlayMessage(MessageObject messageObject) {
                        return -CC.$default$needPlayMessage(this, messageObject);
                    }

                    public /* synthetic */ void videoTimerReached() {
                        -CC.$default$videoTimerReached(this);
                    }
                });
            } else if (i == 1) {
                chatMessageCell = new ChatActionCell(this.mContext);
                chatMessageCell.setDelegate(new ChatActionCellDelegate() {
                    public /* synthetic */ void didClickImage(ChatActionCell chatActionCell) {
                        ChatActionCellDelegate.-CC.$default$didClickImage(this, chatActionCell);
                    }

                    public /* synthetic */ void didLongPress(ChatActionCell chatActionCell, float f, float f2) {
                        ChatActionCellDelegate.-CC.$default$didLongPress(this, chatActionCell, f, f2);
                    }

                    public /* synthetic */ void didPressBotButton(MessageObject messageObject, KeyboardButton keyboardButton) {
                        ChatActionCellDelegate.-CC.$default$didPressBotButton(this, messageObject, keyboardButton);
                    }

                    public /* synthetic */ void didPressReplyMessage(ChatActionCell chatActionCell, int i) {
                        ChatActionCellDelegate.-CC.$default$didPressReplyMessage(this, chatActionCell, i);
                    }

                    public /* synthetic */ void needOpenUserProfile(int i) {
                        ChatActionCellDelegate.-CC.$default$needOpenUserProfile(this, i);
                    }
                });
            } else {
                chatMessageCell = null;
            }
            chatMessageCell.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(chatMessageCell);
        }

        /* JADX WARNING: Removed duplicated region for block: B:14:0x0059  */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r10, int r11) {
            /*
            r9 = this;
            r0 = r9.messages;
            r0 = r0.get(r11);
            r0 = (org.telegram.messenger.MessageObject) r0;
            r1 = r10.itemView;
            r2 = r1 instanceof org.telegram.ui.Cells.ChatMessageCell;
            if (r2 == 0) goto L_0x008f;
        L_0x000e:
            r1 = (org.telegram.ui.Cells.ChatMessageCell) r1;
            r2 = 0;
            r1.isChat = r2;
            r3 = r11 + -1;
            r4 = r9.getItemViewType(r3);
            r5 = 1;
            r11 = r11 + r5;
            r6 = r9.getItemViewType(r11);
            r7 = r0.messageOwner;
            r7 = r7.reply_markup;
            r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_replyInlineMarkup;
            r8 = 300; // 0x12c float:4.2E-43 double:1.48E-321;
            if (r7 != 0) goto L_0x0052;
        L_0x0029:
            r7 = r10.getItemViewType();
            if (r4 != r7) goto L_0x0052;
        L_0x002f:
            r4 = r9.messages;
            r3 = r4.get(r3);
            r3 = (org.telegram.messenger.MessageObject) r3;
            r4 = r3.isOutOwner();
            r7 = r0.isOutOwner();
            if (r4 != r7) goto L_0x0052;
        L_0x0041:
            r3 = r3.messageOwner;
            r3 = r3.date;
            r4 = r0.messageOwner;
            r4 = r4.date;
            r3 = r3 - r4;
            r3 = java.lang.Math.abs(r3);
            if (r3 > r8) goto L_0x0052;
        L_0x0050:
            r3 = 1;
            goto L_0x0053;
        L_0x0052:
            r3 = 0;
        L_0x0053:
            r10 = r10.getItemViewType();
            if (r6 != r10) goto L_0x0083;
        L_0x0059:
            r10 = r9.messages;
            r10 = r10.get(r11);
            r10 = (org.telegram.messenger.MessageObject) r10;
            r11 = r10.messageOwner;
            r11 = r11.reply_markup;
            r11 = r11 instanceof org.telegram.tgnet.TLRPC.TL_replyInlineMarkup;
            if (r11 != 0) goto L_0x0083;
        L_0x0069:
            r11 = r10.isOutOwner();
            r4 = r0.isOutOwner();
            if (r11 != r4) goto L_0x0083;
        L_0x0073:
            r10 = r10.messageOwner;
            r10 = r10.date;
            r11 = r0.messageOwner;
            r11 = r11.date;
            r10 = r10 - r11;
            r10 = java.lang.Math.abs(r10);
            if (r10 > r8) goto L_0x0083;
        L_0x0082:
            r2 = 1;
        L_0x0083:
            r10 = r9.showSecretMessages;
            r1.isChat = r10;
            r1.setFullyDraw(r5);
            r10 = 0;
            r1.setMessageObject(r0, r10, r3, r2);
            goto L_0x009d;
        L_0x008f:
            r10 = r1 instanceof org.telegram.ui.Cells.ChatActionCell;
            if (r10 == 0) goto L_0x009d;
        L_0x0093:
            r1 = (org.telegram.ui.Cells.ChatActionCell) r1;
            r1.setMessageObject(r0);
            r10 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            r1.setAlpha(r10);
        L_0x009d:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemePreviewActivity$MessagesAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public int getItemViewType(int i) {
            return (i < 0 || i >= this.messages.size()) ? 4 : ((MessageObject) this.messages.get(i)).contentType;
        }
    }

    public ThemePreviewActivity(File file, ThemeInfo themeInfo) {
        this.applyingTheme = themeInfo;
        this.themeFile = file;
    }

    public View createView(Context context) {
        Context context2 = context;
        this.page1 = new FrameLayout(context2);
        this.actionBar.createMenu().addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItemSearchListener() {
            public boolean canCollapseSearch() {
                return true;
            }

            public void onSearchCollapse() {
            }

            public void onSearchExpand() {
            }

            public void onTextChanged(EditText editText) {
            }
        }).setSearchFieldHint(LocaleController.getString("Search", NUM));
        this.actionBar.setBackButtonDrawable(new MenuDrawable());
        this.actionBar.setAddToContainer(false);
        this.actionBar.setTitle(LocaleController.getString("ThemePreview", NUM));
        this.page1 = new FrameLayout(context2) {
            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                int size = MeasureSpec.getSize(i);
                int size2 = MeasureSpec.getSize(i2);
                setMeasuredDimension(size, size2);
                measureChildWithMargins(ThemePreviewActivity.this.actionBar, i, 0, i2, 0);
                int measuredHeight = ThemePreviewActivity.this.actionBar.getMeasuredHeight();
                if (ThemePreviewActivity.this.actionBar.getVisibility() == 0) {
                    size2 -= measuredHeight;
                }
                ((FrameLayout.LayoutParams) ThemePreviewActivity.this.listView.getLayoutParams()).topMargin = measuredHeight;
                ThemePreviewActivity.this.listView.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec(size2, NUM));
                measureChildWithMargins(ThemePreviewActivity.this.floatingButton, i, 0, i2, 0);
            }

            /* Access modifiers changed, original: protected */
            public boolean drawChild(Canvas canvas, View view, long j) {
                boolean drawChild = super.drawChild(canvas, view, j);
                if (view == ThemePreviewActivity.this.actionBar && ThemePreviewActivity.this.parentLayout != null) {
                    ThemePreviewActivity.this.parentLayout.drawHeaderShadow(canvas, ThemePreviewActivity.this.actionBar.getVisibility() == 0 ? ThemePreviewActivity.this.actionBar.getMeasuredHeight() : 0);
                }
                return drawChild;
            }
        };
        this.page1.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
        this.listView = new RecyclerListView(context2);
        this.listView.setVerticalScrollBarEnabled(true);
        this.listView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        this.listView.setLayoutManager(new LinearLayoutManager(context2, 1, false));
        int i = 2;
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        this.page1.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.floatingButton = new ImageView(context2);
        this.floatingButton.setScaleType(ScaleType.CENTER);
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("chats_actionBackground"), Theme.getColor("chats_actionPressedBackground"));
        if (VERSION.SDK_INT < 21) {
            Drawable mutate = context.getResources().getDrawable(NUM).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(-16777216, Mode.MULTIPLY));
            Drawable combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorCircleDrawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            createSimpleSelectorCircleDrawable = combinedDrawable;
        }
        this.floatingButton.setBackgroundDrawable(createSimpleSelectorCircleDrawable);
        this.floatingButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_actionIcon"), Mode.MULTIPLY));
        this.floatingButton.setImageResource(NUM);
        if (VERSION.SDK_INT >= 21) {
            StateListAnimator stateListAnimator = new StateListAnimator();
            String str = "translationZ";
            stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButton, str, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButton, str, new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            this.floatingButton.setStateListAnimator(stateListAnimator);
            this.floatingButton.setOutlineProvider(new ViewOutlineProvider() {
                @SuppressLint({"NewApi"})
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        this.page1.addView(this.floatingButton, LayoutHelper.createFrame(VERSION.SDK_INT >= 21 ? 56 : 60, VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, (LocaleController.isRTL ? 3 : 5) | 80, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 14.0f, 14.0f));
        this.dialogsAdapter = new DialogsAdapter(context2);
        this.listView.setAdapter(this.dialogsAdapter);
        this.page2 = new SizeNotifierFrameLayout(context2) {
            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                int size = MeasureSpec.getSize(i);
                int size2 = MeasureSpec.getSize(i2);
                setMeasuredDimension(size, size2);
                measureChildWithMargins(ThemePreviewActivity.this.actionBar2, i, 0, i2, 0);
                i = ThemePreviewActivity.this.actionBar2.getMeasuredHeight();
                if (ThemePreviewActivity.this.actionBar2.getVisibility() == 0) {
                    size2 -= i;
                }
                ((FrameLayout.LayoutParams) ThemePreviewActivity.this.listView2.getLayoutParams()).topMargin = i;
                ThemePreviewActivity.this.listView2.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec(size2, NUM));
            }

            /* Access modifiers changed, original: protected */
            public boolean drawChild(Canvas canvas, View view, long j) {
                boolean drawChild = super.drawChild(canvas, view, j);
                if (view == ThemePreviewActivity.this.actionBar2 && ThemePreviewActivity.this.parentLayout != null) {
                    ThemePreviewActivity.this.parentLayout.drawHeaderShadow(canvas, ThemePreviewActivity.this.actionBar2.getVisibility() == 0 ? ThemePreviewActivity.this.actionBar2.getMeasuredHeight() : 0);
                }
                return drawChild;
            }
        };
        this.page2.setBackgroundImage(Theme.getCachedWallpaper(), Theme.isWallpaperMotion());
        this.messagesAdapter = new MessagesAdapter(context2);
        this.actionBar2 = createActionBar(context);
        this.actionBar2.setBackButtonDrawable(new BackDrawable(false));
        if (this.messagesAdapter.showSecretMessages) {
            this.actionBar2.setTitle("Telegram Beta Chat");
            this.actionBar2.setSubtitle(LocaleController.formatPluralString("Members", 505));
        } else {
            this.actionBar2.setTitle("Reinhardt");
            this.actionBar2.setSubtitle(LocaleController.formatDateOnline((System.currentTimeMillis() / 1000) - 3600));
        }
        this.page2.addView(this.actionBar2, LayoutHelper.createFrame(-1, -2.0f));
        this.listView2 = new RecyclerListView(context2) {
            public boolean drawChild(Canvas canvas, View view, long j) {
                boolean drawChild = super.drawChild(canvas, view, j);
                if (view instanceof ChatMessageCell) {
                    ChatMessageCell chatMessageCell = (ChatMessageCell) view;
                    chatMessageCell.getMessageObject();
                    ImageReceiver avatarImage = chatMessageCell.getAvatarImage();
                    if (avatarImage != null) {
                        int top = view.getTop();
                        if (chatMessageCell.isPinnedBottom()) {
                            ViewHolder childViewHolder = ThemePreviewActivity.this.listView2.getChildViewHolder(view);
                            if (childViewHolder != null) {
                                if (ThemePreviewActivity.this.listView2.findViewHolderForAdapterPosition(childViewHolder.getAdapterPosition() - 1) != null) {
                                    avatarImage.setImageY(-AndroidUtilities.dp(1000.0f));
                                    avatarImage.draw(canvas);
                                    return drawChild;
                                }
                            }
                        }
                        float translationX = chatMessageCell.getTranslationX();
                        int top2 = view.getTop() + chatMessageCell.getLayoutHeight();
                        int measuredHeight = ThemePreviewActivity.this.listView2.getMeasuredHeight() - ThemePreviewActivity.this.listView2.getPaddingBottom();
                        if (top2 > measuredHeight) {
                            top2 = measuredHeight;
                        }
                        if (chatMessageCell.isPinnedTop()) {
                            ViewHolder childViewHolder2 = ThemePreviewActivity.this.listView2.getChildViewHolder(view);
                            if (childViewHolder2 != null) {
                                int i = 0;
                                while (i < 20) {
                                    i++;
                                    childViewHolder2 = ThemePreviewActivity.this.listView2.findViewHolderForAdapterPosition(childViewHolder2.getAdapterPosition() + 1);
                                    if (childViewHolder2 == null) {
                                        break;
                                    }
                                    top = childViewHolder2.itemView.getTop();
                                    if (top2 - AndroidUtilities.dp(48.0f) < childViewHolder2.itemView.getBottom()) {
                                        translationX = Math.min(childViewHolder2.itemView.getTranslationX(), translationX);
                                    }
                                    View view2 = childViewHolder2.itemView;
                                    if (view2 instanceof ChatMessageCell) {
                                        if (!((ChatMessageCell) view2).isPinnedTop()) {
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                        if (top2 - AndroidUtilities.dp(48.0f) < top) {
                            top2 = top + AndroidUtilities.dp(48.0f);
                        }
                        if (translationX != 0.0f) {
                            canvas.save();
                            canvas.translate(translationX, 0.0f);
                        }
                        avatarImage.setImageY(top2 - AndroidUtilities.dp(44.0f));
                        avatarImage.draw(canvas);
                        if (translationX != 0.0f) {
                            canvas.restore();
                        }
                    }
                }
                return drawChild;
            }
        };
        this.listView2.setVerticalScrollBarEnabled(true);
        this.listView2.setItemAnimator(null);
        this.listView2.setLayoutAnimation(null);
        this.listView2.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f));
        this.listView2.setClipToPadding(false);
        this.listView2.setLayoutManager(new LinearLayoutManager(context2, 1, true));
        RecyclerListView recyclerListView = this.listView2;
        if (LocaleController.isRTL) {
            i = 1;
        }
        recyclerListView.setVerticalScrollbarPosition(i);
        this.page2.addView(this.listView2, LayoutHelper.createFrame(-1, -1, 51));
        this.listView2.setAdapter(this.messagesAdapter);
        this.fragmentView = new FrameLayout(context2);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        final ViewPager viewPager = new ViewPager(context2);
        viewPager.addOnPageChangeListener(new OnPageChangeListener() {
            public void onPageScrollStateChanged(int i) {
            }

            public void onPageScrolled(int i, float f, int i2) {
            }

            public void onPageSelected(int i) {
                ThemePreviewActivity.this.dotsContainer.invalidate();
            }
        });
        viewPager.setAdapter(new PagerAdapter() {
            public int getCount() {
                return 2;
            }

            public int getItemPosition(Object obj) {
                return -1;
            }

            public boolean isViewFromObject(View view, Object obj) {
                return obj == view;
            }

            public Object instantiateItem(ViewGroup viewGroup, int i) {
                View access$1600 = i == 0 ? ThemePreviewActivity.this.page1 : ThemePreviewActivity.this.page2;
                viewGroup.addView(access$1600);
                return access$1600;
            }

            public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
                viewGroup.removeView((View) obj);
            }

            public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
                if (dataSetObserver != null) {
                    super.unregisterDataSetObserver(dataSetObserver);
                }
            }
        });
        AndroidUtilities.setViewPagerEdgeEffectColor(viewPager, Theme.getColor("actionBarDefault"));
        frameLayout.addView(viewPager, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        View view = new View(context2);
        view.setBackgroundResource(NUM);
        frameLayout.addView(view, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        FrameLayout frameLayout2 = new FrameLayout(context2);
        frameLayout2.setBackgroundColor(-1);
        frameLayout.addView(frameLayout2, LayoutHelper.createFrame(-1, 48, 83));
        this.dotsContainer = new View(context2) {
            private Paint paint = new Paint(1);

            /* Access modifiers changed, original: protected */
            public void onDraw(Canvas canvas) {
                int currentItem = viewPager.getCurrentItem();
                int i = 0;
                while (i < 2) {
                    this.paint.setColor(i == currentItem ? -6710887 : -3355444);
                    canvas.drawCircle((float) AndroidUtilities.dp((float) ((i * 15) + 3)), (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(3.0f), this.paint);
                    i++;
                }
            }
        };
        frameLayout2.addView(this.dotsContainer, LayoutHelper.createFrame(22, 8, 17));
        TextView textView = new TextView(context2);
        textView.setTextSize(1, 14.0f);
        textView.setTextColor(-15095832);
        textView.setGravity(17);
        textView.setBackgroundDrawable(Theme.createSelectorDrawable(NUM, 0));
        textView.setPadding(AndroidUtilities.dp(29.0f), 0, AndroidUtilities.dp(29.0f), 0);
        textView.setText(LocaleController.getString("Cancel", NUM).toUpperCase());
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        frameLayout2.addView(textView, LayoutHelper.createFrame(-2, -1, 51));
        textView.setOnClickListener(new -$$Lambda$ThemePreviewActivity$VGx4sgeoasvuNaWlUta0WpzoZKY(this));
        textView = new TextView(context2);
        textView.setTextSize(1, 14.0f);
        textView.setTextColor(-15095832);
        textView.setGravity(17);
        textView.setBackgroundDrawable(Theme.createSelectorDrawable(NUM, 0));
        textView.setPadding(AndroidUtilities.dp(29.0f), 0, AndroidUtilities.dp(29.0f), 0);
        textView.setText(LocaleController.getString("ApplyTheme", NUM).toUpperCase());
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        frameLayout2.addView(textView, LayoutHelper.createFrame(-2, -1, 53));
        textView.setOnClickListener(new -$$Lambda$ThemePreviewActivity$YPhE-lyYHwkD5fNafYq-tQN7oD0(this));
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$0$ThemePreviewActivity(View view) {
        Theme.applyPreviousTheme();
        this.parentLayout.rebuildAllFragmentViews(false, false);
        finishFragment();
    }

    public /* synthetic */ void lambda$createView$1$ThemePreviewActivity(View view) {
        this.applied = true;
        this.parentLayout.rebuildAllFragmentViews(false, false);
        Theme.applyThemeFile(this.themeFile, this.applyingTheme.name, false);
        finishFragment();
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
        super.onFragmentDestroy();
    }

    public void onResume() {
        super.onResume();
        DialogsAdapter dialogsAdapter = this.dialogsAdapter;
        if (dialogsAdapter != null) {
            dialogsAdapter.notifyDataSetChanged();
        }
        MessagesAdapter messagesAdapter = this.messagesAdapter;
        if (messagesAdapter != null) {
            messagesAdapter.notifyDataSetChanged();
        }
        SizeNotifierFrameLayout sizeNotifierFrameLayout = this.page2;
        if (sizeNotifierFrameLayout != null) {
            sizeNotifierFrameLayout.onResume();
        }
    }

    public void onPause() {
        super.onPause();
        SizeNotifierFrameLayout sizeNotifierFrameLayout = this.page2;
        if (sizeNotifierFrameLayout != null) {
            sizeNotifierFrameLayout.onResume();
        }
    }

    public boolean onBackPressed() {
        Theme.applyPreviousTheme();
        this.parentLayout.rebuildAllFragmentViews(false, false);
        return super.onBackPressed();
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.emojiDidLoad) {
            RecyclerListView recyclerListView = this.listView;
            if (recyclerListView != null) {
                i = recyclerListView.getChildCount();
                for (int i3 = 0; i3 < i; i3++) {
                    View childAt = this.listView.getChildAt(i3);
                    if (childAt instanceof DialogCell) {
                        ((DialogCell) childAt).update(0);
                    }
                }
            }
        }
    }
}
