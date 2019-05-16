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
import android.os.Parcelable;
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
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutParams;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
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
import org.telegram.messenger.ImageLocation;
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
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.Page;
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
import org.telegram.tgnet.TLRPC.TL_userProfilePhoto;
import org.telegram.tgnet.TLRPC.TL_userProfilePhotoEmpty;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserFull;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
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
import org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate;
import org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate.-CC;
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
        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int i, boolean z) {
            if (fileLocation == null) {
                return null;
            }
            User user = MessagesController.getInstance(SettingsActivity.this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(SettingsActivity.this.currentAccount).getClientUserId()));
            if (user != null) {
                UserProfilePhoto userProfilePhoto = user.photo;
                if (userProfilePhoto != null) {
                    FileLocation fileLocation2 = userProfilePhoto.photo_big;
                    if (fileLocation2 != null && fileLocation2.local_id == fileLocation.local_id && fileLocation2.volume_id == fileLocation.volume_id && fileLocation2.dc_id == fileLocation.dc_id) {
                        int[] iArr = new int[2];
                        SettingsActivity.this.avatarImage.getLocationInWindow(iArr);
                        PlaceProviderObject placeProviderObject = new PlaceProviderObject();
                        i = 0;
                        placeProviderObject.viewX = iArr[0];
                        int i2 = iArr[1];
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
                        placeProviderObject.scale = SettingsActivity.this.avatarContainer.getScaleX();
                        return placeProviderObject;
                    }
                }
            }
            return null;
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
    private UserFull userInfo;
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

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType != 0) {
                if (itemViewType == 2) {
                    TextCell textCell = (TextCell) viewHolder.itemView;
                    if (i == SettingsActivity.this.languageRow) {
                        textCell.setTextAndIcon(LocaleController.getString("Language", NUM), NUM, true);
                    } else if (i == SettingsActivity.this.notificationRow) {
                        textCell.setTextAndIcon(LocaleController.getString("NotificationsAndSounds", NUM), NUM, true);
                    } else if (i == SettingsActivity.this.privacyRow) {
                        textCell.setTextAndIcon(LocaleController.getString("PrivacySettings", NUM), NUM, true);
                    } else if (i == SettingsActivity.this.dataRow) {
                        textCell.setTextAndIcon(LocaleController.getString("DataSettings", NUM), NUM, true);
                    } else if (i == SettingsActivity.this.chatRow) {
                        textCell.setTextAndIcon(LocaleController.getString("ChatSettings", NUM), NUM, true);
                    } else if (i == SettingsActivity.this.helpRow) {
                        textCell.setTextAndIcon(LocaleController.getString("SettingsHelp", NUM), NUM, false);
                    }
                } else if (itemViewType != 4) {
                    if (itemViewType == 6) {
                        TextDetailCell textDetailCell = (TextDetailCell) viewHolder.itemView;
                        User currentUser;
                        String format;
                        if (i == SettingsActivity.this.numberRow) {
                            currentUser = UserConfig.getInstance(SettingsActivity.this.currentAccount).getCurrentUser();
                            if (currentUser != null) {
                                String str = currentUser.phone;
                                if (!(str == null || str.length() == 0)) {
                                    PhoneFormat instance = PhoneFormat.getInstance();
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append("+");
                                    stringBuilder.append(currentUser.phone);
                                    format = instance.format(stringBuilder.toString());
                                    textDetailCell.setTextAndValue(format, LocaleController.getString("TapToChangePhone", NUM), true);
                                }
                            }
                            format = LocaleController.getString("NumberUnknown", NUM);
                            textDetailCell.setTextAndValue(format, LocaleController.getString("TapToChangePhone", NUM), true);
                        } else if (i == SettingsActivity.this.usernameRow) {
                            currentUser = UserConfig.getInstance(SettingsActivity.this.currentAccount).getCurrentUser();
                            if (currentUser == null || TextUtils.isEmpty(currentUser.username)) {
                                format = LocaleController.getString("UsernameEmpty", NUM);
                            } else {
                                StringBuilder stringBuilder2 = new StringBuilder();
                                stringBuilder2.append("@");
                                stringBuilder2.append(currentUser.username);
                                format = stringBuilder2.toString();
                            }
                            textDetailCell.setTextAndValue(format, LocaleController.getString("Username", NUM), true);
                        } else if (i == SettingsActivity.this.bioRow) {
                            String str2 = "UserBio";
                            if (SettingsActivity.this.userInfo == null || !TextUtils.isEmpty(SettingsActivity.this.userInfo.about)) {
                                textDetailCell.setTextWithEmojiAndValue(SettingsActivity.this.userInfo == null ? LocaleController.getString("Loading", NUM) : SettingsActivity.this.userInfo.about, LocaleController.getString(str2, NUM), false);
                            } else {
                                textDetailCell.setTextAndValue(LocaleController.getString(str2, NUM), LocaleController.getString("UserBioDetail", NUM), false);
                            }
                        }
                    }
                } else if (i == SettingsActivity.this.settingsSectionRow2) {
                    ((HeaderCell) viewHolder.itemView).setText(LocaleController.getString("SETTINGS", NUM));
                } else if (i == SettingsActivity.this.numberSectionRow) {
                    ((HeaderCell) viewHolder.itemView).setText(LocaleController.getString("Account", NUM));
                }
            } else if (i == SettingsActivity.this.overscrollRow) {
                ((EmptyCell) viewHolder.itemView).setHeight(AndroidUtilities.dp(88.0f));
            }
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            return adapterPosition == SettingsActivity.this.notificationRow || adapterPosition == SettingsActivity.this.numberRow || adapterPosition == SettingsActivity.this.privacyRow || adapterPosition == SettingsActivity.this.languageRow || adapterPosition == SettingsActivity.this.usernameRow || adapterPosition == SettingsActivity.this.bioRow || adapterPosition == SettingsActivity.this.versionRow || adapterPosition == SettingsActivity.this.dataRow || adapterPosition == SettingsActivity.this.chatRow || adapterPosition == SettingsActivity.this.helpRow;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = null;
            String str = "windowBackgroundWhite";
            if (i == 0) {
                view = new EmptyCell(this.mContext);
                view.setBackgroundColor(Theme.getColor(str));
            } else if (i == 1) {
                view = new ShadowSectionCell(this.mContext);
            } else if (i == 2) {
                view = new TextCell(this.mContext);
                view.setBackgroundColor(Theme.getColor(str));
            } else if (i == 4) {
                view = new HeaderCell(this.mContext, 23);
                view.setBackgroundColor(Theme.getColor(str));
            } else if (i == 5) {
                View textInfoPrivacyCell = new TextInfoPrivacyCell(this.mContext, 10);
                textInfoPrivacyCell.getTextView().setGravity(1);
                textInfoPrivacyCell.getTextView().setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
                textInfoPrivacyCell.getTextView().setMovementMethod(null);
                textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                try {
                    PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                    int i2 = packageInfo.versionCode / 10;
                    String str2 = "";
                    switch (packageInfo.versionCode % 10) {
                        case 0:
                        case 9:
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("universal ");
                            stringBuilder.append(Build.CPU_ABI);
                            stringBuilder.append(" ");
                            stringBuilder.append(Build.CPU_ABI2);
                            str2 = stringBuilder.toString();
                            break;
                        case 1:
                        case 3:
                            str2 = "arm-v7a";
                            break;
                        case 2:
                        case 4:
                            str2 = "x86";
                            break;
                        case 5:
                        case 7:
                            str2 = "arm64-v8a";
                            break;
                        case 6:
                        case 8:
                            str2 = "x86_64";
                            break;
                        default:
                            break;
                    }
                    Object[] objArr = new Object[1];
                    objArr[0] = String.format(Locale.US, "v%s (%d) %s", new Object[]{packageInfo.versionName, Integer.valueOf(i2), str2});
                    textInfoPrivacyCell.setText(LocaleController.formatString("TelegramVersion", NUM, objArr));
                } catch (Exception e) {
                    FileLog.e(e);
                }
                textInfoPrivacyCell.getTextView().setPadding(0, AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(14.0f));
                view = textInfoPrivacyCell;
            } else if (i == 6) {
                view = new TextDetailCell(this.mContext);
                view.setBackgroundColor(Theme.getColor(str));
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
        }

        public int getItemViewType(int i) {
            if (i == SettingsActivity.this.overscrollRow) {
                return 0;
            }
            if (i == SettingsActivity.this.settingsSectionRow) {
                return 1;
            }
            if (i == SettingsActivity.this.notificationRow || i == SettingsActivity.this.privacyRow || i == SettingsActivity.this.languageRow || i == SettingsActivity.this.dataRow || i == SettingsActivity.this.chatRow || i == SettingsActivity.this.helpRow) {
                return 2;
            }
            if (i == SettingsActivity.this.versionRow) {
                return 5;
            }
            if (i == SettingsActivity.this.numberRow || i == SettingsActivity.this.usernameRow || i == SettingsActivity.this.bioRow) {
                return 6;
            }
            if (i == SettingsActivity.this.settingsSectionRow2 || i == SettingsActivity.this.numberSectionRow) {
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
        final /* synthetic */ SettingsActivity this$0;

        private class FaqSearchResult {
            private int num;
            private String[] path;
            private String title;
            private String url;

            public FaqSearchResult(String str, String[] strArr, String str2) {
                this.title = str;
                this.path = strArr;
                this.url = str2;
            }

            public boolean equals(Object obj) {
                if (!(obj instanceof FaqSearchResult)) {
                    return false;
                }
                return this.title.equals(((FaqSearchResult) obj).title);
            }

            public String toString() {
                SerializedData serializedData = new SerializedData();
                serializedData.writeInt32(this.num);
                int i = 0;
                serializedData.writeInt32(0);
                serializedData.writeString(this.title);
                String[] strArr = this.path;
                serializedData.writeInt32(strArr != null ? strArr.length : 0);
                if (this.path != null) {
                    while (true) {
                        strArr = this.path;
                        if (i >= strArr.length) {
                            break;
                        }
                        serializedData.writeString(strArr[i]);
                        i++;
                    }
                }
                serializedData.writeString(this.url);
                return Utilities.bytesToHex(serializedData.toByteArray());
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

            public SearchResult(SearchAdapter searchAdapter, int i, String str, int i2, Runnable runnable) {
                this(i, str, null, null, null, i2, runnable);
            }

            public SearchResult(SearchAdapter searchAdapter, int i, String str, String str2, int i2, Runnable runnable) {
                this(i, str, null, str2, null, i2, runnable);
            }

            public SearchResult(SearchAdapter searchAdapter, int i, String str, String str2, String str3, int i2, Runnable runnable) {
                this(i, str, str2, str3, null, i2, runnable);
            }

            public SearchResult(int i, String str, String str2, String str3, String str4, int i2, Runnable runnable) {
                this.guid = i;
                this.searchTitle = str;
                this.rowName = str2;
                this.openRunnable = runnable;
                this.iconResId = i2;
                if (str3 != null && str4 != null) {
                    this.path = new String[]{str3, str4};
                } else if (str3 != null) {
                    this.path = new String[]{str3};
                }
            }

            public boolean equals(Object obj) {
                boolean z = false;
                if (!(obj instanceof SearchResult)) {
                    return false;
                }
                if (this.guid == ((SearchResult) obj).guid) {
                    z = true;
                }
                return z;
            }

            public String toString() {
                SerializedData serializedData = new SerializedData();
                serializedData.writeInt32(this.num);
                serializedData.writeInt32(1);
                serializedData.writeInt32(this.guid);
                return Utilities.bytesToHex(serializedData.toByteArray());
            }

            private void open() {
                this.openRunnable.run();
                if (this.rowName != null) {
                    BaseFragment baseFragment = (BaseFragment) SearchAdapter.this.this$0.parentLayout.fragmentsStack.get(SearchAdapter.this.this$0.parentLayout.fragmentsStack.size() - 1);
                    try {
                        Field declaredField = baseFragment.getClass().getDeclaredField("listView");
                        declaredField.setAccessible(true);
                        ((RecyclerListView) declaredField.get(baseFragment)).highlightRow(new -$$Lambda$SettingsActivity$SearchAdapter$SearchResult$QTQolgPlTMmAnvlmOMDH38UI6H4(this, baseFragment));
                        declaredField.setAccessible(false);
                    } catch (Throwable unused) {
                    }
                }
            }

            public /* synthetic */ int lambda$open$0$SettingsActivity$SearchAdapter$SearchResult(BaseFragment baseFragment) {
                int i = -1;
                try {
                    Field declaredField = baseFragment.getClass().getDeclaredField(this.rowName);
                    Field declaredField2 = baseFragment.getClass().getDeclaredField("layoutManager");
                    declaredField.setAccessible(true);
                    declaredField2.setAccessible(true);
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) declaredField2.get(baseFragment);
                    i = declaredField.getInt(baseFragment);
                    linearLayoutManager.scrollToPositionWithOffset(i, 0);
                    declaredField.setAccessible(false);
                    declaredField2.setAccessible(false);
                    return i;
                } catch (Throwable unused) {
                    return i;
                }
            }
        }

        public /* synthetic */ void lambda$new$0$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new ChangeNameActivity());
        }

        public /* synthetic */ void lambda$new$1$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new ChangePhoneHelpActivity());
        }

        public /* synthetic */ void lambda$new$2$SettingsActivity$SearchAdapter() {
            int i = 0;
            while (i < 3) {
                if (!UserConfig.getInstance(i).isClientActivated()) {
                    break;
                }
                i++;
            }
            i = -1;
            if (i >= 0) {
                this.this$0.presentFragment(new LoginActivity(i));
            }
        }

        public /* synthetic */ void lambda$new$3$SettingsActivity$SearchAdapter() {
            if (this.this$0.userInfo != null) {
                this.this$0.presentFragment(new ChangeBioActivity());
            }
        }

        public /* synthetic */ void lambda$new$4$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new NotificationsSettingsActivity());
        }

        public /* synthetic */ void lambda$new$5$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new NotificationsCustomSettingsActivity(1, new ArrayList(), true));
        }

        public /* synthetic */ void lambda$new$6$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new NotificationsCustomSettingsActivity(0, new ArrayList(), true));
        }

        public /* synthetic */ void lambda$new$7$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new NotificationsCustomSettingsActivity(2, new ArrayList(), true));
        }

        public /* synthetic */ void lambda$new$8$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new NotificationsSettingsActivity());
        }

        public /* synthetic */ void lambda$new$9$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new NotificationsSettingsActivity());
        }

        public /* synthetic */ void lambda$new$10$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new NotificationsSettingsActivity());
        }

        public /* synthetic */ void lambda$new$11$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new NotificationsSettingsActivity());
        }

        public /* synthetic */ void lambda$new$12$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new NotificationsSettingsActivity());
        }

        public /* synthetic */ void lambda$new$13$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new NotificationsSettingsActivity());
        }

        public /* synthetic */ void lambda$new$14$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacySettingsActivity());
        }

        public /* synthetic */ void lambda$new$15$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new BlockedUsersActivity());
        }

        public /* synthetic */ void lambda$new$16$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacyControlActivity(0, true));
        }

        public /* synthetic */ void lambda$new$17$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacyControlActivity(4, true));
        }

        public /* synthetic */ void lambda$new$18$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacyControlActivity(5, true));
        }

        public /* synthetic */ void lambda$new$19$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacyControlActivity(3, true));
        }

        public /* synthetic */ void lambda$new$20$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacyControlActivity(2, true));
        }

        public /* synthetic */ void lambda$new$21$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacyControlActivity(1, true));
        }

        public /* synthetic */ void lambda$new$22$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new PasscodeActivity(SharedConfig.passcodeHash.length() > 0 ? 2 : 0));
        }

        public /* synthetic */ void lambda$new$23$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new TwoStepVerificationActivity(0));
        }

        public /* synthetic */ void lambda$new$24$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new SessionsActivity(0));
        }

        public /* synthetic */ void lambda$new$25$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacySettingsActivity());
        }

        public /* synthetic */ void lambda$new$26$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacySettingsActivity());
        }

        public /* synthetic */ void lambda$new$27$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacySettingsActivity());
        }

        public /* synthetic */ void lambda$new$28$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new SessionsActivity(1));
        }

        public /* synthetic */ void lambda$new$29$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacySettingsActivity());
        }

        public /* synthetic */ void lambda$new$30$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacySettingsActivity());
        }

        public /* synthetic */ void lambda$new$31$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacySettingsActivity());
        }

        public /* synthetic */ void lambda$new$32$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacySettingsActivity());
        }

        public /* synthetic */ void lambda$new$33$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacySettingsActivity());
        }

        public /* synthetic */ void lambda$new$34$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        public /* synthetic */ void lambda$new$35$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        public /* synthetic */ void lambda$new$36$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new CacheControlActivity());
        }

        public /* synthetic */ void lambda$new$37$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new CacheControlActivity());
        }

        public /* synthetic */ void lambda$new$38$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new CacheControlActivity());
        }

        public /* synthetic */ void lambda$new$39$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new CacheControlActivity());
        }

        public /* synthetic */ void lambda$new$40$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataUsageActivity());
        }

        public /* synthetic */ void lambda$new$41$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        public /* synthetic */ void lambda$new$42$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataAutoDownloadActivity(0));
        }

        public /* synthetic */ void lambda$new$43$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataAutoDownloadActivity(1));
        }

        public /* synthetic */ void lambda$new$44$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataAutoDownloadActivity(2));
        }

        public /* synthetic */ void lambda$new$45$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        public /* synthetic */ void lambda$new$46$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        public /* synthetic */ void lambda$new$47$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        public /* synthetic */ void lambda$new$48$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        public /* synthetic */ void lambda$new$49$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        public /* synthetic */ void lambda$new$50$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        public /* synthetic */ void lambda$new$51$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        public /* synthetic */ void lambda$new$52$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        public /* synthetic */ void lambda$new$53$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        public /* synthetic */ void lambda$new$54$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new ProxyListActivity());
        }

        public /* synthetic */ void lambda$new$55$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new ProxyListActivity());
        }

        public /* synthetic */ void lambda$new$56$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        public /* synthetic */ void lambda$new$57$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        public /* synthetic */ void lambda$new$58$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new WallpapersListActivity(0));
        }

        public /* synthetic */ void lambda$new$59$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new WallpapersListActivity(1));
        }

        public /* synthetic */ void lambda$new$60$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new WallpapersListActivity(0));
        }

        public /* synthetic */ void lambda$new$61$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new ThemeActivity(1));
        }

        public /* synthetic */ void lambda$new$62$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        public /* synthetic */ void lambda$new$63$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        public /* synthetic */ void lambda$new$64$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        public /* synthetic */ void lambda$new$65$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        public /* synthetic */ void lambda$new$66$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        public /* synthetic */ void lambda$new$67$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        public /* synthetic */ void lambda$new$68$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        public /* synthetic */ void lambda$new$69$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new StickersActivity(0));
        }

        public /* synthetic */ void lambda$new$70$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new StickersActivity(0));
        }

        public /* synthetic */ void lambda$new$71$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new FeaturedStickersActivity());
        }

        public /* synthetic */ void lambda$new$72$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new StickersActivity(1));
        }

        public /* synthetic */ void lambda$new$73$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new ArchivedStickersActivity(0));
        }

        public /* synthetic */ void lambda$new$74$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new ArchivedStickersActivity(1));
        }

        public /* synthetic */ void lambda$new$75$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new LanguageSelectActivity());
        }

        public /* synthetic */ void lambda$new$77$SettingsActivity$SearchAdapter() {
            SettingsActivity settingsActivity = this.this$0;
            settingsActivity.showDialog(AlertsCreator.createSupportAlert(settingsActivity));
        }

        public /* synthetic */ void lambda$new$78$SettingsActivity$SearchAdapter() {
            Browser.openUrl(this.this$0.getParentActivity(), LocaleController.getString("TelegramFaqUrl", NUM));
        }

        public /* synthetic */ void lambda$new$79$SettingsActivity$SearchAdapter() {
            Browser.openUrl(this.this$0.getParentActivity(), LocaleController.getString("PrivacyPolicyUrl", NUM));
        }

        public SearchAdapter(SettingsActivity settingsActivity, Context context) {
            this.this$0 = settingsActivity;
            SearchResult[] searchResultArr = new SearchResult[80];
            searchResultArr[0] = new SearchResult(this, 500, LocaleController.getString("EditName", NUM), 0, new -$$Lambda$SettingsActivity$SearchAdapter$fYhNxPaKcCcAqdniDsRVcdOfAcw(this));
            searchResultArr[1] = new SearchResult(this, 501, LocaleController.getString("ChangePhoneNumber", NUM), 0, new -$$Lambda$SettingsActivity$SearchAdapter$AQE0PybSOWsTppwbXC4ScpJt5cU(this));
            searchResultArr[2] = new SearchResult(this, 502, LocaleController.getString("AddAnotherAccount", NUM), 0, new -$$Lambda$SettingsActivity$SearchAdapter$qf5DMONPDpbbFVQlIYoeluu6fAg(this));
            searchResultArr[3] = new SearchResult(this, 503, LocaleController.getString("UserBio", NUM), 0, new -$$Lambda$SettingsActivity$SearchAdapter$wEStD-IsL8y26JcLIYLGQZisGR0(this));
            String str = "NotificationsAndSounds";
            searchResultArr[4] = new SearchResult(this, 1, LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$pgorJIrrbTNFMCB_ShcfXSVsFZo(this));
            searchResultArr[5] = new SearchResult(this, 2, LocaleController.getString("NotificationsPrivateChats", NUM), LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$EaevAppnRfEWu4EsBvHSVnfoJO4(this));
            searchResultArr[6] = new SearchResult(this, 3, LocaleController.getString("NotificationsGroups", NUM), LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$-Yb9Kq1MuLzQbkVBNuUX-K5eF_U(this));
            searchResultArr[7] = new SearchResult(this, 4, LocaleController.getString("NotificationsChannels", NUM), LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$zEBaSkQhcY7piZudVwqn9mABazs(this));
            searchResultArr[8] = new SearchResult(this, 5, LocaleController.getString("VoipNotificationSettings", NUM), "callsSectionRow", LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$aceyqZ4yZ6dj0j20qgJOi7MQ0NE(this));
            searchResultArr[9] = new SearchResult(this, 6, LocaleController.getString("BadgeNumber", NUM), "badgeNumberSection", LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$t5wmPo8eU16Y-X8HwQqExxITKKQ(this));
            searchResultArr[10] = new SearchResult(this, 7, LocaleController.getString("InAppNotifications", NUM), "inappSectionRow", LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$S2uiNLXT4KCsYO2kv5G38zxY-Ow(this));
            searchResultArr[11] = new SearchResult(this, 8, LocaleController.getString("ContactJoined", NUM), "contactJoinedRow", LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$Dee0X2OE_oxy67HDXUAjVnFzV7c(this));
            searchResultArr[12] = new SearchResult(this, 9, LocaleController.getString("PinnedMessages", NUM), "pinnedMessageRow", LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$O36kxEh77vwepAjM1YBjwwIEdDY(this));
            searchResultArr[13] = new SearchResult(this, 10, LocaleController.getString("ResetAllNotifications", NUM), "resetNotificationsRow", LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$OmbyIf1DIC6b-M8uR5C3UfAIxWo(this));
            str = "PrivacySettings";
            searchResultArr[14] = new SearchResult(this, 100, LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$fw82m1Z1AIBCwhHRMVab7sgzrow(this));
            searchResultArr[15] = new SearchResult(this, 101, LocaleController.getString("BlockedUsers", NUM), LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$z1aqQi8eq9En3J0XVwkCNdcd6mM(this));
            searchResultArr[16] = new SearchResult(this, 102, LocaleController.getString("PrivacyLastSeen", NUM), LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$f_3utWtHCzlSn6039S3bgof9vWg(this));
            searchResultArr[17] = new SearchResult(this, 103, LocaleController.getString("PrivacyProfilePhoto", NUM), LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$xiSHHuIuMQVeQ_fdA3Wj_Bgq4iA(this));
            searchResultArr[18] = new SearchResult(this, 104, LocaleController.getString("PrivacyForwards", NUM), LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$RYPspq-lY6i8gNBxBdrIhxctFpo(this));
            searchResultArr[19] = new SearchResult(this, 105, LocaleController.getString("PrivacyP2P", NUM), LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$6LoBQomheYgP8glCLASSNAMExFz2WzjyY(this));
            searchResultArr[20] = new SearchResult(this, 106, LocaleController.getString("Calls", NUM), LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$VprFTsbEA5x2jnmQ8O1zs63h-9E(this));
            searchResultArr[21] = new SearchResult(this, 107, LocaleController.getString("GroupsAndChannels", NUM), LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$c5DPXMo95Hg5TAoapaOpYope9ao(this));
            searchResultArr[22] = new SearchResult(this, 108, LocaleController.getString("Passcode", NUM), LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$YveYfMPgJtpzBc_Aye2IlUKRL_E(this));
            searchResultArr[23] = new SearchResult(this, 109, LocaleController.getString("TwoStepVerification", NUM), LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$OE5ko5HrJRcdo_04aDjTiMIRF4o(this));
            searchResultArr[24] = new SearchResult(this, 110, LocaleController.getString("SessionsTitle", NUM), LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$-P9hp4zriAme7mOQTdmdfHidC8Y(this));
            searchResultArr[25] = new SearchResult(this, 111, LocaleController.getString("PrivacyDeleteCloudDrafts", NUM), "clearDraftsRow", LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$B_YXrprYoUDKN_R4RWC-vN8dQ3Y(this));
            searchResultArr[26] = new SearchResult(this, 112, LocaleController.getString("DeleteAccountIfAwayFor2", NUM), "deleteAccountRow", LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$5sT7VrARMFm8AXSv5eWPD8wnHec(this));
            searchResultArr[27] = new SearchResult(this, 113, LocaleController.getString("PrivacyPaymentsClear", NUM), "paymentsClearRow", LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$lQlwXu3fj_1ozEGBWi7cV-Yrauo(this));
            searchResultArr[28] = new SearchResult(this, 114, LocaleController.getString("WebSessionsTitle", NUM), LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$VLzl5etbBP7OOe7IzDvNV3te2pQ(this));
            searchResultArr[29] = new SearchResult(this, 115, LocaleController.getString("SyncContactsDelete", NUM), "contactsDeleteRow", LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$i8RSZV17ziFovJKq7XfIMHoM02o(this));
            searchResultArr[30] = new SearchResult(this, 116, LocaleController.getString("SyncContacts", NUM), "contactsSyncRow", LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$ykgPCWhfDa0SAQRBusRsel30WAo(this));
            searchResultArr[31] = new SearchResult(this, 117, LocaleController.getString("SuggestContacts", NUM), "contactsSuggestRow", LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$ogTF-_W6GjBUa6jCJUHIlUDUEH0(this));
            searchResultArr[32] = new SearchResult(this, 118, LocaleController.getString("MapPreviewProvider", NUM), "secretMapRow", LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$gXqnmEjhglRSWqeZPzgMYOpOhsk(this));
            searchResultArr[33] = new SearchResult(this, 119, LocaleController.getString("SecretWebPage", NUM), "secretWebpageRow", LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$lb1RmyZ9zEsc2hcDKKU7dHdJeD8(this));
            String str2 = "DataSettings";
            searchResultArr[34] = new SearchResult(this, 200, LocaleController.getString(str2, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$CPyFvh3ngQ1x3gECH51yA4WVVU8(this));
            searchResultArr[35] = new SearchResult(this, 201, LocaleController.getString("DataUsage", NUM), "usageSectionRow", LocaleController.getString(str2, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$SR0QQ8fXitd2n2FmlZnOuv-PjCM(this));
            String str3 = "StorageUsage";
            searchResultArr[36] = new SearchResult(this, 202, LocaleController.getString(str3, NUM), LocaleController.getString(str2, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$jxZ1ug5oHHZLFprdCJ2sObOMdeY(this));
            String str4 = str3;
            searchResultArr[37] = new SearchResult(203, LocaleController.getString("KeepMedia", NUM), "keepMediaRow", LocaleController.getString(str2, NUM), LocaleController.getString(str3, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$2a_PtgyUOPZ5daThh02W6OTXvjI(this));
            searchResultArr[38] = new SearchResult(204, LocaleController.getString("ClearMediaCache", NUM), "cacheRow", LocaleController.getString(str2, NUM), LocaleController.getString(str4, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$OfGBrtqXQbffkrMJs-wPG7G0fXA(this));
            searchResultArr[39] = new SearchResult(205, LocaleController.getString("LocalDatabase", NUM), "databaseRow", LocaleController.getString(str2, NUM), LocaleController.getString(str4, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$d-3fa2mVqtsd9YcCWBrdaK4AvwI(this));
            searchResultArr[40] = new SearchResult(this, 206, LocaleController.getString("NetworkUsage", NUM), LocaleController.getString(str2, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$TWYG1OVbu9uZ6lFX1vM2hYJxs7w(this));
            searchResultArr[41] = new SearchResult(this, 207, LocaleController.getString("AutomaticMediaDownload", NUM), "mediaDownloadSectionRow", LocaleController.getString(str2, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$2CLVCiRkKjEbcz0jARW3ZP41JX8(this));
            searchResultArr[42] = new SearchResult(this, 208, LocaleController.getString("WhenUsingMobileData", NUM), LocaleController.getString(str2, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$VUKBbuzi1KBkCnnX2JlWeTm2Cac(this));
            searchResultArr[43] = new SearchResult(this, 209, LocaleController.getString("WhenConnectedOnWiFi", NUM), LocaleController.getString(str2, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$K3WxuNbtWdfw44GPZwu3Vih5_x4(this));
            searchResultArr[44] = new SearchResult(this, 210, LocaleController.getString("WhenRoaming", NUM), LocaleController.getString(str2, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$BLHHvMFpEDhqBkzKQy4VYJV2Wzg(this));
            searchResultArr[45] = new SearchResult(this, 211, LocaleController.getString("ResetAutomaticMediaDownload", NUM), "resetDownloadRow", LocaleController.getString(str2, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$Pqx2xX3w9fZ7nlbzuimuQvpIHXo(this));
            searchResultArr[46] = new SearchResult(this, 212, LocaleController.getString("AutoplayMedia", NUM), "autoplayHeaderRow", LocaleController.getString(str2, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$LMKnGgygm7Zy5y5yA0ZMoVKQ0rQ(this));
            searchResultArr[47] = new SearchResult(this, 213, LocaleController.getString("AutoplayGIF", NUM), "autoplayGifsRow", LocaleController.getString(str2, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$lrHAN36teH_lCOFJ5PXNFKTwHTM(this));
            searchResultArr[48] = new SearchResult(this, 214, LocaleController.getString("AutoplayVideo", NUM), "autoplayVideoRow", LocaleController.getString(str2, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$raORBf9hVrFvO0t2rS9X8ZjwExo(this));
            searchResultArr[49] = new SearchResult(this, 215, LocaleController.getString("Streaming", NUM), "streamSectionRow", LocaleController.getString(str2, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$ZTzEE1deo_nmR-UL_VYD8uhoHoI(this));
            searchResultArr[50] = new SearchResult(this, 216, LocaleController.getString("EnableStreaming", NUM), "enableStreamRow", LocaleController.getString(str2, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$rOx4Eve-2YVGzrlUGIGIovv9JXE(this));
            searchResultArr[51] = new SearchResult(this, 217, LocaleController.getString("Calls", NUM), "callsSectionRow", LocaleController.getString(str2, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$rGwlYZXuIw_8MDI7I45SYbMs0G8(this));
            searchResultArr[52] = new SearchResult(this, 218, LocaleController.getString("VoipUseLessData", NUM), "useLessDataForCallsRow", LocaleController.getString(str2, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$yQdZ2pbhXfP1NJfQiJKJVPctbyk(this));
            searchResultArr[53] = new SearchResult(this, 219, LocaleController.getString("VoipQuickReplies", NUM), "quickRepliesRow", LocaleController.getString(str2, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$B4etMl_s5sjFGhMYip4XHa1DxEY(this));
            searchResultArr[54] = new SearchResult(this, 220, LocaleController.getString("ProxySettings", NUM), LocaleController.getString(str2, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$i2kSSNMxTtfCLASSNAMERmjV3ptURjCLA(this));
            searchResultArr[55] = new SearchResult(221, LocaleController.getString("UseProxyForCalls", NUM), "callsRow", LocaleController.getString(str2, NUM), LocaleController.getString("ProxySettings", NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$XMZSE9GUh-0GP5dBwOjHByrjteo(this));
            str = "ChatSettings";
            searchResultArr[56] = new SearchResult(this, 300, LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$t63Mb0PWa1-oMCnIjcS62wWkWhI(this));
            searchResultArr[57] = new SearchResult(this, 301, LocaleController.getString("TextSizeHeader", NUM), "textSizeHeaderRow", LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$oHMz_dP6H50aHqxTbdc3E7d4Oyo(this));
            searchResultArr[58] = new SearchResult(this, 302, LocaleController.getString("ChatBackground", NUM), LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$PQPKVfMuNVXvznSLWUi9d_FbGD8(this));
            searchResultArr[59] = new SearchResult(303, LocaleController.getString("SetColor", NUM), null, LocaleController.getString(str, NUM), LocaleController.getString("ChatBackground", NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$hzumI77K9pwC4a7mUUTUW31jkYs(this));
            searchResultArr[60] = new SearchResult(304, LocaleController.getString("ResetChatBackgrounds", NUM), "resetRow", LocaleController.getString(str, NUM), LocaleController.getString("ChatBackground", NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$Ov5OcCfPmPzmx4efoTSoB8-YMKk(this));
            searchResultArr[61] = new SearchResult(this, 305, LocaleController.getString("AutoNightTheme", NUM), LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$mEajmgrt04XurJBLXFyM6GNHru0(this));
            searchResultArr[62] = new SearchResult(this, 306, LocaleController.getString("ColorTheme", NUM), "themeHeaderRow", LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$vSOlOOSDVPitPhRnpx1o7rNw4zw(this));
            searchResultArr[63] = new SearchResult(this, 307, LocaleController.getString("ChromeCustomTabs", NUM), "customTabsRow", LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$46GIfyE4OhRvg3Gs4uaPMW_J3UM(this));
            searchResultArr[64] = new SearchResult(this, 308, LocaleController.getString("DirectShare", NUM), "directShareRow", LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$s0h_sbhuVis6N-TS7j6bEyh7FfQ(this));
            searchResultArr[65] = new SearchResult(this, 309, LocaleController.getString("EnableAnimations", NUM), "enableAnimationsRow", LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$5_PGzT67poE7m5IzEQLBofn8u-o(this));
            searchResultArr[66] = new SearchResult(this, 310, LocaleController.getString("RaiseToSpeak", NUM), "raiseToSpeakRow", LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$djYxV0TLWfpCNMAt-hbj8er6rqo(this));
            searchResultArr[67] = new SearchResult(this, 311, LocaleController.getString("SendByEnter", NUM), "sendByEnterRow", LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$NM0akNEGYlYo-ZnAPY_boq0dLjY(this));
            searchResultArr[68] = new SearchResult(this, 312, LocaleController.getString("SaveToGallerySettings", NUM), "saveToGalleryRow", LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$WOvivT9gT9S9mbWbIsPI-YD8I38(this));
            String str5 = "StickersAndMasks";
            searchResultArr[69] = new SearchResult(this, 313, LocaleController.getString(str5, NUM), LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$LE1pN2xBggeckg4BXNGrk8dAfCA(this));
            searchResultArr[70] = new SearchResult(314, LocaleController.getString("SuggestStickers", NUM), "suggestRow", LocaleController.getString(str, NUM), LocaleController.getString(str5, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$guZCAugp-k8fJ3Gd84aSwGDA7co(this));
            searchResultArr[71] = new SearchResult(315, LocaleController.getString("FeaturedStickers", NUM), null, LocaleController.getString(str, NUM), LocaleController.getString(str5, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$a2LSsVzKR8HJrNBIEh5xSXFk_kY(this));
            searchResultArr[72] = new SearchResult(316, LocaleController.getString("Masks", NUM), null, LocaleController.getString(str, NUM), LocaleController.getString(str5, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$vTZmp1Lfwd9Jvvar_tEr9C2amaYY(this));
            searchResultArr[73] = new SearchResult(317, LocaleController.getString("ArchivedStickers", NUM), null, LocaleController.getString(str, NUM), LocaleController.getString(str5, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$O-sT8FF_ujolqkcXgAFR2EWMCOQ(this));
            searchResultArr[74] = new SearchResult(317, LocaleController.getString("ArchivedMasks", NUM), null, LocaleController.getString(str, NUM), LocaleController.getString(str5, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$dg765ZjLDUr492FSgTOKjOaKweE(this));
            searchResultArr[75] = new SearchResult(this, 400, LocaleController.getString("Language", NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$h743V_ShaSJwbDnqAuxpCdWSljA(this));
            String str6 = "SettingsHelp";
            searchResultArr[76] = new SearchResult(this, 401, LocaleController.getString(str6, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$ZbLluxRqKz7PU5zZomfrUBW8mDo(this.this$0));
            searchResultArr[77] = new SearchResult(this, 402, LocaleController.getString("AskAQuestion", NUM), LocaleController.getString(str6, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$8sCZE9SRgbZa275PGe2lLCWITbw(this));
            searchResultArr[78] = new SearchResult(this, 403, LocaleController.getString("TelegramFAQ", NUM), LocaleController.getString(str6, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$UKpWGlt--8lNdI9VfZj9-2dBm0k(this));
            searchResultArr[79] = new SearchResult(this, 404, LocaleController.getString("PrivacyPolicy", NUM), LocaleController.getString(str6, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$dYFPJFhuxdXwNU2N5QyWTOm1hz8(this));
            this.searchArray = searchResultArr;
            this.mContext = context;
            HashMap hashMap = new HashMap();
            int i = 0;
            while (true) {
                SearchResult[] searchResultArr2 = this.searchArray;
                if (i >= searchResultArr2.length) {
                    break;
                }
                hashMap.put(Integer.valueOf(searchResultArr2[i].guid), this.searchArray[i]);
                i++;
            }
            Set<String> stringSet = MessagesController.getGlobalMainSettings().getStringSet("settingsSearchRecent2", null);
            if (stringSet != null) {
                for (String hexToBytes : stringSet) {
                    try {
                        SerializedData serializedData = new SerializedData(Utilities.hexToBytes(hexToBytes));
                        int readInt32 = serializedData.readInt32(false);
                        int readInt322 = serializedData.readInt32(false);
                        if (readInt322 == 0) {
                            String readString = serializedData.readString(false);
                            int readInt323 = serializedData.readInt32(false);
                            String[] strArr = null;
                            if (readInt323 > 0) {
                                strArr = new String[readInt323];
                                for (int i2 = 0; i2 < readInt323; i2++) {
                                    strArr[i2] = serializedData.readString(false);
                                }
                            }
                            FaqSearchResult faqSearchResult = new FaqSearchResult(readString, strArr, serializedData.readString(false));
                            faqSearchResult.num = readInt32;
                            this.recentSearches.add(faqSearchResult);
                        } else if (readInt322 == 1) {
                            try {
                                SearchResult searchResult = (SearchResult) hashMap.get(Integer.valueOf(serializedData.readInt32(false)));
                                if (searchResult != null) {
                                    searchResult.num = readInt32;
                                    this.recentSearches.add(searchResult);
                                }
                            } catch (Exception unused) {
                            }
                        }
                    } catch (Exception unused2) {
                    }
                }
            }
            Collections.sort(this.recentSearches, new -$$Lambda$SettingsActivity$SearchAdapter$-ziwQi4oxZ4JeHh57M9tW4fyg4M(this));
        }

        public /* synthetic */ int lambda$new$80$SettingsActivity$SearchAdapter(Object obj, Object obj2) {
            int num = getNum(obj);
            int num2 = getNum(obj2);
            if (num < num2) {
                return -1;
            }
            return num > num2 ? 1 : 0;
        }

        private void loadFaqWebPage() {
            if (this.faqWebPage == null && !this.loadingFaqPage) {
                this.loadingFaqPage = true;
                TL_messages_getWebPage tL_messages_getWebPage = new TL_messages_getWebPage();
                tL_messages_getWebPage.url = LocaleController.getString("TelegramFaqUrl", NUM);
                tL_messages_getWebPage.hash = 0;
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tL_messages_getWebPage, new -$$Lambda$SettingsActivity$SearchAdapter$UTf_RnUuomiZ8paJt1JCx1Z2AJU(this));
            }
        }

        public /* synthetic */ void lambda$loadFaqWebPage$81$SettingsActivity$SearchAdapter(TLObject tLObject, TL_error tL_error) {
            if (tLObject instanceof WebPage) {
                WebPage webPage = (WebPage) tLObject;
                Page page = webPage.cached_page;
                if (page != null) {
                    int size = page.blocks.size();
                    for (int i = 0; i < size; i++) {
                        PageBlock pageBlock = (PageBlock) webPage.cached_page.blocks.get(i);
                        if (pageBlock instanceof TL_pageBlockList) {
                            String str = null;
                            if (i != 0) {
                                PageBlock pageBlock2 = (PageBlock) webPage.cached_page.blocks.get(i - 1);
                                if (pageBlock2 instanceof TL_pageBlockParagraph) {
                                    str = ArticleViewer.getPlainText(((TL_pageBlockParagraph) pageBlock2).text).toString();
                                }
                            }
                            TL_pageBlockList tL_pageBlockList = (TL_pageBlockList) pageBlock;
                            int size2 = tL_pageBlockList.items.size();
                            for (int i2 = 0; i2 < size2; i2++) {
                                PageListItem pageListItem = (PageListItem) tL_pageBlockList.items.get(i2);
                                if (pageListItem instanceof TL_pageListItemText) {
                                    TL_pageListItemText tL_pageListItemText = (TL_pageListItemText) pageListItem;
                                    String url = ArticleViewer.getUrl(tL_pageListItemText.text);
                                    String charSequence = ArticleViewer.getPlainText(tL_pageListItemText.text).toString();
                                    if (!(TextUtils.isEmpty(url) || TextUtils.isEmpty(charSequence))) {
                                        String str2 = "SettingsSearchFaq";
                                        this.faqSearchArray.add(new FaqSearchResult(charSequence, str != null ? new String[]{LocaleController.getString(str2, NUM), str} : new String[]{LocaleController.getString(str2, NUM)}, url));
                                    }
                                }
                            }
                        } else if (pageBlock instanceof TL_pageBlockAnchor) {
                            break;
                        }
                    }
                    this.faqWebPage = webPage;
                }
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
                return size + i;
            }
            if (!this.recentSearches.isEmpty()) {
                i = this.recentSearches.size() + 1;
            }
            return i;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 0;
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            boolean z = true;
            if (itemViewType == 0) {
                SettingsSearchCell settingsSearchCell = (SettingsSearchCell) viewHolder.itemView;
                boolean z2 = false;
                SearchResult searchResult;
                String[] access$4500;
                if (!this.searchWas) {
                    i--;
                    Object obj = this.recentSearches.get(i);
                    String access$4700;
                    if (obj instanceof SearchResult) {
                        searchResult = (SearchResult) obj;
                        access$4700 = searchResult.searchTitle;
                        access$4500 = searchResult.path;
                        if (i >= this.recentSearches.size() - 1) {
                            z = false;
                        }
                        settingsSearchCell.setTextAndValue(access$4700, access$4500, false, z);
                    } else if (obj instanceof FaqSearchResult) {
                        FaqSearchResult faqSearchResult = (FaqSearchResult) obj;
                        access$4700 = faqSearchResult.title;
                        access$4500 = faqSearchResult.path;
                        if (i < this.recentSearches.size() - 1) {
                            z2 = true;
                        }
                        settingsSearchCell.setTextAndValue(access$4700, access$4500, true, z2);
                    }
                } else if (i < this.searchResults.size()) {
                    int access$4400;
                    searchResult = (SearchResult) this.searchResults.get(i);
                    SearchResult searchResult2 = i > 0 ? (SearchResult) this.searchResults.get(i - 1) : null;
                    if (searchResult2 == null || searchResult2.iconResId != searchResult.iconResId) {
                        access$4400 = searchResult.iconResId;
                    } else {
                        access$4400 = 0;
                    }
                    CharSequence charSequence = (CharSequence) this.resultNames.get(i);
                    access$4500 = searchResult.path;
                    if (i >= this.searchResults.size() - 1) {
                        z = false;
                    }
                    settingsSearchCell.setTextAndValueAndIcon(charSequence, access$4500, access$4400, z);
                } else {
                    i -= this.searchResults.size() + 1;
                    CharSequence charSequence2 = (CharSequence) this.resultNames.get(this.searchResults.size() + i);
                    access$4500 = ((FaqSearchResult) this.faqSearchResults.get(i)).path;
                    if (i < this.searchResults.size() - 1) {
                        z2 = true;
                    }
                    settingsSearchCell.setTextAndValue(charSequence2, access$4500, true, z2);
                }
            } else if (itemViewType == 1) {
                ((GraySectionCell) viewHolder.itemView).setText(LocaleController.getString("SettingsFaqSearchTitle", NUM));
            } else if (itemViewType == 2) {
                ((HeaderCell) viewHolder.itemView).setText(LocaleController.getString("SettingsRecent", NUM));
            }
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View settingsSearchCell;
            if (i == 0) {
                settingsSearchCell = new SettingsSearchCell(this.mContext);
            } else if (i != 1) {
                settingsSearchCell = new HeaderCell(this.mContext, 16);
            } else {
                settingsSearchCell = new GraySectionCell(this.mContext);
            }
            settingsSearchCell.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(settingsSearchCell);
        }

        public int getItemViewType(int i) {
            if (this.searchWas) {
                if (i >= this.searchResults.size() && i == this.searchResults.size()) {
                    return 1;
                }
                return 0;
            } else if (i == 0) {
                return 2;
            } else {
                return 0;
            }
        }

        public void addRecent(Object obj) {
            int indexOf = this.recentSearches.indexOf(obj);
            if (indexOf >= 0) {
                this.recentSearches.remove(indexOf);
            }
            int i = 0;
            this.recentSearches.add(0, obj);
            if (!this.searchWas) {
                notifyDataSetChanged();
            }
            if (this.recentSearches.size() > 20) {
                ArrayList arrayList = this.recentSearches;
                arrayList.remove(arrayList.size() - 1);
            }
            LinkedHashSet linkedHashSet = new LinkedHashSet();
            indexOf = this.recentSearches.size();
            while (i < indexOf) {
                Object obj2 = this.recentSearches.get(i);
                if (obj2 instanceof SearchResult) {
                    ((SearchResult) obj2).num = i;
                } else if (obj2 instanceof FaqSearchResult) {
                    ((FaqSearchResult) obj2).num = i;
                }
                linkedHashSet.add(obj2.toString());
                i++;
            }
            MessagesController.getGlobalMainSettings().edit().putStringSet("settingsSearchRecent2", linkedHashSet).commit();
        }

        public void clearRecent() {
            this.recentSearches.clear();
            MessagesController.getGlobalMainSettings().edit().remove("settingsSearchRecent2").commit();
            notifyDataSetChanged();
        }

        private int getNum(Object obj) {
            if (obj instanceof SearchResult) {
                return ((SearchResult) obj).num;
            }
            return obj instanceof FaqSearchResult ? ((FaqSearchResult) obj).num : 0;
        }

        public void search(String str) {
            this.lastSearchString = str;
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            if (TextUtils.isEmpty(str)) {
                this.searchWas = false;
                this.searchResults.clear();
                this.faqSearchResults.clear();
                this.resultNames.clear();
                this.this$0.emptyView.setTopImage(0);
                this.this$0.emptyView.setText(LocaleController.getString("SettingsNoRecent", NUM));
                notifyDataSetChanged();
                return;
            }
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            -$$Lambda$SettingsActivity$SearchAdapter$m2x7wHx-LIygW0A40K--p_u9GHA -__lambda_settingsactivity_searchadapter_m2x7whx-liygw0a40k--p_u9gha = new -$$Lambda$SettingsActivity$SearchAdapter$m2x7wHx-LIygW0A40K--p_u9GHA(this, str);
            this.searchRunnable = -__lambda_settingsactivity_searchadapter_m2x7whx-liygw0a40k--p_u9gha;
            dispatchQueue.postRunnable(-__lambda_settingsactivity_searchadapter_m2x7whx-liygw0a40k--p_u9gha, 300);
        }

        public /* synthetic */ void lambda$search$83$SettingsActivity$SearchAdapter(String str) {
            SpannableStringBuilder spannableStringBuilder;
            String str2;
            int i;
            int indexOf;
            int i2;
            int i3;
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = new ArrayList();
            String str3 = " ";
            String[] split = str.split(str3);
            String[] strArr = new String[split.length];
            int i4 = 0;
            while (true) {
                spannableStringBuilder = null;
                if (i4 >= split.length) {
                    break;
                }
                strArr[i4] = LocaleController.getInstance().getTranslitString(split[i4]);
                if (strArr[i4].equals(split[i4])) {
                    strArr[i4] = null;
                }
                i4++;
            }
            i4 = 0;
            while (true) {
                SearchResult[] searchResultArr = this.searchArray;
                str2 = "windowBackgroundWhiteBlueText4";
                if (i4 >= searchResultArr.length) {
                    break;
                }
                String str4;
                SearchResult searchResult = searchResultArr[i4];
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(str3);
                stringBuilder.append(searchResult.searchTitle.toLowerCase());
                String stringBuilder2 = stringBuilder.toString();
                SpannableStringBuilder spannableStringBuilder2 = spannableStringBuilder;
                i = 0;
                while (i < split.length) {
                    SpannableStringBuilder spannableStringBuilder3;
                    String str5;
                    if (split[i].length() != 0) {
                        String str6 = split[i];
                        StringBuilder stringBuilder3 = new StringBuilder();
                        stringBuilder3.append(str3);
                        stringBuilder3.append(str6);
                        indexOf = stringBuilder2.indexOf(stringBuilder3.toString());
                        if (indexOf < 0 && strArr[i] != null) {
                            str6 = strArr[i];
                            stringBuilder3 = new StringBuilder();
                            stringBuilder3.append(str3);
                            stringBuilder3.append(str6);
                            indexOf = stringBuilder2.indexOf(stringBuilder3.toString());
                        }
                        if (indexOf < 0) {
                            break;
                        }
                        spannableStringBuilder3 = spannableStringBuilder2 == null ? new SpannableStringBuilder(searchResult.searchTitle) : spannableStringBuilder2;
                        str5 = stringBuilder2;
                        spannableStringBuilder3.setSpan(new ForegroundColorSpan(Theme.getColor(str2)), indexOf, str6.length() + indexOf, 33);
                    } else {
                        str5 = stringBuilder2;
                        spannableStringBuilder3 = spannableStringBuilder2;
                    }
                    if (spannableStringBuilder3 != null && i == split.length - 1) {
                        if (searchResult.guid == 502) {
                            i2 = -1;
                            for (i3 = 0; i3 < 3; i3++) {
                                if (!UserConfig.getInstance(i4).isClientActivated()) {
                                    i2 = i3;
                                    break;
                                }
                            }
                            if (i2 < 0) {
                            }
                        }
                        arrayList.add(searchResult);
                        arrayList3.add(spannableStringBuilder3);
                    }
                    i++;
                    str4 = str;
                    spannableStringBuilder2 = spannableStringBuilder3;
                    stringBuilder2 = str5;
                }
                i4++;
                str4 = str;
                spannableStringBuilder = null;
            }
            if (this.faqWebPage != null) {
                i2 = this.faqSearchArray.size();
                i3 = 0;
                while (i3 < i2) {
                    FaqSearchResult faqSearchResult = (FaqSearchResult) this.faqSearchArray.get(i3);
                    StringBuilder stringBuilder4 = new StringBuilder();
                    stringBuilder4.append(str3);
                    stringBuilder4.append(faqSearchResult.title.toLowerCase());
                    String stringBuilder5 = stringBuilder4.toString();
                    indexOf = 0;
                    Object obj = null;
                    while (indexOf < split.length) {
                        String str7;
                        int i5;
                        if (split[indexOf].length() != 0) {
                            String str8 = split[indexOf];
                            StringBuilder stringBuilder6 = new StringBuilder();
                            stringBuilder6.append(str3);
                            stringBuilder6.append(str8);
                            i = stringBuilder5.indexOf(stringBuilder6.toString());
                            if (i < 0 && strArr[indexOf] != null) {
                                str8 = strArr[indexOf];
                                stringBuilder6 = new StringBuilder();
                                stringBuilder6.append(str3);
                                stringBuilder6.append(str8);
                                i = stringBuilder5.indexOf(stringBuilder6.toString());
                            }
                            if (i < 0) {
                                break;
                            }
                            if (obj == null) {
                                str7 = str3;
                                obj = new SpannableStringBuilder(faqSearchResult.title);
                            } else {
                                str7 = str3;
                            }
                            i5 = i2;
                            obj.setSpan(new ForegroundColorSpan(Theme.getColor(str2)), i, str8.length() + i, 33);
                        } else {
                            str7 = str3;
                            i5 = i2;
                        }
                        if (obj != null && indexOf == split.length - 1) {
                            arrayList2.add(faqSearchResult);
                            arrayList3.add(obj);
                        }
                        indexOf++;
                        str3 = str7;
                        i2 = i5;
                    }
                    i3++;
                    str3 = str3;
                    i2 = i2;
                }
            }
            AndroidUtilities.runOnUIThread(new -$$Lambda$SettingsActivity$SearchAdapter$J7nIENc_bGXKkD4Bbp2hXn2w5uY(this, str, arrayList, arrayList2, arrayList3));
        }

        public /* synthetic */ void lambda$null$82$SettingsActivity$SearchAdapter(String str, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3) {
            if (str.equals(this.lastSearchString)) {
                if (!this.searchWas) {
                    this.this$0.emptyView.setTopImage(NUM);
                    this.this$0.emptyView.setText(LocaleController.getString("SettingsNoResults", NUM));
                }
                this.searchWas = true;
                this.searchResults = arrayList;
                this.faqSearchResults = arrayList2;
                this.resultNames = arrayList3;
                notifyDataSetChanged();
            }
        }
    }

    public /* synthetic */ String getInitialSearchString() {
        return -CC.$default$getInitialSearchString(this);
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.imageUpdater = new ImageUpdater();
        ImageUpdater imageUpdater = this.imageUpdater;
        imageUpdater.parentFragment = this;
        imageUpdater.delegate = this;
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
        BackupImageView backupImageView = this.avatarImage;
        if (backupImageView != null) {
            backupImageView.setImageDrawable(null);
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.userInfoDidLoad);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
        this.imageUpdater.clear();
    }

    public View createView(Context context) {
        int findFirstVisibleItemPosition;
        int top;
        Object tag;
        Context context2 = context;
        String str = "avatar_backgroundActionBarBlue";
        this.actionBar.setBackgroundColor(Theme.getColor(str));
        this.actionBar.setItemsBackgroundColor(Theme.getColor("avatar_actionBarSelectorBlue"), false);
        this.actionBar.setItemsColor(Theme.getColor("avatar_actionBarIconBlue"), false);
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAddToContainer(false);
        this.extraHeight = 88;
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    SettingsActivity.this.finishFragment();
                } else if (i == 1) {
                    SettingsActivity.this.presentFragment(new ChangeNameActivity());
                } else if (i == 2) {
                    SettingsActivity.this.presentFragment(new LogoutActivity());
                }
            }
        });
        ActionBarMenu createMenu = this.actionBar.createMenu();
        ActionBarMenuItem actionBarMenuItemSearchListener = createMenu.addItem(3, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItemSearchListener() {
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
                String str = "windowBackgroundWhite";
                SettingsActivity.this.fragmentView.setBackgroundColor(Theme.getColor(str));
                SettingsActivity.this.fragmentView.setTag(str);
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
                String str = "windowBackgroundGray";
                SettingsActivity.this.fragmentView.setBackgroundColor(Theme.getColor(str));
                SettingsActivity.this.fragmentView.setTag(str);
                SettingsActivity.this.needLayout();
            }

            public void onTextChanged(EditText editText) {
                SettingsActivity.this.searchAdapter.search(editText.getText().toString().toLowerCase());
            }
        });
        String str2 = "SearchInSettings";
        actionBarMenuItemSearchListener.setContentDescription(LocaleController.getString(str2, NUM));
        actionBarMenuItemSearchListener.setSearchFieldHint(LocaleController.getString(str2, NUM));
        this.otherItem = createMenu.addItem(0, NUM);
        this.otherItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
        this.otherItem.addSubItem(1, NUM, LocaleController.getString("EditName", NUM));
        this.otherItem.addSubItem(2, NUM, LocaleController.getString("LogOut", NUM));
        if (this.listView != null) {
            findFirstVisibleItemPosition = this.layoutManager.findFirstVisibleItemPosition();
            View findViewByPosition = this.layoutManager.findViewByPosition(findFirstVisibleItemPosition);
            if (findViewByPosition != null) {
                top = findViewByPosition.getTop();
            } else {
                findFirstVisibleItemPosition = -1;
                top = 0;
            }
            tag = this.writeButton.getTag();
        } else {
            tag = null;
            findFirstVisibleItemPosition = -1;
            top = 0;
        }
        this.listAdapter = new ListAdapter(context2);
        this.searchAdapter = new SearchAdapter(this, context2);
        this.fragmentView = new FrameLayout(context2) {
            /* Access modifiers changed, original: protected */
            public boolean drawChild(Canvas canvas, View view, long j) {
                if (view != SettingsActivity.this.listView) {
                    return super.drawChild(canvas, view, j);
                }
                boolean drawChild = super.drawChild(canvas, view, j);
                if (SettingsActivity.this.parentLayout != null) {
                    int childCount = getChildCount();
                    int i = 0;
                    int i2 = 0;
                    while (i2 < childCount) {
                        View childAt = getChildAt(i2);
                        if (childAt != view && (childAt instanceof ActionBar) && childAt.getVisibility() == 0) {
                            if (((ActionBar) childAt).getCastShadows()) {
                                i = childAt.getMeasuredHeight();
                            }
                            SettingsActivity.this.parentLayout.drawHeaderShadow(canvas, i);
                        } else {
                            i2++;
                        }
                    }
                    SettingsActivity.this.parentLayout.drawHeaderShadow(canvas, i);
                }
                return drawChild;
            }
        };
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.fragmentView.setTag("windowBackgroundGray");
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.listView = new RecyclerListView(context2);
        this.listView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView = this.listView;
        AnonymousClass5 anonymousClass5 = new LinearLayoutManager(context2, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = anonymousClass5;
        recyclerListView.setLayoutManager(anonymousClass5);
        this.listView.setGlowColor(Theme.getColor(str));
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        this.listView.setOnItemClickListener(new -$$Lambda$SettingsActivity$iv7dvdiS8exd3Bpi9ZCm9_zKqgo(this));
        this.listView.setOnItemLongClickListener(new OnItemLongClickListener() {
            private int pressCount = 0;

            public boolean onItemClick(View view, int i) {
                String str = "Cancel";
                Builder builder;
                if (SettingsActivity.this.listView.getAdapter() == SettingsActivity.this.searchAdapter) {
                    builder = new Builder(SettingsActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("AppName", NUM));
                    builder.setMessage(LocaleController.getString("ClearSearch", NUM));
                    builder.setPositiveButton(LocaleController.getString("ClearButton", NUM).toUpperCase(), new -$$Lambda$SettingsActivity$6$KjtmC6iNzDhKFgrEkHFfZeqs51k(this));
                    builder.setNegativeButton(LocaleController.getString(str, NUM), null);
                    SettingsActivity.this.showDialog(builder.create());
                    return true;
                } else if (i != SettingsActivity.this.versionRow) {
                    return false;
                } else {
                    this.pressCount++;
                    if (this.pressCount >= 2 || BuildVars.DEBUG_PRIVATE_VERSION) {
                        int i2;
                        String str2;
                        builder = new Builder(SettingsActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString("DebugMenu", NUM));
                        CharSequence[] charSequenceArr = new CharSequence[11];
                        charSequenceArr[0] = LocaleController.getString("DebugMenuImportContacts", NUM);
                        charSequenceArr[1] = LocaleController.getString("DebugMenuReloadContacts", NUM);
                        charSequenceArr[2] = LocaleController.getString("DebugMenuResetContacts", NUM);
                        charSequenceArr[3] = LocaleController.getString("DebugMenuResetDialogs", NUM);
                        if (BuildVars.LOGS_ENABLED) {
                            i2 = NUM;
                            str2 = "DebugMenuDisableLogs";
                        } else {
                            i2 = NUM;
                            str2 = "DebugMenuEnableLogs";
                        }
                        charSequenceArr[4] = LocaleController.getString(str2, i2);
                        if (SharedConfig.inappCamera) {
                            i2 = NUM;
                            str2 = "DebugMenuDisableCamera";
                        } else {
                            i2 = NUM;
                            str2 = "DebugMenuEnableCamera";
                        }
                        charSequenceArr[5] = LocaleController.getString(str2, i2);
                        charSequenceArr[6] = LocaleController.getString("DebugMenuClearMediaCache", NUM);
                        charSequenceArr[7] = LocaleController.getString("DebugMenuCallSettings", NUM);
                        charSequenceArr[8] = null;
                        charSequenceArr[9] = BuildVars.DEBUG_PRIVATE_VERSION ? "Check for app updates" : null;
                        charSequenceArr[10] = LocaleController.getString("DebugMenuReadAllDialogs", NUM);
                        builder.setItems(charSequenceArr, new -$$Lambda$SettingsActivity$6$kxL5W9nAqj10O1Y1PL9gcNop-Uc(this));
                        builder.setNegativeButton(LocaleController.getString(str, NUM), null);
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

            public /* synthetic */ void lambda$onItemClick$0$SettingsActivity$6(DialogInterface dialogInterface, int i) {
                SettingsActivity.this.searchAdapter.clearRecent();
            }

            public /* synthetic */ void lambda$onItemClick$1$SettingsActivity$6(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    UserConfig.getInstance(SettingsActivity.this.currentAccount).syncContacts = true;
                    UserConfig.getInstance(SettingsActivity.this.currentAccount).saveConfig(false);
                    ContactsController.getInstance(SettingsActivity.this.currentAccount).forceImportContacts();
                } else if (i == 1) {
                    ContactsController.getInstance(SettingsActivity.this.currentAccount).loadContacts(false, 0);
                } else if (i == 2) {
                    ContactsController.getInstance(SettingsActivity.this.currentAccount).resetImportedContacts();
                } else if (i == 3) {
                    MessagesController.getInstance(SettingsActivity.this.currentAccount).forceResetDialogs();
                } else if (i == 4) {
                    BuildVars.LOGS_ENABLED = 1 ^ BuildVars.LOGS_ENABLED;
                    ApplicationLoader.applicationContext.getSharedPreferences("systemConfig", 0).edit().putBoolean("logsEnabled", BuildVars.LOGS_ENABLED).commit();
                } else if (i == 5) {
                    SharedConfig.toggleInappCamera();
                } else if (i == 6) {
                    MessagesStorage.getInstance(SettingsActivity.this.currentAccount).clearSentMedia();
                    SharedConfig.setNoSoundHintShowed(false);
                    MessagesController.getGlobalMainSettings().edit().remove("archivehint").remove("archivehint_l").remove("gifhint").remove("soundHint").commit();
                } else if (i == 7) {
                    VoIPHelper.showCallDebugSettings(SettingsActivity.this.getParentActivity());
                } else if (i == 8) {
                    SharedConfig.toggleRoundCamera16to9();
                } else if (i == 9) {
                    ((LaunchActivity) SettingsActivity.this.getParentActivity()).checkAppUpdate(true);
                } else if (i == 10) {
                    MessagesStorage.getInstance(SettingsActivity.this.currentAccount).readAllDialogs();
                }
            }
        });
        this.emptyView = new EmptyTextProgressView(context2);
        this.emptyView.showTextView();
        this.emptyView.setTextSize(18);
        this.emptyView.setVisibility(8);
        this.emptyView.setShowAtCenter(true);
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        frameLayout.addView(this.actionBar);
        this.extraHeightView = new View(context2);
        this.extraHeightView.setPivotY(0.0f);
        this.extraHeightView.setBackgroundColor(Theme.getColor(str));
        frameLayout.addView(this.extraHeightView, LayoutHelper.createFrame(-1, 88.0f));
        this.shadowView = new View(context2);
        this.shadowView.setBackgroundResource(NUM);
        frameLayout.addView(this.shadowView, LayoutHelper.createFrame(-1, 3.0f));
        this.avatarContainer = new FrameLayout(context2);
        this.avatarContainer.setPivotX(LocaleController.isRTL ? (float) AndroidUtilities.dp(42.0f) : 0.0f);
        this.avatarContainer.setPivotY(0.0f);
        int i = 5;
        frameLayout.addView(this.avatarContainer, LayoutHelper.createFrame(42, 42.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? 0 : 64), 0.0f, (float) (LocaleController.isRTL ? 112 : 0), 0.0f));
        this.avatarContainer.setOnClickListener(new -$$Lambda$SettingsActivity$tP6tYHrZMyudHveG_joX5MC_Y2o(this));
        this.avatarImage = new BackupImageView(context2);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(21.0f));
        this.avatarImage.setContentDescription(LocaleController.getString("AccDescrProfilePicture", NUM));
        this.avatarContainer.addView(this.avatarImage, LayoutHelper.createFrame(42, 42.0f));
        final Paint paint = new Paint(1);
        paint.setColor(NUM);
        this.avatarProgressView = new RadialProgressView(context2) {
            /* Access modifiers changed, original: protected */
            public void onDraw(Canvas canvas) {
                if (SettingsActivity.this.avatarImage != null && SettingsActivity.this.avatarImage.getImageReceiver().hasNotThumb()) {
                    paint.setAlpha((int) (SettingsActivity.this.avatarImage.getImageReceiver().getCurrentAlpha() * 85.0f));
                    canvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(21.0f), paint);
                }
                super.onDraw(canvas);
            }
        };
        this.avatarProgressView.setSize(AndroidUtilities.dp(26.0f));
        this.avatarProgressView.setProgressColor(-1);
        this.avatarContainer.addView(this.avatarProgressView, LayoutHelper.createFrame(42, 42.0f));
        showAvatarProgress(false, false);
        this.nameTextView = new TextView(context2) {
            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, i2);
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
        this.onlineTextView = new TextView(context2);
        this.onlineTextView.setTextColor(Theme.getColor("avatar_subtitleInProfileBlue"));
        this.onlineTextView.setTextSize(1, 14.0f);
        this.onlineTextView.setLines(1);
        this.onlineTextView.setMaxLines(1);
        this.onlineTextView.setSingleLine(true);
        this.onlineTextView.setEllipsize(TruncateAt.END);
        this.onlineTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        frameLayout.addView(this.onlineTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 48.0f : 118.0f, 0.0f, LocaleController.isRTL ? 166.0f : 96.0f, 0.0f));
        this.writeButton = new ImageView(context2);
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("profile_actionBackground"), Theme.getColor("profile_actionPressedBackground"));
        if (VERSION.SDK_INT < 21) {
            Drawable mutate = context.getResources().getDrawable(NUM).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(-16777216, Mode.MULTIPLY));
            Drawable combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorCircleDrawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            createSimpleSelectorCircleDrawable = combinedDrawable;
        }
        this.writeButton.setBackgroundDrawable(createSimpleSelectorCircleDrawable);
        this.writeButton.setImageResource(NUM);
        this.writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("profile_actionIcon"), Mode.MULTIPLY));
        this.writeButton.setScaleType(ScaleType.CENTER);
        if (VERSION.SDK_INT >= 21) {
            StateListAnimator stateListAnimator = new StateListAnimator();
            stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.writeButton, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(this.writeButton, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            this.writeButton.setStateListAnimator(stateListAnimator);
            this.writeButton.setOutlineProvider(new ViewOutlineProvider() {
                @SuppressLint({"NewApi"})
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        ImageView imageView = this.writeButton;
        int i2 = VERSION.SDK_INT >= 21 ? 56 : 60;
        float f = VERSION.SDK_INT >= 21 ? 56.0f : 60.0f;
        if (LocaleController.isRTL) {
            i = 3;
        }
        frameLayout.addView(imageView, LayoutHelper.createFrame(i2, f, i | 48, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 16.0f, 0.0f));
        this.writeButton.setOnClickListener(new -$$Lambda$SettingsActivity$r8NmpkmDLMDut28p1uEJ0PAPo-c(this));
        this.writeButton.setContentDescription(LocaleController.getString("AccDescrChangeProfilePicture", NUM));
        if (findFirstVisibleItemPosition != -1) {
            this.layoutManager.scrollToPositionWithOffset(findFirstVisibleItemPosition, top);
            if (tag != null) {
                this.writeButton.setTag(Integer.valueOf(0));
                this.writeButton.setScaleX(0.2f);
                this.writeButton.setScaleY(0.2f);
                this.writeButton.setAlpha(0.0f);
                this.writeButton.setVisibility(8);
            }
        }
        needLayout();
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1 && SettingsActivity.this.listView.getAdapter() == SettingsActivity.this.searchAdapter) {
                    AndroidUtilities.hideKeyboard(SettingsActivity.this.getParentActivity().getCurrentFocus());
                }
            }

            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                if (SettingsActivity.this.layoutManager.getItemCount() != 0) {
                    i = 0;
                    View childAt = recyclerView.getChildAt(0);
                    if (childAt != null && SettingsActivity.this.avatarContainer.getVisibility() == 0) {
                        if (SettingsActivity.this.layoutManager.findFirstVisibleItemPosition() == 0) {
                            i2 = AndroidUtilities.dp(88.0f);
                            if (childAt.getTop() < 0) {
                                i = childAt.getTop();
                            }
                            i += i2;
                        }
                        if (SettingsActivity.this.extraHeight != i) {
                            SettingsActivity.this.extraHeight = i;
                            SettingsActivity.this.needLayout();
                        }
                    }
                }
            }
        });
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$0$SettingsActivity(View view, int i) {
        if (this.listView.getAdapter() == this.listAdapter) {
            if (i == this.notificationRow) {
                presentFragment(new NotificationsSettingsActivity());
            } else if (i == this.privacyRow) {
                presentFragment(new PrivacySettingsActivity());
            } else if (i == this.dataRow) {
                presentFragment(new DataSettingsActivity());
            } else if (i == this.chatRow) {
                presentFragment(new ThemeActivity(0));
            } else if (i == this.helpRow) {
                showHelpAlert();
            } else if (i == this.languageRow) {
                presentFragment(new LanguageSelectActivity());
            } else if (i == this.usernameRow) {
                presentFragment(new ChangeUsernameActivity());
            } else if (i == this.bioRow) {
                if (this.userInfo != null) {
                    presentFragment(new ChangeBioActivity());
                }
            } else if (i == this.numberRow) {
                presentFragment(new ChangePhoneHelpActivity());
            }
        } else if (i >= 0) {
            Object valueOf = Integer.valueOf(this.numberRow);
            if (!this.searchAdapter.searchWas) {
                i--;
                if (i >= 0) {
                    if (i < this.searchAdapter.recentSearches.size()) {
                        valueOf = this.searchAdapter.recentSearches.get(i);
                    }
                } else {
                    return;
                }
            } else if (i < this.searchAdapter.searchResults.size()) {
                valueOf = this.searchAdapter.searchResults.get(i);
            } else {
                i -= this.searchAdapter.searchResults.size() + 1;
                if (i >= 0 && i < this.searchAdapter.faqSearchResults.size()) {
                    valueOf = this.searchAdapter.faqSearchResults.get(i);
                }
            }
            if (valueOf instanceof SearchResult) {
                ((SearchResult) valueOf).open();
            } else if (valueOf instanceof FaqSearchResult) {
                FaqSearchResult faqSearchResult = (FaqSearchResult) valueOf;
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.openArticle, this.searchAdapter.faqWebPage, faqSearchResult.url);
            }
            if (valueOf != null) {
                this.searchAdapter.addRecent(valueOf);
            }
        }
    }

    public /* synthetic */ void lambda$createView$1$SettingsActivity(View view) {
        if (this.avatar == null) {
            User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
            if (user != null) {
                UserProfilePhoto userProfilePhoto = user.photo;
                if (!(userProfilePhoto == null || userProfilePhoto.photo_big == null)) {
                    PhotoViewer.getInstance().setParentActivity(getParentActivity());
                    userProfilePhoto = user.photo;
                    int i = userProfilePhoto.dc_id;
                    if (i != 0) {
                        userProfilePhoto.photo_big.dc_id = i;
                    }
                    PhotoViewer.getInstance().openPhoto(user.photo.photo_big, this.provider);
                }
            }
        }
    }

    public /* synthetic */ void lambda$createView$3$SettingsActivity(View view) {
        User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
        if (user == null) {
            user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
        }
        if (user != null) {
            ImageUpdater imageUpdater = this.imageUpdater;
            UserProfilePhoto userProfilePhoto = user.photo;
            boolean z = (userProfilePhoto == null || userProfilePhoto.photo_big == null || (userProfilePhoto instanceof TL_userProfilePhotoEmpty)) ? false : true;
            imageUpdater.openMenu(z, new -$$Lambda$SettingsActivity$Y3os7qOdvyHRbMkK3Q2dvtuvdp0(this));
        }
    }

    public /* synthetic */ void lambda$null$2$SettingsActivity() {
        MessagesController.getInstance(this.currentAccount).deleteUserPhoto(null);
    }

    public void didUploadPhoto(InputFile inputFile, PhotoSize photoSize, PhotoSize photoSize2) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$SettingsActivity$NCmSP4zZF-AHhm4jiT4j3ELozqs(this, inputFile, photoSize2, photoSize));
    }

    public /* synthetic */ void lambda$didUploadPhoto$6$SettingsActivity(InputFile inputFile, PhotoSize photoSize, PhotoSize photoSize2) {
        if (inputFile != null) {
            TL_photos_uploadProfilePhoto tL_photos_uploadProfilePhoto = new TL_photos_uploadProfilePhoto();
            tL_photos_uploadProfilePhoto.file = inputFile;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_photos_uploadProfilePhoto, new -$$Lambda$SettingsActivity$xEUCjiAh7lu_htPiO_5d1rnOSOk(this));
            return;
        }
        this.avatar = photoSize.location;
        this.avatarBig = photoSize2.location;
        this.avatarImage.setImage(ImageLocation.getForLocal(this.avatar), "50_50", this.avatarDrawable, null);
        showAvatarProgress(true, false);
    }

    public /* synthetic */ void lambda$null$5$SettingsActivity(TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
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
            TL_photos_photo tL_photos_photo = (TL_photos_photo) tLObject;
            ArrayList arrayList = tL_photos_photo.photo.sizes;
            PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(arrayList, 150);
            PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(arrayList, 800);
            user.photo = new TL_userProfilePhoto();
            UserProfilePhoto userProfilePhoto = user.photo;
            userProfilePhoto.photo_id = tL_photos_photo.photo.id;
            if (closestPhotoSizeWithSize != null) {
                userProfilePhoto.photo_small = closestPhotoSizeWithSize.location;
            }
            if (closestPhotoSizeWithSize2 != null) {
                user.photo.photo_big = closestPhotoSizeWithSize2.location;
            } else if (closestPhotoSizeWithSize != null) {
                user.photo.photo_small = closestPhotoSizeWithSize.location;
            }
            if (tL_photos_photo != null) {
                if (!(closestPhotoSizeWithSize == null || this.avatar == null)) {
                    FileLoader.getPathToAttach(this.avatar, true).renameTo(FileLoader.getPathToAttach(closestPhotoSizeWithSize, true));
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(this.avatar.volume_id);
                    String str = "_";
                    stringBuilder.append(str);
                    stringBuilder.append(this.avatar.local_id);
                    String str2 = "@50_50";
                    stringBuilder.append(str2);
                    String stringBuilder2 = stringBuilder.toString();
                    StringBuilder stringBuilder3 = new StringBuilder();
                    stringBuilder3.append(closestPhotoSizeWithSize.location.volume_id);
                    stringBuilder3.append(str);
                    stringBuilder3.append(closestPhotoSizeWithSize.location.local_id);
                    stringBuilder3.append(str2);
                    ImageLoader.getInstance().replaceImageInCache(stringBuilder2, stringBuilder3.toString(), ImageLocation.getForUser(user, false), true);
                }
                if (!(closestPhotoSizeWithSize2 == null || this.avatarBig == null)) {
                    FileLoader.getPathToAttach(this.avatarBig, true).renameTo(FileLoader.getPathToAttach(closestPhotoSizeWithSize2, true));
                }
            }
            MessagesStorage.getInstance(this.currentAccount).clearUserPhotos(user.id);
            ArrayList arrayList2 = new ArrayList();
            arrayList2.add(user);
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(arrayList2, null, false, true);
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$SettingsActivity$YjutmGJ_Hyc5u6zgslQKrIDUgoo(this));
    }

    public /* synthetic */ void lambda$null$4$SettingsActivity() {
        this.avatar = null;
        this.avatarBig = null;
        updateUserData();
        showAvatarProgress(false, true);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(1535));
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
        UserConfig.getInstance(this.currentAccount).saveConfig(true);
    }

    private void showAvatarProgress(final boolean z, boolean z2) {
        if (this.avatarProgressView != null) {
            AnimatorSet animatorSet = this.avatarAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.avatarAnimation = null;
            }
            if (z2) {
                this.avatarAnimation = new AnimatorSet();
                Animator[] animatorArr;
                if (z) {
                    this.avatarProgressView.setVisibility(0);
                    AnimatorSet animatorSet2 = this.avatarAnimation;
                    animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{1.0f});
                    animatorSet2.playTogether(animatorArr);
                } else {
                    animatorSet = this.avatarAnimation;
                    animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{0.0f});
                    animatorSet.playTogether(animatorArr);
                }
                this.avatarAnimation.setDuration(180);
                this.avatarAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (SettingsActivity.this.avatarAnimation != null && SettingsActivity.this.avatarProgressView != null) {
                            if (!z) {
                                SettingsActivity.this.avatarProgressView.setVisibility(4);
                            }
                            SettingsActivity.this.avatarAnimation = null;
                        }
                    }

                    public void onAnimationCancel(Animator animator) {
                        SettingsActivity.this.avatarAnimation = null;
                    }
                });
                this.avatarAnimation.start();
            } else if (z) {
                this.avatarProgressView.setAlpha(1.0f);
                this.avatarProgressView.setVisibility(0);
            } else {
                this.avatarProgressView.setAlpha(0.0f);
                this.avatarProgressView.setVisibility(4);
            }
        }
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        this.imageUpdater.onActivityResult(i, i2, intent);
    }

    public void saveSelfArgs(Bundle bundle) {
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            String str = imageUpdater.currentPicturePath;
            if (str != null) {
                bundle.putString("path", str);
            }
        }
    }

    public void restoreSelfArgs(Bundle bundle) {
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.currentPicturePath = bundle.getString("path");
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.updateInterfaces) {
            i = ((Integer) objArr[0]).intValue();
            if ((i & 2) != 0 || (i & 1) != 0) {
                updateUserData();
            }
        } else if (i == NotificationCenter.userInfoDidLoad) {
            if (((Integer) objArr[0]).intValue() == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                ListAdapter listAdapter = this.listAdapter;
                if (listAdapter != null) {
                    this.userInfo = (UserFull) objArr[1];
                    listAdapter.notifyItemChanged(this.bioRow);
                }
            }
        } else if (i == NotificationCenter.emojiDidLoad) {
            RecyclerListView recyclerListView = this.listView;
            if (recyclerListView != null) {
                recyclerListView.invalidateViews();
            }
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        updateUserData();
        fixLayout();
        setParentActivityTitle(LocaleController.getString("Settings", NUM));
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        fixLayout();
    }

    private void needLayout() {
        int i = 0;
        int currentActionBarHeight = (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) recyclerListView.getLayoutParams();
            if (layoutParams.topMargin != currentActionBarHeight) {
                layoutParams.topMargin = currentActionBarHeight;
                this.listView.setLayoutParams(layoutParams);
                this.extraHeightView.setTranslationY((float) currentActionBarHeight);
            }
            layoutParams = (FrameLayout.LayoutParams) this.emptyView.getLayoutParams();
            if (layoutParams.topMargin != currentActionBarHeight) {
                layoutParams.topMargin = currentActionBarHeight;
                this.emptyView.setLayoutParams(layoutParams);
            }
        }
        FrameLayout frameLayout = this.avatarContainer;
        if (frameLayout != null) {
            int i2 = frameLayout.getVisibility() == 0 ? this.extraHeight : 0;
            float dp = ((float) i2) / ((float) AndroidUtilities.dp(88.0f));
            this.extraHeightView.setScaleY(dp);
            this.shadowView.setTranslationY((float) (currentActionBarHeight + i2));
            this.writeButton.setTranslationY((float) ((((this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight()) + i2) - AndroidUtilities.dp(29.5f)));
            final boolean z = dp > 0.2f;
            if (z != (this.writeButton.getTag() == null)) {
                if (z) {
                    this.writeButton.setTag(null);
                    this.writeButton.setVisibility(0);
                } else {
                    this.writeButton.setTag(Integer.valueOf(0));
                }
                AnimatorSet animatorSet = this.writeButtonAnimation;
                if (animatorSet != null) {
                    this.writeButtonAnimation = null;
                    animatorSet.cancel();
                }
                this.writeButtonAnimation = new AnimatorSet();
                String str = "alpha";
                String str2 = "scaleY";
                String str3 = "scaleX";
                Animator[] animatorArr;
                if (z) {
                    this.writeButtonAnimation.setInterpolator(new DecelerateInterpolator());
                    AnimatorSet animatorSet2 = this.writeButtonAnimation;
                    animatorArr = new Animator[3];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.writeButton, str3, new float[]{1.0f});
                    animatorArr[1] = ObjectAnimator.ofFloat(this.writeButton, str2, new float[]{1.0f});
                    animatorArr[2] = ObjectAnimator.ofFloat(this.writeButton, str, new float[]{1.0f});
                    animatorSet2.playTogether(animatorArr);
                } else {
                    this.writeButtonAnimation.setInterpolator(new AccelerateInterpolator());
                    AnimatorSet animatorSet3 = this.writeButtonAnimation;
                    animatorArr = new Animator[3];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.writeButton, str3, new float[]{0.2f});
                    animatorArr[1] = ObjectAnimator.ofFloat(this.writeButton, str2, new float[]{0.2f});
                    animatorArr[2] = ObjectAnimator.ofFloat(this.writeButton, str, new float[]{0.0f});
                    animatorSet3.playTogether(animatorArr);
                }
                this.writeButtonAnimation.setDuration(150);
                this.writeButtonAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (SettingsActivity.this.writeButtonAnimation != null && SettingsActivity.this.writeButtonAnimation.equals(animator)) {
                            SettingsActivity.this.writeButton.setVisibility(z ? 0 : 8);
                            SettingsActivity.this.writeButtonAnimation = null;
                        }
                    }
                });
                this.writeButtonAnimation.start();
            }
            float f = ((18.0f * dp) + 42.0f) / 42.0f;
            this.avatarContainer.setScaleX(f);
            this.avatarContainer.setScaleY(f);
            this.avatarProgressView.setSize(AndroidUtilities.dp(26.0f / this.avatarContainer.getScaleX()));
            this.avatarProgressView.setStrokeWidth(3.0f / this.avatarContainer.getScaleX());
            if (this.actionBar.getOccupyStatusBar()) {
                i = AndroidUtilities.statusBarHeight;
            }
            float currentActionBarHeight2 = ((float) i) + ((((float) ActionBar.getCurrentActionBarHeight()) / 2.0f) * (dp + 1.0f));
            f = AndroidUtilities.density;
            double d = (double) ((currentActionBarHeight2 - (21.0f * f)) + ((f * 27.0f) * dp));
            this.avatarContainer.setTranslationY((float) Math.ceil(d));
            this.nameTextView.setTranslationY((((float) Math.floor(d)) - ((float) Math.ceil((double) AndroidUtilities.density))) + ((float) Math.floor((double) ((AndroidUtilities.density * 7.0f) * dp))));
            this.onlineTextView.setTranslationY((((float) Math.floor(d)) + ((float) AndroidUtilities.dp(22.0f))) + (((float) Math.floor((double) (AndroidUtilities.density * 11.0f))) * dp));
            float f2 = (0.12f * dp) + 1.0f;
            this.nameTextView.setScaleX(f2);
            this.nameTextView.setScaleY(f2);
            if (LocaleController.isRTL) {
                this.avatarContainer.setTranslationX(((float) AndroidUtilities.dp(95.0f)) * dp);
                this.nameTextView.setTranslationX((AndroidUtilities.density * 69.0f) * dp);
                this.onlineTextView.setTranslationX((AndroidUtilities.density * 69.0f) * dp);
                return;
            }
            this.avatarContainer.setTranslationX(((float) (-AndroidUtilities.dp(47.0f))) * dp);
            this.nameTextView.setTranslationX((AndroidUtilities.density * -21.0f) * dp);
            this.onlineTextView.setTranslationX((AndroidUtilities.density * -21.0f) * dp);
        }
    }

    private void fixLayout() {
        View view = this.fragmentView;
        if (view != null) {
            view.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
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
        Object user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
        if (user != null) {
            FileLocation fileLocation = null;
            UserProfilePhoto userProfilePhoto = user.photo;
            if (userProfilePhoto != null) {
                fileLocation = userProfilePhoto.photo_big;
            }
            this.avatarDrawable = new AvatarDrawable((User) user, true);
            this.avatarDrawable.setColor(Theme.getColor("avatar_backgroundInProfileBlue"));
            BackupImageView backupImageView = this.avatarImage;
            if (backupImageView != null) {
                backupImageView.setImage(ImageLocation.getForUser(user, false), "50_50", this.avatarDrawable, user);
                this.avatarImage.getImageReceiver().setVisible(PhotoViewer.isShowingImage(fileLocation) ^ 1, false);
                this.nameTextView.setText(UserObject.getUserName(user));
                this.onlineTextView.setText(LocaleController.getString("Online", NUM));
                this.avatarImage.getImageReceiver().setVisible(PhotoViewer.isShowingImage(fileLocation) ^ 1, false);
            }
        }
    }

    private void showHelpAlert() {
        if (getParentActivity() != null) {
            Context parentActivity = getParentActivity();
            BottomSheet.Builder builder = new BottomSheet.Builder(parentActivity);
            builder.setApplyTopPadding(false);
            LinearLayout linearLayout = new LinearLayout(parentActivity);
            linearLayout.setOrientation(1);
            HeaderCell headerCell = new HeaderCell(parentActivity, true, 23, 15, false);
            headerCell.setHeight(47);
            headerCell.setText(LocaleController.getString("SettingsHelp", NUM));
            linearLayout.addView(headerCell);
            LinearLayout linearLayout2 = new LinearLayout(parentActivity);
            linearLayout2.setOrientation(1);
            linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2));
            int i = 0;
            while (i < 6) {
                if ((i < 3 || i > 4 || BuildVars.LOGS_ENABLED) && (i != 5 || BuildVars.DEBUG_VERSION)) {
                    TextCell textCell = new TextCell(parentActivity);
                    String string = i != 0 ? i != 1 ? i != 2 ? i != 3 ? i != 4 ? "Switch Backend" : LocaleController.getString("DebugClearLogs", NUM) : LocaleController.getString("DebugSendLogs", NUM) : LocaleController.getString("PrivacyPolicy", NUM) : LocaleController.getString("TelegramFAQ", NUM) : LocaleController.getString("AskAQuestion", NUM);
                    boolean z = BuildVars.LOGS_ENABLED || BuildVars.DEBUG_VERSION ? i == 5 : i == 2;
                    textCell.setText(string, z);
                    textCell.setTag(Integer.valueOf(i));
                    textCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                    linearLayout2.addView(textCell, LayoutHelper.createLinear(-1, -2));
                    textCell.setOnClickListener(new -$$Lambda$SettingsActivity$z3K3WuiCS2o_E03FhC5NesCF_7c(this, builder));
                }
                i++;
            }
            builder.setCustomView(linearLayout);
            showDialog(builder.create());
        }
    }

    public /* synthetic */ void lambda$showHelpAlert$8$SettingsActivity(BottomSheet.Builder builder, View view) {
        int intValue = ((Integer) view.getTag()).intValue();
        if (intValue == 0) {
            showDialog(AlertsCreator.createSupportAlert(this));
        } else if (intValue == 1) {
            Browser.openUrl(getParentActivity(), LocaleController.getString("TelegramFaqUrl", NUM));
        } else if (intValue == 2) {
            Browser.openUrl(getParentActivity(), LocaleController.getString("PrivacyPolicyUrl", NUM));
        } else if (intValue == 3) {
            sendLogs();
        } else if (intValue == 4) {
            FileLog.cleanupLogs();
        } else if (intValue == 5) {
            if (getParentActivity() != null) {
                Builder builder2 = new Builder(getParentActivity());
                builder2.setMessage(LocaleController.getString("AreYouSure", NUM));
                builder2.setTitle(LocaleController.getString("AppName", NUM));
                builder2.setPositiveButton(LocaleController.getString("OK", NUM), new -$$Lambda$SettingsActivity$ZCLGwEQbjP-a8nps5mf6ogxDy10(this));
                builder2.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                showDialog(builder2.create());
            } else {
                return;
            }
        }
        builder.getDismissRunnable().run();
    }

    public /* synthetic */ void lambda$null$7$SettingsActivity(DialogInterface dialogInterface, int i) {
        SharedConfig.pushAuthKey = null;
        SharedConfig.pushAuthKeyId = null;
        SharedConfig.saveConfig();
        ConnectionsManager.getInstance(this.currentAccount).switchBackend();
    }

    private void sendLogs() {
        if (getParentActivity() != null) {
            AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
            alertDialog.setCanCacnel(false);
            alertDialog.show();
            Utilities.globalQueue.postRunnable(new -$$Lambda$SettingsActivity$fDRhZsppR-aQC0aOePrWJpUZ93Y(this, alertDialog));
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:38:0x009b A:{SYNTHETIC, Splitter:B:38:0x009b} */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00a0 A:{Catch:{ Exception -> 0x00b6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x009b A:{SYNTHETIC, Splitter:B:38:0x009b} */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00a0 A:{Catch:{ Exception -> 0x00b6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00ad A:{Catch:{ Exception -> 0x00b6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x00b2 A:{Catch:{ Exception -> 0x00b6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00ad A:{Catch:{ Exception -> 0x00b6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x00b2 A:{Catch:{ Exception -> 0x00b6 }} */
    public /* synthetic */ void lambda$sendLogs$10$SettingsActivity(org.telegram.ui.ActionBar.AlertDialog r13) {
        /*
        r12 = this;
        r0 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x00b6 }
        r1 = 0;
        r0 = r0.getExternalFilesDir(r1);	 Catch:{ Exception -> 0x00b6 }
        r2 = new java.io.File;	 Catch:{ Exception -> 0x00b6 }
        r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00b6 }
        r3.<init>();	 Catch:{ Exception -> 0x00b6 }
        r0 = r0.getAbsolutePath();	 Catch:{ Exception -> 0x00b6 }
        r3.append(r0);	 Catch:{ Exception -> 0x00b6 }
        r0 = "/logs";
        r3.append(r0);	 Catch:{ Exception -> 0x00b6 }
        r0 = r3.toString();	 Catch:{ Exception -> 0x00b6 }
        r2.<init>(r0);	 Catch:{ Exception -> 0x00b6 }
        r0 = new java.io.File;	 Catch:{ Exception -> 0x00b6 }
        r3 = "logs.zip";
        r0.<init>(r2, r3);	 Catch:{ Exception -> 0x00b6 }
        r3 = r0.exists();	 Catch:{ Exception -> 0x00b6 }
        if (r3 == 0) goto L_0x0031;
    L_0x002e:
        r0.delete();	 Catch:{ Exception -> 0x00b6 }
    L_0x0031:
        r2 = r2.listFiles();	 Catch:{ Exception -> 0x00b6 }
        r3 = 1;
        r4 = new boolean[r3];	 Catch:{ Exception -> 0x00b6 }
        r5 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x0094, all -> 0x0091 }
        r5.<init>(r0);	 Catch:{ Exception -> 0x0094, all -> 0x0091 }
        r6 = new java.util.zip.ZipOutputStream;	 Catch:{ Exception -> 0x0094, all -> 0x0091 }
        r7 = new java.io.BufferedOutputStream;	 Catch:{ Exception -> 0x0094, all -> 0x0091 }
        r7.<init>(r5);	 Catch:{ Exception -> 0x0094, all -> 0x0091 }
        r6.<init>(r7);	 Catch:{ Exception -> 0x0094, all -> 0x0091 }
        r5 = 65536; // 0x10000 float:9.18355E-41 double:3.2379E-319;
        r5 = new byte[r5];	 Catch:{ Exception -> 0x008f }
        r7 = 0;
        r8 = 0;
    L_0x004d:
        r9 = r2.length;	 Catch:{ Exception -> 0x008f }
        if (r8 >= r9) goto L_0x0084;
    L_0x0050:
        r9 = new java.io.FileInputStream;	 Catch:{ Exception -> 0x008f }
        r10 = r2[r8];	 Catch:{ Exception -> 0x008f }
        r9.<init>(r10);	 Catch:{ Exception -> 0x008f }
        r10 = new java.io.BufferedInputStream;	 Catch:{ Exception -> 0x008f }
        r11 = r5.length;	 Catch:{ Exception -> 0x008f }
        r10.<init>(r9, r11);	 Catch:{ Exception -> 0x008f }
        r9 = new java.util.zip.ZipEntry;	 Catch:{ Exception -> 0x0080, all -> 0x007d }
        r11 = r2[r8];	 Catch:{ Exception -> 0x0080, all -> 0x007d }
        r11 = r11.getName();	 Catch:{ Exception -> 0x0080, all -> 0x007d }
        r9.<init>(r11);	 Catch:{ Exception -> 0x0080, all -> 0x007d }
        r6.putNextEntry(r9);	 Catch:{ Exception -> 0x0080, all -> 0x007d }
    L_0x006b:
        r9 = r5.length;	 Catch:{ Exception -> 0x0080, all -> 0x007d }
        r9 = r10.read(r5, r7, r9);	 Catch:{ Exception -> 0x0080, all -> 0x007d }
        r11 = -1;
        if (r9 == r11) goto L_0x0077;
    L_0x0073:
        r6.write(r5, r7, r9);	 Catch:{ Exception -> 0x0080, all -> 0x007d }
        goto L_0x006b;
    L_0x0077:
        r10.close();	 Catch:{ Exception -> 0x0080, all -> 0x007d }
        r8 = r8 + 1;
        goto L_0x004d;
    L_0x007d:
        r13 = move-exception;
        r1 = r10;
        goto L_0x00ab;
    L_0x0080:
        r1 = move-exception;
        r2 = r1;
        r1 = r10;
        goto L_0x0096;
    L_0x0084:
        r4[r7] = r3;	 Catch:{ Exception -> 0x008f }
        if (r1 == 0) goto L_0x008b;
    L_0x0088:
        r1.close();	 Catch:{ Exception -> 0x00b6 }
    L_0x008b:
        r6.close();	 Catch:{ Exception -> 0x00b6 }
        goto L_0x00a1;
    L_0x008f:
        r2 = move-exception;
        goto L_0x0096;
    L_0x0091:
        r13 = move-exception;
        r6 = r1;
        goto L_0x00ab;
    L_0x0094:
        r2 = move-exception;
        r6 = r1;
    L_0x0096:
        r2.printStackTrace();	 Catch:{ all -> 0x00aa }
        if (r1 == 0) goto L_0x009e;
    L_0x009b:
        r1.close();	 Catch:{ Exception -> 0x00b6 }
    L_0x009e:
        if (r6 == 0) goto L_0x00a1;
    L_0x00a0:
        goto L_0x008b;
    L_0x00a1:
        r1 = new org.telegram.ui.-$$Lambda$SettingsActivity$r-lxrrATAqv_MWVPXrY6vrA05SQ;	 Catch:{ Exception -> 0x00b6 }
        r1.<init>(r12, r13, r4, r0);	 Catch:{ Exception -> 0x00b6 }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r1);	 Catch:{ Exception -> 0x00b6 }
        goto L_0x00ba;
    L_0x00aa:
        r13 = move-exception;
    L_0x00ab:
        if (r1 == 0) goto L_0x00b0;
    L_0x00ad:
        r1.close();	 Catch:{ Exception -> 0x00b6 }
    L_0x00b0:
        if (r6 == 0) goto L_0x00b5;
    L_0x00b2:
        r6.close();	 Catch:{ Exception -> 0x00b6 }
    L_0x00b5:
        throw r13;	 Catch:{ Exception -> 0x00b6 }
    L_0x00b6:
        r13 = move-exception;
        r13.printStackTrace();
    L_0x00ba:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.SettingsActivity.lambda$sendLogs$10$SettingsActivity(org.telegram.ui.ActionBar.AlertDialog):void");
    }

    public /* synthetic */ void lambda$null$9$SettingsActivity(AlertDialog alertDialog, boolean[] zArr, File file) {
        try {
            alertDialog.dismiss();
        } catch (Exception unused) {
        }
        if (zArr[0]) {
            Parcelable uriForFile;
            if (VERSION.SDK_INT >= 24) {
                uriForFile = FileProvider.getUriForFile(getParentActivity(), "org.telegram.messenger.provider", file);
            } else {
                uriForFile = Uri.fromFile(file);
            }
            Intent intent = new Intent("android.intent.action.SEND");
            if (VERSION.SDK_INT >= 24) {
                intent.addFlags(1);
            }
            intent.setType("message/rfCLASSNAME");
            intent.putExtra("android.intent.extra.EMAIL", "");
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Logs from ");
            stringBuilder.append(LocaleController.getInstance().formatterStats.format(System.currentTimeMillis()));
            intent.putExtra("android.intent.extra.SUBJECT", stringBuilder.toString());
            intent.putExtra("android.intent.extra.STREAM", uriForFile);
            getParentActivity().startActivityForResult(Intent.createChooser(intent, "Select email application."), 500);
            return;
        }
        Toast.makeText(getParentActivity(), LocaleController.getString("ErrorOccurred", NUM), 0).show();
    }

    public ThemeDescription[] getThemeDescriptions() {
        r1 = new ThemeDescription[34];
        r1[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{EmptyCell.class, HeaderCell.class, TextDetailCell.class, TextCell.class}, null, null, null, "windowBackgroundWhite");
        r1[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundGray");
        r1[2] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundWhite");
        r1[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "avatar_backgroundActionBarBlue");
        r1[4] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "avatar_backgroundActionBarBlue");
        r1[5] = new ThemeDescription(this.extraHeightView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "avatar_backgroundActionBarBlue");
        r1[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "avatar_actionBarIconBlue");
        r1[7] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        r1[8] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "avatar_actionBarSelectorBlue");
        r1[9] = new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "profile_title");
        r1[10] = new ThemeDescription(this.onlineTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "avatar_subtitleInProfileBlue");
        r1[11] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, "actionBarDefaultSubmenuBackground");
        r1[12] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, "actionBarDefaultSubmenuItem");
        r1[13] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        r1[14] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        r1[15] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        View view = this.listView;
        Class[] clsArr = new Class[]{TextCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        r1[16] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteBlackText");
        view = this.listView;
        clsArr = new Class[]{TextCell.class};
        strArr = new String[1];
        strArr[0] = "valueTextView";
        r1[17] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteValueText");
        view = this.listView;
        clsArr = new Class[]{TextCell.class};
        strArr = new String[1];
        strArr[0] = "imageView";
        r1[18] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteGrayIcon");
        r1[19] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueHeader");
        r1[20] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r1[21] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        view = this.listView;
        View view2 = view;
        r1[22] = new ThemeDescription(view2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        r1[23] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText3");
        r1[24] = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[]{Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, "avatar_text");
        r1[25] = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[]{this.avatarDrawable}, null, "avatar_backgroundInProfileBlue");
        r1[26] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "profile_actionIcon");
        r1[27] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "profile_actionBackground");
        r1[28] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "profile_actionPressedBackground");
        r1[29] = new ThemeDescription(this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, null, null, null, "key_graySectionText");
        r1[30] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, "graySection");
        r1[31] = new ThemeDescription(this.listView, 0, new Class[]{SettingsSearchCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r1[32] = new ThemeDescription(this.listView, 0, new Class[]{SettingsSearchCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        r1[33] = new ThemeDescription(this.listView, 0, new Class[]{SettingsSearchCell.class}, new String[]{"imageView"}, null, null, null, "windowBackgroundWhiteGrayIcon");
        return r1;
    }
}
