package org.telegram.ui;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.SystemClock;
import android.text.TextUtils.TruncateAt;
import android.text.style.CharacterStyle;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutParams;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
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
import org.telegram.tgnet.TLRPC.TL_reactionCount;
import org.telegram.tgnet.TLRPC.TL_theme;
import org.telegram.tgnet.TLRPC.TL_user;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.MenuDrawable;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate;
import org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate.-CC;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.DialogCell.CustomDialog;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Components.ColorPicker;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.SizeNotifierFrameLayout;

public class ThemePreviewActivity extends BaseFragment implements NotificationCenterDelegate {
    public static final int SCREEN_TYPE_ACCENT_COLOR = 1;
    public static final int SCREEN_TYPE_PREVIEW = 0;
    private int accentColor;
    private ActionBar actionBar2;
    private Runnable applyColorAction;
    private boolean applyColorScheduled;
    private ThemeInfo applyingTheme;
    private int backgroundColor;
    private FrameLayout buttonsContainer;
    private TextView cancelButton;
    private ColorPicker colorPicker;
    private int colorType;
    private boolean deleteOnCancel;
    private DialogsAdapter dialogsAdapter;
    private TextView doneButton;
    private View dotsContainer;
    private TextView dropDown;
    private ActionBarMenuItem dropDownContainer;
    private Drawable dropDownDrawable;
    private ImageView floatingButton;
    private int gradientToColor;
    private int lastPickedColor;
    private int lastPickedColorNum;
    private LinearLayout linearLayout;
    private RecyclerListView listView;
    private RecyclerListView listView2;
    private MessagesAdapter messagesAdapter;
    private int myMessagesAccentColor;
    private int myMessagesGradientAccentColor;
    private boolean nightTheme;
    private OnGlobalLayoutListener onGlobalLayoutListener;
    private FrameLayout page1;
    private SizeNotifierFrameLayout page2;
    private final int screenType;
    private Drawable sheetDrawable;
    private List<ThemeDescription> themeDescriptions;
    private boolean useDefaultThemeForButtons;
    private ViewPager viewPager;
    private long watchForKeyboardEndTime;

    public class DialogsAdapter extends SelectionAdapter {
        private ArrayList<CustomDialog> dialogs = new ArrayList();
        private Context mContext;

        public DialogsAdapter(Context context) {
            this.mContext = context;
            int currentTimeMillis = (int) (System.currentTimeMillis() / 1000);
            CustomDialog customDialog = new CustomDialog();
            customDialog.name = LocaleController.getString("ThemePreviewDialog1", NUM);
            customDialog.message = LocaleController.getString("ThemePreviewDialogMessage1", NUM);
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
            customDialog.name = LocaleController.getString("ThemePreviewDialog2", NUM);
            customDialog.message = LocaleController.getString("ThemePreviewDialogMessage2", NUM);
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
            customDialog.name = LocaleController.getString("ThemePreviewDialog3", NUM);
            customDialog.message = LocaleController.getString("ThemePreviewDialogMessage3", NUM);
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
            customDialog.name = LocaleController.getString("ThemePreviewDialog4", NUM);
            customDialog.message = LocaleController.getString("ThemePreviewDialogMessage4", NUM);
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
            customDialog.name = LocaleController.getString("ThemePreviewDialog5", NUM);
            customDialog.message = LocaleController.getString("ThemePreviewDialogMessage5", NUM);
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
            customDialog.name = LocaleController.getString("ThemePreviewDialog6", NUM);
            customDialog.message = LocaleController.getString("ThemePreviewDialogMessage6", NUM);
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
            customDialog.name = LocaleController.getString("ThemePreviewDialog7", NUM);
            customDialog.message = LocaleController.getString("ThemePreviewDialogMessage7", NUM);
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
            customDialog.name = LocaleController.getString("ThemePreviewDialog8", NUM);
            customDialog.message = LocaleController.getString("ThemePreviewDialogMessage8", NUM);
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
        final /* synthetic */ ThemePreviewActivity this$0;

        public boolean isEnabled(ViewHolder viewHolder) {
            return false;
        }

        public MessagesAdapter(ThemePreviewActivity themePreviewActivity, Context context) {
            TL_message tL_message;
            this.this$0 = themePreviewActivity;
            boolean z = this.this$0.screenType != 1 && Utilities.random.nextInt(100) <= 1;
            this.showSecretMessages = z;
            this.mContext = context;
            this.messages = new ArrayList();
            int currentTimeMillis = ((int) (System.currentTimeMillis() / 1000)) - 3600;
            String str = "audio/ogg";
            int i;
            MessageObject messageObject;
            MessageObject messageObject2;
            MessageMedia messageMedia;
            Document document;
            TL_documentAttributeAudio tL_documentAttributeAudio;
            if (themePreviewActivity.screenType == 1) {
                tL_message = new TL_message();
                tL_message.message = LocaleController.getString("NewThemePreviewReply", NUM);
                i = currentTimeMillis + 60;
                tL_message.date = i;
                tL_message.dialog_id = 1;
                tL_message.flags = 259;
                tL_message.from_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                tL_message.id = 1;
                tL_message.media = new TL_messageMediaEmpty();
                tL_message.out = true;
                tL_message.to_id = new TL_peerUser();
                tL_message.to_id.user_id = 0;
                messageObject = new MessageObject(UserConfig.selectedAccount, tL_message, true);
                tL_message = new TL_message();
                tL_message.message = LocaleController.getString("NewThemePreviewLine2", NUM);
                tL_message.date = currentTimeMillis + 960;
                tL_message.dialog_id = 1;
                tL_message.flags = 259;
                tL_message.from_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                tL_message.id = 1;
                tL_message.media = new TL_messageMediaEmpty();
                tL_message.out = true;
                tL_message.to_id = new TL_peerUser();
                tL_message.to_id.user_id = 0;
                MessageObject messageObject3 = new MessageObject(UserConfig.selectedAccount, tL_message, true);
                messageObject3.resetLayout();
                messageObject3.eventId = 1;
                this.messages.add(messageObject3);
                tL_message = new TL_message();
                tL_message.message = LocaleController.getString("NewThemePreviewLine1", NUM);
                tL_message.date = i;
                tL_message.dialog_id = 1;
                tL_message.flags = 265;
                tL_message.from_id = 0;
                tL_message.id = 1;
                tL_message.reply_to_msg_id = 5;
                tL_message.media = new TL_messageMediaEmpty();
                tL_message.out = false;
                tL_message.to_id = new TL_peerUser();
                tL_message.to_id.user_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                messageObject2 = new MessageObject(UserConfig.selectedAccount, tL_message, true);
                messageObject2.customReplyName = LocaleController.getString("NewThemePreviewName", NUM);
                messageObject2.eventId = 1;
                messageObject2.resetLayout();
                messageObject2.replyMessageObject = messageObject;
                this.messages.add(messageObject2);
                this.messages.add(messageObject);
                tL_message = new TL_message();
                tL_message.date = currentTimeMillis + 120;
                tL_message.dialog_id = 1;
                tL_message.flags = 259;
                tL_message.out = false;
                tL_message.from_id = 0;
                tL_message.id = 1;
                tL_message.media = new TL_messageMediaDocument();
                messageMedia = tL_message.media;
                messageMedia.flags |= 3;
                messageMedia.document = new TL_document();
                document = tL_message.media.document;
                document.mime_type = str;
                document.file_reference = new byte[0];
                tL_documentAttributeAudio = new TL_documentAttributeAudio();
                tL_documentAttributeAudio.flags = 1028;
                tL_documentAttributeAudio.duration = 3;
                tL_documentAttributeAudio.voice = true;
                tL_documentAttributeAudio.waveform = new byte[]{(byte) 0, (byte) 4, (byte) 17, (byte) -50, (byte) -93, (byte) 86, (byte) -103, (byte) -45, (byte) -12, (byte) -26, (byte) 63, (byte) -25, (byte) -3, (byte) 109, (byte) -114, (byte) -54, (byte) -4, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -29, (byte) -1, (byte) -1, (byte) -25, (byte) -1, (byte) -1, (byte) -97, (byte) -43, (byte) 57, (byte) -57, (byte) -108, (byte) 1, (byte) -91, (byte) -4, (byte) -47, (byte) 21, (byte) 99, (byte) 10, (byte) 97, (byte) 43, (byte) 45, (byte) 115, (byte) -112, (byte) -77, (byte) 51, (byte) -63, (byte) 66, (byte) 40, (byte) 34, (byte) -122, (byte) -116, (byte) 48, (byte) -124, (byte) 16, (byte) 66, (byte) -120, (byte) 16, (byte) 68, (byte) 16, (byte) 33, (byte) 4, (byte) 1};
                tL_message.media.document.attributes.add(tL_documentAttributeAudio);
                tL_message.out = true;
                tL_message.to_id = new TL_peerUser();
                tL_message.to_id.user_id = 0;
                messageObject2 = new MessageObject(themePreviewActivity.currentAccount, tL_message, true);
                messageObject2.audioProgressSec = 1;
                messageObject2.audioProgress = 0.3f;
                messageObject2.useCustomPhoto = true;
                this.messages.add(messageObject2);
            } else if (this.showSecretMessages) {
                TL_user tL_user = new TL_user();
                tL_user.id = Integer.MAX_VALUE;
                tL_user.first_name = "Me";
                TL_user tL_user2 = new TL_user();
                tL_user2.id = NUM;
                tL_user2.first_name = "Serj";
                ArrayList arrayList = new ArrayList();
                arrayList.add(tL_user);
                arrayList.add(tL_user2);
                MessagesController.getInstance(themePreviewActivity.currentAccount).putUsers(arrayList, true);
                TL_message tL_message2 = new TL_message();
                tL_message2.message = "Guess why Half-Life 3 was never released.";
                int i2 = currentTimeMillis + 960;
                tL_message2.date = i2;
                tL_message2.dialog_id = -1;
                tL_message2.flags = 259;
                tL_message2.id = NUM;
                tL_message2.media = new TL_messageMediaEmpty();
                tL_message2.out = false;
                tL_message2.to_id = new TL_peerChat();
                tL_message2.to_id.chat_id = 1;
                tL_message2.from_id = tL_user2.id;
                this.messages.add(new MessageObject(themePreviewActivity.currentAccount, tL_message2, true));
                tL_message2 = new TL_message();
                tL_message2.message = "No.\nAnd every unnecessary ping of the dev delays the release for 10 days.\nEvery request for ETA delays the release for 2 weeks.";
                tL_message2.date = i2;
                tL_message2.dialog_id = -1;
                tL_message2.flags = 259;
                tL_message2.id = 1;
                tL_message2.media = new TL_messageMediaEmpty();
                tL_message2.out = false;
                tL_message2.to_id = new TL_peerChat();
                tL_message2.to_id.chat_id = 1;
                tL_message2.from_id = tL_user2.id;
                this.messages.add(new MessageObject(themePreviewActivity.currentAccount, tL_message2, true));
                TL_message tL_message3 = new TL_message();
                tL_message3.message = "Is source code for Android coming anytime soon?";
                tL_message3.date = currentTimeMillis + 600;
                tL_message3.dialog_id = -1;
                tL_message3.flags = 259;
                tL_message3.id = 1;
                tL_message3.media = new TL_messageMediaEmpty();
                tL_message3.out = false;
                tL_message3.to_id = new TL_peerChat();
                tL_message3.to_id.chat_id = 1;
                tL_message3.from_id = tL_user.id;
                this.messages.add(new MessageObject(themePreviewActivity.currentAccount, tL_message3, true));
            } else {
                tL_message = new TL_message();
                tL_message.message = LocaleController.getString("ThemePreviewLine1", NUM);
                i = currentTimeMillis + 60;
                tL_message.date = i;
                tL_message.dialog_id = 1;
                tL_message.flags = 259;
                tL_message.from_id = UserConfig.getInstance(themePreviewActivity.currentAccount).getClientUserId();
                tL_message.id = 1;
                tL_message.media = new TL_messageMediaEmpty();
                tL_message.out = true;
                tL_message.to_id = new TL_peerUser();
                tL_message.to_id.user_id = 0;
                messageObject = new MessageObject(themePreviewActivity.currentAccount, tL_message, true);
                tL_message = new TL_message();
                tL_message.message = LocaleController.getString("ThemePreviewLine2", NUM);
                tL_message.date = currentTimeMillis + 960;
                tL_message.dialog_id = 1;
                tL_message.flags = 259;
                tL_message.from_id = UserConfig.getInstance(themePreviewActivity.currentAccount).getClientUserId();
                tL_message.id = 1;
                tL_message.media = new TL_messageMediaEmpty();
                tL_message.out = true;
                tL_message.to_id = new TL_peerUser();
                tL_message.to_id.user_id = 0;
                this.messages.add(new MessageObject(themePreviewActivity.currentAccount, tL_message, true));
                tL_message = new TL_message();
                tL_message.date = currentTimeMillis + 130;
                tL_message.dialog_id = 1;
                tL_message.flags = 259;
                tL_message.from_id = 0;
                tL_message.id = 5;
                tL_message.media = new TL_messageMediaDocument();
                MessageMedia messageMedia2 = tL_message.media;
                messageMedia2.flags |= 3;
                messageMedia2.document = new TL_document();
                Document document2 = tL_message.media.document;
                document2.mime_type = "audio/mp4";
                document2.file_reference = new byte[0];
                TL_documentAttributeAudio tL_documentAttributeAudio2 = new TL_documentAttributeAudio();
                tL_documentAttributeAudio2.duration = 243;
                tL_documentAttributeAudio2.performer = LocaleController.getString("ThemePreviewSongPerformer", NUM);
                tL_documentAttributeAudio2.title = LocaleController.getString("ThemePreviewSongTitle", NUM);
                tL_message.media.document.attributes.add(tL_documentAttributeAudio2);
                tL_message.out = false;
                tL_message.to_id = new TL_peerUser();
                tL_message.to_id.user_id = UserConfig.getInstance(themePreviewActivity.currentAccount).getClientUserId();
                this.messages.add(new MessageObject(themePreviewActivity.currentAccount, tL_message, true));
                tL_message = new TL_message();
                tL_message.message = LocaleController.getString("ThemePreviewLine3", NUM);
                tL_message.date = i;
                tL_message.dialog_id = 1;
                tL_message.flags = 265;
                tL_message.from_id = 0;
                tL_message.id = 1;
                tL_message.reply_to_msg_id = 5;
                tL_message.media = new TL_messageMediaEmpty();
                tL_message.out = false;
                tL_message.to_id = new TL_peerUser();
                tL_message.to_id.user_id = UserConfig.getInstance(themePreviewActivity.currentAccount).getClientUserId();
                messageObject2 = new MessageObject(themePreviewActivity.currentAccount, tL_message, true);
                messageObject2.customReplyName = LocaleController.getString("ThemePreviewLine3Reply", NUM);
                messageObject2.replyMessageObject = messageObject;
                this.messages.add(messageObject2);
                tL_message = new TL_message();
                tL_message.date = currentTimeMillis + 120;
                tL_message.dialog_id = 1;
                tL_message.flags = 259;
                tL_message.from_id = UserConfig.getInstance(themePreviewActivity.currentAccount).getClientUserId();
                tL_message.id = 1;
                tL_message.media = new TL_messageMediaDocument();
                messageMedia = tL_message.media;
                messageMedia.flags |= 3;
                messageMedia.document = new TL_document();
                document = tL_message.media.document;
                document.mime_type = str;
                document.file_reference = new byte[0];
                tL_documentAttributeAudio = new TL_documentAttributeAudio();
                tL_documentAttributeAudio.flags = 1028;
                tL_documentAttributeAudio.duration = 3;
                tL_documentAttributeAudio.voice = true;
                tL_documentAttributeAudio.waveform = new byte[]{(byte) 0, (byte) 4, (byte) 17, (byte) -50, (byte) -93, (byte) 86, (byte) -103, (byte) -45, (byte) -12, (byte) -26, (byte) 63, (byte) -25, (byte) -3, (byte) 109, (byte) -114, (byte) -54, (byte) -4, (byte) -1, (byte) -1, (byte) -1, (byte) -1, (byte) -29, (byte) -1, (byte) -1, (byte) -25, (byte) -1, (byte) -1, (byte) -97, (byte) -43, (byte) 57, (byte) -57, (byte) -108, (byte) 1, (byte) -91, (byte) -4, (byte) -47, (byte) 21, (byte) 99, (byte) 10, (byte) 97, (byte) 43, (byte) 45, (byte) 115, (byte) -112, (byte) -77, (byte) 51, (byte) -63, (byte) 66, (byte) 40, (byte) 34, (byte) -122, (byte) -116, (byte) 48, (byte) -124, (byte) 16, (byte) 66, (byte) -120, (byte) 16, (byte) 68, (byte) 16, (byte) 33, (byte) 4, (byte) 1};
                tL_message.media.document.attributes.add(tL_documentAttributeAudio);
                tL_message.out = true;
                tL_message.to_id = new TL_peerUser();
                tL_message.to_id.user_id = 0;
                messageObject2 = new MessageObject(themePreviewActivity.currentAccount, tL_message, true);
                messageObject2.audioProgressSec = 1;
                messageObject2.audioProgress = 0.3f;
                messageObject2.useCustomPhoto = true;
                this.messages.add(messageObject2);
                this.messages.add(messageObject);
                tL_message = new TL_message();
                tL_message.date = currentTimeMillis + 10;
                tL_message.dialog_id = 1;
                tL_message.flags = 257;
                tL_message.from_id = 0;
                tL_message.id = 1;
                tL_message.media = new TL_messageMediaPhoto();
                messageMedia = tL_message.media;
                messageMedia.flags |= 3;
                messageMedia.photo = new TL_photo();
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
                tL_message.message = LocaleController.getString("ThemePreviewLine4", NUM);
                tL_message.out = false;
                tL_message.to_id = new TL_peerUser();
                tL_message.to_id.user_id = UserConfig.getInstance(themePreviewActivity.currentAccount).getClientUserId();
                messageObject2 = new MessageObject(themePreviewActivity.currentAccount, tL_message, true);
                messageObject2.useCustomPhoto = true;
                this.messages.add(messageObject2);
            }
            tL_message = new TL_message();
            tL_message.message = LocaleController.formatDateChat((long) currentTimeMillis);
            tL_message.id = 0;
            tL_message.date = currentTimeMillis;
            MessageObject messageObject4 = new MessageObject(themePreviewActivity.currentAccount, tL_message, false);
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

                    public /* synthetic */ void didPressReaction(ChatMessageCell chatMessageCell, TL_reactionCount tL_reactionCount) {
                        -CC.$default$didPressReaction(this, chatMessageCell, tL_reactionCount);
                    }

                    public /* synthetic */ void didPressReplyMessage(ChatMessageCell chatMessageCell, int i) {
                        -CC.$default$didPressReplyMessage(this, chatMessageCell, i);
                    }

                    public /* synthetic */ void didPressShare(ChatMessageCell chatMessageCell) {
                        -CC.$default$didPressShare(this, chatMessageCell);
                    }

                    public /* synthetic */ void didPressUrl(ChatMessageCell chatMessageCell, CharacterStyle characterStyle, boolean z) {
                        -CC.$default$didPressUrl(this, chatMessageCell, characterStyle, z);
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

                    public /* synthetic */ void didStartVideoStream(MessageObject messageObject) {
                        -CC.$default$didStartVideoStream(this, messageObject);
                    }

                    public /* synthetic */ String getAdminRank(int i) {
                        return -CC.$default$getAdminRank(this, i);
                    }

                    public /* synthetic */ void needOpenWebView(String str, String str2, String str3, String str4, int i, int i2) {
                        -CC.$default$needOpenWebView(this, str, str2, str3, str4, i, i2);
                    }

                    public /* synthetic */ boolean needPlayMessage(MessageObject messageObject) {
                        return -CC.$default$needPlayMessage(this, messageObject);
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

    public /* synthetic */ void lambda$new$0$ThemePreviewActivity() {
        this.applyColorScheduled = false;
        applyColor(this.lastPickedColor, this.lastPickedColorNum);
    }

    public ThemePreviewActivity(ThemeInfo themeInfo) {
        this(themeInfo, false, 0, false);
    }

    public ThemePreviewActivity(ThemeInfo themeInfo, boolean z, int i, boolean z2) {
        this.useDefaultThemeForButtons = true;
        this.colorType = 1;
        this.applyColorAction = new -$$Lambda$ThemePreviewActivity$NnNC9ivczXrgLuuk8XD7kgj3Rtc(this);
        this.screenType = i;
        this.swipeBackEnabled = false;
        this.nightTheme = z2;
        this.applyingTheme = themeInfo;
        this.deleteOnCancel = z;
        if (i == 1) {
            Theme.applyThemeTemporary(new ThemeInfo(this.applyingTheme), true);
            this.useDefaultThemeForButtons = false;
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.goingToPreviewTheme, new Object[0]);
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
        this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(this.screenType == 1 ? 12.0f : 0.0f));
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
        float f = 4.0f;
        if (VERSION.SDK_INT >= 21) {
            StateListAnimator stateListAnimator = new StateListAnimator();
            stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButton, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButton, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
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
            private boolean ignoreLayout;

            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                int size = MeasureSpec.getSize(i);
                int size2 = MeasureSpec.getSize(i2);
                setMeasuredDimension(size, size2);
                if (ThemePreviewActivity.this.dropDownContainer != null) {
                    this.ignoreLayout = true;
                    if (!AndroidUtilities.isTablet()) {
                        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) ThemePreviewActivity.this.dropDownContainer.getLayoutParams();
                        layoutParams.topMargin = VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0;
                        ThemePreviewActivity.this.dropDownContainer.setLayoutParams(layoutParams);
                    }
                    if (AndroidUtilities.isTablet() || ApplicationLoader.applicationContext.getResources().getConfiguration().orientation != 2) {
                        ThemePreviewActivity.this.dropDown.setTextSize(1, 20.0f);
                    } else {
                        ThemePreviewActivity.this.dropDown.setTextSize(1, 18.0f);
                    }
                    this.ignoreLayout = false;
                }
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
                    ThemePreviewActivity.this.parentLayout.drawHeaderShadow(canvas, ThemePreviewActivity.this.actionBar2.getVisibility() == 0 ? (int) (((float) ThemePreviewActivity.this.actionBar2.getMeasuredHeight()) + ThemePreviewActivity.this.actionBar2.getTranslationY()) : 0);
                }
                return drawChild;
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        this.page2.setBackgroundImage(Theme.getCachedWallpaper(), Theme.isWallpaperMotion());
        this.messagesAdapter = new MessagesAdapter(this, context2);
        this.actionBar2 = createActionBar(context);
        this.actionBar2.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar2.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    ThemePreviewActivity.this.cancelThemeApply();
                } else if (i < 1 || i > 3) {
                    if (i == 4) {
                        Theme.saveThemeAccent(ThemePreviewActivity.this.applyingTheme, ThemePreviewActivity.this.accentColor);
                        Theme.saveThemeMyMessagesAccent(ThemePreviewActivity.this.applyingTheme, ThemePreviewActivity.this.myMessagesAccentColor, ThemePreviewActivity.this.myMessagesGradientAccentColor);
                        Theme.saveThemeBackgroundColors(ThemePreviewActivity.this.applyingTheme, ThemePreviewActivity.this.backgroundColor, ThemePreviewActivity.this.gradientToColor);
                        Theme.applyPreviousTheme();
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, ThemePreviewActivity.this.applyingTheme, Boolean.valueOf(ThemePreviewActivity.this.nightTheme));
                        ThemePreviewActivity.this.finishFragment();
                    }
                } else if (ThemePreviewActivity.this.getParentActivity() != null && ThemePreviewActivity.this.colorType != i) {
                    if (i == 2 && Theme.hasCustomWallpaper()) {
                        Builder builder = new Builder(ThemePreviewActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString("ChangeChatBackground", NUM));
                        builder.setMessage(LocaleController.getString("ChangeWallpaperToColor", NUM));
                        builder.setPositiveButton(LocaleController.getString("Change", NUM), new -$$Lambda$ThemePreviewActivity$5$wm5km-rPFwVmnDfOb4gv_1mhlLA(this));
                        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                        ThemePreviewActivity.this.showDialog(builder.create());
                        return;
                    }
                    ThemePreviewActivity.this.colorType = i;
                    if (i == 1) {
                        ThemePreviewActivity.this.dropDown.setText(LocaleController.getString("ColorPickerMainColor", NUM));
                        ThemePreviewActivity.this.colorPicker.setType(1, ThemePreviewActivity.this.hasChanges(1), false, false, false);
                        ThemePreviewActivity.this.colorPicker.setColor(ThemePreviewActivity.this.accentColor, 0);
                    } else if (i == 2) {
                        ThemePreviewActivity.this.dropDown.setText(LocaleController.getString("ColorPickerBackground", NUM));
                        int color = Theme.getColor("chat_wallpaper");
                        String str = "chat_wallpaper_gradient_to";
                        int color2 = Theme.hasThemeKey(str) ? Theme.getColor(str) : 0;
                        ColorPicker access$1900 = ThemePreviewActivity.this.colorPicker;
                        boolean access$1800 = ThemePreviewActivity.this.hasChanges(2);
                        boolean z = (ThemePreviewActivity.this.gradientToColor == 0 && color2 == 0) ? false : true;
                        access$1900.setType(2, access$1800, true, z, false);
                        ColorPicker access$19002 = ThemePreviewActivity.this.colorPicker;
                        if (ThemePreviewActivity.this.gradientToColor != 0) {
                            color2 = ThemePreviewActivity.this.gradientToColor;
                        }
                        access$19002.setColor(color2, 1);
                        access$19002 = ThemePreviewActivity.this.colorPicker;
                        if (ThemePreviewActivity.this.backgroundColor != 0) {
                            color = ThemePreviewActivity.this.backgroundColor;
                        }
                        access$19002.setColor(color, 0);
                    } else if (i == 3) {
                        ThemePreviewActivity.this.dropDown.setText(LocaleController.getString("ColorPickerMyMessages", NUM));
                        ThemePreviewActivity.this.colorPicker.setType(2, ThemePreviewActivity.this.hasChanges(3), true, ThemePreviewActivity.this.myMessagesGradientAccentColor != 0, false);
                        ThemePreviewActivity.this.colorPicker.setColor(ThemePreviewActivity.this.myMessagesGradientAccentColor, 1);
                        ThemePreviewActivity.this.colorPicker.setColor(ThemePreviewActivity.this.myMessagesAccentColor != 0 ? ThemePreviewActivity.this.myMessagesAccentColor : ThemePreviewActivity.this.accentColor, 0);
                    }
                    if (i != 1) {
                        ThemePreviewActivity.this.colorPicker.setMinBrightness(null);
                        ThemePreviewActivity.this.colorPicker.setMaxBrightness(null);
                    } else if (ThemePreviewActivity.this.applyingTheme.isDark()) {
                        ThemePreviewActivity.this.colorPicker.setMinBrightness(-$$Lambda$ThemePreviewActivity$5$akIUKIQWF9o14cRKJSWq6DH-xKU.INSTANCE);
                    } else {
                        ThemePreviewActivity.this.colorPicker.setMaxBrightness(-$$Lambda$ThemePreviewActivity$5$WfThNfIvF9yfo01FVbKwhalNP5k.INSTANCE);
                    }
                }
            }

            public /* synthetic */ void lambda$onItemClick$0$ThemePreviewActivity$5(DialogInterface dialogInterface, int i) {
                Theme.resetCustomWallpaper();
                onItemClick(2);
            }
        });
        String str = "fonts/rmedium.ttf";
        if (this.messagesAdapter.showSecretMessages) {
            this.actionBar2.setTitle("Telegram Beta Chat");
            this.actionBar2.setSubtitle(LocaleController.formatPluralString("Members", 505));
        } else if (this.screenType == 1) {
            ActionBarMenu createMenu = this.actionBar2.createMenu();
            createMenu.addItem(4, LocaleController.getString("Save", NUM).toUpperCase());
            this.dropDownContainer = new ActionBarMenuItem(context2, createMenu, 0, 0);
            this.dropDownContainer.setSubMenuOpenSide(1);
            this.dropDownContainer.addSubItem(1, LocaleController.getString("ColorPickerMainColor", NUM));
            this.dropDownContainer.addSubItem(2, LocaleController.getString("ColorPickerBackground", NUM));
            this.dropDownContainer.addSubItem(3, LocaleController.getString("ColorPickerMyMessages", NUM));
            this.actionBar2.addView(this.dropDownContainer, LayoutHelper.createFrame(-2, -1.0f, 51, AndroidUtilities.isTablet() ? 64.0f : 56.0f, 0.0f, 40.0f, 0.0f));
            this.dropDownContainer.setOnClickListener(new -$$Lambda$ThemePreviewActivity$YPhE-lyYHwkD5fNafYq-tQN7oD0(this));
            this.dropDown = new TextView(context2);
            this.dropDown.setGravity(3);
            this.dropDown.setSingleLine(true);
            this.dropDown.setLines(1);
            this.dropDown.setMaxLines(1);
            this.dropDown.setEllipsize(TruncateAt.END);
            this.dropDown.setTextColor(Theme.getColor("actionBarDefaultTitle"));
            this.dropDown.setTypeface(AndroidUtilities.getTypeface(str));
            this.dropDown.setText(LocaleController.getString("ColorPickerMainColor", NUM));
            this.dropDownDrawable = context.getResources().getDrawable(NUM).mutate();
            this.dropDownDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("actionBarDefaultTitle"), Mode.MULTIPLY));
            this.dropDown.setCompoundDrawablesWithIntrinsicBounds(null, null, this.dropDownDrawable, null);
            this.dropDown.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
            this.dropDown.setPadding(0, 0, AndroidUtilities.dp(10.0f), 0);
            this.dropDownContainer.addView(this.dropDown, LayoutHelper.createFrame(-2, -2.0f, 16, 16.0f, 0.0f, 0.0f, 1.0f));
        } else {
            ThemeInfo themeInfo = this.applyingTheme;
            TL_theme tL_theme = themeInfo.info;
            CharSequence name = tL_theme != null ? tL_theme.title : themeInfo.getName();
            int lastIndexOf = name.lastIndexOf(".attheme");
            if (lastIndexOf >= 0) {
                name = name.substring(0, lastIndexOf);
            }
            this.actionBar2.setTitle(name);
            TL_theme tL_theme2 = this.applyingTheme.info;
            if (tL_theme2 != null) {
                int i2 = tL_theme2.installs_count;
                if (i2 > 0) {
                    this.actionBar2.setSubtitle(LocaleController.formatPluralString("ThemeInstallCount", i2));
                }
            }
            this.actionBar2.setSubtitle(LocaleController.formatDateOnline((System.currentTimeMillis() / 1000) - 3600));
        }
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
        RecyclerListView recyclerListView = this.listView2;
        int dp = AndroidUtilities.dp(4.0f);
        if (this.screenType == 1) {
            f = 16.0f;
        }
        recyclerListView.setPadding(0, dp, 0, AndroidUtilities.dp(f));
        this.listView2.setClipToPadding(false);
        this.listView2.setLayoutManager(new LinearLayoutManager(context2, 1, true));
        recyclerListView = this.listView2;
        if (LocaleController.isRTL) {
            i = 1;
        }
        recyclerListView.setVerticalScrollbarPosition(i);
        this.page2.addView(this.listView2, LayoutHelper.createFrame(-1, -1, 51));
        this.listView2.setOnScrollListener(new OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                ThemePreviewActivity.this.listView2.invalidateViews();
            }
        });
        this.page2.addView(this.actionBar2, LayoutHelper.createFrame(-1, -2.0f));
        this.listView2.setAdapter(this.messagesAdapter);
        this.linearLayout = new LinearLayout(context2) {
            private int[] loc = new int[2];
            private Rect paddings = new Rect();

            /* Access modifiers changed, original: protected */
            public boolean drawChild(Canvas canvas, View view, long j) {
                if (view == ThemePreviewActivity.this.colorPicker) {
                    ThemePreviewActivity.this.sheetDrawable.setBounds(view.getLeft() - this.paddings.left, (view.getTop() - this.paddings.top) - AndroidUtilities.dp(12.0f), view.getRight() + this.paddings.right, view.getBottom() + this.paddings.bottom);
                    ThemePreviewActivity.this.sheetDrawable.draw(canvas);
                }
                return super.drawChild(canvas, view, j);
            }

            public void invalidate() {
                super.invalidate();
                if (ThemePreviewActivity.this.page2 != null) {
                    ThemePreviewActivity.this.page2.invalidate();
                }
            }

            /* Access modifiers changed, original: protected */
            public void onDraw(Canvas canvas) {
                getLocationInWindow(this.loc);
                if (VERSION.SDK_INT < 21) {
                    int[] iArr = this.loc;
                    iArr[1] = iArr[1] - AndroidUtilities.statusBarHeight;
                }
                if (ThemePreviewActivity.this.actionBar2.getTranslationY() != ((float) this.loc[1])) {
                    ThemePreviewActivity.this.actionBar2.setTranslationY((float) (-this.loc[1]));
                    ThemePreviewActivity.this.page2.invalidate();
                }
                if (SystemClock.uptimeMillis() < ThemePreviewActivity.this.watchForKeyboardEndTime) {
                    invalidate();
                }
            }
        };
        this.linearLayout.setWillNotDraw(false);
        this.linearLayout.setOrientation(1);
        LinearLayout linearLayout = this.linearLayout;
        this.fragmentView = linearLayout;
        ViewTreeObserver viewTreeObserver = linearLayout.getViewTreeObserver();
        -$$Lambda$ThemePreviewActivity$det2jTWbQ4tLobUSesgzV5EXIuI -__lambda_themepreviewactivity_det2jtwbq4tlobusesgzv5exiui = new -$$Lambda$ThemePreviewActivity$det2jTWbQ4tLobUSesgzV5EXIuI(this);
        this.onGlobalLayoutListener = -__lambda_themepreviewactivity_det2jtwbq4tlobusesgzv5exiui;
        viewTreeObserver.addOnGlobalLayoutListener(-__lambda_themepreviewactivity_det2jtwbq4tlobusesgzv5exiui);
        this.viewPager = new ViewPager(context2);
        this.viewPager.addOnPageChangeListener(new OnPageChangeListener() {
            public void onPageScrollStateChanged(int i) {
            }

            public void onPageScrolled(int i, float f, int i2) {
            }

            public void onPageSelected(int i) {
                ThemePreviewActivity.this.dotsContainer.invalidate();
            }
        });
        this.viewPager.setAdapter(new PagerAdapter() {
            public int getItemPosition(Object obj) {
                return -1;
            }

            public boolean isViewFromObject(View view, Object obj) {
                return obj == view;
            }

            public int getCount() {
                return ThemePreviewActivity.this.screenType == 1 ? 1 : 2;
            }

            public Object instantiateItem(ViewGroup viewGroup, int i) {
                View access$3000 = i == 0 ? ThemePreviewActivity.this.page2 : ThemePreviewActivity.this.page1;
                viewGroup.addView(access$3000);
                return access$3000;
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
        AndroidUtilities.setViewPagerEdgeEffectColor(this.viewPager, Theme.getColor("actionBarDefault"));
        this.linearLayout.addView(this.viewPager, LayoutHelper.createLinear(-1, 0, 1.0f));
        if (this.screenType == 1) {
            this.colorPicker = new ColorPicker(context2, new -$$Lambda$ThemePreviewActivity$3CSXyaKKfzRGi7ekY0M7EIdLxs0(this));
            if (this.applyingTheme.isDark()) {
                this.colorPicker.setMinBrightness(-$$Lambda$ThemePreviewActivity$NvC5jo2MOz5ft-cz50J6KhSW2rY.INSTANCE);
            } else {
                this.colorPicker.setMaxBrightness(-$$Lambda$ThemePreviewActivity$x6lCBnVBm9tdSmRKQA7EQNMGem0.INSTANCE);
            }
            ThemeInfo themeInfo2 = this.applyingTheme;
            this.accentColor = themeInfo2.accentColor;
            this.myMessagesAccentColor = themeInfo2.myMessagesAccentColor;
            this.myMessagesGradientAccentColor = themeInfo2.myMessagesGradientAccentColor;
            this.backgroundColor = themeInfo2.backgroundOverrideColor;
            this.gradientToColor = themeInfo2.backgroundGradientOverrideColor;
            this.colorPicker.setType(1, hasChanges(1), false, false, false);
            this.colorPicker.setColor(this.applyingTheme.accentColor, 0);
            this.linearLayout.addView(this.colorPicker, LayoutHelper.createLinear(-1, 294));
        } else {
            View view = new View(context2);
            view.setBackgroundResource(NUM);
            this.linearLayout.addView(view, LayoutHelper.createLinear(-1, 3, 0, 0, -3, 0, 0));
            this.buttonsContainer = new FrameLayout(context2);
            this.buttonsContainer.setBackgroundColor(getButtonsColor("windowBackgroundWhite"));
            this.linearLayout.addView(this.buttonsContainer, LayoutHelper.createLinear(-1, 48));
            this.dotsContainer = new View(context2) {
                private Paint paint = new Paint(1);

                /* Access modifiers changed, original: protected */
                public void onDraw(Canvas canvas) {
                    int currentItem = ThemePreviewActivity.this.viewPager.getCurrentItem();
                    this.paint.setColor(ThemePreviewActivity.this.getButtonsColor("chat_fieldOverlayText"));
                    int i = 0;
                    while (i < 2) {
                        this.paint.setAlpha(i == currentItem ? 255 : 127);
                        canvas.drawCircle((float) AndroidUtilities.dp((float) ((i * 15) + 3)), (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(3.0f), this.paint);
                        i++;
                    }
                }
            };
            this.buttonsContainer.addView(this.dotsContainer, LayoutHelper.createFrame(22, 8, 17));
            this.cancelButton = new TextView(context2);
            this.cancelButton.setTextSize(1, 14.0f);
            this.cancelButton.setTextColor(getButtonsColor("chat_fieldOverlayText"));
            this.cancelButton.setGravity(17);
            this.cancelButton.setBackgroundDrawable(Theme.createSelectorDrawable(NUM, 0));
            this.cancelButton.setPadding(AndroidUtilities.dp(29.0f), 0, AndroidUtilities.dp(29.0f), 0);
            this.cancelButton.setText(LocaleController.getString("Cancel", NUM).toUpperCase());
            this.cancelButton.setTypeface(AndroidUtilities.getTypeface(str));
            this.buttonsContainer.addView(this.cancelButton, LayoutHelper.createFrame(-2, -1, 51));
            this.cancelButton.setOnClickListener(new -$$Lambda$ThemePreviewActivity$MlBgGb3qPcS-NLKZdUFRg4IQ2R8(this));
            this.doneButton = new TextView(context2);
            this.doneButton.setTextSize(1, 14.0f);
            this.doneButton.setTextColor(getButtonsColor("chat_fieldOverlayText"));
            this.doneButton.setGravity(17);
            this.doneButton.setBackgroundDrawable(Theme.createSelectorDrawable(NUM, 0));
            this.doneButton.setPadding(AndroidUtilities.dp(29.0f), 0, AndroidUtilities.dp(29.0f), 0);
            this.doneButton.setText(LocaleController.getString("ApplyTheme", NUM).toUpperCase());
            this.doneButton.setTypeface(AndroidUtilities.getTypeface(str));
            this.buttonsContainer.addView(this.doneButton, LayoutHelper.createFrame(-2, -1, 53));
            this.doneButton.setOnClickListener(new -$$Lambda$ThemePreviewActivity$tJEe9bCoUTB_mlEHzuORTkBRE2c(this));
        }
        this.themeDescriptions = getThemeDescriptionsInternal();
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$1$ThemePreviewActivity(View view) {
        this.dropDownContainer.toggleSubMenu();
    }

    public /* synthetic */ void lambda$createView$2$ThemePreviewActivity() {
        this.watchForKeyboardEndTime = SystemClock.uptimeMillis() + 1500;
        this.linearLayout.invalidate();
    }

    public /* synthetic */ void lambda$createView$5$ThemePreviewActivity(View view) {
        cancelThemeApply();
    }

    public /* synthetic */ void lambda$createView$6$ThemePreviewActivity(View view) {
        this.parentLayout.rebuildAllFragmentViews(false, false);
        File file = new File(this.applyingTheme.pathToFile);
        ThemeInfo themeInfo = this.applyingTheme;
        Theme.applyThemeFile(file, themeInfo.name, themeInfo.info, false);
        getMessagesController().saveTheme(this.applyingTheme, false, false);
        finishFragment();
    }

    /* JADX WARNING: Missing block: B:11:0x001e, code skipped:
            if (r4 == r2) goto L_0x0021;
     */
    /* JADX WARNING: Missing block: B:20:0x0030, code skipped:
            if (r6 != r5.accentColor) goto L_0x0035;
     */
    private boolean hasChanges(int r6) {
        /*
        r5 = this;
        r0 = 1;
        if (r6 == r0) goto L_0x0006;
    L_0x0003:
        r1 = 2;
        if (r6 != r1) goto L_0x0021;
    L_0x0006:
        r1 = "chat_wallpaper";
        r1 = org.telegram.ui.ActionBar.Theme.getDefaultAccentColor(r1);
        r2 = "chat_wallpaper_gradient_to";
        r2 = org.telegram.ui.ActionBar.Theme.getDefaultAccentColor(r2);
        r3 = r5.backgroundColor;
        if (r3 != 0) goto L_0x0017;
    L_0x0016:
        r3 = r1;
    L_0x0017:
        r4 = r5.gradientToColor;
        if (r4 != 0) goto L_0x001c;
    L_0x001b:
        r4 = r2;
    L_0x001c:
        if (r3 != r1) goto L_0x0035;
    L_0x001e:
        if (r4 == r2) goto L_0x0021;
    L_0x0020:
        goto L_0x0035;
    L_0x0021:
        if (r6 == r0) goto L_0x0026;
    L_0x0023:
        r1 = 3;
        if (r6 != r1) goto L_0x0033;
    L_0x0026:
        r6 = r5.myMessagesGradientAccentColor;
        if (r6 != 0) goto L_0x0035;
    L_0x002a:
        r6 = r5.myMessagesAccentColor;
        if (r6 == 0) goto L_0x0033;
    L_0x002e:
        r1 = r5.accentColor;
        if (r6 == r1) goto L_0x0033;
    L_0x0032:
        goto L_0x0035;
    L_0x0033:
        r6 = 0;
        return r6;
    L_0x0035:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemePreviewActivity.hasChanges(int):boolean");
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
        LinearLayout linearLayout = this.linearLayout;
        if (!(linearLayout == null || this.onGlobalLayoutListener == null)) {
            linearLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this.onGlobalLayoutListener);
        }
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
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
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
        if (this.screenType != 1) {
            this.parentLayout.rebuildAllFragmentViews(false, false);
        }
        if (this.deleteOnCancel) {
            ThemeInfo themeInfo = this.applyingTheme;
            if (!(themeInfo.pathToFile == null || Theme.isThemeInstalled(themeInfo))) {
                new File(this.applyingTheme.pathToFile).delete();
            }
        }
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
        } else if (i == NotificationCenter.didSetNewWallpapper) {
            SizeNotifierFrameLayout sizeNotifierFrameLayout = this.page2;
            if (sizeNotifierFrameLayout != null) {
                sizeNotifierFrameLayout.setBackgroundImage(Theme.getCachedWallpaper(), Theme.isWallpaperMotion());
            }
        }
    }

    private void cancelThemeApply() {
        Theme.applyPreviousTheme();
        if (this.screenType != 1) {
            this.parentLayout.rebuildAllFragmentViews(false, false);
        }
        if (this.deleteOnCancel) {
            ThemeInfo themeInfo = this.applyingTheme;
            if (!(themeInfo.pathToFile == null || Theme.isThemeInstalled(themeInfo))) {
                new File(this.applyingTheme.pathToFile).delete();
            }
        }
        finishFragment();
    }

    private int getButtonsColor(String str) {
        return this.useDefaultThemeForButtons ? Theme.getDefaultColor(str) : Theme.getColor(str);
    }

    private void scheduleApplyColor(int i, int i2, boolean z) {
        if (i2 == -1) {
            ColorPicker colorPicker;
            i = this.colorType;
            if (i == 1 || i == 2) {
                String str = "chat_wallpaper";
                int defaultAccentColor = Theme.getDefaultAccentColor(str);
                String str2 = "chat_wallpaper_gradient_to";
                int defaultAccentColor2 = Theme.getDefaultAccentColor(str2);
                this.backgroundColor = 0;
                this.gradientToColor = 0;
                Theme.setColor(str, defaultAccentColor, false);
                Theme.setColor(str2, defaultAccentColor2, defaultAccentColor2 == 0);
                Theme.applyCurrentThemeBackground(0, 0);
                if (this.colorType == 2) {
                    colorPicker = this.colorPicker;
                    i2 = this.gradientToColor;
                    if (i2 == 0) {
                        i2 = defaultAccentColor2;
                    }
                    colorPicker.setColor(i2, 1);
                    colorPicker = this.colorPicker;
                    i2 = this.backgroundColor;
                    if (i2 == 0) {
                        i2 = defaultAccentColor;
                    }
                    colorPicker.setColor(i2, 0);
                }
            }
            i = this.colorType;
            if (i == 1 || i == 3) {
                this.myMessagesAccentColor = 0;
                this.myMessagesGradientAccentColor = 0;
                Theme.applyCurrentThemeMyMessagesAccent(this.myMessagesAccentColor, this.myMessagesGradientAccentColor);
                if (this.colorType == 3) {
                    this.colorPicker.setColor(this.myMessagesGradientAccentColor, 1);
                    colorPicker = this.colorPicker;
                    i2 = this.myMessagesAccentColor;
                    if (i2 == 0) {
                        i2 = this.accentColor;
                    }
                    colorPicker.setColor(i2, 0);
                }
            }
            this.listView2.invalidateViews();
            return;
        }
        this.lastPickedColor = i;
        this.lastPickedColorNum = i2;
        if (z) {
            this.applyColorAction.run();
        } else if (!this.applyColorScheduled) {
            this.applyColorScheduled = true;
            this.fragmentView.postDelayed(this.applyColorAction, 16);
        }
    }

    private void applyColor(int i, int i2) {
        i2 = this.colorType;
        boolean z = true;
        if (i2 == 1) {
            this.accentColor = i;
            Theme.applyCurrentThemeAccent(i);
        } else if (i2 == 2) {
            if (this.lastPickedColorNum == 0) {
                this.backgroundColor = i;
                Theme.setColor("chat_wallpaper", i, false);
            } else {
                this.gradientToColor = i;
                if (i != 0) {
                    z = false;
                }
                Theme.setColor("chat_wallpaper_gradient_to", i, z);
            }
            Theme.applyCurrentThemeBackground(this.backgroundColor, this.gradientToColor);
            this.colorPicker.setHasChanges(hasChanges(this.colorType));
        } else if (i2 == 3) {
            if (this.lastPickedColorNum == 0) {
                this.myMessagesAccentColor = i;
            } else {
                this.myMessagesGradientAccentColor = i;
            }
            Theme.applyCurrentThemeMyMessagesAccent(this.myMessagesAccentColor, this.myMessagesGradientAccentColor);
            this.listView2.invalidateViews();
            this.colorPicker.setHasChanges(hasChanges(this.colorType));
        }
        i = this.themeDescriptions.size();
        for (i2 = 0; i2 < i; i2++) {
            ThemeDescription themeDescription = (ThemeDescription) this.themeDescriptions.get(i2);
            themeDescription.setColor(Theme.getColor(themeDescription.getCurrentKey()), false, false);
        }
        this.listView.invalidateViews();
        this.listView2.invalidateViews();
        View view = this.dotsContainer;
        if (view != null) {
            view.invalidate();
        }
    }

    private List<ThemeDescription> getThemeDescriptionsInternal() {
        -$$Lambda$ThemePreviewActivity$fj0to6NnT1RWaoKt2E0snoBDjyo -__lambda_themepreviewactivity_fj0to6nnt1rwaokt2e0snobdjyo = new -$$Lambda$ThemePreviewActivity$fj0to6NnT1RWaoKt2E0snoBDjyo(this);
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.viewPager, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"));
        View view = this.fragmentView;
        View view2 = view;
        arrayList.add(new ThemeDescription(view2, ThemeDescription.FLAG_IMAGECOLOR, null, null, new Drawable[]{this.sheetDrawable}, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, "actionBarDefaultSearch"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, "actionBarDefaultSearchPlaceholder"));
        arrayList.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_AB_SUBTITLECOLOR, null, null, null, null, "actionBarDefaultSubtitle"));
        arrayList.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"));
        -$$Lambda$ThemePreviewActivity$fj0to6NnT1RWaoKt2E0snoBDjyo -__lambda_themepreviewactivity_fj0to6nnt1rwaokt2e0snobdjyo2 = -__lambda_themepreviewactivity_fj0to6nnt1rwaokt2e0snobdjyo;
        arrayList.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, -__lambda_themepreviewactivity_fj0to6nnt1rwaokt2e0snobdjyo2, "actionBarDefaultSubmenuBackground"));
        arrayList.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, -__lambda_themepreviewactivity_fj0to6nnt1rwaokt2e0snobdjyo2, "actionBarDefaultSubmenuItem"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView2, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "chats_actionIcon"));
        arrayList.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "chats_actionBackground"));
        arrayList.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "chats_actionPressedBackground"));
        if (!this.useDefaultThemeForButtons) {
            arrayList.add(new ThemeDescription(this.buttonsContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
            arrayList.add(new ThemeDescription(this.cancelButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "chat_fieldOverlayText"));
            arrayList.add(new ThemeDescription(this.doneButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "chat_fieldOverlayText"));
        }
        ColorPicker colorPicker = this.colorPicker;
        if (colorPicker != null) {
            colorPicker.provideThemeDescriptions(arrayList);
        }
        return arrayList;
    }

    public /* synthetic */ void lambda$getThemeDescriptionsInternal$7$ThemePreviewActivity() {
        ActionBarMenuItem actionBarMenuItem = this.dropDownContainer;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.redrawPopup(Theme.getColor("actionBarDefaultSubmenuBackground"));
            this.dropDownContainer.setPopupItemsColor(Theme.getColor("actionBarDefaultSubmenuItem"), false);
        }
    }
}
