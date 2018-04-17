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
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
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

        public void onClick(View v) {
            Theme.applyPreviousTheme();
            ThemePreviewActivity.this.parentLayout.rebuildAllFragmentViews(false, false);
            ThemePreviewActivity.this.finishFragment();
        }
    }

    /* renamed from: org.telegram.ui.ThemePreviewActivity$9 */
    class C17299 implements OnClickListener {
        C17299() {
        }

        public void onClick(View v) {
            ThemePreviewActivity.this.applied = true;
            ThemePreviewActivity.this.parentLayout.rebuildAllFragmentViews(false, false);
            Theme.applyThemeFile(ThemePreviewActivity.this.themeFile, ThemePreviewActivity.this.applyingTheme.name, false);
            ThemePreviewActivity.this.finishFragment();
        }
    }

    /* renamed from: org.telegram.ui.ThemePreviewActivity$1 */
    class C22991 extends ActionBarMenuItemSearchListener {
        C22991() {
        }

        public void onSearchExpand() {
        }

        public boolean canCollapseSearch() {
            return true;
        }

        public void onSearchCollapse() {
        }

        public void onTextChanged(EditText editText) {
        }
    }

    /* renamed from: org.telegram.ui.ThemePreviewActivity$5 */
    class C23015 implements OnPageChangeListener {
        C23015() {
        }

        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        public void onPageSelected(int position) {
            ThemePreviewActivity.this.dotsContainer.invalidate();
        }

        public void onPageScrollStateChanged(int state) {
        }
    }

    /* renamed from: org.telegram.ui.ThemePreviewActivity$6 */
    class C23026 extends PagerAdapter {
        C23026() {
        }

        public int getCount() {
            return 2;
        }

        public boolean isViewFromObject(View view, Object object) {
            return object == view;
        }

        public int getItemPosition(Object object) {
            return -1;
        }

        public Object instantiateItem(ViewGroup container, int position) {
            View view = position == 0 ? ThemePreviewActivity.this.page1 : ThemePreviewActivity.this.page2;
            container.addView(view);
            return view;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        public void unregisterDataSetObserver(DataSetObserver observer) {
            if (observer != null) {
                super.unregisterDataSetObserver(observer);
            }
        }
    }

    public class DialogsAdapter extends SelectionAdapter {
        private ArrayList<CustomDialog> dialogs = new ArrayList();
        private Context mContext;

        public DialogsAdapter(Context context) {
            this.mContext = context;
            int date = (int) (System.currentTimeMillis() / 1000);
            CustomDialog customDialog = new CustomDialog();
            customDialog.name = "Eva Summer";
            customDialog.message = "Reminds me of a Chinese prove...";
            customDialog.id = 0;
            customDialog.unread_count = 0;
            customDialog.pinned = true;
            customDialog.muted = false;
            customDialog.type = 0;
            customDialog.date = date;
            customDialog.verified = false;
            customDialog.isMedia = false;
            customDialog.sent = true;
            this.dialogs.add(customDialog);
            customDialog = new CustomDialog();
            customDialog.name = "Alexandra Smith";
            customDialog.message = "Reminds me of a Chinese prove...";
            customDialog.id = 1;
            customDialog.unread_count = 2;
            customDialog.pinned = false;
            customDialog.muted = false;
            customDialog.type = 0;
            customDialog.date = date - 3600;
            customDialog.verified = false;
            customDialog.isMedia = false;
            customDialog.sent = false;
            this.dialogs.add(customDialog);
            customDialog = new CustomDialog();
            customDialog.name = "Make Apple";
            customDialog.message = "\ud83e\udd37\u200d\u2642\ufe0f Sticker";
            customDialog.id = 2;
            customDialog.unread_count = 3;
            customDialog.pinned = false;
            customDialog.muted = true;
            customDialog.type = 0;
            customDialog.date = date - 7200;
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
            customDialog.date = date - 10800;
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
            customDialog.date = date - 14400;
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
            customDialog.date = date - 18000;
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
            customDialog.date = date - 21600;
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
            customDialog.date = date - 25200;
            customDialog.verified = true;
            customDialog.isMedia = false;
            customDialog.sent = false;
            this.dialogs.add(customDialog);
        }

        public int getItemCount() {
            return this.dialogs.size() + 1;
        }

        public boolean isEnabled(ViewHolder holder) {
            return holder.getItemViewType() != 1;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = null;
            if (viewType == 0) {
                view = new DialogCell(this.mContext, false);
            } else if (viewType == 1) {
                view = new LoadingCell(this.mContext);
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                DialogCell cell = viewHolder.itemView;
                boolean z = true;
                if (i == getItemCount() - 1) {
                    z = false;
                }
                cell.useSeparator = z;
                cell.setDialog((CustomDialog) this.dialogs.get(i));
            }
        }

        public int getItemViewType(int i) {
            if (i == this.dialogs.size()) {
                return 1;
            }
            return 0;
        }
    }

    public class MessagesAdapter extends SelectionAdapter {
        private Context mContext;
        private ArrayList<MessageObject> messages = new ArrayList();

        /* renamed from: org.telegram.ui.ThemePreviewActivity$MessagesAdapter$1 */
        class C23031 implements ChatMessageCellDelegate {
            C23031() {
            }

            public void didPressedShare(ChatMessageCell cell) {
            }

            public boolean needPlayMessage(MessageObject messageObject) {
                return false;
            }

            public void didPressedChannelAvatar(ChatMessageCell cell, Chat chat, int postId) {
            }

            public void didPressedOther(ChatMessageCell cell) {
            }

            public void didPressedUserAvatar(ChatMessageCell cell, User user) {
            }

            public void didPressedBotButton(ChatMessageCell cell, KeyboardButton button) {
            }

            public void didPressedCancelSendButton(ChatMessageCell cell) {
            }

            public void didLongPressed(ChatMessageCell cell) {
            }

            public boolean canPerformActions() {
                return false;
            }

            public void didPressedUrl(MessageObject messageObject, CharacterStyle url, boolean longPress) {
            }

            public void needOpenWebView(String url, String title, String description, String originalUrl, int w, int h) {
            }

            public void didPressedReplyMessage(ChatMessageCell cell, int id) {
            }

            public void didPressedViaBot(ChatMessageCell cell, String username) {
            }

            public void didPressedImage(ChatMessageCell cell) {
            }

            public void didPressedInstantButton(ChatMessageCell cell, int type) {
            }

            public boolean isChatAdminCell(int uid) {
                return false;
            }
        }

        /* renamed from: org.telegram.ui.ThemePreviewActivity$MessagesAdapter$2 */
        class C23042 implements ChatActionCellDelegate {
            C23042() {
            }

            public void didClickedImage(ChatActionCell cell) {
            }

            public void didLongPressed(ChatActionCell cell) {
            }

            public void needOpenUserProfile(int uid) {
            }

            public void didPressedReplyMessage(ChatActionCell cell, int id) {
            }

            public void didPressedBotButton(MessageObject messageObject, KeyboardButton button) {
            }
        }

        public MessagesAdapter(Context context) {
            this.mContext = context;
            int date = ((int) (System.currentTimeMillis() / 1000)) - 3600;
            Message message = new TL_message();
            message.message = "Reinhardt, we need to find you some new tunes \ud83c\udfb6.";
            message.date = date + 60;
            message.dialog_id = 1;
            message.flags = 259;
            message.from_id = UserConfig.getInstance(ThemePreviewActivity.this.currentAccount).getClientUserId();
            message.id = 1;
            message.media = new TL_messageMediaEmpty();
            message.out = true;
            message.to_id = new TL_peerUser();
            message.to_id.user_id = 0;
            MessageObject replyMessageObject = new MessageObject(ThemePreviewActivity.this.currentAccount, message, true);
            message = new TL_message();
            message.message = "I can't even take you seriously right now.";
            message.date = date + 960;
            message.dialog_id = 1;
            message.flags = 259;
            message.from_id = UserConfig.getInstance(ThemePreviewActivity.this.currentAccount).getClientUserId();
            message.id = 1;
            message.media = new TL_messageMediaEmpty();
            message.out = true;
            message.to_id = new TL_peerUser();
            message.to_id.user_id = 0;
            this.messages.add(new MessageObject(ThemePreviewActivity.this.currentAccount, message, true));
            message = new TL_message();
            message.date = date + TsExtractor.TS_STREAM_TYPE_HDMV_DTS;
            message.dialog_id = 1;
            message.flags = 259;
            message.from_id = 0;
            message.id = 5;
            message.media = new TL_messageMediaDocument();
            MessageMedia messageMedia = message.media;
            messageMedia.flags |= 3;
            message.media.document = new TL_document();
            message.media.document.mime_type = MimeTypes.AUDIO_MP4;
            message.media.document.thumb = new TL_photoSizeEmpty();
            message.media.document.thumb.type = "s";
            TL_documentAttributeAudio audio = new TL_documentAttributeAudio();
            audio.duration = 243;
            audio.performer = "David Hasselhoff";
            audio.title = "True Survivor";
            message.media.document.attributes.add(audio);
            message.out = false;
            message.to_id = new TL_peerUser();
            message.to_id.user_id = UserConfig.getInstance(ThemePreviewActivity.this.currentAccount).getClientUserId();
            this.messages.add(new MessageObject(ThemePreviewActivity.this.currentAccount, message, true));
            message = new TL_message();
            message.message = "Ah, you kids today with techno music! You should enjoy the classics, like Hasselhoff!";
            message.date = date + 60;
            message.dialog_id = 1;
            message.flags = 265;
            message.from_id = 0;
            message.id = 1;
            message.reply_to_msg_id = 5;
            message.media = new TL_messageMediaEmpty();
            message.out = false;
            message.to_id = new TL_peerUser();
            message.to_id.user_id = UserConfig.getInstance(ThemePreviewActivity.this.currentAccount).getClientUserId();
            MessageObject messageObject = new MessageObject(ThemePreviewActivity.this.currentAccount, message, true);
            messageObject.customReplyName = "Lucio";
            messageObject.replyMessageObject = replyMessageObject;
            this.messages.add(messageObject);
            message = new TL_message();
            message.date = date + 120;
            message.dialog_id = 1;
            message.flags = 259;
            message.from_id = UserConfig.getInstance(ThemePreviewActivity.this.currentAccount).getClientUserId();
            message.id = 1;
            message.media = new TL_messageMediaDocument();
            MessageMedia messageMedia2 = message.media;
            messageMedia2.flags |= 3;
            message.media.document = new TL_document();
            message.media.document.mime_type = "audio/ogg";
            message.media.document.thumb = new TL_photoSizeEmpty();
            message.media.document.thumb.type = "s";
            TL_documentAttributeAudio audio2 = new TL_documentAttributeAudio();
            audio2.flags = 1028;
            audio2.duration = 3;
            audio2.voice = true;
            audio2.waveform = new byte[]{(byte) 0, (byte) 4, (byte) 17, (byte) -50, (byte) -93, (byte) 86, (byte) -103, (byte) -45, (byte) -12, (byte) -26, (byte) 63, (byte) -25, (byte) -3, (byte) 109, (byte) -114, (byte) -54, (byte) -4, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -29, (byte) -1, (byte) -1, (byte) -25, (byte) -1, (byte) -1, (byte) -97, (byte) -43, (byte) 57, (byte) -57, (byte) -108, (byte) 1, (byte) -91, (byte) -4, (byte) -47, (byte) 21, (byte) 99, (byte) 10, (byte) 97, (byte) 43, (byte) 45, (byte) 115, (byte) -112, (byte) -77, (byte) 51, (byte) -63, (byte) 66, (byte) 40, (byte) 34, (byte) -122, (byte) -116, (byte) 48, (byte) -124, (byte) 16, (byte) 66, (byte) -120, (byte) 16, (byte) 68, (byte) 16, (byte) 33, (byte) 4, (byte) 1};
            message.media.document.attributes.add(audio2);
            message.out = true;
            message.to_id = new TL_peerUser();
            message.to_id.user_id = 0;
            messageObject = new MessageObject(ThemePreviewActivity.this.currentAccount, message, true);
            messageObject.audioProgressSec = 1;
            messageObject.audioProgress = 0.3f;
            messageObject.useCustomPhoto = true;
            this.messages.add(messageObject);
            this.messages.add(replyMessageObject);
            message = new TL_message();
            message.date = date + 10;
            message.dialog_id = 1;
            message.flags = 257;
            message.from_id = 0;
            message.id = 1;
            message.media = new TL_messageMediaPhoto();
            messageMedia = message.media;
            messageMedia.flags |= 3;
            message.media.photo = new TL_photo();
            message.media.photo.has_stickers = false;
            message.media.photo.id = 1;
            message.media.photo.access_hash = 0;
            message.media.photo.date = date;
            TL_photoSize photoSize = new TL_photoSize();
            photoSize.size = 0;
            photoSize.w = 500;
            photoSize.h = 302;
            photoSize.type = "s";
            photoSize.location = new TL_fileLocationUnavailable();
            message.media.photo.sizes.add(photoSize);
            message.message = "Bring it on! I LIVE for this!";
            message.out = false;
            message.to_id = new TL_peerUser();
            message.to_id.user_id = UserConfig.getInstance(ThemePreviewActivity.this.currentAccount).getClientUserId();
            MessageObject messageObject2 = new MessageObject(ThemePreviewActivity.this.currentAccount, message, true);
            messageObject2.useCustomPhoto = true;
            this.messages.add(messageObject2);
            message = new TL_message();
            message.message = LocaleController.formatDateChat((long) date);
            message.id = 0;
            message.date = date;
            messageObject2 = new MessageObject(ThemePreviewActivity.this.currentAccount, message, false);
            messageObject2.type = 10;
            messageObject2.contentType = 1;
            messageObject2.isDateObject = true;
            this.messages.add(messageObject2);
        }

        public int getItemCount() {
            return this.messages.size();
        }

        public boolean isEnabled(ViewHolder holder) {
            return false;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = null;
            if (viewType == 0) {
                view = new ChatMessageCell(this.mContext);
                ((ChatMessageCell) view).setDelegate(new C23031());
            } else if (viewType == 1) {
                view = new ChatActionCell(this.mContext);
                ((ChatActionCell) view).setDelegate(new C23042());
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            MessageObject message = (MessageObject) this.messages.get(position);
            View view = holder.itemView;
            if (view instanceof ChatMessageCell) {
                boolean pinnedBotton;
                ChatMessageCell messageCell = (ChatMessageCell) view;
                boolean pinnedTop = false;
                messageCell.isChat = false;
                int nextType = getItemViewType(position - 1);
                int prevType = getItemViewType(position + 1);
                if ((message.messageOwner.reply_markup instanceof TL_replyInlineMarkup) || nextType != holder.getItemViewType()) {
                    pinnedBotton = false;
                } else {
                    MessageObject nextMessage = (MessageObject) this.messages.get(position - 1);
                    boolean z = nextMessage.isOutOwner() == message.isOutOwner() && Math.abs(nextMessage.messageOwner.date - message.messageOwner.date) <= 300;
                    pinnedBotton = z;
                }
                if (prevType == holder.getItemViewType()) {
                    MessageObject prevMessage = (MessageObject) this.messages.get(position + 1);
                    if (!(prevMessage.messageOwner.reply_markup instanceof TL_replyInlineMarkup) && prevMessage.isOutOwner() == message.isOutOwner() && Math.abs(prevMessage.messageOwner.date - message.messageOwner.date) <= 300) {
                        pinnedTop = true;
                    }
                }
                messageCell.setFullyDraw(true);
                messageCell.setMessageObject(message, null, pinnedBotton, pinnedTop);
            } else if (view instanceof ChatActionCell) {
                ChatActionCell actionCell = (ChatActionCell) view;
                actionCell.setMessageObject(message);
                actionCell.setAlpha(1.0f);
            }
        }

        public int getItemViewType(int i) {
            if (i < 0 || i >= this.messages.size()) {
                return 4;
            }
            return ((MessageObject) this.messages.get(i)).contentType;
        }
    }

    public ThemePreviewActivity(File file, ThemeInfo themeInfo) {
        this.swipeBackEnabled = false;
        this.applyingTheme = themeInfo;
        this.themeFile = file;
    }

    public View createView(Context context) {
        Context context2 = context;
        this.page1 = new FrameLayout(context2);
        this.actionBar.createMenu().addItem(0, (int) R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new C22991()).getSearchField().setHint(LocaleController.getString("Search", R.string.Search));
        this.actionBar.setBackButtonDrawable(new MenuDrawable());
        this.actionBar.setAddToContainer(false);
        this.actionBar.setTitle(LocaleController.getString("ThemePreview", R.string.ThemePreview));
        this.page1 = new FrameLayout(context2) {
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int widthSize = MeasureSpec.getSize(widthMeasureSpec);
                int heightSize = MeasureSpec.getSize(heightMeasureSpec);
                setMeasuredDimension(widthSize, heightSize);
                measureChildWithMargins(ThemePreviewActivity.this.actionBar, widthMeasureSpec, 0, heightMeasureSpec, 0);
                int actionBarHeight = ThemePreviewActivity.this.actionBar.getMeasuredHeight();
                if (ThemePreviewActivity.this.actionBar.getVisibility() == 0) {
                    heightSize -= actionBarHeight;
                }
                ((FrameLayout.LayoutParams) ThemePreviewActivity.this.listView.getLayoutParams()).topMargin = actionBarHeight;
                ThemePreviewActivity.this.listView.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(heightSize, NUM));
                measureChildWithMargins(ThemePreviewActivity.this.floatingButton, widthMeasureSpec, 0, heightMeasureSpec, 0);
            }

            protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                boolean result = super.drawChild(canvas, child, drawingTime);
                if (child == ThemePreviewActivity.this.actionBar && ThemePreviewActivity.this.parentLayout != null) {
                    ThemePreviewActivity.this.parentLayout.drawHeaderShadow(canvas, ThemePreviewActivity.this.actionBar.getVisibility() == 0 ? ThemePreviewActivity.this.actionBar.getMeasuredHeight() : 0);
                }
                return result;
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
        r0.page1.addView(r0.listView, LayoutHelper.createFrame(-1, -1, 51));
        r0.floatingButton = new ImageView(context2);
        r0.floatingButton.setScaleType(ScaleType.CENTER);
        Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_chats_actionBackground), Theme.getColor(Theme.key_chats_actionPressedBackground));
        if (VERSION.SDK_INT < 21) {
            Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.ACTION_BAR_VIDEO_EDIT_COLOR, Mode.MULTIPLY));
            Drawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            drawable = combinedDrawable;
        }
        r0.floatingButton.setBackgroundDrawable(drawable);
        r0.floatingButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_actionIcon), Mode.MULTIPLY));
        r0.floatingButton.setImageResource(R.drawable.floating_pencil);
        if (VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(r0.floatingButton, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            animator.addState(new int[0], ObjectAnimator.ofFloat(r0.floatingButton, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            r0.floatingButton.setStateListAnimator(animator);
            r0.floatingButton.setOutlineProvider(new C17263());
        }
        r0.page1.addView(r0.floatingButton, LayoutHelper.createFrame(VERSION.SDK_INT >= 21 ? 56 : 60, VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, (LocaleController.isRTL ? 3 : 5) | 80, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 14.0f, 14.0f));
        r0.dialogsAdapter = new DialogsAdapter(context2);
        r0.listView.setAdapter(r0.dialogsAdapter);
        r0.page2 = new SizeNotifierFrameLayout(context2) {
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int widthSize = MeasureSpec.getSize(widthMeasureSpec);
                int heightSize = MeasureSpec.getSize(heightMeasureSpec);
                setMeasuredDimension(widthSize, heightSize);
                measureChildWithMargins(ThemePreviewActivity.this.actionBar2, widthMeasureSpec, 0, heightMeasureSpec, 0);
                int actionBarHeight = ThemePreviewActivity.this.actionBar2.getMeasuredHeight();
                if (ThemePreviewActivity.this.actionBar2.getVisibility() == 0) {
                    heightSize -= actionBarHeight;
                }
                ((FrameLayout.LayoutParams) ThemePreviewActivity.this.listView2.getLayoutParams()).topMargin = actionBarHeight;
                ThemePreviewActivity.this.listView2.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(heightSize, NUM));
            }

            protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                boolean result = super.drawChild(canvas, child, drawingTime);
                if (child == ThemePreviewActivity.this.actionBar2 && ThemePreviewActivity.this.parentLayout != null) {
                    ThemePreviewActivity.this.parentLayout.drawHeaderShadow(canvas, ThemePreviewActivity.this.actionBar2.getVisibility() == 0 ? ThemePreviewActivity.this.actionBar2.getMeasuredHeight() : 0);
                }
                return result;
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
        RecyclerListView recyclerListView = r0.listView2;
        if (LocaleController.isRTL) {
            i = 1;
        }
        recyclerListView.setVerticalScrollbarPosition(i);
        r0.page2.addView(r0.listView2, LayoutHelper.createFrame(-1, -1, 51));
        r0.messagesAdapter = new MessagesAdapter(context2);
        r0.listView2.setAdapter(r0.messagesAdapter);
        r0.fragmentView = new FrameLayout(context2);
        FrameLayout frameLayout = r0.fragmentView;
        final ViewPager viewPager = new ViewPager(context2);
        viewPager.addOnPageChangeListener(new C23015());
        viewPager.setAdapter(new C23026());
        AndroidUtilities.setViewPagerEdgeEffectColor(viewPager, Theme.getColor(Theme.key_actionBarDefault));
        frameLayout.addView(viewPager, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        View shadow = new View(context2);
        shadow.setBackgroundResource(R.drawable.header_shadow_reverse);
        frameLayout.addView(shadow, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        FrameLayout bottomLayout = new FrameLayout(context2);
        bottomLayout.setBackgroundColor(-1);
        frameLayout.addView(bottomLayout, LayoutHelper.createFrame(-1, 48, 83));
        r0.dotsContainer = new View(context2) {
            private Paint paint = new Paint(1);

            protected void onDraw(Canvas canvas) {
                int selected = viewPager.getCurrentItem();
                int a = 0;
                while (a < 2) {
                    this.paint.setColor(a == selected ? -6710887 : -3355444);
                    canvas.drawCircle((float) AndroidUtilities.dp((float) (3 + (15 * a))), (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(3.0f), this.paint);
                    a++;
                }
            }
        };
        bottomLayout.addView(r0.dotsContainer, LayoutHelper.createFrame(22, 8, 17));
        TextView cancelButton = new TextView(context2);
        cancelButton.setTextSize(1, 14.0f);
        cancelButton.setTextColor(-15095832);
        cancelButton.setGravity(17);
        cancelButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_AUDIO_SELECTOR_COLOR, 0));
        cancelButton.setPadding(AndroidUtilities.dp(29.0f), 0, AndroidUtilities.dp(29.0f), 0);
        cancelButton.setText(LocaleController.getString("Cancel", R.string.Cancel).toUpperCase());
        cancelButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        bottomLayout.addView(cancelButton, LayoutHelper.createFrame(-2, -1, 51));
        cancelButton.setOnClickListener(new C17288());
        TextView doneButton = new TextView(context2);
        doneButton.setTextSize(1, 14.0f);
        doneButton.setTextColor(-15095832);
        doneButton.setGravity(17);
        doneButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_AUDIO_SELECTOR_COLOR, 0));
        doneButton.setPadding(AndroidUtilities.dp(29.0f), 0, AndroidUtilities.dp(29.0f), 0);
        doneButton.setText(LocaleController.getString("ApplyTheme", R.string.ApplyTheme).toUpperCase());
        doneButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        bottomLayout.addView(doneButton, LayoutHelper.createFrame(-2, -1, 53));
        doneButton.setOnClickListener(new C17299());
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

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.emojiDidLoaded && this.listView != null) {
            int count = this.listView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof DialogCell) {
                    ((DialogCell) child).update(0);
                }
            }
        }
    }
}
