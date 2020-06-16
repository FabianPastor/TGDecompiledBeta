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
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.MovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Keep;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$InputFile;
import org.telegram.tgnet.TLRPC$InputPhoto;
import org.telegram.tgnet.TLRPC$Page;
import org.telegram.tgnet.TLRPC$PageBlock;
import org.telegram.tgnet.TLRPC$PageListItem;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_getWebPage;
import org.telegram.tgnet.TLRPC$TL_pageBlockAnchor;
import org.telegram.tgnet.TLRPC$TL_pageBlockList;
import org.telegram.tgnet.TLRPC$TL_pageBlockParagraph;
import org.telegram.tgnet.TLRPC$TL_pageListItemText;
import org.telegram.tgnet.TLRPC$TL_photos_photo;
import org.telegram.tgnet.TLRPC$TL_photos_uploadProfilePhoto;
import org.telegram.tgnet.TLRPC$TL_userProfilePhoto;
import org.telegram.tgnet.TLRPC$TL_userProfilePhotoEmpty;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserFull;
import org.telegram.tgnet.TLRPC$UserProfilePhoto;
import org.telegram.tgnet.TLRPC$WebPage;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
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
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.SettingsActivity;

public class SettingsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, ImageUpdater.ImageUpdaterDelegate {
    private boolean allowProfileAnimation = true;
    private float animationProgress;
    private TLRPC$FileLocation avatar;
    /* access modifiers changed from: private */
    public AnimatorSet avatarAnimation;
    private TLRPC$FileLocation avatarBig;
    /* access modifiers changed from: private */
    public FrameLayout avatarContainer;
    private AvatarDrawable avatarDrawable;
    /* access modifiers changed from: private */
    public BackupImageView avatarImage;
    /* access modifiers changed from: private */
    public RadialProgressView avatarProgressView;
    /* access modifiers changed from: private */
    public int bioRow;
    /* access modifiers changed from: private */
    public int chatRow;
    /* access modifiers changed from: private */
    public int clearLogsRow;
    /* access modifiers changed from: private */
    public String currentBio;
    /* access modifiers changed from: private */
    public int dataRow;
    /* access modifiers changed from: private */
    public int debugHeaderRow;
    /* access modifiers changed from: private */
    public int devicesRow;
    /* access modifiers changed from: private */
    public int devicesSectionRow;
    /* access modifiers changed from: private */
    public int emptyRow;
    /* access modifiers changed from: private */
    public EmptyTextProgressView emptyView;
    /* access modifiers changed from: private */
    public int extraHeight;
    /* access modifiers changed from: private */
    public int faqRow;
    /* access modifiers changed from: private */
    public int filtersRow;
    /* access modifiers changed from: private */
    public int helpHeaderRow;
    /* access modifiers changed from: private */
    public int helpSectionCell;
    private ImageUpdater imageUpdater;
    private int initialAnimationExtraHeight;
    /* access modifiers changed from: private */
    public int languageRow;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    private TextView nameTextView;
    /* access modifiers changed from: private */
    public int notificationRow;
    /* access modifiers changed from: private */
    public int numberRow;
    /* access modifiers changed from: private */
    public int numberSectionRow;
    private TextView onlineTextView;
    private boolean openAnimationInProgress;
    private ActionBarMenuItem otherItem;
    private boolean playProfileAnimation;
    /* access modifiers changed from: private */
    public int policyRow;
    /* access modifiers changed from: private */
    public int privacyRow;
    private PhotoViewer.PhotoViewerProvider provider = new PhotoViewer.EmptyPhotoViewerProvider() {
        public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC$FileLocation tLRPC$FileLocation, int i, boolean z) {
            TLRPC$User user;
            TLRPC$UserProfilePhoto tLRPC$UserProfilePhoto;
            TLRPC$FileLocation tLRPC$FileLocation2;
            if (tLRPC$FileLocation == null || (user = MessagesController.getInstance(SettingsActivity.this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(SettingsActivity.this.currentAccount).getClientUserId()))) == null || (tLRPC$UserProfilePhoto = user.photo) == null || (tLRPC$FileLocation2 = tLRPC$UserProfilePhoto.photo_big) == null || tLRPC$FileLocation2.local_id != tLRPC$FileLocation.local_id || tLRPC$FileLocation2.volume_id != tLRPC$FileLocation.volume_id || tLRPC$FileLocation2.dc_id != tLRPC$FileLocation.dc_id) {
                return null;
            }
            int[] iArr = new int[2];
            SettingsActivity.this.avatarImage.getLocationInWindow(iArr);
            PhotoViewer.PlaceProviderObject placeProviderObject = new PhotoViewer.PlaceProviderObject();
            int i2 = 0;
            placeProviderObject.viewX = iArr[0];
            int i3 = iArr[1];
            if (Build.VERSION.SDK_INT < 21) {
                i2 = AndroidUtilities.statusBarHeight;
            }
            placeProviderObject.viewY = i3 - i2;
            placeProviderObject.parentView = SettingsActivity.this.avatarImage;
            placeProviderObject.imageReceiver = SettingsActivity.this.avatarImage.getImageReceiver();
            placeProviderObject.dialogId = UserConfig.getInstance(SettingsActivity.this.currentAccount).getClientUserId();
            placeProviderObject.thumb = placeProviderObject.imageReceiver.getBitmapSafe();
            placeProviderObject.size = -1;
            placeProviderObject.radius = SettingsActivity.this.avatarImage.getImageReceiver().getRoundRadius();
            placeProviderObject.scale = SettingsActivity.this.avatarContainer.getScaleX();
            return placeProviderObject;
        }

        public void willHidePhotoViewer() {
            SettingsActivity.this.avatarImage.getImageReceiver().setVisible(true, true);
        }
    };
    /* access modifiers changed from: private */
    public int questionRow;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public SearchAdapter searchAdapter;
    /* access modifiers changed from: private */
    public ActionBarMenuItem searchItem;
    /* access modifiers changed from: private */
    public RecyclerListView searchListView;
    /* access modifiers changed from: private */
    public boolean searchMode;
    /* access modifiers changed from: private */
    public int searchTransitionOffset;
    private float searchTransitionProgress;
    private Animator searchViewTransition;
    /* access modifiers changed from: private */
    public int sendLogsRow;
    /* access modifiers changed from: private */
    public int settingsSectionRow;
    /* access modifiers changed from: private */
    public int settingsSectionRow2;
    /* access modifiers changed from: private */
    public int switchBackendRow;
    /* access modifiers changed from: private */
    public SimpleTextView titleTextView;
    private TopView topView;
    private int transitionIndex;
    private final Interpolator transitionInterpolator = new DecelerateInterpolator();
    /* access modifiers changed from: private */
    public TLRPC$UserFull userInfo;
    /* access modifiers changed from: private */
    public int usernameRow;
    /* access modifiers changed from: private */
    public int versionRow;
    private ImageView writeButton;
    /* access modifiers changed from: private */
    public AnimatorSet writeButtonAnimation;

    public /* synthetic */ String getInitialSearchString() {
        return ImageUpdater.ImageUpdaterDelegate.CC.$default$getInitialSearchString(this);
    }

    private class TopView extends View {
        private int currentColor;
        private Paint paint = new Paint();

        public TopView(Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            setMeasuredDimension(View.MeasureSpec.getSize(i), ActionBar.getCurrentActionBarHeight() + (SettingsActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + AndroidUtilities.dp(91.0f));
        }

        public void setBackgroundColor(int i) {
            if (i != this.currentColor) {
                this.paint.setColor(i);
                invalidate();
            }
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            int measuredHeight = getMeasuredHeight() - AndroidUtilities.dp(91.0f);
            canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) (SettingsActivity.this.extraHeight + measuredHeight + SettingsActivity.this.searchTransitionOffset), this.paint);
            if (SettingsActivity.this.parentLayout != null) {
                SettingsActivity.this.parentLayout.drawHeaderShadow(canvas, measuredHeight + SettingsActivity.this.extraHeight + SettingsActivity.this.searchTransitionOffset);
            }
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.hasOwnBackground = true;
        ImageUpdater imageUpdater2 = new ImageUpdater();
        this.imageUpdater = imageUpdater2;
        imageUpdater2.parentFragment = this;
        imageUpdater2.delegate = this;
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.userInfoDidLoad);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
        updateRows();
        getMediaDataController().checkFeaturedStickers();
        getMessagesController().loadSuggestedFilters();
        this.userInfo = getMessagesController().getUserFull(UserConfig.getInstance(this.currentAccount).getClientUserId());
        getMessagesController().loadUserInfo(UserConfig.getInstance(this.currentAccount).getCurrentUser(), true, this.classGuid);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        BackupImageView backupImageView = this.avatarImage;
        if (backupImageView != null) {
            backupImageView.setImageDrawable((Drawable) null);
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.userInfoDidLoad);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
        this.imageUpdater.clear();
    }

    /* access modifiers changed from: protected */
    public ActionBar createActionBar(Context context) {
        ActionBar actionBar = new ActionBar(context);
        boolean z = false;
        actionBar.setItemsBackgroundColor(Theme.getColor("avatar_actionBarSelectorBlue"), false);
        actionBar.setItemsColor(Theme.getColor("avatar_actionBarIconBlue"), false);
        actionBar.setBackButtonImage(NUM);
        actionBar.setCastShadows(false);
        actionBar.setAddToContainer(false);
        if (Build.VERSION.SDK_INT >= 21 && !AndroidUtilities.isTablet()) {
            z = true;
        }
        actionBar.setOccupyStatusBar(z);
        return actionBar;
    }

    public View createView(Context context) {
        Object obj;
        int i;
        int i2;
        int i3;
        Context context2 = context;
        this.extraHeight = AndroidUtilities.dp(88.0f);
        this.searchTransitionOffset = 0;
        this.searchTransitionProgress = 1.0f;
        this.searchMode = false;
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
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
        ActionBarMenuItem addItem = createMenu.addItem(3, NUM);
        addItem.setIsSearchField(true);
        addItem.setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            public Animator getCustomToggleTransition() {
                SettingsActivity settingsActivity = SettingsActivity.this;
                boolean unused = settingsActivity.searchMode = !settingsActivity.searchMode;
                if (SettingsActivity.this.searchMode) {
                    SettingsActivity.this.searchAdapter.loadFaqWebPage();
                } else {
                    SettingsActivity.this.searchItem.clearFocusOnSearchView();
                }
                SettingsActivity settingsActivity2 = SettingsActivity.this;
                return settingsActivity2.searchExpandTransition(settingsActivity2.searchMode);
            }

            public void onTextChanged(EditText editText) {
                SettingsActivity.this.searchAdapter.search(editText.getText().toString().toLowerCase());
            }
        });
        this.searchItem = addItem;
        addItem.setContentDescription(LocaleController.getString("SearchInSettings", NUM));
        this.searchItem.setSearchFieldHint(LocaleController.getString("SearchInSettings", NUM));
        ActionBarMenuItem addItem2 = createMenu.addItem(0, NUM);
        this.otherItem = addItem2;
        addItem2.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
        this.otherItem.addSubItem(1, NUM, LocaleController.getString("EditName", NUM));
        this.otherItem.addSubItem(2, NUM, LocaleController.getString("LogOut", NUM));
        if (this.listView != null) {
            i2 = this.layoutManager.findFirstVisibleItemPosition();
            View findViewByPosition = this.layoutManager.findViewByPosition(i2);
            if (findViewByPosition != null) {
                i = findViewByPosition.getTop();
            } else {
                i2 = -1;
                i = 0;
            }
            obj = this.writeButton.getTag();
        } else {
            obj = null;
            i2 = -1;
            i = 0;
        }
        this.listAdapter = new ListAdapter(context2);
        this.searchAdapter = new SearchAdapter(this, context2);
        AnonymousClass4 r12 = new FrameLayout(context2) {
            private Paint paint = new Paint();

            public boolean hasOverlappingRendering() {
                return false;
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                SettingsActivity.this.titleTextView.setTextSize((AndroidUtilities.isTablet() || getContext().getResources().getConfiguration().orientation != 2) ? 20 : 18);
                super.onMeasure(i, i2);
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                if (SettingsActivity.this.titleTextView != null) {
                    int dp = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 80.0f : 72.0f);
                    int currentActionBarHeight = ((ActionBar.getCurrentActionBarHeight() - SettingsActivity.this.titleTextView.getTextHeight()) / 2) + ((Build.VERSION.SDK_INT < 21 || AndroidUtilities.isTablet()) ? 0 : AndroidUtilities.statusBarHeight);
                    SettingsActivity.this.titleTextView.layout(dp, currentActionBarHeight, SettingsActivity.this.titleTextView.getMeasuredWidth() + dp, SettingsActivity.this.titleTextView.getTextHeight() + currentActionBarHeight);
                }
                SettingsActivity.this.checkListViewScroll();
            }

            public void onDraw(Canvas canvas) {
                this.paint.setColor(Theme.getColor("windowBackgroundWhite"));
                RecyclerListView access$1700 = SettingsActivity.this.listView.getVisibility() == 0 ? SettingsActivity.this.listView : SettingsActivity.this.searchListView;
                canvas.drawRect((float) access$1700.getLeft(), (float) (access$1700.getTop() + SettingsActivity.this.extraHeight + SettingsActivity.this.searchTransitionOffset), (float) access$1700.getRight(), (float) access$1700.getBottom(), this.paint);
            }
        };
        this.fragmentView = r12;
        r12.setWillNotDraw(false);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        AnonymousClass5 r13 = new RecyclerListView(this, context2) {
            public boolean hasOverlappingRendering() {
                return false;
            }
        };
        this.listView = r13;
        r13.setHideIfEmpty(false);
        this.listView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView = this.listView;
        AnonymousClass6 r14 = new LinearLayoutManager(this, context2, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = r14;
        recyclerListView.setLayoutManager(r14);
        this.listView.setGlowColor(Theme.getColor("avatar_backgroundActionBarBlue"));
        this.listView.setPadding(0, AndroidUtilities.dp(88.0f), 0, 0);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setItemAnimator((RecyclerView.ItemAnimator) null);
        this.listView.setLayoutAnimation((LayoutAnimationController) null);
        this.listView.setClipToPadding(false);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                SettingsActivity.this.lambda$createView$1$SettingsActivity(view, i);
            }
        });
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener() {
            private int pressCount = 0;

            public boolean onItemClick(View view, int i) {
                String str;
                int i2;
                String str2;
                int i3;
                String str3;
                int i4;
                String str4;
                String str5;
                int i5;
                if (i != SettingsActivity.this.versionRow) {
                    return false;
                }
                int i6 = this.pressCount + 1;
                this.pressCount = i6;
                if (i6 >= 2 || BuildVars.DEBUG_PRIVATE_VERSION) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) SettingsActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("DebugMenu", NUM));
                    CharSequence[] charSequenceArr = new CharSequence[13];
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
                        i3 = NUM;
                        str2 = "DebugMenuDisableCamera";
                    } else {
                        i3 = NUM;
                        str2 = "DebugMenuEnableCamera";
                    }
                    charSequenceArr[5] = LocaleController.getString(str2, i3);
                    charSequenceArr[6] = LocaleController.getString("DebugMenuClearMediaCache", NUM);
                    charSequenceArr[7] = LocaleController.getString("DebugMenuCallSettings", NUM);
                    charSequenceArr[8] = null;
                    charSequenceArr[9] = BuildVars.DEBUG_PRIVATE_VERSION ? "Check for app updates" : null;
                    charSequenceArr[10] = LocaleController.getString("DebugMenuReadAllDialogs", NUM);
                    if (SharedConfig.pauseMusicOnRecord) {
                        i4 = NUM;
                        str3 = "DebugMenuDisablePauseMusic";
                    } else {
                        i4 = NUM;
                        str3 = "DebugMenuEnablePauseMusic";
                    }
                    charSequenceArr[11] = LocaleController.getString(str3, i4);
                    if (!BuildVars.DEBUG_VERSION || AndroidUtilities.isTablet() || Build.VERSION.SDK_INT < 23) {
                        str4 = null;
                    } else {
                        if (SharedConfig.smoothKeyboard) {
                            i5 = NUM;
                            str5 = "DebugMenuDisableSmoothKeyboard";
                        } else {
                            i5 = NUM;
                            str5 = "DebugMenuEnableSmoothKeyboard";
                        }
                        str4 = LocaleController.getString(str5, i5);
                    }
                    charSequenceArr[12] = str4;
                    builder.setItems(charSequenceArr, new DialogInterface.OnClickListener() {
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            SettingsActivity.AnonymousClass7.this.lambda$onItemClick$0$SettingsActivity$7(dialogInterface, i);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    SettingsActivity.this.showDialog(builder.create());
                } else {
                    try {
                        Toast.makeText(SettingsActivity.this.getParentActivity(), "¯\\_(ツ)_/¯", 0).show();
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
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
                    BuildVars.LOGS_ENABLED = true ^ BuildVars.LOGS_ENABLED;
                    ApplicationLoader.applicationContext.getSharedPreferences("systemConfig", 0).edit().putBoolean("logsEnabled", BuildVars.LOGS_ENABLED).commit();
                    SettingsActivity.this.updateRows();
                } else if (i == 5) {
                    SharedConfig.toggleInappCamera();
                } else if (i == 6) {
                    MessagesStorage.getInstance(SettingsActivity.this.currentAccount).clearSentMedia();
                    SharedConfig.setNoSoundHintShowed(false);
                    MessagesController.getGlobalMainSettings().edit().remove("archivehint").remove("archivehint_l").remove("gifhint").remove("soundHint").remove("themehint").remove("filterhint").commit();
                    SharedConfig.textSelectionHintShows = 0;
                    SharedConfig.lockRecordAudioVideoHint = 0;
                    SharedConfig.stickersReorderingHintUsed = false;
                } else if (i == 7) {
                    VoIPHelper.showCallDebugSettings(SettingsActivity.this.getParentActivity());
                } else if (i == 8) {
                    SharedConfig.toggleRoundCamera16to9();
                } else if (i == 9) {
                    ((LaunchActivity) SettingsActivity.this.getParentActivity()).checkAppUpdate(true);
                } else if (i == 10) {
                    MessagesStorage.getInstance(SettingsActivity.this.currentAccount).readAllDialogs(-1);
                } else if (i == 11) {
                    SharedConfig.togglePauseMusicOnRecord();
                } else if (i == 12) {
                    SharedConfig.toggleSmoothKeyboard();
                    if (SharedConfig.smoothKeyboard && SettingsActivity.this.getParentActivity() != null) {
                        SettingsActivity.this.getParentActivity().getWindow().setSoftInputMode(32);
                    }
                }
            }
        });
        AnonymousClass8 r2 = new RecyclerListView(this, context2) {
            public boolean hasOverlappingRendering() {
                return false;
            }
        };
        this.searchListView = r2;
        r2.setVerticalScrollBarEnabled(false);
        this.searchListView.setLayoutManager(new LinearLayoutManager(context2, 1, false));
        this.searchListView.setGlowColor(Theme.getColor("avatar_backgroundActionBarBlue"));
        frameLayout.addView(this.searchListView, LayoutHelper.createFrame(-1, -1, 51));
        this.searchListView.setAdapter(this.searchAdapter);
        this.searchListView.setItemAnimator((RecyclerView.ItemAnimator) null);
        this.searchListView.setLayoutAnimation((LayoutAnimationController) null);
        this.searchListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                SettingsActivity.this.lambda$createView$2$SettingsActivity(view, i);
            }
        });
        this.searchListView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener() {
            public final boolean onItemClick(View view, int i) {
                return SettingsActivity.this.lambda$createView$4$SettingsActivity(view, i);
            }
        });
        this.searchListView.setVisibility(8);
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context2);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.showTextView();
        this.emptyView.setTextSize(18);
        this.emptyView.setVisibility(8);
        this.emptyView.setShowAtCenter(true);
        this.emptyView.setPadding(0, AndroidUtilities.dp(50.0f), 0, 0);
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        TopView topView2 = new TopView(context2);
        this.topView = topView2;
        topView2.setBackgroundColor(Theme.getColor("avatar_backgroundActionBarBlue"));
        frameLayout.addView(this.topView);
        frameLayout.addView(this.actionBar);
        FrameLayout frameLayout2 = new FrameLayout(context2);
        this.avatarContainer = frameLayout2;
        frameLayout2.setPivotX(0.0f);
        this.avatarContainer.setPivotY(0.0f);
        frameLayout.addView(this.avatarContainer, LayoutHelper.createFrame(42, 42.0f, 51, 64.0f, 0.0f, 0.0f, 0.0f));
        this.avatarContainer.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                SettingsActivity.this.lambda$createView$5$SettingsActivity(view);
            }
        });
        BackupImageView backupImageView = new BackupImageView(context2);
        this.avatarImage = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(21.0f));
        this.avatarImage.setContentDescription(LocaleController.getString("AccDescrProfilePicture", NUM));
        this.avatarContainer.addView(this.avatarImage, LayoutHelper.createFrame(42, 42.0f));
        final Paint paint = new Paint(1);
        paint.setColor(NUM);
        AnonymousClass9 r132 = new RadialProgressView(context2) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                if (SettingsActivity.this.avatarImage != null && SettingsActivity.this.avatarImage.getImageReceiver().hasNotThumb()) {
                    paint.setAlpha((int) (SettingsActivity.this.avatarImage.getImageReceiver().getCurrentAlpha() * 85.0f));
                    canvas.drawCircle((float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2), (float) AndroidUtilities.dp(21.0f), paint);
                }
                super.onDraw(canvas);
            }
        };
        this.avatarProgressView = r132;
        r132.setSize(AndroidUtilities.dp(26.0f));
        this.avatarProgressView.setProgressColor(-1);
        this.avatarContainer.addView(this.avatarProgressView, LayoutHelper.createFrame(42, 42.0f));
        showAvatarProgress(false, false);
        SimpleTextView simpleTextView = new SimpleTextView(context2);
        this.titleTextView = simpleTextView;
        simpleTextView.setGravity(3);
        this.titleTextView.setTextColor(Theme.getColor("actionBarDefaultTitle"));
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.titleTextView.setText(BuildVars.DEBUG_VERSION ? "Telegram Beta" : LocaleController.getString("AppName", NUM));
        this.titleTextView.setAlpha(0.0f);
        frameLayout.addView(this.titleTextView, LayoutHelper.createFrame(-2, -2, 51));
        TextView textView = new TextView(context2);
        this.nameTextView = textView;
        textView.setTextColor(Theme.getColor("profile_title"));
        this.nameTextView.setTextSize(1, 18.0f);
        this.nameTextView.setLines(1);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.nameTextView.setGravity(3);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setPivotX(0.0f);
        this.nameTextView.setPivotY(0.0f);
        frameLayout.addView(this.nameTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, 96.0f, 0.0f));
        TextView textView2 = new TextView(context2);
        this.onlineTextView = textView2;
        textView2.setTextColor(Theme.getColor("profile_status"));
        this.onlineTextView.setTextSize(1, 14.0f);
        this.onlineTextView.setLines(1);
        this.onlineTextView.setMaxLines(1);
        this.onlineTextView.setSingleLine(true);
        this.onlineTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.onlineTextView.setGravity(3);
        frameLayout.addView(this.onlineTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, 96.0f, 0.0f));
        this.writeButton = new ImageView(context2);
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("profile_actionBackground"), Theme.getColor("profile_actionPressedBackground"));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable mutate = context.getResources().getDrawable(NUM).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorCircleDrawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            createSimpleSelectorCircleDrawable = combinedDrawable;
        }
        this.writeButton.setBackgroundDrawable(createSimpleSelectorCircleDrawable);
        this.writeButton.setImageResource(NUM);
        this.writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("profile_actionIcon"), PorterDuff.Mode.MULTIPLY));
        this.writeButton.setScaleType(ImageView.ScaleType.CENTER);
        if (Build.VERSION.SDK_INT >= 21) {
            StateListAnimator stateListAnimator = new StateListAnimator();
            i3 = i;
            stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.writeButton, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(this.writeButton, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            this.writeButton.setStateListAnimator(stateListAnimator);
            this.writeButton.setOutlineProvider(new ViewOutlineProvider(this) {
                @SuppressLint({"NewApi"})
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        } else {
            i3 = i;
        }
        frameLayout.addView(this.writeButton, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56 : 60, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, 53, 0.0f, 0.0f, 16.0f, 0.0f));
        this.writeButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                SettingsActivity.this.lambda$createView$7$SettingsActivity(view);
            }
        });
        this.writeButton.setContentDescription(LocaleController.getString("AccDescrChangeProfilePicture", NUM));
        if (i2 != -1) {
            this.layoutManager.scrollToPositionWithOffset(i2, i3);
            if (obj != null) {
                this.writeButton.setTag(0);
                this.writeButton.setScaleX(0.2f);
                this.writeButton.setScaleY(0.2f);
                this.writeButton.setAlpha(0.0f);
            }
        }
        needLayout();
        this.searchListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1) {
                    AndroidUtilities.hideKeyboard(SettingsActivity.this.getParentActivity().getCurrentFocus());
                }
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                SettingsActivity.this.checkListViewScroll();
            }
        });
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$1$SettingsActivity(View view, int i) {
        if (i == this.notificationRow) {
            presentFragment(new NotificationsSettingsActivity());
        } else if (i == this.privacyRow) {
            presentFragment(new PrivacySettingsActivity());
        } else if (i == this.dataRow) {
            presentFragment(new DataSettingsActivity());
        } else if (i == this.chatRow) {
            presentFragment(new ThemeActivity(0));
        } else if (i == this.filtersRow) {
            presentFragment(new FiltersSetupActivity());
        } else if (i == this.devicesRow) {
            presentFragment(new SessionsActivity(0));
        } else if (i == this.questionRow) {
            showDialog(AlertsCreator.createSupportAlert(this));
        } else if (i == this.faqRow) {
            Browser.openUrl((Context) getParentActivity(), LocaleController.getString("TelegramFaqUrl", NUM));
        } else if (i == this.policyRow) {
            Browser.openUrl((Context) getParentActivity(), LocaleController.getString("PrivacyPolicyUrl", NUM));
        } else if (i == this.sendLogsRow) {
            sendLogs();
        } else if (i == this.clearLogsRow) {
            FileLog.cleanupLogs();
        } else if (i == this.switchBackendRow) {
            if (getParentActivity() != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setMessage(LocaleController.getString("AreYouSure", NUM));
                builder.setTitle(LocaleController.getString("AppName", NUM));
                builder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        SettingsActivity.this.lambda$null$0$SettingsActivity(dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                showDialog(builder.create());
            }
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

    public /* synthetic */ void lambda$null$0$SettingsActivity(DialogInterface dialogInterface, int i) {
        SharedConfig.pushAuthKey = null;
        SharedConfig.pushAuthKeyId = null;
        SharedConfig.saveConfig();
        ConnectionsManager.getInstance(this.currentAccount).switchBackend();
    }

    public /* synthetic */ void lambda$createView$2$SettingsActivity(View view, int i) {
        if (i >= 0) {
            Object valueOf = Integer.valueOf(this.numberRow);
            if (!this.searchAdapter.searchWas) {
                int i2 = i - 1;
                if (i2 >= 0) {
                    if (i2 < this.searchAdapter.recentSearches.size()) {
                        valueOf = this.searchAdapter.recentSearches.get(i2);
                    }
                } else {
                    return;
                }
            } else if (i < this.searchAdapter.searchResults.size()) {
                valueOf = this.searchAdapter.searchResults.get(i);
            } else {
                int size = i - (this.searchAdapter.searchResults.size() + 1);
                if (size >= 0 && size < this.searchAdapter.faqSearchResults.size()) {
                    valueOf = this.searchAdapter.faqSearchResults.get(size);
                }
            }
            if (valueOf instanceof SearchAdapter.SearchResult) {
                ((SearchAdapter.SearchResult) valueOf).open();
            } else if (valueOf instanceof SearchAdapter.FaqSearchResult) {
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.openArticle, this.searchAdapter.faqWebPage, ((SearchAdapter.FaqSearchResult) valueOf).url);
            }
            if (valueOf != null) {
                this.searchAdapter.addRecent(valueOf);
            }
        }
    }

    public /* synthetic */ boolean lambda$createView$4$SettingsActivity(View view, int i) {
        if (this.searchAdapter.isSearchWas()) {
            return false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setMessage(LocaleController.getString("ClearSearch", NUM));
        builder.setPositiveButton(LocaleController.getString("ClearButton", NUM).toUpperCase(), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                SettingsActivity.this.lambda$null$3$SettingsActivity(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        showDialog(builder.create());
        return true;
    }

    public /* synthetic */ void lambda$null$3$SettingsActivity(DialogInterface dialogInterface, int i) {
        this.searchAdapter.clearRecent();
    }

    public /* synthetic */ void lambda$createView$5$SettingsActivity(View view) {
        TLRPC$User user;
        TLRPC$UserProfilePhoto tLRPC$UserProfilePhoto;
        if (this.avatar == null && (user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()))) != null && (tLRPC$UserProfilePhoto = user.photo) != null && tLRPC$UserProfilePhoto.photo_big != null) {
            PhotoViewer.getInstance().setParentActivity(getParentActivity());
            TLRPC$UserProfilePhoto tLRPC$UserProfilePhoto2 = user.photo;
            int i = tLRPC$UserProfilePhoto2.dc_id;
            if (i != 0) {
                tLRPC$UserProfilePhoto2.photo_big.dc_id = i;
            }
            PhotoViewer.getInstance().openPhoto(user.photo.photo_big, this.provider);
        }
    }

    public /* synthetic */ void lambda$createView$7$SettingsActivity(View view) {
        TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
        if (user == null) {
            user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
        }
        if (user != null) {
            ImageUpdater imageUpdater2 = this.imageUpdater;
            TLRPC$UserProfilePhoto tLRPC$UserProfilePhoto = user.photo;
            imageUpdater2.openMenu((tLRPC$UserProfilePhoto == null || tLRPC$UserProfilePhoto.photo_big == null || (tLRPC$UserProfilePhoto instanceof TLRPC$TL_userProfilePhotoEmpty)) ? false : true, new Runnable() {
                public final void run() {
                    SettingsActivity.this.lambda$null$6$SettingsActivity();
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$6$SettingsActivity() {
        MessagesController.getInstance(this.currentAccount).deleteUserPhoto((TLRPC$InputPhoto) null);
    }

    /* access modifiers changed from: private */
    public void updateRows() {
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.emptyRow = 0;
        int i2 = i + 1;
        this.rowCount = i2;
        this.numberSectionRow = i;
        int i3 = i2 + 1;
        this.rowCount = i3;
        this.numberRow = i2;
        int i4 = i3 + 1;
        this.rowCount = i4;
        this.usernameRow = i3;
        int i5 = i4 + 1;
        this.rowCount = i5;
        this.bioRow = i4;
        int i6 = i5 + 1;
        this.rowCount = i6;
        this.settingsSectionRow = i5;
        int i7 = i6 + 1;
        this.rowCount = i7;
        this.settingsSectionRow2 = i6;
        int i8 = i7 + 1;
        this.rowCount = i8;
        this.notificationRow = i7;
        int i9 = i8 + 1;
        this.rowCount = i9;
        this.privacyRow = i8;
        int i10 = i9 + 1;
        this.rowCount = i10;
        this.dataRow = i9;
        this.rowCount = i10 + 1;
        this.chatRow = i10;
        if (getMessagesController().filtersEnabled || !getMessagesController().dialogFilters.isEmpty()) {
            int i11 = this.rowCount;
            this.rowCount = i11 + 1;
            this.filtersRow = i11;
        } else {
            this.filtersRow = -1;
        }
        int i12 = this.rowCount;
        int i13 = i12 + 1;
        this.rowCount = i13;
        this.devicesRow = i12;
        int i14 = i13 + 1;
        this.rowCount = i14;
        this.languageRow = i13;
        int i15 = i14 + 1;
        this.rowCount = i15;
        this.devicesSectionRow = i14;
        int i16 = i15 + 1;
        this.rowCount = i16;
        this.helpHeaderRow = i15;
        int i17 = i16 + 1;
        this.rowCount = i17;
        this.questionRow = i16;
        int i18 = i17 + 1;
        this.rowCount = i18;
        this.faqRow = i17;
        this.rowCount = i18 + 1;
        this.policyRow = i18;
        if (BuildVars.LOGS_ENABLED || BuildVars.DEBUG_VERSION) {
            int i19 = this.rowCount;
            int i20 = i19 + 1;
            this.rowCount = i20;
            this.helpSectionCell = i19;
            this.rowCount = i20 + 1;
            this.debugHeaderRow = i20;
        } else {
            this.helpSectionCell = -1;
            this.debugHeaderRow = -1;
        }
        if (BuildVars.LOGS_ENABLED) {
            int i21 = this.rowCount;
            int i22 = i21 + 1;
            this.rowCount = i22;
            this.sendLogsRow = i21;
            this.rowCount = i22 + 1;
            this.clearLogsRow = i22;
        } else {
            this.sendLogsRow = -1;
            this.clearLogsRow = -1;
        }
        if (BuildVars.DEBUG_VERSION) {
            int i23 = this.rowCount;
            this.rowCount = i23 + 1;
            this.switchBackendRow = i23;
        } else {
            this.switchBackendRow = -1;
        }
        int i24 = this.rowCount;
        this.rowCount = i24 + 1;
        this.versionRow = i24;
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    /* access modifiers changed from: private */
    public Animator searchExpandTransition(final boolean z) {
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
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(ofFloat, i, z) {
            public final /* synthetic */ ValueAnimator f$1;
            public final /* synthetic */ int f$2;
            public final /* synthetic */ boolean f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                SettingsActivity.this.lambda$searchExpandTransition$8$SettingsActivity(this.f$1, this.f$2, this.f$3, valueAnimator);
            }
        });
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

    public /* synthetic */ void lambda$searchExpandTransition$8$SettingsActivity(ValueAnimator valueAnimator, int i, boolean z, ValueAnimator valueAnimator2) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.searchTransitionProgress = floatValue;
        float f = (floatValue - 0.5f) / 0.5f;
        float f2 = (0.5f - floatValue) / 0.5f;
        if (f < 0.0f) {
            f = 0.0f;
        }
        if (f2 < 0.0f) {
            f2 = 0.0f;
        }
        float f3 = (float) (-i);
        float f4 = this.searchTransitionProgress;
        this.searchTransitionOffset = (int) ((1.0f - f4) * f3);
        float f5 = (float) i;
        this.searchListView.setTranslationY(f4 * f5);
        this.emptyView.setTranslationY(f5 * this.searchTransitionProgress);
        this.listView.setTranslationY(f3 * (1.0f - this.searchTransitionProgress));
        needLayout();
        this.listView.setAlpha(f);
        float f6 = 1.0f - f;
        this.searchListView.setAlpha(f6);
        this.emptyView.setAlpha(f6);
        this.avatarContainer.setAlpha(f);
        this.nameTextView.setAlpha(f);
        this.onlineTextView.setAlpha(f);
        this.searchItem.getSearchField().setAlpha(f2);
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
        ActionBarMenuItem actionBarMenuItem2 = this.searchItem;
        if (this.searchTransitionProgress > 0.5f) {
            i2 = 0;
        }
        actionBarMenuItem2.setVisibility(i2);
        ActionBar actionBar = this.actionBar;
        if (this.searchTransitionProgress < 0.5f) {
            z2 = true;
        }
        actionBar.onSearchFieldVisibilityChanged(z2);
        ActionBarMenuItem actionBarMenuItem3 = this.otherItem;
        if (actionBarMenuItem3 != null) {
            actionBarMenuItem3.setAlpha(f);
        }
        this.searchItem.setAlpha(f);
        this.topView.invalidate();
    }

    /* access modifiers changed from: private */
    public void updateSearchViewState(boolean z) {
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

    public void didUploadPhoto(TLRPC$InputFile tLRPC$InputFile, TLRPC$PhotoSize tLRPC$PhotoSize, TLRPC$PhotoSize tLRPC$PhotoSize2) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$InputFile, tLRPC$PhotoSize2, tLRPC$PhotoSize) {
            public final /* synthetic */ TLRPC$InputFile f$1;
            public final /* synthetic */ TLRPC$PhotoSize f$2;
            public final /* synthetic */ TLRPC$PhotoSize f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                SettingsActivity.this.lambda$didUploadPhoto$11$SettingsActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$didUploadPhoto$11$SettingsActivity(TLRPC$InputFile tLRPC$InputFile, TLRPC$PhotoSize tLRPC$PhotoSize, TLRPC$PhotoSize tLRPC$PhotoSize2) {
        if (tLRPC$InputFile != null) {
            TLRPC$TL_photos_uploadProfilePhoto tLRPC$TL_photos_uploadProfilePhoto = new TLRPC$TL_photos_uploadProfilePhoto();
            tLRPC$TL_photos_uploadProfilePhoto.file = tLRPC$InputFile;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_photos_uploadProfilePhoto, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    SettingsActivity.this.lambda$null$10$SettingsActivity(tLObject, tLRPC$TL_error);
                }
            });
            return;
        }
        TLRPC$FileLocation tLRPC$FileLocation = tLRPC$PhotoSize.location;
        this.avatar = tLRPC$FileLocation;
        this.avatarBig = tLRPC$PhotoSize2.location;
        this.avatarImage.setImage(ImageLocation.getForLocal(tLRPC$FileLocation), "50_50", (Drawable) this.avatarDrawable, (Object) null);
        showAvatarProgress(true, false);
    }

    public /* synthetic */ void lambda$null$10$SettingsActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
            if (user == null) {
                user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
                if (user != null) {
                    MessagesController.getInstance(this.currentAccount).putUser(user, false);
                } else {
                    return;
                }
            } else {
                UserConfig.getInstance(this.currentAccount).setCurrentUser(user);
            }
            TLRPC$TL_photos_photo tLRPC$TL_photos_photo = (TLRPC$TL_photos_photo) tLObject;
            ArrayList<TLRPC$PhotoSize> arrayList = tLRPC$TL_photos_photo.photo.sizes;
            TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(arrayList, 150);
            TLRPC$PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(arrayList, 800);
            TLRPC$TL_userProfilePhoto tLRPC$TL_userProfilePhoto = new TLRPC$TL_userProfilePhoto();
            user.photo = tLRPC$TL_userProfilePhoto;
            tLRPC$TL_userProfilePhoto.photo_id = tLRPC$TL_photos_photo.photo.id;
            if (closestPhotoSizeWithSize != null) {
                tLRPC$TL_userProfilePhoto.photo_small = closestPhotoSizeWithSize.location;
            }
            if (closestPhotoSizeWithSize2 != null) {
                user.photo.photo_big = closestPhotoSizeWithSize2.location;
            } else if (closestPhotoSizeWithSize != null) {
                user.photo.photo_small = closestPhotoSizeWithSize.location;
            }
            if (tLRPC$TL_photos_photo != null) {
                if (!(closestPhotoSizeWithSize == null || this.avatar == null)) {
                    FileLoader.getPathToAttach(this.avatar, true).renameTo(FileLoader.getPathToAttach(closestPhotoSizeWithSize, true));
                    ImageLoader.getInstance().replaceImageInCache(this.avatar.volume_id + "_" + this.avatar.local_id + "@50_50", closestPhotoSizeWithSize.location.volume_id + "_" + closestPhotoSizeWithSize.location.local_id + "@50_50", ImageLocation.getForUser(user, false), true);
                }
                if (!(closestPhotoSizeWithSize2 == null || this.avatarBig == null)) {
                    FileLoader.getPathToAttach(this.avatarBig, true).renameTo(FileLoader.getPathToAttach(closestPhotoSizeWithSize2, true));
                }
            }
            MessagesStorage.getInstance(this.currentAccount).clearUserPhotos(user.id);
            ArrayList arrayList2 = new ArrayList();
            arrayList2.add(user);
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(arrayList2, (ArrayList<TLRPC$Chat>) null, false, true);
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                SettingsActivity.this.lambda$null$9$SettingsActivity();
            }
        });
    }

    public /* synthetic */ void lambda$null$9$SettingsActivity() {
        this.avatar = null;
        this.avatarBig = null;
        updateUserData();
        showAvatarProgress(false, true);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, 1535);
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
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.avatarAnimation = animatorSet2;
                if (z) {
                    this.avatarProgressView.setVisibility(0);
                    this.avatarAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{1.0f})});
                } else {
                    animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{0.0f})});
                }
                this.avatarAnimation.setDuration(180);
                this.avatarAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (SettingsActivity.this.avatarAnimation != null && SettingsActivity.this.avatarProgressView != null) {
                            if (!z) {
                                SettingsActivity.this.avatarProgressView.setVisibility(4);
                            }
                            AnimatorSet unused = SettingsActivity.this.avatarAnimation = null;
                        }
                    }

                    public void onAnimationCancel(Animator animator) {
                        AnimatorSet unused = SettingsActivity.this.avatarAnimation = null;
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
        String str;
        ImageUpdater imageUpdater2 = this.imageUpdater;
        if (imageUpdater2 != null && (str = imageUpdater2.currentPicturePath) != null) {
            bundle.putString("path", str);
        }
    }

    public void restoreSelfArgs(Bundle bundle) {
        ImageUpdater imageUpdater2 = this.imageUpdater;
        if (imageUpdater2 != null) {
            imageUpdater2.currentPicturePath = bundle.getString("path");
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        RecyclerListView recyclerListView;
        if (i == NotificationCenter.updateInterfaces) {
            int intValue = objArr[0].intValue();
            if ((intValue & 2) != 0 || (intValue & 1) != 0) {
                updateUserData();
            }
        } else if (i == NotificationCenter.userInfoDidLoad) {
            if (objArr[0].intValue() == UserConfig.getInstance(this.currentAccount).getClientUserId() && this.listAdapter != null) {
                TLRPC$UserFull tLRPC$UserFull = objArr[1];
                this.userInfo = tLRPC$UserFull;
                if (!TextUtils.equals(tLRPC$UserFull.about, this.currentBio)) {
                    this.listAdapter.notifyItemChanged(this.bioRow);
                }
            }
        } else if (i == NotificationCenter.emojiDidLoad && (recyclerListView = this.listView) != null) {
            recyclerListView.invalidateViews();
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
        updateUserData();
        fixLayout();
        setParentActivityTitle(LocaleController.getString("Settings", NUM));
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        fixLayout();
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationStart(boolean z, boolean z2) {
        if (((!z && z2) || (z && !z2)) && this.playProfileAnimation && this.allowProfileAnimation) {
            this.openAnimationInProgress = true;
        }
        if (z) {
            this.transitionIndex = NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(this.transitionIndex, new int[]{NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.mediaCountDidLoad, NotificationCenter.mediaCountsDidLoad, NotificationCenter.userInfoDidLoad});
        }
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            if (!z2 && this.playProfileAnimation && this.allowProfileAnimation) {
                this.openAnimationInProgress = false;
            }
            NotificationCenter.getInstance(this.currentAccount).onAnimationFinish(this.transitionIndex);
        }
    }

    @Keep
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
        int blue = Color.blue(color2);
        this.topView.setBackgroundColor(Color.rgb(red + ((int) (((float) (Color.red(color) - red)) * f)), green + ((int) (((float) (Color.green(color) - green)) * f)), blue + ((int) (((float) (Color.blue(color) - blue)) * f))));
        int color3 = Theme.getColor("avatar_actionBarIconBlue");
        int color4 = Theme.getColor("actionBarDefaultIcon");
        int red2 = Color.red(color4);
        int green2 = Color.green(color4);
        int blue2 = Color.blue(color4);
        this.actionBar.setItemsColor(Color.rgb(red2 + ((int) (((float) (Color.red(color3) - red2)) * f)), green2 + ((int) (((float) (Color.green(color3) - green2)) * f)), blue2 + ((int) (((float) (Color.blue(color3) - blue2)) * f))), false);
        this.titleTextView.setAlpha(1.0f - f);
        this.nameTextView.setAlpha(f);
        this.onlineTextView.setAlpha(f);
        this.extraHeight = (int) (((float) this.initialAnimationExtraHeight) * f);
        this.avatarContainer.setAlpha(f);
        needLayout();
    }

    /* access modifiers changed from: protected */
    public AnimatorSet onCustomTransitionAnimation(boolean z, final Runnable runnable) {
        if (!this.playProfileAnimation || !this.allowProfileAnimation) {
            return null;
        }
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(180);
        this.listView.setLayerType(2, (Paint) null);
        this.actionBar.createMenu();
        if (z) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.onlineTextView.getLayoutParams();
            layoutParams.rightMargin = (int) ((AndroidUtilities.density * -21.0f) + ((float) AndroidUtilities.dp(8.0f)));
            this.onlineTextView.setLayoutParams(layoutParams);
            int ceil = (int) Math.ceil((double) (((float) (AndroidUtilities.displaySize.x - AndroidUtilities.dp(126.0f))) + (AndroidUtilities.density * 21.0f)));
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.nameTextView.getLayoutParams();
            float f = (float) ceil;
            if (f < this.nameTextView.getPaint().measureText(this.nameTextView.getText().toString()) * 1.12f) {
                layoutParams2.width = (int) Math.ceil((double) (f / 1.12f));
            } else {
                layoutParams2.width = -2;
            }
            this.nameTextView.setLayoutParams(layoutParams2);
            this.initialAnimationExtraHeight = AndroidUtilities.dp(88.0f);
            setAnimationProgress(0.0f);
            ArrayList arrayList = new ArrayList();
            arrayList.add(ObjectAnimator.ofFloat(this, "animationProgress", new float[]{0.0f, 1.0f}));
            ImageView imageView = this.writeButton;
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
            ArrayList arrayList2 = new ArrayList();
            arrayList2.add(ObjectAnimator.ofFloat(this, "animationProgress", new float[]{1.0f, 0.0f}));
            ImageView imageView2 = this.writeButton;
            if (imageView2 != null) {
                arrayList2.add(ObjectAnimator.ofFloat(imageView2, View.SCALE_X, new float[]{0.2f}));
                arrayList2.add(ObjectAnimator.ofFloat(this.writeButton, View.SCALE_Y, new float[]{0.2f}));
                arrayList2.add(ObjectAnimator.ofFloat(this.writeButton, View.ALPHA, new float[]{0.0f}));
            }
            arrayList2.add(ObjectAnimator.ofFloat(this.onlineTextView, View.ALPHA, new float[]{0.0f}));
            arrayList2.add(ObjectAnimator.ofFloat(this.nameTextView, View.ALPHA, new float[]{0.0f}));
            arrayList2.add(ObjectAnimator.ofFloat(this.searchItem, View.TRANSLATION_X, new float[]{(float) AndroidUtilities.dp(48.0f)}));
            arrayList2.add(ObjectAnimator.ofFloat(this.otherItem, View.TRANSLATION_X, new float[]{(float) AndroidUtilities.dp(48.0f)}));
            animatorSet.playTogether(arrayList2);
        }
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                SettingsActivity.this.listView.setLayerType(0, (Paint) null);
                runnable.run();
            }
        });
        animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        animatorSet.getClass();
        AndroidUtilities.runOnUIThread(new Runnable(animatorSet) {
            public final /* synthetic */ AnimatorSet f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                this.f$0.start();
            }
        }, 50);
        return animatorSet;
    }

    /* access modifiers changed from: private */
    public void checkListViewScroll() {
        if (this.listView.getVisibility() == 0 && this.listView.getChildCount() > 0 && !this.openAnimationInProgress && this.writeButton.getVisibility() == 0) {
            int i = 0;
            View childAt = this.listView.getChildAt(0);
            RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(childAt);
            int top = childAt.getTop();
            if (top >= 0 && holder != null && holder.getAdapterPosition() == 0) {
                i = top;
            }
            if (this.extraHeight != i) {
                this.extraHeight = i;
                this.topView.invalidate();
                needLayout();
            }
        }
    }

    /* access modifiers changed from: private */
    public void needLayout() {
        int i;
        int i2 = 0;
        int currentActionBarHeight = (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null && !this.openAnimationInProgress) {
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
                    boolean z = dp > 0.2f && !this.searchMode;
                    if (z != (this.writeButton.getTag() == null)) {
                        if (z) {
                            this.writeButton.setTag((Object) null);
                        } else {
                            this.writeButton.setTag(0);
                        }
                        AnimatorSet animatorSet = this.writeButtonAnimation;
                        if (animatorSet != null) {
                            this.writeButtonAnimation = null;
                            animatorSet.cancel();
                        }
                        AnimatorSet animatorSet2 = new AnimatorSet();
                        this.writeButtonAnimation = animatorSet2;
                        if (z) {
                            animatorSet2.setInterpolator(new DecelerateInterpolator());
                            this.writeButtonAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.writeButton, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.writeButton, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.writeButton, View.ALPHA, new float[]{1.0f})});
                        } else {
                            animatorSet2.setInterpolator(new AccelerateInterpolator());
                            this.writeButtonAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.writeButton, View.SCALE_X, new float[]{0.2f}), ObjectAnimator.ofFloat(this.writeButton, View.SCALE_Y, new float[]{0.2f}), ObjectAnimator.ofFloat(this.writeButton, View.ALPHA, new float[]{0.0f})});
                        }
                        this.writeButtonAnimation.setDuration(150);
                        this.writeButtonAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                if (SettingsActivity.this.writeButtonAnimation != null && SettingsActivity.this.writeButtonAnimation.equals(animator)) {
                                    AnimatorSet unused = SettingsActivity.this.writeButtonAnimation = null;
                                }
                            }
                        });
                        this.writeButtonAnimation.start();
                    }
                }
            }
            if (this.actionBar.getOccupyStatusBar()) {
                i2 = AndroidUtilities.statusBarHeight;
            }
            float f = AndroidUtilities.density;
            float currentActionBarHeight2 = ((((float) i2) + ((((float) ActionBar.getCurrentActionBarHeight()) / 2.0f) * (dp + 1.0f))) - (21.0f * f)) + (f * 27.0f * dp);
            float f2 = ((18.0f * dp) + 42.0f) / 42.0f;
            this.avatarContainer.setScaleX(f2);
            this.avatarContainer.setScaleY(f2);
            this.avatarContainer.setTranslationX(((float) (-AndroidUtilities.dp(47.0f))) * dp);
            double d = (double) currentActionBarHeight2;
            this.avatarContainer.setTranslationY((float) Math.ceil(d));
            TextView textView = this.nameTextView;
            if (textView != null) {
                textView.setTranslationX(AndroidUtilities.density * -21.0f * dp);
                this.onlineTextView.setTranslationX(AndroidUtilities.density * -21.0f * dp);
                this.nameTextView.setTranslationY((((float) Math.floor(d)) - ((float) Math.ceil((double) AndroidUtilities.density))) + ((float) Math.floor((double) (AndroidUtilities.density * 7.0f * dp))));
                this.onlineTextView.setTranslationY(((float) Math.floor(d)) + ((float) AndroidUtilities.dp(22.0f)) + (((float) Math.floor((double) (AndroidUtilities.density * 11.0f))) * dp));
                float f3 = (0.12f * dp) + 1.0f;
                this.nameTextView.setScaleX(f3);
                this.nameTextView.setScaleY(f3);
                if (!this.openAnimationInProgress) {
                    if (AndroidUtilities.isTablet()) {
                        i = AndroidUtilities.dp(490.0f);
                    } else {
                        i = AndroidUtilities.displaySize.x;
                    }
                    int dp2 = AndroidUtilities.dp(214.0f);
                    int i3 = i - dp2;
                    float f4 = (float) i;
                    int max = (int) ((f4 - (((float) dp2) * Math.max(0.0f, 1.0f - (dp != 1.0f ? (0.15f * dp) / (1.0f - dp) : 1.0f)))) - this.nameTextView.getTranslationX());
                    float measureText = this.nameTextView.getPaint().measureText(this.nameTextView.getText().toString()) * f3;
                    FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.nameTextView.getLayoutParams();
                    float f5 = (float) max;
                    if (f5 < measureText) {
                        layoutParams2.width = Math.max(i3, (int) Math.ceil((double) (((float) (max - AndroidUtilities.dp(24.0f))) / (((1.12f - f3) * 7.0f) + f3))));
                    } else {
                        layoutParams2.width = (int) Math.ceil((double) measureText);
                    }
                    layoutParams2.width = (int) Math.min(((f4 - this.nameTextView.getX()) / f3) - ((float) AndroidUtilities.dp(8.0f)), (float) layoutParams2.width);
                    this.nameTextView.setLayoutParams(layoutParams2);
                    float measureText2 = this.onlineTextView.getPaint().measureText(this.onlineTextView.getText().toString());
                    FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.onlineTextView.getLayoutParams();
                    layoutParams3.rightMargin = (int) Math.ceil((double) (this.onlineTextView.getTranslationX() + ((float) AndroidUtilities.dp(8.0f)) + (((float) AndroidUtilities.dp(40.0f)) * (1.0f - dp))));
                    if (f5 < measureText2) {
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
            view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    if (SettingsActivity.this.fragmentView == null) {
                        return true;
                    }
                    SettingsActivity.this.checkListViewScroll();
                    SettingsActivity.this.needLayout();
                    SettingsActivity.this.fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
        }
    }

    private void updateUserData() {
        TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
        if (user != null) {
            TLRPC$FileLocation tLRPC$FileLocation = null;
            TLRPC$UserProfilePhoto tLRPC$UserProfilePhoto = user.photo;
            if (tLRPC$UserProfilePhoto != null) {
                tLRPC$FileLocation = tLRPC$UserProfilePhoto.photo_big;
            }
            AvatarDrawable avatarDrawable2 = new AvatarDrawable(user, true);
            this.avatarDrawable = avatarDrawable2;
            avatarDrawable2.setColor(Theme.getColor("avatar_backgroundInProfileBlue"));
            BackupImageView backupImageView = this.avatarImage;
            if (backupImageView != null) {
                backupImageView.setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) this.avatarDrawable, (Object) user);
                this.avatarImage.getImageReceiver().setVisible(!PhotoViewer.isShowingImage(tLRPC$FileLocation), false);
                this.nameTextView.setText(UserObject.getUserName(user));
                this.onlineTextView.setText(LocaleController.getString("Online", NUM));
                this.avatarImage.getImageReceiver().setVisible(!PhotoViewer.isShowingImage(tLRPC$FileLocation), false);
            }
        }
    }

    private void sendLogs() {
        if (getParentActivity() != null) {
            AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
            alertDialog.setCanCacnel(false);
            alertDialog.show();
            Utilities.globalQueue.postRunnable(new Runnable(alertDialog) {
                public final /* synthetic */ AlertDialog f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    SettingsActivity.this.lambda$sendLogs$13$SettingsActivity(this.f$1);
                }
            });
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v1, resolved type: java.io.BufferedInputStream} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v0, resolved type: java.io.BufferedInputStream} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v1, resolved type: java.util.zip.ZipOutputStream} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v2, resolved type: java.util.zip.ZipOutputStream} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v3, resolved type: java.util.zip.ZipOutputStream} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v6, resolved type: java.io.BufferedInputStream} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v7, resolved type: java.io.BufferedInputStream} */
    /* JADX WARNING: type inference failed for: r1v0, types: [java.io.BufferedInputStream, java.lang.String] */
    /* JADX WARNING: type inference failed for: r1v4 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x0099 A[SYNTHETIC, Splitter:B:38:0x0099] */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x009e A[Catch:{ Exception -> 0x00b5 }] */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00ac A[Catch:{ Exception -> 0x00b5 }] */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00b1 A[Catch:{ Exception -> 0x00b5 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$sendLogs$13$SettingsActivity(org.telegram.ui.ActionBar.AlertDialog r14) {
        /*
            r13 = this;
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x00b5 }
            r1 = 0
            java.io.File r0 = r0.getExternalFilesDir(r1)     // Catch:{ Exception -> 0x00b5 }
            java.io.File r2 = new java.io.File     // Catch:{ Exception -> 0x00b5 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00b5 }
            r3.<init>()     // Catch:{ Exception -> 0x00b5 }
            java.lang.String r0 = r0.getAbsolutePath()     // Catch:{ Exception -> 0x00b5 }
            r3.append(r0)     // Catch:{ Exception -> 0x00b5 }
            java.lang.String r0 = "/logs"
            r3.append(r0)     // Catch:{ Exception -> 0x00b5 }
            java.lang.String r0 = r3.toString()     // Catch:{ Exception -> 0x00b5 }
            r2.<init>(r0)     // Catch:{ Exception -> 0x00b5 }
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x00b5 }
            java.lang.String r3 = "logs.zip"
            r0.<init>(r2, r3)     // Catch:{ Exception -> 0x00b5 }
            boolean r3 = r0.exists()     // Catch:{ Exception -> 0x00b5 }
            if (r3 == 0) goto L_0x0031
            r0.delete()     // Catch:{ Exception -> 0x00b5 }
        L_0x0031:
            java.io.File[] r2 = r2.listFiles()     // Catch:{ Exception -> 0x00b5 }
            r3 = 1
            boolean[] r4 = new boolean[r3]     // Catch:{ Exception -> 0x00b5 }
            java.io.FileOutputStream r5 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x0090, all -> 0x008d }
            r5.<init>(r0)     // Catch:{ Exception -> 0x0090, all -> 0x008d }
            java.util.zip.ZipOutputStream r6 = new java.util.zip.ZipOutputStream     // Catch:{ Exception -> 0x0090, all -> 0x008d }
            java.io.BufferedOutputStream r7 = new java.io.BufferedOutputStream     // Catch:{ Exception -> 0x0090, all -> 0x008d }
            r7.<init>(r5)     // Catch:{ Exception -> 0x0090, all -> 0x008d }
            r6.<init>(r7)     // Catch:{ Exception -> 0x0090, all -> 0x008d }
            r5 = 65536(0x10000, float:9.18355E-41)
            byte[] r7 = new byte[r5]     // Catch:{ Exception -> 0x008a, all -> 0x0088 }
            r8 = 0
            r9 = 0
        L_0x004d:
            int r10 = r2.length     // Catch:{ Exception -> 0x008a, all -> 0x0088 }
            if (r9 >= r10) goto L_0x007d
            java.io.FileInputStream r10 = new java.io.FileInputStream     // Catch:{ Exception -> 0x008a, all -> 0x0088 }
            r11 = r2[r9]     // Catch:{ Exception -> 0x008a, all -> 0x0088 }
            r10.<init>(r11)     // Catch:{ Exception -> 0x008a, all -> 0x0088 }
            java.io.BufferedInputStream r11 = new java.io.BufferedInputStream     // Catch:{ Exception -> 0x008a, all -> 0x0088 }
            r11.<init>(r10, r5)     // Catch:{ Exception -> 0x008a, all -> 0x0088 }
            java.util.zip.ZipEntry r10 = new java.util.zip.ZipEntry     // Catch:{ Exception -> 0x007b }
            r12 = r2[r9]     // Catch:{ Exception -> 0x007b }
            java.lang.String r12 = r12.getName()     // Catch:{ Exception -> 0x007b }
            r10.<init>(r12)     // Catch:{ Exception -> 0x007b }
            r6.putNextEntry(r10)     // Catch:{ Exception -> 0x007b }
        L_0x006a:
            int r10 = r11.read(r7, r8, r5)     // Catch:{ Exception -> 0x007b }
            r12 = -1
            if (r10 == r12) goto L_0x0075
            r6.write(r7, r8, r10)     // Catch:{ Exception -> 0x007b }
            goto L_0x006a
        L_0x0075:
            r11.close()     // Catch:{ Exception -> 0x007b }
            int r9 = r9 + 1
            goto L_0x004d
        L_0x007b:
            r1 = move-exception
            goto L_0x0094
        L_0x007d:
            r4[r8] = r3     // Catch:{ Exception -> 0x008a, all -> 0x0088 }
            if (r1 == 0) goto L_0x0084
            r1.close()     // Catch:{ Exception -> 0x00b5 }
        L_0x0084:
            r6.close()     // Catch:{ Exception -> 0x00b5 }
            goto L_0x009f
        L_0x0088:
            r14 = move-exception
            goto L_0x00aa
        L_0x008a:
            r2 = move-exception
            r11 = r1
            goto L_0x0093
        L_0x008d:
            r14 = move-exception
            r6 = r1
            goto L_0x00aa
        L_0x0090:
            r2 = move-exception
            r6 = r1
            r11 = r6
        L_0x0093:
            r1 = r2
        L_0x0094:
            r1.printStackTrace()     // Catch:{ all -> 0x00a8 }
            if (r11 == 0) goto L_0x009c
            r11.close()     // Catch:{ Exception -> 0x00b5 }
        L_0x009c:
            if (r6 == 0) goto L_0x009f
            goto L_0x0084
        L_0x009f:
            org.telegram.ui.-$$Lambda$SettingsActivity$NpHx7O2QlzkDb5TDgDDIoAD33qI r1 = new org.telegram.ui.-$$Lambda$SettingsActivity$NpHx7O2QlzkDb5TDgDDIoAD33qI     // Catch:{ Exception -> 0x00b5 }
            r1.<init>(r14, r4, r0)     // Catch:{ Exception -> 0x00b5 }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)     // Catch:{ Exception -> 0x00b5 }
            goto L_0x00b9
        L_0x00a8:
            r14 = move-exception
            r1 = r11
        L_0x00aa:
            if (r1 == 0) goto L_0x00af
            r1.close()     // Catch:{ Exception -> 0x00b5 }
        L_0x00af:
            if (r6 == 0) goto L_0x00b4
            r6.close()     // Catch:{ Exception -> 0x00b5 }
        L_0x00b4:
            throw r14     // Catch:{ Exception -> 0x00b5 }
        L_0x00b5:
            r14 = move-exception
            r14.printStackTrace()
        L_0x00b9:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.SettingsActivity.lambda$sendLogs$13$SettingsActivity(org.telegram.ui.ActionBar.AlertDialog):void");
    }

    public /* synthetic */ void lambda$null$12$SettingsActivity(AlertDialog alertDialog, boolean[] zArr, File file) {
        Uri uri;
        try {
            alertDialog.dismiss();
        } catch (Exception unused) {
        }
        if (zArr[0]) {
            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(getParentActivity(), "org.telegram.messenger.beta.provider", file);
            } else {
                uri = Uri.fromFile(file);
            }
            Intent intent = new Intent("android.intent.action.SEND");
            if (Build.VERSION.SDK_INT >= 24) {
                intent.addFlags(1);
            }
            intent.setType("message/rfCLASSNAME");
            intent.putExtra("android.intent.extra.EMAIL", "");
            intent.putExtra("android.intent.extra.SUBJECT", "Logs from " + LocaleController.getInstance().formatterStats.format(System.currentTimeMillis()));
            intent.putExtra("android.intent.extra.STREAM", uri);
            if (getParentActivity() != null) {
                getParentActivity().startActivityForResult(Intent.createChooser(intent, "Select email application."), 500);
                return;
            }
            return;
        }
        Toast.makeText(getParentActivity(), LocaleController.getString("ErrorOccurred", NUM), 0).show();
    }

    private class SearchAdapter extends RecyclerListView.SelectionAdapter {
        private ArrayList<FaqSearchResult> faqSearchArray = new ArrayList<>();
        /* access modifiers changed from: private */
        public ArrayList<FaqSearchResult> faqSearchResults = new ArrayList<>();
        /* access modifiers changed from: private */
        public TLRPC$WebPage faqWebPage;
        private String lastSearchString;
        private boolean loadingFaqPage;
        private Context mContext;
        /* access modifiers changed from: private */
        public ArrayList<Object> recentSearches = new ArrayList<>();
        private ArrayList<CharSequence> resultNames = new ArrayList<>();
        private SearchResult[] searchArray;
        /* access modifiers changed from: private */
        public ArrayList<SearchResult> searchResults = new ArrayList<>();
        private Runnable searchRunnable;
        /* access modifiers changed from: private */
        public boolean searchWas;
        final /* synthetic */ SettingsActivity this$0;

        private class SearchResult {
            /* access modifiers changed from: private */
            public int guid;
            /* access modifiers changed from: private */
            public int iconResId;
            /* access modifiers changed from: private */
            public int num;
            private Runnable openRunnable;
            /* access modifiers changed from: private */
            public String[] path;
            private String rowName;
            /* access modifiers changed from: private */
            public String searchTitle;

            public SearchResult(SearchAdapter searchAdapter, int i, String str, int i2, Runnable runnable) {
                this(i, str, (String) null, (String) null, (String) null, i2, runnable);
            }

            public SearchResult(SearchAdapter searchAdapter, int i, String str, String str2, int i2, Runnable runnable) {
                this(i, str, (String) null, str2, (String) null, i2, runnable);
            }

            public SearchResult(SearchAdapter searchAdapter, int i, String str, String str2, String str3, int i2, Runnable runnable) {
                this(i, str, str2, str3, (String) null, i2, runnable);
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
                if ((obj instanceof SearchResult) && this.guid == ((SearchResult) obj).guid) {
                    return true;
                }
                return false;
            }

            public String toString() {
                SerializedData serializedData = new SerializedData();
                serializedData.writeInt32(this.num);
                serializedData.writeInt32(1);
                serializedData.writeInt32(this.guid);
                return Utilities.bytesToHex(serializedData.toByteArray());
            }

            /* access modifiers changed from: private */
            public void open() {
                this.openRunnable.run();
                if (this.rowName != null) {
                    BaseFragment baseFragment = SearchAdapter.this.this$0.parentLayout.fragmentsStack.get(SearchAdapter.this.this$0.parentLayout.fragmentsStack.size() - 1);
                    try {
                        Field declaredField = baseFragment.getClass().getDeclaredField("listView");
                        declaredField.setAccessible(true);
                        ((RecyclerListView) declaredField.get(baseFragment)).highlightRow(
                        /*  JADX ERROR: Method code generation error
                            jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0041: INVOKE  
                              (wrap: org.telegram.ui.Components.RecyclerListView : 0x003f: CHECK_CAST  (r0v9 org.telegram.ui.Components.RecyclerListView) = (org.telegram.ui.Components.RecyclerListView) (wrap: java.lang.Object : 0x003b: INVOKE  (r0v8 java.lang.Object) = 
                              (r1v7 'declaredField' java.lang.reflect.Field)
                              (r0v7 'baseFragment' org.telegram.ui.ActionBar.BaseFragment)
                             java.lang.reflect.Field.get(java.lang.Object):java.lang.Object type: VIRTUAL))
                              (wrap: org.telegram.ui.-$$Lambda$SettingsActivity$SearchAdapter$SearchResult$QTQolgPlTMmAnvlmOMDH38UI6H4 : 0x0038: CONSTRUCTOR  (r2v1 org.telegram.ui.-$$Lambda$SettingsActivity$SearchAdapter$SearchResult$QTQolgPlTMmAnvlmOMDH38UI6H4) = 
                              (r4v0 'this' org.telegram.ui.SettingsActivity$SearchAdapter$SearchResult A[THIS])
                              (r0v7 'baseFragment' org.telegram.ui.ActionBar.BaseFragment)
                             call: org.telegram.ui.-$$Lambda$SettingsActivity$SearchAdapter$SearchResult$QTQolgPlTMmAnvlmOMDH38UI6H4.<init>(org.telegram.ui.SettingsActivity$SearchAdapter$SearchResult, org.telegram.ui.ActionBar.BaseFragment):void type: CONSTRUCTOR)
                             org.telegram.ui.Components.RecyclerListView.highlightRow(org.telegram.ui.Components.RecyclerListView$IntReturnCallback):void type: VIRTUAL in method: org.telegram.ui.SettingsActivity.SearchAdapter.SearchResult.open():void, dex: classes.dex
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                            	at jadx.core.codegen.RegionGen.makeTryCatch(RegionGen.java:311)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:68)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                            	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                            	at java.util.ArrayList.forEach(ArrayList.java:1257)
                            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                            	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                            	at java.util.ArrayList.forEach(ArrayList.java:1257)
                            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                            	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                            	at java.util.ArrayList.forEach(ArrayList.java:1257)
                            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                            Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0038: CONSTRUCTOR  (r2v1 org.telegram.ui.-$$Lambda$SettingsActivity$SearchAdapter$SearchResult$QTQolgPlTMmAnvlmOMDH38UI6H4) = 
                              (r4v0 'this' org.telegram.ui.SettingsActivity$SearchAdapter$SearchResult A[THIS])
                              (r0v7 'baseFragment' org.telegram.ui.ActionBar.BaseFragment)
                             call: org.telegram.ui.-$$Lambda$SettingsActivity$SearchAdapter$SearchResult$QTQolgPlTMmAnvlmOMDH38UI6H4.<init>(org.telegram.ui.SettingsActivity$SearchAdapter$SearchResult, org.telegram.ui.ActionBar.BaseFragment):void type: CONSTRUCTOR in method: org.telegram.ui.SettingsActivity.SearchAdapter.SearchResult.open():void, dex: classes.dex
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                            	... 69 more
                            Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: org.telegram.ui.-$$Lambda$SettingsActivity$SearchAdapter$SearchResult$QTQolgPlTMmAnvlmOMDH38UI6H4, state: NOT_LOADED
                            	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                            	... 75 more
                            */
                        /*
                            this = this;
                            java.lang.Runnable r0 = r4.openRunnable
                            r0.run()
                            java.lang.String r0 = r4.rowName
                            if (r0 == 0) goto L_0x0048
                            org.telegram.ui.SettingsActivity$SearchAdapter r0 = org.telegram.ui.SettingsActivity.SearchAdapter.this
                            org.telegram.ui.SettingsActivity r0 = r0.this$0
                            org.telegram.ui.ActionBar.ActionBarLayout r0 = r0.parentLayout
                            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
                            org.telegram.ui.SettingsActivity$SearchAdapter r1 = org.telegram.ui.SettingsActivity.SearchAdapter.this
                            org.telegram.ui.SettingsActivity r1 = r1.this$0
                            org.telegram.ui.ActionBar.ActionBarLayout r1 = r1.parentLayout
                            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r1.fragmentsStack
                            int r1 = r1.size()
                            r2 = 1
                            int r1 = r1 - r2
                            java.lang.Object r0 = r0.get(r1)
                            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
                            java.lang.Class r1 = r0.getClass()     // Catch:{ all -> 0x0048 }
                            java.lang.String r3 = "listView"
                            java.lang.reflect.Field r1 = r1.getDeclaredField(r3)     // Catch:{ all -> 0x0048 }
                            r1.setAccessible(r2)     // Catch:{ all -> 0x0048 }
                            org.telegram.ui.-$$Lambda$SettingsActivity$SearchAdapter$SearchResult$QTQolgPlTMmAnvlmOMDH38UI6H4 r2 = new org.telegram.ui.-$$Lambda$SettingsActivity$SearchAdapter$SearchResult$QTQolgPlTMmAnvlmOMDH38UI6H4     // Catch:{ all -> 0x0048 }
                            r2.<init>(r4, r0)     // Catch:{ all -> 0x0048 }
                            java.lang.Object r0 = r1.get(r0)     // Catch:{ all -> 0x0048 }
                            org.telegram.ui.Components.RecyclerListView r0 = (org.telegram.ui.Components.RecyclerListView) r0     // Catch:{ all -> 0x0048 }
                            r0.highlightRow(r2)     // Catch:{ all -> 0x0048 }
                            r0 = 0
                            r1.setAccessible(r0)     // Catch:{ all -> 0x0048 }
                        L_0x0048:
                            return
                        */
                        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.SettingsActivity.SearchAdapter.SearchResult.open():void");
                    }

                    public /* synthetic */ int lambda$open$0$SettingsActivity$SearchAdapter$SearchResult(BaseFragment baseFragment) {
                        int i = -1;
                        try {
                            Field declaredField = baseFragment.getClass().getDeclaredField(this.rowName);
                            Field declaredField2 = baseFragment.getClass().getDeclaredField("layoutManager");
                            declaredField.setAccessible(true);
                            declaredField2.setAccessible(true);
                            i = declaredField.getInt(baseFragment);
                            ((LinearLayoutManager) declaredField2.get(baseFragment)).scrollToPositionWithOffset(i, 0);
                            declaredField.setAccessible(false);
                            declaredField2.setAccessible(false);
                            return i;
                        } catch (Throwable unused) {
                            return i;
                        }
                    }
                }

                private class FaqSearchResult {
                    /* access modifiers changed from: private */
                    public int num;
                    /* access modifiers changed from: private */
                    public String[] path;
                    /* access modifiers changed from: private */
                    public String title;
                    /* access modifiers changed from: private */
                    public String url;

                    public FaqSearchResult(SearchAdapter searchAdapter, String str, String[] strArr, String str2) {
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
                                String[] strArr2 = this.path;
                                if (i >= strArr2.length) {
                                    break;
                                }
                                serializedData.writeString(strArr2[i]);
                                i++;
                            }
                        }
                        serializedData.writeString(this.url);
                        return Utilities.bytesToHex(serializedData.toByteArray());
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
                    while (true) {
                        if (i >= 3) {
                            i = -1;
                            break;
                        } else if (!UserConfig.getInstance(i).isClientActivated()) {
                            break;
                        } else {
                            i++;
                        }
                    }
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
                    this.this$0.presentFragment(new TwoStepVerificationActivity());
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

                public /* synthetic */ void lambda$new$79$SettingsActivity$SearchAdapter() {
                    SettingsActivity settingsActivity = this.this$0;
                    settingsActivity.showDialog(AlertsCreator.createSupportAlert(settingsActivity));
                }

                public /* synthetic */ void lambda$new$80$SettingsActivity$SearchAdapter() {
                    Browser.openUrl((Context) this.this$0.getParentActivity(), LocaleController.getString("TelegramFaqUrl", NUM));
                }

                public /* synthetic */ void lambda$new$81$SettingsActivity$SearchAdapter() {
                    Browser.openUrl((Context) this.this$0.getParentActivity(), LocaleController.getString("PrivacyPolicyUrl", NUM));
                }

                public SearchAdapter(SettingsActivity settingsActivity, Context context) {
                    this.this$0 = settingsActivity;
                    String str = "StorageUsage";
                    this.searchArray = new SearchResult[]{new SearchResult(this, 500, LocaleController.getString("EditName", NUM), 0, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$0$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 501, LocaleController.getString("ChangePhoneNumber", NUM), 0, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$1$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 502, LocaleController.getString("AddAnotherAccount", NUM), 0, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$2$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 503, LocaleController.getString("UserBio", NUM), 0, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$3$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 1, LocaleController.getString("NotificationsAndSounds", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$4$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 2, LocaleController.getString("NotificationsPrivateChats", NUM), LocaleController.getString("NotificationsAndSounds", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$5$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 3, LocaleController.getString("NotificationsGroups", NUM), LocaleController.getString("NotificationsAndSounds", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$6$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 4, LocaleController.getString("NotificationsChannels", NUM), LocaleController.getString("NotificationsAndSounds", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$7$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 5, LocaleController.getString("VoipNotificationSettings", NUM), "callsSectionRow", LocaleController.getString("NotificationsAndSounds", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$8$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 6, LocaleController.getString("BadgeNumber", NUM), "badgeNumberSection", LocaleController.getString("NotificationsAndSounds", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$9$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 7, LocaleController.getString("InAppNotifications", NUM), "inappSectionRow", LocaleController.getString("NotificationsAndSounds", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$10$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 8, LocaleController.getString("ContactJoined", NUM), "contactJoinedRow", LocaleController.getString("NotificationsAndSounds", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$11$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 9, LocaleController.getString("PinnedMessages", NUM), "pinnedMessageRow", LocaleController.getString("NotificationsAndSounds", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$12$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 10, LocaleController.getString("ResetAllNotifications", NUM), "resetNotificationsRow", LocaleController.getString("NotificationsAndSounds", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$13$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 100, LocaleController.getString("PrivacySettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$14$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 101, LocaleController.getString("BlockedUsers", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$15$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 105, LocaleController.getString("PrivacyPhone", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$16$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 102, LocaleController.getString("PrivacyLastSeen", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$17$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 103, LocaleController.getString("PrivacyProfilePhoto", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$18$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 104, LocaleController.getString("PrivacyForwards", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$19$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 105, LocaleController.getString("PrivacyP2P", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$20$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 106, LocaleController.getString("Calls", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$21$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 107, LocaleController.getString("GroupsAndChannels", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$22$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 108, LocaleController.getString("Passcode", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$23$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 109, LocaleController.getString("TwoStepVerification", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$24$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 110, LocaleController.getString("SessionsTitle", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$25$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 111, LocaleController.getString("PrivacyDeleteCloudDrafts", NUM), "clearDraftsRow", LocaleController.getString("PrivacySettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$26$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 112, LocaleController.getString("DeleteAccountIfAwayFor2", NUM), "deleteAccountRow", LocaleController.getString("PrivacySettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$27$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 113, LocaleController.getString("PrivacyPaymentsClear", NUM), "paymentsClearRow", LocaleController.getString("PrivacySettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$28$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 114, LocaleController.getString("WebSessionsTitle", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$29$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 115, LocaleController.getString("SyncContactsDelete", NUM), "contactsDeleteRow", LocaleController.getString("PrivacySettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$30$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 116, LocaleController.getString("SyncContacts", NUM), "contactsSyncRow", LocaleController.getString("PrivacySettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$31$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 117, LocaleController.getString("SuggestContacts", NUM), "contactsSuggestRow", LocaleController.getString("PrivacySettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$32$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 118, LocaleController.getString("MapPreviewProvider", NUM), "secretMapRow", LocaleController.getString("PrivacySettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$33$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 119, LocaleController.getString("SecretWebPage", NUM), "secretWebpageRow", LocaleController.getString("PrivacySettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$34$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 120, LocaleController.getString("Devices", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$35$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 200, LocaleController.getString("DataSettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$36$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 201, LocaleController.getString("DataUsage", NUM), "usageSectionRow", LocaleController.getString("DataSettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$37$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 202, LocaleController.getString("StorageUsage", NUM), LocaleController.getString("DataSettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$38$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(203, LocaleController.getString("KeepMedia", NUM), "keepMediaRow", LocaleController.getString("DataSettings", NUM), LocaleController.getString(str, NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$39$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(204, LocaleController.getString("ClearMediaCache", NUM), "cacheRow", LocaleController.getString("DataSettings", NUM), LocaleController.getString(str, NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$40$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(205, LocaleController.getString("LocalDatabase", NUM), "databaseRow", LocaleController.getString("DataSettings", NUM), LocaleController.getString(str, NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$41$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 206, LocaleController.getString("NetworkUsage", NUM), LocaleController.getString("DataSettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$42$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 207, LocaleController.getString("AutomaticMediaDownload", NUM), "mediaDownloadSectionRow", LocaleController.getString("DataSettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$43$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 208, LocaleController.getString("WhenUsingMobileData", NUM), LocaleController.getString("DataSettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$44$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 209, LocaleController.getString("WhenConnectedOnWiFi", NUM), LocaleController.getString("DataSettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$45$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 210, LocaleController.getString("WhenRoaming", NUM), LocaleController.getString("DataSettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$46$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 211, LocaleController.getString("ResetAutomaticMediaDownload", NUM), "resetDownloadRow", LocaleController.getString("DataSettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$47$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 212, LocaleController.getString("AutoplayMedia", NUM), "autoplayHeaderRow", LocaleController.getString("DataSettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$48$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 213, LocaleController.getString("AutoplayGIF", NUM), "autoplayGifsRow", LocaleController.getString("DataSettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$49$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 214, LocaleController.getString("AutoplayVideo", NUM), "autoplayVideoRow", LocaleController.getString("DataSettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$50$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 215, LocaleController.getString("Streaming", NUM), "streamSectionRow", LocaleController.getString("DataSettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$51$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 216, LocaleController.getString("EnableStreaming", NUM), "enableStreamRow", LocaleController.getString("DataSettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$52$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 217, LocaleController.getString("Calls", NUM), "callsSectionRow", LocaleController.getString("DataSettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$53$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 218, LocaleController.getString("VoipUseLessData", NUM), "useLessDataForCallsRow", LocaleController.getString("DataSettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$54$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 219, LocaleController.getString("VoipQuickReplies", NUM), "quickRepliesRow", LocaleController.getString("DataSettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$55$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 220, LocaleController.getString("ProxySettings", NUM), LocaleController.getString("DataSettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$56$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(221, LocaleController.getString("UseProxyForCalls", NUM), "callsRow", LocaleController.getString("DataSettings", NUM), LocaleController.getString("ProxySettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$57$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 300, LocaleController.getString("ChatSettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$58$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 301, LocaleController.getString("TextSizeHeader", NUM), "textSizeHeaderRow", LocaleController.getString("ChatSettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$59$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 302, LocaleController.getString("ChatBackground", NUM), LocaleController.getString("ChatSettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$60$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(303, LocaleController.getString("SetColor", NUM), (String) null, LocaleController.getString("ChatSettings", NUM), LocaleController.getString("ChatBackground", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$61$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(304, LocaleController.getString("ResetChatBackgrounds", NUM), "resetRow", LocaleController.getString("ChatSettings", NUM), LocaleController.getString("ChatBackground", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$62$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 305, LocaleController.getString("AutoNightTheme", NUM), LocaleController.getString("ChatSettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$63$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 306, LocaleController.getString("ColorTheme", NUM), "themeHeaderRow", LocaleController.getString("ChatSettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$64$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 307, LocaleController.getString("ChromeCustomTabs", NUM), "customTabsRow", LocaleController.getString("ChatSettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$65$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 308, LocaleController.getString("DirectShare", NUM), "directShareRow", LocaleController.getString("ChatSettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$66$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 309, LocaleController.getString("EnableAnimations", NUM), "enableAnimationsRow", LocaleController.getString("ChatSettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$67$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 310, LocaleController.getString("RaiseToSpeak", NUM), "raiseToSpeakRow", LocaleController.getString("ChatSettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$68$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 311, LocaleController.getString("SendByEnter", NUM), "sendByEnterRow", LocaleController.getString("ChatSettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$69$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 312, LocaleController.getString("SaveToGallerySettings", NUM), "saveToGalleryRow", LocaleController.getString("ChatSettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$70$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 312, LocaleController.getString("DistanceUnits", NUM), "distanceRow", LocaleController.getString("ChatSettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$71$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 313, LocaleController.getString("StickersAndMasks", NUM), LocaleController.getString("ChatSettings", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$72$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(314, LocaleController.getString("SuggestStickers", NUM), "suggestRow", LocaleController.getString("ChatSettings", NUM), LocaleController.getString("StickersAndMasks", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$73$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(315, LocaleController.getString("FeaturedStickers", NUM), (String) null, LocaleController.getString("ChatSettings", NUM), LocaleController.getString("StickersAndMasks", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$74$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(316, LocaleController.getString("Masks", NUM), (String) null, LocaleController.getString("ChatSettings", NUM), LocaleController.getString("StickersAndMasks", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$75$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(317, LocaleController.getString("ArchivedStickers", NUM), (String) null, LocaleController.getString("ChatSettings", NUM), LocaleController.getString("StickersAndMasks", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$76$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(317, LocaleController.getString("ArchivedMasks", NUM), (String) null, LocaleController.getString("ChatSettings", NUM), LocaleController.getString("StickersAndMasks", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$77$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 400, LocaleController.getString("Language", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$78$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 402, LocaleController.getString("AskAQuestion", NUM), LocaleController.getString("SettingsHelp", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$79$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 403, LocaleController.getString("TelegramFAQ", NUM), LocaleController.getString("SettingsHelp", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$80$SettingsActivity$SearchAdapter();
                        }
                    }), new SearchResult(this, 404, LocaleController.getString("PrivacyPolicy", NUM), LocaleController.getString("SettingsHelp", NUM), NUM, new Runnable() {
                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$new$81$SettingsActivity$SearchAdapter();
                        }
                    })};
                    this.mContext = context;
                    HashMap hashMap = new HashMap();
                    int i = 0;
                    while (true) {
                        SearchResult[] searchResultArr = this.searchArray;
                        if (i >= searchResultArr.length) {
                            break;
                        }
                        hashMap.put(Integer.valueOf(searchResultArr[i].guid), this.searchArray[i]);
                        i++;
                    }
                    Set<String> stringSet = MessagesController.getGlobalMainSettings().getStringSet("settingsSearchRecent2", (Set) null);
                    if (stringSet != null) {
                        for (String hexToBytes : stringSet) {
                            try {
                                SerializedData serializedData = new SerializedData(Utilities.hexToBytes(hexToBytes));
                                boolean z = false;
                                int readInt32 = serializedData.readInt32(false);
                                int readInt322 = serializedData.readInt32(false);
                                if (readInt322 == 0) {
                                    String readString = serializedData.readString(false);
                                    int readInt323 = serializedData.readInt32(false);
                                    String[] strArr = null;
                                    if (readInt323 > 0) {
                                        strArr = new String[readInt323];
                                        int i2 = 0;
                                        while (i2 < readInt323) {
                                            strArr[i2] = serializedData.readString(z);
                                            i2++;
                                            z = false;
                                        }
                                    }
                                    FaqSearchResult faqSearchResult = new FaqSearchResult(this, readString, strArr, serializedData.readString(z));
                                    int unused = faqSearchResult.num = readInt32;
                                    this.recentSearches.add(faqSearchResult);
                                } else if (readInt322 == 1) {
                                    try {
                                        SearchResult searchResult = (SearchResult) hashMap.get(Integer.valueOf(serializedData.readInt32(false)));
                                        if (searchResult != null) {
                                            int unused2 = searchResult.num = readInt32;
                                            this.recentSearches.add(searchResult);
                                        }
                                    } catch (Exception unused3) {
                                    }
                                }
                            } catch (Exception unused4) {
                            }
                        }
                    }
                    Collections.sort(this.recentSearches, new Comparator() {
                        public final int compare(Object obj, Object obj2) {
                            return SettingsActivity.SearchAdapter.this.lambda$new$82$SettingsActivity$SearchAdapter(obj, obj2);
                        }
                    });
                }

                public /* synthetic */ int lambda$new$82$SettingsActivity$SearchAdapter(Object obj, Object obj2) {
                    int num = getNum(obj);
                    int num2 = getNum(obj2);
                    if (num < num2) {
                        return -1;
                    }
                    return num > num2 ? 1 : 0;
                }

                /* access modifiers changed from: private */
                public void loadFaqWebPage() {
                    if (this.faqWebPage == null && !this.loadingFaqPage) {
                        this.loadingFaqPage = true;
                        TLRPC$TL_messages_getWebPage tLRPC$TL_messages_getWebPage = new TLRPC$TL_messages_getWebPage();
                        tLRPC$TL_messages_getWebPage.url = LocaleController.getString("TelegramFaqUrl", NUM);
                        tLRPC$TL_messages_getWebPage.hash = 0;
                        ConnectionsManager.getInstance(this.this$0.currentAccount).sendRequest(tLRPC$TL_messages_getWebPage, new RequestDelegate() {
                            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                                SettingsActivity.SearchAdapter.this.lambda$loadFaqWebPage$83$SettingsActivity$SearchAdapter(tLObject, tLRPC$TL_error);
                            }
                        });
                    }
                }

                public /* synthetic */ void lambda$loadFaqWebPage$83$SettingsActivity$SearchAdapter(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    TLRPC$WebPage tLRPC$WebPage;
                    TLRPC$Page tLRPC$Page;
                    if ((tLObject instanceof TLRPC$WebPage) && (tLRPC$Page = tLRPC$WebPage.cached_page) != null) {
                        int size = tLRPC$Page.blocks.size();
                        for (int i = 0; i < size; i++) {
                            TLRPC$PageBlock tLRPC$PageBlock = tLRPC$WebPage.cached_page.blocks.get(i);
                            if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockList) {
                                String str = null;
                                if (i != 0) {
                                    TLRPC$PageBlock tLRPC$PageBlock2 = tLRPC$WebPage.cached_page.blocks.get(i - 1);
                                    if (tLRPC$PageBlock2 instanceof TLRPC$TL_pageBlockParagraph) {
                                        str = ArticleViewer.getPlainText(((TLRPC$TL_pageBlockParagraph) tLRPC$PageBlock2).text).toString();
                                    }
                                }
                                TLRPC$TL_pageBlockList tLRPC$TL_pageBlockList = (TLRPC$TL_pageBlockList) tLRPC$PageBlock;
                                int size2 = tLRPC$TL_pageBlockList.items.size();
                                for (int i2 = 0; i2 < size2; i2++) {
                                    TLRPC$PageListItem tLRPC$PageListItem = tLRPC$TL_pageBlockList.items.get(i2);
                                    if (tLRPC$PageListItem instanceof TLRPC$TL_pageListItemText) {
                                        TLRPC$TL_pageListItemText tLRPC$TL_pageListItemText = (TLRPC$TL_pageListItemText) tLRPC$PageListItem;
                                        String url = ArticleViewer.getUrl(tLRPC$TL_pageListItemText.text);
                                        String charSequence = ArticleViewer.getPlainText(tLRPC$TL_pageListItemText.text).toString();
                                        if (!TextUtils.isEmpty(url) && !TextUtils.isEmpty(charSequence)) {
                                            this.faqSearchArray.add(new FaqSearchResult(this, charSequence, str != null ? new String[]{LocaleController.getString("SettingsSearchFaq", NUM), str} : new String[]{LocaleController.getString("SettingsSearchFaq", NUM)}, url));
                                        }
                                    }
                                }
                            } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockAnchor) {
                                break;
                            }
                        }
                        this.faqWebPage = (tLRPC$WebPage = (TLRPC$WebPage) tLObject);
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
                    } else if (this.recentSearches.isEmpty()) {
                        return 0;
                    } else {
                        return this.recentSearches.size() + 1;
                    }
                }

                public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                    return viewHolder.getItemViewType() == 0;
                }

                public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                    int i2;
                    int itemViewType = viewHolder.getItemViewType();
                    boolean z = true;
                    if (itemViewType == 0) {
                        SettingsSearchCell settingsSearchCell = (SettingsSearchCell) viewHolder.itemView;
                        boolean z2 = false;
                        if (!this.searchWas) {
                            int i3 = i - 1;
                            Object obj = this.recentSearches.get(i3);
                            if (obj instanceof SearchResult) {
                                SearchResult searchResult = (SearchResult) obj;
                                String access$4500 = searchResult.searchTitle;
                                String[] access$4300 = searchResult.path;
                                if (i3 >= this.recentSearches.size() - 1) {
                                    z = false;
                                }
                                settingsSearchCell.setTextAndValue(access$4500, access$4300, false, z);
                            } else if (obj instanceof FaqSearchResult) {
                                FaqSearchResult faqSearchResult = (FaqSearchResult) obj;
                                String access$4600 = faqSearchResult.title;
                                String[] access$4400 = faqSearchResult.path;
                                if (i3 < this.recentSearches.size() - 1) {
                                    z2 = true;
                                }
                                settingsSearchCell.setTextAndValue(access$4600, access$4400, true, z2);
                            }
                        } else if (i < this.searchResults.size()) {
                            SearchResult searchResult2 = this.searchResults.get(i);
                            SearchResult searchResult3 = i > 0 ? this.searchResults.get(i - 1) : null;
                            if (searchResult3 == null || searchResult3.iconResId != searchResult2.iconResId) {
                                i2 = searchResult2.iconResId;
                            } else {
                                i2 = 0;
                            }
                            CharSequence charSequence = this.resultNames.get(i);
                            String[] access$43002 = searchResult2.path;
                            if (i >= this.searchResults.size() - 1) {
                                z = false;
                            }
                            settingsSearchCell.setTextAndValueAndIcon(charSequence, access$43002, i2, z);
                        } else {
                            int size = i - (this.searchResults.size() + 1);
                            CharSequence charSequence2 = this.resultNames.get(this.searchResults.size() + size);
                            String[] access$44002 = this.faqSearchResults.get(size).path;
                            if (size < this.searchResults.size() - 1) {
                                z2 = true;
                            }
                            settingsSearchCell.setTextAndValue(charSequence2, access$44002, true, z2);
                        }
                    } else if (itemViewType == 1) {
                        ((GraySectionCell) viewHolder.itemView).setText(LocaleController.getString("SettingsFaqSearchTitle", NUM));
                    } else if (itemViewType == 2) {
                        ((HeaderCell) viewHolder.itemView).setText(LocaleController.getString("SettingsRecent", NUM));
                    }
                }

                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                    View view;
                    if (i == 0) {
                        view = new SettingsSearchCell(this.mContext);
                    } else if (i != 1) {
                        view = new HeaderCell(this.mContext, 16);
                    } else {
                        view = new GraySectionCell(this.mContext);
                    }
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    return new RecyclerListView.Holder(view);
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
                    this.recentSearches.add(0, obj);
                    if (!this.searchWas) {
                        notifyDataSetChanged();
                    }
                    if (this.recentSearches.size() > 20) {
                        ArrayList<Object> arrayList = this.recentSearches;
                        arrayList.remove(arrayList.size() - 1);
                    }
                    LinkedHashSet linkedHashSet = new LinkedHashSet();
                    int size = this.recentSearches.size();
                    for (int i = 0; i < size; i++) {
                        Object obj2 = this.recentSearches.get(i);
                        if (obj2 instanceof SearchResult) {
                            int unused = ((SearchResult) obj2).num = i;
                        } else if (obj2 instanceof FaqSearchResult) {
                            int unused2 = ((FaqSearchResult) obj2).num = i;
                        }
                        linkedHashSet.add(obj2.toString());
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
                    if (obj instanceof FaqSearchResult) {
                        return ((FaqSearchResult) obj).num;
                    }
                    return 0;
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
                    $$Lambda$SettingsActivity$SearchAdapter$gFQTg_CI3E60WOkp9EeNPvq3p0 r1 = new Runnable(str) {
                        public final /* synthetic */ String f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$search$85$SettingsActivity$SearchAdapter(this.f$1);
                        }
                    };
                    this.searchRunnable = r1;
                    dispatchQueue.postRunnable(r1, 300);
                }

                public /* synthetic */ void lambda$search$85$SettingsActivity$SearchAdapter(String str) {
                    SpannableStringBuilder spannableStringBuilder;
                    int i;
                    String str2;
                    String str3;
                    SpannableStringBuilder spannableStringBuilder2;
                    ArrayList arrayList = new ArrayList();
                    ArrayList arrayList2 = new ArrayList();
                    ArrayList arrayList3 = new ArrayList();
                    String str4 = " ";
                    String[] split = str.split(str4);
                    String[] strArr = new String[split.length];
                    int i2 = 0;
                    while (true) {
                        spannableStringBuilder = null;
                        if (i2 >= split.length) {
                            break;
                        }
                        strArr[i2] = LocaleController.getInstance().getTranslitString(split[i2]);
                        if (strArr[i2].equals(split[i2])) {
                            strArr[i2] = null;
                        }
                        i2++;
                    }
                    int i3 = 0;
                    while (true) {
                        SearchResult[] searchResultArr = this.searchArray;
                        if (i3 >= searchResultArr.length) {
                            break;
                        }
                        SearchResult searchResult = searchResultArr[i3];
                        String str5 = str4 + searchResult.searchTitle.toLowerCase();
                        SpannableStringBuilder spannableStringBuilder3 = spannableStringBuilder;
                        int i4 = 0;
                        while (i4 < split.length) {
                            if (split[i4].length() != 0) {
                                String str6 = split[i4];
                                int indexOf = str5.indexOf(str4 + str6);
                                if (indexOf < 0 && strArr[i4] != null) {
                                    str6 = strArr[i4];
                                    indexOf = str5.indexOf(str4 + str6);
                                }
                                if (indexOf < 0) {
                                    break;
                                }
                                spannableStringBuilder2 = spannableStringBuilder3 == null ? new SpannableStringBuilder(searchResult.searchTitle) : spannableStringBuilder3;
                                str3 = str5;
                                spannableStringBuilder2.setSpan(new ForegroundColorSpan(Theme.getColor("windowBackgroundWhiteBlueText4")), indexOf, str6.length() + indexOf, 33);
                            } else {
                                str3 = str5;
                                spannableStringBuilder2 = spannableStringBuilder3;
                            }
                            if (spannableStringBuilder2 != null && i4 == split.length - 1) {
                                if (searchResult.guid == 502) {
                                    int i5 = -1;
                                    int i6 = 0;
                                    while (true) {
                                        if (i6 >= 3) {
                                            break;
                                        } else if (!UserConfig.getInstance(i3).isClientActivated()) {
                                            i5 = i6;
                                            break;
                                        } else {
                                            i6++;
                                        }
                                    }
                                    if (i5 < 0) {
                                    }
                                }
                                arrayList.add(searchResult);
                                arrayList3.add(spannableStringBuilder2);
                            }
                            i4++;
                            String str7 = str;
                            spannableStringBuilder3 = spannableStringBuilder2;
                            str5 = str3;
                        }
                        i3++;
                        String str8 = str;
                        spannableStringBuilder = null;
                    }
                    if (this.faqWebPage != null) {
                        int size = this.faqSearchArray.size();
                        int i7 = 0;
                        while (i7 < size) {
                            FaqSearchResult faqSearchResult = this.faqSearchArray.get(i7);
                            String str9 = str4 + faqSearchResult.title.toLowerCase();
                            int i8 = 0;
                            SpannableStringBuilder spannableStringBuilder4 = null;
                            while (i8 < split.length) {
                                if (split[i8].length() != 0) {
                                    String str10 = split[i8];
                                    int indexOf2 = str9.indexOf(str4 + str10);
                                    if (indexOf2 < 0 && strArr[i8] != null) {
                                        str10 = strArr[i8];
                                        indexOf2 = str9.indexOf(str4 + str10);
                                    }
                                    if (indexOf2 < 0) {
                                        break;
                                    }
                                    if (spannableStringBuilder4 == null) {
                                        str2 = str4;
                                        spannableStringBuilder4 = new SpannableStringBuilder(faqSearchResult.title);
                                    } else {
                                        str2 = str4;
                                    }
                                    i = size;
                                    spannableStringBuilder4.setSpan(new ForegroundColorSpan(Theme.getColor("windowBackgroundWhiteBlueText4")), indexOf2, str10.length() + indexOf2, 33);
                                } else {
                                    str2 = str4;
                                    i = size;
                                }
                                if (spannableStringBuilder4 != null && i8 == split.length - 1) {
                                    arrayList2.add(faqSearchResult);
                                    arrayList3.add(spannableStringBuilder4);
                                }
                                i8++;
                                str4 = str2;
                                size = i;
                            }
                            i7++;
                            str4 = str4;
                            size = size;
                        }
                    }
                    AndroidUtilities.runOnUIThread(new Runnable(str, arrayList, arrayList2, arrayList3) {
                        public final /* synthetic */ String f$1;
                        public final /* synthetic */ ArrayList f$2;
                        public final /* synthetic */ ArrayList f$3;
                        public final /* synthetic */ ArrayList f$4;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                        }

                        public final void run() {
                            SettingsActivity.SearchAdapter.this.lambda$null$84$SettingsActivity$SearchAdapter(this.f$1, this.f$2, this.f$3, this.f$4);
                        }
                    });
                }

                public /* synthetic */ void lambda$null$84$SettingsActivity$SearchAdapter(String str, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3) {
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

            private class ListAdapter extends RecyclerListView.SelectionAdapter {
                private Context mContext;

                public ListAdapter(Context context) {
                    this.mContext = context;
                }

                public int getItemCount() {
                    return SettingsActivity.this.rowCount;
                }

                public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                    String str;
                    String str2;
                    String str3;
                    int itemViewType = viewHolder.getItemViewType();
                    boolean z = false;
                    if (itemViewType == 2) {
                        TextCell textCell = (TextCell) viewHolder.itemView;
                        if (i == SettingsActivity.this.languageRow) {
                            textCell.setTextAndIcon(LocaleController.getString("Language", NUM), NUM, false);
                        } else if (i == SettingsActivity.this.notificationRow) {
                            textCell.setTextAndIcon(LocaleController.getString("NotificationsAndSounds", NUM), NUM, true);
                        } else if (i == SettingsActivity.this.privacyRow) {
                            textCell.setTextAndIcon(LocaleController.getString("PrivacySettings", NUM), NUM, true);
                        } else if (i == SettingsActivity.this.dataRow) {
                            textCell.setTextAndIcon(LocaleController.getString("DataSettings", NUM), NUM, true);
                        } else if (i == SettingsActivity.this.chatRow) {
                            textCell.setTextAndIcon(LocaleController.getString("ChatSettings", NUM), NUM, true);
                        } else if (i == SettingsActivity.this.filtersRow) {
                            textCell.setTextAndIcon(LocaleController.getString("Filters", NUM), NUM, true);
                        } else if (i == SettingsActivity.this.questionRow) {
                            textCell.setTextAndIcon(LocaleController.getString("AskAQuestion", NUM), NUM, true);
                        } else if (i == SettingsActivity.this.faqRow) {
                            textCell.setTextAndIcon(LocaleController.getString("TelegramFAQ", NUM), NUM, true);
                        } else if (i == SettingsActivity.this.policyRow) {
                            textCell.setTextAndIcon(LocaleController.getString("PrivacyPolicy", NUM), NUM, false);
                        } else if (i == SettingsActivity.this.sendLogsRow) {
                            textCell.setText(LocaleController.getString("DebugSendLogs", NUM), true);
                        } else if (i == SettingsActivity.this.clearLogsRow) {
                            String string = LocaleController.getString("DebugClearLogs", NUM);
                            if (SettingsActivity.this.switchBackendRow != -1) {
                                z = true;
                            }
                            textCell.setText(string, z);
                        } else if (i == SettingsActivity.this.switchBackendRow) {
                            textCell.setText("Switch Backend", false);
                        } else if (i == SettingsActivity.this.devicesRow) {
                            textCell.setTextAndIcon(LocaleController.getString("Devices", NUM), NUM, true);
                        }
                    } else if (itemViewType == 4) {
                        HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                        if (i == SettingsActivity.this.settingsSectionRow2) {
                            headerCell.setText(LocaleController.getString("SETTINGS", NUM));
                        } else if (i == SettingsActivity.this.numberSectionRow) {
                            headerCell.setText(LocaleController.getString("Account", NUM));
                        } else if (i == SettingsActivity.this.helpHeaderRow) {
                            headerCell.setText(LocaleController.getString("SettingsHelp", NUM));
                        } else if (i == SettingsActivity.this.debugHeaderRow) {
                            headerCell.setText(LocaleController.getString("SettingsDebug", NUM));
                        }
                    } else if (itemViewType == 6) {
                        TextDetailCell textDetailCell = (TextDetailCell) viewHolder.itemView;
                        if (i == SettingsActivity.this.numberRow) {
                            TLRPC$User currentUser = UserConfig.getInstance(SettingsActivity.this.currentAccount).getCurrentUser();
                            if (currentUser == null || (str3 = currentUser.phone) == null || str3.length() == 0) {
                                str2 = LocaleController.getString("NumberUnknown", NUM);
                            } else {
                                str2 = PhoneFormat.getInstance().format("+" + currentUser.phone);
                            }
                            textDetailCell.setTextAndValue(str2, LocaleController.getString("TapToChangePhone", NUM), true);
                        } else if (i == SettingsActivity.this.usernameRow) {
                            TLRPC$User currentUser2 = UserConfig.getInstance(SettingsActivity.this.currentAccount).getCurrentUser();
                            if (currentUser2 == null || TextUtils.isEmpty(currentUser2.username)) {
                                str = LocaleController.getString("UsernameEmpty", NUM);
                            } else {
                                str = "@" + currentUser2.username;
                            }
                            textDetailCell.setTextAndValue(str, LocaleController.getString("Username", NUM), true);
                        } else if (i == SettingsActivity.this.bioRow) {
                            String str4 = null;
                            if (SettingsActivity.this.userInfo == null || !TextUtils.isEmpty(SettingsActivity.this.userInfo.about)) {
                                textDetailCell.setTextWithEmojiAndValue(SettingsActivity.this.userInfo == null ? LocaleController.getString("Loading", NUM) : SettingsActivity.this.userInfo.about, LocaleController.getString("UserBio", NUM), false);
                                SettingsActivity settingsActivity = SettingsActivity.this;
                                if (settingsActivity.userInfo != null) {
                                    str4 = SettingsActivity.this.userInfo.about;
                                }
                                String unused = settingsActivity.currentBio = str4;
                                return;
                            }
                            textDetailCell.setTextAndValue(LocaleController.getString("UserBio", NUM), LocaleController.getString("UserBioDetail", NUM), false);
                            String unused2 = SettingsActivity.this.currentBio = null;
                        }
                    }
                }

                public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                    int adapterPosition = viewHolder.getAdapterPosition();
                    return adapterPosition == SettingsActivity.this.notificationRow || adapterPosition == SettingsActivity.this.numberRow || adapterPosition == SettingsActivity.this.privacyRow || adapterPosition == SettingsActivity.this.languageRow || adapterPosition == SettingsActivity.this.usernameRow || adapterPosition == SettingsActivity.this.bioRow || adapterPosition == SettingsActivity.this.versionRow || adapterPosition == SettingsActivity.this.dataRow || adapterPosition == SettingsActivity.this.chatRow || adapterPosition == SettingsActivity.this.questionRow || adapterPosition == SettingsActivity.this.devicesRow || adapterPosition == SettingsActivity.this.filtersRow || adapterPosition == SettingsActivity.this.faqRow || adapterPosition == SettingsActivity.this.policyRow || adapterPosition == SettingsActivity.this.sendLogsRow || adapterPosition == SettingsActivity.this.clearLogsRow || adapterPosition == SettingsActivity.this.switchBackendRow;
                }

                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                    int i2 = i;
                    View view = null;
                    if (i2 == 0) {
                        view = new EmptyCell(this.mContext, LocaleController.isRTL ? 46 : 36);
                    } else if (i2 == 1) {
                        view = new ShadowSectionCell(this.mContext);
                        CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        combinedDrawable.setFullsize(true);
                        view.setBackgroundDrawable(combinedDrawable);
                    } else if (i2 == 2) {
                        view = new TextCell(this.mContext);
                    } else if (i2 == 4) {
                        view = new HeaderCell(this.mContext, 23);
                    } else if (i2 == 5) {
                        TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(this.mContext, 10);
                        textInfoPrivacyCell.getTextView().setGravity(1);
                        textInfoPrivacyCell.getTextView().setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
                        textInfoPrivacyCell.getTextView().setMovementMethod((MovementMethod) null);
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        try {
                            PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                            int i3 = packageInfo.versionCode / 10;
                            String str = "";
                            switch (packageInfo.versionCode % 10) {
                                case 0:
                                case 9:
                                    str = "universal " + Build.CPU_ABI + " " + Build.CPU_ABI2;
                                    break;
                                case 1:
                                case 3:
                                    str = "arm-v7a";
                                    break;
                                case 2:
                                case 4:
                                    str = "x86";
                                    break;
                                case 5:
                                case 7:
                                    str = "arm64-v8a";
                                    break;
                                case 6:
                                case 8:
                                    str = "x86_64";
                                    break;
                            }
                            textInfoPrivacyCell.setText(LocaleController.formatString("TelegramVersion", NUM, String.format(Locale.US, "v%s (%d) %s", new Object[]{packageInfo.versionName, Integer.valueOf(i3), str})));
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                        textInfoPrivacyCell.getTextView().setPadding(0, AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(14.0f));
                        CombinedDrawable combinedDrawable2 = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        combinedDrawable2.setFullsize(true);
                        textInfoPrivacyCell.setBackgroundDrawable(combinedDrawable2);
                        view = textInfoPrivacyCell;
                    } else if (i2 == 6) {
                        view = new TextDetailCell(this.mContext);
                    }
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    return new RecyclerListView.Holder(view);
                }

                public int getItemViewType(int i) {
                    if (i == SettingsActivity.this.emptyRow) {
                        return 0;
                    }
                    if (i == SettingsActivity.this.settingsSectionRow || i == SettingsActivity.this.devicesSectionRow || i == SettingsActivity.this.helpSectionCell) {
                        return 1;
                    }
                    if (i == SettingsActivity.this.notificationRow || i == SettingsActivity.this.privacyRow || i == SettingsActivity.this.languageRow || i == SettingsActivity.this.dataRow || i == SettingsActivity.this.chatRow || i == SettingsActivity.this.questionRow || i == SettingsActivity.this.devicesRow || i == SettingsActivity.this.filtersRow || i == SettingsActivity.this.faqRow || i == SettingsActivity.this.policyRow || i == SettingsActivity.this.sendLogsRow || i == SettingsActivity.this.clearLogsRow || i == SettingsActivity.this.switchBackendRow) {
                        return 2;
                    }
                    if (i == SettingsActivity.this.versionRow) {
                        return 5;
                    }
                    if (i == SettingsActivity.this.numberRow || i == SettingsActivity.this.usernameRow || i == SettingsActivity.this.bioRow) {
                        return 6;
                    }
                    if (i == SettingsActivity.this.settingsSectionRow2 || i == SettingsActivity.this.numberSectionRow || i == SettingsActivity.this.helpHeaderRow || i == SettingsActivity.this.debugHeaderRow) {
                        return 4;
                    }
                    return 2;
                }
            }

            public ArrayList<ThemeDescription> getThemeDescriptions() {
                ArrayList<ThemeDescription> arrayList = new ArrayList<>();
                arrayList.add(new ThemeDescription(this.fragmentView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
                arrayList.add(new ThemeDescription(this.fragmentView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
                arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_backgroundActionBarBlue"));
                arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_backgroundActionBarBlue"));
                arrayList.add(new ThemeDescription(this.topView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_backgroundActionBarBlue"));
                arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_actionBarIconBlue"));
                arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
                arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_actionBarSelectorBlue"));
                arrayList.add(new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_title"));
                arrayList.add(new ThemeDescription(this.onlineTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_status"));
                arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuBackground"));
                arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuItem"));
                arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_AB_SUBMENUITEM, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuItemIcon"));
                arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
                arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
                arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
                arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
                arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
                arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
                arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
                arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
                arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
                arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
                arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
                arrayList.add(new ThemeDescription(this.avatarImage, 0, (Class[]) null, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
                arrayList.add(new ThemeDescription(this.avatarImage, 0, (Class[]) null, (Paint) null, new Drawable[]{this.avatarDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_backgroundInProfileBlue"));
                arrayList.add(new ThemeDescription(this.writeButton, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_actionIcon"));
                arrayList.add(new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_actionBackground"));
                arrayList.add(new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_actionPressedBackground"));
                arrayList.add(new ThemeDescription((View) this.searchListView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
                arrayList.add(new ThemeDescription((View) this.searchListView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_graySectionText"));
                arrayList.add(new ThemeDescription(this.searchListView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "graySection"));
                arrayList.add(new ThemeDescription((View) this.searchListView, 0, new Class[]{SettingsSearchCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription((View) this.searchListView, 0, new Class[]{SettingsSearchCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
                arrayList.add(new ThemeDescription((View) this.searchListView, 0, new Class[]{SettingsSearchCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
                return arrayList;
            }
        }
