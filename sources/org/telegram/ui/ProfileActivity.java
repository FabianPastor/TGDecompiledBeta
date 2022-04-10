package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.MovementMethod;
import android.text.style.ForegroundColorSpan;
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
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
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
import org.telegram.tgnet.TLRPC$ReplyMarkup;
import org.telegram.tgnet.TLRPC$TL_account_password;
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
import org.telegram.ui.Cells.DividerCell;
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
import org.telegram.ui.Components.ProfileGalleryView;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScamDrawable;
import org.telegram.ui.Components.SharedMediaLayout;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StickerEmptyView;
import org.telegram.ui.Components.TimerDrawable;
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
    public String currentBio;
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
    private ImageView ttlIconView;
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

    static /* synthetic */ int access$7012(ProfileActivity profileActivity, int i) {
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
                this.paint.setColor(Theme.getColor("windowBackgroundWhite"));
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
            ProfileActivity.this.updateProfileData();
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
                int color = Theme.getColor("profile_title");
                int color2 = Theme.getColor("player_actionBarTitle");
                int offsetColor = AndroidUtilities.getOffsetColor(color, color2, f, 1.0f);
                ProfileActivity.this.nameTextView[1].setTextColor(offsetColor);
                if (ProfileActivity.this.lockIconDrawable != null) {
                    ProfileActivity.this.lockIconDrawable.setColorFilter(offsetColor, PorterDuff.Mode.MULTIPLY);
                }
                if (ProfileActivity.this.scamDrawable != null) {
                    ProfileActivity.this.scamDrawable.setColor(AndroidUtilities.getOffsetColor(Theme.getColor("avatar_subtitleInProfileBlue"), color2, f, 1.0f));
                }
                ProfileActivity.this.actionBar.setItemsColor(AndroidUtilities.getOffsetColor(Theme.getColor("actionBarDefaultIcon"), Theme.getColor("windowBackgroundWhiteGrayText2"), f, 1.0f), false);
                ProfileActivity.this.actionBar.setItemsBackgroundColor(AndroidUtilities.getOffsetColor(Theme.getColor("avatar_actionBarSelectorBlue"), Theme.getColor("actionBarActionModeDefaultSelector"), f, 1.0f), false);
                ProfileActivity.this.topView.invalidate();
                ProfileActivity.this.otherItem.setIconColor(Theme.getColor("actionBarDefaultIcon"));
                ProfileActivity.this.callItem.setIconColor(Theme.getColor("actionBarDefaultIcon"));
                ProfileActivity.this.videoCallItem.setIconColor(Theme.getColor("actionBarDefaultIcon"));
                ProfileActivity.this.editItem.setIconColor(Theme.getColor("actionBarDefaultIcon"));
                if (ProfileActivity.this.verifiedDrawable != null) {
                    ProfileActivity.this.verifiedDrawable.setColorFilter(AndroidUtilities.getOffsetColor(Theme.getColor("profile_verifiedBackground"), Theme.getColor("player_actionBarTitle"), f, 1.0f), PorterDuff.Mode.MULTIPLY);
                }
                if (ProfileActivity.this.verifiedCheckDrawable != null) {
                    ProfileActivity.this.verifiedCheckDrawable.setColorFilter(AndroidUtilities.getOffsetColor(Theme.getColor("profile_verifiedCheck"), Theme.getColor("windowBackgroundWhite"), f, 1.0f), PorterDuff.Mode.MULTIPLY);
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
        AnonymousClass4 r0 = new ActionBar(context) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                ProfileActivity.this.avatarContainer.getHitRect(ProfileActivity.this.rect);
                if (ProfileActivity.this.rect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                    return false;
                }
                return super.onTouchEvent(motionEvent);
            }
        };
        boolean z = false;
        r0.setBackgroundColor(0);
        r0.setItemsBackgroundColor(AvatarDrawable.getButtonColorForId((this.userId != 0 || (ChatObject.isChannel(this.chatId, this.currentAccount) && !this.currentChat.megagroup)) ? 5 : this.chatId), false);
        r0.setItemsColor(Theme.getColor("actionBarDefaultIcon"), false);
        r0.setBackButtonDrawable(new BackDrawable(false));
        r0.setCastShadows(false);
        r0.setAddToContainer(false);
        r0.setClipContent(true);
        if (Build.VERSION.SDK_INT >= 21 && !AndroidUtilities.isTablet() && !this.inBubbleMode) {
            z = true;
        }
        r0.setOccupyStatusBar(z);
        ImageView backButton = r0.getBackButton();
        backButton.setOnLongClickListener(new ProfileActivity$$ExternalSyntheticLambda16(this, backButton));
        return r0;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createActionBar$2(ImageView imageView, View view) {
        ActionBarPopupWindow show = BackButtonMenu.show(this, imageView, getDialogId());
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

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0051, code lost:
        r0 = r0.participants;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View createView(android.content.Context r38) {
        /*
            r37 = this;
            r13 = r37
            r14 = r38
            org.telegram.ui.ActionBar.Theme.createProfileResources(r38)
            r15 = 0
            org.telegram.ui.ActionBar.Theme.createChatResources(r14, r15)
            r13.searchTransitionOffset = r15
            r12 = 1065353216(0x3var_, float:1.0)
            r13.searchTransitionProgress = r12
            r13.searchMode = r15
            r11 = 1
            r13.hasOwnBackground = r11
            r16 = 1118830592(0x42b00000, float:88.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r0 = (float) r0
            r13.extraHeight = r0
            org.telegram.ui.ActionBar.ActionBar r0 = r13.actionBar
            org.telegram.ui.ProfileActivity$5 r1 = new org.telegram.ui.ProfileActivity$5
            r1.<init>()
            r0.setActionBarMenuOnItemClick(r1)
            org.telegram.ui.Components.SharedMediaLayout r0 = r13.sharedMediaLayout
            if (r0 == 0) goto L_0x0030
            r0.onDestroy()
        L_0x0030:
            long r0 = r13.dialogId
            r17 = 0
            int r2 = (r0 > r17 ? 1 : (r0 == r17 ? 0 : -1))
            if (r2 == 0) goto L_0x003a
        L_0x0038:
            r9 = r0
            goto L_0x0045
        L_0x003a:
            long r0 = r13.userId
            int r2 = (r0 > r17 ? 1 : (r0 == r17 ? 0 : -1))
            if (r2 == 0) goto L_0x0041
            goto L_0x0038
        L_0x0041:
            long r0 = r13.chatId
            long r0 = -r0
            goto L_0x0038
        L_0x0045:
            org.telegram.ui.ProfileActivity$6 r0 = new org.telegram.ui.ProfileActivity$6
            r0.<init>(r14)
            r13.fragmentView = r0
            org.telegram.tgnet.TLRPC$ChatFull r0 = r13.chatInfo
            r8 = 0
            if (r0 == 0) goto L_0x0061
            org.telegram.tgnet.TLRPC$ChatParticipants r0 = r0.participants
            if (r0 == 0) goto L_0x0061
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r0 = r0.participants
            int r0 = r0.size()
            r1 = 5
            if (r0 <= r1) goto L_0x0061
            java.util.ArrayList<java.lang.Integer> r0 = r13.sortedUsers
            goto L_0x0062
        L_0x0061:
            r0 = r8
        L_0x0062:
            org.telegram.ui.ProfileActivity$7 r7 = new org.telegram.ui.ProfileActivity$7
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloader r5 = r13.sharedMediaPreloader
            org.telegram.tgnet.TLRPC$UserFull r1 = r13.userInfo
            if (r1 == 0) goto L_0x006e
            int r1 = r1.common_chats_count
            r6 = r1
            goto L_0x006f
        L_0x006e:
            r6 = 0
        L_0x006f:
            java.util.ArrayList<java.lang.Integer> r3 = r13.sortedUsers
            org.telegram.tgnet.TLRPC$ChatFull r4 = r13.chatInfo
            if (r0 == 0) goto L_0x0078
            r19 = 1
            goto L_0x007a
        L_0x0078:
            r19 = 0
        L_0x007a:
            r20 = 1
            r0 = r7
            r1 = r37
            r2 = r38
            r21 = r3
            r22 = r4
            r3 = r9
            r15 = r7
            r7 = r21
            r8 = r22
            r24 = r9
            r9 = r19
            r10 = r37
            r14 = 1
            r11 = r37
            r12 = r20
            r0.<init>(r2, r3, r5, r6, r7, r8, r9, r10, r11, r12)
            r13.sharedMediaLayout = r15
            androidx.recyclerview.widget.RecyclerView$LayoutParams r0 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
            r9 = -1
            r0.<init>((int) r9, (int) r9)
            r15.setLayoutParams(r0)
            org.telegram.ui.ActionBar.ActionBar r0 = r13.actionBar
            org.telegram.ui.ActionBar.ActionBarMenu r0 = r0.createMenu()
            long r1 = r13.userId
            org.telegram.messenger.UserConfig r3 = r37.getUserConfig()
            long r3 = r3.clientUserId
            r5 = 8
            int r6 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r6 != 0) goto L_0x00e2
            r1 = 37
            r2 = 2131165863(0x7var_a7, float:1.7945955E38)
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r3 = r37.getResourceProvider()
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.addItem(r1, r2, r3)
            r13.qrItem = r1
            boolean r2 = r37.isQrNeedVisible()
            if (r2 == 0) goto L_0x00cf
            r2 = 0
            goto L_0x00d1
        L_0x00cf:
            r2 = 8
        L_0x00d1:
            r1.setVisibility(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r13.qrItem
            r2 = 2131624473(0x7f0e0219, float:1.8876127E38)
            java.lang.String r3 = "AuthAnotherClientScan"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setContentDescription(r2)
        L_0x00e2:
            org.telegram.ui.Components.ImageUpdater r1 = r13.imageUpdater
            if (r1 == 0) goto L_0x0125
            r1 = 32
            r2 = 2131165513(0x7var_, float:1.7945245E38)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.addItem((int) r1, (int) r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r1.setIsSearchField(r14)
            org.telegram.ui.ProfileActivity$8 r2 = new org.telegram.ui.ProfileActivity$8
            r2.<init>()
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r1.setActionBarMenuItemSearchListener(r2)
            r13.searchItem = r1
            java.lang.String r2 = "SearchInSettings"
            r3 = 2131627895(0x7f0e0var_, float:1.8883067E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r2, r3)
            r1.setContentDescription(r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r13.searchItem
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)
            r1.setSearchFieldHint(r2)
            org.telegram.ui.Components.SharedMediaLayout r1 = r13.sharedMediaLayout
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r1.getSearchItem()
            r1.setVisibility(r5)
            boolean r1 = r13.expandPhoto
            if (r1 == 0) goto L_0x0125
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r13.searchItem
            r1.setVisibility(r5)
        L_0x0125:
            r1 = 16
            r2 = 2131166078(0x7var_e, float:1.7946391E38)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.addItem((int) r1, (int) r2)
            r13.videoCallItem = r1
            r2 = 2131628638(0x7f0e125e, float:1.8884574E38)
            java.lang.String r3 = "VideoCall"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setContentDescription(r2)
            long r1 = r13.chatId
            r3 = 15
            int r4 = (r1 > r17 ? 1 : (r1 == r17 ? 0 : -1))
            if (r4 == 0) goto L_0x0173
            r1 = 2131165942(0x7var_f6, float:1.7946115E38)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.addItem((int) r3, (int) r1)
            r13.callItem = r1
            org.telegram.tgnet.TLRPC$Chat r1 = r13.currentChat
            boolean r1 = org.telegram.messenger.ChatObject.isChannelOrGiga(r1)
            if (r1 == 0) goto L_0x0164
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r13.callItem
            r2 = 2131628756(0x7f0e12d4, float:1.8884814E38)
            java.lang.String r3 = "VoipChannelVoiceChat"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setContentDescription(r2)
            goto L_0x0188
        L_0x0164:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r13.callItem
            r2 = 2131628886(0x7f0e1356, float:1.8885077E38)
            java.lang.String r3 = "VoipGroupVoiceChat"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setContentDescription(r2)
            goto L_0x0188
        L_0x0173:
            r1 = 2131165523(0x7var_, float:1.7945266E38)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.addItem((int) r3, (int) r1)
            r13.callItem = r1
            r2 = 2131624710(0x7f0e0306, float:1.8876607E38)
            java.lang.String r3 = "Call"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setContentDescription(r2)
        L_0x0188:
            r1 = 12
            r2 = 2131165493(0x7var_, float:1.7945205E38)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.addItem((int) r1, (int) r2)
            r13.editItem = r1
            r2 = 2131625432(0x7f0e05d8, float:1.8878072E38)
            java.lang.String r3 = "Edit"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setContentDescription(r2)
            r1 = 10
            r2 = 2131165510(0x7var_, float:1.794524E38)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r0.addItem((int) r1, (int) r2)
            r13.otherItem = r0
            android.widget.ImageView r0 = new android.widget.ImageView
            r10 = r38
            r11 = 1
            r0.<init>(r10)
            r13.ttlIconView = r0
            r1 = 1061997773(0x3f4ccccd, float:0.8)
            r2 = 0
            org.telegram.messenger.AndroidUtilities.updateViewVisibilityAnimated(r0, r2, r1, r2)
            android.widget.ImageView r0 = r13.ttlIconView
            r1 = 2131165831(0x7var_, float:1.794589E38)
            r0.setImageResource(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r13.otherItem
            android.widget.ImageView r1 = r13.ttlIconView
            r27 = 12
            r28 = 1094713344(0x41400000, float:12.0)
            r29 = 19
            r30 = 1090519040(0x41000000, float:8.0)
            r31 = 1073741824(0x40000000, float:2.0)
            r32 = 0
            r33 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r27, r28, r29, r30, r31, r32, r33)
            r0.addView(r1, r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r13.otherItem
            r1 = 2131623987(0x7f0e0033, float:1.887514E38)
            java.lang.String r2 = "AccDescrMoreOptions"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setContentDescription(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r13.listView
            if (r0 == 0) goto L_0x0214
            org.telegram.ui.Components.ImageUpdater r0 = r13.imageUpdater
            if (r0 == 0) goto L_0x0214
            androidx.recyclerview.widget.LinearLayoutManager r0 = r13.layoutManager
            int r0 = r0.findFirstVisibleItemPosition()
            androidx.recyclerview.widget.LinearLayoutManager r1 = r13.layoutManager
            android.view.View r1 = r1.findViewByPosition(r0)
            if (r1 == 0) goto L_0x0209
            r1.getTop()
            org.telegram.ui.Components.RecyclerListView r1 = r13.listView
            r1.getPaddingTop()
            goto L_0x020a
        L_0x0209:
            r0 = -1
        L_0x020a:
            org.telegram.ui.Components.RLottieImageView r1 = r13.writeButton
            java.lang.Object r8 = r1.getTag()
            r12 = r0
            r14 = r8
            r0 = 0
            goto L_0x0217
        L_0x0214:
            r0 = 0
            r12 = -1
            r14 = 0
        L_0x0217:
            r13.createActionBarMenu(r0)
            org.telegram.ui.ProfileActivity$ListAdapter r0 = new org.telegram.ui.ProfileActivity$ListAdapter
            r0.<init>(r10)
            r13.listAdapter = r0
            org.telegram.ui.ProfileActivity$SearchAdapter r0 = new org.telegram.ui.ProfileActivity$SearchAdapter
            r0.<init>(r13, r10)
            r13.searchAdapter = r0
            org.telegram.ui.Components.AvatarDrawable r0 = new org.telegram.ui.Components.AvatarDrawable
            r0.<init>()
            r13.avatarDrawable = r0
            r0.setProfile(r11)
            android.view.View r0 = r13.fragmentView
            r1 = 0
            r0.setWillNotDraw(r1)
            android.view.View r0 = r13.fragmentView
            r2 = r0
            org.telegram.ui.ProfileActivity$NestedFrameLayout r2 = (org.telegram.ui.ProfileActivity.NestedFrameLayout) r2
            r13.contentView = r2
            r2.needBlur = r11
            r15 = r0
            android.widget.FrameLayout r15 = (android.widget.FrameLayout) r15
            org.telegram.ui.ProfileActivity$9 r0 = new org.telegram.ui.ProfileActivity$9
            r0.<init>(r10)
            r13.listView = r0
            r0.setVerticalScrollBarEnabled(r1)
            org.telegram.ui.ProfileActivity$10 r0 = new org.telegram.ui.ProfileActivity$10
            r0.<init>()
            org.telegram.ui.Components.RecyclerListView r2 = r13.listView
            r2.setItemAnimator(r0)
            r0.setSupportsChangeAnimations(r1)
            r0.setDelayAnimations(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r13.listView
            r0.setClipToPadding(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r13.listView
            r0.setHideIfEmpty(r1)
            org.telegram.ui.ProfileActivity$11 r0 = new org.telegram.ui.ProfileActivity$11
            r0.<init>(r10)
            r13.layoutManager = r0
            r0.setOrientation(r11)
            androidx.recyclerview.widget.LinearLayoutManager r0 = r13.layoutManager
            r0.mIgnoreTopPadding = r1
            org.telegram.ui.Components.RecyclerListView r2 = r13.listView
            r2.setLayoutManager(r0)
            org.telegram.ui.Components.RecyclerListView r0 = r13.listView
            r0.setGlowColor(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r13.listView
            org.telegram.ui.ProfileActivity$ListAdapter r1 = r13.listAdapter
            r0.setAdapter(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r13.listView
            r1 = 51
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r9, (int) r9, (int) r1)
            r15.addView(r0, r2)
            org.telegram.ui.Components.RecyclerListView r0 = r13.listView
            org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda35 r2 = new org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda35
            r3 = r24
            r2.<init>(r13, r3, r10)
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended) r2)
            org.telegram.ui.Components.RecyclerListView r0 = r13.listView
            org.telegram.ui.ProfileActivity$13 r2 = new org.telegram.ui.ProfileActivity$13
            r2.<init>()
            r0.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r13.searchItem
            java.lang.String r2 = "avatar_backgroundActionBarBlue"
            if (r0 == 0) goto L_0x033b
            org.telegram.ui.Components.RecyclerListView r0 = new org.telegram.ui.Components.RecyclerListView
            r0.<init>(r10)
            r13.searchListView = r0
            r3 = 0
            r0.setVerticalScrollBarEnabled(r3)
            org.telegram.ui.Components.RecyclerListView r0 = r13.searchListView
            androidx.recyclerview.widget.LinearLayoutManager r4 = new androidx.recyclerview.widget.LinearLayoutManager
            r4.<init>(r10, r11, r3)
            r0.setLayoutManager(r4)
            org.telegram.ui.Components.RecyclerListView r0 = r13.searchListView
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setGlowColor(r3)
            org.telegram.ui.Components.RecyclerListView r0 = r13.searchListView
            org.telegram.ui.ProfileActivity$SearchAdapter r3 = r13.searchAdapter
            r0.setAdapter(r3)
            org.telegram.ui.Components.RecyclerListView r0 = r13.searchListView
            r3 = 0
            r0.setItemAnimator(r3)
            org.telegram.ui.Components.RecyclerListView r0 = r13.searchListView
            r0.setVisibility(r5)
            org.telegram.ui.Components.RecyclerListView r0 = r13.searchListView
            r0.setLayoutAnimation(r3)
            org.telegram.ui.Components.RecyclerListView r0 = r13.searchListView
            java.lang.String r4 = "windowBackgroundWhite"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r0.setBackgroundColor(r4)
            org.telegram.ui.Components.RecyclerListView r0 = r13.searchListView
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r9, (int) r9, (int) r1)
            r15.addView(r0, r4)
            org.telegram.ui.Components.RecyclerListView r0 = r13.searchListView
            org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda34 r4 = new org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda34
            r4.<init>(r13)
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r4)
            org.telegram.ui.Components.RecyclerListView r0 = r13.searchListView
            org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda36 r4 = new org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda36
            r4.<init>(r13)
            r0.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r4)
            org.telegram.ui.Components.RecyclerListView r0 = r13.searchListView
            org.telegram.ui.ProfileActivity$14 r4 = new org.telegram.ui.ProfileActivity$14
            r4.<init>()
            r0.setOnScrollListener(r4)
            org.telegram.ui.Components.RecyclerListView r0 = r13.searchListView
            r0.setAnimateEmptyView(r11, r11)
            org.telegram.ui.Components.StickerEmptyView r0 = new org.telegram.ui.Components.StickerEmptyView
            r0.<init>(r10, r3, r11)
            r13.emptyView = r0
            r0.setAnimateLayoutChange(r11)
            org.telegram.ui.Components.StickerEmptyView r0 = r13.emptyView
            android.widget.TextView r0 = r0.subtitle
            r0.setVisibility(r5)
            org.telegram.ui.Components.StickerEmptyView r0 = r13.emptyView
            r0.setVisibility(r5)
            org.telegram.ui.Components.StickerEmptyView r0 = r13.emptyView
            r15.addView(r0)
            org.telegram.ui.ProfileActivity$SearchAdapter r0 = r13.searchAdapter
            r0.loadFaqWebPage()
            goto L_0x033c
        L_0x033b:
            r3 = 0
        L_0x033c:
            long r4 = r13.banFromGroup
            java.lang.String r19 = "fonts/rmedium.ttf"
            r20 = 1111490560(0x42400000, float:48.0)
            int r0 = (r4 > r17 ? 1 : (r4 == r17 ? 0 : -1))
            if (r0 == 0) goto L_0x03ed
            org.telegram.messenger.MessagesController r0 = r37.getMessagesController()
            long r4 = r13.banFromGroup
            java.lang.Long r4 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r4)
            org.telegram.tgnet.TLRPC$ChannelParticipant r4 = r13.currentChannelParticipant
            if (r4 != 0) goto L_0x037b
            org.telegram.tgnet.TLRPC$TL_channels_getParticipant r4 = new org.telegram.tgnet.TLRPC$TL_channels_getParticipant
            r4.<init>()
            org.telegram.tgnet.TLRPC$InputChannel r5 = org.telegram.messenger.MessagesController.getInputChannel((org.telegram.tgnet.TLRPC$Chat) r0)
            r4.channel = r5
            org.telegram.messenger.MessagesController r5 = r37.getMessagesController()
            long r6 = r13.userId
            org.telegram.tgnet.TLRPC$InputPeer r5 = r5.getInputPeer((long) r6)
            r4.participant = r5
            org.telegram.tgnet.ConnectionsManager r5 = r37.getConnectionsManager()
            org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda30 r6 = new org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda30
            r6.<init>(r13)
            r5.sendRequest(r4, r6)
        L_0x037b:
            org.telegram.ui.ProfileActivity$15 r4 = new org.telegram.ui.ProfileActivity$15
            r4.<init>(r13, r10)
            r5 = 0
            r4.setWillNotDraw(r5)
            r5 = 83
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r9, (int) r1, (int) r5)
            r15.addView(r4, r5)
            org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda14 r5 = new org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda14
            r5.<init>(r13, r0)
            r4.setOnClickListener(r5)
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r10)
            java.lang.String r5 = "windowBackgroundWhiteRedText"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.setTextColor(r5)
            r5 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r11, r5)
            r5 = 17
            r0.setGravity(r5)
            android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r19)
            r0.setTypeface(r5)
            r5 = 2131624604(0x7f0e029c, float:1.8876392E38)
            java.lang.String r6 = "BanFromTheGroup"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r0.setText(r5)
            r27 = -2
            r28 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r29 = 17
            r30 = 0
            r31 = 1065353216(0x3var_, float:1.0)
            r32 = 0
            r33 = 0
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r27, r28, r29, r30, r31, r32, r33)
            r4.addView(r0, r5)
            org.telegram.ui.Components.RecyclerListView r0 = r13.listView
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r6 = 0
            r0.setPadding(r6, r4, r6, r5)
            org.telegram.ui.Components.RecyclerListView r0 = r13.listView
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r0.setBottomGlowOffset(r4)
            goto L_0x03f7
        L_0x03ed:
            r6 = 0
            org.telegram.ui.Components.RecyclerListView r0 = r13.listView
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r0.setPadding(r6, r4, r6, r6)
        L_0x03f7:
            org.telegram.ui.ProfileActivity$TopView r0 = new org.telegram.ui.ProfileActivity$TopView
            r0.<init>(r10)
            r13.topView = r0
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setBackgroundColor(r2)
            org.telegram.ui.ProfileActivity$TopView r0 = r13.topView
            r15.addView(r0)
            org.telegram.ui.ProfileActivity$NestedFrameLayout r0 = r13.contentView
            java.util.ArrayList<android.view.View> r0 = r0.blurBehindViews
            org.telegram.ui.ProfileActivity$TopView r2 = r13.topView
            r0.add(r2)
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r10)
            r13.avatarContainer = r0
            org.telegram.ui.ProfileActivity$17 r0 = new org.telegram.ui.ProfileActivity$17
            r0.<init>(r10)
            r13.avatarContainer2 = r0
            r2 = 0
            r8 = 1065353216(0x3var_, float:1.0)
            org.telegram.messenger.AndroidUtilities.updateViewVisibilityAnimated(r0, r11, r8, r2)
            android.widget.FrameLayout r0 = r13.avatarContainer2
            r23 = -1
            r24 = -1082130432(0xffffffffbvar_, float:-1.0)
            r25 = 8388611(0x800003, float:1.1754948E-38)
            r26 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
            r15.addView(r0, r2)
            android.widget.FrameLayout r0 = r13.avatarContainer
            r7 = 0
            r0.setPivotX(r7)
            android.widget.FrameLayout r0 = r13.avatarContainer
            r0.setPivotY(r7)
            android.widget.FrameLayout r0 = r13.avatarContainer2
            android.widget.FrameLayout r2 = r13.avatarContainer
            r23 = 42
            r24 = 1109917696(0x42280000, float:42.0)
            r25 = 51
            r26 = 1115684864(0x42800000, float:64.0)
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
            r0.addView(r2, r4)
            org.telegram.ui.ProfileActivity$18 r0 = new org.telegram.ui.ProfileActivity$18
            r0.<init>(r13, r10)
            r13.avatarImage = r0
            org.telegram.messenger.ImageReceiver r0 = r0.getImageReceiver()
            r0.setAllowDecodeSingleFrame(r11)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r13.avatarImage
            r2 = 1101529088(0x41a80000, float:21.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setRoundRadius(r2)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r13.avatarImage
            r0.setPivotX(r7)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r13.avatarImage
            r0.setPivotY(r7)
            android.widget.FrameLayout r0 = r13.avatarContainer
            org.telegram.ui.ProfileActivity$AvatarImageView r2 = r13.avatarImage
            r6 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r6)
            r0.addView(r2, r4)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r13.avatarImage
            org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda13 r2 = new org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda13
            r2.<init>(r13)
            r0.setOnClickListener(r2)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r13.avatarImage
            org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda15 r2 = new org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda15
            r2.<init>(r13)
            r0.setOnLongClickListener(r2)
            org.telegram.ui.ProfileActivity$19 r0 = new org.telegram.ui.ProfileActivity$19
            r0.<init>(r10)
            r13.avatarProgressView = r0
            r2 = 1104150528(0x41d00000, float:26.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setSize(r2)
            org.telegram.ui.Components.RadialProgressView r0 = r13.avatarProgressView
            r0.setProgressColor(r9)
            org.telegram.ui.Components.RadialProgressView r0 = r13.avatarProgressView
            r2 = 0
            r0.setNoProgress(r2)
            android.widget.FrameLayout r0 = r13.avatarContainer
            org.telegram.ui.Components.RadialProgressView r2 = r13.avatarProgressView
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r6)
            r0.addView(r2, r4)
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r10)
            r13.timeItem = r0
            r2 = 1092616192(0x41200000, float:10.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r5 = 1084227584(0x40a00000, float:5.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r0.setPadding(r4, r2, r6, r5)
            android.widget.ImageView r0 = r13.timeItem
            android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.CENTER
            r0.setScaleType(r2)
            android.widget.ImageView r0 = r13.timeItem
            r0.setAlpha(r7)
            android.widget.ImageView r0 = r13.timeItem
            org.telegram.ui.Components.TimerDrawable r2 = new org.telegram.ui.Components.TimerDrawable
            r2.<init>(r10, r3)
            r13.timerDrawable = r2
            r0.setImageDrawable(r2)
            android.widget.ImageView r0 = r13.timeItem
            r2 = 34
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r2, (int) r2, (int) r1)
            r15.addView(r0, r1)
            r0 = 0
            r13.showAvatarProgress(r0, r0)
            org.telegram.ui.Components.ProfileGalleryView r0 = r13.avatarsViewPager
            if (r0 == 0) goto L_0x0513
            r0.onDestroy()
        L_0x0513:
            org.telegram.ui.ProfileActivity$OverlaysView r0 = new org.telegram.ui.ProfileActivity$OverlaysView
            r0.<init>(r10)
            r13.overlaysView = r0
            org.telegram.ui.Components.ProfileGalleryView r6 = new org.telegram.ui.Components.ProfileGalleryView
            long r0 = r13.userId
            int r2 = (r0 > r17 ? 1 : (r0 == r17 ? 0 : -1))
            if (r2 == 0) goto L_0x0523
            goto L_0x0526
        L_0x0523:
            long r0 = r13.chatId
            long r0 = -r0
        L_0x0526:
            r2 = r0
            org.telegram.ui.ActionBar.ActionBar r4 = r13.actionBar
            org.telegram.ui.Components.RecyclerListView r5 = r13.listView
            org.telegram.ui.ProfileActivity$AvatarImageView r1 = r13.avatarImage
            int r21 = r37.getClassGuid()
            org.telegram.ui.ProfileActivity$OverlaysView r0 = r13.overlaysView
            r22 = r0
            r0 = r6
            r23 = r1
            r1 = r38
            r11 = r6
            r9 = -1082130432(0xffffffffbvar_, float:-1.0)
            r6 = r23
            r7 = r21
            r21 = 1065353216(0x3var_, float:1.0)
            r8 = r22
            r0.<init>(r1, r2, r4, r5, r6, r7, r8)
            r13.avatarsViewPager = r11
            org.telegram.tgnet.TLRPC$ChatFull r0 = r13.chatInfo
            r11.setChatInfo(r0)
            android.widget.FrameLayout r0 = r13.avatarContainer2
            org.telegram.ui.Components.ProfileGalleryView r1 = r13.avatarsViewPager
            r0.addView(r1)
            android.widget.FrameLayout r0 = r13.avatarContainer2
            org.telegram.ui.ProfileActivity$OverlaysView r1 = r13.overlaysView
            r0.addView(r1)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r13.avatarImage
            org.telegram.ui.Components.ProfileGalleryView r1 = r13.avatarsViewPager
            r0.setAvatarsViewPager(r1)
            org.telegram.ui.ProfileActivity$PagerIndicatorView r0 = new org.telegram.ui.ProfileActivity$PagerIndicatorView
            r0.<init>(r10)
            r13.avatarsViewPagerIndicatorView = r0
            android.widget.FrameLayout r1 = r13.avatarContainer2
            r2 = -1
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r9)
            r1.addView(r0, r3)
            org.telegram.ui.ActionBar.ActionBar r0 = r13.actionBar
            r15.addView(r0)
            r0 = 0
        L_0x057b:
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r13.nameTextView
            int r2 = r1.length
            r3 = 2
            if (r0 >= r2) goto L_0x063d
            int r2 = r13.playProfileAnimation
            if (r2 != 0) goto L_0x058b
            if (r0 != 0) goto L_0x058b
            r1 = 1
            r2 = 0
            goto L_0x0639
        L_0x058b:
            org.telegram.ui.ActionBar.SimpleTextView r2 = new org.telegram.ui.ActionBar.SimpleTextView
            r2.<init>(r10)
            r1[r0] = r2
            r1 = 1
            if (r0 != r1) goto L_0x05a3
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r13.nameTextView
            r1 = r1[r0]
            java.lang.String r2 = "profile_title"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setTextColor(r2)
            goto L_0x05b0
        L_0x05a3:
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r13.nameTextView
            r1 = r1[r0]
            java.lang.String r2 = "actionBarDefaultTitle"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setTextColor(r2)
        L_0x05b0:
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r13.nameTextView
            r1 = r1[r0]
            r2 = 18
            r1.setTextSize(r2)
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r13.nameTextView
            r1 = r1[r0]
            r2 = 3
            r1.setGravity(r2)
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r13.nameTextView
            r1 = r1[r0]
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r19)
            r1.setTypeface(r2)
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r13.nameTextView
            r1 = r1[r0]
            r2 = 1067869798(0x3fa66666, float:1.3)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = -r2
            r1.setLeftDrawableTopPadding(r2)
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r13.nameTextView
            r1 = r1[r0]
            r2 = 0
            r1.setPivotX(r2)
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r13.nameTextView
            r1 = r1[r0]
            r1.setPivotY(r2)
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r13.nameTextView
            r1 = r1[r0]
            if (r0 != 0) goto L_0x05f2
            r4 = 0
            goto L_0x05f4
        L_0x05f2:
            r4 = 1065353216(0x3var_, float:1.0)
        L_0x05f4:
            r1.setAlpha(r4)
            r1 = 1
            if (r0 != r1) goto L_0x0608
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r13.nameTextView
            r4 = r4[r0]
            r4.setScrollNonFitText(r1)
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r13.nameTextView
            r4 = r4[r0]
            r4.setImportantForAccessibility(r3)
        L_0x0608:
            if (r0 != 0) goto L_0x061c
            r3 = 48
            boolean r4 = r13.callItemVisible
            if (r4 == 0) goto L_0x0619
            long r4 = r13.userId
            int r6 = (r4 > r17 ? 1 : (r4 == r17 ? 0 : -1))
            if (r6 == 0) goto L_0x0619
            r4 = 48
            goto L_0x061a
        L_0x0619:
            r4 = 0
        L_0x061a:
            int r3 = r3 + r4
            goto L_0x061d
        L_0x061c:
            r3 = 0
        L_0x061d:
            android.widget.FrameLayout r4 = r13.avatarContainer2
            org.telegram.ui.ActionBar.SimpleTextView[] r5 = r13.nameTextView
            r5 = r5[r0]
            r23 = -2
            r24 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r25 = 51
            r26 = 1122762752(0x42eCLASSNAME, float:118.0)
            r27 = 0
            float r3 = (float) r3
            r29 = 0
            r28 = r3
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
            r4.addView(r5, r3)
        L_0x0639:
            int r0 = r0 + 1
            goto L_0x057b
        L_0x063d:
            r2 = 0
            r0 = 0
        L_0x063f:
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r13.onlineTextView
            int r4 = r1.length
            if (r0 >= r4) goto L_0x06a7
            org.telegram.ui.ActionBar.SimpleTextView r4 = new org.telegram.ui.ActionBar.SimpleTextView
            r4.<init>(r10)
            r1[r0] = r4
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r13.onlineTextView
            r1 = r1[r0]
            java.lang.String r4 = "avatar_subtitleInProfileBlue"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r1.setTextColor(r4)
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r13.onlineTextView
            r1 = r1[r0]
            r4 = 14
            r1.setTextSize(r4)
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r13.onlineTextView
            r1 = r1[r0]
            r4 = 3
            r1.setGravity(r4)
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r13.onlineTextView
            r1 = r1[r0]
            if (r0 == 0) goto L_0x0675
            if (r0 != r3) goto L_0x0672
            goto L_0x0675
        L_0x0672:
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x0676
        L_0x0675:
            r4 = 0
        L_0x0676:
            r1.setAlpha(r4)
            if (r0 <= 0) goto L_0x0682
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r13.onlineTextView
            r1 = r1[r0]
            r1.setImportantForAccessibility(r3)
        L_0x0682:
            android.widget.FrameLayout r1 = r13.avatarContainer2
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r13.onlineTextView
            r4 = r4[r0]
            r23 = -2
            r24 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r25 = 51
            r26 = 1122762752(0x42eCLASSNAME, float:118.0)
            r27 = 0
            if (r0 != 0) goto L_0x0697
            r28 = 1111490560(0x42400000, float:48.0)
            goto L_0x069b
        L_0x0697:
            r5 = 1090519040(0x41000000, float:8.0)
            r28 = 1090519040(0x41000000, float:8.0)
        L_0x069b:
            r29 = 0
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
            r1.addView(r4, r5)
            int r0 = r0 + 1
            goto L_0x063f
        L_0x06a7:
            org.telegram.ui.ProfileActivity$20 r0 = new org.telegram.ui.ProfileActivity$20
            r0.<init>(r13, r10, r10)
            r13.mediaCounterTextView = r0
            r0.setAlpha(r2)
            android.widget.FrameLayout r0 = r13.avatarContainer2
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r1 = r13.mediaCounterTextView
            r23 = -2
            r24 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r25 = 51
            r26 = 1122762752(0x42eCLASSNAME, float:118.0)
            r27 = 0
            r28 = 1090519040(0x41000000, float:8.0)
            r29 = 0
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
            r0.addView(r1, r4)
            r37.updateProfileData()
            org.telegram.ui.Components.RLottieImageView r0 = new org.telegram.ui.Components.RLottieImageView
            r0.<init>(r10)
            r13.writeButton = r0
            android.content.res.Resources r0 = r38.getResources()
            r1 = 2131165452(0x7var_c, float:1.7945122E38)
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r1)
            android.graphics.drawable.Drawable r0 = r0.mutate()
            android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
            r4 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.graphics.PorterDuff$Mode r5 = android.graphics.PorterDuff.Mode.MULTIPLY
            r1.<init>(r4, r5)
            r0.setColorFilter(r1)
            org.telegram.ui.Components.CombinedDrawable r1 = new org.telegram.ui.Components.CombinedDrawable
            r4 = 1113587712(0x42600000, float:56.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            java.lang.String r6 = "profile_actionBackground"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            java.lang.String r7 = "profile_actionPressedBackground"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorCircleDrawable(r5, r6, r7)
            r6 = 0
            r1.<init>(r0, r5, r6, r6)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.setIconSize(r0, r5)
            org.telegram.ui.Components.RLottieImageView r0 = r13.writeButton
            r0.setBackground(r1)
            long r0 = r13.userId
            int r5 = (r0 > r17 ? 1 : (r0 == r17 ? 0 : -1))
            if (r5 == 0) goto L_0x079f
            org.telegram.ui.Components.ImageUpdater r0 = r13.imageUpdater
            if (r0 == 0) goto L_0x0788
            org.telegram.ui.Components.RLottieDrawable r0 = new org.telegram.ui.Components.RLottieDrawable
            r24 = 2131558414(0x7f0d000e, float:1.8742143E38)
            r1 = 2131558414(0x7f0d000e, float:1.8742143E38)
            java.lang.String r25 = java.lang.String.valueOf(r1)
            int r26 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r27 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r28 = 0
            r29 = 0
            r23 = r0
            r23.<init>(r24, r25, r26, r27, r28, r29)
            r13.cameraDrawable = r0
            org.telegram.ui.Components.RLottieDrawable r0 = new org.telegram.ui.Components.RLottieDrawable
            r31 = 2131558414(0x7f0d000e, float:1.8742143E38)
            r1 = 1109917696(0x42280000, float:42.0)
            int r33 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r34 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r35 = 0
            r36 = 0
            java.lang.String r32 = "2131558414_cell"
            r30 = r0
            r30.<init>(r31, r32, r33, r34, r35, r36)
            r13.cellCameraDrawable = r0
            org.telegram.ui.Components.RLottieImageView r0 = r13.writeButton
            org.telegram.ui.Components.RLottieDrawable r1 = r13.cameraDrawable
            r0.setAnimation(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r13.writeButton
            r1 = 2131623964(0x7f0e001c, float:1.8875094E38)
            java.lang.String r4 = "AccDescrChangeProfilePicture"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            r0.setContentDescription(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r13.writeButton
            r1 = 1073741824(0x40000000, float:2.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r4 = 1073741824(0x40000000, float:2.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r5 = 0
            r0.setPadding(r1, r5, r5, r4)
            goto L_0x07b5
        L_0x0788:
            org.telegram.ui.Components.RLottieImageView r0 = r13.writeButton
            r1 = 2131166075(0x7var_b, float:1.7946385E38)
            r0.setImageResource(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r13.writeButton
            r1 = 2131624001(0x7f0e0041, float:1.887517E38)
            java.lang.String r4 = "AccDescrOpenChat"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            r0.setContentDescription(r1)
            goto L_0x07b5
        L_0x079f:
            org.telegram.ui.Components.RLottieImageView r0 = r13.writeButton
            r1 = 2131166071(0x7var_, float:1.7946377E38)
            r0.setImageResource(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r13.writeButton
            r1 = 2131628670(0x7f0e127e, float:1.888464E38)
            java.lang.String r4 = "ViewDiscussion"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            r0.setContentDescription(r1)
        L_0x07b5:
            org.telegram.ui.Components.RLottieImageView r0 = r13.writeButton
            android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
            java.lang.String r4 = "profile_actionIcon"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            android.graphics.PorterDuff$Mode r5 = android.graphics.PorterDuff.Mode.MULTIPLY
            r1.<init>(r4, r5)
            r0.setColorFilter(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r13.writeButton
            android.widget.ImageView$ScaleType r1 = android.widget.ImageView.ScaleType.CENTER
            r0.setScaleType(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r13.writeButton
            r23 = 60
            r24 = 1114636288(0x42700000, float:60.0)
            r25 = 53
            r26 = 0
            r27 = 0
            r28 = 1098907648(0x41800000, float:16.0)
            r29 = 0
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
            r15.addView(r0, r1)
            org.telegram.ui.Components.RLottieImageView r0 = r13.writeButton
            org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda12 r1 = new org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda12
            r1.<init>(r13)
            r0.setOnClickListener(r1)
            r0 = 0
            r13.needLayout(r0)
            r1 = -1
            if (r12 == r1) goto L_0x0813
            if (r14 == 0) goto L_0x0813
            org.telegram.ui.Components.RLottieImageView r1 = r13.writeButton
            java.lang.Integer r4 = java.lang.Integer.valueOf(r0)
            r1.setTag(r4)
            org.telegram.ui.Components.RLottieImageView r0 = r13.writeButton
            r1 = 1045220557(0x3e4ccccd, float:0.2)
            r0.setScaleX(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r13.writeButton
            r0.setScaleY(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r13.writeButton
            r0.setAlpha(r2)
        L_0x0813:
            org.telegram.ui.Components.RecyclerListView r0 = r13.listView
            org.telegram.ui.ProfileActivity$21 r1 = new org.telegram.ui.ProfileActivity$21
            r1.<init>()
            r0.setOnScrollListener(r1)
            org.telegram.ui.Components.UndoView r0 = new org.telegram.ui.Components.UndoView
            r0.<init>(r10)
            r13.undoView = r0
            r4 = -1
            r5 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r6 = 83
            r7 = 1090519040(0x41000000, float:8.0)
            r8 = 0
            r9 = 1090519040(0x41000000, float:8.0)
            r10 = 1090519040(0x41000000, float:8.0)
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r5, r6, r7, r8, r9, r10)
            r15.addView(r0, r1)
            float[] r0 = new float[r3]
            r0 = {0, NUM} // fill-array
            android.animation.ValueAnimator r0 = android.animation.ValueAnimator.ofFloat(r0)
            r13.expandAnimator = r0
            org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda0 r1 = new org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda0
            r1.<init>(r13)
            r0.addUpdateListener(r1)
            android.animation.ValueAnimator r0 = r13.expandAnimator
            org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
            r0.setInterpolator(r1)
            android.animation.ValueAnimator r0 = r13.expandAnimator
            org.telegram.ui.ProfileActivity$22 r1 = new org.telegram.ui.ProfileActivity$22
            r1.<init>()
            r0.addListener(r1)
            r37.updateRowsIds()
            r37.updateSelectedMediaTabText()
            org.telegram.ui.Components.HintView r0 = new org.telegram.ui.Components.HintView
            android.app.Activity r1 = r37.getParentActivity()
            r3 = 9
            r0.<init>(r1, r3)
            r13.fwdRestrictedHint = r0
            r0.setAlpha(r2)
            org.telegram.ui.Components.HintView r0 = r13.fwdRestrictedHint
            r1 = -2
            r2 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r3 = 51
            r4 = 1094713344(0x41400000, float:12.0)
            r5 = 0
            r6 = 1094713344(0x41400000, float:12.0)
            r7 = 0
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r1, r2, r3, r4, r5, r6, r7)
            r15.addView(r0, r1)
            org.telegram.ui.Components.SharedMediaLayout r0 = r13.sharedMediaLayout
            org.telegram.ui.Components.HintView r1 = r13.fwdRestrictedHint
            r0.setForwardRestrictedHint(r1)
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 21
            if (r0 < r1) goto L_0x08a1
            android.app.Activity r0 = r37.getParentActivity()
            android.view.Window r0 = r0.getWindow()
            android.view.View r0 = r0.getDecorView()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            goto L_0x08a2
        L_0x08a1:
            r0 = r15
        L_0x08a2:
            org.telegram.ui.ProfileActivity$23 r1 = new org.telegram.ui.ProfileActivity$23
            r1.<init>(r0, r15)
            r13.pinchToZoomHelper = r1
            org.telegram.ui.ProfileActivity$24 r0 = new org.telegram.ui.ProfileActivity$24
            r0.<init>()
            r1.setCallback(r0)
            org.telegram.ui.Components.ProfileGalleryView r0 = r13.avatarsViewPager
            org.telegram.ui.PinchToZoomHelper r1 = r13.pinchToZoomHelper
            r0.setPinchToZoomHelper(r1)
            android.graphics.Paint r0 = r13.scrimPaint
            r1 = 0
            r0.setAlpha(r1)
            android.graphics.Paint r0 = r13.actionBarBackgroundPaint
            java.lang.String r1 = "listSelectorSDK21"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setColor(r1)
            org.telegram.ui.ProfileActivity$NestedFrameLayout r0 = r13.contentView
            java.util.ArrayList<android.view.View> r0 = r0.blurBehindViews
            org.telegram.ui.Components.SharedMediaLayout r1 = r13.sharedMediaLayout
            r0.add(r1)
            r37.updateTtlIcon()
            android.view.View r0 = r13.fragmentView
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
                showDialog(AlertsCreator.createTTLAlert(getParentActivity(), this.currentEncryptedChat, (Theme.ResourcesProvider) null).create());
            } else if (i2 == this.notificationsRow) {
                if ((!LocaleController.isRTL || f3 > ((float) AndroidUtilities.dp(76.0f))) && (LocaleController.isRTL || f3 < ((float) (view.getMeasuredWidth() - AndroidUtilities.dp(76.0f))))) {
                    ChatNotificationsPopupWrapper chatNotificationsPopupWrapper = new ChatNotificationsPopupWrapper(context, this.currentAccount, (PopupSwipeBackLayout) null, true, true, new ChatNotificationsPopupWrapper.Callback() {
                        public void dismiss() {
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
                            ProfileActivity.this.getNotificationsController().muteUntil(j2, i);
                            if (BulletinFactory.canShowBulletin(ProfileActivity.this)) {
                                ProfileActivity profileActivity = ProfileActivity.this;
                                BulletinFactory.createMuteBulletin(profileActivity, 5, i, profileActivity.getResourceProvider()).show();
                            }
                            if (ProfileActivity.this.notificationsRow >= 0) {
                                ProfileActivity.this.listAdapter.notifyItemChanged(ProfileActivity.this.notificationsRow);
                            }
                        }

                        public void showCustomize() {
                            if (j2 != 0) {
                                Bundle bundle = new Bundle();
                                bundle.putLong("dialog_id", j2);
                                ProfileActivity.this.presentFragment(new ProfileNotificationsActivity(bundle));
                            }
                        }

                        public void toggleMute() {
                            ProfileActivity.this.getNotificationsController().muteDialog(j2, !ProfileActivity.this.getMessagesController().isDialogMuted(j2));
                            ProfileActivity profileActivity = ProfileActivity.this;
                            BulletinFactory.createMuteBulletin(profileActivity, profileActivity.getMessagesController().isDialogMuted(ProfileActivity.this.dialogId), (Theme.ResourcesProvider) null).show();
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
                AlertsCreator.createReportAlert(getParentActivity(), getDialogId(), 0, this, (Runnable) null);
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
                if (this.currentChat != null) {
                    try {
                        Intent intent = new Intent("android.intent.action.SEND");
                        intent.setType("text/plain");
                        if (!TextUtils.isEmpty(this.chatInfo.about)) {
                            intent.putExtra("android.intent.extra.TEXT", this.currentChat.title + "\n" + this.chatInfo.about + "\nhttps://" + getMessagesController().linkPrefix + "/" + this.currentChat.username);
                        } else {
                            intent.putExtra("android.intent.extra.TEXT", this.currentChat.title + "\nhttps://" + getMessagesController().linkPrefix + "/" + this.currentChat.username);
                        }
                        getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString("BotShare", NUM)), 500);
                    } catch (Exception e2) {
                        FileLog.e((Throwable) e2);
                    }
                }
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
            } else if (i2 == this.devicesRow) {
                presentFragment(new SessionsActivity(0));
            } else if (i2 == this.questionRow) {
                showDialog(AlertsCreator.createSupportAlert(this));
            } else if (i2 == this.faqRow) {
                Browser.openUrl((Context) getParentActivity(), LocaleController.getString("TelegramFaqUrl", NUM));
            } else if (i2 == this.policyRow) {
                Browser.openUrl((Context) getParentActivity(), LocaleController.getString("PrivacyPolicyUrl", NUM));
            } else if (i2 == this.sendLogsRow) {
                sendLogs(false);
            } else if (i2 == this.sendLastLogsRow) {
                sendLogs(true);
            } else if (i2 == this.clearLogsRow) {
                FileLog.cleanupLogs();
            } else if (i2 == this.switchBackendRow) {
                if (getParentActivity() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    builder.setMessage(LocaleController.getString("AreYouSure", NUM));
                    builder.setTitle(LocaleController.getString("AppName", NUM));
                    builder.setPositiveButton(LocaleController.getString("OK", NUM), new ProfileActivity$$ExternalSyntheticLambda4(this));
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
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
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setMessage(LocaleController.getString("ClearSearch", NUM));
        builder.setPositiveButton(LocaleController.getString("ClearButton", NUM).toUpperCase(), new ProfileActivity$$ExternalSyntheticLambda5(this));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
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
            if (!AndroidUtilities.isTablet() && !this.isInLandscapeMode && this.avatarImage.getImageReceiver().hasNotThumb()) {
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
            scamDrawable2.setColor(ColorUtils.blendARGB(Theme.getColor("avatar_subtitleInProfileBlue"), Color.argb(179, 255, 255, 255), lerp));
        }
        Drawable drawable = this.lockIconDrawable;
        if (drawable != null) {
            drawable.setColorFilter(ColorUtils.blendARGB(Theme.getColor("chat_lockIcon"), -1, lerp), PorterDuff.Mode.MULTIPLY);
        }
        CrossfadeDrawable crossfadeDrawable = this.verifiedCrossfadeDrawable;
        if (crossfadeDrawable != null) {
            crossfadeDrawable.setProgress(lerp);
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
            i = Theme.getColor((String) tag);
        } else {
            i = Theme.getColor("avatar_subtitleInProfileBlue");
        }
        this.onlineTextView[1].setTextColor(ColorUtils.blendARGB(i, Color.argb(179, 255, 255, 255), lerp));
        if (this.extraHeight > ((float) AndroidUtilities.dp(88.0f))) {
            SimpleTextView[] simpleTextViewArr = this.nameTextView;
            simpleTextViewArr[1].setPivotY((float) AndroidUtilities.lerp(0, simpleTextViewArr[1].getMeasuredHeight(), lerp));
            this.nameTextView[1].setScaleX(AndroidUtilities.lerp(1.12f, 1.67f, lerp));
            this.nameTextView[1].setScaleY(AndroidUtilities.lerp(1.12f, 1.67f, lerp));
        }
        needLayoutText(Math.min(1.0f, this.extraHeight / ((float) AndroidUtilities.dp(88.0f))));
        this.nameTextView[1].setTextColor(ColorUtils.blendARGB(Theme.getColor("profile_title"), -1, lerp));
        this.actionBar.setItemsColor(ColorUtils.blendARGB(Theme.getColor("actionBarDefaultIcon"), -1, lerp), false);
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
                    imageUpdater2.openMenu(z, new ProfileActivity$$ExternalSyntheticLambda19(this), new ProfileActivity$$ExternalSyntheticLambda11(this));
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
                    i = NUM;
                    str = "EditAdminRights";
                } else {
                    i = NUM;
                    str = "SetAsAdmin";
                }
                arrayList.add(LocaleController.getString(str, i));
                arrayList2.add(NUM);
                arrayList3.add(0);
            }
            if (z4) {
                if (z2) {
                    return true;
                }
                arrayList.add(LocaleController.getString("ChangePermissions", NUM));
                arrayList2.add(NUM);
                arrayList3.add(1);
            }
            if (!z6) {
                z7 = false;
            } else if (z2) {
                return true;
            } else {
                arrayList.add(LocaleController.getString("KickFromGroup", NUM));
                arrayList2.add(NUM);
                arrayList3.add(2);
                z7 = true;
            }
            if (z2 || arrayList.isEmpty()) {
                return false;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[0]), AndroidUtilities.toIntArray(arrayList2), new ProfileActivity$$ExternalSyntheticLambda8(this, arrayList3, tLRPC$ChatParticipant, tLRPC$ChannelParticipant, user, z3));
            AlertDialog create = builder.create();
            showDialog(create);
            if (z7) {
                create.setItemColor(arrayList.size() - 1, Theme.getColor("dialogTextRed2"), Theme.getColor("dialogRedIcon"));
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
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setMessage(LocaleController.formatString("AdminWillBeRemoved", NUM, ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name)));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), new ProfileActivity$$ExternalSyntheticLambda10(this, tLRPC$ChannelParticipant, intValue, tLRPC$User, tLRPC$ChatParticipant, z));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
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
        AnonymousClass25 r18 = r0;
        final TLRPC$User tLRPC$User2 = tLRPC$User;
        AnonymousClass25 r0 = new ChatRightsEditActivity(this, tLRPC$User.id, this.chatId, tLRPC$TL_chatAdminRights, this.currentChat.default_banned_rights, tLRPC$TL_chatBannedRights, str, i, true, false, (String) null) {
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
        AnonymousClass25 r02 = r18;
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
        if (i == this.usernameRow || i == this.setUsernameRow) {
            if (this.userId != 0) {
                TLRPC$User user = getMessagesController().getUser(Long.valueOf(this.userId));
                if (user == null || (str = user.username) == null) {
                    return false;
                }
            } else if (this.chatId == 0 || (chat = getMessagesController().getChat(Long.valueOf(this.chatId))) == null || (str = chat.username) == null) {
                return false;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setItems(new CharSequence[]{LocaleController.getString("Copy", NUM)}, new ProfileActivity$$ExternalSyntheticLambda7(this, str));
            showDialog(builder.create());
            return true;
        } else if (i == this.phoneRow || i == this.numberRow) {
            TLRPC$User user2 = getMessagesController().getUser(Long.valueOf(this.userId));
            if (user2 == null || (str2 = user2.phone) == null || str2.length() == 0 || getParentActivity() == null) {
                return false;
            }
            AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            if (i == this.phoneRow) {
                TLRPC$UserFull tLRPC$UserFull2 = this.userInfo;
                if (tLRPC$UserFull2 != null && tLRPC$UserFull2.phone_calls_available) {
                    arrayList.add(LocaleController.getString("CallViaTelegram", NUM));
                    arrayList2.add(2);
                    if (Build.VERSION.SDK_INT >= 18 && this.userInfo.video_calls_available) {
                        arrayList.add(LocaleController.getString("VideoCallViaTelegram", NUM));
                        arrayList2.add(3);
                    }
                }
                arrayList.add(LocaleController.getString("Call", NUM));
                arrayList2.add(0);
            }
            arrayList.add(LocaleController.getString("Copy", NUM));
            arrayList2.add(1);
            builder2.setItems((CharSequence[]) arrayList.toArray(new CharSequence[0]), new ProfileActivity$$ExternalSyntheticLambda9(this, arrayList2, user2));
            showDialog(builder2.create());
            return true;
        } else if (i != this.channelInfoRow && i != this.userInfoRow && i != this.locationRow && i != this.bioRow) {
            return false;
        } else {
            if (i == this.bioRow && ((tLRPC$UserFull = this.userInfo) == null || TextUtils.isEmpty(tLRPC$UserFull.about))) {
                return false;
            }
            boolean z = view instanceof AboutLinkCell;
            if (z && ((AboutLinkCell) view).onClick()) {
                return false;
            }
            AlertDialog.Builder builder3 = new AlertDialog.Builder((Context) getParentActivity());
            builder3.setItems(new CharSequence[]{LocaleController.getString("Copy", NUM)}, new ProfileActivity$$ExternalSyntheticLambda6(this, i));
            showDialog(builder3.create());
            return !z;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processOnClickOrPress$19(String str, DialogInterface dialogInterface, int i) {
        String str2;
        if (i == 0) {
            try {
                ClipboardManager clipboardManager = (ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard");
                if (this.userId != 0) {
                    str2 = "@" + str;
                    BulletinFactory.of(this).createCopyBulletin(LocaleController.getString("UsernameCopied", NUM)).show();
                } else {
                    str2 = "https://" + MessagesController.getInstance(UserConfig.selectedAccount).linkPrefix + "/" + str;
                    BulletinFactory.of(this).createCopyBulletin(LocaleController.getString("LinkCopied", NUM)).show();
                }
                clipboardManager.setPrimaryClip(ClipData.newPlainText("label", str2));
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processOnClickOrPress$20(ArrayList arrayList, TLRPC$User tLRPC$User, DialogInterface dialogInterface, int i) {
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
                    BulletinFactory.of(this).createCopyBulletin(LocaleController.getString("PhoneCopied", NUM)).show();
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
    public /* synthetic */ void lambda$processOnClickOrPress$21(int i, DialogInterface dialogInterface, int i2) {
        try {
            String str = null;
            if (i == this.locationRow) {
                TLRPC$ChatFull tLRPC$ChatFull = this.chatInfo;
                if (tLRPC$ChatFull != null) {
                    TLRPC$ChannelLocation tLRPC$ChannelLocation = tLRPC$ChatFull.location;
                    if (tLRPC$ChannelLocation instanceof TLRPC$TL_channelLocation) {
                        str = ((TLRPC$TL_channelLocation) tLRPC$ChannelLocation).address;
                    }
                }
            } else if (i == this.channelInfoRow) {
                TLRPC$ChatFull tLRPC$ChatFull2 = this.chatInfo;
                if (tLRPC$ChatFull2 != null) {
                    str = tLRPC$ChatFull2.about;
                }
            } else {
                TLRPC$UserFull tLRPC$UserFull = this.userInfo;
                if (tLRPC$UserFull != null) {
                    str = tLRPC$UserFull.about;
                }
            }
            if (!TextUtils.isEmpty(str)) {
                AndroidUtilities.addToClipboard(str);
                if (i == this.bioRow) {
                    BulletinFactory.of(this).createCopyBulletin(LocaleController.getString("BioCopied", NUM)).show();
                } else {
                    BulletinFactory.of(this).createCopyBulletin(LocaleController.getString("TextCopied", NUM)).show();
                }
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public void leaveChatPressed() {
        AlertsCreator.createClearOrDeleteDialogAlert(this, false, this.currentChat, (TLRPC$User) null, false, true, new ProfileActivity$$ExternalSyntheticLambda29(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$leaveChatPressed$22(boolean z) {
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
            getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tLRPC$TL_channels_getParticipants, new ProfileActivity$$ExternalSyntheticLambda32(this, tLRPC$TL_channels_getParticipants, i2)), this.classGuid);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getChannelParticipants$24(TLRPC$TL_channels_getParticipants tLRPC$TL_channels_getParticipants, int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new ProfileActivity$$ExternalSyntheticLambda24(this, tLRPC$TL_error, tLObject, tLRPC$TL_channels_getParticipants), (long) i);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getChannelParticipants$23(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, TLRPC$TL_channels_getParticipants tLRPC$TL_channels_getParticipants) {
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
            ActionBar actionBar = this.actionBar;
            Property<ActionBar, Float> property13 = this.ACTIONBAR_HEADER_PROGRESS;
            float[] fArr13 = new float[1];
            fArr13[0] = z ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(actionBar, property13, fArr13));
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
                                ProfileActivity.this.callItem.setVisibility(4);
                            }
                            if (ProfileActivity.this.videoCallItemVisible) {
                                ProfileActivity.this.videoCallItem.setVisibility(4);
                            }
                            if (ProfileActivity.this.editItemVisible) {
                                ProfileActivity.this.editItem.setVisibility(4);
                            }
                            ProfileActivity.this.otherItem.setVisibility(4);
                        } else {
                            if (ProfileActivity.this.sharedMediaLayout.isSearchItemVisible()) {
                                searchItem2.setVisibility(0);
                            }
                            ProfileActivity.this.sharedMediaLayout.photoVideoOptionsItem.setVisibility(4);
                            AnimatorSet unused = ProfileActivity.this.headerShadowAnimatorSet = new AnimatorSet();
                            AnimatorSet access$15400 = ProfileActivity.this.headerShadowAnimatorSet;
                            ProfileActivity profileActivity = ProfileActivity.this;
                            access$15400.playTogether(new Animator[]{ObjectAnimator.ofFloat(profileActivity, profileActivity.HEADER_SHADOW, new float[]{1.0f})});
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
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needCheckSystemBarColors, new Object[0]);
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
        groupCreateActivity.setDelegate((GroupCreateActivity.ContactsAddActivityDelegate) new ProfileActivity$$ExternalSyntheticLambda37(this));
        presentFragment(groupCreateActivity);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$openAddMember$25(ArrayList arrayList, int i) {
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
                    this.mediaCounterTextView.setText(LocaleController.formatPluralString("Media", lastMediaCount[0]));
                } else if (this.sharedMediaLayout.getPhotosVideosTypeFilter() == 1 || lastMediaCount[7] == 0) {
                    this.mediaCounterTextView.setText(LocaleController.formatPluralString("Photos", lastMediaCount[6]));
                } else if (this.sharedMediaLayout.getPhotosVideosTypeFilter() == 2 || lastMediaCount[6] == 0) {
                    this.mediaCounterTextView.setText(LocaleController.formatPluralString("Videos", lastMediaCount[7]));
                } else {
                    this.mediaCounterTextView.setText(String.format("%s, %s", new Object[]{LocaleController.formatPluralString("Photos", lastMediaCount[6]), LocaleController.formatPluralString("Videos", lastMediaCount[7])}));
                }
            } else if (closestTab == 1) {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("Files", lastMediaCount[1]));
            } else if (closestTab == 2) {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("Voice", lastMediaCount[2]));
            } else if (closestTab == 3) {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("Links", lastMediaCount[3]));
            } else if (closestTab == 4) {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("MusicFiles", lastMediaCount[4]));
            } else if (closestTab == 5) {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("GIFs", lastMediaCount[5]));
            } else if (closestTab == 6) {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("CommonGroups", this.userInfo.common_chats_count));
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
                        boolean z3 = min > 0.5f;
                        if (z3 != this.isQrItemVisible) {
                            this.isQrItemVisible = z3;
                            AnimatorSet animatorSet3 = this.qrItemAnimation;
                            if (animatorSet3 != null) {
                                animatorSet3.cancel();
                                this.qrItemAnimation = null;
                            }
                            if (z) {
                                AnimatorSet animatorSet4 = new AnimatorSet();
                                this.qrItemAnimation = animatorSet4;
                                if (z3) {
                                    animatorSet4.setInterpolator(new DecelerateInterpolator());
                                    this.qrItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.qrItem, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.qrItem, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.avatarsViewPagerIndicatorView, View.TRANSLATION_X, new float[]{(float) (-AndroidUtilities.dp(48.0f))})});
                                } else {
                                    animatorSet4.setInterpolator(new AccelerateInterpolator());
                                    this.qrItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.qrItem, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.qrItem, View.SCALE_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.avatarsViewPagerIndicatorView, View.TRANSLATION_X, new float[]{0.0f})});
                                }
                                this.qrItemAnimation.setDuration(150);
                                this.qrItemAnimation.addListener(new AnimatorListenerAdapter() {
                                    public void onAnimationEnd(Animator animator) {
                                        AnimatorSet unused = ProfileActivity.this.qrItemAnimation = null;
                                    }
                                });
                                this.qrItemAnimation.start();
                            } else {
                                this.qrItem.setAlpha(z3 ? 1.0f : 0.0f);
                                float dp = ((float) AndroidUtilities.dp(48.0f)) * this.qrItem.getAlpha();
                                this.qrItem.setTranslationX(dp);
                                this.avatarsViewPagerIndicatorView.setTranslationX(dp - ((float) AndroidUtilities.dp(48.0f)));
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
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needCheckSystemBarColors, new Object[0]);
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
                        this.topView.setBackgroundColor(Theme.getColor("avatar_backgroundActionBarBlue"));
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
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needCheckSystemBarColors, new Object[0]);
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
                this.actionBar.setItemsColor(ColorUtils.blendARGB(Theme.getColor("actionBarDefaultIcon"), -1, this.animationProgress), false);
                ScamDrawable scamDrawable2 = this.scamDrawable;
                if (scamDrawable2 != null) {
                    scamDrawable2.setColor(ColorUtils.blendARGB(Theme.getColor("avatar_subtitleInProfileBlue"), Color.argb(179, 255, 255, 255), this.animationProgress));
                }
                Drawable drawable = this.lockIconDrawable;
                if (drawable != null) {
                    drawable.setColorFilter(ColorUtils.blendARGB(Theme.getColor("chat_lockIcon"), -1, this.animationProgress), PorterDuff.Mode.MULTIPLY);
                }
                CrossfadeDrawable crossfadeDrawable = this.verifiedCrossfadeDrawable;
                if (crossfadeDrawable != null) {
                    crossfadeDrawable.setProgress(this.animationProgress);
                    this.nameTextView[1].invalidate();
                }
                FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.avatarContainer.getLayoutParams();
                int lerp3 = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(42.0f), (this.extraHeight + ((float) currentActionBarHeight)) / this.avatarScale, this.animationProgress);
                layoutParams3.height = lerp3;
                layoutParams3.width = lerp3;
                layoutParams3.leftMargin = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(64.0f), 0.0f, this.animationProgress);
                this.avatarContainer.requestLayout();
            } else if (this.extraHeight <= ((float) AndroidUtilities.dp(88.0f))) {
                this.avatarScale = ((18.0f * min) + 42.0f) / 42.0f;
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
            str = "g";
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
                    updateProfileData();
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
                    updateProfileData();
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
                updateProfileData();
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
    public /* synthetic */ void lambda$didReceivedNotification$26(Object[] objArr) {
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
            setParentActivityTitle(LocaleController.getString("Settings", NUM));
        }
        updateProfileData();
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
            i = AvatarDrawable.getProfileBackColorForId((this.userId != 0 || (ChatObject.isChannel(this.chatId, this.currentAccount) && !this.currentChat.megagroup)) ? 5 : this.chatId);
        }
        int i3 = this.actionBarAnimationColorFrom;
        if (i3 == 0) {
            i3 = Theme.getColor("actionBarDefault");
        }
        this.topView.setBackgroundColor(ColorUtils.blendARGB(SharedConfig.chatBlurEnabled() ? ColorUtils.setAlphaComponent(i3, 0) : i3, i, f));
        this.timerDrawable.setBackgroundColor(ColorUtils.blendARGB(i3, i, f));
        this.actionBar.setItemsColor(ColorUtils.blendARGB(Theme.getColor("actionBarDefaultIcon"), AvatarDrawable.getIconColorForId((this.userId != 0 || (ChatObject.isChannel(this.chatId, this.currentAccount) && !this.currentChat.megagroup)) ? 5 : this.chatId), f), false);
        int color = Theme.getColor("profile_title");
        int color2 = Theme.getColor("actionBarDefaultTitle");
        for (int i4 = 0; i4 < 2; i4++) {
            SimpleTextView[] simpleTextViewArr = this.nameTextView;
            if (!(simpleTextViewArr[i4] == null || (i4 == 1 && this.playProfileAnimation == 2))) {
                simpleTextViewArr[i4].setTextColor(ColorUtils.blendARGB(color2, color, f));
            }
        }
        if (this.isOnline[0]) {
            i2 = Theme.getColor("profile_status");
        } else {
            if (this.userId == 0 && (!ChatObject.isChannel(this.chatId, this.currentAccount) || this.currentChat.megagroup)) {
                j = this.chatId;
            }
            i2 = AvatarDrawable.getProfileTextColorForId(j);
        }
        int color3 = Theme.getColor(this.isOnline[0] ? "chat_status" : "actionBarDefaultSubtitle");
        for (int i5 = 0; i5 < 2; i5++) {
            SimpleTextView[] simpleTextViewArr2 = this.onlineTextView;
            if (!(simpleTextViewArr2[i5] == null || (i5 == 1 && this.playProfileAnimation == 2))) {
                simpleTextViewArr2[i5].setTextColor(ColorUtils.blendARGB(color3, i2, f));
            }
        }
        this.extraHeight = this.initialAnimationExtraHeight * f;
        long j2 = this.userId;
        if (j2 == 0) {
            j2 = this.chatId;
        }
        int profileColorForId = AvatarDrawable.getProfileColorForId(j2);
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
        this.fragmentView.invalidate();
        AboutLinkCell aboutLinkCell2 = this.aboutLinkCell;
        if (aboutLinkCell2 != null) {
            aboutLinkCell2.invalidate();
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x04b7  */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x0310  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.animation.AnimatorSet onCustomTransitionAnimation(boolean r17, java.lang.Runnable r18) {
        /*
            r16 = this;
            r0 = r16
            int r1 = r0.playProfileAnimation
            r2 = 0
            if (r1 == 0) goto L_0x0515
            boolean r1 = r0.allowProfileAnimation
            if (r1 == 0) goto L_0x0515
            boolean r1 = r0.isPulledDown
            if (r1 != 0) goto L_0x0515
            boolean r1 = r0.disableProfileAnimation
            if (r1 != 0) goto L_0x0515
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
            if (r7 != 0) goto L_0x0078
            org.telegram.ui.ActionBar.ActionBarMenuItem r7 = r0.animatingItem
            if (r7 != 0) goto L_0x0078
            r7 = 2131165510(0x7var_, float:1.794524E38)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r5.addItem((int) r6, (int) r7)
            r0.animatingItem = r5
        L_0x0078:
            r5 = 0
            java.lang.String r7 = "animationProgress"
            r8 = 1045220557(0x3e4ccccd, float:0.2)
            r9 = 0
            r10 = 0
            r11 = 1
            if (r17 == 0) goto L_0x033f
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
            if (r12 == r4) goto L_0x0117
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
            if (r13 >= 0) goto L_0x0103
            float r12 = r12 / r14
            double r12 = (double) r12
            double r12 = java.lang.Math.ceil(r12)
            int r12 = (int) r12
            r15.width = r12
            goto L_0x0106
        L_0x0103:
            r12 = -2
            r15.width = r12
        L_0x0106:
            org.telegram.ui.ActionBar.SimpleTextView[] r12 = r0.nameTextView
            r12 = r12[r11]
            r12.setLayoutParams(r15)
            r12 = 1118830592(0x42b00000, float:88.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            r0.initialAnimationExtraHeight = r12
            goto L_0x013b
        L_0x0117:
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
        L_0x013b:
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
            if (r7 == 0) goto L_0x019a
            java.lang.Object r7 = r7.getTag()
            if (r7 != 0) goto L_0x019a
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
        L_0x019a:
            int r7 = r0.playProfileAnimation
            if (r7 != r4) goto L_0x01d2
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
        L_0x01d2:
            r7 = 0
        L_0x01d3:
            if (r7 >= r4) goto L_0x01fc
            org.telegram.ui.ActionBar.SimpleTextView[] r8 = r0.nameTextView
            r8 = r8[r7]
            if (r7 != 0) goto L_0x01de
            r13 = 1065353216(0x3var_, float:1.0)
            goto L_0x01df
        L_0x01de:
            r13 = 0
        L_0x01df:
            r8.setAlpha(r13)
            org.telegram.ui.ActionBar.SimpleTextView[] r8 = r0.nameTextView
            r8 = r8[r7]
            android.util.Property r13 = android.view.View.ALPHA
            float[] r14 = new float[r11]
            if (r7 != 0) goto L_0x01ee
            r15 = 0
            goto L_0x01f0
        L_0x01ee:
            r15 = 1065353216(0x3var_, float:1.0)
        L_0x01f0:
            r14[r10] = r15
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r8, r13, r14)
            r12.add(r8)
            int r7 = r7 + 1
            goto L_0x01d3
        L_0x01fc:
            android.widget.ImageView r7 = r0.timeItem
            java.lang.Object r7 = r7.getTag()
            if (r7 == 0) goto L_0x0234
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
        L_0x0234:
            org.telegram.ui.ActionBar.ActionBarMenuItem r7 = r0.animatingItem
            if (r7 == 0) goto L_0x024a
            r7.setAlpha(r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r7 = r0.animatingItem
            android.util.Property r8 = android.view.View.ALPHA
            float[] r13 = new float[r11]
            r13[r10] = r9
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r13)
            r12.add(r7)
        L_0x024a:
            boolean r7 = r0.callItemVisible
            if (r7 == 0) goto L_0x0268
            long r7 = r0.chatId
            int r13 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
            if (r13 == 0) goto L_0x0268
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.callItem
            r5.setAlpha(r9)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.callItem
            android.util.Property r6 = android.view.View.ALPHA
            float[] r7 = new float[r11]
            r7[r10] = r3
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r6, r7)
            r12.add(r5)
        L_0x0268:
            boolean r5 = r0.videoCallItemVisible
            if (r5 == 0) goto L_0x0280
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.videoCallItem
            r5.setAlpha(r9)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.videoCallItem
            android.util.Property r6 = android.view.View.ALPHA
            float[] r7 = new float[r11]
            r7[r10] = r3
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r6, r7)
            r12.add(r5)
        L_0x0280:
            boolean r5 = r0.editItemVisible
            if (r5 == 0) goto L_0x0298
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.editItem
            r5.setAlpha(r9)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.editItem
            android.util.Property r6 = android.view.View.ALPHA
            float[] r7 = new float[r11]
            r7[r10] = r3
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r6, r7)
            r12.add(r5)
        L_0x0298:
            android.widget.ImageView r5 = r0.ttlIconView
            java.lang.Object r5 = r5.getTag()
            if (r5 == 0) goto L_0x02b4
            android.widget.ImageView r5 = r0.ttlIconView
            r5.setAlpha(r9)
            android.widget.ImageView r5 = r0.ttlIconView
            android.util.Property r6 = android.view.View.ALPHA
            float[] r7 = new float[r11]
            r7[r10] = r3
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r6, r7)
            r12.add(r5)
        L_0x02b4:
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r0.parentLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r5 = r5.fragmentsStack
            int r5 = r5.size()
            if (r5 <= r11) goto L_0x02cd
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r0.parentLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = r2.fragmentsStack
            int r5 = r2.size()
            int r5 = r5 - r4
            java.lang.Object r2 = r2.get(r5)
            org.telegram.ui.ActionBar.BaseFragment r2 = (org.telegram.ui.ActionBar.BaseFragment) r2
        L_0x02cd:
            boolean r5 = r2 instanceof org.telegram.ui.ChatActivity
            if (r5 == 0) goto L_0x030d
            org.telegram.ui.ChatActivity r2 = (org.telegram.ui.ChatActivity) r2
            org.telegram.ui.Components.ChatAvatarContainer r2 = r2.getAvatarContainer()
            org.telegram.ui.ActionBar.SimpleTextView r5 = r2.getSubtitleTextView()
            android.graphics.drawable.Drawable r5 = r5.getLeftDrawable()
            if (r5 == 0) goto L_0x030d
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
            goto L_0x030e
        L_0x030d:
            r2 = 0
        L_0x030e:
            if (r2 != 0) goto L_0x033a
            r2 = 0
        L_0x0311:
            if (r2 >= r4) goto L_0x033a
            org.telegram.ui.ActionBar.SimpleTextView[] r5 = r0.onlineTextView
            r5 = r5[r2]
            if (r2 != 0) goto L_0x031c
            r6 = 1065353216(0x3var_, float:1.0)
            goto L_0x031d
        L_0x031c:
            r6 = 0
        L_0x031d:
            r5.setAlpha(r6)
            org.telegram.ui.ActionBar.SimpleTextView[] r5 = r0.onlineTextView
            r5 = r5[r2]
            android.util.Property r6 = android.view.View.ALPHA
            float[] r7 = new float[r11]
            if (r2 != 0) goto L_0x032c
            r8 = 0
            goto L_0x032e
        L_0x032c:
            r8 = 1065353216(0x3var_, float:1.0)
        L_0x032e:
            r7[r10] = r8
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r6, r7)
            r12.add(r5)
            int r2 = r2 + 1
            goto L_0x0311
        L_0x033a:
            r1.playTogether(r12)
            goto L_0x04d7
        L_0x033f:
            float r12 = r0.extraHeight
            r0.initialAnimationExtraHeight = r12
            java.util.ArrayList r12 = new java.util.ArrayList
            r12.<init>()
            float[] r13 = new float[r4]
            r13 = {NUM, 0} // fill-array
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r0, r7, r13)
            r12.add(r7)
            org.telegram.ui.Components.RLottieImageView r7 = r0.writeButton
            if (r7 == 0) goto L_0x0383
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
        L_0x0383:
            r7 = 0
        L_0x0384:
            if (r7 >= r4) goto L_0x03a0
            org.telegram.ui.ActionBar.SimpleTextView[] r8 = r0.nameTextView
            r8 = r8[r7]
            android.util.Property r13 = android.view.View.ALPHA
            float[] r14 = new float[r11]
            if (r7 != 0) goto L_0x0393
            r15 = 1065353216(0x3var_, float:1.0)
            goto L_0x0394
        L_0x0393:
            r15 = 0
        L_0x0394:
            r14[r10] = r15
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r8, r13, r14)
            r12.add(r8)
            int r7 = r7 + 1
            goto L_0x0384
        L_0x03a0:
            android.widget.ImageView r7 = r0.timeItem
            java.lang.Object r7 = r7.getTag()
            if (r7 == 0) goto L_0x03dd
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
        L_0x03dd:
            org.telegram.ui.ActionBar.ActionBarMenuItem r7 = r0.animatingItem
            if (r7 == 0) goto L_0x03f3
            r7.setAlpha(r9)
            org.telegram.ui.ActionBar.ActionBarMenuItem r7 = r0.animatingItem
            android.util.Property r8 = android.view.View.ALPHA
            float[] r13 = new float[r11]
            r13[r10] = r3
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r8, r13)
            r12.add(r7)
        L_0x03f3:
            boolean r7 = r0.callItemVisible
            if (r7 == 0) goto L_0x0411
            long r7 = r0.chatId
            int r13 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
            if (r13 == 0) goto L_0x0411
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.callItem
            r5.setAlpha(r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.callItem
            android.util.Property r6 = android.view.View.ALPHA
            float[] r7 = new float[r11]
            r7[r10] = r9
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r6, r7)
            r12.add(r5)
        L_0x0411:
            boolean r5 = r0.videoCallItemVisible
            if (r5 == 0) goto L_0x0429
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.videoCallItem
            r5.setAlpha(r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.videoCallItem
            android.util.Property r6 = android.view.View.ALPHA
            float[] r7 = new float[r11]
            r7[r10] = r9
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r6, r7)
            r12.add(r5)
        L_0x0429:
            boolean r5 = r0.editItemVisible
            if (r5 == 0) goto L_0x0441
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.editItem
            r5.setAlpha(r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.editItem
            android.util.Property r6 = android.view.View.ALPHA
            float[] r7 = new float[r11]
            r7[r10] = r9
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r6, r7)
            r12.add(r5)
        L_0x0441:
            android.widget.ImageView r5 = r0.ttlIconView
            if (r5 == 0) goto L_0x0458
            android.util.Property r6 = android.view.View.ALPHA
            float[] r7 = new float[r4]
            float r8 = r5.getAlpha()
            r7[r10] = r8
            r7[r11] = r9
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r6, r7)
            r12.add(r5)
        L_0x0458:
            org.telegram.ui.ActionBar.ActionBarLayout r5 = r0.parentLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r5 = r5.fragmentsStack
            int r5 = r5.size()
            if (r5 <= r11) goto L_0x0471
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r0.parentLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = r2.fragmentsStack
            int r5 = r2.size()
            int r5 = r5 - r4
            java.lang.Object r2 = r2.get(r5)
            org.telegram.ui.ActionBar.BaseFragment r2 = (org.telegram.ui.ActionBar.BaseFragment) r2
        L_0x0471:
            boolean r5 = r2 instanceof org.telegram.ui.ChatActivity
            if (r5 == 0) goto L_0x04b4
            org.telegram.ui.ChatActivity r2 = (org.telegram.ui.ChatActivity) r2
            org.telegram.ui.Components.ChatAvatarContainer r2 = r2.getAvatarContainer()
            org.telegram.ui.ActionBar.SimpleTextView r5 = r2.getSubtitleTextView()
            android.graphics.drawable.Drawable r5 = r5.getLeftDrawable()
            if (r5 == 0) goto L_0x04b4
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
            goto L_0x04b5
        L_0x04b4:
            r2 = 0
        L_0x04b5:
            if (r2 != 0) goto L_0x04d4
            r2 = 0
        L_0x04b8:
            if (r2 >= r4) goto L_0x04d4
            org.telegram.ui.ActionBar.SimpleTextView[] r5 = r0.onlineTextView
            r5 = r5[r2]
            android.util.Property r6 = android.view.View.ALPHA
            float[] r7 = new float[r11]
            if (r2 != 0) goto L_0x04c7
            r8 = 1065353216(0x3var_, float:1.0)
            goto L_0x04c8
        L_0x04c7:
            r8 = 0
        L_0x04c8:
            r7[r10] = r8
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r6, r7)
            r12.add(r5)
            int r2 = r2 + 1
            goto L_0x04b8
        L_0x04d4:
            r1.playTogether(r12)
        L_0x04d7:
            r0.profileTransitionInProgress = r11
            float[] r2 = new float[r4]
            r2 = {0, NUM} // fill-array
            android.animation.ValueAnimator r2 = android.animation.ValueAnimator.ofFloat(r2)
            org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda2 r3 = new org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda2
            r3.<init>(r0)
            r2.addUpdateListener(r3)
            android.animation.Animator[] r3 = new android.animation.Animator[r11]
            r3[r10] = r2
            r1.playTogether(r3)
            org.telegram.ui.ProfileActivity$33 r2 = new org.telegram.ui.ProfileActivity$33
            r3 = r18
            r2.<init>(r3)
            r1.addListener(r2)
            int r2 = r0.playProfileAnimation
            if (r2 != r4) goto L_0x0502
            org.telegram.ui.Components.CubicBezierInterpolator r2 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            goto L_0x0507
        L_0x0502:
            android.view.animation.DecelerateInterpolator r2 = new android.view.animation.DecelerateInterpolator
            r2.<init>()
        L_0x0507:
            r1.setInterpolator(r2)
            org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda18 r2 = new org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda18
            r2.<init>(r1)
            r3 = 50
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2, r3)
            return r1
        L_0x0515:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.onCustomTransitionAnimation(boolean, java.lang.Runnable):android.animation.AnimatorSet");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCustomTransitionAnimation$27(ValueAnimator valueAnimator) {
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
                Collections.sort(this.sortedUsers, new ProfileActivity$$ExternalSyntheticLambda28(this, currentTime));
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
    public /* synthetic */ int lambda$updateOnlineCount$28(int r5, java.lang.Integer r6, java.lang.Integer r7) {
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.lambda$updateOnlineCount$28(int, java.lang.Integer, java.lang.Integer):int");
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
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x00db, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated) == false) goto L_0x00dd;
     */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x030f  */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x00ab  */
    /* JADX WARNING: Removed duplicated region for block: B:255:0x056d  */
    /* JADX WARNING: Removed duplicated region for block: B:258:0x0579  */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x058b  */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x05a8  */
    /* JADX WARNING: Removed duplicated region for block: B:277:? A[RETURN, SYNTHETIC] */
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
            r10.privacyRow = r2
            r10.dataRow = r2
            r10.chatRow = r2
            r10.filtersRow = r2
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
            if (r3 == 0) goto L_0x00a2
            int[] r3 = r3.getLastMediaCount()
            r5 = 0
        L_0x0096:
            int r6 = r3.length
            if (r5 >= r6) goto L_0x00a2
            r6 = r3[r5]
            if (r6 <= 0) goto L_0x009f
            r3 = 1
            goto L_0x00a3
        L_0x009f:
            int r5 = r5 + 1
            goto L_0x0096
        L_0x00a2:
            r3 = 0
        L_0x00a3:
            long r5 = r10.userId
            r7 = 0
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 == 0) goto L_0x030f
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 == 0) goto L_0x00b7
            int r5 = r10.rowCount
            int r6 = r5 + 1
            r10.rowCount = r6
            r10.emptyRow = r5
        L_0x00b7:
            org.telegram.messenger.MessagesController r5 = r10.getMessagesController()
            long r6 = r10.userId
            java.lang.Long r6 = java.lang.Long.valueOf(r6)
            org.telegram.tgnet.TLRPC$User r5 = r5.getUser(r6)
            boolean r6 = org.telegram.messenger.UserObject.isUserSelf(r5)
            if (r6 == 0) goto L_0x01f1
            org.telegram.tgnet.TLRPC$FileLocation r3 = r10.avatarBig
            if (r3 != 0) goto L_0x00f5
            org.telegram.tgnet.TLRPC$UserProfilePhoto r3 = r5.photo
            if (r3 == 0) goto L_0x00dd
            org.telegram.tgnet.TLRPC$FileLocation r3 = r3.photo_big
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_fileLocation_layer97
            if (r4 != 0) goto L_0x00f5
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated
            if (r3 != 0) goto L_0x00f5
        L_0x00dd:
            org.telegram.ui.Components.ProfileGalleryView r3 = r10.avatarsViewPager
            if (r3 == 0) goto L_0x00e7
            int r3 = r3.getRealCount()
            if (r3 != 0) goto L_0x00f5
        L_0x00e7:
            int r3 = r10.rowCount
            int r4 = r3 + 1
            r10.rowCount = r4
            r10.setAvatarRow = r3
            int r3 = r4 + 1
            r10.rowCount = r3
            r10.setAvatarSectionRow = r4
        L_0x00f5:
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
            if (r4 == 0) goto L_0x0131
            int r4 = r10.rowCount
            int r5 = r4 + 1
            r10.rowCount = r5
            r10.phoneSuggestionRow = r4
            int r4 = r5 + 1
            r10.rowCount = r4
            r10.phoneSuggestionSectionRow = r5
        L_0x0131:
            java.lang.String r4 = "VALIDATE_PASSWORD"
            boolean r3 = r3.contains(r4)
            if (r3 == 0) goto L_0x0147
            int r3 = r10.rowCount
            int r4 = r3 + 1
            r10.rowCount = r4
            r10.passwordSuggestionRow = r3
            int r3 = r4 + 1
            r10.rowCount = r3
            r10.passwordSuggestionSectionRow = r4
        L_0x0147:
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
            org.telegram.messenger.MessagesController r3 = r10.getMessagesController()
            boolean r3 = r3.filtersEnabled
            if (r3 != 0) goto L_0x017b
            org.telegram.messenger.MessagesController r3 = r10.getMessagesController()
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r3 = r3.dialogFilters
            boolean r3 = r3.isEmpty()
            if (r3 != 0) goto L_0x0183
        L_0x017b:
            int r3 = r10.rowCount
            int r4 = r3 + 1
            r10.rowCount = r4
            r10.filtersRow = r3
        L_0x0183:
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
            int r3 = r4 + 1
            r10.rowCount = r3
            r10.helpHeaderRow = r4
            int r4 = r3 + 1
            r10.rowCount = r4
            r10.questionRow = r3
            int r3 = r4 + 1
            r10.rowCount = r3
            r10.faqRow = r4
            int r4 = r3 + 1
            r10.rowCount = r4
            r10.policyRow = r3
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r3 != 0) goto L_0x01b7
            boolean r3 = org.telegram.messenger.BuildVars.DEBUG_PRIVATE_VERSION
            if (r3 == 0) goto L_0x01c3
        L_0x01b7:
            int r3 = r4 + 1
            r10.rowCount = r3
            r10.helpSectionCell = r4
            int r4 = r3 + 1
            r10.rowCount = r4
            r10.debugHeaderRow = r3
        L_0x01c3:
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r3 == 0) goto L_0x01db
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
        L_0x01db:
            boolean r3 = org.telegram.messenger.BuildVars.DEBUG_PRIVATE_VERSION
            if (r3 == 0) goto L_0x01e7
            int r3 = r10.rowCount
            int r4 = r3 + 1
            r10.rowCount = r4
            r10.switchBackendRow = r3
        L_0x01e7:
            int r3 = r10.rowCount
            int r4 = r3 + 1
            r10.rowCount = r4
            r10.versionRow = r3
            goto L_0x0569
        L_0x01f1:
            org.telegram.tgnet.TLRPC$UserFull r6 = r10.userInfo
            if (r6 == 0) goto L_0x01fd
            java.lang.String r6 = r6.about
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 == 0) goto L_0x0207
        L_0x01fd:
            if (r5 == 0) goto L_0x0209
            java.lang.String r6 = r5.username
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 != 0) goto L_0x0209
        L_0x0207:
            r6 = 1
            goto L_0x020a
        L_0x0209:
            r6 = 0
        L_0x020a:
            if (r5 == 0) goto L_0x0215
            java.lang.String r7 = r5.phone
            boolean r7 = android.text.TextUtils.isEmpty(r7)
            if (r7 != 0) goto L_0x0215
            goto L_0x0216
        L_0x0215:
            r4 = 0
        L_0x0216:
            int r7 = r10.rowCount
            int r8 = r7 + 1
            r10.rowCount = r8
            r10.infoHeaderRow = r7
            boolean r7 = r10.isBot
            if (r7 != 0) goto L_0x022c
            if (r4 != 0) goto L_0x0226
            if (r6 != 0) goto L_0x022c
        L_0x0226:
            int r4 = r8 + 1
            r10.rowCount = r4
            r10.phoneRow = r8
        L_0x022c:
            org.telegram.tgnet.TLRPC$UserFull r4 = r10.userInfo
            if (r4 == 0) goto L_0x0240
            java.lang.String r4 = r4.about
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x0240
            int r4 = r10.rowCount
            int r6 = r4 + 1
            r10.rowCount = r6
            r10.userInfoRow = r4
        L_0x0240:
            if (r5 == 0) goto L_0x0252
            java.lang.String r4 = r5.username
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x0252
            int r4 = r10.rowCount
            int r6 = r4 + 1
            r10.rowCount = r6
            r10.usernameRow = r4
        L_0x0252:
            int r4 = r10.phoneRow
            if (r4 != r2) goto L_0x025e
            int r4 = r10.userInfoRow
            if (r4 != r2) goto L_0x025e
            int r4 = r10.usernameRow
            if (r4 == r2) goto L_0x0266
        L_0x025e:
            int r4 = r10.rowCount
            int r6 = r4 + 1
            r10.rowCount = r6
            r10.notificationsDividerRow = r4
        L_0x0266:
            long r6 = r10.userId
            org.telegram.messenger.UserConfig r4 = r10.getUserConfig()
            long r8 = r4.getClientUserId()
            int r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r4 == 0) goto L_0x027c
            int r4 = r10.rowCount
            int r6 = r4 + 1
            r10.rowCount = r6
            r10.notificationsRow = r4
        L_0x027c:
            int r4 = r10.rowCount
            int r6 = r4 + 1
            r10.rowCount = r6
            r10.infoSectionRow = r4
            org.telegram.tgnet.TLRPC$EncryptedChat r4 = r10.currentEncryptedChat
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChat
            if (r7 == 0) goto L_0x029c
            int r7 = r6 + 1
            r10.rowCount = r7
            r10.settingsTimerRow = r6
            int r6 = r7 + 1
            r10.rowCount = r6
            r10.settingsKeyRow = r7
            int r7 = r6 + 1
            r10.rowCount = r7
            r10.secretSettingsSectionRow = r6
        L_0x029c:
            if (r5 == 0) goto L_0x02c4
            boolean r6 = r10.isBot
            if (r6 != 0) goto L_0x02c4
            if (r4 != 0) goto L_0x02c4
            long r6 = r5.id
            org.telegram.messenger.UserConfig r4 = r10.getUserConfig()
            long r8 = r4.getClientUserId()
            int r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r4 == 0) goto L_0x02c4
            boolean r4 = r10.userBlocked
            if (r4 == 0) goto L_0x02c4
            int r4 = r10.rowCount
            int r6 = r4 + 1
            r10.rowCount = r6
            r10.unblockRow = r4
            int r4 = r6 + 1
            r10.rowCount = r4
            r10.lastSectionRow = r6
        L_0x02c4:
            if (r5 == 0) goto L_0x02dc
            boolean r4 = r10.isBot
            if (r4 == 0) goto L_0x02dc
            boolean r4 = r5.bot_nochats
            if (r4 != 0) goto L_0x02dc
            int r4 = r10.rowCount
            int r5 = r4 + 1
            r10.rowCount = r5
            r10.addToGroupButtonRow = r4
            int r4 = r5 + 1
            r10.rowCount = r4
            r10.addToGroupInfoRow = r5
        L_0x02dc:
            if (r3 != 0) goto L_0x0305
            org.telegram.tgnet.TLRPC$UserFull r3 = r10.userInfo
            if (r3 == 0) goto L_0x02e7
            int r3 = r3.common_chats_count
            if (r3 == 0) goto L_0x02e7
            goto L_0x0305
        L_0x02e7:
            int r3 = r10.lastSectionRow
            if (r3 != r2) goto L_0x0569
            boolean r3 = r10.needSendMessage
            if (r3 == 0) goto L_0x0569
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
            goto L_0x0569
        L_0x0305:
            int r3 = r10.rowCount
            int r4 = r3 + 1
            r10.rowCount = r4
            r10.sharedMediaRow = r3
            goto L_0x0569
        L_0x030f:
            long r5 = r10.chatId
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 == 0) goto L_0x0569
            org.telegram.tgnet.TLRPC$ChatFull r5 = r10.chatInfo
            if (r5 == 0) goto L_0x0329
            java.lang.String r5 = r5.about
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 == 0) goto L_0x0333
            org.telegram.tgnet.TLRPC$ChatFull r5 = r10.chatInfo
            org.telegram.tgnet.TLRPC$ChannelLocation r5 = r5.location
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC$TL_channelLocation
            if (r5 != 0) goto L_0x0333
        L_0x0329:
            org.telegram.tgnet.TLRPC$Chat r5 = r10.currentChat
            java.lang.String r5 = r5.username
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 != 0) goto L_0x0395
        L_0x0333:
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 == 0) goto L_0x0357
            org.telegram.tgnet.TLRPC$Chat r5 = r10.currentChat
            boolean r5 = org.telegram.messenger.ChatObject.isChannel(r5)
            if (r5 == 0) goto L_0x0357
            org.telegram.tgnet.TLRPC$ChatFull r5 = r10.chatInfo
            if (r5 == 0) goto L_0x0357
            org.telegram.tgnet.TLRPC$Chat r6 = r10.currentChat
            boolean r6 = r6.megagroup
            if (r6 != 0) goto L_0x0357
            long r5 = r5.linked_chat_id
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 == 0) goto L_0x0357
            int r5 = r10.rowCount
            int r6 = r5 + 1
            r10.rowCount = r6
            r10.emptyRow = r5
        L_0x0357:
            int r5 = r10.rowCount
            int r6 = r5 + 1
            r10.rowCount = r6
            r10.infoHeaderRow = r5
            org.telegram.tgnet.TLRPC$ChatFull r5 = r10.chatInfo
            if (r5 == 0) goto L_0x0383
            java.lang.String r5 = r5.about
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 != 0) goto L_0x0373
            int r5 = r10.rowCount
            int r6 = r5 + 1
            r10.rowCount = r6
            r10.channelInfoRow = r5
        L_0x0373:
            org.telegram.tgnet.TLRPC$ChatFull r5 = r10.chatInfo
            org.telegram.tgnet.TLRPC$ChannelLocation r5 = r5.location
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC$TL_channelLocation
            if (r5 == 0) goto L_0x0383
            int r5 = r10.rowCount
            int r6 = r5 + 1
            r10.rowCount = r6
            r10.locationRow = r5
        L_0x0383:
            org.telegram.tgnet.TLRPC$Chat r5 = r10.currentChat
            java.lang.String r5 = r5.username
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 != 0) goto L_0x0395
            int r5 = r10.rowCount
            int r6 = r5 + 1
            r10.rowCount = r6
            r10.usernameRow = r5
        L_0x0395:
            int r5 = r10.infoHeaderRow
            if (r5 == r2) goto L_0x03a1
            int r5 = r10.rowCount
            int r6 = r5 + 1
            r10.rowCount = r6
            r10.notificationsDividerRow = r5
        L_0x03a1:
            int r5 = r10.rowCount
            int r6 = r5 + 1
            r10.rowCount = r6
            r10.notificationsRow = r5
            int r5 = r6 + 1
            r10.rowCount = r5
            r10.infoSectionRow = r6
            org.telegram.tgnet.TLRPC$Chat r5 = r10.currentChat
            boolean r5 = org.telegram.messenger.ChatObject.isChannel(r5)
            if (r5 == 0) goto L_0x03ff
            org.telegram.tgnet.TLRPC$Chat r5 = r10.currentChat
            boolean r6 = r5.megagroup
            if (r6 != 0) goto L_0x03ff
            org.telegram.tgnet.TLRPC$ChatFull r6 = r10.chatInfo
            if (r6 == 0) goto L_0x03ff
            boolean r5 = r5.creator
            if (r5 != 0) goto L_0x03c9
            boolean r5 = r6.can_view_participants
            if (r5 == 0) goto L_0x03ff
        L_0x03c9:
            int r5 = r10.rowCount
            int r7 = r5 + 1
            r10.rowCount = r7
            r10.membersHeaderRow = r5
            int r5 = r7 + 1
            r10.rowCount = r5
            r10.subscribersRow = r7
            int r7 = r6.requests_pending
            if (r7 <= 0) goto L_0x03e1
            int r7 = r5 + 1
            r10.rowCount = r7
            r10.subscribersRequestsRow = r5
        L_0x03e1:
            int r5 = r10.rowCount
            int r7 = r5 + 1
            r10.rowCount = r7
            r10.administratorsRow = r5
            int r5 = r6.banned_count
            if (r5 != 0) goto L_0x03f1
            int r5 = r6.kicked_count
            if (r5 == 0) goto L_0x03f7
        L_0x03f1:
            int r5 = r7 + 1
            r10.rowCount = r5
            r10.blockedUsersRow = r7
        L_0x03f7:
            int r5 = r10.rowCount
            int r6 = r5 + 1
            r10.rowCount = r6
            r10.membersSectionRow = r5
        L_0x03ff:
            org.telegram.tgnet.TLRPC$Chat r5 = r10.currentChat
            boolean r5 = org.telegram.messenger.ChatObject.isChannel(r5)
            r6 = 5
            r7 = 0
            if (r5 == 0) goto L_0x04d4
            org.telegram.tgnet.TLRPC$ChatFull r5 = r10.chatInfo
            if (r5 == 0) goto L_0x04b6
            org.telegram.tgnet.TLRPC$Chat r8 = r10.currentChat
            boolean r8 = r8.megagroup
            if (r8 == 0) goto L_0x04b6
            org.telegram.tgnet.TLRPC$ChatParticipants r5 = r5.participants
            if (r5 == 0) goto L_0x04b6
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r5 = r5.participants
            boolean r5 = r5.isEmpty()
            if (r5 != 0) goto L_0x04b6
            org.telegram.tgnet.TLRPC$Chat r5 = r10.currentChat
            boolean r5 = org.telegram.messenger.ChatObject.isNotInChat(r5)
            if (r5 != 0) goto L_0x0443
            org.telegram.tgnet.TLRPC$Chat r5 = r10.currentChat
            boolean r5 = org.telegram.messenger.ChatObject.canAddUsers(r5)
            if (r5 == 0) goto L_0x0443
            org.telegram.tgnet.TLRPC$ChatFull r5 = r10.chatInfo
            int r5 = r5.participants_count
            org.telegram.messenger.MessagesController r8 = r10.getMessagesController()
            int r8 = r8.maxMegagroupCount
            if (r5 >= r8) goto L_0x0443
            int r5 = r10.rowCount
            int r8 = r5 + 1
            r10.rowCount = r8
            r10.addMemberRow = r5
        L_0x0443:
            org.telegram.tgnet.TLRPC$ChatFull r5 = r10.chatInfo
            org.telegram.tgnet.TLRPC$ChatParticipants r5 = r5.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r5 = r5.participants
            int r5 = r5.size()
            r8 = 2
            if (r5 <= r6) goto L_0x0456
            if (r3 == 0) goto L_0x0456
            int r6 = r10.usersForceShowingIn
            if (r6 != r4) goto L_0x0493
        L_0x0456:
            int r6 = r10.usersForceShowingIn
            if (r6 == r8) goto L_0x0493
            int r6 = r10.addMemberRow
            if (r6 != r2) goto L_0x0466
            int r6 = r10.rowCount
            int r8 = r6 + 1
            r10.rowCount = r8
            r10.membersHeaderRow = r6
        L_0x0466:
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
            if (r5 == 0) goto L_0x0489
            java.util.ArrayList<java.lang.Integer> r6 = r10.visibleSortedUsers
            r6.addAll(r5)
        L_0x0489:
            r10.usersForceShowingIn = r4
            org.telegram.ui.Components.SharedMediaLayout r4 = r10.sharedMediaLayout
            if (r4 == 0) goto L_0x04b6
            r4.setChatUsers(r7, r7)
            goto L_0x04b6
        L_0x0493:
            int r4 = r10.addMemberRow
            if (r4 == r2) goto L_0x049f
            int r4 = r10.rowCount
            int r5 = r4 + 1
            r10.rowCount = r5
            r10.membersSectionRow = r4
        L_0x049f:
            org.telegram.ui.Components.SharedMediaLayout r4 = r10.sharedMediaLayout
            if (r4 == 0) goto L_0x04b6
            java.util.ArrayList<java.lang.Integer> r4 = r10.sortedUsers
            boolean r4 = r4.isEmpty()
            if (r4 != 0) goto L_0x04ad
            r10.usersForceShowingIn = r8
        L_0x04ad:
            org.telegram.ui.Components.SharedMediaLayout r4 = r10.sharedMediaLayout
            java.util.ArrayList<java.lang.Integer> r5 = r10.sortedUsers
            org.telegram.tgnet.TLRPC$ChatFull r6 = r10.chatInfo
            r4.setChatUsers(r5, r6)
        L_0x04b6:
            int r4 = r10.lastSectionRow
            if (r4 != r2) goto L_0x055f
            org.telegram.tgnet.TLRPC$Chat r4 = r10.currentChat
            boolean r5 = r4.left
            if (r5 == 0) goto L_0x055f
            boolean r4 = r4.kicked
            if (r4 != 0) goto L_0x055f
            int r4 = r10.rowCount
            int r5 = r4 + 1
            r10.rowCount = r5
            r10.joinRow = r4
            int r4 = r5 + 1
            r10.rowCount = r4
            r10.lastSectionRow = r5
            goto L_0x055f
        L_0x04d4:
            org.telegram.tgnet.TLRPC$ChatFull r4 = r10.chatInfo
            if (r4 == 0) goto L_0x055f
            org.telegram.tgnet.TLRPC$ChatParticipants r4 = r4.participants
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantsForbidden
            if (r4 != 0) goto L_0x055f
            org.telegram.tgnet.TLRPC$Chat r4 = r10.currentChat
            boolean r4 = org.telegram.messenger.ChatObject.canAddUsers(r4)
            if (r4 != 0) goto L_0x04f0
            org.telegram.tgnet.TLRPC$Chat r4 = r10.currentChat
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r4.default_banned_rights
            if (r4 == 0) goto L_0x04f0
            boolean r4 = r4.invite_users
            if (r4 != 0) goto L_0x04f8
        L_0x04f0:
            int r4 = r10.rowCount
            int r5 = r4 + 1
            r10.rowCount = r5
            r10.addMemberRow = r4
        L_0x04f8:
            org.telegram.tgnet.TLRPC$ChatFull r4 = r10.chatInfo
            org.telegram.tgnet.TLRPC$ChatParticipants r4 = r4.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r4 = r4.participants
            int r4 = r4.size()
            if (r4 <= r6) goto L_0x051f
            if (r3 != 0) goto L_0x0507
            goto L_0x051f
        L_0x0507:
            int r4 = r10.addMemberRow
            if (r4 == r2) goto L_0x0513
            int r4 = r10.rowCount
            int r5 = r4 + 1
            r10.rowCount = r5
            r10.membersSectionRow = r4
        L_0x0513:
            org.telegram.ui.Components.SharedMediaLayout r4 = r10.sharedMediaLayout
            if (r4 == 0) goto L_0x055f
            java.util.ArrayList<java.lang.Integer> r5 = r10.sortedUsers
            org.telegram.tgnet.TLRPC$ChatFull r6 = r10.chatInfo
            r4.setChatUsers(r5, r6)
            goto L_0x055f
        L_0x051f:
            int r4 = r10.addMemberRow
            if (r4 != r2) goto L_0x052b
            int r4 = r10.rowCount
            int r5 = r4 + 1
            r10.rowCount = r5
            r10.membersHeaderRow = r4
        L_0x052b:
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
            if (r4 == 0) goto L_0x0558
            java.util.ArrayList<java.lang.Integer> r5 = r10.visibleSortedUsers
            r5.addAll(r4)
        L_0x0558:
            org.telegram.ui.Components.SharedMediaLayout r4 = r10.sharedMediaLayout
            if (r4 == 0) goto L_0x055f
            r4.setChatUsers(r7, r7)
        L_0x055f:
            if (r3 == 0) goto L_0x0569
            int r3 = r10.rowCount
            int r4 = r3 + 1
            r10.rowCount = r4
            r10.sharedMediaRow = r3
        L_0x0569:
            int r3 = r10.sharedMediaRow
            if (r3 != r2) goto L_0x0575
            int r2 = r10.rowCount
            int r3 = r2 + 1
            r10.rowCount = r3
            r10.bottomPaddingRow = r2
        L_0x0575:
            org.telegram.ui.ActionBar.ActionBar r2 = r10.actionBar
            if (r2 == 0) goto L_0x058b
            int r2 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight()
            org.telegram.ui.ActionBar.ActionBar r3 = r10.actionBar
            boolean r3 = r3.getOccupyStatusBar()
            if (r3 == 0) goto L_0x0588
            int r3 = org.telegram.messenger.AndroidUtilities.statusBarHeight
            goto L_0x0589
        L_0x0588:
            r3 = 0
        L_0x0589:
            int r2 = r2 + r3
            goto L_0x058c
        L_0x058b:
            r2 = 0
        L_0x058c:
            org.telegram.ui.Components.RecyclerListView r3 = r10.listView
            if (r3 == 0) goto L_0x05a8
            int r3 = r10.rowCount
            if (r0 > r3) goto L_0x05a8
            int r0 = r10.listContentHeight
            if (r0 == 0) goto L_0x05aa
            int r0 = r0 + r2
            r2 = 1118830592(0x42b00000, float:88.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 + r2
            org.telegram.ui.Components.RecyclerListView r2 = r10.listView
            int r2 = r2.getMeasuredHeight()
            if (r0 >= r2) goto L_0x05aa
        L_0x05a8:
            r10.lastMeasuredContentWidth = r1
        L_0x05aa:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.updateRowsIds():void");
    }

    private Drawable getScamDrawable(int i) {
        if (this.scamDrawable == null) {
            ScamDrawable scamDrawable2 = new ScamDrawable(11, i);
            this.scamDrawable = scamDrawable2;
            scamDrawable2.setColor(Theme.getColor("avatar_subtitleInProfileBlue"));
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
            this.verifiedCrossfadeDrawable = new CrossfadeDrawable(new CombinedDrawable(this.verifiedDrawable, this.verifiedCheckDrawable), ContextCompat.getDrawable(getParentActivity(), NUM));
        }
        return this.verifiedCrossfadeDrawable;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x0209  */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x020e  */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x0211  */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x0237  */
    /* JADX WARNING: Removed duplicated region for block: B:196:0x0419  */
    /* JADX WARNING: Removed duplicated region for block: B:274:0x05ce  */
    /* JADX WARNING: Removed duplicated region for block: B:277:0x05d5  */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x05d9  */
    /* JADX WARNING: Removed duplicated region for block: B:290:0x061e  */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x0620  */
    /* JADX WARNING: Removed duplicated region for block: B:294:0x0625  */
    /* JADX WARNING: Removed duplicated region for block: B:305:0x0665  */
    /* JADX WARNING: Removed duplicated region for block: B:322:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateProfileData() {
        /*
            r26 = this;
            r0 = r26
            android.widget.FrameLayout r1 = r0.avatarContainer
            if (r1 == 0) goto L_0x067a
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r0.nameTextView
            if (r1 != 0) goto L_0x000c
            goto L_0x067a
        L_0x000c:
            org.telegram.tgnet.ConnectionsManager r1 = r26.getConnectionsManager()
            int r1 = r1.getConnectionState()
            r2 = 2
            r4 = 1
            if (r1 != r2) goto L_0x0022
            r1 = 2131628977(0x7f0e13b1, float:1.8885262E38)
            java.lang.String r5 = "WaitingForNetwork"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r1)
            goto L_0x0049
        L_0x0022:
            if (r1 != r4) goto L_0x002e
            r1 = 2131625127(0x7f0e04a7, float:1.8877453E38)
            java.lang.String r5 = "Connecting"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r1)
            goto L_0x0049
        L_0x002e:
            r5 = 5
            if (r1 != r5) goto L_0x003b
            r1 = 2131628526(0x7f0e11ee, float:1.8884347E38)
            java.lang.String r5 = "Updating"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r1)
            goto L_0x0049
        L_0x003b:
            r5 = 4
            if (r1 != r5) goto L_0x0048
            r1 = 2131625129(0x7f0e04a9, float:1.8877457E38)
            java.lang.String r5 = "ConnectingToProxy"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r1)
            goto L_0x0049
        L_0x0048:
            r1 = 0
        L_0x0049:
            long r5 = r0.userId
            java.lang.String r7 = "g"
            r8 = 0
            r10 = 0
            int r11 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1))
            if (r11 == 0) goto L_0x0273
            org.telegram.messenger.MessagesController r5 = r26.getMessagesController()
            long r11 = r0.userId
            java.lang.Long r6 = java.lang.Long.valueOf(r11)
            org.telegram.tgnet.TLRPC$User r5 = r5.getUser(r6)
            if (r5 != 0) goto L_0x0065
            return
        L_0x0065:
            org.telegram.tgnet.TLRPC$UserProfilePhoto r6 = r5.photo
            if (r6 == 0) goto L_0x006c
            org.telegram.tgnet.TLRPC$FileLocation r6 = r6.photo_big
            goto L_0x006d
        L_0x006c:
            r6 = 0
        L_0x006d:
            org.telegram.ui.Components.AvatarDrawable r11 = r0.avatarDrawable
            r11.setInfo((org.telegram.tgnet.TLRPC$User) r5)
            org.telegram.messenger.ImageLocation r15 = org.telegram.messenger.ImageLocation.getForUserOrChat(r5, r10)
            org.telegram.messenger.ImageLocation r14 = org.telegram.messenger.ImageLocation.getForUserOrChat(r5, r4)
            org.telegram.ui.Components.ProfileGalleryView r11 = r0.avatarsViewPager
            org.telegram.messenger.ImageLocation r12 = r11.getCurrentVideoLocation(r14, r15)
            org.telegram.ui.Components.ProfileGalleryView r11 = r0.avatarsViewPager
            r11.initIfEmpty(r15, r14)
            if (r12 == 0) goto L_0x008d
            int r11 = r12.imageType
            if (r11 != r2) goto L_0x008d
            r13 = r7
            goto L_0x008e
        L_0x008d:
            r13 = 0
        L_0x008e:
            org.telegram.tgnet.TLRPC$FileLocation r7 = r0.avatarBig
            if (r7 != 0) goto L_0x00a5
            org.telegram.ui.ProfileActivity$AvatarImageView r11 = r0.avatarImage
            org.telegram.ui.Components.AvatarDrawable r7 = r0.avatarDrawable
            java.lang.String r16 = "50_50"
            r18 = r14
            r3 = r15
            r15 = r16
            r16 = r7
            r17 = r5
            r11.setImage((org.telegram.messenger.ImageLocation) r12, (java.lang.String) r13, (org.telegram.messenger.ImageLocation) r14, (java.lang.String) r15, (android.graphics.drawable.Drawable) r16, (java.lang.Object) r17)
            goto L_0x00a8
        L_0x00a5:
            r18 = r14
            r3 = r15
        L_0x00a8:
            r7 = -1
            if (r18 == 0) goto L_0x00af
            int r11 = r0.setAvatarRow
            if (r11 != r7) goto L_0x00b5
        L_0x00af:
            if (r18 != 0) goto L_0x00bb
            int r11 = r0.setAvatarRow
            if (r11 != r7) goto L_0x00bb
        L_0x00b5:
            r0.updateListAnimated(r10)
            r0.needLayout(r4)
        L_0x00bb:
            if (r3 == 0) goto L_0x00d8
            org.telegram.messenger.ImageLocation r7 = r0.prevLoadedImageLocation
            if (r7 == 0) goto L_0x00c9
            long r11 = r3.photoId
            long r13 = r7.photoId
            int r7 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r7 == 0) goto L_0x00d8
        L_0x00c9:
            r0.prevLoadedImageLocation = r3
            org.telegram.messenger.FileLoader r11 = r26.getFileLoader()
            r14 = 0
            r15 = 0
            r16 = 1
            r12 = r3
            r13 = r5
            r11.loadFile(r12, r13, r14, r15, r16)
        L_0x00d8:
            java.lang.String r3 = org.telegram.messenger.UserObject.getUserName(r5)
            long r11 = r5.id
            org.telegram.messenger.UserConfig r7 = r26.getUserConfig()
            long r13 = r7.getClientUserId()
            int r7 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r7 != 0) goto L_0x00f5
            r7 = 2131626913(0x7f0e0ba1, float:1.8881076E38)
            java.lang.String r11 = "Online"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r11, r7)
            goto L_0x0168
        L_0x00f5:
            long r11 = r5.id
            r13 = 333000(0x514c8, double:1.64524E-318)
            int r7 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r7 == 0) goto L_0x015f
            r13 = 777000(0xbdb28, double:3.83889E-318)
            int r7 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r7 == 0) goto L_0x015f
            r13 = 42777(0xa719, double:2.11346E-319)
            int r7 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r7 != 0) goto L_0x010d
            goto L_0x015f
        L_0x010d:
            boolean r7 = org.telegram.messenger.MessagesController.isSupportUser(r5)
            if (r7 == 0) goto L_0x011d
            r7 = 2131628278(0x7f0e10f6, float:1.8883844E38)
            java.lang.String r11 = "SupportStatus"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r11, r7)
            goto L_0x0168
        L_0x011d:
            boolean r7 = r0.isBot
            if (r7 == 0) goto L_0x012b
            r7 = 2131624642(0x7f0e02c2, float:1.887647E38)
            java.lang.String r11 = "Bot"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r11, r7)
            goto L_0x0168
        L_0x012b:
            boolean[] r7 = r0.isOnline
            r7[r10] = r10
            int r11 = r0.currentAccount
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatUserStatus(r11, r5, r7)
            org.telegram.ui.ActionBar.SimpleTextView[] r11 = r0.onlineTextView
            r12 = r11[r4]
            if (r12 == 0) goto L_0x0168
            boolean r12 = r0.mediaHeaderVisible
            if (r12 != 0) goto L_0x0168
            boolean[] r12 = r0.isOnline
            boolean r12 = r12[r10]
            if (r12 == 0) goto L_0x0148
            java.lang.String r12 = "profile_status"
            goto L_0x014a
        L_0x0148:
            java.lang.String r12 = "avatar_subtitleInProfileBlue"
        L_0x014a:
            r11 = r11[r4]
            r11.setTag(r12)
            boolean r11 = r0.isPulledDown
            if (r11 != 0) goto L_0x0168
            org.telegram.ui.ActionBar.SimpleTextView[] r11 = r0.onlineTextView
            r11 = r11[r4]
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r11.setTextColor(r12)
            goto L_0x0168
        L_0x015f:
            r7 = 2131628007(0x7f0e0fe7, float:1.8883295E38)
            java.lang.String r11 = "ServiceNotifications"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r11, r7)
        L_0x0168:
            r11 = 0
        L_0x0169:
            if (r11 >= r2) goto L_0x0263
            org.telegram.ui.ActionBar.SimpleTextView[] r12 = r0.nameTextView
            r12 = r12[r11]
            if (r12 != 0) goto L_0x0173
            goto L_0x025f
        L_0x0173:
            if (r11 != 0) goto L_0x01eb
            long r12 = r5.id
            org.telegram.messenger.UserConfig r14 = r26.getUserConfig()
            long r14 = r14.getClientUserId()
            int r16 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
            if (r16 == 0) goto L_0x01eb
            long r12 = r5.id
            r14 = 1000(0x3e8, double:4.94E-321)
            long r16 = r12 / r14
            r19 = 777(0x309, double:3.84E-321)
            int r18 = (r16 > r19 ? 1 : (r16 == r19 ? 0 : -1))
            if (r18 == 0) goto L_0x01eb
            long r12 = r12 / r14
            r14 = 333(0x14d, double:1.645E-321)
            int r16 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
            if (r16 == 0) goto L_0x01eb
            java.lang.String r12 = r5.phone
            if (r12 == 0) goto L_0x01eb
            int r12 = r12.length()
            if (r12 == 0) goto L_0x01eb
            org.telegram.messenger.ContactsController r12 = r26.getContactsController()
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, org.telegram.tgnet.TLRPC$TL_contact> r12 = r12.contactsDict
            long r13 = r5.id
            java.lang.Long r13 = java.lang.Long.valueOf(r13)
            java.lang.Object r12 = r12.get(r13)
            if (r12 != 0) goto L_0x01eb
            org.telegram.messenger.ContactsController r12 = r26.getContactsController()
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, org.telegram.tgnet.TLRPC$TL_contact> r12 = r12.contactsDict
            int r12 = r12.size()
            if (r12 != 0) goto L_0x01c8
            org.telegram.messenger.ContactsController r12 = r26.getContactsController()
            boolean r12 = r12.isLoadingContacts()
            if (r12 != 0) goto L_0x01eb
        L_0x01c8:
            org.telegram.PhoneFormat.PhoneFormat r12 = org.telegram.PhoneFormat.PhoneFormat.getInstance()
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r14 = "+"
            r13.append(r14)
            java.lang.String r14 = r5.phone
            r13.append(r14)
            java.lang.String r13 = r13.toString()
            java.lang.String r12 = r12.format(r13)
            org.telegram.ui.ActionBar.SimpleTextView[] r13 = r0.nameTextView
            r13 = r13[r11]
            r13.setText(r12)
            goto L_0x01f2
        L_0x01eb:
            org.telegram.ui.ActionBar.SimpleTextView[] r12 = r0.nameTextView
            r12 = r12[r11]
            r12.setText(r3)
        L_0x01f2:
            if (r11 != 0) goto L_0x01fe
            if (r1 == 0) goto L_0x01fe
            org.telegram.ui.ActionBar.SimpleTextView[] r12 = r0.onlineTextView
            r12 = r12[r11]
            r12.setText(r1)
            goto L_0x0205
        L_0x01fe:
            org.telegram.ui.ActionBar.SimpleTextView[] r12 = r0.onlineTextView
            r12 = r12[r11]
            r12.setText(r7)
        L_0x0205:
            org.telegram.tgnet.TLRPC$EncryptedChat r12 = r0.currentEncryptedChat
            if (r12 == 0) goto L_0x020e
            android.graphics.drawable.Drawable r12 = r26.getLockIconDrawable()
            goto L_0x020f
        L_0x020e:
            r12 = 0
        L_0x020f:
            if (r11 != 0) goto L_0x0237
            boolean r13 = r5.scam
            if (r13 != 0) goto L_0x0230
            boolean r14 = r5.fake
            if (r14 == 0) goto L_0x021a
            goto L_0x0230
        L_0x021a:
            org.telegram.messenger.MessagesController r13 = r26.getMessagesController()
            long r14 = r0.dialogId
            int r16 = (r14 > r8 ? 1 : (r14 == r8 ? 0 : -1))
            if (r16 == 0) goto L_0x0225
            goto L_0x0227
        L_0x0225:
            long r14 = r0.userId
        L_0x0227:
            boolean r13 = r13.isDialogMuted(r14)
            if (r13 == 0) goto L_0x0249
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.chat_muteIconDrawable
            goto L_0x0251
        L_0x0230:
            r13 = r13 ^ 1
            android.graphics.drawable.Drawable r13 = r0.getScamDrawable(r13)
            goto L_0x0251
        L_0x0237:
            boolean r13 = r5.scam
            if (r13 != 0) goto L_0x024b
            boolean r14 = r5.fake
            if (r14 == 0) goto L_0x0240
            goto L_0x024b
        L_0x0240:
            boolean r13 = r5.verified
            if (r13 == 0) goto L_0x0249
            android.graphics.drawable.Drawable r13 = r26.getVerifiedCrossfadeDrawable()
            goto L_0x0251
        L_0x0249:
            r13 = 0
            goto L_0x0251
        L_0x024b:
            r13 = r13 ^ 1
            android.graphics.drawable.Drawable r13 = r0.getScamDrawable(r13)
        L_0x0251:
            org.telegram.ui.ActionBar.SimpleTextView[] r14 = r0.nameTextView
            r14 = r14[r11]
            r14.setLeftDrawable((android.graphics.drawable.Drawable) r12)
            org.telegram.ui.ActionBar.SimpleTextView[] r12 = r0.nameTextView
            r12 = r12[r11]
            r12.setRightDrawable((android.graphics.drawable.Drawable) r13)
        L_0x025f:
            int r11 = r11 + 1
            goto L_0x0169
        L_0x0263:
            org.telegram.ui.ProfileActivity$AvatarImageView r1 = r0.avatarImage
            org.telegram.messenger.ImageReceiver r1 = r1.getImageReceiver()
            boolean r2 = org.telegram.ui.PhotoViewer.isShowingImage((org.telegram.tgnet.TLRPC$FileLocation) r6)
            r2 = r2 ^ r4
            r1.setVisible(r2, r10)
            goto L_0x0660
        L_0x0273:
            long r5 = r0.chatId
            int r3 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1))
            if (r3 == 0) goto L_0x0660
            org.telegram.messenger.MessagesController r3 = r26.getMessagesController()
            long r5 = r0.chatId
            java.lang.Long r5 = java.lang.Long.valueOf(r5)
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r5)
            if (r3 == 0) goto L_0x028c
            r0.currentChat = r3
            goto L_0x028e
        L_0x028c:
            org.telegram.tgnet.TLRPC$Chat r3 = r0.currentChat
        L_0x028e:
            boolean r5 = org.telegram.messenger.ChatObject.isChannel(r3)
            java.lang.String r8 = "MegaPublic"
            java.lang.String r11 = "MegaPrivate"
            java.lang.String r13 = "MegaLocation"
            java.lang.String r14 = "OnlineCount"
            java.lang.String r15 = "%s, %s"
            java.lang.String r9 = "Subscribers"
            java.lang.String r6 = "Members"
            if (r5 == 0) goto L_0x03c3
            org.telegram.tgnet.TLRPC$ChatFull r5 = r0.chatInfo
            if (r5 == 0) goto L_0x038d
            org.telegram.tgnet.TLRPC$Chat r12 = r0.currentChat
            boolean r10 = r12.megagroup
            if (r10 != 0) goto L_0x02be
            int r5 = r5.participants_count
            if (r5 == 0) goto L_0x038d
            boolean r5 = org.telegram.messenger.ChatObject.hasAdminRights(r12)
            if (r5 != 0) goto L_0x038d
            org.telegram.tgnet.TLRPC$ChatFull r5 = r0.chatInfo
            boolean r5 = r5.can_view_participants
            if (r5 == 0) goto L_0x02be
            goto L_0x038d
        L_0x02be:
            org.telegram.tgnet.TLRPC$Chat r5 = r0.currentChat
            boolean r5 = r5.megagroup
            if (r5 == 0) goto L_0x0356
            int r5 = r0.onlineCount
            if (r5 <= r4) goto L_0x030f
            org.telegram.tgnet.TLRPC$ChatFull r5 = r0.chatInfo
            int r5 = r5.participants_count
            if (r5 == 0) goto L_0x030f
            java.lang.Object[] r10 = new java.lang.Object[r2]
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r6, r5)
            r12 = 0
            r10[r12] = r5
            int r5 = r0.onlineCount
            org.telegram.tgnet.TLRPC$ChatFull r12 = r0.chatInfo
            int r12 = r12.participants_count
            int r5 = java.lang.Math.min(r5, r12)
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r14, r5)
            r10[r4] = r5
            java.lang.String r5 = java.lang.String.format(r15, r10)
            java.lang.Object[] r10 = new java.lang.Object[r2]
            org.telegram.tgnet.TLRPC$ChatFull r12 = r0.chatInfo
            int r12 = r12.participants_count
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatPluralStringComma(r6, r12)
            r19 = 0
            r10[r19] = r12
            int r12 = r0.onlineCount
            org.telegram.tgnet.TLRPC$ChatFull r2 = r0.chatInfo
            int r2 = r2.participants_count
            int r2 = java.lang.Math.min(r12, r2)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralStringComma(r14, r2)
            r10[r4] = r2
            java.lang.String r2 = java.lang.String.format(r15, r10)
            goto L_0x0414
        L_0x030f:
            org.telegram.tgnet.TLRPC$ChatFull r2 = r0.chatInfo
            int r2 = r2.participants_count
            if (r2 != 0) goto L_0x0348
            boolean r2 = r3.has_geo
            if (r2 == 0) goto L_0x0326
            r2 = 2131626423(0x7f0e09b7, float:1.8880082E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r13, r2)
            java.lang.String r5 = r5.toLowerCase()
            goto L_0x03d2
        L_0x0326:
            java.lang.String r2 = r3.username
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x033b
            r2 = 2131626427(0x7f0e09bb, float:1.888009E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r8, r2)
            java.lang.String r5 = r5.toLowerCase()
            goto L_0x03d2
        L_0x033b:
            r2 = 2131626424(0x7f0e09b8, float:1.8880084E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r11, r2)
            java.lang.String r5 = r5.toLowerCase()
            goto L_0x03d2
        L_0x0348:
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r6, r2)
            org.telegram.tgnet.TLRPC$ChatFull r2 = r0.chatInfo
            int r2 = r2.participants_count
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralStringComma(r6, r2)
            goto L_0x0414
        L_0x0356:
            int[] r2 = new int[r4]
            org.telegram.tgnet.TLRPC$ChatFull r5 = r0.chatInfo
            int r5 = r5.participants_count
            org.telegram.messenger.LocaleController.formatShortNumber(r5, r2)
            org.telegram.tgnet.TLRPC$Chat r2 = r0.currentChat
            boolean r2 = r2.megagroup
            if (r2 == 0) goto L_0x0376
            org.telegram.tgnet.TLRPC$ChatFull r2 = r0.chatInfo
            int r2 = r2.participants_count
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r6, r2)
            org.telegram.tgnet.TLRPC$ChatFull r5 = r0.chatInfo
            int r5 = r5.participants_count
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralStringComma(r6, r5)
            goto L_0x0386
        L_0x0376:
            org.telegram.tgnet.TLRPC$ChatFull r2 = r0.chatInfo
            int r2 = r2.participants_count
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r9, r2)
            org.telegram.tgnet.TLRPC$ChatFull r5 = r0.chatInfo
            int r5 = r5.participants_count
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralStringComma(r9, r5)
        L_0x0386:
            r25 = r5
            r5 = r2
            r2 = r25
            goto L_0x0414
        L_0x038d:
            org.telegram.tgnet.TLRPC$Chat r2 = r0.currentChat
            boolean r2 = r2.megagroup
            if (r2 == 0) goto L_0x03a1
            r2 = 2131626311(0x7f0e0947, float:1.8879855E38)
            java.lang.String r5 = "Loading"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r5, r2)
            java.lang.String r5 = r2.toLowerCase()
            goto L_0x03d2
        L_0x03a1:
            int r2 = r3.flags
            r2 = r2 & 64
            if (r2 == 0) goto L_0x03b5
            r2 = 2131624874(0x7f0e03aa, float:1.887694E38)
            java.lang.String r5 = "ChannelPublic"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r5, r2)
            java.lang.String r5 = r2.toLowerCase()
            goto L_0x03d2
        L_0x03b5:
            r2 = 2131624871(0x7f0e03a7, float:1.8876934E38)
            java.lang.String r5 = "ChannelPrivate"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r5, r2)
            java.lang.String r5 = r2.toLowerCase()
            goto L_0x03d2
        L_0x03c3:
            boolean r2 = org.telegram.messenger.ChatObject.isKickedFromChat(r3)
            if (r2 == 0) goto L_0x03d4
            r2 = 2131629029(0x7f0e13e5, float:1.8885367E38)
            java.lang.String r5 = "YouWereKicked"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r2)
        L_0x03d2:
            r2 = r5
            goto L_0x0414
        L_0x03d4:
            boolean r2 = org.telegram.messenger.ChatObject.isLeftFromChat(r3)
            if (r2 == 0) goto L_0x03e4
            r2 = 2131629026(0x7f0e13e2, float:1.8885361E38)
            java.lang.String r5 = "YouLeft"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r2)
            goto L_0x03d2
        L_0x03e4:
            int r2 = r3.participants_count
            org.telegram.tgnet.TLRPC$ChatFull r5 = r0.chatInfo
            if (r5 == 0) goto L_0x03f2
            org.telegram.tgnet.TLRPC$ChatParticipants r2 = r5.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r2 = r2.participants
            int r2 = r2.size()
        L_0x03f2:
            if (r2 == 0) goto L_0x040f
            int r5 = r0.onlineCount
            if (r5 <= r4) goto L_0x040f
            r5 = 2
            java.lang.Object[] r10 = new java.lang.Object[r5]
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r6, r2)
            r5 = 0
            r10[r5] = r2
            int r2 = r0.onlineCount
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r14, r2)
            r10[r4] = r2
            java.lang.String r5 = java.lang.String.format(r15, r10)
            goto L_0x03d2
        L_0x040f:
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r6, r2)
            goto L_0x03d2
        L_0x0414:
            r10 = 0
            r12 = 0
        L_0x0416:
            r14 = 2
            if (r10 >= r14) goto L_0x05cb
            org.telegram.ui.ActionBar.SimpleTextView[] r14 = r0.nameTextView
            r15 = r14[r10]
            if (r15 != 0) goto L_0x042c
            r22 = r1
            r23 = r2
            r21 = r5
        L_0x0425:
            r24 = r6
            r16 = 2131626424(0x7f0e09b8, float:1.8880084E38)
            goto L_0x05be
        L_0x042c:
            java.lang.String r15 = r3.title
            if (r15 == 0) goto L_0x0439
            r14 = r14[r10]
            boolean r14 = r14.setText(r15)
            if (r14 == 0) goto L_0x0439
            r12 = 1
        L_0x0439:
            org.telegram.ui.ActionBar.SimpleTextView[] r14 = r0.nameTextView
            r14 = r14[r10]
            r15 = 0
            r14.setLeftDrawable((android.graphics.drawable.Drawable) r15)
            if (r10 == 0) goto L_0x0475
            boolean r14 = r3.scam
            if (r14 != 0) goto L_0x0465
            boolean r15 = r3.fake
            if (r15 == 0) goto L_0x044c
            goto L_0x0465
        L_0x044c:
            boolean r14 = r3.verified
            if (r14 == 0) goto L_0x045c
            org.telegram.ui.ActionBar.SimpleTextView[] r14 = r0.nameTextView
            r14 = r14[r10]
            android.graphics.drawable.Drawable r15 = r26.getVerifiedCrossfadeDrawable()
            r14.setRightDrawable((android.graphics.drawable.Drawable) r15)
            goto L_0x0472
        L_0x045c:
            org.telegram.ui.ActionBar.SimpleTextView[] r14 = r0.nameTextView
            r14 = r14[r10]
            r15 = 0
            r14.setRightDrawable((android.graphics.drawable.Drawable) r15)
            goto L_0x0472
        L_0x0465:
            org.telegram.ui.ActionBar.SimpleTextView[] r15 = r0.nameTextView
            r15 = r15[r10]
            r14 = r14 ^ 1
            android.graphics.drawable.Drawable r14 = r0.getScamDrawable(r14)
            r15.setRightDrawable((android.graphics.drawable.Drawable) r14)
        L_0x0472:
            r21 = r5
            goto L_0x04a8
        L_0x0475:
            boolean r14 = r3.scam
            if (r14 != 0) goto L_0x0499
            boolean r15 = r3.fake
            if (r15 == 0) goto L_0x047e
            goto L_0x0499
        L_0x047e:
            org.telegram.ui.ActionBar.SimpleTextView[] r14 = r0.nameTextView
            r14 = r14[r10]
            org.telegram.messenger.MessagesController r15 = r26.getMessagesController()
            r21 = r5
            long r4 = r0.chatId
            long r4 = -r4
            boolean r4 = r15.isDialogMuted(r4)
            if (r4 == 0) goto L_0x0494
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.chat_muteIconDrawable
            goto L_0x0495
        L_0x0494:
            r15 = 0
        L_0x0495:
            r14.setRightDrawable((android.graphics.drawable.Drawable) r15)
            goto L_0x04a8
        L_0x0499:
            r21 = r5
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r0.nameTextView
            r4 = r4[r10]
            r5 = r14 ^ 1
            android.graphics.drawable.Drawable r5 = r0.getScamDrawable(r5)
            r4.setRightDrawable((android.graphics.drawable.Drawable) r5)
        L_0x04a8:
            if (r10 != 0) goto L_0x04b4
            if (r1 == 0) goto L_0x04b4
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r0.onlineTextView
            r4 = r4[r10]
            r4.setText(r1)
            goto L_0x0515
        L_0x04b4:
            org.telegram.tgnet.TLRPC$Chat r4 = r0.currentChat
            boolean r5 = r4.megagroup
            if (r5 == 0) goto L_0x04d0
            org.telegram.tgnet.TLRPC$ChatFull r5 = r0.chatInfo
            if (r5 == 0) goto L_0x04d0
            int r5 = r0.onlineCount
            if (r5 <= 0) goto L_0x04d0
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r0.onlineTextView
            r4 = r4[r10]
            if (r10 != 0) goto L_0x04cb
            r5 = r21
            goto L_0x04cc
        L_0x04cb:
            r5 = r2
        L_0x04cc:
            r4.setText(r5)
            goto L_0x0515
        L_0x04d0:
            if (r10 != 0) goto L_0x05a7
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r4)
            if (r4 == 0) goto L_0x05a7
            org.telegram.tgnet.TLRPC$ChatFull r4 = r0.chatInfo
            if (r4 == 0) goto L_0x05a7
            int r4 = r4.participants_count
            if (r4 == 0) goto L_0x05a7
            org.telegram.tgnet.TLRPC$Chat r5 = r0.currentChat
            boolean r14 = r5.megagroup
            if (r14 != 0) goto L_0x04ea
            boolean r5 = r5.broadcast
            if (r5 == 0) goto L_0x05a7
        L_0x04ea:
            r5 = 1
            int[] r14 = new int[r5]
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatShortNumber(r4, r14)
            org.telegram.tgnet.TLRPC$Chat r5 = r0.currentChat
            boolean r5 = r5.megagroup
            java.lang.String r15 = "%d"
            if (r5 == 0) goto L_0x057b
            org.telegram.tgnet.TLRPC$ChatFull r5 = r0.chatInfo
            int r5 = r5.participants_count
            if (r5 != 0) goto L_0x054f
            boolean r4 = r3.has_geo
            if (r4 == 0) goto L_0x051b
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r0.onlineTextView
            r4 = r4[r10]
            r5 = 2131626423(0x7f0e09b7, float:1.8880082E38)
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r13, r5)
            java.lang.String r14 = r14.toLowerCase()
            r4.setText(r14)
        L_0x0515:
            r22 = r1
            r23 = r2
            goto L_0x0425
        L_0x051b:
            r5 = 2131626423(0x7f0e09b7, float:1.8880082E38)
            java.lang.String r4 = r3.username
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x0539
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r0.onlineTextView
            r4 = r4[r10]
            r14 = 2131626427(0x7f0e09bb, float:1.888009E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r8, r14)
            java.lang.String r15 = r15.toLowerCase()
            r4.setText(r15)
            goto L_0x0515
        L_0x0539:
            r14 = 2131626427(0x7f0e09bb, float:1.888009E38)
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r0.onlineTextView
            r4 = r4[r10]
            r15 = 2131626424(0x7f0e09b8, float:1.8880084E38)
            java.lang.String r16 = org.telegram.messenger.LocaleController.getString(r11, r15)
            java.lang.String r5 = r16.toLowerCase()
            r4.setText(r5)
            goto L_0x0515
        L_0x054f:
            r16 = 2131626424(0x7f0e09b8, float:1.8880084E38)
            org.telegram.ui.ActionBar.SimpleTextView[] r5 = r0.onlineTextView
            r5 = r5[r10]
            r22 = r1
            r19 = 0
            r1 = r14[r19]
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r6, r1)
            r23 = r2
            r24 = r6
            r2 = 1
            java.lang.Object[] r6 = new java.lang.Object[r2]
            r2 = r14[r19]
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r6[r19] = r2
            java.lang.String r2 = java.lang.String.format(r15, r6)
            java.lang.String r1 = r1.replace(r2, r4)
            r5.setText(r1)
            goto L_0x05be
        L_0x057b:
            r22 = r1
            r23 = r2
            r24 = r6
            r16 = 2131626424(0x7f0e09b8, float:1.8880084E38)
            r19 = 0
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r0.onlineTextView
            r1 = r1[r10]
            r2 = r14[r19]
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r9, r2)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]
            r5 = r14[r19]
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r6[r19] = r5
            java.lang.String r5 = java.lang.String.format(r15, r6)
            java.lang.String r2 = r2.replace(r5, r4)
            r1.setText(r2)
            goto L_0x05be
        L_0x05a7:
            r22 = r1
            r23 = r2
            r24 = r6
            r16 = 2131626424(0x7f0e09b8, float:1.8880084E38)
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r0.onlineTextView
            r1 = r1[r10]
            if (r10 != 0) goto L_0x05b9
            r2 = r21
            goto L_0x05bb
        L_0x05b9:
            r2 = r23
        L_0x05bb:
            r1.setText(r2)
        L_0x05be:
            int r10 = r10 + 1
            r5 = r21
            r1 = r22
            r2 = r23
            r6 = r24
            r4 = 1
            goto L_0x0416
        L_0x05cb:
            r1 = 1
            if (r12 == 0) goto L_0x05d1
            r0.needLayout(r1)
        L_0x05d1:
            org.telegram.tgnet.TLRPC$ChatPhoto r2 = r3.photo
            if (r2 == 0) goto L_0x05d9
            org.telegram.tgnet.TLRPC$FileLocation r15 = r2.photo_big
            r2 = r15
            goto L_0x05da
        L_0x05d9:
            r2 = 0
        L_0x05da:
            org.telegram.ui.Components.AvatarDrawable r4 = r0.avatarDrawable
            r4.setInfo((org.telegram.tgnet.TLRPC$Chat) r3)
            r4 = 0
            org.telegram.messenger.ImageLocation r5 = org.telegram.messenger.ImageLocation.getForUserOrChat(r3, r4)
            org.telegram.messenger.ImageLocation r14 = org.telegram.messenger.ImageLocation.getForUserOrChat(r3, r1)
            org.telegram.ui.Components.ProfileGalleryView r1 = r0.avatarsViewPager
            org.telegram.messenger.ImageLocation r12 = r1.getCurrentVideoLocation(r14, r5)
            org.telegram.ui.Components.ProfileGalleryView r1 = r0.avatarsViewPager
            boolean r1 = r1.initIfEmpty(r5, r14)
            if (r5 == 0) goto L_0x05f8
            if (r1 == 0) goto L_0x0617
        L_0x05f8:
            boolean r1 = r0.isPulledDown
            if (r1 == 0) goto L_0x0617
            androidx.recyclerview.widget.LinearLayoutManager r1 = r0.layoutManager
            r4 = 0
            android.view.View r1 = r1.findViewByPosition(r4)
            if (r1 == 0) goto L_0x0617
            org.telegram.ui.Components.RecyclerListView r6 = r0.listView
            int r1 = r1.getTop()
            r8 = 1118830592(0x42b00000, float:88.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r1 = r1 - r8
            org.telegram.ui.Components.CubicBezierInterpolator r8 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            r6.smoothScrollBy(r4, r1, r8)
        L_0x0617:
            if (r12 == 0) goto L_0x0620
            int r1 = r12.imageType
            r4 = 2
            if (r1 != r4) goto L_0x0620
            r13 = r7
            goto L_0x0621
        L_0x0620:
            r13 = 0
        L_0x0621:
            org.telegram.tgnet.TLRPC$FileLocation r1 = r0.avatarBig
            if (r1 != 0) goto L_0x0632
            org.telegram.ui.ProfileActivity$AvatarImageView r11 = r0.avatarImage
            org.telegram.ui.Components.AvatarDrawable r1 = r0.avatarDrawable
            java.lang.String r15 = "50_50"
            r16 = r1
            r17 = r3
            r11.setImage((org.telegram.messenger.ImageLocation) r12, (java.lang.String) r13, (org.telegram.messenger.ImageLocation) r14, (java.lang.String) r15, (android.graphics.drawable.Drawable) r16, (java.lang.Object) r17)
        L_0x0632:
            if (r5 == 0) goto L_0x064f
            org.telegram.messenger.ImageLocation r1 = r0.prevLoadedImageLocation
            if (r1 == 0) goto L_0x0640
            long r6 = r5.photoId
            long r8 = r1.photoId
            int r1 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r1 == 0) goto L_0x064f
        L_0x0640:
            r0.prevLoadedImageLocation = r5
            org.telegram.messenger.FileLoader r11 = r26.getFileLoader()
            r14 = 0
            r15 = 0
            r16 = 1
            r12 = r5
            r13 = r3
            r11.loadFile(r12, r13, r14, r15, r16)
        L_0x064f:
            org.telegram.ui.ProfileActivity$AvatarImageView r1 = r0.avatarImage
            org.telegram.messenger.ImageReceiver r1 = r1.getImageReceiver()
            boolean r2 = org.telegram.ui.PhotoViewer.isShowingImage((org.telegram.tgnet.TLRPC$FileLocation) r2)
            r3 = 1
            r2 = r2 ^ r3
            r3 = 0
            r1.setVisible(r2, r3)
            goto L_0x0661
        L_0x0660:
            r3 = 0
        L_0x0661:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.qrItem
            if (r1 == 0) goto L_0x067a
            float r2 = r0.searchTransitionProgress
            r4 = 1056964608(0x3var_, float:0.5)
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 <= 0) goto L_0x0675
            boolean r2 = r26.isQrNeedVisible()
            if (r2 == 0) goto L_0x0675
            r10 = 0
            goto L_0x0677
        L_0x0675:
            r10 = 8
        L_0x0677:
            r1.setVisibility(r10)
        L_0x067a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.updateProfileData():void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:170:0x039e  */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x03d8  */
    /* JADX WARNING: Removed duplicated region for block: B:174:0x03ed  */
    /* JADX WARNING: Removed duplicated region for block: B:176:0x03f4  */
    /* JADX WARNING: Removed duplicated region for block: B:179:0x0408  */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x042b  */
    /* JADX WARNING: Removed duplicated region for block: B:218:0x04e1  */
    /* JADX WARNING: Removed duplicated region for block: B:221:0x04f8  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x050f  */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x0526  */
    /* JADX WARNING: Removed duplicated region for block: B:230:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void createActionBarMenu(boolean r17) {
        /*
            r16 = this;
            r0 = r16
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            if (r1 == 0) goto L_0x052d
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.otherItem
            if (r2 != 0) goto L_0x000c
            goto L_0x052d
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
            r5 = 2131165783(0x7var_, float:1.7945793E38)
            r6 = 0
            r8 = 2131165898(0x7var_ca, float:1.7946026E38)
            r9 = 2131165813(0x7var_, float:1.7945854E38)
            r10 = 1
            int r11 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1))
            if (r11 == 0) goto L_0x01eb
            org.telegram.messenger.MessagesController r3 = r16.getMessagesController()
            long r6 = r0.userId
            java.lang.Long r4 = java.lang.Long.valueOf(r6)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            if (r3 != 0) goto L_0x0044
            return
        L_0x0044:
            boolean r4 = org.telegram.messenger.UserObject.isUserSelf(r3)
            if (r4 == 0) goto L_0x005f
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r3 = 30
            r4 = 2131165793(0x7var_, float:1.7945813E38)
            r6 = 2131625482(0x7f0e060a, float:1.8878173E38)
            java.lang.String r7 = "EditName"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r1.addSubItem(r3, r4, r6)
            goto L_0x038a
        L_0x005f:
            org.telegram.tgnet.TLRPC$UserFull r4 = r0.userInfo
            if (r4 == 0) goto L_0x0078
            boolean r6 = r4.phone_calls_available
            if (r6 == 0) goto L_0x0078
            r0.callItemVisible = r10
            int r6 = android.os.Build.VERSION.SDK_INT
            r7 = 18
            if (r6 < r7) goto L_0x0075
            boolean r4 = r4.video_calls_available
            if (r4 == 0) goto L_0x0075
            r4 = 1
            goto L_0x0076
        L_0x0075:
            r4 = 0
        L_0x0076:
            r0.videoCallItemVisible = r4
        L_0x0078:
            boolean r4 = r0.isBot
            r6 = 2131628487(0x7f0e11c7, float:1.8884268E38)
            java.lang.String r7 = "Unblock"
            r11 = 2131165763(0x7var_, float:1.7945752E38)
            r12 = 2
            if (r4 != 0) goto L_0x00f0
            org.telegram.messenger.ContactsController r4 = r16.getContactsController()
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, org.telegram.tgnet.TLRPC$TL_contact> r4 = r4.contactsDict
            long r13 = r0.userId
            java.lang.Long r13 = java.lang.Long.valueOf(r13)
            java.lang.Object r4 = r4.get(r13)
            if (r4 != 0) goto L_0x0098
            goto L_0x00f0
        L_0x0098:
            org.telegram.tgnet.TLRPC$EncryptedChat r4 = r0.currentEncryptedChat
            if (r4 != 0) goto L_0x009f
            r0.createAutoDeleteItem(r1)
        L_0x009f:
            java.lang.String r1 = r3.phone
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x00b6
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r4 = 3
            r10 = 2131628049(0x7f0e1011, float:1.888338E38)
            java.lang.String r13 = "ShareContact"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r13, r10)
            r1.addSubItem(r4, r8, r10)
        L_0x00b6:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            boolean r4 = r0.userBlocked
            if (r4 != 0) goto L_0x00c6
            r4 = 2131624616(0x7f0e02a8, float:1.8876417E38)
            java.lang.String r6 = "BlockContact"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            goto L_0x00ca
        L_0x00c6:
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r6)
        L_0x00ca:
            r1.addSubItem(r12, r11, r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r4 = 4
            r6 = 2131165793(0x7var_, float:1.7945813E38)
            r7 = 2131625469(0x7f0e05fd, float:1.8878147E38)
            java.lang.String r8 = "EditContact"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r1.addSubItem(r4, r6, r7)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r4 = 5
            r6 = 2131625294(0x7f0e054e, float:1.8877792E38)
            java.lang.String r7 = "DeleteContact"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r1.addSubItem(r4, r5, r6)
            goto L_0x019a
        L_0x00f0:
            boolean r4 = org.telegram.messenger.MessagesController.isSupportUser(r3)
            if (r4 == 0) goto L_0x0105
            boolean r1 = r0.userBlocked
            if (r1 == 0) goto L_0x019a
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r1.addSubItem(r12, r11, r4)
            goto L_0x019a
        L_0x0105:
            org.telegram.tgnet.TLRPC$EncryptedChat r4 = r0.currentEncryptedChat
            if (r4 != 0) goto L_0x010c
            r0.createAutoDeleteItem(r1)
        L_0x010c:
            boolean r1 = r0.isBot
            if (r1 == 0) goto L_0x0138
            boolean r1 = r3.bot_nochats
            if (r1 != 0) goto L_0x0127
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r4 = 9
            r10 = 2131165748(0x7var_, float:1.7945722E38)
            r13 = 2131624651(0x7f0e02cb, float:1.8876488E38)
            java.lang.String r14 = "BotInvite"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r1.addSubItem(r4, r10, r13)
        L_0x0127:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r4 = 10
            r10 = 2131624667(0x7f0e02db, float:1.887652E38)
            java.lang.String r13 = "BotShare"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r13, r10)
            r1.addSubItem(r4, r8, r10)
            goto L_0x0149
        L_0x0138:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r4 = 2131165749(0x7var_, float:1.7945724E38)
            r13 = 2131624218(0x7f0e011a, float:1.887561E38)
            java.lang.String r14 = "AddContact"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r1.addSubItem(r10, r4, r13)
        L_0x0149:
            java.lang.String r1 = r3.phone
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0160
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r4 = 3
            r10 = 2131628049(0x7f0e1011, float:1.888338E38)
            java.lang.String r13 = "ShareContact"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r13, r10)
            r1.addSubItem(r4, r8, r10)
        L_0x0160:
            boolean r1 = r0.isBot
            if (r1 == 0) goto L_0x0183
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            boolean r4 = r0.userBlocked
            if (r4 != 0) goto L_0x016b
            goto L_0x016e
        L_0x016b:
            r11 = 2131165882(0x7var_ba, float:1.7945994E38)
        L_0x016e:
            if (r4 != 0) goto L_0x0176
            r4 = 2131624671(0x7f0e02df, float:1.8876528E38)
            java.lang.String r6 = "BotStop"
            goto L_0x017b
        L_0x0176:
            r4 = 2131624663(0x7f0e02d7, float:1.8876512E38)
            java.lang.String r6 = "BotRestart"
        L_0x017b:
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r1.addSubItem(r12, r11, r4)
            goto L_0x019a
        L_0x0183:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            boolean r4 = r0.userBlocked
            if (r4 != 0) goto L_0x0193
            r4 = 2131624616(0x7f0e02a8, float:1.8876417E38)
            java.lang.String r6 = "BlockContact"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            goto L_0x0197
        L_0x0193:
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r6)
        L_0x0197:
            r1.addSubItem(r12, r11, r4)
        L_0x019a:
            boolean r1 = org.telegram.messenger.UserObject.isDeleted(r3)
            if (r1 != 0) goto L_0x01d6
            boolean r1 = r0.isBot
            if (r1 != 0) goto L_0x01d6
            org.telegram.tgnet.TLRPC$EncryptedChat r1 = r0.currentEncryptedChat
            if (r1 != 0) goto L_0x01d6
            boolean r1 = r0.userBlocked
            if (r1 != 0) goto L_0x01d6
            long r3 = r0.userId
            r6 = 333000(0x514c8, double:1.64524E-318)
            int r1 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1))
            if (r1 == 0) goto L_0x01d6
            r6 = 777000(0xbdb28, double:3.83889E-318)
            int r1 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1))
            if (r1 == 0) goto L_0x01d6
            r6 = 42777(0xa719, double:2.11346E-319)
            int r1 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1))
            if (r1 == 0) goto L_0x01d6
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r3 = 20
            r4 = 2131165908(0x7var_d4, float:1.7946046E38)
            r6 = 2131628192(0x7f0e10a0, float:1.888367E38)
            java.lang.String r7 = "StartEncryptedChat"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r1.addSubItem(r3, r4, r6)
        L_0x01d6:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r3 = 14
            r4 = 2131165807(0x7var_f, float:1.7945842E38)
            r6 = 2131624240(0x7f0e0130, float:1.8875654E38)
            java.lang.String r7 = "AddShortcut"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r1.addSubItem(r3, r4, r6)
            goto L_0x0389
        L_0x01eb:
            long r3 = r0.chatId
            int r11 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1))
            if (r11 == 0) goto L_0x0389
            org.telegram.messenger.MessagesController r3 = r16.getMessagesController()
            long r11 = r0.chatId
            java.lang.Long r4 = java.lang.Long.valueOf(r11)
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r4)
            r0.hasVoiceChatItem = r2
            r4 = 13
            boolean r4 = org.telegram.messenger.ChatObject.canUserDoAdminAction(r3, r4)
            if (r4 == 0) goto L_0x020c
            r0.createAutoDeleteItem(r1)
        L_0x020c:
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r3)
            r4 = 7
            if (r1 == 0) goto L_0x0309
            boolean r1 = org.telegram.messenger.ChatObject.hasAdminRights(r3)
            if (r1 != 0) goto L_0x0223
            boolean r1 = r3.megagroup
            if (r1 == 0) goto L_0x0225
            boolean r1 = org.telegram.messenger.ChatObject.canChangeChatInfo(r3)
            if (r1 == 0) goto L_0x0225
        L_0x0223:
            r0.editItemVisible = r10
        L_0x0225:
            org.telegram.tgnet.TLRPC$ChatFull r1 = r0.chatInfo
            if (r1 == 0) goto L_0x0282
            boolean r1 = org.telegram.messenger.ChatObject.canManageCalls(r3)
            if (r1 == 0) goto L_0x0258
            org.telegram.tgnet.TLRPC$ChatFull r1 = r0.chatInfo
            org.telegram.tgnet.TLRPC$TL_inputGroupCall r1 = r1.call
            if (r1 != 0) goto L_0x0258
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r11 = 15
            r12 = 2131165941(0x7var_f5, float:1.7946113E38)
            boolean r13 = r3.megagroup
            if (r13 == 0) goto L_0x024a
            boolean r13 = r3.gigagroup
            if (r13 != 0) goto L_0x024a
            r13 = 2131628202(0x7f0e10aa, float:1.888369E38)
            java.lang.String r14 = "StartVoipChat"
            goto L_0x024f
        L_0x024a:
            r13 = 2131628199(0x7f0e10a7, float:1.8883684E38)
            java.lang.String r14 = "StartVoipChannel"
        L_0x024f:
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r1.addSubItem(r11, r12, r13)
            r0.hasVoiceChatItem = r10
        L_0x0258:
            org.telegram.tgnet.TLRPC$ChatFull r1 = r0.chatInfo
            boolean r1 = r1.can_view_stats
            if (r1 == 0) goto L_0x0271
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r11 = 19
            r12 = 2131165909(0x7var_d5, float:1.7946048E38)
            r13 = 2131628213(0x7f0e10b5, float:1.8883712E38)
            java.lang.String r14 = "Statistics"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r1.addSubItem(r11, r12, r13)
        L_0x0271:
            org.telegram.messenger.MessagesController r1 = r16.getMessagesController()
            long r11 = r0.chatId
            org.telegram.messenger.ChatObject$Call r1 = r1.getGroupCall(r11, r2)
            if (r1 == 0) goto L_0x027f
            r1 = 1
            goto L_0x0280
        L_0x027f:
            r1 = 0
        L_0x0280:
            r0.callItemVisible = r1
        L_0x0282:
            boolean r1 = r3.megagroup
            if (r1 == 0) goto L_0x02b7
            r0.canSearchMembers = r10
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r6 = 17
            r7 = 2131165893(0x7var_c5, float:1.7946016E38)
            r8 = 2131627897(0x7f0e0var_, float:1.8883071E38)
            java.lang.String r10 = "SearchMembers"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r10, r8)
            r1.addSubItem(r6, r7, r8)
            boolean r1 = r3.creator
            if (r1 != 0) goto L_0x0376
            boolean r1 = r3.left
            if (r1 != 0) goto L_0x0376
            boolean r1 = r3.kicked
            if (r1 != 0) goto L_0x0376
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r3 = 2131626261(0x7f0e0915, float:1.8879753E38)
            java.lang.String r6 = "LeaveMegaMenu"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)
            r1.addSubItem(r4, r9, r3)
            goto L_0x0376
        L_0x02b7:
            java.lang.String r1 = r3.username
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x02cf
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r3 = 10
            r10 = 2131624667(0x7f0e02db, float:1.887652E38)
            java.lang.String r11 = "BotShare"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r1.addSubItem(r3, r8, r10)
        L_0x02cf:
            org.telegram.tgnet.TLRPC$ChatFull r1 = r0.chatInfo
            if (r1 == 0) goto L_0x02ec
            long r10 = r1.linked_chat_id
            int r1 = (r10 > r6 ? 1 : (r10 == r6 ? 0 : -1))
            if (r1 == 0) goto L_0x02ec
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r3 = 22
            r6 = 2131165787(0x7var_b, float:1.79458E38)
            r7 = 2131628670(0x7f0e127e, float:1.888464E38)
            java.lang.String r8 = "ViewDiscussion"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r1.addSubItem(r3, r6, r7)
        L_0x02ec:
            org.telegram.tgnet.TLRPC$Chat r1 = r0.currentChat
            boolean r3 = r1.creator
            if (r3 != 0) goto L_0x0376
            boolean r3 = r1.left
            if (r3 != 0) goto L_0x0376
            boolean r1 = r1.kicked
            if (r1 != 0) goto L_0x0376
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r3 = 2131626259(0x7f0e0913, float:1.887975E38)
            java.lang.String r6 = "LeaveChannelMenu"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)
            r1.addSubItem(r4, r9, r3)
            goto L_0x0376
        L_0x0309:
            org.telegram.tgnet.TLRPC$ChatFull r1 = r0.chatInfo
            if (r1 == 0) goto L_0x033f
            boolean r1 = org.telegram.messenger.ChatObject.canManageCalls(r3)
            if (r1 == 0) goto L_0x032e
            org.telegram.tgnet.TLRPC$ChatFull r1 = r0.chatInfo
            org.telegram.tgnet.TLRPC$TL_inputGroupCall r1 = r1.call
            if (r1 != 0) goto L_0x032e
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r6 = 15
            r7 = 2131165941(0x7var_f5, float:1.7946113E38)
            r8 = 2131628202(0x7f0e10aa, float:1.888369E38)
            java.lang.String r11 = "StartVoipChat"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r11, r8)
            r1.addSubItem(r6, r7, r8)
            r0.hasVoiceChatItem = r10
        L_0x032e:
            org.telegram.messenger.MessagesController r1 = r16.getMessagesController()
            long r6 = r0.chatId
            org.telegram.messenger.ChatObject$Call r1 = r1.getGroupCall(r6, r2)
            if (r1 == 0) goto L_0x033c
            r1 = 1
            goto L_0x033d
        L_0x033c:
            r1 = 0
        L_0x033d:
            r0.callItemVisible = r1
        L_0x033f:
            boolean r1 = org.telegram.messenger.ChatObject.canChangeChatInfo(r3)
            if (r1 == 0) goto L_0x0347
            r0.editItemVisible = r10
        L_0x0347:
            boolean r1 = org.telegram.messenger.ChatObject.isKickedFromChat(r3)
            if (r1 != 0) goto L_0x0368
            boolean r1 = org.telegram.messenger.ChatObject.isLeftFromChat(r3)
            if (r1 != 0) goto L_0x0368
            r0.canSearchMembers = r10
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r3 = 17
            r6 = 2131165893(0x7var_c5, float:1.7946016E38)
            r7 = 2131627897(0x7f0e0var_, float:1.8883071E38)
            java.lang.String r8 = "SearchMembers"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r1.addSubItem(r3, r6, r7)
        L_0x0368:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r3 = 2131625278(0x7f0e053e, float:1.887776E38)
            java.lang.String r6 = "DeleteAndExit"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)
            r1.addSubItem(r4, r9, r3)
        L_0x0376:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r3 = 14
            r4 = 2131165807(0x7var_f, float:1.7945842E38)
            r6 = 2131624240(0x7f0e0130, float:1.8875654E38)
            java.lang.String r7 = "AddShortcut"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r1.addSubItem(r3, r4, r6)
        L_0x0389:
            r10 = 0
        L_0x038a:
            org.telegram.ui.Components.ImageUpdater r1 = r0.imageUpdater
            r3 = 35
            r4 = 33
            r6 = 36
            r7 = 2131627853(0x7f0e0f4d, float:1.8882982E38)
            java.lang.String r8 = "SaveToGallery"
            r11 = 2131165800(0x7var_, float:1.7945827E38)
            r12 = 21
            if (r1 == 0) goto L_0x03d8
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r13 = 2131165751(0x7var_, float:1.7945728E38)
            r14 = 2131624237(0x7f0e012d, float:1.8875648E38)
            java.lang.String r15 = "AddPhoto"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r15, r14)
            r1.addSubItem(r6, r13, r14)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r13 = 2131165715(0x7var_, float:1.7945655E38)
            r14 = 2131628017(0x7f0e0ff1, float:1.8883315E38)
            java.lang.String r15 = "SetAsMain"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r15, r14)
            r1.addSubItem(r4, r13, r14)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r1.addSubItem(r12, r11, r7)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r7 = 2131625265(0x7f0e0531, float:1.8877733E38)
            java.lang.String r8 = "Delete"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r1.addSubItem(r3, r5, r7)
            goto L_0x03e1
        L_0x03d8:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r1.addSubItem(r12, r11, r5)
        L_0x03e1:
            org.telegram.messenger.MessagesController r1 = r16.getMessagesController()
            org.telegram.tgnet.TLRPC$Chat r5 = r0.currentChat
            boolean r1 = r1.isChatNoForwards((org.telegram.tgnet.TLRPC$Chat) r5)
            if (r1 == 0) goto L_0x03f2
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r1.hideSubItem(r12)
        L_0x03f2:
            if (r10 == 0) goto L_0x0404
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r5 = 31
            r7 = 2131626336(0x7f0e0960, float:1.8879905E38)
            java.lang.String r8 = "LogOut"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r1.addSubItem(r5, r9, r7)
        L_0x0404:
            boolean r1 = r0.isPulledDown
            if (r1 != 0) goto L_0x0423
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r1.hideSubItem(r12)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r1.hideSubItem(r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r1.showSubItem(r6)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r4 = 34
            r1.hideSubItem(r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r1.hideSubItem(r3)
        L_0x0423:
            boolean r1 = r0.mediaHeaderVisible
            r3 = 1065353216(0x3var_, float:1.0)
            r4 = 8
            if (r1 != 0) goto L_0x04d3
            boolean r1 = r0.callItemVisible
            r5 = 150(0x96, double:7.4E-322)
            r7 = 0
            if (r1 == 0) goto L_0x0458
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.callItem
            int r1 = r1.getVisibility()
            if (r1 == 0) goto L_0x0465
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.callItem
            r1.setVisibility(r2)
            if (r17 == 0) goto L_0x0465
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.callItem
            r1.setAlpha(r7)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.callItem
            android.view.ViewPropertyAnimator r1 = r1.animate()
            android.view.ViewPropertyAnimator r1 = r1.alpha(r3)
            android.view.ViewPropertyAnimator r1 = r1.setDuration(r5)
            r1.start()
            goto L_0x0465
        L_0x0458:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.callItem
            int r1 = r1.getVisibility()
            if (r1 == r4) goto L_0x0465
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.callItem
            r1.setVisibility(r4)
        L_0x0465:
            boolean r1 = r0.videoCallItemVisible
            if (r1 == 0) goto L_0x048f
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.videoCallItem
            int r1 = r1.getVisibility()
            if (r1 == 0) goto L_0x049c
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.videoCallItem
            r1.setVisibility(r2)
            if (r17 == 0) goto L_0x049c
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.videoCallItem
            r1.setAlpha(r7)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.videoCallItem
            android.view.ViewPropertyAnimator r1 = r1.animate()
            android.view.ViewPropertyAnimator r1 = r1.alpha(r3)
            android.view.ViewPropertyAnimator r1 = r1.setDuration(r5)
            r1.start()
            goto L_0x049c
        L_0x048f:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.videoCallItem
            int r1 = r1.getVisibility()
            if (r1 == r4) goto L_0x049c
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.videoCallItem
            r1.setVisibility(r4)
        L_0x049c:
            boolean r1 = r0.editItemVisible
            if (r1 == 0) goto L_0x04c6
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.editItem
            int r1 = r1.getVisibility()
            if (r1 == 0) goto L_0x04d3
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.editItem
            r1.setVisibility(r2)
            if (r17 == 0) goto L_0x04d3
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.editItem
            r1.setAlpha(r7)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.editItem
            android.view.ViewPropertyAnimator r1 = r1.animate()
            android.view.ViewPropertyAnimator r1 = r1.alpha(r3)
            android.view.ViewPropertyAnimator r1 = r1.setDuration(r5)
            r1.start()
            goto L_0x04d3
        L_0x04c6:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.editItem
            int r1 = r1.getVisibility()
            if (r1 == r4) goto L_0x04d3
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.editItem
            r1.setVisibility(r4)
        L_0x04d3:
            org.telegram.ui.ProfileActivity$PagerIndicatorView r1 = r0.avatarsViewPagerIndicatorView
            if (r1 == 0) goto L_0x0522
            boolean r1 = r1.isIndicatorFullyVisible()
            if (r1 == 0) goto L_0x0522
            boolean r1 = r0.editItemVisible
            if (r1 == 0) goto L_0x04f4
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.editItem
            r1.setVisibility(r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.editItem
            android.view.ViewPropertyAnimator r1 = r1.animate()
            r1.cancel()
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.editItem
            r1.setAlpha(r3)
        L_0x04f4:
            boolean r1 = r0.callItemVisible
            if (r1 == 0) goto L_0x050b
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.callItem
            r1.setVisibility(r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.callItem
            android.view.ViewPropertyAnimator r1 = r1.animate()
            r1.cancel()
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.callItem
            r1.setAlpha(r3)
        L_0x050b:
            boolean r1 = r0.videoCallItemVisible
            if (r1 == 0) goto L_0x0522
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.videoCallItem
            r1.setVisibility(r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.videoCallItem
            android.view.ViewPropertyAnimator r1 = r1.animate()
            r1.cancel()
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.videoCallItem
            r1.setAlpha(r3)
        L_0x0522:
            org.telegram.ui.Components.SharedMediaLayout r1 = r0.sharedMediaLayout
            if (r1 == 0) goto L_0x052d
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r1.getSearchItem()
            r1.requestLayout()
        L_0x052d:
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
        }, false, getResourceProvider());
        TLRPC$UserFull tLRPC$UserFull = this.userInfo;
        if (tLRPC$UserFull == null && this.chatInfo == null) {
            i = 0;
        } else {
            i = tLRPC$UserFull != null ? tLRPC$UserFull.ttl_period : this.chatInfo.ttl_period;
        }
        TimerDrawable ttlIcon = TimerDrawable.getTtlIcon(i);
        this.autoDeleteItemDrawable = ttlIcon;
        this.autoDeleteItem = this.otherItem.addSwipeBackItem(0, ttlIcon, LocaleController.getString("AutoDeletePopupTitle", NUM), this.autoDeletePopupWrapper.windowLayout);
        this.otherItem.addColoredGap();
        updateAutoDeleteItem();
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
        ActionBarMenuItem actionBarMenuItem2 = this.qrItem;
        if (actionBarMenuItem2 != null) {
            actionBarMenuItem2.setVisibility((!isQrNeedVisible() || this.searchTransitionProgress <= 0.5f) ? 8 : 0);
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
    public /* synthetic */ void lambda$searchExpandTransition$29(ValueAnimator valueAnimator, float f, boolean z, ValueAnimator valueAnimator2) {
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
        int i2 = this.searchTransitionProgress > 0.5f ? 0 : 8;
        ActionBarMenuItem actionBarMenuItem = this.otherItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.setVisibility(i2);
            this.otherItem.setAlpha(f2);
        }
        ActionBarMenuItem actionBarMenuItem2 = this.qrItem;
        if (actionBarMenuItem2 != null) {
            actionBarMenuItem2.setAlpha(f2);
            ActionBarMenuItem actionBarMenuItem3 = this.qrItem;
            if (this.searchTransitionProgress > 0.5f && isQrNeedVisible()) {
                i = 0;
            }
            actionBarMenuItem3.setVisibility(i);
        }
        this.searchItem.setVisibility(i2);
        ActionBar actionBar = this.actionBar;
        if (this.searchTransitionProgress >= 0.5f) {
            z2 = false;
        }
        actionBar.onSearchFieldVisibilityChanged(z2);
        ActionBarMenuItem actionBarMenuItem4 = this.otherItem;
        if (actionBarMenuItem4 != null) {
            actionBarMenuItem4.setAlpha(f2);
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
    public /* synthetic */ void lambda$didUploadPhoto$31(String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new ProfileActivity$$ExternalSyntheticLambda23(this, tLRPC$TL_error, tLObject, str));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didUploadPhoto$30(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, String str) {
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
                FileLoader.getPathToAttach(this.avatar, true).renameTo(FileLoader.getPathToAttach(closestPhotoSizeWithSize, true));
                ImageLoader.getInstance().replaceImageInCache(this.avatar.volume_id + "_" + this.avatar.local_id + "@50_50", closestPhotoSizeWithSize.location.volume_id + "_" + closestPhotoSizeWithSize.location.local_id + "@50_50", ImageLocation.getForUserOrChat(user, 1), false);
            }
            if (!(closestPhotoSizeWithSize2 == null || this.avatarBig == null)) {
                FileLoader.getPathToAttach(this.avatarBig, true).renameTo(FileLoader.getPathToAttach(closestPhotoSizeWithSize2, true));
            }
            if (!(tLRPC$VideoSize == null || str == null)) {
                new File(str).renameTo(FileLoader.getPathToAttach(tLRPC$VideoSize, "mp4", true));
            }
            getMessagesStorage().clearUserPhotos(user.id);
            ArrayList arrayList2 = new ArrayList();
            arrayList2.add(user);
            getMessagesStorage().putUsersAndChats(arrayList2, (ArrayList<TLRPC$Chat>) null, false, true);
        }
        this.allowPullingDown = !AndroidUtilities.isTablet() && !this.isInLandscapeMode && this.avatarImage.getImageReceiver().hasNotThumb();
        this.avatar = null;
        this.avatarBig = null;
        this.avatarsViewPager.setCreateThumbFromParent(false);
        updateProfileData();
        showAvatarProgress(false, true);
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_ALL));
        getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
        getUserConfig().saveConfig(true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didUploadPhoto$32(TLRPC$InputFile tLRPC$InputFile, TLRPC$InputFile tLRPC$InputFile2, double d, String str, TLRPC$PhotoSize tLRPC$PhotoSize, TLRPC$PhotoSize tLRPC$PhotoSize2) {
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
            getConnectionsManager().sendRequest(tLRPC$TL_photos_uploadProfilePhoto, new ProfileActivity$$ExternalSyntheticLambda31(this, str));
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
    public /* synthetic */ void lambda$sendLogs$34(boolean r18, org.telegram.ui.ActionBar.AlertDialog r19) {
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.lambda$sendLogs$34(boolean, org.telegram.ui.ActionBar.AlertDialog):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendLogs$33(AlertDialog alertDialog, boolean[] zArr, File file) {
        Uri uri;
        try {
            alertDialog.dismiss();
        } catch (Exception unused) {
        }
        if (zArr[0]) {
            int i = Build.VERSION.SDK_INT;
            if (i >= 24) {
                uri = FileProvider.getUriForFile(getParentActivity(), "org.telegram.messenger.beta.provider", file);
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
            Toast.makeText(getParentActivity(), LocaleController.getString("ErrorOccurred", NUM), 0).show();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            TextInfoPrivacyCell textInfoPrivacyCell;
            View view;
            switch (i) {
                case 1:
                    view = new HeaderCell(this.mContext, 23);
                    break;
                case 2:
                    TextDetailCell textDetailCell = new TextDetailCell(this.mContext);
                    textDetailCell.setContentDescriptionValueFirst(true);
                    textDetailCell.setImageClickListener(new ProfileActivity$ListAdapter$$ExternalSyntheticLambda1(ProfileActivity.this));
                    textInfoPrivacyCell = textDetailCell;
                    break;
                case 3:
                    textInfoPrivacyCell = ProfileActivity.this.aboutLinkCell = new AboutLinkCell(this.mContext, ProfileActivity.this) {
                        /* access modifiers changed from: protected */
                        public void didPressUrl(String str) {
                            if (str.startsWith("@")) {
                                ProfileActivity.this.getMessagesController().openByUserName(str.substring(1), ProfileActivity.this, 0);
                            } else if (str.startsWith("#")) {
                                DialogsActivity dialogsActivity = new DialogsActivity((Bundle) null);
                                dialogsActivity.setSearchString(str);
                                ProfileActivity.this.presentFragment(dialogsActivity);
                            } else if (str.startsWith("/") && ProfileActivity.this.parentLayout.fragmentsStack.size() > 1) {
                                BaseFragment baseFragment = ProfileActivity.this.parentLayout.fragmentsStack.get(ProfileActivity.this.parentLayout.fragmentsStack.size() - 2);
                                if (baseFragment instanceof ChatActivity) {
                                    ProfileActivity.this.finishFragment();
                                    ((ChatActivity) baseFragment).chatActivityEnterView.setCommand((MessageObject) null, str, false, false);
                                }
                            }
                        }

                        /* access modifiers changed from: protected */
                        public void didResizeEnd() {
                            ProfileActivity.this.layoutManager.mIgnoreTopPadding = false;
                        }

                        /* access modifiers changed from: protected */
                        public void didResizeStart() {
                            ProfileActivity.this.layoutManager.mIgnoreTopPadding = true;
                        }
                    };
                    break;
                case 4:
                    textInfoPrivacyCell = new TextCell(this.mContext);
                    break;
                case 5:
                    DividerCell dividerCell = new DividerCell(this.mContext);
                    dividerCell.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(4.0f), 0, 0);
                    textInfoPrivacyCell = dividerCell;
                    break;
                case 6:
                    view = new NotificationsCheckCell(this.mContext, 23, 70, false);
                    break;
                case 7:
                    textInfoPrivacyCell = new ShadowSectionCell(this.mContext);
                    break;
                case 8:
                    textInfoPrivacyCell = new UserCell(this.mContext, ProfileActivity.this.addMemberRow == -1 ? 9 : 6, 0, true);
                    break;
                case 11:
                    textInfoPrivacyCell = new View(this, this.mContext) {
                        /* access modifiers changed from: protected */
                        public void onMeasure(int i, int i2) {
                            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), NUM));
                        }
                    };
                    break;
                case 12:
                    AnonymousClass3 r0 = new View(this.mContext) {
                        private int lastListViewHeight = 0;
                        private int lastPaddingHeight = 0;

                        /* access modifiers changed from: protected */
                        public void onMeasure(int i, int i2) {
                            int i3 = 0;
                            if (this.lastListViewHeight != ProfileActivity.this.listView.getMeasuredHeight()) {
                                this.lastPaddingHeight = 0;
                            }
                            this.lastListViewHeight = ProfileActivity.this.listView.getMeasuredHeight();
                            int childCount = ProfileActivity.this.listView.getChildCount();
                            if (childCount == ProfileActivity.this.listAdapter.getItemCount()) {
                                int i4 = 0;
                                for (int i5 = 0; i5 < childCount; i5++) {
                                    int childAdapterPosition = ProfileActivity.this.listView.getChildAdapterPosition(ProfileActivity.this.listView.getChildAt(i5));
                                    if (childAdapterPosition >= 0 && childAdapterPosition != ProfileActivity.this.bottomPaddingRow) {
                                        i4 += ProfileActivity.this.listView.getChildAt(i5).getMeasuredHeight();
                                    }
                                }
                                int measuredHeight = ((ProfileActivity.this.fragmentView.getMeasuredHeight() - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.statusBarHeight) - i4;
                                if (measuredHeight > AndroidUtilities.dp(88.0f)) {
                                    measuredHeight = 0;
                                }
                                if (measuredHeight > 0) {
                                    i3 = measuredHeight;
                                }
                                int measuredWidth = ProfileActivity.this.listView.getMeasuredWidth();
                                this.lastPaddingHeight = i3;
                                setMeasuredDimension(measuredWidth, i3);
                                return;
                            }
                            setMeasuredDimension(ProfileActivity.this.listView.getMeasuredWidth(), this.lastPaddingHeight);
                        }
                    };
                    r0.setBackground(new ColorDrawable(0));
                    textInfoPrivacyCell = r0;
                    break;
                case 13:
                    if (ProfileActivity.this.sharedMediaLayout.getParent() != null) {
                        ((ViewGroup) ProfileActivity.this.sharedMediaLayout.getParent()).removeView(ProfileActivity.this.sharedMediaLayout);
                    }
                    textInfoPrivacyCell = ProfileActivity.this.sharedMediaLayout;
                    break;
                case 15:
                    textInfoPrivacyCell = new SettingsSuggestionCell(this.mContext) {
                        /* access modifiers changed from: protected */
                        public void onYesClick(int i) {
                            NotificationCenter notificationCenter = ProfileActivity.this.getNotificationCenter();
                            ProfileActivity profileActivity = ProfileActivity.this;
                            int i2 = NotificationCenter.newSuggestionsAvailable;
                            notificationCenter.removeObserver(profileActivity, i2);
                            ProfileActivity.this.getMessagesController().removeSuggestion(0, i == 0 ? "VALIDATE_PHONE_NUMBER" : "VALIDATE_PASSWORD");
                            ProfileActivity.this.getNotificationCenter().addObserver(ProfileActivity.this, i2);
                            if (i == 0) {
                                int unused = ProfileActivity.this.phoneSuggestionRow;
                            } else {
                                int unused2 = ProfileActivity.this.passwordSuggestionRow;
                            }
                            ProfileActivity.this.updateListAnimated(false);
                        }

                        /* access modifiers changed from: protected */
                        public void onNoClick(int i) {
                            if (i == 0) {
                                ProfileActivity.this.presentFragment(new ActionIntroActivity(3));
                            } else {
                                ProfileActivity.this.presentFragment(new TwoStepVerificationSetupActivity(8, (TLRPC$TL_account_password) null));
                            }
                        }
                    };
                    break;
                case 17:
                    textInfoPrivacyCell = new TextInfoPrivacyCell(this.mContext);
                    break;
                default:
                    TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(this.mContext, 10);
                    textInfoPrivacyCell2.getTextView().setGravity(1);
                    textInfoPrivacyCell2.getTextView().setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
                    textInfoPrivacyCell2.getTextView().setMovementMethod((MovementMethod) null);
                    try {
                        PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                        int i2 = packageInfo.versionCode;
                        int i3 = i2 / 10;
                        String str = "";
                        switch (i2 % 10) {
                            case 0:
                            case 9:
                                if (!BuildVars.isStandaloneApp()) {
                                    str = "universal " + Build.CPU_ABI + " " + Build.CPU_ABI2;
                                    break;
                                } else {
                                    str = "direct " + Build.CPU_ABI + " " + Build.CPU_ABI2;
                                    break;
                                }
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
                        textInfoPrivacyCell2.setText(LocaleController.formatString("TelegramVersion", NUM, String.format(Locale.US, "v%s (%d) %s", new Object[]{packageInfo.versionName, Integer.valueOf(i3), str})));
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                    textInfoPrivacyCell2.getTextView().setPadding(0, AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(14.0f));
                    textInfoPrivacyCell2.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    textInfoPrivacyCell = textInfoPrivacyCell2;
                    break;
            }
            textInfoPrivacyCell = view;
            if (i != 13) {
                textInfoPrivacyCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            }
            return new RecyclerListView.Holder(textInfoPrivacyCell);
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

        /* JADX WARNING: Removed duplicated region for block: B:121:0x02fe  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r22, int r23) {
            /*
                r21 = this;
                r1 = r21
                r0 = r22
                r2 = r23
                int r3 = r22.getItemViewType()
                r4 = 2131624842(0x7f0e038a, float:1.8876875E38)
                java.lang.String r5 = "ChannelMembers"
                r6 = -1
                r7 = 1
                if (r3 == r7) goto L_0x0aa7
                r8 = 2
                r9 = 0
                r11 = 2131628559(0x7f0e120f, float:1.8884414E38)
                java.lang.String r12 = "UserBio"
                r13 = 0
                r14 = 0
                if (r3 == r8) goto L_0x0833
                r8 = 3
                if (r3 == r8) goto L_0x07cb
                r11 = 4
                r12 = 12
                if (r3 == r11) goto L_0x031e
                r4 = 6
                if (r3 == r4) goto L_0x01ad
                r4 = 7
                r5 = 2131165483(0x7var_b, float:1.7945184E38)
                java.lang.String r8 = "windowBackgroundGrayShadow"
                if (r3 == r4) goto L_0x013a
                r4 = 8
                if (r3 == r4) goto L_0x0070
                if (r3 == r12) goto L_0x0069
                r4 = 15
                if (r3 == r4) goto L_0x0056
                r2 = 17
                if (r3 == r2) goto L_0x0042
                goto L_0x0b4f
            L_0x0042:
                android.view.View r0 = r0.itemView
                org.telegram.ui.Cells.TextInfoPrivacyCell r0 = (org.telegram.ui.Cells.TextInfoPrivacyCell) r0
                android.content.Context r2 = r1.mContext
                android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r5, (java.lang.String) r8)
                r0.setBackground(r2)
                java.lang.String r2 = "This bot is able to manage a group or channel."
                r0.setText(r2)
                goto L_0x0b4f
            L_0x0056:
                android.view.View r0 = r0.itemView
                org.telegram.ui.Cells.SettingsSuggestionCell r0 = (org.telegram.ui.Cells.SettingsSuggestionCell) r0
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.passwordSuggestionRow
                if (r2 != r3) goto L_0x0063
                goto L_0x0064
            L_0x0063:
                r7 = 0
            L_0x0064:
                r0.setType(r7)
                goto L_0x0b4f
            L_0x0069:
                android.view.View r0 = r0.itemView
                r0.requestLayout()
                goto L_0x0b4f
            L_0x0070:
                android.view.View r0 = r0.itemView
                r15 = r0
                org.telegram.ui.Cells.UserCell r15 = (org.telegram.ui.Cells.UserCell) r15
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this     // Catch:{ Exception -> 0x00bb }
                java.util.ArrayList r0 = r0.visibleSortedUsers     // Catch:{ Exception -> 0x00bb }
                boolean r0 = r0.isEmpty()     // Catch:{ Exception -> 0x00bb }
                if (r0 != 0) goto L_0x00a6
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this     // Catch:{ Exception -> 0x00bb }
                java.util.ArrayList r0 = r0.visibleChatParticipants     // Catch:{ Exception -> 0x00bb }
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this     // Catch:{ Exception -> 0x00bb }
                java.util.ArrayList r3 = r3.visibleSortedUsers     // Catch:{ Exception -> 0x00bb }
                org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this     // Catch:{ Exception -> 0x00bb }
                int r4 = r4.membersStartRow     // Catch:{ Exception -> 0x00bb }
                int r4 = r2 - r4
                java.lang.Object r3 = r3.get(r4)     // Catch:{ Exception -> 0x00bb }
                java.lang.Integer r3 = (java.lang.Integer) r3     // Catch:{ Exception -> 0x00bb }
                int r3 = r3.intValue()     // Catch:{ Exception -> 0x00bb }
                java.lang.Object r0 = r0.get(r3)     // Catch:{ Exception -> 0x00bb }
                org.telegram.tgnet.TLRPC$ChatParticipant r0 = (org.telegram.tgnet.TLRPC$ChatParticipant) r0     // Catch:{ Exception -> 0x00bb }
                goto L_0x00c0
            L_0x00a6:
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this     // Catch:{ Exception -> 0x00bb }
                java.util.ArrayList r0 = r0.visibleChatParticipants     // Catch:{ Exception -> 0x00bb }
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this     // Catch:{ Exception -> 0x00bb }
                int r3 = r3.membersStartRow     // Catch:{ Exception -> 0x00bb }
                int r3 = r2 - r3
                java.lang.Object r0 = r0.get(r3)     // Catch:{ Exception -> 0x00bb }
                org.telegram.tgnet.TLRPC$ChatParticipant r0 = (org.telegram.tgnet.TLRPC$ChatParticipant) r0     // Catch:{ Exception -> 0x00bb }
                goto L_0x00c0
            L_0x00bb:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                r0 = r13
            L_0x00c0:
                if (r0 == 0) goto L_0x0b4f
                boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_chatChannelParticipant
                if (r3 == 0) goto L_0x00f3
                r3 = r0
                org.telegram.tgnet.TLRPC$TL_chatChannelParticipant r3 = (org.telegram.tgnet.TLRPC$TL_chatChannelParticipant) r3
                org.telegram.tgnet.TLRPC$ChannelParticipant r3 = r3.channelParticipant
                java.lang.String r4 = r3.rank
                boolean r4 = android.text.TextUtils.isEmpty(r4)
                if (r4 != 0) goto L_0x00d7
                java.lang.String r3 = r3.rank
            L_0x00d5:
                r13 = r3
                goto L_0x010e
            L_0x00d7:
                boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantCreator
                if (r4 == 0) goto L_0x00e5
                r3 = 2131624821(0x7f0e0375, float:1.8876833E38)
                java.lang.String r4 = "ChannelCreator"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                goto L_0x00d5
            L_0x00e5:
                boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin
                if (r3 == 0) goto L_0x010e
                r3 = 2131624803(0x7f0e0363, float:1.8876796E38)
                java.lang.String r4 = "ChannelAdmin"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                goto L_0x00d5
            L_0x00f3:
                boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantCreator
                if (r3 == 0) goto L_0x0101
                r3 = 2131624821(0x7f0e0375, float:1.8876833E38)
                java.lang.String r4 = "ChannelCreator"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r4, r3)
                goto L_0x010e
            L_0x0101:
                boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantAdmin
                if (r3 == 0) goto L_0x010e
                r3 = 2131624803(0x7f0e0363, float:1.8876796E38)
                java.lang.String r4 = "ChannelAdmin"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r4, r3)
            L_0x010e:
                r15.setAdminRole(r13)
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.messenger.MessagesController r3 = r3.getMessagesController()
                long r4 = r0.user_id
                java.lang.Long r0 = java.lang.Long.valueOf(r4)
                org.telegram.tgnet.TLRPC$User r16 = r3.getUser(r0)
                r17 = 0
                r18 = 0
                r19 = 0
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                int r0 = r0.membersEndRow
                int r0 = r0 - r7
                if (r2 == r0) goto L_0x0133
                r20 = 1
                goto L_0x0135
            L_0x0133:
                r20 = 0
            L_0x0135:
                r15.setData(r16, r17, r18, r19, r20)
                goto L_0x0b4f
            L_0x013a:
                android.view.View r0 = r0.itemView
                java.lang.Integer r3 = java.lang.Integer.valueOf(r23)
                r0.setTag(r3)
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.infoSectionRow
                if (r2 != r3) goto L_0x016b
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.lastSectionRow
                if (r3 != r6) goto L_0x016b
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.secretSettingsSectionRow
                if (r3 != r6) goto L_0x016b
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.sharedMediaRow
                if (r3 != r6) goto L_0x016b
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.membersSectionRow
                if (r3 == r6) goto L_0x019f
            L_0x016b:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.secretSettingsSectionRow
                if (r2 == r3) goto L_0x019f
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.lastSectionRow
                if (r2 == r3) goto L_0x019f
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.membersSectionRow
                if (r2 != r3) goto L_0x0194
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                int r2 = r2.lastSectionRow
                if (r2 != r6) goto L_0x0194
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                int r2 = r2.sharedMediaRow
                if (r2 != r6) goto L_0x0194
                goto L_0x019f
            L_0x0194:
                android.content.Context r2 = r1.mContext
                android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r5, (java.lang.String) r8)
                r0.setBackgroundDrawable(r2)
                goto L_0x0b4f
            L_0x019f:
                android.content.Context r2 = r1.mContext
                r3 = 2131165484(0x7var_c, float:1.7945186E38)
                android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r3, (java.lang.String) r8)
                r0.setBackgroundDrawable(r2)
                goto L_0x0b4f
            L_0x01ad:
                android.view.View r0 = r0.itemView
                org.telegram.ui.Cells.NotificationsCheckCell r0 = (org.telegram.ui.Cells.NotificationsCheckCell) r0
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.notificationsRow
                if (r2 != r3) goto L_0x0b4f
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                int r2 = r2.currentAccount
                android.content.SharedPreferences r2 = org.telegram.messenger.MessagesController.getNotificationsSettings(r2)
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                long r3 = r3.dialogId
                int r5 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1))
                if (r5 == 0) goto L_0x01d4
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                long r3 = r3.dialogId
                goto L_0x01ec
            L_0x01d4:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                long r3 = r3.userId
                int r5 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1))
                if (r5 == 0) goto L_0x01e5
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                long r3 = r3.userId
                goto L_0x01ec
            L_0x01e5:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                long r3 = r3.chatId
                long r3 = -r3
            L_0x01ec:
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                java.lang.String r6 = "custom_"
                r5.append(r6)
                r5.append(r3)
                java.lang.String r5 = r5.toString()
                boolean r5 = r2.getBoolean(r5, r14)
                java.lang.StringBuilder r6 = new java.lang.StringBuilder
                r6.<init>()
                java.lang.String r9 = "notify2_"
                r6.append(r9)
                r6.append(r3)
                java.lang.String r6 = r6.toString()
                boolean r6 = r2.contains(r6)
                java.lang.StringBuilder r9 = new java.lang.StringBuilder
                r9.<init>()
                java.lang.String r10 = "notify2_"
                r9.append(r10)
                r9.append(r3)
                java.lang.String r9 = r9.toString()
                int r9 = r2.getInt(r9, r14)
                java.lang.StringBuilder r10 = new java.lang.StringBuilder
                r10.<init>()
                java.lang.String r11 = "notifyuntil_"
                r10.append(r11)
                r10.append(r3)
                java.lang.String r10 = r10.toString()
                int r2 = r2.getInt(r10, r14)
                if (r9 != r8) goto L_0x02c7
                r8 = 2147483647(0x7fffffff, float:NaN)
                if (r2 == r8) goto L_0x02c7
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.ConnectionsManager r3 = r3.getConnectionsManager()
                int r3 = r3.getCurrentTime()
                int r2 = r2 - r3
                if (r2 > 0) goto L_0x026c
                if (r5 == 0) goto L_0x0260
                r2 = 2131626855(0x7f0e0b67, float:1.8880958E38)
                java.lang.String r3 = "NotificationsCustom"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                goto L_0x0269
            L_0x0260:
                r2 = 2131626884(0x7f0e0b84, float:1.8881017E38)
                java.lang.String r3 = "NotificationsOn"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            L_0x0269:
                r13 = r2
                goto L_0x02fc
            L_0x026c:
                r3 = 3600(0xe10, float:5.045E-42)
                r4 = 2131629005(0x7f0e13cd, float:1.8885319E38)
                java.lang.String r5 = "WillUnmuteIn"
                if (r2 >= r3) goto L_0x0288
                java.lang.Object[] r3 = new java.lang.Object[r7]
                int r2 = r2 / 60
                java.lang.String r6 = "Minutes"
                java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r6, r2)
                r3[r14] = r2
                java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r5, r4, r3)
            L_0x0285:
                r7 = 0
                goto L_0x02fc
            L_0x0288:
                r3 = 86400(0x15180, float:1.21072E-40)
                r6 = 1114636288(0x42700000, float:60.0)
                if (r2 >= r3) goto L_0x02a7
                java.lang.Object[] r3 = new java.lang.Object[r7]
                float r2 = (float) r2
                float r2 = r2 / r6
                float r2 = r2 / r6
                double r6 = (double) r2
                double r6 = java.lang.Math.ceil(r6)
                int r2 = (int) r6
                java.lang.String r6 = "Hours"
                java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r6, r2)
                r3[r14] = r2
                java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r5, r4, r3)
                goto L_0x0285
            L_0x02a7:
                r3 = 31536000(0x1e13380, float:8.2725845E-38)
                if (r2 >= r3) goto L_0x0285
                java.lang.Object[] r3 = new java.lang.Object[r7]
                float r2 = (float) r2
                float r2 = r2 / r6
                float r2 = r2 / r6
                r6 = 1103101952(0x41CLASSNAME, float:24.0)
                float r2 = r2 / r6
                double r6 = (double) r2
                double r6 = java.lang.Math.ceil(r6)
                int r2 = (int) r6
                java.lang.String r6 = "Days"
                java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r6, r2)
                r3[r14] = r2
                java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r5, r4, r3)
                goto L_0x0285
            L_0x02c7:
                if (r9 != 0) goto L_0x02d7
                if (r6 == 0) goto L_0x02cc
                goto L_0x02db
            L_0x02cc:
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.messenger.NotificationsController r2 = r2.getNotificationsController()
                boolean r7 = r2.isGlobalNotificationsEnabled((long) r3)
                goto L_0x02db
            L_0x02d7:
                if (r9 != r7) goto L_0x02da
                goto L_0x02db
            L_0x02da:
                r7 = 0
            L_0x02db:
                if (r7 == 0) goto L_0x02e9
                if (r5 == 0) goto L_0x02e9
                r2 = 2131626855(0x7f0e0b67, float:1.8880958E38)
                java.lang.String r3 = "NotificationsCustom"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r3, r2)
                goto L_0x02fc
            L_0x02e9:
                if (r7 == 0) goto L_0x02f1
                r2 = 2131626884(0x7f0e0b84, float:1.8881017E38)
                java.lang.String r3 = "NotificationsOn"
                goto L_0x02f6
            L_0x02f1:
                r2 = 2131626882(0x7f0e0b82, float:1.8881013E38)
                java.lang.String r3 = "NotificationsOff"
            L_0x02f6:
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                goto L_0x0269
            L_0x02fc:
                if (r13 != 0) goto L_0x0307
                r2 = 2131626882(0x7f0e0b82, float:1.8881013E38)
                java.lang.String r3 = "NotificationsOff"
                java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r3, r2)
            L_0x0307:
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                boolean r2 = r2.fragmentOpened
                r0.setAnimationsEnabled(r2)
                r2 = 2131626849(0x7f0e0b61, float:1.8880946E38)
                java.lang.String r3 = "Notifications"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setTextAndValueAndCheck(r2, r13, r7, r14)
                goto L_0x0b4f
            L_0x031e:
                android.view.View r0 = r0.itemView
                org.telegram.ui.Cells.TextCell r0 = (org.telegram.ui.Cells.TextCell) r0
                java.lang.String r3 = "windowBackgroundWhiteGrayIcon"
                java.lang.String r8 = "windowBackgroundWhiteBlackText"
                r0.setColors(r3, r8)
                r0.setTag(r8)
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.settingsTimerRow
                if (r2 != r3) goto L_0x036c
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.messenger.MessagesController r2 = r2.getMessagesController()
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                long r3 = r3.dialogId
                int r3 = org.telegram.messenger.DialogObject.getEncryptedChatId(r3)
                java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
                org.telegram.tgnet.TLRPC$EncryptedChat r2 = r2.getEncryptedChat(r3)
                int r2 = r2.ttl
                if (r2 != 0) goto L_0x035a
                r2 = 2131628105(0x7f0e1049, float:1.8883493E38)
                java.lang.String r3 = "ShortMessageLifetimeForever"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                goto L_0x035e
            L_0x035a:
                java.lang.String r2 = org.telegram.messenger.LocaleController.formatTTLString(r2)
            L_0x035e:
                r3 = 2131626462(0x7f0e09de, float:1.888016E38)
                java.lang.String r4 = "MessageLifetime"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                r0.setTextAndValue(r3, r2, r14)
                goto L_0x0b4f
            L_0x036c:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.unblockRow
                java.lang.String r8 = "windowBackgroundWhiteRedText5"
                if (r2 != r3) goto L_0x0387
                r2 = 2131628487(0x7f0e11c7, float:1.8884268E38)
                java.lang.String r3 = "Unblock"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2, r14)
                r0.setColors(r13, r8)
                goto L_0x0b4f
            L_0x0387:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.settingsKeyRow
                if (r2 != r3) goto L_0x03bd
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
                r3 = 2131625531(0x7f0e063b, float:1.8878273E38)
                java.lang.String r4 = "EncryptionKey"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                r0.setTextAndValueDrawable(r3, r2, r14)
                goto L_0x0b4f
            L_0x03bd:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.joinRow
                if (r2 != r3) goto L_0x03f0
                java.lang.String r2 = "windowBackgroundWhiteBlueText2"
                r0.setColors(r13, r2)
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r2 = r2.currentChat
                boolean r2 = r2.megagroup
                if (r2 == 0) goto L_0x03e2
                r2 = 2131627539(0x7f0e0e13, float:1.8882345E38)
                java.lang.String r3 = "ProfileJoinGroup"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2, r14)
                goto L_0x0b4f
            L_0x03e2:
                r2 = 2131627538(0x7f0e0e12, float:1.8882343E38)
                java.lang.String r3 = "ProfileJoinChannel"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2, r14)
                goto L_0x0b4f
            L_0x03f0:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.subscribersRow
                java.lang.String r9 = "%d"
                if (r2 != r3) goto L_0x04b3
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r3 = r3.chatInfo
                r6 = 2131165268(0x7var_, float:1.7944748E38)
                if (r3 == 0) goto L_0x0470
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r3 = r3.currentChat
                boolean r3 = org.telegram.messenger.ChatObject.isChannel(r3)
                if (r3 == 0) goto L_0x0448
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r3 = r3.currentChat
                boolean r3 = r3.megagroup
                if (r3 != 0) goto L_0x0448
                r3 = 2131624888(0x7f0e03b8, float:1.8876968E38)
                java.lang.String r4 = "ChannelSubscribers"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                java.lang.Object[] r4 = new java.lang.Object[r7]
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r5 = r5.chatInfo
                int r5 = r5.participants_count
                java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
                r4[r14] = r5
                java.lang.String r4 = java.lang.String.format(r9, r4)
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                int r5 = r5.membersSectionRow
                int r5 = r5 - r7
                if (r2 == r5) goto L_0x0442
                goto L_0x0443
            L_0x0442:
                r7 = 0
            L_0x0443:
                r0.setTextAndValueAndIcon(r3, r4, r6, r7)
                goto L_0x0b4f
            L_0x0448:
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r4)
                java.lang.Object[] r4 = new java.lang.Object[r7]
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r5 = r5.chatInfo
                int r5 = r5.participants_count
                java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
                r4[r14] = r5
                java.lang.String r4 = java.lang.String.format(r9, r4)
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                int r5 = r5.membersSectionRow
                int r5 = r5 - r7
                if (r2 == r5) goto L_0x046a
                goto L_0x046b
            L_0x046a:
                r7 = 0
            L_0x046b:
                r0.setTextAndValueAndIcon(r3, r4, r6, r7)
                goto L_0x0b4f
            L_0x0470:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r3 = r3.currentChat
                boolean r3 = org.telegram.messenger.ChatObject.isChannel(r3)
                if (r3 == 0) goto L_0x049f
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r3 = r3.currentChat
                boolean r3 = r3.megagroup
                if (r3 != 0) goto L_0x049f
                r3 = 2131624888(0x7f0e03b8, float:1.8876968E38)
                java.lang.String r4 = "ChannelSubscribers"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                int r4 = r4.membersSectionRow
                int r4 = r4 - r7
                if (r2 == r4) goto L_0x0499
                goto L_0x049a
            L_0x0499:
                r7 = 0
            L_0x049a:
                r0.setTextAndIcon((java.lang.String) r3, (int) r6, (boolean) r7)
                goto L_0x0b4f
            L_0x049f:
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r4)
                org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                int r4 = r4.membersSectionRow
                int r4 = r4 - r7
                if (r2 == r4) goto L_0x04ad
                goto L_0x04ae
            L_0x04ad:
                r7 = 0
            L_0x04ae:
                r0.setTextAndIcon((java.lang.String) r3, (int) r6, (boolean) r7)
                goto L_0x0b4f
            L_0x04b3:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.subscribersRequestsRow
                if (r2 != r3) goto L_0x04f3
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r3 = r3.chatInfo
                if (r3 == 0) goto L_0x0b4f
                r3 = 2131628260(0x7f0e10e4, float:1.8883808E38)
                java.lang.String r4 = "SubscribeRequests"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                java.lang.Object[] r4 = new java.lang.Object[r7]
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r5 = r5.chatInfo
                int r5 = r5.requests_pending
                java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
                r4[r14] = r5
                java.lang.String r4 = java.lang.String.format(r9, r4)
                r5 = 2131165266(0x7var_, float:1.7944744E38)
                org.telegram.ui.ProfileActivity r6 = org.telegram.ui.ProfileActivity.this
                int r6 = r6.membersSectionRow
                int r6 = r6 - r7
                if (r2 == r6) goto L_0x04ed
                goto L_0x04ee
            L_0x04ed:
                r7 = 0
            L_0x04ee:
                r0.setTextAndValueAndIcon(r3, r4, r5, r7)
                goto L_0x0b4f
            L_0x04f3:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.administratorsRow
                if (r2 != r3) goto L_0x054f
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r3 = r3.chatInfo
                if (r3 == 0) goto L_0x0533
                r3 = 2131624805(0x7f0e0365, float:1.88768E38)
                java.lang.String r4 = "ChannelAdministrators"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                java.lang.Object[] r4 = new java.lang.Object[r7]
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r5 = r5.chatInfo
                int r5 = r5.admins_count
                java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
                r4[r14] = r5
                java.lang.String r4 = java.lang.String.format(r9, r4)
                r5 = 2131165256(0x7var_, float:1.7944724E38)
                org.telegram.ui.ProfileActivity r6 = org.telegram.ui.ProfileActivity.this
                int r6 = r6.membersSectionRow
                int r6 = r6 - r7
                if (r2 == r6) goto L_0x052d
                goto L_0x052e
            L_0x052d:
                r7 = 0
            L_0x052e:
                r0.setTextAndValueAndIcon(r3, r4, r5, r7)
                goto L_0x0b4f
            L_0x0533:
                r3 = 2131624805(0x7f0e0365, float:1.88768E38)
                java.lang.String r4 = "ChannelAdministrators"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                r4 = 2131165256(0x7var_, float:1.7944724E38)
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                int r5 = r5.membersSectionRow
                int r5 = r5 - r7
                if (r2 == r5) goto L_0x0549
                goto L_0x054a
            L_0x0549:
                r7 = 0
            L_0x054a:
                r0.setTextAndIcon((java.lang.String) r3, (int) r4, (boolean) r7)
                goto L_0x0b4f
            L_0x054f:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.blockedUsersRow
                if (r2 != r3) goto L_0x05b7
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r3 = r3.chatInfo
                if (r3 == 0) goto L_0x059b
                r3 = 2131624810(0x7f0e036a, float:1.887681E38)
                java.lang.String r4 = "ChannelBlacklist"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                java.lang.Object[] r4 = new java.lang.Object[r7]
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r5 = r5.chatInfo
                int r5 = r5.banned_count
                org.telegram.ui.ProfileActivity r6 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r6 = r6.chatInfo
                int r6 = r6.kicked_count
                int r5 = java.lang.Math.max(r5, r6)
                java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
                r4[r14] = r5
                java.lang.String r4 = java.lang.String.format(r9, r4)
                r5 = 2131165265(0x7var_, float:1.7944742E38)
                org.telegram.ui.ProfileActivity r6 = org.telegram.ui.ProfileActivity.this
                int r6 = r6.membersSectionRow
                int r6 = r6 - r7
                if (r2 == r6) goto L_0x0595
                goto L_0x0596
            L_0x0595:
                r7 = 0
            L_0x0596:
                r0.setTextAndValueAndIcon(r3, r4, r5, r7)
                goto L_0x0b4f
            L_0x059b:
                r3 = 2131624810(0x7f0e036a, float:1.887681E38)
                java.lang.String r4 = "ChannelBlacklist"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                r4 = 2131165265(0x7var_, float:1.7944742E38)
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                int r5 = r5.membersSectionRow
                int r5 = r5 - r7
                if (r2 == r5) goto L_0x05b1
                goto L_0x05b2
            L_0x05b1:
                r7 = 0
            L_0x05b2:
                r0.setTextAndIcon((java.lang.String) r3, (int) r4, (boolean) r7)
                goto L_0x0b4f
            L_0x05b7:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.addMemberRow
                java.lang.String r4 = "windowBackgroundWhiteBlueButton"
                java.lang.String r5 = "windowBackgroundWhiteBlueIcon"
                if (r2 != r3) goto L_0x05e1
                r0.setColors(r5, r4)
                r2 = 2131624228(0x7f0e0124, float:1.887563E38)
                java.lang.String r3 = "AddMember"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r3 = 2131165258(0x7var_a, float:1.7944728E38)
                org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                int r4 = r4.membersSectionRow
                if (r4 != r6) goto L_0x05db
                goto L_0x05dc
            L_0x05db:
                r7 = 0
            L_0x05dc:
                r0.setTextAndIcon((java.lang.String) r2, (int) r3, (boolean) r7)
                goto L_0x0b4f
            L_0x05e1:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.sendMessageRow
                if (r2 != r3) goto L_0x05f7
                r2 = 2131627977(0x7f0e0fc9, float:1.8883234E38)
                java.lang.String r3 = "SendMessageLocation"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2, r7)
                goto L_0x0b4f
            L_0x05f7:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.reportRow
                if (r2 != r3) goto L_0x0610
                r2 = 2131627759(0x7f0e0eef, float:1.8882792E38)
                java.lang.String r3 = "ReportUserLocation"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2, r14)
                r0.setColors(r13, r8)
                goto L_0x0b4f
            L_0x0610:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.languageRow
                if (r2 != r3) goto L_0x0629
                r2 = 2131626219(0x7f0e08eb, float:1.8879668E38)
                java.lang.String r3 = "Language"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r3 = 2131165702(0x7var_, float:1.7945629E38)
                r0.setTextAndIcon((java.lang.String) r2, (int) r3, (boolean) r14)
                goto L_0x0b4f
            L_0x0629:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.notificationRow
                if (r2 != r3) goto L_0x0642
                r2 = 2131626851(0x7f0e0b63, float:1.888095E38)
                java.lang.String r3 = "NotificationsAndSounds"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r3 = 2131165710(0x7var_e, float:1.7945645E38)
                r0.setTextAndIcon((java.lang.String) r2, (int) r3, (boolean) r7)
                goto L_0x0b4f
            L_0x0642:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.privacyRow
                if (r2 != r3) goto L_0x065b
                r2 = 2131627534(0x7f0e0e0e, float:1.8882335E38)
                java.lang.String r3 = "PrivacySettings"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r3 = 2131165720(0x7var_, float:1.7945665E38)
                r0.setTextAndIcon((java.lang.String) r2, (int) r3, (boolean) r7)
                goto L_0x0b4f
            L_0x065b:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.dataRow
                if (r2 != r3) goto L_0x0674
                r2 = 2131625209(0x7f0e04f9, float:1.887762E38)
                java.lang.String r3 = "DataSettings"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r3 = 2131165685(0x7var_f5, float:1.7945594E38)
                r0.setTextAndIcon((java.lang.String) r2, (int) r3, (boolean) r7)
                goto L_0x0b4f
            L_0x0674:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.chatRow
                if (r2 != r3) goto L_0x068d
                r2 = 2131624950(0x7f0e03f6, float:1.8877094E38)
                java.lang.String r3 = "ChatSettings"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r3 = 2131165676(0x7var_ec, float:1.7945576E38)
                r0.setTextAndIcon((java.lang.String) r2, (int) r3, (boolean) r7)
                goto L_0x0b4f
            L_0x068d:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.filtersRow
                if (r2 != r3) goto L_0x06a6
                r2 = 2131625791(0x7f0e073f, float:1.88788E38)
                java.lang.String r3 = "Filters"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r3 = 2131165689(0x7var_f9, float:1.7945602E38)
                r0.setTextAndIcon((java.lang.String) r2, (int) r3, (boolean) r7)
                goto L_0x0b4f
            L_0x06a6:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.questionRow
                if (r2 != r3) goto L_0x06bf
                r2 = 2131624413(0x7f0e01dd, float:1.8876005E38)
                java.lang.String r3 = "AskAQuestion"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r3 = 2131165732(0x7var_, float:1.794569E38)
                r0.setTextAndIcon((java.lang.String) r2, (int) r3, (boolean) r7)
                goto L_0x0b4f
            L_0x06bf:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.faqRow
                if (r2 != r3) goto L_0x06d8
                r2 = 2131628332(0x7f0e112c, float:1.8883954E38)
                java.lang.String r3 = "TelegramFAQ"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r3 = 2131165694(0x7var_fe, float:1.7945612E38)
                r0.setTextAndIcon((java.lang.String) r2, (int) r3, (boolean) r7)
                goto L_0x0b4f
            L_0x06d8:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.policyRow
                if (r2 != r3) goto L_0x06f1
                r2 = 2131627527(0x7f0e0e07, float:1.888232E38)
                java.lang.String r3 = "PrivacyPolicy"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r3 = 2131165714(0x7var_, float:1.7945653E38)
                r0.setTextAndIcon((java.lang.String) r2, (int) r3, (boolean) r14)
                goto L_0x0b4f
            L_0x06f1:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.sendLogsRow
                if (r2 != r3) goto L_0x0707
                r2 = 2131625258(0x7f0e052a, float:1.8877719E38)
                java.lang.String r3 = "DebugSendLogs"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2, r7)
                goto L_0x0b4f
            L_0x0707:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.sendLastLogsRow
                if (r2 != r3) goto L_0x071d
                r2 = 2131625257(0x7f0e0529, float:1.8877717E38)
                java.lang.String r3 = "DebugSendLastLogs"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2, r7)
                goto L_0x0b4f
            L_0x071d:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.clearLogsRow
                if (r2 != r3) goto L_0x073d
                r2 = 2131625236(0x7f0e0514, float:1.8877674E38)
                java.lang.String r3 = "DebugClearLogs"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.switchBackendRow
                if (r3 == r6) goto L_0x0737
                goto L_0x0738
            L_0x0737:
                r7 = 0
            L_0x0738:
                r0.setText(r2, r7)
                goto L_0x0b4f
            L_0x073d:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.switchBackendRow
                if (r2 != r3) goto L_0x074c
                java.lang.String r2 = "Switch Backend"
                r0.setText(r2, r14)
                goto L_0x0b4f
            L_0x074c:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.devicesRow
                if (r2 != r3) goto L_0x0765
                r2 = 2131625361(0x7f0e0591, float:1.8877928E38)
                java.lang.String r3 = "Devices"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r3 = 2131165687(0x7var_f7, float:1.7945598E38)
                r0.setTextAndIcon((java.lang.String) r2, (int) r3, (boolean) r7)
                goto L_0x0b4f
            L_0x0765:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.setAvatarRow
                if (r2 != r3) goto L_0x07af
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.ui.Components.RLottieDrawable r2 = r2.cellCameraDrawable
                r3 = 86
                r2.setCustomEndFrame(r3)
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.ui.Components.RLottieDrawable r2 = r2.cellCameraDrawable
                r3 = 85
                r2.setCurrentFrame(r3, r14)
                r2 = 2131628023(0x7f0e0ff7, float:1.8883327E38)
                java.lang.String r3 = "SetProfilePhoto"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.ui.Components.RLottieDrawable r3 = r3.cellCameraDrawable
                r0.setTextAndIcon((java.lang.String) r2, (android.graphics.drawable.Drawable) r3, (boolean) r14)
                r0.setColors(r5, r4)
                org.telegram.ui.Components.RLottieImageView r2 = r0.getImageView()
                r3 = 1090519040(0x41000000, float:8.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                r2.setPadding(r14, r14, r14, r3)
                r0.setImageLeft(r12)
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.ui.Cells.TextCell unused = r2.setAvatarCell = r0
                goto L_0x0b4f
            L_0x07af:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.addToGroupButtonRow
                if (r2 != r3) goto L_0x0b4f
                r2 = 2131624253(0x7f0e013d, float:1.887568E38)
                java.lang.String r3 = "AddToGroupOrChannel"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r3 = 2131165495(0x7var_, float:1.7945209E38)
                r0.setTextAndIcon((java.lang.String) r2, (int) r3, (boolean) r14)
                r0.setColors(r5, r4)
                goto L_0x0b4f
            L_0x07cb:
                android.view.View r0 = r0.itemView
                org.telegram.ui.Cells.AboutLinkCell r0 = (org.telegram.ui.Cells.AboutLinkCell) r0
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.userInfoRow
                if (r2 != r3) goto L_0x07ed
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$UserFull r3 = r3.userInfo
                java.lang.String r3 = r3.about
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r12, r11)
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                boolean r5 = r5.isBot
                r0.setTextAndValue(r3, r4, r5)
                goto L_0x0829
            L_0x07ed:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.channelInfoRow
                if (r2 != r3) goto L_0x0829
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r3 = r3.chatInfo
                java.lang.String r3 = r3.about
            L_0x07fd:
                java.lang.String r4 = "\n\n\n"
                boolean r4 = r3.contains(r4)
                if (r4 == 0) goto L_0x080e
                java.lang.String r4 = "\n\n\n"
                java.lang.String r5 = "\n\n"
                java.lang.String r3 = r3.replace(r4, r5)
                goto L_0x07fd
            L_0x080e:
                org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r4 = r4.currentChat
                boolean r4 = org.telegram.messenger.ChatObject.isChannel(r4)
                if (r4 == 0) goto L_0x0825
                org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r4 = r4.currentChat
                boolean r4 = r4.megagroup
                if (r4 != 0) goto L_0x0825
                goto L_0x0826
            L_0x0825:
                r7 = 0
            L_0x0826:
                r0.setText(r3, r7)
            L_0x0829:
                org.telegram.ui.ProfileActivity$ListAdapter$$ExternalSyntheticLambda0 r3 = new org.telegram.ui.ProfileActivity$ListAdapter$$ExternalSyntheticLambda0
                r3.<init>(r1, r2, r0)
                r0.setOnClickListener(r3)
                goto L_0x0b4f
            L_0x0833:
                android.view.View r0 = r0.itemView
                org.telegram.ui.Cells.TextDetailCell r0 = (org.telegram.ui.Cells.TextDetailCell) r0
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.usernameRow
                if (r2 != r3) goto L_0x085e
                android.content.Context r3 = r0.getContext()
                r4 = 2131165863(0x7var_a7, float:1.7945955E38)
                android.graphics.drawable.Drawable r3 = androidx.core.content.ContextCompat.getDrawable(r3, r4)
                android.graphics.PorterDuffColorFilter r4 = new android.graphics.PorterDuffColorFilter
                java.lang.String r5 = "switch2TrackChecked"
                int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
                android.graphics.PorterDuff$Mode r6 = android.graphics.PorterDuff.Mode.MULTIPLY
                r4.<init>(r5, r6)
                r3.setColorFilter(r4)
                r0.setImage(r3)
                goto L_0x0861
            L_0x085e:
                r0.setImage(r13)
            L_0x0861:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.phoneRow
                if (r2 != r3) goto L_0x08b8
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.messenger.MessagesController r3 = r3.getMessagesController()
                org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                long r4 = r4.userId
                java.lang.Long r4 = java.lang.Long.valueOf(r4)
                org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
                java.lang.String r4 = r3.phone
                boolean r4 = android.text.TextUtils.isEmpty(r4)
                if (r4 != 0) goto L_0x08a1
                org.telegram.PhoneFormat.PhoneFormat r4 = org.telegram.PhoneFormat.PhoneFormat.getInstance()
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                java.lang.String r6 = "+"
                r5.append(r6)
                java.lang.String r3 = r3.phone
                r5.append(r3)
                java.lang.String r3 = r5.toString()
                java.lang.String r3 = r4.format(r3)
                goto L_0x08aa
            L_0x08a1:
                r3 = 2131627318(0x7f0e0d36, float:1.8881897E38)
                java.lang.String r4 = "PhoneHidden"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            L_0x08aa:
                r4 = 2131627321(0x7f0e0d39, float:1.8881903E38)
                java.lang.String r5 = "PhoneMobile"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r0.setTextAndValue(r3, r4, r14)
                goto L_0x0a9e
            L_0x08b8:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.usernameRow
                if (r2 != r3) goto L_0x0954
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                long r3 = r3.userId
                int r5 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1))
                if (r5 == 0) goto L_0x090c
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.messenger.MessagesController r3 = r3.getMessagesController()
                org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                long r4 = r4.userId
                java.lang.Long r4 = java.lang.Long.valueOf(r4)
                org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
                if (r3 == 0) goto L_0x08fc
                java.lang.String r4 = r3.username
                boolean r4 = android.text.TextUtils.isEmpty(r4)
                if (r4 != 0) goto L_0x08fc
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                java.lang.String r5 = "@"
                r4.append(r5)
                java.lang.String r3 = r3.username
                r4.append(r3)
                java.lang.String r3 = r4.toString()
                goto L_0x08fe
            L_0x08fc:
                java.lang.String r3 = "-"
            L_0x08fe:
                r4 = 2131628600(0x7f0e1238, float:1.8884497E38)
                java.lang.String r5 = "Username"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r0.setTextAndValue(r3, r4, r14)
                goto L_0x0a9e
            L_0x090c:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r3 = r3.currentChat
                if (r3 == 0) goto L_0x0a9e
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
                r4 = 2131626145(0x7f0e08a1, float:1.8879518E38)
                java.lang.String r5 = "InviteLink"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r0.setTextAndValue(r3, r4, r14)
                goto L_0x0a9e
            L_0x0954:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.locationRow
                if (r2 != r3) goto L_0x098a
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r3 = r3.chatInfo
                if (r3 == 0) goto L_0x0a9e
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r3 = r3.chatInfo
                org.telegram.tgnet.TLRPC$ChannelLocation r3 = r3.location
                boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_channelLocation
                if (r3 == 0) goto L_0x0a9e
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r3 = r3.chatInfo
                org.telegram.tgnet.TLRPC$ChannelLocation r3 = r3.location
                org.telegram.tgnet.TLRPC$TL_channelLocation r3 = (org.telegram.tgnet.TLRPC$TL_channelLocation) r3
                java.lang.String r3 = r3.address
                r4 = 2131624432(0x7f0e01f0, float:1.8876044E38)
                java.lang.String r5 = "AttachLocation"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r0.setTextAndValue(r3, r4, r14)
                goto L_0x0a9e
            L_0x098a:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.numberRow
                if (r2 != r3) goto L_0x09e2
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.currentAccount
                org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r3)
                org.telegram.tgnet.TLRPC$User r3 = r3.getCurrentUser()
                if (r3 == 0) goto L_0x09c8
                java.lang.String r4 = r3.phone
                if (r4 == 0) goto L_0x09c8
                int r4 = r4.length()
                if (r4 == 0) goto L_0x09c8
                org.telegram.PhoneFormat.PhoneFormat r4 = org.telegram.PhoneFormat.PhoneFormat.getInstance()
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                java.lang.String r6 = "+"
                r5.append(r6)
                java.lang.String r3 = r3.phone
                r5.append(r3)
                java.lang.String r3 = r5.toString()
                java.lang.String r3 = r4.format(r3)
                goto L_0x09d1
            L_0x09c8:
                r3 = 2131626907(0x7f0e0b9b, float:1.8881063E38)
                java.lang.String r4 = "NumberUnknown"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            L_0x09d1:
                r4 = 2131628317(0x7f0e111d, float:1.8883923E38)
                java.lang.String r5 = "TapToChangePhone"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r0.setTextAndValue(r3, r4, r7)
                r0.setContentDescriptionValueFirst(r14)
                goto L_0x0a9e
            L_0x09e2:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.setUsernameRow
                if (r2 != r3) goto L_0x0a2f
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.currentAccount
                org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r3)
                org.telegram.tgnet.TLRPC$User r3 = r3.getCurrentUser()
                if (r3 == 0) goto L_0x0a16
                java.lang.String r4 = r3.username
                boolean r4 = android.text.TextUtils.isEmpty(r4)
                if (r4 != 0) goto L_0x0a16
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                java.lang.String r5 = "@"
                r4.append(r5)
                java.lang.String r3 = r3.username
                r4.append(r3)
                java.lang.String r3 = r4.toString()
                goto L_0x0a1f
            L_0x0a16:
                r3 = 2131628604(0x7f0e123c, float:1.8884505E38)
                java.lang.String r4 = "UsernameEmpty"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            L_0x0a1f:
                r4 = 2131628600(0x7f0e1238, float:1.8884497E38)
                java.lang.String r5 = "Username"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r0.setTextAndValue(r3, r4, r7)
                r0.setContentDescriptionValueFirst(r7)
                goto L_0x0a9e
            L_0x0a2f:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.bioRow
                if (r2 != r3) goto L_0x0a9e
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$UserFull r3 = r3.userInfo
                if (r3 == 0) goto L_0x0a67
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$UserFull r3 = r3.userInfo
                java.lang.String r3 = r3.about
                boolean r3 = android.text.TextUtils.isEmpty(r3)
                if (r3 != 0) goto L_0x0a4e
                goto L_0x0a67
            L_0x0a4e:
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r12, r11)
                r4 = 2131628560(0x7f0e1210, float:1.8884416E38)
                java.lang.String r5 = "UserBioDetail"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r0.setTextAndValue(r3, r4, r14)
                r0.setContentDescriptionValueFirst(r14)
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                java.lang.String unused = r3.currentBio = r13
                goto L_0x0a9e
            L_0x0a67:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$UserFull r3 = r3.userInfo
                if (r3 != 0) goto L_0x0a79
                r3 = 2131626311(0x7f0e0947, float:1.8879855E38)
                java.lang.String r4 = "Loading"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                goto L_0x0a81
            L_0x0a79:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$UserFull r3 = r3.userInfo
                java.lang.String r3 = r3.about
            L_0x0a81:
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r12, r11)
                r0.setTextWithEmojiAndValue(r3, r4, r14)
                r0.setContentDescriptionValueFirst(r7)
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$UserFull r4 = r3.userInfo
                if (r4 == 0) goto L_0x0a9b
                org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$UserFull r4 = r4.userInfo
                java.lang.String r13 = r4.about
            L_0x0a9b:
                java.lang.String unused = r3.currentBio = r13
            L_0x0a9e:
                java.lang.Integer r2 = java.lang.Integer.valueOf(r23)
                r0.setTag(r2)
                goto L_0x0b4f
            L_0x0aa7:
                android.view.View r0 = r0.itemView
                org.telegram.ui.Cells.HeaderCell r0 = (org.telegram.ui.Cells.HeaderCell) r0
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.infoHeaderRow
                if (r2 != r3) goto L_0x0aec
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r2 = r2.currentChat
                boolean r2 = org.telegram.messenger.ChatObject.isChannel(r2)
                if (r2 == 0) goto L_0x0adf
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r2 = r2.currentChat
                boolean r2 = r2.megagroup
                if (r2 != 0) goto L_0x0adf
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                int r2 = r2.channelInfoRow
                if (r2 == r6) goto L_0x0adf
                r2 = 2131627727(0x7f0e0ecf, float:1.8882727E38)
                java.lang.String r3 = "ReportChatDescription"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2)
                goto L_0x0b4f
            L_0x0adf:
                r2 = 2131626116(0x7f0e0884, float:1.887946E38)
                java.lang.String r3 = "Info"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2)
                goto L_0x0b4f
            L_0x0aec:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.membersHeaderRow
                if (r2 != r3) goto L_0x0afc
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r0.setText(r2)
                goto L_0x0b4f
            L_0x0afc:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.settingsSectionRow2
                if (r2 != r3) goto L_0x0b11
                r2 = 2131627841(0x7f0e0var_, float:1.8882958E38)
                java.lang.String r3 = "SETTINGS"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2)
                goto L_0x0b4f
            L_0x0b11:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.numberSectionRow
                if (r2 != r3) goto L_0x0b26
                r2 = 2131624086(0x7f0e0096, float:1.8875342E38)
                java.lang.String r3 = "Account"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2)
                goto L_0x0b4f
            L_0x0b26:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.helpHeaderRow
                if (r2 != r3) goto L_0x0b3b
                r2 = 2131628040(0x7f0e1008, float:1.8883361E38)
                java.lang.String r3 = "SettingsHelp"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2)
                goto L_0x0b4f
            L_0x0b3b:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.debugHeaderRow
                if (r2 != r3) goto L_0x0b4f
                r2 = 2131628038(0x7f0e1006, float:1.8883357E38)
                java.lang.String r3 = "SettingsDebug"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2)
            L_0x0b4f:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onBindViewHolder$1(int i, AboutLinkCell aboutLinkCell, View view) {
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
                if (adapterPosition == ProfileActivity.this.notificationRow || adapterPosition == ProfileActivity.this.numberRow || adapterPosition == ProfileActivity.this.privacyRow || adapterPosition == ProfileActivity.this.languageRow || adapterPosition == ProfileActivity.this.setUsernameRow || adapterPosition == ProfileActivity.this.bioRow || adapterPosition == ProfileActivity.this.versionRow || adapterPosition == ProfileActivity.this.dataRow || adapterPosition == ProfileActivity.this.chatRow || adapterPosition == ProfileActivity.this.questionRow || adapterPosition == ProfileActivity.this.devicesRow || adapterPosition == ProfileActivity.this.filtersRow || adapterPosition == ProfileActivity.this.faqRow || adapterPosition == ProfileActivity.this.policyRow || adapterPosition == ProfileActivity.this.sendLogsRow || adapterPosition == ProfileActivity.this.sendLastLogsRow || adapterPosition == ProfileActivity.this.clearLogsRow || adapterPosition == ProfileActivity.this.switchBackendRow || adapterPosition == ProfileActivity.this.setAvatarRow || adapterPosition == ProfileActivity.this.addToGroupButtonRow) {
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
            if (i == ProfileActivity.this.phoneRow || i == ProfileActivity.this.usernameRow || i == ProfileActivity.this.locationRow || i == ProfileActivity.this.numberRow || i == ProfileActivity.this.setUsernameRow || i == ProfileActivity.this.bioRow) {
                return 2;
            }
            if (i == ProfileActivity.this.userInfoRow || i == ProfileActivity.this.channelInfoRow) {
                return 3;
            }
            if (i == ProfileActivity.this.settingsTimerRow || i == ProfileActivity.this.settingsKeyRow || i == ProfileActivity.this.reportRow || i == ProfileActivity.this.subscribersRow || i == ProfileActivity.this.subscribersRequestsRow || i == ProfileActivity.this.administratorsRow || i == ProfileActivity.this.blockedUsersRow || i == ProfileActivity.this.addMemberRow || i == ProfileActivity.this.joinRow || i == ProfileActivity.this.unblockRow || i == ProfileActivity.this.sendMessageRow || i == ProfileActivity.this.notificationRow || i == ProfileActivity.this.privacyRow || i == ProfileActivity.this.languageRow || i == ProfileActivity.this.dataRow || i == ProfileActivity.this.chatRow || i == ProfileActivity.this.questionRow || i == ProfileActivity.this.devicesRow || i == ProfileActivity.this.filtersRow || i == ProfileActivity.this.faqRow || i == ProfileActivity.this.policyRow || i == ProfileActivity.this.sendLogsRow || i == ProfileActivity.this.sendLastLogsRow || i == ProfileActivity.this.clearLogsRow || i == ProfileActivity.this.switchBackendRow || i == ProfileActivity.this.setAvatarRow || i == ProfileActivity.this.addToGroupButtonRow) {
                return 4;
            }
            if (i == ProfileActivity.this.notificationsDividerRow) {
                return 5;
            }
            if (i == ProfileActivity.this.notificationsRow) {
                return 6;
            }
            if (i == ProfileActivity.this.infoSectionRow || i == ProfileActivity.this.lastSectionRow || i == ProfileActivity.this.membersSectionRow || i == ProfileActivity.this.secretSettingsSectionRow || i == ProfileActivity.this.settingsSectionRow || i == ProfileActivity.this.devicesSectionRow || i == ProfileActivity.this.helpSectionCell || i == ProfileActivity.this.setAvatarSectionRow || i == ProfileActivity.this.passwordSuggestionSectionRow || i == ProfileActivity.this.phoneSuggestionSectionRow) {
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
            return i == ProfileActivity.this.addToGroupInfoRow ? 17 : 0;
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
            this.this$0.presentFragment(new ChangeNameActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$1() {
            this.this$0.presentFragment(new ActionIntroActivity(3));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$2() {
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
            this.this$0.presentFragment(PasscodeActivity.determineOpenFragment());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$24() {
            this.this$0.presentFragment(new TwoStepVerificationActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$25() {
            this.this$0.presentFragment(new SessionsActivity(0));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$26() {
            this.this$0.presentFragment(new PrivacySettingsActivity());
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
            this.this$0.presentFragment(new SessionsActivity(1));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$30() {
            this.this$0.presentFragment(new PrivacySettingsActivity());
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
            this.this$0.presentFragment(new SessionsActivity(0));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$36() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$37() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$38() {
            this.this$0.presentFragment(new CacheControlActivity());
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
            this.this$0.presentFragment(new DataUsageActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$43() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$44() {
            this.this$0.presentFragment(new DataAutoDownloadActivity(0));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$45() {
            this.this$0.presentFragment(new DataAutoDownloadActivity(1));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$46() {
            this.this$0.presentFragment(new DataAutoDownloadActivity(2));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$47() {
            this.this$0.presentFragment(new DataSettingsActivity());
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
            this.this$0.presentFragment(new ProxyListActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$57() {
            this.this$0.presentFragment(new ProxyListActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$58() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$59() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$60() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$61() {
            this.this$0.presentFragment(new WallpapersListActivity(0));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$62() {
            this.this$0.presentFragment(new WallpapersListActivity(1));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$63() {
            this.this$0.presentFragment(new WallpapersListActivity(0));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$64() {
            this.this$0.presentFragment(new ThemeActivity(1));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$65() {
            this.this$0.presentFragment(new ThemeActivity(0));
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
            this.this$0.presentFragment(new StickersActivity(0));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$74() {
            this.this$0.presentFragment(new StickersActivity(0));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$75() {
            this.this$0.presentFragment(new FeaturedStickersActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$76() {
            this.this$0.presentFragment(new StickersActivity(1));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$77() {
            this.this$0.presentFragment(new ArchivedStickersActivity(0));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$78() {
            this.this$0.presentFragment(new ArchivedStickersActivity(1));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$79() {
            this.this$0.presentFragment(new LanguageSelectActivity());
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$80() {
            ProfileActivity profileActivity = this.this$0;
            profileActivity.showDialog(AlertsCreator.createSupportAlert(profileActivity));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$81() {
            Browser.openUrl((Context) this.this$0.getParentActivity(), LocaleController.getString("TelegramFaqUrl", NUM));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$82() {
            Browser.openUrl((Context) this.this$0.getParentActivity(), LocaleController.getString("PrivacyPolicyUrl", NUM));
        }

        public SearchAdapter(ProfileActivity profileActivity, Context context) {
            SearchResult searchResult;
            String[] strArr;
            this.this$0 = profileActivity;
            SearchResult[] searchResultArr = new SearchResult[83];
            searchResultArr[0] = new SearchResult(this, 500, LocaleController.getString("EditName", NUM), 0, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda10(this));
            searchResultArr[1] = new SearchResult(this, 501, LocaleController.getString("ChangePhoneNumber", NUM), 0, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda11(this));
            searchResultArr[2] = new SearchResult(this, 502, LocaleController.getString("AddAnotherAccount", NUM), 0, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda32(this));
            searchResultArr[3] = new SearchResult(this, 503, LocaleController.getString("UserBio", NUM), 0, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda62(this));
            searchResultArr[4] = new SearchResult(this, 1, LocaleController.getString("NotificationsAndSounds", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda18(this));
            searchResultArr[5] = new SearchResult(this, 2, LocaleController.getString("NotificationsPrivateChats", NUM), LocaleController.getString("NotificationsAndSounds", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda7(this));
            searchResultArr[6] = new SearchResult(this, 3, LocaleController.getString("NotificationsGroups", NUM), LocaleController.getString("NotificationsAndSounds", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda22(this));
            searchResultArr[7] = new SearchResult(this, 4, LocaleController.getString("NotificationsChannels", NUM), LocaleController.getString("NotificationsAndSounds", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda41(this));
            searchResultArr[8] = new SearchResult(this, 5, LocaleController.getString("VoipNotificationSettings", NUM), "callsSectionRow", LocaleController.getString("NotificationsAndSounds", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda0(this));
            searchResultArr[9] = new SearchResult(this, 6, LocaleController.getString("BadgeNumber", NUM), "badgeNumberSection", LocaleController.getString("NotificationsAndSounds", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda3(this));
            searchResultArr[10] = new SearchResult(this, 7, LocaleController.getString("InAppNotifications", NUM), "inappSectionRow", LocaleController.getString("NotificationsAndSounds", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda63(this));
            searchResultArr[11] = new SearchResult(this, 8, LocaleController.getString("ContactJoined", NUM), "contactJoinedRow", LocaleController.getString("NotificationsAndSounds", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda51(this));
            searchResultArr[12] = new SearchResult(this, 9, LocaleController.getString("PinnedMessages", NUM), "pinnedMessageRow", LocaleController.getString("NotificationsAndSounds", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda61(this));
            searchResultArr[13] = new SearchResult(this, 10, LocaleController.getString("ResetAllNotifications", NUM), "resetNotificationsRow", LocaleController.getString("NotificationsAndSounds", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda13(this));
            searchResultArr[14] = new SearchResult(this, 100, LocaleController.getString("PrivacySettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda24(this));
            searchResultArr[15] = new SearchResult(this, 101, LocaleController.getString("BlockedUsers", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda74(this));
            searchResultArr[16] = new SearchResult(this, 105, LocaleController.getString("PrivacyPhone", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda71(this));
            searchResultArr[17] = new SearchResult(this, 102, LocaleController.getString("PrivacyLastSeen", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda26(this));
            searchResultArr[18] = new SearchResult(this, 103, LocaleController.getString("PrivacyProfilePhoto", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda54(this));
            searchResultArr[19] = new SearchResult(this, 104, LocaleController.getString("PrivacyForwards", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda77(this));
            searchResultArr[20] = new SearchResult(this, 122, LocaleController.getString("PrivacyP2P", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda60(this));
            searchResultArr[21] = new SearchResult(this, 106, LocaleController.getString("Calls", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda5(this));
            searchResultArr[22] = new SearchResult(this, 107, LocaleController.getString("GroupsAndChannels", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda29(this));
            searchResultArr[23] = new SearchResult(this, 108, LocaleController.getString("Passcode", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda23(this));
            searchResultArr[24] = new SearchResult(this, 109, LocaleController.getString("TwoStepVerification", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda69(this));
            searchResultArr[25] = new SearchResult(this, 110, LocaleController.getString("SessionsTitle", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda35(this));
            if (profileActivity.getMessagesController().autoarchiveAvailable) {
                searchResult = new SearchResult(this, 121, LocaleController.getString("ArchiveAndMute", NUM), "newChatsRow", LocaleController.getString("PrivacySettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda17(this));
            } else {
                searchResult = null;
            }
            searchResultArr[26] = searchResult;
            searchResultArr[27] = new SearchResult(this, 112, LocaleController.getString("DeleteAccountIfAwayFor2", NUM), "deleteAccountRow", LocaleController.getString("PrivacySettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda45(this));
            searchResultArr[28] = new SearchResult(this, 113, LocaleController.getString("PrivacyPaymentsClear", NUM), "paymentsClearRow", LocaleController.getString("PrivacySettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda78(this));
            searchResultArr[29] = new SearchResult(this, 114, LocaleController.getString("WebSessionsTitle", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda76(this));
            searchResultArr[30] = new SearchResult(this, 115, LocaleController.getString("SyncContactsDelete", NUM), "contactsDeleteRow", LocaleController.getString("PrivacySettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda57(this));
            searchResultArr[31] = new SearchResult(this, 116, LocaleController.getString("SyncContacts", NUM), "contactsSyncRow", LocaleController.getString("PrivacySettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda19(this));
            searchResultArr[32] = new SearchResult(this, 117, LocaleController.getString("SuggestContacts", NUM), "contactsSuggestRow", LocaleController.getString("PrivacySettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda73(this));
            searchResultArr[33] = new SearchResult(this, 118, LocaleController.getString("MapPreviewProvider", NUM), "secretMapRow", LocaleController.getString("PrivacySettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda55(this));
            searchResultArr[34] = new SearchResult(this, 119, LocaleController.getString("SecretWebPage", NUM), "secretWebpageRow", LocaleController.getString("PrivacySettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda40(this));
            searchResultArr[35] = new SearchResult(this, 120, LocaleController.getString("Devices", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda8(this));
            searchResultArr[36] = new SearchResult(this, 200, LocaleController.getString("DataSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda6(this));
            searchResultArr[37] = new SearchResult(this, 201, LocaleController.getString("DataUsage", NUM), "usageSectionRow", LocaleController.getString("DataSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda46(this));
            String str = "StorageUsage";
            searchResultArr[38] = new SearchResult(this, 202, LocaleController.getString("StorageUsage", NUM), LocaleController.getString("DataSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda27(this));
            searchResultArr[39] = new SearchResult(203, LocaleController.getString("KeepMedia", NUM), "keepMediaRow", LocaleController.getString("DataSettings", NUM), LocaleController.getString(str, NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda64(this));
            searchResultArr[40] = new SearchResult(204, LocaleController.getString("ClearMediaCache", NUM), "cacheRow", LocaleController.getString("DataSettings", NUM), LocaleController.getString(str, NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda15(this));
            searchResultArr[41] = new SearchResult(205, LocaleController.getString("LocalDatabase", NUM), "databaseRow", LocaleController.getString("DataSettings", NUM), LocaleController.getString(str, NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda1(this));
            searchResultArr[42] = new SearchResult(this, 206, LocaleController.getString("NetworkUsage", NUM), LocaleController.getString("DataSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda75(this));
            searchResultArr[43] = new SearchResult(this, 207, LocaleController.getString("AutomaticMediaDownload", NUM), "mediaDownloadSectionRow", LocaleController.getString("DataSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda48(this));
            searchResultArr[44] = new SearchResult(this, 208, LocaleController.getString("WhenUsingMobileData", NUM), LocaleController.getString("DataSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda21(this));
            searchResultArr[45] = new SearchResult(this, 209, LocaleController.getString("WhenConnectedOnWiFi", NUM), LocaleController.getString("DataSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda16(this));
            searchResultArr[46] = new SearchResult(this, 210, LocaleController.getString("WhenRoaming", NUM), LocaleController.getString("DataSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda28(this));
            searchResultArr[47] = new SearchResult(this, 211, LocaleController.getString("ResetAutomaticMediaDownload", NUM), "resetDownloadRow", LocaleController.getString("DataSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda65(this));
            searchResultArr[48] = new SearchResult(this, 212, LocaleController.getString("AutoplayMedia", NUM), "autoplayHeaderRow", LocaleController.getString("DataSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda2(this));
            searchResultArr[49] = new SearchResult(this, 213, LocaleController.getString("AutoplayGIF", NUM), "autoplayGifsRow", LocaleController.getString("DataSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda39(this));
            searchResultArr[50] = new SearchResult(this, 214, LocaleController.getString("AutoplayVideo", NUM), "autoplayVideoRow", LocaleController.getString("DataSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda59(this));
            searchResultArr[51] = new SearchResult(this, 215, LocaleController.getString("Streaming", NUM), "streamSectionRow", LocaleController.getString("DataSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda70(this));
            searchResultArr[52] = new SearchResult(this, 216, LocaleController.getString("EnableStreaming", NUM), "enableStreamRow", LocaleController.getString("DataSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda4(this));
            searchResultArr[53] = new SearchResult(this, 217, LocaleController.getString("Calls", NUM), "callsSectionRow", LocaleController.getString("DataSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda52(this));
            searchResultArr[54] = new SearchResult(this, 218, LocaleController.getString("VoipUseLessData", NUM), "useLessDataForCallsRow", LocaleController.getString("DataSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda43(this));
            searchResultArr[55] = new SearchResult(this, 219, LocaleController.getString("VoipQuickReplies", NUM), "quickRepliesRow", LocaleController.getString("DataSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda44(this));
            searchResultArr[56] = new SearchResult(this, 220, LocaleController.getString("ProxySettings", NUM), LocaleController.getString("DataSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda33(this));
            searchResultArr[57] = new SearchResult(221, LocaleController.getString("UseProxyForCalls", NUM), "callsRow", LocaleController.getString("DataSettings", NUM), LocaleController.getString("ProxySettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda36(this));
            searchResultArr[58] = new SearchResult(this, 111, LocaleController.getString("PrivacyDeleteCloudDrafts", NUM), "clearDraftsRow", LocaleController.getString("DataSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda80(this));
            searchResultArr[59] = new SearchResult(this, 300, LocaleController.getString("ChatSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda20(this));
            searchResultArr[60] = new SearchResult(this, 301, LocaleController.getString("TextSizeHeader", NUM), "textSizeHeaderRow", LocaleController.getString("ChatSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda14(this));
            searchResultArr[61] = new SearchResult(this, 302, LocaleController.getString("ChatBackground", NUM), LocaleController.getString("ChatSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda81(this));
            searchResultArr[62] = new SearchResult(303, LocaleController.getString("SetColor", NUM), (String) null, LocaleController.getString("ChatSettings", NUM), LocaleController.getString("ChatBackground", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda12(this));
            searchResultArr[63] = new SearchResult(304, LocaleController.getString("ResetChatBackgrounds", NUM), "resetRow", LocaleController.getString("ChatSettings", NUM), LocaleController.getString("ChatBackground", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda53(this));
            searchResultArr[64] = new SearchResult(this, 305, LocaleController.getString("AutoNightTheme", NUM), LocaleController.getString("ChatSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda50(this));
            searchResultArr[65] = new SearchResult(this, 306, LocaleController.getString("ColorTheme", NUM), "themeHeaderRow", LocaleController.getString("ChatSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda56(this));
            searchResultArr[66] = new SearchResult(this, 307, LocaleController.getString("ChromeCustomTabs", NUM), "customTabsRow", LocaleController.getString("ChatSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda34(this));
            searchResultArr[67] = new SearchResult(this, 308, LocaleController.getString("DirectShare", NUM), "directShareRow", LocaleController.getString("ChatSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda49(this));
            searchResultArr[68] = new SearchResult(this, 309, LocaleController.getString("EnableAnimations", NUM), "enableAnimationsRow", LocaleController.getString("ChatSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda9(this));
            searchResultArr[69] = new SearchResult(this, 310, LocaleController.getString("RaiseToSpeak", NUM), "raiseToSpeakRow", LocaleController.getString("ChatSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda38(this));
            searchResultArr[70] = new SearchResult(this, 311, LocaleController.getString("SendByEnter", NUM), "sendByEnterRow", LocaleController.getString("ChatSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda82(this));
            searchResultArr[71] = new SearchResult(this, 312, LocaleController.getString("SaveToGallerySettings", NUM), "saveToGalleryRow", LocaleController.getString("ChatSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda79(this));
            searchResultArr[72] = new SearchResult(this, 318, LocaleController.getString("DistanceUnits", NUM), "distanceRow", LocaleController.getString("ChatSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda25(this));
            searchResultArr[73] = new SearchResult(this, 313, LocaleController.getString("StickersAndMasks", NUM), LocaleController.getString("ChatSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda58(this));
            searchResultArr[74] = new SearchResult(314, LocaleController.getString("SuggestStickers", NUM), "suggestRow", LocaleController.getString("ChatSettings", NUM), LocaleController.getString("StickersAndMasks", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda37(this));
            searchResultArr[75] = new SearchResult(315, LocaleController.getString("FeaturedStickers", NUM), (String) null, LocaleController.getString("ChatSettings", NUM), LocaleController.getString("StickersAndMasks", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda30(this));
            searchResultArr[76] = new SearchResult(316, LocaleController.getString("Masks", NUM), (String) null, LocaleController.getString("ChatSettings", NUM), LocaleController.getString("StickersAndMasks", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda68(this));
            searchResultArr[77] = new SearchResult(317, LocaleController.getString("ArchivedStickers", NUM), (String) null, LocaleController.getString("ChatSettings", NUM), LocaleController.getString("StickersAndMasks", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda66(this));
            searchResultArr[78] = new SearchResult(317, LocaleController.getString("ArchivedMasks", NUM), (String) null, LocaleController.getString("ChatSettings", NUM), LocaleController.getString("StickersAndMasks", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda72(this));
            searchResultArr[79] = new SearchResult(this, 400, LocaleController.getString("Language", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda67(this));
            searchResultArr[80] = new SearchResult(this, 402, LocaleController.getString("AskAQuestion", NUM), LocaleController.getString("SettingsHelp", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda47(this));
            searchResultArr[81] = new SearchResult(this, 403, LocaleController.getString("TelegramFAQ", NUM), LocaleController.getString("SettingsHelp", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda42(this));
            searchResultArr[82] = new SearchResult(this, 404, LocaleController.getString("PrivacyPolicy", NUM), LocaleController.getString("SettingsHelp", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda31(this));
            this.searchArray = searchResultArr;
            this.faqSearchArray = new ArrayList<>();
            this.resultNames = new ArrayList<>();
            this.searchResults = new ArrayList<>();
            this.faqSearchResults = new ArrayList<>();
            this.recentSearches = new ArrayList<>();
            this.mContext = context;
            HashMap hashMap = new HashMap();
            int i = 0;
            while (true) {
                SearchResult[] searchResultArr2 = this.searchArray;
                if (i >= searchResultArr2.length) {
                    break;
                }
                if (searchResultArr2[i] != null) {
                    hashMap.put(Integer.valueOf(searchResultArr2[i].guid), this.searchArray[i]);
                }
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
                            if (readInt323 > 0) {
                                strArr = new String[readInt323];
                                int i2 = 0;
                                while (i2 < readInt323) {
                                    strArr[i2] = serializedData.readString(z);
                                    i2++;
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
            Collections.sort(this.recentSearches, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda86(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ int lambda$new$83(Object obj, Object obj2) {
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
                tLRPC$TL_messages_getWebPage.url = LocaleController.getString("TelegramFaqUrl", NUM);
                tLRPC$TL_messages_getWebPage.hash = 0;
                this.this$0.getConnectionsManager().sendRequest(tLRPC$TL_messages_getWebPage, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda87(this));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$loadFaqWebPage$85(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                                        arrayList.add(new MessagesController.FaqSearchResult(charSequence, str != null ? new String[]{LocaleController.getString("SettingsSearchFaq", NUM), str} : new String[]{LocaleController.getString("SettingsSearchFaq", NUM)}, url));
                                    }
                                }
                            }
                        } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockAnchor) {
                            break;
                        }
                    }
                    this.faqWebPage = tLRPC$WebPage;
                }
                AndroidUtilities.runOnUIThread(new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda85(this, arrayList));
            }
            this.loadingFaqPage = false;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$loadFaqWebPage$84(ArrayList arrayList) {
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
                            String access$25300 = searchResult.searchTitle;
                            String[] access$25200 = searchResult.path;
                            if (i >= this.recentSearches.size() - 1) {
                                z = false;
                            }
                            settingsSearchCell.setTextAndValue(access$25300, access$25200, false, z);
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
                    String[] access$252002 = searchResult2.path;
                    if (i >= this.searchResults.size() - 1) {
                        z = false;
                    }
                    settingsSearchCell.setTextAndValueAndIcon(charSequence, access$252002, i2, z);
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
                this.this$0.emptyView.title.setText(LocaleController.getString("SettingsNoRecent", NUM));
                notifyDataSetChanged();
                return;
            }
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            ProfileActivity$SearchAdapter$$ExternalSyntheticLambda83 profileActivity$SearchAdapter$$ExternalSyntheticLambda83 = new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda83(this, str);
            this.searchRunnable = profileActivity$SearchAdapter$$ExternalSyntheticLambda83;
            dispatchQueue.postRunnable(profileActivity$SearchAdapter$$ExternalSyntheticLambda83, 300);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$search$87(String str) {
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
            AndroidUtilities.runOnUIThread(new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda84(this, str, arrayList, arrayList2, arrayList3));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$search$86(String str, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3) {
            if (str.equals(this.lastSearchString)) {
                if (!this.searchWas) {
                    this.this$0.emptyView.stickerView.getImageReceiver().startAnimation();
                    this.this$0.emptyView.title.setText(LocaleController.getString("SettingsNoResults", NUM));
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
        valueAnimator.addUpdateListener(new ProfileActivity$$ExternalSyntheticLambda1(this));
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
    public /* synthetic */ void lambda$dimBehindView$35(ValueAnimator valueAnimator) {
        this.scrimPaint.setAlpha((int) (((Float) valueAnimator.getAnimatedValue()).floatValue() * 255.0f));
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ProfileActivity$$ExternalSyntheticLambda33 profileActivity$$ExternalSyntheticLambda33 = new ProfileActivity$$ExternalSyntheticLambda33(this);
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
        ProfileActivity$$ExternalSyntheticLambda33 profileActivity$$ExternalSyntheticLambda332 = profileActivity$$ExternalSyntheticLambda33;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileActivity$$ExternalSyntheticLambda332, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileActivity$$ExternalSyntheticLambda332, "avatar_actionBarSelectorBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileActivity$$ExternalSyntheticLambda332, "chat_lockIcon"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileActivity$$ExternalSyntheticLambda332, "avatar_subtitleInProfileBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileActivity$$ExternalSyntheticLambda332, "avatar_backgroundActionBarBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileActivity$$ExternalSyntheticLambda332, "profile_title"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileActivity$$ExternalSyntheticLambda332, "profile_status"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileActivity$$ExternalSyntheticLambda332, "avatar_subtitleInProfileBlue"));
        if (this.mediaCounterTextView != null) {
            ProfileActivity$$ExternalSyntheticLambda33 profileActivity$$ExternalSyntheticLambda333 = profileActivity$$ExternalSyntheticLambda33;
            arrayList.add(new ThemeDescription(this.mediaCounterTextView.getTextView(), ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, profileActivity$$ExternalSyntheticLambda333, "player_actionBarSubtitle"));
            arrayList.add(new ThemeDescription(this.mediaCounterTextView.getNextTextView(), ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, profileActivity$$ExternalSyntheticLambda333, "player_actionBarSubtitle"));
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
        ProfileActivity$$ExternalSyntheticLambda33 profileActivity$$ExternalSyntheticLambda334 = profileActivity$$ExternalSyntheticLambda33;
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) profileActivity$$ExternalSyntheticLambda334, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) profileActivity$$ExternalSyntheticLambda334, "windowBackgroundWhiteBlueText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        ProfileActivity$$ExternalSyntheticLambda33 profileActivity$$ExternalSyntheticLambda335 = profileActivity$$ExternalSyntheticLambda33;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileActivity$$ExternalSyntheticLambda335, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileActivity$$ExternalSyntheticLambda335, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileActivity$$ExternalSyntheticLambda335, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileActivity$$ExternalSyntheticLambda335, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileActivity$$ExternalSyntheticLambda335, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileActivity$$ExternalSyntheticLambda335, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, profileActivity$$ExternalSyntheticLambda335, "avatar_backgroundPink"));
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
    public /* synthetic */ void lambda$getThemeDescriptions$36() {
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
                    this.onlineTextView[1].setTextColor(Theme.getColor((String) tag));
                } else {
                    this.onlineTextView[1].setTextColor(Theme.getColor("avatar_subtitleInProfileBlue"));
                }
            }
            Drawable drawable = this.lockIconDrawable;
            if (drawable != null) {
                drawable.setColorFilter(Theme.getColor("chat_lockIcon"), PorterDuff.Mode.MULTIPLY);
            }
            ScamDrawable scamDrawable2 = this.scamDrawable;
            if (scamDrawable2 != null) {
                scamDrawable2.setColor(Theme.getColor("avatar_subtitleInProfileBlue"));
            }
            SimpleTextView[] simpleTextViewArr2 = this.nameTextView;
            if (simpleTextViewArr2[1] != null) {
                simpleTextViewArr2[1].setTextColor(Theme.getColor("profile_title"));
            }
            ActionBar actionBar = this.actionBar;
            if (actionBar != null) {
                actionBar.setItemsColor(Theme.getColor("actionBarDefaultIcon"), false);
                this.actionBar.setItemsBackgroundColor(Theme.getColor("avatar_actionBarSelectorBlue"), false);
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
            Drawable mutate = this.fragmentView.getContext().getResources().getDrawable(NUM).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(mutate, Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("profile_actionBackground"), Theme.getColor("profile_actionPressedBackground")), 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            this.writeButton.setBackground(combinedDrawable);
        } catch (Exception unused) {
        }
    }

    private boolean isQrNeedVisible() {
        return !TextUtils.isEmpty(getUserConfig().getCurrentUser().username);
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
            put(15, ProfileActivity.this.privacyRow, sparseIntArray);
            put(16, ProfileActivity.this.dataRow, sparseIntArray);
            put(17, ProfileActivity.this.chatRow, sparseIntArray);
            put(18, ProfileActivity.this.filtersRow, sparseIntArray);
            put(19, ProfileActivity.this.devicesRow, sparseIntArray);
            put(20, ProfileActivity.this.devicesSectionRow, sparseIntArray);
            put(21, ProfileActivity.this.helpHeaderRow, sparseIntArray);
            put(22, ProfileActivity.this.questionRow, sparseIntArray);
            put(23, ProfileActivity.this.faqRow, sparseIntArray);
            put(24, ProfileActivity.this.policyRow, sparseIntArray);
            put(25, ProfileActivity.this.helpSectionCell, sparseIntArray);
            put(26, ProfileActivity.this.debugHeaderRow, sparseIntArray);
            put(27, ProfileActivity.this.sendLogsRow, sparseIntArray);
            put(28, ProfileActivity.this.sendLastLogsRow, sparseIntArray);
            put(29, ProfileActivity.this.clearLogsRow, sparseIntArray);
            put(30, ProfileActivity.this.switchBackendRow, sparseIntArray);
            put(31, ProfileActivity.this.versionRow, sparseIntArray);
            put(32, ProfileActivity.this.emptyRow, sparseIntArray);
            put(33, ProfileActivity.this.bottomPaddingRow, sparseIntArray);
            put(34, ProfileActivity.this.infoHeaderRow, sparseIntArray);
            put(35, ProfileActivity.this.phoneRow, sparseIntArray);
            put(36, ProfileActivity.this.locationRow, sparseIntArray);
            put(37, ProfileActivity.this.userInfoRow, sparseIntArray);
            put(38, ProfileActivity.this.channelInfoRow, sparseIntArray);
            put(39, ProfileActivity.this.usernameRow, sparseIntArray);
            put(40, ProfileActivity.this.notificationsDividerRow, sparseIntArray);
            put(41, ProfileActivity.this.notificationsRow, sparseIntArray);
            put(42, ProfileActivity.this.infoSectionRow, sparseIntArray);
            put(43, ProfileActivity.this.sendMessageRow, sparseIntArray);
            put(44, ProfileActivity.this.reportRow, sparseIntArray);
            put(45, ProfileActivity.this.settingsTimerRow, sparseIntArray);
            put(46, ProfileActivity.this.settingsKeyRow, sparseIntArray);
            put(47, ProfileActivity.this.secretSettingsSectionRow, sparseIntArray);
            put(48, ProfileActivity.this.membersHeaderRow, sparseIntArray);
            put(49, ProfileActivity.this.addMemberRow, sparseIntArray);
            put(50, ProfileActivity.this.subscribersRow, sparseIntArray);
            put(51, ProfileActivity.this.subscribersRequestsRow, sparseIntArray);
            put(52, ProfileActivity.this.administratorsRow, sparseIntArray);
            put(53, ProfileActivity.this.blockedUsersRow, sparseIntArray);
            put(54, ProfileActivity.this.membersSectionRow, sparseIntArray);
            put(55, ProfileActivity.this.sharedMediaRow, sparseIntArray);
            put(56, ProfileActivity.this.unblockRow, sparseIntArray);
            put(57, ProfileActivity.this.addToGroupButtonRow, sparseIntArray);
            put(58, ProfileActivity.this.addToGroupInfoRow, sparseIntArray);
            put(59, ProfileActivity.this.joinRow, sparseIntArray);
            put(60, ProfileActivity.this.lastSectionRow, sparseIntArray);
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
            i = Theme.getColor("actionBarActionModeDefault");
        } else if (this.mediaHeaderVisible) {
            i = Theme.getColor("windowBackgroundWhite");
        } else {
            i = Theme.getColor("actionBarDefault");
        }
        if (ColorUtils.calculateLuminance(i) > 0.699999988079071d) {
            return true;
        }
        return false;
    }
}
