package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
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
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Keep;
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
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
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
import org.telegram.ui.ActionBar.SimpleTextView;
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
import org.telegram.ui.Components.CubicBezierInterpolator;
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
    private boolean allowProfileAnimation = true;
    private float animationProgress;
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
    private String currentBio;
    private int dataRow;
    private int devicesRow;
    private int emptyRow;
    private EmptyTextProgressView emptyView;
    private int extraHeight;
    private int helpRow;
    private ImageUpdater imageUpdater;
    private int initialAnimationExtraHeight;
    private int languageRow;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private TextView nameTextView;
    private int notificationRow;
    private int numberRow;
    private int numberSectionRow;
    private TextView onlineTextView;
    private boolean openAnimationInProgress;
    private ActionBarMenuItem otherItem;
    private boolean playProfileAnimation;
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
    private ActionBarMenuItem searchItem;
    private RecyclerListView searchListView;
    private boolean searchMode;
    private int searchTransitionOffset;
    private float searchTransitionProgress;
    private Animator searchViewTransition;
    private int settingsSectionRow;
    private int settingsSectionRow2;
    private SimpleTextView titleTextView;
    private TopView topView;
    private final Interpolator transitionInterpolator = new DecelerateInterpolator();
    private UserFull userInfo;
    private int usernameRow;
    private int versionRow;
    private ImageView writeButton;
    private AnimatorSet writeButtonAnimation;

    private class TopView extends View {
        private int currentColor;
        private Paint paint = new Paint();

        public TopView(Context context) {
            super(context);
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            setMeasuredDimension(MeasureSpec.getSize(i), (ActionBar.getCurrentActionBarHeight() + (SettingsActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0)) + AndroidUtilities.dp(91.0f));
        }

        public void setBackgroundColor(int i) {
            if (i != this.currentColor) {
                this.paint.setColor(i);
                invalidate();
            }
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            int measuredHeight = getMeasuredHeight() - AndroidUtilities.dp(91.0f);
            canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) ((SettingsActivity.this.extraHeight + measuredHeight) + SettingsActivity.this.searchTransitionOffset), this.paint);
            if (SettingsActivity.this.parentLayout != null) {
                SettingsActivity.this.parentLayout.drawHeaderShadow(canvas, (measuredHeight + SettingsActivity.this.extraHeight) + SettingsActivity.this.searchTransitionOffset);
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

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
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
                } else if (i == SettingsActivity.this.devicesRow) {
                    textCell.setTextAndIcon(LocaleController.getString("Devices", NUM), NUM, true);
                }
            } else if (itemViewType != 4) {
                if (itemViewType == 6) {
                    TextDetailCell textDetailCell = (TextDetailCell) viewHolder.itemView;
                    User currentUser;
                    String str;
                    String format;
                    if (i == SettingsActivity.this.numberRow) {
                        currentUser = UserConfig.getInstance(SettingsActivity.this.currentAccount).getCurrentUser();
                        if (currentUser != null) {
                            str = currentUser.phone;
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
                        str = null;
                        String str2 = "UserBio";
                        if (SettingsActivity.this.userInfo == null || !TextUtils.isEmpty(SettingsActivity.this.userInfo.about)) {
                            textDetailCell.setTextWithEmojiAndValue(SettingsActivity.this.userInfo == null ? LocaleController.getString("Loading", NUM) : SettingsActivity.this.userInfo.about, LocaleController.getString(str2, NUM), false);
                            SettingsActivity settingsActivity = SettingsActivity.this;
                            if (settingsActivity.userInfo != null) {
                                str = SettingsActivity.this.userInfo.about;
                            }
                            settingsActivity.currentBio = str;
                            return;
                        }
                        textDetailCell.setTextAndValue(LocaleController.getString(str2, NUM), LocaleController.getString("UserBioDetail", NUM), false);
                        SettingsActivity.this.currentBio = null;
                    }
                }
            } else if (i == SettingsActivity.this.settingsSectionRow2) {
                ((HeaderCell) viewHolder.itemView).setText(LocaleController.getString("SETTINGS", NUM));
            } else if (i == SettingsActivity.this.numberSectionRow) {
                ((HeaderCell) viewHolder.itemView).setText(LocaleController.getString("Account", NUM));
            }
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            return adapterPosition == SettingsActivity.this.notificationRow || adapterPosition == SettingsActivity.this.numberRow || adapterPosition == SettingsActivity.this.privacyRow || adapterPosition == SettingsActivity.this.languageRow || adapterPosition == SettingsActivity.this.usernameRow || adapterPosition == SettingsActivity.this.bioRow || adapterPosition == SettingsActivity.this.versionRow || adapterPosition == SettingsActivity.this.dataRow || adapterPosition == SettingsActivity.this.chatRow || adapterPosition == SettingsActivity.this.helpRow || adapterPosition == SettingsActivity.this.devicesRow;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            int i2 = i;
            View view = null;
            if (i2 != 0) {
                String str = "windowBackgroundGray";
                String str2 = "windowBackgroundGrayShadow";
                if (i2 == 1) {
                    view = new ShadowSectionCell(this.mContext);
                    CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor(str)), Theme.getThemedDrawable(this.mContext, NUM, str2));
                    combinedDrawable.setFullsize(true);
                    view.setBackgroundDrawable(combinedDrawable);
                } else if (i2 == 2) {
                    view = new TextCell(this.mContext);
                } else if (i2 == 4) {
                    view = new HeaderCell(this.mContext, 23);
                } else if (i2 == 5) {
                    View textInfoPrivacyCell = new TextInfoPrivacyCell(this.mContext, 10);
                    textInfoPrivacyCell.getTextView().setGravity(1);
                    textInfoPrivacyCell.getTextView().setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
                    textInfoPrivacyCell.getTextView().setMovementMethod(null);
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str2));
                    try {
                        PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                        int i3 = packageInfo.versionCode / 10;
                        String str3 = "";
                        switch (packageInfo.versionCode % 10) {
                            case 0:
                            case 9:
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("universal ");
                                stringBuilder.append(Build.CPU_ABI);
                                stringBuilder.append(" ");
                                stringBuilder.append(Build.CPU_ABI2);
                                str3 = stringBuilder.toString();
                                break;
                            case 1:
                            case 3:
                                str3 = "arm-v7a";
                                break;
                            case 2:
                            case 4:
                                str3 = "x86";
                                break;
                            case 5:
                            case 7:
                                str3 = "arm64-v8a";
                                break;
                            case 6:
                            case 8:
                                str3 = "x86_64";
                                break;
                            default:
                                break;
                        }
                        Object[] objArr = new Object[1];
                        objArr[0] = String.format(Locale.US, "v%s (%d) %s", new Object[]{packageInfo.versionName, Integer.valueOf(i3), str3});
                        textInfoPrivacyCell.setText(LocaleController.formatString("TelegramVersion", NUM, objArr));
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    textInfoPrivacyCell.getTextView().setPadding(0, AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(14.0f));
                    CombinedDrawable combinedDrawable2 = new CombinedDrawable(new ColorDrawable(Theme.getColor(str)), Theme.getThemedDrawable(this.mContext, NUM, str2));
                    combinedDrawable2.setFullsize(true);
                    textInfoPrivacyCell.setBackgroundDrawable(combinedDrawable2);
                    view = textInfoPrivacyCell;
                } else if (i2 == 6) {
                    view = new TextDetailCell(this.mContext);
                }
            } else {
                view = new EmptyCell(this.mContext, LocaleController.isRTL ? 46 : 36);
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
        }

        public int getItemViewType(int i) {
            if (i == SettingsActivity.this.emptyRow) {
                return 0;
            }
            if (i == SettingsActivity.this.settingsSectionRow) {
                return 1;
            }
            if (i == SettingsActivity.this.notificationRow || i == SettingsActivity.this.privacyRow || i == SettingsActivity.this.languageRow || i == SettingsActivity.this.dataRow || i == SettingsActivity.this.chatRow || i == SettingsActivity.this.helpRow || i == SettingsActivity.this.devicesRow) {
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
            this.this$0.presentFragment(new ActionIntroActivity(3));
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
            this.this$0.presentFragment(new PrivacyUsersActivity());
        }

        public /* synthetic */ void lambda$new$16$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacyControlActivity(6, true));
        }

        public /* synthetic */ void lambda$new$17$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacyControlActivity(0, true));
        }

        public /* synthetic */ void lambda$new$18$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacyControlActivity(4, true));
        }

        public /* synthetic */ void lambda$new$19$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacyControlActivity(5, true));
        }

        public /* synthetic */ void lambda$new$20$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacyControlActivity(3, true));
        }

        public /* synthetic */ void lambda$new$21$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacyControlActivity(2, true));
        }

        public /* synthetic */ void lambda$new$22$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacyControlActivity(1, true));
        }

        public /* synthetic */ void lambda$new$23$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new PasscodeActivity(SharedConfig.passcodeHash.length() > 0 ? 2 : 0));
        }

        public /* synthetic */ void lambda$new$24$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new TwoStepVerificationActivity(0));
        }

        public /* synthetic */ void lambda$new$25$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new SessionsActivity(0));
        }

        public /* synthetic */ void lambda$new$26$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacySettingsActivity());
        }

        public /* synthetic */ void lambda$new$27$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacySettingsActivity());
        }

        public /* synthetic */ void lambda$new$28$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacySettingsActivity());
        }

        public /* synthetic */ void lambda$new$29$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new SessionsActivity(1));
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
            this.this$0.presentFragment(new PrivacySettingsActivity());
        }

        public /* synthetic */ void lambda$new$35$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new SessionsActivity(0));
        }

        public /* synthetic */ void lambda$new$36$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        public /* synthetic */ void lambda$new$37$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        public /* synthetic */ void lambda$new$38$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new CacheControlActivity());
        }

        public /* synthetic */ void lambda$new$39$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new CacheControlActivity());
        }

        public /* synthetic */ void lambda$new$40$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new CacheControlActivity());
        }

        public /* synthetic */ void lambda$new$41$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new CacheControlActivity());
        }

        public /* synthetic */ void lambda$new$42$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataUsageActivity());
        }

        public /* synthetic */ void lambda$new$43$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        public /* synthetic */ void lambda$new$44$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataAutoDownloadActivity(0));
        }

        public /* synthetic */ void lambda$new$45$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataAutoDownloadActivity(1));
        }

        public /* synthetic */ void lambda$new$46$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataAutoDownloadActivity(2));
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
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        public /* synthetic */ void lambda$new$55$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        public /* synthetic */ void lambda$new$56$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new ProxyListActivity());
        }

        public /* synthetic */ void lambda$new$57$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new ProxyListActivity());
        }

        public /* synthetic */ void lambda$new$58$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        public /* synthetic */ void lambda$new$59$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        public /* synthetic */ void lambda$new$60$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new WallpapersListActivity(0));
        }

        public /* synthetic */ void lambda$new$61$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new WallpapersListActivity(1));
        }

        public /* synthetic */ void lambda$new$62$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new WallpapersListActivity(0));
        }

        public /* synthetic */ void lambda$new$63$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new ThemeActivity(1));
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
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        public /* synthetic */ void lambda$new$70$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        public /* synthetic */ void lambda$new$71$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        public /* synthetic */ void lambda$new$72$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new StickersActivity(0));
        }

        public /* synthetic */ void lambda$new$73$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new StickersActivity(0));
        }

        public /* synthetic */ void lambda$new$74$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new FeaturedStickersActivity());
        }

        public /* synthetic */ void lambda$new$75$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new StickersActivity(1));
        }

        public /* synthetic */ void lambda$new$76$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new ArchivedStickersActivity(0));
        }

        public /* synthetic */ void lambda$new$77$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new ArchivedStickersActivity(1));
        }

        public /* synthetic */ void lambda$new$78$SettingsActivity$SearchAdapter() {
            this.this$0.presentFragment(new LanguageSelectActivity());
        }

        public /* synthetic */ void lambda$new$80$SettingsActivity$SearchAdapter() {
            SettingsActivity settingsActivity = this.this$0;
            settingsActivity.showDialog(AlertsCreator.createSupportAlert(settingsActivity));
        }

        public /* synthetic */ void lambda$new$81$SettingsActivity$SearchAdapter() {
            Browser.openUrl(this.this$0.getParentActivity(), LocaleController.getString("TelegramFaqUrl", NUM));
        }

        public /* synthetic */ void lambda$new$82$SettingsActivity$SearchAdapter() {
            Browser.openUrl(this.this$0.getParentActivity(), LocaleController.getString("PrivacyPolicyUrl", NUM));
        }

        public SearchAdapter(SettingsActivity settingsActivity, Context context) {
            this.this$0 = settingsActivity;
            SearchResult[] searchResultArr = new SearchResult[83];
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
            searchResultArr[16] = new SearchResult(this, 105, LocaleController.getString("PrivacyPhone", NUM), LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$f_3utWtHCzlSn6039S3bgof9vWg(this));
            searchResultArr[17] = new SearchResult(this, 102, LocaleController.getString("PrivacyLastSeen", NUM), LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$xiSHHuIuMQVeQ_fdA3Wj_Bgq4iA(this));
            searchResultArr[18] = new SearchResult(this, 103, LocaleController.getString("PrivacyProfilePhoto", NUM), LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$RYPspq-lY6i8gNBxBdrIhxctFpo(this));
            searchResultArr[19] = new SearchResult(this, 104, LocaleController.getString("PrivacyForwards", NUM), LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$6LoBQomheYgP8glCLASSNAMExFz2WzjyY(this));
            searchResultArr[20] = new SearchResult(this, 105, LocaleController.getString("PrivacyP2P", NUM), LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$VprFTsbEA5x2jnmQ8O1zs63h-9E(this));
            searchResultArr[21] = new SearchResult(this, 106, LocaleController.getString("Calls", NUM), LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$c5DPXMo95Hg5TAoapaOpYope9ao(this));
            searchResultArr[22] = new SearchResult(this, 107, LocaleController.getString("GroupsAndChannels", NUM), LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$YveYfMPgJtpzBc_Aye2IlUKRL_E(this));
            searchResultArr[23] = new SearchResult(this, 108, LocaleController.getString("Passcode", NUM), LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$OE5ko5HrJRcdo_04aDjTiMIRF4o(this));
            searchResultArr[24] = new SearchResult(this, 109, LocaleController.getString("TwoStepVerification", NUM), LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$-P9hp4zriAme7mOQTdmdfHidC8Y(this));
            searchResultArr[25] = new SearchResult(this, 110, LocaleController.getString("SessionsTitle", NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$B_YXrprYoUDKN_R4RWC-vN8dQ3Y(this));
            searchResultArr[26] = new SearchResult(this, 111, LocaleController.getString("PrivacyDeleteCloudDrafts", NUM), "clearDraftsRow", LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$5sT7VrARMFm8AXSv5eWPD8wnHec(this));
            searchResultArr[27] = new SearchResult(this, 112, LocaleController.getString("DeleteAccountIfAwayFor2", NUM), "deleteAccountRow", LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$lQlwXu3fj_1ozEGBWi7cV-Yrauo(this));
            searchResultArr[28] = new SearchResult(this, 113, LocaleController.getString("PrivacyPaymentsClear", NUM), "paymentsClearRow", LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$VLzl5etbBP7OOe7IzDvNV3te2pQ(this));
            searchResultArr[29] = new SearchResult(this, 114, LocaleController.getString("WebSessionsTitle", NUM), LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$i8RSZV17ziFovJKq7XfIMHoM02o(this));
            searchResultArr[30] = new SearchResult(this, 115, LocaleController.getString("SyncContactsDelete", NUM), "contactsDeleteRow", LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$ykgPCWhfDa0SAQRBusRsel30WAo(this));
            searchResultArr[31] = new SearchResult(this, 116, LocaleController.getString("SyncContacts", NUM), "contactsSyncRow", LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$ogTF-_W6GjBUa6jCJUHIlUDUEH0(this));
            searchResultArr[32] = new SearchResult(this, 117, LocaleController.getString("SuggestContacts", NUM), "contactsSuggestRow", LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$gXqnmEjhglRSWqeZPzgMYOpOhsk(this));
            searchResultArr[33] = new SearchResult(this, 118, LocaleController.getString("MapPreviewProvider", NUM), "secretMapRow", LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$lb1RmyZ9zEsc2hcDKKU7dHdJeD8(this));
            searchResultArr[34] = new SearchResult(this, 119, LocaleController.getString("SecretWebPage", NUM), "secretWebpageRow", LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$CPyFvh3ngQ1x3gECH51yA4WVVU8(this));
            searchResultArr[35] = new SearchResult(this, 120, LocaleController.getString("Devices", NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$SR0QQ8fXitd2n2FmlZnOuv-PjCM(this));
            String str2 = "DataSettings";
            searchResultArr[36] = new SearchResult(this, 200, LocaleController.getString(str2, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$jxZ1ug5oHHZLFprdCJ2sObOMdeY(this));
            searchResultArr[37] = new SearchResult(this, 201, LocaleController.getString("DataUsage", NUM), "usageSectionRow", LocaleController.getString(str2, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$2a_PtgyUOPZ5daThh02W6OTXvjI(this));
            String str3 = "StorageUsage";
            searchResultArr[38] = new SearchResult(this, 202, LocaleController.getString(str3, NUM), LocaleController.getString(str2, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$OfGBrtqXQbffkrMJs-wPG7G0fXA(this));
            String str4 = str3;
            searchResultArr[39] = new SearchResult(203, LocaleController.getString("KeepMedia", NUM), "keepMediaRow", LocaleController.getString(str2, NUM), LocaleController.getString(str3, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$d-3fa2mVqtsd9YcCWBrdaK4AvwI(this));
            searchResultArr[40] = new SearchResult(204, LocaleController.getString("ClearMediaCache", NUM), "cacheRow", LocaleController.getString(str2, NUM), LocaleController.getString(str4, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$TWYG1OVbu9uZ6lFX1vM2hYJxs7w(this));
            searchResultArr[41] = new SearchResult(205, LocaleController.getString("LocalDatabase", NUM), "databaseRow", LocaleController.getString(str2, NUM), LocaleController.getString(str4, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$2CLVCiRkKjEbcz0jARW3ZP41JX8(this));
            searchResultArr[42] = new SearchResult(this, 206, LocaleController.getString("NetworkUsage", NUM), LocaleController.getString(str2, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$VUKBbuzi1KBkCnnX2JlWeTm2Cac(this));
            searchResultArr[43] = new SearchResult(this, 207, LocaleController.getString("AutomaticMediaDownload", NUM), "mediaDownloadSectionRow", LocaleController.getString(str2, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$K3WxuNbtWdfw44GPZwu3Vih5_x4(this));
            searchResultArr[44] = new SearchResult(this, 208, LocaleController.getString("WhenUsingMobileData", NUM), LocaleController.getString(str2, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$BLHHvMFpEDhqBkzKQy4VYJV2Wzg(this));
            searchResultArr[45] = new SearchResult(this, 209, LocaleController.getString("WhenConnectedOnWiFi", NUM), LocaleController.getString(str2, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$Pqx2xX3w9fZ7nlbzuimuQvpIHXo(this));
            searchResultArr[46] = new SearchResult(this, 210, LocaleController.getString("WhenRoaming", NUM), LocaleController.getString(str2, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$LMKnGgygm7Zy5y5yA0ZMoVKQ0rQ(this));
            searchResultArr[47] = new SearchResult(this, 211, LocaleController.getString("ResetAutomaticMediaDownload", NUM), "resetDownloadRow", LocaleController.getString(str2, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$lrHAN36teH_lCOFJ5PXNFKTwHTM(this));
            searchResultArr[48] = new SearchResult(this, 212, LocaleController.getString("AutoplayMedia", NUM), "autoplayHeaderRow", LocaleController.getString(str2, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$raORBf9hVrFvO0t2rS9X8ZjwExo(this));
            searchResultArr[49] = new SearchResult(this, 213, LocaleController.getString("AutoplayGIF", NUM), "autoplayGifsRow", LocaleController.getString(str2, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$ZTzEE1deo_nmR-UL_VYD8uhoHoI(this));
            searchResultArr[50] = new SearchResult(this, 214, LocaleController.getString("AutoplayVideo", NUM), "autoplayVideoRow", LocaleController.getString(str2, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$rOx4Eve-2YVGzrlUGIGIovv9JXE(this));
            searchResultArr[51] = new SearchResult(this, 215, LocaleController.getString("Streaming", NUM), "streamSectionRow", LocaleController.getString(str2, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$rGwlYZXuIw_8MDI7I45SYbMs0G8(this));
            searchResultArr[52] = new SearchResult(this, 216, LocaleController.getString("EnableStreaming", NUM), "enableStreamRow", LocaleController.getString(str2, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$yQdZ2pbhXfP1NJfQiJKJVPctbyk(this));
            searchResultArr[53] = new SearchResult(this, 217, LocaleController.getString("Calls", NUM), "callsSectionRow", LocaleController.getString(str2, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$B4etMl_s5sjFGhMYip4XHa1DxEY(this));
            searchResultArr[54] = new SearchResult(this, 218, LocaleController.getString("VoipUseLessData", NUM), "useLessDataForCallsRow", LocaleController.getString(str2, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$i2kSSNMxTtfCLASSNAMERmjV3ptURjCLA(this));
            searchResultArr[55] = new SearchResult(this, 219, LocaleController.getString("VoipQuickReplies", NUM), "quickRepliesRow", LocaleController.getString(str2, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$XMZSE9GUh-0GP5dBwOjHByrjteo(this));
            searchResultArr[56] = new SearchResult(this, 220, LocaleController.getString("ProxySettings", NUM), LocaleController.getString(str2, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$t63Mb0PWa1-oMCnIjcS62wWkWhI(this));
            searchResultArr[57] = new SearchResult(221, LocaleController.getString("UseProxyForCalls", NUM), "callsRow", LocaleController.getString(str2, NUM), LocaleController.getString("ProxySettings", NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$oHMz_dP6H50aHqxTbdc3E7d4Oyo(this));
            str = "ChatSettings";
            searchResultArr[58] = new SearchResult(this, 300, LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$PQPKVfMuNVXvznSLWUi9d_FbGD8(this));
            searchResultArr[59] = new SearchResult(this, 301, LocaleController.getString("TextSizeHeader", NUM), "textSizeHeaderRow", LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$hzumI77K9pwC4a7mUUTUW31jkYs(this));
            searchResultArr[60] = new SearchResult(this, 302, LocaleController.getString("ChatBackground", NUM), LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$Ov5OcCfPmPzmx4efoTSoB8-YMKk(this));
            searchResultArr[61] = new SearchResult(303, LocaleController.getString("SetColor", NUM), null, LocaleController.getString(str, NUM), LocaleController.getString("ChatBackground", NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$mEajmgrt04XurJBLXFyM6GNHru0(this));
            searchResultArr[62] = new SearchResult(304, LocaleController.getString("ResetChatBackgrounds", NUM), "resetRow", LocaleController.getString(str, NUM), LocaleController.getString("ChatBackground", NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$vSOlOOSDVPitPhRnpx1o7rNw4zw(this));
            searchResultArr[63] = new SearchResult(this, 305, LocaleController.getString("AutoNightTheme", NUM), LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$46GIfyE4OhRvg3Gs4uaPMW_J3UM(this));
            searchResultArr[64] = new SearchResult(this, 306, LocaleController.getString("ColorTheme", NUM), "themeHeaderRow", LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$s0h_sbhuVis6N-TS7j6bEyh7FfQ(this));
            searchResultArr[65] = new SearchResult(this, 307, LocaleController.getString("ChromeCustomTabs", NUM), "customTabsRow", LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$5_PGzT67poE7m5IzEQLBofn8u-o(this));
            searchResultArr[66] = new SearchResult(this, 308, LocaleController.getString("DirectShare", NUM), "directShareRow", LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$djYxV0TLWfpCNMAt-hbj8er6rqo(this));
            searchResultArr[67] = new SearchResult(this, 309, LocaleController.getString("EnableAnimations", NUM), "enableAnimationsRow", LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$NM0akNEGYlYo-ZnAPY_boq0dLjY(this));
            searchResultArr[68] = new SearchResult(this, 310, LocaleController.getString("RaiseToSpeak", NUM), "raiseToSpeakRow", LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$WOvivT9gT9S9mbWbIsPI-YD8I38(this));
            searchResultArr[69] = new SearchResult(this, 311, LocaleController.getString("SendByEnter", NUM), "sendByEnterRow", LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$LE1pN2xBggeckg4BXNGrk8dAfCA(this));
            searchResultArr[70] = new SearchResult(this, 312, LocaleController.getString("SaveToGallerySettings", NUM), "saveToGalleryRow", LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$guZCAugp-k8fJ3Gd84aSwGDA7co(this));
            searchResultArr[71] = new SearchResult(this, 312, LocaleController.getString("DistanceUnits", NUM), "distanceRow", LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$a2LSsVzKR8HJrNBIEh5xSXFk_kY(this));
            String str5 = "StickersAndMasks";
            searchResultArr[72] = new SearchResult(this, 313, LocaleController.getString(str5, NUM), LocaleController.getString(str, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$vTZmp1Lfwd9Jvvar_tEr9C2amaYY(this));
            searchResultArr[73] = new SearchResult(314, LocaleController.getString("SuggestStickers", NUM), "suggestRow", LocaleController.getString(str, NUM), LocaleController.getString(str5, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$O-sT8FF_ujolqkcXgAFR2EWMCOQ(this));
            searchResultArr[74] = new SearchResult(315, LocaleController.getString("FeaturedStickers", NUM), null, LocaleController.getString(str, NUM), LocaleController.getString(str5, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$dg765ZjLDUr492FSgTOKjOaKweE(this));
            searchResultArr[75] = new SearchResult(316, LocaleController.getString("Masks", NUM), null, LocaleController.getString(str, NUM), LocaleController.getString(str5, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$h743V_ShaSJwbDnqAuxpCdWSljA(this));
            searchResultArr[76] = new SearchResult(317, LocaleController.getString("ArchivedStickers", NUM), null, LocaleController.getString(str, NUM), LocaleController.getString(str5, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$6umUiixsrbn0nLlRd8bqshWjNeU(this));
            searchResultArr[77] = new SearchResult(317, LocaleController.getString("ArchivedMasks", NUM), null, LocaleController.getString(str, NUM), LocaleController.getString(str5, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$8sCZE9SRgbZa275PGe2lLCWITbw(this));
            searchResultArr[78] = new SearchResult(this, 400, LocaleController.getString("Language", NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$UKpWGlt--8lNdI9VfZj9-2dBm0k(this));
            String str6 = "SettingsHelp";
            searchResultArr[79] = new SearchResult(this, 401, LocaleController.getString(str6, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$JOnPaeK_uIy91_LPgGyi-g-FEkA(this.this$0));
            searchResultArr[80] = new SearchResult(this, 402, LocaleController.getString("AskAQuestion", NUM), LocaleController.getString(str6, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$98mN5UJETCB-Tj6G-z-MUvkMLLk(this));
            searchResultArr[81] = new SearchResult(this, 403, LocaleController.getString("TelegramFAQ", NUM), LocaleController.getString(str6, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$T6LmnZuXx3-RYB-6IOpAraah3ds(this));
            searchResultArr[82] = new SearchResult(this, 404, LocaleController.getString("PrivacyPolicy", NUM), LocaleController.getString(str6, NUM), NUM, new -$$Lambda$SettingsActivity$SearchAdapter$yKnsP8v67jJI-d3lKhy1KZ_BUQk(this));
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
            Collections.sort(this.recentSearches, new -$$Lambda$SettingsActivity$SearchAdapter$3-Nv6NiCpuj20HjBRWWZ-bVzYfE(this));
        }

        public /* synthetic */ int lambda$new$83$SettingsActivity$SearchAdapter(Object obj, Object obj2) {
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
                ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tL_messages_getWebPage, new -$$Lambda$SettingsActivity$SearchAdapter$a3oWFPx2z1Xg1Prynvar_WdOjIQ(this));
            }
        }

        public /* synthetic */ void lambda$loadFaqWebPage$84$SettingsActivity$SearchAdapter(TLObject tLObject, TL_error tL_error) {
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
                String[] access$4200;
                if (!this.searchWas) {
                    i--;
                    Object obj = this.recentSearches.get(i);
                    String access$4400;
                    if (obj instanceof SearchResult) {
                        searchResult = (SearchResult) obj;
                        access$4400 = searchResult.searchTitle;
                        access$4200 = searchResult.path;
                        if (i >= this.recentSearches.size() - 1) {
                            z = false;
                        }
                        settingsSearchCell.setTextAndValue(access$4400, access$4200, false, z);
                    } else if (obj instanceof FaqSearchResult) {
                        FaqSearchResult faqSearchResult = (FaqSearchResult) obj;
                        access$4400 = faqSearchResult.title;
                        access$4200 = faqSearchResult.path;
                        if (i < this.recentSearches.size() - 1) {
                            z2 = true;
                        }
                        settingsSearchCell.setTextAndValue(access$4400, access$4200, true, z2);
                    }
                } else if (i < this.searchResults.size()) {
                    int access$4100;
                    searchResult = (SearchResult) this.searchResults.get(i);
                    SearchResult searchResult2 = i > 0 ? (SearchResult) this.searchResults.get(i - 1) : null;
                    if (searchResult2 == null || searchResult2.iconResId != searchResult.iconResId) {
                        access$4100 = searchResult.iconResId;
                    } else {
                        access$4100 = 0;
                    }
                    CharSequence charSequence = (CharSequence) this.resultNames.get(i);
                    access$4200 = searchResult.path;
                    if (i >= this.searchResults.size() - 1) {
                        z = false;
                    }
                    settingsSearchCell.setTextAndValueAndIcon(charSequence, access$4200, access$4100, z);
                } else {
                    i -= this.searchResults.size() + 1;
                    CharSequence charSequence2 = (CharSequence) this.resultNames.get(this.searchResults.size() + i);
                    access$4200 = ((FaqSearchResult) this.faqSearchResults.get(i)).path;
                    if (i < this.searchResults.size() - 1) {
                        z2 = true;
                    }
                    settingsSearchCell.setTextAndValue(charSequence2, access$4200, true, z2);
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
            -$$Lambda$SettingsActivity$SearchAdapter$LgPWmspn2qmpxhrX8aw21-e3-qk -__lambda_settingsactivity_searchadapter_lgpwmspn2qmpxhrx8aw21-e3-qk = new -$$Lambda$SettingsActivity$SearchAdapter$LgPWmspn2qmpxhrX8aw21-e3-qk(this, str);
            this.searchRunnable = -__lambda_settingsactivity_searchadapter_lgpwmspn2qmpxhrx8aw21-e3-qk;
            dispatchQueue.postRunnable(-__lambda_settingsactivity_searchadapter_lgpwmspn2qmpxhrx8aw21-e3-qk, 300);
        }

        public /* synthetic */ void lambda$search$86$SettingsActivity$SearchAdapter(String str) {
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
            AndroidUtilities.runOnUIThread(new -$$Lambda$SettingsActivity$SearchAdapter$Qgy0kqOPf_AVF_2_iJo66fzSqoo(this, str, arrayList, arrayList2, arrayList3));
        }

        public /* synthetic */ void lambda$null$85$SettingsActivity$SearchAdapter(String str, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3) {
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

        public boolean isSearchWas() {
            return this.searchWas;
        }
    }

    public /* synthetic */ String getInitialSearchString() {
        return -CC.$default$getInitialSearchString(this);
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.hasOwnBackground = true;
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
        this.chatRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.devicesRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.languageRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.helpRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.versionRow = i;
        MediaDataController.getInstance(this.currentAccount).checkFeaturedStickers();
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

    /* Access modifiers changed, original: protected */
    public ActionBar createActionBar(Context context) {
        ActionBar actionBar = new ActionBar(context);
        boolean z = false;
        actionBar.setItemsBackgroundColor(Theme.getColor("avatar_actionBarSelectorBlue"), false);
        actionBar.setItemsColor(Theme.getColor("avatar_actionBarIconBlue"), false);
        actionBar.setBackButtonImage(NUM);
        actionBar.setCastShadows(false);
        actionBar.setAddToContainer(false);
        if (VERSION.SDK_INT >= 21 && !AndroidUtilities.isTablet()) {
            z = true;
        }
        actionBar.setOccupyStatusBar(z);
        return actionBar;
    }

    public View createView(Context context) {
        int findFirstVisibleItemPosition;
        int top;
        Object tag;
        Context context2 = context;
        this.extraHeight = AndroidUtilities.dp(88.0f);
        this.searchTransitionOffset = 0;
        this.searchTransitionProgress = 1.0f;
        this.searchMode = false;
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
        this.searchItem = createMenu.addItem(3, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItemSearchListener() {
            public Animator getCustomToggleTransition() {
                SettingsActivity settingsActivity = SettingsActivity.this;
                settingsActivity.searchMode = settingsActivity.searchMode ^ 1;
                if (SettingsActivity.this.searchMode) {
                    SettingsActivity.this.searchAdapter.loadFaqWebPage();
                } else {
                    SettingsActivity.this.searchItem.clearFocusOnSearchView();
                }
                settingsActivity = SettingsActivity.this;
                return settingsActivity.searchExpandTransition(settingsActivity.searchMode);
            }

            public void onTextChanged(EditText editText) {
                SettingsActivity.this.searchAdapter.search(editText.getText().toString().toLowerCase());
            }
        });
        String str = "SearchInSettings";
        this.searchItem.setContentDescription(LocaleController.getString(str, NUM));
        this.searchItem.setSearchFieldHint(LocaleController.getString(str, NUM));
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
            private Paint paint = new Paint();

            public boolean hasOverlappingRendering() {
                return false;
            }

            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                SimpleTextView access$1500 = SettingsActivity.this.titleTextView;
                int i3 = (AndroidUtilities.isTablet() || getContext().getResources().getConfiguration().orientation != 2) ? 20 : 18;
                access$1500.setTextSize(i3);
                super.onMeasure(i, i2);
            }

            /* Access modifiers changed, original: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                if (SettingsActivity.this.titleTextView != null) {
                    int dp = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 80.0f : 72.0f);
                    i = (ActionBar.getCurrentActionBarHeight() - SettingsActivity.this.titleTextView.getTextHeight()) / 2;
                    i2 = (VERSION.SDK_INT < 21 || AndroidUtilities.isTablet()) ? 0 : AndroidUtilities.statusBarHeight;
                    i += i2;
                    SettingsActivity.this.titleTextView.layout(dp, i, SettingsActivity.this.titleTextView.getMeasuredWidth() + dp, SettingsActivity.this.titleTextView.getTextHeight() + i);
                }
                SettingsActivity.this.checkListViewScroll();
            }

            public void onDraw(Canvas canvas) {
                this.paint.setColor(Theme.getColor("windowBackgroundWhite"));
                View access$1700 = SettingsActivity.this.listView.getVisibility() == 0 ? SettingsActivity.this.listView : SettingsActivity.this.searchListView;
                canvas.drawRect((float) access$1700.getLeft(), (float) ((access$1700.getTop() + SettingsActivity.this.extraHeight) + SettingsActivity.this.searchTransitionOffset), (float) access$1700.getRight(), (float) access$1700.getBottom(), this.paint);
            }
        };
        this.fragmentView.setWillNotDraw(false);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.listView = new RecyclerListView(context2) {
            public boolean hasOverlappingRendering() {
                return false;
            }
        };
        this.listView.setHideIfEmpty(false);
        this.listView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView = this.listView;
        AnonymousClass6 anonymousClass6 = new LinearLayoutManager(context2, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = anonymousClass6;
        recyclerListView.setLayoutManager(anonymousClass6);
        String str2 = "avatar_backgroundActionBarBlue";
        this.listView.setGlowColor(Theme.getColor(str2));
        this.listView.setPadding(0, AndroidUtilities.dp(88.0f), 0, 0);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        this.listView.setClipToPadding(false);
        this.listView.setOnItemClickListener(new -$$Lambda$SettingsActivity$iv7dvdiS8exd3Bpi9ZCm9_zKqgo(this));
        this.listView.setOnItemLongClickListener(new OnItemLongClickListener() {
            private int pressCount = 0;

            public boolean onItemClick(View view, int i) {
                if (i != SettingsActivity.this.versionRow) {
                    return false;
                }
                this.pressCount++;
                if (this.pressCount >= 2 || BuildVars.DEBUG_PRIVATE_VERSION) {
                    int i2;
                    String str;
                    Builder builder = new Builder(SettingsActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("DebugMenu", NUM));
                    CharSequence[] charSequenceArr = new CharSequence[11];
                    charSequenceArr[0] = LocaleController.getString("DebugMenuImportContacts", NUM);
                    charSequenceArr[1] = LocaleController.getString("DebugMenuReloadContacts", NUM);
                    charSequenceArr[2] = LocaleController.getString("DebugMenuResetContacts", NUM);
                    charSequenceArr[3] = LocaleController.getString("DebugMenuResetDialogs", NUM);
                    if (BuildVars.LOGS_ENABLED) {
                        i2 = NUM;
                        str = "DebugMenuDisableLogs";
                    } else {
                        i2 = NUM;
                        str = "DebugMenuEnableLogs";
                    }
                    charSequenceArr[4] = LocaleController.getString(str, i2);
                    if (SharedConfig.inappCamera) {
                        i2 = NUM;
                        str = "DebugMenuDisableCamera";
                    } else {
                        i2 = NUM;
                        str = "DebugMenuEnableCamera";
                    }
                    charSequenceArr[5] = LocaleController.getString(str, i2);
                    charSequenceArr[6] = LocaleController.getString("DebugMenuClearMediaCache", NUM);
                    charSequenceArr[7] = LocaleController.getString("DebugMenuCallSettings", NUM);
                    charSequenceArr[8] = null;
                    charSequenceArr[9] = BuildVars.DEBUG_PRIVATE_VERSION ? "Check for app updates" : null;
                    charSequenceArr[10] = LocaleController.getString("DebugMenuReadAllDialogs", NUM);
                    builder.setItems(charSequenceArr, new -$$Lambda$SettingsActivity$7$4McRxfMSs8nWqWNBQMHV_tpAvyU(this));
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

            public /* synthetic */ void lambda$onItemClick$0$SettingsActivity$7(DialogInterface dialogInterface, int i) {
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
                    SharedConfig.textSelectionHintShows = 0;
                } else if (i == 7) {
                    VoIPHelper.showCallDebugSettings(SettingsActivity.this.getParentActivity());
                } else if (i == 8) {
                    SharedConfig.toggleRoundCamera16to9();
                } else if (i == 9) {
                    ((LaunchActivity) SettingsActivity.this.getParentActivity()).checkAppUpdate(true);
                } else if (i == 10) {
                    MessagesStorage.getInstance(SettingsActivity.this.currentAccount).readAllDialogs(-1);
                }
            }
        });
        this.searchListView = new RecyclerListView(context2) {
            public boolean hasOverlappingRendering() {
                return false;
            }
        };
        this.searchListView.setVerticalScrollBarEnabled(false);
        this.searchListView.setLayoutManager(new LinearLayoutManager(context2, 1, false));
        this.searchListView.setGlowColor(Theme.getColor(str2));
        frameLayout.addView(this.searchListView, LayoutHelper.createFrame(-1, -1, 51));
        this.searchListView.setAdapter(this.searchAdapter);
        this.searchListView.setItemAnimator(null);
        this.searchListView.setLayoutAnimation(null);
        this.searchListView.setOnItemClickListener(new -$$Lambda$SettingsActivity$E5ax_nuR1sIzF8dCff9conlHBHI(this));
        this.searchListView.setOnItemLongClickListener(new -$$Lambda$SettingsActivity$gZDm_PzI8ArhWn-Z61KkhfUKDBc(this));
        this.searchListView.setVisibility(8);
        this.emptyView = new EmptyTextProgressView(context2);
        this.emptyView.showTextView();
        this.emptyView.setTextSize(18);
        this.emptyView.setVisibility(8);
        this.emptyView.setShowAtCenter(true);
        this.emptyView.setPadding(0, AndroidUtilities.dp(50.0f), 0, 0);
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.topView = new TopView(context2);
        this.topView.setBackgroundColor(Theme.getColor(str2));
        frameLayout.addView(this.topView);
        frameLayout.addView(this.actionBar);
        this.avatarContainer = new FrameLayout(context2);
        this.avatarContainer.setPivotX(0.0f);
        this.avatarContainer.setPivotY(0.0f);
        frameLayout.addView(this.avatarContainer, LayoutHelper.createFrame(42, 42.0f, 51, 64.0f, 0.0f, 0.0f, 0.0f));
        this.avatarContainer.setOnClickListener(new -$$Lambda$SettingsActivity$y4CitHZGIudAU599E763QQaJqys(this));
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
        this.titleTextView = new SimpleTextView(context2);
        this.titleTextView.setGravity(3);
        this.titleTextView.setTextColor(Theme.getColor("actionBarDefaultTitle"));
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.titleTextView.setText(BuildVars.DEBUG_VERSION ? "Telegram Beta" : LocaleController.getString("AppName", NUM));
        this.titleTextView.setAlpha(0.0f);
        frameLayout.addView(this.titleTextView, LayoutHelper.createFrame(-2, -2, 51));
        this.nameTextView = new TextView(context2);
        this.nameTextView.setTextColor(Theme.getColor("profile_title"));
        this.nameTextView.setTextSize(1, 18.0f);
        this.nameTextView.setLines(1);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setEllipsize(TruncateAt.END);
        this.nameTextView.setGravity(3);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setPivotX(0.0f);
        this.nameTextView.setPivotY(0.0f);
        frameLayout.addView(this.nameTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, 96.0f, 0.0f));
        this.onlineTextView = new TextView(context2);
        this.onlineTextView.setTextColor(Theme.getColor("profile_status"));
        this.onlineTextView.setTextSize(1, 14.0f);
        this.onlineTextView.setLines(1);
        this.onlineTextView.setMaxLines(1);
        this.onlineTextView.setSingleLine(true);
        this.onlineTextView.setEllipsize(TruncateAt.END);
        this.onlineTextView.setGravity(3);
        frameLayout.addView(this.onlineTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, 96.0f, 0.0f));
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
        frameLayout.addView(this.writeButton, LayoutHelper.createFrame(VERSION.SDK_INT >= 21 ? 56 : 60, VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, 53, 0.0f, 0.0f, 16.0f, 0.0f));
        this.writeButton.setOnClickListener(new -$$Lambda$SettingsActivity$qZ2Zffw-Lsm81ipndKhZWDeS4zI(this));
        this.writeButton.setContentDescription(LocaleController.getString("AccDescrChangeProfilePicture", NUM));
        if (findFirstVisibleItemPosition != -1) {
            this.layoutManager.scrollToPositionWithOffset(findFirstVisibleItemPosition, top);
            if (tag != null) {
                this.writeButton.setTag(Integer.valueOf(0));
                this.writeButton.setScaleX(0.2f);
                this.writeButton.setScaleY(0.2f);
                this.writeButton.setAlpha(0.0f);
            }
        }
        needLayout();
        this.searchListView.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1) {
                    AndroidUtilities.hideKeyboard(SettingsActivity.this.getParentActivity().getCurrentFocus());
                }
            }
        });
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                SettingsActivity.this.checkListViewScroll();
            }
        });
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$0$SettingsActivity(View view, int i) {
        if (i == this.notificationRow) {
            presentFragment(new NotificationsSettingsActivity());
        } else if (i == this.privacyRow) {
            presentFragment(new PrivacySettingsActivity());
        } else if (i == this.dataRow) {
            presentFragment(new DataSettingsActivity());
        } else if (i == this.chatRow) {
            presentFragment(new ThemeActivity(0));
        } else if (i == this.devicesRow) {
            presentFragment(new SessionsActivity(0));
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
            presentFragment(new ActionIntroActivity(3));
        }
    }

    public /* synthetic */ void lambda$createView$1$SettingsActivity(View view, int i) {
        if (i >= 0) {
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

    public /* synthetic */ boolean lambda$createView$3$SettingsActivity(View view, int i) {
        if (this.searchAdapter.isSearchWas()) {
            return false;
        }
        Builder builder = new Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setMessage(LocaleController.getString("ClearSearch", NUM));
        builder.setPositiveButton(LocaleController.getString("ClearButton", NUM).toUpperCase(), new -$$Lambda$SettingsActivity$dDanrmCzxEiUJ6JZm6j_LmeRgNE(this));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
        showDialog(builder.create());
        return true;
    }

    public /* synthetic */ void lambda$null$2$SettingsActivity(DialogInterface dialogInterface, int i) {
        this.searchAdapter.clearRecent();
    }

    public /* synthetic */ void lambda$createView$4$SettingsActivity(View view) {
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

    public /* synthetic */ void lambda$createView$6$SettingsActivity(View view) {
        User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
        if (user == null) {
            user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
        }
        if (user != null) {
            ImageUpdater imageUpdater = this.imageUpdater;
            UserProfilePhoto userProfilePhoto = user.photo;
            boolean z = (userProfilePhoto == null || userProfilePhoto.photo_big == null || (userProfilePhoto instanceof TL_userProfilePhotoEmpty)) ? false : true;
            imageUpdater.openMenu(z, new -$$Lambda$SettingsActivity$cUD4MnQTEFsrIJReNYTTOl_wJCQ(this));
        }
    }

    public /* synthetic */ void lambda$null$5$SettingsActivity() {
        MessagesController.getInstance(this.currentAccount).deleteUserPhoto(null);
    }

    private Animator searchExpandTransition(final boolean z) {
        if (z) {
            getParentActivity().getWindow().setSoftInputMode(32);
        }
        Animator animator = this.searchViewTransition;
        if (animator != null) {
            animator.removeAllListeners();
            this.searchViewTransition.cancel();
        }
        float[] fArr = new float[2];
        fArr[0] = this.searchTransitionProgress;
        boolean z2 = true;
        fArr[1] = z ? 0.0f : 1.0f;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
        int i = this.extraHeight;
        this.searchListView.setTranslationY((float) i);
        this.searchListView.setVisibility(0);
        this.searchItem.setVisibility(0);
        this.listView.setVisibility(0);
        needLayout();
        this.avatarContainer.setVisibility(0);
        this.nameTextView.setVisibility(0);
        this.onlineTextView.setVisibility(0);
        ActionBar actionBar = this.actionBar;
        if (this.searchTransitionProgress <= 0.5f) {
            z2 = false;
        }
        actionBar.onSearchFieldVisibilityChanged(z2);
        ActionBarMenuItem actionBarMenuItem = this.otherItem;
        int i2 = 8;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.setVisibility(this.searchTransitionProgress > 0.5f ? 0 : 8);
        }
        this.searchItem.setVisibility(this.searchTransitionProgress > 0.5f ? 0 : 8);
        FrameLayout searchContainer = this.searchItem.getSearchContainer();
        if (this.searchTransitionProgress <= 0.5f) {
            i2 = 0;
        }
        searchContainer.setVisibility(i2);
        this.searchListView.setEmptyView(this.emptyView);
        this.avatarContainer.setClickable(false);
        ofFloat.addUpdateListener(new -$$Lambda$SettingsActivity$roC-H3fgrew8wMjfvzWhKR-nxe4(this, ofFloat, i, z));
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                SettingsActivity.this.updateSearchViewState(z);
                SettingsActivity.this.avatarContainer.setClickable(true);
                if (z) {
                    SettingsActivity.this.searchItem.requestFocusOnSearchView();
                }
                SettingsActivity.this.needLayout();
            }
        });
        ofFloat.setDuration(180);
        ofFloat.setInterpolator(this.transitionInterpolator);
        this.searchViewTransition = ofFloat;
        return ofFloat;
    }

    public /* synthetic */ void lambda$searchExpandTransition$7$SettingsActivity(ValueAnimator valueAnimator, int i, boolean z, ValueAnimator valueAnimator2) {
        this.searchTransitionProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        float f = this.searchTransitionProgress;
        float f2 = (f - 0.5f) / 0.5f;
        f = (0.5f - f) / 0.5f;
        if (f2 < 0.0f) {
            f2 = 0.0f;
        }
        if (f < 0.0f) {
            f = 0.0f;
        }
        float f3 = (float) (-i);
        float f4 = this.searchTransitionProgress;
        this.searchTransitionOffset = (int) ((1.0f - f4) * f3);
        float f5 = (float) i;
        this.searchListView.setTranslationY(f4 * f5);
        this.emptyView.setTranslationY(f5 * this.searchTransitionProgress);
        this.listView.setTranslationY(f3 * (1.0f - this.searchTransitionProgress));
        needLayout();
        this.listView.setAlpha(f2);
        float f6 = 1.0f - f2;
        this.searchListView.setAlpha(f6);
        this.emptyView.setAlpha(f6);
        this.avatarContainer.setAlpha(f2);
        this.nameTextView.setAlpha(f2);
        this.onlineTextView.setAlpha(f2);
        this.searchItem.getSearchField().setAlpha(f);
        if (z && this.searchTransitionProgress < 0.7f) {
            this.searchItem.requestFocusOnSearchView();
        }
        int i2 = 8;
        boolean z2 = false;
        this.searchItem.getSearchContainer().setVisibility(this.searchTransitionProgress < 0.5f ? 0 : 8);
        ActionBarMenuItem actionBarMenuItem = this.otherItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.setVisibility(this.searchTransitionProgress > 0.5f ? 0 : 8);
        }
        actionBarMenuItem = this.searchItem;
        if (this.searchTransitionProgress > 0.5f) {
            i2 = 0;
        }
        actionBarMenuItem.setVisibility(i2);
        ActionBar actionBar = this.actionBar;
        if (this.searchTransitionProgress < 0.5f) {
            z2 = true;
        }
        actionBar.onSearchFieldVisibilityChanged(z2);
        actionBarMenuItem = this.otherItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.setAlpha(f2);
        }
        this.searchItem.setAlpha(f2);
        this.topView.invalidate();
    }

    private void updateSearchViewState(boolean z) {
        int i = 0;
        int i2 = z ? 8 : 0;
        this.listView.setVisibility(i2);
        this.searchListView.setVisibility(z ? 0 : 8);
        FrameLayout searchContainer = this.searchItem.getSearchContainer();
        if (!z) {
            i = 8;
        }
        searchContainer.setVisibility(i);
        this.actionBar.onSearchFieldVisibilityChanged(z);
        this.avatarContainer.setVisibility(i2);
        this.nameTextView.setVisibility(i2);
        this.onlineTextView.setVisibility(i2);
        ActionBarMenuItem actionBarMenuItem = this.otherItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.setAlpha(1.0f);
            this.otherItem.setVisibility(i2);
        }
        this.searchItem.setVisibility(i2);
        this.avatarContainer.setAlpha(1.0f);
        this.nameTextView.setAlpha(1.0f);
        this.onlineTextView.setAlpha(1.0f);
        this.searchItem.setAlpha(1.0f);
        this.listView.setAlpha(1.0f);
        this.searchListView.setAlpha(1.0f);
        this.emptyView.setAlpha(1.0f);
        if (z) {
            this.searchListView.setEmptyView(this.emptyView);
        } else {
            this.emptyView.setVisibility(8);
        }
    }

    public void didUploadPhoto(InputFile inputFile, PhotoSize photoSize, PhotoSize photoSize2) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$SettingsActivity$y3N6AebEX9cIH8T9V2hR3xgRVDs(this, inputFile, photoSize2, photoSize));
    }

    public /* synthetic */ void lambda$didUploadPhoto$10$SettingsActivity(InputFile inputFile, PhotoSize photoSize, PhotoSize photoSize2) {
        if (inputFile != null) {
            TL_photos_uploadProfilePhoto tL_photos_uploadProfilePhoto = new TL_photos_uploadProfilePhoto();
            tL_photos_uploadProfilePhoto.file = inputFile;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_photos_uploadProfilePhoto, new -$$Lambda$SettingsActivity$ePb9Hvz3Tr_ON8EtiNjgG2yG9JQ(this));
            return;
        }
        this.avatar = photoSize.location;
        this.avatarBig = photoSize2.location;
        this.avatarImage.setImage(ImageLocation.getForLocal(this.avatar), "50_50", this.avatarDrawable, null);
        showAvatarProgress(true, false);
    }

    public /* synthetic */ void lambda$null$9$SettingsActivity(TLObject tLObject, TL_error tL_error) {
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
        AndroidUtilities.runOnUIThread(new -$$Lambda$SettingsActivity$hdrwgqzO6gCwhEe24Rg3fXQuyRQ(this));
    }

    public /* synthetic */ void lambda$null$8$SettingsActivity() {
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
            if (((Integer) objArr[0]).intValue() == UserConfig.getInstance(this.currentAccount).getClientUserId() && this.listAdapter != null) {
                this.userInfo = (UserFull) objArr[1];
                if (!TextUtils.equals(this.userInfo.about, this.currentBio)) {
                    this.listAdapter.notifyItemChanged(this.bioRow);
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

    /* Access modifiers changed, original: protected */
    public void onTransitionAnimationStart(boolean z, boolean z2) {
        if (((!z && z2) || (z && !z2)) && this.playProfileAnimation && this.allowProfileAnimation) {
            this.openAnimationInProgress = true;
        }
        if (z) {
            NotificationCenter.getInstance(this.currentAccount).setAllowedNotificationsDutingAnimation(new int[]{NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.mediaCountDidLoad, NotificationCenter.mediaCountsDidLoad, NotificationCenter.userInfoDidLoad});
            NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(true);
        }
    }

    /* Access modifiers changed, original: protected */
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            if (!z2 && this.playProfileAnimation && this.allowProfileAnimation) {
                this.openAnimationInProgress = false;
            }
            NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(false);
        }
    }

    public float getAnimationProgress() {
        return this.animationProgress;
    }

    @Keep
    public void setAnimationProgress(float f) {
        this.animationProgress = f;
        this.listView.setAlpha(f);
        this.listView.setTranslationX(((float) AndroidUtilities.dp(48.0f)) - (((float) AndroidUtilities.dp(48.0f)) * f));
        int color = Theme.getColor("avatar_backgroundActionBarBlue");
        int color2 = Theme.getColor("actionBarDefault");
        int red = Color.red(color2);
        int green = Color.green(color2);
        color2 = Color.blue(color2);
        this.topView.setBackgroundColor(Color.rgb(red + ((int) (((float) (Color.red(color) - red)) * f)), green + ((int) (((float) (Color.green(color) - green)) * f)), color2 + ((int) (((float) (Color.blue(color) - color2)) * f))));
        color = Theme.getColor("avatar_actionBarIconBlue");
        color2 = Theme.getColor("actionBarDefaultIcon");
        red = Color.red(color2);
        green = Color.green(color2);
        color2 = Color.blue(color2);
        this.actionBar.setItemsColor(Color.rgb(red + ((int) (((float) (Color.red(color) - red)) * f)), green + ((int) (((float) (Color.green(color) - green)) * f)), color2 + ((int) (((float) (Color.blue(color) - color2)) * f))), false);
        this.titleTextView.setAlpha(1.0f - f);
        this.nameTextView.setAlpha(f);
        this.onlineTextView.setAlpha(f);
        this.extraHeight = (int) (((float) this.initialAnimationExtraHeight) * f);
        this.avatarContainer.setAlpha(f);
        needLayout();
    }

    /* Access modifiers changed, original: protected */
    public AnimatorSet onCustomTransitionAnimation(boolean z, final Runnable runnable) {
        if (!this.playProfileAnimation || !this.allowProfileAnimation) {
            return null;
        }
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(180);
        this.listView.setLayerType(2, null);
        this.actionBar.createMenu();
        String str = "animationProgress";
        ArrayList arrayList;
        ImageView imageView;
        if (z) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.onlineTextView.getLayoutParams();
            layoutParams.rightMargin = (int) ((AndroidUtilities.density * -21.0f) + ((float) AndroidUtilities.dp(8.0f)));
            this.onlineTextView.setLayoutParams(layoutParams);
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.nameTextView.getLayoutParams();
            float ceil = (float) ((int) Math.ceil((double) (((float) (AndroidUtilities.displaySize.x - AndroidUtilities.dp(126.0f))) + (AndroidUtilities.density * 21.0f))));
            if (ceil < this.nameTextView.getPaint().measureText(this.nameTextView.getText().toString()) * 1.12f) {
                layoutParams2.width = (int) Math.ceil((double) (ceil / 1.12f));
            } else {
                layoutParams2.width = -2;
            }
            this.nameTextView.setLayoutParams(layoutParams2);
            this.initialAnimationExtraHeight = AndroidUtilities.dp(88.0f);
            setAnimationProgress(0.0f);
            arrayList = new ArrayList();
            arrayList.add(ObjectAnimator.ofFloat(this, str, new float[]{0.0f, 1.0f}));
            imageView = this.writeButton;
            if (imageView != null) {
                imageView.setScaleX(0.2f);
                this.writeButton.setScaleY(0.2f);
                this.writeButton.setAlpha(0.0f);
                arrayList.add(ObjectAnimator.ofFloat(this.writeButton, View.SCALE_X, new float[]{1.0f}));
                arrayList.add(ObjectAnimator.ofFloat(this.writeButton, View.SCALE_Y, new float[]{1.0f}));
                arrayList.add(ObjectAnimator.ofFloat(this.writeButton, View.ALPHA, new float[]{1.0f}));
            }
            arrayList.add(ObjectAnimator.ofFloat(this.onlineTextView, View.ALPHA, new float[]{0.0f, 1.0f}));
            arrayList.add(ObjectAnimator.ofFloat(this.nameTextView, View.ALPHA, new float[]{0.0f, 1.0f}));
            this.searchItem.setTranslationX((float) AndroidUtilities.dp(48.0f));
            this.otherItem.setTranslationX((float) AndroidUtilities.dp(48.0f));
            arrayList.add(ObjectAnimator.ofFloat(this.searchItem, View.TRANSLATION_X, new float[]{0.0f}));
            arrayList.add(ObjectAnimator.ofFloat(this.otherItem, View.TRANSLATION_X, new float[]{0.0f}));
            animatorSet.playTogether(arrayList);
        } else {
            this.initialAnimationExtraHeight = this.extraHeight;
            arrayList = new ArrayList();
            arrayList.add(ObjectAnimator.ofFloat(this, str, new float[]{1.0f, 0.0f}));
            imageView = this.writeButton;
            if (imageView != null) {
                arrayList.add(ObjectAnimator.ofFloat(imageView, View.SCALE_X, new float[]{0.2f}));
                arrayList.add(ObjectAnimator.ofFloat(this.writeButton, View.SCALE_Y, new float[]{0.2f}));
                arrayList.add(ObjectAnimator.ofFloat(this.writeButton, View.ALPHA, new float[]{0.0f}));
            }
            arrayList.add(ObjectAnimator.ofFloat(this.onlineTextView, View.ALPHA, new float[]{0.0f}));
            arrayList.add(ObjectAnimator.ofFloat(this.nameTextView, View.ALPHA, new float[]{0.0f}));
            arrayList.add(ObjectAnimator.ofFloat(this.searchItem, View.TRANSLATION_X, new float[]{(float) AndroidUtilities.dp(48.0f)}));
            arrayList.add(ObjectAnimator.ofFloat(this.otherItem, View.TRANSLATION_X, new float[]{(float) AndroidUtilities.dp(48.0f)}));
            animatorSet.playTogether(arrayList);
        }
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                SettingsActivity.this.listView.setLayerType(0, null);
                runnable.run();
            }
        });
        animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        animatorSet.getClass();
        AndroidUtilities.runOnUIThread(new -$$Lambda$V1ckApGFHcFeYW_JU7RAm0ElIv8(animatorSet), 50);
        return animatorSet;
    }

    public void setPlayProfileAnimation(boolean z) {
        SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
        if (!AndroidUtilities.isTablet() && globalMainSettings.getBoolean("view_animations", true)) {
            this.playProfileAnimation = z;
        }
    }

    private void checkListViewScroll() {
        if (this.listView.getVisibility() == 0 && this.listView.getChildCount() > 0 && !this.openAnimationInProgress && this.writeButton.getVisibility() == 0) {
            View childAt = this.listView.getChildAt(0);
            Holder holder = (Holder) this.listView.findContainingViewHolder(childAt);
            int top = childAt.getTop();
            if (top < 0 || holder == null || holder.getAdapterPosition() != 0) {
                top = 0;
            }
            if (this.extraHeight != top) {
                this.extraHeight = top;
                this.topView.invalidate();
                needLayout();
            }
        }
    }

    private void needLayout() {
        int i = 0;
        int currentActionBarHeight = (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
        RecyclerListView recyclerListView = this.listView;
        if (!(recyclerListView == null || this.openAnimationInProgress)) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) recyclerListView.getLayoutParams();
            if (layoutParams.topMargin != currentActionBarHeight) {
                layoutParams.topMargin = currentActionBarHeight;
                this.listView.setLayoutParams(layoutParams);
                this.searchListView.setLayoutParams(layoutParams);
            }
        }
        if (this.avatarContainer != null) {
            float dp = ((float) this.extraHeight) / ((float) AndroidUtilities.dp(88.0f));
            this.listView.setTopGlowOffset(this.extraHeight);
            ImageView imageView = this.writeButton;
            if (imageView != null) {
                imageView.setTranslationY((float) (((((this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight()) + this.extraHeight) + this.searchTransitionOffset) - AndroidUtilities.dp(29.5f)));
                if (!this.openAnimationInProgress) {
                    Object obj = (dp <= 0.2f || this.searchMode) ? null : 1;
                    if (obj != (this.writeButton.getTag() == null ? 1 : null)) {
                        if (obj != null) {
                            this.writeButton.setTag(null);
                        } else {
                            this.writeButton.setTag(Integer.valueOf(0));
                        }
                        AnimatorSet animatorSet = this.writeButtonAnimation;
                        if (animatorSet != null) {
                            this.writeButtonAnimation = null;
                            animatorSet.cancel();
                        }
                        this.writeButtonAnimation = new AnimatorSet();
                        if (obj != null) {
                            this.writeButtonAnimation.setInterpolator(new DecelerateInterpolator());
                            AnimatorSet animatorSet2 = this.writeButtonAnimation;
                            Animator[] animatorArr = new Animator[3];
                            animatorArr[0] = ObjectAnimator.ofFloat(this.writeButton, View.SCALE_X, new float[]{1.0f});
                            animatorArr[1] = ObjectAnimator.ofFloat(this.writeButton, View.SCALE_Y, new float[]{1.0f});
                            animatorArr[2] = ObjectAnimator.ofFloat(this.writeButton, View.ALPHA, new float[]{1.0f});
                            animatorSet2.playTogether(animatorArr);
                        } else {
                            this.writeButtonAnimation.setInterpolator(new AccelerateInterpolator());
                            AnimatorSet animatorSet3 = this.writeButtonAnimation;
                            Animator[] animatorArr2 = new Animator[3];
                            animatorArr2[0] = ObjectAnimator.ofFloat(this.writeButton, View.SCALE_X, new float[]{0.2f});
                            animatorArr2[1] = ObjectAnimator.ofFloat(this.writeButton, View.SCALE_Y, new float[]{0.2f});
                            animatorArr2[2] = ObjectAnimator.ofFloat(this.writeButton, View.ALPHA, new float[]{0.0f});
                            animatorSet3.playTogether(animatorArr2);
                        }
                        this.writeButtonAnimation.setDuration(150);
                        this.writeButtonAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                if (SettingsActivity.this.writeButtonAnimation != null && SettingsActivity.this.writeButtonAnimation.equals(animator)) {
                                    SettingsActivity.this.writeButtonAnimation = null;
                                }
                            }
                        });
                        this.writeButtonAnimation.start();
                    }
                }
            }
            if (this.actionBar.getOccupyStatusBar()) {
                i = AndroidUtilities.statusBarHeight;
            }
            float currentActionBarHeight2 = ((float) i) + ((((float) ActionBar.getCurrentActionBarHeight()) / 2.0f) * (dp + 1.0f));
            float f = AndroidUtilities.density;
            currentActionBarHeight2 = (currentActionBarHeight2 - (21.0f * f)) + ((f * 27.0f) * dp);
            f = ((18.0f * dp) + 42.0f) / 42.0f;
            this.avatarContainer.setScaleX(f);
            this.avatarContainer.setScaleY(f);
            this.avatarContainer.setTranslationX(((float) (-AndroidUtilities.dp(47.0f))) * dp);
            double d = (double) currentActionBarHeight2;
            this.avatarContainer.setTranslationY((float) Math.ceil(d));
            TextView textView = this.nameTextView;
            if (textView != null) {
                textView.setTranslationX((AndroidUtilities.density * -21.0f) * dp);
                this.onlineTextView.setTranslationX((AndroidUtilities.density * -21.0f) * dp);
                this.nameTextView.setTranslationY((((float) Math.floor(d)) - ((float) Math.ceil((double) AndroidUtilities.density))) + ((float) Math.floor((double) ((AndroidUtilities.density * 7.0f) * dp))));
                this.onlineTextView.setTranslationY((((float) Math.floor(d)) + ((float) AndroidUtilities.dp(22.0f))) + (((float) Math.floor((double) (AndroidUtilities.density * 11.0f))) * dp));
                currentActionBarHeight2 = (0.12f * dp) + 1.0f;
                this.nameTextView.setScaleX(currentActionBarHeight2);
                this.nameTextView.setScaleY(currentActionBarHeight2);
                if (!this.openAnimationInProgress) {
                    int dp2;
                    if (AndroidUtilities.isTablet()) {
                        dp2 = AndroidUtilities.dp(490.0f);
                    } else {
                        dp2 = AndroidUtilities.displaySize.x;
                    }
                    int dp3 = AndroidUtilities.dp(214.0f);
                    int i2 = dp2 - dp3;
                    float f2 = (float) dp2;
                    int max = (int) ((f2 - (((float) dp3) * Math.max(0.0f, 1.0f - (dp != 1.0f ? (0.15f * dp) / (1.0f - dp) : 1.0f)))) - this.nameTextView.getTranslationX());
                    f = this.nameTextView.getPaint().measureText(this.nameTextView.getText().toString()) * currentActionBarHeight2;
                    FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.nameTextView.getLayoutParams();
                    float f3 = (float) max;
                    if (f3 < f) {
                        layoutParams2.width = Math.max(i2, (int) Math.ceil((double) (((float) (max - AndroidUtilities.dp(24.0f))) / (((1.12f - currentActionBarHeight2) * 7.0f) + currentActionBarHeight2))));
                    } else {
                        layoutParams2.width = (int) Math.ceil((double) f);
                    }
                    layoutParams2.width = (int) Math.min(((f2 - this.nameTextView.getX()) / currentActionBarHeight2) - ((float) AndroidUtilities.dp(8.0f)), (float) layoutParams2.width);
                    this.nameTextView.setLayoutParams(layoutParams2);
                    f2 = this.onlineTextView.getPaint().measureText(this.onlineTextView.getText().toString());
                    FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.onlineTextView.getLayoutParams();
                    layoutParams3.rightMargin = (int) Math.ceil((double) ((this.onlineTextView.getTranslationX() + ((float) AndroidUtilities.dp(8.0f))) + (((float) AndroidUtilities.dp(40.0f)) * (1.0f - dp))));
                    if (f3 < f2) {
                        layoutParams3.width = (int) Math.ceil((double) max);
                    } else {
                        layoutParams3.width = -2;
                    }
                    this.onlineTextView.setLayoutParams(layoutParams3);
                }
            }
        }
    }

    private void fixLayout() {
        View view = this.fragmentView;
        if (view != null) {
            view.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    if (SettingsActivity.this.fragmentView != null) {
                        SettingsActivity.this.checkListViewScroll();
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
                    textCell.setOnClickListener(new -$$Lambda$SettingsActivity$ymyOkTRkh0LvSaq01cGQ7tyGrVk(this, builder));
                }
                i++;
            }
            builder.setCustomView(linearLayout);
            showDialog(builder.create());
        }
    }

    public /* synthetic */ void lambda$showHelpAlert$12$SettingsActivity(BottomSheet.Builder builder, View view) {
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
                builder2.setPositiveButton(LocaleController.getString("OK", NUM), new -$$Lambda$SettingsActivity$hppFosdWcqMafwCtAbJwe8DFGSk(this));
                builder2.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                showDialog(builder2.create());
            } else {
                return;
            }
        }
        builder.getDismissRunnable().run();
    }

    public /* synthetic */ void lambda$null$11$SettingsActivity(DialogInterface dialogInterface, int i) {
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
            Utilities.globalQueue.postRunnable(new -$$Lambda$SettingsActivity$AbOnJ0B3GfE6DY2J5Cpn_hOtbqY(this, alertDialog));
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
    public /* synthetic */ void lambda$sendLogs$14$SettingsActivity(org.telegram.ui.ActionBar.AlertDialog r13) {
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
        r1 = new org.telegram.ui.-$$Lambda$SettingsActivity$ta33HFzGvXvJ5GbSKWlvVm4YS9M;	 Catch:{ Exception -> 0x00b6 }
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.SettingsActivity.lambda$sendLogs$14$SettingsActivity(org.telegram.ui.ActionBar.AlertDialog):void");
    }

    public /* synthetic */ void lambda$null$13$SettingsActivity(AlertDialog alertDialog, boolean[] zArr, File file) {
        try {
            alertDialog.dismiss();
        } catch (Exception unused) {
        }
        if (zArr[0]) {
            Parcelable uriForFile;
            if (VERSION.SDK_INT >= 24) {
                uriForFile = FileProvider.getUriForFile(getParentActivity(), "org.telegram.messenger.beta.provider", file);
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
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[37];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, 0, null, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, 0, null, null, null, null, "windowBackgroundGray");
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "avatar_backgroundActionBarBlue");
        themeDescriptionArr[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "avatar_backgroundActionBarBlue");
        themeDescriptionArr[4] = new ThemeDescription(this.topView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "avatar_backgroundActionBarBlue");
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "avatar_actionBarIconBlue");
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        themeDescriptionArr[7] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "avatar_actionBarSelectorBlue");
        themeDescriptionArr[8] = new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "profile_title");
        themeDescriptionArr[9] = new ThemeDescription(this.onlineTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "profile_status");
        themeDescriptionArr[10] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, "actionBarDefaultSubmenuBackground");
        themeDescriptionArr[11] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, "actionBarDefaultSubmenuItem");
        themeDescriptionArr[12] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "actionBarDefaultSubmenuItemIcon");
        themeDescriptionArr[13] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        themeDescriptionArr[14] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        themeDescriptionArr[15] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        themeDescriptionArr[16] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGray");
        View view = this.listView;
        Class[] clsArr = new Class[]{TextCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        themeDescriptionArr[17] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteBlackText");
        View view2 = this.listView;
        Class[] clsArr2 = new Class[]{TextCell.class};
        String[] strArr2 = new String[1];
        strArr2[0] = "valueTextView";
        themeDescriptionArr[18] = new ThemeDescription(view2, 0, clsArr2, strArr2, null, null, null, "windowBackgroundWhiteValueText");
        View view3 = this.listView;
        Class[] clsArr3 = new Class[]{TextCell.class};
        String[] strArr3 = new String[1];
        strArr3[0] = "imageView";
        themeDescriptionArr[19] = new ThemeDescription(view3, 0, clsArr3, strArr3, null, null, null, "windowBackgroundWhiteGrayIcon");
        themeDescriptionArr[20] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueHeader");
        themeDescriptionArr[21] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[22] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        themeDescriptionArr[23] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        themeDescriptionArr[24] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGray");
        themeDescriptionArr[25] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText4");
        themeDescriptionArr[26] = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[]{Theme.avatar_savedDrawable}, null, "avatar_text");
        themeDescriptionArr[27] = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[]{this.avatarDrawable}, null, "avatar_backgroundInProfileBlue");
        themeDescriptionArr[28] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "profile_actionIcon");
        themeDescriptionArr[29] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "profile_actionBackground");
        themeDescriptionArr[30] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "profile_actionPressedBackground");
        themeDescriptionArr[31] = new ThemeDescription(this.searchListView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueHeader");
        themeDescriptionArr[32] = new ThemeDescription(this.searchListView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, null, null, null, "key_graySectionText");
        themeDescriptionArr[33] = new ThemeDescription(this.searchListView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, "graySection");
        themeDescriptionArr[34] = new ThemeDescription(this.searchListView, 0, new Class[]{SettingsSearchCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[35] = new ThemeDescription(this.searchListView, 0, new Class[]{SettingsSearchCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        themeDescriptionArr[36] = new ThemeDescription(this.searchListView, 0, new Class[]{SettingsSearchCell.class}, new String[]{"imageView"}, null, null, null, "windowBackgroundWhiteGrayIcon");
        return themeDescriptionArr;
    }
}
