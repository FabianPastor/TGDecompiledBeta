package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.exoplayer2.extractor.ts.PsExtractor;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;
import org.telegram.messenger.exoplayer2.ui.AspectRatioFrameLayout;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.LinearSmoothScrollerMiddle;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.State;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEvent;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeStickerSet;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventsFilter;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsAdmins;
import org.telegram.tgnet.TLRPC.TL_channels_adminLogResults;
import org.telegram.tgnet.TLRPC.TL_channels_channelParticipants;
import org.telegram.tgnet.TLRPC.TL_channels_getAdminLog;
import org.telegram.tgnet.TLRPC.TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetEmpty;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetShortName;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_replyInlineMarkup;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.BotHelpCell;
import org.telegram.ui.Cells.BotHelpCell.BotHelpCellDelegate;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate;
import org.telegram.ui.Cells.ChatLoadingCell;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate;
import org.telegram.ui.Cells.ChatUnreadCell;
import org.telegram.ui.Components.AdminLogFilterAlert;
import org.telegram.ui.Components.AdminLogFilterAlert.AdminLogFilterAlertDelegate;
import org.telegram.ui.Components.ChatAvatarContainer;
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PipRoundVideoView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.URLSpanMono;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Components.URLSpanReplacement;
import org.telegram.ui.Components.URLSpanUserMention;
import org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PlaceProviderObject;

public class ChannelAdminLogActivity extends BaseFragment implements NotificationCenterDelegate {
    private ArrayList<ChannelParticipant> admins;
    private Paint aspectPaint;
    private Path aspectPath;
    private AspectRatioFrameLayout aspectRatioFrameLayout;
    private ChatAvatarContainer avatarContainer;
    private FrameLayout bottomOverlayChat;
    private TextView bottomOverlayChatText;
    private ImageView bottomOverlayImage;
    private ChatActivityAdapter chatAdapter;
    private LinearLayoutManager chatLayoutManager;
    private RecyclerListView chatListView;
    private ArrayList<ChatMessageCell> chatMessageCellsCache = new ArrayList();
    private boolean checkTextureViewPosition;
    private SizeNotifierFrameLayout contentView;
    protected Chat currentChat;
    private TL_channelAdminLogEventsFilter currentFilter = null;
    private boolean currentFloatingDateOnScreen;
    private boolean currentFloatingTopIsNotMessage;
    private TextView emptyView;
    private FrameLayout emptyViewContainer;
    private boolean endReached;
    private AnimatorSet floatingDateAnimation;
    private ChatActionCell floatingDateView;
    private boolean loading;
    private int loadsCount;
    protected ArrayList<MessageObject> messages = new ArrayList();
    private HashMap<String, ArrayList<MessageObject>> messagesByDays = new HashMap();
    private LongSparseArray<MessageObject> messagesDict = new LongSparseArray();
    private int[] mid = new int[]{2};
    private int minDate;
    private long minEventId;
    private boolean openAnimationEnded;
    private boolean paused = true;
    private RadialProgressView progressBar;
    private FrameLayout progressView;
    private View progressView2;
    private PhotoViewerProvider provider = new C23291();
    private FrameLayout roundVideoContainer;
    private MessageObject scrollToMessage;
    private int scrollToOffsetOnRecreate = 0;
    private int scrollToPositionOnRecreate = -1;
    private boolean scrollingFloatingDate;
    private ImageView searchCalendarButton;
    private FrameLayout searchContainer;
    private SimpleTextView searchCountText;
    private ImageView searchDownButton;
    private ActionBarMenuItem searchItem;
    private String searchQuery = TtmlNode.ANONYMOUS_REGION_ID;
    private ImageView searchUpButton;
    private boolean searchWas;
    private SparseArray<User> selectedAdmins;
    private MessageObject selectedObject;
    private TextureView videoTextureView;
    private boolean wasPaused = false;

    /* renamed from: org.telegram.ui.ChannelAdminLogActivity$6 */
    class C09556 implements OnTouchListener {
        C09556() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ChannelAdminLogActivity$2 */
    class C19642 implements RequestDelegate {
        C19642() {
        }

        public void run(TLObject response, TL_error error) {
            if (response != null) {
                final TL_channels_adminLogResults res = (TL_channels_adminLogResults) response;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        int a;
                        int i = 0;
                        MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).putUsers(res.users, false);
                        MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).putChats(res.chats, false);
                        int oldRowsCount = ChannelAdminLogActivity.this.messages.size();
                        boolean added = false;
                        for (a = 0; a < res.events.size(); a++) {
                            TL_channelAdminLogEvent event = (TL_channelAdminLogEvent) res.events.get(a);
                            if (ChannelAdminLogActivity.this.messagesDict.indexOfKey(event.id) < 0) {
                                ChannelAdminLogActivity.this.minEventId = Math.min(ChannelAdminLogActivity.this.minEventId, event.id);
                                added = true;
                                MessageObject messageObject = new MessageObject(ChannelAdminLogActivity.this.currentAccount, event, ChannelAdminLogActivity.this.messages, ChannelAdminLogActivity.this.messagesByDays, ChannelAdminLogActivity.this.currentChat, ChannelAdminLogActivity.this.mid);
                                if (messageObject.contentType >= 0) {
                                    ChannelAdminLogActivity.this.messagesDict.put(event.id, messageObject);
                                }
                            }
                        }
                        a = ChannelAdminLogActivity.this.messages.size() - oldRowsCount;
                        ChannelAdminLogActivity.this.loading = false;
                        if (!added) {
                            ChannelAdminLogActivity.this.endReached = true;
                        }
                        ChannelAdminLogActivity.this.progressView.setVisibility(4);
                        ChannelAdminLogActivity.this.chatListView.setEmptyView(ChannelAdminLogActivity.this.emptyViewContainer);
                        if (a != 0) {
                            boolean end = false;
                            if (ChannelAdminLogActivity.this.endReached) {
                                end = true;
                                ChannelAdminLogActivity.this.chatAdapter.notifyItemRangeChanged(0, 2);
                            }
                            int firstVisPos = ChannelAdminLogActivity.this.chatLayoutManager.findLastVisibleItemPosition();
                            View firstVisView = ChannelAdminLogActivity.this.chatLayoutManager.findViewByPosition(firstVisPos);
                            int top = (firstVisView == null ? 0 : firstVisView.getTop()) - ChannelAdminLogActivity.this.chatListView.getPaddingTop();
                            if (a - (end ? 1 : 0) > 0) {
                                int insertStart = (end ? 0 : 1) + 1;
                                ChannelAdminLogActivity.this.chatAdapter.notifyItemChanged(insertStart);
                                ChannelAdminLogActivity.this.chatAdapter.notifyItemRangeInserted(insertStart, a - (end ? 1 : 0));
                            }
                            if (firstVisPos != -1) {
                                LinearLayoutManager access$1300 = ChannelAdminLogActivity.this.chatLayoutManager;
                                int i2 = firstVisPos + a;
                                if (end) {
                                    i = 1;
                                }
                                access$1300.scrollToPositionWithOffset(i2 - i, top);
                            }
                        } else if (ChannelAdminLogActivity.this.endReached) {
                            ChannelAdminLogActivity.this.chatAdapter.notifyItemRemoved(0);
                        }
                    }
                });
            }
        }
    }

    /* renamed from: org.telegram.ui.ChannelAdminLogActivity$3 */
    class C19653 extends ActionBarMenuOnItemClick {
        C19653() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                ChannelAdminLogActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.ChannelAdminLogActivity$4 */
    class C19664 extends ActionBarMenuItemSearchListener {
        C19664() {
        }

        public void onSearchCollapse() {
            ChannelAdminLogActivity.this.searchQuery = TtmlNode.ANONYMOUS_REGION_ID;
            ChannelAdminLogActivity.this.avatarContainer.setVisibility(0);
            if (ChannelAdminLogActivity.this.searchWas) {
                ChannelAdminLogActivity.this.searchWas = false;
                ChannelAdminLogActivity.this.loadMessages(true);
            }
            ChannelAdminLogActivity.this.updateBottomOverlay();
        }

        public void onSearchExpand() {
            ChannelAdminLogActivity.this.avatarContainer.setVisibility(8);
            ChannelAdminLogActivity.this.updateBottomOverlay();
        }

        public void onSearchPressed(EditText editText) {
            ChannelAdminLogActivity.this.searchWas = true;
            ChannelAdminLogActivity.this.searchQuery = editText.getText().toString();
            ChannelAdminLogActivity.this.loadMessages(true);
        }
    }

    /* renamed from: org.telegram.ui.ChannelAdminLogActivity$8 */
    class C19688 implements OnItemClickListener {
        C19688() {
        }

        public void onItemClick(View view, int position) {
            ChannelAdminLogActivity.this.createMenu(view);
        }
    }

    public class ChatActivityAdapter extends Adapter {
        private int loadingUpRow;
        private Context mContext;
        private int messagesEndRow;
        private int messagesStartRow;
        private int rowCount;

        /* renamed from: org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter$1 */
        class C19691 implements ChatMessageCellDelegate {
            C19691() {
            }

            public void didPressedShare(ChatMessageCell cell) {
                if (ChannelAdminLogActivity.this.getParentActivity() != null) {
                    ChannelAdminLogActivity channelAdminLogActivity = ChannelAdminLogActivity.this;
                    Context access$6100 = ChatActivityAdapter.this.mContext;
                    MessageObject messageObject = cell.getMessageObject();
                    boolean z = ChatObject.isChannel(ChannelAdminLogActivity.this.currentChat) && !ChannelAdminLogActivity.this.currentChat.megagroup && ChannelAdminLogActivity.this.currentChat.username != null && ChannelAdminLogActivity.this.currentChat.username.length() > 0;
                    channelAdminLogActivity.showDialog(ShareAlert.createShareAlert(access$6100, messageObject, null, z, null, false));
                }
            }

            public boolean needPlayMessage(MessageObject messageObject) {
                if (!messageObject.isVoice()) {
                    if (!messageObject.isRoundVideo()) {
                        if (messageObject.isMusic()) {
                            return MediaController.getInstance().setPlaylist(ChannelAdminLogActivity.this.messages, messageObject);
                        }
                        return false;
                    }
                }
                boolean result = MediaController.getInstance().playMessage(messageObject);
                MediaController.getInstance().setVoiceMessagesPlaylist(null, false);
                return result;
            }

            public void didPressedChannelAvatar(ChatMessageCell cell, Chat chat, int postId) {
                if (chat != null && chat != ChannelAdminLogActivity.this.currentChat) {
                    Bundle args = new Bundle();
                    args.putInt("chat_id", chat.id);
                    if (postId != 0) {
                        args.putInt("message_id", postId);
                    }
                    if (MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).checkCanOpenChat(args, ChannelAdminLogActivity.this)) {
                        ChannelAdminLogActivity.this.presentFragment(new ChatActivity(args), true);
                    }
                }
            }

            public void didPressedOther(ChatMessageCell cell) {
                ChannelAdminLogActivity.this.createMenu(cell);
            }

            public void didPressedUserAvatar(ChatMessageCell cell, User user) {
                if (user != null && user.id != UserConfig.getInstance(ChannelAdminLogActivity.this.currentAccount).getClientUserId()) {
                    Bundle args = new Bundle();
                    args.putInt("user_id", user.id);
                    ChannelAdminLogActivity.this.addCanBanUser(args, user.id);
                    ProfileActivity fragment = new ProfileActivity(args);
                    fragment.setPlayProfileAnimation(false);
                    ChannelAdminLogActivity.this.presentFragment(fragment);
                }
            }

            public void didPressedBotButton(ChatMessageCell cell, KeyboardButton button) {
            }

            public void didPressedCancelSendButton(ChatMessageCell cell) {
            }

            public void didLongPressed(ChatMessageCell cell) {
                ChannelAdminLogActivity.this.createMenu(cell);
            }

            public boolean canPerformActions() {
                return true;
            }

            public void didPressedUrl(MessageObject messageObject, CharacterStyle url, boolean longPress) {
                if (url != null) {
                    if (url instanceof URLSpanMono) {
                        ((URLSpanMono) url).copyToClipboard();
                        Toast.makeText(ChannelAdminLogActivity.this.getParentActivity(), LocaleController.getString("TextCopied", R.string.TextCopied), 0).show();
                    } else if (url instanceof URLSpanUserMention) {
                        User user = MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).getUser(Utilities.parseInt(((URLSpanUserMention) url).getURL()));
                        if (user != null) {
                            MessagesController.openChatOrProfileWith(user, null, ChannelAdminLogActivity.this, 0, false);
                        }
                    } else if (url instanceof URLSpanNoUnderline) {
                        str = ((URLSpanNoUnderline) url).getURL();
                        if (str.startsWith("@")) {
                            MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).openByUserName(str.substring(1), ChannelAdminLogActivity.this, 0);
                        } else if (str.startsWith("#")) {
                            DialogsActivity fragment = new DialogsActivity(null);
                            fragment.setSearchString(str);
                            ChannelAdminLogActivity.this.presentFragment(fragment);
                        }
                    } else {
                        str = ((URLSpan) url).getURL();
                        if (longPress) {
                            Builder builder = new Builder(ChannelAdminLogActivity.this.getParentActivity());
                            builder.setTitle(str);
                            builder.setItems(new CharSequence[]{LocaleController.getString("Open", R.string.Open), LocaleController.getString("Copy", R.string.Copy)}, new OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == 0) {
                                        Browser.openUrl(ChannelAdminLogActivity.this.getParentActivity(), str, true);
                                    } else if (which == 1) {
                                        String url = str;
                                        if (url.startsWith("mailto:")) {
                                            url = url.substring(7);
                                        } else if (url.startsWith("tel:")) {
                                            url = url.substring(4);
                                        }
                                        AndroidUtilities.addToClipboard(url);
                                    }
                                }
                            });
                            ChannelAdminLogActivity.this.showDialog(builder.create());
                        } else if (url instanceof URLSpanReplacement) {
                            ChannelAdminLogActivity.this.showOpenUrlAlert(((URLSpanReplacement) url).getURL(), true);
                        } else if (url instanceof URLSpan) {
                            if (!(!(messageObject.messageOwner.media instanceof TL_messageMediaWebPage) || messageObject.messageOwner.media.webpage == null || messageObject.messageOwner.media.webpage.cached_page == null)) {
                                String lowerUrl = str.toLowerCase();
                                String lowerUrl2 = messageObject.messageOwner.media.webpage.url.toLowerCase();
                                if ((lowerUrl.contains("telegra.ph") || lowerUrl.contains("t.me/iv")) && (lowerUrl.contains(lowerUrl2) || lowerUrl2.contains(lowerUrl))) {
                                    ArticleViewer.getInstance().setParentActivity(ChannelAdminLogActivity.this.getParentActivity(), ChannelAdminLogActivity.this);
                                    ArticleViewer.getInstance().open(messageObject);
                                    return;
                                }
                            }
                            Browser.openUrl(ChannelAdminLogActivity.this.getParentActivity(), str, true);
                        } else if (url instanceof ClickableSpan) {
                            ((ClickableSpan) url).onClick(ChannelAdminLogActivity.this.fragmentView);
                        }
                    }
                }
            }

            public void needOpenWebView(String url, String title, String description, String originalUrl, int w, int h) {
                EmbedBottomSheet.show(ChatActivityAdapter.this.mContext, title, description, originalUrl, url, w, h);
            }

            public void didPressedReplyMessage(ChatMessageCell cell, int id) {
            }

            public void didPressedViaBot(ChatMessageCell cell, String username) {
            }

            public void didPressedImage(ChatMessageCell cell) {
                MessageObject message = cell.getMessageObject();
                if (message.type == 13) {
                    ChannelAdminLogActivity.this.showDialog(new StickersAlert(ChannelAdminLogActivity.this.getParentActivity(), ChannelAdminLogActivity.this, message.getInputStickerSet(), null, null));
                } else {
                    if (!(message.isVideo() || message.type == 1 || (message.type == 0 && !message.isWebpageDocument()))) {
                        if (!message.isGif()) {
                            File f;
                            if (message.type == 3) {
                                f = null;
                                try {
                                    if (!(message.messageOwner.attachPath == null || message.messageOwner.attachPath.length() == 0)) {
                                        f = new File(message.messageOwner.attachPath);
                                    }
                                    if (f == null || !f.exists()) {
                                        f = FileLoader.getPathToMessage(message.messageOwner);
                                    }
                                    Intent intent = new Intent("android.intent.action.VIEW");
                                    if (VERSION.SDK_INT >= 24) {
                                        intent.setFlags(1);
                                        intent.setDataAndType(FileProvider.getUriForFile(ChannelAdminLogActivity.this.getParentActivity(), "org.telegram.messenger.beta.provider", f), MimeTypes.VIDEO_MP4);
                                    } else {
                                        intent.setDataAndType(Uri.fromFile(f), MimeTypes.VIDEO_MP4);
                                    }
                                    ChannelAdminLogActivity.this.getParentActivity().startActivityForResult(intent, 500);
                                } catch (Exception e) {
                                    ChannelAdminLogActivity.this.alertUserOpenError(message);
                                }
                            } else if (message.type == 4) {
                                if (AndroidUtilities.isGoogleMapsInstalled(ChannelAdminLogActivity.this)) {
                                    LocationActivity fragment = new LocationActivity(0);
                                    fragment.setMessageObject(message);
                                    ChannelAdminLogActivity.this.presentFragment(fragment);
                                } else {
                                    return;
                                }
                            } else if (message.type == 9 || message.type == 0) {
                                if (message.getDocumentName().toLowerCase().endsWith("attheme")) {
                                    File f2;
                                    f = null;
                                    if (!(message.messageOwner.attachPath == null || message.messageOwner.attachPath.length() == 0)) {
                                        f2 = new File(message.messageOwner.attachPath);
                                        if (f2.exists()) {
                                            f = f2;
                                        }
                                    }
                                    if (f == null) {
                                        f2 = FileLoader.getPathToMessage(message.messageOwner);
                                        if (f2.exists()) {
                                            f = f2;
                                        }
                                    }
                                    if (ChannelAdminLogActivity.this.chatLayoutManager != null) {
                                        if (ChannelAdminLogActivity.this.chatLayoutManager.findLastVisibleItemPosition() < ChannelAdminLogActivity.this.chatLayoutManager.getItemCount() - 1) {
                                            ChannelAdminLogActivity.this.scrollToPositionOnRecreate = ChannelAdminLogActivity.this.chatLayoutManager.findFirstVisibleItemPosition();
                                            Holder holder = (Holder) ChannelAdminLogActivity.this.chatListView.findViewHolderForAdapterPosition(ChannelAdminLogActivity.this.scrollToPositionOnRecreate);
                                            if (holder != null) {
                                                ChannelAdminLogActivity.this.scrollToOffsetOnRecreate = holder.itemView.getTop();
                                            } else {
                                                ChannelAdminLogActivity.this.scrollToPositionOnRecreate = -1;
                                            }
                                        } else {
                                            ChannelAdminLogActivity.this.scrollToPositionOnRecreate = -1;
                                        }
                                    }
                                    ThemeInfo themeInfo = Theme.applyThemeFile(f, message.getDocumentName(), true);
                                    if (themeInfo != null) {
                                        ChannelAdminLogActivity.this.presentFragment(new ThemePreviewActivity(f, themeInfo));
                                        return;
                                    }
                                    ChannelAdminLogActivity.this.scrollToPositionOnRecreate = -1;
                                }
                                try {
                                    AndroidUtilities.openForView(message, ChannelAdminLogActivity.this.getParentActivity());
                                } catch (Exception e2) {
                                    ChannelAdminLogActivity.this.alertUserOpenError(message);
                                }
                            }
                        }
                    }
                    PhotoViewer.getInstance().setParentActivity(ChannelAdminLogActivity.this.getParentActivity());
                    PhotoViewer.getInstance().openPhoto(message, 0, 0, ChannelAdminLogActivity.this.provider);
                }
            }

            public void didPressedInstantButton(ChatMessageCell cell, int type) {
                MessageObject messageObject = cell.getMessageObject();
                if (type != 0) {
                    Browser.openUrl(ChannelAdminLogActivity.this.getParentActivity(), messageObject.messageOwner.media.webpage.url);
                } else if (messageObject.messageOwner.media != null && messageObject.messageOwner.media.webpage != null && messageObject.messageOwner.media.webpage.cached_page != null) {
                    ArticleViewer.getInstance().setParentActivity(ChannelAdminLogActivity.this.getParentActivity(), ChannelAdminLogActivity.this);
                    ArticleViewer.getInstance().open(messageObject);
                }
            }

            public boolean isChatAdminCell(int uid) {
                return false;
            }
        }

        /* renamed from: org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter$2 */
        class C19702 implements ChatActionCellDelegate {
            C19702() {
            }

            public void didClickedImage(ChatActionCell cell) {
                MessageObject message = cell.getMessageObject();
                PhotoViewer.getInstance().setParentActivity(ChannelAdminLogActivity.this.getParentActivity());
                PhotoSize photoSize = FileLoader.getClosestPhotoSizeWithSize(message.photoThumbs, 640);
                if (photoSize != null) {
                    PhotoViewer.getInstance().openPhoto(photoSize.location, ChannelAdminLogActivity.this.provider);
                    return;
                }
                PhotoViewer.getInstance().openPhoto(message, 0, 0, ChannelAdminLogActivity.this.provider);
            }

            public void didLongPressed(ChatActionCell cell) {
                ChannelAdminLogActivity.this.createMenu(cell);
            }

            public void needOpenUserProfile(int uid) {
                Bundle args;
                if (uid < 0) {
                    args = new Bundle();
                    args.putInt("chat_id", -uid);
                    if (MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).checkCanOpenChat(args, ChannelAdminLogActivity.this)) {
                        ChannelAdminLogActivity.this.presentFragment(new ChatActivity(args), true);
                    }
                } else if (uid != UserConfig.getInstance(ChannelAdminLogActivity.this.currentAccount).getClientUserId()) {
                    args = new Bundle();
                    args.putInt("user_id", uid);
                    ChannelAdminLogActivity.this.addCanBanUser(args, uid);
                    ProfileActivity fragment = new ProfileActivity(args);
                    fragment.setPlayProfileAnimation(false);
                    ChannelAdminLogActivity.this.presentFragment(fragment);
                }
            }

            public void didPressedReplyMessage(ChatActionCell cell, int id) {
            }

            public void didPressedBotButton(MessageObject messageObject, KeyboardButton button) {
            }
        }

        /* renamed from: org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter$3 */
        class C19713 implements BotHelpCellDelegate {
            C19713() {
            }

            public void didPressUrl(String url) {
                if (url.startsWith("@")) {
                    MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).openByUserName(url.substring(1), ChannelAdminLogActivity.this, 0);
                } else if (url.startsWith("#")) {
                    DialogsActivity fragment = new DialogsActivity(null);
                    fragment.setSearchString(url);
                    ChannelAdminLogActivity.this.presentFragment(fragment);
                }
            }
        }

        public ChatActivityAdapter(Context context) {
            this.mContext = context;
        }

        public void updateRows() {
            this.rowCount = 0;
            if (ChannelAdminLogActivity.this.messages.isEmpty()) {
                this.loadingUpRow = -1;
                this.messagesStartRow = -1;
                this.messagesEndRow = -1;
                return;
            }
            if (ChannelAdminLogActivity.this.endReached) {
                this.loadingUpRow = -1;
            } else {
                int i = this.rowCount;
                this.rowCount = i + 1;
                this.loadingUpRow = i;
            }
            this.messagesStartRow = this.rowCount;
            this.rowCount += ChannelAdminLogActivity.this.messages.size();
            this.messagesEndRow = this.rowCount;
        }

        public int getItemCount() {
            return this.rowCount;
        }

        public long getItemId(int i) {
            return -1;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            if (viewType == 0) {
                if (ChannelAdminLogActivity.this.chatMessageCellsCache.isEmpty()) {
                    view = new ChatMessageCell(this.mContext);
                } else {
                    view = (View) ChannelAdminLogActivity.this.chatMessageCellsCache.get(0);
                    ChannelAdminLogActivity.this.chatMessageCellsCache.remove(0);
                }
                ChatMessageCell chatMessageCell = (ChatMessageCell) view;
                chatMessageCell.setDelegate(new C19691());
                chatMessageCell.setAllowAssistant(true);
            } else if (viewType == 1) {
                view = new ChatActionCell(this.mContext);
                ((ChatActionCell) view).setDelegate(new C19702());
            } else if (viewType == 2) {
                view = new ChatUnreadCell(this.mContext);
            } else if (viewType == 3) {
                view = new BotHelpCell(this.mContext);
                ((BotHelpCell) view).setDelegate(new C19713());
            } else if (viewType == 4) {
                view = new ChatLoadingCell(this.mContext);
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            boolean z = false;
            boolean z2 = true;
            if (position == this.loadingUpRow) {
                ChatLoadingCell loadingCell = holder.itemView;
                if (ChannelAdminLogActivity.this.loadsCount > 1) {
                    z = true;
                }
                loadingCell.setProgressVisible(z);
            } else if (position >= this.messagesStartRow && position < this.messagesEndRow) {
                MessageObject message = (MessageObject) ChannelAdminLogActivity.this.messages.get((ChannelAdminLogActivity.this.messages.size() - (position - this.messagesStartRow)) - 1);
                View view = holder.itemView;
                if (view instanceof ChatMessageCell) {
                    boolean pinnedBotton;
                    ChatMessageCell messageCell = (ChatMessageCell) view;
                    messageCell.isChat = true;
                    int nextType = getItemViewType(position + 1);
                    int prevType = getItemViewType(position - 1);
                    if ((message.messageOwner.reply_markup instanceof TL_replyInlineMarkup) || nextType != holder.getItemViewType()) {
                        pinnedBotton = false;
                    } else {
                        MessageObject nextMessage = (MessageObject) ChannelAdminLogActivity.this.messages.get((ChannelAdminLogActivity.this.messages.size() - ((position + 1) - this.messagesStartRow)) - 1);
                        boolean z3 = nextMessage.isOutOwner() == message.isOutOwner() && nextMessage.messageOwner.from_id == message.messageOwner.from_id && Math.abs(nextMessage.messageOwner.date - message.messageOwner.date) <= 300;
                        pinnedBotton = z3;
                    }
                    if (prevType == holder.getItemViewType()) {
                        MessageObject prevMessage = (MessageObject) ChannelAdminLogActivity.this.messages.get(ChannelAdminLogActivity.this.messages.size() - (position - this.messagesStartRow));
                        if ((prevMessage.messageOwner.reply_markup instanceof TL_replyInlineMarkup) || prevMessage.isOutOwner() != message.isOutOwner() || prevMessage.messageOwner.from_id != message.messageOwner.from_id || Math.abs(prevMessage.messageOwner.date - message.messageOwner.date) > 300) {
                            z2 = false;
                        }
                    } else {
                        z2 = false;
                    }
                    messageCell.setMessageObject(message, null, pinnedBotton, z2);
                    if ((view instanceof ChatMessageCell) && DownloadController.getInstance(ChannelAdminLogActivity.this.currentAccount).canDownloadMedia(message)) {
                        ((ChatMessageCell) view).downloadAudioIfNeed();
                    }
                    messageCell.setHighlighted(false);
                    messageCell.setHighlightedText(null);
                } else if (view instanceof ChatActionCell) {
                    ChatActionCell actionCell = (ChatActionCell) view;
                    actionCell.setMessageObject(message);
                    actionCell.setAlpha(1.0f);
                }
            }
        }

        public int getItemViewType(int position) {
            if (position < this.messagesStartRow || position >= this.messagesEndRow) {
                return 4;
            }
            return ((MessageObject) ChannelAdminLogActivity.this.messages.get((ChannelAdminLogActivity.this.messages.size() - (position - this.messagesStartRow)) - 1)).contentType;
        }

        public void onViewAttachedToWindow(ViewHolder holder) {
            if (holder.itemView instanceof ChatMessageCell) {
                final ChatMessageCell messageCell = holder.itemView;
                MessageObject message = messageCell.getMessageObject();
                messageCell.setBackgroundDrawable(null);
                boolean z = true;
                boolean z2 = null == null;
                if (null == null || !false) {
                    z = false;
                }
                messageCell.setCheckPressed(z2, z);
                messageCell.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                    public boolean onPreDraw() {
                        messageCell.getViewTreeObserver().removeOnPreDrawListener(this);
                        int height = ChannelAdminLogActivity.this.chatListView.getMeasuredHeight();
                        int top = messageCell.getTop();
                        int bottom = messageCell.getBottom();
                        int viewTop = top >= 0 ? 0 : -top;
                        int viewBottom = messageCell.getMeasuredHeight();
                        if (viewBottom > height) {
                            viewBottom = viewTop + height;
                        }
                        messageCell.setVisiblePart(viewTop, viewBottom - viewTop);
                        return true;
                    }
                });
                messageCell.setHighlighted(false);
            }
        }

        public void updateRowWithMessageObject(MessageObject messageObject) {
            int index = ChannelAdminLogActivity.this.messages.indexOf(messageObject);
            if (index != -1) {
                notifyItemChanged(((this.messagesStartRow + ChannelAdminLogActivity.this.messages.size()) - index) - 1);
            }
        }

        public void notifyDataSetChanged() {
            updateRows();
            try {
                super.notifyDataSetChanged();
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }

        public void notifyItemChanged(int position) {
            updateRows();
            try {
                super.notifyItemChanged(position);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }

        public void notifyItemRangeChanged(int positionStart, int itemCount) {
            updateRows();
            try {
                super.notifyItemRangeChanged(positionStart, itemCount);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }

        public void notifyItemInserted(int position) {
            updateRows();
            try {
                super.notifyItemInserted(position);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }

        public void notifyItemMoved(int fromPosition, int toPosition) {
            updateRows();
            try {
                super.notifyItemMoved(fromPosition, toPosition);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }

        public void notifyItemRangeInserted(int positionStart, int itemCount) {
            updateRows();
            try {
                super.notifyItemRangeInserted(positionStart, itemCount);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }

        public void notifyItemRemoved(int position) {
            updateRows();
            try {
                super.notifyItemRemoved(position);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }

        public void notifyItemRangeRemoved(int positionStart, int itemCount) {
            updateRows();
            try {
                super.notifyItemRangeRemoved(positionStart, itemCount);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
    }

    /* renamed from: org.telegram.ui.ChannelAdminLogActivity$1 */
    class C23291 extends EmptyPhotoViewerProvider {
        C23291() {
        }

        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
            FileLocation fileLocation2 = fileLocation;
            int count = ChannelAdminLogActivity.this.chatListView.getChildCount();
            int i = 0;
            for (int a = 0; a < count; a++) {
                ImageReceiver imageReceiver = null;
                View view = ChannelAdminLogActivity.this.chatListView.getChildAt(a);
                MessageObject message;
                if (view instanceof ChatMessageCell) {
                    if (messageObject != null) {
                        ChatMessageCell cell = (ChatMessageCell) view;
                        message = cell.getMessageObject();
                        if (message != null && message.getId() == messageObject.getId()) {
                            imageReceiver = cell.getPhotoImage();
                        }
                    }
                } else if (view instanceof ChatActionCell) {
                    ChatActionCell cell2 = (ChatActionCell) view;
                    message = cell2.getMessageObject();
                    if (message != null) {
                        if (messageObject != null) {
                            if (message.getId() == messageObject.getId()) {
                                imageReceiver = cell2.getPhotoImage();
                            }
                        } else if (fileLocation2 != null && message.photoThumbs != null) {
                            for (int b = 0; b < message.photoThumbs.size(); b++) {
                                PhotoSize photoSize = (PhotoSize) message.photoThumbs.get(b);
                                if (photoSize.location.volume_id == fileLocation2.volume_id && photoSize.location.local_id == fileLocation2.local_id) {
                                    imageReceiver = cell2.getPhotoImage();
                                    break;
                                }
                            }
                        }
                    }
                }
                if (imageReceiver != null) {
                    int[] coords = new int[2];
                    view.getLocationInWindow(coords);
                    PlaceProviderObject object = new PlaceProviderObject();
                    object.viewX = coords[0];
                    int i2 = coords[1];
                    if (VERSION.SDK_INT < 21) {
                        i = AndroidUtilities.statusBarHeight;
                    }
                    object.viewY = i2 - i;
                    object.parentView = ChannelAdminLogActivity.this.chatListView;
                    object.imageReceiver = imageReceiver;
                    object.thumb = imageReceiver.getBitmapSafe();
                    object.radius = imageReceiver.getRoundRadius();
                    object.isEvent = true;
                    return object;
                }
            }
            return null;
        }
    }

    public ChannelAdminLogActivity(Chat chat) {
        this.currentChat = chat;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidStarted);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
        loadMessages(true);
        loadAdmins();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidStarted);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
    }

    private void updateEmptyPlaceholder() {
        if (this.emptyView != null) {
            if (TextUtils.isEmpty(this.searchQuery)) {
                if (this.selectedAdmins == null) {
                    if (this.currentFilter == null) {
                        this.emptyView.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
                        if (this.currentChat.megagroup) {
                            this.emptyView.setText(AndroidUtilities.replaceTags(LocaleController.getString("EventLogEmpty", R.string.EventLogEmpty)));
                        } else {
                            this.emptyView.setText(AndroidUtilities.replaceTags(LocaleController.getString("EventLogEmptyChannel", R.string.EventLogEmptyChannel)));
                        }
                    }
                }
                this.emptyView.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(5.0f));
                this.emptyView.setText(AndroidUtilities.replaceTags(LocaleController.getString("EventLogEmptySearch", R.string.EventLogEmptySearch)));
            } else {
                this.emptyView.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(5.0f));
                this.emptyView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("EventLogEmptyTextSearch", R.string.EventLogEmptyTextSearch, this.searchQuery)));
            }
        }
    }

    private void loadMessages(boolean reset) {
        if (!this.loading) {
            int a = 0;
            if (reset) {
                this.minEventId = Long.MAX_VALUE;
                if (this.progressView != null) {
                    this.progressView.setVisibility(0);
                    this.emptyViewContainer.setVisibility(4);
                    this.chatListView.setEmptyView(null);
                }
                this.messagesDict.clear();
                this.messages.clear();
                this.messagesByDays.clear();
            }
            this.loading = true;
            TL_channels_getAdminLog req = new TL_channels_getAdminLog();
            req.channel = MessagesController.getInputChannel(this.currentChat);
            req.f44q = this.searchQuery;
            req.limit = 50;
            if (reset || this.messages.isEmpty()) {
                req.max_id = 0;
            } else {
                req.max_id = this.minEventId;
            }
            req.min_id = 0;
            if (this.currentFilter != null) {
                req.flags = 1 | req.flags;
                req.events_filter = this.currentFilter;
            }
            if (this.selectedAdmins != null) {
                req.flags |= 2;
                while (a < this.selectedAdmins.size()) {
                    req.admins.add(MessagesController.getInstance(this.currentAccount).getInputUser((User) this.selectedAdmins.valueAt(a)));
                    a++;
                }
            }
            updateEmptyPlaceholder();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new C19642());
            if (reset && this.chatAdapter != null) {
                this.chatAdapter.notifyDataSetChanged();
            }
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id != NotificationCenter.emojiDidLoaded) {
            int a = 0;
            int count;
            int a2;
            View view;
            ChatMessageCell cell;
            MessageObject messageObject1;
            if (id == NotificationCenter.messagePlayingDidStarted) {
                if (args[0].isRoundVideo()) {
                    MediaController.getInstance().setTextureView(createTextureView(true), this.aspectRatioFrameLayout, this.roundVideoContainer, true);
                    updateTextureViewPosition();
                }
                if (this.chatListView != null) {
                    count = this.chatListView.getChildCount();
                    for (a2 = 0; a2 < count; a2++) {
                        view = this.chatListView.getChildAt(a2);
                        if (view instanceof ChatMessageCell) {
                            cell = (ChatMessageCell) view;
                            messageObject1 = cell.getMessageObject();
                            if (messageObject1 != null) {
                                if (!messageObject1.isVoice()) {
                                    if (!messageObject1.isMusic()) {
                                        if (messageObject1.isRoundVideo()) {
                                            cell.checkRoundVideoPlayback(false);
                                            if (!(MediaController.getInstance().isPlayingMessage(messageObject1) || messageObject1.audioProgress == 0.0f)) {
                                                messageObject1.resetPlayingProgress();
                                                cell.invalidate();
                                            }
                                        }
                                    }
                                }
                                cell.updateButtonState(false);
                            }
                        }
                    }
                }
                return;
            }
            if (id != NotificationCenter.messagePlayingDidReset) {
                if (id != NotificationCenter.messagePlayingPlayStateChanged) {
                    if (id == NotificationCenter.messagePlayingProgressDidChanged) {
                        Integer mid = args[0];
                        if (this.chatListView != null) {
                            count = this.chatListView.getChildCount();
                            while (a < count) {
                                View view2 = this.chatListView.getChildAt(a);
                                if (view2 instanceof ChatMessageCell) {
                                    ChatMessageCell cell2 = (ChatMessageCell) view2;
                                    MessageObject playing = cell2.getMessageObject();
                                    if (playing != null && playing.getId() == mid.intValue()) {
                                        messageObject1 = MediaController.getInstance().getPlayingMessageObject();
                                        if (messageObject1 != null) {
                                            playing.audioProgress = messageObject1.audioProgress;
                                            playing.audioProgressSec = messageObject1.audioProgressSec;
                                            playing.audioPlayerDuration = messageObject1.audioPlayerDuration;
                                            cell2.updatePlayingMessageProgress();
                                        }
                                    }
                                }
                                a++;
                            }
                        }
                        return;
                    } else if (id == NotificationCenter.didSetNewWallpapper && this.fragmentView != null) {
                        ((SizeNotifierFrameLayout) this.fragmentView).setBackgroundImage(Theme.getCachedWallpaper());
                        this.progressView2.getBackground().setColorFilter(Theme.colorFilter);
                        if (this.emptyView != null) {
                            this.emptyView.getBackground().setColorFilter(Theme.colorFilter);
                        }
                        this.chatListView.invalidateViews();
                        return;
                    } else {
                        return;
                    }
                }
            }
            if (this.chatListView != null) {
                int count2 = this.chatListView.getChildCount();
                for (a2 = 0; a2 < count2; a2++) {
                    view = this.chatListView.getChildAt(a2);
                    if (view instanceof ChatMessageCell) {
                        cell = (ChatMessageCell) view;
                        messageObject1 = cell.getMessageObject();
                        if (messageObject1 != null) {
                            if (!messageObject1.isVoice()) {
                                if (!messageObject1.isMusic()) {
                                    if (messageObject1.isRoundVideo() && !MediaController.getInstance().isPlayingMessage(messageObject1)) {
                                        cell.checkRoundVideoPlayback(true);
                                    }
                                }
                            }
                            cell.updateButtonState(false);
                        }
                    }
                }
            }
        } else if (this.chatListView != null) {
            this.chatListView.invalidateViews();
        }
    }

    private void updateBottomOverlay() {
    }

    public View createView(Context context) {
        Context context2 = context;
        if (this.chatMessageCellsCache.isEmpty()) {
            for (int a = 0; a < 8; a++) {
                r0.chatMessageCellsCache.add(new ChatMessageCell(context2));
            }
        }
        r0.searchWas = false;
        r0.hasOwnBackground = true;
        Theme.createChatResources(context2, false);
        r0.actionBar.setAddToContainer(false);
        ActionBar actionBar = r0.actionBar;
        boolean z = VERSION.SDK_INT >= 21 && !AndroidUtilities.isTablet();
        actionBar.setOccupyStatusBar(z);
        r0.actionBar.setBackButtonDrawable(new BackDrawable(false));
        r0.actionBar.setActionBarMenuOnItemClick(new C19653());
        r0.avatarContainer = new ChatAvatarContainer(context2, null, false);
        r0.avatarContainer.setOccupyStatusBar(AndroidUtilities.isTablet() ^ true);
        r0.actionBar.addView(r0.avatarContainer, 0, LayoutHelper.createFrame(-2, -1.0f, 51, 56.0f, 0.0f, 40.0f, 0.0f));
        r0.searchItem = r0.actionBar.createMenu().addItem(0, (int) R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new C19664());
        r0.searchItem.getSearchField().setHint(LocaleController.getString("Search", R.string.Search));
        r0.avatarContainer.setEnabled(false);
        r0.avatarContainer.setTitle(r0.currentChat.title);
        r0.avatarContainer.setSubtitle(LocaleController.getString("EventLogAllEvents", R.string.EventLogAllEvents));
        r0.avatarContainer.setChatAvatar(r0.currentChat);
        r0.fragmentView = new SizeNotifierFrameLayout(context2) {
            protected void onAttachedToWindow() {
                super.onAttachedToWindow();
                MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
                if (messageObject != null && messageObject.isRoundVideo() && messageObject.eventId != 0 && messageObject.getDialogId() == ((long) (-ChannelAdminLogActivity.this.currentChat.id))) {
                    MediaController.getInstance().setTextureView(ChannelAdminLogActivity.this.createTextureView(false), ChannelAdminLogActivity.this.aspectRatioFrameLayout, ChannelAdminLogActivity.this.roundVideoContainer, true);
                }
            }

            protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                boolean result = super.drawChild(canvas, child, drawingTime);
                if (child == ChannelAdminLogActivity.this.actionBar && ChannelAdminLogActivity.this.parentLayout != null) {
                    ChannelAdminLogActivity.this.parentLayout.drawHeaderShadow(canvas, ChannelAdminLogActivity.this.actionBar.getVisibility() == 0 ? ChannelAdminLogActivity.this.actionBar.getMeasuredHeight() : 0);
                }
                return result;
            }

            protected boolean isActionBarVisible() {
                return ChannelAdminLogActivity.this.actionBar.getVisibility() == 0;
            }

            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int widthSize = MeasureSpec.getSize(widthMeasureSpec);
                int heightSize = MeasureSpec.getSize(heightMeasureSpec);
                setMeasuredDimension(widthSize, heightSize);
                heightSize -= getPaddingTop();
                measureChildWithMargins(ChannelAdminLogActivity.this.actionBar, widthMeasureSpec, 0, heightMeasureSpec, 0);
                int actionBarHeight = ChannelAdminLogActivity.this.actionBar.getMeasuredHeight();
                if (ChannelAdminLogActivity.this.actionBar.getVisibility() == 0) {
                    heightSize -= actionBarHeight;
                }
                int keyboardSize = getKeyboardHeight();
                int childCount = getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = getChildAt(i);
                    if (!(child == null || child.getVisibility() == 8)) {
                        if (child != ChannelAdminLogActivity.this.actionBar) {
                            if (child != ChannelAdminLogActivity.this.chatListView) {
                                if (child != ChannelAdminLogActivity.this.progressView) {
                                    if (child == ChannelAdminLogActivity.this.emptyViewContainer) {
                                        child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(heightSize, NUM));
                                    } else {
                                        measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                                    }
                                }
                            }
                            child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), heightSize - AndroidUtilities.dp(50.0f)), NUM));
                        }
                    }
                }
            }

            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                C19675 c19675 = this;
                int count = getChildCount();
                for (int i = 0; i < count; i++) {
                    View child = getChildAt(i);
                    if (child.getVisibility() != 8) {
                        int childLeft;
                        int childTop;
                        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
                        int width = child.getMeasuredWidth();
                        int height = child.getMeasuredHeight();
                        int gravity = lp.gravity;
                        if (gravity == -1) {
                            gravity = 51;
                        }
                        int verticalGravity = gravity & 112;
                        int i2 = (gravity & 7) & 7;
                        if (i2 != 1) {
                            if (i2 != 5) {
                                i2 = lp.leftMargin;
                            } else {
                                i2 = (r - width) - lp.rightMargin;
                            }
                            childLeft = i2;
                        } else {
                            childLeft = ((((r - l) - width) / 2) + lp.leftMargin) - lp.rightMargin;
                        }
                        if (verticalGravity == 16) {
                            childTop = ((((b - t) - height) / 2) + lp.topMargin) - lp.bottomMargin;
                        } else if (verticalGravity != 48) {
                            childTop = verticalGravity != 80 ? lp.topMargin : ((b - t) - height) - lp.bottomMargin;
                        } else {
                            childTop = getPaddingTop() + lp.topMargin;
                            if (child != ChannelAdminLogActivity.this.actionBar && ChannelAdminLogActivity.this.actionBar.getVisibility() == 0) {
                                childTop += ChannelAdminLogActivity.this.actionBar.getMeasuredHeight();
                            }
                        }
                        int childTop2 = childTop;
                        if (child == ChannelAdminLogActivity.this.emptyViewContainer) {
                            childTop2 -= AndroidUtilities.dp(24.0f) - (ChannelAdminLogActivity.this.actionBar.getVisibility() == 0 ? ChannelAdminLogActivity.this.actionBar.getMeasuredHeight() / 2 : 0);
                        } else if (child == ChannelAdminLogActivity.this.actionBar) {
                            childTop2 -= getPaddingTop();
                        }
                        child.layout(childLeft, childTop2, childLeft + width, childTop2 + height);
                    }
                }
                ChannelAdminLogActivity.this.updateMessagesVisisblePart();
                notifyHeightChanged();
            }
        };
        r0.contentView = (SizeNotifierFrameLayout) r0.fragmentView;
        r0.contentView.setOccupyStatusBar(AndroidUtilities.isTablet() ^ true);
        r0.contentView.setBackgroundImage(Theme.getCachedWallpaper());
        r0.emptyViewContainer = new FrameLayout(context2);
        r0.emptyViewContainer.setVisibility(4);
        r0.contentView.addView(r0.emptyViewContainer, LayoutHelper.createFrame(-1, -2, 17));
        r0.emptyViewContainer.setOnTouchListener(new C09556());
        r0.emptyView = new TextView(context2);
        r0.emptyView.setTextSize(1, 14.0f);
        r0.emptyView.setGravity(17);
        r0.emptyView.setTextColor(Theme.getColor(Theme.key_chat_serviceText));
        r0.emptyView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(10.0f), Theme.getServiceMessageColor()));
        r0.emptyView.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
        r0.emptyViewContainer.addView(r0.emptyView, LayoutHelper.createFrame(-2, -2.0f, 17, 16.0f, 0.0f, 16.0f, 0.0f));
        r0.chatListView = new RecyclerListView(context2) {
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                boolean result = super.drawChild(canvas, child, drawingTime);
                if (child instanceof ChatMessageCell) {
                    ChatMessageCell chatMessageCell = (ChatMessageCell) child;
                    ImageReceiver imageReceiver = chatMessageCell.getAvatarImage();
                    if (imageReceiver != null) {
                        ViewHolder holder;
                        int top = child.getTop();
                        if (chatMessageCell.isPinnedBottom()) {
                            holder = ChannelAdminLogActivity.this.chatListView.getChildViewHolder(child);
                            if (!(holder == null || ChannelAdminLogActivity.this.chatListView.findViewHolderForAdapterPosition(holder.getAdapterPosition() + 1) == null)) {
                                imageReceiver.setImageY(-AndroidUtilities.dp(1000.0f));
                                imageReceiver.draw(canvas);
                                return result;
                            }
                        }
                        if (chatMessageCell.isPinnedTop()) {
                            holder = ChannelAdminLogActivity.this.chatListView.getChildViewHolder(child);
                            if (holder != null) {
                                do {
                                    holder = ChannelAdminLogActivity.this.chatListView.findViewHolderForAdapterPosition(holder.getAdapterPosition() - 1);
                                    if (holder == null) {
                                        break;
                                    }
                                    top = holder.itemView.getTop();
                                    if (!(holder.itemView instanceof ChatMessageCell)) {
                                        break;
                                    }
                                } while (((ChatMessageCell) holder.itemView).isPinnedTop());
                            }
                        }
                        int y = child.getTop() + chatMessageCell.getLayoutHeight();
                        int maxY = ChannelAdminLogActivity.this.chatListView.getHeight() - ChannelAdminLogActivity.this.chatListView.getPaddingBottom();
                        if (y > maxY) {
                            y = maxY;
                        }
                        if (y - AndroidUtilities.dp(48.0f) < top) {
                            y = top + AndroidUtilities.dp(48.0f);
                        }
                        imageReceiver.setImageY(y - AndroidUtilities.dp(44.0f));
                        imageReceiver.draw(canvas);
                    }
                }
                return result;
            }
        };
        r0.chatListView.setOnItemClickListener(new C19688());
        r0.chatListView.setTag(Integer.valueOf(1));
        r0.chatListView.setVerticalScrollBarEnabled(true);
        RecyclerListView recyclerListView = r0.chatListView;
        Adapter chatActivityAdapter = new ChatActivityAdapter(context2);
        r0.chatAdapter = chatActivityAdapter;
        recyclerListView.setAdapter(chatActivityAdapter);
        r0.chatListView.setClipToPadding(false);
        r0.chatListView.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(3.0f));
        r0.chatListView.setItemAnimator(null);
        r0.chatListView.setLayoutAnimation(null);
        r0.chatLayoutManager = new LinearLayoutManager(context2) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }

            public void smoothScrollToPosition(RecyclerView recyclerView, State state, int position) {
                LinearSmoothScrollerMiddle linearSmoothScroller = new LinearSmoothScrollerMiddle(recyclerView.getContext());
                linearSmoothScroller.setTargetPosition(position);
                startSmoothScroll(linearSmoothScroller);
            }
        };
        r0.chatLayoutManager.setOrientation(1);
        r0.chatLayoutManager.setStackFromEnd(true);
        r0.chatListView.setLayoutManager(r0.chatLayoutManager);
        r0.contentView.addView(r0.chatListView, LayoutHelper.createFrame(-1, -1.0f));
        r0.chatListView.setOnScrollListener(new OnScrollListener() {
            private final int scrollValue = AndroidUtilities.dp(100.0f);
            private float totalDy = 0.0f;

            /* renamed from: org.telegram.ui.ChannelAdminLogActivity$10$1 */
            class C09491 extends AnimatorListenerAdapter {
                C09491() {
                }

                public void onAnimationEnd(Animator animation) {
                    if (animation.equals(ChannelAdminLogActivity.this.floatingDateAnimation)) {
                        ChannelAdminLogActivity.this.floatingDateAnimation = null;
                    }
                }
            }

            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    ChannelAdminLogActivity.this.scrollingFloatingDate = true;
                    ChannelAdminLogActivity.this.checkTextureViewPosition = true;
                } else if (newState == 0) {
                    ChannelAdminLogActivity.this.scrollingFloatingDate = false;
                    ChannelAdminLogActivity.this.checkTextureViewPosition = false;
                    ChannelAdminLogActivity.this.hideFloatingDateView(true);
                }
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                ChannelAdminLogActivity.this.chatListView.invalidate();
                if (dy != 0 && ChannelAdminLogActivity.this.scrollingFloatingDate && !ChannelAdminLogActivity.this.currentFloatingTopIsNotMessage && ChannelAdminLogActivity.this.floatingDateView.getTag() == null) {
                    if (ChannelAdminLogActivity.this.floatingDateAnimation != null) {
                        ChannelAdminLogActivity.this.floatingDateAnimation.cancel();
                    }
                    ChannelAdminLogActivity.this.floatingDateView.setTag(Integer.valueOf(1));
                    ChannelAdminLogActivity.this.floatingDateAnimation = new AnimatorSet();
                    ChannelAdminLogActivity.this.floatingDateAnimation.setDuration(150);
                    AnimatorSet access$4500 = ChannelAdminLogActivity.this.floatingDateAnimation;
                    Animator[] animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(ChannelAdminLogActivity.this.floatingDateView, "alpha", new float[]{1.0f});
                    access$4500.playTogether(animatorArr);
                    ChannelAdminLogActivity.this.floatingDateAnimation.addListener(new C09491());
                    ChannelAdminLogActivity.this.floatingDateAnimation.start();
                }
                ChannelAdminLogActivity.this.checkScrollForLoad(true);
                ChannelAdminLogActivity.this.updateMessagesVisisblePart();
            }
        });
        if (r0.scrollToPositionOnRecreate != -1) {
            r0.chatLayoutManager.scrollToPositionWithOffset(r0.scrollToPositionOnRecreate, r0.scrollToOffsetOnRecreate);
            r0.scrollToPositionOnRecreate = -1;
        }
        r0.progressView = new FrameLayout(context2);
        r0.progressView.setVisibility(4);
        r0.contentView.addView(r0.progressView, LayoutHelper.createFrame(-1, -1, 51));
        r0.progressView2 = new View(context2);
        r0.progressView2.setBackgroundResource(R.drawable.system_loader);
        r0.progressView2.getBackground().setColorFilter(Theme.colorFilter);
        r0.progressView.addView(r0.progressView2, LayoutHelper.createFrame(36, 36, 17));
        r0.progressBar = new RadialProgressView(context2);
        r0.progressBar.setSize(AndroidUtilities.dp(28.0f));
        r0.progressBar.setProgressColor(Theme.getColor(Theme.key_chat_serviceText));
        r0.progressView.addView(r0.progressBar, LayoutHelper.createFrame(32, 32, 17));
        r0.floatingDateView = new ChatActionCell(context2);
        r0.floatingDateView.setAlpha(0.0f);
        r0.contentView.addView(r0.floatingDateView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 4.0f, 0.0f, 0.0f));
        r0.contentView.addView(r0.actionBar);
        r0.bottomOverlayChat = new FrameLayout(context2) {
            public void onDraw(Canvas canvas) {
                int bottom = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), bottom);
                Theme.chat_composeShadowDrawable.draw(canvas);
                canvas.drawRect(0.0f, (float) bottom, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
            }
        };
        r0.bottomOverlayChat.setWillNotDraw(false);
        r0.bottomOverlayChat.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
        r0.contentView.addView(r0.bottomOverlayChat, LayoutHelper.createFrame(-1, 51, 80));
        r0.bottomOverlayChat.setOnClickListener(new View.OnClickListener() {

            /* renamed from: org.telegram.ui.ChannelAdminLogActivity$12$1 */
            class C19631 implements AdminLogFilterAlertDelegate {
                C19631() {
                }

                public void didSelectRights(TL_channelAdminLogEventsFilter filter, SparseArray<User> admins) {
                    ChannelAdminLogActivity.this.currentFilter = filter;
                    ChannelAdminLogActivity.this.selectedAdmins = admins;
                    if (ChannelAdminLogActivity.this.currentFilter == null) {
                        if (ChannelAdminLogActivity.this.selectedAdmins == null) {
                            ChannelAdminLogActivity.this.avatarContainer.setSubtitle(LocaleController.getString("EventLogAllEvents", R.string.EventLogAllEvents));
                            ChannelAdminLogActivity.this.loadMessages(true);
                        }
                    }
                    ChannelAdminLogActivity.this.avatarContainer.setSubtitle(LocaleController.getString("EventLogSelectedEvents", R.string.EventLogSelectedEvents));
                    ChannelAdminLogActivity.this.loadMessages(true);
                }
            }

            public void onClick(View view) {
                if (ChannelAdminLogActivity.this.getParentActivity() != null) {
                    AdminLogFilterAlert adminLogFilterAlert = new AdminLogFilterAlert(ChannelAdminLogActivity.this.getParentActivity(), ChannelAdminLogActivity.this.currentFilter, ChannelAdminLogActivity.this.selectedAdmins, ChannelAdminLogActivity.this.currentChat.megagroup);
                    adminLogFilterAlert.setCurrentAdmins(ChannelAdminLogActivity.this.admins);
                    adminLogFilterAlert.setAdminLogFilterAlertDelegate(new C19631());
                    ChannelAdminLogActivity.this.showDialog(adminLogFilterAlert);
                }
            }
        });
        r0.bottomOverlayChatText = new TextView(context2);
        r0.bottomOverlayChatText.setTextSize(1, 15.0f);
        r0.bottomOverlayChatText.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r0.bottomOverlayChatText.setTextColor(Theme.getColor(Theme.key_chat_fieldOverlayText));
        r0.bottomOverlayChatText.setText(LocaleController.getString("SETTINGS", R.string.SETTINGS).toUpperCase());
        r0.bottomOverlayChat.addView(r0.bottomOverlayChatText, LayoutHelper.createFrame(-2, -2, 17));
        r0.bottomOverlayImage = new ImageView(context2);
        r0.bottomOverlayImage.setImageResource(R.drawable.log_info);
        r0.bottomOverlayImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_fieldOverlayText), Mode.MULTIPLY));
        r0.bottomOverlayImage.setScaleType(ScaleType.CENTER);
        r0.bottomOverlayChat.addView(r0.bottomOverlayImage, LayoutHelper.createFrame(48, 48.0f, 53, 3.0f, 0.0f, 0.0f, 0.0f));
        r0.bottomOverlayImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChannelAdminLogActivity.this.getParentActivity());
                if (ChannelAdminLogActivity.this.currentChat.megagroup) {
                    builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("EventLogInfoDetail", R.string.EventLogInfoDetail)));
                } else {
                    builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("EventLogInfoDetailChannel", R.string.EventLogInfoDetailChannel)));
                }
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                builder.setTitle(LocaleController.getString("EventLogInfoTitle", R.string.EventLogInfoTitle));
                ChannelAdminLogActivity.this.showDialog(builder.create());
            }
        });
        r0.searchContainer = new FrameLayout(context2) {
            public void onDraw(Canvas canvas) {
                int bottom = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), bottom);
                Theme.chat_composeShadowDrawable.draw(canvas);
                canvas.drawRect(0.0f, (float) bottom, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
            }
        };
        r0.searchContainer.setWillNotDraw(false);
        r0.searchContainer.setVisibility(4);
        r0.searchContainer.setFocusable(true);
        r0.searchContainer.setFocusableInTouchMode(true);
        r0.searchContainer.setClickable(true);
        r0.searchContainer.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
        r0.contentView.addView(r0.searchContainer, LayoutHelper.createFrame(-1, 51, 80));
        r0.searchCalendarButton = new ImageView(context2);
        r0.searchCalendarButton.setScaleType(ScaleType.CENTER);
        r0.searchCalendarButton.setImageResource(R.drawable.search_calendar);
        r0.searchCalendarButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_searchPanelIcons), Mode.MULTIPLY));
        r0.searchContainer.addView(r0.searchCalendarButton, LayoutHelper.createFrame(48, 48, 53));
        r0.searchCalendarButton.setOnClickListener(new View.OnClickListener() {

            /* renamed from: org.telegram.ui.ChannelAdminLogActivity$15$1 */
            class C09501 implements OnDateSetListener {
                C09501() {
                }

                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.clear();
                    calendar.set(year, month, dayOfMonth);
                    int date = (int) (calendar.getTime().getTime() / 1000);
                    ChannelAdminLogActivity.this.loadMessages(true);
                }
            }

            /* renamed from: org.telegram.ui.ChannelAdminLogActivity$15$2 */
            class C09512 implements OnClickListener {
                C09512() {
                }

                public void onClick(DialogInterface dialog, int which) {
                }
            }

            public void onClick(View view) {
                if (ChannelAdminLogActivity.this.getParentActivity() != null) {
                    AndroidUtilities.hideKeyboard(ChannelAdminLogActivity.this.searchItem.getSearchField());
                    Calendar calendar = Calendar.getInstance();
                    try {
                        DatePickerDialog dialog = new DatePickerDialog(ChannelAdminLogActivity.this.getParentActivity(), new C09501(), calendar.get(1), calendar.get(2), calendar.get(5));
                        final DatePicker datePicker = dialog.getDatePicker();
                        datePicker.setMinDate(1375315200000L);
                        datePicker.setMaxDate(System.currentTimeMillis());
                        dialog.setButton(-1, LocaleController.getString("JumpToDate", R.string.JumpToDate), dialog);
                        dialog.setButton(-2, LocaleController.getString("Cancel", R.string.Cancel), new C09512());
                        if (VERSION.SDK_INT >= 21) {
                            dialog.setOnShowListener(new OnShowListener() {
                                public void onShow(DialogInterface dialog) {
                                    int count = datePicker.getChildCount();
                                    for (int a = 0; a < count; a++) {
                                        View child = datePicker.getChildAt(a);
                                        ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
                                        layoutParams.width = -1;
                                        child.setLayoutParams(layoutParams);
                                    }
                                }
                            });
                        }
                        ChannelAdminLogActivity.this.showDialog(dialog);
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
            }
        });
        r0.searchCountText = new SimpleTextView(context2);
        r0.searchCountText.setTextColor(Theme.getColor(Theme.key_chat_searchPanelText));
        r0.searchCountText.setTextSize(15);
        r0.searchCountText.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r0.searchContainer.addView(r0.searchCountText, LayoutHelper.createFrame(-1, -2.0f, 19, 108.0f, 0.0f, 0.0f, 0.0f));
        r0.chatAdapter.updateRows();
        if (r0.loading && r0.messages.isEmpty()) {
            r0.progressView.setVisibility(0);
            r0.chatListView.setEmptyView(null);
        } else {
            r0.progressView.setVisibility(4);
            r0.chatListView.setEmptyView(r0.emptyViewContainer);
        }
        updateEmptyPlaceholder();
        return r0.fragmentView;
    }

    private void createMenu(View v) {
        MessageObject message = null;
        if (v instanceof ChatMessageCell) {
            message = ((ChatMessageCell) v).getMessageObject();
        } else if (v instanceof ChatActionCell) {
            message = ((ChatActionCell) v).getMessageObject();
        }
        if (message != null) {
            int type = getMessageType(message);
            this.selectedObject = message;
            if (getParentActivity() != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                ArrayList<CharSequence> items = new ArrayList();
                final ArrayList<Integer> options = new ArrayList();
                if (this.selectedObject.type == 0 || this.selectedObject.caption != null) {
                    items.add(LocaleController.getString("Copy", R.string.Copy));
                    options.add(Integer.valueOf(3));
                }
                if (type == 1) {
                    if (this.selectedObject.currentEvent != null && (this.selectedObject.currentEvent.action instanceof TL_channelAdminLogEventActionChangeStickerSet)) {
                        InputStickerSet stickerSet = this.selectedObject.currentEvent.action.new_stickerset;
                        if (stickerSet == null || (stickerSet instanceof TL_inputStickerSetEmpty)) {
                            stickerSet = this.selectedObject.currentEvent.action.prev_stickerset;
                        }
                        if (stickerSet != null) {
                            showDialog(new StickersAlert(getParentActivity(), this, stickerSet, null, null));
                            return;
                        }
                    }
                } else if (type == 3) {
                    if ((this.selectedObject.messageOwner.media instanceof TL_messageMediaWebPage) && MessageObject.isNewGifDocument(this.selectedObject.messageOwner.media.webpage.document)) {
                        items.add(LocaleController.getString("SaveToGIFs", R.string.SaveToGIFs));
                        options.add(Integer.valueOf(11));
                    }
                } else if (type == 4) {
                    if (this.selectedObject.isVideo()) {
                        items.add(LocaleController.getString("SaveToGallery", R.string.SaveToGallery));
                        options.add(Integer.valueOf(4));
                        items.add(LocaleController.getString("ShareFile", R.string.ShareFile));
                        options.add(Integer.valueOf(6));
                    } else if (this.selectedObject.isMusic()) {
                        items.add(LocaleController.getString("SaveToMusic", R.string.SaveToMusic));
                        options.add(Integer.valueOf(10));
                        items.add(LocaleController.getString("ShareFile", R.string.ShareFile));
                        options.add(Integer.valueOf(6));
                    } else if (this.selectedObject.getDocument() != null) {
                        if (MessageObject.isNewGifDocument(this.selectedObject.getDocument())) {
                            items.add(LocaleController.getString("SaveToGIFs", R.string.SaveToGIFs));
                            options.add(Integer.valueOf(11));
                        }
                        items.add(LocaleController.getString("SaveToDownloads", R.string.SaveToDownloads));
                        options.add(Integer.valueOf(10));
                        items.add(LocaleController.getString("ShareFile", R.string.ShareFile));
                        options.add(Integer.valueOf(6));
                    } else {
                        items.add(LocaleController.getString("SaveToGallery", R.string.SaveToGallery));
                        options.add(Integer.valueOf(4));
                    }
                } else if (type == 5) {
                    items.add(LocaleController.getString("ApplyLocalizationFile", R.string.ApplyLocalizationFile));
                    options.add(Integer.valueOf(5));
                    items.add(LocaleController.getString("SaveToDownloads", R.string.SaveToDownloads));
                    options.add(Integer.valueOf(10));
                    items.add(LocaleController.getString("ShareFile", R.string.ShareFile));
                    options.add(Integer.valueOf(6));
                } else if (type == 10) {
                    items.add(LocaleController.getString("ApplyThemeFile", R.string.ApplyThemeFile));
                    options.add(Integer.valueOf(5));
                    items.add(LocaleController.getString("SaveToDownloads", R.string.SaveToDownloads));
                    options.add(Integer.valueOf(10));
                    items.add(LocaleController.getString("ShareFile", R.string.ShareFile));
                    options.add(Integer.valueOf(6));
                } else if (type == 6) {
                    items.add(LocaleController.getString("SaveToGallery", R.string.SaveToGallery));
                    options.add(Integer.valueOf(7));
                    items.add(LocaleController.getString("SaveToDownloads", R.string.SaveToDownloads));
                    options.add(Integer.valueOf(10));
                    items.add(LocaleController.getString("ShareFile", R.string.ShareFile));
                    options.add(Integer.valueOf(6));
                } else if (type == 7) {
                    if (this.selectedObject.isMask()) {
                        items.add(LocaleController.getString("AddToMasks", R.string.AddToMasks));
                    } else {
                        items.add(LocaleController.getString("AddToStickers", R.string.AddToStickers));
                    }
                    options.add(Integer.valueOf(9));
                } else if (type == 8) {
                    User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.selectedObject.messageOwner.media.user_id));
                    if (!(user == null || user.id == UserConfig.getInstance(this.currentAccount).getClientUserId() || ContactsController.getInstance(this.currentAccount).contactsDict.get(Integer.valueOf(user.id)) != null)) {
                        items.add(LocaleController.getString("AddContactTitle", R.string.AddContactTitle));
                        options.add(Integer.valueOf(15));
                    }
                    if (!(this.selectedObject.messageOwner.media.phone_number == null && this.selectedObject.messageOwner.media.phone_number.length() == 0)) {
                        items.add(LocaleController.getString("Copy", R.string.Copy));
                        options.add(Integer.valueOf(16));
                        items.add(LocaleController.getString("Call", R.string.Call));
                        options.add(Integer.valueOf(17));
                    }
                }
                if (!options.isEmpty()) {
                    builder.setItems((CharSequence[]) items.toArray(new CharSequence[items.size()]), new OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (ChannelAdminLogActivity.this.selectedObject != null && i >= 0) {
                                if (i < options.size()) {
                                    ChannelAdminLogActivity.this.processSelectedOption(((Integer) options.get(i)).intValue());
                                }
                            }
                        }
                    });
                    builder.setTitle(LocaleController.getString("Message", R.string.Message));
                    showDialog(builder.create());
                }
            }
        }
    }

    private String getMessageContent(MessageObject messageObject, int previousUid, boolean name) {
        String str = TtmlNode.ANONYMOUS_REGION_ID;
        if (name && previousUid != messageObject.messageOwner.from_id) {
            StringBuilder stringBuilder;
            if (messageObject.messageOwner.from_id > 0) {
                User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(messageObject.messageOwner.from_id));
                if (user != null) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(ContactsController.formatName(user.first_name, user.last_name));
                    stringBuilder.append(":\n");
                    str = stringBuilder.toString();
                }
            } else if (messageObject.messageOwner.from_id < 0) {
                Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-messageObject.messageOwner.from_id));
                if (chat != null) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(chat.title);
                    stringBuilder.append(":\n");
                    str = stringBuilder.toString();
                }
            }
        }
        StringBuilder stringBuilder2;
        if (messageObject.type == 0 && messageObject.messageOwner.message != null) {
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append(str);
            stringBuilder2.append(messageObject.messageOwner.message);
            return stringBuilder2.toString();
        } else if (messageObject.messageOwner.media == null || messageObject.messageOwner.message == null) {
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append(str);
            stringBuilder2.append(messageObject.messageText);
            return stringBuilder2.toString();
        } else {
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append(str);
            stringBuilder2.append(messageObject.messageOwner.message);
            return stringBuilder2.toString();
        }
    }

    private TextureView createTextureView(boolean add) {
        if (this.parentLayout == null) {
            return null;
        }
        if (this.roundVideoContainer == null) {
            if (VERSION.SDK_INT >= 21) {
                this.roundVideoContainer = new FrameLayout(getParentActivity()) {
                    public void setTranslationY(float translationY) {
                        super.setTranslationY(translationY);
                        ChannelAdminLogActivity.this.contentView.invalidate();
                    }
                };
                this.roundVideoContainer.setOutlineProvider(new ViewOutlineProvider() {
                    @TargetApi(21)
                    public void getOutline(View view, Outline outline) {
                        outline.setOval(0, 0, AndroidUtilities.roundMessageSize, AndroidUtilities.roundMessageSize);
                    }
                });
                this.roundVideoContainer.setClipToOutline(true);
            } else {
                this.roundVideoContainer = new FrameLayout(getParentActivity()) {
                    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
                        super.onSizeChanged(w, h, oldw, oldh);
                        ChannelAdminLogActivity.this.aspectPath.reset();
                        ChannelAdminLogActivity.this.aspectPath.addCircle((float) (w / 2), (float) (h / 2), (float) (w / 2), Direction.CW);
                        ChannelAdminLogActivity.this.aspectPath.toggleInverseFillType();
                    }

                    public void setTranslationY(float translationY) {
                        super.setTranslationY(translationY);
                        ChannelAdminLogActivity.this.contentView.invalidate();
                    }

                    public void setVisibility(int visibility) {
                        super.setVisibility(visibility);
                        if (visibility == 0) {
                            setLayerType(2, null);
                        }
                    }

                    protected void dispatchDraw(Canvas canvas) {
                        super.dispatchDraw(canvas);
                        canvas.drawPath(ChannelAdminLogActivity.this.aspectPath, ChannelAdminLogActivity.this.aspectPaint);
                    }
                };
                this.aspectPath = new Path();
                this.aspectPaint = new Paint(1);
                this.aspectPaint.setColor(Theme.ACTION_BAR_VIDEO_EDIT_COLOR);
                this.aspectPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
            }
            this.roundVideoContainer.setWillNotDraw(false);
            this.roundVideoContainer.setVisibility(4);
            this.aspectRatioFrameLayout = new AspectRatioFrameLayout(getParentActivity());
            this.aspectRatioFrameLayout.setBackgroundColor(0);
            if (add) {
                this.roundVideoContainer.addView(this.aspectRatioFrameLayout, LayoutHelper.createFrame(-1, -1.0f));
            }
            this.videoTextureView = new TextureView(getParentActivity());
            this.videoTextureView.setOpaque(false);
            this.aspectRatioFrameLayout.addView(this.videoTextureView, LayoutHelper.createFrame(-1, -1.0f));
        }
        if (this.roundVideoContainer.getParent() == null) {
            this.contentView.addView(this.roundVideoContainer, 1, new FrameLayout.LayoutParams(AndroidUtilities.roundMessageSize, AndroidUtilities.roundMessageSize));
        }
        this.roundVideoContainer.setVisibility(4);
        this.aspectRatioFrameLayout.setDrawingReady(false);
        return this.videoTextureView;
    }

    private void destroyTextureView() {
        if (this.roundVideoContainer != null) {
            if (this.roundVideoContainer.getParent() != null) {
                this.contentView.removeView(this.roundVideoContainer);
                this.aspectRatioFrameLayout.setDrawingReady(false);
                this.roundVideoContainer.setVisibility(4);
                if (VERSION.SDK_INT < 21) {
                    this.roundVideoContainer.setLayerType(0, null);
                }
            }
        }
    }

    private void processSelectedOption(int option) {
        if (this.selectedObject != null) {
            int i = 3;
            int i2 = 0;
            String path;
            switch (option) {
                case 3:
                    AndroidUtilities.addToClipboard(getMessageContent(this.selectedObject, 0, true));
                    break;
                case 4:
                    path = this.selectedObject.messageOwner.attachPath;
                    if (!(path == null || path.length() <= 0 || new File(path).exists())) {
                        path = null;
                    }
                    if (path == null || path.length() == 0) {
                        path = FileLoader.getPathToMessage(this.selectedObject.messageOwner).toString();
                    }
                    if (this.selectedObject.type == 3 || this.selectedObject.type == 1) {
                        if (VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                            Context parentActivity = getParentActivity();
                            if (this.selectedObject.type == 3) {
                                i2 = 1;
                            }
                            MediaController.saveFile(path, parentActivity, i2, null, null);
                            break;
                        }
                        getParentActivity().requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 4);
                        this.selectedObject = null;
                        return;
                    }
                    break;
                case 5:
                    File f;
                    File locFile = null;
                    if (!(this.selectedObject.messageOwner.attachPath == null || this.selectedObject.messageOwner.attachPath.length() == 0)) {
                        f = new File(this.selectedObject.messageOwner.attachPath);
                        if (f.exists()) {
                            locFile = f;
                        }
                    }
                    if (locFile == null) {
                        f = FileLoader.getPathToMessage(this.selectedObject.messageOwner);
                        if (f.exists()) {
                            locFile = f;
                        }
                    }
                    if (locFile != null) {
                        if (!locFile.getName().toLowerCase().endsWith("attheme")) {
                            if (!LocaleController.getInstance().applyLanguageFile(locFile, this.currentAccount)) {
                                if (getParentActivity() != null) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                    builder.setMessage(LocaleController.getString("IncorrectLocalization", R.string.IncorrectLocalization));
                                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                                    showDialog(builder.create());
                                    break;
                                }
                                this.selectedObject = null;
                                return;
                            }
                            presentFragment(new LanguageSelectActivity());
                            break;
                        }
                        if (this.chatLayoutManager != null) {
                            if (this.chatLayoutManager.findLastVisibleItemPosition() < this.chatLayoutManager.getItemCount() - 1) {
                                this.scrollToPositionOnRecreate = this.chatLayoutManager.findFirstVisibleItemPosition();
                                Holder holder = (Holder) this.chatListView.findViewHolderForAdapterPosition(this.scrollToPositionOnRecreate);
                                if (holder != null) {
                                    this.scrollToOffsetOnRecreate = holder.itemView.getTop();
                                } else {
                                    this.scrollToPositionOnRecreate = -1;
                                }
                            } else {
                                this.scrollToPositionOnRecreate = -1;
                            }
                        }
                        ThemeInfo themeInfo = Theme.applyThemeFile(locFile, this.selectedObject.getDocumentName(), true);
                        if (themeInfo != null) {
                            presentFragment(new ThemePreviewActivity(locFile, themeInfo));
                        } else {
                            this.scrollToPositionOnRecreate = -1;
                            if (getParentActivity() == null) {
                                this.selectedObject = null;
                                return;
                            }
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(getParentActivity());
                            builder2.setTitle(LocaleController.getString("AppName", R.string.AppName));
                            builder2.setMessage(LocaleController.getString("IncorrectTheme", R.string.IncorrectTheme));
                            builder2.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                            showDialog(builder2.create());
                        }
                        break;
                    }
                    break;
                case 6:
                    String path2 = this.selectedObject.messageOwner.attachPath;
                    if (!(path2 == null || path2.length() <= 0 || new File(path2).exists())) {
                        path2 = null;
                    }
                    if (path2 == null || path2.length() == 0) {
                        path2 = FileLoader.getPathToMessage(this.selectedObject.messageOwner).toString();
                    }
                    Intent intent = new Intent("android.intent.action.SEND");
                    intent.setType(this.selectedObject.getDocument().mime_type);
                    if (VERSION.SDK_INT >= 24) {
                        try {
                            intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(getParentActivity(), "org.telegram.messenger.beta.provider", new File(path2)));
                            intent.setFlags(1);
                        } catch (Exception e) {
                            intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(new File(path2)));
                        }
                    } else {
                        intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(new File(path2)));
                    }
                    getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString("ShareFile", R.string.ShareFile)), 500);
                    break;
                case 7:
                    path = this.selectedObject.messageOwner.attachPath;
                    if (!(path == null || path.length() <= 0 || new File(path).exists())) {
                        path = null;
                    }
                    if (path == null || path.length() == 0) {
                        path = FileLoader.getPathToMessage(this.selectedObject.messageOwner).toString();
                    }
                    if (VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                        MediaController.saveFile(path, getParentActivity(), 0, null, null);
                        break;
                    }
                    getParentActivity().requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 4);
                    this.selectedObject = null;
                    return;
                case 9:
                    showDialog(new StickersAlert(getParentActivity(), this, this.selectedObject.getInputStickerSet(), null, null));
                    break;
                case 10:
                    if (VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                        path = FileLoader.getDocumentFileName(this.selectedObject.getDocument());
                        if (TextUtils.isEmpty(path)) {
                            path = this.selectedObject.getFileName();
                        }
                        String path3 = this.selectedObject.messageOwner.attachPath;
                        if (!(path3 == null || path3.length() <= 0 || new File(path3).exists())) {
                            path3 = null;
                        }
                        if (path3 == null || path3.length() == 0) {
                            path3 = FileLoader.getPathToMessage(this.selectedObject.messageOwner).toString();
                        }
                        Context parentActivity2 = getParentActivity();
                        if (!this.selectedObject.isMusic()) {
                            i = 2;
                        }
                        MediaController.saveFile(path3, parentActivity2, i, path, this.selectedObject.getDocument() != null ? this.selectedObject.getDocument().mime_type : TtmlNode.ANONYMOUS_REGION_ID);
                        break;
                    }
                    getParentActivity().requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 4);
                    this.selectedObject = null;
                    return;
                    break;
                case 11:
                    MessagesController.getInstance(this.currentAccount).saveGif(this.selectedObject.getDocument());
                    break;
                case 15:
                    Bundle args = new Bundle();
                    args.putInt("user_id", this.selectedObject.messageOwner.media.user_id);
                    args.putString("phone", this.selectedObject.messageOwner.media.phone_number);
                    args.putBoolean("addContact", true);
                    presentFragment(new ContactAddActivity(args));
                    break;
                case 16:
                    AndroidUtilities.addToClipboard(this.selectedObject.messageOwner.media.phone_number);
                    break;
                case 17:
                    try {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("tel:");
                        stringBuilder.append(this.selectedObject.messageOwner.media.phone_number);
                        Intent intent2 = new Intent("android.intent.action.DIAL", Uri.parse(stringBuilder.toString()));
                        intent2.addFlags(268435456);
                        getParentActivity().startActivityForResult(intent2, 500);
                        break;
                    } catch (Throwable e2) {
                        FileLog.m3e(e2);
                        break;
                    }
                default:
                    break;
            }
            this.selectedObject = null;
        }
    }

    private int getMessageType(MessageObject messageObject) {
        if (messageObject == null || messageObject.type == 6) {
            return -1;
        }
        if (!(messageObject.type == 10 || messageObject.type == 11)) {
            if (messageObject.type != 16) {
                if (messageObject.isVoice()) {
                    return 2;
                }
                if (messageObject.isSticker()) {
                    InputStickerSet inputStickerSet = messageObject.getInputStickerSet();
                    if (inputStickerSet instanceof TL_inputStickerSetID) {
                        if (!DataQuery.getInstance(this.currentAccount).isStickerPackInstalled(inputStickerSet.id)) {
                            return 7;
                        }
                    } else if ((inputStickerSet instanceof TL_inputStickerSetShortName) && !DataQuery.getInstance(this.currentAccount).isStickerPackInstalled(inputStickerSet.short_name)) {
                        return 7;
                    }
                }
                if (!messageObject.isRoundVideo() || (messageObject.isRoundVideo() && BuildVars.DEBUG_VERSION)) {
                    if (!((messageObject.messageOwner.media instanceof TL_messageMediaPhoto) || messageObject.getDocument() != null || messageObject.isMusic())) {
                        if (messageObject.isVideo()) {
                        }
                    }
                    boolean canSave = false;
                    if (!(messageObject.messageOwner.attachPath == null || messageObject.messageOwner.attachPath.length() == 0 || !new File(messageObject.messageOwner.attachPath).exists())) {
                        canSave = true;
                    }
                    if (!canSave && FileLoader.getPathToMessage(messageObject.messageOwner).exists()) {
                        canSave = true;
                    }
                    if (canSave) {
                        if (messageObject.getDocument() != null) {
                            String mime = messageObject.getDocument().mime_type;
                            if (mime != null) {
                                if (messageObject.getDocumentName().toLowerCase().endsWith("attheme")) {
                                    return 10;
                                }
                                if (mime.endsWith("/xml")) {
                                    return 5;
                                }
                                if (mime.endsWith("/png") || mime.endsWith("/jpg") || mime.endsWith("/jpeg")) {
                                    return 6;
                                }
                            }
                        }
                        return 4;
                    }
                }
                if (messageObject.type == 12) {
                    return 8;
                }
                if (messageObject.isMediaEmpty()) {
                    return 3;
                }
                return 2;
            }
        }
        if (messageObject.getId() == 0) {
            return -1;
        }
        return 1;
    }

    private void loadAdmins() {
        TL_channels_getParticipants req = new TL_channels_getParticipants();
        req.channel = MessagesController.getInputChannel(this.currentChat);
        req.filter = new TL_channelParticipantsAdmins();
        req.offset = 0;
        req.limit = Callback.DEFAULT_DRAG_ANIMATION_DURATION;
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
            public void run(final TLObject response, final TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        if (error == null) {
                            TL_channels_channelParticipants res = response;
                            MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).putUsers(res.users, false);
                            ChannelAdminLogActivity.this.admins = res.participants;
                            if (ChannelAdminLogActivity.this.visibleDialog instanceof AdminLogFilterAlert) {
                                ((AdminLogFilterAlert) ChannelAdminLogActivity.this.visibleDialog).setCurrentAdmins(ChannelAdminLogActivity.this.admins);
                            }
                        }
                    }
                });
            }
        }), this.classGuid);
    }

    protected void onRemoveFromParent() {
        MediaController.getInstance().setTextureView(this.videoTextureView, null, null, false);
    }

    private void hideFloatingDateView(boolean animated) {
        if (this.floatingDateView.getTag() != null && !this.currentFloatingDateOnScreen) {
            if (!this.scrollingFloatingDate || this.currentFloatingTopIsNotMessage) {
                this.floatingDateView.setTag(null);
                if (animated) {
                    this.floatingDateAnimation = new AnimatorSet();
                    this.floatingDateAnimation.setDuration(150);
                    AnimatorSet animatorSet = this.floatingDateAnimation;
                    Animator[] animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.floatingDateView, "alpha", new float[]{0.0f});
                    animatorSet.playTogether(animatorArr);
                    this.floatingDateAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (animation.equals(ChannelAdminLogActivity.this.floatingDateAnimation)) {
                                ChannelAdminLogActivity.this.floatingDateAnimation = null;
                            }
                        }
                    });
                    this.floatingDateAnimation.setStartDelay(500);
                    this.floatingDateAnimation.start();
                    return;
                }
                if (this.floatingDateAnimation != null) {
                    this.floatingDateAnimation.cancel();
                    this.floatingDateAnimation = null;
                }
                this.floatingDateView.setAlpha(0.0f);
            }
        }
    }

    private void checkScrollForLoad(boolean scroll) {
        if (this.chatLayoutManager != null) {
            if (!this.paused) {
                int firstVisibleItem = this.chatLayoutManager.findFirstVisibleItemPosition();
                if ((firstVisibleItem == -1 ? 0 : Math.abs(this.chatLayoutManager.findLastVisibleItemPosition() - firstVisibleItem) + 1) > 0) {
                    int checkLoadCount;
                    int totalItemCount = this.chatAdapter.getItemCount();
                    if (scroll) {
                        checkLoadCount = 25;
                    } else {
                        checkLoadCount = 5;
                    }
                    if (!(firstVisibleItem > checkLoadCount || this.loading || this.endReached)) {
                        loadMessages(false);
                    }
                }
            }
        }
    }

    private void moveScrollToLastMessage() {
        if (this.chatListView != null && !this.messages.isEmpty()) {
            this.chatLayoutManager.scrollToPositionWithOffset(this.messages.size() - 1, -100000 - this.chatListView.getPaddingTop());
        }
    }

    private void updateTextureViewPosition() {
        boolean foundTextureViewMessage = false;
        int count = this.chatListView.getChildCount();
        for (int a = 0; a < count; a++) {
            View view = this.chatListView.getChildAt(a);
            if (view instanceof ChatMessageCell) {
                ChatMessageCell messageCell = (ChatMessageCell) view;
                MessageObject messageObject = messageCell.getMessageObject();
                if (this.roundVideoContainer != null && messageObject.isRoundVideo() && MediaController.getInstance().isPlayingMessage(messageObject)) {
                    ImageReceiver imageReceiver = messageCell.getPhotoImage();
                    this.roundVideoContainer.setTranslationX((float) imageReceiver.getImageX());
                    this.roundVideoContainer.setTranslationY((float) ((this.fragmentView.getPaddingTop() + messageCell.getTop()) + imageReceiver.getImageY()));
                    this.fragmentView.invalidate();
                    this.roundVideoContainer.invalidate();
                    foundTextureViewMessage = true;
                    break;
                }
            }
        }
        if (this.roundVideoContainer != null) {
            MessageObject messageObject2 = MediaController.getInstance().getPlayingMessageObject();
            if (foundTextureViewMessage) {
                MediaController.getInstance().setCurrentRoundVisible(true);
                return;
            }
            this.roundVideoContainer.setTranslationY((float) ((-AndroidUtilities.roundMessageSize) - 100));
            this.fragmentView.invalidate();
            if (messageObject2 != null && messageObject2.isRoundVideo()) {
                if (this.checkTextureViewPosition || PipRoundVideoView.getInstance() != null) {
                    MediaController.getInstance().setCurrentRoundVisible(false);
                }
            }
        }
    }

    private void updateMessagesVisisblePart() {
        if (this.chatListView != null) {
            int height;
            MessageObject messageObject;
            int count = r0.chatListView.getChildCount();
            int height2 = r0.chatListView.getMeasuredHeight();
            View minDateChild = null;
            View minChild = null;
            View minMessageChild = null;
            boolean foundTextureViewMessage = false;
            int minPositionDateHolder = ConnectionsManager.DEFAULT_DATACENTER_ID;
            int minPositionHolder = ConnectionsManager.DEFAULT_DATACENTER_ID;
            int a = 0;
            while (a < count) {
                int count2;
                View view = r0.chatListView.getChildAt(a);
                if (view instanceof ChatMessageCell) {
                    ChatMessageCell messageCell = (ChatMessageCell) view;
                    int top = messageCell.getTop();
                    int bottom = messageCell.getBottom();
                    int viewTop = top >= 0 ? 0 : -top;
                    int viewBottom = messageCell.getMeasuredHeight();
                    if (viewBottom > height2) {
                        viewBottom = viewTop + height2;
                    }
                    count2 = count;
                    messageCell.setVisiblePart(viewTop, viewBottom - viewTop);
                    count = messageCell.getMessageObject();
                    height = height2;
                    if (r0.roundVideoContainer != 0 && count.isRoundVideo() && MediaController.getInstance().isPlayingMessage(count)) {
                        height2 = messageCell.getPhotoImage();
                        MessageObject messageObject2 = count;
                        r0.roundVideoContainer.setTranslationX((float) height2.getImageX());
                        r0.roundVideoContainer.setTranslationY((float) ((r0.fragmentView.getPaddingTop() + top) + height2.getImageY()));
                        r0.fragmentView.invalidate();
                        r0.roundVideoContainer.invalidate();
                        foundTextureViewMessage = true;
                    }
                } else {
                    count2 = count;
                    height = height2;
                }
                if (view.getBottom() > r0.chatListView.getPaddingTop()) {
                    count = view.getBottom();
                    if (count < minPositionHolder) {
                        minPositionHolder = count;
                        if ((view instanceof ChatMessageCell) || (view instanceof ChatActionCell)) {
                            minMessageChild = view;
                        }
                        minChild = view;
                    }
                    if ((view instanceof ChatActionCell) && ((ChatActionCell) view).getMessageObject().isDateObject) {
                        if (view.getAlpha() != 1.0f) {
                            view.setAlpha(1.0f);
                        }
                        if (count < minPositionDateHolder) {
                            minDateChild = view;
                            minPositionDateHolder = count;
                        }
                    }
                }
                a++;
                count = count2;
                height2 = height;
            }
            height = height2;
            if (r0.roundVideoContainer != null) {
                if (foundTextureViewMessage) {
                    MediaController.getInstance().setCurrentRoundVisible(true);
                } else {
                    r0.roundVideoContainer.setTranslationY((float) ((-AndroidUtilities.roundMessageSize) - 100));
                    r0.fragmentView.invalidate();
                    messageObject = MediaController.getInstance().getPlayingMessageObject();
                    if (messageObject != null && messageObject.isRoundVideo() && r0.checkTextureViewPosition) {
                        MediaController.getInstance().setCurrentRoundVisible(false);
                    }
                }
            }
            if (minMessageChild != null) {
                if (minMessageChild instanceof ChatMessageCell) {
                    messageObject = ((ChatMessageCell) minMessageChild).getMessageObject();
                } else {
                    messageObject = ((ChatActionCell) minMessageChild).getMessageObject();
                }
                r0.floatingDateView.setCustomDate(messageObject.messageOwner.date);
            }
            boolean z = false;
            r0.currentFloatingDateOnScreen = false;
            if (!((minChild instanceof ChatMessageCell) || (minChild instanceof ChatActionCell))) {
                z = true;
            }
            r0.currentFloatingTopIsNotMessage = z;
            if (minDateChild != null) {
                if (minDateChild.getTop() <= r0.chatListView.getPaddingTop()) {
                    if (!r0.currentFloatingTopIsNotMessage) {
                        if (minDateChild.getAlpha() != 0.0f) {
                            minDateChild.setAlpha(0.0f);
                        }
                        if (r0.floatingDateAnimation != null) {
                            r0.floatingDateAnimation.cancel();
                            r0.floatingDateAnimation = null;
                        }
                        if (r0.floatingDateView.getTag() == null) {
                            r0.floatingDateView.setTag(Integer.valueOf(1));
                        }
                        if (r0.floatingDateView.getAlpha() != 1.0f) {
                            r0.floatingDateView.setAlpha(1.0f);
                        }
                        r0.currentFloatingDateOnScreen = true;
                        height2 = minDateChild.getBottom() - r0.chatListView.getPaddingTop();
                        if (height2 > r0.floatingDateView.getMeasuredHeight() || height2 >= r0.floatingDateView.getMeasuredHeight() * 2) {
                            r0.floatingDateView.setTranslationY(0.0f);
                        } else {
                            r0.floatingDateView.setTranslationY((float) (((-r0.floatingDateView.getMeasuredHeight()) * 2) + height2));
                        }
                    }
                }
                if (minDateChild.getAlpha() != 1.0f) {
                    minDateChild.setAlpha(1.0f);
                }
                hideFloatingDateView(r0.currentFloatingTopIsNotMessage ^ true);
                height2 = minDateChild.getBottom() - r0.chatListView.getPaddingTop();
                if (height2 > r0.floatingDateView.getMeasuredHeight()) {
                }
                r0.floatingDateView.setTranslationY(0.0f);
            } else {
                hideFloatingDateView(true);
                r0.floatingDateView.setTranslationY(0.0f);
            }
        }
    }

    public void onTransitionAnimationStart(boolean isOpen, boolean backward) {
        NotificationCenter.getInstance(this.currentAccount).setAllowedNotificationsDutingAnimation(new int[]{NotificationCenter.chatInfoDidLoaded, NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.messagesDidLoaded, NotificationCenter.botKeyboardDidLoaded});
        NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(true);
        if (isOpen) {
            this.openAnimationEnded = false;
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(false);
        if (isOpen) {
            this.openAnimationEnded = true;
        }
    }

    public void onResume() {
        super.onResume();
        this.paused = false;
        checkScrollForLoad(false);
        if (this.wasPaused) {
            this.wasPaused = false;
            if (this.chatAdapter != null) {
                this.chatAdapter.notifyDataSetChanged();
            }
        }
        fixLayout();
    }

    public void onPause() {
        super.onPause();
        this.paused = true;
        this.wasPaused = true;
    }

    private void fixLayout() {
        if (this.avatarContainer != null) {
            this.avatarContainer.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    if (ChannelAdminLogActivity.this.avatarContainer != null) {
                        ChannelAdminLogActivity.this.avatarContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    return true;
                }
            });
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        fixLayout();
        if (this.visibleDialog instanceof DatePickerDialog) {
            this.visibleDialog.dismiss();
        }
    }

    private void alertUserOpenError(MessageObject message) {
        if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            if (message.type == 3) {
                builder.setMessage(LocaleController.getString("NoPlayerInstalled", R.string.NoPlayerInstalled));
            } else {
                builder.setMessage(LocaleController.formatString("NoHandleAppInstalled", R.string.NoHandleAppInstalled, message.getDocument().mime_type));
            }
            showDialog(builder.create());
        }
    }

    public Chat getCurrentChat() {
        return this.currentChat;
    }

    private void addCanBanUser(Bundle bundle, int uid) {
        if (this.currentChat.megagroup && this.admins != null) {
            if (ChatObject.canBlockUsers(this.currentChat)) {
                for (int a = 0; a < this.admins.size(); a++) {
                    ChannelParticipant channelParticipant = (ChannelParticipant) this.admins.get(a);
                    if (channelParticipant.user_id == uid) {
                        if (!channelParticipant.can_edit) {
                            return;
                        }
                        bundle.putInt("ban_chat_id", this.currentChat.id);
                    }
                }
                bundle.putInt("ban_chat_id", this.currentChat.id);
            }
        }
    }

    public void showOpenUrlAlert(final String url, boolean ask) {
        if (!Browser.isInternalUrl(url, null)) {
            if (ask) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setMessage(LocaleController.formatString("OpenUrlAlert", R.string.OpenUrlAlert, url));
                builder.setPositiveButton(LocaleController.getString("Open", R.string.Open), new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Browser.openUrl(ChannelAdminLogActivity.this.getParentActivity(), url, true);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                showDialog(builder.create());
                return;
            }
        }
        Browser.openUrl(getParentActivity(), url, true);
    }

    private void removeMessageObject(MessageObject messageObject) {
        int index = this.messages.indexOf(messageObject);
        if (index != -1) {
            this.messages.remove(index);
            if (this.chatAdapter != null) {
                this.chatAdapter.notifyItemRemoved(((this.chatAdapter.messagesStartRow + this.messages.size()) - index) - 1);
            }
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        r1 = new ThemeDescription[207];
        r1[11] = new ThemeDescription(this.avatarContainer.getSubtitleTextView(), ThemeDescription.FLAG_TEXTCOLOR, null, new Paint[]{Theme.chat_statusPaint, Theme.chat_statusRecordPaint}, null, null, Theme.key_actionBarDefaultSubtitle, null);
        r1[12] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        r1[13] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, Theme.key_avatar_text);
        r1[14] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_backgroundRed);
        r1[15] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_backgroundOrange);
        r1[16] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_backgroundViolet);
        r1[17] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_backgroundGreen);
        r1[18] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_backgroundCyan);
        r1[19] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_backgroundBlue);
        r1[20] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_backgroundPink);
        r1[21] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_nameInMessageRed);
        r1[22] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_nameInMessageOrange);
        r1[23] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_nameInMessageViolet);
        r1[24] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_nameInMessageGreen);
        r1[25] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_nameInMessageCyan);
        r1[26] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_nameInMessageBlue);
        r1[27] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_nameInMessagePink);
        r1[28] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, null, Theme.key_chat_inBubble);
        r1[29] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, null, Theme.key_chat_inBubbleSelected);
        r1[30] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInShadowDrawable, Theme.chat_msgInMediaShadowDrawable}, null, Theme.key_chat_inBubbleShadow);
        r1[31] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, Theme.key_chat_outBubble);
        r1[32] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, null, Theme.key_chat_outBubbleSelected);
        r1[33] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutShadowDrawable, Theme.chat_msgOutMediaShadowDrawable}, null, Theme.key_chat_outBubbleShadow);
        r1[34] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ChatActionCell.class}, Theme.chat_actionTextPaint, null, null, Theme.key_chat_serviceText);
        r1[35] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{ChatActionCell.class}, Theme.chat_actionTextPaint, null, null, Theme.key_chat_serviceLink);
        r1[36] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_shareIconDrawable, Theme.chat_botInlineDrawable, Theme.chat_botLinkDrawalbe, Theme.chat_goIconDrawable}, null, Theme.key_chat_serviceIcon);
        r1[37] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class, ChatActionCell.class}, null, null, null, Theme.key_chat_serviceBackground);
        r1[38] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class, ChatActionCell.class}, null, null, null, Theme.key_chat_serviceBackgroundSelected);
        r1[39] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_messageTextIn);
        r1[40] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_messageTextOut);
        r1[41] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_messageLinkIn, null);
        r1[42] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_messageLinkOut, null);
        r1[43] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckDrawable, Theme.chat_msgOutHalfCheckDrawable}, null, Theme.key_chat_outSentCheck);
        r1[44] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, null, Theme.key_chat_outSentCheckSelected);
        r1[45] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutClockDrawable}, null, Theme.key_chat_outSentClock);
        r1[46] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutSelectedClockDrawable}, null, Theme.key_chat_outSentClockSelected);
        r1[47] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInClockDrawable}, null, Theme.key_chat_inSentClock);
        r1[48] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInSelectedClockDrawable}, null, Theme.key_chat_inSentClockSelected);
        r1[49] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, null, Theme.key_chat_mediaSentCheck);
        r1[50] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgStickerHalfCheckDrawable, Theme.chat_msgStickerCheckDrawable, Theme.chat_msgStickerClockDrawable, Theme.chat_msgStickerViewsDrawable}, null, Theme.key_chat_serviceText);
        r1[51] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgMediaClockDrawable}, null, Theme.key_chat_mediaSentClock);
        r1[52] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutViewsDrawable}, null, Theme.key_chat_outViews);
        r1[53] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutViewsSelectedDrawable}, null, Theme.key_chat_outViewsSelected);
        r1[54] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInViewsDrawable}, null, Theme.key_chat_inViews);
        r1[55] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInViewsSelectedDrawable}, null, Theme.key_chat_inViewsSelected);
        r1[56] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgMediaViewsDrawable}, null, Theme.key_chat_mediaViews);
        r1[57] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutMenuDrawable}, null, Theme.key_chat_outMenu);
        r1[58] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutMenuSelectedDrawable}, null, Theme.key_chat_outMenuSelected);
        r1[59] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInMenuDrawable}, null, Theme.key_chat_inMenu);
        r1[60] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInMenuSelectedDrawable}, null, Theme.key_chat_inMenuSelected);
        r1[61] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgMediaMenuDrawable}, null, Theme.key_chat_mediaMenu);
        r1[62] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutInstantDrawable, Theme.chat_msgOutCallDrawable}, null, Theme.key_chat_outInstant);
        r1[63] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCallSelectedDrawable}, null, Theme.key_chat_outInstantSelected);
        r1[64] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInInstantDrawable, Theme.chat_msgInCallDrawable}, null, Theme.key_chat_inInstant);
        r1[65] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInCallSelectedDrawable}, null, Theme.key_chat_inInstantSelected);
        r1[66] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgCallUpRedDrawable, Theme.chat_msgCallDownRedDrawable}, null, Theme.key_calls_callReceivedRedIcon);
        r1[67] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgCallUpGreenDrawable, Theme.chat_msgCallDownGreenDrawable}, null, Theme.key_calls_callReceivedGreenIcon);
        r1[68] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_msgErrorPaint, null, null, Theme.key_chat_sentError);
        r1[69] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgErrorDrawable}, null, Theme.key_chat_sentErrorIcon);
        r1[70] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_durationPaint, null, null, Theme.key_chat_previewDurationText);
        r1[71] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_gamePaint, null, null, Theme.key_chat_previewGameText);
        r1[72] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inPreviewInstantText);
        r1[73] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outPreviewInstantText);
        r1[74] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inPreviewInstantSelectedText);
        r1[75] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outPreviewInstantSelectedText);
        r1[76] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_deleteProgressPaint, null, null, Theme.key_chat_secretTimeText);
        r1[77] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_stickerNameText);
        r1[78] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_botButtonPaint, null, null, Theme.key_chat_botButtonText);
        r1[79] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_botProgressPaint, null, null, Theme.key_chat_botProgress);
        r1[80] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inForwardedNameText);
        r1[81] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outForwardedNameText);
        r1[82] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inViaBotNameText);
        r1[83] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outViaBotNameText);
        r1[84] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_stickerViaBotNameText);
        r1[85] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyLine);
        r1[86] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyLine);
        r1[87] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_stickerReplyLine);
        r1[88] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyNameText);
        r1[89] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyNameText);
        r1[90] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_stickerReplyNameText);
        r1[91] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyMessageText);
        r1[92] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyMessageText);
        r1[93] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyMediaMessageText);
        r1[94] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyMediaMessageText);
        r1[95] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyMediaMessageSelectedText);
        r1[96] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyMediaMessageSelectedText);
        r1[97] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_stickerReplyMessageText);
        r1[98] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inPreviewLine);
        r1[99] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outPreviewLine);
        r1[100] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inSiteNameText);
        r1[101] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outSiteNameText);
        r1[102] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inContactNameText);
        r1[103] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outContactNameText);
        r1[104] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inContactPhoneText);
        r1[105] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outContactPhoneText);
        r1[106] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_mediaProgress);
        r1[107] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioProgress);
        r1[108] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioProgress);
        r1[109] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioSelectedProgress);
        r1[110] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioSelectedProgress);
        r1[111] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_mediaTimeText);
        r1[112] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inTimeText);
        r1[113] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outTimeText);
        r1[114] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inTimeSelectedText);
        r1[115] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outTimeSelectedText);
        r1[116] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioPerfomerText);
        r1[117] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioPerfomerText);
        r1[118] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioTitleText);
        r1[119] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioTitleText);
        r1[120] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioDurationText);
        r1[121] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioDurationText);
        r1[122] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioDurationSelectedText);
        r1[123] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioDurationSelectedText);
        r1[124] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioSeekbar);
        r1[125] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioSeekbar);
        r1[126] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioSeekbarSelected);
        r1[127] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioSeekbarSelected);
        r1[128] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioSeekbarFill);
        r1[TsExtractor.TS_STREAM_TYPE_AC3] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioCacheSeekbar);
        r1[TsExtractor.TS_STREAM_TYPE_HDMV_DTS] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioSeekbarFill);
        r1[131] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioCacheSeekbar);
        r1[132] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inVoiceSeekbar);
        r1[133] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outVoiceSeekbar);
        r1[TsExtractor.TS_STREAM_TYPE_SPLICE_INFO] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inVoiceSeekbarSelected);
        r1[TsExtractor.TS_STREAM_TYPE_E_AC3] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outVoiceSeekbarSelected);
        r1[136] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inVoiceSeekbarFill);
        r1[137] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outVoiceSeekbarFill);
        r1[TsExtractor.TS_STREAM_TYPE_DTS] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileProgress);
        r1[139] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileProgress);
        r1[140] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileProgressSelected);
        r1[141] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileProgressSelected);
        r1[142] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileNameText);
        r1[143] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileNameText);
        r1[144] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileInfoText);
        r1[145] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileInfoText);
        r1[146] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileInfoSelectedText);
        r1[147] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileInfoSelectedText);
        r1[148] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileBackground);
        r1[149] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileBackground);
        r1[150] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileBackgroundSelected);
        r1[151] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileBackgroundSelected);
        r1[152] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inVenueNameText);
        r1[153] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outVenueNameText);
        r1[154] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inVenueInfoText);
        r1[155] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outVenueInfoText);
        r1[156] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inVenueInfoSelectedText);
        r1[157] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outVenueInfoSelectedText);
        r1[158] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_mediaInfoText);
        r1[159] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_urlPaint, null, null, Theme.key_chat_linkSelectBackground);
        r1[160] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_textSearchSelectionPaint, null, null, Theme.key_chat_textSelectBackground);
        r1[161] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_fileStatesDrawable[0][0], Theme.chat_fileStatesDrawable[1][0], Theme.chat_fileStatesDrawable[2][0], Theme.chat_fileStatesDrawable[3][0], Theme.chat_fileStatesDrawable[4][0]}, null, Theme.key_chat_outLoader);
        r1[162] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_fileStatesDrawable[0][0], Theme.chat_fileStatesDrawable[1][0], Theme.chat_fileStatesDrawable[2][0], Theme.chat_fileStatesDrawable[3][0], Theme.chat_fileStatesDrawable[4][0]}, null, Theme.key_chat_outBubble);
        r1[163] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_fileStatesDrawable[0][1], Theme.chat_fileStatesDrawable[1][1], Theme.chat_fileStatesDrawable[2][1], Theme.chat_fileStatesDrawable[3][1], Theme.chat_fileStatesDrawable[4][1]}, null, Theme.key_chat_outLoaderSelected);
        r1[164] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_fileStatesDrawable[0][1], Theme.chat_fileStatesDrawable[1][1], Theme.chat_fileStatesDrawable[2][1], Theme.chat_fileStatesDrawable[3][1], Theme.chat_fileStatesDrawable[4][1]}, null, Theme.key_chat_outBubbleSelected);
        r1[165] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_fileStatesDrawable[5][0], Theme.chat_fileStatesDrawable[6][0], Theme.chat_fileStatesDrawable[7][0], Theme.chat_fileStatesDrawable[8][0], Theme.chat_fileStatesDrawable[9][0]}, null, Theme.key_chat_inLoader);
        r1[166] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_fileStatesDrawable[5][0], Theme.chat_fileStatesDrawable[6][0], Theme.chat_fileStatesDrawable[7][0], Theme.chat_fileStatesDrawable[8][0], Theme.chat_fileStatesDrawable[9][0]}, null, Theme.key_chat_inBubble);
        r1[167] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_fileStatesDrawable[5][1], Theme.chat_fileStatesDrawable[6][1], Theme.chat_fileStatesDrawable[7][1], Theme.chat_fileStatesDrawable[8][1], Theme.chat_fileStatesDrawable[9][1]}, null, Theme.key_chat_inLoaderSelected);
        r1[168] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_fileStatesDrawable[5][1], Theme.chat_fileStatesDrawable[6][1], Theme.chat_fileStatesDrawable[7][1], Theme.chat_fileStatesDrawable[8][1], Theme.chat_fileStatesDrawable[9][1]}, null, Theme.key_chat_inBubbleSelected);
        r1[169] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[0][0], Theme.chat_photoStatesDrawables[1][0], Theme.chat_photoStatesDrawables[2][0], Theme.chat_photoStatesDrawables[3][0]}, null, Theme.key_chat_mediaLoaderPhoto);
        r1[170] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[0][0], Theme.chat_photoStatesDrawables[1][0], Theme.chat_photoStatesDrawables[2][0], Theme.chat_photoStatesDrawables[3][0]}, null, Theme.key_chat_mediaLoaderPhotoIcon);
        r1[171] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[0][1], Theme.chat_photoStatesDrawables[1][1], Theme.chat_photoStatesDrawables[2][1], Theme.chat_photoStatesDrawables[3][1]}, null, Theme.key_chat_mediaLoaderPhotoSelected);
        r1[172] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[0][1], Theme.chat_photoStatesDrawables[1][1], Theme.chat_photoStatesDrawables[2][1], Theme.chat_photoStatesDrawables[3][1]}, null, Theme.key_chat_mediaLoaderPhotoIconSelected);
        r1[173] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[7][0], Theme.chat_photoStatesDrawables[8][0]}, null, Theme.key_chat_outLoaderPhoto);
        r1[174] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[7][0], Theme.chat_photoStatesDrawables[8][0]}, null, Theme.key_chat_outLoaderPhotoIcon);
        r1[175] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[7][1], Theme.chat_photoStatesDrawables[8][1]}, null, Theme.key_chat_outLoaderPhotoSelected);
        r1[176] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[7][1], Theme.chat_photoStatesDrawables[8][1]}, null, Theme.key_chat_outLoaderPhotoIconSelected);
        r1[177] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[10][0], Theme.chat_photoStatesDrawables[11][0]}, null, Theme.key_chat_inLoaderPhoto);
        r1[178] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[10][0], Theme.chat_photoStatesDrawables[11][0]}, null, Theme.key_chat_inLoaderPhotoIcon);
        r1[179] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[10][1], Theme.chat_photoStatesDrawables[11][1]}, null, Theme.key_chat_inLoaderPhotoSelected);
        r1[180] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[10][1], Theme.chat_photoStatesDrawables[11][1]}, null, Theme.key_chat_inLoaderPhotoIconSelected);
        r1[181] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[9][0]}, null, Theme.key_chat_outFileIcon);
        r1[182] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[9][1]}, null, Theme.key_chat_outFileSelectedIcon);
        View view = null;
        r1[183] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[12][0]}, null, Theme.key_chat_inFileIcon);
        r1[184] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[12][1]}, null, Theme.key_chat_inFileSelectedIcon);
        r1[185] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_contactDrawable[0]}, null, Theme.key_chat_inContactBackground);
        r1[186] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_contactDrawable[0]}, null, Theme.key_chat_inContactIcon);
        r1[187] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_contactDrawable[1]}, null, Theme.key_chat_outContactBackground);
        r1[188] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_contactDrawable[1]}, null, Theme.key_chat_outContactIcon);
        r1[PsExtractor.PRIVATE_STREAM_1] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_locationDrawable[0]}, null, Theme.key_chat_inLocationBackground);
        r1[190] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_locationDrawable[0]}, null, Theme.key_chat_inLocationIcon);
        r1[191] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_locationDrawable[1]}, null, Theme.key_chat_outLocationBackground);
        r1[PsExtractor.AUDIO_STREAM] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_locationDrawable[1]}, null, Theme.key_chat_outLocationIcon);
        r1[193] = new ThemeDescription(this.bottomOverlayChat, 0, null, Theme.chat_composeBackgroundPaint, null, null, Theme.key_chat_messagePanelBackground);
        r1[194] = new ThemeDescription(this.bottomOverlayChat, 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, Theme.key_chat_messagePanelShadow);
        r1[195] = new ThemeDescription(this.bottomOverlayChatText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_fieldOverlayText);
        r1[196] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_serviceText);
        r1[197] = new ThemeDescription(this.progressBar, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_chat_serviceText);
        r1[198] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{ChatUnreadCell.class}, new String[]{"backgroundLayout"}, null, null, null, Theme.key_chat_unreadMessagesStartBackground);
        r1[199] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{ChatUnreadCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_chat_unreadMessagesStartArrowIcon);
        r1[Callback.DEFAULT_DRAG_ANIMATION_DURATION] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ChatUnreadCell.class}, new String[]{"textView"}, null, null, null, Theme.key_chat_unreadMessagesStartText);
        r1[201] = new ThemeDescription(this.progressView2, ThemeDescription.FLAG_SERVICEBACKGROUND, null, null, null, null, Theme.key_chat_serviceBackground);
        r1[202] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_SERVICEBACKGROUND, null, null, null, null, Theme.key_chat_serviceBackground);
        r1[203] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_SERVICEBACKGROUND, new Class[]{ChatLoadingCell.class}, new String[]{"textView"}, null, null, null, Theme.key_chat_serviceBackground);
        r1[204] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{ChatLoadingCell.class}, new String[]{"textView"}, null, null, null, Theme.key_chat_serviceText);
        r1[205] = new ThemeDescription(this.avatarContainer != null ? r0.avatarContainer.getTimeItem() : null, 0, null, null, null, null, Theme.key_chat_secretTimerBackground);
        if (r0.avatarContainer != null) {
            view = r0.avatarContainer.getTimeItem();
        }
        r1[206] = new ThemeDescription(view, 0, null, null, null, null, Theme.key_chat_secretTimerText);
        return r1;
    }
}
