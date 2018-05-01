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
import org.telegram.messenger.C0446R;
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
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.exoplayer2.extractor.ts.PsExtractor;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;
import org.telegram.messenger.exoplayer2.ui.AspectRatioFrameLayout;
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
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
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
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }

        C09556() {
        }
    }

    /* renamed from: org.telegram.ui.ChannelAdminLogActivity$2 */
    class C19642 implements RequestDelegate {
        C19642() {
        }

        public void run(TLObject tLObject, TL_error tL_error) {
            if (tLObject != null) {
                final TL_channels_adminLogResults tL_channels_adminLogResults = (TL_channels_adminLogResults) tLObject;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        int i = 0;
                        MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).putUsers(tL_channels_adminLogResults.users, false);
                        MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).putChats(tL_channels_adminLogResults.chats, false);
                        int size = ChannelAdminLogActivity.this.messages.size();
                        int i2 = 0;
                        int i3 = i2;
                        while (i2 < tL_channels_adminLogResults.events.size()) {
                            TL_channelAdminLogEvent tL_channelAdminLogEvent = (TL_channelAdminLogEvent) tL_channels_adminLogResults.events.get(i2);
                            if (ChannelAdminLogActivity.this.messagesDict.indexOfKey(tL_channelAdminLogEvent.id) < 0) {
                                ChannelAdminLogActivity.this.minEventId = Math.min(ChannelAdminLogActivity.this.minEventId, tL_channelAdminLogEvent.id);
                                MessageObject messageObject = new MessageObject(ChannelAdminLogActivity.this.currentAccount, tL_channelAdminLogEvent, ChannelAdminLogActivity.this.messages, ChannelAdminLogActivity.this.messagesByDays, ChannelAdminLogActivity.this.currentChat, ChannelAdminLogActivity.this.mid);
                                if (messageObject.contentType >= 0) {
                                    ChannelAdminLogActivity.this.messagesDict.put(tL_channelAdminLogEvent.id, messageObject);
                                }
                                i3 = true;
                            }
                            i2++;
                        }
                        i2 = ChannelAdminLogActivity.this.messages.size() - size;
                        ChannelAdminLogActivity.this.loading = false;
                        if (i3 == 0) {
                            ChannelAdminLogActivity.this.endReached = true;
                        }
                        ChannelAdminLogActivity.this.progressView.setVisibility(4);
                        ChannelAdminLogActivity.this.chatListView.setEmptyView(ChannelAdminLogActivity.this.emptyViewContainer);
                        if (i2 != 0) {
                            if (ChannelAdminLogActivity.this.endReached) {
                                ChannelAdminLogActivity.this.chatAdapter.notifyItemRangeChanged(0, 2);
                                size = 1;
                            } else {
                                size = 0;
                            }
                            i3 = ChannelAdminLogActivity.this.chatLayoutManager.findLastVisibleItemPosition();
                            View findViewByPosition = ChannelAdminLogActivity.this.chatLayoutManager.findViewByPosition(i3);
                            if (findViewByPosition != null) {
                                i = findViewByPosition.getTop();
                            }
                            i -= ChannelAdminLogActivity.this.chatListView.getPaddingTop();
                            if (i2 - size > 0) {
                                int i4 = 1 + (size ^ 1);
                                ChannelAdminLogActivity.this.chatAdapter.notifyItemChanged(i4);
                                ChannelAdminLogActivity.this.chatAdapter.notifyItemRangeInserted(i4, i2 - size);
                            }
                            if (i3 != -1) {
                                ChannelAdminLogActivity.this.chatLayoutManager.scrollToPositionWithOffset((i3 + i2) - size, i);
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

        public void onItemClick(int i) {
            if (i == -1) {
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

        public void onItemClick(View view, int i) {
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
            public boolean canPerformActions() {
                return true;
            }

            public void didPressedBotButton(ChatMessageCell chatMessageCell, KeyboardButton keyboardButton) {
            }

            public void didPressedCancelSendButton(ChatMessageCell chatMessageCell) {
            }

            public void didPressedReplyMessage(ChatMessageCell chatMessageCell, int i) {
            }

            public void didPressedViaBot(ChatMessageCell chatMessageCell, String str) {
            }

            public boolean isChatAdminCell(int i) {
                return false;
            }

            C19691() {
            }

            public void didPressedShare(ChatMessageCell chatMessageCell) {
                if (ChannelAdminLogActivity.this.getParentActivity() != null) {
                    ChannelAdminLogActivity channelAdminLogActivity = ChannelAdminLogActivity.this;
                    Context access$6100 = ChatActivityAdapter.this.mContext;
                    MessageObject messageObject = chatMessageCell.getMessageObject();
                    chatMessageCell = (ChatObject.isChannel(ChannelAdminLogActivity.this.currentChat) == null || ChannelAdminLogActivity.this.currentChat.megagroup != null || ChannelAdminLogActivity.this.currentChat.username == null || ChannelAdminLogActivity.this.currentChat.username.length() <= null) ? null : true;
                    channelAdminLogActivity.showDialog(ShareAlert.createShareAlert(access$6100, messageObject, null, chatMessageCell, null, false));
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
                messageObject = MediaController.getInstance().playMessage(messageObject);
                MediaController.getInstance().setVoiceMessagesPlaylist(null, false);
                return messageObject;
            }

            public void didPressedChannelAvatar(ChatMessageCell chatMessageCell, Chat chat, int i) {
                if (chat != null && chat != ChannelAdminLogActivity.this.currentChat) {
                    chatMessageCell = new Bundle();
                    chatMessageCell.putInt("chat_id", chat.id);
                    if (i != 0) {
                        chatMessageCell.putInt("message_id", i);
                    }
                    if (MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).checkCanOpenChat(chatMessageCell, ChannelAdminLogActivity.this) != null) {
                        ChannelAdminLogActivity.this.presentFragment(new ChatActivity(chatMessageCell), true);
                    }
                }
            }

            public void didPressedOther(ChatMessageCell chatMessageCell) {
                ChannelAdminLogActivity.this.createMenu(chatMessageCell);
            }

            public void didPressedUserAvatar(ChatMessageCell chatMessageCell, User user) {
                if (user != null && user.id != UserConfig.getInstance(ChannelAdminLogActivity.this.currentAccount).getClientUserId()) {
                    chatMessageCell = new Bundle();
                    chatMessageCell.putInt("user_id", user.id);
                    ChannelAdminLogActivity.this.addCanBanUser(chatMessageCell, user.id);
                    user = new ProfileActivity(chatMessageCell);
                    user.setPlayProfileAnimation(null);
                    ChannelAdminLogActivity.this.presentFragment(user);
                }
            }

            public void didLongPressed(ChatMessageCell chatMessageCell) {
                ChannelAdminLogActivity.this.createMenu(chatMessageCell);
            }

            public void didPressedUrl(MessageObject messageObject, CharacterStyle characterStyle, boolean z) {
                if (characterStyle != null) {
                    if (characterStyle instanceof URLSpanMono) {
                        ((URLSpanMono) characterStyle).copyToClipboard();
                        Toast.makeText(ChannelAdminLogActivity.this.getParentActivity(), LocaleController.getString("TextCopied", true), 0).show();
                    } else if (characterStyle instanceof URLSpanUserMention) {
                        messageObject = MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).getUser(Utilities.parseInt(((URLSpanUserMention) characterStyle).getURL()));
                        if (messageObject != null) {
                            MessagesController.openChatOrProfileWith(messageObject, null, ChannelAdminLogActivity.this, 0, false);
                        }
                    } else if (characterStyle instanceof URLSpanNoUnderline) {
                        messageObject = ((URLSpanNoUnderline) characterStyle).getURL();
                        if (messageObject.startsWith("@") != null) {
                            MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).openByUserName(messageObject.substring(1), ChannelAdminLogActivity.this, 0);
                        } else if (messageObject.startsWith("#") != null) {
                            characterStyle = new DialogsActivity(null);
                            characterStyle.setSearchString(messageObject);
                            ChannelAdminLogActivity.this.presentFragment(characterStyle);
                        }
                    } else {
                        final String url = ((URLSpan) characterStyle).getURL();
                        if (z) {
                            messageObject = new Builder(ChannelAdminLogActivity.this.getParentActivity());
                            messageObject.setTitle(url);
                            messageObject.setItems(new CharSequence[]{LocaleController.getString("Open", C0446R.string.Open), LocaleController.getString("Copy", C0446R.string.Copy)}, new OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (i == 0) {
                                        Browser.openUrl(ChannelAdminLogActivity.this.getParentActivity(), url, true);
                                    } else if (i == 1) {
                                        dialogInterface = url;
                                        if (dialogInterface.startsWith("mailto:") != 0) {
                                            dialogInterface = dialogInterface.substring(7);
                                        } else if (dialogInterface.startsWith("tel:") != 0) {
                                            dialogInterface = dialogInterface.substring(4);
                                        }
                                        AndroidUtilities.addToClipboard(dialogInterface);
                                    }
                                }
                            });
                            ChannelAdminLogActivity.this.showDialog(messageObject.create());
                        } else if (characterStyle instanceof URLSpanReplacement) {
                            ChannelAdminLogActivity.this.showOpenUrlAlert(((URLSpanReplacement) characterStyle).getURL(), true);
                        } else if (characterStyle instanceof URLSpan) {
                            if (!((messageObject.messageOwner.media instanceof TL_messageMediaWebPage) == null || messageObject.messageOwner.media.webpage == null || messageObject.messageOwner.media.webpage.cached_page == null)) {
                                characterStyle = url.toLowerCase();
                                z = messageObject.messageOwner.media.webpage.url.toLowerCase();
                                if ((characterStyle.contains("telegra.ph") || characterStyle.contains("t.me/iv")) && (characterStyle.contains(z) || z.contains(characterStyle) != null)) {
                                    ArticleViewer.getInstance().setParentActivity(ChannelAdminLogActivity.this.getParentActivity(), ChannelAdminLogActivity.this);
                                    ArticleViewer.getInstance().open(messageObject);
                                    return;
                                }
                            }
                            Browser.openUrl(ChannelAdminLogActivity.this.getParentActivity(), url, true);
                        } else if ((characterStyle instanceof ClickableSpan) != null) {
                            ((ClickableSpan) characterStyle).onClick(ChannelAdminLogActivity.this.fragmentView);
                        }
                    }
                }
            }

            public void needOpenWebView(String str, String str2, String str3, String str4, int i, int i2) {
                EmbedBottomSheet.show(ChatActivityAdapter.this.mContext, str2, str3, str4, str, i, i2);
            }

            public void didPressedImage(org.telegram.ui.Cells.ChatMessageCell r9) {
                /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
                /*
                r8 = this;
                r1 = r9.getMessageObject();
                r9 = r1.type;
                r0 = 13;
                if (r9 != r0) goto L_0x002b;
            L_0x000a:
                r9 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;
                r9 = org.telegram.ui.ChannelAdminLogActivity.this;
                r0 = new org.telegram.ui.Components.StickersAlert;
                r2 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;
                r2 = org.telegram.ui.ChannelAdminLogActivity.this;
                r3 = r2.getParentActivity();
                r2 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;
                r4 = org.telegram.ui.ChannelAdminLogActivity.this;
                r5 = r1.getInputStickerSet();
                r6 = 0;
                r7 = 0;
                r2 = r0;
                r2.<init>(r3, r4, r5, r6, r7);
                r9.showDialog(r0);
                goto L_0x01e8;
            L_0x002b:
                r9 = r1.isVideo();
                if (r9 != 0) goto L_0x01c6;
            L_0x0031:
                r9 = r1.type;
                r0 = 1;
                if (r9 == r0) goto L_0x01c6;
            L_0x0036:
                r9 = r1.type;
                if (r9 != 0) goto L_0x0040;
            L_0x003a:
                r9 = r1.isWebpageDocument();
                if (r9 == 0) goto L_0x01c6;
            L_0x0040:
                r9 = r1.isGif();
                if (r9 == 0) goto L_0x0048;
            L_0x0046:
                goto L_0x01c6;
            L_0x0048:
                r9 = r1.type;
                r2 = 3;
                r3 = 0;
                if (r9 != r2) goto L_0x00ba;
            L_0x004e:
                r9 = r1.messageOwner;	 Catch:{ Exception -> 0x00b1 }
                r9 = r9.attachPath;	 Catch:{ Exception -> 0x00b1 }
                if (r9 == 0) goto L_0x0067;	 Catch:{ Exception -> 0x00b1 }
            L_0x0054:
                r9 = r1.messageOwner;	 Catch:{ Exception -> 0x00b1 }
                r9 = r9.attachPath;	 Catch:{ Exception -> 0x00b1 }
                r9 = r9.length();	 Catch:{ Exception -> 0x00b1 }
                if (r9 == 0) goto L_0x0067;	 Catch:{ Exception -> 0x00b1 }
            L_0x005e:
                r3 = new java.io.File;	 Catch:{ Exception -> 0x00b1 }
                r9 = r1.messageOwner;	 Catch:{ Exception -> 0x00b1 }
                r9 = r9.attachPath;	 Catch:{ Exception -> 0x00b1 }
                r3.<init>(r9);	 Catch:{ Exception -> 0x00b1 }
            L_0x0067:
                if (r3 == 0) goto L_0x006f;	 Catch:{ Exception -> 0x00b1 }
            L_0x0069:
                r9 = r3.exists();	 Catch:{ Exception -> 0x00b1 }
                if (r9 != 0) goto L_0x0075;	 Catch:{ Exception -> 0x00b1 }
            L_0x006f:
                r9 = r1.messageOwner;	 Catch:{ Exception -> 0x00b1 }
                r3 = org.telegram.messenger.FileLoader.getPathToMessage(r9);	 Catch:{ Exception -> 0x00b1 }
            L_0x0075:
                r9 = new android.content.Intent;	 Catch:{ Exception -> 0x00b1 }
                r2 = "android.intent.action.VIEW";	 Catch:{ Exception -> 0x00b1 }
                r9.<init>(r2);	 Catch:{ Exception -> 0x00b1 }
                r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x00b1 }
                r4 = 24;	 Catch:{ Exception -> 0x00b1 }
                if (r2 < r4) goto L_0x0099;	 Catch:{ Exception -> 0x00b1 }
            L_0x0082:
                r9.setFlags(r0);	 Catch:{ Exception -> 0x00b1 }
                r0 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;	 Catch:{ Exception -> 0x00b1 }
                r0 = org.telegram.ui.ChannelAdminLogActivity.this;	 Catch:{ Exception -> 0x00b1 }
                r0 = r0.getParentActivity();	 Catch:{ Exception -> 0x00b1 }
                r2 = "org.telegram.messenger.provider";	 Catch:{ Exception -> 0x00b1 }
                r0 = android.support.v4.content.FileProvider.getUriForFile(r0, r2, r3);	 Catch:{ Exception -> 0x00b1 }
                r2 = "video/mp4";	 Catch:{ Exception -> 0x00b1 }
                r9.setDataAndType(r0, r2);	 Catch:{ Exception -> 0x00b1 }
                goto L_0x00a2;	 Catch:{ Exception -> 0x00b1 }
            L_0x0099:
                r0 = android.net.Uri.fromFile(r3);	 Catch:{ Exception -> 0x00b1 }
                r2 = "video/mp4";	 Catch:{ Exception -> 0x00b1 }
                r9.setDataAndType(r0, r2);	 Catch:{ Exception -> 0x00b1 }
            L_0x00a2:
                r0 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;	 Catch:{ Exception -> 0x00b1 }
                r0 = org.telegram.ui.ChannelAdminLogActivity.this;	 Catch:{ Exception -> 0x00b1 }
                r0 = r0.getParentActivity();	 Catch:{ Exception -> 0x00b1 }
                r2 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;	 Catch:{ Exception -> 0x00b1 }
                r0.startActivityForResult(r9, r2);	 Catch:{ Exception -> 0x00b1 }
                goto L_0x01e8;
            L_0x00b1:
                r9 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;
                r9 = org.telegram.ui.ChannelAdminLogActivity.this;
                r9.alertUserOpenError(r1);
                goto L_0x01e8;
            L_0x00ba:
                r9 = r1.type;
                r2 = 4;
                if (r9 != r2) goto L_0x00dc;
            L_0x00bf:
                r9 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;
                r9 = org.telegram.ui.ChannelAdminLogActivity.this;
                r9 = org.telegram.messenger.AndroidUtilities.isGoogleMapsInstalled(r9);
                if (r9 != 0) goto L_0x00ca;
            L_0x00c9:
                return;
            L_0x00ca:
                r9 = new org.telegram.ui.LocationActivity;
                r0 = 0;
                r9.<init>(r0);
                r9.setMessageObject(r1);
                r0 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;
                r0 = org.telegram.ui.ChannelAdminLogActivity.this;
                r0.presentFragment(r9);
                goto L_0x01e8;
            L_0x00dc:
                r9 = r1.type;
                r2 = 9;
                if (r9 == r2) goto L_0x00e6;
            L_0x00e2:
                r9 = r1.type;
                if (r9 != 0) goto L_0x01e8;
            L_0x00e6:
                r9 = r1.getDocumentName();
                r9 = r9.toLowerCase();
                r2 = "attheme";
                r9 = r9.endsWith(r2);
                if (r9 == 0) goto L_0x01b2;
            L_0x00f6:
                r9 = r1.messageOwner;
                r9 = r9.attachPath;
                if (r9 == 0) goto L_0x0116;
            L_0x00fc:
                r9 = r1.messageOwner;
                r9 = r9.attachPath;
                r9 = r9.length();
                if (r9 == 0) goto L_0x0116;
            L_0x0106:
                r9 = new java.io.File;
                r2 = r1.messageOwner;
                r2 = r2.attachPath;
                r9.<init>(r2);
                r2 = r9.exists();
                if (r2 == 0) goto L_0x0116;
            L_0x0115:
                goto L_0x0117;
            L_0x0116:
                r9 = r3;
            L_0x0117:
                if (r9 != 0) goto L_0x0126;
            L_0x0119:
                r2 = r1.messageOwner;
                r2 = org.telegram.messenger.FileLoader.getPathToMessage(r2);
                r3 = r2.exists();
                if (r3 == 0) goto L_0x0126;
            L_0x0125:
                r9 = r2;
            L_0x0126:
                r2 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;
                r2 = org.telegram.ui.ChannelAdminLogActivity.this;
                r2 = r2.chatLayoutManager;
                r3 = -1;
                if (r2 == 0) goto L_0x0194;
            L_0x0131:
                r2 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;
                r2 = org.telegram.ui.ChannelAdminLogActivity.this;
                r2 = r2.chatLayoutManager;
                r2 = r2.findLastVisibleItemPosition();
                r4 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;
                r4 = org.telegram.ui.ChannelAdminLogActivity.this;
                r4 = r4.chatLayoutManager;
                r4 = r4.getItemCount();
                r4 = r4 - r0;
                if (r2 >= r4) goto L_0x018d;
            L_0x014c:
                r2 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;
                r2 = org.telegram.ui.ChannelAdminLogActivity.this;
                r4 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;
                r4 = org.telegram.ui.ChannelAdminLogActivity.this;
                r4 = r4.chatLayoutManager;
                r4 = r4.findFirstVisibleItemPosition();
                r2.scrollToPositionOnRecreate = r4;
                r2 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;
                r2 = org.telegram.ui.ChannelAdminLogActivity.this;
                r2 = r2.chatListView;
                r4 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;
                r4 = org.telegram.ui.ChannelAdminLogActivity.this;
                r4 = r4.scrollToPositionOnRecreate;
                r2 = r2.findViewHolderForAdapterPosition(r4);
                r2 = (org.telegram.ui.Components.RecyclerListView.Holder) r2;
                if (r2 == 0) goto L_0x0185;
            L_0x0177:
                r4 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;
                r4 = org.telegram.ui.ChannelAdminLogActivity.this;
                r2 = r2.itemView;
                r2 = r2.getTop();
                r4.scrollToOffsetOnRecreate = r2;
                goto L_0x0194;
            L_0x0185:
                r2 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;
                r2 = org.telegram.ui.ChannelAdminLogActivity.this;
                r2.scrollToPositionOnRecreate = r3;
                goto L_0x0194;
            L_0x018d:
                r2 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;
                r2 = org.telegram.ui.ChannelAdminLogActivity.this;
                r2.scrollToPositionOnRecreate = r3;
            L_0x0194:
                r2 = r1.getDocumentName();
                r0 = org.telegram.ui.ActionBar.Theme.applyThemeFile(r9, r2, r0);
                if (r0 == 0) goto L_0x01ab;
            L_0x019e:
                r1 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;
                r1 = org.telegram.ui.ChannelAdminLogActivity.this;
                r2 = new org.telegram.ui.ThemePreviewActivity;
                r2.<init>(r9, r0);
                r1.presentFragment(r2);
                return;
            L_0x01ab:
                r9 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;
                r9 = org.telegram.ui.ChannelAdminLogActivity.this;
                r9.scrollToPositionOnRecreate = r3;
            L_0x01b2:
                r9 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;	 Catch:{ Exception -> 0x01be }
                r9 = org.telegram.ui.ChannelAdminLogActivity.this;	 Catch:{ Exception -> 0x01be }
                r9 = r9.getParentActivity();	 Catch:{ Exception -> 0x01be }
                org.telegram.messenger.AndroidUtilities.openForView(r1, r9);	 Catch:{ Exception -> 0x01be }
                goto L_0x01e8;
            L_0x01be:
                r9 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;
                r9 = org.telegram.ui.ChannelAdminLogActivity.this;
                r9.alertUserOpenError(r1);
                goto L_0x01e8;
            L_0x01c6:
                r9 = org.telegram.ui.PhotoViewer.getInstance();
                r0 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;
                r0 = org.telegram.ui.ChannelAdminLogActivity.this;
                r0 = r0.getParentActivity();
                r9.setParentActivity(r0);
                r0 = org.telegram.ui.PhotoViewer.getInstance();
                r2 = 0;
                r4 = 0;
                r9 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this;
                r9 = org.telegram.ui.ChannelAdminLogActivity.this;
                r6 = r9.provider;
                r0.openPhoto(r1, r2, r4, r6);
            L_0x01e8:
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.1.didPressedImage(org.telegram.ui.Cells.ChatMessageCell):void");
            }

            public void didPressedInstantButton(ChatMessageCell chatMessageCell, int i) {
                chatMessageCell = chatMessageCell.getMessageObject();
                if (i != 0) {
                    Browser.openUrl(ChannelAdminLogActivity.this.getParentActivity(), chatMessageCell.messageOwner.media.webpage.url);
                } else if (chatMessageCell.messageOwner.media != 0 && chatMessageCell.messageOwner.media.webpage != 0 && chatMessageCell.messageOwner.media.webpage.cached_page != 0) {
                    ArticleViewer.getInstance().setParentActivity(ChannelAdminLogActivity.this.getParentActivity(), ChannelAdminLogActivity.this);
                    ArticleViewer.getInstance().open(chatMessageCell);
                }
            }
        }

        /* renamed from: org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter$2 */
        class C19702 implements ChatActionCellDelegate {
            public void didPressedBotButton(MessageObject messageObject, KeyboardButton keyboardButton) {
            }

            public void didPressedReplyMessage(ChatActionCell chatActionCell, int i) {
            }

            C19702() {
            }

            public void didClickedImage(ChatActionCell chatActionCell) {
                MessageObject messageObject = chatActionCell.getMessageObject();
                PhotoViewer.getInstance().setParentActivity(ChannelAdminLogActivity.this.getParentActivity());
                chatActionCell = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 640);
                if (chatActionCell != null) {
                    PhotoViewer.getInstance().openPhoto(chatActionCell.location, ChannelAdminLogActivity.this.provider);
                } else {
                    PhotoViewer.getInstance().openPhoto(messageObject, 0, 0, ChannelAdminLogActivity.this.provider);
                }
            }

            public void didLongPressed(ChatActionCell chatActionCell) {
                ChannelAdminLogActivity.this.createMenu(chatActionCell);
            }

            public void needOpenUserProfile(int i) {
                Bundle bundle;
                if (i < 0) {
                    bundle = new Bundle();
                    bundle.putInt("chat_id", -i);
                    if (MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).checkCanOpenChat(bundle, ChannelAdminLogActivity.this) != 0) {
                        ChannelAdminLogActivity.this.presentFragment(new ChatActivity(bundle), true);
                    }
                } else if (i != UserConfig.getInstance(ChannelAdminLogActivity.this.currentAccount).getClientUserId()) {
                    bundle = new Bundle();
                    bundle.putInt("user_id", i);
                    ChannelAdminLogActivity.this.addCanBanUser(bundle, i);
                    i = new ProfileActivity(bundle);
                    i.setPlayProfileAnimation(false);
                    ChannelAdminLogActivity.this.presentFragment(i);
                }
            }
        }

        /* renamed from: org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter$3 */
        class C19713 implements BotHelpCellDelegate {
            C19713() {
            }

            public void didPressUrl(String str) {
                if (str.startsWith("@")) {
                    MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).openByUserName(str.substring(1), ChannelAdminLogActivity.this, 0);
                } else if (str.startsWith("#")) {
                    BaseFragment dialogsActivity = new DialogsActivity(null);
                    dialogsActivity.setSearchString(str);
                    ChannelAdminLogActivity.this.presentFragment(dialogsActivity);
                }
            }
        }

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

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i == 0) {
                if (ChannelAdminLogActivity.this.chatMessageCellsCache.isEmpty() == 0) {
                    i = (View) ChannelAdminLogActivity.this.chatMessageCellsCache.get(0);
                    ChannelAdminLogActivity.this.chatMessageCellsCache.remove(0);
                } else {
                    i = new ChatMessageCell(this.mContext);
                }
                ChatMessageCell chatMessageCell = (ChatMessageCell) i;
                chatMessageCell.setDelegate(new C19691());
                chatMessageCell.setAllowAssistant(true);
            } else if (i == 1) {
                i = new ChatActionCell(this.mContext);
                ((ChatActionCell) i).setDelegate(new C19702());
            } else if (i == 2) {
                i = new ChatUnreadCell(this.mContext);
            } else if (i == 3) {
                i = new BotHelpCell(this.mContext);
                ((BotHelpCell) i).setDelegate(new C19713());
            } else {
                i = i == 4 ? new ChatLoadingCell(this.mContext) : 0;
            }
            i.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(i);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            boolean z = false;
            boolean z2 = true;
            if (i == this.loadingUpRow) {
                ChatLoadingCell chatLoadingCell = (ChatLoadingCell) viewHolder.itemView;
                if (ChannelAdminLogActivity.this.loadsCount > 1) {
                    z = true;
                }
                chatLoadingCell.setProgressVisible(z);
            } else if (i >= this.messagesStartRow && i < this.messagesEndRow) {
                MessageObject messageObject = (MessageObject) ChannelAdminLogActivity.this.messages.get((ChannelAdminLogActivity.this.messages.size() - (i - this.messagesStartRow)) - 1);
                View view = viewHolder.itemView;
                boolean z3 = view instanceof ChatMessageCell;
                if (z3) {
                    boolean z4;
                    MessageObject messageObject2;
                    ChatMessageCell chatMessageCell = (ChatMessageCell) view;
                    chatMessageCell.isChat = true;
                    int i2 = i + 1;
                    int itemViewType = getItemViewType(i2);
                    int itemViewType2 = getItemViewType(i - 1);
                    if (!(messageObject.messageOwner.reply_markup instanceof TL_replyInlineMarkup) && itemViewType == viewHolder.getItemViewType()) {
                        MessageObject messageObject3 = (MessageObject) ChannelAdminLogActivity.this.messages.get((ChannelAdminLogActivity.this.messages.size() - (i2 - this.messagesStartRow)) - 1);
                        if (messageObject3.isOutOwner() == messageObject.isOutOwner() && messageObject3.messageOwner.from_id == messageObject.messageOwner.from_id && Math.abs(messageObject3.messageOwner.date - messageObject.messageOwner.date) <= 300) {
                            z4 = true;
                            if (itemViewType2 == viewHolder.getItemViewType()) {
                                messageObject2 = (MessageObject) ChannelAdminLogActivity.this.messages.get(ChannelAdminLogActivity.this.messages.size() - (i - this.messagesStartRow));
                                if ((messageObject2.messageOwner.reply_markup instanceof TL_replyInlineMarkup) == 0 && messageObject2.isOutOwner() == messageObject.isOutOwner() && messageObject2.messageOwner.from_id == messageObject.messageOwner.from_id && Math.abs(messageObject2.messageOwner.date - messageObject.messageOwner.date) <= 300) {
                                    chatMessageCell.setMessageObject(messageObject, null, z4, z2);
                                    if (z3 && DownloadController.getInstance(ChannelAdminLogActivity.this.currentAccount).canDownloadMedia(messageObject) != 0) {
                                        chatMessageCell.downloadAudioIfNeed();
                                    }
                                    chatMessageCell.setHighlighted(false);
                                    chatMessageCell.setHighlightedText(null);
                                }
                            }
                            z2 = false;
                            chatMessageCell.setMessageObject(messageObject, null, z4, z2);
                            chatMessageCell.downloadAudioIfNeed();
                            chatMessageCell.setHighlighted(false);
                            chatMessageCell.setHighlightedText(null);
                        }
                    }
                    z4 = false;
                    if (itemViewType2 == viewHolder.getItemViewType()) {
                        messageObject2 = (MessageObject) ChannelAdminLogActivity.this.messages.get(ChannelAdminLogActivity.this.messages.size() - (i - this.messagesStartRow));
                        chatMessageCell.setMessageObject(messageObject, null, z4, z2);
                        chatMessageCell.downloadAudioIfNeed();
                        chatMessageCell.setHighlighted(false);
                        chatMessageCell.setHighlightedText(null);
                    }
                    z2 = false;
                    chatMessageCell.setMessageObject(messageObject, null, z4, z2);
                    chatMessageCell.downloadAudioIfNeed();
                    chatMessageCell.setHighlighted(false);
                    chatMessageCell.setHighlightedText(null);
                } else if ((view instanceof ChatActionCell) != null) {
                    ChatActionCell chatActionCell = (ChatActionCell) view;
                    chatActionCell.setMessageObject(messageObject);
                    chatActionCell.setAlpha(1.0f);
                }
            }
        }

        public int getItemViewType(int i) {
            return (i < this.messagesStartRow || i >= this.messagesEndRow) ? 4 : ((MessageObject) ChannelAdminLogActivity.this.messages.get((ChannelAdminLogActivity.this.messages.size() - (i - this.messagesStartRow)) - 1)).contentType;
        }

        public void onViewAttachedToWindow(ViewHolder viewHolder) {
            if (viewHolder.itemView instanceof ChatMessageCell) {
                final ChatMessageCell chatMessageCell = (ChatMessageCell) viewHolder.itemView;
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
                        chatMessageCell.setVisiblePart(top, measuredHeight2 - top);
                        return true;
                    }
                });
                chatMessageCell.setHighlighted(false);
            }
        }

        public void updateRowWithMessageObject(MessageObject messageObject) {
            messageObject = ChannelAdminLogActivity.this.messages.indexOf(messageObject);
            if (messageObject != -1) {
                notifyItemChanged(((this.messagesStartRow + ChannelAdminLogActivity.this.messages.size()) - messageObject) - 1);
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

        public void notifyItemChanged(int i) {
            updateRows();
            try {
                super.notifyItemChanged(i);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }

        public void notifyItemRangeChanged(int i, int i2) {
            updateRows();
            try {
                super.notifyItemRangeChanged(i, i2);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }

        public void notifyItemInserted(int i) {
            updateRows();
            try {
                super.notifyItemInserted(i);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }

        public void notifyItemMoved(int i, int i2) {
            updateRows();
            try {
                super.notifyItemMoved(i, i2);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }

        public void notifyItemRangeInserted(int i, int i2) {
            updateRows();
            try {
                super.notifyItemRangeInserted(i, i2);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }

        public void notifyItemRemoved(int i) {
            updateRows();
            try {
                super.notifyItemRemoved(i);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }

        public void notifyItemRangeRemoved(int i, int i2) {
            updateRows();
            try {
                super.notifyItemRangeRemoved(i, i2);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
    }

    /* renamed from: org.telegram.ui.ChannelAdminLogActivity$1 */
    class C23291 extends EmptyPhotoViewerProvider {
        C23291() {
        }

        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
            ImageReceiver imageReceiver;
            View childAt;
            FileLocation fileLocation2 = fileLocation;
            int childCount = ChannelAdminLogActivity.this.chatListView.getChildCount();
            int i2 = 0;
            int i3 = 0;
            while (true) {
                imageReceiver = null;
                if (i3 >= childCount) {
                    return null;
                }
                childAt = ChannelAdminLogActivity.this.chatListView.getChildAt(i3);
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
                                PhotoSize photoSize = (PhotoSize) messageObject2.photoThumbs.get(i4);
                                if (photoSize.location.volume_id == fileLocation2.volume_id && photoSize.location.local_id == fileLocation2.local_id) {
                                    imageReceiver = chatActionCell.getPhotoImage();
                                    break;
                                }
                            }
                        }
                    }
                }
                if (imageReceiver != null) {
                    break;
                }
                i3++;
            }
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
    }

    private void updateBottomOverlay() {
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
                            this.emptyView.setText(AndroidUtilities.replaceTags(LocaleController.getString("EventLogEmpty", C0446R.string.EventLogEmpty)));
                        } else {
                            this.emptyView.setText(AndroidUtilities.replaceTags(LocaleController.getString("EventLogEmptyChannel", C0446R.string.EventLogEmptyChannel)));
                        }
                    }
                }
                this.emptyView.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(5.0f));
                this.emptyView.setText(AndroidUtilities.replaceTags(LocaleController.getString("EventLogEmptySearch", C0446R.string.EventLogEmptySearch)));
            } else {
                this.emptyView.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(5.0f));
                this.emptyView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("EventLogEmptyTextSearch", C0446R.string.EventLogEmptyTextSearch, this.searchQuery)));
            }
        }
    }

    private void loadMessages(boolean z) {
        if (!this.loading) {
            int i = 0;
            if (z) {
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
            TLObject tL_channels_getAdminLog = new TL_channels_getAdminLog();
            tL_channels_getAdminLog.channel = MessagesController.getInputChannel(this.currentChat);
            tL_channels_getAdminLog.f44q = this.searchQuery;
            tL_channels_getAdminLog.limit = 50;
            if (z || this.messages.isEmpty()) {
                tL_channels_getAdminLog.max_id = 0;
            } else {
                tL_channels_getAdminLog.max_id = this.minEventId;
            }
            tL_channels_getAdminLog.min_id = 0;
            if (this.currentFilter != null) {
                tL_channels_getAdminLog.flags = 1 | tL_channels_getAdminLog.flags;
                tL_channels_getAdminLog.events_filter = this.currentFilter;
            }
            if (this.selectedAdmins != null) {
                tL_channels_getAdminLog.flags |= 2;
                while (i < this.selectedAdmins.size()) {
                    tL_channels_getAdminLog.admins.add(MessagesController.getInstance(this.currentAccount).getInputUser((User) this.selectedAdmins.valueAt(i)));
                    i++;
                }
            }
            updateEmptyPlaceholder();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_getAdminLog, new C19642());
            if (z && this.chatAdapter) {
                this.chatAdapter.notifyDataSetChanged();
            }
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i != NotificationCenter.emojiDidLoaded) {
            int i3 = 0;
            ChatMessageCell chatMessageCell;
            MessageObject messageObject;
            if (i == NotificationCenter.messagePlayingDidStarted) {
                if (((MessageObject) objArr[0]).isRoundVideo() != 0) {
                    MediaController.getInstance().setTextureView(createTextureView(true), this.aspectRatioFrameLayout, this.roundVideoContainer, true);
                    updateTextureViewPosition();
                }
                if (this.chatListView != 0) {
                    i = this.chatListView.getChildCount();
                    for (i2 = 0; i2 < i; i2++) {
                        objArr = this.chatListView.getChildAt(i2);
                        if (objArr instanceof ChatMessageCell) {
                            chatMessageCell = (ChatMessageCell) objArr;
                            messageObject = chatMessageCell.getMessageObject();
                            if (messageObject != null) {
                                if (!messageObject.isVoice()) {
                                    if (!messageObject.isMusic()) {
                                        if (messageObject.isRoundVideo()) {
                                            chatMessageCell.checkRoundVideoPlayback(false);
                                            if (!(MediaController.getInstance().isPlayingMessage(messageObject) || messageObject.audioProgress == 0.0f)) {
                                                messageObject.resetPlayingProgress();
                                                chatMessageCell.invalidate();
                                            }
                                        }
                                    }
                                }
                                chatMessageCell.updateButtonState(false);
                            }
                        }
                    }
                    return;
                }
                return;
            }
            if (i != NotificationCenter.messagePlayingDidReset) {
                if (i != NotificationCenter.messagePlayingPlayStateChanged) {
                    if (i == NotificationCenter.messagePlayingProgressDidChanged) {
                        Integer num = (Integer) objArr[0];
                        if (this.chatListView != 0) {
                            i2 = this.chatListView.getChildCount();
                            while (i3 < i2) {
                                objArr = this.chatListView.getChildAt(i3);
                                if (objArr instanceof ChatMessageCell) {
                                    chatMessageCell = (ChatMessageCell) objArr;
                                    messageObject = chatMessageCell.getMessageObject();
                                    if (messageObject != null && messageObject.getId() == num.intValue()) {
                                        i = MediaController.getInstance().getPlayingMessageObject();
                                        if (i != 0) {
                                            messageObject.audioProgress = i.audioProgress;
                                            messageObject.audioProgressSec = i.audioProgressSec;
                                            messageObject.audioPlayerDuration = i.audioPlayerDuration;
                                            chatMessageCell.updatePlayingMessageProgress();
                                            return;
                                        }
                                        return;
                                    }
                                }
                                i3++;
                            }
                            return;
                        }
                        return;
                    } else if (i == NotificationCenter.didSetNewWallpapper && this.fragmentView != 0) {
                        ((SizeNotifierFrameLayout) this.fragmentView).setBackgroundImage(Theme.getCachedWallpaper());
                        this.progressView2.getBackground().setColorFilter(Theme.colorFilter);
                        if (this.emptyView != 0) {
                            this.emptyView.getBackground().setColorFilter(Theme.colorFilter);
                        }
                        this.chatListView.invalidateViews();
                        return;
                    } else {
                        return;
                    }
                }
            }
            if (this.chatListView != 0) {
                i = this.chatListView.getChildCount();
                for (i2 = 0; i2 < i; i2++) {
                    objArr = this.chatListView.getChildAt(i2);
                    if (objArr instanceof ChatMessageCell) {
                        chatMessageCell = (ChatMessageCell) objArr;
                        MessageObject messageObject2 = chatMessageCell.getMessageObject();
                        if (messageObject2 != null) {
                            if (!messageObject2.isVoice()) {
                                if (!messageObject2.isMusic()) {
                                    if (messageObject2.isRoundVideo() && !MediaController.getInstance().isPlayingMessage(messageObject2)) {
                                        chatMessageCell.checkRoundVideoPlayback(true);
                                    }
                                }
                            }
                            chatMessageCell.updateButtonState(false);
                        }
                    }
                }
            }
        } else if (this.chatListView != 0) {
            this.chatListView.invalidateViews();
        }
    }

    public View createView(Context context) {
        Context context2 = context;
        if (this.chatMessageCellsCache.isEmpty()) {
            for (int i = 0; i < 8; i++) {
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
        r0.searchItem = r0.actionBar.createMenu().addItem(0, (int) C0446R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new C19664());
        r0.searchItem.getSearchField().setHint(LocaleController.getString("Search", C0446R.string.Search));
        r0.avatarContainer.setEnabled(false);
        r0.avatarContainer.setTitle(r0.currentChat.title);
        r0.avatarContainer.setSubtitle(LocaleController.getString("EventLogAllEvents", C0446R.string.EventLogAllEvents));
        r0.avatarContainer.setChatAvatar(r0.currentChat);
        r0.fragmentView = new SizeNotifierFrameLayout(context2) {
            protected void onAttachedToWindow() {
                super.onAttachedToWindow();
                MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
                if (playingMessageObject != null && playingMessageObject.isRoundVideo() && playingMessageObject.eventId != 0 && playingMessageObject.getDialogId() == ((long) (-ChannelAdminLogActivity.this.currentChat.id))) {
                    MediaController.getInstance().setTextureView(ChannelAdminLogActivity.this.createTextureView(false), ChannelAdminLogActivity.this.aspectRatioFrameLayout, ChannelAdminLogActivity.this.roundVideoContainer, true);
                }
            }

            protected boolean drawChild(Canvas canvas, View view, long j) {
                j = super.drawChild(canvas, view, j);
                if (view == ChannelAdminLogActivity.this.actionBar && ChannelAdminLogActivity.this.parentLayout != null) {
                    ChannelAdminLogActivity.this.parentLayout.drawHeaderShadow(canvas, ChannelAdminLogActivity.this.actionBar.getVisibility() == 0 ? ChannelAdminLogActivity.this.actionBar.getMeasuredHeight() : 0);
                }
                return j;
            }

            protected boolean isActionBarVisible() {
                return ChannelAdminLogActivity.this.actionBar.getVisibility() == 0;
            }

            protected void onMeasure(int i, int i2) {
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
                    if (!(childAt == null || childAt.getVisibility() == 8)) {
                        if (childAt != ChannelAdminLogActivity.this.actionBar) {
                            if (childAt != ChannelAdminLogActivity.this.chatListView) {
                                if (childAt != ChannelAdminLogActivity.this.progressView) {
                                    if (childAt == ChannelAdminLogActivity.this.emptyViewContainer) {
                                        childAt.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec(size2, NUM));
                                    } else {
                                        measureChildWithMargins(childAt, i, 0, i2, 0);
                                    }
                                }
                            }
                            childAt.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), size2 - AndroidUtilities.dp(50.0f)), NUM));
                        }
                    }
                }
            }

            protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                z = getChildCount();
                for (boolean z2 = false; z2 < z; z2++) {
                    View childAt = getChildAt(z2);
                    if (childAt.getVisibility() != 8) {
                        int i5;
                        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) childAt.getLayoutParams();
                        int measuredWidth = childAt.getMeasuredWidth();
                        int measuredHeight = childAt.getMeasuredHeight();
                        int i6 = layoutParams.gravity;
                        if (i6 == -1) {
                            i6 = 51;
                        }
                        int i7 = i6 & 7;
                        i6 &= 112;
                        i7 &= 7;
                        if (i7 == 1) {
                            i7 = ((((i3 - i) - measuredWidth) / 2) + layoutParams.leftMargin) - layoutParams.rightMargin;
                        } else if (i7 != 5) {
                            i7 = layoutParams.leftMargin;
                        } else {
                            i7 = (i3 - measuredWidth) - layoutParams.rightMargin;
                        }
                        if (i6 == 16) {
                            i5 = ((((i4 - i2) - measuredHeight) / 2) + layoutParams.topMargin) - layoutParams.bottomMargin;
                        } else if (i6 != 48) {
                            i5 = i6 != 80 ? layoutParams.topMargin : ((i4 - i2) - measuredHeight) - layoutParams.bottomMargin;
                        } else {
                            i5 = layoutParams.topMargin + getPaddingTop();
                            if (childAt != ChannelAdminLogActivity.this.actionBar && ChannelAdminLogActivity.this.actionBar.getVisibility() == 0) {
                                i5 += ChannelAdminLogActivity.this.actionBar.getMeasuredHeight();
                            }
                        }
                        if (childAt == ChannelAdminLogActivity.this.emptyViewContainer) {
                            i5 -= AndroidUtilities.dp(24.0f) - (ChannelAdminLogActivity.this.actionBar.getVisibility() == 0 ? ChannelAdminLogActivity.this.actionBar.getMeasuredHeight() / 2 : 0);
                        } else if (childAt == ChannelAdminLogActivity.this.actionBar) {
                            i5 -= getPaddingTop();
                        }
                        childAt.layout(i7, i5, measuredWidth + i7, measuredHeight + i5);
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
            public boolean drawChild(Canvas canvas, View view, long j) {
                j = super.drawChild(canvas, view, j);
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
                                return j;
                            }
                        }
                        if (chatMessageCell.isPinnedTop()) {
                            childViewHolder = ChannelAdminLogActivity.this.chatListView.getChildViewHolder(view);
                            if (childViewHolder != null) {
                                do {
                                    childViewHolder = ChannelAdminLogActivity.this.chatListView.findViewHolderForAdapterPosition(childViewHolder.getAdapterPosition() - 1);
                                    if (childViewHolder == null) {
                                        break;
                                    }
                                    top = childViewHolder.itemView.getTop();
                                    if (!(childViewHolder.itemView instanceof ChatMessageCell)) {
                                        break;
                                    }
                                } while (((ChatMessageCell) childViewHolder.itemView).isPinnedTop());
                            }
                        }
                        view = view.getTop() + chatMessageCell.getLayoutHeight();
                        int height = ChannelAdminLogActivity.this.chatListView.getHeight() - ChannelAdminLogActivity.this.chatListView.getPaddingBottom();
                        if (view > height) {
                            view = height;
                        }
                        if (view - AndroidUtilities.dp(48.0f) < top) {
                            view = AndroidUtilities.dp(48.0f) + top;
                        }
                        avatarImage.setImageY(view - AndroidUtilities.dp(44.0f));
                        avatarImage.draw(canvas);
                    }
                }
                return j;
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

            public void smoothScrollToPosition(RecyclerView recyclerView, State state, int i) {
                state = new LinearSmoothScrollerMiddle(recyclerView.getContext());
                state.setTargetPosition(i);
                startSmoothScroll(state);
            }
        };
        r0.chatLayoutManager.setOrientation(1);
        r0.chatLayoutManager.setStackFromEnd(true);
        r0.chatListView.setLayoutManager(r0.chatLayoutManager);
        r0.contentView.addView(r0.chatListView, LayoutHelper.createFrame(-1, -1.0f));
        r0.chatListView.setOnScrollListener(new OnScrollListener() {
            private final int scrollValue = AndroidUtilities.dp(100.0f);
            private float totalDy = null;

            /* renamed from: org.telegram.ui.ChannelAdminLogActivity$10$1 */
            class C09491 extends AnimatorListenerAdapter {
                C09491() {
                }

                public void onAnimationEnd(Animator animator) {
                    if (animator.equals(ChannelAdminLogActivity.this.floatingDateAnimation) != null) {
                        ChannelAdminLogActivity.this.floatingDateAnimation = null;
                    }
                }
            }

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
                if (i2 != 0 && ChannelAdminLogActivity.this.scrollingFloatingDate != 0 && ChannelAdminLogActivity.this.currentFloatingTopIsNotMessage == 0 && ChannelAdminLogActivity.this.floatingDateView.getTag() == 0) {
                    if (ChannelAdminLogActivity.this.floatingDateAnimation != 0) {
                        ChannelAdminLogActivity.this.floatingDateAnimation.cancel();
                    }
                    ChannelAdminLogActivity.this.floatingDateView.setTag(Integer.valueOf(1));
                    ChannelAdminLogActivity.this.floatingDateAnimation = new AnimatorSet();
                    ChannelAdminLogActivity.this.floatingDateAnimation.setDuration(150);
                    i = ChannelAdminLogActivity.this.floatingDateAnimation;
                    i2 = new Animator[1];
                    i2[0] = ObjectAnimator.ofFloat(ChannelAdminLogActivity.this.floatingDateView, "alpha", new float[]{1.0f});
                    i.playTogether(i2);
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
        r0.progressView2.setBackgroundResource(C0446R.drawable.system_loader);
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
                int intrinsicHeight = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), intrinsicHeight);
                Theme.chat_composeShadowDrawable.draw(canvas);
                canvas.drawRect(0.0f, (float) intrinsicHeight, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
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

                public void didSelectRights(TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter, SparseArray<User> sparseArray) {
                    ChannelAdminLogActivity.this.currentFilter = tL_channelAdminLogEventsFilter;
                    ChannelAdminLogActivity.this.selectedAdmins = sparseArray;
                    if (ChannelAdminLogActivity.this.currentFilter == null) {
                        if (ChannelAdminLogActivity.this.selectedAdmins == null) {
                            ChannelAdminLogActivity.this.avatarContainer.setSubtitle(LocaleController.getString("EventLogAllEvents", C0446R.string.EventLogAllEvents));
                            ChannelAdminLogActivity.this.loadMessages(true);
                        }
                    }
                    ChannelAdminLogActivity.this.avatarContainer.setSubtitle(LocaleController.getString("EventLogSelectedEvents", C0446R.string.EventLogSelectedEvents));
                    ChannelAdminLogActivity.this.loadMessages(true);
                }
            }

            public void onClick(View view) {
                if (ChannelAdminLogActivity.this.getParentActivity() != null) {
                    view = new AdminLogFilterAlert(ChannelAdminLogActivity.this.getParentActivity(), ChannelAdminLogActivity.this.currentFilter, ChannelAdminLogActivity.this.selectedAdmins, ChannelAdminLogActivity.this.currentChat.megagroup);
                    view.setCurrentAdmins(ChannelAdminLogActivity.this.admins);
                    view.setAdminLogFilterAlertDelegate(new C19631());
                    ChannelAdminLogActivity.this.showDialog(view);
                }
            }
        });
        r0.bottomOverlayChatText = new TextView(context2);
        r0.bottomOverlayChatText.setTextSize(1, 15.0f);
        r0.bottomOverlayChatText.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r0.bottomOverlayChatText.setTextColor(Theme.getColor(Theme.key_chat_fieldOverlayText));
        r0.bottomOverlayChatText.setText(LocaleController.getString("SETTINGS", C0446R.string.SETTINGS).toUpperCase());
        r0.bottomOverlayChat.addView(r0.bottomOverlayChatText, LayoutHelper.createFrame(-2, -2, 17));
        r0.bottomOverlayImage = new ImageView(context2);
        r0.bottomOverlayImage.setImageResource(C0446R.drawable.log_info);
        r0.bottomOverlayImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_fieldOverlayText), Mode.MULTIPLY));
        r0.bottomOverlayImage.setScaleType(ScaleType.CENTER);
        r0.bottomOverlayChat.addView(r0.bottomOverlayImage, LayoutHelper.createFrame(48, 48.0f, 53, 3.0f, 0.0f, 0.0f, 0.0f));
        r0.bottomOverlayImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                view = new AlertDialog.Builder(ChannelAdminLogActivity.this.getParentActivity());
                if (ChannelAdminLogActivity.this.currentChat.megagroup) {
                    view.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("EventLogInfoDetail", C0446R.string.EventLogInfoDetail)));
                } else {
                    view.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("EventLogInfoDetailChannel", C0446R.string.EventLogInfoDetailChannel)));
                }
                view.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), null);
                view.setTitle(LocaleController.getString("EventLogInfoTitle", C0446R.string.EventLogInfoTitle));
                ChannelAdminLogActivity.this.showDialog(view.create());
            }
        });
        r0.searchContainer = new FrameLayout(context2) {
            public void onDraw(Canvas canvas) {
                int intrinsicHeight = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), intrinsicHeight);
                Theme.chat_composeShadowDrawable.draw(canvas);
                canvas.drawRect(0.0f, (float) intrinsicHeight, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
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
        r0.searchCalendarButton.setImageResource(C0446R.drawable.search_calendar);
        r0.searchCalendarButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_searchPanelIcons), Mode.MULTIPLY));
        r0.searchContainer.addView(r0.searchCalendarButton, LayoutHelper.createFrame(48, 48, 53));
        r0.searchCalendarButton.setOnClickListener(new View.OnClickListener() {

            /* renamed from: org.telegram.ui.ChannelAdminLogActivity$15$1 */
            class C09501 implements OnDateSetListener {
                C09501() {
                }

                public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
                    datePicker = Calendar.getInstance();
                    datePicker.clear();
                    datePicker.set(i, i2, i3);
                    datePicker = datePicker.getTime().getTime() / 1000;
                    ChannelAdminLogActivity.this.loadMessages(1);
                }
            }

            /* renamed from: org.telegram.ui.ChannelAdminLogActivity$15$2 */
            class C09512 implements OnClickListener {
                public void onClick(DialogInterface dialogInterface, int i) {
                }

                C09512() {
                }
            }

            public void onClick(View view) {
                if (ChannelAdminLogActivity.this.getParentActivity() != null) {
                    AndroidUtilities.hideKeyboard(ChannelAdminLogActivity.this.searchItem.getSearchField());
                    view = Calendar.getInstance();
                    try {
                        View datePickerDialog = new DatePickerDialog(ChannelAdminLogActivity.this.getParentActivity(), new C09501(), view.get(1), view.get(2), view.get(5));
                        final DatePicker datePicker = datePickerDialog.getDatePicker();
                        datePicker.setMinDate(1375315200000L);
                        datePicker.setMaxDate(System.currentTimeMillis());
                        datePickerDialog.setButton(-1, LocaleController.getString("JumpToDate", C0446R.string.JumpToDate), datePickerDialog);
                        datePickerDialog.setButton(-2, LocaleController.getString("Cancel", C0446R.string.Cancel), new C09512());
                        if (VERSION.SDK_INT >= 21) {
                            datePickerDialog.setOnShowListener(new OnShowListener() {
                                public void onShow(DialogInterface dialogInterface) {
                                    dialogInterface = datePicker.getChildCount();
                                    for (int i = 0; i < dialogInterface; i++) {
                                        View childAt = datePicker.getChildAt(i);
                                        ViewGroup.LayoutParams layoutParams = childAt.getLayoutParams();
                                        layoutParams.width = -1;
                                        childAt.setLayoutParams(layoutParams);
                                    }
                                }
                            });
                        }
                        ChannelAdminLogActivity.this.showDialog(datePickerDialog);
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

    private void createMenu(View view) {
        view = view instanceof ChatMessageCell ? ((ChatMessageCell) view).getMessageObject() : view instanceof ChatActionCell ? ((ChatActionCell) view).getMessageObject() : null;
        if (view != null) {
            int messageType = getMessageType(view);
            this.selectedObject = view;
            if (getParentActivity() != null) {
                view = new AlertDialog.Builder(getParentActivity());
                ArrayList arrayList = new ArrayList();
                final ArrayList arrayList2 = new ArrayList();
                if (this.selectedObject.type == 0 || this.selectedObject.caption != null) {
                    arrayList.add(LocaleController.getString("Copy", C0446R.string.Copy));
                    arrayList2.add(Integer.valueOf(3));
                }
                if (messageType == 1) {
                    if (this.selectedObject.currentEvent != null && (this.selectedObject.currentEvent.action instanceof TL_channelAdminLogEventActionChangeStickerSet)) {
                        InputStickerSet inputStickerSet;
                        InputStickerSet inputStickerSet2 = this.selectedObject.currentEvent.action.new_stickerset;
                        if (inputStickerSet2 != null) {
                            if (inputStickerSet2 instanceof TL_inputStickerSetEmpty) {
                            }
                            inputStickerSet = inputStickerSet2;
                            if (inputStickerSet != null) {
                                showDialog(new StickersAlert(getParentActivity(), this, inputStickerSet, null, null));
                                return;
                            }
                        }
                        inputStickerSet2 = this.selectedObject.currentEvent.action.prev_stickerset;
                        inputStickerSet = inputStickerSet2;
                        if (inputStickerSet != null) {
                            showDialog(new StickersAlert(getParentActivity(), this, inputStickerSet, null, null));
                            return;
                        }
                    }
                } else if (messageType == 3) {
                    if ((this.selectedObject.messageOwner.media instanceof TL_messageMediaWebPage) && MessageObject.isNewGifDocument(this.selectedObject.messageOwner.media.webpage.document)) {
                        arrayList.add(LocaleController.getString("SaveToGIFs", C0446R.string.SaveToGIFs));
                        arrayList2.add(Integer.valueOf(11));
                    }
                } else if (messageType == 4) {
                    if (this.selectedObject.isVideo()) {
                        arrayList.add(LocaleController.getString("SaveToGallery", C0446R.string.SaveToGallery));
                        arrayList2.add(Integer.valueOf(4));
                        arrayList.add(LocaleController.getString("ShareFile", C0446R.string.ShareFile));
                        arrayList2.add(Integer.valueOf(6));
                    } else if (this.selectedObject.isMusic()) {
                        arrayList.add(LocaleController.getString("SaveToMusic", C0446R.string.SaveToMusic));
                        arrayList2.add(Integer.valueOf(10));
                        arrayList.add(LocaleController.getString("ShareFile", C0446R.string.ShareFile));
                        arrayList2.add(Integer.valueOf(6));
                    } else if (this.selectedObject.getDocument() != null) {
                        if (MessageObject.isNewGifDocument(this.selectedObject.getDocument())) {
                            arrayList.add(LocaleController.getString("SaveToGIFs", C0446R.string.SaveToGIFs));
                            arrayList2.add(Integer.valueOf(11));
                        }
                        arrayList.add(LocaleController.getString("SaveToDownloads", C0446R.string.SaveToDownloads));
                        arrayList2.add(Integer.valueOf(10));
                        arrayList.add(LocaleController.getString("ShareFile", C0446R.string.ShareFile));
                        arrayList2.add(Integer.valueOf(6));
                    } else {
                        arrayList.add(LocaleController.getString("SaveToGallery", C0446R.string.SaveToGallery));
                        arrayList2.add(Integer.valueOf(4));
                    }
                } else if (messageType == 5) {
                    arrayList.add(LocaleController.getString("ApplyLocalizationFile", C0446R.string.ApplyLocalizationFile));
                    arrayList2.add(Integer.valueOf(5));
                    arrayList.add(LocaleController.getString("SaveToDownloads", C0446R.string.SaveToDownloads));
                    arrayList2.add(Integer.valueOf(10));
                    arrayList.add(LocaleController.getString("ShareFile", C0446R.string.ShareFile));
                    arrayList2.add(Integer.valueOf(6));
                } else if (messageType == 10) {
                    arrayList.add(LocaleController.getString("ApplyThemeFile", C0446R.string.ApplyThemeFile));
                    arrayList2.add(Integer.valueOf(5));
                    arrayList.add(LocaleController.getString("SaveToDownloads", C0446R.string.SaveToDownloads));
                    arrayList2.add(Integer.valueOf(10));
                    arrayList.add(LocaleController.getString("ShareFile", C0446R.string.ShareFile));
                    arrayList2.add(Integer.valueOf(6));
                } else if (messageType == 6) {
                    arrayList.add(LocaleController.getString("SaveToGallery", C0446R.string.SaveToGallery));
                    arrayList2.add(Integer.valueOf(7));
                    arrayList.add(LocaleController.getString("SaveToDownloads", C0446R.string.SaveToDownloads));
                    arrayList2.add(Integer.valueOf(10));
                    arrayList.add(LocaleController.getString("ShareFile", C0446R.string.ShareFile));
                    arrayList2.add(Integer.valueOf(6));
                } else if (messageType == 7) {
                    if (this.selectedObject.isMask()) {
                        arrayList.add(LocaleController.getString("AddToMasks", C0446R.string.AddToMasks));
                    } else {
                        arrayList.add(LocaleController.getString("AddToStickers", C0446R.string.AddToStickers));
                    }
                    arrayList2.add(Integer.valueOf(9));
                } else if (messageType == 8) {
                    User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.selectedObject.messageOwner.media.user_id));
                    if (!(user == null || user.id == UserConfig.getInstance(this.currentAccount).getClientUserId() || ContactsController.getInstance(this.currentAccount).contactsDict.get(Integer.valueOf(user.id)) != null)) {
                        arrayList.add(LocaleController.getString("AddContactTitle", C0446R.string.AddContactTitle));
                        arrayList2.add(Integer.valueOf(15));
                    }
                    if (!(this.selectedObject.messageOwner.media.phone_number == null && this.selectedObject.messageOwner.media.phone_number.length() == 0)) {
                        arrayList.add(LocaleController.getString("Copy", C0446R.string.Copy));
                        arrayList2.add(Integer.valueOf(16));
                        arrayList.add(LocaleController.getString("Call", C0446R.string.Call));
                        arrayList2.add(Integer.valueOf(17));
                    }
                }
                if (!arrayList2.isEmpty()) {
                    view.setItems((CharSequence[]) arrayList.toArray(new CharSequence[arrayList.size()]), new OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (ChannelAdminLogActivity.this.selectedObject != null && i >= 0) {
                                if (i < arrayList2.size()) {
                                    ChannelAdminLogActivity.this.processSelectedOption(((Integer) arrayList2.get(i)).intValue());
                                }
                            }
                        }
                    });
                    view.setTitle(LocaleController.getString("Message", C0446R.string.Message));
                    showDialog(view.create());
                }
            }
        }
    }

    private String getMessageContent(MessageObject messageObject, int i, boolean z) {
        String str = TtmlNode.ANONYMOUS_REGION_ID;
        if (z && i != messageObject.messageOwner.from_id) {
            if (messageObject.messageOwner.from_id > 0) {
                i = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(messageObject.messageOwner.from_id));
                if (i != 0) {
                    z = new StringBuilder();
                    z.append(ContactsController.formatName(i.first_name, i.last_name));
                    z.append(":\n");
                    str = z.toString();
                }
            } else if (messageObject.messageOwner.from_id < 0) {
                i = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-messageObject.messageOwner.from_id));
                if (i != 0) {
                    z = new StringBuilder();
                    z.append(i.title);
                    z.append(":\n");
                    str = z.toString();
                }
            }
        }
        if (messageObject.type == 0 && messageObject.messageOwner.message != 0) {
            i = new StringBuilder();
            i.append(str);
            i.append(messageObject.messageOwner.message);
            return i.toString();
        } else if (messageObject.messageOwner.media == 0 || messageObject.messageOwner.message == 0) {
            i = new StringBuilder();
            i.append(str);
            i.append(messageObject.messageText);
            return i.toString();
        } else {
            i = new StringBuilder();
            i.append(str);
            i.append(messageObject.messageOwner.message);
            return i.toString();
        }
    }

    private TextureView createTextureView(boolean z) {
        if (this.parentLayout == null) {
            return false;
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
                        outline.setOval(0, 0, AndroidUtilities.roundMessageSize, AndroidUtilities.roundMessageSize);
                    }
                });
                this.roundVideoContainer.setClipToOutline(true);
            } else {
                this.roundVideoContainer = new FrameLayout(getParentActivity()) {
                    protected void onSizeChanged(int i, int i2, int i3, int i4) {
                        super.onSizeChanged(i, i2, i3, i4);
                        ChannelAdminLogActivity.this.aspectPath.reset();
                        i = (float) (i / 2);
                        ChannelAdminLogActivity.this.aspectPath.addCircle(i, (float) (i2 / 2), i, Direction.CW);
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
            if (z) {
                this.roundVideoContainer.addView(this.aspectRatioFrameLayout, LayoutHelper.createFrame(-1, -1.0f));
            }
            this.videoTextureView = new TextureView(getParentActivity());
            this.videoTextureView.setOpaque(false);
            this.aspectRatioFrameLayout.addView(this.videoTextureView, LayoutHelper.createFrame(-1, -1.0f));
        }
        if (!this.roundVideoContainer.getParent()) {
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

    private void processSelectedOption(int r14) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r13 = this;
        r0 = r13.selectedObject;
        if (r0 != 0) goto L_0x0005;
    L_0x0004:
        return;
    L_0x0005:
        r0 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
        r1 = 3;
        r2 = 4;
        r3 = 23;
        r4 = 0;
        r5 = 1;
        r6 = 0;
        switch(r14) {
            case 3: goto L_0x03b0;
            case 4: goto L_0x0347;
            case 5: goto L_0x0221;
            case 6: goto L_0x0190;
            case 7: goto L_0x0139;
            case 8: goto L_0x0011;
            case 9: goto L_0x0121;
            case 10: goto L_0x0097;
            case 11: goto L_0x0086;
            case 12: goto L_0x0011;
            case 13: goto L_0x0011;
            case 14: goto L_0x0011;
            case 15: goto L_0x0058;
            case 16: goto L_0x004b;
            case 17: goto L_0x0013;
            default: goto L_0x0011;
        };
    L_0x0011:
        goto L_0x03b9;
    L_0x0013:
        r14 = new android.content.Intent;	 Catch:{ Exception -> 0x0045 }
        r1 = "android.intent.action.DIAL";	 Catch:{ Exception -> 0x0045 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0045 }
        r2.<init>();	 Catch:{ Exception -> 0x0045 }
        r3 = "tel:";	 Catch:{ Exception -> 0x0045 }
        r2.append(r3);	 Catch:{ Exception -> 0x0045 }
        r3 = r13.selectedObject;	 Catch:{ Exception -> 0x0045 }
        r3 = r3.messageOwner;	 Catch:{ Exception -> 0x0045 }
        r3 = r3.media;	 Catch:{ Exception -> 0x0045 }
        r3 = r3.phone_number;	 Catch:{ Exception -> 0x0045 }
        r2.append(r3);	 Catch:{ Exception -> 0x0045 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0045 }
        r2 = android.net.Uri.parse(r2);	 Catch:{ Exception -> 0x0045 }
        r14.<init>(r1, r2);	 Catch:{ Exception -> 0x0045 }
        r1 = 268435456; // 0x10000000 float:2.5243549E-29 double:1.32624737E-315;	 Catch:{ Exception -> 0x0045 }
        r14.addFlags(r1);	 Catch:{ Exception -> 0x0045 }
        r1 = r13.getParentActivity();	 Catch:{ Exception -> 0x0045 }
        r1.startActivityForResult(r14, r0);	 Catch:{ Exception -> 0x0045 }
        goto L_0x03b9;
    L_0x0045:
        r14 = move-exception;
        org.telegram.messenger.FileLog.m3e(r14);
        goto L_0x03b9;
    L_0x004b:
        r14 = r13.selectedObject;
        r14 = r14.messageOwner;
        r14 = r14.media;
        r14 = r14.phone_number;
        org.telegram.messenger.AndroidUtilities.addToClipboard(r14);
        goto L_0x03b9;
    L_0x0058:
        r14 = new android.os.Bundle;
        r14.<init>();
        r0 = "user_id";
        r1 = r13.selectedObject;
        r1 = r1.messageOwner;
        r1 = r1.media;
        r1 = r1.user_id;
        r14.putInt(r0, r1);
        r0 = "phone";
        r1 = r13.selectedObject;
        r1 = r1.messageOwner;
        r1 = r1.media;
        r1 = r1.phone_number;
        r14.putString(r0, r1);
        r0 = "addContact";
        r14.putBoolean(r0, r5);
        r0 = new org.telegram.ui.ContactAddActivity;
        r0.<init>(r14);
        r13.presentFragment(r0);
        goto L_0x03b9;
    L_0x0086:
        r14 = r13.selectedObject;
        r14 = r14.getDocument();
        r0 = r13.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r0.saveGif(r14);
        goto L_0x03b9;
    L_0x0097:
        r14 = android.os.Build.VERSION.SDK_INT;
        if (r14 < r3) goto L_0x00b7;
    L_0x009b:
        r14 = r13.getParentActivity();
        r0 = "android.permission.WRITE_EXTERNAL_STORAGE";
        r14 = r14.checkSelfPermission(r0);
        if (r14 == 0) goto L_0x00b7;
    L_0x00a7:
        r14 = r13.getParentActivity();
        r0 = new java.lang.String[r5];
        r1 = "android.permission.WRITE_EXTERNAL_STORAGE";
        r0[r4] = r1;
        r14.requestPermissions(r0, r2);
        r13.selectedObject = r6;
        return;
    L_0x00b7:
        r14 = r13.selectedObject;
        r14 = r14.getDocument();
        r14 = org.telegram.messenger.FileLoader.getDocumentFileName(r14);
        r0 = android.text.TextUtils.isEmpty(r14);
        if (r0 == 0) goto L_0x00cd;
    L_0x00c7:
        r14 = r13.selectedObject;
        r14 = r14.getFileName();
    L_0x00cd:
        r0 = r13.selectedObject;
        r0 = r0.messageOwner;
        r0 = r0.attachPath;
        if (r0 == 0) goto L_0x00e7;
    L_0x00d5:
        r2 = r0.length();
        if (r2 <= 0) goto L_0x00e7;
    L_0x00db:
        r2 = new java.io.File;
        r2.<init>(r0);
        r2 = r2.exists();
        if (r2 != 0) goto L_0x00e7;
    L_0x00e6:
        r0 = r6;
    L_0x00e7:
        if (r0 == 0) goto L_0x00ef;
    L_0x00e9:
        r2 = r0.length();
        if (r2 != 0) goto L_0x00fb;
    L_0x00ef:
        r0 = r13.selectedObject;
        r0 = r0.messageOwner;
        r0 = org.telegram.messenger.FileLoader.getPathToMessage(r0);
        r0 = r0.toString();
    L_0x00fb:
        r2 = r13.getParentActivity();
        r3 = r13.selectedObject;
        r3 = r3.isMusic();
        if (r3 == 0) goto L_0x0108;
    L_0x0107:
        goto L_0x0109;
    L_0x0108:
        r1 = 2;
    L_0x0109:
        r3 = r13.selectedObject;
        r3 = r3.getDocument();
        if (r3 == 0) goto L_0x011a;
    L_0x0111:
        r3 = r13.selectedObject;
        r3 = r3.getDocument();
        r3 = r3.mime_type;
        goto L_0x011c;
    L_0x011a:
        r3 = "";
    L_0x011c:
        org.telegram.messenger.MediaController.saveFile(r0, r2, r1, r14, r3);
        goto L_0x03b9;
    L_0x0121:
        r14 = new org.telegram.ui.Components.StickersAlert;
        r8 = r13.getParentActivity();
        r0 = r13.selectedObject;
        r10 = r0.getInputStickerSet();
        r11 = 0;
        r12 = 0;
        r7 = r14;
        r9 = r13;
        r7.<init>(r8, r9, r10, r11, r12);
        r13.showDialog(r14);
        goto L_0x03b9;
    L_0x0139:
        r14 = r13.selectedObject;
        r14 = r14.messageOwner;
        r14 = r14.attachPath;
        if (r14 == 0) goto L_0x0153;
    L_0x0141:
        r0 = r14.length();
        if (r0 <= 0) goto L_0x0153;
    L_0x0147:
        r0 = new java.io.File;
        r0.<init>(r14);
        r0 = r0.exists();
        if (r0 != 0) goto L_0x0153;
    L_0x0152:
        r14 = r6;
    L_0x0153:
        if (r14 == 0) goto L_0x015b;
    L_0x0155:
        r0 = r14.length();
        if (r0 != 0) goto L_0x0167;
    L_0x015b:
        r14 = r13.selectedObject;
        r14 = r14.messageOwner;
        r14 = org.telegram.messenger.FileLoader.getPathToMessage(r14);
        r14 = r14.toString();
    L_0x0167:
        r0 = android.os.Build.VERSION.SDK_INT;
        if (r0 < r3) goto L_0x0187;
    L_0x016b:
        r0 = r13.getParentActivity();
        r1 = "android.permission.WRITE_EXTERNAL_STORAGE";
        r0 = r0.checkSelfPermission(r1);
        if (r0 == 0) goto L_0x0187;
    L_0x0177:
        r14 = r13.getParentActivity();
        r0 = new java.lang.String[r5];
        r1 = "android.permission.WRITE_EXTERNAL_STORAGE";
        r0[r4] = r1;
        r14.requestPermissions(r0, r2);
        r13.selectedObject = r6;
        return;
    L_0x0187:
        r0 = r13.getParentActivity();
        org.telegram.messenger.MediaController.saveFile(r14, r0, r4, r6, r6);
        goto L_0x03b9;
    L_0x0190:
        r14 = r13.selectedObject;
        r14 = r14.messageOwner;
        r14 = r14.attachPath;
        if (r14 == 0) goto L_0x01aa;
    L_0x0198:
        r1 = r14.length();
        if (r1 <= 0) goto L_0x01aa;
    L_0x019e:
        r1 = new java.io.File;
        r1.<init>(r14);
        r1 = r1.exists();
        if (r1 != 0) goto L_0x01aa;
    L_0x01a9:
        r14 = r6;
    L_0x01aa:
        if (r14 == 0) goto L_0x01b2;
    L_0x01ac:
        r1 = r14.length();
        if (r1 != 0) goto L_0x01be;
    L_0x01b2:
        r14 = r13.selectedObject;
        r14 = r14.messageOwner;
        r14 = org.telegram.messenger.FileLoader.getPathToMessage(r14);
        r14 = r14.toString();
    L_0x01be:
        r1 = new android.content.Intent;
        r2 = "android.intent.action.SEND";
        r1.<init>(r2);
        r2 = r13.selectedObject;
        r2 = r2.getDocument();
        r2 = r2.mime_type;
        r1.setType(r2);
        r2 = android.os.Build.VERSION.SDK_INT;
        r3 = 24;
        if (r2 < r3) goto L_0x01fd;
    L_0x01d6:
        r2 = "android.intent.extra.STREAM";	 Catch:{ Exception -> 0x01ee }
        r3 = r13.getParentActivity();	 Catch:{ Exception -> 0x01ee }
        r4 = "org.telegram.messenger.provider";	 Catch:{ Exception -> 0x01ee }
        r7 = new java.io.File;	 Catch:{ Exception -> 0x01ee }
        r7.<init>(r14);	 Catch:{ Exception -> 0x01ee }
        r3 = android.support.v4.content.FileProvider.getUriForFile(r3, r4, r7);	 Catch:{ Exception -> 0x01ee }
        r1.putExtra(r2, r3);	 Catch:{ Exception -> 0x01ee }
        r1.setFlags(r5);	 Catch:{ Exception -> 0x01ee }
        goto L_0x020b;
    L_0x01ee:
        r2 = "android.intent.extra.STREAM";
        r3 = new java.io.File;
        r3.<init>(r14);
        r14 = android.net.Uri.fromFile(r3);
        r1.putExtra(r2, r14);
        goto L_0x020b;
    L_0x01fd:
        r2 = "android.intent.extra.STREAM";
        r3 = new java.io.File;
        r3.<init>(r14);
        r14 = android.net.Uri.fromFile(r3);
        r1.putExtra(r2, r14);
    L_0x020b:
        r14 = r13.getParentActivity();
        r2 = "ShareFile";
        r3 = NUM; // 0x7f0c05ef float:1.8612273E38 double:1.053098149E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r2, r3);
        r1 = android.content.Intent.createChooser(r1, r2);
        r14.startActivityForResult(r1, r0);
        goto L_0x03b9;
    L_0x0221:
        r14 = r13.selectedObject;
        r14 = r14.messageOwner;
        r14 = r14.attachPath;
        if (r14 == 0) goto L_0x0247;
    L_0x0229:
        r14 = r13.selectedObject;
        r14 = r14.messageOwner;
        r14 = r14.attachPath;
        r14 = r14.length();
        if (r14 == 0) goto L_0x0247;
    L_0x0235:
        r14 = new java.io.File;
        r0 = r13.selectedObject;
        r0 = r0.messageOwner;
        r0 = r0.attachPath;
        r14.<init>(r0);
        r0 = r14.exists();
        if (r0 == 0) goto L_0x0247;
    L_0x0246:
        goto L_0x0248;
    L_0x0247:
        r14 = r6;
    L_0x0248:
        if (r14 != 0) goto L_0x0259;
    L_0x024a:
        r0 = r13.selectedObject;
        r0 = r0.messageOwner;
        r0 = org.telegram.messenger.FileLoader.getPathToMessage(r0);
        r1 = r0.exists();
        if (r1 == 0) goto L_0x0259;
    L_0x0258:
        r14 = r0;
    L_0x0259:
        if (r14 == 0) goto L_0x03b9;
    L_0x025b:
        r0 = r14.getName();
        r0 = r0.toLowerCase();
        r1 = "attheme";
        r0 = r0.endsWith(r1);
        r1 = NUM; // 0x7f0c048c float:1.8611553E38 double:1.0530979736E-314;
        r2 = NUM; // 0x7f0c0075 float:1.860943E38 double:1.0530974563E-314;
        if (r0 == 0) goto L_0x02f8;
    L_0x0271:
        r0 = r13.chatLayoutManager;
        r3 = -1;
        if (r0 == 0) goto L_0x02a7;
    L_0x0276:
        r0 = r13.chatLayoutManager;
        r0 = r0.findLastVisibleItemPosition();
        r4 = r13.chatLayoutManager;
        r4 = r4.getItemCount();
        r4 = r4 - r5;
        if (r0 >= r4) goto L_0x02a5;
    L_0x0285:
        r0 = r13.chatLayoutManager;
        r0 = r0.findFirstVisibleItemPosition();
        r13.scrollToPositionOnRecreate = r0;
        r0 = r13.chatListView;
        r4 = r13.scrollToPositionOnRecreate;
        r0 = r0.findViewHolderForAdapterPosition(r4);
        r0 = (org.telegram.ui.Components.RecyclerListView.Holder) r0;
        if (r0 == 0) goto L_0x02a2;
    L_0x0299:
        r0 = r0.itemView;
        r0 = r0.getTop();
        r13.scrollToOffsetOnRecreate = r0;
        goto L_0x02a7;
    L_0x02a2:
        r13.scrollToPositionOnRecreate = r3;
        goto L_0x02a7;
    L_0x02a5:
        r13.scrollToPositionOnRecreate = r3;
    L_0x02a7:
        r0 = r13.selectedObject;
        r0 = r0.getDocumentName();
        r0 = org.telegram.ui.ActionBar.Theme.applyThemeFile(r14, r0, r5);
        if (r0 == 0) goto L_0x02bd;
    L_0x02b3:
        r1 = new org.telegram.ui.ThemePreviewActivity;
        r1.<init>(r14, r0);
        r13.presentFragment(r1);
        goto L_0x03b9;
    L_0x02bd:
        r13.scrollToPositionOnRecreate = r3;
        r14 = r13.getParentActivity();
        if (r14 != 0) goto L_0x02c8;
    L_0x02c5:
        r13.selectedObject = r6;
        return;
    L_0x02c8:
        r14 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r0 = r13.getParentActivity();
        r14.<init>(r0);
        r0 = "AppName";
        r0 = org.telegram.messenger.LocaleController.getString(r0, r2);
        r14.setTitle(r0);
        r0 = "IncorrectTheme";
        r2 = NUM; // 0x7f0c032a float:1.8610835E38 double:1.0530977987E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r0, r2);
        r14.setMessage(r0);
        r0 = "OK";
        r0 = org.telegram.messenger.LocaleController.getString(r0, r1);
        r14.setPositiveButton(r0, r6);
        r14 = r14.create();
        r13.showDialog(r14);
        goto L_0x03b9;
    L_0x02f8:
        r0 = org.telegram.messenger.LocaleController.getInstance();
        r3 = r13.currentAccount;
        r14 = r0.applyLanguageFile(r14, r3);
        if (r14 == 0) goto L_0x030e;
    L_0x0304:
        r14 = new org.telegram.ui.LanguageSelectActivity;
        r14.<init>();
        r13.presentFragment(r14);
        goto L_0x03b9;
    L_0x030e:
        r14 = r13.getParentActivity();
        if (r14 != 0) goto L_0x0317;
    L_0x0314:
        r13.selectedObject = r6;
        return;
    L_0x0317:
        r14 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r0 = r13.getParentActivity();
        r14.<init>(r0);
        r0 = "AppName";
        r0 = org.telegram.messenger.LocaleController.getString(r0, r2);
        r14.setTitle(r0);
        r0 = "IncorrectLocalization";
        r2 = NUM; // 0x7f0c0329 float:1.8610833E38 double:1.053097798E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r0, r2);
        r14.setMessage(r0);
        r0 = "OK";
        r0 = org.telegram.messenger.LocaleController.getString(r0, r1);
        r14.setPositiveButton(r0, r6);
        r14 = r14.create();
        r13.showDialog(r14);
        goto L_0x03b9;
    L_0x0347:
        r14 = r13.selectedObject;
        r14 = r14.messageOwner;
        r14 = r14.attachPath;
        if (r14 == 0) goto L_0x0361;
    L_0x034f:
        r0 = r14.length();
        if (r0 <= 0) goto L_0x0361;
    L_0x0355:
        r0 = new java.io.File;
        r0.<init>(r14);
        r0 = r0.exists();
        if (r0 != 0) goto L_0x0361;
    L_0x0360:
        r14 = r6;
    L_0x0361:
        if (r14 == 0) goto L_0x0369;
    L_0x0363:
        r0 = r14.length();
        if (r0 != 0) goto L_0x0375;
    L_0x0369:
        r14 = r13.selectedObject;
        r14 = r14.messageOwner;
        r14 = org.telegram.messenger.FileLoader.getPathToMessage(r14);
        r14 = r14.toString();
    L_0x0375:
        r0 = r13.selectedObject;
        r0 = r0.type;
        if (r0 == r1) goto L_0x0381;
    L_0x037b:
        r0 = r13.selectedObject;
        r0 = r0.type;
        if (r0 != r5) goto L_0x03b9;
    L_0x0381:
        r0 = android.os.Build.VERSION.SDK_INT;
        if (r0 < r3) goto L_0x03a1;
    L_0x0385:
        r0 = r13.getParentActivity();
        r3 = "android.permission.WRITE_EXTERNAL_STORAGE";
        r0 = r0.checkSelfPermission(r3);
        if (r0 == 0) goto L_0x03a1;
    L_0x0391:
        r14 = r13.getParentActivity();
        r0 = new java.lang.String[r5];
        r1 = "android.permission.WRITE_EXTERNAL_STORAGE";
        r0[r4] = r1;
        r14.requestPermissions(r0, r2);
        r13.selectedObject = r6;
        return;
    L_0x03a1:
        r0 = r13.getParentActivity();
        r2 = r13.selectedObject;
        r2 = r2.type;
        if (r2 != r1) goto L_0x03ac;
    L_0x03ab:
        r4 = r5;
    L_0x03ac:
        org.telegram.messenger.MediaController.saveFile(r14, r0, r4, r6, r6);
        goto L_0x03b9;
    L_0x03b0:
        r14 = r13.selectedObject;
        r14 = r13.getMessageContent(r14, r4, r5);
        org.telegram.messenger.AndroidUtilities.addToClipboard(r14);
    L_0x03b9:
        r13.selectedObject = r6;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChannelAdminLogActivity.processSelectedOption(int):void");
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
                    messageObject = messageObject.getInputStickerSet();
                    if (messageObject instanceof TL_inputStickerSetID) {
                        if (DataQuery.getInstance(this.currentAccount).isStickerPackInstalled(messageObject.id) == null) {
                            return 7;
                        }
                    } else if ((messageObject instanceof TL_inputStickerSetShortName) && DataQuery.getInstance(this.currentAccount).isStickerPackInstalled(messageObject.short_name) == null) {
                        return 7;
                    } else {
                        return 2;
                    }
                }
                if (!messageObject.isRoundVideo() || (messageObject.isRoundVideo() && BuildVars.DEBUG_VERSION)) {
                    if (!((messageObject.messageOwner.media instanceof TL_messageMediaPhoto) || messageObject.getDocument() != null || messageObject.isMusic())) {
                        if (messageObject.isVideo()) {
                        }
                    }
                    int i = 0;
                    if (!(messageObject.messageOwner.attachPath == null || messageObject.messageOwner.attachPath.length() == 0 || !new File(messageObject.messageOwner.attachPath).exists())) {
                        i = 1;
                    }
                    if (i == 0 && FileLoader.getPathToMessage(messageObject.messageOwner).exists()) {
                        i = 1;
                    }
                    if (i != 0) {
                        if (messageObject.getDocument() != null) {
                            String str = messageObject.getDocument().mime_type;
                            if (str != null) {
                                if (messageObject.getDocumentName().toLowerCase().endsWith("attheme") != null) {
                                    return 10;
                                }
                                if (str.endsWith("/xml") != null) {
                                    return 5;
                                }
                                if (!(str.endsWith("/png") == null && str.endsWith("/jpg") == null && str.endsWith("/jpeg") == null)) {
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
                if (messageObject.isMediaEmpty() != null) {
                    return 3;
                }
                return 2;
            }
        }
        if (messageObject.getId() == null) {
            return -1;
        }
        return 1;
    }

    private void loadAdmins() {
        TLObject tL_channels_getParticipants = new TL_channels_getParticipants();
        tL_channels_getParticipants.channel = MessagesController.getInputChannel(this.currentChat);
        tL_channels_getParticipants.filter = new TL_channelParticipantsAdmins();
        tL_channels_getParticipants.offset = 0;
        tL_channels_getParticipants.limit = Callback.DEFAULT_DRAG_ANIMATION_DURATION;
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_getParticipants, new RequestDelegate() {
            public void run(final TLObject tLObject, final TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        if (tL_error == null) {
                            TL_channels_channelParticipants tL_channels_channelParticipants = (TL_channels_channelParticipants) tLObject;
                            MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).putUsers(tL_channels_channelParticipants.users, false);
                            ChannelAdminLogActivity.this.admins = tL_channels_channelParticipants.participants;
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

    private void hideFloatingDateView(boolean z) {
        if (this.floatingDateView.getTag() != null && !this.currentFloatingDateOnScreen) {
            if (!this.scrollingFloatingDate || this.currentFloatingTopIsNotMessage) {
                this.floatingDateView.setTag(null);
                if (z) {
                    this.floatingDateAnimation = new AnimatorSet();
                    this.floatingDateAnimation.setDuration(150);
                    z = this.floatingDateAnimation;
                    Animator[] animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.floatingDateView, "alpha", new float[]{0.0f});
                    z.playTogether(animatorArr);
                    this.floatingDateAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (animator.equals(ChannelAdminLogActivity.this.floatingDateAnimation) != null) {
                                ChannelAdminLogActivity.this.floatingDateAnimation = null;
                            }
                        }
                    });
                    this.floatingDateAnimation.setStartDelay(500);
                    this.floatingDateAnimation.start();
                    return;
                }
                if (this.floatingDateAnimation) {
                    this.floatingDateAnimation.cancel();
                    this.floatingDateAnimation = null;
                }
                this.floatingDateView.setAlpha(0.0f);
            }
        }
    }

    private void checkScrollForLoad(boolean z) {
        if (this.chatLayoutManager != null) {
            if (!this.paused) {
                int i;
                boolean findFirstVisibleItemPosition = this.chatLayoutManager.findFirstVisibleItemPosition();
                if (findFirstVisibleItemPosition) {
                    i = 0;
                } else {
                    i = Math.abs(this.chatLayoutManager.findLastVisibleItemPosition() - findFirstVisibleItemPosition) + 1;
                }
                if (i > 0) {
                    this.chatAdapter.getItemCount();
                    if (!(findFirstVisibleItemPosition > (z ? true : true) || this.loading || this.endReached)) {
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
        boolean z;
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
                    z = true;
                    break;
                }
            }
        }
        z = false;
        if (this.roundVideoContainer != null) {
            MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
            if (z) {
                MediaController.getInstance().setCurrentRoundVisible(true);
                return;
            }
            this.roundVideoContainer.setTranslationY((float) ((-AndroidUtilities.roundMessageSize) - 100));
            this.fragmentView.invalidate();
            if (playingMessageObject != null && playingMessageObject.isRoundVideo()) {
                if (this.checkTextureViewPosition || PipRoundVideoView.getInstance() != null) {
                    MediaController.getInstance().setCurrentRoundVisible(false);
                }
            }
        }
    }

    private void updateMessagesVisisblePart() {
        if (this.chatListView != null) {
            MessageObject playingMessageObject;
            int childCount = r0.chatListView.getChildCount();
            int measuredHeight = r0.chatListView.getMeasuredHeight();
            int i = Integer.MAX_VALUE;
            int i2 = i;
            Object obj = null;
            View view = null;
            View view2 = null;
            View view3 = null;
            for (int i3 = 0; i3 < childCount; i3++) {
                View childAt = r0.chatListView.getChildAt(i3);
                boolean z = childAt instanceof ChatMessageCell;
                if (z) {
                    ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                    int top = chatMessageCell.getTop();
                    chatMessageCell.getBottom();
                    int i4 = top >= 0 ? 0 : -top;
                    int measuredHeight2 = chatMessageCell.getMeasuredHeight();
                    if (measuredHeight2 > measuredHeight) {
                        measuredHeight2 = i4 + measuredHeight;
                    }
                    chatMessageCell.setVisiblePart(i4, measuredHeight2 - i4);
                    MessageObject messageObject = chatMessageCell.getMessageObject();
                    if (r0.roundVideoContainer != null && messageObject.isRoundVideo() && MediaController.getInstance().isPlayingMessage(messageObject)) {
                        ImageReceiver photoImage = chatMessageCell.getPhotoImage();
                        r0.roundVideoContainer.setTranslationX((float) photoImage.getImageX());
                        r0.roundVideoContainer.setTranslationY((float) ((r0.fragmentView.getPaddingTop() + top) + photoImage.getImageY()));
                        r0.fragmentView.invalidate();
                        r0.roundVideoContainer.invalidate();
                        obj = 1;
                    }
                }
                if (childAt.getBottom() > r0.chatListView.getPaddingTop()) {
                    int bottom = childAt.getBottom();
                    if (bottom < i) {
                        if (z || (childAt instanceof ChatActionCell)) {
                            view = childAt;
                        }
                        i = bottom;
                        view2 = childAt;
                    }
                    if ((childAt instanceof ChatActionCell) && ((ChatActionCell) childAt).getMessageObject().isDateObject) {
                        if (childAt.getAlpha() != 1.0f) {
                            childAt.setAlpha(1.0f);
                        }
                        if (bottom < i2) {
                            i2 = bottom;
                            view3 = childAt;
                        }
                    }
                }
            }
            if (r0.roundVideoContainer != null) {
                if (obj == null) {
                    r0.roundVideoContainer.setTranslationY((float) ((-AndroidUtilities.roundMessageSize) - 100));
                    r0.fragmentView.invalidate();
                    playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
                    if (playingMessageObject != null && playingMessageObject.isRoundVideo() && r0.checkTextureViewPosition) {
                        MediaController.getInstance().setCurrentRoundVisible(false);
                    }
                } else {
                    MediaController.getInstance().setCurrentRoundVisible(true);
                }
            }
            if (view != null) {
                if (view instanceof ChatMessageCell) {
                    playingMessageObject = ((ChatMessageCell) view).getMessageObject();
                } else {
                    playingMessageObject = ((ChatActionCell) view).getMessageObject();
                }
                r0.floatingDateView.setCustomDate(playingMessageObject.messageOwner.date);
            }
            boolean z2 = false;
            r0.currentFloatingDateOnScreen = false;
            if (!((view2 instanceof ChatMessageCell) || (view2 instanceof ChatActionCell))) {
                z2 = true;
            }
            r0.currentFloatingTopIsNotMessage = z2;
            if (view3 != null) {
                if (view3.getTop() <= r0.chatListView.getPaddingTop()) {
                    if (!r0.currentFloatingTopIsNotMessage) {
                        if (view3.getAlpha() != 0.0f) {
                            view3.setAlpha(0.0f);
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
                        measuredHeight = view3.getBottom() - r0.chatListView.getPaddingTop();
                        if (measuredHeight > r0.floatingDateView.getMeasuredHeight() || measuredHeight >= r0.floatingDateView.getMeasuredHeight() * 2) {
                            r0.floatingDateView.setTranslationY(0.0f);
                        } else {
                            r0.floatingDateView.setTranslationY((float) (((-r0.floatingDateView.getMeasuredHeight()) * 2) + measuredHeight));
                        }
                    }
                }
                if (view3.getAlpha() != 1.0f) {
                    view3.setAlpha(1.0f);
                }
                hideFloatingDateView(r0.currentFloatingTopIsNotMessage ^ true);
                measuredHeight = view3.getBottom() - r0.chatListView.getPaddingTop();
                if (measuredHeight > r0.floatingDateView.getMeasuredHeight()) {
                }
                r0.floatingDateView.setTranslationY(0.0f);
            } else {
                hideFloatingDateView(true);
                r0.floatingDateView.setTranslationY(0.0f);
            }
        }
    }

    public void onTransitionAnimationStart(boolean z, boolean z2) {
        NotificationCenter.getInstance(this.currentAccount).setAllowedNotificationsDutingAnimation(new int[]{NotificationCenter.chatInfoDidLoaded, NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.messagesDidLoaded, NotificationCenter.botKeyboardDidLoaded});
        NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(true);
        if (z) {
            this.openAnimationEnded = false;
        }
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(false);
        if (z) {
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

    public void onConfigurationChanged(Configuration configuration) {
        fixLayout();
        if ((this.visibleDialog instanceof DatePickerDialog) != null) {
            this.visibleDialog.dismiss();
        }
    }

    private void alertUserOpenError(MessageObject messageObject) {
        if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
            builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), null);
            if (messageObject.type == 3) {
                builder.setMessage(LocaleController.getString("NoPlayerInstalled", C0446R.string.NoPlayerInstalled));
            } else {
                builder.setMessage(LocaleController.formatString("NoHandleAppInstalled", C0446R.string.NoHandleAppInstalled, messageObject.getDocument().mime_type));
            }
            showDialog(builder.create());
        }
    }

    public Chat getCurrentChat() {
        return this.currentChat;
    }

    private void addCanBanUser(Bundle bundle, int i) {
        if (this.currentChat.megagroup && this.admins != null) {
            if (ChatObject.canBlockUsers(this.currentChat)) {
                for (int i2 = 0; i2 < this.admins.size(); i2++) {
                    ChannelParticipant channelParticipant = (ChannelParticipant) this.admins.get(i2);
                    if (channelParticipant.user_id == i) {
                        if (channelParticipant.can_edit == 0) {
                            return;
                        }
                        bundle.putInt("ban_chat_id", this.currentChat.id);
                    }
                }
                bundle.putInt("ban_chat_id", this.currentChat.id);
            }
        }
    }

    public void showOpenUrlAlert(final String str, boolean z) {
        if (!Browser.isInternalUrl(str, null)) {
            if (z) {
                z = new AlertDialog.Builder(getParentActivity());
                z.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                z.setMessage(LocaleController.formatString("OpenUrlAlert", C0446R.string.OpenUrlAlert, str));
                z.setPositiveButton(LocaleController.getString("Open", C0446R.string.Open), new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Browser.openUrl(ChannelAdminLogActivity.this.getParentActivity(), str, true);
                    }
                });
                z.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                showDialog(z.create());
                return;
            }
        }
        Browser.openUrl(getParentActivity(), str, true);
    }

    private void removeMessageObject(MessageObject messageObject) {
        messageObject = this.messages.indexOf(messageObject);
        if (messageObject != -1) {
            this.messages.remove(messageObject);
            if (this.chatAdapter != null) {
                this.chatAdapter.notifyItemRemoved(((this.chatAdapter.messagesStartRow + this.messages.size()) - messageObject) - 1);
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
        ThemeDescriptionDelegate themeDescriptionDelegate = null;
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
            themeDescriptionDelegate = r0.avatarContainer.getTimeItem();
        }
        r1[206] = new ThemeDescription(themeDescriptionDelegate, 0, null, null, null, null, Theme.key_chat_secretTimerText);
        return r1;
    }
}
