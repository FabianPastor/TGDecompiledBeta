package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build.VERSION;
import android.os.SystemClock;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.style.CharacterStyle;
import android.util.Property;
import android.view.MotionEvent;
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
import android.widget.TextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ItemDecoration;
import androidx.recyclerview.widget.RecyclerView.LayoutParams;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.RecyclerView.State;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.DownloadController.FileDownloadProgressListener;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController.SearchImage;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_account_getWallPaper;
import org.telegram.tgnet.TLRPC.TL_account_getWallPapers;
import org.telegram.tgnet.TLRPC.TL_account_wallPapers;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_documentAttributeFilename;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC.TL_inputWallPaperSlug;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageEntityTextUrl;
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
import org.telegram.tgnet.TLRPC.TL_wallPaper;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.WallPaper;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.MenuDrawable;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.Theme.ThemeAccent;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate;
import org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate.-CC;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.DialogCell.CustomDialog;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.PatternCell;
import org.telegram.ui.Cells.PatternCell.PatternCellDelegate;
import org.telegram.ui.Cells.TextSelectionHelper.ChatListTextSelectionHelper;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.BackgroundGradientDrawable.Disposable;
import org.telegram.ui.Components.BackgroundGradientDrawable.ListenerAdapter;
import org.telegram.ui.Components.BackgroundGradientDrawable.Sizes;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ColorPicker;
import org.telegram.ui.Components.ColorPicker.ColorPickerDelegate;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgress2;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.SeekBarView;
import org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.WallpaperCheckBoxView;
import org.telegram.ui.Components.WallpaperParallaxEffect;
import org.telegram.ui.WallpapersListActivity.ColorWallpaper;
import org.telegram.ui.WallpapersListActivity.FileWallpaper;

public class ThemePreviewActivity extends BaseFragment implements FileDownloadProgressListener, NotificationCenterDelegate {
    public static final int SCREEN_TYPE_ACCENT_COLOR = 1;
    public static final int SCREEN_TYPE_CHANGE_BACKGROUND = 2;
    public static final int SCREEN_TYPE_PREVIEW = 0;
    private int TAG;
    private ThemeAccent accent;
    private ActionBar actionBar2;
    private Runnable applyColorAction;
    private boolean applyColorScheduled;
    private ThemeInfo applyingTheme;
    private int backgroundColor;
    private int backgroundGradientColor;
    private Disposable backgroundGradientDisposable;
    private BackupImageView backgroundImage;
    private int backgroundRotation;
    private int backupAccentColor;
    private long backupBackgroundGradientOverrideColor;
    private long backupBackgroundOverrideColor;
    private int backupBackgroundRotation;
    private int backupMyMessagesAccentColor;
    private int backupMyMessagesGradientAccentColor;
    private final Mode blendMode;
    private Bitmap blurredBitmap;
    private FrameLayout bottomOverlayChat;
    private TextView bottomOverlayChatText;
    private FrameLayout buttonsContainer;
    private TextView cancelButton;
    private WallpaperCheckBoxView[] checkBoxView;
    private ColorPicker colorPicker;
    private int colorType;
    private float currentIntensity;
    private Object currentWallpaper;
    private Bitmap currentWallpaperBitmap;
    private WallpaperActivityDelegate delegate;
    private boolean deleteOnCancel;
    private DialogsAdapter dialogsAdapter;
    private TextView doneButton;
    private View dotsContainer;
    private TextView dropDown;
    private ActionBarMenuItem dropDownContainer;
    private boolean editingTheme;
    private ImageView floatingButton;
    private FrameLayout frameLayout;
    private String imageFilter;
    private HeaderCell intensityCell;
    private SeekBarView intensitySeekBar;
    private boolean isBlurred;
    private boolean isMotion;
    private int lastPickedColor;
    private int lastPickedColorNum;
    private TL_wallPaper lastSelectedPattern;
    private RecyclerListView listView;
    private RecyclerListView listView2;
    private String loadingFile;
    private File loadingFileObject;
    private PhotoSize loadingSize;
    private int maxWallpaperSize;
    private MessagesAdapter messagesAdapter;
    private AnimatorSet motionAnimation;
    private boolean nightTheme;
    private OnGlobalLayoutListener onGlobalLayoutListener;
    private Bitmap originalBitmap;
    private FrameLayout page1;
    private FrameLayout page2;
    private WallpaperParallaxEffect parallaxEffect;
    private float parallaxScale;
    private int patternColor;
    private FrameLayout[] patternLayout;
    private AnimatorSet patternViewAnimation;
    private ArrayList<Object> patterns;
    private PatternsAdapter patternsAdapter;
    private FrameLayout[] patternsButtonsContainer;
    private TextView[] patternsCancelButton;
    private LinearLayoutManager patternsLayoutManager;
    private RecyclerListView patternsListView;
    private TextView[] patternsSaveButton;
    private int previousBackgroundColor;
    private int previousBackgroundGradientColor;
    private int previousBackgroundRotation;
    private float previousIntensity;
    private TL_wallPaper previousSelectedPattern;
    private boolean progressVisible;
    private RadialProgress2 radialProgress;
    private boolean removeBackgroundOverride;
    private FrameLayout saveButtonsContainer;
    private ActionBarMenuItem saveItem;
    private final int screenType;
    private TL_wallPaper selectedPattern;
    private Drawable sheetDrawable;
    private List<ThemeDescription> themeDescriptions;
    private boolean useDefaultThemeForButtons;
    private ViewPager viewPager;
    private long watchForKeyboardEndTime;

    public interface WallpaperActivityDelegate {
        void didSetNewBackground();
    }

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
            boolean z = this.this$0.screenType == 0 && Utilities.random.nextInt(100) <= 1;
            this.showSecretMessages = z;
            this.mContext = context;
            this.messages = new ArrayList();
            int currentTimeMillis = ((int) (System.currentTimeMillis() / 1000)) - 3600;
            TL_message tL_message2;
            int i;
            MessageObject messageObject;
            if (themePreviewActivity.screenType == 2) {
                tL_message2 = new TL_message();
                if (themePreviewActivity.currentWallpaper instanceof ColorWallpaper) {
                    tL_message2.message = LocaleController.getString("BackgroundColorSinglePreviewLine2", NUM);
                } else {
                    tL_message2.message = LocaleController.getString("BackgroundPreviewLine2", NUM);
                }
                i = currentTimeMillis + 60;
                tL_message2.date = i;
                tL_message2.dialog_id = 1;
                tL_message2.flags = 259;
                tL_message2.from_id = UserConfig.getInstance(themePreviewActivity.currentAccount).getClientUserId();
                tL_message2.id = 1;
                tL_message2.media = new TL_messageMediaEmpty();
                tL_message2.out = true;
                tL_message2.to_id = new TL_peerUser();
                tL_message2.to_id.user_id = 0;
                MessageObject messageObject2 = new MessageObject(themePreviewActivity.currentAccount, tL_message2, true);
                messageObject2.eventId = 1;
                messageObject2.resetLayout();
                this.messages.add(messageObject2);
                tL_message2 = new TL_message();
                if (themePreviewActivity.currentWallpaper instanceof ColorWallpaper) {
                    tL_message2.message = LocaleController.getString("BackgroundColorSinglePreviewLine1", NUM);
                } else {
                    tL_message2.message = LocaleController.getString("BackgroundPreviewLine1", NUM);
                }
                tL_message2.date = i;
                tL_message2.dialog_id = 1;
                tL_message2.flags = 265;
                tL_message2.from_id = 0;
                tL_message2.id = 1;
                tL_message2.media = new TL_messageMediaEmpty();
                tL_message2.out = false;
                tL_message2.to_id = new TL_peerUser();
                tL_message2.to_id.user_id = UserConfig.getInstance(themePreviewActivity.currentAccount).getClientUserId();
                messageObject = new MessageObject(themePreviewActivity.currentAccount, tL_message2, true);
                messageObject.eventId = 1;
                messageObject.resetLayout();
                this.messages.add(messageObject);
            } else {
                String str = "audio/ogg";
                int indexOf;
                MessageObject messageObject3;
                MessageMedia messageMedia;
                Document document;
                TL_documentAttributeAudio tL_documentAttributeAudio;
                if (themePreviewActivity.screenType == 1) {
                    tL_message2 = new TL_message();
                    tL_message2.media = new TL_messageMediaDocument();
                    tL_message2.media.document = new TL_document();
                    Document document2 = tL_message2.media.document;
                    document2.mime_type = "audio/mp3";
                    document2.file_reference = new byte[0];
                    document2.id = -2147483648L;
                    document2.size = 2621440;
                    document2.dc_id = Integer.MIN_VALUE;
                    TL_documentAttributeFilename tL_documentAttributeFilename = new TL_documentAttributeFilename();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(LocaleController.getString("NewThemePreviewReply2", NUM));
                    stringBuilder.append(".mp3");
                    tL_documentAttributeFilename.file_name = stringBuilder.toString();
                    tL_message2.media.document.attributes.add(tL_documentAttributeFilename);
                    int i2 = currentTimeMillis + 60;
                    tL_message2.date = i2;
                    tL_message2.dialog_id = 1;
                    tL_message2.flags = 259;
                    tL_message2.from_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                    tL_message2.id = 1;
                    tL_message2.out = true;
                    tL_message2.to_id = new TL_peerUser();
                    tL_message2.to_id.user_id = 0;
                    MessageObject messageObject4 = new MessageObject(UserConfig.selectedAccount, tL_message2, true);
                    tL_message2 = new TL_message();
                    String string = LocaleController.getString("NewThemePreviewLine3", NUM);
                    StringBuilder stringBuilder2 = new StringBuilder(string);
                    indexOf = string.indexOf(42);
                    i = string.lastIndexOf(42);
                    if (!(indexOf == -1 || i == -1)) {
                        stringBuilder2.replace(i, i + 1, "");
                        stringBuilder2.replace(indexOf, indexOf + 1, "");
                        TL_messageEntityTextUrl tL_messageEntityTextUrl = new TL_messageEntityTextUrl();
                        tL_messageEntityTextUrl.offset = indexOf;
                        tL_messageEntityTextUrl.length = (i - indexOf) - 1;
                        tL_messageEntityTextUrl.url = "https://telegram.org";
                        tL_message2.entities.add(tL_messageEntityTextUrl);
                    }
                    tL_message2.message = stringBuilder2.toString();
                    tL_message2.date = currentTimeMillis + 960;
                    tL_message2.dialog_id = 1;
                    tL_message2.flags = 259;
                    tL_message2.from_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                    tL_message2.id = 1;
                    tL_message2.media = new TL_messageMediaEmpty();
                    tL_message2.out = true;
                    tL_message2.to_id = new TL_peerUser();
                    tL_message2.to_id.user_id = 0;
                    MessageObject messageObject5 = new MessageObject(UserConfig.selectedAccount, tL_message2, true);
                    messageObject5.resetLayout();
                    messageObject5.eventId = 1;
                    this.messages.add(messageObject5);
                    tL_message = new TL_message();
                    tL_message.message = LocaleController.getString("NewThemePreviewLine1", NUM);
                    tL_message.date = i2;
                    tL_message.dialog_id = 1;
                    tL_message.flags = 265;
                    tL_message.from_id = 0;
                    tL_message.id = 1;
                    tL_message.reply_to_msg_id = 5;
                    tL_message.media = new TL_messageMediaEmpty();
                    tL_message.out = false;
                    tL_message.to_id = new TL_peerUser();
                    tL_message.to_id.user_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                    messageObject3 = new MessageObject(UserConfig.selectedAccount, tL_message, true);
                    messageObject3.customReplyName = LocaleController.getString("NewThemePreviewName", NUM);
                    messageObject3.eventId = 1;
                    messageObject3.resetLayout();
                    messageObject3.replyMessageObject = messageObject4;
                    this.messages.add(messageObject3);
                    this.messages.add(messageObject4);
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
                    messageObject3 = new MessageObject(themePreviewActivity.currentAccount, tL_message, true);
                    messageObject3.audioProgressSec = 1;
                    messageObject3.audioProgress = 0.3f;
                    messageObject3.useCustomPhoto = true;
                    this.messages.add(messageObject3);
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
                    TL_message tL_message3 = new TL_message();
                    tL_message3.message = "Guess why Half-Life 3 was never released.";
                    indexOf = currentTimeMillis + 960;
                    tL_message3.date = indexOf;
                    tL_message3.dialog_id = -1;
                    tL_message3.flags = 259;
                    tL_message3.id = NUM;
                    tL_message3.media = new TL_messageMediaEmpty();
                    tL_message3.out = false;
                    tL_message3.to_id = new TL_peerChat();
                    tL_message3.to_id.chat_id = 1;
                    tL_message3.from_id = tL_user2.id;
                    this.messages.add(new MessageObject(themePreviewActivity.currentAccount, tL_message3, true));
                    tL_message3 = new TL_message();
                    tL_message3.message = "No.\nAnd every unnecessary ping of the dev delays the release for 10 days.\nEvery request for ETA delays the release for 2 weeks.";
                    tL_message3.date = indexOf;
                    tL_message3.dialog_id = -1;
                    tL_message3.flags = 259;
                    tL_message3.id = 1;
                    tL_message3.media = new TL_messageMediaEmpty();
                    tL_message3.out = false;
                    tL_message3.to_id = new TL_peerChat();
                    tL_message3.to_id.chat_id = 1;
                    tL_message3.from_id = tL_user2.id;
                    this.messages.add(new MessageObject(themePreviewActivity.currentAccount, tL_message3, true));
                    tL_message2 = new TL_message();
                    tL_message2.message = "Is source code for Android coming anytime soon?";
                    tL_message2.date = currentTimeMillis + 600;
                    tL_message2.dialog_id = -1;
                    tL_message2.flags = 259;
                    tL_message2.id = 1;
                    tL_message2.media = new TL_messageMediaEmpty();
                    tL_message2.out = false;
                    tL_message2.to_id = new TL_peerChat();
                    tL_message2.to_id.chat_id = 1;
                    tL_message2.from_id = tL_user.id;
                    this.messages.add(new MessageObject(themePreviewActivity.currentAccount, tL_message2, true));
                } else {
                    tL_message = new TL_message();
                    tL_message.message = LocaleController.getString("ThemePreviewLine1", NUM);
                    int i3 = currentTimeMillis + 60;
                    tL_message.date = i3;
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
                    Document document3 = tL_message.media.document;
                    document3.mime_type = "audio/mp4";
                    document3.file_reference = new byte[0];
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
                    tL_message.date = i3;
                    tL_message.dialog_id = 1;
                    tL_message.flags = 265;
                    tL_message.from_id = 0;
                    tL_message.id = 1;
                    tL_message.reply_to_msg_id = 5;
                    tL_message.media = new TL_messageMediaEmpty();
                    tL_message.out = false;
                    tL_message.to_id = new TL_peerUser();
                    tL_message.to_id.user_id = UserConfig.getInstance(themePreviewActivity.currentAccount).getClientUserId();
                    messageObject3 = new MessageObject(themePreviewActivity.currentAccount, tL_message, true);
                    messageObject3.customReplyName = LocaleController.getString("ThemePreviewLine3Reply", NUM);
                    messageObject3.replyMessageObject = messageObject;
                    this.messages.add(messageObject3);
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
                    messageObject3 = new MessageObject(themePreviewActivity.currentAccount, tL_message, true);
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
                    messageObject3 = new MessageObject(themePreviewActivity.currentAccount, tL_message, true);
                    messageObject3.useCustomPhoto = true;
                    this.messages.add(messageObject3);
                }
            }
            tL_message = new TL_message();
            tL_message.message = LocaleController.formatDateChat((long) currentTimeMillis);
            tL_message.id = 0;
            tL_message.date = currentTimeMillis;
            MessageObject messageObject6 = new MessageObject(themePreviewActivity.currentAccount, tL_message, false);
            messageObject6.type = 10;
            messageObject6.contentType = 1;
            messageObject6.isDateObject = true;
            this.messages.add(messageObject6);
        }

        private boolean hasButtons() {
            if (this.this$0.buttonsContainer != null) {
                if (this.this$0.screenType == 2) {
                    return true;
                }
                if (this.this$0.screenType == 1 && this.this$0.colorType == 2) {
                    return true;
                }
            }
            return false;
        }

        public int getItemCount() {
            int size = this.messages.size();
            return hasButtons() ? size + 1 : size;
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

                    public /* synthetic */ ChatListTextSelectionHelper getTextSelectionHelper() {
                        return -CC.$default$getTextSelectionHelper(this);
                    }

                    public /* synthetic */ boolean hasSelectedMessages() {
                        return -CC.$default$hasSelectedMessages(this);
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
            } else if (i == 2) {
                if (this.this$0.buttonsContainer.getParent() != null) {
                    ((ViewGroup) this.this$0.buttonsContainer.getParent()).removeView(this.this$0.buttonsContainer);
                }
                chatMessageCell = new FrameLayout(this.mContext) {
                    /* Access modifiers changed, original: protected */
                    public void onMeasure(int i, int i2) {
                        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0f), NUM));
                    }
                };
                chatMessageCell.addView(this.this$0.buttonsContainer, LayoutHelper.createFrame(-2, 34, 17));
            } else {
                chatMessageCell = null;
            }
            chatMessageCell.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(chatMessageCell);
        }

        /* JADX WARNING: Removed duplicated region for block: B:19:0x0068  */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r10, int r11) {
            /*
            r9 = this;
            r0 = r10.getItemViewType();
            r1 = 2;
            if (r0 == r1) goto L_0x00ac;
        L_0x0007:
            r0 = r9.hasButtons();
            if (r0 == 0) goto L_0x000f;
        L_0x000d:
            r11 = r11 + -1;
        L_0x000f:
            r0 = r9.messages;
            r0 = r0.get(r11);
            r0 = (org.telegram.messenger.MessageObject) r0;
            r1 = r10.itemView;
            r2 = r1 instanceof org.telegram.ui.Cells.ChatMessageCell;
            if (r2 == 0) goto L_0x009e;
        L_0x001d:
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
            if (r7 != 0) goto L_0x0061;
        L_0x0038:
            r7 = r10.getItemViewType();
            if (r4 != r7) goto L_0x0061;
        L_0x003e:
            r4 = r9.messages;
            r3 = r4.get(r3);
            r3 = (org.telegram.messenger.MessageObject) r3;
            r4 = r3.isOutOwner();
            r7 = r0.isOutOwner();
            if (r4 != r7) goto L_0x0061;
        L_0x0050:
            r3 = r3.messageOwner;
            r3 = r3.date;
            r4 = r0.messageOwner;
            r4 = r4.date;
            r3 = r3 - r4;
            r3 = java.lang.Math.abs(r3);
            if (r3 > r8) goto L_0x0061;
        L_0x005f:
            r3 = 1;
            goto L_0x0062;
        L_0x0061:
            r3 = 0;
        L_0x0062:
            r10 = r10.getItemViewType();
            if (r6 != r10) goto L_0x0092;
        L_0x0068:
            r10 = r9.messages;
            r10 = r10.get(r11);
            r10 = (org.telegram.messenger.MessageObject) r10;
            r11 = r10.messageOwner;
            r11 = r11.reply_markup;
            r11 = r11 instanceof org.telegram.tgnet.TLRPC.TL_replyInlineMarkup;
            if (r11 != 0) goto L_0x0092;
        L_0x0078:
            r11 = r10.isOutOwner();
            r4 = r0.isOutOwner();
            if (r11 != r4) goto L_0x0092;
        L_0x0082:
            r10 = r10.messageOwner;
            r10 = r10.date;
            r11 = r0.messageOwner;
            r11 = r11.date;
            r10 = r10 - r11;
            r10 = java.lang.Math.abs(r10);
            if (r10 > r8) goto L_0x0092;
        L_0x0091:
            r2 = 1;
        L_0x0092:
            r10 = r9.showSecretMessages;
            r1.isChat = r10;
            r1.setFullyDraw(r5);
            r10 = 0;
            r1.setMessageObject(r0, r10, r3, r2);
            goto L_0x00ac;
        L_0x009e:
            r10 = r1 instanceof org.telegram.ui.Cells.ChatActionCell;
            if (r10 == 0) goto L_0x00ac;
        L_0x00a2:
            r1 = (org.telegram.ui.Cells.ChatActionCell) r1;
            r1.setMessageObject(r0);
            r10 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            r1.setAlpha(r10);
        L_0x00ac:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemePreviewActivity$MessagesAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public int getItemViewType(int i) {
            if (hasButtons()) {
                if (i == 0) {
                    return 2;
                }
                i--;
            }
            return (i < 0 || i >= this.messages.size()) ? 4 : ((MessageObject) this.messages.get(i)).contentType;
        }
    }

    private class PatternsAdapter extends SelectionAdapter {
        private Context mContext;

        public int getItemViewType(int i) {
            return 0;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return false;
        }

        public PatternsAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return ThemePreviewActivity.this.patterns != null ? ThemePreviewActivity.this.patterns.size() : 0;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new Holder(new PatternCell(this.mContext, ThemePreviewActivity.this.maxWallpaperSize, new PatternCellDelegate() {
                public TL_wallPaper getSelectedPattern() {
                    return ThemePreviewActivity.this.selectedPattern;
                }

                public int getPatternColor() {
                    return ThemePreviewActivity.this.patternColor;
                }

                public int getBackgroundColor() {
                    if (ThemePreviewActivity.this.screenType == 2) {
                        return ThemePreviewActivity.this.backgroundColor;
                    }
                    int defaultAccentColor = Theme.getDefaultAccentColor("chat_wallpaper");
                    int i = (int) ThemePreviewActivity.this.accent.backgroundOverrideColor;
                    if (i != 0) {
                        defaultAccentColor = i;
                    }
                    return defaultAccentColor;
                }

                public int getBackgroundGradientColor() {
                    if (ThemePreviewActivity.this.screenType == 2) {
                        return ThemePreviewActivity.this.backgroundGradientColor;
                    }
                    int defaultAccentColor = Theme.getDefaultAccentColor("chat_wallpaper_gradient_to");
                    int i = (int) ThemePreviewActivity.this.accent.backgroundGradientOverrideColor;
                    if (i != 0) {
                        defaultAccentColor = i;
                    }
                    return defaultAccentColor;
                }

                public int getBackgroundGradientAngle() {
                    if (ThemePreviewActivity.this.screenType == 2) {
                        return ThemePreviewActivity.this.backgroundRotation;
                    }
                    return ThemePreviewActivity.this.accent.backgroundRotation;
                }
            }));
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            PatternCell patternCell = (PatternCell) viewHolder.itemView;
            patternCell.setPattern((TL_wallPaper) ThemePreviewActivity.this.patterns.get(i));
            patternCell.getImageReceiver().setColorFilter(new PorterDuffColorFilter(ThemePreviewActivity.this.patternColor, ThemePreviewActivity.this.blendMode));
        }
    }

    public void onProgressUpload(String str, float f, boolean z) {
    }

    public /* synthetic */ void lambda$new$0$ThemePreviewActivity() {
        this.applyColorScheduled = false;
        applyColor(this.lastPickedColor, this.lastPickedColorNum);
    }

    public ThemePreviewActivity(Object obj, Bitmap bitmap) {
        this.useDefaultThemeForButtons = true;
        this.colorType = 1;
        this.applyColorAction = new -$$Lambda$ThemePreviewActivity$NnNC9ivczXrgLuuk8XD7kgj3Rtc(this);
        this.patternLayout = new FrameLayout[2];
        this.patternsCancelButton = new TextView[2];
        this.patternsSaveButton = new TextView[2];
        this.patternsButtonsContainer = new FrameLayout[2];
        this.currentIntensity = 0.5f;
        this.blendMode = Mode.SRC_IN;
        this.parallaxScale = 1.0f;
        this.loadingFile = null;
        this.loadingFileObject = null;
        this.loadingSize = null;
        this.imageFilter = "640_360";
        this.maxWallpaperSize = 1920;
        this.screenType = 2;
        this.currentWallpaper = obj;
        this.currentWallpaperBitmap = bitmap;
        obj = this.currentWallpaper;
        if (obj instanceof ColorWallpaper) {
            ColorWallpaper colorWallpaper = (ColorWallpaper) obj;
            this.isMotion = colorWallpaper.motion;
            this.selectedPattern = colorWallpaper.pattern;
            if (this.selectedPattern != null) {
                this.currentIntensity = colorWallpaper.intensity;
            }
        }
    }

    public ThemePreviewActivity(ThemeInfo themeInfo) {
        this(themeInfo, false, 0, false, false);
    }

    public ThemePreviewActivity(ThemeInfo themeInfo, boolean z, int i, boolean z2, boolean z3) {
        ThemeAccent themeAccent;
        this.useDefaultThemeForButtons = true;
        this.colorType = 1;
        this.applyColorAction = new -$$Lambda$ThemePreviewActivity$NnNC9ivczXrgLuuk8XD7kgj3Rtc(this);
        this.patternLayout = new FrameLayout[2];
        this.patternsCancelButton = new TextView[2];
        this.patternsSaveButton = new TextView[2];
        this.patternsButtonsContainer = new FrameLayout[2];
        this.currentIntensity = 0.5f;
        this.blendMode = Mode.SRC_IN;
        this.parallaxScale = 1.0f;
        this.loadingFile = null;
        this.loadingFileObject = null;
        this.loadingSize = null;
        this.imageFilter = "640_360";
        this.maxWallpaperSize = 1920;
        this.screenType = i;
        this.swipeBackEnabled = false;
        this.nightTheme = z3;
        this.applyingTheme = themeInfo;
        this.deleteOnCancel = z;
        this.editingTheme = z2;
        if (i == 1) {
            this.accent = this.applyingTheme.getAccent(z2 ^ 1);
            this.useDefaultThemeForButtons = false;
            themeAccent = this.accent;
            this.backupAccentColor = themeAccent.accentColor;
            this.backupMyMessagesAccentColor = themeAccent.myMessagesAccentColor;
            this.backupMyMessagesGradientAccentColor = themeAccent.myMessagesGradientAccentColor;
            this.backupBackgroundOverrideColor = themeAccent.backgroundOverrideColor;
            this.backupBackgroundGradientOverrideColor = themeAccent.backgroundGradientOverrideColor;
            this.backupBackgroundRotation = themeAccent.backgroundRotation;
        } else {
            this.accent = this.applyingTheme.getAccent(false);
            themeAccent = this.accent;
            if (themeAccent != null) {
                this.selectedPattern = themeAccent.pattern;
            }
        }
        themeAccent = this.accent;
        if (themeAccent != null) {
            this.isMotion = themeAccent.patternMotion;
            if (!TextUtils.isEmpty(themeAccent.patternSlug)) {
                this.currentIntensity = this.accent.patternIntensity;
            }
            Theme.applyThemeTemporary(this.applyingTheme, true);
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.goingToPreviewTheme, new Object[0]);
    }

    public void setInitialModes(boolean z, boolean z2) {
        this.isBlurred = z;
        this.isMotion = z2;
    }

    public View createView(Context context) {
        int i;
        String str;
        Context context2 = context;
        this.hasOwnBackground = true;
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
        this.page1.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.page1.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
        this.listView = new RecyclerListView(context2);
        this.listView.setVerticalScrollBarEnabled(true);
        this.listView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        this.listView.setLayoutManager(new LinearLayoutManager(context2, 1, false));
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(this.screenType != 0 ? 12.0f : 0.0f));
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
        this.page2 = new FrameLayout(context2) {
            private boolean ignoreLayout;

            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                FrameLayout.LayoutParams layoutParams;
                int size = MeasureSpec.getSize(i);
                int size2 = MeasureSpec.getSize(i2);
                setMeasuredDimension(size, size2);
                int i3 = 0;
                if (ThemePreviewActivity.this.dropDownContainer != null) {
                    this.ignoreLayout = true;
                    if (!AndroidUtilities.isTablet()) {
                        layoutParams = (FrameLayout.LayoutParams) ThemePreviewActivity.this.dropDownContainer.getLayoutParams();
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
                int measuredHeight = ThemePreviewActivity.this.actionBar2.getMeasuredHeight();
                if (ThemePreviewActivity.this.actionBar2.getVisibility() == 0) {
                    size2 -= measuredHeight;
                }
                layoutParams = (FrameLayout.LayoutParams) ThemePreviewActivity.this.listView2.getLayoutParams();
                layoutParams.topMargin = measuredHeight;
                ThemePreviewActivity.this.listView2.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec(size2 - layoutParams.bottomMargin, NUM));
                ((FrameLayout.LayoutParams) ThemePreviewActivity.this.backgroundImage.getLayoutParams()).topMargin = measuredHeight;
                ThemePreviewActivity.this.backgroundImage.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec(size2, NUM));
                if (ThemePreviewActivity.this.bottomOverlayChat != null) {
                    measureChildWithMargins(ThemePreviewActivity.this.bottomOverlayChat, i, 0, i2, 0);
                }
                while (i3 < ThemePreviewActivity.this.patternLayout.length) {
                    if (ThemePreviewActivity.this.patternLayout[i3] != null) {
                        measureChildWithMargins(ThemePreviewActivity.this.patternLayout[i3], i, 0, i2, 0);
                    }
                    i3++;
                }
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
        this.messagesAdapter = new MessagesAdapter(this, context2);
        this.actionBar2 = createActionBar(context);
        this.actionBar2.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar2.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    if (ThemePreviewActivity.this.checkDiscard()) {
                        ThemePreviewActivity.this.cancelThemeApply(false);
                    }
                } else if (i >= 1 && i <= 3) {
                    ThemePreviewActivity.this.selectColorType(i);
                } else if (i == 4) {
                    if (ThemePreviewActivity.this.removeBackgroundOverride) {
                        Theme.resetCustomWallpaper(false);
                    }
                    ThemePreviewActivity.this.accent.patternSlug = ThemePreviewActivity.this.selectedPattern != null ? ThemePreviewActivity.this.selectedPattern.slug : "";
                    ThemePreviewActivity.this.accent.patternIntensity = ThemePreviewActivity.this.currentIntensity;
                    ThemePreviewActivity.this.accent.patternMotion = ThemePreviewActivity.this.isMotion;
                    ThemePreviewActivity.this.saveAccentWallpaper();
                    NotificationCenter.getGlobalInstance().removeObserver(ThemePreviewActivity.this, NotificationCenter.wallpapersDidLoad);
                    Theme.saveThemeAccents(ThemePreviewActivity.this.applyingTheme, true, false, false, true);
                    Theme.applyPreviousTheme();
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, ThemePreviewActivity.this.applyingTheme, Boolean.valueOf(ThemePreviewActivity.this.nightTheme), null, Integer.valueOf(-1));
                    ThemePreviewActivity.this.finishFragment();
                } else if (i == 5 && ThemePreviewActivity.this.getParentActivity() != null) {
                    String stringBuilder;
                    StringBuilder stringBuilder2 = new StringBuilder();
                    if (ThemePreviewActivity.this.isBlurred) {
                        stringBuilder2.append("blur");
                    }
                    if (ThemePreviewActivity.this.isMotion) {
                        if (stringBuilder2.length() > 0) {
                            stringBuilder2.append("+");
                        }
                        stringBuilder2.append("motion");
                    }
                    if (ThemePreviewActivity.this.currentWallpaper instanceof TL_wallPaper) {
                        TL_wallPaper tL_wallPaper = (TL_wallPaper) ThemePreviewActivity.this.currentWallpaper;
                        StringBuilder stringBuilder3 = new StringBuilder();
                        stringBuilder3.append("https://");
                        stringBuilder3.append(MessagesController.getInstance(ThemePreviewActivity.this.currentAccount).linkPrefix);
                        stringBuilder3.append("/bg/");
                        stringBuilder3.append(tL_wallPaper.slug);
                        stringBuilder = stringBuilder3.toString();
                        if (stringBuilder2.length() > 0) {
                            stringBuilder3 = new StringBuilder();
                            stringBuilder3.append(stringBuilder);
                            stringBuilder3.append("?mode=");
                            stringBuilder3.append(stringBuilder2.toString());
                            stringBuilder = stringBuilder3.toString();
                        }
                    } else if (ThemePreviewActivity.this.currentWallpaper instanceof ColorWallpaper) {
                        ColorWallpaper colorWallpaper = new ColorWallpaper(ThemePreviewActivity.this.selectedPattern != null ? ThemePreviewActivity.this.selectedPattern.slug : "c", ThemePreviewActivity.this.backgroundColor, ThemePreviewActivity.this.backgroundGradientColor, ThemePreviewActivity.this.backgroundRotation, ThemePreviewActivity.this.currentIntensity, ThemePreviewActivity.this.isMotion, null);
                        colorWallpaper.pattern = ThemePreviewActivity.this.selectedPattern;
                        stringBuilder = colorWallpaper.getUrl();
                    }
                    String str = stringBuilder;
                    ThemePreviewActivity themePreviewActivity = ThemePreviewActivity.this;
                    themePreviewActivity.showDialog(new ShareAlert(themePreviewActivity.getParentActivity(), null, str, false, str, false));
                }
            }
        });
        this.backgroundImage = new BackupImageView(context2) {
            private Drawable background;

            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, i2);
                ThemePreviewActivity themePreviewActivity = ThemePreviewActivity.this;
                themePreviewActivity.parallaxScale = themePreviewActivity.parallaxEffect.getScale(getMeasuredWidth(), getMeasuredHeight());
                if (ThemePreviewActivity.this.isMotion) {
                    setScaleX(ThemePreviewActivity.this.parallaxScale);
                    setScaleY(ThemePreviewActivity.this.parallaxScale);
                }
                if (ThemePreviewActivity.this.radialProgress != null) {
                    i = AndroidUtilities.dp(44.0f);
                    int measuredWidth = (getMeasuredWidth() - i) / 2;
                    int measuredHeight = (getMeasuredHeight() - i) / 2;
                    ThemePreviewActivity.this.radialProgress.setProgressRect(measuredWidth, measuredHeight, measuredWidth + i, i + measuredHeight);
                }
                themePreviewActivity = ThemePreviewActivity.this;
                boolean z = themePreviewActivity.screenType == 2 && getMeasuredWidth() <= getMeasuredHeight();
                themePreviewActivity.progressVisible = z;
            }

            /* Access modifiers changed, original: protected */
            public void onDraw(Canvas canvas) {
                Drawable drawable = this.background;
                if ((drawable instanceof ColorDrawable) || (drawable instanceof GradientDrawable)) {
                    this.background.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                    this.background.draw(canvas);
                } else if (drawable instanceof BitmapDrawable) {
                    if (((BitmapDrawable) drawable).getTileModeX() == TileMode.REPEAT) {
                        canvas.save();
                        float f = 2.0f / AndroidUtilities.density;
                        canvas.scale(f, f);
                        this.background.setBounds(0, 0, (int) Math.ceil((double) (((float) getMeasuredWidth()) / f)), (int) Math.ceil((double) (((float) getMeasuredHeight()) / f)));
                        this.background.draw(canvas);
                        canvas.restore();
                    } else {
                        int measuredHeight = getMeasuredHeight();
                        float measuredWidth = ((float) getMeasuredWidth()) / ((float) this.background.getIntrinsicWidth());
                        float intrinsicHeight = ((float) measuredHeight) / ((float) this.background.getIntrinsicHeight());
                        if (measuredWidth < intrinsicHeight) {
                            measuredWidth = intrinsicHeight;
                        }
                        int ceil = (int) Math.ceil((double) ((((float) this.background.getIntrinsicWidth()) * measuredWidth) * ThemePreviewActivity.this.parallaxScale));
                        int ceil2 = (int) Math.ceil((double) ((((float) this.background.getIntrinsicHeight()) * measuredWidth) * ThemePreviewActivity.this.parallaxScale));
                        int measuredWidth2 = (getMeasuredWidth() - ceil) / 2;
                        measuredHeight = (measuredHeight - ceil2) / 2;
                        this.background.setBounds(measuredWidth2, measuredHeight, ceil + measuredWidth2, ceil2 + measuredHeight);
                        this.background.draw(canvas);
                    }
                }
                super.onDraw(canvas);
                if (ThemePreviewActivity.this.progressVisible && ThemePreviewActivity.this.radialProgress != null) {
                    ThemePreviewActivity.this.radialProgress.draw(canvas);
                }
            }

            public Drawable getBackground() {
                return this.background;
            }

            public void setBackground(Drawable drawable) {
                this.background = drawable;
            }

            public void setAlpha(float f) {
                if (ThemePreviewActivity.this.radialProgress != null) {
                    ThemePreviewActivity.this.radialProgress.setOverrideAlpha(f);
                }
            }
        };
        int i2 = this.currentWallpaper instanceof ColorWallpaper ? 3 : 2;
        Object obj = this.currentWallpaper;
        if (obj instanceof FileWallpaper) {
            if ("t".equals(((FileWallpaper) obj).slug)) {
                i2 = 0;
            }
        }
        this.page2.addView(this.backgroundImage, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        if (this.screenType == 2) {
            this.backgroundImage.getImageReceiver().setDelegate(new -$$Lambda$ThemePreviewActivity$5zUqexQGA3eLqm6qHzM1TgnjiLk(this));
        }
        String str2 = "fonts/rmedium.ttf";
        if (this.messagesAdapter.showSecretMessages) {
            this.actionBar2.setTitle("Telegram Beta Chat");
            this.actionBar2.setSubtitle(LocaleController.formatPluralString("Members", 505));
        } else {
            int i3 = this.screenType;
            if (i3 == 2) {
                this.actionBar2.setTitle(LocaleController.getString("BackgroundPreview", NUM));
                Object obj2 = this.currentWallpaper;
                if ((obj2 instanceof ColorWallpaper) || (obj2 instanceof TL_wallPaper)) {
                    this.actionBar2.createMenu().addItem(5, NUM);
                }
            } else if (i3 == 1) {
                ActionBarMenu createMenu = this.actionBar2.createMenu();
                this.saveItem = createMenu.addItem(4, LocaleController.getString("Save", NUM).toUpperCase());
                this.dropDownContainer = new ActionBarMenuItem(context2, createMenu, 0, 0);
                this.dropDownContainer.setSubMenuOpenSide(1);
                this.dropDownContainer.addSubItem(1, LocaleController.getString("ColorPickerMainColor", NUM));
                this.dropDownContainer.addSubItem(2, LocaleController.getString("ColorPickerBackground", NUM));
                this.dropDownContainer.addSubItem(3, LocaleController.getString("ColorPickerMyMessages", NUM));
                this.dropDownContainer.setAllowCloseAnimation(false);
                this.actionBar2.addView(this.dropDownContainer, LayoutHelper.createFrame(-2, -1.0f, 51, AndroidUtilities.isTablet() ? 64.0f : 56.0f, 0.0f, 40.0f, 0.0f));
                this.dropDownContainer.setOnClickListener(new -$$Lambda$ThemePreviewActivity$5s7FSpMZlA--ayOSLajCP8l-z-k(this));
                this.dropDown = new TextView(context2);
                this.dropDown.setGravity(3);
                this.dropDown.setSingleLine(true);
                this.dropDown.setLines(1);
                this.dropDown.setMaxLines(1);
                this.dropDown.setEllipsize(TruncateAt.END);
                this.dropDown.setTextColor(Theme.getColor("actionBarDefaultTitle"));
                this.dropDown.setTypeface(AndroidUtilities.getTypeface(str2));
                this.dropDown.setText(LocaleController.getString("ColorPickerMainColor", NUM));
                Drawable mutate2 = context.getResources().getDrawable(NUM).mutate();
                mutate2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("actionBarDefaultTitle"), Mode.MULTIPLY));
                this.dropDown.setCompoundDrawablesWithIntrinsicBounds(null, null, mutate2, null);
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
                    i = tL_theme2.installs_count;
                    if (i > 0) {
                        this.actionBar2.setSubtitle(LocaleController.formatPluralString("ThemeInstallCount", i));
                    }
                }
                this.actionBar2.setSubtitle(LocaleController.formatDateOnline((System.currentTimeMillis() / 1000) - 3600));
            }
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

            /* Access modifiers changed, original: protected */
            public void onChildPressed(View view, float f, float f2, boolean z) {
                if (!z || !(view instanceof ChatMessageCell) || ((ChatMessageCell) view).isInsideBackground(f, f2)) {
                    super.onChildPressed(view, f, f2, z);
                }
            }
        };
        ((DefaultItemAnimator) this.listView2.getItemAnimator()).setDelayAnimations(false);
        this.listView2.setVerticalScrollBarEnabled(true);
        this.listView2.setOverScrollMode(2);
        i = this.screenType;
        if (i == 2) {
            this.listView2.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(52.0f));
        } else if (i == 1) {
            this.listView2.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(16.0f));
        } else {
            this.listView2.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f));
        }
        this.listView2.setClipToPadding(false);
        this.listView2.setLayoutManager(new LinearLayoutManager(context2, 1, true));
        this.listView2.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        if (this.screenType == 1) {
            this.page2.addView(this.listView2, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 294.0f));
            this.listView2.setOnItemClickListener(new -$$Lambda$ThemePreviewActivity$K5VHKJz5ekLeqQEviKY59o_9oM8(this));
        } else {
            this.page2.addView(this.listView2, LayoutHelper.createFrame(-1, -1, 51));
        }
        this.listView2.setOnScrollListener(new OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                ThemePreviewActivity.this.listView2.invalidateViews();
            }
        });
        this.page2.addView(this.actionBar2, LayoutHelper.createFrame(-1, -2.0f));
        this.parallaxEffect = new WallpaperParallaxEffect(context2);
        this.parallaxEffect.setCallback(new -$$Lambda$ThemePreviewActivity$M-QE7kHmCNWWfbMFvvOIuS2G8JQ(this));
        i = this.screenType;
        String str3 = "chat_fieldOverlayText";
        if (i == 1 || i == 2) {
            int i4;
            this.radialProgress = new RadialProgress2(this.backgroundImage);
            this.radialProgress.setColors("chat_serviceBackground", "chat_serviceBackground", "chat_serviceText", "chat_serviceText");
            if (this.screenType == 2) {
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
                this.page2.addView(this.bottomOverlayChat, LayoutHelper.createFrame(-1, 51, 80));
                this.bottomOverlayChat.setOnClickListener(new -$$Lambda$ThemePreviewActivity$MlBgGb3qPcS-NLKZdUFRg4IQ2R8(this));
                this.bottomOverlayChatText = new TextView(context2);
                this.bottomOverlayChatText.setTextSize(1, 15.0f);
                this.bottomOverlayChatText.setTypeface(AndroidUtilities.getTypeface(str2));
                this.bottomOverlayChatText.setTextColor(Theme.getColor(str3));
                this.bottomOverlayChatText.setText(LocaleController.getString("SetBackground", NUM));
                this.bottomOverlayChat.addView(this.bottomOverlayChatText, LayoutHelper.createFrame(-2, -2, 17));
            }
            final Rect rect = new Rect();
            this.sheetDrawable = context.getResources().getDrawable(NUM).mutate();
            this.sheetDrawable.getPadding(rect);
            this.sheetDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhite"), Mode.MULTIPLY));
            String[] strArr = new String[i2];
            int[] iArr = new int[i2];
            this.checkBoxView = new WallpaperCheckBoxView[i2];
            if (i2 != 0) {
                this.buttonsContainer = new FrameLayout(context2);
                if (this.screenType == 1) {
                    strArr[0] = LocaleController.getString("BackgroundMotion", NUM);
                    strArr[1] = LocaleController.getString("BackgroundPattern", NUM);
                } else if (this.currentWallpaper instanceof ColorWallpaper) {
                    strArr[0] = LocaleController.getString("BackgroundColor", NUM);
                    strArr[1] = LocaleController.getString("BackgroundPattern", NUM);
                    strArr[2] = LocaleController.getString("BackgroundMotion", NUM);
                } else {
                    strArr[0] = LocaleController.getString("BackgroundBlurred", NUM);
                    strArr[1] = LocaleController.getString("BackgroundMotion", NUM);
                }
                TextPaint textPaint = new TextPaint(1);
                textPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
                textPaint.setTypeface(AndroidUtilities.getTypeface(str2));
                int i5 = 0;
                i4 = 0;
                while (i5 < strArr.length) {
                    str = str2;
                    iArr[i5] = (int) Math.ceil((double) textPaint.measureText(strArr[i5]));
                    i4 = Math.max(i4, iArr[i5]);
                    i5++;
                    str2 = str;
                }
                str = str2;
            } else {
                str = str2;
                i4 = 0;
            }
            int i6 = 0;
            while (i6 < i2) {
                WallpaperCheckBoxView[] wallpaperCheckBoxViewArr = this.checkBoxView;
                boolean z = (this.screenType != 1 && (this.currentWallpaper instanceof ColorWallpaper) && i6 == 0) ? false : true;
                wallpaperCheckBoxViewArr[i6] = new WallpaperCheckBoxView(context2, z);
                this.checkBoxView[i6].setBackgroundColor(this.backgroundColor);
                this.checkBoxView[i6].setText(strArr[i6], iArr[i6], i4);
                if (this.screenType != 1) {
                    if (!(this.currentWallpaper instanceof ColorWallpaper)) {
                        this.checkBoxView[i6].setChecked(i6 == 0 ? this.isBlurred : this.isMotion, false);
                    } else if (i6 == 1) {
                        this.checkBoxView[i6].setChecked(this.selectedPattern != null, false);
                    } else if (i6 == 2) {
                        this.checkBoxView[i6].setChecked(this.isMotion, false);
                    }
                }
                int dp = AndroidUtilities.dp(56.0f) + i4;
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(dp, -2);
                layoutParams.gravity = 19;
                layoutParams.leftMargin = i6 == 1 ? dp + AndroidUtilities.dp(9.0f) : 0;
                this.buttonsContainer.addView(this.checkBoxView[i6], layoutParams);
                WallpaperCheckBoxView[] wallpaperCheckBoxViewArr2 = this.checkBoxView;
                wallpaperCheckBoxViewArr2[i6].setOnClickListener(new -$$Lambda$ThemePreviewActivity$McmaoCVsH_socB5nBvmq6cCQ2Bo(this, i6, wallpaperCheckBoxViewArr2[i6]));
                if (i6 == 2) {
                    this.checkBoxView[i6].setAlpha(0.0f);
                    this.checkBoxView[i6].setVisibility(4);
                }
                i6++;
            }
            if (this.screenType == 1) {
                updateCheckboxes();
            }
            if (this.screenType == 1 || (this.currentWallpaper instanceof ColorWallpaper)) {
                this.isBlurred = false;
                i6 = 0;
                while (i6 < 2) {
                    FrameLayout.LayoutParams createFrame;
                    this.patternLayout[i6] = new FrameLayout(context2) {
                        public void onDraw(Canvas canvas) {
                            if (i6 == 0) {
                                ThemePreviewActivity.this.sheetDrawable.setBounds(ThemePreviewActivity.this.colorPicker.getLeft() - rect.left, 0, ThemePreviewActivity.this.colorPicker.getRight() + rect.right, getMeasuredHeight());
                            } else {
                                ThemePreviewActivity.this.sheetDrawable.setBounds(-rect.left, 0, getMeasuredWidth() + rect.right, getMeasuredHeight());
                            }
                            ThemePreviewActivity.this.sheetDrawable.draw(canvas);
                        }
                    };
                    if (i6 == 1 || this.screenType == 2) {
                        this.patternLayout[i6].setVisibility(4);
                    }
                    this.patternLayout[i6].setWillNotDraw(false);
                    if (this.screenType == 2) {
                        createFrame = LayoutHelper.createFrame(-1, i6 == 0 ? 342 : 316, 83);
                    } else {
                        createFrame = LayoutHelper.createFrame(-1, i6 == 0 ? 294 : 316, 83);
                    }
                    if (i6 == 0) {
                        createFrame.height += AndroidUtilities.dp(12.0f) + rect.top;
                        this.patternLayout[i6].setPadding(0, AndroidUtilities.dp(12.0f) + rect.top, 0, 0);
                    }
                    this.page2.addView(this.patternLayout[i6], createFrame);
                    if (i6 == 1 || this.screenType == 2) {
                        this.patternsButtonsContainer[i6] = new FrameLayout(context2) {
                            public void onDraw(Canvas canvas) {
                                int intrinsicHeight = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                                Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), intrinsicHeight);
                                Theme.chat_composeShadowDrawable.draw(canvas);
                                canvas.drawRect(0.0f, (float) intrinsicHeight, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
                            }
                        };
                        this.patternsButtonsContainer[i6].setWillNotDraw(false);
                        this.patternsButtonsContainer[i6].setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
                        this.patternsButtonsContainer[i6].setClickable(true);
                        this.patternLayout[i6].addView(this.patternsButtonsContainer[i6], LayoutHelper.createFrame(-1, 51, 80));
                        this.patternsCancelButton[i6] = new TextView(context2);
                        this.patternsCancelButton[i6].setTextSize(1, 15.0f);
                        this.patternsCancelButton[i6].setTypeface(AndroidUtilities.getTypeface(str));
                        this.patternsCancelButton[i6].setTextColor(Theme.getColor(str3));
                        this.patternsCancelButton[i6].setText(LocaleController.getString("Cancel", NUM).toUpperCase());
                        this.patternsCancelButton[i6].setGravity(17);
                        this.patternsCancelButton[i6].setPadding(AndroidUtilities.dp(21.0f), 0, AndroidUtilities.dp(21.0f), 0);
                        this.patternsCancelButton[i6].setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21"), 0));
                        this.patternsButtonsContainer[i6].addView(this.patternsCancelButton[i6], LayoutHelper.createFrame(-2, -1, 51));
                        this.patternsCancelButton[i6].setOnClickListener(new -$$Lambda$ThemePreviewActivity$oKVC7vtrmS69iOD_ENBYyo2a9vo(this, i6));
                        this.patternsSaveButton[i6] = new TextView(context2);
                        this.patternsSaveButton[i6].setTextSize(1, 15.0f);
                        this.patternsSaveButton[i6].setTypeface(AndroidUtilities.getTypeface(str));
                        this.patternsSaveButton[i6].setTextColor(Theme.getColor(str3));
                        this.patternsSaveButton[i6].setText(LocaleController.getString("ApplyTheme", NUM).toUpperCase());
                        this.patternsSaveButton[i6].setGravity(17);
                        this.patternsSaveButton[i6].setPadding(AndroidUtilities.dp(21.0f), 0, AndroidUtilities.dp(21.0f), 0);
                        this.patternsSaveButton[i6].setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21"), 0));
                        this.patternsButtonsContainer[i6].addView(this.patternsSaveButton[i6], LayoutHelper.createFrame(-2, -1, 53));
                        this.patternsSaveButton[i6].setOnClickListener(new -$$Lambda$ThemePreviewActivity$GE-xlfx3D70ATQPtXwmggEyRjRI(this, i6));
                    }
                    if (i6 == 1) {
                        TextView textView = new TextView(context2);
                        textView.setLines(1);
                        textView.setSingleLine(true);
                        textView.setText(LocaleController.getString("BackgroundChoosePattern", NUM));
                        textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                        textView.setTextSize(1, 20.0f);
                        textView.setTypeface(AndroidUtilities.getTypeface(str));
                        textView.setPadding(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(21.0f), AndroidUtilities.dp(8.0f));
                        textView.setEllipsize(TruncateAt.MIDDLE);
                        textView.setGravity(16);
                        this.patternLayout[i6].addView(textView, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, 21.0f, 0.0f, 0.0f));
                        this.patternsListView = new RecyclerListView(context2) {
                            public boolean onTouchEvent(MotionEvent motionEvent) {
                                if (motionEvent.getAction() == 0) {
                                    getParent().requestDisallowInterceptTouchEvent(true);
                                }
                                return super.onTouchEvent(motionEvent);
                            }
                        };
                        RecyclerListView recyclerListView = this.patternsListView;
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context2, 0, false);
                        this.patternsLayoutManager = linearLayoutManager;
                        recyclerListView.setLayoutManager(linearLayoutManager);
                        recyclerListView = this.patternsListView;
                        PatternsAdapter patternsAdapter = new PatternsAdapter(context2);
                        this.patternsAdapter = patternsAdapter;
                        recyclerListView.setAdapter(patternsAdapter);
                        this.patternsListView.addItemDecoration(new ItemDecoration() {
                            public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, State state) {
                                int childAdapterPosition = recyclerView.getChildAdapterPosition(view);
                                rect.left = AndroidUtilities.dp(12.0f);
                                rect.top = 0;
                                rect.bottom = 0;
                                if (childAdapterPosition == state.getItemCount() - 1) {
                                    rect.right = AndroidUtilities.dp(12.0f);
                                }
                            }
                        });
                        this.patternLayout[i6].addView(this.patternsListView, LayoutHelper.createFrame(-1, 100.0f, 51, 0.0f, 76.0f, 0.0f, 0.0f));
                        this.patternsListView.setOnItemClickListener(new -$$Lambda$ThemePreviewActivity$D6LIJaHibvXXnwsT8EX1T-xfcms(this));
                        this.intensityCell = new HeaderCell(context2);
                        this.intensityCell.setText(LocaleController.getString("BackgroundIntensity", NUM));
                        this.patternLayout[i6].addView(this.intensityCell, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 175.0f, 0.0f, 0.0f));
                        this.intensitySeekBar = new SeekBarView(context2) {
                            public boolean onTouchEvent(MotionEvent motionEvent) {
                                if (motionEvent.getAction() == 0) {
                                    getParent().requestDisallowInterceptTouchEvent(true);
                                }
                                return super.onTouchEvent(motionEvent);
                            }
                        };
                        this.intensitySeekBar.setProgress(this.currentIntensity);
                        this.intensitySeekBar.setReportChanges(true);
                        this.intensitySeekBar.setDelegate(new SeekBarViewDelegate() {
                            public void onSeekBarPressed(boolean z) {
                            }

                            public void onSeekBarDrag(boolean z, float f) {
                                ThemePreviewActivity.this.currentIntensity = f;
                                ThemePreviewActivity.this.backgroundImage.getImageReceiver().setAlpha(ThemePreviewActivity.this.currentIntensity);
                                ThemePreviewActivity.this.backgroundImage.invalidate();
                                ThemePreviewActivity.this.patternsListView.invalidateViews();
                            }
                        });
                        this.patternLayout[i6].addView(this.intensitySeekBar, LayoutHelper.createFrame(-1, 30.0f, 51, 9.0f, 215.0f, 9.0f, 0.0f));
                    } else {
                        this.colorPicker = new ColorPicker(context2, this.editingTheme, new ColorPickerDelegate() {
                            public void setColor(int i, int i2, boolean z) {
                                if (ThemePreviewActivity.this.screenType == 2) {
                                    ThemePreviewActivity.this.setBackgroundColor(i, i2, z);
                                } else {
                                    ThemePreviewActivity.this.scheduleApplyColor(i, i2, z);
                                }
                            }

                            public void openThemeCreate(boolean z) {
                                if (!z) {
                                    AlertsCreator.createThemeCreateDialog(ThemePreviewActivity.this, 1, null, null);
                                } else if (ThemePreviewActivity.this.accent.info == null) {
                                    ThemePreviewActivity.this.finishFragment();
                                    MessagesController.getInstance(ThemePreviewActivity.this.currentAccount).saveThemeToServer(ThemePreviewActivity.this.accent.parentTheme, ThemePreviewActivity.this.accent);
                                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needShareTheme, ThemePreviewActivity.this.accent.parentTheme, ThemePreviewActivity.this.accent);
                                } else {
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append("https://");
                                    stringBuilder.append(MessagesController.getInstance(ThemePreviewActivity.this.currentAccount).linkPrefix);
                                    stringBuilder.append("/addtheme/");
                                    stringBuilder.append(ThemePreviewActivity.this.accent.info.slug);
                                    String stringBuilder2 = stringBuilder.toString();
                                    ThemePreviewActivity themePreviewActivity = ThemePreviewActivity.this;
                                    themePreviewActivity.showDialog(new ShareAlert(themePreviewActivity.getParentActivity(), null, stringBuilder2, false, stringBuilder2, false));
                                }
                            }

                            public void deleteTheme() {
                                if (ThemePreviewActivity.this.getParentActivity() != null) {
                                    Builder builder = new Builder(ThemePreviewActivity.this.getParentActivity());
                                    builder.setTitle(LocaleController.getString("DeleteThemeTitle", NUM));
                                    builder.setMessage(LocaleController.getString("DeleteThemeAlert", NUM));
                                    builder.setPositiveButton(LocaleController.getString("Delete", NUM), new -$$Lambda$ThemePreviewActivity$16$OcBy82rlu2YDyBz3RSWz0AHVQqE(this));
                                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                                    AlertDialog create = builder.create();
                                    ThemePreviewActivity.this.showDialog(create);
                                    TextView textView = (TextView) create.getButton(-1);
                                    if (textView != null) {
                                        textView.setTextColor(Theme.getColor("dialogTextRed2"));
                                    }
                                }
                            }

                            public /* synthetic */ void lambda$deleteTheme$0$ThemePreviewActivity$16(DialogInterface dialogInterface, int i) {
                                Theme.deleteThemeAccent(ThemePreviewActivity.this.applyingTheme, ThemePreviewActivity.this.accent, true);
                                Theme.applyPreviousTheme();
                                Theme.refreshThemeColors();
                                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, ThemePreviewActivity.this.applyingTheme, Boolean.valueOf(ThemePreviewActivity.this.nightTheme), null, Integer.valueOf(-1));
                                ThemePreviewActivity.this.finishFragment();
                            }

                            public void rotateColors() {
                                if (ThemePreviewActivity.this.screenType == 2) {
                                    ThemePreviewActivity themePreviewActivity = ThemePreviewActivity.this;
                                    themePreviewActivity.backgroundRotation = themePreviewActivity.backgroundRotation + 45;
                                    while (ThemePreviewActivity.this.backgroundRotation >= 360) {
                                        themePreviewActivity = ThemePreviewActivity.this;
                                        themePreviewActivity.backgroundRotation = themePreviewActivity.backgroundRotation - 360;
                                    }
                                    themePreviewActivity = ThemePreviewActivity.this;
                                    themePreviewActivity.setBackgroundColor(themePreviewActivity.backgroundColor, 0, true);
                                    return;
                                }
                                ThemeAccent access$2300 = ThemePreviewActivity.this.accent;
                                access$2300.backgroundRotation += 45;
                                while (ThemePreviewActivity.this.accent.backgroundRotation >= 360) {
                                    access$2300 = ThemePreviewActivity.this.accent;
                                    access$2300.backgroundRotation -= 360;
                                }
                                Theme.refreshThemeColors();
                            }

                            public int getDefaultColor(int i) {
                                return (ThemePreviewActivity.this.colorType == 3 && ThemePreviewActivity.this.applyingTheme.firstAccentIsDefault && i == 0) ? ((ThemeAccent) ThemePreviewActivity.this.applyingTheme.themeAccentsMap.get(Theme.DEFALT_THEME_ACCENT_ID)).myMessagesAccentColor : 0;
                            }

                            public boolean hasChanges() {
                                ThemePreviewActivity themePreviewActivity = ThemePreviewActivity.this;
                                return themePreviewActivity.hasChanges(themePreviewActivity.colorType);
                            }
                        });
                        if (this.screenType == 1) {
                            this.patternLayout[i6].addView(this.colorPicker, LayoutHelper.createFrame(-1, -1, 1));
                            if (this.applyingTheme.isDark()) {
                                this.colorPicker.setMinBrightness(0.2f);
                            } else {
                                this.colorPicker.setMinBrightness(0.05f);
                                this.colorPicker.setMaxBrightness(0.8f);
                            }
                            this.colorPicker.setType(1, hasChanges(1), false, false, false, 0, false);
                            this.colorPicker.setColor(this.accent.accentColor, 0);
                        } else {
                            this.patternLayout[i6].addView(this.colorPicker, LayoutHelper.createFrame(-1, -1.0f, 1, 0.0f, 0.0f, 0.0f, 48.0f));
                        }
                    }
                    i6++;
                }
            }
            updateButtonState(false, false);
            if (!this.backgroundImage.getImageReceiver().hasBitmapImage()) {
                this.page2.setBackgroundColor(-16777216);
            }
            if (!(this.screenType == 1 || (this.currentWallpaper instanceof ColorWallpaper))) {
                this.backgroundImage.getImageReceiver().setCrossfadeWithOldImage(true);
                this.backgroundImage.getImageReceiver().setForceCrossfade(true);
            }
        } else {
            str = str2;
        }
        this.listView2.setAdapter(this.messagesAdapter);
        this.frameLayout = new FrameLayout(context2) {
            private int[] loc = new int[2];

            public void invalidate() {
                super.invalidate();
                if (ThemePreviewActivity.this.page2 != null) {
                    ThemePreviewActivity.this.page2.invalidate();
                }
            }

            /* Access modifiers changed, original: protected */
            public void onDraw(Canvas canvas) {
                if (!AndroidUtilities.usingHardwareInput) {
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
            }
        };
        this.frameLayout.setWillNotDraw(false);
        FrameLayout frameLayout = this.frameLayout;
        this.fragmentView = frameLayout;
        ViewTreeObserver viewTreeObserver = frameLayout.getViewTreeObserver();
        -$$Lambda$ThemePreviewActivity$I_g4S1Y0y4VHyo3pgzT98mcFmJQ -__lambda_themepreviewactivity_i_g4s1y0y4vhyo3pgzt98mcfmjq = new -$$Lambda$ThemePreviewActivity$I_g4S1Y0y4VHyo3pgzT98mcFmJQ(this);
        this.onGlobalLayoutListener = -__lambda_themepreviewactivity_i_g4s1y0y4vhyo3pgzt98mcfmjq;
        viewTreeObserver.addOnGlobalLayoutListener(-__lambda_themepreviewactivity_i_g4s1y0y4vhyo3pgzt98mcfmjq);
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
                return ThemePreviewActivity.this.screenType != 0 ? 1 : 2;
            }

            public Object instantiateItem(ViewGroup viewGroup, int i) {
                View access$5100 = i == 0 ? ThemePreviewActivity.this.page2 : ThemePreviewActivity.this.page1;
                viewGroup.addView(access$5100);
                return access$5100;
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
        this.frameLayout.addView(this.viewPager, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, this.screenType == 0 ? 48.0f : 0.0f));
        if (this.screenType == 0) {
            View view = new View(context2);
            view.setBackgroundColor(Theme.getColor("dialogShadowLine"));
            FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(-1, 1, 83);
            layoutParams2.bottomMargin = AndroidUtilities.dp(48.0f);
            this.frameLayout.addView(view, layoutParams2);
            this.saveButtonsContainer = new FrameLayout(context2);
            this.saveButtonsContainer.setBackgroundColor(getButtonsColor("windowBackgroundWhite"));
            this.frameLayout.addView(this.saveButtonsContainer, LayoutHelper.createFrame(-1, 48, 83));
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
            this.saveButtonsContainer.addView(this.dotsContainer, LayoutHelper.createFrame(22, 8, 17));
            this.cancelButton = new TextView(context2);
            this.cancelButton.setTextSize(1, 14.0f);
            this.cancelButton.setTextColor(getButtonsColor(str3));
            this.cancelButton.setGravity(17);
            this.cancelButton.setBackgroundDrawable(Theme.createSelectorDrawable(NUM, 0));
            this.cancelButton.setPadding(AndroidUtilities.dp(29.0f), 0, AndroidUtilities.dp(29.0f), 0);
            this.cancelButton.setText(LocaleController.getString("Cancel", NUM).toUpperCase());
            this.cancelButton.setTypeface(AndroidUtilities.getTypeface(str));
            this.saveButtonsContainer.addView(this.cancelButton, LayoutHelper.createFrame(-2, -1, 51));
            this.cancelButton.setOnClickListener(new -$$Lambda$ThemePreviewActivity$dt5jEPdCNrVYaXU3ksa7GiAxiUE(this));
            this.doneButton = new TextView(context2);
            this.doneButton.setTextSize(1, 14.0f);
            this.doneButton.setTextColor(getButtonsColor(str3));
            this.doneButton.setGravity(17);
            this.doneButton.setBackgroundDrawable(Theme.createSelectorDrawable(NUM, 0));
            this.doneButton.setPadding(AndroidUtilities.dp(29.0f), 0, AndroidUtilities.dp(29.0f), 0);
            this.doneButton.setText(LocaleController.getString("ApplyTheme", NUM).toUpperCase());
            this.doneButton.setTypeface(AndroidUtilities.getTypeface(str));
            this.saveButtonsContainer.addView(this.doneButton, LayoutHelper.createFrame(-2, -1, 53));
            this.doneButton.setOnClickListener(new -$$Lambda$ThemePreviewActivity$fCAJ-xi8TXWO3zb94lpRBGxA74Q(this));
        }
        this.themeDescriptions = getThemeDescriptionsInternal();
        setCurrentImage(true);
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$1$ThemePreviewActivity(ImageReceiver imageReceiver, boolean z, boolean z2) {
        if (!(this.currentWallpaper instanceof ColorWallpaper)) {
            Drawable drawable = imageReceiver.getDrawable();
            if (z && drawable != null) {
                Theme.applyChatServiceMessageColor(AndroidUtilities.calcDrawableColor(drawable));
                this.listView2.invalidateViews();
                FrameLayout frameLayout = this.buttonsContainer;
                if (frameLayout != null) {
                    int childCount = frameLayout.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        this.buttonsContainer.getChildAt(i).invalidate();
                    }
                }
                RadialProgress2 radialProgress2 = this.radialProgress;
                if (radialProgress2 != null) {
                    String str = "chat_serviceText";
                    String str2 = "chat_serviceBackground";
                    radialProgress2.setColors(str2, str2, str, str);
                }
                if (!z2 && this.isBlurred && this.blurredBitmap == null) {
                    this.backgroundImage.getImageReceiver().setCrossfadeWithOldImage(false);
                    updateBlurred();
                    this.backgroundImage.getImageReceiver().setCrossfadeWithOldImage(true);
                }
            }
        }
    }

    public /* synthetic */ void lambda$createView$2$ThemePreviewActivity(View view) {
        this.dropDownContainer.toggleSubMenu();
    }

    public /* synthetic */ void lambda$createView$3$ThemePreviewActivity(View view, int i, float f, float f2) {
        if (view instanceof ChatMessageCell) {
            ChatMessageCell chatMessageCell = (ChatMessageCell) view;
            if (!chatMessageCell.isInsideBackground(f, f2)) {
                selectColorType(2);
            } else if (chatMessageCell.getMessageObject().isOutOwner()) {
                selectColorType(3);
            } else {
                selectColorType(1);
            }
        }
    }

    public /* synthetic */ void lambda$createView$4$ThemePreviewActivity(int i, int i2) {
        if (this.isMotion) {
            float f = 1.0f;
            if (this.motionAnimation != null) {
                f = (this.backgroundImage.getScaleX() - 1.0f) / (this.parallaxScale - 1.0f);
            }
            this.backgroundImage.setTranslationX(((float) i) * f);
            this.backgroundImage.setTranslationY(((float) i2) * f);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:96:0x01f0  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x01ed  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x01fa  */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x023b  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x01ed  */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x01f0  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x01fa  */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x023b  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x0149 A:{SYNTHETIC, Splitter:B:64:0x0149} */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x017c  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0172  */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x01f0  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x01ed  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x01fa  */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x023b  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x0149 A:{SYNTHETIC, Splitter:B:64:0x0149} */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0172  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x017c  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x01ed  */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x01f0  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x01fa  */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x023b  */
    public /* synthetic */ void lambda$createView$5$ThemePreviewActivity(android.view.View r20) {
        /*
        r19 = this;
        r1 = r19;
        r0 = org.telegram.ui.ActionBar.Theme.getActiveTheme();
        r2 = r1.isBlurred;
        r3 = 0;
        r2 = r0.generateWallpaperName(r3, r2);
        r4 = r1.isBlurred;
        r5 = 0;
        if (r4 == 0) goto L_0x0018;
    L_0x0012:
        r0 = r0.generateWallpaperName(r3, r5);
        r4 = r0;
        goto L_0x0019;
    L_0x0018:
        r4 = r2;
    L_0x0019:
        r6 = new java.io.File;
        r0 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed();
        r6.<init>(r0, r2);
        r0 = r1.currentWallpaper;
        r7 = r0 instanceof org.telegram.tgnet.TLRPC.TL_wallPaper;
        r8 = "jpg";
        r9 = "t";
        r10 = 87;
        r11 = 1;
        if (r7 == 0) goto L_0x005e;
    L_0x002f:
        r0 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x0040 }
        r0.<init>(r6);	 Catch:{ Exception -> 0x0040 }
        r7 = r1.originalBitmap;	 Catch:{ Exception -> 0x0040 }
        r12 = android.graphics.Bitmap.CompressFormat.JPEG;	 Catch:{ Exception -> 0x0040 }
        r7.compress(r12, r10, r0);	 Catch:{ Exception -> 0x0040 }
        r0.close();	 Catch:{ Exception -> 0x0040 }
        r0 = 1;
        goto L_0x0045;
    L_0x0040:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        r0 = 0;
    L_0x0045:
        if (r0 != 0) goto L_0x00e8;
    L_0x0047:
        r0 = r1.currentWallpaper;
        r0 = (org.telegram.tgnet.TLRPC.TL_wallPaper) r0;
        r0 = r0.document;
        r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r11);
        r0 = org.telegram.messenger.AndroidUtilities.copyFile(r0, r6);	 Catch:{ Exception -> 0x0057 }
        goto L_0x00e8;
    L_0x0057:
        r0 = move-exception;
        r7 = r0;
        org.telegram.messenger.FileLog.e(r7);
        goto L_0x0143;
    L_0x005e:
        r7 = r0 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper;
        if (r7 == 0) goto L_0x00ea;
    L_0x0062:
        r7 = r1.selectedPattern;
        if (r7 == 0) goto L_0x00e7;
    L_0x0066:
        r0 = (org.telegram.ui.WallpapersListActivity.ColorWallpaper) r0;	 Catch:{ all -> 0x00e2 }
        r0 = r1.backgroundImage;	 Catch:{ all -> 0x00e2 }
        r0 = r0.getImageReceiver();	 Catch:{ all -> 0x00e2 }
        r0 = r0.getBitmap();	 Catch:{ all -> 0x00e2 }
        r7 = r0.getWidth();	 Catch:{ all -> 0x00e2 }
        r12 = r0.getHeight();	 Catch:{ all -> 0x00e2 }
        r13 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ all -> 0x00e2 }
        r7 = android.graphics.Bitmap.createBitmap(r7, r12, r13);	 Catch:{ all -> 0x00e2 }
        r12 = new android.graphics.Canvas;	 Catch:{ all -> 0x00e2 }
        r12.<init>(r7);	 Catch:{ all -> 0x00e2 }
        r13 = r1.backgroundGradientColor;	 Catch:{ all -> 0x00e2 }
        r14 = 2;
        if (r13 == 0) goto L_0x00ae;
    L_0x008a:
        r13 = new android.graphics.drawable.GradientDrawable;	 Catch:{ all -> 0x00e2 }
        r15 = r1.backgroundRotation;	 Catch:{ all -> 0x00e2 }
        r15 = org.telegram.ui.Components.BackgroundGradientDrawable.getGradientOrientation(r15);	 Catch:{ all -> 0x00e2 }
        r3 = new int[r14];	 Catch:{ all -> 0x00e2 }
        r10 = r1.backgroundColor;	 Catch:{ all -> 0x00e2 }
        r3[r5] = r10;	 Catch:{ all -> 0x00e2 }
        r10 = r1.backgroundGradientColor;	 Catch:{ all -> 0x00e2 }
        r3[r11] = r10;	 Catch:{ all -> 0x00e2 }
        r13.<init>(r15, r3);	 Catch:{ all -> 0x00e2 }
        r3 = r7.getWidth();	 Catch:{ all -> 0x00e2 }
        r10 = r7.getHeight();	 Catch:{ all -> 0x00e2 }
        r13.setBounds(r5, r5, r3, r10);	 Catch:{ all -> 0x00e2 }
        r13.draw(r12);	 Catch:{ all -> 0x00e2 }
        goto L_0x00b3;
    L_0x00ae:
        r3 = r1.backgroundColor;	 Catch:{ all -> 0x00e2 }
        r12.drawColor(r3);	 Catch:{ all -> 0x00e2 }
    L_0x00b3:
        r3 = new android.graphics.Paint;	 Catch:{ all -> 0x00e2 }
        r3.<init>(r14);	 Catch:{ all -> 0x00e2 }
        r10 = new android.graphics.PorterDuffColorFilter;	 Catch:{ all -> 0x00e2 }
        r13 = r1.patternColor;	 Catch:{ all -> 0x00e2 }
        r14 = r1.blendMode;	 Catch:{ all -> 0x00e2 }
        r10.<init>(r13, r14);	 Catch:{ all -> 0x00e2 }
        r3.setColorFilter(r10);	 Catch:{ all -> 0x00e2 }
        r10 = NUM; // 0x437var_ float:255.0 double:5.5947823E-315;
        r13 = r1.currentIntensity;	 Catch:{ all -> 0x00e2 }
        r13 = r13 * r10;
        r10 = (int) r13;	 Catch:{ all -> 0x00e2 }
        r3.setAlpha(r10);	 Catch:{ all -> 0x00e2 }
        r10 = 0;
        r12.drawBitmap(r0, r10, r10, r3);	 Catch:{ all -> 0x00e2 }
        r0 = new java.io.FileOutputStream;	 Catch:{ all -> 0x00e2 }
        r0.<init>(r6);	 Catch:{ all -> 0x00e2 }
        r3 = android.graphics.Bitmap.CompressFormat.JPEG;	 Catch:{ all -> 0x00e2 }
        r10 = 87;
        r7.compress(r3, r10, r0);	 Catch:{ all -> 0x00e2 }
        r0.close();	 Catch:{ all -> 0x00e2 }
        goto L_0x00e7;
    L_0x00e2:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x0143;
    L_0x00e7:
        r0 = 1;
    L_0x00e8:
        r3 = 0;
        goto L_0x0145;
    L_0x00ea:
        r3 = r0 instanceof org.telegram.ui.WallpapersListActivity.FileWallpaper;
        if (r3 == 0) goto L_0x011c;
    L_0x00ee:
        r0 = (org.telegram.ui.WallpapersListActivity.FileWallpaper) r0;
        r3 = r0.resId;
        if (r3 != 0) goto L_0x00e7;
    L_0x00f4:
        r3 = r0.slug;
        r3 = r9.equals(r3);
        if (r3 == 0) goto L_0x00fd;
    L_0x00fc:
        goto L_0x00e7;
    L_0x00fd:
        r3 = r0.originalPath;	 Catch:{ Exception -> 0x0115 }
        if (r3 == 0) goto L_0x0104;
    L_0x0101:
        r0 = r0.originalPath;	 Catch:{ Exception -> 0x0115 }
        goto L_0x0106;
    L_0x0104:
        r0 = r0.path;	 Catch:{ Exception -> 0x0115 }
    L_0x0106:
        r3 = r0.equals(r6);	 Catch:{ Exception -> 0x0115 }
        if (r3 == 0) goto L_0x010e;
    L_0x010c:
        r0 = 1;
        goto L_0x0145;
    L_0x010e:
        r0 = org.telegram.messenger.AndroidUtilities.copyFile(r0, r6);	 Catch:{ Exception -> 0x0113 }
        goto L_0x0145;
    L_0x0113:
        r0 = move-exception;
        goto L_0x0117;
    L_0x0115:
        r0 = move-exception;
        r3 = 0;
    L_0x0117:
        org.telegram.messenger.FileLog.e(r0);
        r0 = 0;
        goto L_0x0145;
    L_0x011c:
        r3 = r0 instanceof org.telegram.messenger.MediaController.SearchImage;
        if (r3 == 0) goto L_0x0143;
    L_0x0120:
        r0 = (org.telegram.messenger.MediaController.SearchImage) r0;
        r3 = r0.photo;
        if (r3 == 0) goto L_0x0133;
    L_0x0126:
        r0 = r3.sizes;
        r3 = r1.maxWallpaperSize;
        r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r3, r11);
        r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r11);
        goto L_0x0139;
    L_0x0133:
        r0 = r0.imageUrl;
        r0 = org.telegram.messenger.ImageLoader.getHttpFilePath(r0, r8);
    L_0x0139:
        r0 = org.telegram.messenger.AndroidUtilities.copyFile(r0, r6);	 Catch:{ Exception -> 0x013e }
        goto L_0x00e8;
    L_0x013e:
        r0 = move-exception;
        r3 = r0;
        org.telegram.messenger.FileLog.e(r3);
    L_0x0143:
        r0 = 0;
        goto L_0x00e8;
    L_0x0145:
        r7 = r1.isBlurred;
        if (r7 == 0) goto L_0x016a;
    L_0x0149:
        r0 = new java.io.File;	 Catch:{ all -> 0x0165 }
        r7 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed();	 Catch:{ all -> 0x0165 }
        r0.<init>(r7, r4);	 Catch:{ all -> 0x0165 }
        r7 = new java.io.FileOutputStream;	 Catch:{ all -> 0x0165 }
        r7.<init>(r0);	 Catch:{ all -> 0x0165 }
        r0 = r1.blurredBitmap;	 Catch:{ all -> 0x0165 }
        r10 = android.graphics.Bitmap.CompressFormat.JPEG;	 Catch:{ all -> 0x0165 }
        r12 = 87;
        r0.compress(r10, r12, r7);	 Catch:{ all -> 0x0165 }
        r7.close();	 Catch:{ all -> 0x0165 }
        r0 = 1;
        goto L_0x016a;
    L_0x0165:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        r0 = 0;
    L_0x016a:
        r7 = 45;
        r10 = r1.currentWallpaper;
        r12 = r10 instanceof org.telegram.tgnet.TLRPC.TL_wallPaper;
        if (r12 == 0) goto L_0x017c;
    L_0x0172:
        r10 = (org.telegram.tgnet.TLRPC.TL_wallPaper) r10;
        r8 = r10.slug;
    L_0x0176:
        r7 = 0;
        r10 = 0;
        r12 = 45;
    L_0x017a:
        r14 = 0;
        goto L_0x01c8;
    L_0x017c:
        r12 = r10 instanceof org.telegram.ui.WallpapersListActivity.ColorWallpaper;
        if (r12 == 0) goto L_0x0193;
    L_0x0180:
        r10 = (org.telegram.ui.WallpapersListActivity.ColorWallpaper) r10;
        r7 = r1.selectedPattern;
        if (r7 == 0) goto L_0x0189;
    L_0x0186:
        r7 = r7.slug;
        goto L_0x018b;
    L_0x0189:
        r7 = "c";
    L_0x018b:
        r8 = r7;
        r7 = r1.backgroundColor;
        r10 = r1.backgroundGradientColor;
        r12 = r1.backgroundRotation;
        goto L_0x017a;
    L_0x0193:
        r12 = r10 instanceof org.telegram.ui.WallpapersListActivity.FileWallpaper;
        if (r12 == 0) goto L_0x01a3;
    L_0x0197:
        r10 = (org.telegram.ui.WallpapersListActivity.FileWallpaper) r10;
        r8 = r10.slug;
        r10 = r10.path;
        r14 = r10;
    L_0x019e:
        r7 = 0;
        r10 = 0;
        r12 = 45;
        goto L_0x01c8;
    L_0x01a3:
        r12 = r10 instanceof org.telegram.messenger.MediaController.SearchImage;
        if (r12 == 0) goto L_0x01c5;
    L_0x01a7:
        r10 = (org.telegram.messenger.MediaController.SearchImage) r10;
        r12 = r10.photo;
        if (r12 == 0) goto L_0x01ba;
    L_0x01ad:
        r8 = r12.sizes;
        r10 = r1.maxWallpaperSize;
        r8 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r8, r10, r11);
        r8 = org.telegram.messenger.FileLoader.getPathToAttach(r8, r11);
        goto L_0x01c0;
    L_0x01ba:
        r10 = r10.imageUrl;
        r8 = org.telegram.messenger.ImageLoader.getHttpFilePath(r10, r8);
    L_0x01c0:
        r10 = "";
        r14 = r8;
        r8 = r10;
        goto L_0x019e;
    L_0x01c5:
        r8 = "d";
        goto L_0x0176;
    L_0x01c8:
        r15 = new org.telegram.ui.ActionBar.Theme$OverrideWallpaperInfo;
        r15.<init>();
        r15.fileName = r4;
        r15.originalFileName = r2;
        r15.slug = r8;
        r2 = r1.isBlurred;
        r15.isBlurred = r2;
        r2 = r1.isMotion;
        r15.isMotion = r2;
        r15.color = r7;
        r15.gradientColor = r10;
        r15.rotation = r12;
        r2 = r1.currentIntensity;
        r15.intensity = r2;
        r2 = r1.currentAccount;
        r13 = org.telegram.messenger.MessagesController.getInstance(r2);
        if (r8 == 0) goto L_0x01f0;
    L_0x01ed:
        r16 = 1;
        goto L_0x01f2;
    L_0x01f0:
        r16 = 0;
    L_0x01f2:
        r17 = 0;
        r2 = r15;
        r13.saveWallpaperToServer(r14, r15, r16, r17);
        if (r0 == 0) goto L_0x0237;
    L_0x01fa:
        r0 = "chat_serviceBackground";
        r0 = org.telegram.ui.ActionBar.Theme.getColor(r0);
        org.telegram.ui.ActionBar.Theme.serviceMessageColorBackup = r0;
        r0 = r2.slug;
        r0 = r9.equals(r0);
        if (r0 == 0) goto L_0x020b;
    L_0x020a:
        r2 = 0;
    L_0x020b:
        r0 = org.telegram.ui.ActionBar.Theme.getActiveTheme();
        r0.setOverrideWallpaper(r2);
        org.telegram.ui.ActionBar.Theme.reloadWallpaper();
        if (r3 != 0) goto L_0x0237;
    L_0x0217:
        r0 = org.telegram.messenger.ImageLoader.getInstance();
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = r6.getAbsolutePath();
        r3 = org.telegram.messenger.ImageLoader.getHttpFileName(r3);
        r2.append(r3);
        r3 = "@100_100";
        r2.append(r3);
        r2 = r2.toString();
        r0.removeImage(r2);
    L_0x0237:
        r0 = r1.delegate;
        if (r0 == 0) goto L_0x023e;
    L_0x023b:
        r0.didSetNewBackground();
    L_0x023e:
        r19.finishFragment();
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemePreviewActivity.lambda$createView$5$ThemePreviewActivity(android.view.View):void");
    }

    public /* synthetic */ void lambda$createView$6$ThemePreviewActivity(int i, WallpaperCheckBoxView wallpaperCheckBoxView, View view) {
        if (this.buttonsContainer.getAlpha() != 1.0f || this.patternViewAnimation != null) {
            return;
        }
        if ((this.screenType == 1 && i == 0) || ((this.currentWallpaper instanceof ColorWallpaper) && i == 2)) {
            wallpaperCheckBoxView.setChecked(wallpaperCheckBoxView.isChecked() ^ 1, true);
            this.isMotion = wallpaperCheckBoxView.isChecked();
            this.parallaxEffect.setEnabled(this.isMotion);
            animateMotionChange();
            return;
        }
        boolean z = false;
        if (i == 1 && (this.screenType == 1 || (this.currentWallpaper instanceof ColorWallpaper))) {
            if (this.checkBoxView[1].isChecked()) {
                this.lastSelectedPattern = this.selectedPattern;
                this.backgroundImage.setImageDrawable(null);
                this.selectedPattern = null;
                this.isMotion = false;
                updateButtonState(false, true);
                animateMotionChange();
                if (this.patternLayout[1].getVisibility() == 0) {
                    if (this.screenType == 1) {
                        showPatternsView(0, true);
                    } else {
                        showPatternsView(i, this.patternLayout[i].getVisibility() != 0);
                    }
                }
            } else {
                selectPattern(this.lastSelectedPattern != null ? -1 : 0);
                if (this.screenType == 1) {
                    showPatternsView(1, true);
                } else {
                    showPatternsView(i, this.patternLayout[i].getVisibility() != 0);
                }
            }
            WallpaperCheckBoxView wallpaperCheckBoxView2 = this.checkBoxView[1];
            if (this.selectedPattern != null) {
                z = true;
            }
            wallpaperCheckBoxView2.setChecked(z, true);
            updateSelectedPattern(true);
            this.patternsListView.invalidateViews();
            updateMotionButton();
        } else if (this.currentWallpaper instanceof ColorWallpaper) {
            if (this.patternLayout[i].getVisibility() != 0) {
                z = true;
            }
            showPatternsView(i, z);
        } else {
            wallpaperCheckBoxView.setChecked(wallpaperCheckBoxView.isChecked() ^ 1, true);
            if (i == 0) {
                this.isBlurred = wallpaperCheckBoxView.isChecked();
                updateBlurred();
                return;
            }
            this.isMotion = wallpaperCheckBoxView.isChecked();
            this.parallaxEffect.setEnabled(this.isMotion);
            animateMotionChange();
        }
    }

    public /* synthetic */ void lambda$createView$7$ThemePreviewActivity(int i, View view) {
        if (this.patternViewAnimation == null) {
            if (i == 0) {
                this.backgroundRotation = this.previousBackgroundRotation;
                setBackgroundColor(this.previousBackgroundGradientColor, 1, true);
                setBackgroundColor(this.previousBackgroundColor, 0, true);
            } else {
                this.selectedPattern = this.previousSelectedPattern;
                TL_wallPaper tL_wallPaper = this.selectedPattern;
                if (tL_wallPaper == null) {
                    this.backgroundImage.setImageDrawable(null);
                } else {
                    BackupImageView backupImageView = this.backgroundImage;
                    ImageLocation forDocument = ImageLocation.getForDocument(tL_wallPaper.document);
                    String str = this.imageFilter;
                    TL_wallPaper tL_wallPaper2 = this.selectedPattern;
                    backupImageView.setImage(forDocument, str, null, null, "jpg", tL_wallPaper2.document.size, 1, tL_wallPaper2);
                }
                this.checkBoxView[1].setChecked(this.selectedPattern != null, false);
                this.currentIntensity = this.previousIntensity;
                this.intensitySeekBar.setProgress(this.currentIntensity);
                this.backgroundImage.getImageReceiver().setAlpha(this.currentIntensity);
                updateButtonState(false, true);
                updateSelectedPattern(true);
            }
            if (this.screenType == 2) {
                showPatternsView(i, false);
            } else {
                if (this.selectedPattern == null) {
                    if (this.isMotion) {
                        this.isMotion = false;
                        this.checkBoxView[0].setChecked(false, true);
                        animateMotionChange();
                    }
                    updateMotionButton();
                }
                showPatternsView(0, true);
            }
        }
    }

    public /* synthetic */ void lambda$createView$8$ThemePreviewActivity(int i, View view) {
        if (this.patternViewAnimation == null) {
            if (this.screenType == 2) {
                showPatternsView(i, false);
            } else {
                showPatternsView(0, true);
            }
        }
    }

    public /* synthetic */ void lambda$createView$9$ThemePreviewActivity(View view, int i) {
        Object obj = this.selectedPattern != null ? 1 : null;
        selectPattern(i);
        if (obj == (this.selectedPattern == null ? 1 : null)) {
            animateMotionChange();
            updateMotionButton();
        }
        updateSelectedPattern(true);
        this.checkBoxView[1].setChecked(this.selectedPattern != null, true);
        this.patternsListView.invalidateViews();
        i = view.getLeft();
        int right = view.getRight();
        int dp = AndroidUtilities.dp(52.0f);
        i -= dp;
        if (i < 0) {
            this.patternsListView.smoothScrollBy(i, 0);
            return;
        }
        right += dp;
        if (right > this.patternsListView.getMeasuredWidth()) {
            RecyclerListView recyclerListView = this.patternsListView;
            recyclerListView.smoothScrollBy(right - recyclerListView.getMeasuredWidth(), 0);
        }
    }

    public /* synthetic */ void lambda$createView$10$ThemePreviewActivity() {
        this.watchForKeyboardEndTime = SystemClock.uptimeMillis() + 1500;
        this.frameLayout.invalidate();
    }

    public /* synthetic */ void lambda$createView$11$ThemePreviewActivity(View view) {
        cancelThemeApply(false);
    }

    /* JADX WARNING: Removed duplicated region for block: B:9:0x0033  */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x001b  */
    /* JADX WARNING: Removed duplicated region for block: B:14:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x005e  */
    public /* synthetic */ void lambda$createView$12$ThemePreviewActivity(android.view.View r8) {
        /*
        r7 = this;
        r8 = org.telegram.ui.ActionBar.Theme.getPreviousTheme();
        r0 = 0;
        if (r8 == 0) goto L_0x0014;
    L_0x0007:
        r1 = r8.prevAccentId;
        if (r1 < 0) goto L_0x0014;
    L_0x000b:
        r2 = r8.themeAccentsMap;
        r1 = r2.get(r1);
        r1 = (org.telegram.ui.ActionBar.Theme.ThemeAccent) r1;
        goto L_0x0015;
    L_0x0014:
        r1 = r0;
    L_0x0015:
        r2 = r7.accent;
        r3 = 1;
        r4 = 0;
        if (r2 == 0) goto L_0x0033;
    L_0x001b:
        r7.saveAccentWallpaper();
        r0 = r7.applyingTheme;
        org.telegram.ui.ActionBar.Theme.saveThemeAccents(r0, r3, r4, r4, r4);
        org.telegram.ui.ActionBar.Theme.applyPreviousTheme();
        r0 = r7.applyingTheme;
        r2 = r7.nightTheme;
        org.telegram.ui.ActionBar.Theme.applyTheme(r0, r2);
        r0 = r7.parentLayout;
        r0.rebuildAllFragmentViews(r4, r4);
        goto L_0x0057;
    L_0x0033:
        r2 = r7.parentLayout;
        r2.rebuildAllFragmentViews(r4, r4);
        r2 = new java.io.File;
        r5 = r7.applyingTheme;
        r5 = r5.pathToFile;
        r2.<init>(r5);
        r5 = r7.applyingTheme;
        r6 = r5.name;
        r5 = r5.info;
        org.telegram.ui.ActionBar.Theme.applyThemeFile(r2, r6, r5, r4);
        r2 = r7.applyingTheme;
        r2 = r2.account;
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);
        r5 = r7.applyingTheme;
        r2.saveTheme(r5, r0, r4, r4);
    L_0x0057:
        r7.finishFragment();
        r0 = r7.screenType;
        if (r0 != 0) goto L_0x0077;
    L_0x005e:
        r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
        r2 = org.telegram.messenger.NotificationCenter.didApplyNewTheme;
        r5 = 3;
        r5 = new java.lang.Object[r5];
        r5[r4] = r8;
        r5[r3] = r1;
        r8 = 2;
        r1 = r7.deleteOnCancel;
        r1 = java.lang.Boolean.valueOf(r1);
        r5[r8] = r1;
        r0.postNotificationName(r2, r5);
    L_0x0077:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemePreviewActivity.lambda$createView$12$ThemePreviewActivity(android.view.View):void");
    }

    private void selectColorType(int i) {
        int i2 = i;
        if (!(getParentActivity() == null || this.colorType == i2 || this.patternViewAnimation != null)) {
            if (i2 == 2 && (Theme.hasCustomWallpaper() || this.accent.backgroundOverrideColor == 4294967296L)) {
                Builder builder = new Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("ChangeChatBackground", NUM));
                builder.setMessage(LocaleController.getString("ChangeWallpaperToColor", NUM));
                builder.setPositiveButton(LocaleController.getString("Change", NUM), new -$$Lambda$ThemePreviewActivity$Ua1Vy0xkaITDckMpvhn6OFq2c9Y(this));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                showDialog(builder.create());
                return;
            }
            int i3 = this.colorType;
            this.colorType = i2;
            if (i2 == 1) {
                this.dropDown.setText(LocaleController.getString("ColorPickerMainColor", NUM));
                this.colorPicker.setType(1, hasChanges(1), false, false, false, 0, false);
                this.colorPicker.setColor(this.accent.accentColor, 0);
            } else if (i2 == 2) {
                this.dropDown.setText(LocaleController.getString("ColorPickerBackground", NUM));
                int color = Theme.getColor("chat_wallpaper");
                String str = "chat_wallpaper_gradient_to";
                int color2 = Theme.hasThemeKey(str) ? Theme.getColor(str) : 0;
                long j = this.accent.backgroundGradientOverrideColor;
                int i4 = (int) j;
                if (i4 == 0 && j != 0) {
                    color2 = 0;
                }
                int i5 = (int) this.accent.backgroundOverrideColor;
                ColorPicker colorPicker = this.colorPicker;
                boolean hasChanges = hasChanges(2);
                boolean z = (i4 == 0 && color2 == 0) ? false : true;
                colorPicker.setType(2, hasChanges, true, z, false, this.accent.backgroundRotation, false);
                ColorPicker colorPicker2 = this.colorPicker;
                if (i4 != 0) {
                    color2 = i4;
                }
                colorPicker2.setColor(color2, 1);
                ColorPicker colorPicker3 = this.colorPicker;
                if (i5 != 0) {
                    color = i5;
                }
                colorPicker3.setColor(color, 0);
                this.messagesAdapter.notifyItemInserted(0);
                this.listView2.smoothScrollBy(0, AndroidUtilities.dp(60.0f));
            } else if (i2 == 3) {
                this.dropDown.setText(LocaleController.getString("ColorPickerMyMessages", NUM));
                this.colorPicker.setType(2, hasChanges(3), true, this.accent.myMessagesGradientAccentColor != 0, true, 0, false);
                this.colorPicker.setColor(this.accent.myMessagesGradientAccentColor, 1);
                ColorPicker colorPicker4 = this.colorPicker;
                ThemeAccent themeAccent = this.accent;
                int i6 = themeAccent.myMessagesAccentColor;
                if (i6 == 0) {
                    i6 = themeAccent.accentColor;
                }
                colorPicker4.setColor(i6, 0);
            }
            if (i2 == 1 || i2 == 3) {
                if (i3 == 2) {
                    this.messagesAdapter.notifyItemRemoved(0);
                    if (this.patternLayout[1].getVisibility() == 0) {
                        showPatternsView(0, true);
                    }
                }
                if (this.applyingTheme.isDark()) {
                    this.colorPicker.setMinBrightness(0.2f);
                } else {
                    this.colorPicker.setMinBrightness(0.05f);
                    this.colorPicker.setMaxBrightness(0.8f);
                }
            } else {
                this.colorPicker.setMinBrightness(0.0f);
                this.colorPicker.setMaxBrightness(1.0f);
            }
        }
    }

    public /* synthetic */ void lambda$selectColorType$13$ThemePreviewActivity(DialogInterface dialogInterface, int i) {
        ThemeAccent themeAccent = this.accent;
        if (themeAccent.backgroundOverrideColor == 4294967296L) {
            themeAccent.backgroundOverrideColor = 0;
            themeAccent.backgroundGradientOverrideColor = 0;
            Theme.refreshThemeColors();
        }
        this.removeBackgroundOverride = true;
        Theme.resetCustomWallpaper(true);
        selectColorType(2);
    }

    private void selectPattern(int i) {
        WallPaper wallPaper;
        if (i < 0 || i >= this.patterns.size()) {
            wallPaper = this.lastSelectedPattern;
        } else {
            wallPaper = (TL_wallPaper) this.patterns.get(i);
        }
        if (wallPaper != null) {
            this.backgroundImage.setImage(ImageLocation.getForDocument(wallPaper.document), this.imageFilter, null, null, "jpg", wallPaper.document.size, 1, wallPaper);
            this.selectedPattern = wallPaper;
            if (this.screenType == 1) {
                this.isMotion = this.checkBoxView[0].isChecked();
            } else {
                this.isMotion = this.checkBoxView[2].isChecked();
            }
            updateButtonState(false, true);
        }
    }

    private void updateCheckboxes() {
        WallpaperCheckBoxView[] wallpaperCheckBoxViewArr = this.checkBoxView;
        if (wallpaperCheckBoxViewArr != null) {
            boolean z = true;
            if (this.screenType == 1) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) wallpaperCheckBoxViewArr[1].getLayoutParams();
                this.checkBoxView[1].setChecked(this.selectedPattern != null, false);
                int dp = (layoutParams.width + AndroidUtilities.dp(9.0f)) / 2;
                float f = 0.0f;
                this.checkBoxView[1].setTranslationX(this.selectedPattern != null ? 0.0f : (float) (-dp));
                this.checkBoxView[0].setTranslationX(this.selectedPattern != null ? 0.0f : (float) dp);
                this.checkBoxView[0].setChecked(this.isMotion, false);
                View view = this.checkBoxView[0];
                if (this.selectedPattern == null) {
                    z = false;
                }
                view.setEnabled(z);
                this.checkBoxView[0].setVisibility(this.selectedPattern != null ? 0 : 4);
                view = this.checkBoxView[0];
                if (this.selectedPattern != null) {
                    f = 1.0f;
                }
                view.setAlpha(f);
            }
        }
    }

    private void saveAccentWallpaper() {
        ThemeAccent themeAccent = this.accent;
        if (themeAccent != null && !TextUtils.isEmpty(themeAccent.patternSlug)) {
            try {
                File pathToWallpaper = this.accent.getPathToWallpaper();
                Drawable background = this.backgroundImage.getBackground();
                Bitmap bitmap = this.backgroundImage.getImageReceiver().getBitmap();
                Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
                Canvas canvas = new Canvas(createBitmap);
                background.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                background.draw(canvas);
                Paint paint = new Paint(2);
                paint.setColorFilter(new PorterDuffColorFilter(this.patternColor, this.blendMode));
                paint.setAlpha((int) (this.currentIntensity * 255.0f));
                canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint);
                FileOutputStream fileOutputStream = new FileOutputStream(pathToWallpaper);
                createBitmap.compress(CompressFormat.JPEG, 87, fileOutputStream);
                fileOutputStream.close();
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
    }

    private boolean hasChanges(int i) {
        if (this.editingTheme) {
            return false;
        }
        int defaultAccentColor;
        if (i == 1 || i == 2) {
            int i2;
            long j = this.backupBackgroundOverrideColor;
            if (j == 0) {
                defaultAccentColor = Theme.getDefaultAccentColor("chat_wallpaper");
                i2 = (int) this.accent.backgroundOverrideColor;
                if (i2 == 0) {
                    i2 = defaultAccentColor;
                }
                if (i2 != defaultAccentColor) {
                    return true;
                }
            } else if (j != this.accent.backgroundOverrideColor) {
                return true;
            }
            j = this.backupBackgroundGradientOverrideColor;
            if (j == 0) {
                defaultAccentColor = Theme.getDefaultAccentColor("chat_wallpaper_gradient_to");
                long j2 = this.accent.backgroundGradientOverrideColor;
                i2 = (int) j2;
                if (i2 == 0 && j2 != 0) {
                    i2 = 0;
                } else if (i2 == 0) {
                    i2 = defaultAccentColor;
                }
                if (i2 != defaultAccentColor) {
                    return true;
                }
            } else if (j != this.accent.backgroundGradientOverrideColor) {
                return true;
            }
            if (this.accent.backgroundRotation != this.backupBackgroundRotation) {
                return true;
            }
        }
        if (i == 1 || i == 3) {
            i = this.backupMyMessagesAccentColor;
            if (i == 0) {
                ThemeAccent themeAccent = this.accent;
                defaultAccentColor = themeAccent.myMessagesAccentColor;
                if (!(defaultAccentColor == 0 || defaultAccentColor == themeAccent.accentColor)) {
                    return true;
                }
            } else if (i != this.accent.myMessagesAccentColor) {
                return true;
            }
            i = this.backupMyMessagesGradientAccentColor;
            if (i != 0) {
                if (i != this.accent.myMessagesGradientAccentColor) {
                    return true;
                }
            } else if (this.accent.myMessagesGradientAccentColor != 0) {
                return true;
            }
        }
        return false;
    }

    /* JADX WARNING: Missing block: B:23:0x004a, code skipped:
            if (r7.accent.patternMotion == r7.isMotion) goto L_0x004c;
     */
    /* JADX WARNING: Missing block: B:27:0x0058, code skipped:
            if (r7.accent.patternIntensity == r7.currentIntensity) goto L_0x00a6;
     */
    private boolean checkDiscard() {
        /*
        r7 = this;
        r0 = r7.screenType;
        r1 = 1;
        if (r0 != r1) goto L_0x00a6;
    L_0x0005:
        r0 = r7.accent;
        r2 = r0.accentColor;
        r3 = r7.backupAccentColor;
        if (r2 != r3) goto L_0x005a;
    L_0x000d:
        r2 = r0.myMessagesAccentColor;
        r3 = r7.backupMyMessagesAccentColor;
        if (r2 != r3) goto L_0x005a;
    L_0x0013:
        r2 = r0.myMessagesGradientAccentColor;
        r3 = r7.backupMyMessagesGradientAccentColor;
        if (r2 != r3) goto L_0x005a;
    L_0x0019:
        r2 = r0.backgroundOverrideColor;
        r4 = r7.backupBackgroundOverrideColor;
        r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r6 != 0) goto L_0x005a;
    L_0x0021:
        r2 = r0.backgroundGradientOverrideColor;
        r4 = r7.backupBackgroundGradientOverrideColor;
        r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r6 != 0) goto L_0x005a;
    L_0x0029:
        r2 = r0.backgroundRotation;
        r3 = r7.backupBackgroundRotation;
        if (r2 != r3) goto L_0x005a;
    L_0x002f:
        r0 = r0.patternSlug;
        r2 = r7.selectedPattern;
        if (r2 == 0) goto L_0x0038;
    L_0x0035:
        r2 = r2.slug;
        goto L_0x003a;
    L_0x0038:
        r2 = "";
    L_0x003a:
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x005a;
    L_0x0040:
        r0 = r7.selectedPattern;
        if (r0 == 0) goto L_0x004c;
    L_0x0044:
        r0 = r7.accent;
        r0 = r0.patternMotion;
        r2 = r7.isMotion;
        if (r0 != r2) goto L_0x005a;
    L_0x004c:
        r0 = r7.selectedPattern;
        if (r0 == 0) goto L_0x00a6;
    L_0x0050:
        r0 = r7.accent;
        r0 = r0.patternIntensity;
        r2 = r7.currentIntensity;
        r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r0 == 0) goto L_0x00a6;
    L_0x005a:
        r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r1 = r7.getParentActivity();
        r0.<init>(r1);
        r1 = NUM; // 0x7f0e09a8 float:1.8880051E38 double:1.053163378E-314;
        r2 = "SaveChangesAlertTitle";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r0.setTitle(r1);
        r1 = NUM; // 0x7f0e09a7 float:1.888005E38 double:1.0531633775E-314;
        r2 = "SaveChangesAlertText";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r0.setMessage(r1);
        r1 = NUM; // 0x7f0e09a6 float:1.8880047E38 double:1.053163377E-314;
        r2 = "Save";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r2 = new org.telegram.ui.-$$Lambda$ThemePreviewActivity$VhuCSQ0ekK36xL0S9AHA-K57wyo;
        r2.<init>(r7);
        r0.setPositiveButton(r1, r2);
        r1 = NUM; // 0x7f0e07e2 float:1.887913E38 double:1.0531631537E-314;
        r2 = "PassportDiscard";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r2 = new org.telegram.ui.-$$Lambda$ThemePreviewActivity$6MCYHNXt8AHlTpbo4JBN0Y_054c;
        r2.<init>(r7);
        r0.setNegativeButton(r1, r2);
        r0 = r0.create();
        r7.showDialog(r0);
        r0 = 0;
        return r0;
    L_0x00a6:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemePreviewActivity.checkDiscard():boolean");
    }

    public /* synthetic */ void lambda$checkDiscard$14$ThemePreviewActivity(DialogInterface dialogInterface, int i) {
        this.actionBar2.getActionBarMenuOnItemClick().onItemClick(4);
    }

    public /* synthetic */ void lambda$checkDiscard$15$ThemePreviewActivity(DialogInterface dialogInterface, int i) {
        cancelThemeApply(false);
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
        if (this.screenType == 1) {
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
        }
        if (this.screenType == 0 && this.accent == null) {
            this.isMotion = Theme.isWallpaperMotion();
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append((int) (1080.0f / AndroidUtilities.density));
            stringBuilder.append("_");
            stringBuilder.append((int) (1920.0f / AndroidUtilities.density));
            stringBuilder.append("_f");
            this.imageFilter = stringBuilder.toString();
            Point point = AndroidUtilities.displaySize;
            this.maxWallpaperSize = Math.min(1920, Math.max(point.x, point.y));
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.wallpapersNeedReload);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.wallpapersDidLoad);
            this.TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
            if (this.patterns == null) {
                this.patterns = new ArrayList();
                MessagesStorage.getInstance(this.currentAccount).getWallpapers();
            }
        }
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
        FrameLayout frameLayout = this.frameLayout;
        if (!(frameLayout == null || this.onGlobalLayoutListener == null)) {
            frameLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this.onGlobalLayoutListener);
        }
        int i = this.screenType;
        if (i == 2) {
            Bitmap bitmap = this.blurredBitmap;
            if (bitmap != null) {
                bitmap.recycle();
                this.blurredBitmap = null;
            }
            Theme.applyChatServiceMessageColor();
        } else if (i == 1) {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
        }
        if (!(this.screenType == 0 && this.accent == null)) {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.wallpapersNeedReload);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.wallpapersDidLoad);
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
        if (this.isMotion) {
            this.parallaxEffect.setEnabled(true);
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onPause() {
        super.onPause();
        if (this.isMotion) {
            this.parallaxEffect.setEnabled(false);
        }
    }

    public void onFailedDownload(String str, boolean z) {
        updateButtonState(true, z);
    }

    public void onSuccessDownload(String str) {
        RadialProgress2 radialProgress2 = this.radialProgress;
        if (radialProgress2 != null) {
            radialProgress2.setProgress(1.0f, this.progressVisible);
        }
        updateButtonState(false, true);
    }

    public void onProgressDownload(String str, float f) {
        RadialProgress2 radialProgress2 = this.radialProgress;
        if (radialProgress2 != null) {
            radialProgress2.setProgress(f, this.progressVisible);
            if (this.radialProgress.getIcon() != 10) {
                updateButtonState(false, true);
            }
        }
    }

    public int getObserverTag() {
        return this.TAG;
    }

    private void updateBlurred() {
        Bitmap bitmap;
        if (this.isBlurred && this.blurredBitmap == null) {
            bitmap = this.currentWallpaperBitmap;
            if (bitmap != null) {
                this.originalBitmap = bitmap;
                this.blurredBitmap = Utilities.blurWallpaper(bitmap);
            } else {
                ImageReceiver imageReceiver = this.backgroundImage.getImageReceiver();
                if (imageReceiver.hasNotThumb() || imageReceiver.hasStaticThumb()) {
                    this.originalBitmap = imageReceiver.getBitmap();
                    this.blurredBitmap = Utilities.blurWallpaper(imageReceiver.getBitmap());
                }
            }
        }
        if (this.isBlurred) {
            bitmap = this.blurredBitmap;
            if (bitmap != null) {
                this.backgroundImage.setImageBitmap(bitmap);
                return;
            }
            return;
        }
        setCurrentImage(false);
    }

    public boolean onBackPressed() {
        if (!checkDiscard()) {
            return false;
        }
        cancelThemeApply(true);
        return true;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        int i3 = 0;
        if (i == NotificationCenter.emojiDidLoad) {
            RecyclerListView recyclerListView = this.listView;
            if (recyclerListView != null) {
                i = recyclerListView.getChildCount();
                for (i2 = 0; i2 < i; i2++) {
                    View childAt = this.listView.getChildAt(i2);
                    if (childAt instanceof DialogCell) {
                        ((DialogCell) childAt).update(0);
                    }
                }
            }
        } else if (i == NotificationCenter.didSetNewWallpapper) {
            if (this.page2 != null) {
                setCurrentImage(true);
            }
        } else if (i == NotificationCenter.wallpapersNeedReload) {
            Object obj = this.currentWallpaper;
            if (obj instanceof FileWallpaper) {
                FileWallpaper fileWallpaper = (FileWallpaper) obj;
                if (fileWallpaper.slug == null) {
                    fileWallpaper.slug = (String) objArr[0];
                }
            }
        } else if (i == NotificationCenter.wallpapersDidLoad) {
            ArrayList arrayList = (ArrayList) objArr[0];
            this.patterns.clear();
            i2 = arrayList.size();
            Object obj2 = null;
            for (int i4 = 0; i4 < i2; i4++) {
                TL_wallPaper tL_wallPaper = (TL_wallPaper) arrayList.get(i4);
                if (tL_wallPaper.pattern) {
                    this.patterns.add(tL_wallPaper);
                    ThemeAccent themeAccent = this.accent;
                    if (themeAccent != null && themeAccent.patternSlug.equals(tL_wallPaper.slug)) {
                        this.selectedPattern = tL_wallPaper;
                        setCurrentImage(false);
                        updateButtonState(false, false);
                        updateCheckboxes();
                        obj2 = 1;
                    }
                }
            }
            if (obj2 == null) {
                TL_wallPaper tL_wallPaper2 = this.selectedPattern;
                if (tL_wallPaper2 != null) {
                    this.patterns.add(0, tL_wallPaper2);
                }
            }
            PatternsAdapter patternsAdapter = this.patternsAdapter;
            if (patternsAdapter != null) {
                patternsAdapter.notifyDataSetChanged();
            }
            long j = 0;
            int size = arrayList.size();
            while (i3 < size) {
                long j2 = ((TL_wallPaper) arrayList.get(i3)).id;
                j = (((((((j * 20261) + 2147483648L) + ((long) ((int) (j2 >> 32)))) % 2147483648L) * 20261) + 2147483648L) + ((long) ((int) j2))) % 2147483648L;
                i3++;
            }
            TL_account_getWallPapers tL_account_getWallPapers = new TL_account_getWallPapers();
            tL_account_getWallPapers.hash = (int) j;
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_getWallPapers, new -$$Lambda$ThemePreviewActivity$jwuubXLjV2DOEKcd1H91K4q9q5Q(this)), this.classGuid);
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$19$ThemePreviewActivity(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$ThemePreviewActivity$oNfZC9XiXGgSHfmZ3Ipn_Xg_78A(this, tLObject));
    }

    public /* synthetic */ void lambda$null$18$ThemePreviewActivity(TLObject tLObject) {
        if (tLObject instanceof TL_account_wallPapers) {
            TL_account_wallPapers tL_account_wallPapers = (TL_account_wallPapers) tLObject;
            this.patterns.clear();
            int size = tL_account_wallPapers.wallpapers.size();
            Object obj = null;
            for (int i = 0; i < size; i++) {
                TL_wallPaper tL_wallPaper = (TL_wallPaper) tL_account_wallPapers.wallpapers.get(i);
                if (tL_wallPaper.pattern) {
                    this.patterns.add(tL_wallPaper);
                    ThemeAccent themeAccent = this.accent;
                    if (themeAccent != null && themeAccent.patternSlug.equals(tL_wallPaper.slug)) {
                        this.selectedPattern = tL_wallPaper;
                        setCurrentImage(false);
                        updateButtonState(false, false);
                        updateCheckboxes();
                        obj = 1;
                    }
                }
            }
            if (obj == null) {
                TL_wallPaper tL_wallPaper2 = this.selectedPattern;
                if (tL_wallPaper2 != null) {
                    this.patterns.add(0, tL_wallPaper2);
                }
            }
            PatternsAdapter patternsAdapter = this.patternsAdapter;
            if (patternsAdapter != null) {
                patternsAdapter.notifyDataSetChanged();
            }
            MessagesStorage.getInstance(this.currentAccount).putWallpapers(tL_account_wallPapers.wallpapers, 1);
        }
        if (this.selectedPattern == null) {
            ThemeAccent themeAccent2 = this.accent;
            if (themeAccent2 != null && !TextUtils.isEmpty(themeAccent2.patternSlug)) {
                TL_account_getWallPaper tL_account_getWallPaper = new TL_account_getWallPaper();
                TL_inputWallPaperSlug tL_inputWallPaperSlug = new TL_inputWallPaperSlug();
                tL_inputWallPaperSlug.slug = this.accent.patternSlug;
                tL_account_getWallPaper.wallpaper = tL_inputWallPaperSlug;
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(getConnectionsManager().sendRequest(tL_account_getWallPaper, new -$$Lambda$ThemePreviewActivity$FLxgtJIEQMFfRtLB25WVCLASSNAMEMcPw(this)), this.classGuid);
            }
        }
    }

    public /* synthetic */ void lambda$null$17$ThemePreviewActivity(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$ThemePreviewActivity$iet83enWJgayDe97kbpOEmbDnAs(this, tLObject));
    }

    public /* synthetic */ void lambda$null$16$ThemePreviewActivity(TLObject tLObject) {
        if (tLObject instanceof TL_wallPaper) {
            TL_wallPaper tL_wallPaper = (TL_wallPaper) tLObject;
            if (tL_wallPaper.pattern) {
                this.selectedPattern = tL_wallPaper;
                setCurrentImage(false);
                updateButtonState(false, false);
                updateCheckboxes();
                this.patterns.add(0, this.selectedPattern);
                PatternsAdapter patternsAdapter = this.patternsAdapter;
                if (patternsAdapter != null) {
                    patternsAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    private void cancelThemeApply(boolean z) {
        if (this.screenType == 2) {
            if (!z) {
                finishFragment();
            }
            return;
        }
        Theme.applyPreviousTheme();
        if (this.screenType == 1) {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
            if (this.editingTheme) {
                ThemeAccent themeAccent = this.accent;
                themeAccent.accentColor = this.backupAccentColor;
                themeAccent.myMessagesAccentColor = this.backupMyMessagesAccentColor;
                themeAccent.myMessagesGradientAccentColor = this.backupMyMessagesGradientAccentColor;
                themeAccent.backgroundOverrideColor = this.backupBackgroundOverrideColor;
                themeAccent.backgroundGradientOverrideColor = this.backupBackgroundGradientOverrideColor;
                themeAccent.backgroundRotation = this.backupBackgroundRotation;
            }
            Theme.saveThemeAccents(this.applyingTheme, false, true, false, false);
        } else {
            if (this.accent != null) {
                Theme.saveThemeAccents(this.applyingTheme, false, this.deleteOnCancel, false, false);
            }
            this.parentLayout.rebuildAllFragmentViews(false, false);
            if (this.deleteOnCancel) {
                ThemeInfo themeInfo = this.applyingTheme;
                if (!(themeInfo.pathToFile == null || Theme.isThemeInstalled(themeInfo))) {
                    new File(this.applyingTheme.pathToFile).delete();
                }
            }
        }
        if (!z) {
            finishFragment();
        }
    }

    private int getButtonsColor(String str) {
        return this.useDefaultThemeForButtons ? Theme.getDefaultColor(str) : Theme.getColor(str);
    }

    private void scheduleApplyColor(int i, int i2, boolean z) {
        if (i2 == -1) {
            i = this.colorType;
            if (i == 1 || i == 2) {
                long j = this.backupBackgroundOverrideColor;
                if (j != 0) {
                    this.accent.backgroundOverrideColor = j;
                } else {
                    this.accent.backgroundOverrideColor = 0;
                }
                j = this.backupBackgroundGradientOverrideColor;
                if (j != 0) {
                    this.accent.backgroundGradientOverrideColor = j;
                } else {
                    this.accent.backgroundGradientOverrideColor = 0;
                }
                this.accent.backgroundRotation = this.backupBackgroundRotation;
                if (this.colorType == 2) {
                    i = Theme.getDefaultAccentColor("chat_wallpaper");
                    i2 = Theme.getDefaultAccentColor("chat_wallpaper_gradient_to");
                    ThemeAccent themeAccent = this.accent;
                    int i3 = (int) themeAccent.backgroundGradientOverrideColor;
                    int i4 = (int) themeAccent.backgroundOverrideColor;
                    ColorPicker colorPicker = this.colorPicker;
                    if (i3 != 0) {
                        i2 = i3;
                    }
                    colorPicker.setColor(i2, 1);
                    ColorPicker colorPicker2 = this.colorPicker;
                    if (i4 != 0) {
                        i = i4;
                    }
                    colorPicker2.setColor(i, 0);
                }
            }
            i = this.colorType;
            if (i == 1 || i == 3) {
                i = this.backupMyMessagesAccentColor;
                if (i != 0) {
                    this.accent.myMessagesAccentColor = i;
                } else {
                    this.accent.myMessagesAccentColor = 0;
                }
                i = this.backupMyMessagesGradientAccentColor;
                if (i != 0) {
                    this.accent.myMessagesGradientAccentColor = i;
                } else {
                    this.accent.myMessagesGradientAccentColor = 0;
                }
                if (this.colorType == 3) {
                    this.colorPicker.setColor(this.accent.myMessagesGradientAccentColor, 1);
                    ColorPicker colorPicker3 = this.colorPicker;
                    ThemeAccent themeAccent2 = this.accent;
                    int i5 = themeAccent2.myMessagesAccentColor;
                    if (i5 == 0) {
                        i5 = themeAccent2.accentColor;
                    }
                    colorPicker3.setColor(i5, 0);
                }
            }
            Theme.refreshThemeColors();
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
        if (i2 == 1) {
            this.accent.accentColor = i;
            Theme.refreshThemeColors();
        } else if (i2 == 2) {
            if (this.lastPickedColorNum == 0) {
                this.accent.backgroundOverrideColor = (long) i;
            } else {
                i2 = Theme.getDefaultAccentColor("chat_wallpaper_gradient_to");
                if (i != 0 || i2 == 0) {
                    this.accent.backgroundGradientOverrideColor = (long) i;
                } else {
                    this.accent.backgroundGradientOverrideColor = 4294967296L;
                }
            }
            Theme.refreshThemeColors();
            this.colorPicker.setHasChanges(hasChanges(this.colorType));
        } else if (i2 == 3) {
            if (this.lastPickedColorNum == 0) {
                this.accent.myMessagesAccentColor = i;
            } else {
                this.accent.myMessagesGradientAccentColor = i;
            }
            Theme.refreshThemeColors();
            this.listView2.invalidateViews();
            this.colorPicker.setHasChanges(hasChanges(this.colorType));
        }
        i = this.themeDescriptions.size();
        for (int i3 = 0; i3 < i; i3++) {
            ThemeDescription themeDescription = (ThemeDescription) this.themeDescriptions.get(i3);
            themeDescription.setColor(Theme.getColor(themeDescription.getCurrentKey()), false, false);
        }
        this.listView.invalidateViews();
        this.listView2.invalidateViews();
        View view = this.dotsContainer;
        if (view != null) {
            view.invalidate();
        }
    }

    private void updateButtonState(boolean z, boolean z2) {
        TL_wallPaper tL_wallPaper = this.selectedPattern;
        if (tL_wallPaper == null) {
            tL_wallPaper = this.currentWallpaper;
        }
        boolean z3 = tL_wallPaper instanceof TL_wallPaper;
        if (z3 || (tL_wallPaper instanceof SearchImage)) {
            String attachFileName;
            File pathToAttach;
            int i;
            if (z2 && !this.progressVisible) {
                z2 = false;
            }
            if (z3) {
                tL_wallPaper = tL_wallPaper;
                attachFileName = FileLoader.getAttachFileName(tL_wallPaper.document);
                if (!TextUtils.isEmpty(attachFileName)) {
                    pathToAttach = FileLoader.getPathToAttach(tL_wallPaper.document, true);
                    i = tL_wallPaper.document.size;
                } else {
                    return;
                }
            }
            File pathToAttach2;
            String attachFileName2;
            SearchImage searchImage = (SearchImage) tL_wallPaper;
            Photo photo = searchImage.photo;
            if (photo != null) {
                PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, this.maxWallpaperSize, true);
                pathToAttach2 = FileLoader.getPathToAttach(closestPhotoSizeWithSize, true);
                attachFileName2 = FileLoader.getAttachFileName(closestPhotoSizeWithSize);
                i = closestPhotoSizeWithSize.size;
            } else {
                pathToAttach2 = ImageLoader.getHttpFilePath(searchImage.imageUrl, "jpg");
                attachFileName2 = pathToAttach2.getName();
                i = searchImage.size;
            }
            String str = attachFileName2;
            pathToAttach = pathToAttach2;
            attachFileName = str;
            if (TextUtils.isEmpty(attachFileName)) {
                return;
            }
            boolean exists = pathToAttach.exists();
            if (exists) {
                DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                RadialProgress2 radialProgress2 = this.radialProgress;
                if (radialProgress2 != null) {
                    radialProgress2.setProgress(1.0f, z2);
                    this.radialProgress.setIcon(4, z, z2);
                }
                this.backgroundImage.invalidate();
                if (this.screenType == 2) {
                    if (i != 0) {
                        this.actionBar2.setSubtitle(AndroidUtilities.formatFileSize((long) i));
                    } else {
                        this.actionBar2.setSubtitle(null);
                    }
                }
            } else {
                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(attachFileName, null, this);
                if (this.radialProgress != null) {
                    FileLoader.getInstance(this.currentAccount).isLoadingFile(attachFileName);
                    Float fileProgress = ImageLoader.getInstance().getFileProgress(attachFileName);
                    if (fileProgress != null) {
                        this.radialProgress.setProgress(fileProgress.floatValue(), z2);
                    } else {
                        this.radialProgress.setProgress(0.0f, z2);
                    }
                    this.radialProgress.setIcon(10, z, z2);
                }
                if (this.screenType == 2) {
                    this.actionBar2.setSubtitle(LocaleController.getString("LoadingFullImage", NUM));
                }
                this.backgroundImage.invalidate();
            }
            float f = 0.5f;
            if (this.selectedPattern == null) {
                FrameLayout frameLayout = this.buttonsContainer;
                if (frameLayout != null) {
                    frameLayout.setAlpha(exists ? 1.0f : 0.5f);
                }
            }
            int i2 = this.screenType;
            TextView textView;
            if (i2 == 0) {
                this.doneButton.setEnabled(exists);
                textView = this.doneButton;
                if (exists) {
                    f = 1.0f;
                }
                textView.setAlpha(f);
            } else if (i2 == 2) {
                this.bottomOverlayChat.setEnabled(exists);
                textView = this.bottomOverlayChatText;
                if (exists) {
                    f = 1.0f;
                }
                textView.setAlpha(f);
            } else {
                this.saveItem.setEnabled(exists);
                ActionBarMenuItem actionBarMenuItem = this.saveItem;
                if (exists) {
                    f = 1.0f;
                }
                actionBarMenuItem.setAlpha(f);
            }
        } else {
            RadialProgress2 radialProgress22 = this.radialProgress;
            if (radialProgress22 != null) {
                radialProgress22.setIcon(4, z, z2);
            }
        }
    }

    public void setDelegate(WallpaperActivityDelegate wallpaperActivityDelegate) {
        this.delegate = wallpaperActivityDelegate;
    }

    public void setPatterns(ArrayList<Object> arrayList) {
        this.patterns = arrayList;
        if (this.screenType == 1 || (this.currentWallpaper instanceof ColorWallpaper)) {
            ColorWallpaper colorWallpaper = (ColorWallpaper) this.currentWallpaper;
            if (colorWallpaper.patternId != 0) {
                int size = this.patterns.size();
                for (int i = 0; i < size; i++) {
                    TL_wallPaper tL_wallPaper = (TL_wallPaper) this.patterns.get(i);
                    if (tL_wallPaper.id == colorWallpaper.patternId) {
                        this.selectedPattern = tL_wallPaper;
                        break;
                    }
                }
                this.currentIntensity = colorWallpaper.intensity;
            }
        }
    }

    private void updateSelectedPattern(boolean z) {
        int childCount = this.patternsListView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.patternsListView.getChildAt(i);
            if (childAt instanceof PatternCell) {
                ((PatternCell) childAt).updateSelected(z);
            }
        }
    }

    private void updateMotionButton() {
        float f = 1.0f;
        float f2 = 0.0f;
        Object obj;
        Property property;
        float[] fArr;
        if (this.screenType == 2) {
            this.checkBoxView[this.selectedPattern != null ? 2 : 0].setVisibility(0);
            if (this.selectedPattern == null && (this.currentWallpaper instanceof ColorWallpaper)) {
                this.checkBoxView[2].setChecked(false, true);
            }
            AnimatorSet animatorSet = new AnimatorSet();
            Animator[] animatorArr = new Animator[2];
            obj = this.checkBoxView[2];
            property = View.ALPHA;
            fArr = new float[1];
            fArr[0] = this.selectedPattern != null ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(obj, property, fArr);
            obj = this.checkBoxView[0];
            property = View.ALPHA;
            fArr = new float[1];
            if (this.selectedPattern != null) {
                f = 0.0f;
            }
            fArr[0] = f;
            animatorArr[1] = ObjectAnimator.ofFloat(obj, property, fArr);
            animatorSet.playTogether(animatorArr);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    ThemePreviewActivity.this.checkBoxView[ThemePreviewActivity.this.selectedPattern != null ? 0 : 2].setVisibility(4);
                }
            });
            animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            animatorSet.setDuration(200);
            animatorSet.start();
        } else {
            if (this.checkBoxView[0].isEnabled() != (this.selectedPattern != null)) {
                if (this.selectedPattern == null) {
                    this.checkBoxView[0].setChecked(false, true);
                }
                this.checkBoxView[0].setEnabled(this.selectedPattern != null);
                if (this.selectedPattern != null) {
                    this.checkBoxView[0].setVisibility(0);
                }
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.checkBoxView[1].getLayoutParams();
                AnimatorSet animatorSet2 = new AnimatorSet();
                int dp = (layoutParams.width + AndroidUtilities.dp(9.0f)) / 2;
                Animator[] animatorArr2 = new Animator[1];
                Object obj2 = this.checkBoxView[0];
                Property property2 = View.ALPHA;
                float[] fArr2 = new float[1];
                if (this.selectedPattern == null) {
                    f = 0.0f;
                }
                fArr2[0] = f;
                animatorArr2[0] = ObjectAnimator.ofFloat(obj2, property2, fArr2);
                animatorSet2.playTogether(animatorArr2);
                Animator[] animatorArr3 = new Animator[1];
                obj = this.checkBoxView[0];
                property = View.TRANSLATION_X;
                fArr = new float[1];
                fArr[0] = this.selectedPattern != null ? 0.0f : (float) dp;
                animatorArr3[0] = ObjectAnimator.ofFloat(obj, property, fArr);
                animatorSet2.playTogether(animatorArr3);
                animatorArr3 = new Animator[1];
                obj = this.checkBoxView[1];
                property = View.TRANSLATION_X;
                float[] fArr3 = new float[1];
                if (this.selectedPattern == null) {
                    f2 = (float) (-dp);
                }
                fArr3[0] = f2;
                animatorArr3[0] = ObjectAnimator.ofFloat(obj, property, fArr3);
                animatorSet2.playTogether(animatorArr3);
                animatorSet2.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                animatorSet2.setDuration(200);
                animatorSet2.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (ThemePreviewActivity.this.selectedPattern == null) {
                            ThemePreviewActivity.this.checkBoxView[0].setVisibility(4);
                        }
                    }
                });
                animatorSet2.start();
            }
        }
    }

    private void showPatternsView(int i, boolean z) {
        ArrayList arrayList;
        int i2;
        int i3 = i;
        final boolean z2 = z && i3 == 1 && this.selectedPattern != null;
        if (z) {
            if (i3 != 0) {
                this.previousSelectedPattern = this.selectedPattern;
                this.previousIntensity = this.currentIntensity;
                this.patternsAdapter.notifyDataSetChanged();
                arrayList = this.patterns;
                if (arrayList != null) {
                    TL_wallPaper tL_wallPaper = this.selectedPattern;
                    if (tL_wallPaper == null) {
                        i2 = 0;
                    } else {
                        i2 = arrayList.indexOf(tL_wallPaper) + (this.screenType == 2 ? 1 : 0);
                    }
                    this.patternsLayoutManager.scrollToPositionWithOffset(i2, (this.patternsListView.getMeasuredWidth() - AndroidUtilities.dp(124.0f)) / 2);
                }
            } else if (this.screenType == 2) {
                this.previousBackgroundColor = this.backgroundColor;
                this.previousBackgroundGradientColor = this.backgroundGradientColor;
                this.previousBackgroundRotation = this.backupBackgroundRotation;
                this.colorPicker.setType(0, false, true, this.previousBackgroundGradientColor != 0, false, this.previousBackgroundRotation, false);
                this.colorPicker.setColor(this.backgroundGradientColor, 1);
                this.colorPicker.setColor(this.backgroundColor, 0);
            }
        }
        if (this.screenType == 2) {
            this.checkBoxView[z2 ? 2 : 0].setVisibility(0);
        }
        this.patternViewAnimation = new AnimatorSet();
        arrayList = new ArrayList();
        int i4 = i3 == 0 ? 1 : 0;
        float f = 1.0f;
        if (z) {
            this.patternLayout[i3].setVisibility(0);
            if (this.screenType == 2) {
                arrayList.add(ObjectAnimator.ofFloat(this.listView2, View.TRANSLATION_Y, new float[]{(float) ((-this.patternLayout[i3].getMeasuredHeight()) + AndroidUtilities.dp(48.0f))}));
                Object obj = this.checkBoxView[2];
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = z2 ? 1.0f : 0.0f;
                arrayList.add(ObjectAnimator.ofFloat(obj, property, fArr));
                obj = this.checkBoxView[0];
                property = View.ALPHA;
                fArr = new float[1];
                if (z2) {
                    f = 0.0f;
                }
                fArr[0] = f;
                arrayList.add(ObjectAnimator.ofFloat(obj, property, fArr));
                arrayList.add(ObjectAnimator.ofFloat(this.backgroundImage, View.ALPHA, new float[]{0.0f}));
                if (this.patternLayout[i4].getVisibility() == 0) {
                    arrayList.add(ObjectAnimator.ofFloat(this.patternLayout[i4], View.ALPHA, new float[]{0.0f}));
                    arrayList.add(ObjectAnimator.ofFloat(this.patternLayout[i3], View.ALPHA, new float[]{0.0f, 1.0f}));
                    this.patternLayout[i3].setTranslationY(0.0f);
                } else {
                    arrayList.add(ObjectAnimator.ofFloat(this.patternLayout[i3], View.TRANSLATION_Y, new float[]{(float) this.patternLayout[i3].getMeasuredHeight(), 0.0f}));
                }
            } else {
                if (i3 == 1) {
                    arrayList.add(ObjectAnimator.ofFloat(this.patternLayout[i3], View.ALPHA, new float[]{0.0f, 1.0f}));
                } else {
                    this.patternLayout[i3].setAlpha(1.0f);
                    arrayList.add(ObjectAnimator.ofFloat(this.patternLayout[i4], View.ALPHA, new float[]{0.0f}));
                }
                this.colorPicker.hideKeyboard();
            }
        } else {
            arrayList.add(ObjectAnimator.ofFloat(this.listView2, View.TRANSLATION_Y, new float[]{0.0f}));
            arrayList.add(ObjectAnimator.ofFloat(this.patternLayout[i3], View.TRANSLATION_Y, new float[]{(float) r10[i3].getMeasuredHeight()}));
            arrayList.add(ObjectAnimator.ofFloat(this.checkBoxView[0], View.ALPHA, new float[]{1.0f}));
            arrayList.add(ObjectAnimator.ofFloat(this.checkBoxView[2], View.ALPHA, new float[]{0.0f}));
            arrayList.add(ObjectAnimator.ofFloat(this.backgroundImage, View.ALPHA, new float[]{1.0f}));
        }
        this.patternViewAnimation.playTogether(arrayList);
        final boolean z3 = z;
        i2 = i4;
        i3 = i;
        this.patternViewAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                ThemePreviewActivity.this.patternViewAnimation = null;
                if (z3 && ThemePreviewActivity.this.patternLayout[i2].getVisibility() == 0) {
                    ThemePreviewActivity.this.patternLayout[i2].setAlpha(1.0f);
                    ThemePreviewActivity.this.patternLayout[i2].setVisibility(4);
                } else if (!z3) {
                    ThemePreviewActivity.this.patternLayout[i3].setVisibility(4);
                }
                int i = 2;
                if (ThemePreviewActivity.this.screenType == 2) {
                    WallpaperCheckBoxView[] access$5700 = ThemePreviewActivity.this.checkBoxView;
                    if (z2) {
                        i = 0;
                    }
                    access$5700[i].setVisibility(4);
                } else if (i3 == 1) {
                    ThemePreviewActivity.this.patternLayout[i2].setAlpha(0.0f);
                }
            }
        });
        this.patternViewAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        this.patternViewAnimation.setDuration(200);
        this.patternViewAnimation.start();
    }

    private void animateMotionChange() {
        AnimatorSet animatorSet = this.motionAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.motionAnimation = new AnimatorSet();
        if (this.isMotion) {
            animatorSet = this.motionAnimation;
            Animator[] animatorArr = new Animator[2];
            animatorArr[0] = ObjectAnimator.ofFloat(this.backgroundImage, View.SCALE_X, new float[]{this.parallaxScale});
            animatorArr[1] = ObjectAnimator.ofFloat(this.backgroundImage, View.SCALE_Y, new float[]{this.parallaxScale});
            animatorSet.playTogether(animatorArr);
        } else {
            animatorSet = this.motionAnimation;
            r4 = new Animator[4];
            r4[0] = ObjectAnimator.ofFloat(this.backgroundImage, View.SCALE_X, new float[]{1.0f});
            r4[1] = ObjectAnimator.ofFloat(this.backgroundImage, View.SCALE_Y, new float[]{1.0f});
            r4[2] = ObjectAnimator.ofFloat(this.backgroundImage, View.TRANSLATION_X, new float[]{0.0f});
            r4[3] = ObjectAnimator.ofFloat(this.backgroundImage, View.TRANSLATION_Y, new float[]{0.0f});
            animatorSet.playTogether(r4);
        }
        this.motionAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        this.motionAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                ThemePreviewActivity.this.motionAnimation = null;
            }
        });
        this.motionAnimation.start();
    }

    private void setBackgroundColor(int i, int i2, boolean z) {
        if (i2 == 0) {
            this.backgroundColor = i;
        } else {
            this.backgroundGradientColor = i;
        }
        int i3 = 0;
        if (this.checkBoxView != null) {
            int i4 = 0;
            while (true) {
                WallpaperCheckBoxView[] wallpaperCheckBoxViewArr = this.checkBoxView;
                if (i4 >= wallpaperCheckBoxViewArr.length) {
                    break;
                }
                if (wallpaperCheckBoxViewArr[i4] != null) {
                    if (i2 == 0) {
                        wallpaperCheckBoxViewArr[i4].setBackgroundColor(i);
                    } else {
                        wallpaperCheckBoxViewArr[i4].setBackgroundGradientColor(i);
                    }
                }
                i4++;
            }
        }
        if (this.backgroundGradientColor != 0) {
            this.backgroundImage.setBackground(new GradientDrawable(BackgroundGradientDrawable.getGradientOrientation(this.backgroundRotation), new int[]{this.backgroundColor, this.backgroundGradientColor}));
            this.patternColor = AndroidUtilities.getPatternColor(AndroidUtilities.getAverageColor(this.backgroundColor, this.backgroundGradientColor));
        } else {
            this.backgroundImage.setBackgroundColor(this.backgroundColor);
            this.patternColor = AndroidUtilities.getPatternColor(this.backgroundColor);
        }
        r5 = new int[4];
        int i5 = this.patternColor;
        r5[0] = i5;
        r5[1] = i5;
        r5[2] = i5;
        r5[3] = i5;
        Theme.applyChatServiceMessageColor(r5);
        BackupImageView backupImageView = this.backgroundImage;
        if (backupImageView != null) {
            backupImageView.getImageReceiver().setColorFilter(new PorterDuffColorFilter(this.patternColor, this.blendMode));
            this.backgroundImage.getImageReceiver().setAlpha(this.currentIntensity);
            this.backgroundImage.invalidate();
        }
        RecyclerListView recyclerListView = this.listView2;
        if (recyclerListView != null) {
            recyclerListView.invalidateViews();
        }
        FrameLayout frameLayout = this.buttonsContainer;
        if (frameLayout != null) {
            i = frameLayout.getChildCount();
            while (i3 < i) {
                this.buttonsContainer.getChildAt(i3).invalidate();
                i3++;
            }
        }
        RadialProgress2 radialProgress2 = this.radialProgress;
        if (radialProgress2 != null) {
            String str = "chat_serviceText";
            String str2 = "chat_serviceBackground";
            radialProgress2.setColors(str2, str2, str, str);
        }
    }

    private void setCurrentImage(boolean z) {
        if (this.screenType == 0 && this.accent == null) {
            this.backgroundImage.setBackground(Theme.getCachedWallpaper());
            return;
        }
        PhotoSize photoSize = null;
        int i = 0;
        int i2;
        BackupImageView backupImageView;
        if (this.screenType == 2) {
            Object obj = this.currentWallpaper;
            if (obj instanceof TL_wallPaper) {
                TL_wallPaper tL_wallPaper = (TL_wallPaper) obj;
                if (z) {
                    photoSize = FileLoader.getClosestPhotoSizeWithSize(tL_wallPaper.document.thumbs, 100);
                }
                this.backgroundImage.setImage(ImageLocation.getForDocument(tL_wallPaper.document), this.imageFilter, ImageLocation.getForDocument(photoSize, tL_wallPaper.document), "100_100_b", "jpg", tL_wallPaper.document.size, 1, tL_wallPaper);
                return;
            } else if (obj instanceof ColorWallpaper) {
                ColorWallpaper colorWallpaper = (ColorWallpaper) obj;
                this.backgroundRotation = colorWallpaper.gradientRotation;
                setBackgroundColor(colorWallpaper.color, 0, true);
                i2 = colorWallpaper.gradientColor;
                if (i2 != 0) {
                    setBackgroundColor(i2, 1, true);
                }
                TL_wallPaper tL_wallPaper2 = this.selectedPattern;
                if (tL_wallPaper2 != null) {
                    backupImageView = this.backgroundImage;
                    ImageLocation forDocument = ImageLocation.getForDocument(tL_wallPaper2.document);
                    String str = this.imageFilter;
                    TL_wallPaper tL_wallPaper3 = this.selectedPattern;
                    backupImageView.setImage(forDocument, str, null, null, "jpg", tL_wallPaper3.document.size, 1, tL_wallPaper3);
                    return;
                }
                return;
            } else if (obj instanceof FileWallpaper) {
                Bitmap bitmap = this.currentWallpaperBitmap;
                if (bitmap != null) {
                    this.backgroundImage.setImageBitmap(bitmap);
                    return;
                }
                FileWallpaper fileWallpaper = (FileWallpaper) obj;
                File file = fileWallpaper.originalPath;
                if (file != null) {
                    this.backgroundImage.setImage(file.getAbsolutePath(), this.imageFilter, null);
                    return;
                }
                file = fileWallpaper.path;
                if (file != null) {
                    this.backgroundImage.setImage(file.getAbsolutePath(), this.imageFilter, null);
                    return;
                }
                if ("t".equals(fileWallpaper.slug)) {
                    BackupImageView backupImageView2 = this.backgroundImage;
                    backupImageView2.setImageDrawable(Theme.getThemedWallpaper(false, backupImageView2));
                    return;
                }
                i2 = fileWallpaper.resId;
                if (i2 != 0) {
                    this.backgroundImage.setImageResource(i2);
                    return;
                }
                return;
            } else if (obj instanceof SearchImage) {
                SearchImage searchImage = (SearchImage) obj;
                Photo photo = searchImage.photo;
                if (photo != null) {
                    PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 100);
                    PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(searchImage.photo.sizes, this.maxWallpaperSize, true);
                    if (closestPhotoSizeWithSize2 == closestPhotoSizeWithSize) {
                        closestPhotoSizeWithSize2 = null;
                    }
                    this.backgroundImage.setImage(ImageLocation.getForPhoto(closestPhotoSizeWithSize2, searchImage.photo), this.imageFilter, ImageLocation.getForPhoto(closestPhotoSizeWithSize, searchImage.photo), "100_100_b", "jpg", closestPhotoSizeWithSize2 != null ? closestPhotoSizeWithSize2.size : 0, 1, searchImage);
                    return;
                }
                this.backgroundImage.setImage(searchImage.imageUrl, this.imageFilter, searchImage.thumbUrl, "100_100_b");
                return;
            } else {
                return;
            }
        }
        Disposable disposable = this.backgroundGradientDisposable;
        if (disposable != null) {
            disposable.dispose();
            this.backgroundGradientDisposable = null;
        }
        i2 = Theme.getDefaultAccentColor("chat_wallpaper");
        int i3 = (int) this.accent.backgroundOverrideColor;
        if (i3 != 0) {
            i2 = i3;
        }
        i3 = Theme.getDefaultAccentColor("chat_wallpaper_gradient_to");
        long j = this.accent.backgroundGradientOverrideColor;
        int i4 = (int) j;
        if (i4 == 0 && j != 0) {
            i3 = 0;
        } else if (i4 != 0) {
            i3 = i4;
        }
        if (TextUtils.isEmpty(this.accent.patternSlug) || Theme.hasCustomWallpaper()) {
            this.backgroundImage.setBackground(Theme.getCachedWallpaper());
        } else {
            Drawable backgroundGradientDrawable;
            if (i3 != 0) {
                backgroundGradientDrawable = new BackgroundGradientDrawable(BackgroundGradientDrawable.getGradientOrientation(this.accent.backgroundRotation), new int[]{i2, i3});
                this.backgroundGradientDisposable = backgroundGradientDrawable.startDithering(Sizes.ofDeviceScreen(), new ListenerAdapter() {
                    public void onSizeReady(int i, int i2) {
                        Point point = AndroidUtilities.displaySize;
                        Object obj = 1;
                        Object obj2 = point.x <= point.y ? 1 : null;
                        if (i > i2) {
                            obj = null;
                        }
                        if (obj2 == obj) {
                            ThemePreviewActivity.this.backgroundImage.invalidate();
                        }
                    }
                }, 100);
            } else {
                backgroundGradientDrawable = new ColorDrawable(i2);
            }
            this.backgroundImage.setBackground(backgroundGradientDrawable);
            TL_wallPaper tL_wallPaper4 = this.selectedPattern;
            if (tL_wallPaper4 != null) {
                BackupImageView backupImageView3 = this.backgroundImage;
                ImageLocation forDocument2 = ImageLocation.getForDocument(tL_wallPaper4.document);
                String str2 = this.imageFilter;
                TL_wallPaper tL_wallPaper5 = this.selectedPattern;
                backupImageView3.setImage(forDocument2, str2, null, null, "jpg", tL_wallPaper5.document.size, 1, tL_wallPaper5);
            }
        }
        if (i3 == 0) {
            this.patternColor = AndroidUtilities.getPatternColor(i2);
        } else {
            this.patternColor = AndroidUtilities.getPatternColor(AndroidUtilities.getAverageColor(i2, i3));
        }
        backupImageView = this.backgroundImage;
        if (backupImageView != null) {
            backupImageView.getImageReceiver().setColorFilter(new PorterDuffColorFilter(this.patternColor, this.blendMode));
            this.backgroundImage.getImageReceiver().setAlpha(this.currentIntensity);
            this.backgroundImage.invalidate();
        }
        if (this.checkBoxView != null) {
            while (true) {
                WallpaperCheckBoxView[] wallpaperCheckBoxViewArr = this.checkBoxView;
                if (i < wallpaperCheckBoxViewArr.length) {
                    wallpaperCheckBoxViewArr[i].setBackgroundColor(i2);
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    private List<ThemeDescription> getThemeDescriptionsInternal() {
        -$$Lambda$ThemePreviewActivity$x1lSd-U-b2JICwuHjfag8JNNqwc -__lambda_themepreviewactivity_x1lsd-u-b2jicwuhjfag8jnnqwc = new -$$Lambda$ThemePreviewActivity$x1lSd-U-b2JICwuHjfag8JNNqwc(this);
        ArrayList arrayList = new ArrayList();
        -$$Lambda$ThemePreviewActivity$x1lSd-U-b2JICwuHjfag8JNNqwc -__lambda_themepreviewactivity_x1lsd-u-b2jicwuhjfag8jnnqwc2 = -__lambda_themepreviewactivity_x1lsd-u-b2jicwuhjfag8jnnqwc;
        arrayList.add(new ThemeDescription(this.page1, ThemeDescription.FLAG_BACKGROUND, null, null, null, -__lambda_themepreviewactivity_x1lsd-u-b2jicwuhjfag8jnnqwc2, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.viewPager, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, "actionBarDefaultSearch"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, "actionBarDefaultSearchPlaceholder"));
        arrayList.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_AB_SUBTITLECOLOR, null, null, null, null, "actionBarDefaultSubtitle"));
        arrayList.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, -__lambda_themepreviewactivity_x1lsd-u-b2jicwuhjfag8jnnqwc2, "actionBarDefaultSubmenuBackground"));
        arrayList.add(new ThemeDescription(this.actionBar2, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, -__lambda_themepreviewactivity_x1lsd-u-b2jicwuhjfag8jnnqwc2, "actionBarDefaultSubmenuItem"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView2, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "chats_actionIcon"));
        arrayList.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "chats_actionBackground"));
        arrayList.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "chats_actionPressedBackground"));
        if (!this.useDefaultThemeForButtons) {
            arrayList.add(new ThemeDescription(this.saveButtonsContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
            arrayList.add(new ThemeDescription(this.cancelButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "chat_fieldOverlayText"));
            arrayList.add(new ThemeDescription(this.doneButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "chat_fieldOverlayText"));
        }
        ColorPicker colorPicker = this.colorPicker;
        if (colorPicker != null) {
            colorPicker.provideThemeDescriptions(arrayList);
        }
        if (this.patternLayout != null) {
            FrameLayout[] frameLayoutArr;
            TextView[] textViewArr;
            int i = 0;
            while (true) {
                frameLayoutArr = this.patternLayout;
                if (i >= frameLayoutArr.length) {
                    break;
                }
                arrayList.add(new ThemeDescription(frameLayoutArr[i], 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, "chat_messagePanelShadow"));
                arrayList.add(new ThemeDescription(this.patternLayout[i], 0, null, Theme.chat_composeBackgroundPaint, null, null, "chat_messagePanelBackground"));
                i++;
            }
            i = 0;
            while (true) {
                frameLayoutArr = this.patternsButtonsContainer;
                if (i >= frameLayoutArr.length) {
                    break;
                }
                arrayList.add(new ThemeDescription(frameLayoutArr[i], 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, "chat_messagePanelShadow"));
                arrayList.add(new ThemeDescription(this.patternsButtonsContainer[i], 0, null, Theme.chat_composeBackgroundPaint, null, null, "chat_messagePanelBackground"));
                i++;
            }
            arrayList.add(new ThemeDescription(this.bottomOverlayChat, 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, "chat_messagePanelShadow"));
            arrayList.add(new ThemeDescription(this.bottomOverlayChat, 0, null, Theme.chat_composeBackgroundPaint, null, null, "chat_messagePanelBackground"));
            arrayList.add(new ThemeDescription(this.bottomOverlayChatText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "chat_fieldOverlayText"));
            i = 0;
            while (true) {
                textViewArr = this.patternsSaveButton;
                if (i >= textViewArr.length) {
                    break;
                }
                arrayList.add(new ThemeDescription(textViewArr[i], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "chat_fieldOverlayText"));
                i++;
            }
            i = 0;
            while (true) {
                textViewArr = this.patternsCancelButton;
                if (i >= textViewArr.length) {
                    break;
                }
                arrayList.add(new ThemeDescription(textViewArr[i], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "chat_fieldOverlayText"));
                i++;
            }
            arrayList.add(new ThemeDescription(this.intensitySeekBar, 0, new Class[]{SeekBarView.class}, new String[]{"innerPaint1"}, null, null, null, "player_progressBackground"));
            arrayList.add(new ThemeDescription(this.intensitySeekBar, 0, new Class[]{SeekBarView.class}, new String[]{"outerPaint1"}, null, null, null, "player_progress"));
            arrayList.add(new ThemeDescription(this.intensityCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueHeader"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, null, "chat_inBubble"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, null, "chat_inBubbleSelected"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInShadowDrawable, Theme.chat_msgInMediaShadowDrawable}, null, "chat_inBubbleShadow"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, "chat_outBubble"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, "chat_outBubbleGradient"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, null, "chat_outBubbleSelected"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutShadowDrawable, Theme.chat_msgOutMediaShadowDrawable}, null, "chat_outBubbleShadow"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_messageTextIn"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_messageTextOut"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckDrawable}, null, "chat_outSentCheck"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable}, null, "chat_outSentCheckSelected"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckReadDrawable, Theme.chat_msgOutHalfCheckDrawable}, null, "chat_outSentCheckRead"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckReadSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, null, "chat_outSentCheckReadSelected"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, null, "chat_mediaSentCheck"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inReplyLine"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outReplyLine"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inReplyNameText"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outReplyNameText"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inReplyMessageText"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outReplyMessageText"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inReplyMediaMessageSelectedText"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outReplyMediaMessageSelectedText"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inTimeText"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outTimeText"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inTimeSelectedText"));
            arrayList.add(new ThemeDescription(this.listView2, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outTimeSelectedText"));
        }
        return arrayList;
    }

    public /* synthetic */ void lambda$getThemeDescriptionsInternal$20$ThemePreviewActivity() {
        ActionBarMenuItem actionBarMenuItem = this.dropDownContainer;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.redrawPopup(Theme.getColor("actionBarDefaultSubmenuBackground"));
            this.dropDownContainer.setPopupItemsColor(Theme.getColor("actionBarDefaultSubmenuItem"), false);
        }
        Drawable drawable = this.sheetDrawable;
        if (drawable != null) {
            drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhite"), Mode.MULTIPLY));
        }
    }
}
