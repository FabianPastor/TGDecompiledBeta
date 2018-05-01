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
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.style.CharacterStyle;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_photo;
import org.telegram.tgnet.TLRPC.TL_photoSize;
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC.TL_replyInlineMarkup;
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

    /* renamed from: org.telegram.ui.ThemePreviewActivity$3 */
    class C17263 extends ViewOutlineProvider {
        C17263() {
        }

        @SuppressLint({"NewApi"})
        public void getOutline(View view, Outline outline) {
            outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
        }
    }

    /* renamed from: org.telegram.ui.ThemePreviewActivity$8 */
    class C17288 implements OnClickListener {
        C17288() {
        }

        public void onClick(View view) {
            Theme.applyPreviousTheme();
            ThemePreviewActivity.this.parentLayout.rebuildAllFragmentViews(false, false);
            ThemePreviewActivity.this.finishFragment();
        }
    }

    /* renamed from: org.telegram.ui.ThemePreviewActivity$9 */
    class C17299 implements OnClickListener {
        C17299() {
        }

        public void onClick(View view) {
            ThemePreviewActivity.this.applied = true;
            ThemePreviewActivity.this.parentLayout.rebuildAllFragmentViews(false, false);
            Theme.applyThemeFile(ThemePreviewActivity.this.themeFile, ThemePreviewActivity.this.applyingTheme.name, false);
            ThemePreviewActivity.this.finishFragment();
        }
    }

    /* renamed from: org.telegram.ui.ThemePreviewActivity$1 */
    class C22991 extends ActionBarMenuItemSearchListener {
        public boolean canCollapseSearch() {
            return true;
        }

        public void onSearchCollapse() {
        }

        public void onSearchExpand() {
        }

        public void onTextChanged(EditText editText) {
        }

        C22991() {
        }
    }

    /* renamed from: org.telegram.ui.ThemePreviewActivity$5 */
    class C23015 implements OnPageChangeListener {
        public void onPageScrollStateChanged(int i) {
        }

        public void onPageScrolled(int i, float f, int i2) {
        }

        C23015() {
        }

        public void onPageSelected(int i) {
            ThemePreviewActivity.this.dotsContainer.invalidate();
        }
    }

    /* renamed from: org.telegram.ui.ThemePreviewActivity$6 */
    class C23026 extends PagerAdapter {
        public int getCount() {
            return 2;
        }

        public int getItemPosition(Object obj) {
            return -1;
        }

        public boolean isViewFromObject(View view, Object obj) {
            return obj == view;
        }

        C23026() {
        }

        public Object instantiateItem(ViewGroup viewGroup, int i) {
            i = i == 0 ? ThemePreviewActivity.this.page1 : ThemePreviewActivity.this.page2;
            viewGroup.addView(i);
            return i;
        }

        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            viewGroup.removeView((View) obj);
        }

        public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
            if (dataSetObserver != null) {
                super.unregisterDataSetObserver(dataSetObserver);
            }
        }
    }

    public class DialogsAdapter extends SelectionAdapter {
        private ArrayList<CustomDialog> dialogs = new ArrayList();
        private Context mContext;

        public DialogsAdapter(Context context) {
            this.mContext = context;
            ThemePreviewActivity currentTimeMillis = (int) (System.currentTimeMillis() / 1000);
            context = new CustomDialog();
            context.name = "Eva Summer";
            context.message = "Reminds me of a Chinese prove...";
            context.id = 0;
            context.unread_count = 0;
            context.pinned = true;
            context.muted = false;
            context.type = 0;
            context.date = currentTimeMillis;
            context.verified = false;
            context.isMedia = false;
            context.sent = true;
            this.dialogs.add(context);
            context = new CustomDialog();
            context.name = "Alexandra Smith";
            context.message = "Reminds me of a Chinese prove...";
            context.id = 1;
            context.unread_count = 2;
            context.pinned = false;
            context.muted = false;
            context.type = 0;
            context.date = currentTimeMillis - 3600;
            context.verified = false;
            context.isMedia = false;
            context.sent = false;
            this.dialogs.add(context);
            context = new CustomDialog();
            context.name = "Make Apple";
            context.message = "\ud83e\udd37\u200d\u2642\ufe0f Sticker";
            context.id = 2;
            context.unread_count = 3;
            context.pinned = false;
            context.muted = true;
            context.type = 0;
            context.date = currentTimeMillis - 7200;
            context.verified = false;
            context.isMedia = true;
            context.sent = false;
            this.dialogs.add(context);
            context = new CustomDialog();
            context.name = "Paul Newman";
            context.message = "Any ideas?";
            context.id = 3;
            context.unread_count = 0;
            context.pinned = false;
            context.muted = false;
            context.type = 2;
            context.date = currentTimeMillis - 10800;
            context.verified = false;
            context.isMedia = false;
            context.sent = false;
            this.dialogs.add(context);
            context = new CustomDialog();
            context.name = "Old Pirates";
            context.message = "Yo-ho-ho!";
            context.id = 4;
            context.unread_count = 0;
            context.pinned = false;
            context.muted = false;
            context.type = 1;
            context.date = currentTimeMillis - 14400;
            context.verified = false;
            context.isMedia = false;
            context.sent = true;
            this.dialogs.add(context);
            context = new CustomDialog();
            context.name = "Kate Bright";
            context.message = "Hola!";
            context.id = 5;
            context.unread_count = 0;
            context.pinned = false;
            context.muted = false;
            context.type = 0;
            context.date = currentTimeMillis - 18000;
            context.verified = false;
            context.isMedia = false;
            context.sent = false;
            this.dialogs.add(context);
            context = new CustomDialog();
            context.name = "Nick K";
            context.message = "These are not the droids you are looking for";
            context.id = 6;
            context.unread_count = 0;
            context.pinned = false;
            context.muted = false;
            context.type = 0;
            context.date = currentTimeMillis - 21600;
            context.verified = true;
            context.isMedia = false;
            context.sent = false;
            this.dialogs.add(context);
            context = new CustomDialog();
            context.name = "Adler Toberg";
            context.message = "Did someone say peanut butter?";
            context.id = 0;
            context.unread_count = 0;
            context.pinned = false;
            context.muted = false;
            context.type = 0;
            context.date = currentTimeMillis - 25200;
            context.verified = true;
            context.isMedia = false;
            context.sent = false;
            this.dialogs.add(context);
        }

        public int getItemCount() {
            return this.dialogs.size() + 1;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return viewHolder.getItemViewType() != 1;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            viewGroup = i == 0 ? new DialogCell(this.mContext, false) : i == 1 ? new LoadingCell(this.mContext) : null;
            viewGroup.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(viewGroup);
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
        private ArrayList<MessageObject> messages = new ArrayList();

        /* renamed from: org.telegram.ui.ThemePreviewActivity$MessagesAdapter$1 */
        class C23031 implements ChatMessageCellDelegate {
            public boolean canPerformActions() {
                return false;
            }

            public void didLongPressed(ChatMessageCell chatMessageCell) {
            }

            public void didPressedBotButton(ChatMessageCell chatMessageCell, KeyboardButton keyboardButton) {
            }

            public void didPressedCancelSendButton(ChatMessageCell chatMessageCell) {
            }

            public void didPressedChannelAvatar(ChatMessageCell chatMessageCell, Chat chat, int i) {
            }

            public void didPressedImage(ChatMessageCell chatMessageCell) {
            }

            public void didPressedInstantButton(ChatMessageCell chatMessageCell, int i) {
            }

            public void didPressedOther(ChatMessageCell chatMessageCell) {
            }

            public void didPressedReplyMessage(ChatMessageCell chatMessageCell, int i) {
            }

            public void didPressedShare(ChatMessageCell chatMessageCell) {
            }

            public void didPressedUrl(MessageObject messageObject, CharacterStyle characterStyle, boolean z) {
            }

            public void didPressedUserAvatar(ChatMessageCell chatMessageCell, User user) {
            }

            public void didPressedViaBot(ChatMessageCell chatMessageCell, String str) {
            }

            public boolean isChatAdminCell(int i) {
                return false;
            }

            public void needOpenWebView(String str, String str2, String str3, String str4, int i, int i2) {
            }

            public boolean needPlayMessage(MessageObject messageObject) {
                return false;
            }

            C23031() {
            }
        }

        /* renamed from: org.telegram.ui.ThemePreviewActivity$MessagesAdapter$2 */
        class C23042 implements ChatActionCellDelegate {
            public void didClickedImage(ChatActionCell chatActionCell) {
            }

            public void didLongPressed(ChatActionCell chatActionCell) {
            }

            public void didPressedBotButton(MessageObject messageObject, KeyboardButton keyboardButton) {
            }

            public void didPressedReplyMessage(ChatActionCell chatActionCell, int i) {
            }

            public void needOpenUserProfile(int i) {
            }

            C23042() {
            }
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return false;
        }

        public MessagesAdapter(Context context) {
            this.mContext = context;
            context = ((int) (System.currentTimeMillis() / 1000)) - 3600;
            Message tL_message = new TL_message();
            tL_message.message = "Reinhardt, we need to find you some new tunes \ud83c\udfb6.";
            int i = context + 60;
            tL_message.date = i;
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
            tL_message.date = context + 960;
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
            tL_message.date = context + TsExtractor.TS_STREAM_TYPE_HDMV_DTS;
            tL_message.dialog_id = 1;
            tL_message.flags = 259;
            tL_message.from_id = 0;
            tL_message.id = 5;
            tL_message.media = new TL_messageMediaDocument();
            MessageMedia messageMedia = tL_message.media;
            messageMedia.flags |= 3;
            tL_message.media.document = new TL_document();
            tL_message.media.document.mime_type = MimeTypes.AUDIO_MP4;
            tL_message.media.document.thumb = new TL_photoSizeEmpty();
            tL_message.media.document.thumb.type = "s";
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
            tL_message.date = i;
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
            tL_message.date = context + 120;
            tL_message.dialog_id = 1;
            tL_message.flags = 259;
            tL_message.from_id = UserConfig.getInstance(ThemePreviewActivity.this.currentAccount).getClientUserId();
            tL_message.id = 1;
            tL_message.media = new TL_messageMediaDocument();
            MessageMedia messageMedia2 = tL_message.media;
            messageMedia2.flags |= 3;
            tL_message.media.document = new TL_document();
            tL_message.media.document.mime_type = "audio/ogg";
            tL_message.media.document.thumb = new TL_photoSizeEmpty();
            tL_message.media.document.thumb.type = "s";
            TL_documentAttributeAudio tL_documentAttributeAudio2 = new TL_documentAttributeAudio();
            tL_documentAttributeAudio2.flags = 1028;
            tL_documentAttributeAudio2.duration = 3;
            tL_documentAttributeAudio2.voice = true;
            tL_documentAttributeAudio2.waveform = new byte[]{(byte) 0, (byte) 4, (byte) 17, (byte) -50, (byte) -93, (byte) 86, (byte) -103, (byte) -45, (byte) -12, (byte) -26, (byte) 63, (byte) -25, (byte) -3, (byte) 109, (byte) -114, (byte) -54, (byte) -4, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -29, (byte) -1, (byte) -1, (byte) -25, (byte) -1, (byte) -1, (byte) -97, (byte) -43, (byte) 57, (byte) -57, (byte) -108, (byte) 1, (byte) -91, (byte) -4, (byte) -47, (byte) 21, (byte) 99, (byte) 10, (byte) 97, (byte) 43, (byte) 45, (byte) 115, (byte) -112, (byte) -77, (byte) 51, (byte) -63, (byte) 66, (byte) 40, (byte) 34, (byte) -122, (byte) -116, (byte) 48, (byte) -124, (byte) 16, (byte) 66, (byte) -120, (byte) 16, (byte) 68, (byte) 16, (byte) 33, (byte) 4, (byte) 1};
            tL_message.media.document.attributes.add(tL_documentAttributeAudio2);
            tL_message.out = true;
            tL_message.to_id = new TL_peerUser();
            tL_message.to_id.user_id = 0;
            messageObject2 = new MessageObject(ThemePreviewActivity.this.currentAccount, tL_message, true);
            messageObject2.audioProgressSec = 1;
            messageObject2.audioProgress = 0.3f;
            messageObject2.useCustomPhoto = true;
            this.messages.add(messageObject2);
            this.messages.add(messageObject);
            tL_message = new TL_message();
            tL_message.date = context + 10;
            tL_message.dialog_id = 1;
            tL_message.flags = 257;
            tL_message.from_id = 0;
            tL_message.id = 1;
            tL_message.media = new TL_messageMediaPhoto();
            messageMedia2 = tL_message.media;
            messageMedia2.flags |= 3;
            tL_message.media.photo = new TL_photo();
            tL_message.media.photo.has_stickers = false;
            tL_message.media.photo.id = 1;
            tL_message.media.photo.access_hash = 0;
            tL_message.media.photo.date = context;
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
            messageObject2 = new MessageObject(ThemePreviewActivity.this.currentAccount, tL_message, true);
            messageObject2.useCustomPhoto = true;
            this.messages.add(messageObject2);
            tL_message = new TL_message();
            tL_message.message = LocaleController.formatDateChat((long) context);
            tL_message.id = 0;
            tL_message.date = context;
            context = new MessageObject(ThemePreviewActivity.this.currentAccount, tL_message, false);
            context.type = 10;
            context.contentType = 1;
            context.isDateObject = true;
            this.messages.add(context);
        }

        public int getItemCount() {
            return this.messages.size();
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i == 0) {
                viewGroup = new ChatMessageCell(this.mContext);
                ((ChatMessageCell) viewGroup).setDelegate(new C23031());
            } else if (i == 1) {
                viewGroup = new ChatActionCell(this.mContext);
                ((ChatActionCell) viewGroup).setDelegate(new C23042());
            } else {
                viewGroup = null;
            }
            viewGroup.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(viewGroup);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            MessageObject messageObject = (MessageObject) this.messages.get(i);
            View view = viewHolder.itemView;
            if (view instanceof ChatMessageCell) {
                boolean z;
                MessageObject messageObject2;
                ChatMessageCell chatMessageCell = (ChatMessageCell) view;
                boolean z2 = false;
                chatMessageCell.isChat = false;
                int i2 = i - 1;
                int itemViewType = getItemViewType(i2);
                i++;
                int itemViewType2 = getItemViewType(i);
                if (!(messageObject.messageOwner.reply_markup instanceof TL_replyInlineMarkup) && itemViewType == viewHolder.getItemViewType()) {
                    MessageObject messageObject3 = (MessageObject) this.messages.get(i2);
                    if (messageObject3.isOutOwner() == messageObject.isOutOwner() && Math.abs(messageObject3.messageOwner.date - messageObject.messageOwner.date) <= 300) {
                        z = true;
                        if (itemViewType2 == viewHolder.getItemViewType()) {
                            messageObject2 = (MessageObject) this.messages.get(i);
                            if ((messageObject2.messageOwner.reply_markup instanceof TL_replyInlineMarkup) == 0 && messageObject2.isOutOwner() == messageObject.isOutOwner() && Math.abs(messageObject2.messageOwner.date - messageObject.messageOwner.date) <= 300) {
                                z2 = true;
                            }
                        }
                        chatMessageCell.setFullyDraw(true);
                        chatMessageCell.setMessageObject(messageObject, null, z, z2);
                    }
                }
                z = false;
                if (itemViewType2 == viewHolder.getItemViewType()) {
                    messageObject2 = (MessageObject) this.messages.get(i);
                    z2 = true;
                }
                chatMessageCell.setFullyDraw(true);
                chatMessageCell.setMessageObject(messageObject, null, z, z2);
            } else if ((view instanceof ChatActionCell) != null) {
                ChatActionCell chatActionCell = (ChatActionCell) view;
                chatActionCell.setMessageObject(messageObject);
                chatActionCell.setAlpha(1.0f);
            }
        }

        public int getItemViewType(int i) {
            return (i < 0 || i >= this.messages.size()) ? 4 : ((MessageObject) this.messages.get(i)).contentType;
        }
    }

    public ThemePreviewActivity(File file, ThemeInfo themeInfo) {
        this.swipeBackEnabled = false;
        this.applyingTheme = themeInfo;
        this.themeFile = file;
    }

    public View createView(Context context) {
        Drawable combinedDrawable;
        Context context2 = context;
        this.page1 = new FrameLayout(context2);
        this.actionBar.createMenu().addItem(0, (int) C0446R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new C22991()).getSearchField().setHint(LocaleController.getString("Search", C0446R.string.Search));
        this.actionBar.setBackButtonDrawable(new MenuDrawable());
        this.actionBar.setAddToContainer(false);
        this.actionBar.setTitle(LocaleController.getString("ThemePreview", C0446R.string.ThemePreview));
        this.page1 = new FrameLayout(context2) {
            protected void onMeasure(int i, int i2) {
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

            protected boolean drawChild(Canvas canvas, View view, long j) {
                j = super.drawChild(canvas, view, j);
                if (view == ThemePreviewActivity.this.actionBar && ThemePreviewActivity.this.parentLayout != null) {
                    ThemePreviewActivity.this.parentLayout.drawHeaderShadow(canvas, ThemePreviewActivity.this.actionBar.getVisibility() == 0 ? ThemePreviewActivity.this.actionBar.getMeasuredHeight() : 0);
                }
                return j;
            }
        };
        this.page1.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
        this.listView = new RecyclerListView(context2);
        this.listView.setVerticalScrollBarEnabled(true);
        this.listView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        this.listView.setLayoutManager(new LinearLayoutManager(context2, 1, false));
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        r0.page1.addView(r0.listView, LayoutHelper.createFrame(-1, -1, 51));
        r0.floatingButton = new ImageView(context2);
        r0.floatingButton.setScaleType(ScaleType.CENTER);
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_chats_actionBackground), Theme.getColor(Theme.key_chats_actionPressedBackground));
        if (VERSION.SDK_INT < 21) {
            Drawable mutate = context.getResources().getDrawable(C0446R.drawable.floating_shadow).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(Theme.ACTION_BAR_VIDEO_EDIT_COLOR, Mode.MULTIPLY));
            combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorCircleDrawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
        } else {
            combinedDrawable = createSimpleSelectorCircleDrawable;
        }
        r0.floatingButton.setBackgroundDrawable(combinedDrawable);
        r0.floatingButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_actionIcon), Mode.MULTIPLY));
        r0.floatingButton.setImageResource(C0446R.drawable.floating_pencil);
        if (VERSION.SDK_INT >= 21) {
            StateListAnimator stateListAnimator = new StateListAnimator();
            stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(r0.floatingButton, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(r0.floatingButton, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            r0.floatingButton.setStateListAnimator(stateListAnimator);
            r0.floatingButton.setOutlineProvider(new C17263());
        }
        r0.page1.addView(r0.floatingButton, LayoutHelper.createFrame(VERSION.SDK_INT >= 21 ? 56 : 60, VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, (LocaleController.isRTL ? 3 : 5) | 80, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 14.0f, 14.0f));
        r0.dialogsAdapter = new DialogsAdapter(context2);
        r0.listView.setAdapter(r0.dialogsAdapter);
        r0.page2 = new SizeNotifierFrameLayout(context2) {
            protected void onMeasure(int i, int i2) {
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

            protected boolean drawChild(Canvas canvas, View view, long j) {
                j = super.drawChild(canvas, view, j);
                if (view == ThemePreviewActivity.this.actionBar2 && ThemePreviewActivity.this.parentLayout != null) {
                    ThemePreviewActivity.this.parentLayout.drawHeaderShadow(canvas, ThemePreviewActivity.this.actionBar2.getVisibility() == 0 ? ThemePreviewActivity.this.actionBar2.getMeasuredHeight() : 0);
                }
                return j;
            }
        };
        r0.page2.setBackgroundImage(Theme.getCachedWallpaper());
        r0.actionBar2 = createActionBar(context);
        r0.actionBar2.setBackButtonDrawable(new BackDrawable(false));
        r0.actionBar2.setTitle("Reinhardt");
        r0.actionBar2.setSubtitle(LocaleController.formatDateOnline((System.currentTimeMillis() / 1000) - 3600));
        r0.page2.addView(r0.actionBar2, LayoutHelper.createFrame(-1, -2.0f));
        r0.listView2 = new RecyclerListView(context2);
        r0.listView2.setVerticalScrollBarEnabled(true);
        r0.listView2.setItemAnimator(null);
        r0.listView2.setLayoutAnimation(null);
        r0.listView2.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f));
        r0.listView2.setClipToPadding(false);
        r0.listView2.setLayoutManager(new LinearLayoutManager(context2, 1, true));
        r0.listView2.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        r0.page2.addView(r0.listView2, LayoutHelper.createFrame(-1, -1, 51));
        r0.messagesAdapter = new MessagesAdapter(context2);
        r0.listView2.setAdapter(r0.messagesAdapter);
        r0.fragmentView = new FrameLayout(context2);
        FrameLayout frameLayout = (FrameLayout) r0.fragmentView;
        final View viewPager = new ViewPager(context2);
        viewPager.addOnPageChangeListener(new C23015());
        viewPager.setAdapter(new C23026());
        AndroidUtilities.setViewPagerEdgeEffectColor(viewPager, Theme.getColor(Theme.key_actionBarDefault));
        frameLayout.addView(viewPager, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        View view = new View(context2);
        view.setBackgroundResource(C0446R.drawable.header_shadow_reverse);
        frameLayout.addView(view, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        view = new FrameLayout(context2);
        view.setBackgroundColor(-1);
        frameLayout.addView(view, LayoutHelper.createFrame(-1, 48, 83));
        r0.dotsContainer = new View(context2) {
            private Paint paint = new Paint(1);

            protected void onDraw(Canvas canvas) {
                int currentItem = viewPager.getCurrentItem();
                int i = 0;
                while (i < 2) {
                    this.paint.setColor(i == currentItem ? -6710887 : -3355444);
                    canvas.drawCircle((float) AndroidUtilities.dp((float) (3 + (15 * i))), (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(3.0f), this.paint);
                    i++;
                }
            }
        };
        view.addView(r0.dotsContainer, LayoutHelper.createFrame(22, 8, 17));
        View textView = new TextView(context2);
        textView.setTextSize(1, 14.0f);
        textView.setTextColor(-15095832);
        textView.setGravity(17);
        textView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_AUDIO_SELECTOR_COLOR, 0));
        textView.setPadding(AndroidUtilities.dp(29.0f), 0, AndroidUtilities.dp(29.0f), 0);
        textView.setText(LocaleController.getString("Cancel", C0446R.string.Cancel).toUpperCase());
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        view.addView(textView, LayoutHelper.createFrame(-2, -1, 51));
        textView.setOnClickListener(new C17288());
        textView = new TextView(context2);
        textView.setTextSize(1, 14.0f);
        textView.setTextColor(-15095832);
        textView.setGravity(17);
        textView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_AUDIO_SELECTOR_COLOR, 0));
        textView.setPadding(AndroidUtilities.dp(29.0f), 0, AndroidUtilities.dp(29.0f), 0);
        textView.setText(LocaleController.getString("ApplyTheme", C0446R.string.ApplyTheme).toUpperCase());
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        view.addView(textView, LayoutHelper.createFrame(-2, -1, 53));
        textView.setOnClickListener(new C17299());
        return r0.fragmentView;
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
        super.onFragmentDestroy();
    }

    public void onResume() {
        super.onResume();
        if (this.dialogsAdapter != null) {
            this.dialogsAdapter.notifyDataSetChanged();
        }
        if (this.messagesAdapter != null) {
            this.messagesAdapter.notifyDataSetChanged();
        }
    }

    public boolean onBackPressed() {
        Theme.applyPreviousTheme();
        this.parentLayout.rebuildAllFragmentViews(false, false);
        return super.onBackPressed();
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.emojiDidLoaded && this.listView != 0) {
            i = this.listView.getChildCount();
            for (objArr = null; objArr < i; objArr++) {
                View childAt = this.listView.getChildAt(objArr);
                if (childAt instanceof DialogCell) {
                    ((DialogCell) childAt).update(0);
                }
            }
        }
    }
}
