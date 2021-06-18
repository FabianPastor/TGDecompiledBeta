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
import android.content.res.Configuration;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
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
import android.util.Property;
import android.util.SparseArray;
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
import j$.util.Comparator;
import j$.util.function.Function;
import j$.util.function.ToDoubleFunction;
import j$.util.function.ToIntFunction;
import j$.util.function.ToLongFunction;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
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
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.RequestDelegate;
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
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
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
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CrossfadeDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.ImageUpdater;
import org.telegram.ui.Components.ProfileGalleryView;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScamDrawable;
import org.telegram.ui.Components.SharedMediaLayout;
import org.telegram.ui.Components.StickerEmptyView;
import org.telegram.ui.Components.TimerDrawable;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.GroupCreateActivity;
import org.telegram.ui.NotificationsSettingsActivity;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.ProfileActivity;

public class ProfileActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, DialogsActivity.DialogsActivityDelegate, SharedMediaLayout.SharedMediaPreloaderDelegate, ImageUpdater.ImageUpdaterDelegate {
    private Property<ActionBar, Float> ACTIONBAR_HEADER_PROGRESS;
    /* access modifiers changed from: private */
    public final Property<ProfileActivity, Float> HEADER_SHADOW;
    /* access modifiers changed from: private */
    public int addMemberRow;
    /* access modifiers changed from: private */
    public int administratorsRow;
    private boolean allowProfileAnimation;
    /* access modifiers changed from: private */
    public boolean allowPullingDown;
    /* access modifiers changed from: private */
    public ActionBarMenuItem animatingItem;
    private float animationProgress;
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
    public int banFromGroup;
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
    public int channelInfoRow;
    /* access modifiers changed from: private */
    public TLRPC$ChatFull chatInfo;
    /* access modifiers changed from: private */
    public int chatRow;
    /* access modifiers changed from: private */
    public int chat_id;
    /* access modifiers changed from: private */
    public int clearLogsRow;
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
    public long dialog_id;
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
    public SparseArray<TLRPC$ChatParticipant> participantsMap;
    /* access modifiers changed from: private */
    public int phoneRow;
    PinchToZoomHelper pinchToZoomHelper;
    /* access modifiers changed from: private */
    public int playProfileAnimation;
    /* access modifiers changed from: private */
    public int policyRow;
    /* access modifiers changed from: private */
    public HashMap<Integer, Integer> positionToOffset;
    private ImageLocation prevLoadedImageLocation;
    /* access modifiers changed from: private */
    public int privacyRow;
    boolean profileTransitionInProgress;
    private PhotoViewer.PhotoViewerProvider provider;
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
    private int selectedUser;
    /* access modifiers changed from: private */
    public int sendLastLogsRow;
    /* access modifiers changed from: private */
    public int sendLogsRow;
    /* access modifiers changed from: private */
    public int sendMessageRow;
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
    public int unblockRow;
    /* access modifiers changed from: private */
    public UndoView undoView;
    private ImageLocation uploadingImageLocation;
    /* access modifiers changed from: private */
    public boolean userBlocked;
    /* access modifiers changed from: private */
    public TLRPC$UserFull userInfo;
    /* access modifiers changed from: private */
    public int userInfoRow;
    /* access modifiers changed from: private */
    public int user_id;
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

    static /* synthetic */ int access$7212(ProfileActivity profileActivity, int i) {
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
            float access$700 = ProfileActivity.this.extraHeight + ((float) (ActionBar.getCurrentActionBarHeight() + (ProfileActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0))) + ((float) ProfileActivity.this.searchTransitionOffset);
            int access$900 = (int) ((1.0f - ProfileActivity.this.mediaHeaderAnimationProgress) * access$700);
            if (access$900 != 0) {
                this.paint.setColor(this.currentColor);
                canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) access$900, this.paint);
            }
            float f = (float) access$900;
            if (f != access$700) {
                this.paint.setColor(Theme.getColor("windowBackgroundWhite"));
                canvas.drawRect(0.0f, f, (float) getMeasuredWidth(), access$700, this.paint);
            }
            if (ProfileActivity.this.parentLayout != null) {
                ProfileActivity.this.parentLayout.drawHeaderShadow(canvas, (int) (ProfileActivity.this.headerShadowAlpha * 255.0f), (int) access$700);
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
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ProfileActivity.OverlaysView.this.lambda$new$0$ProfileActivity$OverlaysView(valueAnimator);
                }
            });
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
        /* renamed from: lambda$new$0 */
        public /* synthetic */ void lambda$new$0$ProfileActivity$OverlaysView(ValueAnimator valueAnimator) {
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
        /* JADX WARNING: Removed duplicated region for block: B:68:0x0222  */
        /* JADX WARNING: Removed duplicated region for block: B:72:0x024a  */
        /* JADX WARNING: Removed duplicated region for block: B:75:0x0264  */
        /* JADX WARNING: Removed duplicated region for block: B:76:0x0267  */
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
                if (r3 <= r8) goto L_0x02dd
                r12 = 20
                if (r3 > r12) goto L_0x02dd
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
                int r15 = (int) r15
                r12.setAlpha(r15)
            L_0x00c4:
                int r12 = r21.getMeasuredWidth()
                r15 = 1092616192(0x41200000, float:10.0)
                int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
                int r12 = r12 - r15
                int r15 = r3 + -1
                int r15 = r15 * 2
                float r15 = (float) r15
                int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
                int r12 = r12 - r15
                int r12 = r12 / r3
                r15 = 1082130432(0x40800000, float:4.0)
                int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
                int r2 = android.os.Build.VERSION.SDK_INT
                r8 = 21
                if (r2 < r8) goto L_0x00f1
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                boolean r2 = r2.inBubbleMode
                if (r2 != 0) goto L_0x00f1
                int r2 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                goto L_0x00f2
            L_0x00f1:
                r2 = 0
            L_0x00f2:
                int r15 = r15 + r2
                r2 = 0
                r8 = 0
            L_0x00f5:
                r16 = 1140457472(0x43fa0000, float:500.0)
                if (r2 >= r3) goto L_0x027a
                int r17 = r2 * 2
                int r7 = r17 + 5
                float r7 = (float) r7
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
                int r17 = r12 * r2
                int r7 = r7 + r17
                r17 = 85
                int r5 = r0.previousSelectedPotision
                r18 = 80
                r19 = 1073741824(0x40000000, float:2.0)
                if (r2 != r5) goto L_0x0169
                float r5 = r0.previousSelectedProgress
                float r5 = r5 - r11
                float r5 = java.lang.Math.abs(r5)
                r20 = 953267991(0x38d1b717, float:1.0E-4)
                int r5 = (r5 > r20 ? 1 : (r5 == r20 ? 0 : -1))
                if (r5 <= 0) goto L_0x0169
                float r5 = r0.previousSelectedProgress
                r22.save()
                float r8 = (float) r7
                float r4 = (float) r12
                float r4 = r4 * r5
                float r4 = r4 + r8
                float r13 = (float) r15
                int r6 = r7 + r12
                float r6 = (float) r6
                int r16 = org.telegram.messenger.AndroidUtilities.dp(r19)
                int r11 = r15 + r16
                float r11 = (float) r11
                r1.clipRect(r4, r13, r6, r11)
                android.graphics.RectF r4 = r0.rect
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r19)
                int r11 = r11 + r15
                float r11 = (float) r11
                r4.set(r8, r13, r6, r11)
                android.graphics.Paint r4 = r0.barPaint
                float r6 = r0.alpha
                float r6 = r6 * r14
                int r6 = (int) r6
                r4.setAlpha(r6)
                android.graphics.RectF r4 = r0.rect
                r6 = 1065353216(0x3var_, float:1.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r6)
                float r8 = (float) r8
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r6)
                float r6 = (float) r11
                android.graphics.Paint r11 = r0.barPaint
                r1.drawRoundRect(r4, r8, r6, r11)
                r22.restore()
                r13 = r15
                r4 = 80
                r6 = 1118437376(0x42aa0000, float:85.0)
            L_0x0166:
                r8 = 1
                goto L_0x020d
            L_0x0169:
                int r4 = r0.selectedPosition
                if (r2 != r4) goto L_0x0206
                org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                org.telegram.ui.Components.ProfileGalleryView r4 = r4.avatarsViewPager
                boolean r4 = r4.isCurrentItemVideo()
                if (r4 == 0) goto L_0x01fe
                org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                org.telegram.ui.Components.ProfileGalleryView r4 = r4.avatarsViewPager
                float r5 = r4.getCurrentItemProgress()
                r0.currentProgress = r5
                r4 = 0
                int r6 = (r5 > r4 ? 1 : (r5 == r4 ? 0 : -1))
                if (r6 > 0) goto L_0x0196
                org.telegram.ui.ProfileActivity r6 = org.telegram.ui.ProfileActivity.this
                org.telegram.ui.Components.ProfileGalleryView r6 = r6.avatarsViewPager
                boolean r6 = r6.isLoadingCurrentVideo()
                if (r6 != 0) goto L_0x019c
            L_0x0196:
                float r6 = r0.currentLoadingAnimationProgress
                int r6 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1))
                if (r6 <= 0) goto L_0x01c3
            L_0x019c:
                float r4 = r0.currentLoadingAnimationProgress
                int r6 = r0.currentLoadingAnimationDirection
                r13 = r15
                long r14 = (long) r6
                long r14 = r14 * r9
                float r8 = (float) r14
                float r8 = r8 / r16
                float r4 = r4 + r8
                r0.currentLoadingAnimationProgress = r4
                r8 = 1065353216(0x3var_, float:1.0)
                int r14 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
                if (r14 <= 0) goto L_0x01b7
                r0.currentLoadingAnimationProgress = r8
                int r6 = r6 * -1
                r0.currentLoadingAnimationDirection = r6
                goto L_0x01c4
            L_0x01b7:
                r8 = 0
                int r4 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
                if (r4 > 0) goto L_0x01c4
                r0.currentLoadingAnimationProgress = r8
                int r6 = r6 * -1
                r0.currentLoadingAnimationDirection = r6
                goto L_0x01c4
            L_0x01c3:
                r13 = r15
            L_0x01c4:
                android.graphics.RectF r4 = r0.rect
                float r6 = (float) r7
                float r8 = (float) r13
                int r14 = r7 + r12
                float r14 = (float) r14
                int r15 = org.telegram.messenger.AndroidUtilities.dp(r19)
                int r15 = r15 + r13
                float r15 = (float) r15
                r4.set(r6, r8, r14, r15)
                android.graphics.Paint r4 = r0.barPaint
                r6 = 1111490560(0x42400000, float:48.0)
                float r8 = r0.currentLoadingAnimationProgress
                float r8 = r8 * r6
                r6 = 1118437376(0x42aa0000, float:85.0)
                float r8 = r8 + r6
                float r11 = r0.alpha
                float r8 = r8 * r11
                int r8 = (int) r8
                r4.setAlpha(r8)
                android.graphics.RectF r4 = r0.rect
                r11 = 1065353216(0x3var_, float:1.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r11)
                float r8 = (float) r8
                int r14 = org.telegram.messenger.AndroidUtilities.dp(r11)
                float r14 = (float) r14
                android.graphics.Paint r15 = r0.barPaint
                r1.drawRoundRect(r4, r8, r14, r15)
                r4 = 80
                goto L_0x0166
            L_0x01fe:
                r13 = r15
                r6 = 1118437376(0x42aa0000, float:85.0)
                r11 = 1065353216(0x3var_, float:1.0)
                r0.currentProgress = r11
                goto L_0x0209
            L_0x0206:
                r13 = r15
                r6 = 1118437376(0x42aa0000, float:85.0)
            L_0x0209:
                r4 = 85
                r5 = 1065353216(0x3var_, float:1.0)
            L_0x020d:
                android.graphics.RectF r11 = r0.rect
                float r7 = (float) r7
                float r14 = (float) r13
                float r15 = (float) r12
                float r15 = r15 * r5
                float r15 = r15 + r7
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r19)
                int r5 = r5 + r13
                float r5 = (float) r5
                r11.set(r7, r14, r15, r5)
                int r5 = r0.selectedPosition
                if (r2 == r5) goto L_0x024a
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                int r5 = r5.overlayCountVisible
                r7 = 3
                if (r5 != r7) goto L_0x0247
                android.graphics.Paint r5 = r0.barPaint
                float r4 = (float) r4
                org.telegram.ui.Components.CubicBezierInterpolator r7 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
                float[] r11 = r0.alphas
                r11 = r11[r2]
                float r7 = r7.getInterpolation(r11)
                r11 = 1132396544(0x437var_, float:255.0)
                float r4 = org.telegram.messenger.AndroidUtilities.lerp(r4, r11, r7)
                float r7 = r0.alpha
                float r4 = r4 * r7
                int r4 = (int) r4
                r5.setAlpha(r4)
                goto L_0x0252
            L_0x0247:
                r11 = 1132396544(0x437var_, float:255.0)
                goto L_0x0252
            L_0x024a:
                r11 = 1132396544(0x437var_, float:255.0)
                float[] r4 = r0.alphas
                r5 = 1061158912(0x3var_, float:0.75)
                r4[r2] = r5
            L_0x0252:
                android.graphics.RectF r4 = r0.rect
                r5 = 1065353216(0x3var_, float:1.0)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r5)
                float r7 = (float) r7
                int r14 = org.telegram.messenger.AndroidUtilities.dp(r5)
                float r5 = (float) r14
                int r14 = r0.selectedPosition
                if (r2 != r14) goto L_0x0267
                android.graphics.Paint r14 = r0.selectedBarPaint
                goto L_0x0269
            L_0x0267:
                android.graphics.Paint r14 = r0.barPaint
            L_0x0269:
                r1.drawRoundRect(r4, r7, r5, r14)
                int r2 = r2 + 1
                r15 = r13
                r4 = 1132396544(0x437var_, float:255.0)
                r5 = 2
                r6 = 0
                r11 = 1065353216(0x3var_, float:1.0)
                r13 = 3
                r14 = 1118437376(0x42aa0000, float:85.0)
                goto L_0x00f5
            L_0x027a:
                org.telegram.ui.ProfileActivity r1 = org.telegram.ui.ProfileActivity.this
                int r1 = r1.overlayCountVisible
                r2 = 2
                if (r1 != r2) goto L_0x02a1
                float r1 = r0.alpha
                r2 = 1065353216(0x3var_, float:1.0)
                int r3 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r3 >= 0) goto L_0x029a
                float r3 = (float) r9
                r4 = 1127481344(0x43340000, float:180.0)
                float r3 = r3 / r4
                float r1 = r1 + r3
                r0.alpha = r1
                int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r1 <= 0) goto L_0x0298
                r0.alpha = r2
            L_0x0298:
                r8 = 1
                goto L_0x02de
            L_0x029a:
                org.telegram.ui.ProfileActivity r1 = org.telegram.ui.ProfileActivity.this
                r2 = 3
                int unused = r1.overlayCountVisible = r2
                goto L_0x02de
            L_0x02a1:
                r2 = 3
                org.telegram.ui.ProfileActivity r1 = org.telegram.ui.ProfileActivity.this
                int r1 = r1.overlayCountVisible
                if (r1 != r2) goto L_0x02de
                r1 = 0
            L_0x02ab:
                float[] r2 = r0.alphas
                int r3 = r2.length
                if (r1 >= r3) goto L_0x02de
                int r3 = r0.selectedPosition
                r4 = -1
                if (r1 == r3) goto L_0x02d4
                r3 = r2[r1]
                r5 = 0
                int r3 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
                if (r3 <= 0) goto L_0x02d4
                r3 = r2[r1]
                float r6 = (float) r9
                float r6 = r6 / r16
                float r3 = r3 - r6
                r2[r1] = r3
                r3 = r2[r1]
                int r3 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
                if (r3 > 0) goto L_0x02d2
                r2[r1] = r5
                int r2 = r0.previousSelectedPotision
                if (r1 != r2) goto L_0x02d2
                r0.previousSelectedPotision = r4
            L_0x02d2:
                r8 = 1
                goto L_0x02da
            L_0x02d4:
                int r2 = r0.previousSelectedPotision
                if (r1 != r2) goto L_0x02da
                r0.previousSelectedPotision = r4
            L_0x02da:
                int r1 = r1 + 1
                goto L_0x02ab
            L_0x02dd:
                r8 = 0
            L_0x02de:
                r1 = 2
                r2 = 0
            L_0x02e0:
                if (r2 >= r1) goto L_0x032a
                boolean[] r3 = r0.pressedOverlayVisible
                boolean r3 = r3[r2]
                if (r3 == 0) goto L_0x030a
                float[] r3 = r0.pressedOverlayAlpha
                r4 = r3[r2]
                r5 = 1065353216(0x3var_, float:1.0)
                int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
                if (r4 >= 0) goto L_0x0308
                r4 = r3[r2]
                float r6 = (float) r9
                r7 = 1127481344(0x43340000, float:180.0)
                float r6 = r6 / r7
                float r4 = r4 + r6
                r3[r2] = r4
                r4 = r3[r2]
                int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
                if (r4 <= 0) goto L_0x0303
                r3[r2] = r5
            L_0x0303:
                r6 = 0
                r8 = 1
            L_0x0305:
                r11 = 1127481344(0x43340000, float:180.0)
                goto L_0x0327
            L_0x0308:
                r6 = 0
                goto L_0x0305
            L_0x030a:
                r5 = 1065353216(0x3var_, float:1.0)
                float[] r3 = r0.pressedOverlayAlpha
                r4 = r3[r2]
                r6 = 0
                int r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
                if (r4 <= 0) goto L_0x0305
                r4 = r3[r2]
                float r7 = (float) r9
                r11 = 1127481344(0x43340000, float:180.0)
                float r7 = r7 / r11
                float r4 = r4 - r7
                r3[r2] = r4
                r4 = r3[r2]
                int r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
                if (r4 >= 0) goto L_0x0326
                r3[r2] = r6
            L_0x0326:
                r8 = 1
            L_0x0327:
                int r2 = r2 + 1
                goto L_0x02e0
            L_0x032a:
                if (r8 == 0) goto L_0x032f
                r21.postInvalidateOnAnimation()
            L_0x032f:
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

    private class NestedFrameLayout extends FrameLayout implements NestedScrollingParent3 {
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
                            iArr[1] = iArr[1] - Math.min(iArr[1], i2);
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
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ProfileActivity.PagerIndicatorView.this.lambda$new$0$ProfileActivity$PagerIndicatorView(valueAnimator);
                }
            });
            final boolean access$2400 = ProfileActivity.this.expandPhoto;
            ofFloat.addListener(new AnimatorListenerAdapter(ProfileActivity.this) {
                public void onAnimationEnd(Animator animator) {
                    if (PagerIndicatorView.this.isIndicatorVisible) {
                        if (ProfileActivity.this.searchItem != null) {
                            ProfileActivity.this.searchItem.setVisibility(8);
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
                    if (ProfileActivity.this.searchItem != null && !access$2400) {
                        ProfileActivity.this.searchItem.setVisibility(0);
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
        /* renamed from: lambda$new$0 */
        public /* synthetic */ void lambda$new$0$ProfileActivity$PagerIndicatorView(ValueAnimator valueAnimator) {
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
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public final void run() {
                            ProfileActivity.PagerIndicatorView.this.updateAvatarItemsInternal();
                        }
                    }, 500);
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
            this.indicatorRect.right = (float) (getMeasuredWidth() - AndroidUtilities.dp(54.0f));
            RectF rectF = this.indicatorRect;
            rectF.left = rectF.right - (measureText + AndroidUtilities.dpf2(16.0f));
            this.indicatorRect.top = (float) ((ProfileActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + AndroidUtilities.dp(15.0f));
            RectF rectF2 = this.indicatorRect;
            rectF2.bottom = rectF2.top + ((float) AndroidUtilities.dp(26.0f));
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
        this.isOnline = new boolean[1];
        this.headerShadowAlpha = 1.0f;
        this.participantsMap = new SparseArray<>();
        this.allowProfileAnimation = true;
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
                if (ProfileActivity.this.user_id == 0 ? ProfileActivity.this.chat_id == 0 || (chat = ProfileActivity.this.getMessagesController().getChat(Integer.valueOf(ProfileActivity.this.chat_id))) == null || (tLRPC$ChatPhoto = chat.photo) == null || (tLRPC$FileLocation2 = tLRPC$ChatPhoto.photo_big) == null : (user = ProfileActivity.this.getMessagesController().getUser(Integer.valueOf(ProfileActivity.this.user_id))) == null || (tLRPC$UserProfilePhoto = user.photo) == null || (tLRPC$FileLocation2 = tLRPC$UserProfilePhoto.photo_big) == null) {
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
                if (ProfileActivity.this.user_id != 0) {
                    placeProviderObject.dialogId = ProfileActivity.this.user_id;
                } else if (ProfileActivity.this.chat_id != 0) {
                    placeProviderObject.dialogId = -ProfileActivity.this.chat_id;
                }
                placeProviderObject.thumb = placeProviderObject.imageReceiver.getBitmapSafe();
                placeProviderObject.size = -1;
                placeProviderObject.radius = ProfileActivity.this.avatarImage.getImageReceiver().getRoundRadius();
                placeProviderObject.scale = ProfileActivity.this.avatarContainer.getScaleX();
                if (ProfileActivity.this.user_id == ProfileActivity.this.getUserConfig().clientUserId) {
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
        this.savedScrollPosition = -1;
        this.sharedMediaPreloader = sharedMediaPreloader2;
    }

    public boolean onFragmentCreate() {
        this.user_id = this.arguments.getInt("user_id", 0);
        this.chat_id = this.arguments.getInt("chat_id", 0);
        this.banFromGroup = this.arguments.getInt("ban_chat_id", 0);
        this.reportSpam = this.arguments.getBoolean("reportSpam", false);
        if (!this.expandPhoto) {
            boolean z = this.arguments.getBoolean("expandPhoto", false);
            this.expandPhoto = z;
            if (z) {
                this.needSendMessage = true;
            }
        }
        if (this.user_id != 0) {
            long j = this.arguments.getLong("dialog_id", 0);
            this.dialog_id = j;
            if (j != 0) {
                this.currentEncryptedChat = getMessagesController().getEncryptedChat(Integer.valueOf((int) (this.dialog_id >> 32)));
            }
            TLRPC$User user = getMessagesController().getUser(Integer.valueOf(this.user_id));
            if (user == null) {
                return false;
            }
            getNotificationCenter().addObserver(this, NotificationCenter.contactsDidLoad);
            getNotificationCenter().addObserver(this, NotificationCenter.encryptedChatCreated);
            getNotificationCenter().addObserver(this, NotificationCenter.encryptedChatUpdated);
            getNotificationCenter().addObserver(this, NotificationCenter.blockedUsersDidLoad);
            getNotificationCenter().addObserver(this, NotificationCenter.botInfoDidLoad);
            getNotificationCenter().addObserver(this, NotificationCenter.userInfoDidLoad);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.reloadInterface);
            this.userBlocked = getMessagesController().blockePeers.indexOfKey(this.user_id) >= 0;
            if (user.bot) {
                this.isBot = true;
                MediaDataController mediaDataController = getMediaDataController();
                int i = user.id;
                mediaDataController.loadBotInfo(i, (long) i, true, this.classGuid);
            }
            this.userInfo = getMessagesController().getUserFull(this.user_id);
            getMessagesController().loadFullUser(getMessagesController().getUser(Integer.valueOf(this.user_id)), this.classGuid, true);
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
        } else if (this.chat_id == 0) {
            return false;
        } else {
            TLRPC$Chat chat = getMessagesController().getChat(Integer.valueOf(this.chat_id));
            this.currentChat = chat;
            if (chat == null) {
                CountDownLatch countDownLatch = new CountDownLatch(1);
                getMessagesStorage().getStorageQueue().postRunnable(new Runnable(countDownLatch) {
                    public final /* synthetic */ CountDownLatch f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        ProfileActivity.this.lambda$onFragmentCreate$0$ProfileActivity(this.f$1);
                    }
                });
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
                this.chatInfo = getMessagesController().getChatFull(this.chat_id);
            }
            if (ChatObject.isChannel(this.currentChat)) {
                getMessagesController().loadFullChat(this.chat_id, this.classGuid, true);
            } else if (this.chatInfo == null) {
                this.chatInfo = getMessagesStorage().loadChatInfo(this.chat_id, false, (CountDownLatch) null, false, false);
            }
        }
        if (this.sharedMediaPreloader == null) {
            this.sharedMediaPreloader = new SharedMediaLayout.SharedMediaPreloader(this);
        }
        this.sharedMediaPreloader.addDelegate(this);
        getNotificationCenter().addObserver(this, NotificationCenter.updateInterfaces);
        getNotificationCenter().addObserver(this, NotificationCenter.didReceiveNewMessages);
        getNotificationCenter().addObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
        updateRowsIds();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
        if (this.arguments.containsKey("preload_messages")) {
            getMessagesController().ensureMessagesLoaded((long) this.user_id, 0, (MessagesController.MessagesLoadedCallback) null);
        }
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onFragmentCreate$0 */
    public /* synthetic */ void lambda$onFragmentCreate$0$ProfileActivity(CountDownLatch countDownLatch) {
        this.currentChat = getMessagesStorage().getChat(this.chat_id);
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
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
        ProfileGalleryView profileGalleryView = this.avatarsViewPager;
        if (profileGalleryView != null) {
            profileGalleryView.onDestroy();
        }
        if (this.user_id != 0) {
            getNotificationCenter().removeObserver(this, NotificationCenter.contactsDidLoad);
            getNotificationCenter().removeObserver(this, NotificationCenter.encryptedChatCreated);
            getNotificationCenter().removeObserver(this, NotificationCenter.encryptedChatUpdated);
            getNotificationCenter().removeObserver(this, NotificationCenter.blockedUsersDidLoad);
            getNotificationCenter().removeObserver(this, NotificationCenter.botInfoDidLoad);
            getNotificationCenter().removeObserver(this, NotificationCenter.userInfoDidLoad);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.reloadInterface);
            getMessagesController().cancelLoadFullUser(this.user_id);
        } else if (this.chat_id != 0) {
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
        AnonymousClass3 r0 = new ActionBar(context) {
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
        r0.setItemsBackgroundColor(AvatarDrawable.getButtonColorForId((this.user_id != 0 || (ChatObject.isChannel(this.chat_id, this.currentAccount) && !this.currentChat.megagroup)) ? 5 : this.chat_id), false);
        r0.setItemsColor(Theme.getColor("actionBarDefaultIcon"), false);
        r0.setBackButtonDrawable(new BackDrawable(false));
        r0.setCastShadows(false);
        r0.setAddToContainer(false);
        r0.setClipContent(true);
        if (Build.VERSION.SDK_INT >= 21 && !AndroidUtilities.isTablet() && !this.inBubbleMode) {
            z = true;
        }
        r0.setOccupyStatusBar(z);
        return r0;
    }

    /* JADX WARNING: type inference failed for: r0v102, types: [android.view.View] */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0046, code lost:
        r0 = r0.participants;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View createView(android.content.Context r31) {
        /*
            r30 = this;
            r11 = r30
            r12 = r31
            org.telegram.ui.ActionBar.Theme.createProfileResources(r31)
            r13 = 0
            r11.searchTransitionOffset = r13
            r14 = 1065353216(0x3var_, float:1.0)
            r11.searchTransitionProgress = r14
            r11.searchMode = r13
            r15 = 1
            r11.hasOwnBackground = r15
            r16 = 1118830592(0x42b00000, float:88.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r0 = (float) r0
            r11.extraHeight = r0
            org.telegram.ui.ActionBar.ActionBar r0 = r11.actionBar
            org.telegram.ui.ProfileActivity$4 r1 = new org.telegram.ui.ProfileActivity$4
            r1.<init>()
            r0.setActionBarMenuOnItemClick(r1)
            org.telegram.ui.Components.SharedMediaLayout r0 = r11.sharedMediaLayout
            if (r0 == 0) goto L_0x002d
            r0.onDestroy()
        L_0x002d:
            long r0 = r11.dialog_id
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 == 0) goto L_0x0037
        L_0x0035:
            r9 = r0
            goto L_0x0041
        L_0x0037:
            int r0 = r11.user_id
            if (r0 == 0) goto L_0x003c
            goto L_0x003f
        L_0x003c:
            int r0 = r11.chat_id
            int r0 = -r0
        L_0x003f:
            long r0 = (long) r0
            goto L_0x0035
        L_0x0041:
            org.telegram.tgnet.TLRPC$ChatFull r0 = r11.chatInfo
            r8 = 0
            if (r0 == 0) goto L_0x0056
            org.telegram.tgnet.TLRPC$ChatParticipants r0 = r0.participants
            if (r0 == 0) goto L_0x0056
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r0 = r0.participants
            int r0 = r0.size()
            r1 = 5
            if (r0 <= r1) goto L_0x0056
            java.util.ArrayList<java.lang.Integer> r0 = r11.sortedUsers
            goto L_0x0057
        L_0x0056:
            r0 = r8
        L_0x0057:
            org.telegram.ui.ProfileActivity$5 r7 = new org.telegram.ui.ProfileActivity$5
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloader r5 = r11.sharedMediaPreloader
            org.telegram.tgnet.TLRPC$UserFull r1 = r11.userInfo
            if (r1 == 0) goto L_0x0063
            int r1 = r1.common_chats_count
            r6 = r1
            goto L_0x0064
        L_0x0063:
            r6 = 0
        L_0x0064:
            java.util.ArrayList<java.lang.Integer> r3 = r11.sortedUsers
            org.telegram.tgnet.TLRPC$ChatFull r4 = r11.chatInfo
            if (r0 == 0) goto L_0x006d
            r17 = 1
            goto L_0x006f
        L_0x006d:
            r17 = 0
        L_0x006f:
            r0 = r7
            r1 = r30
            r2 = r31
            r18 = r3
            r19 = r4
            r3 = r9
            r14 = r7
            r7 = r18
            r8 = r19
            r22 = r9
            r9 = r17
            r10 = r30
            r0.<init>(r2, r3, r5, r6, r7, r8, r9, r10)
            r11.sharedMediaLayout = r14
            androidx.recyclerview.widget.RecyclerView$LayoutParams r0 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
            r9 = -1
            r0.<init>((int) r9, (int) r9)
            r14.setLayoutParams(r0)
            org.telegram.ui.ActionBar.ActionBar r0 = r11.actionBar
            org.telegram.ui.ActionBar.ActionBarMenu r0 = r0.createMenu()
            org.telegram.ui.Components.ImageUpdater r1 = r11.imageUpdater
            r2 = 8
            if (r1 == 0) goto L_0x00dd
            r1 = 32
            r3 = 2131165471(0x7var_f, float:1.794516E38)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.addItem((int) r1, (int) r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r1.setIsSearchField(r15)
            org.telegram.ui.ProfileActivity$6 r3 = new org.telegram.ui.ProfileActivity$6
            r3.<init>()
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r1.setActionBarMenuItemSearchListener(r3)
            r11.searchItem = r1
            java.lang.String r3 = "SearchInSettings"
            r4 = 2131627335(0x7f0e0d47, float:1.8881932E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r3, r4)
            r1.setContentDescription(r5)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r11.searchItem
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)
            r1.setSearchFieldHint(r3)
            org.telegram.ui.Components.SharedMediaLayout r1 = r11.sharedMediaLayout
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r1.getSearchItem()
            r1.setVisibility(r2)
            boolean r1 = r11.expandPhoto
            if (r1 == 0) goto L_0x00dd
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r11.searchItem
            r1.setVisibility(r2)
        L_0x00dd:
            r1 = 16
            r3 = 2131165978(0x7var_a, float:1.7946188E38)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.addItem((int) r1, (int) r3)
            r11.videoCallItem = r1
            r3 = 2131627992(0x7f0e0fd8, float:1.8883264E38)
            java.lang.String r4 = "VideoCall"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setContentDescription(r3)
            int r1 = r11.chat_id
            r3 = 15
            if (r1 == 0) goto L_0x0110
            r1 = 2131165850(0x7var_a, float:1.7945929E38)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.addItem((int) r3, (int) r1)
            r11.callItem = r1
            r3 = 2131628191(0x7f0e109f, float:1.8883668E38)
            java.lang.String r4 = "VoipGroupVoiceChat"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setContentDescription(r3)
            goto L_0x0125
        L_0x0110:
            r1 = 2131165481(0x7var_, float:1.794518E38)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.addItem((int) r3, (int) r1)
            r11.callItem = r1
            r3 = 2131624616(0x7f0e02a8, float:1.8876417E38)
            java.lang.String r4 = "Call"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setContentDescription(r3)
        L_0x0125:
            r1 = 12
            r3 = 2131165451(0x7var_b, float:1.794512E38)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.addItem((int) r1, (int) r3)
            r11.editItem = r1
            r3 = 2131625232(0x7f0e0510, float:1.8877666E38)
            java.lang.String r4 = "Edit"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setContentDescription(r3)
            r1 = 10
            r3 = 2131165468(0x7var_c, float:1.7945154E38)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r0.addItem((int) r1, (int) r3)
            r11.otherItem = r0
            r1 = 2131623986(0x7f0e0032, float:1.8875139E38)
            java.lang.String r3 = "AccDescrMoreOptions"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r0.setContentDescription(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r11.listView
            if (r0 == 0) goto L_0x017c
            org.telegram.ui.Components.ImageUpdater r0 = r11.imageUpdater
            if (r0 == 0) goto L_0x017c
            androidx.recyclerview.widget.LinearLayoutManager r0 = r11.layoutManager
            int r0 = r0.findFirstVisibleItemPosition()
            androidx.recyclerview.widget.LinearLayoutManager r1 = r11.layoutManager
            android.view.View r1 = r1.findViewByPosition(r0)
            if (r1 == 0) goto L_0x0172
            r1.getTop()
            org.telegram.ui.Components.RecyclerListView r1 = r11.listView
            r1.getPaddingTop()
            goto L_0x0173
        L_0x0172:
            r0 = -1
        L_0x0173:
            org.telegram.ui.Components.RLottieImageView r1 = r11.writeButton
            java.lang.Object r8 = r1.getTag()
            r10 = r0
            r14 = r8
            goto L_0x017e
        L_0x017c:
            r10 = -1
            r14 = 0
        L_0x017e:
            r11.createActionBarMenu(r13)
            org.telegram.ui.ProfileActivity$ListAdapter r0 = new org.telegram.ui.ProfileActivity$ListAdapter
            r0.<init>(r12)
            r11.listAdapter = r0
            org.telegram.ui.ProfileActivity$SearchAdapter r0 = new org.telegram.ui.ProfileActivity$SearchAdapter
            r0.<init>(r11, r12)
            r11.searchAdapter = r0
            org.telegram.ui.Components.AvatarDrawable r0 = new org.telegram.ui.Components.AvatarDrawable
            r0.<init>()
            r11.avatarDrawable = r0
            r0.setProfile(r15)
            org.telegram.ui.ProfileActivity$7 r0 = new org.telegram.ui.ProfileActivity$7
            r0.<init>(r12)
            r11.fragmentView = r0
            r0.setWillNotDraw(r13)
            android.view.View r0 = r11.fragmentView
            r8 = r0
            android.widget.FrameLayout r8 = (android.widget.FrameLayout) r8
            org.telegram.ui.ProfileActivity$8 r0 = new org.telegram.ui.ProfileActivity$8
            r0.<init>(r12)
            r11.listView = r0
            r0.setVerticalScrollBarEnabled(r13)
            org.telegram.ui.ProfileActivity$9 r0 = new org.telegram.ui.ProfileActivity$9
            r0.<init>()
            org.telegram.ui.Components.RecyclerListView r1 = r11.listView
            r1.setItemAnimator(r0)
            r0.setSupportsChangeAnimations(r13)
            r0.setDelayAnimations(r13)
            org.telegram.ui.Components.RecyclerListView r0 = r11.listView
            r0.setClipToPadding(r13)
            org.telegram.ui.Components.RecyclerListView r0 = r11.listView
            r0.setHideIfEmpty(r13)
            org.telegram.ui.ProfileActivity$10 r0 = new org.telegram.ui.ProfileActivity$10
            r0.<init>(r12)
            r11.layoutManager = r0
            r0.setOrientation(r15)
            org.telegram.ui.Components.RecyclerListView r0 = r11.listView
            androidx.recyclerview.widget.LinearLayoutManager r1 = r11.layoutManager
            r0.setLayoutManager(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r11.listView
            r0.setGlowColor(r13)
            org.telegram.ui.Components.RecyclerListView r0 = r11.listView
            org.telegram.ui.ProfileActivity$ListAdapter r1 = r11.listAdapter
            r0.setAdapter(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r11.listView
            r1 = 51
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r9, r1)
            r8.addView(r0, r3)
            org.telegram.ui.Components.RecyclerListView r0 = r11.listView
            org.telegram.ui.-$$Lambda$ProfileActivity$3YTUEsV_9oH2zUH8lXHul2mUsNg r3 = new org.telegram.ui.-$$Lambda$ProfileActivity$3YTUEsV_9oH2zUH8lXHul2mUsNg
            r4 = r22
            r3.<init>(r4)
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended) r3)
            org.telegram.ui.Components.RecyclerListView r0 = r11.listView
            org.telegram.ui.ProfileActivity$11 r3 = new org.telegram.ui.ProfileActivity$11
            r3.<init>()
            r0.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r11.searchItem
            java.lang.String r3 = "avatar_backgroundActionBarBlue"
            if (r0 == 0) goto L_0x029b
            org.telegram.ui.Components.RecyclerListView r0 = new org.telegram.ui.Components.RecyclerListView
            r0.<init>(r12)
            r11.searchListView = r0
            r0.setVerticalScrollBarEnabled(r13)
            org.telegram.ui.Components.RecyclerListView r0 = r11.searchListView
            androidx.recyclerview.widget.LinearLayoutManager r4 = new androidx.recyclerview.widget.LinearLayoutManager
            r4.<init>(r12, r15, r13)
            r0.setLayoutManager(r4)
            org.telegram.ui.Components.RecyclerListView r0 = r11.searchListView
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r0.setGlowColor(r4)
            org.telegram.ui.Components.RecyclerListView r0 = r11.searchListView
            org.telegram.ui.ProfileActivity$SearchAdapter r4 = r11.searchAdapter
            r0.setAdapter(r4)
            org.telegram.ui.Components.RecyclerListView r0 = r11.searchListView
            r4 = 0
            r0.setItemAnimator(r4)
            org.telegram.ui.Components.RecyclerListView r0 = r11.searchListView
            r0.setVisibility(r2)
            org.telegram.ui.Components.RecyclerListView r0 = r11.searchListView
            r0.setLayoutAnimation(r4)
            org.telegram.ui.Components.RecyclerListView r0 = r11.searchListView
            java.lang.String r5 = "windowBackgroundWhite"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r0.setBackgroundColor(r5)
            org.telegram.ui.Components.RecyclerListView r0 = r11.searchListView
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r9, r1)
            r8.addView(r0, r5)
            org.telegram.ui.Components.RecyclerListView r0 = r11.searchListView
            org.telegram.ui.-$$Lambda$ProfileActivity$c_H9oU9_xOmcJdt-y089Vxj31tI r5 = new org.telegram.ui.-$$Lambda$ProfileActivity$c_H9oU9_xOmcJdt-y089Vxj31tI
            r5.<init>()
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r5)
            org.telegram.ui.Components.RecyclerListView r0 = r11.searchListView
            org.telegram.ui.-$$Lambda$ProfileActivity$oElgoAeRCtpeE-keMvHSx5ux8C8 r5 = new org.telegram.ui.-$$Lambda$ProfileActivity$oElgoAeRCtpeE-keMvHSx5ux8C8
            r5.<init>()
            r0.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r5)
            org.telegram.ui.Components.RecyclerListView r0 = r11.searchListView
            org.telegram.ui.ProfileActivity$12 r5 = new org.telegram.ui.ProfileActivity$12
            r5.<init>()
            r0.setOnScrollListener(r5)
            org.telegram.ui.Components.RecyclerListView r0 = r11.searchListView
            r0.setAnimateEmptyView(r15, r15)
            org.telegram.ui.Components.StickerEmptyView r0 = new org.telegram.ui.Components.StickerEmptyView
            r0.<init>(r12, r4, r15)
            r11.emptyView = r0
            r0.setAnimateLayoutChange(r15)
            org.telegram.ui.Components.StickerEmptyView r0 = r11.emptyView
            android.widget.TextView r0 = r0.subtitle
            r0.setVisibility(r2)
            org.telegram.ui.Components.StickerEmptyView r0 = r11.emptyView
            r0.setVisibility(r2)
            org.telegram.ui.Components.StickerEmptyView r0 = r11.emptyView
            r8.addView(r0)
            org.telegram.ui.ProfileActivity$SearchAdapter r0 = r11.searchAdapter
            r0.loadFaqWebPage()
        L_0x029b:
            int r0 = r11.banFromGroup
            java.lang.String r17 = "fonts/rmedium.ttf"
            r18 = 1111490560(0x42400000, float:48.0)
            if (r0 == 0) goto L_0x0348
            org.telegram.messenger.MessagesController r0 = r30.getMessagesController()
            int r2 = r11.banFromGroup
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r2)
            org.telegram.tgnet.TLRPC$ChannelParticipant r2 = r11.currentChannelParticipant
            if (r2 != 0) goto L_0x02d8
            org.telegram.tgnet.TLRPC$TL_channels_getParticipant r2 = new org.telegram.tgnet.TLRPC$TL_channels_getParticipant
            r2.<init>()
            org.telegram.tgnet.TLRPC$InputChannel r4 = org.telegram.messenger.MessagesController.getInputChannel((org.telegram.tgnet.TLRPC$Chat) r0)
            r2.channel = r4
            org.telegram.messenger.MessagesController r4 = r30.getMessagesController()
            int r5 = r11.user_id
            org.telegram.tgnet.TLRPC$InputPeer r4 = r4.getInputPeer((int) r5)
            r2.participant = r4
            org.telegram.tgnet.ConnectionsManager r4 = r30.getConnectionsManager()
            org.telegram.ui.-$$Lambda$ProfileActivity$JIvt10ClV36JH8Lt1CzjI6KYZzQ r5 = new org.telegram.ui.-$$Lambda$ProfileActivity$JIvt10ClV36JH8Lt1CzjI6KYZzQ
            r5.<init>()
            r4.sendRequest(r2, r5)
        L_0x02d8:
            org.telegram.ui.ProfileActivity$13 r2 = new org.telegram.ui.ProfileActivity$13
            r2.<init>(r12)
            r2.setWillNotDraw(r13)
            r4 = 83
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r1, r4)
            r8.addView(r2, r4)
            org.telegram.ui.-$$Lambda$ProfileActivity$4kx0pgMr44m_HMdk2p0o0tBPVjw r4 = new org.telegram.ui.-$$Lambda$ProfileActivity$4kx0pgMr44m_HMdk2p0o0tBPVjw
            r4.<init>(r0)
            r2.setOnClickListener(r4)
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r12)
            java.lang.String r4 = "windowBackgroundWhiteRedText"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r0.setTextColor(r4)
            r4 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r15, r4)
            r4 = 17
            r0.setGravity(r4)
            android.graphics.Typeface r4 = org.telegram.messenger.AndroidUtilities.getTypeface(r17)
            r0.setTypeface(r4)
            r4 = 2131624546(0x7f0e0262, float:1.8876275E38)
            java.lang.String r5 = "BanFromTheGroup"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.setText(r4)
            r21 = -2
            r22 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r23 = 17
            r24 = 0
            r25 = 1065353216(0x3var_, float:1.0)
            r26 = 0
            r27 = 0
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r21, r22, r23, r24, r25, r26, r27)
            r2.addView(r0, r4)
            org.telegram.ui.Components.RecyclerListView r0 = r11.listView
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r0.setPadding(r13, r2, r13, r4)
            org.telegram.ui.Components.RecyclerListView r0 = r11.listView
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r0.setBottomGlowOffset(r2)
            goto L_0x0351
        L_0x0348:
            org.telegram.ui.Components.RecyclerListView r0 = r11.listView
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r16)
            r0.setPadding(r13, r2, r13, r13)
        L_0x0351:
            org.telegram.ui.ProfileActivity$TopView r0 = new org.telegram.ui.ProfileActivity$TopView
            r0.<init>(r12)
            r11.topView = r0
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r0.setBackgroundColor(r2)
            org.telegram.ui.ProfileActivity$TopView r0 = r11.topView
            r8.addView(r0)
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r12)
            r11.avatarContainer = r0
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r12)
            r11.avatarContainer2 = r0
            r7 = 1065353216(0x3var_, float:1.0)
            org.telegram.messenger.AndroidUtilities.updateViewVisibilityAnimated(r0, r15, r7, r13)
            android.widget.FrameLayout r0 = r11.avatarContainer2
            r6 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r6)
            r8.addView(r0, r2)
            android.widget.FrameLayout r0 = r11.avatarContainer
            r5 = 0
            r0.setPivotX(r5)
            android.widget.FrameLayout r0 = r11.avatarContainer
            r0.setPivotY(r5)
            android.widget.FrameLayout r0 = r11.avatarContainer2
            android.widget.FrameLayout r2 = r11.avatarContainer
            r19 = 42
            r20 = 1109917696(0x42280000, float:42.0)
            r21 = 51
            r22 = 1115684864(0x42800000, float:64.0)
            r23 = 0
            r24 = 0
            r25 = 0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r0.addView(r2, r3)
            org.telegram.ui.ProfileActivity$15 r0 = new org.telegram.ui.ProfileActivity$15
            r0.<init>(r12)
            r11.avatarImage = r0
            r2 = 1101529088(0x41a80000, float:21.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setRoundRadius(r2)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r11.avatarImage
            r0.setPivotX(r5)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r11.avatarImage
            r0.setPivotY(r5)
            android.widget.FrameLayout r0 = r11.avatarContainer
            org.telegram.ui.ProfileActivity$AvatarImageView r2 = r11.avatarImage
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r6)
            r0.addView(r2, r3)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r11.avatarImage
            org.telegram.ui.-$$Lambda$ProfileActivity$YjKZ12VFQcJlJ-2lWStDoKh0EUs r2 = new org.telegram.ui.-$$Lambda$ProfileActivity$YjKZ12VFQcJlJ-2lWStDoKh0EUs
            r2.<init>()
            r0.setOnClickListener(r2)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r11.avatarImage
            org.telegram.ui.-$$Lambda$ProfileActivity$jCfEV0hn5c5R6FjFwEiJp3C0gn4 r2 = new org.telegram.ui.-$$Lambda$ProfileActivity$jCfEV0hn5c5R6FjFwEiJp3C0gn4
            r2.<init>()
            r0.setOnLongClickListener(r2)
            org.telegram.ui.ProfileActivity$16 r0 = new org.telegram.ui.ProfileActivity$16
            r0.<init>(r12)
            r11.avatarProgressView = r0
            r2 = 1104150528(0x41d00000, float:26.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setSize(r2)
            org.telegram.ui.Components.RadialProgressView r0 = r11.avatarProgressView
            r0.setProgressColor(r9)
            org.telegram.ui.Components.RadialProgressView r0 = r11.avatarProgressView
            r0.setNoProgress(r13)
            android.widget.FrameLayout r0 = r11.avatarContainer
            org.telegram.ui.Components.RadialProgressView r2 = r11.avatarProgressView
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r6)
            r0.addView(r2, r3)
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r12)
            r11.timeItem = r0
            r2 = 1092616192(0x41200000, float:10.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r4 = 1084227584(0x40a00000, float:5.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r0.setPadding(r3, r2, r5, r4)
            android.widget.ImageView r0 = r11.timeItem
            android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.CENTER
            r0.setScaleType(r2)
            android.widget.ImageView r0 = r11.timeItem
            org.telegram.ui.Components.TimerDrawable r2 = new org.telegram.ui.Components.TimerDrawable
            r2.<init>(r12)
            r11.timerDrawable = r2
            r0.setImageDrawable(r2)
            android.widget.ImageView r0 = r11.timeItem
            r2 = 34
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r2, r1)
            r8.addView(r0, r1)
            r30.updateTimeItem()
            r11.showAvatarProgress(r13, r13)
            org.telegram.ui.Components.ProfileGalleryView r0 = r11.avatarsViewPager
            if (r0 == 0) goto L_0x044d
            r0.onDestroy()
        L_0x044d:
            org.telegram.ui.ProfileActivity$OverlaysView r0 = new org.telegram.ui.ProfileActivity$OverlaysView
            r0.<init>(r12)
            r11.overlaysView = r0
            org.telegram.ui.Components.ProfileGalleryView r5 = new org.telegram.ui.Components.ProfileGalleryView
            int r0 = r11.user_id
            if (r0 == 0) goto L_0x045b
            goto L_0x045e
        L_0x045b:
            int r0 = r11.chat_id
            int r0 = -r0
        L_0x045e:
            long r0 = (long) r0
            r2 = r0
            org.telegram.ui.ActionBar.ActionBar r4 = r11.actionBar
            org.telegram.ui.Components.RecyclerListView r1 = r11.listView
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r11.avatarImage
            int r19 = r30.getClassGuid()
            org.telegram.ui.ProfileActivity$OverlaysView r13 = r11.overlaysView
            r21 = r0
            r0 = r5
            r22 = r1
            r1 = r31
            r15 = r5
            r5 = r22
            r9 = -1082130432(0xffffffffbvar_, float:-1.0)
            r6 = r21
            r21 = 1065353216(0x3var_, float:1.0)
            r7 = r19
            r29 = r8
            r8 = r13
            r0.<init>(r1, r2, r4, r5, r6, r7, r8)
            r11.avatarsViewPager = r15
            org.telegram.tgnet.TLRPC$ChatFull r0 = r11.chatInfo
            r15.setChatInfo(r0)
            android.widget.FrameLayout r0 = r11.avatarContainer2
            org.telegram.ui.Components.ProfileGalleryView r1 = r11.avatarsViewPager
            r0.addView(r1)
            android.widget.FrameLayout r0 = r11.avatarContainer2
            org.telegram.ui.ProfileActivity$OverlaysView r1 = r11.overlaysView
            r0.addView(r1)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r11.avatarImage
            org.telegram.ui.Components.ProfileGalleryView r1 = r11.avatarsViewPager
            r0.setAvatarsViewPager(r1)
            org.telegram.ui.ProfileActivity$PagerIndicatorView r0 = new org.telegram.ui.ProfileActivity$PagerIndicatorView
            r0.<init>(r12)
            r11.avatarsViewPagerIndicatorView = r0
            android.widget.FrameLayout r1 = r11.avatarContainer2
            r2 = -1
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r9)
            r1.addView(r0, r3)
            org.telegram.ui.ActionBar.ActionBar r0 = r11.actionBar
            r1 = r29
            r1.addView(r0)
            r0 = 0
        L_0x04b9:
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r11.nameTextView
            int r3 = r2.length
            r4 = 3
            r5 = 2
            if (r0 >= r3) goto L_0x056a
            int r3 = r11.playProfileAnimation
            if (r3 != 0) goto L_0x04ca
            if (r0 != 0) goto L_0x04ca
            r2 = 1
            r3 = 0
            goto L_0x0566
        L_0x04ca:
            org.telegram.ui.ActionBar.SimpleTextView r3 = new org.telegram.ui.ActionBar.SimpleTextView
            r3.<init>(r12)
            r2[r0] = r3
            r2 = 1
            if (r0 != r2) goto L_0x04e2
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r11.nameTextView
            r2 = r2[r0]
            java.lang.String r3 = "profile_title"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setTextColor(r3)
            goto L_0x04ef
        L_0x04e2:
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r11.nameTextView
            r2 = r2[r0]
            java.lang.String r3 = "actionBarDefaultTitle"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setTextColor(r3)
        L_0x04ef:
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r11.nameTextView
            r2 = r2[r0]
            r3 = 18
            r2.setTextSize(r3)
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r11.nameTextView
            r2 = r2[r0]
            r2.setGravity(r4)
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r11.nameTextView
            r2 = r2[r0]
            android.graphics.Typeface r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r17)
            r2.setTypeface(r3)
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r11.nameTextView
            r2 = r2[r0]
            r3 = 1067869798(0x3fa66666, float:1.3)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = -r3
            r2.setLeftDrawableTopPadding(r3)
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r11.nameTextView
            r2 = r2[r0]
            r3 = 0
            r2.setPivotX(r3)
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r11.nameTextView
            r2 = r2[r0]
            r2.setPivotY(r3)
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r11.nameTextView
            r2 = r2[r0]
            if (r0 != 0) goto L_0x0530
            r7 = 0
            goto L_0x0532
        L_0x0530:
            r7 = 1065353216(0x3var_, float:1.0)
        L_0x0532:
            r2.setAlpha(r7)
            r2 = 1
            if (r0 != r2) goto L_0x0546
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r11.nameTextView
            r4 = r4[r0]
            r4.setScrollNonFitText(r2)
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r11.nameTextView
            r4 = r4[r0]
            r4.setImportantForAccessibility(r5)
        L_0x0546:
            android.widget.FrameLayout r4 = r11.avatarContainer2
            org.telegram.ui.ActionBar.SimpleTextView[] r5 = r11.nameTextView
            r5 = r5[r0]
            r23 = -2
            r24 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r25 = 51
            r26 = 1122762752(0x42eCLASSNAME, float:118.0)
            r27 = 0
            if (r0 != 0) goto L_0x055b
            r28 = 1111490560(0x42400000, float:48.0)
            goto L_0x055d
        L_0x055b:
            r28 = 0
        L_0x055d:
            r29 = 0
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
            r4.addView(r5, r6)
        L_0x0566:
            int r0 = r0 + 1
            goto L_0x04b9
        L_0x056a:
            r3 = 0
            r0 = 0
        L_0x056c:
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r11.onlineTextView
            int r6 = r2.length
            if (r0 >= r6) goto L_0x05d3
            org.telegram.ui.ActionBar.SimpleTextView r6 = new org.telegram.ui.ActionBar.SimpleTextView
            r6.<init>(r12)
            r2[r0] = r6
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r11.onlineTextView
            r2 = r2[r0]
            java.lang.String r6 = "avatar_subtitleInProfileBlue"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r2.setTextColor(r6)
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r11.onlineTextView
            r2 = r2[r0]
            r6 = 14
            r2.setTextSize(r6)
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r11.onlineTextView
            r2 = r2[r0]
            r2.setGravity(r4)
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r11.onlineTextView
            r2 = r2[r0]
            if (r0 == 0) goto L_0x05a1
            if (r0 != r5) goto L_0x059e
            goto L_0x05a1
        L_0x059e:
            r7 = 1065353216(0x3var_, float:1.0)
            goto L_0x05a2
        L_0x05a1:
            r7 = 0
        L_0x05a2:
            r2.setAlpha(r7)
            if (r0 <= 0) goto L_0x05ae
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r11.onlineTextView
            r2 = r2[r0]
            r2.setImportantForAccessibility(r5)
        L_0x05ae:
            android.widget.FrameLayout r2 = r11.avatarContainer2
            org.telegram.ui.ActionBar.SimpleTextView[] r6 = r11.onlineTextView
            r6 = r6[r0]
            r23 = -2
            r24 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r25 = 51
            r26 = 1122762752(0x42eCLASSNAME, float:118.0)
            r27 = 0
            if (r0 != 0) goto L_0x05c3
            r28 = 1111490560(0x42400000, float:48.0)
            goto L_0x05c7
        L_0x05c3:
            r7 = 1090519040(0x41000000, float:8.0)
            r28 = 1090519040(0x41000000, float:8.0)
        L_0x05c7:
            r29 = 0
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
            r2.addView(r6, r7)
            int r0 = r0 + 1
            goto L_0x056c
        L_0x05d3:
            org.telegram.ui.ProfileActivity$17 r0 = new org.telegram.ui.ProfileActivity$17
            r0.<init>(r12, r12)
            r11.mediaCounterTextView = r0
            r0.setAlpha(r3)
            android.widget.FrameLayout r0 = r11.avatarContainer2
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r2 = r11.mediaCounterTextView
            r23 = -2
            r24 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r25 = 51
            r26 = 1122762752(0x42eCLASSNAME, float:118.0)
            r27 = 0
            r28 = 1090519040(0x41000000, float:8.0)
            r29 = 0
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
            r0.addView(r2, r4)
            r30.updateProfileData()
            org.telegram.ui.Components.RLottieImageView r0 = new org.telegram.ui.Components.RLottieImageView
            r0.<init>(r12)
            r11.writeButton = r0
            android.content.res.Resources r0 = r31.getResources()
            r2 = 2131165412(0x7var_e4, float:1.794504E38)
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r2)
            android.graphics.drawable.Drawable r0 = r0.mutate()
            android.graphics.PorterDuffColorFilter r2 = new android.graphics.PorterDuffColorFilter
            r4 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.graphics.PorterDuff$Mode r6 = android.graphics.PorterDuff.Mode.MULTIPLY
            r2.<init>(r4, r6)
            r0.setColorFilter(r2)
            org.telegram.ui.Components.CombinedDrawable r2 = new org.telegram.ui.Components.CombinedDrawable
            r4 = 1113587712(0x42600000, float:56.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r4)
            java.lang.String r7 = "profile_actionBackground"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            java.lang.String r8 = "profile_actionPressedBackground"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorCircleDrawable(r6, r7, r8)
            r7 = 0
            r2.<init>(r0, r6, r7, r7)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r2.setIconSize(r0, r6)
            org.telegram.ui.Components.RLottieImageView r0 = r11.writeButton
            r0.setBackgroundDrawable(r2)
            int r0 = r11.user_id
            if (r0 == 0) goto L_0x06a6
            org.telegram.ui.Components.ImageUpdater r0 = r11.imageUpdater
            if (r0 == 0) goto L_0x068f
            org.telegram.ui.Components.RLottieDrawable r0 = new org.telegram.ui.Components.RLottieDrawable
            r24 = 2131558410(0x7f0d000a, float:1.8742135E38)
            int r26 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r27 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r28 = 0
            r29 = 0
            java.lang.String r25 = "NUM"
            r23 = r0
            r23.<init>((int) r24, (java.lang.String) r25, (int) r26, (int) r27, (boolean) r28, (int[]) r29)
            r11.cameraDrawable = r0
            org.telegram.ui.Components.RLottieImageView r2 = r11.writeButton
            r2.setAnimation(r0)
            org.telegram.ui.Components.RLottieImageView r0 = r11.writeButton
            r2 = 2131623964(0x7f0e001c, float:1.8875094E38)
            java.lang.String r4 = "AccDescrChangeProfilePicture"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            r0.setContentDescription(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r11.writeButton
            r2 = 1073741824(0x40000000, float:2.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r4 = 1073741824(0x40000000, float:2.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r6 = 0
            r0.setPadding(r2, r6, r6, r4)
            goto L_0x06bc
        L_0x068f:
            org.telegram.ui.Components.RLottieImageView r0 = r11.writeButton
            r2 = 2131165975(0x7var_, float:1.7946182E38)
            r0.setImageResource(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r11.writeButton
            r2 = 2131624000(0x7f0e0040, float:1.8875167E38)
            java.lang.String r4 = "AccDescrOpenChat"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            r0.setContentDescription(r2)
            goto L_0x06bc
        L_0x06a6:
            org.telegram.ui.Components.RLottieImageView r0 = r11.writeButton
            r2 = 2131165971(0x7var_, float:1.7946174E38)
            r0.setImageResource(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r11.writeButton
            r2 = 2131628024(0x7f0e0ff8, float:1.888333E38)
            java.lang.String r4 = "ViewDiscussion"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            r0.setContentDescription(r2)
        L_0x06bc:
            org.telegram.ui.Components.RLottieImageView r0 = r11.writeButton
            android.graphics.PorterDuffColorFilter r2 = new android.graphics.PorterDuffColorFilter
            java.lang.String r4 = "profile_actionIcon"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            android.graphics.PorterDuff$Mode r6 = android.graphics.PorterDuff.Mode.MULTIPLY
            r2.<init>(r4, r6)
            r0.setColorFilter(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r11.writeButton
            android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.CENTER
            r0.setScaleType(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r11.writeButton
            r23 = 60
            r24 = 1114636288(0x42700000, float:60.0)
            r25 = 53
            r26 = 0
            r27 = 0
            r28 = 1098907648(0x41800000, float:16.0)
            r29 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
            r1.addView(r0, r2)
            org.telegram.ui.Components.RLottieImageView r0 = r11.writeButton
            org.telegram.ui.-$$Lambda$ProfileActivity$UJPIQLJnlPvpuaHK6bxzezN2-bI r2 = new org.telegram.ui.-$$Lambda$ProfileActivity$UJPIQLJnlPvpuaHK6bxzezN2-bI
            r2.<init>()
            r0.setOnClickListener(r2)
            r0 = 0
            r11.needLayout(r0)
            r2 = -1
            if (r10 == r2) goto L_0x071a
            if (r14 == 0) goto L_0x071a
            org.telegram.ui.Components.RLottieImageView r2 = r11.writeButton
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r2.setTag(r0)
            org.telegram.ui.Components.RLottieImageView r0 = r11.writeButton
            r2 = 1045220557(0x3e4ccccd, float:0.2)
            r0.setScaleX(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r11.writeButton
            r0.setScaleY(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r11.writeButton
            r0.setAlpha(r3)
        L_0x071a:
            org.telegram.ui.Components.RecyclerListView r0 = r11.listView
            org.telegram.ui.ProfileActivity$18 r2 = new org.telegram.ui.ProfileActivity$18
            r2.<init>()
            r0.setOnScrollListener(r2)
            org.telegram.ui.Components.UndoView r0 = new org.telegram.ui.Components.UndoView
            r0.<init>(r12)
            r11.undoView = r0
            r12 = -1
            r13 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r14 = 83
            r15 = 1090519040(0x41000000, float:8.0)
            r16 = 0
            r17 = 1090519040(0x41000000, float:8.0)
            r18 = 1090519040(0x41000000, float:8.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18)
            r1.addView(r0, r2)
            float[] r0 = new float[r5]
            r0 = {0, NUM} // fill-array
            android.animation.ValueAnimator r0 = android.animation.ValueAnimator.ofFloat(r0)
            r11.expandAnimator = r0
            org.telegram.ui.-$$Lambda$ProfileActivity$JmVDeoGw87pdTan3PtsPlD_GwA0 r2 = new org.telegram.ui.-$$Lambda$ProfileActivity$JmVDeoGw87pdTan3PtsPlD_GwA0
            r2.<init>()
            r0.addUpdateListener(r2)
            android.animation.ValueAnimator r0 = r11.expandAnimator
            org.telegram.ui.Components.CubicBezierInterpolator r2 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
            r0.setInterpolator(r2)
            android.animation.ValueAnimator r0 = r11.expandAnimator
            org.telegram.ui.ProfileActivity$19 r2 = new org.telegram.ui.ProfileActivity$19
            r2.<init>()
            r0.addListener(r2)
            r30.updateRowsIds()
            r30.updateSelectedMediaTabText()
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 21
            if (r0 < r2) goto L_0x077f
            android.app.Activity r0 = r30.getParentActivity()
            android.view.Window r0 = r0.getWindow()
            android.view.View r0 = r0.getDecorView()
            r8 = r0
            android.view.ViewGroup r8 = (android.view.ViewGroup) r8
            goto L_0x0780
        L_0x077f:
            r8 = r1
        L_0x0780:
            org.telegram.ui.ProfileActivity$20 r0 = new org.telegram.ui.ProfileActivity$20
            r0.<init>(r8)
            r11.pinchToZoomHelper = r0
            org.telegram.ui.ProfileActivity$21 r1 = new org.telegram.ui.ProfileActivity$21
            r1.<init>()
            r0.setCallback(r1)
            org.telegram.ui.Components.ProfileGalleryView r0 = r11.avatarsViewPager
            org.telegram.ui.PinchToZoomHelper r1 = r11.pinchToZoomHelper
            r0.setPinchToZoomHelper(r1)
            android.view.View r0 = r11.fragmentView
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.createView(android.content.Context):android.view.View");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$3 */
    public /* synthetic */ void lambda$createView$3$ProfileActivity(long j, View view, int i, float f, float f2) {
        TLRPC$ChatParticipant tLRPC$ChatParticipant;
        long j2 = j;
        int i2 = i;
        if (getParentActivity() != null) {
            if (i2 == this.settingsKeyRow) {
                Bundle bundle = new Bundle();
                bundle.putInt("chat_id", (int) (this.dialog_id >> 32));
                presentFragment(new IdenticonActivity(bundle));
            } else if (i2 == this.settingsTimerRow) {
                showDialog(AlertsCreator.createTTLAlert(getParentActivity(), this.currentEncryptedChat).create());
            } else if (i2 == this.notificationsRow) {
                if ((!LocaleController.isRTL || f > ((float) AndroidUtilities.dp(76.0f))) && (LocaleController.isRTL || f < ((float) (view.getMeasuredWidth() - AndroidUtilities.dp(76.0f))))) {
                    AlertsCreator.showCustomNotificationsDialog(this, j, -1, (ArrayList<NotificationsSettingsActivity.NotificationException>) null, this.currentAccount, new MessagesStorage.IntCallback() {
                        public final void run(int i) {
                            ProfileActivity.this.lambda$createView$1$ProfileActivity(i);
                        }
                    });
                    return;
                }
                NotificationsCheckCell notificationsCheckCell = (NotificationsCheckCell) view;
                boolean z = !notificationsCheckCell.isChecked();
                boolean isGlobalNotificationsEnabled = getNotificationsController().isGlobalNotificationsEnabled(j);
                long j3 = 0;
                if (z) {
                    SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                    if (isGlobalNotificationsEnabled) {
                        edit.remove("notify2_" + j);
                    } else {
                        edit.putInt("notify2_" + j, 0);
                    }
                    getMessagesStorage().setDialogFlags(j, 0);
                    edit.commit();
                    TLRPC$Dialog tLRPC$Dialog = getMessagesController().dialogs_dict.get(j);
                    if (tLRPC$Dialog != null) {
                        tLRPC$Dialog.notify_settings = new TLRPC$TL_peerNotifySettings();
                    }
                } else {
                    SharedPreferences.Editor edit2 = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                    if (!isGlobalNotificationsEnabled) {
                        edit2.remove("notify2_" + j);
                    } else {
                        edit2.putInt("notify2_" + j, 2);
                        j3 = 1;
                    }
                    getNotificationsController().removeNotificationsForDialog(j);
                    getMessagesStorage().setDialogFlags(j, j3);
                    edit2.commit();
                    TLRPC$Dialog tLRPC$Dialog2 = getMessagesController().dialogs_dict.get(j);
                    if (tLRPC$Dialog2 != null) {
                        TLRPC$TL_peerNotifySettings tLRPC$TL_peerNotifySettings = new TLRPC$TL_peerNotifySettings();
                        tLRPC$Dialog2.notify_settings = tLRPC$TL_peerNotifySettings;
                        if (isGlobalNotificationsEnabled) {
                            tLRPC$TL_peerNotifySettings.mute_until = Integer.MAX_VALUE;
                        }
                    }
                }
                getNotificationsController().updateServerNotificationsSettings(j);
                notificationsCheckCell.setChecked(z);
                RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findViewHolderForPosition(this.notificationsRow);
                if (holder != null) {
                    this.listAdapter.onBindViewHolder(holder, this.notificationsRow);
                }
            } else if (i2 == this.unblockRow) {
                getMessagesController().unblockPeer(this.user_id);
                if (BulletinFactory.canShowBulletin(this)) {
                    BulletinFactory.createBanBulletin(this, false).show();
                }
            } else if (i2 == this.sendMessageRow) {
                onWriteButtonClick();
            } else if (i2 == this.reportRow) {
                AlertsCreator.createReportAlert(getParentActivity(), getDialogId(), 0, this);
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
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
            } else if (i2 == this.locationRow) {
                if (this.chatInfo.location instanceof TLRPC$TL_channelLocation) {
                    LocationActivity locationActivity = new LocationActivity(5);
                    locationActivity.setChatLocation(this.chat_id, (TLRPC$TL_channelLocation) this.chatInfo.location);
                    presentFragment(locationActivity);
                }
            } else if (i2 == this.joinRow) {
                getMessagesController().addUserToChat(this.currentChat.id, getUserConfig().getCurrentUser(), 0, (String) null, this, (Runnable) null);
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.closeSearchByActiveAction, new Object[0]);
            } else if (i2 == this.subscribersRow) {
                Bundle bundle2 = new Bundle();
                bundle2.putInt("chat_id", this.chat_id);
                bundle2.putInt("type", 2);
                ChatUsersActivity chatUsersActivity = new ChatUsersActivity(bundle2);
                chatUsersActivity.setInfo(this.chatInfo);
                presentFragment(chatUsersActivity);
            } else if (i2 == this.administratorsRow) {
                Bundle bundle3 = new Bundle();
                bundle3.putInt("chat_id", this.chat_id);
                bundle3.putInt("type", 1);
                ChatUsersActivity chatUsersActivity2 = new ChatUsersActivity(bundle3);
                chatUsersActivity2.setInfo(this.chatInfo);
                presentFragment(chatUsersActivity2);
            } else if (i2 == this.blockedUsersRow) {
                Bundle bundle4 = new Bundle();
                bundle4.putInt("chat_id", this.chat_id);
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
                    builder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener() {
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            ProfileActivity.this.lambda$createView$2$ProfileActivity(dialogInterface, i);
                        }
                    });
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
                processOnClickOrPress(i2);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$1 */
    public /* synthetic */ void lambda$createView$1$ProfileActivity(int i) {
        this.listAdapter.notifyItemChanged(this.notificationsRow);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$2 */
    public /* synthetic */ void lambda$createView$2$ProfileActivity(DialogInterface dialogInterface, int i) {
        SharedConfig.pushAuthKey = null;
        SharedConfig.pushAuthKeyId = null;
        SharedConfig.saveConfig();
        getConnectionsManager().switchBackend();
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00a2  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00a9  */
    /* renamed from: lambda$createView$4 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$createView$4$ProfileActivity(android.view.View r8, int r9) {
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.lambda$createView$4$ProfileActivity(android.view.View, int):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$6 */
    public /* synthetic */ boolean lambda$createView$6$ProfileActivity(View view, int i) {
        if (this.searchAdapter.isSearchWas() || this.searchAdapter.recentSearches.isEmpty()) {
            return false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setMessage(LocaleController.getString("ClearSearch", NUM));
        builder.setPositiveButton(LocaleController.getString("ClearButton", NUM).toUpperCase(), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                ProfileActivity.this.lambda$createView$5$ProfileActivity(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        showDialog(builder.create());
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$5 */
    public /* synthetic */ void lambda$createView$5$ProfileActivity(DialogInterface dialogInterface, int i) {
        this.searchAdapter.clearRecent();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$7 */
    public /* synthetic */ void lambda$createView$7$ProfileActivity(TLObject tLObject) {
        this.currentChannelParticipant = ((TLRPC$TL_channels_channelParticipant) tLObject).participant;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$8 */
    public /* synthetic */ void lambda$createView$8$ProfileActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable(tLObject) {
                public final /* synthetic */ TLObject f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ProfileActivity.this.lambda$createView$7$ProfileActivity(this.f$1);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$9 */
    public /* synthetic */ void lambda$createView$9$ProfileActivity(TLRPC$Chat tLRPC$Chat, View view) {
        int i = this.user_id;
        int i2 = this.banFromGroup;
        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights = tLRPC$Chat.default_banned_rights;
        TLRPC$ChannelParticipant tLRPC$ChannelParticipant = this.currentChannelParticipant;
        ChatRightsEditActivity chatRightsEditActivity = new ChatRightsEditActivity(i, i2, (TLRPC$TL_chatAdminRights) null, tLRPC$TL_chatBannedRights, tLRPC$ChannelParticipant != null ? tLRPC$ChannelParticipant.banned_rights : null, "", 1, true, false);
        chatRightsEditActivity.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() {
            public void didSetRights(int i, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, String str) {
                ProfileActivity.this.removeSelfFromStack();
            }

            public void didChangeOwner(TLRPC$User tLRPC$User) {
                ProfileActivity.this.undoView.showWithAction((long) (-ProfileActivity.this.chat_id), ProfileActivity.this.currentChat.megagroup ? 10 : 9, (Object) tLRPC$User);
            }
        });
        presentFragment(chatRightsEditActivity);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$10 */
    public /* synthetic */ void lambda$createView$10$ProfileActivity(View view) {
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
    /* renamed from: lambda$createView$11 */
    public /* synthetic */ boolean lambda$createView$11$ProfileActivity(View view) {
        if (this.avatarBig != null) {
            return false;
        }
        openAvatar();
        return false;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$12 */
    public /* synthetic */ void lambda$createView$12$ProfileActivity(View view) {
        if (this.writeButton.getTag() == null) {
            onWriteButtonClick();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$13 */
    public /* synthetic */ void lambda$createView$13$ProfileActivity(ValueAnimator valueAnimator) {
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
            actionBarMenuItem.setAlpha(1.0f - lerp);
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
        float f = (float) currentActionBarHeight;
        float dpvar_ = ((this.extraHeight + f) - AndroidUtilities.dpf2(38.0f)) - ((float) this.nameTextView[1].getBottom());
        float f2 = this.nameX;
        float f3 = this.nameY;
        float f4 = 1.0f - lerp;
        float f5 = f4 * f4;
        float f6 = f4 * 2.0f * lerp;
        float f7 = lerp * lerp;
        float f8 = (f2 * f5) + ((dpf2 + f2 + ((dpvar_ - f2) / 2.0f)) * f6) + (dpvar_ * f7);
        float f9 = (f3 * f5) + ((dpf2 + f3 + ((dpvar_ - f3) / 2.0f)) * f6) + (dpvar_ * f7);
        float dpvar_ = AndroidUtilities.dpf2(16.0f) - ((float) this.onlineTextView[1].getLeft());
        float dpvar_ = ((this.extraHeight + f) - AndroidUtilities.dpf2(18.0f)) - ((float) this.onlineTextView[1].getBottom());
        float var_ = this.onlineX;
        float var_ = this.onlineY;
        float var_ = (var_ * f5) + ((dpf2 + var_ + ((dpvar_ - var_) / 2.0f)) * f6) + (dpvar_ * f7);
        float var_ = (f5 * var_) + (f6 * (dpf2 + var_ + ((dpvar_ - var_) / 2.0f))) + (f7 * dpvar_);
        this.nameTextView[1].setTranslationX(f8);
        this.nameTextView[1].setTranslationY(f9);
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
            simpleTextViewArr[1].setPivotY(AndroidUtilities.lerp(0.0f, (float) simpleTextViewArr[1].getMeasuredHeight(), lerp));
            this.nameTextView[1].setScaleX(AndroidUtilities.lerp(1.12f, 1.67f, lerp));
            this.nameTextView[1].setScaleY(AndroidUtilities.lerp(1.12f, 1.67f, lerp));
        }
        needLayoutText(Math.min(1.0f, this.extraHeight / ((float) AndroidUtilities.dp(88.0f))));
        this.nameTextView[1].setTextColor(ColorUtils.blendARGB(Theme.getColor("profile_title"), -1, lerp));
        this.actionBar.setItemsColor(ColorUtils.blendARGB(Theme.getColor("actionBarDefaultIcon"), -1, lerp), false);
        this.avatarImage.setForegroundAlpha(lerp);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.avatarContainer.getLayoutParams();
        layoutParams.width = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(42.0f), ((float) this.listView.getMeasuredWidth()) / this.avatarScale, lerp);
        layoutParams.height = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(42.0f), (this.extraHeight + f) / this.avatarScale, lerp);
        layoutParams.leftMargin = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(64.0f), 0.0f, lerp);
        this.avatarContainer.requestLayout();
    }

    public long getDialogId() {
        long j = this.dialog_id;
        if (j != 0) {
            return j;
        }
        int i = this.user_id;
        if (i != 0) {
            return (long) i;
        }
        return (long) (-this.chat_id);
    }

    public TLRPC$Chat getCurrentChat() {
        return this.currentChat;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x004b, code lost:
        r0 = getMessagesController().getChat(java.lang.Integer.valueOf(r4.chat_id));
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void openAvatar() {
        /*
            r4 = this;
            org.telegram.ui.Components.RecyclerListView r0 = r4.listView
            int r0 = r0.getScrollState()
            r1 = 1
            if (r0 != r1) goto L_0x000a
            return
        L_0x000a:
            int r0 = r4.user_id
            if (r0 == 0) goto L_0x0047
            org.telegram.messenger.MessagesController r0 = r4.getMessagesController()
            int r1 = r4.user_id
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r1)
            org.telegram.tgnet.TLRPC$UserProfilePhoto r1 = r0.photo
            if (r1 == 0) goto L_0x00ac
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_big
            if (r1 == 0) goto L_0x00ac
            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.getInstance()
            android.app.Activity r2 = r4.getParentActivity()
            r1.setParentActivity(r2)
            org.telegram.tgnet.TLRPC$UserProfilePhoto r1 = r0.photo
            int r2 = r1.dc_id
            if (r2 == 0) goto L_0x0039
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_big
            r1.dc_id = r2
        L_0x0039:
            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.getInstance()
            org.telegram.tgnet.TLRPC$UserProfilePhoto r0 = r0.photo
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.photo_big
            org.telegram.ui.PhotoViewer$PhotoViewerProvider r2 = r4.provider
            r1.openPhoto(r0, r2)
            goto L_0x00ac
        L_0x0047:
            int r0 = r4.chat_id
            if (r0 == 0) goto L_0x00ac
            org.telegram.messenger.MessagesController r0 = r4.getMessagesController()
            int r1 = r4.chat_id
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r1)
            org.telegram.tgnet.TLRPC$ChatPhoto r1 = r0.photo
            if (r1 == 0) goto L_0x00ac
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_big
            if (r1 == 0) goto L_0x00ac
            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.getInstance()
            android.app.Activity r2 = r4.getParentActivity()
            r1.setParentActivity(r2)
            org.telegram.tgnet.TLRPC$ChatPhoto r1 = r0.photo
            int r2 = r1.dc_id
            if (r2 == 0) goto L_0x0076
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_big
            r1.dc_id = r2
        L_0x0076:
            org.telegram.tgnet.TLRPC$ChatFull r1 = r4.chatInfo
            if (r1 == 0) goto L_0x009e
            org.telegram.tgnet.TLRPC$Photo r1 = r1.chat_photo
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_photo
            if (r2 == 0) goto L_0x009e
            java.util.ArrayList<org.telegram.tgnet.TLRPC$VideoSize> r1 = r1.video_sizes
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x009e
            org.telegram.tgnet.TLRPC$ChatFull r1 = r4.chatInfo
            org.telegram.tgnet.TLRPC$Photo r1 = r1.chat_photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$VideoSize> r1 = r1.video_sizes
            r2 = 0
            java.lang.Object r1 = r1.get(r2)
            org.telegram.tgnet.TLRPC$VideoSize r1 = (org.telegram.tgnet.TLRPC$VideoSize) r1
            org.telegram.tgnet.TLRPC$ChatFull r2 = r4.chatInfo
            org.telegram.tgnet.TLRPC$Photo r2 = r2.chat_photo
            org.telegram.messenger.ImageLocation r1 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$VideoSize) r1, (org.telegram.tgnet.TLRPC$Photo) r2)
            goto L_0x009f
        L_0x009e:
            r1 = 0
        L_0x009f:
            org.telegram.ui.PhotoViewer r2 = org.telegram.ui.PhotoViewer.getInstance()
            org.telegram.tgnet.TLRPC$ChatPhoto r0 = r0.photo
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.photo_big
            org.telegram.ui.PhotoViewer$PhotoViewerProvider r3 = r4.provider
            r2.openPhotoWithVideo(r0, r1, r3)
        L_0x00ac:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.openAvatar():void");
    }

    /* access modifiers changed from: private */
    public void onWriteButtonClick() {
        if (this.user_id != 0) {
            boolean z = true;
            if (this.imageUpdater != null) {
                TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
                if (user == null) {
                    user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
                }
                if (user != null) {
                    ImageUpdater imageUpdater2 = this.imageUpdater;
                    TLRPC$UserProfilePhoto tLRPC$UserProfilePhoto = user.photo;
                    if (tLRPC$UserProfilePhoto == null || tLRPC$UserProfilePhoto.photo_big == null || (tLRPC$UserProfilePhoto instanceof TLRPC$TL_userProfilePhotoEmpty)) {
                        z = false;
                    }
                    imageUpdater2.openMenu(z, new Runnable() {
                        public final void run() {
                            ProfileActivity.this.lambda$onWriteButtonClick$14$ProfileActivity();
                        }
                    }, new DialogInterface.OnDismissListener() {
                        public final void onDismiss(DialogInterface dialogInterface) {
                            ProfileActivity.this.lambda$onWriteButtonClick$15$ProfileActivity(dialogInterface);
                        }
                    });
                    this.cameraDrawable.setCurrentFrame(0);
                    this.cameraDrawable.setCustomEndFrame(43);
                    this.writeButton.playAnimation();
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
            TLRPC$User user2 = getMessagesController().getUser(Integer.valueOf(this.user_id));
            if (user2 != null && !(user2 instanceof TLRPC$TL_userEmpty)) {
                Bundle bundle = new Bundle();
                bundle.putInt("user_id", this.user_id);
                if (getMessagesController().checkCanOpenChat(bundle, this)) {
                    if (!AndroidUtilities.isTablet()) {
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
                    presentFragment(chatActivity, true);
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
    /* renamed from: lambda$onWriteButtonClick$14 */
    public /* synthetic */ void lambda$onWriteButtonClick$14$ProfileActivity() {
        MessagesController.getInstance(this.currentAccount).deleteUserPhoto((TLRPC$InputPhoto) null);
        this.cameraDrawable.setCurrentFrame(0);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onWriteButtonClick$15 */
    public /* synthetic */ void lambda$onWriteButtonClick$15$ProfileActivity(DialogInterface dialogInterface) {
        this.cameraDrawable.setCustomEndFrame(86);
        this.writeButton.playAnimation();
    }

    /* access modifiers changed from: private */
    public void openDiscussion() {
        TLRPC$ChatFull tLRPC$ChatFull = this.chatInfo;
        if (tLRPC$ChatFull != null && tLRPC$ChatFull.linked_chat_id != 0) {
            Bundle bundle = new Bundle();
            bundle.putInt("chat_id", this.chatInfo.linked_chat_id);
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
            TLRPC$User user = getMessagesController().getUser(Integer.valueOf(tLRPC$ChatParticipant2.user_id));
            if (user == null || tLRPC$ChatParticipant2.user_id == getUserConfig().getClientUserId()) {
                return false;
            }
            this.selectedUser = tLRPC$ChatParticipant2.user_id;
            ArrayList arrayList3 = null;
            if (ChatObject.isChannel(this.currentChat)) {
                TLRPC$ChannelParticipant tLRPC$ChannelParticipant2 = ((TLRPC$TL_chatChannelParticipant) tLRPC$ChatParticipant2).channelParticipant;
                getMessagesController().getUser(Integer.valueOf(tLRPC$ChatParticipant2.user_id));
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
            builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[0]), AndroidUtilities.toIntArray(arrayList2), new DialogInterface.OnClickListener(arrayList3, tLRPC$ChatParticipant, tLRPC$ChannelParticipant, user, z3) {
                public final /* synthetic */ ArrayList f$1;
                public final /* synthetic */ TLRPC$ChatParticipant f$2;
                public final /* synthetic */ TLRPC$ChannelParticipant f$3;
                public final /* synthetic */ TLRPC$User f$4;
                public final /* synthetic */ boolean f$5;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    ProfileActivity.this.lambda$onMemberClick$17$ProfileActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, dialogInterface, i);
                }
            });
            AlertDialog create = builder.create();
            showDialog(create);
            if (z7) {
                create.setItemColor(arrayList.size() - 1, Theme.getColor("dialogTextRed2"), Theme.getColor("dialogRedIcon"));
            }
        } else if (tLRPC$ChatParticipant2.user_id == getUserConfig().getClientUserId()) {
            return false;
        } else {
            Bundle bundle = new Bundle();
            bundle.putInt("user_id", tLRPC$ChatParticipant2.user_id);
            bundle.putBoolean("preload_messages", true);
            presentFragment(new ProfileActivity(bundle));
        }
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onMemberClick$17 */
    public /* synthetic */ void lambda$onMemberClick$17$ProfileActivity(ArrayList arrayList, TLRPC$ChatParticipant tLRPC$ChatParticipant, TLRPC$ChannelParticipant tLRPC$ChannelParticipant, TLRPC$User tLRPC$User, boolean z, DialogInterface dialogInterface, int i) {
        if (((Integer) arrayList.get(i)).intValue() == 2) {
            kickUser(this.selectedUser, tLRPC$ChatParticipant);
            return;
        }
        int intValue = ((Integer) arrayList.get(i)).intValue();
        if (intValue == 1 && ((tLRPC$ChannelParticipant instanceof TLRPC$TL_channelParticipantAdmin) || (tLRPC$ChatParticipant instanceof TLRPC$TL_chatParticipantAdmin))) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setMessage(LocaleController.formatString("AdminWillBeRemoved", NUM, ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name)));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener(tLRPC$ChannelParticipant, intValue, tLRPC$User, tLRPC$ChatParticipant, z) {
                public final /* synthetic */ TLRPC$ChannelParticipant f$1;
                public final /* synthetic */ int f$2;
                public final /* synthetic */ TLRPC$User f$3;
                public final /* synthetic */ TLRPC$ChatParticipant f$4;
                public final /* synthetic */ boolean f$5;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    ProfileActivity.this.lambda$onMemberClick$16$ProfileActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        } else if (tLRPC$ChannelParticipant != null) {
            openRightsEdit(intValue, tLRPC$User, tLRPC$ChatParticipant, tLRPC$ChannelParticipant.admin_rights, tLRPC$ChannelParticipant.banned_rights, tLRPC$ChannelParticipant.rank, z);
        } else {
            openRightsEdit(intValue, tLRPC$User, tLRPC$ChatParticipant, (TLRPC$TL_chatAdminRights) null, (TLRPC$TL_chatBannedRights) null, "", z);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onMemberClick$16 */
    public /* synthetic */ void lambda$onMemberClick$16$ProfileActivity(TLRPC$ChannelParticipant tLRPC$ChannelParticipant, int i, TLRPC$User tLRPC$User, TLRPC$ChatParticipant tLRPC$ChatParticipant, boolean z, DialogInterface dialogInterface, int i2) {
        TLRPC$ChannelParticipant tLRPC$ChannelParticipant2 = tLRPC$ChannelParticipant;
        if (tLRPC$ChannelParticipant2 != null) {
            openRightsEdit(i, tLRPC$User, tLRPC$ChatParticipant, tLRPC$ChannelParticipant2.admin_rights, tLRPC$ChannelParticipant2.banned_rights, tLRPC$ChannelParticipant2.rank, z);
            return;
        }
        openRightsEdit(i, tLRPC$User, tLRPC$ChatParticipant, (TLRPC$TL_chatAdminRights) null, (TLRPC$TL_chatBannedRights) null, "", z);
    }

    private void openRightsEdit(int i, TLRPC$User tLRPC$User, TLRPC$ChatParticipant tLRPC$ChatParticipant, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, String str, boolean z) {
        boolean[] zArr = new boolean[1];
        final TLRPC$User tLRPC$User2 = tLRPC$User;
        final boolean[] zArr2 = zArr;
        AnonymousClass22 r0 = new ChatRightsEditActivity(this, tLRPC$User2.id, this.chat_id, tLRPC$TL_chatAdminRights, this.currentChat.default_banned_rights, tLRPC$TL_chatBannedRights, str, i, true, false) {
            final /* synthetic */ ProfileActivity this$0;

            {
                this.this$0 = r12;
            }

            /* access modifiers changed from: protected */
            public void onTransitionAnimationEnd(boolean z, boolean z2) {
                if (!z && z2 && zArr2[0] && BulletinFactory.canShowBulletin(this.this$0)) {
                    BulletinFactory.createPromoteToAdminBulletin(this.this$0, tLRPC$User2.first_name).show();
                }
            }
        };
        final int i2 = i;
        final TLRPC$ChatParticipant tLRPC$ChatParticipant2 = tLRPC$ChatParticipant;
        final boolean z2 = z;
        final boolean[] zArr3 = zArr;
        r0.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() {
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
                        zArr3[0] = true;
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
                ProfileActivity.this.undoView.showWithAction((long) (-ProfileActivity.this.chat_id), ProfileActivity.this.currentChat.megagroup ? 10 : 9, (Object) tLRPC$User);
            }
        });
        presentFragment(r0);
    }

    /* access modifiers changed from: private */
    public boolean processOnClickOrPress(int i) {
        String str;
        TLRPC$Chat chat;
        String str2;
        TLRPC$UserFull tLRPC$UserFull;
        if (i == this.usernameRow || i == this.setUsernameRow) {
            if (this.user_id != 0) {
                TLRPC$User user = getMessagesController().getUser(Integer.valueOf(this.user_id));
                if (user == null || (str = user.username) == null) {
                    return false;
                }
            } else if (this.chat_id == 0 || (chat = getMessagesController().getChat(Integer.valueOf(this.chat_id))) == null || (str = chat.username) == null) {
                return false;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setItems(new CharSequence[]{LocaleController.getString("Copy", NUM)}, new DialogInterface.OnClickListener(str) {
                public final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    ProfileActivity.this.lambda$processOnClickOrPress$18$ProfileActivity(this.f$1, dialogInterface, i);
                }
            });
            showDialog(builder.create());
            return true;
        } else if (i == this.phoneRow || i == this.numberRow) {
            TLRPC$User user2 = getMessagesController().getUser(Integer.valueOf(this.user_id));
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
            builder2.setItems((CharSequence[]) arrayList.toArray(new CharSequence[0]), new DialogInterface.OnClickListener(arrayList2, user2) {
                public final /* synthetic */ ArrayList f$1;
                public final /* synthetic */ TLRPC$User f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    ProfileActivity.this.lambda$processOnClickOrPress$19$ProfileActivity(this.f$1, this.f$2, dialogInterface, i);
                }
            });
            showDialog(builder2.create());
            return true;
        } else if (i != this.channelInfoRow && i != this.userInfoRow && i != this.locationRow && i != this.bioRow) {
            return false;
        } else {
            if (i == this.bioRow && ((tLRPC$UserFull = this.userInfo) == null || TextUtils.isEmpty(tLRPC$UserFull.about))) {
                return false;
            }
            AlertDialog.Builder builder3 = new AlertDialog.Builder((Context) getParentActivity());
            builder3.setItems(new CharSequence[]{LocaleController.getString("Copy", NUM)}, new DialogInterface.OnClickListener(i) {
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    ProfileActivity.this.lambda$processOnClickOrPress$20$ProfileActivity(this.f$1, dialogInterface, i);
                }
            });
            showDialog(builder3.create());
            return true;
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$processOnClickOrPress$18 */
    public /* synthetic */ void lambda$processOnClickOrPress$18$ProfileActivity(String str, DialogInterface dialogInterface, int i) {
        String str2;
        if (i == 0) {
            try {
                ClipboardManager clipboardManager = (ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard");
                if (this.user_id != 0) {
                    str2 = "@" + str;
                    BulletinFactory.of((BaseFragment) this).createCopyBulletin(LocaleController.getString("UsernameCopied", NUM)).show();
                } else {
                    str2 = "https://" + MessagesController.getInstance(UserConfig.selectedAccount).linkPrefix + "/" + str;
                    BulletinFactory.of((BaseFragment) this).createCopyBulletin(LocaleController.getString("LinkCopied", NUM)).show();
                }
                clipboardManager.setPrimaryClip(ClipData.newPlainText("label", str2));
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$processOnClickOrPress$19 */
    public /* synthetic */ void lambda$processOnClickOrPress$19$ProfileActivity(ArrayList arrayList, TLRPC$User tLRPC$User, DialogInterface dialogInterface, int i) {
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
                BulletinFactory.of((BaseFragment) this).createCopyBulletin(LocaleController.getString("PhoneCopied", NUM)).show();
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
    /* renamed from: lambda$processOnClickOrPress$20 */
    public /* synthetic */ void lambda$processOnClickOrPress$20$ProfileActivity(int i, DialogInterface dialogInterface, int i2) {
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
                    BulletinFactory.of((BaseFragment) this).createCopyBulletin(LocaleController.getString("BioCopied", NUM)).show();
                } else {
                    BulletinFactory.of((BaseFragment) this).createCopyBulletin(LocaleController.getString("TextCopied", NUM)).show();
                }
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public void leaveChatPressed() {
        AlertsCreator.createClearOrDeleteDialogAlert(this, false, this.currentChat, (TLRPC$User) null, false, true, new MessagesStorage.BooleanCallback() {
            public final void run(boolean z) {
                ProfileActivity.this.lambda$leaveChatPressed$21$ProfileActivity(z);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$leaveChatPressed$21 */
    public /* synthetic */ void lambda$leaveChatPressed$21$ProfileActivity(boolean z) {
        this.playProfileAnimation = 0;
        NotificationCenter notificationCenter = getNotificationCenter();
        int i = NotificationCenter.closeChats;
        notificationCenter.removeObserver(this, i);
        getNotificationCenter().postNotificationName(i, new Object[0]);
        finishFragment();
        getNotificationCenter().postNotificationName(NotificationCenter.needDeleteDialog, Long.valueOf((long) (-this.currentChat.id)), null, this.currentChat, Boolean.valueOf(z));
    }

    /* access modifiers changed from: private */
    public void getChannelParticipants(boolean z) {
        SparseArray<TLRPC$ChatParticipant> sparseArray;
        if (!this.loadingUsers && (sparseArray = this.participantsMap) != null && this.chatInfo != null) {
            this.loadingUsers = true;
            int i = 0;
            int i2 = (sparseArray.size() == 0 || !z) ? 0 : 300;
            TLRPC$TL_channels_getParticipants tLRPC$TL_channels_getParticipants = new TLRPC$TL_channels_getParticipants();
            tLRPC$TL_channels_getParticipants.channel = getMessagesController().getInputChannel(this.chat_id);
            tLRPC$TL_channels_getParticipants.filter = new TLRPC$TL_channelParticipantsRecent();
            if (!z) {
                i = this.participantsMap.size();
            }
            tLRPC$TL_channels_getParticipants.offset = i;
            tLRPC$TL_channels_getParticipants.limit = 200;
            getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tLRPC$TL_channels_getParticipants, new RequestDelegate(tLRPC$TL_channels_getParticipants, i2) {
                public final /* synthetic */ TLRPC$TL_channels_getParticipants f$1;
                public final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    ProfileActivity.this.lambda$getChannelParticipants$23$ProfileActivity(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                }
            }), this.classGuid);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getChannelParticipants$23 */
    public /* synthetic */ void lambda$getChannelParticipants$23$ProfileActivity(TLRPC$TL_channels_getParticipants tLRPC$TL_channels_getParticipants, int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject, tLRPC$TL_channels_getParticipants) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;
            public final /* synthetic */ TLRPC$TL_channels_getParticipants f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ProfileActivity.this.lambda$getChannelParticipants$22$ProfileActivity(this.f$1, this.f$2, this.f$3);
            }
        }, (long) i);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getChannelParticipants$22 */
    public /* synthetic */ void lambda$getChannelParticipants$22$ProfileActivity(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, TLRPC$TL_channels_getParticipants tLRPC$TL_channels_getParticipants) {
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
                getMessagesStorage().updateChannelUsers(this.chat_id, tLRPC$TL_channels_channelParticipants.participants);
            }
            for (int i = 0; i < tLRPC$TL_channels_channelParticipants.participants.size(); i++) {
                TLRPC$TL_chatChannelParticipant tLRPC$TL_chatChannelParticipant = new TLRPC$TL_chatChannelParticipant();
                TLRPC$ChannelParticipant tLRPC$ChannelParticipant = tLRPC$TL_channels_channelParticipants.participants.get(i);
                tLRPC$TL_chatChannelParticipant.channelParticipant = tLRPC$ChannelParticipant;
                tLRPC$TL_chatChannelParticipant.inviter_id = tLRPC$ChannelParticipant.inviter_id;
                int peerId = MessageObject.getPeerId(tLRPC$ChannelParticipant.peer);
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
            } else if (this.sharedMediaLayout.isSearchItemVisible()) {
                searchItem2.setVisibility(0);
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
            ActionBar actionBar = this.actionBar;
            Property<ActionBar, Float> property11 = this.ACTIONBAR_HEADER_PROGRESS;
            float[] fArr11 = new float[1];
            fArr11[0] = z ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(actionBar, property11, fArr11));
            SimpleTextView simpleTextView = this.onlineTextView[1];
            Property property12 = View.ALPHA;
            float[] fArr12 = new float[1];
            fArr12[0] = z ? 0.0f : 1.0f;
            arrayList.add(ObjectAnimator.ofFloat(simpleTextView, property12, fArr12));
            AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher = this.mediaCounterTextView;
            Property property13 = View.ALPHA;
            float[] fArr13 = new float[1];
            if (!z) {
                f = 0.0f;
            }
            fArr13[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(clippingTextViewSwitcher, property13, fArr13));
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
                            AnimatorSet unused = ProfileActivity.this.headerShadowAnimatorSet = new AnimatorSet();
                            AnimatorSet access$14100 = ProfileActivity.this.headerShadowAnimatorSet;
                            ProfileActivity profileActivity = ProfileActivity.this;
                            access$14100.playTogether(new Animator[]{ObjectAnimator.ofFloat(profileActivity, profileActivity.HEADER_SHADOW, new float[]{1.0f})});
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
        }
    }

    /* access modifiers changed from: private */
    public void openAddMember() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("addToGroup", true);
        bundle.putInt("chatId", this.currentChat.id);
        GroupCreateActivity groupCreateActivity = new GroupCreateActivity(bundle);
        groupCreateActivity.setInfo(this.chatInfo);
        TLRPC$ChatFull tLRPC$ChatFull = this.chatInfo;
        if (!(tLRPC$ChatFull == null || tLRPC$ChatFull.participants == null)) {
            SparseArray sparseArray = new SparseArray();
            for (int i = 0; i < this.chatInfo.participants.participants.size(); i++) {
                sparseArray.put(this.chatInfo.participants.participants.get(i).user_id, (Object) null);
            }
            groupCreateActivity.setIgnoreUsers(sparseArray);
        }
        groupCreateActivity.setDelegate((GroupCreateActivity.ContactsAddActivityDelegate) new GroupCreateActivity.ContactsAddActivityDelegate() {
            public final void didSelectUsers(ArrayList arrayList, int i) {
                ProfileActivity.this.lambda$openAddMember$24$ProfileActivity(arrayList, i);
            }

            public /* synthetic */ void needAddBot(TLRPC$User tLRPC$User) {
                GroupCreateActivity.ContactsAddActivityDelegate.CC.$default$needAddBot(this, tLRPC$User);
            }
        });
        presentFragment(groupCreateActivity);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$openAddMember$24 */
    public /* synthetic */ void lambda$openAddMember$24$ProfileActivity(ArrayList arrayList, int i) {
        HashSet hashSet = new HashSet();
        if (this.chatInfo.participants.participants != null) {
            for (int i2 = 0; i2 < this.chatInfo.participants.participants.size(); i2++) {
                hashSet.add(Integer.valueOf(this.chatInfo.participants.participants.get(i2).user_id));
            }
        }
        int size = arrayList.size();
        for (int i3 = 0; i3 < size; i3++) {
            TLRPC$User tLRPC$User = (TLRPC$User) arrayList.get(i3);
            getMessagesController().addUserToChat(this.chat_id, tLRPC$User, i, (String) null, this, (Runnable) null);
            if (!hashSet.contains(Integer.valueOf(tLRPC$User.id))) {
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

    /* access modifiers changed from: private */
    public void updateSelectedMediaTabText() {
        SharedMediaLayout sharedMediaLayout2 = this.sharedMediaLayout;
        if (sharedMediaLayout2 != null && this.mediaCounterTextView != null) {
            int closestTab = sharedMediaLayout2.getClosestTab();
            int[] lastMediaCount = this.sharedMediaPreloader.getLastMediaCount();
            if (closestTab == 0) {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("Media", lastMediaCount[0]));
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
                    if (z2 && this.chat_id != 0) {
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
                        ActionBarMenuItem actionBarMenuItem3 = this.otherItem;
                        if (actionBarMenuItem3 != null) {
                            actionBarMenuItem3.showSubItem(21);
                            if (this.imageUpdater != null) {
                                this.otherItem.showSubItem(36);
                                this.otherItem.showSubItem(34);
                                this.otherItem.showSubItem(35);
                                this.otherItem.hideSubItem(33);
                                this.otherItem.hideSubItem(31);
                            }
                        }
                        ActionBarMenuItem actionBarMenuItem4 = this.searchItem;
                        if (actionBarMenuItem4 != null) {
                            actionBarMenuItem4.setEnabled(false);
                        }
                        this.isPulledDown = true;
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
                        float dp = (!this.openAnimationInProgress || this.playProfileAnimation != 2) ? 0.0f : (-(1.0f - this.animationProgress)) * ((float) AndroidUtilities.dp(50.0f));
                        this.nameTextView[1].setTranslationX(AndroidUtilities.dpf2(16.0f) - ((float) this.nameTextView[1].getLeft()));
                        this.nameTextView[1].setTranslationY(((f4 - AndroidUtilities.dpf2(38.0f)) - ((float) this.nameTextView[1].getBottom())) + dp);
                        this.onlineTextView[1].setTranslationX(AndroidUtilities.dpf2(16.0f) - ((float) this.onlineTextView[1].getLeft()));
                        this.onlineTextView[1].setTranslationY(((f4 - AndroidUtilities.dpf2(18.0f)) - ((float) this.onlineTextView[1].getBottom())) + dp);
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
                this.avatarScale = ((min * 18.0f) + 42.0f) / 42.0f;
                float f5 = (0.12f * min) + 1.0f;
                ValueAnimator valueAnimator3 = this.expandAnimator;
                if (valueAnimator3 == null || !valueAnimator3.isRunning()) {
                    this.avatarContainer.setScaleX(this.avatarScale);
                    this.avatarContainer.setScaleY(this.avatarScale);
                    this.avatarContainer.setTranslationX(this.avatarX);
                    this.avatarContainer.setTranslationY((float) Math.ceil((double) this.avatarY));
                    float dp2 = (((float) AndroidUtilities.dp(42.0f)) * this.avatarScale) - ((float) AndroidUtilities.dp(42.0f));
                    this.timeItem.setTranslationX(this.avatarContainer.getX() + ((float) AndroidUtilities.dp(16.0f)) + dp2);
                    this.timeItem.setTranslationY(this.avatarContainer.getY() + ((float) AndroidUtilities.dp(15.0f)) + dp2);
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
        if (this.isPulledDown || (this.overlaysView.animator != null && this.overlaysView.animator.isRunning())) {
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
        int i3;
        int i4;
        TLRPC$Chat tLRPC$Chat;
        RecyclerListView recyclerListView;
        RecyclerListView recyclerListView2;
        RecyclerListView.Holder holder;
        int i5 = 0;
        if (i == NotificationCenter.updateInterfaces) {
            int intValue = objArr[0].intValue();
            boolean z = ((intValue & 2) == 0 && (intValue & 1) == 0 && (intValue & 4) == 0) ? false : true;
            if (this.user_id != 0) {
                if (z) {
                    updateProfileData();
                }
                if ((intValue & 1024) != 0 && (recyclerListView2 = this.listView) != null && (holder = (RecyclerListView.Holder) recyclerListView2.findViewHolderForPosition(this.phoneRow)) != null) {
                    this.listAdapter.onBindViewHolder(holder, this.phoneRow);
                }
            } else if (this.chat_id != 0) {
                int i6 = intValue & 8192;
                if (!(i6 == 0 && (intValue & 8) == 0 && (intValue & 16) == 0 && (intValue & 32) == 0 && (intValue & 4) == 0)) {
                    if (i6 != 0) {
                        updateListAnimated(true);
                    } else {
                        updateOnlineCount(true);
                    }
                    updateProfileData();
                }
                if (z && (recyclerListView = this.listView) != null) {
                    int childCount = recyclerListView.getChildCount();
                    while (i5 < childCount) {
                        View childAt = this.listView.getChildAt(i5);
                        if (childAt instanceof UserCell) {
                            ((UserCell) childAt).update(intValue);
                        }
                        i5++;
                    }
                }
            }
        } else if (i == NotificationCenter.chatOnlineCountDidLoad) {
            Integer num = objArr[0];
            if (this.chatInfo != null && (tLRPC$Chat = this.currentChat) != null && tLRPC$Chat.id == num.intValue()) {
                this.chatInfo.online_count = objArr[1].intValue();
                updateOnlineCount(true);
                updateProfileData();
            }
        } else if (i == NotificationCenter.contactsDidLoad) {
            createActionBarMenu(true);
        } else if (i == NotificationCenter.encryptedChatCreated) {
            if (this.creatingChat) {
                AndroidUtilities.runOnUIThread(new Runnable(objArr) {
                    public final /* synthetic */ Object[] f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        ProfileActivity.this.lambda$didReceivedNotification$25$ProfileActivity(this.f$1);
                    }
                });
            }
        } else if (i == NotificationCenter.encryptedChatUpdated) {
            TLRPC$EncryptedChat tLRPC$EncryptedChat = objArr[0];
            TLRPC$EncryptedChat tLRPC$EncryptedChat2 = this.currentEncryptedChat;
            if (tLRPC$EncryptedChat2 != null && tLRPC$EncryptedChat.id == tLRPC$EncryptedChat2.id) {
                this.currentEncryptedChat = tLRPC$EncryptedChat;
                updateListAnimated(false);
                updateTimeItem();
            }
        } else if (i == NotificationCenter.blockedUsersDidLoad) {
            boolean z2 = this.userBlocked;
            boolean z3 = getMessagesController().blockePeers.indexOfKey(this.user_id) >= 0;
            this.userBlocked = z3;
            if (z2 != z3) {
                createActionBarMenu(true);
                updateListAnimated(false);
            }
        } else if (i == NotificationCenter.groupCallUpdated) {
            Integer num2 = objArr[0];
            if (this.currentChat != null) {
                int intValue2 = num2.intValue();
                TLRPC$Chat tLRPC$Chat2 = this.currentChat;
                if (intValue2 == tLRPC$Chat2.id && ChatObject.canManageCalls(tLRPC$Chat2)) {
                    TLRPC$ChatFull chatFull = MessagesController.getInstance(this.currentAccount).getChatFull(num2.intValue());
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
            if (tLRPC$ChatFull3.id == this.chat_id) {
                boolean booleanValue = objArr[2].booleanValue();
                TLRPC$ChatFull tLRPC$ChatFull4 = this.chatInfo;
                if ((tLRPC$ChatFull4 instanceof TLRPC$TL_channelFull) && tLRPC$ChatFull3.participants == null) {
                    tLRPC$ChatFull3.participants = tLRPC$ChatFull4.participants;
                }
                if (tLRPC$ChatFull4 == null && (tLRPC$ChatFull3 instanceof TLRPC$TL_channelFull)) {
                    i5 = 1;
                }
                this.chatInfo = tLRPC$ChatFull3;
                if (this.mergeDialogId == 0 && (i4 = tLRPC$ChatFull3.migrated_from_chat_id) != 0) {
                    this.mergeDialogId = (long) (-i4);
                    getMediaDataController().getMediaCount(this.mergeDialogId, 0, this.classGuid, true);
                }
                fetchUsersFromChannelInfo();
                ProfileGalleryView profileGalleryView = this.avatarsViewPager;
                if (profileGalleryView != null) {
                    profileGalleryView.setChatInfo(this.chatInfo);
                }
                updateListAnimated(true);
                TLRPC$Chat chat = getMessagesController().getChat(Integer.valueOf(this.chat_id));
                if (chat != null) {
                    this.currentChat = chat;
                    createActionBarMenu(true);
                }
                if (this.currentChat.megagroup && (i5 != 0 || !booleanValue)) {
                    getChannelParticipants(true);
                }
                updateTimeItem();
            }
        } else if (i == NotificationCenter.closeChats) {
            removeSelfFromStack();
        } else if (i == NotificationCenter.botInfoDidLoad) {
            TLRPC$BotInfo tLRPC$BotInfo = objArr[0];
            if (tLRPC$BotInfo.user_id == this.user_id) {
                this.botInfo = tLRPC$BotInfo;
                updateListAnimated(false);
            }
        } else if (i == NotificationCenter.userInfoDidLoad) {
            if (objArr[0].intValue() == this.user_id) {
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
                updateTimeItem();
            }
        } else if (i == NotificationCenter.didReceiveNewMessages) {
            if (!objArr[2].booleanValue()) {
                long dialogId = getDialogId();
                if (dialogId == objArr[0].longValue()) {
                    int i7 = (int) dialogId;
                    ArrayList arrayList = objArr[1];
                    while (i5 < arrayList.size()) {
                        MessageObject messageObject = (MessageObject) arrayList.get(i5);
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
                        i5++;
                    }
                }
            }
        } else if (i == NotificationCenter.emojiDidLoad) {
            RecyclerListView recyclerListView3 = this.listView;
            if (recyclerListView3 != null) {
                recyclerListView3.invalidateViews();
            }
        } else if (i == NotificationCenter.reloadInterface) {
            int i8 = this.emptyRow;
            updateRowsIds();
            ListAdapter listAdapter3 = this.listAdapter;
            if (listAdapter3 != null && i8 != (i3 = this.emptyRow)) {
                if (i3 == -1) {
                    listAdapter3.notifyItemRemoved(i3);
                } else {
                    listAdapter3.notifyItemInserted(i3);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$didReceivedNotification$25 */
    public /* synthetic */ void lambda$didReceivedNotification$25$ProfileActivity(Object[] objArr) {
        NotificationCenter notificationCenter = getNotificationCenter();
        int i = NotificationCenter.closeChats;
        notificationCenter.removeObserver(this, i);
        getNotificationCenter().postNotificationName(i, new Object[0]);
        Bundle bundle = new Bundle();
        bundle.putInt("enc_id", objArr[0].id);
        presentFragment(new ChatActivity(bundle), true);
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
        if (this.sharedMediaRow == -1 || (sharedMediaLayout2 = this.sharedMediaLayout) == null) {
            return true;
        }
        sharedMediaLayout2.getHitRect(this.rect);
        if (!this.rect.contains((int) motionEvent.getX(), ((int) motionEvent.getY()) - this.actionBar.getMeasuredHeight())) {
            return true;
        }
        return this.sharedMediaLayout.isCurrentTabFirst();
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
        int i3;
        float f2 = f;
        this.animationProgress = f2;
        this.listView.setAlpha(f2);
        this.listView.setTranslationX(((float) AndroidUtilities.dp(48.0f)) - (((float) AndroidUtilities.dp(48.0f)) * f2));
        int i4 = 2;
        if (this.playProfileAnimation != 2 || (i = this.avatarColor) == 0) {
            i = AvatarDrawable.getProfileBackColorForId((this.user_id != 0 || (ChatObject.isChannel(this.chat_id, this.currentAccount) && !this.currentChat.megagroup)) ? 5 : this.chat_id);
        }
        int color = Theme.getColor("actionBarDefault");
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        this.topView.setBackgroundColor(Color.rgb(red + ((int) (((float) (Color.red(i) - red)) * f2)), green + ((int) (((float) (Color.green(i) - green)) * f2)), blue + ((int) (((float) (Color.blue(i) - blue)) * f2))));
        int iconColorForId = AvatarDrawable.getIconColorForId((this.user_id != 0 || (ChatObject.isChannel(this.chat_id, this.currentAccount) && !this.currentChat.megagroup)) ? 5 : this.chat_id);
        int color2 = Theme.getColor("actionBarDefaultIcon");
        int red2 = Color.red(color2);
        int green2 = Color.green(color2);
        int blue2 = Color.blue(color2);
        this.actionBar.setItemsColor(Color.rgb(red2 + ((int) (((float) (Color.red(iconColorForId) - red2)) * f2)), green2 + ((int) (((float) (Color.green(iconColorForId) - green2)) * f2)), blue2 + ((int) (((float) (Color.blue(iconColorForId) - blue2)) * f2))), false);
        int color3 = Theme.getColor("profile_title");
        int color4 = Theme.getColor("actionBarDefaultTitle");
        int red3 = Color.red(color4);
        int green3 = Color.green(color4);
        int blue3 = Color.blue(color4);
        int alpha = Color.alpha(color4);
        int red4 = (int) (((float) (Color.red(color3) - red3)) * f2);
        int green4 = (int) (((float) (Color.green(color3) - green3)) * f2);
        int blue4 = (int) (((float) (Color.blue(color3) - blue3)) * f2);
        int alpha2 = (int) (((float) (Color.alpha(color3) - alpha)) * f2);
        int i5 = 0;
        while (true) {
            i2 = 1;
            if (i5 >= i4) {
                break;
            }
            SimpleTextView[] simpleTextViewArr = this.nameTextView;
            if (!(simpleTextViewArr[i5] == null || (i5 == 1 && this.playProfileAnimation == i4))) {
                simpleTextViewArr[i5].setTextColor(Color.argb(alpha + alpha2, red3 + red4, green3 + green4, blue3 + blue4));
            }
            i5++;
            i4 = 2;
        }
        if (this.isOnline[0]) {
            i3 = Theme.getColor("profile_status");
        } else {
            i3 = AvatarDrawable.getProfileTextColorForId((this.user_id != 0 || (ChatObject.isChannel(this.chat_id, this.currentAccount) && !this.currentChat.megagroup)) ? 5 : this.chat_id);
        }
        int i6 = 0;
        int color5 = Theme.getColor(this.isOnline[0] ? "chat_status" : "actionBarDefaultSubtitle");
        int red5 = Color.red(color5);
        int green5 = Color.green(color5);
        int blue5 = Color.blue(color5);
        int alpha3 = Color.alpha(color5);
        int red6 = (int) (((float) (Color.red(i3) - red5)) * f2);
        int green6 = (int) (((float) (Color.green(i3) - green5)) * f2);
        int blue6 = (int) (((float) (Color.blue(i3) - blue5)) * f2);
        int alpha4 = (int) (((float) (Color.alpha(i3) - alpha3)) * f2);
        int i7 = 2;
        while (i6 < i7) {
            SimpleTextView[] simpleTextViewArr2 = this.onlineTextView;
            if (!(simpleTextViewArr2[i6] == null || (i6 == i2 && this.playProfileAnimation == i7))) {
                simpleTextViewArr2[i6].setTextColor(Color.argb(alpha3 + alpha4, red5 + red6, green5 + green6, blue5 + blue6));
            }
            i6++;
            i7 = 2;
            i2 = 1;
        }
        this.extraHeight = this.initialAnimationExtraHeight * f2;
        int i8 = this.user_id;
        if (i8 == 0) {
            i8 = this.chat_id;
        }
        int profileColorForId = AvatarDrawable.getProfileColorForId(i8);
        int i9 = this.user_id;
        if (i9 == 0) {
            i9 = this.chat_id;
        }
        int colorForId = AvatarDrawable.getColorForId(i9);
        if (profileColorForId != colorForId) {
            this.avatarDrawable.setColor(Color.rgb(Color.red(colorForId) + ((int) (((float) (Color.red(profileColorForId) - Color.red(colorForId))) * f2)), Color.green(colorForId) + ((int) (((float) (Color.green(profileColorForId) - Color.green(colorForId))) * f2)), Color.blue(colorForId) + ((int) (((float) (Color.blue(profileColorForId) - Color.blue(colorForId))) * f2))));
            this.avatarImage.invalidate();
        }
        this.topView.invalidate();
        needLayout(true);
        this.fragmentView.invalidate();
    }

    /* access modifiers changed from: protected */
    public AnimatorSet onCustomTransitionAnimation(boolean z, final Runnable runnable) {
        if (this.playProfileAnimation == 0 || !this.allowProfileAnimation || this.isPulledDown) {
            return null;
        }
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(this.playProfileAnimation == 2 ? 250 : 180);
        this.listView.setLayerType(2, (Paint) null);
        ActionBarMenu createMenu = this.actionBar.createMenu();
        if (createMenu.getItem(10) == null && this.animatingItem == null) {
            this.animatingItem = createMenu.addItem(10, NUM);
        }
        if (z) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.onlineTextView[1].getLayoutParams();
            layoutParams.rightMargin = (int) ((AndroidUtilities.density * -21.0f) + ((float) AndroidUtilities.dp(8.0f)));
            this.onlineTextView[1].setLayoutParams(layoutParams);
            if (this.playProfileAnimation != 2) {
                int ceil = (int) Math.ceil((double) (((float) (AndroidUtilities.displaySize.x - AndroidUtilities.dp(126.0f))) + (AndroidUtilities.density * 21.0f)));
                float measureText = (this.nameTextView[1].getPaint().measureText(this.nameTextView[1].getText().toString()) * 1.12f) + ((float) this.nameTextView[1].getSideDrawablesSize());
                FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.nameTextView[1].getLayoutParams();
                float f = (float) ceil;
                if (f < measureText) {
                    layoutParams2.width = (int) Math.ceil((double) (f / 1.12f));
                } else {
                    layoutParams2.width = -2;
                }
                this.nameTextView[1].setLayoutParams(layoutParams2);
                this.initialAnimationExtraHeight = (float) AndroidUtilities.dp(88.0f);
            } else {
                FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.nameTextView[1].getLayoutParams();
                layoutParams3.width = (int) (((float) (AndroidUtilities.displaySize.x - AndroidUtilities.dp(32.0f))) / 1.67f);
                this.nameTextView[1].setLayoutParams(layoutParams3);
            }
            this.fragmentView.setBackgroundColor(0);
            setAnimationProgress(0.0f);
            ArrayList arrayList = new ArrayList();
            arrayList.add(ObjectAnimator.ofFloat(this, "animationProgress", new float[]{0.0f, 1.0f}));
            RLottieImageView rLottieImageView = this.writeButton;
            if (rLottieImageView != null && rLottieImageView.getTag() == null) {
                this.writeButton.setScaleX(0.2f);
                this.writeButton.setScaleY(0.2f);
                this.writeButton.setAlpha(0.0f);
                arrayList.add(ObjectAnimator.ofFloat(this.writeButton, View.SCALE_X, new float[]{1.0f}));
                arrayList.add(ObjectAnimator.ofFloat(this.writeButton, View.SCALE_Y, new float[]{1.0f}));
                arrayList.add(ObjectAnimator.ofFloat(this.writeButton, View.ALPHA, new float[]{1.0f}));
            }
            if (this.playProfileAnimation == 2) {
                this.avatarColor = AndroidUtilities.calcBitmapColor(this.avatarImage.getImageReceiver().getBitmap());
                this.nameTextView[1].setTextColor(-1);
                this.onlineTextView[1].setTextColor(Color.argb(179, 255, 255, 255));
                this.actionBar.setItemsBackgroundColor(NUM, false);
                this.overlaysView.setOverlaysVisible();
            }
            int i = 0;
            while (i < 2) {
                this.onlineTextView[i].setAlpha(i == 0 ? 1.0f : 0.0f);
                this.nameTextView[i].setAlpha(i == 0 ? 1.0f : 0.0f);
                SimpleTextView simpleTextView = this.onlineTextView[i];
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = i == 0 ? 0.0f : 1.0f;
                arrayList.add(ObjectAnimator.ofFloat(simpleTextView, property, fArr));
                SimpleTextView simpleTextView2 = this.nameTextView[i];
                Property property2 = View.ALPHA;
                float[] fArr2 = new float[1];
                fArr2[0] = i == 0 ? 0.0f : 1.0f;
                arrayList.add(ObjectAnimator.ofFloat(simpleTextView2, property2, fArr2));
                i++;
            }
            if (this.timeItem.getTag() != null) {
                arrayList.add(ObjectAnimator.ofFloat(this.timeItem, View.ALPHA, new float[]{0.0f}));
                arrayList.add(ObjectAnimator.ofFloat(this.timeItem, View.SCALE_X, new float[]{0.0f}));
                arrayList.add(ObjectAnimator.ofFloat(this.timeItem, View.SCALE_Y, new float[]{0.0f}));
            }
            ActionBarMenuItem actionBarMenuItem = this.animatingItem;
            if (actionBarMenuItem != null) {
                actionBarMenuItem.setAlpha(1.0f);
                arrayList.add(ObjectAnimator.ofFloat(this.animatingItem, View.ALPHA, new float[]{0.0f}));
            }
            if (this.callItemVisible) {
                this.callItem.setAlpha(0.0f);
                arrayList.add(ObjectAnimator.ofFloat(this.callItem, View.ALPHA, new float[]{1.0f}));
            }
            if (this.videoCallItemVisible) {
                this.videoCallItem.setAlpha(0.0f);
                arrayList.add(ObjectAnimator.ofFloat(this.videoCallItem, View.ALPHA, new float[]{1.0f}));
            }
            if (this.editItemVisible) {
                this.editItem.setAlpha(0.0f);
                arrayList.add(ObjectAnimator.ofFloat(this.editItem, View.ALPHA, new float[]{1.0f}));
            }
            animatorSet.playTogether(arrayList);
        } else {
            this.initialAnimationExtraHeight = this.extraHeight;
            ArrayList arrayList2 = new ArrayList();
            arrayList2.add(ObjectAnimator.ofFloat(this, "animationProgress", new float[]{1.0f, 0.0f}));
            RLottieImageView rLottieImageView2 = this.writeButton;
            if (rLottieImageView2 != null) {
                arrayList2.add(ObjectAnimator.ofFloat(rLottieImageView2, View.SCALE_X, new float[]{0.2f}));
                arrayList2.add(ObjectAnimator.ofFloat(this.writeButton, View.SCALE_Y, new float[]{0.2f}));
                arrayList2.add(ObjectAnimator.ofFloat(this.writeButton, View.ALPHA, new float[]{0.0f}));
            }
            int i2 = 0;
            while (i2 < 2) {
                SimpleTextView simpleTextView3 = this.onlineTextView[i2];
                Property property3 = View.ALPHA;
                float[] fArr3 = new float[1];
                fArr3[0] = i2 == 0 ? 1.0f : 0.0f;
                arrayList2.add(ObjectAnimator.ofFloat(simpleTextView3, property3, fArr3));
                SimpleTextView simpleTextView4 = this.nameTextView[i2];
                Property property4 = View.ALPHA;
                float[] fArr4 = new float[1];
                fArr4[0] = i2 == 0 ? 1.0f : 0.0f;
                arrayList2.add(ObjectAnimator.ofFloat(simpleTextView4, property4, fArr4));
                i2++;
            }
            if (this.timeItem.getTag() != null) {
                arrayList2.add(ObjectAnimator.ofFloat(this.timeItem, View.ALPHA, new float[]{1.0f}));
                arrayList2.add(ObjectAnimator.ofFloat(this.timeItem, View.SCALE_X, new float[]{1.0f}));
                arrayList2.add(ObjectAnimator.ofFloat(this.timeItem, View.SCALE_Y, new float[]{1.0f}));
            }
            ActionBarMenuItem actionBarMenuItem2 = this.animatingItem;
            if (actionBarMenuItem2 != null) {
                actionBarMenuItem2.setAlpha(0.0f);
                arrayList2.add(ObjectAnimator.ofFloat(this.animatingItem, View.ALPHA, new float[]{1.0f}));
            }
            if (this.callItemVisible) {
                this.callItem.setAlpha(1.0f);
                arrayList2.add(ObjectAnimator.ofFloat(this.callItem, View.ALPHA, new float[]{0.0f}));
            }
            if (this.videoCallItemVisible) {
                this.videoCallItem.setAlpha(1.0f);
                arrayList2.add(ObjectAnimator.ofFloat(this.videoCallItem, View.ALPHA, new float[]{0.0f}));
            }
            if (this.editItemVisible) {
                this.editItem.setAlpha(1.0f);
                arrayList2.add(ObjectAnimator.ofFloat(this.editItem, View.ALPHA, new float[]{0.0f}));
            }
            animatorSet.playTogether(arrayList2);
        }
        this.profileTransitionInProgress = true;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                ProfileActivity.this.lambda$onCustomTransitionAnimation$26$ProfileActivity(valueAnimator);
            }
        });
        animatorSet.playTogether(new Animator[]{ofFloat});
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                ProfileActivity.this.listView.setLayerType(0, (Paint) null);
                if (ProfileActivity.this.animatingItem != null) {
                    ProfileActivity.this.actionBar.createMenu().clearItems();
                    ActionBarMenuItem unused = ProfileActivity.this.animatingItem = null;
                }
                runnable.run();
                if (ProfileActivity.this.playProfileAnimation == 2) {
                    int unused2 = ProfileActivity.this.playProfileAnimation = 1;
                    ProfileActivity.this.avatarImage.setForegroundAlpha(1.0f);
                    ProfileActivity.this.avatarContainer.setVisibility(8);
                    ProfileActivity.this.avatarsViewPager.resetCurrentItem();
                    ProfileActivity.this.avatarsViewPager.setVisibility(0);
                }
                ProfileActivity profileActivity = ProfileActivity.this;
                profileActivity.profileTransitionInProgress = false;
                profileActivity.fragmentView.invalidate();
            }
        });
        animatorSet.setInterpolator(this.playProfileAnimation == 2 ? CubicBezierInterpolator.DEFAULT : new DecelerateInterpolator());
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
    /* renamed from: lambda$onCustomTransitionAnimation$26 */
    public /* synthetic */ void lambda$onCustomTransitionAnimation$26$ProfileActivity(ValueAnimator valueAnimator) {
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
                TLRPC$User user = getMessagesController().getUser(Integer.valueOf(this.chatInfo.participants.participants.get(i).user_id));
                if (!(user == null || (tLRPC$UserStatus = user.status) == null || ((tLRPC$UserStatus.expires <= currentTime && user.id != getUserConfig().getClientUserId()) || user.status.expires <= 10000))) {
                    this.onlineCount++;
                }
                this.sortedUsers.add(Integer.valueOf(i));
            }
            try {
                Collections.sort(this.sortedUsers, new Object(currentTime) {
                    public final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final int compare(Object obj, Object obj2) {
                        return ProfileActivity.this.lambda$updateOnlineCount$27$ProfileActivity(this.f$1, (Integer) obj, (Integer) obj2);
                    }

                    public /* synthetic */ Comparator reversed() {
                        return Comparator.CC.$default$reversed(this);
                    }

                    public /* synthetic */ java.util.Comparator thenComparing(Function function) {
                        return Comparator.CC.$default$thenComparing((java.util.Comparator) this, function);
                    }

                    public /* synthetic */ java.util.Comparator thenComparing(Function function, java.util.Comparator comparator) {
                        return Comparator.CC.$default$thenComparing(this, function, comparator);
                    }

                    public /* synthetic */ java.util.Comparator thenComparing(java.util.Comparator comparator) {
                        return Comparator.CC.$default$thenComparing((java.util.Comparator) this, comparator);
                    }

                    public /* synthetic */ java.util.Comparator thenComparingDouble(ToDoubleFunction toDoubleFunction) {
                        return Comparator.CC.$default$thenComparingDouble(this, toDoubleFunction);
                    }

                    public /* synthetic */ java.util.Comparator thenComparingInt(ToIntFunction toIntFunction) {
                        return Comparator.CC.$default$thenComparingInt(this, toIntFunction);
                    }

                    public /* synthetic */ java.util.Comparator thenComparingLong(ToLongFunction toLongFunction) {
                        return Comparator.CC.$default$thenComparingLong(this, toLongFunction);
                    }
                });
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
    /* renamed from: lambda$updateOnlineCount$27 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ int lambda$updateOnlineCount$27$ProfileActivity(int r5, java.lang.Integer r6, java.lang.Integer r7) {
        /*
            r4 = this;
            org.telegram.messenger.MessagesController r0 = r4.getMessagesController()
            org.telegram.tgnet.TLRPC$ChatFull r1 = r4.chatInfo
            org.telegram.tgnet.TLRPC$ChatParticipants r1 = r1.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r1 = r1.participants
            int r7 = r7.intValue()
            java.lang.Object r7 = r1.get(r7)
            org.telegram.tgnet.TLRPC$ChatParticipant r7 = (org.telegram.tgnet.TLRPC$ChatParticipant) r7
            int r7 = r7.user_id
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r7 = r0.getUser(r7)
            org.telegram.messenger.MessagesController r0 = r4.getMessagesController()
            org.telegram.tgnet.TLRPC$ChatFull r1 = r4.chatInfo
            org.telegram.tgnet.TLRPC$ChatParticipants r1 = r1.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r1 = r1.participants
            int r6 = r6.intValue()
            java.lang.Object r6 = r1.get(r6)
            org.telegram.tgnet.TLRPC$ChatParticipant r6 = (org.telegram.tgnet.TLRPC$ChatParticipant) r6
            int r6 = r6.user_id
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.lambda$updateOnlineCount$27$ProfileActivity(int, java.lang.Integer, java.lang.Integer):int");
    }

    public void setChatInfo(TLRPC$ChatFull tLRPC$ChatFull) {
        int i;
        this.chatInfo = tLRPC$ChatFull;
        if (!(tLRPC$ChatFull == null || (i = tLRPC$ChatFull.migrated_from_chat_id) == 0 || this.mergeDialogId != 0)) {
            this.mergeDialogId = (long) (-i);
            getMediaDataController().getMediaCounts(this.mergeDialogId, this.classGuid);
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

    private void kickUser(int i, TLRPC$ChatParticipant tLRPC$ChatParticipant) {
        if (i != 0) {
            TLRPC$User user = getMessagesController().getUser(Integer.valueOf(i));
            getMessagesController().deleteParticipantFromChat(this.chat_id, user, this.chatInfo);
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
        int i2 = NotificationCenter.closeChats;
        notificationCenter.removeObserver(this, i2);
        if (AndroidUtilities.isTablet()) {
            getNotificationCenter().postNotificationName(i2, Long.valueOf(-((long) this.chat_id)));
        } else {
            getNotificationCenter().postNotificationName(i2, new Object[0]);
        }
        getMessagesController().deleteParticipantFromChat(this.chat_id, getMessagesController().getUser(Integer.valueOf(getUserConfig().getClientUserId())), this.chatInfo);
        this.playProfileAnimation = 0;
        finishFragment();
    }

    public boolean isChat() {
        return this.chat_id != 0;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x00c9, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated) == false) goto L_0x00cb;
     */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x02ad  */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x0099  */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x04fb  */
    /* JADX WARNING: Removed duplicated region for block: B:243:0x0507  */
    /* JADX WARNING: Removed duplicated region for block: B:248:0x0519  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x0536  */
    /* JADX WARNING: Removed duplicated region for block: B:262:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateRowsIds() {
        /*
            r9 = this;
            int r0 = r9.rowCount
            r1 = 0
            r9.rowCount = r1
            r2 = -1
            r9.setAvatarRow = r2
            r9.setAvatarSectionRow = r2
            r9.numberSectionRow = r2
            r9.numberRow = r2
            r9.setUsernameRow = r2
            r9.bioRow = r2
            r9.settingsSectionRow = r2
            r9.settingsSectionRow2 = r2
            r9.notificationRow = r2
            r9.languageRow = r2
            r9.privacyRow = r2
            r9.dataRow = r2
            r9.chatRow = r2
            r9.filtersRow = r2
            r9.devicesRow = r2
            r9.devicesSectionRow = r2
            r9.helpHeaderRow = r2
            r9.questionRow = r2
            r9.faqRow = r2
            r9.policyRow = r2
            r9.helpSectionCell = r2
            r9.debugHeaderRow = r2
            r9.sendLogsRow = r2
            r9.sendLastLogsRow = r2
            r9.clearLogsRow = r2
            r9.switchBackendRow = r2
            r9.versionRow = r2
            r9.sendMessageRow = r2
            r9.reportRow = r2
            r9.emptyRow = r2
            r9.infoHeaderRow = r2
            r9.phoneRow = r2
            r9.userInfoRow = r2
            r9.locationRow = r2
            r9.channelInfoRow = r2
            r9.usernameRow = r2
            r9.settingsTimerRow = r2
            r9.settingsKeyRow = r2
            r9.notificationsDividerRow = r2
            r9.notificationsRow = r2
            r9.infoSectionRow = r2
            r9.secretSettingsSectionRow = r2
            r9.bottomPaddingRow = r2
            r9.membersHeaderRow = r2
            r9.membersStartRow = r2
            r9.membersEndRow = r2
            r9.addMemberRow = r2
            r9.subscribersRow = r2
            r9.administratorsRow = r2
            r9.blockedUsersRow = r2
            r9.membersSectionRow = r2
            r9.sharedMediaRow = r2
            r9.unblockRow = r2
            r9.joinRow = r2
            r9.lastSectionRow = r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r3 = r9.visibleChatParticipants
            r3.clear()
            java.util.ArrayList<java.lang.Integer> r3 = r9.visibleSortedUsers
            r3.clear()
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloader r3 = r9.sharedMediaPreloader
            r4 = 1
            if (r3 == 0) goto L_0x0094
            int[] r3 = r3.getLastMediaCount()
            r5 = 0
        L_0x0088:
            int r6 = r3.length
            if (r5 >= r6) goto L_0x0094
            r6 = r3[r5]
            if (r6 <= 0) goto L_0x0091
            r3 = 1
            goto L_0x0095
        L_0x0091:
            int r5 = r5 + 1
            goto L_0x0088
        L_0x0094:
            r3 = 0
        L_0x0095:
            int r5 = r9.user_id
            if (r5 == 0) goto L_0x02ad
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 == 0) goto L_0x00a5
            int r5 = r9.rowCount
            int r6 = r5 + 1
            r9.rowCount = r6
            r9.emptyRow = r5
        L_0x00a5:
            org.telegram.messenger.MessagesController r5 = r9.getMessagesController()
            int r6 = r9.user_id
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$User r5 = r5.getUser(r6)
            boolean r6 = org.telegram.messenger.UserObject.isUserSelf(r5)
            if (r6 == 0) goto L_0x01ab
            org.telegram.tgnet.TLRPC$FileLocation r3 = r9.avatarBig
            if (r3 != 0) goto L_0x00e3
            org.telegram.tgnet.TLRPC$UserProfilePhoto r3 = r5.photo
            if (r3 == 0) goto L_0x00cb
            org.telegram.tgnet.TLRPC$FileLocation r3 = r3.photo_big
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_fileLocation_layer97
            if (r4 != 0) goto L_0x00e3
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated
            if (r3 != 0) goto L_0x00e3
        L_0x00cb:
            org.telegram.ui.Components.ProfileGalleryView r3 = r9.avatarsViewPager
            if (r3 == 0) goto L_0x00d5
            int r3 = r3.getRealCount()
            if (r3 != 0) goto L_0x00e3
        L_0x00d5:
            int r3 = r9.rowCount
            int r4 = r3 + 1
            r9.rowCount = r4
            r9.setAvatarRow = r3
            int r3 = r4 + 1
            r9.rowCount = r3
            r9.setAvatarSectionRow = r4
        L_0x00e3:
            int r3 = r9.rowCount
            int r4 = r3 + 1
            r9.rowCount = r4
            r9.numberSectionRow = r3
            int r3 = r4 + 1
            r9.rowCount = r3
            r9.numberRow = r4
            int r4 = r3 + 1
            r9.rowCount = r4
            r9.setUsernameRow = r3
            int r3 = r4 + 1
            r9.rowCount = r3
            r9.bioRow = r4
            int r4 = r3 + 1
            r9.rowCount = r4
            r9.settingsSectionRow = r3
            int r3 = r4 + 1
            r9.rowCount = r3
            r9.settingsSectionRow2 = r4
            int r4 = r3 + 1
            r9.rowCount = r4
            r9.notificationRow = r3
            int r3 = r4 + 1
            r9.rowCount = r3
            r9.privacyRow = r4
            int r4 = r3 + 1
            r9.rowCount = r4
            r9.dataRow = r3
            int r3 = r4 + 1
            r9.rowCount = r3
            r9.chatRow = r4
            org.telegram.messenger.MessagesController r3 = r9.getMessagesController()
            boolean r3 = r3.filtersEnabled
            if (r3 != 0) goto L_0x0135
            org.telegram.messenger.MessagesController r3 = r9.getMessagesController()
            java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r3 = r3.dialogFilters
            boolean r3 = r3.isEmpty()
            if (r3 != 0) goto L_0x013d
        L_0x0135:
            int r3 = r9.rowCount
            int r4 = r3 + 1
            r9.rowCount = r4
            r9.filtersRow = r3
        L_0x013d:
            int r3 = r9.rowCount
            int r4 = r3 + 1
            r9.rowCount = r4
            r9.devicesRow = r3
            int r3 = r4 + 1
            r9.rowCount = r3
            r9.languageRow = r4
            int r4 = r3 + 1
            r9.rowCount = r4
            r9.devicesSectionRow = r3
            int r3 = r4 + 1
            r9.rowCount = r3
            r9.helpHeaderRow = r4
            int r4 = r3 + 1
            r9.rowCount = r4
            r9.questionRow = r3
            int r3 = r4 + 1
            r9.rowCount = r3
            r9.faqRow = r4
            int r4 = r3 + 1
            r9.rowCount = r4
            r9.policyRow = r3
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r3 != 0) goto L_0x0171
            boolean r3 = org.telegram.messenger.BuildVars.DEBUG_PRIVATE_VERSION
            if (r3 == 0) goto L_0x017d
        L_0x0171:
            int r3 = r4 + 1
            r9.rowCount = r3
            r9.helpSectionCell = r4
            int r4 = r3 + 1
            r9.rowCount = r4
            r9.debugHeaderRow = r3
        L_0x017d:
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r3 == 0) goto L_0x0195
            int r3 = r9.rowCount
            int r4 = r3 + 1
            r9.rowCount = r4
            r9.sendLogsRow = r3
            int r3 = r4 + 1
            r9.rowCount = r3
            r9.sendLastLogsRow = r4
            int r4 = r3 + 1
            r9.rowCount = r4
            r9.clearLogsRow = r3
        L_0x0195:
            boolean r3 = org.telegram.messenger.BuildVars.DEBUG_PRIVATE_VERSION
            if (r3 == 0) goto L_0x01a1
            int r3 = r9.rowCount
            int r4 = r3 + 1
            r9.rowCount = r4
            r9.switchBackendRow = r3
        L_0x01a1:
            int r3 = r9.rowCount
            int r4 = r3 + 1
            r9.rowCount = r4
            r9.versionRow = r3
            goto L_0x04f7
        L_0x01ab:
            org.telegram.tgnet.TLRPC$UserFull r6 = r9.userInfo
            if (r6 == 0) goto L_0x01b7
            java.lang.String r6 = r6.about
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 == 0) goto L_0x01c1
        L_0x01b7:
            if (r5 == 0) goto L_0x01c3
            java.lang.String r6 = r5.username
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 != 0) goto L_0x01c3
        L_0x01c1:
            r6 = 1
            goto L_0x01c4
        L_0x01c3:
            r6 = 0
        L_0x01c4:
            if (r5 == 0) goto L_0x01cf
            java.lang.String r7 = r5.phone
            boolean r7 = android.text.TextUtils.isEmpty(r7)
            if (r7 != 0) goto L_0x01cf
            goto L_0x01d0
        L_0x01cf:
            r4 = 0
        L_0x01d0:
            int r7 = r9.rowCount
            int r8 = r7 + 1
            r9.rowCount = r8
            r9.infoHeaderRow = r7
            boolean r7 = r9.isBot
            if (r7 != 0) goto L_0x01e6
            if (r4 != 0) goto L_0x01e0
            if (r6 != 0) goto L_0x01e6
        L_0x01e0:
            int r4 = r8 + 1
            r9.rowCount = r4
            r9.phoneRow = r8
        L_0x01e6:
            org.telegram.tgnet.TLRPC$UserFull r4 = r9.userInfo
            if (r4 == 0) goto L_0x01fa
            java.lang.String r4 = r4.about
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x01fa
            int r4 = r9.rowCount
            int r6 = r4 + 1
            r9.rowCount = r6
            r9.userInfoRow = r4
        L_0x01fa:
            if (r5 == 0) goto L_0x020c
            java.lang.String r4 = r5.username
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x020c
            int r4 = r9.rowCount
            int r6 = r4 + 1
            r9.rowCount = r6
            r9.usernameRow = r4
        L_0x020c:
            int r4 = r9.phoneRow
            if (r4 != r2) goto L_0x0218
            int r4 = r9.userInfoRow
            if (r4 != r2) goto L_0x0218
            int r4 = r9.usernameRow
            if (r4 == r2) goto L_0x0220
        L_0x0218:
            int r4 = r9.rowCount
            int r6 = r4 + 1
            r9.rowCount = r6
            r9.notificationsDividerRow = r4
        L_0x0220:
            int r4 = r9.user_id
            org.telegram.messenger.UserConfig r6 = r9.getUserConfig()
            int r6 = r6.getClientUserId()
            if (r4 == r6) goto L_0x0234
            int r4 = r9.rowCount
            int r6 = r4 + 1
            r9.rowCount = r6
            r9.notificationsRow = r4
        L_0x0234:
            int r4 = r9.rowCount
            int r6 = r4 + 1
            r9.rowCount = r6
            r9.infoSectionRow = r4
            org.telegram.tgnet.TLRPC$EncryptedChat r4 = r9.currentEncryptedChat
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChat
            if (r7 == 0) goto L_0x0254
            int r7 = r6 + 1
            r9.rowCount = r7
            r9.settingsTimerRow = r6
            int r6 = r7 + 1
            r9.rowCount = r6
            r9.settingsKeyRow = r7
            int r7 = r6 + 1
            r9.rowCount = r7
            r9.secretSettingsSectionRow = r6
        L_0x0254:
            if (r5 == 0) goto L_0x027a
            boolean r6 = r9.isBot
            if (r6 != 0) goto L_0x027a
            if (r4 != 0) goto L_0x027a
            int r4 = r5.id
            org.telegram.messenger.UserConfig r5 = r9.getUserConfig()
            int r5 = r5.getClientUserId()
            if (r4 == r5) goto L_0x027a
            boolean r4 = r9.userBlocked
            if (r4 == 0) goto L_0x027a
            int r4 = r9.rowCount
            int r5 = r4 + 1
            r9.rowCount = r5
            r9.unblockRow = r4
            int r4 = r5 + 1
            r9.rowCount = r4
            r9.lastSectionRow = r5
        L_0x027a:
            if (r3 != 0) goto L_0x02a3
            org.telegram.tgnet.TLRPC$UserFull r3 = r9.userInfo
            if (r3 == 0) goto L_0x0285
            int r3 = r3.common_chats_count
            if (r3 == 0) goto L_0x0285
            goto L_0x02a3
        L_0x0285:
            int r3 = r9.lastSectionRow
            if (r3 != r2) goto L_0x04f7
            boolean r3 = r9.needSendMessage
            if (r3 == 0) goto L_0x04f7
            int r3 = r9.rowCount
            int r4 = r3 + 1
            r9.rowCount = r4
            r9.sendMessageRow = r3
            int r3 = r4 + 1
            r9.rowCount = r3
            r9.reportRow = r4
            int r4 = r3 + 1
            r9.rowCount = r4
            r9.lastSectionRow = r3
            goto L_0x04f7
        L_0x02a3:
            int r3 = r9.rowCount
            int r4 = r3 + 1
            r9.rowCount = r4
            r9.sharedMediaRow = r3
            goto L_0x04f7
        L_0x02ad:
            int r5 = r9.chat_id
            if (r5 == 0) goto L_0x04f7
            org.telegram.tgnet.TLRPC$ChatFull r5 = r9.chatInfo
            if (r5 == 0) goto L_0x02c5
            java.lang.String r5 = r5.about
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 == 0) goto L_0x02cf
            org.telegram.tgnet.TLRPC$ChatFull r5 = r9.chatInfo
            org.telegram.tgnet.TLRPC$ChannelLocation r5 = r5.location
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC$TL_channelLocation
            if (r5 != 0) goto L_0x02cf
        L_0x02c5:
            org.telegram.tgnet.TLRPC$Chat r5 = r9.currentChat
            java.lang.String r5 = r5.username
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 != 0) goto L_0x032f
        L_0x02cf:
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 == 0) goto L_0x02f1
            org.telegram.tgnet.TLRPC$Chat r5 = r9.currentChat
            boolean r5 = org.telegram.messenger.ChatObject.isChannel(r5)
            if (r5 == 0) goto L_0x02f1
            org.telegram.tgnet.TLRPC$ChatFull r5 = r9.chatInfo
            if (r5 == 0) goto L_0x02f1
            org.telegram.tgnet.TLRPC$Chat r6 = r9.currentChat
            boolean r6 = r6.megagroup
            if (r6 != 0) goto L_0x02f1
            int r5 = r5.linked_chat_id
            if (r5 == 0) goto L_0x02f1
            int r5 = r9.rowCount
            int r6 = r5 + 1
            r9.rowCount = r6
            r9.emptyRow = r5
        L_0x02f1:
            int r5 = r9.rowCount
            int r6 = r5 + 1
            r9.rowCount = r6
            r9.infoHeaderRow = r5
            org.telegram.tgnet.TLRPC$ChatFull r5 = r9.chatInfo
            if (r5 == 0) goto L_0x031d
            java.lang.String r5 = r5.about
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 != 0) goto L_0x030d
            int r5 = r9.rowCount
            int r6 = r5 + 1
            r9.rowCount = r6
            r9.channelInfoRow = r5
        L_0x030d:
            org.telegram.tgnet.TLRPC$ChatFull r5 = r9.chatInfo
            org.telegram.tgnet.TLRPC$ChannelLocation r5 = r5.location
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC$TL_channelLocation
            if (r5 == 0) goto L_0x031d
            int r5 = r9.rowCount
            int r6 = r5 + 1
            r9.rowCount = r6
            r9.locationRow = r5
        L_0x031d:
            org.telegram.tgnet.TLRPC$Chat r5 = r9.currentChat
            java.lang.String r5 = r5.username
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 != 0) goto L_0x032f
            int r5 = r9.rowCount
            int r6 = r5 + 1
            r9.rowCount = r6
            r9.usernameRow = r5
        L_0x032f:
            int r5 = r9.infoHeaderRow
            if (r5 == r2) goto L_0x033b
            int r5 = r9.rowCount
            int r6 = r5 + 1
            r9.rowCount = r6
            r9.notificationsDividerRow = r5
        L_0x033b:
            int r5 = r9.rowCount
            int r6 = r5 + 1
            r9.rowCount = r6
            r9.notificationsRow = r5
            int r5 = r6 + 1
            r9.rowCount = r5
            r9.infoSectionRow = r6
            org.telegram.tgnet.TLRPC$Chat r5 = r9.currentChat
            boolean r5 = org.telegram.messenger.ChatObject.isChannel(r5)
            if (r5 == 0) goto L_0x038d
            org.telegram.tgnet.TLRPC$Chat r5 = r9.currentChat
            boolean r6 = r5.megagroup
            if (r6 != 0) goto L_0x038d
            org.telegram.tgnet.TLRPC$ChatFull r6 = r9.chatInfo
            if (r6 == 0) goto L_0x038d
            boolean r5 = r5.creator
            if (r5 != 0) goto L_0x0363
            boolean r5 = r6.can_view_participants
            if (r5 == 0) goto L_0x038d
        L_0x0363:
            int r5 = r9.rowCount
            int r7 = r5 + 1
            r9.rowCount = r7
            r9.membersHeaderRow = r5
            int r5 = r7 + 1
            r9.rowCount = r5
            r9.subscribersRow = r7
            int r7 = r5 + 1
            r9.rowCount = r7
            r9.administratorsRow = r5
            int r5 = r6.banned_count
            if (r5 != 0) goto L_0x037f
            int r5 = r6.kicked_count
            if (r5 == 0) goto L_0x0385
        L_0x037f:
            int r5 = r7 + 1
            r9.rowCount = r5
            r9.blockedUsersRow = r7
        L_0x0385:
            int r5 = r9.rowCount
            int r6 = r5 + 1
            r9.rowCount = r6
            r9.membersSectionRow = r5
        L_0x038d:
            org.telegram.tgnet.TLRPC$Chat r5 = r9.currentChat
            boolean r5 = org.telegram.messenger.ChatObject.isChannel(r5)
            r6 = 5
            r7 = 0
            if (r5 == 0) goto L_0x0462
            org.telegram.tgnet.TLRPC$ChatFull r5 = r9.chatInfo
            if (r5 == 0) goto L_0x0444
            org.telegram.tgnet.TLRPC$Chat r8 = r9.currentChat
            boolean r8 = r8.megagroup
            if (r8 == 0) goto L_0x0444
            org.telegram.tgnet.TLRPC$ChatParticipants r5 = r5.participants
            if (r5 == 0) goto L_0x0444
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r5 = r5.participants
            boolean r5 = r5.isEmpty()
            if (r5 != 0) goto L_0x0444
            org.telegram.tgnet.TLRPC$Chat r5 = r9.currentChat
            boolean r5 = org.telegram.messenger.ChatObject.isNotInChat(r5)
            if (r5 != 0) goto L_0x03d1
            org.telegram.tgnet.TLRPC$Chat r5 = r9.currentChat
            boolean r5 = org.telegram.messenger.ChatObject.canAddUsers(r5)
            if (r5 == 0) goto L_0x03d1
            org.telegram.tgnet.TLRPC$ChatFull r5 = r9.chatInfo
            int r5 = r5.participants_count
            org.telegram.messenger.MessagesController r8 = r9.getMessagesController()
            int r8 = r8.maxMegagroupCount
            if (r5 >= r8) goto L_0x03d1
            int r5 = r9.rowCount
            int r8 = r5 + 1
            r9.rowCount = r8
            r9.addMemberRow = r5
        L_0x03d1:
            org.telegram.tgnet.TLRPC$ChatFull r5 = r9.chatInfo
            org.telegram.tgnet.TLRPC$ChatParticipants r5 = r5.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r5 = r5.participants
            int r5 = r5.size()
            r8 = 2
            if (r5 <= r6) goto L_0x03e4
            if (r3 == 0) goto L_0x03e4
            int r6 = r9.usersForceShowingIn
            if (r6 != r4) goto L_0x0421
        L_0x03e4:
            int r6 = r9.usersForceShowingIn
            if (r6 == r8) goto L_0x0421
            int r6 = r9.addMemberRow
            if (r6 != r2) goto L_0x03f4
            int r6 = r9.rowCount
            int r8 = r6 + 1
            r9.rowCount = r8
            r9.membersHeaderRow = r6
        L_0x03f4:
            int r6 = r9.rowCount
            r9.membersStartRow = r6
            int r6 = r6 + r5
            r9.rowCount = r6
            r9.membersEndRow = r6
            int r5 = r6 + 1
            r9.rowCount = r5
            r9.membersSectionRow = r6
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r5 = r9.visibleChatParticipants
            org.telegram.tgnet.TLRPC$ChatFull r6 = r9.chatInfo
            org.telegram.tgnet.TLRPC$ChatParticipants r6 = r6.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r6 = r6.participants
            r5.addAll(r6)
            java.util.ArrayList<java.lang.Integer> r5 = r9.sortedUsers
            if (r5 == 0) goto L_0x0417
            java.util.ArrayList<java.lang.Integer> r6 = r9.visibleSortedUsers
            r6.addAll(r5)
        L_0x0417:
            r9.usersForceShowingIn = r4
            org.telegram.ui.Components.SharedMediaLayout r4 = r9.sharedMediaLayout
            if (r4 == 0) goto L_0x0444
            r4.setChatUsers(r7, r7)
            goto L_0x0444
        L_0x0421:
            int r4 = r9.addMemberRow
            if (r4 == r2) goto L_0x042d
            int r4 = r9.rowCount
            int r5 = r4 + 1
            r9.rowCount = r5
            r9.membersSectionRow = r4
        L_0x042d:
            org.telegram.ui.Components.SharedMediaLayout r4 = r9.sharedMediaLayout
            if (r4 == 0) goto L_0x0444
            java.util.ArrayList<java.lang.Integer> r4 = r9.sortedUsers
            boolean r4 = r4.isEmpty()
            if (r4 != 0) goto L_0x043b
            r9.usersForceShowingIn = r8
        L_0x043b:
            org.telegram.ui.Components.SharedMediaLayout r4 = r9.sharedMediaLayout
            java.util.ArrayList<java.lang.Integer> r5 = r9.sortedUsers
            org.telegram.tgnet.TLRPC$ChatFull r6 = r9.chatInfo
            r4.setChatUsers(r5, r6)
        L_0x0444:
            int r4 = r9.lastSectionRow
            if (r4 != r2) goto L_0x04ed
            org.telegram.tgnet.TLRPC$Chat r4 = r9.currentChat
            boolean r5 = r4.left
            if (r5 == 0) goto L_0x04ed
            boolean r4 = r4.kicked
            if (r4 != 0) goto L_0x04ed
            int r4 = r9.rowCount
            int r5 = r4 + 1
            r9.rowCount = r5
            r9.joinRow = r4
            int r4 = r5 + 1
            r9.rowCount = r4
            r9.lastSectionRow = r5
            goto L_0x04ed
        L_0x0462:
            org.telegram.tgnet.TLRPC$ChatFull r4 = r9.chatInfo
            if (r4 == 0) goto L_0x04ed
            org.telegram.tgnet.TLRPC$ChatParticipants r4 = r4.participants
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantsForbidden
            if (r4 != 0) goto L_0x04ed
            org.telegram.tgnet.TLRPC$Chat r4 = r9.currentChat
            boolean r4 = org.telegram.messenger.ChatObject.canAddUsers(r4)
            if (r4 != 0) goto L_0x047e
            org.telegram.tgnet.TLRPC$Chat r4 = r9.currentChat
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r4.default_banned_rights
            if (r4 == 0) goto L_0x047e
            boolean r4 = r4.invite_users
            if (r4 != 0) goto L_0x0486
        L_0x047e:
            int r4 = r9.rowCount
            int r5 = r4 + 1
            r9.rowCount = r5
            r9.addMemberRow = r4
        L_0x0486:
            org.telegram.tgnet.TLRPC$ChatFull r4 = r9.chatInfo
            org.telegram.tgnet.TLRPC$ChatParticipants r4 = r4.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r4 = r4.participants
            int r4 = r4.size()
            if (r4 <= r6) goto L_0x04ad
            if (r3 != 0) goto L_0x0495
            goto L_0x04ad
        L_0x0495:
            int r4 = r9.addMemberRow
            if (r4 == r2) goto L_0x04a1
            int r4 = r9.rowCount
            int r5 = r4 + 1
            r9.rowCount = r5
            r9.membersSectionRow = r4
        L_0x04a1:
            org.telegram.ui.Components.SharedMediaLayout r4 = r9.sharedMediaLayout
            if (r4 == 0) goto L_0x04ed
            java.util.ArrayList<java.lang.Integer> r5 = r9.sortedUsers
            org.telegram.tgnet.TLRPC$ChatFull r6 = r9.chatInfo
            r4.setChatUsers(r5, r6)
            goto L_0x04ed
        L_0x04ad:
            int r4 = r9.addMemberRow
            if (r4 != r2) goto L_0x04b9
            int r4 = r9.rowCount
            int r5 = r4 + 1
            r9.rowCount = r5
            r9.membersHeaderRow = r4
        L_0x04b9:
            int r4 = r9.rowCount
            r9.membersStartRow = r4
            org.telegram.tgnet.TLRPC$ChatFull r5 = r9.chatInfo
            org.telegram.tgnet.TLRPC$ChatParticipants r5 = r5.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r5 = r5.participants
            int r5 = r5.size()
            int r4 = r4 + r5
            r9.rowCount = r4
            r9.membersEndRow = r4
            int r5 = r4 + 1
            r9.rowCount = r5
            r9.membersSectionRow = r4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r4 = r9.visibleChatParticipants
            org.telegram.tgnet.TLRPC$ChatFull r5 = r9.chatInfo
            org.telegram.tgnet.TLRPC$ChatParticipants r5 = r5.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r5 = r5.participants
            r4.addAll(r5)
            java.util.ArrayList<java.lang.Integer> r4 = r9.sortedUsers
            if (r4 == 0) goto L_0x04e6
            java.util.ArrayList<java.lang.Integer> r5 = r9.visibleSortedUsers
            r5.addAll(r4)
        L_0x04e6:
            org.telegram.ui.Components.SharedMediaLayout r4 = r9.sharedMediaLayout
            if (r4 == 0) goto L_0x04ed
            r4.setChatUsers(r7, r7)
        L_0x04ed:
            if (r3 == 0) goto L_0x04f7
            int r3 = r9.rowCount
            int r4 = r3 + 1
            r9.rowCount = r4
            r9.sharedMediaRow = r3
        L_0x04f7:
            int r3 = r9.sharedMediaRow
            if (r3 != r2) goto L_0x0503
            int r2 = r9.rowCount
            int r3 = r2 + 1
            r9.rowCount = r3
            r9.bottomPaddingRow = r2
        L_0x0503:
            org.telegram.ui.ActionBar.ActionBar r2 = r9.actionBar
            if (r2 == 0) goto L_0x0519
            int r2 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight()
            org.telegram.ui.ActionBar.ActionBar r3 = r9.actionBar
            boolean r3 = r3.getOccupyStatusBar()
            if (r3 == 0) goto L_0x0516
            int r3 = org.telegram.messenger.AndroidUtilities.statusBarHeight
            goto L_0x0517
        L_0x0516:
            r3 = 0
        L_0x0517:
            int r2 = r2 + r3
            goto L_0x051a
        L_0x0519:
            r2 = 0
        L_0x051a:
            org.telegram.ui.Components.RecyclerListView r3 = r9.listView
            if (r3 == 0) goto L_0x0536
            int r3 = r9.rowCount
            if (r0 > r3) goto L_0x0536
            int r0 = r9.listContentHeight
            if (r0 == 0) goto L_0x0538
            int r0 = r0 + r2
            r2 = 1118830592(0x42b00000, float:88.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 + r2
            org.telegram.ui.Components.RecyclerListView r2 = r9.listView
            int r2 = r2.getMeasuredHeight()
            if (r0 >= r2) goto L_0x0538
        L_0x0536:
            r9.lastMeasuredContentWidth = r1
        L_0x0538:
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
    /* JADX WARNING: Removed duplicated region for block: B:105:0x01f2  */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x01f7  */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x01fa  */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x0223  */
    /* JADX WARNING: Removed duplicated region for block: B:196:0x0400  */
    /* JADX WARNING: Removed duplicated region for block: B:274:0x05bb  */
    /* JADX WARNING: Removed duplicated region for block: B:277:0x05c2  */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x05c5  */
    /* JADX WARNING: Removed duplicated region for block: B:290:0x060a  */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x060d  */
    /* JADX WARNING: Removed duplicated region for block: B:294:0x0612  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateProfileData() {
        /*
            r24 = this;
            r0 = r24
            android.widget.FrameLayout r1 = r0.avatarContainer
            if (r1 == 0) goto L_0x0648
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r0.nameTextView
            if (r1 != 0) goto L_0x000c
            goto L_0x0648
        L_0x000c:
            org.telegram.tgnet.ConnectionsManager r1 = r24.getConnectionsManager()
            int r1 = r1.getConnectionState()
            r2 = 2
            r4 = 1
            if (r1 != r2) goto L_0x0022
            r1 = 2131628269(0x7f0e10ed, float:1.8883826E38)
            java.lang.String r5 = "WaitingForNetwork"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r1)
            goto L_0x0049
        L_0x0022:
            if (r1 != r4) goto L_0x002e
            r1 = 2131624982(0x7f0e0416, float:1.887716E38)
            java.lang.String r5 = "Connecting"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r1)
            goto L_0x0049
        L_0x002e:
            r5 = 5
            if (r1 != r5) goto L_0x003b
            r1 = 2131627889(0x7f0e0var_, float:1.8883055E38)
            java.lang.String r5 = "Updating"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r1)
            goto L_0x0049
        L_0x003b:
            r5 = 4
            if (r1 != r5) goto L_0x0048
            r1 = 2131624984(0x7f0e0418, float:1.8877163E38)
            java.lang.String r5 = "ConnectingToProxy"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r1)
            goto L_0x0049
        L_0x0048:
            r1 = 0
        L_0x0049:
            int r5 = r0.user_id
            java.lang.String r6 = "g"
            r7 = 0
            if (r5 == 0) goto L_0x025f
            org.telegram.messenger.MessagesController r5 = r24.getMessagesController()
            int r8 = r0.user_id
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            org.telegram.tgnet.TLRPC$User r5 = r5.getUser(r8)
            if (r5 != 0) goto L_0x0061
            return
        L_0x0061:
            org.telegram.tgnet.TLRPC$UserProfilePhoto r8 = r5.photo
            if (r8 == 0) goto L_0x0068
            org.telegram.tgnet.TLRPC$FileLocation r8 = r8.photo_big
            goto L_0x0069
        L_0x0068:
            r8 = 0
        L_0x0069:
            org.telegram.ui.Components.AvatarDrawable r9 = r0.avatarDrawable
            r9.setInfo((org.telegram.tgnet.TLRPC$User) r5)
            org.telegram.messenger.ImageLocation r15 = org.telegram.messenger.ImageLocation.getForUserOrChat(r5, r7)
            org.telegram.messenger.ImageLocation r14 = org.telegram.messenger.ImageLocation.getForUserOrChat(r5, r4)
            org.telegram.ui.Components.ProfileGalleryView r9 = r0.avatarsViewPager
            org.telegram.messenger.ImageLocation r10 = r9.getCurrentVideoLocation(r14, r15)
            org.telegram.ui.Components.ProfileGalleryView r9 = r0.avatarsViewPager
            r9.initIfEmpty(r15, r14)
            if (r10 == 0) goto L_0x0089
            int r9 = r10.imageType
            if (r9 != r2) goto L_0x0089
            r11 = r6
            goto L_0x008a
        L_0x0089:
            r11 = 0
        L_0x008a:
            org.telegram.tgnet.TLRPC$FileLocation r6 = r0.avatarBig
            if (r6 != 0) goto L_0x009e
            org.telegram.ui.ProfileActivity$AvatarImageView r9 = r0.avatarImage
            org.telegram.ui.Components.AvatarDrawable r6 = r0.avatarDrawable
            java.lang.String r13 = "50_50"
            r12 = r14
            r16 = r14
            r14 = r6
            r6 = r15
            r15 = r5
            r9.setImage((org.telegram.messenger.ImageLocation) r10, (java.lang.String) r11, (org.telegram.messenger.ImageLocation) r12, (java.lang.String) r13, (android.graphics.drawable.Drawable) r14, (java.lang.Object) r15)
            goto L_0x00a1
        L_0x009e:
            r16 = r14
            r6 = r15
        L_0x00a1:
            r9 = -1
            if (r16 == 0) goto L_0x00a8
            int r10 = r0.setAvatarRow
            if (r10 != r9) goto L_0x00ae
        L_0x00a8:
            if (r16 != 0) goto L_0x00b4
            int r10 = r0.setAvatarRow
            if (r10 != r9) goto L_0x00b4
        L_0x00ae:
            r0.updateListAnimated(r7)
            r0.needLayout(r4)
        L_0x00b4:
            if (r6 == 0) goto L_0x00d0
            org.telegram.messenger.ImageLocation r9 = r0.prevLoadedImageLocation
            if (r9 == 0) goto L_0x00c2
            long r10 = r6.photoId
            long r12 = r9.photoId
            int r9 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r9 == 0) goto L_0x00d0
        L_0x00c2:
            r0.prevLoadedImageLocation = r6
            org.telegram.messenger.FileLoader r9 = r24.getFileLoader()
            r12 = 0
            r13 = 0
            r14 = 1
            r10 = r6
            r11 = r5
            r9.loadFile(r10, r11, r12, r13, r14)
        L_0x00d0:
            java.lang.String r6 = org.telegram.messenger.UserObject.getUserName(r5)
            int r9 = r5.id
            org.telegram.messenger.UserConfig r10 = r24.getUserConfig()
            int r10 = r10.getClientUserId()
            if (r9 != r10) goto L_0x00eb
            r9 = 2131626554(0x7f0e0a3a, float:1.8880347E38)
            java.lang.String r10 = "Online"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            goto L_0x0158
        L_0x00eb:
            int r9 = r5.id
            r10 = 333000(0x514c8, float:4.66632E-40)
            if (r9 == r10) goto L_0x014f
            r10 = 777000(0xbdb28, float:1.088809E-39)
            if (r9 == r10) goto L_0x014f
            r10 = 42777(0xa719, float:5.9943E-41)
            if (r9 != r10) goto L_0x00fd
            goto L_0x014f
        L_0x00fd:
            boolean r9 = org.telegram.messenger.MessagesController.isSupportUser(r5)
            if (r9 == 0) goto L_0x010d
            r9 = 2131627663(0x7f0e0e8f, float:1.8882597E38)
            java.lang.String r10 = "SupportStatus"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            goto L_0x0158
        L_0x010d:
            boolean r9 = r0.isBot
            if (r9 == 0) goto L_0x011b
            r9 = 2131624583(0x7f0e0287, float:1.887635E38)
            java.lang.String r10 = "Bot"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            goto L_0x0158
        L_0x011b:
            boolean[] r9 = r0.isOnline
            r9[r7] = r7
            int r10 = r0.currentAccount
            java.lang.String r9 = org.telegram.messenger.LocaleController.formatUserStatus(r10, r5, r9)
            org.telegram.ui.ActionBar.SimpleTextView[] r10 = r0.onlineTextView
            r11 = r10[r4]
            if (r11 == 0) goto L_0x0158
            boolean r11 = r0.mediaHeaderVisible
            if (r11 != 0) goto L_0x0158
            boolean[] r11 = r0.isOnline
            boolean r11 = r11[r7]
            if (r11 == 0) goto L_0x0138
            java.lang.String r11 = "profile_status"
            goto L_0x013a
        L_0x0138:
            java.lang.String r11 = "avatar_subtitleInProfileBlue"
        L_0x013a:
            r10 = r10[r4]
            r10.setTag(r11)
            boolean r10 = r0.isPulledDown
            if (r10 != 0) goto L_0x0158
            org.telegram.ui.ActionBar.SimpleTextView[] r10 = r0.onlineTextView
            r10 = r10[r4]
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r10.setTextColor(r11)
            goto L_0x0158
        L_0x014f:
            r9 = 2131627437(0x7f0e0dad, float:1.8882138E38)
            java.lang.String r10 = "ServiceNotifications"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
        L_0x0158:
            r10 = 0
        L_0x0159:
            if (r10 >= r2) goto L_0x024f
            org.telegram.ui.ActionBar.SimpleTextView[] r11 = r0.nameTextView
            r11 = r11[r10]
            if (r11 != 0) goto L_0x0163
            goto L_0x024b
        L_0x0163:
            if (r10 != 0) goto L_0x01d4
            int r11 = r5.id
            org.telegram.messenger.UserConfig r12 = r24.getUserConfig()
            int r12 = r12.getClientUserId()
            if (r11 == r12) goto L_0x01d4
            int r11 = r5.id
            int r12 = r11 / 1000
            r13 = 777(0x309, float:1.089E-42)
            if (r12 == r13) goto L_0x01d4
            int r11 = r11 / 1000
            r12 = 333(0x14d, float:4.67E-43)
            if (r11 == r12) goto L_0x01d4
            java.lang.String r11 = r5.phone
            if (r11 == 0) goto L_0x01d4
            int r11 = r11.length()
            if (r11 == 0) goto L_0x01d4
            org.telegram.messenger.ContactsController r11 = r24.getContactsController()
            j$.util.concurrent.ConcurrentHashMap<java.lang.Integer, org.telegram.tgnet.TLRPC$TL_contact> r11 = r11.contactsDict
            int r12 = r5.id
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.Object r11 = r11.get(r12)
            if (r11 != 0) goto L_0x01d4
            org.telegram.messenger.ContactsController r11 = r24.getContactsController()
            j$.util.concurrent.ConcurrentHashMap<java.lang.Integer, org.telegram.tgnet.TLRPC$TL_contact> r11 = r11.contactsDict
            int r11 = r11.size()
            if (r11 != 0) goto L_0x01b1
            org.telegram.messenger.ContactsController r11 = r24.getContactsController()
            boolean r11 = r11.isLoadingContacts()
            if (r11 != 0) goto L_0x01d4
        L_0x01b1:
            org.telegram.PhoneFormat.PhoneFormat r11 = org.telegram.PhoneFormat.PhoneFormat.getInstance()
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r13 = "+"
            r12.append(r13)
            java.lang.String r13 = r5.phone
            r12.append(r13)
            java.lang.String r12 = r12.toString()
            java.lang.String r11 = r11.format(r12)
            org.telegram.ui.ActionBar.SimpleTextView[] r12 = r0.nameTextView
            r12 = r12[r10]
            r12.setText(r11)
            goto L_0x01db
        L_0x01d4:
            org.telegram.ui.ActionBar.SimpleTextView[] r11 = r0.nameTextView
            r11 = r11[r10]
            r11.setText(r6)
        L_0x01db:
            if (r10 != 0) goto L_0x01e7
            if (r1 == 0) goto L_0x01e7
            org.telegram.ui.ActionBar.SimpleTextView[] r11 = r0.onlineTextView
            r11 = r11[r10]
            r11.setText(r1)
            goto L_0x01ee
        L_0x01e7:
            org.telegram.ui.ActionBar.SimpleTextView[] r11 = r0.onlineTextView
            r11 = r11[r10]
            r11.setText(r9)
        L_0x01ee:
            org.telegram.tgnet.TLRPC$EncryptedChat r11 = r0.currentEncryptedChat
            if (r11 == 0) goto L_0x01f7
            android.graphics.drawable.Drawable r11 = r24.getLockIconDrawable()
            goto L_0x01f8
        L_0x01f7:
            r11 = 0
        L_0x01f8:
            if (r10 != 0) goto L_0x0223
            boolean r12 = r5.scam
            if (r12 != 0) goto L_0x021c
            boolean r13 = r5.fake
            if (r13 == 0) goto L_0x0203
            goto L_0x021c
        L_0x0203:
            org.telegram.messenger.MessagesController r12 = r24.getMessagesController()
            long r13 = r0.dialog_id
            r15 = 0
            int r17 = (r13 > r15 ? 1 : (r13 == r15 ? 0 : -1))
            if (r17 == 0) goto L_0x0210
            goto L_0x0213
        L_0x0210:
            int r13 = r0.user_id
            long r13 = (long) r13
        L_0x0213:
            boolean r12 = r12.isDialogMuted(r13)
            if (r12 == 0) goto L_0x0235
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.chat_muteIconDrawable
            goto L_0x023d
        L_0x021c:
            r12 = r12 ^ 1
            android.graphics.drawable.Drawable r12 = r0.getScamDrawable(r12)
            goto L_0x023d
        L_0x0223:
            boolean r12 = r5.scam
            if (r12 != 0) goto L_0x0237
            boolean r13 = r5.fake
            if (r13 == 0) goto L_0x022c
            goto L_0x0237
        L_0x022c:
            boolean r12 = r5.verified
            if (r12 == 0) goto L_0x0235
            android.graphics.drawable.Drawable r12 = r24.getVerifiedCrossfadeDrawable()
            goto L_0x023d
        L_0x0235:
            r12 = 0
            goto L_0x023d
        L_0x0237:
            r12 = r12 ^ 1
            android.graphics.drawable.Drawable r12 = r0.getScamDrawable(r12)
        L_0x023d:
            org.telegram.ui.ActionBar.SimpleTextView[] r13 = r0.nameTextView
            r13 = r13[r10]
            r13.setLeftDrawable((android.graphics.drawable.Drawable) r11)
            org.telegram.ui.ActionBar.SimpleTextView[] r11 = r0.nameTextView
            r11 = r11[r10]
            r11.setRightDrawable((android.graphics.drawable.Drawable) r12)
        L_0x024b:
            int r10 = r10 + 1
            goto L_0x0159
        L_0x024f:
            org.telegram.ui.ProfileActivity$AvatarImageView r1 = r0.avatarImage
            org.telegram.messenger.ImageReceiver r1 = r1.getImageReceiver()
            boolean r2 = org.telegram.ui.PhotoViewer.isShowingImage((org.telegram.tgnet.TLRPC$FileLocation) r8)
            r2 = r2 ^ r4
            r1.setVisible(r2, r7)
            goto L_0x0648
        L_0x025f:
            int r5 = r0.chat_id
            if (r5 == 0) goto L_0x0648
            org.telegram.messenger.MessagesController r5 = r24.getMessagesController()
            int r8 = r0.chat_id
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            org.telegram.tgnet.TLRPC$Chat r5 = r5.getChat(r8)
            if (r5 == 0) goto L_0x0276
            r0.currentChat = r5
            goto L_0x0278
        L_0x0276:
            org.telegram.tgnet.TLRPC$Chat r5 = r0.currentChat
        L_0x0278:
            boolean r8 = org.telegram.messenger.ChatObject.isChannel(r5)
            java.lang.String r10 = "MegaPublic"
            java.lang.String r12 = "MegaPrivate"
            java.lang.String r14 = "MegaLocation"
            java.lang.String r15 = "OnlineCount"
            java.lang.String r3 = "%s, %s"
            java.lang.String r11 = "Subscribers"
            java.lang.String r9 = "Members"
            if (r8 == 0) goto L_0x03aa
            org.telegram.tgnet.TLRPC$ChatFull r8 = r0.chatInfo
            if (r8 == 0) goto L_0x0374
            org.telegram.tgnet.TLRPC$Chat r13 = r0.currentChat
            boolean r7 = r13.megagroup
            if (r7 != 0) goto L_0x02a8
            int r7 = r8.participants_count
            if (r7 == 0) goto L_0x0374
            boolean r7 = org.telegram.messenger.ChatObject.hasAdminRights(r13)
            if (r7 != 0) goto L_0x0374
            org.telegram.tgnet.TLRPC$ChatFull r7 = r0.chatInfo
            boolean r7 = r7.can_view_participants
            if (r7 == 0) goto L_0x02a8
            goto L_0x0374
        L_0x02a8:
            org.telegram.tgnet.TLRPC$Chat r7 = r0.currentChat
            boolean r7 = r7.megagroup
            if (r7 == 0) goto L_0x0340
            int r7 = r0.onlineCount
            if (r7 <= r4) goto L_0x02f9
            org.telegram.tgnet.TLRPC$ChatFull r7 = r0.chatInfo
            int r7 = r7.participants_count
            if (r7 == 0) goto L_0x02f9
            java.lang.Object[] r8 = new java.lang.Object[r2]
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r9, r7)
            r13 = 0
            r8[r13] = r7
            int r7 = r0.onlineCount
            org.telegram.tgnet.TLRPC$ChatFull r13 = r0.chatInfo
            int r13 = r13.participants_count
            int r7 = java.lang.Math.min(r7, r13)
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r15, r7)
            r8[r4] = r7
            java.lang.String r7 = java.lang.String.format(r3, r8)
            java.lang.Object[] r8 = new java.lang.Object[r2]
            org.telegram.tgnet.TLRPC$ChatFull r13 = r0.chatInfo
            int r13 = r13.participants_count
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatPluralStringComma(r9, r13)
            r19 = 0
            r8[r19] = r13
            int r13 = r0.onlineCount
            org.telegram.tgnet.TLRPC$ChatFull r2 = r0.chatInfo
            int r2 = r2.participants_count
            int r2 = java.lang.Math.min(r13, r2)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralStringComma(r15, r2)
            r8[r4] = r2
            java.lang.String r2 = java.lang.String.format(r3, r8)
            goto L_0x03fb
        L_0x02f9:
            org.telegram.tgnet.TLRPC$ChatFull r2 = r0.chatInfo
            int r2 = r2.participants_count
            if (r2 != 0) goto L_0x0332
            boolean r2 = r5.has_geo
            if (r2 == 0) goto L_0x0310
            r2 = 2131626120(0x7f0e0888, float:1.8879467E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r14, r2)
            java.lang.String r7 = r3.toLowerCase()
            goto L_0x03b9
        L_0x0310:
            java.lang.String r2 = r5.username
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0325
            r2 = 2131626124(0x7f0e088c, float:1.8879475E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r10, r2)
            java.lang.String r7 = r3.toLowerCase()
            goto L_0x03b9
        L_0x0325:
            r2 = 2131626121(0x7f0e0889, float:1.887947E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r12, r2)
            java.lang.String r7 = r3.toLowerCase()
            goto L_0x03b9
        L_0x0332:
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r9, r2)
            org.telegram.tgnet.TLRPC$ChatFull r2 = r0.chatInfo
            int r2 = r2.participants_count
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralStringComma(r9, r2)
            goto L_0x03fb
        L_0x0340:
            int[] r2 = new int[r4]
            org.telegram.tgnet.TLRPC$ChatFull r3 = r0.chatInfo
            int r3 = r3.participants_count
            org.telegram.messenger.LocaleController.formatShortNumber(r3, r2)
            org.telegram.tgnet.TLRPC$Chat r2 = r0.currentChat
            boolean r2 = r2.megagroup
            if (r2 == 0) goto L_0x0360
            org.telegram.tgnet.TLRPC$ChatFull r2 = r0.chatInfo
            int r2 = r2.participants_count
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r9, r2)
            org.telegram.tgnet.TLRPC$ChatFull r3 = r0.chatInfo
            int r3 = r3.participants_count
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralStringComma(r9, r3)
            goto L_0x0370
        L_0x0360:
            org.telegram.tgnet.TLRPC$ChatFull r2 = r0.chatInfo
            int r2 = r2.participants_count
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r11, r2)
            org.telegram.tgnet.TLRPC$ChatFull r3 = r0.chatInfo
            int r3 = r3.participants_count
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralStringComma(r11, r3)
        L_0x0370:
            r7 = r2
            r2 = r3
            goto L_0x03fb
        L_0x0374:
            org.telegram.tgnet.TLRPC$Chat r2 = r0.currentChat
            boolean r2 = r2.megagroup
            if (r2 == 0) goto L_0x0388
            r2 = 2131626018(0x7f0e0822, float:1.887926E38)
            java.lang.String r3 = "Loading"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.String r7 = r2.toLowerCase()
            goto L_0x03b9
        L_0x0388:
            int r2 = r5.flags
            r2 = r2 & 64
            if (r2 == 0) goto L_0x039c
            r2 = 2131624769(0x7f0e0341, float:1.8876727E38)
            java.lang.String r3 = "ChannelPublic"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.String r7 = r2.toLowerCase()
            goto L_0x03b9
        L_0x039c:
            r2 = 2131624766(0x7f0e033e, float:1.887672E38)
            java.lang.String r3 = "ChannelPrivate"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.String r7 = r2.toLowerCase()
            goto L_0x03b9
        L_0x03aa:
            boolean r2 = org.telegram.messenger.ChatObject.isKickedFromChat(r5)
            if (r2 == 0) goto L_0x03bb
            r2 = 2131628315(0x7f0e111b, float:1.888392E38)
            java.lang.String r3 = "YouWereKicked"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r3, r2)
        L_0x03b9:
            r2 = r7
            goto L_0x03fb
        L_0x03bb:
            boolean r2 = org.telegram.messenger.ChatObject.isLeftFromChat(r5)
            if (r2 == 0) goto L_0x03cb
            r2 = 2131628314(0x7f0e111a, float:1.8883917E38)
            java.lang.String r3 = "YouLeft"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r3, r2)
            goto L_0x03b9
        L_0x03cb:
            int r2 = r5.participants_count
            org.telegram.tgnet.TLRPC$ChatFull r7 = r0.chatInfo
            if (r7 == 0) goto L_0x03d9
            org.telegram.tgnet.TLRPC$ChatParticipants r2 = r7.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r2 = r2.participants
            int r2 = r2.size()
        L_0x03d9:
            if (r2 == 0) goto L_0x03f6
            int r7 = r0.onlineCount
            if (r7 <= r4) goto L_0x03f6
            r7 = 2
            java.lang.Object[] r8 = new java.lang.Object[r7]
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r9, r2)
            r7 = 0
            r8[r7] = r2
            int r2 = r0.onlineCount
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r15, r2)
            r8[r4] = r2
            java.lang.String r7 = java.lang.String.format(r3, r8)
            goto L_0x03b9
        L_0x03f6:
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r9, r2)
            goto L_0x03b9
        L_0x03fb:
            r3 = 0
            r8 = 0
        L_0x03fd:
            r13 = 2
            if (r3 >= r13) goto L_0x05b6
            org.telegram.ui.ActionBar.SimpleTextView[] r13 = r0.nameTextView
            r15 = r13[r3]
            if (r15 != 0) goto L_0x0413
            r22 = r1
            r23 = r2
            r20 = r6
            r21 = r7
        L_0x040e:
            r17 = 2131626121(0x7f0e0889, float:1.887947E38)
            goto L_0x05a9
        L_0x0413:
            java.lang.String r15 = r5.title
            if (r15 == 0) goto L_0x0420
            r13 = r13[r3]
            boolean r13 = r13.setText(r15)
            if (r13 == 0) goto L_0x0420
            r8 = 1
        L_0x0420:
            org.telegram.ui.ActionBar.SimpleTextView[] r13 = r0.nameTextView
            r13 = r13[r3]
            r15 = 0
            r13.setLeftDrawable((android.graphics.drawable.Drawable) r15)
            if (r3 == 0) goto L_0x045e
            boolean r13 = r5.scam
            if (r13 != 0) goto L_0x044c
            boolean r15 = r5.fake
            if (r15 == 0) goto L_0x0433
            goto L_0x044c
        L_0x0433:
            boolean r13 = r5.verified
            if (r13 == 0) goto L_0x0443
            org.telegram.ui.ActionBar.SimpleTextView[] r13 = r0.nameTextView
            r13 = r13[r3]
            android.graphics.drawable.Drawable r15 = r24.getVerifiedCrossfadeDrawable()
            r13.setRightDrawable((android.graphics.drawable.Drawable) r15)
            goto L_0x0459
        L_0x0443:
            org.telegram.ui.ActionBar.SimpleTextView[] r13 = r0.nameTextView
            r13 = r13[r3]
            r15 = 0
            r13.setRightDrawable((android.graphics.drawable.Drawable) r15)
            goto L_0x0459
        L_0x044c:
            org.telegram.ui.ActionBar.SimpleTextView[] r15 = r0.nameTextView
            r15 = r15[r3]
            r13 = r13 ^ 1
            android.graphics.drawable.Drawable r13 = r0.getScamDrawable(r13)
            r15.setRightDrawable((android.graphics.drawable.Drawable) r13)
        L_0x0459:
            r20 = r6
            r21 = r7
            goto L_0x0496
        L_0x045e:
            boolean r13 = r5.scam
            if (r13 != 0) goto L_0x0485
            boolean r15 = r5.fake
            if (r15 == 0) goto L_0x0467
            goto L_0x0485
        L_0x0467:
            org.telegram.ui.ActionBar.SimpleTextView[] r13 = r0.nameTextView
            r13 = r13[r3]
            org.telegram.messenger.MessagesController r15 = r24.getMessagesController()
            int r4 = r0.chat_id
            int r4 = -r4
            r20 = r6
            r21 = r7
            long r6 = (long) r4
            boolean r4 = r15.isDialogMuted(r6)
            if (r4 == 0) goto L_0x0480
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.chat_muteIconDrawable
            goto L_0x0481
        L_0x0480:
            r15 = 0
        L_0x0481:
            r13.setRightDrawable((android.graphics.drawable.Drawable) r15)
            goto L_0x0496
        L_0x0485:
            r20 = r6
            r21 = r7
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r0.nameTextView
            r4 = r4[r3]
            r6 = r13 ^ 1
            android.graphics.drawable.Drawable r6 = r0.getScamDrawable(r6)
            r4.setRightDrawable((android.graphics.drawable.Drawable) r6)
        L_0x0496:
            if (r3 != 0) goto L_0x04a2
            if (r1 == 0) goto L_0x04a2
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r0.onlineTextView
            r4 = r4[r3]
            r4.setText(r1)
            goto L_0x0503
        L_0x04a2:
            org.telegram.tgnet.TLRPC$Chat r4 = r0.currentChat
            boolean r6 = r4.megagroup
            if (r6 == 0) goto L_0x04be
            org.telegram.tgnet.TLRPC$ChatFull r6 = r0.chatInfo
            if (r6 == 0) goto L_0x04be
            int r6 = r0.onlineCount
            if (r6 <= 0) goto L_0x04be
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r0.onlineTextView
            r4 = r4[r3]
            if (r3 != 0) goto L_0x04b9
            r6 = r21
            goto L_0x04ba
        L_0x04b9:
            r6 = r2
        L_0x04ba:
            r4.setText(r6)
            goto L_0x0503
        L_0x04be:
            if (r3 != 0) goto L_0x0594
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r4)
            if (r4 == 0) goto L_0x0594
            org.telegram.tgnet.TLRPC$ChatFull r4 = r0.chatInfo
            if (r4 == 0) goto L_0x0594
            int r4 = r4.participants_count
            if (r4 == 0) goto L_0x0594
            org.telegram.tgnet.TLRPC$Chat r6 = r0.currentChat
            boolean r7 = r6.megagroup
            if (r7 != 0) goto L_0x04d8
            boolean r6 = r6.broadcast
            if (r6 == 0) goto L_0x0594
        L_0x04d8:
            r6 = 1
            int[] r7 = new int[r6]
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatShortNumber(r4, r7)
            org.telegram.tgnet.TLRPC$Chat r6 = r0.currentChat
            boolean r6 = r6.megagroup
            java.lang.String r13 = "%d"
            if (r6 == 0) goto L_0x056a
            org.telegram.tgnet.TLRPC$ChatFull r6 = r0.chatInfo
            int r6 = r6.participants_count
            if (r6 != 0) goto L_0x053d
            boolean r4 = r5.has_geo
            if (r4 == 0) goto L_0x0509
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r0.onlineTextView
            r4 = r4[r3]
            r6 = 2131626120(0x7f0e0888, float:1.8879467E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r14, r6)
            java.lang.String r7 = r7.toLowerCase()
            r4.setText(r7)
        L_0x0503:
            r22 = r1
            r23 = r2
            goto L_0x040e
        L_0x0509:
            r6 = 2131626120(0x7f0e0888, float:1.8879467E38)
            java.lang.String r4 = r5.username
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x0527
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r0.onlineTextView
            r4 = r4[r3]
            r15 = 2131626124(0x7f0e088c, float:1.8879475E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r10, r15)
            java.lang.String r7 = r7.toLowerCase()
            r4.setText(r7)
            goto L_0x0503
        L_0x0527:
            r15 = 2131626124(0x7f0e088c, float:1.8879475E38)
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r0.onlineTextView
            r4 = r4[r3]
            r7 = 2131626121(0x7f0e0889, float:1.887947E38)
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r12, r7)
            java.lang.String r13 = r13.toLowerCase()
            r4.setText(r13)
            goto L_0x0503
        L_0x053d:
            r15 = 2131626124(0x7f0e088c, float:1.8879475E38)
            r17 = 2131626121(0x7f0e0889, float:1.887947E38)
            org.telegram.ui.ActionBar.SimpleTextView[] r6 = r0.onlineTextView
            r6 = r6[r3]
            r18 = 0
            r15 = r7[r18]
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatPluralString(r9, r15)
            r22 = r1
            r23 = r2
            r1 = 1
            java.lang.Object[] r2 = new java.lang.Object[r1]
            r1 = r7[r18]
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r2[r18] = r1
            java.lang.String r1 = java.lang.String.format(r13, r2)
            java.lang.String r1 = r15.replace(r1, r4)
            r6.setText(r1)
            goto L_0x05a9
        L_0x056a:
            r22 = r1
            r23 = r2
            r17 = 2131626121(0x7f0e0889, float:1.887947E38)
            r18 = 0
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r0.onlineTextView
            r1 = r1[r3]
            r2 = r7[r18]
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r11, r2)
            r6 = 1
            java.lang.Object[] r15 = new java.lang.Object[r6]
            r6 = r7[r18]
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r15[r18] = r6
            java.lang.String r6 = java.lang.String.format(r13, r15)
            java.lang.String r2 = r2.replace(r6, r4)
            r1.setText(r2)
            goto L_0x05a9
        L_0x0594:
            r22 = r1
            r23 = r2
            r17 = 2131626121(0x7f0e0889, float:1.887947E38)
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r0.onlineTextView
            r1 = r1[r3]
            if (r3 != 0) goto L_0x05a4
            r2 = r21
            goto L_0x05a6
        L_0x05a4:
            r2 = r23
        L_0x05a6:
            r1.setText(r2)
        L_0x05a9:
            int r3 = r3 + 1
            r6 = r20
            r7 = r21
            r1 = r22
            r2 = r23
            r4 = 1
            goto L_0x03fd
        L_0x05b6:
            r20 = r6
            r1 = 1
            if (r8 == 0) goto L_0x05be
            r0.needLayout(r1)
        L_0x05be:
            org.telegram.tgnet.TLRPC$ChatPhoto r2 = r5.photo
            if (r2 == 0) goto L_0x05c5
            org.telegram.tgnet.TLRPC$FileLocation r15 = r2.photo_big
            goto L_0x05c6
        L_0x05c5:
            r15 = 0
        L_0x05c6:
            org.telegram.ui.Components.AvatarDrawable r2 = r0.avatarDrawable
            r2.setInfo((org.telegram.tgnet.TLRPC$Chat) r5)
            r2 = 0
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForUserOrChat(r5, r2)
            org.telegram.messenger.ImageLocation r11 = org.telegram.messenger.ImageLocation.getForUserOrChat(r5, r1)
            org.telegram.ui.Components.ProfileGalleryView r1 = r0.avatarsViewPager
            org.telegram.messenger.ImageLocation r9 = r1.getCurrentVideoLocation(r11, r3)
            org.telegram.ui.Components.ProfileGalleryView r1 = r0.avatarsViewPager
            boolean r1 = r1.initIfEmpty(r3, r11)
            if (r3 == 0) goto L_0x05e4
            if (r1 == 0) goto L_0x0603
        L_0x05e4:
            boolean r1 = r0.isPulledDown
            if (r1 == 0) goto L_0x0603
            androidx.recyclerview.widget.LinearLayoutManager r1 = r0.layoutManager
            r2 = 0
            android.view.View r1 = r1.findViewByPosition(r2)
            if (r1 == 0) goto L_0x0603
            org.telegram.ui.Components.RecyclerListView r4 = r0.listView
            int r1 = r1.getTop()
            r6 = 1118830592(0x42b00000, float:88.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r1 = r1 - r6
            org.telegram.ui.Components.CubicBezierInterpolator r6 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            r4.smoothScrollBy(r2, r1, r6)
        L_0x0603:
            if (r9 == 0) goto L_0x060d
            int r1 = r9.imageType
            r2 = 2
            if (r1 != r2) goto L_0x060d
            r10 = r20
            goto L_0x060e
        L_0x060d:
            r10 = 0
        L_0x060e:
            org.telegram.tgnet.TLRPC$FileLocation r1 = r0.avatarBig
            if (r1 != 0) goto L_0x061c
            org.telegram.ui.ProfileActivity$AvatarImageView r8 = r0.avatarImage
            org.telegram.ui.Components.AvatarDrawable r13 = r0.avatarDrawable
            java.lang.String r12 = "50_50"
            r14 = r5
            r8.setImage((org.telegram.messenger.ImageLocation) r9, (java.lang.String) r10, (org.telegram.messenger.ImageLocation) r11, (java.lang.String) r12, (android.graphics.drawable.Drawable) r13, (java.lang.Object) r14)
        L_0x061c:
            if (r3 == 0) goto L_0x0638
            org.telegram.messenger.ImageLocation r1 = r0.prevLoadedImageLocation
            if (r1 == 0) goto L_0x062a
            long r6 = r3.photoId
            long r1 = r1.photoId
            int r4 = (r6 > r1 ? 1 : (r6 == r1 ? 0 : -1))
            if (r4 == 0) goto L_0x0638
        L_0x062a:
            r0.prevLoadedImageLocation = r3
            org.telegram.messenger.FileLoader r8 = r24.getFileLoader()
            r11 = 0
            r12 = 0
            r13 = 1
            r9 = r3
            r10 = r5
            r8.loadFile(r9, r10, r11, r12, r13)
        L_0x0638:
            org.telegram.ui.ProfileActivity$AvatarImageView r1 = r0.avatarImage
            org.telegram.messenger.ImageReceiver r1 = r1.getImageReceiver()
            boolean r2 = org.telegram.ui.PhotoViewer.isShowingImage((org.telegram.tgnet.TLRPC$FileLocation) r15)
            r3 = 1
            r2 = r2 ^ r3
            r3 = 0
            r1.setVisible(r2, r3)
        L_0x0648:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.updateProfileData():void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:155:0x0365  */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x039f  */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x03aa  */
    /* JADX WARNING: Removed duplicated region for block: B:161:0x03be  */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x03e1  */
    /* JADX WARNING: Removed duplicated region for block: B:200:0x0497  */
    /* JADX WARNING: Removed duplicated region for block: B:203:0x04ae  */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x04c5  */
    /* JADX WARNING: Removed duplicated region for block: B:209:0x04dc  */
    /* JADX WARNING: Removed duplicated region for block: B:212:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void createActionBarMenu(boolean r17) {
        /*
            r16 = this;
            r0 = r16
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            if (r1 == 0) goto L_0x04e3
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.otherItem
            if (r2 != 0) goto L_0x000c
            goto L_0x04e3
        L_0x000c:
            r1.createMenu()
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r1.removeAllSubItems()
            r1 = 0
            r0.animatingItem = r1
            r1 = 0
            r0.editItemVisible = r1
            r0.callItemVisible = r1
            r0.videoCallItemVisible = r1
            r0.canSearchMembers = r1
            int r2 = r0.user_id
            r3 = 2131165728(0x7var_, float:1.7945681E38)
            r4 = 2131165817(0x7var_, float:1.7945862E38)
            r5 = 2131165753(0x7var_, float:1.7945732E38)
            r6 = 1
            if (r2 == 0) goto L_0x01cf
            org.telegram.messenger.MessagesController r2 = r16.getMessagesController()
            int r7 = r0.user_id
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r7)
            if (r2 != 0) goto L_0x003f
            return
        L_0x003f:
            boolean r7 = org.telegram.messenger.UserObject.isUserSelf(r2)
            r8 = 2131165737(0x7var_, float:1.79457E38)
            if (r7 == 0) goto L_0x005a
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.otherItem
            r4 = 30
            r7 = 2131625281(0x7f0e0541, float:1.8877766E38)
            java.lang.String r9 = "EditName"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r9, r7)
            r2.addSubItem(r4, r8, r7)
            goto L_0x0351
        L_0x005a:
            org.telegram.tgnet.TLRPC$UserFull r7 = r0.userInfo
            if (r7 == 0) goto L_0x0073
            boolean r9 = r7.phone_calls_available
            if (r9 == 0) goto L_0x0073
            r0.callItemVisible = r6
            int r9 = android.os.Build.VERSION.SDK_INT
            r10 = 18
            if (r9 < r10) goto L_0x0070
            boolean r7 = r7.video_calls_available
            if (r7 == 0) goto L_0x0070
            r7 = 1
            goto L_0x0071
        L_0x0070:
            r7 = 0
        L_0x0071:
            r0.videoCallItemVisible = r7
        L_0x0073:
            boolean r7 = r0.isBot
            r9 = 2131627851(0x7f0e0f4b, float:1.8882978E38)
            java.lang.String r10 = "Unblock"
            r11 = 2131165712(0x7var_, float:1.7945649E38)
            r12 = 2
            if (r7 != 0) goto L_0x00e1
            org.telegram.messenger.ContactsController r7 = r16.getContactsController()
            j$.util.concurrent.ConcurrentHashMap<java.lang.Integer, org.telegram.tgnet.TLRPC$TL_contact> r7 = r7.contactsDict
            int r13 = r0.user_id
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            java.lang.Object r7 = r7.get(r13)
            if (r7 != 0) goto L_0x0093
            goto L_0x00e1
        L_0x0093:
            java.lang.String r6 = r2.phone
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 != 0) goto L_0x00aa
            org.telegram.ui.ActionBar.ActionBarMenuItem r6 = r0.otherItem
            r7 = 3
            r13 = 2131627473(0x7f0e0dd1, float:1.8882211E38)
            java.lang.String r14 = "ShareContact"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r6.addSubItem(r7, r4, r13)
        L_0x00aa:
            org.telegram.ui.ActionBar.ActionBarMenuItem r4 = r0.otherItem
            boolean r6 = r0.userBlocked
            if (r6 != 0) goto L_0x00ba
            r6 = 2131624558(0x7f0e026e, float:1.88763E38)
            java.lang.String r7 = "BlockContact"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            goto L_0x00be
        L_0x00ba:
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r10, r9)
        L_0x00be:
            r4.addSubItem(r12, r11, r6)
            org.telegram.ui.ActionBar.ActionBarMenuItem r4 = r0.otherItem
            r6 = 4
            r7 = 2131625269(0x7f0e0535, float:1.8877741E38)
            java.lang.String r9 = "EditContact"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r9, r7)
            r4.addSubItem(r6, r8, r7)
            org.telegram.ui.ActionBar.ActionBarMenuItem r4 = r0.otherItem
            r6 = 5
            r7 = 2131625137(0x7f0e04b1, float:1.8877473E38)
            java.lang.String r8 = "DeleteContact"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r4.addSubItem(r6, r3, r7)
            goto L_0x0184
        L_0x00e1:
            boolean r7 = org.telegram.messenger.MessagesController.isSupportUser(r2)
            if (r7 == 0) goto L_0x00f6
            boolean r4 = r0.userBlocked
            if (r4 == 0) goto L_0x0184
            org.telegram.ui.ActionBar.ActionBarMenuItem r4 = r0.otherItem
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r4.addSubItem(r12, r11, r6)
            goto L_0x0184
        L_0x00f6:
            boolean r7 = r0.isBot
            if (r7 == 0) goto L_0x0122
            boolean r6 = r2.bot_nochats
            if (r6 != 0) goto L_0x0111
            org.telegram.ui.ActionBar.ActionBarMenuItem r6 = r0.otherItem
            r7 = 9
            r8 = 2131165703(0x7var_, float:1.794563E38)
            r13 = 2131624587(0x7f0e028b, float:1.8876358E38)
            java.lang.String r14 = "BotInvite"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r6.addSubItem(r7, r8, r13)
        L_0x0111:
            org.telegram.ui.ActionBar.ActionBarMenuItem r6 = r0.otherItem
            r7 = 10
            r8 = 2131624595(0x7f0e0293, float:1.8876374E38)
            java.lang.String r13 = "BotShare"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r13, r8)
            r6.addSubItem(r7, r4, r8)
            goto L_0x0133
        L_0x0122:
            org.telegram.ui.ActionBar.ActionBarMenuItem r7 = r0.otherItem
            r8 = 2131165704(0x7var_, float:1.7945633E38)
            r13 = 2131624193(0x7f0e0101, float:1.8875559E38)
            java.lang.String r14 = "AddContact"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r7.addSubItem(r6, r8, r13)
        L_0x0133:
            java.lang.String r6 = r2.phone
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 != 0) goto L_0x014a
            org.telegram.ui.ActionBar.ActionBarMenuItem r6 = r0.otherItem
            r7 = 3
            r8 = 2131627473(0x7f0e0dd1, float:1.8882211E38)
            java.lang.String r13 = "ShareContact"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r13, r8)
            r6.addSubItem(r7, r4, r8)
        L_0x014a:
            boolean r4 = r0.isBot
            if (r4 == 0) goto L_0x016d
            org.telegram.ui.ActionBar.ActionBarMenuItem r4 = r0.otherItem
            boolean r6 = r0.userBlocked
            if (r6 != 0) goto L_0x0155
            goto L_0x0158
        L_0x0155:
            r11 = 2131165804(0x7var_c, float:1.7945835E38)
        L_0x0158:
            if (r6 != 0) goto L_0x0160
            r6 = 2131624599(0x7f0e0297, float:1.8876382E38)
            java.lang.String r7 = "BotStop"
            goto L_0x0165
        L_0x0160:
            r6 = 2131624593(0x7f0e0291, float:1.887637E38)
            java.lang.String r7 = "BotRestart"
        L_0x0165:
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r4.addSubItem(r12, r11, r6)
            goto L_0x0184
        L_0x016d:
            org.telegram.ui.ActionBar.ActionBarMenuItem r4 = r0.otherItem
            boolean r6 = r0.userBlocked
            if (r6 != 0) goto L_0x017d
            r6 = 2131624558(0x7f0e026e, float:1.88763E38)
            java.lang.String r7 = "BlockContact"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            goto L_0x0181
        L_0x017d:
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r10, r9)
        L_0x0181:
            r4.addSubItem(r12, r11, r6)
        L_0x0184:
            boolean r2 = org.telegram.messenger.UserObject.isDeleted(r2)
            if (r2 != 0) goto L_0x01ba
            boolean r2 = r0.isBot
            if (r2 != 0) goto L_0x01ba
            org.telegram.tgnet.TLRPC$EncryptedChat r2 = r0.currentEncryptedChat
            if (r2 != 0) goto L_0x01ba
            boolean r2 = r0.userBlocked
            if (r2 != 0) goto L_0x01ba
            int r2 = r0.user_id
            r4 = 333000(0x514c8, float:4.66632E-40)
            if (r2 == r4) goto L_0x01ba
            r4 = 777000(0xbdb28, float:1.088809E-39)
            if (r2 == r4) goto L_0x01ba
            r4 = 42777(0xa719, float:5.9943E-41)
            if (r2 == r4) goto L_0x01ba
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.otherItem
            r4 = 20
            r6 = 2131165820(0x7var_c, float:1.7945868E38)
            r7 = 2131627582(0x7f0e0e3e, float:1.8882433E38)
            java.lang.String r8 = "StartEncryptedChat"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r2.addSubItem(r4, r6, r7)
        L_0x01ba:
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.otherItem
            r4 = 14
            r6 = 2131165748(0x7var_, float:1.7945722E38)
            r7 = 2131624215(0x7f0e0117, float:1.8875603E38)
            java.lang.String r8 = "AddShortcut"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r2.addSubItem(r4, r6, r7)
            goto L_0x0350
        L_0x01cf:
            int r2 = r0.chat_id
            if (r2 == 0) goto L_0x0350
            org.telegram.messenger.MessagesController r2 = r16.getMessagesController()
            int r7 = r0.chat_id
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r7)
            r0.hasVoiceChatItem = r1
            boolean r7 = org.telegram.messenger.ChatObject.isChannel(r2)
            r8 = 7
            if (r7 == 0) goto L_0x02d0
            boolean r7 = org.telegram.messenger.ChatObject.hasAdminRights(r2)
            if (r7 != 0) goto L_0x01fa
            boolean r7 = r2.megagroup
            if (r7 == 0) goto L_0x01fc
            boolean r7 = org.telegram.messenger.ChatObject.canChangeChatInfo(r2)
            if (r7 == 0) goto L_0x01fc
        L_0x01fa:
            r0.editItemVisible = r6
        L_0x01fc:
            org.telegram.tgnet.TLRPC$ChatFull r7 = r0.chatInfo
            if (r7 == 0) goto L_0x024b
            boolean r7 = org.telegram.messenger.ChatObject.canManageCalls(r2)
            if (r7 == 0) goto L_0x0221
            org.telegram.tgnet.TLRPC$ChatFull r7 = r0.chatInfo
            org.telegram.tgnet.TLRPC$TL_inputGroupCall r7 = r7.call
            if (r7 != 0) goto L_0x0221
            org.telegram.ui.ActionBar.ActionBarMenuItem r7 = r0.otherItem
            r9 = 15
            r10 = 2131165849(0x7var_, float:1.7945927E38)
            r11 = 2131627590(0x7f0e0e46, float:1.8882449E38)
            java.lang.String r12 = "StartVoipChat"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            r7.addSubItem(r9, r10, r11)
            r0.hasVoiceChatItem = r6
        L_0x0221:
            org.telegram.tgnet.TLRPC$ChatFull r7 = r0.chatInfo
            boolean r7 = r7.can_view_stats
            if (r7 == 0) goto L_0x023a
            org.telegram.ui.ActionBar.ActionBarMenuItem r7 = r0.otherItem
            r9 = 19
            r10 = 2131165821(0x7var_d, float:1.794587E38)
            r11 = 2131627601(0x7f0e0e51, float:1.8882471E38)
            java.lang.String r12 = "Statistics"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            r7.addSubItem(r9, r10, r11)
        L_0x023a:
            org.telegram.messenger.MessagesController r7 = r16.getMessagesController()
            int r9 = r0.chat_id
            org.telegram.messenger.ChatObject$Call r7 = r7.getGroupCall(r9, r1)
            if (r7 == 0) goto L_0x0248
            r7 = 1
            goto L_0x0249
        L_0x0248:
            r7 = 0
        L_0x0249:
            r0.callItemVisible = r7
        L_0x024b:
            boolean r7 = r2.megagroup
            if (r7 == 0) goto L_0x0280
            r0.canSearchMembers = r6
            org.telegram.ui.ActionBar.ActionBarMenuItem r4 = r0.otherItem
            r6 = 17
            r7 = 2131165815(0x7var_, float:1.7945858E38)
            r9 = 2131627336(0x7f0e0d48, float:1.8881934E38)
            java.lang.String r10 = "SearchMembers"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r4.addSubItem(r6, r7, r9)
            boolean r4 = r2.creator
            if (r4 != 0) goto L_0x033d
            boolean r4 = r2.left
            if (r4 != 0) goto L_0x033d
            boolean r2 = r2.kicked
            if (r2 != 0) goto L_0x033d
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.otherItem
            r4 = 2131625971(0x7f0e07f3, float:1.8879165E38)
            java.lang.String r6 = "LeaveMegaMenu"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r2.addSubItem(r8, r5, r4)
            goto L_0x033d
        L_0x0280:
            java.lang.String r2 = r2.username
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0298
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.otherItem
            r6 = 10
            r7 = 2131624595(0x7f0e0293, float:1.8876374E38)
            java.lang.String r9 = "BotShare"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r9, r7)
            r2.addSubItem(r6, r4, r7)
        L_0x0298:
            org.telegram.tgnet.TLRPC$ChatFull r2 = r0.chatInfo
            if (r2 == 0) goto L_0x02b3
            int r2 = r2.linked_chat_id
            if (r2 == 0) goto L_0x02b3
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.otherItem
            r4 = 22
            r6 = 2131165731(0x7var_, float:1.7945687E38)
            r7 = 2131628024(0x7f0e0ff8, float:1.888333E38)
            java.lang.String r9 = "ViewDiscussion"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r9, r7)
            r2.addSubItem(r4, r6, r7)
        L_0x02b3:
            org.telegram.tgnet.TLRPC$Chat r2 = r0.currentChat
            boolean r4 = r2.creator
            if (r4 != 0) goto L_0x033d
            boolean r4 = r2.left
            if (r4 != 0) goto L_0x033d
            boolean r2 = r2.kicked
            if (r2 != 0) goto L_0x033d
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.otherItem
            r4 = 2131625969(0x7f0e07f1, float:1.887916E38)
            java.lang.String r6 = "LeaveChannelMenu"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r2.addSubItem(r8, r5, r4)
            goto L_0x033d
        L_0x02d0:
            org.telegram.tgnet.TLRPC$ChatFull r4 = r0.chatInfo
            if (r4 == 0) goto L_0x0306
            boolean r4 = org.telegram.messenger.ChatObject.canManageCalls(r2)
            if (r4 == 0) goto L_0x02f5
            org.telegram.tgnet.TLRPC$ChatFull r4 = r0.chatInfo
            org.telegram.tgnet.TLRPC$TL_inputGroupCall r4 = r4.call
            if (r4 != 0) goto L_0x02f5
            org.telegram.ui.ActionBar.ActionBarMenuItem r4 = r0.otherItem
            r7 = 15
            r9 = 2131165849(0x7var_, float:1.7945927E38)
            r10 = 2131627590(0x7f0e0e46, float:1.8882449E38)
            java.lang.String r11 = "StartVoipChat"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r4.addSubItem(r7, r9, r10)
            r0.hasVoiceChatItem = r6
        L_0x02f5:
            org.telegram.messenger.MessagesController r4 = r16.getMessagesController()
            int r7 = r0.chat_id
            org.telegram.messenger.ChatObject$Call r4 = r4.getGroupCall(r7, r1)
            if (r4 == 0) goto L_0x0303
            r4 = 1
            goto L_0x0304
        L_0x0303:
            r4 = 0
        L_0x0304:
            r0.callItemVisible = r4
        L_0x0306:
            boolean r4 = org.telegram.messenger.ChatObject.canChangeChatInfo(r2)
            if (r4 == 0) goto L_0x030e
            r0.editItemVisible = r6
        L_0x030e:
            boolean r4 = org.telegram.messenger.ChatObject.isKickedFromChat(r2)
            if (r4 != 0) goto L_0x032f
            boolean r2 = org.telegram.messenger.ChatObject.isLeftFromChat(r2)
            if (r2 != 0) goto L_0x032f
            r0.canSearchMembers = r6
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.otherItem
            r4 = 17
            r6 = 2131165815(0x7var_, float:1.7945858E38)
            r7 = 2131627336(0x7f0e0d48, float:1.8881934E38)
            java.lang.String r9 = "SearchMembers"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r9, r7)
            r2.addSubItem(r4, r6, r7)
        L_0x032f:
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.otherItem
            r4 = 2131625121(0x7f0e04a1, float:1.887744E38)
            java.lang.String r6 = "DeleteAndExit"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r2.addSubItem(r8, r5, r4)
        L_0x033d:
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.otherItem
            r4 = 14
            r6 = 2131165748(0x7var_, float:1.7945722E38)
            r7 = 2131624215(0x7f0e0117, float:1.8875603E38)
            java.lang.String r8 = "AddShortcut"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r2.addSubItem(r4, r6, r7)
        L_0x0350:
            r6 = 0
        L_0x0351:
            org.telegram.ui.Components.ImageUpdater r2 = r0.imageUpdater
            r4 = 35
            r7 = 33
            r8 = 36
            r9 = 2131627297(0x7f0e0d21, float:1.8881854E38)
            java.lang.String r10 = "SaveToGallery"
            r11 = 2131165741(0x7var_d, float:1.7945708E38)
            r12 = 21
            if (r2 == 0) goto L_0x039f
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.otherItem
            r13 = 2131165706(0x7var_a, float:1.7945637E38)
            r14 = 2131624212(0x7f0e0114, float:1.8875597E38)
            java.lang.String r15 = "AddPhoto"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r15, r14)
            r2.addSubItem(r8, r13, r14)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.otherItem
            r13 = 2131165670(0x7var_e6, float:1.7945564E38)
            r14 = 2131627445(0x7f0e0db5, float:1.8882155E38)
            java.lang.String r15 = "SetAsMain"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r15, r14)
            r2.addSubItem(r7, r13, r14)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.otherItem
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r2.addSubItem(r12, r11, r9)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.otherItem
            r9 = 2131625108(0x7f0e0494, float:1.8877415E38)
            java.lang.String r10 = "Delete"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r2.addSubItem(r4, r3, r9)
            goto L_0x03a8
        L_0x039f:
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.otherItem
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r2.addSubItem(r12, r11, r3)
        L_0x03a8:
            if (r6 == 0) goto L_0x03ba
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.otherItem
            r3 = 31
            r6 = 2131626042(0x7f0e083a, float:1.887931E38)
            java.lang.String r9 = "LogOut"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r2.addSubItem(r3, r5, r6)
        L_0x03ba:
            boolean r2 = r0.isPulledDown
            if (r2 != 0) goto L_0x03d9
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.otherItem
            r2.hideSubItem(r12)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.otherItem
            r2.hideSubItem(r7)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.otherItem
            r2.showSubItem(r8)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.otherItem
            r3 = 34
            r2.hideSubItem(r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.otherItem
            r2.hideSubItem(r4)
        L_0x03d9:
            boolean r2 = r0.mediaHeaderVisible
            r3 = 1065353216(0x3var_, float:1.0)
            r4 = 8
            if (r2 != 0) goto L_0x0489
            boolean r2 = r0.callItemVisible
            r5 = 150(0x96, double:7.4E-322)
            r7 = 0
            if (r2 == 0) goto L_0x040e
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.callItem
            int r2 = r2.getVisibility()
            if (r2 == 0) goto L_0x041b
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.callItem
            r2.setVisibility(r1)
            if (r17 == 0) goto L_0x041b
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.callItem
            r2.setAlpha(r7)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.callItem
            android.view.ViewPropertyAnimator r2 = r2.animate()
            android.view.ViewPropertyAnimator r2 = r2.alpha(r3)
            android.view.ViewPropertyAnimator r2 = r2.setDuration(r5)
            r2.start()
            goto L_0x041b
        L_0x040e:
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.callItem
            int r2 = r2.getVisibility()
            if (r2 == r4) goto L_0x041b
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.callItem
            r2.setVisibility(r4)
        L_0x041b:
            boolean r2 = r0.videoCallItemVisible
            if (r2 == 0) goto L_0x0445
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.videoCallItem
            int r2 = r2.getVisibility()
            if (r2 == 0) goto L_0x0452
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.videoCallItem
            r2.setVisibility(r1)
            if (r17 == 0) goto L_0x0452
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.videoCallItem
            r2.setAlpha(r7)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.videoCallItem
            android.view.ViewPropertyAnimator r2 = r2.animate()
            android.view.ViewPropertyAnimator r2 = r2.alpha(r3)
            android.view.ViewPropertyAnimator r2 = r2.setDuration(r5)
            r2.start()
            goto L_0x0452
        L_0x0445:
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.videoCallItem
            int r2 = r2.getVisibility()
            if (r2 == r4) goto L_0x0452
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.videoCallItem
            r2.setVisibility(r4)
        L_0x0452:
            boolean r2 = r0.editItemVisible
            if (r2 == 0) goto L_0x047c
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.editItem
            int r2 = r2.getVisibility()
            if (r2 == 0) goto L_0x0489
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.editItem
            r2.setVisibility(r1)
            if (r17 == 0) goto L_0x0489
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.editItem
            r1.setAlpha(r7)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.editItem
            android.view.ViewPropertyAnimator r1 = r1.animate()
            android.view.ViewPropertyAnimator r1 = r1.alpha(r3)
            android.view.ViewPropertyAnimator r1 = r1.setDuration(r5)
            r1.start()
            goto L_0x0489
        L_0x047c:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.editItem
            int r1 = r1.getVisibility()
            if (r1 == r4) goto L_0x0489
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.editItem
            r1.setVisibility(r4)
        L_0x0489:
            org.telegram.ui.ProfileActivity$PagerIndicatorView r1 = r0.avatarsViewPagerIndicatorView
            if (r1 == 0) goto L_0x04d8
            boolean r1 = r1.isIndicatorFullyVisible()
            if (r1 == 0) goto L_0x04d8
            boolean r1 = r0.editItemVisible
            if (r1 == 0) goto L_0x04aa
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.editItem
            r1.setVisibility(r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.editItem
            android.view.ViewPropertyAnimator r1 = r1.animate()
            r1.cancel()
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.editItem
            r1.setAlpha(r3)
        L_0x04aa:
            boolean r1 = r0.callItemVisible
            if (r1 == 0) goto L_0x04c1
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.callItem
            r1.setVisibility(r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.callItem
            android.view.ViewPropertyAnimator r1 = r1.animate()
            r1.cancel()
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.callItem
            r1.setAlpha(r3)
        L_0x04c1:
            boolean r1 = r0.videoCallItemVisible
            if (r1 == 0) goto L_0x04d8
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.videoCallItem
            r1.setVisibility(r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.videoCallItem
            android.view.ViewPropertyAnimator r1 = r1.animate()
            r1.cancel()
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.videoCallItem
            r1.setAlpha(r3)
        L_0x04d8:
            org.telegram.ui.Components.SharedMediaLayout r1 = r0.sharedMediaLayout
            if (r1 == 0) goto L_0x04e3
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r1.getSearchItem()
            r1.requestLayout()
        L_0x04e3:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.createActionBarMenu(boolean):void");
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
        int i = (int) longValue;
        if (i == 0) {
            bundle.putInt("enc_id", (int) (longValue >> 32));
        } else if (i > 0) {
            bundle.putInt("user_id", i);
        } else {
            bundle.putInt("chat_id", -i);
        }
        if (getMessagesController().checkCanOpenChat(bundle, dialogsActivity)) {
            NotificationCenter notificationCenter = getNotificationCenter();
            int i2 = NotificationCenter.closeChats;
            notificationCenter.removeObserver(this, i2);
            getNotificationCenter().postNotificationName(i2, new Object[0]);
            presentFragment(new ChatActivity(bundle), true);
            removeSelfFromStack();
            getSendMessagesHelper().sendMessage(getMessagesController().getUser(Integer.valueOf(this.user_id)), longValue, (MessageObject) null, (MessageObject) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
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
            TLRPC$User user = getMessagesController().getUser(Integer.valueOf(this.user_id));
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
            VoIPHelper.startCall(this.currentChat, (TLRPC$InputPeer) null, (String) null, getMessagesController().getGroupCall(this.chat_id, false) == null, getParentActivity(), this, getAccountInstance());
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
        ActionBarMenuItem actionBarMenuItem = this.otherItem;
        int i = 8;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.setVisibility(this.searchTransitionProgress > 0.5f ? 0 : 8);
        }
        this.searchItem.setVisibility(this.searchTransitionProgress > 0.5f ? 0 : 8);
        FrameLayout searchContainer = this.searchItem.getSearchContainer();
        if (this.searchTransitionProgress <= 0.5f) {
            i = 0;
        }
        searchContainer.setVisibility(i);
        this.searchListView.setEmptyView(this.emptyView);
        this.avatarContainer.setClickable(false);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(ofFloat, f, z) {
            public final /* synthetic */ ValueAnimator f$1;
            public final /* synthetic */ float f$2;
            public final /* synthetic */ boolean f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                ProfileActivity.this.lambda$searchExpandTransition$28$ProfileActivity(this.f$1, this.f$2, this.f$3, valueAnimator);
            }
        });
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
    /* renamed from: lambda$searchExpandTransition$28 */
    public /* synthetic */ void lambda$searchExpandTransition$28$ProfileActivity(ValueAnimator valueAnimator, float f, boolean z, ValueAnimator valueAnimator2) {
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
        ActionBarMenuItem actionBarMenuItem = this.otherItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.setVisibility(this.searchTransitionProgress > 0.5f ? 0 : 8);
        }
        ActionBarMenuItem actionBarMenuItem2 = this.searchItem;
        if (this.searchTransitionProgress > 0.5f) {
            i = 0;
        }
        actionBarMenuItem2.setVisibility(i);
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
        FrameLayout searchContainer = this.searchItem.getSearchContainer();
        if (!z) {
            i = 8;
        }
        searchContainer.setVisibility(i);
        this.actionBar.onSearchFieldVisibilityChanged(z);
        this.avatarContainer.setVisibility(i2);
        this.nameTextView[1].setVisibility(i2);
        this.onlineTextView[1].setVisibility(i2);
        ActionBarMenuItem actionBarMenuItem = this.otherItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.setAlpha(1.0f);
            this.otherItem.setVisibility(i2);
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
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$InputFile, tLRPC$InputFile2, d, str, tLRPC$PhotoSize2, tLRPC$PhotoSize) {
            public final /* synthetic */ TLRPC$InputFile f$1;
            public final /* synthetic */ TLRPC$InputFile f$2;
            public final /* synthetic */ double f$3;
            public final /* synthetic */ String f$4;
            public final /* synthetic */ TLRPC$PhotoSize f$5;
            public final /* synthetic */ TLRPC$PhotoSize f$6;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r6;
                this.f$5 = r7;
                this.f$6 = r8;
            }

            public final void run() {
                ProfileActivity.this.lambda$didUploadPhoto$31$ProfileActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$didUploadPhoto$30 */
    public /* synthetic */ void lambda$didUploadPhoto$30$ProfileActivity(String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject, str) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;
            public final /* synthetic */ String f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ProfileActivity.this.lambda$didUploadPhoto$29$ProfileActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$didUploadPhoto$29 */
    public /* synthetic */ void lambda$didUploadPhoto$29$ProfileActivity(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, String str) {
        this.avatarsViewPager.removeUploadingImage(this.uploadingImageLocation);
        if (tLRPC$TL_error == null) {
            TLRPC$User user = getMessagesController().getUser(Integer.valueOf(getUserConfig().getClientUserId()));
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
        getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, 1535);
        getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
        getUserConfig().saveConfig(true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$didUploadPhoto$31 */
    public /* synthetic */ void lambda$didUploadPhoto$31$ProfileActivity(TLRPC$InputFile tLRPC$InputFile, TLRPC$InputFile tLRPC$InputFile2, double d, String str, TLRPC$PhotoSize tLRPC$PhotoSize, TLRPC$PhotoSize tLRPC$PhotoSize2) {
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
            getConnectionsManager().sendRequest(tLRPC$TL_photos_uploadProfilePhoto, new RequestDelegate(str) {
                public final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    ProfileActivity.this.lambda$didUploadPhoto$30$ProfileActivity(this.f$1, tLObject, tLRPC$TL_error);
                }
            });
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
            alertDialog.setCanCacnel(false);
            alertDialog.show();
            Utilities.globalQueue.postRunnable(new Runnable(z, alertDialog) {
                public final /* synthetic */ boolean f$1;
                public final /* synthetic */ AlertDialog f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    ProfileActivity.this.lambda$sendLogs$33$ProfileActivity(this.f$1, this.f$2);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00ad A[SYNTHETIC, Splitter:B:40:0x00ad] */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00b2 A[Catch:{ Exception -> 0x00d1 }] */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x00c5 A[Catch:{ Exception -> 0x00c9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x00cd A[Catch:{ Exception -> 0x00c9 }] */
    /* renamed from: lambda$sendLogs$33 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$sendLogs$33$ProfileActivity(boolean r18, org.telegram.ui.ActionBar.AlertDialog r19) {
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
            org.telegram.ui.-$$Lambda$ProfileActivity$Be-OZnN7FUZoQfCOHUCTqaq-LYE r0 = new org.telegram.ui.-$$Lambda$ProfileActivity$Be-OZnN7FUZoQfCOHUCTqaq-LYE     // Catch:{ Exception -> 0x00d1 }
            r2 = r17
            r1 = r19
            r0.<init>(r1, r4, r3)     // Catch:{ Exception -> 0x00c9 }
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.lambda$sendLogs$33$ProfileActivity(boolean, org.telegram.ui.ActionBar.AlertDialog):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$sendLogs$32 */
    public /* synthetic */ void lambda$sendLogs$32$ProfileActivity(AlertDialog alertDialog, boolean[] zArr, File file) {
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

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v0, resolved type: org.telegram.ui.Components.SharedMediaLayout} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v1, resolved type: org.telegram.ui.Components.SharedMediaLayout} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v3, resolved type: org.telegram.ui.Cells.TextDetailCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v22, resolved type: org.telegram.ui.Cells.TextInfoPrivacyCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v18, resolved type: org.telegram.ui.Components.SharedMediaLayout} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v28, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v29, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v30, resolved type: org.telegram.ui.ProfileActivity$ListAdapter$1} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v31, resolved type: org.telegram.ui.Cells.TextCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v32, resolved type: org.telegram.ui.Cells.DividerCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v33, resolved type: org.telegram.ui.Cells.NotificationsCheckCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v34, resolved type: org.telegram.ui.Cells.ShadowSectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v35, resolved type: org.telegram.ui.Cells.UserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v36, resolved type: org.telegram.ui.ProfileActivity$ListAdapter$2} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v37, resolved type: org.telegram.ui.ProfileActivity$ListAdapter$3} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v38, resolved type: org.telegram.ui.Components.SharedMediaLayout} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r13, int r14) {
            /*
                r12 = this;
                r13 = -1
                r0 = 23
                r1 = 0
                r2 = 1
                r3 = 0
                switch(r14) {
                    case 1: goto L_0x017f;
                    case 2: goto L_0x0174;
                    case 3: goto L_0x016a;
                    case 4: goto L_0x0162;
                    case 5: goto L_0x014b;
                    case 6: goto L_0x0141;
                    case 7: goto L_0x0139;
                    case 8: goto L_0x0125;
                    case 9: goto L_0x0009;
                    case 10: goto L_0x0009;
                    case 11: goto L_0x011d;
                    case 12: goto L_0x010d;
                    case 13: goto L_0x00e4;
                    case 14: goto L_0x000b;
                    default: goto L_0x0009;
                }
            L_0x0009:
                goto L_0x0186
            L_0x000b:
                org.telegram.ui.Cells.TextInfoPrivacyCell r0 = new org.telegram.ui.Cells.TextInfoPrivacyCell
                android.content.Context r4 = r12.mContext
                r5 = 10
                r0.<init>(r4, r5)
                android.widget.TextView r4 = r0.getTextView()
                r4.setGravity(r2)
                android.widget.TextView r4 = r0.getTextView()
                java.lang.String r6 = "windowBackgroundWhiteGrayText3"
                int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
                r4.setTextColor(r6)
                android.widget.TextView r4 = r0.getTextView()
                r4.setMovementMethod(r1)
                android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x00be }
                android.content.pm.PackageManager r1 = r1.getPackageManager()     // Catch:{ Exception -> 0x00be }
                android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x00be }
                java.lang.String r4 = r4.getPackageName()     // Catch:{ Exception -> 0x00be }
                android.content.pm.PackageInfo r1 = r1.getPackageInfo(r4, r3)     // Catch:{ Exception -> 0x00be }
                int r4 = r1.versionCode     // Catch:{ Exception -> 0x00be }
                int r6 = r4 / 10
                java.lang.String r7 = ""
                int r4 = r4 % r5
                switch(r4) {
                    case 0: goto L_0x0056;
                    case 1: goto L_0x0053;
                    case 2: goto L_0x0050;
                    case 3: goto L_0x0053;
                    case 4: goto L_0x0050;
                    case 5: goto L_0x004d;
                    case 6: goto L_0x004a;
                    case 7: goto L_0x004d;
                    case 8: goto L_0x004a;
                    case 9: goto L_0x0056;
                    default: goto L_0x0049;
                }     // Catch:{ Exception -> 0x00be }
            L_0x0049:
                goto L_0x0095
            L_0x004a:
                java.lang.String r7 = "x86_64"
                goto L_0x0095
            L_0x004d:
                java.lang.String r7 = "arm64-v8a"
                goto L_0x0095
            L_0x0050:
                java.lang.String r7 = "x86"
                goto L_0x0095
            L_0x0053:
                java.lang.String r7 = "arm-v7a"
                goto L_0x0095
            L_0x0056:
                boolean r4 = org.telegram.messenger.AndroidUtilities.isStandaloneApp()     // Catch:{ Exception -> 0x00be }
                java.lang.String r5 = " "
                if (r4 == 0) goto L_0x007a
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00be }
                r4.<init>()     // Catch:{ Exception -> 0x00be }
                java.lang.String r7 = "direct "
                r4.append(r7)     // Catch:{ Exception -> 0x00be }
                java.lang.String r7 = android.os.Build.CPU_ABI     // Catch:{ Exception -> 0x00be }
                r4.append(r7)     // Catch:{ Exception -> 0x00be }
                r4.append(r5)     // Catch:{ Exception -> 0x00be }
                java.lang.String r5 = android.os.Build.CPU_ABI2     // Catch:{ Exception -> 0x00be }
                r4.append(r5)     // Catch:{ Exception -> 0x00be }
                java.lang.String r7 = r4.toString()     // Catch:{ Exception -> 0x00be }
                goto L_0x0095
            L_0x007a:
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00be }
                r4.<init>()     // Catch:{ Exception -> 0x00be }
                java.lang.String r7 = "universal "
                r4.append(r7)     // Catch:{ Exception -> 0x00be }
                java.lang.String r7 = android.os.Build.CPU_ABI     // Catch:{ Exception -> 0x00be }
                r4.append(r7)     // Catch:{ Exception -> 0x00be }
                r4.append(r5)     // Catch:{ Exception -> 0x00be }
                java.lang.String r5 = android.os.Build.CPU_ABI2     // Catch:{ Exception -> 0x00be }
                r4.append(r5)     // Catch:{ Exception -> 0x00be }
                java.lang.String r7 = r4.toString()     // Catch:{ Exception -> 0x00be }
            L_0x0095:
                java.lang.String r4 = "TelegramVersion"
                r5 = 2131627722(0x7f0e0eca, float:1.8882716E38)
                java.lang.Object[] r8 = new java.lang.Object[r2]     // Catch:{ Exception -> 0x00be }
                java.util.Locale r9 = java.util.Locale.US     // Catch:{ Exception -> 0x00be }
                java.lang.String r10 = "v%s (%d) %s"
                r11 = 3
                java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ Exception -> 0x00be }
                java.lang.String r1 = r1.versionName     // Catch:{ Exception -> 0x00be }
                r11[r3] = r1     // Catch:{ Exception -> 0x00be }
                java.lang.Integer r1 = java.lang.Integer.valueOf(r6)     // Catch:{ Exception -> 0x00be }
                r11[r2] = r1     // Catch:{ Exception -> 0x00be }
                r1 = 2
                r11[r1] = r7     // Catch:{ Exception -> 0x00be }
                java.lang.String r1 = java.lang.String.format(r9, r10, r11)     // Catch:{ Exception -> 0x00be }
                r8[r3] = r1     // Catch:{ Exception -> 0x00be }
                java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r4, r5, r8)     // Catch:{ Exception -> 0x00be }
                r0.setText(r1)     // Catch:{ Exception -> 0x00be }
                goto L_0x00c2
            L_0x00be:
                r1 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
            L_0x00c2:
                android.widget.TextView r1 = r0.getTextView()
                r2 = 1096810496(0x41600000, float:14.0)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                r1.setPadding(r3, r4, r3, r2)
                android.content.Context r1 = r12.mContext
                r2 = 2131165442(0x7var_, float:1.7945101E38)
                java.lang.String r3 = "windowBackgroundGrayShadow"
                android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r1, (int) r2, (java.lang.String) r3)
                r0.setBackgroundDrawable(r1)
                r1 = r0
                goto L_0x0186
            L_0x00e4:
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                org.telegram.ui.Components.SharedMediaLayout r0 = r0.sharedMediaLayout
                android.view.ViewParent r0 = r0.getParent()
                if (r0 == 0) goto L_0x0105
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                org.telegram.ui.Components.SharedMediaLayout r0 = r0.sharedMediaLayout
                android.view.ViewParent r0 = r0.getParent()
                android.view.ViewGroup r0 = (android.view.ViewGroup) r0
                org.telegram.ui.ProfileActivity r1 = org.telegram.ui.ProfileActivity.this
                org.telegram.ui.Components.SharedMediaLayout r1 = r1.sharedMediaLayout
                r0.removeView(r1)
            L_0x0105:
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                org.telegram.ui.Components.SharedMediaLayout r1 = r0.sharedMediaLayout
                goto L_0x0186
            L_0x010d:
                org.telegram.ui.ProfileActivity$ListAdapter$3 r1 = new org.telegram.ui.ProfileActivity$ListAdapter$3
                android.content.Context r0 = r12.mContext
                r1.<init>(r0)
                android.graphics.drawable.ColorDrawable r0 = new android.graphics.drawable.ColorDrawable
                r0.<init>(r3)
                r1.setBackground(r0)
                goto L_0x0186
            L_0x011d:
                org.telegram.ui.ProfileActivity$ListAdapter$2 r1 = new org.telegram.ui.ProfileActivity$ListAdapter$2
                android.content.Context r0 = r12.mContext
                r1.<init>(r0)
                goto L_0x0186
            L_0x0125:
                org.telegram.ui.Cells.UserCell r1 = new org.telegram.ui.Cells.UserCell
                android.content.Context r0 = r12.mContext
                org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                int r4 = r4.addMemberRow
                if (r4 != r13) goto L_0x0134
                r4 = 9
                goto L_0x0135
            L_0x0134:
                r4 = 6
            L_0x0135:
                r1.<init>(r0, r4, r3, r2)
                goto L_0x0186
            L_0x0139:
                org.telegram.ui.Cells.ShadowSectionCell r1 = new org.telegram.ui.Cells.ShadowSectionCell
                android.content.Context r0 = r12.mContext
                r1.<init>(r0)
                goto L_0x0186
            L_0x0141:
                org.telegram.ui.Cells.NotificationsCheckCell r1 = new org.telegram.ui.Cells.NotificationsCheckCell
                android.content.Context r2 = r12.mContext
                r4 = 70
                r1.<init>(r2, r0, r4, r3)
                goto L_0x0186
            L_0x014b:
                org.telegram.ui.Cells.DividerCell r1 = new org.telegram.ui.Cells.DividerCell
                android.content.Context r0 = r12.mContext
                r1.<init>(r0)
                r0 = 1101004800(0x41a00000, float:20.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                r2 = 1082130432(0x40800000, float:4.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                r1.setPadding(r0, r2, r3, r3)
                goto L_0x0186
            L_0x0162:
                org.telegram.ui.Cells.TextCell r1 = new org.telegram.ui.Cells.TextCell
                android.content.Context r0 = r12.mContext
                r1.<init>(r0)
                goto L_0x0186
            L_0x016a:
                org.telegram.ui.ProfileActivity$ListAdapter$1 r1 = new org.telegram.ui.ProfileActivity$ListAdapter$1
                android.content.Context r0 = r12.mContext
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                r1.<init>(r0, r2)
                goto L_0x0186
            L_0x0174:
                org.telegram.ui.Cells.TextDetailCell r1 = new org.telegram.ui.Cells.TextDetailCell
                android.content.Context r0 = r12.mContext
                r1.<init>(r0)
                r1.setContentDescriptionValueFirst(r2)
                goto L_0x0186
            L_0x017f:
                org.telegram.ui.Cells.HeaderCell r1 = new org.telegram.ui.Cells.HeaderCell
                android.content.Context r2 = r12.mContext
                r1.<init>(r2, r0)
            L_0x0186:
                r0 = 13
                if (r14 == r0) goto L_0x0193
                androidx.recyclerview.widget.RecyclerView$LayoutParams r14 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r0 = -2
                r14.<init>((int) r13, (int) r0)
                r1.setLayoutParams(r14)
            L_0x0193:
                org.telegram.ui.Components.RecyclerListView$Holder r13 = new org.telegram.ui.Components.RecyclerListView$Holder
                r13.<init>(r1)
                return r13
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

        /* JADX WARNING: Removed duplicated region for block: B:114:0x02cb  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r20, int r21) {
            /*
                r19 = this;
                r1 = r19
                r0 = r20
                r2 = r21
                int r3 = r20.getItemViewType()
                r4 = 2131624737(0x7f0e0321, float:1.8876662E38)
                java.lang.String r5 = "ChannelMembers"
                r6 = -1
                r7 = 1
                if (r3 == r7) goto L_0x09a5
                r8 = 2
                r9 = 2131627916(0x7f0e0f8c, float:1.888311E38)
                java.lang.String r10 = "UserBio"
                r11 = 0
                r12 = 0
                if (r3 == r8) goto L_0x0766
                r8 = 3
                if (r3 == r8) goto L_0x0707
                r9 = 4
                if (r3 == r9) goto L_0x02e2
                r4 = 6
                if (r3 == r4) goto L_0x017b
                r4 = 7
                if (r3 == r4) goto L_0x0103
                r4 = 8
                if (r3 == r4) goto L_0x003a
                r2 = 12
                if (r3 == r2) goto L_0x0033
                goto L_0x0a4d
            L_0x0033:
                android.view.View r0 = r0.itemView
                r0.requestLayout()
                goto L_0x0a4d
            L_0x003a:
                android.view.View r0 = r0.itemView
                r13 = r0
                org.telegram.ui.Cells.UserCell r13 = (org.telegram.ui.Cells.UserCell) r13
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this     // Catch:{ Exception -> 0x0085 }
                java.util.ArrayList r0 = r0.visibleSortedUsers     // Catch:{ Exception -> 0x0085 }
                boolean r0 = r0.isEmpty()     // Catch:{ Exception -> 0x0085 }
                if (r0 != 0) goto L_0x0070
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this     // Catch:{ Exception -> 0x0085 }
                java.util.ArrayList r0 = r0.visibleChatParticipants     // Catch:{ Exception -> 0x0085 }
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this     // Catch:{ Exception -> 0x0085 }
                java.util.ArrayList r3 = r3.visibleSortedUsers     // Catch:{ Exception -> 0x0085 }
                org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this     // Catch:{ Exception -> 0x0085 }
                int r4 = r4.membersStartRow     // Catch:{ Exception -> 0x0085 }
                int r4 = r2 - r4
                java.lang.Object r3 = r3.get(r4)     // Catch:{ Exception -> 0x0085 }
                java.lang.Integer r3 = (java.lang.Integer) r3     // Catch:{ Exception -> 0x0085 }
                int r3 = r3.intValue()     // Catch:{ Exception -> 0x0085 }
                java.lang.Object r0 = r0.get(r3)     // Catch:{ Exception -> 0x0085 }
                org.telegram.tgnet.TLRPC$ChatParticipant r0 = (org.telegram.tgnet.TLRPC$ChatParticipant) r0     // Catch:{ Exception -> 0x0085 }
                goto L_0x008a
            L_0x0070:
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this     // Catch:{ Exception -> 0x0085 }
                java.util.ArrayList r0 = r0.visibleChatParticipants     // Catch:{ Exception -> 0x0085 }
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this     // Catch:{ Exception -> 0x0085 }
                int r3 = r3.membersStartRow     // Catch:{ Exception -> 0x0085 }
                int r3 = r2 - r3
                java.lang.Object r0 = r0.get(r3)     // Catch:{ Exception -> 0x0085 }
                org.telegram.tgnet.TLRPC$ChatParticipant r0 = (org.telegram.tgnet.TLRPC$ChatParticipant) r0     // Catch:{ Exception -> 0x0085 }
                goto L_0x008a
            L_0x0085:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                r0 = r11
            L_0x008a:
                if (r0 == 0) goto L_0x0a4d
                boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_chatChannelParticipant
                if (r3 == 0) goto L_0x00bd
                r3 = r0
                org.telegram.tgnet.TLRPC$TL_chatChannelParticipant r3 = (org.telegram.tgnet.TLRPC$TL_chatChannelParticipant) r3
                org.telegram.tgnet.TLRPC$ChannelParticipant r3 = r3.channelParticipant
                java.lang.String r4 = r3.rank
                boolean r4 = android.text.TextUtils.isEmpty(r4)
                if (r4 != 0) goto L_0x00a1
                java.lang.String r3 = r3.rank
            L_0x009f:
                r11 = r3
                goto L_0x00d8
            L_0x00a1:
                boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantCreator
                if (r4 == 0) goto L_0x00af
                r3 = 2131624716(0x7f0e030c, float:1.887662E38)
                java.lang.String r4 = "ChannelCreator"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                goto L_0x009f
            L_0x00af:
                boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin
                if (r3 == 0) goto L_0x00d8
                r3 = 2131624698(0x7f0e02fa, float:1.8876583E38)
                java.lang.String r4 = "ChannelAdmin"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                goto L_0x009f
            L_0x00bd:
                boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantCreator
                if (r3 == 0) goto L_0x00cb
                r3 = 2131624716(0x7f0e030c, float:1.887662E38)
                java.lang.String r4 = "ChannelCreator"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r4, r3)
                goto L_0x00d8
            L_0x00cb:
                boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantAdmin
                if (r3 == 0) goto L_0x00d8
                r3 = 2131624698(0x7f0e02fa, float:1.8876583E38)
                java.lang.String r4 = "ChannelAdmin"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r4, r3)
            L_0x00d8:
                r13.setAdminRole(r11)
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.messenger.MessagesController r3 = r3.getMessagesController()
                int r0 = r0.user_id
                java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
                org.telegram.tgnet.TLRPC$User r14 = r3.getUser(r0)
                r15 = 0
                r16 = 0
                r17 = 0
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                int r0 = r0.membersEndRow
                int r0 = r0 - r7
                if (r2 == r0) goto L_0x00fc
                r18 = 1
                goto L_0x00fe
            L_0x00fc:
                r18 = 0
            L_0x00fe:
                r13.setData(r14, r15, r16, r17, r18)
                goto L_0x0a4d
            L_0x0103:
                android.view.View r0 = r0.itemView
                java.lang.Integer r3 = java.lang.Integer.valueOf(r21)
                r0.setTag(r3)
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.infoSectionRow
                java.lang.String r4 = "windowBackgroundGrayShadow"
                if (r2 != r3) goto L_0x0136
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.lastSectionRow
                if (r3 != r6) goto L_0x0136
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.secretSettingsSectionRow
                if (r3 != r6) goto L_0x0136
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.sharedMediaRow
                if (r3 != r6) goto L_0x0136
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.membersSectionRow
                if (r3 == r6) goto L_0x016d
            L_0x0136:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.secretSettingsSectionRow
                if (r2 == r3) goto L_0x016d
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.lastSectionRow
                if (r2 == r3) goto L_0x016d
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.membersSectionRow
                if (r2 != r3) goto L_0x015f
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                int r2 = r2.lastSectionRow
                if (r2 != r6) goto L_0x015f
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                int r2 = r2.sharedMediaRow
                if (r2 != r6) goto L_0x015f
                goto L_0x016d
            L_0x015f:
                android.content.Context r2 = r1.mContext
                r3 = 2131165441(0x7var_, float:1.79451E38)
                android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r3, (java.lang.String) r4)
                r0.setBackgroundDrawable(r2)
                goto L_0x0a4d
            L_0x016d:
                android.content.Context r2 = r1.mContext
                r3 = 2131165442(0x7var_, float:1.7945101E38)
                android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r3, (java.lang.String) r4)
                r0.setBackgroundDrawable(r2)
                goto L_0x0a4d
            L_0x017b:
                android.view.View r0 = r0.itemView
                org.telegram.ui.Cells.NotificationsCheckCell r0 = (org.telegram.ui.Cells.NotificationsCheckCell) r0
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.notificationsRow
                if (r2 != r3) goto L_0x0a4d
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                int r2 = r2.currentAccount
                android.content.SharedPreferences r2 = org.telegram.messenger.MessagesController.getNotificationsSettings(r2)
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                long r3 = r3.dialog_id
                r5 = 0
                int r9 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
                if (r9 == 0) goto L_0x01a4
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                long r3 = r3.dialog_id
                goto L_0x01bb
            L_0x01a4:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.user_id
                if (r3 == 0) goto L_0x01b3
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.user_id
                goto L_0x01ba
            L_0x01b3:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.chat_id
                int r3 = -r3
            L_0x01ba:
                long r3 = (long) r3
            L_0x01bb:
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                java.lang.String r6 = "custom_"
                r5.append(r6)
                r5.append(r3)
                java.lang.String r5 = r5.toString()
                boolean r5 = r2.getBoolean(r5, r12)
                java.lang.StringBuilder r6 = new java.lang.StringBuilder
                r6.<init>()
                java.lang.String r9 = "notify2_"
                r6.append(r9)
                r6.append(r3)
                java.lang.String r6 = r6.toString()
                boolean r6 = r2.contains(r6)
                java.lang.StringBuilder r10 = new java.lang.StringBuilder
                r10.<init>()
                r10.append(r9)
                r10.append(r3)
                java.lang.String r9 = r10.toString()
                int r9 = r2.getInt(r9, r12)
                java.lang.StringBuilder r10 = new java.lang.StringBuilder
                r10.<init>()
                java.lang.String r13 = "notifyuntil_"
                r10.append(r13)
                r10.append(r3)
                java.lang.String r10 = r10.toString()
                int r2 = r2.getInt(r10, r12)
                if (r9 != r8) goto L_0x0294
                r8 = 2147483647(0x7fffffff, float:NaN)
                if (r2 == r8) goto L_0x0294
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.ConnectionsManager r3 = r3.getConnectionsManager()
                int r3 = r3.getCurrentTime()
                int r2 = r2 - r3
                if (r2 > 0) goto L_0x0239
                if (r5 == 0) goto L_0x022d
                r2 = 2131626501(0x7f0e0a05, float:1.888024E38)
                java.lang.String r3 = "NotificationsCustom"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                goto L_0x0236
            L_0x022d:
                r2 = 2131626529(0x7f0e0a21, float:1.8880297E38)
                java.lang.String r3 = "NotificationsOn"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            L_0x0236:
                r11 = r2
                goto L_0x02c9
            L_0x0239:
                r3 = 3600(0xe10, float:5.045E-42)
                r4 = 2131628296(0x7f0e1108, float:1.888388E38)
                java.lang.String r5 = "WillUnmuteIn"
                if (r2 >= r3) goto L_0x0255
                java.lang.Object[] r3 = new java.lang.Object[r7]
                int r2 = r2 / 60
                java.lang.String r6 = "Minutes"
                java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r6, r2)
                r3[r12] = r2
                java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r5, r4, r3)
            L_0x0252:
                r7 = 0
                goto L_0x02c9
            L_0x0255:
                r3 = 86400(0x15180, float:1.21072E-40)
                r6 = 1114636288(0x42700000, float:60.0)
                if (r2 >= r3) goto L_0x0274
                java.lang.Object[] r3 = new java.lang.Object[r7]
                float r2 = (float) r2
                float r2 = r2 / r6
                float r2 = r2 / r6
                double r6 = (double) r2
                double r6 = java.lang.Math.ceil(r6)
                int r2 = (int) r6
                java.lang.String r6 = "Hours"
                java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r6, r2)
                r3[r12] = r2
                java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r5, r4, r3)
                goto L_0x0252
            L_0x0274:
                r3 = 31536000(0x1e13380, float:8.2725845E-38)
                if (r2 >= r3) goto L_0x0252
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
                r3[r12] = r2
                java.lang.String r11 = org.telegram.messenger.LocaleController.formatString(r5, r4, r3)
                goto L_0x0252
            L_0x0294:
                if (r9 != 0) goto L_0x02a4
                if (r6 == 0) goto L_0x0299
                goto L_0x02a8
            L_0x0299:
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.messenger.NotificationsController r2 = r2.getNotificationsController()
                boolean r7 = r2.isGlobalNotificationsEnabled((long) r3)
                goto L_0x02a8
            L_0x02a4:
                if (r9 != r7) goto L_0x02a7
                goto L_0x02a8
            L_0x02a7:
                r7 = 0
            L_0x02a8:
                if (r7 == 0) goto L_0x02b6
                if (r5 == 0) goto L_0x02b6
                r2 = 2131626501(0x7f0e0a05, float:1.888024E38)
                java.lang.String r3 = "NotificationsCustom"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r3, r2)
                goto L_0x02c9
            L_0x02b6:
                if (r7 == 0) goto L_0x02be
                r2 = 2131626529(0x7f0e0a21, float:1.8880297E38)
                java.lang.String r3 = "NotificationsOn"
                goto L_0x02c3
            L_0x02be:
                r2 = 2131626527(0x7f0e0a1f, float:1.8880293E38)
                java.lang.String r3 = "NotificationsOff"
            L_0x02c3:
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                goto L_0x0236
            L_0x02c9:
                if (r11 != 0) goto L_0x02d4
                r2 = 2131626527(0x7f0e0a1f, float:1.8880293E38)
                java.lang.String r3 = "NotificationsOff"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r3, r2)
            L_0x02d4:
                r2 = 2131626495(0x7f0e09ff, float:1.8880228E38)
                java.lang.String r3 = "Notifications"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setTextAndValueAndCheck(r2, r11, r7, r12)
                goto L_0x0a4d
            L_0x02e2:
                android.view.View r0 = r0.itemView
                org.telegram.ui.Cells.TextCell r0 = (org.telegram.ui.Cells.TextCell) r0
                java.lang.String r3 = "windowBackgroundWhiteGrayIcon"
                java.lang.String r8 = "windowBackgroundWhiteBlackText"
                r0.setColors(r3, r8)
                r0.setTag(r8)
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.settingsTimerRow
                r8 = 32
                if (r2 != r3) goto L_0x0330
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.messenger.MessagesController r2 = r2.getMessagesController()
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                long r3 = r3.dialog_id
                long r3 = r3 >> r8
                int r4 = (int) r3
                java.lang.Integer r3 = java.lang.Integer.valueOf(r4)
                org.telegram.tgnet.TLRPC$EncryptedChat r2 = r2.getEncryptedChat(r3)
                int r2 = r2.ttl
                if (r2 != 0) goto L_0x031e
                r2 = 2131627528(0x7f0e0e08, float:1.8882323E38)
                java.lang.String r3 = "ShortMessageLifetimeForever"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                goto L_0x0322
            L_0x031e:
                java.lang.String r2 = org.telegram.messenger.LocaleController.formatTTLString(r2)
            L_0x0322:
                r3 = 2131626158(0x7f0e08ae, float:1.8879544E38)
                java.lang.String r4 = "MessageLifetime"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                r0.setTextAndValue(r3, r2, r12)
                goto L_0x0a4d
            L_0x0330:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.unblockRow
                java.lang.String r9 = "windowBackgroundWhiteRedText5"
                if (r2 != r3) goto L_0x034b
                r2 = 2131627851(0x7f0e0f4b, float:1.8882978E38)
                java.lang.String r3 = "Unblock"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2, r12)
                r0.setColors(r11, r9)
                goto L_0x0a4d
            L_0x034b:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.settingsKeyRow
                if (r2 != r3) goto L_0x037f
                org.telegram.ui.Components.IdenticonDrawable r2 = new org.telegram.ui.Components.IdenticonDrawable
                r2.<init>()
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.messenger.MessagesController r3 = r3.getMessagesController()
                org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                long r4 = r4.dialog_id
                long r4 = r4 >> r8
                int r5 = (int) r4
                java.lang.Integer r4 = java.lang.Integer.valueOf(r5)
                org.telegram.tgnet.TLRPC$EncryptedChat r3 = r3.getEncryptedChat(r4)
                r2.setEncryptedChat(r3)
                r3 = 2131625323(0x7f0e056b, float:1.887785E38)
                java.lang.String r4 = "EncryptionKey"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                r0.setTextAndValueDrawable(r3, r2, r12)
                goto L_0x0a4d
            L_0x037f:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.joinRow
                if (r2 != r3) goto L_0x03b2
                java.lang.String r2 = "windowBackgroundWhiteBlueText2"
                r0.setColors(r11, r2)
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r2 = r2.currentChat
                boolean r2 = r2.megagroup
                if (r2 == 0) goto L_0x03a4
                r2 = 2131627085(0x7f0e0c4d, float:1.8881424E38)
                java.lang.String r3 = "ProfileJoinGroup"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2, r12)
                goto L_0x0a4d
            L_0x03a4:
                r2 = 2131627084(0x7f0e0c4c, float:1.8881422E38)
                java.lang.String r3 = "ProfileJoinChannel"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2, r12)
                goto L_0x0a4d
            L_0x03b2:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.subscribersRow
                java.lang.String r8 = "%d"
                if (r2 != r3) goto L_0x0475
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r3 = r3.chatInfo
                r6 = 2131165257(0x7var_, float:1.7944726E38)
                if (r3 == 0) goto L_0x0432
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r3 = r3.currentChat
                boolean r3 = org.telegram.messenger.ChatObject.isChannel(r3)
                if (r3 == 0) goto L_0x040a
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r3 = r3.currentChat
                boolean r3 = r3.megagroup
                if (r3 != 0) goto L_0x040a
                r3 = 2131624783(0x7f0e034f, float:1.8876755E38)
                java.lang.String r4 = "ChannelSubscribers"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                java.lang.Object[] r4 = new java.lang.Object[r7]
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r5 = r5.chatInfo
                int r5 = r5.participants_count
                java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
                r4[r12] = r5
                java.lang.String r4 = java.lang.String.format(r8, r4)
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                int r5 = r5.membersSectionRow
                int r5 = r5 - r7
                if (r2 == r5) goto L_0x0404
                goto L_0x0405
            L_0x0404:
                r7 = 0
            L_0x0405:
                r0.setTextAndValueAndIcon(r3, r4, r6, r7)
                goto L_0x0a4d
            L_0x040a:
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r4)
                java.lang.Object[] r4 = new java.lang.Object[r7]
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r5 = r5.chatInfo
                int r5 = r5.participants_count
                java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
                r4[r12] = r5
                java.lang.String r4 = java.lang.String.format(r8, r4)
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                int r5 = r5.membersSectionRow
                int r5 = r5 - r7
                if (r2 == r5) goto L_0x042c
                goto L_0x042d
            L_0x042c:
                r7 = 0
            L_0x042d:
                r0.setTextAndValueAndIcon(r3, r4, r6, r7)
                goto L_0x0a4d
            L_0x0432:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r3 = r3.currentChat
                boolean r3 = org.telegram.messenger.ChatObject.isChannel(r3)
                if (r3 == 0) goto L_0x0461
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r3 = r3.currentChat
                boolean r3 = r3.megagroup
                if (r3 != 0) goto L_0x0461
                r3 = 2131624783(0x7f0e034f, float:1.8876755E38)
                java.lang.String r4 = "ChannelSubscribers"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                int r4 = r4.membersSectionRow
                int r4 = r4 - r7
                if (r2 == r4) goto L_0x045b
                goto L_0x045c
            L_0x045b:
                r7 = 0
            L_0x045c:
                r0.setTextAndIcon((java.lang.String) r3, (int) r6, (boolean) r7)
                goto L_0x0a4d
            L_0x0461:
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r4)
                org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                int r4 = r4.membersSectionRow
                int r4 = r4 - r7
                if (r2 == r4) goto L_0x046f
                goto L_0x0470
            L_0x046f:
                r7 = 0
            L_0x0470:
                r0.setTextAndIcon((java.lang.String) r3, (int) r6, (boolean) r7)
                goto L_0x0a4d
            L_0x0475:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.administratorsRow
                if (r2 != r3) goto L_0x04d1
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r3 = r3.chatInfo
                if (r3 == 0) goto L_0x04b5
                r3 = 2131624700(0x7f0e02fc, float:1.8876587E38)
                java.lang.String r4 = "ChannelAdministrators"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                java.lang.Object[] r4 = new java.lang.Object[r7]
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r5 = r5.chatInfo
                int r5 = r5.admins_count
                java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
                r4[r12] = r5
                java.lang.String r4 = java.lang.String.format(r8, r4)
                r5 = 2131165247(0x7var_f, float:1.7944706E38)
                org.telegram.ui.ProfileActivity r6 = org.telegram.ui.ProfileActivity.this
                int r6 = r6.membersSectionRow
                int r6 = r6 - r7
                if (r2 == r6) goto L_0x04af
                goto L_0x04b0
            L_0x04af:
                r7 = 0
            L_0x04b0:
                r0.setTextAndValueAndIcon(r3, r4, r5, r7)
                goto L_0x0a4d
            L_0x04b5:
                r3 = 2131624700(0x7f0e02fc, float:1.8876587E38)
                java.lang.String r4 = "ChannelAdministrators"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                r4 = 2131165247(0x7var_f, float:1.7944706E38)
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                int r5 = r5.membersSectionRow
                int r5 = r5 - r7
                if (r2 == r5) goto L_0x04cb
                goto L_0x04cc
            L_0x04cb:
                r7 = 0
            L_0x04cc:
                r0.setTextAndIcon((java.lang.String) r3, (int) r4, (boolean) r7)
                goto L_0x0a4d
            L_0x04d1:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.blockedUsersRow
                if (r2 != r3) goto L_0x0539
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r3 = r3.chatInfo
                if (r3 == 0) goto L_0x051d
                r3 = 2131624705(0x7f0e0301, float:1.8876597E38)
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
                r4[r12] = r5
                java.lang.String r4 = java.lang.String.format(r8, r4)
                r5 = 2131165255(0x7var_, float:1.7944722E38)
                org.telegram.ui.ProfileActivity r6 = org.telegram.ui.ProfileActivity.this
                int r6 = r6.membersSectionRow
                int r6 = r6 - r7
                if (r2 == r6) goto L_0x0517
                goto L_0x0518
            L_0x0517:
                r7 = 0
            L_0x0518:
                r0.setTextAndValueAndIcon(r3, r4, r5, r7)
                goto L_0x0a4d
            L_0x051d:
                r3 = 2131624705(0x7f0e0301, float:1.8876597E38)
                java.lang.String r4 = "ChannelBlacklist"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                r4 = 2131165255(0x7var_, float:1.7944722E38)
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                int r5 = r5.membersSectionRow
                int r5 = r5 - r7
                if (r2 == r5) goto L_0x0533
                goto L_0x0534
            L_0x0533:
                r7 = 0
            L_0x0534:
                r0.setTextAndIcon((java.lang.String) r3, (int) r4, (boolean) r7)
                goto L_0x0a4d
            L_0x0539:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.addMemberRow
                if (r2 != r3) goto L_0x0563
                java.lang.String r2 = "windowBackgroundWhiteBlueIcon"
                java.lang.String r3 = "windowBackgroundWhiteBlueButton"
                r0.setColors(r2, r3)
                r2 = 2131624203(0x7f0e010b, float:1.887558E38)
                java.lang.String r3 = "AddMember"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r3 = 2131165249(0x7var_, float:1.794471E38)
                org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                int r4 = r4.membersSectionRow
                if (r4 != r6) goto L_0x055d
                goto L_0x055e
            L_0x055d:
                r7 = 0
            L_0x055e:
                r0.setTextAndIcon((java.lang.String) r2, (int) r3, (boolean) r7)
                goto L_0x0a4d
            L_0x0563:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.sendMessageRow
                if (r2 != r3) goto L_0x0579
                r2 = 2131627408(0x7f0e0d90, float:1.888208E38)
                java.lang.String r3 = "SendMessageLocation"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2, r7)
                goto L_0x0a4d
            L_0x0579:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.reportRow
                if (r2 != r3) goto L_0x0592
                r2 = 2131627231(0x7f0e0cdf, float:1.888172E38)
                java.lang.String r3 = "ReportUserLocation"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2, r12)
                r0.setColors(r11, r9)
                goto L_0x0a4d
            L_0x0592:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.languageRow
                if (r2 != r3) goto L_0x05ab
                r2 = 2131625936(0x7f0e07d0, float:1.8879094E38)
                java.lang.String r3 = "Language"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r3 = 2131165657(0x7var_d9, float:1.7945537E38)
                r0.setTextAndIcon((java.lang.String) r2, (int) r3, (boolean) r12)
                goto L_0x0a4d
            L_0x05ab:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.notificationRow
                if (r2 != r3) goto L_0x05c4
                r2 = 2131626497(0x7f0e0a01, float:1.8880232E38)
                java.lang.String r3 = "NotificationsAndSounds"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r3 = 2131165665(0x7var_e1, float:1.7945554E38)
                r0.setTextAndIcon((java.lang.String) r2, (int) r3, (boolean) r7)
                goto L_0x0a4d
            L_0x05c4:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.privacyRow
                if (r2 != r3) goto L_0x05dd
                r2 = 2131627080(0x7f0e0CLASSNAME, float:1.8881414E38)
                java.lang.String r3 = "PrivacySettings"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r3 = 2131165675(0x7var_eb, float:1.7945574E38)
                r0.setTextAndIcon((java.lang.String) r2, (int) r3, (boolean) r7)
                goto L_0x0a4d
            L_0x05dd:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.dataRow
                if (r2 != r3) goto L_0x05f6
                r2 = 2131625056(0x7f0e0460, float:1.887731E38)
                java.lang.String r3 = "DataSettings"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r3 = 2131165640(0x7var_c8, float:1.7945503E38)
                r0.setTextAndIcon((java.lang.String) r2, (int) r3, (boolean) r7)
                goto L_0x0a4d
            L_0x05f6:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.chatRow
                if (r2 != r3) goto L_0x060f
                r2 = 2131624842(0x7f0e038a, float:1.8876875E38)
                java.lang.String r3 = "ChatSettings"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r3 = 2131165632(0x7var_c0, float:1.7945487E38)
                r0.setTextAndIcon((java.lang.String) r2, (int) r3, (boolean) r7)
                goto L_0x0a4d
            L_0x060f:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.filtersRow
                if (r2 != r3) goto L_0x0628
                r2 = 2131625568(0x7f0e0660, float:1.8878348E38)
                java.lang.String r3 = "Filters"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r3 = 2131165644(0x7var_cc, float:1.794551E38)
                r0.setTextAndIcon((java.lang.String) r2, (int) r3, (boolean) r7)
                goto L_0x0a4d
            L_0x0628:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.questionRow
                if (r2 != r3) goto L_0x0641
                r2 = 2131624376(0x7f0e01b8, float:1.887593E38)
                java.lang.String r3 = "AskAQuestion"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r3 = 2131165687(0x7var_f7, float:1.7945598E38)
                r0.setTextAndIcon((java.lang.String) r2, (int) r3, (boolean) r7)
                goto L_0x0a4d
            L_0x0641:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.faqRow
                if (r2 != r3) goto L_0x065a
                r2 = 2131627711(0x7f0e0ebf, float:1.8882694E38)
                java.lang.String r3 = "TelegramFAQ"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r3 = 2131165649(0x7var_d1, float:1.7945521E38)
                r0.setTextAndIcon((java.lang.String) r2, (int) r3, (boolean) r7)
                goto L_0x0a4d
            L_0x065a:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.policyRow
                if (r2 != r3) goto L_0x0673
                r2 = 2131627073(0x7f0e0CLASSNAME, float:1.88814E38)
                java.lang.String r3 = "PrivacyPolicy"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r3 = 2131165669(0x7var_e5, float:1.7945562E38)
                r0.setTextAndIcon((java.lang.String) r2, (int) r3, (boolean) r12)
                goto L_0x0a4d
            L_0x0673:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.sendLogsRow
                if (r2 != r3) goto L_0x0689
                r2 = 2131625101(0x7f0e048d, float:1.88774E38)
                java.lang.String r3 = "DebugSendLogs"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2, r7)
                goto L_0x0a4d
            L_0x0689:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.sendLastLogsRow
                if (r2 != r3) goto L_0x069f
                r2 = 2131625100(0x7f0e048c, float:1.8877398E38)
                java.lang.String r3 = "DebugSendLastLogs"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2, r7)
                goto L_0x0a4d
            L_0x069f:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.clearLogsRow
                if (r2 != r3) goto L_0x06bf
                r2 = 2131625083(0x7f0e047b, float:1.8877364E38)
                java.lang.String r3 = "DebugClearLogs"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.switchBackendRow
                if (r3 == r6) goto L_0x06b9
                goto L_0x06ba
            L_0x06b9:
                r7 = 0
            L_0x06ba:
                r0.setText(r2, r7)
                goto L_0x0a4d
            L_0x06bf:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.switchBackendRow
                if (r2 != r3) goto L_0x06ce
                java.lang.String r2 = "Switch Backend"
                r0.setText(r2, r12)
                goto L_0x0a4d
            L_0x06ce:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.devicesRow
                if (r2 != r3) goto L_0x06e7
                r2 = 2131625183(0x7f0e04df, float:1.8877567E38)
                java.lang.String r3 = "Devices"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r3 = 2131165642(0x7var_ca, float:1.7945507E38)
                r0.setTextAndIcon((java.lang.String) r2, (int) r3, (boolean) r7)
                goto L_0x0a4d
            L_0x06e7:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.setAvatarRow
                if (r2 != r3) goto L_0x0a4d
                java.lang.String r2 = "windowBackgroundWhiteBlueIcon"
                java.lang.String r3 = "windowBackgroundWhiteBlueButton"
                r0.setColors(r2, r3)
                r2 = 2131627450(0x7f0e0dba, float:1.8882165E38)
                java.lang.String r3 = "SetProfilePhoto"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r3 = 2131165706(0x7var_a, float:1.7945637E38)
                r0.setTextAndIcon((java.lang.String) r2, (int) r3, (boolean) r12)
                goto L_0x0a4d
            L_0x0707:
                android.view.View r0 = r0.itemView
                org.telegram.ui.Cells.AboutLinkCell r0 = (org.telegram.ui.Cells.AboutLinkCell) r0
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.userInfoRow
                if (r2 != r3) goto L_0x072a
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$UserFull r2 = r2.userInfo
                java.lang.String r2 = r2.about
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r10, r9)
                org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                boolean r4 = r4.isBot
                r0.setTextAndValue(r2, r3, r4)
                goto L_0x0a4d
            L_0x072a:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.channelInfoRow
                if (r2 != r3) goto L_0x0a4d
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r2 = r2.chatInfo
                java.lang.String r2 = r2.about
            L_0x073a:
                java.lang.String r3 = "\n\n\n"
                boolean r4 = r2.contains(r3)
                if (r4 == 0) goto L_0x0749
                java.lang.String r4 = "\n\n"
                java.lang.String r2 = r2.replace(r3, r4)
                goto L_0x073a
            L_0x0749:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r3 = r3.currentChat
                boolean r3 = org.telegram.messenger.ChatObject.isChannel(r3)
                if (r3 == 0) goto L_0x0760
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r3 = r3.currentChat
                boolean r3 = r3.megagroup
                if (r3 != 0) goto L_0x0760
                goto L_0x0761
            L_0x0760:
                r7 = 0
            L_0x0761:
                r0.setText(r2, r7)
                goto L_0x0a4d
            L_0x0766:
                android.view.View r0 = r0.itemView
                org.telegram.ui.Cells.TextDetailCell r0 = (org.telegram.ui.Cells.TextDetailCell) r0
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.phoneRow
                java.lang.String r4 = "+"
                if (r2 != r3) goto L_0x07c1
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.messenger.MessagesController r2 = r2.getMessagesController()
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.user_id
                java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
                org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r3)
                java.lang.String r3 = r2.phone
                boolean r3 = android.text.TextUtils.isEmpty(r3)
                if (r3 != 0) goto L_0x07aa
                org.telegram.PhoneFormat.PhoneFormat r3 = org.telegram.PhoneFormat.PhoneFormat.getInstance()
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                r5.append(r4)
                java.lang.String r2 = r2.phone
                r5.append(r2)
                java.lang.String r2 = r5.toString()
                java.lang.String r2 = r3.format(r2)
                goto L_0x07b3
            L_0x07aa:
                r2 = 2131626936(0x7f0e0bb8, float:1.8881122E38)
                java.lang.String r3 = "PhoneHidden"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            L_0x07b3:
                r3 = 2131626939(0x7f0e0bbb, float:1.8881128E38)
                java.lang.String r4 = "PhoneMobile"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                r0.setTextAndValue(r2, r3, r12)
                goto L_0x0a4d
            L_0x07c1:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.usernameRow
                java.lang.String r5 = "Username"
                if (r2 != r3) goto L_0x085b
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                int r2 = r2.user_id
                if (r2 == 0) goto L_0x0813
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.messenger.MessagesController r2 = r2.getMessagesController()
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.user_id
                java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
                org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r3)
                if (r2 == 0) goto L_0x0805
                java.lang.String r3 = r2.username
                boolean r3 = android.text.TextUtils.isEmpty(r3)
                if (r3 != 0) goto L_0x0805
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r4 = "@"
                r3.append(r4)
                java.lang.String r2 = r2.username
                r3.append(r2)
                java.lang.String r2 = r3.toString()
                goto L_0x0807
            L_0x0805:
                java.lang.String r2 = "-"
            L_0x0807:
                r3 = 2131627954(0x7f0e0fb2, float:1.8883187E38)
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
                r0.setTextAndValue(r2, r3, r12)
                goto L_0x0a4d
            L_0x0813:
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r2 = r2.currentChat
                if (r2 == 0) goto L_0x0a4d
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.messenger.MessagesController r2 = r2.getMessagesController()
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.chat_id
                java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
                org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r3)
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                org.telegram.messenger.MessagesController r4 = r4.getMessagesController()
                java.lang.String r4 = r4.linkPrefix
                r3.append(r4)
                java.lang.String r4 = "/"
                r3.append(r4)
                java.lang.String r2 = r2.username
                r3.append(r2)
                java.lang.String r2 = r3.toString()
                r3 = 2131625881(0x7f0e0799, float:1.8878982E38)
                java.lang.String r4 = "InviteLink"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                r0.setTextAndValue(r2, r3, r12)
                goto L_0x0a4d
            L_0x085b:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.locationRow
                if (r2 != r3) goto L_0x0891
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r2 = r2.chatInfo
                if (r2 == 0) goto L_0x0a4d
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r2 = r2.chatInfo
                org.telegram.tgnet.TLRPC$ChannelLocation r2 = r2.location
                boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelLocation
                if (r2 == 0) goto L_0x0a4d
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r2 = r2.chatInfo
                org.telegram.tgnet.TLRPC$ChannelLocation r2 = r2.location
                org.telegram.tgnet.TLRPC$TL_channelLocation r2 = (org.telegram.tgnet.TLRPC$TL_channelLocation) r2
                java.lang.String r2 = r2.address
                r3 = 2131624395(0x7f0e01cb, float:1.8875969E38)
                java.lang.String r4 = "AttachLocation"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                r0.setTextAndValue(r2, r3, r12)
                goto L_0x0a4d
            L_0x0891:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.numberRow
                if (r2 != r3) goto L_0x08e7
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                int r2 = r2.currentAccount
                org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
                org.telegram.tgnet.TLRPC$User r2 = r2.getCurrentUser()
                if (r2 == 0) goto L_0x08cd
                java.lang.String r3 = r2.phone
                if (r3 == 0) goto L_0x08cd
                int r3 = r3.length()
                if (r3 == 0) goto L_0x08cd
                org.telegram.PhoneFormat.PhoneFormat r3 = org.telegram.PhoneFormat.PhoneFormat.getInstance()
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                r5.append(r4)
                java.lang.String r2 = r2.phone
                r5.append(r2)
                java.lang.String r2 = r5.toString()
                java.lang.String r2 = r3.format(r2)
                goto L_0x08d6
            L_0x08cd:
                r2 = 2131626548(0x7f0e0a34, float:1.8880335E38)
                java.lang.String r3 = "NumberUnknown"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            L_0x08d6:
                r3 = 2131627696(0x7f0e0eb0, float:1.8882664E38)
                java.lang.String r4 = "TapToChangePhone"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                r0.setTextAndValue(r2, r3, r7)
                r0.setContentDescriptionValueFirst(r12)
                goto L_0x0a4d
            L_0x08e7:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.setUsernameRow
                if (r2 != r3) goto L_0x0933
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                int r2 = r2.currentAccount
                org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
                org.telegram.tgnet.TLRPC$User r2 = r2.getCurrentUser()
                if (r2 == 0) goto L_0x091b
                java.lang.String r3 = r2.username
                boolean r3 = android.text.TextUtils.isEmpty(r3)
                if (r3 != 0) goto L_0x091b
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r4 = "@"
                r3.append(r4)
                java.lang.String r2 = r2.username
                r3.append(r2)
                java.lang.String r2 = r3.toString()
                goto L_0x0924
            L_0x091b:
                r2 = 2131627958(0x7f0e0fb6, float:1.8883195E38)
                java.lang.String r3 = "UsernameEmpty"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            L_0x0924:
                r3 = 2131627954(0x7f0e0fb2, float:1.8883187E38)
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
                r0.setTextAndValue(r2, r3, r7)
                r0.setContentDescriptionValueFirst(r7)
                goto L_0x0a4d
            L_0x0933:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.bioRow
                if (r2 != r3) goto L_0x0a4d
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$UserFull r2 = r2.userInfo
                if (r2 == 0) goto L_0x096c
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$UserFull r2 = r2.userInfo
                java.lang.String r2 = r2.about
                boolean r2 = android.text.TextUtils.isEmpty(r2)
                if (r2 != 0) goto L_0x0952
                goto L_0x096c
            L_0x0952:
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r10, r9)
                r3 = 2131627917(0x7f0e0f8d, float:1.8883112E38)
                java.lang.String r4 = "UserBioDetail"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                r0.setTextAndValue(r2, r3, r12)
                r0.setContentDescriptionValueFirst(r12)
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                java.lang.String unused = r0.currentBio = r11
                goto L_0x0a4d
            L_0x096c:
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$UserFull r2 = r2.userInfo
                if (r2 != 0) goto L_0x097e
                r2 = 2131626018(0x7f0e0822, float:1.887926E38)
                java.lang.String r3 = "Loading"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                goto L_0x0986
            L_0x097e:
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$UserFull r2 = r2.userInfo
                java.lang.String r2 = r2.about
            L_0x0986:
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r10, r9)
                r0.setTextWithEmojiAndValue(r2, r3, r12)
                r0.setContentDescriptionValueFirst(r7)
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$UserFull r2 = r0.userInfo
                if (r2 == 0) goto L_0x09a0
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$UserFull r2 = r2.userInfo
                java.lang.String r11 = r2.about
            L_0x09a0:
                java.lang.String unused = r0.currentBio = r11
                goto L_0x0a4d
            L_0x09a5:
                android.view.View r0 = r0.itemView
                org.telegram.ui.Cells.HeaderCell r0 = (org.telegram.ui.Cells.HeaderCell) r0
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.infoHeaderRow
                if (r2 != r3) goto L_0x09ea
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r2 = r2.currentChat
                boolean r2 = org.telegram.messenger.ChatObject.isChannel(r2)
                if (r2 == 0) goto L_0x09dd
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r2 = r2.currentChat
                boolean r2 = r2.megagroup
                if (r2 != 0) goto L_0x09dd
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                int r2 = r2.channelInfoRow
                if (r2 == r6) goto L_0x09dd
                r2 = 2131627201(0x7f0e0cc1, float:1.888166E38)
                java.lang.String r3 = "ReportChatDescription"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2)
                goto L_0x0a4d
            L_0x09dd:
                r2 = 2131625853(0x7f0e077d, float:1.8878926E38)
                java.lang.String r3 = "Info"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2)
                goto L_0x0a4d
            L_0x09ea:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.membersHeaderRow
                if (r2 != r3) goto L_0x09fa
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r0.setText(r2)
                goto L_0x0a4d
            L_0x09fa:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.settingsSectionRow2
                if (r2 != r3) goto L_0x0a0f
                r2 = 2131627287(0x7f0e0d17, float:1.8881834E38)
                java.lang.String r3 = "SETTINGS"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2)
                goto L_0x0a4d
            L_0x0a0f:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.numberSectionRow
                if (r2 != r3) goto L_0x0a24
                r2 = 2131624080(0x7f0e0090, float:1.887533E38)
                java.lang.String r3 = "Account"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2)
                goto L_0x0a4d
            L_0x0a24:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.helpHeaderRow
                if (r2 != r3) goto L_0x0a39
                r2 = 2131627466(0x7f0e0dca, float:1.8882197E38)
                java.lang.String r3 = "SettingsHelp"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2)
                goto L_0x0a4d
            L_0x0a39:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.debugHeaderRow
                if (r2 != r3) goto L_0x0a4d
                r2 = 2131627464(0x7f0e0dc8, float:1.8882193E38)
                java.lang.String r3 = "SettingsDebug"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r0.setText(r2)
            L_0x0a4d:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            if (ProfileActivity.this.notificationRow != -1) {
                int adapterPosition = viewHolder.getAdapterPosition();
                if (adapterPosition == ProfileActivity.this.notificationRow || adapterPosition == ProfileActivity.this.numberRow || adapterPosition == ProfileActivity.this.privacyRow || adapterPosition == ProfileActivity.this.languageRow || adapterPosition == ProfileActivity.this.setUsernameRow || adapterPosition == ProfileActivity.this.bioRow || adapterPosition == ProfileActivity.this.versionRow || adapterPosition == ProfileActivity.this.dataRow || adapterPosition == ProfileActivity.this.chatRow || adapterPosition == ProfileActivity.this.questionRow || adapterPosition == ProfileActivity.this.devicesRow || adapterPosition == ProfileActivity.this.filtersRow || adapterPosition == ProfileActivity.this.faqRow || adapterPosition == ProfileActivity.this.policyRow || adapterPosition == ProfileActivity.this.sendLogsRow || adapterPosition == ProfileActivity.this.sendLastLogsRow || adapterPosition == ProfileActivity.this.clearLogsRow || adapterPosition == ProfileActivity.this.switchBackendRow || adapterPosition == ProfileActivity.this.setAvatarRow) {
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
            if (itemViewType == 1 || itemViewType == 5 || itemViewType == 7 || itemViewType == 9 || itemViewType == 10 || itemViewType == 11 || itemViewType == 12 || itemViewType == 13) {
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
            if (i == ProfileActivity.this.settingsTimerRow || i == ProfileActivity.this.settingsKeyRow || i == ProfileActivity.this.reportRow || i == ProfileActivity.this.subscribersRow || i == ProfileActivity.this.administratorsRow || i == ProfileActivity.this.blockedUsersRow || i == ProfileActivity.this.addMemberRow || i == ProfileActivity.this.joinRow || i == ProfileActivity.this.unblockRow || i == ProfileActivity.this.sendMessageRow || i == ProfileActivity.this.notificationRow || i == ProfileActivity.this.privacyRow || i == ProfileActivity.this.languageRow || i == ProfileActivity.this.dataRow || i == ProfileActivity.this.chatRow || i == ProfileActivity.this.questionRow || i == ProfileActivity.this.devicesRow || i == ProfileActivity.this.filtersRow || i == ProfileActivity.this.faqRow || i == ProfileActivity.this.policyRow || i == ProfileActivity.this.sendLogsRow || i == ProfileActivity.this.sendLastLogsRow || i == ProfileActivity.this.clearLogsRow || i == ProfileActivity.this.switchBackendRow || i == ProfileActivity.this.setAvatarRow) {
                return 4;
            }
            if (i == ProfileActivity.this.notificationsDividerRow) {
                return 5;
            }
            if (i == ProfileActivity.this.notificationsRow) {
                return 6;
            }
            if (i == ProfileActivity.this.infoSectionRow || i == ProfileActivity.this.lastSectionRow || i == ProfileActivity.this.membersSectionRow || i == ProfileActivity.this.secretSettingsSectionRow || i == ProfileActivity.this.settingsSectionRow || i == ProfileActivity.this.devicesSectionRow || i == ProfileActivity.this.helpSectionCell || i == ProfileActivity.this.setAvatarSectionRow) {
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
            return i == ProfileActivity.this.versionRow ? 14 : 0;
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
        /* renamed from: lambda$new$0 */
        public /* synthetic */ void lambda$new$0$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new ChangeNameActivity());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$1 */
        public /* synthetic */ void lambda$new$1$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new ActionIntroActivity(3));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$2 */
        public /* synthetic */ void lambda$new$2$ProfileActivity$SearchAdapter() {
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
        /* renamed from: lambda$new$3 */
        public /* synthetic */ void lambda$new$3$ProfileActivity$SearchAdapter() {
            if (this.this$0.userInfo != null) {
                this.this$0.presentFragment(new ChangeBioActivity());
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$4 */
        public /* synthetic */ void lambda$new$4$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new NotificationsSettingsActivity());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$5 */
        public /* synthetic */ void lambda$new$5$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new NotificationsCustomSettingsActivity(1, new ArrayList(), true));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$6 */
        public /* synthetic */ void lambda$new$6$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new NotificationsCustomSettingsActivity(0, new ArrayList(), true));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$7 */
        public /* synthetic */ void lambda$new$7$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new NotificationsCustomSettingsActivity(2, new ArrayList(), true));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$8 */
        public /* synthetic */ void lambda$new$8$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new NotificationsSettingsActivity());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$9 */
        public /* synthetic */ void lambda$new$9$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new NotificationsSettingsActivity());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$10 */
        public /* synthetic */ void lambda$new$10$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new NotificationsSettingsActivity());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$11 */
        public /* synthetic */ void lambda$new$11$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new NotificationsSettingsActivity());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$12 */
        public /* synthetic */ void lambda$new$12$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new NotificationsSettingsActivity());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$13 */
        public /* synthetic */ void lambda$new$13$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new NotificationsSettingsActivity());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$14 */
        public /* synthetic */ void lambda$new$14$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacySettingsActivity());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$15 */
        public /* synthetic */ void lambda$new$15$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacyUsersActivity());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$16 */
        public /* synthetic */ void lambda$new$16$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacyControlActivity(6, true));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$17 */
        public /* synthetic */ void lambda$new$17$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacyControlActivity(0, true));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$18 */
        public /* synthetic */ void lambda$new$18$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacyControlActivity(4, true));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$19 */
        public /* synthetic */ void lambda$new$19$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacyControlActivity(5, true));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$20 */
        public /* synthetic */ void lambda$new$20$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacyControlActivity(3, true));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$21 */
        public /* synthetic */ void lambda$new$21$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacyControlActivity(2, true));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$22 */
        public /* synthetic */ void lambda$new$22$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacyControlActivity(1, true));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$23 */
        public /* synthetic */ void lambda$new$23$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new PasscodeActivity(SharedConfig.passcodeHash.length() > 0 ? 2 : 0));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$24 */
        public /* synthetic */ void lambda$new$24$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new TwoStepVerificationActivity());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$25 */
        public /* synthetic */ void lambda$new$25$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new SessionsActivity(0));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$26 */
        public /* synthetic */ void lambda$new$26$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacySettingsActivity());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$27 */
        public /* synthetic */ void lambda$new$27$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacySettingsActivity());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$28 */
        public /* synthetic */ void lambda$new$28$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacySettingsActivity());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$29 */
        public /* synthetic */ void lambda$new$29$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new SessionsActivity(1));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$30 */
        public /* synthetic */ void lambda$new$30$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacySettingsActivity());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$31 */
        public /* synthetic */ void lambda$new$31$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacySettingsActivity());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$32 */
        public /* synthetic */ void lambda$new$32$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacySettingsActivity());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$33 */
        public /* synthetic */ void lambda$new$33$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacySettingsActivity());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$34 */
        public /* synthetic */ void lambda$new$34$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacySettingsActivity());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$35 */
        public /* synthetic */ void lambda$new$35$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new SessionsActivity(0));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$36 */
        public /* synthetic */ void lambda$new$36$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$37 */
        public /* synthetic */ void lambda$new$37$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$38 */
        public /* synthetic */ void lambda$new$38$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new CacheControlActivity());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$39 */
        public /* synthetic */ void lambda$new$39$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new CacheControlActivity());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$40 */
        public /* synthetic */ void lambda$new$40$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new CacheControlActivity());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$41 */
        public /* synthetic */ void lambda$new$41$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new CacheControlActivity());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$42 */
        public /* synthetic */ void lambda$new$42$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataUsageActivity());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$43 */
        public /* synthetic */ void lambda$new$43$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$44 */
        public /* synthetic */ void lambda$new$44$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataAutoDownloadActivity(0));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$45 */
        public /* synthetic */ void lambda$new$45$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataAutoDownloadActivity(1));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$46 */
        public /* synthetic */ void lambda$new$46$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataAutoDownloadActivity(2));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$47 */
        public /* synthetic */ void lambda$new$47$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$48 */
        public /* synthetic */ void lambda$new$48$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$49 */
        public /* synthetic */ void lambda$new$49$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$50 */
        public /* synthetic */ void lambda$new$50$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$51 */
        public /* synthetic */ void lambda$new$51$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$52 */
        public /* synthetic */ void lambda$new$52$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$53 */
        public /* synthetic */ void lambda$new$53$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$54 */
        public /* synthetic */ void lambda$new$54$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$55 */
        public /* synthetic */ void lambda$new$55$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$56 */
        public /* synthetic */ void lambda$new$56$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new ProxyListActivity());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$57 */
        public /* synthetic */ void lambda$new$57$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new ProxyListActivity());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$58 */
        public /* synthetic */ void lambda$new$58$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$59 */
        public /* synthetic */ void lambda$new$59$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$60 */
        public /* synthetic */ void lambda$new$60$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$61 */
        public /* synthetic */ void lambda$new$61$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new WallpapersListActivity(0));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$62 */
        public /* synthetic */ void lambda$new$62$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new WallpapersListActivity(1));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$63 */
        public /* synthetic */ void lambda$new$63$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new WallpapersListActivity(0));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$64 */
        public /* synthetic */ void lambda$new$64$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new ThemeActivity(1));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$65 */
        public /* synthetic */ void lambda$new$65$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$66 */
        public /* synthetic */ void lambda$new$66$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$67 */
        public /* synthetic */ void lambda$new$67$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$68 */
        public /* synthetic */ void lambda$new$68$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$69 */
        public /* synthetic */ void lambda$new$69$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$70 */
        public /* synthetic */ void lambda$new$70$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$71 */
        public /* synthetic */ void lambda$new$71$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$72 */
        public /* synthetic */ void lambda$new$72$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$73 */
        public /* synthetic */ void lambda$new$73$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new StickersActivity(0));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$74 */
        public /* synthetic */ void lambda$new$74$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new StickersActivity(0));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$75 */
        public /* synthetic */ void lambda$new$75$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new FeaturedStickersActivity());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$76 */
        public /* synthetic */ void lambda$new$76$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new StickersActivity(1));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$77 */
        public /* synthetic */ void lambda$new$77$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new ArchivedStickersActivity(0));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$78 */
        public /* synthetic */ void lambda$new$78$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new ArchivedStickersActivity(1));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$79 */
        public /* synthetic */ void lambda$new$79$ProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new LanguageSelectActivity());
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$80 */
        public /* synthetic */ void lambda$new$80$ProfileActivity$SearchAdapter() {
            ProfileActivity profileActivity = this.this$0;
            profileActivity.showDialog(AlertsCreator.createSupportAlert(profileActivity));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$81 */
        public /* synthetic */ void lambda$new$81$ProfileActivity$SearchAdapter() {
            Browser.openUrl((Context) this.this$0.getParentActivity(), LocaleController.getString("TelegramFaqUrl", NUM));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$82 */
        public /* synthetic */ void lambda$new$82$ProfileActivity$SearchAdapter() {
            Browser.openUrl((Context) this.this$0.getParentActivity(), LocaleController.getString("PrivacyPolicyUrl", NUM));
        }

        public SearchAdapter(ProfileActivity profileActivity, Context context) {
            SearchResult searchResult;
            String[] strArr;
            this.this$0 = profileActivity;
            SearchResult[] searchResultArr = new SearchResult[83];
            searchResultArr[0] = new SearchResult(this, 500, LocaleController.getString("EditName", NUM), 0, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$0$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[1] = new SearchResult(this, 501, LocaleController.getString("ChangePhoneNumber", NUM), 0, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$1$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[2] = new SearchResult(this, 502, LocaleController.getString("AddAnotherAccount", NUM), 0, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$2$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[3] = new SearchResult(this, 503, LocaleController.getString("UserBio", NUM), 0, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$3$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[4] = new SearchResult(this, 1, LocaleController.getString("NotificationsAndSounds", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$4$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[5] = new SearchResult(this, 2, LocaleController.getString("NotificationsPrivateChats", NUM), LocaleController.getString("NotificationsAndSounds", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$5$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[6] = new SearchResult(this, 3, LocaleController.getString("NotificationsGroups", NUM), LocaleController.getString("NotificationsAndSounds", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$6$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[7] = new SearchResult(this, 4, LocaleController.getString("NotificationsChannels", NUM), LocaleController.getString("NotificationsAndSounds", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$7$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[8] = new SearchResult(this, 5, LocaleController.getString("VoipNotificationSettings", NUM), "callsSectionRow", LocaleController.getString("NotificationsAndSounds", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$8$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[9] = new SearchResult(this, 6, LocaleController.getString("BadgeNumber", NUM), "badgeNumberSection", LocaleController.getString("NotificationsAndSounds", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$9$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[10] = new SearchResult(this, 7, LocaleController.getString("InAppNotifications", NUM), "inappSectionRow", LocaleController.getString("NotificationsAndSounds", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$10$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[11] = new SearchResult(this, 8, LocaleController.getString("ContactJoined", NUM), "contactJoinedRow", LocaleController.getString("NotificationsAndSounds", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$11$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[12] = new SearchResult(this, 9, LocaleController.getString("PinnedMessages", NUM), "pinnedMessageRow", LocaleController.getString("NotificationsAndSounds", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$12$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[13] = new SearchResult(this, 10, LocaleController.getString("ResetAllNotifications", NUM), "resetNotificationsRow", LocaleController.getString("NotificationsAndSounds", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$13$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[14] = new SearchResult(this, 100, LocaleController.getString("PrivacySettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$14$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[15] = new SearchResult(this, 101, LocaleController.getString("BlockedUsers", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$15$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[16] = new SearchResult(this, 105, LocaleController.getString("PrivacyPhone", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$16$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[17] = new SearchResult(this, 102, LocaleController.getString("PrivacyLastSeen", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$17$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[18] = new SearchResult(this, 103, LocaleController.getString("PrivacyProfilePhoto", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$18$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[19] = new SearchResult(this, 104, LocaleController.getString("PrivacyForwards", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$19$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[20] = new SearchResult(this, 105, LocaleController.getString("PrivacyP2P", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$20$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[21] = new SearchResult(this, 106, LocaleController.getString("Calls", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$21$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[22] = new SearchResult(this, 107, LocaleController.getString("GroupsAndChannels", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$22$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[23] = new SearchResult(this, 108, LocaleController.getString("Passcode", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$23$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[24] = new SearchResult(this, 109, LocaleController.getString("TwoStepVerification", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$24$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[25] = new SearchResult(this, 110, LocaleController.getString("SessionsTitle", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$25$ProfileActivity$SearchAdapter();
                }
            });
            if (profileActivity.getMessagesController().autoarchiveAvailable) {
                searchResult = new SearchResult(this, 121, LocaleController.getString("ArchiveAndMute", NUM), "newChatsRow", LocaleController.getString("PrivacySettings", NUM), NUM, new Runnable() {
                    public final void run() {
                        ProfileActivity.SearchAdapter.this.lambda$new$26$ProfileActivity$SearchAdapter();
                    }
                });
            } else {
                searchResult = null;
            }
            searchResultArr[26] = searchResult;
            searchResultArr[27] = new SearchResult(this, 112, LocaleController.getString("DeleteAccountIfAwayFor2", NUM), "deleteAccountRow", LocaleController.getString("PrivacySettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$27$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[28] = new SearchResult(this, 113, LocaleController.getString("PrivacyPaymentsClear", NUM), "paymentsClearRow", LocaleController.getString("PrivacySettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$28$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[29] = new SearchResult(this, 114, LocaleController.getString("WebSessionsTitle", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$29$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[30] = new SearchResult(this, 115, LocaleController.getString("SyncContactsDelete", NUM), "contactsDeleteRow", LocaleController.getString("PrivacySettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$30$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[31] = new SearchResult(this, 116, LocaleController.getString("SyncContacts", NUM), "contactsSyncRow", LocaleController.getString("PrivacySettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$31$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[32] = new SearchResult(this, 117, LocaleController.getString("SuggestContacts", NUM), "contactsSuggestRow", LocaleController.getString("PrivacySettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$32$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[33] = new SearchResult(this, 118, LocaleController.getString("MapPreviewProvider", NUM), "secretMapRow", LocaleController.getString("PrivacySettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$33$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[34] = new SearchResult(this, 119, LocaleController.getString("SecretWebPage", NUM), "secretWebpageRow", LocaleController.getString("PrivacySettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$34$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[35] = new SearchResult(this, 120, LocaleController.getString("Devices", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$35$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[36] = new SearchResult(this, 200, LocaleController.getString("DataSettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$36$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[37] = new SearchResult(this, 201, LocaleController.getString("DataUsage", NUM), "usageSectionRow", LocaleController.getString("DataSettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$37$ProfileActivity$SearchAdapter();
                }
            });
            String str = "StorageUsage";
            searchResultArr[38] = new SearchResult(this, 202, LocaleController.getString("StorageUsage", NUM), LocaleController.getString("DataSettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$38$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[39] = new SearchResult(203, LocaleController.getString("KeepMedia", NUM), "keepMediaRow", LocaleController.getString("DataSettings", NUM), LocaleController.getString(str, NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$39$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[40] = new SearchResult(204, LocaleController.getString("ClearMediaCache", NUM), "cacheRow", LocaleController.getString("DataSettings", NUM), LocaleController.getString(str, NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$40$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[41] = new SearchResult(205, LocaleController.getString("LocalDatabase", NUM), "databaseRow", LocaleController.getString("DataSettings", NUM), LocaleController.getString(str, NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$41$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[42] = new SearchResult(this, 206, LocaleController.getString("NetworkUsage", NUM), LocaleController.getString("DataSettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$42$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[43] = new SearchResult(this, 207, LocaleController.getString("AutomaticMediaDownload", NUM), "mediaDownloadSectionRow", LocaleController.getString("DataSettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$43$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[44] = new SearchResult(this, 208, LocaleController.getString("WhenUsingMobileData", NUM), LocaleController.getString("DataSettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$44$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[45] = new SearchResult(this, 209, LocaleController.getString("WhenConnectedOnWiFi", NUM), LocaleController.getString("DataSettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$45$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[46] = new SearchResult(this, 210, LocaleController.getString("WhenRoaming", NUM), LocaleController.getString("DataSettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$46$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[47] = new SearchResult(this, 211, LocaleController.getString("ResetAutomaticMediaDownload", NUM), "resetDownloadRow", LocaleController.getString("DataSettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$47$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[48] = new SearchResult(this, 212, LocaleController.getString("AutoplayMedia", NUM), "autoplayHeaderRow", LocaleController.getString("DataSettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$48$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[49] = new SearchResult(this, 213, LocaleController.getString("AutoplayGIF", NUM), "autoplayGifsRow", LocaleController.getString("DataSettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$49$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[50] = new SearchResult(this, 214, LocaleController.getString("AutoplayVideo", NUM), "autoplayVideoRow", LocaleController.getString("DataSettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$50$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[51] = new SearchResult(this, 215, LocaleController.getString("Streaming", NUM), "streamSectionRow", LocaleController.getString("DataSettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$51$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[52] = new SearchResult(this, 216, LocaleController.getString("EnableStreaming", NUM), "enableStreamRow", LocaleController.getString("DataSettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$52$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[53] = new SearchResult(this, 217, LocaleController.getString("Calls", NUM), "callsSectionRow", LocaleController.getString("DataSettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$53$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[54] = new SearchResult(this, 218, LocaleController.getString("VoipUseLessData", NUM), "useLessDataForCallsRow", LocaleController.getString("DataSettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$54$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[55] = new SearchResult(this, 219, LocaleController.getString("VoipQuickReplies", NUM), "quickRepliesRow", LocaleController.getString("DataSettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$55$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[56] = new SearchResult(this, 220, LocaleController.getString("ProxySettings", NUM), LocaleController.getString("DataSettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$56$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[57] = new SearchResult(221, LocaleController.getString("UseProxyForCalls", NUM), "callsRow", LocaleController.getString("DataSettings", NUM), LocaleController.getString("ProxySettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$57$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[58] = new SearchResult(this, 111, LocaleController.getString("PrivacyDeleteCloudDrafts", NUM), "clearDraftsRow", LocaleController.getString("DataSettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$58$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[59] = new SearchResult(this, 300, LocaleController.getString("ChatSettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$59$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[60] = new SearchResult(this, 301, LocaleController.getString("TextSizeHeader", NUM), "textSizeHeaderRow", LocaleController.getString("ChatSettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$60$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[61] = new SearchResult(this, 302, LocaleController.getString("ChatBackground", NUM), LocaleController.getString("ChatSettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$61$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[62] = new SearchResult(303, LocaleController.getString("SetColor", NUM), (String) null, LocaleController.getString("ChatSettings", NUM), LocaleController.getString("ChatBackground", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$62$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[63] = new SearchResult(304, LocaleController.getString("ResetChatBackgrounds", NUM), "resetRow", LocaleController.getString("ChatSettings", NUM), LocaleController.getString("ChatBackground", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$63$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[64] = new SearchResult(this, 305, LocaleController.getString("AutoNightTheme", NUM), LocaleController.getString("ChatSettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$64$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[65] = new SearchResult(this, 306, LocaleController.getString("ColorTheme", NUM), "themeHeaderRow", LocaleController.getString("ChatSettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$65$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[66] = new SearchResult(this, 307, LocaleController.getString("ChromeCustomTabs", NUM), "customTabsRow", LocaleController.getString("ChatSettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$66$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[67] = new SearchResult(this, 308, LocaleController.getString("DirectShare", NUM), "directShareRow", LocaleController.getString("ChatSettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$67$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[68] = new SearchResult(this, 309, LocaleController.getString("EnableAnimations", NUM), "enableAnimationsRow", LocaleController.getString("ChatSettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$68$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[69] = new SearchResult(this, 310, LocaleController.getString("RaiseToSpeak", NUM), "raiseToSpeakRow", LocaleController.getString("ChatSettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$69$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[70] = new SearchResult(this, 311, LocaleController.getString("SendByEnter", NUM), "sendByEnterRow", LocaleController.getString("ChatSettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$70$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[71] = new SearchResult(this, 312, LocaleController.getString("SaveToGallerySettings", NUM), "saveToGalleryRow", LocaleController.getString("ChatSettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$71$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[72] = new SearchResult(this, 312, LocaleController.getString("DistanceUnits", NUM), "distanceRow", LocaleController.getString("ChatSettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$72$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[73] = new SearchResult(this, 313, LocaleController.getString("StickersAndMasks", NUM), LocaleController.getString("ChatSettings", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$73$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[74] = new SearchResult(314, LocaleController.getString("SuggestStickers", NUM), "suggestRow", LocaleController.getString("ChatSettings", NUM), LocaleController.getString("StickersAndMasks", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$74$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[75] = new SearchResult(315, LocaleController.getString("FeaturedStickers", NUM), (String) null, LocaleController.getString("ChatSettings", NUM), LocaleController.getString("StickersAndMasks", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$75$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[76] = new SearchResult(316, LocaleController.getString("Masks", NUM), (String) null, LocaleController.getString("ChatSettings", NUM), LocaleController.getString("StickersAndMasks", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$76$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[77] = new SearchResult(317, LocaleController.getString("ArchivedStickers", NUM), (String) null, LocaleController.getString("ChatSettings", NUM), LocaleController.getString("StickersAndMasks", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$77$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[78] = new SearchResult(317, LocaleController.getString("ArchivedMasks", NUM), (String) null, LocaleController.getString("ChatSettings", NUM), LocaleController.getString("StickersAndMasks", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$78$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[79] = new SearchResult(this, 400, LocaleController.getString("Language", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$79$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[80] = new SearchResult(this, 402, LocaleController.getString("AskAQuestion", NUM), LocaleController.getString("SettingsHelp", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$80$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[81] = new SearchResult(this, 403, LocaleController.getString("TelegramFAQ", NUM), LocaleController.getString("SettingsHelp", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$81$ProfileActivity$SearchAdapter();
                }
            });
            searchResultArr[82] = new SearchResult(this, 404, LocaleController.getString("PrivacyPolicy", NUM), LocaleController.getString("SettingsHelp", NUM), NUM, new Runnable() {
                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$new$82$ProfileActivity$SearchAdapter();
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
            Collections.sort(this.recentSearches, new Object() {
                public final int compare(Object obj, Object obj2) {
                    return ProfileActivity.SearchAdapter.this.lambda$new$83$ProfileActivity$SearchAdapter(obj, obj2);
                }

                public /* synthetic */ java.util.Comparator reversed() {
                    return Comparator.CC.$default$reversed(this);
                }

                public /* synthetic */ java.util.Comparator thenComparing(Function function) {
                    return Comparator.CC.$default$thenComparing((java.util.Comparator) this, function);
                }

                public /* synthetic */ java.util.Comparator thenComparing(Function function, java.util.Comparator comparator) {
                    return Comparator.CC.$default$thenComparing(this, function, comparator);
                }

                public /* synthetic */ java.util.Comparator thenComparing(java.util.Comparator comparator) {
                    return Comparator.CC.$default$thenComparing((java.util.Comparator) this, comparator);
                }

                public /* synthetic */ java.util.Comparator thenComparingDouble(ToDoubleFunction toDoubleFunction) {
                    return Comparator.CC.$default$thenComparingDouble(this, toDoubleFunction);
                }

                public /* synthetic */ java.util.Comparator thenComparingInt(ToIntFunction toIntFunction) {
                    return Comparator.CC.$default$thenComparingInt(this, toIntFunction);
                }

                public /* synthetic */ java.util.Comparator thenComparingLong(ToLongFunction toLongFunction) {
                    return Comparator.CC.$default$thenComparingLong(this, toLongFunction);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$83 */
        public /* synthetic */ int lambda$new$83$ProfileActivity$SearchAdapter(Object obj, Object obj2) {
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
                this.this$0.getConnectionsManager().sendRequest(tLRPC$TL_messages_getWebPage, new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        ProfileActivity.SearchAdapter.this.lambda$loadFaqWebPage$85$ProfileActivity$SearchAdapter(tLObject, tLRPC$TL_error);
                    }
                });
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$loadFaqWebPage$85 */
        public /* synthetic */ void lambda$loadFaqWebPage$85$ProfileActivity$SearchAdapter(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                AndroidUtilities.runOnUIThread(new Runnable(arrayList) {
                    public final /* synthetic */ ArrayList f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        ProfileActivity.SearchAdapter.this.lambda$loadFaqWebPage$84$ProfileActivity$SearchAdapter(this.f$1);
                    }
                });
            }
            this.loadingFaqPage = false;
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$loadFaqWebPage$84 */
        public /* synthetic */ void lambda$loadFaqWebPage$84$ProfileActivity$SearchAdapter(ArrayList arrayList) {
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
                            String access$22700 = searchResult.searchTitle;
                            String[] access$22600 = searchResult.path;
                            if (i >= this.recentSearches.size() - 1) {
                                z = false;
                            }
                            settingsSearchCell.setTextAndValue(access$22700, access$22600, false, z);
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
                    String[] access$226002 = searchResult2.path;
                    if (i >= this.searchResults.size() - 1) {
                        z = false;
                    }
                    settingsSearchCell.setTextAndValueAndIcon(charSequence, access$226002, i2, z);
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
            $$Lambda$ProfileActivity$SearchAdapter$GljfJTfxfMnyTtz5iII2fCyNwVU r1 = new Runnable(str) {
                public final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ProfileActivity.SearchAdapter.this.lambda$search$87$ProfileActivity$SearchAdapter(this.f$1);
                }
            };
            this.searchRunnable = r1;
            dispatchQueue.postRunnable(r1, 300);
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$search$87 */
        public /* synthetic */ void lambda$search$87$ProfileActivity$SearchAdapter(String str) {
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
                    ProfileActivity.SearchAdapter.this.lambda$search$86$ProfileActivity$SearchAdapter(this.f$1, this.f$2, this.f$3, this.f$4);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$search$86 */
        public /* synthetic */ void lambda$search$86$ProfileActivity$SearchAdapter(String str, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3) {
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

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        $$Lambda$ProfileActivity$Ej91zEX2QJ13OnYunMfq0v8rZ2M r10 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                ProfileActivity.this.lambda$getThemeDescriptions$34$ProfileActivity();
            }
        };
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
        $$Lambda$ProfileActivity$Ej91zEX2QJ13OnYunMfq0v8rZ2M r7 = r10;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_actionBarSelectorBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "chat_lockIcon"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_subtitleInProfileBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundActionBarBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "profile_title"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "profile_status"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_subtitleInProfileBlue"));
        if (this.mediaCounterTextView != null) {
            $$Lambda$ProfileActivity$Ej91zEX2QJ13OnYunMfq0v8rZ2M r72 = r10;
            arrayList.add(new ThemeDescription(this.mediaCounterTextView.getTextView(), ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, r72, "player_actionBarSubtitle"));
            arrayList.add(new ThemeDescription(this.mediaCounterTextView.getNextTextView(), ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, r72, "player_actionBarSubtitle"));
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
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{UserCell.class}, new String[]{"adminTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_creatorIcon"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        $$Lambda$ProfileActivity$Ej91zEX2QJ13OnYunMfq0v8rZ2M r8 = r10;
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r8, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r8, "windowBackgroundWhiteBlueText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        $$Lambda$ProfileActivity$Ej91zEX2QJ13OnYunMfq0v8rZ2M r73 = r10;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r73, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r73, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r73, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r73, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r73, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r73, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r73, "avatar_backgroundPink"));
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
    /* renamed from: lambda$getThemeDescriptions$34 */
    public /* synthetic */ void lambda$getThemeDescriptions$34$ProfileActivity() {
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
            this.nameTextView[1].setTextColor(Theme.getColor("profile_title"));
            this.actionBar.setItemsColor(Theme.getColor("actionBarDefaultIcon"), false);
            this.actionBar.setItemsBackgroundColor(Theme.getColor("avatar_actionBarSelectorBlue"), false);
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
        DiffUtil.calculateDiff(diffCallback).dispatchUpdatesTo((RecyclerView.Adapter) this.listAdapter);
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
            put(7, ProfileActivity.this.settingsSectionRow, sparseIntArray);
            put(8, ProfileActivity.this.settingsSectionRow2, sparseIntArray);
            put(9, ProfileActivity.this.notificationRow, sparseIntArray);
            put(10, ProfileActivity.this.languageRow, sparseIntArray);
            put(11, ProfileActivity.this.privacyRow, sparseIntArray);
            put(12, ProfileActivity.this.dataRow, sparseIntArray);
            put(13, ProfileActivity.this.chatRow, sparseIntArray);
            put(14, ProfileActivity.this.filtersRow, sparseIntArray);
            put(15, ProfileActivity.this.devicesRow, sparseIntArray);
            put(16, ProfileActivity.this.devicesSectionRow, sparseIntArray);
            put(17, ProfileActivity.this.helpHeaderRow, sparseIntArray);
            put(18, ProfileActivity.this.questionRow, sparseIntArray);
            put(19, ProfileActivity.this.faqRow, sparseIntArray);
            put(20, ProfileActivity.this.policyRow, sparseIntArray);
            put(21, ProfileActivity.this.helpSectionCell, sparseIntArray);
            put(22, ProfileActivity.this.debugHeaderRow, sparseIntArray);
            put(23, ProfileActivity.this.sendLogsRow, sparseIntArray);
            put(24, ProfileActivity.this.sendLastLogsRow, sparseIntArray);
            put(25, ProfileActivity.this.clearLogsRow, sparseIntArray);
            put(26, ProfileActivity.this.switchBackendRow, sparseIntArray);
            put(27, ProfileActivity.this.versionRow, sparseIntArray);
            put(28, ProfileActivity.this.emptyRow, sparseIntArray);
            put(29, ProfileActivity.this.bottomPaddingRow, sparseIntArray);
            put(30, ProfileActivity.this.infoHeaderRow, sparseIntArray);
            put(31, ProfileActivity.this.phoneRow, sparseIntArray);
            put(32, ProfileActivity.this.locationRow, sparseIntArray);
            put(33, ProfileActivity.this.userInfoRow, sparseIntArray);
            put(34, ProfileActivity.this.channelInfoRow, sparseIntArray);
            put(35, ProfileActivity.this.usernameRow, sparseIntArray);
            put(36, ProfileActivity.this.notificationsDividerRow, sparseIntArray);
            put(37, ProfileActivity.this.notificationsRow, sparseIntArray);
            put(38, ProfileActivity.this.infoSectionRow, sparseIntArray);
            put(39, ProfileActivity.this.sendMessageRow, sparseIntArray);
            put(40, ProfileActivity.this.reportRow, sparseIntArray);
            put(41, ProfileActivity.this.settingsTimerRow, sparseIntArray);
            put(42, ProfileActivity.this.settingsKeyRow, sparseIntArray);
            put(43, ProfileActivity.this.secretSettingsSectionRow, sparseIntArray);
            put(44, ProfileActivity.this.membersHeaderRow, sparseIntArray);
            put(45, ProfileActivity.this.addMemberRow, sparseIntArray);
            put(46, ProfileActivity.this.subscribersRow, sparseIntArray);
            put(47, ProfileActivity.this.administratorsRow, sparseIntArray);
            put(48, ProfileActivity.this.blockedUsersRow, sparseIntArray);
            put(49, ProfileActivity.this.membersSectionRow, sparseIntArray);
            put(50, ProfileActivity.this.sharedMediaRow, sparseIntArray);
            put(51, ProfileActivity.this.unblockRow, sparseIntArray);
            put(52, ProfileActivity.this.joinRow, sparseIntArray);
            put(53, ProfileActivity.this.lastSectionRow, sparseIntArray);
        }

        private void put(int i, int i2, SparseIntArray sparseIntArray) {
            if (i2 >= 0) {
                sparseIntArray.put(i2, i);
            }
        }
    }
}
