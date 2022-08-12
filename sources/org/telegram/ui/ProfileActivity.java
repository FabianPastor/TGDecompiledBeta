package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.util.Property;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Keep;
import androidx.collection.LongSparseArray;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LanguageDetector;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$BotInfo;
import org.telegram.tgnet.TLRPC$ChannelLocation;
import org.telegram.tgnet.TLRPC$ChannelParticipant;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$ChatParticipant;
import org.telegram.tgnet.TLRPC$ChatPhoto;
import org.telegram.tgnet.TLRPC$DecryptedMessageAction;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$InputFile;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$InputPhoto;
import org.telegram.tgnet.TLRPC$MessageAction;
import org.telegram.tgnet.TLRPC$Page;
import org.telegram.tgnet.TLRPC$PageBlock;
import org.telegram.tgnet.TLRPC$PageListItem;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$PrivacyRule;
import org.telegram.tgnet.TLRPC$ReplyMarkup;
import org.telegram.tgnet.TLRPC$TL_channelFull;
import org.telegram.tgnet.TLRPC$TL_channelLocation;
import org.telegram.tgnet.TLRPC$TL_channelParticipant;
import org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin;
import org.telegram.tgnet.TLRPC$TL_channelParticipantCreator;
import org.telegram.tgnet.TLRPC$TL_channelParticipantsRecent;
import org.telegram.tgnet.TLRPC$TL_channels_channelParticipant;
import org.telegram.tgnet.TLRPC$TL_channels_channelParticipants;
import org.telegram.tgnet.TLRPC$TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC$TL_chatAdminRights;
import org.telegram.tgnet.TLRPC$TL_chatBannedRights;
import org.telegram.tgnet.TLRPC$TL_chatChannelParticipant;
import org.telegram.tgnet.TLRPC$TL_chatFull;
import org.telegram.tgnet.TLRPC$TL_chatParticipant;
import org.telegram.tgnet.TLRPC$TL_chatParticipantAdmin;
import org.telegram.tgnet.TLRPC$TL_chatParticipants;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionSetMessageTTL;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputGroupCall;
import org.telegram.tgnet.TLRPC$TL_messageEncryptedAction;
import org.telegram.tgnet.TLRPC$TL_messages_getWebPage;
import org.telegram.tgnet.TLRPC$TL_pageBlockAnchor;
import org.telegram.tgnet.TLRPC$TL_pageBlockList;
import org.telegram.tgnet.TLRPC$TL_pageBlockParagraph;
import org.telegram.tgnet.TLRPC$TL_pageListItemText;
import org.telegram.tgnet.TLRPC$TL_peerNotifySettings;
import org.telegram.tgnet.TLRPC$TL_peerUser;
import org.telegram.tgnet.TLRPC$TL_photos_photo;
import org.telegram.tgnet.TLRPC$TL_photos_uploadProfilePhoto;
import org.telegram.tgnet.TLRPC$TL_privacyValueAllowAll;
import org.telegram.tgnet.TLRPC$TL_privacyValueAllowContacts;
import org.telegram.tgnet.TLRPC$TL_privacyValueDisallowAll;
import org.telegram.tgnet.TLRPC$TL_userEmpty;
import org.telegram.tgnet.TLRPC$TL_userProfilePhoto;
import org.telegram.tgnet.TLRPC$TL_userProfilePhotoEmpty;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserFull;
import org.telegram.tgnet.TLRPC$UserProfilePhoto;
import org.telegram.tgnet.TLRPC$UserStatus;
import org.telegram.tgnet.TLRPC$VideoSize;
import org.telegram.tgnet.TLRPC$WebPage;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.AboutLinkCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.SettingsSearchCell;
import org.telegram.ui.Cells.SettingsSuggestionCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextDetailCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.ChatRightsEditActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.AudioPlayerAlert;
import org.telegram.ui.Components.AutoDeletePopupWrapper;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackButtonMenu;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ChatNotificationsPopupWrapper;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CrossfadeDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.HintView;
import org.telegram.ui.Components.ImageUpdater;
import org.telegram.ui.Components.PopupSwipeBackLayout;
import org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet;
import org.telegram.ui.Components.ProfileGalleryView;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScamDrawable;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.SharedMediaLayout;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StickerEmptyView;
import org.telegram.ui.Components.TimerDrawable;
import org.telegram.ui.Components.TranslateAlert;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.GroupCreateActivity;
import org.telegram.ui.PhotoViewer;

public class ProfileActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, DialogsActivity.DialogsActivityDelegate, SharedMediaLayout.SharedMediaPreloaderDelegate, ImageUpdater.ImageUpdaterDelegate, SharedMediaLayout.Delegate {
    private Property<ActionBar, Float> ACTIONBAR_HEADER_PROGRESS;
    /* access modifiers changed from: private */
    public final Property<ProfileActivity, Float> HEADER_SHADOW;
    /* access modifiers changed from: private */
    public AboutLinkCell aboutLinkCell;
    private int actionBarAnimationColorFrom;
    /* access modifiers changed from: private */
    public Paint actionBarBackgroundPaint;
    /* access modifiers changed from: private */
    public int addMemberRow;
    /* access modifiers changed from: private */
    public int addToGroupButtonRow;
    /* access modifiers changed from: private */
    public int addToGroupInfoRow;
    /* access modifiers changed from: private */
    public int administratorsRow;
    private boolean allowProfileAnimation;
    /* access modifiers changed from: private */
    public boolean allowPullingDown;
    /* access modifiers changed from: private */
    public ActionBarMenuItem animatingItem;
    /* access modifiers changed from: private */
    public float animationProgress;
    private ActionBarMenuSubItem autoDeleteItem;
    TimerDrawable autoDeleteItemDrawable;
    AutoDeletePopupWrapper autoDeletePopupWrapper;
    private TLRPC$FileLocation avatar;
    /* access modifiers changed from: private */
    public AnimatorSet avatarAnimation;
    /* access modifiers changed from: private */
    public TLRPC$FileLocation avatarBig;
    private int avatarColor;
    /* access modifiers changed from: private */
    public FrameLayout avatarContainer;
    /* access modifiers changed from: private */
    public FrameLayout avatarContainer2;
    private AvatarDrawable avatarDrawable;
    /* access modifiers changed from: private */
    public AvatarImageView avatarImage;
    /* access modifiers changed from: private */
    public RadialProgressView avatarProgressView;
    private float avatarScale;
    private float avatarX;
    private float avatarY;
    /* access modifiers changed from: private */
    public ProfileGalleryView avatarsViewPager;
    /* access modifiers changed from: private */
    public PagerIndicatorView avatarsViewPagerIndicatorView;
    /* access modifiers changed from: private */
    public long banFromGroup;
    /* access modifiers changed from: private */
    public int bioRow;
    /* access modifiers changed from: private */
    public int blockedUsersRow;
    /* access modifiers changed from: private */
    public TLRPC$BotInfo botInfo;
    /* access modifiers changed from: private */
    public int bottomPaddingRow;
    /* access modifiers changed from: private */
    public ActionBarMenuItem callItem;
    /* access modifiers changed from: private */
    public boolean callItemVisible;
    private RLottieDrawable cameraDrawable;
    private boolean canSearchMembers;
    /* access modifiers changed from: private */
    public RLottieDrawable cellCameraDrawable;
    /* access modifiers changed from: private */
    public int channelInfoRow;
    /* access modifiers changed from: private */
    public long chatId;
    /* access modifiers changed from: private */
    public TLRPC$ChatFull chatInfo;
    /* access modifiers changed from: private */
    public int chatRow;
    /* access modifiers changed from: private */
    public int clearLogsRow;
    /* access modifiers changed from: private */
    public NestedFrameLayout contentView;
    /* access modifiers changed from: private */
    public boolean creatingChat;
    /* access modifiers changed from: private */
    public CharSequence currentBio;
    private TLRPC$ChannelParticipant currentChannelParticipant;
    /* access modifiers changed from: private */
    public TLRPC$Chat currentChat;
    /* access modifiers changed from: private */
    public TLRPC$EncryptedChat currentEncryptedChat;
    /* access modifiers changed from: private */
    public float currentExpanAnimatorFracture;
    /* access modifiers changed from: private */
    public int dataRow;
    /* access modifiers changed from: private */
    public int debugHeaderRow;
    /* access modifiers changed from: private */
    public int devicesRow;
    /* access modifiers changed from: private */
    public int devicesSectionRow;
    /* access modifiers changed from: private */
    public long dialogId;
    /* access modifiers changed from: private */
    public boolean disableProfileAnimation;
    /* access modifiers changed from: private */
    public boolean doNotSetForeground;
    /* access modifiers changed from: private */
    public ActionBarMenuItem editItem;
    /* access modifiers changed from: private */
    public boolean editItemVisible;
    /* access modifiers changed from: private */
    public int emptyRow;
    /* access modifiers changed from: private */
    public StickerEmptyView emptyView;
    /* access modifiers changed from: private */
    public ValueAnimator expandAnimator;
    private float[] expandAnimatorValues;
    /* access modifiers changed from: private */
    public boolean expandPhoto;
    private float expandProgress;
    /* access modifiers changed from: private */
    public float extraHeight;
    /* access modifiers changed from: private */
    public int faqRow;
    /* access modifiers changed from: private */
    public int filtersRow;
    /* access modifiers changed from: private */
    public boolean firstLayout;
    /* access modifiers changed from: private */
    public boolean fragmentOpened;
    /* access modifiers changed from: private */
    public HintView fwdRestrictedHint;
    private boolean hasVoiceChatItem;
    /* access modifiers changed from: private */
    public AnimatorSet headerAnimatorSet;
    protected float headerShadowAlpha;
    /* access modifiers changed from: private */
    public AnimatorSet headerShadowAnimatorSet;
    /* access modifiers changed from: private */
    public int helpHeaderRow;
    /* access modifiers changed from: private */
    public int helpSectionCell;
    /* access modifiers changed from: private */
    public ImageUpdater imageUpdater;
    /* access modifiers changed from: private */
    public int infoHeaderRow;
    /* access modifiers changed from: private */
    public int infoSectionRow;
    /* access modifiers changed from: private */
    public float initialAnimationExtraHeight;
    /* access modifiers changed from: private */
    public boolean invalidateScroll;
    /* access modifiers changed from: private */
    public boolean isBot;
    public boolean isFragmentOpened;
    /* access modifiers changed from: private */
    public boolean isInLandscapeMode;
    private boolean[] isOnline;
    /* access modifiers changed from: private */
    public boolean isPulledDown;
    private boolean isQrItemVisible;
    /* access modifiers changed from: private */
    public int joinRow;
    /* access modifiers changed from: private */
    public int languageRow;
    /* access modifiers changed from: private */
    public int lastMeasuredContentHeight;
    /* access modifiers changed from: private */
    public int lastMeasuredContentWidth;
    /* access modifiers changed from: private */
    public int lastSectionRow;
    /* access modifiers changed from: private */
    public LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public ListAdapter listAdapter;
    /* access modifiers changed from: private */
    public int listContentHeight;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public float listViewVelocityY;
    private boolean loadingUsers;
    /* access modifiers changed from: private */
    public int locationRow;
    /* access modifiers changed from: private */
    public Drawable lockIconDrawable;
    private AudioPlayerAlert.ClippingTextViewSwitcher mediaCounterTextView;
    /* access modifiers changed from: private */
    public float mediaHeaderAnimationProgress;
    /* access modifiers changed from: private */
    public boolean mediaHeaderVisible;
    /* access modifiers changed from: private */
    public int membersEndRow;
    /* access modifiers changed from: private */
    public int membersHeaderRow;
    /* access modifiers changed from: private */
    public int membersSectionRow;
    /* access modifiers changed from: private */
    public int membersStartRow;
    private long mergeDialogId;
    /* access modifiers changed from: private */
    public SimpleTextView[] nameTextView;
    /* access modifiers changed from: private */
    public String nameTextViewRightDrawableContentDescription;
    private float nameX;
    private float nameY;
    private int navigationBarAnimationColorFrom;
    private boolean needSendMessage;
    private boolean needTimerImage;
    /* access modifiers changed from: private */
    public int notificationRow;
    /* access modifiers changed from: private */
    public int notificationsDividerRow;
    /* access modifiers changed from: private */
    public int notificationsRow;
    /* access modifiers changed from: private */
    public int numberRow;
    /* access modifiers changed from: private */
    public int numberSectionRow;
    private int onlineCount;
    /* access modifiers changed from: private */
    public SimpleTextView[] onlineTextView;
    private float onlineX;
    private float onlineY;
    /* access modifiers changed from: private */
    public boolean openAnimationInProgress;
    /* access modifiers changed from: private */
    public boolean openingAvatar;
    /* access modifiers changed from: private */
    public ActionBarMenuItem otherItem;
    /* access modifiers changed from: private */
    public int overlayCountVisible;
    /* access modifiers changed from: private */
    public OverlaysView overlaysView;
    /* access modifiers changed from: private */
    public LongSparseArray<TLRPC$ChatParticipant> participantsMap;
    /* access modifiers changed from: private */
    public int passwordSuggestionRow;
    /* access modifiers changed from: private */
    public int passwordSuggestionSectionRow;
    /* access modifiers changed from: private */
    public int phoneRow;
    /* access modifiers changed from: private */
    public int phoneSuggestionRow;
    /* access modifiers changed from: private */
    public int phoneSuggestionSectionRow;
    PinchToZoomHelper pinchToZoomHelper;
    /* access modifiers changed from: private */
    public int playProfileAnimation;
    /* access modifiers changed from: private */
    public int policyRow;
    /* access modifiers changed from: private */
    public HashMap<Integer, Integer> positionToOffset;
    /* access modifiers changed from: private */
    public int premiumRow;
    /* access modifiers changed from: private */
    public int premiumSectionsRow;
    /* access modifiers changed from: private */
    public Drawable premiumStarDrawable;
    private CrossfadeDrawable premuimCrossfadeDrawable;
    private ImageLocation prevLoadedImageLocation;
    ChatActivity previousTransitionFragment;
    /* access modifiers changed from: private */
    public int privacyRow;
    boolean profileTransitionInProgress;
    private PhotoViewer.PhotoViewerProvider provider;
    /* access modifiers changed from: private */
    public ActionBarMenuItem qrItem;
    /* access modifiers changed from: private */
    public AnimatorSet qrItemAnimation;
    /* access modifiers changed from: private */
    public int questionRow;
    private boolean recreateMenuAfterAnimation;
    /* access modifiers changed from: private */
    public Rect rect;
    /* access modifiers changed from: private */
    public int reportRow;
    /* access modifiers changed from: private */
    public boolean reportSpam;
    /* access modifiers changed from: private */
    public Theme.ResourcesProvider resourcesProvider;
    /* access modifiers changed from: private */
    public int rowCount;
    int savedScrollOffset;
    int savedScrollPosition;
    /* access modifiers changed from: private */
    public ScamDrawable scamDrawable;
    private AnimatorSet scrimAnimatorSet;
    /* access modifiers changed from: private */
    public Paint scrimPaint;
    /* access modifiers changed from: private */
    public View scrimView;
    /* access modifiers changed from: private */
    public boolean scrolling;
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
    /* access modifiers changed from: private */
    public Animator searchViewTransition;
    /* access modifiers changed from: private */
    public int secretSettingsSectionRow;
    private long selectedUser;
    /* access modifiers changed from: private */
    public int sendLastLogsRow;
    /* access modifiers changed from: private */
    public int sendLogsRow;
    /* access modifiers changed from: private */
    public int sendMessageRow;
    /* access modifiers changed from: private */
    public TextCell setAvatarCell;
    /* access modifiers changed from: private */
    public int setAvatarRow;
    /* access modifiers changed from: private */
    public int setAvatarSectionRow;
    /* access modifiers changed from: private */
    public int setUsernameRow;
    /* access modifiers changed from: private */
    public int settingsKeyRow;
    /* access modifiers changed from: private */
    public int settingsSectionRow;
    /* access modifiers changed from: private */
    public int settingsSectionRow2;
    /* access modifiers changed from: private */
    public int settingsTimerRow;
    /* access modifiers changed from: private */
    public SharedMediaLayout sharedMediaLayout;
    /* access modifiers changed from: private */
    public boolean sharedMediaLayoutAttached;
    private SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader;
    /* access modifiers changed from: private */
    public int sharedMediaRow;
    /* access modifiers changed from: private */
    public ArrayList<Integer> sortedUsers;
    /* access modifiers changed from: private */
    public int stickersRow;
    /* access modifiers changed from: private */
    public int subscribersRequestsRow;
    /* access modifiers changed from: private */
    public int subscribersRow;
    /* access modifiers changed from: private */
    public int switchBackendRow;
    private ImageView timeItem;
    private TimerDrawable timerDrawable;
    /* access modifiers changed from: private */
    public TopView topView;
    /* access modifiers changed from: private */
    public boolean transitionAnimationInProress;
    private int transitionIndex;
    /* access modifiers changed from: private */
    public View transitionOnlineText;
    /* access modifiers changed from: private */
    public ImageView ttlIconView;
    /* access modifiers changed from: private */
    public int unblockRow;
    /* access modifiers changed from: private */
    public UndoView undoView;
    private ImageLocation uploadingImageLocation;
    /* access modifiers changed from: private */
    public boolean userBlocked;
    /* access modifiers changed from: private */
    public long userId;
    /* access modifiers changed from: private */
    public TLRPC$UserFull userInfo;
    /* access modifiers changed from: private */
    public int userInfoRow;
    /* access modifiers changed from: private */
    public int usernameRow;
    /* access modifiers changed from: private */
    public boolean usersEndReached;
    private int usersForceShowingIn;
    /* access modifiers changed from: private */
    public Drawable verifiedCheckDrawable;
    private CrossfadeDrawable verifiedCrossfadeDrawable;
    /* access modifiers changed from: private */
    public Drawable verifiedDrawable;
    /* access modifiers changed from: private */
    public int versionRow;
    /* access modifiers changed from: private */
    public ActionBarMenuItem videoCallItem;
    /* access modifiers changed from: private */
    public boolean videoCallItemVisible;
    /* access modifiers changed from: private */
    public final ArrayList<TLRPC$ChatParticipant> visibleChatParticipants;
    /* access modifiers changed from: private */
    public final ArrayList<Integer> visibleSortedUsers;
    /* access modifiers changed from: private */
    public Paint whitePaint;
    /* access modifiers changed from: private */
    public RLottieImageView writeButton;
    /* access modifiers changed from: private */
    public AnimatorSet writeButtonAnimation;

    public /* synthetic */ String getInitialSearchString() {
        return ImageUpdater.ImageUpdaterDelegate.CC.$default$getInitialSearchString(this);
    }

    static /* synthetic */ int access$7512(ProfileActivity profileActivity, int i) {
        int i2 = profileActivity.listContentHeight + i;
        profileActivity.listContentHeight = i2;
        return i2;
    }

    public static class AvatarImageView extends BackupImageView {
        ProfileGalleryView avatarsViewPager;
        private ImageReceiver.BitmapHolder drawableHolder;
        private float foregroundAlpha;
        private ImageReceiver foregroundImageReceiver = new ImageReceiver(this);
        private final Paint placeholderPaint;
        private final RectF rect = new RectF();

        public void setAvatarsViewPager(ProfileGalleryView profileGalleryView) {
            this.avatarsViewPager = profileGalleryView;
        }

        public AvatarImageView(Context context) {
            super(context);
            Paint paint = new Paint(1);
            this.placeholderPaint = paint;
            paint.setColor(-16777216);
        }

        public void setForegroundImage(ImageLocation imageLocation, String str, Drawable drawable) {
            this.foregroundImageReceiver.setImage(imageLocation, str, drawable, 0, (String) null, (Object) null, 0);
            ImageReceiver.BitmapHolder bitmapHolder = this.drawableHolder;
            if (bitmapHolder != null) {
                bitmapHolder.release();
                this.drawableHolder = null;
            }
        }

        public void setForegroundImageDrawable(ImageReceiver.BitmapHolder bitmapHolder) {
            if (bitmapHolder != null) {
                this.foregroundImageReceiver.setImageBitmap(bitmapHolder.drawable);
            }
            ImageReceiver.BitmapHolder bitmapHolder2 = this.drawableHolder;
            if (bitmapHolder2 != null) {
                bitmapHolder2.release();
                this.drawableHolder = null;
            }
            this.drawableHolder = bitmapHolder;
        }

        public float getForegroundAlpha() {
            return this.foregroundAlpha;
        }

        public void setForegroundAlpha(float f) {
            this.foregroundAlpha = f;
            invalidate();
        }

        public void clearForeground() {
            AnimatedFileDrawable animation = this.foregroundImageReceiver.getAnimation();
            if (animation != null) {
                animation.removeSecondParentView(this);
            }
            this.foregroundImageReceiver.clearImage();
            ImageReceiver.BitmapHolder bitmapHolder = this.drawableHolder;
            if (bitmapHolder != null) {
                bitmapHolder.release();
                this.drawableHolder = null;
            }
            this.foregroundAlpha = 0.0f;
            invalidate();
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.foregroundImageReceiver.onDetachedFromWindow();
            ImageReceiver.BitmapHolder bitmapHolder = this.drawableHolder;
            if (bitmapHolder != null) {
                bitmapHolder.release();
                this.drawableHolder = null;
            }
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.foregroundImageReceiver.onAttachedToWindow();
        }

        public void setRoundRadius(int i) {
            super.setRoundRadius(i);
            this.foregroundImageReceiver.setRoundRadius(i);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.foregroundAlpha < 1.0f) {
                this.imageReceiver.setImageCoords(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
                this.imageReceiver.draw(canvas);
            }
            if (this.foregroundAlpha <= 0.0f) {
                return;
            }
            if (this.foregroundImageReceiver.getDrawable() != null) {
                this.foregroundImageReceiver.setImageCoords(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
                this.foregroundImageReceiver.setAlpha(this.foregroundAlpha);
                this.foregroundImageReceiver.draw(canvas);
                return;
            }
            this.rect.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
            this.placeholderPaint.setAlpha((int) (this.foregroundAlpha * 255.0f));
            float f = (float) this.foregroundImageReceiver.getRoundRadius()[0];
            canvas.drawRoundRect(this.rect, f, f, this.placeholderPaint);
        }

        public void invalidate() {
            super.invalidate();
            ProfileGalleryView profileGalleryView = this.avatarsViewPager;
            if (profileGalleryView != null) {
                profileGalleryView.invalidate();
            }
        }
    }

    private class TopView extends View {
        private int currentColor;
        private Paint paint = new Paint();

        public TopView(Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            setMeasuredDimension(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i) + AndroidUtilities.dp(3.0f));
        }

        public void setBackgroundColor(int i) {
            if (i != this.currentColor) {
                this.currentColor = i;
                this.paint.setColor(i);
                invalidate();
            }
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            float access$800 = ProfileActivity.this.extraHeight + ((float) (ActionBar.getCurrentActionBarHeight() + (ProfileActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0))) + ((float) ProfileActivity.this.searchTransitionOffset);
            int access$1000 = (int) ((1.0f - ProfileActivity.this.mediaHeaderAnimationProgress) * access$800);
            if (access$1000 != 0) {
                if (ProfileActivity.this.previousTransitionFragment != null) {
                    Rect rect = AndroidUtilities.rectTmp2;
                    rect.set(0, 0, getMeasuredWidth(), access$1000);
                    ProfileActivity.this.previousTransitionFragment.contentView.drawBlurRect(canvas, getY(), rect, ProfileActivity.this.previousTransitionFragment.getActionBar().blurScrimPaint, true);
                }
                this.paint.setColor(this.currentColor);
                canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) access$1000, this.paint);
            }
            if (((float) access$1000) != access$800) {
                this.paint.setColor(ProfileActivity.this.getThemedColor("windowBackgroundWhite"));
                Rect rect2 = AndroidUtilities.rectTmp2;
                rect2.set(0, access$1000, getMeasuredWidth(), (int) access$800);
                ProfileActivity.this.contentView.drawBlurRect(canvas, getY(), rect2, this.paint, true);
            }
            if (ProfileActivity.this.parentLayout != null) {
                ProfileActivity.this.parentLayout.drawHeaderShadow(canvas, (int) (ProfileActivity.this.headerShadowAlpha * 255.0f), (int) access$800);
            }
        }
    }

    private class OverlaysView extends View implements ProfileGalleryView.Callback {
        private float alpha;
        private float[] alphas;
        /* access modifiers changed from: private */
        public final ValueAnimator animator;
        private final float[] animatorValues;
        private final Paint backgroundPaint;
        private final Paint barPaint;
        private final GradientDrawable bottomOverlayGradient;
        private final Rect bottomOverlayRect;
        private float currentAnimationValue;
        private int currentLoadingAnimationDirection;
        private float currentLoadingAnimationProgress;
        private float currentProgress;
        /* access modifiers changed from: private */
        public boolean isOverlaysVisible;
        private long lastTime;
        private final float[] pressedOverlayAlpha;
        private final GradientDrawable[] pressedOverlayGradient;
        private final boolean[] pressedOverlayVisible;
        private int previousSelectedPotision;
        private float previousSelectedProgress;
        private final RectF rect;
        private final Paint selectedBarPaint;
        private int selectedPosition;
        private final int statusBarHeight;
        private final GradientDrawable topOverlayGradient;
        private final Rect topOverlayRect;

        public OverlaysView(Context context) {
            super(context);
            this.statusBarHeight = (!ProfileActivity.this.actionBar.getOccupyStatusBar() || ProfileActivity.this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight;
            this.topOverlayRect = new Rect();
            this.bottomOverlayRect = new Rect();
            this.rect = new RectF();
            this.animatorValues = new float[]{0.0f, 1.0f};
            this.pressedOverlayGradient = new GradientDrawable[2];
            this.pressedOverlayVisible = new boolean[2];
            this.pressedOverlayAlpha = new float[2];
            this.alpha = 0.0f;
            this.alphas = null;
            this.previousSelectedPotision = -1;
            this.currentLoadingAnimationDirection = 1;
            setVisibility(8);
            Paint paint = new Paint(1);
            this.barPaint = paint;
            paint.setColor(NUM);
            Paint paint2 = new Paint(1);
            this.selectedBarPaint = paint2;
            paint2.setColor(-1);
            GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{NUM, 0});
            this.topOverlayGradient = gradientDrawable;
            gradientDrawable.setShape(0);
            GradientDrawable gradientDrawable2 = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{NUM, 0});
            this.bottomOverlayGradient = gradientDrawable2;
            gradientDrawable2.setShape(0);
            int i = 0;
            while (i < 2) {
                this.pressedOverlayGradient[i] = new GradientDrawable(i == 0 ? GradientDrawable.Orientation.LEFT_RIGHT : GradientDrawable.Orientation.RIGHT_LEFT, new int[]{NUM, 0});
                this.pressedOverlayGradient[i].setShape(0);
                i++;
            }
            Paint paint3 = new Paint(1);
            this.backgroundPaint = paint3;
            paint3.setColor(-16777216);
            paint3.setAlpha(66);
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.animator = ofFloat;
            ofFloat.setDuration(250);
            ofFloat.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
            ofFloat.addUpdateListener(new ProfileActivity$OverlaysView$$ExternalSyntheticLambda0(this));
            ofFloat.addListener(new AnimatorListenerAdapter(ProfileActivity.this) {
                public void onAnimationEnd(Animator animator) {
                    if (!OverlaysView.this.isOverlaysVisible) {
                        OverlaysView.this.setVisibility(8);
                    }
                }

                public void onAnimationStart(Animator animator) {
                    OverlaysView.this.setVisibility(0);
                }
            });
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(ValueAnimator valueAnimator) {
            float[] fArr = this.animatorValues;
            float animatedFraction = valueAnimator.getAnimatedFraction();
            this.currentAnimationValue = animatedFraction;
            setAlphaValue(AndroidUtilities.lerp(fArr, animatedFraction), true);
        }

        public void saveCurrentPageProgress() {
            this.previousSelectedProgress = this.currentProgress;
            this.previousSelectedPotision = this.selectedPosition;
            this.currentLoadingAnimationProgress = 0.0f;
            this.currentLoadingAnimationDirection = 1;
        }

        public void setAlphaValue(float f, boolean z) {
            if (Build.VERSION.SDK_INT > 18) {
                int i = (int) (255.0f * f);
                this.topOverlayGradient.setAlpha(i);
                this.bottomOverlayGradient.setAlpha(i);
                this.backgroundPaint.setAlpha((int) (66.0f * f));
                this.barPaint.setAlpha((int) (85.0f * f));
                this.selectedBarPaint.setAlpha(i);
                this.alpha = f;
            } else {
                setAlpha(f);
            }
            if (!z) {
                this.currentAnimationValue = f;
            }
            invalidate();
        }

        public boolean isOverlaysVisible() {
            return this.isOverlaysVisible;
        }

        public void setOverlaysVisible() {
            this.isOverlaysVisible = true;
            setVisibility(0);
        }

        public void setOverlaysVisible(boolean z, float f) {
            if (z != this.isOverlaysVisible) {
                this.isOverlaysVisible = z;
                this.animator.cancel();
                float lerp = AndroidUtilities.lerp(this.animatorValues, this.currentAnimationValue);
                float f2 = 1.0f;
                if (z) {
                    this.animator.setDuration((long) (((1.0f - lerp) * 250.0f) / f));
                } else {
                    this.animator.setDuration((long) ((250.0f * lerp) / f));
                }
                float[] fArr = this.animatorValues;
                fArr[0] = lerp;
                if (!z) {
                    f2 = 0.0f;
                }
                fArr[1] = f2;
                this.animator.start();
            }
        }

        /* access modifiers changed from: protected */
        public void onSizeChanged(int i, int i2, int i3, int i4) {
            int currentActionBarHeight = this.statusBarHeight + ActionBar.getCurrentActionBarHeight();
            this.topOverlayRect.set(0, 0, i, (int) (((float) currentActionBarHeight) * 0.5f));
            this.bottomOverlayRect.set(0, (int) (((float) i2) - (((float) AndroidUtilities.dp(72.0f)) * 0.5f)), i, i2);
            this.topOverlayGradient.setBounds(0, this.topOverlayRect.bottom, i, currentActionBarHeight + AndroidUtilities.dp(16.0f));
            this.bottomOverlayGradient.setBounds(0, (i2 - AndroidUtilities.dp(72.0f)) - AndroidUtilities.dp(24.0f), i, this.bottomOverlayRect.top);
            int i5 = i / 5;
            this.pressedOverlayGradient[0].setBounds(0, 0, i5, i2);
            this.pressedOverlayGradient[1].setBounds(i - i5, 0, i, i2);
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Removed duplicated region for block: B:67:0x021c  */
        /* JADX WARNING: Removed duplicated region for block: B:70:0x0241  */
        /* JADX WARNING: Removed duplicated region for block: B:73:0x0259  */
        /* JADX WARNING: Removed duplicated region for block: B:74:0x025c  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onDraw(android.graphics.Canvas r22) {
            /*
                r21 = this;
                r0 = r21
                r1 = r22
                r3 = 0
            L_0x0005:
                r4 = 1132396544(0x437var_, float:255.0)
                r5 = 2
                r6 = 0
                if (r3 >= r5) goto L_0x0029
                float[] r5 = r0.pressedOverlayAlpha
                r7 = r5[r3]
                int r6 = (r7 > r6 ? 1 : (r7 == r6 ? 0 : -1))
                if (r6 <= 0) goto L_0x0026
                android.graphics.drawable.GradientDrawable[] r6 = r0.pressedOverlayGradient
                r6 = r6[r3]
                r5 = r5[r3]
                float r5 = r5 * r4
                int r4 = (int) r5
                r6.setAlpha(r4)
                android.graphics.drawable.GradientDrawable[] r4 = r0.pressedOverlayGradient
                r4 = r4[r3]
                r4.draw(r1)
            L_0x0026:
                int r3 = r3 + 1
                goto L_0x0005
            L_0x0029:
                android.graphics.drawable.GradientDrawable r3 = r0.topOverlayGradient
                r3.draw(r1)
                android.graphics.drawable.GradientDrawable r3 = r0.bottomOverlayGradient
                r3.draw(r1)
                android.graphics.Rect r3 = r0.topOverlayRect
                android.graphics.Paint r7 = r0.backgroundPaint
                r1.drawRect(r3, r7)
                android.graphics.Rect r3 = r0.bottomOverlayRect
                android.graphics.Paint r7 = r0.backgroundPaint
                r1.drawRect(r3, r7)
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.ui.Components.ProfileGalleryView r3 = r3.avatarsViewPager
                int r3 = r3.getRealCount()
                org.telegram.ui.ProfileActivity r7 = org.telegram.ui.ProfileActivity.this
                org.telegram.ui.Components.ProfileGalleryView r7 = r7.avatarsViewPager
                int r7 = r7.getRealPosition()
                r0.selectedPosition = r7
                float[] r7 = r0.alphas
                if (r7 == 0) goto L_0x005e
                int r7 = r7.length
                if (r7 == r3) goto L_0x0065
            L_0x005e:
                float[] r7 = new float[r3]
                r0.alphas = r7
                java.util.Arrays.fill(r7, r6)
            L_0x0065:
                long r7 = android.os.SystemClock.elapsedRealtime()
                long r9 = r0.lastTime
                long r9 = r7 - r9
                r11 = 0
                int r13 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
                if (r13 < 0) goto L_0x0079
                r11 = 20
                int r13 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
                if (r13 <= 0) goto L_0x007b
            L_0x0079:
                r9 = 17
            L_0x007b:
                r0.lastTime = r7
                r8 = 1
                r11 = 1065353216(0x3var_, float:1.0)
                if (r3 <= r8) goto L_0x02d0
                r12 = 20
                if (r3 > r12) goto L_0x02d0
                org.telegram.ui.ProfileActivity r12 = org.telegram.ui.ProfileActivity.this
                int r12 = r12.overlayCountVisible
                r13 = 3
                if (r12 != 0) goto L_0x0097
                r0.alpha = r6
                org.telegram.ui.ProfileActivity r12 = org.telegram.ui.ProfileActivity.this
                int unused = r12.overlayCountVisible = r13
                goto L_0x00a6
            L_0x0097:
                org.telegram.ui.ProfileActivity r12 = org.telegram.ui.ProfileActivity.this
                int r12 = r12.overlayCountVisible
                if (r12 != r8) goto L_0x00a6
                r0.alpha = r6
                org.telegram.ui.ProfileActivity r12 = org.telegram.ui.ProfileActivity.this
                int unused = r12.overlayCountVisible = r5
            L_0x00a6:
                org.telegram.ui.ProfileActivity r12 = org.telegram.ui.ProfileActivity.this
                int r12 = r12.overlayCountVisible
                r14 = 1118437376(0x42aa0000, float:85.0)
                if (r12 != r5) goto L_0x00c4
                android.graphics.Paint r12 = r0.barPaint
                float r15 = r0.alpha
                float r15 = r15 * r14
                int r15 = (int) r15
                r12.setAlpha(r15)
                android.graphics.Paint r12 = r0.selectedBarPaint
                float r15 = r0.alpha
                float r15 = r15 * r4
                int r4 = (int) r15
                r12.setAlpha(r4)
            L_0x00c4:
                int r4 = r21.getMeasuredWidth()
                r12 = 1092616192(0x41200000, float:10.0)
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
                int r4 = r4 - r12
                int r12 = r3 + -1
                int r12 = r12 * 2
                float r12 = (float) r12
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
                int r4 = r4 - r12
                int r4 = r4 / r3
                r12 = 1082130432(0x40800000, float:4.0)
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
                int r15 = android.os.Build.VERSION.SDK_INT
                r2 = 21
                if (r15 < r2) goto L_0x00f1
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                boolean r2 = r2.inBubbleMode
                if (r2 != 0) goto L_0x00f1
                int r2 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                goto L_0x00f2
            L_0x00f1:
                r2 = 0
            L_0x00f2:
                int r12 = r12 + r2
                r2 = 0
                r15 = 0
            L_0x00f5:
                r16 = 1140457472(0x43fa0000, float:500.0)
                if (r2 >= r3) goto L_0x026d
                int r17 = r2 * 2
                int r8 = r17 + 5
                float r8 = (float) r8
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
                int r17 = r4 * r2
                int r8 = r8 + r17
                r17 = 85
                int r7 = r0.previousSelectedPotision
                r18 = 80
                r19 = 1073741824(0x40000000, float:2.0)
                if (r2 != r7) goto L_0x0168
                float r7 = r0.previousSelectedProgress
                float r7 = r7 - r11
                float r7 = java.lang.Math.abs(r7)
                r20 = 953267991(0x38d1b717, float:1.0E-4)
                int r7 = (r7 > r20 ? 1 : (r7 == r20 ? 0 : -1))
                if (r7 <= 0) goto L_0x0168
                float r7 = r0.previousSelectedProgress
                r22.save()
                float r15 = (float) r8
                float r5 = (float) r4
                float r5 = r5 * r7
                float r5 = r5 + r15
                float r13 = (float) r12
                int r6 = r8 + r4
                float r6 = (float) r6
                int r16 = org.telegram.messenger.AndroidUtilities.dp(r19)
                int r11 = r12 + r16
                float r11 = (float) r11
                r1.clipRect(r5, r13, r6, r11)
                android.graphics.RectF r5 = r0.rect
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r19)
                int r11 = r11 + r12
                float r11 = (float) r11
                r5.set(r15, r13, r6, r11)
                android.graphics.Paint r5 = r0.barPaint
                float r6 = r0.alpha
                float r6 = r6 * r14
                int r6 = (int) r6
                r5.setAlpha(r6)
                android.graphics.RectF r5 = r0.rect
                r6 = 1065353216(0x3var_, float:1.0)
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r6)
                float r11 = (float) r11
                int r13 = org.telegram.messenger.AndroidUtilities.dp(r6)
                float r6 = (float) r13
                android.graphics.Paint r13 = r0.barPaint
                r1.drawRoundRect(r5, r11, r6, r13)
                r22.restore()
                r5 = 80
                r6 = 1118437376(0x42aa0000, float:85.0)
            L_0x0165:
                r15 = 1
                goto L_0x0207
            L_0x0168:
                int r5 = r0.selectedPosition
                if (r2 != r5) goto L_0x0201
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                org.telegram.ui.Components.ProfileGalleryView r5 = r5.avatarsViewPager
                boolean r5 = r5.isCurrentItemVideo()
                if (r5 == 0) goto L_0x01fa
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                org.telegram.ui.Components.ProfileGalleryView r5 = r5.avatarsViewPager
                float r7 = r5.getCurrentItemProgress()
                r0.currentProgress = r7
                r5 = 0
                int r6 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
                if (r6 > 0) goto L_0x0195
                org.telegram.ui.ProfileActivity r6 = org.telegram.ui.ProfileActivity.this
                org.telegram.ui.Components.ProfileGalleryView r6 = r6.avatarsViewPager
                boolean r6 = r6.isLoadingCurrentVideo()
                if (r6 != 0) goto L_0x019b
            L_0x0195:
                float r6 = r0.currentLoadingAnimationProgress
                int r6 = (r6 > r5 ? 1 : (r6 == r5 ? 0 : -1))
                if (r6 <= 0) goto L_0x01c0
            L_0x019b:
                float r5 = r0.currentLoadingAnimationProgress
                int r6 = r0.currentLoadingAnimationDirection
                long r14 = (long) r6
                long r14 = r14 * r9
                float r13 = (float) r14
                float r13 = r13 / r16
                float r5 = r5 + r13
                r0.currentLoadingAnimationProgress = r5
                r13 = 1065353216(0x3var_, float:1.0)
                int r14 = (r5 > r13 ? 1 : (r5 == r13 ? 0 : -1))
                if (r14 <= 0) goto L_0x01b5
                r0.currentLoadingAnimationProgress = r13
                int r6 = r6 * -1
                r0.currentLoadingAnimationDirection = r6
                goto L_0x01c0
            L_0x01b5:
                r13 = 0
                int r5 = (r5 > r13 ? 1 : (r5 == r13 ? 0 : -1))
                if (r5 > 0) goto L_0x01c0
                r0.currentLoadingAnimationProgress = r13
                int r6 = r6 * -1
                r0.currentLoadingAnimationDirection = r6
            L_0x01c0:
                android.graphics.RectF r5 = r0.rect
                float r6 = (float) r8
                float r13 = (float) r12
                int r14 = r8 + r4
                float r14 = (float) r14
                int r15 = org.telegram.messenger.AndroidUtilities.dp(r19)
                int r15 = r15 + r12
                float r15 = (float) r15
                r5.set(r6, r13, r14, r15)
                android.graphics.Paint r5 = r0.barPaint
                r6 = 1111490560(0x42400000, float:48.0)
                float r13 = r0.currentLoadingAnimationProgress
                float r13 = r13 * r6
                r6 = 1118437376(0x42aa0000, float:85.0)
                float r13 = r13 + r6
                float r11 = r0.alpha
                float r13 = r13 * r11
                int r11 = (int) r13
                r5.setAlpha(r11)
                android.graphics.RectF r5 = r0.rect
                r11 = 1065353216(0x3var_, float:1.0)
                int r13 = org.telegram.messenger.AndroidUtilities.dp(r11)
                float r13 = (float) r13
                int r14 = org.telegram.messenger.AndroidUtilities.dp(r11)
                float r14 = (float) r14
                android.graphics.Paint r15 = r0.barPaint
                r1.drawRoundRect(r5, r13, r14, r15)
                r5 = 80
                goto L_0x0165
            L_0x01fa:
                r6 = 1118437376(0x42aa0000, float:85.0)
                r11 = 1065353216(0x3var_, float:1.0)
                r0.currentProgress = r11
                goto L_0x0203
            L_0x0201:
                r6 = 1118437376(0x42aa0000, float:85.0)
            L_0x0203:
                r5 = 85
                r7 = 1065353216(0x3var_, float:1.0)
            L_0x0207:
                android.graphics.RectF r11 = r0.rect
                float r8 = (float) r8
                float r13 = (float) r12
                float r14 = (float) r4
                float r14 = r14 * r7
                float r14 = r14 + r8
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r19)
                int r7 = r7 + r12
                float r7 = (float) r7
                r11.set(r8, r13, r14, r7)
                int r7 = r0.selectedPosition
                if (r2 == r7) goto L_0x0241
                org.telegram.ui.ProfileActivity r7 = org.telegram.ui.ProfileActivity.this
                int r7 = r7.overlayCountVisible
                r8 = 3
                if (r7 != r8) goto L_0x0247
                android.graphics.Paint r7 = r0.barPaint
                r8 = 255(0xff, float:3.57E-43)
                org.telegram.ui.Components.CubicBezierInterpolator r11 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
                float[] r13 = r0.alphas
                r13 = r13[r2]
                float r11 = r11.getInterpolation(r13)
                int r5 = org.telegram.messenger.AndroidUtilities.lerp((int) r5, (int) r8, (float) r11)
                float r5 = (float) r5
                float r8 = r0.alpha
                float r5 = r5 * r8
                int r5 = (int) r5
                r7.setAlpha(r5)
                goto L_0x0247
            L_0x0241:
                float[] r5 = r0.alphas
                r7 = 1061158912(0x3var_, float:0.75)
                r5[r2] = r7
            L_0x0247:
                android.graphics.RectF r5 = r0.rect
                r7 = 1065353216(0x3var_, float:1.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r8 = (float) r8
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r7 = (float) r11
                int r11 = r0.selectedPosition
                if (r2 != r11) goto L_0x025c
                android.graphics.Paint r11 = r0.selectedBarPaint
                goto L_0x025e
            L_0x025c:
                android.graphics.Paint r11 = r0.barPaint
            L_0x025e:
                r1.drawRoundRect(r5, r8, r7, r11)
                int r2 = r2 + 1
                r5 = 2
                r6 = 0
                r8 = 1
                r11 = 1065353216(0x3var_, float:1.0)
                r13 = 3
                r14 = 1118437376(0x42aa0000, float:85.0)
                goto L_0x00f5
            L_0x026d:
                org.telegram.ui.ProfileActivity r1 = org.telegram.ui.ProfileActivity.this
                int r1 = r1.overlayCountVisible
                r2 = 2
                if (r1 != r2) goto L_0x0294
                float r1 = r0.alpha
                r2 = 1065353216(0x3var_, float:1.0)
                int r3 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r3 >= 0) goto L_0x028d
                float r3 = (float) r9
                r4 = 1127481344(0x43340000, float:180.0)
                float r3 = r3 / r4
                float r1 = r1 + r3
                r0.alpha = r1
                int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r1 <= 0) goto L_0x028b
                r0.alpha = r2
            L_0x028b:
                r15 = 1
                goto L_0x02d1
            L_0x028d:
                org.telegram.ui.ProfileActivity r1 = org.telegram.ui.ProfileActivity.this
                r2 = 3
                int unused = r1.overlayCountVisible = r2
                goto L_0x02d1
            L_0x0294:
                r2 = 3
                org.telegram.ui.ProfileActivity r1 = org.telegram.ui.ProfileActivity.this
                int r1 = r1.overlayCountVisible
                if (r1 != r2) goto L_0x02d1
                r1 = 0
            L_0x029e:
                float[] r2 = r0.alphas
                int r3 = r2.length
                if (r1 >= r3) goto L_0x02d1
                int r3 = r0.selectedPosition
                r4 = -1
                if (r1 == r3) goto L_0x02c7
                r3 = r2[r1]
                r5 = 0
                int r3 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
                if (r3 <= 0) goto L_0x02c7
                r3 = r2[r1]
                float r6 = (float) r9
                float r6 = r6 / r16
                float r3 = r3 - r6
                r2[r1] = r3
                r3 = r2[r1]
                int r3 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
                if (r3 > 0) goto L_0x02c5
                r2[r1] = r5
                int r2 = r0.previousSelectedPotision
                if (r1 != r2) goto L_0x02c5
                r0.previousSelectedPotision = r4
            L_0x02c5:
                r15 = 1
                goto L_0x02cd
            L_0x02c7:
                int r2 = r0.previousSelectedPotision
                if (r1 != r2) goto L_0x02cd
                r0.previousSelectedPotision = r4
            L_0x02cd:
                int r1 = r1 + 1
                goto L_0x029e
            L_0x02d0:
                r15 = 0
            L_0x02d1:
                r1 = 2
                r2 = 0
            L_0x02d3:
                if (r2 >= r1) goto L_0x031f
                boolean[] r3 = r0.pressedOverlayVisible
                boolean r3 = r3[r2]
                if (r3 == 0) goto L_0x02fc
                float[] r3 = r0.pressedOverlayAlpha
                r4 = r3[r2]
                r5 = 1065353216(0x3var_, float:1.0)
                int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
                if (r4 >= 0) goto L_0x02fa
                r4 = r3[r2]
                float r6 = (float) r9
                r7 = 1127481344(0x43340000, float:180.0)
                float r6 = r6 / r7
                float r4 = r4 + r6
                r3[r2] = r4
                r4 = r3[r2]
                int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
                if (r4 <= 0) goto L_0x02f6
                r3[r2] = r5
            L_0x02f6:
                r6 = 0
                r8 = 1127481344(0x43340000, float:180.0)
                goto L_0x0318
            L_0x02fa:
                r6 = 0
                goto L_0x031a
            L_0x02fc:
                r5 = 1065353216(0x3var_, float:1.0)
                float[] r3 = r0.pressedOverlayAlpha
                r4 = r3[r2]
                r6 = 0
                int r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
                if (r4 <= 0) goto L_0x031a
                r4 = r3[r2]
                float r7 = (float) r9
                r8 = 1127481344(0x43340000, float:180.0)
                float r7 = r7 / r8
                float r4 = r4 - r7
                r3[r2] = r4
                r4 = r3[r2]
                int r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
                if (r4 >= 0) goto L_0x0318
                r3[r2] = r6
            L_0x0318:
                r15 = 1
                goto L_0x031c
            L_0x031a:
                r8 = 1127481344(0x43340000, float:180.0)
            L_0x031c:
                int r2 = r2 + 1
                goto L_0x02d3
            L_0x031f:
                if (r15 == 0) goto L_0x0324
                r21.postInvalidateOnAnimation()
            L_0x0324:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.OverlaysView.onDraw(android.graphics.Canvas):void");
        }

        public void onDown(boolean z) {
            this.pressedOverlayVisible[!z] = true;
            postInvalidateOnAnimation();
        }

        public void onRelease() {
            Arrays.fill(this.pressedOverlayVisible, false);
            postInvalidateOnAnimation();
        }

        public void onPhotosLoaded() {
            ProfileActivity.this.updateProfileData(false);
        }

        public void onVideoSet() {
            invalidate();
        }
    }

    private class NestedFrameLayout extends SizeNotifierFrameLayout implements NestedScrollingParent3 {
        private NestedScrollingParentHelper nestedScrollingParentHelper = new NestedScrollingParentHelper(this);

        public void onNestedScroll(View view, int i, int i2, int i3, int i4, int i5) {
        }

        public void onStopNestedScroll(View view) {
        }

        public NestedFrameLayout(Context context) {
            super(context);
        }

        public void onNestedScroll(View view, int i, int i2, int i3, int i4, int i5, int[] iArr) {
            if (view == ProfileActivity.this.listView && ProfileActivity.this.sharedMediaLayoutAttached) {
                RecyclerListView currentListView = ProfileActivity.this.sharedMediaLayout.getCurrentListView();
                if (ProfileActivity.this.sharedMediaLayout.getTop() == 0) {
                    iArr[1] = i4;
                    currentListView.scrollBy(0, i4);
                }
            }
        }

        public boolean onNestedPreFling(View view, float f, float f2) {
            return super.onNestedPreFling(view, f, f2);
        }

        public void onNestedPreScroll(View view, int i, int i2, int[] iArr, int i3) {
            int i4;
            if (view == ProfileActivity.this.listView) {
                int i5 = -1;
                if (ProfileActivity.this.sharedMediaRow != -1 && ProfileActivity.this.sharedMediaLayoutAttached) {
                    boolean isSearchFieldVisible = ProfileActivity.this.actionBar.isSearchFieldVisible();
                    int top = ProfileActivity.this.sharedMediaLayout.getTop();
                    boolean z = false;
                    if (i2 < 0) {
                        if (top <= 0) {
                            RecyclerListView currentListView = ProfileActivity.this.sharedMediaLayout.getCurrentListView();
                            int findFirstVisibleItemPosition = ((LinearLayoutManager) currentListView.getLayoutManager()).findFirstVisibleItemPosition();
                            if (findFirstVisibleItemPosition != -1) {
                                RecyclerView.ViewHolder findViewHolderForAdapterPosition = currentListView.findViewHolderForAdapterPosition(findFirstVisibleItemPosition);
                                if (findViewHolderForAdapterPosition != null) {
                                    i5 = findViewHolderForAdapterPosition.itemView.getTop();
                                }
                                int paddingTop = currentListView.getPaddingTop();
                                if (!(i5 == paddingTop && findFirstVisibleItemPosition == 0)) {
                                    if (findFirstVisibleItemPosition != 0) {
                                        i4 = i2;
                                    } else {
                                        i4 = Math.max(i2, i5 - paddingTop);
                                    }
                                    iArr[1] = i4;
                                    currentListView.scrollBy(0, i2);
                                    z = true;
                                }
                            }
                        }
                        if (!isSearchFieldVisible) {
                            return;
                        }
                        if (z || top >= 0) {
                            iArr[1] = i2;
                        } else {
                            iArr[1] = i2 - Math.max(top, i2);
                        }
                    } else if (isSearchFieldVisible) {
                        RecyclerListView currentListView2 = ProfileActivity.this.sharedMediaLayout.getCurrentListView();
                        iArr[1] = i2;
                        if (top > 0) {
                            iArr[1] = iArr[1] - i2;
                        }
                        if (iArr[1] > 0) {
                            currentListView2.scrollBy(0, iArr[1]);
                        }
                    }
                }
            }
        }

        public boolean onStartNestedScroll(View view, View view2, int i, int i2) {
            return ProfileActivity.this.sharedMediaRow != -1 && i == 2;
        }

        public void onNestedScrollAccepted(View view, View view2, int i, int i2) {
            this.nestedScrollingParentHelper.onNestedScrollAccepted(view, view2, i);
        }

        public void onStopNestedScroll(View view, int i) {
            this.nestedScrollingParentHelper.onStopNestedScroll(view);
        }

        /* access modifiers changed from: protected */
        public void drawList(Canvas canvas, boolean z) {
            super.drawList(canvas, z);
            canvas.save();
            canvas.translate(0.0f, ProfileActivity.this.listView.getY());
            ProfileActivity.this.sharedMediaLayout.drawListForBlur(canvas);
            canvas.restore();
        }
    }

    private class PagerIndicatorView extends View {
        private final PagerAdapter adapter;
        private final ValueAnimator animator;
        private final float[] animatorValues = {0.0f, 1.0f};
        private final Paint backgroundPaint;
        private final RectF indicatorRect = new RectF();
        /* access modifiers changed from: private */
        public boolean isIndicatorVisible;
        private final TextPaint textPaint;

        public PagerIndicatorView(Context context) {
            super(context);
            PagerAdapter adapter2 = ProfileActivity.this.avatarsViewPager.getAdapter();
            this.adapter = adapter2;
            setVisibility(8);
            TextPaint textPaint2 = new TextPaint(1);
            this.textPaint = textPaint2;
            textPaint2.setColor(-1);
            textPaint2.setTypeface(Typeface.SANS_SERIF);
            textPaint2.setTextAlign(Paint.Align.CENTER);
            textPaint2.setTextSize(AndroidUtilities.dpf2(15.0f));
            Paint paint = new Paint(1);
            this.backgroundPaint = paint;
            paint.setColor(NUM);
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.animator = ofFloat;
            ofFloat.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
            ofFloat.addUpdateListener(new ProfileActivity$PagerIndicatorView$$ExternalSyntheticLambda0(this));
            ofFloat.addListener(new AnimatorListenerAdapter(ProfileActivity.this, ProfileActivity.this.expandPhoto) {
                final /* synthetic */ boolean val$expanded;

                {
                    this.val$expanded = r3;
                }

                public void onAnimationEnd(Animator animator) {
                    if (PagerIndicatorView.this.isIndicatorVisible) {
                        if (ProfileActivity.this.searchItem != null) {
                            ProfileActivity.this.searchItem.setClickable(false);
                        }
                        if (ProfileActivity.this.editItemVisible) {
                            ProfileActivity.this.editItem.setVisibility(8);
                        }
                        if (ProfileActivity.this.callItemVisible) {
                            ProfileActivity.this.callItem.setVisibility(8);
                        }
                        if (ProfileActivity.this.videoCallItemVisible) {
                            ProfileActivity.this.videoCallItem.setVisibility(8);
                            return;
                        }
                        return;
                    }
                    PagerIndicatorView.this.setVisibility(8);
                }

                public void onAnimationStart(Animator animator) {
                    if (ProfileActivity.this.searchItem != null && !this.val$expanded) {
                        ProfileActivity.this.searchItem.setClickable(true);
                    }
                    if (ProfileActivity.this.editItemVisible) {
                        ProfileActivity.this.editItem.setVisibility(0);
                    }
                    if (ProfileActivity.this.callItemVisible) {
                        ProfileActivity.this.callItem.setVisibility(0);
                    }
                    if (ProfileActivity.this.videoCallItemVisible) {
                        ProfileActivity.this.videoCallItem.setVisibility(0);
                    }
                    PagerIndicatorView.this.setVisibility(0);
                }
            });
            ProfileActivity.this.avatarsViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(ProfileActivity.this) {
                private int prevPage;

                public void onPageScrollStateChanged(int i) {
                }

                public void onPageScrolled(int i, float f, int i2) {
                }

                public void onPageSelected(int i) {
                    int realPosition = ProfileActivity.this.avatarsViewPager.getRealPosition(i);
                    PagerIndicatorView.this.invalidateIndicatorRect(this.prevPage != realPosition);
                    this.prevPage = realPosition;
                    PagerIndicatorView.this.updateAvatarItems();
                }
            });
            adapter2.registerDataSetObserver(new DataSetObserver(ProfileActivity.this) {
                public void onChanged() {
                    int realCount = ProfileActivity.this.avatarsViewPager.getRealCount();
                    if (ProfileActivity.this.overlayCountVisible == 0 && realCount > 1 && realCount <= 20 && ProfileActivity.this.overlaysView.isOverlaysVisible()) {
                        int unused = ProfileActivity.this.overlayCountVisible = 1;
                    }
                    PagerIndicatorView.this.invalidateIndicatorRect(false);
                    PagerIndicatorView.this.refreshVisibility(1.0f);
                    PagerIndicatorView.this.updateAvatarItems();
                }
            });
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(ValueAnimator valueAnimator) {
            float lerp = AndroidUtilities.lerp(this.animatorValues, valueAnimator.getAnimatedFraction());
            if (ProfileActivity.this.searchItem != null && !ProfileActivity.this.isPulledDown) {
                float f = 1.0f - lerp;
                ProfileActivity.this.searchItem.setScaleX(f);
                ProfileActivity.this.searchItem.setScaleY(f);
                ProfileActivity.this.searchItem.setAlpha(f);
            }
            if (ProfileActivity.this.editItemVisible) {
                float f2 = 1.0f - lerp;
                ProfileActivity.this.editItem.setScaleX(f2);
                ProfileActivity.this.editItem.setScaleY(f2);
                ProfileActivity.this.editItem.setAlpha(f2);
            }
            if (ProfileActivity.this.callItemVisible) {
                float f3 = 1.0f - lerp;
                ProfileActivity.this.callItem.setScaleX(f3);
                ProfileActivity.this.callItem.setScaleY(f3);
                ProfileActivity.this.callItem.setAlpha(f3);
            }
            if (ProfileActivity.this.videoCallItemVisible) {
                float f4 = 1.0f - lerp;
                ProfileActivity.this.videoCallItem.setScaleX(f4);
                ProfileActivity.this.videoCallItem.setScaleY(f4);
                ProfileActivity.this.videoCallItem.setAlpha(f4);
            }
            setScaleX(lerp);
            setScaleY(lerp);
            setAlpha(lerp);
        }

        /* access modifiers changed from: private */
        public void updateAvatarItemsInternal() {
            if (ProfileActivity.this.otherItem != null && ProfileActivity.this.avatarsViewPager != null && ProfileActivity.this.isPulledDown) {
                if (ProfileActivity.this.avatarsViewPager.getRealPosition() == 0) {
                    ProfileActivity.this.otherItem.hideSubItem(33);
                    ProfileActivity.this.otherItem.showSubItem(36);
                    return;
                }
                ProfileActivity.this.otherItem.showSubItem(33);
                ProfileActivity.this.otherItem.hideSubItem(36);
            }
        }

        /* access modifiers changed from: private */
        public void updateAvatarItems() {
            if (ProfileActivity.this.imageUpdater != null) {
                if (ProfileActivity.this.otherItem.isSubMenuShowing()) {
                    AndroidUtilities.runOnUIThread(new ProfileActivity$PagerIndicatorView$$ExternalSyntheticLambda1(this), 500);
                } else {
                    updateAvatarItemsInternal();
                }
            }
        }

        public boolean isIndicatorFullyVisible() {
            return this.isIndicatorVisible && !this.animator.isRunning();
        }

        public void setIndicatorVisible(boolean z, float f) {
            if (z != this.isIndicatorVisible) {
                this.isIndicatorVisible = z;
                this.animator.cancel();
                float lerp = AndroidUtilities.lerp(this.animatorValues, this.animator.getAnimatedFraction());
                float f2 = 1.0f;
                if (f <= 0.0f) {
                    this.animator.setDuration(0);
                } else if (z) {
                    this.animator.setDuration((long) (((1.0f - lerp) * 250.0f) / f));
                } else {
                    this.animator.setDuration((long) ((250.0f * lerp) / f));
                }
                float[] fArr = this.animatorValues;
                fArr[0] = lerp;
                if (!z) {
                    f2 = 0.0f;
                }
                fArr[1] = f2;
                this.animator.start();
            }
        }

        public void refreshVisibility(float f) {
            setIndicatorVisible(ProfileActivity.this.isPulledDown && ProfileActivity.this.avatarsViewPager.getRealCount() > 20, f);
        }

        /* access modifiers changed from: protected */
        public void onSizeChanged(int i, int i2, int i3, int i4) {
            invalidateIndicatorRect(false);
        }

        /* access modifiers changed from: private */
        public void invalidateIndicatorRect(boolean z) {
            if (z) {
                ProfileActivity.this.overlaysView.saveCurrentPageProgress();
            }
            ProfileActivity.this.overlaysView.invalidate();
            float measureText = this.textPaint.measureText(getCurrentTitle());
            int i = 0;
            this.indicatorRect.right = (float) ((getMeasuredWidth() - AndroidUtilities.dp(54.0f)) - (ProfileActivity.this.qrItem != null ? AndroidUtilities.dp(48.0f) : 0));
            RectF rectF = this.indicatorRect;
            rectF.left = rectF.right - (measureText + AndroidUtilities.dpf2(16.0f));
            RectF rectF2 = this.indicatorRect;
            if (ProfileActivity.this.actionBar.getOccupyStatusBar()) {
                i = AndroidUtilities.statusBarHeight;
            }
            rectF2.top = (float) (i + AndroidUtilities.dp(15.0f));
            RectF rectF3 = this.indicatorRect;
            rectF3.bottom = rectF3.top + ((float) AndroidUtilities.dp(26.0f));
            setPivotX(this.indicatorRect.centerX());
            setPivotY(this.indicatorRect.centerY());
            invalidate();
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            float dpf2 = AndroidUtilities.dpf2(12.0f);
            canvas.drawRoundRect(this.indicatorRect, dpf2, dpf2, this.backgroundPaint);
            canvas.drawText(getCurrentTitle(), this.indicatorRect.centerX(), this.indicatorRect.top + AndroidUtilities.dpf2(18.5f), this.textPaint);
        }

        private String getCurrentTitle() {
            return this.adapter.getPageTitle(ProfileActivity.this.avatarsViewPager.getCurrentItem()).toString();
        }

        /* access modifiers changed from: private */
        public ActionBarMenuItem getSecondaryMenuItem() {
            if (ProfileActivity.this.callItemVisible) {
                return ProfileActivity.this.callItem;
            }
            if (ProfileActivity.this.editItemVisible) {
                return ProfileActivity.this.editItem;
            }
            if (ProfileActivity.this.searchItem != null) {
                return ProfileActivity.this.searchItem;
            }
            return null;
        }
    }

    public ProfileActivity(Bundle bundle) {
        this(bundle, (SharedMediaLayout.SharedMediaPreloader) null);
    }

    public ProfileActivity(Bundle bundle, SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader2) {
        super(bundle);
        this.nameTextView = new SimpleTextView[2];
        this.nameTextViewRightDrawableContentDescription = null;
        this.onlineTextView = new SimpleTextView[2];
        this.scrimView = null;
        this.scrimPaint = new Paint(1) {
            public void setAlpha(int i) {
                super.setAlpha(i);
                ProfileActivity.this.fragmentView.invalidate();
            }
        };
        this.actionBarBackgroundPaint = new Paint(1);
        this.isOnline = new boolean[1];
        this.headerShadowAlpha = 1.0f;
        this.participantsMap = new LongSparseArray<>();
        this.allowProfileAnimation = true;
        this.disableProfileAnimation = false;
        this.positionToOffset = new HashMap<>();
        this.expandAnimatorValues = new float[]{0.0f, 1.0f};
        this.whitePaint = new Paint();
        this.onlineCount = -1;
        this.rect = new Rect();
        this.visibleChatParticipants = new ArrayList<>();
        this.visibleSortedUsers = new ArrayList<>();
        this.usersForceShowingIn = 0;
        this.firstLayout = true;
        this.invalidateScroll = true;
        this.isQrItemVisible = true;
        this.actionBarAnimationColorFrom = 0;
        this.navigationBarAnimationColorFrom = 0;
        this.HEADER_SHADOW = new AnimationProperties.FloatProperty<ProfileActivity>("headerShadow") {
            public void setValue(ProfileActivity profileActivity, float f) {
                ProfileActivity profileActivity2 = ProfileActivity.this;
                profileActivity2.headerShadowAlpha = f;
                profileActivity2.topView.invalidate();
            }

            public Float get(ProfileActivity profileActivity) {
                return Float.valueOf(ProfileActivity.this.headerShadowAlpha);
            }
        };
        this.provider = new PhotoViewer.EmptyPhotoViewerProvider() {
            public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC$FileLocation tLRPC$FileLocation, int i, boolean z) {
                TLRPC$FileLocation tLRPC$FileLocation2;
                TLRPC$Chat chat;
                TLRPC$ChatPhoto tLRPC$ChatPhoto;
                TLRPC$User user;
                TLRPC$UserProfilePhoto tLRPC$UserProfilePhoto;
                if (tLRPC$FileLocation == null) {
                    return null;
                }
                if (ProfileActivity.this.userId == 0 ? ProfileActivity.this.chatId == 0 || (chat = ProfileActivity.this.getMessagesController().getChat(Long.valueOf(ProfileActivity.this.chatId))) == null || (tLRPC$ChatPhoto = chat.photo) == null || (tLRPC$FileLocation2 = tLRPC$ChatPhoto.photo_big) == null : (user = ProfileActivity.this.getMessagesController().getUser(Long.valueOf(ProfileActivity.this.userId))) == null || (tLRPC$UserProfilePhoto = user.photo) == null || (tLRPC$FileLocation2 = tLRPC$UserProfilePhoto.photo_big) == null) {
                    tLRPC$FileLocation2 = null;
                }
                if (tLRPC$FileLocation2 == null || tLRPC$FileLocation2.local_id != tLRPC$FileLocation.local_id || tLRPC$FileLocation2.volume_id != tLRPC$FileLocation.volume_id || tLRPC$FileLocation2.dc_id != tLRPC$FileLocation.dc_id) {
                    return null;
                }
                int[] iArr = new int[2];
                ProfileActivity.this.avatarImage.getLocationInWindow(iArr);
                PhotoViewer.PlaceProviderObject placeProviderObject = new PhotoViewer.PlaceProviderObject();
                boolean z2 = false;
                placeProviderObject.viewX = iArr[0];
                placeProviderObject.viewY = iArr[1] - (Build.VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight);
                placeProviderObject.parentView = ProfileActivity.this.avatarImage;
                placeProviderObject.imageReceiver = ProfileActivity.this.avatarImage.getImageReceiver();
                if (ProfileActivity.this.userId != 0) {
                    placeProviderObject.dialogId = ProfileActivity.this.userId;
                } else if (ProfileActivity.this.chatId != 0) {
                    placeProviderObject.dialogId = -ProfileActivity.this.chatId;
                }
                placeProviderObject.thumb = placeProviderObject.imageReceiver.getBitmapSafe();
                placeProviderObject.size = -1;
                placeProviderObject.radius = ProfileActivity.this.avatarImage.getImageReceiver().getRoundRadius();
                placeProviderObject.scale = ProfileActivity.this.avatarContainer.getScaleX();
                if (ProfileActivity.this.userId == ProfileActivity.this.getUserConfig().clientUserId) {
                    z2 = true;
                }
                placeProviderObject.canEdit = z2;
                return placeProviderObject;
            }

            public void willHidePhotoViewer() {
                ProfileActivity.this.avatarImage.getImageReceiver().setVisible(true, true);
            }

            public void openPhotoForEdit(String str, String str2, boolean z) {
                ProfileActivity.this.imageUpdater.openPhotoForEdit(str, str2, 0, z);
            }
        };
        this.ACTIONBAR_HEADER_PROGRESS = new AnimationProperties.FloatProperty<ActionBar>("animationProgress") {
            public void setValue(ActionBar actionBar, float f) {
                float unused = ProfileActivity.this.mediaHeaderAnimationProgress = f;
                ProfileActivity.this.topView.invalidate();
                int themedColor = ProfileActivity.this.getThemedColor("profile_title");
                int themedColor2 = ProfileActivity.this.getThemedColor("player_actionBarTitle");
                int offsetColor = AndroidUtilities.getOffsetColor(themedColor, themedColor2, f, 1.0f);
                ProfileActivity.this.nameTextView[1].setTextColor(offsetColor);
                if (ProfileActivity.this.lockIconDrawable != null) {
                    ProfileActivity.this.lockIconDrawable.setColorFilter(offsetColor, PorterDuff.Mode.MULTIPLY);
                }
                if (ProfileActivity.this.scamDrawable != null) {
                    ProfileActivity.this.scamDrawable.setColor(AndroidUtilities.getOffsetColor(ProfileActivity.this.getThemedColor("avatar_subtitleInProfileBlue"), themedColor2, f, 1.0f));
                }
                ProfileActivity.this.actionBar.setItemsColor(AndroidUtilities.getOffsetColor(ProfileActivity.this.getThemedColor("actionBarDefaultIcon"), ProfileActivity.this.getThemedColor("windowBackgroundWhiteGrayText2"), f, 1.0f), false);
                ProfileActivity.this.actionBar.setItemsBackgroundColor(AndroidUtilities.getOffsetColor(ProfileActivity.this.getThemedColor("avatar_actionBarSelectorBlue"), ProfileActivity.this.getThemedColor("actionBarActionModeDefaultSelector"), f, 1.0f), false);
                ProfileActivity.this.topView.invalidate();
                ProfileActivity.this.otherItem.setIconColor(ProfileActivity.this.getThemedColor("actionBarDefaultIcon"));
                ProfileActivity.this.callItem.setIconColor(ProfileActivity.this.getThemedColor("actionBarDefaultIcon"));
                ProfileActivity.this.videoCallItem.setIconColor(ProfileActivity.this.getThemedColor("actionBarDefaultIcon"));
                ProfileActivity.this.editItem.setIconColor(ProfileActivity.this.getThemedColor("actionBarDefaultIcon"));
                if (ProfileActivity.this.verifiedDrawable != null) {
                    ProfileActivity.this.verifiedDrawable.setColorFilter(AndroidUtilities.getOffsetColor(ProfileActivity.this.getThemedColor("profile_verifiedBackground"), ProfileActivity.this.getThemedColor("player_actionBarTitle"), f, 1.0f), PorterDuff.Mode.MULTIPLY);
                }
                if (ProfileActivity.this.verifiedCheckDrawable != null) {
                    ProfileActivity.this.verifiedCheckDrawable.setColorFilter(AndroidUtilities.getOffsetColor(ProfileActivity.this.getThemedColor("profile_verifiedCheck"), ProfileActivity.this.getThemedColor("windowBackgroundWhite"), f, 1.0f), PorterDuff.Mode.MULTIPLY);
                }
                if (ProfileActivity.this.premiumStarDrawable != null) {
                    ProfileActivity.this.premiumStarDrawable.setColorFilter(AndroidUtilities.getOffsetColor(ProfileActivity.this.getThemedColor("profile_verifiedBackground"), ProfileActivity.this.getThemedColor("player_actionBarTitle"), f, 1.0f), PorterDuff.Mode.MULTIPLY);
                }
                if (ProfileActivity.this.avatarsViewPagerIndicatorView.getSecondaryMenuItem() == null) {
                    return;
                }
                if (ProfileActivity.this.videoCallItemVisible || ProfileActivity.this.editItemVisible || ProfileActivity.this.callItemVisible) {
                    ProfileActivity profileActivity = ProfileActivity.this;
                    profileActivity.needLayoutText(Math.min(1.0f, profileActivity.extraHeight / ((float) AndroidUtilities.dp(88.0f))));
                }
            }

            public Float get(ActionBar actionBar) {
                return Float.valueOf(ProfileActivity.this.mediaHeaderAnimationProgress);
            }
        };
        this.scrimAnimatorSet = null;
        this.savedScrollPosition = -1;
        this.sharedMediaPreloader = sharedMediaPreloader2;
    }

    public boolean onFragmentCreate() {
        this.userId = this.arguments.getLong("user_id", 0);
        this.chatId = this.arguments.getLong("chat_id", 0);
        this.banFromGroup = this.arguments.getLong("ban_chat_id", 0);
        this.reportSpam = this.arguments.getBoolean("reportSpam", false);
        if (!this.expandPhoto) {
            boolean z = this.arguments.getBoolean("expandPhoto", false);
            this.expandPhoto = z;
            if (z) {
                this.needSendMessage = true;
            }
        }
        if (this.userId != 0) {
            long j = this.arguments.getLong("dialog_id", 0);
            this.dialogId = j;
            if (j != 0) {
                this.currentEncryptedChat = getMessagesController().getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(this.dialogId)));
            }
            TLRPC$User user = getMessagesController().getUser(Long.valueOf(this.userId));
            if (user == null) {
                return false;
            }
            getNotificationCenter().addObserver(this, NotificationCenter.contactsDidLoad);
            getNotificationCenter().addObserver(this, NotificationCenter.newSuggestionsAvailable);
            getNotificationCenter().addObserver(this, NotificationCenter.encryptedChatCreated);
            getNotificationCenter().addObserver(this, NotificationCenter.encryptedChatUpdated);
            getNotificationCenter().addObserver(this, NotificationCenter.blockedUsersDidLoad);
            getNotificationCenter().addObserver(this, NotificationCenter.botInfoDidLoad);
            getNotificationCenter().addObserver(this, NotificationCenter.userInfoDidLoad);
            getNotificationCenter().addObserver(this, NotificationCenter.privacyRulesUpdated);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.reloadInterface);
            this.userBlocked = getMessagesController().blockePeers.indexOfKey(this.userId) >= 0;
            if (user.bot) {
                this.isBot = true;
                MediaDataController mediaDataController = getMediaDataController();
                long j2 = user.id;
                mediaDataController.loadBotInfo(j2, j2, true, this.classGuid);
            }
            this.userInfo = getMessagesController().getUserFull(this.userId);
            getMessagesController().loadFullUser(getMessagesController().getUser(Long.valueOf(this.userId)), this.classGuid, true);
            this.participantsMap = null;
            if (UserObject.isUserSelf(user)) {
                ImageUpdater imageUpdater2 = new ImageUpdater(true);
                this.imageUpdater = imageUpdater2;
                imageUpdater2.setOpenWithFrontfaceCamera(true);
                ImageUpdater imageUpdater3 = this.imageUpdater;
                imageUpdater3.parentFragment = this;
                imageUpdater3.setDelegate(this);
                getMediaDataController().checkFeaturedStickers();
                getMessagesController().loadSuggestedFilters();
                getMessagesController().loadUserInfo(getUserConfig().getCurrentUser(), true, this.classGuid);
            }
            this.actionBarAnimationColorFrom = this.arguments.getInt("actionBarColor", 0);
        } else if (this.chatId == 0) {
            return false;
        } else {
            TLRPC$Chat chat = getMessagesController().getChat(Long.valueOf(this.chatId));
            this.currentChat = chat;
            if (chat == null) {
                CountDownLatch countDownLatch = new CountDownLatch(1);
                getMessagesStorage().getStorageQueue().postRunnable(new ProfileActivity$$ExternalSyntheticLambda20(this, countDownLatch));
                try {
                    countDownLatch.await();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                if (this.currentChat == null) {
                    return false;
                }
                getMessagesController().putChat(this.currentChat, true);
            }
            if (this.currentChat.megagroup) {
                getChannelParticipants(true);
            } else {
                this.participantsMap = null;
            }
            getNotificationCenter().addObserver(this, NotificationCenter.chatInfoDidLoad);
            getNotificationCenter().addObserver(this, NotificationCenter.chatOnlineCountDidLoad);
            getNotificationCenter().addObserver(this, NotificationCenter.groupCallUpdated);
            this.sortedUsers = new ArrayList<>();
            updateOnlineCount(true);
            if (this.chatInfo == null) {
                this.chatInfo = getMessagesController().getChatFull(this.chatId);
            }
            if (ChatObject.isChannel(this.currentChat)) {
                getMessagesController().loadFullChat(this.chatId, this.classGuid, true);
            } else if (this.chatInfo == null) {
                this.chatInfo = getMessagesStorage().loadChatInfo(this.chatId, false, (CountDownLatch) null, false, false);
            }
        }
        if (this.sharedMediaPreloader == null) {
            this.sharedMediaPreloader = new SharedMediaLayout.SharedMediaPreloader(this);
        }
        this.sharedMediaPreloader.addDelegate(this);
        getNotificationCenter().addObserver(this, NotificationCenter.updateInterfaces);
        getNotificationCenter().addObserver(this, NotificationCenter.didReceiveNewMessages);
        getNotificationCenter().addObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        updateRowsIds();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
        if (this.arguments.containsKey("preload_messages")) {
            getMessagesController().ensureMessagesLoaded(this.userId, 0, (MessagesController.MessagesLoadedCallback) null);
        }
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onFragmentCreate$0(CountDownLatch countDownLatch) {
        this.currentChat = getMessagesStorage().getChat(this.chatId);
        countDownLatch.countDown();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        SharedMediaLayout sharedMediaLayout2 = this.sharedMediaLayout;
        if (sharedMediaLayout2 != null) {
            sharedMediaLayout2.onDestroy();
        }
        SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader2 = this.sharedMediaPreloader;
        if (sharedMediaPreloader2 != null) {
            sharedMediaPreloader2.onDestroy(this);
        }
        SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader3 = this.sharedMediaPreloader;
        if (sharedMediaPreloader3 != null) {
            sharedMediaPreloader3.removeDelegate(this);
        }
        getNotificationCenter().removeObserver(this, NotificationCenter.updateInterfaces);
        getNotificationCenter().removeObserver(this, NotificationCenter.closeChats);
        getNotificationCenter().removeObserver(this, NotificationCenter.didReceiveNewMessages);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        ProfileGalleryView profileGalleryView = this.avatarsViewPager;
        if (profileGalleryView != null) {
            profileGalleryView.onDestroy();
        }
        if (this.userId != 0) {
            getNotificationCenter().removeObserver(this, NotificationCenter.newSuggestionsAvailable);
            getNotificationCenter().removeObserver(this, NotificationCenter.contactsDidLoad);
            getNotificationCenter().removeObserver(this, NotificationCenter.encryptedChatCreated);
            getNotificationCenter().removeObserver(this, NotificationCenter.encryptedChatUpdated);
            getNotificationCenter().removeObserver(this, NotificationCenter.blockedUsersDidLoad);
            getNotificationCenter().removeObserver(this, NotificationCenter.botInfoDidLoad);
            getNotificationCenter().removeObserver(this, NotificationCenter.userInfoDidLoad);
            getNotificationCenter().removeObserver(this, NotificationCenter.privacyRulesUpdated);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.reloadInterface);
            getMessagesController().cancelLoadFullUser(this.userId);
        } else if (this.chatId != 0) {
            getNotificationCenter().removeObserver(this, NotificationCenter.chatInfoDidLoad);
            getNotificationCenter().removeObserver(this, NotificationCenter.chatOnlineCountDidLoad);
            getNotificationCenter().removeObserver(this, NotificationCenter.groupCallUpdated);
        }
        AvatarImageView avatarImageView = this.avatarImage;
        if (avatarImageView != null) {
            avatarImageView.setImageDrawable((Drawable) null);
        }
        ImageUpdater imageUpdater2 = this.imageUpdater;
        if (imageUpdater2 != null) {
            imageUpdater2.clear();
        }
        PinchToZoomHelper pinchToZoomHelper2 = this.pinchToZoomHelper;
        if (pinchToZoomHelper2 != null) {
            pinchToZoomHelper2.clear();
        }
    }

    /* access modifiers changed from: protected */
    public ActionBar createActionBar(Context context) {
        ChatActivity.ThemeDelegate themeDelegate;
        BaseFragment lastFragment = this.parentLayout.getLastFragment();
        if (!(!(lastFragment instanceof ChatActivity) || (themeDelegate = ((ChatActivity) lastFragment).themeDelegate) == null || themeDelegate.getCurrentTheme() == null)) {
            this.resourcesProvider = lastFragment.getResourceProvider();
        }
        AnonymousClass4 r0 = new ActionBar(context, this.resourcesProvider) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                ProfileActivity.this.avatarContainer.getHitRect(ProfileActivity.this.rect);
                if (ProfileActivity.this.rect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                    return false;
                }
                return super.onTouchEvent(motionEvent);
            }

            public void setItemsColor(int i, boolean z) {
                super.setItemsColor(i, z);
                if (!z && ProfileActivity.this.ttlIconView != null) {
                    ProfileActivity.this.ttlIconView.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
                }
            }
        };
        boolean z = true;
        r0.setForceSkipTouches(true);
        r0.setBackgroundColor(0);
        r0.setItemsBackgroundColor(getThemedColor("avatar_actionBarSelectorBlue"), false);
        r0.setItemsColor(getThemedColor("actionBarDefaultIcon"), false);
        r0.setBackButtonDrawable(new BackDrawable(false));
        r0.setCastShadows(false);
        r0.setAddToContainer(false);
        r0.setClipContent(true);
        if (Build.VERSION.SDK_INT < 21 || AndroidUtilities.isTablet() || this.inBubbleMode) {
            z = false;
        }
        r0.setOccupyStatusBar(z);
        ImageView backButton = r0.getBackButton();
        backButton.setOnLongClickListener(new ProfileActivity$$ExternalSyntheticLambda16(this, backButton));
        return r0;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createActionBar$2(ImageView imageView, View view) {
        ActionBarPopupWindow show = BackButtonMenu.show(this, imageView, getDialogId(), this.resourcesProvider);
        if (show == null) {
            return false;
        }
        show.setOnDismissListener(new ProfileActivity$$ExternalSyntheticLambda17(this));
        dimBehindView(imageView, 0.3f);
        UndoView undoView2 = this.undoView;
        if (undoView2 != null) {
            undoView2.hide(true, 1);
        }
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createActionBar$1() {
        dimBehindView(false);
    }

    /* JADX WARNING: type inference failed for: r0v125, types: [android.view.View] */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x006e, code lost:
        r0 = r0.participants;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View createView(android.content.Context r38) {
        /*
            r37 = this;
            r14 = r37
            r15 = r38
            org.telegram.ui.ActionBar.Theme.createProfileResources(r38)
            r13 = 0
            org.telegram.ui.ActionBar.Theme.createChatResources(r15, r13)
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r14.parentLayout
            org.telegram.ui.ActionBar.BaseFragment r0 = r0.getLastFragment()
            boolean r1 = r0 instanceof org.telegram.ui.ChatActivity
            if (r1 == 0) goto L_0x0028
            r1 = r0
            org.telegram.ui.ChatActivity r1 = (org.telegram.ui.ChatActivity) r1
            org.telegram.ui.ChatActivity$ThemeDelegate r1 = r1.themeDelegate
            if (r1 == 0) goto L_0x0028
            org.telegram.ui.ActionBar.EmojiThemes r1 = r1.getCurrentTheme()
            if (r1 == 0) goto L_0x0028
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r0 = r0.getResourceProvider()
            r14.resourcesProvider = r0
        L_0x0028:
            r14.searchTransitionOffset = r13
            r12 = 1065353216(0x3var_, float:1.0)
            r14.searchTransitionProgress = r12
            r14.searchMode = r13
            r11 = 1
            r14.hasOwnBackground = r11
            r16 = 1118830592(0x42b00000, float:88.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r0 = (float) r0
            r14.extraHeight = r0
            org.telegram.ui.ActionBar.ActionBar r0 = r14.actionBar
            org.telegram.ui.ProfileActivity$5 r1 = new org.telegram.ui.ProfileActivity$5
            r1.<init>()
            r0.setActionBarMenuOnItemClick(r1)
            org.telegram.ui.Components.SharedMediaLayout r0 = r14.sharedMediaLayout
            if (r0 == 0) goto L_0x004d
            r0.onDestroy()
        L_0x004d:
            long r0 = r14.dialogId
            r17 = 0
            int r2 = (r0 > r17 ? 1 : (r0 == r17 ? 0 : -1))
            if (r2 == 0) goto L_0x0057
        L_0x0055:
            r9 = r0
            goto L_0x0062
        L_0x0057:
            long r0 = r14.userId
            int r2 = (r0 > r17 ? 1 : (r0 == r17 ? 0 : -1))
            if (r2 == 0) goto L_0x005e
            goto L_0x0055
        L_0x005e:
            long r0 = r14.chatId
            long r0 = -r0
            goto L_0x0055
        L_0x0062:
            org.telegram.ui.ProfileActivity$6 r0 = new org.telegram.ui.ProfileActivity$6
            r0.<init>(r15)
            r14.fragmentView = r0
            org.telegram.tgnet.TLRPC$ChatFull r0 = r14.chatInfo
            r8 = 0
            if (r0 == 0) goto L_0x007e
            org.telegram.tgnet.TLRPC$ChatParticipants r0 = r0.participants
            if (r0 == 0) goto L_0x007e
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r0 = r0.participants
            int r0 = r0.size()
            r1 = 5
            if (r0 <= r1) goto L_0x007e
            java.util.ArrayList<java.lang.Integer> r0 = r14.sortedUsers
            goto L_0x007f
        L_0x007e:
            r0 = r8
        L_0x007f:
            org.telegram.ui.ProfileActivity$7 r7 = new org.telegram.ui.ProfileActivity$7
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloader r5 = r14.sharedMediaPreloader
            org.telegram.tgnet.TLRPC$UserFull r1 = r14.userInfo
            if (r1 == 0) goto L_0x008b
            int r1 = r1.common_chats_count
            r6 = r1
            goto L_0x008c
        L_0x008b:
            r6 = 0
        L_0x008c:
            java.util.ArrayList<java.lang.Integer> r3 = r14.sortedUsers
            org.telegram.tgnet.TLRPC$ChatFull r4 = r14.chatInfo
            if (r0 == 0) goto L_0x0095
            r19 = 1
            goto L_0x0097
        L_0x0095:
            r19 = 0
        L_0x0097:
            r20 = 1
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r14.resourcesProvider
            r0 = r7
            r1 = r37
            r21 = r2
            r2 = r38
            r22 = r3
            r23 = r4
            r3 = r9
            r24 = r7
            r7 = r22
            r8 = r23
            r26 = r9
            r9 = r19
            r10 = r37
            r15 = 1
            r11 = r37
            r12 = r20
            r15 = 0
            r13 = r21
            r0.<init>(r2, r3, r5, r6, r7, r8, r9, r10, r11, r12, r13)
            r0 = r24
            r14.sharedMediaLayout = r0
            androidx.recyclerview.widget.RecyclerView$LayoutParams r1 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
            r9 = -1
            r1.<init>((int) r9, (int) r9)
            r0.setLayoutParams(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r14.actionBar
            org.telegram.ui.ActionBar.ActionBarMenu r0 = r0.createMenu()
            long r1 = r14.userId
            org.telegram.messenger.UserConfig r3 = r37.getUserConfig()
            long r3 = r3.clientUserId
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 != 0) goto L_0x010f
            r1 = 37
            int r2 = org.telegram.messenger.R.drawable.msg_qr_mini
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r3 = r37.getResourceProvider()
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.addItem(r1, r2, r3)
            r14.qrItem = r1
            int r2 = org.telegram.messenger.R.string.GetQRCode
            java.lang.String r3 = "GetQRCode"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setContentDescription(r2)
            r14.updateQrItemVisibility(r15)
            int r1 = r14.currentAccount
            org.telegram.messenger.ContactsController r1 = org.telegram.messenger.ContactsController.getInstance(r1)
            r2 = 7
            java.util.ArrayList r1 = r1.getPrivacyRules(r2)
            if (r1 != 0) goto L_0x010f
            int r1 = r14.currentAccount
            org.telegram.messenger.ContactsController r1 = org.telegram.messenger.ContactsController.getInstance(r1)
            r1.loadPrivacySettings()
        L_0x010f:
            org.telegram.ui.Components.ImageUpdater r1 = r14.imageUpdater
            r2 = 8
            if (r1 == 0) goto L_0x0154
            r1 = 32
            int r3 = org.telegram.messenger.R.drawable.ic_ab_search
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.addItem((int) r1, (int) r3)
            r3 = 1
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r1.setIsSearchField(r3)
            org.telegram.ui.ProfileActivity$8 r4 = new org.telegram.ui.ProfileActivity$8
            r4.<init>()
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r1.setActionBarMenuItemSearchListener(r4)
            r14.searchItem = r1
            int r4 = org.telegram.messenger.R.string.SearchInSettings
            java.lang.String r5 = "SearchInSettings"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r1.setContentDescription(r6)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r14.searchItem
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r1.setSearchFieldHint(r4)
            org.telegram.ui.Components.SharedMediaLayout r1 = r14.sharedMediaLayout
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r1.getSearchItem()
            r1.setVisibility(r2)
            boolean r1 = r14.expandPhoto
            if (r1 == 0) goto L_0x0155
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r14.searchItem
            r1.setVisibility(r2)
            goto L_0x0155
        L_0x0154:
            r3 = 1
        L_0x0155:
            r1 = 16
            int r4 = org.telegram.messenger.R.drawable.profile_video
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.addItem((int) r1, (int) r4)
            r14.videoCallItem = r1
            int r4 = org.telegram.messenger.R.string.VideoCall
            java.lang.String r5 = "VideoCall"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r1.setContentDescription(r4)
            long r4 = r14.chatId
            r1 = 15
            int r6 = (r4 > r17 ? 1 : (r4 == r17 ? 0 : -1))
            if (r6 == 0) goto L_0x019e
            int r4 = org.telegram.messenger.R.drawable.msg_voicechat2
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.addItem((int) r1, (int) r4)
            r14.callItem = r1
            org.telegram.tgnet.TLRPC$Chat r1 = r14.currentChat
            boolean r1 = org.telegram.messenger.ChatObject.isChannelOrGiga(r1)
            if (r1 == 0) goto L_0x0190
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r14.callItem
            int r4 = org.telegram.messenger.R.string.VoipChannelVoiceChat
            java.lang.String r5 = "VoipChannelVoiceChat"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r1.setContentDescription(r4)
            goto L_0x01b1
        L_0x0190:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r14.callItem
            int r4 = org.telegram.messenger.R.string.VoipGroupVoiceChat
            java.lang.String r5 = "VoipGroupVoiceChat"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r1.setContentDescription(r4)
            goto L_0x01b1
        L_0x019e:
            int r4 = org.telegram.messenger.R.drawable.ic_call
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.addItem((int) r1, (int) r4)
            r14.callItem = r1
            int r4 = org.telegram.messenger.R.string.Call
            java.lang.String r5 = "Call"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r1.setContentDescription(r4)
        L_0x01b1:
            r1 = 12
            int r4 = org.telegram.messenger.R.drawable.group_edit_profile
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.addItem((int) r1, (int) r4)
            r14.editItem = r1
            int r4 = org.telegram.messenger.R.string.Edit
            java.lang.String r5 = "Edit"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r1.setContentDescription(r4)
            r1 = 10
            int r4 = org.telegram.messenger.R.drawable.ic_ab_other
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r5 = r14.resourcesProvider
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r0.addItem(r1, r4, r5)
            r14.otherItem = r0
            android.widget.ImageView r0 = new android.widget.ImageView
            r10 = r38
            r11 = 1
            r0.<init>(r10)
            r14.ttlIconView = r0
            android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
            java.lang.String r3 = "actionBarDefaultIcon"
            int r3 = r14.getThemedColor(r3)
            android.graphics.PorterDuff$Mode r4 = android.graphics.PorterDuff.Mode.MULTIPLY
            r1.<init>(r3, r4)
            r0.setColorFilter(r1)
            android.widget.ImageView r0 = r14.ttlIconView
            r1 = 1061997773(0x3f4ccccd, float:0.8)
            org.telegram.messenger.AndroidUtilities.updateViewVisibilityAnimated(r0, r15, r1, r15)
            android.widget.ImageView r0 = r14.ttlIconView
            int r1 = org.telegram.messenger.R.drawable.msg_mini_autodelete_timer
            r0.setImageResource(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r14.otherItem
            android.widget.ImageView r1 = r14.ttlIconView
            r29 = 12
            r30 = 1094713344(0x41400000, float:12.0)
            r31 = 19
            r32 = 1090519040(0x41000000, float:8.0)
            r33 = 1073741824(0x40000000, float:2.0)
            r34 = 0
            r35 = 0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r29, r30, r31, r32, r33, r34, r35)
            r0.addView(r1, r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r14.otherItem
            int r1 = org.telegram.messenger.R.string.AccDescrMoreOptions
            java.lang.String r3 = "AccDescrMoreOptions"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r0.setContentDescription(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r14.listView
            if (r0 == 0) goto L_0x024a
            org.telegram.ui.Components.ImageUpdater r0 = r14.imageUpdater
            if (r0 == 0) goto L_0x024a
            androidx.recyclerview.widget.LinearLayoutManager r0 = r14.layoutManager
            int r0 = r0.findFirstVisibleItemPosition()
            androidx.recyclerview.widget.LinearLayoutManager r1 = r14.layoutManager
            android.view.View r1 = r1.findViewByPosition(r0)
            if (r1 == 0) goto L_0x0240
            r1.getTop()
            org.telegram.ui.Components.RecyclerListView r1 = r14.listView
            r1.getPaddingTop()
            goto L_0x0241
        L_0x0240:
            r0 = -1
        L_0x0241:
            org.telegram.ui.Components.RLottieImageView r1 = r14.writeButton
            java.lang.Object r8 = r1.getTag()
            r12 = r0
            r13 = r8
            goto L_0x024c
        L_0x024a:
            r12 = -1
            r13 = 0
        L_0x024c:
            r14.createActionBarMenu(r15)
            org.telegram.ui.ProfileActivity$ListAdapter r0 = new org.telegram.ui.ProfileActivity$ListAdapter
            r0.<init>(r10)
            r14.listAdapter = r0
            org.telegram.ui.ProfileActivity$SearchAdapter r0 = new org.telegram.ui.ProfileActivity$SearchAdapter
            r0.<init>(r14, r10)
            r14.searchAdapter = r0
            org.telegram.ui.Components.AvatarDrawable r0 = new org.telegram.ui.Components.AvatarDrawable
            r0.<init>()
            r14.avatarDrawable = r0
            r0.setProfile(r11)
            android.view.View r0 = r14.fragmentView
            r0.setWillNotDraw(r15)
            android.view.View r0 = r14.fragmentView
            r1 = r0
            org.telegram.ui.ProfileActivity$NestedFrameLayout r1 = (org.telegram.ui.ProfileActivity.NestedFrameLayout) r1
            r14.contentView = r1
            r1.needBlur = r11
            r8 = r0
            android.widget.FrameLayout r8 = (android.widget.FrameLayout) r8
            org.telegram.ui.ProfileActivity$9 r0 = new org.telegram.ui.ProfileActivity$9
            r0.<init>(r10)
            r14.listView = r0
            r0.setVerticalScrollBarEnabled(r15)
            org.telegram.ui.ProfileActivity$10 r0 = new org.telegram.ui.ProfileActivity$10
            r0.<init>()
            org.telegram.ui.Components.RecyclerListView r1 = r14.listView
            r1.setItemAnimator(r0)
            r0.setSupportsChangeAnimations(r15)
            r0.setDelayAnimations(r15)
            org.telegram.ui.Components.RecyclerListView r0 = r14.listView
            r0.setClipToPadding(r15)
            org.telegram.ui.Components.RecyclerListView r0 = r14.listView
            r0.setHideIfEmpty(r15)
            org.telegram.ui.ProfileActivity$11 r0 = new org.telegram.ui.ProfileActivity$11
            r0.<init>(r10)
            r14.layoutManager = r0
            r0.setOrientation(r11)
            androidx.recyclerview.widget.LinearLayoutManager r0 = r14.layoutManager
            r0.mIgnoreTopPadding = r15
            org.telegram.ui.Components.RecyclerListView r1 = r14.listView
            r1.setLayoutManager(r0)
            org.telegram.ui.Components.RecyclerListView r0 = r14.listView
            r0.setGlowColor(r15)
            org.telegram.ui.Components.RecyclerListView r0 = r14.listView
            org.telegram.ui.ProfileActivity$ListAdapter r1 = r14.listAdapter
            r0.setAdapter(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r14.listView
            r1 = 51
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r9, (int) r9, (int) r1)
            r8.addView(r0, r3)
            org.telegram.ui.Components.RecyclerListView r0 = r14.listView
            org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda38 r3 = new org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda38
            r4 = r26
            r3.<init>(r14, r4, r10)
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended) r3)
            org.telegram.ui.Components.RecyclerListView r0 = r14.listView
            org.telegram.ui.ProfileActivity$13 r3 = new org.telegram.ui.ProfileActivity$13
            r3.<init>()
            r0.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r14.searchItem
            java.lang.String r3 = "avatar_backgroundActionBarBlue"
            if (r0 == 0) goto L_0x036e
            org.telegram.ui.Components.RecyclerListView r0 = new org.telegram.ui.Components.RecyclerListView
            r0.<init>(r10)
            r14.searchListView = r0
            r0.setVerticalScrollBarEnabled(r15)
            org.telegram.ui.Components.RecyclerListView r0 = r14.searchListView
            androidx.recyclerview.widget.LinearLayoutManager r4 = new androidx.recyclerview.widget.LinearLayoutManager
            r4.<init>(r10, r11, r15)
            r0.setLayoutManager(r4)
            org.telegram.ui.Components.RecyclerListView r0 = r14.searchListView
            int r4 = r14.getThemedColor(r3)
            r0.setGlowColor(r4)
            org.telegram.ui.Components.RecyclerListView r0 = r14.searchListView
            org.telegram.ui.ProfileActivity$SearchAdapter r4 = r14.searchAdapter
            r0.setAdapter(r4)
            org.telegram.ui.Components.RecyclerListView r0 = r14.searchListView
            r7 = 0
            r0.setItemAnimator(r7)
            org.telegram.ui.Components.RecyclerListView r0 = r14.searchListView
            r0.setVisibility(r2)
            org.telegram.ui.Components.RecyclerListView r0 = r14.searchListView
            r0.setLayoutAnimation(r7)
            org.telegram.ui.Components.RecyclerListView r0 = r14.searchListView
            java.lang.String r4 = "windowBackgroundWhite"
            int r4 = r14.getThemedColor(r4)
            r0.setBackgroundColor(r4)
            org.telegram.ui.Components.RecyclerListView r0 = r14.searchListView
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r9, (int) r9, (int) r1)
            r8.addView(r0, r4)
            org.telegram.ui.Components.RecyclerListView r0 = r14.searchListView
            org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda37 r4 = new org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda37
            r4.<init>(r14)
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r4)
            org.telegram.ui.Components.RecyclerListView r0 = r14.searchListView
            org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda39 r4 = new org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda39
            r4.<init>(r14)
            r0.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r4)
            org.telegram.ui.Components.RecyclerListView r0 = r14.searchListView
            org.telegram.ui.ProfileActivity$14 r4 = new org.telegram.ui.ProfileActivity$14
            r4.<init>()
            r0.setOnScrollListener(r4)
            org.telegram.ui.Components.RecyclerListView r0 = r14.searchListView
            r0.setAnimateEmptyView(r11, r11)
            org.telegram.ui.Components.StickerEmptyView r0 = new org.telegram.ui.Components.StickerEmptyView
            r0.<init>(r10, r7, r11)
            r14.emptyView = r0
            r0.setAnimateLayoutChange(r11)
            org.telegram.ui.Components.StickerEmptyView r0 = r14.emptyView
            android.widget.TextView r0 = r0.subtitle
            r0.setVisibility(r2)
            org.telegram.ui.Components.StickerEmptyView r0 = r14.emptyView
            r0.setVisibility(r2)
            org.telegram.ui.Components.StickerEmptyView r0 = r14.emptyView
            r8.addView(r0)
            org.telegram.ui.ProfileActivity$SearchAdapter r0 = r14.searchAdapter
            r0.loadFaqWebPage()
            goto L_0x036f
        L_0x036e:
            r7 = 0
        L_0x036f:
            long r4 = r14.banFromGroup
            java.lang.String r19 = "fonts/rmedium.ttf"
            int r0 = (r4 > r17 ? 1 : (r4 == r17 ? 0 : -1))
            if (r0 == 0) goto L_0x041d
            org.telegram.messenger.MessagesController r0 = r37.getMessagesController()
            long r4 = r14.banFromGroup
            java.lang.Long r2 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r2)
            org.telegram.tgnet.TLRPC$ChannelParticipant r2 = r14.currentChannelParticipant
            if (r2 != 0) goto L_0x03ac
            org.telegram.tgnet.TLRPC$TL_channels_getParticipant r2 = new org.telegram.tgnet.TLRPC$TL_channels_getParticipant
            r2.<init>()
            org.telegram.tgnet.TLRPC$InputChannel r4 = org.telegram.messenger.MessagesController.getInputChannel((org.telegram.tgnet.TLRPC$Chat) r0)
            r2.channel = r4
            org.telegram.messenger.MessagesController r4 = r37.getMessagesController()
            long r5 = r14.userId
            org.telegram.tgnet.TLRPC$InputPeer r4 = r4.getInputPeer((long) r5)
            r2.participant = r4
            org.telegram.tgnet.ConnectionsManager r4 = r37.getConnectionsManager()
            org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda33 r5 = new org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda33
            r5.<init>(r14)
            r4.sendRequest(r2, r5)
        L_0x03ac:
            org.telegram.ui.ProfileActivity$15 r2 = new org.telegram.ui.ProfileActivity$15
            r2.<init>(r14, r10)
            r2.setWillNotDraw(r15)
            r4 = 83
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r9, (int) r1, (int) r4)
            r8.addView(r2, r4)
            org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda13 r4 = new org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda13
            r4.<init>(r14, r0)
            r2.setOnClickListener(r4)
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r10)
            java.lang.String r4 = "windowBackgroundWhiteRedText"
            int r4 = r14.getThemedColor(r4)
            r0.setTextColor(r4)
            r4 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r11, r4)
            r4 = 17
            r0.setGravity(r4)
            android.graphics.Typeface r4 = org.telegram.messenger.AndroidUtilities.getTypeface(r19)
            r0.setTypeface(r4)
            int r4 = org.telegram.messenger.R.string.BanFromTheGroup
            java.lang.String r5 = "BanFromTheGroup"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.setText(r4)
            r20 = -2
            r21 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r22 = 17
            r23 = 0
            r24 = 1065353216(0x3var_, float:1.0)
            r25 = 0
            r26 = 0
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r2.addView(r0, r4)
            org.telegram.ui.Components.RecyclerListView r0 = r14.listView
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r4 = 1111490560(0x42400000, float:48.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r0.setPadding(r15, r2, r15, r5)
            org.telegram.ui.Components.RecyclerListView r0 = r14.listView
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r0.setBottomGlowOffset(r2)
            goto L_0x0426
        L_0x041d:
            org.telegram.ui.Components.RecyclerListView r0 = r14.listView
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r0.setPadding(r15, r2, r15, r15)
        L_0x0426:
            org.telegram.ui.ProfileActivity$TopView r0 = new org.telegram.ui.ProfileActivity$TopView
            r0.<init>(r10)
            r14.topView = r0
            int r2 = r14.getThemedColor(r3)
            r0.setBackgroundColor(r2)
            org.telegram.ui.ProfileActivity$TopView r0 = r14.topView
            r8.addView(r0)
            org.telegram.ui.ProfileActivity$NestedFrameLayout r0 = r14.contentView
            java.util.ArrayList<android.view.View> r0 = r0.blurBehindViews
            org.telegram.ui.ProfileActivity$TopView r2 = r14.topView
            r0.add(r2)
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r10)
            r14.avatarContainer = r0
            org.telegram.ui.ProfileActivity$17 r0 = new org.telegram.ui.ProfileActivity$17
            r0.<init>(r10)
            r14.avatarContainer2 = r0
            r6 = 1065353216(0x3var_, float:1.0)
            org.telegram.messenger.AndroidUtilities.updateViewVisibilityAnimated(r0, r11, r6, r15)
            android.widget.FrameLayout r0 = r14.avatarContainer2
            r20 = -1
            r21 = -1082130432(0xffffffffbvar_, float:-1.0)
            r22 = 8388611(0x800003, float:1.1754948E-38)
            r23 = 0
            r24 = 0
            r25 = 0
            r26 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r8.addView(r0, r2)
            android.widget.FrameLayout r0 = r14.avatarContainer
            r5 = 0
            r0.setPivotX(r5)
            android.widget.FrameLayout r0 = r14.avatarContainer
            r0.setPivotY(r5)
            android.widget.FrameLayout r0 = r14.avatarContainer2
            android.widget.FrameLayout r2 = r14.avatarContainer
            r20 = 42
            r21 = 1109917696(0x42280000, float:42.0)
            r22 = 51
            r23 = 1115684864(0x42800000, float:64.0)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r0.addView(r2, r3)
            org.telegram.ui.ProfileActivity$18 r0 = new org.telegram.ui.ProfileActivity$18
            r0.<init>(r14, r10)
            r14.avatarImage = r0
            org.telegram.messenger.ImageReceiver r0 = r0.getImageReceiver()
            r0.setAllowDecodeSingleFrame(r11)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r14.avatarImage
            r2 = 1101529088(0x41a80000, float:21.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setRoundRadius(r2)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r14.avatarImage
            r0.setPivotX(r5)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r14.avatarImage
            r0.setPivotY(r5)
            android.widget.FrameLayout r0 = r14.avatarContainer
            org.telegram.ui.ProfileActivity$AvatarImageView r2 = r14.avatarImage
            r4 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r4)
            r0.addView(r2, r3)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r14.avatarImage
            org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda12 r2 = new org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda12
            r2.<init>(r14)
            r0.setOnClickListener(r2)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r14.avatarImage
            org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda15 r2 = new org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda15
            r2.<init>(r14)
            r0.setOnLongClickListener(r2)
            org.telegram.ui.ProfileActivity$19 r0 = new org.telegram.ui.ProfileActivity$19
            r0.<init>(r10)
            r14.avatarProgressView = r0
            r2 = 1104150528(0x41d00000, float:26.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setSize(r2)
            org.telegram.ui.Components.RadialProgressView r0 = r14.avatarProgressView
            r0.setProgressColor(r9)
            org.telegram.ui.Components.RadialProgressView r0 = r14.avatarProgressView
            r0.setNoProgress(r15)
            android.widget.FrameLayout r0 = r14.avatarContainer
            org.telegram.ui.Components.RadialProgressView r2 = r14.avatarProgressView
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r4)
            r0.addView(r2, r3)
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r10)
            r14.timeItem = r0
            r2 = 1092616192(0x41200000, float:10.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r16 = 1084227584(0x40a00000, float:5.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r0.setPadding(r3, r2, r6, r11)
            android.widget.ImageView r0 = r14.timeItem
            android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.CENTER
            r0.setScaleType(r2)
            android.widget.ImageView r0 = r14.timeItem
            r0.setAlpha(r5)
            android.widget.ImageView r0 = r14.timeItem
            org.telegram.ui.Components.TimerDrawable r2 = new org.telegram.ui.Components.TimerDrawable
            r2.<init>(r10, r7)
            r14.timerDrawable = r2
            r0.setImageDrawable(r2)
            android.widget.ImageView r0 = r14.timeItem
            r0.setTranslationY(r4)
            android.widget.ImageView r0 = r14.timeItem
            r2 = 34
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r2, (int) r2, (int) r1)
            r8.addView(r0, r1)
            r14.showAvatarProgress(r15, r15)
            org.telegram.ui.Components.ProfileGalleryView r0 = r14.avatarsViewPager
            if (r0 == 0) goto L_0x0544
            r0.onDestroy()
        L_0x0544:
            org.telegram.ui.ProfileActivity$OverlaysView r0 = new org.telegram.ui.ProfileActivity$OverlaysView
            r0.<init>(r10)
            r14.overlaysView = r0
            org.telegram.ui.Components.ProfileGalleryView r11 = new org.telegram.ui.Components.ProfileGalleryView
            long r0 = r14.userId
            int r2 = (r0 > r17 ? 1 : (r0 == r17 ? 0 : -1))
            if (r2 == 0) goto L_0x0554
            goto L_0x0557
        L_0x0554:
            long r0 = r14.chatId
            long r0 = -r0
        L_0x0557:
            r2 = r0
            org.telegram.ui.ActionBar.ActionBar r6 = r14.actionBar
            org.telegram.ui.Components.RecyclerListView r1 = r14.listView
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r14.avatarImage
            int r16 = r37.getClassGuid()
            org.telegram.ui.ProfileActivity$OverlaysView r15 = r14.overlaysView
            r22 = r0
            r0 = r11
            r23 = r1
            r1 = r38
            r9 = -1082130432(0xffffffffbvar_, float:-1.0)
            r4 = r6
            r6 = 0
            r5 = r23
            r23 = 1065353216(0x3var_, float:1.0)
            r6 = r22
            r7 = r16
            r36 = r8
            r8 = r15
            r0.<init>(r1, r2, r4, r5, r6, r7, r8)
            r14.avatarsViewPager = r11
            org.telegram.tgnet.TLRPC$ChatFull r0 = r14.chatInfo
            r11.setChatInfo(r0)
            android.widget.FrameLayout r0 = r14.avatarContainer2
            org.telegram.ui.Components.ProfileGalleryView r1 = r14.avatarsViewPager
            r0.addView(r1)
            android.widget.FrameLayout r0 = r14.avatarContainer2
            org.telegram.ui.ProfileActivity$OverlaysView r1 = r14.overlaysView
            r0.addView(r1)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r14.avatarImage
            org.telegram.ui.Components.ProfileGalleryView r1 = r14.avatarsViewPager
            r0.setAvatarsViewPager(r1)
            org.telegram.ui.ProfileActivity$PagerIndicatorView r0 = new org.telegram.ui.ProfileActivity$PagerIndicatorView
            r0.<init>(r10)
            r14.avatarsViewPagerIndicatorView = r0
            android.widget.FrameLayout r1 = r14.avatarContainer2
            r2 = -1
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r9)
            r1.addView(r0, r3)
            org.telegram.ui.ActionBar.ActionBar r0 = r14.actionBar
            r1 = r36
            r1.addView(r0)
            boolean r0 = r14.callItemVisible
            r2 = 54
            if (r0 == 0) goto L_0x05c0
            long r3 = r14.userId
            int r0 = (r3 > r17 ? 1 : (r3 == r17 ? 0 : -1))
            if (r0 == 0) goto L_0x05c0
            r0 = 54
            goto L_0x05c1
        L_0x05c0:
            r0 = 0
        L_0x05c1:
            int r2 = r2 + r0
            float r0 = (float) r2
            r2 = -2
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r14.parentLayout
            if (r3 == 0) goto L_0x0611
            org.telegram.ui.ActionBar.BaseFragment r3 = r3.getLastFragment()
            boolean r3 = r3 instanceof org.telegram.ui.ChatActivity
            if (r3 == 0) goto L_0x0611
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r14.parentLayout
            org.telegram.ui.ActionBar.BaseFragment r3 = r3.getLastFragment()
            org.telegram.ui.ChatActivity r3 = (org.telegram.ui.ChatActivity) r3
            org.telegram.ui.Components.ChatAvatarContainer r3 = r3.getAvatarContainer()
            if (r3 == 0) goto L_0x0611
            android.view.ViewGroup$LayoutParams r4 = r3.getLayoutParams()
            if (r4 == 0) goto L_0x0611
            org.telegram.ui.ActionBar.SimpleTextView r4 = r3.getTitleTextView()
            if (r4 == 0) goto L_0x0611
            android.view.ViewGroup$LayoutParams r0 = r3.getLayoutParams()
            android.view.ViewGroup$MarginLayoutParams r0 = (android.view.ViewGroup.MarginLayoutParams) r0
            int r0 = r0.rightMargin
            int r2 = r3.getWidth()
            org.telegram.ui.ActionBar.SimpleTextView r4 = r3.getTitleTextView()
            int r4 = r4.getRight()
            int r2 = r2 - r4
            int r0 = r0 + r2
            float r0 = (float) r0
            float r2 = org.telegram.messenger.AndroidUtilities.density
            float r0 = r0 / r2
            org.telegram.ui.ActionBar.SimpleTextView r2 = r3.getTitleTextView()
            int r2 = r2.getWidth()
            float r2 = (float) r2
            float r3 = org.telegram.messenger.AndroidUtilities.density
            float r2 = r2 / r3
            int r2 = (int) r2
        L_0x0611:
            r5 = r0
            r0 = 0
        L_0x0613:
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r14.nameTextView
            int r4 = r3.length
            r6 = 2
            if (r0 >= r4) goto L_0x06d5
            int r4 = r14.playProfileAnimation
            if (r4 != 0) goto L_0x0622
            if (r0 != 0) goto L_0x0622
            r4 = 0
            goto L_0x06d1
        L_0x0622:
            org.telegram.ui.ProfileActivity$20 r4 = new org.telegram.ui.ProfileActivity$20
            r4.<init>(r10)
            r3[r0] = r4
            r3 = 1
            if (r0 != r3) goto L_0x063a
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r14.nameTextView
            r3 = r3[r0]
            java.lang.String r4 = "profile_title"
            int r4 = r14.getThemedColor(r4)
            r3.setTextColor(r4)
            goto L_0x0647
        L_0x063a:
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r14.nameTextView
            r3 = r3[r0]
            java.lang.String r4 = "actionBarDefaultTitle"
            int r4 = r14.getThemedColor(r4)
            r3.setTextColor(r4)
        L_0x0647:
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r14.nameTextView
            r3 = r3[r0]
            r4 = 18
            r3.setTextSize(r4)
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r14.nameTextView
            r3 = r3[r0]
            r4 = 3
            r3.setGravity(r4)
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r14.nameTextView
            r3 = r3[r0]
            android.graphics.Typeface r4 = org.telegram.messenger.AndroidUtilities.getTypeface(r19)
            r3.setTypeface(r4)
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r14.nameTextView
            r3 = r3[r0]
            r4 = 1067869798(0x3fa66666, float:1.3)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = -r4
            r3.setLeftDrawableTopPadding(r4)
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r14.nameTextView
            r3 = r3[r0]
            r4 = 0
            r3.setPivotX(r4)
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r14.nameTextView
            r3 = r3[r0]
            r3.setPivotY(r4)
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r14.nameTextView
            r3 = r3[r0]
            if (r0 != 0) goto L_0x0689
            r7 = 0
            goto L_0x068b
        L_0x0689:
            r7 = 1065353216(0x3var_, float:1.0)
        L_0x068b:
            r3.setAlpha(r7)
            r3 = 1
            if (r0 != r3) goto L_0x069f
            org.telegram.ui.ActionBar.SimpleTextView[] r7 = r14.nameTextView
            r7 = r7[r0]
            r7.setScrollNonFitText(r3)
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r14.nameTextView
            r3 = r3[r0]
            r3.setImportantForAccessibility(r6)
        L_0x069f:
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r14.nameTextView
            r3 = r3[r0]
            if (r0 != 0) goto L_0x06a7
            r6 = 1
            goto L_0x06a8
        L_0x06a7:
            r6 = 0
        L_0x06a8:
            r3.setFocusable(r6)
            android.widget.FrameLayout r3 = r14.avatarContainer2
            org.telegram.ui.ActionBar.SimpleTextView[] r6 = r14.nameTextView
            r6 = r6[r0]
            if (r0 != 0) goto L_0x06b6
            r25 = r2
            goto L_0x06b9
        L_0x06b6:
            r7 = -2
            r25 = -2
        L_0x06b9:
            r26 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r27 = 51
            r28 = 1122762752(0x42eCLASSNAME, float:118.0)
            r29 = 0
            if (r0 != 0) goto L_0x06c6
            r30 = r5
            goto L_0x06c8
        L_0x06c6:
            r30 = 0
        L_0x06c8:
            r31 = 0
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r3.addView(r6, r7)
        L_0x06d1:
            int r0 = r0 + 1
            goto L_0x0613
        L_0x06d5:
            r4 = 0
            r0 = 0
        L_0x06d7:
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r14.onlineTextView
            int r3 = r2.length
            if (r0 >= r3) goto L_0x074b
            org.telegram.ui.ActionBar.SimpleTextView r3 = new org.telegram.ui.ActionBar.SimpleTextView
            r3.<init>(r10)
            r2[r0] = r3
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r14.onlineTextView
            r2 = r2[r0]
            java.lang.String r3 = "avatar_subtitleInProfileBlue"
            int r3 = r14.getThemedColor(r3)
            r2.setTextColor(r3)
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r14.onlineTextView
            r2 = r2[r0]
            r3 = 14
            r2.setTextSize(r3)
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r14.onlineTextView
            r2 = r2[r0]
            r3 = 3
            r2.setGravity(r3)
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r14.onlineTextView
            r2 = r2[r0]
            if (r0 == 0) goto L_0x070d
            if (r0 != r6) goto L_0x070a
            goto L_0x070d
        L_0x070a:
            r3 = 1065353216(0x3var_, float:1.0)
            goto L_0x070e
        L_0x070d:
            r3 = 0
        L_0x070e:
            r2.setAlpha(r3)
            if (r0 <= 0) goto L_0x071a
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r14.onlineTextView
            r2 = r2[r0]
            r2.setImportantForAccessibility(r6)
        L_0x071a:
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r14.onlineTextView
            r2 = r2[r0]
            if (r0 != 0) goto L_0x0722
            r3 = 1
            goto L_0x0723
        L_0x0722:
            r3 = 0
        L_0x0723:
            r2.setFocusable(r3)
            android.widget.FrameLayout r2 = r14.avatarContainer2
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r14.onlineTextView
            r3 = r3[r0]
            r25 = -2
            r26 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r27 = 51
            r28 = 1122762752(0x42eCLASSNAME, float:118.0)
            r29 = 0
            if (r0 != 0) goto L_0x073b
            r30 = r5
            goto L_0x073f
        L_0x073b:
            r7 = 1090519040(0x41000000, float:8.0)
            r30 = 1090519040(0x41000000, float:8.0)
        L_0x073f:
            r31 = 0
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r2.addView(r3, r7)
            int r0 = r0 + 1
            goto L_0x06d7
        L_0x074b:
            org.telegram.ui.ProfileActivity$21 r0 = new org.telegram.ui.ProfileActivity$21
            r0.<init>(r10, r10)
            r14.mediaCounterTextView = r0
            r0.setAlpha(r4)
            android.widget.FrameLayout r0 = r14.avatarContainer2
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r2 = r14.mediaCounterTextView
            r25 = -2
            r26 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r27 = 51
            r28 = 1122762752(0x42eCLASSNAME, float:118.0)
            r29 = 0
            r30 = 1090519040(0x41000000, float:8.0)
            r31 = 0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r0.addView(r2, r3)
            r0 = 1
            r14.updateProfileData(r0)
            org.telegram.ui.Components.RLottieImageView r0 = new org.telegram.ui.Components.RLottieImageView
            r0.<init>(r10)
            r14.writeButton = r0
            android.content.res.Resources r0 = r38.getResources()
            int r2 = org.telegram.messenger.R.drawable.floating_shadow_profile
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r2)
            android.graphics.drawable.Drawable r0 = r0.mutate()
            android.graphics.PorterDuffColorFilter r2 = new android.graphics.PorterDuffColorFilter
            r3 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.graphics.PorterDuff$Mode r5 = android.graphics.PorterDuff.Mode.MULTIPLY
            r2.<init>(r3, r5)
            r0.setColorFilter(r2)
            org.telegram.ui.Components.CombinedDrawable r2 = new org.telegram.ui.Components.CombinedDrawable
            r3 = 1113587712(0x42600000, float:56.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r3)
            java.lang.String r7 = "profile_actionBackground"
            int r7 = r14.getThemedColor(r7)
            java.lang.String r8 = "profile_actionPressedBackground"
            int r8 = r14.getThemedColor(r8)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorCircleDrawable(r5, r7, r8)
            r7 = 0
            r2.<init>(r0, r5, r7, r7)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r2.setIconSize(r0, r5)
            org.telegram.ui.Components.RLottieImageView r0 = r14.writeButton
            r0.setBackground(r2)
            long r7 = r14.userId
            int r0 = (r7 > r17 ? 1 : (r7 == r17 ? 0 : -1))
            if (r0 == 0) goto L_0x0846
            org.telegram.ui.Components.ImageUpdater r0 = r14.imageUpdater
            if (r0 == 0) goto L_0x0831
            org.telegram.ui.Components.RLottieDrawable r0 = new org.telegram.ui.Components.RLottieDrawable
            int r2 = org.telegram.messenger.R.raw.camera_outline
            java.lang.String r27 = java.lang.String.valueOf(r2)
            int r28 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r29 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r30 = 0
            r31 = 0
            r25 = r0
            r26 = r2
            r25.<init>(r26, r27, r28, r29, r30, r31)
            r14.cameraDrawable = r0
            org.telegram.ui.Components.RLottieDrawable r0 = new org.telegram.ui.Components.RLottieDrawable
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r2)
            java.lang.String r5 = "_cell"
            r3.append(r5)
            java.lang.String r27 = r3.toString()
            r3 = 1109917696(0x42280000, float:42.0)
            int r28 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r29 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r25 = r0
            r25.<init>(r26, r27, r28, r29, r30, r31)
            r14.cellCameraDrawable = r0
            org.telegram.ui.Components.RLottieImageView r0 = r14.writeButton
            org.telegram.ui.Components.RLottieDrawable r2 = r14.cameraDrawable
            r0.setAnimation(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r14.writeButton
            int r2 = org.telegram.messenger.R.string.AccDescrChangeProfilePicture
            java.lang.String r3 = "AccDescrChangeProfilePicture"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setContentDescription(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r14.writeButton
            r2 = 1073741824(0x40000000, float:2.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r3 = 1073741824(0x40000000, float:2.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r5 = 0
            r0.setPadding(r2, r5, r5, r3)
            goto L_0x085a
        L_0x0831:
            org.telegram.ui.Components.RLottieImageView r0 = r14.writeButton
            int r2 = org.telegram.messenger.R.drawable.profile_newmsg
            r0.setImageResource(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r14.writeButton
            int r2 = org.telegram.messenger.R.string.AccDescrOpenChat
            java.lang.String r3 = "AccDescrOpenChat"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setContentDescription(r2)
            goto L_0x085a
        L_0x0846:
            org.telegram.ui.Components.RLottieImageView r0 = r14.writeButton
            int r2 = org.telegram.messenger.R.drawable.profile_discuss
            r0.setImageResource(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r14.writeButton
            int r2 = org.telegram.messenger.R.string.ViewDiscussion
            java.lang.String r3 = "ViewDiscussion"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setContentDescription(r2)
        L_0x085a:
            org.telegram.ui.Components.RLottieImageView r0 = r14.writeButton
            android.graphics.PorterDuffColorFilter r2 = new android.graphics.PorterDuffColorFilter
            java.lang.String r3 = "profile_actionIcon"
            int r3 = r14.getThemedColor(r3)
            android.graphics.PorterDuff$Mode r5 = android.graphics.PorterDuff.Mode.MULTIPLY
            r2.<init>(r3, r5)
            r0.setColorFilter(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r14.writeButton
            android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.CENTER
            r0.setScaleType(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r14.writeButton
            r25 = 60
            r26 = 1114636288(0x42700000, float:60.0)
            r27 = 53
            r28 = 0
            r29 = 0
            r30 = 1098907648(0x41800000, float:16.0)
            r31 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r1.addView(r0, r2)
            org.telegram.ui.Components.RLottieImageView r0 = r14.writeButton
            org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda11 r2 = new org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda11
            r2.<init>(r14)
            r0.setOnClickListener(r2)
            r0 = 0
            r14.needLayout(r0)
            r2 = -1
            if (r12 == r2) goto L_0x08b8
            if (r13 == 0) goto L_0x08b8
            org.telegram.ui.Components.RLottieImageView r2 = r14.writeButton
            java.lang.Integer r3 = java.lang.Integer.valueOf(r0)
            r2.setTag(r3)
            org.telegram.ui.Components.RLottieImageView r0 = r14.writeButton
            r2 = 1045220557(0x3e4ccccd, float:0.2)
            r0.setScaleX(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r14.writeButton
            r0.setScaleY(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r14.writeButton
            r0.setAlpha(r4)
        L_0x08b8:
            org.telegram.ui.Components.RecyclerListView r0 = r14.listView
            org.telegram.ui.ProfileActivity$22 r2 = new org.telegram.ui.ProfileActivity$22
            r2.<init>()
            r0.setOnScrollListener(r2)
            org.telegram.ui.Components.UndoView r0 = new org.telegram.ui.Components.UndoView
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r14.resourcesProvider
            r3 = 0
            r5 = 0
            r0.<init>(r10, r3, r5, r2)
            r14.undoView = r0
            r7 = -1
            r8 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r9 = 83
            r10 = 1090519040(0x41000000, float:8.0)
            r11 = 0
            r12 = 1090519040(0x41000000, float:8.0)
            r13 = 1090519040(0x41000000, float:8.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r8, r9, r10, r11, r12, r13)
            r1.addView(r0, r2)
            float[] r0 = new float[r6]
            r0 = {0, NUM} // fill-array
            android.animation.ValueAnimator r0 = android.animation.ValueAnimator.ofFloat(r0)
            r14.expandAnimator = r0
            org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda1 r2 = new org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda1
            r2.<init>(r14)
            r0.addUpdateListener(r2)
            android.animation.ValueAnimator r0 = r14.expandAnimator
            org.telegram.ui.Components.CubicBezierInterpolator r2 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
            r0.setInterpolator(r2)
            android.animation.ValueAnimator r0 = r14.expandAnimator
            org.telegram.ui.ProfileActivity$23 r2 = new org.telegram.ui.ProfileActivity$23
            r2.<init>()
            r0.addListener(r2)
            r37.updateRowsIds()
            r37.updateSelectedMediaTabText()
            org.telegram.ui.Components.HintView r0 = new org.telegram.ui.Components.HintView
            android.app.Activity r2 = r37.getParentActivity()
            r3 = 9
            r0.<init>(r2, r3)
            r14.fwdRestrictedHint = r0
            r0.setAlpha(r4)
            org.telegram.ui.Components.HintView r0 = r14.fwdRestrictedHint
            r2 = -2
            r3 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r4 = 51
            r5 = 1094713344(0x41400000, float:12.0)
            r6 = 0
            r7 = 1094713344(0x41400000, float:12.0)
            r8 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r3, r4, r5, r6, r7, r8)
            r1.addView(r0, r2)
            org.telegram.ui.Components.SharedMediaLayout r0 = r14.sharedMediaLayout
            org.telegram.ui.Components.HintView r2 = r14.fwdRestrictedHint
            r0.setForwardRestrictedHint(r2)
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 21
            if (r0 < r2) goto L_0x094b
            android.app.Activity r0 = r37.getParentActivity()
            android.view.Window r0 = r0.getWindow()
            android.view.View r0 = r0.getDecorView()
            r8 = r0
            android.view.ViewGroup r8 = (android.view.ViewGroup) r8
            goto L_0x094c
        L_0x094b:
            r8 = r1
        L_0x094c:
            org.telegram.ui.ProfileActivity$24 r0 = new org.telegram.ui.ProfileActivity$24
            r0.<init>(r8, r1)
            r14.pinchToZoomHelper = r0
            org.telegram.ui.ProfileActivity$25 r1 = new org.telegram.ui.ProfileActivity$25
            r1.<init>()
            r0.setCallback(r1)
            org.telegram.ui.Components.ProfileGalleryView r0 = r14.avatarsViewPager
            org.telegram.ui.PinchToZoomHelper r1 = r14.pinchToZoomHelper
            r0.setPinchToZoomHelper(r1)
            android.graphics.Paint r0 = r14.scrimPaint
            r1 = 0
            r0.setAlpha(r1)
            android.graphics.Paint r0 = r14.actionBarBackgroundPaint
            java.lang.String r1 = "listSelectorSDK21"
            int r1 = r14.getThemedColor(r1)
            r0.setColor(r1)
            org.telegram.ui.ProfileActivity$NestedFrameLayout r0 = r14.contentView
            java.util.ArrayList<android.view.View> r0 = r0.blurBehindViews
            org.telegram.ui.Components.SharedMediaLayout r1 = r14.sharedMediaLayout
            r0.add(r1)
            r37.updateTtlIcon()
            android.view.View r0 = r14.fragmentView
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.createView(android.content.Context):android.view.View");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$4(long j, Context context, View view, int i, float f, float f2) {
        TLRPC$ChatParticipant tLRPC$ChatParticipant;
        final long j2 = j;
        View view2 = view;
        int i2 = i;
        float f3 = f;
        if (getParentActivity() != null) {
            this.listView.stopScroll();
            if (i2 == this.settingsKeyRow) {
                Bundle bundle = new Bundle();
                bundle.putInt("chat_id", DialogObject.getEncryptedChatId(this.dialogId));
                presentFragment(new IdenticonActivity(bundle));
            } else if (i2 == this.settingsTimerRow) {
                showDialog(AlertsCreator.createTTLAlert(getParentActivity(), this.currentEncryptedChat, this.resourcesProvider).create());
            } else if (i2 == this.notificationsRow) {
                if ((!LocaleController.isRTL || f3 > ((float) AndroidUtilities.dp(76.0f))) && (LocaleController.isRTL || f3 < ((float) (view.getMeasuredWidth() - AndroidUtilities.dp(76.0f))))) {
                    ChatNotificationsPopupWrapper chatNotificationsPopupWrapper = new ChatNotificationsPopupWrapper(context, this.currentAccount, (PopupSwipeBackLayout) null, true, true, new ChatNotificationsPopupWrapper.Callback() {
                        public /* synthetic */ void dismiss() {
                            ChatNotificationsPopupWrapper.Callback.CC.$default$dismiss(this);
                        }

                        public void toggleSound() {
                            SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(ProfileActivity.this.currentAccount);
                            boolean z = !notificationsSettings.getBoolean("sound_enabled_" + j2, true);
                            notificationsSettings.edit().putBoolean("sound_enabled_" + j2, z).apply();
                            if (BulletinFactory.canShowBulletin(ProfileActivity.this)) {
                                ProfileActivity profileActivity = ProfileActivity.this;
                                BulletinFactory.createSoundEnabledBulletin(profileActivity, z ^ true ? 1 : 0, profileActivity.getResourceProvider()).show();
                            }
                        }

                        public void muteFor(int i) {
                            if (i == 0) {
                                if (ProfileActivity.this.getMessagesController().isDialogMuted(j2)) {
                                    toggleMute();
                                }
                                if (BulletinFactory.canShowBulletin(ProfileActivity.this)) {
                                    ProfileActivity profileActivity = ProfileActivity.this;
                                    BulletinFactory.createMuteBulletin(profileActivity, 4, i, profileActivity.getResourceProvider()).show();
                                    return;
                                }
                                return;
                            }
                            ProfileActivity.this.getNotificationsController().muteUntil(j2, i);
                            if (BulletinFactory.canShowBulletin(ProfileActivity.this)) {
                                ProfileActivity profileActivity2 = ProfileActivity.this;
                                BulletinFactory.createMuteBulletin(profileActivity2, 5, i, profileActivity2.getResourceProvider()).show();
                            }
                            if (ProfileActivity.this.notificationsRow >= 0) {
                                ProfileActivity.this.listAdapter.notifyItemChanged(ProfileActivity.this.notificationsRow);
                            }
                        }

                        public void showCustomize() {
                            if (j2 != 0) {
                                Bundle bundle = new Bundle();
                                bundle.putLong("dialog_id", j2);
                                ProfileActivity profileActivity = ProfileActivity.this;
                                profileActivity.presentFragment(new ProfileNotificationsActivity(bundle, profileActivity.resourcesProvider));
                            }
                        }

                        public void toggleMute() {
                            boolean isDialogMuted = ProfileActivity.this.getMessagesController().isDialogMuted(j2);
                            ProfileActivity.this.getNotificationsController().muteDialog(j2, !isDialogMuted);
                            BulletinFactory.createMuteBulletin(ProfileActivity.this, !isDialogMuted, (Theme.ResourcesProvider) null).show();
                            if (ProfileActivity.this.notificationsRow >= 0) {
                                ProfileActivity.this.listAdapter.notifyItemChanged(ProfileActivity.this.notificationsRow);
                            }
                        }
                    }, getResourceProvider());
                    chatNotificationsPopupWrapper.lambda$update$10(j2);
                    chatNotificationsPopupWrapper.showAsOptions(this, view2, f3, f2);
                    return;
                }
                NotificationsCheckCell notificationsCheckCell = (NotificationsCheckCell) view2;
                boolean z = !notificationsCheckCell.isChecked();
                boolean isGlobalNotificationsEnabled = getNotificationsController().isGlobalNotificationsEnabled(j2);
                long j3 = 0;
                if (z) {
                    SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                    if (isGlobalNotificationsEnabled) {
                        edit.remove("notify2_" + j2);
                    } else {
                        edit.putInt("notify2_" + j2, 0);
                    }
                    getMessagesStorage().setDialogFlags(j2, 0);
                    edit.commit();
                    TLRPC$Dialog tLRPC$Dialog = getMessagesController().dialogs_dict.get(j2);
                    if (tLRPC$Dialog != null) {
                        tLRPC$Dialog.notify_settings = new TLRPC$TL_peerNotifySettings();
                    }
                } else {
                    SharedPreferences.Editor edit2 = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                    if (!isGlobalNotificationsEnabled) {
                        edit2.remove("notify2_" + j2);
                    } else {
                        edit2.putInt("notify2_" + j2, 2);
                        j3 = 1;
                    }
                    getNotificationsController().removeNotificationsForDialog(j2);
                    getMessagesStorage().setDialogFlags(j2, j3);
                    edit2.commit();
                    TLRPC$Dialog tLRPC$Dialog2 = getMessagesController().dialogs_dict.get(j2);
                    if (tLRPC$Dialog2 != null) {
                        TLRPC$TL_peerNotifySettings tLRPC$TL_peerNotifySettings = new TLRPC$TL_peerNotifySettings();
                        tLRPC$Dialog2.notify_settings = tLRPC$TL_peerNotifySettings;
                        if (isGlobalNotificationsEnabled) {
                            tLRPC$TL_peerNotifySettings.mute_until = Integer.MAX_VALUE;
                        }
                    }
                }
                getNotificationsController().updateServerNotificationsSettings(j2);
                notificationsCheckCell.setChecked(z);
                RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findViewHolderForPosition(this.notificationsRow);
                if (holder != null) {
                    this.listAdapter.onBindViewHolder(holder, this.notificationsRow);
                }
            } else if (i2 == this.unblockRow) {
                getMessagesController().unblockPeer(this.userId);
                if (BulletinFactory.canShowBulletin(this)) {
                    BulletinFactory.createBanBulletin(this, false).show();
                }
            } else if (i2 == this.addToGroupButtonRow) {
                try {
                    this.actionBar.getActionBarMenuOnItemClick().onItemClick(9);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else if (i2 == this.sendMessageRow) {
                onWriteButtonClick();
            } else if (i2 == this.reportRow) {
                AlertsCreator.createReportAlert(getParentActivity(), getDialogId(), 0, this, this.resourcesProvider, (Runnable) null);
            } else if (i2 >= this.membersStartRow && i2 < this.membersEndRow) {
                if (!this.sortedUsers.isEmpty()) {
                    tLRPC$ChatParticipant = this.chatInfo.participants.participants.get(this.sortedUsers.get(i2 - this.membersStartRow).intValue());
                } else {
                    tLRPC$ChatParticipant = this.chatInfo.participants.participants.get(i2 - this.membersStartRow);
                }
                onMemberClick(tLRPC$ChatParticipant, false);
            } else if (i2 == this.addMemberRow) {
                openAddMember();
            } else if (i2 == this.usernameRow) {
                processOnClickOrPress(i2, view2);
            } else if (i2 == this.locationRow) {
                if (this.chatInfo.location instanceof TLRPC$TL_channelLocation) {
                    LocationActivity locationActivity = new LocationActivity(5);
                    locationActivity.setChatLocation(this.chatId, (TLRPC$TL_channelLocation) this.chatInfo.location);
                    presentFragment(locationActivity);
                }
            } else if (i2 == this.joinRow) {
                getMessagesController().addUserToChat(this.currentChat.id, getUserConfig().getCurrentUser(), 0, (String) null, this, (Runnable) null);
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.closeSearchByActiveAction, new Object[0]);
            } else if (i2 == this.subscribersRow) {
                Bundle bundle2 = new Bundle();
                bundle2.putLong("chat_id", this.chatId);
                bundle2.putInt("type", 2);
                ChatUsersActivity chatUsersActivity = new ChatUsersActivity(bundle2);
                chatUsersActivity.setInfo(this.chatInfo);
                presentFragment(chatUsersActivity);
            } else if (i2 == this.subscribersRequestsRow) {
                presentFragment(new MemberRequestsActivity(this.chatId));
            } else if (i2 == this.administratorsRow) {
                Bundle bundle3 = new Bundle();
                bundle3.putLong("chat_id", this.chatId);
                bundle3.putInt("type", 1);
                ChatUsersActivity chatUsersActivity2 = new ChatUsersActivity(bundle3);
                chatUsersActivity2.setInfo(this.chatInfo);
                presentFragment(chatUsersActivity2);
            } else if (i2 == this.blockedUsersRow) {
                Bundle bundle4 = new Bundle();
                bundle4.putLong("chat_id", this.chatId);
                bundle4.putInt("type", 0);
                ChatUsersActivity chatUsersActivity3 = new ChatUsersActivity(bundle4);
                chatUsersActivity3.setInfo(this.chatInfo);
                presentFragment(chatUsersActivity3);
            } else if (i2 == this.notificationRow) {
                presentFragment(new NotificationsSettingsActivity());
            } else if (i2 == this.privacyRow) {
                presentFragment(new PrivacySettingsActivity());
            } else if (i2 == this.dataRow) {
                presentFragment(new DataSettingsActivity());
            } else if (i2 == this.chatRow) {
                presentFragment(new ThemeActivity(0));
            } else if (i2 == this.filtersRow) {
                presentFragment(new FiltersSetupActivity());
            } else if (i2 == this.stickersRow) {
                presentFragment(new StickersActivity(0));
            } else if (i2 == this.devicesRow) {
                presentFragment(new SessionsActivity(0));
            } else if (i2 == this.questionRow) {
                showDialog(AlertsCreator.createSupportAlert(this, this.resourcesProvider));
            } else if (i2 == this.faqRow) {
                Browser.openUrl((Context) getParentActivity(), LocaleController.getString("TelegramFaqUrl", R.string.TelegramFaqUrl));
            } else if (i2 == this.policyRow) {
                Browser.openUrl((Context) getParentActivity(), LocaleController.getString("PrivacyPolicyUrl", R.string.PrivacyPolicyUrl));
            } else if (i2 == this.sendLogsRow) {
                sendLogs(false);
            } else if (i2 == this.sendLastLogsRow) {
                sendLogs(true);
            } else if (i2 == this.clearLogsRow) {
                FileLog.cleanupLogs();
            } else if (i2 == this.switchBackendRow) {
                if (getParentActivity() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity(), this.resourcesProvider);
                    builder.setMessage(LocaleController.getString("AreYouSure", R.string.AreYouSure));
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new ProfileActivity$$ExternalSyntheticLambda4(this));
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                    showDialog(builder.create());
                }
            } else if (i2 == this.languageRow) {
                presentFragment(new LanguageSelectActivity());
            } else if (i2 == this.setUsernameRow) {
                presentFragment(new ChangeUsernameActivity());
            } else if (i2 == this.bioRow) {
                if (this.userInfo != null) {
                    presentFragment(new ChangeBioActivity());
                }
            } else if (i2 == this.numberRow) {
                presentFragment(new ActionIntroActivity(3));
            } else if (i2 == this.setAvatarRow) {
                onWriteButtonClick();
            } else if (i2 == this.premiumRow) {
                presentFragment(new PremiumPreviewFragment("settings"));
            } else {
                processOnClickOrPress(i2, view2);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$3(DialogInterface dialogInterface, int i) {
        SharedConfig.pushAuthKey = null;
        SharedConfig.pushAuthKeyId = null;
        SharedConfig.saveConfig();
        getConnectionsManager().switchBackend(true);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00a2  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00a9  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$createView$5(android.view.View r8, int r9) {
        /*
            r7 = this;
            if (r9 >= 0) goto L_0x0003
            return
        L_0x0003:
            int r8 = r7.numberRow
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            org.telegram.ui.ProfileActivity$SearchAdapter r0 = r7.searchAdapter
            boolean r0 = r0.searchWas
            r1 = 0
            r2 = 1
            if (r0 == 0) goto L_0x0050
            org.telegram.ui.ProfileActivity$SearchAdapter r0 = r7.searchAdapter
            java.util.ArrayList r0 = r0.searchResults
            int r0 = r0.size()
            if (r9 >= r0) goto L_0x002b
            org.telegram.ui.ProfileActivity$SearchAdapter r8 = r7.searchAdapter
            java.util.ArrayList r8 = r8.searchResults
            java.lang.Object r8 = r8.get(r9)
            goto L_0x009d
        L_0x002b:
            org.telegram.ui.ProfileActivity$SearchAdapter r0 = r7.searchAdapter
            java.util.ArrayList r0 = r0.searchResults
            int r0 = r0.size()
            int r0 = r0 + r2
            int r9 = r9 - r0
            if (r9 < 0) goto L_0x009d
            org.telegram.ui.ProfileActivity$SearchAdapter r0 = r7.searchAdapter
            java.util.ArrayList r0 = r0.faqSearchResults
            int r0 = r0.size()
            if (r9 >= r0) goto L_0x009d
            org.telegram.ui.ProfileActivity$SearchAdapter r8 = r7.searchAdapter
            java.util.ArrayList r8 = r8.faqSearchResults
            java.lang.Object r8 = r8.get(r9)
            goto L_0x009d
        L_0x0050:
            org.telegram.ui.ProfileActivity$SearchAdapter r0 = r7.searchAdapter
            java.util.ArrayList r0 = r0.recentSearches
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x005e
            int r9 = r9 + -1
        L_0x005e:
            if (r9 < 0) goto L_0x0077
            org.telegram.ui.ProfileActivity$SearchAdapter r0 = r7.searchAdapter
            java.util.ArrayList r0 = r0.recentSearches
            int r0 = r0.size()
            if (r9 >= r0) goto L_0x0077
            org.telegram.ui.ProfileActivity$SearchAdapter r8 = r7.searchAdapter
            java.util.ArrayList r8 = r8.recentSearches
            java.lang.Object r8 = r8.get(r9)
            goto L_0x009d
        L_0x0077:
            org.telegram.ui.ProfileActivity$SearchAdapter r0 = r7.searchAdapter
            java.util.ArrayList r0 = r0.recentSearches
            int r0 = r0.size()
            int r0 = r0 + r2
            int r9 = r9 - r0
            if (r9 < 0) goto L_0x009d
            org.telegram.ui.ProfileActivity$SearchAdapter r0 = r7.searchAdapter
            java.util.ArrayList r0 = r0.faqSearchArray
            int r0 = r0.size()
            if (r9 >= r0) goto L_0x009d
            org.telegram.ui.ProfileActivity$SearchAdapter r8 = r7.searchAdapter
            java.util.ArrayList r8 = r8.faqSearchArray
            java.lang.Object r8 = r8.get(r9)
            r9 = 0
            goto L_0x009e
        L_0x009d:
            r9 = 1
        L_0x009e:
            boolean r0 = r8 instanceof org.telegram.ui.ProfileActivity.SearchAdapter.SearchResult
            if (r0 == 0) goto L_0x00a9
            r0 = r8
            org.telegram.ui.ProfileActivity$SearchAdapter$SearchResult r0 = (org.telegram.ui.ProfileActivity.SearchAdapter.SearchResult) r0
            r0.open()
            goto L_0x00ca
        L_0x00a9:
            boolean r0 = r8 instanceof org.telegram.messenger.MessagesController.FaqSearchResult
            if (r0 == 0) goto L_0x00ca
            r0 = r8
            org.telegram.messenger.MessagesController$FaqSearchResult r0 = (org.telegram.messenger.MessagesController.FaqSearchResult) r0
            int r3 = r7.currentAccount
            org.telegram.messenger.NotificationCenter r3 = org.telegram.messenger.NotificationCenter.getInstance(r3)
            int r4 = org.telegram.messenger.NotificationCenter.openArticle
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            org.telegram.ui.ProfileActivity$SearchAdapter r6 = r7.searchAdapter
            org.telegram.tgnet.TLRPC$WebPage r6 = r6.faqWebPage
            r5[r1] = r6
            java.lang.String r0 = r0.url
            r5[r2] = r0
            r3.postNotificationName(r4, r5)
        L_0x00ca:
            if (r9 == 0) goto L_0x00d3
            if (r8 == 0) goto L_0x00d3
            org.telegram.ui.ProfileActivity$SearchAdapter r9 = r7.searchAdapter
            r9.addRecent(r8)
        L_0x00d3:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.lambda$createView$5(android.view.View, int):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createView$7(View view, int i) {
        if (this.searchAdapter.isSearchWas() || this.searchAdapter.recentSearches.isEmpty()) {
            return false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity(), this.resourcesProvider);
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setMessage(LocaleController.getString("ClearSearch", R.string.ClearSearch));
        builder.setPositiveButton(LocaleController.getString("ClearButton", R.string.ClearButton).toUpperCase(), new ProfileActivity$$ExternalSyntheticLambda5(this));
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
        showDialog(builder.create());
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$6(DialogInterface dialogInterface, int i) {
        this.searchAdapter.clearRecent();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$8(TLObject tLObject) {
        this.currentChannelParticipant = ((TLRPC$TL_channels_channelParticipant) tLObject).participant;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$9(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new ProfileActivity$$ExternalSyntheticLambda21(this, tLObject));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$10(TLRPC$Chat tLRPC$Chat, View view) {
        long j = this.userId;
        long j2 = this.banFromGroup;
        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights = tLRPC$Chat.default_banned_rights;
        TLRPC$ChannelParticipant tLRPC$ChannelParticipant = this.currentChannelParticipant;
        ChatRightsEditActivity chatRightsEditActivity = new ChatRightsEditActivity(j, j2, (TLRPC$TL_chatAdminRights) null, tLRPC$TL_chatBannedRights, tLRPC$ChannelParticipant != null ? tLRPC$ChannelParticipant.banned_rights : null, "", 1, true, false, (String) null);
        chatRightsEditActivity.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() {
            public void didSetRights(int i, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, String str) {
                ProfileActivity.this.removeSelfFromStack();
            }

            public void didChangeOwner(TLRPC$User tLRPC$User) {
                ProfileActivity.this.undoView.showWithAction(-ProfileActivity.this.chatId, ProfileActivity.this.currentChat.megagroup ? 10 : 9, (Object) tLRPC$User);
            }
        });
        presentFragment(chatRightsEditActivity);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$11(View view) {
        RecyclerView.ViewHolder findContainingViewHolder;
        Integer num;
        if (this.avatarBig == null) {
            if (!AndroidUtilities.isTablet() && !this.isInLandscapeMode && this.avatarImage.getImageReceiver().hasNotThumb() && !AndroidUtilities.isAccessibilityScreenReaderEnabled()) {
                this.openingAvatar = true;
                this.allowPullingDown = true;
                View view2 = null;
                int i = 0;
                while (true) {
                    if (i >= this.listView.getChildCount()) {
                        break;
                    }
                    RecyclerListView recyclerListView = this.listView;
                    if (recyclerListView.getChildAdapterPosition(recyclerListView.getChildAt(i)) == 0) {
                        view2 = this.listView.getChildAt(i);
                        break;
                    }
                    i++;
                }
                if (!(view2 == null || (findContainingViewHolder = this.listView.findContainingViewHolder(view2)) == null || (num = this.positionToOffset.get(Integer.valueOf(findContainingViewHolder.getAdapterPosition()))) == null)) {
                    this.listView.smoothScrollBy(0, -(num.intValue() + ((this.listView.getPaddingTop() - view2.getTop()) - this.actionBar.getMeasuredHeight())), CubicBezierInterpolator.EASE_OUT_QUINT);
                    return;
                }
            }
            openAvatar();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createView$12(View view) {
        if (this.avatarBig != null) {
            return false;
        }
        openAvatar();
        return false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$13(View view) {
        if (this.writeButton.getTag() == null) {
            onWriteButtonClick();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$14(ValueAnimator valueAnimator) {
        int i;
        int currentActionBarHeight = ActionBar.getCurrentActionBarHeight() + (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0);
        float[] fArr = this.expandAnimatorValues;
        float animatedFraction = valueAnimator.getAnimatedFraction();
        this.currentExpanAnimatorFracture = animatedFraction;
        float lerp = AndroidUtilities.lerp(fArr, animatedFraction);
        this.avatarContainer.setScaleX(this.avatarScale);
        this.avatarContainer.setScaleY(this.avatarScale);
        this.avatarContainer.setTranslationX(AndroidUtilities.lerp(this.avatarX, 0.0f, lerp));
        this.avatarContainer.setTranslationY(AndroidUtilities.lerp((float) Math.ceil((double) this.avatarY), 0.0f, lerp));
        this.avatarImage.setRoundRadius((int) AndroidUtilities.lerp(AndroidUtilities.dpf2(21.0f), 0.0f, lerp));
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        if (actionBarMenuItem != null) {
            float f = 1.0f - lerp;
            actionBarMenuItem.setAlpha(f);
            this.searchItem.setScaleY(f);
            this.searchItem.setVisibility(0);
            ActionBarMenuItem actionBarMenuItem2 = this.searchItem;
            actionBarMenuItem2.setClickable(actionBarMenuItem2.getAlpha() > 0.5f);
            if (this.qrItem != null) {
                float dp = ((float) AndroidUtilities.dp(48.0f)) * lerp;
                this.qrItem.setTranslationX(dp);
                this.avatarsViewPagerIndicatorView.setTranslationX(dp - ((float) AndroidUtilities.dp(48.0f)));
            }
        }
        if (this.extraHeight > ((float) AndroidUtilities.dp(88.0f)) && this.expandProgress < 0.33f) {
            refreshNameAndOnlineXY();
        }
        ScamDrawable scamDrawable2 = this.scamDrawable;
        if (scamDrawable2 != null) {
            scamDrawable2.setColor(ColorUtils.blendARGB(getThemedColor("avatar_subtitleInProfileBlue"), Color.argb(179, 255, 255, 255), lerp));
        }
        Drawable drawable = this.lockIconDrawable;
        if (drawable != null) {
            drawable.setColorFilter(ColorUtils.blendARGB(getThemedColor("chat_lockIcon"), -1, lerp), PorterDuff.Mode.MULTIPLY);
        }
        CrossfadeDrawable crossfadeDrawable = this.verifiedCrossfadeDrawable;
        if (crossfadeDrawable != null) {
            crossfadeDrawable.setProgress(lerp);
        }
        CrossfadeDrawable crossfadeDrawable2 = this.premuimCrossfadeDrawable;
        if (crossfadeDrawable2 != null) {
            crossfadeDrawable2.setProgress(lerp);
        }
        float dpf2 = AndroidUtilities.dpf2(8.0f);
        float dpvar_ = AndroidUtilities.dpf2(16.0f) - ((float) this.nameTextView[1].getLeft());
        float f2 = (float) currentActionBarHeight;
        float dpvar_ = ((this.extraHeight + f2) - AndroidUtilities.dpf2(38.0f)) - ((float) this.nameTextView[1].getBottom());
        float f3 = this.nameX;
        float f4 = this.nameY;
        float f5 = 1.0f - lerp;
        float f6 = f5 * f5;
        float f7 = f5 * 2.0f * lerp;
        float f8 = lerp * lerp;
        float f9 = (f3 * f6) + ((dpf2 + f3 + ((dpvar_ - f3) / 2.0f)) * f7) + (dpvar_ * f8);
        float var_ = (f4 * f6) + ((dpf2 + f4 + ((dpvar_ - f4) / 2.0f)) * f7) + (dpvar_ * f8);
        float dpvar_ = AndroidUtilities.dpf2(16.0f) - ((float) this.onlineTextView[1].getLeft());
        float dpvar_ = ((this.extraHeight + f2) - AndroidUtilities.dpf2(18.0f)) - ((float) this.onlineTextView[1].getBottom());
        float var_ = this.onlineX;
        float var_ = this.onlineY;
        float var_ = (var_ * f6) + ((dpf2 + var_ + ((dpvar_ - var_) / 2.0f)) * f7) + (dpvar_ * f8);
        float var_ = (f6 * var_) + (f7 * (dpf2 + var_ + ((dpvar_ - var_) / 2.0f))) + (f8 * dpvar_);
        this.nameTextView[1].setTranslationX(f9);
        this.nameTextView[1].setTranslationY(var_);
        this.onlineTextView[1].setTranslationX(var_);
        this.onlineTextView[1].setTranslationY(var_);
        this.mediaCounterTextView.setTranslationX(var_);
        this.mediaCounterTextView.setTranslationY(var_);
        Object tag = this.onlineTextView[1].getTag();
        if (tag instanceof String) {
            i = getThemedColor((String) tag);
        } else {
            i = getThemedColor("avatar_subtitleInProfileBlue");
        }
        this.onlineTextView[1].setTextColor(ColorUtils.blendARGB(i, Color.argb(179, 255, 255, 255), lerp));
        if (this.extraHeight > ((float) AndroidUtilities.dp(88.0f))) {
            SimpleTextView[] simpleTextViewArr = this.nameTextView;
            simpleTextViewArr[1].setPivotY((float) AndroidUtilities.lerp(0, simpleTextViewArr[1].getMeasuredHeight(), lerp));
            this.nameTextView[1].setScaleX(AndroidUtilities.lerp(1.12f, 1.67f, lerp));
            this.nameTextView[1].setScaleY(AndroidUtilities.lerp(1.12f, 1.67f, lerp));
        }
        needLayoutText(Math.min(1.0f, this.extraHeight / ((float) AndroidUtilities.dp(88.0f))));
        this.nameTextView[1].setTextColor(ColorUtils.blendARGB(getThemedColor("profile_title"), -1, lerp));
        this.actionBar.setItemsColor(ColorUtils.blendARGB(getThemedColor("actionBarDefaultIcon"), -1, lerp), false);
        this.avatarImage.setForegroundAlpha(lerp);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.avatarContainer.getLayoutParams();
        layoutParams.width = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(42.0f), ((float) this.listView.getMeasuredWidth()) / this.avatarScale, lerp);
        layoutParams.height = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(42.0f), (this.extraHeight + f2) / this.avatarScale, lerp);
        layoutParams.leftMargin = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(64.0f), 0.0f, lerp);
        this.avatarContainer.requestLayout();
    }

    private void updateTtlIcon() {
        TLRPC$UserFull tLRPC$UserFull;
        if (this.ttlIconView != null) {
            boolean z = false;
            if (this.currentEncryptedChat == null && (((tLRPC$UserFull = this.userInfo) != null && tLRPC$UserFull.ttl_period > 0) || (this.chatInfo != null && ChatObject.canUserDoAdminAction(this.currentChat, 13) && this.chatInfo.ttl_period > 0))) {
                z = true;
            }
            AndroidUtilities.updateViewVisibilityAnimated(this.ttlIconView, z, 0.8f, this.fragmentOpened);
        }
    }

    public long getDialogId() {
        long j = this.dialogId;
        if (j != 0) {
            return j;
        }
        long j2 = this.userId;
        if (j2 != 0) {
            return j2;
        }
        return -this.chatId;
    }

    public TLRPC$Chat getCurrentChat() {
        return this.currentChat;
    }

    public TLRPC$UserFull getUserInfo() {
        return this.userInfo;
    }

    public boolean isFragmentOpened() {
        return this.isFragmentOpened;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0051, code lost:
        r0 = getMessagesController().getChat(java.lang.Long.valueOf(r5.chatId));
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void openAvatar() {
        /*
            r5 = this;
            org.telegram.ui.Components.RecyclerListView r0 = r5.listView
            int r0 = r0.getScrollState()
            r1 = 1
            if (r0 != r1) goto L_0x000a
            return
        L_0x000a:
            long r0 = r5.userId
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 == 0) goto L_0x004b
            org.telegram.messenger.MessagesController r0 = r5.getMessagesController()
            long r1 = r5.userId
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r1)
            org.telegram.tgnet.TLRPC$UserProfilePhoto r1 = r0.photo
            if (r1 == 0) goto L_0x00b2
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_big
            if (r1 == 0) goto L_0x00b2
            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.getInstance()
            android.app.Activity r2 = r5.getParentActivity()
            r1.setParentActivity(r2)
            org.telegram.tgnet.TLRPC$UserProfilePhoto r1 = r0.photo
            int r2 = r1.dc_id
            if (r2 == 0) goto L_0x003d
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_big
            r1.dc_id = r2
        L_0x003d:
            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.getInstance()
            org.telegram.tgnet.TLRPC$UserProfilePhoto r0 = r0.photo
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.photo_big
            org.telegram.ui.PhotoViewer$PhotoViewerProvider r2 = r5.provider
            r1.openPhoto(r0, r2)
            goto L_0x00b2
        L_0x004b:
            long r0 = r5.chatId
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 == 0) goto L_0x00b2
            org.telegram.messenger.MessagesController r0 = r5.getMessagesController()
            long r1 = r5.chatId
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r1)
            org.telegram.tgnet.TLRPC$ChatPhoto r1 = r0.photo
            if (r1 == 0) goto L_0x00b2
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_big
            if (r1 == 0) goto L_0x00b2
            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.getInstance()
            android.app.Activity r2 = r5.getParentActivity()
            r1.setParentActivity(r2)
            org.telegram.tgnet.TLRPC$ChatPhoto r1 = r0.photo
            int r2 = r1.dc_id
            if (r2 == 0) goto L_0x007c
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_big
            r1.dc_id = r2
        L_0x007c:
            org.telegram.tgnet.TLRPC$ChatFull r1 = r5.chatInfo
            if (r1 == 0) goto L_0x00a4
            org.telegram.tgnet.TLRPC$Photo r1 = r1.chat_photo
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_photo
            if (r2 == 0) goto L_0x00a4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$VideoSize> r1 = r1.video_sizes
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x00a4
            org.telegram.tgnet.TLRPC$ChatFull r1 = r5.chatInfo
            org.telegram.tgnet.TLRPC$Photo r1 = r1.chat_photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$VideoSize> r1 = r1.video_sizes
            r2 = 0
            java.lang.Object r1 = r1.get(r2)
            org.telegram.tgnet.TLRPC$VideoSize r1 = (org.telegram.tgnet.TLRPC$VideoSize) r1
            org.telegram.tgnet.TLRPC$ChatFull r2 = r5.chatInfo
            org.telegram.tgnet.TLRPC$Photo r2 = r2.chat_photo
            org.telegram.messenger.ImageLocation r1 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$VideoSize) r1, (org.telegram.tgnet.TLRPC$Photo) r2)
            goto L_0x00a5
        L_0x00a4:
            r1 = 0
        L_0x00a5:
            org.telegram.ui.PhotoViewer r2 = org.telegram.ui.PhotoViewer.getInstance()
            org.telegram.tgnet.TLRPC$ChatPhoto r0 = r0.photo
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.photo_big
            org.telegram.ui.PhotoViewer$PhotoViewerProvider r3 = r5.provider
            r2.openPhotoWithVideo(r0, r1, r3)
        L_0x00b2:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.openAvatar():void");
    }

    /* access modifiers changed from: private */
    public void onWriteButtonClick() {
        if (this.userId != 0) {
            boolean z = true;
            if (this.imageUpdater != null) {
                TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
                if (user == null) {
                    user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
                }
                if (user != null) {
                    ImageUpdater imageUpdater2 = this.imageUpdater;
                    TLRPC$UserProfilePhoto tLRPC$UserProfilePhoto = user.photo;
                    if (tLRPC$UserProfilePhoto == null || tLRPC$UserProfilePhoto.photo_big == null || (tLRPC$UserProfilePhoto instanceof TLRPC$TL_userProfilePhotoEmpty)) {
                        z = false;
                    }
                    imageUpdater2.openMenu(z, new ProfileActivity$$ExternalSyntheticLambda19(this), new ProfileActivity$$ExternalSyntheticLambda10(this));
                    this.cameraDrawable.setCurrentFrame(0);
                    this.cameraDrawable.setCustomEndFrame(43);
                    this.cellCameraDrawable.setCurrentFrame(0);
                    this.cellCameraDrawable.setCustomEndFrame(43);
                    this.writeButton.playAnimation();
                    TextCell textCell = this.setAvatarCell;
                    if (textCell != null) {
                        textCell.getImageView().playAnimation();
                        return;
                    }
                    return;
                }
                return;
            }
            if (this.playProfileAnimation != 0) {
                ArrayList<BaseFragment> arrayList = this.parentLayout.fragmentsStack;
                if (arrayList.get(arrayList.size() - 2) instanceof ChatActivity) {
                    finishFragment();
                    return;
                }
            }
            TLRPC$User user2 = getMessagesController().getUser(Long.valueOf(this.userId));
            if (user2 != null && !(user2 instanceof TLRPC$TL_userEmpty)) {
                Bundle bundle = new Bundle();
                bundle.putLong("user_id", this.userId);
                if (getMessagesController().checkCanOpenChat(bundle, this)) {
                    boolean z2 = this.arguments.getBoolean("removeFragmentOnChatOpen", true);
                    if (!AndroidUtilities.isTablet() && z2) {
                        NotificationCenter notificationCenter = getNotificationCenter();
                        int i = NotificationCenter.closeChats;
                        notificationCenter.removeObserver(this, i);
                        getNotificationCenter().postNotificationName(i, new Object[0]);
                    }
                    int i2 = getArguments().getInt("nearby_distance", -1);
                    if (i2 >= 0) {
                        bundle.putInt("nearby_distance", i2);
                    }
                    ChatActivity chatActivity = new ChatActivity(bundle);
                    chatActivity.setPreloadedSticker(getMediaDataController().getGreetingsSticker(), false);
                    presentFragment(chatActivity, z2);
                    if (AndroidUtilities.isTablet()) {
                        finishFragment();
                        return;
                    }
                    return;
                }
                return;
            }
            return;
        }
        openDiscussion();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onWriteButtonClick$15() {
        MessagesController.getInstance(this.currentAccount).deleteUserPhoto((TLRPC$InputPhoto) null);
        this.cameraDrawable.setCurrentFrame(0);
        this.cellCameraDrawable.setCurrentFrame(0);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onWriteButtonClick$16(DialogInterface dialogInterface) {
        if (!this.imageUpdater.isUploadingImage()) {
            this.cameraDrawable.setCustomEndFrame(86);
            this.cellCameraDrawable.setCustomEndFrame(86);
            this.writeButton.playAnimation();
            TextCell textCell = this.setAvatarCell;
            if (textCell != null) {
                textCell.getImageView().playAnimation();
                return;
            }
            return;
        }
        this.cameraDrawable.setCurrentFrame(0, false);
        this.cellCameraDrawable.setCurrentFrame(0, false);
    }

    /* access modifiers changed from: private */
    public void openDiscussion() {
        TLRPC$ChatFull tLRPC$ChatFull = this.chatInfo;
        if (tLRPC$ChatFull != null && tLRPC$ChatFull.linked_chat_id != 0) {
            Bundle bundle = new Bundle();
            bundle.putLong("chat_id", this.chatInfo.linked_chat_id);
            if (getMessagesController().checkCanOpenChat(bundle, this)) {
                presentFragment(new ChatActivity(bundle));
            }
        }
    }

    public boolean onMemberClick(TLRPC$ChatParticipant tLRPC$ChatParticipant, boolean z) {
        return onMemberClick(tLRPC$ChatParticipant, z, false);
    }

    public boolean onMemberClick(TLRPC$ChatParticipant tLRPC$ChatParticipant, boolean z, boolean z2) {
        boolean z3;
        boolean z4;
        TLRPC$ChannelParticipant tLRPC$ChannelParticipant;
        boolean z5;
        boolean z6;
        ArrayList arrayList;
        ArrayList arrayList2;
        boolean z7;
        String str;
        int i;
        TLRPC$ChatParticipant tLRPC$ChatParticipant2 = tLRPC$ChatParticipant;
        if (getParentActivity() == null) {
            return false;
        }
        if (z) {
            TLRPC$User user = getMessagesController().getUser(Long.valueOf(tLRPC$ChatParticipant2.user_id));
            if (user == null || tLRPC$ChatParticipant2.user_id == getUserConfig().getClientUserId()) {
                return false;
            }
            this.selectedUser = tLRPC$ChatParticipant2.user_id;
            ArrayList arrayList3 = null;
            if (ChatObject.isChannel(this.currentChat)) {
                TLRPC$ChannelParticipant tLRPC$ChannelParticipant2 = ((TLRPC$TL_chatChannelParticipant) tLRPC$ChatParticipant2).channelParticipant;
                getMessagesController().getUser(Long.valueOf(tLRPC$ChatParticipant2.user_id));
                z5 = ChatObject.canAddAdmins(this.currentChat);
                if (z5 && ((tLRPC$ChannelParticipant2 instanceof TLRPC$TL_channelParticipantCreator) || ((tLRPC$ChannelParticipant2 instanceof TLRPC$TL_channelParticipantAdmin) && !tLRPC$ChannelParticipant2.can_edit))) {
                    z5 = false;
                }
                boolean z8 = ChatObject.canBlockUsers(this.currentChat) && ((!(tLRPC$ChannelParticipant2 instanceof TLRPC$TL_channelParticipantAdmin) && !(tLRPC$ChannelParticipant2 instanceof TLRPC$TL_channelParticipantCreator)) || tLRPC$ChannelParticipant2.can_edit);
                z4 = this.currentChat.gigagroup ? false : z8;
                z3 = tLRPC$ChannelParticipant2 instanceof TLRPC$TL_channelParticipantAdmin;
                boolean z9 = z8;
                tLRPC$ChannelParticipant = tLRPC$ChannelParticipant2;
                z6 = z9;
            } else {
                TLRPC$Chat tLRPC$Chat = this.currentChat;
                boolean z10 = tLRPC$Chat.creator || ((tLRPC$ChatParticipant2 instanceof TLRPC$TL_chatParticipant) && (ChatObject.canBlockUsers(tLRPC$Chat) || tLRPC$ChatParticipant2.inviter_id == getUserConfig().getClientUserId()));
                z5 = this.currentChat.creator;
                z3 = tLRPC$ChatParticipant2 instanceof TLRPC$TL_chatParticipantAdmin;
                z4 = z5;
                z6 = z10;
                tLRPC$ChannelParticipant = null;
            }
            if (z2) {
                arrayList = null;
            } else {
                arrayList = new ArrayList();
            }
            if (z2) {
                arrayList2 = null;
            } else {
                arrayList2 = new ArrayList();
            }
            if (!z2) {
                arrayList3 = new ArrayList();
            }
            if (z5) {
                if (z2) {
                    return true;
                }
                if (z3) {
                    i = R.string.EditAdminRights;
                    str = "EditAdminRights";
                } else {
                    i = R.string.SetAsAdmin;
                    str = "SetAsAdmin";
                }
                arrayList.add(LocaleController.getString(str, i));
                arrayList2.add(Integer.valueOf(R.drawable.msg_admins));
                arrayList3.add(0);
            }
            if (z4) {
                if (z2) {
                    return true;
                }
                arrayList.add(LocaleController.getString("ChangePermissions", R.string.ChangePermissions));
                arrayList2.add(Integer.valueOf(R.drawable.msg_permissions));
                arrayList3.add(1);
            }
            if (!z6) {
                z7 = false;
            } else if (z2) {
                return true;
            } else {
                arrayList.add(LocaleController.getString("KickFromGroup", R.string.KickFromGroup));
                arrayList2.add(Integer.valueOf(R.drawable.msg_remove));
                arrayList3.add(2);
                z7 = true;
            }
            if (z2 || arrayList.isEmpty()) {
                return false;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity(), this.resourcesProvider);
            builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[0]), AndroidUtilities.toIntArray(arrayList2), new ProfileActivity$$ExternalSyntheticLambda7(this, arrayList3, tLRPC$ChatParticipant, tLRPC$ChannelParticipant, user, z3));
            AlertDialog create = builder.create();
            showDialog(create);
            if (z7) {
                create.setItemColor(arrayList.size() - 1, getThemedColor("dialogTextRed2"), getThemedColor("dialogRedIcon"));
            }
        } else if (tLRPC$ChatParticipant2.user_id == getUserConfig().getClientUserId()) {
            return false;
        } else {
            Bundle bundle = new Bundle();
            bundle.putLong("user_id", tLRPC$ChatParticipant2.user_id);
            bundle.putBoolean("preload_messages", true);
            presentFragment(new ProfileActivity(bundle));
        }
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onMemberClick$18(ArrayList arrayList, TLRPC$ChatParticipant tLRPC$ChatParticipant, TLRPC$ChannelParticipant tLRPC$ChannelParticipant, TLRPC$User tLRPC$User, boolean z, DialogInterface dialogInterface, int i) {
        if (((Integer) arrayList.get(i)).intValue() == 2) {
            kickUser(this.selectedUser, tLRPC$ChatParticipant);
            return;
        }
        int intValue = ((Integer) arrayList.get(i)).intValue();
        if (intValue == 1 && ((tLRPC$ChannelParticipant instanceof TLRPC$TL_channelParticipantAdmin) || (tLRPC$ChatParticipant instanceof TLRPC$TL_chatParticipantAdmin))) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity(), this.resourcesProvider);
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setMessage(LocaleController.formatString("AdminWillBeRemoved", R.string.AdminWillBeRemoved, ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name)));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new ProfileActivity$$ExternalSyntheticLambda9(this, tLRPC$ChannelParticipant, intValue, tLRPC$User, tLRPC$ChatParticipant, z));
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        } else if (tLRPC$ChannelParticipant != null) {
            openRightsEdit(intValue, tLRPC$User, tLRPC$ChatParticipant, tLRPC$ChannelParticipant.admin_rights, tLRPC$ChannelParticipant.banned_rights, tLRPC$ChannelParticipant.rank, z);
        } else {
            openRightsEdit(intValue, tLRPC$User, tLRPC$ChatParticipant, (TLRPC$TL_chatAdminRights) null, (TLRPC$TL_chatBannedRights) null, "", z);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onMemberClick$17(TLRPC$ChannelParticipant tLRPC$ChannelParticipant, int i, TLRPC$User tLRPC$User, TLRPC$ChatParticipant tLRPC$ChatParticipant, boolean z, DialogInterface dialogInterface, int i2) {
        TLRPC$ChannelParticipant tLRPC$ChannelParticipant2 = tLRPC$ChannelParticipant;
        if (tLRPC$ChannelParticipant2 != null) {
            openRightsEdit(i, tLRPC$User, tLRPC$ChatParticipant, tLRPC$ChannelParticipant2.admin_rights, tLRPC$ChannelParticipant2.banned_rights, tLRPC$ChannelParticipant2.rank, z);
            return;
        }
        openRightsEdit(i, tLRPC$User, tLRPC$ChatParticipant, (TLRPC$TL_chatAdminRights) null, (TLRPC$TL_chatBannedRights) null, "", z);
    }

    private void openRightsEdit(int i, TLRPC$User tLRPC$User, TLRPC$ChatParticipant tLRPC$ChatParticipant, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, String str, boolean z) {
        final boolean[] zArr = new boolean[1];
        AnonymousClass26 r18 = r0;
        final TLRPC$User tLRPC$User2 = tLRPC$User;
        AnonymousClass26 r0 = new ChatRightsEditActivity(this, tLRPC$User.id, this.chatId, tLRPC$TL_chatAdminRights, this.currentChat.default_banned_rights, tLRPC$TL_chatBannedRights, str, i, true, false, (String) null) {
            final /* synthetic */ ProfileActivity this$0;

            {
                this.this$0 = r15;
            }

            /* access modifiers changed from: protected */
            public void onTransitionAnimationEnd(boolean z, boolean z2) {
                if (!z && z2 && zArr[0] && BulletinFactory.canShowBulletin(this.this$0)) {
                    BulletinFactory.createPromoteToAdminBulletin(this.this$0, tLRPC$User2.first_name).show();
                }
            }
        };
        final int i2 = i;
        final TLRPC$ChatParticipant tLRPC$ChatParticipant2 = tLRPC$ChatParticipant;
        final boolean z2 = z;
        final boolean[] zArr2 = zArr;
        AnonymousClass26 r02 = r18;
        r02.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() {
            public void didSetRights(int i, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, String str) {
                boolean z;
                TLRPC$ChatParticipant tLRPC$ChatParticipant;
                int i2 = i2;
                int i3 = 0;
                if (i2 == 0) {
                    TLRPC$ChatParticipant tLRPC$ChatParticipant2 = tLRPC$ChatParticipant2;
                    if (tLRPC$ChatParticipant2 instanceof TLRPC$TL_chatChannelParticipant) {
                        TLRPC$TL_chatChannelParticipant tLRPC$TL_chatChannelParticipant = (TLRPC$TL_chatChannelParticipant) tLRPC$ChatParticipant2;
                        if (i == 1) {
                            TLRPC$TL_channelParticipantAdmin tLRPC$TL_channelParticipantAdmin = new TLRPC$TL_channelParticipantAdmin();
                            tLRPC$TL_chatChannelParticipant.channelParticipant = tLRPC$TL_channelParticipantAdmin;
                            tLRPC$TL_channelParticipantAdmin.flags |= 4;
                        } else {
                            tLRPC$TL_chatChannelParticipant.channelParticipant = new TLRPC$TL_channelParticipant();
                        }
                        tLRPC$TL_chatChannelParticipant.channelParticipant.inviter_id = ProfileActivity.this.getUserConfig().getClientUserId();
                        tLRPC$TL_chatChannelParticipant.channelParticipant.peer = new TLRPC$TL_peerUser();
                        TLRPC$ChannelParticipant tLRPC$ChannelParticipant = tLRPC$TL_chatChannelParticipant.channelParticipant;
                        TLRPC$Peer tLRPC$Peer = tLRPC$ChannelParticipant.peer;
                        TLRPC$ChatParticipant tLRPC$ChatParticipant3 = tLRPC$ChatParticipant2;
                        tLRPC$Peer.user_id = tLRPC$ChatParticipant3.user_id;
                        tLRPC$ChannelParticipant.date = tLRPC$ChatParticipant3.date;
                        tLRPC$ChannelParticipant.banned_rights = tLRPC$TL_chatBannedRights;
                        tLRPC$ChannelParticipant.admin_rights = tLRPC$TL_chatAdminRights;
                        tLRPC$ChannelParticipant.rank = str;
                    } else if (tLRPC$ChatParticipant2 != null) {
                        if (i == 1) {
                            tLRPC$ChatParticipant = new TLRPC$TL_chatParticipantAdmin();
                        } else {
                            tLRPC$ChatParticipant = new TLRPC$TL_chatParticipant();
                        }
                        TLRPC$ChatParticipant tLRPC$ChatParticipant4 = tLRPC$ChatParticipant2;
                        tLRPC$ChatParticipant.user_id = tLRPC$ChatParticipant4.user_id;
                        tLRPC$ChatParticipant.date = tLRPC$ChatParticipant4.date;
                        tLRPC$ChatParticipant.inviter_id = tLRPC$ChatParticipant4.inviter_id;
                        int indexOf = ProfileActivity.this.chatInfo.participants.participants.indexOf(tLRPC$ChatParticipant2);
                        if (indexOf >= 0) {
                            ProfileActivity.this.chatInfo.participants.participants.set(indexOf, tLRPC$ChatParticipant);
                        }
                    }
                    if (i == 1 && !z2) {
                        zArr2[0] = true;
                    }
                } else if (i2 == 1 && i == 0 && ProfileActivity.this.currentChat.megagroup && ProfileActivity.this.chatInfo != null && ProfileActivity.this.chatInfo.participants != null) {
                    int i4 = 0;
                    while (true) {
                        if (i4 >= ProfileActivity.this.chatInfo.participants.participants.size()) {
                            z = false;
                            break;
                        } else if (MessageObject.getPeerId(((TLRPC$TL_chatChannelParticipant) ProfileActivity.this.chatInfo.participants.participants.get(i4)).channelParticipant.peer) == tLRPC$ChatParticipant2.user_id) {
                            ProfileActivity.this.chatInfo.participants_count--;
                            ProfileActivity.this.chatInfo.participants.participants.remove(i4);
                            z = true;
                            break;
                        } else {
                            i4++;
                        }
                    }
                    if (ProfileActivity.this.chatInfo != null && ProfileActivity.this.chatInfo.participants != null) {
                        while (true) {
                            if (i3 >= ProfileActivity.this.chatInfo.participants.participants.size()) {
                                break;
                            } else if (ProfileActivity.this.chatInfo.participants.participants.get(i3).user_id == tLRPC$ChatParticipant2.user_id) {
                                ProfileActivity.this.chatInfo.participants.participants.remove(i3);
                                z = true;
                                break;
                            } else {
                                i3++;
                            }
                        }
                    }
                    if (z) {
                        ProfileActivity.this.updateOnlineCount(true);
                        ProfileActivity.this.updateRowsIds();
                        ProfileActivity.this.listAdapter.notifyDataSetChanged();
                    }
                }
            }

            public void didChangeOwner(TLRPC$User tLRPC$User) {
                ProfileActivity.this.undoView.showWithAction(-ProfileActivity.this.chatId, ProfileActivity.this.currentChat.megagroup ? 10 : 9, (Object) tLRPC$User);
            }
        });
        presentFragment(r02);
    }

    /* access modifiers changed from: private */
    public boolean processOnClickOrPress(int i, View view) {
        String str;
        TLRPC$Chat chat;
        String str2;
        TLRPC$UserFull tLRPC$UserFull;
        int i2 = i;
        View view2 = view;
        if (i2 == this.usernameRow || i2 == this.setUsernameRow) {
            if (this.userId != 0) {
                TLRPC$User user = getMessagesController().getUser(Long.valueOf(this.userId));
                if (user == null || (str = user.username) == null) {
                    return false;
                }
            } else if (this.chatId == 0 || (chat = getMessagesController().getChat(Long.valueOf(this.chatId))) == null || (str = chat.username) == null) {
                return false;
            }
            if (this.userId == 0) {
                String str3 = "https://" + MessagesController.getInstance(UserConfig.selectedAccount).linkPrefix + "/" + str;
                showDialog(new ShareAlert(getParentActivity(), (ArrayList) null, str3, false, str3, false) {
                    /* access modifiers changed from: protected */
                    public void onSend(LongSparseArray<TLRPC$Dialog> longSparseArray, int i) {
                        AndroidUtilities.runOnUIThread(new ProfileActivity$28$$ExternalSyntheticLambda0(this, longSparseArray, i), 250);
                    }

                    /* access modifiers changed from: private */
                    public /* synthetic */ void lambda$onSend$0(LongSparseArray longSparseArray, int i) {
                        BulletinFactory.createInviteSentBulletin(ProfileActivity.this.getParentActivity(), ProfileActivity.this.contentView, longSparseArray.size(), longSparseArray.size() == 1 ? ((TLRPC$Dialog) longSparseArray.valueAt(0)).id : 0, i, getThemedColor("undo_background"), getThemedColor("undo_infoColor")).show();
                    }
                });
            } else {
                try {
                    BulletinFactory.of(this).createCopyBulletin(LocaleController.getString("UsernameCopied", R.string.UsernameCopied), this.resourcesProvider).show();
                    ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", "@" + str));
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            return true;
        } else if (i2 == this.phoneRow || i2 == this.numberRow) {
            TLRPC$User user2 = getMessagesController().getUser(Long.valueOf(this.userId));
            if (user2 == null || (str2 = user2.phone) == null || str2.length() == 0 || getParentActivity() == null) {
                return false;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity(), this.resourcesProvider);
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            if (i2 == this.phoneRow) {
                TLRPC$UserFull tLRPC$UserFull2 = this.userInfo;
                if (tLRPC$UserFull2 != null && tLRPC$UserFull2.phone_calls_available) {
                    arrayList.add(LocaleController.getString("CallViaTelegram", R.string.CallViaTelegram));
                    arrayList2.add(2);
                    if (Build.VERSION.SDK_INT >= 18 && this.userInfo.video_calls_available) {
                        arrayList.add(LocaleController.getString("VideoCallViaTelegram", R.string.VideoCallViaTelegram));
                        arrayList2.add(3);
                    }
                }
                arrayList.add(LocaleController.getString("Call", R.string.Call));
                arrayList2.add(0);
            }
            arrayList.add(LocaleController.getString("Copy", R.string.Copy));
            arrayList2.add(1);
            builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[0]), new ProfileActivity$$ExternalSyntheticLambda8(this, arrayList2, user2));
            showDialog(builder.create());
            return true;
        } else if (i2 != this.channelInfoRow && i2 != this.userInfoRow && i2 != this.locationRow && i2 != this.bioRow) {
            return false;
        } else {
            if (i2 == this.bioRow && ((tLRPC$UserFull = this.userInfo) == null || TextUtils.isEmpty(tLRPC$UserFull.about))) {
                return false;
            }
            boolean z = view2 instanceof AboutLinkCell;
            if (z && ((AboutLinkCell) view2).onClick()) {
                return false;
            }
            String str4 = null;
            if (i2 == this.locationRow) {
                TLRPC$ChatFull tLRPC$ChatFull = this.chatInfo;
                if (tLRPC$ChatFull != null) {
                    TLRPC$ChannelLocation tLRPC$ChannelLocation = tLRPC$ChatFull.location;
                    if (tLRPC$ChannelLocation instanceof TLRPC$TL_channelLocation) {
                        str4 = ((TLRPC$TL_channelLocation) tLRPC$ChannelLocation).address;
                    }
                }
            } else if (i2 == this.channelInfoRow) {
                TLRPC$ChatFull tLRPC$ChatFull2 = this.chatInfo;
                if (tLRPC$ChatFull2 != null) {
                    str4 = tLRPC$ChatFull2.about;
                }
            } else {
                TLRPC$UserFull tLRPC$UserFull3 = this.userInfo;
                if (tLRPC$UserFull3 != null) {
                    str4 = tLRPC$UserFull3.about;
                }
            }
            String str5 = str4;
            if (TextUtils.isEmpty(str5)) {
                return false;
            }
            String[] strArr = {"und"};
            boolean z2 = MessagesController.getGlobalMainSettings().getBoolean("translate_button", false);
            boolean[] zArr = new boolean[1];
            zArr[0] = i2 == this.bioRow || i2 == this.channelInfoRow || i2 == this.userInfoRow;
            String language = LocaleController.getInstance().getCurrentLocale().getLanguage();
            ProfileActivity$$ExternalSyntheticLambda28 profileActivity$$ExternalSyntheticLambda28 = r1;
            ProfileActivity$$ExternalSyntheticLambda28 profileActivity$$ExternalSyntheticLambda282 = new ProfileActivity$$ExternalSyntheticLambda28(this, zArr, str5, i, strArr, language);
            if (!zArr[0]) {
                profileActivity$$ExternalSyntheticLambda28.run();
            } else if (LanguageDetector.hasSupport()) {
                LanguageDetector.detectLanguage(str5, new ProfileActivity$$ExternalSyntheticLambda31(this, strArr, zArr, language, z2, profileActivity$$ExternalSyntheticLambda28), new ProfileActivity$$ExternalSyntheticLambda30(profileActivity$$ExternalSyntheticLambda28));
            } else {
                profileActivity$$ExternalSyntheticLambda28.run();
            }
            return !z;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processOnClickOrPress$19(ArrayList arrayList, TLRPC$User tLRPC$User, DialogInterface dialogInterface, int i) {
        int intValue = ((Integer) arrayList.get(i)).intValue();
        if (intValue == 0) {
            try {
                Intent intent = new Intent("android.intent.action.DIAL", Uri.parse("tel:+" + tLRPC$User.phone));
                intent.addFlags(NUM);
                getParentActivity().startActivityForResult(intent, 500);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else if (intValue == 1) {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", "+" + tLRPC$User.phone));
                if (Build.VERSION.SDK_INT < 31) {
                    BulletinFactory.of(this).createCopyBulletin(LocaleController.getString("PhoneCopied", R.string.PhoneCopied)).show();
                }
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        } else if (intValue == 2 || intValue == 3) {
            boolean z = intValue == 3;
            TLRPC$UserFull tLRPC$UserFull = this.userInfo;
            VoIPHelper.startCall(tLRPC$User, z, tLRPC$UserFull != null && tLRPC$UserFull.video_calls_available, getParentActivity(), this.userInfo, getAccountInstance());
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processOnClickOrPress$22(boolean[] zArr, String str, int i, String[] strArr, String str2) {
        if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity(), this.resourcesProvider);
            builder.setItems(zArr[0] ? new CharSequence[]{LocaleController.getString("Copy", R.string.Copy), LocaleController.getString("TranslateMessage", R.string.TranslateMessage)} : new CharSequence[]{LocaleController.getString("Copy", R.string.Copy)}, new ProfileActivity$$ExternalSyntheticLambda6(this, str, i, strArr, str2));
            showDialog(builder.create());
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processOnClickOrPress$21(String str, int i, String[] strArr, String str2, DialogInterface dialogInterface, int i2) {
        if (i2 == 0) {
            try {
                AndroidUtilities.addToClipboard(str);
                if (i == this.bioRow) {
                    BulletinFactory.of(this).createCopyBulletin(LocaleController.getString("BioCopied", R.string.BioCopied)).show();
                } else {
                    BulletinFactory.of(this).createCopyBulletin(LocaleController.getString("TextCopied", R.string.TextCopied)).show();
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else if (i2 == 1) {
            TranslateAlert.showAlert(this.fragmentView.getContext(), this, strArr[0], str2, str, false, new ProfileActivity$$ExternalSyntheticLambda40(this), (Runnable) null);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$processOnClickOrPress$20(URLSpan uRLSpan) {
        if (uRLSpan == null) {
            return false;
        }
        openUrl(uRLSpan.getURL());
        return true;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x001f, code lost:
        r2 = r1.currentChat;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$processOnClickOrPress$23(java.lang.String[] r2, boolean[] r3, java.lang.String r4, boolean r5, java.lang.Runnable r6, java.lang.String r7) {
        /*
            r1 = this;
            r0 = 0
            r2[r0] = r7
            if (r7 == 0) goto L_0x003d
            boolean r2 = r7.equals(r4)
            if (r2 == 0) goto L_0x0013
            java.lang.String r2 = "und"
            boolean r2 = r7.equals(r2)
            if (r2 == 0) goto L_0x003d
        L_0x0013:
            if (r5 == 0) goto L_0x001f
            java.util.HashSet r2 = org.telegram.ui.RestrictedLanguagesSelectActivity.getRestrictedLanguages()
            boolean r2 = r2.contains(r7)
            if (r2 == 0) goto L_0x003b
        L_0x001f:
            org.telegram.tgnet.TLRPC$Chat r2 = r1.currentChat
            if (r2 == 0) goto L_0x003d
            boolean r4 = r2.has_link
            if (r4 != 0) goto L_0x002b
            java.lang.String r2 = r2.username
            if (r2 == 0) goto L_0x003d
        L_0x002b:
            java.lang.String r2 = "uk"
            boolean r2 = r2.equals(r7)
            if (r2 != 0) goto L_0x003b
            java.lang.String r2 = "ru"
            boolean r2 = r2.equals(r7)
            if (r2 == 0) goto L_0x003d
        L_0x003b:
            r2 = 1
            goto L_0x003e
        L_0x003d:
            r2 = 0
        L_0x003e:
            r3[r0] = r2
            r6.run()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.lambda$processOnClickOrPress$23(java.lang.String[], boolean[], java.lang.String, boolean, java.lang.Runnable, java.lang.String):void");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$processOnClickOrPress$24(Runnable runnable, Exception exc) {
        FileLog.e("mlkit: failed to detect language in selection", (Throwable) exc);
        runnable.run();
    }

    /* access modifiers changed from: private */
    public void leaveChatPressed() {
        AlertsCreator.createClearOrDeleteDialogAlert(this, false, this.currentChat, (TLRPC$User) null, false, false, true, new ProfileActivity$$ExternalSyntheticLambda32(this), this.resourcesProvider);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$leaveChatPressed$25(boolean z) {
        this.playProfileAnimation = 0;
        NotificationCenter notificationCenter = getNotificationCenter();
        int i = NotificationCenter.closeChats;
        notificationCenter.removeObserver(this, i);
        getNotificationCenter().postNotificationName(i, new Object[0]);
        finishFragment();
        getNotificationCenter().postNotificationName(NotificationCenter.needDeleteDialog, Long.valueOf(-this.currentChat.id), null, this.currentChat, Boolean.valueOf(z));
    }

    /* access modifiers changed from: private */
    public void getChannelParticipants(boolean z) {
        LongSparseArray<TLRPC$ChatParticipant> longSparseArray;
        if (!this.loadingUsers && (longSparseArray = this.participantsMap) != null && this.chatInfo != null) {
            this.loadingUsers = true;
            int i = 0;
            int i2 = (longSparseArray.size() == 0 || !z) ? 0 : 300;
            TLRPC$TL_channels_getParticipants tLRPC$TL_channels_getParticipants = new TLRPC$TL_channels_getParticipants();
            tLRPC$TL_channels_getParticipants.channel = getMessagesController().getInputChannel(this.chatId);
            tLRPC$TL_channels_getParticipants.filter = new TLRPC$TL_channelParticipantsRecent();
            if (!z) {
                i = this.participantsMap.size();
            }
            tLRPC$TL_channels_getParticipants.offset = i;
            tLRPC$TL_channels_getParticipants.limit = 200;
            getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tLRPC$TL_channels_getParticipants, new ProfileActivity$$ExternalSyntheticLambda35(this, tLRPC$TL_channels_getParticipants, i2)), this.classGuid);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getChannelParticipants$27(TLRPC$TL_channels_getParticipants tLRPC$TL_channels_getParticipants, int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new ProfileActivity$$ExternalSyntheticLambda24(this, tLRPC$TL_error, tLObject, tLRPC$TL_channels_getParticipants), (long) i);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getChannelParticipants$26(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, TLRPC$TL_channels_getParticipants tLRPC$TL_channels_getParticipants) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_channels_channelParticipants tLRPC$TL_channels_channelParticipants = (TLRPC$TL_channels_channelParticipants) tLObject;
            getMessagesController().putUsers(tLRPC$TL_channels_channelParticipants.users, false);
            getMessagesController().putChats(tLRPC$TL_channels_channelParticipants.chats, false);
            if (tLRPC$TL_channels_channelParticipants.users.size() < 200) {
                this.usersEndReached = true;
            }
            if (tLRPC$TL_channels_getParticipants.offset == 0) {
                this.participantsMap.clear();
                this.chatInfo.participants = new TLRPC$TL_chatParticipants();
                getMessagesStorage().putUsersAndChats(tLRPC$TL_channels_channelParticipants.users, tLRPC$TL_channels_channelParticipants.chats, true, true);
                getMessagesStorage().updateChannelUsers(this.chatId, tLRPC$TL_channels_channelParticipants.participants);
            }
            for (int i = 0; i < tLRPC$TL_channels_channelParticipants.participants.size(); i++) {
                TLRPC$TL_chatChannelParticipant tLRPC$TL_chatChannelParticipant = new TLRPC$TL_chatChannelParticipant();
                TLRPC$ChannelParticipant tLRPC$ChannelParticipant = tLRPC$TL_channels_channelParticipants.participants.get(i);
                tLRPC$TL_chatChannelParticipant.channelParticipant = tLRPC$ChannelParticipant;
                tLRPC$TL_chatChannelParticipant.inviter_id = tLRPC$ChannelParticipant.inviter_id;
                long peerId = MessageObject.getPeerId(tLRPC$ChannelParticipant.peer);
                tLRPC$TL_chatChannelParticipant.user_id = peerId;
                tLRPC$TL_chatChannelParticipant.date = tLRPC$TL_chatChannelParticipant.channelParticipant.date;
                if (this.participantsMap.indexOfKey(peerId) < 0) {
                    TLRPC$ChatFull tLRPC$ChatFull = this.chatInfo;
                    if (tLRPC$ChatFull.participants == null) {
                        tLRPC$ChatFull.participants = new TLRPC$TL_chatParticipants();
                    }
                    this.chatInfo.participants.participants.add(tLRPC$TL_chatChannelParticipant);
                    this.participantsMap.put(tLRPC$TL_chatChannelParticipant.user_id, tLRPC$TL_chatChannelParticipant);
                }
            }
        }
        this.loadingUsers = false;
        updateListAnimated(true);
    }

    private void setMediaHeaderVisible(boolean z) {
        if (this.mediaHeaderVisible != z) {
            this.mediaHeaderVisible = z;
            AnimatorSet animatorSet = this.headerAnimatorSet;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            AnimatorSet animatorSet2 = this.headerShadowAnimatorSet;
            if (animatorSet2 != null) {
                animatorSet2.cancel();
            }
            final ActionBarMenuItem searchItem2 = this.sharedMediaLayout.getSearchItem();
            if (!this.mediaHeaderVisible) {
                if (this.callItemVisible) {
                    this.callItem.setVisibility(0);
                }
                if (this.videoCallItemVisible) {
                    this.videoCallItem.setVisibility(0);
                }
                if (this.editItemVisible) {
                    this.editItem.setVisibility(0);
                }
                this.otherItem.setVisibility(0);
            } else {
                if (this.sharedMediaLayout.isSearchItemVisible()) {
                    searchItem2.setVisibility(0);
                }
                if (this.sharedMediaLayout.isCalendarItemVisible()) {
                    this.sharedMediaLayout.photoVideoOptionsItem.setVisibility(0);
                } else {
                    this.sharedMediaLayout.photoVideoOptionsItem.setVisibility(4);
                }
            }
            ActionBar actionBar = this.actionBar;
            if (actionBar != null) {
                actionBar.createMenu().requestLayout();
            }
            ArrayList arrayList = new ArrayList();
            ActionBarMenuItem actionBarMenuItem = this.callItem;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            float f = 1.0f;
            fArr[0] = z ? 0.0f : 1.0f;
            arrayList.add(ObjectAnimator.ofFloat(actionBarMenuItem, property, fArr));
            ActionBarMenuItem actionBarMenuItem2 = this.videoCallItem;
            Property property2 = View.ALPHA;
            float[] fArr2 = new float[1];
            fArr2[0] = z ? 0.0f : 1.0f;
            arrayList.add(ObjectAnimator.ofFloat(actionBarMenuItem2, property2, fArr2));
            ActionBarMenuItem actionBarMenuItem3 = this.otherItem;
            Property property3 = View.ALPHA;
            float[] fArr3 = new float[1];
            fArr3[0] = z ? 0.0f : 1.0f;
            arrayList.add(ObjectAnimator.ofFloat(actionBarMenuItem3, property3, fArr3));
            ActionBarMenuItem actionBarMenuItem4 = this.editItem;
            Property property4 = View.ALPHA;
            float[] fArr4 = new float[1];
            fArr4[0] = z ? 0.0f : 1.0f;
            arrayList.add(ObjectAnimator.ofFloat(actionBarMenuItem4, property4, fArr4));
            ActionBarMenuItem actionBarMenuItem5 = this.callItem;
            Property property5 = View.TRANSLATION_Y;
            float[] fArr5 = new float[1];
            fArr5[0] = z ? (float) (-AndroidUtilities.dp(10.0f)) : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(actionBarMenuItem5, property5, fArr5));
            ActionBarMenuItem actionBarMenuItem6 = this.videoCallItem;
            Property property6 = View.TRANSLATION_Y;
            float[] fArr6 = new float[1];
            fArr6[0] = z ? (float) (-AndroidUtilities.dp(10.0f)) : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(actionBarMenuItem6, property6, fArr6));
            ActionBarMenuItem actionBarMenuItem7 = this.otherItem;
            Property property7 = View.TRANSLATION_Y;
            float[] fArr7 = new float[1];
            fArr7[0] = z ? (float) (-AndroidUtilities.dp(10.0f)) : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(actionBarMenuItem7, property7, fArr7));
            ActionBarMenuItem actionBarMenuItem8 = this.editItem;
            Property property8 = View.TRANSLATION_Y;
            float[] fArr8 = new float[1];
            fArr8[0] = z ? (float) (-AndroidUtilities.dp(10.0f)) : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(actionBarMenuItem8, property8, fArr8));
            Property property9 = View.ALPHA;
            float[] fArr9 = new float[1];
            fArr9[0] = z ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(searchItem2, property9, fArr9));
            Property property10 = View.TRANSLATION_Y;
            float[] fArr10 = new float[1];
            fArr10[0] = z ? 0.0f : (float) AndroidUtilities.dp(10.0f);
            arrayList.add(ObjectAnimator.ofFloat(searchItem2, property10, fArr10));
            ImageView imageView = this.sharedMediaLayout.photoVideoOptionsItem;
            Property property11 = View.ALPHA;
            float[] fArr11 = new float[1];
            fArr11[0] = z ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(imageView, property11, fArr11));
            ImageView imageView2 = this.sharedMediaLayout.photoVideoOptionsItem;
            Property property12 = View.TRANSLATION_Y;
            float[] fArr12 = new float[1];
            fArr12[0] = z ? 0.0f : (float) AndroidUtilities.dp(10.0f);
            arrayList.add(ObjectAnimator.ofFloat(imageView2, property12, fArr12));
            ActionBar actionBar2 = this.actionBar;
            Property<ActionBar, Float> property13 = this.ACTIONBAR_HEADER_PROGRESS;
            float[] fArr13 = new float[1];
            fArr13[0] = z ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(actionBar2, property13, fArr13));
            SimpleTextView simpleTextView = this.onlineTextView[1];
            Property property14 = View.ALPHA;
            float[] fArr14 = new float[1];
            fArr14[0] = z ? 0.0f : 1.0f;
            arrayList.add(ObjectAnimator.ofFloat(simpleTextView, property14, fArr14));
            AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher = this.mediaCounterTextView;
            Property property15 = View.ALPHA;
            float[] fArr15 = new float[1];
            if (!z) {
                f = 0.0f;
            }
            fArr15[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(clippingTextViewSwitcher, property15, fArr15));
            if (z) {
                arrayList.add(ObjectAnimator.ofFloat(this, this.HEADER_SHADOW, new float[]{0.0f}));
            }
            AnimatorSet animatorSet3 = new AnimatorSet();
            this.headerAnimatorSet = animatorSet3;
            animatorSet3.playTogether(arrayList);
            this.headerAnimatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.headerAnimatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (ProfileActivity.this.headerAnimatorSet != null) {
                        if (ProfileActivity.this.mediaHeaderVisible) {
                            if (ProfileActivity.this.callItemVisible) {
                                ProfileActivity.this.callItem.setVisibility(8);
                            }
                            if (ProfileActivity.this.videoCallItemVisible) {
                                ProfileActivity.this.videoCallItem.setVisibility(8);
                            }
                            if (ProfileActivity.this.editItemVisible) {
                                ProfileActivity.this.editItem.setVisibility(8);
                            }
                            ProfileActivity.this.otherItem.setVisibility(8);
                        } else {
                            if (ProfileActivity.this.sharedMediaLayout.isSearchItemVisible()) {
                                searchItem2.setVisibility(0);
                            }
                            ProfileActivity.this.sharedMediaLayout.photoVideoOptionsItem.setVisibility(4);
                            AnimatorSet unused = ProfileActivity.this.headerShadowAnimatorSet = new AnimatorSet();
                            AnimatorSet access$16100 = ProfileActivity.this.headerShadowAnimatorSet;
                            ProfileActivity profileActivity = ProfileActivity.this;
                            access$16100.playTogether(new Animator[]{ObjectAnimator.ofFloat(profileActivity, profileActivity.HEADER_SHADOW, new float[]{1.0f})});
                            ProfileActivity.this.headerShadowAnimatorSet.setDuration(100);
                            ProfileActivity.this.headerShadowAnimatorSet.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animator) {
                                    AnimatorSet unused = ProfileActivity.this.headerShadowAnimatorSet = null;
                                }
                            });
                            ProfileActivity.this.headerShadowAnimatorSet.start();
                        }
                    }
                    AnimatorSet unused2 = ProfileActivity.this.headerAnimatorSet = null;
                }

                public void onAnimationCancel(Animator animator) {
                    AnimatorSet unused = ProfileActivity.this.headerAnimatorSet = null;
                }
            });
            this.headerAnimatorSet.setDuration(150);
            this.headerAnimatorSet.start();
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needCheckSystemBarColors, Boolean.TRUE);
        }
    }

    /* access modifiers changed from: private */
    public void openAddMember() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("addToGroup", true);
        bundle.putLong("chatId", this.currentChat.id);
        GroupCreateActivity groupCreateActivity = new GroupCreateActivity(bundle);
        groupCreateActivity.setInfo(this.chatInfo);
        TLRPC$ChatFull tLRPC$ChatFull = this.chatInfo;
        if (!(tLRPC$ChatFull == null || tLRPC$ChatFull.participants == null)) {
            LongSparseArray longSparseArray = new LongSparseArray();
            for (int i = 0; i < this.chatInfo.participants.participants.size(); i++) {
                longSparseArray.put(this.chatInfo.participants.participants.get(i).user_id, null);
            }
            groupCreateActivity.setIgnoreUsers(longSparseArray);
        }
        groupCreateActivity.setDelegate((GroupCreateActivity.ContactsAddActivityDelegate) new ProfileActivity$$ExternalSyntheticLambda41(this));
        presentFragment(groupCreateActivity);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$openAddMember$28(ArrayList arrayList, int i) {
        HashSet hashSet = new HashSet();
        if (this.chatInfo.participants.participants != null) {
            for (int i2 = 0; i2 < this.chatInfo.participants.participants.size(); i2++) {
                hashSet.add(Long.valueOf(this.chatInfo.participants.participants.get(i2).user_id));
            }
        }
        int size = arrayList.size();
        for (int i3 = 0; i3 < size; i3++) {
            TLRPC$User tLRPC$User = (TLRPC$User) arrayList.get(i3);
            getMessagesController().addUserToChat(this.chatId, tLRPC$User, i, (String) null, this, (Runnable) null);
            if (!hashSet.contains(Long.valueOf(tLRPC$User.id))) {
                TLRPC$ChatFull tLRPC$ChatFull = this.chatInfo;
                if (tLRPC$ChatFull.participants == null) {
                    tLRPC$ChatFull.participants = new TLRPC$TL_chatParticipants();
                }
                if (ChatObject.isChannel(this.currentChat)) {
                    TLRPC$TL_chatChannelParticipant tLRPC$TL_chatChannelParticipant = new TLRPC$TL_chatChannelParticipant();
                    TLRPC$TL_channelParticipant tLRPC$TL_channelParticipant = new TLRPC$TL_channelParticipant();
                    tLRPC$TL_chatChannelParticipant.channelParticipant = tLRPC$TL_channelParticipant;
                    tLRPC$TL_channelParticipant.inviter_id = getUserConfig().getClientUserId();
                    tLRPC$TL_chatChannelParticipant.channelParticipant.peer = new TLRPC$TL_peerUser();
                    TLRPC$ChannelParticipant tLRPC$ChannelParticipant = tLRPC$TL_chatChannelParticipant.channelParticipant;
                    tLRPC$ChannelParticipant.peer.user_id = tLRPC$User.id;
                    tLRPC$ChannelParticipant.date = getConnectionsManager().getCurrentTime();
                    tLRPC$TL_chatChannelParticipant.user_id = tLRPC$User.id;
                    this.chatInfo.participants.participants.add(tLRPC$TL_chatChannelParticipant);
                } else {
                    TLRPC$TL_chatParticipant tLRPC$TL_chatParticipant = new TLRPC$TL_chatParticipant();
                    tLRPC$TL_chatParticipant.user_id = tLRPC$User.id;
                    tLRPC$TL_chatParticipant.inviter_id = getAccountInstance().getUserConfig().clientUserId;
                    this.chatInfo.participants.participants.add(tLRPC$TL_chatParticipant);
                }
                this.chatInfo.participants_count++;
                getMessagesController().putUser(tLRPC$User, false);
            }
        }
        updateListAnimated(true);
    }

    /* JADX WARNING: type inference failed for: r2v12, types: [androidx.recyclerview.widget.RecyclerView$ViewHolder] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void checkListViewScroll() {
        /*
            r6 = this;
            org.telegram.ui.Components.RecyclerListView r0 = r6.listView
            int r0 = r0.getVisibility()
            if (r0 == 0) goto L_0x0009
            return
        L_0x0009:
            boolean r0 = r6.sharedMediaLayoutAttached
            if (r0 == 0) goto L_0x001f
            org.telegram.ui.Components.SharedMediaLayout r0 = r6.sharedMediaLayout
            org.telegram.ui.Components.RecyclerListView r1 = r6.listView
            int r1 = r1.getMeasuredHeight()
            org.telegram.ui.Components.SharedMediaLayout r2 = r6.sharedMediaLayout
            int r2 = r2.getTop()
            int r1 = r1 - r2
            r0.setVisibleHeight(r1)
        L_0x001f:
            org.telegram.ui.Components.RecyclerListView r0 = r6.listView
            int r0 = r0.getChildCount()
            if (r0 <= 0) goto L_0x00bf
            boolean r0 = r6.openAnimationInProgress
            if (r0 == 0) goto L_0x002d
            goto L_0x00bf
        L_0x002d:
            r0 = 0
            r1 = 0
        L_0x002f:
            org.telegram.ui.Components.RecyclerListView r2 = r6.listView
            int r2 = r2.getChildCount()
            r3 = 0
            if (r1 >= r2) goto L_0x004e
            org.telegram.ui.Components.RecyclerListView r2 = r6.listView
            android.view.View r4 = r2.getChildAt(r1)
            int r2 = r2.getChildAdapterPosition(r4)
            if (r2 != 0) goto L_0x004b
            org.telegram.ui.Components.RecyclerListView r2 = r6.listView
            android.view.View r1 = r2.getChildAt(r1)
            goto L_0x004f
        L_0x004b:
            int r1 = r1 + 1
            goto L_0x002f
        L_0x004e:
            r1 = r3
        L_0x004f:
            if (r1 != 0) goto L_0x0052
            goto L_0x005b
        L_0x0052:
            org.telegram.ui.Components.RecyclerListView r2 = r6.listView
            androidx.recyclerview.widget.RecyclerView$ViewHolder r2 = r2.findContainingViewHolder(r1)
            r3 = r2
            org.telegram.ui.Components.RecyclerListView$Holder r3 = (org.telegram.ui.Components.RecyclerListView.Holder) r3
        L_0x005b:
            if (r1 != 0) goto L_0x005f
            r1 = 0
            goto L_0x0063
        L_0x005f:
            int r1 = r1.getTop()
        L_0x0063:
            r2 = -1
            if (r3 == 0) goto L_0x006b
            int r3 = r3.getAdapterPosition()
            goto L_0x006c
        L_0x006b:
            r3 = -1
        L_0x006c:
            if (r1 < 0) goto L_0x0071
            if (r3 != 0) goto L_0x0071
            goto L_0x0072
        L_0x0071:
            r1 = 0
        L_0x0072:
            org.telegram.ui.Components.ImageUpdater r3 = r6.imageUpdater
            r4 = 1
            if (r3 != 0) goto L_0x0081
            org.telegram.ui.ActionBar.ActionBar r3 = r6.actionBar
            boolean r3 = r3.isSearchFieldVisible()
            if (r3 == 0) goto L_0x0081
            r3 = 1
            goto L_0x0082
        L_0x0081:
            r3 = 0
        L_0x0082:
            int r5 = r6.sharedMediaRow
            if (r5 == r2) goto L_0x009d
            if (r3 != 0) goto L_0x009d
            org.telegram.ui.Components.RecyclerListView r2 = r6.listView
            androidx.recyclerview.widget.RecyclerView$ViewHolder r2 = r2.findViewHolderForAdapterPosition(r5)
            org.telegram.ui.Components.RecyclerListView$Holder r2 = (org.telegram.ui.Components.RecyclerListView.Holder) r2
            if (r2 == 0) goto L_0x009c
            android.view.View r2 = r2.itemView
            int r2 = r2.getTop()
            if (r2 > 0) goto L_0x009c
            r3 = 1
            goto L_0x009d
        L_0x009c:
            r3 = 0
        L_0x009d:
            r6.setMediaHeaderVisible(r3)
            float r2 = r6.extraHeight
            float r1 = (float) r1
            int r2 = (r2 > r1 ? 1 : (r2 == r1 ? 0 : -1))
            if (r2 == 0) goto L_0x00bf
            r6.extraHeight = r1
            org.telegram.ui.ProfileActivity$TopView r1 = r6.topView
            r1.invalidate()
            int r1 = r6.playProfileAnimation
            if (r1 == 0) goto L_0x00bc
            float r1 = r6.extraHeight
            r2 = 0
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 == 0) goto L_0x00ba
            r0 = 1
        L_0x00ba:
            r6.allowProfileAnimation = r0
        L_0x00bc:
            r6.needLayout(r4)
        L_0x00bf:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.checkListViewScroll():void");
    }

    public void updateSelectedMediaTabText() {
        SharedMediaLayout sharedMediaLayout2 = this.sharedMediaLayout;
        if (sharedMediaLayout2 != null && this.mediaCounterTextView != null) {
            int closestTab = sharedMediaLayout2.getClosestTab();
            int[] lastMediaCount = this.sharedMediaPreloader.getLastMediaCount();
            if (closestTab == 0) {
                if (lastMediaCount[7] == 0 && lastMediaCount[6] == 0) {
                    this.mediaCounterTextView.setText(LocaleController.formatPluralString("Media", lastMediaCount[0], new Object[0]));
                } else if (this.sharedMediaLayout.getPhotosVideosTypeFilter() == 1 || lastMediaCount[7] == 0) {
                    this.mediaCounterTextView.setText(LocaleController.formatPluralString("Photos", lastMediaCount[6], new Object[0]));
                } else if (this.sharedMediaLayout.getPhotosVideosTypeFilter() == 2 || lastMediaCount[6] == 0) {
                    this.mediaCounterTextView.setText(LocaleController.formatPluralString("Videos", lastMediaCount[7], new Object[0]));
                } else {
                    this.mediaCounterTextView.setText(String.format("%s, %s", new Object[]{LocaleController.formatPluralString("Photos", lastMediaCount[6], new Object[0]), LocaleController.formatPluralString("Videos", lastMediaCount[7], new Object[0])}));
                }
            } else if (closestTab == 1) {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("Files", lastMediaCount[1], new Object[0]));
            } else if (closestTab == 2) {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("Voice", lastMediaCount[2], new Object[0]));
            } else if (closestTab == 3) {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("Links", lastMediaCount[3], new Object[0]));
            } else if (closestTab == 4) {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("MusicFiles", lastMediaCount[4], new Object[0]));
            } else if (closestTab == 5) {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("GIFs", lastMediaCount[5], new Object[0]));
            } else if (closestTab == 6) {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("CommonGroups", this.userInfo.common_chats_count, new Object[0]));
            } else if (closestTab == 7) {
                this.mediaCounterTextView.setText(this.onlineTextView[1].getText());
            }
        }
    }

    /* access modifiers changed from: private */
    public void needLayout(boolean z) {
        OverlaysView overlaysView2;
        ValueAnimator valueAnimator;
        BackupImageView currentItemView;
        TLRPC$ChatFull tLRPC$ChatFull;
        int i = 0;
        int currentActionBarHeight = (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null && !this.openAnimationInProgress) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) recyclerListView.getLayoutParams();
            if (layoutParams.topMargin != currentActionBarHeight) {
                layoutParams.topMargin = currentActionBarHeight;
                this.listView.setLayoutParams(layoutParams);
            }
        }
        if (this.avatarContainer != null) {
            float min = Math.min(1.0f, this.extraHeight / ((float) AndroidUtilities.dp(88.0f)));
            this.listView.setTopGlowOffset((int) this.extraHeight);
            this.listView.setOverScrollMode((this.extraHeight <= ((float) AndroidUtilities.dp(88.0f)) || this.extraHeight >= ((float) (this.listView.getMeasuredWidth() - currentActionBarHeight))) ? 0 : 2);
            RLottieImageView rLottieImageView = this.writeButton;
            if (rLottieImageView != null) {
                rLottieImageView.setTranslationY(((((float) ((this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight())) + this.extraHeight) + ((float) this.searchTransitionOffset)) - ((float) AndroidUtilities.dp(29.5f)));
                if (!this.openAnimationInProgress) {
                    float f = 0.2f;
                    boolean z2 = min > 0.2f && !this.searchMode && (this.imageUpdater == null || this.setAvatarRow == -1);
                    if (z2 && this.chatId != 0) {
                        z2 = (!ChatObject.isChannel(this.currentChat) || this.currentChat.megagroup || (tLRPC$ChatFull = this.chatInfo) == null || tLRPC$ChatFull.linked_chat_id == 0 || this.infoHeaderRow == -1) ? false : true;
                    }
                    if (z2 != (this.writeButton.getTag() == null)) {
                        if (z2) {
                            this.writeButton.setTag((Object) null);
                        } else {
                            this.writeButton.setTag(0);
                        }
                        AnimatorSet animatorSet = this.writeButtonAnimation;
                        if (animatorSet != null) {
                            this.writeButtonAnimation = null;
                            animatorSet.cancel();
                        }
                        if (z) {
                            AnimatorSet animatorSet2 = new AnimatorSet();
                            this.writeButtonAnimation = animatorSet2;
                            if (z2) {
                                animatorSet2.setInterpolator(new DecelerateInterpolator());
                                this.writeButtonAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.writeButton, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.writeButton, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.writeButton, View.ALPHA, new float[]{1.0f})});
                            } else {
                                animatorSet2.setInterpolator(new AccelerateInterpolator());
                                this.writeButtonAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.writeButton, View.SCALE_X, new float[]{0.2f}), ObjectAnimator.ofFloat(this.writeButton, View.SCALE_Y, new float[]{0.2f}), ObjectAnimator.ofFloat(this.writeButton, View.ALPHA, new float[]{0.0f})});
                            }
                            this.writeButtonAnimation.setDuration(150);
                            this.writeButtonAnimation.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animator) {
                                    if (ProfileActivity.this.writeButtonAnimation != null && ProfileActivity.this.writeButtonAnimation.equals(animator)) {
                                        AnimatorSet unused = ProfileActivity.this.writeButtonAnimation = null;
                                    }
                                }
                            });
                            this.writeButtonAnimation.start();
                        } else {
                            this.writeButton.setScaleX(z2 ? 1.0f : 0.2f);
                            RLottieImageView rLottieImageView2 = this.writeButton;
                            if (z2) {
                                f = 1.0f;
                            }
                            rLottieImageView2.setScaleY(f);
                            this.writeButton.setAlpha(z2 ? 1.0f : 0.0f);
                        }
                    }
                    if (this.qrItem != null) {
                        updateQrItemVisibility(z);
                        if (!z) {
                            float dp = ((float) AndroidUtilities.dp(48.0f)) * this.qrItem.getAlpha();
                            this.qrItem.setTranslationX(dp);
                            PagerIndicatorView pagerIndicatorView = this.avatarsViewPagerIndicatorView;
                            if (pagerIndicatorView != null) {
                                pagerIndicatorView.setTranslationX(dp - ((float) AndroidUtilities.dp(48.0f)));
                            }
                        }
                    }
                }
            }
            this.avatarX = (-AndroidUtilities.dpf2(47.0f)) * min;
            float currentActionBarHeight2 = ((float) (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0)) + ((((float) ActionBar.getCurrentActionBarHeight()) / 2.0f) * (min + 1.0f));
            float f2 = AndroidUtilities.density;
            this.avatarY = (currentActionBarHeight2 - (f2 * 21.0f)) + (f2 * 27.0f * min) + this.actionBar.getTranslationY();
            float f3 = this.openAnimationInProgress ? this.initialAnimationExtraHeight : this.extraHeight;
            if (f3 > ((float) AndroidUtilities.dp(88.0f)) || this.isPulledDown) {
                float max = Math.max(0.0f, Math.min(1.0f, (f3 - ((float) AndroidUtilities.dp(88.0f))) / ((float) ((this.listView.getMeasuredWidth() - currentActionBarHeight) - AndroidUtilities.dp(88.0f)))));
                this.expandProgress = max;
                this.avatarScale = AndroidUtilities.lerp(1.4285715f, 2.4285715f, Math.min(1.0f, max * 3.0f));
                float min2 = Math.min(AndroidUtilities.dpf2(2000.0f), Math.max(AndroidUtilities.dpf2(1100.0f), Math.abs(this.listViewVelocityY))) / AndroidUtilities.dpf2(1100.0f);
                if (!this.allowPullingDown || (!this.openingAvatar && this.expandProgress < 0.33f)) {
                    if (this.isPulledDown) {
                        this.isPulledDown = false;
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needCheckSystemBarColors, Boolean.TRUE);
                        ActionBarMenuItem actionBarMenuItem = this.otherItem;
                        if (actionBarMenuItem != null) {
                            actionBarMenuItem.hideSubItem(21);
                            if (this.imageUpdater != null) {
                                this.otherItem.hideSubItem(33);
                                this.otherItem.hideSubItem(34);
                                this.otherItem.hideSubItem(35);
                                this.otherItem.showSubItem(36);
                                this.otherItem.showSubItem(31);
                                this.otherItem.showSubItem(30);
                            }
                        }
                        ActionBarMenuItem actionBarMenuItem2 = this.searchItem;
                        if (actionBarMenuItem2 != null) {
                            actionBarMenuItem2.setEnabled(!this.scrolling);
                        }
                        this.overlaysView.setOverlaysVisible(false, min2);
                        this.avatarsViewPagerIndicatorView.refreshVisibility(min2);
                        this.expandAnimator.cancel();
                        this.avatarImage.getImageReceiver().setAllowStartAnimation(true);
                        this.avatarImage.getImageReceiver().startAnimation();
                        float lerp = AndroidUtilities.lerp(this.expandAnimatorValues, this.currentExpanAnimatorFracture);
                        float[] fArr = this.expandAnimatorValues;
                        fArr[0] = lerp;
                        fArr[1] = 0.0f;
                        if (!this.isInLandscapeMode) {
                            this.expandAnimator.setDuration((long) ((lerp * 250.0f) / min2));
                        } else {
                            this.expandAnimator.setDuration(0);
                        }
                        this.topView.setBackgroundColor(getThemedColor("avatar_backgroundActionBarBlue"));
                        if (!this.doNotSetForeground && (currentItemView = this.avatarsViewPager.getCurrentItemView()) != null) {
                            this.avatarImage.setForegroundImageDrawable(currentItemView.getImageReceiver().getDrawableSafe());
                        }
                        this.avatarImage.setForegroundAlpha(1.0f);
                        this.avatarContainer.setVisibility(0);
                        this.avatarsViewPager.setVisibility(8);
                        this.expandAnimator.start();
                    }
                    this.avatarContainer.setScaleX(this.avatarScale);
                    this.avatarContainer.setScaleY(this.avatarScale);
                    ValueAnimator valueAnimator2 = this.expandAnimator;
                    if (valueAnimator2 == null || !valueAnimator2.isRunning()) {
                        refreshNameAndOnlineXY();
                        this.nameTextView[1].setTranslationX(this.nameX);
                        this.nameTextView[1].setTranslationY(this.nameY);
                        this.onlineTextView[1].setTranslationX(this.onlineX);
                        this.onlineTextView[1].setTranslationY(this.onlineY);
                        this.mediaCounterTextView.setTranslationX(this.onlineX);
                        this.mediaCounterTextView.setTranslationY(this.onlineY);
                    }
                } else {
                    if (!this.isPulledDown) {
                        if (this.otherItem != null) {
                            if (!getMessagesController().isChatNoForwards(this.currentChat)) {
                                this.otherItem.showSubItem(21);
                            } else {
                                this.otherItem.hideSubItem(21);
                            }
                            if (this.imageUpdater != null) {
                                this.otherItem.showSubItem(36);
                                this.otherItem.showSubItem(34);
                                this.otherItem.showSubItem(35);
                                this.otherItem.hideSubItem(33);
                                this.otherItem.hideSubItem(31);
                            }
                        }
                        ActionBarMenuItem actionBarMenuItem3 = this.searchItem;
                        if (actionBarMenuItem3 != null) {
                            actionBarMenuItem3.setEnabled(false);
                        }
                        this.isPulledDown = true;
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needCheckSystemBarColors, Boolean.TRUE);
                        this.overlaysView.setOverlaysVisible(true, min2);
                        this.avatarsViewPagerIndicatorView.refreshVisibility(min2);
                        this.avatarsViewPager.setCreateThumbFromParent(true);
                        this.avatarsViewPager.getAdapter().notifyDataSetChanged();
                        this.expandAnimator.cancel();
                        float lerp2 = AndroidUtilities.lerp(this.expandAnimatorValues, this.currentExpanAnimatorFracture);
                        float[] fArr2 = this.expandAnimatorValues;
                        fArr2[0] = lerp2;
                        fArr2[1] = 1.0f;
                        this.expandAnimator.setDuration((long) (((1.0f - lerp2) * 250.0f) / min2));
                        this.expandAnimator.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationStart(Animator animator) {
                                ProfileActivity.this.setForegroundImage(false);
                                ProfileActivity.this.avatarsViewPager.setAnimatedFileMaybe(ProfileActivity.this.avatarImage.getImageReceiver().getAnimation());
                                ProfileActivity.this.avatarsViewPager.resetCurrentItem();
                            }

                            public void onAnimationEnd(Animator animator) {
                                ProfileActivity.this.expandAnimator.removeListener(this);
                                ProfileActivity.this.topView.setBackgroundColor(-16777216);
                                ProfileActivity.this.avatarContainer.setVisibility(8);
                                ProfileActivity.this.avatarsViewPager.setVisibility(0);
                            }
                        });
                        this.expandAnimator.start();
                    }
                    ViewGroup.LayoutParams layoutParams2 = this.avatarsViewPager.getLayoutParams();
                    layoutParams2.width = this.listView.getMeasuredWidth();
                    float f4 = f3 + ((float) currentActionBarHeight);
                    layoutParams2.height = (int) f4;
                    this.avatarsViewPager.requestLayout();
                    if (!this.expandAnimator.isRunning()) {
                        float dp2 = (!this.openAnimationInProgress || this.playProfileAnimation != 2) ? 0.0f : (-(1.0f - this.animationProgress)) * ((float) AndroidUtilities.dp(50.0f));
                        this.nameTextView[1].setTranslationX(AndroidUtilities.dpf2(16.0f) - ((float) this.nameTextView[1].getLeft()));
                        this.nameTextView[1].setTranslationY(((f4 - AndroidUtilities.dpf2(38.0f)) - ((float) this.nameTextView[1].getBottom())) + dp2);
                        this.onlineTextView[1].setTranslationX(AndroidUtilities.dpf2(16.0f) - ((float) this.onlineTextView[1].getLeft()));
                        this.onlineTextView[1].setTranslationY(((f4 - AndroidUtilities.dpf2(18.0f)) - ((float) this.onlineTextView[1].getBottom())) + dp2);
                        this.mediaCounterTextView.setTranslationX(this.onlineTextView[1].getTranslationX());
                        this.mediaCounterTextView.setTranslationY(this.onlineTextView[1].getTranslationY());
                    }
                }
            }
            if (this.openAnimationInProgress && this.playProfileAnimation == 2) {
                float currentActionBarHeight3 = ((((float) (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0)) + (((float) ActionBar.getCurrentActionBarHeight()) / 2.0f)) - (AndroidUtilities.density * 21.0f)) + this.actionBar.getTranslationY();
                this.nameTextView[0].setTranslationX(0.0f);
                double d = (double) currentActionBarHeight3;
                this.nameTextView[0].setTranslationY(((float) Math.floor(d)) + ((float) AndroidUtilities.dp(1.3f)));
                this.onlineTextView[0].setTranslationX(0.0f);
                this.onlineTextView[0].setTranslationY(((float) Math.floor(d)) + ((float) AndroidUtilities.dp(24.0f)));
                this.nameTextView[0].setScaleX(1.0f);
                this.nameTextView[0].setScaleY(1.0f);
                SimpleTextView[] simpleTextViewArr = this.nameTextView;
                simpleTextViewArr[1].setPivotY((float) simpleTextViewArr[1].getMeasuredHeight());
                this.nameTextView[1].setScaleX(1.67f);
                this.nameTextView[1].setScaleY(1.67f);
                this.avatarScale = AndroidUtilities.lerp(1.0f, 2.4285715f, this.animationProgress);
                this.avatarImage.setRoundRadius((int) AndroidUtilities.lerp(AndroidUtilities.dpf2(21.0f), 0.0f, this.animationProgress));
                this.avatarContainer.setTranslationX(AndroidUtilities.lerp(0.0f, 0.0f, this.animationProgress));
                this.avatarContainer.setTranslationY(AndroidUtilities.lerp((float) Math.ceil(d), 0.0f, this.animationProgress));
                float measuredWidth = ((float) (this.avatarContainer.getMeasuredWidth() - AndroidUtilities.dp(42.0f))) * this.avatarScale;
                this.timeItem.setTranslationX(this.avatarContainer.getX() + ((float) AndroidUtilities.dp(16.0f)) + measuredWidth);
                this.timeItem.setTranslationY(this.avatarContainer.getY() + ((float) AndroidUtilities.dp(15.0f)) + measuredWidth);
                this.avatarContainer.setScaleX(this.avatarScale);
                this.avatarContainer.setScaleY(this.avatarScale);
                this.overlaysView.setAlphaValue(this.animationProgress, false);
                this.actionBar.setItemsColor(ColorUtils.blendARGB(getThemedColor("actionBarDefaultIcon"), -1, this.animationProgress), false);
                ScamDrawable scamDrawable2 = this.scamDrawable;
                if (scamDrawable2 != null) {
                    scamDrawable2.setColor(ColorUtils.blendARGB(getThemedColor("avatar_subtitleInProfileBlue"), Color.argb(179, 255, 255, 255), this.animationProgress));
                }
                Drawable drawable = this.lockIconDrawable;
                if (drawable != null) {
                    drawable.setColorFilter(ColorUtils.blendARGB(getThemedColor("chat_lockIcon"), -1, this.animationProgress), PorterDuff.Mode.MULTIPLY);
                }
                CrossfadeDrawable crossfadeDrawable = this.verifiedCrossfadeDrawable;
                if (crossfadeDrawable != null) {
                    crossfadeDrawable.setProgress(this.animationProgress);
                    this.nameTextView[1].invalidate();
                }
                CrossfadeDrawable crossfadeDrawable2 = this.premuimCrossfadeDrawable;
                if (crossfadeDrawable2 != null) {
                    crossfadeDrawable2.setProgress(this.animationProgress);
                    this.nameTextView[1].invalidate();
                }
                FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.avatarContainer.getLayoutParams();
                int lerp3 = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(42.0f), (this.extraHeight + ((float) currentActionBarHeight)) / this.avatarScale, this.animationProgress);
                layoutParams3.height = lerp3;
                layoutParams3.width = lerp3;
                layoutParams3.leftMargin = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(64.0f), 0.0f, this.animationProgress);
                this.avatarContainer.requestLayout();
            } else if (this.extraHeight <= ((float) AndroidUtilities.dp(88.0f))) {
                this.avatarScale = ((min * 18.0f) + 42.0f) / 42.0f;
                float f5 = (0.12f * min) + 1.0f;
                ValueAnimator valueAnimator3 = this.expandAnimator;
                if (valueAnimator3 == null || !valueAnimator3.isRunning()) {
                    this.avatarContainer.setScaleX(this.avatarScale);
                    this.avatarContainer.setScaleY(this.avatarScale);
                    this.avatarContainer.setTranslationX(this.avatarX);
                    this.avatarContainer.setTranslationY((float) Math.ceil((double) this.avatarY));
                    float dp3 = (((float) AndroidUtilities.dp(42.0f)) * this.avatarScale) - ((float) AndroidUtilities.dp(42.0f));
                    this.timeItem.setTranslationX(this.avatarContainer.getX() + ((float) AndroidUtilities.dp(16.0f)) + dp3);
                    this.timeItem.setTranslationY(this.avatarContainer.getY() + ((float) AndroidUtilities.dp(15.0f)) + dp3);
                }
                this.nameX = AndroidUtilities.density * -21.0f * min;
                this.nameY = ((float) Math.floor((double) this.avatarY)) + ((float) AndroidUtilities.dp(1.3f)) + (((float) AndroidUtilities.dp(7.0f)) * min);
                this.onlineX = AndroidUtilities.density * -21.0f * min;
                this.onlineY = ((float) Math.floor((double) this.avatarY)) + ((float) AndroidUtilities.dp(24.0f)) + (((float) Math.floor((double) (AndroidUtilities.density * 11.0f))) * min);
                while (true) {
                    SimpleTextView[] simpleTextViewArr2 = this.nameTextView;
                    if (i >= simpleTextViewArr2.length) {
                        break;
                    }
                    if (simpleTextViewArr2[i] != null) {
                        ValueAnimator valueAnimator4 = this.expandAnimator;
                        if (valueAnimator4 == null || !valueAnimator4.isRunning()) {
                            this.nameTextView[i].setTranslationX(this.nameX);
                            this.nameTextView[i].setTranslationY(this.nameY);
                            this.onlineTextView[i].setTranslationX(this.onlineX);
                            this.onlineTextView[i].setTranslationY(this.onlineY);
                            if (i == 1) {
                                this.mediaCounterTextView.setTranslationX(this.onlineX);
                                this.mediaCounterTextView.setTranslationY(this.onlineY);
                            }
                        }
                        this.nameTextView[i].setScaleX(f5);
                        this.nameTextView[i].setScaleY(f5);
                    }
                    i++;
                }
            }
            if (!this.openAnimationInProgress && ((valueAnimator = this.expandAnimator) == null || !valueAnimator.isRunning())) {
                needLayoutText(min);
            }
        }
        if (this.isPulledDown || !((overlaysView2 = this.overlaysView) == null || overlaysView2.animator == null || !this.overlaysView.animator.isRunning())) {
            ViewGroup.LayoutParams layoutParams4 = this.overlaysView.getLayoutParams();
            layoutParams4.width = this.listView.getMeasuredWidth();
            layoutParams4.height = (int) (this.extraHeight + ((float) currentActionBarHeight));
            this.overlaysView.requestLayout();
        }
    }

    public void updateQrItemVisibility(boolean z) {
        if (this.qrItem != null) {
            float f = 1.0f;
            int i = 0;
            boolean z2 = isQrNeedVisible() && Math.min(1.0f, this.extraHeight / ((float) AndroidUtilities.dp(88.0f))) > 0.5f && this.searchTransitionProgress > 0.5f;
            if (!z) {
                AnimatorSet animatorSet = this.qrItemAnimation;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.qrItemAnimation = null;
                }
                this.isQrItemVisible = z2;
                ActionBarMenuItem actionBarMenuItem = this.qrItem;
                if (!z2) {
                    f = 0.0f;
                }
                actionBarMenuItem.setAlpha(f);
                ActionBarMenuItem actionBarMenuItem2 = this.qrItem;
                if (!z2) {
                    i = 8;
                }
                actionBarMenuItem2.setVisibility(i);
            } else if (z2 != this.isQrItemVisible) {
                this.isQrItemVisible = z2;
                AnimatorSet animatorSet2 = this.qrItemAnimation;
                if (animatorSet2 != null) {
                    animatorSet2.cancel();
                    this.qrItemAnimation = null;
                }
                this.qrItemAnimation = new AnimatorSet();
                if (this.qrItem.getVisibility() != 8 || z2) {
                    this.qrItem.setVisibility(0);
                }
                if (z2) {
                    this.qrItemAnimation.setInterpolator(new DecelerateInterpolator());
                    this.qrItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.qrItem, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.qrItem, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.avatarsViewPagerIndicatorView, View.TRANSLATION_X, new float[]{(float) (-AndroidUtilities.dp(48.0f))})});
                } else {
                    this.qrItemAnimation.setInterpolator(new AccelerateInterpolator());
                    this.qrItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.qrItem, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.qrItem, View.SCALE_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.avatarsViewPagerIndicatorView, View.TRANSLATION_X, new float[]{0.0f})});
                }
                this.qrItemAnimation.setDuration(150);
                this.qrItemAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        AnimatorSet unused = ProfileActivity.this.qrItemAnimation = null;
                    }
                });
                this.qrItemAnimation.start();
            }
        }
    }

    /* access modifiers changed from: private */
    public void setForegroundImage(boolean z) {
        Drawable drawable = this.avatarImage.getImageReceiver().getDrawable();
        String str = null;
        if (drawable instanceof AnimatedFileDrawable) {
            AnimatedFileDrawable animatedFileDrawable = (AnimatedFileDrawable) drawable;
            this.avatarImage.setForegroundImage((ImageLocation) null, (String) null, animatedFileDrawable);
            if (z) {
                animatedFileDrawable.addSecondParentView(this.avatarImage);
                return;
            }
            return;
        }
        ImageLocation imageLocation = this.avatarsViewPager.getImageLocation(0);
        if (imageLocation != null && imageLocation.imageType == 2) {
            str = "avatar";
        }
        this.avatarImage.setForegroundImage(imageLocation, str, drawable);
    }

    private void refreshNameAndOnlineXY() {
        this.nameX = ((float) AndroidUtilities.dp(-21.0f)) + (((float) this.avatarContainer.getMeasuredWidth()) * (this.avatarScale - 1.4285715f));
        this.nameY = ((float) Math.floor((double) this.avatarY)) + ((float) AndroidUtilities.dp(1.3f)) + ((float) AndroidUtilities.dp(7.0f)) + ((((float) this.avatarContainer.getMeasuredHeight()) * (this.avatarScale - 1.4285715f)) / 2.0f);
        this.onlineX = ((float) AndroidUtilities.dp(-21.0f)) + (((float) this.avatarContainer.getMeasuredWidth()) * (this.avatarScale - 1.4285715f));
        this.onlineY = ((float) Math.floor((double) this.avatarY)) + ((float) AndroidUtilities.dp(24.0f)) + ((float) Math.floor((double) (AndroidUtilities.density * 11.0f))) + ((((float) this.avatarContainer.getMeasuredHeight()) * (this.avatarScale - 1.4285715f)) / 2.0f);
    }

    public RecyclerListView getListView() {
        return this.listView;
    }

    /* access modifiers changed from: private */
    public void needLayoutText(float f) {
        float scaleX = this.nameTextView[1].getScaleX();
        float f2 = this.extraHeight > ((float) AndroidUtilities.dp(88.0f)) ? 1.67f : 1.12f;
        if (this.extraHeight <= ((float) AndroidUtilities.dp(88.0f)) || scaleX == f2) {
            int dp = AndroidUtilities.isTablet() ? AndroidUtilities.dp(490.0f) : AndroidUtilities.displaySize.x;
            ActionBarMenuItem unused = this.avatarsViewPagerIndicatorView.getSecondaryMenuItem();
            int i = 0;
            if (this.editItemVisible) {
                i = 48;
            }
            if (this.callItemVisible) {
                i += 48;
            }
            if (this.videoCallItemVisible) {
                i += 48;
            }
            if (this.searchItem != null) {
                i += 48;
            }
            int dp2 = AndroidUtilities.dp((((float) i) * (1.0f - this.mediaHeaderAnimationProgress)) + 40.0f + 126.0f);
            int i2 = dp - dp2;
            float f3 = (float) dp;
            int max = (int) ((f3 - (((float) dp2) * Math.max(0.0f, 1.0f - (f != 1.0f ? (0.15f * f) / (1.0f - f) : 1.0f)))) - this.nameTextView[1].getTranslationX());
            float measureText = (this.nameTextView[1].getPaint().measureText(this.nameTextView[1].getText().toString()) * scaleX) + ((float) this.nameTextView[1].getSideDrawablesSize());
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.nameTextView[1].getLayoutParams();
            int i3 = layoutParams.width;
            float f4 = (float) max;
            if (f4 < measureText) {
                layoutParams.width = Math.max(i2, (int) Math.ceil((double) (((float) (max - AndroidUtilities.dp(24.0f))) / (((f2 - scaleX) * 7.0f) + scaleX))));
            } else {
                layoutParams.width = (int) Math.ceil((double) measureText);
            }
            int min = (int) Math.min(((f3 - this.nameTextView[1].getX()) / scaleX) - ((float) AndroidUtilities.dp(8.0f)), (float) layoutParams.width);
            layoutParams.width = min;
            if (min != i3) {
                this.nameTextView[1].requestLayout();
            }
            float measureText2 = this.onlineTextView[1].getPaint().measureText(this.onlineTextView[1].getText().toString());
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.onlineTextView[1].getLayoutParams();
            FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.mediaCounterTextView.getLayoutParams();
            int i4 = layoutParams2.width;
            int ceil = (int) Math.ceil((double) (this.onlineTextView[1].getTranslationX() + ((float) AndroidUtilities.dp(8.0f)) + (((float) AndroidUtilities.dp(40.0f)) * (1.0f - f))));
            layoutParams2.rightMargin = ceil;
            layoutParams3.rightMargin = ceil;
            if (f4 < measureText2) {
                int ceil2 = (int) Math.ceil((double) max);
                layoutParams2.width = ceil2;
                layoutParams3.width = ceil2;
            } else {
                layoutParams2.width = -2;
                layoutParams3.width = -2;
            }
            if (i4 != layoutParams2.width) {
                this.onlineTextView[1].requestLayout();
                this.mediaCounterTextView.requestLayout();
            }
        }
    }

    private void fixLayout() {
        View view = this.fragmentView;
        if (view != null) {
            view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    if (ProfileActivity.this.fragmentView != null) {
                        ProfileActivity.this.checkListViewScroll();
                        ProfileActivity.this.needLayout(true);
                        ProfileActivity.this.fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    return true;
                }
            });
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        View findViewByPosition;
        super.onConfigurationChanged(configuration);
        SharedMediaLayout sharedMediaLayout2 = this.sharedMediaLayout;
        if (sharedMediaLayout2 != null) {
            sharedMediaLayout2.onConfigurationChanged(configuration);
        }
        invalidateIsInLandscapeMode();
        if (this.isInLandscapeMode && this.isPulledDown && (findViewByPosition = this.layoutManager.findViewByPosition(0)) != null) {
            this.listView.scrollBy(0, findViewByPosition.getTop() - AndroidUtilities.dp(88.0f));
        }
        fixLayout();
    }

    private void invalidateIsInLandscapeMode() {
        Point point = new Point();
        getParentActivity().getWindowManager().getDefaultDisplay().getSize(point);
        this.isInLandscapeMode = point.x > point.y;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        TLRPC$Chat tLRPC$Chat;
        RecyclerListView recyclerListView;
        RecyclerListView recyclerListView2;
        RecyclerListView.Holder holder;
        int i3 = 0;
        if (i == NotificationCenter.updateInterfaces) {
            int intValue = objArr[0].intValue();
            boolean z = ((MessagesController.UPDATE_MASK_AVATAR & intValue) == 0 && (MessagesController.UPDATE_MASK_NAME & intValue) == 0 && (MessagesController.UPDATE_MASK_STATUS & intValue) == 0) ? false : true;
            if (this.userId != 0) {
                if (z) {
                    updateProfileData(true);
                }
                if ((intValue & MessagesController.UPDATE_MASK_PHONE) != 0 && (recyclerListView2 = this.listView) != null && (holder = (RecyclerListView.Holder) recyclerListView2.findViewHolderForPosition(this.phoneRow)) != null) {
                    this.listAdapter.onBindViewHolder(holder, this.phoneRow);
                }
            } else if (this.chatId != 0) {
                if (!((MessagesController.UPDATE_MASK_CHAT & intValue) == 0 && (MessagesController.UPDATE_MASK_CHAT_AVATAR & intValue) == 0 && (MessagesController.UPDATE_MASK_CHAT_NAME & intValue) == 0 && (MessagesController.UPDATE_MASK_CHAT_MEMBERS & intValue) == 0 && (MessagesController.UPDATE_MASK_STATUS & intValue) == 0)) {
                    if ((MessagesController.UPDATE_MASK_CHAT & intValue) != 0) {
                        updateListAnimated(true);
                    } else {
                        updateOnlineCount(true);
                    }
                    updateProfileData(true);
                }
                if (z && (recyclerListView = this.listView) != null) {
                    int childCount = recyclerListView.getChildCount();
                    while (i3 < childCount) {
                        View childAt = this.listView.getChildAt(i3);
                        if (childAt instanceof UserCell) {
                            ((UserCell) childAt).update(intValue);
                        }
                        i3++;
                    }
                }
            }
        } else if (i == NotificationCenter.chatOnlineCountDidLoad) {
            Long l = objArr[0];
            if (this.chatInfo != null && (tLRPC$Chat = this.currentChat) != null && tLRPC$Chat.id == l.longValue()) {
                this.chatInfo.online_count = objArr[1].intValue();
                updateOnlineCount(true);
                updateProfileData(false);
            }
        } else if (i == NotificationCenter.contactsDidLoad) {
            createActionBarMenu(true);
        } else if (i == NotificationCenter.encryptedChatCreated) {
            if (this.creatingChat) {
                AndroidUtilities.runOnUIThread(new ProfileActivity$$ExternalSyntheticLambda27(this, objArr));
            }
        } else if (i == NotificationCenter.encryptedChatUpdated) {
            TLRPC$EncryptedChat tLRPC$EncryptedChat = objArr[0];
            TLRPC$EncryptedChat tLRPC$EncryptedChat2 = this.currentEncryptedChat;
            if (tLRPC$EncryptedChat2 != null && tLRPC$EncryptedChat.id == tLRPC$EncryptedChat2.id) {
                this.currentEncryptedChat = tLRPC$EncryptedChat;
                updateListAnimated(false);
            }
        } else if (i == NotificationCenter.blockedUsersDidLoad) {
            boolean z2 = this.userBlocked;
            boolean z3 = getMessagesController().blockePeers.indexOfKey(this.userId) >= 0;
            this.userBlocked = z3;
            if (z2 != z3) {
                createActionBarMenu(true);
                updateListAnimated(false);
            }
        } else if (i == NotificationCenter.groupCallUpdated) {
            Long l2 = objArr[0];
            if (this.currentChat != null) {
                long longValue = l2.longValue();
                TLRPC$Chat tLRPC$Chat2 = this.currentChat;
                if (longValue == tLRPC$Chat2.id && ChatObject.canManageCalls(tLRPC$Chat2)) {
                    TLRPC$ChatFull chatFull = MessagesController.getInstance(this.currentAccount).getChatFull(l2.longValue());
                    if (chatFull != null) {
                        TLRPC$ChatFull tLRPC$ChatFull = this.chatInfo;
                        if (tLRPC$ChatFull != null) {
                            chatFull.participants = tLRPC$ChatFull.participants;
                        }
                        this.chatInfo = chatFull;
                    }
                    TLRPC$ChatFull tLRPC$ChatFull2 = this.chatInfo;
                    if (tLRPC$ChatFull2 != null) {
                        TLRPC$TL_inputGroupCall tLRPC$TL_inputGroupCall = tLRPC$ChatFull2.call;
                        if ((tLRPC$TL_inputGroupCall == null && !this.hasVoiceChatItem) || (tLRPC$TL_inputGroupCall != null && this.hasVoiceChatItem)) {
                            createActionBarMenu(false);
                        }
                    }
                }
            }
        } else if (i == NotificationCenter.chatInfoDidLoad) {
            TLRPC$ChatFull tLRPC$ChatFull3 = objArr[0];
            if (tLRPC$ChatFull3.id == this.chatId) {
                boolean booleanValue = objArr[2].booleanValue();
                TLRPC$ChatFull tLRPC$ChatFull4 = this.chatInfo;
                if ((tLRPC$ChatFull4 instanceof TLRPC$TL_channelFull) && tLRPC$ChatFull3.participants == null) {
                    tLRPC$ChatFull3.participants = tLRPC$ChatFull4.participants;
                }
                if (tLRPC$ChatFull4 == null && (tLRPC$ChatFull3 instanceof TLRPC$TL_channelFull)) {
                    i3 = 1;
                }
                this.chatInfo = tLRPC$ChatFull3;
                if (this.mergeDialogId == 0) {
                    long j = tLRPC$ChatFull3.migrated_from_chat_id;
                    if (j != 0) {
                        this.mergeDialogId = -j;
                        getMediaDataController().getMediaCount(this.mergeDialogId, 0, this.classGuid, true);
                    }
                }
                fetchUsersFromChannelInfo();
                ProfileGalleryView profileGalleryView = this.avatarsViewPager;
                if (profileGalleryView != null) {
                    profileGalleryView.setChatInfo(this.chatInfo);
                }
                updateListAnimated(true);
                TLRPC$Chat chat = getMessagesController().getChat(Long.valueOf(this.chatId));
                if (chat != null) {
                    this.currentChat = chat;
                    createActionBarMenu(true);
                }
                if (this.currentChat.megagroup && (i3 != 0 || !booleanValue)) {
                    getChannelParticipants(true);
                }
                updateAutoDeleteItem();
                updateTtlIcon();
            }
        } else if (i == NotificationCenter.closeChats) {
            removeSelfFromStack();
        } else if (i == NotificationCenter.botInfoDidLoad) {
            TLRPC$BotInfo tLRPC$BotInfo = objArr[0];
            if (tLRPC$BotInfo.user_id == this.userId) {
                this.botInfo = tLRPC$BotInfo;
                updateListAnimated(false);
            }
        } else if (i == NotificationCenter.userInfoDidLoad) {
            if (objArr[0].longValue() == this.userId) {
                TLRPC$UserFull tLRPC$UserFull = objArr[1];
                this.userInfo = tLRPC$UserFull;
                if (this.imageUpdater == null) {
                    if (this.openAnimationInProgress || this.callItemVisible) {
                        this.recreateMenuAfterAnimation = true;
                    } else {
                        createActionBarMenu(true);
                    }
                    updateListAnimated(false);
                    this.sharedMediaLayout.setCommonGroupsCount(this.userInfo.common_chats_count);
                    updateSelectedMediaTabText();
                    SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader2 = this.sharedMediaPreloader;
                    if (sharedMediaPreloader2 == null || sharedMediaPreloader2.isMediaWasLoaded()) {
                        resumeDelayedFragmentAnimation();
                        needLayout(true);
                    }
                } else if (!TextUtils.equals(tLRPC$UserFull.about, this.currentBio)) {
                    this.listAdapter.notifyItemChanged(this.bioRow);
                }
                updateAutoDeleteItem();
                updateTtlIcon();
            }
        } else if (i == NotificationCenter.privacyRulesUpdated) {
            if (this.qrItem != null) {
                updateQrItemVisibility(true);
            }
        } else if (i == NotificationCenter.didReceiveNewMessages) {
            if (!objArr[2].booleanValue()) {
                long dialogId2 = getDialogId();
                if (dialogId2 == objArr[0].longValue()) {
                    DialogObject.isEncryptedDialog(dialogId2);
                    ArrayList arrayList = objArr[1];
                    while (i3 < arrayList.size()) {
                        MessageObject messageObject = (MessageObject) arrayList.get(i3);
                        if (this.currentEncryptedChat != null) {
                            TLRPC$MessageAction tLRPC$MessageAction = messageObject.messageOwner.action;
                            if (tLRPC$MessageAction instanceof TLRPC$TL_messageEncryptedAction) {
                                TLRPC$DecryptedMessageAction tLRPC$DecryptedMessageAction = tLRPC$MessageAction.encryptedAction;
                                if (tLRPC$DecryptedMessageAction instanceof TLRPC$TL_decryptedMessageActionSetMessageTTL) {
                                    TLRPC$TL_decryptedMessageActionSetMessageTTL tLRPC$TL_decryptedMessageActionSetMessageTTL = (TLRPC$TL_decryptedMessageActionSetMessageTTL) tLRPC$DecryptedMessageAction;
                                    ListAdapter listAdapter2 = this.listAdapter;
                                    if (listAdapter2 != null) {
                                        listAdapter2.notifyDataSetChanged();
                                    }
                                }
                            }
                        }
                        i3++;
                    }
                }
            }
        } else if (i == NotificationCenter.emojiLoaded) {
            RecyclerListView recyclerListView3 = this.listView;
            if (recyclerListView3 != null) {
                recyclerListView3.invalidateViews();
            }
        } else if (i == NotificationCenter.reloadInterface) {
            updateListAnimated(false);
        } else if (i == NotificationCenter.newSuggestionsAvailable) {
            int i4 = this.passwordSuggestionRow;
            int i5 = this.phoneSuggestionRow;
            updateRowsIds();
            if (i4 != this.passwordSuggestionRow || i5 != this.phoneSuggestionRow) {
                this.listAdapter.notifyDataSetChanged();
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didReceivedNotification$29(Object[] objArr) {
        NotificationCenter notificationCenter = getNotificationCenter();
        int i = NotificationCenter.closeChats;
        notificationCenter.removeObserver(this, i);
        getNotificationCenter().postNotificationName(i, new Object[0]);
        Bundle bundle = new Bundle();
        bundle.putInt("enc_id", objArr[0].id);
        presentFragment(new ChatActivity(bundle), true);
    }

    private void updateAutoDeleteItem() {
        if (this.autoDeleteItem != null && this.autoDeletePopupWrapper != null) {
            int i = 0;
            TLRPC$UserFull tLRPC$UserFull = this.userInfo;
            if (!(tLRPC$UserFull == null && this.chatInfo == null)) {
                i = tLRPC$UserFull != null ? tLRPC$UserFull.ttl_period : this.chatInfo.ttl_period;
            }
            this.autoDeleteItemDrawable.setTime(i);
            this.autoDeletePopupWrapper.lambda$updateItems$7(i);
        }
    }

    private void updateTimeItem() {
        TimerDrawable timerDrawable2 = this.timerDrawable;
        if (timerDrawable2 != null) {
            TLRPC$EncryptedChat tLRPC$EncryptedChat = this.currentEncryptedChat;
            if (tLRPC$EncryptedChat != null) {
                timerDrawable2.setTime(tLRPC$EncryptedChat.ttl);
                this.timeItem.setTag(1);
                this.timeItem.setVisibility(0);
                return;
            }
            TLRPC$UserFull tLRPC$UserFull = this.userInfo;
            if (tLRPC$UserFull != null) {
                timerDrawable2.setTime(tLRPC$UserFull.ttl_period);
                if (!this.needTimerImage || this.userInfo.ttl_period == 0) {
                    this.timeItem.setTag((Object) null);
                    this.timeItem.setVisibility(8);
                    return;
                }
                this.timeItem.setTag(1);
                this.timeItem.setVisibility(0);
                return;
            }
            TLRPC$ChatFull tLRPC$ChatFull = this.chatInfo;
            if (tLRPC$ChatFull != null) {
                timerDrawable2.setTime(tLRPC$ChatFull.ttl_period);
                if (!this.needTimerImage || this.chatInfo.ttl_period == 0) {
                    this.timeItem.setTag((Object) null);
                    this.timeItem.setVisibility(8);
                    return;
                }
                this.timeItem.setTag(1);
                this.timeItem.setVisibility(0);
                return;
            }
            this.timeItem.setTag((Object) null);
            this.timeItem.setVisibility(8);
        }
    }

    public boolean needDelayOpenAnimation() {
        return this.playProfileAnimation == 0;
    }

    public void mediaCountUpdated() {
        SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader2;
        SharedMediaLayout sharedMediaLayout2 = this.sharedMediaLayout;
        if (!(sharedMediaLayout2 == null || (sharedMediaPreloader2 = this.sharedMediaPreloader) == null)) {
            sharedMediaLayout2.setNewMediaCounts(sharedMediaPreloader2.getLastMediaCount());
        }
        updateSharedMediaRows();
        updateSelectedMediaTabText();
        if (this.userInfo != null) {
            resumeDelayedFragmentAnimation();
        }
    }

    public void onResume() {
        super.onResume();
        SharedMediaLayout sharedMediaLayout2 = this.sharedMediaLayout;
        if (sharedMediaLayout2 != null) {
            sharedMediaLayout2.onResume();
        }
        invalidateIsInLandscapeMode();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            this.firstLayout = true;
            listAdapter2.notifyDataSetChanged();
        }
        ImageUpdater imageUpdater2 = this.imageUpdater;
        if (imageUpdater2 != null) {
            imageUpdater2.onResume();
            setParentActivityTitle(LocaleController.getString("Settings", R.string.Settings));
        }
        updateProfileData(true);
        fixLayout();
        SimpleTextView[] simpleTextViewArr = this.nameTextView;
        if (simpleTextViewArr[1] != null) {
            setParentActivityTitle(simpleTextViewArr[1].getText());
        }
    }

    public void onPause() {
        super.onPause();
        UndoView undoView2 = this.undoView;
        if (undoView2 != null) {
            undoView2.hide(true, 0);
        }
        ImageUpdater imageUpdater2 = this.imageUpdater;
        if (imageUpdater2 != null) {
            imageUpdater2.onPause();
        }
    }

    public boolean isSwipeBackEnabled(MotionEvent motionEvent) {
        SharedMediaLayout sharedMediaLayout2;
        ProfileGalleryView profileGalleryView = this.avatarsViewPager;
        if (profileGalleryView != null && profileGalleryView.getVisibility() == 0 && this.avatarsViewPager.getRealCount() > 1) {
            this.avatarsViewPager.getHitRect(this.rect);
            if (this.rect.contains((int) motionEvent.getX(), ((int) motionEvent.getY()) - this.actionBar.getMeasuredHeight())) {
                return false;
            }
        }
        if (this.sharedMediaRow == -1 || (sharedMediaLayout2 = this.sharedMediaLayout) == null) {
            return true;
        }
        if (!sharedMediaLayout2.isSwipeBackEnabled()) {
            return false;
        }
        this.sharedMediaLayout.getHitRect(this.rect);
        if (!this.rect.contains((int) motionEvent.getX(), ((int) motionEvent.getY()) - this.actionBar.getMeasuredHeight())) {
            return true;
        }
        return this.sharedMediaLayout.isCurrentTabFirst();
    }

    public boolean canBeginSlide() {
        if (!this.sharedMediaLayout.isSwipeBackEnabled()) {
            return false;
        }
        return super.canBeginSlide();
    }

    public UndoView getUndoView() {
        return this.undoView;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x000d, code lost:
        r0 = r2.sharedMediaLayout;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onBackPressed() {
        /*
            r2 = this;
            org.telegram.ui.ActionBar.ActionBar r0 = r2.actionBar
            boolean r0 = r0.isEnabled()
            if (r0 == 0) goto L_0x0019
            int r0 = r2.sharedMediaRow
            r1 = -1
            if (r0 == r1) goto L_0x0017
            org.telegram.ui.Components.SharedMediaLayout r0 = r2.sharedMediaLayout
            if (r0 == 0) goto L_0x0017
            boolean r0 = r0.closeActionMode()
            if (r0 != 0) goto L_0x0019
        L_0x0017:
            r0 = 1
            goto L_0x001a
        L_0x0019:
            r0 = 0
        L_0x001a:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.onBackPressed():boolean");
    }

    public boolean isSettings() {
        return this.imageUpdater != null;
    }

    /* access modifiers changed from: protected */
    public void onBecomeFullyHidden() {
        UndoView undoView2 = this.undoView;
        if (undoView2 != null) {
            undoView2.hide(true, 0);
        }
    }

    public void setPlayProfileAnimation(int i) {
        SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
        if (!AndroidUtilities.isTablet()) {
            this.needTimerImage = i != 0;
            if (globalMainSettings.getBoolean("view_animations", true)) {
                this.playProfileAnimation = i;
            } else if (i == 2) {
                this.expandPhoto = true;
            }
        }
    }

    private void updateSharedMediaRows() {
        if (this.listAdapter != null) {
            updateListAnimated(false);
        }
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationStart(boolean z, boolean z2) {
        this.isFragmentOpened = z;
        if (((!z && z2) || (z && !z2)) && this.playProfileAnimation != 0 && this.allowProfileAnimation && !this.isPulledDown) {
            this.openAnimationInProgress = true;
        }
        if (z) {
            if (this.imageUpdater != null) {
                this.transitionIndex = getNotificationCenter().setAnimationInProgress(this.transitionIndex, new int[]{NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.mediaCountDidLoad, NotificationCenter.mediaCountsDidLoad, NotificationCenter.userInfoDidLoad});
            } else {
                this.transitionIndex = getNotificationCenter().setAnimationInProgress(this.transitionIndex, new int[]{NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.mediaCountDidLoad, NotificationCenter.mediaCountsDidLoad});
            }
            if (Build.VERSION.SDK_INT >= 21 && !z2 && getParentActivity() != null) {
                this.navigationBarAnimationColorFrom = getParentActivity().getWindow().getNavigationBarColor();
            }
        }
        this.transitionAnimationInProress = true;
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            if (!z2) {
                if (this.playProfileAnimation != 0 && this.allowProfileAnimation) {
                    this.openAnimationInProgress = false;
                    checkListViewScroll();
                    if (this.recreateMenuAfterAnimation) {
                        createActionBarMenu(true);
                    }
                }
                if (!this.fragmentOpened) {
                    this.fragmentOpened = true;
                    this.invalidateScroll = true;
                    this.fragmentView.requestLayout();
                }
            }
            getNotificationCenter().onAnimationFinish(this.transitionIndex);
        }
        this.transitionAnimationInProress = false;
    }

    @Keep
    public float getAnimationProgress() {
        return this.animationProgress;
    }

    @Keep
    public void setAnimationProgress(float f) {
        int i;
        int i2;
        this.animationProgress = f;
        this.listView.setAlpha(f);
        this.listView.setTranslationX(((float) AndroidUtilities.dp(48.0f)) - (((float) AndroidUtilities.dp(48.0f)) * f));
        long j = 5;
        if (this.playProfileAnimation != 2 || (i = this.avatarColor) == 0) {
            i = AvatarDrawable.getProfileBackColorForId((this.userId != 0 || (ChatObject.isChannel(this.chatId, this.currentAccount) && !this.currentChat.megagroup)) ? 5 : this.chatId, this.resourcesProvider);
        }
        int i3 = this.actionBarAnimationColorFrom;
        if (i3 == 0) {
            i3 = getThemedColor("actionBarDefault");
        }
        this.topView.setBackgroundColor(ColorUtils.blendARGB(SharedConfig.chatBlurEnabled() ? ColorUtils.setAlphaComponent(i3, 0) : i3, i, f));
        this.timerDrawable.setBackgroundColor(ColorUtils.blendARGB(i3, i, f));
        this.actionBar.setItemsColor(ColorUtils.blendARGB(getThemedColor("actionBarDefaultIcon"), AvatarDrawable.getIconColorForId((this.userId != 0 || (ChatObject.isChannel(this.chatId, this.currentAccount) && !this.currentChat.megagroup)) ? 5 : this.chatId, this.resourcesProvider), f), false);
        int themedColor = getThemedColor("profile_title");
        int themedColor2 = getThemedColor("actionBarDefaultTitle");
        for (int i4 = 0; i4 < 2; i4++) {
            SimpleTextView[] simpleTextViewArr = this.nameTextView;
            if (!(simpleTextViewArr[i4] == null || (i4 == 1 && this.playProfileAnimation == 2))) {
                simpleTextViewArr[i4].setTextColor(ColorUtils.blendARGB(themedColor2, themedColor, f));
            }
        }
        if (this.isOnline[0]) {
            i2 = getThemedColor("profile_status");
        } else {
            if (this.userId == 0 && (!ChatObject.isChannel(this.chatId, this.currentAccount) || this.currentChat.megagroup)) {
                j = this.chatId;
            }
            i2 = AvatarDrawable.getProfileTextColorForId(j, this.resourcesProvider);
        }
        int themedColor3 = getThemedColor(this.isOnline[0] ? "chat_status" : "actionBarDefaultSubtitle");
        for (int i5 = 0; i5 < 2; i5++) {
            SimpleTextView[] simpleTextViewArr2 = this.onlineTextView;
            if (!(simpleTextViewArr2[i5] == null || (i5 == 1 && this.playProfileAnimation == 2))) {
                simpleTextViewArr2[i5].setTextColor(ColorUtils.blendARGB(themedColor3, i2, f));
            }
        }
        this.extraHeight = this.initialAnimationExtraHeight * f;
        long j2 = this.userId;
        if (j2 == 0) {
            j2 = this.chatId;
        }
        int profileColorForId = AvatarDrawable.getProfileColorForId(j2, this.resourcesProvider);
        long j3 = this.userId;
        if (j3 == 0) {
            j3 = this.chatId;
        }
        int colorForId = AvatarDrawable.getColorForId(j3);
        if (profileColorForId != colorForId) {
            this.avatarDrawable.setColor(ColorUtils.blendARGB(colorForId, profileColorForId, f));
            this.avatarImage.invalidate();
        }
        int i6 = this.navigationBarAnimationColorFrom;
        if (i6 != 0) {
            setNavigationBarColor(ColorUtils.blendARGB(i6, getNavigationBarColor(), f));
        }
        this.topView.invalidate();
        needLayout(true);
        View view = this.fragmentView;
        if (view != null) {
            view.invalidate();
        }
        AboutLinkCell aboutLinkCell2 = this.aboutLinkCell;
        if (aboutLinkCell2 != null) {
            aboutLinkCell2.invalidate();
        }
    }

    public int getNavigationBarColor() {
        return Theme.getColor("windowBackgroundGray", this.resourcesProvider);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:145:0x04c2  */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x0315  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.animation.AnimatorSet onCustomTransitionAnimation(boolean r17, java.lang.Runnable r18) {
        /*
            r16 = this;
            r0 = r16
            int r1 = r0.playProfileAnimation
            r2 = 0
            if (r1 == 0) goto L_0x0520
            boolean r1 = r0.allowProfileAnimation
            if (r1 == 0) goto L_0x0520
            boolean r1 = r0.isPulledDown
            if (r1 != 0) goto L_0x0520
            boolean r1 = r0.disableProfileAnimation
            if (r1 != 0) goto L_0x0520
            android.widget.ImageView r1 = r0.timeItem
            r3 = 1065353216(0x3var_, float:1.0)
            if (r1 == 0) goto L_0x001c
            r1.setAlpha(r3)
        L_0x001c:
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r0.parentLayout
            r4 = 2
            if (r1 == 0) goto L_0x0040
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r1.fragmentsStack
            int r1 = r1.size()
            if (r1 < r4) goto L_0x0040
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r0.parentLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r1.fragmentsStack
            int r5 = r1.size()
            int r5 = r5 - r4
            java.lang.Object r1 = r1.get(r5)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
            boolean r5 = r1 instanceof org.telegram.ui.ChatActivity
            if (r5 == 0) goto L_0x0040
            org.telegram.ui.ChatActivity r1 = (org.telegram.ui.ChatActivity) r1
            r0.previousTransitionFragment = r1
        L_0x0040:
            org.telegram.ui.ChatActivity r1 = r0.previousTransitionFragment
            if (r1 == 0) goto L_0x0047
            r16.updateTimeItem()
        L_0x0047:
            android.animation.AnimatorSet r1 = new android.animation.AnimatorSet
            r1.<init>()
            int r5 = r0.playProfileAnimation
            if (r5 != r4) goto L_0x0053
            r5 = 250(0xfa, double:1.235E-321)
            goto L_0x0055
        L_0x0053:
            r5 = 180(0xb4, double:8.9E-322)
        L_0x0055:
            r1.setDuration(r5)
            org.telegram.ui.Components.RecyclerListView r5 = r0.listView
            r5.setLayerType(r4, r2)
            org.telegram.ui.ActionBar.ActionBar r5 = r0.actionBar
            org.telegram.ui.ActionBar.ActionBarMenu r5 = r5.createMenu()
            r6 = 10
            org.telegram.ui.ActionBar.ActionBarMenuItem r7 = r5.getItem(r6)
            if (r7 != 0) goto L_0x0077
            org.telegram.ui.ActionBar.ActionBarMenuItem r7 = r0.animatingItem
            if (r7 != 0) goto L_0x0077
            int r7 = org.telegram.messenger.R.drawable.ic_ab_other
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r5.addItem((int) r6, (int) r7)
            r0.animatingItem = r5
        L_0x0077:
            r5 = 0
            java.lang.String r7 = "animationProgress"
            r8 = 1045220557(0x3e4ccccd, float:0.2)
            r9 = 0
            r10 = 0
            r11 = 1
            if (r17 == 0) goto L_0x0344
            org.telegram.ui.ActionBar.SimpleTextView[] r12 = r0.onlineTextView
            r12 = r12[r11]
            android.view.ViewGroup$LayoutParams r12 = r12.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r12 = (android.widget.FrameLayout.LayoutParams) r12
            r13 = -1045954560(0xffffffffc1a80000, float:-21.0)
            float r14 = org.telegram.messenger.AndroidUtilities.density
            float r14 = r14 * r13
            r13 = 1090519040(0x41000000, float:8.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            float r14 = r14 + r13
            int r13 = (int) r14
            r12.rightMargin = r13
            org.telegram.ui.ActionBar.SimpleTextView[] r13 = r0.onlineTextView
            r13 = r13[r11]
            r13.setLayoutParams(r12)
            int r12 = r0.playProfileAnimation
            if (r12 == r4) goto L_0x0116
            android.graphics.Point r12 = org.telegram.messenger.AndroidUtilities.displaySize
            int r12 = r12.x
            r13 = 1123811328(0x42fCLASSNAME, float:126.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r12 = r12 - r13
            float r12 = (float) r12
            r13 = 1101529088(0x41a80000, float:21.0)
            float r14 = org.telegram.messenger.AndroidUtilities.density
            float r14 = r14 * r13
            float r12 = r12 + r14
            double r12 = (double) r12
            double r12 = java.lang.Math.ceil(r12)
            int r12 = (int) r12
            org.telegram.ui.ActionBar.SimpleTextView[] r13 = r0.nameTextView
            r13 = r13[r11]
            android.graphics.Paint r13 = r13.getPaint()
            org.telegram.ui.ActionBar.SimpleTextView[] r14 = r0.nameTextView
            r14 = r14[r11]
            java.lang.CharSequence r14 = r14.getText()
            java.lang.String r14 = r14.toString()
            float r13 = r13.measureText(r14)
            r14 = 1066359849(0x3f8f5CLASSNAME, float:1.12)
            float r13 = r13 * r14
            org.telegram.ui.ActionBar.SimpleTextView[] r15 = r0.nameTextView
            r15 = r15[r11]
            int r15 = r15.getSideDrawablesSize()
            float r15 = (float) r15
            float r13 = r13 + r15
            org.telegram.ui.ActionBar.SimpleTextView[] r15 = r0.nameTextView
            r15 = r15[r11]
            android.view.ViewGroup$LayoutParams r15 = r15.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r15 = (android.widget.FrameLayout.LayoutParams) r15
            float r12 = (float) r12
            int r13 = (r12 > r13 ? 1 : (r12 == r13 ? 0 : -1))
            if (r13 >= 0) goto L_0x0102
            float r12 = r12 / r14
            double r12 = (double) r12
            double r12 = java.lang.Math.ceil(r12)
            int r12 = (int) r12
            r15.width = r12
            goto L_0x0105
        L_0x0102:
            r12 = -2
            r15.width = r12
        L_0x0105:
            org.telegram.ui.ActionBar.SimpleTextView[] r12 = r0.nameTextView
            r12 = r12[r11]
            r12.setLayoutParams(r15)
            r12 = 1118830592(0x42b00000, float:88.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            r0.initialAnimationExtraHeight = r12
            goto L_0x013a
        L_0x0116:
            org.telegram.ui.ActionBar.SimpleTextView[] r12 = r0.nameTextView
            r12 = r12[r11]
            android.view.ViewGroup$LayoutParams r12 = r12.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r12 = (android.widget.FrameLayout.LayoutParams) r12
            android.graphics.Point r13 = org.telegram.messenger.AndroidUtilities.displaySize
            int r13 = r13.x
            r14 = 1107296256(0x42000000, float:32.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r13 = r13 - r14
            float r13 = (float) r13
            r14 = 1070973583(0x3fd5CLASSNAMEf, float:1.67)
            float r13 = r13 / r14
            int r13 = (int) r13
            r12.width = r13
            org.telegram.ui.ActionBar.SimpleTextView[] r13 = r0.nameTextView
            r13 = r13[r11]
            r13.setLayoutParams(r12)
        L_0x013a:
            android.view.View r12 = r0.fragmentView
            r12.setBackgroundColor(r10)
            r0.setAnimationProgress(r9)
            java.util.ArrayList r12 = new java.util.ArrayList
            r12.<init>()
            float[] r13 = new float[r4]
            r13 = {0, NUM} // fill-array
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r0, r7, r13)
            r12.add(r7)
            org.telegram.ui.Components.RLottieImageView r7 = r0.writeButton
            if (r7 == 0) goto L_0x0199
            java.lang.Object r7 = r7.getTag()
            if (r7 != 0) goto L_0x0199
            org.telegram.ui.Components.RLottieImageView r7 = r0.writeButton
            r7.setScaleX(r8)
            org.telegram.ui.Components.RLottieImageView r7 = r0.writeButton
            r7.setScaleY(r8)
            org.telegram.ui.Components.RLottieImageView r7 = r0.writeButton
            r7.setAlpha(r9)
            org.telegram.ui.Components.RLottieImageView r7 = r0.writeButton
            android.util.Property r8 = android.view.View.SCALE_X
            float[] r13 = new float[r11]
            r13[r10] = r3
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r13)
            r12.add(r7)
            org.telegram.ui.Components.RLottieImageView r7 = r0.writeButton
            android.util.Property r8 = android.view.View.SCALE_Y
            float[] r13 = new float[r11]
            r13[r10] = r3
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r13)
            r12.add(r7)
            org.telegram.ui.Components.RLottieImageView r7 = r0.writeButton
            android.util.Property r8 = android.view.View.ALPHA
            float[] r13 = new float[r11]
            r13[r10] = r3
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r13)
            r12.add(r7)
        L_0x0199:
            int r7 = r0.playProfileAnimation
            if (r7 != r4) goto L_0x01d1
            org.telegram.ui.ProfileActivity$AvatarImageView r7 = r0.avatarImage
            org.telegram.messenger.ImageReceiver r7 = r7.getImageReceiver()
            android.graphics.Bitmap r7 = r7.getBitmap()
            int r7 = org.telegram.messenger.AndroidUtilities.calcBitmapColor(r7)
            r0.avatarColor = r7
            org.telegram.ui.ActionBar.SimpleTextView[] r7 = r0.nameTextView
            r7 = r7[r11]
            r8 = -1
            r7.setTextColor(r8)
            org.telegram.ui.ActionBar.SimpleTextView[] r7 = r0.onlineTextView
            r7 = r7[r11]
            r8 = 179(0xb3, float:2.51E-43)
            r13 = 255(0xff, float:3.57E-43)
            int r8 = android.graphics.Color.argb(r8, r13, r13, r13)
            r7.setTextColor(r8)
            org.telegram.ui.ActionBar.ActionBar r7 = r0.actionBar
            r8 = 1090519039(0x40ffffff, float:7.9999995)
            r7.setItemsBackgroundColor(r8, r10)
            org.telegram.ui.ProfileActivity$OverlaysView r7 = r0.overlaysView
            r7.setOverlaysVisible()
        L_0x01d1:
            r7 = 0
        L_0x01d2:
            if (r7 >= r4) goto L_0x01fb
            org.telegram.ui.ActionBar.SimpleTextView[] r8 = r0.nameTextView
            r8 = r8[r7]
            if (r7 != 0) goto L_0x01dd
            r13 = 1065353216(0x3var_, float:1.0)
            goto L_0x01de
        L_0x01dd:
            r13 = 0
        L_0x01de:
            r8.setAlpha(r13)
            org.telegram.ui.ActionBar.SimpleTextView[] r8 = r0.nameTextView
            r8 = r8[r7]
            android.util.Property r13 = android.view.View.ALPHA
            float[] r14 = new float[r11]
            if (r7 != 0) goto L_0x01ed
            r15 = 0
            goto L_0x01ef
        L_0x01ed:
            r15 = 1065353216(0x3var_, float:1.0)
        L_0x01ef:
            r14[r10] = r15
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r8, r13, r14)
            r12.add(r8)
            int r7 = r7 + 1
            goto L_0x01d2
        L_0x01fb:
            android.widget.ImageView r7 = r0.timeItem
            java.lang.Object r7 = r7.getTag()
            if (r7 == 0) goto L_0x0233
            android.widget.ImageView r7 = r0.timeItem
            android.util.Property r8 = android.view.View.ALPHA
            float[] r13 = new float[r4]
            r13 = {NUM, 0} // fill-array
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r13)
            r12.add(r7)
            android.widget.ImageView r7 = r0.timeItem
            android.util.Property r8 = android.view.View.SCALE_X
            float[] r13 = new float[r4]
            r13 = {NUM, 0} // fill-array
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r13)
            r12.add(r7)
            android.widget.ImageView r7 = r0.timeItem
            android.util.Property r8 = android.view.View.SCALE_Y
            float[] r13 = new float[r4]
            r13 = {NUM, 0} // fill-array
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r13)
            r12.add(r7)
        L_0x0233:
            org.telegram.ui.ActionBar.ActionBarMenuItem r7 = r0.animatingItem
            if (r7 == 0) goto L_0x0249
            r7.setAlpha(r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r7 = r0.animatingItem
            android.util.Property r8 = android.view.View.ALPHA
            float[] r13 = new float[r11]
            r13[r10] = r9
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r13)
            r12.add(r7)
        L_0x0249:
            boolean r7 = r0.callItemVisible
            if (r7 == 0) goto L_0x0267
            long r7 = r0.chatId
            int r13 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
            if (r13 == 0) goto L_0x0267
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.callItem
            r5.setAlpha(r9)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.callItem
            android.util.Property r6 = android.view.View.ALPHA
            float[] r7 = new float[r11]
            r7[r10] = r3
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r6, r7)
            r12.add(r5)
        L_0x0267:
            boolean r5 = r0.videoCallItemVisible
            if (r5 == 0) goto L_0x027f
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.videoCallItem
            r5.setAlpha(r9)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.videoCallItem
            android.util.Property r6 = android.view.View.ALPHA
            float[] r7 = new float[r11]
            r7[r10] = r3
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r6, r7)
            r12.add(r5)
        L_0x027f:
            boolean r5 = r0.editItemVisible
            if (r5 == 0) goto L_0x0297
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.editItem
            r5.setAlpha(r9)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.editItem
            android.util.Property r6 = android.view.View.ALPHA
            float[] r7 = new float[r11]
            r7[r10] = r3
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r6, r7)
            r12.add(r5)
        L_0x0297:
            android.widget.ImageView r5 = r0.ttlIconView
            java.lang.Object r5 = r5.getTag()
            if (r5 == 0) goto L_0x02b3
            android.widget.ImageView r5 = r0.ttlIconView
            r5.setAlpha(r9)
            android.widget.ImageView r5 = r0.ttlIconView
            android.util.Property r6 = android.view.View.ALPHA
            float[] r7 = new float[r11]
            r7[r10] = r3
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r6, r7)
            r12.add(r5)
        L_0x02b3:
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r0.parentLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r5 = r5.fragmentsStack
            int r5 = r5.size()
            if (r5 <= r11) goto L_0x02cc
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r0.parentLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = r2.fragmentsStack
            int r5 = r2.size()
            int r5 = r5 - r4
            java.lang.Object r2 = r2.get(r5)
            org.telegram.ui.ActionBar.BaseFragment r2 = (org.telegram.ui.ActionBar.BaseFragment) r2
        L_0x02cc:
            boolean r5 = r2 instanceof org.telegram.ui.ChatActivity
            if (r5 == 0) goto L_0x0312
            org.telegram.ui.ChatActivity r2 = (org.telegram.ui.ChatActivity) r2
            org.telegram.ui.Components.ChatAvatarContainer r2 = r2.getAvatarContainer()
            org.telegram.ui.ActionBar.SimpleTextView r5 = r2.getSubtitleTextView()
            android.graphics.drawable.Drawable r5 = r5.getLeftDrawable()
            if (r5 != 0) goto L_0x02e6
            boolean[] r5 = r2.statusMadeShorter
            boolean r5 = r5[r10]
            if (r5 == 0) goto L_0x0312
        L_0x02e6:
            org.telegram.ui.ActionBar.SimpleTextView r2 = r2.getSubtitleTextView()
            r0.transitionOnlineText = r2
            android.widget.FrameLayout r2 = r0.avatarContainer2
            r2.invalidate()
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r0.onlineTextView
            r2 = r2[r10]
            r2.setAlpha(r9)
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r0.onlineTextView
            r2 = r2[r11]
            r2.setAlpha(r9)
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r0.onlineTextView
            r2 = r2[r11]
            android.util.Property r5 = android.view.View.ALPHA
            float[] r6 = new float[r11]
            r6[r10] = r3
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r5, r6)
            r12.add(r2)
            r2 = 1
            goto L_0x0313
        L_0x0312:
            r2 = 0
        L_0x0313:
            if (r2 != 0) goto L_0x033f
            r2 = 0
        L_0x0316:
            if (r2 >= r4) goto L_0x033f
            org.telegram.ui.ActionBar.SimpleTextView[] r5 = r0.onlineTextView
            r5 = r5[r2]
            if (r2 != 0) goto L_0x0321
            r6 = 1065353216(0x3var_, float:1.0)
            goto L_0x0322
        L_0x0321:
            r6 = 0
        L_0x0322:
            r5.setAlpha(r6)
            org.telegram.ui.ActionBar.SimpleTextView[] r5 = r0.onlineTextView
            r5 = r5[r2]
            android.util.Property r6 = android.view.View.ALPHA
            float[] r7 = new float[r11]
            if (r2 != 0) goto L_0x0331
            r8 = 0
            goto L_0x0333
        L_0x0331:
            r8 = 1065353216(0x3var_, float:1.0)
        L_0x0333:
            r7[r10] = r8
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r6, r7)
            r12.add(r5)
            int r2 = r2 + 1
            goto L_0x0316
        L_0x033f:
            r1.playTogether(r12)
            goto L_0x04e2
        L_0x0344:
            float r12 = r0.extraHeight
            r0.initialAnimationExtraHeight = r12
            java.util.ArrayList r12 = new java.util.ArrayList
            r12.<init>()
            float[] r13 = new float[r4]
            r13 = {NUM, 0} // fill-array
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r0, r7, r13)
            r12.add(r7)
            org.telegram.ui.Components.RLottieImageView r7 = r0.writeButton
            if (r7 == 0) goto L_0x0388
            android.util.Property r13 = android.view.View.SCALE_X
            float[] r14 = new float[r11]
            r14[r10] = r8
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r13, r14)
            r12.add(r7)
            org.telegram.ui.Components.RLottieImageView r7 = r0.writeButton
            android.util.Property r13 = android.view.View.SCALE_Y
            float[] r14 = new float[r11]
            r14[r10] = r8
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r13, r14)
            r12.add(r7)
            org.telegram.ui.Components.RLottieImageView r7 = r0.writeButton
            android.util.Property r8 = android.view.View.ALPHA
            float[] r13 = new float[r11]
            r13[r10] = r9
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r13)
            r12.add(r7)
        L_0x0388:
            r7 = 0
        L_0x0389:
            if (r7 >= r4) goto L_0x03a5
            org.telegram.ui.ActionBar.SimpleTextView[] r8 = r0.nameTextView
            r8 = r8[r7]
            android.util.Property r13 = android.view.View.ALPHA
            float[] r14 = new float[r11]
            if (r7 != 0) goto L_0x0398
            r15 = 1065353216(0x3var_, float:1.0)
            goto L_0x0399
        L_0x0398:
            r15 = 0
        L_0x0399:
            r14[r10] = r15
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r8, r13, r14)
            r12.add(r8)
            int r7 = r7 + 1
            goto L_0x0389
        L_0x03a5:
            android.widget.ImageView r7 = r0.timeItem
            java.lang.Object r7 = r7.getTag()
            if (r7 == 0) goto L_0x03e2
            android.widget.ImageView r7 = r0.timeItem
            r7.setAlpha(r9)
            android.widget.ImageView r7 = r0.timeItem
            android.util.Property r8 = android.view.View.ALPHA
            float[] r13 = new float[r4]
            r13 = {0, NUM} // fill-array
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r13)
            r12.add(r7)
            android.widget.ImageView r7 = r0.timeItem
            android.util.Property r8 = android.view.View.SCALE_X
            float[] r13 = new float[r4]
            r13 = {0, NUM} // fill-array
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r13)
            r12.add(r7)
            android.widget.ImageView r7 = r0.timeItem
            android.util.Property r8 = android.view.View.SCALE_Y
            float[] r13 = new float[r4]
            r13 = {0, NUM} // fill-array
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r13)
            r12.add(r7)
        L_0x03e2:
            org.telegram.ui.ActionBar.ActionBarMenuItem r7 = r0.animatingItem
            if (r7 == 0) goto L_0x03f8
            r7.setAlpha(r9)
            org.telegram.ui.ActionBar.ActionBarMenuItem r7 = r0.animatingItem
            android.util.Property r8 = android.view.View.ALPHA
            float[] r13 = new float[r11]
            r13[r10] = r3
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r13)
            r12.add(r7)
        L_0x03f8:
            boolean r7 = r0.callItemVisible
            if (r7 == 0) goto L_0x0416
            long r7 = r0.chatId
            int r13 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
            if (r13 == 0) goto L_0x0416
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.callItem
            r5.setAlpha(r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.callItem
            android.util.Property r6 = android.view.View.ALPHA
            float[] r7 = new float[r11]
            r7[r10] = r9
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r6, r7)
            r12.add(r5)
        L_0x0416:
            boolean r5 = r0.videoCallItemVisible
            if (r5 == 0) goto L_0x042e
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.videoCallItem
            r5.setAlpha(r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.videoCallItem
            android.util.Property r6 = android.view.View.ALPHA
            float[] r7 = new float[r11]
            r7[r10] = r9
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r6, r7)
            r12.add(r5)
        L_0x042e:
            boolean r5 = r0.editItemVisible
            if (r5 == 0) goto L_0x0446
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.editItem
            r5.setAlpha(r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.editItem
            android.util.Property r6 = android.view.View.ALPHA
            float[] r7 = new float[r11]
            r7[r10] = r9
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r6, r7)
            r12.add(r5)
        L_0x0446:
            android.widget.ImageView r5 = r0.ttlIconView
            if (r5 == 0) goto L_0x045d
            android.util.Property r6 = android.view.View.ALPHA
            float[] r7 = new float[r4]
            float r8 = r5.getAlpha()
            r7[r10] = r8
            r7[r11] = r9
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r6, r7)
            r12.add(r5)
        L_0x045d:
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r0.parentLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r5 = r5.fragmentsStack
            int r5 = r5.size()
            if (r5 <= r11) goto L_0x0476
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r0.parentLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = r2.fragmentsStack
            int r5 = r2.size()
            int r5 = r5 - r4
            java.lang.Object r2 = r2.get(r5)
            org.telegram.ui.ActionBar.BaseFragment r2 = (org.telegram.ui.ActionBar.BaseFragment) r2
        L_0x0476:
            boolean r5 = r2 instanceof org.telegram.ui.ChatActivity
            if (r5 == 0) goto L_0x04bf
            org.telegram.ui.ChatActivity r2 = (org.telegram.ui.ChatActivity) r2
            org.telegram.ui.Components.ChatAvatarContainer r2 = r2.getAvatarContainer()
            org.telegram.ui.ActionBar.SimpleTextView r5 = r2.getSubtitleTextView()
            android.graphics.drawable.Drawable r5 = r5.getLeftDrawable()
            if (r5 != 0) goto L_0x0490
            boolean[] r5 = r2.statusMadeShorter
            boolean r5 = r5[r10]
            if (r5 == 0) goto L_0x04bf
        L_0x0490:
            org.telegram.ui.ActionBar.SimpleTextView r2 = r2.getSubtitleTextView()
            r0.transitionOnlineText = r2
            android.widget.FrameLayout r2 = r0.avatarContainer2
            r2.invalidate()
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r0.onlineTextView
            r2 = r2[r10]
            android.util.Property r5 = android.view.View.ALPHA
            float[] r6 = new float[r11]
            r6[r10] = r9
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r5, r6)
            r12.add(r2)
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r0.onlineTextView
            r2 = r2[r11]
            android.util.Property r5 = android.view.View.ALPHA
            float[] r6 = new float[r11]
            r6[r10] = r9
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r5, r6)
            r12.add(r2)
            r2 = 1
            goto L_0x04c0
        L_0x04bf:
            r2 = 0
        L_0x04c0:
            if (r2 != 0) goto L_0x04df
            r2 = 0
        L_0x04c3:
            if (r2 >= r4) goto L_0x04df
            org.telegram.ui.ActionBar.SimpleTextView[] r5 = r0.onlineTextView
            r5 = r5[r2]
            android.util.Property r6 = android.view.View.ALPHA
            float[] r7 = new float[r11]
            if (r2 != 0) goto L_0x04d2
            r8 = 1065353216(0x3var_, float:1.0)
            goto L_0x04d3
        L_0x04d2:
            r8 = 0
        L_0x04d3:
            r7[r10] = r8
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r6, r7)
            r12.add(r5)
            int r2 = r2 + 1
            goto L_0x04c3
        L_0x04df:
            r1.playTogether(r12)
        L_0x04e2:
            r0.profileTransitionInProgress = r11
            float[] r2 = new float[r4]
            r2 = {0, NUM} // fill-array
            android.animation.ValueAnimator r2 = android.animation.ValueAnimator.ofFloat(r2)
            org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda0 r3 = new org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda0
            r3.<init>(r0)
            r2.addUpdateListener(r3)
            android.animation.Animator[] r3 = new android.animation.Animator[r11]
            r3[r10] = r2
            r1.playTogether(r3)
            org.telegram.ui.ProfileActivity$35 r2 = new org.telegram.ui.ProfileActivity$35
            r3 = r18
            r2.<init>(r3)
            r1.addListener(r2)
            int r2 = r0.playProfileAnimation
            if (r2 != r4) goto L_0x050d
            org.telegram.ui.Components.CubicBezierInterpolator r2 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            goto L_0x0512
        L_0x050d:
            android.view.animation.DecelerateInterpolator r2 = new android.view.animation.DecelerateInterpolator
            r2.<init>()
        L_0x0512:
            r1.setInterpolator(r2)
            org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda18 r2 = new org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda18
            r2.<init>(r1)
            r3 = 50
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2, r3)
            return r1
        L_0x0520:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.onCustomTransitionAnimation(boolean, java.lang.Runnable):android.animation.AnimatorSet");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCustomTransitionAnimation$30(ValueAnimator valueAnimator) {
        this.fragmentView.invalidate();
    }

    /* access modifiers changed from: private */
    public void updateOnlineCount(boolean z) {
        TLRPC$UserStatus tLRPC$UserStatus;
        this.onlineCount = 0;
        int currentTime = getConnectionsManager().getCurrentTime();
        this.sortedUsers.clear();
        TLRPC$ChatFull tLRPC$ChatFull = this.chatInfo;
        if ((tLRPC$ChatFull instanceof TLRPC$TL_chatFull) || ((tLRPC$ChatFull instanceof TLRPC$TL_channelFull) && tLRPC$ChatFull.participants_count <= 200 && tLRPC$ChatFull.participants != null)) {
            for (int i = 0; i < this.chatInfo.participants.participants.size(); i++) {
                TLRPC$User user = getMessagesController().getUser(Long.valueOf(this.chatInfo.participants.participants.get(i).user_id));
                if (!(user == null || (tLRPC$UserStatus = user.status) == null || ((tLRPC$UserStatus.expires <= currentTime && user.id != getUserConfig().getClientUserId()) || user.status.expires <= 10000))) {
                    this.onlineCount++;
                }
                this.sortedUsers.add(Integer.valueOf(i));
            }
            try {
                Collections.sort(this.sortedUsers, new ProfileActivity$$ExternalSyntheticLambda29(this, currentTime));
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            if (z && this.listAdapter != null && this.membersStartRow > 0) {
                AndroidUtilities.updateVisibleRows(this.listView);
            }
            if (this.sharedMediaLayout != null && this.sharedMediaRow != -1) {
                if ((this.sortedUsers.size() > 5 || this.usersForceShowingIn == 2) && this.usersForceShowingIn != 1) {
                    this.sharedMediaLayout.setChatUsers(this.sortedUsers, this.chatInfo);
                }
            }
        } else if ((tLRPC$ChatFull instanceof TLRPC$TL_channelFull) && tLRPC$ChatFull.participants_count > 200) {
            this.onlineCount = tLRPC$ChatFull.online_count;
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x005c  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0074 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x007f A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x008a A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x0093 A[ADDED_TO_REGION] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ int lambda$updateOnlineCount$31(int r5, java.lang.Integer r6, java.lang.Integer r7) {
        /*
            r4 = this;
            org.telegram.messenger.MessagesController r0 = r4.getMessagesController()
            org.telegram.tgnet.TLRPC$ChatFull r1 = r4.chatInfo
            org.telegram.tgnet.TLRPC$ChatParticipants r1 = r1.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r1 = r1.participants
            int r7 = r7.intValue()
            java.lang.Object r7 = r1.get(r7)
            org.telegram.tgnet.TLRPC$ChatParticipant r7 = (org.telegram.tgnet.TLRPC$ChatParticipant) r7
            long r1 = r7.user_id
            java.lang.Long r7 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r7 = r0.getUser(r7)
            org.telegram.messenger.MessagesController r0 = r4.getMessagesController()
            org.telegram.tgnet.TLRPC$ChatFull r1 = r4.chatInfo
            org.telegram.tgnet.TLRPC$ChatParticipants r1 = r1.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r1 = r1.participants
            int r6 = r6.intValue()
            java.lang.Object r6 = r1.get(r6)
            org.telegram.tgnet.TLRPC$ChatParticipant r6 = (org.telegram.tgnet.TLRPC$ChatParticipant) r6
            long r1 = r6.user_id
            java.lang.Long r6 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r6 = r0.getUser(r6)
            r0 = 50000(0xCLASSNAME, float:7.0065E-41)
            r1 = -110(0xfffffffffffffvar_, float:NaN)
            r2 = 0
            if (r7 == 0) goto L_0x0059
            boolean r3 = r7.bot
            if (r3 == 0) goto L_0x004b
            r7 = -110(0xfffffffffffffvar_, float:NaN)
            goto L_0x005a
        L_0x004b:
            boolean r3 = r7.self
            if (r3 == 0) goto L_0x0052
            int r7 = r5 + r0
            goto L_0x005a
        L_0x0052:
            org.telegram.tgnet.TLRPC$UserStatus r7 = r7.status
            if (r7 == 0) goto L_0x0059
            int r7 = r7.expires
            goto L_0x005a
        L_0x0059:
            r7 = 0
        L_0x005a:
            if (r6 == 0) goto L_0x006f
            boolean r3 = r6.bot
            if (r3 == 0) goto L_0x0061
            goto L_0x0070
        L_0x0061:
            boolean r1 = r6.self
            if (r1 == 0) goto L_0x0068
            int r1 = r5 + r0
            goto L_0x0070
        L_0x0068:
            org.telegram.tgnet.TLRPC$UserStatus r5 = r6.status
            if (r5 == 0) goto L_0x006f
            int r1 = r5.expires
            goto L_0x0070
        L_0x006f:
            r1 = 0
        L_0x0070:
            r5 = -1
            r6 = 1
            if (r7 <= 0) goto L_0x007d
            if (r1 <= 0) goto L_0x007d
            if (r7 <= r1) goto L_0x0079
            return r6
        L_0x0079:
            if (r7 >= r1) goto L_0x007c
            return r5
        L_0x007c:
            return r2
        L_0x007d:
            if (r7 >= 0) goto L_0x0088
            if (r1 >= 0) goto L_0x0088
            if (r7 <= r1) goto L_0x0084
            return r6
        L_0x0084:
            if (r7 >= r1) goto L_0x0087
            return r5
        L_0x0087:
            return r2
        L_0x0088:
            if (r7 >= 0) goto L_0x008c
            if (r1 > 0) goto L_0x0090
        L_0x008c:
            if (r7 != 0) goto L_0x0091
            if (r1 == 0) goto L_0x0091
        L_0x0090:
            return r5
        L_0x0091:
            if (r1 >= 0) goto L_0x0095
            if (r7 > 0) goto L_0x0099
        L_0x0095:
            if (r1 != 0) goto L_0x009a
            if (r7 == 0) goto L_0x009a
        L_0x0099:
            return r6
        L_0x009a:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.lambda$updateOnlineCount$31(int, java.lang.Integer, java.lang.Integer):int");
    }

    public void setChatInfo(TLRPC$ChatFull tLRPC$ChatFull) {
        this.chatInfo = tLRPC$ChatFull;
        if (tLRPC$ChatFull != null) {
            long j = tLRPC$ChatFull.migrated_from_chat_id;
            if (j != 0 && this.mergeDialogId == 0) {
                this.mergeDialogId = -j;
                getMediaDataController().getMediaCounts(this.mergeDialogId, this.classGuid);
            }
        }
        SharedMediaLayout sharedMediaLayout2 = this.sharedMediaLayout;
        if (sharedMediaLayout2 != null) {
            sharedMediaLayout2.setChatInfo(this.chatInfo);
        }
        ProfileGalleryView profileGalleryView = this.avatarsViewPager;
        if (profileGalleryView != null) {
            profileGalleryView.setChatInfo(this.chatInfo);
        }
        fetchUsersFromChannelInfo();
    }

    public void setUserInfo(TLRPC$UserFull tLRPC$UserFull) {
        this.userInfo = tLRPC$UserFull;
    }

    public boolean canSearchMembers() {
        return this.canSearchMembers;
    }

    private void fetchUsersFromChannelInfo() {
        TLRPC$Chat tLRPC$Chat = this.currentChat;
        if (tLRPC$Chat != null && tLRPC$Chat.megagroup) {
            TLRPC$ChatFull tLRPC$ChatFull = this.chatInfo;
            if ((tLRPC$ChatFull instanceof TLRPC$TL_channelFull) && tLRPC$ChatFull.participants != null) {
                for (int i = 0; i < this.chatInfo.participants.participants.size(); i++) {
                    TLRPC$ChatParticipant tLRPC$ChatParticipant = this.chatInfo.participants.participants.get(i);
                    this.participantsMap.put(tLRPC$ChatParticipant.user_id, tLRPC$ChatParticipant);
                }
            }
        }
    }

    private void kickUser(long j, TLRPC$ChatParticipant tLRPC$ChatParticipant) {
        if (j != 0) {
            TLRPC$User user = getMessagesController().getUser(Long.valueOf(j));
            getMessagesController().deleteParticipantFromChat(this.chatId, user, this.chatInfo);
            if (!(this.currentChat == null || user == null || !BulletinFactory.canShowBulletin(this))) {
                BulletinFactory.createRemoveFromChatBulletin(this, user, this.currentChat.title).show();
            }
            if (this.chatInfo.participants.participants.remove(tLRPC$ChatParticipant)) {
                updateListAnimated(true);
                return;
            }
            return;
        }
        NotificationCenter notificationCenter = getNotificationCenter();
        int i = NotificationCenter.closeChats;
        notificationCenter.removeObserver(this, i);
        if (AndroidUtilities.isTablet()) {
            getNotificationCenter().postNotificationName(i, Long.valueOf(-this.chatId));
        } else {
            getNotificationCenter().postNotificationName(i, new Object[0]);
        }
        getMessagesController().deleteParticipantFromChat(this.chatId, getMessagesController().getUser(Long.valueOf(getUserConfig().getClientUserId())), this.chatInfo);
        this.playProfileAnimation = 0;
        finishFragment();
    }

    public boolean isChat() {
        return this.chatId != 0;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x00e1, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated) == false) goto L_0x00e3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateRowsIds() {
        /*
            r10 = this;
            int r0 = r10.rowCount
            r1 = 0
            r10.rowCount = r1
            r2 = -1
            r10.setAvatarRow = r2
            r10.setAvatarSectionRow = r2
            r10.numberSectionRow = r2
            r10.numberRow = r2
            r10.setUsernameRow = r2
            r10.bioRow = r2
            r10.phoneSuggestionSectionRow = r2
            r10.phoneSuggestionRow = r2
            r10.passwordSuggestionSectionRow = r2
            r10.passwordSuggestionRow = r2
            r10.settingsSectionRow = r2
            r10.settingsSectionRow2 = r2
            r10.notificationRow = r2
            r10.languageRow = r2
            r10.premiumRow = r2
            r10.premiumSectionsRow = r2
            r10.privacyRow = r2
            r10.dataRow = r2
            r10.chatRow = r2
            r10.filtersRow = r2
            r10.stickersRow = r2
            r10.devicesRow = r2
            r10.devicesSectionRow = r2
            r10.helpHeaderRow = r2
            r10.questionRow = r2
            r10.faqRow = r2
            r10.policyRow = r2
            r10.helpSectionCell = r2
            r10.debugHeaderRow = r2
            r10.sendLogsRow = r2
            r10.sendLastLogsRow = r2
            r10.clearLogsRow = r2
            r10.switchBackendRow = r2
            r10.versionRow = r2
            r10.sendMessageRow = r2
            r10.reportRow = r2
            r10.emptyRow = r2
            r10.infoHeaderRow = r2
            r10.phoneRow = r2
            r10.userInfoRow = r2
            r10.locationRow = r2
            r10.channelInfoRow = r2
            r10.usernameRow = r2
            r10.settingsTimerRow = r2
            r10.settingsKeyRow = r2
            r10.notificationsDividerRow = r2
            r10.notificationsRow = r2
            r10.infoSectionRow = r2
            r10.secretSettingsSectionRow = r2
            r10.bottomPaddingRow = r2
            r10.addToGroupButtonRow = r2
            r10.addToGroupInfoRow = r2
            r10.membersHeaderRow = r2
            r10.membersStartRow = r2
            r10.membersEndRow = r2
            r10.addMemberRow = r2
            r10.subscribersRow = r2
            r10.subscribersRequestsRow = r2
            r10.administratorsRow = r2
            r10.blockedUsersRow = r2
            r10.membersSectionRow = r2
            r10.sharedMediaRow = r2
            r10.unblockRow = r2
            r10.joinRow = r2
            r10.lastSectionRow = r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r3 = r10.visibleChatParticipants
            r3.clear()
            java.util.ArrayList<java.lang.Integer> r3 = r10.visibleSortedUsers
            r3.clear()
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloader r3 = r10.sharedMediaPreloader
            r4 = 1
            if (r3 == 0) goto L_0x00a8
            int[] r3 = r3.getLastMediaCount()
            r5 = 0
        L_0x009c:
            int r6 = r3.length
            if (r5 >= r6) goto L_0x00a8
            r6 = r3[r5]
            if (r6 <= 0) goto L_0x00a5
            r3 = 1
            goto L_0x00a9
        L_0x00a5:
            int r5 = r5 + 1
            goto L_0x009c
        L_0x00a8:
            r3 = 0
        L_0x00a9:
            long r5 = r10.userId
            r7 = 0
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 == 0) goto L_0x0333
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 == 0) goto L_0x00bd
            int r5 = r10.rowCount
            int r6 = r5 + 1
            r10.rowCount = r6
            r10.emptyRow = r5
        L_0x00bd:
            org.telegram.messenger.MessagesController r5 = r10.getMessagesController()
            long r6 = r10.userId
            java.lang.Long r6 = java.lang.Long.valueOf(r6)
            org.telegram.tgnet.TLRPC$User r5 = r5.getUser(r6)
            boolean r6 = org.telegram.messenger.UserObject.isUserSelf(r5)
            if (r6 == 0) goto L_0x0215
            org.telegram.tgnet.TLRPC$FileLocation r3 = r10.avatarBig
            if (r3 != 0) goto L_0x00fb
            org.telegram.tgnet.TLRPC$UserProfilePhoto r3 = r5.photo
            if (r3 == 0) goto L_0x00e3
            org.telegram.tgnet.TLRPC$FileLocation r3 = r3.photo_big
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_fileLocation_layer97
            if (r4 != 0) goto L_0x00fb
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated
            if (r3 != 0) goto L_0x00fb
        L_0x00e3:
            org.telegram.ui.Components.ProfileGalleryView r3 = r10.avatarsViewPager
            if (r3 == 0) goto L_0x00ed
            int r3 = r3.getRealCount()
            if (r3 != 0) goto L_0x00fb
        L_0x00ed:
            int r3 = r10.rowCount
            int r4 = r3 + 1
            r10.rowCount = r4
            r10.setAvatarRow = r3
            int r3 = r4 + 1
            r10.rowCount = r3
            r10.setAvatarSectionRow = r4
        L_0x00fb:
            int r3 = r10.rowCount
            int r4 = r3 + 1
            r10.rowCount = r4
            r10.numberSectionRow = r3
            int r3 = r4 + 1
            r10.rowCount = r3
            r10.numberRow = r4
            int r4 = r3 + 1
            r10.rowCount = r4
            r10.setUsernameRow = r3
            int r3 = r4 + 1
            r10.rowCount = r3
            r10.bioRow = r4
            int r4 = r3 + 1
            r10.rowCount = r4
            r10.settingsSectionRow = r3
            org.telegram.messenger.MessagesController r3 = r10.getMessagesController()
            java.util.Set<java.lang.String> r3 = r3.pendingSuggestions
            java.lang.String r4 = "VALIDATE_PHONE_NUMBER"
            boolean r4 = r3.contains(r4)
            if (r4 == 0) goto L_0x0137
            int r4 = r10.rowCount
            int r5 = r4 + 1
            r10.rowCount = r5
            r10.phoneSuggestionRow = r4
            int r4 = r5 + 1
            r10.rowCount = r4
            r10.phoneSuggestionSectionRow = r5
        L_0x0137:
            java.lang.String r4 = "VALIDATE_PASSWORD"
            boolean r3 = r3.contains(r4)
            if (r3 == 0) goto L_0x014d
            int r3 = r10.rowCount
            int r4 = r3 + 1
            r10.rowCount = r4
            r10.passwordSuggestionRow = r3
            int r3 = r4 + 1
            r10.rowCount = r3
            r10.passwordSuggestionSectionRow = r4
        L_0x014d:
            int r3 = r10.rowCount
            int r4 = r3 + 1
            r10.rowCount = r4
            r10.settingsSectionRow2 = r3
            int r3 = r4 + 1
            r10.rowCount = r3
            r10.notificationRow = r4
            int r4 = r3 + 1
            r10.rowCount = r4
            r10.privacyRow = r3
            int r3 = r4 + 1
            r10.rowCount = r3
            r10.dataRow = r4
            int r4 = r3 + 1
            r10.rowCount = r4
            r10.chatRow = r3
            int r3 = r4 + 1
            r10.rowCount = r3
            r10.stickersRow = r4
            org.telegram.messenger.MessagesController r3 = r10.getMessagesController()
            boolean r3 = r3.filtersEnabled
            if (r3 != 0) goto L_0x0187
            org.telegram.messenger.MessagesController r3 = r10.getMessagesController()
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r3 = r3.dialogFilters
            boolean r3 = r3.isEmpty()
            if (r3 != 0) goto L_0x018f
        L_0x0187:
            int r3 = r10.rowCount
            int r4 = r3 + 1
            r10.rowCount = r4
            r10.filtersRow = r3
        L_0x018f:
            int r3 = r10.rowCount
            int r4 = r3 + 1
            r10.rowCount = r4
            r10.devicesRow = r3
            int r3 = r4 + 1
            r10.rowCount = r3
            r10.languageRow = r4
            int r4 = r3 + 1
            r10.rowCount = r4
            r10.devicesSectionRow = r3
            org.telegram.messenger.MessagesController r3 = r10.getMessagesController()
            boolean r3 = r3.premiumLocked
            if (r3 != 0) goto L_0x01b9
            int r3 = r10.rowCount
            int r4 = r3 + 1
            r10.rowCount = r4
            r10.premiumRow = r3
            int r3 = r4 + 1
            r10.rowCount = r3
            r10.premiumSectionsRow = r4
        L_0x01b9:
            int r3 = r10.rowCount
            int r4 = r3 + 1
            r10.rowCount = r4
            r10.helpHeaderRow = r3
            int r3 = r4 + 1
            r10.rowCount = r3
            r10.questionRow = r4
            int r4 = r3 + 1
            r10.rowCount = r4
            r10.faqRow = r3
            int r3 = r4 + 1
            r10.rowCount = r3
            r10.policyRow = r4
            boolean r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r4 != 0) goto L_0x01db
            boolean r4 = org.telegram.messenger.BuildVars.DEBUG_PRIVATE_VERSION
            if (r4 == 0) goto L_0x01e7
        L_0x01db:
            int r4 = r3 + 1
            r10.rowCount = r4
            r10.helpSectionCell = r3
            int r3 = r4 + 1
            r10.rowCount = r3
            r10.debugHeaderRow = r4
        L_0x01e7:
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r3 == 0) goto L_0x01ff
            int r3 = r10.rowCount
            int r4 = r3 + 1
            r10.rowCount = r4
            r10.sendLogsRow = r3
            int r3 = r4 + 1
            r10.rowCount = r3
            r10.sendLastLogsRow = r4
            int r4 = r3 + 1
            r10.rowCount = r4
            r10.clearLogsRow = r3
        L_0x01ff:
            boolean r3 = org.telegram.messenger.BuildVars.DEBUG_PRIVATE_VERSION
            if (r3 == 0) goto L_0x020b
            int r3 = r10.rowCount
            int r4 = r3 + 1
            r10.rowCount = r4
            r10.switchBackendRow = r3
        L_0x020b:
            int r3 = r10.rowCount
            int r4 = r3 + 1
            r10.rowCount = r4
            r10.versionRow = r3
            goto L_0x058d
        L_0x0215:
            org.telegram.tgnet.TLRPC$UserFull r6 = r10.userInfo
            if (r6 == 0) goto L_0x0221
            java.lang.String r6 = r6.about
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 == 0) goto L_0x022b
        L_0x0221:
            if (r5 == 0) goto L_0x022d
            java.lang.String r6 = r5.username
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 != 0) goto L_0x022d
        L_0x022b:
            r6 = 1
            goto L_0x022e
        L_0x022d:
            r6 = 0
        L_0x022e:
            if (r5 == 0) goto L_0x0239
            java.lang.String r7 = r5.phone
            boolean r7 = android.text.TextUtils.isEmpty(r7)
            if (r7 != 0) goto L_0x0239
            goto L_0x023a
        L_0x0239:
            r4 = 0
        L_0x023a:
            int r7 = r10.rowCount
            int r8 = r7 + 1
            r10.rowCount = r8
            r10.infoHeaderRow = r7
            boolean r7 = r10.isBot
            if (r7 != 0) goto L_0x0250
            if (r4 != 0) goto L_0x024a
            if (r6 != 0) goto L_0x0250
        L_0x024a:
            int r4 = r8 + 1
            r10.rowCount = r4
            r10.phoneRow = r8
        L_0x0250:
            org.telegram.tgnet.TLRPC$UserFull r4 = r10.userInfo
            if (r4 == 0) goto L_0x0264
            java.lang.String r4 = r4.about
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x0264
            int r4 = r10.rowCount
            int r6 = r4 + 1
            r10.rowCount = r6
            r10.userInfoRow = r4
        L_0x0264:
            if (r5 == 0) goto L_0x0276
            java.lang.String r4 = r5.username
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x0276
            int r4 = r10.rowCount
            int r6 = r4 + 1
            r10.rowCount = r6
            r10.usernameRow = r4
        L_0x0276:
            int r4 = r10.phoneRow
            if (r4 != r2) goto L_0x0282
            int r4 = r10.userInfoRow
            if (r4 != r2) goto L_0x0282
            int r4 = r10.usernameRow
            if (r4 == r2) goto L_0x028a
        L_0x0282:
            int r4 = r10.rowCount
            int r6 = r4 + 1
            r10.rowCount = r6
            r10.notificationsDividerRow = r4
        L_0x028a:
            long r6 = r10.userId
            org.telegram.messenger.UserConfig r4 = r10.getUserConfig()
            long r8 = r4.getClientUserId()
            int r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r4 == 0) goto L_0x02a0
            int r4 = r10.rowCount
            int r6 = r4 + 1
            r10.rowCount = r6
            r10.notificationsRow = r4
        L_0x02a0:
            int r4 = r10.rowCount
            int r6 = r4 + 1
            r10.rowCount = r6
            r10.infoSectionRow = r4
            org.telegram.tgnet.TLRPC$EncryptedChat r4 = r10.currentEncryptedChat
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChat
            if (r7 == 0) goto L_0x02c0
            int r7 = r6 + 1
            r10.rowCount = r7
            r10.settingsTimerRow = r6
            int r6 = r7 + 1
            r10.rowCount = r6
            r10.settingsKeyRow = r7
            int r7 = r6 + 1
            r10.rowCount = r7
            r10.secretSettingsSectionRow = r6
        L_0x02c0:
            if (r5 == 0) goto L_0x02e8
            boolean r6 = r10.isBot
            if (r6 != 0) goto L_0x02e8
            if (r4 != 0) goto L_0x02e8
            long r6 = r5.id
            org.telegram.messenger.UserConfig r4 = r10.getUserConfig()
            long r8 = r4.getClientUserId()
            int r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r4 == 0) goto L_0x02e8
            boolean r4 = r10.userBlocked
            if (r4 == 0) goto L_0x02e8
            int r4 = r10.rowCount
            int r6 = r4 + 1
            r10.rowCount = r6
            r10.unblockRow = r4
            int r4 = r6 + 1
            r10.rowCount = r4
            r10.lastSectionRow = r6
        L_0x02e8:
            if (r5 == 0) goto L_0x0300
            boolean r4 = r10.isBot
            if (r4 == 0) goto L_0x0300
            boolean r4 = r5.bot_nochats
            if (r4 != 0) goto L_0x0300
            int r4 = r10.rowCount
            int r5 = r4 + 1
            r10.rowCount = r5
            r10.addToGroupButtonRow = r4
            int r4 = r5 + 1
            r10.rowCount = r4
            r10.addToGroupInfoRow = r5
        L_0x0300:
            if (r3 != 0) goto L_0x0329
            org.telegram.tgnet.TLRPC$UserFull r3 = r10.userInfo
            if (r3 == 0) goto L_0x030b
            int r3 = r3.common_chats_count
            if (r3 == 0) goto L_0x030b
            goto L_0x0329
        L_0x030b:
            int r3 = r10.lastSectionRow
            if (r3 != r2) goto L_0x058d
            boolean r3 = r10.needSendMessage
            if (r3 == 0) goto L_0x058d
            int r3 = r10.rowCount
            int r4 = r3 + 1
            r10.rowCount = r4
            r10.sendMessageRow = r3
            int r3 = r4 + 1
            r10.rowCount = r3
            r10.reportRow = r4
            int r4 = r3 + 1
            r10.rowCount = r4
            r10.lastSectionRow = r3
            goto L_0x058d
        L_0x0329:
            int r3 = r10.rowCount
            int r4 = r3 + 1
            r10.rowCount = r4
            r10.sharedMediaRow = r3
            goto L_0x058d
        L_0x0333:
            long r5 = r10.chatId
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 == 0) goto L_0x058d
            org.telegram.tgnet.TLRPC$ChatFull r5 = r10.chatInfo
            if (r5 == 0) goto L_0x034d
            java.lang.String r5 = r5.about
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 == 0) goto L_0x0357
            org.telegram.tgnet.TLRPC$ChatFull r5 = r10.chatInfo
            org.telegram.tgnet.TLRPC$ChannelLocation r5 = r5.location
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC$TL_channelLocation
            if (r5 != 0) goto L_0x0357
        L_0x034d:
            org.telegram.tgnet.TLRPC$Chat r5 = r10.currentChat
            java.lang.String r5 = r5.username
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 != 0) goto L_0x03b9
        L_0x0357:
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 == 0) goto L_0x037b
            org.telegram.tgnet.TLRPC$Chat r5 = r10.currentChat
            boolean r5 = org.telegram.messenger.ChatObject.isChannel(r5)
            if (r5 == 0) goto L_0x037b
            org.telegram.tgnet.TLRPC$ChatFull r5 = r10.chatInfo
            if (r5 == 0) goto L_0x037b
            org.telegram.tgnet.TLRPC$Chat r6 = r10.currentChat
            boolean r6 = r6.megagroup
            if (r6 != 0) goto L_0x037b
            long r5 = r5.linked_chat_id
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 == 0) goto L_0x037b
            int r5 = r10.rowCount
            int r6 = r5 + 1
            r10.rowCount = r6
            r10.emptyRow = r5
        L_0x037b:
            int r5 = r10.rowCount
            int r6 = r5 + 1
            r10.rowCount = r6
            r10.infoHeaderRow = r5
            org.telegram.tgnet.TLRPC$ChatFull r5 = r10.chatInfo
            if (r5 == 0) goto L_0x03a7
            java.lang.String r5 = r5.about
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 != 0) goto L_0x0397
            int r5 = r10.rowCount
            int r6 = r5 + 1
            r10.rowCount = r6
            r10.channelInfoRow = r5
        L_0x0397:
            org.telegram.tgnet.TLRPC$ChatFull r5 = r10.chatInfo
            org.telegram.tgnet.TLRPC$ChannelLocation r5 = r5.location
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC$TL_channelLocation
            if (r5 == 0) goto L_0x03a7
            int r5 = r10.rowCount
            int r6 = r5 + 1
            r10.rowCount = r6
            r10.locationRow = r5
        L_0x03a7:
            org.telegram.tgnet.TLRPC$Chat r5 = r10.currentChat
            java.lang.String r5 = r5.username
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 != 0) goto L_0x03b9
            int r5 = r10.rowCount
            int r6 = r5 + 1
            r10.rowCount = r6
            r10.usernameRow = r5
        L_0x03b9:
            int r5 = r10.infoHeaderRow
            if (r5 == r2) goto L_0x03c5
            int r5 = r10.rowCount
            int r6 = r5 + 1
            r10.rowCount = r6
            r10.notificationsDividerRow = r5
        L_0x03c5:
            int r5 = r10.rowCount
            int r6 = r5 + 1
            r10.rowCount = r6
            r10.notificationsRow = r5
            int r5 = r6 + 1
            r10.rowCount = r5
            r10.infoSectionRow = r6
            org.telegram.tgnet.TLRPC$Chat r5 = r10.currentChat
            boolean r5 = org.telegram.messenger.ChatObject.isChannel(r5)
            if (r5 == 0) goto L_0x0423
            org.telegram.tgnet.TLRPC$Chat r5 = r10.currentChat
            boolean r6 = r5.megagroup
            if (r6 != 0) goto L_0x0423
            org.telegram.tgnet.TLRPC$ChatFull r6 = r10.chatInfo
            if (r6 == 0) goto L_0x0423
            boolean r5 = r5.creator
            if (r5 != 0) goto L_0x03ed
            boolean r5 = r6.can_view_participants
            if (r5 == 0) goto L_0x0423
        L_0x03ed:
            int r5 = r10.rowCount
            int r7 = r5 + 1
            r10.rowCount = r7
            r10.membersHeaderRow = r5
            int r5 = r7 + 1
            r10.rowCount = r5
            r10.subscribersRow = r7
            int r7 = r6.requests_pending
            if (r7 <= 0) goto L_0x0405
            int r7 = r5 + 1
            r10.rowCount = r7
            r10.subscribersRequestsRow = r5
        L_0x0405:
            int r5 = r10.rowCount
            int r7 = r5 + 1
            r10.rowCount = r7
            r10.administratorsRow = r5
            int r5 = r6.banned_count
            if (r5 != 0) goto L_0x0415
            int r5 = r6.kicked_count
            if (r5 == 0) goto L_0x041b
        L_0x0415:
            int r5 = r7 + 1
            r10.rowCount = r5
            r10.blockedUsersRow = r7
        L_0x041b:
            int r5 = r10.rowCount
            int r6 = r5 + 1
            r10.rowCount = r6
            r10.membersSectionRow = r5
        L_0x0423:
            org.telegram.tgnet.TLRPC$Chat r5 = r10.currentChat
            boolean r5 = org.telegram.messenger.ChatObject.isChannel(r5)
            r6 = 5
            r7 = 0
            if (r5 == 0) goto L_0x04f8
            org.telegram.tgnet.TLRPC$ChatFull r5 = r10.chatInfo
            if (r5 == 0) goto L_0x04da
            org.telegram.tgnet.TLRPC$Chat r8 = r10.currentChat
            boolean r8 = r8.megagroup
            if (r8 == 0) goto L_0x04da
            org.telegram.tgnet.TLRPC$ChatParticipants r5 = r5.participants
            if (r5 == 0) goto L_0x04da
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r5 = r5.participants
            boolean r5 = r5.isEmpty()
            if (r5 != 0) goto L_0x04da
            org.telegram.tgnet.TLRPC$Chat r5 = r10.currentChat
            boolean r5 = org.telegram.messenger.ChatObject.isNotInChat(r5)
            if (r5 != 0) goto L_0x0467
            org.telegram.tgnet.TLRPC$Chat r5 = r10.currentChat
            boolean r5 = org.telegram.messenger.ChatObject.canAddUsers(r5)
            if (r5 == 0) goto L_0x0467
            org.telegram.tgnet.TLRPC$ChatFull r5 = r10.chatInfo
            int r5 = r5.participants_count
            org.telegram.messenger.MessagesController r8 = r10.getMessagesController()
            int r8 = r8.maxMegagroupCount
            if (r5 >= r8) goto L_0x0467
            int r5 = r10.rowCount
            int r8 = r5 + 1
            r10.rowCount = r8
            r10.addMemberRow = r5
        L_0x0467:
            org.telegram.tgnet.TLRPC$ChatFull r5 = r10.chatInfo
            org.telegram.tgnet.TLRPC$ChatParticipants r5 = r5.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r5 = r5.participants
            int r5 = r5.size()
            r8 = 2
            if (r5 <= r6) goto L_0x047a
            if (r3 == 0) goto L_0x047a
            int r6 = r10.usersForceShowingIn
            if (r6 != r4) goto L_0x04b7
        L_0x047a:
            int r6 = r10.usersForceShowingIn
            if (r6 == r8) goto L_0x04b7
            int r6 = r10.addMemberRow
            if (r6 != r2) goto L_0x048a
            int r6 = r10.rowCount
            int r8 = r6 + 1
            r10.rowCount = r8
            r10.membersHeaderRow = r6
        L_0x048a:
            int r6 = r10.rowCount
            r10.membersStartRow = r6
            int r6 = r6 + r5
            r10.rowCount = r6
            r10.membersEndRow = r6
            int r5 = r6 + 1
            r10.rowCount = r5
            r10.membersSectionRow = r6
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r5 = r10.visibleChatParticipants
            org.telegram.tgnet.TLRPC$ChatFull r6 = r10.chatInfo
            org.telegram.tgnet.TLRPC$ChatParticipants r6 = r6.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r6 = r6.participants
            r5.addAll(r6)
            java.util.ArrayList<java.lang.Integer> r5 = r10.sortedUsers
            if (r5 == 0) goto L_0x04ad
            java.util.ArrayList<java.lang.Integer> r6 = r10.visibleSortedUsers
            r6.addAll(r5)
        L_0x04ad:
            r10.usersForceShowingIn = r4
            org.telegram.ui.Components.SharedMediaLayout r4 = r10.sharedMediaLayout
            if (r4 == 0) goto L_0x04da
            r4.setChatUsers(r7, r7)
            goto L_0x04da
        L_0x04b7:
            int r4 = r10.addMemberRow
            if (r4 == r2) goto L_0x04c3
            int r4 = r10.rowCount
            int r5 = r4 + 1
            r10.rowCount = r5
            r10.membersSectionRow = r4
        L_0x04c3:
            org.telegram.ui.Components.SharedMediaLayout r4 = r10.sharedMediaLayout
            if (r4 == 0) goto L_0x04da
            java.util.ArrayList<java.lang.Integer> r4 = r10.sortedUsers
            boolean r4 = r4.isEmpty()
            if (r4 != 0) goto L_0x04d1
            r10.usersForceShowingIn = r8
        L_0x04d1:
            org.telegram.ui.Components.SharedMediaLayout r4 = r10.sharedMediaLayout
            java.util.ArrayList<java.lang.Integer> r5 = r10.sortedUsers
            org.telegram.tgnet.TLRPC$ChatFull r6 = r10.chatInfo
            r4.setChatUsers(r5, r6)
        L_0x04da:
            int r4 = r10.lastSectionRow
            if (r4 != r2) goto L_0x0583
            org.telegram.tgnet.TLRPC$Chat r4 = r10.currentChat
            boolean r5 = r4.left
            if (r5 == 0) goto L_0x0583
            boolean r4 = r4.kicked
            if (r4 != 0) goto L_0x0583
            int r4 = r10.rowCount
            int r5 = r4 + 1
            r10.rowCount = r5
            r10.joinRow = r4
            int r4 = r5 + 1
            r10.rowCount = r4
            r10.lastSectionRow = r5
            goto L_0x0583
        L_0x04f8:
            org.telegram.tgnet.TLRPC$ChatFull r4 = r10.chatInfo
            if (r4 == 0) goto L_0x0583
            org.telegram.tgnet.TLRPC$ChatParticipants r4 = r4.participants
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantsForbidden
            if (r4 != 0) goto L_0x0583
            org.telegram.tgnet.TLRPC$Chat r4 = r10.currentChat
            boolean r4 = org.telegram.messenger.ChatObject.canAddUsers(r4)
            if (r4 != 0) goto L_0x0514
            org.telegram.tgnet.TLRPC$Chat r4 = r10.currentChat
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r4.default_banned_rights
            if (r4 == 0) goto L_0x0514
            boolean r4 = r4.invite_users
            if (r4 != 0) goto L_0x051c
        L_0x0514:
            int r4 = r10.rowCount
            int r5 = r4 + 1
            r10.rowCount = r5
            r10.addMemberRow = r4
        L_0x051c:
            org.telegram.tgnet.TLRPC$ChatFull r4 = r10.chatInfo
            org.telegram.tgnet.TLRPC$ChatParticipants r4 = r4.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r4 = r4.participants
            int r4 = r4.size()
            if (r4 <= r6) goto L_0x0543
            if (r3 != 0) goto L_0x052b
            goto L_0x0543
        L_0x052b:
            int r4 = r10.addMemberRow
            if (r4 == r2) goto L_0x0537
            int r4 = r10.rowCount
            int r5 = r4 + 1
            r10.rowCount = r5
            r10.membersSectionRow = r4
        L_0x0537:
            org.telegram.ui.Components.SharedMediaLayout r4 = r10.sharedMediaLayout
            if (r4 == 0) goto L_0x0583
            java.util.ArrayList<java.lang.Integer> r5 = r10.sortedUsers
            org.telegram.tgnet.TLRPC$ChatFull r6 = r10.chatInfo
            r4.setChatUsers(r5, r6)
            goto L_0x0583
        L_0x0543:
            int r4 = r10.addMemberRow
            if (r4 != r2) goto L_0x054f
            int r4 = r10.rowCount
            int r5 = r4 + 1
            r10.rowCount = r5
            r10.membersHeaderRow = r4
        L_0x054f:
            int r4 = r10.rowCount
            r10.membersStartRow = r4
            org.telegram.tgnet.TLRPC$ChatFull r5 = r10.chatInfo
            org.telegram.tgnet.TLRPC$ChatParticipants r5 = r5.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r5 = r5.participants
            int r5 = r5.size()
            int r4 = r4 + r5
            r10.rowCount = r4
            r10.membersEndRow = r4
            int r5 = r4 + 1
            r10.rowCount = r5
            r10.membersSectionRow = r4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r4 = r10.visibleChatParticipants
            org.telegram.tgnet.TLRPC$ChatFull r5 = r10.chatInfo
            org.telegram.tgnet.TLRPC$ChatParticipants r5 = r5.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r5 = r5.participants
            r4.addAll(r5)
            java.util.ArrayList<java.lang.Integer> r4 = r10.sortedUsers
            if (r4 == 0) goto L_0x057c
            java.util.ArrayList<java.lang.Integer> r5 = r10.visibleSortedUsers
            r5.addAll(r4)
        L_0x057c:
            org.telegram.ui.Components.SharedMediaLayout r4 = r10.sharedMediaLayout
            if (r4 == 0) goto L_0x0583
            r4.setChatUsers(r7, r7)
        L_0x0583:
            if (r3 == 0) goto L_0x058d
            int r3 = r10.rowCount
            int r4 = r3 + 1
            r10.rowCount = r4
            r10.sharedMediaRow = r3
        L_0x058d:
            int r3 = r10.sharedMediaRow
            if (r3 != r2) goto L_0x0599
            int r2 = r10.rowCount
            int r3 = r2 + 1
            r10.rowCount = r3
            r10.bottomPaddingRow = r2
        L_0x0599:
            org.telegram.ui.ActionBar.ActionBar r2 = r10.actionBar
            if (r2 == 0) goto L_0x05af
            int r2 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight()
            org.telegram.ui.ActionBar.ActionBar r3 = r10.actionBar
            boolean r3 = r3.getOccupyStatusBar()
            if (r3 == 0) goto L_0x05ac
            int r3 = org.telegram.messenger.AndroidUtilities.statusBarHeight
            goto L_0x05ad
        L_0x05ac:
            r3 = 0
        L_0x05ad:
            int r2 = r2 + r3
            goto L_0x05b0
        L_0x05af:
            r2 = 0
        L_0x05b0:
            org.telegram.ui.Components.RecyclerListView r3 = r10.listView
            if (r3 == 0) goto L_0x05cc
            int r3 = r10.rowCount
            if (r0 > r3) goto L_0x05cc
            int r0 = r10.listContentHeight
            if (r0 == 0) goto L_0x05ce
            int r0 = r0 + r2
            r2 = 1118830592(0x42b00000, float:88.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 + r2
            org.telegram.ui.Components.RecyclerListView r2 = r10.listView
            int r2 = r2.getMeasuredHeight()
            if (r0 >= r2) goto L_0x05ce
        L_0x05cc:
            r10.lastMeasuredContentWidth = r1
        L_0x05ce:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.updateRowsIds():void");
    }

    private Drawable getScamDrawable(int i) {
        if (this.scamDrawable == null) {
            ScamDrawable scamDrawable2 = new ScamDrawable(11, i);
            this.scamDrawable = scamDrawable2;
            scamDrawable2.setColor(getThemedColor("avatar_subtitleInProfileBlue"));
        }
        return this.scamDrawable;
    }

    private Drawable getLockIconDrawable() {
        if (this.lockIconDrawable == null) {
            this.lockIconDrawable = Theme.chat_lockIconDrawable.getConstantState().newDrawable().mutate();
        }
        return this.lockIconDrawable;
    }

    private Drawable getVerifiedCrossfadeDrawable() {
        if (this.verifiedCrossfadeDrawable == null) {
            this.verifiedDrawable = Theme.profile_verifiedDrawable.getConstantState().newDrawable().mutate();
            this.verifiedCheckDrawable = Theme.profile_verifiedCheckDrawable.getConstantState().newDrawable().mutate();
            this.verifiedCrossfadeDrawable = new CrossfadeDrawable(new CombinedDrawable(this.verifiedDrawable, this.verifiedCheckDrawable), ContextCompat.getDrawable(getParentActivity(), R.drawable.verified_profile));
        }
        return this.verifiedCrossfadeDrawable;
    }

    private Drawable getPremiumCrossfadeDrawable() {
        if (this.premuimCrossfadeDrawable == null) {
            Drawable mutate = ContextCompat.getDrawable(getParentActivity(), R.drawable.msg_premium_liststar).mutate();
            this.premiumStarDrawable = mutate;
            mutate.setColorFilter(getThemedColor("profile_verifiedBackground"), PorterDuff.Mode.MULTIPLY);
            this.premuimCrossfadeDrawable = new CrossfadeDrawable(this.premiumStarDrawable, ContextCompat.getDrawable(getParentActivity(), R.drawable.msg_premium_prolfilestar).mutate());
        }
        return this.premuimCrossfadeDrawable;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x023a  */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x023f  */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x0242  */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x02b1  */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x02ec  */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x04f7  */
    /* JADX WARNING: Removed duplicated region for block: B:262:0x05e2 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0609  */
    /* JADX WARNING: Removed duplicated region for block: B:277:0x0618 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:310:0x0714  */
    /* JADX WARNING: Removed duplicated region for block: B:313:0x071b  */
    /* JADX WARNING: Removed duplicated region for block: B:314:0x071e  */
    /* JADX WARNING: Removed duplicated region for block: B:326:0x0765  */
    /* JADX WARNING: Removed duplicated region for block: B:327:0x076a  */
    /* JADX WARNING: Removed duplicated region for block: B:330:0x0770  */
    /* JADX WARNING: Removed duplicated region for block: B:341:0x07ba  */
    /* JADX WARNING: Removed duplicated region for block: B:352:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateProfileData(boolean r35) {
        /*
            r34 = this;
            r0 = r34
            r1 = r35
            android.widget.FrameLayout r2 = r0.avatarContainer
            if (r2 == 0) goto L_0x07bd
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r0.nameTextView
            if (r2 != 0) goto L_0x000e
            goto L_0x07bd
        L_0x000e:
            org.telegram.tgnet.ConnectionsManager r2 = r34.getConnectionsManager()
            int r2 = r2.getConnectionState()
            r3 = 2
            r5 = 1
            if (r2 != r3) goto L_0x0023
            int r2 = org.telegram.messenger.R.string.WaitingForNetwork
            java.lang.String r6 = "WaitingForNetwork"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            goto L_0x0047
        L_0x0023:
            if (r2 != r5) goto L_0x002e
            int r2 = org.telegram.messenger.R.string.Connecting
            java.lang.String r6 = "Connecting"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            goto L_0x0047
        L_0x002e:
            r6 = 5
            if (r2 != r6) goto L_0x003a
            int r2 = org.telegram.messenger.R.string.Updating
            java.lang.String r6 = "Updating"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            goto L_0x0047
        L_0x003a:
            r6 = 4
            if (r2 != r6) goto L_0x0046
            int r2 = org.telegram.messenger.R.string.ConnectingToProxy
            java.lang.String r6 = "ConnectingToProxy"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            goto L_0x0047
        L_0x0046:
            r2 = 0
        L_0x0047:
            long r6 = r0.userId
            java.lang.String r8 = "drawableMuteIcon"
            java.lang.String r9 = "AccDescrVerified"
            java.lang.String r10 = "ScamMessage"
            r11 = 1103101952(0x41CLASSNAME, float:24.0)
            r12 = 0
            r14 = 0
            int r15 = (r6 > r12 ? 1 : (r6 == r12 ? 0 : -1))
            if (r15 == 0) goto L_0x0324
            org.telegram.messenger.MessagesController r6 = r34.getMessagesController()
            long r12 = r0.userId
            java.lang.Long r7 = java.lang.Long.valueOf(r12)
            org.telegram.tgnet.TLRPC$User r6 = r6.getUser(r7)
            if (r6 != 0) goto L_0x0069
            return
        L_0x0069:
            org.telegram.tgnet.TLRPC$UserProfilePhoto r7 = r6.photo
            if (r7 == 0) goto L_0x0070
            org.telegram.tgnet.TLRPC$FileLocation r7 = r7.photo_big
            goto L_0x0071
        L_0x0070:
            r7 = 0
        L_0x0071:
            org.telegram.ui.Components.AvatarDrawable r12 = r0.avatarDrawable
            r12.setInfo((org.telegram.tgnet.TLRPC$User) r6)
            org.telegram.messenger.ImageLocation r12 = org.telegram.messenger.ImageLocation.getForUserOrChat(r6, r14)
            org.telegram.messenger.ImageLocation r13 = org.telegram.messenger.ImageLocation.getForUserOrChat(r6, r5)
            r15 = 3
            org.telegram.messenger.ImageLocation r20 = org.telegram.messenger.ImageLocation.getForUserOrChat(r6, r15)
            org.telegram.ui.Components.ProfileGalleryView r15 = r0.avatarsViewPager
            org.telegram.messenger.ImageLocation r18 = r15.getCurrentVideoLocation(r13, r12)
            org.telegram.ui.Components.ProfileGalleryView r15 = r0.avatarsViewPager
            r15.initIfEmpty(r12, r13, r1)
            org.telegram.tgnet.TLRPC$FileLocation r1 = r0.avatarBig
            if (r1 != 0) goto L_0x00c2
            if (r20 == 0) goto L_0x00af
            org.telegram.ui.ProfileActivity$AvatarImageView r1 = r0.avatarImage
            org.telegram.messenger.ImageReceiver r1 = r1.getImageReceiver()
            r1.setVideoThumbIsSame(r5)
            org.telegram.ui.ProfileActivity$AvatarImageView r1 = r0.avatarImage
            org.telegram.ui.Components.AvatarDrawable r15 = r0.avatarDrawable
            java.lang.String r19 = "g"
            java.lang.String r21 = "avatar"
            r17 = r1
            r22 = r15
            r23 = r6
            r17.setImage((org.telegram.messenger.ImageLocation) r18, (java.lang.String) r19, (org.telegram.messenger.ImageLocation) r20, (java.lang.String) r21, (android.graphics.drawable.Drawable) r22, (java.lang.Object) r23)
            goto L_0x00c2
        L_0x00af:
            org.telegram.ui.ProfileActivity$AvatarImageView r1 = r0.avatarImage
            org.telegram.ui.Components.AvatarDrawable r15 = r0.avatarDrawable
            java.lang.String r19 = "g"
            java.lang.String r21 = "50_50"
            r17 = r1
            r20 = r13
            r22 = r15
            r23 = r6
            r17.setImage((org.telegram.messenger.ImageLocation) r18, (java.lang.String) r19, (org.telegram.messenger.ImageLocation) r20, (java.lang.String) r21, (android.graphics.drawable.Drawable) r22, (java.lang.Object) r23)
        L_0x00c2:
            r1 = -1
            if (r13 == 0) goto L_0x00c9
            int r15 = r0.setAvatarRow
            if (r15 != r1) goto L_0x00cf
        L_0x00c9:
            if (r13 != 0) goto L_0x00d5
            int r13 = r0.setAvatarRow
            if (r13 != r1) goto L_0x00d5
        L_0x00cf:
            r0.updateListAnimated(r14)
            r0.needLayout(r5)
        L_0x00d5:
            if (r12 == 0) goto L_0x00f6
            org.telegram.messenger.ImageLocation r1 = r0.prevLoadedImageLocation
            if (r1 == 0) goto L_0x00e3
            long r3 = r12.photoId
            long r13 = r1.photoId
            int r1 = (r3 > r13 ? 1 : (r3 == r13 ? 0 : -1))
            if (r1 == 0) goto L_0x00f6
        L_0x00e3:
            r0.prevLoadedImageLocation = r12
            org.telegram.messenger.FileLoader r17 = r34.getFileLoader()
            r20 = 0
            r21 = 0
            r22 = 1
            r18 = r12
            r19 = r6
            r17.loadFile(r18, r19, r20, r21, r22)
        L_0x00f6:
            java.lang.String r1 = org.telegram.messenger.UserObject.getUserName(r6)
            long r3 = r6.id
            org.telegram.messenger.UserConfig r12 = r34.getUserConfig()
            long r12 = r12.getClientUserId()
            int r14 = (r3 > r12 ? 1 : (r3 == r12 ? 0 : -1))
            if (r14 != 0) goto L_0x0112
            int r3 = org.telegram.messenger.R.string.Online
            java.lang.String r4 = "Online"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            goto L_0x0183
        L_0x0112:
            long r3 = r6.id
            r12 = 333000(0x514c8, double:1.64524E-318)
            int r14 = (r3 > r12 ? 1 : (r3 == r12 ? 0 : -1))
            if (r14 == 0) goto L_0x017b
            r12 = 777000(0xbdb28, double:3.83889E-318)
            int r14 = (r3 > r12 ? 1 : (r3 == r12 ? 0 : -1))
            if (r14 == 0) goto L_0x017b
            r12 = 42777(0xa719, double:2.11346E-319)
            int r14 = (r3 > r12 ? 1 : (r3 == r12 ? 0 : -1))
            if (r14 != 0) goto L_0x012a
            goto L_0x017b
        L_0x012a:
            boolean r3 = org.telegram.messenger.MessagesController.isSupportUser(r6)
            if (r3 == 0) goto L_0x0139
            int r3 = org.telegram.messenger.R.string.SupportStatus
            java.lang.String r4 = "SupportStatus"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            goto L_0x0183
        L_0x0139:
            boolean r3 = r0.isBot
            if (r3 == 0) goto L_0x0146
            int r3 = org.telegram.messenger.R.string.Bot
            java.lang.String r4 = "Bot"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            goto L_0x0183
        L_0x0146:
            boolean[] r3 = r0.isOnline
            r4 = 0
            r3[r4] = r4
            int r12 = r0.currentAccount
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatUserStatus(r12, r6, r3)
            org.telegram.ui.ActionBar.SimpleTextView[] r12 = r0.onlineTextView
            r13 = r12[r5]
            if (r13 == 0) goto L_0x0183
            boolean r13 = r0.mediaHeaderVisible
            if (r13 != 0) goto L_0x0183
            boolean[] r13 = r0.isOnline
            boolean r13 = r13[r4]
            if (r13 == 0) goto L_0x0164
            java.lang.String r4 = "profile_status"
            goto L_0x0166
        L_0x0164:
            java.lang.String r4 = "avatar_subtitleInProfileBlue"
        L_0x0166:
            r12 = r12[r5]
            r12.setTag(r4)
            boolean r12 = r0.isPulledDown
            if (r12 != 0) goto L_0x0183
            org.telegram.ui.ActionBar.SimpleTextView[] r12 = r0.onlineTextView
            r12 = r12[r5]
            int r4 = r0.getThemedColor(r4)
            r12.setTextColor(r4)
            goto L_0x0183
        L_0x017b:
            int r3 = org.telegram.messenger.R.string.ServiceNotifications
            java.lang.String r4 = "ServiceNotifications"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
        L_0x0183:
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r0.nameTextView     // Catch:{ Exception -> 0x0198 }
            r4 = r4[r5]     // Catch:{ Exception -> 0x0198 }
            android.graphics.Paint r4 = r4.getPaint()     // Catch:{ Exception -> 0x0198 }
            android.graphics.Paint$FontMetricsInt r4 = r4.getFontMetricsInt()     // Catch:{ Exception -> 0x0198 }
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)     // Catch:{ Exception -> 0x0198 }
            r12 = 0
            java.lang.CharSequence r1 = org.telegram.messenger.Emoji.replaceEmoji(r1, r4, r11, r12)     // Catch:{ Exception -> 0x0198 }
        L_0x0198:
            r4 = 0
        L_0x0199:
            r11 = 2
            if (r4 >= r11) goto L_0x0313
            org.telegram.ui.ActionBar.SimpleTextView[] r11 = r0.nameTextView
            r11 = r11[r4]
            if (r11 != 0) goto L_0x01a4
            goto L_0x030f
        L_0x01a4:
            if (r4 != 0) goto L_0x021c
            long r11 = r6.id
            org.telegram.messenger.UserConfig r14 = r34.getUserConfig()
            long r14 = r14.getClientUserId()
            int r16 = (r11 > r14 ? 1 : (r11 == r14 ? 0 : -1))
            if (r16 == 0) goto L_0x021c
            long r11 = r6.id
            r14 = 1000(0x3e8, double:4.94E-321)
            long r16 = r11 / r14
            r18 = 777(0x309, double:3.84E-321)
            int r20 = (r16 > r18 ? 1 : (r16 == r18 ? 0 : -1))
            if (r20 == 0) goto L_0x021c
            long r11 = r11 / r14
            r14 = 333(0x14d, double:1.645E-321)
            int r16 = (r11 > r14 ? 1 : (r11 == r14 ? 0 : -1))
            if (r16 == 0) goto L_0x021c
            java.lang.String r11 = r6.phone
            if (r11 == 0) goto L_0x021c
            int r11 = r11.length()
            if (r11 == 0) goto L_0x021c
            org.telegram.messenger.ContactsController r11 = r34.getContactsController()
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, org.telegram.tgnet.TLRPC$TL_contact> r11 = r11.contactsDict
            long r14 = r6.id
            java.lang.Long r12 = java.lang.Long.valueOf(r14)
            java.lang.Object r11 = r11.get(r12)
            if (r11 != 0) goto L_0x021c
            org.telegram.messenger.ContactsController r11 = r34.getContactsController()
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, org.telegram.tgnet.TLRPC$TL_contact> r11 = r11.contactsDict
            int r11 = r11.size()
            if (r11 != 0) goto L_0x01f9
            org.telegram.messenger.ContactsController r11 = r34.getContactsController()
            boolean r11 = r11.isLoadingContacts()
            if (r11 != 0) goto L_0x021c
        L_0x01f9:
            org.telegram.PhoneFormat.PhoneFormat r11 = org.telegram.PhoneFormat.PhoneFormat.getInstance()
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r14 = "+"
            r12.append(r14)
            java.lang.String r14 = r6.phone
            r12.append(r14)
            java.lang.String r12 = r12.toString()
            java.lang.String r11 = r11.format(r12)
            org.telegram.ui.ActionBar.SimpleTextView[] r12 = r0.nameTextView
            r12 = r12[r4]
            r12.setText(r11)
            goto L_0x0223
        L_0x021c:
            org.telegram.ui.ActionBar.SimpleTextView[] r11 = r0.nameTextView
            r11 = r11[r4]
            r11.setText(r1)
        L_0x0223:
            if (r4 != 0) goto L_0x022f
            if (r2 == 0) goto L_0x022f
            org.telegram.ui.ActionBar.SimpleTextView[] r11 = r0.onlineTextView
            r11 = r11[r4]
            r11.setText(r2)
            goto L_0x0236
        L_0x022f:
            org.telegram.ui.ActionBar.SimpleTextView[] r11 = r0.onlineTextView
            r11 = r11[r4]
            r11.setText(r3)
        L_0x0236:
            org.telegram.tgnet.TLRPC$EncryptedChat r11 = r0.currentEncryptedChat
            if (r11 == 0) goto L_0x023f
            android.graphics.drawable.Drawable r11 = r34.getLockIconDrawable()
            goto L_0x0240
        L_0x023f:
            r11 = 0
        L_0x0240:
            if (r4 != 0) goto L_0x02b1
            boolean r12 = r6.scam
            if (r12 != 0) goto L_0x02a2
            boolean r14 = r6.fake
            if (r14 == 0) goto L_0x024b
            goto L_0x02a2
        L_0x024b:
            boolean r12 = r6.verified
            if (r12 == 0) goto L_0x025d
            android.graphics.drawable.Drawable r12 = r34.getVerifiedCrossfadeDrawable()
            int r14 = org.telegram.messenger.R.string.AccDescrVerified
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r9, r14)
            r0.nameTextViewRightDrawableContentDescription = r14
            goto L_0x02db
        L_0x025d:
            org.telegram.messenger.MessagesController r12 = r34.getMessagesController()
            boolean r12 = r12.isPremiumUser(r6)
            if (r12 == 0) goto L_0x0276
            android.graphics.drawable.Drawable r12 = r34.getPremiumCrossfadeDrawable()
            int r14 = org.telegram.messenger.R.string.AccDescrPremium
            java.lang.String r15 = "AccDescrPremium"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r15, r14)
            r0.nameTextViewRightDrawableContentDescription = r14
            goto L_0x02d1
        L_0x0276:
            org.telegram.messenger.MessagesController r12 = r34.getMessagesController()
            long r14 = r0.dialogId
            r16 = 0
            int r18 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1))
            r19 = r14
            if (r18 == 0) goto L_0x0287
            r13 = r19
            goto L_0x0289
        L_0x0287:
            long r13 = r0.userId
        L_0x0289:
            boolean r12 = r12.isDialogMuted(r13)
            if (r12 == 0) goto L_0x029e
            android.graphics.drawable.Drawable r12 = r0.getThemedDrawable(r8)
            int r13 = org.telegram.messenger.R.string.NotificationsMuted
            java.lang.String r14 = "NotificationsMuted"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r0.nameTextViewRightDrawableContentDescription = r13
            goto L_0x02db
        L_0x029e:
            r12 = 0
            r0.nameTextViewRightDrawableContentDescription = r12
            goto L_0x02d3
        L_0x02a2:
            r12 = r12 ^ 1
            android.graphics.drawable.Drawable r12 = r0.getScamDrawable(r12)
            int r13 = org.telegram.messenger.R.string.ScamMessage
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r10, r13)
            r0.nameTextViewRightDrawableContentDescription = r13
            goto L_0x02db
        L_0x02b1:
            boolean r12 = r6.scam
            if (r12 != 0) goto L_0x02d5
            boolean r13 = r6.fake
            if (r13 == 0) goto L_0x02ba
            goto L_0x02d5
        L_0x02ba:
            boolean r12 = r6.verified
            if (r12 == 0) goto L_0x02c3
            android.graphics.drawable.Drawable r12 = r34.getVerifiedCrossfadeDrawable()
            goto L_0x02db
        L_0x02c3:
            org.telegram.messenger.MessagesController r12 = r34.getMessagesController()
            boolean r12 = r12.isPremiumUser(r6)
            if (r12 == 0) goto L_0x02d3
            android.graphics.drawable.Drawable r12 = r34.getPremiumCrossfadeDrawable()
        L_0x02d1:
            r13 = 1
            goto L_0x02dc
        L_0x02d3:
            r12 = 0
            goto L_0x02db
        L_0x02d5:
            r12 = r12 ^ 1
            android.graphics.drawable.Drawable r12 = r0.getScamDrawable(r12)
        L_0x02db:
            r13 = 0
        L_0x02dc:
            org.telegram.ui.ActionBar.SimpleTextView[] r14 = r0.nameTextView
            r14 = r14[r4]
            r14.setLeftDrawable((android.graphics.drawable.Drawable) r11)
            org.telegram.ui.ActionBar.SimpleTextView[] r11 = r0.nameTextView
            r11 = r11[r4]
            r11.setRightDrawable((android.graphics.drawable.Drawable) r12)
            if (r4 != 0) goto L_0x02f3
            org.telegram.ui.ActionBar.SimpleTextView[] r11 = r0.nameTextView
            r11 = r11[r4]
            r11.setCanHideRightDrawable(r13)
        L_0x02f3:
            boolean r11 = r6.self
            if (r11 != 0) goto L_0x030f
            org.telegram.messenger.MessagesController r11 = r34.getMessagesController()
            boolean r11 = r11.isPremiumUser(r6)
            if (r11 == 0) goto L_0x030f
            org.telegram.ui.ActionBar.SimpleTextView[] r11 = r0.nameTextView
            r12 = r11[r4]
            r11 = r11[r4]
            org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda14 r13 = new org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda14
            r13.<init>(r0, r6, r12)
            r11.setRightDrawableOnClick(r13)
        L_0x030f:
            int r4 = r4 + 1
            goto L_0x0199
        L_0x0313:
            org.telegram.ui.ProfileActivity$AvatarImageView r1 = r0.avatarImage
            org.telegram.messenger.ImageReceiver r1 = r1.getImageReceiver()
            boolean r2 = org.telegram.ui.PhotoViewer.isShowingImage((org.telegram.tgnet.TLRPC$FileLocation) r7)
            r2 = r2 ^ r5
            r3 = 0
            r1.setVisible(r2, r3)
            goto L_0x07b5
        L_0x0324:
            long r3 = r0.chatId
            r6 = 0
            int r12 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1))
            if (r12 == 0) goto L_0x07b5
            org.telegram.messenger.MessagesController r3 = r34.getMessagesController()
            long r6 = r0.chatId
            java.lang.Long r4 = java.lang.Long.valueOf(r6)
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r4)
            if (r3 == 0) goto L_0x033f
            r0.currentChat = r3
            goto L_0x0341
        L_0x033f:
            org.telegram.tgnet.TLRPC$Chat r3 = r0.currentChat
        L_0x0341:
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r3)
            java.lang.String r6 = "MegaPublic"
            java.lang.String r7 = "MegaPrivate"
            java.lang.String r12 = "MegaLocation"
            java.lang.String r14 = "OnlineCount"
            java.lang.String r15 = "%s, %s"
            java.lang.String r13 = "Subscribers"
            java.lang.String r11 = "Members"
            if (r4 == 0) goto L_0x0497
            org.telegram.tgnet.TLRPC$ChatFull r4 = r0.chatInfo
            if (r4 == 0) goto L_0x0462
            org.telegram.tgnet.TLRPC$Chat r5 = r0.currentChat
            r19 = r13
            boolean r13 = r5.megagroup
            if (r13 != 0) goto L_0x0375
            int r4 = r4.participants_count
            if (r4 == 0) goto L_0x0371
            boolean r4 = org.telegram.messenger.ChatObject.hasAdminRights(r5)
            if (r4 != 0) goto L_0x0371
            org.telegram.tgnet.TLRPC$ChatFull r4 = r0.chatInfo
            boolean r4 = r4.can_view_participants
            if (r4 == 0) goto L_0x0375
        L_0x0371:
            r13 = r19
            goto L_0x0462
        L_0x0375:
            org.telegram.tgnet.TLRPC$Chat r4 = r0.currentChat
            boolean r4 = r4.megagroup
            if (r4 == 0) goto L_0x041d
            int r4 = r0.onlineCount
            r5 = 1
            if (r4 <= r5) goto L_0x03d2
            org.telegram.tgnet.TLRPC$ChatFull r4 = r0.chatInfo
            int r4 = r4.participants_count
            if (r4 == 0) goto L_0x03d2
            r5 = 2
            java.lang.Object[] r13 = new java.lang.Object[r5]
            r5 = r13
            r13 = r19
            r1 = 0
            r19 = r2
            java.lang.Object[] r2 = new java.lang.Object[r1]
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r11, r4, r2)
            r5[r1] = r2
            int r2 = r0.onlineCount
            org.telegram.tgnet.TLRPC$ChatFull r4 = r0.chatInfo
            int r4 = r4.participants_count
            int r2 = java.lang.Math.min(r2, r4)
            java.lang.Object[] r4 = new java.lang.Object[r1]
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r14, r2, r4)
            r4 = 1
            r5[r4] = r2
            java.lang.String r2 = java.lang.String.format(r15, r5)
            r4 = 2
            java.lang.Object[] r5 = new java.lang.Object[r4]
            org.telegram.tgnet.TLRPC$ChatFull r4 = r0.chatInfo
            int r4 = r4.participants_count
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralStringComma(r11, r4)
            r5[r1] = r4
            int r1 = r0.onlineCount
            org.telegram.tgnet.TLRPC$ChatFull r4 = r0.chatInfo
            int r4 = r4.participants_count
            int r1 = java.lang.Math.min(r1, r4)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralStringComma(r14, r1)
            r4 = 1
            r5[r4] = r1
            java.lang.String r1 = java.lang.String.format(r15, r5)
            goto L_0x04f2
        L_0x03d2:
            r13 = r19
            r19 = r2
            org.telegram.tgnet.TLRPC$ChatFull r1 = r0.chatInfo
            int r1 = r1.participants_count
            if (r1 != 0) goto L_0x040c
            boolean r1 = r3.has_geo
            if (r1 == 0) goto L_0x03ec
            int r1 = org.telegram.messenger.R.string.MegaLocation
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r12, r1)
            java.lang.String r2 = r1.toLowerCase()
            goto L_0x04a7
        L_0x03ec:
            java.lang.String r1 = r3.username
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0400
            int r1 = org.telegram.messenger.R.string.MegaPublic
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r6, r1)
            java.lang.String r2 = r1.toLowerCase()
            goto L_0x04a7
        L_0x0400:
            int r1 = org.telegram.messenger.R.string.MegaPrivate
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r7, r1)
            java.lang.String r2 = r1.toLowerCase()
            goto L_0x04a7
        L_0x040c:
            r2 = 0
            java.lang.Object[] r4 = new java.lang.Object[r2]
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r11, r1, r4)
            org.telegram.tgnet.TLRPC$ChatFull r1 = r0.chatInfo
            int r1 = r1.participants_count
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralStringComma(r11, r1)
            goto L_0x04f2
        L_0x041d:
            r13 = r19
            r1 = 1
            r19 = r2
            int[] r2 = new int[r1]
            org.telegram.tgnet.TLRPC$ChatFull r1 = r0.chatInfo
            int r1 = r1.participants_count
            org.telegram.messenger.LocaleController.formatShortNumber(r1, r2)
            org.telegram.tgnet.TLRPC$Chat r1 = r0.currentChat
            boolean r1 = r1.megagroup
            if (r1 == 0) goto L_0x0448
            org.telegram.tgnet.TLRPC$ChatFull r1 = r0.chatInfo
            int r1 = r1.participants_count
            r2 = 0
            java.lang.Object[] r4 = new java.lang.Object[r2]
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r11, r1, r4)
            org.telegram.tgnet.TLRPC$ChatFull r4 = r0.chatInfo
            int r4 = r4.participants_count
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralStringComma(r11, r4)
            r2 = r1
            r1 = r4
            goto L_0x04f2
        L_0x0448:
            r2 = 0
            org.telegram.tgnet.TLRPC$ChatFull r1 = r0.chatInfo
            int r1 = r1.participants_count
            java.lang.Object[] r4 = new java.lang.Object[r2]
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r13, r1, r4)
            org.telegram.tgnet.TLRPC$ChatFull r2 = r0.chatInfo
            int r2 = r2.participants_count
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralStringComma(r13, r2)
            r33 = r2
            r2 = r1
            r1 = r33
            goto L_0x04f2
        L_0x0462:
            r19 = r2
            org.telegram.tgnet.TLRPC$Chat r1 = r0.currentChat
            boolean r1 = r1.megagroup
            if (r1 == 0) goto L_0x0477
            int r1 = org.telegram.messenger.R.string.Loading
            java.lang.String r2 = "Loading"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.String r2 = r1.toLowerCase()
            goto L_0x04a7
        L_0x0477:
            int r1 = r3.flags
            r1 = r1 & 64
            if (r1 == 0) goto L_0x048a
            int r1 = org.telegram.messenger.R.string.ChannelPublic
            java.lang.String r2 = "ChannelPublic"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.String r2 = r1.toLowerCase()
            goto L_0x04a7
        L_0x048a:
            int r1 = org.telegram.messenger.R.string.ChannelPrivate
            java.lang.String r2 = "ChannelPrivate"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.String r2 = r1.toLowerCase()
            goto L_0x04a7
        L_0x0497:
            r19 = r2
            boolean r1 = org.telegram.messenger.ChatObject.isKickedFromChat(r3)
            if (r1 == 0) goto L_0x04a9
            int r1 = org.telegram.messenger.R.string.YouWereKicked
            java.lang.String r2 = "YouWereKicked"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r1)
        L_0x04a7:
            r1 = r2
            goto L_0x04f2
        L_0x04a9:
            boolean r1 = org.telegram.messenger.ChatObject.isLeftFromChat(r3)
            if (r1 == 0) goto L_0x04b8
            int r1 = org.telegram.messenger.R.string.YouLeft
            java.lang.String r2 = "YouLeft"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r1)
            goto L_0x04a7
        L_0x04b8:
            int r1 = r3.participants_count
            org.telegram.tgnet.TLRPC$ChatFull r2 = r0.chatInfo
            if (r2 == 0) goto L_0x04c6
            org.telegram.tgnet.TLRPC$ChatParticipants r1 = r2.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r1 = r1.participants
            int r1 = r1.size()
        L_0x04c6:
            if (r1 == 0) goto L_0x04ea
            int r2 = r0.onlineCount
            r4 = 1
            if (r2 <= r4) goto L_0x04ea
            r2 = 2
            java.lang.Object[] r5 = new java.lang.Object[r2]
            r2 = 0
            java.lang.Object[] r4 = new java.lang.Object[r2]
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r11, r1, r4)
            r5[r2] = r1
            int r1 = r0.onlineCount
            java.lang.Object[] r4 = new java.lang.Object[r2]
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r14, r1, r4)
            r4 = 1
            r5[r4] = r1
            java.lang.String r1 = java.lang.String.format(r15, r5)
            r2 = r1
            goto L_0x04f2
        L_0x04ea:
            r2 = 0
            java.lang.Object[] r4 = new java.lang.Object[r2]
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r11, r1, r4)
            goto L_0x04a7
        L_0x04f2:
            r4 = 0
            r5 = 0
        L_0x04f4:
            r14 = 2
            if (r4 >= r14) goto L_0x0711
            org.telegram.ui.ActionBar.SimpleTextView[] r15 = r0.nameTextView
            r17 = r15[r4]
            if (r17 != 0) goto L_0x050f
            r20 = r1
            r21 = r2
            r22 = r6
            r24 = r7
            r25 = r8
            r26 = r9
            r14 = r19
            r16 = 1103101952(0x41CLASSNAME, float:24.0)
            goto L_0x06ff
        L_0x050f:
            java.lang.String r14 = r3.title
            if (r14 == 0) goto L_0x0544
            r15 = r15[r4]     // Catch:{ Exception -> 0x0532 }
            android.graphics.Paint r15 = r15.getPaint()     // Catch:{ Exception -> 0x0532 }
            android.graphics.Paint$FontMetricsInt r15 = r15.getFontMetricsInt()     // Catch:{ Exception -> 0x0532 }
            r20 = r1
            r16 = 1103101952(0x41CLASSNAME, float:24.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r16)     // Catch:{ Exception -> 0x052f }
            r21 = r2
            r2 = 0
            java.lang.CharSequence r14 = org.telegram.messenger.Emoji.replaceEmoji(r14, r15, r1, r2)     // Catch:{ Exception -> 0x052d }
            goto L_0x0538
        L_0x052d:
            goto L_0x0538
        L_0x052f:
            r21 = r2
            goto L_0x0538
        L_0x0532:
            r20 = r1
            r21 = r2
            r16 = 1103101952(0x41CLASSNAME, float:24.0)
        L_0x0538:
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r0.nameTextView
            r1 = r1[r4]
            boolean r1 = r1.setText(r14)
            if (r1 == 0) goto L_0x054a
            r5 = 1
            goto L_0x054a
        L_0x0544:
            r20 = r1
            r21 = r2
            r16 = 1103101952(0x41CLASSNAME, float:24.0)
        L_0x054a:
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r0.nameTextView
            r1 = r1[r4]
            r2 = 0
            r1.setLeftDrawable((android.graphics.drawable.Drawable) r2)
            if (r4 == 0) goto L_0x0596
            boolean r1 = r3.scam
            if (r1 != 0) goto L_0x0580
            boolean r2 = r3.fake
            if (r2 == 0) goto L_0x055d
            goto L_0x0580
        L_0x055d:
            boolean r1 = r3.verified
            if (r1 == 0) goto L_0x0575
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r0.nameTextView
            r1 = r1[r4]
            android.graphics.drawable.Drawable r2 = r34.getVerifiedCrossfadeDrawable()
            r1.setRightDrawable((android.graphics.drawable.Drawable) r2)
            int r1 = org.telegram.messenger.R.string.AccDescrVerified
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r9, r1)
            r0.nameTextViewRightDrawableContentDescription = r1
            goto L_0x05ae
        L_0x0575:
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r0.nameTextView
            r1 = r1[r4]
            r2 = 0
            r1.setRightDrawable((android.graphics.drawable.Drawable) r2)
            r0.nameTextViewRightDrawableContentDescription = r2
            goto L_0x05e0
        L_0x0580:
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r0.nameTextView
            r2 = r2[r4]
            r1 = r1 ^ 1
            android.graphics.drawable.Drawable r1 = r0.getScamDrawable(r1)
            r2.setRightDrawable((android.graphics.drawable.Drawable) r1)
            int r1 = org.telegram.messenger.R.string.ScamMessage
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r10, r1)
            r0.nameTextViewRightDrawableContentDescription = r1
            goto L_0x05ae
        L_0x0596:
            boolean r1 = r3.scam
            if (r1 != 0) goto L_0x05d2
            boolean r2 = r3.fake
            if (r2 == 0) goto L_0x059f
            goto L_0x05d2
        L_0x059f:
            boolean r1 = r3.verified
            if (r1 == 0) goto L_0x05b0
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r0.nameTextView
            r1 = r1[r4]
            android.graphics.drawable.Drawable r2 = r34.getVerifiedCrossfadeDrawable()
            r1.setRightDrawable((android.graphics.drawable.Drawable) r2)
        L_0x05ae:
            r2 = 0
            goto L_0x05e0
        L_0x05b0:
            org.telegram.messenger.MessagesController r1 = r34.getMessagesController()
            long r14 = r0.chatId
            long r14 = -r14
            boolean r1 = r1.isDialogMuted(r14)
            if (r1 == 0) goto L_0x05c9
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r0.nameTextView
            r1 = r1[r4]
            android.graphics.drawable.Drawable r2 = r0.getThemedDrawable(r8)
            r1.setRightDrawable((android.graphics.drawable.Drawable) r2)
            goto L_0x05ae
        L_0x05c9:
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r0.nameTextView
            r1 = r1[r4]
            r2 = 0
            r1.setRightDrawable((android.graphics.drawable.Drawable) r2)
            goto L_0x05e0
        L_0x05d2:
            r2 = 0
            org.telegram.ui.ActionBar.SimpleTextView[] r14 = r0.nameTextView
            r14 = r14[r4]
            r1 = r1 ^ 1
            android.graphics.drawable.Drawable r1 = r0.getScamDrawable(r1)
            r14.setRightDrawable((android.graphics.drawable.Drawable) r1)
        L_0x05e0:
            if (r4 != 0) goto L_0x05f9
            if (r19 == 0) goto L_0x05f9
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r0.onlineTextView
            r1 = r1[r4]
            r14 = r19
            r1.setText(r14)
        L_0x05ed:
            r19 = r5
        L_0x05ef:
            r22 = r6
            r24 = r7
            r25 = r8
            r26 = r9
            goto L_0x06fd
        L_0x05f9:
            r14 = r19
            org.telegram.tgnet.TLRPC$Chat r1 = r0.currentChat
            boolean r15 = r1.megagroup
            if (r15 == 0) goto L_0x0618
            org.telegram.tgnet.TLRPC$ChatFull r15 = r0.chatInfo
            if (r15 == 0) goto L_0x0618
            int r15 = r0.onlineCount
            if (r15 <= 0) goto L_0x0618
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r0.onlineTextView
            r1 = r1[r4]
            if (r4 != 0) goto L_0x0612
            r15 = r21
            goto L_0x0614
        L_0x0612:
            r15 = r20
        L_0x0614:
            r1.setText(r15)
            goto L_0x05ed
        L_0x0618:
            if (r4 != 0) goto L_0x06e5
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r1 == 0) goto L_0x06e5
            org.telegram.tgnet.TLRPC$ChatFull r1 = r0.chatInfo
            if (r1 == 0) goto L_0x06e5
            int r1 = r1.participants_count
            if (r1 == 0) goto L_0x06e5
            org.telegram.tgnet.TLRPC$Chat r15 = r0.currentChat
            boolean r2 = r15.megagroup
            if (r2 != 0) goto L_0x0632
            boolean r2 = r15.broadcast
            if (r2 == 0) goto L_0x06e5
        L_0x0632:
            r2 = 1
            int[] r15 = new int[r2]
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatShortNumber(r1, r15)
            org.telegram.tgnet.TLRPC$Chat r2 = r0.currentChat
            boolean r2 = r2.megagroup
            r19 = r5
            java.lang.String r5 = "%d"
            if (r2 == 0) goto L_0x06b9
            org.telegram.tgnet.TLRPC$ChatFull r2 = r0.chatInfo
            int r2 = r2.participants_count
            if (r2 != 0) goto L_0x068d
            boolean r1 = r3.has_geo
            if (r1 == 0) goto L_0x065f
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r0.onlineTextView
            r1 = r1[r4]
            int r2 = org.telegram.messenger.R.string.MegaLocation
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r12, r2)
            java.lang.String r2 = r2.toLowerCase()
            r1.setText(r2)
            goto L_0x05ef
        L_0x065f:
            java.lang.String r1 = r3.username
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x067a
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r0.onlineTextView
            r1 = r1[r4]
            int r2 = org.telegram.messenger.R.string.MegaPublic
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            java.lang.String r2 = r2.toLowerCase()
            r1.setText(r2)
            goto L_0x05ef
        L_0x067a:
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r0.onlineTextView
            r1 = r1[r4]
            int r2 = org.telegram.messenger.R.string.MegaPrivate
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r7, r2)
            java.lang.String r2 = r2.toLowerCase()
            r1.setText(r2)
            goto L_0x05ef
        L_0x068d:
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r0.onlineTextView
            r2 = r2[r4]
            r22 = r6
            r24 = r7
            r6 = 0
            r7 = r15[r6]
            r25 = r8
            java.lang.Object[] r8 = new java.lang.Object[r6]
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r11, r7, r8)
            r26 = r9
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]
            r8 = r15[r6]
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r9[r6] = r8
            java.lang.String r5 = java.lang.String.format(r5, r9)
            java.lang.String r1 = r7.replace(r5, r1)
            r2.setText(r1)
            goto L_0x06fd
        L_0x06b9:
            r22 = r6
            r24 = r7
            r25 = r8
            r26 = r9
            r6 = 0
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r0.onlineTextView
            r2 = r2[r4]
            r7 = r15[r6]
            java.lang.Object[] r8 = new java.lang.Object[r6]
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r13, r7, r8)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]
            r8 = r15[r6]
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r9[r6] = r8
            java.lang.String r5 = java.lang.String.format(r5, r9)
            java.lang.String r1 = r7.replace(r5, r1)
            r2.setText(r1)
            goto L_0x06fd
        L_0x06e5:
            r19 = r5
            r22 = r6
            r24 = r7
            r25 = r8
            r26 = r9
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r0.onlineTextView
            r1 = r1[r4]
            if (r4 != 0) goto L_0x06f8
            r2 = r21
            goto L_0x06fa
        L_0x06f8:
            r2 = r20
        L_0x06fa:
            r1.setText(r2)
        L_0x06fd:
            r5 = r19
        L_0x06ff:
            int r4 = r4 + 1
            r19 = r14
            r1 = r20
            r2 = r21
            r6 = r22
            r7 = r24
            r8 = r25
            r9 = r26
            goto L_0x04f4
        L_0x0711:
            r1 = 1
            if (r5 == 0) goto L_0x0717
            r0.needLayout(r1)
        L_0x0717:
            org.telegram.tgnet.TLRPC$ChatPhoto r2 = r3.photo
            if (r2 == 0) goto L_0x071e
            org.telegram.tgnet.TLRPC$FileLocation r12 = r2.photo_big
            goto L_0x071f
        L_0x071e:
            r12 = 0
        L_0x071f:
            org.telegram.ui.Components.AvatarDrawable r2 = r0.avatarDrawable
            r2.setInfo((org.telegram.tgnet.TLRPC$Chat) r3)
            r2 = 0
            org.telegram.messenger.ImageLocation r4 = org.telegram.messenger.ImageLocation.getForUserOrChat(r3, r2)
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForUserOrChat(r3, r1)
            org.telegram.ui.Components.ProfileGalleryView r1 = r0.avatarsViewPager
            org.telegram.messenger.ImageLocation r1 = r1.getCurrentVideoLocation(r2, r4)
            org.telegram.ui.Components.ProfileGalleryView r5 = r0.avatarsViewPager
            r6 = r35
            boolean r5 = r5.initIfEmpty(r4, r2, r6)
            if (r4 == 0) goto L_0x073f
            if (r5 == 0) goto L_0x075e
        L_0x073f:
            boolean r5 = r0.isPulledDown
            if (r5 == 0) goto L_0x075e
            androidx.recyclerview.widget.LinearLayoutManager r5 = r0.layoutManager
            r6 = 0
            android.view.View r5 = r5.findViewByPosition(r6)
            if (r5 == 0) goto L_0x075e
            org.telegram.ui.Components.RecyclerListView r7 = r0.listView
            int r5 = r5.getTop()
            r8 = 1118830592(0x42b00000, float:88.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r5 = r5 - r8
            org.telegram.ui.Components.CubicBezierInterpolator r8 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            r7.smoothScrollBy(r6, r5, r8)
        L_0x075e:
            if (r1 == 0) goto L_0x076a
            int r5 = r1.imageType
            r6 = 2
            if (r5 != r6) goto L_0x076a
            java.lang.String r5 = "g"
            r28 = r5
            goto L_0x076c
        L_0x076a:
            r28 = 0
        L_0x076c:
            org.telegram.tgnet.TLRPC$FileLocation r5 = r0.avatarBig
            if (r5 != 0) goto L_0x0783
            org.telegram.ui.ProfileActivity$AvatarImageView r5 = r0.avatarImage
            org.telegram.ui.Components.AvatarDrawable r6 = r0.avatarDrawable
            java.lang.String r30 = "50_50"
            r26 = r5
            r27 = r1
            r29 = r2
            r31 = r6
            r32 = r3
            r26.setImage((org.telegram.messenger.ImageLocation) r27, (java.lang.String) r28, (org.telegram.messenger.ImageLocation) r29, (java.lang.String) r30, (android.graphics.drawable.Drawable) r31, (java.lang.Object) r32)
        L_0x0783:
            if (r4 == 0) goto L_0x07a4
            org.telegram.messenger.ImageLocation r1 = r0.prevLoadedImageLocation
            if (r1 == 0) goto L_0x0791
            long r5 = r4.photoId
            long r1 = r1.photoId
            int r7 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r7 == 0) goto L_0x07a4
        L_0x0791:
            r0.prevLoadedImageLocation = r4
            org.telegram.messenger.FileLoader r26 = r34.getFileLoader()
            r29 = 0
            r30 = 0
            r31 = 1
            r27 = r4
            r28 = r3
            r26.loadFile(r27, r28, r29, r30, r31)
        L_0x07a4:
            org.telegram.ui.ProfileActivity$AvatarImageView r1 = r0.avatarImage
            org.telegram.messenger.ImageReceiver r1 = r1.getImageReceiver()
            boolean r2 = org.telegram.ui.PhotoViewer.isShowingImage((org.telegram.tgnet.TLRPC$FileLocation) r12)
            r3 = 1
            r2 = r2 ^ r3
            r4 = 0
            r1.setVisible(r2, r4)
            goto L_0x07b6
        L_0x07b5:
            r3 = 1
        L_0x07b6:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.qrItem
            if (r1 == 0) goto L_0x07bd
            r0.updateQrItemVisibility(r3)
        L_0x07bd:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.updateProfileData(boolean):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateProfileData$32(TLRPC$User tLRPC$User, SimpleTextView simpleTextView, View view) {
        PremiumPreviewBottomSheet premiumPreviewBottomSheet = new PremiumPreviewBottomSheet(this, this.currentAccount, tLRPC$User);
        simpleTextView.getLocationOnScreen(new int[2]);
        premiumPreviewBottomSheet.startEnterFromX = (float) simpleTextView.rightDrawableX;
        premiumPreviewBottomSheet.startEnterFromY = (float) simpleTextView.rightDrawableY;
        premiumPreviewBottomSheet.startEnterFromScale = simpleTextView.getScaleX();
        premiumPreviewBottomSheet.startEnterFromX1 = (float) simpleTextView.getLeft();
        premiumPreviewBottomSheet.startEnterFromY1 = (float) simpleTextView.getTop();
        premiumPreviewBottomSheet.startEnterFromView = simpleTextView;
        showDialog(premiumPreviewBottomSheet);
    }

    /* JADX WARNING: Removed duplicated region for block: B:181:0x0387  */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x03c2  */
    /* JADX WARNING: Removed duplicated region for block: B:185:0x03db  */
    /* JADX WARNING: Removed duplicated region for block: B:187:0x03e2  */
    /* JADX WARNING: Removed duplicated region for block: B:190:0x03f7  */
    /* JADX WARNING: Removed duplicated region for block: B:193:0x041a  */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x04d0  */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x04e7  */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x04fe  */
    /* JADX WARNING: Removed duplicated region for block: B:238:0x0515  */
    /* JADX WARNING: Removed duplicated region for block: B:241:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void createActionBarMenu(boolean r17) {
        /*
            r16 = this;
            r0 = r16
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            if (r1 == 0) goto L_0x051c
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.otherItem
            if (r2 != 0) goto L_0x000c
            goto L_0x051c
        L_0x000c:
            android.content.Context r1 = r1.getContext()
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.otherItem
            r2.removeAllSubItems()
            r2 = 0
            r0.animatingItem = r2
            r2 = 0
            r0.editItemVisible = r2
            r0.callItemVisible = r2
            r0.videoCallItemVisible = r2
            r0.canSearchMembers = r2
            long r3 = r0.userId
            r5 = 10
            java.lang.String r6 = "AddShortcut"
            r7 = 14
            r8 = 0
            r10 = 1
            int r11 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
            if (r11 == 0) goto L_0x01f1
            org.telegram.messenger.MessagesController r3 = r16.getMessagesController()
            long r8 = r0.userId
            java.lang.Long r4 = java.lang.Long.valueOf(r8)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            if (r3 != 0) goto L_0x0041
            return
        L_0x0041:
            boolean r4 = org.telegram.messenger.UserObject.isUserSelf(r3)
            if (r4 == 0) goto L_0x005a
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r3 = 30
            int r4 = org.telegram.messenger.R.drawable.msg_edit
            int r5 = org.telegram.messenger.R.string.EditName
            java.lang.String r6 = "EditName"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r1.addSubItem(r3, r4, r5)
            goto L_0x0379
        L_0x005a:
            org.telegram.tgnet.TLRPC$UserFull r4 = r0.userInfo
            if (r4 == 0) goto L_0x0073
            boolean r8 = r4.phone_calls_available
            if (r8 == 0) goto L_0x0073
            r0.callItemVisible = r10
            int r8 = android.os.Build.VERSION.SDK_INT
            r9 = 18
            if (r8 < r9) goto L_0x0070
            boolean r4 = r4.video_calls_available
            if (r4 == 0) goto L_0x0070
            r4 = 1
            goto L_0x0071
        L_0x0070:
            r4 = 0
        L_0x0071:
            r0.videoCallItemVisible = r4
        L_0x0073:
            boolean r4 = r0.isBot
            java.lang.String r8 = "Unblock"
            r9 = 2
            if (r4 != 0) goto L_0x00e4
            org.telegram.messenger.ContactsController r4 = r16.getContactsController()
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, org.telegram.tgnet.TLRPC$TL_contact> r4 = r4.contactsDict
            long r11 = r0.userId
            java.lang.Long r11 = java.lang.Long.valueOf(r11)
            java.lang.Object r4 = r4.get(r11)
            if (r4 != 0) goto L_0x008d
            goto L_0x00e4
        L_0x008d:
            org.telegram.tgnet.TLRPC$EncryptedChat r4 = r0.currentEncryptedChat
            if (r4 != 0) goto L_0x0094
            r0.createAutoDeleteItem(r1)
        L_0x0094:
            java.lang.String r1 = r3.phone
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x00ac
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r4 = 3
            int r5 = org.telegram.messenger.R.drawable.msg_share
            int r10 = org.telegram.messenger.R.string.ShareContact
            java.lang.String r11 = "ShareContact"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r1.addSubItem(r4, r5, r10)
        L_0x00ac:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            boolean r4 = r0.userBlocked
            int r5 = org.telegram.messenger.R.drawable.msg_block
            if (r4 != 0) goto L_0x00b9
            int r4 = org.telegram.messenger.R.string.BlockContact
            java.lang.String r8 = "BlockContact"
            goto L_0x00bb
        L_0x00b9:
            int r4 = org.telegram.messenger.R.string.Unblock
        L_0x00bb:
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r8, r4)
            r1.addSubItem(r9, r5, r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r4 = 4
            int r5 = org.telegram.messenger.R.drawable.msg_edit
            int r8 = org.telegram.messenger.R.string.EditContact
            java.lang.String r9 = "EditContact"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r1.addSubItem(r4, r5, r8)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r4 = 5
            int r5 = org.telegram.messenger.R.drawable.msg_delete
            int r8 = org.telegram.messenger.R.string.DeleteContact
            java.lang.String r9 = "DeleteContact"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r1.addSubItem(r4, r5, r8)
            goto L_0x0177
        L_0x00e4:
            boolean r4 = org.telegram.messenger.MessagesController.isSupportUser(r3)
            if (r4 == 0) goto L_0x00fd
            boolean r1 = r0.userBlocked
            if (r1 == 0) goto L_0x0177
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            int r4 = org.telegram.messenger.R.drawable.msg_block
            int r5 = org.telegram.messenger.R.string.Unblock
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r8, r5)
            r1.addSubItem(r9, r4, r5)
            goto L_0x0177
        L_0x00fd:
            org.telegram.tgnet.TLRPC$EncryptedChat r4 = r0.currentEncryptedChat
            if (r4 != 0) goto L_0x0104
            r0.createAutoDeleteItem(r1)
        L_0x0104:
            boolean r1 = r0.isBot
            if (r1 == 0) goto L_0x0118
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            int r4 = org.telegram.messenger.R.drawable.msg_share
            int r10 = org.telegram.messenger.R.string.BotShare
            java.lang.String r11 = "BotShare"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r1.addSubItem(r5, r4, r10)
            goto L_0x0127
        L_0x0118:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            int r4 = org.telegram.messenger.R.drawable.msg_addcontact
            int r5 = org.telegram.messenger.R.string.AddContact
            java.lang.String r11 = "AddContact"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r11, r5)
            r1.addSubItem(r10, r4, r5)
        L_0x0127:
            java.lang.String r1 = r3.phone
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x013f
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r4 = 3
            int r5 = org.telegram.messenger.R.drawable.msg_share
            int r10 = org.telegram.messenger.R.string.ShareContact
            java.lang.String r11 = "ShareContact"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r1.addSubItem(r4, r5, r10)
        L_0x013f:
            boolean r1 = r0.isBot
            if (r1 == 0) goto L_0x0161
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            boolean r4 = r0.userBlocked
            if (r4 != 0) goto L_0x014c
            int r5 = org.telegram.messenger.R.drawable.msg_block
            goto L_0x014e
        L_0x014c:
            int r5 = org.telegram.messenger.R.drawable.msg_retry
        L_0x014e:
            if (r4 != 0) goto L_0x0155
            int r4 = org.telegram.messenger.R.string.BotStop
            java.lang.String r8 = "BotStop"
            goto L_0x0159
        L_0x0155:
            int r4 = org.telegram.messenger.R.string.BotRestart
            java.lang.String r8 = "BotRestart"
        L_0x0159:
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r8, r4)
            r1.addSubItem(r9, r5, r4)
            goto L_0x0177
        L_0x0161:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            boolean r4 = r0.userBlocked
            int r5 = org.telegram.messenger.R.drawable.msg_block
            if (r4 != 0) goto L_0x016e
            int r4 = org.telegram.messenger.R.string.BlockContact
            java.lang.String r8 = "BlockContact"
            goto L_0x0170
        L_0x016e:
            int r4 = org.telegram.messenger.R.string.Unblock
        L_0x0170:
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r8, r4)
            r1.addSubItem(r9, r5, r4)
        L_0x0177:
            boolean r1 = org.telegram.messenger.UserObject.isDeleted(r3)
            if (r1 != 0) goto L_0x01e2
            boolean r1 = r0.isBot
            if (r1 != 0) goto L_0x01e2
            org.telegram.tgnet.TLRPC$EncryptedChat r1 = r0.currentEncryptedChat
            if (r1 != 0) goto L_0x01e2
            boolean r1 = r0.userBlocked
            if (r1 != 0) goto L_0x01e2
            long r4 = r0.userId
            r8 = 333000(0x514c8, double:1.64524E-318)
            int r1 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
            if (r1 == 0) goto L_0x01e2
            r8 = 777000(0xbdb28, double:3.83889E-318)
            int r1 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
            if (r1 == 0) goto L_0x01e2
            r8 = 42777(0xa719, double:2.11346E-319)
            int r1 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
            if (r1 == 0) goto L_0x01e2
            boolean r1 = r3.premium
            if (r1 != 0) goto L_0x01d1
            boolean r1 = org.telegram.messenger.BuildVars.IS_BILLING_UNAVAILABLE
            if (r1 != 0) goto L_0x01d1
            boolean r1 = r3.self
            if (r1 != 0) goto L_0x01d1
            org.telegram.tgnet.TLRPC$UserFull r1 = r0.userInfo
            if (r1 == 0) goto L_0x01d1
            org.telegram.messenger.MessagesController r1 = r16.getMessagesController()
            boolean r1 = r1.premiumLocked
            if (r1 != 0) goto L_0x01d1
            org.telegram.tgnet.TLRPC$UserFull r1 = r0.userInfo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_premiumGiftOption> r1 = r1.premium_gifts
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x01d1
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r3 = 38
            int r4 = org.telegram.messenger.R.drawable.msg_gift_premium
            int r5 = org.telegram.messenger.R.string.GiftPremium
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString((int) r5)
            r1.addSubItem(r3, r4, r5)
        L_0x01d1:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r3 = 20
            int r4 = org.telegram.messenger.R.drawable.msg_secret
            int r5 = org.telegram.messenger.R.string.StartEncryptedChat
            java.lang.String r8 = "StartEncryptedChat"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r8, r5)
            r1.addSubItem(r3, r4, r5)
        L_0x01e2:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            int r3 = org.telegram.messenger.R.drawable.msg_home
            int r4 = org.telegram.messenger.R.string.AddShortcut
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r1.addSubItem(r7, r3, r4)
            goto L_0x0378
        L_0x01f1:
            long r3 = r0.chatId
            int r11 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
            if (r11 == 0) goto L_0x0378
            org.telegram.messenger.MessagesController r3 = r16.getMessagesController()
            long r11 = r0.chatId
            java.lang.Long r4 = java.lang.Long.valueOf(r11)
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r4)
            r0.hasVoiceChatItem = r2
            r4 = 13
            boolean r4 = org.telegram.messenger.ChatObject.canUserDoAdminAction(r3, r4)
            if (r4 == 0) goto L_0x0212
            r0.createAutoDeleteItem(r1)
        L_0x0212:
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r3)
            java.lang.String r4 = "SearchMembers"
            r11 = 17
            java.lang.String r12 = "StartVoipChat"
            r13 = 15
            r14 = 7
            if (r1 == 0) goto L_0x0307
            boolean r1 = org.telegram.messenger.ChatObject.hasAdminRights(r3)
            if (r1 != 0) goto L_0x0231
            boolean r1 = r3.megagroup
            if (r1 == 0) goto L_0x0233
            boolean r1 = org.telegram.messenger.ChatObject.canChangeChatInfo(r3)
            if (r1 == 0) goto L_0x0233
        L_0x0231:
            r0.editItemVisible = r10
        L_0x0233:
            org.telegram.tgnet.TLRPC$ChatFull r1 = r0.chatInfo
            if (r1 == 0) goto L_0x0287
            boolean r1 = org.telegram.messenger.ChatObject.canManageCalls(r3)
            if (r1 == 0) goto L_0x025f
            org.telegram.tgnet.TLRPC$ChatFull r1 = r0.chatInfo
            org.telegram.tgnet.TLRPC$TL_inputGroupCall r1 = r1.call
            if (r1 != 0) goto L_0x025f
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            int r15 = org.telegram.messenger.R.drawable.msg_voicechat
            boolean r7 = r3.megagroup
            if (r7 == 0) goto L_0x0252
            boolean r7 = r3.gigagroup
            if (r7 != 0) goto L_0x0252
            int r7 = org.telegram.messenger.R.string.StartVoipChat
            goto L_0x0256
        L_0x0252:
            int r7 = org.telegram.messenger.R.string.StartVoipChannel
            java.lang.String r12 = "StartVoipChannel"
        L_0x0256:
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r12, r7)
            r1.addSubItem(r13, r15, r7)
            r0.hasVoiceChatItem = r10
        L_0x025f:
            org.telegram.tgnet.TLRPC$ChatFull r1 = r0.chatInfo
            boolean r1 = r1.can_view_stats
            if (r1 == 0) goto L_0x0276
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r7 = 19
            int r12 = org.telegram.messenger.R.drawable.msg_stats
            int r13 = org.telegram.messenger.R.string.Statistics
            java.lang.String r15 = "Statistics"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r15, r13)
            r1.addSubItem(r7, r12, r13)
        L_0x0276:
            org.telegram.messenger.MessagesController r1 = r16.getMessagesController()
            long r12 = r0.chatId
            org.telegram.messenger.ChatObject$Call r1 = r1.getGroupCall(r12, r2)
            if (r1 == 0) goto L_0x0284
            r1 = 1
            goto L_0x0285
        L_0x0284:
            r1 = 0
        L_0x0285:
            r0.callItemVisible = r1
        L_0x0287:
            boolean r1 = r3.megagroup
            if (r1 == 0) goto L_0x02b7
            r0.canSearchMembers = r10
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            int r5 = org.telegram.messenger.R.drawable.msg_search
            int r7 = org.telegram.messenger.R.string.SearchMembers
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r7)
            r1.addSubItem(r11, r5, r4)
            boolean r1 = r3.creator
            if (r1 != 0) goto L_0x0369
            boolean r1 = r3.left
            if (r1 != 0) goto L_0x0369
            boolean r1 = r3.kicked
            if (r1 != 0) goto L_0x0369
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            int r3 = org.telegram.messenger.R.drawable.msg_leave
            int r4 = org.telegram.messenger.R.string.LeaveMegaMenu
            java.lang.String r5 = "LeaveMegaMenu"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r1.addSubItem(r14, r3, r4)
            goto L_0x0369
        L_0x02b7:
            java.lang.String r1 = r3.username
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x02ce
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            int r3 = org.telegram.messenger.R.drawable.msg_share
            int r4 = org.telegram.messenger.R.string.BotShare
            java.lang.String r7 = "BotShare"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r1.addSubItem(r5, r3, r4)
        L_0x02ce:
            org.telegram.tgnet.TLRPC$ChatFull r1 = r0.chatInfo
            if (r1 == 0) goto L_0x02e9
            long r3 = r1.linked_chat_id
            int r1 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
            if (r1 == 0) goto L_0x02e9
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r3 = 22
            int r4 = org.telegram.messenger.R.drawable.msg_discussion
            int r5 = org.telegram.messenger.R.string.ViewDiscussion
            java.lang.String r7 = "ViewDiscussion"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r5)
            r1.addSubItem(r3, r4, r5)
        L_0x02e9:
            org.telegram.tgnet.TLRPC$Chat r1 = r0.currentChat
            boolean r3 = r1.creator
            if (r3 != 0) goto L_0x0369
            boolean r3 = r1.left
            if (r3 != 0) goto L_0x0369
            boolean r1 = r1.kicked
            if (r1 != 0) goto L_0x0369
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            int r3 = org.telegram.messenger.R.drawable.msg_leave
            int r4 = org.telegram.messenger.R.string.LeaveChannelMenu
            java.lang.String r5 = "LeaveChannelMenu"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r1.addSubItem(r14, r3, r4)
            goto L_0x0369
        L_0x0307:
            org.telegram.tgnet.TLRPC$ChatFull r1 = r0.chatInfo
            if (r1 == 0) goto L_0x0337
            boolean r1 = org.telegram.messenger.ChatObject.canManageCalls(r3)
            if (r1 == 0) goto L_0x0326
            org.telegram.tgnet.TLRPC$ChatFull r1 = r0.chatInfo
            org.telegram.tgnet.TLRPC$TL_inputGroupCall r1 = r1.call
            if (r1 != 0) goto L_0x0326
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            int r5 = org.telegram.messenger.R.drawable.msg_voicechat
            int r7 = org.telegram.messenger.R.string.StartVoipChat
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r12, r7)
            r1.addSubItem(r13, r5, r7)
            r0.hasVoiceChatItem = r10
        L_0x0326:
            org.telegram.messenger.MessagesController r1 = r16.getMessagesController()
            long r7 = r0.chatId
            org.telegram.messenger.ChatObject$Call r1 = r1.getGroupCall(r7, r2)
            if (r1 == 0) goto L_0x0334
            r1 = 1
            goto L_0x0335
        L_0x0334:
            r1 = 0
        L_0x0335:
            r0.callItemVisible = r1
        L_0x0337:
            boolean r1 = org.telegram.messenger.ChatObject.canChangeChatInfo(r3)
            if (r1 == 0) goto L_0x033f
            r0.editItemVisible = r10
        L_0x033f:
            boolean r1 = org.telegram.messenger.ChatObject.isKickedFromChat(r3)
            if (r1 != 0) goto L_0x035a
            boolean r1 = org.telegram.messenger.ChatObject.isLeftFromChat(r3)
            if (r1 != 0) goto L_0x035a
            r0.canSearchMembers = r10
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            int r3 = org.telegram.messenger.R.drawable.msg_search
            int r5 = org.telegram.messenger.R.string.SearchMembers
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r5)
            r1.addSubItem(r11, r3, r4)
        L_0x035a:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            int r3 = org.telegram.messenger.R.drawable.msg_leave
            int r4 = org.telegram.messenger.R.string.DeleteAndExit
            java.lang.String r5 = "DeleteAndExit"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r1.addSubItem(r14, r3, r4)
        L_0x0369:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            int r3 = org.telegram.messenger.R.drawable.msg_home
            int r4 = org.telegram.messenger.R.string.AddShortcut
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r5 = 14
            r1.addSubItem(r5, r3, r4)
        L_0x0378:
            r10 = 0
        L_0x0379:
            org.telegram.ui.Components.ImageUpdater r1 = r0.imageUpdater
            r3 = 35
            r4 = 33
            r5 = 36
            java.lang.String r6 = "SaveToGallery"
            r7 = 21
            if (r1 == 0) goto L_0x03c2
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            int r8 = org.telegram.messenger.R.drawable.msg_addphoto
            int r9 = org.telegram.messenger.R.string.AddPhoto
            java.lang.String r11 = "AddPhoto"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
            r1.addSubItem(r5, r8, r9)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            int r8 = org.telegram.messenger.R.drawable.msg_openprofile
            int r9 = org.telegram.messenger.R.string.SetAsMain
            java.lang.String r11 = "SetAsMain"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
            r1.addSubItem(r4, r8, r9)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            int r8 = org.telegram.messenger.R.drawable.msg_gallery
            int r9 = org.telegram.messenger.R.string.SaveToGallery
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r9)
            r1.addSubItem(r7, r8, r6)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            int r6 = org.telegram.messenger.R.drawable.msg_delete
            int r8 = org.telegram.messenger.R.string.Delete
            java.lang.String r9 = "Delete"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r1.addSubItem(r3, r6, r8)
            goto L_0x03cf
        L_0x03c2:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            int r8 = org.telegram.messenger.R.drawable.msg_gallery
            int r9 = org.telegram.messenger.R.string.SaveToGallery
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r9)
            r1.addSubItem(r7, r8, r6)
        L_0x03cf:
            org.telegram.messenger.MessagesController r1 = r16.getMessagesController()
            org.telegram.tgnet.TLRPC$Chat r6 = r0.currentChat
            boolean r1 = r1.isChatNoForwards((org.telegram.tgnet.TLRPC$Chat) r6)
            if (r1 == 0) goto L_0x03e0
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r1.hideSubItem(r7)
        L_0x03e0:
            if (r10 == 0) goto L_0x03f3
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r6 = 31
            int r8 = org.telegram.messenger.R.drawable.msg_leave
            int r9 = org.telegram.messenger.R.string.LogOut
            java.lang.String r10 = "LogOut"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r1.addSubItem(r6, r8, r9)
        L_0x03f3:
            boolean r1 = r0.isPulledDown
            if (r1 != 0) goto L_0x0412
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r1.hideSubItem(r7)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r1.hideSubItem(r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r1.showSubItem(r5)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r4 = 34
            r1.hideSubItem(r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r1.hideSubItem(r3)
        L_0x0412:
            boolean r1 = r0.mediaHeaderVisible
            r3 = 1065353216(0x3var_, float:1.0)
            r4 = 8
            if (r1 != 0) goto L_0x04c2
            boolean r1 = r0.callItemVisible
            r5 = 150(0x96, double:7.4E-322)
            r7 = 0
            if (r1 == 0) goto L_0x0447
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.callItem
            int r1 = r1.getVisibility()
            if (r1 == 0) goto L_0x0454
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.callItem
            r1.setVisibility(r2)
            if (r17 == 0) goto L_0x0454
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.callItem
            r1.setAlpha(r7)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.callItem
            android.view.ViewPropertyAnimator r1 = r1.animate()
            android.view.ViewPropertyAnimator r1 = r1.alpha(r3)
            android.view.ViewPropertyAnimator r1 = r1.setDuration(r5)
            r1.start()
            goto L_0x0454
        L_0x0447:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.callItem
            int r1 = r1.getVisibility()
            if (r1 == r4) goto L_0x0454
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.callItem
            r1.setVisibility(r4)
        L_0x0454:
            boolean r1 = r0.videoCallItemVisible
            if (r1 == 0) goto L_0x047e
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.videoCallItem
            int r1 = r1.getVisibility()
            if (r1 == 0) goto L_0x048b
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.videoCallItem
            r1.setVisibility(r2)
            if (r17 == 0) goto L_0x048b
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.videoCallItem
            r1.setAlpha(r7)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.videoCallItem
            android.view.ViewPropertyAnimator r1 = r1.animate()
            android.view.ViewPropertyAnimator r1 = r1.alpha(r3)
            android.view.ViewPropertyAnimator r1 = r1.setDuration(r5)
            r1.start()
            goto L_0x048b
        L_0x047e:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.videoCallItem
            int r1 = r1.getVisibility()
            if (r1 == r4) goto L_0x048b
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.videoCallItem
            r1.setVisibility(r4)
        L_0x048b:
            boolean r1 = r0.editItemVisible
            if (r1 == 0) goto L_0x04b5
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.editItem
            int r1 = r1.getVisibility()
            if (r1 == 0) goto L_0x04c2
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.editItem
            r1.setVisibility(r2)
            if (r17 == 0) goto L_0x04c2
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.editItem
            r1.setAlpha(r7)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.editItem
            android.view.ViewPropertyAnimator r1 = r1.animate()
            android.view.ViewPropertyAnimator r1 = r1.alpha(r3)
            android.view.ViewPropertyAnimator r1 = r1.setDuration(r5)
            r1.start()
            goto L_0x04c2
        L_0x04b5:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.editItem
            int r1 = r1.getVisibility()
            if (r1 == r4) goto L_0x04c2
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.editItem
            r1.setVisibility(r4)
        L_0x04c2:
            org.telegram.ui.ProfileActivity$PagerIndicatorView r1 = r0.avatarsViewPagerIndicatorView
            if (r1 == 0) goto L_0x0511
            boolean r1 = r1.isIndicatorFullyVisible()
            if (r1 == 0) goto L_0x0511
            boolean r1 = r0.editItemVisible
            if (r1 == 0) goto L_0x04e3
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.editItem
            r1.setVisibility(r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.editItem
            android.view.ViewPropertyAnimator r1 = r1.animate()
            r1.cancel()
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.editItem
            r1.setAlpha(r3)
        L_0x04e3:
            boolean r1 = r0.callItemVisible
            if (r1 == 0) goto L_0x04fa
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.callItem
            r1.setVisibility(r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.callItem
            android.view.ViewPropertyAnimator r1 = r1.animate()
            r1.cancel()
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.callItem
            r1.setAlpha(r3)
        L_0x04fa:
            boolean r1 = r0.videoCallItemVisible
            if (r1 == 0) goto L_0x0511
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.videoCallItem
            r1.setVisibility(r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.videoCallItem
            android.view.ViewPropertyAnimator r1 = r1.animate()
            r1.cancel()
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.videoCallItem
            r1.setAlpha(r3)
        L_0x0511:
            org.telegram.ui.Components.SharedMediaLayout r1 = r0.sharedMediaLayout
            if (r1 == 0) goto L_0x051c
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r1.getSearchItem()
            r1.requestLayout()
        L_0x051c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.createActionBarMenu(boolean):void");
    }

    private void createAutoDeleteItem(Context context) {
        int i;
        this.autoDeletePopupWrapper = new AutoDeletePopupWrapper(context, this.otherItem.getPopupLayout().getSwipeBack(), new AutoDeletePopupWrapper.Callback() {
            public void dismiss() {
                ProfileActivity.this.otherItem.toggleSubMenu();
            }

            public void setAutoDeleteHistory(int i, int i2) {
                ProfileActivity.this.setAutoDeleteHistory(i, i2);
            }
        }, false, this.resourcesProvider);
        TLRPC$UserFull tLRPC$UserFull = this.userInfo;
        if (tLRPC$UserFull == null && this.chatInfo == null) {
            i = 0;
        } else {
            i = tLRPC$UserFull != null ? tLRPC$UserFull.ttl_period : this.chatInfo.ttl_period;
        }
        TimerDrawable ttlIcon = TimerDrawable.getTtlIcon(i);
        this.autoDeleteItemDrawable = ttlIcon;
        this.autoDeleteItem = this.otherItem.addSwipeBackItem(0, ttlIcon, LocaleController.getString("AutoDeletePopupTitle", R.string.AutoDeletePopupTitle), this.autoDeletePopupWrapper.windowLayout);
        this.otherItem.addColoredGap();
        updateAutoDeleteItem();
    }

    public Theme.ResourcesProvider getResourceProvider() {
        return this.resourcesProvider;
    }

    public int getThemedColor(String str) {
        return Theme.getColor(str, this.resourcesProvider);
    }

    public Drawable getThemedDrawable(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Drawable drawable = resourcesProvider2 != null ? resourcesProvider2.getDrawable(str) : null;
        return drawable != null ? drawable : super.getThemedDrawable(str);
    }

    /* access modifiers changed from: private */
    public void setAutoDeleteHistory(int i, int i2) {
        long dialogId2 = getDialogId();
        getMessagesController().setDialogHistoryTTL(dialogId2, i);
        if (this.userInfo != null || this.chatInfo != null) {
            UndoView undoView2 = this.undoView;
            TLRPC$User user = getMessagesController().getUser(Long.valueOf(dialogId2));
            TLRPC$UserFull tLRPC$UserFull = this.userInfo;
            undoView2.showWithAction(dialogId2, i2, (Object) user, (Object) Integer.valueOf(tLRPC$UserFull != null ? tLRPC$UserFull.ttl_period : this.chatInfo.ttl_period), (Runnable) null, (Runnable) null);
        }
    }

    /* access modifiers changed from: protected */
    public void onDialogDismiss(Dialog dialog) {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            recyclerListView.invalidateViews();
        }
    }

    public void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList<Long> arrayList, CharSequence charSequence, boolean z) {
        long longValue = arrayList.get(0).longValue();
        Bundle bundle = new Bundle();
        bundle.putBoolean("scrollToTopOnResume", true);
        if (DialogObject.isEncryptedDialog(longValue)) {
            bundle.putInt("enc_id", DialogObject.getEncryptedChatId(longValue));
        } else if (DialogObject.isUserDialog(longValue)) {
            bundle.putLong("user_id", longValue);
        } else if (DialogObject.isChatDialog(longValue)) {
            bundle.putLong("chat_id", -longValue);
        }
        if (getMessagesController().checkCanOpenChat(bundle, dialogsActivity)) {
            NotificationCenter notificationCenter = getNotificationCenter();
            int i = NotificationCenter.closeChats;
            notificationCenter.removeObserver(this, i);
            getNotificationCenter().postNotificationName(i, new Object[0]);
            presentFragment(new ChatActivity(bundle), true);
            removeSelfFromStack();
            getSendMessagesHelper().sendMessage(getMessagesController().getUser(Long.valueOf(this.userId)), longValue, (MessageObject) null, (MessageObject) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
            if (!TextUtils.isEmpty(charSequence)) {
                SendMessagesHelper.prepareSendingText(AccountInstance.getInstance(this.currentAccount), charSequence.toString(), longValue, true, 0);
            }
        }
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        boolean z;
        boolean z2;
        ImageUpdater imageUpdater2 = this.imageUpdater;
        if (imageUpdater2 != null) {
            imageUpdater2.onRequestPermissionsResultFragment(i, strArr, iArr);
        }
        if (i == 101 || i == 102) {
            TLRPC$User user = getMessagesController().getUser(Long.valueOf(this.userId));
            if (user != null) {
                int i2 = 0;
                while (true) {
                    if (i2 >= iArr.length) {
                        z = true;
                        break;
                    } else if (iArr[i2] != 0) {
                        z = false;
                        break;
                    } else {
                        i2++;
                    }
                }
                if (iArr.length <= 0 || !z) {
                    VoIPHelper.permissionDenied(getParentActivity(), (Runnable) null, i);
                    return;
                }
                boolean z3 = i == 102;
                TLRPC$UserFull tLRPC$UserFull = this.userInfo;
                VoIPHelper.startCall(user, z3, tLRPC$UserFull != null && tLRPC$UserFull.video_calls_available, getParentActivity(), this.userInfo, getAccountInstance());
            }
        } else if (i == 103 && this.currentChat != null) {
            int i3 = 0;
            while (true) {
                if (i3 >= iArr.length) {
                    z2 = true;
                    break;
                } else if (iArr[i3] != 0) {
                    z2 = false;
                    break;
                } else {
                    i3++;
                }
            }
            if (iArr.length <= 0 || !z2) {
                VoIPHelper.permissionDenied(getParentActivity(), (Runnable) null, i);
                return;
            }
            VoIPHelper.startCall(this.currentChat, (TLRPC$InputPeer) null, (String) null, getMessagesController().getGroupCall(this.chatId, false) == null, getParentActivity(), this, getAccountInstance());
        }
    }

    public void dismissCurrentDialog() {
        ImageUpdater imageUpdater2 = this.imageUpdater;
        if (imageUpdater2 == null || !imageUpdater2.dismissCurrentDialog(this.visibleDialog)) {
            super.dismissCurrentDialog();
        }
    }

    public boolean dismissDialogOnPause(Dialog dialog) {
        ImageUpdater imageUpdater2 = this.imageUpdater;
        return (imageUpdater2 == null || imageUpdater2.dismissDialogOnPause(dialog)) && super.dismissDialogOnPause(dialog);
    }

    /* access modifiers changed from: private */
    public Animator searchExpandTransition(final boolean z) {
        if (z) {
            AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
            AndroidUtilities.setAdjustResizeToNothing(getParentActivity(), this.classGuid);
        }
        Animator animator = this.searchViewTransition;
        if (animator != null) {
            animator.removeAllListeners();
            this.searchViewTransition.cancel();
        }
        float[] fArr = new float[2];
        fArr[0] = this.searchTransitionProgress;
        fArr[1] = z ? 0.0f : 1.0f;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
        float f = this.extraHeight;
        this.searchListView.setTranslationY(f);
        this.searchListView.setVisibility(0);
        this.searchItem.setVisibility(0);
        this.listView.setVisibility(0);
        needLayout(true);
        this.avatarContainer.setVisibility(0);
        this.nameTextView[1].setVisibility(0);
        this.onlineTextView[1].setVisibility(0);
        this.actionBar.onSearchFieldVisibilityChanged(this.searchTransitionProgress > 0.5f);
        int i = 8;
        int i2 = this.searchTransitionProgress > 0.5f ? 0 : 8;
        ActionBarMenuItem actionBarMenuItem = this.otherItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.setVisibility(i2);
        }
        if (this.qrItem != null) {
            updateQrItemVisibility(false);
        }
        this.searchItem.setVisibility(i2);
        FrameLayout searchContainer = this.searchItem.getSearchContainer();
        if (this.searchTransitionProgress <= 0.5f) {
            i = 0;
        }
        searchContainer.setVisibility(i);
        this.searchListView.setEmptyView(this.emptyView);
        this.avatarContainer.setClickable(false);
        ofFloat.addUpdateListener(new ProfileActivity$$ExternalSyntheticLambda3(this, ofFloat, f, z));
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                ProfileActivity.this.updateSearchViewState(z);
                ProfileActivity.this.avatarContainer.setClickable(true);
                if (z) {
                    ProfileActivity.this.searchItem.requestFocusOnSearchView();
                }
                ProfileActivity.this.needLayout(true);
                Animator unused = ProfileActivity.this.searchViewTransition = null;
                ProfileActivity.this.fragmentView.invalidate();
                if (z) {
                    boolean unused2 = ProfileActivity.this.invalidateScroll = true;
                    ProfileActivity.this.saveScrollPosition();
                    AndroidUtilities.requestAdjustResize(ProfileActivity.this.getParentActivity(), ProfileActivity.this.classGuid);
                    ProfileActivity.this.emptyView.setPreventMoving(false);
                }
            }
        });
        if (!z) {
            this.invalidateScroll = true;
            saveScrollPosition();
            AndroidUtilities.requestAdjustNothing(getParentActivity(), this.classGuid);
            this.emptyView.setPreventMoving(true);
        }
        ofFloat.setDuration(220);
        ofFloat.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.searchViewTransition = ofFloat;
        return ofFloat;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$searchExpandTransition$33(ValueAnimator valueAnimator, float f, boolean z, ValueAnimator valueAnimator2) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.searchTransitionProgress = floatValue;
        float f2 = (floatValue - 0.5f) / 0.5f;
        float f3 = (0.5f - floatValue) / 0.5f;
        if (f2 < 0.0f) {
            f2 = 0.0f;
        }
        if (f3 < 0.0f) {
            f3 = 0.0f;
        }
        float f4 = -f;
        this.searchTransitionOffset = (int) ((1.0f - floatValue) * f4);
        this.searchListView.setTranslationY(floatValue * f);
        this.emptyView.setTranslationY(f * this.searchTransitionProgress);
        this.listView.setTranslationY(f4 * (1.0f - this.searchTransitionProgress));
        this.listView.setScaleX(1.0f - ((1.0f - this.searchTransitionProgress) * 0.01f));
        this.listView.setScaleY(1.0f - ((1.0f - this.searchTransitionProgress) * 0.01f));
        this.listView.setAlpha(this.searchTransitionProgress);
        boolean z2 = true;
        needLayout(true);
        this.listView.setAlpha(f2);
        this.searchListView.setAlpha(1.0f - this.searchTransitionProgress);
        this.searchListView.setScaleX((this.searchTransitionProgress * 0.05f) + 1.0f);
        this.searchListView.setScaleY((this.searchTransitionProgress * 0.05f) + 1.0f);
        this.emptyView.setAlpha(1.0f - f2);
        this.avatarContainer.setAlpha(f2);
        this.nameTextView[1].setAlpha(f2);
        this.onlineTextView[1].setAlpha(f2);
        this.searchItem.getSearchField().setAlpha(f3);
        if (z && this.searchTransitionProgress < 0.7f) {
            this.searchItem.requestFocusOnSearchView();
        }
        int i = 8;
        this.searchItem.getSearchContainer().setVisibility(this.searchTransitionProgress < 0.5f ? 0 : 8);
        if (this.searchTransitionProgress > 0.5f) {
            i = 0;
        }
        ActionBarMenuItem actionBarMenuItem = this.otherItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.setVisibility(i);
            this.otherItem.setAlpha(f2);
        }
        ActionBarMenuItem actionBarMenuItem2 = this.qrItem;
        if (actionBarMenuItem2 != null) {
            actionBarMenuItem2.setAlpha(f2);
            updateQrItemVisibility(false);
        }
        this.searchItem.setVisibility(i);
        ActionBar actionBar = this.actionBar;
        if (this.searchTransitionProgress >= 0.5f) {
            z2 = false;
        }
        actionBar.onSearchFieldVisibilityChanged(z2);
        ActionBarMenuItem actionBarMenuItem3 = this.otherItem;
        if (actionBarMenuItem3 != null) {
            actionBarMenuItem3.setAlpha(f2);
        }
        this.searchItem.setAlpha(f2);
        this.topView.invalidate();
        this.fragmentView.invalidate();
    }

    /* access modifiers changed from: private */
    public void updateSearchViewState(boolean z) {
        int i = 0;
        int i2 = z ? 8 : 0;
        this.listView.setVisibility(i2);
        this.searchListView.setVisibility(z ? 0 : 8);
        this.searchItem.getSearchContainer().setVisibility(z ? 0 : 8);
        this.actionBar.onSearchFieldVisibilityChanged(z);
        this.avatarContainer.setVisibility(i2);
        this.nameTextView[1].setVisibility(i2);
        this.onlineTextView[1].setVisibility(i2);
        ActionBarMenuItem actionBarMenuItem = this.otherItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.setAlpha(1.0f);
            this.otherItem.setVisibility(i2);
        }
        ActionBarMenuItem actionBarMenuItem2 = this.qrItem;
        if (actionBarMenuItem2 != null) {
            actionBarMenuItem2.setAlpha(1.0f);
            ActionBarMenuItem actionBarMenuItem3 = this.qrItem;
            if (z || !isQrNeedVisible()) {
                i = 8;
            }
            actionBarMenuItem3.setVisibility(i);
        }
        this.searchItem.setVisibility(i2);
        this.avatarContainer.setAlpha(1.0f);
        this.nameTextView[1].setAlpha(1.0f);
        this.onlineTextView[1].setAlpha(1.0f);
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

    public void onUploadProgressChanged(float f) {
        RadialProgressView radialProgressView = this.avatarProgressView;
        if (radialProgressView != null) {
            radialProgressView.setProgress(f);
            this.avatarsViewPager.setUploadProgress(this.uploadingImageLocation, f);
        }
    }

    public void didStartUpload(boolean z) {
        RadialProgressView radialProgressView = this.avatarProgressView;
        if (radialProgressView != null) {
            radialProgressView.setProgress(0.0f);
        }
    }

    public void didUploadPhoto(TLRPC$InputFile tLRPC$InputFile, TLRPC$InputFile tLRPC$InputFile2, double d, String str, TLRPC$PhotoSize tLRPC$PhotoSize, TLRPC$PhotoSize tLRPC$PhotoSize2) {
        AndroidUtilities.runOnUIThread(new ProfileActivity$$ExternalSyntheticLambda22(this, tLRPC$InputFile, tLRPC$InputFile2, d, str, tLRPC$PhotoSize2, tLRPC$PhotoSize));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didUploadPhoto$35(String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new ProfileActivity$$ExternalSyntheticLambda23(this, tLRPC$TL_error, tLObject, str));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didUploadPhoto$34(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, String str) {
        this.avatarsViewPager.removeUploadingImage(this.uploadingImageLocation);
        if (tLRPC$TL_error == null) {
            TLRPC$User user = getMessagesController().getUser(Long.valueOf(getUserConfig().getClientUserId()));
            if (user == null) {
                user = getUserConfig().getCurrentUser();
                if (user != null) {
                    getMessagesController().putUser(user, false);
                } else {
                    return;
                }
            } else {
                getUserConfig().setCurrentUser(user);
            }
            TLRPC$TL_photos_photo tLRPC$TL_photos_photo = (TLRPC$TL_photos_photo) tLObject;
            ArrayList<TLRPC$PhotoSize> arrayList = tLRPC$TL_photos_photo.photo.sizes;
            TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(arrayList, 150);
            TLRPC$PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(arrayList, 800);
            TLRPC$VideoSize tLRPC$VideoSize = tLRPC$TL_photos_photo.photo.video_sizes.isEmpty() ? null : tLRPC$TL_photos_photo.photo.video_sizes.get(0);
            TLRPC$TL_userProfilePhoto tLRPC$TL_userProfilePhoto = new TLRPC$TL_userProfilePhoto();
            user.photo = tLRPC$TL_userProfilePhoto;
            tLRPC$TL_userProfilePhoto.photo_id = tLRPC$TL_photos_photo.photo.id;
            if (closestPhotoSizeWithSize != null) {
                tLRPC$TL_userProfilePhoto.photo_small = closestPhotoSizeWithSize.location;
            }
            if (closestPhotoSizeWithSize2 != null) {
                tLRPC$TL_userProfilePhoto.photo_big = closestPhotoSizeWithSize2.location;
            }
            if (!(closestPhotoSizeWithSize == null || this.avatar == null)) {
                FileLoader.getInstance(this.currentAccount).getPathToAttach(this.avatar, true).renameTo(FileLoader.getInstance(this.currentAccount).getPathToAttach(closestPhotoSizeWithSize, true));
                ImageLoader.getInstance().replaceImageInCache(this.avatar.volume_id + "_" + this.avatar.local_id + "@50_50", closestPhotoSizeWithSize.location.volume_id + "_" + closestPhotoSizeWithSize.location.local_id + "@50_50", ImageLocation.getForUserOrChat(user, 1), false);
            }
            if (!(closestPhotoSizeWithSize2 == null || this.avatarBig == null)) {
                FileLoader.getInstance(this.currentAccount).getPathToAttach(this.avatarBig, true).renameTo(FileLoader.getInstance(this.currentAccount).getPathToAttach(closestPhotoSizeWithSize2, true));
            }
            if (!(tLRPC$VideoSize == null || str == null)) {
                new File(str).renameTo(FileLoader.getInstance(this.currentAccount).getPathToAttach(tLRPC$VideoSize, "mp4", true));
            }
            getMessagesStorage().clearUserPhotos(user.id);
            ArrayList arrayList2 = new ArrayList();
            arrayList2.add(user);
            getMessagesStorage().putUsersAndChats(arrayList2, (ArrayList<TLRPC$Chat>) null, false, true);
        }
        this.allowPullingDown = !AndroidUtilities.isTablet() && !this.isInLandscapeMode && this.avatarImage.getImageReceiver().hasNotThumb() && !AndroidUtilities.isAccessibilityScreenReaderEnabled();
        this.avatar = null;
        this.avatarBig = null;
        this.avatarsViewPager.setCreateThumbFromParent(false);
        updateProfileData(true);
        showAvatarProgress(false, true);
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_ALL));
        getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
        getUserConfig().saveConfig(true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didUploadPhoto$36(TLRPC$InputFile tLRPC$InputFile, TLRPC$InputFile tLRPC$InputFile2, double d, String str, TLRPC$PhotoSize tLRPC$PhotoSize, TLRPC$PhotoSize tLRPC$PhotoSize2) {
        if (tLRPC$InputFile == null && tLRPC$InputFile2 == null) {
            TLRPC$FileLocation tLRPC$FileLocation = tLRPC$PhotoSize.location;
            this.avatar = tLRPC$FileLocation;
            this.avatarBig = tLRPC$PhotoSize2.location;
            this.avatarImage.setImage(ImageLocation.getForLocal(tLRPC$FileLocation), "50_50", (Drawable) this.avatarDrawable, (Object) null);
            if (this.setAvatarRow != -1) {
                updateRowsIds();
                ListAdapter listAdapter2 = this.listAdapter;
                if (listAdapter2 != null) {
                    listAdapter2.notifyDataSetChanged();
                }
                needLayout(true);
            }
            ProfileGalleryView profileGalleryView = this.avatarsViewPager;
            ImageLocation forLocal = ImageLocation.getForLocal(this.avatarBig);
            this.uploadingImageLocation = forLocal;
            profileGalleryView.addUploadingImage(forLocal, ImageLocation.getForLocal(this.avatar));
            showAvatarProgress(true, false);
        } else {
            TLRPC$TL_photos_uploadProfilePhoto tLRPC$TL_photos_uploadProfilePhoto = new TLRPC$TL_photos_uploadProfilePhoto();
            if (tLRPC$InputFile != null) {
                tLRPC$TL_photos_uploadProfilePhoto.file = tLRPC$InputFile;
                tLRPC$TL_photos_uploadProfilePhoto.flags |= 1;
            }
            if (tLRPC$InputFile2 != null) {
                tLRPC$TL_photos_uploadProfilePhoto.video = tLRPC$InputFile2;
                int i = tLRPC$TL_photos_uploadProfilePhoto.flags | 2;
                tLRPC$TL_photos_uploadProfilePhoto.flags = i;
                tLRPC$TL_photos_uploadProfilePhoto.video_start_ts = d;
                tLRPC$TL_photos_uploadProfilePhoto.flags = i | 4;
            }
            getConnectionsManager().sendRequest(tLRPC$TL_photos_uploadProfilePhoto, new ProfileActivity$$ExternalSyntheticLambda34(this, str));
        }
        this.actionBar.createMenu().requestLayout();
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
                        if (ProfileActivity.this.avatarAnimation != null && ProfileActivity.this.avatarProgressView != null) {
                            if (!z) {
                                ProfileActivity.this.avatarProgressView.setVisibility(4);
                            }
                            AnimatorSet unused = ProfileActivity.this.avatarAnimation = null;
                        }
                    }

                    public void onAnimationCancel(Animator animator) {
                        AnimatorSet unused = ProfileActivity.this.avatarAnimation = null;
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
        ImageUpdater imageUpdater2 = this.imageUpdater;
        if (imageUpdater2 != null) {
            imageUpdater2.onActivityResult(i, i2, intent);
        }
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

    private void sendLogs(boolean z) {
        if (getParentActivity() != null) {
            AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
            alertDialog.setCanCancel(false);
            alertDialog.show();
            Utilities.globalQueue.postRunnable(new ProfileActivity$$ExternalSyntheticLambda26(this, z, alertDialog));
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00ad A[SYNTHETIC, Splitter:B:40:0x00ad] */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00b2 A[Catch:{ Exception -> 0x00d1 }] */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x00c5 A[Catch:{ Exception -> 0x00c9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x00cd A[Catch:{ Exception -> 0x00c9 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$sendLogs$38(boolean r18, org.telegram.ui.ActionBar.AlertDialog r19) {
        /*
            r17 = this;
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x00d1 }
            r1 = 0
            java.io.File r0 = r0.getExternalFilesDir(r1)     // Catch:{ Exception -> 0x00d1 }
            java.io.File r2 = new java.io.File     // Catch:{ Exception -> 0x00d1 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00d1 }
            r3.<init>()     // Catch:{ Exception -> 0x00d1 }
            java.lang.String r0 = r0.getAbsolutePath()     // Catch:{ Exception -> 0x00d1 }
            r3.append(r0)     // Catch:{ Exception -> 0x00d1 }
            java.lang.String r0 = "/logs"
            r3.append(r0)     // Catch:{ Exception -> 0x00d1 }
            java.lang.String r0 = r3.toString()     // Catch:{ Exception -> 0x00d1 }
            r2.<init>(r0)     // Catch:{ Exception -> 0x00d1 }
            java.io.File r3 = new java.io.File     // Catch:{ Exception -> 0x00d1 }
            java.lang.String r0 = "logs.zip"
            r3.<init>(r2, r0)     // Catch:{ Exception -> 0x00d1 }
            boolean r0 = r3.exists()     // Catch:{ Exception -> 0x00d1 }
            if (r0 == 0) goto L_0x0031
            r3.delete()     // Catch:{ Exception -> 0x00d1 }
        L_0x0031:
            java.io.File[] r0 = r2.listFiles()     // Catch:{ Exception -> 0x00d1 }
            r2 = 1
            boolean[] r4 = new boolean[r2]     // Catch:{ Exception -> 0x00d1 }
            long r5 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x00d1 }
            java.io.FileOutputStream r7 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x00a6, all -> 0x00a1 }
            r7.<init>(r3)     // Catch:{ Exception -> 0x00a6, all -> 0x00a1 }
            java.util.zip.ZipOutputStream r8 = new java.util.zip.ZipOutputStream     // Catch:{ Exception -> 0x00a6, all -> 0x00a1 }
            java.io.BufferedOutputStream r9 = new java.io.BufferedOutputStream     // Catch:{ Exception -> 0x00a6, all -> 0x00a1 }
            r9.<init>(r7)     // Catch:{ Exception -> 0x00a6, all -> 0x00a1 }
            r8.<init>(r9)     // Catch:{ Exception -> 0x00a6, all -> 0x00a1 }
            r7 = 65536(0x10000, float:9.18355E-41)
            byte[] r9 = new byte[r7]     // Catch:{ Exception -> 0x009f }
            r10 = 0
            r11 = 0
        L_0x0051:
            int r12 = r0.length     // Catch:{ Exception -> 0x009f }
            if (r11 >= r12) goto L_0x0099
            if (r18 == 0) goto L_0x0066
            r12 = r0[r11]     // Catch:{ Exception -> 0x009f }
            long r12 = r12.lastModified()     // Catch:{ Exception -> 0x009f }
            long r12 = r5 - r12
            r14 = 86400000(0x5265CLASSNAME, double:4.2687272E-316)
            int r16 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
            if (r16 <= 0) goto L_0x0066
            goto L_0x008e
        L_0x0066:
            java.io.FileInputStream r12 = new java.io.FileInputStream     // Catch:{ Exception -> 0x009f }
            r13 = r0[r11]     // Catch:{ Exception -> 0x009f }
            r12.<init>(r13)     // Catch:{ Exception -> 0x009f }
            java.io.BufferedInputStream r13 = new java.io.BufferedInputStream     // Catch:{ Exception -> 0x009f }
            r13.<init>(r12, r7)     // Catch:{ Exception -> 0x009f }
            java.util.zip.ZipEntry r12 = new java.util.zip.ZipEntry     // Catch:{ Exception -> 0x0096, all -> 0x0091 }
            r14 = r0[r11]     // Catch:{ Exception -> 0x0096, all -> 0x0091 }
            java.lang.String r14 = r14.getName()     // Catch:{ Exception -> 0x0096, all -> 0x0091 }
            r12.<init>(r14)     // Catch:{ Exception -> 0x0096, all -> 0x0091 }
            r8.putNextEntry(r12)     // Catch:{ Exception -> 0x0096, all -> 0x0091 }
        L_0x0080:
            int r12 = r13.read(r9, r10, r7)     // Catch:{ Exception -> 0x0096, all -> 0x0091 }
            r14 = -1
            if (r12 == r14) goto L_0x008b
            r8.write(r9, r10, r12)     // Catch:{ Exception -> 0x0096, all -> 0x0091 }
            goto L_0x0080
        L_0x008b:
            r13.close()     // Catch:{ Exception -> 0x0096, all -> 0x0091 }
        L_0x008e:
            int r11 = r11 + 1
            goto L_0x0051
        L_0x0091:
            r0 = move-exception
            r2 = r17
            r1 = r13
            goto L_0x00c3
        L_0x0096:
            r0 = move-exception
            r1 = r13
            goto L_0x00a8
        L_0x0099:
            r4[r10] = r2     // Catch:{ Exception -> 0x009f }
        L_0x009b:
            r8.close()     // Catch:{ Exception -> 0x00d1 }
            goto L_0x00b3
        L_0x009f:
            r0 = move-exception
            goto L_0x00a8
        L_0x00a1:
            r0 = move-exception
            r2 = r17
            r8 = r1
            goto L_0x00c3
        L_0x00a6:
            r0 = move-exception
            r8 = r1
        L_0x00a8:
            r0.printStackTrace()     // Catch:{ all -> 0x00c0 }
            if (r1 == 0) goto L_0x00b0
            r1.close()     // Catch:{ Exception -> 0x00d1 }
        L_0x00b0:
            if (r8 == 0) goto L_0x00b3
            goto L_0x009b
        L_0x00b3:
            org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda25 r0 = new org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda25     // Catch:{ Exception -> 0x00d1 }
            r2 = r17
            r1 = r19
            r0.<init>(r2, r1, r4, r3)     // Catch:{ Exception -> 0x00c9 }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)     // Catch:{ Exception -> 0x00c9 }
            goto L_0x00d7
        L_0x00c0:
            r0 = move-exception
            r2 = r17
        L_0x00c3:
            if (r1 == 0) goto L_0x00cb
            r1.close()     // Catch:{ Exception -> 0x00c9 }
            goto L_0x00cb
        L_0x00c9:
            r0 = move-exception
            goto L_0x00d4
        L_0x00cb:
            if (r8 == 0) goto L_0x00d0
            r8.close()     // Catch:{ Exception -> 0x00c9 }
        L_0x00d0:
            throw r0     // Catch:{ Exception -> 0x00c9 }
        L_0x00d1:
            r0 = move-exception
            r2 = r17
        L_0x00d4:
            r0.printStackTrace()
        L_0x00d7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.lambda$sendLogs$38(boolean, org.telegram.ui.ActionBar.AlertDialog):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendLogs$37(AlertDialog alertDialog, boolean[] zArr, File file) {
        Uri uri;
        try {
            alertDialog.dismiss();
        } catch (Exception unused) {
        }
        if (zArr[0]) {
            int i = Build.VERSION.SDK_INT;
            if (i >= 24) {
                Activity parentActivity = getParentActivity();
                uri = FileProvider.getUriForFile(parentActivity, ApplicationLoader.getApplicationId() + ".provider", file);
            } else {
                uri = Uri.fromFile(file);
            }
            Intent intent = new Intent("android.intent.action.SEND");
            if (i >= 24) {
                intent.addFlags(1);
            }
            intent.setType("message/rfCLASSNAME");
            intent.putExtra("android.intent.extra.EMAIL", "");
            intent.putExtra("android.intent.extra.SUBJECT", "Logs from " + LocaleController.getInstance().formatterStats.format(System.currentTimeMillis()));
            intent.putExtra("android.intent.extra.STREAM", uri);
            if (getParentActivity() != null) {
                try {
                    getParentActivity().startActivityForResult(Intent.createChooser(intent, "Select email application."), 500);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
        } else if (getParentActivity() != null) {
            Toast.makeText(getParentActivity(), LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred), 0).show();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        /* JADX WARNING: type inference failed for: r0v1, types: [android.view.View] */
        /* JADX WARNING: type inference failed for: r0v4 */
        /* JADX WARNING: type inference failed for: r0v29 */
        /* JADX WARNING: type inference failed for: r0v33 */
        /* JADX WARNING: type inference failed for: r0v34 */
        /* JADX WARNING: type inference failed for: r0v35 */
        /* JADX WARNING: type inference failed for: r0v36 */
        /* JADX WARNING: type inference failed for: r3v24, types: [org.telegram.ui.Cells.NotificationsCheckCell] */
        /* JADX WARNING: type inference failed for: r0v37 */
        /* JADX WARNING: type inference failed for: r2v21, types: [org.telegram.ui.Cells.UserCell] */
        /* JADX WARNING: type inference failed for: r0v38 */
        /* JADX WARNING: type inference failed for: r0v39 */
        /* JADX WARNING: type inference failed for: r0v40 */
        /* JADX WARNING: type inference failed for: r0v41 */
        /* JADX WARNING: type inference failed for: r0v42 */
        /* JADX WARNING: type inference failed for: r0v43 */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r14, int r15) {
            /*
                r13 = this;
                r14 = -1
                r0 = 1
                r1 = 0
                switch(r15) {
                    case 1: goto L_0x0149;
                    case 2: goto L_0x012c;
                    case 3: goto L_0x0117;
                    case 4: goto L_0x0108;
                    case 5: goto L_0x00ea;
                    case 6: goto L_0x00d5;
                    case 7: goto L_0x00c6;
                    case 8: goto L_0x00a5;
                    case 9: goto L_0x0006;
                    case 10: goto L_0x0006;
                    case 11: goto L_0x009c;
                    case 12: goto L_0x008b;
                    case 13: goto L_0x0062;
                    case 14: goto L_0x0006;
                    case 15: goto L_0x0053;
                    case 16: goto L_0x0006;
                    case 17: goto L_0x0044;
                    case 18: goto L_0x0035;
                    default: goto L_0x0006;
                }
            L_0x0006:
                org.telegram.ui.Cells.TextInfoPrivacyCell r2 = new org.telegram.ui.Cells.TextInfoPrivacyCell
                android.content.Context r3 = r13.mContext
                org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r4.resourcesProvider
                r5 = 10
                r2.<init>(r3, r5, r4)
                android.widget.TextView r3 = r2.getTextView()
                r3.setGravity(r0)
                android.widget.TextView r3 = r2.getTextView()
                org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                java.lang.String r6 = "windowBackgroundWhiteGrayText3"
                int r4 = r4.getThemedColor(r6)
                r3.setTextColor(r4)
                android.widget.TextView r3 = r2.getTextView()
                r4 = 0
                r3.setMovementMethod(r4)
                goto L_0x015a
            L_0x0035:
                org.telegram.ui.Components.Premium.ProfilePremiumCell r0 = new org.telegram.ui.Components.Premium.ProfilePremiumCell
                android.content.Context r1 = r13.mContext
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r2.resourcesProvider
                r0.<init>(r1, r2)
                goto L_0x021f
            L_0x0044:
                org.telegram.ui.Cells.TextInfoPrivacyCell r0 = new org.telegram.ui.Cells.TextInfoPrivacyCell
                android.content.Context r1 = r13.mContext
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r2.resourcesProvider
                r0.<init>(r1, r2)
                goto L_0x021f
            L_0x0053:
                org.telegram.ui.ProfileActivity$ListAdapter$4 r0 = new org.telegram.ui.ProfileActivity$ListAdapter$4
                android.content.Context r1 = r13.mContext
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r2.resourcesProvider
                r0.<init>(r1, r2)
                goto L_0x021f
            L_0x0062:
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                org.telegram.ui.Components.SharedMediaLayout r0 = r0.sharedMediaLayout
                android.view.ViewParent r0 = r0.getParent()
                if (r0 == 0) goto L_0x0083
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                org.telegram.ui.Components.SharedMediaLayout r0 = r0.sharedMediaLayout
                android.view.ViewParent r0 = r0.getParent()
                android.view.ViewGroup r0 = (android.view.ViewGroup) r0
                org.telegram.ui.ProfileActivity r1 = org.telegram.ui.ProfileActivity.this
                org.telegram.ui.Components.SharedMediaLayout r1 = r1.sharedMediaLayout
                r0.removeView(r1)
            L_0x0083:
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                org.telegram.ui.Components.SharedMediaLayout r0 = r0.sharedMediaLayout
                goto L_0x021f
            L_0x008b:
                org.telegram.ui.ProfileActivity$ListAdapter$3 r0 = new org.telegram.ui.ProfileActivity$ListAdapter$3
                android.content.Context r2 = r13.mContext
                r0.<init>(r2)
                android.graphics.drawable.ColorDrawable r2 = new android.graphics.drawable.ColorDrawable
                r2.<init>(r1)
                r0.setBackground(r2)
                goto L_0x021f
            L_0x009c:
                org.telegram.ui.ProfileActivity$ListAdapter$2 r0 = new org.telegram.ui.ProfileActivity$ListAdapter$2
                android.content.Context r1 = r13.mContext
                r0.<init>(r13, r1)
                goto L_0x021f
            L_0x00a5:
                org.telegram.ui.Cells.UserCell r0 = new org.telegram.ui.Cells.UserCell
                android.content.Context r3 = r13.mContext
                org.telegram.ui.ProfileActivity r1 = org.telegram.ui.ProfileActivity.this
                int r1 = r1.addMemberRow
                if (r1 != r14) goto L_0x00b6
                r1 = 9
                r4 = 9
                goto L_0x00b8
            L_0x00b6:
                r1 = 6
                r4 = 6
            L_0x00b8:
                r5 = 0
                r6 = 1
                org.telegram.ui.ProfileActivity r1 = org.telegram.ui.ProfileActivity.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r7 = r1.resourcesProvider
                r2 = r0
                r2.<init>((android.content.Context) r3, (int) r4, (int) r5, (boolean) r6, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r7)
                goto L_0x021f
            L_0x00c6:
                org.telegram.ui.Cells.ShadowSectionCell r0 = new org.telegram.ui.Cells.ShadowSectionCell
                android.content.Context r1 = r13.mContext
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r2.resourcesProvider
                r0.<init>((android.content.Context) r1, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r2)
                goto L_0x021f
            L_0x00d5:
                org.telegram.ui.Cells.NotificationsCheckCell r0 = new org.telegram.ui.Cells.NotificationsCheckCell
                android.content.Context r4 = r13.mContext
                r5 = 23
                r6 = 70
                r7 = 0
                org.telegram.ui.ProfileActivity r1 = org.telegram.ui.ProfileActivity.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r8 = r1.resourcesProvider
                r3 = r0
                r3.<init>(r4, r5, r6, r7, r8)
                goto L_0x021f
            L_0x00ea:
                org.telegram.ui.Cells.DividerCell r0 = new org.telegram.ui.Cells.DividerCell
                android.content.Context r2 = r13.mContext
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r3 = r3.resourcesProvider
                r0.<init>(r2, r3)
                r2 = 1101004800(0x41a00000, float:20.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                r3 = 1082130432(0x40800000, float:4.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                r0.setPadding(r2, r3, r1, r1)
                goto L_0x021f
            L_0x0108:
                org.telegram.ui.Cells.TextCell r0 = new org.telegram.ui.Cells.TextCell
                android.content.Context r1 = r13.mContext
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r2.resourcesProvider
                r0.<init>(r1, r2)
                goto L_0x021f
            L_0x0117:
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                org.telegram.ui.ProfileActivity$ListAdapter$1 r1 = new org.telegram.ui.ProfileActivity$ListAdapter$1
                android.content.Context r2 = r13.mContext
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r4 = r3.resourcesProvider
                r1.<init>(r2, r3, r4)
                org.telegram.ui.Cells.AboutLinkCell r0 = r0.aboutLinkCell = r1
                goto L_0x021f
            L_0x012c:
                org.telegram.ui.Cells.TextDetailCell r1 = new org.telegram.ui.Cells.TextDetailCell
                android.content.Context r2 = r13.mContext
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r3 = r3.resourcesProvider
                r1.<init>(r2, r3)
                r1.setContentDescriptionValueFirst(r0)
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                org.telegram.ui.ProfileActivity$ListAdapter$$ExternalSyntheticLambda2 r2 = new org.telegram.ui.ProfileActivity$ListAdapter$$ExternalSyntheticLambda2
                r2.<init>(r0)
                r1.setImageClickListener(r2)
                r0 = r1
                goto L_0x021f
            L_0x0149:
                org.telegram.ui.Cells.HeaderCell r0 = new org.telegram.ui.Cells.HeaderCell
                android.content.Context r1 = r13.mContext
                r2 = 23
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r3 = r3.resourcesProvider
                r0.<init>(r1, r2, r3)
                goto L_0x021f
            L_0x015a:
                android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x01f6 }
                android.content.pm.PackageManager r3 = r3.getPackageManager()     // Catch:{ Exception -> 0x01f6 }
                android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x01f6 }
                java.lang.String r4 = r4.getPackageName()     // Catch:{ Exception -> 0x01f6 }
                android.content.pm.PackageInfo r3 = r3.getPackageInfo(r4, r1)     // Catch:{ Exception -> 0x01f6 }
                int r4 = r3.versionCode     // Catch:{ Exception -> 0x01f6 }
                int r6 = r4 / 10
                int r4 = r4 % r5
                r5 = 2
                java.lang.String r7 = " "
                if (r4 == r0) goto L_0x01b4
                if (r4 == r5) goto L_0x01b4
                boolean r4 = org.telegram.messenger.BuildVars.isStandaloneApp()     // Catch:{ Exception -> 0x01f6 }
                if (r4 == 0) goto L_0x0198
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01f6 }
                r4.<init>()     // Catch:{ Exception -> 0x01f6 }
                java.lang.String r8 = "direct "
                r4.append(r8)     // Catch:{ Exception -> 0x01f6 }
                java.lang.String r8 = android.os.Build.CPU_ABI     // Catch:{ Exception -> 0x01f6 }
                r4.append(r8)     // Catch:{ Exception -> 0x01f6 }
                r4.append(r7)     // Catch:{ Exception -> 0x01f6 }
                java.lang.String r7 = android.os.Build.CPU_ABI2     // Catch:{ Exception -> 0x01f6 }
                r4.append(r7)     // Catch:{ Exception -> 0x01f6 }
                java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x01f6 }
                goto L_0x01cf
            L_0x0198:
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01f6 }
                r4.<init>()     // Catch:{ Exception -> 0x01f6 }
                java.lang.String r8 = "universal "
                r4.append(r8)     // Catch:{ Exception -> 0x01f6 }
                java.lang.String r8 = android.os.Build.CPU_ABI     // Catch:{ Exception -> 0x01f6 }
                r4.append(r8)     // Catch:{ Exception -> 0x01f6 }
                r4.append(r7)     // Catch:{ Exception -> 0x01f6 }
                java.lang.String r7 = android.os.Build.CPU_ABI2     // Catch:{ Exception -> 0x01f6 }
                r4.append(r7)     // Catch:{ Exception -> 0x01f6 }
                java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x01f6 }
                goto L_0x01cf
            L_0x01b4:
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01f6 }
                r4.<init>()     // Catch:{ Exception -> 0x01f6 }
                java.lang.String r8 = "store bundled "
                r4.append(r8)     // Catch:{ Exception -> 0x01f6 }
                java.lang.String r8 = android.os.Build.CPU_ABI     // Catch:{ Exception -> 0x01f6 }
                r4.append(r8)     // Catch:{ Exception -> 0x01f6 }
                r4.append(r7)     // Catch:{ Exception -> 0x01f6 }
                java.lang.String r7 = android.os.Build.CPU_ABI2     // Catch:{ Exception -> 0x01f6 }
                r4.append(r7)     // Catch:{ Exception -> 0x01f6 }
                java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x01f6 }
            L_0x01cf:
                java.lang.String r7 = "TelegramVersion"
                int r8 = org.telegram.messenger.R.string.TelegramVersion     // Catch:{ Exception -> 0x01f6 }
                java.lang.Object[] r9 = new java.lang.Object[r0]     // Catch:{ Exception -> 0x01f6 }
                java.util.Locale r10 = java.util.Locale.US     // Catch:{ Exception -> 0x01f6 }
                java.lang.String r11 = "v%s (%d) %s"
                r12 = 3
                java.lang.Object[] r12 = new java.lang.Object[r12]     // Catch:{ Exception -> 0x01f6 }
                java.lang.String r3 = r3.versionName     // Catch:{ Exception -> 0x01f6 }
                r12[r1] = r3     // Catch:{ Exception -> 0x01f6 }
                java.lang.Integer r3 = java.lang.Integer.valueOf(r6)     // Catch:{ Exception -> 0x01f6 }
                r12[r0] = r3     // Catch:{ Exception -> 0x01f6 }
                r12[r5] = r4     // Catch:{ Exception -> 0x01f6 }
                java.lang.String r0 = java.lang.String.format(r10, r11, r12)     // Catch:{ Exception -> 0x01f6 }
                r9[r1] = r0     // Catch:{ Exception -> 0x01f6 }
                java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r7, r8, r9)     // Catch:{ Exception -> 0x01f6 }
                r2.setText(r0)     // Catch:{ Exception -> 0x01f6 }
                goto L_0x01fa
            L_0x01f6:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x01fa:
                android.widget.TextView r0 = r2.getTextView()
                r3 = 1096810496(0x41600000, float:14.0)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                r0.setPadding(r1, r4, r1, r3)
                android.content.Context r0 = r13.mContext
                int r1 = org.telegram.messenger.R.drawable.greydivider_bottom
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                java.lang.String r4 = "windowBackgroundGrayShadow"
                int r3 = r3.getThemedColor(r4)
                android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r0, (int) r1, (int) r3)
                r2.setBackgroundDrawable(r0)
                r0 = r2
            L_0x021f:
                r1 = 13
                if (r15 == r1) goto L_0x022c
                androidx.recyclerview.widget.RecyclerView$LayoutParams r15 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r1 = -2
                r15.<init>((int) r14, (int) r1)
                r0.setLayoutParams(r15)
            L_0x022c:
                org.telegram.ui.Components.RecyclerListView$Holder r14 = new org.telegram.ui.Components.RecyclerListView$Holder
                r14.<init>(r0)
                return r14
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.ListAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.itemView == ProfileActivity.this.sharedMediaLayout) {
                boolean unused = ProfileActivity.this.sharedMediaLayoutAttached = true;
            }
        }

        public void onViewDetachedFromWindow(RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.itemView == ProfileActivity.this.sharedMediaLayout) {
                boolean unused = ProfileActivity.this.sharedMediaLayoutAttached = false;
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:123:0x030e  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r19, int r20) {
            /*
                r18 = this;
                r1 = r18
                r0 = r19
                r2 = r20
                int r3 = r19.getItemViewType()
                java.lang.String r4 = "ChannelMembers"
                r5 = -1
                r6 = 1
                if (r3 == r6) goto L_0x0b1d
                r7 = 2
                r8 = 0
                r10 = 0
                r11 = 0
                if (r3 == r7) goto L_0x0920
                r7 = 3
                if (r3 == r7) goto L_0x07ec
                r12 = 4
                r13 = 12
                if (r3 == r12) goto L_0x032c
                r12 = 6
                if (r3 == r12) goto L_0x01bb
                r7 = 7
                java.lang.String r8 = "windowBackgroundGrayShadow"
                if (r3 == r7) goto L_0x013b
                r7 = 8
                if (r3 == r7) goto L_0x0077
                if (r3 == r13) goto L_0x0070
                r7 = 15
                if (r3 == r7) goto L_0x005d
                r7 = 17
                if (r3 == r7) goto L_0x003b
                r7 = 18
                if (r3 == r7) goto L_0x032c
                goto L_0x0bc0
            L_0x003b:
                android.view.View r0 = r0.itemView
                org.telegram.ui.Cells.TextInfoPrivacyCell r0 = (org.telegram.ui.Cells.TextInfoPrivacyCell) r0
                android.content.Context r2 = r1.mContext
                int r3 = org.telegram.messenger.R.drawable.greydivider
                org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                int r4 = r4.getThemedColor(r8)
                android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r3, (int) r4)
                r0.setBackground(r2)
                int r2 = org.telegram.messenger.R.string.BotAddToGroupOrChannelInfo
                java.lang.String r3 = "BotAddToGroupOrChannelInfo"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2)
                goto L_0x0bc0
            L_0x005d:
                android.view.View r0 = r0.itemView
                org.telegram.ui.Cells.SettingsSuggestionCell r0 = (org.telegram.ui.Cells.SettingsSuggestionCell) r0
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.passwordSuggestionRow
                if (r2 != r3) goto L_0x006a
                goto L_0x006b
            L_0x006a:
                r6 = 0
            L_0x006b:
                r0.setType(r6)
                goto L_0x0bc0
            L_0x0070:
                android.view.View r0 = r0.itemView
                r0.requestLayout()
                goto L_0x0bc0
            L_0x0077:
                android.view.View r0 = r0.itemView
                r12 = r0
                org.telegram.ui.Cells.UserCell r12 = (org.telegram.ui.Cells.UserCell) r12
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this     // Catch:{ Exception -> 0x00c2 }
                java.util.ArrayList r0 = r0.visibleSortedUsers     // Catch:{ Exception -> 0x00c2 }
                boolean r0 = r0.isEmpty()     // Catch:{ Exception -> 0x00c2 }
                if (r0 != 0) goto L_0x00ad
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this     // Catch:{ Exception -> 0x00c2 }
                java.util.ArrayList r0 = r0.visibleChatParticipants     // Catch:{ Exception -> 0x00c2 }
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this     // Catch:{ Exception -> 0x00c2 }
                java.util.ArrayList r3 = r3.visibleSortedUsers     // Catch:{ Exception -> 0x00c2 }
                org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this     // Catch:{ Exception -> 0x00c2 }
                int r4 = r4.membersStartRow     // Catch:{ Exception -> 0x00c2 }
                int r4 = r2 - r4
                java.lang.Object r3 = r3.get(r4)     // Catch:{ Exception -> 0x00c2 }
                java.lang.Integer r3 = (java.lang.Integer) r3     // Catch:{ Exception -> 0x00c2 }
                int r3 = r3.intValue()     // Catch:{ Exception -> 0x00c2 }
                java.lang.Object r0 = r0.get(r3)     // Catch:{ Exception -> 0x00c2 }
                org.telegram.tgnet.TLRPC$ChatParticipant r0 = (org.telegram.tgnet.TLRPC$ChatParticipant) r0     // Catch:{ Exception -> 0x00c2 }
                goto L_0x00c7
            L_0x00ad:
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this     // Catch:{ Exception -> 0x00c2 }
                java.util.ArrayList r0 = r0.visibleChatParticipants     // Catch:{ Exception -> 0x00c2 }
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this     // Catch:{ Exception -> 0x00c2 }
                int r3 = r3.membersStartRow     // Catch:{ Exception -> 0x00c2 }
                int r3 = r2 - r3
                java.lang.Object r0 = r0.get(r3)     // Catch:{ Exception -> 0x00c2 }
                org.telegram.tgnet.TLRPC$ChatParticipant r0 = (org.telegram.tgnet.TLRPC$ChatParticipant) r0     // Catch:{ Exception -> 0x00c2 }
                goto L_0x00c7
            L_0x00c2:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                r0 = r10
            L_0x00c7:
                if (r0 == 0) goto L_0x0bc0
                boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_chatChannelParticipant
                if (r3 == 0) goto L_0x00f8
                r3 = r0
                org.telegram.tgnet.TLRPC$TL_chatChannelParticipant r3 = (org.telegram.tgnet.TLRPC$TL_chatChannelParticipant) r3
                org.telegram.tgnet.TLRPC$ChannelParticipant r3 = r3.channelParticipant
                java.lang.String r4 = r3.rank
                boolean r4 = android.text.TextUtils.isEmpty(r4)
                if (r4 != 0) goto L_0x00de
                java.lang.String r3 = r3.rank
            L_0x00dc:
                r10 = r3
                goto L_0x0111
            L_0x00de:
                boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantCreator
                if (r4 == 0) goto L_0x00eb
                int r3 = org.telegram.messenger.R.string.ChannelCreator
                java.lang.String r4 = "ChannelCreator"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                goto L_0x00dc
            L_0x00eb:
                boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin
                if (r3 == 0) goto L_0x0111
                int r3 = org.telegram.messenger.R.string.ChannelAdmin
                java.lang.String r4 = "ChannelAdmin"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                goto L_0x00dc
            L_0x00f8:
                boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantCreator
                if (r3 == 0) goto L_0x0105
                int r3 = org.telegram.messenger.R.string.ChannelCreator
                java.lang.String r4 = "ChannelCreator"
                java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r4, r3)
                goto L_0x0111
            L_0x0105:
                boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantAdmin
                if (r3 == 0) goto L_0x0111
                int r3 = org.telegram.messenger.R.string.ChannelAdmin
                java.lang.String r4 = "ChannelAdmin"
                java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r4, r3)
            L_0x0111:
                r12.setAdminRole(r10)
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.messenger.MessagesController r3 = r3.getMessagesController()
                long r4 = r0.user_id
                java.lang.Long r0 = java.lang.Long.valueOf(r4)
                org.telegram.tgnet.TLRPC$User r13 = r3.getUser(r0)
                r14 = 0
                r15 = 0
                r16 = 0
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                int r0 = r0.membersEndRow
                int r0 = r0 - r6
                if (r2 == r0) goto L_0x0134
                r17 = 1
                goto L_0x0136
            L_0x0134:
                r17 = 0
            L_0x0136:
                r12.setData(r13, r14, r15, r16, r17)
                goto L_0x0bc0
            L_0x013b:
                android.view.View r0 = r0.itemView
                java.lang.Integer r3 = java.lang.Integer.valueOf(r20)
                r0.setTag(r3)
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.infoSectionRow
                if (r2 != r3) goto L_0x016c
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.lastSectionRow
                if (r3 != r5) goto L_0x016c
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.secretSettingsSectionRow
                if (r3 != r5) goto L_0x016c
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.sharedMediaRow
                if (r3 != r5) goto L_0x016c
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.membersSectionRow
                if (r3 == r5) goto L_0x01a8
            L_0x016c:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.secretSettingsSectionRow
                if (r2 == r3) goto L_0x01a8
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.lastSectionRow
                if (r2 == r3) goto L_0x01a8
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.membersSectionRow
                if (r2 != r3) goto L_0x0195
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                int r2 = r2.lastSectionRow
                if (r2 != r5) goto L_0x0195
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                int r2 = r2.sharedMediaRow
                if (r2 != r5) goto L_0x0195
                goto L_0x01a8
            L_0x0195:
                android.content.Context r2 = r1.mContext
                int r3 = org.telegram.messenger.R.drawable.greydivider
                org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                int r4 = r4.getThemedColor(r8)
                android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r3, (int) r4)
                r0.setBackgroundDrawable(r2)
                goto L_0x0bc0
            L_0x01a8:
                android.content.Context r2 = r1.mContext
                int r3 = org.telegram.messenger.R.drawable.greydivider_bottom
                org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                int r4 = r4.getThemedColor(r8)
                android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r3, (int) r4)
                r0.setBackgroundDrawable(r2)
                goto L_0x0bc0
            L_0x01bb:
                android.view.View r0 = r0.itemView
                org.telegram.ui.Cells.NotificationsCheckCell r0 = (org.telegram.ui.Cells.NotificationsCheckCell) r0
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.notificationsRow
                if (r2 != r3) goto L_0x0bc0
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                int r2 = r2.currentAccount
                android.content.SharedPreferences r2 = org.telegram.messenger.MessagesController.getNotificationsSettings(r2)
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                long r3 = r3.dialogId
                int r5 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
                if (r5 == 0) goto L_0x01e2
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                long r3 = r3.dialogId
                goto L_0x01fa
            L_0x01e2:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                long r3 = r3.userId
                int r5 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
                if (r5 == 0) goto L_0x01f3
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                long r3 = r3.userId
                goto L_0x01fa
            L_0x01f3:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                long r3 = r3.chatId
                long r3 = -r3
            L_0x01fa:
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                java.lang.String r8 = "custom_"
                r5.append(r8)
                r5.append(r3)
                java.lang.String r5 = r5.toString()
                boolean r5 = r2.getBoolean(r5, r11)
                java.lang.StringBuilder r8 = new java.lang.StringBuilder
                r8.<init>()
                java.lang.String r9 = "notify2_"
                r8.append(r9)
                r8.append(r3)
                java.lang.String r8 = r8.toString()
                boolean r8 = r2.contains(r8)
                java.lang.StringBuilder r12 = new java.lang.StringBuilder
                r12.<init>()
                r12.append(r9)
                r12.append(r3)
                java.lang.String r9 = r12.toString()
                int r9 = r2.getInt(r9, r11)
                java.lang.StringBuilder r12 = new java.lang.StringBuilder
                r12.<init>()
                java.lang.String r13 = "notifyuntil_"
                r12.append(r13)
                r12.append(r3)
                java.lang.String r12 = r12.toString()
                int r2 = r2.getInt(r12, r11)
                if (r9 != r7) goto L_0x02da
                r7 = 2147483647(0x7fffffff, float:NaN)
                if (r2 == r7) goto L_0x02da
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.ConnectionsManager r3 = r3.getConnectionsManager()
                int r3 = r3.getCurrentTime()
                int r2 = r2 - r3
                if (r2 > 0) goto L_0x0276
                if (r5 == 0) goto L_0x026b
                int r2 = org.telegram.messenger.R.string.NotificationsCustom
                java.lang.String r3 = "NotificationsCustom"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                goto L_0x0273
            L_0x026b:
                int r2 = org.telegram.messenger.R.string.NotificationsOn
                java.lang.String r3 = "NotificationsOn"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            L_0x0273:
                r10 = r2
                goto L_0x030c
            L_0x0276:
                r3 = 3600(0xe10, float:5.045E-42)
                java.lang.String r4 = "WillUnmuteIn"
                if (r2 >= r3) goto L_0x0293
                int r3 = org.telegram.messenger.R.string.WillUnmuteIn
                java.lang.Object[] r5 = new java.lang.Object[r6]
                int r2 = r2 / 60
                java.lang.Object[] r6 = new java.lang.Object[r11]
                java.lang.String r7 = "Minutes"
                java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r7, r2, r6)
                r5[r11] = r2
                java.lang.String r10 = org.telegram.messenger.LocaleController.formatString(r4, r3, r5)
            L_0x0290:
                r6 = 0
                goto L_0x030c
            L_0x0293:
                r3 = 86400(0x15180, float:1.21072E-40)
                r5 = 1114636288(0x42700000, float:60.0)
                if (r2 >= r3) goto L_0x02b6
                int r3 = org.telegram.messenger.R.string.WillUnmuteIn
                java.lang.Object[] r6 = new java.lang.Object[r6]
                float r2 = (float) r2
                float r2 = r2 / r5
                float r2 = r2 / r5
                double r7 = (double) r2
                double r7 = java.lang.Math.ceil(r7)
                int r2 = (int) r7
                java.lang.Object[] r5 = new java.lang.Object[r11]
                java.lang.String r7 = "Hours"
                java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r7, r2, r5)
                r6[r11] = r2
                java.lang.String r10 = org.telegram.messenger.LocaleController.formatString(r4, r3, r6)
                goto L_0x0290
            L_0x02b6:
                r3 = 31536000(0x1e13380, float:8.2725845E-38)
                if (r2 >= r3) goto L_0x0290
                int r3 = org.telegram.messenger.R.string.WillUnmuteIn
                java.lang.Object[] r6 = new java.lang.Object[r6]
                float r2 = (float) r2
                float r2 = r2 / r5
                float r2 = r2 / r5
                r5 = 1103101952(0x41CLASSNAME, float:24.0)
                float r2 = r2 / r5
                double r7 = (double) r2
                double r7 = java.lang.Math.ceil(r7)
                int r2 = (int) r7
                java.lang.Object[] r5 = new java.lang.Object[r11]
                java.lang.String r7 = "Days"
                java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r7, r2, r5)
                r6[r11] = r2
                java.lang.String r10 = org.telegram.messenger.LocaleController.formatString(r4, r3, r6)
                goto L_0x0290
            L_0x02da:
                if (r9 != 0) goto L_0x02ea
                if (r8 == 0) goto L_0x02df
                goto L_0x02ee
            L_0x02df:
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.messenger.NotificationsController r2 = r2.getNotificationsController()
                boolean r6 = r2.isGlobalNotificationsEnabled((long) r3)
                goto L_0x02ee
            L_0x02ea:
                if (r9 != r6) goto L_0x02ed
                goto L_0x02ee
            L_0x02ed:
                r6 = 0
            L_0x02ee:
                if (r6 == 0) goto L_0x02fb
                if (r5 == 0) goto L_0x02fb
                int r2 = org.telegram.messenger.R.string.NotificationsCustom
                java.lang.String r3 = "NotificationsCustom"
                java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r3, r2)
                goto L_0x030c
            L_0x02fb:
                if (r6 == 0) goto L_0x0302
                int r2 = org.telegram.messenger.R.string.NotificationsOn
                java.lang.String r3 = "NotificationsOn"
                goto L_0x0306
            L_0x0302:
                int r2 = org.telegram.messenger.R.string.NotificationsOff
                java.lang.String r3 = "NotificationsOff"
            L_0x0306:
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                goto L_0x0273
            L_0x030c:
                if (r10 != 0) goto L_0x0316
                int r2 = org.telegram.messenger.R.string.NotificationsOff
                java.lang.String r3 = "NotificationsOff"
                java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r3, r2)
            L_0x0316:
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                boolean r2 = r2.fragmentOpened
                r0.setAnimationsEnabled(r2)
                int r2 = org.telegram.messenger.R.string.Notifications
                java.lang.String r3 = "Notifications"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setTextAndValueAndCheck(r2, r10, r6, r11)
                goto L_0x0bc0
            L_0x032c:
                android.view.View r0 = r0.itemView
                org.telegram.ui.Cells.TextCell r0 = (org.telegram.ui.Cells.TextCell) r0
                java.lang.String r3 = "windowBackgroundWhiteGrayIcon"
                java.lang.String r7 = "windowBackgroundWhiteBlackText"
                r0.setColors(r3, r7)
                r0.setTag(r7)
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.settingsTimerRow
                if (r2 != r3) goto L_0x0378
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.messenger.MessagesController r2 = r2.getMessagesController()
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                long r3 = r3.dialogId
                int r3 = org.telegram.messenger.DialogObject.getEncryptedChatId(r3)
                java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
                org.telegram.tgnet.TLRPC$EncryptedChat r2 = r2.getEncryptedChat(r3)
                int r2 = r2.ttl
                if (r2 != 0) goto L_0x0367
                int r2 = org.telegram.messenger.R.string.ShortMessageLifetimeForever
                java.lang.String r3 = "ShortMessageLifetimeForever"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                goto L_0x036b
            L_0x0367:
                java.lang.String r2 = org.telegram.messenger.LocaleController.formatTTLString(r2)
            L_0x036b:
                int r3 = org.telegram.messenger.R.string.MessageLifetime
                java.lang.String r4 = "MessageLifetime"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                r0.setTextAndValue(r3, r2, r11)
                goto L_0x0bc0
            L_0x0378:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.unblockRow
                java.lang.String r7 = "windowBackgroundWhiteRedText5"
                if (r2 != r3) goto L_0x0392
                int r2 = org.telegram.messenger.R.string.Unblock
                java.lang.String r3 = "Unblock"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2, r11)
                r0.setColors(r10, r7)
                goto L_0x0bc0
            L_0x0392:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.settingsKeyRow
                if (r2 != r3) goto L_0x03c7
                org.telegram.ui.Components.IdenticonDrawable r2 = new org.telegram.ui.Components.IdenticonDrawable
                r2.<init>()
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.messenger.MessagesController r3 = r3.getMessagesController()
                org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                long r4 = r4.dialogId
                int r4 = org.telegram.messenger.DialogObject.getEncryptedChatId(r4)
                java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
                org.telegram.tgnet.TLRPC$EncryptedChat r3 = r3.getEncryptedChat(r4)
                r2.setEncryptedChat(r3)
                int r3 = org.telegram.messenger.R.string.EncryptionKey
                java.lang.String r4 = "EncryptionKey"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                r0.setTextAndValueDrawable(r3, r2, r11)
                goto L_0x0bc0
            L_0x03c7:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.joinRow
                if (r2 != r3) goto L_0x03f8
                java.lang.String r2 = "windowBackgroundWhiteBlueText2"
                r0.setColors(r10, r2)
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r2 = r2.currentChat
                boolean r2 = r2.megagroup
                if (r2 == 0) goto L_0x03eb
                int r2 = org.telegram.messenger.R.string.ProfileJoinGroup
                java.lang.String r3 = "ProfileJoinGroup"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2, r11)
                goto L_0x0bc0
            L_0x03eb:
                int r2 = org.telegram.messenger.R.string.ProfileJoinChannel
                java.lang.String r3 = "ProfileJoinChannel"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2, r11)
                goto L_0x0bc0
            L_0x03f8:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.subscribersRow
                java.lang.String r8 = "%d"
                if (r2 != r3) goto L_0x04c2
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r3 = r3.chatInfo
                if (r3 == 0) goto L_0x047a
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r3 = r3.currentChat
                boolean r3 = org.telegram.messenger.ChatObject.isChannel(r3)
                if (r3 == 0) goto L_0x044e
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r3 = r3.currentChat
                boolean r3 = r3.megagroup
                if (r3 != 0) goto L_0x044e
                int r3 = org.telegram.messenger.R.string.ChannelSubscribers
                java.lang.String r4 = "ChannelSubscribers"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                java.lang.Object[] r4 = new java.lang.Object[r6]
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r5 = r5.chatInfo
                int r5 = r5.participants_count
                java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
                r4[r11] = r5
                java.lang.String r4 = java.lang.String.format(r8, r4)
                int r5 = org.telegram.messenger.R.drawable.msg_groups
                org.telegram.ui.ProfileActivity r7 = org.telegram.ui.ProfileActivity.this
                int r7 = r7.membersSectionRow
                int r7 = r7 - r6
                if (r2 == r7) goto L_0x0448
                goto L_0x0449
            L_0x0448:
                r6 = 0
            L_0x0449:
                r0.setTextAndValueAndIcon(r3, r4, r5, r6)
                goto L_0x0bc0
            L_0x044e:
                int r3 = org.telegram.messenger.R.string.ChannelMembers
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                java.lang.Object[] r4 = new java.lang.Object[r6]
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r5 = r5.chatInfo
                int r5 = r5.participants_count
                java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
                r4[r11] = r5
                java.lang.String r4 = java.lang.String.format(r8, r4)
                int r5 = org.telegram.messenger.R.drawable.msg_groups
                org.telegram.ui.ProfileActivity r7 = org.telegram.ui.ProfileActivity.this
                int r7 = r7.membersSectionRow
                int r7 = r7 - r6
                if (r2 == r7) goto L_0x0474
                goto L_0x0475
            L_0x0474:
                r6 = 0
            L_0x0475:
                r0.setTextAndValueAndIcon(r3, r4, r5, r6)
                goto L_0x0bc0
            L_0x047a:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r3 = r3.currentChat
                boolean r3 = org.telegram.messenger.ChatObject.isChannel(r3)
                if (r3 == 0) goto L_0x04aa
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r3 = r3.currentChat
                boolean r3 = r3.megagroup
                if (r3 != 0) goto L_0x04aa
                int r3 = org.telegram.messenger.R.string.ChannelSubscribers
                java.lang.String r4 = "ChannelSubscribers"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                int r4 = org.telegram.messenger.R.drawable.msg_groups
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                int r5 = r5.membersSectionRow
                int r5 = r5 - r6
                if (r2 == r5) goto L_0x04a4
                goto L_0x04a5
            L_0x04a4:
                r6 = 0
            L_0x04a5:
                r0.setTextAndIcon((java.lang.String) r3, (int) r4, (boolean) r6)
                goto L_0x0bc0
            L_0x04aa:
                int r3 = org.telegram.messenger.R.string.ChannelMembers
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                int r4 = org.telegram.messenger.R.drawable.msg_groups
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                int r5 = r5.membersSectionRow
                int r5 = r5 - r6
                if (r2 == r5) goto L_0x04bc
                goto L_0x04bd
            L_0x04bc:
                r6 = 0
            L_0x04bd:
                r0.setTextAndIcon((java.lang.String) r3, (int) r4, (boolean) r6)
                goto L_0x0bc0
            L_0x04c2:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.subscribersRequestsRow
                if (r2 != r3) goto L_0x0500
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r3 = r3.chatInfo
                if (r3 == 0) goto L_0x0bc0
                int r3 = org.telegram.messenger.R.string.SubscribeRequests
                java.lang.String r4 = "SubscribeRequests"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                java.lang.Object[] r4 = new java.lang.Object[r6]
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r5 = r5.chatInfo
                int r5 = r5.requests_pending
                java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
                r4[r11] = r5
                java.lang.String r4 = java.lang.String.format(r8, r4)
                int r5 = org.telegram.messenger.R.drawable.msg_requests
                org.telegram.ui.ProfileActivity r7 = org.telegram.ui.ProfileActivity.this
                int r7 = r7.membersSectionRow
                int r7 = r7 - r6
                if (r2 == r7) goto L_0x04fa
                goto L_0x04fb
            L_0x04fa:
                r6 = 0
            L_0x04fb:
                r0.setTextAndValueAndIcon(r3, r4, r5, r6)
                goto L_0x0bc0
            L_0x0500:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.administratorsRow
                if (r2 != r3) goto L_0x0558
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r3 = r3.chatInfo
                if (r3 == 0) goto L_0x053e
                int r3 = org.telegram.messenger.R.string.ChannelAdministrators
                java.lang.String r4 = "ChannelAdministrators"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                java.lang.Object[] r4 = new java.lang.Object[r6]
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r5 = r5.chatInfo
                int r5 = r5.admins_count
                java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
                r4[r11] = r5
                java.lang.String r4 = java.lang.String.format(r8, r4)
                int r5 = org.telegram.messenger.R.drawable.msg_admins
                org.telegram.ui.ProfileActivity r7 = org.telegram.ui.ProfileActivity.this
                int r7 = r7.membersSectionRow
                int r7 = r7 - r6
                if (r2 == r7) goto L_0x0538
                goto L_0x0539
            L_0x0538:
                r6 = 0
            L_0x0539:
                r0.setTextAndValueAndIcon(r3, r4, r5, r6)
                goto L_0x0bc0
            L_0x053e:
                int r3 = org.telegram.messenger.R.string.ChannelAdministrators
                java.lang.String r4 = "ChannelAdministrators"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                int r4 = org.telegram.messenger.R.drawable.msg_admins
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                int r5 = r5.membersSectionRow
                int r5 = r5 - r6
                if (r2 == r5) goto L_0x0552
                goto L_0x0553
            L_0x0552:
                r6 = 0
            L_0x0553:
                r0.setTextAndIcon((java.lang.String) r3, (int) r4, (boolean) r6)
                goto L_0x0bc0
            L_0x0558:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.blockedUsersRow
                if (r2 != r3) goto L_0x05bc
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r3 = r3.chatInfo
                if (r3 == 0) goto L_0x05a2
                int r3 = org.telegram.messenger.R.string.ChannelBlacklist
                java.lang.String r4 = "ChannelBlacklist"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                java.lang.Object[] r4 = new java.lang.Object[r6]
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r5 = r5.chatInfo
                int r5 = r5.banned_count
                org.telegram.ui.ProfileActivity r7 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r7 = r7.chatInfo
                int r7 = r7.kicked_count
                int r5 = java.lang.Math.max(r5, r7)
                java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
                r4[r11] = r5
                java.lang.String r4 = java.lang.String.format(r8, r4)
                int r5 = org.telegram.messenger.R.drawable.msg_user_remove
                org.telegram.ui.ProfileActivity r7 = org.telegram.ui.ProfileActivity.this
                int r7 = r7.membersSectionRow
                int r7 = r7 - r6
                if (r2 == r7) goto L_0x059c
                goto L_0x059d
            L_0x059c:
                r6 = 0
            L_0x059d:
                r0.setTextAndValueAndIcon(r3, r4, r5, r6)
                goto L_0x0bc0
            L_0x05a2:
                int r3 = org.telegram.messenger.R.string.ChannelBlacklist
                java.lang.String r4 = "ChannelBlacklist"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                int r4 = org.telegram.messenger.R.drawable.msg_user_remove
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                int r5 = r5.membersSectionRow
                int r5 = r5 - r6
                if (r2 == r5) goto L_0x05b6
                goto L_0x05b7
            L_0x05b6:
                r6 = 0
            L_0x05b7:
                r0.setTextAndIcon((java.lang.String) r3, (int) r4, (boolean) r6)
                goto L_0x0bc0
            L_0x05bc:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.addMemberRow
                java.lang.String r4 = "windowBackgroundWhiteBlueButton"
                java.lang.String r8 = "windowBackgroundWhiteBlueIcon"
                if (r2 != r3) goto L_0x05e4
                r0.setColors(r8, r4)
                int r2 = org.telegram.messenger.R.string.AddMember
                java.lang.String r3 = "AddMember"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                int r3 = org.telegram.messenger.R.drawable.msg_contact_add
                org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                int r4 = r4.membersSectionRow
                if (r4 != r5) goto L_0x05de
                goto L_0x05df
            L_0x05de:
                r6 = 0
            L_0x05df:
                r0.setTextAndIcon((java.lang.String) r2, (int) r3, (boolean) r6)
                goto L_0x0bc0
            L_0x05e4:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.sendMessageRow
                if (r2 != r3) goto L_0x05f9
                int r2 = org.telegram.messenger.R.string.SendMessageLocation
                java.lang.String r3 = "SendMessageLocation"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2, r6)
                goto L_0x0bc0
            L_0x05f9:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.reportRow
                if (r2 != r3) goto L_0x0611
                int r2 = org.telegram.messenger.R.string.ReportUserLocation
                java.lang.String r3 = "ReportUserLocation"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2, r11)
                r0.setColors(r10, r7)
                goto L_0x0bc0
            L_0x0611:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.languageRow
                if (r2 != r3) goto L_0x062d
                int r2 = org.telegram.messenger.R.string.Language
                java.lang.String r3 = "Language"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                int r3 = org.telegram.messenger.R.drawable.msg_language
                r0.setTextAndIcon((java.lang.String) r2, (int) r3, (boolean) r11)
                r2 = 23
                r0.setImageLeft(r2)
                goto L_0x0bc0
            L_0x062d:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.notificationRow
                if (r2 != r3) goto L_0x0644
                int r2 = org.telegram.messenger.R.string.NotificationsAndSounds
                java.lang.String r3 = "NotificationsAndSounds"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                int r3 = org.telegram.messenger.R.drawable.msg_notifications
                r0.setTextAndIcon((java.lang.String) r2, (int) r3, (boolean) r6)
                goto L_0x0bc0
            L_0x0644:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.privacyRow
                if (r2 != r3) goto L_0x065b
                int r2 = org.telegram.messenger.R.string.PrivacySettings
                java.lang.String r3 = "PrivacySettings"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                int r3 = org.telegram.messenger.R.drawable.msg_secret
                r0.setTextAndIcon((java.lang.String) r2, (int) r3, (boolean) r6)
                goto L_0x0bc0
            L_0x065b:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.dataRow
                if (r2 != r3) goto L_0x0672
                int r2 = org.telegram.messenger.R.string.DataSettings
                java.lang.String r3 = "DataSettings"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                int r3 = org.telegram.messenger.R.drawable.msg_data
                r0.setTextAndIcon((java.lang.String) r2, (int) r3, (boolean) r6)
                goto L_0x0bc0
            L_0x0672:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.chatRow
                if (r2 != r3) goto L_0x0689
                int r2 = org.telegram.messenger.R.string.ChatSettings
                java.lang.String r3 = "ChatSettings"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                int r3 = org.telegram.messenger.R.drawable.msg_msgbubble3
                r0.setTextAndIcon((java.lang.String) r2, (int) r3, (boolean) r6)
                goto L_0x0bc0
            L_0x0689:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.filtersRow
                if (r2 != r3) goto L_0x06a0
                int r2 = org.telegram.messenger.R.string.Filters
                java.lang.String r3 = "Filters"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                int r3 = org.telegram.messenger.R.drawable.msg_folders
                r0.setTextAndIcon((java.lang.String) r2, (int) r3, (boolean) r6)
                goto L_0x0bc0
            L_0x06a0:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.stickersRow
                if (r2 != r3) goto L_0x06b5
                int r2 = org.telegram.messenger.R.string.StickersName
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString((int) r2)
                int r3 = org.telegram.messenger.R.drawable.msg_sticker
                r0.setTextAndIcon((java.lang.String) r2, (int) r3, (boolean) r6)
                goto L_0x0bc0
            L_0x06b5:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.questionRow
                if (r2 != r3) goto L_0x06cc
                int r2 = org.telegram.messenger.R.string.AskAQuestion
                java.lang.String r3 = "AskAQuestion"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                int r3 = org.telegram.messenger.R.drawable.msg_ask_question
                r0.setTextAndIcon((java.lang.String) r2, (int) r3, (boolean) r6)
                goto L_0x0bc0
            L_0x06cc:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.faqRow
                if (r2 != r3) goto L_0x06e3
                int r2 = org.telegram.messenger.R.string.TelegramFAQ
                java.lang.String r3 = "TelegramFAQ"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                int r3 = org.telegram.messenger.R.drawable.msg_help
                r0.setTextAndIcon((java.lang.String) r2, (int) r3, (boolean) r6)
                goto L_0x0bc0
            L_0x06e3:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.policyRow
                if (r2 != r3) goto L_0x06fa
                int r2 = org.telegram.messenger.R.string.PrivacyPolicy
                java.lang.String r3 = "PrivacyPolicy"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                int r3 = org.telegram.messenger.R.drawable.msg_policy
                r0.setTextAndIcon((java.lang.String) r2, (int) r3, (boolean) r11)
                goto L_0x0bc0
            L_0x06fa:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.sendLogsRow
                if (r2 != r3) goto L_0x070f
                int r2 = org.telegram.messenger.R.string.DebugSendLogs
                java.lang.String r3 = "DebugSendLogs"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2, r6)
                goto L_0x0bc0
            L_0x070f:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.sendLastLogsRow
                if (r2 != r3) goto L_0x0724
                int r2 = org.telegram.messenger.R.string.DebugSendLastLogs
                java.lang.String r3 = "DebugSendLastLogs"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2, r6)
                goto L_0x0bc0
            L_0x0724:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.clearLogsRow
                if (r2 != r3) goto L_0x0743
                int r2 = org.telegram.messenger.R.string.DebugClearLogs
                java.lang.String r3 = "DebugClearLogs"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.switchBackendRow
                if (r3 == r5) goto L_0x073d
                goto L_0x073e
            L_0x073d:
                r6 = 0
            L_0x073e:
                r0.setText(r2, r6)
                goto L_0x0bc0
            L_0x0743:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.switchBackendRow
                if (r2 != r3) goto L_0x0752
                java.lang.String r2 = "Switch Backend"
                r0.setText(r2, r11)
                goto L_0x0bc0
            L_0x0752:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.devicesRow
                if (r2 != r3) goto L_0x0769
                int r2 = org.telegram.messenger.R.string.Devices
                java.lang.String r3 = "Devices"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                int r3 = org.telegram.messenger.R.drawable.menu_devices
                r0.setTextAndIcon((java.lang.String) r2, (int) r3, (boolean) r6)
                goto L_0x0bc0
            L_0x0769:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.setAvatarRow
                if (r2 != r3) goto L_0x07b2
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.ui.Components.RLottieDrawable r2 = r2.cellCameraDrawable
                r3 = 86
                r2.setCustomEndFrame(r3)
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.ui.Components.RLottieDrawable r2 = r2.cellCameraDrawable
                r3 = 85
                r2.setCurrentFrame(r3, r11)
                int r2 = org.telegram.messenger.R.string.SetProfilePhoto
                java.lang.String r3 = "SetProfilePhoto"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.ui.Components.RLottieDrawable r3 = r3.cellCameraDrawable
                r0.setTextAndIcon((java.lang.String) r2, (android.graphics.drawable.Drawable) r3, (boolean) r11)
                r0.setColors(r8, r4)
                org.telegram.ui.Components.RLottieImageView r2 = r0.getImageView()
                r3 = 1090519040(0x41000000, float:8.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                r2.setPadding(r11, r11, r11, r3)
                r0.setImageLeft(r13)
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.ui.Cells.TextCell unused = r2.setAvatarCell = r0
                goto L_0x0bc0
            L_0x07b2:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.addToGroupButtonRow
                if (r2 != r3) goto L_0x07cc
                int r2 = org.telegram.messenger.R.string.AddToGroupOrChannel
                java.lang.String r3 = "AddToGroupOrChannel"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                int r3 = org.telegram.messenger.R.drawable.msg_groups_create
                r0.setTextAndIcon((java.lang.String) r2, (int) r3, (boolean) r11)
                r0.setColors(r8, r4)
                goto L_0x0bc0
            L_0x07cc:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.premiumRow
                if (r2 != r3) goto L_0x0bc0
                int r2 = org.telegram.messenger.R.string.TelegramPremium
                java.lang.String r3 = "TelegramPremium"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                org.telegram.ui.Components.Premium.PremiumGradient r3 = org.telegram.ui.Components.Premium.PremiumGradient.getInstance()
                org.telegram.ui.Components.Premium.PremiumGradient$InternalDrawable r3 = r3.premiumStarMenuDrawable
                r0.setTextAndIcon((java.lang.String) r2, (android.graphics.drawable.Drawable) r3, (boolean) r11)
                r2 = 23
                r0.setImageLeft(r2)
                goto L_0x0bc0
            L_0x07ec:
                android.view.View r0 = r0.itemView
                org.telegram.ui.Cells.AboutLinkCell r0 = (org.telegram.ui.Cells.AboutLinkCell) r0
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.userInfoRow
                java.lang.String r4 = "UserBio"
                if (r2 != r3) goto L_0x0850
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$UserFull r3 = r3.userInfo
                org.telegram.tgnet.TLRPC$User r3 = r3.user
                if (r3 == 0) goto L_0x080d
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$UserFull r3 = r3.userInfo
                org.telegram.tgnet.TLRPC$User r3 = r3.user
                goto L_0x0823
            L_0x080d:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.messenger.MessagesController r3 = r3.getMessagesController()
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$UserFull r5 = r5.userInfo
                long r7 = r5.id
                java.lang.Long r5 = java.lang.Long.valueOf(r7)
                org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r5)
            L_0x0823:
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                boolean r5 = r5.isBot
                if (r5 != 0) goto L_0x083d
                if (r3 == 0) goto L_0x083c
                boolean r3 = r3.premium
                if (r3 == 0) goto L_0x083c
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$UserFull r3 = r3.userInfo
                java.lang.String r3 = r3.about
                if (r3 == 0) goto L_0x083c
                goto L_0x083d
            L_0x083c:
                r6 = 0
            L_0x083d:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$UserFull r3 = r3.userInfo
                java.lang.String r3 = r3.about
                int r5 = org.telegram.messenger.R.string.UserBio
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r5)
                r0.setTextAndValue(r3, r4, r6)
                goto L_0x0904
            L_0x0850:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.channelInfoRow
                if (r2 != r3) goto L_0x088c
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r3 = r3.chatInfo
                java.lang.String r3 = r3.about
            L_0x0860:
                java.lang.String r4 = "\n\n\n"
                boolean r5 = r3.contains(r4)
                if (r5 == 0) goto L_0x086f
                java.lang.String r5 = "\n\n"
                java.lang.String r3 = r3.replace(r4, r5)
                goto L_0x0860
            L_0x086f:
                org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r4 = r4.currentChat
                boolean r4 = org.telegram.messenger.ChatObject.isChannel(r4)
                if (r4 == 0) goto L_0x0886
                org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r4 = r4.currentChat
                boolean r4 = r4.megagroup
                if (r4 != 0) goto L_0x0886
                goto L_0x0887
            L_0x0886:
                r6 = 0
            L_0x0887:
                r0.setText(r3, r6)
                goto L_0x0904
            L_0x088c:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.bioRow
                if (r2 != r3) goto L_0x0904
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$UserFull r3 = r3.userInfo
                if (r3 == 0) goto L_0x08c2
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$UserFull r3 = r3.userInfo
                java.lang.String r3 = r3.about
                boolean r3 = android.text.TextUtils.isEmpty(r3)
                if (r3 != 0) goto L_0x08ab
                goto L_0x08c2
            L_0x08ab:
                int r3 = org.telegram.messenger.R.string.UserBio
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                int r4 = org.telegram.messenger.R.string.UserBioDetail
                java.lang.String r5 = "UserBioDetail"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r0.setTextAndValue(r3, r4, r11)
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                java.lang.CharSequence unused = r3.currentBio = r10
                goto L_0x0901
            L_0x08c2:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$UserFull r3 = r3.userInfo
                if (r3 != 0) goto L_0x08d3
                int r3 = org.telegram.messenger.R.string.Loading
                java.lang.String r5 = "Loading"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
                goto L_0x08db
            L_0x08d3:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$UserFull r3 = r3.userInfo
                java.lang.String r3 = r3.about
            L_0x08db:
                int r5 = org.telegram.messenger.R.string.UserBio
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r5)
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                org.telegram.messenger.UserConfig r5 = r5.getUserConfig()
                boolean r5 = r5.isPremium()
                r0.setTextAndValue(r3, r4, r5)
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$UserFull r4 = r3.userInfo
                if (r4 == 0) goto L_0x08fe
                org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$UserFull r4 = r4.userInfo
                java.lang.String r10 = r4.about
            L_0x08fe:
                java.lang.CharSequence unused = r3.currentBio = r10
            L_0x0901:
                r0.setMoreButtonDisabled(r6)
            L_0x0904:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.bioRow
                if (r2 != r3) goto L_0x0916
                org.telegram.ui.ProfileActivity$ListAdapter$$ExternalSyntheticLambda0 r2 = new org.telegram.ui.ProfileActivity$ListAdapter$$ExternalSyntheticLambda0
                r2.<init>(r1)
                r0.setOnClickListener(r2)
                goto L_0x0bc0
            L_0x0916:
                org.telegram.ui.ProfileActivity$ListAdapter$$ExternalSyntheticLambda1 r3 = new org.telegram.ui.ProfileActivity$ListAdapter$$ExternalSyntheticLambda1
                r3.<init>(r1, r2, r0)
                r0.setOnClickListener(r3)
                goto L_0x0bc0
            L_0x0920:
                android.view.View r0 = r0.itemView
                org.telegram.ui.Cells.TextDetailCell r0 = (org.telegram.ui.Cells.TextDetailCell) r0
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.usernameRow
                if (r2 != r3) goto L_0x0954
                android.content.Context r3 = r0.getContext()
                int r4 = org.telegram.messenger.R.drawable.msg_qr_mini
                android.graphics.drawable.Drawable r3 = androidx.core.content.ContextCompat.getDrawable(r3, r4)
                android.graphics.PorterDuffColorFilter r4 = new android.graphics.PorterDuffColorFilter
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                java.lang.String r7 = "switch2TrackChecked"
                int r5 = r5.getThemedColor(r7)
                android.graphics.PorterDuff$Mode r7 = android.graphics.PorterDuff.Mode.MULTIPLY
                r4.<init>(r5, r7)
                r3.setColorFilter(r4)
                int r4 = org.telegram.messenger.R.string.GetQRCode
                java.lang.String r5 = "GetQRCode"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r0.setImage(r3, r4)
                goto L_0x0957
            L_0x0954:
                r0.setImage(r10)
            L_0x0957:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.phoneRow
                java.lang.String r4 = "+"
                if (r2 != r3) goto L_0x09ac
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.messenger.MessagesController r3 = r3.getMessagesController()
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                long r5 = r5.userId
                java.lang.Long r5 = java.lang.Long.valueOf(r5)
                org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r5)
                java.lang.String r5 = r3.phone
                boolean r5 = android.text.TextUtils.isEmpty(r5)
                if (r5 != 0) goto L_0x0997
                org.telegram.PhoneFormat.PhoneFormat r5 = org.telegram.PhoneFormat.PhoneFormat.getInstance()
                java.lang.StringBuilder r6 = new java.lang.StringBuilder
                r6.<init>()
                r6.append(r4)
                java.lang.String r3 = r3.phone
                r6.append(r3)
                java.lang.String r3 = r6.toString()
                java.lang.String r3 = r5.format(r3)
                goto L_0x099f
            L_0x0997:
                int r3 = org.telegram.messenger.R.string.PhoneHidden
                java.lang.String r4 = "PhoneHidden"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            L_0x099f:
                int r4 = org.telegram.messenger.R.string.PhoneMobile
                java.lang.String r5 = "PhoneMobile"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r0.setTextAndValue(r3, r4, r11)
                goto L_0x0b14
            L_0x09ac:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.usernameRow
                java.lang.String r5 = "@"
                java.lang.String r7 = "Username"
                if (r2 != r3) goto L_0x0a46
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                long r3 = r3.userId
                int r6 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
                if (r6 == 0) goto L_0x09ff
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.messenger.MessagesController r3 = r3.getMessagesController()
                org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                long r8 = r4.userId
                java.lang.Long r4 = java.lang.Long.valueOf(r8)
                org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
                if (r3 == 0) goto L_0x09f2
                java.lang.String r4 = r3.username
                boolean r4 = android.text.TextUtils.isEmpty(r4)
                if (r4 != 0) goto L_0x09f2
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                r4.append(r5)
                java.lang.String r3 = r3.username
                r4.append(r3)
                java.lang.String r3 = r4.toString()
                goto L_0x09f4
            L_0x09f2:
                java.lang.String r3 = "-"
            L_0x09f4:
                int r4 = org.telegram.messenger.R.string.Username
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
                r0.setTextAndValue(r3, r4, r11)
                goto L_0x0b14
            L_0x09ff:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r3 = r3.currentChat
                if (r3 == 0) goto L_0x0b14
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.messenger.MessagesController r3 = r3.getMessagesController()
                org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                long r4 = r4.chatId
                java.lang.Long r4 = java.lang.Long.valueOf(r4)
                org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r4)
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                org.telegram.messenger.MessagesController r5 = r5.getMessagesController()
                java.lang.String r5 = r5.linkPrefix
                r4.append(r5)
                java.lang.String r5 = "/"
                r4.append(r5)
                java.lang.String r3 = r3.username
                r4.append(r3)
                java.lang.String r3 = r4.toString()
                int r4 = org.telegram.messenger.R.string.InviteLink
                java.lang.String r5 = "InviteLink"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r0.setTextAndValue(r3, r4, r11)
                goto L_0x0b14
            L_0x0a46:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.locationRow
                if (r2 != r3) goto L_0x0a7b
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r3 = r3.chatInfo
                if (r3 == 0) goto L_0x0b14
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r3 = r3.chatInfo
                org.telegram.tgnet.TLRPC$ChannelLocation r3 = r3.location
                boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_channelLocation
                if (r3 == 0) goto L_0x0b14
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r3 = r3.chatInfo
                org.telegram.tgnet.TLRPC$ChannelLocation r3 = r3.location
                org.telegram.tgnet.TLRPC$TL_channelLocation r3 = (org.telegram.tgnet.TLRPC$TL_channelLocation) r3
                java.lang.String r3 = r3.address
                int r4 = org.telegram.messenger.R.string.AttachLocation
                java.lang.String r5 = "AttachLocation"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r0.setTextAndValue(r3, r4, r11)
                goto L_0x0b14
            L_0x0a7b:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.numberRow
                if (r2 != r3) goto L_0x0ace
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.currentAccount
                org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r3)
                org.telegram.tgnet.TLRPC$User r3 = r3.getCurrentUser()
                if (r3 == 0) goto L_0x0ab7
                java.lang.String r5 = r3.phone
                if (r5 == 0) goto L_0x0ab7
                int r5 = r5.length()
                if (r5 == 0) goto L_0x0ab7
                org.telegram.PhoneFormat.PhoneFormat r5 = org.telegram.PhoneFormat.PhoneFormat.getInstance()
                java.lang.StringBuilder r7 = new java.lang.StringBuilder
                r7.<init>()
                r7.append(r4)
                java.lang.String r3 = r3.phone
                r7.append(r3)
                java.lang.String r3 = r7.toString()
                java.lang.String r3 = r5.format(r3)
                goto L_0x0abf
            L_0x0ab7:
                int r3 = org.telegram.messenger.R.string.NumberUnknown
                java.lang.String r4 = "NumberUnknown"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            L_0x0abf:
                int r4 = org.telegram.messenger.R.string.TapToChangePhone
                java.lang.String r5 = "TapToChangePhone"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r0.setTextAndValue(r3, r4, r6)
                r0.setContentDescriptionValueFirst(r11)
                goto L_0x0b14
            L_0x0ace:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.setUsernameRow
                if (r2 != r3) goto L_0x0b14
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.currentAccount
                org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r3)
                org.telegram.tgnet.TLRPC$User r3 = r3.getCurrentUser()
                if (r3 == 0) goto L_0x0b00
                java.lang.String r4 = r3.username
                boolean r4 = android.text.TextUtils.isEmpty(r4)
                if (r4 != 0) goto L_0x0b00
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                r4.append(r5)
                java.lang.String r3 = r3.username
                r4.append(r3)
                java.lang.String r3 = r4.toString()
                goto L_0x0b08
            L_0x0b00:
                int r3 = org.telegram.messenger.R.string.UsernameEmpty
                java.lang.String r4 = "UsernameEmpty"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            L_0x0b08:
                int r4 = org.telegram.messenger.R.string.Username
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
                r0.setTextAndValue(r3, r4, r6)
                r0.setContentDescriptionValueFirst(r6)
            L_0x0b14:
                java.lang.Integer r2 = java.lang.Integer.valueOf(r20)
                r0.setTag(r2)
                goto L_0x0bc0
            L_0x0b1d:
                android.view.View r0 = r0.itemView
                org.telegram.ui.Cells.HeaderCell r0 = (org.telegram.ui.Cells.HeaderCell) r0
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.infoHeaderRow
                if (r2 != r3) goto L_0x0b5f
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r2 = r2.currentChat
                boolean r2 = org.telegram.messenger.ChatObject.isChannel(r2)
                if (r2 == 0) goto L_0x0b53
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r2 = r2.currentChat
                boolean r2 = r2.megagroup
                if (r2 != 0) goto L_0x0b53
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                int r2 = r2.channelInfoRow
                if (r2 == r5) goto L_0x0b53
                int r2 = org.telegram.messenger.R.string.ReportChatDescription
                java.lang.String r3 = "ReportChatDescription"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2)
                goto L_0x0bc0
            L_0x0b53:
                int r2 = org.telegram.messenger.R.string.Info
                java.lang.String r3 = "Info"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2)
                goto L_0x0bc0
            L_0x0b5f:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.membersHeaderRow
                if (r2 != r3) goto L_0x0b71
                int r2 = org.telegram.messenger.R.string.ChannelMembers
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
                r0.setText(r2)
                goto L_0x0bc0
            L_0x0b71:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.settingsSectionRow2
                if (r2 != r3) goto L_0x0b85
                int r2 = org.telegram.messenger.R.string.SETTINGS
                java.lang.String r3 = "SETTINGS"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2)
                goto L_0x0bc0
            L_0x0b85:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.numberSectionRow
                if (r2 != r3) goto L_0x0b99
                int r2 = org.telegram.messenger.R.string.Account
                java.lang.String r3 = "Account"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2)
                goto L_0x0bc0
            L_0x0b99:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.helpHeaderRow
                if (r2 != r3) goto L_0x0bad
                int r2 = org.telegram.messenger.R.string.SettingsHelp
                java.lang.String r3 = "SettingsHelp"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2)
                goto L_0x0bc0
            L_0x0bad:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.debugHeaderRow
                if (r2 != r3) goto L_0x0bc0
                int r2 = org.telegram.messenger.R.string.SettingsDebug
                java.lang.String r3 = "SettingsDebug"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2)
            L_0x0bc0:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onBindViewHolder$1(View view) {
            if (ProfileActivity.this.userInfo != null) {
                ProfileActivity.this.presentFragment(new ChangeBioActivity());
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onBindViewHolder$2(int i, AboutLinkCell aboutLinkCell, View view) {
            boolean unused = ProfileActivity.this.processOnClickOrPress(i, aboutLinkCell);
        }

        public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getAdapterPosition() == ProfileActivity.this.setAvatarRow) {
                TextCell unused = ProfileActivity.this.setAvatarCell = null;
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            if (ProfileActivity.this.notificationRow != -1) {
                int adapterPosition = viewHolder.getAdapterPosition();
                if (adapterPosition == ProfileActivity.this.notificationRow || adapterPosition == ProfileActivity.this.numberRow || adapterPosition == ProfileActivity.this.privacyRow || adapterPosition == ProfileActivity.this.languageRow || adapterPosition == ProfileActivity.this.setUsernameRow || adapterPosition == ProfileActivity.this.bioRow || adapterPosition == ProfileActivity.this.versionRow || adapterPosition == ProfileActivity.this.dataRow || adapterPosition == ProfileActivity.this.chatRow || adapterPosition == ProfileActivity.this.questionRow || adapterPosition == ProfileActivity.this.devicesRow || adapterPosition == ProfileActivity.this.filtersRow || adapterPosition == ProfileActivity.this.stickersRow || adapterPosition == ProfileActivity.this.faqRow || adapterPosition == ProfileActivity.this.policyRow || adapterPosition == ProfileActivity.this.sendLogsRow || adapterPosition == ProfileActivity.this.sendLastLogsRow || adapterPosition == ProfileActivity.this.clearLogsRow || adapterPosition == ProfileActivity.this.switchBackendRow || adapterPosition == ProfileActivity.this.setAvatarRow || adapterPosition == ProfileActivity.this.addToGroupButtonRow || adapterPosition == ProfileActivity.this.premiumRow) {
                    return true;
                }
                return false;
            }
            View view = viewHolder.itemView;
            if (view instanceof UserCell) {
                Object currentObject = ((UserCell) view).getCurrentObject();
                if ((currentObject instanceof TLRPC$User) && UserObject.isUserSelf((TLRPC$User) currentObject)) {
                    return false;
                }
            }
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 1 || itemViewType == 5 || itemViewType == 7 || itemViewType == 11 || itemViewType == 12 || itemViewType == 13 || itemViewType == 9 || itemViewType == 10) {
                return false;
            }
            return true;
        }

        public int getItemCount() {
            return ProfileActivity.this.rowCount;
        }

        public int getItemViewType(int i) {
            if (i == ProfileActivity.this.infoHeaderRow || i == ProfileActivity.this.membersHeaderRow || i == ProfileActivity.this.settingsSectionRow2 || i == ProfileActivity.this.numberSectionRow || i == ProfileActivity.this.helpHeaderRow || i == ProfileActivity.this.debugHeaderRow) {
                return 1;
            }
            if (i == ProfileActivity.this.phoneRow || i == ProfileActivity.this.usernameRow || i == ProfileActivity.this.locationRow || i == ProfileActivity.this.numberRow || i == ProfileActivity.this.setUsernameRow) {
                return 2;
            }
            if (i == ProfileActivity.this.userInfoRow || i == ProfileActivity.this.channelInfoRow || i == ProfileActivity.this.bioRow) {
                return 3;
            }
            if (i == ProfileActivity.this.settingsTimerRow || i == ProfileActivity.this.settingsKeyRow || i == ProfileActivity.this.reportRow || i == ProfileActivity.this.subscribersRow || i == ProfileActivity.this.subscribersRequestsRow || i == ProfileActivity.this.administratorsRow || i == ProfileActivity.this.blockedUsersRow || i == ProfileActivity.this.addMemberRow || i == ProfileActivity.this.joinRow || i == ProfileActivity.this.unblockRow || i == ProfileActivity.this.sendMessageRow || i == ProfileActivity.this.notificationRow || i == ProfileActivity.this.privacyRow || i == ProfileActivity.this.languageRow || i == ProfileActivity.this.dataRow || i == ProfileActivity.this.chatRow || i == ProfileActivity.this.questionRow || i == ProfileActivity.this.devicesRow || i == ProfileActivity.this.filtersRow || i == ProfileActivity.this.stickersRow || i == ProfileActivity.this.faqRow || i == ProfileActivity.this.policyRow || i == ProfileActivity.this.sendLogsRow || i == ProfileActivity.this.sendLastLogsRow || i == ProfileActivity.this.clearLogsRow || i == ProfileActivity.this.switchBackendRow || i == ProfileActivity.this.setAvatarRow || i == ProfileActivity.this.addToGroupButtonRow) {
                return 4;
            }
            if (i == ProfileActivity.this.notificationsDividerRow) {
                return 5;
            }
            if (i == ProfileActivity.this.notificationsRow) {
                return 6;
            }
            if (i == ProfileActivity.this.infoSectionRow || i == ProfileActivity.this.lastSectionRow || i == ProfileActivity.this.membersSectionRow || i == ProfileActivity.this.secretSettingsSectionRow || i == ProfileActivity.this.settingsSectionRow || i == ProfileActivity.this.devicesSectionRow || i == ProfileActivity.this.helpSectionCell || i == ProfileActivity.this.setAvatarSectionRow || i == ProfileActivity.this.passwordSuggestionSectionRow || i == ProfileActivity.this.phoneSuggestionSectionRow || i == ProfileActivity.this.premiumSectionsRow) {
                return 7;
            }
            if (i >= ProfileActivity.this.membersStartRow && i < ProfileActivity.this.membersEndRow) {
                return 8;
            }
            if (i == ProfileActivity.this.emptyRow) {
                return 11;
            }
            if (i == ProfileActivity.this.bottomPaddingRow) {
                return 12;
            }
            if (i == ProfileActivity.this.sharedMediaRow) {
                return 13;
            }
            if (i == ProfileActivity.this.versionRow) {
                return 14;
            }
            if (i == ProfileActivity.this.passwordSuggestionRow || i == ProfileActivity.this.phoneSuggestionRow) {
                return 15;
            }
            if (i == ProfileActivity.this.addToGroupInfoRow) {
                return 17;
            }
            return i == ProfileActivity.this.premiumRow ? 18 : 0;
        }
    }

    private class SearchAdapter extends RecyclerListView.SelectionAdapter {
        /* access modifiers changed from: private */
        public ArrayList<MessagesController.FaqSearchResult> faqSearchArray;
        /* access modifiers changed from: private */
        public ArrayList<MessagesController.FaqSearchResult> faqSearchResults;
        /* access modifiers changed from: private */
        public TLRPC$WebPage faqWebPage;
        private String lastSearchString;
        private boolean loadingFaqPage;
        private Context mContext;
        /* access modifiers changed from: private */
        public ArrayList<Object> recentSearches;
        private ArrayList<CharSequence> resultNames;
        private SearchResult[] searchArray;
        /* access modifiers changed from: private */
        public ArrayList<SearchResult> searchResults;
        private Runnable searchRunnable;
        /* access modifiers changed from: private */
        public boolean searchWas;
        final /* synthetic */ ProfileActivity this$0;

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
                AndroidUtilities.scrollToFragmentRow(SearchAdapter.this.this$0.parentLayout, this.rowName);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0() {
            ProfileActivity profileActivity = this.this$0;
            profileActivity.presentFragment(new ChangeNameActivity(profileActivity.resourcesProvider));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$1() {
            this.this$0.presentFragment(new ActionIntroActivity(3));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$2() {
            int i = 0;
            while (true) {
                if (i >= 4) {
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

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$3() {
            if (this.this$0.userInfo != null) {
                this.this$0.presentFragment(new ChangeBioActivity());
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$4() {
            this.this$0.presentFragment(new NotificationsSettingsActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$5() {
            this.this$0.presentFragment(new NotificationsCustomSettingsActivity(1, new ArrayList(), true));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$6() {
            this.this$0.presentFragment(new NotificationsCustomSettingsActivity(0, new ArrayList(), true));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$7() {
            this.this$0.presentFragment(new NotificationsCustomSettingsActivity(2, new ArrayList(), true));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$8() {
            this.this$0.presentFragment(new NotificationsSettingsActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$9() {
            this.this$0.presentFragment(new NotificationsSettingsActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$10() {
            this.this$0.presentFragment(new NotificationsSettingsActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$11() {
            this.this$0.presentFragment(new NotificationsSettingsActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$12() {
            this.this$0.presentFragment(new NotificationsSettingsActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$13() {
            this.this$0.presentFragment(new NotificationsSettingsActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$14() {
            this.this$0.presentFragment(new PrivacySettingsActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$15() {
            this.this$0.presentFragment(new PrivacyUsersActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$16() {
            this.this$0.presentFragment(new PrivacyControlActivity(6, true));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$17() {
            this.this$0.presentFragment(new PrivacyControlActivity(0, true));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$18() {
            this.this$0.presentFragment(new PrivacyControlActivity(4, true));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$19() {
            this.this$0.presentFragment(new PrivacyControlActivity(5, true));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$20() {
            this.this$0.presentFragment(new PrivacyControlActivity(3, true));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$21() {
            this.this$0.presentFragment(new PrivacyControlActivity(2, true));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$22() {
            this.this$0.presentFragment(new PrivacyControlActivity(1, true));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$23() {
            if (!this.this$0.getUserConfig().isPremium()) {
                try {
                    this.this$0.fragmentView.performHapticFeedback(3, 2);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                BulletinFactory.of(this.this$0).createRestrictVoiceMessagesPremiumBulletin().show();
                return;
            }
            this.this$0.presentFragment(new PrivacyControlActivity(8, true));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$24() {
            this.this$0.presentFragment(PasscodeActivity.determineOpenFragment());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$25() {
            this.this$0.presentFragment(new TwoStepVerificationActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$26() {
            this.this$0.presentFragment(new SessionsActivity(0));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$27() {
            this.this$0.presentFragment(new PrivacySettingsActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$28() {
            this.this$0.presentFragment(new PrivacySettingsActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$29() {
            this.this$0.presentFragment(new PrivacySettingsActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$30() {
            this.this$0.presentFragment(new SessionsActivity(1));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$31() {
            this.this$0.presentFragment(new PrivacySettingsActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$32() {
            this.this$0.presentFragment(new PrivacySettingsActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$33() {
            this.this$0.presentFragment(new PrivacySettingsActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$34() {
            this.this$0.presentFragment(new PrivacySettingsActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$35() {
            this.this$0.presentFragment(new PrivacySettingsActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$36() {
            this.this$0.presentFragment(new SessionsActivity(0));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$37() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$38() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$39() {
            this.this$0.presentFragment(new CacheControlActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$40() {
            this.this$0.presentFragment(new CacheControlActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$41() {
            this.this$0.presentFragment(new CacheControlActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$42() {
            this.this$0.presentFragment(new CacheControlActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$43() {
            this.this$0.presentFragment(new DataUsageActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$44() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$45() {
            this.this$0.presentFragment(new DataAutoDownloadActivity(0));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$46() {
            this.this$0.presentFragment(new DataAutoDownloadActivity(1));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$47() {
            this.this$0.presentFragment(new DataAutoDownloadActivity(2));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$48() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$49() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$50() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$51() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$52() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$53() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$54() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$55() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$56() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$57() {
            this.this$0.presentFragment(new ProxyListActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$58() {
            this.this$0.presentFragment(new ProxyListActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$59() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$60() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$61() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$62() {
            this.this$0.presentFragment(new WallpapersListActivity(0));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$63() {
            this.this$0.presentFragment(new WallpapersListActivity(1));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$64() {
            this.this$0.presentFragment(new WallpapersListActivity(0));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$65() {
            this.this$0.presentFragment(new ThemeActivity(1));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$66() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$67() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$68() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$69() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$70() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$71() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$72() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$73() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$74() {
            this.this$0.presentFragment(new StickersActivity(0));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$75() {
            this.this$0.presentFragment(new StickersActivity(0));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$76() {
            this.this$0.presentFragment(new FeaturedStickersActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$77() {
            this.this$0.presentFragment(new StickersActivity(1));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$78() {
            this.this$0.presentFragment(new ArchivedStickersActivity(0));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$79() {
            this.this$0.presentFragment(new ArchivedStickersActivity(1));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$80() {
            this.this$0.presentFragment(new LanguageSelectActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$81() {
            ProfileActivity profileActivity = this.this$0;
            profileActivity.showDialog(AlertsCreator.createSupportAlert(profileActivity, (Theme.ResourcesProvider) null));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$82() {
            Browser.openUrl((Context) this.this$0.getParentActivity(), LocaleController.getString("TelegramFaqUrl", R.string.TelegramFaqUrl));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$83() {
            Browser.openUrl((Context) this.this$0.getParentActivity(), LocaleController.getString("PrivacyPolicyUrl", R.string.PrivacyPolicyUrl));
        }

        public SearchAdapter(ProfileActivity profileActivity, Context context) {
            SearchResult searchResult;
            String[] strArr;
            this.this$0 = profileActivity;
            SearchResult[] searchResultArr = new SearchResult[84];
            searchResultArr[0] = new SearchResult(this, 500, LocaleController.getString("EditName", R.string.EditName), 0, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda10(this));
            searchResultArr[1] = new SearchResult(this, 501, LocaleController.getString("ChangePhoneNumber", R.string.ChangePhoneNumber), 0, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda11(this));
            searchResultArr[2] = new SearchResult(this, 502, LocaleController.getString("AddAnotherAccount", R.string.AddAnotherAccount), 0, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda32(this));
            searchResultArr[3] = new SearchResult(this, 503, LocaleController.getString("UserBio", R.string.UserBio), 0, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda63(this));
            int i = R.string.NotificationsAndSounds;
            String string = LocaleController.getString("NotificationsAndSounds", i);
            int i2 = R.drawable.msg_notifications;
            searchResultArr[4] = new SearchResult(this, 1, string, i2, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda18(this));
            int i3 = i2;
            searchResultArr[5] = new SearchResult(this, 2, LocaleController.getString("NotificationsPrivateChats", R.string.NotificationsPrivateChats), LocaleController.getString("NotificationsAndSounds", i), i3, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda7(this));
            searchResultArr[6] = new SearchResult(this, 3, LocaleController.getString("NotificationsGroups", R.string.NotificationsGroups), LocaleController.getString("NotificationsAndSounds", i), i3, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda22(this));
            searchResultArr[7] = new SearchResult(this, 4, LocaleController.getString("NotificationsChannels", R.string.NotificationsChannels), LocaleController.getString("NotificationsAndSounds", i), i3, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda42(this));
            int i4 = i2;
            searchResultArr[8] = new SearchResult(this, 5, LocaleController.getString("VoipNotificationSettings", R.string.VoipNotificationSettings), "callsSectionRow", LocaleController.getString("NotificationsAndSounds", i), i4, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda0(this));
            searchResultArr[9] = new SearchResult(this, 6, LocaleController.getString("BadgeNumber", R.string.BadgeNumber), "badgeNumberSection", LocaleController.getString("NotificationsAndSounds", i), i4, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda3(this));
            searchResultArr[10] = new SearchResult(this, 7, LocaleController.getString("InAppNotifications", R.string.InAppNotifications), "inappSectionRow", LocaleController.getString("NotificationsAndSounds", i), i4, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda64(this));
            searchResultArr[11] = new SearchResult(this, 8, LocaleController.getString("ContactJoined", R.string.ContactJoined), "contactJoinedRow", LocaleController.getString("NotificationsAndSounds", i), i4, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda52(this));
            searchResultArr[12] = new SearchResult(this, 9, LocaleController.getString("PinnedMessages", R.string.PinnedMessages), "pinnedMessageRow", LocaleController.getString("NotificationsAndSounds", i), i4, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda62(this));
            searchResultArr[13] = new SearchResult(this, 10, LocaleController.getString("ResetAllNotifications", R.string.ResetAllNotifications), "resetNotificationsRow", LocaleController.getString("NotificationsAndSounds", i), i4, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda13(this));
            int i5 = R.string.PrivacySettings;
            String string2 = LocaleController.getString("PrivacySettings", i5);
            int i6 = R.drawable.msg_secret;
            searchResultArr[14] = new SearchResult(this, 100, string2, i6, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda24(this));
            int i7 = i6;
            searchResultArr[15] = new SearchResult(this, 101, LocaleController.getString("BlockedUsers", R.string.BlockedUsers), LocaleController.getString("PrivacySettings", i5), i7, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda75(this));
            searchResultArr[16] = new SearchResult(this, 105, LocaleController.getString("PrivacyPhone", R.string.PrivacyPhone), LocaleController.getString("PrivacySettings", i5), i7, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda72(this));
            searchResultArr[17] = new SearchResult(this, 102, LocaleController.getString("PrivacyLastSeen", R.string.PrivacyLastSeen), LocaleController.getString("PrivacySettings", i5), i7, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda26(this));
            searchResultArr[18] = new SearchResult(this, 103, LocaleController.getString("PrivacyProfilePhoto", R.string.PrivacyProfilePhoto), LocaleController.getString("PrivacySettings", i5), i7, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda55(this));
            searchResultArr[19] = new SearchResult(this, 104, LocaleController.getString("PrivacyForwards", R.string.PrivacyForwards), LocaleController.getString("PrivacySettings", i5), i7, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda78(this));
            searchResultArr[20] = new SearchResult(this, 122, LocaleController.getString("PrivacyP2P", R.string.PrivacyP2P), LocaleController.getString("PrivacySettings", i5), i7, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda61(this));
            int i8 = R.string.Calls;
            String str = "Calls";
            int i9 = i6;
            int i10 = i8;
            searchResultArr[21] = new SearchResult(this, 106, LocaleController.getString("Calls", i8), LocaleController.getString("PrivacySettings", i5), i9, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda5(this));
            searchResultArr[22] = new SearchResult(this, 107, LocaleController.getString("GroupsAndChannels", R.string.GroupsAndChannels), LocaleController.getString("PrivacySettings", i5), i9, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda29(this));
            searchResultArr[23] = new SearchResult(this, 123, LocaleController.getString("PrivacyVoiceMessages", R.string.PrivacyVoiceMessages), LocaleController.getString("PrivacySettings", i5), i9, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda23(this));
            searchResultArr[24] = new SearchResult(this, 108, LocaleController.getString("Passcode", R.string.Passcode), LocaleController.getString("PrivacySettings", i5), i9, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda70(this));
            searchResultArr[25] = new SearchResult(this, 109, LocaleController.getString("TwoStepVerification", R.string.TwoStepVerification), LocaleController.getString("PrivacySettings", i5), i9, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda35(this));
            searchResultArr[26] = new SearchResult(this, 110, LocaleController.getString("SessionsTitle", R.string.SessionsTitle), i6, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda17(this));
            if (profileActivity.getMessagesController().autoarchiveAvailable) {
                searchResult = new SearchResult(this, 121, LocaleController.getString("ArchiveAndMute", R.string.ArchiveAndMute), "newChatsRow", LocaleController.getString("PrivacySettings", i5), i6, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda46(this));
            } else {
                searchResult = null;
            }
            searchResultArr[27] = searchResult;
            int i11 = i6;
            searchResultArr[28] = new SearchResult(this, 112, LocaleController.getString("DeleteAccountIfAwayFor2", R.string.DeleteAccountIfAwayFor2), "deleteAccountRow", LocaleController.getString("PrivacySettings", i5), i11, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda79(this));
            searchResultArr[29] = new SearchResult(this, 113, LocaleController.getString("PrivacyPaymentsClear", R.string.PrivacyPaymentsClear), "paymentsClearRow", LocaleController.getString("PrivacySettings", i5), i11, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda77(this));
            searchResultArr[30] = new SearchResult(this, 114, LocaleController.getString("WebSessionsTitle", R.string.WebSessionsTitle), LocaleController.getString("PrivacySettings", i5), i6, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda58(this));
            int i12 = i6;
            searchResultArr[31] = new SearchResult(this, 115, LocaleController.getString("SyncContactsDelete", R.string.SyncContactsDelete), "contactsDeleteRow", LocaleController.getString("PrivacySettings", i5), i12, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda19(this));
            searchResultArr[32] = new SearchResult(this, 116, LocaleController.getString("SyncContacts", R.string.SyncContacts), "contactsSyncRow", LocaleController.getString("PrivacySettings", i5), i12, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda74(this));
            searchResultArr[33] = new SearchResult(this, 117, LocaleController.getString("SuggestContacts", R.string.SuggestContacts), "contactsSuggestRow", LocaleController.getString("PrivacySettings", i5), i12, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda56(this));
            searchResultArr[34] = new SearchResult(this, 118, LocaleController.getString("MapPreviewProvider", R.string.MapPreviewProvider), "secretMapRow", LocaleController.getString("PrivacySettings", i5), i12, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda41(this));
            searchResultArr[35] = new SearchResult(this, 119, LocaleController.getString("SecretWebPage", R.string.SecretWebPage), "secretWebpageRow", LocaleController.getString("PrivacySettings", i5), i12, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda8(this));
            searchResultArr[36] = new SearchResult(this, 120, LocaleController.getString("Devices", R.string.Devices), i6, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda6(this));
            int i13 = R.string.DataSettings;
            String string3 = LocaleController.getString("DataSettings", i13);
            int i14 = R.drawable.msg_data;
            searchResultArr[37] = new SearchResult(this, 200, string3, i14, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda47(this));
            searchResultArr[38] = new SearchResult(this, 201, LocaleController.getString("DataUsage", R.string.DataUsage), "usageSectionRow", LocaleController.getString("DataSettings", i13), i14, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda27(this));
            int i15 = R.string.StorageUsage;
            int i16 = i15;
            searchResultArr[39] = new SearchResult(this, 202, LocaleController.getString("StorageUsage", i15), LocaleController.getString("DataSettings", i13), i14, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda65(this));
            String str2 = "StorageUsage";
            int i17 = i16;
            int i18 = i17;
            searchResultArr[40] = new SearchResult(203, LocaleController.getString("KeepMedia", R.string.KeepMedia), "keepMediaRow", LocaleController.getString("DataSettings", i13), LocaleController.getString(str2, i17), i14, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda15(this));
            String str3 = str2;
            int i19 = i18;
            String str4 = str3;
            int i20 = i14;
            searchResultArr[41] = new SearchResult(204, LocaleController.getString("ClearMediaCache", R.string.ClearMediaCache), "cacheRow", LocaleController.getString("DataSettings", i13), LocaleController.getString(str3, i19), i20, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda1(this));
            searchResultArr[42] = new SearchResult(205, LocaleController.getString("LocalDatabase", R.string.LocalDatabase), "databaseRow", LocaleController.getString("DataSettings", i13), LocaleController.getString(str4, i19), i20, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda76(this));
            searchResultArr[43] = new SearchResult(this, 206, LocaleController.getString("NetworkUsage", R.string.NetworkUsage), LocaleController.getString("DataSettings", i13), i14, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda49(this));
            searchResultArr[44] = new SearchResult(this, 207, LocaleController.getString("AutomaticMediaDownload", R.string.AutomaticMediaDownload), "mediaDownloadSectionRow", LocaleController.getString("DataSettings", i13), i14, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda21(this));
            int i21 = i14;
            searchResultArr[45] = new SearchResult(this, 208, LocaleController.getString("WhenUsingMobileData", R.string.WhenUsingMobileData), LocaleController.getString("DataSettings", i13), i21, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda16(this));
            searchResultArr[46] = new SearchResult(this, 209, LocaleController.getString("WhenConnectedOnWiFi", R.string.WhenConnectedOnWiFi), LocaleController.getString("DataSettings", i13), i21, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda28(this));
            searchResultArr[47] = new SearchResult(this, 210, LocaleController.getString("WhenRoaming", R.string.WhenRoaming), LocaleController.getString("DataSettings", i13), i21, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda66(this));
            int i22 = i14;
            searchResultArr[48] = new SearchResult(this, 211, LocaleController.getString("ResetAutomaticMediaDownload", R.string.ResetAutomaticMediaDownload), "resetDownloadRow", LocaleController.getString("DataSettings", i13), i22, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda2(this));
            searchResultArr[49] = new SearchResult(this, 212, LocaleController.getString("AutoplayMedia", R.string.AutoplayMedia), "autoplayHeaderRow", LocaleController.getString("DataSettings", i13), i22, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda40(this));
            searchResultArr[50] = new SearchResult(this, 213, LocaleController.getString("AutoplayGIF", R.string.AutoplayGIF), "autoplayGifsRow", LocaleController.getString("DataSettings", i13), i22, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda60(this));
            searchResultArr[51] = new SearchResult(this, 214, LocaleController.getString("AutoplayVideo", R.string.AutoplayVideo), "autoplayVideoRow", LocaleController.getString("DataSettings", i13), i22, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda71(this));
            searchResultArr[52] = new SearchResult(this, 215, LocaleController.getString("Streaming", R.string.Streaming), "streamSectionRow", LocaleController.getString("DataSettings", i13), i22, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda4(this));
            searchResultArr[53] = new SearchResult(this, 216, LocaleController.getString("EnableStreaming", R.string.EnableStreaming), "enableStreamRow", LocaleController.getString("DataSettings", i13), i22, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda53(this));
            searchResultArr[54] = new SearchResult(this, 217, LocaleController.getString(str, i10), "callsSectionRow", LocaleController.getString("DataSettings", i13), i22, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda44(this));
            searchResultArr[55] = new SearchResult(this, 218, LocaleController.getString("VoipUseLessData", R.string.VoipUseLessData), "useLessDataForCallsRow", LocaleController.getString("DataSettings", i13), i22, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda45(this));
            searchResultArr[56] = new SearchResult(this, 219, LocaleController.getString("VoipQuickReplies", R.string.VoipQuickReplies), "quickRepliesRow", LocaleController.getString("DataSettings", i13), i22, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda33(this));
            int i23 = R.string.ProxySettings;
            searchResultArr[57] = new SearchResult(this, 220, LocaleController.getString("ProxySettings", i23), LocaleController.getString("DataSettings", i13), i14, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda36(this));
            searchResultArr[58] = new SearchResult(221, LocaleController.getString("UseProxyForCalls", R.string.UseProxyForCalls), "callsRow", LocaleController.getString("DataSettings", i13), LocaleController.getString("ProxySettings", i23), i14, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda81(this));
            searchResultArr[59] = new SearchResult(this, 111, LocaleController.getString("PrivacyDeleteCloudDrafts", R.string.PrivacyDeleteCloudDrafts), "clearDraftsRow", LocaleController.getString("DataSettings", i13), i14, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda20(this));
            int i24 = R.string.ChatSettings;
            String string4 = LocaleController.getString("ChatSettings", i24);
            int i25 = R.drawable.msg_msgbubble3;
            searchResultArr[60] = new SearchResult(this, 300, string4, i25, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda14(this));
            searchResultArr[61] = new SearchResult(this, 301, LocaleController.getString("TextSizeHeader", R.string.TextSizeHeader), "textSizeHeaderRow", LocaleController.getString("ChatSettings", i24), i25, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda82(this));
            int i26 = R.string.ChatBackground;
            searchResultArr[62] = new SearchResult(this, 302, LocaleController.getString("ChatBackground", i26), LocaleController.getString("ChatSettings", i24), i25, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda12(this));
            int i27 = i25;
            searchResultArr[63] = new SearchResult(303, LocaleController.getString("SetColor", R.string.SetColor), (String) null, LocaleController.getString("ChatSettings", i24), LocaleController.getString("ChatBackground", i26), i27, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda54(this));
            searchResultArr[64] = new SearchResult(304, LocaleController.getString("ResetChatBackgrounds", R.string.ResetChatBackgrounds), "resetRow", LocaleController.getString("ChatSettings", i24), LocaleController.getString("ChatBackground", i26), i27, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda51(this));
            searchResultArr[65] = new SearchResult(this, 305, LocaleController.getString("AutoNightTheme", R.string.AutoNightTheme), LocaleController.getString("ChatSettings", i24), i25, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda57(this));
            int i28 = i25;
            searchResultArr[66] = new SearchResult(this, 306, LocaleController.getString("ColorTheme", R.string.ColorTheme), "themeHeaderRow", LocaleController.getString("ChatSettings", i24), i28, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda34(this));
            searchResultArr[67] = new SearchResult(this, 307, LocaleController.getString("ChromeCustomTabs", R.string.ChromeCustomTabs), "customTabsRow", LocaleController.getString("ChatSettings", i24), i28, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda50(this));
            searchResultArr[68] = new SearchResult(this, 308, LocaleController.getString("DirectShare", R.string.DirectShare), "directShareRow", LocaleController.getString("ChatSettings", i24), i28, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda9(this));
            searchResultArr[69] = new SearchResult(this, 309, LocaleController.getString("EnableAnimations", R.string.EnableAnimations), "enableAnimationsRow", LocaleController.getString("ChatSettings", i24), i28, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda39(this));
            searchResultArr[70] = new SearchResult(this, 310, LocaleController.getString("RaiseToSpeak", R.string.RaiseToSpeak), "raiseToSpeakRow", LocaleController.getString("ChatSettings", i24), i28, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda83(this));
            searchResultArr[71] = new SearchResult(this, 311, LocaleController.getString("SendByEnter", R.string.SendByEnter), "sendByEnterRow", LocaleController.getString("ChatSettings", i24), i28, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda80(this));
            searchResultArr[72] = new SearchResult(this, 312, LocaleController.getString("SaveToGallerySettings", R.string.SaveToGallerySettings), "saveToGalleryRow", LocaleController.getString("ChatSettings", i24), i28, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda25(this));
            searchResultArr[73] = new SearchResult(this, 318, LocaleController.getString("DistanceUnits", R.string.DistanceUnits), "distanceRow", LocaleController.getString("ChatSettings", i24), i28, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda59(this));
            int i29 = R.string.StickersAndMasks;
            searchResultArr[74] = new SearchResult(this, 313, LocaleController.getString("StickersAndMasks", i29), LocaleController.getString("ChatSettings", i24), i25, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda37(this));
            int i30 = i25;
            searchResultArr[75] = new SearchResult(314, LocaleController.getString("SuggestStickers", R.string.SuggestStickers), "suggestRow", LocaleController.getString("ChatSettings", i24), LocaleController.getString("StickersAndMasks", i29), i30, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda30(this));
            searchResultArr[76] = new SearchResult(315, LocaleController.getString("FeaturedStickers", R.string.FeaturedStickers), (String) null, LocaleController.getString("ChatSettings", i24), LocaleController.getString("StickersAndMasks", i29), i30, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda69(this));
            searchResultArr[77] = new SearchResult(316, LocaleController.getString("Masks", R.string.Masks), (String) null, LocaleController.getString("ChatSettings", i24), LocaleController.getString("StickersAndMasks", i29), i30, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda67(this));
            searchResultArr[78] = new SearchResult(317, LocaleController.getString("ArchivedStickers", R.string.ArchivedStickers), (String) null, LocaleController.getString("ChatSettings", i24), LocaleController.getString("StickersAndMasks", i29), i30, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda73(this));
            searchResultArr[79] = new SearchResult(317, LocaleController.getString("ArchivedMasks", R.string.ArchivedMasks), (String) null, LocaleController.getString("ChatSettings", i24), LocaleController.getString("StickersAndMasks", i29), i30, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda68(this));
            searchResultArr[80] = new SearchResult(this, 400, LocaleController.getString("Language", R.string.Language), R.drawable.msg_language, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda48(this));
            String string5 = LocaleController.getString("AskAQuestion", R.string.AskAQuestion);
            int i31 = R.string.SettingsHelp;
            String string6 = LocaleController.getString("SettingsHelp", i31);
            int i32 = R.drawable.msg_help;
            searchResultArr[81] = new SearchResult(this, 402, string5, string6, i32, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda43(this));
            searchResultArr[82] = new SearchResult(this, 403, LocaleController.getString("TelegramFAQ", R.string.TelegramFAQ), LocaleController.getString("SettingsHelp", i31), i32, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda31(this));
            searchResultArr[83] = new SearchResult(this, 404, LocaleController.getString("PrivacyPolicy", R.string.PrivacyPolicy), LocaleController.getString("SettingsHelp", i31), i32, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda38(this));
            this.searchArray = searchResultArr;
            this.faqSearchArray = new ArrayList<>();
            this.resultNames = new ArrayList<>();
            this.searchResults = new ArrayList<>();
            this.faqSearchResults = new ArrayList<>();
            this.recentSearches = new ArrayList<>();
            this.mContext = context;
            HashMap hashMap = new HashMap();
            int i33 = 0;
            while (true) {
                SearchResult[] searchResultArr2 = this.searchArray;
                if (i33 >= searchResultArr2.length) {
                    break;
                }
                if (searchResultArr2[i33] != null) {
                    hashMap.put(Integer.valueOf(searchResultArr2[i33].guid), this.searchArray[i33]);
                }
                i33++;
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
                            if (readInt323 > 0) {
                                strArr = new String[readInt323];
                                int i34 = 0;
                                while (i34 < readInt323) {
                                    strArr[i34] = serializedData.readString(z);
                                    i34++;
                                    z = false;
                                }
                            } else {
                                strArr = null;
                            }
                            MessagesController.FaqSearchResult faqSearchResult = new MessagesController.FaqSearchResult(readString, strArr, serializedData.readString(z));
                            faqSearchResult.num = readInt32;
                            this.recentSearches.add(faqSearchResult);
                        } else if (readInt322 == 1) {
                            try {
                                SearchResult searchResult2 = (SearchResult) hashMap.get(Integer.valueOf(serializedData.readInt32(false)));
                                if (searchResult2 != null) {
                                    int unused = searchResult2.num = readInt32;
                                    this.recentSearches.add(searchResult2);
                                }
                            } catch (Exception unused2) {
                            }
                        }
                    } catch (Exception unused3) {
                    }
                }
            }
            Collections.sort(this.recentSearches, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda87(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ int lambda$new$84(Object obj, Object obj2) {
            int num = getNum(obj);
            int num2 = getNum(obj2);
            if (num < num2) {
                return -1;
            }
            return num > num2 ? 1 : 0;
        }

        /* access modifiers changed from: private */
        public void loadFaqWebPage() {
            TLRPC$WebPage tLRPC$WebPage = this.this$0.getMessagesController().faqWebPage;
            this.faqWebPage = tLRPC$WebPage;
            if (tLRPC$WebPage != null) {
                this.faqSearchArray.addAll(this.this$0.getMessagesController().faqSearchArray);
            }
            if (this.faqWebPage == null && !this.loadingFaqPage) {
                this.loadingFaqPage = true;
                TLRPC$TL_messages_getWebPage tLRPC$TL_messages_getWebPage = new TLRPC$TL_messages_getWebPage();
                tLRPC$TL_messages_getWebPage.url = LocaleController.getString("TelegramFaqUrl", R.string.TelegramFaqUrl);
                tLRPC$TL_messages_getWebPage.hash = 0;
                this.this$0.getConnectionsManager().sendRequest(tLRPC$TL_messages_getWebPage, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda88(this));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$loadFaqWebPage$86(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            if (tLObject instanceof TLRPC$WebPage) {
                ArrayList arrayList = new ArrayList();
                TLRPC$WebPage tLRPC$WebPage = (TLRPC$WebPage) tLObject;
                TLRPC$Page tLRPC$Page = tLRPC$WebPage.cached_page;
                if (tLRPC$Page != null) {
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
                                        arrayList.add(new MessagesController.FaqSearchResult(charSequence, str != null ? new String[]{LocaleController.getString("SettingsSearchFaq", R.string.SettingsSearchFaq), str} : new String[]{LocaleController.getString("SettingsSearchFaq", R.string.SettingsSearchFaq)}, url));
                                    }
                                }
                            }
                        } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockAnchor) {
                            break;
                        }
                    }
                    this.faqWebPage = tLRPC$WebPage;
                }
                AndroidUtilities.runOnUIThread(new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda86(this, arrayList));
            }
            this.loadingFaqPage = false;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$loadFaqWebPage$85(ArrayList arrayList) {
            this.faqSearchArray.addAll(arrayList);
            this.this$0.getMessagesController().faqSearchArray = arrayList;
            this.this$0.getMessagesController().faqWebPage = this.faqWebPage;
            if (!this.searchWas) {
                notifyDataSetChanged();
            }
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
            int size2 = this.recentSearches.isEmpty() ? 0 : this.recentSearches.size() + 1;
            if (!this.faqSearchArray.isEmpty()) {
                i = this.faqSearchArray.size() + 1;
            }
            return size2 + i;
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
                    if (!this.recentSearches.isEmpty()) {
                        i--;
                    }
                    if (i < this.recentSearches.size()) {
                        Object obj = this.recentSearches.get(i);
                        if (obj instanceof SearchResult) {
                            SearchResult searchResult = (SearchResult) obj;
                            String access$26100 = searchResult.searchTitle;
                            String[] access$26000 = searchResult.path;
                            if (i >= this.recentSearches.size() - 1) {
                                z = false;
                            }
                            settingsSearchCell.setTextAndValue(access$26100, access$26000, false, z);
                        } else if (obj instanceof MessagesController.FaqSearchResult) {
                            MessagesController.FaqSearchResult faqSearchResult = (MessagesController.FaqSearchResult) obj;
                            String str = faqSearchResult.title;
                            String[] strArr = faqSearchResult.path;
                            if (i < this.recentSearches.size() - 1) {
                                z2 = true;
                            }
                            settingsSearchCell.setTextAndValue(str, strArr, true, z2);
                        }
                    } else {
                        int size = i - (this.recentSearches.size() + 1);
                        MessagesController.FaqSearchResult faqSearchResult2 = this.faqSearchArray.get(size);
                        String str2 = faqSearchResult2.title;
                        String[] strArr2 = faqSearchResult2.path;
                        if (size < this.recentSearches.size() - 1) {
                            z2 = true;
                        }
                        settingsSearchCell.setTextAndValue(str2, strArr2, true, z2);
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
                    String[] access$260002 = searchResult2.path;
                    if (i >= this.searchResults.size() - 1) {
                        z = false;
                    }
                    settingsSearchCell.setTextAndValueAndIcon(charSequence, access$260002, i2, z);
                } else {
                    int size2 = i - (this.searchResults.size() + 1);
                    CharSequence charSequence2 = this.resultNames.get(this.searchResults.size() + size2);
                    String[] strArr3 = this.faqSearchResults.get(size2).path;
                    if (size2 < this.searchResults.size() - 1) {
                        z2 = true;
                    }
                    settingsSearchCell.setTextAndValue(charSequence2, strArr3, true, z2);
                }
            } else if (itemViewType == 1) {
                ((GraySectionCell) viewHolder.itemView).setText(LocaleController.getString("SettingsFaqSearchTitle", R.string.SettingsFaqSearchTitle));
            } else if (itemViewType == 2) {
                ((HeaderCell) viewHolder.itemView).setText(LocaleController.getString("SettingsRecent", R.string.SettingsRecent));
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
            } else if (i != 0) {
                return (this.recentSearches.isEmpty() || i != this.recentSearches.size() + 1) ? 0 : 1;
            } else {
                if (!this.recentSearches.isEmpty()) {
                    return 2;
                }
                return 1;
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
                } else if (obj2 instanceof MessagesController.FaqSearchResult) {
                    ((MessagesController.FaqSearchResult) obj2).num = i;
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
            if (obj instanceof MessagesController.FaqSearchResult) {
                return ((MessagesController.FaqSearchResult) obj).num;
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
                this.this$0.emptyView.stickerView.getImageReceiver().startAnimation();
                this.this$0.emptyView.title.setText(LocaleController.getString("SettingsNoRecent", R.string.SettingsNoRecent));
                notifyDataSetChanged();
                return;
            }
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            ProfileActivity$SearchAdapter$$ExternalSyntheticLambda84 profileActivity$SearchAdapter$$ExternalSyntheticLambda84 = new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda84(this, str);
            this.searchRunnable = profileActivity$SearchAdapter$$ExternalSyntheticLambda84;
            dispatchQueue.postRunnable(profileActivity$SearchAdapter$$ExternalSyntheticLambda84, 300);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$search$88(String str) {
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
                if (searchResult != null) {
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
                            spannableStringBuilder2.setSpan(new ForegroundColorSpan(this.this$0.getThemedColor("windowBackgroundWhiteBlueText4")), indexOf, str6.length() + indexOf, 33);
                        } else {
                            str3 = str5;
                            spannableStringBuilder2 = spannableStringBuilder3;
                        }
                        if (spannableStringBuilder2 != null && i4 == split.length - 1) {
                            if (searchResult.guid == 502) {
                                int i5 = -1;
                                int i6 = 0;
                                while (true) {
                                    if (i6 >= 4) {
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
                }
                i3++;
                String str8 = str;
                spannableStringBuilder = null;
            }
            if (this.faqWebPage != null) {
                int size = this.faqSearchArray.size();
                int i7 = 0;
                while (i7 < size) {
                    MessagesController.FaqSearchResult faqSearchResult = this.faqSearchArray.get(i7);
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
                            spannableStringBuilder4.setSpan(new ForegroundColorSpan(this.this$0.getThemedColor("windowBackgroundWhiteBlueText4")), indexOf2, str10.length() + indexOf2, 33);
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
            AndroidUtilities.runOnUIThread(new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda85(this, str, arrayList, arrayList2, arrayList3));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$search$87(String str, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3) {
            if (str.equals(this.lastSearchString)) {
                if (!this.searchWas) {
                    this.this$0.emptyView.stickerView.getImageReceiver().startAnimation();
                    this.this$0.emptyView.title.setText(LocaleController.getString("SettingsNoResults", R.string.SettingsNoResults));
                }
                this.searchWas = true;
                this.searchResults = arrayList;
                this.faqSearchResults = arrayList2;
                this.resultNames = arrayList3;
                notifyDataSetChanged();
                this.this$0.emptyView.stickerView.getImageReceiver().startAnimation();
            }
        }

        public boolean isSearchWas() {
            return this.searchWas;
        }
    }

    /* access modifiers changed from: private */
    public void openUrl(String str) {
        if (str.startsWith("@")) {
            getMessagesController().openByUserName(str.substring(1), this, 0);
        } else if (str.startsWith("#")) {
            DialogsActivity dialogsActivity = new DialogsActivity((Bundle) null);
            dialogsActivity.setSearchString(str);
            presentFragment(dialogsActivity);
        } else if (str.startsWith("/") && this.parentLayout.fragmentsStack.size() > 1) {
            ArrayList<BaseFragment> arrayList = this.parentLayout.fragmentsStack;
            BaseFragment baseFragment = arrayList.get(arrayList.size() - 2);
            if (baseFragment instanceof ChatActivity) {
                finishFragment();
                ((ChatActivity) baseFragment).chatActivityEnterView.setCommand((MessageObject) null, str, false, false);
            }
        }
    }

    private void dimBehindView(View view, float f) {
        this.scrimView = view;
        dimBehindView(f);
    }

    private void dimBehindView(boolean z) {
        dimBehindView(z ? 0.2f : 0.0f);
    }

    private void dimBehindView(float f) {
        ValueAnimator valueAnimator;
        boolean z = f > 0.0f;
        this.fragmentView.invalidate();
        AnimatorSet animatorSet = this.scrimAnimatorSet;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.scrimAnimatorSet = new AnimatorSet();
        ArrayList arrayList = new ArrayList();
        if (z) {
            valueAnimator = ValueAnimator.ofFloat(new float[]{0.0f, f});
            arrayList.add(valueAnimator);
        } else {
            valueAnimator = ValueAnimator.ofFloat(new float[]{((float) this.scrimPaint.getAlpha()) / 255.0f, 0.0f});
            arrayList.add(valueAnimator);
        }
        valueAnimator.addUpdateListener(new ProfileActivity$$ExternalSyntheticLambda2(this));
        this.scrimAnimatorSet.playTogether(arrayList);
        this.scrimAnimatorSet.setDuration(z ? 150 : 220);
        if (!z) {
            this.scrimAnimatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    View unused = ProfileActivity.this.scrimView = null;
                    ProfileActivity.this.fragmentView.invalidate();
                }
            });
        }
        this.scrimAnimatorSet.start();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$dimBehindView$39(ValueAnimator valueAnimator) {
        this.scrimPaint.setAlpha((int) (((Float) valueAnimator.getAnimatedValue()).floatValue() * 255.0f));
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        if (this.resourcesProvider != null) {
            return null;
        }
        ProfileActivity$$ExternalSyntheticLambda36 profileActivity$$ExternalSyntheticLambda36 = new ProfileActivity$$ExternalSyntheticLambda36(this);
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        SharedMediaLayout sharedMediaLayout2 = this.sharedMediaLayout;
        if (sharedMediaLayout2 != null) {
            arrayList.addAll(sharedMediaLayout2.getThemeDescriptions());
        }
        arrayList.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.searchListView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuBackground"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuItem"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuItemIcon"));
        ProfileActivity$$ExternalSyntheticLambda36 profileActivity$$ExternalSyntheticLambda362 = profileActivity$$ExternalSyntheticLambda36;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileActivity$$ExternalSyntheticLambda362, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileActivity$$ExternalSyntheticLambda362, "avatar_actionBarSelectorBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileActivity$$ExternalSyntheticLambda362, "chat_lockIcon"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileActivity$$ExternalSyntheticLambda362, "avatar_subtitleInProfileBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileActivity$$ExternalSyntheticLambda362, "avatar_backgroundActionBarBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileActivity$$ExternalSyntheticLambda362, "profile_title"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileActivity$$ExternalSyntheticLambda362, "profile_status"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileActivity$$ExternalSyntheticLambda362, "avatar_subtitleInProfileBlue"));
        if (this.mediaCounterTextView != null) {
            ProfileActivity$$ExternalSyntheticLambda36 profileActivity$$ExternalSyntheticLambda363 = profileActivity$$ExternalSyntheticLambda36;
            arrayList.add(new ThemeDescription(this.mediaCounterTextView.getTextView(), ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, profileActivity$$ExternalSyntheticLambda363, "player_actionBarSubtitle"));
            arrayList.add(new ThemeDescription(this.mediaCounterTextView.getNextTextView(), ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, profileActivity$$ExternalSyntheticLambda363, "player_actionBarSubtitle"));
        }
        arrayList.add(new ThemeDescription(this.topView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_backgroundActionBarBlue"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription(this.avatarImage, 0, (Class[]) null, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        arrayList.add(new ThemeDescription(this.avatarImage, 0, (Class[]) null, (Paint) null, new Drawable[]{this.avatarDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_backgroundInProfileBlue"));
        arrayList.add(new ThemeDescription(this.writeButton, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_actionIcon"));
        arrayList.add(new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_actionBackground"));
        arrayList.add(new ThemeDescription(this.writeButton, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_actionPressedBackground"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGreenText2"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText5"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText2"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueButton"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueIcon"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{SettingsSuggestionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{SettingsSuggestionCell.class}, new String[]{"detailTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{SettingsSuggestionCell.class}, new String[]{"detailTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteLinkText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{SettingsSuggestionCell.class}, new String[]{"yesButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_buttonText"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{SettingsSuggestionCell.class}, new String[]{"yesButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addButton"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{SettingsSuggestionCell.class}, new String[]{"yesButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addButtonPressed"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{SettingsSuggestionCell.class}, new String[]{"noButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_buttonText"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{SettingsSuggestionCell.class}, new String[]{"noButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addButton"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{SettingsSuggestionCell.class}, new String[]{"noButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addButtonPressed"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{UserCell.class}, new String[]{"adminTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_creatorIcon"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        ProfileActivity$$ExternalSyntheticLambda36 profileActivity$$ExternalSyntheticLambda364 = profileActivity$$ExternalSyntheticLambda36;
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) profileActivity$$ExternalSyntheticLambda364, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) profileActivity$$ExternalSyntheticLambda364, "windowBackgroundWhiteBlueText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        ProfileActivity$$ExternalSyntheticLambda36 profileActivity$$ExternalSyntheticLambda365 = profileActivity$$ExternalSyntheticLambda36;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileActivity$$ExternalSyntheticLambda365, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileActivity$$ExternalSyntheticLambda365, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileActivity$$ExternalSyntheticLambda365, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileActivity$$ExternalSyntheticLambda365, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileActivity$$ExternalSyntheticLambda365, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileActivity$$ExternalSyntheticLambda365, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileActivity$$ExternalSyntheticLambda365, "avatar_backgroundPink"));
        arrayList.add(new ThemeDescription(this.undoView, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_background"));
        arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
        arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
        arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        arrayList.add(new ThemeDescription((View) this.undoView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{UndoView.class}, new String[]{"leftImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        RecyclerListView recyclerListView = this.listView;
        RecyclerListView recyclerListView2 = recyclerListView;
        arrayList.add(new ThemeDescription(recyclerListView2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AboutLinkCell.class}, Theme.profile_aboutTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        RecyclerListView recyclerListView3 = this.listView;
        RecyclerListView recyclerListView4 = recyclerListView3;
        arrayList.add(new ThemeDescription(recyclerListView4, ThemeDescription.FLAG_LINKCOLOR, new Class[]{AboutLinkCell.class}, Theme.profile_aboutTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteLinkText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{AboutLinkCell.class}, Theme.linkSelectionPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteLinkSelection"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription((View) this.searchListView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription((View) this.searchListView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_graySectionText"));
        arrayList.add(new ThemeDescription(this.searchListView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "graySection"));
        arrayList.add(new ThemeDescription((View) this.searchListView, 0, new Class[]{SettingsSearchCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.searchListView, 0, new Class[]{SettingsSearchCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription((View) this.searchListView, 0, new Class[]{SettingsSearchCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        if (this.mediaHeaderVisible) {
            arrayList.add(new ThemeDescription(this.nameTextView[1], 0, (Class[]) null, (Paint) null, new Drawable[]{this.verifiedCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "player_actionBarTitle"));
            arrayList.add(new ThemeDescription(this.nameTextView[1], 0, (Class[]) null, (Paint) null, new Drawable[]{this.verifiedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        } else {
            arrayList.add(new ThemeDescription(this.nameTextView[1], 0, (Class[]) null, (Paint) null, new Drawable[]{this.verifiedCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_verifiedCheck"));
            arrayList.add(new ThemeDescription(this.nameTextView[1], 0, (Class[]) null, (Paint) null, new Drawable[]{this.verifiedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_verifiedBackground"));
        }
        return arrayList;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getThemeDescriptions$40() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof UserCell) {
                    ((UserCell) childAt).update(0);
                }
            }
        }
        if (!this.isPulledDown) {
            SimpleTextView[] simpleTextViewArr = this.onlineTextView;
            if (simpleTextViewArr[1] != null) {
                Object tag = simpleTextViewArr[1].getTag();
                if (tag instanceof String) {
                    this.onlineTextView[1].setTextColor(getThemedColor((String) tag));
                } else {
                    this.onlineTextView[1].setTextColor(getThemedColor("avatar_subtitleInProfileBlue"));
                }
            }
            Drawable drawable = this.lockIconDrawable;
            if (drawable != null) {
                drawable.setColorFilter(getThemedColor("chat_lockIcon"), PorterDuff.Mode.MULTIPLY);
            }
            ScamDrawable scamDrawable2 = this.scamDrawable;
            if (scamDrawable2 != null) {
                scamDrawable2.setColor(getThemedColor("avatar_subtitleInProfileBlue"));
            }
            SimpleTextView[] simpleTextViewArr2 = this.nameTextView;
            if (simpleTextViewArr2[1] != null) {
                simpleTextViewArr2[1].setTextColor(getThemedColor("profile_title"));
            }
            ActionBar actionBar = this.actionBar;
            if (actionBar != null) {
                actionBar.setItemsColor(getThemedColor("actionBarDefaultIcon"), false);
                this.actionBar.setItemsBackgroundColor(getThemedColor("avatar_actionBarSelectorBlue"), false);
            }
        }
    }

    public void updateListAnimated(boolean z) {
        if (this.listAdapter == null) {
            if (z) {
                updateOnlineCount(false);
            }
            updateRowsIds();
            return;
        }
        DiffCallback diffCallback = new DiffCallback();
        diffCallback.oldRowCount = this.rowCount;
        diffCallback.fillPositions(diffCallback.oldPositionToItem);
        diffCallback.oldChatParticipant.clear();
        diffCallback.oldChatParticipantSorted.clear();
        diffCallback.oldChatParticipant.addAll(this.visibleChatParticipants);
        diffCallback.oldChatParticipantSorted.addAll(this.visibleSortedUsers);
        diffCallback.oldMembersStartRow = this.membersStartRow;
        diffCallback.oldMembersEndRow = this.membersEndRow;
        if (z) {
            updateOnlineCount(false);
        }
        saveScrollPosition();
        updateRowsIds();
        diffCallback.fillPositions(diffCallback.newPositionToItem);
        try {
            DiffUtil.calculateDiff(diffCallback).dispatchUpdatesTo((RecyclerView.Adapter) this.listAdapter);
        } catch (Exception unused) {
            this.listAdapter.notifyDataSetChanged();
        }
        int i = this.savedScrollPosition;
        if (i >= 0) {
            this.layoutManager.scrollToPositionWithOffset(i, this.savedScrollOffset - this.listView.getPaddingTop());
        }
        AndroidUtilities.updateVisibleRows(this.listView);
    }

    /* access modifiers changed from: private */
    public void saveScrollPosition() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null && this.layoutManager != null && recyclerListView.getChildCount() > 0) {
            View view = null;
            int i = Integer.MAX_VALUE;
            int i2 = -1;
            for (int i3 = 0; i3 < this.listView.getChildCount(); i3++) {
                RecyclerListView recyclerListView2 = this.listView;
                int childAdapterPosition = recyclerListView2.getChildAdapterPosition(recyclerListView2.getChildAt(i3));
                View childAt = this.listView.getChildAt(i3);
                if (childAdapterPosition != -1 && childAt.getTop() < i) {
                    i = childAt.getTop();
                    i2 = childAdapterPosition;
                    view = childAt;
                }
            }
            if (view != null) {
                this.savedScrollPosition = i2;
                int top = view.getTop();
                this.savedScrollOffset = top;
                if (this.savedScrollPosition == 0 && !this.allowPullingDown && top > AndroidUtilities.dp(88.0f)) {
                    this.savedScrollOffset = AndroidUtilities.dp(88.0f);
                }
                this.layoutManager.scrollToPositionWithOffset(i2, view.getTop() - this.listView.getPaddingTop());
            }
        }
    }

    public void scrollToSharedMedia() {
        this.layoutManager.scrollToPositionWithOffset(this.sharedMediaRow, -this.listView.getPaddingTop());
    }

    /* access modifiers changed from: private */
    public void onTextDetailCellImageClicked(View view) {
        View view2 = (View) view.getParent();
        if (view2.getTag() != null && ((Integer) view2.getTag()).intValue() == this.usernameRow) {
            Bundle bundle = new Bundle();
            bundle.putLong("chat_id", this.chatId);
            bundle.putLong("user_id", this.userId);
            presentFragment(new QrActivity(bundle));
        }
    }

    /* access modifiers changed from: protected */
    public void onBecomeFullyVisible() {
        super.onBecomeFullyVisible();
        try {
            Drawable mutate = this.fragmentView.getContext().getResources().getDrawable(R.drawable.floating_shadow_profile).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(mutate, Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), getThemedColor("profile_actionBackground"), getThemedColor("profile_actionPressedBackground")), 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            this.writeButton.setBackground(combinedDrawable);
        } catch (Exception unused) {
        }
    }

    private boolean isQrNeedVisible() {
        char c;
        if (!TextUtils.isEmpty(getUserConfig().getCurrentUser().username)) {
            return true;
        }
        ArrayList<TLRPC$PrivacyRule> privacyRules = ContactsController.getInstance(this.currentAccount).getPrivacyRules(6);
        if (privacyRules == null) {
            return false;
        }
        int i = 0;
        while (true) {
            c = 2;
            if (i >= privacyRules.size()) {
                break;
            }
            TLRPC$PrivacyRule tLRPC$PrivacyRule = privacyRules.get(i);
            if (!(tLRPC$PrivacyRule instanceof TLRPC$TL_privacyValueAllowAll)) {
                if (tLRPC$PrivacyRule instanceof TLRPC$TL_privacyValueDisallowAll) {
                    break;
                } else if (tLRPC$PrivacyRule instanceof TLRPC$TL_privacyValueAllowContacts) {
                    c = 1;
                    break;
                } else {
                    i++;
                }
            } else {
                c = 0;
                break;
            }
        }
        if (c == 0 || c == 1) {
            return true;
        }
        return false;
    }

    private class DiffCallback extends DiffUtil.Callback {
        SparseIntArray newPositionToItem;
        ArrayList<TLRPC$ChatParticipant> oldChatParticipant;
        ArrayList<Integer> oldChatParticipantSorted;
        int oldMembersEndRow;
        int oldMembersStartRow;
        SparseIntArray oldPositionToItem;
        int oldRowCount;

        private DiffCallback() {
            this.oldPositionToItem = new SparseIntArray();
            this.newPositionToItem = new SparseIntArray();
            this.oldChatParticipant = new ArrayList<>();
            this.oldChatParticipantSorted = new ArrayList<>();
        }

        public int getOldListSize() {
            return this.oldRowCount;
        }

        public int getNewListSize() {
            return ProfileActivity.this.rowCount;
        }

        public boolean areItemsTheSame(int i, int i2) {
            TLRPC$ChatParticipant tLRPC$ChatParticipant;
            TLRPC$ChatParticipant tLRPC$ChatParticipant2;
            if (i2 < ProfileActivity.this.membersStartRow || i2 >= ProfileActivity.this.membersEndRow || i < this.oldMembersStartRow || i >= this.oldMembersEndRow) {
                int i3 = this.oldPositionToItem.get(i, -1);
                if (i3 != this.newPositionToItem.get(i2, -1) || i3 < 0) {
                    return false;
                }
                return true;
            }
            if (!this.oldChatParticipantSorted.isEmpty()) {
                tLRPC$ChatParticipant = this.oldChatParticipant.get(this.oldChatParticipantSorted.get(i - this.oldMembersStartRow).intValue());
            } else {
                tLRPC$ChatParticipant = this.oldChatParticipant.get(i - this.oldMembersStartRow);
            }
            if (!ProfileActivity.this.sortedUsers.isEmpty()) {
                tLRPC$ChatParticipant2 = (TLRPC$ChatParticipant) ProfileActivity.this.visibleChatParticipants.get(((Integer) ProfileActivity.this.visibleSortedUsers.get(i2 - ProfileActivity.this.membersStartRow)).intValue());
            } else {
                tLRPC$ChatParticipant2 = (TLRPC$ChatParticipant) ProfileActivity.this.visibleChatParticipants.get(i2 - ProfileActivity.this.membersStartRow);
            }
            if (tLRPC$ChatParticipant.user_id == tLRPC$ChatParticipant2.user_id) {
                return true;
            }
            return false;
        }

        public boolean areContentsTheSame(int i, int i2) {
            return areItemsTheSame(i, i2);
        }

        public void fillPositions(SparseIntArray sparseIntArray) {
            sparseIntArray.clear();
            put(1, ProfileActivity.this.setAvatarRow, sparseIntArray);
            put(2, ProfileActivity.this.setAvatarSectionRow, sparseIntArray);
            put(3, ProfileActivity.this.numberSectionRow, sparseIntArray);
            put(4, ProfileActivity.this.numberRow, sparseIntArray);
            put(5, ProfileActivity.this.setUsernameRow, sparseIntArray);
            put(6, ProfileActivity.this.bioRow, sparseIntArray);
            put(7, ProfileActivity.this.phoneSuggestionRow, sparseIntArray);
            put(8, ProfileActivity.this.phoneSuggestionSectionRow, sparseIntArray);
            put(9, ProfileActivity.this.passwordSuggestionRow, sparseIntArray);
            put(10, ProfileActivity.this.passwordSuggestionSectionRow, sparseIntArray);
            put(11, ProfileActivity.this.settingsSectionRow, sparseIntArray);
            put(12, ProfileActivity.this.settingsSectionRow2, sparseIntArray);
            put(13, ProfileActivity.this.notificationRow, sparseIntArray);
            put(14, ProfileActivity.this.languageRow, sparseIntArray);
            put(15, ProfileActivity.this.premiumRow, sparseIntArray);
            put(16, ProfileActivity.this.premiumSectionsRow, sparseIntArray);
            put(17, ProfileActivity.this.privacyRow, sparseIntArray);
            put(18, ProfileActivity.this.dataRow, sparseIntArray);
            put(19, ProfileActivity.this.chatRow, sparseIntArray);
            put(20, ProfileActivity.this.filtersRow, sparseIntArray);
            put(21, ProfileActivity.this.stickersRow, sparseIntArray);
            put(22, ProfileActivity.this.devicesRow, sparseIntArray);
            put(23, ProfileActivity.this.devicesSectionRow, sparseIntArray);
            put(24, ProfileActivity.this.helpHeaderRow, sparseIntArray);
            put(25, ProfileActivity.this.questionRow, sparseIntArray);
            put(26, ProfileActivity.this.faqRow, sparseIntArray);
            put(27, ProfileActivity.this.policyRow, sparseIntArray);
            put(28, ProfileActivity.this.helpSectionCell, sparseIntArray);
            put(29, ProfileActivity.this.debugHeaderRow, sparseIntArray);
            put(30, ProfileActivity.this.sendLogsRow, sparseIntArray);
            put(31, ProfileActivity.this.sendLastLogsRow, sparseIntArray);
            put(32, ProfileActivity.this.clearLogsRow, sparseIntArray);
            put(33, ProfileActivity.this.switchBackendRow, sparseIntArray);
            put(34, ProfileActivity.this.versionRow, sparseIntArray);
            put(35, ProfileActivity.this.emptyRow, sparseIntArray);
            put(36, ProfileActivity.this.bottomPaddingRow, sparseIntArray);
            put(37, ProfileActivity.this.infoHeaderRow, sparseIntArray);
            put(38, ProfileActivity.this.phoneRow, sparseIntArray);
            put(39, ProfileActivity.this.locationRow, sparseIntArray);
            put(40, ProfileActivity.this.userInfoRow, sparseIntArray);
            put(41, ProfileActivity.this.channelInfoRow, sparseIntArray);
            put(42, ProfileActivity.this.usernameRow, sparseIntArray);
            put(43, ProfileActivity.this.notificationsDividerRow, sparseIntArray);
            put(44, ProfileActivity.this.notificationsRow, sparseIntArray);
            put(45, ProfileActivity.this.infoSectionRow, sparseIntArray);
            put(46, ProfileActivity.this.sendMessageRow, sparseIntArray);
            put(47, ProfileActivity.this.reportRow, sparseIntArray);
            put(48, ProfileActivity.this.settingsTimerRow, sparseIntArray);
            put(49, ProfileActivity.this.settingsKeyRow, sparseIntArray);
            put(50, ProfileActivity.this.secretSettingsSectionRow, sparseIntArray);
            put(51, ProfileActivity.this.membersHeaderRow, sparseIntArray);
            put(52, ProfileActivity.this.addMemberRow, sparseIntArray);
            put(53, ProfileActivity.this.subscribersRow, sparseIntArray);
            put(54, ProfileActivity.this.subscribersRequestsRow, sparseIntArray);
            put(55, ProfileActivity.this.administratorsRow, sparseIntArray);
            put(56, ProfileActivity.this.blockedUsersRow, sparseIntArray);
            put(57, ProfileActivity.this.membersSectionRow, sparseIntArray);
            put(58, ProfileActivity.this.sharedMediaRow, sparseIntArray);
            put(59, ProfileActivity.this.unblockRow, sparseIntArray);
            put(60, ProfileActivity.this.addToGroupButtonRow, sparseIntArray);
            put(61, ProfileActivity.this.addToGroupInfoRow, sparseIntArray);
            put(62, ProfileActivity.this.joinRow, sparseIntArray);
            put(63, ProfileActivity.this.lastSectionRow, sparseIntArray);
        }

        private void put(int i, int i2, SparseIntArray sparseIntArray) {
            if (i2 >= 0) {
                sparseIntArray.put(i2, i);
            }
        }
    }

    public boolean isLightStatusBar() {
        int i;
        if (this.isPulledDown) {
            return false;
        }
        if (this.actionBar.isActionModeShowed()) {
            i = getThemedColor("actionBarActionModeDefault");
        } else if (this.mediaHeaderVisible) {
            i = getThemedColor("windowBackgroundWhite");
        } else {
            i = getThemedColor("actionBarDefault");
        }
        if (ColorUtils.calculateLuminance(i) > 0.699999988079071d) {
            return true;
        }
        return false;
    }
}
