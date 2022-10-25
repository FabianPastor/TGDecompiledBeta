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
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.util.Property;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebStorage;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Keep;
import androidx.collection.LongSparseArray;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import j$.util.function.Consumer;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ChatThemeController;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LanguageDetector;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$BotInfo;
import org.telegram.tgnet.TLRPC$ChannelLocation;
import org.telegram.tgnet.TLRPC$ChannelParticipant;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$ChatParticipant;
import org.telegram.tgnet.TLRPC$ChatParticipants;
import org.telegram.tgnet.TLRPC$ChatPhoto;
import org.telegram.tgnet.TLRPC$DecryptedMessageAction;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$EmojiStatus;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$InputFile;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$MessageAction;
import org.telegram.tgnet.TLRPC$Page;
import org.telegram.tgnet.TLRPC$PageBlock;
import org.telegram.tgnet.TLRPC$PageListItem;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$PrivacyRule;
import org.telegram.tgnet.TLRPC$ReplyMarkup;
import org.telegram.tgnet.TLRPC$TL_account_getPassword;
import org.telegram.tgnet.TLRPC$TL_account_password;
import org.telegram.tgnet.TLRPC$TL_account_updateEmojiStatus;
import org.telegram.tgnet.TLRPC$TL_boolTrue;
import org.telegram.tgnet.TLRPC$TL_channelFull;
import org.telegram.tgnet.TLRPC$TL_channelLocation;
import org.telegram.tgnet.TLRPC$TL_channelParticipant;
import org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin;
import org.telegram.tgnet.TLRPC$TL_channelParticipantCreator;
import org.telegram.tgnet.TLRPC$TL_channelParticipantsRecent;
import org.telegram.tgnet.TLRPC$TL_channels_channelParticipant;
import org.telegram.tgnet.TLRPC$TL_channels_channelParticipants;
import org.telegram.tgnet.TLRPC$TL_channels_getParticipant;
import org.telegram.tgnet.TLRPC$TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC$TL_chatAdminRights;
import org.telegram.tgnet.TLRPC$TL_chatBannedRights;
import org.telegram.tgnet.TLRPC$TL_chatChannelParticipant;
import org.telegram.tgnet.TLRPC$TL_chatFull;
import org.telegram.tgnet.TLRPC$TL_chatParticipant;
import org.telegram.tgnet.TLRPC$TL_chatParticipantAdmin;
import org.telegram.tgnet.TLRPC$TL_chatParticipants;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionSetMessageTTL;
import org.telegram.tgnet.TLRPC$TL_emojiStatus;
import org.telegram.tgnet.TLRPC$TL_emojiStatusEmpty;
import org.telegram.tgnet.TLRPC$TL_emojiStatusUntil;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated;
import org.telegram.tgnet.TLRPC$TL_forumTopic;
import org.telegram.tgnet.TLRPC$TL_inputGroupCall;
import org.telegram.tgnet.TLRPC$TL_inputPhoto;
import org.telegram.tgnet.TLRPC$TL_messageEncryptedAction;
import org.telegram.tgnet.TLRPC$TL_messages_getWebPage;
import org.telegram.tgnet.TLRPC$TL_messages_reportReaction;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$TL_pageBlockAnchor;
import org.telegram.tgnet.TLRPC$TL_pageBlockList;
import org.telegram.tgnet.TLRPC$TL_pageBlockParagraph;
import org.telegram.tgnet.TLRPC$TL_pageListItemText;
import org.telegram.tgnet.TLRPC$TL_peerNotifySettings;
import org.telegram.tgnet.TLRPC$TL_peerUser;
import org.telegram.tgnet.TLRPC$TL_photo;
import org.telegram.tgnet.TLRPC$TL_photos_photo;
import org.telegram.tgnet.TLRPC$TL_photos_updateProfilePhoto;
import org.telegram.tgnet.TLRPC$TL_photos_uploadProfilePhoto;
import org.telegram.tgnet.TLRPC$TL_privacyValueAllowAll;
import org.telegram.tgnet.TLRPC$TL_privacyValueAllowContacts;
import org.telegram.tgnet.TLRPC$TL_privacyValueDisallowAll;
import org.telegram.tgnet.TLRPC$TL_userEmpty;
import org.telegram.tgnet.TLRPC$TL_userProfilePhoto;
import org.telegram.tgnet.TLRPC$TL_userProfilePhotoEmpty;
import org.telegram.tgnet.TLRPC$TL_username;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserFull;
import org.telegram.tgnet.TLRPC$UserProfilePhoto;
import org.telegram.tgnet.TLRPC$UserStatus;
import org.telegram.tgnet.TLRPC$VideoSize;
import org.telegram.tgnet.TLRPC$WebPage;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.AboutLinkCell;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.DrawerProfileCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.SettingsSearchCell;
import org.telegram.ui.Cells.SettingsSuggestionCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.ChatRightsEditActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.AudioPlayerAlert;
import org.telegram.ui.Components.AutoDeletePopupWrapper;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackButtonMenu;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ChatAvatarContainer;
import org.telegram.ui.Components.ChatNotificationsPopupWrapper;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CrossfadeDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EmojiPacksAlert;
import org.telegram.ui.Components.FloatingDebug.FloatingDebugController;
import org.telegram.ui.Components.FragmentContextView;
import org.telegram.ui.Components.HintView;
import org.telegram.ui.Components.ImageUpdater;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.GiftPremiumBottomSheet;
import org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet;
import org.telegram.ui.Components.Premium.ProfilePremiumCell;
import org.telegram.ui.Components.ProfileGalleryView;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
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
import org.telegram.ui.PinchToZoomHelper;
import org.telegram.ui.ProfileActivity;
import org.telegram.ui.SelectAnimatedEmojiDialog;
/* loaded from: classes3.dex */
public class ProfileActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, DialogsActivity.DialogsActivityDelegate, SharedMediaLayout.SharedMediaPreloaderDelegate, ImageUpdater.ImageUpdaterDelegate, SharedMediaLayout.Delegate {
    private Property<ActionBar, Float> ACTIONBAR_HEADER_PROGRESS;
    private final Property<ProfileActivity, Float> HEADER_SHADOW;
    private AboutLinkCell aboutLinkCell;
    private int actionBarAnimationColorFrom;
    private Paint actionBarBackgroundPaint;
    private int addMemberRow;
    private int addToGroupButtonRow;
    private int addToGroupInfoRow;
    private int administratorsRow;
    private boolean allowProfileAnimation;
    private boolean allowPullingDown;
    private DrawerProfileCell.AnimatedStatusView animatedStatusView;
    private ActionBarMenuItem animatingItem;
    private float animationProgress;
    private ActionBarMenuSubItem autoDeleteItem;
    TimerDrawable autoDeleteItemDrawable;
    AutoDeletePopupWrapper autoDeletePopupWrapper;
    private TLRPC$FileLocation avatar;
    private AnimatorSet avatarAnimation;
    private TLRPC$FileLocation avatarBig;
    private int avatarColor;
    private FrameLayout avatarContainer;
    private FrameLayout avatarContainer2;
    private AvatarDrawable avatarDrawable;
    private AvatarImageView avatarImage;
    private RadialProgressView avatarProgressView;
    private float avatarScale;
    private float avatarX;
    private float avatarY;
    private ProfileGalleryView avatarsViewPager;
    private PagerIndicatorView avatarsViewPagerIndicatorView;
    private long banFromGroup;
    private int bioRow;
    private int blockedUsersRow;
    private TLRPC$BotInfo botInfo;
    private int bottomPaddingRow;
    private ActionBarMenuItem callItem;
    private boolean callItemVisible;
    private RLottieDrawable cameraDrawable;
    private boolean canSearchMembers;
    private RLottieDrawable cellCameraDrawable;
    private int channelInfoRow;
    private long chatId;
    private TLRPC$ChatFull chatInfo;
    private int chatRow;
    private int clearLogsRow;
    private NestedFrameLayout contentView;
    private boolean creatingChat;
    private CharSequence currentBio;
    private TLRPC$ChannelParticipant currentChannelParticipant;
    private TLRPC$Chat currentChat;
    private TLRPC$EncryptedChat currentEncryptedChat;
    private float currentExpanAnimatorFracture;
    private TLRPC$TL_account_password currentPassword;
    private int dataRow;
    private int debugHeaderRow;
    private int devicesRow;
    private int devicesSectionRow;
    private long dialogId;
    private boolean disableProfileAnimation;
    private boolean doNotSetForeground;
    private ActionBarMenuItem editItem;
    private boolean editItemVisible;
    private AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable[] emojiStatusDrawable;
    private int emptyRow;
    private StickerEmptyView emptyView;
    private ValueAnimator expandAnimator;
    private float[] expandAnimatorValues;
    private boolean expandPhoto;
    private float expandProgress;
    private float extraHeight;
    private int faqRow;
    private int filtersRow;
    private boolean firstLayout;
    private boolean fragmentOpened;
    private HintView fwdRestrictedHint;
    private boolean hasVoiceChatItem;
    private AnimatorSet headerAnimatorSet;
    protected float headerShadowAlpha;
    private AnimatorSet headerShadowAnimatorSet;
    private int helpHeaderRow;
    private int helpSectionCell;
    private ImageUpdater imageUpdater;
    private int infoHeaderRow;
    private int infoSectionRow;
    private float initialAnimationExtraHeight;
    private boolean invalidateScroll;
    private boolean isBot;
    public boolean isFragmentOpened;
    private boolean isInLandscapeMode;
    private boolean[] isOnline;
    private boolean isPulledDown;
    private boolean isQrItemVisible;
    private boolean isTopic;
    private int joinRow;
    private int languageRow;
    private float lastEmojiStatusProgress;
    private int lastMeasuredContentHeight;
    private int lastMeasuredContentWidth;
    private int lastSectionRow;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private int listContentHeight;
    private RecyclerListView listView;
    private float listViewVelocityY;
    private boolean loadingUsers;
    private int locationRow;
    private Drawable lockIconDrawable;
    private AudioPlayerAlert.ClippingTextViewSwitcher mediaCounterTextView;
    private float mediaHeaderAnimationProgress;
    private boolean mediaHeaderVisible;
    private int membersEndRow;
    private int membersHeaderRow;
    private int membersSectionRow;
    private int membersStartRow;
    private long mergeDialogId;
    private SimpleTextView[] nameTextView;
    private String nameTextViewRightDrawableContentDescription;
    private float nameX;
    private float nameY;
    private int navigationBarAnimationColorFrom;
    private boolean needSendMessage;
    private boolean needTimerImage;
    private int notificationRow;
    private int notificationsDividerRow;
    HashSet<Integer> notificationsExceptionTopics;
    private int notificationsRow;
    private int notificationsSimpleRow;
    private int numberRow;
    private int numberSectionRow;
    private int onlineCount;
    private SimpleTextView[] onlineTextView;
    private float onlineX;
    private float onlineY;
    private boolean openAnimationInProgress;
    private boolean openingAvatar;
    private ActionBarMenuItem otherItem;
    private int overlayCountVisible;
    private OverlaysView overlaysView;
    private LongSparseArray<TLRPC$ChatParticipant> participantsMap;
    private int passwordSuggestionRow;
    private int passwordSuggestionSectionRow;
    private int phoneRow;
    private int phoneSuggestionRow;
    private int phoneSuggestionSectionRow;
    PinchToZoomHelper pinchToZoomHelper;
    private int playProfileAnimation;
    private int policyRow;
    private HashMap<Integer, Integer> positionToOffset;
    private CrossfadeDrawable premiumCrossfadeDrawable;
    private int premiumRow;
    private int premiumSectionsRow;
    private Drawable premiumStarDrawable;
    private ImageLocation prevLoadedImageLocation;
    ChatActivity previousTransitionFragment;
    private int privacyRow;
    boolean profileTransitionInProgress;
    private PhotoViewer.PhotoViewerProvider provider;
    private ActionBarMenuItem qrItem;
    private AnimatorSet qrItemAnimation;
    private int questionRow;
    private boolean recreateMenuAfterAnimation;
    private Rect rect;
    private int reportDividerRow;
    private long reportReactionFromDialogId;
    private int reportReactionMessageId;
    private int reportReactionRow;
    private int reportRow;
    private boolean reportSpam;
    private Theme.ResourcesProvider resourcesProvider;
    private int rowCount;
    int savedScrollOffset;
    int savedScrollPosition;
    private ScamDrawable scamDrawable;
    private AnimatorSet scrimAnimatorSet;
    private Paint scrimPaint;
    private View scrimView;
    private boolean scrolling;
    private SearchAdapter searchAdapter;
    private ActionBarMenuItem searchItem;
    private RecyclerListView searchListView;
    private boolean searchMode;
    private int searchTransitionOffset;
    private float searchTransitionProgress;
    private Animator searchViewTransition;
    private int secretSettingsSectionRow;
    private SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow selectAnimatedEmojiDialog;
    private long selectedUser;
    private int sendLastLogsRow;
    private int sendLogsRow;
    private int sendMessageRow;
    private TextCell setAvatarCell;
    private int setAvatarRow;
    private int setAvatarSectionRow;
    private int setUsernameRow;
    private int settingsKeyRow;
    private int settingsSectionRow;
    private int settingsSectionRow2;
    private int settingsTimerRow;
    private SharedMediaLayout sharedMediaLayout;
    private boolean sharedMediaLayoutAttached;
    private SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader;
    private int sharedMediaRow;
    private ArrayList<Integer> sortedUsers;
    private int stickersRow;
    private int subscribersRequestsRow;
    private int subscribersRow;
    private int switchBackendRow;
    private ImageView timeItem;
    private TimerDrawable timerDrawable;
    private float titleAnimationsYDiff;
    private TopView topView;
    private int topicId;
    private boolean transitionAnimationInProress;
    private int transitionIndex;
    private View transitionOnlineText;
    private ImageView ttlIconView;
    private int unblockRow;
    private UndoView undoView;
    private ImageLocation uploadingImageLocation;
    private boolean userBlocked;
    private long userId;
    private TLRPC$UserFull userInfo;
    private int userInfoRow;
    private int usernameRow;
    private boolean usersEndReached;
    private int usersForceShowingIn;
    private Drawable verifiedCheckDrawable;
    private CrossfadeDrawable verifiedCrossfadeDrawable;
    private Drawable verifiedDrawable;
    private int versionRow;
    private ActionBarMenuItem videoCallItem;
    private boolean videoCallItemVisible;
    private final ArrayList<TLRPC$ChatParticipant> visibleChatParticipants;
    private final ArrayList<Integer> visibleSortedUsers;
    private Paint whitePaint;
    private RLottieImageView writeButton;
    private AnimatorSet writeButtonAnimation;

    @Override // org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate
    public /* synthetic */ String getInitialSearchString() {
        return ImageUpdater.ImageUpdaterDelegate.CC.$default$getInitialSearchString(this);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void access$27200(ProfileActivity profileActivity, View view) {
        profileActivity.onTextDetailCellImageClicked(view);
    }

    static /* synthetic */ int access$7712(ProfileActivity profileActivity, int i) {
        int i2 = profileActivity.listContentHeight + i;
        profileActivity.listContentHeight = i2;
        return i2;
    }

    public int getTopicId() {
        return this.topicId;
    }

    /* loaded from: classes3.dex */
    public static class AvatarImageView extends BackupImageView {
        ProfileGalleryView avatarsViewPager;
        private ImageReceiver.BitmapHolder drawableHolder;
        private float foregroundAlpha;
        private ImageReceiver foregroundImageReceiver;
        private final Paint placeholderPaint;
        private final RectF rect;

        public void setAvatarsViewPager(ProfileGalleryView profileGalleryView) {
            this.avatarsViewPager = profileGalleryView;
        }

        public AvatarImageView(Context context) {
            super(context);
            this.rect = new RectF();
            this.foregroundImageReceiver = new ImageReceiver(this);
            Paint paint = new Paint(1);
            this.placeholderPaint = paint;
            paint.setColor(-16777216);
        }

        public void setForegroundImage(ImageLocation imageLocation, String str, Drawable drawable) {
            this.foregroundImageReceiver.setImage(imageLocation, str, drawable, 0L, (String) null, (Object) null, 0);
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

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.telegram.ui.Components.BackupImageView, android.view.View
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.foregroundImageReceiver.onDetachedFromWindow();
            ImageReceiver.BitmapHolder bitmapHolder = this.drawableHolder;
            if (bitmapHolder != null) {
                bitmapHolder.release();
                this.drawableHolder = null;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.telegram.ui.Components.BackupImageView, android.view.View
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.foregroundImageReceiver.onAttachedToWindow();
        }

        @Override // org.telegram.ui.Components.BackupImageView
        public void setRoundRadius(int i) {
            super.setRoundRadius(i);
            this.foregroundImageReceiver.setRoundRadius(i);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.telegram.ui.Components.BackupImageView, android.view.View
        public void onDraw(Canvas canvas) {
            AnimatedEmojiDrawable animatedEmojiDrawable = this.animatedEmojiDrawable;
            ImageReceiver imageReceiver = animatedEmojiDrawable != null ? animatedEmojiDrawable.getImageReceiver() : this.imageReceiver;
            if (this.foregroundAlpha < 1.0f) {
                imageReceiver.setImageCoords(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
                imageReceiver.draw(canvas);
            }
            if (this.foregroundAlpha > 0.0f) {
                if (this.foregroundImageReceiver.getDrawable() != null) {
                    this.foregroundImageReceiver.setImageCoords(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
                    this.foregroundImageReceiver.setAlpha(this.foregroundAlpha);
                    this.foregroundImageReceiver.draw(canvas);
                    return;
                }
                this.rect.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
                this.placeholderPaint.setAlpha((int) (this.foregroundAlpha * 255.0f));
                float f = this.foregroundImageReceiver.getRoundRadius()[0];
                canvas.drawRoundRect(this.rect, f, f, this.placeholderPaint);
            }
        }

        @Override // android.view.View
        public void invalidate() {
            super.invalidate();
            ProfileGalleryView profileGalleryView = this.avatarsViewPager;
            if (profileGalleryView != null) {
                profileGalleryView.invalidate();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class TopView extends View {
        private int currentColor;
        private Paint paint;

        public TopView(Context context) {
            super(context);
            this.paint = new Paint();
        }

        @Override // android.view.View
        protected void onMeasure(int i, int i2) {
            setMeasuredDimension(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i) + AndroidUtilities.dp(3.0f));
        }

        @Override // android.view.View
        public void setBackgroundColor(int i) {
            if (i != this.currentColor) {
                this.currentColor = i;
                this.paint.setColor(i);
                invalidate();
            }
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            float currentActionBarHeight = ProfileActivity.this.extraHeight + ActionBar.getCurrentActionBarHeight() + (((BaseFragment) ProfileActivity.this).actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ProfileActivity.this.searchTransitionOffset;
            int i = (int) ((1.0f - ProfileActivity.this.mediaHeaderAnimationProgress) * currentActionBarHeight);
            if (i != 0) {
                if (ProfileActivity.this.previousTransitionFragment != null) {
                    Rect rect = AndroidUtilities.rectTmp2;
                    rect.set(0, 0, getMeasuredWidth(), i);
                    ProfileActivity.this.previousTransitionFragment.contentView.drawBlurRect(canvas, getY(), rect, ProfileActivity.this.previousTransitionFragment.getActionBar().blurScrimPaint, true);
                }
                this.paint.setColor(this.currentColor);
                canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), i, this.paint);
            }
            if (i != currentActionBarHeight) {
                this.paint.setColor(ProfileActivity.this.getThemedColor("windowBackgroundWhite"));
                Rect rect2 = AndroidUtilities.rectTmp2;
                rect2.set(0, i, getMeasuredWidth(), (int) currentActionBarHeight);
                ProfileActivity.this.contentView.drawBlurRect(canvas, getY(), rect2, this.paint, true);
            }
            if (((BaseFragment) ProfileActivity.this).parentLayout != null) {
                ((BaseFragment) ProfileActivity.this).parentLayout.drawHeaderShadow(canvas, (int) (ProfileActivity.this.headerShadowAlpha * 255.0f), (int) currentActionBarHeight);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class OverlaysView extends View implements ProfileGalleryView.Callback {
        private float alpha;
        private float[] alphas;
        private final ValueAnimator animator;
        private final float[] animatorValues;
        private final Paint backgroundPaint;
        private final Paint barPaint;
        private final GradientDrawable bottomOverlayGradient;
        private final Rect bottomOverlayRect;
        private float currentAnimationValue;
        private int currentLoadingAnimationDirection;
        private float currentLoadingAnimationProgress;
        private float currentProgress;
        private boolean isOverlaysVisible;
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
            this.statusBarHeight = (!((BaseFragment) ProfileActivity.this).actionBar.getOccupyStatusBar() || ((BaseFragment) ProfileActivity.this).inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight;
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
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.animator = ofFloat;
            ofFloat.setDuration(250L);
            ofFloat.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ProfileActivity$OverlaysView$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ProfileActivity.OverlaysView.this.lambda$new$0(valueAnimator);
                }
            });
            ofFloat.addListener(new AnimatorListenerAdapter(ProfileActivity.this) { // from class: org.telegram.ui.ProfileActivity.OverlaysView.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    if (!OverlaysView.this.isOverlaysVisible) {
                        OverlaysView.this.setVisibility(8);
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animator) {
                    OverlaysView.this.setVisibility(0);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
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
                    this.animator.setDuration(((1.0f - lerp) * 250.0f) / f);
                } else {
                    this.animator.setDuration((250.0f * lerp) / f);
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

        @Override // android.view.View
        protected void onSizeChanged(int i, int i2, int i3, int i4) {
            int currentActionBarHeight = this.statusBarHeight + ActionBar.getCurrentActionBarHeight();
            this.topOverlayRect.set(0, 0, i, (int) (currentActionBarHeight * 0.5f));
            this.bottomOverlayRect.set(0, (int) (i2 - (AndroidUtilities.dp(72.0f) * 0.5f)), i, i2);
            this.topOverlayGradient.setBounds(0, this.topOverlayRect.bottom, i, currentActionBarHeight + AndroidUtilities.dp(16.0f));
            this.bottomOverlayGradient.setBounds(0, (i2 - AndroidUtilities.dp(72.0f)) - AndroidUtilities.dp(24.0f), i, this.bottomOverlayRect.top);
            int i5 = i / 5;
            this.pressedOverlayGradient[0].setBounds(0, 0, i5, i2);
            this.pressedOverlayGradient[1].setBounds(i - i5, 0, i, i2);
        }

        /* JADX WARN: Removed duplicated region for block: B:69:0x021c  */
        /* JADX WARN: Removed duplicated region for block: B:72:0x0241  */
        /* JADX WARN: Removed duplicated region for block: B:75:0x0259  */
        /* JADX WARN: Removed duplicated region for block: B:76:0x025c  */
        @Override // android.view.View
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        protected void onDraw(android.graphics.Canvas r22) {
            /*
                Method dump skipped, instructions count: 805
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.OverlaysView.onDraw(android.graphics.Canvas):void");
        }

        @Override // org.telegram.ui.Components.ProfileGalleryView.Callback
        public void onDown(boolean z) {
            this.pressedOverlayVisible[!z ? 1 : 0] = true;
            postInvalidateOnAnimation();
        }

        @Override // org.telegram.ui.Components.ProfileGalleryView.Callback
        public void onRelease() {
            Arrays.fill(this.pressedOverlayVisible, false);
            postInvalidateOnAnimation();
        }

        @Override // org.telegram.ui.Components.ProfileGalleryView.Callback
        public void onPhotosLoaded() {
            ProfileActivity.this.updateProfileData(false);
        }

        @Override // org.telegram.ui.Components.ProfileGalleryView.Callback
        public void onVideoSet() {
            invalidate();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class NestedFrameLayout extends SizeNotifierFrameLayout implements NestedScrollingParent3 {
        private NestedScrollingParentHelper nestedScrollingParentHelper;

        @Override // androidx.core.view.NestedScrollingParent2
        public void onNestedScroll(View view, int i, int i2, int i3, int i4, int i5) {
        }

        @Override // android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
        public void onStopNestedScroll(View view) {
        }

        public NestedFrameLayout(Context context) {
            super(context);
            this.nestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        }

        @Override // androidx.core.view.NestedScrollingParent3
        public void onNestedScroll(View view, int i, int i2, int i3, int i4, int i5, int[] iArr) {
            if (view != ProfileActivity.this.listView || !ProfileActivity.this.sharedMediaLayoutAttached) {
                return;
            }
            RecyclerListView currentListView = ProfileActivity.this.sharedMediaLayout.getCurrentListView();
            if (ProfileActivity.this.sharedMediaLayout.getTop() != 0) {
                return;
            }
            iArr[1] = i4;
            currentListView.scrollBy(0, i4);
        }

        @Override // android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
        public boolean onNestedPreFling(View view, float f, float f2) {
            return super.onNestedPreFling(view, f, f2);
        }

        @Override // androidx.core.view.NestedScrollingParent2
        public void onNestedPreScroll(View view, int i, int i2, int[] iArr, int i3) {
            if (view == ProfileActivity.this.listView) {
                int i4 = -1;
                if (ProfileActivity.this.sharedMediaRow == -1 || !ProfileActivity.this.sharedMediaLayoutAttached) {
                    return;
                }
                boolean isSearchFieldVisible = ((BaseFragment) ProfileActivity.this).actionBar.isSearchFieldVisible();
                int top = ProfileActivity.this.sharedMediaLayout.getTop();
                boolean z = false;
                if (i2 >= 0) {
                    if (!isSearchFieldVisible) {
                        return;
                    }
                    RecyclerListView currentListView = ProfileActivity.this.sharedMediaLayout.getCurrentListView();
                    iArr[1] = i2;
                    if (top > 0) {
                        iArr[1] = iArr[1] - i2;
                    }
                    if (iArr[1] <= 0) {
                        return;
                    }
                    currentListView.scrollBy(0, iArr[1]);
                    return;
                }
                if (top <= 0) {
                    RecyclerListView currentListView2 = ProfileActivity.this.sharedMediaLayout.getCurrentListView();
                    int findFirstVisibleItemPosition = ((LinearLayoutManager) currentListView2.getLayoutManager()).findFirstVisibleItemPosition();
                    if (findFirstVisibleItemPosition != -1) {
                        RecyclerView.ViewHolder findViewHolderForAdapterPosition = currentListView2.findViewHolderForAdapterPosition(findFirstVisibleItemPosition);
                        if (findViewHolderForAdapterPosition != null) {
                            i4 = findViewHolderForAdapterPosition.itemView.getTop();
                        }
                        int paddingTop = currentListView2.getPaddingTop();
                        if (i4 != paddingTop || findFirstVisibleItemPosition != 0) {
                            iArr[1] = findFirstVisibleItemPosition != 0 ? i2 : Math.max(i2, i4 - paddingTop);
                            currentListView2.scrollBy(0, i2);
                            z = true;
                        }
                    }
                }
                if (!isSearchFieldVisible) {
                    return;
                }
                if (!z && top < 0) {
                    iArr[1] = i2 - Math.max(top, i2);
                } else {
                    iArr[1] = i2;
                }
            }
        }

        @Override // androidx.core.view.NestedScrollingParent2
        public boolean onStartNestedScroll(View view, View view2, int i, int i2) {
            return ProfileActivity.this.sharedMediaRow != -1 && i == 2;
        }

        @Override // androidx.core.view.NestedScrollingParent2
        public void onNestedScrollAccepted(View view, View view2, int i, int i2) {
            this.nestedScrollingParentHelper.onNestedScrollAccepted(view, view2, i);
        }

        @Override // androidx.core.view.NestedScrollingParent2
        public void onStopNestedScroll(View view, int i) {
            this.nestedScrollingParentHelper.onStopNestedScroll(view);
        }

        @Override // org.telegram.ui.Components.SizeNotifierFrameLayout
        protected void drawList(Canvas canvas, boolean z) {
            super.drawList(canvas, z);
            canvas.save();
            canvas.translate(0.0f, ProfileActivity.this.listView.getY());
            ProfileActivity.this.sharedMediaLayout.drawListForBlur(canvas);
            canvas.restore();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class PagerIndicatorView extends View {
        private final PagerAdapter adapter;
        private final ValueAnimator animator;
        private final float[] animatorValues;
        private final Paint backgroundPaint;
        private final RectF indicatorRect;
        private boolean isIndicatorVisible;
        private final TextPaint textPaint;

        public PagerIndicatorView(Context context) {
            super(context);
            this.indicatorRect = new RectF();
            this.animatorValues = new float[]{0.0f, 1.0f};
            PagerAdapter adapter = ProfileActivity.this.avatarsViewPager.getAdapter();
            this.adapter = adapter;
            setVisibility(8);
            TextPaint textPaint = new TextPaint(1);
            this.textPaint = textPaint;
            textPaint.setColor(-1);
            textPaint.setTypeface(Typeface.SANS_SERIF);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setTextSize(AndroidUtilities.dpf2(15.0f));
            Paint paint = new Paint(1);
            this.backgroundPaint = paint;
            paint.setColor(NUM);
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.animator = ofFloat;
            ofFloat.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ProfileActivity$PagerIndicatorView$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ProfileActivity.PagerIndicatorView.this.lambda$new$0(valueAnimator);
                }
            });
            ofFloat.addListener(new AnimatorListenerAdapter(ProfileActivity.this, ProfileActivity.this.expandPhoto) { // from class: org.telegram.ui.ProfileActivity.PagerIndicatorView.1
                final /* synthetic */ boolean val$expanded;

                {
                    this.val$expanded = r3;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
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
                        if (!ProfileActivity.this.videoCallItemVisible) {
                            return;
                        }
                        ProfileActivity.this.videoCallItem.setVisibility(8);
                        return;
                    }
                    PagerIndicatorView.this.setVisibility(8);
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
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
            ProfileActivity.this.avatarsViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(ProfileActivity.this) { // from class: org.telegram.ui.ProfileActivity.PagerIndicatorView.2
                private int prevPage;

                @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
                public void onPageScrollStateChanged(int i) {
                }

                @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
                public void onPageScrolled(int i, float f, int i2) {
                }

                @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
                public void onPageSelected(int i) {
                    int realPosition = ProfileActivity.this.avatarsViewPager.getRealPosition(i);
                    PagerIndicatorView.this.invalidateIndicatorRect(this.prevPage != realPosition);
                    this.prevPage = realPosition;
                    PagerIndicatorView.this.updateAvatarItems();
                }
            });
            adapter.registerDataSetObserver(new DataSetObserver(ProfileActivity.this) { // from class: org.telegram.ui.ProfileActivity.PagerIndicatorView.3
                @Override // android.database.DataSetObserver
                public void onChanged() {
                    int realCount = ProfileActivity.this.avatarsViewPager.getRealCount();
                    if (ProfileActivity.this.overlayCountVisible == 0 && realCount > 1 && realCount <= 20 && ProfileActivity.this.overlaysView.isOverlaysVisible()) {
                        ProfileActivity.this.overlayCountVisible = 1;
                    }
                    PagerIndicatorView.this.invalidateIndicatorRect(false);
                    PagerIndicatorView.this.refreshVisibility(1.0f);
                    PagerIndicatorView.this.updateAvatarItems();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
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

        /* JADX INFO: Access modifiers changed from: private */
        public void updateAvatarItemsInternal() {
            if (ProfileActivity.this.otherItem == null || ProfileActivity.this.avatarsViewPager == null || !ProfileActivity.this.isPulledDown) {
                return;
            }
            if (ProfileActivity.this.avatarsViewPager.getRealPosition() == 0) {
                ProfileActivity.this.otherItem.hideSubItem(33);
                ProfileActivity.this.otherItem.showSubItem(36);
                return;
            }
            ProfileActivity.this.otherItem.showSubItem(33);
            ProfileActivity.this.otherItem.hideSubItem(36);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updateAvatarItems() {
            if (ProfileActivity.this.imageUpdater == null) {
                return;
            }
            if (ProfileActivity.this.otherItem.isSubMenuShowing()) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ProfileActivity$PagerIndicatorView$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        ProfileActivity.PagerIndicatorView.this.updateAvatarItemsInternal();
                    }
                }, 500L);
            } else {
                updateAvatarItemsInternal();
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
                    this.animator.setDuration(0L);
                } else if (z) {
                    this.animator.setDuration(((1.0f - lerp) * 250.0f) / f);
                } else {
                    this.animator.setDuration((250.0f * lerp) / f);
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

        @Override // android.view.View
        protected void onSizeChanged(int i, int i2, int i3, int i4) {
            invalidateIndicatorRect(false);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void invalidateIndicatorRect(boolean z) {
            if (z) {
                ProfileActivity.this.overlaysView.saveCurrentPageProgress();
            }
            ProfileActivity.this.overlaysView.invalidate();
            float measureText = this.textPaint.measureText(getCurrentTitle());
            int i = 0;
            this.indicatorRect.right = (getMeasuredWidth() - AndroidUtilities.dp(54.0f)) - (ProfileActivity.this.qrItem != null ? AndroidUtilities.dp(48.0f) : 0);
            RectF rectF = this.indicatorRect;
            rectF.left = rectF.right - (measureText + AndroidUtilities.dpf2(16.0f));
            RectF rectF2 = this.indicatorRect;
            if (((BaseFragment) ProfileActivity.this).actionBar.getOccupyStatusBar()) {
                i = AndroidUtilities.statusBarHeight;
            }
            rectF2.top = i + AndroidUtilities.dp(15.0f);
            RectF rectF3 = this.indicatorRect;
            rectF3.bottom = rectF3.top + AndroidUtilities.dp(26.0f);
            setPivotX(this.indicatorRect.centerX());
            setPivotY(this.indicatorRect.centerY());
            invalidate();
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            float dpf2 = AndroidUtilities.dpf2(12.0f);
            canvas.drawRoundRect(this.indicatorRect, dpf2, dpf2, this.backgroundPaint);
            canvas.drawText(getCurrentTitle(), this.indicatorRect.centerX(), this.indicatorRect.top + AndroidUtilities.dpf2(18.5f), this.textPaint);
        }

        private String getCurrentTitle() {
            return this.adapter.getPageTitle(ProfileActivity.this.avatarsViewPager.getCurrentItem()).toString();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public ActionBarMenuItem getSecondaryMenuItem() {
            if (ProfileActivity.this.callItemVisible) {
                return ProfileActivity.this.callItem;
            }
            if (ProfileActivity.this.editItemVisible) {
                return ProfileActivity.this.editItem;
            }
            if (ProfileActivity.this.searchItem == null) {
                return null;
            }
            return ProfileActivity.this.searchItem;
        }
    }

    public ProfileActivity(Bundle bundle) {
        this(bundle, null);
    }

    public ProfileActivity(Bundle bundle, SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader) {
        super(bundle);
        this.nameTextView = new SimpleTextView[2];
        this.nameTextViewRightDrawableContentDescription = null;
        this.onlineTextView = new SimpleTextView[2];
        this.emojiStatusDrawable = new AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable[2];
        this.scrimView = null;
        this.scrimPaint = new Paint(1) { // from class: org.telegram.ui.ProfileActivity.1
            @Override // android.graphics.Paint
            public void setAlpha(int i) {
                super.setAlpha(i);
                ((BaseFragment) ProfileActivity.this).fragmentView.invalidate();
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
        this.reportReactionMessageId = 0;
        this.reportReactionFromDialogId = 0L;
        this.notificationsExceptionTopics = new HashSet<>();
        this.HEADER_SHADOW = new AnimationProperties.FloatProperty<ProfileActivity>("headerShadow") { // from class: org.telegram.ui.ProfileActivity.2
            @Override // org.telegram.ui.Components.AnimationProperties.FloatProperty
            public void setValue(ProfileActivity profileActivity, float f) {
                ProfileActivity profileActivity2 = ProfileActivity.this;
                profileActivity2.headerShadowAlpha = f;
                profileActivity2.topView.invalidate();
            }

            @Override // android.util.Property
            public Float get(ProfileActivity profileActivity) {
                return Float.valueOf(ProfileActivity.this.headerShadowAlpha);
            }
        };
        this.provider = new PhotoViewer.EmptyPhotoViewerProvider() { // from class: org.telegram.ui.ProfileActivity.3
            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC$FileLocation tLRPC$FileLocation, int i, boolean z) {
                TLRPC$Chat chat;
                TLRPC$ChatPhoto tLRPC$ChatPhoto;
                TLRPC$FileLocation tLRPC$FileLocation2;
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
                placeProviderObject.size = -1L;
                placeProviderObject.radius = ProfileActivity.this.avatarImage.getImageReceiver().getRoundRadius();
                placeProviderObject.scale = ProfileActivity.this.avatarContainer.getScaleX();
                if (ProfileActivity.this.userId == ProfileActivity.this.getUserConfig().clientUserId) {
                    z2 = true;
                }
                placeProviderObject.canEdit = z2;
                return placeProviderObject;
            }

            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public void willHidePhotoViewer() {
                ProfileActivity.this.avatarImage.getImageReceiver().setVisible(true, true);
            }

            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public void openPhotoForEdit(String str, String str2, boolean z) {
                ProfileActivity.this.imageUpdater.openPhotoForEdit(str, str2, 0, z);
            }
        };
        this.ACTIONBAR_HEADER_PROGRESS = new AnimationProperties.FloatProperty<ActionBar>("animationProgress") { // from class: org.telegram.ui.ProfileActivity.35
            @Override // org.telegram.ui.Components.AnimationProperties.FloatProperty
            public void setValue(ActionBar actionBar, float f) {
                ProfileActivity.this.mediaHeaderAnimationProgress = f;
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
                ((BaseFragment) ProfileActivity.this).actionBar.setItemsColor(AndroidUtilities.getOffsetColor(ProfileActivity.this.getThemedColor("actionBarDefaultIcon"), ProfileActivity.this.getThemedColor("actionBarActionModeDefaultIcon"), f, 1.0f), false);
                ((BaseFragment) ProfileActivity.this).actionBar.setItemsBackgroundColor(AndroidUtilities.getOffsetColor(ProfileActivity.this.getThemedColor("avatar_actionBarSelectorBlue"), ProfileActivity.this.getThemedColor("actionBarActionModeDefaultSelector"), f, 1.0f), false);
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
                ProfileActivity.this.updateEmojiStatusDrawableColor();
                if (ProfileActivity.this.avatarsViewPagerIndicatorView.getSecondaryMenuItem() != null) {
                    if (!ProfileActivity.this.videoCallItemVisible && !ProfileActivity.this.editItemVisible && !ProfileActivity.this.callItemVisible) {
                        return;
                    }
                    ProfileActivity profileActivity = ProfileActivity.this;
                    profileActivity.needLayoutText(Math.min(1.0f, profileActivity.extraHeight / AndroidUtilities.dp(88.0f)));
                }
            }

            @Override // android.util.Property
            public Float get(ActionBar actionBar) {
                return Float.valueOf(ProfileActivity.this.mediaHeaderAnimationProgress);
            }
        };
        this.scrimAnimatorSet = null;
        this.savedScrollPosition = -1;
        this.sharedMediaPreloader = sharedMediaPreloader;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        this.userId = this.arguments.getLong("user_id", 0L);
        this.chatId = this.arguments.getLong("chat_id", 0L);
        int i = this.arguments.getInt("topic_id", 0);
        this.topicId = i;
        this.isTopic = i != 0;
        this.banFromGroup = this.arguments.getLong("ban_chat_id", 0L);
        this.reportReactionMessageId = this.arguments.getInt("report_reaction_message_id", 0);
        this.reportReactionFromDialogId = this.arguments.getLong("report_reaction_from_dialog_id", 0L);
        this.reportSpam = this.arguments.getBoolean("reportSpam", false);
        if (!this.expandPhoto) {
            boolean z = this.arguments.getBoolean("expandPhoto", false);
            this.expandPhoto = z;
            if (z) {
                this.needSendMessage = true;
            }
        }
        if (this.userId != 0) {
            long j = this.arguments.getLong("dialog_id", 0L);
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
                ImageUpdater imageUpdater = new ImageUpdater(true);
                this.imageUpdater = imageUpdater;
                imageUpdater.setOpenWithFrontfaceCamera(true);
                ImageUpdater imageUpdater2 = this.imageUpdater;
                imageUpdater2.parentFragment = this;
                imageUpdater2.setDelegate(this);
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
                final CountDownLatch countDownLatch = new CountDownLatch(1);
                getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda23
                    @Override // java.lang.Runnable
                    public final void run() {
                        ProfileActivity.this.lambda$onFragmentCreate$0(countDownLatch);
                    }
                });
                try {
                    countDownLatch.await();
                } catch (Exception e) {
                    FileLog.e(e);
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
                this.chatInfo = getMessagesStorage().loadChatInfo(this.chatId, false, null, false, false);
            }
            if (!this.isTopic && this.currentChat.forum) {
                getNotificationsController().loadTopicsNotificationsExceptions(-this.chatId, new Consumer() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda35
                    @Override // j$.util.function.Consumer
                    public final void accept(Object obj) {
                        ProfileActivity.this.lambda$onFragmentCreate$1((HashSet) obj);
                    }

                    @Override // j$.util.function.Consumer
                    public /* synthetic */ Consumer andThen(Consumer consumer) {
                        return consumer.getClass();
                    }
                });
            }
        }
        if (this.sharedMediaPreloader == null) {
            this.sharedMediaPreloader = new SharedMediaLayout.SharedMediaPreloader(this);
        }
        this.sharedMediaPreloader.addDelegate(this);
        getNotificationCenter().addObserver(this, NotificationCenter.updateInterfaces);
        getNotificationCenter().addObserver(this, NotificationCenter.didReceiveNewMessages);
        getNotificationCenter().addObserver(this, NotificationCenter.closeChats);
        getNotificationCenter().addObserver(this, NotificationCenter.topicsDidLoaded);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        updateRowsIds();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        if (this.arguments.containsKey("preload_messages")) {
            getMessagesController().ensureMessagesLoaded(this.userId, 0, null);
        }
        if (this.userId != 0 && UserObject.isUserSelf(getMessagesController().getUser(Long.valueOf(this.userId)))) {
            getConnectionsManager().sendRequest(new TLRPC$TL_account_getPassword(), new RequestDelegate() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda40
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    ProfileActivity.this.lambda$onFragmentCreate$2(tLObject, tLRPC$TL_error);
                }
            });
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onFragmentCreate$0(CountDownLatch countDownLatch) {
        this.currentChat = getMessagesStorage().getChat(this.chatId);
        countDownLatch.countDown();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onFragmentCreate$1(HashSet hashSet) {
        this.notificationsExceptionTopics.addAll(hashSet);
        int i = this.notificationsRow;
        if (i >= 0) {
            this.listAdapter.notifyItemChanged(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onFragmentCreate$2(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject instanceof TLRPC$TL_account_password) {
            this.currentPassword = (TLRPC$TL_account_password) tLObject;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        SharedMediaLayout sharedMediaLayout = this.sharedMediaLayout;
        if (sharedMediaLayout != null) {
            sharedMediaLayout.onDestroy();
        }
        SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader = this.sharedMediaPreloader;
        if (sharedMediaPreloader != null) {
            sharedMediaPreloader.onDestroy(this);
        }
        SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader2 = this.sharedMediaPreloader;
        if (sharedMediaPreloader2 != null) {
            sharedMediaPreloader2.removeDelegate(this);
        }
        getNotificationCenter().removeObserver(this, NotificationCenter.updateInterfaces);
        getNotificationCenter().removeObserver(this, NotificationCenter.closeChats);
        getNotificationCenter().removeObserver(this, NotificationCenter.didReceiveNewMessages);
        getNotificationCenter().removeObserver(this, NotificationCenter.topicsDidLoaded);
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
            avatarImageView.setImageDrawable(null);
        }
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.clear();
        }
        PinchToZoomHelper pinchToZoomHelper = this.pinchToZoomHelper;
        if (pinchToZoomHelper != null) {
            pinchToZoomHelper.clear();
        }
        for (int i = 0; i < 2; i++) {
            AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable[] swapAnimatedEmojiDrawableArr = this.emojiStatusDrawable;
            if (swapAnimatedEmojiDrawableArr[i] != null) {
                swapAnimatedEmojiDrawableArr[i].detach();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ActionBar createActionBar(Context context) {
        ChatActivity.ThemeDelegate themeDelegate;
        BaseFragment lastFragment = this.parentLayout.getLastFragment();
        if ((lastFragment instanceof ChatActivity) && (themeDelegate = ((ChatActivity) lastFragment).themeDelegate) != null && themeDelegate.getCurrentTheme() != null) {
            this.resourcesProvider = lastFragment.getResourceProvider();
        }
        ActionBar actionBar = new ActionBar(context, this.resourcesProvider) { // from class: org.telegram.ui.ProfileActivity.4
            @Override // org.telegram.ui.ActionBar.ActionBar, android.view.View
            public boolean onTouchEvent(MotionEvent motionEvent) {
                ProfileActivity.this.avatarContainer.getHitRect(ProfileActivity.this.rect);
                if (ProfileActivity.this.rect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                    return false;
                }
                return super.onTouchEvent(motionEvent);
            }

            @Override // org.telegram.ui.ActionBar.ActionBar
            public void setItemsColor(int i, boolean z) {
                super.setItemsColor(i, z);
                if (z || ProfileActivity.this.ttlIconView == null) {
                    return;
                }
                ProfileActivity.this.ttlIconView.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
            }
        };
        boolean z = true;
        actionBar.setForceSkipTouches(true);
        actionBar.setBackgroundColor(0);
        actionBar.setItemsBackgroundColor(getThemedColor("avatar_actionBarSelectorBlue"), false);
        actionBar.setItemsColor(getThemedColor("actionBarDefaultIcon"), false);
        actionBar.setBackButtonDrawable(new BackDrawable(false));
        actionBar.setCastShadows(false);
        actionBar.setAddToContainer(false);
        actionBar.setClipContent(true);
        if (Build.VERSION.SDK_INT < 21 || AndroidUtilities.isTablet() || this.inBubbleMode) {
            z = false;
        }
        actionBar.setOccupyStatusBar(z);
        final ImageView backButton = actionBar.getBackButton();
        backButton.setOnLongClickListener(new View.OnLongClickListener() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda18
            @Override // android.view.View.OnLongClickListener
            public final boolean onLongClick(View view) {
                boolean lambda$createActionBar$4;
                lambda$createActionBar$4 = ProfileActivity.this.lambda$createActionBar$4(backButton, view);
                return lambda$createActionBar$4;
            }
        });
        return actionBar;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createActionBar$4(ImageView imageView, View view) {
        ActionBarPopupWindow show = BackButtonMenu.show(this, imageView, getDialogId(), this.resourcesProvider);
        if (show != null) {
            show.setOnDismissListener(new PopupWindow.OnDismissListener() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda19
                @Override // android.widget.PopupWindow.OnDismissListener
                public final void onDismiss() {
                    ProfileActivity.this.lambda$createActionBar$3();
                }
            });
            dimBehindView(imageView, 0.3f);
            UndoView undoView = this.undoView;
            if (undoView != null) {
                undoView.hide(true, 1);
            }
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createActionBar$3() {
        dimBehindView(false);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(final Context context) {
        int i;
        Object obj;
        Theme.ResourcesProvider resourcesProvider;
        boolean z;
        float f;
        ChatAvatarContainer avatarContainer;
        TLRPC$ChatParticipants tLRPC$ChatParticipants;
        ChatActivity.ThemeDelegate themeDelegate;
        Theme.createProfileResources(context);
        Theme.createChatResources(context, false);
        BaseFragment lastFragment = this.parentLayout.getLastFragment();
        if ((lastFragment instanceof ChatActivity) && (themeDelegate = ((ChatActivity) lastFragment).themeDelegate) != null && themeDelegate.getCurrentTheme() != null) {
            this.resourcesProvider = lastFragment.getResourceProvider();
        }
        this.searchTransitionOffset = 0;
        this.searchTransitionProgress = 1.0f;
        this.searchMode = false;
        this.hasOwnBackground = true;
        this.extraHeight = AndroidUtilities.dp(88.0f);
        this.actionBar.setActionBarMenuOnItemClick(new AnonymousClass5());
        SharedMediaLayout sharedMediaLayout = this.sharedMediaLayout;
        if (sharedMediaLayout != null) {
            sharedMediaLayout.onDestroy();
        }
        long j = this.dialogId;
        if (j == 0) {
            j = this.userId;
            if (j == 0) {
                j = -this.chatId;
            }
        }
        final long j2 = j;
        this.fragmentView = new AnonymousClass6(context);
        TLRPC$ChatFull tLRPC$ChatFull = this.chatInfo;
        ArrayList<Integer> arrayList = (tLRPC$ChatFull == null || (tLRPC$ChatParticipants = tLRPC$ChatFull.participants) == null || tLRPC$ChatParticipants.participants.size() <= 5) ? null : this.sortedUsers;
        SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader = this.sharedMediaPreloader;
        TLRPC$UserFull tLRPC$UserFull = this.userInfo;
        SharedMediaLayout sharedMediaLayout2 = new SharedMediaLayout(context, j2, sharedMediaPreloader, tLRPC$UserFull != null ? tLRPC$UserFull.common_chats_count : 0, this.sortedUsers, this.chatInfo, arrayList != null, this, this, 1, this.resourcesProvider) { // from class: org.telegram.ui.ProfileActivity.7
            @Override // org.telegram.ui.Components.SharedMediaLayout
            protected void onSelectedTabChanged() {
                ProfileActivity.this.updateSelectedMediaTabText();
            }

            @Override // org.telegram.ui.Components.SharedMediaLayout
            protected boolean canShowSearchItem() {
                return ProfileActivity.this.mediaHeaderVisible;
            }

            @Override // org.telegram.ui.Components.SharedMediaLayout
            protected void onSearchStateChanged(boolean z2) {
                if (SharedConfig.smoothKeyboard) {
                    AndroidUtilities.removeAdjustResize(ProfileActivity.this.getParentActivity(), ((BaseFragment) ProfileActivity.this).classGuid);
                }
                ProfileActivity.this.listView.stopScroll();
                ProfileActivity.this.avatarContainer2.setPivotY(ProfileActivity.this.avatarContainer.getPivotY() + (ProfileActivity.this.avatarContainer.getMeasuredHeight() / 2.0f));
                ProfileActivity.this.avatarContainer2.setPivotX(ProfileActivity.this.avatarContainer2.getMeasuredWidth() / 2.0f);
                AndroidUtilities.updateViewVisibilityAnimated(ProfileActivity.this.avatarContainer2, !z2, 0.95f, true);
                int i2 = 4;
                ProfileActivity.this.callItem.setVisibility((z2 || !ProfileActivity.this.callItemVisible) ? 8 : 4);
                ProfileActivity.this.videoCallItem.setVisibility((z2 || !ProfileActivity.this.videoCallItemVisible) ? 8 : 4);
                ProfileActivity.this.editItem.setVisibility((z2 || !ProfileActivity.this.editItemVisible) ? 8 : 4);
                ProfileActivity.this.otherItem.setVisibility(z2 ? 8 : 4);
                if (ProfileActivity.this.qrItem != null) {
                    ActionBarMenuItem actionBarMenuItem = ProfileActivity.this.qrItem;
                    if (z2) {
                        i2 = 8;
                    }
                    actionBarMenuItem.setVisibility(i2);
                }
            }

            @Override // org.telegram.ui.Components.SharedMediaLayout
            protected boolean onMemberClick(TLRPC$ChatParticipant tLRPC$ChatParticipant, boolean z2) {
                return ProfileActivity.this.onMemberClick(tLRPC$ChatParticipant, z2);
            }

            @Override // org.telegram.ui.Components.SharedMediaLayout
            protected void drawBackgroundWithBlur(Canvas canvas, float f2, Rect rect, Paint paint) {
                ProfileActivity.this.contentView.drawBlurRect(canvas, ProfileActivity.this.listView.getY() + getY() + f2, rect, paint, true);
            }

            @Override // org.telegram.ui.Components.SharedMediaLayout
            protected void invalidateBlur() {
                ProfileActivity.this.contentView.invalidateBlur();
            }
        };
        this.sharedMediaLayout = sharedMediaLayout2;
        sharedMediaLayout2.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
        ActionBarMenu createMenu = this.actionBar.createMenu();
        if (this.userId == getUserConfig().clientUserId) {
            ActionBarMenuItem addItem = createMenu.addItem(37, R.drawable.msg_qr_mini, getResourceProvider());
            this.qrItem = addItem;
            addItem.setContentDescription(LocaleController.getString("GetQRCode", R.string.GetQRCode));
            updateQrItemVisibility(false);
            if (ContactsController.getInstance(this.currentAccount).getPrivacyRules(7) == null) {
                ContactsController.getInstance(this.currentAccount).loadPrivacySettings();
            }
        }
        if (this.imageUpdater != null) {
            ActionBarMenuItem actionBarMenuItemSearchListener = createMenu.addItem(32, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() { // from class: org.telegram.ui.ProfileActivity.8
                @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
                public Animator getCustomToggleTransition() {
                    ProfileActivity profileActivity = ProfileActivity.this;
                    profileActivity.searchMode = !profileActivity.searchMode;
                    if (!ProfileActivity.this.searchMode) {
                        ProfileActivity.this.searchItem.clearFocusOnSearchView();
                    }
                    if (ProfileActivity.this.searchMode) {
                        ProfileActivity.this.searchItem.getSearchField().setText("");
                    }
                    ProfileActivity profileActivity2 = ProfileActivity.this;
                    return profileActivity2.searchExpandTransition(profileActivity2.searchMode);
                }

                @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
                public void onTextChanged(EditText editText) {
                    ProfileActivity.this.searchAdapter.search(editText.getText().toString().toLowerCase());
                }
            });
            this.searchItem = actionBarMenuItemSearchListener;
            int i2 = R.string.SearchInSettings;
            actionBarMenuItemSearchListener.setContentDescription(LocaleController.getString("SearchInSettings", i2));
            this.searchItem.setSearchFieldHint(LocaleController.getString("SearchInSettings", i2));
            this.sharedMediaLayout.getSearchItem().setVisibility(8);
            if (this.expandPhoto) {
                this.searchItem.setVisibility(8);
            }
        }
        ActionBarMenuItem addItem2 = createMenu.addItem(16, R.drawable.profile_video);
        this.videoCallItem = addItem2;
        addItem2.setContentDescription(LocaleController.getString("VideoCall", R.string.VideoCall));
        if (this.chatId != 0) {
            this.callItem = createMenu.addItem(15, R.drawable.msg_voicechat2);
            if (ChatObject.isChannelOrGiga(this.currentChat)) {
                this.callItem.setContentDescription(LocaleController.getString("VoipChannelVoiceChat", R.string.VoipChannelVoiceChat));
            } else {
                this.callItem.setContentDescription(LocaleController.getString("VoipGroupVoiceChat", R.string.VoipGroupVoiceChat));
            }
        } else {
            ActionBarMenuItem addItem3 = createMenu.addItem(15, R.drawable.ic_call);
            this.callItem = addItem3;
            addItem3.setContentDescription(LocaleController.getString("Call", R.string.Call));
        }
        ActionBarMenuItem addItem4 = createMenu.addItem(12, R.drawable.group_edit_profile);
        this.editItem = addItem4;
        addItem4.setContentDescription(LocaleController.getString("Edit", R.string.Edit));
        this.otherItem = createMenu.addItem(10, R.drawable.ic_ab_other, this.resourcesProvider);
        ImageView imageView = new ImageView(context);
        this.ttlIconView = imageView;
        imageView.setColorFilter(new PorterDuffColorFilter(getThemedColor("actionBarDefaultIcon"), PorterDuff.Mode.MULTIPLY));
        AndroidUtilities.updateViewVisibilityAnimated(this.ttlIconView, false, 0.8f, false);
        this.ttlIconView.setImageResource(R.drawable.msg_mini_autodelete_timer);
        this.otherItem.addView(this.ttlIconView, LayoutHelper.createFrame(12, 12.0f, 19, 8.0f, 2.0f, 0.0f, 0.0f));
        this.otherItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
        if (this.listView == null || this.imageUpdater == null) {
            i = -1;
            obj = null;
        } else {
            int findFirstVisibleItemPosition = this.layoutManager.findFirstVisibleItemPosition();
            View findViewByPosition = this.layoutManager.findViewByPosition(findFirstVisibleItemPosition);
            if (findViewByPosition != null) {
                findViewByPosition.getTop();
                this.listView.getPaddingTop();
            } else {
                findFirstVisibleItemPosition = -1;
            }
            i = findFirstVisibleItemPosition;
            obj = this.writeButton.getTag();
        }
        createActionBarMenu(false);
        this.listAdapter = new ListAdapter(context);
        this.searchAdapter = new SearchAdapter(context);
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        this.avatarDrawable = avatarDrawable;
        avatarDrawable.setProfile(true);
        this.fragmentView.setWillNotDraw(false);
        View view = this.fragmentView;
        NestedFrameLayout nestedFrameLayout = (NestedFrameLayout) view;
        this.contentView = nestedFrameLayout;
        nestedFrameLayout.needBlur = true;
        FrameLayout frameLayout = (FrameLayout) view;
        RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.ProfileActivity.9
            private VelocityTracker velocityTracker;

            @Override // org.telegram.ui.Components.RecyclerListView, android.view.View
            public boolean hasOverlappingRendering() {
                return false;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // androidx.recyclerview.widget.RecyclerView
            public void requestChildOnScreen(View view2, View view3) {
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // org.telegram.ui.Components.RecyclerListView
            public boolean canHighlightChildAt(View view2, float f2, float f3) {
                return !(view2 instanceof AboutLinkCell);
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // org.telegram.ui.Components.RecyclerListView
            public boolean allowSelectChildAtPosition(View view2) {
                return view2 != ProfileActivity.this.sharedMediaLayout;
            }

            @Override // android.view.View
            public void invalidate() {
                super.invalidate();
                if (((BaseFragment) ProfileActivity.this).fragmentView != null) {
                    ((BaseFragment) ProfileActivity.this).fragmentView.invalidate();
                }
            }

            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
            public boolean onTouchEvent(MotionEvent motionEvent) {
                VelocityTracker velocityTracker;
                View findViewByPosition2;
                int action = motionEvent.getAction();
                if (action == 0) {
                    VelocityTracker velocityTracker2 = this.velocityTracker;
                    if (velocityTracker2 == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    } else {
                        velocityTracker2.clear();
                    }
                    this.velocityTracker.addMovement(motionEvent);
                } else if (action == 2) {
                    VelocityTracker velocityTracker3 = this.velocityTracker;
                    if (velocityTracker3 != null) {
                        velocityTracker3.addMovement(motionEvent);
                        this.velocityTracker.computeCurrentVelocity(1000);
                        ProfileActivity.this.listViewVelocityY = this.velocityTracker.getYVelocity(motionEvent.getPointerId(motionEvent.getActionIndex()));
                    }
                } else if ((action == 1 || action == 3) && (velocityTracker = this.velocityTracker) != null) {
                    velocityTracker.recycle();
                    this.velocityTracker = null;
                }
                boolean onTouchEvent = super.onTouchEvent(motionEvent);
                if ((action == 1 || action == 3) && ProfileActivity.this.allowPullingDown && (findViewByPosition2 = ProfileActivity.this.layoutManager.findViewByPosition(0)) != null) {
                    if (ProfileActivity.this.isPulledDown) {
                        ProfileActivity.this.listView.smoothScrollBy(0, (findViewByPosition2.getTop() - ProfileActivity.this.listView.getMeasuredWidth()) + ActionBar.getCurrentActionBarHeight() + (((BaseFragment) ProfileActivity.this).actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0), CubicBezierInterpolator.EASE_OUT_QUINT);
                    } else {
                        ProfileActivity.this.listView.smoothScrollBy(0, findViewByPosition2.getTop() - AndroidUtilities.dp(88.0f), CubicBezierInterpolator.EASE_OUT_QUINT);
                    }
                }
                return onTouchEvent;
            }

            @Override // androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
            public boolean drawChild(Canvas canvas, View view2, long j3) {
                if (getItemAnimator().isRunning() && view2.getBackground() == null && view2.getTranslationY() != 0.0f) {
                    boolean z2 = ProfileActivity.this.listView.getChildAdapterPosition(view2) == ProfileActivity.this.sharedMediaRow && view2.getAlpha() != 1.0f;
                    if (z2) {
                        ProfileActivity.this.whitePaint.setAlpha((int) (ProfileActivity.this.listView.getAlpha() * 255.0f * view2.getAlpha()));
                    }
                    canvas.drawRect(ProfileActivity.this.listView.getX(), view2.getY(), ProfileActivity.this.listView.getX() + ProfileActivity.this.listView.getMeasuredWidth(), view2.getY() + view2.getHeight(), ProfileActivity.this.whitePaint);
                    if (z2) {
                        ProfileActivity.this.whitePaint.setAlpha((int) (ProfileActivity.this.listView.getAlpha() * 255.0f));
                    }
                }
                return super.drawChild(canvas, view2, j3);
            }
        };
        this.listView = recyclerListView;
        recyclerListView.setVerticalScrollBarEnabled(false);
        AnonymousClass10 anonymousClass10 = new AnonymousClass10();
        this.listView.setItemAnimator(anonymousClass10);
        anonymousClass10.setSupportsChangeAnimations(false);
        anonymousClass10.setDelayAnimations(false);
        this.listView.setClipToPadding(false);
        this.listView.setHideIfEmpty(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context) { // from class: org.telegram.ui.ProfileActivity.11
            @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public boolean supportsPredictiveItemAnimations() {
                return ProfileActivity.this.imageUpdater != null;
            }

            @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public int scrollVerticallyBy(int i3, RecyclerView.Recycler recycler, RecyclerView.State state) {
                boolean z2 = false;
                View findViewByPosition2 = ProfileActivity.this.layoutManager.findViewByPosition(0);
                if (findViewByPosition2 != null && !ProfileActivity.this.openingAvatar) {
                    int top = findViewByPosition2.getTop() - AndroidUtilities.dp(88.0f);
                    if (ProfileActivity.this.allowPullingDown || top <= i3) {
                        if (ProfileActivity.this.allowPullingDown) {
                            if (i3 >= top) {
                                ProfileActivity.this.allowPullingDown = false;
                            } else if (ProfileActivity.this.listView.getScrollState() == 1 && !ProfileActivity.this.isPulledDown) {
                                i3 /= 2;
                            }
                        }
                    } else if (ProfileActivity.this.avatarsViewPager.hasImages() && ProfileActivity.this.avatarImage.getImageReceiver().hasNotThumb() && !AndroidUtilities.isAccessibilityScreenReaderEnabled() && !ProfileActivity.this.isInLandscapeMode && !AndroidUtilities.isTablet()) {
                        ProfileActivity profileActivity = ProfileActivity.this;
                        if (profileActivity.avatarBig == null) {
                            z2 = true;
                        }
                        profileActivity.allowPullingDown = z2;
                    }
                    i3 = top;
                }
                return super.scrollVerticallyBy(i3, recycler, state);
            }
        };
        this.layoutManager = linearLayoutManager;
        linearLayoutManager.setOrientation(1);
        LinearLayoutManager linearLayoutManager2 = this.layoutManager;
        linearLayoutManager2.mIgnoreTopPadding = false;
        this.listView.setLayoutManager(linearLayoutManager2);
        this.listView.setGlowColor(0);
        this.listView.setAdapter(this.listAdapter);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListenerExtended() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda45
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
            public /* synthetic */ boolean hasDoubleTap(View view2, int i3) {
                return RecyclerListView.OnItemClickListenerExtended.CC.$default$hasDoubleTap(this, view2, i3);
            }

            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
            public /* synthetic */ void onDoubleTap(View view2, int i3, float f2, float f3) {
                RecyclerListView.OnItemClickListenerExtended.CC.$default$onDoubleTap(this, view2, i3, f2, f3);
            }

            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
            public final void onItemClick(View view2, int i3, float f2, float f3) {
                ProfileActivity.this.lambda$createView$7(j2, context, view2, i3, f2, f3);
            }
        });
        this.listView.setOnItemLongClickListener(new AnonymousClass15(context));
        if (this.searchItem != null) {
            RecyclerListView recyclerListView2 = new RecyclerListView(context);
            this.searchListView = recyclerListView2;
            recyclerListView2.setVerticalScrollBarEnabled(false);
            this.searchListView.setLayoutManager(new LinearLayoutManager(context, 1, false));
            this.searchListView.setGlowColor(getThemedColor("avatar_backgroundActionBarBlue"));
            this.searchListView.setAdapter(this.searchAdapter);
            resourcesProvider = null;
            this.searchListView.setItemAnimator(null);
            this.searchListView.setVisibility(8);
            this.searchListView.setLayoutAnimation(null);
            this.searchListView.setBackgroundColor(getThemedColor("windowBackgroundWhite"));
            frameLayout.addView(this.searchListView, LayoutHelper.createFrame(-1, -1, 51));
            this.searchListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda44
                @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
                public final void onItemClick(View view2, int i3) {
                    ProfileActivity.this.lambda$createView$8(view2, i3);
                }
            });
            this.searchListView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda46
                @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
                public final boolean onItemClick(View view2, int i3) {
                    boolean lambda$createView$10;
                    lambda$createView$10 = ProfileActivity.this.lambda$createView$10(view2, i3);
                    return lambda$createView$10;
                }
            });
            this.searchListView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.ProfileActivity.16
                @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                public void onScrollStateChanged(RecyclerView recyclerView, int i3) {
                    if (i3 == 1) {
                        AndroidUtilities.hideKeyboard(ProfileActivity.this.getParentActivity().getCurrentFocus());
                    }
                }
            });
            this.searchListView.setAnimateEmptyView(true, 1);
            StickerEmptyView stickerEmptyView = new StickerEmptyView(context, null, 1);
            this.emptyView = stickerEmptyView;
            stickerEmptyView.setAnimateLayoutChange(true);
            this.emptyView.subtitle.setVisibility(8);
            this.emptyView.setVisibility(8);
            frameLayout.addView(this.emptyView);
            this.searchAdapter.loadFaqWebPage();
        } else {
            resourcesProvider = null;
        }
        if (this.banFromGroup != 0) {
            final TLRPC$Chat chat = getMessagesController().getChat(Long.valueOf(this.banFromGroup));
            if (this.currentChannelParticipant == null) {
                TLRPC$TL_channels_getParticipant tLRPC$TL_channels_getParticipant = new TLRPC$TL_channels_getParticipant();
                tLRPC$TL_channels_getParticipant.channel = MessagesController.getInputChannel(chat);
                tLRPC$TL_channels_getParticipant.participant = getMessagesController().getInputPeer(this.userId);
                getConnectionsManager().sendRequest(tLRPC$TL_channels_getParticipant, new RequestDelegate() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda39
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        ProfileActivity.this.lambda$createView$12(tLObject, tLRPC$TL_error);
                    }
                });
            }
            FrameLayout frameLayout2 = new FrameLayout(this, context) { // from class: org.telegram.ui.ProfileActivity.17
                @Override // android.view.View
                protected void onDraw(Canvas canvas) {
                    int intrinsicHeight = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                    Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), intrinsicHeight);
                    Theme.chat_composeShadowDrawable.draw(canvas);
                    canvas.drawRect(0.0f, intrinsicHeight, getMeasuredWidth(), getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
                }
            };
            frameLayout2.setWillNotDraw(false);
            frameLayout.addView(frameLayout2, LayoutHelper.createFrame(-1, 51, 83));
            frameLayout2.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda14
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    ProfileActivity.this.lambda$createView$13(chat, view2);
                }
            });
            TextView textView = new TextView(context);
            textView.setTextColor(getThemedColor("windowBackgroundWhiteRedText"));
            textView.setTextSize(1, 15.0f);
            textView.setGravity(17);
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView.setText(LocaleController.getString("BanFromTheGroup", R.string.BanFromTheGroup));
            frameLayout2.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 1.0f, 0.0f, 0.0f));
            this.listView.setPadding(0, AndroidUtilities.dp(88.0f), 0, AndroidUtilities.dp(48.0f));
            this.listView.setBottomGlowOffset(AndroidUtilities.dp(48.0f));
        } else {
            this.listView.setPadding(0, AndroidUtilities.dp(88.0f), 0, 0);
        }
        TopView topView = new TopView(context);
        this.topView = topView;
        topView.setBackgroundColor(getThemedColor("avatar_backgroundActionBarBlue"));
        frameLayout.addView(this.topView);
        this.contentView.blurBehindViews.add(this.topView);
        DrawerProfileCell.AnimatedStatusView animatedStatusView = new DrawerProfileCell.AnimatedStatusView(context, 20, 60);
        this.animatedStatusView = animatedStatusView;
        animatedStatusView.setPivotX(AndroidUtilities.dp(30.0f));
        this.animatedStatusView.setPivotY(AndroidUtilities.dp(30.0f));
        this.avatarContainer = new FrameLayout(context);
        FrameLayout frameLayout3 = new FrameLayout(context) { // from class: org.telegram.ui.ProfileActivity.19
            @Override // android.view.ViewGroup, android.view.View
            protected void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                if (ProfileActivity.this.transitionOnlineText != null) {
                    canvas.save();
                    canvas.translate(ProfileActivity.this.onlineTextView[0].getX(), ProfileActivity.this.onlineTextView[0].getY());
                    canvas.saveLayerAlpha(0.0f, 0.0f, ProfileActivity.this.transitionOnlineText.getMeasuredWidth(), ProfileActivity.this.transitionOnlineText.getMeasuredHeight(), (int) ((1.0f - ProfileActivity.this.animationProgress) * 255.0f), 31);
                    ProfileActivity.this.transitionOnlineText.draw(canvas);
                    canvas.restore();
                    canvas.restore();
                    invalidate();
                }
            }
        };
        this.avatarContainer2 = frameLayout3;
        AndroidUtilities.updateViewVisibilityAnimated(frameLayout3, true, 1.0f, false);
        frameLayout.addView(this.avatarContainer2, LayoutHelper.createFrame(-1, -1.0f, 8388611, 0.0f, 0.0f, 0.0f, 0.0f));
        this.avatarContainer.setPivotX(0.0f);
        this.avatarContainer.setPivotY(0.0f);
        this.avatarContainer2.addView(this.avatarContainer, LayoutHelper.createFrame(42, 42.0f, 51, 64.0f, 0.0f, 0.0f, 0.0f));
        AvatarImageView avatarImageView = new AvatarImageView(this, context) { // from class: org.telegram.ui.ProfileActivity.20
            @Override // android.view.View
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                if (getImageReceiver().hasNotThumb()) {
                    accessibilityNodeInfo.setText(LocaleController.getString("AccDescrProfilePicture", R.string.AccDescrProfilePicture));
                    if (Build.VERSION.SDK_INT < 21) {
                        return;
                    }
                    accessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, LocaleController.getString("Open", R.string.Open)));
                    accessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(32, LocaleController.getString("AccDescrOpenInPhotoViewer", R.string.AccDescrOpenInPhotoViewer)));
                    return;
                }
                accessibilityNodeInfo.setVisibleToUser(false);
            }

            @Override // android.view.View
            protected void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                AnimatedEmojiDrawable animatedEmojiDrawable = this.animatedEmojiDrawable;
                if (animatedEmojiDrawable != null) {
                    animatedEmojiDrawable.getImageReceiver().startAnimation();
                }
            }
        };
        this.avatarImage = avatarImageView;
        avatarImageView.getImageReceiver().setAllowDecodeSingleFrame(true);
        this.avatarImage.setRoundRadius(getSmallAvatarRoundRadius());
        this.avatarImage.setPivotX(0.0f);
        this.avatarImage.setPivotY(0.0f);
        this.avatarContainer.addView(this.avatarImage, LayoutHelper.createFrame(-1, -1.0f));
        this.avatarImage.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda10
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                ProfileActivity.this.lambda$createView$15(view2);
            }
        });
        this.avatarImage.setOnLongClickListener(new View.OnLongClickListener() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda17
            @Override // android.view.View.OnLongClickListener
            public final boolean onLongClick(View view2) {
                boolean lambda$createView$16;
                lambda$createView$16 = ProfileActivity.this.lambda$createView$16(view2);
                return lambda$createView$16;
            }
        });
        RadialProgressView radialProgressView = new RadialProgressView(context) { // from class: org.telegram.ui.ProfileActivity.21
            private Paint paint;

            {
                Paint paint = new Paint(1);
                this.paint = paint;
                paint.setColor(NUM);
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // org.telegram.ui.Components.RadialProgressView, android.view.View
            public void onDraw(Canvas canvas) {
                if (ProfileActivity.this.avatarImage != null && ProfileActivity.this.avatarImage.getImageReceiver().hasNotThumb()) {
                    this.paint.setAlpha((int) (ProfileActivity.this.avatarImage.getImageReceiver().getCurrentAlpha() * 85.0f));
                    canvas.drawCircle(getMeasuredWidth() / 2.0f, getMeasuredHeight() / 2.0f, getMeasuredWidth() / 2.0f, this.paint);
                }
                super.onDraw(canvas);
            }
        };
        this.avatarProgressView = radialProgressView;
        radialProgressView.setSize(AndroidUtilities.dp(26.0f));
        this.avatarProgressView.setProgressColor(-1);
        this.avatarProgressView.setNoProgress(false);
        this.avatarContainer.addView(this.avatarProgressView, LayoutHelper.createFrame(-1, -1.0f));
        ImageView imageView2 = new ImageView(context);
        this.timeItem = imageView2;
        imageView2.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f));
        this.timeItem.setScaleType(ImageView.ScaleType.CENTER);
        this.timeItem.setAlpha(0.0f);
        ImageView imageView3 = this.timeItem;
        TimerDrawable timerDrawable = new TimerDrawable(context, resourcesProvider);
        this.timerDrawable = timerDrawable;
        imageView3.setImageDrawable(timerDrawable);
        this.timeItem.setTranslationY(-1.0f);
        frameLayout.addView(this.timeItem, LayoutHelper.createFrame(34, 34, 51));
        showAvatarProgress(false, false);
        ProfileGalleryView profileGalleryView = this.avatarsViewPager;
        if (profileGalleryView != null) {
            profileGalleryView.onDestroy();
        }
        this.overlaysView = new OverlaysView(context);
        long j3 = this.userId;
        if (j3 == 0) {
            j3 = -this.chatId;
        }
        ProfileGalleryView profileGalleryView2 = new ProfileGalleryView(context, j3, this.actionBar, this.listView, this.avatarImage, getClassGuid(), this.overlaysView);
        this.avatarsViewPager = profileGalleryView2;
        if (!this.isTopic) {
            profileGalleryView2.setChatInfo(this.chatInfo);
        }
        this.avatarContainer2.addView(this.avatarsViewPager);
        this.avatarContainer2.addView(this.overlaysView);
        this.avatarImage.setAvatarsViewPager(this.avatarsViewPager);
        PagerIndicatorView pagerIndicatorView = new PagerIndicatorView(context);
        this.avatarsViewPagerIndicatorView = pagerIndicatorView;
        this.avatarContainer2.addView(pagerIndicatorView, LayoutHelper.createFrame(-1, -1.0f));
        frameLayout.addView(this.actionBar);
        float f2 = 54 + ((!this.callItemVisible || this.userId == 0) ? 0 : 54);
        INavigationLayout iNavigationLayout = this.parentLayout;
        if (iNavigationLayout == null || !(iNavigationLayout.getLastFragment() instanceof ChatActivity) || (avatarContainer = ((ChatActivity) this.parentLayout.getLastFragment()).getAvatarContainer()) == null) {
            z = false;
        } else {
            z = avatarContainer.getTitleTextView().getPaddingRight() != 0;
            if (avatarContainer.getLayoutParams() != null && avatarContainer.getTitleTextView() != null) {
                f2 = (((ViewGroup.MarginLayoutParams) avatarContainer.getLayoutParams()).rightMargin + (avatarContainer.getWidth() - avatarContainer.getTitleTextView().getRight())) / AndroidUtilities.density;
            }
        }
        int i3 = 0;
        while (true) {
            SimpleTextView[] simpleTextViewArr = this.nameTextView;
            if (i3 >= simpleTextViewArr.length) {
                break;
            }
            if (this.playProfileAnimation != 0 || i3 != 0) {
                simpleTextViewArr[i3] = new SimpleTextView(context) { // from class: org.telegram.ui.ProfileActivity.22
                    @Override // org.telegram.ui.ActionBar.SimpleTextView, android.view.View
                    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                        if (!isFocusable() || ProfileActivity.this.nameTextViewRightDrawableContentDescription == null) {
                            return;
                        }
                        accessibilityNodeInfo.setText(((Object) getText()) + ", " + ProfileActivity.this.nameTextViewRightDrawableContentDescription);
                    }
                };
                if (i3 == 1) {
                    this.nameTextView[i3].setTextColor(getThemedColor("profile_title"));
                } else {
                    this.nameTextView[i3].setTextColor(getThemedColor("actionBarDefaultTitle"));
                }
                this.nameTextView[i3].setPadding(0, AndroidUtilities.dp(6.0f), 0, AndroidUtilities.dp(i3 == 0 ? 12.0f : 4.0f));
                this.nameTextView[i3].setTextSize(18);
                this.nameTextView[i3].setGravity(3);
                this.nameTextView[i3].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.nameTextView[i3].setLeftDrawableTopPadding(-AndroidUtilities.dp(1.3f));
                this.nameTextView[i3].setPivotX(0.0f);
                this.nameTextView[i3].setPivotY(0.0f);
                this.nameTextView[i3].setAlpha(i3 == 0 ? 0.0f : 1.0f);
                if (i3 == 1) {
                    this.nameTextView[i3].setScrollNonFitText(true);
                    this.nameTextView[i3].setImportantForAccessibility(2);
                }
                this.nameTextView[i3].setFocusable(i3 == 0);
                this.nameTextView[i3].setEllipsizeByGradient(true);
                this.nameTextView[i3].setRightDrawableOutside(i3 == 0);
                FrameLayout frameLayout4 = this.avatarContainer2;
                SimpleTextView simpleTextView = this.nameTextView[i3];
                if (i3 == 0) {
                    f = f2 - (z ? 10 : 0);
                } else {
                    f = 0.0f;
                }
                frameLayout4.addView(simpleTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, -6.0f, f, 0.0f));
            }
            i3++;
        }
        int i4 = 0;
        while (true) {
            SimpleTextView[] simpleTextViewArr2 = this.onlineTextView;
            if (i4 >= simpleTextViewArr2.length) {
                break;
            }
            simpleTextViewArr2[i4] = new SimpleTextView(context);
            this.onlineTextView[i4].setEllipsizeByGradient(true);
            this.onlineTextView[i4].setTextColor(getThemedColor("avatar_subtitleInProfileBlue"));
            this.onlineTextView[i4].setTextSize(14);
            this.onlineTextView[i4].setGravity(3);
            this.onlineTextView[i4].setAlpha((i4 == 0 || i4 == 2) ? 0.0f : 1.0f);
            if (i4 > 0) {
                this.onlineTextView[i4].setImportantForAccessibility(2);
            }
            this.onlineTextView[i4].setFocusable(i4 == 0);
            this.avatarContainer2.addView(this.onlineTextView[i4], LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, i4 == 0 ? f2 - 12.0f : 8.0f, 0.0f));
            i4++;
        }
        this.avatarContainer2.addView(this.animatedStatusView);
        AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher = new AudioPlayerAlert.ClippingTextViewSwitcher(context) { // from class: org.telegram.ui.ProfileActivity.23
            @Override // org.telegram.ui.Components.AudioPlayerAlert.ClippingTextViewSwitcher
            protected TextView createTextView() {
                TextView textView2 = new TextView(context);
                textView2.setTextColor(ProfileActivity.this.getThemedColor("player_actionBarSubtitle"));
                textView2.setTextSize(1, 14.0f);
                textView2.setSingleLine(true);
                textView2.setEllipsize(TextUtils.TruncateAt.END);
                textView2.setGravity(3);
                return textView2;
            }
        };
        this.mediaCounterTextView = clippingTextViewSwitcher;
        clippingTextViewSwitcher.setAlpha(0.0f);
        this.avatarContainer2.addView(this.mediaCounterTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, 8.0f, 0.0f));
        updateProfileData(true);
        this.writeButton = new RLottieImageView(context);
        Drawable mutate = context.getResources().getDrawable(R.drawable.floating_shadow_profile).mutate();
        mutate.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
        CombinedDrawable combinedDrawable = new CombinedDrawable(mutate, Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), getThemedColor("profile_actionBackground"), getThemedColor("profile_actionPressedBackground")), 0, 0);
        combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
        this.writeButton.setBackground(combinedDrawable);
        if (this.userId != 0) {
            if (this.imageUpdater != null) {
                int i5 = R.raw.camera_outline;
                this.cameraDrawable = new RLottieDrawable(i5, String.valueOf(i5), AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f), false, null);
                this.cellCameraDrawable = new RLottieDrawable(i5, i5 + "_cell", AndroidUtilities.dp(42.0f), AndroidUtilities.dp(42.0f), false, null);
                this.writeButton.setAnimation(this.cameraDrawable);
                this.writeButton.setContentDescription(LocaleController.getString("AccDescrChangeProfilePicture", R.string.AccDescrChangeProfilePicture));
                this.writeButton.setPadding(AndroidUtilities.dp(2.0f), 0, 0, AndroidUtilities.dp(2.0f));
            } else {
                this.writeButton.setImageResource(R.drawable.profile_newmsg);
                this.writeButton.setContentDescription(LocaleController.getString("AccDescrOpenChat", R.string.AccDescrOpenChat));
            }
        } else {
            this.writeButton.setImageResource(R.drawable.profile_discuss);
            this.writeButton.setContentDescription(LocaleController.getString("ViewDiscussion", R.string.ViewDiscussion));
        }
        this.writeButton.setColorFilter(new PorterDuffColorFilter(getThemedColor("profile_actionIcon"), PorterDuff.Mode.MULTIPLY));
        this.writeButton.setScaleType(ImageView.ScaleType.CENTER);
        frameLayout.addView(this.writeButton, LayoutHelper.createFrame(60, 60.0f, 53, 0.0f, 0.0f, 16.0f, 0.0f));
        this.writeButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda9
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                ProfileActivity.this.lambda$createView$17(view2);
            }
        });
        needLayout(false);
        if (i != -1 && obj != null) {
            this.writeButton.setTag(0);
            this.writeButton.setScaleX(0.2f);
            this.writeButton.setScaleY(0.2f);
            this.writeButton.setAlpha(0.0f);
        }
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.ProfileActivity.24
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int i6) {
                boolean z2 = true;
                if (i6 == 1) {
                    AndroidUtilities.hideKeyboard(ProfileActivity.this.getParentActivity().getCurrentFocus());
                }
                if (ProfileActivity.this.openingAvatar && i6 != 2) {
                    ProfileActivity.this.openingAvatar = false;
                }
                if (ProfileActivity.this.searchItem != null) {
                    ProfileActivity.this.scrolling = i6 != 0;
                    ActionBarMenuItem actionBarMenuItem = ProfileActivity.this.searchItem;
                    if (ProfileActivity.this.scrolling || ProfileActivity.this.isPulledDown) {
                        z2 = false;
                    }
                    actionBarMenuItem.setEnabled(z2);
                }
                ProfileActivity.this.sharedMediaLayout.scrollingByUser = ProfileActivity.this.listView.scrollingByUser;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int i6, int i7) {
                if (ProfileActivity.this.fwdRestrictedHint != null) {
                    ProfileActivity.this.fwdRestrictedHint.hide();
                }
                ProfileActivity.this.checkListViewScroll();
                boolean z2 = false;
                if (ProfileActivity.this.participantsMap != null && !ProfileActivity.this.usersEndReached && ProfileActivity.this.layoutManager.findLastVisibleItemPosition() > ProfileActivity.this.membersEndRow - 8) {
                    ProfileActivity.this.getChannelParticipants(false);
                }
                SharedMediaLayout sharedMediaLayout3 = ProfileActivity.this.sharedMediaLayout;
                if (ProfileActivity.this.sharedMediaLayout.getY() == 0.0f) {
                    z2 = true;
                }
                sharedMediaLayout3.setPinnedToTop(z2);
            }
        });
        UndoView undoView = new UndoView(context, null, false, this.resourcesProvider);
        this.undoView = undoView;
        frameLayout.addView(undoView, LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.expandAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda2
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                ProfileActivity.this.lambda$createView$18(valueAnimator);
            }
        });
        this.expandAnimator.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
        this.expandAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ProfileActivity.25
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                ((BaseFragment) ProfileActivity.this).actionBar.setItemsBackgroundColor(ProfileActivity.this.isPulledDown ? NUM : ProfileActivity.this.getThemedColor("avatar_actionBarSelectorBlue"), false);
                ProfileActivity.this.avatarImage.clearForeground();
                ProfileActivity.this.doNotSetForeground = false;
            }
        });
        updateRowsIds();
        updateSelectedMediaTabText();
        HintView hintView = new HintView(getParentActivity(), 9);
        this.fwdRestrictedHint = hintView;
        hintView.setAlpha(0.0f);
        frameLayout.addView(this.fwdRestrictedHint, LayoutHelper.createFrame(-2, -2.0f, 51, 12.0f, 0.0f, 12.0f, 0.0f));
        this.sharedMediaLayout.setForwardRestrictedHint(this.fwdRestrictedHint);
        PinchToZoomHelper pinchToZoomHelper = new PinchToZoomHelper(Build.VERSION.SDK_INT >= 21 ? (ViewGroup) getParentActivity().getWindow().getDecorView() : frameLayout, frameLayout) { // from class: org.telegram.ui.ProfileActivity.26
            Paint statusBarPaint;

            @Override // org.telegram.ui.PinchToZoomHelper
            protected void invalidateViews() {
                super.invalidateViews();
                ((BaseFragment) ProfileActivity.this).fragmentView.invalidate();
                for (int i6 = 0; i6 < ProfileActivity.this.avatarsViewPager.getChildCount(); i6++) {
                    ProfileActivity.this.avatarsViewPager.getChildAt(i6).invalidate();
                }
                if (ProfileActivity.this.writeButton != null) {
                    ProfileActivity.this.writeButton.invalidate();
                }
            }

            @Override // org.telegram.ui.PinchToZoomHelper
            protected void drawOverlays(Canvas canvas, float f3, float f4, float f5, float f6, float f7) {
                if (f3 > 0.0f) {
                    RectF rectF = AndroidUtilities.rectTmp;
                    rectF.set(0.0f, 0.0f, ProfileActivity.this.avatarsViewPager.getMeasuredWidth(), ProfileActivity.this.avatarsViewPager.getMeasuredHeight() + AndroidUtilities.dp(30.0f));
                    canvas.saveLayerAlpha(rectF, (int) (255.0f * f3), 31);
                    ProfileActivity.this.avatarContainer2.draw(canvas);
                    if (((BaseFragment) ProfileActivity.this).actionBar.getOccupyStatusBar() && !SharedConfig.noStatusBar) {
                        if (this.statusBarPaint == null) {
                            Paint paint = new Paint();
                            this.statusBarPaint = paint;
                            paint.setColor(ColorUtils.setAlphaComponent(-16777216, 51));
                        }
                        canvas.drawRect(((BaseFragment) ProfileActivity.this).actionBar.getX(), ((BaseFragment) ProfileActivity.this).actionBar.getY(), ((BaseFragment) ProfileActivity.this).actionBar.getX() + ((BaseFragment) ProfileActivity.this).actionBar.getMeasuredWidth(), ((BaseFragment) ProfileActivity.this).actionBar.getY() + AndroidUtilities.statusBarHeight, this.statusBarPaint);
                    }
                    canvas.save();
                    canvas.translate(((BaseFragment) ProfileActivity.this).actionBar.getX(), ((BaseFragment) ProfileActivity.this).actionBar.getY());
                    ((BaseFragment) ProfileActivity.this).actionBar.draw(canvas);
                    canvas.restore();
                    if (ProfileActivity.this.writeButton != null && ProfileActivity.this.writeButton.getVisibility() == 0 && ProfileActivity.this.writeButton.getAlpha() > 0.0f) {
                        canvas.save();
                        float f8 = (f3 * 0.5f) + 0.5f;
                        canvas.scale(f8, f8, ProfileActivity.this.writeButton.getX() + (ProfileActivity.this.writeButton.getMeasuredWidth() / 2.0f), ProfileActivity.this.writeButton.getY() + (ProfileActivity.this.writeButton.getMeasuredHeight() / 2.0f));
                        canvas.translate(ProfileActivity.this.writeButton.getX(), ProfileActivity.this.writeButton.getY());
                        ProfileActivity.this.writeButton.draw(canvas);
                        canvas.restore();
                    }
                    canvas.restore();
                }
            }

            @Override // org.telegram.ui.PinchToZoomHelper
            protected boolean zoomEnabled(View view2, ImageReceiver imageReceiver) {
                return super.zoomEnabled(view2, imageReceiver) && ProfileActivity.this.listView.getScrollState() != 1;
            }
        };
        this.pinchToZoomHelper = pinchToZoomHelper;
        pinchToZoomHelper.setCallback(new PinchToZoomHelper.Callback() { // from class: org.telegram.ui.ProfileActivity.27
            @Override // org.telegram.ui.PinchToZoomHelper.Callback
            public /* synthetic */ TextureView getCurrentTextureView() {
                return PinchToZoomHelper.Callback.CC.$default$getCurrentTextureView(this);
            }

            @Override // org.telegram.ui.PinchToZoomHelper.Callback
            public /* synthetic */ void onZoomFinished(MessageObject messageObject) {
                PinchToZoomHelper.Callback.CC.$default$onZoomFinished(this, messageObject);
            }

            @Override // org.telegram.ui.PinchToZoomHelper.Callback
            public void onZoomStarted(MessageObject messageObject) {
                ProfileActivity.this.listView.cancelClickRunnables(true);
                if (ProfileActivity.this.sharedMediaLayout != null && ProfileActivity.this.sharedMediaLayout.getCurrentListView() != null) {
                    ProfileActivity.this.sharedMediaLayout.getCurrentListView().cancelClickRunnables(true);
                }
                Bitmap bitmap = ProfileActivity.this.pinchToZoomHelper.getPhotoImage() == null ? null : ProfileActivity.this.pinchToZoomHelper.getPhotoImage().getBitmap();
                if (bitmap != null) {
                    ProfileActivity.this.topView.setBackgroundColor(ColorUtils.blendARGB(AndroidUtilities.calcBitmapColor(bitmap), ProfileActivity.this.getThemedColor("windowBackgroundWhite"), 0.1f));
                }
            }
        });
        this.avatarsViewPager.setPinchToZoomHelper(this.pinchToZoomHelper);
        this.scrimPaint.setAlpha(0);
        this.actionBarBackgroundPaint.setColor(getThemedColor("listSelectorSDK21"));
        this.contentView.blurBehindViews.add(this.sharedMediaLayout);
        updateTtlIcon();
        return this.fragmentView;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.ProfileActivity$5  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass5 extends ActionBar.ActionBarMenuOnItemClick {
        AnonymousClass5() {
        }

        @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
        public void onItemClick(int i) {
            String format;
            long j;
            if (ProfileActivity.this.getParentActivity() == null) {
                return;
            }
            if (i == -1) {
                ProfileActivity.this.finishFragment();
                return;
            }
            String str = null;
            String str2 = null;
            Integer num = null;
            String str3 = null;
            final boolean z = false;
            if (i == 2) {
                TLRPC$User user = ProfileActivity.this.getMessagesController().getUser(Long.valueOf(ProfileActivity.this.userId));
                if (user == null) {
                    return;
                }
                if (!ProfileActivity.this.isBot || MessagesController.isSupportUser(user)) {
                    if (ProfileActivity.this.userBlocked) {
                        ProfileActivity.this.getMessagesController().unblockPeer(ProfileActivity.this.userId);
                        if (!BulletinFactory.canShowBulletin(ProfileActivity.this)) {
                            return;
                        }
                        BulletinFactory.createBanBulletin(ProfileActivity.this, false).show();
                    } else if (ProfileActivity.this.reportSpam) {
                        ProfileActivity profileActivity = ProfileActivity.this;
                        AlertsCreator.showBlockReportSpamAlert(profileActivity, profileActivity.userId, user, null, ProfileActivity.this.currentEncryptedChat, false, null, new MessagesStorage.IntCallback() { // from class: org.telegram.ui.ProfileActivity$5$$ExternalSyntheticLambda9
                            @Override // org.telegram.messenger.MessagesStorage.IntCallback
                            public final void run(int i2) {
                                ProfileActivity.AnonymousClass5.this.lambda$onItemClick$0(i2);
                            }
                        }, ProfileActivity.this.resourcesProvider);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this.getParentActivity(), ProfileActivity.this.resourcesProvider);
                        builder.setTitle(LocaleController.getString("BlockUser", R.string.BlockUser));
                        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureBlockContact2", R.string.AreYouSureBlockContact2, ContactsController.formatName(user.first_name, user.last_name))));
                        builder.setPositiveButton(LocaleController.getString("BlockContact", R.string.BlockContact), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ProfileActivity$5$$ExternalSyntheticLambda0
                            @Override // android.content.DialogInterface.OnClickListener
                            public final void onClick(DialogInterface dialogInterface, int i2) {
                                ProfileActivity.AnonymousClass5.this.lambda$onItemClick$1(dialogInterface, i2);
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        AlertDialog create = builder.create();
                        ProfileActivity.this.showDialog(create);
                        TextView textView = (TextView) create.getButton(-1);
                        if (textView == null) {
                            return;
                        }
                        textView.setTextColor(ProfileActivity.this.getThemedColor("dialogTextRed2"));
                    }
                } else if (!ProfileActivity.this.userBlocked) {
                    ProfileActivity.this.getMessagesController().blockPeer(ProfileActivity.this.userId);
                } else {
                    ProfileActivity.this.getMessagesController().unblockPeer(ProfileActivity.this.userId);
                    ProfileActivity.this.getSendMessagesHelper().sendMessage("/start", ProfileActivity.this.userId, null, null, null, false, null, null, null, true, 0, null, false);
                    ProfileActivity.this.finishFragment();
                }
            } else if (i == 1) {
                TLRPC$User user2 = ProfileActivity.this.getMessagesController().getUser(Long.valueOf(ProfileActivity.this.userId));
                Bundle bundle = new Bundle();
                bundle.putLong("user_id", user2.id);
                bundle.putBoolean("addContact", true);
                ProfileActivity profileActivity2 = ProfileActivity.this;
                profileActivity2.presentFragment(new ContactAddActivity(bundle, profileActivity2.resourcesProvider));
            } else if (i == 3) {
                Bundle bundle2 = new Bundle();
                bundle2.putBoolean("onlySelect", true);
                bundle2.putInt("dialogsType", 3);
                bundle2.putString("selectAlertString", LocaleController.getString("SendContactToText", R.string.SendContactToText));
                bundle2.putString("selectAlertStringGroup", LocaleController.getString("SendContactToGroupText", R.string.SendContactToGroupText));
                DialogsActivity dialogsActivity = new DialogsActivity(bundle2);
                dialogsActivity.setDelegate(ProfileActivity.this);
                ProfileActivity.this.presentFragment(dialogsActivity);
            } else if (i == 4) {
                Bundle bundle3 = new Bundle();
                bundle3.putLong("user_id", ProfileActivity.this.userId);
                ProfileActivity profileActivity3 = ProfileActivity.this;
                profileActivity3.presentFragment(new ContactAddActivity(bundle3, profileActivity3.resourcesProvider));
            } else if (i == 5) {
                final TLRPC$User user3 = ProfileActivity.this.getMessagesController().getUser(Long.valueOf(ProfileActivity.this.userId));
                if (user3 == null || ProfileActivity.this.getParentActivity() == null) {
                    return;
                }
                AlertDialog.Builder builder2 = new AlertDialog.Builder(ProfileActivity.this.getParentActivity(), ProfileActivity.this.resourcesProvider);
                builder2.setTitle(LocaleController.getString("DeleteContact", R.string.DeleteContact));
                builder2.setMessage(LocaleController.getString("AreYouSureDeleteContact", R.string.AreYouSureDeleteContact));
                builder2.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ProfileActivity$5$$ExternalSyntheticLambda4
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i2) {
                        ProfileActivity.AnonymousClass5.this.lambda$onItemClick$2(user3, dialogInterface, i2);
                    }
                });
                builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                AlertDialog create2 = builder2.create();
                ProfileActivity.this.showDialog(create2);
                TextView textView2 = (TextView) create2.getButton(-1);
                if (textView2 == null) {
                    return;
                }
                textView2.setTextColor(ProfileActivity.this.getThemedColor("dialogTextRed2"));
            } else if (i == 7) {
                ProfileActivity.this.leaveChatPressed();
            } else if (i == 12) {
                if (ProfileActivity.this.isTopic) {
                    new Bundle().putLong("chat_id", ProfileActivity.this.chatId);
                    ProfileActivity.this.presentFragment(TopicCreateFragment.create(ProfileActivity.this.chatId, ProfileActivity.this.topicId));
                    return;
                }
                Bundle bundle4 = new Bundle();
                bundle4.putLong("chat_id", ProfileActivity.this.chatId);
                ChatEditActivity chatEditActivity = new ChatEditActivity(bundle4);
                chatEditActivity.setInfo(ProfileActivity.this.chatInfo);
                ProfileActivity.this.presentFragment(chatEditActivity);
            } else if (i == 9) {
                final TLRPC$User user4 = ProfileActivity.this.getMessagesController().getUser(Long.valueOf(ProfileActivity.this.userId));
                if (user4 == null) {
                    return;
                }
                Bundle bundle5 = new Bundle();
                bundle5.putBoolean("onlySelect", true);
                bundle5.putInt("dialogsType", 2);
                bundle5.putBoolean("resetDelegate", false);
                bundle5.putBoolean("closeFragment", false);
                final DialogsActivity dialogsActivity2 = new DialogsActivity(bundle5);
                dialogsActivity2.setDelegate(new DialogsActivity.DialogsActivityDelegate() { // from class: org.telegram.ui.ProfileActivity$5$$ExternalSyntheticLambda11
                    @Override // org.telegram.ui.DialogsActivity.DialogsActivityDelegate
                    public final void didSelectDialogs(DialogsActivity dialogsActivity3, ArrayList arrayList, CharSequence charSequence, boolean z2) {
                        ProfileActivity.AnonymousClass5.this.lambda$onItemClick$6(user4, dialogsActivity2, dialogsActivity3, arrayList, charSequence, z2);
                    }
                });
                ProfileActivity.this.presentFragment(dialogsActivity2);
            } else if (i == 10) {
                try {
                    if (ProfileActivity.this.userId != 0) {
                        TLRPC$User user5 = ProfileActivity.this.getMessagesController().getUser(Long.valueOf(ProfileActivity.this.userId));
                        if (user5 == null) {
                            return;
                        }
                        if (ProfileActivity.this.botInfo != null && ProfileActivity.this.userInfo != null && !TextUtils.isEmpty(ProfileActivity.this.userInfo.about)) {
                            format = String.format("%s https://" + ProfileActivity.this.getMessagesController().linkPrefix + "/%s", ProfileActivity.this.userInfo.about, user5.username);
                        } else {
                            format = String.format("https://" + ProfileActivity.this.getMessagesController().linkPrefix + "/%s", user5.username);
                        }
                        str = format;
                    } else if (ProfileActivity.this.chatId != 0) {
                        TLRPC$Chat chat = ProfileActivity.this.getMessagesController().getChat(Long.valueOf(ProfileActivity.this.chatId));
                        if (chat == null) {
                            return;
                        }
                        if (ProfileActivity.this.chatInfo != null && !TextUtils.isEmpty(ProfileActivity.this.chatInfo.about)) {
                            str = String.format("%s\nhttps://" + ProfileActivity.this.getMessagesController().linkPrefix + "/%s", ProfileActivity.this.chatInfo.about, chat.username);
                        } else {
                            str = String.format("https://" + ProfileActivity.this.getMessagesController().linkPrefix + "/%s", chat.username);
                        }
                    }
                    if (TextUtils.isEmpty(str)) {
                        return;
                    }
                    Intent intent = new Intent("android.intent.action.SEND");
                    intent.setType("text/plain");
                    intent.putExtra("android.intent.extra.TEXT", str);
                    ProfileActivity.this.startActivityForResult(Intent.createChooser(intent, LocaleController.getString("BotShare", R.string.BotShare)), 500);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            } else if (i == 14) {
                try {
                    if (ProfileActivity.this.currentEncryptedChat != null) {
                        j = DialogObject.makeEncryptedDialogId(ProfileActivity.this.currentEncryptedChat.id);
                    } else if (ProfileActivity.this.userId != 0) {
                        j = ProfileActivity.this.userId;
                    } else if (ProfileActivity.this.chatId == 0) {
                        return;
                    } else {
                        j = -ProfileActivity.this.chatId;
                    }
                    ProfileActivity.this.getMediaDataController().installShortcut(j);
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
            } else if (i == 15 || i == 16) {
                if (ProfileActivity.this.userId != 0) {
                    TLRPC$User user6 = ProfileActivity.this.getMessagesController().getUser(Long.valueOf(ProfileActivity.this.userId));
                    if (user6 == null) {
                        return;
                    }
                    VoIPHelper.startCall(user6, i == 16, ProfileActivity.this.userInfo != null && ProfileActivity.this.userInfo.video_calls_available, ProfileActivity.this.getParentActivity(), ProfileActivity.this.userInfo, ProfileActivity.this.getAccountInstance());
                } else if (ProfileActivity.this.chatId == 0) {
                } else {
                    if (ProfileActivity.this.getMessagesController().getGroupCall(ProfileActivity.this.chatId, false) != null) {
                        TLRPC$Chat tLRPC$Chat = ProfileActivity.this.currentChat;
                        Activity parentActivity = ProfileActivity.this.getParentActivity();
                        ProfileActivity profileActivity4 = ProfileActivity.this;
                        VoIPHelper.startCall(tLRPC$Chat, null, null, false, parentActivity, profileActivity4, profileActivity4.getAccountInstance());
                        return;
                    }
                    ProfileActivity profileActivity5 = ProfileActivity.this;
                    VoIPHelper.showGroupCallAlert(profileActivity5, profileActivity5.currentChat, null, false, ProfileActivity.this.getAccountInstance());
                }
            } else if (i == 17) {
                Bundle bundle6 = new Bundle();
                bundle6.putLong("chat_id", ProfileActivity.this.chatId);
                bundle6.putInt("type", 2);
                bundle6.putBoolean("open_search", true);
                ChatUsersActivity chatUsersActivity = new ChatUsersActivity(bundle6);
                chatUsersActivity.setInfo(ProfileActivity.this.chatInfo);
                ProfileActivity.this.presentFragment(chatUsersActivity);
            } else if (i == 18) {
                ProfileActivity.this.openAddMember();
            } else if (i == 19) {
                TLRPC$Chat chat2 = ProfileActivity.this.getMessagesController().getChat(Long.valueOf(ProfileActivity.this.chatId));
                Bundle bundle7 = new Bundle();
                bundle7.putLong("chat_id", ProfileActivity.this.chatId);
                bundle7.putBoolean("is_megagroup", chat2.megagroup);
                ProfileActivity.this.presentFragment(new StatisticActivity(bundle7));
            } else if (i == 22) {
                ProfileActivity.this.openDiscussion();
            } else if (i == 38) {
                ProfileActivity profileActivity6 = ProfileActivity.this;
                ProfileActivity profileActivity7 = ProfileActivity.this;
                profileActivity6.showDialog(new GiftPremiumBottomSheet(profileActivity7, profileActivity7.getMessagesController().getUser(Long.valueOf(ProfileActivity.this.userId))));
            } else if (i == 20) {
                AlertDialog.Builder builder3 = new AlertDialog.Builder(ProfileActivity.this.getParentActivity(), ProfileActivity.this.resourcesProvider);
                builder3.setTitle(LocaleController.getString("AreYouSureSecretChatTitle", R.string.AreYouSureSecretChatTitle));
                builder3.setMessage(LocaleController.getString("AreYouSureSecretChat", R.string.AreYouSureSecretChat));
                builder3.setPositiveButton(LocaleController.getString("Start", R.string.Start), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ProfileActivity$5$$ExternalSyntheticLambda2
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i2) {
                        ProfileActivity.AnonymousClass5.this.lambda$onItemClick$7(dialogInterface, i2);
                    }
                });
                builder3.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                ProfileActivity.this.showDialog(builder3.create());
            } else if (i == 21) {
                if (ProfileActivity.this.getParentActivity() == null) {
                    return;
                }
                int i2 = Build.VERSION.SDK_INT;
                if (i2 < 23 || ((i2 > 28 && !BuildVars.NO_SCOPED_STORAGE) || ProfileActivity.this.getParentActivity().checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0)) {
                    ImageLocation imageLocation = ProfileActivity.this.avatarsViewPager.getImageLocation(ProfileActivity.this.avatarsViewPager.getRealPosition());
                    if (imageLocation == null) {
                        return;
                    }
                    if (imageLocation.imageType == 2) {
                        z = true;
                    }
                    FileLoader fileLoader = FileLoader.getInstance(((BaseFragment) ProfileActivity.this).currentAccount);
                    TLRPC$TL_fileLocationToBeDeprecated tLRPC$TL_fileLocationToBeDeprecated = imageLocation.location;
                    if (z) {
                        str2 = "mp4";
                    }
                    File pathToAttach = fileLoader.getPathToAttach(tLRPC$TL_fileLocationToBeDeprecated, str2, true);
                    if (!pathToAttach.exists()) {
                        return;
                    }
                    MediaController.saveFile(pathToAttach.toString(), ProfileActivity.this.getParentActivity(), 0, null, null, new Runnable() { // from class: org.telegram.ui.ProfileActivity$5$$ExternalSyntheticLambda7
                        @Override // java.lang.Runnable
                        public final void run() {
                            ProfileActivity.AnonymousClass5.this.lambda$onItemClick$8(z);
                        }
                    });
                    return;
                }
                ProfileActivity.this.getParentActivity().requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 4);
            } else if (i == 30) {
                ProfileActivity profileActivity8 = ProfileActivity.this;
                profileActivity8.presentFragment(new ChangeNameActivity(profileActivity8.resourcesProvider));
            } else if (i == 31) {
                ProfileActivity.this.presentFragment(new LogoutActivity());
            } else if (i == 33) {
                int realPosition = ProfileActivity.this.avatarsViewPager.getRealPosition();
                final TLRPC$Photo photo = ProfileActivity.this.avatarsViewPager.getPhoto(realPosition);
                if (photo == null) {
                    return;
                }
                ProfileActivity.this.avatarsViewPager.startMovePhotoToBegin(realPosition);
                TLRPC$TL_photos_updateProfilePhoto tLRPC$TL_photos_updateProfilePhoto = new TLRPC$TL_photos_updateProfilePhoto();
                TLRPC$TL_inputPhoto tLRPC$TL_inputPhoto = new TLRPC$TL_inputPhoto();
                tLRPC$TL_photos_updateProfilePhoto.id = tLRPC$TL_inputPhoto;
                tLRPC$TL_inputPhoto.id = photo.id;
                tLRPC$TL_inputPhoto.access_hash = photo.access_hash;
                tLRPC$TL_inputPhoto.file_reference = photo.file_reference;
                final UserConfig userConfig = ProfileActivity.this.getUserConfig();
                ProfileActivity.this.getConnectionsManager().sendRequest(tLRPC$TL_photos_updateProfilePhoto, new RequestDelegate() { // from class: org.telegram.ui.ProfileActivity$5$$ExternalSyntheticLambda10
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        ProfileActivity.AnonymousClass5.this.lambda$onItemClick$10(userConfig, photo, tLObject, tLRPC$TL_error);
                    }
                });
                UndoView undoView = ProfileActivity.this.undoView;
                long j2 = ProfileActivity.this.userId;
                if (!photo.video_sizes.isEmpty()) {
                    num = 1;
                }
                undoView.showWithAction(j2, 22, num);
                TLRPC$User user7 = ProfileActivity.this.getMessagesController().getUser(Long.valueOf(userConfig.clientUserId));
                TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 800);
                if (user7 != null) {
                    TLRPC$PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 90);
                    TLRPC$UserProfilePhoto tLRPC$UserProfilePhoto = user7.photo;
                    tLRPC$UserProfilePhoto.photo_id = photo.id;
                    tLRPC$UserProfilePhoto.photo_small = closestPhotoSizeWithSize2.location;
                    tLRPC$UserProfilePhoto.photo_big = closestPhotoSizeWithSize.location;
                    userConfig.setCurrentUser(user7);
                    userConfig.saveConfig(true);
                    NotificationCenter.getInstance(((BaseFragment) ProfileActivity.this).currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                    ProfileActivity.this.updateProfileData(true);
                }
                ProfileActivity.this.avatarsViewPager.commitMoveToBegin();
            } else if (i == 34) {
                int realPosition2 = ProfileActivity.this.avatarsViewPager.getRealPosition();
                ImageLocation imageLocation2 = ProfileActivity.this.avatarsViewPager.getImageLocation(realPosition2);
                if (imageLocation2 == null) {
                    return;
                }
                File pathToAttach2 = FileLoader.getInstance(((BaseFragment) ProfileActivity.this).currentAccount).getPathToAttach(PhotoViewer.getFileLocation(imageLocation2), PhotoViewer.getFileLocationExt(imageLocation2), true);
                boolean z2 = imageLocation2.imageType == 2;
                if (z2) {
                    ImageLocation realImageLocation = ProfileActivity.this.avatarsViewPager.getRealImageLocation(realPosition2);
                    str3 = FileLoader.getInstance(((BaseFragment) ProfileActivity.this).currentAccount).getPathToAttach(PhotoViewer.getFileLocation(realImageLocation), PhotoViewer.getFileLocationExt(realImageLocation), true).getAbsolutePath();
                }
                ProfileActivity.this.imageUpdater.openPhotoForEdit(pathToAttach2.getAbsolutePath(), str3, 0, z2);
            } else if (i != 35) {
                if (i == 36) {
                    ProfileActivity.this.onWriteButtonClick();
                } else if (i != 37 || ProfileActivity.this.qrItem == null || ProfileActivity.this.qrItem.getAlpha() <= 0.0f) {
                } else {
                    Bundle bundle8 = new Bundle();
                    bundle8.putLong("chat_id", ProfileActivity.this.chatId);
                    bundle8.putLong("user_id", ProfileActivity.this.userId);
                    ProfileActivity.this.presentFragment(new QrActivity(bundle8));
                }
            } else {
                AlertDialog.Builder builder4 = new AlertDialog.Builder(ProfileActivity.this.getParentActivity(), ProfileActivity.this.resourcesProvider);
                ImageLocation imageLocation3 = ProfileActivity.this.avatarsViewPager.getImageLocation(ProfileActivity.this.avatarsViewPager.getRealPosition());
                if (imageLocation3 == null) {
                    return;
                }
                if (imageLocation3.imageType == 2) {
                    builder4.setTitle(LocaleController.getString("AreYouSureDeleteVideoTitle", R.string.AreYouSureDeleteVideoTitle));
                    builder4.setMessage(LocaleController.formatString("AreYouSureDeleteVideo", R.string.AreYouSureDeleteVideo, new Object[0]));
                } else {
                    builder4.setTitle(LocaleController.getString("AreYouSureDeletePhotoTitle", R.string.AreYouSureDeletePhotoTitle));
                    builder4.setMessage(LocaleController.formatString("AreYouSureDeletePhoto", R.string.AreYouSureDeletePhoto, new Object[0]));
                }
                builder4.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ProfileActivity$5$$ExternalSyntheticLambda1
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i3) {
                        ProfileActivity.AnonymousClass5.this.lambda$onItemClick$11(dialogInterface, i3);
                    }
                });
                builder4.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                AlertDialog create3 = builder4.create();
                ProfileActivity.this.showDialog(create3);
                TextView textView3 = (TextView) create3.getButton(-1);
                if (textView3 == null) {
                    return;
                }
                textView3.setTextColor(ProfileActivity.this.getThemedColor("dialogTextRed2"));
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onItemClick$0(int i) {
            if (i == 1) {
                NotificationCenter notificationCenter = ProfileActivity.this.getNotificationCenter();
                ProfileActivity profileActivity = ProfileActivity.this;
                int i2 = NotificationCenter.closeChats;
                notificationCenter.removeObserver(profileActivity, i2);
                ProfileActivity.this.getNotificationCenter().postNotificationName(i2, new Object[0]);
                ProfileActivity.this.playProfileAnimation = 0;
                ProfileActivity.this.finishFragment();
                return;
            }
            ProfileActivity.this.getNotificationCenter().postNotificationName(NotificationCenter.peerSettingsDidLoad, Long.valueOf(ProfileActivity.this.userId));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onItemClick$1(DialogInterface dialogInterface, int i) {
            ProfileActivity.this.getMessagesController().blockPeer(ProfileActivity.this.userId);
            if (BulletinFactory.canShowBulletin(ProfileActivity.this)) {
                BulletinFactory.createBanBulletin(ProfileActivity.this, true).show();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onItemClick$2(TLRPC$User tLRPC$User, DialogInterface dialogInterface, int i) {
            ArrayList<TLRPC$User> arrayList = new ArrayList<>();
            arrayList.add(tLRPC$User);
            ProfileActivity.this.getContactsController().deleteContact(arrayList, true);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onItemClick$6(final TLRPC$User tLRPC$User, final DialogsActivity dialogsActivity, final DialogsActivity dialogsActivity2, ArrayList arrayList, CharSequence charSequence, boolean z) {
            TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights;
            final long j = ((MessagesStorage.TopicKey) arrayList.get(0)).dialogId;
            TLRPC$Chat chat = MessagesController.getInstance(((BaseFragment) ProfileActivity.this).currentAccount).getChat(Long.valueOf(-j));
            if (chat != null && (chat.creator || ((tLRPC$TL_chatAdminRights = chat.admin_rights) != null && tLRPC$TL_chatAdminRights.add_admins))) {
                ProfileActivity.this.getMessagesController().checkIsInChat(chat, tLRPC$User, new MessagesController.IsInChatCheckedCallback() { // from class: org.telegram.ui.ProfileActivity$5$$ExternalSyntheticLambda8
                    @Override // org.telegram.messenger.MessagesController.IsInChatCheckedCallback
                    public final void run(boolean z2, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights2, String str) {
                        ProfileActivity.AnonymousClass5.this.lambda$onItemClick$4(j, dialogsActivity, z2, tLRPC$TL_chatAdminRights2, str);
                    }
                });
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this.getParentActivity(), ProfileActivity.this.resourcesProvider);
            int i = R.string.AddBot;
            builder.setTitle(LocaleController.getString("AddBot", i));
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("AddMembersAlertNamesText", R.string.AddMembersAlertNamesText, UserObject.getUserName(tLRPC$User), chat == null ? "" : chat.title)));
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            builder.setPositiveButton(LocaleController.getString("AddBot", i), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ProfileActivity$5$$ExternalSyntheticLambda3
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i2) {
                    ProfileActivity.AnonymousClass5.this.lambda$onItemClick$5(j, dialogsActivity2, tLRPC$User, dialogInterface, i2);
                }
            });
            ProfileActivity.this.showDialog(builder.create());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onItemClick$4(final long j, final DialogsActivity dialogsActivity, final boolean z, final TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, final String str) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ProfileActivity$5$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.AnonymousClass5.this.lambda$onItemClick$3(j, tLRPC$TL_chatAdminRights, str, z, dialogsActivity);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onItemClick$3(long j, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, String str, boolean z, final DialogsActivity dialogsActivity) {
            ChatRightsEditActivity chatRightsEditActivity = new ChatRightsEditActivity(ProfileActivity.this.userId, -j, tLRPC$TL_chatAdminRights, null, null, str, 2, true, !z, null);
            chatRightsEditActivity.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() { // from class: org.telegram.ui.ProfileActivity.5.1
                @Override // org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate
                public void didChangeOwner(TLRPC$User tLRPC$User) {
                }

                @Override // org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate
                public void didSetRights(int i, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights2, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, String str2) {
                    ProfileActivity.this.disableProfileAnimation = true;
                    dialogsActivity.removeSelfFromStack();
                    NotificationCenter notificationCenter = ProfileActivity.this.getNotificationCenter();
                    ProfileActivity profileActivity = ProfileActivity.this;
                    int i2 = NotificationCenter.closeChats;
                    notificationCenter.removeObserver(profileActivity, i2);
                    ProfileActivity.this.getNotificationCenter().postNotificationName(i2, new Object[0]);
                }
            });
            ProfileActivity.this.presentFragment(chatRightsEditActivity);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onItemClick$5(long j, DialogsActivity dialogsActivity, TLRPC$User tLRPC$User, DialogInterface dialogInterface, int i) {
            ProfileActivity.this.disableProfileAnimation = true;
            Bundle bundle = new Bundle();
            bundle.putBoolean("scrollToTopOnResume", true);
            long j2 = -j;
            bundle.putLong("chat_id", j2);
            if (!ProfileActivity.this.getMessagesController().checkCanOpenChat(bundle, dialogsActivity)) {
                return;
            }
            ChatActivity chatActivity = new ChatActivity(bundle);
            NotificationCenter notificationCenter = ProfileActivity.this.getNotificationCenter();
            ProfileActivity profileActivity = ProfileActivity.this;
            int i2 = NotificationCenter.closeChats;
            notificationCenter.removeObserver(profileActivity, i2);
            ProfileActivity.this.getNotificationCenter().postNotificationName(i2, new Object[0]);
            ProfileActivity.this.getMessagesController().addUserToChat(j2, tLRPC$User, 0, null, chatActivity, true, null, null);
            ProfileActivity.this.presentFragment(chatActivity, true);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onItemClick$7(DialogInterface dialogInterface, int i) {
            ProfileActivity.this.creatingChat = true;
            ProfileActivity.this.getSecretChatHelper().startSecretChat(ProfileActivity.this.getParentActivity(), ProfileActivity.this.getMessagesController().getUser(Long.valueOf(ProfileActivity.this.userId)));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onItemClick$8(boolean z) {
            if (ProfileActivity.this.getParentActivity() == null) {
                return;
            }
            BulletinFactory.createSaveToGalleryBulletin(ProfileActivity.this, z, null).show();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onItemClick$10(final UserConfig userConfig, final TLRPC$Photo tLRPC$Photo, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ProfileActivity$5$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.AnonymousClass5.this.lambda$onItemClick$9(tLObject, userConfig, tLRPC$Photo);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onItemClick$9(TLObject tLObject, UserConfig userConfig, TLRPC$Photo tLRPC$Photo) {
            ProfileActivity.this.avatarsViewPager.finishSettingMainPhoto();
            if (tLObject instanceof TLRPC$TL_photos_photo) {
                TLRPC$TL_photos_photo tLRPC$TL_photos_photo = (TLRPC$TL_photos_photo) tLObject;
                ProfileActivity.this.getMessagesController().putUsers(tLRPC$TL_photos_photo.users, false);
                TLRPC$User user = ProfileActivity.this.getMessagesController().getUser(Long.valueOf(userConfig.clientUserId));
                if (!(tLRPC$TL_photos_photo.photo instanceof TLRPC$TL_photo)) {
                    return;
                }
                ProfileActivity.this.avatarsViewPager.replaceFirstPhoto(tLRPC$Photo, tLRPC$TL_photos_photo.photo);
                if (user == null) {
                    return;
                }
                user.photo.photo_id = tLRPC$TL_photos_photo.photo.id;
                userConfig.setCurrentUser(user);
                userConfig.saveConfig(true);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onItemClick$11(DialogInterface dialogInterface, int i) {
            int realPosition = ProfileActivity.this.avatarsViewPager.getRealPosition();
            TLRPC$Photo photo = ProfileActivity.this.avatarsViewPager.getPhoto(realPosition);
            if (ProfileActivity.this.avatarsViewPager.getRealCount() == 1) {
                ProfileActivity.this.setForegroundImage(true);
            }
            if (photo == null || ProfileActivity.this.avatarsViewPager.getRealPosition() == 0) {
                ProfileActivity.this.getMessagesController().deleteUserPhoto(null);
            } else {
                TLRPC$TL_inputPhoto tLRPC$TL_inputPhoto = new TLRPC$TL_inputPhoto();
                tLRPC$TL_inputPhoto.id = photo.id;
                tLRPC$TL_inputPhoto.access_hash = photo.access_hash;
                byte[] bArr = photo.file_reference;
                tLRPC$TL_inputPhoto.file_reference = bArr;
                if (bArr == null) {
                    tLRPC$TL_inputPhoto.file_reference = new byte[0];
                }
                ProfileActivity.this.getMessagesController().deleteUserPhoto(tLRPC$TL_inputPhoto);
                ProfileActivity.this.getMessagesStorage().clearUserPhoto(ProfileActivity.this.userId, photo.id);
            }
            if (ProfileActivity.this.avatarsViewPager.removePhotoAtIndex(realPosition)) {
                ProfileActivity.this.avatarsViewPager.setVisibility(8);
                ProfileActivity.this.avatarImage.setForegroundAlpha(1.0f);
                ProfileActivity.this.avatarContainer.setVisibility(0);
                ProfileActivity.this.doNotSetForeground = true;
                View findViewByPosition = ProfileActivity.this.layoutManager.findViewByPosition(0);
                if (findViewByPosition == null) {
                    return;
                }
                ProfileActivity.this.listView.smoothScrollBy(0, findViewByPosition.getTop() - AndroidUtilities.dp(88.0f), CubicBezierInterpolator.EASE_OUT_QUINT);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.ProfileActivity$6  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass6 extends NestedFrameLayout {
        private Paint grayPaint;
        private boolean ignoreLayout;
        private final ArrayList<View> sortedChildren;
        private final Comparator<View> viewComparator;
        private boolean wasPortrait;

        @Override // android.view.View
        public boolean hasOverlappingRendering() {
            return false;
        }

        AnonymousClass6(Context context) {
            super(context);
            this.grayPaint = new Paint();
            this.sortedChildren = new ArrayList<>();
            this.viewComparator = ProfileActivity$6$$ExternalSyntheticLambda1.INSTANCE;
        }

        @Override // android.view.ViewGroup, android.view.View
        public boolean dispatchTouchEvent(MotionEvent motionEvent) {
            if (!ProfileActivity.this.pinchToZoomHelper.isInOverlayMode()) {
                if (ProfileActivity.this.sharedMediaLayout == null || !ProfileActivity.this.sharedMediaLayout.isInFastScroll() || !ProfileActivity.this.sharedMediaLayout.isPinnedToTop()) {
                    if (ProfileActivity.this.sharedMediaLayout != null && ProfileActivity.this.sharedMediaLayout.checkPinchToZoom(motionEvent)) {
                        return true;
                    }
                    return super.dispatchTouchEvent(motionEvent);
                }
                return ProfileActivity.this.sharedMediaLayout.dispatchFastScrollEvent(motionEvent);
            }
            return ProfileActivity.this.pinchToZoomHelper.onTouchEvent(motionEvent);
        }

        /* JADX WARN: Removed duplicated region for block: B:135:0x04a8  */
        /* JADX WARN: Removed duplicated region for block: B:144:0x04e8  */
        /* JADX WARN: Removed duplicated region for block: B:145:0x04ea  */
        /* JADX WARN: Removed duplicated region for block: B:148:0x04ef  */
        /* JADX WARN: Removed duplicated region for block: B:157:? A[RETURN, SYNTHETIC] */
        @Override // android.widget.FrameLayout, android.view.View
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        protected void onMeasure(int r18, int r19) {
            /*
                Method dump skipped, instructions count: 1274
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.AnonymousClass6.onMeasure(int, int):void");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onMeasure$0() {
            if (ProfileActivity.this.selectAnimatedEmojiDialog != null) {
                ProfileActivity.this.selectAnimatedEmojiDialog.dismiss();
                ProfileActivity.this.selectAnimatedEmojiDialog = null;
            }
        }

        @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            ProfileActivity profileActivity = ProfileActivity.this;
            profileActivity.savedScrollPosition = -1;
            profileActivity.firstLayout = false;
            ProfileActivity.this.invalidateScroll = false;
            ProfileActivity.this.checkListViewScroll();
        }

        @Override // android.view.View, android.view.ViewParent
        public void requestLayout() {
            if (this.ignoreLayout) {
                return;
            }
            super.requestLayout();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ int lambda$$1(View view, View view2) {
            return (int) (view.getY() - view2.getY());
        }

        @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, android.view.ViewGroup, android.view.View
        protected void dispatchDraw(Canvas canvas) {
            int top;
            FragmentContextView fragmentContextView;
            boolean z;
            int i;
            ProfileActivity.this.whitePaint.setColor(ProfileActivity.this.getThemedColor("windowBackgroundWhite"));
            float f = 1.0f;
            if (ProfileActivity.this.listView.getVisibility() != 0) {
                canvas.drawRect(0.0f, ProfileActivity.this.searchListView.getTop() + ProfileActivity.this.extraHeight + ProfileActivity.this.searchTransitionOffset, getMeasuredWidth(), top + getMeasuredHeight(), ProfileActivity.this.whitePaint);
            } else {
                this.grayPaint.setColor(ProfileActivity.this.getThemedColor("windowBackgroundGray"));
                if (ProfileActivity.this.transitionAnimationInProress) {
                    ProfileActivity.this.whitePaint.setAlpha((int) (ProfileActivity.this.listView.getAlpha() * 255.0f));
                }
                if (ProfileActivity.this.transitionAnimationInProress) {
                    this.grayPaint.setAlpha((int) (ProfileActivity.this.listView.getAlpha() * 255.0f));
                }
                int childCount = ProfileActivity.this.listView.getChildCount();
                this.sortedChildren.clear();
                boolean z2 = false;
                for (int i2 = 0; i2 < childCount; i2++) {
                    if (ProfileActivity.this.listView.getChildAdapterPosition(ProfileActivity.this.listView.getChildAt(i2)) != -1) {
                        this.sortedChildren.add(ProfileActivity.this.listView.getChildAt(i2));
                    } else {
                        z2 = true;
                    }
                }
                Collections.sort(this.sortedChildren, this.viewComparator);
                float y = ProfileActivity.this.listView.getY();
                int size = this.sortedChildren.size();
                if (!ProfileActivity.this.openAnimationInProgress && size > 0 && !z2) {
                    y += this.sortedChildren.get(0).getY();
                }
                float f2 = y;
                boolean z3 = false;
                float f3 = 1.0f;
                for (int i3 = 0; i3 < size; i3++) {
                    View view = this.sortedChildren.get(i3);
                    boolean z4 = view.getBackground() != null;
                    int y2 = (int) (ProfileActivity.this.listView.getY() + view.getY());
                    if (z3 == z4) {
                        if (view.getAlpha() == 1.0f) {
                            f3 = 1.0f;
                        }
                    } else {
                        if (z3) {
                            z = z4;
                            canvas.drawRect(ProfileActivity.this.listView.getX(), f2, ProfileActivity.this.listView.getX() + ProfileActivity.this.listView.getMeasuredWidth(), y2, this.grayPaint);
                            i = y2;
                        } else {
                            z = z4;
                            if (f3 != 1.0f) {
                                float f4 = y2;
                                i = y2;
                                canvas.drawRect(ProfileActivity.this.listView.getX(), f2, ProfileActivity.this.listView.getX() + ProfileActivity.this.listView.getMeasuredWidth(), f4, this.grayPaint);
                                ProfileActivity.this.whitePaint.setAlpha((int) (f3 * 255.0f));
                                canvas.drawRect(ProfileActivity.this.listView.getX(), f2, ProfileActivity.this.listView.getX() + ProfileActivity.this.listView.getMeasuredWidth(), f4, ProfileActivity.this.whitePaint);
                                ProfileActivity.this.whitePaint.setAlpha(255);
                            } else {
                                i = y2;
                                canvas.drawRect(ProfileActivity.this.listView.getX(), f2, ProfileActivity.this.listView.getX() + ProfileActivity.this.listView.getMeasuredWidth(), i, ProfileActivity.this.whitePaint);
                            }
                        }
                        f2 = i;
                        f3 = view.getAlpha();
                        z3 = z;
                    }
                }
                if (z3) {
                    canvas.drawRect(ProfileActivity.this.listView.getX(), f2, ProfileActivity.this.listView.getX() + ProfileActivity.this.listView.getMeasuredWidth(), ProfileActivity.this.listView.getBottom(), this.grayPaint);
                } else if (f3 != 1.0f) {
                    canvas.drawRect(ProfileActivity.this.listView.getX(), f2, ProfileActivity.this.listView.getX() + ProfileActivity.this.listView.getMeasuredWidth(), ProfileActivity.this.listView.getBottom(), this.grayPaint);
                    ProfileActivity.this.whitePaint.setAlpha((int) (f3 * 255.0f));
                    canvas.drawRect(ProfileActivity.this.listView.getX(), f2, ProfileActivity.this.listView.getX() + ProfileActivity.this.listView.getMeasuredWidth(), ProfileActivity.this.listView.getBottom(), ProfileActivity.this.whitePaint);
                    ProfileActivity.this.whitePaint.setAlpha(255);
                } else {
                    canvas.drawRect(ProfileActivity.this.listView.getX(), f2, ProfileActivity.this.listView.getX() + ProfileActivity.this.listView.getMeasuredWidth(), ProfileActivity.this.listView.getBottom(), ProfileActivity.this.whitePaint);
                }
            }
            super.dispatchDraw(canvas);
            ProfileActivity profileActivity = ProfileActivity.this;
            if (profileActivity.profileTransitionInProgress && ((BaseFragment) profileActivity).parentLayout.getFragmentStack().size() > 1) {
                BaseFragment baseFragment = ((BaseFragment) ProfileActivity.this).parentLayout.getFragmentStack().get(((BaseFragment) ProfileActivity.this).parentLayout.getFragmentStack().size() - 2);
                if ((baseFragment instanceof ChatActivity) && (fragmentContextView = ((ChatActivity) baseFragment).getFragmentContextView()) != null && fragmentContextView.isCallStyle()) {
                    float dpf2 = ProfileActivity.this.extraHeight / AndroidUtilities.dpf2(fragmentContextView.getStyleHeight());
                    if (dpf2 <= 1.0f) {
                        f = dpf2;
                    }
                    canvas.save();
                    canvas.translate(fragmentContextView.getX(), fragmentContextView.getY());
                    fragmentContextView.setDrawOverlay(true);
                    fragmentContextView.setCollapseTransition(true, ProfileActivity.this.extraHeight, f);
                    fragmentContextView.draw(canvas);
                    fragmentContextView.setCollapseTransition(false, ProfileActivity.this.extraHeight, f);
                    fragmentContextView.setDrawOverlay(false);
                    canvas.restore();
                }
            }
            if (ProfileActivity.this.scrimPaint.getAlpha() > 0) {
                canvas.drawRect(0.0f, 0.0f, getWidth(), getHeight(), ProfileActivity.this.scrimPaint);
            }
            if (ProfileActivity.this.scrimView != null) {
                int save = canvas.save();
                canvas.translate(ProfileActivity.this.scrimView.getLeft(), ProfileActivity.this.scrimView.getTop());
                if (ProfileActivity.this.scrimView == ((BaseFragment) ProfileActivity.this).actionBar.getBackButton()) {
                    int alpha = ProfileActivity.this.actionBarBackgroundPaint.getAlpha();
                    ProfileActivity.this.actionBarBackgroundPaint.setAlpha((int) ((alpha * (ProfileActivity.this.scrimPaint.getAlpha() / 255.0f)) / 0.3f));
                    float max = Math.max(ProfileActivity.this.scrimView.getMeasuredWidth(), ProfileActivity.this.scrimView.getMeasuredHeight()) / 2;
                    canvas.drawCircle(max, max, 0.7f * max, ProfileActivity.this.actionBarBackgroundPaint);
                    ProfileActivity.this.actionBarBackgroundPaint.setAlpha(alpha);
                }
                ProfileActivity.this.scrimView.draw(canvas);
                canvas.restoreToCount(save);
            }
        }

        @Override // android.view.ViewGroup
        protected boolean drawChild(Canvas canvas, View view, long j) {
            if (!ProfileActivity.this.pinchToZoomHelper.isInOverlayMode() || !(view == ProfileActivity.this.avatarContainer2 || view == ((BaseFragment) ProfileActivity.this).actionBar || view == ProfileActivity.this.writeButton)) {
                return super.drawChild(canvas, view, j);
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.ProfileActivity$10  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass10 extends DefaultItemAnimator {
        int animationIndex = -1;

        @Override // androidx.recyclerview.widget.DefaultItemAnimator
        protected long getAddAnimationDelay(long j, long j2, long j3) {
            return 0L;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.ItemAnimator
        public long getAddDuration() {
            return 220L;
        }

        @Override // androidx.recyclerview.widget.DefaultItemAnimator
        protected long getMoveAnimationDelay() {
            return 0L;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.ItemAnimator
        public long getMoveDuration() {
            return 220L;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.ItemAnimator
        public long getRemoveDuration() {
            return 220L;
        }

        AnonymousClass10() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // androidx.recyclerview.widget.DefaultItemAnimator
        public void onAllAnimationsDone() {
            super.onAllAnimationsDone();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ProfileActivity$10$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.AnonymousClass10.this.lambda$onAllAnimationsDone$0();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onAllAnimationsDone$0() {
            ProfileActivity.this.getNotificationCenter().onAnimationFinish(this.animationIndex);
        }

        @Override // androidx.recyclerview.widget.DefaultItemAnimator, androidx.recyclerview.widget.RecyclerView.ItemAnimator
        public void runPendingAnimations() {
            boolean z = !this.mPendingRemovals.isEmpty();
            boolean z2 = !this.mPendingMoves.isEmpty();
            boolean z3 = !this.mPendingChanges.isEmpty();
            boolean z4 = !this.mPendingAdditions.isEmpty();
            if (z || z2 || z4 || z3) {
                ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ProfileActivity$10$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        ProfileActivity.AnonymousClass10.this.lambda$runPendingAnimations$1(valueAnimator);
                    }
                });
                ofFloat.setDuration(getMoveDuration());
                ofFloat.start();
                this.animationIndex = ProfileActivity.this.getNotificationCenter().setAnimationInProgress(this.animationIndex, null);
            }
            super.runPendingAnimations();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$runPendingAnimations$1(ValueAnimator valueAnimator) {
            ProfileActivity.this.listView.invalidate();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$7(final long j, Context context, View view, int i, float f, float f2) {
        TLRPC$ChatParticipant tLRPC$ChatParticipant;
        float f3;
        float f4;
        if (getParentActivity() == null) {
            return;
        }
        this.listView.stopScroll();
        if (i == this.notificationsSimpleRow) {
            boolean isDialogMuted = getMessagesController().isDialogMuted(j, this.topicId);
            getNotificationsController().muteDialog(j, this.topicId, !isDialogMuted);
            BulletinFactory.createMuteBulletin(this, !isDialogMuted, null).show();
            int i2 = this.notificationsSimpleRow;
            if (i2 >= 0) {
                this.listAdapter.notifyItemChanged(i2);
            }
        } else if (i == this.reportReactionRow) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity(), this.resourcesProvider);
            builder.setTitle(LocaleController.getString("ReportReaction", R.string.ReportReaction));
            builder.setMessage(LocaleController.getString("ReportAlertReaction", R.string.ReportAlertReaction));
            TLRPC$Chat chat = getMessagesController().getChat(Long.valueOf(-this.reportReactionFromDialogId));
            final CheckBoxCell[] checkBoxCellArr = new CheckBoxCell[1];
            if (chat != null && ChatObject.canBlockUsers(chat)) {
                LinearLayout linearLayout = new LinearLayout(getParentActivity());
                linearLayout.setOrientation(1);
                checkBoxCellArr[0] = new CheckBoxCell(getParentActivity(), 1, this.resourcesProvider);
                checkBoxCellArr[0].setBackgroundDrawable(Theme.getSelectorDrawable(false));
                checkBoxCellArr[0].setText(LocaleController.getString("BanUser", R.string.BanUser), "", true, false);
                checkBoxCellArr[0].setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
                linearLayout.addView(checkBoxCellArr[0], LayoutHelper.createLinear(-1, -2));
                checkBoxCellArr[0].setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda16
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                        ProfileActivity.lambda$createView$5(checkBoxCellArr, view2);
                    }
                });
                builder.setCustomViewOffset(12);
                builder.setView(linearLayout);
            }
            builder.setPositiveButton(LocaleController.getString("ReportChat", R.string.ReportChat), new AnonymousClass12(checkBoxCellArr));
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), new DialogInterface.OnClickListener(this) { // from class: org.telegram.ui.ProfileActivity.13
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i3) {
                    dialogInterface.dismiss();
                }
            });
            TextView textView = (TextView) builder.show().getButton(-1);
            if (textView != null) {
                textView.setTextColor(Theme.getColor("dialogTextRed2"));
            }
        }
        if (i == this.settingsKeyRow) {
            Bundle bundle = new Bundle();
            bundle.putInt("chat_id", DialogObject.getEncryptedChatId(this.dialogId));
            presentFragment(new IdenticonActivity(bundle));
        } else if (i == this.settingsTimerRow) {
            showDialog(AlertsCreator.createTTLAlert(getParentActivity(), this.currentEncryptedChat, this.resourcesProvider).create());
        } else if (i == this.notificationsRow) {
            if ((LocaleController.isRTL && f <= AndroidUtilities.dp(76.0f)) || (!LocaleController.isRTL && f >= view.getMeasuredWidth() - AndroidUtilities.dp(76.0f))) {
                NotificationsCheckCell notificationsCheckCell = (NotificationsCheckCell) view;
                boolean z = !notificationsCheckCell.isChecked();
                boolean isGlobalNotificationsEnabled = getNotificationsController().isGlobalNotificationsEnabled(j);
                String sharedPrefKey = NotificationsController.getSharedPrefKey(j, this.topicId);
                long j2 = 0;
                if (z) {
                    SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                    if (isGlobalNotificationsEnabled) {
                        edit.remove("notify2_" + sharedPrefKey);
                    } else {
                        edit.putInt("notify2_" + sharedPrefKey, 0);
                    }
                    if (this.topicId == 0) {
                        getMessagesStorage().setDialogFlags(j, 0L);
                        TLRPC$Dialog tLRPC$Dialog = getMessagesController().dialogs_dict.get(j);
                        if (tLRPC$Dialog != null) {
                            tLRPC$Dialog.notify_settings = new TLRPC$TL_peerNotifySettings();
                        }
                    }
                    edit.apply();
                } else {
                    SharedPreferences.Editor edit2 = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                    if (!isGlobalNotificationsEnabled) {
                        edit2.remove("notify2_" + sharedPrefKey);
                    } else {
                        edit2.putInt("notify2_" + sharedPrefKey, 2);
                        j2 = 1;
                    }
                    getNotificationsController().removeNotificationsForDialog(j);
                    if (this.topicId == 0) {
                        getMessagesStorage().setDialogFlags(j, j2);
                        TLRPC$Dialog tLRPC$Dialog2 = getMessagesController().dialogs_dict.get(j);
                        if (tLRPC$Dialog2 != null) {
                            TLRPC$TL_peerNotifySettings tLRPC$TL_peerNotifySettings = new TLRPC$TL_peerNotifySettings();
                            tLRPC$Dialog2.notify_settings = tLRPC$TL_peerNotifySettings;
                            if (isGlobalNotificationsEnabled) {
                                tLRPC$TL_peerNotifySettings.mute_until = Integer.MAX_VALUE;
                            }
                        }
                    }
                    edit2.apply();
                }
                getNotificationsController().updateServerNotificationsSettings(j, this.topicId);
                notificationsCheckCell.setChecked(z);
                RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findViewHolderForPosition(this.notificationsRow);
                if (holder == null) {
                    return;
                }
                this.listAdapter.onBindViewHolder(holder, this.notificationsRow);
                return;
            }
            ChatNotificationsPopupWrapper chatNotificationsPopupWrapper = new ChatNotificationsPopupWrapper(context, this.currentAccount, null, true, true, new ChatNotificationsPopupWrapper.Callback() { // from class: org.telegram.ui.ProfileActivity.14
                @Override // org.telegram.ui.Components.ChatNotificationsPopupWrapper.Callback
                public /* synthetic */ void dismiss() {
                    ChatNotificationsPopupWrapper.Callback.CC.$default$dismiss(this);
                }

                @Override // org.telegram.ui.Components.ChatNotificationsPopupWrapper.Callback
                public void toggleSound() {
                    SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(((BaseFragment) ProfileActivity.this).currentAccount);
                    boolean z2 = !notificationsSettings.getBoolean("sound_enabled_" + NotificationsController.getSharedPrefKey(j, ProfileActivity.this.topicId), true);
                    notificationsSettings.edit().putBoolean("sound_enabled_" + NotificationsController.getSharedPrefKey(j, ProfileActivity.this.topicId), z2).apply();
                    if (BulletinFactory.canShowBulletin(ProfileActivity.this)) {
                        ProfileActivity profileActivity = ProfileActivity.this;
                        BulletinFactory.createSoundEnabledBulletin(profileActivity, !z2, profileActivity.getResourceProvider()).show();
                    }
                }

                @Override // org.telegram.ui.Components.ChatNotificationsPopupWrapper.Callback
                public void muteFor(int i3) {
                    if (i3 == 0) {
                        if (ProfileActivity.this.getMessagesController().isDialogMuted(j, ProfileActivity.this.topicId)) {
                            toggleMute();
                        }
                        if (!BulletinFactory.canShowBulletin(ProfileActivity.this)) {
                            return;
                        }
                        ProfileActivity profileActivity = ProfileActivity.this;
                        BulletinFactory.createMuteBulletin(profileActivity, 4, i3, profileActivity.getResourceProvider()).show();
                        return;
                    }
                    ProfileActivity.this.getNotificationsController().muteUntil(j, ProfileActivity.this.topicId, i3);
                    if (BulletinFactory.canShowBulletin(ProfileActivity.this)) {
                        ProfileActivity profileActivity2 = ProfileActivity.this;
                        BulletinFactory.createMuteBulletin(profileActivity2, 5, i3, profileActivity2.getResourceProvider()).show();
                    }
                    if (ProfileActivity.this.notificationsRow < 0) {
                        return;
                    }
                    ProfileActivity.this.listAdapter.notifyItemChanged(ProfileActivity.this.notificationsRow);
                }

                @Override // org.telegram.ui.Components.ChatNotificationsPopupWrapper.Callback
                public void showCustomize() {
                    if (j != 0) {
                        Bundle bundle2 = new Bundle();
                        bundle2.putLong("dialog_id", j);
                        bundle2.putInt("topic_id", ProfileActivity.this.topicId);
                        ProfileActivity profileActivity = ProfileActivity.this;
                        profileActivity.presentFragment(new ProfileNotificationsActivity(bundle2, profileActivity.resourcesProvider));
                    }
                }

                @Override // org.telegram.ui.Components.ChatNotificationsPopupWrapper.Callback
                public void toggleMute() {
                    boolean isDialogMuted2 = ProfileActivity.this.getMessagesController().isDialogMuted(j, ProfileActivity.this.topicId);
                    ProfileActivity.this.getNotificationsController().muteDialog(j, ProfileActivity.this.topicId, !isDialogMuted2);
                    BulletinFactory.createMuteBulletin(ProfileActivity.this, !isDialogMuted2, null).show();
                    if (ProfileActivity.this.notificationsRow >= 0) {
                        ProfileActivity.this.listAdapter.notifyItemChanged(ProfileActivity.this.notificationsRow);
                    }
                }

                @Override // org.telegram.ui.Components.ChatNotificationsPopupWrapper.Callback
                public void openExceptions() {
                    Bundle bundle2 = new Bundle();
                    bundle2.putLong("dialog_id", j);
                    TopicsNotifySettingsFragments topicsNotifySettingsFragments = new TopicsNotifySettingsFragments(bundle2);
                    topicsNotifySettingsFragments.setExceptions(ProfileActivity.this.notificationsExceptionTopics);
                    ProfileActivity.this.presentFragment(topicsNotifySettingsFragments);
                }
            }, getResourceProvider());
            chatNotificationsPopupWrapper.lambda$update$11(j, this.topicId, this.notificationsExceptionTopics);
            if (AndroidUtilities.isTablet()) {
                ViewGroup view2 = this.parentLayout.getView();
                f3 = view2.getX() + view2.getPaddingLeft() + f;
                f4 = f2 + view2.getY() + view2.getPaddingTop();
            } else {
                f3 = f;
                f4 = f2;
            }
            chatNotificationsPopupWrapper.showAsOptions(this, view, f3, f4);
        } else if (i == this.unblockRow) {
            getMessagesController().unblockPeer(this.userId);
            if (!BulletinFactory.canShowBulletin(this)) {
                return;
            }
            BulletinFactory.createBanBulletin(this, false).show();
        } else if (i == this.addToGroupButtonRow) {
            try {
                this.actionBar.getActionBarMenuOnItemClick().onItemClick(9);
            } catch (Exception e) {
                FileLog.e(e);
            }
        } else if (i == this.sendMessageRow) {
            onWriteButtonClick();
        } else if (i == this.reportRow) {
            AlertsCreator.createReportAlert(getParentActivity(), getDialogId(), 0, this, this.resourcesProvider, null);
        } else if (i >= this.membersStartRow && i < this.membersEndRow) {
            if (!this.sortedUsers.isEmpty()) {
                tLRPC$ChatParticipant = this.chatInfo.participants.participants.get(this.sortedUsers.get(i - this.membersStartRow).intValue());
            } else {
                tLRPC$ChatParticipant = this.chatInfo.participants.participants.get(i - this.membersStartRow);
            }
            onMemberClick(tLRPC$ChatParticipant, false);
        } else if (i == this.addMemberRow) {
            openAddMember();
        } else if (i == this.usernameRow) {
            processOnClickOrPress(i, view, f, f2);
        } else if (i == this.locationRow) {
            if (!(this.chatInfo.location instanceof TLRPC$TL_channelLocation)) {
                return;
            }
            LocationActivity locationActivity = new LocationActivity(5);
            locationActivity.setChatLocation(this.chatId, (TLRPC$TL_channelLocation) this.chatInfo.location);
            presentFragment(locationActivity);
        } else if (i == this.joinRow) {
            getMessagesController().addUserToChat(this.currentChat.id, getUserConfig().getCurrentUser(), 0, null, this, null);
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.closeSearchByActiveAction, new Object[0]);
        } else if (i == this.subscribersRow) {
            Bundle bundle2 = new Bundle();
            bundle2.putLong("chat_id", this.chatId);
            bundle2.putInt("type", 2);
            ChatUsersActivity chatUsersActivity = new ChatUsersActivity(bundle2);
            chatUsersActivity.setInfo(this.chatInfo);
            presentFragment(chatUsersActivity);
        } else if (i == this.subscribersRequestsRow) {
            presentFragment(new MemberRequestsActivity(this.chatId));
        } else if (i == this.administratorsRow) {
            Bundle bundle3 = new Bundle();
            bundle3.putLong("chat_id", this.chatId);
            bundle3.putInt("type", 1);
            ChatUsersActivity chatUsersActivity2 = new ChatUsersActivity(bundle3);
            chatUsersActivity2.setInfo(this.chatInfo);
            presentFragment(chatUsersActivity2);
        } else if (i == this.blockedUsersRow) {
            Bundle bundle4 = new Bundle();
            bundle4.putLong("chat_id", this.chatId);
            bundle4.putInt("type", 0);
            ChatUsersActivity chatUsersActivity3 = new ChatUsersActivity(bundle4);
            chatUsersActivity3.setInfo(this.chatInfo);
            presentFragment(chatUsersActivity3);
        } else if (i == this.notificationRow) {
            presentFragment(new NotificationsSettingsActivity());
        } else if (i == this.privacyRow) {
            presentFragment(new PrivacySettingsActivity().setCurrentPassword(this.currentPassword));
        } else if (i == this.dataRow) {
            presentFragment(new DataSettingsActivity());
        } else if (i == this.chatRow) {
            presentFragment(new ThemeActivity(0));
        } else if (i == this.filtersRow) {
            presentFragment(new FiltersSetupActivity());
        } else if (i == this.stickersRow) {
            presentFragment(new StickersActivity(0, null));
        } else if (i == this.devicesRow) {
            presentFragment(new SessionsActivity(0));
        } else if (i == this.questionRow) {
            showDialog(AlertsCreator.createSupportAlert(this, this.resourcesProvider));
        } else if (i == this.faqRow) {
            Browser.openUrl(getParentActivity(), LocaleController.getString("TelegramFaqUrl", R.string.TelegramFaqUrl));
        } else if (i == this.policyRow) {
            Browser.openUrl(getParentActivity(), LocaleController.getString("PrivacyPolicyUrl", R.string.PrivacyPolicyUrl));
        } else if (i == this.sendLogsRow) {
            sendLogs(false);
        } else if (i == this.sendLastLogsRow) {
            sendLogs(true);
        } else if (i == this.clearLogsRow) {
            FileLog.cleanupLogs();
        } else if (i == this.switchBackendRow) {
            if (getParentActivity() == null) {
                return;
            }
            AlertDialog.Builder builder2 = new AlertDialog.Builder(getParentActivity(), this.resourcesProvider);
            builder2.setMessage(LocaleController.getString("AreYouSure", R.string.AreYouSure));
            builder2.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder2.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda5
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i3) {
                    ProfileActivity.this.lambda$createView$6(dialogInterface, i3);
                }
            });
            builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            showDialog(builder2.create());
        } else if (i == this.languageRow) {
            presentFragment(new LanguageSelectActivity());
        } else if (i == this.setUsernameRow) {
            presentFragment(new ChangeUsernameActivity());
        } else if (i == this.bioRow) {
            if (this.userInfo == null) {
                return;
            }
            presentFragment(new ChangeBioActivity());
        } else if (i == this.numberRow) {
            presentFragment(new ActionIntroActivity(3));
        } else if (i == this.setAvatarRow) {
            onWriteButtonClick();
        } else if (i == this.premiumRow) {
            presentFragment(new PremiumPreviewFragment("settings"));
        } else {
            processOnClickOrPress(i, view, f, f2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$createView$5(CheckBoxCell[] checkBoxCellArr, View view) {
        checkBoxCellArr[0].setChecked(!checkBoxCellArr[0].isChecked(), true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.ProfileActivity$12  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass12 implements DialogInterface.OnClickListener {
        final /* synthetic */ CheckBoxCell[] val$cells;

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$onClick$0(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        }

        AnonymousClass12(CheckBoxCell[] checkBoxCellArr) {
            this.val$cells = checkBoxCellArr;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            TLRPC$TL_messages_reportReaction tLRPC$TL_messages_reportReaction = new TLRPC$TL_messages_reportReaction();
            tLRPC$TL_messages_reportReaction.user_id = ProfileActivity.this.getMessagesController().getInputUser(ProfileActivity.this.userId);
            tLRPC$TL_messages_reportReaction.peer = ProfileActivity.this.getMessagesController().getInputPeer(ProfileActivity.this.reportReactionFromDialogId);
            tLRPC$TL_messages_reportReaction.id = ProfileActivity.this.reportReactionMessageId;
            ConnectionsManager.getInstance(((BaseFragment) ProfileActivity.this).currentAccount).sendRequest(tLRPC$TL_messages_reportReaction, ProfileActivity$12$$ExternalSyntheticLambda0.INSTANCE);
            CheckBoxCell[] checkBoxCellArr = this.val$cells;
            if (checkBoxCellArr[0] != null && checkBoxCellArr[0].isChecked()) {
                ProfileActivity.this.getMessagesController().deleteParticipantFromChat(-ProfileActivity.this.reportReactionFromDialogId, ProfileActivity.this.getMessagesController().getUser(Long.valueOf(ProfileActivity.this.userId)));
            }
            ProfileActivity.this.reportReactionMessageId = 0;
            ProfileActivity.this.updateListAnimated(false);
            BulletinFactory.of(ProfileActivity.this).createReportSent(ProfileActivity.this.resourcesProvider).show();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$6(DialogInterface dialogInterface, int i) {
        SharedConfig.pushAuthKey = null;
        SharedConfig.pushAuthKeyId = null;
        SharedConfig.saveConfig();
        getConnectionsManager().switchBackend(true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.ProfileActivity$15  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass15 implements RecyclerListView.OnItemLongClickListener {
        private int pressCount = 0;
        final /* synthetic */ Context val$context;

        AnonymousClass15(Context context) {
            this.val$context = context;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
        public boolean onItemClick(View view, int i) {
            int i2;
            String str;
            String string;
            int i3;
            String str2;
            int i4;
            String str3;
            String str4;
            String str5;
            String str6;
            String str7;
            int i5;
            String str8;
            if (i != ProfileActivity.this.versionRow) {
                if (i < ProfileActivity.this.membersStartRow || i >= ProfileActivity.this.membersEndRow) {
                    return ProfileActivity.this.processOnClickOrPress(i, view, view.getX() + (view.getWidth() / 2.0f), view.getY() + (view.getHeight() / 2.0f));
                }
                return ProfileActivity.this.onMemberClick(!ProfileActivity.this.sortedUsers.isEmpty() ? (TLRPC$ChatParticipant) ProfileActivity.this.visibleChatParticipants.get(((Integer) ProfileActivity.this.sortedUsers.get(i - ProfileActivity.this.membersStartRow)).intValue()) : (TLRPC$ChatParticipant) ProfileActivity.this.visibleChatParticipants.get(i - ProfileActivity.this.membersStartRow), true);
            }
            int i6 = this.pressCount + 1;
            this.pressCount = i6;
            if (i6 >= 2 || BuildVars.DEBUG_PRIVATE_VERSION) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this.getParentActivity(), ProfileActivity.this.resourcesProvider);
                builder.setTitle(LocaleController.getString("DebugMenu", R.string.DebugMenu));
                CharSequence[] charSequenceArr = new CharSequence[23];
                charSequenceArr[0] = LocaleController.getString("DebugMenuImportContacts", R.string.DebugMenuImportContacts);
                charSequenceArr[1] = LocaleController.getString("DebugMenuReloadContacts", R.string.DebugMenuReloadContacts);
                charSequenceArr[2] = LocaleController.getString("DebugMenuResetContacts", R.string.DebugMenuResetContacts);
                charSequenceArr[3] = LocaleController.getString("DebugMenuResetDialogs", R.string.DebugMenuResetDialogs);
                if (BuildVars.DEBUG_VERSION) {
                    string = null;
                } else {
                    if (BuildVars.LOGS_ENABLED) {
                        i2 = R.string.DebugMenuDisableLogs;
                        str = "DebugMenuDisableLogs";
                    } else {
                        i2 = R.string.DebugMenuEnableLogs;
                        str = "DebugMenuEnableLogs";
                    }
                    string = LocaleController.getString(str, i2);
                }
                charSequenceArr[4] = string;
                if (SharedConfig.inappCamera) {
                    i3 = R.string.DebugMenuDisableCamera;
                    str2 = "DebugMenuDisableCamera";
                } else {
                    i3 = R.string.DebugMenuEnableCamera;
                    str2 = "DebugMenuEnableCamera";
                }
                charSequenceArr[5] = LocaleController.getString(str2, i3);
                charSequenceArr[6] = LocaleController.getString("DebugMenuClearMediaCache", R.string.DebugMenuClearMediaCache);
                charSequenceArr[7] = LocaleController.getString("DebugMenuCallSettings", R.string.DebugMenuCallSettings);
                charSequenceArr[8] = null;
                charSequenceArr[9] = (BuildVars.DEBUG_PRIVATE_VERSION || BuildVars.isStandaloneApp()) ? LocaleController.getString("DebugMenuCheckAppUpdate", R.string.DebugMenuCheckAppUpdate) : null;
                charSequenceArr[10] = LocaleController.getString("DebugMenuReadAllDialogs", R.string.DebugMenuReadAllDialogs);
                if (SharedConfig.pauseMusicOnRecord) {
                    i4 = R.string.DebugMenuDisablePauseMusic;
                    str3 = "DebugMenuDisablePauseMusic";
                } else {
                    i4 = R.string.DebugMenuEnablePauseMusic;
                    str3 = "DebugMenuEnablePauseMusic";
                }
                charSequenceArr[11] = LocaleController.getString(str3, i4);
                if (!BuildVars.DEBUG_VERSION || AndroidUtilities.isTablet() || Build.VERSION.SDK_INT < 23) {
                    str4 = null;
                } else {
                    if (SharedConfig.smoothKeyboard) {
                        i5 = R.string.DebugMenuDisableSmoothKeyboard;
                        str8 = "DebugMenuDisableSmoothKeyboard";
                    } else {
                        i5 = R.string.DebugMenuEnableSmoothKeyboard;
                        str8 = "DebugMenuEnableSmoothKeyboard";
                    }
                    str4 = LocaleController.getString(str8, i5);
                }
                charSequenceArr[12] = str4;
                charSequenceArr[13] = BuildVars.DEBUG_PRIVATE_VERSION ? SharedConfig.disableVoiceAudioEffects ? "Enable voip audio effects" : "Disable voip audio effects" : null;
                int i7 = Build.VERSION.SDK_INT;
                charSequenceArr[14] = i7 >= 21 ? SharedConfig.noStatusBar ? "Show status bar background" : "Hide status bar background" : null;
                boolean z = BuildVars.DEBUG_PRIVATE_VERSION;
                charSequenceArr[15] = z ? "Clean app update" : null;
                charSequenceArr[16] = z ? "Reset suggestions" : null;
                if (z) {
                    str5 = LocaleController.getString(SharedConfig.forceRtmpStream ? R.string.DebugMenuDisableForceRtmpStreamFlag : R.string.DebugMenuEnableForceRtmpStreamFlag);
                } else {
                    str5 = null;
                }
                charSequenceArr[17] = str5;
                charSequenceArr[18] = BuildVars.DEBUG_PRIVATE_VERSION ? LocaleController.getString(R.string.DebugMenuClearWebViewCache) : null;
                if (i7 >= 19) {
                    str6 = LocaleController.getString(SharedConfig.debugWebView ? R.string.DebugMenuDisableWebViewDebug : R.string.DebugMenuEnableWebViewDebug);
                } else {
                    str6 = null;
                }
                charSequenceArr[19] = str6;
                charSequenceArr[20] = (!AndroidUtilities.isTabletInternal() || !BuildVars.DEBUG_PRIVATE_VERSION) ? null : SharedConfig.forceDisableTabletMode ? "Enable tablet mode" : "Disable tablet mode";
                charSequenceArr[21] = LocaleController.getString(SharedConfig.useLNavigation ? R.string.AltNavigationDisable : R.string.AltNavigationEnable);
                if (BuildVars.DEBUG_PRIVATE_VERSION) {
                    str7 = LocaleController.getString(SharedConfig.isFloatingDebugActive ? R.string.FloatingDebugDisable : R.string.FloatingDebugEnable);
                } else {
                    str7 = null;
                }
                charSequenceArr[22] = str7;
                final Context context = this.val$context;
                builder.setItems(charSequenceArr, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ProfileActivity$15$$ExternalSyntheticLambda0
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i8) {
                        ProfileActivity.AnonymousClass15.this.lambda$onItemClick$0(context, dialogInterface, i8);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                ProfileActivity.this.showDialog(builder.create());
            } else {
                try {
                    Toast.makeText(ProfileActivity.this.getParentActivity(), "¯\\_(ツ)_/¯", 0).show();
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            return true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onItemClick$0(Context context, DialogInterface dialogInterface, int i) {
            if (i == 0) {
                ProfileActivity.this.getUserConfig().syncContacts = true;
                ProfileActivity.this.getUserConfig().saveConfig(false);
                ProfileActivity.this.getContactsController().forceImportContacts();
            } else if (i == 1) {
                ProfileActivity.this.getContactsController().loadContacts(false, 0L);
            } else if (i == 2) {
                ProfileActivity.this.getContactsController().resetImportedContacts();
            } else if (i == 3) {
                ProfileActivity.this.getMessagesController().forceResetDialogs();
            } else if (i == 4) {
                BuildVars.LOGS_ENABLED = !BuildVars.LOGS_ENABLED;
                ApplicationLoader.applicationContext.getSharedPreferences("systemConfig", 0).edit().putBoolean("logsEnabled", BuildVars.LOGS_ENABLED).commit();
                ProfileActivity.this.updateRowsIds();
                ProfileActivity.this.listAdapter.notifyDataSetChanged();
            } else if (i == 5) {
                SharedConfig.toggleInappCamera();
            } else if (i == 6) {
                ProfileActivity.this.getMessagesStorage().clearSentMedia();
                SharedConfig.setNoSoundHintShowed(false);
                MessagesController.getGlobalMainSettings().edit().remove("archivehint").remove("proximityhint").remove("archivehint_l").remove("gifhint").remove("reminderhint").remove("soundHint").remove("themehint").remove("bganimationhint").remove("filterhint").commit();
                MessagesController.getEmojiSettings(((BaseFragment) ProfileActivity.this).currentAccount).edit().remove("featured_hidden").commit();
                SharedConfig.textSelectionHintShows = 0;
                SharedConfig.lockRecordAudioVideoHint = 0;
                SharedConfig.stickersReorderingHintUsed = false;
                SharedConfig.forwardingOptionsHintShown = false;
                SharedConfig.messageSeenHintCount = 3;
                SharedConfig.emojiInteractionsHintCount = 3;
                SharedConfig.dayNightThemeSwitchHintCount = 3;
                SharedConfig.fastScrollHintCount = 3;
                ChatThemeController.getInstance(((BaseFragment) ProfileActivity.this).currentAccount).clearCache();
            } else if (i == 7) {
                VoIPHelper.showCallDebugSettings(ProfileActivity.this.getParentActivity());
            } else if (i == 8) {
                SharedConfig.toggleRoundCamera16to9();
            } else if (i == 9) {
                ((LaunchActivity) ProfileActivity.this.getParentActivity()).checkAppUpdate(true);
            } else if (i == 10) {
                ProfileActivity.this.getMessagesStorage().readAllDialogs(-1);
            } else if (i == 11) {
                SharedConfig.togglePauseMusicOnRecord();
            } else if (i == 12) {
                SharedConfig.toggleSmoothKeyboard();
                if (!SharedConfig.smoothKeyboard || ProfileActivity.this.getParentActivity() == null) {
                    return;
                }
                ProfileActivity.this.getParentActivity().getWindow().setSoftInputMode(16);
            } else if (i == 13) {
                SharedConfig.toggleDisableVoiceAudioEffects();
            } else if (i == 14) {
                SharedConfig.toggleNoStatusBar();
                if (ProfileActivity.this.getParentActivity() == null || Build.VERSION.SDK_INT < 21) {
                    return;
                }
                if (SharedConfig.noStatusBar) {
                    ProfileActivity.this.getParentActivity().getWindow().setStatusBarColor(0);
                } else {
                    ProfileActivity.this.getParentActivity().getWindow().setStatusBarColor(NUM);
                }
            } else if (i == 15) {
                SharedConfig.pendingAppUpdate = null;
                SharedConfig.saveConfig();
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.appUpdateAvailable, new Object[0]);
            } else if (i == 16) {
                Set<String> set = ProfileActivity.this.getMessagesController().pendingSuggestions;
                set.add("VALIDATE_PHONE_NUMBER");
                set.add("VALIDATE_PASSWORD");
                ProfileActivity.this.getNotificationCenter().postNotificationName(NotificationCenter.newSuggestionsAvailable, new Object[0]);
            } else if (i == 17) {
                SharedConfig.toggleForceRTMPStream();
            } else if (i == 18) {
                ApplicationLoader.applicationContext.deleteDatabase("webview.db");
                ApplicationLoader.applicationContext.deleteDatabase("webviewCache.db");
                WebStorage.getInstance().deleteAllData();
            } else if (i == 19) {
                SharedConfig.toggleDebugWebView();
                Toast.makeText(ProfileActivity.this.getParentActivity(), LocaleController.getString(SharedConfig.debugWebView ? R.string.DebugMenuWebViewDebugEnabled : R.string.DebugMenuWebViewDebugDisabled), 0).show();
            } else if (i == 20) {
                SharedConfig.toggleForceDisableTabletMode();
                Activity findActivity = AndroidUtilities.findActivity(context);
                Intent launchIntentForPackage = findActivity.getPackageManager().getLaunchIntentForPackage(findActivity.getPackageName());
                findActivity.finishAffinity();
                findActivity.startActivity(launchIntentForPackage);
                System.exit(0);
            } else if (i == 21) {
                SharedConfig.useLNavigation = !SharedConfig.useLNavigation;
                SharedConfig.saveConfig();
                ProfileActivity.this.getParentActivity().recreate();
            } else if (i != 22) {
            } else {
                FloatingDebugController.setActive((LaunchActivity) ProfileActivity.this.getParentActivity(), true ^ FloatingDebugController.isActive());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:29:0x00a2  */
    /* JADX WARN: Removed duplicated region for block: B:30:0x00a9  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$createView$8(android.view.View r8, int r9) {
        /*
            r7 = this;
            if (r9 >= 0) goto L3
            return
        L3:
            int r8 = r7.numberRow
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            org.telegram.ui.ProfileActivity$SearchAdapter r0 = r7.searchAdapter
            boolean r0 = org.telegram.ui.ProfileActivity.SearchAdapter.access$28300(r0)
            r1 = 0
            r2 = 1
            if (r0 == 0) goto L50
            org.telegram.ui.ProfileActivity$SearchAdapter r0 = r7.searchAdapter
            java.util.ArrayList r0 = org.telegram.ui.ProfileActivity.SearchAdapter.access$28400(r0)
            int r0 = r0.size()
            if (r9 >= r0) goto L2b
            org.telegram.ui.ProfileActivity$SearchAdapter r8 = r7.searchAdapter
            java.util.ArrayList r8 = org.telegram.ui.ProfileActivity.SearchAdapter.access$28400(r8)
            java.lang.Object r8 = r8.get(r9)
            goto L9d
        L2b:
            org.telegram.ui.ProfileActivity$SearchAdapter r0 = r7.searchAdapter
            java.util.ArrayList r0 = org.telegram.ui.ProfileActivity.SearchAdapter.access$28400(r0)
            int r0 = r0.size()
            int r0 = r0 + r2
            int r9 = r9 - r0
            if (r9 < 0) goto L9d
            org.telegram.ui.ProfileActivity$SearchAdapter r0 = r7.searchAdapter
            java.util.ArrayList r0 = org.telegram.ui.ProfileActivity.SearchAdapter.access$28500(r0)
            int r0 = r0.size()
            if (r9 >= r0) goto L9d
            org.telegram.ui.ProfileActivity$SearchAdapter r8 = r7.searchAdapter
            java.util.ArrayList r8 = org.telegram.ui.ProfileActivity.SearchAdapter.access$28500(r8)
            java.lang.Object r8 = r8.get(r9)
            goto L9d
        L50:
            org.telegram.ui.ProfileActivity$SearchAdapter r0 = r7.searchAdapter
            java.util.ArrayList r0 = org.telegram.ui.ProfileActivity.SearchAdapter.access$28200(r0)
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L5e
            int r9 = r9 + (-1)
        L5e:
            if (r9 < 0) goto L77
            org.telegram.ui.ProfileActivity$SearchAdapter r0 = r7.searchAdapter
            java.util.ArrayList r0 = org.telegram.ui.ProfileActivity.SearchAdapter.access$28200(r0)
            int r0 = r0.size()
            if (r9 >= r0) goto L77
            org.telegram.ui.ProfileActivity$SearchAdapter r8 = r7.searchAdapter
            java.util.ArrayList r8 = org.telegram.ui.ProfileActivity.SearchAdapter.access$28200(r8)
            java.lang.Object r8 = r8.get(r9)
            goto L9d
        L77:
            org.telegram.ui.ProfileActivity$SearchAdapter r0 = r7.searchAdapter
            java.util.ArrayList r0 = org.telegram.ui.ProfileActivity.SearchAdapter.access$28200(r0)
            int r0 = r0.size()
            int r0 = r0 + r2
            int r9 = r9 - r0
            if (r9 < 0) goto L9d
            org.telegram.ui.ProfileActivity$SearchAdapter r0 = r7.searchAdapter
            java.util.ArrayList r0 = org.telegram.ui.ProfileActivity.SearchAdapter.access$28600(r0)
            int r0 = r0.size()
            if (r9 >= r0) goto L9d
            org.telegram.ui.ProfileActivity$SearchAdapter r8 = r7.searchAdapter
            java.util.ArrayList r8 = org.telegram.ui.ProfileActivity.SearchAdapter.access$28600(r8)
            java.lang.Object r8 = r8.get(r9)
            r9 = 0
            goto L9e
        L9d:
            r9 = 1
        L9e:
            boolean r0 = r8 instanceof org.telegram.ui.ProfileActivity.SearchAdapter.SearchResult
            if (r0 == 0) goto La9
            r0 = r8
            org.telegram.ui.ProfileActivity$SearchAdapter$SearchResult r0 = (org.telegram.ui.ProfileActivity.SearchAdapter.SearchResult) r0
            org.telegram.ui.ProfileActivity.SearchAdapter.SearchResult.access$28700(r0)
            goto Lca
        La9:
            boolean r0 = r8 instanceof org.telegram.messenger.MessagesController.FaqSearchResult
            if (r0 == 0) goto Lca
            r0 = r8
            org.telegram.messenger.MessagesController$FaqSearchResult r0 = (org.telegram.messenger.MessagesController.FaqSearchResult) r0
            int r3 = r7.currentAccount
            org.telegram.messenger.NotificationCenter r3 = org.telegram.messenger.NotificationCenter.getInstance(r3)
            int r4 = org.telegram.messenger.NotificationCenter.openArticle
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            org.telegram.ui.ProfileActivity$SearchAdapter r6 = r7.searchAdapter
            org.telegram.tgnet.TLRPC$WebPage r6 = org.telegram.ui.ProfileActivity.SearchAdapter.access$28800(r6)
            r5[r1] = r6
            java.lang.String r0 = r0.url
            r5[r2] = r0
            r3.postNotificationName(r4, r5)
        Lca:
            if (r9 == 0) goto Ld3
            if (r8 == 0) goto Ld3
            org.telegram.ui.ProfileActivity$SearchAdapter r9 = r7.searchAdapter
            r9.addRecent(r8)
        Ld3:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.lambda$createView$8(android.view.View, int):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createView$10(View view, int i) {
        if (this.searchAdapter.isSearchWas() || this.searchAdapter.recentSearches.isEmpty()) {
            return false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity(), this.resourcesProvider);
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setMessage(LocaleController.getString("ClearSearch", R.string.ClearSearch));
        builder.setPositiveButton(LocaleController.getString("ClearButton", R.string.ClearButton), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda4
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                ProfileActivity.this.lambda$createView$9(dialogInterface, i2);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        showDialog(builder.create());
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$9(DialogInterface dialogInterface, int i) {
        this.searchAdapter.clearRecent();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$11(TLObject tLObject) {
        this.currentChannelParticipant = ((TLRPC$TL_channels_channelParticipant) tLObject).participant;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$12(final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda24
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.this.lambda$createView$11(tLObject);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$13(TLRPC$Chat tLRPC$Chat, View view) {
        long j = this.userId;
        long j2 = this.banFromGroup;
        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights = tLRPC$Chat.default_banned_rights;
        TLRPC$ChannelParticipant tLRPC$ChannelParticipant = this.currentChannelParticipant;
        ChatRightsEditActivity chatRightsEditActivity = new ChatRightsEditActivity(j, j2, null, tLRPC$TL_chatBannedRights, tLRPC$ChannelParticipant != null ? tLRPC$ChannelParticipant.banned_rights : null, "", 1, true, false, null);
        chatRightsEditActivity.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() { // from class: org.telegram.ui.ProfileActivity.18
            @Override // org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate
            public void didSetRights(int i, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights2, String str) {
                ProfileActivity.this.removeSelfFromStack();
            }

            @Override // org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate
            public void didChangeOwner(TLRPC$User tLRPC$User) {
                ProfileActivity.this.undoView.showWithAction(-ProfileActivity.this.chatId, ProfileActivity.this.currentChat.megagroup ? 10 : 9, tLRPC$User);
            }
        });
        presentFragment(chatRightsEditActivity);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$15(View view) {
        RecyclerView.ViewHolder findContainingViewHolder;
        Integer num;
        TLRPC$Document findDocument;
        final TLRPC$InputStickerSet inputStickerSet;
        TLRPC$TL_messages_stickerSet stickerSet;
        if (this.avatarBig != null) {
            return;
        }
        View view2 = null;
        TLRPC$TL_forumTopic tLRPC$TL_forumTopic = null;
        if (this.isTopic) {
            ArrayList<TLRPC$TL_forumTopic> topics = getMessagesController().getTopicsController().getTopics(this.chatId);
            if (topics == null) {
                return;
            }
            for (int i = 0; tLRPC$TL_forumTopic == null && i < topics.size(); i++) {
                TLRPC$TL_forumTopic tLRPC$TL_forumTopic2 = topics.get(i);
                if (tLRPC$TL_forumTopic2 != null && tLRPC$TL_forumTopic2.id == this.topicId) {
                    tLRPC$TL_forumTopic = tLRPC$TL_forumTopic2;
                }
            }
            if (tLRPC$TL_forumTopic == null) {
                return;
            }
            long j = tLRPC$TL_forumTopic.icon_emoji_id;
            if (j == 0 || (findDocument = AnimatedEmojiDrawable.findDocument(this.currentAccount, j)) == null || (inputStickerSet = MessageObject.getInputStickerSet(findDocument)) == null || (stickerSet = MediaDataController.getInstance(this.currentAccount).getStickerSet(inputStickerSet, false)) == null || stickerSet.set == null) {
                return;
            }
            BulletinFactory.of(this).createEmojiBulletin(findDocument, AndroidUtilities.replaceTags(LocaleController.formatString("TopicContainsEmojiPackSingle", R.string.TopicContainsEmojiPackSingle, stickerSet.set.title)), LocaleController.getString("ViewAction", R.string.ViewAction), new Runnable() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda26
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.this.lambda$createView$14(inputStickerSet);
                }
            }).show();
            return;
        }
        if (!AndroidUtilities.isTablet() && !this.isInLandscapeMode && this.avatarImage.getImageReceiver().hasNotThumb() && !AndroidUtilities.isAccessibilityScreenReaderEnabled()) {
            this.openingAvatar = true;
            this.allowPullingDown = true;
            int i2 = 0;
            while (true) {
                if (i2 >= this.listView.getChildCount()) {
                    break;
                }
                RecyclerListView recyclerListView = this.listView;
                if (recyclerListView.getChildAdapterPosition(recyclerListView.getChildAt(i2)) == 0) {
                    view2 = this.listView.getChildAt(i2);
                    break;
                }
                i2++;
            }
            if (view2 != null && (findContainingViewHolder = this.listView.findContainingViewHolder(view2)) != null && (num = this.positionToOffset.get(Integer.valueOf(findContainingViewHolder.getAdapterPosition()))) != null) {
                this.listView.smoothScrollBy(0, -(num.intValue() + ((this.listView.getPaddingTop() - view2.getTop()) - this.actionBar.getMeasuredHeight())), CubicBezierInterpolator.EASE_OUT_QUINT);
                return;
            }
        }
        openAvatar();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$14(TLRPC$InputStickerSet tLRPC$InputStickerSet) {
        ArrayList arrayList = new ArrayList(1);
        arrayList.add(tLRPC$InputStickerSet);
        showDialog(new EmojiPacksAlert(this, getParentActivity(), this.resourcesProvider, arrayList));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createView$16(View view) {
        if (this.avatarBig != null) {
            return false;
        }
        openAvatar();
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$17(View view) {
        if (this.writeButton.getTag() != null) {
            return;
        }
        onWriteButtonClick();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$18(ValueAnimator valueAnimator) {
        int themedColor;
        SimpleTextView[] simpleTextViewArr;
        int currentActionBarHeight = ActionBar.getCurrentActionBarHeight() + (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0);
        float[] fArr = this.expandAnimatorValues;
        float animatedFraction = valueAnimator.getAnimatedFraction();
        this.currentExpanAnimatorFracture = animatedFraction;
        float lerp = AndroidUtilities.lerp(fArr, animatedFraction);
        this.avatarContainer.setScaleX(this.avatarScale);
        this.avatarContainer.setScaleY(this.avatarScale);
        this.avatarContainer.setTranslationX(AndroidUtilities.lerp(this.avatarX, 0.0f, lerp));
        this.avatarContainer.setTranslationY(AndroidUtilities.lerp((float) Math.ceil(this.avatarY), 0.0f, lerp));
        this.avatarImage.setRoundRadius((int) AndroidUtilities.lerp(getSmallAvatarRoundRadius(), 0.0f, lerp));
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        if (actionBarMenuItem != null) {
            float f = 1.0f - lerp;
            actionBarMenuItem.setAlpha(f);
            this.searchItem.setScaleY(f);
            this.searchItem.setVisibility(0);
            ActionBarMenuItem actionBarMenuItem2 = this.searchItem;
            actionBarMenuItem2.setClickable(actionBarMenuItem2.getAlpha() > 0.5f);
            if (this.qrItem != null) {
                float dp = AndroidUtilities.dp(48.0f) * lerp;
                this.qrItem.setTranslationX(dp);
                this.avatarsViewPagerIndicatorView.setTranslationX(dp - AndroidUtilities.dp(48.0f));
            }
        }
        if (this.extraHeight > AndroidUtilities.dp(88.0f) && this.expandProgress < 0.33f) {
            refreshNameAndOnlineXY();
        }
        ScamDrawable scamDrawable = this.scamDrawable;
        if (scamDrawable != null) {
            scamDrawable.setColor(ColorUtils.blendARGB(getThemedColor("avatar_subtitleInProfileBlue"), Color.argb(179, 255, 255, 255), lerp));
        }
        Drawable drawable = this.lockIconDrawable;
        if (drawable != null) {
            drawable.setColorFilter(ColorUtils.blendARGB(getThemedColor("chat_lockIcon"), -1, lerp), PorterDuff.Mode.MULTIPLY);
        }
        CrossfadeDrawable crossfadeDrawable = this.verifiedCrossfadeDrawable;
        if (crossfadeDrawable != null) {
            crossfadeDrawable.setProgress(lerp);
        }
        CrossfadeDrawable crossfadeDrawable2 = this.premiumCrossfadeDrawable;
        if (crossfadeDrawable2 != null) {
            crossfadeDrawable2.setProgress(lerp);
        }
        updateEmojiStatusDrawableColor(lerp);
        float dpf2 = AndroidUtilities.dpf2(8.0f);
        float dpvar_ = AndroidUtilities.dpf2(16.0f) - this.nameTextView[1].getLeft();
        float f2 = currentActionBarHeight;
        float dpvar_ = ((this.extraHeight + f2) - AndroidUtilities.dpf2(38.0f)) - this.nameTextView[1].getBottom();
        float f3 = this.nameX;
        float f4 = this.nameY;
        float f5 = 1.0f - lerp;
        float f6 = f5 * f5;
        float f7 = f5 * 2.0f * lerp;
        float f8 = lerp * lerp;
        float f9 = (f3 * f6) + ((dpf2 + f3 + ((dpvar_ - f3) / 2.0f)) * f7) + (dpvar_ * f8);
        float var_ = (f4 * f6) + ((dpf2 + f4 + ((dpvar_ - f4) / 2.0f)) * f7) + (dpvar_ * f8);
        float dpvar_ = AndroidUtilities.dpf2(16.0f) - this.onlineTextView[1].getLeft();
        float dpvar_ = ((this.extraHeight + f2) - AndroidUtilities.dpf2(18.0f)) - this.onlineTextView[1].getBottom();
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
            themedColor = getThemedColor((String) tag);
        } else {
            themedColor = getThemedColor("avatar_subtitleInProfileBlue");
        }
        this.onlineTextView[1].setTextColor(ColorUtils.blendARGB(themedColor, Color.argb(179, 255, 255, 255), lerp));
        if (this.extraHeight > AndroidUtilities.dp(88.0f)) {
            this.nameTextView[1].setPivotY(AndroidUtilities.lerp(0, simpleTextViewArr[1].getMeasuredHeight(), lerp));
            this.nameTextView[1].setScaleX(AndroidUtilities.lerp(1.12f, 1.67f, lerp));
            this.nameTextView[1].setScaleY(AndroidUtilities.lerp(1.12f, 1.67f, lerp));
        }
        needLayoutText(Math.min(1.0f, this.extraHeight / AndroidUtilities.dp(88.0f)));
        this.nameTextView[1].setTextColor(ColorUtils.blendARGB(getThemedColor("profile_title"), -1, lerp));
        this.actionBar.setItemsColor(ColorUtils.blendARGB(getThemedColor("actionBarDefaultIcon"), -1, lerp), false);
        this.avatarImage.setForegroundAlpha(lerp);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.avatarContainer.getLayoutParams();
        layoutParams.width = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(42.0f), this.listView.getMeasuredWidth() / this.avatarScale, lerp);
        layoutParams.height = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(42.0f), (this.extraHeight + f2) / this.avatarScale, lerp);
        layoutParams.leftMargin = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(64.0f), 0.0f, lerp);
        this.avatarContainer.requestLayout();
    }

    private int getSmallAvatarRoundRadius() {
        if (getCurrentChat() != null && getCurrentChat().forum) {
            return AndroidUtilities.dp(16.0f);
        }
        return AndroidUtilities.dp(21.0f);
    }

    private void updateTtlIcon() {
        TLRPC$UserFull tLRPC$UserFull;
        if (this.ttlIconView == null) {
            return;
        }
        boolean z = false;
        if (this.currentEncryptedChat == null && (((tLRPC$UserFull = this.userInfo) != null && tLRPC$UserFull.ttl_period > 0) || (this.chatInfo != null && ChatObject.canUserDoAdminAction(this.currentChat, 13) && this.chatInfo.ttl_period > 0))) {
            z = true;
        }
        AndroidUtilities.updateViewVisibilityAnimated(this.ttlIconView, z, 0.8f, this.fragmentOpened);
    }

    public long getDialogId() {
        long j = this.dialogId;
        if (j != 0) {
            return j;
        }
        long j2 = this.userId;
        return j2 != 0 ? j2 : -this.chatId;
    }

    public void getEmojiStatusLocation(Rect rect) {
        SimpleTextView[] simpleTextViewArr = this.nameTextView;
        if (simpleTextViewArr[1] == null) {
            return;
        }
        if (simpleTextViewArr[1].getRightDrawable() == null) {
            rect.set(this.nameTextView[1].getWidth() - 1, (this.nameTextView[1].getHeight() / 2) - 1, this.nameTextView[1].getWidth() + 1, (this.nameTextView[1].getHeight() / 2) + 1);
            return;
        }
        rect.set(this.nameTextView[1].getRightDrawable().getBounds());
        rect.offset((int) (rect.centerX() * (this.nameTextView[1].getScaleX() - 1.0f)), 0);
        rect.offset((int) this.nameTextView[1].getX(), (int) this.nameTextView[1].getY());
    }

    public void showStatusSelect() {
        if (this.selectAnimatedEmojiDialog != null) {
            return;
        }
        SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow[] selectAnimatedEmojiDialogWindowArr = new SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow[1];
        Rect rect = AndroidUtilities.rectTmp2;
        getEmojiStatusLocation(rect);
        int i = this.nameTextView[1].getScaleX() < 1.5f ? 16 : 32;
        int dp = (-(this.avatarContainer2.getHeight() - rect.centerY())) - AndroidUtilities.dp(i);
        int min = (int) Math.min(AndroidUtilities.dp(324.0f), AndroidUtilities.displaySize.x * 0.95f);
        int centerX = rect.centerX();
        int clamp = MathUtils.clamp(centerX - (min / 2), 0, AndroidUtilities.displaySize.x - min);
        int i2 = centerX - clamp;
        AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable[] swapAnimatedEmojiDrawableArr = this.emojiStatusDrawable;
        if (swapAnimatedEmojiDrawableArr[1] != null) {
            boolean z = swapAnimatedEmojiDrawableArr[1].getDrawable() instanceof AnimatedEmojiDrawable;
        }
        AnonymousClass28 anonymousClass28 = new AnonymousClass28(this, getContext(), true, Integer.valueOf(Math.max(0, i2)), 0, this.resourcesProvider, i, selectAnimatedEmojiDialogWindowArr);
        TLRPC$User user = getMessagesController().getUser(Long.valueOf(this.userId));
        if (user != null) {
            TLRPC$EmojiStatus tLRPC$EmojiStatus = user.emoji_status;
            if ((tLRPC$EmojiStatus instanceof TLRPC$TL_emojiStatusUntil) && ((TLRPC$TL_emojiStatusUntil) tLRPC$EmojiStatus).until > ((int) (System.currentTimeMillis() / 1000))) {
                anonymousClass28.setExpireDateHint(((TLRPC$TL_emojiStatusUntil) user.emoji_status).until);
            }
        }
        AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable[] swapAnimatedEmojiDrawableArr2 = this.emojiStatusDrawable;
        anonymousClass28.setSelected((swapAnimatedEmojiDrawableArr2[1] == null || !(swapAnimatedEmojiDrawableArr2[1].getDrawable() instanceof AnimatedEmojiDrawable)) ? null : Long.valueOf(((AnimatedEmojiDrawable) this.emojiStatusDrawable[1].getDrawable()).getDocumentId()));
        anonymousClass28.setSaveState(3);
        anonymousClass28.setScrimDrawable(this.emojiStatusDrawable[1], this.nameTextView[1]);
        SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow selectAnimatedEmojiDialogWindow = new SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow(anonymousClass28, -2, -2) { // from class: org.telegram.ui.ProfileActivity.29
            @Override // org.telegram.ui.SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow, android.widget.PopupWindow
            public void dismiss() {
                super.dismiss();
                ProfileActivity.this.selectAnimatedEmojiDialog = null;
            }
        };
        this.selectAnimatedEmojiDialog = selectAnimatedEmojiDialogWindow;
        selectAnimatedEmojiDialogWindowArr[0] = selectAnimatedEmojiDialogWindow;
        int[] iArr = new int[2];
        SimpleTextView[] simpleTextViewArr = this.nameTextView;
        if (simpleTextViewArr[1] != null) {
            simpleTextViewArr[1].getLocationOnScreen(iArr);
        }
        selectAnimatedEmojiDialogWindowArr[0].showAsDropDown(this.fragmentView, clamp, dp, 51);
        selectAnimatedEmojiDialogWindowArr[0].dimBehind();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.ProfileActivity$28  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass28 extends SelectAnimatedEmojiDialog {
        final /* synthetic */ SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow[] val$popup;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass28(BaseFragment baseFragment, Context context, boolean z, Integer num, int i, Theme.ResourcesProvider resourcesProvider, int i2, SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow[] selectAnimatedEmojiDialogWindowArr) {
            super(baseFragment, context, z, num, i, resourcesProvider, i2);
            this.val$popup = selectAnimatedEmojiDialogWindowArr;
        }

        @Override // org.telegram.ui.SelectAnimatedEmojiDialog
        protected void onEmojiSelected(View view, Long l, TLRPC$Document tLRPC$Document, Integer num) {
            TLRPC$TL_account_updateEmojiStatus tLRPC$TL_account_updateEmojiStatus = new TLRPC$TL_account_updateEmojiStatus();
            if (l == null) {
                tLRPC$TL_account_updateEmojiStatus.emoji_status = new TLRPC$TL_emojiStatusEmpty();
            } else if (num != null) {
                TLRPC$TL_emojiStatusUntil tLRPC$TL_emojiStatusUntil = new TLRPC$TL_emojiStatusUntil();
                tLRPC$TL_account_updateEmojiStatus.emoji_status = tLRPC$TL_emojiStatusUntil;
                tLRPC$TL_emojiStatusUntil.document_id = l.longValue();
                ((TLRPC$TL_emojiStatusUntil) tLRPC$TL_account_updateEmojiStatus.emoji_status).until = num.intValue();
            } else {
                TLRPC$TL_emojiStatus tLRPC$TL_emojiStatus = new TLRPC$TL_emojiStatus();
                tLRPC$TL_account_updateEmojiStatus.emoji_status = tLRPC$TL_emojiStatus;
                tLRPC$TL_emojiStatus.document_id = l.longValue();
            }
            TLRPC$User user = MessagesController.getInstance(((BaseFragment) ProfileActivity.this).currentAccount).getUser(Long.valueOf(UserConfig.getInstance(((BaseFragment) ProfileActivity.this).currentAccount).getClientUserId()));
            if (user != null) {
                user.emoji_status = tLRPC$TL_account_updateEmojiStatus.emoji_status;
                MessagesController.getInstance(((BaseFragment) ProfileActivity.this).currentAccount).updateEmojiStatusUntilUpdate(user.id, user.emoji_status);
                NotificationCenter.getInstance(((BaseFragment) ProfileActivity.this).currentAccount).postNotificationName(NotificationCenter.userEmojiStatusUpdated, user);
            }
            for (int i = 0; i < 2; i++) {
                if (ProfileActivity.this.emojiStatusDrawable[i] != null) {
                    if (l == null) {
                        ProfileActivity.this.emojiStatusDrawable[i].set(ProfileActivity.this.getPremiumCrossfadeDrawable(), true);
                    } else {
                        ProfileActivity.this.emojiStatusDrawable[i].set(l.longValue(), true);
                    }
                }
            }
            if (l != null) {
                ProfileActivity.this.animatedStatusView.animateChange(ReactionsLayoutInBubble.VisibleReaction.fromCustomEmoji(l));
            }
            ProfileActivity.this.updateEmojiStatusDrawableColor();
            ProfileActivity.this.updateEmojiStatusEffectPosition();
            ConnectionsManager.getInstance(((BaseFragment) ProfileActivity.this).currentAccount).sendRequest(tLRPC$TL_account_updateEmojiStatus, ProfileActivity$28$$ExternalSyntheticLambda0.INSTANCE);
            if (this.val$popup[0] != null) {
                ProfileActivity.this.selectAnimatedEmojiDialog = null;
                this.val$popup[0].dismiss();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$onEmojiSelected$0(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            boolean z = tLObject instanceof TLRPC$TL_boolTrue;
        }
    }

    @Override // org.telegram.ui.Components.SharedMediaLayout.Delegate
    public TLRPC$Chat getCurrentChat() {
        return this.currentChat;
    }

    public TLRPC$UserFull getUserInfo() {
        return this.userInfo;
    }

    @Override // org.telegram.ui.Components.SharedMediaLayout.Delegate
    public boolean isFragmentOpened() {
        return this.isFragmentOpened;
    }

    private void openAvatar() {
        TLRPC$Chat chat;
        TLRPC$ChatPhoto tLRPC$ChatPhoto;
        ImageLocation imageLocation;
        if (this.listView.getScrollState() == 1) {
            return;
        }
        if (this.userId != 0) {
            TLRPC$User user = getMessagesController().getUser(Long.valueOf(this.userId));
            TLRPC$UserProfilePhoto tLRPC$UserProfilePhoto = user.photo;
            if (tLRPC$UserProfilePhoto == null || tLRPC$UserProfilePhoto.photo_big == null) {
                return;
            }
            PhotoViewer.getInstance().setParentActivity(this);
            TLRPC$UserProfilePhoto tLRPC$UserProfilePhoto2 = user.photo;
            int i = tLRPC$UserProfilePhoto2.dc_id;
            if (i != 0) {
                tLRPC$UserProfilePhoto2.photo_big.dc_id = i;
            }
            PhotoViewer.getInstance().openPhoto(user.photo.photo_big, this.provider);
        } else if (this.chatId == 0 || (tLRPC$ChatPhoto = (chat = getMessagesController().getChat(Long.valueOf(this.chatId))).photo) == null || tLRPC$ChatPhoto.photo_big == null) {
        } else {
            PhotoViewer.getInstance().setParentActivity(this);
            TLRPC$ChatPhoto tLRPC$ChatPhoto2 = chat.photo;
            int i2 = tLRPC$ChatPhoto2.dc_id;
            if (i2 != 0) {
                tLRPC$ChatPhoto2.photo_big.dc_id = i2;
            }
            TLRPC$ChatFull tLRPC$ChatFull = this.chatInfo;
            if (tLRPC$ChatFull != null) {
                TLRPC$Photo tLRPC$Photo = tLRPC$ChatFull.chat_photo;
                if ((tLRPC$Photo instanceof TLRPC$TL_photo) && !tLRPC$Photo.video_sizes.isEmpty()) {
                    imageLocation = ImageLocation.getForPhoto(this.chatInfo.chat_photo.video_sizes.get(0), this.chatInfo.chat_photo);
                    PhotoViewer.getInstance().openPhotoWithVideo(chat.photo.photo_big, imageLocation, this.provider);
                }
            }
            imageLocation = null;
            PhotoViewer.getInstance().openPhotoWithVideo(chat.photo.photo_big, imageLocation, this.provider);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onWriteButtonClick() {
        if (this.userId != 0) {
            boolean z = true;
            if (this.imageUpdater != null) {
                TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
                if (user == null) {
                    user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
                }
                if (user == null) {
                    return;
                }
                ImageUpdater imageUpdater = this.imageUpdater;
                TLRPC$UserProfilePhoto tLRPC$UserProfilePhoto = user.photo;
                if (tLRPC$UserProfilePhoto == null || tLRPC$UserProfilePhoto.photo_big == null || (tLRPC$UserProfilePhoto instanceof TLRPC$TL_userProfilePhotoEmpty)) {
                    z = false;
                }
                imageUpdater.openMenu(z, new Runnable() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda21
                    @Override // java.lang.Runnable
                    public final void run() {
                        ProfileActivity.this.lambda$onWriteButtonClick$19();
                    }
                }, new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda8
                    @Override // android.content.DialogInterface.OnDismissListener
                    public final void onDismiss(DialogInterface dialogInterface) {
                        ProfileActivity.this.lambda$onWriteButtonClick$20(dialogInterface);
                    }
                });
                this.cameraDrawable.setCurrentFrame(0);
                this.cameraDrawable.setCustomEndFrame(43);
                this.cellCameraDrawable.setCurrentFrame(0);
                this.cellCameraDrawable.setCustomEndFrame(43);
                this.writeButton.playAnimation();
                TextCell textCell = this.setAvatarCell;
                if (textCell == null) {
                    return;
                }
                textCell.getImageView().playAnimation();
                return;
            } else if (this.playProfileAnimation != 0 && (this.parentLayout.getFragmentStack().get(this.parentLayout.getFragmentStack().size() - 2) instanceof ChatActivity)) {
                finishFragment();
                return;
            } else {
                TLRPC$User user2 = getMessagesController().getUser(Long.valueOf(this.userId));
                if (user2 == null || (user2 instanceof TLRPC$TL_userEmpty)) {
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putLong("user_id", this.userId);
                if (!getMessagesController().checkCanOpenChat(bundle, this)) {
                    return;
                }
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
                if (!AndroidUtilities.isTablet()) {
                    return;
                }
                finishFragment();
                return;
            }
        }
        openDiscussion();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onWriteButtonClick$19() {
        MessagesController.getInstance(this.currentAccount).deleteUserPhoto(null);
        this.cameraDrawable.setCurrentFrame(0);
        this.cellCameraDrawable.setCurrentFrame(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onWriteButtonClick$20(DialogInterface dialogInterface) {
        if (!this.imageUpdater.isUploadingImage()) {
            this.cameraDrawable.setCustomEndFrame(86);
            this.cellCameraDrawable.setCustomEndFrame(86);
            this.writeButton.playAnimation();
            TextCell textCell = this.setAvatarCell;
            if (textCell == null) {
                return;
            }
            textCell.getImageView().playAnimation();
            return;
        }
        this.cameraDrawable.setCurrentFrame(0, false);
        this.cellCameraDrawable.setCurrentFrame(0, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openDiscussion() {
        TLRPC$ChatFull tLRPC$ChatFull = this.chatInfo;
        if (tLRPC$ChatFull == null || tLRPC$ChatFull.linked_chat_id == 0) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putLong("chat_id", this.chatInfo.linked_chat_id);
        if (!getMessagesController().checkCanOpenChat(bundle, this)) {
            return;
        }
        presentFragment(new ChatActivity(bundle));
    }

    public boolean onMemberClick(TLRPC$ChatParticipant tLRPC$ChatParticipant, boolean z) {
        return onMemberClick(tLRPC$ChatParticipant, z, false);
    }

    @Override // org.telegram.ui.Components.SharedMediaLayout.Delegate
    public boolean onMemberClick(final TLRPC$ChatParticipant tLRPC$ChatParticipant, boolean z, boolean z2) {
        boolean z3;
        boolean z4;
        boolean z5;
        boolean z6;
        TLRPC$ChannelParticipant tLRPC$ChannelParticipant;
        boolean z7;
        int i;
        String str;
        if (getParentActivity() == null) {
            return false;
        }
        if (z) {
            final TLRPC$User user = getMessagesController().getUser(Long.valueOf(tLRPC$ChatParticipant.user_id));
            if (user == null || tLRPC$ChatParticipant.user_id == getUserConfig().getClientUserId()) {
                return false;
            }
            this.selectedUser = tLRPC$ChatParticipant.user_id;
            final ArrayList arrayList = null;
            if (ChatObject.isChannel(this.currentChat)) {
                TLRPC$ChannelParticipant tLRPC$ChannelParticipant2 = ((TLRPC$TL_chatChannelParticipant) tLRPC$ChatParticipant).channelParticipant;
                getMessagesController().getUser(Long.valueOf(tLRPC$ChatParticipant.user_id));
                z3 = ChatObject.canAddAdmins(this.currentChat);
                if (z3 && ((tLRPC$ChannelParticipant2 instanceof TLRPC$TL_channelParticipantCreator) || ((tLRPC$ChannelParticipant2 instanceof TLRPC$TL_channelParticipantAdmin) && !tLRPC$ChannelParticipant2.can_edit))) {
                    z3 = false;
                }
                boolean z8 = ChatObject.canBlockUsers(this.currentChat) && ((!(tLRPC$ChannelParticipant2 instanceof TLRPC$TL_channelParticipantAdmin) && !(tLRPC$ChannelParticipant2 instanceof TLRPC$TL_channelParticipantCreator)) || tLRPC$ChannelParticipant2.can_edit);
                z5 = this.currentChat.gigagroup ? false : z8;
                z4 = tLRPC$ChannelParticipant2 instanceof TLRPC$TL_channelParticipantAdmin;
                boolean z9 = z8;
                tLRPC$ChannelParticipant = tLRPC$ChannelParticipant2;
                z6 = z9;
            } else {
                TLRPC$Chat tLRPC$Chat = this.currentChat;
                boolean z10 = tLRPC$Chat.creator || ((tLRPC$ChatParticipant instanceof TLRPC$TL_chatParticipant) && (ChatObject.canBlockUsers(tLRPC$Chat) || tLRPC$ChatParticipant.inviter_id == getUserConfig().getClientUserId()));
                z3 = this.currentChat.creator;
                z4 = tLRPC$ChatParticipant instanceof TLRPC$TL_chatParticipantAdmin;
                z5 = z3;
                z6 = z10;
                tLRPC$ChannelParticipant = null;
            }
            ArrayList arrayList2 = z2 ? null : new ArrayList();
            ArrayList arrayList3 = z2 ? null : new ArrayList();
            if (!z2) {
                arrayList = new ArrayList();
            }
            if (z3) {
                if (z2) {
                    return true;
                }
                if (z4) {
                    i = R.string.EditAdminRights;
                    str = "EditAdminRights";
                } else {
                    i = R.string.SetAsAdmin;
                    str = "SetAsAdmin";
                }
                arrayList2.add(LocaleController.getString(str, i));
                arrayList3.add(Integer.valueOf(R.drawable.msg_admins));
                arrayList.add(0);
            }
            if (z5) {
                if (z2) {
                    return true;
                }
                arrayList2.add(LocaleController.getString("ChangePermissions", R.string.ChangePermissions));
                arrayList3.add(Integer.valueOf(R.drawable.msg_permissions));
                arrayList.add(1);
            }
            if (!z6) {
                z7 = false;
            } else if (z2) {
                return true;
            } else {
                arrayList2.add(LocaleController.getString("KickFromGroup", R.string.KickFromGroup));
                arrayList3.add(Integer.valueOf(R.drawable.msg_remove));
                arrayList.add(2);
                z7 = true;
            }
            if (z2 || arrayList2.isEmpty()) {
                return false;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity(), this.resourcesProvider);
            final TLRPC$ChannelParticipant tLRPC$ChannelParticipant3 = tLRPC$ChannelParticipant;
            final boolean z11 = z4;
            builder.setItems((CharSequence[]) arrayList2.toArray(new CharSequence[0]), AndroidUtilities.toIntArray(arrayList3), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda6
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i2) {
                    ProfileActivity.this.lambda$onMemberClick$22(arrayList, tLRPC$ChatParticipant, tLRPC$ChannelParticipant3, user, z11, dialogInterface, i2);
                }
            });
            AlertDialog create = builder.create();
            showDialog(create);
            if (z7) {
                create.setItemColor(arrayList2.size() - 1, getThemedColor("dialogTextRed2"), getThemedColor("dialogRedIcon"));
            }
        } else if (tLRPC$ChatParticipant.user_id == getUserConfig().getClientUserId()) {
            return false;
        } else {
            Bundle bundle = new Bundle();
            bundle.putLong("user_id", tLRPC$ChatParticipant.user_id);
            bundle.putBoolean("preload_messages", true);
            presentFragment(new ProfileActivity(bundle));
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onMemberClick$22(ArrayList arrayList, final TLRPC$ChatParticipant tLRPC$ChatParticipant, final TLRPC$ChannelParticipant tLRPC$ChannelParticipant, final TLRPC$User tLRPC$User, final boolean z, DialogInterface dialogInterface, int i) {
        if (((Integer) arrayList.get(i)).intValue() == 2) {
            kickUser(this.selectedUser, tLRPC$ChatParticipant);
            return;
        }
        final int intValue = ((Integer) arrayList.get(i)).intValue();
        if (intValue != 1 || (!(tLRPC$ChannelParticipant instanceof TLRPC$TL_channelParticipantAdmin) && !(tLRPC$ChatParticipant instanceof TLRPC$TL_chatParticipantAdmin))) {
            if (tLRPC$ChannelParticipant != null) {
                openRightsEdit(intValue, tLRPC$User, tLRPC$ChatParticipant, tLRPC$ChannelParticipant.admin_rights, tLRPC$ChannelParticipant.banned_rights, tLRPC$ChannelParticipant.rank, z);
                return;
            } else {
                openRightsEdit(intValue, tLRPC$User, tLRPC$ChatParticipant, null, null, "", z);
                return;
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity(), this.resourcesProvider);
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setMessage(LocaleController.formatString("AdminWillBeRemoved", R.string.AdminWillBeRemoved, ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name)));
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda7
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface2, int i2) {
                ProfileActivity.this.lambda$onMemberClick$21(tLRPC$ChannelParticipant, intValue, tLRPC$User, tLRPC$ChatParticipant, z, dialogInterface2, i2);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        showDialog(builder.create());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onMemberClick$21(TLRPC$ChannelParticipant tLRPC$ChannelParticipant, int i, TLRPC$User tLRPC$User, TLRPC$ChatParticipant tLRPC$ChatParticipant, boolean z, DialogInterface dialogInterface, int i2) {
        if (tLRPC$ChannelParticipant != null) {
            openRightsEdit(i, tLRPC$User, tLRPC$ChatParticipant, tLRPC$ChannelParticipant.admin_rights, tLRPC$ChannelParticipant.banned_rights, tLRPC$ChannelParticipant.rank, z);
        } else {
            openRightsEdit(i, tLRPC$User, tLRPC$ChatParticipant, null, null, "", z);
        }
    }

    private void openRightsEdit(final int i, final TLRPC$User tLRPC$User, final TLRPC$ChatParticipant tLRPC$ChatParticipant, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, String str, final boolean z) {
        final boolean[] zArr = new boolean[1];
        ChatRightsEditActivity chatRightsEditActivity = new ChatRightsEditActivity(tLRPC$User.id, this.chatId, tLRPC$TL_chatAdminRights, this.currentChat.default_banned_rights, tLRPC$TL_chatBannedRights, str, i, true, false, null) { // from class: org.telegram.ui.ProfileActivity.30
            @Override // org.telegram.ui.ActionBar.BaseFragment
            public void onTransitionAnimationEnd(boolean z2, boolean z3) {
                if (z2 || !z3 || !zArr[0] || !BulletinFactory.canShowBulletin(ProfileActivity.this)) {
                    return;
                }
                BulletinFactory.createPromoteToAdminBulletin(ProfileActivity.this, tLRPC$User.first_name).show();
            }
        };
        chatRightsEditActivity.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() { // from class: org.telegram.ui.ProfileActivity.31
            @Override // org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate
            public void didSetRights(int i2, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights2, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights2, String str2) {
                boolean z2;
                TLRPC$ChatParticipant tLRPC$TL_chatParticipant;
                int i3 = i;
                int i4 = 0;
                if (i3 != 0) {
                    if (i3 != 1 || i2 != 0 || !ProfileActivity.this.currentChat.megagroup || ProfileActivity.this.chatInfo == null || ProfileActivity.this.chatInfo.participants == null) {
                        return;
                    }
                    int i5 = 0;
                    while (true) {
                        if (i5 >= ProfileActivity.this.chatInfo.participants.participants.size()) {
                            z2 = false;
                            break;
                        } else if (MessageObject.getPeerId(((TLRPC$TL_chatChannelParticipant) ProfileActivity.this.chatInfo.participants.participants.get(i5)).channelParticipant.peer) == tLRPC$ChatParticipant.user_id) {
                            ProfileActivity.this.chatInfo.participants_count--;
                            ProfileActivity.this.chatInfo.participants.participants.remove(i5);
                            z2 = true;
                            break;
                        } else {
                            i5++;
                        }
                    }
                    if (ProfileActivity.this.chatInfo != null && ProfileActivity.this.chatInfo.participants != null) {
                        while (true) {
                            if (i4 >= ProfileActivity.this.chatInfo.participants.participants.size()) {
                                break;
                            } else if (ProfileActivity.this.chatInfo.participants.participants.get(i4).user_id == tLRPC$ChatParticipant.user_id) {
                                ProfileActivity.this.chatInfo.participants.participants.remove(i4);
                                z2 = true;
                                break;
                            } else {
                                i4++;
                            }
                        }
                    }
                    if (!z2) {
                        return;
                    }
                    ProfileActivity.this.updateOnlineCount(true);
                    ProfileActivity.this.updateRowsIds();
                    ProfileActivity.this.listAdapter.notifyDataSetChanged();
                    return;
                }
                TLRPC$ChatParticipant tLRPC$ChatParticipant2 = tLRPC$ChatParticipant;
                if (tLRPC$ChatParticipant2 instanceof TLRPC$TL_chatChannelParticipant) {
                    TLRPC$TL_chatChannelParticipant tLRPC$TL_chatChannelParticipant = (TLRPC$TL_chatChannelParticipant) tLRPC$ChatParticipant2;
                    if (i2 == 1) {
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
                    TLRPC$ChatParticipant tLRPC$ChatParticipant3 = tLRPC$ChatParticipant;
                    tLRPC$Peer.user_id = tLRPC$ChatParticipant3.user_id;
                    tLRPC$ChannelParticipant.date = tLRPC$ChatParticipant3.date;
                    tLRPC$ChannelParticipant.banned_rights = tLRPC$TL_chatBannedRights2;
                    tLRPC$ChannelParticipant.admin_rights = tLRPC$TL_chatAdminRights2;
                    tLRPC$ChannelParticipant.rank = str2;
                } else if (tLRPC$ChatParticipant2 != null) {
                    if (i2 == 1) {
                        tLRPC$TL_chatParticipant = new TLRPC$TL_chatParticipantAdmin();
                    } else {
                        tLRPC$TL_chatParticipant = new TLRPC$TL_chatParticipant();
                    }
                    TLRPC$ChatParticipant tLRPC$ChatParticipant4 = tLRPC$ChatParticipant;
                    tLRPC$TL_chatParticipant.user_id = tLRPC$ChatParticipant4.user_id;
                    tLRPC$TL_chatParticipant.date = tLRPC$ChatParticipant4.date;
                    tLRPC$TL_chatParticipant.inviter_id = tLRPC$ChatParticipant4.inviter_id;
                    int indexOf = ProfileActivity.this.chatInfo.participants.participants.indexOf(tLRPC$ChatParticipant);
                    if (indexOf >= 0) {
                        ProfileActivity.this.chatInfo.participants.participants.set(indexOf, tLRPC$TL_chatParticipant);
                    }
                }
                if (i2 != 1 || z) {
                    return;
                }
                zArr[0] = true;
            }

            @Override // org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate
            public void didChangeOwner(TLRPC$User tLRPC$User2) {
                ProfileActivity.this.undoView.showWithAction(-ProfileActivity.this.chatId, ProfileActivity.this.currentChat.megagroup ? 10 : 9, tLRPC$User2);
            }
        });
        presentFragment(chatRightsEditActivity);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean processOnClickOrPress(final int i, final View view, final float f, final float f2) {
        TLRPC$Chat chat;
        String str;
        String str2;
        TLRPC$UserFull tLRPC$UserFull;
        View view2 = view;
        if (i == this.usernameRow || i == this.setUsernameRow) {
            if (this.userId != 0) {
                TLRPC$User user = getMessagesController().getUser(Long.valueOf(this.userId));
                if (user == null || (str = user.username) == null) {
                    return false;
                }
            } else if (this.chatId == 0 || (chat = getMessagesController().getChat(Long.valueOf(this.chatId))) == null || !ChatObject.isPublic(chat)) {
                return false;
            } else {
                str = chat.username;
            }
            if (this.userId == 0) {
                TLRPC$Chat chat2 = getMessagesController().getChat(Long.valueOf(this.chatId));
                String str3 = "https://" + getLink(ChatObject.getPublicUsername(chat2), this.topicId);
                showDialog(new AnonymousClass32(getParentActivity(), null, str3, false, str3, false));
            } else {
                try {
                    BulletinFactory.of(this).createCopyBulletin(LocaleController.getString("UsernameCopied", R.string.UsernameCopied), this.resourcesProvider).show();
                    ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", "@" + str));
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            return true;
        } else if (i == this.phoneRow || i == this.numberRow) {
            final TLRPC$User user2 = getMessagesController().getUser(Long.valueOf(this.userId));
            if (user2 == null || (str2 = user2.phone) == null || str2.length() == 0 || getParentActivity() == null) {
                return false;
            }
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = new ArrayList();
            if (i == this.phoneRow) {
                TLRPC$UserFull tLRPC$UserFull2 = this.userInfo;
                if (tLRPC$UserFull2 != null && tLRPC$UserFull2.phone_calls_available) {
                    arrayList3.add(Integer.valueOf(R.drawable.msg_calls));
                    arrayList.add(LocaleController.getString("CallViaTelegram", R.string.CallViaTelegram));
                    arrayList2.add(2);
                    if (Build.VERSION.SDK_INT >= 18 && this.userInfo.video_calls_available) {
                        arrayList3.add(Integer.valueOf(R.drawable.msg_videocall));
                        arrayList.add(LocaleController.getString("VideoCallViaTelegram", R.string.VideoCallViaTelegram));
                        arrayList2.add(3);
                    }
                }
                arrayList3.add(Integer.valueOf(R.drawable.msg_calls_regular));
                arrayList.add(LocaleController.getString("Call", R.string.Call));
                arrayList2.add(0);
            }
            arrayList3.add(Integer.valueOf(R.drawable.msg_copy));
            arrayList.add(LocaleController.getString("Copy", R.string.Copy));
            arrayList2.add(1);
            final AtomicReference atomicReference = new AtomicReference();
            ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(this, getContext(), R.drawable.popup_fixed_alert, this.resourcesProvider) { // from class: org.telegram.ui.ProfileActivity.33
                Path path = new Path();

                @Override // android.view.ViewGroup
                protected boolean drawChild(Canvas canvas, View view3, long j) {
                    canvas.save();
                    this.path.rewind();
                    RectF rectF = AndroidUtilities.rectTmp;
                    rectF.set(view3.getLeft(), view3.getTop(), view3.getRight(), view3.getBottom());
                    this.path.addRoundRect(rectF, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), Path.Direction.CW);
                    canvas.clipPath(this.path);
                    boolean drawChild = super.drawChild(canvas, view3, j);
                    canvas.restore();
                    return drawChild;
                }
            };
            actionBarPopupWindowLayout.setFitItems(true);
            for (int i2 = 0; i2 < arrayList3.size(); i2++) {
                final int intValue = ((Integer) arrayList2.get(i2)).intValue();
                ActionBarMenuItem.addItem(actionBarPopupWindowLayout, ((Integer) arrayList3.get(i2)).intValue(), (CharSequence) arrayList.get(i2), false, this.resourcesProvider).setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda13
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view3) {
                        ProfileActivity.this.lambda$processOnClickOrPress$23(atomicReference, intValue, user2, view3);
                    }
                });
            }
            ActionBarPopupWindow actionBarPopupWindow = new ActionBarPopupWindow(actionBarPopupWindowLayout, -2, -2);
            actionBarPopupWindow.setPauseNotifications(true);
            actionBarPopupWindow.setDismissAnimationDuration(220);
            actionBarPopupWindow.setOutsideTouchable(true);
            actionBarPopupWindow.setClippingEnabled(true);
            actionBarPopupWindow.setAnimationStyle(R.style.PopupContextAnimation);
            actionBarPopupWindow.setFocusable(true);
            actionBarPopupWindowLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
            actionBarPopupWindow.setInputMethodMode(2);
            actionBarPopupWindow.getContentView().setFocusableInTouchMode(true);
            atomicReference.set(actionBarPopupWindow);
            float f3 = f;
            float f4 = f2;
            while (view2 != getFragmentView()) {
                f3 += view2.getX();
                f4 += view2.getY();
                view2 = (View) view2.getParent();
            }
            if (AndroidUtilities.isTablet()) {
                ViewGroup view3 = this.parentLayout.getView();
                f3 += view3.getX() + view3.getPaddingLeft();
                f4 += view3.getY() + view3.getPaddingTop();
            }
            actionBarPopupWindow.showAtLocation(getFragmentView(), 0, (int) (f3 - (actionBarPopupWindowLayout.getMeasuredWidth() / 2.0f)), (int) f4);
            actionBarPopupWindow.dimBehind();
            return true;
        } else if (i != this.channelInfoRow && i != this.userInfoRow && i != this.locationRow && i != this.bioRow) {
            return false;
        } else {
            if (i == this.bioRow && ((tLRPC$UserFull = this.userInfo) == null || TextUtils.isEmpty(tLRPC$UserFull.about))) {
                return false;
            }
            if ((view2 instanceof AboutLinkCell) && ((AboutLinkCell) view2).onClick()) {
                return false;
            }
            String str4 = null;
            if (i == this.locationRow) {
                TLRPC$ChatFull tLRPC$ChatFull = this.chatInfo;
                if (tLRPC$ChatFull != null) {
                    TLRPC$ChannelLocation tLRPC$ChannelLocation = tLRPC$ChatFull.location;
                    if (tLRPC$ChannelLocation instanceof TLRPC$TL_channelLocation) {
                        str4 = ((TLRPC$TL_channelLocation) tLRPC$ChannelLocation).address;
                    }
                }
            } else if (i == this.channelInfoRow) {
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
            final String str5 = str4;
            if (TextUtils.isEmpty(str5)) {
                return false;
            }
            final String[] strArr = {"und"};
            final boolean z = MessagesController.getGlobalMainSettings().getBoolean("translate_button", false);
            final boolean[] zArr = new boolean[1];
            zArr[0] = i == this.bioRow || i == this.channelInfoRow || i == this.userInfoRow;
            final String language = LocaleController.getInstance().getCurrentLocale().getLanguage();
            final Runnable runnable = new Runnable() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda33
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.this.lambda$processOnClickOrPress$26(zArr, str5, i, strArr, language, f, f2, view);
                }
            };
            if (zArr[0]) {
                if (LanguageDetector.hasSupport()) {
                    LanguageDetector.detectLanguage(str5, new LanguageDetector.StringCallback() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda37
                        @Override // org.telegram.messenger.LanguageDetector.StringCallback
                        public final void run(String str6) {
                            ProfileActivity.this.lambda$processOnClickOrPress$27(strArr, zArr, language, z, runnable, str6);
                        }
                    }, new LanguageDetector.ExceptionCallback() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda36
                        @Override // org.telegram.messenger.LanguageDetector.ExceptionCallback
                        public final void run(Exception exc) {
                            ProfileActivity.lambda$processOnClickOrPress$28(runnable, exc);
                        }
                    });
                } else {
                    runnable.run();
                }
            } else {
                runnable.run();
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.ProfileActivity$32  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass32 extends ShareAlert {
        AnonymousClass32(Context context, ArrayList arrayList, String str, boolean z, String str2, boolean z2) {
            super(context, arrayList, str, z, str2, z2);
        }

        @Override // org.telegram.ui.Components.ShareAlert
        protected void onSend(final LongSparseArray<TLRPC$Dialog> longSparseArray, final int i, TLRPC$TL_forumTopic tLRPC$TL_forumTopic) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ProfileActivity$32$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.AnonymousClass32.this.lambda$onSend$0(longSparseArray, i);
                }
            }, 250L);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onSend$0(LongSparseArray longSparseArray, int i) {
            BulletinFactory.createInviteSentBulletin(ProfileActivity.this.getParentActivity(), ProfileActivity.this.contentView, longSparseArray.size(), longSparseArray.size() == 1 ? ((TLRPC$Dialog) longSparseArray.valueAt(0)).id : 0L, i, getThemedColor("undo_background"), getThemedColor("undo_infoColor")).show();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$processOnClickOrPress$23(AtomicReference atomicReference, int i, TLRPC$User tLRPC$User, View view) {
        ((ActionBarPopupWindow) atomicReference.get()).dismiss();
        if (i == 0) {
            try {
                Intent intent = new Intent("android.intent.action.DIAL", Uri.parse("tel:+" + tLRPC$User.phone));
                intent.addFlags(NUM);
                getParentActivity().startActivityForResult(intent, 500);
            } catch (Exception e) {
                FileLog.e(e);
            }
        } else if (i != 1) {
            if (i != 2 && i != 3) {
                return;
            }
            boolean z = i == 3;
            TLRPC$UserFull tLRPC$UserFull = this.userInfo;
            VoIPHelper.startCall(tLRPC$User, z, tLRPC$UserFull != null && tLRPC$UserFull.video_calls_available, getParentActivity(), this.userInfo, getAccountInstance());
        } else {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", "+" + tLRPC$User.phone));
                if (!AndroidUtilities.shouldShowClipboardToast()) {
                    return;
                }
                BulletinFactory.of(this).createCopyBulletin(LocaleController.getString("PhoneCopied", R.string.PhoneCopied)).show();
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$processOnClickOrPress$26(boolean[] zArr, final String str, final int i, final String[] strArr, final String str2, float f, float f2, View view) {
        if (getParentActivity() == null) {
            return;
        }
        boolean z = false;
        CharSequence[] charSequenceArr = zArr[0] ? new CharSequence[]{LocaleController.getString("Copy", R.string.Copy), LocaleController.getString("TranslateMessage", R.string.TranslateMessage)} : new CharSequence[]{LocaleController.getString("Copy", R.string.Copy)};
        int[] iArr = zArr[0] ? new int[]{R.drawable.msg_copy, R.drawable.msg_translate} : new int[]{R.drawable.msg_copy};
        final AtomicReference atomicReference = new AtomicReference();
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(this, getContext(), R.drawable.popup_fixed_alert, this.resourcesProvider) { // from class: org.telegram.ui.ProfileActivity.34
            Path path = new Path();

            @Override // android.view.ViewGroup
            protected boolean drawChild(Canvas canvas, View view2, long j) {
                canvas.save();
                this.path.rewind();
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(view2.getLeft(), view2.getTop(), view2.getRight(), view2.getBottom());
                this.path.addRoundRect(rectF, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), Path.Direction.CW);
                canvas.clipPath(this.path);
                boolean drawChild = super.drawChild(canvas, view2, j);
                canvas.restore();
                return drawChild;
            }
        };
        actionBarPopupWindowLayout.setFitItems(true);
        int i2 = 0;
        while (i2 < iArr.length) {
            final int i3 = i2;
            ActionBarMenuItem.addItem(actionBarPopupWindowLayout, iArr[i2], charSequenceArr[i2], z, this.resourcesProvider).setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda12
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    ProfileActivity.this.lambda$processOnClickOrPress$25(atomicReference, i3, str, i, strArr, str2, view2);
                }
            });
            i2++;
            z = false;
        }
        ActionBarPopupWindow actionBarPopupWindow = new ActionBarPopupWindow(actionBarPopupWindowLayout, -2, -2);
        actionBarPopupWindow.setPauseNotifications(true);
        actionBarPopupWindow.setDismissAnimationDuration(220);
        actionBarPopupWindow.setOutsideTouchable(true);
        actionBarPopupWindow.setClippingEnabled(true);
        actionBarPopupWindow.setAnimationStyle(R.style.PopupContextAnimation);
        actionBarPopupWindow.setFocusable(true);
        actionBarPopupWindowLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
        actionBarPopupWindow.setInputMethodMode(2);
        actionBarPopupWindow.getContentView().setFocusableInTouchMode(true);
        atomicReference.set(actionBarPopupWindow);
        float f3 = f;
        float f4 = f2;
        for (View view2 = view; view2 != getFragmentView(); view2 = (View) view2.getParent()) {
            f3 += view2.getX();
            f4 += view2.getY();
        }
        if (AndroidUtilities.isTablet()) {
            ViewGroup view3 = this.parentLayout.getView();
            f3 += view3.getX() + view3.getPaddingLeft();
            f4 += view3.getY() + view3.getPaddingTop();
        }
        actionBarPopupWindow.showAtLocation(getFragmentView(), 0, (int) (f3 - (actionBarPopupWindowLayout.getMeasuredWidth() / 2.0f)), (int) f4);
        actionBarPopupWindow.dimBehind();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$processOnClickOrPress$25(AtomicReference atomicReference, int i, String str, int i2, String[] strArr, String str2, View view) {
        ((ActionBarPopupWindow) atomicReference.get()).dismiss();
        try {
            if (i == 0) {
                AndroidUtilities.addToClipboard(str);
                if (i2 == this.bioRow) {
                    BulletinFactory.of(this).createCopyBulletin(LocaleController.getString("BioCopied", R.string.BioCopied)).show();
                } else {
                    BulletinFactory.of(this).createCopyBulletin(LocaleController.getString("TextCopied", R.string.TextCopied)).show();
                }
            } else if (i != 1) {
            } else {
                TranslateAlert.showAlert(this.fragmentView.getContext(), this, strArr[0], str2, str, false, new TranslateAlert.OnLinkPress() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda47
                    @Override // org.telegram.ui.Components.TranslateAlert.OnLinkPress
                    public final boolean run(URLSpan uRLSpan) {
                        boolean lambda$processOnClickOrPress$24;
                        lambda$processOnClickOrPress$24 = ProfileActivity.this.lambda$processOnClickOrPress$24(uRLSpan);
                        return lambda$processOnClickOrPress$24;
                    }
                }, null);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$processOnClickOrPress$24(URLSpan uRLSpan) {
        if (uRLSpan != null) {
            openUrl(uRLSpan.getURL());
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$processOnClickOrPress$27(String[] strArr, boolean[] zArr, String str, boolean z, Runnable runnable, String str2) {
        TLRPC$Chat tLRPC$Chat;
        strArr[0] = str2;
        zArr[0] = str2 != null && (!str2.equals(str) || str2.equals("und")) && ((z && !RestrictedLanguagesSelectActivity.getRestrictedLanguages().contains(str2)) || ((tLRPC$Chat = this.currentChat) != null && ((tLRPC$Chat.has_link || ChatObject.isPublic(tLRPC$Chat)) && ("uk".equals(str2) || "ru".equals(str2)))));
        runnable.run();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$processOnClickOrPress$28(Runnable runnable, Exception exc) {
        FileLog.e("mlkit: failed to detect language in selection", exc);
        runnable.run();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void leaveChatPressed() {
        AlertsCreator.createClearOrDeleteDialogAlert(this, false, this.currentChat, null, false, false, true, new MessagesStorage.BooleanCallback() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda38
            @Override // org.telegram.messenger.MessagesStorage.BooleanCallback
            public final void run(boolean z) {
                ProfileActivity.this.lambda$leaveChatPressed$29(z);
            }
        }, this.resourcesProvider);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$leaveChatPressed$29(boolean z) {
        this.playProfileAnimation = 0;
        NotificationCenter notificationCenter = getNotificationCenter();
        int i = NotificationCenter.closeChats;
        notificationCenter.removeObserver(this, i);
        getNotificationCenter().postNotificationName(i, new Object[0]);
        finishFragment();
        getNotificationCenter().postNotificationName(NotificationCenter.needDeleteDialog, Long.valueOf(-this.currentChat.id), null, this.currentChat, Boolean.valueOf(z));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getChannelParticipants(boolean z) {
        LongSparseArray<TLRPC$ChatParticipant> longSparseArray;
        if (this.loadingUsers || (longSparseArray = this.participantsMap) == null || this.chatInfo == null) {
            return;
        }
        this.loadingUsers = true;
        int i = 0;
        final int i2 = (longSparseArray.size() == 0 || !z) ? 0 : 300;
        final TLRPC$TL_channels_getParticipants tLRPC$TL_channels_getParticipants = new TLRPC$TL_channels_getParticipants();
        tLRPC$TL_channels_getParticipants.channel = getMessagesController().getInputChannel(this.chatId);
        tLRPC$TL_channels_getParticipants.filter = new TLRPC$TL_channelParticipantsRecent();
        if (!z) {
            i = this.participantsMap.size();
        }
        tLRPC$TL_channels_getParticipants.offset = i;
        tLRPC$TL_channels_getParticipants.limit = 200;
        getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tLRPC$TL_channels_getParticipants, new RequestDelegate() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda42
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                ProfileActivity.this.lambda$getChannelParticipants$31(tLRPC$TL_channels_getParticipants, i2, tLObject, tLRPC$TL_error);
            }
        }), this.classGuid);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getChannelParticipants$31(final TLRPC$TL_channels_getParticipants tLRPC$TL_channels_getParticipants, int i, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda28
            @Override // java.lang.Runnable
            public final void run() {
                ProfileActivity.this.lambda$getChannelParticipants$30(tLRPC$TL_error, tLObject, tLRPC$TL_channels_getParticipants);
            }
        }, i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getChannelParticipants$30(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, TLRPC$TL_channels_getParticipants tLRPC$TL_channels_getParticipants) {
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
        if (this.mediaHeaderVisible == z) {
            return;
        }
        this.mediaHeaderVisible = z;
        AnimatorSet animatorSet = this.headerAnimatorSet;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        AnimatorSet animatorSet2 = this.headerShadowAnimatorSet;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
        }
        final ActionBarMenuItem searchItem = this.sharedMediaLayout.getSearchItem();
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
                searchItem.setVisibility(0);
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
        fArr5[0] = z ? -AndroidUtilities.dp(10.0f) : 0.0f;
        arrayList.add(ObjectAnimator.ofFloat(actionBarMenuItem5, property5, fArr5));
        ActionBarMenuItem actionBarMenuItem6 = this.videoCallItem;
        Property property6 = View.TRANSLATION_Y;
        float[] fArr6 = new float[1];
        fArr6[0] = z ? -AndroidUtilities.dp(10.0f) : 0.0f;
        arrayList.add(ObjectAnimator.ofFloat(actionBarMenuItem6, property6, fArr6));
        ActionBarMenuItem actionBarMenuItem7 = this.otherItem;
        Property property7 = View.TRANSLATION_Y;
        float[] fArr7 = new float[1];
        fArr7[0] = z ? -AndroidUtilities.dp(10.0f) : 0.0f;
        arrayList.add(ObjectAnimator.ofFloat(actionBarMenuItem7, property7, fArr7));
        ActionBarMenuItem actionBarMenuItem8 = this.editItem;
        Property property8 = View.TRANSLATION_Y;
        float[] fArr8 = new float[1];
        fArr8[0] = z ? -AndroidUtilities.dp(10.0f) : 0.0f;
        arrayList.add(ObjectAnimator.ofFloat(actionBarMenuItem8, property8, fArr8));
        Property property9 = View.ALPHA;
        float[] fArr9 = new float[1];
        fArr9[0] = z ? 1.0f : 0.0f;
        arrayList.add(ObjectAnimator.ofFloat(searchItem, property9, fArr9));
        Property property10 = View.TRANSLATION_Y;
        float[] fArr10 = new float[1];
        fArr10[0] = z ? 0.0f : AndroidUtilities.dp(10.0f);
        arrayList.add(ObjectAnimator.ofFloat(searchItem, property10, fArr10));
        ImageView imageView = this.sharedMediaLayout.photoVideoOptionsItem;
        Property property11 = View.ALPHA;
        float[] fArr11 = new float[1];
        fArr11[0] = z ? 1.0f : 0.0f;
        arrayList.add(ObjectAnimator.ofFloat(imageView, property11, fArr11));
        ImageView imageView2 = this.sharedMediaLayout.photoVideoOptionsItem;
        Property property12 = View.TRANSLATION_Y;
        float[] fArr12 = new float[1];
        fArr12[0] = z ? 0.0f : AndroidUtilities.dp(10.0f);
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
            arrayList.add(ObjectAnimator.ofFloat(this, this.HEADER_SHADOW, 0.0f));
        }
        AnimatorSet animatorSet3 = new AnimatorSet();
        this.headerAnimatorSet = animatorSet3;
        animatorSet3.playTogether(arrayList);
        this.headerAnimatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.headerAnimatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ProfileActivity.36
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
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
                            searchItem.setVisibility(0);
                        }
                        ProfileActivity.this.sharedMediaLayout.photoVideoOptionsItem.setVisibility(4);
                        ProfileActivity.this.headerShadowAnimatorSet = new AnimatorSet();
                        AnimatorSet animatorSet4 = ProfileActivity.this.headerShadowAnimatorSet;
                        ProfileActivity profileActivity = ProfileActivity.this;
                        animatorSet4.playTogether(ObjectAnimator.ofFloat(profileActivity, profileActivity.HEADER_SHADOW, 1.0f));
                        ProfileActivity.this.headerShadowAnimatorSet.setDuration(100L);
                        ProfileActivity.this.headerShadowAnimatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ProfileActivity.36.1
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animator2) {
                                ProfileActivity.this.headerShadowAnimatorSet = null;
                            }
                        });
                        ProfileActivity.this.headerShadowAnimatorSet.start();
                    }
                }
                ProfileActivity.this.headerAnimatorSet = null;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                ProfileActivity.this.headerAnimatorSet = null;
            }
        });
        this.headerAnimatorSet.setDuration(150L);
        this.headerAnimatorSet.start();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needCheckSystemBarColors, Boolean.TRUE);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openAddMember() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("addToGroup", true);
        bundle.putLong("chatId", this.currentChat.id);
        GroupCreateActivity groupCreateActivity = new GroupCreateActivity(bundle);
        groupCreateActivity.setInfo(this.chatInfo);
        TLRPC$ChatFull tLRPC$ChatFull = this.chatInfo;
        if (tLRPC$ChatFull != null && tLRPC$ChatFull.participants != null) {
            LongSparseArray<TLObject> longSparseArray = new LongSparseArray<>();
            for (int i = 0; i < this.chatInfo.participants.participants.size(); i++) {
                longSparseArray.put(this.chatInfo.participants.participants.get(i).user_id, null);
            }
            groupCreateActivity.setIgnoreUsers(longSparseArray);
        }
        groupCreateActivity.setDelegate(new GroupCreateActivity.ContactsAddActivityDelegate() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda48
            @Override // org.telegram.ui.GroupCreateActivity.ContactsAddActivityDelegate
            public final void didSelectUsers(ArrayList arrayList, int i2) {
                ProfileActivity.this.lambda$openAddMember$33(arrayList, i2);
            }

            @Override // org.telegram.ui.GroupCreateActivity.ContactsAddActivityDelegate
            public /* synthetic */ void needAddBot(TLRPC$User tLRPC$User) {
                GroupCreateActivity.ContactsAddActivityDelegate.CC.$default$needAddBot(this, tLRPC$User);
            }
        });
        presentFragment(groupCreateActivity);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$openAddMember$33(final ArrayList arrayList, int i) {
        HashSet hashSet = new HashSet();
        if (this.chatInfo.participants.participants != null) {
            for (int i2 = 0; i2 < this.chatInfo.participants.participants.size(); i2++) {
                hashSet.add(Long.valueOf(this.chatInfo.participants.participants.get(i2).user_id));
            }
        }
        final int size = arrayList.size();
        final int[] iArr = new int[1];
        for (int i3 = 0; i3 < size; i3++) {
            TLRPC$User tLRPC$User = (TLRPC$User) arrayList.get(i3);
            getMessagesController().addUserToChat(this.chatId, tLRPC$User, i, null, this, new Runnable() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda31
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.this.lambda$openAddMember$32(iArr, size, arrayList);
                }
            });
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

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$openAddMember$32(int[] iArr, int i, ArrayList arrayList) {
        int i2 = iArr[0] + 1;
        iArr[0] = i2;
        if (i2 == i) {
            BulletinFactory.of(this).createUsersAddedBulletin(arrayList, this.currentChat).show();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkListViewScroll() {
        RecyclerListView.Holder holder;
        View view;
        if (this.listView.getVisibility() != 0) {
            return;
        }
        if (this.sharedMediaLayoutAttached) {
            this.sharedMediaLayout.setVisibleHeight(this.listView.getMeasuredHeight() - this.sharedMediaLayout.getTop());
        }
        if (this.listView.getChildCount() <= 0 || this.openAnimationInProgress) {
            return;
        }
        boolean z = false;
        int i = 0;
        while (true) {
            holder = null;
            if (i >= this.listView.getChildCount()) {
                view = null;
                break;
            }
            RecyclerListView recyclerListView = this.listView;
            if (recyclerListView.getChildAdapterPosition(recyclerListView.getChildAt(i)) == 0) {
                view = this.listView.getChildAt(i);
                break;
            }
            i++;
        }
        if (view != null) {
            holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(view);
        }
        int top = view == null ? 0 : view.getTop();
        int adapterPosition = holder != null ? holder.getAdapterPosition() : -1;
        if (top < 0 || adapterPosition != 0) {
            top = 0;
        }
        boolean z2 = this.imageUpdater == null && this.actionBar.isSearchFieldVisible();
        int i2 = this.sharedMediaRow;
        if (i2 != -1 && !z2) {
            RecyclerListView.Holder holder2 = (RecyclerListView.Holder) this.listView.findViewHolderForAdapterPosition(i2);
            z2 = holder2 != null && holder2.itemView.getTop() <= 0;
        }
        setMediaHeaderVisible(z2);
        float f = top;
        if (this.extraHeight == f || this.transitionAnimationInProress) {
            return;
        }
        this.extraHeight = f;
        this.topView.invalidate();
        if (this.playProfileAnimation != 0) {
            if (this.extraHeight != 0.0f) {
                z = true;
            }
            this.allowProfileAnimation = z;
        }
        needLayout(true);
    }

    @Override // org.telegram.ui.Components.SharedMediaLayout.Delegate
    public void updateSelectedMediaTabText() {
        SharedMediaLayout sharedMediaLayout = this.sharedMediaLayout;
        if (sharedMediaLayout == null || this.mediaCounterTextView == null) {
            return;
        }
        int closestTab = sharedMediaLayout.getClosestTab();
        int[] lastMediaCount = this.sharedMediaPreloader.getLastMediaCount();
        if (closestTab == 0) {
            if (lastMediaCount[7] == 0 && lastMediaCount[6] == 0) {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("Media", lastMediaCount[0], new Object[0]));
            } else if (this.sharedMediaLayout.getPhotosVideosTypeFilter() == 1 || lastMediaCount[7] == 0) {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("Photos", lastMediaCount[6], new Object[0]));
            } else if (this.sharedMediaLayout.getPhotosVideosTypeFilter() == 2 || lastMediaCount[6] == 0) {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("Videos", lastMediaCount[7], new Object[0]));
            } else {
                this.mediaCounterTextView.setText(String.format("%s, %s", LocaleController.formatPluralString("Photos", lastMediaCount[6], new Object[0]), LocaleController.formatPluralString("Videos", lastMediaCount[7], new Object[0])));
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
        } else if (closestTab != 7) {
        } else {
            this.mediaCounterTextView.setText(this.onlineTextView[1].getText());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void needLayout(boolean z) {
        OverlaysView overlaysView;
        BackupImageView currentItemView;
        ValueAnimator valueAnimator;
        SimpleTextView[] simpleTextViewArr;
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
            float min = Math.min(1.0f, this.extraHeight / AndroidUtilities.dp(88.0f));
            this.listView.setTopGlowOffset((int) this.extraHeight);
            this.listView.setOverScrollMode((this.extraHeight <= ((float) AndroidUtilities.dp(88.0f)) || this.extraHeight >= ((float) (this.listView.getMeasuredWidth() - currentActionBarHeight))) ? 0 : 2);
            RLottieImageView rLottieImageView = this.writeButton;
            if (rLottieImageView != null) {
                rLottieImageView.setTranslationY(((((this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight()) + this.extraHeight) + this.searchTransitionOffset) - AndroidUtilities.dp(29.5f));
                if (!this.openAnimationInProgress) {
                    float f = 0.2f;
                    boolean z2 = min > 0.2f && !this.searchMode && (this.imageUpdater == null || this.setAvatarRow == -1);
                    if (z2 && this.chatId != 0) {
                        z2 = (!ChatObject.isChannel(this.currentChat) || this.currentChat.megagroup || (tLRPC$ChatFull = this.chatInfo) == null || tLRPC$ChatFull.linked_chat_id == 0 || this.infoHeaderRow == -1) ? false : true;
                    }
                    if (z2 != (this.writeButton.getTag() == null)) {
                        if (z2) {
                            this.writeButton.setTag(null);
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
                                this.writeButtonAnimation.playTogether(ObjectAnimator.ofFloat(this.writeButton, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.writeButton, View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.writeButton, View.ALPHA, 1.0f));
                            } else {
                                animatorSet2.setInterpolator(new AccelerateInterpolator());
                                this.writeButtonAnimation.playTogether(ObjectAnimator.ofFloat(this.writeButton, View.SCALE_X, 0.2f), ObjectAnimator.ofFloat(this.writeButton, View.SCALE_Y, 0.2f), ObjectAnimator.ofFloat(this.writeButton, View.ALPHA, 0.0f));
                            }
                            this.writeButtonAnimation.setDuration(150L);
                            this.writeButtonAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ProfileActivity.37
                                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                                public void onAnimationEnd(Animator animator) {
                                    if (ProfileActivity.this.writeButtonAnimation == null || !ProfileActivity.this.writeButtonAnimation.equals(animator)) {
                                        return;
                                    }
                                    ProfileActivity.this.writeButtonAnimation = null;
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
                            float dp = AndroidUtilities.dp(48.0f) * this.qrItem.getAlpha();
                            this.qrItem.setTranslationX(dp);
                            PagerIndicatorView pagerIndicatorView = this.avatarsViewPagerIndicatorView;
                            if (pagerIndicatorView != null) {
                                pagerIndicatorView.setTranslationX(dp - AndroidUtilities.dp(48.0f));
                            }
                        }
                    }
                }
            }
            this.avatarX = (-AndroidUtilities.dpf2(47.0f)) * min;
            float f2 = AndroidUtilities.density;
            this.avatarY = (((this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ((ActionBar.getCurrentActionBarHeight() / 2.0f) * (min + 1.0f))) - (f2 * 21.0f)) + (f2 * 27.0f * min) + this.actionBar.getTranslationY();
            float f3 = this.openAnimationInProgress ? this.initialAnimationExtraHeight : this.extraHeight;
            if (f3 > AndroidUtilities.dp(88.0f) || this.isPulledDown) {
                float max = Math.max(0.0f, Math.min(1.0f, (f3 - AndroidUtilities.dp(88.0f)) / ((this.listView.getMeasuredWidth() - currentActionBarHeight) - AndroidUtilities.dp(88.0f))));
                this.expandProgress = max;
                this.avatarScale = AndroidUtilities.lerp(1.4285715f, 2.4285715f, Math.min(1.0f, max * 3.0f));
                float min2 = Math.min(AndroidUtilities.dpf2(2000.0f), Math.max(AndroidUtilities.dpf2(1100.0f), Math.abs(this.listViewVelocityY))) / AndroidUtilities.dpf2(1100.0f);
                if (this.allowPullingDown && (this.openingAvatar || this.expandProgress >= 0.33f)) {
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
                        ActionBarMenuItem actionBarMenuItem = this.searchItem;
                        if (actionBarMenuItem != null) {
                            actionBarMenuItem.setEnabled(false);
                        }
                        this.isPulledDown = true;
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needCheckSystemBarColors, Boolean.TRUE);
                        this.overlaysView.setOverlaysVisible(true, min2);
                        this.avatarsViewPagerIndicatorView.refreshVisibility(min2);
                        this.avatarsViewPager.setCreateThumbFromParent(true);
                        this.avatarsViewPager.getAdapter().notifyDataSetChanged();
                        this.expandAnimator.cancel();
                        float lerp = AndroidUtilities.lerp(this.expandAnimatorValues, this.currentExpanAnimatorFracture);
                        float[] fArr = this.expandAnimatorValues;
                        fArr[0] = lerp;
                        fArr[1] = 1.0f;
                        this.expandAnimator.setDuration(((1.0f - lerp) * 250.0f) / min2);
                        this.expandAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ProfileActivity.38
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationStart(Animator animator) {
                                ProfileActivity.this.setForegroundImage(false);
                                ProfileActivity.this.avatarsViewPager.setAnimatedFileMaybe(ProfileActivity.this.avatarImage.getImageReceiver().getAnimation());
                                ProfileActivity.this.avatarsViewPager.resetCurrentItem();
                            }

                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
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
                    float f4 = f3 + currentActionBarHeight;
                    layoutParams2.height = (int) f4;
                    this.avatarsViewPager.requestLayout();
                    if (!this.expandAnimator.isRunning()) {
                        float dp2 = (!this.openAnimationInProgress || this.playProfileAnimation != 2) ? 0.0f : (-(1.0f - this.animationProgress)) * AndroidUtilities.dp(50.0f);
                        this.nameTextView[1].setTranslationX(AndroidUtilities.dpf2(16.0f) - this.nameTextView[1].getLeft());
                        this.nameTextView[1].setTranslationY(((f4 - AndroidUtilities.dpf2(38.0f)) - this.nameTextView[1].getBottom()) + dp2);
                        this.onlineTextView[1].setTranslationX(AndroidUtilities.dpf2(16.0f) - this.onlineTextView[1].getLeft());
                        this.onlineTextView[1].setTranslationY(((f4 - AndroidUtilities.dpf2(18.0f)) - this.onlineTextView[1].getBottom()) + dp2);
                        this.mediaCounterTextView.setTranslationX(this.onlineTextView[1].getTranslationX());
                        this.mediaCounterTextView.setTranslationY(this.onlineTextView[1].getTranslationY());
                    }
                } else {
                    if (this.isPulledDown) {
                        this.isPulledDown = false;
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needCheckSystemBarColors, Boolean.TRUE);
                        ActionBarMenuItem actionBarMenuItem2 = this.otherItem;
                        if (actionBarMenuItem2 != null) {
                            actionBarMenuItem2.hideSubItem(21);
                            if (this.imageUpdater != null) {
                                this.otherItem.hideSubItem(33);
                                this.otherItem.hideSubItem(34);
                                this.otherItem.hideSubItem(35);
                                this.otherItem.showSubItem(36);
                                this.otherItem.showSubItem(31);
                                this.otherItem.showSubItem(30);
                            }
                        }
                        ActionBarMenuItem actionBarMenuItem3 = this.searchItem;
                        if (actionBarMenuItem3 != null) {
                            actionBarMenuItem3.setEnabled(!this.scrolling);
                        }
                        this.overlaysView.setOverlaysVisible(false, min2);
                        this.avatarsViewPagerIndicatorView.refreshVisibility(min2);
                        this.expandAnimator.cancel();
                        this.avatarImage.getImageReceiver().setAllowStartAnimation(true);
                        this.avatarImage.getImageReceiver().startAnimation();
                        float lerp2 = AndroidUtilities.lerp(this.expandAnimatorValues, this.currentExpanAnimatorFracture);
                        float[] fArr2 = this.expandAnimatorValues;
                        fArr2[0] = lerp2;
                        fArr2[1] = 0.0f;
                        if (!this.isInLandscapeMode) {
                            this.expandAnimator.setDuration((lerp2 * 250.0f) / min2);
                        } else {
                            this.expandAnimator.setDuration(0L);
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
                }
            }
            if (this.openAnimationInProgress && this.playProfileAnimation == 2) {
                float currentActionBarHeight2 = (((this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + (ActionBar.getCurrentActionBarHeight() / 2.0f)) - (AndroidUtilities.density * 21.0f)) + this.actionBar.getTranslationY();
                this.nameTextView[0].setTranslationX(0.0f);
                double d = currentActionBarHeight2;
                this.nameTextView[0].setTranslationY(((float) Math.floor(d)) + AndroidUtilities.dp(1.3f));
                this.onlineTextView[0].setTranslationX(0.0f);
                this.onlineTextView[0].setTranslationY(((float) Math.floor(d)) + AndroidUtilities.dp(24.0f));
                this.nameTextView[0].setScaleX(1.0f);
                this.nameTextView[0].setScaleY(1.0f);
                this.nameTextView[1].setPivotY(simpleTextViewArr[1].getMeasuredHeight());
                this.nameTextView[1].setScaleX(1.67f);
                this.nameTextView[1].setScaleY(1.67f);
                this.avatarScale = AndroidUtilities.lerp(1.0f, 2.4285715f, this.animationProgress);
                this.avatarImage.setRoundRadius((int) AndroidUtilities.lerp(getSmallAvatarRoundRadius(), 0.0f, this.animationProgress));
                this.avatarContainer.setTranslationX(AndroidUtilities.lerp(0.0f, 0.0f, this.animationProgress));
                this.avatarContainer.setTranslationY(AndroidUtilities.lerp((float) Math.ceil(d), 0.0f, this.animationProgress));
                float measuredWidth = (this.avatarContainer.getMeasuredWidth() - AndroidUtilities.dp(42.0f)) * this.avatarScale;
                this.timeItem.setTranslationX(this.avatarContainer.getX() + AndroidUtilities.dp(16.0f) + measuredWidth);
                this.timeItem.setTranslationY(this.avatarContainer.getY() + AndroidUtilities.dp(15.0f) + measuredWidth);
                this.avatarContainer.setScaleX(this.avatarScale);
                this.avatarContainer.setScaleY(this.avatarScale);
                this.overlaysView.setAlphaValue(this.animationProgress, false);
                this.actionBar.setItemsColor(ColorUtils.blendARGB(getThemedColor("actionBarDefaultIcon"), -1, this.animationProgress), false);
                ScamDrawable scamDrawable = this.scamDrawable;
                if (scamDrawable != null) {
                    scamDrawable.setColor(ColorUtils.blendARGB(getThemedColor("avatar_subtitleInProfileBlue"), Color.argb(179, 255, 255, 255), this.animationProgress));
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
                CrossfadeDrawable crossfadeDrawable2 = this.premiumCrossfadeDrawable;
                if (crossfadeDrawable2 != null) {
                    crossfadeDrawable2.setProgress(this.animationProgress);
                    this.nameTextView[1].invalidate();
                }
                updateEmojiStatusDrawableColor(this.animationProgress);
                FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.avatarContainer.getLayoutParams();
                int lerp3 = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(42.0f), (this.extraHeight + currentActionBarHeight) / this.avatarScale, this.animationProgress);
                layoutParams3.height = lerp3;
                layoutParams3.width = lerp3;
                layoutParams3.leftMargin = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(64.0f), 0.0f, this.animationProgress);
                this.avatarContainer.requestLayout();
            } else if (this.extraHeight <= AndroidUtilities.dp(88.0f)) {
                this.avatarScale = ((min * 18.0f) + 42.0f) / 42.0f;
                float f5 = (0.12f * min) + 1.0f;
                ValueAnimator valueAnimator3 = this.expandAnimator;
                if (valueAnimator3 == null || !valueAnimator3.isRunning()) {
                    this.avatarContainer.setScaleX(this.avatarScale);
                    this.avatarContainer.setScaleY(this.avatarScale);
                    this.avatarContainer.setTranslationX(this.avatarX);
                    this.avatarContainer.setTranslationY((float) Math.ceil(this.avatarY));
                    float dp3 = (AndroidUtilities.dp(42.0f) * this.avatarScale) - AndroidUtilities.dp(42.0f);
                    this.timeItem.setTranslationX(this.avatarContainer.getX() + AndroidUtilities.dp(16.0f) + dp3);
                    this.timeItem.setTranslationY(this.avatarContainer.getY() + AndroidUtilities.dp(15.0f) + dp3);
                }
                this.nameX = AndroidUtilities.density * (-21.0f) * min;
                this.nameY = ((float) Math.floor(this.avatarY)) + AndroidUtilities.dp(1.3f) + (AndroidUtilities.dp(7.0f) * min) + (this.titleAnimationsYDiff * (1.0f - this.animationProgress));
                this.onlineX = AndroidUtilities.density * (-21.0f) * min;
                this.onlineY = ((float) Math.floor(this.avatarY)) + AndroidUtilities.dp(24.0f) + (((float) Math.floor(AndroidUtilities.density * 11.0f)) * min);
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
        if (this.isPulledDown || ((overlaysView = this.overlaysView) != null && overlaysView.animator != null && this.overlaysView.animator.isRunning())) {
            ViewGroup.LayoutParams layoutParams4 = this.overlaysView.getLayoutParams();
            layoutParams4.width = this.listView.getMeasuredWidth();
            layoutParams4.height = (int) (this.extraHeight + currentActionBarHeight);
            this.overlaysView.requestLayout();
        }
        updateEmojiStatusEffectPosition();
    }

    public void updateQrItemVisibility(boolean z) {
        if (this.qrItem == null) {
            return;
        }
        float f = 1.0f;
        int i = 0;
        boolean z2 = isQrNeedVisible() && Math.min(1.0f, this.extraHeight / ((float) AndroidUtilities.dp(88.0f))) > 0.5f && this.searchTransitionProgress > 0.5f;
        if (z) {
            if (z2 == this.isQrItemVisible) {
                return;
            }
            this.isQrItemVisible = z2;
            AnimatorSet animatorSet = this.qrItemAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.qrItemAnimation = null;
            }
            this.qrItem.setClickable(this.isQrItemVisible);
            this.qrItemAnimation = new AnimatorSet();
            if (this.qrItem.getVisibility() != 8 || z2) {
                this.qrItem.setVisibility(0);
            }
            if (z2) {
                this.qrItemAnimation.setInterpolator(new DecelerateInterpolator());
                this.qrItemAnimation.playTogether(ObjectAnimator.ofFloat(this.qrItem, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.qrItem, View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.avatarsViewPagerIndicatorView, View.TRANSLATION_X, -AndroidUtilities.dp(48.0f)));
            } else {
                this.qrItemAnimation.setInterpolator(new AccelerateInterpolator());
                this.qrItemAnimation.playTogether(ObjectAnimator.ofFloat(this.qrItem, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.qrItem, View.SCALE_Y, 0.0f), ObjectAnimator.ofFloat(this.avatarsViewPagerIndicatorView, View.TRANSLATION_X, 0.0f));
            }
            this.qrItemAnimation.setDuration(150L);
            this.qrItemAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ProfileActivity.39
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    ProfileActivity.this.qrItemAnimation = null;
                }
            });
            this.qrItemAnimation.start();
            return;
        }
        AnimatorSet animatorSet2 = this.qrItemAnimation;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
            this.qrItemAnimation = null;
        }
        this.isQrItemVisible = z2;
        this.qrItem.setClickable(z2);
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
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setForegroundImage(boolean z) {
        Drawable drawable = this.avatarImage.getImageReceiver().getDrawable();
        String str = null;
        if (drawable instanceof AnimatedFileDrawable) {
            AnimatedFileDrawable animatedFileDrawable = (AnimatedFileDrawable) drawable;
            this.avatarImage.setForegroundImage(null, null, animatedFileDrawable);
            if (!z) {
                return;
            }
            animatedFileDrawable.addSecondParentView(this.avatarImage);
            return;
        }
        ImageLocation imageLocation = this.avatarsViewPager.getImageLocation(0);
        if (imageLocation != null && imageLocation.imageType == 2) {
            str = "avatar";
        }
        this.avatarImage.setForegroundImage(imageLocation, str, drawable);
    }

    private void refreshNameAndOnlineXY() {
        this.nameX = AndroidUtilities.dp(-21.0f) + (this.avatarContainer.getMeasuredWidth() * (this.avatarScale - 1.4285715f));
        this.nameY = ((float) Math.floor(this.avatarY)) + AndroidUtilities.dp(1.3f) + AndroidUtilities.dp(7.0f) + ((this.avatarContainer.getMeasuredHeight() * (this.avatarScale - 1.4285715f)) / 2.0f);
        this.onlineX = AndroidUtilities.dp(-21.0f) + (this.avatarContainer.getMeasuredWidth() * (this.avatarScale - 1.4285715f));
        this.onlineY = ((float) Math.floor(this.avatarY)) + AndroidUtilities.dp(24.0f) + ((float) Math.floor(AndroidUtilities.density * 11.0f)) + ((this.avatarContainer.getMeasuredHeight() * (this.avatarScale - 1.4285715f)) / 2.0f);
    }

    @Override // org.telegram.ui.Components.SharedMediaLayout.Delegate
    public RecyclerListView getListView() {
        return this.listView;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void needLayoutText(float f) {
        float scaleX = this.nameTextView[1].getScaleX();
        float f2 = this.extraHeight > ((float) AndroidUtilities.dp(88.0f)) ? 1.67f : 1.12f;
        if (this.extraHeight <= AndroidUtilities.dp(88.0f) || scaleX == f2) {
            int dp = AndroidUtilities.isTablet() ? AndroidUtilities.dp(490.0f) : AndroidUtilities.displaySize.x;
            this.avatarsViewPagerIndicatorView.getSecondaryMenuItem();
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
            int dp2 = AndroidUtilities.dp((i * (1.0f - this.mediaHeaderAnimationProgress)) + 40.0f + 126.0f);
            int i2 = dp - dp2;
            float f3 = dp;
            int max = (int) ((f3 - (dp2 * Math.max(0.0f, 1.0f - (f != 1.0f ? (0.15f * f) / (1.0f - f) : 1.0f)))) - this.nameTextView[1].getTranslationX());
            float measureText = (this.nameTextView[1].getPaint().measureText(this.nameTextView[1].getText().toString()) * scaleX) + this.nameTextView[1].getSideDrawablesSize();
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.nameTextView[1].getLayoutParams();
            int i3 = layoutParams.width;
            float f4 = max;
            if (f4 < measureText) {
                layoutParams.width = Math.max(i2, (int) Math.ceil((max - AndroidUtilities.dp(24.0f)) / (((f2 - scaleX) * 7.0f) + scaleX)));
            } else {
                layoutParams.width = (int) Math.ceil(measureText);
            }
            int min = (int) Math.min(((f3 - this.nameTextView[1].getX()) / scaleX) - AndroidUtilities.dp(8.0f), layoutParams.width);
            layoutParams.width = min;
            if (min != i3) {
                this.nameTextView[1].requestLayout();
            }
            float measureText2 = this.onlineTextView[1].getPaint().measureText(this.onlineTextView[1].getText().toString());
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.onlineTextView[1].getLayoutParams();
            FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.mediaCounterTextView.getLayoutParams();
            int i4 = layoutParams2.width;
            int ceil = (int) Math.ceil(this.onlineTextView[1].getTranslationX() + AndroidUtilities.dp(8.0f) + (AndroidUtilities.dp(40.0f) * (1.0f - f)));
            layoutParams2.rightMargin = ceil;
            layoutParams3.rightMargin = ceil;
            if (f4 < measureText2) {
                int ceil2 = (int) Math.ceil(max);
                layoutParams2.width = ceil2;
                layoutParams3.width = ceil2;
            } else {
                layoutParams2.width = -2;
                layoutParams3.width = -2;
            }
            if (i4 == layoutParams2.width) {
                return;
            }
            this.onlineTextView[1].requestLayout();
            this.mediaCounterTextView.requestLayout();
        }
    }

    private void fixLayout() {
        View view = this.fragmentView;
        if (view == null) {
            return;
        }
        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.ProfileActivity.40
            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public boolean onPreDraw() {
                if (((BaseFragment) ProfileActivity.this).fragmentView != null) {
                    ProfileActivity.this.checkListViewScroll();
                    ProfileActivity.this.needLayout(true);
                    ((BaseFragment) ProfileActivity.this).fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
                }
                return true;
            }
        });
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onConfigurationChanged(Configuration configuration) {
        View findViewByPosition;
        super.onConfigurationChanged(configuration);
        SharedMediaLayout sharedMediaLayout = this.sharedMediaLayout;
        if (sharedMediaLayout != null) {
            sharedMediaLayout.onConfigurationChanged(configuration);
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

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, final Object... objArr) {
        TLRPC$Chat tLRPC$Chat;
        RecyclerListView recyclerListView;
        RecyclerListView recyclerListView2;
        RecyclerListView.Holder holder;
        int i3 = 0;
        if (i == NotificationCenter.updateInterfaces) {
            int intValue = ((Integer) objArr[0]).intValue();
            boolean z = ((MessagesController.UPDATE_MASK_AVATAR & intValue) == 0 && (MessagesController.UPDATE_MASK_NAME & intValue) == 0 && (MessagesController.UPDATE_MASK_STATUS & intValue) == 0 && (MessagesController.UPDATE_MASK_EMOJI_STATUS & intValue) == 0) ? false : true;
            if (this.userId != 0) {
                if (z) {
                    updateProfileData(true);
                }
                if ((intValue & MessagesController.UPDATE_MASK_PHONE) == 0 || (recyclerListView2 = this.listView) == null || (holder = (RecyclerListView.Holder) recyclerListView2.findViewHolderForPosition(this.phoneRow)) == null) {
                    return;
                }
                this.listAdapter.onBindViewHolder(holder, this.phoneRow);
            } else if (this.chatId == 0) {
            } else {
                if ((MessagesController.UPDATE_MASK_CHAT & intValue) != 0 || (MessagesController.UPDATE_MASK_CHAT_AVATAR & intValue) != 0 || (MessagesController.UPDATE_MASK_CHAT_NAME & intValue) != 0 || (MessagesController.UPDATE_MASK_CHAT_MEMBERS & intValue) != 0 || (MessagesController.UPDATE_MASK_STATUS & intValue) != 0) {
                    if ((MessagesController.UPDATE_MASK_CHAT & intValue) != 0) {
                        updateListAnimated(true);
                    } else {
                        updateOnlineCount(true);
                    }
                    updateProfileData(true);
                }
                if (!z || (recyclerListView = this.listView) == null) {
                    return;
                }
                int childCount = recyclerListView.getChildCount();
                while (i3 < childCount) {
                    View childAt = this.listView.getChildAt(i3);
                    if (childAt instanceof UserCell) {
                        ((UserCell) childAt).update(intValue);
                    }
                    i3++;
                }
            }
        } else if (i == NotificationCenter.chatOnlineCountDidLoad) {
            Long l = (Long) objArr[0];
            if (this.chatInfo == null || (tLRPC$Chat = this.currentChat) == null || tLRPC$Chat.id != l.longValue()) {
                return;
            }
            this.chatInfo.online_count = ((Integer) objArr[1]).intValue();
            updateOnlineCount(true);
            updateProfileData(false);
        } else if (i == NotificationCenter.contactsDidLoad) {
            createActionBarMenu(true);
        } else if (i == NotificationCenter.encryptedChatCreated) {
            if (!this.creatingChat) {
                return;
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda32
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.this.lambda$didReceivedNotification$34(objArr);
                }
            });
        } else if (i == NotificationCenter.encryptedChatUpdated) {
            TLRPC$EncryptedChat tLRPC$EncryptedChat = (TLRPC$EncryptedChat) objArr[0];
            TLRPC$EncryptedChat tLRPC$EncryptedChat2 = this.currentEncryptedChat;
            if (tLRPC$EncryptedChat2 == null || tLRPC$EncryptedChat.id != tLRPC$EncryptedChat2.id) {
                return;
            }
            this.currentEncryptedChat = tLRPC$EncryptedChat;
            updateListAnimated(false);
        } else if (i == NotificationCenter.blockedUsersDidLoad) {
            boolean z2 = this.userBlocked;
            boolean z3 = getMessagesController().blockePeers.indexOfKey(this.userId) >= 0;
            this.userBlocked = z3;
            if (z2 == z3) {
                return;
            }
            createActionBarMenu(true);
            updateListAnimated(false);
        } else if (i == NotificationCenter.groupCallUpdated) {
            Long l2 = (Long) objArr[0];
            if (this.currentChat == null) {
                return;
            }
            long longValue = l2.longValue();
            TLRPC$Chat tLRPC$Chat2 = this.currentChat;
            if (longValue != tLRPC$Chat2.id || !ChatObject.canManageCalls(tLRPC$Chat2)) {
                return;
            }
            TLRPC$ChatFull chatFull = MessagesController.getInstance(this.currentAccount).getChatFull(l2.longValue());
            if (chatFull != null) {
                TLRPC$ChatFull tLRPC$ChatFull = this.chatInfo;
                if (tLRPC$ChatFull != null) {
                    chatFull.participants = tLRPC$ChatFull.participants;
                }
                this.chatInfo = chatFull;
            }
            TLRPC$ChatFull tLRPC$ChatFull2 = this.chatInfo;
            if (tLRPC$ChatFull2 == null) {
                return;
            }
            TLRPC$TL_inputGroupCall tLRPC$TL_inputGroupCall = tLRPC$ChatFull2.call;
            if ((tLRPC$TL_inputGroupCall != null || this.hasVoiceChatItem) && (tLRPC$TL_inputGroupCall == null || !this.hasVoiceChatItem)) {
                return;
            }
            createActionBarMenu(false);
        } else if (i == NotificationCenter.chatInfoDidLoad) {
            TLRPC$ChatFull tLRPC$ChatFull3 = (TLRPC$ChatFull) objArr[0];
            if (tLRPC$ChatFull3.id != this.chatId) {
                return;
            }
            boolean booleanValue = ((Boolean) objArr[2]).booleanValue();
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
                    getMediaDataController().getMediaCount(this.mergeDialogId, this.topicId, 0, this.classGuid, true);
                }
            }
            fetchUsersFromChannelInfo();
            ProfileGalleryView profileGalleryView = this.avatarsViewPager;
            if (profileGalleryView != null && !this.isTopic) {
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
        } else if (i == NotificationCenter.closeChats) {
            removeSelfFromStack();
        } else if (i == NotificationCenter.botInfoDidLoad) {
            TLRPC$BotInfo tLRPC$BotInfo = (TLRPC$BotInfo) objArr[0];
            if (tLRPC$BotInfo.user_id != this.userId) {
                return;
            }
            this.botInfo = tLRPC$BotInfo;
            updateListAnimated(false);
        } else if (i == NotificationCenter.userInfoDidLoad) {
            if (((Long) objArr[0]).longValue() != this.userId) {
                return;
            }
            TLRPC$UserFull tLRPC$UserFull = (TLRPC$UserFull) objArr[1];
            this.userInfo = tLRPC$UserFull;
            if (this.imageUpdater != null) {
                if (!TextUtils.equals(tLRPC$UserFull.about, this.currentBio)) {
                    this.listAdapter.notifyItemChanged(this.bioRow);
                }
            } else {
                if (!this.openAnimationInProgress && !this.callItemVisible) {
                    createActionBarMenu(true);
                } else {
                    this.recreateMenuAfterAnimation = true;
                }
                updateListAnimated(false);
                this.sharedMediaLayout.setCommonGroupsCount(this.userInfo.common_chats_count);
                updateSelectedMediaTabText();
                SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader = this.sharedMediaPreloader;
                if (sharedMediaPreloader == null || sharedMediaPreloader.isMediaWasLoaded()) {
                    resumeDelayedFragmentAnimation();
                    needLayout(true);
                }
            }
            updateAutoDeleteItem();
            updateTtlIcon();
        } else if (i == NotificationCenter.privacyRulesUpdated) {
            if (this.qrItem == null) {
                return;
            }
            updateQrItemVisibility(true);
        } else if (i == NotificationCenter.didReceiveNewMessages) {
            if (((Boolean) objArr[2]).booleanValue()) {
                return;
            }
            long dialogId = getDialogId();
            if (dialogId != ((Long) objArr[0]).longValue()) {
                return;
            }
            DialogObject.isEncryptedDialog(dialogId);
            ArrayList arrayList = (ArrayList) objArr[1];
            while (i3 < arrayList.size()) {
                MessageObject messageObject = (MessageObject) arrayList.get(i3);
                if (this.currentEncryptedChat != null) {
                    TLRPC$MessageAction tLRPC$MessageAction = messageObject.messageOwner.action;
                    if (tLRPC$MessageAction instanceof TLRPC$TL_messageEncryptedAction) {
                        TLRPC$DecryptedMessageAction tLRPC$DecryptedMessageAction = tLRPC$MessageAction.encryptedAction;
                        if (tLRPC$DecryptedMessageAction instanceof TLRPC$TL_decryptedMessageActionSetMessageTTL) {
                            TLRPC$TL_decryptedMessageActionSetMessageTTL tLRPC$TL_decryptedMessageActionSetMessageTTL = (TLRPC$TL_decryptedMessageActionSetMessageTTL) tLRPC$DecryptedMessageAction;
                            ListAdapter listAdapter = this.listAdapter;
                            if (listAdapter != null) {
                                listAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
                i3++;
            }
        } else if (i == NotificationCenter.emojiLoaded) {
            RecyclerListView recyclerListView3 = this.listView;
            if (recyclerListView3 == null) {
                return;
            }
            recyclerListView3.invalidateViews();
        } else if (i == NotificationCenter.reloadInterface) {
            updateListAnimated(false);
        } else if (i == NotificationCenter.newSuggestionsAvailable) {
            int i4 = this.passwordSuggestionRow;
            int i5 = this.phoneSuggestionRow;
            updateRowsIds();
            if (i4 == this.passwordSuggestionRow && i5 == this.phoneSuggestionRow) {
                return;
            }
            this.listAdapter.notifyDataSetChanged();
        } else if (i != NotificationCenter.topicsDidLoaded || !this.isTopic) {
        } else {
            updateProfileData(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$didReceivedNotification$34(Object[] objArr) {
        NotificationCenter notificationCenter = getNotificationCenter();
        int i = NotificationCenter.closeChats;
        notificationCenter.removeObserver(this, i);
        getNotificationCenter().postNotificationName(i, new Object[0]);
        Bundle bundle = new Bundle();
        bundle.putInt("enc_id", ((TLRPC$EncryptedChat) objArr[0]).id);
        presentFragment(new ChatActivity(bundle), true);
    }

    private void updateAutoDeleteItem() {
        if (this.autoDeleteItem == null || this.autoDeletePopupWrapper == null) {
            return;
        }
        int i = 0;
        TLRPC$UserFull tLRPC$UserFull = this.userInfo;
        if (tLRPC$UserFull != null || this.chatInfo != null) {
            i = tLRPC$UserFull != null ? tLRPC$UserFull.ttl_period : this.chatInfo.ttl_period;
        }
        this.autoDeleteItemDrawable.setTime(i);
        this.autoDeletePopupWrapper.lambda$updateItems$7(i);
    }

    private void updateTimeItem() {
        TimerDrawable timerDrawable = this.timerDrawable;
        if (timerDrawable == null) {
            return;
        }
        TLRPC$EncryptedChat tLRPC$EncryptedChat = this.currentEncryptedChat;
        if (tLRPC$EncryptedChat != null) {
            timerDrawable.setTime(tLRPC$EncryptedChat.ttl);
            this.timeItem.setTag(1);
            this.timeItem.setVisibility(0);
            return;
        }
        TLRPC$UserFull tLRPC$UserFull = this.userInfo;
        if (tLRPC$UserFull != null) {
            timerDrawable.setTime(tLRPC$UserFull.ttl_period);
            if (this.needTimerImage && this.userInfo.ttl_period != 0) {
                this.timeItem.setTag(1);
                this.timeItem.setVisibility(0);
                return;
            }
            this.timeItem.setTag(null);
            this.timeItem.setVisibility(8);
            return;
        }
        TLRPC$ChatFull tLRPC$ChatFull = this.chatInfo;
        if (tLRPC$ChatFull != null) {
            timerDrawable.setTime(tLRPC$ChatFull.ttl_period);
            if (this.needTimerImage && this.chatInfo.ttl_period != 0) {
                this.timeItem.setTag(1);
                this.timeItem.setVisibility(0);
                return;
            }
            this.timeItem.setTag(null);
            this.timeItem.setVisibility(8);
            return;
        }
        this.timeItem.setTag(null);
        this.timeItem.setVisibility(8);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean needDelayOpenAnimation() {
        return this.playProfileAnimation == 0;
    }

    @Override // org.telegram.ui.Components.SharedMediaLayout.SharedMediaPreloaderDelegate
    public void mediaCountUpdated() {
        SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader;
        SharedMediaLayout sharedMediaLayout = this.sharedMediaLayout;
        if (sharedMediaLayout != null && (sharedMediaPreloader = this.sharedMediaPreloader) != null) {
            sharedMediaLayout.setNewMediaCounts(sharedMediaPreloader.getLastMediaCount());
        }
        updateSharedMediaRows();
        updateSelectedMediaTabText();
        if (this.userInfo != null) {
            resumeDelayedFragmentAnimation();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        SharedMediaLayout sharedMediaLayout = this.sharedMediaLayout;
        if (sharedMediaLayout != null) {
            sharedMediaLayout.onResume();
        }
        invalidateIsInLandscapeMode();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            this.firstLayout = true;
            listAdapter.notifyDataSetChanged();
        }
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.onResume();
            setParentActivityTitle(LocaleController.getString("Settings", R.string.Settings));
        }
        updateProfileData(true);
        fixLayout();
        SimpleTextView[] simpleTextViewArr = this.nameTextView;
        if (simpleTextViewArr[1] != null) {
            setParentActivityTitle(simpleTextViewArr[1].getText());
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onPause() {
        super.onPause();
        UndoView undoView = this.undoView;
        if (undoView != null) {
            undoView.hide(true, 0);
        }
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.onPause();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean isSwipeBackEnabled(MotionEvent motionEvent) {
        SharedMediaLayout sharedMediaLayout;
        ProfileGalleryView profileGalleryView = this.avatarsViewPager;
        if (profileGalleryView != null && profileGalleryView.getVisibility() == 0 && this.avatarsViewPager.getRealCount() > 1) {
            this.avatarsViewPager.getHitRect(this.rect);
            if (this.rect.contains((int) motionEvent.getX(), ((int) motionEvent.getY()) - this.actionBar.getMeasuredHeight())) {
                return false;
            }
        }
        if (this.sharedMediaRow == -1 || (sharedMediaLayout = this.sharedMediaLayout) == null) {
            return true;
        }
        if (!sharedMediaLayout.isSwipeBackEnabled()) {
            return false;
        }
        this.sharedMediaLayout.getHitRect(this.rect);
        if (this.rect.contains((int) motionEvent.getX(), ((int) motionEvent.getY()) - this.actionBar.getMeasuredHeight())) {
            return this.sharedMediaLayout.isCurrentTabFirst();
        }
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean canBeginSlide() {
        if (!this.sharedMediaLayout.isSwipeBackEnabled()) {
            return false;
        }
        return super.canBeginSlide();
    }

    public UndoView getUndoView() {
        return this.undoView;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onBackPressed() {
        SharedMediaLayout sharedMediaLayout;
        return this.actionBar.isEnabled() && (this.sharedMediaRow == -1 || (sharedMediaLayout = this.sharedMediaLayout) == null || !sharedMediaLayout.closeActionMode());
    }

    public boolean isSettings() {
        return this.imageUpdater != null;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onBecomeFullyHidden() {
        UndoView undoView = this.undoView;
        if (undoView != null) {
            undoView.hide(true, 0);
        }
    }

    public void setPlayProfileAnimation(int i) {
        SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
        if (!AndroidUtilities.isTablet()) {
            this.needTimerImage = i != 0;
            if (globalMainSettings.getBoolean("view_animations", true)) {
                this.playProfileAnimation = i;
            } else if (i != 2) {
            } else {
                this.expandPhoto = true;
            }
        }
    }

    private void updateSharedMediaRows() {
        if (this.listAdapter == null) {
            return;
        }
        updateListAnimated(false);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
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

    @Override // org.telegram.ui.ActionBar.BaseFragment
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
        int profileBackColorForId;
        int profileTextColorForId;
        this.animationProgress = f;
        this.listView.setAlpha(f);
        this.listView.setTranslationX(AndroidUtilities.dp(48.0f) - (AndroidUtilities.dp(48.0f) * f));
        long j = 5;
        if (this.playProfileAnimation != 2 || (profileBackColorForId = this.avatarColor) == 0) {
            profileBackColorForId = AvatarDrawable.getProfileBackColorForId((this.userId != 0 || (ChatObject.isChannel(this.chatId, this.currentAccount) && !this.currentChat.megagroup)) ? 5L : this.chatId, this.resourcesProvider);
        }
        int i = this.actionBarAnimationColorFrom;
        if (i == 0) {
            i = getThemedColor("actionBarDefault");
        }
        this.topView.setBackgroundColor(ColorUtils.blendARGB(SharedConfig.chatBlurEnabled() ? ColorUtils.setAlphaComponent(i, 0) : i, profileBackColorForId, f));
        this.timerDrawable.setBackgroundColor(ColorUtils.blendARGB(i, profileBackColorForId, f));
        this.actionBar.setItemsColor(ColorUtils.blendARGB(getThemedColor("actionBarDefaultIcon"), AvatarDrawable.getIconColorForId((this.userId != 0 || (ChatObject.isChannel(this.chatId, this.currentAccount) && !this.currentChat.megagroup)) ? 5L : this.chatId, this.resourcesProvider), f), false);
        int themedColor = getThemedColor("profile_title");
        int themedColor2 = getThemedColor("actionBarDefaultTitle");
        for (int i2 = 0; i2 < 2; i2++) {
            SimpleTextView[] simpleTextViewArr = this.nameTextView;
            if (simpleTextViewArr[i2] != null && (i2 != 1 || this.playProfileAnimation != 2)) {
                simpleTextViewArr[i2].setTextColor(ColorUtils.blendARGB(themedColor2, themedColor, f));
            }
        }
        if (this.isOnline[0]) {
            profileTextColorForId = getThemedColor("profile_status");
        } else {
            if (this.userId == 0 && (!ChatObject.isChannel(this.chatId, this.currentAccount) || this.currentChat.megagroup)) {
                j = this.chatId;
            }
            profileTextColorForId = AvatarDrawable.getProfileTextColorForId(j, this.resourcesProvider);
        }
        int themedColor3 = getThemedColor(this.isOnline[0] ? "chat_status" : "actionBarDefaultSubtitle");
        for (int i3 = 0; i3 < 2; i3++) {
            SimpleTextView[] simpleTextViewArr2 = this.onlineTextView;
            if (simpleTextViewArr2[i3] != null && (i3 != 1 || this.playProfileAnimation != 2)) {
                simpleTextViewArr2[i3].setTextColor(ColorUtils.blendARGB(themedColor3, profileTextColorForId, f));
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
        int i4 = this.navigationBarAnimationColorFrom;
        if (i4 != 0) {
            setNavigationBarColor(ColorUtils.blendARGB(i4, getNavigationBarColor(), f));
        }
        this.topView.invalidate();
        needLayout(true);
        View view = this.fragmentView;
        if (view != null) {
            view.invalidate();
        }
        AboutLinkCell aboutLinkCell = this.aboutLinkCell;
        if (aboutLinkCell != null) {
            aboutLinkCell.invalidate();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public int getNavigationBarColor() {
        return Theme.getColor("windowBackgroundGray", this.resourcesProvider);
    }

    /* JADX WARN: Removed duplicated region for block: B:147:0x04e0  */
    /* JADX WARN: Removed duplicated region for block: B:93:0x0329  */
    @Override // org.telegram.ui.ActionBar.BaseFragment
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public android.animation.AnimatorSet onCustomTransitionAnimation(boolean r17, final java.lang.Runnable r18) {
        /*
            Method dump skipped, instructions count: 1416
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.onCustomTransitionAnimation(boolean, java.lang.Runnable):android.animation.AnimatorSet");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCustomTransitionAnimation$35(ValueAnimator valueAnimator) {
        this.fragmentView.invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateOnlineCount(boolean z) {
        TLRPC$UserStatus tLRPC$UserStatus;
        this.onlineCount = 0;
        final int currentTime = getConnectionsManager().getCurrentTime();
        this.sortedUsers.clear();
        TLRPC$ChatFull tLRPC$ChatFull = this.chatInfo;
        if ((tLRPC$ChatFull instanceof TLRPC$TL_chatFull) || ((tLRPC$ChatFull instanceof TLRPC$TL_channelFull) && tLRPC$ChatFull.participants_count <= 200 && tLRPC$ChatFull.participants != null)) {
            for (int i = 0; i < this.chatInfo.participants.participants.size(); i++) {
                TLRPC$User user = getMessagesController().getUser(Long.valueOf(this.chatInfo.participants.participants.get(i).user_id));
                if (user != null && (tLRPC$UserStatus = user.status) != null && ((tLRPC$UserStatus.expires > currentTime || user.id == getUserConfig().getClientUserId()) && user.status.expires > 10000)) {
                    this.onlineCount++;
                }
                this.sortedUsers.add(Integer.valueOf(i));
            }
            try {
                Collections.sort(this.sortedUsers, new Comparator() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda34
                    @Override // java.util.Comparator
                    public final int compare(Object obj, Object obj2) {
                        int lambda$updateOnlineCount$36;
                        lambda$updateOnlineCount$36 = ProfileActivity.this.lambda$updateOnlineCount$36(currentTime, (Integer) obj, (Integer) obj2);
                        return lambda$updateOnlineCount$36;
                    }
                });
            } catch (Exception e) {
                FileLog.e(e);
            }
            if (z && this.listAdapter != null && this.membersStartRow > 0) {
                AndroidUtilities.updateVisibleRows(this.listView);
            }
            if (this.sharedMediaLayout == null || this.sharedMediaRow == -1) {
                return;
            }
            if ((this.sortedUsers.size() <= 5 && this.usersForceShowingIn != 2) || this.usersForceShowingIn == 1) {
                return;
            }
            this.sharedMediaLayout.setChatUsers(this.sortedUsers, this.chatInfo);
        } else if ((tLRPC$ChatFull instanceof TLRPC$TL_channelFull) && tLRPC$ChatFull.participants_count > 200) {
            this.onlineCount = tLRPC$ChatFull.online_count;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:15:0x005c  */
    /* JADX WARN: Removed duplicated region for block: B:27:0x0074 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:34:0x007f A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:41:0x008a A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:46:0x0093 A[ADDED_TO_REGION] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ int lambda$updateOnlineCount$36(int r5, java.lang.Integer r6, java.lang.Integer r7) {
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
            if (r7 == 0) goto L59
            boolean r3 = r7.bot
            if (r3 == 0) goto L4b
            r7 = -110(0xfffffffffffffvar_, float:NaN)
            goto L5a
        L4b:
            boolean r3 = r7.self
            if (r3 == 0) goto L52
            int r7 = r5 + r0
            goto L5a
        L52:
            org.telegram.tgnet.TLRPC$UserStatus r7 = r7.status
            if (r7 == 0) goto L59
            int r7 = r7.expires
            goto L5a
        L59:
            r7 = 0
        L5a:
            if (r6 == 0) goto L6f
            boolean r3 = r6.bot
            if (r3 == 0) goto L61
            goto L70
        L61:
            boolean r1 = r6.self
            if (r1 == 0) goto L68
            int r1 = r5 + r0
            goto L70
        L68:
            org.telegram.tgnet.TLRPC$UserStatus r5 = r6.status
            if (r5 == 0) goto L6f
            int r1 = r5.expires
            goto L70
        L6f:
            r1 = 0
        L70:
            r5 = -1
            r6 = 1
            if (r7 <= 0) goto L7d
            if (r1 <= 0) goto L7d
            if (r7 <= r1) goto L79
            return r6
        L79:
            if (r7 >= r1) goto L7c
            return r5
        L7c:
            return r2
        L7d:
            if (r7 >= 0) goto L88
            if (r1 >= 0) goto L88
            if (r7 <= r1) goto L84
            return r6
        L84:
            if (r7 >= r1) goto L87
            return r5
        L87:
            return r2
        L88:
            if (r7 >= 0) goto L8c
            if (r1 > 0) goto L90
        L8c:
            if (r7 != 0) goto L91
            if (r1 == 0) goto L91
        L90:
            return r5
        L91:
            if (r1 >= 0) goto L95
            if (r7 > 0) goto L99
        L95:
            if (r1 != 0) goto L9a
            if (r7 == 0) goto L9a
        L99:
            return r6
        L9a:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.lambda$updateOnlineCount$36(int, java.lang.Integer, java.lang.Integer):int");
    }

    public void setChatInfo(TLRPC$ChatFull tLRPC$ChatFull) {
        this.chatInfo = tLRPC$ChatFull;
        if (tLRPC$ChatFull != null) {
            long j = tLRPC$ChatFull.migrated_from_chat_id;
            if (j != 0 && this.mergeDialogId == 0) {
                this.mergeDialogId = -j;
                getMediaDataController().getMediaCounts(this.mergeDialogId, this.topicId, this.classGuid);
            }
        }
        SharedMediaLayout sharedMediaLayout = this.sharedMediaLayout;
        if (sharedMediaLayout != null) {
            sharedMediaLayout.setChatInfo(this.chatInfo);
        }
        ProfileGalleryView profileGalleryView = this.avatarsViewPager;
        if (profileGalleryView != null && !this.isTopic) {
            profileGalleryView.setChatInfo(this.chatInfo);
        }
        fetchUsersFromChannelInfo();
    }

    public void setUserInfo(TLRPC$UserFull tLRPC$UserFull) {
        this.userInfo = tLRPC$UserFull;
    }

    @Override // org.telegram.ui.Components.SharedMediaLayout.Delegate
    public boolean canSearchMembers() {
        return this.canSearchMembers;
    }

    private void fetchUsersFromChannelInfo() {
        TLRPC$Chat tLRPC$Chat = this.currentChat;
        if (tLRPC$Chat == null || !tLRPC$Chat.megagroup) {
            return;
        }
        TLRPC$ChatFull tLRPC$ChatFull = this.chatInfo;
        if (!(tLRPC$ChatFull instanceof TLRPC$TL_channelFull) || tLRPC$ChatFull.participants == null) {
            return;
        }
        for (int i = 0; i < this.chatInfo.participants.participants.size(); i++) {
            TLRPC$ChatParticipant tLRPC$ChatParticipant = this.chatInfo.participants.participants.get(i);
            this.participantsMap.put(tLRPC$ChatParticipant.user_id, tLRPC$ChatParticipant);
        }
    }

    private void kickUser(long j, TLRPC$ChatParticipant tLRPC$ChatParticipant) {
        if (j != 0) {
            TLRPC$User user = getMessagesController().getUser(Long.valueOf(j));
            getMessagesController().deleteParticipantFromChat(this.chatId, user);
            if (this.currentChat != null && user != null && BulletinFactory.canShowBulletin(this)) {
                BulletinFactory.createRemoveFromChatBulletin(this, user, this.currentChat.title).show();
            }
            if (!this.chatInfo.participants.participants.remove(tLRPC$ChatParticipant)) {
                return;
            }
            updateListAnimated(true);
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
        getMessagesController().deleteParticipantFromChat(this.chatId, getMessagesController().getUser(Long.valueOf(getUserConfig().getClientUserId())));
        this.playProfileAnimation = 0;
        finishFragment();
    }

    public boolean isChat() {
        return this.chatId != 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x00e7, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated) == false) goto L26;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void updateRowsIds() {
        /*
            Method dump skipped, instructions count: 1573
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.updateRowsIds():void");
    }

    private Drawable getScamDrawable(int i) {
        if (this.scamDrawable == null) {
            ScamDrawable scamDrawable = new ScamDrawable(11, i);
            this.scamDrawable = scamDrawable;
            scamDrawable.setColor(getThemedColor("avatar_subtitleInProfileBlue"));
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

    /* JADX INFO: Access modifiers changed from: private */
    public Drawable getPremiumCrossfadeDrawable() {
        if (this.premiumCrossfadeDrawable == null) {
            Drawable mutate = ContextCompat.getDrawable(getParentActivity(), R.drawable.msg_premium_liststar).mutate();
            this.premiumStarDrawable = mutate;
            mutate.setColorFilter(getThemedColor("profile_verifiedBackground"), PorterDuff.Mode.MULTIPLY);
            this.premiumCrossfadeDrawable = new CrossfadeDrawable(this.premiumStarDrawable, ContextCompat.getDrawable(getParentActivity(), R.drawable.msg_premium_prolfilestar).mutate());
        }
        return this.premiumCrossfadeDrawable;
    }

    private Drawable getEmojiStatusDrawable(TLRPC$EmojiStatus tLRPC$EmojiStatus, boolean z, boolean z2, int i) {
        AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable[] swapAnimatedEmojiDrawableArr = this.emojiStatusDrawable;
        if (swapAnimatedEmojiDrawableArr[i] == null) {
            swapAnimatedEmojiDrawableArr[i] = new AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable(this.nameTextView[i], AndroidUtilities.dp(24.0f), i == 0 ? 7 : 2);
        }
        if (tLRPC$EmojiStatus instanceof TLRPC$TL_emojiStatus) {
            this.emojiStatusDrawable[i].set(((TLRPC$TL_emojiStatus) tLRPC$EmojiStatus).document_id, z2);
        } else {
            if (tLRPC$EmojiStatus instanceof TLRPC$TL_emojiStatusUntil) {
                TLRPC$TL_emojiStatusUntil tLRPC$TL_emojiStatusUntil = (TLRPC$TL_emojiStatusUntil) tLRPC$EmojiStatus;
                if (tLRPC$TL_emojiStatusUntil.until > ((int) (System.currentTimeMillis() / 1000)) && !this.reportSpam) {
                    this.emojiStatusDrawable[i].set(tLRPC$TL_emojiStatusUntil.document_id, z2);
                }
            }
            this.emojiStatusDrawable[i].set(getPremiumCrossfadeDrawable(), z2);
        }
        updateEmojiStatusDrawableColor();
        return this.emojiStatusDrawable[i];
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateEmojiStatusDrawableColor() {
        updateEmojiStatusDrawableColor(this.lastEmojiStatusProgress);
    }

    private void updateEmojiStatusDrawableColor(float f) {
        int blendARGB = ColorUtils.blendARGB(AndroidUtilities.getOffsetColor(getThemedColor("profile_verifiedBackground"), getThemedColor("player_actionBarTitle"), this.mediaHeaderAnimationProgress, 1.0f), -1, f);
        for (int i = 0; i < 2; i++) {
            AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable[] swapAnimatedEmojiDrawableArr = this.emojiStatusDrawable;
            if (swapAnimatedEmojiDrawableArr[i] != null) {
                swapAnimatedEmojiDrawableArr[i].setColor(Integer.valueOf(blendARGB));
            }
        }
        this.animatedStatusView.setColor(blendARGB);
        this.lastEmojiStatusProgress = f;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateEmojiStatusEffectPosition() {
        this.animatedStatusView.setScaleX(this.nameTextView[1].getScaleX());
        this.animatedStatusView.setScaleY(this.nameTextView[1].getScaleY());
        this.animatedStatusView.translate(this.nameTextView[1].getX() + (this.nameTextView[1].getRightDrawableX() * this.nameTextView[1].getScaleX()), this.nameTextView[1].getY() + (this.nameTextView[1].getHeight() - ((this.nameTextView[1].getHeight() - this.nameTextView[1].getRightDrawableY()) * this.nameTextView[1].getScaleY())));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:213:0x0416, code lost:
        if (r29.chatInfo.can_view_participants != false) goto L388;
     */
    /* JADX WARN: Code restructure failed: missing block: B:282:0x0608, code lost:
        if (r29.nameTextView[r4].setText(r13) != false) goto L311;
     */
    /* JADX WARN: Code restructure failed: missing block: B:290:0x0632, code lost:
        if (r29.nameTextView[r4].setText(r1) != false) goto L311;
     */
    /* JADX WARN: Code restructure failed: missing block: B:291:0x0634, code lost:
        r8 = true;
     */
    /* JADX WARN: Removed duplicated region for block: B:108:0x023a  */
    /* JADX WARN: Removed duplicated region for block: B:109:0x023f  */
    /* JADX WARN: Removed duplicated region for block: B:112:0x0246  */
    /* JADX WARN: Removed duplicated region for block: B:113:0x0248  */
    /* JADX WARN: Removed duplicated region for block: B:116:0x024e  */
    /* JADX WARN: Removed duplicated region for block: B:146:0x02f1  */
    /* JADX WARN: Removed duplicated region for block: B:172:0x0351 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:177:0x0360  */
    /* JADX WARN: Removed duplicated region for block: B:182:0x037c  */
    /* JADX WARN: Removed duplicated region for block: B:266:0x05c2  */
    /* JADX WARN: Removed duplicated region for block: B:373:0x0829  */
    /* JADX WARN: Removed duplicated region for block: B:382:0x083d  */
    /* JADX WARN: Removed duplicated region for block: B:383:0x0847  */
    /* JADX WARN: Removed duplicated region for block: B:412:0x08df  */
    /* JADX WARN: Removed duplicated region for block: B:429:0x0394 A[ADDED_TO_REGION, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void updateProfileData(boolean r30) {
        /*
            Method dump skipped, instructions count: 2283
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.updateProfileData(boolean):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateProfileData$37(View view) {
        showStatusSelect();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateProfileData$38(TLRPC$User tLRPC$User, SimpleTextView simpleTextView, View view) {
        ImageLocation forDocument;
        PremiumPreviewBottomSheet premiumPreviewBottomSheet = new PremiumPreviewBottomSheet(this, this.currentAccount, tLRPC$User, this.resourcesProvider);
        simpleTextView.getLocationOnScreen(new int[2]);
        premiumPreviewBottomSheet.startEnterFromX = simpleTextView.rightDrawableX;
        premiumPreviewBottomSheet.startEnterFromY = simpleTextView.rightDrawableY;
        premiumPreviewBottomSheet.startEnterFromScale = simpleTextView.getScaleX();
        premiumPreviewBottomSheet.startEnterFromX1 = simpleTextView.getLeft();
        premiumPreviewBottomSheet.startEnterFromY1 = simpleTextView.getTop();
        premiumPreviewBottomSheet.startEnterFromView = simpleTextView;
        Drawable rightDrawable = simpleTextView.getRightDrawable();
        AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable[] swapAnimatedEmojiDrawableArr = this.emojiStatusDrawable;
        if (rightDrawable == swapAnimatedEmojiDrawableArr[1] && swapAnimatedEmojiDrawableArr[1] != null && (swapAnimatedEmojiDrawableArr[1].getDrawable() instanceof AnimatedEmojiDrawable)) {
            premiumPreviewBottomSheet.startEnterFromScale *= 0.98f;
            TLRPC$Document document = ((AnimatedEmojiDrawable) this.emojiStatusDrawable[1].getDrawable()).getDocument();
            if (document != null) {
                BackupImageView backupImageView = new BackupImageView(getContext());
                String str = "160_160";
                SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(document.thumbs, "windowBackgroundWhiteGrayIcon", 0.2f);
                TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90);
                if ("video/webm".equals(document.mime_type)) {
                    forDocument = ImageLocation.getForDocument(document);
                    str = str + "_g";
                    if (svgThumb != null) {
                        svgThumb.overrideWidthAndHeight(512, 512);
                    }
                } else {
                    if (svgThumb != null && MessageObject.isAnimatedStickerDocument(document, false)) {
                        svgThumb.overrideWidthAndHeight(512, 512);
                    }
                    forDocument = ImageLocation.getForDocument(document);
                }
                String str2 = str;
                backupImageView.setLayerNum(7);
                backupImageView.setRoundRadius(AndroidUtilities.dp(4.0f));
                backupImageView.setImage(forDocument, str2, ImageLocation.getForDocument(closestPhotoSizeWithSize, document), "140_140", svgThumb, document);
                if (((AnimatedEmojiDrawable) this.emojiStatusDrawable[1].getDrawable()).canOverrideColor()) {
                    backupImageView.setColorFilter(new PorterDuffColorFilter(getThemedColor("windowBackgroundWhiteBlueIcon"), PorterDuff.Mode.MULTIPLY));
                } else {
                    premiumPreviewBottomSheet.statusStickerSet = MessageObject.getInputStickerSet(document);
                }
                premiumPreviewBottomSheet.overrideTitleIcon = backupImageView;
                premiumPreviewBottomSheet.isEmojiStatus = true;
            }
        }
        showDialog(premiumPreviewBottomSheet);
    }

    /* JADX WARN: Removed duplicated region for block: B:194:0x039c  */
    /* JADX WARN: Removed duplicated region for block: B:195:0x03d7  */
    /* JADX WARN: Removed duplicated region for block: B:198:0x03f0  */
    /* JADX WARN: Removed duplicated region for block: B:200:0x03f7  */
    /* JADX WARN: Removed duplicated region for block: B:203:0x040c  */
    /* JADX WARN: Removed duplicated region for block: B:206:0x042f  */
    /* JADX WARN: Removed duplicated region for block: B:242:0x04e5  */
    /* JADX WARN: Removed duplicated region for block: B:245:0x04fc  */
    /* JADX WARN: Removed duplicated region for block: B:248:0x0513  */
    /* JADX WARN: Removed duplicated region for block: B:251:0x052a  */
    /* JADX WARN: Removed duplicated region for block: B:254:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void createActionBarMenu(boolean r17) {
        /*
            Method dump skipped, instructions count: 1330
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.createActionBarMenu(boolean):void");
    }

    private void createAutoDeleteItem(Context context) {
        int i;
        this.autoDeletePopupWrapper = new AutoDeletePopupWrapper(context, this.otherItem.getPopupLayout().getSwipeBack(), new AutoDeletePopupWrapper.Callback() { // from class: org.telegram.ui.ProfileActivity.42
            @Override // org.telegram.ui.Components.AutoDeletePopupWrapper.Callback
            public void dismiss() {
                ProfileActivity.this.otherItem.toggleSubMenu();
            }

            @Override // org.telegram.ui.Components.AutoDeletePopupWrapper.Callback
            public void setAutoDeleteHistory(int i2, int i3) {
                ProfileActivity.this.setAutoDeleteHistory(i2, i3);
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

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public Theme.ResourcesProvider getResourceProvider() {
        return this.resourcesProvider;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public int getThemedColor(String str) {
        return Theme.getColor(str, this.resourcesProvider);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public Drawable getThemedDrawable(String str) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Drawable drawable = resourcesProvider != null ? resourcesProvider.getDrawable(str) : null;
        return drawable != null ? drawable : super.getThemedDrawable(str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setAutoDeleteHistory(int i, int i2) {
        long dialogId = getDialogId();
        getMessagesController().setDialogHistoryTTL(dialogId, i);
        if (this.userInfo == null && this.chatInfo == null) {
            return;
        }
        UndoView undoView = this.undoView;
        TLRPC$User user = getMessagesController().getUser(Long.valueOf(dialogId));
        TLRPC$UserFull tLRPC$UserFull = this.userInfo;
        undoView.showWithAction(dialogId, i2, user, Integer.valueOf(tLRPC$UserFull != null ? tLRPC$UserFull.ttl_period : this.chatInfo.ttl_period), (Runnable) null, (Runnable) null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onDialogDismiss(Dialog dialog) {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            recyclerListView.invalidateViews();
        }
    }

    @Override // org.telegram.ui.DialogsActivity.DialogsActivityDelegate
    public void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList<MessagesStorage.TopicKey> arrayList, CharSequence charSequence, boolean z) {
        long j = arrayList.get(0).dialogId;
        Bundle bundle = new Bundle();
        bundle.putBoolean("scrollToTopOnResume", true);
        if (DialogObject.isEncryptedDialog(j)) {
            bundle.putInt("enc_id", DialogObject.getEncryptedChatId(j));
        } else if (DialogObject.isUserDialog(j)) {
            bundle.putLong("user_id", j);
        } else if (DialogObject.isChatDialog(j)) {
            bundle.putLong("chat_id", -j);
        }
        if (!getMessagesController().checkCanOpenChat(bundle, dialogsActivity)) {
            return;
        }
        NotificationCenter notificationCenter = getNotificationCenter();
        int i = NotificationCenter.closeChats;
        notificationCenter.removeObserver(this, i);
        getNotificationCenter().postNotificationName(i, new Object[0]);
        presentFragment(new ChatActivity(bundle), true);
        removeSelfFromStack();
        getSendMessagesHelper().sendMessage(getMessagesController().getUser(Long.valueOf(this.userId)), j, (MessageObject) null, (MessageObject) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
        if (TextUtils.isEmpty(charSequence)) {
            return;
        }
        SendMessagesHelper.prepareSendingText(AccountInstance.getInstance(this.currentAccount), charSequence.toString(), j, true, 0);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        boolean z;
        boolean z2;
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.onRequestPermissionsResultFragment(i, strArr, iArr);
        }
        if (i == 101 || i == 102) {
            TLRPC$User user = getMessagesController().getUser(Long.valueOf(this.userId));
            if (user == null) {
                return;
            }
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
            if (iArr.length > 0 && z) {
                boolean z3 = i == 102;
                TLRPC$UserFull tLRPC$UserFull = this.userInfo;
                VoIPHelper.startCall(user, z3, tLRPC$UserFull != null && tLRPC$UserFull.video_calls_available, getParentActivity(), this.userInfo, getAccountInstance());
                return;
            }
            VoIPHelper.permissionDenied(getParentActivity(), null, i);
        } else if (i != 103 || this.currentChat == null) {
        } else {
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
            if (iArr.length > 0 && z2) {
                VoIPHelper.startCall(this.currentChat, null, null, getMessagesController().getGroupCall(this.chatId, false) == null, getParentActivity(), this, getAccountInstance());
                return;
            }
            VoIPHelper.permissionDenied(getParentActivity(), null, i);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void dismissCurrentDialog() {
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater == null || !imageUpdater.dismissCurrentDialog(this.visibleDialog)) {
            super.dismissCurrentDialog();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean dismissDialogOnPause(Dialog dialog) {
        ImageUpdater imageUpdater = this.imageUpdater;
        return (imageUpdater == null || imageUpdater.dismissDialogOnPause(dialog)) && super.dismissDialogOnPause(dialog);
    }

    /* JADX INFO: Access modifiers changed from: private */
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
        final ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
        final float f = this.extraHeight;
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
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda3
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                ProfileActivity.this.lambda$searchExpandTransition$39(ofFloat, f, z, valueAnimator);
            }
        });
        ofFloat.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ProfileActivity.43
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator2) {
                ProfileActivity.this.updateSearchViewState(z);
                ProfileActivity.this.avatarContainer.setClickable(true);
                if (z) {
                    ProfileActivity.this.searchItem.requestFocusOnSearchView();
                }
                ProfileActivity.this.needLayout(true);
                ProfileActivity.this.searchViewTransition = null;
                ((BaseFragment) ProfileActivity.this).fragmentView.invalidate();
                if (z) {
                    ProfileActivity.this.invalidateScroll = true;
                    ProfileActivity.this.saveScrollPosition();
                    AndroidUtilities.requestAdjustResize(ProfileActivity.this.getParentActivity(), ((BaseFragment) ProfileActivity.this).classGuid);
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
        ofFloat.setDuration(220L);
        ofFloat.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.searchViewTransition = ofFloat;
        return ofFloat;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$searchExpandTransition$39(ValueAnimator valueAnimator, float f, boolean z, ValueAnimator valueAnimator2) {
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

    /* JADX INFO: Access modifiers changed from: private */
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

    @Override // org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate
    public void onUploadProgressChanged(float f) {
        RadialProgressView radialProgressView = this.avatarProgressView;
        if (radialProgressView == null) {
            return;
        }
        radialProgressView.setProgress(f);
        this.avatarsViewPager.setUploadProgress(this.uploadingImageLocation, f);
    }

    @Override // org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate
    public void didStartUpload(boolean z) {
        RadialProgressView radialProgressView = this.avatarProgressView;
        if (radialProgressView == null) {
            return;
        }
        radialProgressView.setProgress(0.0f);
    }

    @Override // org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate
    public void didUploadPhoto(final TLRPC$InputFile tLRPC$InputFile, final TLRPC$InputFile tLRPC$InputFile2, final double d, final String str, final TLRPC$PhotoSize tLRPC$PhotoSize, final TLRPC$PhotoSize tLRPC$PhotoSize2) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda25
            @Override // java.lang.Runnable
            public final void run() {
                ProfileActivity.this.lambda$didUploadPhoto$42(tLRPC$InputFile, tLRPC$InputFile2, d, str, tLRPC$PhotoSize2, tLRPC$PhotoSize);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$didUploadPhoto$41(final String str, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda27
            @Override // java.lang.Runnable
            public final void run() {
                ProfileActivity.this.lambda$didUploadPhoto$40(tLRPC$TL_error, tLObject, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$didUploadPhoto$40(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, String str) {
        this.avatarsViewPager.removeUploadingImage(this.uploadingImageLocation);
        if (tLRPC$TL_error == null) {
            TLRPC$User user = getMessagesController().getUser(Long.valueOf(getUserConfig().getClientUserId()));
            if (user == null) {
                user = getUserConfig().getCurrentUser();
                if (user == null) {
                    return;
                }
                getMessagesController().putUser(user, false);
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
            if (closestPhotoSizeWithSize != null && this.avatar != null) {
                FileLoader.getInstance(this.currentAccount).getPathToAttach(this.avatar, true).renameTo(FileLoader.getInstance(this.currentAccount).getPathToAttach(closestPhotoSizeWithSize, true));
                ImageLoader.getInstance().replaceImageInCache(this.avatar.volume_id + "_" + this.avatar.local_id + "@50_50", closestPhotoSizeWithSize.location.volume_id + "_" + closestPhotoSizeWithSize.location.local_id + "@50_50", ImageLocation.getForUserOrChat(user, 1), false);
            }
            if (closestPhotoSizeWithSize2 != null && this.avatarBig != null) {
                FileLoader.getInstance(this.currentAccount).getPathToAttach(this.avatarBig, true).renameTo(FileLoader.getInstance(this.currentAccount).getPathToAttach(closestPhotoSizeWithSize2, true));
            }
            if (tLRPC$VideoSize != null && str != null) {
                new File(str).renameTo(FileLoader.getInstance(this.currentAccount).getPathToAttach(tLRPC$VideoSize, "mp4", true));
            }
            getMessagesStorage().clearUserPhotos(user.id);
            ArrayList<TLRPC$User> arrayList2 = new ArrayList<>();
            arrayList2.add(user);
            getMessagesStorage().putUsersAndChats(arrayList2, null, false, true);
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

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$didUploadPhoto$42(TLRPC$InputFile tLRPC$InputFile, TLRPC$InputFile tLRPC$InputFile2, double d, final String str, TLRPC$PhotoSize tLRPC$PhotoSize, TLRPC$PhotoSize tLRPC$PhotoSize2) {
        if (tLRPC$InputFile != null || tLRPC$InputFile2 != null) {
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
            getConnectionsManager().sendRequest(tLRPC$TL_photos_uploadProfilePhoto, new RequestDelegate() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda41
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    ProfileActivity.this.lambda$didUploadPhoto$41(str, tLObject, tLRPC$TL_error);
                }
            });
        } else {
            TLRPC$FileLocation tLRPC$FileLocation = tLRPC$PhotoSize.location;
            this.avatar = tLRPC$FileLocation;
            this.avatarBig = tLRPC$PhotoSize2.location;
            this.avatarImage.setImage(ImageLocation.getForLocal(tLRPC$FileLocation), "50_50", this.avatarDrawable, (Object) null);
            if (this.setAvatarRow != -1) {
                updateRowsIds();
                ListAdapter listAdapter = this.listAdapter;
                if (listAdapter != null) {
                    listAdapter.notifyDataSetChanged();
                }
                needLayout(true);
            }
            ProfileGalleryView profileGalleryView = this.avatarsViewPager;
            ImageLocation forLocal = ImageLocation.getForLocal(this.avatarBig);
            this.uploadingImageLocation = forLocal;
            profileGalleryView.addUploadingImage(forLocal, ImageLocation.getForLocal(this.avatar));
            showAvatarProgress(true, false);
        }
        this.actionBar.createMenu().requestLayout();
    }

    private void showAvatarProgress(final boolean z, boolean z2) {
        if (this.avatarProgressView == null) {
            return;
        }
        AnimatorSet animatorSet = this.avatarAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.avatarAnimation = null;
        }
        if (!z2) {
            if (z) {
                this.avatarProgressView.setAlpha(1.0f);
                this.avatarProgressView.setVisibility(0);
                return;
            }
            this.avatarProgressView.setAlpha(0.0f);
            this.avatarProgressView.setVisibility(4);
            return;
        }
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.avatarAnimation = animatorSet2;
        if (z) {
            this.avatarProgressView.setVisibility(0);
            this.avatarAnimation.playTogether(ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, 1.0f));
        } else {
            animatorSet2.playTogether(ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, 0.0f));
        }
        this.avatarAnimation.setDuration(180L);
        this.avatarAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ProfileActivity.44
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                if (ProfileActivity.this.avatarAnimation == null || ProfileActivity.this.avatarProgressView == null) {
                    return;
                }
                if (!z) {
                    ProfileActivity.this.avatarProgressView.setVisibility(4);
                }
                ProfileActivity.this.avatarAnimation = null;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                ProfileActivity.this.avatarAnimation = null;
            }
        });
        this.avatarAnimation.start();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onActivityResultFragment(int i, int i2, Intent intent) {
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.onActivityResult(i, i2, intent);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void saveSelfArgs(Bundle bundle) {
        String str;
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater == null || (str = imageUpdater.currentPicturePath) == null) {
            return;
        }
        bundle.putString("path", str);
    }

    public void restoreSelfArgs(Bundle bundle) {
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.currentPicturePath = bundle.getString("path");
        }
    }

    private void sendLogs(final boolean z) {
        if (getParentActivity() == null) {
            return;
        }
        final AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
        alertDialog.setCanCancel(false);
        alertDialog.show();
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda30
            @Override // java.lang.Runnable
            public final void run() {
                ProfileActivity.this.lambda$sendLogs$44(z, alertDialog);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$sendLogs$44(boolean z, final AlertDialog alertDialog) {
        ZipOutputStream zipOutputStream;
        try {
            BufferedInputStream bufferedInputStream = null;
            File externalFilesDir = ApplicationLoader.applicationContext.getExternalFilesDir(null);
            File file = new File(externalFilesDir.getAbsolutePath() + "/logs");
            final File file2 = new File(file, "logs.zip");
            if (file2.exists()) {
                file2.delete();
            }
            try {
                try {
                    File[] listFiles = file.listFiles();
                    final boolean[] zArr = new boolean[1];
                    long currentTimeMillis = System.currentTimeMillis();
                    try {
                        zipOutputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(file2)));
                        try {
                            byte[] bArr = new byte[65536];
                            for (int i = 0; i < listFiles.length; i++) {
                                if (!z || currentTimeMillis - listFiles[i].lastModified() <= 86400000) {
                                    BufferedInputStream bufferedInputStream2 = new BufferedInputStream(new FileInputStream(listFiles[i]), 65536);
                                    try {
                                        zipOutputStream.putNextEntry(new ZipEntry(listFiles[i].getName()));
                                        while (true) {
                                            int read = bufferedInputStream2.read(bArr, 0, 65536);
                                            if (read == -1) {
                                                break;
                                            }
                                            zipOutputStream.write(bArr, 0, read);
                                        }
                                        bufferedInputStream2.close();
                                    } catch (Exception e) {
                                        e = e;
                                        bufferedInputStream = bufferedInputStream2;
                                        e.printStackTrace();
                                        if (bufferedInputStream != null) {
                                            bufferedInputStream.close();
                                        }
                                        if (zipOutputStream != null) {
                                            zipOutputStream.close();
                                        }
                                        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda29
                                            @Override // java.lang.Runnable
                                            public final void run() {
                                                ProfileActivity.this.lambda$sendLogs$43(alertDialog, zArr, file2);
                                            }
                                        });
                                    } catch (Throwable th) {
                                        th = th;
                                        bufferedInputStream = bufferedInputStream2;
                                        if (bufferedInputStream != null) {
                                            bufferedInputStream.close();
                                        }
                                        if (zipOutputStream != null) {
                                            zipOutputStream.close();
                                        }
                                        throw th;
                                    }
                                }
                            }
                            zArr[0] = true;
                        } catch (Exception e2) {
                            e = e2;
                        }
                    } catch (Exception e3) {
                        e = e3;
                        zipOutputStream = null;
                    } catch (Throwable th2) {
                        th = th2;
                        zipOutputStream = null;
                    }
                    zipOutputStream.close();
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda29
                        @Override // java.lang.Runnable
                        public final void run() {
                            ProfileActivity.this.lambda$sendLogs$43(alertDialog, zArr, file2);
                        }
                    });
                } catch (Throwable th3) {
                    th = th3;
                }
            } catch (Exception e4) {
                e = e4;
                e.printStackTrace();
            }
        } catch (Exception e5) {
            e = e5;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$sendLogs$43(AlertDialog alertDialog, boolean[] zArr, File file) {
        Uri fromFile;
        try {
            alertDialog.dismiss();
        } catch (Exception unused) {
        }
        if (zArr[0]) {
            int i = Build.VERSION.SDK_INT;
            if (i >= 24) {
                Activity parentActivity = getParentActivity();
                fromFile = FileProvider.getUriForFile(parentActivity, ApplicationLoader.getApplicationId() + ".provider", file);
            } else {
                fromFile = Uri.fromFile(file);
            }
            Intent intent = new Intent("android.intent.action.SEND");
            if (i >= 24) {
                intent.addFlags(1);
            }
            intent.setType("message/rfCLASSNAME");
            intent.putExtra("android.intent.extra.EMAIL", "");
            intent.putExtra("android.intent.extra.SUBJECT", "Logs from " + LocaleController.getInstance().formatterStats.format(System.currentTimeMillis()));
            intent.putExtra("android.intent.extra.STREAM", fromFile);
            if (getParentActivity() == null) {
                return;
            }
            try {
                getParentActivity().startActivityForResult(Intent.createChooser(intent, "Select email application."), 500);
            } catch (Exception e) {
                FileLog.e(e);
            }
        } else if (getParentActivity() == null) {
        } else {
            Toast.makeText(getParentActivity(), LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred), 0).show();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /* renamed from: onCreateViewHolder */
        public RecyclerView.ViewHolder mo1788onCreateViewHolder(ViewGroup viewGroup, int i) {
            View headerCell;
            TextDetailCell textDetailCell;
            String str;
            boolean z = false;
            switch (i) {
                case 1:
                    headerCell = new HeaderCell(this.mContext, 23, ProfileActivity.this.resourcesProvider);
                    break;
                case 2:
                case 19:
                    Context context = this.mContext;
                    Theme.ResourcesProvider resourcesProvider = ProfileActivity.this.resourcesProvider;
                    if (i == 19) {
                        z = true;
                    }
                    TextDetailCell textDetailCell2 = new TextDetailCell(context, resourcesProvider, z);
                    textDetailCell2.setContentDescriptionValueFirst(true);
                    final ProfileActivity profileActivity = ProfileActivity.this;
                    textDetailCell2.setImageClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ProfileActivity$ListAdapter$$ExternalSyntheticLambda1
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            ProfileActivity.access$27200(ProfileActivity.this, view);
                        }
                    });
                    textDetailCell = textDetailCell2;
                    headerCell = textDetailCell;
                    break;
                case 3:
                    ProfileActivity profileActivity2 = ProfileActivity.this;
                    Context context2 = this.mContext;
                    ProfileActivity profileActivity3 = ProfileActivity.this;
                    headerCell = profileActivity2.aboutLinkCell = new AboutLinkCell(context2, profileActivity3, profileActivity3.resourcesProvider) { // from class: org.telegram.ui.ProfileActivity.ListAdapter.1
                        @Override // org.telegram.ui.Cells.AboutLinkCell
                        protected void didPressUrl(String str2) {
                            ProfileActivity.this.openUrl(str2);
                        }

                        @Override // org.telegram.ui.Cells.AboutLinkCell
                        protected void didResizeEnd() {
                            ProfileActivity.this.layoutManager.mIgnoreTopPadding = false;
                        }

                        @Override // org.telegram.ui.Cells.AboutLinkCell
                        protected void didResizeStart() {
                            ProfileActivity.this.layoutManager.mIgnoreTopPadding = true;
                        }
                    };
                    break;
                case 4:
                    headerCell = new TextCell(this.mContext, ProfileActivity.this.resourcesProvider);
                    break;
                case 5:
                    headerCell = new DividerCell(this.mContext, ProfileActivity.this.resourcesProvider);
                    headerCell.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(4.0f), 0, 0);
                    break;
                case 6:
                    headerCell = new NotificationsCheckCell(this.mContext, 23, 70, false, ProfileActivity.this.resourcesProvider);
                    break;
                case 7:
                    headerCell = new ShadowSectionCell(this.mContext, ProfileActivity.this.resourcesProvider);
                    break;
                case 8:
                    headerCell = new UserCell(this.mContext, ProfileActivity.this.addMemberRow == -1 ? 9 : 6, 0, true, ProfileActivity.this.resourcesProvider);
                    break;
                case 9:
                case 10:
                case 14:
                case 16:
                default:
                    TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(this.mContext, 10, ProfileActivity.this.resourcesProvider);
                    textInfoPrivacyCell.getTextView().setGravity(1);
                    textInfoPrivacyCell.getTextView().setTextColor(ProfileActivity.this.getThemedColor("windowBackgroundWhiteGrayText3"));
                    textInfoPrivacyCell.getTextView().setMovementMethod(null);
                    try {
                        PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                        int i2 = packageInfo.versionCode;
                        int i3 = i2 / 10;
                        int i4 = i2 % 10;
                        if (i4 == 1 || i4 == 2) {
                            str = "store bundled " + Build.CPU_ABI + " " + Build.CPU_ABI2;
                        } else if (BuildVars.isStandaloneApp()) {
                            str = "direct " + Build.CPU_ABI + " " + Build.CPU_ABI2;
                        } else {
                            str = "universal " + Build.CPU_ABI + " " + Build.CPU_ABI2;
                        }
                        textInfoPrivacyCell.setText(LocaleController.formatString("TelegramVersion", R.string.TelegramVersion, String.format(Locale.US, "v%s (%d) %s", packageInfo.versionName, Integer.valueOf(i3), str)));
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    textInfoPrivacyCell.getTextView().setPadding(0, AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(14.0f));
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider_bottom, ProfileActivity.this.getThemedColor("windowBackgroundGrayShadow")));
                    textDetailCell = textInfoPrivacyCell;
                    headerCell = textDetailCell;
                    break;
                case 11:
                    headerCell = new View(this, this.mContext) { // from class: org.telegram.ui.ProfileActivity.ListAdapter.2
                        @Override // android.view.View
                        protected void onMeasure(int i5, int i6) {
                            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i5), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), NUM));
                        }
                    };
                    break;
                case 12:
                    headerCell = new View(this.mContext) { // from class: org.telegram.ui.ProfileActivity.ListAdapter.3
                        private int lastPaddingHeight = 0;
                        private int lastListViewHeight = 0;

                        @Override // android.view.View
                        protected void onMeasure(int i5, int i6) {
                            int i7 = 0;
                            if (this.lastListViewHeight != ProfileActivity.this.listView.getMeasuredHeight()) {
                                this.lastPaddingHeight = 0;
                            }
                            this.lastListViewHeight = ProfileActivity.this.listView.getMeasuredHeight();
                            int childCount = ProfileActivity.this.listView.getChildCount();
                            if (childCount == ProfileActivity.this.listAdapter.getItemCount()) {
                                int i8 = 0;
                                for (int i9 = 0; i9 < childCount; i9++) {
                                    int childAdapterPosition = ProfileActivity.this.listView.getChildAdapterPosition(ProfileActivity.this.listView.getChildAt(i9));
                                    if (childAdapterPosition >= 0 && childAdapterPosition != ProfileActivity.this.bottomPaddingRow) {
                                        i8 += ProfileActivity.this.listView.getChildAt(i9).getMeasuredHeight();
                                    }
                                }
                                int measuredHeight = ((((BaseFragment) ProfileActivity.this).fragmentView.getMeasuredHeight() - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.statusBarHeight) - i8;
                                if (measuredHeight > AndroidUtilities.dp(88.0f)) {
                                    measuredHeight = 0;
                                }
                                if (measuredHeight > 0) {
                                    i7 = measuredHeight;
                                }
                                int measuredWidth = ProfileActivity.this.listView.getMeasuredWidth();
                                this.lastPaddingHeight = i7;
                                setMeasuredDimension(measuredWidth, i7);
                                return;
                            }
                            setMeasuredDimension(ProfileActivity.this.listView.getMeasuredWidth(), this.lastPaddingHeight);
                        }
                    };
                    headerCell.setBackground(new ColorDrawable(0));
                    break;
                case 13:
                    if (ProfileActivity.this.sharedMediaLayout.getParent() != null) {
                        ((ViewGroup) ProfileActivity.this.sharedMediaLayout.getParent()).removeView(ProfileActivity.this.sharedMediaLayout);
                    }
                    headerCell = ProfileActivity.this.sharedMediaLayout;
                    break;
                case 15:
                    headerCell = new SettingsSuggestionCell(this.mContext, ProfileActivity.this.resourcesProvider) { // from class: org.telegram.ui.ProfileActivity.ListAdapter.4
                        @Override // org.telegram.ui.Cells.SettingsSuggestionCell
                        protected void onYesClick(int i5) {
                            NotificationCenter notificationCenter = ProfileActivity.this.getNotificationCenter();
                            ProfileActivity profileActivity4 = ProfileActivity.this;
                            int i6 = NotificationCenter.newSuggestionsAvailable;
                            notificationCenter.removeObserver(profileActivity4, i6);
                            ProfileActivity.this.getMessagesController().removeSuggestion(0L, i5 == 0 ? "VALIDATE_PHONE_NUMBER" : "VALIDATE_PASSWORD");
                            ProfileActivity.this.getNotificationCenter().addObserver(ProfileActivity.this, i6);
                            if (i5 == 0) {
                                int unused = ProfileActivity.this.phoneSuggestionRow;
                            } else {
                                int unused2 = ProfileActivity.this.passwordSuggestionRow;
                            }
                            ProfileActivity.this.updateListAnimated(false);
                        }

                        @Override // org.telegram.ui.Cells.SettingsSuggestionCell
                        protected void onNoClick(int i5) {
                            if (i5 == 0) {
                                ProfileActivity.this.presentFragment(new ActionIntroActivity(3));
                            } else {
                                ProfileActivity.this.presentFragment(new TwoStepVerificationSetupActivity(8, null));
                            }
                        }
                    };
                    break;
                case 17:
                    headerCell = new TextInfoPrivacyCell(this.mContext, ProfileActivity.this.resourcesProvider);
                    break;
                case 18:
                    headerCell = new ProfilePremiumCell(this.mContext, ProfileActivity.this.resourcesProvider);
                    break;
                case 20:
                    headerCell = new TextCheckCell(this.mContext, ProfileActivity.this.resourcesProvider);
                    break;
            }
            if (i != 13) {
                headerCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            }
            return new RecyclerListView.Holder(headerCell);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.itemView == ProfileActivity.this.sharedMediaLayout) {
                ProfileActivity.this.sharedMediaLayoutAttached = true;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewDetachedFromWindow(RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.itemView == ProfileActivity.this.sharedMediaLayout) {
                ProfileActivity.this.sharedMediaLayoutAttached = false;
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Removed duplicated region for block: B:123:0x032a A[ADDED_TO_REGION] */
        /* JADX WARN: Removed duplicated region for block: B:126:0x0337  */
        /* JADX WARN: Removed duplicated region for block: B:127:0x033c  */
        /* JADX WARN: Type inference failed for: r0v25, types: [org.telegram.tgnet.TLRPC$ChatParticipant] */
        /* JADX WARN: Type inference failed for: r0v30, types: [org.telegram.tgnet.TLRPC$ChatParticipant] */
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r23, final int r24) {
            /*
                Method dump skipped, instructions count: 3378
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onBindViewHolder$1(View view) {
            if (ProfileActivity.this.userInfo != null) {
                ProfileActivity.this.presentFragment(new ChangeBioActivity());
            }
        }

        private CharSequence alsoUsernamesString(String str, ArrayList<TLRPC$TL_username> arrayList, CharSequence charSequence) {
            if (arrayList == null) {
                return charSequence;
            }
            ArrayList arrayList2 = new ArrayList(arrayList);
            int i = 0;
            while (i < arrayList2.size()) {
                if (!((TLRPC$TL_username) arrayList2.get(i)).active || (str != null && str.equals(((TLRPC$TL_username) arrayList2.get(i)).username))) {
                    arrayList2.remove(i);
                    i--;
                }
                i++;
            }
            SpannableStringBuilder spannableStringBuilder = charSequence;
            if (arrayList2.size() > 0) {
                SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder();
                for (int i2 = 0; i2 < arrayList2.size(); i2++) {
                    final String str2 = ((TLRPC$TL_username) arrayList2.get(i2)).username;
                    SpannableString spannableString = new SpannableString("@" + str2);
                    spannableString.setSpan(new ClickableSpan() { // from class: org.telegram.ui.ProfileActivity.ListAdapter.6
                        @Override // android.text.style.ClickableSpan
                        public void onClick(View view) {
                            String str3 = ProfileActivity.this.getMessagesController().linkPrefix + "/" + str2;
                            if (ProfileActivity.this.currentChat == null || !ProfileActivity.this.currentChat.noforwards) {
                                AndroidUtilities.addToClipboard(str3);
                                ProfileActivity.this.undoView.showWithAction(0L, 56, (Runnable) null);
                            }
                        }

                        @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
                        public void updateDrawState(TextPaint textPaint) {
                            textPaint.setUnderlineText(false);
                        }
                    }, 0, spannableString.length(), 33);
                    spannableString.setSpan(new ForegroundColorSpan(ProfileActivity.this.getThemedColor("chat_messageLinkIn")), 0, spannableString.length(), 33);
                    spannableStringBuilder2.append((CharSequence) spannableString);
                    if (i2 < arrayList2.size() - 1) {
                        spannableStringBuilder2.append((CharSequence) ", ");
                    }
                }
                String string = LocaleController.getString("UsernameAlso", R.string.UsernameAlso);
                SpannableStringBuilder spannableStringBuilder3 = new SpannableStringBuilder(string);
                int indexOf = string.indexOf("%1$s");
                spannableStringBuilder = spannableStringBuilder3;
                if (indexOf >= 0) {
                    spannableStringBuilder3.replace(indexOf, indexOf + 4, (CharSequence) spannableStringBuilder2);
                    spannableStringBuilder = spannableStringBuilder3;
                }
            }
            return spannableStringBuilder;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getAdapterPosition() == ProfileActivity.this.setAvatarRow) {
                ProfileActivity.this.setAvatarCell = null;
            }
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            if (ProfileActivity.this.notificationRow != -1) {
                int adapterPosition = viewHolder.getAdapterPosition();
                return adapterPosition == ProfileActivity.this.notificationRow || adapterPosition == ProfileActivity.this.numberRow || adapterPosition == ProfileActivity.this.privacyRow || adapterPosition == ProfileActivity.this.languageRow || adapterPosition == ProfileActivity.this.setUsernameRow || adapterPosition == ProfileActivity.this.bioRow || adapterPosition == ProfileActivity.this.versionRow || adapterPosition == ProfileActivity.this.dataRow || adapterPosition == ProfileActivity.this.chatRow || adapterPosition == ProfileActivity.this.questionRow || adapterPosition == ProfileActivity.this.devicesRow || adapterPosition == ProfileActivity.this.filtersRow || adapterPosition == ProfileActivity.this.stickersRow || adapterPosition == ProfileActivity.this.faqRow || adapterPosition == ProfileActivity.this.policyRow || adapterPosition == ProfileActivity.this.sendLogsRow || adapterPosition == ProfileActivity.this.sendLastLogsRow || adapterPosition == ProfileActivity.this.clearLogsRow || adapterPosition == ProfileActivity.this.switchBackendRow || adapterPosition == ProfileActivity.this.setAvatarRow || adapterPosition == ProfileActivity.this.addToGroupButtonRow || adapterPosition == ProfileActivity.this.premiumRow;
            }
            View view = viewHolder.itemView;
            if (view instanceof UserCell) {
                Object currentObject = ((UserCell) view).getCurrentObject();
                if ((currentObject instanceof TLRPC$User) && UserObject.isUserSelf((TLRPC$User) currentObject)) {
                    return false;
                }
            }
            int itemViewType = viewHolder.getItemViewType();
            return (itemViewType == 1 || itemViewType == 5 || itemViewType == 7 || itemViewType == 11 || itemViewType == 12 || itemViewType == 13 || itemViewType == 9 || itemViewType == 10) ? false : true;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return ProfileActivity.this.rowCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (i == ProfileActivity.this.infoHeaderRow || i == ProfileActivity.this.membersHeaderRow || i == ProfileActivity.this.settingsSectionRow2 || i == ProfileActivity.this.numberSectionRow || i == ProfileActivity.this.helpHeaderRow || i == ProfileActivity.this.debugHeaderRow) {
                return 1;
            }
            if (i == ProfileActivity.this.phoneRow || i == ProfileActivity.this.locationRow || i == ProfileActivity.this.numberRow) {
                return 2;
            }
            if (i == ProfileActivity.this.usernameRow || i == ProfileActivity.this.setUsernameRow) {
                return 19;
            }
            if (i == ProfileActivity.this.userInfoRow || i == ProfileActivity.this.channelInfoRow || i == ProfileActivity.this.bioRow) {
                return 3;
            }
            if (i == ProfileActivity.this.settingsTimerRow || i == ProfileActivity.this.settingsKeyRow || i == ProfileActivity.this.reportRow || i == ProfileActivity.this.reportReactionRow || i == ProfileActivity.this.subscribersRow || i == ProfileActivity.this.subscribersRequestsRow || i == ProfileActivity.this.administratorsRow || i == ProfileActivity.this.blockedUsersRow || i == ProfileActivity.this.addMemberRow || i == ProfileActivity.this.joinRow || i == ProfileActivity.this.unblockRow || i == ProfileActivity.this.sendMessageRow || i == ProfileActivity.this.notificationRow || i == ProfileActivity.this.privacyRow || i == ProfileActivity.this.languageRow || i == ProfileActivity.this.dataRow || i == ProfileActivity.this.chatRow || i == ProfileActivity.this.questionRow || i == ProfileActivity.this.devicesRow || i == ProfileActivity.this.filtersRow || i == ProfileActivity.this.stickersRow || i == ProfileActivity.this.faqRow || i == ProfileActivity.this.policyRow || i == ProfileActivity.this.sendLogsRow || i == ProfileActivity.this.sendLastLogsRow || i == ProfileActivity.this.clearLogsRow || i == ProfileActivity.this.switchBackendRow || i == ProfileActivity.this.setAvatarRow || i == ProfileActivity.this.addToGroupButtonRow) {
                return 4;
            }
            if (i == ProfileActivity.this.notificationsDividerRow) {
                return 5;
            }
            if (i == ProfileActivity.this.notificationsRow) {
                return 6;
            }
            if (i == ProfileActivity.this.notificationsSimpleRow) {
                return 20;
            }
            if (i == ProfileActivity.this.infoSectionRow || i == ProfileActivity.this.lastSectionRow || i == ProfileActivity.this.membersSectionRow || i == ProfileActivity.this.secretSettingsSectionRow || i == ProfileActivity.this.settingsSectionRow || i == ProfileActivity.this.devicesSectionRow || i == ProfileActivity.this.helpSectionCell || i == ProfileActivity.this.setAvatarSectionRow || i == ProfileActivity.this.passwordSuggestionSectionRow || i == ProfileActivity.this.phoneSuggestionSectionRow || i == ProfileActivity.this.premiumSectionsRow || i == ProfileActivity.this.reportDividerRow) {
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

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class SearchAdapter extends RecyclerListView.SelectionAdapter {
        private ArrayList<MessagesController.FaqSearchResult> faqSearchArray;
        private ArrayList<MessagesController.FaqSearchResult> faqSearchResults;
        private TLRPC$WebPage faqWebPage;
        private String lastSearchString;
        private boolean loadingFaqPage;
        private Context mContext;
        private ArrayList<Object> recentSearches;
        private ArrayList<CharSequence> resultNames;
        private SearchResult[] searchArray;
        private ArrayList<SearchResult> searchResults;
        private Runnable searchRunnable;
        private boolean searchWas;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public class SearchResult {
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
                } else if (str3 == null) {
                } else {
                    this.path = new String[]{str3};
                }
            }

            public boolean equals(Object obj) {
                return (obj instanceof SearchResult) && this.guid == ((SearchResult) obj).guid;
            }

            public String toString() {
                SerializedData serializedData = new SerializedData();
                serializedData.writeInt32(this.num);
                serializedData.writeInt32(1);
                serializedData.writeInt32(this.guid);
                return Utilities.bytesToHex(serializedData.toByteArray());
            }

            /* JADX INFO: Access modifiers changed from: private */
            public void open() {
                this.openRunnable.run();
                AndroidUtilities.scrollToFragmentRow(((BaseFragment) ProfileActivity.this).parentLayout, this.rowName);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0() {
            ProfileActivity profileActivity = ProfileActivity.this;
            profileActivity.presentFragment(new ChangeNameActivity(profileActivity.resourcesProvider));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$1() {
            ProfileActivity.this.presentFragment(new ActionIntroActivity(3));
        }

        /* JADX INFO: Access modifiers changed from: private */
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
                ProfileActivity.this.presentFragment(new LoginActivity(i));
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$3() {
            if (ProfileActivity.this.userInfo != null) {
                ProfileActivity.this.presentFragment(new ChangeBioActivity());
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$4() {
            ProfileActivity.this.presentFragment(new NotificationsSettingsActivity());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$5() {
            ProfileActivity.this.presentFragment(new NotificationsCustomSettingsActivity(1, new ArrayList(), true));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$6() {
            ProfileActivity.this.presentFragment(new NotificationsCustomSettingsActivity(0, new ArrayList(), true));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$7() {
            ProfileActivity.this.presentFragment(new NotificationsCustomSettingsActivity(2, new ArrayList(), true));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$8() {
            ProfileActivity.this.presentFragment(new NotificationsSettingsActivity());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$9() {
            ProfileActivity.this.presentFragment(new NotificationsSettingsActivity());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$10() {
            ProfileActivity.this.presentFragment(new NotificationsSettingsActivity());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$11() {
            ProfileActivity.this.presentFragment(new NotificationsSettingsActivity());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$12() {
            ProfileActivity.this.presentFragment(new NotificationsSettingsActivity());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$13() {
            ProfileActivity.this.presentFragment(new NotificationsSettingsActivity());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$14() {
            ProfileActivity.this.presentFragment(new PrivacySettingsActivity());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$15() {
            ProfileActivity.this.presentFragment(new PrivacyUsersActivity());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$16() {
            ProfileActivity.this.presentFragment(new PrivacyControlActivity(6, true));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$17() {
            ProfileActivity.this.presentFragment(new PrivacyControlActivity(0, true));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$18() {
            ProfileActivity.this.presentFragment(new PrivacyControlActivity(4, true));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$19() {
            ProfileActivity.this.presentFragment(new PrivacyControlActivity(5, true));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$20() {
            ProfileActivity.this.presentFragment(new PrivacyControlActivity(3, true));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$21() {
            ProfileActivity.this.presentFragment(new PrivacyControlActivity(2, true));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$22() {
            ProfileActivity.this.presentFragment(new PrivacyControlActivity(1, true));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$23() {
            if (!ProfileActivity.this.getUserConfig().isPremium()) {
                try {
                    ((BaseFragment) ProfileActivity.this).fragmentView.performHapticFeedback(3, 2);
                } catch (Exception e) {
                    FileLog.e(e);
                }
                BulletinFactory.of(ProfileActivity.this).createRestrictVoiceMessagesPremiumBulletin().show();
                return;
            }
            ProfileActivity.this.presentFragment(new PrivacyControlActivity(8, true));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$24() {
            ProfileActivity.this.presentFragment(PasscodeActivity.determineOpenFragment());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$25() {
            ProfileActivity.this.presentFragment(new TwoStepVerificationActivity());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$26() {
            ProfileActivity.this.presentFragment(new SessionsActivity(0));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$27() {
            ProfileActivity.this.presentFragment(new PrivacySettingsActivity());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$28() {
            ProfileActivity.this.presentFragment(new PrivacySettingsActivity());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$29() {
            ProfileActivity.this.presentFragment(new PrivacySettingsActivity());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$30() {
            ProfileActivity.this.presentFragment(new SessionsActivity(1));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$31() {
            ProfileActivity.this.presentFragment(new PrivacySettingsActivity());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$32() {
            ProfileActivity.this.presentFragment(new PrivacySettingsActivity());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$33() {
            ProfileActivity.this.presentFragment(new PrivacySettingsActivity());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$34() {
            ProfileActivity.this.presentFragment(new PrivacySettingsActivity());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$35() {
            ProfileActivity.this.presentFragment(new PrivacySettingsActivity());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$36() {
            ProfileActivity.this.presentFragment(new SessionsActivity(0));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$37() {
            ProfileActivity.this.presentFragment(new DataSettingsActivity());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$38() {
            ProfileActivity.this.presentFragment(new DataSettingsActivity());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$39() {
            ProfileActivity.this.presentFragment(new CacheControlActivity());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$40() {
            ProfileActivity.this.presentFragment(new CacheControlActivity());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$41() {
            ProfileActivity.this.presentFragment(new CacheControlActivity());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$42() {
            ProfileActivity.this.presentFragment(new CacheControlActivity());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$43() {
            ProfileActivity.this.presentFragment(new DataUsageActivity());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$44() {
            ProfileActivity.this.presentFragment(new DataSettingsActivity());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$45() {
            ProfileActivity.this.presentFragment(new DataAutoDownloadActivity(0));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$46() {
            ProfileActivity.this.presentFragment(new DataAutoDownloadActivity(1));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$47() {
            ProfileActivity.this.presentFragment(new DataAutoDownloadActivity(2));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$48() {
            ProfileActivity.this.presentFragment(new DataSettingsActivity());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$49() {
            ProfileActivity.this.presentFragment(new DataSettingsActivity());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$50() {
            ProfileActivity.this.presentFragment(new DataSettingsActivity());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$51() {
            ProfileActivity.this.presentFragment(new DataSettingsActivity());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$52() {
            ProfileActivity.this.presentFragment(new DataSettingsActivity());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$53() {
            ProfileActivity.this.presentFragment(new DataSettingsActivity());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$54() {
            ProfileActivity.this.presentFragment(new DataSettingsActivity());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$55() {
            ProfileActivity.this.presentFragment(new DataSettingsActivity());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$56() {
            ProfileActivity.this.presentFragment(new DataSettingsActivity());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$57() {
            ProfileActivity.this.presentFragment(new ProxyListActivity());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$58() {
            ProfileActivity.this.presentFragment(new ProxyListActivity());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$59() {
            ProfileActivity.this.presentFragment(new DataSettingsActivity());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$60() {
            ProfileActivity.this.presentFragment(new ThemeActivity(0));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$61() {
            ProfileActivity.this.presentFragment(new ThemeActivity(0));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$62() {
            ProfileActivity.this.presentFragment(new WallpapersListActivity(0));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$63() {
            ProfileActivity.this.presentFragment(new WallpapersListActivity(1));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$64() {
            ProfileActivity.this.presentFragment(new WallpapersListActivity(0));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$65() {
            ProfileActivity.this.presentFragment(new ThemeActivity(1));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$66() {
            ProfileActivity.this.presentFragment(new ThemeActivity(0));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$67() {
            ProfileActivity.this.presentFragment(new ThemeActivity(0));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$68() {
            ProfileActivity.this.presentFragment(new ThemeActivity(0));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$69() {
            ProfileActivity.this.presentFragment(new ThemeActivity(0));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$70() {
            ProfileActivity.this.presentFragment(new ThemeActivity(0));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$71() {
            ProfileActivity.this.presentFragment(new ThemeActivity(0));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$72() {
            ProfileActivity.this.presentFragment(new ThemeActivity(0));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$73() {
            ProfileActivity.this.presentFragment(new ThemeActivity(0));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$74() {
            ProfileActivity.this.presentFragment(new StickersActivity(0, null));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$75() {
            ProfileActivity.this.presentFragment(new StickersActivity(0, null));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$76() {
            ProfileActivity.this.presentFragment(new FeaturedStickersActivity());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$77() {
            ProfileActivity.this.presentFragment(new StickersActivity(1, null));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$78() {
            ProfileActivity.this.presentFragment(new ArchivedStickersActivity(0));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$79() {
            ProfileActivity.this.presentFragment(new ArchivedStickersActivity(1));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$80() {
            ProfileActivity.this.presentFragment(new LanguageSelectActivity());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$81() {
            ProfileActivity profileActivity = ProfileActivity.this;
            profileActivity.showDialog(AlertsCreator.createSupportAlert(profileActivity, null));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$82() {
            Browser.openUrl(ProfileActivity.this.getParentActivity(), LocaleController.getString("TelegramFaqUrl", R.string.TelegramFaqUrl));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$83() {
            Browser.openUrl(ProfileActivity.this.getParentActivity(), LocaleController.getString("PrivacyPolicyUrl", R.string.PrivacyPolicyUrl));
        }

        public SearchAdapter(Context context) {
            SerializedData serializedData;
            boolean z;
            int readInt32;
            int readInt322;
            String[] strArr;
            SearchResult[] searchResultArr = new SearchResult[84];
            searchResultArr[0] = new SearchResult(this, 500, LocaleController.getString("EditName", R.string.EditName), 0, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda10
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$0();
                }
            });
            searchResultArr[1] = new SearchResult(this, 501, LocaleController.getString("ChangePhoneNumber", R.string.ChangePhoneNumber), 0, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda11
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$1();
                }
            });
            searchResultArr[2] = new SearchResult(this, 502, LocaleController.getString("AddAnotherAccount", R.string.AddAnotherAccount), 0, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda32
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$2();
                }
            });
            searchResultArr[3] = new SearchResult(this, 503, LocaleController.getString("UserBio", R.string.UserBio), 0, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda63
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$3();
                }
            });
            int i = R.string.NotificationsAndSounds;
            String string = LocaleController.getString("NotificationsAndSounds", i);
            int i2 = R.drawable.msg_notifications;
            searchResultArr[4] = new SearchResult(this, 1, string, i2, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda18
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$4();
                }
            });
            searchResultArr[5] = new SearchResult(this, 2, LocaleController.getString("NotificationsPrivateChats", R.string.NotificationsPrivateChats), LocaleController.getString("NotificationsAndSounds", i), i2, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$5();
                }
            });
            searchResultArr[6] = new SearchResult(this, 3, LocaleController.getString("NotificationsGroups", R.string.NotificationsGroups), LocaleController.getString("NotificationsAndSounds", i), i2, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda22
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$6();
                }
            });
            searchResultArr[7] = new SearchResult(this, 4, LocaleController.getString("NotificationsChannels", R.string.NotificationsChannels), LocaleController.getString("NotificationsAndSounds", i), i2, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda42
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$7();
                }
            });
            searchResultArr[8] = new SearchResult(this, 5, LocaleController.getString("VoipNotificationSettings", R.string.VoipNotificationSettings), "callsSectionRow", LocaleController.getString("NotificationsAndSounds", i), i2, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$8();
                }
            });
            searchResultArr[9] = new SearchResult(this, 6, LocaleController.getString("BadgeNumber", R.string.BadgeNumber), "badgeNumberSection", LocaleController.getString("NotificationsAndSounds", i), i2, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$9();
                }
            });
            searchResultArr[10] = new SearchResult(this, 7, LocaleController.getString("InAppNotifications", R.string.InAppNotifications), "inappSectionRow", LocaleController.getString("NotificationsAndSounds", i), i2, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda64
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$10();
                }
            });
            searchResultArr[11] = new SearchResult(this, 8, LocaleController.getString("ContactJoined", R.string.ContactJoined), "contactJoinedRow", LocaleController.getString("NotificationsAndSounds", i), i2, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda52
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$11();
                }
            });
            searchResultArr[12] = new SearchResult(this, 9, LocaleController.getString("PinnedMessages", R.string.PinnedMessages), "pinnedMessageRow", LocaleController.getString("NotificationsAndSounds", i), i2, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda62
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$12();
                }
            });
            searchResultArr[13] = new SearchResult(this, 10, LocaleController.getString("ResetAllNotifications", R.string.ResetAllNotifications), "resetNotificationsRow", LocaleController.getString("NotificationsAndSounds", i), i2, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda13
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$13();
                }
            });
            int i3 = R.string.PrivacySettings;
            String string2 = LocaleController.getString("PrivacySettings", i3);
            int i4 = R.drawable.msg_secret;
            searchResultArr[14] = new SearchResult(this, 100, string2, i4, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda24
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$14();
                }
            });
            searchResultArr[15] = new SearchResult(this, 101, LocaleController.getString("BlockedUsers", R.string.BlockedUsers), LocaleController.getString("PrivacySettings", i3), i4, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda75
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$15();
                }
            });
            searchResultArr[16] = new SearchResult(this, 105, LocaleController.getString("PrivacyPhone", R.string.PrivacyPhone), LocaleController.getString("PrivacySettings", i3), i4, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda72
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$16();
                }
            });
            searchResultArr[17] = new SearchResult(this, 102, LocaleController.getString("PrivacyLastSeen", R.string.PrivacyLastSeen), LocaleController.getString("PrivacySettings", i3), i4, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda26
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$17();
                }
            });
            searchResultArr[18] = new SearchResult(this, 103, LocaleController.getString("PrivacyProfilePhoto", R.string.PrivacyProfilePhoto), LocaleController.getString("PrivacySettings", i3), i4, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda55
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$18();
                }
            });
            searchResultArr[19] = new SearchResult(this, 104, LocaleController.getString("PrivacyForwards", R.string.PrivacyForwards), LocaleController.getString("PrivacySettings", i3), i4, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda78
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$19();
                }
            });
            searchResultArr[20] = new SearchResult(this, 122, LocaleController.getString("PrivacyP2P", R.string.PrivacyP2P), LocaleController.getString("PrivacySettings", i3), i4, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda61
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$20();
                }
            });
            int i5 = R.string.Calls;
            searchResultArr[21] = new SearchResult(this, 106, LocaleController.getString("Calls", i5), LocaleController.getString("PrivacySettings", i3), i4, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$21();
                }
            });
            searchResultArr[22] = new SearchResult(this, 107, LocaleController.getString("GroupsAndChannels", R.string.GroupsAndChannels), LocaleController.getString("PrivacySettings", i3), i4, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda29
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$22();
                }
            });
            searchResultArr[23] = new SearchResult(this, 123, LocaleController.getString("PrivacyVoiceMessages", R.string.PrivacyVoiceMessages), LocaleController.getString("PrivacySettings", i3), i4, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda23
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$23();
                }
            });
            searchResultArr[24] = new SearchResult(this, 108, LocaleController.getString("Passcode", R.string.Passcode), LocaleController.getString("PrivacySettings", i3), i4, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda70
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$24();
                }
            });
            searchResultArr[25] = new SearchResult(this, 109, LocaleController.getString("TwoStepVerification", R.string.TwoStepVerification), LocaleController.getString("PrivacySettings", i3), i4, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda35
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$25();
                }
            });
            searchResultArr[26] = new SearchResult(this, 110, LocaleController.getString("SessionsTitle", R.string.SessionsTitle), i4, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda17
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$26();
                }
            });
            searchResultArr[27] = ProfileActivity.this.getMessagesController().autoarchiveAvailable ? new SearchResult(this, 121, LocaleController.getString("ArchiveAndMute", R.string.ArchiveAndMute), "newChatsRow", LocaleController.getString("PrivacySettings", i3), i4, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda46
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$27();
                }
            }) : null;
            searchResultArr[28] = new SearchResult(this, 112, LocaleController.getString("DeleteAccountIfAwayFor2", R.string.DeleteAccountIfAwayFor2), "deleteAccountRow", LocaleController.getString("PrivacySettings", i3), i4, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda79
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$28();
                }
            });
            searchResultArr[29] = new SearchResult(this, 113, LocaleController.getString("PrivacyPaymentsClear", R.string.PrivacyPaymentsClear), "paymentsClearRow", LocaleController.getString("PrivacySettings", i3), i4, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda77
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$29();
                }
            });
            searchResultArr[30] = new SearchResult(this, 114, LocaleController.getString("WebSessionsTitle", R.string.WebSessionsTitle), LocaleController.getString("PrivacySettings", i3), i4, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda58
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$30();
                }
            });
            searchResultArr[31] = new SearchResult(this, 115, LocaleController.getString("SyncContactsDelete", R.string.SyncContactsDelete), "contactsDeleteRow", LocaleController.getString("PrivacySettings", i3), i4, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda19
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$31();
                }
            });
            searchResultArr[32] = new SearchResult(this, 116, LocaleController.getString("SyncContacts", R.string.SyncContacts), "contactsSyncRow", LocaleController.getString("PrivacySettings", i3), i4, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda74
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$32();
                }
            });
            searchResultArr[33] = new SearchResult(this, 117, LocaleController.getString("SuggestContacts", R.string.SuggestContacts), "contactsSuggestRow", LocaleController.getString("PrivacySettings", i3), i4, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda56
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$33();
                }
            });
            searchResultArr[34] = new SearchResult(this, 118, LocaleController.getString("MapPreviewProvider", R.string.MapPreviewProvider), "secretMapRow", LocaleController.getString("PrivacySettings", i3), i4, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda41
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$34();
                }
            });
            searchResultArr[35] = new SearchResult(this, 119, LocaleController.getString("SecretWebPage", R.string.SecretWebPage), "secretWebpageRow", LocaleController.getString("PrivacySettings", i3), i4, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$35();
                }
            });
            searchResultArr[36] = new SearchResult(this, 120, LocaleController.getString("Devices", R.string.Devices), i4, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$36();
                }
            });
            int i6 = R.string.DataSettings;
            String string3 = LocaleController.getString("DataSettings", i6);
            int i7 = R.drawable.msg_data;
            searchResultArr[37] = new SearchResult(this, 200, string3, i7, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda47
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$37();
                }
            });
            searchResultArr[38] = new SearchResult(this, 201, LocaleController.getString("DataUsage", R.string.DataUsage), "usageSectionRow", LocaleController.getString("DataSettings", i6), i7, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda27
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$38();
                }
            });
            int i8 = R.string.StorageUsage;
            searchResultArr[39] = new SearchResult(this, 202, LocaleController.getString("StorageUsage", i8), LocaleController.getString("DataSettings", i6), i7, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda65
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$39();
                }
            });
            searchResultArr[40] = new SearchResult(203, LocaleController.getString("KeepMedia", R.string.KeepMedia), "keepMediaRow", LocaleController.getString("DataSettings", i6), LocaleController.getString("StorageUsage", i8), i7, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda15
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$40();
                }
            });
            searchResultArr[41] = new SearchResult(204, LocaleController.getString("ClearMediaCache", R.string.ClearMediaCache), "cacheRow", LocaleController.getString("DataSettings", i6), LocaleController.getString("StorageUsage", i8), i7, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$41();
                }
            });
            searchResultArr[42] = new SearchResult(205, LocaleController.getString("LocalDatabase", R.string.LocalDatabase), "databaseRow", LocaleController.getString("DataSettings", i6), LocaleController.getString("StorageUsage", i8), i7, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda76
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$42();
                }
            });
            searchResultArr[43] = new SearchResult(this, 206, LocaleController.getString("NetworkUsage", R.string.NetworkUsage), LocaleController.getString("DataSettings", i6), i7, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda49
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$43();
                }
            });
            searchResultArr[44] = new SearchResult(this, 207, LocaleController.getString("AutomaticMediaDownload", R.string.AutomaticMediaDownload), "mediaDownloadSectionRow", LocaleController.getString("DataSettings", i6), i7, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda21
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$44();
                }
            });
            searchResultArr[45] = new SearchResult(this, 208, LocaleController.getString("WhenUsingMobileData", R.string.WhenUsingMobileData), LocaleController.getString("DataSettings", i6), i7, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda16
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$45();
                }
            });
            searchResultArr[46] = new SearchResult(this, 209, LocaleController.getString("WhenConnectedOnWiFi", R.string.WhenConnectedOnWiFi), LocaleController.getString("DataSettings", i6), i7, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda28
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$46();
                }
            });
            searchResultArr[47] = new SearchResult(this, 210, LocaleController.getString("WhenRoaming", R.string.WhenRoaming), LocaleController.getString("DataSettings", i6), i7, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda66
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$47();
                }
            });
            searchResultArr[48] = new SearchResult(this, 211, LocaleController.getString("ResetAutomaticMediaDownload", R.string.ResetAutomaticMediaDownload), "resetDownloadRow", LocaleController.getString("DataSettings", i6), i7, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$48();
                }
            });
            searchResultArr[49] = new SearchResult(this, 212, LocaleController.getString("AutoplayMedia", R.string.AutoplayMedia), "autoplayHeaderRow", LocaleController.getString("DataSettings", i6), i7, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda40
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$49();
                }
            });
            searchResultArr[50] = new SearchResult(this, 213, LocaleController.getString("AutoplayGIF", R.string.AutoplayGIF), "autoplayGifsRow", LocaleController.getString("DataSettings", i6), i7, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda60
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$50();
                }
            });
            searchResultArr[51] = new SearchResult(this, 214, LocaleController.getString("AutoplayVideo", R.string.AutoplayVideo), "autoplayVideoRow", LocaleController.getString("DataSettings", i6), i7, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda71
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$51();
                }
            });
            searchResultArr[52] = new SearchResult(this, 215, LocaleController.getString("Streaming", R.string.Streaming), "streamSectionRow", LocaleController.getString("DataSettings", i6), i7, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$52();
                }
            });
            searchResultArr[53] = new SearchResult(this, 216, LocaleController.getString("EnableStreaming", R.string.EnableStreaming), "enableStreamRow", LocaleController.getString("DataSettings", i6), i7, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda53
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$53();
                }
            });
            searchResultArr[54] = new SearchResult(this, 217, LocaleController.getString("Calls", i5), "callsSectionRow", LocaleController.getString("DataSettings", i6), i7, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda44
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$54();
                }
            });
            searchResultArr[55] = new SearchResult(this, 218, LocaleController.getString("VoipUseLessData", R.string.VoipUseLessData), "useLessDataForCallsRow", LocaleController.getString("DataSettings", i6), i7, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda45
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$55();
                }
            });
            searchResultArr[56] = new SearchResult(this, 219, LocaleController.getString("VoipQuickReplies", R.string.VoipQuickReplies), "quickRepliesRow", LocaleController.getString("DataSettings", i6), i7, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda33
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$56();
                }
            });
            int i9 = R.string.ProxySettings;
            searchResultArr[57] = new SearchResult(this, 220, LocaleController.getString("ProxySettings", i9), LocaleController.getString("DataSettings", i6), i7, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda36
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$57();
                }
            });
            searchResultArr[58] = new SearchResult(221, LocaleController.getString("UseProxyForCalls", R.string.UseProxyForCalls), "callsRow", LocaleController.getString("DataSettings", i6), LocaleController.getString("ProxySettings", i9), i7, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda81
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$58();
                }
            });
            searchResultArr[59] = new SearchResult(this, 111, LocaleController.getString("PrivacyDeleteCloudDrafts", R.string.PrivacyDeleteCloudDrafts), "clearDraftsRow", LocaleController.getString("DataSettings", i6), i7, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda20
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$59();
                }
            });
            int i10 = R.string.ChatSettings;
            String string4 = LocaleController.getString("ChatSettings", i10);
            int i11 = R.drawable.msg_msgbubble3;
            searchResultArr[60] = new SearchResult(this, 300, string4, i11, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda14
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$60();
                }
            });
            searchResultArr[61] = new SearchResult(this, 301, LocaleController.getString("TextSizeHeader", R.string.TextSizeHeader), "textSizeHeaderRow", LocaleController.getString("ChatSettings", i10), i11, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda82
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$61();
                }
            });
            int i12 = R.string.ChatBackground;
            searchResultArr[62] = new SearchResult(this, 302, LocaleController.getString("ChatBackground", i12), LocaleController.getString("ChatSettings", i10), i11, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda12
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$62();
                }
            });
            searchResultArr[63] = new SearchResult(303, LocaleController.getString("SetColor", R.string.SetColor), null, LocaleController.getString("ChatSettings", i10), LocaleController.getString("ChatBackground", i12), i11, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda54
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$63();
                }
            });
            searchResultArr[64] = new SearchResult(304, LocaleController.getString("ResetChatBackgrounds", R.string.ResetChatBackgrounds), "resetRow", LocaleController.getString("ChatSettings", i10), LocaleController.getString("ChatBackground", i12), i11, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda51
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$64();
                }
            });
            searchResultArr[65] = new SearchResult(this, 305, LocaleController.getString("AutoNightTheme", R.string.AutoNightTheme), LocaleController.getString("ChatSettings", i10), i11, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda57
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$65();
                }
            });
            searchResultArr[66] = new SearchResult(this, 306, LocaleController.getString("ColorTheme", R.string.ColorTheme), "themeHeaderRow", LocaleController.getString("ChatSettings", i10), i11, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda34
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$66();
                }
            });
            searchResultArr[67] = new SearchResult(this, 307, LocaleController.getString("ChromeCustomTabs", R.string.ChromeCustomTabs), "customTabsRow", LocaleController.getString("ChatSettings", i10), i11, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda50
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$67();
                }
            });
            searchResultArr[68] = new SearchResult(this, 308, LocaleController.getString("DirectShare", R.string.DirectShare), "directShareRow", LocaleController.getString("ChatSettings", i10), i11, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$68();
                }
            });
            searchResultArr[69] = new SearchResult(this, 309, LocaleController.getString("EnableAnimations", R.string.EnableAnimations), "enableAnimationsRow", LocaleController.getString("ChatSettings", i10), i11, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda39
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$69();
                }
            });
            searchResultArr[70] = new SearchResult(this, 310, LocaleController.getString("RaiseToSpeak", R.string.RaiseToSpeak), "raiseToSpeakRow", LocaleController.getString("ChatSettings", i10), i11, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda83
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$70();
                }
            });
            searchResultArr[71] = new SearchResult(this, 311, LocaleController.getString("SendByEnter", R.string.SendByEnter), "sendByEnterRow", LocaleController.getString("ChatSettings", i10), i11, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda80
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$71();
                }
            });
            searchResultArr[72] = new SearchResult(this, 312, LocaleController.getString("SaveToGallerySettings", R.string.SaveToGallerySettings), "saveToGalleryRow", LocaleController.getString("ChatSettings", i10), i11, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda25
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$72();
                }
            });
            searchResultArr[73] = new SearchResult(this, 318, LocaleController.getString("DistanceUnits", R.string.DistanceUnits), "distanceRow", LocaleController.getString("ChatSettings", i10), i11, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda59
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$73();
                }
            });
            int i13 = R.string.StickersAndMasks;
            searchResultArr[74] = new SearchResult(this, 313, LocaleController.getString("StickersAndMasks", i13), LocaleController.getString("ChatSettings", i10), i11, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda37
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$74();
                }
            });
            searchResultArr[75] = new SearchResult(314, LocaleController.getString("SuggestStickers", R.string.SuggestStickers), "suggestRow", LocaleController.getString("ChatSettings", i10), LocaleController.getString("StickersAndMasks", i13), i11, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda30
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$75();
                }
            });
            searchResultArr[76] = new SearchResult(315, LocaleController.getString("FeaturedStickers", R.string.FeaturedStickers), null, LocaleController.getString("ChatSettings", i10), LocaleController.getString("StickersAndMasks", i13), i11, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda69
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$76();
                }
            });
            searchResultArr[77] = new SearchResult(316, LocaleController.getString("Masks", R.string.Masks), null, LocaleController.getString("ChatSettings", i10), LocaleController.getString("StickersAndMasks", i13), i11, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda67
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$77();
                }
            });
            searchResultArr[78] = new SearchResult(317, LocaleController.getString("ArchivedStickers", R.string.ArchivedStickers), null, LocaleController.getString("ChatSettings", i10), LocaleController.getString("StickersAndMasks", i13), i11, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda73
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$78();
                }
            });
            searchResultArr[79] = new SearchResult(317, LocaleController.getString("ArchivedMasks", R.string.ArchivedMasks), null, LocaleController.getString("ChatSettings", i10), LocaleController.getString("StickersAndMasks", i13), i11, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda68
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$79();
                }
            });
            searchResultArr[80] = new SearchResult(this, 400, LocaleController.getString("Language", R.string.Language), R.drawable.msg_language, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda48
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$80();
                }
            });
            String string5 = LocaleController.getString("AskAQuestion", R.string.AskAQuestion);
            int i14 = R.string.SettingsHelp;
            String string6 = LocaleController.getString("SettingsHelp", i14);
            int i15 = R.drawable.msg_help;
            searchResultArr[81] = new SearchResult(this, 402, string5, string6, i15, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda43
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$81();
                }
            });
            searchResultArr[82] = new SearchResult(this, 403, LocaleController.getString("TelegramFAQ", R.string.TelegramFAQ), LocaleController.getString("SettingsHelp", i14), i15, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda31
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$82();
                }
            });
            searchResultArr[83] = new SearchResult(this, 404, LocaleController.getString("PrivacyPolicy", R.string.PrivacyPolicy), LocaleController.getString("SettingsHelp", i14), i15, new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda38
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$83();
                }
            });
            this.searchArray = searchResultArr;
            this.faqSearchArray = new ArrayList<>();
            this.resultNames = new ArrayList<>();
            this.searchResults = new ArrayList<>();
            this.faqSearchResults = new ArrayList<>();
            this.recentSearches = new ArrayList<>();
            this.mContext = context;
            HashMap hashMap = new HashMap();
            int i16 = 0;
            while (true) {
                SearchResult[] searchResultArr2 = this.searchArray;
                if (i16 >= searchResultArr2.length) {
                    break;
                }
                if (searchResultArr2[i16] != null) {
                    hashMap.put(Integer.valueOf(searchResultArr2[i16].guid), this.searchArray[i16]);
                }
                i16++;
            }
            Set<String> stringSet = MessagesController.getGlobalMainSettings().getStringSet("settingsSearchRecent2", null);
            if (stringSet != null) {
                for (String str : stringSet) {
                    try {
                        serializedData = new SerializedData(Utilities.hexToBytes(str));
                        z = false;
                        readInt32 = serializedData.readInt32(false);
                        readInt322 = serializedData.readInt32(false);
                    } catch (Exception unused) {
                    }
                    if (readInt322 == 0) {
                        String readString = serializedData.readString(false);
                        int readInt323 = serializedData.readInt32(false);
                        if (readInt323 > 0) {
                            strArr = new String[readInt323];
                            int i17 = 0;
                            while (i17 < readInt323) {
                                strArr[i17] = serializedData.readString(z);
                                i17++;
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
                            SearchResult searchResult = (SearchResult) hashMap.get(Integer.valueOf(serializedData.readInt32(false)));
                            if (searchResult != null) {
                                searchResult.num = readInt32;
                                this.recentSearches.add(searchResult);
                            }
                        } catch (Exception unused2) {
                        }
                    }
                }
            }
            Collections.sort(this.recentSearches, new Comparator() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda87
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    int lambda$new$84;
                    lambda$new$84 = ProfileActivity.SearchAdapter.this.lambda$new$84(obj, obj2);
                    return lambda$new$84;
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ int lambda$new$84(Object obj, Object obj2) {
            int num = getNum(obj);
            int num2 = getNum(obj2);
            if (num < num2) {
                return -1;
            }
            return num > num2 ? 1 : 0;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void loadFaqWebPage() {
            TLRPC$WebPage tLRPC$WebPage = ProfileActivity.this.getMessagesController().faqWebPage;
            this.faqWebPage = tLRPC$WebPage;
            if (tLRPC$WebPage != null) {
                this.faqSearchArray.addAll(ProfileActivity.this.getMessagesController().faqSearchArray);
            }
            if (this.faqWebPage != null || this.loadingFaqPage) {
                return;
            }
            this.loadingFaqPage = true;
            TLRPC$TL_messages_getWebPage tLRPC$TL_messages_getWebPage = new TLRPC$TL_messages_getWebPage();
            tLRPC$TL_messages_getWebPage.url = LocaleController.getString("TelegramFaqUrl", R.string.TelegramFaqUrl);
            tLRPC$TL_messages_getWebPage.hash = 0;
            ProfileActivity.this.getConnectionsManager().sendRequest(tLRPC$TL_messages_getWebPage, new RequestDelegate() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda88
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    ProfileActivity.SearchAdapter.this.lambda$loadFaqWebPage$86(tLObject, tLRPC$TL_error);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$loadFaqWebPage$86(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            if (tLObject instanceof TLRPC$WebPage) {
                final ArrayList arrayList = new ArrayList();
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
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda86
                    @Override // java.lang.Runnable
                    public final void run() {
                        ProfileActivity.SearchAdapter.this.lambda$loadFaqWebPage$85(arrayList);
                    }
                });
            }
            this.loadingFaqPage = false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$loadFaqWebPage$85(ArrayList arrayList) {
            this.faqSearchArray.addAll(arrayList);
            ProfileActivity.this.getMessagesController().faqSearchArray = arrayList;
            ProfileActivity.this.getMessagesController().faqWebPage = this.faqWebPage;
            if (!this.searchWas) {
                notifyDataSetChanged();
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
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

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            boolean z = true;
            if (itemViewType != 0) {
                if (itemViewType == 1) {
                    ((GraySectionCell) viewHolder.itemView).setText(LocaleController.getString("SettingsFaqSearchTitle", R.string.SettingsFaqSearchTitle));
                    return;
                } else if (itemViewType != 2) {
                    return;
                } else {
                    ((HeaderCell) viewHolder.itemView).setText(LocaleController.getString("SettingsRecent", R.string.SettingsRecent));
                    return;
                }
            }
            SettingsSearchCell settingsSearchCell = (SettingsSearchCell) viewHolder.itemView;
            boolean z2 = false;
            if (this.searchWas) {
                if (i < this.searchResults.size()) {
                    SearchResult searchResult = this.searchResults.get(i);
                    SearchResult searchResult2 = i > 0 ? this.searchResults.get(i - 1) : null;
                    int i2 = (searchResult2 == null || searchResult2.iconResId != searchResult.iconResId) ? searchResult.iconResId : 0;
                    CharSequence charSequence = this.resultNames.get(i);
                    String[] strArr = searchResult.path;
                    if (i >= this.searchResults.size() - 1) {
                        z = false;
                    }
                    settingsSearchCell.setTextAndValueAndIcon(charSequence, strArr, i2, z);
                    return;
                }
                int size = i - (this.searchResults.size() + 1);
                CharSequence charSequence2 = this.resultNames.get(this.searchResults.size() + size);
                String[] strArr2 = this.faqSearchResults.get(size).path;
                if (size < this.searchResults.size() - 1) {
                    z2 = true;
                }
                settingsSearchCell.setTextAndValue(charSequence2, strArr2, true, z2);
                return;
            }
            if (!this.recentSearches.isEmpty()) {
                i--;
            }
            if (i < this.recentSearches.size()) {
                Object obj = this.recentSearches.get(i);
                if (obj instanceof SearchResult) {
                    SearchResult searchResult3 = (SearchResult) obj;
                    String str = searchResult3.searchTitle;
                    String[] strArr3 = searchResult3.path;
                    if (i >= this.recentSearches.size() - 1) {
                        z = false;
                    }
                    settingsSearchCell.setTextAndValue(str, strArr3, false, z);
                    return;
                } else if (!(obj instanceof MessagesController.FaqSearchResult)) {
                    return;
                } else {
                    MessagesController.FaqSearchResult faqSearchResult = (MessagesController.FaqSearchResult) obj;
                    String str2 = faqSearchResult.title;
                    String[] strArr4 = faqSearchResult.path;
                    if (i < this.recentSearches.size() - 1) {
                        z2 = true;
                    }
                    settingsSearchCell.setTextAndValue(str2, strArr4, true, z2);
                    return;
                }
            }
            int size2 = i - (this.recentSearches.size() + 1);
            MessagesController.FaqSearchResult faqSearchResult2 = this.faqSearchArray.get(size2);
            String str3 = faqSearchResult2.title;
            String[] strArr5 = faqSearchResult2.path;
            if (size2 < this.recentSearches.size() - 1) {
                z2 = true;
            }
            settingsSearchCell.setTextAndValue(str3, strArr5, true, z2);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /* renamed from: onCreateViewHolder */
        public RecyclerView.ViewHolder mo1788onCreateViewHolder(ViewGroup viewGroup, int i) {
            View settingsSearchCell;
            if (i == 0) {
                settingsSearchCell = new SettingsSearchCell(this.mContext);
            } else if (i == 1) {
                settingsSearchCell = new GraySectionCell(this.mContext);
            } else {
                settingsSearchCell = new HeaderCell(this.mContext, 16);
            }
            settingsSearchCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(settingsSearchCell);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (this.searchWas) {
                if (i >= this.searchResults.size() && i == this.searchResults.size()) {
                    return 1;
                }
            } else if (i == 0) {
                return !this.recentSearches.isEmpty() ? 2 : 1;
            } else if (!this.recentSearches.isEmpty() && i == this.recentSearches.size() + 1) {
                return 1;
            }
            return 0;
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
                    ((SearchResult) obj2).num = i;
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
            if (!(obj instanceof MessagesController.FaqSearchResult)) {
                return 0;
            }
            return ((MessagesController.FaqSearchResult) obj).num;
        }

        public void search(final String str) {
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
                ProfileActivity.this.emptyView.stickerView.getImageReceiver().startAnimation();
                ProfileActivity.this.emptyView.title.setText(LocaleController.getString("SettingsNoRecent", R.string.SettingsNoRecent));
                notifyDataSetChanged();
                return;
            }
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            Runnable runnable = new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda84
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$search$88(str);
                }
            };
            this.searchRunnable = runnable;
            dispatchQueue.postRunnable(runnable, 300L);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$search$88(final String str) {
            SpannableStringBuilder spannableStringBuilder;
            String str2;
            int i;
            String str3;
            SpannableStringBuilder spannableStringBuilder2;
            final ArrayList arrayList = new ArrayList();
            final ArrayList arrayList2 = new ArrayList();
            final ArrayList arrayList3 = new ArrayList();
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
                            if (indexOf >= 0) {
                                spannableStringBuilder2 = spannableStringBuilder3 == null ? new SpannableStringBuilder(searchResult.searchTitle) : spannableStringBuilder3;
                                str3 = str5;
                                spannableStringBuilder2.setSpan(new ForegroundColorSpan(ProfileActivity.this.getThemedColor("windowBackgroundWhiteBlueText4")), indexOf, str6.length() + indexOf, 33);
                            }
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
                        spannableStringBuilder3 = spannableStringBuilder2;
                        str5 = str3;
                    }
                }
                i3++;
                spannableStringBuilder = null;
            }
            if (this.faqWebPage != null) {
                int size = this.faqSearchArray.size();
                int i7 = 0;
                while (i7 < size) {
                    MessagesController.FaqSearchResult faqSearchResult = this.faqSearchArray.get(i7);
                    String str7 = str4 + faqSearchResult.title.toLowerCase();
                    int i8 = 0;
                    SpannableStringBuilder spannableStringBuilder4 = null;
                    while (i8 < split.length) {
                        if (split[i8].length() != 0) {
                            String str8 = split[i8];
                            int indexOf2 = str7.indexOf(str4 + str8);
                            if (indexOf2 < 0 && strArr[i8] != null) {
                                str8 = strArr[i8];
                                indexOf2 = str7.indexOf(str4 + str8);
                            }
                            if (indexOf2 >= 0) {
                                if (spannableStringBuilder4 == null) {
                                    str2 = str4;
                                    spannableStringBuilder4 = new SpannableStringBuilder(faqSearchResult.title);
                                } else {
                                    str2 = str4;
                                }
                                i = size;
                                spannableStringBuilder4.setSpan(new ForegroundColorSpan(ProfileActivity.this.getThemedColor("windowBackgroundWhiteBlueText4")), indexOf2, str8.length() + indexOf2, 33);
                            }
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
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ProfileActivity$SearchAdapter$$ExternalSyntheticLambda85
                @Override // java.lang.Runnable
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$search$87(str, arrayList, arrayList2, arrayList3);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$search$87(String str, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3) {
            if (!str.equals(this.lastSearchString)) {
                return;
            }
            if (!this.searchWas) {
                ProfileActivity.this.emptyView.stickerView.getImageReceiver().startAnimation();
                ProfileActivity.this.emptyView.title.setText(LocaleController.getString("SettingsNoResults", R.string.SettingsNoResults));
            }
            this.searchWas = true;
            this.searchResults = arrayList;
            this.faqSearchResults = arrayList2;
            this.resultNames = arrayList3;
            notifyDataSetChanged();
            ProfileActivity.this.emptyView.stickerView.getImageReceiver().startAnimation();
        }

        public boolean isSearchWas() {
            return this.searchWas;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openUrl(String str) {
        if (str.startsWith("@")) {
            getMessagesController().openByUserName(str.substring(1), this, 0);
        } else if (str.startsWith("#")) {
            DialogsActivity dialogsActivity = new DialogsActivity(null);
            dialogsActivity.setSearchString(str);
            presentFragment(dialogsActivity);
        } else if (!str.startsWith("/") || this.parentLayout.getFragmentStack().size() <= 1) {
        } else {
            BaseFragment baseFragment = this.parentLayout.getFragmentStack().get(this.parentLayout.getFragmentStack().size() - 2);
            if (!(baseFragment instanceof ChatActivity)) {
                return;
            }
            finishFragment();
            ((ChatActivity) baseFragment).chatActivityEnterView.setCommand(null, str, false, false);
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
        ValueAnimator ofFloat;
        boolean z = f > 0.0f;
        this.fragmentView.invalidate();
        AnimatorSet animatorSet = this.scrimAnimatorSet;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.scrimAnimatorSet = new AnimatorSet();
        ArrayList arrayList = new ArrayList();
        if (z) {
            ofFloat = ValueAnimator.ofFloat(0.0f, f);
            arrayList.add(ofFloat);
        } else {
            ofFloat = ValueAnimator.ofFloat(this.scrimPaint.getAlpha() / 255.0f, 0.0f);
            arrayList.add(ofFloat);
        }
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                ProfileActivity.this.lambda$dimBehindView$45(valueAnimator);
            }
        });
        this.scrimAnimatorSet.playTogether(arrayList);
        this.scrimAnimatorSet.setDuration(z ? 150L : 220L);
        if (!z) {
            this.scrimAnimatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ProfileActivity.45
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    ProfileActivity.this.scrimView = null;
                    ((BaseFragment) ProfileActivity.this).fragmentView.invalidate();
                }
            });
        }
        this.scrimAnimatorSet.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$dimBehindView$45(ValueAnimator valueAnimator) {
        this.scrimPaint.setAlpha((int) (((Float) valueAnimator.getAnimatedValue()).floatValue() * 255.0f));
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        if (this.resourcesProvider != null) {
            return null;
        }
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda43
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                ProfileActivity.this.lambda$getThemeDescriptions$46();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        SharedMediaLayout sharedMediaLayout = this.sharedMediaLayout;
        if (sharedMediaLayout != null) {
            arrayList.addAll(sharedMediaLayout.getThemeDescriptions());
        }
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.searchListView, 0, null, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.listView, 0, null, null, null, null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, "actionBarDefaultSubmenuBackground"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, "actionBarDefaultSubmenuItem"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "actionBarDefaultSubmenuItemIcon"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_actionBarSelectorBlue"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "chat_lockIcon"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_subtitleInProfileBlue"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundActionBarBlue"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "profile_title"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "profile_status"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_subtitleInProfileBlue"));
        if (this.mediaCounterTextView != null) {
            arrayList.add(new ThemeDescription(this.mediaCounterTextView.getTextView(), ThemeDescription.FLAG_TEXTCOLOR, null, null, null, themeDescriptionDelegate, "player_actionBarSubtitle"));
            arrayList.add(new ThemeDescription(this.mediaCounterTextView.getNextTextView(), ThemeDescription.FLAG_TEXTCOLOR, null, null, null, themeDescriptionDelegate, "player_actionBarSubtitle"));
        }
        arrayList.add(new ThemeDescription(this.topView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "avatar_backgroundActionBarBlue"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider"));
        arrayList.add(new ThemeDescription(this.avatarImage, 0, null, null, Theme.avatarDrawables, null, "avatar_text"));
        arrayList.add(new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[]{this.avatarDrawable}, null, "avatar_backgroundInProfileBlue"));
        arrayList.add(new ThemeDescription(this.writeButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "profile_actionIcon"));
        arrayList.add(new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "profile_actionBackground"));
        arrayList.add(new ThemeDescription(this.writeButton, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "profile_actionPressedBackground"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGreenText2"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText5"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText2"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueButton"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueIcon"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{SettingsSuggestionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{SettingsSuggestionCell.class}, new String[]{"detailTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{SettingsSuggestionCell.class}, new String[]{"detailTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteLinkText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{SettingsSuggestionCell.class}, new String[]{"yesButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_buttonText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{SettingsSuggestionCell.class}, new String[]{"yesButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addButton"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{SettingsSuggestionCell.class}, new String[]{"yesButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addButtonPressed"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{SettingsSuggestionCell.class}, new String[]{"noButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_buttonText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{SettingsSuggestionCell.class}, new String[]{"noButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addButton"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{SettingsSuggestionCell.class}, new String[]{"noButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addButtonPressed"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{UserCell.class}, new String[]{"adminTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_creatorIcon"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, "windowBackgroundWhiteBlueText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, null, Theme.avatarDrawables, null, "avatar_text"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundPink"));
        arrayList.add(new ThemeDescription(this.undoView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "undo_background"));
        arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
        arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
        arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        arrayList.add(new ThemeDescription(this.undoView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{UndoView.class}, new String[]{"leftImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AboutLinkCell.class}, Theme.profile_aboutTextPaint, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{AboutLinkCell.class}, Theme.profile_aboutTextPaint, null, null, "windowBackgroundWhiteLinkText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{AboutLinkCell.class}, Theme.linkSelectionPaint, null, null, "windowBackgroundWhiteLinkSelection"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription(this.searchListView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription(this.searchListView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_graySectionText"));
        arrayList.add(new ThemeDescription(this.searchListView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, "graySection"));
        arrayList.add(new ThemeDescription(this.searchListView, 0, new Class[]{SettingsSearchCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.searchListView, 0, new Class[]{SettingsSearchCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.searchListView, 0, new Class[]{SettingsSearchCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        if (this.mediaHeaderVisible) {
            arrayList.add(new ThemeDescription(this.nameTextView[1], 0, null, null, new Drawable[]{this.verifiedCheckDrawable}, null, "player_actionBarTitle"));
            arrayList.add(new ThemeDescription(this.nameTextView[1], 0, null, null, new Drawable[]{this.verifiedDrawable}, null, "windowBackgroundWhite"));
        } else {
            arrayList.add(new ThemeDescription(this.nameTextView[1], 0, null, null, new Drawable[]{this.verifiedCheckDrawable}, null, "profile_verifiedCheck"));
            arrayList.add(new ThemeDescription(this.nameTextView[1], 0, null, null, new Drawable[]{this.verifiedDrawable}, null, "profile_verifiedBackground"));
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getThemeDescriptions$46() {
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
            ScamDrawable scamDrawable = this.scamDrawable;
            if (scamDrawable != null) {
                scamDrawable.setColor(getThemedColor("avatar_subtitleInProfileBlue"));
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
        updateEmojiStatusDrawableColor();
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
            DiffUtil.calculateDiff(diffCallback).dispatchUpdatesTo(this.listAdapter);
        } catch (Exception unused) {
            this.listAdapter.notifyDataSetChanged();
        }
        int i = this.savedScrollPosition;
        if (i >= 0) {
            this.layoutManager.scrollToPositionWithOffset(i, this.savedScrollOffset - this.listView.getPaddingTop());
        }
        AndroidUtilities.updateVisibleRows(this.listView);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveScrollPosition() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView == null || this.layoutManager == null || recyclerListView.getChildCount() <= 0) {
            return;
        }
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
        if (view == null) {
            return;
        }
        this.savedScrollPosition = i2;
        int top = view.getTop();
        this.savedScrollOffset = top;
        if (this.savedScrollPosition == 0 && !this.allowPullingDown && top > AndroidUtilities.dp(88.0f)) {
            this.savedScrollOffset = AndroidUtilities.dp(88.0f);
        }
        this.layoutManager.scrollToPositionWithOffset(i2, view.getTop() - this.listView.getPaddingTop());
    }

    @Override // org.telegram.ui.Components.SharedMediaLayout.Delegate
    public void scrollToSharedMedia() {
        this.layoutManager.scrollToPositionWithOffset(this.sharedMediaRow, -this.listView.getPaddingTop());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onTextDetailCellImageClicked(View view) {
        View view2 = (View) view.getParent();
        if (view2.getTag() == null || ((Integer) view2.getTag()).intValue() != this.usernameRow) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putLong("chat_id", this.chatId);
        bundle.putLong("user_id", this.userId);
        presentFragment(new QrActivity(bundle));
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
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
        return c == 0 || c == 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class DiffCallback extends DiffUtil.Callback {
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

        @Override // androidx.recyclerview.widget.DiffUtil.Callback
        public int getOldListSize() {
            return this.oldRowCount;
        }

        @Override // androidx.recyclerview.widget.DiffUtil.Callback
        public int getNewListSize() {
            return ProfileActivity.this.rowCount;
        }

        @Override // androidx.recyclerview.widget.DiffUtil.Callback
        public boolean areItemsTheSame(int i, int i2) {
            TLRPC$ChatParticipant tLRPC$ChatParticipant;
            if (i2 >= ProfileActivity.this.membersStartRow && i2 < ProfileActivity.this.membersEndRow && i >= this.oldMembersStartRow && i < this.oldMembersEndRow) {
                if (!this.oldChatParticipantSorted.isEmpty()) {
                    tLRPC$ChatParticipant = this.oldChatParticipant.get(this.oldChatParticipantSorted.get(i - this.oldMembersStartRow).intValue());
                } else {
                    tLRPC$ChatParticipant = this.oldChatParticipant.get(i - this.oldMembersStartRow);
                }
                return tLRPC$ChatParticipant.user_id == (!ProfileActivity.this.sortedUsers.isEmpty() ? (TLRPC$ChatParticipant) ProfileActivity.this.visibleChatParticipants.get(((Integer) ProfileActivity.this.visibleSortedUsers.get(i2 - ProfileActivity.this.membersStartRow)).intValue()) : (TLRPC$ChatParticipant) ProfileActivity.this.visibleChatParticipants.get(i2 - ProfileActivity.this.membersStartRow)).user_id;
            }
            int i3 = this.oldPositionToItem.get(i, -1);
            return i3 == this.newPositionToItem.get(i2, -1) && i3 >= 0;
        }

        @Override // androidx.recyclerview.widget.DiffUtil.Callback
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
            put(44, ProfileActivity.this.reportDividerRow, sparseIntArray);
            put(45, ProfileActivity.this.notificationsRow, sparseIntArray);
            put(46, ProfileActivity.this.infoSectionRow, sparseIntArray);
            put(47, ProfileActivity.this.sendMessageRow, sparseIntArray);
            put(48, ProfileActivity.this.reportRow, sparseIntArray);
            put(49, ProfileActivity.this.reportReactionRow, sparseIntArray);
            put(50, ProfileActivity.this.settingsTimerRow, sparseIntArray);
            put(51, ProfileActivity.this.settingsKeyRow, sparseIntArray);
            put(52, ProfileActivity.this.secretSettingsSectionRow, sparseIntArray);
            put(53, ProfileActivity.this.membersHeaderRow, sparseIntArray);
            put(54, ProfileActivity.this.addMemberRow, sparseIntArray);
            put(55, ProfileActivity.this.subscribersRow, sparseIntArray);
            put(56, ProfileActivity.this.subscribersRequestsRow, sparseIntArray);
            put(57, ProfileActivity.this.administratorsRow, sparseIntArray);
            put(58, ProfileActivity.this.blockedUsersRow, sparseIntArray);
            put(59, ProfileActivity.this.membersSectionRow, sparseIntArray);
            put(60, ProfileActivity.this.sharedMediaRow, sparseIntArray);
            put(61, ProfileActivity.this.unblockRow, sparseIntArray);
            put(62, ProfileActivity.this.addToGroupButtonRow, sparseIntArray);
            put(63, ProfileActivity.this.addToGroupInfoRow, sparseIntArray);
            put(64, ProfileActivity.this.joinRow, sparseIntArray);
            put(65, ProfileActivity.this.lastSectionRow, sparseIntArray);
            put(66, ProfileActivity.this.notificationsSimpleRow, sparseIntArray);
        }

        private void put(int i, int i2, SparseIntArray sparseIntArray) {
            if (i2 >= 0) {
                sparseIntArray.put(i2, i);
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean isLightStatusBar() {
        int themedColor;
        if (this.isPulledDown) {
            return false;
        }
        if (this.actionBar.isActionModeShowed()) {
            themedColor = getThemedColor("actionBarActionModeDefault");
        } else if (this.mediaHeaderVisible) {
            themedColor = getThemedColor("windowBackgroundWhite");
        } else {
            themedColor = getThemedColor("actionBarDefault");
        }
        return ColorUtils.calculateLuminance(themedColor) > 0.699999988079071d;
    }

    public String getLink(String str, int i) {
        String str2 = getMessagesController().linkPrefix + "/" + str;
        if (i != 0) {
            return str2 + "/" + i;
        }
        return str2;
    }
}
