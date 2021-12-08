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
import android.os.SystemClock;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.MovementMethod;
import android.text.style.ForegroundColorSpan;
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
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.collection.LongSparseArray;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ChatThemeController;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
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
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CrossfadeDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.FragmentContextView;
import org.telegram.ui.Components.HintView;
import org.telegram.ui.Components.IdenticonDrawable;
import org.telegram.ui.Components.ImageUpdater;
import org.telegram.ui.Components.LayoutHelper;
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
import org.telegram.ui.PinchToZoomHelper;

public class ProfileActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, DialogsActivity.DialogsActivityDelegate, SharedMediaLayout.SharedMediaPreloaderDelegate, ImageUpdater.ImageUpdaterDelegate, SharedMediaLayout.Delegate {
    private static final int add_contact = 1;
    private static final int add_member = 18;
    private static final int add_photo = 36;
    private static final int add_shortcut = 14;
    private static final int block_contact = 2;
    private static final int call_item = 15;
    private static final int delete_avatar = 35;
    private static final int delete_contact = 5;
    private static final int edit_avatar = 34;
    private static final int edit_channel = 12;
    private static final int edit_contact = 4;
    private static final int edit_name = 30;
    private static final int gallery_menu_save = 21;
    private static final int invite_to_group = 9;
    private static final int leave_group = 7;
    private static final int logout = 31;
    private static final int search_button = 32;
    private static final int search_members = 17;
    private static final int set_as_main = 33;
    private static final int share = 10;
    private static final int share_contact = 3;
    private static final int start_secret_chat = 20;
    private static final int statistics = 19;
    private static final int video_call_item = 16;
    private static final int view_discussion = 22;
    private Property<ActionBar, Float> ACTIONBAR_HEADER_PROGRESS;
    /* access modifiers changed from: private */
    public final Property<ProfileActivity, Float> HEADER_SHADOW;
    private int actionBarAnimationColorFrom;
    /* access modifiers changed from: private */
    public int addMemberRow;
    /* access modifiers changed from: private */
    public int administratorsRow;
    private boolean allowProfileAnimation;
    /* access modifiers changed from: private */
    public boolean allowPullingDown;
    /* access modifiers changed from: private */
    public ActionBarMenuItem animatingItem;
    /* access modifiers changed from: private */
    public float animationProgress;
    private TLRPC.FileLocation avatar;
    /* access modifiers changed from: private */
    public AnimatorSet avatarAnimation;
    /* access modifiers changed from: private */
    public TLRPC.FileLocation avatarBig;
    private int avatarColor;
    /* access modifiers changed from: private */
    public FrameLayout avatarContainer;
    /* access modifiers changed from: private */
    public FrameLayout avatarContainer2;
    private AvatarDrawable avatarDrawable;
    /* access modifiers changed from: private */
    public AvatarImageView avatarImage;
    private View avatarOverlay;
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
    public TLRPC.BotInfo botInfo;
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
    public long chatId;
    /* access modifiers changed from: private */
    public TLRPC.ChatFull chatInfo;
    /* access modifiers changed from: private */
    public int chatRow;
    /* access modifiers changed from: private */
    public int clearLogsRow;
    /* access modifiers changed from: private */
    public boolean creatingChat;
    /* access modifiers changed from: private */
    public String currentBio;
    private TLRPC.ChannelParticipant currentChannelParticipant;
    /* access modifiers changed from: private */
    public TLRPC.Chat currentChat;
    /* access modifiers changed from: private */
    public TLRPC.EncryptedChat currentEncryptedChat;
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
    public LongSparseArray<TLRPC.ChatParticipant> participantsMap;
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
    private long selectedUser;
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
    public int unblockRow;
    /* access modifiers changed from: private */
    public UndoView undoView;
    private ImageLocation uploadingImageLocation;
    /* access modifiers changed from: private */
    public boolean userBlocked;
    /* access modifiers changed from: private */
    public long userId;
    /* access modifiers changed from: private */
    public TLRPC.UserFull userInfo;
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
    public final ArrayList<TLRPC.ChatParticipant> visibleChatParticipants;
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

    static /* synthetic */ int access$7112(ProfileActivity x0, int x1) {
        int i = x0.listContentHeight + x1;
        x0.listContentHeight = i;
        return i;
    }

    public static class AvatarImageView extends BackupImageView {
        ProfileGalleryView avatarsViewPager;
        private ImageReceiver.BitmapHolder drawableHolder;
        private float foregroundAlpha;
        private ImageReceiver foregroundImageReceiver = new ImageReceiver(this);
        private final Paint placeholderPaint;
        private final RectF rect = new RectF();

        public void setAvatarsViewPager(ProfileGalleryView avatarsViewPager2) {
            this.avatarsViewPager = avatarsViewPager2;
        }

        public AvatarImageView(Context context) {
            super(context);
            Paint paint = new Paint(1);
            this.placeholderPaint = paint;
            paint.setColor(-16777216);
        }

        public void setForegroundImage(ImageLocation imageLocation, String imageFilter, Drawable thumb) {
            this.foregroundImageReceiver.setImage(imageLocation, imageFilter, thumb, 0, (String) null, (Object) null, 0);
            ImageReceiver.BitmapHolder bitmapHolder = this.drawableHolder;
            if (bitmapHolder != null) {
                bitmapHolder.release();
                this.drawableHolder = null;
            }
        }

        public void setForegroundImageDrawable(ImageReceiver.BitmapHolder holder) {
            if (holder != null) {
                this.foregroundImageReceiver.setImageBitmap(holder.drawable);
            }
            ImageReceiver.BitmapHolder bitmapHolder = this.drawableHolder;
            if (bitmapHolder != null) {
                bitmapHolder.release();
                this.drawableHolder = null;
            }
            this.drawableHolder = holder;
        }

        public float getForegroundAlpha() {
            return this.foregroundAlpha;
        }

        public void setForegroundAlpha(float value) {
            this.foregroundAlpha = value;
            invalidate();
        }

        public void clearForeground() {
            AnimatedFileDrawable drawable = this.foregroundImageReceiver.getAnimation();
            if (drawable != null) {
                drawable.removeSecondParentView(this);
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

        public void setRoundRadius(int value) {
            super.setRoundRadius(value);
            this.foregroundImageReceiver.setRoundRadius(value);
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
            int radius = this.foregroundImageReceiver.getRoundRadius()[0];
            canvas.drawRoundRect(this.rect, (float) radius, (float) radius, this.placeholderPaint);
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
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(widthMeasureSpec) + AndroidUtilities.dp(3.0f));
        }

        public void setBackgroundColor(int color) {
            if (color != this.currentColor) {
                this.currentColor = color;
                this.paint.setColor(color);
                invalidate();
            }
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            float v = ProfileActivity.this.extraHeight + ((float) (ActionBar.getCurrentActionBarHeight() + (ProfileActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0))) + ((float) ProfileActivity.this.searchTransitionOffset);
            int y1 = (int) ((1.0f - ProfileActivity.this.mediaHeaderAnimationProgress) * v);
            if (y1 != 0) {
                this.paint.setColor(this.currentColor);
                canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) y1, this.paint);
            }
            if (((float) y1) != v) {
                this.paint.setColor(Theme.getColor("windowBackgroundWhite"));
                canvas.drawRect(0.0f, (float) y1, (float) getMeasuredWidth(), v, this.paint);
            }
            if (ProfileActivity.this.parentLayout != null) {
                ProfileActivity.this.parentLayout.drawHeaderShadow(canvas, (int) (ProfileActivity.this.headerShadowAlpha * 255.0f), (int) v);
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
                public void onAnimationEnd(Animator animation) {
                    if (!OverlaysView.this.isOverlaysVisible) {
                        OverlaysView.this.setVisibility(8);
                    }
                }

                public void onAnimationStart(Animator animation) {
                    OverlaysView.this.setVisibility(0);
                }
            });
        }

        /* renamed from: lambda$new$0$org-telegram-ui-ProfileActivity$OverlaysView  reason: not valid java name */
        public /* synthetic */ void m3731lambda$new$0$orgtelegramuiProfileActivity$OverlaysView(ValueAnimator anim) {
            float[] fArr = this.animatorValues;
            float animatedFraction = anim.getAnimatedFraction();
            this.currentAnimationValue = animatedFraction;
            setAlphaValue(AndroidUtilities.lerp(fArr, animatedFraction), true);
        }

        public void saveCurrentPageProgress() {
            this.previousSelectedProgress = this.currentProgress;
            this.previousSelectedPotision = this.selectedPosition;
            this.currentLoadingAnimationProgress = 0.0f;
            this.currentLoadingAnimationDirection = 1;
        }

        public void setAlphaValue(float value, boolean self) {
            if (Build.VERSION.SDK_INT > 18) {
                int alpha2 = (int) (255.0f * value);
                this.topOverlayGradient.setAlpha(alpha2);
                this.bottomOverlayGradient.setAlpha(alpha2);
                this.backgroundPaint.setAlpha((int) (66.0f * value));
                this.barPaint.setAlpha((int) (85.0f * value));
                this.selectedBarPaint.setAlpha(alpha2);
                this.alpha = value;
            } else {
                setAlpha(value);
            }
            if (!self) {
                this.currentAnimationValue = value;
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

        public void setOverlaysVisible(boolean overlaysVisible, float durationFactor) {
            if (overlaysVisible != this.isOverlaysVisible) {
                this.isOverlaysVisible = overlaysVisible;
                this.animator.cancel();
                float value = AndroidUtilities.lerp(this.animatorValues, this.currentAnimationValue);
                float f = 1.0f;
                if (overlaysVisible) {
                    this.animator.setDuration((long) (((1.0f - value) * 250.0f) / durationFactor));
                } else {
                    this.animator.setDuration((long) ((250.0f * value) / durationFactor));
                }
                float[] fArr = this.animatorValues;
                fArr[0] = value;
                if (!overlaysVisible) {
                    f = 0.0f;
                }
                fArr[1] = f;
                this.animator.start();
            }
        }

        /* access modifiers changed from: protected */
        public void onSizeChanged(int w, int h, int oldw, int oldh) {
            int actionBarHeight = this.statusBarHeight + ActionBar.getCurrentActionBarHeight();
            this.topOverlayRect.set(0, 0, w, (int) (((float) actionBarHeight) * 0.5f));
            this.bottomOverlayRect.set(0, (int) (((float) h) - (((float) AndroidUtilities.dp(72.0f)) * 0.5f)), w, h);
            this.topOverlayGradient.setBounds(0, this.topOverlayRect.bottom, w, AndroidUtilities.dp(16.0f) + actionBarHeight);
            this.bottomOverlayGradient.setBounds(0, (h - AndroidUtilities.dp(72.0f)) - AndroidUtilities.dp(24.0f), w, this.bottomOverlayRect.top);
            this.pressedOverlayGradient[0].setBounds(0, 0, w / 5, h);
            this.pressedOverlayGradient[1].setBounds(w - (w / 5), 0, w, h);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            int count;
            float progress;
            int baseAlpha;
            Canvas canvas2 = canvas;
            for (int i = 0; i < 2; i++) {
                float[] fArr = this.pressedOverlayAlpha;
                if (fArr[i] > 0.0f) {
                    this.pressedOverlayGradient[i].setAlpha((int) (fArr[i] * 255.0f));
                    this.pressedOverlayGradient[i].draw(canvas2);
                }
            }
            this.topOverlayGradient.draw(canvas2);
            this.bottomOverlayGradient.draw(canvas2);
            canvas2.drawRect(this.topOverlayRect, this.backgroundPaint);
            canvas2.drawRect(this.bottomOverlayRect, this.backgroundPaint);
            int count2 = ProfileActivity.this.avatarsViewPager.getRealCount();
            this.selectedPosition = ProfileActivity.this.avatarsViewPager.getRealPosition();
            float[] fArr2 = this.alphas;
            if (fArr2 == null || fArr2.length != count2) {
                float[] fArr3 = new float[count2];
                this.alphas = fArr3;
                Arrays.fill(fArr3, 0.0f);
            }
            boolean invalidate = false;
            long newTime = SystemClock.elapsedRealtime();
            long dt = newTime - this.lastTime;
            if (dt < 0 || dt > 20) {
                dt = 17;
            }
            this.lastTime = newTime;
            float f = 1.0f;
            if (count2 <= 1 || count2 > 20) {
                long j = newTime;
            } else {
                if (ProfileActivity.this.overlayCountVisible == 0) {
                    this.alpha = 0.0f;
                    int unused = ProfileActivity.this.overlayCountVisible = 3;
                } else if (ProfileActivity.this.overlayCountVisible == 1) {
                    this.alpha = 0.0f;
                    int unused2 = ProfileActivity.this.overlayCountVisible = 2;
                }
                if (ProfileActivity.this.overlayCountVisible == 2) {
                    this.barPaint.setAlpha((int) (this.alpha * 85.0f));
                    this.selectedBarPaint.setAlpha((int) (this.alpha * 255.0f));
                }
                int width = ((getMeasuredWidth() - AndroidUtilities.dp(10.0f)) - AndroidUtilities.dp((float) ((count2 - 1) * 2))) / count2;
                int y = AndroidUtilities.dp(4.0f) + ((Build.VERSION.SDK_INT < 21 || ProfileActivity.this.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight);
                int a = 0;
                while (a < count2) {
                    int x = AndroidUtilities.dp((float) ((a * 2) + 5)) + (width * a);
                    if (a != this.previousSelectedPotision || Math.abs(this.previousSelectedProgress - f) <= 1.0E-4f) {
                        count = count2;
                        if (a != this.selectedPosition) {
                            progress = 1.0f;
                            baseAlpha = 85;
                        } else if (ProfileActivity.this.avatarsViewPager.isCurrentItemVideo()) {
                            float currentItemProgress = ProfileActivity.this.avatarsViewPager.getCurrentItemProgress();
                            this.currentProgress = currentItemProgress;
                            float progress2 = currentItemProgress;
                            if ((progress2 <= 0.0f && ProfileActivity.this.avatarsViewPager.isLoadingCurrentVideo()) || this.currentLoadingAnimationProgress > 0.0f) {
                                float f2 = this.currentLoadingAnimationProgress;
                                int i2 = this.currentLoadingAnimationDirection;
                                float f3 = f2 + (((float) (((long) i2) * dt)) / 500.0f);
                                this.currentLoadingAnimationProgress = f3;
                                if (f3 > 1.0f) {
                                    this.currentLoadingAnimationProgress = 1.0f;
                                    this.currentLoadingAnimationDirection = i2 * -1;
                                } else if (f3 <= 0.0f) {
                                    this.currentLoadingAnimationProgress = 0.0f;
                                    this.currentLoadingAnimationDirection = i2 * -1;
                                }
                            }
                            this.rect.set((float) x, (float) y, (float) (x + width), (float) (y + AndroidUtilities.dp(2.0f)));
                            this.barPaint.setAlpha((int) (((this.currentLoadingAnimationProgress * 48.0f) + 85.0f) * this.alpha));
                            canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f), this.barPaint);
                            invalidate = true;
                            baseAlpha = 80;
                            progress = progress2;
                        } else {
                            this.currentProgress = 1.0f;
                            progress = 1.0f;
                            baseAlpha = 85;
                        }
                    } else {
                        float progress3 = this.previousSelectedProgress;
                        canvas.save();
                        count = count2;
                        canvas2.clipRect(((float) x) + (((float) width) * progress3), (float) y, (float) (x + width), (float) (y + AndroidUtilities.dp(2.0f)));
                        this.rect.set((float) x, (float) y, (float) (x + width), (float) (y + AndroidUtilities.dp(2.0f)));
                        this.barPaint.setAlpha((int) (this.alpha * 85.0f));
                        canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f), this.barPaint);
                        canvas.restore();
                        invalidate = true;
                        baseAlpha = 80;
                        progress = progress3;
                    }
                    boolean invalidate2 = invalidate;
                    long newTime2 = newTime;
                    this.rect.set((float) x, (float) y, ((float) x) + (((float) width) * progress), (float) (AndroidUtilities.dp(2.0f) + y));
                    if (a == this.selectedPosition) {
                        this.alphas[a] = 0.75f;
                    } else if (ProfileActivity.this.overlayCountVisible == 3) {
                        this.barPaint.setAlpha((int) (AndroidUtilities.lerp((float) baseAlpha, 255.0f, CubicBezierInterpolator.EASE_BOTH.getInterpolation(this.alphas[a])) * this.alpha));
                    }
                    canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f), a == this.selectedPosition ? this.selectedBarPaint : this.barPaint);
                    a++;
                    invalidate = invalidate2;
                    count2 = count;
                    newTime = newTime2;
                    f = 1.0f;
                }
                long j2 = newTime;
                if (ProfileActivity.this.overlayCountVisible == 2) {
                    float f4 = this.alpha;
                    if (f4 < 1.0f) {
                        float f5 = f4 + (((float) dt) / 180.0f);
                        this.alpha = f5;
                        if (f5 > 1.0f) {
                            this.alpha = 1.0f;
                        }
                        invalidate = true;
                    } else {
                        int unused3 = ProfileActivity.this.overlayCountVisible = 3;
                    }
                } else if (ProfileActivity.this.overlayCountVisible == 3) {
                    int i3 = 0;
                    while (true) {
                        float[] fArr4 = this.alphas;
                        if (i3 >= fArr4.length) {
                            break;
                        }
                        if (i3 != this.selectedPosition && fArr4[i3] > 0.0f) {
                            fArr4[i3] = fArr4[i3] - (((float) dt) / 500.0f);
                            if (fArr4[i3] <= 0.0f) {
                                fArr4[i3] = 0.0f;
                                if (i3 == this.previousSelectedPotision) {
                                    this.previousSelectedPotision = -1;
                                }
                            }
                            invalidate = true;
                        } else if (i3 == this.previousSelectedPotision) {
                            this.previousSelectedPotision = -1;
                        }
                        i3++;
                    }
                }
            }
            for (int i4 = 0; i4 < 2; i4++) {
                if (this.pressedOverlayVisible[i4]) {
                    float[] fArr5 = this.pressedOverlayAlpha;
                    if (fArr5[i4] < 1.0f) {
                        fArr5[i4] = fArr5[i4] + (((float) dt) / 180.0f);
                        if (fArr5[i4] > 1.0f) {
                            fArr5[i4] = 1.0f;
                        }
                        invalidate = true;
                    }
                } else {
                    float[] fArr6 = this.pressedOverlayAlpha;
                    if (fArr6[i4] > 0.0f) {
                        fArr6[i4] = fArr6[i4] - (((float) dt) / 180.0f);
                        if (fArr6[i4] < 0.0f) {
                            fArr6[i4] = 0.0f;
                        }
                        invalidate = true;
                    }
                }
            }
            if (invalidate) {
                postInvalidateOnAnimation();
            }
        }

        public void onDown(boolean left) {
            this.pressedOverlayVisible[!left] = true;
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

        public NestedFrameLayout(Context context) {
            super(context);
        }

        public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, int[] consumed) {
            if (target == ProfileActivity.this.listView && ProfileActivity.this.sharedMediaLayoutAttached) {
                RecyclerListView innerListView = ProfileActivity.this.sharedMediaLayout.getCurrentListView();
                if (ProfileActivity.this.sharedMediaLayout.getTop() == 0) {
                    consumed[1] = dyUnconsumed;
                    innerListView.scrollBy(0, dyUnconsumed);
                }
            }
        }

        public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        }

        public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
            return super.onNestedPreFling(target, velocityX, velocityY);
        }

        public void onNestedPreScroll(View target, int dx, int dy, int[] consumed, int type) {
            int i = dy;
            if (target == ProfileActivity.this.listView) {
                int top = -1;
                if (ProfileActivity.this.sharedMediaRow != -1 && ProfileActivity.this.sharedMediaLayoutAttached) {
                    boolean searchVisible = ProfileActivity.this.actionBar.isSearchFieldVisible();
                    int t = ProfileActivity.this.sharedMediaLayout.getTop();
                    if (i < 0) {
                        boolean scrolledInner = false;
                        if (t <= 0) {
                            RecyclerListView innerListView = ProfileActivity.this.sharedMediaLayout.getCurrentListView();
                            int pos = ((LinearLayoutManager) innerListView.getLayoutManager()).findFirstVisibleItemPosition();
                            if (pos != -1) {
                                RecyclerView.ViewHolder holder = innerListView.findViewHolderForAdapterPosition(pos);
                                if (holder != null) {
                                    top = holder.itemView.getTop();
                                }
                                int paddingTop = innerListView.getPaddingTop();
                                if (!(top == paddingTop && pos == 0)) {
                                    consumed[1] = pos != 0 ? i : Math.max(i, top - paddingTop);
                                    innerListView.scrollBy(0, i);
                                    scrolledInner = true;
                                }
                            }
                        }
                        if (!searchVisible) {
                            return;
                        }
                        if (scrolledInner || t >= 0) {
                            consumed[1] = i;
                        } else {
                            consumed[1] = i - Math.max(t, i);
                        }
                    } else if (searchVisible) {
                        RecyclerListView innerListView2 = ProfileActivity.this.sharedMediaLayout.getCurrentListView();
                        consumed[1] = i;
                        if (t > 0) {
                            consumed[1] = consumed[1] - i;
                        }
                        if (consumed[1] > 0) {
                            innerListView2.scrollBy(0, consumed[1]);
                        }
                    }
                }
            }
        }

        public boolean onStartNestedScroll(View child, View target, int axes, int type) {
            return ProfileActivity.this.sharedMediaRow != -1 && axes == 2;
        }

        public void onNestedScrollAccepted(View child, View target, int axes, int type) {
            this.nestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
        }

        public void onStopNestedScroll(View target, int type) {
            this.nestedScrollingParentHelper.onStopNestedScroll(target);
        }

        public void onStopNestedScroll(View child) {
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
            final boolean expanded = ProfileActivity.this.expandPhoto;
            ofFloat.addListener(new AnimatorListenerAdapter(ProfileActivity.this) {
                public void onAnimationEnd(Animator animation) {
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

                public void onAnimationStart(Animator animation) {
                    if (ProfileActivity.this.searchItem != null && !expanded) {
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

                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                public void onPageSelected(int position) {
                    int realPosition = ProfileActivity.this.avatarsViewPager.getRealPosition(position);
                    PagerIndicatorView.this.invalidateIndicatorRect(this.prevPage != realPosition);
                    this.prevPage = realPosition;
                    PagerIndicatorView.this.updateAvatarItems();
                }

                public void onPageScrollStateChanged(int state) {
                }
            });
            adapter2.registerDataSetObserver(new DataSetObserver(ProfileActivity.this) {
                public void onChanged() {
                    int count = ProfileActivity.this.avatarsViewPager.getRealCount();
                    if (ProfileActivity.this.overlayCountVisible == 0 && count > 1 && count <= 20 && ProfileActivity.this.overlaysView.isOverlaysVisible()) {
                        int unused = ProfileActivity.this.overlayCountVisible = 1;
                    }
                    PagerIndicatorView.this.invalidateIndicatorRect(false);
                    PagerIndicatorView.this.refreshVisibility(1.0f);
                    PagerIndicatorView.this.updateAvatarItems();
                }
            });
        }

        /* renamed from: lambda$new$0$org-telegram-ui-ProfileActivity$PagerIndicatorView  reason: not valid java name */
        public /* synthetic */ void m3732lambda$new$0$orgtelegramuiProfileActivity$PagerIndicatorView(ValueAnimator a) {
            float value = AndroidUtilities.lerp(this.animatorValues, a.getAnimatedFraction());
            if (ProfileActivity.this.searchItem != null && !ProfileActivity.this.isPulledDown) {
                ProfileActivity.this.searchItem.setScaleX(1.0f - value);
                ProfileActivity.this.searchItem.setScaleY(1.0f - value);
                ProfileActivity.this.searchItem.setAlpha(1.0f - value);
            }
            if (ProfileActivity.this.editItemVisible) {
                ProfileActivity.this.editItem.setScaleX(1.0f - value);
                ProfileActivity.this.editItem.setScaleY(1.0f - value);
                ProfileActivity.this.editItem.setAlpha(1.0f - value);
            }
            if (ProfileActivity.this.callItemVisible) {
                ProfileActivity.this.callItem.setScaleX(1.0f - value);
                ProfileActivity.this.callItem.setScaleY(1.0f - value);
                ProfileActivity.this.callItem.setAlpha(1.0f - value);
            }
            if (ProfileActivity.this.videoCallItemVisible) {
                ProfileActivity.this.videoCallItem.setScaleX(1.0f - value);
                ProfileActivity.this.videoCallItem.setScaleY(1.0f - value);
                ProfileActivity.this.videoCallItem.setAlpha(1.0f - value);
            }
            setScaleX(value);
            setScaleY(value);
            setAlpha(value);
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

        public boolean isIndicatorVisible() {
            return this.isIndicatorVisible;
        }

        public boolean isIndicatorFullyVisible() {
            return this.isIndicatorVisible && !this.animator.isRunning();
        }

        public void setIndicatorVisible(boolean indicatorVisible, float durationFactor) {
            if (indicatorVisible != this.isIndicatorVisible) {
                this.isIndicatorVisible = indicatorVisible;
                this.animator.cancel();
                float value = AndroidUtilities.lerp(this.animatorValues, this.animator.getAnimatedFraction());
                float f = 1.0f;
                if (durationFactor <= 0.0f) {
                    this.animator.setDuration(0);
                } else if (indicatorVisible) {
                    this.animator.setDuration((long) (((1.0f - value) * 250.0f) / durationFactor));
                } else {
                    this.animator.setDuration((long) ((250.0f * value) / durationFactor));
                }
                float[] fArr = this.animatorValues;
                fArr[0] = value;
                if (!indicatorVisible) {
                    f = 0.0f;
                }
                fArr[1] = f;
                this.animator.start();
            }
        }

        public void refreshVisibility(float durationFactor) {
            setIndicatorVisible(ProfileActivity.this.isPulledDown && ProfileActivity.this.avatarsViewPager.getRealCount() > 20, durationFactor);
        }

        /* access modifiers changed from: protected */
        public void onSizeChanged(int w, int h, int oldw, int oldh) {
            invalidateIndicatorRect(false);
        }

        /* access modifiers changed from: private */
        public void invalidateIndicatorRect(boolean pageChanged) {
            if (pageChanged) {
                ProfileActivity.this.overlaysView.saveCurrentPageProgress();
            }
            ProfileActivity.this.overlaysView.invalidate();
            float textWidth = this.textPaint.measureText(getCurrentTitle());
            this.indicatorRect.right = (float) (getMeasuredWidth() - AndroidUtilities.dp(54.0f));
            RectF rectF = this.indicatorRect;
            rectF.left = rectF.right - (AndroidUtilities.dpf2(16.0f) + textWidth);
            this.indicatorRect.top = (float) ((ProfileActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + AndroidUtilities.dp(15.0f));
            RectF rectF2 = this.indicatorRect;
            rectF2.bottom = rectF2.top + ((float) AndroidUtilities.dp(26.0f));
            setPivotX(this.indicatorRect.centerX());
            setPivotY(this.indicatorRect.centerY());
            invalidate();
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            float radius = AndroidUtilities.dpf2(12.0f);
            canvas.drawRoundRect(this.indicatorRect, radius, radius, this.backgroundPaint);
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

    public ProfileActivity(Bundle args) {
        this(args, (SharedMediaLayout.SharedMediaPreloader) null);
    }

    public ProfileActivity(Bundle args, SharedMediaLayout.SharedMediaPreloader preloader) {
        super(args);
        this.nameTextView = new SimpleTextView[2];
        this.onlineTextView = new SimpleTextView[2];
        this.isOnline = new boolean[1];
        this.headerShadowAlpha = 1.0f;
        this.participantsMap = new LongSparseArray<>();
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
        this.actionBarAnimationColorFrom = 0;
        this.navigationBarAnimationColorFrom = 0;
        this.HEADER_SHADOW = new AnimationProperties.FloatProperty<ProfileActivity>("headerShadow") {
            public void setValue(ProfileActivity object, float value) {
                ProfileActivity.this.headerShadowAlpha = value;
                ProfileActivity.this.topView.invalidate();
            }

            public Float get(ProfileActivity object) {
                return Float.valueOf(ProfileActivity.this.headerShadowAlpha);
            }
        };
        this.provider = new PhotoViewer.EmptyPhotoViewerProvider() {
            public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index, boolean needPreview) {
                TLRPC.Chat chat;
                if (fileLocation == null) {
                    return null;
                }
                TLRPC.FileLocation photoBig = null;
                if (ProfileActivity.this.userId != 0) {
                    TLRPC.User user = ProfileActivity.this.getMessagesController().getUser(Long.valueOf(ProfileActivity.this.userId));
                    if (!(user == null || user.photo == null || user.photo.photo_big == null)) {
                        photoBig = user.photo.photo_big;
                    }
                } else if (!(ProfileActivity.this.chatId == 0 || (chat = ProfileActivity.this.getMessagesController().getChat(Long.valueOf(ProfileActivity.this.chatId))) == null || chat.photo == null || chat.photo.photo_big == null)) {
                    photoBig = chat.photo.photo_big;
                }
                if (photoBig == null || photoBig.local_id != fileLocation.local_id || photoBig.volume_id != fileLocation.volume_id || photoBig.dc_id != fileLocation.dc_id) {
                    return null;
                }
                int[] coords = new int[2];
                ProfileActivity.this.avatarImage.getLocationInWindow(coords);
                PhotoViewer.PlaceProviderObject object = new PhotoViewer.PlaceProviderObject();
                boolean z = false;
                object.viewX = coords[0];
                object.viewY = coords[1] - (Build.VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight);
                object.parentView = ProfileActivity.this.avatarImage;
                object.imageReceiver = ProfileActivity.this.avatarImage.getImageReceiver();
                if (ProfileActivity.this.userId != 0) {
                    object.dialogId = ProfileActivity.this.userId;
                } else if (ProfileActivity.this.chatId != 0) {
                    object.dialogId = -ProfileActivity.this.chatId;
                }
                object.thumb = object.imageReceiver.getBitmapSafe();
                object.size = -1;
                object.radius = ProfileActivity.this.avatarImage.getImageReceiver().getRoundRadius();
                object.scale = ProfileActivity.this.avatarContainer.getScaleX();
                if (ProfileActivity.this.userId == ProfileActivity.this.getUserConfig().clientUserId) {
                    z = true;
                }
                object.canEdit = z;
                return object;
            }

            public void willHidePhotoViewer() {
                ProfileActivity.this.avatarImage.getImageReceiver().setVisible(true, true);
            }

            public void openPhotoForEdit(String file, String thumb, boolean isVideo) {
                ProfileActivity.this.imageUpdater.openPhotoForEdit(file, thumb, 0, isVideo);
            }
        };
        this.ACTIONBAR_HEADER_PROGRESS = new AnimationProperties.FloatProperty<ActionBar>("animationProgress") {
            public void setValue(ActionBar object, float value) {
                float unused = ProfileActivity.this.mediaHeaderAnimationProgress = value;
                ProfileActivity.this.topView.invalidate();
                int color1 = Theme.getColor("profile_title");
                int color2 = Theme.getColor("player_actionBarTitle");
                int c = AndroidUtilities.getOffsetColor(color1, color2, value, 1.0f);
                ProfileActivity.this.nameTextView[1].setTextColor(c);
                if (ProfileActivity.this.lockIconDrawable != null) {
                    ProfileActivity.this.lockIconDrawable.setColorFilter(c, PorterDuff.Mode.MULTIPLY);
                }
                if (ProfileActivity.this.scamDrawable != null) {
                    ProfileActivity.this.scamDrawable.setColor(AndroidUtilities.getOffsetColor(Theme.getColor("avatar_subtitleInProfileBlue"), color2, value, 1.0f));
                }
                ProfileActivity.this.actionBar.setItemsColor(AndroidUtilities.getOffsetColor(Theme.getColor("actionBarDefaultIcon"), Theme.getColor("windowBackgroundWhiteGrayText2"), value, 1.0f), false);
                ProfileActivity.this.actionBar.setItemsBackgroundColor(AndroidUtilities.getOffsetColor(Theme.getColor("avatar_actionBarSelectorBlue"), Theme.getColor("actionBarActionModeDefaultSelector"), value, 1.0f), false);
                ProfileActivity.this.topView.invalidate();
                ProfileActivity.this.otherItem.setIconColor(Theme.getColor("actionBarDefaultIcon"));
                ProfileActivity.this.callItem.setIconColor(Theme.getColor("actionBarDefaultIcon"));
                ProfileActivity.this.videoCallItem.setIconColor(Theme.getColor("actionBarDefaultIcon"));
                ProfileActivity.this.editItem.setIconColor(Theme.getColor("actionBarDefaultIcon"));
                if (ProfileActivity.this.verifiedDrawable != null) {
                    ProfileActivity.this.verifiedDrawable.setColorFilter(AndroidUtilities.getOffsetColor(Theme.getColor("profile_verifiedBackground"), Theme.getColor("player_actionBarTitle"), value, 1.0f), PorterDuff.Mode.MULTIPLY);
                }
                if (ProfileActivity.this.verifiedCheckDrawable != null) {
                    ProfileActivity.this.verifiedCheckDrawable.setColorFilter(AndroidUtilities.getOffsetColor(Theme.getColor("profile_verifiedCheck"), Theme.getColor("windowBackgroundWhite"), value, 1.0f), PorterDuff.Mode.MULTIPLY);
                }
                if (ProfileActivity.this.avatarsViewPagerIndicatorView.getSecondaryMenuItem() == null) {
                    return;
                }
                if (ProfileActivity.this.videoCallItemVisible || ProfileActivity.this.editItemVisible || ProfileActivity.this.callItemVisible) {
                    ProfileActivity profileActivity = ProfileActivity.this;
                    profileActivity.needLayoutText(Math.min(1.0f, profileActivity.extraHeight / ((float) AndroidUtilities.dp(88.0f))));
                }
            }

            public Float get(ActionBar object) {
                return Float.valueOf(ProfileActivity.this.mediaHeaderAnimationProgress);
            }
        };
        this.savedScrollPosition = -1;
        this.sharedMediaPreloader = preloader;
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
            TLRPC.User user = getMessagesController().getUser(Long.valueOf(this.userId));
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
                getMediaDataController().loadBotInfo(user.id, user.id, true, this.classGuid);
            }
            this.userInfo = getMessagesController().getUserFull(this.userId);
            getMessagesController().loadFullUser(getMessagesController().getUser(Long.valueOf(this.userId)), this.classGuid, true);
            this.participantsMap = null;
            if (UserObject.isUserSelf(user)) {
                ImageUpdater imageUpdater2 = new ImageUpdater(true);
                this.imageUpdater = imageUpdater2;
                imageUpdater2.setOpenWithFrontfaceCamera(true);
                this.imageUpdater.parentFragment = this;
                this.imageUpdater.setDelegate(this);
                getMediaDataController().checkFeaturedStickers();
                getMessagesController().loadSuggestedFilters();
                getMessagesController().loadUserInfo(getUserConfig().getCurrentUser(), true, this.classGuid);
            }
            this.actionBarAnimationColorFrom = this.arguments.getInt("actionBarColor", 0);
        } else if (this.chatId == 0) {
            return false;
        } else {
            TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(this.chatId));
            this.currentChat = chat;
            if (chat == null) {
                CountDownLatch countDownLatch = new CountDownLatch(1);
                getMessagesStorage().getStorageQueue().postRunnable(new ProfileActivity$$ExternalSyntheticLambda8(this, countDownLatch));
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

    /* renamed from: lambda$onFragmentCreate$0$org-telegram-ui-ProfileActivity  reason: not valid java name */
    public /* synthetic */ void m3707lambda$onFragmentCreate$0$orgtelegramuiProfileActivity(CountDownLatch countDownLatch) {
        this.currentChat = getMessagesStorage().getChat(this.chatId);
        countDownLatch.countDown();
    }

    /* access modifiers changed from: protected */
    public void setParentLayout(ActionBarLayout layout) {
        super.setParentLayout(layout);
        Activity activity = getParentActivity();
        if (activity != null && Build.VERSION.SDK_INT >= 21) {
            this.navigationBarAnimationColorFrom = activity.getWindow().getNavigationBarColor();
        }
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
        ActionBar actionBar = new ActionBar(context) {
            public boolean onTouchEvent(MotionEvent event) {
                ProfileActivity.this.avatarContainer.getHitRect(ProfileActivity.this.rect);
                if (ProfileActivity.this.rect.contains((int) event.getX(), (int) event.getY())) {
                    return false;
                }
                return super.onTouchEvent(event);
            }
        };
        boolean z = false;
        actionBar.setBackgroundColor(0);
        actionBar.setItemsBackgroundColor(AvatarDrawable.getButtonColorForId((this.userId != 0 || (ChatObject.isChannel(this.chatId, this.currentAccount) && !this.currentChat.megagroup)) ? 5 : this.chatId), false);
        actionBar.setItemsColor(Theme.getColor("actionBarDefaultIcon"), false);
        actionBar.setBackButtonDrawable(new BackDrawable(false));
        actionBar.setCastShadows(false);
        actionBar.setAddToContainer(false);
        actionBar.setClipContent(true);
        if (Build.VERSION.SDK_INT >= 21 && !AndroidUtilities.isTablet() && !this.inBubbleMode) {
            z = true;
        }
        actionBar.setOccupyStatusBar(z);
        return actionBar;
    }

    public View createView(Context context) {
        long did;
        int scrollTo;
        Object writeButtonTag;
        int scrollToPosition;
        String str;
        ViewGroup decorView;
        int rightMargin;
        Theme.createProfileResources(context);
        Theme.createChatResources(context, false);
        this.searchTransitionOffset = 0;
        this.searchTransitionProgress = 1.0f;
        this.searchMode = false;
        this.hasOwnBackground = true;
        this.extraHeight = (float) AndroidUtilities.dp(88.0f);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            /* JADX WARNING: type inference failed for: r6v12, types: [java.lang.Integer] */
            /* JADX WARNING: Multi-variable type inference failed */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onItemClick(int r23) {
                /*
                    r22 = this;
                    r1 = r22
                    r2 = r23
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    android.app.Activity r0 = r0.getParentActivity()
                    if (r0 != 0) goto L_0x000d
                    return
                L_0x000d:
                    r0 = -1
                    if (r2 != r0) goto L_0x0017
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    r0.finishFragment()
                    goto L_0x0860
                L_0x0017:
                    java.lang.String r3 = "dialogTextRed2"
                    r4 = 2131624692(0x7f0e02f4, float:1.887657E38)
                    java.lang.String r5 = "Cancel"
                    r6 = 0
                    r7 = 2
                    r8 = 0
                    r9 = 1
                    if (r2 != r7) goto L_0x0151
                    org.telegram.ui.ProfileActivity r7 = org.telegram.ui.ProfileActivity.this
                    org.telegram.messenger.MessagesController r7 = r7.getMessagesController()
                    org.telegram.ui.ProfileActivity r10 = org.telegram.ui.ProfileActivity.this
                    long r10 = r10.userId
                    java.lang.Long r10 = java.lang.Long.valueOf(r10)
                    org.telegram.tgnet.TLRPC$User r7 = r7.getUser(r10)
                    if (r7 != 0) goto L_0x003b
                    return
                L_0x003b:
                    org.telegram.ui.ProfileActivity r10 = org.telegram.ui.ProfileActivity.this
                    boolean r10 = r10.isBot
                    if (r10 == 0) goto L_0x009a
                    boolean r10 = org.telegram.messenger.MessagesController.isSupportUser(r7)
                    if (r10 == 0) goto L_0x004a
                    goto L_0x009a
                L_0x004a:
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    boolean r0 = r0.userBlocked
                    if (r0 != 0) goto L_0x0063
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                    org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                    long r3 = r3.userId
                    r0.blockPeer(r3)
                    goto L_0x014f
                L_0x0063:
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                    org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                    long r3 = r3.userId
                    r0.unblockPeer(r3)
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    org.telegram.messenger.SendMessagesHelper r8 = r0.getSendMessagesHelper()
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    long r10 = r0.userId
                    r12 = 0
                    r13 = 0
                    r14 = 0
                    r15 = 0
                    r16 = 0
                    r17 = 0
                    r18 = 0
                    r19 = 1
                    r20 = 0
                    r21 = 0
                    java.lang.String r9 = "/start"
                    r8.sendMessage(r9, r10, r12, r13, r14, r15, r16, r17, r18, r19, r20, r21)
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    r0.finishFragment()
                    goto L_0x014f
                L_0x009a:
                    org.telegram.ui.ProfileActivity r10 = org.telegram.ui.ProfileActivity.this
                    boolean r10 = r10.userBlocked
                    if (r10 == 0) goto L_0x00c4
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                    org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                    long r3 = r3.userId
                    r0.unblockPeer(r3)
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    boolean r0 = org.telegram.ui.Components.BulletinFactory.canShowBulletin(r0)
                    if (r0 == 0) goto L_0x014f
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    org.telegram.ui.Components.Bulletin r0 = org.telegram.ui.Components.BulletinFactory.createBanBulletin(r0, r8)
                    r0.show()
                    goto L_0x014f
                L_0x00c4:
                    org.telegram.ui.ProfileActivity r10 = org.telegram.ui.ProfileActivity.this
                    boolean r10 = r10.reportSpam
                    if (r10 == 0) goto L_0x00eb
                    org.telegram.ui.ProfileActivity r11 = org.telegram.ui.ProfileActivity.this
                    long r12 = r11.userId
                    r15 = 0
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    org.telegram.tgnet.TLRPC$EncryptedChat r16 = r0.currentEncryptedChat
                    r17 = 0
                    r18 = 0
                    org.telegram.ui.ProfileActivity$4$$ExternalSyntheticLambda6 r0 = new org.telegram.ui.ProfileActivity$4$$ExternalSyntheticLambda6
                    r0.<init>(r1)
                    r20 = 0
                    r14 = r7
                    r19 = r0
                    org.telegram.ui.Components.AlertsCreator.showBlockReportSpamAlert(r11, r12, r14, r15, r16, r17, r18, r19, r20)
                    goto L_0x014f
                L_0x00eb:
                    org.telegram.ui.ActionBar.AlertDialog$Builder r10 = new org.telegram.ui.ActionBar.AlertDialog$Builder
                    org.telegram.ui.ProfileActivity r11 = org.telegram.ui.ProfileActivity.this
                    android.app.Activity r11 = r11.getParentActivity()
                    r10.<init>((android.content.Context) r11)
                    r11 = 2131624586(0x7f0e028a, float:1.8876356E38)
                    java.lang.String r12 = "BlockUser"
                    java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
                    r10.setTitle(r11)
                    r11 = 2131624347(0x7f0e019b, float:1.8875871E38)
                    java.lang.Object[] r9 = new java.lang.Object[r9]
                    java.lang.String r12 = r7.first_name
                    java.lang.String r13 = r7.last_name
                    java.lang.String r12 = org.telegram.messenger.ContactsController.formatName(r12, r13)
                    r9[r8] = r12
                    java.lang.String r8 = "AreYouSureBlockContact2"
                    java.lang.String r8 = org.telegram.messenger.LocaleController.formatString(r8, r11, r9)
                    android.text.SpannableStringBuilder r8 = org.telegram.messenger.AndroidUtilities.replaceTags(r8)
                    r10.setMessage(r8)
                    r8 = 2131624585(0x7f0e0289, float:1.8876354E38)
                    java.lang.String r9 = "BlockContact"
                    java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
                    org.telegram.ui.ProfileActivity$4$$ExternalSyntheticLambda0 r9 = new org.telegram.ui.ProfileActivity$4$$ExternalSyntheticLambda0
                    r9.<init>(r1)
                    r10.setPositiveButton(r8, r9)
                    java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                    r10.setNegativeButton(r4, r6)
                    org.telegram.ui.ActionBar.AlertDialog r4 = r10.create()
                    org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                    r5.showDialog(r4)
                    android.view.View r0 = r4.getButton(r0)
                    android.widget.TextView r0 = (android.widget.TextView) r0
                    if (r0 == 0) goto L_0x014e
                    int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                    r0.setTextColor(r3)
                L_0x014e:
                L_0x014f:
                    goto L_0x0860
                L_0x0151:
                    java.lang.String r10 = "user_id"
                    if (r2 != r9) goto L_0x0184
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                    org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                    long r3 = r3.userId
                    java.lang.Long r3 = java.lang.Long.valueOf(r3)
                    org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r3)
                    android.os.Bundle r3 = new android.os.Bundle
                    r3.<init>()
                    long r4 = r0.id
                    r3.putLong(r10, r4)
                    java.lang.String r4 = "addContact"
                    r3.putBoolean(r4, r9)
                    org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                    org.telegram.ui.ContactAddActivity r5 = new org.telegram.ui.ContactAddActivity
                    r5.<init>(r3)
                    r4.presentFragment(r5)
                    goto L_0x0860
                L_0x0184:
                    java.lang.String r11 = "dialogsType"
                    java.lang.String r12 = "onlySelect"
                    r13 = 3
                    if (r2 != r13) goto L_0x01c3
                    android.os.Bundle r0 = new android.os.Bundle
                    r0.<init>()
                    r0.putBoolean(r12, r9)
                    r0.putInt(r11, r13)
                    r3 = 2131627704(0x7f0e0eb8, float:1.888268E38)
                    java.lang.String r4 = "SendContactToText"
                    java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                    java.lang.String r4 = "selectAlertString"
                    r0.putString(r4, r3)
                    r3 = 2131627703(0x7f0e0eb7, float:1.8882678E38)
                    java.lang.String r4 = "SendContactToGroupText"
                    java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                    java.lang.String r4 = "selectAlertStringGroup"
                    r0.putString(r4, r3)
                    org.telegram.ui.DialogsActivity r3 = new org.telegram.ui.DialogsActivity
                    r3.<init>(r0)
                    org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                    r3.setDelegate(r4)
                    org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                    r4.presentFragment(r3)
                    goto L_0x0860
                L_0x01c3:
                    r13 = 4
                    if (r2 != r13) goto L_0x01e0
                    android.os.Bundle r0 = new android.os.Bundle
                    r0.<init>()
                    org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                    long r3 = r3.userId
                    r0.putLong(r10, r3)
                    org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                    org.telegram.ui.ContactAddActivity r4 = new org.telegram.ui.ContactAddActivity
                    r4.<init>(r0)
                    r3.presentFragment(r4)
                    goto L_0x0860
                L_0x01e0:
                    r10 = 5
                    r14 = 2131625188(0x7f0e04e4, float:1.8877577E38)
                    java.lang.String r15 = "Delete"
                    if (r2 != r10) goto L_0x0258
                    org.telegram.ui.ProfileActivity r7 = org.telegram.ui.ProfileActivity.this
                    org.telegram.messenger.MessagesController r7 = r7.getMessagesController()
                    org.telegram.ui.ProfileActivity r8 = org.telegram.ui.ProfileActivity.this
                    long r8 = r8.userId
                    java.lang.Long r8 = java.lang.Long.valueOf(r8)
                    org.telegram.tgnet.TLRPC$User r7 = r7.getUser(r8)
                    if (r7 == 0) goto L_0x0257
                    org.telegram.ui.ProfileActivity r8 = org.telegram.ui.ProfileActivity.this
                    android.app.Activity r8 = r8.getParentActivity()
                    if (r8 != 0) goto L_0x0207
                    goto L_0x0257
                L_0x0207:
                    org.telegram.ui.ActionBar.AlertDialog$Builder r8 = new org.telegram.ui.ActionBar.AlertDialog$Builder
                    org.telegram.ui.ProfileActivity r9 = org.telegram.ui.ProfileActivity.this
                    android.app.Activity r9 = r9.getParentActivity()
                    r8.<init>((android.content.Context) r9)
                    r9 = 2131625217(0x7f0e0501, float:1.8877636E38)
                    java.lang.String r10 = "DeleteContact"
                    java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
                    r8.setTitle(r9)
                    r9 = 2131624362(0x7f0e01aa, float:1.8875902E38)
                    java.lang.String r10 = "AreYouSureDeleteContact"
                    java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
                    r8.setMessage(r9)
                    java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r15, r14)
                    org.telegram.ui.ProfileActivity$4$$ExternalSyntheticLambda3 r10 = new org.telegram.ui.ProfileActivity$4$$ExternalSyntheticLambda3
                    r10.<init>(r1, r7)
                    r8.setPositiveButton(r9, r10)
                    java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                    r8.setNegativeButton(r4, r6)
                    org.telegram.ui.ActionBar.AlertDialog r4 = r8.create()
                    org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                    r5.showDialog(r4)
                    android.view.View r0 = r4.getButton(r0)
                    android.widget.TextView r0 = (android.widget.TextView) r0
                    if (r0 == 0) goto L_0x0255
                    int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                    r0.setTextColor(r3)
                L_0x0255:
                    goto L_0x0860
                L_0x0257:
                    return
                L_0x0258:
                    r10 = 7
                    if (r2 != r10) goto L_0x0262
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    r0.leaveChatPressed()
                    goto L_0x0860
                L_0x0262:
                    r10 = 12
                    java.lang.String r0 = "chat_id"
                    if (r2 != r10) goto L_0x028b
                    android.os.Bundle r3 = new android.os.Bundle
                    r3.<init>()
                    org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                    long r4 = r4.chatId
                    r3.putLong(r0, r4)
                    org.telegram.ui.ChatEditActivity r0 = new org.telegram.ui.ChatEditActivity
                    r0.<init>(r3)
                    org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                    org.telegram.tgnet.TLRPC$ChatFull r4 = r4.chatInfo
                    r0.setInfo(r4)
                    org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                    r4.presentFragment(r0)
                    goto L_0x0860
                L_0x028b:
                    r10 = 9
                    if (r2 != r10) goto L_0x02df
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                    org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                    long r3 = r3.userId
                    java.lang.Long r3 = java.lang.Long.valueOf(r3)
                    org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r3)
                    if (r0 != 0) goto L_0x02a6
                    return
                L_0x02a6:
                    android.os.Bundle r3 = new android.os.Bundle
                    r3.<init>()
                    r3.putBoolean(r12, r9)
                    r3.putInt(r11, r7)
                    r4 = 2131624245(0x7f0e0135, float:1.8875664E38)
                    java.lang.Object[] r5 = new java.lang.Object[r7]
                    java.lang.String r6 = org.telegram.messenger.UserObject.getUserName(r0)
                    r5[r8] = r6
                    java.lang.String r6 = "%1$s"
                    r5[r9] = r6
                    java.lang.String r6 = "AddToTheGroupAlertText"
                    java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
                    java.lang.String r5 = "addToGroupAlertString"
                    r3.putString(r5, r4)
                    org.telegram.ui.DialogsActivity r4 = new org.telegram.ui.DialogsActivity
                    r4.<init>(r3)
                    org.telegram.ui.ProfileActivity$4$$ExternalSyntheticLambda8 r5 = new org.telegram.ui.ProfileActivity$4$$ExternalSyntheticLambda8
                    r5.<init>(r1, r0)
                    r4.setDelegate(r5)
                    org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                    r5.presentFragment(r4)
                    goto L_0x0860
                L_0x02df:
                    r10 = 10
                    r11 = 0
                    if (r2 != r10) goto L_0x0446
                    r0 = 0
                    org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this     // Catch:{ Exception -> 0x0440 }
                    long r3 = r3.userId     // Catch:{ Exception -> 0x0440 }
                    java.lang.String r5 = "https://"
                    java.lang.String r6 = "/%s"
                    int r10 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1))
                    if (r10 == 0) goto L_0x0383
                    org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this     // Catch:{ Exception -> 0x0440 }
                    org.telegram.messenger.MessagesController r3 = r3.getMessagesController()     // Catch:{ Exception -> 0x0440 }
                    org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this     // Catch:{ Exception -> 0x0440 }
                    long r10 = r4.userId     // Catch:{ Exception -> 0x0440 }
                    java.lang.Long r4 = java.lang.Long.valueOf(r10)     // Catch:{ Exception -> 0x0440 }
                    org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)     // Catch:{ Exception -> 0x0440 }
                    if (r3 != 0) goto L_0x030b
                    return
                L_0x030b:
                    org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this     // Catch:{ Exception -> 0x0440 }
                    org.telegram.tgnet.TLRPC$BotInfo r4 = r4.botInfo     // Catch:{ Exception -> 0x0440 }
                    if (r4 == 0) goto L_0x035c
                    org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this     // Catch:{ Exception -> 0x0440 }
                    org.telegram.tgnet.TLRPC$UserFull r4 = r4.userInfo     // Catch:{ Exception -> 0x0440 }
                    if (r4 == 0) goto L_0x035c
                    org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this     // Catch:{ Exception -> 0x0440 }
                    org.telegram.tgnet.TLRPC$UserFull r4 = r4.userInfo     // Catch:{ Exception -> 0x0440 }
                    java.lang.String r4 = r4.about     // Catch:{ Exception -> 0x0440 }
                    boolean r4 = android.text.TextUtils.isEmpty(r4)     // Catch:{ Exception -> 0x0440 }
                    if (r4 != 0) goto L_0x035c
                    java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0440 }
                    r4.<init>()     // Catch:{ Exception -> 0x0440 }
                    java.lang.String r5 = "%s https://"
                    r4.append(r5)     // Catch:{ Exception -> 0x0440 }
                    org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this     // Catch:{ Exception -> 0x0440 }
                    org.telegram.messenger.MessagesController r5 = r5.getMessagesController()     // Catch:{ Exception -> 0x0440 }
                    java.lang.String r5 = r5.linkPrefix     // Catch:{ Exception -> 0x0440 }
                    r4.append(r5)     // Catch:{ Exception -> 0x0440 }
                    r4.append(r6)     // Catch:{ Exception -> 0x0440 }
                    java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0440 }
                    java.lang.Object[] r5 = new java.lang.Object[r7]     // Catch:{ Exception -> 0x0440 }
                    org.telegram.ui.ProfileActivity r6 = org.telegram.ui.ProfileActivity.this     // Catch:{ Exception -> 0x0440 }
                    org.telegram.tgnet.TLRPC$UserFull r6 = r6.userInfo     // Catch:{ Exception -> 0x0440 }
                    java.lang.String r6 = r6.about     // Catch:{ Exception -> 0x0440 }
                    r5[r8] = r6     // Catch:{ Exception -> 0x0440 }
                    java.lang.String r6 = r3.username     // Catch:{ Exception -> 0x0440 }
                    r5[r9] = r6     // Catch:{ Exception -> 0x0440 }
                    java.lang.String r4 = java.lang.String.format(r4, r5)     // Catch:{ Exception -> 0x0440 }
                    r0 = r4
                    goto L_0x0412
                L_0x035c:
                    java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0440 }
                    r4.<init>()     // Catch:{ Exception -> 0x0440 }
                    r4.append(r5)     // Catch:{ Exception -> 0x0440 }
                    org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this     // Catch:{ Exception -> 0x0440 }
                    org.telegram.messenger.MessagesController r5 = r5.getMessagesController()     // Catch:{ Exception -> 0x0440 }
                    java.lang.String r5 = r5.linkPrefix     // Catch:{ Exception -> 0x0440 }
                    r4.append(r5)     // Catch:{ Exception -> 0x0440 }
                    r4.append(r6)     // Catch:{ Exception -> 0x0440 }
                    java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0440 }
                    java.lang.Object[] r5 = new java.lang.Object[r9]     // Catch:{ Exception -> 0x0440 }
                    java.lang.String r6 = r3.username     // Catch:{ Exception -> 0x0440 }
                    r5[r8] = r6     // Catch:{ Exception -> 0x0440 }
                    java.lang.String r4 = java.lang.String.format(r4, r5)     // Catch:{ Exception -> 0x0440 }
                    r0 = r4
                    goto L_0x0412
                L_0x0383:
                    org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this     // Catch:{ Exception -> 0x0440 }
                    long r3 = r3.chatId     // Catch:{ Exception -> 0x0440 }
                    int r10 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1))
                    if (r10 == 0) goto L_0x0412
                    org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this     // Catch:{ Exception -> 0x0440 }
                    org.telegram.messenger.MessagesController r3 = r3.getMessagesController()     // Catch:{ Exception -> 0x0440 }
                    org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this     // Catch:{ Exception -> 0x0440 }
                    long r10 = r4.chatId     // Catch:{ Exception -> 0x0440 }
                    java.lang.Long r4 = java.lang.Long.valueOf(r10)     // Catch:{ Exception -> 0x0440 }
                    org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r4)     // Catch:{ Exception -> 0x0440 }
                    if (r3 != 0) goto L_0x03a4
                    return
                L_0x03a4:
                    org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this     // Catch:{ Exception -> 0x0440 }
                    org.telegram.tgnet.TLRPC$ChatFull r4 = r4.chatInfo     // Catch:{ Exception -> 0x0440 }
                    if (r4 == 0) goto L_0x03ec
                    org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this     // Catch:{ Exception -> 0x0440 }
                    org.telegram.tgnet.TLRPC$ChatFull r4 = r4.chatInfo     // Catch:{ Exception -> 0x0440 }
                    java.lang.String r4 = r4.about     // Catch:{ Exception -> 0x0440 }
                    boolean r4 = android.text.TextUtils.isEmpty(r4)     // Catch:{ Exception -> 0x0440 }
                    if (r4 != 0) goto L_0x03ec
                    java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0440 }
                    r4.<init>()     // Catch:{ Exception -> 0x0440 }
                    java.lang.String r5 = "%s\nhttps://"
                    r4.append(r5)     // Catch:{ Exception -> 0x0440 }
                    org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this     // Catch:{ Exception -> 0x0440 }
                    org.telegram.messenger.MessagesController r5 = r5.getMessagesController()     // Catch:{ Exception -> 0x0440 }
                    java.lang.String r5 = r5.linkPrefix     // Catch:{ Exception -> 0x0440 }
                    r4.append(r5)     // Catch:{ Exception -> 0x0440 }
                    r4.append(r6)     // Catch:{ Exception -> 0x0440 }
                    java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0440 }
                    java.lang.Object[] r5 = new java.lang.Object[r7]     // Catch:{ Exception -> 0x0440 }
                    org.telegram.ui.ProfileActivity r6 = org.telegram.ui.ProfileActivity.this     // Catch:{ Exception -> 0x0440 }
                    org.telegram.tgnet.TLRPC$ChatFull r6 = r6.chatInfo     // Catch:{ Exception -> 0x0440 }
                    java.lang.String r6 = r6.about     // Catch:{ Exception -> 0x0440 }
                    r5[r8] = r6     // Catch:{ Exception -> 0x0440 }
                    java.lang.String r6 = r3.username     // Catch:{ Exception -> 0x0440 }
                    r5[r9] = r6     // Catch:{ Exception -> 0x0440 }
                    java.lang.String r4 = java.lang.String.format(r4, r5)     // Catch:{ Exception -> 0x0440 }
                    r0 = r4
                    goto L_0x0413
                L_0x03ec:
                    java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0440 }
                    r4.<init>()     // Catch:{ Exception -> 0x0440 }
                    r4.append(r5)     // Catch:{ Exception -> 0x0440 }
                    org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this     // Catch:{ Exception -> 0x0440 }
                    org.telegram.messenger.MessagesController r5 = r5.getMessagesController()     // Catch:{ Exception -> 0x0440 }
                    java.lang.String r5 = r5.linkPrefix     // Catch:{ Exception -> 0x0440 }
                    r4.append(r5)     // Catch:{ Exception -> 0x0440 }
                    r4.append(r6)     // Catch:{ Exception -> 0x0440 }
                    java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0440 }
                    java.lang.Object[] r5 = new java.lang.Object[r9]     // Catch:{ Exception -> 0x0440 }
                    java.lang.String r6 = r3.username     // Catch:{ Exception -> 0x0440 }
                    r5[r8] = r6     // Catch:{ Exception -> 0x0440 }
                    java.lang.String r4 = java.lang.String.format(r4, r5)     // Catch:{ Exception -> 0x0440 }
                    r0 = r4
                    goto L_0x0413
                L_0x0412:
                L_0x0413:
                    boolean r3 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0440 }
                    if (r3 == 0) goto L_0x041a
                    return
                L_0x041a:
                    android.content.Intent r3 = new android.content.Intent     // Catch:{ Exception -> 0x0440 }
                    java.lang.String r4 = "android.intent.action.SEND"
                    r3.<init>(r4)     // Catch:{ Exception -> 0x0440 }
                    java.lang.String r4 = "text/plain"
                    r3.setType(r4)     // Catch:{ Exception -> 0x0440 }
                    java.lang.String r4 = "android.intent.extra.TEXT"
                    r3.putExtra(r4, r0)     // Catch:{ Exception -> 0x0440 }
                    org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this     // Catch:{ Exception -> 0x0440 }
                    java.lang.String r5 = "BotShare"
                    r6 = 2131624622(0x7f0e02ae, float:1.8876429E38)
                    java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r6)     // Catch:{ Exception -> 0x0440 }
                    android.content.Intent r5 = android.content.Intent.createChooser(r3, r5)     // Catch:{ Exception -> 0x0440 }
                    r6 = 500(0x1f4, float:7.0E-43)
                    r4.startActivityForResult(r5, r6)     // Catch:{ Exception -> 0x0440 }
                    goto L_0x0444
                L_0x0440:
                    r0 = move-exception
                    org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                L_0x0444:
                    goto L_0x0860
                L_0x0446:
                    r10 = 14
                    if (r2 != r10) goto L_0x0493
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this     // Catch:{ Exception -> 0x048d }
                    org.telegram.tgnet.TLRPC$EncryptedChat r0 = r0.currentEncryptedChat     // Catch:{ Exception -> 0x048d }
                    if (r0 == 0) goto L_0x0460
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this     // Catch:{ Exception -> 0x048d }
                    org.telegram.tgnet.TLRPC$EncryptedChat r0 = r0.currentEncryptedChat     // Catch:{ Exception -> 0x048d }
                    int r0 = r0.id     // Catch:{ Exception -> 0x048d }
                    long r3 = (long) r0     // Catch:{ Exception -> 0x048d }
                    long r3 = org.telegram.messenger.DialogObject.makeEncryptedDialogId(r3)     // Catch:{ Exception -> 0x048d }
                    goto L_0x0482
                L_0x0460:
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this     // Catch:{ Exception -> 0x048d }
                    long r3 = r0.userId     // Catch:{ Exception -> 0x048d }
                    int r0 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1))
                    if (r0 == 0) goto L_0x0471
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this     // Catch:{ Exception -> 0x048d }
                    long r3 = r0.userId     // Catch:{ Exception -> 0x048d }
                    goto L_0x0482
                L_0x0471:
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this     // Catch:{ Exception -> 0x048d }
                    long r3 = r0.chatId     // Catch:{ Exception -> 0x048d }
                    int r0 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1))
                    if (r0 == 0) goto L_0x048c
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this     // Catch:{ Exception -> 0x048d }
                    long r3 = r0.chatId     // Catch:{ Exception -> 0x048d }
                    long r3 = -r3
                L_0x0482:
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this     // Catch:{ Exception -> 0x048d }
                    org.telegram.messenger.MediaDataController r0 = r0.getMediaDataController()     // Catch:{ Exception -> 0x048d }
                    r0.installShortcut(r3)     // Catch:{ Exception -> 0x048d }
                    goto L_0x0491
                L_0x048c:
                    return
                L_0x048d:
                    r0 = move-exception
                    org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                L_0x0491:
                    goto L_0x0860
                L_0x0493:
                    r10 = 15
                    r11 = 16
                    if (r2 == r10) goto L_0x07c5
                    if (r2 != r11) goto L_0x049d
                    goto L_0x07c5
                L_0x049d:
                    r10 = 17
                    if (r2 != r10) goto L_0x04ce
                    android.os.Bundle r3 = new android.os.Bundle
                    r3.<init>()
                    org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                    long r4 = r4.chatId
                    r3.putLong(r0, r4)
                    java.lang.String r0 = "type"
                    r3.putInt(r0, r7)
                    java.lang.String r0 = "open_search"
                    r3.putBoolean(r0, r9)
                    org.telegram.ui.ChatUsersActivity r0 = new org.telegram.ui.ChatUsersActivity
                    r0.<init>(r3)
                    org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                    org.telegram.tgnet.TLRPC$ChatFull r4 = r4.chatInfo
                    r0.setInfo(r4)
                    org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                    r4.presentFragment(r0)
                    goto L_0x0860
                L_0x04ce:
                    r10 = 18
                    if (r2 != r10) goto L_0x04d9
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    r0.openAddMember()
                    goto L_0x0860
                L_0x04d9:
                    r10 = 19
                    if (r2 != r10) goto L_0x0512
                    org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                    org.telegram.messenger.MessagesController r3 = r3.getMessagesController()
                    org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                    long r4 = r4.chatId
                    java.lang.Long r4 = java.lang.Long.valueOf(r4)
                    org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r4)
                    android.os.Bundle r4 = new android.os.Bundle
                    r4.<init>()
                    org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                    long r5 = r5.chatId
                    r4.putLong(r0, r5)
                    boolean r0 = r3.megagroup
                    java.lang.String r5 = "is_megagroup"
                    r4.putBoolean(r5, r0)
                    org.telegram.ui.StatisticActivity r0 = new org.telegram.ui.StatisticActivity
                    r0.<init>(r4)
                    org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                    r5.presentFragment(r0)
                    goto L_0x0860
                L_0x0512:
                    r0 = 22
                    if (r2 != r0) goto L_0x051d
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    r0.openDiscussion()
                    goto L_0x0860
                L_0x051d:
                    r10 = 20
                    if (r2 != r10) goto L_0x0567
                    org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
                    org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                    android.app.Activity r3 = r3.getParentActivity()
                    r0.<init>((android.content.Context) r3)
                    r3 = 2131624386(0x7f0e01c2, float:1.887595E38)
                    java.lang.String r7 = "AreYouSureSecretChatTitle"
                    java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r7, r3)
                    r0.setTitle(r3)
                    r3 = 2131624385(0x7f0e01c1, float:1.8875948E38)
                    java.lang.String r7 = "AreYouSureSecretChat"
                    java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r7, r3)
                    r0.setMessage(r3)
                    r3 = 2131627918(0x7f0e0f8e, float:1.8883114E38)
                    java.lang.String r7 = "Start"
                    java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r7, r3)
                    org.telegram.ui.ProfileActivity$4$$ExternalSyntheticLambda1 r7 = new org.telegram.ui.ProfileActivity$4$$ExternalSyntheticLambda1
                    r7.<init>(r1)
                    r0.setPositiveButton(r3, r7)
                    java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r4)
                    r0.setNegativeButton(r3, r6)
                    org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                    org.telegram.ui.ActionBar.AlertDialog r4 = r0.create()
                    r3.showDialog(r4)
                    goto L_0x0860
                L_0x0567:
                    r10 = 21
                    if (r2 != r10) goto L_0x05e4
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    android.app.Activity r0 = r0.getParentActivity()
                    if (r0 != 0) goto L_0x0574
                    return
                L_0x0574:
                    int r0 = android.os.Build.VERSION.SDK_INT
                    r3 = 23
                    if (r0 < r3) goto L_0x05a0
                    int r0 = android.os.Build.VERSION.SDK_INT
                    r3 = 28
                    if (r0 <= r3) goto L_0x0584
                    boolean r0 = org.telegram.messenger.BuildVars.NO_SCOPED_STORAGE
                    if (r0 == 0) goto L_0x05a0
                L_0x0584:
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    android.app.Activity r0 = r0.getParentActivity()
                    java.lang.String r3 = "android.permission.WRITE_EXTERNAL_STORAGE"
                    int r0 = r0.checkSelfPermission(r3)
                    if (r0 == 0) goto L_0x05a0
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    android.app.Activity r0 = r0.getParentActivity()
                    java.lang.String[] r4 = new java.lang.String[r9]
                    r4[r8] = r3
                    r0.requestPermissions(r4, r13)
                    return
                L_0x05a0:
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    org.telegram.ui.Components.ProfileGalleryView r0 = r0.avatarsViewPager
                    org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                    org.telegram.ui.Components.ProfileGalleryView r3 = r3.avatarsViewPager
                    int r3 = r3.getRealPosition()
                    org.telegram.messenger.ImageLocation r0 = r0.getImageLocation(r3)
                    if (r0 != 0) goto L_0x05b7
                    return
                L_0x05b7:
                    int r3 = r0.imageType
                    if (r3 != r7) goto L_0x05bc
                    r8 = 1
                L_0x05bc:
                    r3 = r8
                    org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r4 = r0.location
                    if (r3 == 0) goto L_0x05c3
                    java.lang.String r6 = "mp4"
                L_0x05c3:
                    java.io.File r4 = org.telegram.messenger.FileLoader.getPathToAttach(r4, r6, r9)
                    boolean r5 = r4.exists()
                    if (r5 == 0) goto L_0x05e2
                    java.lang.String r6 = r4.toString()
                    org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                    android.app.Activity r7 = r5.getParentActivity()
                    r8 = 0
                    r9 = 0
                    r10 = 0
                    org.telegram.ui.ProfileActivity$4$$ExternalSyntheticLambda5 r11 = new org.telegram.ui.ProfileActivity$4$$ExternalSyntheticLambda5
                    r11.<init>(r1, r3)
                    org.telegram.messenger.MediaController.saveFile(r6, r7, r8, r9, r10, r11)
                L_0x05e2:
                    goto L_0x0860
                L_0x05e4:
                    r10 = 30
                    if (r2 != r10) goto L_0x05f4
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    org.telegram.ui.ChangeNameActivity r3 = new org.telegram.ui.ChangeNameActivity
                    r3.<init>()
                    r0.presentFragment(r3)
                    goto L_0x0860
                L_0x05f4:
                    r10 = 31
                    if (r2 != r10) goto L_0x0604
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    org.telegram.ui.LogoutActivity r3 = new org.telegram.ui.LogoutActivity
                    r3.<init>()
                    r0.presentFragment(r3)
                    goto L_0x0860
                L_0x0604:
                    r10 = 33
                    if (r2 != r10) goto L_0x06d1
                    org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                    org.telegram.ui.Components.ProfileGalleryView r3 = r3.avatarsViewPager
                    int r3 = r3.getRealPosition()
                    org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                    org.telegram.ui.Components.ProfileGalleryView r4 = r4.avatarsViewPager
                    org.telegram.tgnet.TLRPC$Photo r4 = r4.getPhoto(r3)
                    if (r4 != 0) goto L_0x061f
                    return
                L_0x061f:
                    org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                    org.telegram.ui.Components.ProfileGalleryView r5 = r5.avatarsViewPager
                    r5.startMovePhotoToBegin(r3)
                    org.telegram.tgnet.TLRPC$TL_photos_updateProfilePhoto r5 = new org.telegram.tgnet.TLRPC$TL_photos_updateProfilePhoto
                    r5.<init>()
                    org.telegram.tgnet.TLRPC$TL_inputPhoto r7 = new org.telegram.tgnet.TLRPC$TL_inputPhoto
                    r7.<init>()
                    r5.id = r7
                    org.telegram.tgnet.TLRPC$InputPhoto r7 = r5.id
                    long r10 = r4.id
                    r7.id = r10
                    org.telegram.tgnet.TLRPC$InputPhoto r7 = r5.id
                    long r10 = r4.access_hash
                    r7.access_hash = r10
                    org.telegram.tgnet.TLRPC$InputPhoto r7 = r5.id
                    byte[] r10 = r4.file_reference
                    r7.file_reference = r10
                    org.telegram.ui.ProfileActivity r7 = org.telegram.ui.ProfileActivity.this
                    org.telegram.messenger.UserConfig r7 = r7.getUserConfig()
                    org.telegram.ui.ProfileActivity r10 = org.telegram.ui.ProfileActivity.this
                    org.telegram.tgnet.ConnectionsManager r10 = r10.getConnectionsManager()
                    org.telegram.ui.ProfileActivity$4$$ExternalSyntheticLambda7 r11 = new org.telegram.ui.ProfileActivity$4$$ExternalSyntheticLambda7
                    r11.<init>(r1, r7, r4)
                    r10.sendRequest(r5, r11)
                    org.telegram.ui.ProfileActivity r10 = org.telegram.ui.ProfileActivity.this
                    org.telegram.ui.Components.UndoView r10 = r10.undoView
                    org.telegram.ui.ProfileActivity r11 = org.telegram.ui.ProfileActivity.this
                    long r11 = r11.userId
                    java.util.ArrayList<org.telegram.tgnet.TLRPC$VideoSize> r13 = r4.video_sizes
                    boolean r13 = r13.isEmpty()
                    if (r13 == 0) goto L_0x066f
                    goto L_0x0673
                L_0x066f:
                    java.lang.Integer r6 = java.lang.Integer.valueOf(r9)
                L_0x0673:
                    r10.showWithAction((long) r11, (int) r0, (java.lang.Object) r6)
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                    long r10 = r7.clientUserId
                    java.lang.Long r6 = java.lang.Long.valueOf(r10)
                    org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r6)
                    java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r6 = r4.sizes
                    r10 = 800(0x320, float:1.121E-42)
                    org.telegram.tgnet.TLRPC$PhotoSize r6 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r6, r10)
                    if (r0 == 0) goto L_0x06c6
                    java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r10 = r4.sizes
                    r11 = 90
                    org.telegram.tgnet.TLRPC$PhotoSize r10 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r10, r11)
                    org.telegram.tgnet.TLRPC$UserProfilePhoto r11 = r0.photo
                    long r12 = r4.id
                    r11.photo_id = r12
                    org.telegram.tgnet.TLRPC$UserProfilePhoto r11 = r0.photo
                    org.telegram.tgnet.TLRPC$FileLocation r12 = r10.location
                    r11.photo_small = r12
                    org.telegram.tgnet.TLRPC$UserProfilePhoto r11 = r0.photo
                    org.telegram.tgnet.TLRPC$FileLocation r12 = r6.location
                    r11.photo_big = r12
                    r7.setCurrentUser(r0)
                    r7.saveConfig(r9)
                    org.telegram.ui.ProfileActivity r9 = org.telegram.ui.ProfileActivity.this
                    int r9 = r9.currentAccount
                    org.telegram.messenger.NotificationCenter r9 = org.telegram.messenger.NotificationCenter.getInstance(r9)
                    int r11 = org.telegram.messenger.NotificationCenter.mainUserInfoChanged
                    java.lang.Object[] r8 = new java.lang.Object[r8]
                    r9.postNotificationName(r11, r8)
                    org.telegram.ui.ProfileActivity r8 = org.telegram.ui.ProfileActivity.this
                    r8.updateProfileData()
                L_0x06c6:
                    org.telegram.ui.ProfileActivity r8 = org.telegram.ui.ProfileActivity.this
                    org.telegram.ui.Components.ProfileGalleryView r8 = r8.avatarsViewPager
                    r8.commitMoveToBegin()
                    goto L_0x0860
                L_0x06d1:
                    r0 = 34
                    if (r2 != r0) goto L_0x072c
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    org.telegram.ui.Components.ProfileGalleryView r0 = r0.avatarsViewPager
                    int r0 = r0.getRealPosition()
                    org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                    org.telegram.ui.Components.ProfileGalleryView r3 = r3.avatarsViewPager
                    org.telegram.messenger.ImageLocation r3 = r3.getImageLocation(r0)
                    if (r3 != 0) goto L_0x06ec
                    return
                L_0x06ec:
                    org.telegram.tgnet.TLRPC$FileLocation r4 = org.telegram.ui.PhotoViewer.getFileLocation(r3)
                    java.lang.String r5 = org.telegram.ui.PhotoViewer.getFileLocationExt(r3)
                    java.io.File r4 = org.telegram.messenger.FileLoader.getPathToAttach(r4, r5, r9)
                    int r5 = r3.imageType
                    if (r5 != r7) goto L_0x06fe
                    r5 = 1
                    goto L_0x06ff
                L_0x06fe:
                    r5 = 0
                L_0x06ff:
                    if (r5 == 0) goto L_0x071c
                    org.telegram.ui.ProfileActivity r6 = org.telegram.ui.ProfileActivity.this
                    org.telegram.ui.Components.ProfileGalleryView r6 = r6.avatarsViewPager
                    org.telegram.messenger.ImageLocation r6 = r6.getRealImageLocation(r0)
                    org.telegram.tgnet.TLRPC$FileLocation r7 = org.telegram.ui.PhotoViewer.getFileLocation(r6)
                    java.lang.String r10 = org.telegram.ui.PhotoViewer.getFileLocationExt(r6)
                    java.io.File r7 = org.telegram.messenger.FileLoader.getPathToAttach(r7, r10, r9)
                    java.lang.String r6 = r7.getAbsolutePath()
                    goto L_0x071d
                L_0x071c:
                    r6 = 0
                L_0x071d:
                    org.telegram.ui.ProfileActivity r7 = org.telegram.ui.ProfileActivity.this
                    org.telegram.ui.Components.ImageUpdater r7 = r7.imageUpdater
                    java.lang.String r9 = r4.getAbsolutePath()
                    r7.openPhotoForEdit(r9, r6, r8, r5)
                    goto L_0x0860
                L_0x072c:
                    r0 = 35
                    if (r2 != r0) goto L_0x07b8
                    org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
                    org.telegram.ui.ProfileActivity r9 = org.telegram.ui.ProfileActivity.this
                    android.app.Activity r9 = r9.getParentActivity()
                    r0.<init>((android.content.Context) r9)
                    org.telegram.ui.ProfileActivity r9 = org.telegram.ui.ProfileActivity.this
                    org.telegram.ui.Components.ProfileGalleryView r9 = r9.avatarsViewPager
                    org.telegram.ui.ProfileActivity r10 = org.telegram.ui.ProfileActivity.this
                    org.telegram.ui.Components.ProfileGalleryView r10 = r10.avatarsViewPager
                    int r10 = r10.getRealPosition()
                    org.telegram.messenger.ImageLocation r9 = r9.getImageLocation(r10)
                    if (r9 != 0) goto L_0x0752
                    return
                L_0x0752:
                    int r10 = r9.imageType
                    if (r10 != r7) goto L_0x0771
                    r7 = 2131624382(0x7f0e01be, float:1.8875942E38)
                    java.lang.String r10 = "AreYouSureDeleteVideoTitle"
                    java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r10, r7)
                    r0.setTitle(r7)
                    r7 = 2131624380(0x7f0e01bc, float:1.8875938E38)
                    java.lang.Object[] r8 = new java.lang.Object[r8]
                    java.lang.String r10 = "AreYouSureDeleteVideo"
                    java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r10, r7, r8)
                    r0.setMessage(r7)
                    goto L_0x078b
                L_0x0771:
                    r7 = 2131624371(0x7f0e01b3, float:1.887592E38)
                    java.lang.String r10 = "AreYouSureDeletePhotoTitle"
                    java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r10, r7)
                    r0.setTitle(r7)
                    r7 = 2131624369(0x7f0e01b1, float:1.8875916E38)
                    java.lang.Object[] r8 = new java.lang.Object[r8]
                    java.lang.String r10 = "AreYouSureDeletePhoto"
                    java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r10, r7, r8)
                    r0.setMessage(r7)
                L_0x078b:
                    java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r15, r14)
                    org.telegram.ui.ProfileActivity$4$$ExternalSyntheticLambda2 r8 = new org.telegram.ui.ProfileActivity$4$$ExternalSyntheticLambda2
                    r8.<init>(r1)
                    r0.setPositiveButton(r7, r8)
                    java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                    r0.setNegativeButton(r4, r6)
                    org.telegram.ui.ActionBar.AlertDialog r4 = r0.create()
                    org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                    r5.showDialog(r4)
                    r5 = -1
                    android.view.View r5 = r4.getButton(r5)
                    android.widget.TextView r5 = (android.widget.TextView) r5
                    if (r5 == 0) goto L_0x07c3
                    int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                    r5.setTextColor(r3)
                    goto L_0x07c3
                L_0x07b8:
                    r0 = 36
                    if (r2 != r0) goto L_0x07c3
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    r0.onWriteButtonClick()
                    goto L_0x0860
                L_0x07c3:
                    goto L_0x0860
                L_0x07c5:
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    long r3 = r0.userId
                    r12 = 0
                    int r0 = (r3 > r12 ? 1 : (r3 == r12 ? 0 : -1))
                    if (r0 == 0) goto L_0x0818
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                    org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                    long r3 = r3.userId
                    java.lang.Long r3 = java.lang.Long.valueOf(r3)
                    org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r3)
                    if (r0 == 0) goto L_0x085f
                    if (r2 != r11) goto L_0x07eb
                    r13 = 1
                    goto L_0x07ec
                L_0x07eb:
                    r13 = 0
                L_0x07ec:
                    org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                    org.telegram.tgnet.TLRPC$UserFull r3 = r3.userInfo
                    if (r3 == 0) goto L_0x0800
                    org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                    org.telegram.tgnet.TLRPC$UserFull r3 = r3.userInfo
                    boolean r3 = r3.video_calls_available
                    if (r3 == 0) goto L_0x0800
                    r14 = 1
                    goto L_0x0801
                L_0x0800:
                    r14 = 0
                L_0x0801:
                    org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                    android.app.Activity r15 = r3.getParentActivity()
                    org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                    org.telegram.tgnet.TLRPC$UserFull r16 = r3.userInfo
                    org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                    org.telegram.messenger.AccountInstance r17 = r3.getAccountInstance()
                    r12 = r0
                    org.telegram.ui.Components.voip.VoIPHelper.startCall(r12, r13, r14, r15, r16, r17)
                    goto L_0x085f
                L_0x0818:
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    long r3 = r0.chatId
                    r9 = 0
                    int r0 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1))
                    if (r0 == 0) goto L_0x085f
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                    org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                    long r3 = r3.chatId
                    org.telegram.messenger.ChatObject$Call r0 = r0.getGroupCall(r3, r8)
                    if (r0 != 0) goto L_0x0846
                    org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                    org.telegram.tgnet.TLRPC$Chat r4 = r3.currentChat
                    org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                    org.telegram.messenger.AccountInstance r5 = r5.getAccountInstance()
                    org.telegram.ui.Components.voip.VoIPHelper.showGroupCallAlert(r3, r4, r6, r8, r5)
                    goto L_0x085e
                L_0x0846:
                    org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                    org.telegram.tgnet.TLRPC$Chat r4 = r3.currentChat
                    r5 = 0
                    r6 = 0
                    r7 = 0
                    org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                    android.app.Activity r8 = r3.getParentActivity()
                    org.telegram.ui.ProfileActivity r9 = org.telegram.ui.ProfileActivity.this
                    org.telegram.messenger.AccountInstance r10 = r9.getAccountInstance()
                    org.telegram.ui.Components.voip.VoIPHelper.startCall(r4, r5, r6, r7, r8, r9, r10)
                L_0x085e:
                    goto L_0x0860
                L_0x085f:
                L_0x0860:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.AnonymousClass4.onItemClick(int):void");
            }

            /* renamed from: lambda$onItemClick$0$org-telegram-ui-ProfileActivity$4  reason: not valid java name */
            public /* synthetic */ void m3721lambda$onItemClick$0$orgtelegramuiProfileActivity$4(int param) {
                if (param == 1) {
                    ProfileActivity.this.getNotificationCenter().removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
                    ProfileActivity.this.getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                    int unused = ProfileActivity.this.playProfileAnimation = 0;
                    ProfileActivity.this.finishFragment();
                    return;
                }
                ProfileActivity.this.getNotificationCenter().postNotificationName(NotificationCenter.peerSettingsDidLoad, Long.valueOf(ProfileActivity.this.userId));
            }

            /* renamed from: lambda$onItemClick$1$org-telegram-ui-ProfileActivity$4  reason: not valid java name */
            public /* synthetic */ void m3722lambda$onItemClick$1$orgtelegramuiProfileActivity$4(DialogInterface dialogInterface, int i) {
                ProfileActivity.this.getMessagesController().blockPeer(ProfileActivity.this.userId);
                if (BulletinFactory.canShowBulletin(ProfileActivity.this)) {
                    BulletinFactory.createBanBulletin(ProfileActivity.this, true).show();
                }
            }

            /* renamed from: lambda$onItemClick$2$org-telegram-ui-ProfileActivity$4  reason: not valid java name */
            public /* synthetic */ void m3723lambda$onItemClick$2$orgtelegramuiProfileActivity$4(TLRPC.User user, DialogInterface dialogInterface, int i) {
                ArrayList<TLRPC.User> arrayList = new ArrayList<>();
                arrayList.add(user);
                ProfileActivity.this.getContactsController().deleteContact(arrayList, true);
            }

            /* renamed from: lambda$onItemClick$3$org-telegram-ui-ProfileActivity$4  reason: not valid java name */
            public /* synthetic */ void m3724lambda$onItemClick$3$orgtelegramuiProfileActivity$4(TLRPC.User user, DialogsActivity fragment1, ArrayList dids, CharSequence message, boolean param) {
                long did = ((Long) dids.get(0)).longValue();
                Bundle args1 = new Bundle();
                args1.putBoolean("scrollToTopOnResume", true);
                args1.putLong("chat_id", -did);
                if (ProfileActivity.this.getMessagesController().checkCanOpenChat(args1, fragment1)) {
                    ProfileActivity.this.getNotificationCenter().removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
                    ProfileActivity.this.getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                    ProfileActivity.this.getMessagesController().addUserToChat(-did, user, 0, (String) null, ProfileActivity.this, (Runnable) null);
                    ProfileActivity.this.presentFragment(new ChatActivity(args1), true);
                    ProfileActivity.this.removeSelfFromStack();
                }
            }

            /* renamed from: lambda$onItemClick$4$org-telegram-ui-ProfileActivity$4  reason: not valid java name */
            public /* synthetic */ void m3725lambda$onItemClick$4$orgtelegramuiProfileActivity$4(DialogInterface dialogInterface, int i) {
                boolean unused = ProfileActivity.this.creatingChat = true;
                ProfileActivity.this.getSecretChatHelper().startSecretChat(ProfileActivity.this.getParentActivity(), ProfileActivity.this.getMessagesController().getUser(Long.valueOf(ProfileActivity.this.userId)));
            }

            /* renamed from: lambda$onItemClick$5$org-telegram-ui-ProfileActivity$4  reason: not valid java name */
            public /* synthetic */ void m3726lambda$onItemClick$5$orgtelegramuiProfileActivity$4(boolean isVideo) {
                if (ProfileActivity.this.getParentActivity() != null) {
                    BulletinFactory.createSaveToGalleryBulletin(ProfileActivity.this, isVideo, (Theme.ResourcesProvider) null).show();
                }
            }

            /* renamed from: lambda$onItemClick$7$org-telegram-ui-ProfileActivity$4  reason: not valid java name */
            public /* synthetic */ void m3728lambda$onItemClick$7$orgtelegramuiProfileActivity$4(UserConfig userConfig, TLRPC.Photo photo, TLObject response, TLRPC.TL_error error) {
                AndroidUtilities.runOnUIThread(new ProfileActivity$4$$ExternalSyntheticLambda4(this, response, userConfig, photo));
            }

            /* renamed from: lambda$onItemClick$6$org-telegram-ui-ProfileActivity$4  reason: not valid java name */
            public /* synthetic */ void m3727lambda$onItemClick$6$orgtelegramuiProfileActivity$4(TLObject response, UserConfig userConfig, TLRPC.Photo photo) {
                ProfileActivity.this.avatarsViewPager.finishSettingMainPhoto();
                if (response instanceof TLRPC.TL_photos_photo) {
                    TLRPC.TL_photos_photo photos_photo = (TLRPC.TL_photos_photo) response;
                    ProfileActivity.this.getMessagesController().putUsers(photos_photo.users, false);
                    TLRPC.User user = ProfileActivity.this.getMessagesController().getUser(Long.valueOf(userConfig.clientUserId));
                    if (photos_photo.photo instanceof TLRPC.TL_photo) {
                        ProfileActivity.this.avatarsViewPager.replaceFirstPhoto(photo, photos_photo.photo);
                        if (user != null) {
                            user.photo.photo_id = photos_photo.photo.id;
                            userConfig.setCurrentUser(user);
                            userConfig.saveConfig(true);
                        }
                    }
                }
            }

            /* renamed from: lambda$onItemClick$8$org-telegram-ui-ProfileActivity$4  reason: not valid java name */
            public /* synthetic */ void m3729lambda$onItemClick$8$orgtelegramuiProfileActivity$4(DialogInterface dialogInterface, int i) {
                int position = ProfileActivity.this.avatarsViewPager.getRealPosition();
                TLRPC.Photo photo = ProfileActivity.this.avatarsViewPager.getPhoto(position);
                if (ProfileActivity.this.avatarsViewPager.getRealCount() == 1) {
                    ProfileActivity.this.setForegroundImage(true);
                }
                if (photo == null || ProfileActivity.this.avatarsViewPager.getRealPosition() == 0) {
                    ProfileActivity.this.getMessagesController().deleteUserPhoto((TLRPC.InputPhoto) null);
                } else {
                    TLRPC.TL_inputPhoto inputPhoto = new TLRPC.TL_inputPhoto();
                    inputPhoto.id = photo.id;
                    inputPhoto.access_hash = photo.access_hash;
                    inputPhoto.file_reference = photo.file_reference;
                    if (inputPhoto.file_reference == null) {
                        inputPhoto.file_reference = new byte[0];
                    }
                    ProfileActivity.this.getMessagesController().deleteUserPhoto(inputPhoto);
                    ProfileActivity.this.getMessagesStorage().clearUserPhoto(ProfileActivity.this.userId, photo.id);
                }
                if (ProfileActivity.this.avatarsViewPager.removePhotoAtIndex(position)) {
                    ProfileActivity.this.avatarsViewPager.setVisibility(8);
                    ProfileActivity.this.avatarImage.setForegroundAlpha(1.0f);
                    ProfileActivity.this.avatarContainer.setVisibility(0);
                    boolean unused = ProfileActivity.this.doNotSetForeground = true;
                    View view = ProfileActivity.this.layoutManager.findViewByPosition(0);
                    if (view != null) {
                        ProfileActivity.this.listView.smoothScrollBy(0, view.getTop() - AndroidUtilities.dp(88.0f), CubicBezierInterpolator.EASE_OUT_QUINT);
                    }
                }
            }
        });
        SharedMediaLayout sharedMediaLayout2 = this.sharedMediaLayout;
        if (sharedMediaLayout2 != null) {
            sharedMediaLayout2.onDestroy();
        }
        if (this.dialogId != 0) {
            did = this.dialogId;
        } else if (this.userId != 0) {
            did = this.userId;
        } else {
            did = -this.chatId;
        }
        TLRPC.ChatFull chatFull = this.chatInfo;
        ArrayList<Integer> users = (chatFull == null || chatFull.participants == null || this.chatInfo.participants.participants.size() <= 5) ? null : this.sortedUsers;
        SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader2 = this.sharedMediaPreloader;
        TLRPC.UserFull userFull = this.userInfo;
        AnonymousClass5 r15 = r0;
        long did2 = did;
        AnonymousClass5 r0 = new SharedMediaLayout(context, did, sharedMediaPreloader2, userFull != null ? userFull.common_chats_count : 0, this.sortedUsers, this.chatInfo, users != null, this, this, 1) {
            /* access modifiers changed from: protected */
            public void onSelectedTabChanged() {
                ProfileActivity.this.updateSelectedMediaTabText();
            }

            /* access modifiers changed from: protected */
            public boolean canShowSearchItem() {
                return ProfileActivity.this.mediaHeaderVisible;
            }

            /* access modifiers changed from: protected */
            public void onSearchStateChanged(boolean expanded) {
                if (SharedConfig.smoothKeyboard) {
                    AndroidUtilities.removeAdjustResize(ProfileActivity.this.getParentActivity(), ProfileActivity.this.classGuid);
                }
                ProfileActivity.this.listView.stopScroll();
                ProfileActivity.this.avatarContainer2.setPivotY(ProfileActivity.this.avatarContainer.getPivotY() + (((float) ProfileActivity.this.avatarContainer.getMeasuredHeight()) / 2.0f));
                ProfileActivity.this.avatarContainer2.setPivotX(((float) ProfileActivity.this.avatarContainer2.getMeasuredWidth()) / 2.0f);
                AndroidUtilities.updateViewVisibilityAnimated(ProfileActivity.this.avatarContainer2, !expanded, 0.95f, true);
                int i = 4;
                ProfileActivity.this.callItem.setVisibility((expanded || !ProfileActivity.this.callItemVisible) ? 8 : 4);
                ProfileActivity.this.videoCallItem.setVisibility((expanded || !ProfileActivity.this.videoCallItemVisible) ? 8 : 4);
                ProfileActivity.this.editItem.setVisibility((expanded || !ProfileActivity.this.editItemVisible) ? 8 : 4);
                ActionBarMenuItem access$3600 = ProfileActivity.this.otherItem;
                if (expanded) {
                    i = 8;
                }
                access$3600.setVisibility(i);
            }

            /* access modifiers changed from: protected */
            public boolean onMemberClick(TLRPC.ChatParticipant participant, boolean isLong) {
                return ProfileActivity.this.onMemberClick(participant, isLong);
            }
        };
        this.sharedMediaLayout = r15;
        r15.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
        ActionBarMenu menu = this.actionBar.createMenu();
        if (this.imageUpdater != null) {
            ActionBarMenuItem actionBarMenuItemSearchListener = menu.addItem(32, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
                public Animator getCustomToggleTransition() {
                    ProfileActivity profileActivity = ProfileActivity.this;
                    boolean unused = profileActivity.searchMode = !profileActivity.searchMode;
                    if (!ProfileActivity.this.searchMode) {
                        ProfileActivity.this.searchItem.clearFocusOnSearchView();
                    }
                    if (ProfileActivity.this.searchMode) {
                        ProfileActivity.this.searchItem.getSearchField().setText("");
                    }
                    ProfileActivity profileActivity2 = ProfileActivity.this;
                    return profileActivity2.searchExpandTransition(profileActivity2.searchMode);
                }

                public void onTextChanged(EditText editText) {
                    ProfileActivity.this.searchAdapter.search(editText.getText().toString().toLowerCase());
                }
            });
            this.searchItem = actionBarMenuItemSearchListener;
            actionBarMenuItemSearchListener.setContentDescription(LocaleController.getString("SearchInSettings", NUM));
            this.searchItem.setSearchFieldHint(LocaleController.getString("SearchInSettings", NUM));
            this.sharedMediaLayout.getSearchItem().setVisibility(8);
            if (this.expandPhoto) {
                this.searchItem.setVisibility(8);
            }
        }
        ActionBarMenuItem addItem = menu.addItem(16, NUM);
        this.videoCallItem = addItem;
        addItem.setContentDescription(LocaleController.getString("VideoCall", NUM));
        if (this.chatId != 0) {
            this.callItem = menu.addItem(15, NUM);
            if (ChatObject.isChannelOrGiga(this.currentChat)) {
                this.callItem.setContentDescription(LocaleController.getString("VoipChannelVoiceChat", NUM));
            } else {
                this.callItem.setContentDescription(LocaleController.getString("VoipGroupVoiceChat", NUM));
            }
        } else {
            ActionBarMenuItem addItem2 = menu.addItem(15, NUM);
            this.callItem = addItem2;
            addItem2.setContentDescription(LocaleController.getString("Call", NUM));
        }
        ActionBarMenuItem addItem3 = menu.addItem(12, NUM);
        this.editItem = addItem3;
        addItem3.setContentDescription(LocaleController.getString("Edit", NUM));
        ActionBarMenuItem addItem4 = menu.addItem(10, NUM);
        this.otherItem = addItem4;
        addItem4.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
        int scrollToPosition2 = 0;
        if (this.listView == null || this.imageUpdater == null) {
            scrollToPosition = 0;
            writeButtonTag = null;
            scrollTo = -1;
        } else {
            int scrollTo2 = this.layoutManager.findFirstVisibleItemPosition();
            View topView2 = this.layoutManager.findViewByPosition(scrollTo2);
            if (topView2 != null) {
                scrollToPosition2 = topView2.getTop() - this.listView.getPaddingTop();
            } else {
                scrollTo2 = -1;
            }
            scrollToPosition = scrollToPosition2;
            writeButtonTag = this.writeButton.getTag();
            scrollTo = scrollTo2;
        }
        createActionBarMenu(false);
        final Context context2 = context;
        this.listAdapter = new ListAdapter(context2);
        this.searchAdapter = new SearchAdapter(this, context2);
        AvatarDrawable avatarDrawable2 = new AvatarDrawable();
        this.avatarDrawable = avatarDrawable2;
        avatarDrawable2.setProfile(true);
        this.fragmentView = new NestedFrameLayout(context2) {
            private Paint grayPaint = new Paint();
            private boolean ignoreLayout;
            private final ArrayList<View> sortedChildren = new ArrayList<>();
            private final Comparator<View> viewComparator = ProfileActivity$7$$ExternalSyntheticLambda0.INSTANCE;

            public boolean dispatchTouchEvent(MotionEvent ev) {
                if (ProfileActivity.this.pinchToZoomHelper.isInOverlayMode()) {
                    return ProfileActivity.this.pinchToZoomHelper.onTouchEvent(ev);
                }
                if (ProfileActivity.this.sharedMediaLayout != null && ProfileActivity.this.sharedMediaLayout.isInFastScroll() && ProfileActivity.this.sharedMediaLayout.isPinnedToTop()) {
                    return ProfileActivity.this.sharedMediaLayout.dispatchFastScrollEvent(ev);
                }
                if (ProfileActivity.this.sharedMediaLayout == null || !ProfileActivity.this.sharedMediaLayout.checkPinchToZoom(ev)) {
                    return super.dispatchTouchEvent(ev);
                }
                return true;
            }

            public boolean hasOverlappingRendering() {
                return false;
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                boolean changed;
                int paddingTop;
                int paddingBottom;
                int paddingBottom2;
                View view;
                int pos;
                boolean layout;
                int paddingBottom3;
                int paddingTop2;
                int paddingBottom4;
                int actionBarHeight = ActionBar.getCurrentActionBarHeight() + (ProfileActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0);
                if (ProfileActivity.this.listView != null) {
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) ProfileActivity.this.listView.getLayoutParams();
                    if (layoutParams.topMargin != actionBarHeight) {
                        layoutParams.topMargin = actionBarHeight;
                    }
                }
                if (ProfileActivity.this.searchListView != null) {
                    FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) ProfileActivity.this.searchListView.getLayoutParams();
                    if (layoutParams2.topMargin != actionBarHeight) {
                        layoutParams2.topMargin = actionBarHeight;
                    }
                }
                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(heightMeasureSpec), NUM));
                if (ProfileActivity.this.lastMeasuredContentWidth == getMeasuredWidth() && ProfileActivity.this.lastMeasuredContentHeight == getMeasuredHeight()) {
                    changed = false;
                } else {
                    boolean changed2 = (ProfileActivity.this.lastMeasuredContentWidth == 0 || ProfileActivity.this.lastMeasuredContentWidth == getMeasuredWidth()) ? false : true;
                    int unused = ProfileActivity.this.listContentHeight = 0;
                    int count = ProfileActivity.this.listAdapter.getItemCount();
                    int unused2 = ProfileActivity.this.lastMeasuredContentWidth = getMeasuredWidth();
                    int unused3 = ProfileActivity.this.lastMeasuredContentHeight = getMeasuredHeight();
                    int ws = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), NUM);
                    int hs = View.MeasureSpec.makeMeasureSpec(ProfileActivity.this.listView.getMeasuredHeight(), 0);
                    ProfileActivity.this.positionToOffset.clear();
                    for (int i = 0; i < count; i++) {
                        int type = ProfileActivity.this.listAdapter.getItemViewType(i);
                        ProfileActivity.this.positionToOffset.put(Integer.valueOf(i), Integer.valueOf(ProfileActivity.this.listContentHeight));
                        if (type == 13) {
                            ProfileActivity profileActivity = ProfileActivity.this;
                            ProfileActivity.access$7112(profileActivity, profileActivity.listView.getMeasuredHeight());
                        } else {
                            RecyclerView.ViewHolder holder = ProfileActivity.this.listAdapter.createViewHolder((ViewGroup) null, type);
                            ProfileActivity.this.listAdapter.onBindViewHolder(holder, i);
                            holder.itemView.measure(ws, hs);
                            ProfileActivity.access$7112(ProfileActivity.this, holder.itemView.getMeasuredHeight());
                        }
                    }
                    if (ProfileActivity.this.emptyView != null) {
                        ((FrameLayout.LayoutParams) ProfileActivity.this.emptyView.getLayoutParams()).topMargin = AndroidUtilities.dp(88.0f) + AndroidUtilities.statusBarHeight;
                    }
                    changed = changed2;
                }
                if (!ProfileActivity.this.fragmentOpened && (ProfileActivity.this.expandPhoto || (ProfileActivity.this.openAnimationInProgress && ProfileActivity.this.playProfileAnimation == 2))) {
                    this.ignoreLayout = true;
                    if (ProfileActivity.this.expandPhoto) {
                        if (ProfileActivity.this.searchItem != null) {
                            ProfileActivity.this.searchItem.setAlpha(0.0f);
                            ProfileActivity.this.searchItem.setEnabled(false);
                        }
                        ProfileActivity.this.nameTextView[1].setTextColor(-1);
                        ProfileActivity.this.onlineTextView[1].setTextColor(Color.argb(179, 255, 255, 255));
                        ProfileActivity.this.actionBar.setItemsBackgroundColor(NUM, false);
                        ProfileActivity.this.actionBar.setItemsColor(-1, false);
                        ProfileActivity.this.overlaysView.setOverlaysVisible();
                        ProfileActivity.this.overlaysView.setAlphaValue(1.0f, false);
                        ProfileActivity.this.avatarImage.setForegroundAlpha(1.0f);
                        ProfileActivity.this.avatarContainer.setVisibility(8);
                        ProfileActivity.this.avatarsViewPager.resetCurrentItem();
                        ProfileActivity.this.avatarsViewPager.setVisibility(0);
                        boolean unused4 = ProfileActivity.this.expandPhoto = false;
                    }
                    boolean unused5 = ProfileActivity.this.allowPullingDown = true;
                    boolean unused6 = ProfileActivity.this.isPulledDown = true;
                    if (ProfileActivity.this.otherItem != null) {
                        if (!ProfileActivity.this.getMessagesController().isChatNoForwards(ProfileActivity.this.currentChat)) {
                            ProfileActivity.this.otherItem.showSubItem(21);
                        } else {
                            ProfileActivity.this.otherItem.hideSubItem(21);
                        }
                        if (ProfileActivity.this.imageUpdater != null) {
                            ProfileActivity.this.otherItem.showSubItem(34);
                            ProfileActivity.this.otherItem.showSubItem(35);
                            ProfileActivity.this.otherItem.hideSubItem(31);
                        }
                    }
                    float unused7 = ProfileActivity.this.currentExpanAnimatorFracture = 1.0f;
                    if (ProfileActivity.this.isInLandscapeMode) {
                        paddingTop2 = AndroidUtilities.dp(88.0f);
                        paddingBottom3 = 0;
                    } else {
                        paddingTop2 = ProfileActivity.this.listView.getMeasuredWidth();
                        paddingBottom3 = Math.max(0, getMeasuredHeight() - ((ProfileActivity.this.listContentHeight + AndroidUtilities.dp(88.0f)) + actionBarHeight));
                    }
                    if (ProfileActivity.this.banFromGroup != 0) {
                        int paddingBottom5 = paddingBottom3 + AndroidUtilities.dp(48.0f);
                        ProfileActivity.this.listView.setBottomGlowOffset(AndroidUtilities.dp(48.0f));
                        paddingBottom4 = paddingBottom5;
                    } else {
                        ProfileActivity.this.listView.setBottomGlowOffset(0);
                        paddingBottom4 = paddingBottom3;
                    }
                    float unused8 = ProfileActivity.this.initialAnimationExtraHeight = (float) (paddingTop2 - actionBarHeight);
                    ProfileActivity.this.layoutManager.scrollToPositionWithOffset(0, -actionBarHeight);
                    ProfileActivity.this.listView.setPadding(0, paddingTop2, 0, paddingBottom4);
                    measureChildWithMargins(ProfileActivity.this.listView, widthMeasureSpec, 0, heightMeasureSpec, 0);
                    ProfileActivity.this.listView.layout(0, actionBarHeight, ProfileActivity.this.listView.getMeasuredWidth(), ProfileActivity.this.listView.getMeasuredHeight() + actionBarHeight);
                    this.ignoreLayout = false;
                } else if (ProfileActivity.this.fragmentOpened && !ProfileActivity.this.openAnimationInProgress && !ProfileActivity.this.firstLayout) {
                    this.ignoreLayout = true;
                    if (ProfileActivity.this.isInLandscapeMode || AndroidUtilities.isTablet()) {
                        paddingBottom = 0;
                        paddingTop = AndroidUtilities.dp(88.0f);
                    } else {
                        int paddingTop3 = ProfileActivity.this.listView.getMeasuredWidth();
                        paddingBottom = Math.max(0, getMeasuredHeight() - ((ProfileActivity.this.listContentHeight + AndroidUtilities.dp(88.0f)) + actionBarHeight));
                        paddingTop = paddingTop3;
                    }
                    if (ProfileActivity.this.banFromGroup != 0) {
                        int paddingBottom6 = paddingBottom + AndroidUtilities.dp(48.0f);
                        ProfileActivity.this.listView.setBottomGlowOffset(AndroidUtilities.dp(48.0f));
                        paddingBottom2 = paddingBottom6;
                    } else {
                        ProfileActivity.this.listView.setBottomGlowOffset(0);
                        paddingBottom2 = paddingBottom;
                    }
                    int currentPaddingTop = ProfileActivity.this.listView.getPaddingTop();
                    View view2 = null;
                    int pos2 = -1;
                    int i2 = 0;
                    while (true) {
                        if (i2 >= ProfileActivity.this.listView.getChildCount()) {
                            break;
                        }
                        int p = ProfileActivity.this.listView.getChildAdapterPosition(ProfileActivity.this.listView.getChildAt(i2));
                        if (p != -1) {
                            view2 = ProfileActivity.this.listView.getChildAt(i2);
                            pos2 = p;
                            break;
                        }
                        i2++;
                    }
                    if (view2 == null) {
                        View view3 = ProfileActivity.this.listView.getChildAt(0);
                        if (view3 != null) {
                            RecyclerView.ViewHolder holder2 = ProfileActivity.this.listView.findContainingViewHolder(view3);
                            int pos3 = holder2.getAdapterPosition();
                            if (pos3 == -1) {
                                view = view3;
                                pos = holder2.getPosition();
                            } else {
                                view = view3;
                                pos = pos3;
                            }
                        } else {
                            view = view3;
                            pos = pos2;
                        }
                    } else {
                        view = view2;
                        pos = pos2;
                    }
                    int top = paddingTop;
                    if (view != null) {
                        top = view.getTop();
                    }
                    boolean layout2 = false;
                    if (!ProfileActivity.this.actionBar.isSearchFieldVisible() || ProfileActivity.this.sharedMediaRow < 0) {
                        if (ProfileActivity.this.invalidateScroll || currentPaddingTop != paddingTop) {
                            if (ProfileActivity.this.savedScrollPosition >= 0) {
                                ProfileActivity.this.layoutManager.scrollToPositionWithOffset(ProfileActivity.this.savedScrollPosition, ProfileActivity.this.savedScrollOffset - paddingTop);
                            } else if ((!changed || !ProfileActivity.this.allowPullingDown) && view != null) {
                                if (pos == 0 && !ProfileActivity.this.allowPullingDown && top > AndroidUtilities.dp(88.0f)) {
                                    top = AndroidUtilities.dp(88.0f);
                                }
                                ProfileActivity.this.layoutManager.scrollToPositionWithOffset(pos, top - paddingTop);
                                layout2 = true;
                                int i3 = top;
                            } else {
                                ProfileActivity.this.layoutManager.scrollToPositionWithOffset(0, AndroidUtilities.dp(88.0f) - paddingTop);
                            }
                        }
                        int i4 = top;
                    } else {
                        ProfileActivity.this.layoutManager.scrollToPositionWithOffset(ProfileActivity.this.sharedMediaRow, -paddingTop);
                        layout2 = true;
                        int i5 = top;
                    }
                    if (currentPaddingTop == paddingTop && ProfileActivity.this.listView.getPaddingBottom() == paddingBottom2) {
                        layout = layout2;
                    } else {
                        ProfileActivity.this.listView.setPadding(0, paddingTop, 0, paddingBottom2);
                        layout = true;
                    }
                    if (layout) {
                        int i6 = pos;
                        measureChildWithMargins(ProfileActivity.this.listView, widthMeasureSpec, 0, heightMeasureSpec, 0);
                        try {
                            ProfileActivity.this.listView.layout(0, actionBarHeight, ProfileActivity.this.listView.getMeasuredWidth(), ProfileActivity.this.listView.getMeasuredHeight() + actionBarHeight);
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    }
                    this.ignoreLayout = false;
                }
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                ProfileActivity.this.savedScrollPosition = -1;
                boolean unused = ProfileActivity.this.firstLayout = false;
                boolean unused2 = ProfileActivity.this.invalidateScroll = false;
                ProfileActivity.this.checkListViewScroll();
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }

            static /* synthetic */ int lambda$$0(View view, View view2) {
                return (int) (view.getY() - view2.getY());
            }

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                FragmentContextView fragmentContextView;
                int i;
                boolean currentHasBackground;
                int currentY;
                Canvas canvas2 = canvas;
                ProfileActivity.this.whitePaint.setColor(Theme.getColor("windowBackgroundWhite"));
                float f = 1.0f;
                if (ProfileActivity.this.listView.getVisibility() == 0) {
                    this.grayPaint.setColor(Theme.getColor("windowBackgroundGray"));
                    if (ProfileActivity.this.transitionAnimationInProress) {
                        ProfileActivity.this.whitePaint.setAlpha((int) (ProfileActivity.this.listView.getAlpha() * 255.0f));
                    }
                    if (ProfileActivity.this.transitionAnimationInProress) {
                        this.grayPaint.setAlpha((int) (ProfileActivity.this.listView.getAlpha() * 255.0f));
                    }
                    int count = ProfileActivity.this.listView.getChildCount();
                    this.sortedChildren.clear();
                    boolean hasRemovingItems = false;
                    for (int i2 = 0; i2 < count; i2++) {
                        if (ProfileActivity.this.listView.getChildAdapterPosition(ProfileActivity.this.listView.getChildAt(i2)) != -1) {
                            this.sortedChildren.add(ProfileActivity.this.listView.getChildAt(i2));
                        } else {
                            hasRemovingItems = true;
                        }
                    }
                    Collections.sort(this.sortedChildren, this.viewComparator);
                    float lastY = ProfileActivity.this.listView.getY();
                    int count2 = this.sortedChildren.size();
                    if (!ProfileActivity.this.openAnimationInProgress && count2 > 0 && !hasRemovingItems) {
                        lastY += this.sortedChildren.get(0).getY();
                    }
                    float alpha = 1.0f;
                    boolean hasBackground = false;
                    float lastY2 = lastY;
                    int i3 = 0;
                    while (i3 < count2) {
                        View child = this.sortedChildren.get(i3);
                        boolean currentHasBackground2 = child.getBackground() != null;
                        int currentY2 = (int) (ProfileActivity.this.listView.getY() + child.getY());
                        if (hasBackground != currentHasBackground2) {
                            if (hasBackground) {
                                currentY = currentY2;
                                currentHasBackground = currentHasBackground2;
                                i = i3;
                                canvas.drawRect(ProfileActivity.this.listView.getX(), lastY2, ((float) ProfileActivity.this.listView.getMeasuredWidth()) + ProfileActivity.this.listView.getX(), (float) currentY2, this.grayPaint);
                            } else {
                                currentY = currentY2;
                                currentHasBackground = currentHasBackground2;
                                i = i3;
                                if (alpha != f) {
                                    canvas.drawRect(ProfileActivity.this.listView.getX(), lastY2, ProfileActivity.this.listView.getX() + ((float) ProfileActivity.this.listView.getMeasuredWidth()), (float) currentY, this.grayPaint);
                                    ProfileActivity.this.whitePaint.setAlpha((int) (alpha * 255.0f));
                                    canvas.drawRect(ProfileActivity.this.listView.getX(), lastY2, ProfileActivity.this.listView.getX() + ((float) ProfileActivity.this.listView.getMeasuredWidth()), (float) currentY, ProfileActivity.this.whitePaint);
                                    ProfileActivity.this.whitePaint.setAlpha(255);
                                } else {
                                    canvas.drawRect(ProfileActivity.this.listView.getX(), lastY2, ProfileActivity.this.listView.getX() + ((float) ProfileActivity.this.listView.getMeasuredWidth()), (float) currentY, ProfileActivity.this.whitePaint);
                                }
                            }
                            hasBackground = currentHasBackground;
                            lastY2 = (float) currentY;
                            alpha = child.getAlpha();
                        } else if (child.getAlpha() == f) {
                            alpha = 1.0f;
                            i = i3;
                        } else {
                            i = i3;
                        }
                        i3 = i + 1;
                        f = 1.0f;
                    }
                    int i4 = i3;
                    if (hasBackground) {
                        canvas.drawRect(ProfileActivity.this.listView.getX(), lastY2, ProfileActivity.this.listView.getX() + ((float) ProfileActivity.this.listView.getMeasuredWidth()), (float) ProfileActivity.this.listView.getBottom(), this.grayPaint);
                    } else if (alpha != 1.0f) {
                        canvas.drawRect(ProfileActivity.this.listView.getX(), lastY2, ProfileActivity.this.listView.getX() + ((float) ProfileActivity.this.listView.getMeasuredWidth()), (float) ProfileActivity.this.listView.getBottom(), this.grayPaint);
                        ProfileActivity.this.whitePaint.setAlpha((int) (255.0f * alpha));
                        canvas.drawRect(ProfileActivity.this.listView.getX(), lastY2, ProfileActivity.this.listView.getX() + ((float) ProfileActivity.this.listView.getMeasuredWidth()), (float) ProfileActivity.this.listView.getBottom(), ProfileActivity.this.whitePaint);
                        ProfileActivity.this.whitePaint.setAlpha(255);
                    } else {
                        canvas.drawRect(ProfileActivity.this.listView.getX(), lastY2, ProfileActivity.this.listView.getX() + ((float) ProfileActivity.this.listView.getMeasuredWidth()), (float) ProfileActivity.this.listView.getBottom(), ProfileActivity.this.whitePaint);
                    }
                } else {
                    int top = ProfileActivity.this.searchListView.getTop();
                    canvas.drawRect(0.0f, ((float) ProfileActivity.this.searchTransitionOffset) + ((float) top) + ProfileActivity.this.extraHeight, (float) getMeasuredWidth(), (float) (getMeasuredHeight() + top), ProfileActivity.this.whitePaint);
                }
                super.dispatchDraw(canvas);
                if (ProfileActivity.this.profileTransitionInProgress && ProfileActivity.this.parentLayout.fragmentsStack.size() > 1) {
                    BaseFragment fragment = ProfileActivity.this.parentLayout.fragmentsStack.get(ProfileActivity.this.parentLayout.fragmentsStack.size() - 2);
                    if ((fragment instanceof ChatActivity) && (fragmentContextView = ((ChatActivity) fragment).getFragmentContextView()) != null && fragmentContextView.isCallStyle()) {
                        float progress = ProfileActivity.this.extraHeight / AndroidUtilities.dpf2((float) fragmentContextView.getStyleHeight());
                        if (progress > 1.0f) {
                            progress = 1.0f;
                        }
                        canvas.save();
                        canvas2.translate(fragmentContextView.getX(), fragmentContextView.getY());
                        fragmentContextView.setDrawOverlay(true);
                        fragmentContextView.setCollapseTransition(true, ProfileActivity.this.extraHeight, progress);
                        fragmentContextView.draw(canvas2);
                        fragmentContextView.setCollapseTransition(false, ProfileActivity.this.extraHeight, progress);
                        fragmentContextView.setDrawOverlay(false);
                        canvas.restore();
                    }
                }
            }

            /* access modifiers changed from: protected */
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                if (!ProfileActivity.this.pinchToZoomHelper.isInOverlayMode() || (child != ProfileActivity.this.avatarContainer2 && child != ProfileActivity.this.actionBar && child != ProfileActivity.this.writeButton)) {
                    return super.drawChild(canvas, child, drawingTime);
                }
                return true;
            }
        };
        this.fragmentView.setWillNotDraw(false);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        AnonymousClass8 r02 = new RecyclerListView(context2) {
            private VelocityTracker velocityTracker;

            /* access modifiers changed from: protected */
            public boolean allowSelectChildAtPosition(View child) {
                return child != ProfileActivity.this.sharedMediaLayout;
            }

            public boolean hasOverlappingRendering() {
                return false;
            }

            /* access modifiers changed from: protected */
            public void requestChildOnScreen(View child, View focused) {
            }

            public void invalidate() {
                super.invalidate();
                if (ProfileActivity.this.fragmentView != null) {
                    ProfileActivity.this.fragmentView.invalidate();
                }
            }

            public boolean onTouchEvent(MotionEvent e) {
                View view;
                VelocityTracker velocityTracker2;
                int action = e.getAction();
                if (action == 0) {
                    VelocityTracker velocityTracker3 = this.velocityTracker;
                    if (velocityTracker3 == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    } else {
                        velocityTracker3.clear();
                    }
                    this.velocityTracker.addMovement(e);
                } else if (action == 2) {
                    VelocityTracker velocityTracker4 = this.velocityTracker;
                    if (velocityTracker4 != null) {
                        velocityTracker4.addMovement(e);
                        this.velocityTracker.computeCurrentVelocity(1000);
                        float unused = ProfileActivity.this.listViewVelocityY = this.velocityTracker.getYVelocity(e.getPointerId(e.getActionIndex()));
                    }
                } else if ((action == 1 || action == 3) && (velocityTracker2 = this.velocityTracker) != null) {
                    velocityTracker2.recycle();
                    this.velocityTracker = null;
                }
                boolean result = super.onTouchEvent(e);
                if ((action == 1 || action == 3) && ProfileActivity.this.allowPullingDown && (view = ProfileActivity.this.layoutManager.findViewByPosition(0)) != null) {
                    if (ProfileActivity.this.isPulledDown) {
                        ProfileActivity.this.listView.smoothScrollBy(0, (view.getTop() - ProfileActivity.this.listView.getMeasuredWidth()) + ActionBar.getCurrentActionBarHeight() + (ProfileActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0), CubicBezierInterpolator.EASE_OUT_QUINT);
                    } else {
                        ProfileActivity.this.listView.smoothScrollBy(0, view.getTop() - AndroidUtilities.dp(88.0f), CubicBezierInterpolator.EASE_OUT_QUINT);
                    }
                }
                return result;
            }

            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                if (getItemAnimator().isRunning() && child.getBackground() == null && child.getTranslationY() != 0.0f) {
                    boolean useAlpha = ProfileActivity.this.listView.getChildAdapterPosition(child) == ProfileActivity.this.sharedMediaRow && child.getAlpha() != 1.0f;
                    if (useAlpha) {
                        ProfileActivity.this.whitePaint.setAlpha((int) (ProfileActivity.this.listView.getAlpha() * 255.0f * child.getAlpha()));
                    }
                    canvas.drawRect(ProfileActivity.this.listView.getX(), child.getY(), ProfileActivity.this.listView.getX() + ((float) ProfileActivity.this.listView.getMeasuredWidth()), child.getY() + ((float) child.getHeight()), ProfileActivity.this.whitePaint);
                    if (useAlpha) {
                        ProfileActivity.this.whitePaint.setAlpha((int) (ProfileActivity.this.listView.getAlpha() * 255.0f));
                    }
                }
                return super.drawChild(canvas, child, drawingTime);
            }
        };
        this.listView = r02;
        r02.setVerticalScrollBarEnabled(false);
        AnonymousClass9 r6 = new DefaultItemAnimator() {
            int animationIndex = -1;

            /* access modifiers changed from: protected */
            public void onAllAnimationsDone() {
                super.onAllAnimationsDone();
                ProfileActivity.this.getNotificationCenter().onAnimationFinish(this.animationIndex);
            }

            public void runPendingAnimations() {
                boolean removalsPending = !this.mPendingRemovals.isEmpty();
                boolean movesPending = !this.mPendingMoves.isEmpty();
                boolean changesPending = !this.mPendingChanges.isEmpty();
                boolean additionsPending = !this.mPendingAdditions.isEmpty();
                if (removalsPending || movesPending || additionsPending || changesPending) {
                    ValueAnimator valueAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                    valueAnimator.addUpdateListener(new ProfileActivity$9$$ExternalSyntheticLambda0(this));
                    valueAnimator.setDuration(getMoveDuration());
                    valueAnimator.start();
                    this.animationIndex = ProfileActivity.this.getNotificationCenter().setAnimationInProgress(this.animationIndex, (int[]) null);
                }
                super.runPendingAnimations();
            }

            /* renamed from: lambda$runPendingAnimations$0$org-telegram-ui-ProfileActivity$9  reason: not valid java name */
            public /* synthetic */ void m3730lambda$runPendingAnimations$0$orgtelegramuiProfileActivity$9(ValueAnimator valueAnimator1) {
                ProfileActivity.this.listView.invalidate();
            }

            /* access modifiers changed from: protected */
            public long getAddAnimationDelay(long removeDuration, long moveDuration, long changeDuration) {
                return 0;
            }

            /* access modifiers changed from: protected */
            public long getMoveAnimationDelay() {
                return 0;
            }

            public long getMoveDuration() {
                return 220;
            }

            public long getRemoveDuration() {
                return 220;
            }

            public long getAddDuration() {
                return 220;
            }
        };
        this.listView.setItemAnimator(r6);
        r6.setSupportsChangeAnimations(false);
        r6.setDelayAnimations(false);
        this.listView.setClipToPadding(false);
        this.listView.setHideIfEmpty(false);
        AnonymousClass10 r03 = new LinearLayoutManager(context2) {
            public boolean supportsPredictiveItemAnimations() {
                return ProfileActivity.this.imageUpdater != null;
            }

            public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
                boolean z = false;
                View view = ProfileActivity.this.layoutManager.findViewByPosition(0);
                if (view != null && !ProfileActivity.this.openingAvatar) {
                    int canScroll = view.getTop() - AndroidUtilities.dp(88.0f);
                    if (!ProfileActivity.this.allowPullingDown && canScroll > dy) {
                        dy = canScroll;
                        if (ProfileActivity.this.avatarsViewPager.hasImages() && ProfileActivity.this.avatarImage.getImageReceiver().hasNotThumb() && !ProfileActivity.this.isInLandscapeMode && !AndroidUtilities.isTablet()) {
                            ProfileActivity profileActivity = ProfileActivity.this;
                            if (profileActivity.avatarBig == null) {
                                z = true;
                            }
                            boolean unused = profileActivity.allowPullingDown = z;
                        }
                    } else if (ProfileActivity.this.allowPullingDown) {
                        if (dy >= canScroll) {
                            dy = canScroll;
                            boolean unused2 = ProfileActivity.this.allowPullingDown = false;
                        } else if (ProfileActivity.this.listView.getScrollState() == 1 && !ProfileActivity.this.isPulledDown) {
                            dy /= 2;
                        }
                    }
                }
                return super.scrollVerticallyBy(dy, recycler, state);
            }
        };
        this.layoutManager = r03;
        r03.setOrientation(1);
        this.listView.setLayoutManager(this.layoutManager);
        this.listView.setGlowColor(0);
        this.listView.setAdapter(this.listAdapter);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        long did3 = did2;
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListenerExtended) new ProfileActivity$$ExternalSyntheticLambda26(this, did3));
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener() {
            private int pressCount = 0;

            public boolean onItemClick(View view, int position) {
                TLRPC.ChatParticipant participant;
                String str;
                String str2;
                int i;
                String str3;
                int i2;
                String str4;
                String str5;
                int i3;
                String str6;
                int i4;
                if (position == ProfileActivity.this.versionRow) {
                    int i5 = this.pressCount + 1;
                    this.pressCount = i5;
                    if (i5 >= 2 || BuildVars.DEBUG_PRIVATE_VERSION) {
                        AlertDialog.Builder builder = new AlertDialog.Builder((Context) ProfileActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString("DebugMenu", NUM));
                        CharSequence[] items = new CharSequence[17];
                        items[0] = LocaleController.getString("DebugMenuImportContacts", NUM);
                        items[1] = LocaleController.getString("DebugMenuReloadContacts", NUM);
                        items[2] = LocaleController.getString("DebugMenuResetContacts", NUM);
                        items[3] = LocaleController.getString("DebugMenuResetDialogs", NUM);
                        if (BuildVars.DEBUG_VERSION) {
                            str = null;
                        } else {
                            if (BuildVars.LOGS_ENABLED) {
                                i4 = NUM;
                                str6 = "DebugMenuDisableLogs";
                            } else {
                                i4 = NUM;
                                str6 = "DebugMenuEnableLogs";
                            }
                            str = LocaleController.getString(str6, i4);
                        }
                        items[4] = str;
                        if (SharedConfig.inappCamera) {
                            i = NUM;
                            str2 = "DebugMenuDisableCamera";
                        } else {
                            i = NUM;
                            str2 = "DebugMenuEnableCamera";
                        }
                        items[5] = LocaleController.getString(str2, i);
                        items[6] = LocaleController.getString("DebugMenuClearMediaCache", NUM);
                        items[7] = LocaleController.getString("DebugMenuCallSettings", NUM);
                        items[8] = null;
                        items[9] = (BuildVars.DEBUG_PRIVATE_VERSION || BuildVars.isStandaloneApp()) ? LocaleController.getString("DebugMenuCheckAppUpdate", NUM) : null;
                        items[10] = LocaleController.getString("DebugMenuReadAllDialogs", NUM);
                        if (SharedConfig.pauseMusicOnRecord) {
                            i2 = NUM;
                            str3 = "DebugMenuDisablePauseMusic";
                        } else {
                            i2 = NUM;
                            str3 = "DebugMenuEnablePauseMusic";
                        }
                        items[11] = LocaleController.getString(str3, i2);
                        if (!BuildVars.DEBUG_VERSION || AndroidUtilities.isTablet() || Build.VERSION.SDK_INT < 23) {
                            str4 = null;
                        } else {
                            if (SharedConfig.smoothKeyboard) {
                                i3 = NUM;
                                str5 = "DebugMenuDisableSmoothKeyboard";
                            } else {
                                i3 = NUM;
                                str5 = "DebugMenuEnableSmoothKeyboard";
                            }
                            str4 = LocaleController.getString(str5, i3);
                        }
                        items[12] = str4;
                        items[13] = BuildVars.DEBUG_PRIVATE_VERSION ? SharedConfig.disableVoiceAudioEffects ? "Enable voip audio effects" : "Disable voip audio effects" : null;
                        items[14] = Build.VERSION.SDK_INT >= 21 ? SharedConfig.noStatusBar ? "Show status bar background" : "Hide status bar background" : null;
                        items[15] = BuildVars.DEBUG_PRIVATE_VERSION ? "Clean app update" : null;
                        items[16] = BuildVars.DEBUG_PRIVATE_VERSION ? "Reset suggestions" : null;
                        builder.setItems(items, new ProfileActivity$11$$ExternalSyntheticLambda0(this));
                        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                        ProfileActivity.this.showDialog(builder.create());
                    } else {
                        try {
                            Toast.makeText(ProfileActivity.this.getParentActivity(), "¯\\_(ツ)_/¯", 0).show();
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    }
                    return true;
                } else if (position < ProfileActivity.this.membersStartRow || position >= ProfileActivity.this.membersEndRow) {
                    return ProfileActivity.this.processOnClickOrPress(position);
                } else {
                    if (!ProfileActivity.this.sortedUsers.isEmpty()) {
                        participant = (TLRPC.ChatParticipant) ProfileActivity.this.visibleChatParticipants.get(((Integer) ProfileActivity.this.sortedUsers.get(position - ProfileActivity.this.membersStartRow)).intValue());
                    } else {
                        participant = (TLRPC.ChatParticipant) ProfileActivity.this.visibleChatParticipants.get(position - ProfileActivity.this.membersStartRow);
                    }
                    return ProfileActivity.this.onMemberClick(participant, true);
                }
            }

            /* renamed from: lambda$onItemClick$0$org-telegram-ui-ProfileActivity$11  reason: not valid java name */
            public /* synthetic */ void m3720lambda$onItemClick$0$orgtelegramuiProfileActivity$11(DialogInterface dialog, int which) {
                if (which == 0) {
                    ProfileActivity.this.getUserConfig().syncContacts = true;
                    ProfileActivity.this.getUserConfig().saveConfig(false);
                    ProfileActivity.this.getContactsController().forceImportContacts();
                } else if (which == 1) {
                    ProfileActivity.this.getContactsController().loadContacts(false, 0);
                } else if (which == 2) {
                    ProfileActivity.this.getContactsController().resetImportedContacts();
                } else if (which == 3) {
                    ProfileActivity.this.getMessagesController().forceResetDialogs();
                } else if (which == 4) {
                    BuildVars.LOGS_ENABLED = true ^ BuildVars.LOGS_ENABLED;
                    ApplicationLoader.applicationContext.getSharedPreferences("systemConfig", 0).edit().putBoolean("logsEnabled", BuildVars.LOGS_ENABLED).commit();
                    ProfileActivity.this.updateRowsIds();
                    ProfileActivity.this.listAdapter.notifyDataSetChanged();
                } else if (which == 5) {
                    SharedConfig.toggleInappCamera();
                } else if (which == 6) {
                    ProfileActivity.this.getMessagesStorage().clearSentMedia();
                    SharedConfig.setNoSoundHintShowed(false);
                    MessagesController.getGlobalMainSettings().edit().remove("archivehint").remove("proximityhint").remove("archivehint_l").remove("gifhint").remove("reminderhint").remove("soundHint").remove("themehint").remove("bganimationhint").remove("filterhint").commit();
                    MessagesController.getEmojiSettings(ProfileActivity.this.currentAccount).edit().remove("featured_hidden").commit();
                    SharedConfig.textSelectionHintShows = 0;
                    SharedConfig.lockRecordAudioVideoHint = 0;
                    SharedConfig.stickersReorderingHintUsed = false;
                    SharedConfig.forwardingOptionsHintShown = false;
                    SharedConfig.messageSeenHintCount = 3;
                    SharedConfig.emojiInteractionsHintCount = 3;
                    SharedConfig.dayNightThemeSwitchHintCount = 3;
                    SharedConfig.fastScrollHintCount = 3;
                    ChatThemeController.getInstance(ProfileActivity.this.currentAccount).clearCache();
                } else if (which == 7) {
                    VoIPHelper.showCallDebugSettings(ProfileActivity.this.getParentActivity());
                } else if (which == 8) {
                    SharedConfig.toggleRoundCamera16to9();
                } else if (which == 9) {
                    ((LaunchActivity) ProfileActivity.this.getParentActivity()).checkAppUpdate(true);
                } else if (which == 10) {
                    ProfileActivity.this.getMessagesStorage().readAllDialogs(-1);
                } else if (which == 11) {
                    SharedConfig.togglePauseMusicOnRecord();
                } else if (which == 12) {
                    SharedConfig.toggleSmoothKeyboard();
                    if (SharedConfig.smoothKeyboard && ProfileActivity.this.getParentActivity() != null) {
                        ProfileActivity.this.getParentActivity().getWindow().setSoftInputMode(16);
                    }
                } else if (which == 13) {
                    SharedConfig.toggleDisableVoiceAudioEffects();
                } else if (which == 14) {
                    SharedConfig.toggleNoStatusBar();
                    if (ProfileActivity.this.getParentActivity() != null && Build.VERSION.SDK_INT >= 21) {
                        if (SharedConfig.noStatusBar) {
                            ProfileActivity.this.getParentActivity().getWindow().setStatusBarColor(0);
                        } else {
                            ProfileActivity.this.getParentActivity().getWindow().setStatusBarColor(NUM);
                        }
                    }
                } else if (which == 15) {
                    SharedConfig.pendingAppUpdate = null;
                    SharedConfig.saveConfig();
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.appUpdateAvailable, new Object[0]);
                } else if (which == 16) {
                    Set<String> suggestions = ProfileActivity.this.getMessagesController().pendingSuggestions;
                    suggestions.add("VALIDATE_PHONE_NUMBER");
                    suggestions.add("VALIDATE_PASSWORD");
                    ProfileActivity.this.getNotificationCenter().postNotificationName(NotificationCenter.newSuggestionsAvailable, new Object[0]);
                }
            }
        });
        if (this.searchItem != null) {
            RecyclerListView recyclerListView = new RecyclerListView(context2);
            this.searchListView = recyclerListView;
            recyclerListView.setVerticalScrollBarEnabled(false);
            this.searchListView.setLayoutManager(new LinearLayoutManager(context2, 1, false));
            this.searchListView.setGlowColor(Theme.getColor("avatar_backgroundActionBarBlue"));
            this.searchListView.setAdapter(this.searchAdapter);
            this.searchListView.setItemAnimator((RecyclerView.ItemAnimator) null);
            this.searchListView.setVisibility(8);
            this.searchListView.setLayoutAnimation((LayoutAnimationController) null);
            this.searchListView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            frameLayout.addView(this.searchListView, LayoutHelper.createFrame(-1, -1, 51));
            this.searchListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new ProfileActivity$$ExternalSyntheticLambda25(this));
            this.searchListView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new ProfileActivity$$ExternalSyntheticLambda27(this));
            this.searchListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState == 1) {
                        AndroidUtilities.hideKeyboard(ProfileActivity.this.getParentActivity().getCurrentFocus());
                    }
                }
            });
            this.searchListView.setAnimateEmptyView(true, 1);
            StickerEmptyView stickerEmptyView = new StickerEmptyView(context2, (View) null, 1);
            this.emptyView = stickerEmptyView;
            stickerEmptyView.setAnimateLayoutChange(true);
            this.emptyView.subtitle.setVisibility(8);
            this.emptyView.setVisibility(8);
            frameLayout.addView(this.emptyView);
            this.searchAdapter.loadFaqWebPage();
        }
        if (this.banFromGroup != 0) {
            TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(this.banFromGroup));
            if (this.currentChannelParticipant == null) {
                TLRPC.TL_channels_getParticipant req = new TLRPC.TL_channels_getParticipant();
                req.channel = MessagesController.getInputChannel(chat);
                str = "fonts/rmedium.ttf";
                req.participant = getMessagesController().getInputPeer(this.userId);
                getConnectionsManager().sendRequest(req, new ProfileActivity$$ExternalSyntheticLambda20(this));
            } else {
                str = "fonts/rmedium.ttf";
            }
            AnonymousClass13 r1 = new FrameLayout(context2) {
                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    int bottom = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                    Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), bottom);
                    Theme.chat_composeShadowDrawable.draw(canvas);
                    canvas.drawRect(0.0f, (float) bottom, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
                }
            };
            r1.setWillNotDraw(false);
            frameLayout.addView(r1, LayoutHelper.createFrame(-1, 51, 83));
            r1.setOnClickListener(new ProfileActivity$$ExternalSyntheticLambda4(this, chat));
            TextView textView = new TextView(context2);
            textView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText"));
            textView.setTextSize(1, 15.0f);
            textView.setGravity(17);
            textView.setTypeface(AndroidUtilities.getTypeface(str));
            textView.setText(LocaleController.getString("BanFromTheGroup", NUM));
            r1.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 1.0f, 0.0f, 0.0f));
            TLRPC.Chat chat2 = chat;
            AnonymousClass13 r26 = r1;
            this.listView.setPadding(0, AndroidUtilities.dp(88.0f), 0, AndroidUtilities.dp(48.0f));
            this.listView.setBottomGlowOffset(AndroidUtilities.dp(48.0f));
        } else {
            str = "fonts/rmedium.ttf";
            this.listView.setPadding(0, AndroidUtilities.dp(88.0f), 0, 0);
        }
        TopView topView3 = new TopView(context2);
        this.topView = topView3;
        topView3.setBackgroundColor(Theme.getColor("avatar_backgroundActionBarBlue"));
        frameLayout.addView(this.topView);
        this.avatarContainer = new FrameLayout(context2);
        AnonymousClass15 r04 = new FrameLayout(context2) {
            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                if (ProfileActivity.this.transitionOnlineText != null) {
                    canvas.save();
                    canvas.translate(ProfileActivity.this.onlineTextView[0].getX(), ProfileActivity.this.onlineTextView[0].getY());
                    canvas.saveLayerAlpha(0.0f, 0.0f, (float) ProfileActivity.this.transitionOnlineText.getMeasuredWidth(), (float) ProfileActivity.this.transitionOnlineText.getMeasuredHeight(), (int) ((1.0f - ProfileActivity.this.animationProgress) * 255.0f), 31);
                    ProfileActivity.this.transitionOnlineText.draw(canvas);
                    canvas.restore();
                    canvas.restore();
                    invalidate();
                }
            }
        };
        this.avatarContainer2 = r04;
        AndroidUtilities.updateViewVisibilityAnimated(r04, true, 1.0f, false);
        frameLayout.addView(this.avatarContainer2, LayoutHelper.createFrame(-1, -1.0f, 8388611, 0.0f, 0.0f, 0.0f, 0.0f));
        this.avatarContainer.setPivotX(0.0f);
        this.avatarContainer.setPivotY(0.0f);
        this.avatarContainer2.addView(this.avatarContainer, LayoutHelper.createFrame(42, 42.0f, 51, 64.0f, 0.0f, 0.0f, 0.0f));
        AnonymousClass16 r05 = new AvatarImageView(context2) {
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(info);
                if (getImageReceiver().hasNotThumb()) {
                    info.setText(LocaleController.getString("AccDescrProfilePicture", NUM));
                    if (Build.VERSION.SDK_INT >= 21) {
                        info.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, LocaleController.getString("Open", NUM)));
                        info.addAction(new AccessibilityNodeInfo.AccessibilityAction(32, LocaleController.getString("AccDescrOpenInPhotoViewer", NUM)));
                        return;
                    }
                    return;
                }
                info.setVisibleToUser(false);
            }
        };
        this.avatarImage = r05;
        r05.getImageReceiver().setAllowDecodeSingleFrame(true);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(21.0f));
        this.avatarImage.setPivotX(0.0f);
        this.avatarImage.setPivotY(0.0f);
        this.avatarContainer.addView(this.avatarImage, LayoutHelper.createFrame(-1, -1.0f));
        this.avatarImage.setOnClickListener(new ProfileActivity$$ExternalSyntheticLambda2(this));
        this.avatarImage.setOnLongClickListener(new ProfileActivity$$ExternalSyntheticLambda5(this));
        AnonymousClass17 r06 = new RadialProgressView(context2) {
            private Paint paint;

            {
                Paint paint2 = new Paint(1);
                this.paint = paint2;
                paint2.setColor(NUM);
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                if (ProfileActivity.this.avatarImage != null && ProfileActivity.this.avatarImage.getImageReceiver().hasNotThumb()) {
                    this.paint.setAlpha((int) (ProfileActivity.this.avatarImage.getImageReceiver().getCurrentAlpha() * 85.0f));
                    canvas.drawCircle(((float) getMeasuredWidth()) / 2.0f, ((float) getMeasuredHeight()) / 2.0f, ((float) getMeasuredWidth()) / 2.0f, this.paint);
                }
                super.onDraw(canvas);
            }
        };
        this.avatarProgressView = r06;
        r06.setSize(AndroidUtilities.dp(26.0f));
        this.avatarProgressView.setProgressColor(-1);
        this.avatarProgressView.setNoProgress(false);
        this.avatarContainer.addView(this.avatarProgressView, LayoutHelper.createFrame(-1, -1.0f));
        ImageView imageView = new ImageView(context2);
        this.timeItem = imageView;
        imageView.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f));
        this.timeItem.setScaleType(ImageView.ScaleType.CENTER);
        this.timeItem.setAlpha(0.0f);
        ImageView imageView2 = this.timeItem;
        TimerDrawable timerDrawable2 = new TimerDrawable(context2);
        this.timerDrawable = timerDrawable2;
        imageView2.setImageDrawable(timerDrawable2);
        frameLayout.addView(this.timeItem, LayoutHelper.createFrame(34, 34, 51));
        updateTimeItem();
        showAvatarProgress(false, false);
        ProfileGalleryView profileGalleryView = this.avatarsViewPager;
        if (profileGalleryView != null) {
            profileGalleryView.onDestroy();
        }
        this.overlaysView = new OverlaysView(context2);
        long j = this.userId;
        if (j == 0) {
            j = -this.chatId;
        }
        ActionBarMenu actionBarMenu = menu;
        int i = scrollToPosition;
        long j2 = did3;
        AnonymousClass9 r23 = r6;
        ProfileGalleryView profileGalleryView2 = new ProfileGalleryView(context, j, this.actionBar, this.listView, this.avatarImage, getClassGuid(), this.overlaysView);
        this.avatarsViewPager = profileGalleryView2;
        profileGalleryView2.setChatInfo(this.chatInfo);
        this.avatarContainer2.addView(this.avatarsViewPager);
        this.avatarContainer2.addView(this.overlaysView);
        this.avatarImage.setAvatarsViewPager(this.avatarsViewPager);
        PagerIndicatorView pagerIndicatorView = new PagerIndicatorView(context2);
        this.avatarsViewPagerIndicatorView = pagerIndicatorView;
        this.avatarContainer2.addView(pagerIndicatorView, LayoutHelper.createFrame(-1, -1.0f));
        FrameLayout frameLayout2 = frameLayout;
        frameLayout2.addView(this.actionBar);
        int a = 0;
        while (true) {
            SimpleTextView[] simpleTextViewArr = this.nameTextView;
            if (a >= simpleTextViewArr.length) {
                break;
            }
            if (this.playProfileAnimation != 0 || a != 0) {
                simpleTextViewArr[a] = new SimpleTextView(context2);
                if (a == 1) {
                    this.nameTextView[a].setTextColor(Theme.getColor("profile_title"));
                } else {
                    this.nameTextView[a].setTextColor(Theme.getColor("actionBarDefaultTitle"));
                }
                this.nameTextView[a].setTextSize(18);
                this.nameTextView[a].setGravity(3);
                this.nameTextView[a].setTypeface(AndroidUtilities.getTypeface(str));
                this.nameTextView[a].setLeftDrawableTopPadding(-AndroidUtilities.dp(1.3f));
                this.nameTextView[a].setPivotX(0.0f);
                this.nameTextView[a].setPivotY(0.0f);
                this.nameTextView[a].setAlpha(a == 0 ? 0.0f : 1.0f);
                if (a == 1) {
                    this.nameTextView[a].setScrollNonFitText(true);
                    this.nameTextView[a].setImportantForAccessibility(2);
                }
                if (a == 0) {
                    rightMargin = 48 + ((!this.callItemVisible || this.userId == 0) ? 0 : 48);
                } else {
                    rightMargin = 0;
                }
                this.avatarContainer2.addView(this.nameTextView[a], LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, (float) rightMargin, 0.0f));
            }
            a++;
        }
        int a2 = 0;
        while (true) {
            SimpleTextView[] simpleTextViewArr2 = this.onlineTextView;
            if (a2 >= simpleTextViewArr2.length) {
                break;
            }
            simpleTextViewArr2[a2] = new SimpleTextView(context2);
            this.onlineTextView[a2].setTextColor(Theme.getColor("avatar_subtitleInProfileBlue"));
            this.onlineTextView[a2].setTextSize(14);
            this.onlineTextView[a2].setGravity(3);
            this.onlineTextView[a2].setAlpha((a2 == 0 || a2 == 2) ? 0.0f : 1.0f);
            if (a2 > 0) {
                this.onlineTextView[a2].setImportantForAccessibility(2);
            }
            this.avatarContainer2.addView(this.onlineTextView[a2], LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, a2 == 0 ? 48.0f : 8.0f, 0.0f));
            a2++;
        }
        AnonymousClass18 r07 = new AudioPlayerAlert.ClippingTextViewSwitcher(context2) {
            /* access modifiers changed from: protected */
            public TextView createTextView() {
                TextView textView = new TextView(context2);
                textView.setTextColor(Theme.getColor("player_actionBarSubtitle"));
                textView.setTextSize(1, 14.0f);
                textView.setSingleLine(true);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setGravity(3);
                return textView;
            }
        };
        this.mediaCounterTextView = r07;
        r07.setAlpha(0.0f);
        this.avatarContainer2.addView(this.mediaCounterTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, 8.0f, 0.0f));
        updateProfileData();
        this.writeButton = new RLottieImageView(context2);
        Drawable shadowDrawable = context.getResources().getDrawable(NUM).mutate();
        shadowDrawable.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
        CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable, Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("profile_actionBackground"), Theme.getColor("profile_actionPressedBackground")), 0, 0);
        combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
        this.writeButton.setBackgroundDrawable(combinedDrawable);
        if (this.userId == 0) {
            this.writeButton.setImageResource(NUM);
            this.writeButton.setContentDescription(LocaleController.getString("ViewDiscussion", NUM));
        } else if (this.imageUpdater != null) {
            RLottieDrawable rLottieDrawable = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f), false, (int[]) null);
            this.cameraDrawable = rLottieDrawable;
            this.writeButton.setAnimation(rLottieDrawable);
            this.writeButton.setContentDescription(LocaleController.getString("AccDescrChangeProfilePicture", NUM));
            this.writeButton.setPadding(AndroidUtilities.dp(2.0f), 0, 0, AndroidUtilities.dp(2.0f));
        } else {
            this.writeButton.setImageResource(NUM);
            this.writeButton.setContentDescription(LocaleController.getString("AccDescrOpenChat", NUM));
        }
        this.writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("profile_actionIcon"), PorterDuff.Mode.MULTIPLY));
        this.writeButton.setScaleType(ImageView.ScaleType.CENTER);
        frameLayout2.addView(this.writeButton, LayoutHelper.createFrame(60, 60.0f, 53, 0.0f, 0.0f, 16.0f, 0.0f));
        this.writeButton.setOnClickListener(new ProfileActivity$$ExternalSyntheticLambda3(this));
        needLayout(false);
        if (!(scrollTo == -1 || writeButtonTag == null)) {
            this.writeButton.setTag(0);
            this.writeButton.setScaleX(0.2f);
            this.writeButton.setScaleY(0.2f);
            this.writeButton.setAlpha(0.0f);
        }
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                boolean z = true;
                if (newState == 1) {
                    AndroidUtilities.hideKeyboard(ProfileActivity.this.getParentActivity().getCurrentFocus());
                }
                if (ProfileActivity.this.openingAvatar && newState != 2) {
                    boolean unused = ProfileActivity.this.openingAvatar = false;
                }
                if (ProfileActivity.this.searchItem != null) {
                    boolean unused2 = ProfileActivity.this.scrolling = newState != 0;
                    ActionBarMenuItem access$2600 = ProfileActivity.this.searchItem;
                    if (ProfileActivity.this.scrolling || ProfileActivity.this.isPulledDown) {
                        z = false;
                    }
                    access$2600.setEnabled(z);
                }
                ProfileActivity.this.sharedMediaLayout.scrollingByUser = ProfileActivity.this.listView.scrollingByUser;
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (ProfileActivity.this.fwdRestrictedHint != null) {
                    ProfileActivity.this.fwdRestrictedHint.hide();
                }
                ProfileActivity.this.checkListViewScroll();
                boolean z = false;
                if (ProfileActivity.this.participantsMap != null && !ProfileActivity.this.usersEndReached && ProfileActivity.this.layoutManager.findLastVisibleItemPosition() > ProfileActivity.this.membersEndRow - 8) {
                    ProfileActivity.this.getChannelParticipants(false);
                }
                SharedMediaLayout access$2100 = ProfileActivity.this.sharedMediaLayout;
                if (ProfileActivity.this.sharedMediaLayout.getY() == 0.0f) {
                    z = true;
                }
                access$2100.setPinnedToTop(z);
            }
        });
        UndoView undoView2 = new UndoView(context2);
        this.undoView = undoView2;
        frameLayout2.addView(undoView2, LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.expandAnimator = ofFloat;
        ofFloat.addUpdateListener(new ProfileActivity$$ExternalSyntheticLambda0(this));
        this.expandAnimator.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
        this.expandAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animation) {
            }

            public void onAnimationEnd(Animator animation) {
                ProfileActivity.this.actionBar.setItemsBackgroundColor(ProfileActivity.this.isPulledDown ? NUM : Theme.getColor("avatar_actionBarSelectorBlue"), false);
                ProfileActivity.this.avatarImage.clearForeground();
                boolean unused = ProfileActivity.this.doNotSetForeground = false;
            }
        });
        updateRowsIds();
        updateSelectedMediaTabText();
        HintView hintView = new HintView(getParentActivity(), 9);
        this.fwdRestrictedHint = hintView;
        hintView.setAlpha(0.0f);
        frameLayout2.addView(this.fwdRestrictedHint, LayoutHelper.createFrame(-2, -2.0f, 51, 12.0f, 0.0f, 12.0f, 0.0f));
        this.sharedMediaLayout.setForwardRestrictedHint(this.fwdRestrictedHint);
        if (Build.VERSION.SDK_INT >= 21) {
            decorView = (ViewGroup) getParentActivity().getWindow().getDecorView();
        } else {
            decorView = frameLayout2;
        }
        AnonymousClass21 r4 = new PinchToZoomHelper(decorView, frameLayout2) {
            Paint statusBarPaint;

            /* access modifiers changed from: protected */
            public void invalidateViews() {
                super.invalidateViews();
                ProfileActivity.this.fragmentView.invalidate();
                for (int i = 0; i < ProfileActivity.this.avatarsViewPager.getChildCount(); i++) {
                    ProfileActivity.this.avatarsViewPager.getChildAt(i).invalidate();
                }
                if (ProfileActivity.this.writeButton != null) {
                    ProfileActivity.this.writeButton.invalidate();
                }
            }

            /* access modifiers changed from: protected */
            public void drawOverlays(Canvas canvas, float alpha, float parentOffsetX, float parentOffsetY, float clipTop, float clipBottom) {
                if (alpha > 0.0f) {
                    AndroidUtilities.rectTmp.set(0.0f, 0.0f, (float) ProfileActivity.this.avatarsViewPager.getMeasuredWidth(), (float) (ProfileActivity.this.avatarsViewPager.getMeasuredHeight() + AndroidUtilities.dp(30.0f)));
                    canvas.saveLayerAlpha(AndroidUtilities.rectTmp, (int) (255.0f * alpha), 31);
                    ProfileActivity.this.avatarContainer2.draw(canvas);
                    if (ProfileActivity.this.actionBar.getOccupyStatusBar()) {
                        if (this.statusBarPaint == null) {
                            Paint paint = new Paint();
                            this.statusBarPaint = paint;
                            paint.setColor(ColorUtils.setAlphaComponent(-16777216, 51));
                        }
                        canvas.drawRect(ProfileActivity.this.actionBar.getX(), ProfileActivity.this.actionBar.getY(), ProfileActivity.this.actionBar.getX() + ((float) ProfileActivity.this.actionBar.getMeasuredWidth()), ProfileActivity.this.actionBar.getY() + ((float) AndroidUtilities.statusBarHeight), this.statusBarPaint);
                    }
                    canvas.save();
                    canvas.translate(ProfileActivity.this.actionBar.getX(), ProfileActivity.this.actionBar.getY());
                    ProfileActivity.this.actionBar.draw(canvas);
                    canvas.restore();
                    if (ProfileActivity.this.writeButton != null && ProfileActivity.this.writeButton.getVisibility() == 0 && ProfileActivity.this.writeButton.getAlpha() > 0.0f) {
                        canvas.save();
                        float s = (alpha * 0.5f) + 0.5f;
                        canvas.scale(s, s, ProfileActivity.this.writeButton.getX() + (((float) ProfileActivity.this.writeButton.getMeasuredWidth()) / 2.0f), ProfileActivity.this.writeButton.getY() + (((float) ProfileActivity.this.writeButton.getMeasuredHeight()) / 2.0f));
                        canvas.translate(ProfileActivity.this.writeButton.getX(), ProfileActivity.this.writeButton.getY());
                        ProfileActivity.this.writeButton.draw(canvas);
                        canvas.restore();
                    }
                    canvas.restore();
                }
            }

            /* access modifiers changed from: protected */
            public boolean zoomEnabled(View child, ImageReceiver receiver) {
                if (super.zoomEnabled(child, receiver) && ProfileActivity.this.listView.getScrollState() != 1) {
                    return true;
                }
                return false;
            }
        };
        this.pinchToZoomHelper = r4;
        r4.setCallback(new PinchToZoomHelper.Callback() {
            public /* synthetic */ TextureView getCurrentTextureView() {
                return PinchToZoomHelper.Callback.CC.$default$getCurrentTextureView(this);
            }

            public /* synthetic */ void onZoomFinished(MessageObject messageObject) {
                PinchToZoomHelper.Callback.CC.$default$onZoomFinished(this, messageObject);
            }

            public void onZoomStarted(MessageObject messageObject) {
                ProfileActivity.this.listView.cancelClickRunnables(true);
                if (!(ProfileActivity.this.sharedMediaLayout == null || ProfileActivity.this.sharedMediaLayout.getCurrentListView() == null)) {
                    ProfileActivity.this.sharedMediaLayout.getCurrentListView().cancelClickRunnables(true);
                }
                Bitmap bitmap = ProfileActivity.this.pinchToZoomHelper.getPhotoImage() == null ? null : ProfileActivity.this.pinchToZoomHelper.getPhotoImage().getBitmap();
                if (bitmap != null) {
                    ProfileActivity.this.topView.setBackgroundColor(ColorUtils.blendARGB(AndroidUtilities.calcBitmapColor(bitmap), Theme.getColor("windowBackgroundWhite"), 0.1f));
                }
            }
        });
        this.avatarsViewPager.setPinchToZoomHelper(this.pinchToZoomHelper);
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-ProfileActivity  reason: not valid java name */
    public /* synthetic */ void m3691lambda$createView$3$orgtelegramuiProfileActivity(long did, View view, int position, float x, float y) {
        TLRPC.ChatParticipant participant;
        long flags;
        long j = did;
        int i = position;
        if (getParentActivity() != null) {
            if (i == this.settingsKeyRow) {
                Bundle args = new Bundle();
                args.putInt("chat_id", DialogObject.getEncryptedChatId(this.dialogId));
                presentFragment(new IdenticonActivity(args));
            } else if (i == this.settingsTimerRow) {
                showDialog(AlertsCreator.createTTLAlert(getParentActivity(), this.currentEncryptedChat, (Theme.ResourcesProvider) null).create());
            } else if (i == this.notificationsRow) {
                if ((!LocaleController.isRTL || x > ((float) AndroidUtilities.dp(76.0f))) && (LocaleController.isRTL || x < ((float) (view.getMeasuredWidth() - AndroidUtilities.dp(76.0f))))) {
                    AlertsCreator.showCustomNotificationsDialog(this, did, -1, (ArrayList<NotificationsSettingsActivity.NotificationException>) null, this.currentAccount, new ProfileActivity$$ExternalSyntheticLambda19(this));
                    return;
                }
                NotificationsCheckCell checkCell = (NotificationsCheckCell) view;
                boolean checked = !checkCell.isChecked();
                boolean defaultEnabled = getNotificationsController().isGlobalNotificationsEnabled(j);
                if (checked) {
                    SharedPreferences.Editor editor = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                    if (defaultEnabled) {
                        editor.remove("notify2_" + j);
                    } else {
                        editor.putInt("notify2_" + j, 0);
                    }
                    getMessagesStorage().setDialogFlags(j, 0);
                    editor.commit();
                    TLRPC.Dialog dialog = getMessagesController().dialogs_dict.get(j);
                    if (dialog != null) {
                        dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
                    }
                } else {
                    SharedPreferences.Editor editor2 = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                    if (!defaultEnabled) {
                        editor2.remove("notify2_" + j);
                        flags = 0;
                    } else {
                        editor2.putInt("notify2_" + j, 2);
                        flags = 1;
                    }
                    getNotificationsController().removeNotificationsForDialog(j);
                    getMessagesStorage().setDialogFlags(j, flags);
                    editor2.commit();
                    TLRPC.Dialog dialog2 = getMessagesController().dialogs_dict.get(j);
                    if (dialog2 != null) {
                        dialog2.notify_settings = new TLRPC.TL_peerNotifySettings();
                        if (defaultEnabled) {
                            dialog2.notify_settings.mute_until = Integer.MAX_VALUE;
                        }
                    }
                }
                getNotificationsController().updateServerNotificationsSettings(j);
                checkCell.setChecked(checked);
                RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findViewHolderForPosition(this.notificationsRow);
                if (holder != null) {
                    this.listAdapter.onBindViewHolder(holder, this.notificationsRow);
                }
            } else if (i == this.unblockRow) {
                getMessagesController().unblockPeer(this.userId);
                if (BulletinFactory.canShowBulletin(this)) {
                    BulletinFactory.createBanBulletin(this, false).show();
                }
            } else if (i == this.sendMessageRow) {
                onWriteButtonClick();
            } else if (i == this.reportRow) {
                AlertsCreator.createReportAlert(getParentActivity(), getDialogId(), 0, this);
            } else if (i >= this.membersStartRow && i < this.membersEndRow) {
                if (!this.sortedUsers.isEmpty()) {
                    participant = this.chatInfo.participants.participants.get(this.sortedUsers.get(i - this.membersStartRow).intValue());
                } else {
                    participant = this.chatInfo.participants.participants.get(i - this.membersStartRow);
                }
                onMemberClick(participant, false);
            } else if (i == this.addMemberRow) {
                openAddMember();
            } else if (i == this.usernameRow) {
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
            } else if (i == this.locationRow) {
                if (this.chatInfo.location instanceof TLRPC.TL_channelLocation) {
                    LocationActivity fragment = new LocationActivity(5);
                    fragment.setChatLocation(this.chatId, (TLRPC.TL_channelLocation) this.chatInfo.location);
                    presentFragment(fragment);
                }
            } else if (i == this.joinRow) {
                getMessagesController().addUserToChat(this.currentChat.id, getUserConfig().getCurrentUser(), 0, (String) null, this, (Runnable) null);
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.closeSearchByActiveAction, new Object[0]);
            } else if (i == this.subscribersRow) {
                Bundle args2 = new Bundle();
                args2.putLong("chat_id", this.chatId);
                args2.putInt("type", 2);
                ChatUsersActivity fragment2 = new ChatUsersActivity(args2);
                fragment2.setInfo(this.chatInfo);
                presentFragment(fragment2);
            } else if (i == this.subscribersRequestsRow) {
                presentFragment(new MemberRequestsActivity(this.chatId));
            } else if (i == this.administratorsRow) {
                Bundle args3 = new Bundle();
                args3.putLong("chat_id", this.chatId);
                args3.putInt("type", 1);
                ChatUsersActivity fragment3 = new ChatUsersActivity(args3);
                fragment3.setInfo(this.chatInfo);
                presentFragment(fragment3);
            } else if (i == this.blockedUsersRow) {
                Bundle args4 = new Bundle();
                args4.putLong("chat_id", this.chatId);
                args4.putInt("type", 0);
                ChatUsersActivity fragment4 = new ChatUsersActivity(args4);
                fragment4.setInfo(this.chatInfo);
                presentFragment(fragment4);
            } else if (i == this.notificationRow) {
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
                sendLogs(false);
            } else if (i == this.sendLastLogsRow) {
                sendLogs(true);
            } else if (i == this.clearLogsRow) {
                FileLog.cleanupLogs();
            } else if (i == this.switchBackendRow) {
                if (getParentActivity() != null) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder((Context) getParentActivity());
                    builder1.setMessage(LocaleController.getString("AreYouSure", NUM));
                    builder1.setTitle(LocaleController.getString("AppName", NUM));
                    builder1.setPositiveButton(LocaleController.getString("OK", NUM), new ProfileActivity$$ExternalSyntheticLambda29(this));
                    builder1.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    showDialog(builder1.create());
                }
            } else if (i == this.languageRow) {
                presentFragment(new LanguageSelectActivity());
            } else if (i == this.setUsernameRow) {
                presentFragment(new ChangeUsernameActivity());
            } else if (i == this.bioRow) {
                if (this.userInfo != null) {
                    presentFragment(new ChangeBioActivity());
                }
            } else if (i == this.numberRow) {
                presentFragment(new ActionIntroActivity(3));
            } else if (i == this.setAvatarRow) {
                onWriteButtonClick();
            } else {
                processOnClickOrPress(i);
            }
        }
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-ProfileActivity  reason: not valid java name */
    public /* synthetic */ void m3685lambda$createView$1$orgtelegramuiProfileActivity(int param) {
        this.listAdapter.notifyItemChanged(this.notificationsRow);
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-ProfileActivity  reason: not valid java name */
    public /* synthetic */ void m3690lambda$createView$2$orgtelegramuiProfileActivity(DialogInterface dialogInterface, int i) {
        SharedConfig.pushAuthKey = null;
        SharedConfig.pushAuthKeyId = null;
        SharedConfig.saveConfig();
        getConnectionsManager().switchBackend(true);
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-ProfileActivity  reason: not valid java name */
    public /* synthetic */ void m3692lambda$createView$4$orgtelegramuiProfileActivity(View view, int position) {
        if (position >= 0) {
            Object object = Integer.valueOf(this.numberRow);
            boolean add = true;
            if (!this.searchAdapter.searchWas) {
                if (!this.searchAdapter.recentSearches.isEmpty()) {
                    position--;
                }
                if (position < 0 || position >= this.searchAdapter.recentSearches.size()) {
                    int position2 = position - (this.searchAdapter.recentSearches.size() + 1);
                    if (position2 >= 0 && position2 < this.searchAdapter.faqSearchArray.size()) {
                        object = this.searchAdapter.faqSearchArray.get(position2);
                        add = false;
                    }
                } else {
                    object = this.searchAdapter.recentSearches.get(position);
                }
            } else if (position < this.searchAdapter.searchResults.size()) {
                object = this.searchAdapter.searchResults.get(position);
            } else {
                int position3 = position - (this.searchAdapter.searchResults.size() + 1);
                if (position3 >= 0 && position3 < this.searchAdapter.faqSearchResults.size()) {
                    object = this.searchAdapter.faqSearchResults.get(position3);
                }
            }
            if (object instanceof SearchAdapter.SearchResult) {
                ((SearchAdapter.SearchResult) object).open();
            } else if (object instanceof MessagesController.FaqSearchResult) {
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.openArticle, this.searchAdapter.faqWebPage, ((MessagesController.FaqSearchResult) object).url);
            }
            if (add && object != null) {
                this.searchAdapter.addRecent(object);
            }
        }
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-ProfileActivity  reason: not valid java name */
    public /* synthetic */ boolean m3694lambda$createView$6$orgtelegramuiProfileActivity(View view, int position) {
        if (this.searchAdapter.isSearchWas() || this.searchAdapter.recentSearches.isEmpty()) {
            return false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setMessage(LocaleController.getString("ClearSearch", NUM));
        builder.setPositiveButton(LocaleController.getString("ClearButton", NUM).toUpperCase(), new ProfileActivity$$ExternalSyntheticLambda30(this));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        showDialog(builder.create());
        return true;
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-ProfileActivity  reason: not valid java name */
    public /* synthetic */ void m3693lambda$createView$5$orgtelegramuiProfileActivity(DialogInterface dialogInterface, int i) {
        this.searchAdapter.clearRecent();
    }

    /* renamed from: lambda$createView$8$org-telegram-ui-ProfileActivity  reason: not valid java name */
    public /* synthetic */ void m3696lambda$createView$8$orgtelegramuiProfileActivity(TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new ProfileActivity$$ExternalSyntheticLambda9(this, response));
        }
    }

    /* renamed from: lambda$createView$7$org-telegram-ui-ProfileActivity  reason: not valid java name */
    public /* synthetic */ void m3695lambda$createView$7$orgtelegramuiProfileActivity(TLObject response) {
        this.currentChannelParticipant = ((TLRPC.TL_channels_channelParticipant) response).participant;
    }

    /* renamed from: lambda$createView$9$org-telegram-ui-ProfileActivity  reason: not valid java name */
    public /* synthetic */ void m3697lambda$createView$9$orgtelegramuiProfileActivity(TLRPC.Chat chat, View v) {
        long j = this.userId;
        long j2 = this.banFromGroup;
        TLRPC.TL_chatBannedRights tL_chatBannedRights = chat.default_banned_rights;
        TLRPC.ChannelParticipant channelParticipant = this.currentChannelParticipant;
        ChatRightsEditActivity fragment = new ChatRightsEditActivity(j, j2, (TLRPC.TL_chatAdminRights) null, tL_chatBannedRights, channelParticipant != null ? channelParticipant.banned_rights : null, "", 1, true, false);
        fragment.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() {
            public void didSetRights(int rights, TLRPC.TL_chatAdminRights rightsAdmin, TLRPC.TL_chatBannedRights rightsBanned, String rank) {
                ProfileActivity.this.removeSelfFromStack();
            }

            public void didChangeOwner(TLRPC.User user) {
                ProfileActivity.this.undoView.showWithAction(-ProfileActivity.this.chatId, ProfileActivity.this.currentChat.megagroup ? 10 : 9, (Object) user);
            }
        });
        presentFragment(fragment);
    }

    /* renamed from: lambda$createView$10$org-telegram-ui-ProfileActivity  reason: not valid java name */
    public /* synthetic */ void m3686lambda$createView$10$orgtelegramuiProfileActivity(View v) {
        RecyclerView.ViewHolder holder;
        Integer offset;
        if (this.avatarBig == null) {
            if (!AndroidUtilities.isTablet() && !this.isInLandscapeMode && this.avatarImage.getImageReceiver().hasNotThumb()) {
                this.openingAvatar = true;
                this.allowPullingDown = true;
                View child = null;
                int i = 0;
                while (true) {
                    if (i >= this.listView.getChildCount()) {
                        break;
                    }
                    RecyclerListView recyclerListView = this.listView;
                    if (recyclerListView.getChildAdapterPosition(recyclerListView.getChildAt(i)) == 0) {
                        child = this.listView.getChildAt(i);
                        break;
                    }
                    i++;
                }
                if (!(child == null || (holder = this.listView.findContainingViewHolder(child)) == null || (offset = this.positionToOffset.get(Integer.valueOf(holder.getAdapterPosition()))) == null)) {
                    this.listView.smoothScrollBy(0, -(offset.intValue() + ((this.listView.getPaddingTop() - child.getTop()) - this.actionBar.getMeasuredHeight())), CubicBezierInterpolator.EASE_OUT_QUINT);
                    return;
                }
            }
            openAvatar();
        }
    }

    /* renamed from: lambda$createView$11$org-telegram-ui-ProfileActivity  reason: not valid java name */
    public /* synthetic */ boolean m3687lambda$createView$11$orgtelegramuiProfileActivity(View v) {
        if (this.avatarBig != null) {
            return false;
        }
        openAvatar();
        return false;
    }

    /* renamed from: lambda$createView$12$org-telegram-ui-ProfileActivity  reason: not valid java name */
    public /* synthetic */ void m3688lambda$createView$12$orgtelegramuiProfileActivity(View v) {
        if (this.writeButton.getTag() == null) {
            onWriteButtonClick();
        }
    }

    /* renamed from: lambda$createView$13$org-telegram-ui-ProfileActivity  reason: not valid java name */
    public /* synthetic */ void m3689lambda$createView$13$orgtelegramuiProfileActivity(ValueAnimator anim) {
        int statusColor;
        int newTop = ActionBar.getCurrentActionBarHeight() + (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0);
        float[] fArr = this.expandAnimatorValues;
        float animatedFraction = anim.getAnimatedFraction();
        this.currentExpanAnimatorFracture = animatedFraction;
        float value = AndroidUtilities.lerp(fArr, animatedFraction);
        this.avatarContainer.setScaleX(this.avatarScale);
        this.avatarContainer.setScaleY(this.avatarScale);
        this.avatarContainer.setTranslationX(AndroidUtilities.lerp(this.avatarX, 0.0f, value));
        this.avatarContainer.setTranslationY(AndroidUtilities.lerp((float) Math.ceil((double) this.avatarY), 0.0f, value));
        this.avatarImage.setRoundRadius((int) AndroidUtilities.lerp(AndroidUtilities.dpf2(21.0f), 0.0f, value));
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.setAlpha(1.0f - value);
        }
        if (this.extraHeight > ((float) AndroidUtilities.dp(88.0f)) && this.expandProgress < 0.33f) {
            refreshNameAndOnlineXY();
        }
        ScamDrawable scamDrawable2 = this.scamDrawable;
        if (scamDrawable2 != null) {
            scamDrawable2.setColor(ColorUtils.blendARGB(Theme.getColor("avatar_subtitleInProfileBlue"), Color.argb(179, 255, 255, 255), value));
        }
        Drawable drawable = this.lockIconDrawable;
        if (drawable != null) {
            drawable.setColorFilter(ColorUtils.blendARGB(Theme.getColor("chat_lockIcon"), -1, value), PorterDuff.Mode.MULTIPLY);
        }
        CrossfadeDrawable crossfadeDrawable = this.verifiedCrossfadeDrawable;
        if (crossfadeDrawable != null) {
            crossfadeDrawable.setProgress(value);
        }
        float k = AndroidUtilities.dpf2(8.0f);
        float nameTextViewXEnd = AndroidUtilities.dpf2(16.0f) - ((float) this.nameTextView[1].getLeft());
        float nameTextViewYEnd = ((((float) newTop) + this.extraHeight) - AndroidUtilities.dpf2(38.0f)) - ((float) this.nameTextView[1].getBottom());
        float f = this.nameX;
        float f2 = this.nameY;
        float nameTextViewX = ((1.0f - value) * (1.0f - value) * f) + ((1.0f - value) * 2.0f * value * (k + f + ((nameTextViewXEnd - f) / 2.0f))) + (value * value * nameTextViewXEnd);
        float onlineTextViewXEnd = AndroidUtilities.dpf2(16.0f) - ((float) this.onlineTextView[1].getLeft());
        float onlineTextViewYEnd = ((((float) newTop) + this.extraHeight) - AndroidUtilities.dpf2(18.0f)) - ((float) this.onlineTextView[1].getBottom());
        float f3 = this.onlineX;
        float f4 = this.onlineY;
        float onlineTextViewX = ((1.0f - value) * (1.0f - value) * f3) + ((1.0f - value) * 2.0f * value * (k + f3 + ((onlineTextViewXEnd - f3) / 2.0f))) + (value * value * onlineTextViewXEnd);
        float onlineTextViewY = ((1.0f - value) * (1.0f - value) * f4) + ((1.0f - value) * 2.0f * value * (k + f4 + ((onlineTextViewYEnd - f4) / 2.0f))) + (value * value * onlineTextViewYEnd);
        this.nameTextView[1].setTranslationX(nameTextViewX);
        this.nameTextView[1].setTranslationY(((1.0f - value) * (1.0f - value) * f2) + ((1.0f - value) * 2.0f * value * (k + f2 + ((nameTextViewYEnd - f2) / 2.0f))) + (value * value * nameTextViewYEnd));
        this.onlineTextView[1].setTranslationX(onlineTextViewX);
        this.onlineTextView[1].setTranslationY(onlineTextViewY);
        this.mediaCounterTextView.setTranslationX(onlineTextViewX);
        this.mediaCounterTextView.setTranslationY(onlineTextViewY);
        Object onlineTextViewTag = this.onlineTextView[1].getTag();
        if (onlineTextViewTag instanceof String) {
            statusColor = Theme.getColor((String) onlineTextViewTag);
        } else {
            statusColor = Theme.getColor("avatar_subtitleInProfileBlue");
        }
        float f5 = nameTextViewX;
        this.onlineTextView[1].setTextColor(ColorUtils.blendARGB(statusColor, Color.argb(179, 255, 255, 255), value));
        if (this.extraHeight > ((float) AndroidUtilities.dp(88.0f))) {
            SimpleTextView[] simpleTextViewArr = this.nameTextView;
            simpleTextViewArr[1].setPivotY(AndroidUtilities.lerp(0.0f, (float) simpleTextViewArr[1].getMeasuredHeight(), value));
            float f6 = k;
            this.nameTextView[1].setScaleX(AndroidUtilities.lerp(1.12f, 1.67f, value));
            this.nameTextView[1].setScaleY(AndroidUtilities.lerp(1.12f, 1.67f, value));
        }
        needLayoutText(Math.min(1.0f, this.extraHeight / ((float) AndroidUtilities.dp(88.0f))));
        this.nameTextView[1].setTextColor(ColorUtils.blendARGB(Theme.getColor("profile_title"), -1, value));
        this.actionBar.setItemsColor(ColorUtils.blendARGB(Theme.getColor("actionBarDefaultIcon"), -1, value), false);
        this.avatarImage.setForegroundAlpha(value);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) this.avatarContainer.getLayoutParams();
        params.width = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(42.0f), ((float) this.listView.getMeasuredWidth()) / this.avatarScale, value);
        params.height = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(42.0f), (this.extraHeight + ((float) newTop)) / this.avatarScale, value);
        params.leftMargin = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(64.0f), 0.0f, value);
        this.avatarContainer.requestLayout();
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

    public TLRPC.Chat getCurrentChat() {
        return this.currentChat;
    }

    public boolean isFragmentOpened() {
        return this.isFragmentOpened;
    }

    private void openAvatar() {
        ImageLocation videoLocation;
        if (this.listView.getScrollState() != 1) {
            if (this.userId != 0) {
                TLRPC.User user = getMessagesController().getUser(Long.valueOf(this.userId));
                if (user.photo != null && user.photo.photo_big != null) {
                    PhotoViewer.getInstance().setParentActivity(getParentActivity());
                    if (user.photo.dc_id != 0) {
                        user.photo.photo_big.dc_id = user.photo.dc_id;
                    }
                    PhotoViewer.getInstance().openPhoto(user.photo.photo_big, this.provider);
                }
            } else if (this.chatId != 0) {
                TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(this.chatId));
                if (chat.photo != null && chat.photo.photo_big != null) {
                    PhotoViewer.getInstance().setParentActivity(getParentActivity());
                    if (chat.photo.dc_id != 0) {
                        chat.photo.photo_big.dc_id = chat.photo.dc_id;
                    }
                    TLRPC.ChatFull chatFull = this.chatInfo;
                    if (chatFull == null || !(chatFull.chat_photo instanceof TLRPC.TL_photo) || this.chatInfo.chat_photo.video_sizes.isEmpty()) {
                        videoLocation = null;
                    } else {
                        videoLocation = ImageLocation.getForPhoto(this.chatInfo.chat_photo.video_sizes.get(0), this.chatInfo.chat_photo);
                    }
                    PhotoViewer.getInstance().openPhotoWithVideo(chat.photo.photo_big, videoLocation, this.provider);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void onWriteButtonClick() {
        if (this.userId != 0) {
            boolean z = true;
            if (this.imageUpdater != null) {
                TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
                if (user == null) {
                    user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
                }
                if (user != null) {
                    ImageUpdater imageUpdater2 = this.imageUpdater;
                    if (user.photo == null || user.photo.photo_big == null || (user.photo instanceof TLRPC.TL_userProfilePhotoEmpty)) {
                        z = false;
                    }
                    imageUpdater2.openMenu(z, new ProfileActivity$$ExternalSyntheticLambda7(this), new ProfileActivity$$ExternalSyntheticLambda1(this));
                    this.cameraDrawable.setCurrentFrame(0);
                    this.cameraDrawable.setCustomEndFrame(43);
                    this.writeButton.playAnimation();
                }
            } else if (this.playProfileAnimation == 0 || !(this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 2) instanceof ChatActivity)) {
                TLRPC.User user2 = getMessagesController().getUser(Long.valueOf(this.userId));
                if (user2 != null && !(user2 instanceof TLRPC.TL_userEmpty)) {
                    Bundle args = new Bundle();
                    args.putLong("user_id", this.userId);
                    if (getMessagesController().checkCanOpenChat(args, this)) {
                        boolean removeFragment = this.arguments.getBoolean("removeFragmentOnChatOpen", true);
                        if (!AndroidUtilities.isTablet() && removeFragment) {
                            getNotificationCenter().removeObserver(this, NotificationCenter.closeChats);
                            getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                        }
                        int distance = getArguments().getInt("nearby_distance", -1);
                        if (distance >= 0) {
                            args.putInt("nearby_distance", distance);
                        }
                        ChatActivity chatActivity = new ChatActivity(args);
                        chatActivity.setPreloadedSticker(getMediaDataController().getGreetingsSticker(), false);
                        presentFragment(chatActivity, removeFragment);
                        if (AndroidUtilities.isTablet()) {
                            finishFragment();
                        }
                    }
                }
            } else {
                finishFragment();
            }
        } else {
            openDiscussion();
        }
    }

    /* renamed from: lambda$onWriteButtonClick$14$org-telegram-ui-ProfileActivity  reason: not valid java name */
    public /* synthetic */ void m3710lambda$onWriteButtonClick$14$orgtelegramuiProfileActivity() {
        MessagesController.getInstance(this.currentAccount).deleteUserPhoto((TLRPC.InputPhoto) null);
        this.cameraDrawable.setCurrentFrame(0);
    }

    /* renamed from: lambda$onWriteButtonClick$15$org-telegram-ui-ProfileActivity  reason: not valid java name */
    public /* synthetic */ void m3711lambda$onWriteButtonClick$15$orgtelegramuiProfileActivity(DialogInterface dialog) {
        if (!this.imageUpdater.isUploadingImage()) {
            this.cameraDrawable.setCustomEndFrame(86);
            this.writeButton.playAnimation();
            return;
        }
        this.cameraDrawable.setCurrentFrame(0, false);
    }

    /* access modifiers changed from: private */
    public void openDiscussion() {
        TLRPC.ChatFull chatFull = this.chatInfo;
        if (chatFull != null && chatFull.linked_chat_id != 0) {
            Bundle args = new Bundle();
            args.putLong("chat_id", this.chatInfo.linked_chat_id);
            if (getMessagesController().checkCanOpenChat(args, this)) {
                presentFragment(new ChatActivity(args));
            }
        }
    }

    public boolean onMemberClick(TLRPC.ChatParticipant participant, boolean isLong) {
        return onMemberClick(participant, isLong, false);
    }

    public boolean onMemberClick(TLRPC.ChatParticipant participant, boolean isLong, boolean resultOnly) {
        boolean canRestrict;
        boolean allowKick;
        boolean canEditAdmin;
        boolean editingAdmin;
        TLRPC.ChannelParticipant channelParticipant;
        boolean hasRemove;
        String str;
        int i;
        TLRPC.ChatParticipant chatParticipant = participant;
        if (getParentActivity() == null) {
            return false;
        }
        if (isLong) {
            TLRPC.User user = getMessagesController().getUser(Long.valueOf(chatParticipant.user_id));
            if (user == null || chatParticipant.user_id == getUserConfig().getClientUserId()) {
                return false;
            }
            this.selectedUser = chatParticipant.user_id;
            if (ChatObject.isChannel(this.currentChat)) {
                TLRPC.ChannelParticipant channelParticipant2 = ((TLRPC.TL_chatChannelParticipant) chatParticipant).channelParticipant;
                TLRPC.User user2 = getMessagesController().getUser(Long.valueOf(chatParticipant.user_id));
                boolean canEditAdmin2 = ChatObject.canAddAdmins(this.currentChat);
                if (canEditAdmin2 && ((channelParticipant2 instanceof TLRPC.TL_channelParticipantCreator) || ((channelParticipant2 instanceof TLRPC.TL_channelParticipantAdmin) && !channelParticipant2.can_edit))) {
                    canEditAdmin2 = false;
                }
                boolean allowKick2 = ChatObject.canBlockUsers(this.currentChat) && ((!(channelParticipant2 instanceof TLRPC.TL_channelParticipantAdmin) && !(channelParticipant2 instanceof TLRPC.TL_channelParticipantCreator)) || channelParticipant2.can_edit);
                boolean canRestrict2 = allowKick2;
                if (this.currentChat.gigagroup) {
                    canRestrict2 = false;
                }
                channelParticipant = channelParticipant2;
                editingAdmin = channelParticipant2 instanceof TLRPC.TL_channelParticipantAdmin;
                canEditAdmin = canEditAdmin2;
                allowKick = allowKick2;
                canRestrict = canRestrict2;
            } else {
                boolean allowKick3 = this.currentChat.creator || ((chatParticipant instanceof TLRPC.TL_chatParticipant) && (ChatObject.canBlockUsers(this.currentChat) || chatParticipant.inviter_id == getUserConfig().getClientUserId()));
                channelParticipant = null;
                editingAdmin = chatParticipant instanceof TLRPC.TL_chatParticipantAdmin;
                canEditAdmin = this.currentChat.creator;
                allowKick = allowKick3;
                canRestrict = this.currentChat.creator;
            }
            ArrayList<Integer> arrayList = null;
            ArrayList<String> items = resultOnly ? null : new ArrayList<>();
            ArrayList<Integer> icons = resultOnly ? null : new ArrayList<>();
            if (!resultOnly) {
                arrayList = new ArrayList<>();
            }
            ArrayList<Integer> actions = arrayList;
            if (canEditAdmin) {
                if (resultOnly) {
                    return true;
                }
                if (editingAdmin) {
                    i = NUM;
                    str = "EditAdminRights";
                } else {
                    i = NUM;
                    str = "SetAsAdmin";
                }
                items.add(LocaleController.getString(str, i));
                icons.add(NUM);
                actions.add(0);
            }
            if (canRestrict) {
                if (resultOnly) {
                    return true;
                }
                items.add(LocaleController.getString("ChangePermissions", NUM));
                icons.add(NUM);
                actions.add(1);
            }
            if (!allowKick) {
                hasRemove = false;
            } else if (resultOnly) {
                return true;
            } else {
                items.add(LocaleController.getString("KickFromGroup", NUM));
                icons.add(NUM);
                actions.add(2);
                hasRemove = true;
            }
            if (resultOnly || items.isEmpty()) {
                return false;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            ProfileActivity$$ExternalSyntheticLambda33 profileActivity$$ExternalSyntheticLambda33 = r0;
            int[] intArray = AndroidUtilities.toIntArray(icons);
            boolean z = canEditAdmin;
            CharSequence[] charSequenceArr = (CharSequence[]) items.toArray(new CharSequence[0]);
            boolean z2 = allowKick;
            AlertDialog.Builder builder2 = builder;
            ArrayList<Integer> arrayList2 = actions;
            ArrayList<Integer> arrayList3 = icons;
            ArrayList<String> items2 = items;
            ProfileActivity$$ExternalSyntheticLambda33 profileActivity$$ExternalSyntheticLambda332 = new ProfileActivity$$ExternalSyntheticLambda33(this, actions, participant, channelParticipant, user, editingAdmin);
            builder2.setItems(charSequenceArr, intArray, profileActivity$$ExternalSyntheticLambda332);
            AlertDialog alertDialog = builder2.create();
            showDialog(alertDialog);
            if (hasRemove) {
                alertDialog.setItemColor(items2.size() - 1, Theme.getColor("dialogTextRed2"), Theme.getColor("dialogRedIcon"));
            }
            return true;
        } else if (chatParticipant.user_id == getUserConfig().getClientUserId()) {
            return false;
        } else {
            Bundle args = new Bundle();
            args.putLong("user_id", chatParticipant.user_id);
            args.putBoolean("preload_messages", true);
            presentFragment(new ProfileActivity(args));
            return true;
        }
    }

    /* renamed from: lambda$onMemberClick$17$org-telegram-ui-ProfileActivity  reason: not valid java name */
    public /* synthetic */ void m3709lambda$onMemberClick$17$orgtelegramuiProfileActivity(ArrayList actions, TLRPC.ChatParticipant participant, TLRPC.ChannelParticipant channelParticipant, TLRPC.User user, boolean editingAdmin, DialogInterface dialogInterface, int i) {
        ArrayList arrayList = actions;
        TLRPC.ChatParticipant chatParticipant = participant;
        TLRPC.ChannelParticipant channelParticipant2 = channelParticipant;
        TLRPC.User user2 = user;
        int i2 = i;
        if (((Integer) arrayList.get(i2)).intValue() == 2) {
            kickUser(this.selectedUser, chatParticipant);
            return;
        }
        int action = ((Integer) arrayList.get(i2)).intValue();
        if (action == 1 && ((channelParticipant2 instanceof TLRPC.TL_channelParticipantAdmin) || (chatParticipant instanceof TLRPC.TL_chatParticipantAdmin))) {
            AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
            builder2.setTitle(LocaleController.getString("AppName", NUM));
            builder2.setMessage(LocaleController.formatString("AdminWillBeRemoved", NUM, ContactsController.formatName(user2.first_name, user2.last_name)));
            String string = LocaleController.getString("OK", NUM);
            ProfileActivity$$ExternalSyntheticLambda35 profileActivity$$ExternalSyntheticLambda35 = r0;
            ProfileActivity$$ExternalSyntheticLambda35 profileActivity$$ExternalSyntheticLambda352 = new ProfileActivity$$ExternalSyntheticLambda35(this, channelParticipant, action, user, participant, editingAdmin);
            builder2.setPositiveButton(string, profileActivity$$ExternalSyntheticLambda35);
            builder2.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            showDialog(builder2.create());
        } else if (channelParticipant2 != null) {
            openRightsEdit(action, user, participant, channelParticipant2.admin_rights, channelParticipant2.banned_rights, channelParticipant2.rank, editingAdmin);
        } else {
            openRightsEdit(action, user, participant, (TLRPC.TL_chatAdminRights) null, (TLRPC.TL_chatBannedRights) null, "", editingAdmin);
        }
    }

    /* renamed from: lambda$onMemberClick$16$org-telegram-ui-ProfileActivity  reason: not valid java name */
    public /* synthetic */ void m3708lambda$onMemberClick$16$orgtelegramuiProfileActivity(TLRPC.ChannelParticipant channelParticipant, int action, TLRPC.User user, TLRPC.ChatParticipant participant, boolean editingAdmin, DialogInterface dialog, int which) {
        TLRPC.ChannelParticipant channelParticipant2 = channelParticipant;
        if (channelParticipant2 != null) {
            openRightsEdit(action, user, participant, channelParticipant2.admin_rights, channelParticipant2.banned_rights, channelParticipant2.rank, editingAdmin);
            return;
        }
        openRightsEdit(action, user, participant, (TLRPC.TL_chatAdminRights) null, (TLRPC.TL_chatBannedRights) null, "", editingAdmin);
    }

    private void openRightsEdit(int action, TLRPC.User user, TLRPC.ChatParticipant participant, TLRPC.TL_chatAdminRights adminRights, TLRPC.TL_chatBannedRights bannedRights, String rank, boolean editingAdmin) {
        boolean[] needShowBulletin = new boolean[1];
        final boolean[] zArr = needShowBulletin;
        boolean[] needShowBulletin2 = needShowBulletin;
        final TLRPC.User user2 = user;
        ChatRightsEditActivity fragment = new ChatRightsEditActivity(this, user.id, this.chatId, adminRights, this.currentChat.default_banned_rights, bannedRights, rank, action, true, false) {
            final /* synthetic */ ProfileActivity this$0;

            {
                this.this$0 = this$0;
            }

            /* access modifiers changed from: protected */
            public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
                if (!isOpen && backward && zArr[0] && BulletinFactory.canShowBulletin(this.this$0)) {
                    BulletinFactory.createPromoteToAdminBulletin(this.this$0, user2.first_name).show();
                }
            }
        };
        final int i = action;
        final TLRPC.ChatParticipant chatParticipant = participant;
        final boolean z = editingAdmin;
        final boolean[] zArr2 = needShowBulletin2;
        fragment.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() {
            public void didSetRights(int rights, TLRPC.TL_chatAdminRights rightsAdmin, TLRPC.TL_chatBannedRights rightsBanned, String rank) {
                TLRPC.ChatParticipant newParticipant;
                int i = i;
                if (i == 0) {
                    TLRPC.ChatParticipant chatParticipant = chatParticipant;
                    if (chatParticipant instanceof TLRPC.TL_chatChannelParticipant) {
                        TLRPC.TL_chatChannelParticipant channelParticipant1 = (TLRPC.TL_chatChannelParticipant) chatParticipant;
                        if (rights == 1) {
                            channelParticipant1.channelParticipant = new TLRPC.TL_channelParticipantAdmin();
                            channelParticipant1.channelParticipant.flags |= 4;
                        } else {
                            channelParticipant1.channelParticipant = new TLRPC.TL_channelParticipant();
                        }
                        channelParticipant1.channelParticipant.inviter_id = ProfileActivity.this.getUserConfig().getClientUserId();
                        channelParticipant1.channelParticipant.peer = new TLRPC.TL_peerUser();
                        channelParticipant1.channelParticipant.peer.user_id = chatParticipant.user_id;
                        channelParticipant1.channelParticipant.date = chatParticipant.date;
                        channelParticipant1.channelParticipant.banned_rights = rightsBanned;
                        channelParticipant1.channelParticipant.admin_rights = rightsAdmin;
                        channelParticipant1.channelParticipant.rank = rank;
                    } else if (chatParticipant != null) {
                        if (rights == 1) {
                            newParticipant = new TLRPC.TL_chatParticipantAdmin();
                        } else {
                            newParticipant = new TLRPC.TL_chatParticipant();
                        }
                        newParticipant.user_id = chatParticipant.user_id;
                        newParticipant.date = chatParticipant.date;
                        newParticipant.inviter_id = chatParticipant.inviter_id;
                        int index = ProfileActivity.this.chatInfo.participants.participants.indexOf(chatParticipant);
                        if (index >= 0) {
                            ProfileActivity.this.chatInfo.participants.participants.set(index, newParticipant);
                        }
                    }
                    if (rights == 1 && !z) {
                        zArr2[0] = true;
                    }
                } else if (i == 1 && rights == 0 && ProfileActivity.this.currentChat.megagroup && ProfileActivity.this.chatInfo != null && ProfileActivity.this.chatInfo.participants != null) {
                    boolean changed = false;
                    int a = 0;
                    while (true) {
                        if (a >= ProfileActivity.this.chatInfo.participants.participants.size()) {
                            break;
                        } else if (MessageObject.getPeerId(((TLRPC.TL_chatChannelParticipant) ProfileActivity.this.chatInfo.participants.participants.get(a)).channelParticipant.peer) == chatParticipant.user_id) {
                            ProfileActivity.this.chatInfo.participants_count--;
                            ProfileActivity.this.chatInfo.participants.participants.remove(a);
                            changed = true;
                            break;
                        } else {
                            a++;
                        }
                    }
                    if (ProfileActivity.this.chatInfo != null && ProfileActivity.this.chatInfo.participants != null) {
                        int a2 = 0;
                        while (true) {
                            if (a2 >= ProfileActivity.this.chatInfo.participants.participants.size()) {
                                break;
                            } else if (ProfileActivity.this.chatInfo.participants.participants.get(a2).user_id == chatParticipant.user_id) {
                                ProfileActivity.this.chatInfo.participants.participants.remove(a2);
                                changed = true;
                                break;
                            } else {
                                a2++;
                            }
                        }
                    }
                    if (changed) {
                        ProfileActivity.this.updateOnlineCount(true);
                        ProfileActivity.this.updateRowsIds();
                        ProfileActivity.this.listAdapter.notifyDataSetChanged();
                    }
                }
            }

            public void didChangeOwner(TLRPC.User user) {
                ProfileActivity.this.undoView.showWithAction(-ProfileActivity.this.chatId, ProfileActivity.this.currentChat.megagroup ? 10 : 9, (Object) user);
            }
        });
        presentFragment(fragment);
    }

    /* access modifiers changed from: private */
    public boolean processOnClickOrPress(int position) {
        String username;
        TLRPC.Chat chat;
        TLRPC.UserFull userFull;
        if (position == this.usernameRow || position == this.setUsernameRow) {
            if (this.userId != 0) {
                TLRPC.User user = getMessagesController().getUser(Long.valueOf(this.userId));
                if (user == null || user.username == null) {
                    return false;
                }
                username = user.username;
            } else if (this.chatId == 0 || (chat = getMessagesController().getChat(Long.valueOf(this.chatId))) == null || chat.username == null) {
                return false;
            } else {
                username = chat.username;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setItems(new CharSequence[]{LocaleController.getString("Copy", NUM)}, new ProfileActivity$$ExternalSyntheticLambda32(this, username));
            showDialog(builder.create());
            return true;
        } else if (position == this.phoneRow || position == this.numberRow) {
            TLRPC.User user2 = getMessagesController().getUser(Long.valueOf(this.userId));
            if (user2 == null || user2.phone == null || user2.phone.length() == 0 || getParentActivity() == null) {
                return false;
            }
            AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
            ArrayList<CharSequence> items = new ArrayList<>();
            ArrayList<Integer> actions = new ArrayList<>();
            if (position == this.phoneRow) {
                TLRPC.UserFull userFull2 = this.userInfo;
                if (userFull2 != null && userFull2.phone_calls_available) {
                    items.add(LocaleController.getString("CallViaTelegram", NUM));
                    actions.add(2);
                    if (Build.VERSION.SDK_INT >= 18 && this.userInfo.video_calls_available) {
                        items.add(LocaleController.getString("VideoCallViaTelegram", NUM));
                        actions.add(3);
                    }
                }
                items.add(LocaleController.getString("Call", NUM));
                actions.add(0);
            }
            items.add(LocaleController.getString("Copy", NUM));
            actions.add(1);
            builder2.setItems((CharSequence[]) items.toArray(new CharSequence[0]), new ProfileActivity$$ExternalSyntheticLambda34(this, actions, user2));
            showDialog(builder2.create());
            return true;
        } else if (position != this.channelInfoRow && position != this.userInfoRow && position != this.locationRow && position != this.bioRow) {
            return false;
        } else {
            if (position == this.bioRow && ((userFull = this.userInfo) == null || TextUtils.isEmpty(userFull.about))) {
                return false;
            }
            AlertDialog.Builder builder3 = new AlertDialog.Builder((Context) getParentActivity());
            builder3.setItems(new CharSequence[]{LocaleController.getString("Copy", NUM)}, new ProfileActivity$$ExternalSyntheticLambda31(this, position));
            showDialog(builder3.create());
            return true;
        }
    }

    /* renamed from: lambda$processOnClickOrPress$18$org-telegram-ui-ProfileActivity  reason: not valid java name */
    public /* synthetic */ void m3713lambda$processOnClickOrPress$18$orgtelegramuiProfileActivity(String username, DialogInterface dialogInterface, int i) {
        String text;
        if (i == 0) {
            try {
                ClipboardManager clipboard = (ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard");
                if (this.userId != 0) {
                    text = "@" + username;
                    BulletinFactory.of(this).createCopyBulletin(LocaleController.getString("UsernameCopied", NUM)).show();
                } else {
                    text = "https://" + MessagesController.getInstance(UserConfig.selectedAccount).linkPrefix + "/" + username;
                    BulletinFactory.of(this).createCopyBulletin(LocaleController.getString("LinkCopied", NUM)).show();
                }
                clipboard.setPrimaryClip(ClipData.newPlainText("label", text));
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    /* renamed from: lambda$processOnClickOrPress$19$org-telegram-ui-ProfileActivity  reason: not valid java name */
    public /* synthetic */ void m3714lambda$processOnClickOrPress$19$orgtelegramuiProfileActivity(ArrayList actions, TLRPC.User user, DialogInterface dialogInterface, int i) {
        int i2 = ((Integer) actions.get(i)).intValue();
        if (i2 == 0) {
            try {
                Intent intent = new Intent("android.intent.action.DIAL", Uri.parse("tel:+" + user.phone));
                intent.addFlags(NUM);
                getParentActivity().startActivityForResult(intent, 500);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else if (i2 == 1) {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", "+" + user.phone));
                BulletinFactory.of(this).createCopyBulletin(LocaleController.getString("PhoneCopied", NUM)).show();
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        } else if (i2 == 2 || i2 == 3) {
            boolean z = i2 == 3;
            TLRPC.UserFull userFull = this.userInfo;
            VoIPHelper.startCall(user, z, userFull != null && userFull.video_calls_available, getParentActivity(), this.userInfo, getAccountInstance());
        }
    }

    /* renamed from: lambda$processOnClickOrPress$20$org-telegram-ui-ProfileActivity  reason: not valid java name */
    public /* synthetic */ void m3715lambda$processOnClickOrPress$20$orgtelegramuiProfileActivity(int position, DialogInterface dialogInterface, int i) {
        String about;
        try {
            String str = null;
            if (position == this.locationRow) {
                TLRPC.ChatFull chatFull = this.chatInfo;
                if (chatFull != null && (chatFull.location instanceof TLRPC.TL_channelLocation)) {
                    str = ((TLRPC.TL_channelLocation) this.chatInfo.location).address;
                }
                about = str;
            } else if (position == this.channelInfoRow) {
                TLRPC.ChatFull chatFull2 = this.chatInfo;
                if (chatFull2 != null) {
                    str = chatFull2.about;
                }
                about = str;
            } else {
                TLRPC.UserFull userFull = this.userInfo;
                if (userFull != null) {
                    str = userFull.about;
                }
                about = str;
            }
            if (!TextUtils.isEmpty(about)) {
                AndroidUtilities.addToClipboard(about);
                if (position == this.bioRow) {
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
        AlertsCreator.createClearOrDeleteDialogAlert(this, false, this.currentChat, (TLRPC.User) null, false, true, new ProfileActivity$$ExternalSyntheticLambda18(this));
    }

    /* renamed from: lambda$leaveChatPressed$21$org-telegram-ui-ProfileActivity  reason: not valid java name */
    public /* synthetic */ void m3705lambda$leaveChatPressed$21$orgtelegramuiProfileActivity(boolean param) {
        this.playProfileAnimation = 0;
        getNotificationCenter().removeObserver(this, NotificationCenter.closeChats);
        getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
        finishFragment();
        getNotificationCenter().postNotificationName(NotificationCenter.needDeleteDialog, Long.valueOf(-this.currentChat.id), null, this.currentChat, Boolean.valueOf(param));
    }

    /* access modifiers changed from: private */
    public void getChannelParticipants(boolean reload) {
        LongSparseArray<TLRPC.ChatParticipant> longSparseArray;
        if (!this.loadingUsers && (longSparseArray = this.participantsMap) != null && this.chatInfo != null) {
            this.loadingUsers = true;
            int i = 0;
            int delay = (longSparseArray.size() == 0 || !reload) ? 0 : 300;
            TLRPC.TL_channels_getParticipants req = new TLRPC.TL_channels_getParticipants();
            req.channel = getMessagesController().getInputChannel(this.chatId);
            req.filter = new TLRPC.TL_channelParticipantsRecent();
            if (!reload) {
                i = this.participantsMap.size();
            }
            req.offset = i;
            req.limit = 200;
            getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(req, new ProfileActivity$$ExternalSyntheticLambda23(this, req, delay)), this.classGuid);
        }
    }

    /* renamed from: lambda$getChannelParticipants$23$org-telegram-ui-ProfileActivity  reason: not valid java name */
    public /* synthetic */ void m3703lambda$getChannelParticipants$23$orgtelegramuiProfileActivity(TLRPC.TL_channels_getParticipants req, int delay, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new ProfileActivity$$ExternalSyntheticLambda13(this, error, response, req), (long) delay);
    }

    /* renamed from: lambda$getChannelParticipants$22$org-telegram-ui-ProfileActivity  reason: not valid java name */
    public /* synthetic */ void m3702lambda$getChannelParticipants$22$orgtelegramuiProfileActivity(TLRPC.TL_error error, TLObject response, TLRPC.TL_channels_getParticipants req) {
        if (error == null) {
            TLRPC.TL_channels_channelParticipants res = (TLRPC.TL_channels_channelParticipants) response;
            getMessagesController().putUsers(res.users, false);
            getMessagesController().putChats(res.chats, false);
            if (res.users.size() < 200) {
                this.usersEndReached = true;
            }
            if (req.offset == 0) {
                this.participantsMap.clear();
                this.chatInfo.participants = new TLRPC.TL_chatParticipants();
                getMessagesStorage().putUsersAndChats(res.users, res.chats, true, true);
                getMessagesStorage().updateChannelUsers(this.chatId, res.participants);
            }
            for (int a = 0; a < res.participants.size(); a++) {
                TLRPC.TL_chatChannelParticipant participant = new TLRPC.TL_chatChannelParticipant();
                participant.channelParticipant = res.participants.get(a);
                participant.inviter_id = participant.channelParticipant.inviter_id;
                participant.user_id = MessageObject.getPeerId(participant.channelParticipant.peer);
                participant.date = participant.channelParticipant.date;
                if (this.participantsMap.indexOfKey(participant.user_id) < 0) {
                    if (this.chatInfo.participants == null) {
                        this.chatInfo.participants = new TLRPC.TL_chatParticipants();
                    }
                    this.chatInfo.participants.participants.add(participant);
                    this.participantsMap.put(participant.user_id, participant);
                }
            }
        }
        this.loadingUsers = false;
        updateListAnimated(true);
    }

    private void setMediaHeaderVisible(boolean visible) {
        if (this.mediaHeaderVisible != visible) {
            this.mediaHeaderVisible = visible;
            AnimatorSet animatorSet = this.headerAnimatorSet;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            AnimatorSet animatorSet2 = this.headerShadowAnimatorSet;
            if (animatorSet2 != null) {
                animatorSet2.cancel();
            }
            final ActionBarMenuItem mediaSearchItem = this.sharedMediaLayout.getSearchItem();
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
                    mediaSearchItem.setVisibility(0);
                }
                if (this.sharedMediaLayout.isCalendarItemVisible()) {
                    this.sharedMediaLayout.photoVideoOptionsItem.setVisibility(0);
                } else {
                    this.sharedMediaLayout.photoVideoOptionsItem.setVisibility(4);
                }
            }
            ArrayList<Animator> animators = new ArrayList<>();
            ActionBarMenuItem actionBarMenuItem = this.callItem;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            float f = 1.0f;
            fArr[0] = visible ? 0.0f : 1.0f;
            animators.add(ObjectAnimator.ofFloat(actionBarMenuItem, property, fArr));
            ActionBarMenuItem actionBarMenuItem2 = this.videoCallItem;
            Property property2 = View.ALPHA;
            float[] fArr2 = new float[1];
            fArr2[0] = visible ? 0.0f : 1.0f;
            animators.add(ObjectAnimator.ofFloat(actionBarMenuItem2, property2, fArr2));
            ActionBarMenuItem actionBarMenuItem3 = this.otherItem;
            Property property3 = View.ALPHA;
            float[] fArr3 = new float[1];
            fArr3[0] = visible ? 0.0f : 1.0f;
            animators.add(ObjectAnimator.ofFloat(actionBarMenuItem3, property3, fArr3));
            ActionBarMenuItem actionBarMenuItem4 = this.editItem;
            Property property4 = View.ALPHA;
            float[] fArr4 = new float[1];
            fArr4[0] = visible ? 0.0f : 1.0f;
            animators.add(ObjectAnimator.ofFloat(actionBarMenuItem4, property4, fArr4));
            ActionBarMenuItem actionBarMenuItem5 = this.callItem;
            Property property5 = View.TRANSLATION_Y;
            float[] fArr5 = new float[1];
            fArr5[0] = visible ? (float) (-AndroidUtilities.dp(10.0f)) : 0.0f;
            animators.add(ObjectAnimator.ofFloat(actionBarMenuItem5, property5, fArr5));
            ActionBarMenuItem actionBarMenuItem6 = this.videoCallItem;
            Property property6 = View.TRANSLATION_Y;
            float[] fArr6 = new float[1];
            fArr6[0] = visible ? (float) (-AndroidUtilities.dp(10.0f)) : 0.0f;
            animators.add(ObjectAnimator.ofFloat(actionBarMenuItem6, property6, fArr6));
            ActionBarMenuItem actionBarMenuItem7 = this.otherItem;
            Property property7 = View.TRANSLATION_Y;
            float[] fArr7 = new float[1];
            fArr7[0] = visible ? (float) (-AndroidUtilities.dp(10.0f)) : 0.0f;
            animators.add(ObjectAnimator.ofFloat(actionBarMenuItem7, property7, fArr7));
            ActionBarMenuItem actionBarMenuItem8 = this.editItem;
            Property property8 = View.TRANSLATION_Y;
            float[] fArr8 = new float[1];
            fArr8[0] = visible ? (float) (-AndroidUtilities.dp(10.0f)) : 0.0f;
            animators.add(ObjectAnimator.ofFloat(actionBarMenuItem8, property8, fArr8));
            Property property9 = View.ALPHA;
            float[] fArr9 = new float[1];
            fArr9[0] = visible ? 1.0f : 0.0f;
            animators.add(ObjectAnimator.ofFloat(mediaSearchItem, property9, fArr9));
            Property property10 = View.TRANSLATION_Y;
            float[] fArr10 = new float[1];
            fArr10[0] = visible ? 0.0f : (float) AndroidUtilities.dp(10.0f);
            animators.add(ObjectAnimator.ofFloat(mediaSearchItem, property10, fArr10));
            ImageView imageView = this.sharedMediaLayout.photoVideoOptionsItem;
            Property property11 = View.ALPHA;
            float[] fArr11 = new float[1];
            fArr11[0] = visible ? 1.0f : 0.0f;
            animators.add(ObjectAnimator.ofFloat(imageView, property11, fArr11));
            ImageView imageView2 = this.sharedMediaLayout.photoVideoOptionsItem;
            Property property12 = View.TRANSLATION_Y;
            float[] fArr12 = new float[1];
            fArr12[0] = visible ? 0.0f : (float) AndroidUtilities.dp(10.0f);
            animators.add(ObjectAnimator.ofFloat(imageView2, property12, fArr12));
            ActionBar actionBar = this.actionBar;
            Property<ActionBar, Float> property13 = this.ACTIONBAR_HEADER_PROGRESS;
            float[] fArr13 = new float[1];
            fArr13[0] = visible ? 1.0f : 0.0f;
            animators.add(ObjectAnimator.ofFloat(actionBar, property13, fArr13));
            SimpleTextView simpleTextView = this.onlineTextView[1];
            Property property14 = View.ALPHA;
            float[] fArr14 = new float[1];
            fArr14[0] = visible ? 0.0f : 1.0f;
            animators.add(ObjectAnimator.ofFloat(simpleTextView, property14, fArr14));
            AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher = this.mediaCounterTextView;
            Property property15 = View.ALPHA;
            float[] fArr15 = new float[1];
            if (!visible) {
                f = 0.0f;
            }
            fArr15[0] = f;
            animators.add(ObjectAnimator.ofFloat(clippingTextViewSwitcher, property15, fArr15));
            if (visible) {
                animators.add(ObjectAnimator.ofFloat(this, this.HEADER_SHADOW, new float[]{0.0f}));
            }
            AnimatorSet animatorSet3 = new AnimatorSet();
            this.headerAnimatorSet = animatorSet3;
            animatorSet3.playTogether(animators);
            this.headerAnimatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.headerAnimatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
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
                                mediaSearchItem.setVisibility(0);
                            }
                            ProfileActivity.this.sharedMediaLayout.photoVideoOptionsItem.setVisibility(4);
                            AnimatorSet unused = ProfileActivity.this.headerShadowAnimatorSet = new AnimatorSet();
                            AnimatorSet access$14500 = ProfileActivity.this.headerShadowAnimatorSet;
                            ProfileActivity profileActivity = ProfileActivity.this;
                            access$14500.playTogether(new Animator[]{ObjectAnimator.ofFloat(profileActivity, profileActivity.HEADER_SHADOW, new float[]{1.0f})});
                            ProfileActivity.this.headerShadowAnimatorSet.setDuration(100);
                            ProfileActivity.this.headerShadowAnimatorSet.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animation) {
                                    AnimatorSet unused = ProfileActivity.this.headerShadowAnimatorSet = null;
                                }
                            });
                            ProfileActivity.this.headerShadowAnimatorSet.start();
                        }
                    }
                    AnimatorSet unused2 = ProfileActivity.this.headerAnimatorSet = null;
                }

                public void onAnimationCancel(Animator animation) {
                    AnimatorSet unused = ProfileActivity.this.headerAnimatorSet = null;
                }
            });
            this.headerAnimatorSet.setDuration(150);
            this.headerAnimatorSet.start();
        }
    }

    /* access modifiers changed from: private */
    public void openAddMember() {
        Bundle args = new Bundle();
        args.putBoolean("addToGroup", true);
        args.putLong("chatId", this.currentChat.id);
        GroupCreateActivity fragment = new GroupCreateActivity(args);
        fragment.setInfo(this.chatInfo);
        TLRPC.ChatFull chatFull = this.chatInfo;
        if (!(chatFull == null || chatFull.participants == null)) {
            LongSparseArray<TLObject> users = new LongSparseArray<>();
            for (int a = 0; a < this.chatInfo.participants.participants.size(); a++) {
                users.put(this.chatInfo.participants.participants.get(a).user_id, null);
            }
            fragment.setIgnoreUsers(users);
        }
        fragment.setDelegate((GroupCreateActivity.ContactsAddActivityDelegate) new ProfileActivity$$ExternalSyntheticLambda28(this));
        presentFragment(fragment);
    }

    /* renamed from: lambda$openAddMember$24$org-telegram-ui-ProfileActivity  reason: not valid java name */
    public /* synthetic */ void m3712lambda$openAddMember$24$orgtelegramuiProfileActivity(ArrayList users, int fwdCount) {
        HashSet<Long> currentParticipants = new HashSet<>();
        if (this.chatInfo.participants.participants != null) {
            for (int i = 0; i < this.chatInfo.participants.participants.size(); i++) {
                currentParticipants.add(Long.valueOf(this.chatInfo.participants.participants.get(i).user_id));
            }
        }
        int N = users.size();
        for (int a = 0; a < N; a++) {
            TLRPC.User user = (TLRPC.User) users.get(a);
            getMessagesController().addUserToChat(this.chatId, user, fwdCount, (String) null, this, (Runnable) null);
            if (!currentParticipants.contains(Long.valueOf(user.id))) {
                if (this.chatInfo.participants == null) {
                    this.chatInfo.participants = new TLRPC.TL_chatParticipants();
                }
                if (ChatObject.isChannel(this.currentChat)) {
                    TLRPC.TL_chatChannelParticipant channelParticipant1 = new TLRPC.TL_chatChannelParticipant();
                    channelParticipant1.channelParticipant = new TLRPC.TL_channelParticipant();
                    channelParticipant1.channelParticipant.inviter_id = getUserConfig().getClientUserId();
                    channelParticipant1.channelParticipant.peer = new TLRPC.TL_peerUser();
                    channelParticipant1.channelParticipant.peer.user_id = user.id;
                    channelParticipant1.channelParticipant.date = getConnectionsManager().getCurrentTime();
                    channelParticipant1.user_id = user.id;
                    this.chatInfo.participants.participants.add(channelParticipant1);
                } else {
                    TLRPC.ChatParticipant participant = new TLRPC.TL_chatParticipant();
                    participant.user_id = user.id;
                    participant.inviter_id = getAccountInstance().getUserConfig().clientUserId;
                    this.chatInfo.participants.participants.add(participant);
                }
                this.chatInfo.participants_count++;
                getMessagesController().putUser(user, false);
            }
        }
        updateListAnimated(true);
    }

    /* access modifiers changed from: private */
    public void checkListViewScroll() {
        boolean mediaHeaderVisible2;
        if (this.listView.getVisibility() == 0) {
            if (this.sharedMediaLayoutAttached) {
                this.sharedMediaLayout.setVisibleHeight(this.listView.getMeasuredHeight() - this.sharedMediaLayout.getTop());
            }
            if (this.listView.getChildCount() > 0 && !this.openAnimationInProgress) {
                int newOffset = 0;
                View child = null;
                int i = 0;
                while (true) {
                    if (i >= this.listView.getChildCount()) {
                        break;
                    }
                    RecyclerListView recyclerListView = this.listView;
                    if (recyclerListView.getChildAdapterPosition(recyclerListView.getChildAt(i)) == 0) {
                        child = this.listView.getChildAt(i);
                        break;
                    }
                    i++;
                }
                RecyclerListView.Holder holder = child == null ? null : (RecyclerListView.Holder) this.listView.findContainingViewHolder(child);
                boolean z = false;
                int top = child == null ? 0 : child.getTop();
                int adapterPosition = holder != null ? holder.getAdapterPosition() : -1;
                if (top >= 0 && adapterPosition == 0) {
                    newOffset = top;
                }
                boolean searchVisible = this.imageUpdater == null && this.actionBar.isSearchFieldVisible();
                int i2 = this.sharedMediaRow;
                if (i2 == -1 || searchVisible) {
                    mediaHeaderVisible2 = searchVisible;
                } else {
                    RecyclerListView.Holder holder2 = (RecyclerListView.Holder) this.listView.findViewHolderForAdapterPosition(i2);
                    mediaHeaderVisible2 = holder2 != null && holder2.itemView.getTop() <= 0;
                }
                setMediaHeaderVisible(mediaHeaderVisible2);
                if (this.extraHeight != ((float) newOffset)) {
                    this.extraHeight = (float) newOffset;
                    this.topView.invalidate();
                    if (this.playProfileAnimation != 0) {
                        if (this.extraHeight != 0.0f) {
                            z = true;
                        }
                        this.allowProfileAnimation = z;
                    }
                    needLayout(true);
                }
            }
        }
    }

    public void updateSelectedMediaTabText() {
        SharedMediaLayout sharedMediaLayout2 = this.sharedMediaLayout;
        if (sharedMediaLayout2 != null && this.mediaCounterTextView != null) {
            int id = sharedMediaLayout2.getClosestTab();
            int[] mediaCount = this.sharedMediaPreloader.getLastMediaCount();
            if (id == 0) {
                if (mediaCount[7] == 0 && mediaCount[6] == 0) {
                    this.mediaCounterTextView.setText(LocaleController.formatPluralString("Media", mediaCount[0]));
                } else if (this.sharedMediaLayout.getPhotosVideosTypeFilter() == 1 || mediaCount[7] == 0) {
                    this.mediaCounterTextView.setText(LocaleController.formatPluralString("Photos", mediaCount[6]));
                } else if (this.sharedMediaLayout.getPhotosVideosTypeFilter() == 2 || mediaCount[6] == 0) {
                    this.mediaCounterTextView.setText(LocaleController.formatPluralString("Videos", mediaCount[7]));
                } else {
                    this.mediaCounterTextView.setText(String.format("%s, %s", new Object[]{LocaleController.formatPluralString("Photos", mediaCount[6]), LocaleController.formatPluralString("Videos", mediaCount[7])}));
                }
            } else if (id == 1) {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("Files", mediaCount[1]));
            } else if (id == 2) {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("Voice", mediaCount[2]));
            } else if (id == 3) {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("Links", mediaCount[3]));
            } else if (id == 4) {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("MusicFiles", mediaCount[4]));
            } else if (id == 5) {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("GIFs", mediaCount[5]));
            } else if (id == 6) {
                this.mediaCounterTextView.setText(LocaleController.formatPluralString("CommonGroups", this.userInfo.common_chats_count));
            } else if (id == 7) {
                this.mediaCounterTextView.setText(this.onlineTextView[1].getText());
            }
        }
    }

    /* access modifiers changed from: private */
    public void needLayout(boolean animated) {
        ValueAnimator valueAnimator;
        BackupImageView imageView;
        TLRPC.ChatFull chatFull;
        int newTop = (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null && !this.openAnimationInProgress) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) recyclerListView.getLayoutParams();
            if (layoutParams.topMargin != newTop) {
                layoutParams.topMargin = newTop;
                this.listView.setLayoutParams(layoutParams);
            }
        }
        if (this.avatarContainer != null) {
            float diff = Math.min(1.0f, this.extraHeight / ((float) AndroidUtilities.dp(88.0f)));
            this.listView.setTopGlowOffset((int) this.extraHeight);
            this.listView.setOverScrollMode((this.extraHeight <= ((float) AndroidUtilities.dp(88.0f)) || this.extraHeight >= ((float) (this.listView.getMeasuredWidth() - newTop))) ? 0 : 2);
            RLottieImageView rLottieImageView = this.writeButton;
            if (rLottieImageView != null) {
                rLottieImageView.setTranslationY(((((float) ((this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight())) + this.extraHeight) + ((float) this.searchTransitionOffset)) - ((float) AndroidUtilities.dp(29.5f)));
                if (!this.openAnimationInProgress) {
                    float f = 0.2f;
                    boolean setVisible = diff > 0.2f && !this.searchMode && (this.imageUpdater == null || this.setAvatarRow == -1);
                    if (setVisible && this.chatId != 0) {
                        setVisible = (!ChatObject.isChannel(this.currentChat) || this.currentChat.megagroup || (chatFull = this.chatInfo) == null || chatFull.linked_chat_id == 0 || this.infoHeaderRow == -1) ? false : true;
                    }
                    if (setVisible != (this.writeButton.getTag() == null)) {
                        if (setVisible) {
                            this.writeButton.setTag((Object) null);
                        } else {
                            this.writeButton.setTag(0);
                        }
                        if (this.writeButtonAnimation != null) {
                            AnimatorSet old = this.writeButtonAnimation;
                            this.writeButtonAnimation = null;
                            old.cancel();
                        }
                        if (animated) {
                            AnimatorSet animatorSet = new AnimatorSet();
                            this.writeButtonAnimation = animatorSet;
                            if (setVisible) {
                                animatorSet.setInterpolator(new DecelerateInterpolator());
                                this.writeButtonAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.writeButton, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.writeButton, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.writeButton, View.ALPHA, new float[]{1.0f})});
                            } else {
                                animatorSet.setInterpolator(new AccelerateInterpolator());
                                this.writeButtonAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.writeButton, View.SCALE_X, new float[]{0.2f}), ObjectAnimator.ofFloat(this.writeButton, View.SCALE_Y, new float[]{0.2f}), ObjectAnimator.ofFloat(this.writeButton, View.ALPHA, new float[]{0.0f})});
                            }
                            this.writeButtonAnimation.setDuration(150);
                            this.writeButtonAnimation.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animation) {
                                    if (ProfileActivity.this.writeButtonAnimation != null && ProfileActivity.this.writeButtonAnimation.equals(animation)) {
                                        AnimatorSet unused = ProfileActivity.this.writeButtonAnimation = null;
                                    }
                                }
                            });
                            this.writeButtonAnimation.start();
                        } else {
                            this.writeButton.setScaleX(setVisible ? 1.0f : 0.2f);
                            RLottieImageView rLottieImageView2 = this.writeButton;
                            if (setVisible) {
                                f = 1.0f;
                            }
                            rLottieImageView2.setScaleY(f);
                            this.writeButton.setAlpha(setVisible ? 1.0f : 0.0f);
                        }
                    }
                }
            }
            this.avatarX = (-AndroidUtilities.dpf2(47.0f)) * diff;
            this.avatarY = ((((float) (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0)) + ((((float) ActionBar.getCurrentActionBarHeight()) / 2.0f) * (diff + 1.0f))) - (AndroidUtilities.density * 21.0f)) + (AndroidUtilities.density * 27.0f * diff) + this.actionBar.getTranslationY();
            float h = this.openAnimationInProgress ? this.initialAnimationExtraHeight : this.extraHeight;
            if (h > ((float) AndroidUtilities.dp(88.0f)) || this.isPulledDown) {
                float max = Math.max(0.0f, Math.min(1.0f, (h - ((float) AndroidUtilities.dp(88.0f))) / ((float) ((this.listView.getMeasuredWidth() - newTop) - AndroidUtilities.dp(88.0f)))));
                this.expandProgress = max;
                this.avatarScale = AndroidUtilities.lerp(1.4285715f, 2.4285715f, Math.min(1.0f, max * 3.0f));
                float durationFactor = Math.min(AndroidUtilities.dpf2(2000.0f), Math.max(AndroidUtilities.dpf2(1100.0f), Math.abs(this.listViewVelocityY))) / AndroidUtilities.dpf2(1100.0f);
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
                        this.overlaysView.setOverlaysVisible(false, durationFactor);
                        this.avatarsViewPagerIndicatorView.refreshVisibility(durationFactor);
                        this.expandAnimator.cancel();
                        this.avatarImage.getImageReceiver().setAllowStartAnimation(true);
                        this.avatarImage.getImageReceiver().startAnimation();
                        float value = AndroidUtilities.lerp(this.expandAnimatorValues, this.currentExpanAnimatorFracture);
                        float[] fArr = this.expandAnimatorValues;
                        fArr[0] = value;
                        fArr[1] = 0.0f;
                        if (!this.isInLandscapeMode) {
                            this.expandAnimator.setDuration((long) ((250.0f * value) / durationFactor));
                        } else {
                            this.expandAnimator.setDuration(0);
                        }
                        this.topView.setBackgroundColor(Theme.getColor("avatar_backgroundActionBarBlue"));
                        if (!this.doNotSetForeground && (imageView = this.avatarsViewPager.getCurrentItemView()) != null) {
                            this.avatarImage.setForegroundImageDrawable(imageView.getImageReceiver().getDrawableSafe());
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
                        this.overlaysView.setOverlaysVisible(true, durationFactor);
                        this.avatarsViewPagerIndicatorView.refreshVisibility(durationFactor);
                        this.avatarsViewPager.setCreateThumbFromParent(true);
                        this.avatarsViewPager.getAdapter().notifyDataSetChanged();
                        this.expandAnimator.cancel();
                        float value2 = AndroidUtilities.lerp(this.expandAnimatorValues, this.currentExpanAnimatorFracture);
                        float[] fArr2 = this.expandAnimatorValues;
                        fArr2[0] = value2;
                        fArr2[1] = 1.0f;
                        this.expandAnimator.setDuration((long) (((1.0f - value2) * 250.0f) / durationFactor));
                        this.expandAnimator.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationStart(Animator animation) {
                                ProfileActivity.this.setForegroundImage(false);
                                ProfileActivity.this.avatarsViewPager.setAnimatedFileMaybe(ProfileActivity.this.avatarImage.getImageReceiver().getAnimation());
                                ProfileActivity.this.avatarsViewPager.resetCurrentItem();
                            }

                            public void onAnimationEnd(Animator animation) {
                                ProfileActivity.this.expandAnimator.removeListener(this);
                                ProfileActivity.this.topView.setBackgroundColor(-16777216);
                                ProfileActivity.this.avatarContainer.setVisibility(8);
                                ProfileActivity.this.avatarsViewPager.setVisibility(0);
                            }
                        });
                        this.expandAnimator.start();
                    }
                    ViewGroup.LayoutParams params = this.avatarsViewPager.getLayoutParams();
                    params.width = this.listView.getMeasuredWidth();
                    params.height = (int) (((float) newTop) + h);
                    this.avatarsViewPager.requestLayout();
                    if (!this.expandAnimator.isRunning()) {
                        float additionalTranslationY = 0.0f;
                        if (this.openAnimationInProgress && this.playProfileAnimation == 2) {
                            additionalTranslationY = (-(1.0f - this.animationProgress)) * ((float) AndroidUtilities.dp(50.0f));
                        }
                        this.nameTextView[1].setTranslationX(AndroidUtilities.dpf2(16.0f) - ((float) this.nameTextView[1].getLeft()));
                        this.nameTextView[1].setTranslationY((((((float) newTop) + h) - AndroidUtilities.dpf2(38.0f)) - ((float) this.nameTextView[1].getBottom())) + additionalTranslationY);
                        this.onlineTextView[1].setTranslationX(AndroidUtilities.dpf2(16.0f) - ((float) this.onlineTextView[1].getLeft()));
                        this.onlineTextView[1].setTranslationY((((((float) newTop) + h) - AndroidUtilities.dpf2(18.0f)) - ((float) this.onlineTextView[1].getBottom())) + additionalTranslationY);
                        this.mediaCounterTextView.setTranslationX(this.onlineTextView[1].getTranslationX());
                        this.mediaCounterTextView.setTranslationY(this.onlineTextView[1].getTranslationY());
                    }
                }
            }
            if (this.openAnimationInProgress && this.playProfileAnimation == 2) {
                float avY = ((((float) (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0)) + (((float) ActionBar.getCurrentActionBarHeight()) / 2.0f)) - (AndroidUtilities.density * 21.0f)) + this.actionBar.getTranslationY();
                this.nameTextView[0].setTranslationX(0.0f);
                this.nameTextView[0].setTranslationY(((float) Math.floor((double) avY)) + ((float) AndroidUtilities.dp(1.3f)));
                this.onlineTextView[0].setTranslationX(0.0f);
                this.onlineTextView[0].setTranslationY(((float) Math.floor((double) avY)) + ((float) AndroidUtilities.dp(24.0f)));
                this.nameTextView[0].setScaleX(1.0f);
                this.nameTextView[0].setScaleY(1.0f);
                SimpleTextView[] simpleTextViewArr = this.nameTextView;
                simpleTextViewArr[1].setPivotY((float) simpleTextViewArr[1].getMeasuredHeight());
                this.nameTextView[1].setScaleX(1.67f);
                this.nameTextView[1].setScaleY(1.67f);
                this.avatarScale = AndroidUtilities.lerp(1.0f, 2.4285715f, this.animationProgress);
                this.avatarImage.setRoundRadius((int) AndroidUtilities.lerp(AndroidUtilities.dpf2(21.0f), 0.0f, this.animationProgress));
                this.avatarContainer.setTranslationX(AndroidUtilities.lerp(0.0f, 0.0f, this.animationProgress));
                this.avatarContainer.setTranslationY(AndroidUtilities.lerp((float) Math.ceil((double) avY), 0.0f, this.animationProgress));
                float extra = ((float) (this.avatarContainer.getMeasuredWidth() - AndroidUtilities.dp(42.0f))) * this.avatarScale;
                this.timeItem.setTranslationX(this.avatarContainer.getX() + ((float) AndroidUtilities.dp(16.0f)) + extra);
                this.timeItem.setTranslationY(this.avatarContainer.getY() + ((float) AndroidUtilities.dp(15.0f)) + extra);
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
                FrameLayout.LayoutParams params2 = (FrameLayout.LayoutParams) this.avatarContainer.getLayoutParams();
                int lerp = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(42.0f), (this.extraHeight + ((float) newTop)) / this.avatarScale, this.animationProgress);
                params2.height = lerp;
                params2.width = lerp;
                params2.leftMargin = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(64.0f), 0.0f, this.animationProgress);
                this.avatarContainer.requestLayout();
            } else if (this.extraHeight <= ((float) AndroidUtilities.dp(88.0f))) {
                this.avatarScale = ((diff * 18.0f) + 42.0f) / 42.0f;
                float nameScale = (0.12f * diff) + 1.0f;
                ValueAnimator valueAnimator3 = this.expandAnimator;
                if (valueAnimator3 == null || !valueAnimator3.isRunning()) {
                    this.avatarContainer.setScaleX(this.avatarScale);
                    this.avatarContainer.setScaleY(this.avatarScale);
                    this.avatarContainer.setTranslationX(this.avatarX);
                    this.avatarContainer.setTranslationY((float) Math.ceil((double) this.avatarY));
                    float extra2 = (((float) AndroidUtilities.dp(42.0f)) * this.avatarScale) - ((float) AndroidUtilities.dp(42.0f));
                    this.timeItem.setTranslationX(this.avatarContainer.getX() + ((float) AndroidUtilities.dp(16.0f)) + extra2);
                    this.timeItem.setTranslationY(this.avatarContainer.getY() + ((float) AndroidUtilities.dp(15.0f)) + extra2);
                }
                this.nameX = AndroidUtilities.density * -21.0f * diff;
                this.nameY = ((float) Math.floor((double) this.avatarY)) + ((float) AndroidUtilities.dp(1.3f)) + (((float) AndroidUtilities.dp(7.0f)) * diff);
                this.onlineX = AndroidUtilities.density * -21.0f * diff;
                this.onlineY = ((float) Math.floor((double) this.avatarY)) + ((float) AndroidUtilities.dp(24.0f)) + (((float) Math.floor((double) (AndroidUtilities.density * 11.0f))) * diff);
                int a = 0;
                while (true) {
                    SimpleTextView[] simpleTextViewArr2 = this.nameTextView;
                    if (a >= simpleTextViewArr2.length) {
                        break;
                    }
                    if (simpleTextViewArr2[a] != null) {
                        ValueAnimator valueAnimator4 = this.expandAnimator;
                        if (valueAnimator4 == null || !valueAnimator4.isRunning()) {
                            this.nameTextView[a].setTranslationX(this.nameX);
                            this.nameTextView[a].setTranslationY(this.nameY);
                            this.onlineTextView[a].setTranslationX(this.onlineX);
                            this.onlineTextView[a].setTranslationY(this.onlineY);
                            if (a == 1) {
                                this.mediaCounterTextView.setTranslationX(this.onlineX);
                                this.mediaCounterTextView.setTranslationY(this.onlineY);
                            }
                        }
                        this.nameTextView[a].setScaleX(nameScale);
                        this.nameTextView[a].setScaleY(nameScale);
                    }
                    a++;
                }
            }
            if (!this.openAnimationInProgress && ((valueAnimator = this.expandAnimator) == null || !valueAnimator.isRunning())) {
                needLayoutText(diff);
            }
        }
        if (this.isPulledDown || (this.overlaysView.animator != null && this.overlaysView.animator.isRunning())) {
            ViewGroup.LayoutParams overlaysLp = this.overlaysView.getLayoutParams();
            overlaysLp.width = this.listView.getMeasuredWidth();
            overlaysLp.height = (int) (this.extraHeight + ((float) newTop));
            this.overlaysView.requestLayout();
        }
    }

    /* access modifiers changed from: private */
    public void setForegroundImage(boolean secondParent) {
        String filter;
        Drawable drawable = this.avatarImage.getImageReceiver().getDrawable();
        if (drawable instanceof AnimatedFileDrawable) {
            AnimatedFileDrawable fileDrawable = (AnimatedFileDrawable) drawable;
            this.avatarImage.setForegroundImage((ImageLocation) null, (String) null, fileDrawable);
            if (secondParent) {
                fileDrawable.addSecondParentView(this.avatarImage);
                return;
            }
            return;
        }
        ImageLocation location = this.avatarsViewPager.getImageLocation(0);
        if (location == null || location.imageType != 2) {
            filter = null;
        } else {
            filter = "g";
        }
        this.avatarImage.setForegroundImage(location, filter, drawable);
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
    public void needLayoutText(float diff) {
        int width;
        float scale = this.nameTextView[1].getScaleX();
        float maxScale = this.extraHeight > ((float) AndroidUtilities.dp(88.0f)) ? 1.67f : 1.12f;
        if (this.extraHeight <= ((float) AndroidUtilities.dp(88.0f)) || scale == maxScale) {
            int viewWidth = AndroidUtilities.isTablet() ? AndroidUtilities.dp(490.0f) : AndroidUtilities.displaySize.x;
            ActionBarMenuItem access$14200 = this.avatarsViewPagerIndicatorView.getSecondaryMenuItem();
            int extra = 0;
            if (this.editItemVisible) {
                extra = 0 + 48;
            }
            if (this.callItemVisible) {
                extra += 48;
            }
            if (this.videoCallItemVisible) {
                extra += 48;
            }
            if (this.searchItem != null) {
                extra += 48;
            }
            int buttonsWidth = AndroidUtilities.dp((((float) extra) * (1.0f - this.mediaHeaderAnimationProgress)) + 40.0f + 126.0f);
            int minWidth = viewWidth - buttonsWidth;
            int width2 = (int) ((((float) viewWidth) - (((float) buttonsWidth) * Math.max(0.0f, 1.0f - (diff != 1.0f ? (0.15f * diff) / (1.0f - diff) : 1.0f)))) - this.nameTextView[1].getTranslationX());
            float width22 = (this.nameTextView[1].getPaint().measureText(this.nameTextView[1].getText().toString()) * scale) + ((float) this.nameTextView[1].getSideDrawablesSize());
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.nameTextView[1].getLayoutParams();
            int prevWidth = layoutParams.width;
            if (((float) width2) < width22) {
                width = width2;
                layoutParams.width = Math.max(minWidth, (int) Math.ceil((double) (((float) (width2 - AndroidUtilities.dp(24.0f))) / (scale + ((maxScale - scale) * 7.0f)))));
            } else {
                width = width2;
                layoutParams.width = (int) Math.ceil((double) width22);
            }
            layoutParams.width = (int) Math.min(((((float) viewWidth) - this.nameTextView[1].getX()) / scale) - ((float) AndroidUtilities.dp(8.0f)), (float) layoutParams.width);
            if (layoutParams.width != prevWidth) {
                this.nameTextView[1].requestLayout();
            }
            float width23 = this.onlineTextView[1].getPaint().measureText(this.onlineTextView[1].getText().toString());
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.onlineTextView[1].getLayoutParams();
            FrameLayout.LayoutParams layoutParams22 = (FrameLayout.LayoutParams) this.mediaCounterTextView.getLayoutParams();
            int prevWidth2 = layoutParams2.width;
            float f = maxScale;
            int ceil = (int) Math.ceil((double) (this.onlineTextView[1].getTranslationX() + ((float) AndroidUtilities.dp(8.0f)) + (((float) AndroidUtilities.dp(40.0f)) * (1.0f - diff))));
            layoutParams2.rightMargin = ceil;
            layoutParams22.rightMargin = ceil;
            int width3 = width;
            if (((float) width3) < width23) {
                float f2 = width23;
                int ceil2 = (int) Math.ceil((double) width3);
                layoutParams2.width = ceil2;
                layoutParams22.width = ceil2;
            } else {
                layoutParams2.width = -2;
                layoutParams22.width = -2;
            }
            if (prevWidth2 != layoutParams2.width) {
                this.onlineTextView[1].requestLayout();
                this.mediaCounterTextView.requestLayout();
            }
        }
    }

    private void fixLayout() {
        if (this.fragmentView != null) {
            this.fragmentView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
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

    public void onConfigurationChanged(Configuration newConfig) {
        View view;
        super.onConfigurationChanged(newConfig);
        SharedMediaLayout sharedMediaLayout2 = this.sharedMediaLayout;
        if (sharedMediaLayout2 != null) {
            sharedMediaLayout2.onConfigurationChanged(newConfig);
        }
        invalidateIsInLandscapeMode();
        if (this.isInLandscapeMode && this.isPulledDown && (view = this.layoutManager.findViewByPosition(0)) != null) {
            this.listView.scrollBy(0, view.getTop() - AndroidUtilities.dp(88.0f));
        }
        fixLayout();
    }

    private void invalidateIsInLandscapeMode() {
        Point size = new Point();
        getParentActivity().getWindowManager().getDefaultDisplay().getSize(size);
        this.isInLandscapeMode = size.x > size.y;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        TLRPC.Chat chat;
        RecyclerListView recyclerListView;
        RecyclerListView recyclerListView2;
        RecyclerListView.Holder holder;
        boolean infoChanged = false;
        if (id == NotificationCenter.updateInterfaces) {
            int mask = args[0].intValue();
            if (!((MessagesController.UPDATE_MASK_AVATAR & mask) == 0 && (MessagesController.UPDATE_MASK_NAME & mask) == 0 && (MessagesController.UPDATE_MASK_STATUS & mask) == 0)) {
                infoChanged = true;
            }
            if (this.userId != 0) {
                if (infoChanged) {
                    updateProfileData();
                }
                if ((MessagesController.UPDATE_MASK_PHONE & mask) != 0 && (recyclerListView2 = this.listView) != null && (holder = (RecyclerListView.Holder) recyclerListView2.findViewHolderForPosition(this.phoneRow)) != null) {
                    this.listAdapter.onBindViewHolder(holder, this.phoneRow);
                }
            } else if (this.chatId != 0) {
                if (!((MessagesController.UPDATE_MASK_CHAT & mask) == 0 && (MessagesController.UPDATE_MASK_CHAT_AVATAR & mask) == 0 && (MessagesController.UPDATE_MASK_CHAT_NAME & mask) == 0 && (MessagesController.UPDATE_MASK_CHAT_MEMBERS & mask) == 0 && (MessagesController.UPDATE_MASK_STATUS & mask) == 0)) {
                    if ((MessagesController.UPDATE_MASK_CHAT & mask) != 0) {
                        updateListAnimated(true);
                    } else {
                        updateOnlineCount(true);
                    }
                    updateProfileData();
                }
                if (infoChanged && (recyclerListView = this.listView) != null) {
                    int count = recyclerListView.getChildCount();
                    for (int a = 0; a < count; a++) {
                        View child = this.listView.getChildAt(a);
                        if (child instanceof UserCell) {
                            ((UserCell) child).update(mask);
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.chatOnlineCountDidLoad) {
            Long chatId2 = args[0];
            if (this.chatInfo != null && (chat = this.currentChat) != null && chat.id == chatId2.longValue()) {
                this.chatInfo.online_count = args[1].intValue();
                updateOnlineCount(true);
                updateProfileData();
            }
        } else if (id == NotificationCenter.contactsDidLoad) {
            createActionBarMenu(true);
        } else if (id == NotificationCenter.encryptedChatCreated) {
            if (this.creatingChat) {
                AndroidUtilities.runOnUIThread(new ProfileActivity$$ExternalSyntheticLambda16(this, args));
            }
        } else if (id == NotificationCenter.encryptedChatUpdated) {
            TLRPC.EncryptedChat chat2 = args[0];
            if (this.currentEncryptedChat != null && chat2.id == this.currentEncryptedChat.id) {
                this.currentEncryptedChat = chat2;
                updateListAnimated(false);
                updateTimeItem();
            }
        } else if (id == NotificationCenter.blockedUsersDidLoad) {
            boolean oldValue = this.userBlocked;
            boolean z = getMessagesController().blockePeers.indexOfKey(this.userId) >= 0;
            this.userBlocked = z;
            if (oldValue != z) {
                createActionBarMenu(true);
                updateListAnimated(false);
            }
        } else if (id == NotificationCenter.groupCallUpdated) {
            Long chatId3 = args[0];
            if (this.currentChat != null && chatId3.longValue() == this.currentChat.id && ChatObject.canManageCalls(this.currentChat)) {
                TLRPC.ChatFull chatFull = MessagesController.getInstance(this.currentAccount).getChatFull(chatId3.longValue());
                if (chatFull != null) {
                    TLRPC.ChatFull chatFull2 = this.chatInfo;
                    if (chatFull2 != null) {
                        chatFull.participants = chatFull2.participants;
                    }
                    this.chatInfo = chatFull;
                }
                TLRPC.ChatFull chatFull3 = this.chatInfo;
                if (chatFull3 == null) {
                    return;
                }
                if ((chatFull3.call == null && !this.hasVoiceChatItem) || (this.chatInfo.call != null && this.hasVoiceChatItem)) {
                    createActionBarMenu(false);
                }
            }
        } else if (id == NotificationCenter.chatInfoDidLoad) {
            TLRPC.ChatFull chatFull4 = args[0];
            if (chatFull4.id == this.chatId) {
                boolean byChannelUsers = args[2].booleanValue();
                if ((this.chatInfo instanceof TLRPC.TL_channelFull) && chatFull4.participants == null) {
                    chatFull4.participants = this.chatInfo.participants;
                }
                if (this.chatInfo == null && (chatFull4 instanceof TLRPC.TL_channelFull)) {
                    infoChanged = true;
                }
                this.chatInfo = chatFull4;
                if (this.mergeDialogId == 0 && chatFull4.migrated_from_chat_id != 0) {
                    this.mergeDialogId = -this.chatInfo.migrated_from_chat_id;
                    getMediaDataController().getMediaCount(this.mergeDialogId, 0, this.classGuid, true);
                }
                fetchUsersFromChannelInfo();
                ProfileGalleryView profileGalleryView = this.avatarsViewPager;
                if (profileGalleryView != null) {
                    profileGalleryView.setChatInfo(this.chatInfo);
                }
                updateListAnimated(true);
                TLRPC.Chat newChat = getMessagesController().getChat(Long.valueOf(this.chatId));
                if (newChat != null) {
                    this.currentChat = newChat;
                    createActionBarMenu(true);
                }
                if (this.currentChat.megagroup && (infoChanged || !byChannelUsers)) {
                    getChannelParticipants(true);
                }
                updateTimeItem();
            }
        } else if (id == NotificationCenter.closeChats) {
            removeSelfFromStack();
        } else if (id == NotificationCenter.botInfoDidLoad) {
            TLRPC.BotInfo info = args[0];
            if (info.user_id == this.userId) {
                this.botInfo = info;
                updateListAnimated(false);
            }
        } else if (id == NotificationCenter.userInfoDidLoad) {
            if (args[0].longValue() == this.userId) {
                TLRPC.UserFull userFull = args[1];
                this.userInfo = userFull;
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
                } else if (!TextUtils.equals(userFull.about, this.currentBio)) {
                    this.listAdapter.notifyItemChanged(this.bioRow);
                }
                updateTimeItem();
            }
        } else if (id == NotificationCenter.didReceiveNewMessages) {
            if (!args[2].booleanValue()) {
                long did = getDialogId();
                if (did == args[0].longValue()) {
                    boolean isEncryptedDialog = DialogObject.isEncryptedDialog(did);
                    ArrayList<MessageObject> arr = args[1];
                    for (int a2 = 0; a2 < arr.size(); a2++) {
                        MessageObject obj = arr.get(a2);
                        if (this.currentEncryptedChat != null && (obj.messageOwner.action instanceof TLRPC.TL_messageEncryptedAction) && (obj.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL)) {
                            TLRPC.TL_decryptedMessageActionSetMessageTTL tL_decryptedMessageActionSetMessageTTL = (TLRPC.TL_decryptedMessageActionSetMessageTTL) obj.messageOwner.action.encryptedAction;
                            ListAdapter listAdapter2 = this.listAdapter;
                            if (listAdapter2 != null) {
                                listAdapter2.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.emojiLoaded) {
            RecyclerListView recyclerListView3 = this.listView;
            if (recyclerListView3 != null) {
                recyclerListView3.invalidateViews();
            }
        } else if (id == NotificationCenter.reloadInterface) {
            int i = this.emptyRow;
            updateListAnimated(false);
        } else if (id == NotificationCenter.newSuggestionsAvailable) {
            int prevRow1 = this.passwordSuggestionRow;
            int prevRow2 = this.phoneSuggestionRow;
            updateRowsIds();
            if (prevRow1 != this.passwordSuggestionRow || prevRow2 != this.phoneSuggestionRow) {
                this.listAdapter.notifyDataSetChanged();
            }
        }
    }

    /* renamed from: lambda$didReceivedNotification$25$org-telegram-ui-ProfileActivity  reason: not valid java name */
    public /* synthetic */ void m3698xd43936b7(Object[] args) {
        getNotificationCenter().removeObserver(this, NotificationCenter.closeChats);
        getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
        Bundle args2 = new Bundle();
        args2.putInt("enc_id", args[0].id);
        presentFragment(new ChatActivity(args2), true);
    }

    private void updateTimeItem() {
        TimerDrawable timerDrawable2 = this.timerDrawable;
        if (timerDrawable2 != null) {
            TLRPC.EncryptedChat encryptedChat = this.currentEncryptedChat;
            if (encryptedChat != null) {
                timerDrawable2.setTime(encryptedChat.ttl);
                this.timeItem.setTag(1);
                this.timeItem.setVisibility(0);
                return;
            }
            TLRPC.UserFull userFull = this.userInfo;
            if (userFull != null) {
                timerDrawable2.setTime(userFull.ttl_period);
                if (!this.needTimerImage || this.userInfo.ttl_period == 0) {
                    this.timeItem.setTag((Object) null);
                    this.timeItem.setVisibility(8);
                    return;
                }
                this.timeItem.setTag(1);
                this.timeItem.setVisibility(0);
                return;
            }
            TLRPC.ChatFull chatFull = this.chatInfo;
            if (chatFull != null) {
                timerDrawable2.setTime(chatFull.ttl_period);
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
        if (this.playProfileAnimation == 0) {
            return true;
        }
        return false;
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

    public boolean isSwipeBackEnabled(MotionEvent event) {
        SharedMediaLayout sharedMediaLayout2;
        ProfileGalleryView profileGalleryView = this.avatarsViewPager;
        if (profileGalleryView != null && profileGalleryView.getVisibility() == 0 && this.avatarsViewPager.getRealCount() > 1) {
            this.avatarsViewPager.getHitRect(this.rect);
            if (this.rect.contains((int) event.getX(), ((int) event.getY()) - this.actionBar.getMeasuredHeight())) {
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
        if (!this.rect.contains((int) event.getX(), ((int) event.getY()) - this.actionBar.getMeasuredHeight())) {
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

    public void setPlayProfileAnimation(int type) {
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        if (!AndroidUtilities.isTablet()) {
            this.needTimerImage = type != 0;
            if (preferences.getBoolean("view_animations", true)) {
                this.playProfileAnimation = type;
            } else if (type == 2) {
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
    public void onTransitionAnimationStart(boolean isOpen, boolean backward) {
        this.isFragmentOpened = isOpen;
        if (((!isOpen && backward) || (isOpen && !backward)) && this.playProfileAnimation != 0 && this.allowProfileAnimation && !this.isPulledDown) {
            this.openAnimationInProgress = true;
        }
        if (isOpen) {
            if (this.imageUpdater != null) {
                this.transitionIndex = getNotificationCenter().setAnimationInProgress(this.transitionIndex, new int[]{NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.mediaCountDidLoad, NotificationCenter.mediaCountsDidLoad, NotificationCenter.userInfoDidLoad});
            } else {
                this.transitionIndex = getNotificationCenter().setAnimationInProgress(this.transitionIndex, new int[]{NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.mediaCountDidLoad, NotificationCenter.mediaCountsDidLoad});
            }
        }
        this.transitionAnimationInProress = true;
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            if (!backward) {
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

    public float getAnimationProgress() {
        return this.animationProgress;
    }

    public void setAnimationProgress(float progress) {
        int color;
        int color2;
        int r;
        int subtitleColor;
        int color3;
        int titleColor;
        int r2;
        int iconColor;
        int color4;
        float f = progress;
        this.animationProgress = f;
        this.listView.setAlpha(f);
        this.listView.setTranslationX(((float) AndroidUtilities.dp(48.0f)) - (((float) AndroidUtilities.dp(48.0f)) * f));
        if (this.playProfileAnimation != 2 || this.avatarColor == 0) {
            color = AvatarDrawable.getProfileBackColorForId((this.userId != 0 || (ChatObject.isChannel(this.chatId, this.currentAccount) && !this.currentChat.megagroup)) ? 5 : this.chatId);
        } else {
            color = this.avatarColor;
        }
        int actionBarColor = this.actionBarAnimationColorFrom;
        if (actionBarColor == 0) {
            actionBarColor = Theme.getColor("actionBarDefault");
        }
        int r3 = Color.red(actionBarColor);
        int g = Color.green(actionBarColor);
        int b = Color.blue(actionBarColor);
        this.topView.setBackgroundColor(Color.rgb(r3 + ((int) (((float) (Color.red(color) - r3)) * f)), g + ((int) (((float) (Color.green(color) - g)) * f)), b + ((int) (((float) (Color.blue(color) - b)) * f))));
        int color5 = AvatarDrawable.getIconColorForId((this.userId != 0 || (ChatObject.isChannel(this.chatId, this.currentAccount) && !this.currentChat.megagroup)) ? 5 : this.chatId);
        int iconColor2 = Theme.getColor("actionBarDefaultIcon");
        int r4 = Color.red(iconColor2);
        int g2 = Color.green(iconColor2);
        int b2 = Color.blue(iconColor2);
        this.actionBar.setItemsColor(Color.rgb(r4 + ((int) (((float) (Color.red(color5) - r4)) * f)), g2 + ((int) (((float) (Color.green(color5) - g2)) * f)), b2 + ((int) (((float) (Color.blue(color5) - b2)) * f))), false);
        int color6 = Theme.getColor("profile_title");
        int titleColor2 = Theme.getColor("actionBarDefaultTitle");
        int r5 = Color.red(titleColor2);
        int g3 = Color.green(titleColor2);
        int b3 = Color.blue(titleColor2);
        int a = Color.alpha(titleColor2);
        int rD = (int) (((float) (Color.red(color6) - r5)) * f);
        int gD = (int) (((float) (Color.green(color6) - g3)) * f);
        int bD = (int) (((float) (Color.blue(color6) - b3)) * f);
        int aD = (int) (((float) (Color.alpha(color6) - a)) * f);
        int i = 0;
        while (i < 2) {
            SimpleTextView[] simpleTextViewArr = this.nameTextView;
            if (simpleTextViewArr[i] != null) {
                color4 = color6;
                if (i == 1) {
                    iconColor = iconColor2;
                    if (this.playProfileAnimation == 2) {
                        r2 = r5;
                        titleColor = titleColor2;
                    }
                } else {
                    iconColor = iconColor2;
                }
                r2 = r5;
                titleColor = titleColor2;
                simpleTextViewArr[i].setTextColor(Color.argb(a + aD, r5 + rD, g3 + gD, b3 + bD));
            } else {
                color4 = color6;
                iconColor = iconColor2;
                r2 = r5;
                titleColor = titleColor2;
            }
            i++;
            color6 = color4;
            iconColor2 = iconColor;
            r5 = r2;
            titleColor2 = titleColor;
        }
        int i2 = iconColor2;
        int i3 = r5;
        int i4 = titleColor2;
        if (this.isOnline[0]) {
            color2 = Theme.getColor("profile_status");
        } else {
            color2 = AvatarDrawable.getProfileTextColorForId((this.userId != 0 || (ChatObject.isChannel(this.chatId, this.currentAccount) && !this.currentChat.megagroup)) ? 5 : this.chatId);
        }
        int subtitleColor2 = Theme.getColor(this.isOnline[0] ? "chat_status" : "actionBarDefaultSubtitle");
        int r6 = Color.red(subtitleColor2);
        int g4 = Color.green(subtitleColor2);
        int b4 = Color.blue(subtitleColor2);
        int a2 = Color.alpha(subtitleColor2);
        int rD2 = (int) (((float) (Color.red(color2) - r6)) * f);
        int gD2 = (int) (((float) (Color.green(color2) - g4)) * f);
        int bD2 = (int) (((float) (Color.blue(color2) - b4)) * f);
        int aD2 = (int) (((float) (Color.alpha(color2) - a2)) * f);
        int i5 = 0;
        while (i5 < 2) {
            SimpleTextView[] simpleTextViewArr2 = this.onlineTextView;
            if (simpleTextViewArr2[i5] != null) {
                if (i5 == 1) {
                    color3 = color2;
                    if (this.playProfileAnimation == 2) {
                        subtitleColor = subtitleColor2;
                        r = r6;
                    }
                } else {
                    color3 = color2;
                }
                subtitleColor = subtitleColor2;
                r = r6;
                simpleTextViewArr2[i5].setTextColor(Color.argb(a2 + aD2, r6 + rD2, g4 + gD2, b4 + bD2));
            } else {
                color3 = color2;
                subtitleColor = subtitleColor2;
                r = r6;
            }
            i5++;
            color2 = color3;
            subtitleColor2 = subtitleColor;
            r6 = r;
        }
        int i6 = subtitleColor2;
        int i7 = r6;
        this.extraHeight = this.initialAnimationExtraHeight * f;
        long j = this.userId;
        if (j == 0) {
            j = this.chatId;
        }
        int color7 = AvatarDrawable.getProfileColorForId(j);
        long j2 = this.userId;
        if (j2 == 0) {
            j2 = this.chatId;
        }
        int color22 = AvatarDrawable.getColorForId(j2);
        if (color7 != color22) {
            this.avatarDrawable.setColor(Color.rgb(Color.red(color22) + ((int) (((float) (Color.red(color7) - Color.red(color22))) * f)), Color.green(color22) + ((int) (((float) (Color.green(color7) - Color.green(color22))) * f)), Color.blue(color22) + ((int) (((float) (Color.blue(color7) - Color.blue(color22))) * f))));
            this.avatarImage.invalidate();
        }
        int i8 = this.navigationBarAnimationColorFrom;
        if (i8 != 0) {
            setNavigationBarColor(ColorUtils.blendARGB(i8, getNavigationBarColor(), f));
        }
        this.topView.invalidate();
        needLayout(true);
        this.fragmentView.invalidate();
    }

    /* JADX WARNING: type inference failed for: r4v13, types: [org.telegram.ui.ActionBar.BaseFragment] */
    /* JADX WARNING: type inference failed for: r4v36, types: [org.telegram.ui.ActionBar.BaseFragment] */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.animation.AnimatorSet onCustomTransitionAnimation(boolean r19, java.lang.Runnable r20) {
        /*
            r18 = this;
            r0 = r18
            int r1 = r0.playProfileAnimation
            r2 = 0
            if (r1 == 0) goto L_0x04ec
            boolean r1 = r0.allowProfileAnimation
            if (r1 == 0) goto L_0x04ec
            boolean r1 = r0.isPulledDown
            if (r1 != 0) goto L_0x04ec
            android.widget.ImageView r1 = r0.timeItem
            r3 = 1065353216(0x3var_, float:1.0)
            if (r1 == 0) goto L_0x0018
            r1.setAlpha(r3)
        L_0x0018:
            android.animation.AnimatorSet r1 = new android.animation.AnimatorSet
            r1.<init>()
            int r4 = r0.playProfileAnimation
            r5 = 2
            if (r4 != r5) goto L_0x0025
            r6 = 250(0xfa, double:1.235E-321)
            goto L_0x0027
        L_0x0025:
            r6 = 180(0xb4, double:8.9E-322)
        L_0x0027:
            r1.setDuration(r6)
            org.telegram.ui.Components.RecyclerListView r4 = r0.listView
            r4.setLayerType(r5, r2)
            org.telegram.ui.ActionBar.ActionBar r4 = r0.actionBar
            org.telegram.ui.ActionBar.ActionBarMenu r4 = r4.createMenu()
            r6 = 10
            org.telegram.ui.ActionBar.ActionBarMenuItem r7 = r4.getItem(r6)
            if (r7 != 0) goto L_0x004a
            org.telegram.ui.ActionBar.ActionBarMenuItem r7 = r0.animatingItem
            if (r7 != 0) goto L_0x004a
            r7 = 2131165492(0x7var_, float:1.7945203E38)
            org.telegram.ui.ActionBar.ActionBarMenuItem r6 = r4.addItem((int) r6, (int) r7)
            r0.animatingItem = r6
        L_0x004a:
            java.lang.String r8 = "animationProgress"
            r9 = 1045220557(0x3e4ccccd, float:0.2)
            r10 = 0
            r11 = 0
            r12 = 1
            if (r19 == 0) goto L_0x0315
            org.telegram.ui.ActionBar.SimpleTextView[] r13 = r0.onlineTextView
            r13 = r13[r12]
            android.view.ViewGroup$LayoutParams r13 = r13.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r13 = (android.widget.FrameLayout.LayoutParams) r13
            r14 = -1045954560(0xffffffffc1a80000, float:-21.0)
            float r15 = org.telegram.messenger.AndroidUtilities.density
            float r15 = r15 * r14
            r14 = 1090519040(0x41000000, float:8.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r14 = (float) r14
            float r15 = r15 + r14
            int r14 = (int) r15
            r13.rightMargin = r14
            org.telegram.ui.ActionBar.SimpleTextView[] r14 = r0.onlineTextView
            r14 = r14[r12]
            r14.setLayoutParams(r13)
            int r14 = r0.playProfileAnimation
            if (r14 == r5) goto L_0x00ed
            android.graphics.Point r14 = org.telegram.messenger.AndroidUtilities.displaySize
            int r14 = r14.x
            r15 = 1123811328(0x42fCLASSNAME, float:126.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r14 = r14 - r15
            float r14 = (float) r14
            r15 = 1101529088(0x41a80000, float:21.0)
            float r16 = org.telegram.messenger.AndroidUtilities.density
            float r16 = r16 * r15
            float r14 = r14 + r16
            double r14 = (double) r14
            double r14 = java.lang.Math.ceil(r14)
            int r14 = (int) r14
            org.telegram.ui.ActionBar.SimpleTextView[] r15 = r0.nameTextView
            r15 = r15[r12]
            android.graphics.Paint r15 = r15.getPaint()
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r0.nameTextView
            r2 = r2[r12]
            java.lang.CharSequence r2 = r2.getText()
            java.lang.String r2 = r2.toString()
            float r2 = r15.measureText(r2)
            r15 = 1066359849(0x3f8f5CLASSNAME, float:1.12)
            float r2 = r2 * r15
            org.telegram.ui.ActionBar.SimpleTextView[] r6 = r0.nameTextView
            r6 = r6[r12]
            int r6 = r6.getSideDrawablesSize()
            float r6 = (float) r6
            float r2 = r2 + r6
            org.telegram.ui.ActionBar.SimpleTextView[] r6 = r0.nameTextView
            r6 = r6[r12]
            android.view.ViewGroup$LayoutParams r6 = r6.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r6 = (android.widget.FrameLayout.LayoutParams) r6
            float r7 = (float) r14
            int r7 = (r7 > r2 ? 1 : (r7 == r2 ? 0 : -1))
            if (r7 >= 0) goto L_0x00d7
            float r7 = (float) r14
            float r7 = r7 / r15
            r17 = r4
            double r3 = (double) r7
            double r3 = java.lang.Math.ceil(r3)
            int r3 = (int) r3
            r6.width = r3
            goto L_0x00dc
        L_0x00d7:
            r17 = r4
            r3 = -2
            r6.width = r3
        L_0x00dc:
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r0.nameTextView
            r3 = r3[r12]
            r3.setLayoutParams(r6)
            r3 = 1118830592(0x42b00000, float:88.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            r0.initialAnimationExtraHeight = r3
            goto L_0x0114
        L_0x00ed:
            r17 = r4
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r0.nameTextView
            r2 = r2[r12]
            android.view.ViewGroup$LayoutParams r2 = r2.getLayoutParams()
            r6 = r2
            android.widget.FrameLayout$LayoutParams r6 = (android.widget.FrameLayout.LayoutParams) r6
            android.graphics.Point r2 = org.telegram.messenger.AndroidUtilities.displaySize
            int r2 = r2.x
            r3 = 1107296256(0x42000000, float:32.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 - r3
            float r2 = (float) r2
            r3 = 1070973583(0x3fd5CLASSNAMEf, float:1.67)
            float r2 = r2 / r3
            int r2 = (int) r2
            r6.width = r2
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r0.nameTextView
            r2 = r2[r12]
            r2.setLayoutParams(r6)
        L_0x0114:
            android.view.View r2 = r0.fragmentView
            r2.setBackgroundColor(r11)
            r0.setAnimationProgress(r10)
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            float[] r3 = new float[r5]
            r3 = {0, NUM} // fill-array
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r0, r8, r3)
            r2.add(r3)
            org.telegram.ui.Components.RLottieImageView r3 = r0.writeButton
            if (r3 == 0) goto L_0x0175
            java.lang.Object r3 = r3.getTag()
            if (r3 != 0) goto L_0x0175
            org.telegram.ui.Components.RLottieImageView r3 = r0.writeButton
            r3.setScaleX(r9)
            org.telegram.ui.Components.RLottieImageView r3 = r0.writeButton
            r3.setScaleY(r9)
            org.telegram.ui.Components.RLottieImageView r3 = r0.writeButton
            r3.setAlpha(r10)
            org.telegram.ui.Components.RLottieImageView r3 = r0.writeButton
            android.util.Property r4 = android.view.View.SCALE_X
            float[] r7 = new float[r12]
            r8 = 1065353216(0x3var_, float:1.0)
            r7[r11] = r8
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r7)
            r2.add(r3)
            org.telegram.ui.Components.RLottieImageView r3 = r0.writeButton
            android.util.Property r4 = android.view.View.SCALE_Y
            float[] r7 = new float[r12]
            r7[r11] = r8
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r7)
            r2.add(r3)
            org.telegram.ui.Components.RLottieImageView r3 = r0.writeButton
            android.util.Property r4 = android.view.View.ALPHA
            float[] r7 = new float[r12]
            r7[r11] = r8
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r7)
            r2.add(r3)
        L_0x0175:
            int r3 = r0.playProfileAnimation
            if (r3 != r5) goto L_0x01ad
            org.telegram.ui.ProfileActivity$AvatarImageView r3 = r0.avatarImage
            org.telegram.messenger.ImageReceiver r3 = r3.getImageReceiver()
            android.graphics.Bitmap r3 = r3.getBitmap()
            int r3 = org.telegram.messenger.AndroidUtilities.calcBitmapColor(r3)
            r0.avatarColor = r3
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r0.nameTextView
            r3 = r3[r12]
            r4 = -1
            r3.setTextColor(r4)
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r0.onlineTextView
            r3 = r3[r12]
            r4 = 179(0xb3, float:2.51E-43)
            r7 = 255(0xff, float:3.57E-43)
            int r4 = android.graphics.Color.argb(r4, r7, r7, r7)
            r3.setTextColor(r4)
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            r4 = 1090519039(0x40ffffff, float:7.9999995)
            r3.setItemsBackgroundColor(r4, r11)
            org.telegram.ui.ProfileActivity$OverlaysView r3 = r0.overlaysView
            r3.setOverlaysVisible()
        L_0x01ad:
            r3 = 0
        L_0x01ae:
            if (r3 >= r5) goto L_0x01d7
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r0.nameTextView
            r4 = r4[r3]
            if (r3 != 0) goto L_0x01b9
            r7 = 1065353216(0x3var_, float:1.0)
            goto L_0x01ba
        L_0x01b9:
            r7 = 0
        L_0x01ba:
            r4.setAlpha(r7)
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r0.nameTextView
            r4 = r4[r3]
            android.util.Property r7 = android.view.View.ALPHA
            float[] r8 = new float[r12]
            if (r3 != 0) goto L_0x01c9
            r9 = 0
            goto L_0x01cb
        L_0x01c9:
            r9 = 1065353216(0x3var_, float:1.0)
        L_0x01cb:
            r8[r11] = r9
            android.animation.ObjectAnimator r4 = android.animation.ObjectAnimator.ofFloat(r4, r7, r8)
            r2.add(r4)
            int r3 = r3 + 1
            goto L_0x01ae
        L_0x01d7:
            android.widget.ImageView r3 = r0.timeItem
            java.lang.Object r3 = r3.getTag()
            if (r3 == 0) goto L_0x020f
            android.widget.ImageView r3 = r0.timeItem
            android.util.Property r4 = android.view.View.ALPHA
            float[] r7 = new float[r5]
            r7 = {NUM, 0} // fill-array
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r7)
            r2.add(r3)
            android.widget.ImageView r3 = r0.timeItem
            android.util.Property r4 = android.view.View.SCALE_X
            float[] r7 = new float[r5]
            r7 = {NUM, 0} // fill-array
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r7)
            r2.add(r3)
            android.widget.ImageView r3 = r0.timeItem
            android.util.Property r4 = android.view.View.SCALE_Y
            float[] r7 = new float[r5]
            r7 = {NUM, 0} // fill-array
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r7)
            r2.add(r3)
        L_0x020f:
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.animatingItem
            if (r3 == 0) goto L_0x0227
            r4 = 1065353216(0x3var_, float:1.0)
            r3.setAlpha(r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.animatingItem
            android.util.Property r4 = android.view.View.ALPHA
            float[] r7 = new float[r12]
            r7[r11] = r10
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r7)
            r2.add(r3)
        L_0x0227:
            boolean r3 = r0.callItemVisible
            if (r3 == 0) goto L_0x0249
            long r3 = r0.chatId
            r7 = 0
            int r9 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r9 == 0) goto L_0x0249
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.callItem
            r3.setAlpha(r10)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.callItem
            android.util.Property r4 = android.view.View.ALPHA
            float[] r7 = new float[r12]
            r8 = 1065353216(0x3var_, float:1.0)
            r7[r11] = r8
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r7)
            r2.add(r3)
        L_0x0249:
            boolean r3 = r0.videoCallItemVisible
            if (r3 == 0) goto L_0x0263
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.videoCallItem
            r3.setAlpha(r10)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.videoCallItem
            android.util.Property r4 = android.view.View.ALPHA
            float[] r7 = new float[r12]
            r8 = 1065353216(0x3var_, float:1.0)
            r7[r11] = r8
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r7)
            r2.add(r3)
        L_0x0263:
            boolean r3 = r0.editItemVisible
            if (r3 == 0) goto L_0x027d
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.editItem
            r3.setAlpha(r10)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.editItem
            android.util.Property r4 = android.view.View.ALPHA
            float[] r7 = new float[r12]
            r8 = 1065353216(0x3var_, float:1.0)
            r7[r11] = r8
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r7)
            r2.add(r3)
        L_0x027d:
            r3 = 0
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r0.parentLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = r4.fragmentsStack
            int r4 = r4.size()
            if (r4 <= r12) goto L_0x029e
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r0.parentLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = r4.fragmentsStack
            org.telegram.ui.ActionBar.ActionBarLayout r7 = r0.parentLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r7 = r7.fragmentsStack
            int r7 = r7.size()
            int r7 = r7 - r5
            java.lang.Object r4 = r4.get(r7)
            org.telegram.ui.ActionBar.BaseFragment r4 = (org.telegram.ui.ActionBar.BaseFragment) r4
            r16 = r4
            goto L_0x02a0
        L_0x029e:
            r16 = 0
        L_0x02a0:
            r4 = r16
            boolean r7 = r4 instanceof org.telegram.ui.ChatActivity
            if (r7 == 0) goto L_0x02e4
            r7 = r4
            org.telegram.ui.ChatActivity r7 = (org.telegram.ui.ChatActivity) r7
            org.telegram.ui.Components.ChatAvatarContainer r7 = r7.getAvatarContainer()
            org.telegram.ui.ActionBar.SimpleTextView r8 = r7.getSubtitleTextView()
            android.graphics.drawable.Drawable r8 = r8.getLeftDrawable()
            if (r8 == 0) goto L_0x02e4
            org.telegram.ui.ActionBar.SimpleTextView r8 = r7.getSubtitleTextView()
            r0.transitionOnlineText = r8
            android.widget.FrameLayout r8 = r0.avatarContainer2
            r8.invalidate()
            r3 = 1
            org.telegram.ui.ActionBar.SimpleTextView[] r8 = r0.onlineTextView
            r8 = r8[r11]
            r8.setAlpha(r10)
            org.telegram.ui.ActionBar.SimpleTextView[] r8 = r0.onlineTextView
            r8 = r8[r12]
            r8.setAlpha(r10)
            org.telegram.ui.ActionBar.SimpleTextView[] r8 = r0.onlineTextView
            r8 = r8[r12]
            android.util.Property r9 = android.view.View.ALPHA
            float[] r13 = new float[r12]
            r14 = 1065353216(0x3var_, float:1.0)
            r13[r11] = r14
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r8, r9, r13)
            r2.add(r8)
        L_0x02e4:
            if (r3 != 0) goto L_0x0310
            r7 = 0
        L_0x02e7:
            if (r7 >= r5) goto L_0x0310
            org.telegram.ui.ActionBar.SimpleTextView[] r8 = r0.onlineTextView
            r8 = r8[r7]
            if (r7 != 0) goto L_0x02f2
            r9 = 1065353216(0x3var_, float:1.0)
            goto L_0x02f3
        L_0x02f2:
            r9 = 0
        L_0x02f3:
            r8.setAlpha(r9)
            org.telegram.ui.ActionBar.SimpleTextView[] r8 = r0.onlineTextView
            r8 = r8[r7]
            android.util.Property r9 = android.view.View.ALPHA
            float[] r13 = new float[r12]
            if (r7 != 0) goto L_0x0302
            r14 = 0
            goto L_0x0304
        L_0x0302:
            r14 = 1065353216(0x3var_, float:1.0)
        L_0x0304:
            r13[r11] = r14
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r8, r9, r13)
            r2.add(r8)
            int r7 = r7 + 1
            goto L_0x02e7
        L_0x0310:
            r1.playTogether(r2)
            goto L_0x04ab
        L_0x0315:
            r17 = r4
            float r2 = r0.extraHeight
            r0.initialAnimationExtraHeight = r2
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            float[] r3 = new float[r5]
            r3 = {NUM, 0} // fill-array
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r0, r8, r3)
            r2.add(r3)
            org.telegram.ui.Components.RLottieImageView r3 = r0.writeButton
            if (r3 == 0) goto L_0x035b
            android.util.Property r4 = android.view.View.SCALE_X
            float[] r6 = new float[r12]
            r6[r11] = r9
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r6)
            r2.add(r3)
            org.telegram.ui.Components.RLottieImageView r3 = r0.writeButton
            android.util.Property r4 = android.view.View.SCALE_Y
            float[] r6 = new float[r12]
            r6[r11] = r9
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r6)
            r2.add(r3)
            org.telegram.ui.Components.RLottieImageView r3 = r0.writeButton
            android.util.Property r4 = android.view.View.ALPHA
            float[] r6 = new float[r12]
            r6[r11] = r10
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r6)
            r2.add(r3)
        L_0x035b:
            r3 = 0
        L_0x035c:
            if (r3 >= r5) goto L_0x0378
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r0.nameTextView
            r4 = r4[r3]
            android.util.Property r6 = android.view.View.ALPHA
            float[] r7 = new float[r12]
            if (r3 != 0) goto L_0x036b
            r8 = 1065353216(0x3var_, float:1.0)
            goto L_0x036c
        L_0x036b:
            r8 = 0
        L_0x036c:
            r7[r11] = r8
            android.animation.ObjectAnimator r4 = android.animation.ObjectAnimator.ofFloat(r4, r6, r7)
            r2.add(r4)
            int r3 = r3 + 1
            goto L_0x035c
        L_0x0378:
            android.widget.ImageView r3 = r0.timeItem
            java.lang.Object r3 = r3.getTag()
            if (r3 == 0) goto L_0x03b0
            android.widget.ImageView r3 = r0.timeItem
            android.util.Property r4 = android.view.View.ALPHA
            float[] r6 = new float[r5]
            r6 = {0, NUM} // fill-array
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r6)
            r2.add(r3)
            android.widget.ImageView r3 = r0.timeItem
            android.util.Property r4 = android.view.View.SCALE_X
            float[] r6 = new float[r5]
            r6 = {0, NUM} // fill-array
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r6)
            r2.add(r3)
            android.widget.ImageView r3 = r0.timeItem
            android.util.Property r4 = android.view.View.SCALE_Y
            float[] r6 = new float[r5]
            r6 = {0, NUM} // fill-array
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r6)
            r2.add(r3)
        L_0x03b0:
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.animatingItem
            if (r3 == 0) goto L_0x03c8
            r3.setAlpha(r10)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.animatingItem
            android.util.Property r4 = android.view.View.ALPHA
            float[] r6 = new float[r12]
            r7 = 1065353216(0x3var_, float:1.0)
            r6[r11] = r7
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r6)
            r2.add(r3)
        L_0x03c8:
            boolean r3 = r0.callItemVisible
            if (r3 == 0) goto L_0x03ea
            long r3 = r0.chatId
            r6 = 0
            int r8 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1))
            if (r8 == 0) goto L_0x03ea
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.callItem
            r4 = 1065353216(0x3var_, float:1.0)
            r3.setAlpha(r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.callItem
            android.util.Property r4 = android.view.View.ALPHA
            float[] r6 = new float[r12]
            r6[r11] = r10
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r6)
            r2.add(r3)
        L_0x03ea:
            boolean r3 = r0.videoCallItemVisible
            if (r3 == 0) goto L_0x0404
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.videoCallItem
            r4 = 1065353216(0x3var_, float:1.0)
            r3.setAlpha(r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.videoCallItem
            android.util.Property r4 = android.view.View.ALPHA
            float[] r6 = new float[r12]
            r6[r11] = r10
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r6)
            r2.add(r3)
        L_0x0404:
            boolean r3 = r0.editItemVisible
            if (r3 == 0) goto L_0x041f
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.editItem
            r8 = 1065353216(0x3var_, float:1.0)
            r3.setAlpha(r8)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.editItem
            android.util.Property r4 = android.view.View.ALPHA
            float[] r6 = new float[r12]
            r6[r11] = r10
            android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r6)
            r2.add(r3)
            goto L_0x0421
        L_0x041f:
            r8 = 1065353216(0x3var_, float:1.0)
        L_0x0421:
            r3 = 0
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r0.parentLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = r4.fragmentsStack
            int r4 = r4.size()
            if (r4 <= r12) goto L_0x0442
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r0.parentLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = r4.fragmentsStack
            org.telegram.ui.ActionBar.ActionBarLayout r6 = r0.parentLayout
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r6 = r6.fragmentsStack
            int r6 = r6.size()
            int r6 = r6 - r5
            java.lang.Object r4 = r4.get(r6)
            org.telegram.ui.ActionBar.BaseFragment r4 = (org.telegram.ui.ActionBar.BaseFragment) r4
            r16 = r4
            goto L_0x0444
        L_0x0442:
            r16 = 0
        L_0x0444:
            r4 = r16
            boolean r6 = r4 instanceof org.telegram.ui.ChatActivity
            if (r6 == 0) goto L_0x0489
            r6 = r4
            org.telegram.ui.ChatActivity r6 = (org.telegram.ui.ChatActivity) r6
            org.telegram.ui.Components.ChatAvatarContainer r6 = r6.getAvatarContainer()
            org.telegram.ui.ActionBar.SimpleTextView r7 = r6.getSubtitleTextView()
            android.graphics.drawable.Drawable r7 = r7.getLeftDrawable()
            if (r7 == 0) goto L_0x0489
            org.telegram.ui.ActionBar.SimpleTextView r7 = r6.getSubtitleTextView()
            r0.transitionOnlineText = r7
            android.widget.FrameLayout r7 = r0.avatarContainer2
            r7.invalidate()
            r3 = 1
            org.telegram.ui.ActionBar.SimpleTextView[] r7 = r0.onlineTextView
            r7 = r7[r11]
            android.util.Property r9 = android.view.View.ALPHA
            float[] r13 = new float[r12]
            r13[r11] = r10
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r9, r13)
            r2.add(r7)
            org.telegram.ui.ActionBar.SimpleTextView[] r7 = r0.onlineTextView
            r7 = r7[r12]
            android.util.Property r9 = android.view.View.ALPHA
            float[] r13 = new float[r12]
            r13[r11] = r10
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r9, r13)
            r2.add(r7)
        L_0x0489:
            if (r3 != 0) goto L_0x04a8
            r6 = 0
        L_0x048c:
            if (r6 >= r5) goto L_0x04a8
            org.telegram.ui.ActionBar.SimpleTextView[] r7 = r0.onlineTextView
            r7 = r7[r6]
            android.util.Property r9 = android.view.View.ALPHA
            float[] r13 = new float[r12]
            if (r6 != 0) goto L_0x049b
            r14 = 1065353216(0x3var_, float:1.0)
            goto L_0x049c
        L_0x049b:
            r14 = 0
        L_0x049c:
            r13[r11] = r14
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r7, r9, r13)
            r2.add(r7)
            int r6 = r6 + 1
            goto L_0x048c
        L_0x04a8:
            r1.playTogether(r2)
        L_0x04ab:
            r0.profileTransitionInProgress = r12
            float[] r2 = new float[r5]
            r2 = {0, NUM} // fill-array
            android.animation.ValueAnimator r2 = android.animation.ValueAnimator.ofFloat(r2)
            org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda11 r3 = new org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda11
            r3.<init>(r0)
            r2.addUpdateListener(r3)
            android.animation.Animator[] r3 = new android.animation.Animator[r12]
            r3[r11] = r2
            r1.playTogether(r3)
            org.telegram.ui.ProfileActivity$30 r3 = new org.telegram.ui.ProfileActivity$30
            r4 = r20
            r3.<init>(r4)
            r1.addListener(r3)
            int r3 = r0.playProfileAnimation
            if (r3 != r5) goto L_0x04d6
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            goto L_0x04db
        L_0x04d6:
            android.view.animation.DecelerateInterpolator r3 = new android.view.animation.DecelerateInterpolator
            r3.<init>()
        L_0x04db:
            r1.setInterpolator(r3)
            r1.getClass()
            org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda6 r3 = new org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda6
            r3.<init>(r1)
            r5 = 50
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r3, r5)
            return r1
        L_0x04ec:
            r4 = r20
            r1 = 0
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.onCustomTransitionAnimation(boolean, java.lang.Runnable):android.animation.AnimatorSet");
    }

    /* renamed from: lambda$onCustomTransitionAnimation$26$org-telegram-ui-ProfileActivity  reason: not valid java name */
    public /* synthetic */ void m3706x4b6486e4(ValueAnimator valueAnimator1) {
        this.fragmentView.invalidate();
    }

    /* access modifiers changed from: private */
    public void updateOnlineCount(boolean notify) {
        this.onlineCount = 0;
        int currentTime = getConnectionsManager().getCurrentTime();
        this.sortedUsers.clear();
        TLRPC.ChatFull chatFull = this.chatInfo;
        if ((chatFull instanceof TLRPC.TL_chatFull) || ((chatFull instanceof TLRPC.TL_channelFull) && chatFull.participants_count <= 200 && this.chatInfo.participants != null)) {
            for (int a = 0; a < this.chatInfo.participants.participants.size(); a++) {
                TLRPC.User user = getMessagesController().getUser(Long.valueOf(this.chatInfo.participants.participants.get(a).user_id));
                if (!(user == null || user.status == null || ((user.status.expires <= currentTime && user.id != getUserConfig().getClientUserId()) || user.status.expires <= 10000))) {
                    this.onlineCount++;
                }
                this.sortedUsers.add(Integer.valueOf(a));
            }
            try {
                Collections.sort(this.sortedUsers, new ProfileActivity$$ExternalSyntheticLambda17(this, currentTime));
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            if (notify && this.listAdapter != null && this.membersStartRow > 0) {
                AndroidUtilities.updateVisibleRows(this.listView);
            }
            if (this.sharedMediaLayout != null && this.sharedMediaRow != -1) {
                if ((this.sortedUsers.size() > 5 || this.usersForceShowingIn == 2) && this.usersForceShowingIn != 1) {
                    this.sharedMediaLayout.setChatUsers(this.sortedUsers, this.chatInfo);
                    return;
                }
                return;
            }
            return;
        }
        TLRPC.ChatFull chatFull2 = this.chatInfo;
        if ((chatFull2 instanceof TLRPC.TL_channelFull) && chatFull2.participants_count > 200) {
            this.onlineCount = this.chatInfo.online_count;
        }
    }

    /* renamed from: lambda$updateOnlineCount$27$org-telegram-ui-ProfileActivity  reason: not valid java name */
    public /* synthetic */ int m3719lambda$updateOnlineCount$27$orgtelegramuiProfileActivity(int currentTime, Integer lhs, Integer rhs) {
        TLRPC.User user1 = getMessagesController().getUser(Long.valueOf(this.chatInfo.participants.participants.get(rhs.intValue()).user_id));
        TLRPC.User user2 = getMessagesController().getUser(Long.valueOf(this.chatInfo.participants.participants.get(lhs.intValue()).user_id));
        int status1 = 0;
        int status2 = 0;
        if (user1 != null) {
            if (user1.bot) {
                status1 = -110;
            } else if (user1.self) {
                status1 = currentTime + 50000;
            } else if (user1.status != null) {
                status1 = user1.status.expires;
            }
        }
        if (user2 != null) {
            if (user2.bot) {
                status2 = -110;
            } else if (user2.self) {
                status2 = currentTime + 50000;
            } else if (user2.status != null) {
                status2 = user2.status.expires;
            }
        }
        if (status1 <= 0 || status2 <= 0) {
            if (status1 >= 0 || status2 >= 0) {
                if ((status1 >= 0 || status2 <= 0) && (status1 != 0 || status2 == 0)) {
                    return ((status2 >= 0 || status1 <= 0) && (status2 != 0 || status1 == 0)) ? 0 : 1;
                }
                return -1;
            } else if (status1 > status2) {
                return 1;
            } else {
                return status1 < status2 ? -1 : 0;
            }
        } else if (status1 > status2) {
            return 1;
        } else {
            return status1 < status2 ? -1 : 0;
        }
    }

    public void setChatInfo(TLRPC.ChatFull value) {
        this.chatInfo = value;
        if (!(value == null || value.migrated_from_chat_id == 0 || this.mergeDialogId != 0)) {
            this.mergeDialogId = -this.chatInfo.migrated_from_chat_id;
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

    public void setUserInfo(TLRPC.UserFull value) {
        this.userInfo = value;
    }

    public boolean canSearchMembers() {
        return this.canSearchMembers;
    }

    private void fetchUsersFromChannelInfo() {
        TLRPC.Chat chat = this.currentChat;
        if (chat != null && chat.megagroup) {
            TLRPC.ChatFull chatFull = this.chatInfo;
            if ((chatFull instanceof TLRPC.TL_channelFull) && chatFull.participants != null) {
                for (int a = 0; a < this.chatInfo.participants.participants.size(); a++) {
                    TLRPC.ChatParticipant chatParticipant = this.chatInfo.participants.participants.get(a);
                    this.participantsMap.put(chatParticipant.user_id, chatParticipant);
                }
            }
        }
    }

    private void kickUser(long uid, TLRPC.ChatParticipant participant) {
        if (uid != 0) {
            TLRPC.User user = getMessagesController().getUser(Long.valueOf(uid));
            getMessagesController().deleteParticipantFromChat(this.chatId, user, this.chatInfo);
            if (!(this.currentChat == null || user == null || !BulletinFactory.canShowBulletin(this))) {
                BulletinFactory.createRemoveFromChatBulletin(this, user, this.currentChat.title).show();
            }
            if (this.chatInfo.participants.participants.remove(participant)) {
                updateListAnimated(true);
                return;
            }
            return;
        }
        getNotificationCenter().removeObserver(this, NotificationCenter.closeChats);
        if (AndroidUtilities.isTablet()) {
            getNotificationCenter().postNotificationName(NotificationCenter.closeChats, Long.valueOf(-this.chatId));
        } else {
            getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
        }
        getMessagesController().deleteParticipantFromChat(this.chatId, getMessagesController().getUser(Long.valueOf(getUserConfig().getClientUserId())), this.chatInfo);
        this.playProfileAnimation = 0;
        finishFragment();
    }

    public boolean isChat() {
        return this.chatId != 0;
    }

    /* access modifiers changed from: private */
    public void updateRowsIds() {
        int actionBarHeight;
        int i;
        TLRPC.UserFull userFull;
        ProfileGalleryView profileGalleryView;
        int prevRowsCount = this.rowCount;
        this.rowCount = 0;
        this.setAvatarRow = -1;
        this.setAvatarSectionRow = -1;
        this.numberSectionRow = -1;
        this.numberRow = -1;
        this.setUsernameRow = -1;
        this.bioRow = -1;
        this.phoneSuggestionSectionRow = -1;
        this.phoneSuggestionRow = -1;
        this.passwordSuggestionSectionRow = -1;
        this.passwordSuggestionRow = -1;
        this.settingsSectionRow = -1;
        this.settingsSectionRow2 = -1;
        this.notificationRow = -1;
        this.languageRow = -1;
        this.privacyRow = -1;
        this.dataRow = -1;
        this.chatRow = -1;
        this.filtersRow = -1;
        this.devicesRow = -1;
        this.devicesSectionRow = -1;
        this.helpHeaderRow = -1;
        this.questionRow = -1;
        this.faqRow = -1;
        this.policyRow = -1;
        this.helpSectionCell = -1;
        this.debugHeaderRow = -1;
        this.sendLogsRow = -1;
        this.sendLastLogsRow = -1;
        this.clearLogsRow = -1;
        this.switchBackendRow = -1;
        this.versionRow = -1;
        this.sendMessageRow = -1;
        this.reportRow = -1;
        this.emptyRow = -1;
        this.infoHeaderRow = -1;
        this.phoneRow = -1;
        this.userInfoRow = -1;
        this.locationRow = -1;
        this.channelInfoRow = -1;
        this.usernameRow = -1;
        this.settingsTimerRow = -1;
        this.settingsKeyRow = -1;
        this.notificationsDividerRow = -1;
        this.notificationsRow = -1;
        this.infoSectionRow = -1;
        this.secretSettingsSectionRow = -1;
        this.bottomPaddingRow = -1;
        this.membersHeaderRow = -1;
        this.membersStartRow = -1;
        this.membersEndRow = -1;
        this.addMemberRow = -1;
        this.subscribersRow = -1;
        this.subscribersRequestsRow = -1;
        this.administratorsRow = -1;
        this.blockedUsersRow = -1;
        this.membersSectionRow = -1;
        this.sharedMediaRow = -1;
        this.unblockRow = -1;
        this.joinRow = -1;
        this.lastSectionRow = -1;
        this.visibleChatParticipants.clear();
        this.visibleSortedUsers.clear();
        boolean hasMedia = false;
        SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader2 = this.sharedMediaPreloader;
        if (sharedMediaPreloader2 != null) {
            int[] lastMediaCount = sharedMediaPreloader2.getLastMediaCount();
            int a = 0;
            while (true) {
                if (a >= lastMediaCount.length) {
                    break;
                } else if (lastMediaCount[a] > 0) {
                    hasMedia = true;
                    break;
                } else {
                    a++;
                }
            }
        }
        boolean z = true;
        if (this.userId != 0) {
            if (LocaleController.isRTL) {
                int i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.emptyRow = i2;
            }
            TLRPC.User user = getMessagesController().getUser(Long.valueOf(this.userId));
            if (UserObject.isUserSelf(user)) {
                if (this.avatarBig == null && ((user.photo == null || (!(user.photo.photo_big instanceof TLRPC.TL_fileLocation_layer97) && !(user.photo.photo_big instanceof TLRPC.TL_fileLocationToBeDeprecated))) && ((profileGalleryView = this.avatarsViewPager) == null || profileGalleryView.getRealCount() == 0))) {
                    int i3 = this.rowCount;
                    int i4 = i3 + 1;
                    this.rowCount = i4;
                    this.setAvatarRow = i3;
                    this.rowCount = i4 + 1;
                    this.setAvatarSectionRow = i4;
                }
                int i5 = this.rowCount;
                int i6 = i5 + 1;
                this.rowCount = i6;
                this.numberSectionRow = i5;
                int i7 = i6 + 1;
                this.rowCount = i7;
                this.numberRow = i6;
                int i8 = i7 + 1;
                this.rowCount = i8;
                this.setUsernameRow = i7;
                int i9 = i8 + 1;
                this.rowCount = i9;
                this.bioRow = i8;
                this.rowCount = i9 + 1;
                this.settingsSectionRow = i9;
                Set<String> suggestions = getMessagesController().pendingSuggestions;
                if (suggestions.contains("VALIDATE_PHONE_NUMBER")) {
                    int i10 = this.rowCount;
                    int i11 = i10 + 1;
                    this.rowCount = i11;
                    this.phoneSuggestionRow = i10;
                    this.rowCount = i11 + 1;
                    this.phoneSuggestionSectionRow = i11;
                }
                if (suggestions.contains("VALIDATE_PASSWORD")) {
                    int i12 = this.rowCount;
                    int i13 = i12 + 1;
                    this.rowCount = i13;
                    this.passwordSuggestionRow = i12;
                    this.rowCount = i13 + 1;
                    this.passwordSuggestionSectionRow = i13;
                }
                int i14 = this.rowCount;
                int i15 = i14 + 1;
                this.rowCount = i15;
                this.settingsSectionRow2 = i14;
                int i16 = i15 + 1;
                this.rowCount = i16;
                this.notificationRow = i15;
                int i17 = i16 + 1;
                this.rowCount = i17;
                this.privacyRow = i16;
                int i18 = i17 + 1;
                this.rowCount = i18;
                this.dataRow = i17;
                this.rowCount = i18 + 1;
                this.chatRow = i18;
                if (getMessagesController().filtersEnabled || !getMessagesController().dialogFilters.isEmpty()) {
                    int i19 = this.rowCount;
                    this.rowCount = i19 + 1;
                    this.filtersRow = i19;
                }
                int i20 = this.rowCount;
                int i21 = i20 + 1;
                this.rowCount = i21;
                this.devicesRow = i20;
                int i22 = i21 + 1;
                this.rowCount = i22;
                this.languageRow = i21;
                int i23 = i22 + 1;
                this.rowCount = i23;
                this.devicesSectionRow = i22;
                int i24 = i23 + 1;
                this.rowCount = i24;
                this.helpHeaderRow = i23;
                int i25 = i24 + 1;
                this.rowCount = i25;
                this.questionRow = i24;
                int i26 = i25 + 1;
                this.rowCount = i26;
                this.faqRow = i25;
                this.rowCount = i26 + 1;
                this.policyRow = i26;
                if (BuildVars.LOGS_ENABLED || BuildVars.DEBUG_PRIVATE_VERSION) {
                    int i27 = this.rowCount;
                    int i28 = i27 + 1;
                    this.rowCount = i28;
                    this.helpSectionCell = i27;
                    this.rowCount = i28 + 1;
                    this.debugHeaderRow = i28;
                }
                if (BuildVars.LOGS_ENABLED) {
                    int i29 = this.rowCount;
                    int i30 = i29 + 1;
                    this.rowCount = i30;
                    this.sendLogsRow = i29;
                    int i31 = i30 + 1;
                    this.rowCount = i31;
                    this.sendLastLogsRow = i30;
                    this.rowCount = i31 + 1;
                    this.clearLogsRow = i31;
                }
                if (BuildVars.DEBUG_PRIVATE_VERSION) {
                    int i32 = this.rowCount;
                    this.rowCount = i32 + 1;
                    this.switchBackendRow = i32;
                }
                int i33 = this.rowCount;
                this.rowCount = i33 + 1;
                this.versionRow = i33;
            } else {
                TLRPC.UserFull userFull2 = this.userInfo;
                boolean hasInfo = (userFull2 != null && !TextUtils.isEmpty(userFull2.about)) || (user != null && !TextUtils.isEmpty(user.username));
                if (user == null || TextUtils.isEmpty(user.phone)) {
                    z = false;
                }
                boolean hasPhone = z;
                int i34 = this.rowCount;
                int i35 = i34 + 1;
                this.rowCount = i35;
                this.infoHeaderRow = i34;
                if (!this.isBot && (hasPhone || !hasInfo)) {
                    this.rowCount = i35 + 1;
                    this.phoneRow = i35;
                }
                TLRPC.UserFull userFull3 = this.userInfo;
                if (userFull3 != null && !TextUtils.isEmpty(userFull3.about)) {
                    int i36 = this.rowCount;
                    this.rowCount = i36 + 1;
                    this.userInfoRow = i36;
                }
                if (user != null && !TextUtils.isEmpty(user.username)) {
                    int i37 = this.rowCount;
                    this.rowCount = i37 + 1;
                    this.usernameRow = i37;
                }
                if (!(this.phoneRow == -1 && this.userInfoRow == -1 && this.usernameRow == -1)) {
                    int i38 = this.rowCount;
                    this.rowCount = i38 + 1;
                    this.notificationsDividerRow = i38;
                }
                if (this.userId != getUserConfig().getClientUserId()) {
                    int i39 = this.rowCount;
                    this.rowCount = i39 + 1;
                    this.notificationsRow = i39;
                }
                int i40 = this.rowCount;
                int i41 = i40 + 1;
                this.rowCount = i41;
                this.infoSectionRow = i40;
                TLRPC.EncryptedChat encryptedChat = this.currentEncryptedChat;
                if (encryptedChat instanceof TLRPC.TL_encryptedChat) {
                    int i42 = i41 + 1;
                    this.rowCount = i42;
                    this.settingsTimerRow = i41;
                    int i43 = i42 + 1;
                    this.rowCount = i43;
                    this.settingsKeyRow = i42;
                    this.rowCount = i43 + 1;
                    this.secretSettingsSectionRow = i43;
                }
                if (user != null && !this.isBot && encryptedChat == null && user.id != getUserConfig().getClientUserId() && this.userBlocked) {
                    int i44 = this.rowCount;
                    int i45 = i44 + 1;
                    this.rowCount = i45;
                    this.unblockRow = i44;
                    this.rowCount = i45 + 1;
                    this.lastSectionRow = i45;
                }
                if (hasMedia || !((userFull = this.userInfo) == null || userFull.common_chats_count == 0)) {
                    int i46 = this.rowCount;
                    this.rowCount = i46 + 1;
                    this.sharedMediaRow = i46;
                } else if (this.lastSectionRow == -1 && this.needSendMessage) {
                    int i47 = this.rowCount;
                    int i48 = i47 + 1;
                    this.rowCount = i48;
                    this.sendMessageRow = i47;
                    int i49 = i48 + 1;
                    this.rowCount = i49;
                    this.reportRow = i48;
                    this.rowCount = i49 + 1;
                    this.lastSectionRow = i49;
                }
            }
        } else if (this.chatId != 0) {
            TLRPC.ChatFull chatFull = this.chatInfo;
            if ((chatFull != null && (!TextUtils.isEmpty(chatFull.about) || (this.chatInfo.location instanceof TLRPC.TL_channelLocation))) || !TextUtils.isEmpty(this.currentChat.username)) {
                if (LocaleController.isRTL && ChatObject.isChannel(this.currentChat) && this.chatInfo != null && !this.currentChat.megagroup && this.chatInfo.linked_chat_id != 0) {
                    int i50 = this.rowCount;
                    this.rowCount = i50 + 1;
                    this.emptyRow = i50;
                }
                int i51 = this.rowCount;
                this.rowCount = i51 + 1;
                this.infoHeaderRow = i51;
                TLRPC.ChatFull chatFull2 = this.chatInfo;
                if (chatFull2 != null) {
                    if (!TextUtils.isEmpty(chatFull2.about)) {
                        int i52 = this.rowCount;
                        this.rowCount = i52 + 1;
                        this.channelInfoRow = i52;
                    }
                    if (this.chatInfo.location instanceof TLRPC.TL_channelLocation) {
                        int i53 = this.rowCount;
                        this.rowCount = i53 + 1;
                        this.locationRow = i53;
                    }
                }
                if (!TextUtils.isEmpty(this.currentChat.username)) {
                    int i54 = this.rowCount;
                    this.rowCount = i54 + 1;
                    this.usernameRow = i54;
                }
            }
            if (this.infoHeaderRow != -1) {
                int i55 = this.rowCount;
                this.rowCount = i55 + 1;
                this.notificationsDividerRow = i55;
            }
            int i56 = this.rowCount;
            int i57 = i56 + 1;
            this.rowCount = i57;
            this.notificationsRow = i56;
            this.rowCount = i57 + 1;
            this.infoSectionRow = i57;
            if (ChatObject.isChannel(this.currentChat) && !this.currentChat.megagroup && this.chatInfo != null && (this.currentChat.creator || this.chatInfo.can_view_participants)) {
                int i58 = this.rowCount;
                int i59 = i58 + 1;
                this.rowCount = i59;
                this.membersHeaderRow = i58;
                this.rowCount = i59 + 1;
                this.subscribersRow = i59;
                if (this.chatInfo.requests_pending > 0) {
                    int i60 = this.rowCount;
                    this.rowCount = i60 + 1;
                    this.subscribersRequestsRow = i60;
                }
                int i61 = this.rowCount;
                this.rowCount = i61 + 1;
                this.administratorsRow = i61;
                if (!(this.chatInfo.banned_count == 0 && this.chatInfo.kicked_count == 0)) {
                    int i62 = this.rowCount;
                    this.rowCount = i62 + 1;
                    this.blockedUsersRow = i62;
                }
                int i63 = this.rowCount;
                this.rowCount = i63 + 1;
                this.membersSectionRow = i63;
            }
            if (ChatObject.isChannel(this.currentChat)) {
                if (this.chatInfo != null && this.currentChat.megagroup && this.chatInfo.participants != null && !this.chatInfo.participants.participants.isEmpty()) {
                    if (!ChatObject.isNotInChat(this.currentChat) && ChatObject.canAddUsers(this.currentChat) && this.chatInfo.participants_count < getMessagesController().maxMegagroupCount) {
                        int i64 = this.rowCount;
                        this.rowCount = i64 + 1;
                        this.addMemberRow = i64;
                    }
                    int count = this.chatInfo.participants.participants.size();
                    if ((count <= 5 || !hasMedia || this.usersForceShowingIn == 1) && this.usersForceShowingIn != 2) {
                        if (this.addMemberRow == -1) {
                            int i65 = this.rowCount;
                            this.rowCount = i65 + 1;
                            this.membersHeaderRow = i65;
                        }
                        int i66 = this.rowCount;
                        this.membersStartRow = i66;
                        int i67 = i66 + count;
                        this.rowCount = i67;
                        this.membersEndRow = i67;
                        this.rowCount = i67 + 1;
                        this.membersSectionRow = i67;
                        this.visibleChatParticipants.addAll(this.chatInfo.participants.participants);
                        ArrayList<Integer> arrayList = this.sortedUsers;
                        if (arrayList != null) {
                            this.visibleSortedUsers.addAll(arrayList);
                        }
                        this.usersForceShowingIn = 1;
                        SharedMediaLayout sharedMediaLayout2 = this.sharedMediaLayout;
                        if (sharedMediaLayout2 != null) {
                            sharedMediaLayout2.setChatUsers((ArrayList<Integer>) null, (TLRPC.ChatFull) null);
                        }
                    } else {
                        if (this.addMemberRow != -1) {
                            int i68 = this.rowCount;
                            this.rowCount = i68 + 1;
                            this.membersSectionRow = i68;
                        }
                        if (this.sharedMediaLayout != null) {
                            if (!this.sortedUsers.isEmpty()) {
                                this.usersForceShowingIn = 2;
                            }
                            this.sharedMediaLayout.setChatUsers(this.sortedUsers, this.chatInfo);
                        }
                    }
                }
                if (this.lastSectionRow == -1 && this.currentChat.left && !this.currentChat.kicked) {
                    int i69 = this.rowCount;
                    int i70 = i69 + 1;
                    this.rowCount = i70;
                    this.joinRow = i69;
                    this.rowCount = i70 + 1;
                    this.lastSectionRow = i70;
                }
            } else {
                TLRPC.ChatFull chatFull3 = this.chatInfo;
                if (chatFull3 != null && !(chatFull3.participants instanceof TLRPC.TL_chatParticipantsForbidden)) {
                    if (ChatObject.canAddUsers(this.currentChat) || this.currentChat.default_banned_rights == null || !this.currentChat.default_banned_rights.invite_users) {
                        int i71 = this.rowCount;
                        this.rowCount = i71 + 1;
                        this.addMemberRow = i71;
                    }
                    if (this.chatInfo.participants.participants.size() <= 5 || !hasMedia) {
                        if (this.addMemberRow == -1) {
                            int i72 = this.rowCount;
                            this.rowCount = i72 + 1;
                            this.membersHeaderRow = i72;
                        }
                        int i73 = this.rowCount;
                        this.membersStartRow = i73;
                        int size = i73 + this.chatInfo.participants.participants.size();
                        this.rowCount = size;
                        this.membersEndRow = size;
                        this.rowCount = size + 1;
                        this.membersSectionRow = size;
                        this.visibleChatParticipants.addAll(this.chatInfo.participants.participants);
                        ArrayList<Integer> arrayList2 = this.sortedUsers;
                        if (arrayList2 != null) {
                            this.visibleSortedUsers.addAll(arrayList2);
                        }
                        SharedMediaLayout sharedMediaLayout3 = this.sharedMediaLayout;
                        if (sharedMediaLayout3 != null) {
                            sharedMediaLayout3.setChatUsers((ArrayList<Integer>) null, (TLRPC.ChatFull) null);
                        }
                    } else {
                        if (this.addMemberRow != -1) {
                            int i74 = this.rowCount;
                            this.rowCount = i74 + 1;
                            this.membersSectionRow = i74;
                        }
                        SharedMediaLayout sharedMediaLayout4 = this.sharedMediaLayout;
                        if (sharedMediaLayout4 != null) {
                            sharedMediaLayout4.setChatUsers(this.sortedUsers, this.chatInfo);
                        }
                    }
                }
            }
            if (hasMedia) {
                int i75 = this.rowCount;
                this.rowCount = i75 + 1;
                this.sharedMediaRow = i75;
            }
        }
        if (this.sharedMediaRow == -1) {
            int i76 = this.rowCount;
            this.rowCount = i76 + 1;
            this.bottomPaddingRow = i76;
        }
        if (this.actionBar != null) {
            actionBarHeight = ActionBar.getCurrentActionBarHeight() + (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0);
        } else {
            actionBarHeight = 0;
        }
        if (this.listView == null || prevRowsCount > this.rowCount || ((i = this.listContentHeight) != 0 && i + actionBarHeight + AndroidUtilities.dp(88.0f) < this.listView.getMeasuredHeight())) {
            this.lastMeasuredContentWidth = 0;
        }
    }

    private Drawable getScamDrawable(int type) {
        if (this.scamDrawable == null) {
            ScamDrawable scamDrawable2 = new ScamDrawable(11, type);
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
    public void updateProfileData() {
        String onlineTextOverride;
        String profileStatusString;
        String shortNumber;
        String filter;
        View view;
        String onlineTextOverride2;
        String statusString;
        int currentConnectionState;
        String str;
        String str2;
        TLRPC.ChatFull chatFull;
        String profileStatusString2;
        String filter2;
        ImageLocation thumbLocation;
        ImageLocation imageLocation;
        String newString2;
        String newString;
        if (this.avatarContainer != null && this.nameTextView != null) {
            int currentConnectionState2 = getConnectionsManager().getConnectionState();
            if (currentConnectionState2 == 2) {
                onlineTextOverride = LocaleController.getString("WaitingForNetwork", NUM);
            } else if (currentConnectionState2 == 1) {
                onlineTextOverride = LocaleController.getString("Connecting", NUM);
            } else if (currentConnectionState2 == 5) {
                onlineTextOverride = LocaleController.getString("Updating", NUM);
            } else if (currentConnectionState2 == 4) {
                onlineTextOverride = LocaleController.getString("ConnectingToProxy", NUM);
            } else {
                onlineTextOverride = null;
            }
            if (this.userId != 0) {
                TLRPC.User user = getMessagesController().getUser(Long.valueOf(this.userId));
                if (user != null) {
                    TLRPC.FileLocation photoBig = null;
                    if (user.photo != null) {
                        photoBig = user.photo.photo_big;
                    }
                    this.avatarDrawable.setInfo(user);
                    ImageLocation imageLocation2 = ImageLocation.getForUserOrChat(user, 0);
                    ImageLocation thumbLocation2 = ImageLocation.getForUserOrChat(user, 1);
                    ImageLocation videoLocation = this.avatarsViewPager.getCurrentVideoLocation(thumbLocation2, imageLocation2);
                    this.avatarsViewPager.initIfEmpty(imageLocation2, thumbLocation2);
                    if (videoLocation == null || videoLocation.imageType != 2) {
                        filter2 = null;
                    } else {
                        filter2 = "g";
                    }
                    if (this.avatarBig == null) {
                        ImageLocation imageLocation3 = videoLocation;
                        thumbLocation = thumbLocation2;
                        imageLocation = imageLocation2;
                        this.avatarImage.setImage(videoLocation, filter2, thumbLocation2, "50_50", (Drawable) this.avatarDrawable, (Object) user);
                    } else {
                        thumbLocation = thumbLocation2;
                        imageLocation = imageLocation2;
                    }
                    if (!(thumbLocation == null || this.setAvatarRow == -1) || (thumbLocation == null && this.setAvatarRow == -1)) {
                        updateListAnimated(false);
                        needLayout(true);
                    }
                    if (imageLocation != null && (this.prevLoadedImageLocation == null || imageLocation.photoId != this.prevLoadedImageLocation.photoId)) {
                        this.prevLoadedImageLocation = imageLocation;
                        getFileLoader().loadFile(imageLocation, user, (String) null, 0, 1);
                    }
                    String newString3 = UserObject.getUserName(user);
                    if (user.id == getUserConfig().getClientUserId()) {
                        newString2 = LocaleController.getString("Online", NUM);
                    } else if (user.id == 333000 || user.id == 777000 || user.id == 42777) {
                        newString2 = LocaleController.getString("ServiceNotifications", NUM);
                    } else if (MessagesController.isSupportUser(user)) {
                        newString2 = LocaleController.getString("SupportStatus", NUM);
                    } else if (this.isBot) {
                        newString2 = LocaleController.getString("Bot", NUM);
                    } else {
                        this.isOnline[0] = false;
                        newString2 = LocaleController.formatUserStatus(this.currentAccount, user, this.isOnline);
                        SimpleTextView[] simpleTextViewArr = this.onlineTextView;
                        if (simpleTextViewArr[1] != null && !this.mediaHeaderVisible) {
                            String key = this.isOnline[0] ? "profile_status" : "avatar_subtitleInProfileBlue";
                            simpleTextViewArr[1].setTag(key);
                            if (!this.isPulledDown) {
                                this.onlineTextView[1].setTextColor(Theme.getColor(key));
                            }
                        }
                    }
                    int a = 0;
                    for (int i = 2; a < i; i = 2) {
                        if (this.nameTextView[a] == null) {
                            newString = newString3;
                        } else {
                            if (a != 0 || user.id == getUserConfig().getClientUserId() || user.id / 1000 == 777 || user.id / 1000 == 333 || user.phone == null || user.phone.length() == 0 || getContactsController().contactsDict.get(Long.valueOf(user.id)) != null || (getContactsController().contactsDict.size() == 0 && getContactsController().isLoadingContacts())) {
                                this.nameTextView[a].setText(newString3);
                            } else {
                                this.nameTextView[a].setText(PhoneFormat.getInstance().format("+" + user.phone));
                            }
                            if (a != 0 || onlineTextOverride == null) {
                                this.onlineTextView[a].setText(newString2);
                            } else {
                                this.onlineTextView[a].setText(onlineTextOverride);
                            }
                            Drawable leftIcon = this.currentEncryptedChat != null ? getLockIconDrawable() : null;
                            Drawable rightIcon = null;
                            if (a == 0) {
                                if (user.scam) {
                                    newString = newString3;
                                } else if (user.fake) {
                                    newString = newString3;
                                } else {
                                    MessagesController messagesController = getMessagesController();
                                    newString = newString3;
                                    long j = this.dialogId;
                                    if (j == 0) {
                                        j = this.userId;
                                    }
                                    rightIcon = messagesController.isDialogMuted(j) ? Theme.chat_muteIconDrawable : null;
                                }
                                rightIcon = getScamDrawable(user.scam ^ true ? 1 : 0);
                            } else {
                                newString = newString3;
                                if (user.scam || user.fake) {
                                    rightIcon = getScamDrawable(user.scam ^ true ? 1 : 0);
                                } else if (user.verified) {
                                    rightIcon = getVerifiedCrossfadeDrawable();
                                }
                            }
                            this.nameTextView[a].setLeftDrawable(leftIcon);
                            this.nameTextView[a].setRightDrawable(rightIcon);
                        }
                        a++;
                        newString3 = newString;
                    }
                    this.avatarImage.getImageReceiver().setVisible(!PhotoViewer.isShowingImage(photoBig), false);
                    int i2 = currentConnectionState2;
                    String str3 = onlineTextOverride;
                }
            } else if (this.chatId != 0) {
                TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(this.chatId));
                if (chat != null) {
                    this.currentChat = chat;
                } else {
                    chat = this.currentChat;
                }
                String str4 = "MegaLocation";
                if (ChatObject.isChannel(chat)) {
                    if (this.chatInfo == null || (!this.currentChat.megagroup && (this.chatInfo.participants_count == 0 || ChatObject.hasAdminRights(this.currentChat) || this.chatInfo.can_view_participants))) {
                        if (this.currentChat.megagroup) {
                            shortNumber = LocaleController.getString("Loading", NUM).toLowerCase();
                            profileStatusString = shortNumber;
                        } else if ((chat.flags & 64) != 0) {
                            shortNumber = LocaleController.getString("ChannelPublic", NUM).toLowerCase();
                            profileStatusString = shortNumber;
                        } else {
                            shortNumber = LocaleController.getString("ChannelPrivate", NUM).toLowerCase();
                            profileStatusString = shortNumber;
                        }
                    } else if (!this.currentChat.megagroup) {
                        String formatShortNumber = LocaleController.formatShortNumber(this.chatInfo.participants_count, new int[1]);
                        if (this.currentChat.megagroup) {
                            shortNumber = LocaleController.formatPluralString("Members", this.chatInfo.participants_count);
                            profileStatusString2 = LocaleController.formatPluralStringComma("Members", this.chatInfo.participants_count);
                        } else {
                            shortNumber = LocaleController.formatPluralString("Subscribers", this.chatInfo.participants_count);
                            profileStatusString2 = LocaleController.formatPluralStringComma("Subscribers", this.chatInfo.participants_count);
                        }
                        profileStatusString = profileStatusString2;
                    } else if (this.onlineCount > 1 && this.chatInfo.participants_count != 0) {
                        shortNumber = String.format("%s, %s", new Object[]{LocaleController.formatPluralString("Members", this.chatInfo.participants_count), LocaleController.formatPluralString("OnlineCount", Math.min(this.onlineCount, this.chatInfo.participants_count))});
                        profileStatusString = String.format("%s, %s", new Object[]{LocaleController.formatPluralStringComma("Members", this.chatInfo.participants_count), LocaleController.formatPluralStringComma("OnlineCount", Math.min(this.onlineCount, this.chatInfo.participants_count))});
                    } else if (this.chatInfo.participants_count != 0) {
                        shortNumber = LocaleController.formatPluralString("Members", this.chatInfo.participants_count);
                        profileStatusString = LocaleController.formatPluralStringComma("Members", this.chatInfo.participants_count);
                    } else if (chat.has_geo) {
                        shortNumber = LocaleController.getString(str4, NUM).toLowerCase();
                        profileStatusString = shortNumber;
                    } else if (!TextUtils.isEmpty(chat.username)) {
                        shortNumber = LocaleController.getString("MegaPublic", NUM).toLowerCase();
                        profileStatusString = shortNumber;
                    } else {
                        shortNumber = LocaleController.getString("MegaPrivate", NUM).toLowerCase();
                        profileStatusString = shortNumber;
                    }
                } else if (ChatObject.isKickedFromChat(chat)) {
                    shortNumber = LocaleController.getString("YouWereKicked", NUM);
                    profileStatusString = shortNumber;
                } else if (ChatObject.isLeftFromChat(chat)) {
                    shortNumber = LocaleController.getString("YouLeft", NUM);
                    profileStatusString = shortNumber;
                } else {
                    int count = chat.participants_count;
                    TLRPC.ChatFull chatFull2 = this.chatInfo;
                    if (chatFull2 != null) {
                        count = chatFull2.participants.participants.size();
                    }
                    if (count == 0 || this.onlineCount <= 1) {
                        String statusString2 = LocaleController.formatPluralString("Members", count);
                        shortNumber = statusString2;
                        profileStatusString = statusString2;
                    } else {
                        String profileStatusString3 = String.format("%s, %s", new Object[]{LocaleController.formatPluralString("Members", count), LocaleController.formatPluralString("OnlineCount", this.onlineCount)});
                        shortNumber = profileStatusString3;
                        profileStatusString = profileStatusString3;
                    }
                }
                int a2 = 0;
                boolean changed = false;
                while (a2 < 2) {
                    if (this.nameTextView[a2] == null) {
                        currentConnectionState = currentConnectionState2;
                        statusString = shortNumber;
                        onlineTextOverride2 = onlineTextOverride;
                        str = str4;
                    } else {
                        if (chat.title != null && this.nameTextView[a2].setText(chat.title)) {
                            changed = true;
                        }
                        this.nameTextView[a2].setLeftDrawable((Drawable) null);
                        if (a2 != 0) {
                            if (!chat.scam) {
                                if (!chat.fake) {
                                    if (chat.verified) {
                                        this.nameTextView[a2].setRightDrawable(getVerifiedCrossfadeDrawable());
                                        str2 = str4;
                                    } else {
                                        this.nameTextView[a2].setRightDrawable((Drawable) null);
                                        str2 = str4;
                                    }
                                }
                            }
                            this.nameTextView[a2].setRightDrawable(getScamDrawable(chat.scam ^ true ? 1 : 0));
                            str2 = str4;
                        } else {
                            if (chat.scam) {
                                str2 = str4;
                            } else if (chat.fake) {
                                str2 = str4;
                            } else {
                                str2 = str4;
                                this.nameTextView[a2].setRightDrawable(getMessagesController().isDialogMuted(-this.chatId) ? Theme.chat_muteIconDrawable : null);
                            }
                            this.nameTextView[a2].setRightDrawable(getScamDrawable(chat.scam ^ true ? 1 : 0));
                        }
                        if (a2 == 0 && onlineTextOverride != null) {
                            this.onlineTextView[a2].setText(onlineTextOverride);
                            currentConnectionState = currentConnectionState2;
                            statusString = shortNumber;
                            onlineTextOverride2 = onlineTextOverride;
                            str = str2;
                        } else if (!this.currentChat.megagroup || this.chatInfo == null || this.onlineCount <= 0) {
                            if (a2 != 0 || !ChatObject.isChannel(this.currentChat) || (chatFull = this.chatInfo) == null || chatFull.participants_count == 0) {
                                currentConnectionState = currentConnectionState2;
                                statusString = shortNumber;
                                onlineTextOverride2 = onlineTextOverride;
                                str = str2;
                            } else if (this.currentChat.megagroup || this.currentChat.broadcast) {
                                int[] result = new int[1];
                                String shortNumber2 = LocaleController.formatShortNumber(this.chatInfo.participants_count, result);
                                if (!this.currentChat.megagroup) {
                                    currentConnectionState = currentConnectionState2;
                                    statusString = shortNumber;
                                    onlineTextOverride2 = onlineTextOverride;
                                    str = str2;
                                    this.onlineTextView[a2].setText(LocaleController.formatPluralString("Subscribers", result[0]).replace(String.format("%d", new Object[]{Integer.valueOf(result[0])}), shortNumber2));
                                } else if (this.chatInfo.participants_count != 0) {
                                    str = str2;
                                    currentConnectionState = currentConnectionState2;
                                    statusString = shortNumber;
                                    onlineTextOverride2 = onlineTextOverride;
                                    this.onlineTextView[a2].setText(LocaleController.formatPluralString("Members", result[0]).replace(String.format("%d", new Object[]{Integer.valueOf(result[0])}), shortNumber2));
                                } else if (chat.has_geo) {
                                    str = str2;
                                    this.onlineTextView[a2].setText(LocaleController.getString(str, NUM).toLowerCase());
                                    currentConnectionState = currentConnectionState2;
                                    statusString = shortNumber;
                                    onlineTextOverride2 = onlineTextOverride;
                                } else {
                                    str = str2;
                                    if (!TextUtils.isEmpty(chat.username)) {
                                        this.onlineTextView[a2].setText(LocaleController.getString("MegaPublic", NUM).toLowerCase());
                                        currentConnectionState = currentConnectionState2;
                                        statusString = shortNumber;
                                        onlineTextOverride2 = onlineTextOverride;
                                    } else {
                                        this.onlineTextView[a2].setText(LocaleController.getString("MegaPrivate", NUM).toLowerCase());
                                        currentConnectionState = currentConnectionState2;
                                        statusString = shortNumber;
                                        onlineTextOverride2 = onlineTextOverride;
                                    }
                                }
                            } else {
                                currentConnectionState = currentConnectionState2;
                                statusString = shortNumber;
                                onlineTextOverride2 = onlineTextOverride;
                                str = str2;
                            }
                            this.onlineTextView[a2].setText(a2 == 0 ? statusString : profileStatusString);
                        } else {
                            this.onlineTextView[a2].setText(a2 == 0 ? shortNumber : profileStatusString);
                            currentConnectionState = currentConnectionState2;
                            statusString = shortNumber;
                            onlineTextOverride2 = onlineTextOverride;
                            str = str2;
                        }
                    }
                    a2++;
                    str4 = str;
                    currentConnectionState2 = currentConnectionState;
                    shortNumber = statusString;
                    onlineTextOverride = onlineTextOverride2;
                }
                String str5 = shortNumber;
                String str6 = onlineTextOverride;
                if (changed) {
                    needLayout(true);
                }
                TLRPC.FileLocation photoBig2 = null;
                if (chat.photo != null) {
                    photoBig2 = chat.photo.photo_big;
                }
                this.avatarDrawable.setInfo(chat);
                ImageLocation imageLocation4 = ImageLocation.getForUserOrChat(chat, 0);
                ImageLocation thumbLocation3 = ImageLocation.getForUserOrChat(chat, 1);
                ImageLocation videoLocation2 = this.avatarsViewPager.getCurrentVideoLocation(thumbLocation3, imageLocation4);
                boolean initied = this.avatarsViewPager.initIfEmpty(imageLocation4, thumbLocation3);
                if ((imageLocation4 == null || initied) && this.isPulledDown && (view = this.layoutManager.findViewByPosition(0)) != null) {
                    this.listView.smoothScrollBy(0, view.getTop() - AndroidUtilities.dp(88.0f), CubicBezierInterpolator.EASE_OUT_QUINT);
                }
                if (videoLocation2 == null || videoLocation2.imageType != 2) {
                    filter = null;
                } else {
                    filter = "g";
                }
                if (this.avatarBig == null) {
                    this.avatarImage.setImage(videoLocation2, filter, thumbLocation3, "50_50", (Drawable) this.avatarDrawable, (Object) chat);
                }
                if (imageLocation4 != null && (this.prevLoadedImageLocation == null || imageLocation4.photoId != this.prevLoadedImageLocation.photoId)) {
                    this.prevLoadedImageLocation = imageLocation4;
                    getFileLoader().loadFile(imageLocation4, chat, (String) null, 0, 1);
                }
                this.avatarImage.getImageReceiver().setVisible(!PhotoViewer.isShowingImage(photoBig2), false);
            } else {
                String str7 = onlineTextOverride;
            }
        }
    }

    private void createActionBarMenu(boolean animated) {
        String str;
        int i;
        String str2;
        int i2;
        if (this.actionBar != null && this.otherItem != null) {
            ActionBarMenu createMenu = this.actionBar.createMenu();
            this.otherItem.removeAllSubItems();
            this.animatingItem = null;
            this.editItemVisible = false;
            this.callItemVisible = false;
            this.videoCallItemVisible = false;
            this.canSearchMembers = false;
            boolean selfUser = false;
            if (this.userId != 0) {
                TLRPC.User user = getMessagesController().getUser(Long.valueOf(this.userId));
                if (user != null) {
                    if (UserObject.isUserSelf(user)) {
                        this.otherItem.addSubItem(30, NUM, (CharSequence) LocaleController.getString("EditName", NUM));
                        selfUser = true;
                    } else {
                        TLRPC.UserFull userFull = this.userInfo;
                        if (userFull != null && userFull.phone_calls_available) {
                            this.callItemVisible = true;
                            this.videoCallItemVisible = Build.VERSION.SDK_INT >= 18 && this.userInfo.video_calls_available;
                        }
                        int i3 = NUM;
                        String str3 = "Unblock";
                        int i4 = NUM;
                        if (!this.isBot && getContactsController().contactsDict.get(Long.valueOf(this.userId)) != null) {
                            if (!TextUtils.isEmpty(user.phone)) {
                                this.otherItem.addSubItem(3, NUM, (CharSequence) LocaleController.getString("ShareContact", NUM));
                            }
                            ActionBarMenuItem actionBarMenuItem = this.otherItem;
                            if (!this.userBlocked) {
                                i3 = NUM;
                                str3 = "BlockContact";
                            }
                            actionBarMenuItem.addSubItem(2, NUM, (CharSequence) LocaleController.getString(str3, i3));
                            this.otherItem.addSubItem(4, NUM, (CharSequence) LocaleController.getString("EditContact", NUM));
                            this.otherItem.addSubItem(5, NUM, (CharSequence) LocaleController.getString("DeleteContact", NUM));
                        } else if (!MessagesController.isSupportUser(user)) {
                            if (this.isBot) {
                                if (!user.bot_nochats) {
                                    this.otherItem.addSubItem(9, NUM, (CharSequence) LocaleController.getString("BotInvite", NUM));
                                }
                                this.otherItem.addSubItem(10, NUM, (CharSequence) LocaleController.getString("BotShare", NUM));
                            } else {
                                this.otherItem.addSubItem(1, NUM, (CharSequence) LocaleController.getString("AddContact", NUM));
                            }
                            if (!TextUtils.isEmpty(user.phone)) {
                                this.otherItem.addSubItem(3, NUM, (CharSequence) LocaleController.getString("ShareContact", NUM));
                            }
                            if (this.isBot) {
                                ActionBarMenuItem actionBarMenuItem2 = this.otherItem;
                                boolean z = this.userBlocked;
                                if (z) {
                                    i4 = NUM;
                                }
                                if (!z) {
                                    i2 = NUM;
                                    str2 = "BotStop";
                                } else {
                                    i2 = NUM;
                                    str2 = "BotRestart";
                                }
                                actionBarMenuItem2.addSubItem(2, i4, (CharSequence) LocaleController.getString(str2, i2));
                            } else {
                                this.otherItem.addSubItem(2, NUM, (CharSequence) !this.userBlocked ? LocaleController.getString("BlockContact", NUM) : LocaleController.getString(str3, NUM));
                            }
                        } else if (this.userBlocked) {
                            this.otherItem.addSubItem(2, NUM, (CharSequence) LocaleController.getString(str3, NUM));
                        }
                        if (!UserObject.isDeleted(user) && !this.isBot && this.currentEncryptedChat == null && !this.userBlocked) {
                            long j = this.userId;
                            if (!(j == 333000 || j == 777000 || j == 42777)) {
                                this.otherItem.addSubItem(20, NUM, (CharSequence) LocaleController.getString("StartEncryptedChat", NUM));
                            }
                        }
                        this.otherItem.addSubItem(14, NUM, (CharSequence) LocaleController.getString("AddShortcut", NUM));
                    }
                } else {
                    return;
                }
            } else if (this.chatId != 0) {
                TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(this.chatId));
                this.hasVoiceChatItem = false;
                if (ChatObject.isChannel(chat)) {
                    if (ChatObject.hasAdminRights(chat) || (chat.megagroup && ChatObject.canChangeChatInfo(chat))) {
                        this.editItemVisible = true;
                    }
                    if (this.chatInfo != null) {
                        if (ChatObject.canManageCalls(chat) && this.chatInfo.call == null) {
                            ActionBarMenuItem actionBarMenuItem3 = this.otherItem;
                            if (!chat.megagroup || chat.gigagroup) {
                                i = NUM;
                                str = "StartVoipChannel";
                            } else {
                                i = NUM;
                                str = "StartVoipChat";
                            }
                            actionBarMenuItem3.addSubItem(15, NUM, (CharSequence) LocaleController.getString(str, i));
                            this.hasVoiceChatItem = true;
                        }
                        if (this.chatInfo.can_view_stats) {
                            this.otherItem.addSubItem(19, NUM, (CharSequence) LocaleController.getString("Statistics", NUM));
                        }
                        this.callItemVisible = getMessagesController().getGroupCall(this.chatId, false) != null;
                    }
                    if (chat.megagroup) {
                        this.canSearchMembers = true;
                        this.otherItem.addSubItem(17, NUM, (CharSequence) LocaleController.getString("SearchMembers", NUM));
                        if (!chat.creator && !chat.left && !chat.kicked) {
                            this.otherItem.addSubItem(7, NUM, (CharSequence) LocaleController.getString("LeaveMegaMenu", NUM));
                        }
                    } else {
                        if (!TextUtils.isEmpty(chat.username)) {
                            this.otherItem.addSubItem(10, NUM, (CharSequence) LocaleController.getString("BotShare", NUM));
                        }
                        TLRPC.ChatFull chatFull = this.chatInfo;
                        if (!(chatFull == null || chatFull.linked_chat_id == 0)) {
                            this.otherItem.addSubItem(22, NUM, (CharSequence) LocaleController.getString("ViewDiscussion", NUM));
                        }
                        if (!this.currentChat.creator && !this.currentChat.left && !this.currentChat.kicked) {
                            this.otherItem.addSubItem(7, NUM, (CharSequence) LocaleController.getString("LeaveChannelMenu", NUM));
                        }
                    }
                } else {
                    if (this.chatInfo != null) {
                        if (ChatObject.canManageCalls(chat) && this.chatInfo.call == null) {
                            this.otherItem.addSubItem(15, NUM, (CharSequence) LocaleController.getString("StartVoipChat", NUM));
                            this.hasVoiceChatItem = true;
                        }
                        this.callItemVisible = getMessagesController().getGroupCall(this.chatId, false) != null;
                    }
                    if (ChatObject.canChangeChatInfo(chat)) {
                        this.editItemVisible = true;
                    }
                    if (!ChatObject.isKickedFromChat(chat) && !ChatObject.isLeftFromChat(chat)) {
                        this.canSearchMembers = true;
                        this.otherItem.addSubItem(17, NUM, (CharSequence) LocaleController.getString("SearchMembers", NUM));
                    }
                    this.otherItem.addSubItem(7, NUM, (CharSequence) LocaleController.getString("DeleteAndExit", NUM));
                }
                this.otherItem.addSubItem(14, NUM, (CharSequence) LocaleController.getString("AddShortcut", NUM));
            }
            if (this.imageUpdater != null) {
                this.otherItem.addSubItem(36, NUM, (CharSequence) LocaleController.getString("AddPhoto", NUM));
                this.otherItem.addSubItem(33, NUM, (CharSequence) LocaleController.getString("SetAsMain", NUM));
                this.otherItem.addSubItem(21, NUM, (CharSequence) LocaleController.getString("SaveToGallery", NUM));
                this.otherItem.addSubItem(35, NUM, (CharSequence) LocaleController.getString("Delete", NUM));
            } else {
                this.otherItem.addSubItem(21, NUM, (CharSequence) LocaleController.getString("SaveToGallery", NUM));
            }
            if (getMessagesController().isChatNoForwards(this.currentChat)) {
                this.otherItem.hideSubItem(21);
            }
            if (selfUser) {
                this.otherItem.addSubItem(31, NUM, (CharSequence) LocaleController.getString("LogOut", NUM));
            }
            if (!this.isPulledDown) {
                this.otherItem.hideSubItem(21);
                this.otherItem.hideSubItem(33);
                this.otherItem.showSubItem(36);
                this.otherItem.hideSubItem(34);
                this.otherItem.hideSubItem(35);
            }
            if (!this.mediaHeaderVisible) {
                if (this.callItemVisible) {
                    if (this.callItem.getVisibility() != 0) {
                        this.callItem.setVisibility(0);
                        if (animated) {
                            this.callItem.setAlpha(0.0f);
                            this.callItem.animate().alpha(1.0f).setDuration(150).start();
                        }
                    }
                } else if (this.callItem.getVisibility() != 8) {
                    this.callItem.setVisibility(8);
                }
                if (this.videoCallItemVisible) {
                    if (this.videoCallItem.getVisibility() != 0) {
                        this.videoCallItem.setVisibility(0);
                        if (animated) {
                            this.videoCallItem.setAlpha(0.0f);
                            this.videoCallItem.animate().alpha(1.0f).setDuration(150).start();
                        }
                    }
                } else if (this.videoCallItem.getVisibility() != 8) {
                    this.videoCallItem.setVisibility(8);
                }
                if (this.editItemVisible) {
                    if (this.editItem.getVisibility() != 0) {
                        this.editItem.setVisibility(0);
                        if (animated) {
                            this.editItem.setAlpha(0.0f);
                            this.editItem.animate().alpha(1.0f).setDuration(150).start();
                        }
                    }
                } else if (this.editItem.getVisibility() != 8) {
                    this.editItem.setVisibility(8);
                }
            }
            PagerIndicatorView pagerIndicatorView = this.avatarsViewPagerIndicatorView;
            if (pagerIndicatorView != null && pagerIndicatorView.isIndicatorFullyVisible()) {
                if (this.editItemVisible) {
                    this.editItem.setVisibility(8);
                    this.editItem.animate().cancel();
                    this.editItem.setAlpha(1.0f);
                }
                if (this.callItemVisible) {
                    this.callItem.setVisibility(8);
                    this.callItem.animate().cancel();
                    this.callItem.setAlpha(1.0f);
                }
                if (this.videoCallItemVisible) {
                    this.videoCallItem.setVisibility(8);
                    this.videoCallItem.animate().cancel();
                    this.videoCallItem.setAlpha(1.0f);
                }
            }
            SharedMediaLayout sharedMediaLayout2 = this.sharedMediaLayout;
            if (sharedMediaLayout2 != null) {
                sharedMediaLayout2.getSearchItem().requestLayout();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDialogDismiss(Dialog dialog) {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            recyclerListView.invalidateViews();
        }
    }

    public void didSelectDialogs(DialogsActivity fragment, ArrayList<Long> dids, CharSequence message, boolean param) {
        long did = dids.get(0).longValue();
        Bundle args = new Bundle();
        args.putBoolean("scrollToTopOnResume", true);
        if (DialogObject.isEncryptedDialog(did)) {
            args.putInt("enc_id", DialogObject.getEncryptedChatId(did));
        } else if (DialogObject.isUserDialog(did)) {
            args.putLong("user_id", did);
        } else if (DialogObject.isChatDialog(did)) {
            args.putLong("chat_id", -did);
        }
        if (getMessagesController().checkCanOpenChat(args, fragment)) {
            getNotificationCenter().removeObserver(this, NotificationCenter.closeChats);
            getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
            presentFragment(new ChatActivity(args), true);
            removeSelfFromStack();
            getSendMessagesHelper().sendMessage(getMessagesController().getUser(Long.valueOf(this.userId)), did, (MessageObject) null, (MessageObject) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
        }
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        ImageUpdater imageUpdater2 = this.imageUpdater;
        if (imageUpdater2 != null) {
            imageUpdater2.onRequestPermissionsResultFragment(requestCode, permissions, grantResults);
        }
        if (requestCode == 101 || requestCode == 102) {
            TLRPC.User user = getMessagesController().getUser(Long.valueOf(this.userId));
            if (user != null) {
                boolean allGranted = true;
                int a = 0;
                while (true) {
                    if (a >= grantResults.length) {
                        break;
                    } else if (grantResults[a] != 0) {
                        allGranted = false;
                        break;
                    } else {
                        a++;
                    }
                }
                if (grantResults.length <= 0 || !allGranted) {
                    VoIPHelper.permissionDenied(getParentActivity(), (Runnable) null, requestCode);
                    return;
                }
                boolean z = requestCode == 102;
                TLRPC.UserFull userFull = this.userInfo;
                VoIPHelper.startCall(user, z, userFull != null && userFull.video_calls_available, getParentActivity(), this.userInfo, getAccountInstance());
            }
        } else if (requestCode == 103 && this.currentChat != null) {
            boolean allGranted2 = true;
            int a2 = 0;
            while (true) {
                if (a2 >= grantResults.length) {
                    break;
                } else if (grantResults[a2] != 0) {
                    allGranted2 = false;
                    break;
                } else {
                    a2++;
                }
            }
            if (grantResults.length <= 0 || !allGranted2) {
                VoIPHelper.permissionDenied(getParentActivity(), (Runnable) null, requestCode);
                return;
            }
            VoIPHelper.startCall(this.currentChat, (TLRPC.InputPeer) null, (String) null, getMessagesController().getGroupCall(this.chatId, false) == null, getParentActivity(), this, getAccountInstance());
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
    public Animator searchExpandTransition(final boolean enter) {
        if (enter) {
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
        fArr[1] = enter ? 0.0f : 1.0f;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(fArr);
        float offset = this.extraHeight;
        this.searchListView.setTranslationY(offset);
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
        valueAnimator.addUpdateListener(new ProfileActivity$$ExternalSyntheticLambda22(this, valueAnimator, offset, enter));
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                ProfileActivity.this.updateSearchViewState(enter);
                ProfileActivity.this.avatarContainer.setClickable(true);
                if (enter) {
                    ProfileActivity.this.searchItem.requestFocusOnSearchView();
                }
                ProfileActivity.this.needLayout(true);
                Animator unused = ProfileActivity.this.searchViewTransition = null;
                ProfileActivity.this.fragmentView.invalidate();
                if (enter) {
                    boolean unused2 = ProfileActivity.this.invalidateScroll = true;
                    ProfileActivity.this.saveScrollPosition();
                    AndroidUtilities.requestAdjustResize(ProfileActivity.this.getParentActivity(), ProfileActivity.this.classGuid);
                    ProfileActivity.this.emptyView.setPreventMoving(false);
                }
            }
        });
        if (!enter) {
            this.invalidateScroll = true;
            saveScrollPosition();
            AndroidUtilities.requestAdjustNothing(getParentActivity(), this.classGuid);
            this.emptyView.setPreventMoving(true);
        }
        valueAnimator.setDuration(220);
        valueAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.searchViewTransition = valueAnimator;
        return valueAnimator;
    }

    /* renamed from: lambda$searchExpandTransition$28$org-telegram-ui-ProfileActivity  reason: not valid java name */
    public /* synthetic */ void m3716lambda$searchExpandTransition$28$orgtelegramuiProfileActivity(ValueAnimator valueAnimator, float offset, boolean enter, ValueAnimator animation) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.searchTransitionProgress = floatValue;
        float progressHalf = (floatValue - 0.5f) / 0.5f;
        float progressHalfEnd = (0.5f - floatValue) / 0.5f;
        if (progressHalf < 0.0f) {
            progressHalf = 0.0f;
        }
        if (progressHalfEnd < 0.0f) {
            progressHalfEnd = 0.0f;
        }
        this.searchTransitionOffset = (int) ((-offset) * (1.0f - floatValue));
        this.searchListView.setTranslationY(floatValue * offset);
        this.emptyView.setTranslationY(this.searchTransitionProgress * offset);
        this.listView.setTranslationY((-offset) * (1.0f - this.searchTransitionProgress));
        this.listView.setScaleX(1.0f - ((1.0f - this.searchTransitionProgress) * 0.01f));
        this.listView.setScaleY(1.0f - ((1.0f - this.searchTransitionProgress) * 0.01f));
        this.listView.setAlpha(this.searchTransitionProgress);
        boolean z = true;
        needLayout(true);
        this.listView.setAlpha(progressHalf);
        this.searchListView.setAlpha(1.0f - this.searchTransitionProgress);
        this.searchListView.setScaleX((this.searchTransitionProgress * 0.05f) + 1.0f);
        this.searchListView.setScaleY((this.searchTransitionProgress * 0.05f) + 1.0f);
        this.emptyView.setAlpha(1.0f - progressHalf);
        this.avatarContainer.setAlpha(progressHalf);
        this.nameTextView[1].setAlpha(progressHalf);
        this.onlineTextView[1].setAlpha(progressHalf);
        this.searchItem.getSearchField().setAlpha(progressHalfEnd);
        if (enter && this.searchTransitionProgress < 0.7f) {
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
            z = false;
        }
        actionBar.onSearchFieldVisibilityChanged(z);
        ActionBarMenuItem actionBarMenuItem3 = this.otherItem;
        if (actionBarMenuItem3 != null) {
            actionBarMenuItem3.setAlpha(progressHalf);
        }
        this.searchItem.setAlpha(progressHalf);
        this.topView.invalidate();
        this.fragmentView.invalidate();
    }

    /* access modifiers changed from: private */
    public void updateSearchViewState(boolean enter) {
        int i = 0;
        int hide = enter ? 8 : 0;
        this.listView.setVisibility(hide);
        this.searchListView.setVisibility(enter ? 0 : 8);
        FrameLayout searchContainer = this.searchItem.getSearchContainer();
        if (!enter) {
            i = 8;
        }
        searchContainer.setVisibility(i);
        this.actionBar.onSearchFieldVisibilityChanged(enter);
        this.avatarContainer.setVisibility(hide);
        this.nameTextView[1].setVisibility(hide);
        this.onlineTextView[1].setVisibility(hide);
        ActionBarMenuItem actionBarMenuItem = this.otherItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.setAlpha(1.0f);
            this.otherItem.setVisibility(hide);
        }
        this.searchItem.setVisibility(hide);
        this.avatarContainer.setAlpha(1.0f);
        this.nameTextView[1].setAlpha(1.0f);
        this.onlineTextView[1].setAlpha(1.0f);
        this.searchItem.setAlpha(1.0f);
        this.listView.setAlpha(1.0f);
        this.searchListView.setAlpha(1.0f);
        this.emptyView.setAlpha(1.0f);
        if (enter) {
            this.searchListView.setEmptyView(this.emptyView);
        } else {
            this.emptyView.setVisibility(8);
        }
    }

    public void onUploadProgressChanged(float progress) {
        RadialProgressView radialProgressView = this.avatarProgressView;
        if (radialProgressView != null) {
            radialProgressView.setProgress(progress);
            this.avatarsViewPager.setUploadProgress(this.uploadingImageLocation, progress);
        }
    }

    public void didStartUpload(boolean isVideo) {
        RadialProgressView radialProgressView = this.avatarProgressView;
        if (radialProgressView != null) {
            radialProgressView.setProgress(0.0f);
        }
    }

    public void didUploadPhoto(TLRPC.InputFile photo, TLRPC.InputFile video, double videoStartTimestamp, String videoPath, TLRPC.PhotoSize bigSize, TLRPC.PhotoSize smallSize) {
        AndroidUtilities.runOnUIThread(new ProfileActivity$$ExternalSyntheticLambda10(this, photo, video, videoStartTimestamp, videoPath, smallSize, bigSize));
    }

    /* renamed from: lambda$didUploadPhoto$31$org-telegram-ui-ProfileActivity  reason: not valid java name */
    public /* synthetic */ void m3701lambda$didUploadPhoto$31$orgtelegramuiProfileActivity(TLRPC.InputFile photo, TLRPC.InputFile video, double videoStartTimestamp, String videoPath, TLRPC.PhotoSize smallSize, TLRPC.PhotoSize bigSize) {
        if (photo == null && video == null) {
            this.avatar = smallSize.location;
            this.avatarBig = bigSize.location;
            this.avatarImage.setImage(ImageLocation.getForLocal(this.avatar), "50_50", (Drawable) this.avatarDrawable, (Object) null);
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
            TLRPC.TL_photos_uploadProfilePhoto req = new TLRPC.TL_photos_uploadProfilePhoto();
            if (photo != null) {
                req.file = photo;
                req.flags = 1 | req.flags;
            }
            if (video != null) {
                req.video = video;
                req.flags |= 2;
                req.video_start_ts = videoStartTimestamp;
                req.flags |= 4;
            }
            getConnectionsManager().sendRequest(req, new ProfileActivity$$ExternalSyntheticLambda21(this, videoPath));
        }
        this.actionBar.createMenu().requestLayout();
    }

    /* renamed from: lambda$didUploadPhoto$30$org-telegram-ui-ProfileActivity  reason: not valid java name */
    public /* synthetic */ void m3700lambda$didUploadPhoto$30$orgtelegramuiProfileActivity(String videoPath, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new ProfileActivity$$ExternalSyntheticLambda12(this, error, response, videoPath));
    }

    /* renamed from: lambda$didUploadPhoto$29$org-telegram-ui-ProfileActivity  reason: not valid java name */
    public /* synthetic */ void m3699lambda$didUploadPhoto$29$orgtelegramuiProfileActivity(TLRPC.TL_error error, TLObject response, String videoPath) {
        String str = videoPath;
        this.avatarsViewPager.removeUploadingImage(this.uploadingImageLocation);
        if (error == null) {
            TLRPC.User user = getMessagesController().getUser(Long.valueOf(getUserConfig().getClientUserId()));
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
            TLRPC.TL_photos_photo photos_photo = (TLRPC.TL_photos_photo) response;
            ArrayList<TLRPC.PhotoSize> sizes = photos_photo.photo.sizes;
            TLRPC.PhotoSize small = FileLoader.getClosestPhotoSizeWithSize(sizes, 150);
            TLRPC.PhotoSize big = FileLoader.getClosestPhotoSizeWithSize(sizes, 800);
            TLRPC.VideoSize videoSize = photos_photo.photo.video_sizes.isEmpty() ? null : photos_photo.photo.video_sizes.get(0);
            user.photo = new TLRPC.TL_userProfilePhoto();
            user.photo.photo_id = photos_photo.photo.id;
            if (small != null) {
                user.photo.photo_small = small.location;
            }
            if (big != null) {
                user.photo.photo_big = big.location;
            }
            if (!(small == null || this.avatar == null)) {
                FileLoader.getPathToAttach(this.avatar, true).renameTo(FileLoader.getPathToAttach(small, true));
                user = user;
                ImageLoader.getInstance().replaceImageInCache(this.avatar.volume_id + "_" + this.avatar.local_id + "@50_50", small.location.volume_id + "_" + small.location.local_id + "@50_50", ImageLocation.getForUserOrChat(user, 1), false);
            }
            if (!(big == null || this.avatarBig == null)) {
                FileLoader.getPathToAttach(this.avatarBig, true).renameTo(FileLoader.getPathToAttach(big, true));
            }
            if (!(videoSize == null || str == null)) {
                new File(str).renameTo(FileLoader.getPathToAttach(videoSize, "mp4", true));
            }
            getMessagesStorage().clearUserPhotos(user.id);
            ArrayList<TLRPC.User> users = new ArrayList<>();
            users.add(user);
            getMessagesStorage().putUsersAndChats(users, (ArrayList<TLRPC.Chat>) null, false, true);
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

    private void showAvatarProgress(final boolean show, boolean animated) {
        if (this.avatarProgressView != null) {
            AnimatorSet animatorSet = this.avatarAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.avatarAnimation = null;
            }
            if (animated) {
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.avatarAnimation = animatorSet2;
                if (show) {
                    this.avatarProgressView.setVisibility(0);
                    this.avatarAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{1.0f})});
                } else {
                    animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{0.0f})});
                }
                this.avatarAnimation.setDuration(180);
                this.avatarAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (ProfileActivity.this.avatarAnimation != null && ProfileActivity.this.avatarProgressView != null) {
                            if (!show) {
                                ProfileActivity.this.avatarProgressView.setVisibility(4);
                            }
                            AnimatorSet unused = ProfileActivity.this.avatarAnimation = null;
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        AnimatorSet unused = ProfileActivity.this.avatarAnimation = null;
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
        ImageUpdater imageUpdater2 = this.imageUpdater;
        if (imageUpdater2 != null) {
            imageUpdater2.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void saveSelfArgs(Bundle args) {
        ImageUpdater imageUpdater2 = this.imageUpdater;
        if (imageUpdater2 != null && imageUpdater2.currentPicturePath != null) {
            args.putString("path", this.imageUpdater.currentPicturePath);
        }
    }

    public void restoreSelfArgs(Bundle args) {
        ImageUpdater imageUpdater2 = this.imageUpdater;
        if (imageUpdater2 != null) {
            imageUpdater2.currentPicturePath = args.getString("path");
        }
    }

    private void sendLogs(boolean last) {
        if (getParentActivity() != null) {
            AlertDialog progressDialog = new AlertDialog(getParentActivity(), 3);
            progressDialog.setCanCacnel(false);
            progressDialog.show();
            Utilities.globalQueue.postRunnable(new ProfileActivity$$ExternalSyntheticLambda15(this, last, progressDialog));
        }
    }

    /* renamed from: lambda$sendLogs$33$org-telegram-ui-ProfileActivity  reason: not valid java name */
    public /* synthetic */ void m3718lambda$sendLogs$33$orgtelegramuiProfileActivity(boolean last, AlertDialog progressDialog) {
        int count;
        try {
            File dir = new File(ApplicationLoader.applicationContext.getExternalFilesDir((String) null).getAbsolutePath() + "/logs");
            File zipFile = new File(dir, "logs.zip");
            if (zipFile.exists()) {
                zipFile.delete();
            }
            File[] files = dir.listFiles();
            boolean[] finished = new boolean[1];
            long currentDate = System.currentTimeMillis();
            BufferedInputStream origin = null;
            ZipOutputStream out = null;
            try {
                out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
                byte[] data = new byte[65536];
                int i = 0;
                while (true) {
                    int count2 = 0;
                    if (i >= files.length) {
                        break;
                    }
                    if (!last || currentDate - files[i].lastModified() <= 86400000) {
                        BufferedInputStream origin2 = new BufferedInputStream(new FileInputStream(files[i]), data.length);
                        out.putNextEntry(new ZipEntry(files[i].getName()));
                        while (true) {
                            int read = origin2.read(data, count2, data.length);
                            count = read;
                            if (read == -1) {
                                break;
                            }
                            out.write(data, 0, count);
                            count2 = 0;
                        }
                        origin2.close();
                        origin = null;
                    }
                    i++;
                }
                finished[0] = true;
                if (origin != null) {
                    origin.close();
                }
            } catch (Exception e) {
                try {
                    e.printStackTrace();
                    if (origin != null) {
                        origin.close();
                    }
                    if (out != null) {
                    }
                } catch (Exception e2) {
                    e = e2;
                    e.printStackTrace();
                } catch (Throwable th) {
                    AlertDialog alertDialog = progressDialog;
                    if (origin != null) {
                        origin.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                    throw th;
                }
            }
            out.close();
            AndroidUtilities.runOnUIThread(new ProfileActivity$$ExternalSyntheticLambda14(this, progressDialog, finished, zipFile));
        } catch (Exception e3) {
            e = e3;
            AlertDialog alertDialog2 = progressDialog;
            e.printStackTrace();
        }
    }

    /* renamed from: lambda$sendLogs$32$org-telegram-ui-ProfileActivity  reason: not valid java name */
    public /* synthetic */ void m3717lambda$sendLogs$32$orgtelegramuiProfileActivity(AlertDialog progressDialog, boolean[] finished, File zipFile) {
        Uri uri;
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
        }
        if (finished[0]) {
            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(getParentActivity(), "org.telegram.messenger.beta.provider", zipFile);
            } else {
                uri = Uri.fromFile(zipFile);
            }
            Intent i = new Intent("android.intent.action.SEND");
            if (Build.VERSION.SDK_INT >= 24) {
                i.addFlags(1);
            }
            i.setType("message/rfCLASSNAME");
            i.putExtra("android.intent.extra.EMAIL", "");
            i.putExtra("android.intent.extra.SUBJECT", "Logs from " + LocaleController.getInstance().formatterStats.format(System.currentTimeMillis()));
            i.putExtra("android.intent.extra.STREAM", uri);
            if (getParentActivity() != null) {
                try {
                    getParentActivity().startActivityForResult(Intent.createChooser(i, "Select email application."), 500);
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
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

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            int i = viewType;
            switch (i) {
                case 1:
                    view = new HeaderCell(this.mContext, 23);
                    break;
                case 2:
                    TextDetailCell textDetailCell = new TextDetailCell(this.mContext);
                    textDetailCell.setContentDescriptionValueFirst(true);
                    TextDetailCell textDetailCell2 = textDetailCell;
                    view = textDetailCell;
                    break;
                case 3:
                    view = new AboutLinkCell(this.mContext, ProfileActivity.this) {
                        /* access modifiers changed from: protected */
                        public void didPressUrl(String url) {
                            if (url.startsWith("@")) {
                                ProfileActivity.this.getMessagesController().openByUserName(url.substring(1), ProfileActivity.this, 0);
                            } else if (url.startsWith("#")) {
                                DialogsActivity fragment = new DialogsActivity((Bundle) null);
                                fragment.setSearchString(url);
                                ProfileActivity.this.presentFragment(fragment);
                            } else if (url.startsWith("/") && ProfileActivity.this.parentLayout.fragmentsStack.size() > 1) {
                                BaseFragment previousFragment = ProfileActivity.this.parentLayout.fragmentsStack.get(ProfileActivity.this.parentLayout.fragmentsStack.size() - 2);
                                if (previousFragment instanceof ChatActivity) {
                                    ProfileActivity.this.finishFragment();
                                    ((ChatActivity) previousFragment).chatActivityEnterView.setCommand((MessageObject) null, url, false, false);
                                }
                            }
                        }
                    };
                    break;
                case 4:
                    view = new TextCell(this.mContext);
                    break;
                case 5:
                    View dividerCell = new DividerCell(this.mContext);
                    dividerCell.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(4.0f), 0, 0);
                    view = dividerCell;
                    break;
                case 6:
                    view = new NotificationsCheckCell(this.mContext, 23, 70, false);
                    break;
                case 7:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 8:
                    view = new UserCell(this.mContext, ProfileActivity.this.addMemberRow == -1 ? 9 : 6, 0, true);
                    break;
                case 11:
                    view = new View(this.mContext) {
                        /* access modifiers changed from: protected */
                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), NUM));
                        }
                    };
                    break;
                case 12:
                    View r0 = new View(this.mContext) {
                        private int lastListViewHeight = 0;
                        private int lastPaddingHeight = 0;

                        /* access modifiers changed from: protected */
                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            if (this.lastListViewHeight != ProfileActivity.this.listView.getMeasuredHeight()) {
                                this.lastPaddingHeight = 0;
                            }
                            this.lastListViewHeight = ProfileActivity.this.listView.getMeasuredHeight();
                            int n = ProfileActivity.this.listView.getChildCount();
                            if (n == ProfileActivity.this.listAdapter.getItemCount()) {
                                int totalHeight = 0;
                                for (int i = 0; i < n; i++) {
                                    int p = ProfileActivity.this.listView.getChildAdapterPosition(ProfileActivity.this.listView.getChildAt(i));
                                    if (p >= 0 && p != ProfileActivity.this.bottomPaddingRow) {
                                        totalHeight += ProfileActivity.this.listView.getChildAt(i).getMeasuredHeight();
                                    }
                                }
                                int paddingHeight = ((ProfileActivity.this.fragmentView.getMeasuredHeight() - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.statusBarHeight) - totalHeight;
                                if (paddingHeight > AndroidUtilities.dp(88.0f)) {
                                    paddingHeight = 0;
                                }
                                if (paddingHeight <= 0) {
                                    paddingHeight = 0;
                                }
                                int measuredWidth = ProfileActivity.this.listView.getMeasuredWidth();
                                this.lastPaddingHeight = paddingHeight;
                                setMeasuredDimension(measuredWidth, paddingHeight);
                                return;
                            }
                            setMeasuredDimension(ProfileActivity.this.listView.getMeasuredWidth(), this.lastPaddingHeight);
                        }
                    };
                    r0.setBackground(new ColorDrawable(0));
                    view = r0;
                    break;
                case 13:
                    if (ProfileActivity.this.sharedMediaLayout.getParent() != null) {
                        ((ViewGroup) ProfileActivity.this.sharedMediaLayout.getParent()).removeView(ProfileActivity.this.sharedMediaLayout);
                    }
                    view = ProfileActivity.this.sharedMediaLayout;
                    break;
                case 15:
                    view = new SettingsSuggestionCell(this.mContext) {
                        /* access modifiers changed from: protected */
                        public void onYesClick(int type) {
                            ProfileActivity.this.getNotificationCenter().removeObserver(ProfileActivity.this, NotificationCenter.newSuggestionsAvailable);
                            ProfileActivity.this.getMessagesController().removeSuggestion(0, type == 0 ? "VALIDATE_PHONE_NUMBER" : "VALIDATE_PASSWORD");
                            ProfileActivity.this.getNotificationCenter().addObserver(ProfileActivity.this, NotificationCenter.newSuggestionsAvailable);
                            ProfileActivity profileActivity = ProfileActivity.this;
                            if (type == 0) {
                                int access$16900 = profileActivity.phoneSuggestionRow;
                            } else {
                                int access$17000 = profileActivity.passwordSuggestionRow;
                            }
                            ProfileActivity.this.updateListAnimated(false);
                        }

                        /* access modifiers changed from: protected */
                        public void onNoClick(int type) {
                            if (type == 0) {
                                ProfileActivity.this.presentFragment(new ActionIntroActivity(3));
                            } else {
                                ProfileActivity.this.presentFragment(new TwoStepVerificationSetupActivity(8, (TLRPC.TL_account_password) null));
                            }
                        }
                    };
                    break;
                default:
                    TextInfoPrivacyCell cell = new TextInfoPrivacyCell(this.mContext, 10);
                    cell.getTextView().setGravity(1);
                    cell.getTextView().setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
                    cell.getTextView().setMovementMethod((MovementMethod) null);
                    try {
                        PackageInfo pInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                        int code = pInfo.versionCode / 10;
                        String abi = "";
                        switch (pInfo.versionCode % 10) {
                            case 0:
                            case 9:
                                if (!BuildVars.isStandaloneApp()) {
                                    abi = "universal " + Build.CPU_ABI + " " + Build.CPU_ABI2;
                                    break;
                                } else {
                                    abi = "direct " + Build.CPU_ABI + " " + Build.CPU_ABI2;
                                    break;
                                }
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
                        cell.setText(LocaleController.formatString("TelegramVersion", NUM, String.format(Locale.US, "v%s (%d) %s", new Object[]{pInfo.versionName, Integer.valueOf(code), abi})));
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                    cell.getTextView().setPadding(0, AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(14.0f));
                    View view2 = cell;
                    view2.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    view = view2;
                    break;
            }
            if (i != 13) {
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            }
            return new RecyclerListView.Holder(view);
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            if (holder.itemView == ProfileActivity.this.sharedMediaLayout) {
                boolean unused = ProfileActivity.this.sharedMediaLayoutAttached = true;
            }
        }

        public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
            if (holder.itemView == ProfileActivity.this.sharedMediaLayout) {
                boolean unused = ProfileActivity.this.sharedMediaLayoutAttached = false;
            }
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String value;
            String value2;
            String text;
            String text2;
            String value3;
            long did;
            String val;
            String str;
            int i;
            TLRPC.ChatParticipant part;
            String role;
            String role2;
            RecyclerView.ViewHolder viewHolder = holder;
            int i2 = position;
            String str2 = null;
            boolean z = false;
            switch (holder.getItemViewType()) {
                case 1:
                    HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                    if (i2 == ProfileActivity.this.infoHeaderRow) {
                        if (!ChatObject.isChannel(ProfileActivity.this.currentChat) || ProfileActivity.this.currentChat.megagroup || ProfileActivity.this.channelInfoRow == -1) {
                            headerCell.setText(LocaleController.getString("Info", NUM));
                            return;
                        } else {
                            headerCell.setText(LocaleController.getString("ReportChatDescription", NUM));
                            return;
                        }
                    } else if (i2 == ProfileActivity.this.membersHeaderRow) {
                        headerCell.setText(LocaleController.getString("ChannelMembers", NUM));
                        return;
                    } else if (i2 == ProfileActivity.this.settingsSectionRow2) {
                        headerCell.setText(LocaleController.getString("SETTINGS", NUM));
                        return;
                    } else if (i2 == ProfileActivity.this.numberSectionRow) {
                        headerCell.setText(LocaleController.getString("Account", NUM));
                        return;
                    } else if (i2 == ProfileActivity.this.helpHeaderRow) {
                        headerCell.setText(LocaleController.getString("SettingsHelp", NUM));
                        return;
                    } else if (i2 == ProfileActivity.this.debugHeaderRow) {
                        headerCell.setText(LocaleController.getString("SettingsDebug", NUM));
                        return;
                    } else {
                        return;
                    }
                case 2:
                    TextDetailCell detailCell = (TextDetailCell) viewHolder.itemView;
                    if (i2 == ProfileActivity.this.phoneRow) {
                        TLRPC.User user = ProfileActivity.this.getMessagesController().getUser(Long.valueOf(ProfileActivity.this.userId));
                        if (!TextUtils.isEmpty(user.phone)) {
                            text2 = PhoneFormat.getInstance().format("+" + user.phone);
                        } else {
                            text2 = LocaleController.getString("PhoneHidden", NUM);
                        }
                        detailCell.setTextAndValue(text2, LocaleController.getString("PhoneMobile", NUM), false);
                        return;
                    } else if (i2 == ProfileActivity.this.usernameRow) {
                        if (ProfileActivity.this.userId != 0) {
                            TLRPC.User user2 = ProfileActivity.this.getMessagesController().getUser(Long.valueOf(ProfileActivity.this.userId));
                            if (user2 == null || TextUtils.isEmpty(user2.username)) {
                                text = "-";
                            } else {
                                text = "@" + user2.username;
                            }
                            detailCell.setTextAndValue(text, LocaleController.getString("Username", NUM), false);
                            return;
                        } else if (ProfileActivity.this.currentChat != null) {
                            TLRPC.Chat chat = ProfileActivity.this.getMessagesController().getChat(Long.valueOf(ProfileActivity.this.chatId));
                            detailCell.setTextAndValue(ProfileActivity.this.getMessagesController().linkPrefix + "/" + chat.username, LocaleController.getString("InviteLink", NUM), false);
                            return;
                        } else {
                            return;
                        }
                    } else if (i2 == ProfileActivity.this.locationRow) {
                        if (ProfileActivity.this.chatInfo != null && (ProfileActivity.this.chatInfo.location instanceof TLRPC.TL_channelLocation)) {
                            detailCell.setTextAndValue(((TLRPC.TL_channelLocation) ProfileActivity.this.chatInfo.location).address, LocaleController.getString("AttachLocation", NUM), false);
                            return;
                        }
                        return;
                    } else if (i2 == ProfileActivity.this.numberRow) {
                        TLRPC.User user3 = UserConfig.getInstance(ProfileActivity.this.currentAccount).getCurrentUser();
                        if (user3 == null || user3.phone == null || user3.phone.length() == 0) {
                            value2 = LocaleController.getString("NumberUnknown", NUM);
                        } else {
                            value2 = PhoneFormat.getInstance().format("+" + user3.phone);
                        }
                        detailCell.setTextAndValue(value2, LocaleController.getString("TapToChangePhone", NUM), true);
                        detailCell.setContentDescriptionValueFirst(false);
                        return;
                    } else if (i2 == ProfileActivity.this.setUsernameRow) {
                        TLRPC.User user4 = UserConfig.getInstance(ProfileActivity.this.currentAccount).getCurrentUser();
                        if (user4 == null || TextUtils.isEmpty(user4.username)) {
                            value = LocaleController.getString("UsernameEmpty", NUM);
                        } else {
                            value = "@" + user4.username;
                        }
                        detailCell.setTextAndValue(value, LocaleController.getString("Username", NUM), true);
                        detailCell.setContentDescriptionValueFirst(true);
                        return;
                    } else if (i2 != ProfileActivity.this.bioRow) {
                        return;
                    } else {
                        if (ProfileActivity.this.userInfo == null || !TextUtils.isEmpty(ProfileActivity.this.userInfo.about)) {
                            detailCell.setTextWithEmojiAndValue(ProfileActivity.this.userInfo == null ? LocaleController.getString("Loading", NUM) : ProfileActivity.this.userInfo.about, LocaleController.getString("UserBio", NUM), false);
                            detailCell.setContentDescriptionValueFirst(true);
                            ProfileActivity profileActivity = ProfileActivity.this;
                            if (profileActivity.userInfo != null) {
                                str2 = ProfileActivity.this.userInfo.about;
                            }
                            String unused = profileActivity.currentBio = str2;
                            return;
                        }
                        detailCell.setTextAndValue(LocaleController.getString("UserBio", NUM), LocaleController.getString("UserBioDetail", NUM), false);
                        detailCell.setContentDescriptionValueFirst(false);
                        String unused2 = ProfileActivity.this.currentBio = null;
                        return;
                    }
                case 3:
                    AboutLinkCell aboutLinkCell = (AboutLinkCell) viewHolder.itemView;
                    if (i2 == ProfileActivity.this.userInfoRow) {
                        aboutLinkCell.setTextAndValue(ProfileActivity.this.userInfo.about, LocaleController.getString("UserBio", NUM), ProfileActivity.this.isBot);
                        return;
                    } else if (i2 == ProfileActivity.this.channelInfoRow) {
                        String text3 = ProfileActivity.this.chatInfo.about;
                        while (text3.contains("\n\n\n")) {
                            text3 = text3.replace("\n\n\n", "\n\n");
                        }
                        if (ChatObject.isChannel(ProfileActivity.this.currentChat) && !ProfileActivity.this.currentChat.megagroup) {
                            z = true;
                        }
                        aboutLinkCell.setText(text3, z);
                        return;
                    } else {
                        return;
                    }
                case 4:
                    TextCell textCell = (TextCell) viewHolder.itemView;
                    textCell.setColors("windowBackgroundWhiteGrayIcon", "windowBackgroundWhiteBlackText");
                    textCell.setTag("windowBackgroundWhiteBlackText");
                    if (i2 == ProfileActivity.this.settingsTimerRow) {
                        TLRPC.EncryptedChat encryptedChat = ProfileActivity.this.getMessagesController().getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(ProfileActivity.this.dialogId)));
                        if (encryptedChat.ttl == 0) {
                            value3 = LocaleController.getString("ShortMessageLifetimeForever", NUM);
                        } else {
                            value3 = LocaleController.formatTTLString(encryptedChat.ttl);
                        }
                        textCell.setTextAndValue(LocaleController.getString("MessageLifetime", NUM), value3, false);
                        return;
                    } else if (i2 == ProfileActivity.this.unblockRow) {
                        textCell.setText(LocaleController.getString("Unblock", NUM), false);
                        textCell.setColors((String) null, "windowBackgroundWhiteRedText5");
                        return;
                    } else if (i2 == ProfileActivity.this.settingsKeyRow) {
                        IdenticonDrawable identiconDrawable = new IdenticonDrawable();
                        identiconDrawable.setEncryptedChat(ProfileActivity.this.getMessagesController().getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(ProfileActivity.this.dialogId))));
                        textCell.setTextAndValueDrawable(LocaleController.getString("EncryptionKey", NUM), identiconDrawable, false);
                        return;
                    } else if (i2 == ProfileActivity.this.joinRow) {
                        textCell.setColors((String) null, "windowBackgroundWhiteBlueText2");
                        if (ProfileActivity.this.currentChat.megagroup) {
                            textCell.setText(LocaleController.getString("ProfileJoinGroup", NUM), false);
                            return;
                        } else {
                            textCell.setText(LocaleController.getString("ProfileJoinChannel", NUM), false);
                            return;
                        }
                    } else if (i2 == ProfileActivity.this.subscribersRow) {
                        if (ProfileActivity.this.chatInfo != null) {
                            if (!ChatObject.isChannel(ProfileActivity.this.currentChat) || ProfileActivity.this.currentChat.megagroup) {
                                String string = LocaleController.getString("ChannelMembers", NUM);
                                String format = String.format("%d", new Object[]{Integer.valueOf(ProfileActivity.this.chatInfo.participants_count)});
                                if (i2 != ProfileActivity.this.membersSectionRow - 1) {
                                    z = true;
                                }
                                textCell.setTextAndValueAndIcon(string, format, NUM, z);
                                return;
                            }
                            String string2 = LocaleController.getString("ChannelSubscribers", NUM);
                            String format2 = String.format("%d", new Object[]{Integer.valueOf(ProfileActivity.this.chatInfo.participants_count)});
                            if (i2 != ProfileActivity.this.membersSectionRow - 1) {
                                z = true;
                            }
                            textCell.setTextAndValueAndIcon(string2, format2, NUM, z);
                            return;
                        } else if (!ChatObject.isChannel(ProfileActivity.this.currentChat) || ProfileActivity.this.currentChat.megagroup) {
                            String string3 = LocaleController.getString("ChannelMembers", NUM);
                            if (i2 != ProfileActivity.this.membersSectionRow - 1) {
                                z = true;
                            }
                            textCell.setTextAndIcon(string3, NUM, z);
                            return;
                        } else {
                            String string4 = LocaleController.getString("ChannelSubscribers", NUM);
                            if (i2 != ProfileActivity.this.membersSectionRow - 1) {
                                z = true;
                            }
                            textCell.setTextAndIcon(string4, NUM, z);
                            return;
                        }
                    } else if (i2 == ProfileActivity.this.subscribersRequestsRow) {
                        if (ProfileActivity.this.chatInfo != null) {
                            String string5 = LocaleController.getString("SubscribeRequests", NUM);
                            String format3 = String.format("%d", new Object[]{Integer.valueOf(ProfileActivity.this.chatInfo.requests_pending)});
                            if (i2 != ProfileActivity.this.membersSectionRow - 1) {
                                z = true;
                            }
                            textCell.setTextAndValueAndIcon(string5, format3, NUM, z);
                            return;
                        }
                        return;
                    } else if (i2 == ProfileActivity.this.administratorsRow) {
                        if (ProfileActivity.this.chatInfo != null) {
                            String string6 = LocaleController.getString("ChannelAdministrators", NUM);
                            String format4 = String.format("%d", new Object[]{Integer.valueOf(ProfileActivity.this.chatInfo.admins_count)});
                            if (i2 != ProfileActivity.this.membersSectionRow - 1) {
                                z = true;
                            }
                            textCell.setTextAndValueAndIcon(string6, format4, NUM, z);
                            return;
                        }
                        String string7 = LocaleController.getString("ChannelAdministrators", NUM);
                        if (i2 != ProfileActivity.this.membersSectionRow - 1) {
                            z = true;
                        }
                        textCell.setTextAndIcon(string7, NUM, z);
                        return;
                    } else if (i2 == ProfileActivity.this.blockedUsersRow) {
                        if (ProfileActivity.this.chatInfo != null) {
                            String string8 = LocaleController.getString("ChannelBlacklist", NUM);
                            String format5 = String.format("%d", new Object[]{Integer.valueOf(Math.max(ProfileActivity.this.chatInfo.banned_count, ProfileActivity.this.chatInfo.kicked_count))});
                            if (i2 != ProfileActivity.this.membersSectionRow - 1) {
                                z = true;
                            }
                            textCell.setTextAndValueAndIcon(string8, format5, NUM, z);
                            return;
                        }
                        String string9 = LocaleController.getString("ChannelBlacklist", NUM);
                        if (i2 != ProfileActivity.this.membersSectionRow - 1) {
                            z = true;
                        }
                        textCell.setTextAndIcon(string9, NUM, z);
                        return;
                    } else if (i2 == ProfileActivity.this.addMemberRow) {
                        textCell.setColors("windowBackgroundWhiteBlueIcon", "windowBackgroundWhiteBlueButton");
                        String string10 = LocaleController.getString("AddMember", NUM);
                        if (ProfileActivity.this.membersSectionRow == -1) {
                            z = true;
                        }
                        textCell.setTextAndIcon(string10, NUM, z);
                        return;
                    } else if (i2 == ProfileActivity.this.sendMessageRow) {
                        textCell.setText(LocaleController.getString("SendMessageLocation", NUM), true);
                        return;
                    } else if (i2 == ProfileActivity.this.reportRow) {
                        textCell.setText(LocaleController.getString("ReportUserLocation", NUM), false);
                        textCell.setColors((String) null, "windowBackgroundWhiteRedText5");
                        return;
                    } else if (i2 == ProfileActivity.this.languageRow) {
                        textCell.setTextAndIcon(LocaleController.getString("Language", NUM), NUM, false);
                        return;
                    } else if (i2 == ProfileActivity.this.notificationRow) {
                        textCell.setTextAndIcon(LocaleController.getString("NotificationsAndSounds", NUM), NUM, true);
                        return;
                    } else if (i2 == ProfileActivity.this.privacyRow) {
                        textCell.setTextAndIcon(LocaleController.getString("PrivacySettings", NUM), NUM, true);
                        return;
                    } else if (i2 == ProfileActivity.this.dataRow) {
                        textCell.setTextAndIcon(LocaleController.getString("DataSettings", NUM), NUM, true);
                        return;
                    } else if (i2 == ProfileActivity.this.chatRow) {
                        textCell.setTextAndIcon(LocaleController.getString("ChatSettings", NUM), NUM, true);
                        return;
                    } else if (i2 == ProfileActivity.this.filtersRow) {
                        textCell.setTextAndIcon(LocaleController.getString("Filters", NUM), NUM, true);
                        return;
                    } else if (i2 == ProfileActivity.this.questionRow) {
                        textCell.setTextAndIcon(LocaleController.getString("AskAQuestion", NUM), NUM, true);
                        return;
                    } else if (i2 == ProfileActivity.this.faqRow) {
                        textCell.setTextAndIcon(LocaleController.getString("TelegramFAQ", NUM), NUM, true);
                        return;
                    } else if (i2 == ProfileActivity.this.policyRow) {
                        textCell.setTextAndIcon(LocaleController.getString("PrivacyPolicy", NUM), NUM, false);
                        return;
                    } else if (i2 == ProfileActivity.this.sendLogsRow) {
                        textCell.setText(LocaleController.getString("DebugSendLogs", NUM), true);
                        return;
                    } else if (i2 == ProfileActivity.this.sendLastLogsRow) {
                        textCell.setText(LocaleController.getString("DebugSendLastLogs", NUM), true);
                        return;
                    } else if (i2 == ProfileActivity.this.clearLogsRow) {
                        String string11 = LocaleController.getString("DebugClearLogs", NUM);
                        if (ProfileActivity.this.switchBackendRow != -1) {
                            z = true;
                        }
                        textCell.setText(string11, z);
                        return;
                    } else if (i2 == ProfileActivity.this.switchBackendRow) {
                        textCell.setText("Switch Backend", false);
                        return;
                    } else if (i2 == ProfileActivity.this.devicesRow) {
                        textCell.setTextAndIcon(LocaleController.getString("Devices", NUM), NUM, true);
                        return;
                    } else if (i2 == ProfileActivity.this.setAvatarRow) {
                        textCell.setColors("windowBackgroundWhiteBlueIcon", "windowBackgroundWhiteBlueButton");
                        textCell.setTextAndIcon(LocaleController.getString("SetProfilePhoto", NUM), NUM, false);
                        return;
                    } else {
                        return;
                    }
                case 6:
                    NotificationsCheckCell checkCell = (NotificationsCheckCell) viewHolder.itemView;
                    if (i2 == ProfileActivity.this.notificationsRow) {
                        SharedPreferences preferences = MessagesController.getNotificationsSettings(ProfileActivity.this.currentAccount);
                        if (ProfileActivity.this.dialogId != 0) {
                            did = ProfileActivity.this.dialogId;
                        } else if (ProfileActivity.this.userId != 0) {
                            did = ProfileActivity.this.userId;
                        } else {
                            did = -ProfileActivity.this.chatId;
                        }
                        boolean enabled = false;
                        boolean custom = preferences.getBoolean("custom_" + did, false);
                        boolean hasOverride = preferences.contains("notify2_" + did);
                        int value4 = preferences.getInt("notify2_" + did, 0);
                        int delta = preferences.getInt("notifyuntil_" + did, 0);
                        if (value4 != 3 || delta == Integer.MAX_VALUE) {
                            if (value4 == 0) {
                                if (hasOverride) {
                                    enabled = true;
                                } else {
                                    enabled = ProfileActivity.this.getNotificationsController().isGlobalNotificationsEnabled(did);
                                }
                            } else if (value4 == 1) {
                                enabled = true;
                            }
                            if (!enabled || !custom) {
                                if (enabled) {
                                    i = NUM;
                                    str = "NotificationsOn";
                                } else {
                                    i = NUM;
                                    str = "NotificationsOff";
                                }
                                val = LocaleController.getString(str, i);
                            } else {
                                val = LocaleController.getString("NotificationsCustom", NUM);
                            }
                        } else {
                            int delta2 = delta - ProfileActivity.this.getConnectionsManager().getCurrentTime();
                            if (delta2 <= 0) {
                                if (custom) {
                                    val = LocaleController.getString("NotificationsCustom", NUM);
                                } else {
                                    val = LocaleController.getString("NotificationsOn", NUM);
                                }
                                enabled = true;
                                SharedPreferences sharedPreferences = preferences;
                            } else if (delta2 < 3600) {
                                SharedPreferences sharedPreferences2 = preferences;
                                val = LocaleController.formatString("WillUnmuteIn", NUM, LocaleController.formatPluralString("Minutes", delta2 / 60));
                            } else {
                                if (delta2 < 86400) {
                                    val = LocaleController.formatString("WillUnmuteIn", NUM, LocaleController.formatPluralString("Hours", (int) Math.ceil((double) ((((float) delta2) / 60.0f) / 60.0f))));
                                } else if (delta2 < 31536000) {
                                    val = LocaleController.formatString("WillUnmuteIn", NUM, LocaleController.formatPluralString("Days", (int) Math.ceil((double) (((((float) delta2) / 60.0f) / 60.0f) / 24.0f))));
                                } else {
                                    val = null;
                                }
                            }
                        }
                        if (val == null) {
                            val = LocaleController.getString("NotificationsOff", NUM);
                        }
                        checkCell.setTextAndValueAndCheck(LocaleController.getString("Notifications", NUM), val, enabled, false);
                        return;
                    }
                    return;
                case 7:
                    View sectionCell = viewHolder.itemView;
                    sectionCell.setTag(Integer.valueOf(position));
                    if ((i2 == ProfileActivity.this.infoSectionRow && ProfileActivity.this.lastSectionRow == -1 && ProfileActivity.this.secretSettingsSectionRow == -1 && ProfileActivity.this.sharedMediaRow == -1 && ProfileActivity.this.membersSectionRow == -1) || i2 == ProfileActivity.this.secretSettingsSectionRow || i2 == ProfileActivity.this.lastSectionRow || (i2 == ProfileActivity.this.membersSectionRow && ProfileActivity.this.lastSectionRow == -1 && ProfileActivity.this.sharedMediaRow == -1)) {
                        sectionCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else {
                        sectionCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    }
                case 8:
                    UserCell userCell = (UserCell) viewHolder.itemView;
                    try {
                        if (!ProfileActivity.this.visibleSortedUsers.isEmpty()) {
                            part = (TLRPC.ChatParticipant) ProfileActivity.this.visibleChatParticipants.get(((Integer) ProfileActivity.this.visibleSortedUsers.get(i2 - ProfileActivity.this.membersStartRow)).intValue());
                        } else {
                            part = (TLRPC.ChatParticipant) ProfileActivity.this.visibleChatParticipants.get(i2 - ProfileActivity.this.membersStartRow);
                        }
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                        part = null;
                    }
                    if (part != null) {
                        if (part instanceof TLRPC.TL_chatChannelParticipant) {
                            TLRPC.ChannelParticipant channelParticipant = ((TLRPC.TL_chatChannelParticipant) part).channelParticipant;
                            if (!TextUtils.isEmpty(channelParticipant.rank)) {
                                role2 = channelParticipant.rank;
                            } else if (channelParticipant instanceof TLRPC.TL_channelParticipantCreator) {
                                role2 = LocaleController.getString("ChannelCreator", NUM);
                            } else if (channelParticipant instanceof TLRPC.TL_channelParticipantAdmin) {
                                role2 = LocaleController.getString("ChannelAdmin", NUM);
                            } else {
                                role2 = null;
                            }
                            role = role2;
                        } else if (part instanceof TLRPC.TL_chatParticipantCreator) {
                            role = LocaleController.getString("ChannelCreator", NUM);
                        } else if (part instanceof TLRPC.TL_chatParticipantAdmin) {
                            role = LocaleController.getString("ChannelAdmin", NUM);
                        } else {
                            role = null;
                        }
                        userCell.setAdminRole(role);
                        userCell.setData(ProfileActivity.this.getMessagesController().getUser(Long.valueOf(part.user_id)), (CharSequence) null, (CharSequence) null, 0, i2 != ProfileActivity.this.membersEndRow - 1);
                        return;
                    }
                    return;
                case 12:
                    viewHolder.itemView.requestLayout();
                    return;
                case 15:
                    SettingsSuggestionCell suggestionCell = (SettingsSuggestionCell) viewHolder.itemView;
                    if (i2 == ProfileActivity.this.passwordSuggestionRow) {
                        z = true;
                    }
                    suggestionCell.setType(z ? 1 : 0);
                    return;
                default:
                    return;
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            if (ProfileActivity.this.notificationRow != -1) {
                int position = holder.getAdapterPosition();
                if (position == ProfileActivity.this.notificationRow || position == ProfileActivity.this.numberRow || position == ProfileActivity.this.privacyRow || position == ProfileActivity.this.languageRow || position == ProfileActivity.this.setUsernameRow || position == ProfileActivity.this.bioRow || position == ProfileActivity.this.versionRow || position == ProfileActivity.this.dataRow || position == ProfileActivity.this.chatRow || position == ProfileActivity.this.questionRow || position == ProfileActivity.this.devicesRow || position == ProfileActivity.this.filtersRow || position == ProfileActivity.this.faqRow || position == ProfileActivity.this.policyRow || position == ProfileActivity.this.sendLogsRow || position == ProfileActivity.this.sendLastLogsRow || position == ProfileActivity.this.clearLogsRow || position == ProfileActivity.this.switchBackendRow || position == ProfileActivity.this.setAvatarRow) {
                    return true;
                }
                return false;
            }
            if (holder.itemView instanceof UserCell) {
                Object object = ((UserCell) holder.itemView).getCurrentObject();
                if ((object instanceof TLRPC.User) && UserObject.isUserSelf((TLRPC.User) object)) {
                    return false;
                }
            }
            int type = holder.getItemViewType();
            if (type == 1 || type == 5 || type == 7 || type == 9 || type == 10 || type == 11 || type == 12 || type == 13) {
                return false;
            }
            return true;
        }

        public int getItemCount() {
            return ProfileActivity.this.rowCount;
        }

        public int getItemViewType(int position) {
            if (position == ProfileActivity.this.infoHeaderRow || position == ProfileActivity.this.membersHeaderRow || position == ProfileActivity.this.settingsSectionRow2 || position == ProfileActivity.this.numberSectionRow || position == ProfileActivity.this.helpHeaderRow || position == ProfileActivity.this.debugHeaderRow) {
                return 1;
            }
            if (position == ProfileActivity.this.phoneRow || position == ProfileActivity.this.usernameRow || position == ProfileActivity.this.locationRow || position == ProfileActivity.this.numberRow || position == ProfileActivity.this.setUsernameRow || position == ProfileActivity.this.bioRow) {
                return 2;
            }
            if (position == ProfileActivity.this.userInfoRow || position == ProfileActivity.this.channelInfoRow) {
                return 3;
            }
            if (position == ProfileActivity.this.settingsTimerRow || position == ProfileActivity.this.settingsKeyRow || position == ProfileActivity.this.reportRow || position == ProfileActivity.this.subscribersRow || position == ProfileActivity.this.subscribersRequestsRow || position == ProfileActivity.this.administratorsRow || position == ProfileActivity.this.blockedUsersRow || position == ProfileActivity.this.addMemberRow || position == ProfileActivity.this.joinRow || position == ProfileActivity.this.unblockRow || position == ProfileActivity.this.sendMessageRow || position == ProfileActivity.this.notificationRow || position == ProfileActivity.this.privacyRow || position == ProfileActivity.this.languageRow || position == ProfileActivity.this.dataRow || position == ProfileActivity.this.chatRow || position == ProfileActivity.this.questionRow || position == ProfileActivity.this.devicesRow || position == ProfileActivity.this.filtersRow || position == ProfileActivity.this.faqRow || position == ProfileActivity.this.policyRow || position == ProfileActivity.this.sendLogsRow || position == ProfileActivity.this.sendLastLogsRow || position == ProfileActivity.this.clearLogsRow || position == ProfileActivity.this.switchBackendRow || position == ProfileActivity.this.setAvatarRow) {
                return 4;
            }
            if (position == ProfileActivity.this.notificationsDividerRow) {
                return 5;
            }
            if (position == ProfileActivity.this.notificationsRow) {
                return 6;
            }
            if (position == ProfileActivity.this.infoSectionRow || position == ProfileActivity.this.lastSectionRow || position == ProfileActivity.this.membersSectionRow || position == ProfileActivity.this.secretSettingsSectionRow || position == ProfileActivity.this.settingsSectionRow || position == ProfileActivity.this.devicesSectionRow || position == ProfileActivity.this.helpSectionCell || position == ProfileActivity.this.setAvatarSectionRow || position == ProfileActivity.this.passwordSuggestionSectionRow || position == ProfileActivity.this.phoneSuggestionSectionRow) {
                return 7;
            }
            if (position >= ProfileActivity.this.membersStartRow && position < ProfileActivity.this.membersEndRow) {
                return 8;
            }
            if (position == ProfileActivity.this.emptyRow) {
                return 11;
            }
            if (position == ProfileActivity.this.bottomPaddingRow) {
                return 12;
            }
            if (position == ProfileActivity.this.sharedMediaRow) {
                return 13;
            }
            if (position == ProfileActivity.this.versionRow) {
                return 14;
            }
            if (position == ProfileActivity.this.passwordSuggestionRow || position == ProfileActivity.this.phoneSuggestionRow) {
                return 15;
            }
            return 0;
        }
    }

    private class SearchAdapter extends RecyclerListView.SelectionAdapter {
        /* access modifiers changed from: private */
        public ArrayList<MessagesController.FaqSearchResult> faqSearchArray;
        /* access modifiers changed from: private */
        public ArrayList<MessagesController.FaqSearchResult> faqSearchResults;
        /* access modifiers changed from: private */
        public TLRPC.WebPage faqWebPage;
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

            public SearchResult(SearchAdapter searchAdapter, int g, String search, int icon, Runnable open) {
                this(g, search, (String) null, (String) null, (String) null, icon, open);
            }

            public SearchResult(SearchAdapter searchAdapter, int g, String search, String pathArg1, int icon, Runnable open) {
                this(g, search, (String) null, pathArg1, (String) null, icon, open);
            }

            public SearchResult(SearchAdapter searchAdapter, int g, String search, String row, String pathArg1, int icon, Runnable open) {
                this(g, search, row, pathArg1, (String) null, icon, open);
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
                if ((obj instanceof SearchResult) && this.guid == ((SearchResult) obj).guid) {
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

            /* access modifiers changed from: private */
            public void open() {
                this.openRunnable.run();
                AndroidUtilities.scrollToFragmentRow(SearchAdapter.this.this$0.parentLayout, this.rowName);
            }
        }

        /* renamed from: lambda$new$0$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3735lambda$new$0$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new ChangeNameActivity());
        }

        /* renamed from: lambda$new$1$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3736lambda$new$1$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new ActionIntroActivity(3));
        }

        /* renamed from: lambda$new$2$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3747lambda$new$2$orgtelegramuiProfileActivity$SearchAdapter() {
            int freeAccount = -1;
            int a = 0;
            while (true) {
                if (a >= 3) {
                    break;
                } else if (!UserConfig.getInstance(a).isClientActivated()) {
                    freeAccount = a;
                    break;
                } else {
                    a++;
                }
            }
            if (freeAccount >= 0) {
                this.this$0.presentFragment(new LoginActivity(freeAccount));
            }
        }

        /* renamed from: lambda$new$3$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3758lambda$new$3$orgtelegramuiProfileActivity$SearchAdapter() {
            if (this.this$0.userInfo != null) {
                this.this$0.presentFragment(new ChangeBioActivity());
            }
        }

        /* renamed from: lambda$new$4$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3769lambda$new$4$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new NotificationsSettingsActivity());
        }

        /* renamed from: lambda$new$5$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3780lambda$new$5$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new NotificationsCustomSettingsActivity(1, new ArrayList(), true));
        }

        /* renamed from: lambda$new$6$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3791lambda$new$6$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new NotificationsCustomSettingsActivity(0, new ArrayList(), true));
        }

        /* renamed from: lambda$new$7$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3802lambda$new$7$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new NotificationsCustomSettingsActivity(2, new ArrayList(), true));
        }

        /* renamed from: lambda$new$8$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3813lambda$new$8$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new NotificationsSettingsActivity());
        }

        /* renamed from: lambda$new$9$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3818lambda$new$9$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new NotificationsSettingsActivity());
        }

        /* renamed from: lambda$new$10$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3737lambda$new$10$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new NotificationsSettingsActivity());
        }

        /* renamed from: lambda$new$11$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3738lambda$new$11$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new NotificationsSettingsActivity());
        }

        /* renamed from: lambda$new$12$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3739lambda$new$12$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new NotificationsSettingsActivity());
        }

        /* renamed from: lambda$new$13$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3740lambda$new$13$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new NotificationsSettingsActivity());
        }

        /* renamed from: lambda$new$14$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3741lambda$new$14$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacySettingsActivity());
        }

        /* renamed from: lambda$new$15$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3742lambda$new$15$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacyUsersActivity());
        }

        /* renamed from: lambda$new$16$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3743lambda$new$16$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacyControlActivity(6, true));
        }

        /* renamed from: lambda$new$17$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3744lambda$new$17$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacyControlActivity(0, true));
        }

        /* renamed from: lambda$new$18$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3745lambda$new$18$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacyControlActivity(4, true));
        }

        /* renamed from: lambda$new$19$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3746lambda$new$19$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacyControlActivity(5, true));
        }

        /* renamed from: lambda$new$20$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3748lambda$new$20$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacyControlActivity(3, true));
        }

        /* renamed from: lambda$new$21$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3749lambda$new$21$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacyControlActivity(2, true));
        }

        /* renamed from: lambda$new$22$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3750lambda$new$22$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacyControlActivity(1, true));
        }

        /* renamed from: lambda$new$23$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3751lambda$new$23$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new PasscodeActivity(SharedConfig.passcodeHash.length() > 0 ? 2 : 0));
        }

        /* renamed from: lambda$new$24$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3752lambda$new$24$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new TwoStepVerificationActivity());
        }

        /* renamed from: lambda$new$25$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3753lambda$new$25$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new SessionsActivity(0));
        }

        /* renamed from: lambda$new$26$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3754lambda$new$26$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacySettingsActivity());
        }

        /* renamed from: lambda$new$27$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3755lambda$new$27$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacySettingsActivity());
        }

        /* renamed from: lambda$new$28$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3756lambda$new$28$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacySettingsActivity());
        }

        /* renamed from: lambda$new$29$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3757lambda$new$29$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new SessionsActivity(1));
        }

        /* renamed from: lambda$new$30$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3759lambda$new$30$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacySettingsActivity());
        }

        /* renamed from: lambda$new$31$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3760lambda$new$31$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacySettingsActivity());
        }

        /* renamed from: lambda$new$32$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3761lambda$new$32$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacySettingsActivity());
        }

        /* renamed from: lambda$new$33$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3762lambda$new$33$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacySettingsActivity());
        }

        /* renamed from: lambda$new$34$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3763lambda$new$34$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new PrivacySettingsActivity());
        }

        /* renamed from: lambda$new$35$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3764lambda$new$35$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new SessionsActivity(0));
        }

        /* renamed from: lambda$new$36$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3765lambda$new$36$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        /* renamed from: lambda$new$37$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3766lambda$new$37$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        /* renamed from: lambda$new$38$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3767lambda$new$38$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new CacheControlActivity());
        }

        /* renamed from: lambda$new$39$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3768lambda$new$39$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new CacheControlActivity());
        }

        /* renamed from: lambda$new$40$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3770lambda$new$40$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new CacheControlActivity());
        }

        /* renamed from: lambda$new$41$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3771lambda$new$41$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new CacheControlActivity());
        }

        /* renamed from: lambda$new$42$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3772lambda$new$42$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataUsageActivity());
        }

        /* renamed from: lambda$new$43$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3773lambda$new$43$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        /* renamed from: lambda$new$44$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3774lambda$new$44$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataAutoDownloadActivity(0));
        }

        /* renamed from: lambda$new$45$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3775lambda$new$45$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataAutoDownloadActivity(1));
        }

        /* renamed from: lambda$new$46$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3776lambda$new$46$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataAutoDownloadActivity(2));
        }

        /* renamed from: lambda$new$47$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3777lambda$new$47$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        /* renamed from: lambda$new$48$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3778lambda$new$48$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        /* renamed from: lambda$new$49$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3779lambda$new$49$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        /* renamed from: lambda$new$50$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3781lambda$new$50$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        /* renamed from: lambda$new$51$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3782lambda$new$51$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        /* renamed from: lambda$new$52$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3783lambda$new$52$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        /* renamed from: lambda$new$53$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3784lambda$new$53$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        /* renamed from: lambda$new$54$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3785lambda$new$54$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        /* renamed from: lambda$new$55$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3786lambda$new$55$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        /* renamed from: lambda$new$56$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3787lambda$new$56$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new ProxyListActivity());
        }

        /* renamed from: lambda$new$57$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3788lambda$new$57$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new ProxyListActivity());
        }

        /* renamed from: lambda$new$58$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3789lambda$new$58$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new DataSettingsActivity());
        }

        /* renamed from: lambda$new$59$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3790lambda$new$59$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        /* renamed from: lambda$new$60$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3792lambda$new$60$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        /* renamed from: lambda$new$61$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3793lambda$new$61$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new WallpapersListActivity(0));
        }

        /* renamed from: lambda$new$62$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3794lambda$new$62$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new WallpapersListActivity(1));
        }

        /* renamed from: lambda$new$63$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3795lambda$new$63$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new WallpapersListActivity(0));
        }

        /* renamed from: lambda$new$64$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3796lambda$new$64$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new ThemeActivity(1));
        }

        /* renamed from: lambda$new$65$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3797lambda$new$65$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        /* renamed from: lambda$new$66$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3798lambda$new$66$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        /* renamed from: lambda$new$67$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3799lambda$new$67$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        /* renamed from: lambda$new$68$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3800lambda$new$68$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        /* renamed from: lambda$new$69$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3801lambda$new$69$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        /* renamed from: lambda$new$70$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3803lambda$new$70$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        /* renamed from: lambda$new$71$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3804lambda$new$71$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        /* renamed from: lambda$new$72$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3805lambda$new$72$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new ThemeActivity(0));
        }

        /* renamed from: lambda$new$73$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3806lambda$new$73$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new StickersActivity(0));
        }

        /* renamed from: lambda$new$74$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3807lambda$new$74$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new StickersActivity(0));
        }

        /* renamed from: lambda$new$75$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3808lambda$new$75$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new FeaturedStickersActivity());
        }

        /* renamed from: lambda$new$76$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3809lambda$new$76$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new StickersActivity(1));
        }

        /* renamed from: lambda$new$77$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3810lambda$new$77$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new ArchivedStickersActivity(0));
        }

        /* renamed from: lambda$new$78$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3811lambda$new$78$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new ArchivedStickersActivity(1));
        }

        /* renamed from: lambda$new$79$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3812lambda$new$79$orgtelegramuiProfileActivity$SearchAdapter() {
            this.this$0.presentFragment(new LanguageSelectActivity());
        }

        /* renamed from: lambda$new$80$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3814lambda$new$80$orgtelegramuiProfileActivity$SearchAdapter() {
            ProfileActivity profileActivity = this.this$0;
            profileActivity.showDialog(AlertsCreator.createSupportAlert(profileActivity));
        }

        /* renamed from: lambda$new$81$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3815lambda$new$81$orgtelegramuiProfileActivity$SearchAdapter() {
            Browser.openUrl((Context) this.this$0.getParentActivity(), LocaleController.getString("TelegramFaqUrl", NUM));
        }

        /* renamed from: lambda$new$82$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3816lambda$new$82$orgtelegramuiProfileActivity$SearchAdapter() {
            Browser.openUrl((Context) this.this$0.getParentActivity(), LocaleController.getString("PrivacyPolicyUrl", NUM));
        }

        public SearchAdapter(ProfileActivity profileActivity, Context context) {
            this.this$0 = profileActivity;
            SearchResult[] searchResultArr = new SearchResult[83];
            searchResultArr[0] = new SearchResult(this, 500, LocaleController.getString("EditName", NUM), 0, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda0(this));
            searchResultArr[1] = new SearchResult(this, 501, LocaleController.getString("ChangePhoneNumber", NUM), 0, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda11(this));
            searchResultArr[2] = new SearchResult(this, 502, LocaleController.getString("AddAnotherAccount", NUM), 0, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda3(this));
            searchResultArr[3] = new SearchResult(this, 503, LocaleController.getString("UserBio", NUM), 0, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda15(this));
            searchResultArr[4] = new SearchResult(this, 1, LocaleController.getString("NotificationsAndSounds", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda27(this));
            searchResultArr[5] = new SearchResult(this, 2, LocaleController.getString("NotificationsPrivateChats", NUM), LocaleController.getString("NotificationsAndSounds", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda39(this));
            searchResultArr[6] = new SearchResult(this, 3, LocaleController.getString("NotificationsGroups", NUM), LocaleController.getString("NotificationsAndSounds", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda51(this));
            searchResultArr[7] = new SearchResult(this, 4, LocaleController.getString("NotificationsChannels", NUM), LocaleController.getString("NotificationsAndSounds", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda63(this));
            searchResultArr[8] = new SearchResult(this, 5, LocaleController.getString("VoipNotificationSettings", NUM), "callsSectionRow", LocaleController.getString("NotificationsAndSounds", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda75(this));
            searchResultArr[9] = new SearchResult(this, 6, LocaleController.getString("BadgeNumber", NUM), "badgeNumberSection", LocaleController.getString("NotificationsAndSounds", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda80(this));
            searchResultArr[10] = new SearchResult(this, 7, LocaleController.getString("InAppNotifications", NUM), "inappSectionRow", LocaleController.getString("NotificationsAndSounds", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda22(this));
            searchResultArr[11] = new SearchResult(this, 8, LocaleController.getString("ContactJoined", NUM), "contactJoinedRow", LocaleController.getString("NotificationsAndSounds", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda33(this));
            searchResultArr[12] = new SearchResult(this, 9, LocaleController.getString("PinnedMessages", NUM), "pinnedMessageRow", LocaleController.getString("NotificationsAndSounds", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda44(this));
            searchResultArr[13] = new SearchResult(this, 10, LocaleController.getString("ResetAllNotifications", NUM), "resetNotificationsRow", LocaleController.getString("NotificationsAndSounds", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda55(this));
            searchResultArr[14] = new SearchResult(this, 100, LocaleController.getString("PrivacySettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda66(this));
            searchResultArr[15] = new SearchResult(this, 101, LocaleController.getString("BlockedUsers", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda77(this));
            searchResultArr[16] = new SearchResult(this, 105, LocaleController.getString("PrivacyPhone", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda86(this));
            searchResultArr[17] = new SearchResult(this, 102, LocaleController.getString("PrivacyLastSeen", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda87(this));
            searchResultArr[18] = new SearchResult(this, 103, LocaleController.getString("PrivacyProfilePhoto", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda1(this));
            searchResultArr[19] = new SearchResult(this, 104, LocaleController.getString("PrivacyForwards", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda2(this));
            searchResultArr[20] = new SearchResult(this, 122, LocaleController.getString("PrivacyP2P", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda4(this));
            searchResultArr[21] = new SearchResult(this, 106, LocaleController.getString("Calls", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda5(this));
            searchResultArr[22] = new SearchResult(this, 107, LocaleController.getString("GroupsAndChannels", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda6(this));
            searchResultArr[23] = new SearchResult(this, 108, LocaleController.getString("Passcode", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda7(this));
            searchResultArr[24] = new SearchResult(this, 109, LocaleController.getString("TwoStepVerification", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda8(this));
            searchResultArr[25] = new SearchResult(this, 110, LocaleController.getString("SessionsTitle", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda9(this));
            SearchResult searchResult = null;
            if (profileActivity.getMessagesController().autoarchiveAvailable) {
                searchResult = new SearchResult(this, 121, LocaleController.getString("ArchiveAndMute", NUM), "newChatsRow", LocaleController.getString("PrivacySettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda10(this));
            }
            searchResultArr[26] = searchResult;
            searchResultArr[27] = new SearchResult(this, 112, LocaleController.getString("DeleteAccountIfAwayFor2", NUM), "deleteAccountRow", LocaleController.getString("PrivacySettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda12(this));
            searchResultArr[28] = new SearchResult(this, 113, LocaleController.getString("PrivacyPaymentsClear", NUM), "paymentsClearRow", LocaleController.getString("PrivacySettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda13(this));
            searchResultArr[29] = new SearchResult(this, 114, LocaleController.getString("WebSessionsTitle", NUM), LocaleController.getString("PrivacySettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda14(this));
            searchResultArr[30] = new SearchResult(this, 115, LocaleController.getString("SyncContactsDelete", NUM), "contactsDeleteRow", LocaleController.getString("PrivacySettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda16(this));
            searchResultArr[31] = new SearchResult(this, 116, LocaleController.getString("SyncContacts", NUM), "contactsSyncRow", LocaleController.getString("PrivacySettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda17(this));
            searchResultArr[32] = new SearchResult(this, 117, LocaleController.getString("SuggestContacts", NUM), "contactsSuggestRow", LocaleController.getString("PrivacySettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda18(this));
            searchResultArr[33] = new SearchResult(this, 118, LocaleController.getString("MapPreviewProvider", NUM), "secretMapRow", LocaleController.getString("PrivacySettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda19(this));
            searchResultArr[34] = new SearchResult(this, 119, LocaleController.getString("SecretWebPage", NUM), "secretWebpageRow", LocaleController.getString("PrivacySettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda20(this));
            searchResultArr[35] = new SearchResult(this, 120, LocaleController.getString("Devices", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda21(this));
            searchResultArr[36] = new SearchResult(this, 200, LocaleController.getString("DataSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda23(this));
            searchResultArr[37] = new SearchResult(this, 201, LocaleController.getString("DataUsage", NUM), "usageSectionRow", LocaleController.getString("DataSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda24(this));
            searchResultArr[38] = new SearchResult(this, 202, LocaleController.getString("StorageUsage", NUM), LocaleController.getString("DataSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda25(this));
            String str = "StorageUsage";
            searchResultArr[39] = new SearchResult(203, LocaleController.getString("KeepMedia", NUM), "keepMediaRow", LocaleController.getString("DataSettings", NUM), LocaleController.getString("StorageUsage", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda26(this));
            searchResultArr[40] = new SearchResult(204, LocaleController.getString("ClearMediaCache", NUM), "cacheRow", LocaleController.getString("DataSettings", NUM), LocaleController.getString(str, NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda28(this));
            searchResultArr[41] = new SearchResult(205, LocaleController.getString("LocalDatabase", NUM), "databaseRow", LocaleController.getString("DataSettings", NUM), LocaleController.getString(str, NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda29(this));
            searchResultArr[42] = new SearchResult(this, 206, LocaleController.getString("NetworkUsage", NUM), LocaleController.getString("DataSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda30(this));
            searchResultArr[43] = new SearchResult(this, 207, LocaleController.getString("AutomaticMediaDownload", NUM), "mediaDownloadSectionRow", LocaleController.getString("DataSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda31(this));
            searchResultArr[44] = new SearchResult(this, 208, LocaleController.getString("WhenUsingMobileData", NUM), LocaleController.getString("DataSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda32(this));
            searchResultArr[45] = new SearchResult(this, 209, LocaleController.getString("WhenConnectedOnWiFi", NUM), LocaleController.getString("DataSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda34(this));
            searchResultArr[46] = new SearchResult(this, 210, LocaleController.getString("WhenRoaming", NUM), LocaleController.getString("DataSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda35(this));
            searchResultArr[47] = new SearchResult(this, 211, LocaleController.getString("ResetAutomaticMediaDownload", NUM), "resetDownloadRow", LocaleController.getString("DataSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda36(this));
            searchResultArr[48] = new SearchResult(this, 212, LocaleController.getString("AutoplayMedia", NUM), "autoplayHeaderRow", LocaleController.getString("DataSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda37(this));
            searchResultArr[49] = new SearchResult(this, 213, LocaleController.getString("AutoplayGIF", NUM), "autoplayGifsRow", LocaleController.getString("DataSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda38(this));
            searchResultArr[50] = new SearchResult(this, 214, LocaleController.getString("AutoplayVideo", NUM), "autoplayVideoRow", LocaleController.getString("DataSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda40(this));
            searchResultArr[51] = new SearchResult(this, 215, LocaleController.getString("Streaming", NUM), "streamSectionRow", LocaleController.getString("DataSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda41(this));
            searchResultArr[52] = new SearchResult(this, 216, LocaleController.getString("EnableStreaming", NUM), "enableStreamRow", LocaleController.getString("DataSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda42(this));
            searchResultArr[53] = new SearchResult(this, 217, LocaleController.getString("Calls", NUM), "callsSectionRow", LocaleController.getString("DataSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda43(this));
            searchResultArr[54] = new SearchResult(this, 218, LocaleController.getString("VoipUseLessData", NUM), "useLessDataForCallsRow", LocaleController.getString("DataSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda45(this));
            searchResultArr[55] = new SearchResult(this, 219, LocaleController.getString("VoipQuickReplies", NUM), "quickRepliesRow", LocaleController.getString("DataSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda46(this));
            searchResultArr[56] = new SearchResult(this, 220, LocaleController.getString("ProxySettings", NUM), LocaleController.getString("DataSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda47(this));
            searchResultArr[57] = new SearchResult(221, LocaleController.getString("UseProxyForCalls", NUM), "callsRow", LocaleController.getString("DataSettings", NUM), LocaleController.getString("ProxySettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda48(this));
            searchResultArr[58] = new SearchResult(this, 111, LocaleController.getString("PrivacyDeleteCloudDrafts", NUM), "clearDraftsRow", LocaleController.getString("DataSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda49(this));
            searchResultArr[59] = new SearchResult(this, 300, LocaleController.getString("ChatSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda50(this));
            searchResultArr[60] = new SearchResult(this, 301, LocaleController.getString("TextSizeHeader", NUM), "textSizeHeaderRow", LocaleController.getString("ChatSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda52(this));
            searchResultArr[61] = new SearchResult(this, 302, LocaleController.getString("ChatBackground", NUM), LocaleController.getString("ChatSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda53(this));
            searchResultArr[62] = new SearchResult(303, LocaleController.getString("SetColor", NUM), (String) null, LocaleController.getString("ChatSettings", NUM), LocaleController.getString("ChatBackground", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda54(this));
            searchResultArr[63] = new SearchResult(304, LocaleController.getString("ResetChatBackgrounds", NUM), "resetRow", LocaleController.getString("ChatSettings", NUM), LocaleController.getString("ChatBackground", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda56(this));
            searchResultArr[64] = new SearchResult(this, 305, LocaleController.getString("AutoNightTheme", NUM), LocaleController.getString("ChatSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda57(this));
            searchResultArr[65] = new SearchResult(this, 306, LocaleController.getString("ColorTheme", NUM), "themeHeaderRow", LocaleController.getString("ChatSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda58(this));
            searchResultArr[66] = new SearchResult(this, 307, LocaleController.getString("ChromeCustomTabs", NUM), "customTabsRow", LocaleController.getString("ChatSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda59(this));
            searchResultArr[67] = new SearchResult(this, 308, LocaleController.getString("DirectShare", NUM), "directShareRow", LocaleController.getString("ChatSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda60(this));
            searchResultArr[68] = new SearchResult(this, 309, LocaleController.getString("EnableAnimations", NUM), "enableAnimationsRow", LocaleController.getString("ChatSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda61(this));
            searchResultArr[69] = new SearchResult(this, 310, LocaleController.getString("RaiseToSpeak", NUM), "raiseToSpeakRow", LocaleController.getString("ChatSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda62(this));
            searchResultArr[70] = new SearchResult(this, 311, LocaleController.getString("SendByEnter", NUM), "sendByEnterRow", LocaleController.getString("ChatSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda64(this));
            searchResultArr[71] = new SearchResult(this, 312, LocaleController.getString("SaveToGallerySettings", NUM), "saveToGalleryRow", LocaleController.getString("ChatSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda65(this));
            searchResultArr[72] = new SearchResult(this, 318, LocaleController.getString("DistanceUnits", NUM), "distanceRow", LocaleController.getString("ChatSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda67(this));
            searchResultArr[73] = new SearchResult(this, 313, LocaleController.getString("StickersAndMasks", NUM), LocaleController.getString("ChatSettings", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda68(this));
            searchResultArr[74] = new SearchResult(314, LocaleController.getString("SuggestStickers", NUM), "suggestRow", LocaleController.getString("ChatSettings", NUM), LocaleController.getString("StickersAndMasks", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda69(this));
            searchResultArr[75] = new SearchResult(315, LocaleController.getString("FeaturedStickers", NUM), (String) null, LocaleController.getString("ChatSettings", NUM), LocaleController.getString("StickersAndMasks", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda70(this));
            searchResultArr[76] = new SearchResult(316, LocaleController.getString("Masks", NUM), (String) null, LocaleController.getString("ChatSettings", NUM), LocaleController.getString("StickersAndMasks", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda71(this));
            searchResultArr[77] = new SearchResult(317, LocaleController.getString("ArchivedStickers", NUM), (String) null, LocaleController.getString("ChatSettings", NUM), LocaleController.getString("StickersAndMasks", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda72(this));
            searchResultArr[78] = new SearchResult(317, LocaleController.getString("ArchivedMasks", NUM), (String) null, LocaleController.getString("ChatSettings", NUM), LocaleController.getString("StickersAndMasks", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda73(this));
            searchResultArr[79] = new SearchResult(this, 400, LocaleController.getString("Language", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda74(this));
            searchResultArr[80] = new SearchResult(this, 402, LocaleController.getString("AskAQuestion", NUM), LocaleController.getString("SettingsHelp", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda76(this));
            searchResultArr[81] = new SearchResult(this, 403, LocaleController.getString("TelegramFAQ", NUM), LocaleController.getString("SettingsHelp", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda78(this));
            searchResultArr[82] = new SearchResult(this, 404, LocaleController.getString("PrivacyPolicy", NUM), LocaleController.getString("SettingsHelp", NUM), NUM, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda79(this));
            this.searchArray = searchResultArr;
            this.faqSearchArray = new ArrayList<>();
            this.resultNames = new ArrayList<>();
            this.searchResults = new ArrayList<>();
            this.faqSearchResults = new ArrayList<>();
            this.recentSearches = new ArrayList<>();
            this.mContext = context;
            HashMap hashMap = new HashMap();
            int a = 0;
            while (true) {
                SearchResult[] searchResultArr2 = this.searchArray;
                if (a >= searchResultArr2.length) {
                    break;
                }
                if (searchResultArr2[a] != null) {
                    hashMap.put(Integer.valueOf(searchResultArr2[a].guid), this.searchArray[a]);
                }
                a++;
            }
            Set<String> set = MessagesController.getGlobalMainSettings().getStringSet("settingsSearchRecent2", (Set) null);
            if (set != null) {
                for (String value : set) {
                    try {
                        SerializedData data = new SerializedData(Utilities.hexToBytes(value));
                        int num = data.readInt32(false);
                        int type = data.readInt32(false);
                        if (type == 0) {
                            String title = data.readString(false);
                            int count = data.readInt32(false);
                            String[] path = null;
                            if (count > 0) {
                                path = new String[count];
                                for (int a2 = 0; a2 < count; a2++) {
                                    path[a2] = data.readString(false);
                                }
                            }
                            MessagesController.FaqSearchResult result = new MessagesController.FaqSearchResult(title, path, data.readString(false));
                            result.num = num;
                            this.recentSearches.add(result);
                        } else if (type == 1) {
                            try {
                                SearchResult result2 = (SearchResult) hashMap.get(Integer.valueOf(data.readInt32(false)));
                                if (result2 != null) {
                                    int unused = result2.num = num;
                                    this.recentSearches.add(result2);
                                }
                            } catch (Exception e) {
                            }
                        }
                    } catch (Exception e2) {
                    }
                }
            }
            Collections.sort(this.recentSearches, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda84(this));
        }

        /* renamed from: lambda$new$83$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ int m3817lambda$new$83$orgtelegramuiProfileActivity$SearchAdapter(Object o1, Object o2) {
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

        /* access modifiers changed from: private */
        public void loadFaqWebPage() {
            TLRPC.WebPage webPage = this.this$0.getMessagesController().faqWebPage;
            this.faqWebPage = webPage;
            if (webPage != null) {
                this.faqSearchArray.addAll(this.this$0.getMessagesController().faqSearchArray);
            }
            if (this.faqWebPage == null && !this.loadingFaqPage) {
                this.loadingFaqPage = true;
                TLRPC.TL_messages_getWebPage req2 = new TLRPC.TL_messages_getWebPage();
                req2.url = LocaleController.getString("TelegramFaqUrl", NUM);
                req2.hash = 0;
                this.this$0.getConnectionsManager().sendRequest(req2, new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda85(this));
            }
        }

        /* renamed from: lambda$loadFaqWebPage$85$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3734xd152d752(TLObject response2, TLRPC.TL_error error2) {
            int N;
            int N2;
            String[] path;
            TLObject tLObject = response2;
            if (tLObject instanceof TLRPC.WebPage) {
                ArrayList<MessagesController.FaqSearchResult> arrayList = new ArrayList<>();
                TLRPC.WebPage page = (TLRPC.WebPage) tLObject;
                if (page.cached_page != null) {
                    int a = 0;
                    int N3 = page.cached_page.blocks.size();
                    while (true) {
                        if (a >= N3) {
                            break;
                        }
                        TLRPC.PageBlock block = page.cached_page.blocks.get(a);
                        if (block instanceof TLRPC.TL_pageBlockList) {
                            String paragraph = null;
                            if (a != 0) {
                                TLRPC.PageBlock prevBlock = page.cached_page.blocks.get(a - 1);
                                if (prevBlock instanceof TLRPC.TL_pageBlockParagraph) {
                                    paragraph = ArticleViewer.getPlainText(((TLRPC.TL_pageBlockParagraph) prevBlock).text).toString();
                                }
                            }
                            TLRPC.TL_pageBlockList list = (TLRPC.TL_pageBlockList) block;
                            int b = 0;
                            int N22 = list.items.size();
                            while (b < N22) {
                                TLRPC.PageListItem item = list.items.get(b);
                                if (item instanceof TLRPC.TL_pageListItemText) {
                                    TLRPC.TL_pageListItemText itemText = (TLRPC.TL_pageListItemText) item;
                                    String url = ArticleViewer.getUrl(itemText.text);
                                    String text = ArticleViewer.getPlainText(itemText.text).toString();
                                    if (TextUtils.isEmpty(url)) {
                                        N2 = N3;
                                    } else if (TextUtils.isEmpty(text)) {
                                        N2 = N3;
                                    } else {
                                        if (paragraph != null) {
                                            N2 = N3;
                                            path = new String[]{LocaleController.getString("SettingsSearchFaq", NUM), paragraph};
                                        } else {
                                            N2 = N3;
                                            path = new String[]{LocaleController.getString("SettingsSearchFaq", NUM)};
                                        }
                                        arrayList.add(new MessagesController.FaqSearchResult(text, path, url));
                                    }
                                } else {
                                    N2 = N3;
                                }
                                b++;
                                TLObject tLObject2 = response2;
                                N3 = N2;
                            }
                            N = N3;
                        } else {
                            N = N3;
                            if (block instanceof TLRPC.TL_pageBlockAnchor) {
                                break;
                            }
                        }
                        a++;
                        TLObject tLObject3 = response2;
                        N3 = N;
                    }
                    this.faqWebPage = page;
                }
                AndroidUtilities.runOnUIThread(new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda83(this, arrayList));
            }
            this.loadingFaqPage = false;
        }

        /* renamed from: lambda$loadFaqWebPage$84$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3733x5bd8b111(ArrayList arrayList) {
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

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 0;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int icon;
            switch (holder.getItemViewType()) {
                case 0:
                    SettingsSearchCell searchCell = (SettingsSearchCell) holder.itemView;
                    boolean z = false;
                    boolean z2 = true;
                    if (!this.searchWas) {
                        if (!this.recentSearches.isEmpty()) {
                            position--;
                        }
                        if (position < this.recentSearches.size()) {
                            Object object = this.recentSearches.get(position);
                            if (object instanceof SearchResult) {
                                SearchResult result = (SearchResult) object;
                                String access$23600 = result.searchTitle;
                                String[] access$23500 = result.path;
                                if (position >= this.recentSearches.size() - 1) {
                                    z2 = false;
                                }
                                searchCell.setTextAndValue(access$23600, access$23500, false, z2);
                                return;
                            } else if (object instanceof MessagesController.FaqSearchResult) {
                                MessagesController.FaqSearchResult result2 = (MessagesController.FaqSearchResult) object;
                                String str = result2.title;
                                String[] strArr = result2.path;
                                if (position < this.recentSearches.size() - 1) {
                                    z = true;
                                }
                                searchCell.setTextAndValue(str, strArr, true, z);
                                return;
                            } else {
                                return;
                            }
                        } else {
                            int position2 = position - (this.recentSearches.size() + 1);
                            MessagesController.FaqSearchResult result3 = this.faqSearchArray.get(position2);
                            String str2 = result3.title;
                            String[] strArr2 = result3.path;
                            if (position2 < this.recentSearches.size() - 1) {
                                z = true;
                            }
                            searchCell.setTextAndValue(str2, strArr2, true, z);
                            return;
                        }
                    } else if (position < this.searchResults.size()) {
                        SearchResult result4 = this.searchResults.get(position);
                        SearchResult prevResult = position > 0 ? this.searchResults.get(position - 1) : null;
                        if (prevResult == null || prevResult.iconResId != result4.iconResId) {
                            icon = result4.iconResId;
                        } else {
                            icon = 0;
                        }
                        CharSequence charSequence = this.resultNames.get(position);
                        String[] access$235002 = result4.path;
                        if (position < this.searchResults.size() - 1) {
                            z = true;
                        }
                        searchCell.setTextAndValueAndIcon(charSequence, access$235002, icon, z);
                        return;
                    } else {
                        int position3 = position - (this.searchResults.size() + 1);
                        CharSequence charSequence2 = this.resultNames.get(this.searchResults.size() + position3);
                        String[] strArr3 = this.faqSearchResults.get(position3).path;
                        if (position3 < this.searchResults.size() - 1) {
                            z = true;
                        }
                        searchCell.setTextAndValue(charSequence2, strArr3, true, z);
                        return;
                    }
                case 1:
                    ((GraySectionCell) holder.itemView).setText(LocaleController.getString("SettingsFaqSearchTitle", NUM));
                    return;
                case 2:
                    ((HeaderCell) holder.itemView).setText(LocaleController.getString("SettingsRecent", NUM));
                    return;
                default:
                    return;
            }
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public int getItemViewType(int position) {
            if (this.searchWas) {
                if (position >= this.searchResults.size() && position == this.searchResults.size()) {
                    return 1;
                }
            } else if (position != 0) {
                return (this.recentSearches.isEmpty() || position != this.recentSearches.size() + 1) ? 0 : 1;
            } else {
                if (!this.recentSearches.isEmpty()) {
                    return 2;
                }
                return 1;
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
                ArrayList<Object> arrayList = this.recentSearches;
                arrayList.remove(arrayList.size() - 1);
            }
            LinkedHashSet<String> toSave = new LinkedHashSet<>();
            int N = this.recentSearches.size();
            for (int a = 0; a < N; a++) {
                Object o = this.recentSearches.get(a);
                if (o instanceof SearchResult) {
                    int unused = ((SearchResult) o).num = a;
                } else if (o instanceof MessagesController.FaqSearchResult) {
                    ((MessagesController.FaqSearchResult) o).num = a;
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
            if (o instanceof MessagesController.FaqSearchResult) {
                return ((MessagesController.FaqSearchResult) o).num;
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
                this.this$0.emptyView.stickerView.getImageReceiver().startAnimation();
                this.this$0.emptyView.title.setText(LocaleController.getString("SettingsNoRecent", NUM));
                notifyDataSetChanged();
                return;
            }
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            ProfileActivity$SearchAdapter$$ExternalSyntheticLambda81 profileActivity$SearchAdapter$$ExternalSyntheticLambda81 = new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda81(this, text);
            this.searchRunnable = profileActivity$SearchAdapter$$ExternalSyntheticLambda81;
            dispatchQueue.postRunnable(profileActivity$SearchAdapter$$ExternalSyntheticLambda81, 300);
        }

        /* renamed from: lambda$search$87$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3820lambda$search$87$orgtelegramuiProfileActivity$SearchAdapter(String text) {
            String str;
            int N;
            String title;
            String str2;
            String title2;
            int index;
            ArrayList<SearchResult> results = new ArrayList<>();
            ArrayList<MessagesController.FaqSearchResult> faqResults = new ArrayList<>();
            ArrayList<CharSequence> names = new ArrayList<>();
            String str3 = " ";
            String[] searchArgs = text.split(str3);
            String[] translitArgs = new String[searchArgs.length];
            for (int a = 0; a < searchArgs.length; a++) {
                translitArgs[a] = LocaleController.getInstance().getTranslitString(searchArgs[a]);
                if (translitArgs[a].equals(searchArgs[a])) {
                    translitArgs[a] = null;
                }
            }
            int a2 = 0;
            while (true) {
                SearchResult[] searchResultArr = this.searchArray;
                if (a2 >= searchResultArr.length) {
                    break;
                }
                SearchResult result = searchResultArr[a2];
                if (result != null) {
                    String title3 = str3 + result.searchTitle.toLowerCase();
                    SpannableStringBuilder stringBuilder = null;
                    int i = 0;
                    while (true) {
                        if (i >= searchArgs.length) {
                            break;
                        }
                        if (searchArgs[i].length() != 0) {
                            String searchString = searchArgs[i];
                            int index2 = title3.indexOf(str3 + searchString);
                            if (index2 >= 0 || translitArgs[i] == null) {
                                index = index2;
                            } else {
                                searchString = translitArgs[i];
                                int i2 = index2;
                                index = title3.indexOf(str3 + searchString);
                            }
                            if (index < 0) {
                                String str4 = searchString;
                                break;
                            }
                            if (stringBuilder == null) {
                                title2 = title3;
                                stringBuilder = new SpannableStringBuilder(result.searchTitle);
                            } else {
                                title2 = title3;
                            }
                            String str5 = searchString;
                            stringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor("windowBackgroundWhiteBlueText4")), index, searchString.length() + index, 33);
                        } else {
                            title2 = title3;
                        }
                        if (stringBuilder != null && i == searchArgs.length - 1) {
                            if (result.guid == 502) {
                                int freeAccount = -1;
                                int b = 0;
                                while (true) {
                                    if (b >= 3) {
                                        break;
                                    } else if (!UserConfig.getInstance(a2).isClientActivated()) {
                                        freeAccount = b;
                                        break;
                                    } else {
                                        b++;
                                    }
                                }
                                if (freeAccount < 0) {
                                }
                            }
                            results.add(result);
                            names.add(stringBuilder);
                        }
                        i++;
                        String str6 = text;
                        title3 = title2;
                    }
                }
                a2++;
                String str7 = text;
            }
            if (this.faqWebPage != null) {
                int a3 = 0;
                int N2 = this.faqSearchArray.size();
                while (a3 < N2) {
                    MessagesController.FaqSearchResult result2 = this.faqSearchArray.get(a3);
                    String title4 = str3 + result2.title.toLowerCase();
                    SpannableStringBuilder stringBuilder2 = null;
                    int i3 = 0;
                    while (true) {
                        if (i3 >= searchArgs.length) {
                            str = str3;
                            N = N2;
                            break;
                        }
                        if (searchArgs[i3].length() != 0) {
                            String searchString2 = searchArgs[i3];
                            int index3 = title4.indexOf(str3 + searchString2);
                            if (index3 >= 0 || translitArgs[i3] == null) {
                                N = N2;
                            } else {
                                searchString2 = translitArgs[i3];
                                N = N2;
                                index3 = title4.indexOf(str3 + searchString2);
                            }
                            if (index3 < 0) {
                                str = str3;
                                break;
                            }
                            if (stringBuilder2 == null) {
                                str2 = str3;
                                stringBuilder2 = new SpannableStringBuilder(result2.title);
                            } else {
                                str2 = str3;
                            }
                            title = title4;
                            stringBuilder2.setSpan(new ForegroundColorSpan(Theme.getColor("windowBackgroundWhiteBlueText4")), index3, searchString2.length() + index3, 33);
                        } else {
                            str2 = str3;
                            N = N2;
                            title = title4;
                        }
                        if (stringBuilder2 != null && i3 == searchArgs.length - 1) {
                            faqResults.add(result2);
                            names.add(stringBuilder2);
                        }
                        i3++;
                        N2 = N;
                        str3 = str2;
                        title4 = title;
                    }
                    String str8 = title4;
                    a3++;
                    N2 = N;
                    str3 = str;
                }
            }
            AndroidUtilities.runOnUIThread(new ProfileActivity$SearchAdapter$$ExternalSyntheticLambda82(this, text, results, faqResults, names));
        }

        /* renamed from: lambda$search$86$org-telegram-ui-ProfileActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3819lambda$search$86$orgtelegramuiProfileActivity$SearchAdapter(String text, ArrayList results, ArrayList faqResults, ArrayList names) {
            if (text.equals(this.lastSearchString)) {
                if (!this.searchWas) {
                    this.this$0.emptyView.stickerView.getImageReceiver().startAnimation();
                    this.this$0.emptyView.title.setText(LocaleController.getString("SettingsNoResults", NUM));
                }
                this.searchWas = true;
                this.searchResults = results;
                this.faqSearchResults = faqResults;
                this.resultNames = names;
                notifyDataSetChanged();
                this.this$0.emptyView.stickerView.getImageReceiver().startAnimation();
            }
        }

        public boolean isSearchWas() {
            return this.searchWas;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate themeDelegate = new ProfileActivity$$ExternalSyntheticLambda24(this);
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
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDelegate, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDelegate, "avatar_actionBarSelectorBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDelegate, "chat_lockIcon"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = themeDelegate;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_subtitleInProfileBlue"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate2 = themeDelegate;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundActionBarBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "profile_title"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "profile_status"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_subtitleInProfileBlue"));
        if (this.mediaCounterTextView != null) {
            arrayList.add(new ThemeDescription(this.mediaCounterTextView.getTextView(), ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, themeDelegate, "player_actionBarSubtitle"));
            arrayList.add(new ThemeDescription(this.mediaCounterTextView.getNextTextView(), ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, themeDelegate, "player_actionBarSubtitle"));
        }
        arrayList.add(new ThemeDescription(this.topView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_backgroundActionBarBlue"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription(this.avatarImage, 0, (Class[]) null, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        arrayList.add(new ThemeDescription(this.avatarImage, 0, (Class[]) null, (Paint) null, new Drawable[]{this.avatarDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_backgroundInProfileBlue"));
        arrayList.add(new ThemeDescription(this.writeButton, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_actionIcon"));
        arrayList.add(new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_actionBackground"));
        arrayList.add(new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_actionPressedBackground"));
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
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, themeDelegate, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, themeDelegate, "windowBackgroundWhiteBlueText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate3 = themeDelegate;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate3, "avatar_backgroundRed"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate4 = themeDelegate;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate4, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate3, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate4, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate3, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate4, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate3, "avatar_backgroundPink"));
        arrayList.add(new ThemeDescription(this.undoView, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_background"));
        arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
        arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
        arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        arrayList.add(new ThemeDescription((View) this.undoView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{UndoView.class}, new String[]{"leftImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AboutLinkCell.class}, Theme.profile_aboutTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        RecyclerListView recyclerListView = this.listView;
        RecyclerListView recyclerListView2 = recyclerListView;
        arrayList.add(new ThemeDescription(recyclerListView2, ThemeDescription.FLAG_LINKCOLOR, new Class[]{AboutLinkCell.class}, Theme.profile_aboutTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteLinkText"));
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

    /* renamed from: lambda$getThemeDescriptions$34$org-telegram-ui-ProfileActivity  reason: not valid java name */
    public /* synthetic */ void m3704lambda$getThemeDescriptions$34$orgtelegramuiProfileActivity() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof UserCell) {
                    ((UserCell) child).update(0);
                }
            }
        }
        if (this.isPulledDown == 0) {
            SimpleTextView[] simpleTextViewArr = this.onlineTextView;
            if (simpleTextViewArr[1] != null) {
                Object onlineTextViewTag = simpleTextViewArr[1].getTag();
                if (onlineTextViewTag instanceof String) {
                    this.onlineTextView[1].setTextColor(Theme.getColor((String) onlineTextViewTag));
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
            if (this.actionBar != null) {
                this.actionBar.setItemsColor(Theme.getColor("actionBarDefaultIcon"), false);
                this.actionBar.setItemsBackgroundColor(Theme.getColor("avatar_actionBarSelectorBlue"), false);
            }
        }
    }

    public void updateListAnimated(boolean updateOnlineCount) {
        if (this.listAdapter == null) {
            if (updateOnlineCount) {
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
        if (updateOnlineCount) {
            updateOnlineCount(false);
        }
        saveScrollPosition();
        updateRowsIds();
        diffCallback.fillPositions(diffCallback.newPositionToItem);
        try {
            DiffUtil.calculateDiff(diffCallback).dispatchUpdatesTo((RecyclerView.Adapter) this.listAdapter);
        } catch (Exception e) {
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
            int position = -1;
            int top = Integer.MAX_VALUE;
            for (int i = 0; i < this.listView.getChildCount(); i++) {
                RecyclerListView recyclerListView2 = this.listView;
                int childPosition = recyclerListView2.getChildAdapterPosition(recyclerListView2.getChildAt(i));
                View child = this.listView.getChildAt(i);
                if (childPosition != -1 && child.getTop() < top) {
                    view = child;
                    position = childPosition;
                    top = child.getTop();
                }
            }
            if (view != null) {
                this.savedScrollPosition = position;
                int top2 = view.getTop();
                this.savedScrollOffset = top2;
                if (this.savedScrollPosition == 0 && !this.allowPullingDown && top2 > AndroidUtilities.dp(88.0f)) {
                    this.savedScrollOffset = AndroidUtilities.dp(88.0f);
                }
                this.layoutManager.scrollToPositionWithOffset(position, view.getTop() - this.listView.getPaddingTop());
            }
        }
    }

    public void scrollToSharedMedia() {
        this.layoutManager.scrollToPositionWithOffset(this.sharedMediaRow, -this.listView.getPaddingTop());
    }

    private class DiffCallback extends DiffUtil.Callback {
        SparseIntArray newPositionToItem;
        ArrayList<TLRPC.ChatParticipant> oldChatParticipant;
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

        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            TLRPC.ChatParticipant oldItem;
            TLRPC.ChatParticipant newItem;
            if (newItemPosition < ProfileActivity.this.membersStartRow || newItemPosition >= ProfileActivity.this.membersEndRow || oldItemPosition < this.oldMembersStartRow || oldItemPosition >= this.oldMembersEndRow) {
                int oldIndex = this.oldPositionToItem.get(oldItemPosition, -1);
                if (oldIndex != this.newPositionToItem.get(newItemPosition, -1) || oldIndex < 0) {
                    return false;
                }
                return true;
            }
            if (!this.oldChatParticipantSorted.isEmpty()) {
                oldItem = this.oldChatParticipant.get(this.oldChatParticipantSorted.get(oldItemPosition - this.oldMembersStartRow).intValue());
            } else {
                oldItem = this.oldChatParticipant.get(oldItemPosition - this.oldMembersStartRow);
            }
            if (!ProfileActivity.this.sortedUsers.isEmpty()) {
                newItem = (TLRPC.ChatParticipant) ProfileActivity.this.visibleChatParticipants.get(((Integer) ProfileActivity.this.visibleSortedUsers.get(newItemPosition - ProfileActivity.this.membersStartRow)).intValue());
            } else {
                newItem = (TLRPC.ChatParticipant) ProfileActivity.this.visibleChatParticipants.get(newItemPosition - ProfileActivity.this.membersStartRow);
            }
            if (oldItem.user_id == newItem.user_id) {
                return true;
            }
            return false;
        }

        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return areItemsTheSame(oldItemPosition, newItemPosition);
        }

        public void fillPositions(SparseIntArray sparseIntArray) {
            sparseIntArray.clear();
            int pointer = 0 + 1;
            put(pointer, ProfileActivity.this.setAvatarRow, sparseIntArray);
            int pointer2 = pointer + 1;
            put(pointer2, ProfileActivity.this.setAvatarSectionRow, sparseIntArray);
            int pointer3 = pointer2 + 1;
            put(pointer3, ProfileActivity.this.numberSectionRow, sparseIntArray);
            int pointer4 = pointer3 + 1;
            put(pointer4, ProfileActivity.this.numberRow, sparseIntArray);
            int pointer5 = pointer4 + 1;
            put(pointer5, ProfileActivity.this.setUsernameRow, sparseIntArray);
            int pointer6 = pointer5 + 1;
            put(pointer6, ProfileActivity.this.bioRow, sparseIntArray);
            int pointer7 = pointer6 + 1;
            put(pointer7, ProfileActivity.this.phoneSuggestionRow, sparseIntArray);
            int pointer8 = pointer7 + 1;
            put(pointer8, ProfileActivity.this.phoneSuggestionSectionRow, sparseIntArray);
            int pointer9 = pointer8 + 1;
            put(pointer9, ProfileActivity.this.passwordSuggestionRow, sparseIntArray);
            int pointer10 = pointer9 + 1;
            put(pointer10, ProfileActivity.this.passwordSuggestionSectionRow, sparseIntArray);
            int pointer11 = pointer10 + 1;
            put(pointer11, ProfileActivity.this.settingsSectionRow, sparseIntArray);
            int pointer12 = pointer11 + 1;
            put(pointer12, ProfileActivity.this.settingsSectionRow2, sparseIntArray);
            int pointer13 = pointer12 + 1;
            put(pointer13, ProfileActivity.this.notificationRow, sparseIntArray);
            int pointer14 = pointer13 + 1;
            put(pointer14, ProfileActivity.this.languageRow, sparseIntArray);
            int pointer15 = pointer14 + 1;
            put(pointer15, ProfileActivity.this.privacyRow, sparseIntArray);
            int pointer16 = pointer15 + 1;
            put(pointer16, ProfileActivity.this.dataRow, sparseIntArray);
            int pointer17 = pointer16 + 1;
            put(pointer17, ProfileActivity.this.chatRow, sparseIntArray);
            int pointer18 = pointer17 + 1;
            put(pointer18, ProfileActivity.this.filtersRow, sparseIntArray);
            int pointer19 = pointer18 + 1;
            put(pointer19, ProfileActivity.this.devicesRow, sparseIntArray);
            int pointer20 = pointer19 + 1;
            put(pointer20, ProfileActivity.this.devicesSectionRow, sparseIntArray);
            int pointer21 = pointer20 + 1;
            put(pointer21, ProfileActivity.this.helpHeaderRow, sparseIntArray);
            int pointer22 = pointer21 + 1;
            put(pointer22, ProfileActivity.this.questionRow, sparseIntArray);
            int pointer23 = pointer22 + 1;
            put(pointer23, ProfileActivity.this.faqRow, sparseIntArray);
            int pointer24 = pointer23 + 1;
            put(pointer24, ProfileActivity.this.policyRow, sparseIntArray);
            int pointer25 = pointer24 + 1;
            put(pointer25, ProfileActivity.this.helpSectionCell, sparseIntArray);
            int pointer26 = pointer25 + 1;
            put(pointer26, ProfileActivity.this.debugHeaderRow, sparseIntArray);
            int pointer27 = pointer26 + 1;
            put(pointer27, ProfileActivity.this.sendLogsRow, sparseIntArray);
            int pointer28 = pointer27 + 1;
            put(pointer28, ProfileActivity.this.sendLastLogsRow, sparseIntArray);
            int pointer29 = pointer28 + 1;
            put(pointer29, ProfileActivity.this.clearLogsRow, sparseIntArray);
            int pointer30 = pointer29 + 1;
            put(pointer30, ProfileActivity.this.switchBackendRow, sparseIntArray);
            int pointer31 = pointer30 + 1;
            put(pointer31, ProfileActivity.this.versionRow, sparseIntArray);
            int pointer32 = pointer31 + 1;
            put(pointer32, ProfileActivity.this.emptyRow, sparseIntArray);
            int pointer33 = pointer32 + 1;
            put(pointer33, ProfileActivity.this.bottomPaddingRow, sparseIntArray);
            int pointer34 = pointer33 + 1;
            put(pointer34, ProfileActivity.this.infoHeaderRow, sparseIntArray);
            int pointer35 = pointer34 + 1;
            put(pointer35, ProfileActivity.this.phoneRow, sparseIntArray);
            int pointer36 = pointer35 + 1;
            put(pointer36, ProfileActivity.this.locationRow, sparseIntArray);
            int pointer37 = pointer36 + 1;
            put(pointer37, ProfileActivity.this.userInfoRow, sparseIntArray);
            int pointer38 = pointer37 + 1;
            put(pointer38, ProfileActivity.this.channelInfoRow, sparseIntArray);
            int pointer39 = pointer38 + 1;
            put(pointer39, ProfileActivity.this.usernameRow, sparseIntArray);
            int pointer40 = pointer39 + 1;
            put(pointer40, ProfileActivity.this.notificationsDividerRow, sparseIntArray);
            int pointer41 = pointer40 + 1;
            put(pointer41, ProfileActivity.this.notificationsRow, sparseIntArray);
            int pointer42 = pointer41 + 1;
            put(pointer42, ProfileActivity.this.infoSectionRow, sparseIntArray);
            int pointer43 = pointer42 + 1;
            put(pointer43, ProfileActivity.this.sendMessageRow, sparseIntArray);
            int pointer44 = pointer43 + 1;
            put(pointer44, ProfileActivity.this.reportRow, sparseIntArray);
            int pointer45 = pointer44 + 1;
            put(pointer45, ProfileActivity.this.settingsTimerRow, sparseIntArray);
            int pointer46 = pointer45 + 1;
            put(pointer46, ProfileActivity.this.settingsKeyRow, sparseIntArray);
            int pointer47 = pointer46 + 1;
            put(pointer47, ProfileActivity.this.secretSettingsSectionRow, sparseIntArray);
            int pointer48 = pointer47 + 1;
            put(pointer48, ProfileActivity.this.membersHeaderRow, sparseIntArray);
            int pointer49 = pointer48 + 1;
            put(pointer49, ProfileActivity.this.addMemberRow, sparseIntArray);
            int pointer50 = pointer49 + 1;
            put(pointer50, ProfileActivity.this.subscribersRow, sparseIntArray);
            int pointer51 = pointer50 + 1;
            put(pointer51, ProfileActivity.this.subscribersRequestsRow, sparseIntArray);
            int pointer52 = pointer51 + 1;
            put(pointer52, ProfileActivity.this.administratorsRow, sparseIntArray);
            int pointer53 = pointer52 + 1;
            put(pointer53, ProfileActivity.this.blockedUsersRow, sparseIntArray);
            int pointer54 = pointer53 + 1;
            put(pointer54, ProfileActivity.this.membersSectionRow, sparseIntArray);
            int pointer55 = pointer54 + 1;
            put(pointer55, ProfileActivity.this.sharedMediaRow, sparseIntArray);
            int pointer56 = pointer55 + 1;
            put(pointer56, ProfileActivity.this.unblockRow, sparseIntArray);
            int pointer57 = pointer56 + 1;
            put(pointer57, ProfileActivity.this.joinRow, sparseIntArray);
            put(pointer57 + 1, ProfileActivity.this.lastSectionRow, sparseIntArray);
        }

        private void put(int id, int position, SparseIntArray sparseIntArray) {
            if (position >= 0) {
                sparseIntArray.put(position, id);
            }
        }
    }
}
