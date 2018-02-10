package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
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
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
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
import org.telegram.messenger.beta.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
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
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.BottomSheet.BottomSheetCell;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextInfoCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.AvatarUpdater;
import org.telegram.ui.Components.AvatarUpdater.AvatarUpdaterDelegate;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberPicker;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PlaceProviderObject;

public class SettingsActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int edit_name = 1;
    private static final int logout = 2;
    private int askQuestionRow;
    private int autoplayGifsRow;
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImage;
    private AvatarUpdater avatarUpdater = new AvatarUpdater();
    private int backgroundRow;
    private int bioRow;
    private int clearLogsRow;
    private int contactsReimportRow;
    private int contactsSectionRow;
    private int contactsSortRow;
    private int customTabsRow;
    private int dataRow;
    private int directShareRow;
    private int dumpCallStatsRow;
    private int emojiRow;
    private int emptyRow;
    private int enableAnimationsRow;
    private int extraHeight;
    private View extraHeightView;
    private int languageRow;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private int messagesSectionRow;
    private int messagesSectionRow2;
    private TextView nameTextView;
    private int notificationRow;
    private int numberRow;
    private int numberSectionRow;
    private TextView onlineTextView;
    private int overscrollRow;
    private int privacyPolicyRow;
    private int privacyRow;
    private PhotoViewerProvider provider = new EmptyPhotoViewerProvider() {
        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
            PlaceProviderObject placeProviderObject = null;
            int i = 0;
            if (fileLocation != null) {
                User user = MessagesController.getInstance(SettingsActivity.this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(SettingsActivity.this.currentAccount).getClientUserId()));
                if (!(user == null || user.photo == null || user.photo.photo_big == null)) {
                    FileLocation photoBig = user.photo.photo_big;
                    if (photoBig.local_id == fileLocation.local_id && photoBig.volume_id == fileLocation.volume_id && photoBig.dc_id == fileLocation.dc_id) {
                        int[] coords = new int[2];
                        SettingsActivity.this.avatarImage.getLocationInWindow(coords);
                        placeProviderObject = new PlaceProviderObject();
                        placeProviderObject.viewX = coords[0];
                        int i2 = coords[1];
                        if (VERSION.SDK_INT < 21) {
                            i = AndroidUtilities.statusBarHeight;
                        }
                        placeProviderObject.viewY = i2 - i;
                        placeProviderObject.parentView = SettingsActivity.this.avatarImage;
                        placeProviderObject.imageReceiver = SettingsActivity.this.avatarImage.getImageReceiver();
                        placeProviderObject.dialogId = UserConfig.getInstance(SettingsActivity.this.currentAccount).getClientUserId();
                        placeProviderObject.thumb = placeProviderObject.imageReceiver.getBitmapSafe();
                        placeProviderObject.size = -1;
                        placeProviderObject.radius = SettingsActivity.this.avatarImage.getImageReceiver().getRoundRadius();
                        placeProviderObject.scale = SettingsActivity.this.avatarImage.getScaleX();
                    }
                }
            }
            return placeProviderObject;
        }

        public void willHidePhotoViewer() {
            SettingsActivity.this.avatarImage.getImageReceiver().setVisible(true, true);
        }
    };
    private int raiseToSpeakRow;
    private int rowCount;
    private int saveToGalleryRow;
    private int sendByEnterRow;
    private int sendLogsRow;
    private int settingsSectionRow;
    private int settingsSectionRow2;
    private View shadowView;
    private int stickersRow;
    private int supportSectionRow;
    private int supportSectionRow2;
    private int switchBackendButtonRow;
    private int telegramFaqRow;
    private int textSizeRow;
    private int themeRow;
    private int usernameRow;
    private int versionRow;
    private ImageView writeButton;
    private AnimatorSet writeButtonAnimation;

    private static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            try {
                return super.onTouchEvent(widget, buffer, event);
            } catch (Throwable e) {
                FileLog.e(e);
                return false;
            }
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return SettingsActivity.this.rowCount;
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            String value;
            switch (holder.getItemViewType()) {
                case 0:
                    if (position == SettingsActivity.this.overscrollRow) {
                        ((EmptyCell) holder.itemView).setHeight(AndroidUtilities.dp(88.0f));
                        return;
                    } else {
                        ((EmptyCell) holder.itemView).setHeight(AndroidUtilities.dp(16.0f));
                        return;
                    }
                case 2:
                    TextSettingsCell textCell = holder.itemView;
                    if (position == SettingsActivity.this.textSizeRow) {
                        int size = MessagesController.getGlobalMainSettings().getInt("fons_size", AndroidUtilities.isTablet() ? 18 : 16);
                        textCell.setTextAndValue(LocaleController.getString("TextSize", R.string.TextSize), String.format("%d", new Object[]{Integer.valueOf(size)}), true);
                        return;
                    } else if (position == SettingsActivity.this.languageRow) {
                        textCell.setTextAndValue(LocaleController.getString("Language", R.string.Language), LocaleController.getCurrentLanguageName(), true);
                        return;
                    } else if (position == SettingsActivity.this.themeRow) {
                        textCell.setTextAndValue(LocaleController.getString("Theme", R.string.Theme), Theme.getCurrentThemeName(), true);
                        return;
                    } else if (position == SettingsActivity.this.contactsSortRow) {
                        int sort = MessagesController.getGlobalMainSettings().getInt("sortContactsBy", 0);
                        if (sort == 0) {
                            value = LocaleController.getString("Default", R.string.Default);
                        } else if (sort == 1) {
                            value = LocaleController.getString("FirstName", R.string.SortFirstName);
                        } else {
                            value = LocaleController.getString("LastName", R.string.SortLastName);
                        }
                        textCell.setTextAndValue(LocaleController.getString("SortBy", R.string.SortBy), value, true);
                        return;
                    } else if (position == SettingsActivity.this.notificationRow) {
                        textCell.setText(LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds), true);
                        return;
                    } else if (position == SettingsActivity.this.backgroundRow) {
                        textCell.setText(LocaleController.getString("ChatBackground", R.string.ChatBackground), true);
                        return;
                    } else if (position == SettingsActivity.this.sendLogsRow) {
                        textCell.setText(LocaleController.getString("DebugSendLogs", R.string.DebugSendLogs), true);
                        return;
                    } else if (position == SettingsActivity.this.clearLogsRow) {
                        textCell.setText(LocaleController.getString("DebugClearLogs", R.string.DebugClearLogs), true);
                        return;
                    } else if (position == SettingsActivity.this.askQuestionRow) {
                        textCell.setText(LocaleController.getString("AskAQuestion", R.string.AskAQuestion), true);
                        return;
                    } else if (position == SettingsActivity.this.privacyRow) {
                        textCell.setText(LocaleController.getString("PrivacySettings", R.string.PrivacySettings), true);
                        return;
                    } else if (position == SettingsActivity.this.dataRow) {
                        textCell.setText(LocaleController.getString("DataSettings", R.string.DataSettings), true);
                        return;
                    } else if (position == SettingsActivity.this.switchBackendButtonRow) {
                        textCell.setText("Switch Backend", true);
                        return;
                    } else if (position == SettingsActivity.this.telegramFaqRow) {
                        textCell.setText(LocaleController.getString("TelegramFAQ", R.string.TelegramFAQ), true);
                        return;
                    } else if (position == SettingsActivity.this.contactsReimportRow) {
                        textCell.setText(LocaleController.getString("ImportContacts", R.string.ImportContacts), true);
                        return;
                    } else if (position == SettingsActivity.this.stickersRow) {
                        textCell.setTextAndValue(LocaleController.getString("StickersName", R.string.StickersName), DataQuery.getInstance(SettingsActivity.this.currentAccount).getUnreadStickerSets().size() != 0 ? String.format("%d", new Object[]{Integer.valueOf(DataQuery.getInstance(SettingsActivity.this.currentAccount).getUnreadStickerSets().size())}) : TtmlNode.ANONYMOUS_REGION_ID, true);
                        return;
                    } else if (position == SettingsActivity.this.privacyPolicyRow) {
                        textCell.setText(LocaleController.getString("PrivacyPolicy", R.string.PrivacyPolicy), true);
                        return;
                    } else if (position == SettingsActivity.this.emojiRow) {
                        textCell.setText(LocaleController.getString("Emoji", R.string.Emoji), true);
                        return;
                    } else {
                        return;
                    }
                case 3:
                    TextCheckCell textCell2 = holder.itemView;
                    SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                    if (position == SettingsActivity.this.enableAnimationsRow) {
                        textCell2.setTextAndCheck(LocaleController.getString("EnableAnimations", R.string.EnableAnimations), preferences.getBoolean("view_animations", true), false);
                        return;
                    } else if (position == SettingsActivity.this.sendByEnterRow) {
                        textCell2.setTextAndCheck(LocaleController.getString("SendByEnter", R.string.SendByEnter), preferences.getBoolean("send_by_enter", false), true);
                        return;
                    } else if (position == SettingsActivity.this.saveToGalleryRow) {
                        textCell2.setTextAndCheck(LocaleController.getString("SaveToGallerySettings", R.string.SaveToGallerySettings), SharedConfig.saveToGallery, false);
                        return;
                    } else if (position == SettingsActivity.this.autoplayGifsRow) {
                        textCell2.setTextAndCheck(LocaleController.getString("AutoplayGifs", R.string.AutoplayGifs), SharedConfig.autoplayGifs, true);
                        return;
                    } else if (position == SettingsActivity.this.raiseToSpeakRow) {
                        textCell2.setTextAndCheck(LocaleController.getString("RaiseToSpeak", R.string.RaiseToSpeak), SharedConfig.raiseToSpeak, true);
                        return;
                    } else if (position == SettingsActivity.this.customTabsRow) {
                        textCell2.setTextAndValueAndCheck(LocaleController.getString("ChromeCustomTabs", R.string.ChromeCustomTabs), LocaleController.getString("ChromeCustomTabsInfo", R.string.ChromeCustomTabsInfo), SharedConfig.customTabs, false, true);
                        return;
                    } else if (position == SettingsActivity.this.directShareRow) {
                        textCell2.setTextAndValueAndCheck(LocaleController.getString("DirectShare", R.string.DirectShare), LocaleController.getString("DirectShareInfo", R.string.DirectShareInfo), SharedConfig.directShare, false, true);
                        return;
                    } else if (position == SettingsActivity.this.dumpCallStatsRow) {
                        textCell2.setTextAndCheck("Dump detailed call stats", preferences.getBoolean("dbg_dump_call_stats", false), true);
                        return;
                    } else {
                        return;
                    }
                case 4:
                    if (position == SettingsActivity.this.settingsSectionRow2) {
                        ((HeaderCell) holder.itemView).setText(LocaleController.getString("SETTINGS", R.string.SETTINGS));
                        return;
                    } else if (position == SettingsActivity.this.supportSectionRow2) {
                        ((HeaderCell) holder.itemView).setText(LocaleController.getString("Support", R.string.Support));
                        return;
                    } else if (position == SettingsActivity.this.messagesSectionRow2) {
                        ((HeaderCell) holder.itemView).setText(LocaleController.getString("MessagesSettings", R.string.MessagesSettings));
                        return;
                    } else if (position == SettingsActivity.this.numberSectionRow) {
                        ((HeaderCell) holder.itemView).setText(LocaleController.getString("Info", R.string.Info));
                        return;
                    } else {
                        return;
                    }
                case 6:
                    TextDetailSettingsCell textCell3 = holder.itemView;
                    User user;
                    if (position == SettingsActivity.this.numberRow) {
                        user = UserConfig.getInstance(SettingsActivity.this.currentAccount).getCurrentUser();
                        if (user == null || user.phone == null || user.phone.length() == 0) {
                            value = LocaleController.getString("NumberUnknown", R.string.NumberUnknown);
                        } else {
                            value = PhoneFormat.getInstance().format("+" + user.phone);
                        }
                        textCell3.setTextAndValue(value, LocaleController.getString("Phone", R.string.Phone), true);
                        return;
                    } else if (position == SettingsActivity.this.usernameRow) {
                        user = UserConfig.getInstance(SettingsActivity.this.currentAccount).getCurrentUser();
                        if (user == null || TextUtils.isEmpty(user.username)) {
                            value = LocaleController.getString("UsernameEmpty", R.string.UsernameEmpty);
                        } else {
                            value = "@" + user.username;
                        }
                        textCell3.setTextAndValue(value, LocaleController.getString("Username", R.string.Username), true);
                        return;
                    } else if (position == SettingsActivity.this.bioRow) {
                        TL_userFull userFull = MessagesController.getInstance(SettingsActivity.this.currentAccount).getUserFull(UserConfig.getInstance(SettingsActivity.this.currentAccount).getClientUserId());
                        if (userFull == null) {
                            value = LocaleController.getString("Loading", R.string.Loading);
                        } else if (TextUtils.isEmpty(userFull.about)) {
                            value = LocaleController.getString("UserBioEmpty", R.string.UserBioEmpty);
                        } else {
                            value = userFull.about;
                        }
                        textCell3.setTextWithEmojiAndValue(value, LocaleController.getString("UserBio", R.string.UserBio), false);
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

        public boolean isEnabled(ViewHolder holder) {
            int position = holder.getAdapterPosition();
            if (position == SettingsActivity.this.textSizeRow || position == SettingsActivity.this.enableAnimationsRow || position == SettingsActivity.this.notificationRow || position == SettingsActivity.this.backgroundRow || position == SettingsActivity.this.numberRow || position == SettingsActivity.this.askQuestionRow || position == SettingsActivity.this.sendLogsRow || position == SettingsActivity.this.sendByEnterRow || position == SettingsActivity.this.autoplayGifsRow || position == SettingsActivity.this.privacyRow || position == SettingsActivity.this.clearLogsRow || position == SettingsActivity.this.languageRow || position == SettingsActivity.this.usernameRow || position == SettingsActivity.this.bioRow || position == SettingsActivity.this.switchBackendButtonRow || position == SettingsActivity.this.telegramFaqRow || position == SettingsActivity.this.contactsSortRow || position == SettingsActivity.this.contactsReimportRow || position == SettingsActivity.this.saveToGalleryRow || position == SettingsActivity.this.stickersRow || position == SettingsActivity.this.raiseToSpeakRow || position == SettingsActivity.this.privacyPolicyRow || position == SettingsActivity.this.customTabsRow || position == SettingsActivity.this.directShareRow || position == SettingsActivity.this.versionRow || position == SettingsActivity.this.emojiRow || position == SettingsActivity.this.dataRow || position == SettingsActivity.this.themeRow || position == SettingsActivity.this.dumpCallStatsRow) {
                return true;
            }
            return false;
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
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 3:
                    view = new TextCheckCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 4:
                    view = new HeaderCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 5:
                    view = new TextInfoCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
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
                        TextInfoCell textInfoCell = (TextInfoCell) view;
                        Object[] objArr = new Object[1];
                        objArr[0] = String.format(Locale.US, "v%s (%d) %s", new Object[]{pInfo.versionName, Integer.valueOf(code), abi});
                        textInfoCell.setText(LocaleController.formatString("TelegramVersion", R.string.TelegramVersion, objArr));
                        break;
                    } catch (Throwable e) {
                        FileLog.e(e);
                        break;
                    }
                case 6:
                    view = new TextDetailSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
        }

        public int getItemViewType(int position) {
            if (position == SettingsActivity.this.emptyRow || position == SettingsActivity.this.overscrollRow) {
                return 0;
            }
            if (position == SettingsActivity.this.settingsSectionRow || position == SettingsActivity.this.supportSectionRow || position == SettingsActivity.this.messagesSectionRow || position == SettingsActivity.this.contactsSectionRow) {
                return 1;
            }
            if (position == SettingsActivity.this.enableAnimationsRow || position == SettingsActivity.this.sendByEnterRow || position == SettingsActivity.this.saveToGalleryRow || position == SettingsActivity.this.autoplayGifsRow || position == SettingsActivity.this.raiseToSpeakRow || position == SettingsActivity.this.customTabsRow || position == SettingsActivity.this.directShareRow || position == SettingsActivity.this.dumpCallStatsRow) {
                return 3;
            }
            if (position == SettingsActivity.this.notificationRow || position == SettingsActivity.this.themeRow || position == SettingsActivity.this.backgroundRow || position == SettingsActivity.this.askQuestionRow || position == SettingsActivity.this.sendLogsRow || position == SettingsActivity.this.privacyRow || position == SettingsActivity.this.clearLogsRow || position == SettingsActivity.this.switchBackendButtonRow || position == SettingsActivity.this.telegramFaqRow || position == SettingsActivity.this.contactsReimportRow || position == SettingsActivity.this.textSizeRow || position == SettingsActivity.this.languageRow || position == SettingsActivity.this.contactsSortRow || position == SettingsActivity.this.stickersRow || position == SettingsActivity.this.privacyPolicyRow || position == SettingsActivity.this.emojiRow || position == SettingsActivity.this.dataRow) {
                return 2;
            }
            if (position == SettingsActivity.this.versionRow) {
                return 5;
            }
            if (position == SettingsActivity.this.numberRow || position == SettingsActivity.this.usernameRow || position == SettingsActivity.this.bioRow) {
                return 6;
            }
            if (position == SettingsActivity.this.settingsSectionRow2 || position == SettingsActivity.this.messagesSectionRow2 || position == SettingsActivity.this.supportSectionRow2 || position == SettingsActivity.this.numberSectionRow) {
                return 4;
            }
            return 2;
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.avatarUpdater.parentFragment = this;
        this.avatarUpdater.delegate = new AvatarUpdaterDelegate() {
            public void didUploadedPhoto(InputFile file, PhotoSize small, PhotoSize big) {
                TL_photos_uploadProfilePhoto req = new TL_photos_uploadProfilePhoto();
                req.file = file;
                ConnectionsManager.getInstance(SettingsActivity.this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                        if (error == null) {
                            User user = MessagesController.getInstance(SettingsActivity.this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(SettingsActivity.this.currentAccount).getClientUserId()));
                            if (user == null) {
                                user = UserConfig.getInstance(SettingsActivity.this.currentAccount).getCurrentUser();
                                if (user != null) {
                                    MessagesController.getInstance(SettingsActivity.this.currentAccount).putUser(user, false);
                                } else {
                                    return;
                                }
                            }
                            UserConfig.getInstance(SettingsActivity.this.currentAccount).setCurrentUser(user);
                            TL_photos_photo photo = (TL_photos_photo) response;
                            ArrayList<PhotoSize> sizes = photo.photo.sizes;
                            PhotoSize smallSize = FileLoader.getClosestPhotoSizeWithSize(sizes, 100);
                            PhotoSize bigSize = FileLoader.getClosestPhotoSizeWithSize(sizes, 1000);
                            user.photo = new TL_userProfilePhoto();
                            user.photo.photo_id = photo.photo.id;
                            if (smallSize != null) {
                                user.photo.photo_small = smallSize.location;
                            }
                            if (bigSize != null) {
                                user.photo.photo_big = bigSize.location;
                            } else if (smallSize != null) {
                                user.photo.photo_small = smallSize.location;
                            }
                            MessagesStorage.getInstance(SettingsActivity.this.currentAccount).clearUserPhotos(user.id);
                            ArrayList<User> users = new ArrayList();
                            users.add(user);
                            MessagesStorage.getInstance(SettingsActivity.this.currentAccount).putUsersAndChats(users, null, false, true);
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    NotificationCenter.getInstance(SettingsActivity.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_ALL));
                                    NotificationCenter.getInstance(SettingsActivity.this.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                                    UserConfig.getInstance(SettingsActivity.this.currentAccount).saveConfig(true);
                                }
                            });
                        }
                    }
                });
            }
        };
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.featuredStickersDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.userInfoDidLoaded);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
        this.rowCount = 0;
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.overscrollRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.emptyRow = i;
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
        this.backgroundRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.themeRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.languageRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.enableAnimationsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.messagesSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.messagesSectionRow2 = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.customTabsRow = i;
        if (VERSION.SDK_INT >= 23) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.directShareRow = i;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.stickersRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.textSizeRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.raiseToSpeakRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.sendByEnterRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.autoplayGifsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.saveToGalleryRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.supportSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.supportSectionRow2 = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.askQuestionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.telegramFaqRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.privacyPolicyRow = i;
        if (BuildVars.LOGS_ENABLED) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.sendLogsRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.clearLogsRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.dumpCallStatsRow = i;
        } else {
            this.sendLogsRow = -1;
            this.clearLogsRow = -1;
            this.dumpCallStatsRow = -1;
        }
        if (BuildVars.DEBUG_VERSION) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.switchBackendButtonRow = i;
        } else {
            this.switchBackendButtonRow = -1;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.versionRow = i;
        DataQuery.getInstance(this.currentAccount).checkFeaturedStickers();
        MessagesController.getInstance(this.currentAccount).loadFullUser(UserConfig.getInstance(this.currentAccount).getCurrentUser(), this.classGuid, true);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.avatarImage != null) {
            this.avatarImage.setImageDrawable(null);
        }
        MessagesController.getInstance(this.currentAccount).cancelLoadFullUser(UserConfig.getInstance(this.currentAccount).getClientUserId());
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.featuredStickersDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.userInfoDidLoaded);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
        this.avatarUpdater.clear();
    }

    public View createView(Context context) {
        this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_avatar_backgroundActionBarBlue));
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_avatar_actionBarSelectorBlue), false);
        this.actionBar.setItemsColor(Theme.getColor(Theme.key_avatar_actionBarIconBlue), false);
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAddToContainer(false);
        this.extraHeight = 88;
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    SettingsActivity.this.finishFragment();
                } else if (id == 1) {
                    SettingsActivity.this.presentFragment(new ChangeNameActivity());
                } else if (id == 2 && SettingsActivity.this.getParentActivity() != null) {
                    Builder builder = new Builder(SettingsActivity.this.getParentActivity());
                    builder.setMessage(LocaleController.getString("AreYouSureLogout", R.string.AreYouSureLogout));
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            MessagesController.getInstance(SettingsActivity.this.currentAccount).performLogout(true);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    SettingsActivity.this.showDialog(builder.create());
                }
            }
        });
        ActionBarMenuItem item = this.actionBar.createMenu().addItem(0, (int) R.drawable.ic_ab_other);
        item.addSubItem(1, LocaleController.getString("EditName", R.string.EditName));
        item.addSubItem(2, LocaleController.getString("LogOut", R.string.LogOut));
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
                    if (view != child && (view instanceof ActionBar) && view.getVisibility() == 0) {
                        if (((ActionBar) view).getCastShadows()) {
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
        LayoutManager anonymousClass5 = new LinearLayoutManager(context, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = anonymousClass5;
        recyclerListView.setLayoutManager(anonymousClass5);
        this.listView.setGlowColor(Theme.getColor(Theme.key_avatar_backgroundActionBarBlue));
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(View view, int position) {
                Builder builder;
                if (position == SettingsActivity.this.textSizeRow) {
                    if (SettingsActivity.this.getParentActivity() != null) {
                        builder = new Builder(SettingsActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString("TextSize", R.string.TextSize));
                        View numberPicker = new NumberPicker(SettingsActivity.this.getParentActivity());
                        numberPicker.setMinValue(12);
                        numberPicker.setMaxValue(30);
                        numberPicker.setValue(SharedConfig.fontSize);
                        builder.setView(numberPicker);
                        final View view2 = numberPicker;
                        final int i = position;
                        builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Editor editor = MessagesController.getGlobalMainSettings().edit();
                                editor.putInt("fons_size", view2.getValue());
                                SharedConfig.fontSize = view2.getValue();
                                editor.commit();
                                if (SettingsActivity.this.listAdapter != null) {
                                    SettingsActivity.this.listAdapter.notifyItemChanged(i);
                                }
                            }
                        });
                        SettingsActivity.this.showDialog(builder.create());
                    }
                } else if (position == SettingsActivity.this.enableAnimationsRow) {
                    preferences = MessagesController.getGlobalMainSettings();
                    boolean animations = preferences.getBoolean("view_animations", true);
                    editor = preferences.edit();
                    editor.putBoolean("view_animations", !animations);
                    editor.commit();
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(!animations);
                    }
                } else if (position == SettingsActivity.this.notificationRow) {
                    SettingsActivity.this.presentFragment(new NotificationsSettingsActivity());
                } else if (position == SettingsActivity.this.backgroundRow) {
                    SettingsActivity.this.presentFragment(new WallpapersActivity());
                } else if (position == SettingsActivity.this.askQuestionRow) {
                    if (SettingsActivity.this.getParentActivity() != null) {
                        TextView message = new TextView(SettingsActivity.this.getParentActivity());
                        Spannable spannableString = new SpannableString(Html.fromHtml(LocaleController.getString("AskAQuestionInfo", R.string.AskAQuestionInfo).replace("\n", "<br>")));
                        URLSpan[] spans = (URLSpan[]) spannableString.getSpans(0, spannableString.length(), URLSpan.class);
                        for (URLSpan span : spans) {
                            int start = spannableString.getSpanStart(span);
                            int end = spannableString.getSpanEnd(span);
                            spannableString.removeSpan(span);
                            spannableString.setSpan(new URLSpanNoUnderline(span.getURL()), start, end, 0);
                        }
                        message.setText(spannableString);
                        message.setTextSize(1, 16.0f);
                        message.setLinkTextColor(Theme.getColor(Theme.key_dialogTextLink));
                        message.setHighlightColor(Theme.getColor(Theme.key_dialogLinkSelection));
                        message.setPadding(AndroidUtilities.dp(23.0f), 0, AndroidUtilities.dp(23.0f), 0);
                        message.setMovementMethod(new LinkMovementMethodMy());
                        message.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                        builder = new Builder(SettingsActivity.this.getParentActivity());
                        builder.setView(message);
                        builder.setTitle(LocaleController.getString("AskAQuestion", R.string.AskAQuestion));
                        builder.setPositiveButton(LocaleController.getString("AskButton", R.string.AskButton), new OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SettingsActivity.this.performAskAQuestion();
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        SettingsActivity.this.showDialog(builder.create());
                    }
                } else if (position == SettingsActivity.this.sendLogsRow) {
                    SettingsActivity.this.sendLogs();
                } else if (position == SettingsActivity.this.clearLogsRow) {
                    FileLog.cleanupLogs();
                } else if (position == SettingsActivity.this.sendByEnterRow) {
                    preferences = MessagesController.getGlobalMainSettings();
                    boolean send = preferences.getBoolean("send_by_enter", false);
                    editor = preferences.edit();
                    editor.putBoolean("send_by_enter", !send);
                    editor.commit();
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(!send);
                    }
                } else if (position == SettingsActivity.this.raiseToSpeakRow) {
                    SharedConfig.toogleRaiseToSpeak();
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(SharedConfig.raiseToSpeak);
                    }
                } else if (position == SettingsActivity.this.autoplayGifsRow) {
                    SharedConfig.toggleAutoplayGifs();
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(SharedConfig.autoplayGifs);
                    }
                } else if (position == SettingsActivity.this.saveToGalleryRow) {
                    SharedConfig.toggleSaveToGallery();
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(SharedConfig.saveToGallery);
                    }
                } else if (position == SettingsActivity.this.customTabsRow) {
                    SharedConfig.toggleCustomTabs();
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(SharedConfig.customTabs);
                    }
                } else if (position == SettingsActivity.this.directShareRow) {
                    SharedConfig.toggleDirectShare();
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(SharedConfig.directShare);
                    }
                } else if (position == SettingsActivity.this.privacyRow) {
                    SettingsActivity.this.presentFragment(new PrivacySettingsActivity());
                } else if (position == SettingsActivity.this.dataRow) {
                    SettingsActivity.this.presentFragment(new DataSettingsActivity());
                } else if (position == SettingsActivity.this.languageRow) {
                    SettingsActivity.this.presentFragment(new LanguageSelectActivity());
                } else if (position == SettingsActivity.this.themeRow) {
                    SettingsActivity.this.presentFragment(new ThemeActivity(0));
                } else if (position == SettingsActivity.this.switchBackendButtonRow) {
                    if (SettingsActivity.this.getParentActivity() != null) {
                        builder = new Builder(SettingsActivity.this.getParentActivity());
                        builder.setMessage(LocaleController.getString("AreYouSure", R.string.AreYouSure));
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ConnectionsManager.getInstance(SettingsActivity.this.currentAccount).switchBackend();
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        SettingsActivity.this.showDialog(builder.create());
                    }
                } else if (position == SettingsActivity.this.telegramFaqRow) {
                    Browser.openUrl(SettingsActivity.this.getParentActivity(), LocaleController.getString("TelegramFaqUrl", R.string.TelegramFaqUrl));
                } else if (position == SettingsActivity.this.privacyPolicyRow) {
                    Browser.openUrl(SettingsActivity.this.getParentActivity(), LocaleController.getString("PrivacyPolicyUrl", R.string.PrivacyPolicyUrl));
                } else if (position == SettingsActivity.this.contactsReimportRow) {
                } else {
                    final int i2;
                    if (position == SettingsActivity.this.contactsSortRow) {
                        if (SettingsActivity.this.getParentActivity() != null) {
                            builder = new Builder(SettingsActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("SortBy", R.string.SortBy));
                            i2 = position;
                            builder.setItems(new CharSequence[]{LocaleController.getString("Default", R.string.Default), LocaleController.getString("SortFirstName", R.string.SortFirstName), LocaleController.getString("SortLastName", R.string.SortLastName)}, new OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Editor editor = MessagesController.getGlobalMainSettings().edit();
                                    editor.putInt("sortContactsBy", which);
                                    editor.commit();
                                    if (SettingsActivity.this.listAdapter != null) {
                                        SettingsActivity.this.listAdapter.notifyItemChanged(i2);
                                    }
                                }
                            });
                            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                            SettingsActivity.this.showDialog(builder.create());
                        }
                    } else if (position == SettingsActivity.this.usernameRow) {
                        SettingsActivity.this.presentFragment(new ChangeUsernameActivity());
                    } else if (position == SettingsActivity.this.bioRow) {
                        if (MessagesController.getInstance(SettingsActivity.this.currentAccount).getUserFull(UserConfig.getInstance(SettingsActivity.this.currentAccount).getClientUserId()) != null) {
                            SettingsActivity.this.presentFragment(new ChangeBioActivity());
                        }
                    } else if (position == SettingsActivity.this.numberRow) {
                        SettingsActivity.this.presentFragment(new ChangePhoneHelpActivity());
                    } else if (position == SettingsActivity.this.stickersRow) {
                        SettingsActivity.this.presentFragment(new StickersActivity(0));
                    } else if (position == SettingsActivity.this.emojiRow) {
                        if (SettingsActivity.this.getParentActivity() != null) {
                            final boolean[] maskValues = new boolean[2];
                            BottomSheet.Builder builder2 = new BottomSheet.Builder(SettingsActivity.this.getParentActivity());
                            builder2.setApplyTopPadding(false);
                            builder2.setApplyBottomPadding(false);
                            LinearLayout linearLayout = new LinearLayout(SettingsActivity.this.getParentActivity());
                            linearLayout.setOrientation(1);
                            a = 0;
                            while (true) {
                                if (a < (VERSION.SDK_INT >= 19 ? 2 : 1)) {
                                    String name = null;
                                    if (a == 0) {
                                        maskValues[a] = SharedConfig.allowBigEmoji;
                                        name = LocaleController.getString("EmojiBigSize", R.string.EmojiBigSize);
                                    } else if (a == 1) {
                                        maskValues[a] = SharedConfig.useSystemEmoji;
                                        name = LocaleController.getString("EmojiUseDefault", R.string.EmojiUseDefault);
                                    }
                                    CheckBoxCell checkBoxCell = new CheckBoxCell(SettingsActivity.this.getParentActivity(), 1);
                                    checkBoxCell.setTag(Integer.valueOf(a));
                                    checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                                    linearLayout.addView(checkBoxCell, LayoutHelper.createLinear(-1, 48));
                                    checkBoxCell.setText(name, TtmlNode.ANONYMOUS_REGION_ID, maskValues[a], true);
                                    checkBoxCell.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                                    checkBoxCell.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {
                                            CheckBoxCell cell = (CheckBoxCell) v;
                                            int num = ((Integer) cell.getTag()).intValue();
                                            maskValues[num] = !maskValues[num];
                                            cell.setChecked(maskValues[num], true);
                                        }
                                    });
                                    a++;
                                } else {
                                    BottomSheetCell cell = new BottomSheetCell(SettingsActivity.this.getParentActivity(), 1);
                                    cell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                                    cell.setTextAndIcon(LocaleController.getString("Save", R.string.Save).toUpperCase(), 0);
                                    cell.setTextColor(Theme.getColor(Theme.key_dialogTextBlue2));
                                    i2 = position;
                                    cell.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {
                                            try {
                                                if (SettingsActivity.this.visibleDialog != null) {
                                                    SettingsActivity.this.visibleDialog.dismiss();
                                                }
                                            } catch (Throwable e) {
                                                FileLog.e(e);
                                            }
                                            Editor editor = MessagesController.getGlobalMainSettings().edit();
                                            boolean z = maskValues[0];
                                            SharedConfig.allowBigEmoji = z;
                                            editor.putBoolean("allowBigEmoji", z);
                                            z = maskValues[1];
                                            SharedConfig.useSystemEmoji = z;
                                            editor.putBoolean("useSystemEmoji", z);
                                            editor.commit();
                                            if (SettingsActivity.this.listAdapter != null) {
                                                SettingsActivity.this.listAdapter.notifyItemChanged(i2);
                                            }
                                        }
                                    });
                                    linearLayout.addView(cell, LayoutHelper.createLinear(-1, 48));
                                    builder2.setCustomView(linearLayout);
                                    SettingsActivity.this.showDialog(builder2.create());
                                    return;
                                }
                            }
                        }
                    } else if (position == SettingsActivity.this.dumpCallStatsRow) {
                        preferences = MessagesController.getGlobalMainSettings();
                        boolean dump = preferences.getBoolean("dbg_dump_call_stats", false);
                        editor = preferences.edit();
                        editor.putBoolean("dbg_dump_call_stats", !dump);
                        editor.commit();
                        if (view instanceof TextCheckCell) {
                            ((TextCheckCell) view).setChecked(!dump);
                        }
                    }
                }
            }
        });
        this.listView.setOnItemLongClickListener(new OnItemLongClickListener() {
            private int pressCount = 0;

            public boolean onItemClick(View view, int position) {
                if (position != SettingsActivity.this.versionRow) {
                    return false;
                }
                this.pressCount++;
                if (this.pressCount >= 2 || BuildVars.DEBUG_PRIVATE_VERSION) {
                    String string;
                    Builder builder = new Builder(SettingsActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("DebugMenu", R.string.DebugMenu));
                    CharSequence[] items = new CharSequence[7];
                    items[0] = LocaleController.getString("DebugMenuImportContacts", R.string.DebugMenuImportContacts);
                    items[1] = LocaleController.getString("DebugMenuReloadContacts", R.string.DebugMenuReloadContacts);
                    items[2] = LocaleController.getString("DebugMenuResetContacts", R.string.DebugMenuResetContacts);
                    items[3] = LocaleController.getString("DebugMenuResetDialogs", R.string.DebugMenuResetDialogs);
                    items[4] = BuildVars.LOGS_ENABLED ? LocaleController.getString("DebugMenuDisableLogs", R.string.DebugMenuDisableLogs) : LocaleController.getString("DebugMenuEnableLogs", R.string.DebugMenuEnableLogs);
                    if (SharedConfig.inappCamera) {
                        string = LocaleController.getString("DebugMenuDisableCamera", R.string.DebugMenuDisableCamera);
                    } else {
                        string = LocaleController.getString("DebugMenuEnableCamera", R.string.DebugMenuEnableCamera);
                    }
                    items[5] = string;
                    items[6] = LocaleController.getString("DebugMenuClearMediaCache", R.string.DebugMenuClearMediaCache);
                    builder.setItems(items, new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            boolean z = true;
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
                                if (BuildVars.LOGS_ENABLED) {
                                    z = false;
                                }
                                BuildVars.LOGS_ENABLED = z;
                                ApplicationLoader.applicationContext.getSharedPreferences("systemConfig", 0).edit().putBoolean("logsEnabled", BuildVars.LOGS_ENABLED).commit();
                            } else if (which == 5) {
                                SharedConfig.toggleInappCamera();
                            } else if (which == 6) {
                                MessagesStorage.getInstance(SettingsActivity.this.currentAccount).clearSentMedia();
                            } else if (which == 7) {
                                SharedConfig.toggleRoundCamera16to9();
                            }
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    SettingsActivity.this.showDialog(builder.create());
                } else {
                    try {
                        Toast.makeText(SettingsActivity.this.getParentActivity(), "¯\\_(ツ)_/¯", 0).show();
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                }
                return true;
            }
        });
        frameLayout.addView(this.actionBar);
        this.extraHeightView = new View(context);
        this.extraHeightView.setPivotY(0.0f);
        this.extraHeightView.setBackgroundColor(Theme.getColor(Theme.key_avatar_backgroundActionBarBlue));
        frameLayout.addView(this.extraHeightView, LayoutHelper.createFrame(-1, 88.0f));
        this.shadowView = new View(context);
        this.shadowView.setBackgroundResource(R.drawable.header_shadow);
        frameLayout.addView(this.shadowView, LayoutHelper.createFrame(-1, 3.0f));
        this.avatarImage = new BackupImageView(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(21.0f));
        this.avatarImage.setPivotX(0.0f);
        this.avatarImage.setPivotY(0.0f);
        frameLayout.addView(this.avatarImage, LayoutHelper.createFrame(42, 42.0f, 51, 64.0f, 0.0f, 0.0f, 0.0f));
        this.avatarImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                User user = MessagesController.getInstance(SettingsActivity.this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(SettingsActivity.this.currentAccount).getClientUserId()));
                if (user != null && user.photo != null && user.photo.photo_big != null) {
                    PhotoViewer.getInstance().setParentActivity(SettingsActivity.this.getParentActivity());
                    PhotoViewer.getInstance().openPhoto(user.photo.photo_big, SettingsActivity.this.provider);
                }
            }
        });
        this.nameTextView = new TextView(context);
        this.nameTextView.setTextColor(Theme.getColor(Theme.key_profile_title));
        this.nameTextView.setTextSize(1, 18.0f);
        this.nameTextView.setLines(1);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setEllipsize(TruncateAt.END);
        this.nameTextView.setGravity(3);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setPivotX(0.0f);
        this.nameTextView.setPivotY(0.0f);
        frameLayout.addView(this.nameTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, 48.0f, 0.0f));
        this.onlineTextView = new TextView(context);
        this.onlineTextView.setTextColor(Theme.getColor(Theme.key_avatar_subtitleInProfileBlue));
        this.onlineTextView.setTextSize(1, 14.0f);
        this.onlineTextView.setLines(1);
        this.onlineTextView.setMaxLines(1);
        this.onlineTextView.setSingleLine(true);
        this.onlineTextView.setEllipsize(TruncateAt.END);
        this.onlineTextView.setGravity(3);
        frameLayout.addView(this.onlineTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, 48.0f, 0.0f));
        this.writeButton = new ImageView(context);
        Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_profile_actionBackground), Theme.getColor(Theme.key_profile_actionPressedBackground));
        if (VERSION.SDK_INT < 21) {
            Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow_profile).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.ACTION_BAR_VIDEO_EDIT_COLOR, Mode.MULTIPLY));
            Drawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            drawable = combinedDrawable;
        }
        this.writeButton.setBackgroundDrawable(drawable);
        this.writeButton.setImageResource(R.drawable.floating_camera);
        this.writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_profile_actionIcon), Mode.MULTIPLY));
        this.writeButton.setScaleType(ScaleType.CENTER);
        if (VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.writeButton, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            animator.addState(new int[0], ObjectAnimator.ofFloat(this.writeButton, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            this.writeButton.setStateListAnimator(animator);
            this.writeButton.setOutlineProvider(new ViewOutlineProvider() {
                @SuppressLint({"NewApi"})
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        frameLayout.addView(this.writeButton, LayoutHelper.createFrame(VERSION.SDK_INT >= 21 ? 56 : 60, VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, 53, 0.0f, 0.0f, 16.0f, 0.0f));
        this.writeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (SettingsActivity.this.getParentActivity() != null) {
                    Builder builder = new Builder(SettingsActivity.this.getParentActivity());
                    User user = MessagesController.getInstance(SettingsActivity.this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(SettingsActivity.this.currentAccount).getClientUserId()));
                    if (user == null) {
                        user = UserConfig.getInstance(SettingsActivity.this.currentAccount).getCurrentUser();
                    }
                    if (user != null) {
                        CharSequence[] items;
                        boolean fullMenu = false;
                        if (user.photo == null || user.photo.photo_big == null || (user.photo instanceof TL_userProfilePhotoEmpty)) {
                            items = new CharSequence[]{LocaleController.getString("FromCamera", R.string.FromCamera), LocaleController.getString("FromGalley", R.string.FromGalley)};
                        } else {
                            items = new CharSequence[]{LocaleController.getString("FromCamera", R.string.FromCamera), LocaleController.getString("FromGalley", R.string.FromGalley), LocaleController.getString("DeletePhoto", R.string.DeletePhoto)};
                            fullMenu = true;
                        }
                        boolean full = fullMenu;
                        builder.setItems(items, new OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0) {
                                    SettingsActivity.this.avatarUpdater.openCamera();
                                } else if (i == 1) {
                                    SettingsActivity.this.avatarUpdater.openGallery();
                                } else if (i == 2) {
                                    MessagesController.getInstance(SettingsActivity.this.currentAccount).deleteUserPhoto(null);
                                }
                            }
                        });
                        SettingsActivity.this.showDialog(builder.create());
                    }
                }
            }
        });
        needLayout();
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int i = 0;
                if (SettingsActivity.this.layoutManager.getItemCount() != 0) {
                    int height = 0;
                    View child = recyclerView.getChildAt(0);
                    if (child != null) {
                        if (SettingsActivity.this.layoutManager.findFirstVisibleItemPosition() == 0) {
                            int dp = AndroidUtilities.dp(88.0f);
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
        });
        return this.fragmentView;
    }

    private void performAskAQuestion() {
        final SharedPreferences preferences = MessagesController.getMainSettings(this.currentAccount);
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
                            if (supportUser != null && supportUser.id == 333000) {
                                supportUser = null;
                            }
                            data.cleanup();
                        }
                    } catch (Throwable e) {
                        FileLog.e(e);
                        supportUser = null;
                    }
                }
            }
        }
        if (supportUser == null) {
            final AlertDialog progressDialog = new AlertDialog(getParentActivity(), 1);
            progressDialog.setMessage(LocaleController.getString("Loading", R.string.Loading));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_help_getSupport(), new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    if (error == null) {
                        final TL_help_support res = (TL_help_support) response;
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                Editor editor = preferences.edit();
                                editor.putInt("support_id", res.user.id);
                                SerializedData data = new SerializedData();
                                res.user.serializeToStream(data);
                                editor.putString("support_user", Base64.encodeToString(data.toByteArray(), 0));
                                editor.commit();
                                data.cleanup();
                                try {
                                    progressDialog.dismiss();
                                } catch (Throwable e) {
                                    FileLog.e(e);
                                }
                                ArrayList<User> users = new ArrayList();
                                users.add(res.user);
                                MessagesStorage.getInstance(SettingsActivity.this.currentAccount).putUsersAndChats(users, null, true, true);
                                MessagesController.getInstance(SettingsActivity.this.currentAccount).putUser(res.user, false);
                                Bundle args = new Bundle();
                                args.putInt("user_id", res.user.id);
                                SettingsActivity.this.presentFragment(new ChatActivity(args));
                            }
                        });
                        return;
                    }
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            try {
                                progressDialog.dismiss();
                            } catch (Throwable e) {
                                FileLog.e(e);
                            }
                        }
                    });
                }
            });
            return;
        }
        MessagesController.getInstance(this.currentAccount).putUser(supportUser, true);
        Bundle args = new Bundle();
        args.putInt("user_id", supportUser.id);
        presentFragment(new ChatActivity(args));
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        this.avatarUpdater.onActivityResult(requestCode, resultCode, data);
    }

    public void saveSelfArgs(Bundle args) {
        if (this.avatarUpdater != null && this.avatarUpdater.currentPicturePath != null) {
            args.putString("path", this.avatarUpdater.currentPicturePath);
        }
    }

    public void restoreSelfArgs(Bundle args) {
        if (this.avatarUpdater != null) {
            this.avatarUpdater.currentPicturePath = args.getString("path");
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.updateInterfaces) {
            int mask = ((Integer) args[0]).intValue();
            if ((mask & 2) != 0 || (mask & 1) != 0) {
                updateUserData();
            }
        } else if (id == NotificationCenter.featuredStickersDidLoaded) {
            if (this.listAdapter != null) {
                this.listAdapter.notifyItemChanged(this.stickersRow);
            }
        } else if (id == NotificationCenter.userInfoDidLoaded) {
            if (args[0].intValue() == UserConfig.getInstance(this.currentAccount).getClientUserId() && this.listAdapter != null) {
                this.listAdapter.notifyItemChanged(this.bioRow);
            }
        } else if (id == NotificationCenter.emojiDidLoaded && this.listView != null) {
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
        int newTop = (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
        if (this.listView != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.listView.getLayoutParams();
            if (layoutParams.topMargin != newTop) {
                layoutParams.topMargin = newTop;
                this.listView.setLayoutParams(layoutParams);
                this.extraHeightView.setTranslationY((float) newTop);
            }
        }
        if (this.avatarImage != null) {
            float diff = ((float) this.extraHeight) / ((float) AndroidUtilities.dp(88.0f));
            this.extraHeightView.setScaleY(diff);
            this.shadowView.setTranslationY((float) (this.extraHeight + newTop));
            this.writeButton.setTranslationY((float) ((((this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight()) + this.extraHeight) - AndroidUtilities.dp(29.5f)));
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
            this.avatarImage.setScaleX((42.0f + (18.0f * diff)) / 42.0f);
            this.avatarImage.setScaleY((42.0f + (18.0f * diff)) / 42.0f);
            float avatarY = ((((float) (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0)) + ((((float) ActionBar.getCurrentActionBarHeight()) / 2.0f) * (1.0f + diff))) - (21.0f * AndroidUtilities.density)) + ((27.0f * AndroidUtilities.density) * diff);
            this.avatarImage.setTranslationX(((float) (-AndroidUtilities.dp(47.0f))) * diff);
            this.avatarImage.setTranslationY((float) Math.ceil((double) avatarY));
            this.nameTextView.setTranslationX((-21.0f * AndroidUtilities.density) * diff);
            this.nameTextView.setTranslationY((((float) Math.floor((double) avatarY)) - ((float) Math.ceil((double) AndroidUtilities.density))) + ((float) Math.floor((double) ((7.0f * AndroidUtilities.density) * diff))));
            this.onlineTextView.setTranslationX((-21.0f * AndroidUtilities.density) * diff);
            this.onlineTextView.setTranslationY((((float) Math.floor((double) avatarY)) + ((float) AndroidUtilities.dp(22.0f))) + (((float) Math.floor((double) (11.0f * AndroidUtilities.density))) * diff));
            this.nameTextView.setScaleX(1.0f + (0.12f * diff));
            this.nameTextView.setScaleY(1.0f + (0.12f * diff));
        }
    }

    private void fixLayout() {
        if (this.fragmentView != null) {
            this.fragmentView.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    if (SettingsActivity.this.fragmentView != null) {
                        SettingsActivity.this.needLayout();
                        SettingsActivity.this.fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    return true;
                }
            });
        }
    }

    private void updateUserData() {
        boolean z = true;
        User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
        TLObject photo = null;
        FileLocation photoBig = null;
        if (user.photo != null) {
            photo = user.photo.photo_small;
            photoBig = user.photo.photo_big;
        }
        this.avatarDrawable = new AvatarDrawable(user, true);
        this.avatarDrawable.setColor(Theme.getColor(Theme.key_avatar_backgroundInProfileBlue));
        if (this.avatarImage != null) {
            this.avatarImage.setImage(photo, "50_50", this.avatarDrawable);
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
        try {
            ArrayList<Uri> uris = new ArrayList();
            for (File file : new File(ApplicationLoader.applicationContext.getExternalFilesDir(null).getAbsolutePath() + "/logs").listFiles()) {
                if (VERSION.SDK_INT >= 24) {
                    uris.add(FileProvider.getUriForFile(getParentActivity(), "org.telegram.messenger.beta.provider", file));
                } else {
                    uris.add(Uri.fromFile(file));
                }
            }
            if (!uris.isEmpty()) {
                Intent i = new Intent("android.intent.action.SEND_MULTIPLE");
                if (VERSION.SDK_INT >= 24) {
                    i.addFlags(1);
                }
                i.setType("message/rfc822");
                i.putExtra("android.intent.extra.EMAIL", TtmlNode.ANONYMOUS_REGION_ID);
                i.putExtra("android.intent.extra.SUBJECT", "last logs");
                i.putParcelableArrayListExtra("android.intent.extra.STREAM", uris);
                getParentActivity().startActivityForResult(Intent.createChooser(i, "Select email application."), 500);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[32];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{EmptyCell.class, TextSettingsCell.class, TextCheckCell.class, HeaderCell.class, TextInfoCell.class, TextDetailSettingsCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_avatar_backgroundActionBarBlue);
        themeDescriptionArr[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_avatar_backgroundActionBarBlue);
        themeDescriptionArr[4] = new ThemeDescription(this.extraHeightView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_avatar_backgroundActionBarBlue);
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_avatar_actionBarIconBlue);
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[7] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_avatar_actionBarSelectorBlue);
        themeDescriptionArr[8] = new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_profile_title);
        themeDescriptionArr[9] = new ThemeDescription(this.onlineTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_avatar_subtitleInProfileBlue);
        themeDescriptionArr[10] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, Theme.key_actionBarDefaultSubmenuBackground);
        themeDescriptionArr[11] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, Theme.key_actionBarDefaultSubmenuItem);
        themeDescriptionArr[12] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        themeDescriptionArr[13] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        themeDescriptionArr[14] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[15] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[16] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText);
        themeDescriptionArr[17] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[18] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        themeDescriptionArr[19] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchThumb);
        themeDescriptionArr[20] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack);
        themeDescriptionArr[21] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchThumbChecked);
        themeDescriptionArr[22] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked);
        themeDescriptionArr[23] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader);
        themeDescriptionArr[24] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[25] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        themeDescriptionArr[26] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText5);
        themeDescriptionArr[27] = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, Theme.key_avatar_text);
        themeDescriptionArr[28] = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[]{this.avatarDrawable}, null, Theme.key_avatar_backgroundInProfileBlue);
        themeDescriptionArr[29] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_profile_actionIcon);
        themeDescriptionArr[30] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_profile_actionBackground);
        themeDescriptionArr[31] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_profile_actionPressedBackground);
        return themeDescriptionArr;
    }
}
