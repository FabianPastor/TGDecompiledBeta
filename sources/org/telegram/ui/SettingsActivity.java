package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.support.v4.content.FileProvider;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.DispatchQueue;
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
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.PageBlock;
import org.telegram.tgnet.TLRPC.PageListItem;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_getWebPage;
import org.telegram.tgnet.TLRPC.TL_pageBlockAnchor;
import org.telegram.tgnet.TLRPC.TL_pageBlockList;
import org.telegram.tgnet.TLRPC.TL_pageBlockParagraph;
import org.telegram.tgnet.TLRPC.TL_pageListItemText;
import org.telegram.tgnet.TLRPC.TL_photos_photo;
import org.telegram.tgnet.TLRPC.TL_photos_uploadProfilePhoto;
import org.telegram.tgnet.TLRPC.TL_userFull;
import org.telegram.tgnet.TLRPC.TL_userProfilePhoto;
import org.telegram.tgnet.TLRPC.TL_userProfilePhotoEmpty;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.SettingsSearchCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextDetailCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.ImageUpdater;
import org.telegram.ui.Components.ImageUpdater$ImageUpdaterDelegate$$CC;
import org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PlaceProviderObject;

public class SettingsActivity extends BaseFragment implements NotificationCenterDelegate, ImageUpdaterDelegate {
    private static final int edit_name = 1;
    private static final int logout = 2;
    private static final int search_button = 3;
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
    private EmptyTextProgressView emptyView;
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
    private ActionBarMenuItem otherItem;
    private int overscrollRow;
    private int privacyRow;
    private PhotoViewerProvider provider = new EmptyPhotoViewerProvider() {
        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int index, boolean needPreview) {
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
    };
    private int rowCount;
    private SearchAdapter searchAdapter;
    private int settingsSectionRow;
    private int settingsSectionRow2;
    private View shadowView;
    private TL_userFull userInfo;
    private int usernameRow;
    private int versionRow;
    private ImageView writeButton;
    private AnimatorSet writeButtonAnimation;

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
                        ((EmptyCell) holder.itemView).setHeight(AndroidUtilities.dp(88.0f));
                        return;
                    }
                    return;
                case 2:
                    TextCell textCell = holder.itemView;
                    if (position == SettingsActivity.this.languageRow) {
                        textCell.setTextAndIcon(LocaleController.getString("Language", NUM), NUM, true);
                        return;
                    } else if (position == SettingsActivity.this.notificationRow) {
                        textCell.setTextAndIcon(LocaleController.getString("NotificationsAndSounds", NUM), NUM, true);
                        return;
                    } else if (position == SettingsActivity.this.privacyRow) {
                        textCell.setTextAndIcon(LocaleController.getString("PrivacySettings", NUM), NUM, true);
                        return;
                    } else if (position == SettingsActivity.this.dataRow) {
                        textCell.setTextAndIcon(LocaleController.getString("DataSettings", NUM), NUM, true);
                        return;
                    } else if (position == SettingsActivity.this.chatRow) {
                        textCell.setTextAndIcon(LocaleController.getString("ChatSettings", NUM), NUM, true);
                        return;
                    } else if (position == SettingsActivity.this.helpRow) {
                        textCell.setTextAndIcon(LocaleController.getString("SettingsHelp", NUM), NUM, false);
                        return;
                    } else {
                        return;
                    }
                case 4:
                    if (position == SettingsActivity.this.settingsSectionRow2) {
                        ((HeaderCell) holder.itemView).setText(LocaleController.getString("SETTINGS", NUM));
                        return;
                    } else if (position == SettingsActivity.this.numberSectionRow) {
                        ((HeaderCell) holder.itemView).setText(LocaleController.getString("Account", NUM));
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
                            value = LocaleController.getString("NumberUnknown", NUM);
                        } else {
                            value = PhoneFormat.getInstance().format("+" + user.phone);
                        }
                        textCell2.setTextAndValue(value, LocaleController.getString("TapToChangePhone", NUM), true);
                        return;
                    } else if (position == SettingsActivity.this.usernameRow) {
                        user = UserConfig.getInstance(SettingsActivity.this.currentAccount).getCurrentUser();
                        if (user == null || TextUtils.isEmpty(user.username)) {
                            value = LocaleController.getString("UsernameEmpty", NUM);
                        } else {
                            value = "@" + user.username;
                        }
                        textCell2.setTextAndValue(value, LocaleController.getString("Username", NUM), true);
                        return;
                    } else if (position != SettingsActivity.this.bioRow) {
                        return;
                    } else {
                        if (SettingsActivity.this.userInfo == null || !TextUtils.isEmpty(SettingsActivity.this.userInfo.about)) {
                            textCell2.setTextWithEmojiAndValue(SettingsActivity.this.userInfo == null ? LocaleController.getString("Loading", NUM) : SettingsActivity.this.userInfo.about, LocaleController.getString("UserBio", NUM), false);
                            return;
                        } else {
                            textCell2.setTextAndValue(LocaleController.getString("UserBio", NUM), LocaleController.getString("UserBioDetail", NUM), false);
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
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 1:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 2:
                    view = new TextCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 4:
                    view = new HeaderCell(this.mContext, 23);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 5:
                    View cell = new TextInfoPrivacyCell(this.mContext, 10);
                    cell.getTextView().setGravity(1);
                    cell.getTextView().setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
                    cell.getTextView().setMovementMethod(null);
                    cell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    try {
                        PackageInfo pInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                        int code = pInfo.versionCode / 10;
                        String abi = "";
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
                        cell.setText(LocaleController.formatString("TelegramVersion", NUM, objArr));
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    cell.getTextView().setPadding(0, AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(14.0f));
                    view = cell;
                    break;
                case 6:
                    view = new TextDetailCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
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

    private class SearchAdapter extends SelectionAdapter {
        private ArrayList<FaqSearchResult> faqSearchArray = new ArrayList();
        private ArrayList<FaqSearchResult> faqSearchResults = new ArrayList();
        private WebPage faqWebPage;
        private String lastSearchString;
        private boolean loadingFaqPage;
        private Context mContext;
        private ArrayList<Object> recentSearches = new ArrayList();
        private ArrayList<CharSequence> resultNames = new ArrayList();
        private SearchResult[] searchArray;
        private ArrayList<SearchResult> searchResults = new ArrayList();
        private Runnable searchRunnable;
        private boolean searchWas;

        private class FaqSearchResult {
            private int num;
            private String[] path;
            private String title;
            private String url;

            public FaqSearchResult(String t, String[] p, String u) {
                this.title = t;
                this.path = p;
                this.url = u;
            }

            public boolean equals(Object obj) {
                if (!(obj instanceof FaqSearchResult)) {
                    return false;
                }
                return this.title.equals(((FaqSearchResult) obj).title);
            }

            public String toString() {
                int i = 0;
                SerializedData data = new SerializedData();
                data.writeInt32(this.num);
                data.writeInt32(0);
                data.writeString(this.title);
                if (this.path != null) {
                    i = this.path.length;
                }
                data.writeInt32(i);
                if (this.path != null) {
                    for (String writeString : this.path) {
                        data.writeString(writeString);
                    }
                }
                data.writeString(this.url);
                return Utilities.bytesToHex(data.toByteArray());
            }
        }

        private class SearchResult {
            private int guid;
            private int iconResId;
            private int num;
            private Runnable openRunnable;
            private String[] path;
            private String rowName;
            private String searchTitle;

            public SearchResult(SearchAdapter searchAdapter, int g, String search, int icon, Runnable open) {
                this(g, search, null, null, null, icon, open);
            }

            public SearchResult(SearchAdapter searchAdapter, int g, String search, String pathArg1, int icon, Runnable open) {
                this(g, search, null, pathArg1, null, icon, open);
            }

            public SearchResult(SearchAdapter searchAdapter, int g, String search, String row, String pathArg1, int icon, Runnable open) {
                this(g, search, row, pathArg1, null, icon, open);
            }

            public SearchResult(int g, String search, String row, String pathArg1, String pathArg2, int icon, Runnable open) {
                this.guid = g;
                this.searchTitle = search;
                this.rowName = row;
                this.openRunnable = open;
                this.iconResId = icon;
                if (pathArg1 != null && pathArg2 != null) {
                    this.path = new String[]{pathArg1, pathArg2};
                } else if (pathArg1 != null) {
                    this.path = new String[]{pathArg1};
                }
            }

            public boolean equals(Object obj) {
                if (!(obj instanceof SearchResult)) {
                    return false;
                }
                if (this.guid == ((SearchResult) obj).guid) {
                    return true;
                }
                return false;
            }

            public String toString() {
                SerializedData data = new SerializedData();
                data.writeInt32(this.num);
                data.writeInt32(1);
                data.writeInt32(this.guid);
                return Utilities.bytesToHex(data.toByteArray());
            }

            private void open() {
                this.openRunnable.run();
                if (this.rowName != null) {
                    BaseFragment openingFragment = (BaseFragment) SettingsActivity.this.parentLayout.fragmentsStack.get(SettingsActivity.this.parentLayout.fragmentsStack.size() - 1);
                    try {
                        Field listViewField = openingFragment.getClass().getDeclaredField("listView");
                        listViewField.setAccessible(true);
                        ((RecyclerListView) listViewField.get(openingFragment)).highlightRow(new SettingsActivity$SearchAdapter$SearchResult$$Lambda$0(this, openingFragment));
                        listViewField.setAccessible(false);
                    } catch (Throwable th) {
                    }
                }
            }

            /* Access modifiers changed, original: final|synthetic */
            public final /* synthetic */ int lambda$open$0$SettingsActivity$SearchAdapter$SearchResult(BaseFragment openingFragment) {
                int position = -1;
                try {
                    Field rowField = openingFragment.getClass().getDeclaredField(this.rowName);
                    Field linearLayoutField = openingFragment.getClass().getDeclaredField("layoutManager");
                    rowField.setAccessible(true);
                    linearLayoutField.setAccessible(true);
                    LinearLayoutManager layoutManager = (LinearLayoutManager) linearLayoutField.get(openingFragment);
                    position = rowField.getInt(openingFragment);
                    layoutManager.scrollToPositionWithOffset(position, 0);
                    rowField.setAccessible(false);
                    linearLayoutField.setAccessible(false);
                    return position;
                } catch (Throwable th) {
                    int i = position;
                    return position;
                }
            }
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$0$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new ChangeNameActivity());
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$1$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new ChangePhoneHelpActivity());
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$2$SettingsActivity$SearchAdapter() {
            int freeAccount = -1;
            for (int a = 0; a < 3; a++) {
                if (!UserConfig.getInstance(a).isClientActivated()) {
                    freeAccount = a;
                    break;
                }
            }
            if (freeAccount >= 0) {
                SettingsActivity.this.presentFragment(new LoginActivity(freeAccount));
            }
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$3$SettingsActivity$SearchAdapter() {
            if (SettingsActivity.this.userInfo != null) {
                SettingsActivity.this.presentFragment(new ChangeBioActivity());
            }
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$4$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new NotificationsSettingsActivity());
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$5$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new NotificationsCustomSettingsActivity(1, new ArrayList(), true));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$6$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new NotificationsCustomSettingsActivity(0, new ArrayList(), true));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$7$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new NotificationsCustomSettingsActivity(2, new ArrayList(), true));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$8$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new NotificationsSettingsActivity());
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$9$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new NotificationsSettingsActivity());
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$10$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new NotificationsSettingsActivity());
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$11$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new NotificationsSettingsActivity());
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$12$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new NotificationsSettingsActivity());
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$13$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new NotificationsSettingsActivity());
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$14$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new PrivacySettingsActivity());
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$15$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new BlockedUsersActivity());
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$16$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new PrivacyControlActivity(0, true));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$17$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new PrivacyControlActivity(4, true));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$18$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new PrivacyControlActivity(5, true));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$19$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new PrivacyControlActivity(3, true));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$20$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new PrivacyControlActivity(2, true));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$21$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new PrivacyControlActivity(1, true));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$22$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new PasscodeActivity(SharedConfig.passcodeHash.length() > 0 ? 2 : 0));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$23$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new TwoStepVerificationActivity(0));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$24$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new SessionsActivity(0));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$25$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new PrivacySettingsActivity());
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$26$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new PrivacySettingsActivity());
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$27$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new PrivacySettingsActivity());
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$28$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new SessionsActivity(1));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$29$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new PrivacySettingsActivity());
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$30$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new PrivacySettingsActivity());
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$31$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new PrivacySettingsActivity());
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$32$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new PrivacySettingsActivity());
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$33$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new PrivacySettingsActivity());
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$34$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new DataSettingsActivity());
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$35$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new DataSettingsActivity());
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$36$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new CacheControlActivity());
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$37$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new CacheControlActivity());
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$38$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new CacheControlActivity());
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$39$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new CacheControlActivity());
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$40$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new DataUsageActivity());
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$41$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new DataSettingsActivity());
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$42$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new DataAutoDownloadActivity(0));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$43$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new DataAutoDownloadActivity(1));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$44$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new DataAutoDownloadActivity(2));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$45$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new DataSettingsActivity());
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$46$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new DataSettingsActivity());
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$47$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new DataSettingsActivity());
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$48$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new DataSettingsActivity());
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$49$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new DataSettingsActivity());
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$50$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new DataSettingsActivity());
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$51$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new DataSettingsActivity());
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$52$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new DataSettingsActivity());
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$53$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new DataSettingsActivity());
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$54$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new ProxyListActivity());
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$55$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new ProxyListActivity());
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$56$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new ThemeActivity(0));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$57$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new ThemeActivity(0));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$58$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new WallpapersListActivity(0));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$59$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new WallpapersListActivity(1));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$60$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new WallpapersListActivity(0));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$61$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new ThemeActivity(1));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$62$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new ThemeActivity(0));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$63$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new ThemeActivity(0));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$64$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new ThemeActivity(0));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$65$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new ThemeActivity(0));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$66$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new ThemeActivity(0));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$67$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new ThemeActivity(0));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$68$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new ThemeActivity(0));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$69$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new StickersActivity(0));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$70$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new StickersActivity(0));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$71$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new FeaturedStickersActivity());
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$72$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new StickersActivity(1));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$73$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new ArchivedStickersActivity(0));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$74$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new ArchivedStickersActivity(1));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$75$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.presentFragment(new LanguageSelectActivity());
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$77$SettingsActivity$SearchAdapter() {
            SettingsActivity.this.showDialog(AlertsCreator.createSupportAlert(SettingsActivity.this));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$78$SettingsActivity$SearchAdapter() {
            Browser.openUrl(SettingsActivity.this.getParentActivity(), LocaleController.getString("TelegramFaqUrl", NUM));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$new$79$SettingsActivity$SearchAdapter() {
            Browser.openUrl(SettingsActivity.this.getParentActivity(), LocaleController.getString("PrivacyPolicyUrl", NUM));
        }

        public SearchAdapter(Context context) {
            int a;
            r26 = new SearchResult[80];
            r26[0] = new SearchResult(this, 500, LocaleController.getString("EditName", NUM), 0, new SettingsActivity$SearchAdapter$$Lambda$0(this));
            r26[1] = new SearchResult(this, 501, LocaleController.getString("ChangePhoneNumber", NUM), 0, new SettingsActivity$SearchAdapter$$Lambda$1(this));
            r26[2] = new SearchResult(this, 502, LocaleController.getString("AddAnotherAccount", NUM), 0, new SettingsActivity$SearchAdapter$$Lambda$2(this));
            r26[3] = new SearchResult(this, 503, LocaleController.getString("UserBio", NUM), 0, new SettingsActivity$SearchAdapter$$Lambda$3(this));
            r26[4] = new SearchResult(this, 1, LocaleController.getString("NotificationsAndSounds", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$4(this));
            r26[5] = new SearchResult(this, 2, LocaleController.getString("NotificationsPrivateChats", NUM), LocaleController.getString("NotificationsAndSounds", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$5(this));
            r26[6] = new SearchResult(this, 3, LocaleController.getString("NotificationsGroups", NUM), LocaleController.getString("NotificationsAndSounds", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$6(this));
            r26[7] = new SearchResult(this, 4, LocaleController.getString("NotificationsChannels", NUM), LocaleController.getString("NotificationsAndSounds", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$7(this));
            r26[8] = new SearchResult(this, 5, LocaleController.getString("VoipNotificationSettings", NUM), "callsSectionRow", LocaleController.getString("NotificationsAndSounds", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$8(this));
            r26[9] = new SearchResult(this, 6, LocaleController.getString("BadgeNumber", NUM), "badgeNumberSection", LocaleController.getString("NotificationsAndSounds", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$9(this));
            r26[10] = new SearchResult(this, 7, LocaleController.getString("InAppNotifications", NUM), "inappSectionRow", LocaleController.getString("NotificationsAndSounds", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$10(this));
            r26[11] = new SearchResult(this, 8, LocaleController.getString("ContactJoined", NUM), "contactJoinedRow", LocaleController.getString("NotificationsAndSounds", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$11(this));
            r26[12] = new SearchResult(this, 9, LocaleController.getString("PinnedMessages", NUM), "pinnedMessageRow", LocaleController.getString("NotificationsAndSounds", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$12(this));
            r26[13] = new SearchResult(this, 10, LocaleController.getString("ResetAllNotifications", NUM), "resetNotificationsRow", LocaleController.getString("NotificationsAndSounds", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$13(this));
            r26[14] = new SearchResult(this, 100, LocaleController.getString("PrivacySettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$14(this));
            r26[15] = new SearchResult(this, 101, LocaleController.getString("BlockedUsers", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$15(this));
            r26[16] = new SearchResult(this, 102, LocaleController.getString("PrivacyLastSeen", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$16(this));
            r26[17] = new SearchResult(this, 103, LocaleController.getString("PrivacyProfilePhoto", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$17(this));
            r26[18] = new SearchResult(this, 104, LocaleController.getString("PrivacyForwards", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$18(this));
            r26[19] = new SearchResult(this, 105, LocaleController.getString("PrivacyP2P", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$19(this));
            r26[20] = new SearchResult(this, 106, LocaleController.getString("Calls", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$20(this));
            r26[21] = new SearchResult(this, 107, LocaleController.getString("GroupsAndChannels", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$21(this));
            r26[22] = new SearchResult(this, 108, LocaleController.getString("Passcode", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$22(this));
            r26[23] = new SearchResult(this, 109, LocaleController.getString("TwoStepVerification", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$23(this));
            r26[24] = new SearchResult(this, 110, LocaleController.getString("SessionsTitle", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$24(this));
            r26[25] = new SearchResult(this, 111, LocaleController.getString("PrivacyDeleteCloudDrafts", NUM), "clearDraftsRow", LocaleController.getString("PrivacySettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$25(this));
            r26[26] = new SearchResult(this, 112, LocaleController.getString("DeleteAccountIfAwayFor2", NUM), "deleteAccountRow", LocaleController.getString("PrivacySettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$26(this));
            r26[27] = new SearchResult(this, 113, LocaleController.getString("PrivacyPaymentsClear", NUM), "paymentsClearRow", LocaleController.getString("PrivacySettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$27(this));
            r26[28] = new SearchResult(this, 114, LocaleController.getString("WebSessionsTitle", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$28(this));
            r26[29] = new SearchResult(this, 115, LocaleController.getString("SyncContactsDelete", NUM), "contactsDeleteRow", LocaleController.getString("PrivacySettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$29(this));
            r26[30] = new SearchResult(this, 116, LocaleController.getString("SyncContacts", NUM), "contactsSyncRow", LocaleController.getString("PrivacySettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$30(this));
            r26[31] = new SearchResult(this, 117, LocaleController.getString("SuggestContacts", NUM), "contactsSuggestRow", LocaleController.getString("PrivacySettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$31(this));
            r26[32] = new SearchResult(this, 118, LocaleController.getString("MapPreviewProvider", NUM), "secretMapRow", LocaleController.getString("PrivacySettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$32(this));
            r26[33] = new SearchResult(this, 119, LocaleController.getString("SecretWebPage", NUM), "secretWebpageRow", LocaleController.getString("PrivacySettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$33(this));
            r26[34] = new SearchResult(this, 200, LocaleController.getString("DataSettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$34(this));
            r26[35] = new SearchResult(this, 201, LocaleController.getString("DataUsage", NUM), "usageSectionRow", LocaleController.getString("DataSettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$35(this));
            r26[36] = new SearchResult(this, 202, LocaleController.getString("StorageUsage", NUM), LocaleController.getString("DataSettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$36(this));
            r26[37] = new SearchResult(203, LocaleController.getString("KeepMedia", NUM), "keepMediaRow", LocaleController.getString("DataSettings", NUM), LocaleController.getString("StorageUsage", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$37(this));
            r26[38] = new SearchResult(204, LocaleController.getString("ClearMediaCache", NUM), "cacheRow", LocaleController.getString("DataSettings", NUM), LocaleController.getString("StorageUsage", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$38(this));
            r26[39] = new SearchResult(205, LocaleController.getString("LocalDatabase", NUM), "databaseRow", LocaleController.getString("DataSettings", NUM), LocaleController.getString("StorageUsage", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$39(this));
            r26[40] = new SearchResult(this, 206, LocaleController.getString("NetworkUsage", NUM), LocaleController.getString("DataSettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$40(this));
            r26[41] = new SearchResult(this, 207, LocaleController.getString("AutomaticMediaDownload", NUM), "mediaDownloadSectionRow", LocaleController.getString("DataSettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$41(this));
            r26[42] = new SearchResult(this, 208, LocaleController.getString("WhenUsingMobileData", NUM), LocaleController.getString("DataSettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$42(this));
            r26[43] = new SearchResult(this, 209, LocaleController.getString("WhenConnectedOnWiFi", NUM), LocaleController.getString("DataSettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$43(this));
            r26[44] = new SearchResult(this, 210, LocaleController.getString("WhenRoaming", NUM), LocaleController.getString("DataSettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$44(this));
            r26[45] = new SearchResult(this, 211, LocaleController.getString("ResetAutomaticMediaDownload", NUM), "resetDownloadRow", LocaleController.getString("DataSettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$45(this));
            r26[46] = new SearchResult(this, 212, LocaleController.getString("AutoplayMedia", NUM), "autoplayHeaderRow", LocaleController.getString("DataSettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$46(this));
            r26[47] = new SearchResult(this, 213, LocaleController.getString("AutoplayGIF", NUM), "autoplayGifsRow", LocaleController.getString("DataSettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$47(this));
            r26[48] = new SearchResult(this, 214, LocaleController.getString("AutoplayVideo", NUM), "autoplayVideoRow", LocaleController.getString("DataSettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$48(this));
            r26[49] = new SearchResult(this, 215, LocaleController.getString("Streaming", NUM), "streamSectionRow", LocaleController.getString("DataSettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$49(this));
            r26[50] = new SearchResult(this, 216, LocaleController.getString("EnableStreaming", NUM), "enableStreamRow", LocaleController.getString("DataSettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$50(this));
            r26[51] = new SearchResult(this, 217, LocaleController.getString("Calls", NUM), "callsSectionRow", LocaleController.getString("DataSettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$51(this));
            r26[52] = new SearchResult(this, 218, LocaleController.getString("VoipUseLessData", NUM), "useLessDataForCallsRow", LocaleController.getString("DataSettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$52(this));
            r26[53] = new SearchResult(this, 219, LocaleController.getString("VoipQuickReplies", NUM), "quickRepliesRow", LocaleController.getString("DataSettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$53(this));
            r26[54] = new SearchResult(this, 220, LocaleController.getString("ProxySettings", NUM), LocaleController.getString("DataSettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$54(this));
            r26[55] = new SearchResult(221, LocaleController.getString("UseProxyForCalls", NUM), "callsRow", LocaleController.getString("DataSettings", NUM), LocaleController.getString("ProxySettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$55(this));
            r26[56] = new SearchResult(this, 300, LocaleController.getString("ChatSettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$56(this));
            r26[57] = new SearchResult(this, 301, LocaleController.getString("TextSizeHeader", NUM), "textSizeHeaderRow", LocaleController.getString("ChatSettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$57(this));
            r26[58] = new SearchResult(this, 302, LocaleController.getString("ChatBackground", NUM), LocaleController.getString("ChatSettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$58(this));
            r26[59] = new SearchResult(303, LocaleController.getString("SetColor", NUM), null, LocaleController.getString("ChatSettings", NUM), LocaleController.getString("ChatBackground", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$59(this));
            r26[60] = new SearchResult(304, LocaleController.getString("ResetChatBackgrounds", NUM), "resetRow", LocaleController.getString("ChatSettings", NUM), LocaleController.getString("ChatBackground", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$60(this));
            r26[61] = new SearchResult(this, 305, LocaleController.getString("AutoNightTheme", NUM), LocaleController.getString("ChatSettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$61(this));
            r26[62] = new SearchResult(this, 306, LocaleController.getString("ColorTheme", NUM), "themeHeaderRow", LocaleController.getString("ChatSettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$62(this));
            r26[63] = new SearchResult(this, 307, LocaleController.getString("ChromeCustomTabs", NUM), "customTabsRow", LocaleController.getString("ChatSettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$63(this));
            r26[64] = new SearchResult(this, 308, LocaleController.getString("DirectShare", NUM), "directShareRow", LocaleController.getString("ChatSettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$64(this));
            r26[65] = new SearchResult(this, 309, LocaleController.getString("EnableAnimations", NUM), "enableAnimationsRow", LocaleController.getString("ChatSettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$65(this));
            r26[66] = new SearchResult(this, 310, LocaleController.getString("RaiseToSpeak", NUM), "raiseToSpeakRow", LocaleController.getString("ChatSettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$66(this));
            r26[67] = new SearchResult(this, 311, LocaleController.getString("SendByEnter", NUM), "sendByEnterRow", LocaleController.getString("ChatSettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$67(this));
            r26[68] = new SearchResult(this, 312, LocaleController.getString("SaveToGallerySettings", NUM), "saveToGalleryRow", LocaleController.getString("ChatSettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$68(this));
            r26[69] = new SearchResult(this, 313, LocaleController.getString("StickersAndMasks", NUM), LocaleController.getString("ChatSettings", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$69(this));
            r26[70] = new SearchResult(314, LocaleController.getString("SuggestStickers", NUM), "suggestRow", LocaleController.getString("ChatSettings", NUM), LocaleController.getString("StickersAndMasks", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$70(this));
            r26[71] = new SearchResult(315, LocaleController.getString("FeaturedStickers", NUM), null, LocaleController.getString("ChatSettings", NUM), LocaleController.getString("StickersAndMasks", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$71(this));
            r26[72] = new SearchResult(316, LocaleController.getString("Masks", NUM), null, LocaleController.getString("ChatSettings", NUM), LocaleController.getString("StickersAndMasks", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$72(this));
            r26[73] = new SearchResult(317, LocaleController.getString("ArchivedStickers", NUM), null, LocaleController.getString("ChatSettings", NUM), LocaleController.getString("StickersAndMasks", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$73(this));
            r26[74] = new SearchResult(317, LocaleController.getString("ArchivedMasks", NUM), null, LocaleController.getString("ChatSettings", NUM), LocaleController.getString("StickersAndMasks", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$74(this));
            r26[75] = new SearchResult(this, 400, LocaleController.getString("Language", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$75(this));
            r26[76] = new SearchResult(this, 401, LocaleController.getString("SettingsHelp", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$76(SettingsActivity.this));
            r26[77] = new SearchResult(this, 402, LocaleController.getString("AskAQuestion", NUM), LocaleController.getString("SettingsHelp", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$77(this));
            r26[78] = new SearchResult(this, 403, LocaleController.getString("TelegramFAQ", NUM), LocaleController.getString("SettingsHelp", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$78(this));
            r26[79] = new SearchResult(this, 404, LocaleController.getString("PrivacyPolicy", NUM), LocaleController.getString("SettingsHelp", NUM), NUM, new SettingsActivity$SearchAdapter$$Lambda$79(this));
            this.searchArray = r26;
            this.mContext = context;
            HashMap<Integer, SearchResult> resultHashMap = new HashMap();
            for (a = 0; a < this.searchArray.length; a++) {
                resultHashMap.put(Integer.valueOf(this.searchArray[a].guid), this.searchArray[a]);
            }
            Set<String> set = MessagesController.getGlobalMainSettings().getStringSet("settingsSearchRecent2", null);
            if (set != null) {
                for (String value : set) {
                    SerializedData serializedData = new SerializedData(Utilities.hexToBytes(value));
                    int num = serializedData.readInt32(false);
                    int type = serializedData.readInt32(false);
                    if (type == 0) {
                        String title = serializedData.readString(false);
                        int count = serializedData.readInt32(false);
                        String[] path = null;
                        if (count > 0) {
                            path = new String[count];
                            for (a = 0; a < count; a++) {
                                path[a] = serializedData.readString(false);
                            }
                        }
                        FaqSearchResult faqSearchResult = new FaqSearchResult(title, path, serializedData.readString(false));
                        faqSearchResult.num = num;
                        this.recentSearches.add(faqSearchResult);
                    } else if (type == 1) {
                        SearchResult result = (SearchResult) resultHashMap.get(Integer.valueOf(serializedData.readInt32(false)));
                        if (result != null) {
                            result.num = num;
                            this.recentSearches.add(result);
                        }
                    }
                }
            }
            Collections.sort(this.recentSearches, new SettingsActivity$SearchAdapter$$Lambda$80(this));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ int lambda$new$80$SettingsActivity$SearchAdapter(Object o1, Object o2) {
            int n1 = getNum(o1);
            int n2 = getNum(o2);
            if (n1 < n2) {
                return -1;
            }
            if (n1 > n2) {
                return 1;
            }
            return 0;
        }

        private void loadFaqWebPage() {
            if (this.faqWebPage == null && !this.loadingFaqPage) {
                this.loadingFaqPage = true;
                TL_messages_getWebPage req2 = new TL_messages_getWebPage();
                req2.url = LocaleController.getString("TelegramFaqUrl", NUM);
                req2.hash = 0;
                ConnectionsManager.getInstance(SettingsActivity.this.currentAccount).sendRequest(req2, new SettingsActivity$SearchAdapter$$Lambda$81(this));
            }
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$loadFaqWebPage$81$SettingsActivity$SearchAdapter(TLObject response2, TL_error error2) {
            if (response2 instanceof WebPage) {
                WebPage page = (WebPage) response2;
                int N = page.cached_page.blocks.size();
                for (int a = 0; a < N; a++) {
                    PageBlock block = (PageBlock) page.cached_page.blocks.get(a);
                    if (block instanceof TL_pageBlockList) {
                        String paragraph = null;
                        if (a != 0) {
                            PageBlock prevBlock = (PageBlock) page.cached_page.blocks.get(a - 1);
                            if (prevBlock instanceof TL_pageBlockParagraph) {
                                paragraph = ArticleViewer.getPlainText(((TL_pageBlockParagraph) prevBlock).text).toString();
                            }
                        }
                        TL_pageBlockList list = (TL_pageBlockList) block;
                        int N2 = list.items.size();
                        for (int b = 0; b < N2; b++) {
                            PageListItem item = (PageListItem) list.items.get(b);
                            if (item instanceof TL_pageListItemText) {
                                TL_pageListItemText itemText = (TL_pageListItemText) item;
                                String url = ArticleViewer.getUrl(itemText.text);
                                String text = ArticleViewer.getPlainText(itemText.text).toString();
                                if (!(TextUtils.isEmpty(url) || TextUtils.isEmpty(text))) {
                                    this.faqSearchArray.add(new FaqSearchResult(text, paragraph != null ? new String[]{LocaleController.getString("SettingsSearchFaq", NUM), paragraph} : new String[]{LocaleController.getString("SettingsSearchFaq", NUM)}, url));
                                }
                            }
                        }
                    } else if (block instanceof TL_pageBlockAnchor) {
                        break;
                    }
                }
                this.faqWebPage = page;
            }
            this.loadingFaqPage = false;
        }

        public int getItemCount() {
            int i = 0;
            if (this.searchWas) {
                int size = this.searchResults.size();
                if (!this.faqSearchResults.isEmpty()) {
                    i = this.faqSearchResults.size() + 1;
                }
                return i + size;
            } else if (this.recentSearches.isEmpty()) {
                return 0;
            } else {
                return this.recentSearches.size() + 1;
            }
        }

        public boolean isEnabled(ViewHolder holder) {
            return holder.getItemViewType() == 0;
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            boolean z = false;
            boolean z2 = true;
            switch (holder.getItemViewType()) {
                case 0:
                    SettingsSearchCell searchCell = holder.itemView;
                    SearchResult result;
                    String[] access$4400;
                    CharSequence charSequence;
                    if (!this.searchWas) {
                        position--;
                        SearchResult object = this.recentSearches.get(position);
                        String access$4600;
                        if (object instanceof SearchResult) {
                            result = object;
                            access$4600 = result.searchTitle;
                            access$4400 = result.path;
                            if (position >= this.recentSearches.size() - 1) {
                                z2 = false;
                            }
                            searchCell.setTextAndValue(access$4600, access$4400, false, z2);
                            return;
                        } else if (object instanceof FaqSearchResult) {
                            FaqSearchResult result2 = (FaqSearchResult) object;
                            access$4600 = result2.title;
                            access$4400 = result2.path;
                            if (position < this.recentSearches.size() - 1) {
                                z = true;
                            }
                            searchCell.setTextAndValue(access$4600, access$4400, true, z);
                            return;
                        } else {
                            return;
                        }
                    } else if (position < this.searchResults.size()) {
                        int icon;
                        result = (SearchResult) this.searchResults.get(position);
                        SearchResult prevResult = position > 0 ? (SearchResult) this.searchResults.get(position - 1) : null;
                        if (prevResult == null || prevResult.iconResId != result.iconResId) {
                            icon = result.iconResId;
                        } else {
                            icon = 0;
                        }
                        charSequence = (CharSequence) this.resultNames.get(position);
                        access$4400 = result.path;
                        if (position >= this.searchResults.size() - 1) {
                            z2 = false;
                        }
                        searchCell.setTextAndValueAndIcon(charSequence, access$4400, icon, z2);
                        return;
                    } else {
                        position -= this.searchResults.size() + 1;
                        charSequence = (CharSequence) this.resultNames.get(this.searchResults.size() + position);
                        access$4400 = ((FaqSearchResult) this.faqSearchResults.get(position)).path;
                        if (position < this.searchResults.size() - 1) {
                            z = true;
                        }
                        searchCell.setTextAndValue(charSequence, access$4400, true, z);
                        return;
                    }
                case 1:
                    holder.itemView.setText(LocaleController.getString("SettingsFaqSearchTitle", NUM));
                    return;
                case 2:
                    holder.itemView.setText(LocaleController.getString("SettingsRecent", NUM));
                    return;
                default:
                    return;
            }
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new SettingsSearchCell(this.mContext);
                    break;
                case 1:
                    view = new GraySectionCell(this.mContext);
                    break;
                default:
                    view = new HeaderCell(this.mContext, 16);
                    break;
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
        }

        public int getItemViewType(int position) {
            if (this.searchWas) {
                if (position >= this.searchResults.size() && position == this.searchResults.size()) {
                    return 1;
                }
                return 0;
            } else if (position == 0) {
                return 2;
            } else {
                return 0;
            }
        }

        public void addRecent(Object object) {
            int index = this.recentSearches.indexOf(object);
            if (index >= 0) {
                this.recentSearches.remove(index);
            }
            this.recentSearches.add(0, object);
            if (!this.searchWas) {
                notifyDataSetChanged();
            }
            if (this.recentSearches.size() > 20) {
                this.recentSearches.remove(this.recentSearches.size() - 1);
            }
            LinkedHashSet<String> toSave = new LinkedHashSet();
            int N = this.recentSearches.size();
            for (int a = 0; a < N; a++) {
                Object o = this.recentSearches.get(a);
                if (o instanceof SearchResult) {
                    ((SearchResult) o).num = a;
                } else if (o instanceof FaqSearchResult) {
                    ((FaqSearchResult) o).num = a;
                }
                toSave.add(o.toString());
            }
            MessagesController.getGlobalMainSettings().edit().putStringSet("settingsSearchRecent2", toSave).commit();
        }

        public void clearRecent() {
            this.recentSearches.clear();
            MessagesController.getGlobalMainSettings().edit().remove("settingsSearchRecent2").commit();
            notifyDataSetChanged();
        }

        private int getNum(Object o) {
            if (o instanceof SearchResult) {
                return ((SearchResult) o).num;
            }
            if (o instanceof FaqSearchResult) {
                return ((FaqSearchResult) o).num;
            }
            return 0;
        }

        public void search(String text) {
            this.lastSearchString = text;
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            if (TextUtils.isEmpty(text)) {
                this.searchWas = false;
                this.searchResults.clear();
                this.faqSearchResults.clear();
                this.resultNames.clear();
                SettingsActivity.this.emptyView.setTopImage(0);
                SettingsActivity.this.emptyView.setText(LocaleController.getString("SettingsNoRecent", NUM));
                notifyDataSetChanged();
                return;
            }
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            SettingsActivity$SearchAdapter$$Lambda$82 settingsActivity$SearchAdapter$$Lambda$82 = new SettingsActivity$SearchAdapter$$Lambda$82(this, text);
            this.searchRunnable = settingsActivity$SearchAdapter$$Lambda$82;
            dispatchQueue.postRunnable(settingsActivity$SearchAdapter$$Lambda$82, 300);
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$search$83$SettingsActivity$SearchAdapter(String text) {
            int a;
            String title;
            SpannableStringBuilder stringBuilder;
            int i;
            String searchString;
            int index;
            SpannableStringBuilder spannableStringBuilder;
            ArrayList<SearchResult> results = new ArrayList();
            ArrayList<FaqSearchResult> faqResults = new ArrayList();
            ArrayList<CharSequence> names = new ArrayList();
            String[] searchArgs = text.split(" ");
            String[] translitArgs = new String[searchArgs.length];
            for (a = 0; a < searchArgs.length; a++) {
                translitArgs[a] = LocaleController.getInstance().getTranslitString(searchArgs[a]);
                if (translitArgs[a].equals(searchArgs[a])) {
                    translitArgs[a] = null;
                }
            }
            for (a = 0; a < this.searchArray.length; a++) {
                SearchResult result = this.searchArray[a];
                title = " " + result.searchTitle.toLowerCase();
                stringBuilder = null;
                i = 0;
                while (i < searchArgs.length) {
                    if (searchArgs[i].length() != 0) {
                        searchString = searchArgs[i];
                        index = title.indexOf(" " + searchString);
                        if (index < 0 && translitArgs[i] != null) {
                            searchString = translitArgs[i];
                            index = title.indexOf(" " + searchString);
                        }
                        if (index < 0) {
                            break;
                        }
                        if (stringBuilder == null) {
                            spannableStringBuilder = new SpannableStringBuilder(result.searchTitle);
                        }
                        stringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor("windowBackgroundWhiteBlueText4")), index, searchString.length() + index, 33);
                    }
                    if (stringBuilder != null && i == searchArgs.length - 1) {
                        if (result.guid == 502) {
                            int freeAccount = -1;
                            for (int b = 0; b < 3; b++) {
                                if (!UserConfig.getInstance(a).isClientActivated()) {
                                    freeAccount = b;
                                    break;
                                }
                            }
                            if (freeAccount < 0) {
                            }
                        }
                        results.add(result);
                        names.add(stringBuilder);
                    }
                    i++;
                }
            }
            if (this.faqWebPage != null) {
                int N = this.faqSearchArray.size();
                for (a = 0; a < N; a++) {
                    FaqSearchResult result2 = (FaqSearchResult) this.faqSearchArray.get(a);
                    title = " " + result2.title.toLowerCase();
                    stringBuilder = null;
                    i = 0;
                    while (i < searchArgs.length) {
                        if (searchArgs[i].length() != 0) {
                            searchString = searchArgs[i];
                            index = title.indexOf(" " + searchString);
                            if (index < 0 && translitArgs[i] != null) {
                                searchString = translitArgs[i];
                                index = title.indexOf(" " + searchString);
                            }
                            if (index < 0) {
                                break;
                            }
                            if (stringBuilder == null) {
                                spannableStringBuilder = new SpannableStringBuilder(result2.title);
                            }
                            stringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor("windowBackgroundWhiteBlueText4")), index, searchString.length() + index, 33);
                        }
                        if (stringBuilder != null && i == searchArgs.length - 1) {
                            faqResults.add(result2);
                            names.add(stringBuilder);
                        }
                        i++;
                    }
                }
            }
            AndroidUtilities.runOnUIThread(new SettingsActivity$SearchAdapter$$Lambda$83(this, text, results, faqResults, names));
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$null$82$SettingsActivity$SearchAdapter(String text, ArrayList results, ArrayList faqResults, ArrayList names) {
            if (text.equals(this.lastSearchString)) {
                if (!this.searchWas) {
                    SettingsActivity.this.emptyView.setTopImage(NUM);
                    SettingsActivity.this.emptyView.setText(LocaleController.getString("SettingsNoResults", NUM));
                }
                this.searchWas = true;
                this.searchResults = results;
                this.faqSearchResults = faqResults;
                this.resultNames = names;
                notifyDataSetChanged();
            }
        }
    }

    public String getInitialSearchString() {
        return ImageUpdater$ImageUpdaterDelegate$$CC.getInitialSearchString(this);
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
        this.actionBar.setBackgroundColor(Theme.getColor("avatar_backgroundActionBarBlue"));
        this.actionBar.setItemsBackgroundColor(Theme.getColor("avatar_actionBarSelectorBlue"), false);
        this.actionBar.setItemsColor(Theme.getColor("avatar_actionBarIconBlue"), false);
        this.actionBar.setBackButtonImage(NUM);
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
                } else if (id == 2) {
                    SettingsActivity.this.presentFragment(new LogoutActivity());
                }
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        ActionBarMenuItem searchItem = menu.addItem(3, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItemSearchListener() {
            public void onSearchExpand() {
                if (SettingsActivity.this.otherItem != null) {
                    SettingsActivity.this.otherItem.setVisibility(8);
                }
                SettingsActivity.this.searchAdapter.loadFaqWebPage();
                SettingsActivity.this.listView.setAdapter(SettingsActivity.this.searchAdapter);
                SettingsActivity.this.listView.setEmptyView(SettingsActivity.this.emptyView);
                SettingsActivity.this.avatarContainer.setVisibility(8);
                SettingsActivity.this.writeButton.setVisibility(8);
                SettingsActivity.this.nameTextView.setVisibility(8);
                SettingsActivity.this.onlineTextView.setVisibility(8);
                SettingsActivity.this.extraHeightView.setVisibility(8);
                SettingsActivity.this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                SettingsActivity.this.fragmentView.setTag("windowBackgroundWhite");
                SettingsActivity.this.needLayout();
            }

            public void onSearchCollapse() {
                if (SettingsActivity.this.otherItem != null) {
                    SettingsActivity.this.otherItem.setVisibility(0);
                }
                SettingsActivity.this.listView.setAdapter(SettingsActivity.this.listAdapter);
                SettingsActivity.this.listView.setEmptyView(null);
                SettingsActivity.this.emptyView.setVisibility(8);
                SettingsActivity.this.avatarContainer.setVisibility(0);
                SettingsActivity.this.writeButton.setVisibility(0);
                SettingsActivity.this.nameTextView.setVisibility(0);
                SettingsActivity.this.onlineTextView.setVisibility(0);
                SettingsActivity.this.extraHeightView.setVisibility(0);
                SettingsActivity.this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
                SettingsActivity.this.fragmentView.setTag("windowBackgroundGray");
                SettingsActivity.this.needLayout();
            }

            public void onTextChanged(EditText editText) {
                SettingsActivity.this.searchAdapter.search(editText.getText().toString().toLowerCase());
            }
        });
        searchItem.setContentDescription(LocaleController.getString("SearchInSettings", NUM));
        searchItem.setSearchFieldHint(LocaleController.getString("SearchInSettings", NUM));
        this.otherItem = menu.addItem(0, NUM);
        this.otherItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
        this.otherItem.addSubItem(1, LocaleController.getString("EditName", NUM));
        this.otherItem.addSubItem(2, LocaleController.getString("LogOut", NUM));
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
        this.searchAdapter = new SearchAdapter(context);
        this.fragmentView = new FrameLayout(context) {
            /* Access modifiers changed, original: protected */
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
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
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.fragmentView.setTag("windowBackgroundGray");
        FrameLayout frameLayout = this.fragmentView;
        this.listView = new RecyclerListView(context);
        this.listView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView = this.listView;
        AnonymousClass5 anonymousClass5 = new LinearLayoutManager(context, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = anonymousClass5;
        recyclerListView.setLayoutManager(anonymousClass5);
        this.listView.setGlowColor(Theme.getColor("avatar_backgroundActionBarBlue"));
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        this.listView.setOnItemClickListener(new SettingsActivity$$Lambda$0(this));
        this.listView.setOnItemLongClickListener(new OnItemLongClickListener() {
            private int pressCount = 0;

            public boolean onItemClick(View view, int position) {
                Builder builder;
                if (SettingsActivity.this.listView.getAdapter() == SettingsActivity.this.searchAdapter) {
                    builder = new Builder(SettingsActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("AppName", NUM));
                    builder.setMessage(LocaleController.getString("ClearSearch", NUM));
                    builder.setPositiveButton(LocaleController.getString("ClearButton", NUM).toUpperCase(), new SettingsActivity$6$$Lambda$0(this));
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                    SettingsActivity.this.showDialog(builder.create());
                    return true;
                } else if (position != SettingsActivity.this.versionRow) {
                    return false;
                } else {
                    this.pressCount++;
                    if (this.pressCount >= 2 || BuildVars.DEBUG_PRIVATE_VERSION) {
                        String str;
                        builder = new Builder(SettingsActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString("DebugMenu", NUM));
                        CharSequence[] items = new CharSequence[10];
                        items[0] = LocaleController.getString("DebugMenuImportContacts", NUM);
                        items[1] = LocaleController.getString("DebugMenuReloadContacts", NUM);
                        items[2] = LocaleController.getString("DebugMenuResetContacts", NUM);
                        items[3] = LocaleController.getString("DebugMenuResetDialogs", NUM);
                        items[4] = BuildVars.LOGS_ENABLED ? LocaleController.getString("DebugMenuDisableLogs", NUM) : LocaleController.getString("DebugMenuEnableLogs", NUM);
                        items[5] = SharedConfig.inappCamera ? LocaleController.getString("DebugMenuDisableCamera", NUM) : LocaleController.getString("DebugMenuEnableCamera", NUM);
                        items[6] = LocaleController.getString("DebugMenuClearMediaCache", NUM);
                        items[7] = LocaleController.getString("DebugMenuCallSettings", NUM);
                        items[8] = null;
                        if (BuildVars.DEBUG_PRIVATE_VERSION) {
                            str = "Check for app updates";
                        } else {
                            str = null;
                        }
                        items[9] = str;
                        builder.setItems(items, new SettingsActivity$6$$Lambda$1(this));
                        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                        SettingsActivity.this.showDialog(builder.create());
                    } else {
                        try {
                            Toast.makeText(SettingsActivity.this.getParentActivity(), "¯\\_(ツ)_/¯", 0).show();
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    }
                    return true;
                }
            }

            /* Access modifiers changed, original: final|synthetic */
            public final /* synthetic */ void lambda$onItemClick$0$SettingsActivity$6(DialogInterface dialogInterface, int i) {
                SettingsActivity.this.searchAdapter.clearRecent();
            }

            /* Access modifiers changed, original: final|synthetic */
            public final /* synthetic */ void lambda$onItemClick$1$SettingsActivity$6(DialogInterface dialog, int which) {
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
                    Editor editor = MessagesController.getGlobalMainSettings().edit();
                    SharedConfig.setNoSoundHintShowed(false);
                } else if (which == 7) {
                    VoIPHelper.showCallDebugSettings(SettingsActivity.this.getParentActivity());
                } else if (which == 8) {
                    SharedConfig.toggleRoundCamera16to9();
                } else if (which == 9) {
                    ((LaunchActivity) SettingsActivity.this.getParentActivity()).checkAppUpdate(true);
                }
            }
        });
        this.emptyView = new EmptyTextProgressView(context);
        this.emptyView.showTextView();
        this.emptyView.setTextSize(18);
        this.emptyView.setVisibility(8);
        this.emptyView.setShowAtCenter(true);
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        frameLayout.addView(this.actionBar);
        this.extraHeightView = new View(context);
        this.extraHeightView.setPivotY(0.0f);
        this.extraHeightView.setBackgroundColor(Theme.getColor("avatar_backgroundActionBarBlue"));
        frameLayout.addView(this.extraHeightView, LayoutHelper.createFrame(-1, 88.0f));
        this.shadowView = new View(context);
        this.shadowView.setBackgroundResource(NUM);
        frameLayout.addView(this.shadowView, LayoutHelper.createFrame(-1, 3.0f));
        this.avatarContainer = new FrameLayout(context);
        this.avatarContainer.setPivotX(LocaleController.isRTL ? (float) AndroidUtilities.dp(42.0f) : 0.0f);
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
            i2 = 112;
        } else {
            i2 = 0;
        }
        frameLayout.addView(view, LayoutHelper.createFrame(42, 42.0f, i3, f2, 0.0f, (float) i2, 0.0f));
        this.avatarContainer.setOnClickListener(new SettingsActivity$$Lambda$1(this));
        this.avatarImage = new BackupImageView(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(21.0f));
        this.avatarImage.setContentDescription(LocaleController.getString("AccDescrProfilePicture", NUM));
        this.avatarContainer.addView(this.avatarImage, LayoutHelper.createFrame(42, 42.0f));
        final Paint paint = new Paint(1);
        paint.setColor(NUM);
        this.avatarProgressView = new RadialProgressView(context) {
            /* Access modifiers changed, original: protected */
            public void onDraw(Canvas canvas) {
                if (SettingsActivity.this.avatarImage != null && SettingsActivity.this.avatarImage.getImageReceiver().hasNotThumb()) {
                    paint.setAlpha((int) (85.0f * SettingsActivity.this.avatarImage.getImageReceiver().getCurrentAlpha()));
                    canvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(21.0f), paint);
                }
                super.onDraw(canvas);
            }
        };
        this.avatarProgressView.setSize(AndroidUtilities.dp(26.0f));
        this.avatarProgressView.setProgressColor(-1);
        this.avatarContainer.addView(this.avatarProgressView, LayoutHelper.createFrame(42, 42.0f));
        showAvatarProgress(false, false);
        this.nameTextView = new TextView(context) {
            /* Access modifiers changed, original: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                setPivotX(LocaleController.isRTL ? (float) getMeasuredWidth() : 0.0f);
            }
        };
        this.nameTextView.setTextColor(Theme.getColor("profile_title"));
        this.nameTextView.setTextSize(1, 18.0f);
        this.nameTextView.setLines(1);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setEllipsize(TruncateAt.END);
        this.nameTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setPivotY(0.0f);
        frameLayout.addView(this.nameTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 48.0f : 118.0f, 0.0f, LocaleController.isRTL ? 166.0f : 96.0f, 0.0f));
        this.onlineTextView = new TextView(context);
        this.onlineTextView.setTextColor(Theme.getColor("avatar_subtitleInProfileBlue"));
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
            f = 166.0f;
        } else {
            f = 96.0f;
        }
        frameLayout.addView(view, LayoutHelper.createFrame(-2, -2.0f, i3, f2, 0.0f, f, 0.0f));
        this.writeButton = new ImageView(context);
        Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("profile_actionBackground"), Theme.getColor("profile_actionPressedBackground"));
        if (VERSION.SDK_INT < 21) {
            Drawable shadowDrawable = context.getResources().getDrawable(NUM).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(-16777216, Mode.MULTIPLY));
            Drawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            drawable = combinedDrawable;
        }
        this.writeButton.setBackgroundDrawable(drawable);
        this.writeButton.setImageResource(NUM);
        this.writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("profile_actionIcon"), Mode.MULTIPLY));
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
        frameLayout.addView(this.writeButton, LayoutHelper.createFrame(VERSION.SDK_INT >= 21 ? 56 : 60, VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, (LocaleController.isRTL ? 3 : 5) | 48, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 16.0f, 0.0f));
        this.writeButton.setOnClickListener(new SettingsActivity$$Lambda$2(this));
        this.writeButton.setContentDescription(LocaleController.getString("AccDescrChangeProfilePicture", NUM));
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
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1 && SettingsActivity.this.listView.getAdapter() == SettingsActivity.this.searchAdapter) {
                    AndroidUtilities.hideKeyboard(SettingsActivity.this.getParentActivity().getCurrentFocus());
                }
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int i = 0;
                if (SettingsActivity.this.layoutManager.getItemCount() != 0) {
                    int height = 0;
                    View child = recyclerView.getChildAt(0);
                    if (child != null && SettingsActivity.this.avatarContainer.getVisibility() == 0) {
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

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createView$0$SettingsActivity(View view, int position) {
        if (this.listView.getAdapter() == this.listAdapter) {
            if (position == this.notificationRow) {
                presentFragment(new NotificationsSettingsActivity());
            } else if (position == this.privacyRow) {
                presentFragment(new PrivacySettingsActivity());
            } else if (position == this.dataRow) {
                presentFragment(new DataSettingsActivity());
            } else if (position == this.chatRow) {
                presentFragment(new ThemeActivity(0));
            } else if (position == this.helpRow) {
                showHelpAlert();
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
        } else if (position >= 0) {
            SearchResult object = Integer.valueOf(this.numberRow);
            if (!this.searchAdapter.searchWas) {
                position--;
                if (position < 0) {
                    return;
                }
                if (position < this.searchAdapter.recentSearches.size()) {
                    object = this.searchAdapter.recentSearches.get(position);
                }
            } else if (position < this.searchAdapter.searchResults.size()) {
                object = this.searchAdapter.searchResults.get(position);
            } else {
                position -= this.searchAdapter.searchResults.size() + 1;
                if (position >= 0 && position < this.searchAdapter.faqSearchResults.size()) {
                    object = this.searchAdapter.faqSearchResults.get(position);
                }
            }
            if (object instanceof SearchResult) {
                object.open();
            } else if (object instanceof FaqSearchResult) {
                FaqSearchResult result = (FaqSearchResult) object;
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.openArticle, this.searchAdapter.faqWebPage, result.url);
            }
            if (object != null) {
                this.searchAdapter.addRecent(object);
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createView$1$SettingsActivity(View v) {
        if (this.avatar == null) {
            User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
            if (user != null && user.photo != null && user.photo.photo_big != null) {
                PhotoViewer.getInstance().setParentActivity(getParentActivity());
                PhotoViewer.getInstance().openPhoto(user.photo.photo_big, this.provider);
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createView$3$SettingsActivity(View v) {
        User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
        if (user == null) {
            user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
        }
        if (user != null) {
            ImageUpdater imageUpdater = this.imageUpdater;
            boolean z = (user.photo == null || user.photo.photo_big == null || (user.photo instanceof TL_userProfilePhotoEmpty)) ? false : true;
            imageUpdater.openMenu(z, new SettingsActivity$$Lambda$10(this));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$2$SettingsActivity() {
        MessagesController.getInstance(this.currentAccount).deleteUserPhoto(null);
    }

    public void didUploadPhoto(InputFile file, PhotoSize bigSize, PhotoSize smallSize) {
        AndroidUtilities.runOnUIThread(new SettingsActivity$$Lambda$3(this, file, smallSize, bigSize));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$didUploadPhoto$6$SettingsActivity(InputFile file, PhotoSize smallSize, PhotoSize bigSize) {
        if (file != null) {
            TL_photos_uploadProfilePhoto req = new TL_photos_uploadProfilePhoto();
            req.file = file;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new SettingsActivity$$Lambda$8(this));
            return;
        }
        this.avatar = smallSize.location;
        this.avatarBig = bigSize.location;
        this.avatarImage.setImage(this.avatar, "50_50", this.avatarDrawable, null);
        showAvatarProgress(true, false);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$5$SettingsActivity(TLObject response, TL_error error) {
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
            user.photo.photo_id = photo.photo.id;
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
            MessagesStorage.getInstance(this.currentAccount).clearUserPhotos(user.id);
            ArrayList<User> users = new ArrayList();
            users.add(user);
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(users, null, false, true);
        }
        AndroidUtilities.runOnUIThread(new SettingsActivity$$Lambda$9(this));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$4$SettingsActivity() {
        this.avatar = null;
        this.avatarBig = null;
        updateUserData();
        showAvatarProgress(false, true);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(1535));
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
        setParentActivityTitle(LocaleController.getString("Settings", NUM));
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
            layoutParams = (FrameLayout.LayoutParams) this.emptyView.getLayoutParams();
            if (layoutParams.topMargin != newTop) {
                layoutParams.topMargin = newTop;
                this.emptyView.setLayoutParams(layoutParams);
            }
        }
        if (this.avatarContainer != null) {
            int currentExtraHeight;
            if (this.avatarContainer.getVisibility() == 0) {
                currentExtraHeight = this.extraHeight;
            } else {
                currentExtraHeight = 0;
            }
            float diff = ((float) currentExtraHeight) / ((float) AndroidUtilities.dp(88.0f));
            this.extraHeightView.setScaleY(diff);
            this.shadowView.setTranslationY((float) (newTop + currentExtraHeight));
            this.writeButton.setTranslationY((float) ((((this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight()) + currentExtraHeight) - AndroidUtilities.dp(29.5f)));
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
            this.avatarProgressView.setSize(AndroidUtilities.dp(26.0f / this.avatarContainer.getScaleX()));
            this.avatarProgressView.setStrokeWidth(3.0f / this.avatarContainer.getScaleX());
            float avatarY = ((((float) (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0)) + ((((float) ActionBar.getCurrentActionBarHeight()) / 2.0f) * (1.0f + diff))) - (21.0f * AndroidUtilities.density)) + ((27.0f * AndroidUtilities.density) * diff);
            this.avatarContainer.setTranslationY((float) Math.ceil((double) avatarY));
            this.nameTextView.setTranslationY((((float) Math.floor((double) avatarY)) - ((float) Math.ceil((double) AndroidUtilities.density))) + ((float) Math.floor((double) ((7.0f * AndroidUtilities.density) * diff))));
            this.onlineTextView.setTranslationY((((float) Math.floor((double) avatarY)) + ((float) AndroidUtilities.dp(22.0f))) + (((float) Math.floor((double) (11.0f * AndroidUtilities.density))) * diff));
            this.nameTextView.setScaleX(1.0f + (0.12f * diff));
            this.nameTextView.setScaleY(1.0f + (0.12f * diff));
            if (LocaleController.isRTL) {
                this.avatarContainer.setTranslationX(((float) AndroidUtilities.dp(95.0f)) * diff);
                this.nameTextView.setTranslationX((69.0f * AndroidUtilities.density) * diff);
                this.onlineTextView.setTranslationX((69.0f * AndroidUtilities.density) * diff);
                return;
            }
            this.avatarContainer.setTranslationX(((float) (-AndroidUtilities.dp(47.0f))) * diff);
            this.nameTextView.setTranslationX((-21.0f * AndroidUtilities.density) * diff);
            this.onlineTextView.setTranslationX((-21.0f * AndroidUtilities.density) * diff);
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
        Object user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
        if (user != null) {
            TLObject photo = null;
            FileLocation photoBig = null;
            if (user.photo != null) {
                photo = user.photo.photo_small;
                photoBig = user.photo.photo_big;
            }
            this.avatarDrawable = new AvatarDrawable((User) user, true);
            this.avatarDrawable.setColor(Theme.getColor("avatar_backgroundInProfileBlue"));
            if (this.avatarImage != null) {
                this.avatarImage.setImage(photo, "50_50", this.avatarDrawable, user);
                this.avatarImage.getImageReceiver().setVisible(!PhotoViewer.isShowingImage(photoBig), false);
                this.nameTextView.setText(UserObject.getUserName(user));
                this.onlineTextView.setText(LocaleController.getString("Online", NUM));
                ImageReceiver imageReceiver = this.avatarImage.getImageReceiver();
                if (PhotoViewer.isShowingImage(photoBig)) {
                    z = false;
                }
                imageReceiver.setVisible(z, false);
            }
        }
    }

    private void showHelpAlert() {
        if (getParentActivity() != null) {
            Context context = getParentActivity();
            BottomSheet.Builder builder = new BottomSheet.Builder(context);
            builder.setApplyTopPadding(false);
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(1);
            HeaderCell headerCell = new HeaderCell(context, true, 23, 15, false);
            headerCell.setHeight(47);
            headerCell.setText(LocaleController.getString("SettingsHelp", NUM));
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
                            text = LocaleController.getString("AskAQuestion", NUM);
                            break;
                        case 1:
                            text = LocaleController.getString("TelegramFAQ", NUM);
                            break;
                        case 2:
                            text = LocaleController.getString("PrivacyPolicy", NUM);
                            break;
                        case 3:
                            text = LocaleController.getString("DebugSendLogs", NUM);
                            break;
                        case 4:
                            text = LocaleController.getString("DebugClearLogs", NUM);
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
                    textCell.setOnClickListener(new SettingsActivity$$Lambda$4(this, builder));
                }
                a++;
            }
            builder.setCustomView(linearLayout);
            showDialog(builder.create());
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$showHelpAlert$8$SettingsActivity(BottomSheet.Builder builder, View v2) {
        switch (((Integer) v2.getTag()).intValue()) {
            case 0:
                showDialog(AlertsCreator.createSupportAlert(this));
                break;
            case 1:
                Browser.openUrl(getParentActivity(), LocaleController.getString("TelegramFaqUrl", NUM));
                break;
            case 2:
                Browser.openUrl(getParentActivity(), LocaleController.getString("PrivacyPolicyUrl", NUM));
                break;
            case 3:
                sendLogs();
                break;
            case 4:
                FileLog.cleanupLogs();
                break;
            case 5:
                if (getParentActivity() != null) {
                    Builder builder1 = new Builder(getParentActivity());
                    builder1.setMessage(LocaleController.getString("AreYouSure", NUM));
                    builder1.setTitle(LocaleController.getString("AppName", NUM));
                    builder1.setPositiveButton(LocaleController.getString("OK", NUM), new SettingsActivity$$Lambda$7(this));
                    builder1.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                    showDialog(builder1.create());
                    break;
                }
                return;
        }
        builder.getDismissRunnable().run();
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$7$SettingsActivity(DialogInterface dialogInterface, int i) {
        SharedConfig.pushAuthKey = null;
        SharedConfig.pushAuthKeyId = null;
        SharedConfig.saveConfig();
        ConnectionsManager.getInstance(this.currentAccount).switchBackend();
    }

    private void sendLogs() {
        if (getParentActivity() != null) {
            AlertDialog progressDialog = new AlertDialog(getParentActivity(), 3);
            progressDialog.setCanCacnel(false);
            progressDialog.show();
            Utilities.globalQueue.postRunnable(new SettingsActivity$$Lambda$5(this, progressDialog));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x00f3 A:{Catch:{ Exception -> 0x00fc }} */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x00f8 A:{Catch:{ Exception -> 0x00fc }} */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00b9 A:{SYNTHETIC, Splitter:B:27:0x00b9} */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00be A:{Catch:{ Exception -> 0x00fc }} */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00b9 A:{SYNTHETIC, Splitter:B:27:0x00b9} */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00be A:{Catch:{ Exception -> 0x00fc }} */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x00f3 A:{Catch:{ Exception -> 0x00fc }} */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x00f8 A:{Catch:{ Exception -> 0x00fc }} */
    public final /* synthetic */ void lambda$sendLogs$10$SettingsActivity(org.telegram.ui.ActionBar.AlertDialog r23) {
        /*
        r22 = this;
        r20 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x00fc }
        r21 = 0;
        r18 = r20.getExternalFilesDir(r21);	 Catch:{ Exception -> 0x00fc }
        r7 = new java.io.File;	 Catch:{ Exception -> 0x00fc }
        r20 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00fc }
        r20.<init>();	 Catch:{ Exception -> 0x00fc }
        r21 = r18.getAbsolutePath();	 Catch:{ Exception -> 0x00fc }
        r20 = r20.append(r21);	 Catch:{ Exception -> 0x00fc }
        r21 = "/logs";
        r20 = r20.append(r21);	 Catch:{ Exception -> 0x00fc }
        r20 = r20.toString();	 Catch:{ Exception -> 0x00fc }
        r0 = r20;
        r7.<init>(r0);	 Catch:{ Exception -> 0x00fc }
        r19 = new java.io.File;	 Catch:{ Exception -> 0x00fc }
        r20 = "logs.zip";
        r0 = r19;
        r1 = r20;
        r0.<init>(r7, r1);	 Catch:{ Exception -> 0x00fc }
        r20 = r19.exists();	 Catch:{ Exception -> 0x00fc }
        if (r20 == 0) goto L_0x003c;
    L_0x0039:
        r19.delete();	 Catch:{ Exception -> 0x00fc }
    L_0x003c:
        r11 = r7.listFiles();	 Catch:{ Exception -> 0x00fc }
        r20 = 1;
        r0 = r20;
        r12 = new boolean[r0];	 Catch:{ Exception -> 0x00fc }
        r14 = 0;
        r16 = 0;
        r6 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x010a }
        r0 = r19;
        r6.<init>(r0);	 Catch:{ Exception -> 0x010a }
        r17 = new java.util.zip.ZipOutputStream;	 Catch:{ Exception -> 0x010a }
        r20 = new java.io.BufferedOutputStream;	 Catch:{ Exception -> 0x010a }
        r0 = r20;
        r0.<init>(r6);	 Catch:{ Exception -> 0x010a }
        r0 = r17;
        r1 = r20;
        r0.<init>(r1);	 Catch:{ Exception -> 0x010a }
        r20 = 65536; // 0x10000 float:9.18355E-41 double:3.2379E-319;
        r0 = r20;
        r5 = new byte[r0];	 Catch:{ Exception -> 0x00b1, all -> 0x0101 }
        r13 = 0;
        r15 = r14;
    L_0x0068:
        r0 = r11.length;	 Catch:{ Exception -> 0x010c, all -> 0x0105 }
        r20 = r0;
        r0 = r20;
        if (r13 >= r0) goto L_0x00dc;
    L_0x006f:
        r10 = new java.io.FileInputStream;	 Catch:{ Exception -> 0x010c, all -> 0x0105 }
        r20 = r11[r13];	 Catch:{ Exception -> 0x010c, all -> 0x0105 }
        r0 = r20;
        r10.<init>(r0);	 Catch:{ Exception -> 0x010c, all -> 0x0105 }
        r14 = new java.io.BufferedInputStream;	 Catch:{ Exception -> 0x010c, all -> 0x0105 }
        r0 = r5.length;	 Catch:{ Exception -> 0x010c, all -> 0x0105 }
        r20 = r0;
        r0 = r20;
        r14.<init>(r10, r0);	 Catch:{ Exception -> 0x010c, all -> 0x0105 }
        r9 = new java.util.zip.ZipEntry;	 Catch:{ Exception -> 0x00b1, all -> 0x0101 }
        r20 = r11[r13];	 Catch:{ Exception -> 0x00b1, all -> 0x0101 }
        r20 = r20.getName();	 Catch:{ Exception -> 0x00b1, all -> 0x0101 }
        r0 = r20;
        r9.<init>(r0);	 Catch:{ Exception -> 0x00b1, all -> 0x0101 }
        r0 = r17;
        r0.putNextEntry(r9);	 Catch:{ Exception -> 0x00b1, all -> 0x0101 }
    L_0x0094:
        r20 = 0;
        r0 = r5.length;	 Catch:{ Exception -> 0x00b1, all -> 0x0101 }
        r21 = r0;
        r0 = r20;
        r1 = r21;
        r4 = r14.read(r5, r0, r1);	 Catch:{ Exception -> 0x00b1, all -> 0x0101 }
        r20 = -1;
        r0 = r20;
        if (r4 == r0) goto L_0x00d2;
    L_0x00a7:
        r20 = 0;
        r0 = r17;
        r1 = r20;
        r0.write(r5, r1, r4);	 Catch:{ Exception -> 0x00b1, all -> 0x0101 }
        goto L_0x0094;
    L_0x00b1:
        r8 = move-exception;
        r16 = r17;
    L_0x00b4:
        com.google.devtools.build.android.desugar.runtime.ThrowableExtension.printStackTrace(r8);	 Catch:{ all -> 0x00f0 }
        if (r14 == 0) goto L_0x00bc;
    L_0x00b9:
        r14.close();	 Catch:{ Exception -> 0x00fc }
    L_0x00bc:
        if (r16 == 0) goto L_0x00c1;
    L_0x00be:
        r16.close();	 Catch:{ Exception -> 0x00fc }
    L_0x00c1:
        r20 = new org.telegram.ui.SettingsActivity$$Lambda$6;	 Catch:{ Exception -> 0x00fc }
        r0 = r20;
        r1 = r22;
        r2 = r23;
        r3 = r19;
        r0.<init>(r1, r2, r12, r3);	 Catch:{ Exception -> 0x00fc }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r20);	 Catch:{ Exception -> 0x00fc }
    L_0x00d1:
        return;
    L_0x00d2:
        if (r14 == 0) goto L_0x00d8;
    L_0x00d4:
        r14.close();	 Catch:{ Exception -> 0x00b1, all -> 0x0101 }
        r14 = 0;
    L_0x00d8:
        r13 = r13 + 1;
        r15 = r14;
        goto L_0x0068;
    L_0x00dc:
        r20 = 0;
        r21 = 1;
        r12[r20] = r21;	 Catch:{ Exception -> 0x010c, all -> 0x0105 }
        if (r15 == 0) goto L_0x00e7;
    L_0x00e4:
        r15.close();	 Catch:{ Exception -> 0x00fc }
    L_0x00e7:
        if (r17 == 0) goto L_0x0111;
    L_0x00e9:
        r17.close();	 Catch:{ Exception -> 0x00fc }
        r16 = r17;
        r14 = r15;
        goto L_0x00c1;
    L_0x00f0:
        r20 = move-exception;
    L_0x00f1:
        if (r14 == 0) goto L_0x00f6;
    L_0x00f3:
        r14.close();	 Catch:{ Exception -> 0x00fc }
    L_0x00f6:
        if (r16 == 0) goto L_0x00fb;
    L_0x00f8:
        r16.close();	 Catch:{ Exception -> 0x00fc }
    L_0x00fb:
        throw r20;	 Catch:{ Exception -> 0x00fc }
    L_0x00fc:
        r8 = move-exception;
        com.google.devtools.build.android.desugar.runtime.ThrowableExtension.printStackTrace(r8);
        goto L_0x00d1;
    L_0x0101:
        r20 = move-exception;
        r16 = r17;
        goto L_0x00f1;
    L_0x0105:
        r20 = move-exception;
        r16 = r17;
        r14 = r15;
        goto L_0x00f1;
    L_0x010a:
        r8 = move-exception;
        goto L_0x00b4;
    L_0x010c:
        r8 = move-exception;
        r16 = r17;
        r14 = r15;
        goto L_0x00b4;
    L_0x0111:
        r16 = r17;
        r14 = r15;
        goto L_0x00c1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.SettingsActivity.lambda$sendLogs$10$SettingsActivity(org.telegram.ui.ActionBar.AlertDialog):void");
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$9$SettingsActivity(AlertDialog progressDialog, boolean[] finished, File zipFile) {
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
            i.putExtra("android.intent.extra.EMAIL", "");
            i.putExtra("android.intent.extra.SUBJECT", "Logs from " + LocaleController.getInstance().formatterStats.format(System.currentTimeMillis()));
            i.putExtra("android.intent.extra.STREAM", uri);
            getParentActivity().startActivityForResult(Intent.createChooser(i, "Select email application."), 500);
            return;
        }
        Toast.makeText(getParentActivity(), LocaleController.getString("ErrorOccurred", NUM), 0).show();
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[34];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{EmptyCell.class, HeaderCell.class, TextDetailCell.class, TextCell.class}, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundGray");
        themeDescriptionArr[2] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "avatar_backgroundActionBarBlue");
        themeDescriptionArr[4] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "avatar_backgroundActionBarBlue");
        themeDescriptionArr[5] = new ThemeDescription(this.extraHeightView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "avatar_backgroundActionBarBlue");
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "avatar_actionBarIconBlue");
        themeDescriptionArr[7] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        themeDescriptionArr[8] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "avatar_actionBarSelectorBlue");
        themeDescriptionArr[9] = new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "profile_title");
        themeDescriptionArr[10] = new ThemeDescription(this.onlineTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "avatar_subtitleInProfileBlue");
        themeDescriptionArr[11] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, "actionBarDefaultSubmenuBackground");
        themeDescriptionArr[12] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, "actionBarDefaultSubmenuItem");
        themeDescriptionArr[13] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        themeDescriptionArr[14] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        themeDescriptionArr[15] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        themeDescriptionArr[16] = new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[17] = new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteValueText");
        themeDescriptionArr[18] = new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, null, null, null, "windowBackgroundWhiteGrayIcon");
        themeDescriptionArr[19] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueHeader");
        themeDescriptionArr[20] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[21] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        themeDescriptionArr[22] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        themeDescriptionArr[23] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText3");
        themeDescriptionArr[24] = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[]{Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, "avatar_text");
        themeDescriptionArr[25] = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[]{this.avatarDrawable}, null, "avatar_backgroundInProfileBlue");
        themeDescriptionArr[26] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "profile_actionIcon");
        themeDescriptionArr[27] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "profile_actionBackground");
        themeDescriptionArr[28] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "profile_actionPressedBackground");
        themeDescriptionArr[29] = new ThemeDescription(this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, null, null, null, "key_graySectionText");
        themeDescriptionArr[30] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, "graySection");
        themeDescriptionArr[31] = new ThemeDescription(this.listView, 0, new Class[]{SettingsSearchCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[32] = new ThemeDescription(this.listView, 0, new Class[]{SettingsSearchCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        themeDescriptionArr[33] = new ThemeDescription(this.listView, 0, new Class[]{SettingsSearchCell.class}, new String[]{"imageView"}, null, null, null, "windowBackgroundWhiteGrayIcon");
        return themeDescriptionArr;
    }
}
