package org.telegram.p005ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.p000v4.content.FileProvider;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.exoplayer2.CLASSNAMEC;
import com.google.devtools.build.android.desugar.runtime.ThrowableExtension;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.telegram.PhoneFormat.CLASSNAMEPhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.p005ui.ActionBar.ActionBarMenuItem;
import org.telegram.p005ui.ActionBar.AlertDialog;
import org.telegram.p005ui.ActionBar.AlertDialog.Builder;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.ActionBar.BottomSheet;
import org.telegram.p005ui.ActionBar.CLASSNAMEActionBar;
import org.telegram.p005ui.ActionBar.CLASSNAMEActionBar.ActionBarMenuOnItemClick;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.ActionBar.ThemeDescription;
import org.telegram.p005ui.Cells.EmptyCell;
import org.telegram.p005ui.Cells.HeaderCell;
import org.telegram.p005ui.Cells.ShadowSectionCell;
import org.telegram.p005ui.Cells.TextCell;
import org.telegram.p005ui.Cells.TextDetailCell;
import org.telegram.p005ui.Cells.TextInfoPrivacyCell;
import org.telegram.p005ui.Components.AvatarDrawable;
import org.telegram.p005ui.Components.BackupImageView;
import org.telegram.p005ui.Components.CombinedDrawable;
import org.telegram.p005ui.Components.ImageUpdater;
import org.telegram.p005ui.Components.ImageUpdater.ImageUpdaterDelegate;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.p005ui.Components.RadialProgressView;
import org.telegram.p005ui.Components.RecyclerListView;
import org.telegram.p005ui.Components.RecyclerListView.Holder;
import org.telegram.p005ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.p005ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.p005ui.Components.URLSpanNoUnderline;
import org.telegram.p005ui.Components.voip.VoIPHelper;
import org.telegram.p005ui.PhotoViewer.EmptyPhotoViewerProvider;
import org.telegram.p005ui.PhotoViewer.PhotoViewerProvider;
import org.telegram.p005ui.PhotoViewer.PlaceProviderObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_help_getSupport;
import org.telegram.tgnet.TLRPC.TL_help_support;
import org.telegram.tgnet.TLRPC.TL_photos_photo;
import org.telegram.tgnet.TLRPC.TL_photos_uploadProfilePhoto;
import org.telegram.tgnet.TLRPC.TL_userFull;
import org.telegram.tgnet.TLRPC.TL_userProfilePhoto;
import org.telegram.tgnet.TLRPC.TL_userProfilePhotoEmpty;
import org.telegram.tgnet.TLRPC.User;

/* renamed from: org.telegram.ui.SettingsActivity */
public class SettingsActivity extends BaseFragment implements NotificationCenterDelegate, ImageUpdaterDelegate {
    private static final int edit_name = 1;
    private static final int logout = 2;
    private FileLocation avatar;
    private AnimatorSet avatarAnimation;
    private FileLocation avatarBig;
    private FrameLayout avatarContainer;
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImage;
    private View avatarOverlay;
    private RadialProgressView avatarProgressView;
    private int bioRow;
    private int chatRow;
    private int dataRow;
    private int extraHeight;
    private View extraHeightView;
    private int helpRow;
    private ImageUpdater imageUpdater;
    private int languageRow;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private TextView nameTextView;
    private int notificationRow;
    private int numberRow;
    private int numberSectionRow;
    private TextView onlineTextView;
    private int overscrollRow;
    private int privacyRow;
    private PhotoViewerProvider provider = new CLASSNAME();
    private int rowCount;
    private int settingsSectionRow;
    private int settingsSectionRow2;
    private View shadowView;
    private TL_userFull userInfo;
    private int usernameRow;
    private int versionRow;
    private ImageView writeButton;
    private AnimatorSet writeButtonAnimation;

    /* renamed from: org.telegram.ui.SettingsActivity$10 */
    class CLASSNAME extends OnScrollListener {
        CLASSNAME() {
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            int i = 0;
            if (SettingsActivity.this.layoutManager.getItemCount() != 0) {
                int height = 0;
                View child = recyclerView.getChildAt(0);
                if (child != null) {
                    if (SettingsActivity.this.layoutManager.findFirstVisibleItemPosition() == 0) {
                        int dp = AndroidUtilities.m9dp(88.0f);
                        if (child.getTop() < 0) {
                            i = child.getTop();
                        }
                        height = dp + i;
                    }
                    if (SettingsActivity.this.extraHeight != height) {
                        SettingsActivity.this.extraHeight = height;
                        SettingsActivity.this.needLayout();
                    }
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.SettingsActivity$13 */
    class CLASSNAME implements OnPreDrawListener {
        CLASSNAME() {
        }

        public boolean onPreDraw() {
            if (SettingsActivity.this.fragmentView != null) {
                SettingsActivity.this.needLayout();
                SettingsActivity.this.fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
            }
            return true;
        }
    }

    /* renamed from: org.telegram.ui.SettingsActivity$1 */
    class CLASSNAME extends EmptyPhotoViewerProvider {
        CLASSNAME() {
        }

        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
            PlaceProviderObject object = null;
            int i = 0;
            if (fileLocation != null) {
                User user = MessagesController.getInstance(SettingsActivity.this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(SettingsActivity.this.currentAccount).getClientUserId()));
                if (!(user == null || user.photo == null || user.photo.photo_big == null)) {
                    FileLocation photoBig = user.photo.photo_big;
                    if (photoBig.local_id == fileLocation.local_id && photoBig.volume_id == fileLocation.volume_id && photoBig.dc_id == fileLocation.dc_id) {
                        int[] coords = new int[2];
                        SettingsActivity.this.avatarImage.getLocationInWindow(coords);
                        object = new PlaceProviderObject();
                        object.viewX = coords[0];
                        int i2 = coords[1];
                        if (VERSION.SDK_INT < 21) {
                            i = AndroidUtilities.statusBarHeight;
                        }
                        object.viewY = i2 - i;
                        object.parentView = SettingsActivity.this.avatarImage;
                        object.imageReceiver = SettingsActivity.this.avatarImage.getImageReceiver();
                        object.dialogId = UserConfig.getInstance(SettingsActivity.this.currentAccount).getClientUserId();
                        object.thumb = object.imageReceiver.getBitmapSafe();
                        object.size = -1;
                        object.radius = SettingsActivity.this.avatarImage.getImageReceiver().getRoundRadius();
                        object.scale = SettingsActivity.this.avatarContainer.getScaleX();
                    }
                }
            }
            return object;
        }

        public void willHidePhotoViewer() {
            SettingsActivity.this.avatarImage.getImageReceiver().setVisible(true, true);
        }
    }

    /* renamed from: org.telegram.ui.SettingsActivity$2 */
    class CLASSNAME extends ActionBarMenuOnItemClick {
        CLASSNAME() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                SettingsActivity.this.lambda$checkDiscard$70$PassportActivity();
            } else if (id == 1) {
                SettingsActivity.this.presentFragment(new ChangeNameActivity());
            } else if (id == 2 && SettingsActivity.this.getParentActivity() != null) {
                Builder builder = new Builder(SettingsActivity.this.getParentActivity());
                builder.setMessage(LocaleController.getString("AreYouSureLogout", R.string.AreYouSureLogout));
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new SettingsActivity$2$$Lambda$0(this));
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                SettingsActivity.this.showDialog(builder.create());
            }
        }

        final /* synthetic */ void lambda$onItemClick$0$SettingsActivity$2(DialogInterface dialogInterface, int i) {
            MessagesController.getInstance(SettingsActivity.this.currentAccount).performLogout(1);
        }
    }

    /* renamed from: org.telegram.ui.SettingsActivity$6 */
    class CLASSNAME implements OnItemLongClickListener {
        private int pressCount = 0;

        CLASSNAME() {
        }

        public boolean onItemClick(View view, int position) {
            if (position != SettingsActivity.this.versionRow) {
                return false;
            }
            this.pressCount++;
            if (this.pressCount >= 2 || BuildVars.DEBUG_PRIVATE_VERSION) {
                String str;
                Builder builder = new Builder(SettingsActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("DebugMenu", R.string.DebugMenu));
                CharSequence[] items = new CharSequence[10];
                items[0] = LocaleController.getString("DebugMenuImportContacts", R.string.DebugMenuImportContacts);
                items[1] = LocaleController.getString("DebugMenuReloadContacts", R.string.DebugMenuReloadContacts);
                items[2] = LocaleController.getString("DebugMenuResetContacts", R.string.DebugMenuResetContacts);
                items[3] = LocaleController.getString("DebugMenuResetDialogs", R.string.DebugMenuResetDialogs);
                items[4] = BuildVars.LOGS_ENABLED ? LocaleController.getString("DebugMenuDisableLogs", R.string.DebugMenuDisableLogs) : LocaleController.getString("DebugMenuEnableLogs", R.string.DebugMenuEnableLogs);
                items[5] = SharedConfig.inappCamera ? LocaleController.getString("DebugMenuDisableCamera", R.string.DebugMenuDisableCamera) : LocaleController.getString("DebugMenuEnableCamera", R.string.DebugMenuEnableCamera);
                items[6] = LocaleController.getString("DebugMenuClearMediaCache", R.string.DebugMenuClearMediaCache);
                items[7] = LocaleController.getString("DebugMenuCallSettings", R.string.DebugMenuCallSettings);
                items[8] = null;
                if (BuildVars.DEBUG_PRIVATE_VERSION) {
                    str = "Check for app updates";
                } else {
                    str = null;
                }
                items[9] = str;
                builder.setItems(items, new SettingsActivity$6$$Lambda$0(this));
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                SettingsActivity.this.showDialog(builder.create());
            } else {
                try {
                    Toast.makeText(SettingsActivity.this.getParentActivity(), "\u00af\\_(\u30c4)_/\u00af", 0).show();
                } catch (Throwable e) {
                    FileLog.m13e(e);
                }
            }
            return true;
        }

        final /* synthetic */ void lambda$onItemClick$0$SettingsActivity$6(DialogInterface dialog, int which) {
            if (which == 0) {
                UserConfig.getInstance(SettingsActivity.this.currentAccount).syncContacts = true;
                UserConfig.getInstance(SettingsActivity.this.currentAccount).saveConfig(false);
                ContactsController.getInstance(SettingsActivity.this.currentAccount).forceImportContacts();
            } else if (which == 1) {
                ContactsController.getInstance(SettingsActivity.this.currentAccount).loadContacts(false, 0);
            } else if (which == 2) {
                ContactsController.getInstance(SettingsActivity.this.currentAccount).resetImportedContacts();
            } else if (which == 3) {
                MessagesController.getInstance(SettingsActivity.this.currentAccount).forceResetDialogs();
            } else if (which == 4) {
                boolean z;
                if (BuildVars.LOGS_ENABLED) {
                    z = false;
                } else {
                    z = true;
                }
                BuildVars.LOGS_ENABLED = z;
                ApplicationLoader.applicationContext.getSharedPreferences("systemConfig", 0).edit().putBoolean("logsEnabled", BuildVars.LOGS_ENABLED).commit();
            } else if (which == 5) {
                SharedConfig.toggleInappCamera();
            } else if (which == 6) {
                MessagesStorage.getInstance(SettingsActivity.this.currentAccount).clearSentMedia();
            } else if (which == 7) {
                VoIPHelper.showCallDebugSettings(SettingsActivity.this.getParentActivity());
            } else if (which == 8) {
                SharedConfig.toggleRoundCamera16to9();
            } else if (which == 9) {
                ((LaunchActivity) SettingsActivity.this.getParentActivity()).checkAppUpdate(true);
            }
        }
    }

    /* renamed from: org.telegram.ui.SettingsActivity$9 */
    class CLASSNAME extends ViewOutlineProvider {
        CLASSNAME() {
        }

        @SuppressLint({"NewApi"})
        public void getOutline(View view, Outline outline) {
            outline.setOval(0, 0, AndroidUtilities.m9dp(56.0f), AndroidUtilities.m9dp(56.0f));
        }
    }

    /* renamed from: org.telegram.ui.SettingsActivity$LinkMovementMethodMy */
    private static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        /* synthetic */ LinkMovementMethodMy(CLASSNAME x0) {
            this();
        }

        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            try {
                return super.onTouchEvent(widget, buffer, event);
            } catch (Throwable e) {
                FileLog.m13e(e);
                return false;
            }
        }
    }

    /* renamed from: org.telegram.ui.SettingsActivity$ListAdapter */
    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return SettingsActivity.this.rowCount;
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    if (position == SettingsActivity.this.overscrollRow) {
                        ((EmptyCell) holder.itemView).setHeight(AndroidUtilities.m9dp(88.0f));
                        return;
                    }
                    return;
                case 2:
                    TextCell textCell = holder.itemView;
                    if (position == SettingsActivity.this.languageRow) {
                        textCell.setTextAndIcon(LocaleController.getString("Language", R.string.Language), R.drawable.menu_language, true);
                        return;
                    } else if (position == SettingsActivity.this.notificationRow) {
                        textCell.setTextAndIcon(LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds), R.drawable.menu_notifications, true);
                        return;
                    } else if (position == SettingsActivity.this.privacyRow) {
                        textCell.setTextAndIcon(LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.menu_secret, true);
                        return;
                    } else if (position == SettingsActivity.this.dataRow) {
                        textCell.setTextAndIcon(LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.menu_data, true);
                        return;
                    } else if (position == SettingsActivity.this.chatRow) {
                        textCell.setTextAndIcon(LocaleController.getString("ChatSettings", R.string.ChatSettings), R.drawable.menu_chats, true);
                        return;
                    } else if (position == SettingsActivity.this.helpRow) {
                        textCell.setTextAndIcon(LocaleController.getString("SettingsHelp", R.string.SettingsHelp), R.drawable.menu_help, false);
                        return;
                    } else {
                        return;
                    }
                case 4:
                    if (position == SettingsActivity.this.settingsSectionRow2) {
                        ((HeaderCell) holder.itemView).setText(LocaleController.getString("SETTINGS", R.string.SETTINGS));
                        return;
                    } else if (position == SettingsActivity.this.numberSectionRow) {
                        ((HeaderCell) holder.itemView).setText(LocaleController.getString("Account", R.string.Account));
                        return;
                    } else {
                        return;
                    }
                case 6:
                    TextDetailCell textCell2 = holder.itemView;
                    User user;
                    String value;
                    if (position == SettingsActivity.this.numberRow) {
                        user = UserConfig.getInstance(SettingsActivity.this.currentAccount).getCurrentUser();
                        if (user == null || user.phone == null || user.phone.length() == 0) {
                            value = LocaleController.getString("NumberUnknown", R.string.NumberUnknown);
                        } else {
                            value = CLASSNAMEPhoneFormat.getInstance().format("+" + user.phone);
                        }
                        textCell2.setTextAndValue(value, LocaleController.getString("TapToChangePhone", R.string.TapToChangePhone), true);
                        return;
                    } else if (position == SettingsActivity.this.usernameRow) {
                        user = UserConfig.getInstance(SettingsActivity.this.currentAccount).getCurrentUser();
                        if (user == null || TextUtils.isEmpty(user.username)) {
                            value = LocaleController.getString("UsernameEmpty", R.string.UsernameEmpty);
                        } else {
                            value = "@" + user.username;
                        }
                        textCell2.setTextAndValue(value, LocaleController.getString("Username", R.string.Username), true);
                        return;
                    } else if (position != SettingsActivity.this.bioRow) {
                        return;
                    } else {
                        if (SettingsActivity.this.userInfo == null || !TextUtils.isEmpty(SettingsActivity.this.userInfo.about)) {
                            textCell2.setTextWithEmojiAndValue(SettingsActivity.this.userInfo == null ? LocaleController.getString("Loading", R.string.Loading) : SettingsActivity.this.userInfo.about, LocaleController.getString("UserBio", R.string.UserBio), false);
                            return;
                        } else {
                            textCell2.setTextAndValue(LocaleController.getString("UserBio", R.string.UserBio), LocaleController.getString("UserBioDetail", R.string.UserBioDetail), false);
                            return;
                        }
                    }
                default:
                    return;
            }
        }

        public boolean isEnabled(ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == SettingsActivity.this.notificationRow || position == SettingsActivity.this.numberRow || position == SettingsActivity.this.privacyRow || position == SettingsActivity.this.languageRow || position == SettingsActivity.this.usernameRow || position == SettingsActivity.this.bioRow || position == SettingsActivity.this.versionRow || position == SettingsActivity.this.dataRow || position == SettingsActivity.this.chatRow || position == SettingsActivity.this.helpRow;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
                    view = new EmptyCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 2:
                    view = new TextCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 4:
                    view = new HeaderCell(this.mContext, 23);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 5:
                    View cell = new TextInfoPrivacyCell(this.mContext, 10);
                    cell.getTextView().setGravity(1);
                    cell.getTextView().setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
                    cell.getTextView().setMovementMethod(null);
                    cell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    try {
                        PackageInfo pInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                        int code = pInfo.versionCode / 10;
                        String abi = TtmlNode.ANONYMOUS_REGION_ID;
                        switch (pInfo.versionCode % 10) {
                            case 0:
                            case 9:
                                abi = "universal " + Build.CPU_ABI + " " + Build.CPU_ABI2;
                                break;
                            case 1:
                            case 3:
                                abi = "arm-v7a";
                                break;
                            case 2:
                            case 4:
                                abi = "x86";
                                break;
                            case 5:
                            case 7:
                                abi = "arm64-v8a";
                                break;
                            case 6:
                            case 8:
                                abi = "x86_64";
                                break;
                        }
                        Object[] objArr = new Object[1];
                        objArr[0] = String.format(Locale.US, "v%s (%d) %s", new Object[]{pInfo.versionName, Integer.valueOf(code), abi});
                        cell.setText(LocaleController.formatString("TelegramVersion", R.string.TelegramVersion, objArr));
                    } catch (Throwable e) {
                        FileLog.m13e(e);
                    }
                    cell.getTextView().setPadding(0, AndroidUtilities.m9dp(14.0f), 0, AndroidUtilities.m9dp(14.0f));
                    view = cell;
                    break;
                case 6:
                    view = new TextDetailCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
        }

        public int getItemViewType(int position) {
            if (position == SettingsActivity.this.overscrollRow) {
                return 0;
            }
            if (position == SettingsActivity.this.settingsSectionRow) {
                return 1;
            }
            if (position == SettingsActivity.this.notificationRow || position == SettingsActivity.this.privacyRow || position == SettingsActivity.this.languageRow || position == SettingsActivity.this.dataRow || position == SettingsActivity.this.chatRow || position == SettingsActivity.this.helpRow) {
                return 2;
            }
            if (position == SettingsActivity.this.versionRow) {
                return 5;
            }
            if (position == SettingsActivity.this.numberRow || position == SettingsActivity.this.usernameRow || position == SettingsActivity.this.bioRow) {
                return 6;
            }
            if (position == SettingsActivity.this.settingsSectionRow2 || position == SettingsActivity.this.numberSectionRow) {
                return 4;
            }
            return 2;
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.imageUpdater = new ImageUpdater();
        this.imageUpdater.parentFragment = this;
        this.imageUpdater.delegate = this;
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.userInfoDidLoad);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
        this.rowCount = 0;
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.overscrollRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.numberSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.numberRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.usernameRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.bioRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.settingsSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.settingsSectionRow2 = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.notificationRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.privacyRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.dataRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.chatRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.languageRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.helpRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.versionRow = i;
        DataQuery.getInstance(this.currentAccount).checkFeaturedStickers();
        this.userInfo = MessagesController.getInstance(this.currentAccount).getUserFull(UserConfig.getInstance(this.currentAccount).getClientUserId());
        MessagesController.getInstance(this.currentAccount).loadUserInfo(UserConfig.getInstance(this.currentAccount).getCurrentUser(), true, this.classGuid);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.avatarImage != null) {
            this.avatarImage.setImageDrawable(null);
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.userInfoDidLoad);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
        this.imageUpdater.clear();
    }

    public View createView(Context context) {
        int scrollTo;
        int i;
        int i2;
        float f;
        this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_avatar_backgroundActionBarBlue));
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_avatar_actionBarSelectorBlue), false);
        this.actionBar.setItemsColor(Theme.getColor(Theme.key_avatar_actionBarIconBlue), false);
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAddToContainer(false);
        this.extraHeight = 88;
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setActionBarMenuOnItemClick(new CLASSNAME());
        ActionBarMenuItem item = this.actionBar.createMenu().addItem(0, (int) R.drawable.ic_ab_other);
        item.addSubItem(1, LocaleController.getString("EditName", R.string.EditName));
        item.addSubItem(2, LocaleController.getString("LogOut", R.string.LogOut));
        int scrollToPosition = 0;
        Object writeButtonTag = null;
        if (this.listView != null) {
            scrollTo = this.layoutManager.findFirstVisibleItemPosition();
            View topView = this.layoutManager.findViewByPosition(scrollTo);
            if (topView != null) {
                scrollToPosition = topView.getTop();
            } else {
                scrollTo = -1;
            }
            writeButtonTag = this.writeButton.getTag();
        } else {
            scrollTo = -1;
        }
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context) {
            protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                if (child != SettingsActivity.this.listView) {
                    return super.drawChild(canvas, child, drawingTime);
                }
                boolean result = super.drawChild(canvas, child, drawingTime);
                if (SettingsActivity.this.parentLayout == null) {
                    return result;
                }
                int actionBarHeight = 0;
                int childCount = getChildCount();
                for (int a = 0; a < childCount; a++) {
                    View view = getChildAt(a);
                    if (view != child && (view instanceof CLASSNAMEActionBar) && view.getVisibility() == 0) {
                        if (((CLASSNAMEActionBar) view).getCastShadows()) {
                            actionBarHeight = view.getMeasuredHeight();
                        }
                        SettingsActivity.this.parentLayout.drawHeaderShadow(canvas, actionBarHeight);
                        return result;
                    }
                }
                SettingsActivity.this.parentLayout.drawHeaderShadow(canvas, actionBarHeight);
                return result;
            }
        };
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = this.fragmentView;
        this.listView = new RecyclerListView(context);
        this.listView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView = this.listView;
        LayoutManager CLASSNAME = new LinearLayoutManager(context, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = CLASSNAME;
        recyclerListView.setLayoutManager(CLASSNAME);
        this.listView.setGlowColor(Theme.getColor(Theme.key_avatar_backgroundActionBarBlue));
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        this.listView.setOnItemClickListener(new SettingsActivity$$Lambda$0(this, context));
        this.listView.setOnItemLongClickListener(new CLASSNAME());
        frameLayout.addView(this.actionBar);
        this.extraHeightView = new View(context);
        this.extraHeightView.setPivotY(0.0f);
        this.extraHeightView.setBackgroundColor(Theme.getColor(Theme.key_avatar_backgroundActionBarBlue));
        frameLayout.addView(this.extraHeightView, LayoutHelper.createFrame(-1, 88.0f));
        this.shadowView = new View(context);
        this.shadowView.setBackgroundResource(R.drawable.header_shadow);
        frameLayout.addView(this.shadowView, LayoutHelper.createFrame(-1, 3.0f));
        this.avatarContainer = new FrameLayout(context);
        this.avatarContainer.setPivotX(LocaleController.isRTL ? (float) AndroidUtilities.m9dp(42.0f) : 0.0f);
        this.avatarContainer.setPivotY(0.0f);
        View view = this.avatarContainer;
        int i3 = (LocaleController.isRTL ? 5 : 3) | 48;
        if (LocaleController.isRTL) {
            i = 0;
        } else {
            i = 64;
        }
        float f2 = (float) i;
        if (LocaleController.isRTL) {
            i2 = 64;
        } else {
            i2 = 0;
        }
        frameLayout.addView(view, LayoutHelper.createFrame(42, 42.0f, i3, f2, 0.0f, (float) i2, 0.0f));
        this.avatarContainer.setOnClickListener(new SettingsActivity$$Lambda$1(this));
        this.avatarImage = new BackupImageView(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.m9dp(21.0f));
        this.avatarContainer.addView(this.avatarImage, LayoutHelper.createFrame(42, 42.0f));
        final Paint paint = new Paint(1);
        paint.setColor(NUM);
        this.avatarProgressView = new RadialProgressView(context) {
            protected void onDraw(Canvas canvas) {
                if (SettingsActivity.this.avatarImage != null && SettingsActivity.this.avatarImage.getImageReceiver().hasNotThumb()) {
                    paint.setAlpha((int) (85.0f * SettingsActivity.this.avatarImage.getImageReceiver().getCurrentAlpha()));
                    canvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.m9dp(21.0f), paint);
                }
                super.onDraw(canvas);
            }
        };
        this.avatarProgressView.setSize(AndroidUtilities.m9dp(26.0f));
        this.avatarProgressView.setProgressColor(-1);
        this.avatarContainer.addView(this.avatarProgressView, LayoutHelper.createFrame(42, 42.0f));
        showAvatarProgress(false, false);
        this.nameTextView = new TextView(context) {
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                setPivotX(LocaleController.isRTL ? (float) getMeasuredWidth() : 0.0f);
            }
        };
        this.nameTextView.setTextColor(Theme.getColor(Theme.key_profile_title));
        this.nameTextView.setTextSize(1, 18.0f);
        this.nameTextView.setLines(1);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setEllipsize(TruncateAt.END);
        this.nameTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setPivotY(0.0f);
        frameLayout.addView(this.nameTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 48.0f : 118.0f, 0.0f, LocaleController.isRTL ? 118.0f : 48.0f, 0.0f));
        this.onlineTextView = new TextView(context);
        this.onlineTextView.setTextColor(Theme.getColor(Theme.key_avatar_subtitleInProfileBlue));
        this.onlineTextView.setTextSize(1, 14.0f);
        this.onlineTextView.setLines(1);
        this.onlineTextView.setMaxLines(1);
        this.onlineTextView.setSingleLine(true);
        this.onlineTextView.setEllipsize(TruncateAt.END);
        this.onlineTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        view = this.onlineTextView;
        i3 = (LocaleController.isRTL ? 5 : 3) | 48;
        if (LocaleController.isRTL) {
            f2 = 48.0f;
        } else {
            f2 = 118.0f;
        }
        if (LocaleController.isRTL) {
            f = 118.0f;
        } else {
            f = 48.0f;
        }
        frameLayout.addView(view, LayoutHelper.createFrame(-2, -2.0f, i3, f2, 0.0f, f, 0.0f));
        this.writeButton = new ImageView(context);
        Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.m9dp(56.0f), Theme.getColor(Theme.key_profile_actionBackground), Theme.getColor(Theme.key_profile_actionPressedBackground));
        if (VERSION.SDK_INT < 21) {
            Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow_profile).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.ACTION_BAR_VIDEO_EDIT_COLOR, Mode.MULTIPLY));
            Drawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.m9dp(56.0f), AndroidUtilities.m9dp(56.0f));
            drawable = combinedDrawable;
        }
        this.writeButton.setBackgroundDrawable(drawable);
        this.writeButton.setImageResource(R.drawable.menu_camera_av);
        this.writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_profile_actionIcon), Mode.MULTIPLY));
        this.writeButton.setScaleType(ScaleType.CENTER);
        if (VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.writeButton, "translationZ", new float[]{(float) AndroidUtilities.m9dp(2.0f), (float) AndroidUtilities.m9dp(4.0f)}).setDuration(200));
            animator.addState(new int[0], ObjectAnimator.ofFloat(this.writeButton, "translationZ", new float[]{(float) AndroidUtilities.m9dp(4.0f), (float) AndroidUtilities.m9dp(2.0f)}).setDuration(200));
            this.writeButton.setStateListAnimator(animator);
            this.writeButton.setOutlineProvider(new CLASSNAME());
        }
        frameLayout.addView(this.writeButton, LayoutHelper.createFrame(VERSION.SDK_INT >= 21 ? 56 : 60, VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, (LocaleController.isRTL ? 3 : 5) | 48, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 16.0f, 0.0f));
        this.writeButton.setOnClickListener(new SettingsActivity$$Lambda$2(this));
        if (scrollTo != -1) {
            this.layoutManager.scrollToPositionWithOffset(scrollTo, scrollToPosition);
            if (writeButtonTag != null) {
                this.writeButton.setTag(Integer.valueOf(0));
                this.writeButton.setScaleX(0.2f);
                this.writeButton.setScaleY(0.2f);
                this.writeButton.setAlpha(0.0f);
                this.writeButton.setVisibility(8);
            }
        }
        needLayout();
        this.listView.setOnScrollListener(new CLASSNAME());
        return this.fragmentView;
    }

    final /* synthetic */ void lambda$createView$3$SettingsActivity(Context context, View view, int position) {
        if (position == this.notificationRow) {
            presentFragment(new NotificationsSettingsActivity());
        } else if (position == this.privacyRow) {
            presentFragment(new PrivacySettingsActivity());
        } else if (position == this.dataRow) {
            presentFragment(new DataSettingsActivity());
        } else if (position == this.chatRow) {
            presentFragment(new ChatSettingsActivity());
        } else if (position == this.helpRow) {
            BottomSheet.Builder builder = new BottomSheet.Builder(context);
            builder.setApplyTopPadding(false);
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(1);
            HeaderCell headerCell = new HeaderCell(context, true, 23);
            headerCell.setHeight(47);
            headerCell.setText(LocaleController.getString("SettingsHelp", R.string.SettingsHelp));
            linearLayout.addView(headerCell);
            LinearLayout linearLayoutInviteContainer = new LinearLayout(context);
            linearLayoutInviteContainer.setOrientation(1);
            linearLayout.addView(linearLayoutInviteContainer, LayoutHelper.createLinear(-1, -2));
            int a = 0;
            while (a < 6) {
                if ((a < 3 || a > 4 || BuildVars.LOGS_ENABLED) && (a != 5 || BuildVars.DEBUG_VERSION)) {
                    String text;
                    TextCell textCell = new TextCell(context);
                    switch (a) {
                        case 0:
                            text = LocaleController.getString("AskAQuestion", R.string.AskAQuestion);
                            break;
                        case 1:
                            text = LocaleController.getString("TelegramFAQ", R.string.TelegramFAQ);
                            break;
                        case 2:
                            text = LocaleController.getString("PrivacyPolicy", R.string.PrivacyPolicy);
                            break;
                        case 3:
                            text = LocaleController.getString("DebugSendLogs", R.string.DebugSendLogs);
                            break;
                        case 4:
                            text = LocaleController.getString("DebugClearLogs", R.string.DebugClearLogs);
                            break;
                        default:
                            text = "Switch Backend";
                            break;
                    }
                    boolean z = (BuildVars.LOGS_ENABLED || BuildVars.DEBUG_VERSION) ? a != 5 : a != 2;
                    textCell.setText(text, z);
                    textCell.setTag(Integer.valueOf(a));
                    textCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                    linearLayoutInviteContainer.addView(textCell, LayoutHelper.createLinear(-1, -2));
                    textCell.setOnClickListener(new SettingsActivity$$Lambda$12(this, builder));
                }
                a++;
            }
            builder.setCustomView(linearLayout);
            showDialog(builder.create());
        } else if (position == this.languageRow) {
            presentFragment(new LanguageSelectActivity());
        } else if (position == this.usernameRow) {
            presentFragment(new ChangeUsernameActivity());
        } else if (position == this.bioRow) {
            if (this.userInfo != null) {
                presentFragment(new ChangeBioActivity());
            }
        } else if (position == this.numberRow) {
            presentFragment(new ChangePhoneHelpActivity());
        }
    }

    final /* synthetic */ void lambda$null$2$SettingsActivity(BottomSheet.Builder builder, View v2) {
        Builder builder1;
        switch (((Integer) v2.getTag()).intValue()) {
            case 0:
                if (getParentActivity() != null) {
                    TextView message = new TextView(getParentActivity());
                    Spannable spanned = new SpannableString(Html.fromHtml(LocaleController.getString("AskAQuestionInfo", R.string.AskAQuestionInfo).replace("\n", "<br>")));
                    URLSpan[] spans = (URLSpan[]) spanned.getSpans(0, spanned.length(), URLSpan.class);
                    for (URLSpan span : spans) {
                        int start = spanned.getSpanStart(span);
                        int end = spanned.getSpanEnd(span);
                        spanned.removeSpan(span);
                        spanned.setSpan(new URLSpanNoUnderline(span.getURL()) {
                            public void onClick(View widget) {
                                SettingsActivity.this.dismissCurrentDialig();
                                super.onClick(widget);
                            }
                        }, start, end, 0);
                    }
                    message.setText(spanned);
                    message.setTextSize(1, 16.0f);
                    message.setLinkTextColor(Theme.getColor(Theme.key_dialogTextLink));
                    message.setHighlightColor(Theme.getColor(Theme.key_dialogLinkSelection));
                    message.setPadding(AndroidUtilities.m9dp(23.0f), 0, AndroidUtilities.m9dp(23.0f), 0);
                    message.setMovementMethod(new LinkMovementMethodMy());
                    message.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                    builder1 = new Builder(getParentActivity());
                    builder1.setView(message);
                    builder1.setTitle(LocaleController.getString("AskAQuestion", R.string.AskAQuestion));
                    builder1.setPositiveButton(LocaleController.getString("AskButton", R.string.AskButton), new SettingsActivity$$Lambda$13(this));
                    builder1.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    showDialog(builder1.create());
                    break;
                }
                return;
            case 1:
                Browser.openUrl(getParentActivity(), LocaleController.getString("TelegramFaqUrl", R.string.TelegramFaqUrl));
                break;
            case 2:
                Browser.openUrl(getParentActivity(), LocaleController.getString("PrivacyPolicyUrl", R.string.PrivacyPolicyUrl));
                break;
            case 3:
                sendLogs();
                break;
            case 4:
                FileLog.cleanupLogs();
                break;
            case 5:
                if (getParentActivity() != null) {
                    builder1 = new Builder(getParentActivity());
                    builder1.setMessage(LocaleController.getString("AreYouSure", R.string.AreYouSure));
                    builder1.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder1.setPositiveButton(LocaleController.getString("OK", R.string.OK), new SettingsActivity$$Lambda$14(this));
                    builder1.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    showDialog(builder1.create());
                    break;
                }
                return;
        }
        builder.getDismissRunnable().run();
    }

    final /* synthetic */ void lambda$null$1$SettingsActivity(DialogInterface dialogInterface, int i) {
        SharedConfig.pushAuthKey = null;
        SharedConfig.pushAuthKeyId = null;
        SharedConfig.saveConfig();
        ConnectionsManager.getInstance(this.currentAccount).switchBackend();
    }

    final /* synthetic */ void lambda$createView$4$SettingsActivity(View v) {
        if (this.avatar == null) {
            User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
            if (user != null && user.photo != null && user.photo.photo_big != null) {
                PhotoViewer.getInstance().setParentActivity(getParentActivity());
                PhotoViewer.getInstance().openPhoto(user.photo.photo_big, this.provider);
            }
        }
    }

    final /* synthetic */ void lambda$createView$6$SettingsActivity(View v) {
        User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
        if (user == null) {
            user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
        }
        if (user != null) {
            ImageUpdater imageUpdater = this.imageUpdater;
            boolean z = (user.photo == null || user.photo.photo_big == null || (user.photo instanceof TL_userProfilePhotoEmpty)) ? false : true;
            imageUpdater.openMenu(z, new SettingsActivity$$Lambda$11(this));
        }
    }

    final /* synthetic */ void lambda$null$5$SettingsActivity() {
        MessagesController.getInstance(this.currentAccount).deleteUserPhoto(null);
    }

    public void didUploadPhoto(InputFile file, PhotoSize bigSize, PhotoSize smallSize) {
        AndroidUtilities.runOnUIThread(new SettingsActivity$$Lambda$3(this, file, smallSize, bigSize));
    }

    final /* synthetic */ void lambda$didUploadPhoto$9$SettingsActivity(InputFile file, PhotoSize smallSize, PhotoSize bigSize) {
        if (file != null) {
            TL_photos_uploadProfilePhoto req = new TL_photos_uploadProfilePhoto();
            req.file = file;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new SettingsActivity$$Lambda$9(this));
            return;
        }
        this.avatar = smallSize.location;
        this.avatarBig = bigSize.location;
        this.avatarImage.setImage(this.avatar, "50_50", this.avatarDrawable, null);
        showAvatarProgress(true, false);
    }

    final /* synthetic */ void lambda$null$8$SettingsActivity(TLObject response, TL_error error) {
        if (error == null) {
            User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
            if (user == null) {
                user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
                if (user != null) {
                    MessagesController.getInstance(this.currentAccount).putUser(user, false);
                } else {
                    return;
                }
            }
            UserConfig.getInstance(this.currentAccount).setCurrentUser(user);
            TL_photos_photo photo = (TL_photos_photo) response;
            ArrayList<PhotoSize> sizes = photo.photo.sizes;
            PhotoSize small = FileLoader.getClosestPhotoSizeWithSize(sizes, 150);
            PhotoSize big = FileLoader.getClosestPhotoSizeWithSize(sizes, 800);
            user.photo = new TL_userProfilePhoto();
            user.photo.photo_id = photo.photo.var_id;
            if (small != null) {
                user.photo.photo_small = small.location;
            }
            if (big != null) {
                user.photo.photo_big = big.location;
            } else if (small != null) {
                user.photo.photo_small = small.location;
            }
            if (photo != null) {
                if (!(small == null || this.avatar == null)) {
                    FileLoader.getPathToAttach(this.avatar, true).renameTo(FileLoader.getPathToAttach(small, true));
                    ImageLoader.getInstance().replaceImageInCache(this.avatar.volume_id + "_" + this.avatar.local_id + "@50_50", small.location.volume_id + "_" + small.location.local_id + "@50_50", small.location, true);
                }
                if (!(big == null || this.avatarBig == null)) {
                    FileLoader.getPathToAttach(this.avatarBig, true).renameTo(FileLoader.getPathToAttach(big, true));
                }
            }
            MessagesStorage.getInstance(this.currentAccount).clearUserPhotos(user.var_id);
            ArrayList<User> users = new ArrayList();
            users.add(user);
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(users, null, false, true);
        }
        AndroidUtilities.runOnUIThread(new SettingsActivity$$Lambda$10(this));
    }

    final /* synthetic */ void lambda$null$7$SettingsActivity() {
        this.avatar = null;
        this.avatarBig = null;
        updateUserData();
        showAvatarProgress(false, true);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_ALL));
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
        UserConfig.getInstance(this.currentAccount).saveConfig(true);
    }

    private void showAvatarProgress(final boolean show, boolean animated) {
        if (this.avatarProgressView != null) {
            if (this.avatarAnimation != null) {
                this.avatarAnimation.cancel();
                this.avatarAnimation = null;
            }
            if (animated) {
                this.avatarAnimation = new AnimatorSet();
                AnimatorSet animatorSet;
                Animator[] animatorArr;
                if (show) {
                    this.avatarProgressView.setVisibility(0);
                    animatorSet = this.avatarAnimation;
                    animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{1.0f});
                    animatorSet.playTogether(animatorArr);
                } else {
                    animatorSet = this.avatarAnimation;
                    animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{0.0f});
                    animatorSet.playTogether(animatorArr);
                }
                this.avatarAnimation.setDuration(180);
                this.avatarAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (SettingsActivity.this.avatarAnimation != null && SettingsActivity.this.avatarProgressView != null) {
                            if (!show) {
                                SettingsActivity.this.avatarProgressView.setVisibility(4);
                            }
                            SettingsActivity.this.avatarAnimation = null;
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        SettingsActivity.this.avatarAnimation = null;
                    }
                });
                this.avatarAnimation.start();
            } else if (show) {
                this.avatarProgressView.setAlpha(1.0f);
                this.avatarProgressView.setVisibility(0);
            } else {
                this.avatarProgressView.setAlpha(0.0f);
                this.avatarProgressView.setVisibility(4);
            }
        }
    }

    private void performAskAQuestion() {
        SharedPreferences preferences = MessagesController.getMainSettings(this.currentAccount);
        int uid = preferences.getInt("support_id", 0);
        User supportUser = null;
        if (uid != 0) {
            supportUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(uid));
            if (supportUser == null) {
                String userString = preferences.getString("support_user", null);
                if (userString != null) {
                    try {
                        byte[] datacentersBytes = Base64.decode(userString, 0);
                        if (datacentersBytes != null) {
                            SerializedData data = new SerializedData(datacentersBytes);
                            supportUser = User.TLdeserialize(data, data.readInt32(false), false);
                            if (supportUser != null && supportUser.var_id == 333000) {
                                supportUser = null;
                            }
                            data.cleanup();
                        }
                    } catch (Throwable e) {
                        FileLog.m13e(e);
                        supportUser = null;
                    }
                }
            }
        }
        if (supportUser == null) {
            AlertDialog progressDialog = new AlertDialog(getParentActivity(), 3);
            progressDialog.setCanCacnel(false);
            progressDialog.show();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_help_getSupport(), new SettingsActivity$$Lambda$4(this, preferences, progressDialog));
            return;
        }
        MessagesController.getInstance(this.currentAccount).putUser(supportUser, true);
        Bundle args = new Bundle();
        args.putInt("user_id", supportUser.var_id);
        presentFragment(new ChatActivity(args));
    }

    final /* synthetic */ void lambda$performAskAQuestion$12$SettingsActivity(SharedPreferences preferences, AlertDialog progressDialog, TLObject response, TL_error error) {
        if (error == null) {
            AndroidUtilities.runOnUIThread(new SettingsActivity$$Lambda$7(this, preferences, (TL_help_support) response, progressDialog));
        } else {
            AndroidUtilities.runOnUIThread(new SettingsActivity$$Lambda$8(progressDialog));
        }
    }

    final /* synthetic */ void lambda$null$10$SettingsActivity(SharedPreferences preferences, TL_help_support res, AlertDialog progressDialog) {
        Editor editor = preferences.edit();
        editor.putInt("support_id", res.user.var_id);
        SerializedData data = new SerializedData();
        res.user.serializeToStream(data);
        editor.putString("support_user", Base64.encodeToString(data.toByteArray(), 0));
        editor.commit();
        data.cleanup();
        try {
            progressDialog.dismiss();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
        ArrayList<User> users = new ArrayList();
        users.add(res.user);
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(users, null, true, true);
        MessagesController.getInstance(this.currentAccount).putUser(res.user, false);
        Bundle args = new Bundle();
        args.putInt("user_id", res.user.var_id);
        presentFragment(new ChatActivity(args));
    }

    static final /* synthetic */ void lambda$null$11$SettingsActivity(AlertDialog progressDialog) {
        try {
            progressDialog.dismiss();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        this.imageUpdater.onActivityResult(requestCode, resultCode, data);
    }

    public void saveSelfArgs(Bundle args) {
        if (this.imageUpdater != null && this.imageUpdater.currentPicturePath != null) {
            args.putString("path", this.imageUpdater.currentPicturePath);
        }
    }

    public void restoreSelfArgs(Bundle args) {
        if (this.imageUpdater != null) {
            this.imageUpdater.currentPicturePath = args.getString("path");
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.updateInterfaces) {
            int mask = ((Integer) args[0]).intValue();
            if ((mask & 2) != 0 || (mask & 1) != 0) {
                updateUserData();
            }
        } else if (id == NotificationCenter.userInfoDidLoad) {
            if (args[0].intValue() == UserConfig.getInstance(this.currentAccount).getClientUserId() && this.listAdapter != null) {
                this.userInfo = (TL_userFull) args[1];
                this.listAdapter.notifyItemChanged(this.bioRow);
            }
        } else if (id == NotificationCenter.emojiDidLoad && this.listView != null) {
            this.listView.invalidateViews();
        }
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
        updateUserData();
        fixLayout();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        fixLayout();
    }

    private void needLayout() {
        int newTop = (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + CLASSNAMEActionBar.getCurrentActionBarHeight();
        if (this.listView != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.listView.getLayoutParams();
            if (layoutParams.topMargin != newTop) {
                layoutParams.topMargin = newTop;
                this.listView.setLayoutParams(layoutParams);
                this.extraHeightView.setTranslationY((float) newTop);
            }
        }
        if (this.avatarContainer != null) {
            float diff = ((float) this.extraHeight) / ((float) AndroidUtilities.m9dp(88.0f));
            this.extraHeightView.setScaleY(diff);
            this.shadowView.setTranslationY((float) (this.extraHeight + newTop));
            this.writeButton.setTranslationY((float) ((((this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + CLASSNAMEActionBar.getCurrentActionBarHeight()) + this.extraHeight) - AndroidUtilities.m9dp(29.5f)));
            final boolean setVisible = diff > 0.2f;
            if (setVisible != (this.writeButton.getTag() == null)) {
                if (setVisible) {
                    this.writeButton.setTag(null);
                    this.writeButton.setVisibility(0);
                } else {
                    this.writeButton.setTag(Integer.valueOf(0));
                }
                if (this.writeButtonAnimation != null) {
                    AnimatorSet old = this.writeButtonAnimation;
                    this.writeButtonAnimation = null;
                    old.cancel();
                }
                this.writeButtonAnimation = new AnimatorSet();
                AnimatorSet animatorSet;
                Animator[] animatorArr;
                if (setVisible) {
                    this.writeButtonAnimation.setInterpolator(new DecelerateInterpolator());
                    animatorSet = this.writeButtonAnimation;
                    animatorArr = new Animator[3];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.writeButton, "scaleX", new float[]{1.0f});
                    animatorArr[1] = ObjectAnimator.ofFloat(this.writeButton, "scaleY", new float[]{1.0f});
                    animatorArr[2] = ObjectAnimator.ofFloat(this.writeButton, "alpha", new float[]{1.0f});
                    animatorSet.playTogether(animatorArr);
                } else {
                    this.writeButtonAnimation.setInterpolator(new AccelerateInterpolator());
                    animatorSet = this.writeButtonAnimation;
                    animatorArr = new Animator[3];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.writeButton, "scaleX", new float[]{0.2f});
                    animatorArr[1] = ObjectAnimator.ofFloat(this.writeButton, "scaleY", new float[]{0.2f});
                    animatorArr[2] = ObjectAnimator.ofFloat(this.writeButton, "alpha", new float[]{0.0f});
                    animatorSet.playTogether(animatorArr);
                }
                this.writeButtonAnimation.setDuration(150);
                this.writeButtonAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (SettingsActivity.this.writeButtonAnimation != null && SettingsActivity.this.writeButtonAnimation.equals(animation)) {
                            SettingsActivity.this.writeButton.setVisibility(setVisible ? 0 : 8);
                            SettingsActivity.this.writeButtonAnimation = null;
                        }
                    }
                });
                this.writeButtonAnimation.start();
            }
            this.avatarContainer.setScaleX((42.0f + (18.0f * diff)) / 42.0f);
            this.avatarContainer.setScaleY((42.0f + (18.0f * diff)) / 42.0f);
            this.avatarProgressView.setSize(AndroidUtilities.m9dp(26.0f / this.avatarContainer.getScaleX()));
            this.avatarProgressView.setStrokeWidth(3.0f / this.avatarContainer.getScaleX());
            float avatarY = ((((float) (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0)) + ((((float) CLASSNAMEActionBar.getCurrentActionBarHeight()) / 2.0f) * (1.0f + diff))) - (21.0f * AndroidUtilities.density)) + ((27.0f * AndroidUtilities.density) * diff);
            this.avatarContainer.setTranslationY((float) Math.ceil((double) avatarY));
            this.nameTextView.setTranslationY((((float) Math.floor((double) avatarY)) - ((float) Math.ceil((double) AndroidUtilities.density))) + ((float) Math.floor((double) ((7.0f * AndroidUtilities.density) * diff))));
            this.onlineTextView.setTranslationY((((float) Math.floor((double) avatarY)) + ((float) AndroidUtilities.m9dp(22.0f))) + (((float) Math.floor((double) (11.0f * AndroidUtilities.density))) * diff));
            this.nameTextView.setScaleX(1.0f + (0.12f * diff));
            this.nameTextView.setScaleY(1.0f + (0.12f * diff));
            if (LocaleController.isRTL) {
                this.avatarContainer.setTranslationX(((float) AndroidUtilities.m9dp(47.0f)) * diff);
                this.nameTextView.setTranslationX((21.0f * AndroidUtilities.density) * diff);
                this.onlineTextView.setTranslationX((21.0f * AndroidUtilities.density) * diff);
                return;
            }
            this.avatarContainer.setTranslationX(((float) (-AndroidUtilities.m9dp(47.0f))) * diff);
            this.nameTextView.setTranslationX((-21.0f * AndroidUtilities.density) * diff);
            this.onlineTextView.setTranslationX((-21.0f * AndroidUtilities.density) * diff);
        }
    }

    private void fixLayout() {
        if (this.fragmentView != null) {
            this.fragmentView.getViewTreeObserver().addOnPreDrawListener(new CLASSNAME());
        }
    }

    private void updateUserData() {
        boolean z = true;
        Object user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
        TLObject photo = null;
        FileLocation photoBig = null;
        if (user.photo != null) {
            photo = user.photo.photo_small;
            photoBig = user.photo.photo_big;
        }
        this.avatarDrawable = new AvatarDrawable((User) user, true);
        this.avatarDrawable.setColor(Theme.getColor(Theme.key_avatar_backgroundInProfileBlue));
        if (this.avatarImage != null) {
            this.avatarImage.setImage(photo, "50_50", this.avatarDrawable, user);
            this.avatarImage.getImageReceiver().setVisible(!PhotoViewer.isShowingImage(photoBig), false);
            this.nameTextView.setText(UserObject.getUserName(user));
            this.onlineTextView.setText(LocaleController.getString("Online", R.string.Online));
            ImageReceiver imageReceiver = this.avatarImage.getImageReceiver();
            if (PhotoViewer.isShowingImage(photoBig)) {
                z = false;
            }
            imageReceiver.setVisible(z, false);
        }
    }

    private void sendLogs() {
        if (getParentActivity() != null) {
            AlertDialog progressDialog = new AlertDialog(getParentActivity(), 3);
            progressDialog.setCanCacnel(false);
            progressDialog.show();
            Utilities.globalQueue.postRunnable(new SettingsActivity$$Lambda$5(this, progressDialog));
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:27:0x00b9 A:{SYNTHETIC, Splitter: B:27:0x00b9} */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00be A:{Catch:{ Exception -> 0x00fc }} */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x00f3 A:{Catch:{ Exception -> 0x00fc }} */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x00f8 A:{Catch:{ Exception -> 0x00fc }} */
    /* JADX WARNING: Missing block: B:39:?, code:
            r12[0] = true;
     */
    /* JADX WARNING: Missing block: B:40:0x00e2, code:
            if (r15 == null) goto L_0x00e7;
     */
    /* JADX WARNING: Missing block: B:42:?, code:
            r15.close();
     */
    /* JADX WARNING: Missing block: B:43:0x00e7, code:
            if (r0 == null) goto L_0x0111;
     */
    /* JADX WARNING: Missing block: B:44:0x00e9, code:
            r0.close();
            r16 = r0;
            r14 = r15;
     */
    /* JADX WARNING: Missing block: B:60:0x0111, code:
            r16 = r0;
            r14 = r15;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    final /* synthetic */ void lambda$sendLogs$14$SettingsActivity(AlertDialog progressDialog) {
        BufferedInputStream origin;
        Exception e;
        Throwable th;
        try {
            File dir = new File(ApplicationLoader.applicationContext.getExternalFilesDir(null).getAbsolutePath() + "/logs");
            File file = new File(dir, "logs.zip");
            if (file.exists()) {
                file.delete();
            }
            File[] files = dir.listFiles();
            boolean[] finished = new boolean[1];
            BufferedInputStream origin2 = null;
            ZipOutputStream out = null;
            try {
                ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
                try {
                    byte[] data = new byte[CLASSNAMEC.DEFAULT_BUFFER_SEGMENT_SIZE];
                    int i = 0;
                    while (true) {
                        try {
                            origin = origin2;
                            if (i >= files.length) {
                                break;
                            }
                            origin2 = new BufferedInputStream(new FileInputStream(files[i]), data.length);
                            zipOutputStream.putNextEntry(new ZipEntry(files[i].getName()));
                            while (true) {
                                int count = origin2.read(data, 0, data.length);
                                if (count == -1) {
                                    break;
                                }
                                zipOutputStream.write(data, 0, count);
                            }
                            if (origin2 != null) {
                                origin2.close();
                                origin2 = null;
                            }
                            i++;
                        } catch (Exception e2) {
                            e = e2;
                            out = zipOutputStream;
                            origin2 = origin;
                            try {
                                ThrowableExtension.printStackTrace(e);
                                if (origin2 != null) {
                                    origin2.close();
                                }
                                if (out != null) {
                                    out.close();
                                }
                                AndroidUtilities.runOnUIThread(new SettingsActivity$$Lambda$6(this, progressDialog, finished, file));
                            } catch (Throwable th2) {
                                th = th2;
                                if (origin2 != null) {
                                }
                                if (out != null) {
                                }
                                throw th;
                            }
                        } catch (Throwable th3) {
                            th = th3;
                            out = zipOutputStream;
                            origin2 = origin;
                            if (origin2 != null) {
                                origin2.close();
                            }
                            if (out != null) {
                                out.close();
                            }
                            throw th;
                        }
                    }
                } catch (Exception e3) {
                    e = e3;
                    out = zipOutputStream;
                } catch (Throwable th4) {
                    th = th4;
                    out = zipOutputStream;
                }
            } catch (Exception e4) {
                e = e4;
                ThrowableExtension.printStackTrace(e);
                if (origin2 != null) {
                }
                if (out != null) {
                }
                AndroidUtilities.runOnUIThread(new SettingsActivity$$Lambda$6(this, progressDialog, finished, file));
            }
            AndroidUtilities.runOnUIThread(new SettingsActivity$$Lambda$6(this, progressDialog, finished, file));
        } catch (Exception e5) {
            ThrowableExtension.printStackTrace(e5);
        }
    }

    final /* synthetic */ void lambda$null$13$SettingsActivity(AlertDialog progressDialog, boolean[] finished, File zipFile) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
        }
        if (finished[0]) {
            Uri uri;
            if (VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(getParentActivity(), "org.telegram.messenger.beta.provider", zipFile);
            } else {
                uri = Uri.fromFile(zipFile);
            }
            Intent i = new Intent("android.intent.action.SEND");
            if (VERSION.SDK_INT >= 24) {
                i.addFlags(1);
            }
            i.setType("message/rfCLASSNAME");
            i.putExtra("android.intent.extra.EMAIL", TtmlNode.ANONYMOUS_REGION_ID);
            i.putExtra("android.intent.extra.SUBJECT", "Logs from " + LocaleController.getInstance().formatterStats.format(System.currentTimeMillis()));
            i.putExtra("android.intent.extra.STREAM", uri);
            getParentActivity().startActivityForResult(Intent.createChooser(i, "Select email application."), 500);
            return;
        }
        Toast.makeText(getParentActivity(), LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred), 0).show();
    }

    public ThemeDescription[] getThemeDescriptions() {
        r9 = new ThemeDescription[28];
        r9[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{EmptyCell.class, HeaderCell.class, TextDetailCell.class, TextCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        r9[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        r9[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_avatar_backgroundActionBarBlue);
        r9[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_avatar_backgroundActionBarBlue);
        r9[4] = new ThemeDescription(this.extraHeightView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_avatar_backgroundActionBarBlue);
        r9[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_avatar_actionBarIconBlue);
        r9[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        r9[7] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_avatar_actionBarSelectorBlue);
        r9[8] = new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_profile_title);
        r9[9] = new ThemeDescription(this.onlineTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_avatar_subtitleInProfileBlue);
        r9[10] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, Theme.key_actionBarDefaultSubmenuBackground);
        r9[11] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, Theme.key_actionBarDefaultSubmenuItem);
        r9[12] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r9[13] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        r9[14] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r9[15] = new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r9[16] = new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText);
        r9[17] = new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayIcon);
        r9[18] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader);
        r9[19] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r9[20] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r9[21] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r9[22] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        r9[23] = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[]{Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, Theme.key_avatar_text);
        r9[24] = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[]{this.avatarDrawable}, null, Theme.key_avatar_backgroundInProfileBlue);
        r9[25] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_profile_actionIcon);
        r9[26] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_profile_actionBackground);
        r9[27] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_profile_actionPressedBackground);
        return r9;
    }
}
