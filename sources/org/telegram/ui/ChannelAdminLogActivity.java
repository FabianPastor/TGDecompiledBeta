package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.os.Build.VERSION;
import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScrollerMiddle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.LayoutParams;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.RecyclerView.State;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
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
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.ChannelAdminLogEventAction;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEvent;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeStickerSet;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantToggleAdmin;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventsFilter;
import org.telegram.tgnet.TLRPC.TL_channelParticipantCreator;
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
import org.telegram.tgnet.TLRPC.TL_reactionCount;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.WebPage;
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
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Cells.BotHelpCell;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate;
import org.telegram.ui.Cells.ChatLoadingCell;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate;
import org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate.-CC;
import org.telegram.ui.Cells.ChatUnreadCell;
import org.telegram.ui.Cells.TextSelectionHelper.ChatListTextSelectionHelper;
import org.telegram.ui.Components.AdminLogFilterAlert;
import org.telegram.ui.Components.ChatAvatarContainer;
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PhonebookShareAlert;
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
        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int i, boolean z) {
            FileLocation fileLocation2 = fileLocation;
            int childCount = ChannelAdminLogActivity.this.chatListView.getChildCount();
            int i2 = 0;
            int i3 = 0;
            while (true) {
                ImageReceiver imageReceiver = null;
                if (i3 >= childCount) {
                    return null;
                }
                View childAt = ChannelAdminLogActivity.this.chatListView.getChildAt(i3);
                MessageObject messageObject2;
                if (childAt instanceof ChatMessageCell) {
                    if (messageObject != null) {
                        ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                        messageObject2 = chatMessageCell.getMessageObject();
                        if (messageObject2 != null && messageObject2.getId() == messageObject.getId()) {
                            imageReceiver = chatMessageCell.getPhotoImage();
                        }
                    }
                } else if (childAt instanceof ChatActionCell) {
                    ChatActionCell chatActionCell = (ChatActionCell) childAt;
                    messageObject2 = chatActionCell.getMessageObject();
                    if (messageObject2 != null) {
                        if (messageObject != null) {
                            if (messageObject2.getId() == messageObject.getId()) {
                                imageReceiver = chatActionCell.getPhotoImage();
                            }
                        } else if (fileLocation2 != null && messageObject2.photoThumbs != null) {
                            for (int i4 = 0; i4 < messageObject2.photoThumbs.size(); i4++) {
                                FileLocation fileLocation3 = ((PhotoSize) messageObject2.photoThumbs.get(i4)).location;
                                if (fileLocation3.volume_id == fileLocation2.volume_id && fileLocation3.local_id == fileLocation2.local_id) {
                                    imageReceiver = chatActionCell.getPhotoImage();
                                    break;
                                }
                            }
                        }
                    }
                }
                if (imageReceiver != null) {
                    int[] iArr = new int[2];
                    childAt.getLocationInWindow(iArr);
                    PlaceProviderObject placeProviderObject = new PlaceProviderObject();
                    placeProviderObject.viewX = iArr[0];
                    int i5 = iArr[1];
                    if (VERSION.SDK_INT < 21) {
                        i2 = AndroidUtilities.statusBarHeight;
                    }
                    placeProviderObject.viewY = i5 - i2;
                    placeProviderObject.parentView = ChannelAdminLogActivity.this.chatListView;
                    placeProviderObject.imageReceiver = imageReceiver;
                    placeProviderObject.thumb = imageReceiver.getBitmapSafe();
                    placeProviderObject.radius = imageReceiver.getRoundRadius();
                    placeProviderObject.isEvent = true;
                    return placeProviderObject;
                }
                i3++;
            }
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

        public long getItemId(int i) {
            return -1;
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
            int i;
            if (ChannelAdminLogActivity.this.endReached) {
                this.loadingUpRow = -1;
            } else {
                i = this.rowCount;
                this.rowCount = i + 1;
                this.loadingUpRow = i;
            }
            i = this.rowCount;
            this.messagesStartRow = i;
            this.rowCount = i + ChannelAdminLogActivity.this.messages.size();
            this.messagesEndRow = this.rowCount;
        }

        public int getItemCount() {
            return this.rowCount;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View chatMessageCell;
            if (i == 0) {
                if (ChannelAdminLogActivity.this.chatMessageCellsCache.isEmpty()) {
                    chatMessageCell = new ChatMessageCell(this.mContext);
                } else {
                    chatMessageCell = (View) ChannelAdminLogActivity.this.chatMessageCellsCache.get(0);
                    ChannelAdminLogActivity.this.chatMessageCellsCache.remove(0);
                }
                ChatMessageCell chatMessageCell2 = (ChatMessageCell) chatMessageCell;
                chatMessageCell2.setDelegate(new ChatMessageCellDelegate() {
                    public boolean canPerformActions() {
                        return true;
                    }

                    public /* synthetic */ void didPressBotButton(ChatMessageCell chatMessageCell, KeyboardButton keyboardButton) {
                        -CC.$default$didPressBotButton(this, chatMessageCell, keyboardButton);
                    }

                    public void didPressCancelSendButton(ChatMessageCell chatMessageCell) {
                    }

                    public /* synthetic */ void didPressHiddenForward(ChatMessageCell chatMessageCell) {
                        -CC.$default$didPressHiddenForward(this, chatMessageCell);
                    }

                    public /* synthetic */ void didPressReaction(ChatMessageCell chatMessageCell, TL_reactionCount tL_reactionCount) {
                        -CC.$default$didPressReaction(this, chatMessageCell, tL_reactionCount);
                    }

                    public void didPressReplyMessage(ChatMessageCell chatMessageCell, int i) {
                    }

                    public void didPressViaBot(ChatMessageCell chatMessageCell, String str) {
                    }

                    public /* synthetic */ void didPressVoteButtons(ChatMessageCell chatMessageCell, ArrayList<TL_pollAnswer> arrayList, int i, int i2, int i3) {
                        -CC.$default$didPressVoteButtons(this, chatMessageCell, arrayList, i, i2, i3);
                    }

                    public /* synthetic */ void didStartVideoStream(MessageObject messageObject) {
                        -CC.$default$didStartVideoStream(this, messageObject);
                    }

                    public /* synthetic */ String getAdminRank(int i) {
                        return -CC.$default$getAdminRank(this, i);
                    }

                    public /* synthetic */ ChatListTextSelectionHelper getTextSelectionHelper() {
                        return -CC.$default$getTextSelectionHelper(this);
                    }

                    public /* synthetic */ boolean hasSelectedMessages() {
                        return -CC.$default$hasSelectedMessages(this);
                    }

                    public /* synthetic */ void setShouldNotRepeatSticker(MessageObject messageObject) {
                        -CC.$default$setShouldNotRepeatSticker(this, messageObject);
                    }

                    public /* synthetic */ boolean shouldRepeatSticker(MessageObject messageObject) {
                        return -CC.$default$shouldRepeatSticker(this, messageObject);
                    }

                    public /* synthetic */ void videoTimerReached() {
                        -CC.$default$videoTimerReached(this);
                    }

                    public void didPressShare(ChatMessageCell chatMessageCell) {
                        if (ChannelAdminLogActivity.this.getParentActivity() != null) {
                            ChatActivityAdapter chatActivityAdapter = ChatActivityAdapter.this;
                            ChannelAdminLogActivity channelAdminLogActivity = ChannelAdminLogActivity.this;
                            Context access$4100 = chatActivityAdapter.mContext;
                            MessageObject messageObject = chatMessageCell.getMessageObject();
                            boolean z = ChatObject.isChannel(ChannelAdminLogActivity.this.currentChat) && !ChannelAdminLogActivity.this.currentChat.megagroup;
                            channelAdminLogActivity.showDialog(ShareAlert.createShareAlert(access$4100, messageObject, null, z, null, false));
                        }
                    }

                    public boolean needPlayMessage(MessageObject messageObject) {
                        if (messageObject.isVoice() || messageObject.isRoundVideo()) {
                            boolean playMessage = MediaController.getInstance().playMessage(messageObject);
                            MediaController.getInstance().setVoiceMessagesPlaylist(null, false);
                            return playMessage;
                        } else if (messageObject.isMusic()) {
                            return MediaController.getInstance().setPlaylist(ChannelAdminLogActivity.this.messages, messageObject);
                        } else {
                            return false;
                        }
                    }

                    public void didPressChannelAvatar(ChatMessageCell chatMessageCell, Chat chat, int i, float f, float f2) {
                        if (chat != null && chat != ChannelAdminLogActivity.this.currentChat) {
                            Bundle bundle = new Bundle();
                            bundle.putInt("chat_id", chat.id);
                            if (i != 0) {
                                bundle.putInt("message_id", i);
                            }
                            if (MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).checkCanOpenChat(bundle, ChannelAdminLogActivity.this)) {
                                ChannelAdminLogActivity.this.presentFragment(new ChatActivity(bundle), true);
                            }
                        }
                    }

                    public void didPressOther(ChatMessageCell chatMessageCell, float f, float f2) {
                        ChannelAdminLogActivity.this.createMenu(chatMessageCell);
                    }

                    public void didPressUserAvatar(ChatMessageCell chatMessageCell, User user, float f, float f2) {
                        if (user != null && user.id != UserConfig.getInstance(ChannelAdminLogActivity.this.currentAccount).getClientUserId()) {
                            Bundle bundle = new Bundle();
                            bundle.putInt("user_id", user.id);
                            ChannelAdminLogActivity.this.addCanBanUser(bundle, user.id);
                            ProfileActivity profileActivity = new ProfileActivity(bundle);
                            profileActivity.setPlayProfileAnimation(false);
                            ChannelAdminLogActivity.this.presentFragment(profileActivity);
                        }
                    }

                    public void didLongPress(ChatMessageCell chatMessageCell, float f, float f2) {
                        ChannelAdminLogActivity.this.createMenu(chatMessageCell);
                    }

                    public void didPressUrl(ChatMessageCell chatMessageCell, CharacterStyle characterStyle, boolean z) {
                        if (characterStyle != null) {
                            MessageObject messageObject = chatMessageCell.getMessageObject();
                            if (characterStyle instanceof URLSpanMono) {
                                ((URLSpanMono) characterStyle).copyToClipboard();
                                Toast.makeText(ChannelAdminLogActivity.this.getParentActivity(), LocaleController.getString("TextCopied", NUM), 0).show();
                            } else if (characterStyle instanceof URLSpanUserMention) {
                                User user = MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).getUser(Utilities.parseInt(((URLSpanUserMention) characterStyle).getURL()));
                                if (user != null) {
                                    MessagesController.openChatOrProfileWith(user, null, ChannelAdminLogActivity.this, 0, false);
                                }
                            } else if (characterStyle instanceof URLSpanNoUnderline) {
                                String url = ((URLSpanNoUnderline) characterStyle).getURL();
                                if (url.startsWith("@")) {
                                    MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).openByUserName(url.substring(1), ChannelAdminLogActivity.this, 0);
                                } else if (url.startsWith("#")) {
                                    DialogsActivity dialogsActivity = new DialogsActivity(null);
                                    dialogsActivity.setSearchString(url);
                                    ChannelAdminLogActivity.this.presentFragment(dialogsActivity);
                                }
                            } else {
                                String url2 = ((URLSpan) characterStyle).getURL();
                                if (z) {
                                    Builder builder = new Builder(ChannelAdminLogActivity.this.getParentActivity());
                                    builder.setTitle(url2);
                                    builder.setItems(new CharSequence[]{LocaleController.getString("Open", NUM), LocaleController.getString("Copy", NUM)}, new -$$Lambda$ChannelAdminLogActivity$ChatActivityAdapter$1$D0xLcgtrHEslS6nrwCqImW0Sa1U(this, url2));
                                    ChannelAdminLogActivity.this.showDialog(builder.create());
                                } else if (characterStyle instanceof URLSpanReplacement) {
                                    ChannelAdminLogActivity.this.showOpenUrlAlert(((URLSpanReplacement) characterStyle).getURL(), true);
                                } else if (characterStyle instanceof URLSpan) {
                                    MessageMedia messageMedia = messageObject.messageOwner.media;
                                    if (messageMedia instanceof TL_messageMediaWebPage) {
                                        WebPage webPage = messageMedia.webpage;
                                        if (!(webPage == null || webPage.cached_page == null)) {
                                            String toLowerCase = url2.toLowerCase();
                                            String toLowerCase2 = messageObject.messageOwner.media.webpage.url.toLowerCase();
                                            if ((toLowerCase.contains("telegra.ph") || toLowerCase.contains("t.me/iv")) && (toLowerCase.contains(toLowerCase2) || toLowerCase2.contains(toLowerCase))) {
                                                ArticleViewer.getInstance().setParentActivity(ChannelAdminLogActivity.this.getParentActivity(), ChannelAdminLogActivity.this);
                                                ArticleViewer.getInstance().open(messageObject);
                                                return;
                                            }
                                        }
                                    }
                                    Browser.openUrl(ChannelAdminLogActivity.this.getParentActivity(), url2, true);
                                } else if (characterStyle instanceof ClickableSpan) {
                                    ((ClickableSpan) characterStyle).onClick(ChannelAdminLogActivity.this.fragmentView);
                                }
                            }
                        }
                    }

                    public /* synthetic */ void lambda$didPressUrl$0$ChannelAdminLogActivity$ChatActivityAdapter$1(String str, DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            Browser.openUrl(ChannelAdminLogActivity.this.getParentActivity(), str, true);
                        } else if (i == 1) {
                            CharSequence str2;
                            if (str2.startsWith("mailto:")) {
                                str2 = str2.substring(7);
                            } else if (str2.startsWith("tel:")) {
                                str2 = str2.substring(4);
                            }
                            AndroidUtilities.addToClipboard(str2);
                        }
                    }

                    public void needOpenWebView(String str, String str2, String str3, String str4, int i, int i2) {
                        EmbedBottomSheet.show(ChatActivityAdapter.this.mContext, str2, str3, str4, str, i, i2);
                    }

                    /* JADX WARNING: Missing block: B:49:0x0102, code skipped:
            if (r9.exists() != false) goto L_0x0106;
     */
                    public void didPressImage(org.telegram.ui.Cells.ChatMessageCell r9, float r10, float r11) {
                        /*
                        r8 = this;
                        r1 = r9.getMessageObject();
                        r9 = r1.type;
                        r10 = 13;
                        if (r9 != r10) goto L_0x0027;
                    L_0x000a:
                        r9 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;
                        r9 = org.telegram.ui.ChannelAdminLogActivity.this;
                        r10 = new org.telegram.ui.Components.StickersAlert;
                        r3 = r9.getParentActivity();
                        r11 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;
                        r4 = org.telegram.ui.ChannelAdminLogActivity.this;
                        r5 = r1.getInputStickerSet();
                        r6 = 0;
                        r7 = 0;
                        r2 = r10;
                        r2.<init>(r3, r4, r5, r6, r7);
                        r9.showDialog(r10);
                        goto L_0x01d3;
                    L_0x0027:
                        r9 = r1.isVideo();
                        if (r9 != 0) goto L_0x01b1;
                    L_0x002d:
                        r9 = r1.type;
                        r10 = 1;
                        if (r9 == r10) goto L_0x01b1;
                    L_0x0032:
                        if (r9 != 0) goto L_0x003a;
                    L_0x0034:
                        r9 = r1.isWebpageDocument();
                        if (r9 == 0) goto L_0x01b1;
                    L_0x003a:
                        r9 = r1.isGif();
                        if (r9 == 0) goto L_0x0042;
                    L_0x0040:
                        goto L_0x01b1;
                    L_0x0042:
                        r9 = r1.type;
                        r11 = 3;
                        r0 = 0;
                        if (r9 != r11) goto L_0x00b3;
                    L_0x0048:
                        r9 = r1.messageOwner;	 Catch:{ Exception -> 0x00aa }
                        r9 = r9.attachPath;	 Catch:{ Exception -> 0x00aa }
                        if (r9 == 0) goto L_0x0061;
                    L_0x004e:
                        r9 = r1.messageOwner;	 Catch:{ Exception -> 0x00aa }
                        r9 = r9.attachPath;	 Catch:{ Exception -> 0x00aa }
                        r9 = r9.length();	 Catch:{ Exception -> 0x00aa }
                        if (r9 == 0) goto L_0x0061;
                    L_0x0058:
                        r0 = new java.io.File;	 Catch:{ Exception -> 0x00aa }
                        r9 = r1.messageOwner;	 Catch:{ Exception -> 0x00aa }
                        r9 = r9.attachPath;	 Catch:{ Exception -> 0x00aa }
                        r0.<init>(r9);	 Catch:{ Exception -> 0x00aa }
                    L_0x0061:
                        if (r0 == 0) goto L_0x0069;
                    L_0x0063:
                        r9 = r0.exists();	 Catch:{ Exception -> 0x00aa }
                        if (r9 != 0) goto L_0x006f;
                    L_0x0069:
                        r9 = r1.messageOwner;	 Catch:{ Exception -> 0x00aa }
                        r0 = org.telegram.messenger.FileLoader.getPathToMessage(r9);	 Catch:{ Exception -> 0x00aa }
                    L_0x006f:
                        r9 = new android.content.Intent;	 Catch:{ Exception -> 0x00aa }
                        r11 = "android.intent.action.VIEW";
                        r9.<init>(r11);	 Catch:{ Exception -> 0x00aa }
                        r11 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x00aa }
                        r2 = 24;
                        r3 = "video/mp4";
                        if (r11 < r2) goto L_0x0094;
                    L_0x007f:
                        r9.setFlags(r10);	 Catch:{ Exception -> 0x00aa }
                        r10 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;	 Catch:{ Exception -> 0x00aa }
                        r10 = org.telegram.ui.ChannelAdminLogActivity.this;	 Catch:{ Exception -> 0x00aa }
                        r10 = r10.getParentActivity();	 Catch:{ Exception -> 0x00aa }
                        r11 = "org.telegram.messenger.beta.provider";
                        r10 = androidx.core.content.FileProvider.getUriForFile(r10, r11, r0);	 Catch:{ Exception -> 0x00aa }
                        r9.setDataAndType(r10, r3);	 Catch:{ Exception -> 0x00aa }
                        goto L_0x009b;
                    L_0x0094:
                        r10 = android.net.Uri.fromFile(r0);	 Catch:{ Exception -> 0x00aa }
                        r9.setDataAndType(r10, r3);	 Catch:{ Exception -> 0x00aa }
                    L_0x009b:
                        r10 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;	 Catch:{ Exception -> 0x00aa }
                        r10 = org.telegram.ui.ChannelAdminLogActivity.this;	 Catch:{ Exception -> 0x00aa }
                        r10 = r10.getParentActivity();	 Catch:{ Exception -> 0x00aa }
                        r11 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
                        r10.startActivityForResult(r9, r11);	 Catch:{ Exception -> 0x00aa }
                        goto L_0x01d3;
                    L_0x00aa:
                        r9 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;
                        r9 = org.telegram.ui.ChannelAdminLogActivity.this;
                        r9.alertUserOpenError(r1);
                        goto L_0x01d3;
                    L_0x00b3:
                        r11 = 4;
                        if (r9 != r11) goto L_0x00d3;
                    L_0x00b6:
                        r9 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;
                        r9 = org.telegram.ui.ChannelAdminLogActivity.this;
                        r9 = org.telegram.messenger.AndroidUtilities.isGoogleMapsInstalled(r9);
                        if (r9 != 0) goto L_0x00c1;
                    L_0x00c0:
                        return;
                    L_0x00c1:
                        r9 = new org.telegram.ui.LocationActivity;
                        r10 = 0;
                        r9.<init>(r10);
                        r9.setMessageObject(r1);
                        r10 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;
                        r10 = org.telegram.ui.ChannelAdminLogActivity.this;
                        r10.presentFragment(r9);
                        goto L_0x01d3;
                    L_0x00d3:
                        r11 = 9;
                        if (r9 == r11) goto L_0x00d9;
                    L_0x00d7:
                        if (r9 != 0) goto L_0x01d3;
                    L_0x00d9:
                        r9 = r1.getDocumentName();
                        r9 = r9.toLowerCase();
                        r11 = "attheme";
                        r9 = r9.endsWith(r11);
                        if (r9 == 0) goto L_0x019d;
                    L_0x00e9:
                        r9 = r1.messageOwner;
                        r9 = r9.attachPath;
                        if (r9 == 0) goto L_0x0105;
                    L_0x00ef:
                        r9 = r9.length();
                        if (r9 == 0) goto L_0x0105;
                    L_0x00f5:
                        r9 = new java.io.File;
                        r11 = r1.messageOwner;
                        r11 = r11.attachPath;
                        r9.<init>(r11);
                        r11 = r9.exists();
                        if (r11 == 0) goto L_0x0105;
                    L_0x0104:
                        goto L_0x0106;
                    L_0x0105:
                        r9 = r0;
                    L_0x0106:
                        if (r9 != 0) goto L_0x0115;
                    L_0x0108:
                        r11 = r1.messageOwner;
                        r11 = org.telegram.messenger.FileLoader.getPathToMessage(r11);
                        r2 = r11.exists();
                        if (r2 == 0) goto L_0x0115;
                    L_0x0114:
                        r9 = r11;
                    L_0x0115:
                        r11 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;
                        r11 = org.telegram.ui.ChannelAdminLogActivity.this;
                        r11 = r11.chatLayoutManager;
                        r2 = -1;
                        if (r11 == 0) goto L_0x017f;
                    L_0x0120:
                        r11 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;
                        r11 = org.telegram.ui.ChannelAdminLogActivity.this;
                        r11 = r11.chatLayoutManager;
                        r11 = r11.findLastVisibleItemPosition();
                        r3 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;
                        r3 = org.telegram.ui.ChannelAdminLogActivity.this;
                        r3 = r3.chatLayoutManager;
                        r3 = r3.getItemCount();
                        r3 = r3 - r10;
                        if (r11 >= r3) goto L_0x0178;
                    L_0x013b:
                        r11 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;
                        r11 = org.telegram.ui.ChannelAdminLogActivity.this;
                        r3 = r11.chatLayoutManager;
                        r3 = r3.findFirstVisibleItemPosition();
                        r11.scrollToPositionOnRecreate = r3;
                        r11 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;
                        r11 = org.telegram.ui.ChannelAdminLogActivity.this;
                        r11 = r11.chatListView;
                        r3 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;
                        r3 = org.telegram.ui.ChannelAdminLogActivity.this;
                        r3 = r3.scrollToPositionOnRecreate;
                        r11 = r11.findViewHolderForAdapterPosition(r3);
                        r11 = (org.telegram.ui.Components.RecyclerListView.Holder) r11;
                        if (r11 == 0) goto L_0x0170;
                    L_0x0162:
                        r3 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;
                        r3 = org.telegram.ui.ChannelAdminLogActivity.this;
                        r11 = r11.itemView;
                        r11 = r11.getTop();
                        r3.scrollToOffsetOnRecreate = r11;
                        goto L_0x017f;
                    L_0x0170:
                        r11 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;
                        r11 = org.telegram.ui.ChannelAdminLogActivity.this;
                        r11.scrollToPositionOnRecreate = r2;
                        goto L_0x017f;
                    L_0x0178:
                        r11 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;
                        r11 = org.telegram.ui.ChannelAdminLogActivity.this;
                        r11.scrollToPositionOnRecreate = r2;
                    L_0x017f:
                        r11 = r1.getDocumentName();
                        r9 = org.telegram.ui.ActionBar.Theme.applyThemeFile(r9, r11, r0, r10);
                        if (r9 == 0) goto L_0x0196;
                    L_0x0189:
                        r10 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;
                        r10 = org.telegram.ui.ChannelAdminLogActivity.this;
                        r11 = new org.telegram.ui.ThemePreviewActivity;
                        r11.<init>(r9);
                        r10.presentFragment(r11);
                        return;
                    L_0x0196:
                        r9 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;
                        r9 = org.telegram.ui.ChannelAdminLogActivity.this;
                        r9.scrollToPositionOnRecreate = r2;
                    L_0x019d:
                        r9 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;	 Catch:{ Exception -> 0x01a9 }
                        r9 = org.telegram.ui.ChannelAdminLogActivity.this;	 Catch:{ Exception -> 0x01a9 }
                        r9 = r9.getParentActivity();	 Catch:{ Exception -> 0x01a9 }
                        org.telegram.messenger.AndroidUtilities.openForView(r1, r9);	 Catch:{ Exception -> 0x01a9 }
                        goto L_0x01d3;
                    L_0x01a9:
                        r9 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;
                        r9 = org.telegram.ui.ChannelAdminLogActivity.this;
                        r9.alertUserOpenError(r1);
                        goto L_0x01d3;
                    L_0x01b1:
                        r9 = org.telegram.ui.PhotoViewer.getInstance();
                        r10 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;
                        r10 = org.telegram.ui.ChannelAdminLogActivity.this;
                        r10 = r10.getParentActivity();
                        r9.setParentActivity(r10);
                        r0 = org.telegram.ui.PhotoViewer.getInstance();
                        r2 = 0;
                        r4 = 0;
                        r9 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;
                        r9 = org.telegram.ui.ChannelAdminLogActivity.this;
                        r6 = r9.provider;
                        r0.openPhoto(r1, r2, r4, r6);
                    L_0x01d3:
                        return;
                        */
                        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter$AnonymousClass1.didPressImage(org.telegram.ui.Cells.ChatMessageCell, float, float):void");
                    }

                    public void didPressInstantButton(ChatMessageCell chatMessageCell, int i) {
                        MessageObject messageObject = chatMessageCell.getMessageObject();
                        MessageMedia messageMedia;
                        if (i == 0) {
                            messageMedia = messageObject.messageOwner.media;
                            if (messageMedia != null) {
                                WebPage webPage = messageMedia.webpage;
                                if (webPage != null && webPage.cached_page != null) {
                                    ArticleViewer.getInstance().setParentActivity(ChannelAdminLogActivity.this.getParentActivity(), ChannelAdminLogActivity.this);
                                    ArticleViewer.getInstance().open(messageObject);
                                }
                            }
                        } else if (i == 5) {
                            ChannelAdminLogActivity channelAdminLogActivity = ChannelAdminLogActivity.this;
                            User user = channelAdminLogActivity.getMessagesController().getUser(Integer.valueOf(messageObject.messageOwner.media.user_id));
                            MessageMedia messageMedia2 = messageObject.messageOwner.media;
                            channelAdminLogActivity.openVCard(user, messageMedia2.vcard, messageMedia2.first_name, messageMedia2.last_name);
                        } else {
                            messageMedia = messageObject.messageOwner.media;
                            if (messageMedia != null && messageMedia.webpage != null) {
                                Browser.openUrl(ChannelAdminLogActivity.this.getParentActivity(), messageObject.messageOwner.media.webpage.url);
                            }
                        }
                    }
                });
                chatMessageCell2.setAllowAssistant(true);
            } else if (i == 1) {
                chatMessageCell = new ChatActionCell(this.mContext);
                chatMessageCell.setDelegate(new ChatActionCellDelegate() {
                    public void didPressBotButton(MessageObject messageObject, KeyboardButton keyboardButton) {
                    }

                    public void didPressReplyMessage(ChatActionCell chatActionCell, int i) {
                    }

                    public void didClickImage(ChatActionCell chatActionCell) {
                        MessageObject messageObject = chatActionCell.getMessageObject();
                        PhotoViewer.getInstance().setParentActivity(ChannelAdminLogActivity.this.getParentActivity());
                        PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 640);
                        if (closestPhotoSizeWithSize != null) {
                            PhotoViewer.getInstance().openPhoto(closestPhotoSizeWithSize.location, ImageLocation.getForPhoto(closestPhotoSizeWithSize, messageObject.messageOwner.action.photo), ChannelAdminLogActivity.this.provider);
                            return;
                        }
                        PhotoViewer.getInstance().openPhoto(messageObject, 0, 0, ChannelAdminLogActivity.this.provider);
                    }

                    public void didLongPress(ChatActionCell chatActionCell, float f, float f2) {
                        ChannelAdminLogActivity.this.createMenu(chatActionCell);
                    }

                    public void needOpenUserProfile(int i) {
                        Bundle bundle;
                        if (i < 0) {
                            bundle = new Bundle();
                            bundle.putInt("chat_id", -i);
                            if (MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).checkCanOpenChat(bundle, ChannelAdminLogActivity.this)) {
                                ChannelAdminLogActivity.this.presentFragment(new ChatActivity(bundle), true);
                            }
                        } else if (i != UserConfig.getInstance(ChannelAdminLogActivity.this.currentAccount).getClientUserId()) {
                            bundle = new Bundle();
                            bundle.putInt("user_id", i);
                            ChannelAdminLogActivity.this.addCanBanUser(bundle, i);
                            ProfileActivity profileActivity = new ProfileActivity(bundle);
                            profileActivity.setPlayProfileAnimation(false);
                            ChannelAdminLogActivity.this.presentFragment(profileActivity);
                        }
                    }
                });
            } else if (i == 2) {
                chatMessageCell = new ChatUnreadCell(this.mContext);
            } else if (i == 3) {
                chatMessageCell = new BotHelpCell(this.mContext);
                chatMessageCell.setDelegate(new -$$Lambda$ChannelAdminLogActivity$ChatActivityAdapter$17meO0dSqjA1BWDg9Mv1dvdogDs(this));
            } else {
                chatMessageCell = i == 4 ? new ChatLoadingCell(this.mContext) : null;
            }
            chatMessageCell.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(chatMessageCell);
        }

        public /* synthetic */ void lambda$onCreateViewHolder$0$ChannelAdminLogActivity$ChatActivityAdapter(String str) {
            if (str.startsWith("@")) {
                MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).openByUserName(str.substring(1), ChannelAdminLogActivity.this, 0);
            } else if (str.startsWith("#")) {
                DialogsActivity dialogsActivity = new DialogsActivity(null);
                dialogsActivity.setSearchString(str);
                ChannelAdminLogActivity.this.presentFragment(dialogsActivity);
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:26:0x0095  */
        /* JADX WARNING: Missing block: B:33:0x00cc, code skipped:
            if (java.lang.Math.abs(r11.date - r5.date) <= 300) goto L_0x00d0;
     */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r11, int r12) {
            /*
            r10 = this;
            r0 = r10.loadingUpRow;
            r1 = 0;
            r2 = 1;
            if (r12 != r0) goto L_0x0018;
        L_0x0006:
            r11 = r11.itemView;
            r11 = (org.telegram.ui.Cells.ChatLoadingCell) r11;
            r12 = org.telegram.ui.ChannelAdminLogActivity.this;
            r12 = r12.loadsCount;
            if (r12 <= r2) goto L_0x0013;
        L_0x0012:
            r1 = 1;
        L_0x0013:
            r11.setProgressVisible(r1);
            goto L_0x00e9;
        L_0x0018:
            r0 = r10.messagesStartRow;
            if (r12 < r0) goto L_0x00e9;
        L_0x001c:
            r0 = r10.messagesEndRow;
            if (r12 >= r0) goto L_0x00e9;
        L_0x0020:
            r0 = org.telegram.ui.ChannelAdminLogActivity.this;
            r0 = r0.messages;
            r3 = r0.size();
            r4 = r10.messagesStartRow;
            r4 = r12 - r4;
            r3 = r3 - r4;
            r3 = r3 - r2;
            r0 = r0.get(r3);
            r0 = (org.telegram.messenger.MessageObject) r0;
            r3 = r11.itemView;
            r4 = r3 instanceof org.telegram.ui.Cells.ChatMessageCell;
            if (r4 == 0) goto L_0x00db;
        L_0x003a:
            r3 = (org.telegram.ui.Cells.ChatMessageCell) r3;
            r3.isChat = r2;
            r4 = r12 + 1;
            r5 = r10.getItemViewType(r4);
            r6 = r12 + -1;
            r6 = r10.getItemViewType(r6);
            r7 = r0.messageOwner;
            r7 = r7.reply_markup;
            r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_replyInlineMarkup;
            r8 = 300; // 0x12c float:4.2E-43 double:1.48E-321;
            if (r7 != 0) goto L_0x008e;
        L_0x0054:
            r7 = r11.getItemViewType();
            if (r5 != r7) goto L_0x008e;
        L_0x005a:
            r5 = org.telegram.ui.ChannelAdminLogActivity.this;
            r5 = r5.messages;
            r7 = r5.size();
            r9 = r10.messagesStartRow;
            r4 = r4 - r9;
            r7 = r7 - r4;
            r7 = r7 - r2;
            r4 = r5.get(r7);
            r4 = (org.telegram.messenger.MessageObject) r4;
            r5 = r4.isOutOwner();
            r7 = r0.isOutOwner();
            if (r5 != r7) goto L_0x008e;
        L_0x0077:
            r4 = r4.messageOwner;
            r5 = r4.from_id;
            r7 = r0.messageOwner;
            r9 = r7.from_id;
            if (r5 != r9) goto L_0x008e;
        L_0x0081:
            r4 = r4.date;
            r5 = r7.date;
            r4 = r4 - r5;
            r4 = java.lang.Math.abs(r4);
            if (r4 > r8) goto L_0x008e;
        L_0x008c:
            r4 = 1;
            goto L_0x008f;
        L_0x008e:
            r4 = 0;
        L_0x008f:
            r11 = r11.getItemViewType();
            if (r6 != r11) goto L_0x00cf;
        L_0x0095:
            r11 = org.telegram.ui.ChannelAdminLogActivity.this;
            r11 = r11.messages;
            r5 = r11.size();
            r6 = r10.messagesStartRow;
            r12 = r12 - r6;
            r5 = r5 - r12;
            r11 = r11.get(r5);
            r11 = (org.telegram.messenger.MessageObject) r11;
            r12 = r11.messageOwner;
            r12 = r12.reply_markup;
            r12 = r12 instanceof org.telegram.tgnet.TLRPC.TL_replyInlineMarkup;
            if (r12 != 0) goto L_0x00cf;
        L_0x00af:
            r12 = r11.isOutOwner();
            r5 = r0.isOutOwner();
            if (r12 != r5) goto L_0x00cf;
        L_0x00b9:
            r11 = r11.messageOwner;
            r12 = r11.from_id;
            r5 = r0.messageOwner;
            r6 = r5.from_id;
            if (r12 != r6) goto L_0x00cf;
        L_0x00c3:
            r11 = r11.date;
            r12 = r5.date;
            r11 = r11 - r12;
            r11 = java.lang.Math.abs(r11);
            if (r11 > r8) goto L_0x00cf;
        L_0x00ce:
            goto L_0x00d0;
        L_0x00cf:
            r2 = 0;
        L_0x00d0:
            r11 = 0;
            r3.setMessageObject(r0, r11, r4, r2);
            r3.setHighlighted(r1);
            r3.setHighlightedText(r11);
            goto L_0x00e9;
        L_0x00db:
            r11 = r3 instanceof org.telegram.ui.Cells.ChatActionCell;
            if (r11 == 0) goto L_0x00e9;
        L_0x00df:
            r3 = (org.telegram.ui.Cells.ChatActionCell) r3;
            r3.setMessageObject(r0);
            r11 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            r3.setAlpha(r11);
        L_0x00e9:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public int getItemViewType(int i) {
            if (i < this.messagesStartRow || i >= this.messagesEndRow) {
                return 4;
            }
            ArrayList arrayList = ChannelAdminLogActivity.this.messages;
            return ((MessageObject) arrayList.get((arrayList.size() - (i - this.messagesStartRow)) - 1)).contentType;
        }

        public void onViewAttachedToWindow(ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            if (view instanceof ChatMessageCell) {
                final ChatMessageCell chatMessageCell = (ChatMessageCell) view;
                chatMessageCell.getMessageObject();
                chatMessageCell.setBackgroundDrawable(null);
                chatMessageCell.setCheckPressed(true, false);
                chatMessageCell.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                    public boolean onPreDraw() {
                        chatMessageCell.getViewTreeObserver().removeOnPreDrawListener(this);
                        int measuredHeight = ChannelAdminLogActivity.this.chatListView.getMeasuredHeight();
                        int top = chatMessageCell.getTop();
                        chatMessageCell.getBottom();
                        top = top >= 0 ? 0 : -top;
                        int measuredHeight2 = chatMessageCell.getMeasuredHeight();
                        if (measuredHeight2 > measuredHeight) {
                            measuredHeight2 = top + measuredHeight;
                        }
                        chatMessageCell.setVisiblePart(top, measuredHeight2 - top, (ChannelAdminLogActivity.this.contentView.getHeightWithKeyboard() - AndroidUtilities.dp(48.0f)) - ChannelAdminLogActivity.this.chatListView.getTop());
                        return true;
                    }
                });
                chatMessageCell.setHighlighted(false);
            }
        }

        public void updateRowWithMessageObject(MessageObject messageObject) {
            int indexOf = ChannelAdminLogActivity.this.messages.indexOf(messageObject);
            if (indexOf != -1) {
                notifyItemChanged(((this.messagesStartRow + ChannelAdminLogActivity.this.messages.size()) - indexOf) - 1);
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

        public void notifyItemChanged(int i) {
            updateRows();
            try {
                super.notifyItemChanged(i);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        public void notifyItemRangeChanged(int i, int i2) {
            updateRows();
            try {
                super.notifyItemRangeChanged(i, i2);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        public void notifyItemInserted(int i) {
            updateRows();
            try {
                super.notifyItemInserted(i);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        public void notifyItemMoved(int i, int i2) {
            updateRows();
            try {
                super.notifyItemMoved(i, i2);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        public void notifyItemRangeInserted(int i, int i2) {
            updateRows();
            try {
                super.notifyItemRangeInserted(i, i2);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        public void notifyItemRemoved(int i) {
            updateRows();
            try {
                super.notifyItemRemoved(i);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        public void notifyItemRangeRemoved(int i, int i2) {
            updateRows();
            try {
                super.notifyItemRangeRemoved(i, i2);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    static /* synthetic */ void lambda$null$8(DialogInterface dialogInterface, int i) {
    }

    private void updateBottomOverlay() {
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

    private void loadMessages(boolean z) {
        if (!this.loading) {
            int i = 0;
            if (z) {
                this.minEventId = Long.MAX_VALUE;
                FrameLayout frameLayout = this.progressView;
                if (frameLayout != null) {
                    frameLayout.setVisibility(0);
                    this.emptyViewContainer.setVisibility(4);
                    this.chatListView.setEmptyView(null);
                }
                this.messagesDict.clear();
                this.messages.clear();
                this.messagesByDays.clear();
            }
            this.loading = true;
            TL_channels_getAdminLog tL_channels_getAdminLog = new TL_channels_getAdminLog();
            tL_channels_getAdminLog.channel = MessagesController.getInputChannel(this.currentChat);
            tL_channels_getAdminLog.q = this.searchQuery;
            tL_channels_getAdminLog.limit = 50;
            if (z || this.messages.isEmpty()) {
                tL_channels_getAdminLog.max_id = 0;
            } else {
                tL_channels_getAdminLog.max_id = this.minEventId;
            }
            tL_channels_getAdminLog.min_id = 0;
            TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter = this.currentFilter;
            if (tL_channelAdminLogEventsFilter != null) {
                tL_channels_getAdminLog.flags = 1 | tL_channels_getAdminLog.flags;
                tL_channels_getAdminLog.events_filter = tL_channelAdminLogEventsFilter;
            }
            if (this.selectedAdmins != null) {
                tL_channels_getAdminLog.flags |= 2;
                while (i < this.selectedAdmins.size()) {
                    tL_channels_getAdminLog.admins.add(MessagesController.getInstance(this.currentAccount).getInputUser((User) this.selectedAdmins.valueAt(i)));
                    i++;
                }
            }
            updateEmptyPlaceholder();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_getAdminLog, new -$$Lambda$ChannelAdminLogActivity$eETtXTeGU9y5fKwho2AV8a_Izoc(this));
            if (z) {
                ChatActivityAdapter chatActivityAdapter = this.chatAdapter;
                if (chatActivityAdapter != null) {
                    chatActivityAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    public /* synthetic */ void lambda$loadMessages$1$ChannelAdminLogActivity(TLObject tLObject, TL_error tL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$ChannelAdminLogActivity$K07rFS3bEoWzk9x_xNaPo7WXWaQ(this, (TL_channels_adminLogResults) tLObject));
        }
    }

    public /* synthetic */ void lambda$null$0$ChannelAdminLogActivity(TL_channels_adminLogResults tL_channels_adminLogResults) {
        int i;
        int i2 = 0;
        MessagesController.getInstance(this.currentAccount).putUsers(tL_channels_adminLogResults.users, false);
        MessagesController.getInstance(this.currentAccount).putChats(tL_channels_adminLogResults.chats, false);
        int size = this.messages.size();
        Object obj = null;
        for (i = 0; i < tL_channels_adminLogResults.events.size(); i++) {
            TL_channelAdminLogEvent tL_channelAdminLogEvent = (TL_channelAdminLogEvent) tL_channels_adminLogResults.events.get(i);
            if (this.messagesDict.indexOfKey(tL_channelAdminLogEvent.id) < 0) {
                ChannelAdminLogEventAction channelAdminLogEventAction = tL_channelAdminLogEvent.action;
                if (!(channelAdminLogEventAction instanceof TL_channelAdminLogEventActionParticipantToggleAdmin) || !(channelAdminLogEventAction.prev_participant instanceof TL_channelParticipantCreator) || (channelAdminLogEventAction.new_participant instanceof TL_channelParticipantCreator)) {
                    this.minEventId = Math.min(this.minEventId, tL_channelAdminLogEvent.id);
                    MessageObject messageObject = new MessageObject(this.currentAccount, tL_channelAdminLogEvent, this.messages, this.messagesByDays, this.currentChat, this.mid);
                    if (messageObject.contentType >= 0) {
                        this.messagesDict.put(tL_channelAdminLogEvent.id, messageObject);
                    }
                    obj = 1;
                }
            }
        }
        int size2 = this.messages.size() - size;
        this.loading = false;
        if (obj == null) {
            this.endReached = true;
        }
        this.progressView.setVisibility(4);
        this.chatListView.setEmptyView(this.emptyViewContainer);
        if (size2 != 0) {
            if (this.endReached) {
                this.chatAdapter.notifyItemRangeChanged(0, 2);
                size = 1;
            } else {
                size = 0;
            }
            i = this.chatLayoutManager.findLastVisibleItemPosition();
            View findViewByPosition = this.chatLayoutManager.findViewByPosition(i);
            if (findViewByPosition != null) {
                i2 = findViewByPosition.getTop();
            }
            i2 -= this.chatListView.getPaddingTop();
            if (size2 - size > 0) {
                int i3 = (size ^ 1) + 1;
                this.chatAdapter.notifyItemChanged(i3);
                this.chatAdapter.notifyItemRangeInserted(i3, size2 - size);
            }
            if (i != -1) {
                this.chatLayoutManager.scrollToPositionWithOffset((i + size2) - size, i2);
            }
        } else if (this.endReached) {
            this.chatAdapter.notifyItemRemoved(0);
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        RecyclerListView recyclerListView;
        if (i == NotificationCenter.emojiDidLoad) {
            recyclerListView = this.chatListView;
            if (recyclerListView != null) {
                recyclerListView.invalidateViews();
                return;
            }
            return;
        }
        int i3 = 0;
        View childAt;
        ChatMessageCell chatMessageCell;
        MessageObject messageObject;
        if (i == NotificationCenter.messagePlayingDidStart) {
            if (((MessageObject) objArr[0]).isRoundVideo()) {
                MediaController.getInstance().setTextureView(createTextureView(true), this.aspectRatioFrameLayout, this.roundVideoContainer, true);
                updateTextureViewPosition();
            }
            recyclerListView = this.chatListView;
            if (recyclerListView != null) {
                i = recyclerListView.getChildCount();
                for (i2 = 0; i2 < i; i2++) {
                    childAt = this.chatListView.getChildAt(i2);
                    if (childAt instanceof ChatMessageCell) {
                        chatMessageCell = (ChatMessageCell) childAt;
                        messageObject = chatMessageCell.getMessageObject();
                        if (messageObject != null) {
                            if (messageObject.isVoice() || messageObject.isMusic()) {
                                chatMessageCell.updateButtonState(false, true, false);
                            } else if (messageObject.isRoundVideo()) {
                                chatMessageCell.checkVideoPlayback(false);
                                if (!(MediaController.getInstance().isPlayingMessage(messageObject) || messageObject.audioProgress == 0.0f)) {
                                    messageObject.resetPlayingProgress();
                                    chatMessageCell.invalidate();
                                }
                            }
                        }
                    }
                }
            }
        } else if (i == NotificationCenter.messagePlayingDidReset || i == NotificationCenter.messagePlayingPlayStateChanged) {
            recyclerListView = this.chatListView;
            if (recyclerListView != null) {
                i = recyclerListView.getChildCount();
                for (i2 = 0; i2 < i; i2++) {
                    childAt = this.chatListView.getChildAt(i2);
                    if (childAt instanceof ChatMessageCell) {
                        chatMessageCell = (ChatMessageCell) childAt;
                        messageObject = chatMessageCell.getMessageObject();
                        if (messageObject != null) {
                            if (messageObject.isVoice() || messageObject.isMusic()) {
                                chatMessageCell.updateButtonState(false, true, false);
                            } else if (messageObject.isRoundVideo() && !MediaController.getInstance().isPlayingMessage(messageObject)) {
                                chatMessageCell.checkVideoPlayback(true);
                            }
                        }
                    }
                }
            }
        } else if (i == NotificationCenter.messagePlayingProgressDidChanged) {
            Integer num = (Integer) objArr[0];
            RecyclerListView recyclerListView2 = this.chatListView;
            if (recyclerListView2 != null) {
                i2 = recyclerListView2.getChildCount();
                while (i3 < i2) {
                    childAt = this.chatListView.getChildAt(i3);
                    if (childAt instanceof ChatMessageCell) {
                        chatMessageCell = (ChatMessageCell) childAt;
                        MessageObject messageObject2 = chatMessageCell.getMessageObject();
                        if (messageObject2 != null && messageObject2.getId() == num.intValue()) {
                            MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
                            if (playingMessageObject != null) {
                                messageObject2.audioProgress = playingMessageObject.audioProgress;
                                messageObject2.audioProgressSec = playingMessageObject.audioProgressSec;
                                messageObject2.audioPlayerDuration = playingMessageObject.audioPlayerDuration;
                                chatMessageCell.updatePlayingMessageProgress();
                                return;
                            }
                            return;
                        }
                    }
                    i3++;
                }
            }
        } else if (i == NotificationCenter.didSetNewWallpapper && this.fragmentView != null) {
            this.contentView.setBackgroundImage(Theme.getCachedWallpaper(), Theme.isWallpaperMotion());
            this.progressView2.getBackground().setColorFilter(Theme.colorFilter);
            TextView textView = this.emptyView;
            if (textView != null) {
                textView.getBackground().setColorFilter(Theme.colorFilter);
            }
            this.chatListView.invalidateViews();
        }
    }

    public View createView(Context context) {
        Context context2 = context;
        if (this.chatMessageCellsCache.isEmpty()) {
            for (int i = 0; i < 8; i++) {
                this.chatMessageCellsCache.add(new ChatMessageCell(context2));
            }
        }
        this.searchWas = false;
        this.hasOwnBackground = true;
        Theme.createChatResources(context2, false);
        this.actionBar.setAddToContainer(false);
        ActionBar actionBar = this.actionBar;
        boolean z = VERSION.SDK_INT >= 21 && !AndroidUtilities.isTablet();
        actionBar.setOccupyStatusBar(z);
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    ChannelAdminLogActivity.this.finishFragment();
                }
            }
        });
        this.avatarContainer = new ChatAvatarContainer(context2, null, false);
        this.avatarContainer.setOccupyStatusBar(AndroidUtilities.isTablet() ^ 1);
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
        this.fragmentView = new SizeNotifierFrameLayout(context2) {
            /* Access modifiers changed, original: protected */
            public void onAttachedToWindow() {
                super.onAttachedToWindow();
                MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
                if (playingMessageObject != null && playingMessageObject.isRoundVideo() && playingMessageObject.eventId != 0 && playingMessageObject.getDialogId() == ((long) (-ChannelAdminLogActivity.this.currentChat.id))) {
                    MediaController.getInstance().setTextureView(ChannelAdminLogActivity.this.createTextureView(false), ChannelAdminLogActivity.this.aspectRatioFrameLayout, ChannelAdminLogActivity.this.roundVideoContainer, true);
                }
            }

            /* Access modifiers changed, original: protected */
            public boolean drawChild(Canvas canvas, View view, long j) {
                boolean drawChild = super.drawChild(canvas, view, j);
                if (view == ChannelAdminLogActivity.this.actionBar && ChannelAdminLogActivity.this.parentLayout != null) {
                    ChannelAdminLogActivity.this.parentLayout.drawHeaderShadow(canvas, ChannelAdminLogActivity.this.actionBar.getVisibility() == 0 ? ChannelAdminLogActivity.this.actionBar.getMeasuredHeight() : 0);
                }
                return drawChild;
            }

            /* Access modifiers changed, original: protected */
            public boolean isActionBarVisible() {
                return ChannelAdminLogActivity.this.actionBar.getVisibility() == 0;
            }

            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                int size = MeasureSpec.getSize(i);
                int size2 = MeasureSpec.getSize(i2);
                setMeasuredDimension(size, size2);
                size2 -= getPaddingTop();
                measureChildWithMargins(ChannelAdminLogActivity.this.actionBar, i, 0, i2, 0);
                int measuredHeight = ChannelAdminLogActivity.this.actionBar.getMeasuredHeight();
                if (ChannelAdminLogActivity.this.actionBar.getVisibility() == 0) {
                    size2 -= measuredHeight;
                }
                getKeyboardHeight();
                measuredHeight = getChildCount();
                for (int i3 = 0; i3 < measuredHeight; i3++) {
                    View childAt = getChildAt(i3);
                    if (!(childAt == null || childAt.getVisibility() == 8 || childAt == ChannelAdminLogActivity.this.actionBar)) {
                        if (childAt == ChannelAdminLogActivity.this.chatListView || childAt == ChannelAdminLogActivity.this.progressView) {
                            childAt.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), size2 - AndroidUtilities.dp(50.0f)), NUM));
                        } else if (childAt == ChannelAdminLogActivity.this.emptyViewContainer) {
                            childAt.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec(size2, NUM));
                        } else {
                            measureChildWithMargins(childAt, i, 0, i2, 0);
                        }
                    }
                }
            }

            /* Access modifiers changed, original: protected */
            /* JADX WARNING: Removed duplicated region for block: B:37:0x00bc  */
            /* JADX WARNING: Removed duplicated region for block: B:32:0x009a  */
            /* JADX WARNING: Removed duplicated region for block: B:28:0x0086  */
            /* JADX WARNING: Removed duplicated region for block: B:17:0x004e  */
            /* JADX WARNING: Removed duplicated region for block: B:32:0x009a  */
            /* JADX WARNING: Removed duplicated region for block: B:37:0x00bc  */
            public void onLayout(boolean r10, int r11, int r12, int r13, int r14) {
                /*
                r9 = this;
                r10 = r9.getChildCount();
                r0 = 0;
                r1 = 0;
            L_0x0006:
                if (r1 >= r10) goto L_0x00d2;
            L_0x0008:
                r2 = r9.getChildAt(r1);
                r3 = r2.getVisibility();
                r4 = 8;
                if (r3 != r4) goto L_0x0016;
            L_0x0014:
                goto L_0x00ce;
            L_0x0016:
                r3 = r2.getLayoutParams();
                r3 = (android.widget.FrameLayout.LayoutParams) r3;
                r4 = r2.getMeasuredWidth();
                r5 = r2.getMeasuredHeight();
                r6 = r3.gravity;
                r7 = -1;
                if (r6 != r7) goto L_0x002b;
            L_0x0029:
                r6 = 51;
            L_0x002b:
                r7 = r6 & 7;
                r6 = r6 & 112;
                r7 = r7 & 7;
                r8 = 1;
                if (r7 == r8) goto L_0x003f;
            L_0x0034:
                r8 = 5;
                if (r7 == r8) goto L_0x003a;
            L_0x0037:
                r7 = r3.leftMargin;
                goto L_0x004a;
            L_0x003a:
                r7 = r13 - r4;
                r8 = r3.rightMargin;
                goto L_0x0049;
            L_0x003f:
                r7 = r13 - r11;
                r7 = r7 - r4;
                r7 = r7 / 2;
                r8 = r3.leftMargin;
                r7 = r7 + r8;
                r8 = r3.rightMargin;
            L_0x0049:
                r7 = r7 - r8;
            L_0x004a:
                r8 = 16;
                if (r6 == r8) goto L_0x0086;
            L_0x004e:
                r8 = 48;
                if (r6 == r8) goto L_0x005f;
            L_0x0052:
                r8 = 80;
                if (r6 == r8) goto L_0x0059;
            L_0x0056:
                r3 = r3.topMargin;
                goto L_0x0092;
            L_0x0059:
                r6 = r14 - r12;
                r6 = r6 - r5;
                r3 = r3.bottomMargin;
                goto L_0x0090;
            L_0x005f:
                r3 = r3.topMargin;
                r6 = r9.getPaddingTop();
                r3 = r3 + r6;
                r6 = org.telegram.ui.ChannelAdminLogActivity.this;
                r6 = r6.actionBar;
                if (r2 == r6) goto L_0x0092;
            L_0x006e:
                r6 = org.telegram.ui.ChannelAdminLogActivity.this;
                r6 = r6.actionBar;
                r6 = r6.getVisibility();
                if (r6 != 0) goto L_0x0092;
            L_0x007a:
                r6 = org.telegram.ui.ChannelAdminLogActivity.this;
                r6 = r6.actionBar;
                r6 = r6.getMeasuredHeight();
                r3 = r3 + r6;
                goto L_0x0092;
            L_0x0086:
                r6 = r14 - r12;
                r6 = r6 - r5;
                r6 = r6 / 2;
                r8 = r3.topMargin;
                r6 = r6 + r8;
                r3 = r3.bottomMargin;
            L_0x0090:
                r3 = r6 - r3;
            L_0x0092:
                r6 = org.telegram.ui.ChannelAdminLogActivity.this;
                r6 = r6.emptyViewContainer;
                if (r2 != r6) goto L_0x00bc;
            L_0x009a:
                r6 = NUM; // 0x41CLASSNAME float:24.0 double:5.450047783E-315;
                r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
                r8 = org.telegram.ui.ChannelAdminLogActivity.this;
                r8 = r8.actionBar;
                r8 = r8.getVisibility();
                if (r8 != 0) goto L_0x00b9;
            L_0x00ac:
                r8 = org.telegram.ui.ChannelAdminLogActivity.this;
                r8 = r8.actionBar;
                r8 = r8.getMeasuredHeight();
                r8 = r8 / 2;
                goto L_0x00ba;
            L_0x00b9:
                r8 = 0;
            L_0x00ba:
                r6 = r6 - r8;
                goto L_0x00c8;
            L_0x00bc:
                r6 = org.telegram.ui.ChannelAdminLogActivity.this;
                r6 = r6.actionBar;
                if (r2 != r6) goto L_0x00c9;
            L_0x00c4:
                r6 = r9.getPaddingTop();
            L_0x00c8:
                r3 = r3 - r6;
            L_0x00c9:
                r4 = r4 + r7;
                r5 = r5 + r3;
                r2.layout(r7, r3, r4, r5);
            L_0x00ce:
                r1 = r1 + 1;
                goto L_0x0006;
            L_0x00d2:
                r10 = org.telegram.ui.ChannelAdminLogActivity.this;
                r10.updateMessagesVisisblePart();
                r9.notifyHeightChanged();
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChannelAdminLogActivity$AnonymousClass4.onLayout(boolean, int, int, int, int):void");
            }
        };
        this.contentView = (SizeNotifierFrameLayout) this.fragmentView;
        this.contentView.setOccupyStatusBar(AndroidUtilities.isTablet() ^ 1);
        this.contentView.setBackgroundImage(Theme.getCachedWallpaper(), Theme.isWallpaperMotion());
        this.emptyViewContainer = new FrameLayout(context2);
        this.emptyViewContainer.setVisibility(4);
        this.contentView.addView(this.emptyViewContainer, LayoutHelper.createFrame(-1, -2, 17));
        this.emptyViewContainer.setOnTouchListener(-$$Lambda$ChannelAdminLogActivity$Xh6zBvXYfqVbautwsQ1g7PDF8js.INSTANCE);
        this.emptyView = new TextView(context2);
        this.emptyView.setTextSize(1, 14.0f);
        this.emptyView.setGravity(17);
        String str = "chat_serviceText";
        this.emptyView.setTextColor(Theme.getColor(str));
        this.emptyView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(10.0f), Theme.getServiceMessageColor()));
        this.emptyView.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
        this.emptyViewContainer.addView(this.emptyView, LayoutHelper.createFrame(-2, -2.0f, 17, 16.0f, 0.0f, 16.0f, 0.0f));
        this.chatListView = new RecyclerListView(context2) {
            public boolean drawChild(Canvas canvas, View view, long j) {
                boolean drawChild = super.drawChild(canvas, view, j);
                if (view instanceof ChatMessageCell) {
                    ChatMessageCell chatMessageCell = (ChatMessageCell) view;
                    ImageReceiver avatarImage = chatMessageCell.getAvatarImage();
                    if (avatarImage != null) {
                        ViewHolder childViewHolder;
                        int top = view.getTop();
                        if (chatMessageCell.isPinnedBottom()) {
                            childViewHolder = ChannelAdminLogActivity.this.chatListView.getChildViewHolder(view);
                            if (!(childViewHolder == null || ChannelAdminLogActivity.this.chatListView.findViewHolderForAdapterPosition(childViewHolder.getAdapterPosition() + 1) == null)) {
                                avatarImage.setImageY(-AndroidUtilities.dp(1000.0f));
                                avatarImage.draw(canvas);
                                return drawChild;
                            }
                        }
                        if (chatMessageCell.isPinnedTop()) {
                            childViewHolder = ChannelAdminLogActivity.this.chatListView.getChildViewHolder(view);
                            if (childViewHolder != null) {
                                View view2;
                                do {
                                    childViewHolder = ChannelAdminLogActivity.this.chatListView.findViewHolderForAdapterPosition(childViewHolder.getAdapterPosition() - 1);
                                    if (childViewHolder == null) {
                                        break;
                                    }
                                    top = childViewHolder.itemView.getTop();
                                    view2 = childViewHolder.itemView;
                                    if (!(view2 instanceof ChatMessageCell)) {
                                        break;
                                    }
                                } while (((ChatMessageCell) view2).isPinnedTop());
                            }
                        }
                        int top2 = view.getTop() + chatMessageCell.getLayoutHeight();
                        int height = ChannelAdminLogActivity.this.chatListView.getHeight() - ChannelAdminLogActivity.this.chatListView.getPaddingBottom();
                        if (top2 > height) {
                            top2 = height;
                        }
                        if (top2 - AndroidUtilities.dp(48.0f) < top) {
                            top2 = AndroidUtilities.dp(48.0f) + top;
                        }
                        avatarImage.setImageY(top2 - AndroidUtilities.dp(44.0f));
                        avatarImage.draw(canvas);
                    }
                }
                return drawChild;
            }
        };
        this.chatListView.setOnItemClickListener(new -$$Lambda$ChannelAdminLogActivity$Th_a2zOFfCYw_f0pzM6kJHBXd6E(this));
        this.chatListView.setTag(Integer.valueOf(1));
        this.chatListView.setVerticalScrollBarEnabled(true);
        RecyclerListView recyclerListView = this.chatListView;
        ChatActivityAdapter chatActivityAdapter = new ChatActivityAdapter(context2);
        this.chatAdapter = chatActivityAdapter;
        recyclerListView.setAdapter(chatActivityAdapter);
        this.chatListView.setClipToPadding(false);
        this.chatListView.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(3.0f));
        this.chatListView.setItemAnimator(null);
        this.chatListView.setLayoutAnimation(null);
        this.chatLayoutManager = new LinearLayoutManager(context2) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }

            public void smoothScrollToPosition(RecyclerView recyclerView, State state, int i) {
                LinearSmoothScrollerMiddle linearSmoothScrollerMiddle = new LinearSmoothScrollerMiddle(recyclerView.getContext());
                linearSmoothScrollerMiddle.setTargetPosition(i);
                startSmoothScroll(linearSmoothScrollerMiddle);
            }
        };
        this.chatLayoutManager.setOrientation(1);
        this.chatLayoutManager.setStackFromEnd(true);
        this.chatListView.setLayoutManager(this.chatLayoutManager);
        this.contentView.addView(this.chatListView, LayoutHelper.createFrame(-1, -1.0f));
        this.chatListView.setOnScrollListener(new OnScrollListener() {
            private final int scrollValue = AndroidUtilities.dp(100.0f);
            private float totalDy = 0.0f;

            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1) {
                    ChannelAdminLogActivity.this.scrollingFloatingDate = true;
                    ChannelAdminLogActivity.this.checkTextureViewPosition = true;
                } else if (i == 0) {
                    ChannelAdminLogActivity.this.scrollingFloatingDate = false;
                    ChannelAdminLogActivity.this.checkTextureViewPosition = false;
                    ChannelAdminLogActivity.this.hideFloatingDateView(true);
                }
            }

            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                ChannelAdminLogActivity.this.chatListView.invalidate();
                if (i2 != 0 && ChannelAdminLogActivity.this.scrollingFloatingDate && !ChannelAdminLogActivity.this.currentFloatingTopIsNotMessage && ChannelAdminLogActivity.this.floatingDateView.getTag() == null) {
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
                        public void onAnimationEnd(Animator animator) {
                            if (animator.equals(ChannelAdminLogActivity.this.floatingDateAnimation)) {
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
        int i2 = this.scrollToPositionOnRecreate;
        if (i2 != -1) {
            this.chatLayoutManager.scrollToPositionWithOffset(i2, this.scrollToOffsetOnRecreate);
            this.scrollToPositionOnRecreate = -1;
        }
        this.progressView = new FrameLayout(context2);
        this.progressView.setVisibility(4);
        this.contentView.addView(this.progressView, LayoutHelper.createFrame(-1, -1, 51));
        this.progressView2 = new View(context2);
        this.progressView2.setBackgroundResource(NUM);
        this.progressView2.getBackground().setColorFilter(Theme.colorFilter);
        this.progressView.addView(this.progressView2, LayoutHelper.createFrame(36, 36, 17));
        this.progressBar = new RadialProgressView(context2);
        this.progressBar.setSize(AndroidUtilities.dp(28.0f));
        this.progressBar.setProgressColor(Theme.getColor(str));
        this.progressView.addView(this.progressBar, LayoutHelper.createFrame(32, 32, 17));
        this.floatingDateView = new ChatActionCell(context2);
        this.floatingDateView.setAlpha(0.0f);
        this.contentView.addView(this.floatingDateView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 4.0f, 0.0f, 0.0f));
        this.contentView.addView(this.actionBar);
        this.bottomOverlayChat = new FrameLayout(context2) {
            public void onDraw(Canvas canvas) {
                int intrinsicHeight = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), intrinsicHeight);
                Theme.chat_composeShadowDrawable.draw(canvas);
                canvas.drawRect(0.0f, (float) intrinsicHeight, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
            }
        };
        this.bottomOverlayChat.setWillNotDraw(false);
        this.bottomOverlayChat.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
        this.contentView.addView(this.bottomOverlayChat, LayoutHelper.createFrame(-1, 51, 80));
        this.bottomOverlayChat.setOnClickListener(new -$$Lambda$ChannelAdminLogActivity$PzYgcbyw3IRYlthKzfCR6dwSeeo(this));
        this.bottomOverlayChatText = new TextView(context2);
        this.bottomOverlayChatText.setTextSize(1, 15.0f);
        str = "fonts/rmedium.ttf";
        this.bottomOverlayChatText.setTypeface(AndroidUtilities.getTypeface(str));
        this.bottomOverlayChatText.setTextColor(Theme.getColor("chat_fieldOverlayText"));
        this.bottomOverlayChatText.setText(LocaleController.getString("SETTINGS", NUM).toUpperCase());
        this.bottomOverlayChat.addView(this.bottomOverlayChatText, LayoutHelper.createFrame(-2, -2, 17));
        this.bottomOverlayImage = new ImageView(context2);
        this.bottomOverlayImage.setImageResource(NUM);
        this.bottomOverlayImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_fieldOverlayText"), Mode.MULTIPLY));
        this.bottomOverlayImage.setScaleType(ScaleType.CENTER);
        this.bottomOverlayChat.addView(this.bottomOverlayImage, LayoutHelper.createFrame(48, 48.0f, 53, 3.0f, 0.0f, 0.0f, 0.0f));
        this.bottomOverlayImage.setContentDescription(LocaleController.getString("BotHelp", NUM));
        this.bottomOverlayImage.setOnClickListener(new -$$Lambda$ChannelAdminLogActivity$Hkc6JExHTyVzGS38RTThrIB1hTM(this));
        this.searchContainer = new FrameLayout(context2) {
            public void onDraw(Canvas canvas) {
                int intrinsicHeight = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), intrinsicHeight);
                Theme.chat_composeShadowDrawable.draw(canvas);
                canvas.drawRect(0.0f, (float) intrinsicHeight, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
            }
        };
        this.searchContainer.setWillNotDraw(false);
        this.searchContainer.setVisibility(4);
        this.searchContainer.setFocusable(true);
        this.searchContainer.setFocusableInTouchMode(true);
        this.searchContainer.setClickable(true);
        this.searchContainer.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
        this.contentView.addView(this.searchContainer, LayoutHelper.createFrame(-1, 51, 80));
        this.searchCalendarButton = new ImageView(context2);
        this.searchCalendarButton.setScaleType(ScaleType.CENTER);
        this.searchCalendarButton.setImageResource(NUM);
        this.searchCalendarButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_searchPanelIcons"), Mode.MULTIPLY));
        this.searchContainer.addView(this.searchCalendarButton, LayoutHelper.createFrame(48, 48, 53));
        this.searchCalendarButton.setOnClickListener(new -$$Lambda$ChannelAdminLogActivity$qxESsL3-0yZHIVgE4y2ybeWozac(this));
        this.searchCountText = new SimpleTextView(context2);
        this.searchCountText.setTextColor(Theme.getColor("chat_searchPanelText"));
        this.searchCountText.setTextSize(15);
        this.searchCountText.setTypeface(AndroidUtilities.getTypeface(str));
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

    public /* synthetic */ void lambda$createView$3$ChannelAdminLogActivity(View view, int i) {
        createMenu(view);
    }

    public /* synthetic */ void lambda$createView$5$ChannelAdminLogActivity(View view) {
        if (getParentActivity() != null) {
            AdminLogFilterAlert adminLogFilterAlert = new AdminLogFilterAlert(getParentActivity(), this.currentFilter, this.selectedAdmins, this.currentChat.megagroup);
            adminLogFilterAlert.setCurrentAdmins(this.admins);
            adminLogFilterAlert.setAdminLogFilterAlertDelegate(new -$$Lambda$ChannelAdminLogActivity$np-QiCzzUhW5eVeXU_cFYpLVjKA(this));
            showDialog(adminLogFilterAlert);
        }
    }

    public /* synthetic */ void lambda$null$4$ChannelAdminLogActivity(TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter, SparseArray sparseArray) {
        this.currentFilter = tL_channelAdminLogEventsFilter;
        this.selectedAdmins = sparseArray;
        if (this.currentFilter == null && this.selectedAdmins == null) {
            this.avatarContainer.setSubtitle(LocaleController.getString("EventLogAllEvents", NUM));
        } else {
            this.avatarContainer.setSubtitle(LocaleController.getString("EventLogSelectedEvents", NUM));
        }
        loadMessages(true);
    }

    public /* synthetic */ void lambda$createView$6$ChannelAdminLogActivity(View view) {
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

    public /* synthetic */ void lambda$createView$10$ChannelAdminLogActivity(View view) {
        if (getParentActivity() != null) {
            AndroidUtilities.hideKeyboard(this.searchItem.getSearchField());
            Calendar instance = Calendar.getInstance();
            try {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getParentActivity(), new -$$Lambda$ChannelAdminLogActivity$0sxG9Ul5ZEHgnzFmOGlWQbHqyvo(this), instance.get(1), instance.get(2), instance.get(5));
                DatePicker datePicker = datePickerDialog.getDatePicker();
                datePicker.setMinDate(1375315200000L);
                datePicker.setMaxDate(System.currentTimeMillis());
                datePickerDialog.setButton(-1, LocaleController.getString("JumpToDate", NUM), datePickerDialog);
                datePickerDialog.setButton(-2, LocaleController.getString("Cancel", NUM), -$$Lambda$ChannelAdminLogActivity$v157pu_gWSgDQsCgD-IoOuRIDsE.INSTANCE);
                if (VERSION.SDK_INT >= 21) {
                    datePickerDialog.setOnShowListener(new -$$Lambda$ChannelAdminLogActivity$TCvk3Rm-FmQeaZ3KZrjzNCt3Urw(datePicker));
                }
                showDialog(datePickerDialog);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public /* synthetic */ void lambda$null$7$ChannelAdminLogActivity(DatePicker datePicker, int i, int i2, int i3) {
        Calendar instance = Calendar.getInstance();
        instance.clear();
        instance.set(i, i2, i3);
        long time = instance.getTime().getTime() / 1000;
        loadMessages(true);
    }

    static /* synthetic */ void lambda$null$9(DatePicker datePicker, DialogInterface dialogInterface) {
        int childCount = datePicker.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = datePicker.getChildAt(i);
            ViewGroup.LayoutParams layoutParams = childAt.getLayoutParams();
            layoutParams.width = -1;
            childAt.setLayoutParams(layoutParams);
        }
    }

    private void createMenu(View view) {
        View view2 = view;
        MessageObject messageObject = view2 instanceof ChatMessageCell ? ((ChatMessageCell) view2).getMessageObject() : view2 instanceof ChatActionCell ? ((ChatActionCell) view2).getMessageObject() : null;
        if (messageObject != null) {
            int messageType = getMessageType(messageObject);
            this.selectedObject = messageObject;
            if (getParentActivity() != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                MessageObject messageObject2 = this.selectedObject;
                String str = "Copy";
                if (messageObject2.type == 0 || messageObject2.caption != null) {
                    arrayList.add(LocaleController.getString(str, NUM));
                    arrayList2.add(Integer.valueOf(3));
                }
                if (messageType == 1) {
                    TL_channelAdminLogEvent tL_channelAdminLogEvent = this.selectedObject.currentEvent;
                    if (tL_channelAdminLogEvent != null) {
                        ChannelAdminLogEventAction channelAdminLogEventAction = tL_channelAdminLogEvent.action;
                        if (channelAdminLogEventAction instanceof TL_channelAdminLogEventActionChangeStickerSet) {
                            InputStickerSet inputStickerSet = channelAdminLogEventAction.new_stickerset;
                            if (inputStickerSet == null || (inputStickerSet instanceof TL_inputStickerSetEmpty)) {
                                inputStickerSet = this.selectedObject.currentEvent.action.prev_stickerset;
                            }
                            InputStickerSet inputStickerSet2 = inputStickerSet;
                            if (inputStickerSet2 != null) {
                                showDialog(new StickersAlert(getParentActivity(), this, inputStickerSet2, null, null));
                                return;
                            }
                        }
                    }
                }
                String str2 = "SaveToGIFs";
                if (messageType == 3) {
                    MessageMedia messageMedia = this.selectedObject.messageOwner.media;
                    if ((messageMedia instanceof TL_messageMediaWebPage) && MessageObject.isNewGifDocument(messageMedia.webpage.document)) {
                        arrayList.add(LocaleController.getString(str2, NUM));
                        arrayList2.add(Integer.valueOf(11));
                    }
                } else {
                    String str3 = "SaveToGallery";
                    String str4 = "SaveToDownloads";
                    String str5 = "ShareFile";
                    if (messageType == 4) {
                        if (this.selectedObject.isVideo()) {
                            arrayList.add(LocaleController.getString(str3, NUM));
                            arrayList2.add(Integer.valueOf(4));
                            arrayList.add(LocaleController.getString(str5, NUM));
                            arrayList2.add(Integer.valueOf(6));
                        } else if (this.selectedObject.isMusic()) {
                            arrayList.add(LocaleController.getString("SaveToMusic", NUM));
                            arrayList2.add(Integer.valueOf(10));
                            arrayList.add(LocaleController.getString(str5, NUM));
                            arrayList2.add(Integer.valueOf(6));
                        } else if (this.selectedObject.getDocument() != null) {
                            if (MessageObject.isNewGifDocument(this.selectedObject.getDocument())) {
                                arrayList.add(LocaleController.getString(str2, NUM));
                                arrayList2.add(Integer.valueOf(11));
                            }
                            arrayList.add(LocaleController.getString(str4, NUM));
                            arrayList2.add(Integer.valueOf(10));
                            arrayList.add(LocaleController.getString(str5, NUM));
                            arrayList2.add(Integer.valueOf(6));
                        } else {
                            arrayList.add(LocaleController.getString(str3, NUM));
                            arrayList2.add(Integer.valueOf(4));
                        }
                    } else if (messageType == 5) {
                        arrayList.add(LocaleController.getString("ApplyLocalizationFile", NUM));
                        arrayList2.add(Integer.valueOf(5));
                        arrayList.add(LocaleController.getString(str4, NUM));
                        arrayList2.add(Integer.valueOf(10));
                        arrayList.add(LocaleController.getString(str5, NUM));
                        arrayList2.add(Integer.valueOf(6));
                    } else if (messageType == 10) {
                        arrayList.add(LocaleController.getString("ApplyThemeFile", NUM));
                        arrayList2.add(Integer.valueOf(5));
                        arrayList.add(LocaleController.getString(str4, NUM));
                        arrayList2.add(Integer.valueOf(10));
                        arrayList.add(LocaleController.getString(str5, NUM));
                        arrayList2.add(Integer.valueOf(6));
                    } else if (messageType == 6) {
                        arrayList.add(LocaleController.getString(str3, NUM));
                        arrayList2.add(Integer.valueOf(7));
                        arrayList.add(LocaleController.getString(str4, NUM));
                        arrayList2.add(Integer.valueOf(10));
                        arrayList.add(LocaleController.getString(str5, NUM));
                        arrayList2.add(Integer.valueOf(6));
                    } else if (messageType == 7) {
                        if (this.selectedObject.isMask()) {
                            arrayList.add(LocaleController.getString("AddToMasks", NUM));
                        } else {
                            arrayList.add(LocaleController.getString("AddToStickers", NUM));
                        }
                        arrayList2.add(Integer.valueOf(9));
                    } else if (messageType == 8) {
                        User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.selectedObject.messageOwner.media.user_id));
                        if (!(user == null || user.id == UserConfig.getInstance(this.currentAccount).getClientUserId() || ContactsController.getInstance(this.currentAccount).contactsDict.get(Integer.valueOf(user.id)) != null)) {
                            arrayList.add(LocaleController.getString("AddContactTitle", NUM));
                            arrayList2.add(Integer.valueOf(15));
                        }
                        String str6 = this.selectedObject.messageOwner.media.phone_number;
                        if (!(str6 == null && str6.length() == 0)) {
                            arrayList.add(LocaleController.getString(str, NUM));
                            arrayList2.add(Integer.valueOf(16));
                            arrayList.add(LocaleController.getString("Call", NUM));
                            arrayList2.add(Integer.valueOf(17));
                        }
                    }
                }
                if (!arrayList2.isEmpty()) {
                    builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[0]), new -$$Lambda$ChannelAdminLogActivity$BZ9YJjVsQ77SD-mlnHBXJsrpGqE(this, arrayList2));
                    builder.setTitle(LocaleController.getString("Message", NUM));
                    showDialog(builder.create());
                }
            }
        }
    }

    public /* synthetic */ void lambda$createMenu$11$ChannelAdminLogActivity(ArrayList arrayList, DialogInterface dialogInterface, int i) {
        if (this.selectedObject != null && i >= 0 && i < arrayList.size()) {
            processSelectedOption(((Integer) arrayList.get(i)).intValue());
        }
    }

    private String getMessageContent(MessageObject messageObject, int i, boolean z) {
        String str = "";
        if (z) {
            int i2 = messageObject.messageOwner.from_id;
            if (i != i2) {
                String str2 = ":\n";
                StringBuilder stringBuilder;
                if (i2 > 0) {
                    User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(messageObject.messageOwner.from_id));
                    if (user != null) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(ContactsController.formatName(user.first_name, user.last_name));
                        stringBuilder.append(str2);
                        str = stringBuilder.toString();
                    }
                } else if (i2 < 0) {
                    Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-messageObject.messageOwner.from_id));
                    if (chat != null) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(chat.title);
                        stringBuilder.append(str2);
                        str = stringBuilder.toString();
                    }
                }
            }
        }
        StringBuilder stringBuilder2;
        if (messageObject.type != 0 || messageObject.messageOwner.message == null) {
            Message message = messageObject.messageOwner;
            if (message.media == null || message.message == null) {
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append(str);
                stringBuilder2.append(messageObject.messageText);
                return stringBuilder2.toString();
            }
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append(str);
            stringBuilder2.append(messageObject.messageOwner.message);
            return stringBuilder2.toString();
        }
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(str);
        stringBuilder2.append(messageObject.messageOwner.message);
        return stringBuilder2.toString();
    }

    private TextureView createTextureView(boolean z) {
        if (this.parentLayout == null) {
            return null;
        }
        if (this.roundVideoContainer == null) {
            if (VERSION.SDK_INT >= 21) {
                this.roundVideoContainer = new FrameLayout(getParentActivity()) {
                    public void setTranslationY(float f) {
                        super.setTranslationY(f);
                        ChannelAdminLogActivity.this.contentView.invalidate();
                    }
                };
                this.roundVideoContainer.setOutlineProvider(new ViewOutlineProvider() {
                    @TargetApi(21)
                    public void getOutline(View view, Outline outline) {
                        int i = AndroidUtilities.roundMessageSize;
                        outline.setOval(0, 0, i, i);
                    }
                });
                this.roundVideoContainer.setClipToOutline(true);
            } else {
                this.roundVideoContainer = new FrameLayout(getParentActivity()) {
                    /* Access modifiers changed, original: protected */
                    public void onSizeChanged(int i, int i2, int i3, int i4) {
                        super.onSizeChanged(i, i2, i3, i4);
                        ChannelAdminLogActivity.this.aspectPath.reset();
                        float f = (float) (i / 2);
                        ChannelAdminLogActivity.this.aspectPath.addCircle(f, (float) (i2 / 2), f, Direction.CW);
                        ChannelAdminLogActivity.this.aspectPath.toggleInverseFillType();
                    }

                    public void setTranslationY(float f) {
                        super.setTranslationY(f);
                        ChannelAdminLogActivity.this.contentView.invalidate();
                    }

                    public void setVisibility(int i) {
                        super.setVisibility(i);
                        if (i == 0) {
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
            if (z) {
                this.roundVideoContainer.addView(this.aspectRatioFrameLayout, LayoutHelper.createFrame(-1, -1.0f));
            }
            this.videoTextureView = new TextureView(getParentActivity());
            this.videoTextureView.setOpaque(false);
            this.aspectRatioFrameLayout.addView(this.videoTextureView, LayoutHelper.createFrame(-1, -1.0f));
        }
        if (this.roundVideoContainer.getParent() == null) {
            SizeNotifierFrameLayout sizeNotifierFrameLayout = this.contentView;
            FrameLayout frameLayout = this.roundVideoContainer;
            int i = AndroidUtilities.roundMessageSize;
            sizeNotifierFrameLayout.addView(frameLayout, 1, new FrameLayout.LayoutParams(i, i));
        }
        this.roundVideoContainer.setVisibility(4);
        this.aspectRatioFrameLayout.setDrawingReady(false);
        return this.videoTextureView;
    }

    private void destroyTextureView() {
        FrameLayout frameLayout = this.roundVideoContainer;
        if (frameLayout != null && frameLayout.getParent() != null) {
            this.contentView.removeView(this.roundVideoContainer);
            this.aspectRatioFrameLayout.setDrawingReady(false);
            this.roundVideoContainer.setVisibility(4);
            if (VERSION.SDK_INT < 21) {
                this.roundVideoContainer.setLayerType(0, null);
            }
        }
    }

    /* JADX WARNING: Missing block: B:83:0x022d, code skipped:
            if (r0.exists() != false) goto L_0x0231;
     */
    private void processSelectedOption(int r11) {
        /*
        r10 = this;
        r0 = r10.selectedObject;
        if (r0 != 0) goto L_0x0005;
    L_0x0004:
        return;
    L_0x0005:
        r1 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
        r2 = 3;
        r3 = 4;
        r4 = 23;
        r5 = "android.permission.WRITE_EXTERNAL_STORAGE";
        r6 = 0;
        r7 = 1;
        r8 = 0;
        switch(r11) {
            case 3: goto L_0x0388;
            case 4: goto L_0x0329;
            case 5: goto L_0x0212;
            case 6: goto L_0x0187;
            case 7: goto L_0x0136;
            case 8: goto L_0x0013;
            case 9: goto L_0x011e;
            case 10: goto L_0x0098;
            case 11: goto L_0x0087;
            case 12: goto L_0x0013;
            case 13: goto L_0x0013;
            case 14: goto L_0x0013;
            case 15: goto L_0x0058;
            case 16: goto L_0x004d;
            case 17: goto L_0x0015;
            default: goto L_0x0013;
        };
    L_0x0013:
        goto L_0x038f;
    L_0x0015:
        r0 = new android.content.Intent;	 Catch:{ Exception -> 0x0047 }
        r2 = "android.intent.action.DIAL";
        r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0047 }
        r3.<init>();	 Catch:{ Exception -> 0x0047 }
        r4 = "tel:";
        r3.append(r4);	 Catch:{ Exception -> 0x0047 }
        r4 = r10.selectedObject;	 Catch:{ Exception -> 0x0047 }
        r4 = r4.messageOwner;	 Catch:{ Exception -> 0x0047 }
        r4 = r4.media;	 Catch:{ Exception -> 0x0047 }
        r4 = r4.phone_number;	 Catch:{ Exception -> 0x0047 }
        r3.append(r4);	 Catch:{ Exception -> 0x0047 }
        r3 = r3.toString();	 Catch:{ Exception -> 0x0047 }
        r3 = android.net.Uri.parse(r3);	 Catch:{ Exception -> 0x0047 }
        r0.<init>(r2, r3);	 Catch:{ Exception -> 0x0047 }
        r2 = NUM; // 0x10000000 float:2.5243549E-29 double:1.32624737E-315;
        r0.addFlags(r2);	 Catch:{ Exception -> 0x0047 }
        r2 = r10.getParentActivity();	 Catch:{ Exception -> 0x0047 }
        r2.startActivityForResult(r0, r1);	 Catch:{ Exception -> 0x0047 }
        goto L_0x038f;
    L_0x0047:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x038f;
    L_0x004d:
        r0 = r0.messageOwner;
        r0 = r0.media;
        r0 = r0.phone_number;
        org.telegram.messenger.AndroidUtilities.addToClipboard(r0);
        goto L_0x038f;
    L_0x0058:
        r0 = new android.os.Bundle;
        r0.<init>();
        r1 = r10.selectedObject;
        r1 = r1.messageOwner;
        r1 = r1.media;
        r1 = r1.user_id;
        r2 = "user_id";
        r0.putInt(r2, r1);
        r1 = r10.selectedObject;
        r1 = r1.messageOwner;
        r1 = r1.media;
        r1 = r1.phone_number;
        r2 = "phone";
        r0.putString(r2, r1);
        r1 = "addContact";
        r0.putBoolean(r1, r7);
        r1 = new org.telegram.ui.ContactAddActivity;
        r1.<init>(r0);
        r10.presentFragment(r1);
        goto L_0x038f;
    L_0x0087:
        r0 = r0.getDocument();
        r1 = r10.currentAccount;
        r1 = org.telegram.messenger.MessagesController.getInstance(r1);
        r2 = r10.selectedObject;
        r1.saveGif(r2, r0);
        goto L_0x038f;
    L_0x0098:
        r0 = android.os.Build.VERSION.SDK_INT;
        if (r0 < r4) goto L_0x00b4;
    L_0x009c:
        r0 = r10.getParentActivity();
        r0 = r0.checkSelfPermission(r5);
        if (r0 == 0) goto L_0x00b4;
    L_0x00a6:
        r0 = r10.getParentActivity();
        r1 = new java.lang.String[r7];
        r1[r6] = r5;
        r0.requestPermissions(r1, r3);
        r10.selectedObject = r8;
        return;
    L_0x00b4:
        r0 = r10.selectedObject;
        r0 = r0.getDocument();
        r0 = org.telegram.messenger.FileLoader.getDocumentFileName(r0);
        r1 = android.text.TextUtils.isEmpty(r0);
        if (r1 == 0) goto L_0x00ca;
    L_0x00c4:
        r0 = r10.selectedObject;
        r0 = r0.getFileName();
    L_0x00ca:
        r1 = r10.selectedObject;
        r1 = r1.messageOwner;
        r1 = r1.attachPath;
        if (r1 == 0) goto L_0x00e4;
    L_0x00d2:
        r3 = r1.length();
        if (r3 <= 0) goto L_0x00e4;
    L_0x00d8:
        r3 = new java.io.File;
        r3.<init>(r1);
        r3 = r3.exists();
        if (r3 != 0) goto L_0x00e4;
    L_0x00e3:
        r1 = r8;
    L_0x00e4:
        if (r1 == 0) goto L_0x00ec;
    L_0x00e6:
        r3 = r1.length();
        if (r3 != 0) goto L_0x00f8;
    L_0x00ec:
        r1 = r10.selectedObject;
        r1 = r1.messageOwner;
        r1 = org.telegram.messenger.FileLoader.getPathToMessage(r1);
        r1 = r1.toString();
    L_0x00f8:
        r3 = r10.getParentActivity();
        r4 = r10.selectedObject;
        r4 = r4.isMusic();
        if (r4 == 0) goto L_0x0105;
    L_0x0104:
        goto L_0x0106;
    L_0x0105:
        r2 = 2;
    L_0x0106:
        r4 = r10.selectedObject;
        r4 = r4.getDocument();
        if (r4 == 0) goto L_0x0117;
    L_0x010e:
        r4 = r10.selectedObject;
        r4 = r4.getDocument();
        r4 = r4.mime_type;
        goto L_0x0119;
    L_0x0117:
        r4 = "";
    L_0x0119:
        org.telegram.messenger.MediaController.saveFile(r1, r3, r2, r0, r4);
        goto L_0x038f;
    L_0x011e:
        r0 = new org.telegram.ui.Components.StickersAlert;
        r2 = r10.getParentActivity();
        r1 = r10.selectedObject;
        r4 = r1.getInputStickerSet();
        r5 = 0;
        r6 = 0;
        r1 = r0;
        r3 = r10;
        r1.<init>(r2, r3, r4, r5, r6);
        r10.showDialog(r0);
        goto L_0x038f;
    L_0x0136:
        r0 = r0.messageOwner;
        r0 = r0.attachPath;
        if (r0 == 0) goto L_0x014e;
    L_0x013c:
        r1 = r0.length();
        if (r1 <= 0) goto L_0x014e;
    L_0x0142:
        r1 = new java.io.File;
        r1.<init>(r0);
        r1 = r1.exists();
        if (r1 != 0) goto L_0x014e;
    L_0x014d:
        r0 = r8;
    L_0x014e:
        if (r0 == 0) goto L_0x0156;
    L_0x0150:
        r1 = r0.length();
        if (r1 != 0) goto L_0x0162;
    L_0x0156:
        r0 = r10.selectedObject;
        r0 = r0.messageOwner;
        r0 = org.telegram.messenger.FileLoader.getPathToMessage(r0);
        r0 = r0.toString();
    L_0x0162:
        r1 = android.os.Build.VERSION.SDK_INT;
        if (r1 < r4) goto L_0x017e;
    L_0x0166:
        r1 = r10.getParentActivity();
        r1 = r1.checkSelfPermission(r5);
        if (r1 == 0) goto L_0x017e;
    L_0x0170:
        r0 = r10.getParentActivity();
        r1 = new java.lang.String[r7];
        r1[r6] = r5;
        r0.requestPermissions(r1, r3);
        r10.selectedObject = r8;
        return;
    L_0x017e:
        r1 = r10.getParentActivity();
        org.telegram.messenger.MediaController.saveFile(r0, r1, r6, r8, r8);
        goto L_0x038f;
    L_0x0187:
        r0 = r0.messageOwner;
        r0 = r0.attachPath;
        if (r0 == 0) goto L_0x019f;
    L_0x018d:
        r2 = r0.length();
        if (r2 <= 0) goto L_0x019f;
    L_0x0193:
        r2 = new java.io.File;
        r2.<init>(r0);
        r2 = r2.exists();
        if (r2 != 0) goto L_0x019f;
    L_0x019e:
        r0 = r8;
    L_0x019f:
        if (r0 == 0) goto L_0x01a7;
    L_0x01a1:
        r2 = r0.length();
        if (r2 != 0) goto L_0x01b3;
    L_0x01a7:
        r0 = r10.selectedObject;
        r0 = r0.messageOwner;
        r0 = org.telegram.messenger.FileLoader.getPathToMessage(r0);
        r0 = r0.toString();
    L_0x01b3:
        r2 = new android.content.Intent;
        r3 = "android.intent.action.SEND";
        r2.<init>(r3);
        r3 = r10.selectedObject;
        r3 = r3.getDocument();
        r3 = r3.mime_type;
        r2.setType(r3);
        r3 = android.os.Build.VERSION.SDK_INT;
        r4 = 24;
        r5 = "android.intent.extra.STREAM";
        if (r3 < r4) goto L_0x01f0;
    L_0x01cd:
        r3 = r10.getParentActivity();	 Catch:{ Exception -> 0x01e3 }
        r4 = "org.telegram.messenger.beta.provider";
        r6 = new java.io.File;	 Catch:{ Exception -> 0x01e3 }
        r6.<init>(r0);	 Catch:{ Exception -> 0x01e3 }
        r3 = androidx.core.content.FileProvider.getUriForFile(r3, r4, r6);	 Catch:{ Exception -> 0x01e3 }
        r2.putExtra(r5, r3);	 Catch:{ Exception -> 0x01e3 }
        r2.setFlags(r7);	 Catch:{ Exception -> 0x01e3 }
        goto L_0x01fc;
    L_0x01e3:
        r3 = new java.io.File;
        r3.<init>(r0);
        r0 = android.net.Uri.fromFile(r3);
        r2.putExtra(r5, r0);
        goto L_0x01fc;
    L_0x01f0:
        r3 = new java.io.File;
        r3.<init>(r0);
        r0 = android.net.Uri.fromFile(r3);
        r2.putExtra(r5, r0);
    L_0x01fc:
        r0 = r10.getParentActivity();
        r3 = NUM; // 0x7f0e0a56 float:1.8880404E38 double:1.053163464E-314;
        r4 = "ShareFile";
        r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
        r2 = android.content.Intent.createChooser(r2, r3);
        r0.startActivityForResult(r2, r1);
        goto L_0x038f;
    L_0x0212:
        r0 = r0.messageOwner;
        r0 = r0.attachPath;
        if (r0 == 0) goto L_0x0230;
    L_0x0218:
        r0 = r0.length();
        if (r0 == 0) goto L_0x0230;
    L_0x021e:
        r0 = new java.io.File;
        r1 = r10.selectedObject;
        r1 = r1.messageOwner;
        r1 = r1.attachPath;
        r0.<init>(r1);
        r1 = r0.exists();
        if (r1 == 0) goto L_0x0230;
    L_0x022f:
        goto L_0x0231;
    L_0x0230:
        r0 = r8;
    L_0x0231:
        if (r0 != 0) goto L_0x0242;
    L_0x0233:
        r1 = r10.selectedObject;
        r1 = r1.messageOwner;
        r1 = org.telegram.messenger.FileLoader.getPathToMessage(r1);
        r2 = r1.exists();
        if (r2 == 0) goto L_0x0242;
    L_0x0241:
        r0 = r1;
    L_0x0242:
        if (r0 == 0) goto L_0x038f;
    L_0x0244:
        r1 = r0.getName();
        r1 = r1.toLowerCase();
        r2 = "attheme";
        r1 = r1.endsWith(r2);
        r2 = NUM; // 0x7f0e0775 float:1.887891E38 double:1.0531631E-314;
        r3 = "OK";
        r4 = NUM; // 0x7f0e0100 float:1.8875557E38 double:1.053162283E-314;
        r5 = "AppName";
        if (r1 == 0) goto L_0x02df;
    L_0x025e:
        r1 = r10.chatLayoutManager;
        r6 = -1;
        if (r1 == 0) goto L_0x0292;
    L_0x0263:
        r1 = r1.findLastVisibleItemPosition();
        r9 = r10.chatLayoutManager;
        r9 = r9.getItemCount();
        r9 = r9 - r7;
        if (r1 >= r9) goto L_0x0290;
    L_0x0270:
        r1 = r10.chatLayoutManager;
        r1 = r1.findFirstVisibleItemPosition();
        r10.scrollToPositionOnRecreate = r1;
        r1 = r10.chatListView;
        r9 = r10.scrollToPositionOnRecreate;
        r1 = r1.findViewHolderForAdapterPosition(r9);
        r1 = (org.telegram.ui.Components.RecyclerListView.Holder) r1;
        if (r1 == 0) goto L_0x028d;
    L_0x0284:
        r1 = r1.itemView;
        r1 = r1.getTop();
        r10.scrollToOffsetOnRecreate = r1;
        goto L_0x0292;
    L_0x028d:
        r10.scrollToPositionOnRecreate = r6;
        goto L_0x0292;
    L_0x0290:
        r10.scrollToPositionOnRecreate = r6;
    L_0x0292:
        r1 = r10.selectedObject;
        r1 = r1.getDocumentName();
        r0 = org.telegram.ui.ActionBar.Theme.applyThemeFile(r0, r1, r8, r7);
        if (r0 == 0) goto L_0x02a8;
    L_0x029e:
        r1 = new org.telegram.ui.ThemePreviewActivity;
        r1.<init>(r0);
        r10.presentFragment(r1);
        goto L_0x038f;
    L_0x02a8:
        r10.scrollToPositionOnRecreate = r6;
        r0 = r10.getParentActivity();
        if (r0 != 0) goto L_0x02b3;
    L_0x02b0:
        r10.selectedObject = r8;
        return;
    L_0x02b3:
        r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r1 = r10.getParentActivity();
        r0.<init>(r1);
        r1 = org.telegram.messenger.LocaleController.getString(r5, r4);
        r0.setTitle(r1);
        r1 = NUM; // 0x7f0e0581 float:1.8877895E38 double:1.053162853E-314;
        r4 = "IncorrectTheme";
        r1 = org.telegram.messenger.LocaleController.getString(r4, r1);
        r0.setMessage(r1);
        r1 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r0.setPositiveButton(r1, r8);
        r0 = r0.create();
        r10.showDialog(r0);
        goto L_0x038f;
    L_0x02df:
        r1 = org.telegram.messenger.LocaleController.getInstance();
        r6 = r10.currentAccount;
        r0 = r1.applyLanguageFile(r0, r6);
        if (r0 == 0) goto L_0x02f5;
    L_0x02eb:
        r0 = new org.telegram.ui.LanguageSelectActivity;
        r0.<init>();
        r10.presentFragment(r0);
        goto L_0x038f;
    L_0x02f5:
        r0 = r10.getParentActivity();
        if (r0 != 0) goto L_0x02fe;
    L_0x02fb:
        r10.selectedObject = r8;
        return;
    L_0x02fe:
        r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r1 = r10.getParentActivity();
        r0.<init>(r1);
        r1 = org.telegram.messenger.LocaleController.getString(r5, r4);
        r0.setTitle(r1);
        r1 = NUM; // 0x7f0e0580 float:1.8877893E38 double:1.0531628523E-314;
        r4 = "IncorrectLocalization";
        r1 = org.telegram.messenger.LocaleController.getString(r4, r1);
        r0.setMessage(r1);
        r1 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r0.setPositiveButton(r1, r8);
        r0 = r0.create();
        r10.showDialog(r0);
        goto L_0x038f;
    L_0x0329:
        r0 = r0.messageOwner;
        r0 = r0.attachPath;
        if (r0 == 0) goto L_0x0341;
    L_0x032f:
        r1 = r0.length();
        if (r1 <= 0) goto L_0x0341;
    L_0x0335:
        r1 = new java.io.File;
        r1.<init>(r0);
        r1 = r1.exists();
        if (r1 != 0) goto L_0x0341;
    L_0x0340:
        r0 = r8;
    L_0x0341:
        if (r0 == 0) goto L_0x0349;
    L_0x0343:
        r1 = r0.length();
        if (r1 != 0) goto L_0x0355;
    L_0x0349:
        r0 = r10.selectedObject;
        r0 = r0.messageOwner;
        r0 = org.telegram.messenger.FileLoader.getPathToMessage(r0);
        r0 = r0.toString();
    L_0x0355:
        r1 = r10.selectedObject;
        r1 = r1.type;
        if (r1 == r2) goto L_0x035d;
    L_0x035b:
        if (r1 != r7) goto L_0x038f;
    L_0x035d:
        r1 = android.os.Build.VERSION.SDK_INT;
        if (r1 < r4) goto L_0x0379;
    L_0x0361:
        r1 = r10.getParentActivity();
        r1 = r1.checkSelfPermission(r5);
        if (r1 == 0) goto L_0x0379;
    L_0x036b:
        r0 = r10.getParentActivity();
        r1 = new java.lang.String[r7];
        r1[r6] = r5;
        r0.requestPermissions(r1, r3);
        r10.selectedObject = r8;
        return;
    L_0x0379:
        r1 = r10.getParentActivity();
        r3 = r10.selectedObject;
        r3 = r3.type;
        if (r3 != r2) goto L_0x0384;
    L_0x0383:
        r6 = 1;
    L_0x0384:
        org.telegram.messenger.MediaController.saveFile(r0, r1, r6, r8, r8);
        goto L_0x038f;
    L_0x0388:
        r0 = r10.getMessageContent(r0, r6, r7);
        org.telegram.messenger.AndroidUtilities.addToClipboard(r0);
    L_0x038f:
        r10.selectedObject = r8;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChannelAdminLogActivity.processSelectedOption(int):void");
    }

    private int getMessageType(MessageObject messageObject) {
        if (messageObject == null) {
            return -1;
        }
        int i = messageObject.type;
        if (i == 6) {
            return -1;
        }
        if (i == 10 || i == 11 || i == 16) {
            return messageObject.getId() == 0 ? -1 : 1;
        } else {
            if (messageObject.isVoice()) {
                return 2;
            }
            if (messageObject.isSticker() || messageObject.isAnimatedSticker()) {
                InputStickerSet inputStickerSet = messageObject.getInputStickerSet();
                if (inputStickerSet instanceof TL_inputStickerSetID) {
                    if (!MediaDataController.getInstance(this.currentAccount).isStickerPackInstalled(inputStickerSet.id)) {
                        return 7;
                    }
                } else if (!(inputStickerSet instanceof TL_inputStickerSetShortName) || MediaDataController.getInstance(this.currentAccount).isStickerPackInstalled(inputStickerSet.short_name)) {
                    return 2;
                } else {
                    return 7;
                }
            } else if ((!messageObject.isRoundVideo() || (messageObject.isRoundVideo() && BuildVars.DEBUG_VERSION)) && ((messageObject.messageOwner.media instanceof TL_messageMediaPhoto) || messageObject.getDocument() != null || messageObject.isMusic() || messageObject.isVideo())) {
                Object obj = null;
                String str = messageObject.messageOwner.attachPath;
                if (!(str == null || str.length() == 0 || !new File(messageObject.messageOwner.attachPath).exists())) {
                    obj = 1;
                }
                if (obj == null && FileLoader.getPathToMessage(messageObject.messageOwner).exists()) {
                    obj = 1;
                }
                if (obj != null) {
                    if (messageObject.getDocument() != null) {
                        String str2 = messageObject.getDocument().mime_type;
                        if (str2 != null) {
                            if (messageObject.getDocumentName().toLowerCase().endsWith("attheme")) {
                                return 10;
                            }
                            if (str2.endsWith("/xml")) {
                                return 5;
                            }
                            if (str2.endsWith("/png") || str2.endsWith("/jpg") || str2.endsWith("/jpeg")) {
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
        TL_channels_getParticipants tL_channels_getParticipants = new TL_channels_getParticipants();
        tL_channels_getParticipants.channel = MessagesController.getInputChannel(this.currentChat);
        tL_channels_getParticipants.filter = new TL_channelParticipantsAdmins();
        tL_channels_getParticipants.offset = 0;
        tL_channels_getParticipants.limit = 200;
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_getParticipants, new -$$Lambda$ChannelAdminLogActivity$v_o39r5RzWtyA17rINStweDzJAI(this)), this.classGuid);
    }

    public /* synthetic */ void lambda$loadAdmins$13$ChannelAdminLogActivity(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$ChannelAdminLogActivity$nOrNBbemB40UwQEfR8VXtwD17_w(this, tL_error, tLObject));
    }

    public /* synthetic */ void lambda$null$12$ChannelAdminLogActivity(TL_error tL_error, TLObject tLObject) {
        if (tL_error == null) {
            TL_channels_channelParticipants tL_channels_channelParticipants = (TL_channels_channelParticipants) tLObject;
            MessagesController.getInstance(this.currentAccount).putUsers(tL_channels_channelParticipants.users, false);
            this.admins = tL_channels_channelParticipants.participants;
            Dialog dialog = this.visibleDialog;
            if (dialog instanceof AdminLogFilterAlert) {
                ((AdminLogFilterAlert) dialog).setCurrentAdmins(this.admins);
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public void onRemoveFromParent() {
        MediaController.getInstance().setTextureView(this.videoTextureView, null, null, false);
    }

    private void hideFloatingDateView(boolean z) {
        if (this.floatingDateView.getTag() != null && !this.currentFloatingDateOnScreen) {
            if (!this.scrollingFloatingDate || this.currentFloatingTopIsNotMessage) {
                this.floatingDateView.setTag(null);
                AnimatorSet animatorSet;
                if (z) {
                    this.floatingDateAnimation = new AnimatorSet();
                    this.floatingDateAnimation.setDuration(150);
                    animatorSet = this.floatingDateAnimation;
                    Animator[] animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.floatingDateView, "alpha", new float[]{0.0f});
                    animatorSet.playTogether(animatorArr);
                    this.floatingDateAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (animator.equals(ChannelAdminLogActivity.this.floatingDateAnimation)) {
                                ChannelAdminLogActivity.this.floatingDateAnimation = null;
                            }
                        }
                    });
                    this.floatingDateAnimation.setStartDelay(500);
                    this.floatingDateAnimation.start();
                    return;
                }
                animatorSet = this.floatingDateAnimation;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.floatingDateAnimation = null;
                }
                this.floatingDateView.setAlpha(0.0f);
            }
        }
    }

    private void checkScrollForLoad(boolean z) {
        LinearLayoutManager linearLayoutManager = this.chatLayoutManager;
        if (linearLayoutManager != null && !this.paused) {
            int i;
            int findFirstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
            if (findFirstVisibleItemPosition == -1) {
                i = 0;
            } else {
                i = Math.abs(this.chatLayoutManager.findLastVisibleItemPosition() - findFirstVisibleItemPosition) + 1;
            }
            if (i > 0) {
                this.chatAdapter.getItemCount();
                if (findFirstVisibleItemPosition <= (z ? 25 : 5) && !this.loading && !this.endReached) {
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
        Object obj;
        int childCount = this.chatListView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.chatListView.getChildAt(i);
            if (childAt instanceof ChatMessageCell) {
                ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                MessageObject messageObject = chatMessageCell.getMessageObject();
                if (this.roundVideoContainer != null && messageObject.isRoundVideo() && MediaController.getInstance().isPlayingMessage(messageObject)) {
                    ImageReceiver photoImage = chatMessageCell.getPhotoImage();
                    this.roundVideoContainer.setTranslationX((float) photoImage.getImageX());
                    this.roundVideoContainer.setTranslationY((float) ((this.fragmentView.getPaddingTop() + chatMessageCell.getTop()) + photoImage.getImageY()));
                    this.fragmentView.invalidate();
                    this.roundVideoContainer.invalidate();
                    obj = 1;
                    break;
                }
            }
        }
        obj = null;
        if (this.roundVideoContainer != null) {
            MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
            if (obj == null) {
                this.roundVideoContainer.setTranslationY((float) ((-AndroidUtilities.roundMessageSize) - 100));
                this.fragmentView.invalidate();
                if (playingMessageObject != null && playingMessageObject.isRoundVideo()) {
                    if (this.checkTextureViewPosition || PipRoundVideoView.getInstance() != null) {
                        MediaController.getInstance().setCurrentVideoVisible(false);
                        return;
                    }
                    return;
                }
                return;
            }
            MediaController.getInstance().setCurrentVideoVisible(true);
        }
    }

    private void updateMessagesVisisblePart() {
        RecyclerListView recyclerListView = this.chatListView;
        if (recyclerListView != null) {
            MessageObject messageObject;
            boolean z;
            int childCount = recyclerListView.getChildCount();
            int measuredHeight = this.chatListView.getMeasuredHeight();
            int i = 0;
            Object obj = null;
            View view = null;
            View view2 = null;
            View view3 = null;
            int i2 = Integer.MAX_VALUE;
            int i3 = Integer.MAX_VALUE;
            while (i < childCount) {
                int i4;
                int i5;
                View childAt = this.chatListView.getChildAt(i);
                boolean z2 = childAt instanceof ChatMessageCell;
                if (z2) {
                    ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                    int top = chatMessageCell.getTop();
                    chatMessageCell.getBottom();
                    int i6 = top >= 0 ? 0 : -top;
                    int measuredHeight2 = chatMessageCell.getMeasuredHeight();
                    if (measuredHeight2 > measuredHeight) {
                        measuredHeight2 = i6 + measuredHeight;
                    }
                    i4 = childCount;
                    i5 = measuredHeight;
                    chatMessageCell.setVisiblePart(i6, measuredHeight2 - i6, (this.contentView.getHeightWithKeyboard() - AndroidUtilities.dp(48.0f)) - this.chatListView.getTop());
                    messageObject = chatMessageCell.getMessageObject();
                    if (this.roundVideoContainer != null && messageObject.isRoundVideo() && MediaController.getInstance().isPlayingMessage(messageObject)) {
                        ImageReceiver photoImage = chatMessageCell.getPhotoImage();
                        this.roundVideoContainer.setTranslationX((float) photoImage.getImageX());
                        this.roundVideoContainer.setTranslationY((float) ((this.fragmentView.getPaddingTop() + top) + photoImage.getImageY()));
                        this.fragmentView.invalidate();
                        this.roundVideoContainer.invalidate();
                        obj = 1;
                    }
                } else {
                    i4 = childCount;
                    i5 = measuredHeight;
                }
                if (childAt.getBottom() > this.chatListView.getPaddingTop()) {
                    childCount = childAt.getBottom();
                    if (childCount < i2) {
                        if (z2 || (childAt instanceof ChatActionCell)) {
                            view = childAt;
                        }
                        i2 = childCount;
                        view3 = childAt;
                    }
                    if ((childAt instanceof ChatActionCell) && ((ChatActionCell) childAt).getMessageObject().isDateObject) {
                        if (childAt.getAlpha() != 1.0f) {
                            childAt.setAlpha(1.0f);
                        }
                        if (childCount < i3) {
                            i3 = childCount;
                            view2 = childAt;
                        }
                    }
                }
                i++;
                childCount = i4;
                measuredHeight = i5;
            }
            FrameLayout frameLayout = this.roundVideoContainer;
            if (frameLayout != null) {
                if (obj == null) {
                    frameLayout.setTranslationY((float) ((-AndroidUtilities.roundMessageSize) - 100));
                    this.fragmentView.invalidate();
                    messageObject = MediaController.getInstance().getPlayingMessageObject();
                    if (messageObject != null && messageObject.isRoundVideo() && this.checkTextureViewPosition) {
                        MediaController.getInstance().setCurrentVideoVisible(false);
                    }
                } else {
                    MediaController.getInstance().setCurrentVideoVisible(true);
                }
            }
            if (view != null) {
                if (view instanceof ChatMessageCell) {
                    messageObject = ((ChatMessageCell) view).getMessageObject();
                } else {
                    messageObject = ((ChatActionCell) view).getMessageObject();
                }
                z = false;
                this.floatingDateView.setCustomDate(messageObject.messageOwner.date, false, true);
            } else {
                z = false;
            }
            this.currentFloatingDateOnScreen = z;
            if (!((view3 instanceof ChatMessageCell) || (view3 instanceof ChatActionCell))) {
                z = true;
            }
            this.currentFloatingTopIsNotMessage = z;
            if (view2 != null) {
                if (view2.getTop() > this.chatListView.getPaddingTop() || this.currentFloatingTopIsNotMessage) {
                    if (view2.getAlpha() != 1.0f) {
                        view2.setAlpha(1.0f);
                    }
                    hideFloatingDateView(this.currentFloatingTopIsNotMessage ^ 1);
                } else {
                    if (view2.getAlpha() != 0.0f) {
                        view2.setAlpha(0.0f);
                    }
                    AnimatorSet animatorSet = this.floatingDateAnimation;
                    if (animatorSet != null) {
                        animatorSet.cancel();
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
                measuredHeight = view2.getBottom() - this.chatListView.getPaddingTop();
                if (measuredHeight <= this.floatingDateView.getMeasuredHeight() || measuredHeight >= this.floatingDateView.getMeasuredHeight() * 2) {
                    this.floatingDateView.setTranslationY(0.0f);
                } else {
                    ChatActionCell chatActionCell = this.floatingDateView;
                    chatActionCell.setTranslationY((float) (((-chatActionCell.getMeasuredHeight()) * 2) + measuredHeight));
                }
            } else {
                hideFloatingDateView(true);
                this.floatingDateView.setTranslationY(0.0f);
            }
        }
    }

    public void onTransitionAnimationStart(boolean z, boolean z2) {
        if (z) {
            NotificationCenter.getInstance(this.currentAccount).setAllowedNotificationsDutingAnimation(new int[]{NotificationCenter.chatInfoDidLoad, NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.messagesDidLoad, NotificationCenter.botKeyboardDidLoad});
            NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(true);
            this.openAnimationEnded = false;
        }
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(false);
            this.openAnimationEnded = true;
        }
    }

    public void onResume() {
        super.onResume();
        SizeNotifierFrameLayout sizeNotifierFrameLayout = this.contentView;
        if (sizeNotifierFrameLayout != null) {
            sizeNotifierFrameLayout.onResume();
        }
        this.paused = false;
        checkScrollForLoad(false);
        if (this.wasPaused) {
            this.wasPaused = false;
            ChatActivityAdapter chatActivityAdapter = this.chatAdapter;
            if (chatActivityAdapter != null) {
                chatActivityAdapter.notifyDataSetChanged();
            }
        }
    }

    public void onPause() {
        super.onPause();
        SizeNotifierFrameLayout sizeNotifierFrameLayout = this.contentView;
        if (sizeNotifierFrameLayout != null) {
            sizeNotifierFrameLayout.onPause();
        }
        this.paused = true;
        this.wasPaused = true;
    }

    public void openVCard(User user, String str, String str2, String str3) {
        try {
            File sharingDirectory = AndroidUtilities.getSharingDirectory();
            sharingDirectory.mkdirs();
            File file = new File(sharingDirectory, "vcard.vcf");
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(str);
            bufferedWriter.close();
            showDialog(new PhonebookShareAlert(this, null, user, null, file, ContactsController.formatName(str2, str3)));
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        Dialog dialog = this.visibleDialog;
        if (dialog instanceof DatePickerDialog) {
            dialog.dismiss();
        }
    }

    private void alertUserOpenError(MessageObject messageObject) {
        if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
            if (messageObject.type == 3) {
                builder.setMessage(LocaleController.getString("NoPlayerInstalled", NUM));
            } else {
                builder.setMessage(LocaleController.formatString("NoHandleAppInstalled", NUM, messageObject.getDocument().mime_type));
            }
            showDialog(builder.create());
        }
    }

    public Chat getCurrentChat() {
        return this.currentChat;
    }

    private void addCanBanUser(Bundle bundle, int i) {
        Chat chat = this.currentChat;
        if (chat.megagroup && this.admins != null && ChatObject.canBlockUsers(chat)) {
            int i2 = 0;
            while (i2 < this.admins.size()) {
                ChannelParticipant channelParticipant = (ChannelParticipant) this.admins.get(i2);
                if (channelParticipant.user_id == i) {
                    if (!channelParticipant.can_edit) {
                        return;
                    }
                    bundle.putInt("ban_chat_id", this.currentChat.id);
                } else {
                    i2++;
                }
            }
            bundle.putInt("ban_chat_id", this.currentChat.id);
        }
    }

    public void showOpenUrlAlert(String str, boolean z) {
        if (Browser.isInternalUrl(str, null) || !z) {
            Browser.openUrl(getParentActivity(), str, true);
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("OpenUrlTitle", NUM));
        builder.setMessage(LocaleController.formatString("OpenUrlAlert2", NUM, str));
        builder.setPositiveButton(LocaleController.getString("Open", NUM), new -$$Lambda$ChannelAdminLogActivity$iAIppxrAcTSiaPaqrhtDiQvqAXs(this, str));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
        showDialog(builder.create());
    }

    public /* synthetic */ void lambda$showOpenUrlAlert$14$ChannelAdminLogActivity(String str, DialogInterface dialogInterface, int i) {
        Browser.openUrl(getParentActivity(), str, true);
    }

    private void removeMessageObject(MessageObject messageObject) {
        int indexOf = this.messages.indexOf(messageObject);
        if (indexOf != -1) {
            this.messages.remove(indexOf);
            ChatActivityAdapter chatActivityAdapter = this.chatAdapter;
            if (chatActivityAdapter != null) {
                chatActivityAdapter.notifyItemRemoved(((chatActivityAdapter.messagesStartRow + this.messages.size()) - indexOf) - 1);
            }
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[210];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, 0, null, null, null, null, "chat_wallpaper");
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[2] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, "actionBarDefaultSubmenuBackground");
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, "actionBarDefaultSubmenuItem");
        themeDescriptionArr[7] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "actionBarDefaultSubmenuItemIcon");
        themeDescriptionArr[8] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[9] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[10] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        themeDescriptionArr[11] = new ThemeDescription(this.avatarContainer.getTitleTextView(), ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "actionBarDefaultTitle");
        themeDescriptionArr[12] = new ThemeDescription(this.avatarContainer.getSubtitleTextView(), ThemeDescription.FLAG_TEXTCOLOR, null, new Paint[]{Theme.chat_statusPaint, Theme.chat_statusRecordPaint}, null, null, "actionBarDefaultSubtitle", null);
        themeDescriptionArr[13] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        themeDescriptionArr[14] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.avatar_savedDrawable}, null, "avatar_text");
        themeDescriptionArr[15] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "avatar_backgroundRed");
        themeDescriptionArr[16] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "avatar_backgroundOrange");
        themeDescriptionArr[17] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "avatar_backgroundViolet");
        themeDescriptionArr[18] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "avatar_backgroundGreen");
        themeDescriptionArr[19] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "avatar_backgroundCyan");
        themeDescriptionArr[20] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "avatar_backgroundBlue");
        themeDescriptionArr[21] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "avatar_backgroundPink");
        themeDescriptionArr[22] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "avatar_nameInMessageRed");
        themeDescriptionArr[23] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "avatar_nameInMessageOrange");
        themeDescriptionArr[24] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "avatar_nameInMessageViolet");
        themeDescriptionArr[25] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "avatar_nameInMessageGreen");
        themeDescriptionArr[26] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "avatar_nameInMessageCyan");
        themeDescriptionArr[27] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "avatar_nameInMessageBlue");
        themeDescriptionArr[28] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "avatar_nameInMessagePink");
        themeDescriptionArr[29] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, null, "chat_inBubble");
        themeDescriptionArr[30] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, null, "chat_inBubbleSelected");
        themeDescriptionArr[31] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInDrawable.getShadowDrawable(), Theme.chat_msgInMediaDrawable.getShadowDrawable()}, null, "chat_inBubbleShadow");
        themeDescriptionArr[32] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, "chat_outBubble");
        themeDescriptionArr[33] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, "chat_outBubbleGradient");
        themeDescriptionArr[34] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, null, "chat_outBubbleSelected");
        themeDescriptionArr[35] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutDrawable.getShadowDrawable(), Theme.chat_msgOutMediaDrawable.getShadowDrawable()}, null, "chat_outBubbleShadow");
        themeDescriptionArr[36] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ChatActionCell.class}, Theme.chat_actionTextPaint, null, null, "chat_serviceText");
        themeDescriptionArr[37] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{ChatActionCell.class}, Theme.chat_actionTextPaint, null, null, "chat_serviceLink");
        themeDescriptionArr[38] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_shareIconDrawable, Theme.chat_botInlineDrawable, Theme.chat_botLinkDrawalbe, Theme.chat_goIconDrawable}, null, "chat_serviceIcon");
        themeDescriptionArr[39] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class, ChatActionCell.class}, null, null, null, "chat_serviceBackground");
        themeDescriptionArr[40] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class, ChatActionCell.class}, null, null, null, "chat_serviceBackgroundSelected");
        themeDescriptionArr[41] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_messageTextIn");
        themeDescriptionArr[42] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_messageTextOut");
        themeDescriptionArr[43] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{ChatMessageCell.class}, null, null, null, "chat_messageLinkIn", null);
        themeDescriptionArr[44] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{ChatMessageCell.class}, null, null, null, "chat_messageLinkOut", null);
        themeDescriptionArr[45] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckDrawable}, null, "chat_outSentCheck");
        themeDescriptionArr[46] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable}, null, "chat_outSentCheckSelected");
        themeDescriptionArr[47] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckReadDrawable, Theme.chat_msgOutHalfCheckDrawable}, null, "chat_outSentCheckRead");
        themeDescriptionArr[48] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckReadSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, null, "chat_outSentCheckReadSelected");
        themeDescriptionArr[49] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutClockDrawable}, null, "chat_outSentClock");
        themeDescriptionArr[50] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutSelectedClockDrawable}, null, "chat_outSentClockSelected");
        themeDescriptionArr[51] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInClockDrawable}, null, "chat_inSentClock");
        themeDescriptionArr[52] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInSelectedClockDrawable}, null, "chat_inSentClockSelected");
        themeDescriptionArr[53] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, null, "chat_mediaSentCheck");
        themeDescriptionArr[54] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgStickerHalfCheckDrawable, Theme.chat_msgStickerCheckDrawable, Theme.chat_msgStickerClockDrawable, Theme.chat_msgStickerViewsDrawable}, null, "chat_serviceText");
        themeDescriptionArr[55] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgMediaClockDrawable}, null, "chat_mediaSentClock");
        themeDescriptionArr[56] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutViewsDrawable}, null, "chat_outViews");
        themeDescriptionArr[57] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutViewsSelectedDrawable}, null, "chat_outViewsSelected");
        themeDescriptionArr[58] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInViewsDrawable}, null, "chat_inViews");
        themeDescriptionArr[59] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInViewsSelectedDrawable}, null, "chat_inViewsSelected");
        themeDescriptionArr[60] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgMediaViewsDrawable}, null, "chat_mediaViews");
        themeDescriptionArr[61] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutMenuDrawable}, null, "chat_outMenu");
        themeDescriptionArr[62] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutMenuSelectedDrawable}, null, "chat_outMenuSelected");
        themeDescriptionArr[63] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInMenuDrawable}, null, "chat_inMenu");
        themeDescriptionArr[64] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInMenuSelectedDrawable}, null, "chat_inMenuSelected");
        themeDescriptionArr[65] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgMediaMenuDrawable}, null, "chat_mediaMenu");
        themeDescriptionArr[66] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutInstantDrawable, Theme.chat_msgOutCallDrawable}, null, "chat_outInstant");
        themeDescriptionArr[67] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCallSelectedDrawable}, null, "chat_outInstantSelected");
        themeDescriptionArr[68] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInInstantDrawable, Theme.chat_msgInCallDrawable}, null, "chat_inInstant");
        themeDescriptionArr[69] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInCallSelectedDrawable}, null, "chat_inInstantSelected");
        themeDescriptionArr[70] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgCallUpGreenDrawable}, null, "chat_outUpCall");
        themeDescriptionArr[71] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgCallDownRedDrawable}, null, "chat_inUpCall");
        themeDescriptionArr[72] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgCallDownGreenDrawable}, null, "chat_inDownCall");
        themeDescriptionArr[73] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_msgErrorPaint, null, null, "chat_sentError");
        themeDescriptionArr[74] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgErrorDrawable}, null, "chat_sentErrorIcon");
        themeDescriptionArr[75] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_durationPaint, null, null, "chat_previewDurationText");
        themeDescriptionArr[76] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_gamePaint, null, null, "chat_previewGameText");
        themeDescriptionArr[77] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inPreviewInstantText");
        themeDescriptionArr[78] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outPreviewInstantText");
        themeDescriptionArr[79] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inPreviewInstantSelectedText");
        themeDescriptionArr[80] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outPreviewInstantSelectedText");
        themeDescriptionArr[81] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_deleteProgressPaint, null, null, "chat_secretTimeText");
        themeDescriptionArr[82] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_stickerNameText");
        themeDescriptionArr[83] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_botButtonPaint, null, null, "chat_botButtonText");
        themeDescriptionArr[84] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_botProgressPaint, null, null, "chat_botProgress");
        themeDescriptionArr[85] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inForwardedNameText");
        themeDescriptionArr[86] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outForwardedNameText");
        themeDescriptionArr[87] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inViaBotNameText");
        themeDescriptionArr[88] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outViaBotNameText");
        themeDescriptionArr[89] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_stickerViaBotNameText");
        themeDescriptionArr[90] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inReplyLine");
        themeDescriptionArr[91] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outReplyLine");
        themeDescriptionArr[92] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_stickerReplyLine");
        themeDescriptionArr[93] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inReplyNameText");
        themeDescriptionArr[94] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outReplyNameText");
        themeDescriptionArr[95] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_stickerReplyNameText");
        themeDescriptionArr[96] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inReplyMessageText");
        themeDescriptionArr[97] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outReplyMessageText");
        themeDescriptionArr[98] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inReplyMediaMessageText");
        themeDescriptionArr[99] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outReplyMediaMessageText");
        themeDescriptionArr[100] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inReplyMediaMessageSelectedText");
        themeDescriptionArr[101] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outReplyMediaMessageSelectedText");
        themeDescriptionArr[102] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_stickerReplyMessageText");
        themeDescriptionArr[103] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inPreviewLine");
        themeDescriptionArr[104] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outPreviewLine");
        themeDescriptionArr[105] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inSiteNameText");
        themeDescriptionArr[106] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outSiteNameText");
        themeDescriptionArr[107] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inContactNameText");
        themeDescriptionArr[108] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outContactNameText");
        themeDescriptionArr[109] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inContactPhoneText");
        themeDescriptionArr[110] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outContactPhoneText");
        themeDescriptionArr[111] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_mediaProgress");
        themeDescriptionArr[112] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inAudioProgress");
        themeDescriptionArr[113] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outAudioProgress");
        themeDescriptionArr[114] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inAudioSelectedProgress");
        themeDescriptionArr[115] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outAudioSelectedProgress");
        themeDescriptionArr[116] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_mediaTimeText");
        themeDescriptionArr[117] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inTimeText");
        themeDescriptionArr[118] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outTimeText");
        themeDescriptionArr[119] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inTimeSelectedText");
        themeDescriptionArr[120] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outTimeSelectedText");
        themeDescriptionArr[121] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inAudioPerfomerText");
        themeDescriptionArr[122] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outAudioPerfomerText");
        themeDescriptionArr[123] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inAudioTitleText");
        themeDescriptionArr[124] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outAudioTitleText");
        themeDescriptionArr[125] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inAudioDurationText");
        themeDescriptionArr[126] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outAudioDurationText");
        themeDescriptionArr[127] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inAudioDurationSelectedText");
        themeDescriptionArr[128] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outAudioDurationSelectedText");
        themeDescriptionArr[129] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inAudioSeekbar");
        themeDescriptionArr[130] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outAudioSeekbar");
        themeDescriptionArr[131] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inAudioSeekbarSelected");
        themeDescriptionArr[132] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outAudioSeekbarSelected");
        themeDescriptionArr[133] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inAudioSeekbarFill");
        themeDescriptionArr[134] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inAudioCacheSeekbar");
        themeDescriptionArr[135] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outAudioSeekbarFill");
        themeDescriptionArr[136] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outAudioCacheSeekbar");
        themeDescriptionArr[137] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inVoiceSeekbar");
        themeDescriptionArr[138] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outVoiceSeekbar");
        themeDescriptionArr[139] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inVoiceSeekbarSelected");
        themeDescriptionArr[140] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outVoiceSeekbarSelected");
        themeDescriptionArr[141] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inVoiceSeekbarFill");
        themeDescriptionArr[142] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outVoiceSeekbarFill");
        themeDescriptionArr[143] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inFileProgress");
        themeDescriptionArr[144] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outFileProgress");
        themeDescriptionArr[145] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inFileProgressSelected");
        themeDescriptionArr[146] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outFileProgressSelected");
        themeDescriptionArr[147] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inFileNameText");
        themeDescriptionArr[148] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outFileNameText");
        themeDescriptionArr[149] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inFileInfoText");
        themeDescriptionArr[150] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outFileInfoText");
        themeDescriptionArr[151] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inFileInfoSelectedText");
        themeDescriptionArr[152] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outFileInfoSelectedText");
        themeDescriptionArr[153] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inFileBackground");
        themeDescriptionArr[154] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outFileBackground");
        themeDescriptionArr[155] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inFileBackgroundSelected");
        themeDescriptionArr[156] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outFileBackgroundSelected");
        themeDescriptionArr[157] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inVenueInfoText");
        themeDescriptionArr[158] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outVenueInfoText");
        themeDescriptionArr[159] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inVenueInfoSelectedText");
        themeDescriptionArr[160] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outVenueInfoSelectedText");
        themeDescriptionArr[161] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_mediaInfoText");
        themeDescriptionArr[162] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_urlPaint, null, null, "chat_linkSelectBackground");
        themeDescriptionArr[163] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_textSearchSelectionPaint, null, null, "chat_textSelectBackground");
        themeDescriptionArr[164] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outLoader");
        themeDescriptionArr[165] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outMediaIcon");
        themeDescriptionArr[166] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outLoaderSelected");
        themeDescriptionArr[167] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outMediaIconSelected");
        themeDescriptionArr[168] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inLoader");
        themeDescriptionArr[169] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inMediaIcon");
        themeDescriptionArr[170] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inLoaderSelected");
        themeDescriptionArr[171] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inMediaIconSelected");
        RecyclerListView recyclerListView = this.chatListView;
        int i = ThemeDescription.FLAG_BACKGROUNDFILTER;
        Class[] clsArr = new Class[]{ChatMessageCell.class};
        r14 = new Drawable[4];
        Drawable[][] drawableArr = Theme.chat_photoStatesDrawables;
        r14[0] = drawableArr[0][0];
        r14[1] = drawableArr[1][0];
        r14[2] = drawableArr[2][0];
        r14[3] = drawableArr[3][0];
        themeDescriptionArr[172] = new ThemeDescription(recyclerListView, i, clsArr, null, r14, null, "chat_mediaLoaderPhoto");
        recyclerListView = this.chatListView;
        clsArr = new Class[]{ChatMessageCell.class};
        r14 = new Drawable[4];
        Drawable[][] drawableArr2 = Theme.chat_photoStatesDrawables;
        r14[0] = drawableArr2[0][0];
        r14[1] = drawableArr2[1][0];
        r14[2] = drawableArr2[2][0];
        r14[3] = drawableArr2[3][0];
        themeDescriptionArr[173] = new ThemeDescription(recyclerListView, 0, clsArr, null, r14, null, "chat_mediaLoaderPhotoIcon");
        recyclerListView = this.chatListView;
        i = ThemeDescription.FLAG_BACKGROUNDFILTER;
        clsArr = new Class[]{ChatMessageCell.class};
        r14 = new Drawable[4];
        drawableArr = Theme.chat_photoStatesDrawables;
        r14[0] = drawableArr[0][1];
        r14[1] = drawableArr[1][1];
        r14[2] = drawableArr[2][1];
        r14[3] = drawableArr[3][1];
        themeDescriptionArr[174] = new ThemeDescription(recyclerListView, i, clsArr, null, r14, null, "chat_mediaLoaderPhotoSelected");
        recyclerListView = this.chatListView;
        clsArr = new Class[]{ChatMessageCell.class};
        r7 = new Drawable[4];
        Drawable[][] drawableArr3 = Theme.chat_photoStatesDrawables;
        r7[0] = drawableArr3[0][1];
        r7[1] = drawableArr3[1][1];
        r7[2] = drawableArr3[2][1];
        r7[3] = drawableArr3[3][1];
        themeDescriptionArr[175] = new ThemeDescription(recyclerListView, 0, clsArr, null, r7, null, "chat_mediaLoaderPhotoIconSelected");
        RecyclerListView recyclerListView2 = this.chatListView;
        int i2 = ThemeDescription.FLAG_BACKGROUNDFILTER;
        Class[] clsArr2 = new Class[]{ChatMessageCell.class};
        r7 = new Drawable[2];
        Drawable[][] drawableArr4 = Theme.chat_photoStatesDrawables;
        r7[0] = drawableArr4[7][0];
        r7[1] = drawableArr4[8][0];
        themeDescriptionArr[176] = new ThemeDescription(recyclerListView2, i2, clsArr2, null, r7, null, "chat_outLoaderPhoto");
        recyclerListView2 = this.chatListView;
        clsArr2 = new Class[]{ChatMessageCell.class};
        r7 = new Drawable[2];
        drawableArr4 = Theme.chat_photoStatesDrawables;
        r7[0] = drawableArr4[7][0];
        r7[1] = drawableArr4[8][0];
        themeDescriptionArr[177] = new ThemeDescription(recyclerListView2, 0, clsArr2, null, r7, null, "chat_outLoaderPhotoIcon");
        recyclerListView2 = this.chatListView;
        i2 = ThemeDescription.FLAG_BACKGROUNDFILTER;
        clsArr2 = new Class[]{ChatMessageCell.class};
        r7 = new Drawable[2];
        drawableArr4 = Theme.chat_photoStatesDrawables;
        r7[0] = drawableArr4[7][1];
        r7[1] = drawableArr4[8][1];
        themeDescriptionArr[178] = new ThemeDescription(recyclerListView2, i2, clsArr2, null, r7, null, "chat_outLoaderPhotoSelected");
        recyclerListView2 = this.chatListView;
        clsArr2 = new Class[]{ChatMessageCell.class};
        r7 = new Drawable[2];
        drawableArr4 = Theme.chat_photoStatesDrawables;
        r7[0] = drawableArr4[7][1];
        r7[1] = drawableArr4[8][1];
        themeDescriptionArr[179] = new ThemeDescription(recyclerListView2, 0, clsArr2, null, r7, null, "chat_outLoaderPhotoIconSelected");
        recyclerListView2 = this.chatListView;
        i2 = ThemeDescription.FLAG_BACKGROUNDFILTER;
        clsArr2 = new Class[]{ChatMessageCell.class};
        r7 = new Drawable[2];
        Drawable[][] drawableArr5 = Theme.chat_photoStatesDrawables;
        r7[0] = drawableArr5[10][0];
        r7[1] = drawableArr5[11][0];
        themeDescriptionArr[180] = new ThemeDescription(recyclerListView2, i2, clsArr2, null, r7, null, "chat_inLoaderPhoto");
        recyclerListView2 = this.chatListView;
        clsArr2 = new Class[]{ChatMessageCell.class};
        r7 = new Drawable[2];
        drawableArr5 = Theme.chat_photoStatesDrawables;
        r7[0] = drawableArr5[10][0];
        r7[1] = drawableArr5[11][0];
        themeDescriptionArr[181] = new ThemeDescription(recyclerListView2, 0, clsArr2, null, r7, null, "chat_inLoaderPhotoIcon");
        recyclerListView2 = this.chatListView;
        i2 = ThemeDescription.FLAG_BACKGROUNDFILTER;
        clsArr2 = new Class[]{ChatMessageCell.class};
        r7 = new Drawable[2];
        drawableArr5 = Theme.chat_photoStatesDrawables;
        r7[0] = drawableArr5[10][1];
        r7[1] = drawableArr5[11][1];
        themeDescriptionArr[182] = new ThemeDescription(recyclerListView2, i2, clsArr2, null, r7, null, "chat_inLoaderPhotoSelected");
        recyclerListView2 = this.chatListView;
        clsArr2 = new Class[]{ChatMessageCell.class};
        r5 = new Drawable[2];
        Drawable[][] drawableArr6 = Theme.chat_photoStatesDrawables;
        r5[0] = drawableArr6[10][1];
        r5[1] = drawableArr6[11][1];
        themeDescriptionArr[183] = new ThemeDescription(recyclerListView2, 0, clsArr2, null, r5, null, "chat_inLoaderPhotoIconSelected");
        themeDescriptionArr[184] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[9][0]}, null, "chat_outFileIcon");
        themeDescriptionArr[185] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[9][1]}, null, "chat_outFileSelectedIcon");
        ThemeDescriptionDelegate themeDescriptionDelegate = null;
        themeDescriptionArr[186] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[12][0]}, null, "chat_inFileIcon");
        themeDescriptionArr[187] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[12][1]}, null, "chat_inFileSelectedIcon");
        themeDescriptionArr[188] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_contactDrawable[0]}, null, "chat_inContactBackground");
        themeDescriptionArr[189] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_contactDrawable[0]}, null, "chat_inContactIcon");
        themeDescriptionArr[190] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_contactDrawable[1]}, null, "chat_outContactBackground");
        themeDescriptionArr[191] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_contactDrawable[1]}, null, "chat_outContactIcon");
        themeDescriptionArr[192] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inLocationBackground");
        themeDescriptionArr[193] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_locationDrawable[0]}, null, "chat_inLocationIcon");
        themeDescriptionArr[194] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outLocationBackground");
        themeDescriptionArr[195] = new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_locationDrawable[1]}, null, "chat_outLocationIcon");
        themeDescriptionArr[196] = new ThemeDescription(this.bottomOverlayChat, 0, null, Theme.chat_composeBackgroundPaint, null, null, "chat_messagePanelBackground");
        themeDescriptionArr[197] = new ThemeDescription(this.bottomOverlayChat, 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, "chat_messagePanelShadow");
        themeDescriptionArr[198] = new ThemeDescription(this.bottomOverlayChatText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "chat_fieldOverlayText");
        themeDescriptionArr[199] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "chat_serviceText");
        themeDescriptionArr[200] = new ThemeDescription(this.progressBar, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "chat_serviceText");
        themeDescriptionArr[201] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{ChatUnreadCell.class}, new String[]{"backgroundLayout"}, null, null, null, "chat_unreadMessagesStartBackground");
        themeDescriptionArr[202] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{ChatUnreadCell.class}, new String[]{"imageView"}, null, null, null, "chat_unreadMessagesStartArrowIcon");
        View view = this.chatListView;
        int i3 = ThemeDescription.FLAG_TEXTCOLOR;
        Class[] clsArr3 = new Class[]{ChatUnreadCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        themeDescriptionArr[203] = new ThemeDescription(view, i3, clsArr3, strArr, null, null, null, "chat_unreadMessagesStartText");
        themeDescriptionArr[204] = new ThemeDescription(this.progressView2, ThemeDescription.FLAG_SERVICEBACKGROUND, null, null, null, null, "chat_serviceBackground");
        themeDescriptionArr[205] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_SERVICEBACKGROUND, null, null, null, null, "chat_serviceBackground");
        themeDescriptionArr[206] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_SERVICEBACKGROUND, new Class[]{ChatLoadingCell.class}, new String[]{"textView"}, null, null, null, "chat_serviceBackground");
        themeDescriptionArr[207] = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{ChatLoadingCell.class}, new String[]{"textView"}, null, null, null, "chat_serviceText");
        ChatAvatarContainer chatAvatarContainer = this.avatarContainer;
        themeDescriptionArr[208] = new ThemeDescription(chatAvatarContainer != null ? chatAvatarContainer.getTimeItem() : null, 0, null, null, null, null, "chat_secretTimerBackground");
        chatAvatarContainer = this.avatarContainer;
        if (chatAvatarContainer != null) {
            themeDescriptionDelegate = chatAvatarContainer.getTimeItem();
        }
        themeDescriptionArr[209] = new ThemeDescription(themeDescriptionDelegate, 0, null, null, null, null, "chat_secretTimerText");
        return themeDescriptionArr;
    }
}
