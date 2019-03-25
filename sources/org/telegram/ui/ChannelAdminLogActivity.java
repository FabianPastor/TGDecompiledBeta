package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.TextureView;
import android.view.View;
import android.view.View.MeasureSpec;
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
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DataQuery;
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
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.LinearSmoothScrollerMiddle;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.State;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
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
import org.telegram.tgnet.TLRPC.TL_pollAnswer;
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
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate;
import org.telegram.ui.Cells.ChatLoadingCell;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.ChatMessageCell$ChatMessageCellDelegate$$CC;
import org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate;
import org.telegram.ui.Cells.ChatUnreadCell;
import org.telegram.ui.Components.AdminLogFilterAlert;
import org.telegram.ui.Components.ChatAvatarContainer;
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PipRoundVideoView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
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
    private PhotoViewerProvider provider = new EmptyPhotoViewerProvider() {
        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int index, boolean needPreview) {
            int count = ChannelAdminLogActivity.this.chatListView.getChildCount();
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
                        } else if (fileLocation != null && message.photoThumbs != null) {
                            for (int b = 0; b < message.photoThumbs.size(); b++) {
                                PhotoSize photoSize = (PhotoSize) message.photoThumbs.get(b);
                                if (photoSize.location.volume_id == fileLocation.volume_id && photoSize.location.local_id == fileLocation.local_id) {
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
                    object.viewY = coords[1] - (VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight);
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
    };
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
    private String searchQuery = "";
    private ImageView searchUpButton;
    private boolean searchWas;
    private SparseArray<User> selectedAdmins;
    private MessageObject selectedObject;
    private TextureView videoTextureView;
    private boolean wasPaused = false;

    public class ChatActivityAdapter extends Adapter {
        private int loadingUpRow;
        private Context mContext;
        private int messagesEndRow;
        private int messagesStartRow;
        private int rowCount;

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
                chatMessageCell.setDelegate(new ChatMessageCellDelegate() {
                    public void didPressHiddenForward(ChatMessageCell chatMessageCell) {
                        ChatMessageCell$ChatMessageCellDelegate$$CC.didPressHiddenForward(this, chatMessageCell);
                    }

                    public void videoTimerReached() {
                        ChatMessageCell$ChatMessageCellDelegate$$CC.videoTimerReached(this);
                    }

                    public void didPressShare(ChatMessageCell cell) {
                        if (ChannelAdminLogActivity.this.getParentActivity() != null) {
                            ChannelAdminLogActivity channelAdminLogActivity = ChannelAdminLogActivity.this;
                            Context access$4100 = ChatActivityAdapter.this.mContext;
                            MessageObject messageObject = cell.getMessageObject();
                            boolean z = ChatObject.isChannel(ChannelAdminLogActivity.this.currentChat) && !ChannelAdminLogActivity.this.currentChat.megagroup && ChannelAdminLogActivity.this.currentChat.username != null && ChannelAdminLogActivity.this.currentChat.username.length() > 0;
                            channelAdminLogActivity.showDialog(ShareAlert.createShareAlert(access$4100, messageObject, null, z, null, false));
                        }
                    }

                    public boolean needPlayMessage(MessageObject messageObject) {
                        if (!messageObject.isVoice() && !messageObject.isRoundVideo()) {
                            return messageObject.isMusic() ? MediaController.getInstance().setPlaylist(ChannelAdminLogActivity.this.messages, messageObject) : false;
                        } else {
                            boolean result = MediaController.getInstance().playMessage(messageObject);
                            MediaController.getInstance().setVoiceMessagesPlaylist(null, false);
                            return result;
                        }
                    }

                    public void didPressChannelAvatar(ChatMessageCell cell, Chat chat, int postId) {
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

                    public void didPressOther(ChatMessageCell cell) {
                        ChannelAdminLogActivity.this.createMenu(cell);
                    }

                    public void didPressUserAvatar(ChatMessageCell cell, User user) {
                        if (user != null && user.id != UserConfig.getInstance(ChannelAdminLogActivity.this.currentAccount).getClientUserId()) {
                            Bundle args = new Bundle();
                            args.putInt("user_id", user.id);
                            ChannelAdminLogActivity.this.addCanBanUser(args, user.id);
                            ProfileActivity fragment = new ProfileActivity(args);
                            fragment.setPlayProfileAnimation(false);
                            ChannelAdminLogActivity.this.presentFragment(fragment);
                        }
                    }

                    public void didPressBotButton(ChatMessageCell cell, KeyboardButton button) {
                    }

                    public void didPressVoteButton(ChatMessageCell cell, TL_pollAnswer button) {
                    }

                    public void didPressCancelSendButton(ChatMessageCell cell) {
                    }

                    public void didLongPress(ChatMessageCell cell) {
                        ChannelAdminLogActivity.this.createMenu(cell);
                    }

                    public boolean canPerformActions() {
                        return true;
                    }

                    public void didPressUrl(MessageObject messageObject, CharacterStyle url, boolean longPress) {
                        if (url != null) {
                            if (url instanceof URLSpanMono) {
                                ((URLSpanMono) url).copyToClipboard();
                                Toast.makeText(ChannelAdminLogActivity.this.getParentActivity(), LocaleController.getString("TextCopied", NUM), 0).show();
                            } else if (url instanceof URLSpanUserMention) {
                                User user = MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).getUser(Utilities.parseInt(((URLSpanUserMention) url).getURL()));
                                if (user != null) {
                                    MessagesController.openChatOrProfileWith(user, null, ChannelAdminLogActivity.this, 0, false);
                                }
                            } else if (url instanceof URLSpanNoUnderline) {
                                String str = ((URLSpanNoUnderline) url).getURL();
                                if (str.startsWith("@")) {
                                    MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).openByUserName(str.substring(1), ChannelAdminLogActivity.this, 0);
                                } else if (str.startsWith("#")) {
                                    DialogsActivity fragment = new DialogsActivity(null);
                                    fragment.setSearchString(str);
                                    ChannelAdminLogActivity.this.presentFragment(fragment);
                                }
                            } else {
                                String urlFinal = ((URLSpan) url).getURL();
                                if (longPress) {
                                    Builder builder = new Builder(ChannelAdminLogActivity.this.getParentActivity());
                                    builder.setTitle(urlFinal);
                                    builder.setItems(new CharSequence[]{LocaleController.getString("Open", NUM), LocaleController.getString("Copy", NUM)}, new ChannelAdminLogActivity$ChatActivityAdapter$1$$Lambda$0(this, urlFinal));
                                    ChannelAdminLogActivity.this.showDialog(builder.create());
                                } else if (url instanceof URLSpanReplacement) {
                                    ChannelAdminLogActivity.this.showOpenUrlAlert(((URLSpanReplacement) url).getURL(), true);
                                } else if (url instanceof URLSpan) {
                                    if (!(!(messageObject.messageOwner.media instanceof TL_messageMediaWebPage) || messageObject.messageOwner.media.webpage == null || messageObject.messageOwner.media.webpage.cached_page == null)) {
                                        String lowerUrl = urlFinal.toLowerCase();
                                        String lowerUrl2 = messageObject.messageOwner.media.webpage.url.toLowerCase();
                                        if ((lowerUrl.contains("telegra.ph") || lowerUrl.contains("t.me/iv")) && (lowerUrl.contains(lowerUrl2) || lowerUrl2.contains(lowerUrl))) {
                                            ArticleViewer.getInstance().setParentActivity(ChannelAdminLogActivity.this.getParentActivity(), ChannelAdminLogActivity.this);
                                            ArticleViewer.getInstance().open(messageObject);
                                            return;
                                        }
                                    }
                                    Browser.openUrl(ChannelAdminLogActivity.this.getParentActivity(), urlFinal, true);
                                } else if (url instanceof ClickableSpan) {
                                    ((ClickableSpan) url).onClick(ChannelAdminLogActivity.this.fragmentView);
                                }
                            }
                        }
                    }

                    /* Access modifiers changed, original: final|synthetic */
                    public final /* synthetic */ void lambda$didPressUrl$0$ChannelAdminLogActivity$ChatActivityAdapter$1(String urlFinal, DialogInterface dialog, int which) {
                        if (which == 0) {
                            Browser.openUrl(ChannelAdminLogActivity.this.getParentActivity(), urlFinal, true);
                        } else if (which == 1) {
                            String url1 = urlFinal;
                            if (url1.startsWith("mailto:")) {
                                url1 = url1.substring(7);
                            } else if (url1.startsWith("tel:")) {
                                url1 = url1.substring(4);
                            }
                            AndroidUtilities.addToClipboard(url1);
                        }
                    }

                    public void needOpenWebView(String url, String title, String description, String originalUrl, int w, int h) {
                        EmbedBottomSheet.show(ChatActivityAdapter.this.mContext, title, description, originalUrl, url, w, h);
                    }

                    public void didPressReplyMessage(ChatMessageCell cell, int id) {
                    }

                    public void didPressViaBot(ChatMessageCell cell, String username) {
                    }

                    public void didPressImage(ChatMessageCell cell) {
                        MessageObject message = cell.getMessageObject();
                        File f;
                        if (message.type == 13) {
                            ChannelAdminLogActivity.this.showDialog(new StickersAlert(ChannelAdminLogActivity.this.getParentActivity(), ChannelAdminLogActivity.this, message.getInputStickerSet(), null, null));
                        } else if (message.isVideo() || message.type == 1 || ((message.type == 0 && !message.isWebpageDocument()) || message.isGif())) {
                            PhotoViewer.getInstance().setParentActivity(ChannelAdminLogActivity.this.getParentActivity());
                            PhotoViewer.getInstance().openPhoto(message, 0, 0, ChannelAdminLogActivity.this.provider);
                        } else if (message.type == 3) {
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
                                    intent.setDataAndType(FileProvider.getUriForFile(ChannelAdminLogActivity.this.getParentActivity(), "org.telegram.messenger.provider", f), "video/mp4");
                                } else {
                                    intent.setDataAndType(Uri.fromFile(f), "video/mp4");
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
                            }
                        } else if (message.type == 9 || message.type == 0) {
                            if (message.getDocumentName().toLowerCase().endsWith("attheme")) {
                                File locFile = null;
                                if (!(message.messageOwner.attachPath == null || message.messageOwner.attachPath.length() == 0)) {
                                    f = new File(message.messageOwner.attachPath);
                                    if (f.exists()) {
                                        locFile = f;
                                    }
                                }
                                if (locFile == null) {
                                    f = FileLoader.getPathToMessage(message.messageOwner);
                                    if (f.exists()) {
                                        locFile = f;
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
                                ThemeInfo themeInfo = Theme.applyThemeFile(locFile, message.getDocumentName(), true);
                                if (themeInfo != null) {
                                    ChannelAdminLogActivity.this.presentFragment(new ThemePreviewActivity(locFile, themeInfo));
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

                    public void didPressInstantButton(ChatMessageCell cell, int type) {
                        MessageObject messageObject = cell.getMessageObject();
                        if (type == 0) {
                            if (messageObject.messageOwner.media != null && messageObject.messageOwner.media.webpage != null && messageObject.messageOwner.media.webpage.cached_page != null) {
                                ArticleViewer.getInstance().setParentActivity(ChannelAdminLogActivity.this.getParentActivity(), ChannelAdminLogActivity.this);
                                ArticleViewer.getInstance().open(messageObject);
                            }
                        } else if (type == 5) {
                            ChannelAdminLogActivity.this.openVCard(messageObject.messageOwner.media.vcard, messageObject.messageOwner.media.first_name, messageObject.messageOwner.media.last_name);
                        } else if (messageObject.messageOwner.media != null && messageObject.messageOwner.media.webpage != null) {
                            Browser.openUrl(ChannelAdminLogActivity.this.getParentActivity(), messageObject.messageOwner.media.webpage.url);
                        }
                    }

                    public boolean isChatAdminCell(int uid) {
                        return false;
                    }
                });
                chatMessageCell.setAllowAssistant(true);
            } else if (viewType == 1) {
                view = new ChatActionCell(this.mContext);
                ((ChatActionCell) view).setDelegate(new ChatActionCellDelegate() {
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
                });
            } else if (viewType == 2) {
                view = new ChatUnreadCell(this.mContext);
            } else if (viewType == 3) {
                view = new BotHelpCell(this.mContext);
                ((BotHelpCell) view).setDelegate(new ChannelAdminLogActivity$ChatActivityAdapter$$Lambda$0(this));
            } else if (viewType == 4) {
                view = new ChatLoadingCell(this.mContext);
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$onCreateViewHolder$0$ChannelAdminLogActivity$ChatActivityAdapter(String url) {
            if (url.startsWith("@")) {
                MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).openByUserName(url.substring(1), ChannelAdminLogActivity.this, 0);
            } else if (url.startsWith("#")) {
                DialogsActivity fragment = new DialogsActivity(null);
                fragment.setSearchString(url);
                ChannelAdminLogActivity.this.presentFragment(fragment);
            }
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            if (position == this.loadingUpRow) {
                boolean z;
                ChatLoadingCell loadingCell = holder.itemView;
                if (ChannelAdminLogActivity.this.loadsCount > 1) {
                    z = true;
                } else {
                    z = false;
                }
                loadingCell.setProgressVisible(z);
            } else if (position >= this.messagesStartRow && position < this.messagesEndRow) {
                MessageObject message = (MessageObject) ChannelAdminLogActivity.this.messages.get((ChannelAdminLogActivity.this.messages.size() - (position - this.messagesStartRow)) - 1);
                View view = holder.itemView;
                if (view instanceof ChatMessageCell) {
                    boolean pinnedBotton;
                    boolean pinnedTop;
                    ChatMessageCell messageCell = (ChatMessageCell) view;
                    messageCell.isChat = true;
                    int nextType = getItemViewType(position + 1);
                    int prevType = getItemViewType(position - 1);
                    if ((message.messageOwner.reply_markup instanceof TL_replyInlineMarkup) || nextType != holder.getItemViewType()) {
                        pinnedBotton = false;
                    } else {
                        MessageObject nextMessage = (MessageObject) ChannelAdminLogActivity.this.messages.get((ChannelAdminLogActivity.this.messages.size() - ((position + 1) - this.messagesStartRow)) - 1);
                        pinnedBotton = nextMessage.isOutOwner() == message.isOutOwner() && nextMessage.messageOwner.from_id == message.messageOwner.from_id && Math.abs(nextMessage.messageOwner.date - message.messageOwner.date) <= 300;
                    }
                    if (prevType == holder.getItemViewType()) {
                        MessageObject prevMessage = (MessageObject) ChannelAdminLogActivity.this.messages.get(ChannelAdminLogActivity.this.messages.size() - (position - this.messagesStartRow));
                        pinnedTop = !(prevMessage.messageOwner.reply_markup instanceof TL_replyInlineMarkup) && prevMessage.isOutOwner() == message.isOutOwner() && prevMessage.messageOwner.from_id == message.messageOwner.from_id && Math.abs(prevMessage.messageOwner.date - message.messageOwner.date) <= 300;
                    } else {
                        pinnedTop = false;
                    }
                    messageCell.setMessageObject(message, null, pinnedBotton, pinnedTop);
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
            boolean z = true;
            if (holder.itemView instanceof ChatMessageCell) {
                boolean z2;
                final ChatMessageCell messageCell = holder.itemView;
                MessageObject message = messageCell.getMessageObject();
                messageCell.setBackgroundDrawable(null);
                if (null == null) {
                    z2 = true;
                } else {
                    z2 = false;
                }
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
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        public void notifyItemChanged(int position) {
            updateRows();
            try {
                super.notifyItemChanged(position);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        public void notifyItemRangeChanged(int positionStart, int itemCount) {
            updateRows();
            try {
                super.notifyItemRangeChanged(positionStart, itemCount);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        public void notifyItemInserted(int position) {
            updateRows();
            try {
                super.notifyItemInserted(position);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        public void notifyItemMoved(int fromPosition, int toPosition) {
            updateRows();
            try {
                super.notifyItemMoved(fromPosition, toPosition);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        public void notifyItemRangeInserted(int positionStart, int itemCount) {
            updateRows();
            try {
                super.notifyItemRangeInserted(positionStart, itemCount);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        public void notifyItemRemoved(int position) {
            updateRows();
            try {
                super.notifyItemRemoved(position);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        public void notifyItemRangeRemoved(int positionStart, int itemCount) {
            updateRows();
            try {
                super.notifyItemRangeRemoved(positionStart, itemCount);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public ChannelAdminLogActivity(Chat chat) {
        this.currentChat = chat;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidStart);
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
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
    }

    private void updateEmptyPlaceholder() {
        if (this.emptyView != null) {
            if (!TextUtils.isEmpty(this.searchQuery)) {
                this.emptyView.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(5.0f));
                this.emptyView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("EventLogEmptyTextSearch", NUM, this.searchQuery)));
            } else if (this.selectedAdmins == null && this.currentFilter == null) {
                this.emptyView.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
                if (this.currentChat.megagroup) {
                    this.emptyView.setText(AndroidUtilities.replaceTags(LocaleController.getString("EventLogEmpty", NUM)));
                } else {
                    this.emptyView.setText(AndroidUtilities.replaceTags(LocaleController.getString("EventLogEmptyChannel", NUM)));
                }
            } else {
                this.emptyView.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(5.0f));
                this.emptyView.setText(AndroidUtilities.replaceTags(LocaleController.getString("EventLogEmptySearch", NUM)));
            }
        }
    }

    private void loadMessages(boolean reset) {
        if (!this.loading) {
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
            req.q = this.searchQuery;
            req.limit = 50;
            if (reset || this.messages.isEmpty()) {
                req.max_id = 0;
            } else {
                req.max_id = this.minEventId;
            }
            req.min_id = 0;
            if (this.currentFilter != null) {
                req.flags |= 1;
                req.events_filter = this.currentFilter;
            }
            if (this.selectedAdmins != null) {
                req.flags |= 2;
                for (int a = 0; a < this.selectedAdmins.size(); a++) {
                    req.admins.add(MessagesController.getInstance(this.currentAccount).getInputUser((User) this.selectedAdmins.valueAt(a)));
                }
            }
            updateEmptyPlaceholder();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new ChannelAdminLogActivity$$Lambda$0(this));
            if (reset && this.chatAdapter != null) {
                this.chatAdapter.notifyDataSetChanged();
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$loadMessages$1$ChannelAdminLogActivity(TLObject response, TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new ChannelAdminLogActivity$$Lambda$14(this, (TL_channels_adminLogResults) response));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$0$ChannelAdminLogActivity(TL_channels_adminLogResults res) {
        MessagesController.getInstance(this.currentAccount).putUsers(res.users, false);
        MessagesController.getInstance(this.currentAccount).putChats(res.chats, false);
        boolean added = false;
        int oldRowsCount = this.messages.size();
        for (int a = 0; a < res.events.size(); a++) {
            TL_channelAdminLogEvent event = (TL_channelAdminLogEvent) res.events.get(a);
            if (this.messagesDict.indexOfKey(event.id) < 0) {
                this.minEventId = Math.min(this.minEventId, event.id);
                added = true;
                MessageObject messageObject = new MessageObject(this.currentAccount, event, this.messages, this.messagesByDays, this.currentChat, this.mid);
                if (messageObject.contentType >= 0) {
                    this.messagesDict.put(event.id, messageObject);
                }
            }
        }
        int newRowsCount = this.messages.size() - oldRowsCount;
        this.loading = false;
        if (!added) {
            this.endReached = true;
        }
        this.progressView.setVisibility(4);
        this.chatListView.setEmptyView(this.emptyViewContainer);
        if (newRowsCount != 0) {
            boolean end = false;
            if (this.endReached) {
                end = true;
                this.chatAdapter.notifyItemRangeChanged(0, 2);
            }
            int firstVisPos = this.chatLayoutManager.findLastVisibleItemPosition();
            View firstVisView = this.chatLayoutManager.findViewByPosition(firstVisPos);
            int top = (firstVisView == null ? 0 : firstVisView.getTop()) - this.chatListView.getPaddingTop();
            if (newRowsCount - (end ? 1 : 0) > 0) {
                int insertStart = (end ? 0 : 1) + 1;
                this.chatAdapter.notifyItemChanged(insertStart);
                this.chatAdapter.notifyItemRangeInserted(insertStart, newRowsCount - (end ? 1 : 0));
            }
            if (firstVisPos != -1) {
                this.chatLayoutManager.scrollToPositionWithOffset((firstVisPos + newRowsCount) - (end ? 1 : 0), top);
            }
        } else if (this.endReached) {
            this.chatAdapter.notifyItemRemoved(0);
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        int count;
        int a;
        View view;
        ChatMessageCell cell;
        if (id == NotificationCenter.emojiDidLoad) {
            if (this.chatListView != null) {
                this.chatListView.invalidateViews();
            }
        } else if (id == NotificationCenter.messagePlayingDidStart) {
            if (args[0].isRoundVideo()) {
                MediaController.getInstance().setTextureView(createTextureView(true), this.aspectRatioFrameLayout, this.roundVideoContainer, true);
                updateTextureViewPosition();
            }
            if (this.chatListView != null) {
                count = this.chatListView.getChildCount();
                for (a = 0; a < count; a++) {
                    view = this.chatListView.getChildAt(a);
                    if (view instanceof ChatMessageCell) {
                        cell = (ChatMessageCell) view;
                        MessageObject messageObject1 = cell.getMessageObject();
                        if (messageObject1 != null) {
                            if (messageObject1.isVoice() || messageObject1.isMusic()) {
                                cell.updateButtonState(false, true, false);
                            } else if (messageObject1.isRoundVideo()) {
                                cell.checkVideoPlayback(false);
                                if (!(MediaController.getInstance().isPlayingMessage(messageObject1) || messageObject1.audioProgress == 0.0f)) {
                                    messageObject1.resetPlayingProgress();
                                    cell.invalidate();
                                }
                            }
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.messagePlayingDidReset || id == NotificationCenter.messagePlayingPlayStateChanged) {
            if (this.chatListView != null) {
                count = this.chatListView.getChildCount();
                for (a = 0; a < count; a++) {
                    view = this.chatListView.getChildAt(a);
                    if (view instanceof ChatMessageCell) {
                        cell = (ChatMessageCell) view;
                        MessageObject messageObject = cell.getMessageObject();
                        if (messageObject != null) {
                            if (messageObject.isVoice() || messageObject.isMusic()) {
                                cell.updateButtonState(false, true, false);
                            } else if (messageObject.isRoundVideo() && !MediaController.getInstance().isPlayingMessage(messageObject)) {
                                cell.checkVideoPlayback(true);
                            }
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.messagePlayingProgressDidChanged) {
            Integer mid = args[0];
            if (this.chatListView != null) {
                count = this.chatListView.getChildCount();
                for (a = 0; a < count; a++) {
                    view = this.chatListView.getChildAt(a);
                    if (view instanceof ChatMessageCell) {
                        cell = (ChatMessageCell) view;
                        MessageObject playing = cell.getMessageObject();
                        if (playing != null && playing.getId() == mid.intValue()) {
                            MessageObject player = MediaController.getInstance().getPlayingMessageObject();
                            if (player != null) {
                                playing.audioProgress = player.audioProgress;
                                playing.audioProgressSec = player.audioProgressSec;
                                playing.audioPlayerDuration = player.audioPlayerDuration;
                                cell.updatePlayingMessageProgress();
                                return;
                            }
                            return;
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.didSetNewWallpapper && this.fragmentView != null) {
            this.contentView.setBackgroundImage(Theme.getCachedWallpaper(), Theme.isWallpaperMotion());
            this.progressView2.getBackground().setColorFilter(Theme.colorFilter);
            if (this.emptyView != null) {
                this.emptyView.getBackground().setColorFilter(Theme.colorFilter);
            }
            this.chatListView.invalidateViews();
        }
    }

    private void updateBottomOverlay() {
    }

    public View createView(Context context) {
        if (this.chatMessageCellsCache.isEmpty()) {
            for (int a = 0; a < 8; a++) {
                this.chatMessageCellsCache.add(new ChatMessageCell(context));
            }
        }
        this.searchWas = false;
        this.hasOwnBackground = true;
        Theme.createChatResources(context, false);
        this.actionBar.setAddToContainer(false);
        ActionBar actionBar = this.actionBar;
        boolean z = VERSION.SDK_INT >= 21 && !AndroidUtilities.isTablet();
        actionBar.setOccupyStatusBar(z);
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    ChannelAdminLogActivity.this.finishFragment();
                }
            }
        });
        this.avatarContainer = new ChatAvatarContainer(context, null, false);
        this.avatarContainer.setOccupyStatusBar(!AndroidUtilities.isTablet());
        this.actionBar.addView(this.avatarContainer, 0, LayoutHelper.createFrame(-2, -1.0f, 51, 56.0f, 0.0f, 40.0f, 0.0f));
        this.searchItem = this.actionBar.createMenu().addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItemSearchListener() {
            public void onSearchCollapse() {
                ChannelAdminLogActivity.this.searchQuery = "";
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
        });
        this.searchItem.setSearchFieldHint(LocaleController.getString("Search", NUM));
        this.avatarContainer.setEnabled(false);
        this.avatarContainer.setTitle(this.currentChat.title);
        this.avatarContainer.setSubtitle(LocaleController.getString("EventLogAllEvents", NUM));
        this.avatarContainer.setChatAvatar(this.currentChat);
        this.fragmentView = new SizeNotifierFrameLayout(context) {
            /* Access modifiers changed, original: protected */
            public void onAttachedToWindow() {
                super.onAttachedToWindow();
                MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
                if (messageObject != null && messageObject.isRoundVideo() && messageObject.eventId != 0 && messageObject.getDialogId() == ((long) (-ChannelAdminLogActivity.this.currentChat.id))) {
                    MediaController.getInstance().setTextureView(ChannelAdminLogActivity.this.createTextureView(false), ChannelAdminLogActivity.this.aspectRatioFrameLayout, ChannelAdminLogActivity.this.roundVideoContainer, true);
                }
            }

            /* Access modifiers changed, original: protected */
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                boolean result = super.drawChild(canvas, child, drawingTime);
                if (child == ChannelAdminLogActivity.this.actionBar && ChannelAdminLogActivity.this.parentLayout != null) {
                    ChannelAdminLogActivity.this.parentLayout.drawHeaderShadow(canvas, ChannelAdminLogActivity.this.actionBar.getVisibility() == 0 ? ChannelAdminLogActivity.this.actionBar.getMeasuredHeight() : 0);
                }
                return result;
            }

            /* Access modifiers changed, original: protected */
            public boolean isActionBarVisible() {
                return ChannelAdminLogActivity.this.actionBar.getVisibility() == 0;
            }

            /* Access modifiers changed, original: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
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
                    if (!(child == null || child.getVisibility() == 8 || child == ChannelAdminLogActivity.this.actionBar)) {
                        if (child == ChannelAdminLogActivity.this.chatListView || child == ChannelAdminLogActivity.this.progressView) {
                            child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), heightSize - AndroidUtilities.dp(50.0f)), NUM));
                        } else if (child == ChannelAdminLogActivity.this.emptyViewContainer) {
                            child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(heightSize, NUM));
                        } else {
                            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                        }
                    }
                }
            }

            /* Access modifiers changed, original: protected */
            public void onLayout(boolean changed, int l, int t, int r, int b) {
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
                        switch ((gravity & 7) & 7) {
                            case 1:
                                childLeft = ((((r - l) - width) / 2) + lp.leftMargin) - lp.rightMargin;
                                break;
                            case 5:
                                childLeft = (r - width) - lp.rightMargin;
                                break;
                            default:
                                childLeft = lp.leftMargin;
                                break;
                        }
                        switch (verticalGravity) {
                            case 16:
                                childTop = ((((b - t) - height) / 2) + lp.topMargin) - lp.bottomMargin;
                                break;
                            case 48:
                                childTop = lp.topMargin + getPaddingTop();
                                if (child != ChannelAdminLogActivity.this.actionBar && ChannelAdminLogActivity.this.actionBar.getVisibility() == 0) {
                                    childTop += ChannelAdminLogActivity.this.actionBar.getMeasuredHeight();
                                    break;
                                }
                            case 80:
                                childTop = ((b - t) - height) - lp.bottomMargin;
                                break;
                            default:
                                childTop = lp.topMargin;
                                break;
                        }
                        if (child == ChannelAdminLogActivity.this.emptyViewContainer) {
                            childTop -= AndroidUtilities.dp(24.0f) - (ChannelAdminLogActivity.this.actionBar.getVisibility() == 0 ? ChannelAdminLogActivity.this.actionBar.getMeasuredHeight() / 2 : 0);
                        } else if (child == ChannelAdminLogActivity.this.actionBar) {
                            childTop -= getPaddingTop();
                        }
                        child.layout(childLeft, childTop, childLeft + width, childTop + height);
                    }
                }
                ChannelAdminLogActivity.this.updateMessagesVisisblePart();
                notifyHeightChanged();
            }
        };
        this.contentView = (SizeNotifierFrameLayout) this.fragmentView;
        this.contentView.setOccupyStatusBar(!AndroidUtilities.isTablet());
        this.contentView.setBackgroundImage(Theme.getCachedWallpaper(), Theme.isWallpaperMotion());
        this.emptyViewContainer = new FrameLayout(context);
        this.emptyViewContainer.setVisibility(4);
        this.contentView.addView(this.emptyViewContainer, LayoutHelper.createFrame(-1, -2, 17));
        this.emptyViewContainer.setOnTouchListener(ChannelAdminLogActivity$$Lambda$1.$instance);
        this.emptyView = new TextView(context);
        this.emptyView.setTextSize(1, 14.0f);
        this.emptyView.setGravity(17);
        this.emptyView.setTextColor(Theme.getColor("chat_serviceText"));
        this.emptyView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(10.0f), Theme.getServiceMessageColor()));
        this.emptyView.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
        this.emptyViewContainer.addView(this.emptyView, LayoutHelper.createFrame(-2, -2.0f, 17, 16.0f, 0.0f, 16.0f, 0.0f));
        this.chatListView = new RecyclerListView(context) {
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
        this.chatListView.setOnItemClickListener(new ChannelAdminLogActivity$$Lambda$2(this));
        this.chatListView.setTag(Integer.valueOf(1));
        this.chatListView.setVerticalScrollBarEnabled(true);
        RecyclerListView recyclerListView = this.chatListView;
        ChatActivityAdapter chatActivityAdapter = new ChatActivityAdapter(context);
        this.chatAdapter = chatActivityAdapter;
        recyclerListView.setAdapter(chatActivityAdapter);
        this.chatListView.setClipToPadding(false);
        this.chatListView.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(3.0f));
        this.chatListView.setItemAnimator(null);
        this.chatListView.setLayoutAnimation(null);
        this.chatLayoutManager = new LinearLayoutManager(context) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }

            public void smoothScrollToPosition(RecyclerView recyclerView, State state, int position) {
                LinearSmoothScrollerMiddle linearSmoothScroller = new LinearSmoothScrollerMiddle(recyclerView.getContext());
                linearSmoothScroller.setTargetPosition(position);
                startSmoothScroll(linearSmoothScroller);
            }
        };
        this.chatLayoutManager.setOrientation(1);
        this.chatLayoutManager.setStackFromEnd(true);
        this.chatListView.setLayoutManager(this.chatLayoutManager);
        this.contentView.addView(this.chatListView, LayoutHelper.createFrame(-1, -1.0f));
        this.chatListView.setOnScrollListener(new OnScrollListener() {
            private final int scrollValue = AndroidUtilities.dp(100.0f);
            private float totalDy = 0.0f;

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
                    AnimatorSet access$3300 = ChannelAdminLogActivity.this.floatingDateAnimation;
                    Animator[] animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(ChannelAdminLogActivity.this.floatingDateView, "alpha", new float[]{1.0f});
                    access$3300.playTogether(animatorArr);
                    ChannelAdminLogActivity.this.floatingDateAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (animation.equals(ChannelAdminLogActivity.this.floatingDateAnimation)) {
                                ChannelAdminLogActivity.this.floatingDateAnimation = null;
                            }
                        }
                    });
                    ChannelAdminLogActivity.this.floatingDateAnimation.start();
                }
                ChannelAdminLogActivity.this.checkScrollForLoad(true);
                ChannelAdminLogActivity.this.updateMessagesVisisblePart();
            }
        });
        if (this.scrollToPositionOnRecreate != -1) {
            this.chatLayoutManager.scrollToPositionWithOffset(this.scrollToPositionOnRecreate, this.scrollToOffsetOnRecreate);
            this.scrollToPositionOnRecreate = -1;
        }
        this.progressView = new FrameLayout(context);
        this.progressView.setVisibility(4);
        this.contentView.addView(this.progressView, LayoutHelper.createFrame(-1, -1, 51));
        this.progressView2 = new View(context);
        this.progressView2.setBackgroundResource(NUM);
        this.progressView2.getBackground().setColorFilter(Theme.colorFilter);
        this.progressView.addView(this.progressView2, LayoutHelper.createFrame(36, 36, 17));
        this.progressBar = new RadialProgressView(context);
        this.progressBar.setSize(AndroidUtilities.dp(28.0f));
        this.progressBar.setProgressColor(Theme.getColor("chat_serviceText"));
        this.progressView.addView(this.progressBar, LayoutHelper.createFrame(32, 32, 17));
        this.floatingDateView = new ChatActionCell(context);
        this.floatingDateView.setAlpha(0.0f);
        this.contentView.addView(this.floatingDateView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 4.0f, 0.0f, 0.0f));
        this.contentView.addView(this.actionBar);
        this.bottomOverlayChat = new FrameLayout(context) {
            public void onDraw(Canvas canvas) {
                int bottom = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), bottom);
                Theme.chat_composeShadowDrawable.draw(canvas);
                canvas.drawRect(0.0f, (float) bottom, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
            }
        };
        this.bottomOverlayChat.setWillNotDraw(false);
        this.bottomOverlayChat.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
        this.contentView.addView(this.bottomOverlayChat, LayoutHelper.createFrame(-1, 51, 80));
        this.bottomOverlayChat.setOnClickListener(new ChannelAdminLogActivity$$Lambda$3(this));
        this.bottomOverlayChatText = new TextView(context);
        this.bottomOverlayChatText.setTextSize(1, 15.0f);
        this.bottomOverlayChatText.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.bottomOverlayChatText.setTextColor(Theme.getColor("chat_fieldOverlayText"));
        this.bottomOverlayChatText.setText(LocaleController.getString("SETTINGS", NUM).toUpperCase());
        this.bottomOverlayChat.addView(this.bottomOverlayChatText, LayoutHelper.createFrame(-2, -2, 17));
        this.bottomOverlayImage = new ImageView(context);
        this.bottomOverlayImage.setImageResource(NUM);
        this.bottomOverlayImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_fieldOverlayText"), Mode.MULTIPLY));
        this.bottomOverlayImage.setScaleType(ScaleType.CENTER);
        this.bottomOverlayChat.addView(this.bottomOverlayImage, LayoutHelper.createFrame(48, 48.0f, 53, 3.0f, 0.0f, 0.0f, 0.0f));
        this.bottomOverlayImage.setContentDescription(LocaleController.getString("BotHelp", NUM));
        this.bottomOverlayImage.setOnClickListener(new ChannelAdminLogActivity$$Lambda$4(this));
        this.searchContainer = new FrameLayout(context) {
            public void onDraw(Canvas canvas) {
                int bottom = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), bottom);
                Theme.chat_composeShadowDrawable.draw(canvas);
                canvas.drawRect(0.0f, (float) bottom, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
            }
        };
        this.searchContainer.setWillNotDraw(false);
        this.searchContainer.setVisibility(4);
        this.searchContainer.setFocusable(true);
        this.searchContainer.setFocusableInTouchMode(true);
        this.searchContainer.setClickable(true);
        this.searchContainer.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
        this.contentView.addView(this.searchContainer, LayoutHelper.createFrame(-1, 51, 80));
        this.searchCalendarButton = new ImageView(context);
        this.searchCalendarButton.setScaleType(ScaleType.CENTER);
        this.searchCalendarButton.setImageResource(NUM);
        this.searchCalendarButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_searchPanelIcons"), Mode.MULTIPLY));
        this.searchContainer.addView(this.searchCalendarButton, LayoutHelper.createFrame(48, 48, 53));
        this.searchCalendarButton.setOnClickListener(new ChannelAdminLogActivity$$Lambda$5(this));
        this.searchCountText = new SimpleTextView(context);
        this.searchCountText.setTextColor(Theme.getColor("chat_searchPanelText"));
        this.searchCountText.setTextSize(15);
        this.searchCountText.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.searchContainer.addView(this.searchCountText, LayoutHelper.createFrame(-1, -2.0f, 19, 108.0f, 0.0f, 0.0f, 0.0f));
        this.chatAdapter.updateRows();
        if (this.loading && this.messages.isEmpty()) {
            this.progressView.setVisibility(0);
            this.chatListView.setEmptyView(null);
        } else {
            this.progressView.setVisibility(4);
            this.chatListView.setEmptyView(this.emptyViewContainer);
        }
        updateEmptyPlaceholder();
        return this.fragmentView;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createView$3$ChannelAdminLogActivity(View view, int position) {
        createMenu(view);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createView$5$ChannelAdminLogActivity(View view) {
        if (getParentActivity() != null) {
            AdminLogFilterAlert adminLogFilterAlert = new AdminLogFilterAlert(getParentActivity(), this.currentFilter, this.selectedAdmins, this.currentChat.megagroup);
            adminLogFilterAlert.setCurrentAdmins(this.admins);
            adminLogFilterAlert.setAdminLogFilterAlertDelegate(new ChannelAdminLogActivity$$Lambda$13(this));
            showDialog(adminLogFilterAlert);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$4$ChannelAdminLogActivity(TL_channelAdminLogEventsFilter filter, SparseArray admins) {
        this.currentFilter = filter;
        this.selectedAdmins = admins;
        if (this.currentFilter == null && this.selectedAdmins == null) {
            this.avatarContainer.setSubtitle(LocaleController.getString("EventLogAllEvents", NUM));
        } else {
            this.avatarContainer.setSubtitle(LocaleController.getString("EventLogSelectedEvents", NUM));
        }
        loadMessages(true);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createView$6$ChannelAdminLogActivity(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        if (this.currentChat.megagroup) {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("EventLogInfoDetail", NUM)));
        } else {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("EventLogInfoDetailChannel", NUM)));
        }
        builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
        builder.setTitle(LocaleController.getString("EventLogInfoTitle", NUM));
        showDialog(builder.create());
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createView$10$ChannelAdminLogActivity(View view) {
        if (getParentActivity() != null) {
            AndroidUtilities.hideKeyboard(this.searchItem.getSearchField());
            Calendar calendar = Calendar.getInstance();
            try {
                DatePickerDialog dialog = new DatePickerDialog(getParentActivity(), new ChannelAdminLogActivity$$Lambda$10(this), calendar.get(1), calendar.get(2), calendar.get(5));
                DatePicker datePicker = dialog.getDatePicker();
                datePicker.setMinDate(1375315200000L);
                datePicker.setMaxDate(System.currentTimeMillis());
                dialog.setButton(-1, LocaleController.getString("JumpToDate", NUM), dialog);
                dialog.setButton(-2, LocaleController.getString("Cancel", NUM), ChannelAdminLogActivity$$Lambda$11.$instance);
                if (VERSION.SDK_INT >= 21) {
                    dialog.setOnShowListener(new ChannelAdminLogActivity$$Lambda$12(datePicker));
                }
                showDialog(dialog);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$7$ChannelAdminLogActivity(DatePicker view1, int year1, int month, int dayOfMonth1) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.clear();
        calendar1.set(year1, month, dayOfMonth1);
        int date = (int) (calendar1.getTime().getTime() / 1000);
        loadMessages(true);
    }

    static final /* synthetic */ void lambda$null$8$ChannelAdminLogActivity(DialogInterface dialog12, int which) {
    }

    static final /* synthetic */ void lambda$null$9$ChannelAdminLogActivity(DatePicker datePicker, DialogInterface dialog1) {
        int count = datePicker.getChildCount();
        for (int a = 0; a < count; a++) {
            View child = datePicker.getChildAt(a);
            ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
            layoutParams.width = -1;
            child.setLayoutParams(layoutParams);
        }
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
                ArrayList<Integer> options = new ArrayList();
                if (this.selectedObject.type == 0 || this.selectedObject.caption != null) {
                    items.add(LocaleController.getString("Copy", NUM));
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
                        items.add(LocaleController.getString("SaveToGIFs", NUM));
                        options.add(Integer.valueOf(11));
                    }
                } else if (type == 4) {
                    if (this.selectedObject.isVideo()) {
                        items.add(LocaleController.getString("SaveToGallery", NUM));
                        options.add(Integer.valueOf(4));
                        items.add(LocaleController.getString("ShareFile", NUM));
                        options.add(Integer.valueOf(6));
                    } else if (this.selectedObject.isMusic()) {
                        items.add(LocaleController.getString("SaveToMusic", NUM));
                        options.add(Integer.valueOf(10));
                        items.add(LocaleController.getString("ShareFile", NUM));
                        options.add(Integer.valueOf(6));
                    } else if (this.selectedObject.getDocument() != null) {
                        if (MessageObject.isNewGifDocument(this.selectedObject.getDocument())) {
                            items.add(LocaleController.getString("SaveToGIFs", NUM));
                            options.add(Integer.valueOf(11));
                        }
                        items.add(LocaleController.getString("SaveToDownloads", NUM));
                        options.add(Integer.valueOf(10));
                        items.add(LocaleController.getString("ShareFile", NUM));
                        options.add(Integer.valueOf(6));
                    } else {
                        items.add(LocaleController.getString("SaveToGallery", NUM));
                        options.add(Integer.valueOf(4));
                    }
                } else if (type == 5) {
                    items.add(LocaleController.getString("ApplyLocalizationFile", NUM));
                    options.add(Integer.valueOf(5));
                    items.add(LocaleController.getString("SaveToDownloads", NUM));
                    options.add(Integer.valueOf(10));
                    items.add(LocaleController.getString("ShareFile", NUM));
                    options.add(Integer.valueOf(6));
                } else if (type == 10) {
                    items.add(LocaleController.getString("ApplyThemeFile", NUM));
                    options.add(Integer.valueOf(5));
                    items.add(LocaleController.getString("SaveToDownloads", NUM));
                    options.add(Integer.valueOf(10));
                    items.add(LocaleController.getString("ShareFile", NUM));
                    options.add(Integer.valueOf(6));
                } else if (type == 6) {
                    items.add(LocaleController.getString("SaveToGallery", NUM));
                    options.add(Integer.valueOf(7));
                    items.add(LocaleController.getString("SaveToDownloads", NUM));
                    options.add(Integer.valueOf(10));
                    items.add(LocaleController.getString("ShareFile", NUM));
                    options.add(Integer.valueOf(6));
                } else if (type == 7) {
                    if (this.selectedObject.isMask()) {
                        items.add(LocaleController.getString("AddToMasks", NUM));
                    } else {
                        items.add(LocaleController.getString("AddToStickers", NUM));
                    }
                    options.add(Integer.valueOf(9));
                } else if (type == 8) {
                    User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.selectedObject.messageOwner.media.user_id));
                    if (!(user == null || user.id == UserConfig.getInstance(this.currentAccount).getClientUserId() || ContactsController.getInstance(this.currentAccount).contactsDict.get(Integer.valueOf(user.id)) != null)) {
                        items.add(LocaleController.getString("AddContactTitle", NUM));
                        options.add(Integer.valueOf(15));
                    }
                    if (!(this.selectedObject.messageOwner.media.phone_number == null && this.selectedObject.messageOwner.media.phone_number.length() == 0)) {
                        items.add(LocaleController.getString("Copy", NUM));
                        options.add(Integer.valueOf(16));
                        items.add(LocaleController.getString("Call", NUM));
                        options.add(Integer.valueOf(17));
                    }
                }
                if (!options.isEmpty()) {
                    builder.setItems((CharSequence[]) items.toArray(new CharSequence[items.size()]), new ChannelAdminLogActivity$$Lambda$6(this, options));
                    builder.setTitle(LocaleController.getString("Message", NUM));
                    showDialog(builder.create());
                }
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createMenu$11$ChannelAdminLogActivity(ArrayList options, DialogInterface dialogInterface, int i) {
        if (this.selectedObject != null && i >= 0 && i < options.size()) {
            processSelectedOption(((Integer) options.get(i)).intValue());
        }
    }

    private String getMessageContent(MessageObject messageObject, int previousUid, boolean name) {
        String str = "";
        if (name && previousUid != messageObject.messageOwner.from_id) {
            if (messageObject.messageOwner.from_id > 0) {
                User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(messageObject.messageOwner.from_id));
                if (user != null) {
                    str = ContactsController.formatName(user.first_name, user.last_name) + ":\n";
                }
            } else if (messageObject.messageOwner.from_id < 0) {
                Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-messageObject.messageOwner.from_id));
                if (chat != null) {
                    str = chat.title + ":\n";
                }
            }
        }
        if (messageObject.type == 0 && messageObject.messageOwner.message != null) {
            return str + messageObject.messageOwner.message;
        }
        if (messageObject.messageOwner.media == null || messageObject.messageOwner.message == null) {
            return str + messageObject.messageText;
        }
        return str + messageObject.messageOwner.message;
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
                    /* Access modifiers changed, original: protected */
                    public void onSizeChanged(int w, int h, int oldw, int oldh) {
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

                    /* Access modifiers changed, original: protected */
                    public void dispatchDraw(Canvas canvas) {
                        super.dispatchDraw(canvas);
                        canvas.drawPath(ChannelAdminLogActivity.this.aspectPath, ChannelAdminLogActivity.this.aspectPaint);
                    }
                };
                this.aspectPath = new Path();
                this.aspectPaint = new Paint(1);
                this.aspectPaint.setColor(-16777216);
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
        if (this.roundVideoContainer != null && this.roundVideoContainer.getParent() != null) {
            this.contentView.removeView(this.roundVideoContainer);
            this.aspectRatioFrameLayout.setDrawingReady(false);
            this.roundVideoContainer.setVisibility(4);
            if (VERSION.SDK_INT < 21) {
                this.roundVideoContainer.setLayerType(0, null);
            }
        }
    }

    private void processSelectedOption(int option) {
        if (this.selectedObject != null) {
            String path;
            Intent intent;
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
                            MediaController.saveFile(path, getParentActivity(), this.selectedObject.type == 3 ? 1 : 0, null, null);
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
                        AlertDialog.Builder builder;
                        if (!locFile.getName().toLowerCase().endsWith("attheme")) {
                            if (!LocaleController.getInstance().applyLanguageFile(locFile, this.currentAccount)) {
                                if (getParentActivity() != null) {
                                    builder = new AlertDialog.Builder(getParentActivity());
                                    builder.setTitle(LocaleController.getString("AppName", NUM));
                                    builder.setMessage(LocaleController.getString("IncorrectLocalization", NUM));
                                    builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
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
                        if (themeInfo == null) {
                            this.scrollToPositionOnRecreate = -1;
                            if (getParentActivity() != null) {
                                builder = new AlertDialog.Builder(getParentActivity());
                                builder.setTitle(LocaleController.getString("AppName", NUM));
                                builder.setMessage(LocaleController.getString("IncorrectTheme", NUM));
                                builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
                                showDialog(builder.create());
                                break;
                            }
                            this.selectedObject = null;
                            return;
                        }
                        presentFragment(new ThemePreviewActivity(locFile, themeInfo));
                        break;
                    }
                    break;
                case 6:
                    path = this.selectedObject.messageOwner.attachPath;
                    if (!(path == null || path.length() <= 0 || new File(path).exists())) {
                        path = null;
                    }
                    if (path == null || path.length() == 0) {
                        path = FileLoader.getPathToMessage(this.selectedObject.messageOwner).toString();
                    }
                    intent = new Intent("android.intent.action.SEND");
                    intent.setType(this.selectedObject.getDocument().mime_type);
                    if (VERSION.SDK_INT >= 24) {
                        try {
                            intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(getParentActivity(), "org.telegram.messenger.provider", new File(path)));
                            intent.setFlags(1);
                        } catch (Exception e) {
                            intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(new File(path)));
                        }
                    } else {
                        intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(new File(path)));
                    }
                    getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString("ShareFile", NUM)), 500);
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
                    break;
                case 9:
                    showDialog(new StickersAlert(getParentActivity(), this, this.selectedObject.getInputStickerSet(), null, null));
                    break;
                case 10:
                    if (VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                        String fileName = FileLoader.getDocumentFileName(this.selectedObject.getDocument());
                        if (TextUtils.isEmpty(fileName)) {
                            fileName = this.selectedObject.getFileName();
                        }
                        path = this.selectedObject.messageOwner.attachPath;
                        if (!(path == null || path.length() <= 0 || new File(path).exists())) {
                            path = null;
                        }
                        if (path == null || path.length() == 0) {
                            path = FileLoader.getPathToMessage(this.selectedObject.messageOwner).toString();
                        }
                        MediaController.saveFile(path, getParentActivity(), this.selectedObject.isMusic() ? 3 : 2, fileName, this.selectedObject.getDocument() != null ? this.selectedObject.getDocument().mime_type : "");
                        break;
                    }
                    getParentActivity().requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 4);
                    this.selectedObject = null;
                    return;
                case 11:
                    MessagesController.getInstance(this.currentAccount).saveGif(this.selectedObject, this.selectedObject.getDocument());
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
                        intent = new Intent("android.intent.action.DIAL", Uri.parse("tel:" + this.selectedObject.messageOwner.media.phone_number));
                        intent.addFlags(NUM);
                        getParentActivity().startActivityForResult(intent, 500);
                        break;
                    } catch (Exception e2) {
                        FileLog.e(e2);
                        break;
                    }
            }
            this.selectedObject = null;
        }
    }

    private int getMessageType(MessageObject messageObject) {
        if (messageObject == null || messageObject.type == 6) {
            return -1;
        }
        if (messageObject.type == 10 || messageObject.type == 11 || messageObject.type == 16) {
            if (messageObject.getId() != 0) {
                return 1;
            }
            return -1;
        } else if (messageObject.isVoice()) {
            return 2;
        } else {
            if (messageObject.isSticker()) {
                InputStickerSet inputStickerSet = messageObject.getInputStickerSet();
                if (inputStickerSet instanceof TL_inputStickerSetID) {
                    if (!DataQuery.getInstance(this.currentAccount).isStickerPackInstalled(inputStickerSet.id)) {
                        return 7;
                    }
                } else if ((inputStickerSet instanceof TL_inputStickerSetShortName) && !DataQuery.getInstance(this.currentAccount).isStickerPackInstalled(inputStickerSet.short_name)) {
                    return 7;
                }
            } else if ((!messageObject.isRoundVideo() || (messageObject.isRoundVideo() && BuildVars.DEBUG_VERSION)) && ((messageObject.messageOwner.media instanceof TL_messageMediaPhoto) || messageObject.getDocument() != null || messageObject.isMusic() || messageObject.isVideo())) {
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
            } else if (messageObject.type == 12) {
                return 8;
            } else {
                if (messageObject.isMediaEmpty()) {
                    return 3;
                }
            }
            return 2;
        }
    }

    private void loadAdmins() {
        TL_channels_getParticipants req = new TL_channels_getParticipants();
        req.channel = MessagesController.getInputChannel(this.currentChat);
        req.filter = new TL_channelParticipantsAdmins();
        req.offset = 0;
        req.limit = 200;
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new ChannelAdminLogActivity$$Lambda$7(this)), this.classGuid);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$loadAdmins$13$ChannelAdminLogActivity(TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new ChannelAdminLogActivity$$Lambda$9(this, error, response));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$12$ChannelAdminLogActivity(TL_error error, TLObject response) {
        if (error == null) {
            TL_channels_channelParticipants res = (TL_channels_channelParticipants) response;
            MessagesController.getInstance(this.currentAccount).putUsers(res.users, false);
            this.admins = res.participants;
            if (this.visibleDialog instanceof AdminLogFilterAlert) {
                ((AdminLogFilterAlert) this.visibleDialog).setCurrentAdmins(this.admins);
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public void onRemoveFromParent() {
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
        if (this.chatLayoutManager != null && !this.paused) {
            int firstVisibleItem = this.chatLayoutManager.findFirstVisibleItemPosition();
            if ((firstVisibleItem == -1 ? 0 : Math.abs(this.chatLayoutManager.findLastVisibleItemPosition() - firstVisibleItem) + 1) > 0) {
                int checkLoadCount;
                int totalItemCount = this.chatAdapter.getItemCount();
                if (scroll) {
                    checkLoadCount = 25;
                } else {
                    checkLoadCount = 5;
                }
                if (firstVisibleItem <= checkLoadCount && !this.loading && !this.endReached) {
                    loadMessages(false);
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
        MessageObject messageObject;
        boolean foundTextureViewMessage = false;
        int count = this.chatListView.getChildCount();
        for (int a = 0; a < count; a++) {
            View view = this.chatListView.getChildAt(a);
            if (view instanceof ChatMessageCell) {
                ChatMessageCell messageCell = (ChatMessageCell) view;
                messageObject = messageCell.getMessageObject();
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
            messageObject = MediaController.getInstance().getPlayingMessageObject();
            if (foundTextureViewMessage) {
                MediaController.getInstance().setCurrentVideoVisible(true);
                return;
            }
            this.roundVideoContainer.setTranslationY((float) ((-AndroidUtilities.roundMessageSize) - 100));
            this.fragmentView.invalidate();
            if (messageObject != null && messageObject.isRoundVideo()) {
                if (this.checkTextureViewPosition || PipRoundVideoView.getInstance() != null) {
                    MediaController.getInstance().setCurrentVideoVisible(false);
                }
            }
        }
    }

    private void updateMessagesVisisblePart() {
        if (this.chatListView != null) {
            MessageObject messageObject;
            int count = this.chatListView.getChildCount();
            int height = this.chatListView.getMeasuredHeight();
            int minPositionHolder = Integer.MAX_VALUE;
            int minPositionDateHolder = Integer.MAX_VALUE;
            View minDateChild = null;
            View minChild = null;
            View minMessageChild = null;
            boolean foundTextureViewMessage = false;
            for (int a = 0; a < count; a++) {
                View view = this.chatListView.getChildAt(a);
                if (view instanceof ChatMessageCell) {
                    int viewTop;
                    ChatMessageCell messageCell = (ChatMessageCell) view;
                    int top = messageCell.getTop();
                    int bottom = messageCell.getBottom();
                    if (top >= 0) {
                        viewTop = 0;
                    } else {
                        viewTop = -top;
                    }
                    int viewBottom = messageCell.getMeasuredHeight();
                    if (viewBottom > height) {
                        viewBottom = viewTop + height;
                    }
                    messageCell.setVisiblePart(viewTop, viewBottom - viewTop);
                    messageObject = messageCell.getMessageObject();
                    if (this.roundVideoContainer != null && messageObject.isRoundVideo() && MediaController.getInstance().isPlayingMessage(messageObject)) {
                        ImageReceiver imageReceiver = messageCell.getPhotoImage();
                        this.roundVideoContainer.setTranslationX((float) imageReceiver.getImageX());
                        this.roundVideoContainer.setTranslationY((float) ((this.fragmentView.getPaddingTop() + top) + imageReceiver.getImageY()));
                        this.fragmentView.invalidate();
                        this.roundVideoContainer.invalidate();
                        foundTextureViewMessage = true;
                    }
                }
                if (view.getBottom() > this.chatListView.getPaddingTop()) {
                    int position = view.getBottom();
                    if (position < minPositionHolder) {
                        minPositionHolder = position;
                        if ((view instanceof ChatMessageCell) || (view instanceof ChatActionCell)) {
                            minMessageChild = view;
                        }
                        minChild = view;
                    }
                    if ((view instanceof ChatActionCell) && ((ChatActionCell) view).getMessageObject().isDateObject) {
                        if (view.getAlpha() != 1.0f) {
                            view.setAlpha(1.0f);
                        }
                        if (position < minPositionDateHolder) {
                            minPositionDateHolder = position;
                            minDateChild = view;
                        }
                    }
                }
            }
            if (this.roundVideoContainer != null) {
                if (foundTextureViewMessage) {
                    MediaController.getInstance().setCurrentVideoVisible(true);
                } else {
                    this.roundVideoContainer.setTranslationY((float) ((-AndroidUtilities.roundMessageSize) - 100));
                    this.fragmentView.invalidate();
                    messageObject = MediaController.getInstance().getPlayingMessageObject();
                    if (messageObject != null && messageObject.isRoundVideo() && this.checkTextureViewPosition) {
                        MediaController.getInstance().setCurrentVideoVisible(false);
                    }
                }
            }
            if (minMessageChild != null) {
                if (minMessageChild instanceof ChatMessageCell) {
                    messageObject = ((ChatMessageCell) minMessageChild).getMessageObject();
                } else {
                    messageObject = ((ChatActionCell) minMessageChild).getMessageObject();
                }
                this.floatingDateView.setCustomDate(messageObject.messageOwner.date);
            }
            this.currentFloatingDateOnScreen = false;
            boolean z = ((minChild instanceof ChatMessageCell) || (minChild instanceof ChatActionCell)) ? false : true;
            this.currentFloatingTopIsNotMessage = z;
            if (minDateChild != null) {
                if (minDateChild.getTop() > this.chatListView.getPaddingTop() || this.currentFloatingTopIsNotMessage) {
                    if (minDateChild.getAlpha() != 1.0f) {
                        minDateChild.setAlpha(1.0f);
                    }
                    if (this.currentFloatingTopIsNotMessage) {
                        z = false;
                    } else {
                        z = true;
                    }
                    hideFloatingDateView(z);
                } else {
                    if (minDateChild.getAlpha() != 0.0f) {
                        minDateChild.setAlpha(0.0f);
                    }
                    if (this.floatingDateAnimation != null) {
                        this.floatingDateAnimation.cancel();
                        this.floatingDateAnimation = null;
                    }
                    if (this.floatingDateView.getTag() == null) {
                        this.floatingDateView.setTag(Integer.valueOf(1));
                    }
                    if (this.floatingDateView.getAlpha() != 1.0f) {
                        this.floatingDateView.setAlpha(1.0f);
                    }
                    this.currentFloatingDateOnScreen = true;
                }
                int offset = minDateChild.getBottom() - this.chatListView.getPaddingTop();
                if (offset <= this.floatingDateView.getMeasuredHeight() || offset >= this.floatingDateView.getMeasuredHeight() * 2) {
                    this.floatingDateView.setTranslationY(0.0f);
                    return;
                } else {
                    this.floatingDateView.setTranslationY((float) (((-this.floatingDateView.getMeasuredHeight()) * 2) + offset));
                    return;
                }
            }
            hideFloatingDateView(true);
            this.floatingDateView.setTranslationY(0.0f);
        }
    }

    public void onTransitionAnimationStart(boolean isOpen, boolean backward) {
        NotificationCenter.getInstance(this.currentAccount).setAllowedNotificationsDutingAnimation(new int[]{NotificationCenter.chatInfoDidLoad, NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.messagesDidLoad, NotificationCenter.botKeyboardDidLoad});
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
        if (this.contentView != null) {
            this.contentView.onResume();
        }
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
        if (this.contentView != null) {
            this.contentView.onPause();
        }
        this.paused = true;
        this.wasPaused = true;
    }

    public void openVCard(String vcard, String first_name, String last_name) {
        try {
            File f = new File(FileLoader.getDirectory(4), "sharing/");
            f.mkdirs();
            File f2 = new File(f, "vcard.vcf");
            BufferedWriter writer = new BufferedWriter(new FileWriter(f2));
            writer.write(vcard);
            writer.close();
            presentFragment(new PhonebookShareActivity(null, null, f2, ContactsController.formatName(first_name, last_name)));
        } catch (Exception e) {
            FileLog.e(e);
        }
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
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
            if (message.type == 3) {
                builder.setMessage(LocaleController.getString("NoPlayerInstalled", NUM));
            } else {
                builder.setMessage(LocaleController.formatString("NoHandleAppInstalled", NUM, message.getDocument().mime_type));
            }
            showDialog(builder.create());
        }
    }

    public Chat getCurrentChat() {
        return this.currentChat;
    }

    private void addCanBanUser(Bundle bundle, int uid) {
        if (this.currentChat.megagroup && this.admins != null && ChatObject.canBlockUsers(this.currentChat)) {
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

    public void showOpenUrlAlert(String url, boolean ask) {
        if (Browser.isInternalUrl(url, null) || !ask) {
            Browser.openUrl(getParentActivity(), url, true);
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setMessage(LocaleController.formatString("OpenUrlAlert", NUM, url));
        builder.setPositiveButton(LocaleController.getString("Open", NUM), new ChannelAdminLogActivity$$Lambda$8(this, url));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
        showDialog(builder.create());
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$showOpenUrlAlert$14$ChannelAdminLogActivity(String url, DialogInterface dialogInterface, int i) {
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
        r9 = new ThemeDescription[205];
        r9[11] = new ThemeDescription(this.avatarContainer.getSubtitleTextView(), ThemeDescription.FLAG_TEXTCOLOR, null, new Paint[]{Theme.chat_statusPaint, Theme.chat_statusRecordPaint}, null, null, "actionBarDefaultSubtitle", null);
        r9[12] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        r9[13] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, "avatar_text");
        r9[14] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "avatar_backgroundRed");
        r9[15] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "avatar_backgroundOrange");
        r9[16] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "avatar_backgroundViolet");
        r9[17] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "avatar_backgroundGreen");
        r9[18] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "avatar_backgroundCyan");
        r9[19] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "avatar_backgroundBlue");
        r9[20] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "avatar_backgroundPink");
        r9[21] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "avatar_nameInMessageRed");
        r9[22] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "avatar_nameInMessageOrange");
        r9[23] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "avatar_nameInMessageViolet");
        r9[24] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "avatar_nameInMessageGreen");
        r9[25] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "avatar_nameInMessageCyan");
        r9[26] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "avatar_nameInMessageBlue");
        r9[27] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "avatar_nameInMessagePink");
        r9[28] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, null, "chat_inBubble");
        r9[29] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, null, "chat_inBubbleSelected");
        r9[30] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInShadowDrawable, Theme.chat_msgInMediaShadowDrawable}, null, "chat_inBubbleShadow");
        r9[31] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, "chat_outBubble");
        r9[32] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, null, "chat_outBubbleSelected");
        r9[33] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutShadowDrawable, Theme.chat_msgOutMediaShadowDrawable}, null, "chat_outBubbleShadow");
        r9[34] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ChatActionCell.class}, Theme.chat_actionTextPaint, null, null, "chat_serviceText");
        r9[35] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{ChatActionCell.class}, Theme.chat_actionTextPaint, null, null, "chat_serviceLink");
        r9[36] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_shareIconDrawable, Theme.chat_botInlineDrawable, Theme.chat_botLinkDrawalbe, Theme.chat_goIconDrawable}, null, "chat_serviceIcon");
        r9[37] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class, ChatActionCell.class}, null, null, null, "chat_serviceBackground");
        r9[38] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class, ChatActionCell.class}, null, null, null, "chat_serviceBackgroundSelected");
        r9[39] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_messageTextIn");
        r9[40] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_messageTextOut");
        r9[41] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{ChatMessageCell.class}, null, null, null, "chat_messageLinkIn", null);
        r9[42] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{ChatMessageCell.class}, null, null, null, "chat_messageLinkOut", null);
        r9[43] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckDrawable, Theme.chat_msgOutHalfCheckDrawable}, null, "chat_outSentCheck");
        r9[44] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, null, "chat_outSentCheckSelected");
        r9[45] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutClockDrawable}, null, "chat_outSentClock");
        r9[46] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutSelectedClockDrawable}, null, "chat_outSentClockSelected");
        r9[47] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInClockDrawable}, null, "chat_inSentClock");
        r9[48] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInSelectedClockDrawable}, null, "chat_inSentClockSelected");
        r9[49] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, null, "chat_mediaSentCheck");
        r9[50] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgStickerHalfCheckDrawable, Theme.chat_msgStickerCheckDrawable, Theme.chat_msgStickerClockDrawable, Theme.chat_msgStickerViewsDrawable}, null, "chat_serviceText");
        r9[51] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgMediaClockDrawable}, null, "chat_mediaSentClock");
        r9[52] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutViewsDrawable}, null, "chat_outViews");
        r9[53] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutViewsSelectedDrawable}, null, "chat_outViewsSelected");
        r9[54] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInViewsDrawable}, null, "chat_inViews");
        r9[55] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInViewsSelectedDrawable}, null, "chat_inViewsSelected");
        r9[56] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgMediaViewsDrawable}, null, "chat_mediaViews");
        r9[57] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutMenuDrawable}, null, "chat_outMenu");
        r9[58] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutMenuSelectedDrawable}, null, "chat_outMenuSelected");
        r9[59] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInMenuDrawable}, null, "chat_inMenu");
        r9[60] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInMenuSelectedDrawable}, null, "chat_inMenuSelected");
        r9[61] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgMediaMenuDrawable}, null, "chat_mediaMenu");
        r9[62] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutInstantDrawable, Theme.chat_msgOutCallDrawable}, null, "chat_outInstant");
        r9[63] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCallSelectedDrawable}, null, "chat_outInstantSelected");
        r9[64] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInInstantDrawable, Theme.chat_msgInCallDrawable}, null, "chat_inInstant");
        r9[65] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInCallSelectedDrawable}, null, "chat_inInstantSelected");
        r9[66] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgCallUpRedDrawable, Theme.chat_msgCallDownRedDrawable}, null, "calls_callReceivedRedIcon");
        r9[67] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgCallUpGreenDrawable, Theme.chat_msgCallDownGreenDrawable}, null, "calls_callReceivedGreenIcon");
        r9[68] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_msgErrorPaint, null, null, "chat_sentError");
        r9[69] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgErrorDrawable}, null, "chat_sentErrorIcon");
        r9[70] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_durationPaint, null, null, "chat_previewDurationText");
        r9[71] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_gamePaint, null, null, "chat_previewGameText");
        r9[72] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inPreviewInstantText");
        r9[73] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outPreviewInstantText");
        r9[74] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inPreviewInstantSelectedText");
        r9[75] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outPreviewInstantSelectedText");
        r9[76] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_deleteProgressPaint, null, null, "chat_secretTimeText");
        r9[77] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_stickerNameText");
        r9[78] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_botButtonPaint, null, null, "chat_botButtonText");
        r9[79] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_botProgressPaint, null, null, "chat_botProgress");
        r9[80] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inForwardedNameText");
        r9[81] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outForwardedNameText");
        r9[82] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inViaBotNameText");
        r9[83] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outViaBotNameText");
        r9[84] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_stickerViaBotNameText");
        r9[85] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inReplyLine");
        r9[86] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outReplyLine");
        r9[87] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_stickerReplyLine");
        r9[88] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inReplyNameText");
        r9[89] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outReplyNameText");
        r9[90] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_stickerReplyNameText");
        r9[91] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inReplyMessageText");
        r9[92] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outReplyMessageText");
        r9[93] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inReplyMediaMessageText");
        r9[94] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outReplyMediaMessageText");
        r9[95] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inReplyMediaMessageSelectedText");
        r9[96] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outReplyMediaMessageSelectedText");
        r9[97] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_stickerReplyMessageText");
        r9[98] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inPreviewLine");
        r9[99] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outPreviewLine");
        r9[100] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inSiteNameText");
        r9[101] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outSiteNameText");
        r9[102] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inContactNameText");
        r9[103] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outContactNameText");
        r9[104] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inContactPhoneText");
        r9[105] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outContactPhoneText");
        r9[106] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_mediaProgress");
        r9[107] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inAudioProgress");
        r9[108] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outAudioProgress");
        r9[109] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inAudioSelectedProgress");
        r9[110] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outAudioSelectedProgress");
        r9[111] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_mediaTimeText");
        r9[112] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inTimeText");
        r9[113] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outTimeText");
        r9[114] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inTimeSelectedText");
        r9[115] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outTimeSelectedText");
        r9[116] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inAudioPerfomerText");
        r9[117] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outAudioPerfomerText");
        r9[118] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inAudioTitleText");
        r9[119] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outAudioTitleText");
        r9[120] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inAudioDurationText");
        r9[121] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outAudioDurationText");
        r9[122] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inAudioDurationSelectedText");
        r9[123] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outAudioDurationSelectedText");
        r9[124] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inAudioSeekbar");
        r9[125] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outAudioSeekbar");
        r9[126] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inAudioSeekbarSelected");
        r9[127] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outAudioSeekbarSelected");
        r9[128] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inAudioSeekbarFill");
        r9[129] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inAudioCacheSeekbar");
        r9[130] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outAudioSeekbarFill");
        r9[131] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outAudioCacheSeekbar");
        r9[132] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inVoiceSeekbar");
        r9[133] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outVoiceSeekbar");
        r9[134] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inVoiceSeekbarSelected");
        r9[135] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outVoiceSeekbarSelected");
        r9[136] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inVoiceSeekbarFill");
        r9[137] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outVoiceSeekbarFill");
        r9[138] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inFileProgress");
        r9[139] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outFileProgress");
        r9[140] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inFileProgressSelected");
        r9[141] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outFileProgressSelected");
        r9[142] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inFileNameText");
        r9[143] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outFileNameText");
        r9[144] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inFileInfoText");
        r9[145] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outFileInfoText");
        r9[146] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inFileInfoSelectedText");
        r9[147] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outFileInfoSelectedText");
        r9[148] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inFileBackground");
        r9[149] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outFileBackground");
        r9[150] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inFileBackgroundSelected");
        r9[151] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outFileBackgroundSelected");
        r9[152] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inVenueInfoText");
        r9[153] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outVenueInfoText");
        r9[154] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inVenueInfoSelectedText");
        r9[155] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outVenueInfoSelectedText");
        r9[156] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_mediaInfoText");
        r9[157] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_urlPaint, null, null, "chat_linkSelectBackground");
        r9[158] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_textSearchSelectionPaint, null, null, "chat_textSelectBackground");
        r9[159] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outLoader");
        r9[160] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outMediaIcon");
        r9[161] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outLoaderSelected");
        r9[162] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outMediaIconSelected");
        r9[163] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inLoader");
        r9[164] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inMediaIcon");
        r9[165] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inLoaderSelected");
        r9[166] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inMediaIconSelected");
        r9[167] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[0][0], Theme.chat_photoStatesDrawables[1][0], Theme.chat_photoStatesDrawables[2][0], Theme.chat_photoStatesDrawables[3][0]}, null, "chat_mediaLoaderPhoto");
        r9[168] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[0][0], Theme.chat_photoStatesDrawables[1][0], Theme.chat_photoStatesDrawables[2][0], Theme.chat_photoStatesDrawables[3][0]}, null, "chat_mediaLoaderPhotoIcon");
        r9[169] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[0][1], Theme.chat_photoStatesDrawables[1][1], Theme.chat_photoStatesDrawables[2][1], Theme.chat_photoStatesDrawables[3][1]}, null, "chat_mediaLoaderPhotoSelected");
        r9[170] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[0][1], Theme.chat_photoStatesDrawables[1][1], Theme.chat_photoStatesDrawables[2][1], Theme.chat_photoStatesDrawables[3][1]}, null, "chat_mediaLoaderPhotoIconSelected");
        r9[171] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[7][0], Theme.chat_photoStatesDrawables[8][0]}, null, "chat_outLoaderPhoto");
        r9[172] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[7][0], Theme.chat_photoStatesDrawables[8][0]}, null, "chat_outLoaderPhotoIcon");
        r9[173] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[7][1], Theme.chat_photoStatesDrawables[8][1]}, null, "chat_outLoaderPhotoSelected");
        r9[174] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[7][1], Theme.chat_photoStatesDrawables[8][1]}, null, "chat_outLoaderPhotoIconSelected");
        r9[175] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[10][0], Theme.chat_photoStatesDrawables[11][0]}, null, "chat_inLoaderPhoto");
        r9[176] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[10][0], Theme.chat_photoStatesDrawables[11][0]}, null, "chat_inLoaderPhotoIcon");
        r9[177] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[10][1], Theme.chat_photoStatesDrawables[11][1]}, null, "chat_inLoaderPhotoSelected");
        r9[178] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[10][1], Theme.chat_photoStatesDrawables[11][1]}, null, "chat_inLoaderPhotoIconSelected");
        r9[179] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[9][0]}, null, "chat_outFileIcon");
        r9[180] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[9][1]}, null, "chat_outFileSelectedIcon");
        r9[181] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[12][0]}, null, "chat_inFileIcon");
        r9[182] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[12][1]}, null, "chat_inFileSelectedIcon");
        r9[183] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_contactDrawable[0]}, null, "chat_inContactBackground");
        r9[184] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_contactDrawable[0]}, null, "chat_inContactIcon");
        r9[185] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_contactDrawable[1]}, null, "chat_outContactBackground");
        r9[186] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_contactDrawable[1]}, null, "chat_outContactIcon");
        r9[187] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_locationDrawable[0]}, null, "chat_inLocationBackground");
        r9[188] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_locationDrawable[0]}, null, "chat_inLocationIcon");
        r9[189] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_locationDrawable[1]}, null, "chat_outLocationBackground");
        r9[190] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_locationDrawable[1]}, null, "chat_outLocationIcon");
        r9[191] = new ThemeDescription(this.bottomOverlayChat, 0, null, Theme.chat_composeBackgroundPaint, null, null, "chat_messagePanelBackground");
        r9[192] = new ThemeDescription(this.bottomOverlayChat, 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, "chat_messagePanelShadow");
        r9[193] = new ThemeDescription(this.bottomOverlayChatText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "chat_fieldOverlayText");
        r9[194] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "chat_serviceText");
        r9[195] = new ThemeDescription(this.progressBar, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "chat_serviceText");
        r9[196] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{ChatUnreadCell.class}, new String[]{"backgroundLayout"}, null, null, null, "chat_unreadMessagesStartBackground");
        r9[197] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{ChatUnreadCell.class}, new String[]{"imageView"}, null, null, null, "chat_unreadMessagesStartArrowIcon");
        r9[198] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ChatUnreadCell.class}, new String[]{"textView"}, null, null, null, "chat_unreadMessagesStartText");
        r9[199] = new ThemeDescription(this.progressView2, ThemeDescription.FLAG_SERVICEBACKGROUND, null, null, null, null, "chat_serviceBackground");
        r9[200] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_SERVICEBACKGROUND, null, null, null, null, "chat_serviceBackground");
        r9[201] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_SERVICEBACKGROUND, new Class[]{ChatLoadingCell.class}, new String[]{"textView"}, null, null, null, "chat_serviceBackground");
        r9[202] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{ChatLoadingCell.class}, new String[]{"textView"}, null, null, null, "chat_serviceText");
        r9[203] = new ThemeDescription(this.avatarContainer != null ? this.avatarContainer.getTimeItem() : null, 0, null, null, null, null, "chat_secretTimerBackground");
        r9[204] = new ThemeDescription(this.avatarContainer != null ? this.avatarContainer.getTimeItem() : null, 0, null, null, null, null, "chat_secretTimerText");
        return r9;
    }
}
