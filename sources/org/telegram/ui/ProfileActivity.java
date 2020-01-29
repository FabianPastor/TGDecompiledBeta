package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
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
import android.graphics.Outline;
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
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Property;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Keep;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.SecretChatHelper;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
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
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextDetailCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.ChatRightsEditActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CrossfadeDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ProfileGalleryView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScamDrawable;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.GroupCreateActivity;
import org.telegram.ui.MediaActivity;
import org.telegram.ui.NotificationsSettingsActivity;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.ProfileActivity;

public class ProfileActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, DialogsActivity.DialogsActivityDelegate {
    private static final int add_contact = 1;
    private static final int add_member = 18;
    private static final int add_shortcut = 14;
    private static final int block_contact = 2;
    private static final int call_item = 15;
    private static final int delete_contact = 5;
    private static final int edit_channel = 12;
    private static final int edit_contact = 4;
    private static final int invite_to_group = 9;
    private static final int leave_group = 7;
    private static final int search_members = 17;
    private static final int share = 10;
    private static final int share_contact = 3;
    private static final int statistics = 19;
    /* access modifiers changed from: private */
    public int addMemberRow;
    /* access modifiers changed from: private */
    public int administratorsRow;
    private boolean allowProfileAnimation = true;
    /* access modifiers changed from: private */
    public boolean allowPullingDown;
    /* access modifiers changed from: private */
    public ActionBarMenuItem animatingItem;
    private float animationProgress;
    /* access modifiers changed from: private */
    public int audioRow;
    private AvatarDrawable avatarDrawable;
    /* access modifiers changed from: private */
    public AvatarImageView avatarImage;
    private float avatarScale;
    private float avatarX;
    private float avatarY;
    /* access modifiers changed from: private */
    public ProfileGalleryView avatarsViewPager;
    private PagerIndicatorView avatarsViewPagerIndicatorView;
    /* access modifiers changed from: private */
    public int banFromGroup;
    /* access modifiers changed from: private */
    public int blockedUsersRow;
    /* access modifiers changed from: private */
    public TLRPC.BotInfo botInfo;
    /* access modifiers changed from: private */
    public int bottomPaddingRow;
    /* access modifiers changed from: private */
    public ActionBarMenuItem callItem;
    /* access modifiers changed from: private */
    public int channelInfoRow;
    /* access modifiers changed from: private */
    public TLRPC.ChatFull chatInfo;
    /* access modifiers changed from: private */
    public int chat_id;
    private boolean creatingChat;
    private TLRPC.ChannelParticipant currentChannelParticipant;
    /* access modifiers changed from: private */
    public TLRPC.Chat currentChat;
    /* access modifiers changed from: private */
    public TLRPC.EncryptedChat currentEncryptedChat;
    /* access modifiers changed from: private */
    public long dialog_id;
    /* access modifiers changed from: private */
    public ActionBarMenuItem editItem;
    /* access modifiers changed from: private */
    public int emptyRow;
    /* access modifiers changed from: private */
    public ValueAnimator expandAnimator;
    private float[] expandAnimatorValues = {0.0f, 1.0f};
    private float expandProgress;
    /* access modifiers changed from: private */
    public float extraHeight;
    /* access modifiers changed from: private */
    public int filesRow;
    /* access modifiers changed from: private */
    public int groupsInCommonRow;
    /* access modifiers changed from: private */
    public int infoHeaderRow;
    /* access modifiers changed from: private */
    public int infoSectionRow;
    private float initialAnimationExtraHeight;
    /* access modifiers changed from: private */
    public boolean isBot;
    /* access modifiers changed from: private */
    public boolean isInLandscapeMode;
    private boolean[] isOnline = new boolean[1];
    /* access modifiers changed from: private */
    public boolean isPulledDown;
    /* access modifiers changed from: private */
    public int joinRow;
    /* access modifiers changed from: private */
    public int lastMeasuredContentWidth;
    /* access modifiers changed from: private */
    public int[] lastMediaCount = {-1, -1, -1, -1, -1};
    /* access modifiers changed from: private */
    public int lastSectionRow;
    /* access modifiers changed from: private */
    public LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public int leaveChannelRow;
    /* access modifiers changed from: private */
    public int linksRow;
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
    private Drawable lockIconDrawable;
    private MediaActivity mediaActivity;
    private int[] mediaCount = {-1, -1, -1, -1, -1};
    private int[] mediaMergeCount = {-1, -1, -1, -1, -1};
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
    public SimpleTextView[] nameTextView = new SimpleTextView[2];
    private float nameX;
    private float nameY;
    /* access modifiers changed from: private */
    public int notificationsDividerRow;
    /* access modifiers changed from: private */
    public int notificationsRow;
    private int onlineCount = -1;
    private SimpleTextView[] onlineTextView = new SimpleTextView[2];
    private float onlineX;
    private float onlineY;
    /* access modifiers changed from: private */
    public boolean openAnimationInProgress;
    private OverlaysView overlaysView;
    /* access modifiers changed from: private */
    public SparseArray<TLRPC.ChatParticipant> participantsMap = new SparseArray<>();
    /* access modifiers changed from: private */
    public int phoneRow;
    /* access modifiers changed from: private */
    public int photosRow;
    /* access modifiers changed from: private */
    public boolean playProfileAnimation;
    private int[] prevMediaCount = {-1, -1, -1, -1, -1};
    private PhotoViewer.PhotoViewerProvider provider = new PhotoViewer.EmptyPhotoViewerProvider() {
        public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int i, boolean z) {
            TLRPC.FileLocation fileLocation2;
            TLRPC.Chat chat;
            TLRPC.ChatPhoto chatPhoto;
            TLRPC.User user;
            TLRPC.UserProfilePhoto userProfilePhoto;
            if (fileLocation == null) {
                return null;
            }
            if (ProfileActivity.this.user_id == 0 ? ProfileActivity.this.chat_id == 0 || (chat = MessagesController.getInstance(ProfileActivity.this.currentAccount).getChat(Integer.valueOf(ProfileActivity.this.chat_id))) == null || (chatPhoto = chat.photo) == null || (fileLocation2 = chatPhoto.photo_big) == null : (user = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id))) == null || (userProfilePhoto = user.photo) == null || (fileLocation2 = userProfilePhoto.photo_big) == null) {
                fileLocation2 = null;
            }
            if (fileLocation2 == null || fileLocation2.local_id != fileLocation.local_id || fileLocation2.volume_id != fileLocation.volume_id || fileLocation2.dc_id != fileLocation.dc_id) {
                return null;
            }
            int[] iArr = new int[2];
            ProfileActivity.this.avatarImage.getLocationInWindow(iArr);
            PhotoViewer.PlaceProviderObject placeProviderObject = new PhotoViewer.PlaceProviderObject();
            int i2 = 0;
            placeProviderObject.viewX = iArr[0];
            int i3 = iArr[1];
            if (Build.VERSION.SDK_INT < 21) {
                i2 = AndroidUtilities.statusBarHeight;
            }
            placeProviderObject.viewY = i3 - i2;
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
            placeProviderObject.scale = ProfileActivity.this.avatarImage.getScaleX();
            return placeProviderObject;
        }

        public void willHidePhotoViewer() {
            ProfileActivity.this.avatarImage.getImageReceiver().setVisible(true, true);
        }
    };
    private boolean recreateMenuAfterAnimation;
    /* access modifiers changed from: private */
    public boolean reportSpam;
    /* access modifiers changed from: private */
    public int rowCount;
    private ScamDrawable scamDrawable;
    private int selectedUser;
    /* access modifiers changed from: private */
    public int settingsKeyRow;
    /* access modifiers changed from: private */
    public int settingsSectionRow;
    /* access modifiers changed from: private */
    public int settingsTimerRow;
    /* access modifiers changed from: private */
    public int sharedHeaderRow;
    private MediaActivity.SharedMediaData[] sharedMediaData;
    /* access modifiers changed from: private */
    public int sharedSectionRow;
    /* access modifiers changed from: private */
    public ArrayList<Integer> sortedUsers;
    /* access modifiers changed from: private */
    public int startSecretChatRow;
    /* access modifiers changed from: private */
    public int subscribersRow;
    /* access modifiers changed from: private */
    public TopView topView;
    /* access modifiers changed from: private */
    public int unblockRow;
    /* access modifiers changed from: private */
    public UndoView undoView;
    /* access modifiers changed from: private */
    public boolean userBlocked;
    /* access modifiers changed from: private */
    public TLRPC.UserFull userInfo;
    /* access modifiers changed from: private */
    public int userInfoRow;
    /* access modifiers changed from: private */
    public int user_id;
    /* access modifiers changed from: private */
    public int usernameRow;
    /* access modifiers changed from: private */
    public boolean usersEndReached;
    private Drawable verifiedCheckDrawable;
    private CrossfadeDrawable verifiedCrossfadeDrawable;
    private Drawable verifiedDrawable;
    /* access modifiers changed from: private */
    public int voiceRow;
    private ImageView writeButton;
    /* access modifiers changed from: private */
    public AnimatorSet writeButtonAnimation;

    public class AvatarImageView extends BackupImageView {
        private float foregroundAlpha;
        private ImageReceiver foregroundImageReceiver = new ImageReceiver(this);
        private final Paint placeholderPaint = new Paint(1);
        private final RectF rect = new RectF();

        public AvatarImageView(Context context) {
            super(context);
            this.placeholderPaint.setColor(-16777216);
        }

        public void setForegroundImage(ImageLocation imageLocation, String str, Drawable drawable) {
            this.foregroundImageReceiver.setImage(imageLocation, str, drawable, 0, (String) null, (Object) null, 0);
        }

        public void setForegroundImageDrawable(Drawable drawable) {
            this.foregroundImageReceiver.setImageBitmap(drawable);
        }

        public float getForegroundAlpha() {
            return this.foregroundAlpha;
        }

        public void setForegroundAlpha(float f) {
            this.foregroundAlpha = f;
            invalidate();
        }

        public void clearForeground() {
            this.foregroundImageReceiver.clearImage();
            this.foregroundAlpha = 0.0f;
            invalidate();
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.foregroundImageReceiver.onDetachedFromWindow();
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
                this.imageReceiver.setImageCoords(0, 0, getMeasuredWidth(), getMeasuredHeight());
                this.imageReceiver.draw(canvas);
            }
            if (this.foregroundAlpha <= 0.0f) {
                return;
            }
            if (this.foregroundImageReceiver.getDrawable() != null) {
                this.foregroundImageReceiver.setImageCoords(0, 0, getMeasuredWidth(), getMeasuredHeight());
                this.foregroundImageReceiver.setAlpha(this.foregroundAlpha);
                this.foregroundImageReceiver.draw(canvas);
                return;
            }
            this.rect.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
            this.placeholderPaint.setAlpha((int) (this.foregroundAlpha * 255.0f));
            float roundRadius = (float) this.foregroundImageReceiver.getRoundRadius();
            canvas.drawRoundRect(this.rect, roundRadius, roundRadius, this.placeholderPaint);
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
            float currentActionBarHeight = ((float) (ActionBar.getCurrentActionBarHeight() + (ProfileActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0))) + ProfileActivity.this.extraHeight;
            canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), currentActionBarHeight, this.paint);
            if (ProfileActivity.this.parentLayout != null) {
                ProfileActivity.this.parentLayout.drawHeaderShadow(canvas, (int) currentActionBarHeight);
            }
        }
    }

    private class OverlaysView extends View {
        /* access modifiers changed from: private */
        public final ValueAnimator animator;
        private final float[] animatorValues;
        private final Paint backgroundPaint;
        private final GradientDrawable bottomOverlayGradient;
        private final Rect bottomOverlayRect;
        /* access modifiers changed from: private */
        public boolean isOverlaysVisible;
        private final int statusBarHeight;
        private final GradientDrawable topOverlayGradient;
        private final Rect topOverlayRect;

        public OverlaysView(Context context) {
            super(context);
            this.statusBarHeight = ProfileActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0;
            this.topOverlayRect = new Rect();
            this.bottomOverlayRect = new Rect();
            this.animatorValues = new float[]{0.0f, 1.0f};
            setVisibility(8);
            this.topOverlayGradient = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{NUM, 0});
            this.topOverlayGradient.setShape(0);
            this.bottomOverlayGradient = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{NUM, 0});
            this.bottomOverlayGradient.setShape(0);
            this.backgroundPaint = new Paint(1);
            this.backgroundPaint.setColor(-16777216);
            this.animator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.animator.setDuration(250);
            this.animator.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
            this.animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ProfileActivity.OverlaysView.this.lambda$new$0$ProfileActivity$OverlaysView(valueAnimator);
                }
            });
            this.animator.addListener(new AnimatorListenerAdapter(ProfileActivity.this) {
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

        public /* synthetic */ void lambda$new$0$ProfileActivity$OverlaysView(ValueAnimator valueAnimator) {
            float lerp = AndroidUtilities.lerp(this.animatorValues, valueAnimator.getAnimatedFraction());
            int i = (int) (255.0f * lerp);
            this.topOverlayGradient.setAlpha(i);
            this.bottomOverlayGradient.setAlpha(i);
            this.backgroundPaint.setAlpha((int) (lerp * 66.0f));
            invalidate();
        }

        public boolean isOverlaysVisible() {
            return this.isOverlaysVisible;
        }

        public void setOverlaysVisible(boolean z, float f) {
            if (z != this.isOverlaysVisible) {
                this.isOverlaysVisible = z;
                this.animator.cancel();
                float lerp = AndroidUtilities.lerp(this.animatorValues, this.animator.getAnimatedFraction());
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
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            this.topOverlayGradient.draw(canvas);
            this.bottomOverlayGradient.draw(canvas);
            canvas.drawRect(this.topOverlayRect, this.backgroundPaint);
            canvas.drawRect(this.bottomOverlayRect, this.backgroundPaint);
        }
    }

    private class PagerIndicatorView extends View {
        private final PagerAdapter adapter = ProfileActivity.this.avatarsViewPager.getAdapter();
        private final ValueAnimator animator;
        private final float[] animatorValues = {0.0f, 1.0f};
        private final Paint backgroundPaint;
        private final RectF indicatorRect = new RectF();
        /* access modifiers changed from: private */
        public boolean isIndicatorVisible;
        private final TextPaint textPaint;

        public PagerIndicatorView(Context context) {
            super(context);
            setVisibility(8);
            this.textPaint = new TextPaint(1);
            this.textPaint.setColor(-1);
            this.textPaint.setTypeface(Typeface.SANS_SERIF);
            this.textPaint.setTextAlign(Paint.Align.CENTER);
            this.textPaint.setTextSize(AndroidUtilities.dpf2(15.0f));
            this.backgroundPaint = new Paint(1);
            this.backgroundPaint.setColor(Color.parseColor("#26000000"));
            this.animator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.animator.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
            this.animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ProfileActivity.PagerIndicatorView.this.lambda$new$0$ProfileActivity$PagerIndicatorView(valueAnimator);
                }
            });
            this.animator.addListener(new AnimatorListenerAdapter(ProfileActivity.this) {
                public void onAnimationEnd(Animator animator) {
                    if (PagerIndicatorView.this.isIndicatorVisible) {
                        ActionBarMenuItem access$1300 = PagerIndicatorView.this.getSecondaryMenuItem();
                        if (access$1300 != null) {
                            access$1300.setVisibility(8);
                            return;
                        }
                        return;
                    }
                    PagerIndicatorView.this.setVisibility(8);
                }

                public void onAnimationStart(Animator animator) {
                    ActionBarMenuItem access$1300 = PagerIndicatorView.this.getSecondaryMenuItem();
                    if (access$1300 != null) {
                        access$1300.setVisibility(0);
                    }
                    PagerIndicatorView.this.setVisibility(0);
                }
            });
            ProfileActivity.this.avatarsViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(ProfileActivity.this) {
                public void onPageScrollStateChanged(int i) {
                }

                public void onPageScrolled(int i, float f, int i2) {
                }

                public void onPageSelected(int i) {
                    PagerIndicatorView.this.invalidateIndicatorRect();
                }
            });
            this.adapter.registerDataSetObserver(new DataSetObserver(ProfileActivity.this) {
                public void onChanged() {
                    PagerIndicatorView.this.invalidateIndicatorRect();
                    PagerIndicatorView.this.refreshVisibility(1.0f);
                }
            });
        }

        public /* synthetic */ void lambda$new$0$ProfileActivity$PagerIndicatorView(ValueAnimator valueAnimator) {
            float lerp = AndroidUtilities.lerp(this.animatorValues, valueAnimator.getAnimatedFraction());
            ActionBarMenuItem secondaryMenuItem = getSecondaryMenuItem();
            if (secondaryMenuItem != null) {
                float f = 1.0f - lerp;
                secondaryMenuItem.setScaleX(f);
                secondaryMenuItem.setScaleY(f);
                secondaryMenuItem.setAlpha(f);
            }
            setScaleX(lerp);
            setScaleY(lerp);
            setAlpha(lerp);
        }

        public boolean isIndicatorVisible() {
            return this.isIndicatorVisible;
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
            boolean z = true;
            if (!ProfileActivity.this.isPulledDown || this.adapter.getCount() <= 1) {
                z = false;
            }
            setIndicatorVisible(z, f);
        }

        /* access modifiers changed from: protected */
        public void onSizeChanged(int i, int i2, int i3, int i4) {
            invalidateIndicatorRect();
        }

        /* access modifiers changed from: private */
        public void invalidateIndicatorRect() {
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
            if (ProfileActivity.this.callItem != null) {
                return ProfileActivity.this.callItem;
            }
            if (ProfileActivity.this.editItem != null) {
                return ProfileActivity.this.editItem;
            }
            return null;
        }
    }

    public ProfileActivity(Bundle bundle) {
        super(bundle);
    }

    public boolean onFragmentCreate() {
        this.user_id = this.arguments.getInt("user_id", 0);
        this.chat_id = this.arguments.getInt("chat_id", 0);
        this.banFromGroup = this.arguments.getInt("ban_chat_id", 0);
        this.reportSpam = this.arguments.getBoolean("reportSpam", false);
        if (this.user_id != 0) {
            this.dialog_id = this.arguments.getLong("dialog_id", 0);
            if (this.dialog_id != 0) {
                this.currentEncryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf((int) (this.dialog_id >> 32)));
            }
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
            if (user == null) {
                return false;
            }
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactsDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.encryptedChatCreated);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.encryptedChatUpdated);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.blockedUsersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.botInfoDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.userInfoDidLoad);
            this.userBlocked = MessagesController.getInstance(this.currentAccount).blockedUsers.indexOfKey(this.user_id) >= 0;
            if (user.bot) {
                this.isBot = true;
                MediaDataController.getInstance(this.currentAccount).loadBotInfo(user.id, true, this.classGuid);
            }
            this.userInfo = MessagesController.getInstance(this.currentAccount).getUserFull(this.user_id);
            MessagesController.getInstance(this.currentAccount).loadFullUser(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id)), this.classGuid, true);
            this.participantsMap = null;
        } else if (this.chat_id == 0) {
            return false;
        } else {
            this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id));
            if (this.currentChat == null) {
                CountDownLatch countDownLatch = new CountDownLatch(1);
                MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable(countDownLatch) {
                    private final /* synthetic */ CountDownLatch f$1;

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
                MessagesController.getInstance(this.currentAccount).putChat(this.currentChat, true);
            }
            if (this.currentChat.megagroup) {
                getChannelParticipants(true);
            } else {
                this.participantsMap = null;
            }
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatOnlineCountDidLoad);
            this.sortedUsers = new ArrayList<>();
            updateOnlineCount();
            if (this.chatInfo == null) {
                this.chatInfo = getMessagesController().getChatFull(this.chat_id);
            }
            if (ChatObject.isChannel(this.currentChat)) {
                MessagesController.getInstance(this.currentAccount).loadFullChat(this.chat_id, this.classGuid, true);
            } else if (this.chatInfo == null) {
                this.chatInfo = getMessagesStorage().loadChatInfo(this.chat_id, (CountDownLatch) null, false, false);
            }
        }
        this.sharedMediaData = new MediaActivity.SharedMediaData[5];
        int i = 0;
        while (true) {
            MediaActivity.SharedMediaData[] sharedMediaDataArr = this.sharedMediaData;
            if (i < sharedMediaDataArr.length) {
                sharedMediaDataArr[i] = new MediaActivity.SharedMediaData();
                this.sharedMediaData[i].setMaxId(0, this.dialog_id != 0 ? Integer.MIN_VALUE : Integer.MAX_VALUE);
                i++;
            } else {
                loadMediaCounts();
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.mediaCountDidLoad);
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.mediaCountsDidLoad);
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.mediaDidLoad);
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didReceiveNewMessages);
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagesDeleted);
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
                NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
                updateRowsIds();
                return true;
            }
        }
    }

    public /* synthetic */ void lambda$onFragmentCreate$0$ProfileActivity(CountDownLatch countDownLatch) {
        this.currentChat = MessagesStorage.getInstance(this.currentAccount).getChat(this.chat_id);
        countDownLatch.countDown();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.mediaCountDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.mediaCountsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.mediaDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didReceiveNewMessages);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
        ProfileGalleryView profileGalleryView = this.avatarsViewPager;
        if (profileGalleryView != null) {
            profileGalleryView.onDestroy();
        }
        if (this.user_id != 0) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactsDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.encryptedChatCreated);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.encryptedChatUpdated);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.blockedUsersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.botInfoDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.userInfoDidLoad);
            MessagesController.getInstance(this.currentAccount).cancelLoadFullUser(this.user_id);
        } else if (this.chat_id != 0) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatOnlineCountDidLoad);
        }
    }

    /* access modifiers changed from: protected */
    public ActionBar createActionBar(Context context) {
        AnonymousClass2 r0 = new ActionBar(context) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                return super.onTouchEvent(motionEvent);
            }
        };
        boolean z = false;
        r0.setBackgroundColor(0);
        r0.setItemsBackgroundColor(AvatarDrawable.getButtonColorForId((this.user_id != 0 || (ChatObject.isChannel(this.chat_id, this.currentAccount) && !this.currentChat.megagroup)) ? 5 : this.chat_id), false);
        r0.setItemsColor(Theme.getColor("actionBarDefaultIcon"), false);
        r0.setItemsColor(Theme.getColor("actionBarActionModeDefaultIcon"), true);
        r0.setBackButtonDrawable(new BackDrawable(false));
        r0.setCastShadows(false);
        r0.setAddToContainer(false);
        if (Build.VERSION.SDK_INT >= 21 && !AndroidUtilities.isTablet()) {
            z = true;
        }
        r0.setOccupyStatusBar(z);
        return r0;
    }

    public View createView(Context context) {
        FrameLayout frameLayout;
        Context context2 = context;
        Theme.createProfileResources(context);
        this.hasOwnBackground = true;
        this.extraHeight = AndroidUtilities.dpf2(88.0f);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                int i2;
                long j;
                int i3;
                String str;
                int i4 = i;
                if (ProfileActivity.this.getParentActivity() != null) {
                    if (i4 == -1) {
                        ProfileActivity.this.finishFragment();
                        return;
                    }
                    String str2 = null;
                    if (i4 == 2) {
                        TLRPC.User user = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                        if (user != null) {
                            if (!ProfileActivity.this.isBot || MessagesController.isSupportUser(user)) {
                                if (ProfileActivity.this.userBlocked) {
                                    MessagesController.getInstance(ProfileActivity.this.currentAccount).unblockUser(ProfileActivity.this.user_id);
                                    AlertsCreator.showSimpleToast(ProfileActivity.this, LocaleController.getString("UserUnblocked", NUM));
                                } else if (ProfileActivity.this.reportSpam) {
                                    ProfileActivity profileActivity = ProfileActivity.this;
                                    AlertsCreator.showBlockReportSpamAlert(profileActivity, (long) profileActivity.user_id, user, (TLRPC.Chat) null, ProfileActivity.this.currentEncryptedChat, false, (TLRPC.ChatFull) null, new MessagesStorage.IntCallback() {
                                        public final void run(int i) {
                                            ProfileActivity.AnonymousClass3.this.lambda$onItemClick$0$ProfileActivity$3(i);
                                        }
                                    });
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) ProfileActivity.this.getParentActivity());
                                    builder.setTitle(LocaleController.getString("BlockUser", NUM));
                                    builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureBlockContact2", NUM, ContactsController.formatName(user.first_name, user.last_name))));
                                    builder.setPositiveButton(LocaleController.getString("BlockContact", NUM), new DialogInterface.OnClickListener() {
                                        public final void onClick(DialogInterface dialogInterface, int i) {
                                            ProfileActivity.AnonymousClass3.this.lambda$onItemClick$1$ProfileActivity$3(dialogInterface, i);
                                        }
                                    });
                                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                                    AlertDialog create = builder.create();
                                    ProfileActivity.this.showDialog(create);
                                    TextView textView = (TextView) create.getButton(-1);
                                    if (textView != null) {
                                        textView.setTextColor(Theme.getColor("dialogTextRed2"));
                                    }
                                }
                            } else if (!ProfileActivity.this.userBlocked) {
                                MessagesController.getInstance(ProfileActivity.this.currentAccount).blockUser(ProfileActivity.this.user_id);
                            } else {
                                MessagesController.getInstance(ProfileActivity.this.currentAccount).unblockUser(ProfileActivity.this.user_id);
                                SendMessagesHelper.getInstance(ProfileActivity.this.currentAccount).sendMessage("/start", (long) ProfileActivity.this.user_id, (MessageObject) null, (TLRPC.WebPage) null, false, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                                ProfileActivity.this.finishFragment();
                            }
                        }
                    } else if (i4 == 1) {
                        TLRPC.User user2 = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                        Bundle bundle = new Bundle();
                        bundle.putInt("user_id", user2.id);
                        bundle.putBoolean("addContact", true);
                        ProfileActivity.this.presentFragment(new ContactAddActivity(bundle));
                    } else if (i4 == 3) {
                        Bundle bundle2 = new Bundle();
                        bundle2.putBoolean("onlySelect", true);
                        bundle2.putString("selectAlertString", LocaleController.getString("SendContactToText", NUM));
                        bundle2.putString("selectAlertStringGroup", LocaleController.getString("SendContactToGroupText", NUM));
                        DialogsActivity dialogsActivity = new DialogsActivity(bundle2);
                        dialogsActivity.setDelegate(ProfileActivity.this);
                        ProfileActivity.this.presentFragment(dialogsActivity);
                    } else if (i4 == 4) {
                        Bundle bundle3 = new Bundle();
                        bundle3.putInt("user_id", ProfileActivity.this.user_id);
                        ProfileActivity.this.presentFragment(new ContactAddActivity(bundle3));
                    } else if (i4 == 5) {
                        TLRPC.User user3 = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                        if (user3 != null && ProfileActivity.this.getParentActivity() != null) {
                            AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) ProfileActivity.this.getParentActivity());
                            builder2.setTitle(LocaleController.getString("DeleteContact", NUM));
                            builder2.setMessage(LocaleController.getString("AreYouSureDeleteContact", NUM));
                            builder2.setPositiveButton(LocaleController.getString("Delete", NUM), new DialogInterface.OnClickListener(user3) {
                                private final /* synthetic */ TLRPC.User f$1;

                                {
                                    this.f$1 = r2;
                                }

                                public final void onClick(DialogInterface dialogInterface, int i) {
                                    ProfileActivity.AnonymousClass3.this.lambda$onItemClick$2$ProfileActivity$3(this.f$1, dialogInterface, i);
                                }
                            });
                            builder2.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                            AlertDialog create2 = builder2.create();
                            ProfileActivity.this.showDialog(create2);
                            TextView textView2 = (TextView) create2.getButton(-1);
                            if (textView2 != null) {
                                textView2.setTextColor(Theme.getColor("dialogTextRed2"));
                            }
                        }
                    } else if (i4 == 7) {
                        ProfileActivity.this.leaveChatPressed();
                    } else if (i4 == 12) {
                        Bundle bundle4 = new Bundle();
                        bundle4.putInt("chat_id", ProfileActivity.this.chat_id);
                        ChatEditActivity chatEditActivity = new ChatEditActivity(bundle4);
                        chatEditActivity.setInfo(ProfileActivity.this.chatInfo);
                        ProfileActivity.this.presentFragment(chatEditActivity);
                    } else if (i4 == 9) {
                        TLRPC.User user4 = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                        if (user4 != null) {
                            Bundle bundle5 = new Bundle();
                            bundle5.putBoolean("onlySelect", true);
                            bundle5.putInt("dialogsType", 2);
                            bundle5.putString("addToGroupAlertString", LocaleController.formatString("AddToTheGroupAlertText", NUM, UserObject.getUserName(user4), "%1$s"));
                            DialogsActivity dialogsActivity2 = new DialogsActivity(bundle5);
                            dialogsActivity2.setDelegate(new DialogsActivity.DialogsActivityDelegate(user4) {
                                private final /* synthetic */ TLRPC.User f$1;

                                {
                                    this.f$1 = r2;
                                }

                                public final void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
                                    ProfileActivity.AnonymousClass3.this.lambda$onItemClick$3$ProfileActivity$3(this.f$1, dialogsActivity, arrayList, charSequence, z);
                                }
                            });
                            ProfileActivity.this.presentFragment(dialogsActivity2);
                        }
                    } else if (i4 == 10) {
                        try {
                            if (ProfileActivity.this.user_id != 0) {
                                TLRPC.User user5 = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                                if (user5 != null) {
                                    if (ProfileActivity.this.botInfo == null || ProfileActivity.this.userInfo == null || TextUtils.isEmpty(ProfileActivity.this.userInfo.about)) {
                                        str = String.format("https://" + MessagesController.getInstance(ProfileActivity.this.currentAccount).linkPrefix + "/%s", new Object[]{user5.username});
                                    } else {
                                        str = String.format("%s https://" + MessagesController.getInstance(ProfileActivity.this.currentAccount).linkPrefix + "/%s", new Object[]{ProfileActivity.this.userInfo.about, user5.username});
                                    }
                                    str2 = str;
                                } else {
                                    return;
                                }
                            } else if (ProfileActivity.this.chat_id != 0) {
                                TLRPC.Chat chat = MessagesController.getInstance(ProfileActivity.this.currentAccount).getChat(Integer.valueOf(ProfileActivity.this.chat_id));
                                if (chat != null) {
                                    if (ProfileActivity.this.chatInfo == null || TextUtils.isEmpty(ProfileActivity.this.chatInfo.about)) {
                                        str2 = String.format("https://" + MessagesController.getInstance(ProfileActivity.this.currentAccount).linkPrefix + "/%s", new Object[]{chat.username});
                                    } else {
                                        str2 = String.format("%s\nhttps://" + MessagesController.getInstance(ProfileActivity.this.currentAccount).linkPrefix + "/%s", new Object[]{ProfileActivity.this.chatInfo.about, chat.username});
                                    }
                                } else {
                                    return;
                                }
                            }
                            if (!TextUtils.isEmpty(str2)) {
                                Intent intent = new Intent("android.intent.action.SEND");
                                intent.setType("text/plain");
                                intent.putExtra("android.intent.extra.TEXT", str2);
                                ProfileActivity.this.startActivityForResult(Intent.createChooser(intent, LocaleController.getString("BotShare", NUM)), 500);
                            }
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    } else if (i4 == 14) {
                        try {
                            if (ProfileActivity.this.currentEncryptedChat != null) {
                                j = ((long) ProfileActivity.this.currentEncryptedChat.id) << 32;
                            } else {
                                if (ProfileActivity.this.user_id != 0) {
                                    i3 = ProfileActivity.this.user_id;
                                } else if (ProfileActivity.this.chat_id != 0) {
                                    i3 = -ProfileActivity.this.chat_id;
                                } else {
                                    return;
                                }
                                j = (long) i3;
                            }
                            MediaDataController.getInstance(ProfileActivity.this.currentAccount).installShortcut(j);
                        } catch (Exception e2) {
                            FileLog.e((Throwable) e2);
                        }
                    } else if (i4 == 15) {
                        TLRPC.User user6 = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                        if (user6 != null) {
                            VoIPHelper.startCall(user6, ProfileActivity.this.getParentActivity(), ProfileActivity.this.userInfo);
                        }
                    } else if (i4 == 17) {
                        Bundle bundle6 = new Bundle();
                        bundle6.putInt("chat_id", ProfileActivity.this.chat_id);
                        bundle6.putInt("type", 2);
                        bundle6.putBoolean("open_search", true);
                        ChatUsersActivity chatUsersActivity = new ChatUsersActivity(bundle6);
                        chatUsersActivity.setInfo(ProfileActivity.this.chatInfo);
                        ProfileActivity.this.presentFragment(chatUsersActivity);
                    } else if (i4 == 18) {
                        ProfileActivity.this.openAddMember();
                    } else if (i4 == 19) {
                        if (ProfileActivity.this.user_id != 0) {
                            i2 = ProfileActivity.this.user_id;
                        } else {
                            i2 = -ProfileActivity.this.chat_id;
                        }
                        AlertDialog[] alertDialogArr = {new AlertDialog(ProfileActivity.this.getParentActivity(), 3)};
                        TLRPC.TL_messages_getStatsURL tL_messages_getStatsURL = new TLRPC.TL_messages_getStatsURL();
                        tL_messages_getStatsURL.peer = MessagesController.getInstance(ProfileActivity.this.currentAccount).getInputPeer(i2);
                        tL_messages_getStatsURL.dark = Theme.getCurrentTheme().isDark();
                        tL_messages_getStatsURL.params = "";
                        int sendRequest = ConnectionsManager.getInstance(ProfileActivity.this.currentAccount).sendRequest(tL_messages_getStatsURL, new RequestDelegate(alertDialogArr) {
                            private final /* synthetic */ AlertDialog[] f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                ProfileActivity.AnonymousClass3.this.lambda$onItemClick$5$ProfileActivity$3(this.f$1, tLObject, tL_error);
                            }
                        });
                        if (alertDialogArr[0] != null) {
                            alertDialogArr[0].setOnCancelListener(new DialogInterface.OnCancelListener(sendRequest) {
                                private final /* synthetic */ int f$1;

                                {
                                    this.f$1 = r2;
                                }

                                public final void onCancel(DialogInterface dialogInterface) {
                                    ProfileActivity.AnonymousClass3.this.lambda$onItemClick$6$ProfileActivity$3(this.f$1, dialogInterface);
                                }
                            });
                            ProfileActivity.this.showDialog(alertDialogArr[0]);
                        }
                    }
                }
            }

            public /* synthetic */ void lambda$onItemClick$0$ProfileActivity$3(int i) {
                if (i == 1) {
                    NotificationCenter.getInstance(ProfileActivity.this.currentAccount).removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
                    NotificationCenter.getInstance(ProfileActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                    boolean unused = ProfileActivity.this.playProfileAnimation = false;
                    ProfileActivity.this.finishFragment();
                    return;
                }
                ProfileActivity.this.getNotificationCenter().postNotificationName(NotificationCenter.peerSettingsDidLoad, Long.valueOf((long) ProfileActivity.this.user_id));
            }

            public /* synthetic */ void lambda$onItemClick$1$ProfileActivity$3(DialogInterface dialogInterface, int i) {
                MessagesController.getInstance(ProfileActivity.this.currentAccount).blockUser(ProfileActivity.this.user_id);
                AlertsCreator.showSimpleToast(ProfileActivity.this, LocaleController.getString("UserBlocked", NUM));
            }

            public /* synthetic */ void lambda$onItemClick$2$ProfileActivity$3(TLRPC.User user, DialogInterface dialogInterface, int i) {
                ArrayList arrayList = new ArrayList();
                arrayList.add(user);
                ContactsController.getInstance(ProfileActivity.this.currentAccount).deleteContact(arrayList);
            }

            public /* synthetic */ void lambda$onItemClick$3$ProfileActivity$3(TLRPC.User user, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
                long longValue = ((Long) arrayList.get(0)).longValue();
                Bundle bundle = new Bundle();
                bundle.putBoolean("scrollToTopOnResume", true);
                int i = -((int) longValue);
                bundle.putInt("chat_id", i);
                if (MessagesController.getInstance(ProfileActivity.this.currentAccount).checkCanOpenChat(bundle, dialogsActivity)) {
                    NotificationCenter.getInstance(ProfileActivity.this.currentAccount).removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
                    NotificationCenter.getInstance(ProfileActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                    MessagesController.getInstance(ProfileActivity.this.currentAccount).addUserToChat(i, user, (TLRPC.ChatFull) null, 0, (String) null, ProfileActivity.this, (Runnable) null);
                    ProfileActivity.this.presentFragment(new ChatActivity(bundle), true);
                    ProfileActivity.this.removeSelfFromStack();
                }
            }

            public /* synthetic */ void lambda$onItemClick$5$ProfileActivity$3(AlertDialog[] alertDialogArr, TLObject tLObject, TLRPC.TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new Runnable(alertDialogArr, tLObject) {
                    private final /* synthetic */ AlertDialog[] f$1;
                    private final /* synthetic */ TLObject f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        ProfileActivity.AnonymousClass3.this.lambda$null$4$ProfileActivity$3(this.f$1, this.f$2);
                    }
                });
            }

            public /* synthetic */ void lambda$null$4$ProfileActivity$3(AlertDialog[] alertDialogArr, TLObject tLObject) {
                try {
                    alertDialogArr[0].dismiss();
                } catch (Throwable unused) {
                }
                alertDialogArr[0] = null;
                if (tLObject != null) {
                    ProfileActivity profileActivity = ProfileActivity.this;
                    profileActivity.presentFragment(new WebviewActivity(((TLRPC.TL_statsURL) tLObject).url, (long) (-profileActivity.chat_id)));
                }
            }

            public /* synthetic */ void lambda$onItemClick$6$ProfileActivity$3(int i, DialogInterface dialogInterface) {
                ConnectionsManager.getInstance(ProfileActivity.this.currentAccount).cancelRequest(i, true);
            }
        });
        createActionBarMenu();
        this.listAdapter = new ListAdapter(context2);
        this.avatarDrawable = new AvatarDrawable();
        this.avatarDrawable.setProfile(true);
        this.fragmentView = new FrameLayout(context2) {
            private boolean firstLayout = true;
            private boolean ignoreLayout;

            public boolean hasOverlappingRendering() {
                return false;
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                boolean z;
                int i3;
                int i4;
                int i5;
                int i6;
                boolean z2;
                int currentActionBarHeight = (ProfileActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
                if (ProfileActivity.this.listView != null) {
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) ProfileActivity.this.listView.getLayoutParams();
                    if (layoutParams.topMargin != currentActionBarHeight) {
                        layoutParams.topMargin = currentActionBarHeight;
                    }
                }
                super.onMeasure(i, i2);
                int currentActionBarHeight2 = ActionBar.getCurrentActionBarHeight() + (ProfileActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0);
                if (ProfileActivity.this.lastMeasuredContentWidth != getMeasuredWidth()) {
                    z = ProfileActivity.this.lastMeasuredContentWidth != 0;
                    int unused = ProfileActivity.this.listContentHeight = 0;
                    int itemCount = ProfileActivity.this.listAdapter.getItemCount();
                    int unused2 = ProfileActivity.this.lastMeasuredContentWidth = getMeasuredWidth();
                    int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), NUM);
                    int makeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(0, 0);
                    for (int i7 = 0; i7 < itemCount; i7++) {
                        RecyclerView.ViewHolder createViewHolder = ProfileActivity.this.listAdapter.createViewHolder((ViewGroup) null, ProfileActivity.this.listAdapter.getItemViewType(i7));
                        ProfileActivity.this.listAdapter.onBindViewHolder(createViewHolder, i7);
                        createViewHolder.itemView.measure(makeMeasureSpec, makeMeasureSpec2);
                        ProfileActivity profileActivity = ProfileActivity.this;
                        int unused3 = profileActivity.listContentHeight = profileActivity.listContentHeight + createViewHolder.itemView.getMeasuredHeight();
                        if (ProfileActivity.this.listContentHeight + currentActionBarHeight2 + AndroidUtilities.dp(88.0f) >= ProfileActivity.this.fragmentView.getMeasuredHeight()) {
                            break;
                        }
                    }
                } else {
                    z = false;
                }
                if (!ProfileActivity.this.openAnimationInProgress && !this.firstLayout) {
                    this.ignoreLayout = true;
                    if (!ProfileActivity.this.isInLandscapeMode) {
                        i3 = ProfileActivity.this.listView.getMeasuredWidth() - currentActionBarHeight2;
                        i4 = Math.max(0, ProfileActivity.this.fragmentView.getMeasuredHeight() - ((ProfileActivity.this.listContentHeight + currentActionBarHeight2) + AndroidUtilities.dp(88.0f)));
                    } else {
                        i3 = AndroidUtilities.dp(88.0f);
                        i4 = 0;
                    }
                    if (ProfileActivity.this.banFromGroup != 0) {
                        i4 += AndroidUtilities.dp(48.0f);
                        ProfileActivity.this.listView.setBottomGlowOffset(AndroidUtilities.dp(48.0f));
                    } else {
                        ProfileActivity.this.listView.setBottomGlowOffset(0);
                    }
                    int paddingTop = ProfileActivity.this.listView.getPaddingTop();
                    View childAt = ProfileActivity.this.listView.getChildAt(0);
                    if (childAt != null) {
                        RecyclerView.ViewHolder findContainingViewHolder = ProfileActivity.this.listView.findContainingViewHolder(childAt);
                        int adapterPosition = findContainingViewHolder.getAdapterPosition();
                        i5 = adapterPosition == -1 ? findContainingViewHolder.getPosition() : adapterPosition;
                        i6 = childAt.getTop();
                    } else {
                        i6 = 0;
                        i5 = -1;
                    }
                    if (z || i5 == -1) {
                        z2 = false;
                    } else {
                        ProfileActivity.this.layoutManager.scrollToPositionWithOffset(i5, i6 - i3);
                        z2 = true;
                    }
                    if (!(paddingTop == i3 && ProfileActivity.this.listView.getPaddingBottom() == i4)) {
                        ProfileActivity.this.listView.setPadding(0, i3, 0, i4);
                        z2 = true;
                    }
                    if (z2) {
                        measureChildWithMargins(ProfileActivity.this.listView, i, 0, i2, 0);
                        ProfileActivity.this.listView.layout(0, currentActionBarHeight, ProfileActivity.this.listView.getMeasuredWidth(), ProfileActivity.this.listView.getMeasuredHeight() + currentActionBarHeight);
                    }
                    this.ignoreLayout = false;
                }
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                this.firstLayout = false;
                ProfileActivity.this.checkListViewScroll();
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        FrameLayout frameLayout2 = (FrameLayout) this.fragmentView;
        this.listView = new RecyclerListView(context2) {
            private final Paint paint = new Paint();
            private VelocityTracker velocityTracker;

            public boolean hasOverlappingRendering() {
                return false;
            }

            /* JADX WARNING: Code restructure failed: missing block: B:27:0x00a0, code lost:
                if (r0.itemView.getBottom() >= r1) goto L_0x00a2;
             */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onDraw(android.graphics.Canvas r15) {
                /*
                    r14 = this;
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    int r0 = r0.lastSectionRow
                    r1 = -1
                    if (r0 == r1) goto L_0x0015
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    int r0 = r0.lastSectionRow
                    androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r14.findViewHolderForAdapterPosition(r0)
                    goto L_0x008e
                L_0x0015:
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    int r0 = r0.sharedSectionRow
                    if (r0 == r1) goto L_0x003e
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    int r0 = r0.membersSectionRow
                    if (r0 == r1) goto L_0x0033
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    int r0 = r0.membersSectionRow
                    org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                    int r2 = r2.sharedSectionRow
                    if (r0 >= r2) goto L_0x003e
                L_0x0033:
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    int r0 = r0.sharedSectionRow
                    androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r14.findViewHolderForAdapterPosition(r0)
                    goto L_0x008e
                L_0x003e:
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    int r0 = r0.membersSectionRow
                    if (r0 == r1) goto L_0x0067
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    int r0 = r0.sharedSectionRow
                    if (r0 == r1) goto L_0x005c
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    int r0 = r0.membersSectionRow
                    org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                    int r2 = r2.sharedSectionRow
                    if (r0 <= r2) goto L_0x0067
                L_0x005c:
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    int r0 = r0.membersSectionRow
                    androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r14.findViewHolderForAdapterPosition(r0)
                    goto L_0x008e
                L_0x0067:
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    int r0 = r0.settingsSectionRow
                    if (r0 == r1) goto L_0x007a
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    int r0 = r0.settingsSectionRow
                    androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r14.findViewHolderForAdapterPosition(r0)
                    goto L_0x008e
                L_0x007a:
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    int r0 = r0.infoSectionRow
                    if (r0 == r1) goto L_0x008d
                    org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                    int r0 = r0.infoSectionRow
                    androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r14.findViewHolderForAdapterPosition(r0)
                    goto L_0x008e
                L_0x008d:
                    r0 = 0
                L_0x008e:
                    int r1 = r14.getMeasuredHeight()
                    if (r0 == 0) goto L_0x00a2
                    android.view.View r2 = r0.itemView
                    int r2 = r2.getBottom()
                    android.view.View r0 = r0.itemView
                    int r0 = r0.getBottom()
                    if (r0 < r1) goto L_0x00a3
                L_0x00a2:
                    r2 = r1
                L_0x00a3:
                    android.graphics.Paint r0 = r14.paint
                    java.lang.String r3 = "windowBackgroundWhite"
                    int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                    r0.setColor(r3)
                    r5 = 0
                    r6 = 0
                    int r0 = r14.getMeasuredWidth()
                    float r7 = (float) r0
                    float r10 = (float) r2
                    android.graphics.Paint r9 = r14.paint
                    r4 = r15
                    r8 = r10
                    r4.drawRect(r5, r6, r7, r8, r9)
                    if (r2 == r1) goto L_0x00d9
                    android.graphics.Paint r0 = r14.paint
                    java.lang.String r2 = "windowBackgroundGray"
                    int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                    r0.setColor(r2)
                    r9 = 0
                    int r0 = r14.getMeasuredWidth()
                    float r11 = (float) r0
                    float r12 = (float) r1
                    android.graphics.Paint r13 = r14.paint
                    r8 = r15
                    r8.drawRect(r9, r10, r11, r12, r13)
                L_0x00d9:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.AnonymousClass5.onDraw(android.graphics.Canvas):void");
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                View findViewByPosition;
                VelocityTracker velocityTracker2;
                int action = motionEvent.getAction();
                if (action == 0) {
                    VelocityTracker velocityTracker3 = this.velocityTracker;
                    if (velocityTracker3 == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    } else {
                        velocityTracker3.clear();
                    }
                    this.velocityTracker.addMovement(motionEvent);
                } else if (action == 2) {
                    VelocityTracker velocityTracker4 = this.velocityTracker;
                    if (velocityTracker4 != null) {
                        velocityTracker4.addMovement(motionEvent);
                        this.velocityTracker.computeCurrentVelocity(1000);
                        float unused = ProfileActivity.this.listViewVelocityY = this.velocityTracker.getYVelocity(motionEvent.getPointerId(motionEvent.getActionIndex()));
                    }
                } else if ((action == 1 || action == 3) && (velocityTracker2 = this.velocityTracker) != null) {
                    velocityTracker2.recycle();
                    this.velocityTracker = null;
                }
                boolean onTouchEvent = super.onTouchEvent(motionEvent);
                if ((action == 1 || action == 3) && ProfileActivity.this.allowPullingDown && (findViewByPosition = ProfileActivity.this.layoutManager.findViewByPosition(0)) != null) {
                    if (ProfileActivity.this.isPulledDown) {
                        ProfileActivity.this.listView.smoothScrollBy(0, (findViewByPosition.getTop() - ProfileActivity.this.listView.getMeasuredWidth()) + ActionBar.getCurrentActionBarHeight() + (ProfileActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0), CubicBezierInterpolator.EASE_OUT_QUINT);
                    } else {
                        ProfileActivity.this.listView.smoothScrollBy(0, findViewByPosition.getTop() - AndroidUtilities.dp(88.0f), CubicBezierInterpolator.EASE_OUT_QUINT);
                    }
                }
                return onTouchEvent;
            }
        };
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setItemAnimator((RecyclerView.ItemAnimator) null);
        this.listView.setLayoutAnimation((LayoutAnimationController) null);
        this.listView.setClipToPadding(false);
        this.layoutManager = new LinearLayoutManager(context2) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }

            public int scrollVerticallyBy(int i, RecyclerView.Recycler recycler, RecyclerView.State state) {
                View findViewByPosition = ProfileActivity.this.layoutManager.findViewByPosition(0);
                if (findViewByPosition != null) {
                    int top = findViewByPosition.getTop() - AndroidUtilities.dp(88.0f);
                    if (ProfileActivity.this.allowPullingDown || top <= i) {
                        if (ProfileActivity.this.allowPullingDown) {
                            if (i >= top) {
                                boolean unused = ProfileActivity.this.allowPullingDown = false;
                            } else if (ProfileActivity.this.listView.getScrollState() == 1 && !ProfileActivity.this.isPulledDown) {
                                i /= 2;
                            }
                        }
                    } else if (ProfileActivity.this.avatarsViewPager.hasImages() && ProfileActivity.this.avatarImage.getImageReceiver().hasNotThumb() && !ProfileActivity.this.isInLandscapeMode && !AndroidUtilities.isTablet()) {
                        boolean unused2 = ProfileActivity.this.allowPullingDown = true;
                    }
                    i = top;
                }
                return super.scrollVerticallyBy(i, recycler, state);
            }
        };
        this.layoutManager.setOrientation(1);
        this.listView.setLayoutManager(this.layoutManager);
        this.listView.setGlowColor(AvatarDrawable.getProfileBackColorForId((this.user_id != 0 || (ChatObject.isChannel(this.chat_id, this.currentAccount) && !this.currentChat.megagroup)) ? 5 : this.chat_id));
        frameLayout2.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListenerExtended) new RecyclerListView.OnItemClickListenerExtended() {
            public final void onItemClick(View view, int i, float f, float f2) {
                ProfileActivity.this.lambda$createView$3$ProfileActivity(view, i, f, f2);
            }
        });
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener() {
            public final boolean onItemClick(View view, int i) {
                return ProfileActivity.this.lambda$createView$6$ProfileActivity(view, i);
            }
        });
        if (this.banFromGroup != 0) {
            TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.banFromGroup));
            if (this.currentChannelParticipant == null) {
                TLRPC.TL_channels_getParticipant tL_channels_getParticipant = new TLRPC.TL_channels_getParticipant();
                tL_channels_getParticipant.channel = MessagesController.getInputChannel(chat);
                tL_channels_getParticipant.user_id = MessagesController.getInstance(this.currentAccount).getInputUser(this.user_id);
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_getParticipant, new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        ProfileActivity.this.lambda$createView$8$ProfileActivity(tLObject, tL_error);
                    }
                });
            }
            AnonymousClass7 r4 = new FrameLayout(context2) {
                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    int intrinsicHeight = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                    Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), intrinsicHeight);
                    Theme.chat_composeShadowDrawable.draw(canvas);
                    canvas.drawRect(0.0f, (float) intrinsicHeight, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
                }
            };
            r4.setWillNotDraw(false);
            frameLayout2.addView(r4, LayoutHelper.createFrame(-1, 51, 83));
            r4.setOnClickListener(new View.OnClickListener(chat) {
                private final /* synthetic */ TLRPC.Chat f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    ProfileActivity.this.lambda$createView$9$ProfileActivity(this.f$1, view);
                }
            });
            TextView textView = new TextView(context2);
            textView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText"));
            textView.setTextSize(1, 15.0f);
            textView.setGravity(17);
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView.setText(LocaleController.getString("BanFromTheGroup", NUM));
            r4.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 1.0f, 0.0f, 0.0f));
            this.listView.setPadding(0, AndroidUtilities.dp(88.0f), 0, AndroidUtilities.dp(48.0f));
            this.listView.setBottomGlowOffset(AndroidUtilities.dp(48.0f));
        } else {
            this.listView.setPadding(0, AndroidUtilities.dp(88.0f), 0, 0);
        }
        this.topView = new TopView(context2);
        this.topView.setBackgroundColor(Theme.getColor("avatar_backgroundActionBarBlue"));
        frameLayout2.addView(this.topView);
        this.avatarImage = new AvatarImageView(context2);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(21.0f));
        this.avatarImage.setPivotX(0.0f);
        this.avatarImage.setPivotY(0.0f);
        frameLayout2.addView(this.avatarImage, LayoutHelper.createFrame(42, 42.0f, 51, 64.0f, 0.0f, 0.0f, 0.0f));
        this.avatarImage.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ProfileActivity.this.lambda$createView$10$ProfileActivity(view);
            }
        });
        this.avatarImage.setContentDescription(LocaleController.getString("AccDescrProfilePicture", NUM));
        ProfileGalleryView profileGalleryView = this.avatarsViewPager;
        if (profileGalleryView != null) {
            profileGalleryView.onDestroy();
        }
        int i = this.user_id;
        if (i == 0) {
            i = -this.chat_id;
        }
        ProfileGalleryView profileGalleryView2 = r1;
        ProfileGalleryView profileGalleryView3 = new ProfileGalleryView(context, (long) i, this.actionBar, this.listView, this.avatarImage, getClassGuid());
        this.avatarsViewPager = profileGalleryView2;
        frameLayout2.addView(this.avatarsViewPager);
        this.overlaysView = new OverlaysView(context2);
        frameLayout2.addView(this.overlaysView);
        this.avatarsViewPagerIndicatorView = new PagerIndicatorView(context2);
        frameLayout2.addView(this.avatarsViewPagerIndicatorView, LayoutHelper.createFrame(-1, -1.0f));
        frameLayout2.addView(this.actionBar);
        int i2 = 0;
        while (i2 < 2) {
            if (this.playProfileAnimation || i2 != 0) {
                this.nameTextView[i2] = new SimpleTextView(context2);
                if (i2 == 1) {
                    this.nameTextView[i2].setTextColor(Theme.getColor("profile_title"));
                } else {
                    this.nameTextView[i2].setTextColor(Theme.getColor("actionBarDefaultTitle"));
                }
                this.nameTextView[i2].setTextSize(18);
                this.nameTextView[i2].setGravity(3);
                this.nameTextView[i2].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.nameTextView[i2].setLeftDrawableTopPadding(-AndroidUtilities.dp(1.3f));
                this.nameTextView[i2].setPivotX(0.0f);
                this.nameTextView[i2].setPivotY(0.0f);
                this.nameTextView[i2].setAlpha(i2 == 0 ? 0.0f : 1.0f);
                if (i2 == 1) {
                    this.nameTextView[i2].setScrollNonFitText(true);
                    this.nameTextView[i2].setBackgroundColor(Theme.getColor("avatar_backgroundActionBarBlue"));
                }
                frameLayout2.addView(this.nameTextView[i2], LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, i2 == 0 ? 48.0f : 0.0f, 0.0f));
                this.onlineTextView[i2] = new SimpleTextView(context2);
                this.onlineTextView[i2].setTextColor(Theme.getColor("avatar_subtitleInProfileBlue"));
                this.onlineTextView[i2].setTextSize(14);
                this.onlineTextView[i2].setGravity(3);
                this.onlineTextView[i2].setAlpha(i2 == 0 ? 0.0f : 1.0f);
                frameLayout2.addView(this.onlineTextView[i2], LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, i2 == 0 ? 48.0f : 8.0f, 0.0f));
            }
            i2++;
        }
        updateProfileData();
        if (this.user_id != 0) {
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
            this.writeButton.setContentDescription(LocaleController.getString("AccDescrOpenChat", NUM));
            this.writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("profile_actionIcon"), PorterDuff.Mode.MULTIPLY));
            this.writeButton.setScaleType(ImageView.ScaleType.CENTER);
            if (Build.VERSION.SDK_INT >= 21) {
                StateListAnimator stateListAnimator = new StateListAnimator();
                frameLayout = frameLayout2;
                stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.writeButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
                stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(this.writeButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
                this.writeButton.setStateListAnimator(stateListAnimator);
                this.writeButton.setOutlineProvider(new ViewOutlineProvider() {
                    @SuppressLint({"NewApi"})
                    public void getOutline(View view, Outline outline) {
                        outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                    }
                });
            } else {
                frameLayout = frameLayout2;
            }
            frameLayout.addView(this.writeButton, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56 : 60, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, 53, 0.0f, 0.0f, 16.0f, 0.0f));
            this.writeButton.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ProfileActivity.this.lambda$createView$11$ProfileActivity(view);
                }
            });
        } else {
            frameLayout = frameLayout2;
        }
        needLayout();
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                ProfileActivity.this.checkListViewScroll();
                if (ProfileActivity.this.participantsMap != null && !ProfileActivity.this.usersEndReached && ProfileActivity.this.layoutManager.findLastVisibleItemPosition() > ProfileActivity.this.membersEndRow - 8) {
                    ProfileActivity.this.getChannelParticipants(false);
                }
            }
        });
        this.undoView = new UndoView(context2);
        frameLayout.addView(this.undoView, LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$3$ProfileActivity(View view, int i, float f, float f2) {
        int i2;
        int i3 = i;
        if (getParentActivity() != null) {
            int i4 = 2;
            long j = 0;
            if (i3 == this.photosRow || i3 == this.filesRow || i3 == this.linksRow || i3 == this.audioRow || i3 == this.voiceRow) {
                if (i3 == this.photosRow) {
                    i4 = 0;
                } else if (i3 == this.filesRow) {
                    i4 = 1;
                } else if (i3 == this.linksRow) {
                    i4 = 3;
                } else if (i3 == this.audioRow) {
                    i4 = 4;
                }
                Bundle bundle = new Bundle();
                int i5 = this.user_id;
                if (i5 != 0) {
                    long j2 = this.dialog_id;
                    if (j2 == 0) {
                        j2 = (long) i5;
                    }
                    bundle.putLong("dialog_id", j2);
                } else {
                    bundle.putLong("dialog_id", (long) (-this.chat_id));
                }
                int[] iArr = new int[5];
                System.arraycopy(this.lastMediaCount, 0, iArr, 0, iArr.length);
                this.mediaActivity = new MediaActivity(bundle, iArr, this.sharedMediaData, i4);
                this.mediaActivity.setChatInfo(this.chatInfo);
                presentFragment(this.mediaActivity);
            } else if (i3 == this.groupsInCommonRow) {
                presentFragment(new CommonGroupsActivity(this.user_id));
            } else if (i3 == this.settingsKeyRow) {
                Bundle bundle2 = new Bundle();
                bundle2.putInt("chat_id", (int) (this.dialog_id >> 32));
                presentFragment(new IdenticonActivity(bundle2));
            } else if (i3 == this.settingsTimerRow) {
                showDialog(AlertsCreator.createTTLAlert(getParentActivity(), this.currentEncryptedChat).create());
            } else if (i3 == this.notificationsRow) {
                long j3 = this.dialog_id;
                if (j3 == 0) {
                    int i6 = this.user_id;
                    if (i6 == 0) {
                        i6 = -this.chat_id;
                    }
                    j3 = (long) i6;
                }
                long j4 = j3;
                if ((!LocaleController.isRTL || f > ((float) AndroidUtilities.dp(76.0f))) && (LocaleController.isRTL || f < ((float) (view.getMeasuredWidth() - AndroidUtilities.dp(76.0f))))) {
                    AlertsCreator.showCustomNotificationsDialog(this, j4, -1, (ArrayList<NotificationsSettingsActivity.NotificationException>) null, this.currentAccount, new MessagesStorage.IntCallback() {
                        public final void run(int i) {
                            ProfileActivity.this.lambda$null$1$ProfileActivity(i);
                        }
                    });
                    return;
                }
                NotificationsCheckCell notificationsCheckCell = (NotificationsCheckCell) view;
                boolean z = !notificationsCheckCell.isChecked();
                boolean isGlobalNotificationsEnabled = NotificationsController.getInstance(this.currentAccount).isGlobalNotificationsEnabled(j4);
                if (z) {
                    SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                    if (isGlobalNotificationsEnabled) {
                        edit.remove("notify2_" + j4);
                    } else {
                        edit.putInt("notify2_" + j4, 0);
                    }
                    MessagesStorage.getInstance(this.currentAccount).setDialogFlags(j4, 0);
                    edit.commit();
                    TLRPC.Dialog dialog = MessagesController.getInstance(this.currentAccount).dialogs_dict.get(j4);
                    if (dialog != null) {
                        dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
                    }
                    NotificationsController.getInstance(this.currentAccount).updateServerNotificationsSettings(j4);
                } else {
                    SharedPreferences.Editor edit2 = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                    if (!isGlobalNotificationsEnabled) {
                        edit2.remove("notify2_" + j4);
                    } else {
                        edit2.putInt("notify2_" + j4, 2);
                        j = 1;
                    }
                    NotificationsController.getInstance(this.currentAccount).removeNotificationsForDialog(j4);
                    MessagesStorage.getInstance(this.currentAccount).setDialogFlags(j4, j);
                    edit2.commit();
                    TLRPC.Dialog dialog2 = MessagesController.getInstance(this.currentAccount).dialogs_dict.get(j4);
                    if (dialog2 != null) {
                        dialog2.notify_settings = new TLRPC.TL_peerNotifySettings();
                        if (isGlobalNotificationsEnabled) {
                            dialog2.notify_settings.mute_until = Integer.MAX_VALUE;
                        }
                    }
                    NotificationsController.getInstance(this.currentAccount).updateServerNotificationsSettings(j4);
                }
                notificationsCheckCell.setChecked(z);
                RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findViewHolderForPosition(this.notificationsRow);
                if (holder != null) {
                    this.listAdapter.onBindViewHolder(holder, this.notificationsRow);
                }
            } else if (i3 == this.startSecretChatRow) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setTitle(LocaleController.getString("AreYouSureSecretChatTitle", NUM));
                builder.setMessage(LocaleController.getString("AreYouSureSecretChat", NUM));
                builder.setPositiveButton(LocaleController.getString("Start", NUM), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        ProfileActivity.this.lambda$null$2$ProfileActivity(dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                showDialog(builder.create());
            } else if (i3 == this.unblockRow) {
                MessagesController.getInstance(this.currentAccount).unblockUser(this.user_id);
                AlertsCreator.showSimpleToast(this, LocaleController.getString("UserUnblocked", NUM));
            } else if (i3 >= this.membersStartRow && i3 < this.membersEndRow) {
                if (!this.sortedUsers.isEmpty()) {
                    i2 = this.chatInfo.participants.participants.get(this.sortedUsers.get(i3 - this.membersStartRow).intValue()).user_id;
                } else {
                    i2 = this.chatInfo.participants.participants.get(i3 - this.membersStartRow).user_id;
                }
                if (i2 != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                    Bundle bundle3 = new Bundle();
                    bundle3.putInt("user_id", i2);
                    presentFragment(new ProfileActivity(bundle3));
                }
            } else if (i3 == this.addMemberRow) {
                openAddMember();
            } else if (i3 == this.usernameRow) {
                if (this.currentChat != null) {
                    try {
                        Intent intent = new Intent("android.intent.action.SEND");
                        intent.setType("text/plain");
                        if (!TextUtils.isEmpty(this.chatInfo.about)) {
                            intent.putExtra("android.intent.extra.TEXT", this.currentChat.title + "\n" + this.chatInfo.about + "\nhttps://" + MessagesController.getInstance(this.currentAccount).linkPrefix + "/" + this.currentChat.username);
                        } else {
                            intent.putExtra("android.intent.extra.TEXT", this.currentChat.title + "\nhttps://" + MessagesController.getInstance(this.currentAccount).linkPrefix + "/" + this.currentChat.username);
                        }
                        getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString("BotShare", NUM)), 500);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
            } else if (i3 == this.locationRow) {
                if (this.chatInfo.location instanceof TLRPC.TL_channelLocation) {
                    LocationActivity locationActivity = new LocationActivity(5);
                    locationActivity.setChatLocation(this.chat_id, (TLRPC.TL_channelLocation) this.chatInfo.location);
                    presentFragment(locationActivity);
                }
            } else if (i3 == this.leaveChannelRow) {
                leaveChatPressed();
            } else if (i3 == this.joinRow) {
                MessagesController.getInstance(this.currentAccount).addUserToChat(this.currentChat.id, UserConfig.getInstance(this.currentAccount).getCurrentUser(), (TLRPC.ChatFull) null, 0, (String) null, this, (Runnable) null);
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.closeSearchByActiveAction, new Object[0]);
            } else if (i3 == this.subscribersRow) {
                Bundle bundle4 = new Bundle();
                bundle4.putInt("chat_id", this.chat_id);
                bundle4.putInt("type", 2);
                ChatUsersActivity chatUsersActivity = new ChatUsersActivity(bundle4);
                chatUsersActivity.setInfo(this.chatInfo);
                presentFragment(chatUsersActivity);
            } else if (i3 == this.administratorsRow) {
                Bundle bundle5 = new Bundle();
                bundle5.putInt("chat_id", this.chat_id);
                bundle5.putInt("type", 1);
                ChatUsersActivity chatUsersActivity2 = new ChatUsersActivity(bundle5);
                chatUsersActivity2.setInfo(this.chatInfo);
                presentFragment(chatUsersActivity2);
            } else if (i3 == this.blockedUsersRow) {
                Bundle bundle6 = new Bundle();
                bundle6.putInt("chat_id", this.chat_id);
                bundle6.putInt("type", 0);
                ChatUsersActivity chatUsersActivity3 = new ChatUsersActivity(bundle6);
                chatUsersActivity3.setInfo(this.chatInfo);
                presentFragment(chatUsersActivity3);
            } else {
                processOnClickOrPress(i);
            }
        }
    }

    public /* synthetic */ void lambda$null$1$ProfileActivity(int i) {
        this.listAdapter.notifyItemChanged(this.notificationsRow);
    }

    public /* synthetic */ void lambda$null$2$ProfileActivity(DialogInterface dialogInterface, int i) {
        this.creatingChat = true;
        SecretChatHelper.getInstance(this.currentAccount).startSecretChat(getParentActivity(), MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id)));
    }

    public /* synthetic */ boolean lambda$createView$6$ProfileActivity(View view, int i) {
        TLRPC.ChatParticipant chatParticipant;
        boolean z;
        TLRPC.ChannelParticipant channelParticipant;
        boolean z2;
        boolean z3;
        boolean z4;
        boolean z5;
        String str;
        int i2;
        if (i < this.membersStartRow || i >= this.membersEndRow) {
            return processOnClickOrPress(i);
        }
        if (getParentActivity() == null) {
            return false;
        }
        if (!this.sortedUsers.isEmpty()) {
            chatParticipant = this.chatInfo.participants.participants.get(this.sortedUsers.get(i - this.membersStartRow).intValue());
        } else {
            chatParticipant = this.chatInfo.participants.participants.get(i - this.membersStartRow);
        }
        TLRPC.ChatParticipant chatParticipant2 = chatParticipant;
        TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(chatParticipant2.user_id));
        if (user == null || chatParticipant2.user_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
            return false;
        }
        this.selectedUser = chatParticipant2.user_id;
        if (ChatObject.isChannel(this.currentChat)) {
            TLRPC.ChannelParticipant channelParticipant2 = ((TLRPC.TL_chatChannelParticipant) chatParticipant2).channelParticipant;
            MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(chatParticipant2.user_id));
            z4 = ChatObject.canAddAdmins(this.currentChat);
            if (z4 && ((channelParticipant2 instanceof TLRPC.TL_channelParticipantCreator) || ((channelParticipant2 instanceof TLRPC.TL_channelParticipantAdmin) && !channelParticipant2.can_edit))) {
                z4 = false;
            }
            z3 = ChatObject.canBlockUsers(this.currentChat) && ((!(channelParticipant2 instanceof TLRPC.TL_channelParticipantAdmin) && !(channelParticipant2 instanceof TLRPC.TL_channelParticipantCreator)) || channelParticipant2.can_edit);
            z2 = channelParticipant2 instanceof TLRPC.TL_channelParticipantAdmin;
            channelParticipant = channelParticipant2;
            z = z3;
        } else {
            TLRPC.Chat chat = this.currentChat;
            z3 = chat.creator || ((chatParticipant2 instanceof TLRPC.TL_chatParticipant) && (ChatObject.canBlockUsers(chat) || chatParticipant2.inviter_id == UserConfig.getInstance(this.currentAccount).getClientUserId()));
            z4 = this.currentChat.creator;
            z2 = chatParticipant2 instanceof TLRPC.TL_chatParticipantAdmin;
            channelParticipant = null;
            z = z4;
        }
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        if (z4) {
            if (z2) {
                i2 = NUM;
                str = "EditAdminRights";
            } else {
                i2 = NUM;
                str = "SetAsAdmin";
            }
            arrayList.add(LocaleController.getString(str, i2));
            arrayList2.add(NUM);
            arrayList3.add(0);
        }
        if (z) {
            arrayList.add(LocaleController.getString("ChangePermissions", NUM));
            arrayList2.add(NUM);
            arrayList3.add(1);
        }
        if (z3) {
            arrayList.add(LocaleController.getString("KickFromGroup", NUM));
            arrayList2.add(NUM);
            arrayList3.add(2);
            z5 = true;
        } else {
            z5 = false;
        }
        if (arrayList.isEmpty()) {
            return false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[0]), AndroidUtilities.toIntArray(arrayList2), new DialogInterface.OnClickListener(arrayList3, channelParticipant, chatParticipant2, user) {
            private final /* synthetic */ ArrayList f$1;
            private final /* synthetic */ TLRPC.ChannelParticipant f$2;
            private final /* synthetic */ TLRPC.ChatParticipant f$3;
            private final /* synthetic */ TLRPC.User f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                ProfileActivity.this.lambda$null$5$ProfileActivity(this.f$1, this.f$2, this.f$3, this.f$4, dialogInterface, i);
            }
        });
        AlertDialog create = builder.create();
        showDialog(create);
        if (z5) {
            create.setItemColor(arrayList.size() - 1, Theme.getColor("dialogTextRed2"), Theme.getColor("dialogRedIcon"));
        }
        return true;
    }

    public /* synthetic */ void lambda$null$5$ProfileActivity(ArrayList arrayList, TLRPC.ChannelParticipant channelParticipant, TLRPC.ChatParticipant chatParticipant, TLRPC.User user, DialogInterface dialogInterface, int i) {
        if (((Integer) arrayList.get(i)).intValue() == 2) {
            kickUser(this.selectedUser);
            return;
        }
        int intValue = ((Integer) arrayList.get(i)).intValue();
        if (intValue == 1 && ((channelParticipant instanceof TLRPC.TL_channelParticipantAdmin) || (chatParticipant instanceof TLRPC.TL_chatParticipantAdmin))) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setMessage(LocaleController.formatString("AdminWillBeRemoved", NUM, ContactsController.formatName(user.first_name, user.last_name)));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener(channelParticipant, intValue, user, chatParticipant) {
                private final /* synthetic */ TLRPC.ChannelParticipant f$1;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ TLRPC.User f$3;
                private final /* synthetic */ TLRPC.ChatParticipant f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    ProfileActivity.this.lambda$null$4$ProfileActivity(this.f$1, this.f$2, this.f$3, this.f$4, dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        } else if (channelParticipant != null) {
            openRightsEdit(intValue, user.id, chatParticipant, channelParticipant.admin_rights, channelParticipant.banned_rights, channelParticipant.rank);
        } else {
            openRightsEdit(intValue, user.id, chatParticipant, (TLRPC.TL_chatAdminRights) null, (TLRPC.TL_chatBannedRights) null, "");
        }
    }

    public /* synthetic */ void lambda$null$4$ProfileActivity(TLRPC.ChannelParticipant channelParticipant, int i, TLRPC.User user, TLRPC.ChatParticipant chatParticipant, DialogInterface dialogInterface, int i2) {
        TLRPC.ChannelParticipant channelParticipant2 = channelParticipant;
        TLRPC.User user2 = user;
        if (channelParticipant2 != null) {
            openRightsEdit(i, user2.id, chatParticipant, channelParticipant2.admin_rights, channelParticipant2.banned_rights, channelParticipant2.rank);
            return;
        }
        openRightsEdit(i, user2.id, chatParticipant, (TLRPC.TL_chatAdminRights) null, (TLRPC.TL_chatBannedRights) null, "");
    }

    public /* synthetic */ void lambda$createView$8$ProfileActivity(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable(tLObject) {
                private final /* synthetic */ TLObject f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ProfileActivity.this.lambda$null$7$ProfileActivity(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$7$ProfileActivity(TLObject tLObject) {
        this.currentChannelParticipant = ((TLRPC.TL_channels_channelParticipant) tLObject).participant;
    }

    public /* synthetic */ void lambda$createView$9$ProfileActivity(TLRPC.Chat chat, View view) {
        int i = this.user_id;
        int i2 = this.banFromGroup;
        TLRPC.TL_chatBannedRights tL_chatBannedRights = chat.default_banned_rights;
        TLRPC.ChannelParticipant channelParticipant = this.currentChannelParticipant;
        ChatRightsEditActivity chatRightsEditActivity = new ChatRightsEditActivity(i, i2, (TLRPC.TL_chatAdminRights) null, tL_chatBannedRights, channelParticipant != null ? channelParticipant.banned_rights : null, "", 1, true, false);
        chatRightsEditActivity.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() {
            public void didSetRights(int i, TLRPC.TL_chatAdminRights tL_chatAdminRights, TLRPC.TL_chatBannedRights tL_chatBannedRights, String str) {
                ProfileActivity.this.removeSelfFromStack();
            }

            public void didChangeOwner(TLRPC.User user) {
                ProfileActivity.this.undoView.showWithAction((long) (-ProfileActivity.this.chat_id), ProfileActivity.this.currentChat.megagroup ? 10 : 9, (Object) user);
            }
        });
        presentFragment(chatRightsEditActivity);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x004c, code lost:
        r3 = org.telegram.messenger.MessagesController.getInstance(r2.currentAccount).getChat(java.lang.Integer.valueOf(r2.chat_id));
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$createView$10$ProfileActivity(android.view.View r3) {
        /*
            r2 = this;
            org.telegram.ui.Components.RecyclerListView r3 = r2.listView
            int r3 = r3.getScrollState()
            r0 = 1
            if (r3 == r0) goto L_0x0086
            int r3 = r2.user_id
            if (r3 == 0) goto L_0x0048
            int r3 = r2.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            int r0 = r2.user_id
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r0)
            org.telegram.tgnet.TLRPC$UserProfilePhoto r0 = r3.photo
            if (r0 == 0) goto L_0x0086
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.photo_big
            if (r0 == 0) goto L_0x0086
            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.getInstance()
            android.app.Activity r1 = r2.getParentActivity()
            r0.setParentActivity(r1)
            org.telegram.tgnet.TLRPC$UserProfilePhoto r0 = r3.photo
            int r1 = r0.dc_id
            if (r1 == 0) goto L_0x003a
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.photo_big
            r0.dc_id = r1
        L_0x003a:
            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.getInstance()
            org.telegram.tgnet.TLRPC$UserProfilePhoto r3 = r3.photo
            org.telegram.tgnet.TLRPC$FileLocation r3 = r3.photo_big
            org.telegram.ui.PhotoViewer$PhotoViewerProvider r1 = r2.provider
            r0.openPhoto(r3, r1)
            goto L_0x0086
        L_0x0048:
            int r3 = r2.chat_id
            if (r3 == 0) goto L_0x0086
            int r3 = r2.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            int r0 = r2.chat_id
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r0)
            org.telegram.tgnet.TLRPC$ChatPhoto r0 = r3.photo
            if (r0 == 0) goto L_0x0086
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.photo_big
            if (r0 == 0) goto L_0x0086
            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.getInstance()
            android.app.Activity r1 = r2.getParentActivity()
            r0.setParentActivity(r1)
            org.telegram.tgnet.TLRPC$ChatPhoto r0 = r3.photo
            int r1 = r0.dc_id
            if (r1 == 0) goto L_0x0079
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.photo_big
            r0.dc_id = r1
        L_0x0079:
            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.getInstance()
            org.telegram.tgnet.TLRPC$ChatPhoto r3 = r3.photo
            org.telegram.tgnet.TLRPC$FileLocation r3 = r3.photo_big
            org.telegram.ui.PhotoViewer$PhotoViewerProvider r1 = r2.provider
            r0.openPhoto(r3, r1)
        L_0x0086:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.lambda$createView$10$ProfileActivity(android.view.View):void");
    }

    public /* synthetic */ void lambda$createView$11$ProfileActivity(View view) {
        if (this.playProfileAnimation) {
            ArrayList<BaseFragment> arrayList = this.parentLayout.fragmentsStack;
            if (arrayList.get(arrayList.size() - 2) instanceof ChatActivity) {
                finishFragment();
                return;
            }
        }
        TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
        if (user != null && !(user instanceof TLRPC.TL_userEmpty)) {
            Bundle bundle = new Bundle();
            bundle.putInt("user_id", this.user_id);
            if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(bundle, this)) {
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                presentFragment(new ChatActivity(bundle), true);
            }
        }
    }

    private void openRightsEdit(final int i, int i2, final TLRPC.ChatParticipant chatParticipant, TLRPC.TL_chatAdminRights tL_chatAdminRights, TLRPC.TL_chatBannedRights tL_chatBannedRights, String str) {
        ChatRightsEditActivity chatRightsEditActivity = new ChatRightsEditActivity(i2, this.chat_id, tL_chatAdminRights, this.currentChat.default_banned_rights, tL_chatBannedRights, str, i, true, false);
        int i3 = i;
        TLRPC.ChatParticipant chatParticipant2 = chatParticipant;
        chatRightsEditActivity.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() {
            public void didSetRights(int i, TLRPC.TL_chatAdminRights tL_chatAdminRights, TLRPC.TL_chatBannedRights tL_chatBannedRights, String str) {
                boolean z;
                TLRPC.ChatParticipant chatParticipant;
                int i2 = i;
                if (i2 == 0) {
                    TLRPC.ChatParticipant chatParticipant2 = chatParticipant;
                    if (chatParticipant2 instanceof TLRPC.TL_chatChannelParticipant) {
                        TLRPC.TL_chatChannelParticipant tL_chatChannelParticipant = (TLRPC.TL_chatChannelParticipant) chatParticipant2;
                        if (i == 1) {
                            tL_chatChannelParticipant.channelParticipant = new TLRPC.TL_channelParticipantAdmin();
                            tL_chatChannelParticipant.channelParticipant.flags |= 4;
                        } else {
                            tL_chatChannelParticipant.channelParticipant = new TLRPC.TL_channelParticipant();
                        }
                        tL_chatChannelParticipant.channelParticipant.inviter_id = UserConfig.getInstance(ProfileActivity.this.currentAccount).getClientUserId();
                        TLRPC.ChannelParticipant channelParticipant = tL_chatChannelParticipant.channelParticipant;
                        TLRPC.ChatParticipant chatParticipant3 = chatParticipant;
                        channelParticipant.user_id = chatParticipant3.user_id;
                        channelParticipant.date = chatParticipant3.date;
                        channelParticipant.banned_rights = tL_chatBannedRights;
                        channelParticipant.admin_rights = tL_chatAdminRights;
                        channelParticipant.rank = str;
                    } else if (chatParticipant2 instanceof TLRPC.ChatParticipant) {
                        if (i == 1) {
                            chatParticipant = new TLRPC.TL_chatParticipantAdmin();
                        } else {
                            chatParticipant = new TLRPC.TL_chatParticipant();
                        }
                        TLRPC.ChatParticipant chatParticipant4 = chatParticipant;
                        chatParticipant.user_id = chatParticipant4.user_id;
                        chatParticipant.date = chatParticipant4.date;
                        chatParticipant.inviter_id = chatParticipant4.inviter_id;
                        int indexOf = ProfileActivity.this.chatInfo.participants.participants.indexOf(chatParticipant);
                        if (indexOf >= 0) {
                            ProfileActivity.this.chatInfo.participants.participants.set(indexOf, chatParticipant);
                        }
                    }
                } else if (i2 == 1 && i == 0 && ProfileActivity.this.currentChat.megagroup && ProfileActivity.this.chatInfo != null && ProfileActivity.this.chatInfo.participants != null) {
                    int i3 = 0;
                    int i4 = 0;
                    while (true) {
                        if (i4 >= ProfileActivity.this.chatInfo.participants.participants.size()) {
                            z = false;
                            break;
                        } else if (((TLRPC.TL_chatChannelParticipant) ProfileActivity.this.chatInfo.participants.participants.get(i4)).channelParticipant.user_id == chatParticipant.user_id) {
                            if (ProfileActivity.this.chatInfo != null) {
                                ProfileActivity.this.chatInfo.participants_count--;
                            }
                            ProfileActivity.this.chatInfo.participants.participants.remove(i4);
                            z = true;
                        } else {
                            i4++;
                        }
                    }
                    if (ProfileActivity.this.chatInfo != null && ProfileActivity.this.chatInfo.participants != null) {
                        while (true) {
                            if (i3 >= ProfileActivity.this.chatInfo.participants.participants.size()) {
                                break;
                            } else if (ProfileActivity.this.chatInfo.participants.participants.get(i3).user_id == chatParticipant.user_id) {
                                ProfileActivity.this.chatInfo.participants.participants.remove(i3);
                                z = true;
                                break;
                            } else {
                                i3++;
                            }
                        }
                    }
                    if (z) {
                        ProfileActivity.this.updateOnlineCount();
                        ProfileActivity.this.updateRowsIds();
                        ProfileActivity.this.listAdapter.notifyDataSetChanged();
                    }
                }
            }

            public void didChangeOwner(TLRPC.User user) {
                ProfileActivity.this.undoView.showWithAction((long) (-ProfileActivity.this.chat_id), ProfileActivity.this.currentChat.megagroup ? 10 : 9, (Object) user);
            }
        });
        presentFragment(chatRightsEditActivity);
    }

    private boolean processOnClickOrPress(int i) {
        String str;
        String str2;
        TLRPC.Chat chat;
        if (i == this.usernameRow) {
            if (this.user_id != 0) {
                TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
                if (user == null || (str2 = user.username) == null) {
                    return false;
                }
            } else if (this.chat_id == 0 || (chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id))) == null || (str2 = chat.username) == null) {
                return false;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setItems(new CharSequence[]{LocaleController.getString("Copy", NUM)}, new DialogInterface.OnClickListener(str2) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    ProfileActivity.this.lambda$processOnClickOrPress$12$ProfileActivity(this.f$1, dialogInterface, i);
                }
            });
            showDialog(builder.create());
            return true;
        } else if (i == this.phoneRow) {
            TLRPC.User user2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
            if (user2 == null || (str = user2.phone) == null || str.length() == 0 || getParentActivity() == null) {
                return false;
            }
            AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            TLRPC.UserFull userFull = this.userInfo;
            if (userFull != null && userFull.phone_calls_available) {
                arrayList.add(LocaleController.getString("CallViaTelegram", NUM));
                arrayList2.add(2);
            }
            arrayList.add(LocaleController.getString("Call", NUM));
            arrayList2.add(0);
            arrayList.add(LocaleController.getString("Copy", NUM));
            arrayList2.add(1);
            builder2.setItems((CharSequence[]) arrayList.toArray(new CharSequence[0]), new DialogInterface.OnClickListener(arrayList2, user2) {
                private final /* synthetic */ ArrayList f$1;
                private final /* synthetic */ TLRPC.User f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    ProfileActivity.this.lambda$processOnClickOrPress$13$ProfileActivity(this.f$1, this.f$2, dialogInterface, i);
                }
            });
            showDialog(builder2.create());
            return true;
        } else if (i != this.channelInfoRow && i != this.userInfoRow && i != this.locationRow) {
            return false;
        } else {
            AlertDialog.Builder builder3 = new AlertDialog.Builder((Context) getParentActivity());
            builder3.setItems(new CharSequence[]{LocaleController.getString("Copy", NUM)}, new DialogInterface.OnClickListener(i) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    ProfileActivity.this.lambda$processOnClickOrPress$14$ProfileActivity(this.f$1, dialogInterface, i);
                }
            });
            showDialog(builder3.create());
            return true;
        }
    }

    public /* synthetic */ void lambda$processOnClickOrPress$12$ProfileActivity(String str, DialogInterface dialogInterface, int i) {
        if (i == 0) {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", "@" + str));
                Toast.makeText(getParentActivity(), LocaleController.getString("TextCopied", NUM), 0).show();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public /* synthetic */ void lambda$processOnClickOrPress$13$ProfileActivity(ArrayList arrayList, TLRPC.User user, DialogInterface dialogInterface, int i) {
        int intValue = ((Integer) arrayList.get(i)).intValue();
        if (intValue == 0) {
            try {
                Intent intent = new Intent("android.intent.action.DIAL", Uri.parse("tel:+" + user.phone));
                intent.addFlags(NUM);
                getParentActivity().startActivityForResult(intent, 500);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else if (intValue == 1) {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", "+" + user.phone));
                Toast.makeText(getParentActivity(), LocaleController.getString("PhoneCopied", NUM), 0).show();
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        } else if (intValue == 2) {
            VoIPHelper.startCall(user, getParentActivity(), this.userInfo);
        }
    }

    public /* synthetic */ void lambda$processOnClickOrPress$14$ProfileActivity(int i, DialogInterface dialogInterface, int i2) {
        try {
            String str = null;
            if (i == this.locationRow) {
                if (this.chatInfo != null && (this.chatInfo.location instanceof TLRPC.TL_channelLocation)) {
                    str = ((TLRPC.TL_channelLocation) this.chatInfo.location).address;
                }
            } else if (i == this.channelInfoRow) {
                if (this.chatInfo != null) {
                    str = this.chatInfo.about;
                }
            } else if (this.userInfo != null) {
                str = this.userInfo.about;
            }
            if (!TextUtils.isEmpty(str)) {
                AndroidUtilities.addToClipboard(str);
                Toast.makeText(getParentActivity(), LocaleController.getString("TextCopied", NUM), 0).show();
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public void leaveChatPressed() {
        AlertsCreator.createClearOrDeleteDialogAlert(this, false, this.currentChat, (TLRPC.User) null, false, new MessagesStorage.BooleanCallback() {
            public final void run(boolean z) {
                ProfileActivity.this.lambda$leaveChatPressed$15$ProfileActivity(z);
            }
        });
    }

    public /* synthetic */ void lambda$leaveChatPressed$15$ProfileActivity(boolean z) {
        this.playProfileAnimation = false;
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
        finishFragment();
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.needDeleteDialog, Long.valueOf((long) (-this.currentChat.id)), null, this.currentChat, Boolean.valueOf(z));
    }

    /* access modifiers changed from: private */
    public void getChannelParticipants(boolean z) {
        SparseArray<TLRPC.ChatParticipant> sparseArray;
        if (!this.loadingUsers && (sparseArray = this.participantsMap) != null && this.chatInfo != null) {
            this.loadingUsers = true;
            int i = 0;
            int i2 = (sparseArray.size() == 0 || !z) ? 0 : 300;
            TLRPC.TL_channels_getParticipants tL_channels_getParticipants = new TLRPC.TL_channels_getParticipants();
            tL_channels_getParticipants.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chat_id);
            tL_channels_getParticipants.filter = new TLRPC.TL_channelParticipantsRecent();
            if (!z) {
                i = this.participantsMap.size();
            }
            tL_channels_getParticipants.offset = i;
            tL_channels_getParticipants.limit = 200;
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_getParticipants, new RequestDelegate(tL_channels_getParticipants, i2) {
                private final /* synthetic */ TLRPC.TL_channels_getParticipants f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ProfileActivity.this.lambda$getChannelParticipants$17$ProfileActivity(this.f$1, this.f$2, tLObject, tL_error);
                }
            }), this.classGuid);
        }
    }

    public /* synthetic */ void lambda$getChannelParticipants$17$ProfileActivity(TLRPC.TL_channels_getParticipants tL_channels_getParticipants, int i, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tL_error, tLObject, tL_channels_getParticipants) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ TLRPC.TL_channels_getParticipants f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ProfileActivity.this.lambda$null$16$ProfileActivity(this.f$1, this.f$2, this.f$3);
            }
        }, (long) i);
    }

    public /* synthetic */ void lambda$null$16$ProfileActivity(TLRPC.TL_error tL_error, TLObject tLObject, TLRPC.TL_channels_getParticipants tL_channels_getParticipants) {
        if (tL_error == null) {
            TLRPC.TL_channels_channelParticipants tL_channels_channelParticipants = (TLRPC.TL_channels_channelParticipants) tLObject;
            MessagesController.getInstance(this.currentAccount).putUsers(tL_channels_channelParticipants.users, false);
            if (tL_channels_channelParticipants.users.size() < 200) {
                this.usersEndReached = true;
            }
            if (tL_channels_getParticipants.offset == 0) {
                this.participantsMap.clear();
                this.chatInfo.participants = new TLRPC.TL_chatParticipants();
                MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(tL_channels_channelParticipants.users, (ArrayList<TLRPC.Chat>) null, true, true);
                MessagesStorage.getInstance(this.currentAccount).updateChannelUsers(this.chat_id, tL_channels_channelParticipants.participants);
            }
            for (int i = 0; i < tL_channels_channelParticipants.participants.size(); i++) {
                TLRPC.TL_chatChannelParticipant tL_chatChannelParticipant = new TLRPC.TL_chatChannelParticipant();
                tL_chatChannelParticipant.channelParticipant = tL_channels_channelParticipants.participants.get(i);
                TLRPC.ChannelParticipant channelParticipant = tL_chatChannelParticipant.channelParticipant;
                tL_chatChannelParticipant.inviter_id = channelParticipant.inviter_id;
                tL_chatChannelParticipant.user_id = channelParticipant.user_id;
                tL_chatChannelParticipant.date = channelParticipant.date;
                if (this.participantsMap.indexOfKey(tL_chatChannelParticipant.user_id) < 0) {
                    this.chatInfo.participants.participants.add(tL_chatChannelParticipant);
                    this.participantsMap.put(tL_chatChannelParticipant.user_id, tL_chatChannelParticipant);
                }
            }
        }
        updateOnlineCount();
        this.loadingUsers = false;
        updateRowsIds();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    /* access modifiers changed from: private */
    public void openAddMember() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("addToGroup", true);
        bundle.putInt("chatId", this.currentChat.id);
        GroupCreateActivity groupCreateActivity = new GroupCreateActivity(bundle);
        groupCreateActivity.setInfo(this.chatInfo);
        TLRPC.ChatFull chatFull = this.chatInfo;
        if (!(chatFull == null || chatFull.participants == null)) {
            SparseArray sparseArray = new SparseArray();
            for (int i = 0; i < this.chatInfo.participants.participants.size(); i++) {
                sparseArray.put(this.chatInfo.participants.participants.get(i).user_id, (Object) null);
            }
            groupCreateActivity.setIgnoreUsers(sparseArray);
        }
        groupCreateActivity.setDelegate((GroupCreateActivity.ContactsAddActivityDelegate) new GroupCreateActivity.ContactsAddActivityDelegate() {
            public final void didSelectUsers(ArrayList arrayList, int i) {
                ProfileActivity.this.lambda$openAddMember$18$ProfileActivity(arrayList, i);
            }

            public /* synthetic */ void needAddBot(TLRPC.User user) {
                GroupCreateActivity.ContactsAddActivityDelegate.CC.$default$needAddBot(this, user);
            }
        });
        presentFragment(groupCreateActivity);
    }

    public /* synthetic */ void lambda$openAddMember$18$ProfileActivity(ArrayList arrayList, int i) {
        int size = arrayList.size();
        for (int i2 = 0; i2 < size; i2++) {
            MessagesController.getInstance(this.currentAccount).addUserToChat(this.chat_id, (TLRPC.User) arrayList.get(i2), this.chatInfo, i, (String) null, this, (Runnable) null);
        }
    }

    /* access modifiers changed from: private */
    public void checkListViewScroll() {
        if (this.listView.getChildCount() > 0 && !this.openAnimationInProgress) {
            boolean z = false;
            View childAt = this.listView.getChildAt(0);
            RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(childAt);
            int top = childAt.getTop();
            if (top < 0 || holder == null || holder.getAdapterPosition() != 0) {
                top = 0;
            }
            float f = (float) top;
            if (this.extraHeight != f) {
                this.extraHeight = f;
                this.topView.invalidate();
                if (this.playProfileAnimation) {
                    if (this.extraHeight != 0.0f) {
                        z = true;
                    }
                    this.allowProfileAnimation = z;
                }
                needLayout();
            }
        }
    }

    /* access modifiers changed from: private */
    public void needLayout() {
        ValueAnimator valueAnimator;
        int currentActionBarHeight = (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null && !this.openAnimationInProgress) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) recyclerListView.getLayoutParams();
            if (layoutParams.topMargin != currentActionBarHeight) {
                layoutParams.topMargin = currentActionBarHeight;
                this.listView.setLayoutParams(layoutParams);
            }
        }
        if (this.avatarImage != null) {
            float min = Math.min(1.0f, this.extraHeight / AndroidUtilities.dpf2(88.0f));
            this.listView.setTopGlowOffset((int) this.extraHeight);
            this.listView.setOverScrollMode((this.extraHeight <= ((float) AndroidUtilities.dp(88.0f)) || this.extraHeight >= ((float) (this.listView.getMeasuredWidth() - currentActionBarHeight))) ? 0 : 2);
            ImageView imageView = this.writeButton;
            if (imageView != null) {
                imageView.setTranslationY((((float) ((this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight())) + this.extraHeight) - ((float) AndroidUtilities.dp(29.5f)));
                if (!this.openAnimationInProgress) {
                    boolean z = min > 0.2f;
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
                        this.writeButtonAnimation = new AnimatorSet();
                        if (z) {
                            this.writeButtonAnimation.setInterpolator(new DecelerateInterpolator());
                            this.writeButtonAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.writeButton, View.SCALE_X, new float[]{1.0f}), ObjectAnimator.ofFloat(this.writeButton, View.SCALE_Y, new float[]{1.0f}), ObjectAnimator.ofFloat(this.writeButton, View.ALPHA, new float[]{1.0f})});
                        } else {
                            this.writeButtonAnimation.setInterpolator(new AccelerateInterpolator());
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
                    }
                }
            }
            this.avatarX = (-AndroidUtilities.dpf2(47.0f)) * min;
            int i = this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0;
            float f = AndroidUtilities.density;
            this.avatarY = ((((float) i) + ((((float) ActionBar.getCurrentActionBarHeight()) / 2.0f) * (min + 1.0f))) - (21.0f * f)) + (f * 27.0f * min);
            if (this.extraHeight > AndroidUtilities.dpf2(88.0f) || this.isPulledDown) {
                this.expandProgress = Math.max(0.0f, Math.min(1.0f, (this.extraHeight - AndroidUtilities.dpf2(88.0f)) / (((float) (this.listView.getMeasuredWidth() - currentActionBarHeight)) - AndroidUtilities.dpf2(88.0f))));
                this.avatarScale = AndroidUtilities.lerp(1.4285715f, 2.4285715f, Math.min(1.0f, this.expandProgress * 3.0f));
                float min2 = Math.min(AndroidUtilities.dpf2(2000.0f), Math.max(AndroidUtilities.dpf2(1100.0f), Math.abs(this.listViewVelocityY))) / AndroidUtilities.dpf2(1100.0f);
                if (this.expandProgress >= 0.33f) {
                    if (!this.isPulledDown) {
                        this.isPulledDown = true;
                        this.overlaysView.setOverlaysVisible(true, min2);
                        this.avatarsViewPagerIndicatorView.refreshVisibility(min2);
                        ValueAnimator valueAnimator2 = this.expandAnimator;
                        if (valueAnimator2 == null) {
                            this.expandAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                            this.expandAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(currentActionBarHeight) {
                                private final /* synthetic */ int f$1;

                                {
                                    this.f$1 = r2;
                                }

                                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                                    ProfileActivity.this.lambda$needLayout$19$ProfileActivity(this.f$1, valueAnimator);
                                }
                            });
                            this.expandAnimator.setDuration((long) (250.0f / min2));
                            this.expandAnimator.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
                            this.expandAnimator.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationStart(Animator animator) {
                                    ProfileActivity.this.nameTextView[1].setBackgroundColor(0);
                                }

                                public void onAnimationEnd(Animator animator) {
                                    if (!ProfileActivity.this.isPulledDown) {
                                        ProfileActivity.this.nameTextView[1].setBackgroundColor(Theme.getColor("avatar_backgroundActionBarBlue"));
                                    }
                                    ProfileActivity.this.actionBar.setItemsBackgroundColor(ProfileActivity.this.isPulledDown ? NUM : Theme.getColor("avatar_actionBarSelectorBlue"), false);
                                }
                            });
                        } else {
                            valueAnimator2.cancel();
                            float lerp = AndroidUtilities.lerp(this.expandAnimatorValues, this.expandAnimator.getAnimatedFraction());
                            float[] fArr = this.expandAnimatorValues;
                            fArr[0] = lerp;
                            fArr[1] = 1.0f;
                            this.expandAnimator.setDuration((long) (((1.0f - lerp) * 250.0f) / min2));
                        }
                        this.expandAnimator.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationStart(Animator animator) {
                                ProfileActivity.this.avatarImage.setForegroundImage(ProfileActivity.this.avatarsViewPager.getImageLocation(0), (String) null, ProfileActivity.this.avatarImage.getImageReceiver().getDrawable());
                                ProfileActivity.this.avatarsViewPager.resetCurrentItem();
                            }

                            public void onAnimationEnd(Animator animator) {
                                ProfileActivity.this.expandAnimator.removeListener(this);
                                ProfileActivity.this.avatarImage.clearForeground();
                                ProfileActivity.this.topView.setBackgroundColor(-16777216);
                                ProfileActivity.this.avatarImage.setVisibility(8);
                                ProfileActivity.this.avatarsViewPager.setVisibility(0);
                            }
                        });
                        this.expandAnimator.start();
                    }
                    ViewGroup.LayoutParams layoutParams2 = this.avatarsViewPager.getLayoutParams();
                    layoutParams2.width = this.listView.getMeasuredWidth();
                    float f2 = (float) currentActionBarHeight;
                    layoutParams2.height = (int) (this.extraHeight + f2);
                    this.avatarsViewPager.requestLayout();
                    if (!this.expandAnimator.isRunning()) {
                        this.nameTextView[1].setTranslationX(AndroidUtilities.dpf2(16.0f) - ((float) this.nameTextView[1].getLeft()));
                        this.nameTextView[1].setTranslationY(((this.extraHeight + f2) - AndroidUtilities.dpf2(38.0f)) - ((float) this.nameTextView[1].getBottom()));
                        this.onlineTextView[1].setTranslationX(AndroidUtilities.dpf2(16.0f) - ((float) this.onlineTextView[1].getLeft()));
                        this.onlineTextView[1].setTranslationY(((f2 + this.extraHeight) - AndroidUtilities.dpf2(18.0f)) - ((float) this.onlineTextView[1].getBottom()));
                    }
                } else {
                    if (this.isPulledDown) {
                        this.isPulledDown = false;
                        this.overlaysView.setOverlaysVisible(false, min2);
                        this.avatarsViewPagerIndicatorView.refreshVisibility(min2);
                        this.expandAnimator.cancel();
                        float lerp2 = AndroidUtilities.lerp(this.expandAnimatorValues, this.expandAnimator.getAnimatedFraction());
                        float[] fArr2 = this.expandAnimatorValues;
                        fArr2[0] = lerp2;
                        fArr2[1] = 0.0f;
                        if (!this.isInLandscapeMode) {
                            this.expandAnimator.setDuration((long) ((lerp2 * 250.0f) / min2));
                        } else {
                            this.expandAnimator.setDuration(0);
                        }
                        this.topView.setBackgroundColor(Theme.getColor("avatar_backgroundActionBarBlue"));
                        BackupImageView currentItemView = this.avatarsViewPager.getCurrentItemView();
                        if (currentItemView != null) {
                            this.avatarImage.setForegroundImageDrawable(currentItemView.getImageReceiver().getDrawable());
                        }
                        this.avatarImage.setForegroundAlpha(1.0f);
                        this.avatarImage.setVisibility(0);
                        this.avatarsViewPager.setVisibility(8);
                        this.expandAnimator.start();
                    }
                    this.avatarImage.setScaleX(this.avatarScale);
                    this.avatarImage.setScaleY(this.avatarScale);
                    ValueAnimator valueAnimator3 = this.expandAnimator;
                    if (valueAnimator3 == null || !valueAnimator3.isRunning()) {
                        refreshNameAndOnlineXY();
                        this.nameTextView[1].setTranslationX(this.nameX);
                        this.nameTextView[1].setTranslationY(this.nameY);
                        this.onlineTextView[1].setTranslationX(this.onlineX);
                        this.onlineTextView[1].setTranslationY(this.onlineY);
                    }
                }
            }
            if (this.extraHeight <= AndroidUtilities.dpf2(88.0f)) {
                this.avatarScale = ((18.0f * min) + 42.0f) / 42.0f;
                float f3 = (0.12f * min) + 1.0f;
                ValueAnimator valueAnimator4 = this.expandAnimator;
                if (valueAnimator4 == null || !valueAnimator4.isRunning()) {
                    this.avatarImage.setScaleX(this.avatarScale);
                    this.avatarImage.setScaleY(this.avatarScale);
                    this.avatarImage.setTranslationX(this.avatarX);
                    this.avatarImage.setTranslationY((float) Math.ceil((double) this.avatarY));
                }
                this.nameX = AndroidUtilities.density * -21.0f * min;
                this.nameY = ((float) Math.floor((double) this.avatarY)) + ((float) AndroidUtilities.dp(1.3f)) + (((float) AndroidUtilities.dp(7.0f)) * min);
                this.onlineX = AndroidUtilities.density * -21.0f * min;
                this.onlineY = ((float) Math.floor((double) this.avatarY)) + ((float) AndroidUtilities.dp(24.0f)) + (((float) Math.floor((double) (AndroidUtilities.density * 11.0f))) * min);
                for (int i2 = 0; i2 < 2; i2++) {
                    if (this.nameTextView[i2] != null) {
                        ValueAnimator valueAnimator5 = this.expandAnimator;
                        if (valueAnimator5 == null || !valueAnimator5.isRunning()) {
                            this.nameTextView[i2].setTranslationX(this.nameX);
                            this.nameTextView[i2].setTranslationY(this.nameY);
                            this.onlineTextView[i2].setTranslationX(this.onlineX);
                            this.onlineTextView[i2].setTranslationY(this.onlineY);
                        }
                        this.nameTextView[i2].setScaleX(f3);
                        this.nameTextView[i2].setScaleY(f3);
                    }
                }
            }
            if (!this.openAnimationInProgress && ((valueAnimator = this.expandAnimator) == null || !valueAnimator.isRunning())) {
                needLayoutText(min);
            }
        }
        if (this.isPulledDown || (this.overlaysView.animator != null && this.overlaysView.animator.isRunning())) {
            ViewGroup.LayoutParams layoutParams3 = this.overlaysView.getLayoutParams();
            layoutParams3.width = this.listView.getMeasuredWidth();
            layoutParams3.height = (int) (this.extraHeight + ((float) currentActionBarHeight));
            this.overlaysView.requestLayout();
        }
    }

    public /* synthetic */ void lambda$needLayout$19$ProfileActivity(int i, ValueAnimator valueAnimator) {
        int i2;
        float lerp = AndroidUtilities.lerp(this.expandAnimatorValues, valueAnimator.getAnimatedFraction());
        this.avatarImage.setScaleX(this.avatarScale);
        this.avatarImage.setScaleY(this.avatarScale);
        this.avatarImage.setTranslationX(AndroidUtilities.lerp(this.avatarX, 0.0f, lerp));
        this.avatarImage.setTranslationY(AndroidUtilities.lerp((float) Math.ceil((double) this.avatarY), 0.0f, lerp));
        this.avatarImage.setRoundRadius((int) AndroidUtilities.lerp(AndroidUtilities.dpf2(21.0f), 0.0f, lerp));
        if (this.extraHeight > AndroidUtilities.dpf2(88.0f) && this.expandProgress < 0.33f) {
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
        float f = (float) i;
        float dpvar_ = ((this.extraHeight + f) - AndroidUtilities.dpf2(38.0f)) - ((float) this.nameTextView[1].getBottom());
        float f2 = this.nameX;
        float f3 = this.nameY;
        float f4 = 1.0f - lerp;
        float f5 = f4 * f4;
        float f6 = f4 * 2.0f * lerp;
        float f7 = (f2 * f5) + ((dpf2 + f2 + ((dpvar_ - f2) / 2.0f)) * f6);
        float f8 = lerp * lerp;
        float f9 = f7 + (dpvar_ * f8);
        float var_ = (f3 * f5) + ((dpf2 + f3 + ((dpvar_ - f3) / 2.0f)) * f6) + (dpvar_ * f8);
        float dpvar_ = AndroidUtilities.dpf2(16.0f) - ((float) this.onlineTextView[1].getLeft());
        float dpvar_ = ((this.extraHeight + f) - AndroidUtilities.dpf2(18.0f)) - ((float) this.onlineTextView[1].getBottom());
        float var_ = this.onlineX;
        float var_ = this.onlineY;
        this.nameTextView[1].setTranslationX(f9);
        this.nameTextView[1].setTranslationY(var_);
        SimpleTextView simpleTextView = this.onlineTextView[1];
        simpleTextView.setTranslationX((var_ * f5) + ((dpf2 + var_ + ((dpvar_ - var_) / 2.0f)) * f6) + (dpvar_ * f8));
        this.onlineTextView[1].setTranslationY((f5 * var_) + (f6 * (dpf2 + var_ + ((dpvar_ - var_) / 2.0f))) + (f8 * dpvar_));
        Object tag = this.onlineTextView[1].getTag();
        if (tag instanceof String) {
            i2 = Theme.getColor((String) tag);
        } else {
            i2 = Theme.getColor("avatar_subtitleInProfileBlue");
        }
        this.onlineTextView[1].setTextColor(ColorUtils.blendARGB(i2, Color.argb(179, 255, 255, 255), lerp));
        if (this.extraHeight > AndroidUtilities.dpf2(88.0f)) {
            SimpleTextView[] simpleTextViewArr = this.nameTextView;
            simpleTextViewArr[1].setPivotY(AndroidUtilities.lerp(0.0f, (float) simpleTextViewArr[1].getMeasuredHeight(), lerp));
            this.nameTextView[1].setScaleX(AndroidUtilities.lerp(1.12f, 1.67f, lerp));
            this.nameTextView[1].setScaleY(AndroidUtilities.lerp(1.12f, 1.67f, lerp));
        }
        needLayoutText(Math.min(1.0f, this.extraHeight / AndroidUtilities.dpf2(88.0f)));
        this.nameTextView[1].setTextColor(ColorUtils.blendARGB(Theme.getColor("profile_title"), -1, lerp));
        this.actionBar.setItemsColor(ColorUtils.blendARGB(Theme.getColor("actionBarDefaultIcon"), -1, lerp), false);
        this.avatarImage.setForegroundAlpha(lerp);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.avatarImage.getLayoutParams();
        layoutParams.width = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(42.0f), ((float) this.listView.getMeasuredWidth()) / this.avatarScale, lerp);
        layoutParams.height = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(42.0f), (this.extraHeight + f) / this.avatarScale, lerp);
        layoutParams.leftMargin = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(64.0f), 0.0f, lerp);
        this.avatarImage.requestLayout();
        FileLog.d("w = " + layoutParams.width + " h = " + layoutParams.height + " scale " + this.avatarScale);
    }

    private void refreshNameAndOnlineXY() {
        this.nameX = ((float) AndroidUtilities.dp(-21.0f)) + (((float) this.avatarImage.getMeasuredWidth()) * (this.avatarScale - 1.4285715f));
        this.nameY = ((float) Math.floor((double) this.avatarY)) + ((float) AndroidUtilities.dp(1.3f)) + ((float) AndroidUtilities.dp(7.0f)) + ((((float) this.avatarImage.getMeasuredHeight()) * (this.avatarScale - 1.4285715f)) / 2.0f);
        this.onlineX = ((float) AndroidUtilities.dp(-21.0f)) + (((float) this.avatarImage.getMeasuredWidth()) * (this.avatarScale - 1.4285715f));
        this.onlineY = ((float) Math.floor((double) this.avatarY)) + ((float) AndroidUtilities.dp(24.0f)) + ((float) Math.floor((double) (AndroidUtilities.density * 11.0f))) + ((((float) this.avatarImage.getMeasuredHeight()) * (this.avatarScale - 1.4285715f)) / 2.0f);
    }

    private void needLayoutText(float f) {
        float scaleX = this.nameTextView[1].getScaleX();
        float f2 = this.extraHeight > AndroidUtilities.dpf2(88.0f) ? 1.67f : 1.12f;
        if (this.extraHeight <= ((float) AndroidUtilities.dp(88.0f)) || scaleX == f2) {
            int dp = AndroidUtilities.isTablet() ? AndroidUtilities.dp(490.0f) : AndroidUtilities.displaySize.x;
            int dp2 = AndroidUtilities.dp((float) (126 + 40 + ((this.callItem == null && this.editItem == null) ? 0 : 48)));
            int i = dp - dp2;
            float f3 = (float) dp;
            int max = (int) ((f3 - (((float) dp2) * Math.max(0.0f, 1.0f - (f != 1.0f ? (0.15f * f) / (1.0f - f) : 1.0f)))) - this.nameTextView[1].getTranslationX());
            float measureText = (this.nameTextView[1].getPaint().measureText(this.nameTextView[1].getText().toString()) * scaleX) + ((float) this.nameTextView[1].getSideDrawablesSize());
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.nameTextView[1].getLayoutParams();
            int i2 = layoutParams.width;
            float f4 = (float) max;
            if (f4 < measureText) {
                layoutParams.width = Math.max(i, (int) Math.ceil((double) (((float) (max - AndroidUtilities.dp(24.0f))) / (((f2 - scaleX) * 7.0f) + scaleX))));
            } else {
                layoutParams.width = (int) Math.ceil((double) measureText);
            }
            layoutParams.width = (int) Math.min(((f3 - this.nameTextView[1].getX()) / scaleX) - ((float) AndroidUtilities.dp(8.0f)), (float) layoutParams.width);
            if (layoutParams.width != i2) {
                this.nameTextView[1].requestLayout();
            }
            float measureText2 = this.onlineTextView[1].getPaint().measureText(this.onlineTextView[1].getText().toString());
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.onlineTextView[1].getLayoutParams();
            int i3 = layoutParams2.width;
            layoutParams2.rightMargin = (int) Math.ceil((double) (this.onlineTextView[1].getTranslationX() + ((float) AndroidUtilities.dp(8.0f)) + (((float) AndroidUtilities.dp(40.0f)) * (1.0f - f))));
            if (f4 < measureText2) {
                layoutParams2.width = (int) Math.ceil((double) max);
            } else {
                layoutParams2.width = -2;
            }
            if (i3 != layoutParams2.width) {
                this.onlineTextView[1].requestLayout();
            }
        }
    }

    private void loadMediaCounts() {
        if (this.dialog_id != 0) {
            MediaDataController.getInstance(this.currentAccount).getMediaCounts(this.dialog_id, this.classGuid);
        } else if (this.user_id != 0) {
            MediaDataController.getInstance(this.currentAccount).getMediaCounts((long) this.user_id, this.classGuid);
        } else if (this.chat_id > 0) {
            MediaDataController.getInstance(this.currentAccount).getMediaCounts((long) (-this.chat_id), this.classGuid);
            if (this.mergeDialogId != 0) {
                MediaDataController.getInstance(this.currentAccount).getMediaCounts(this.mergeDialogId, this.classGuid);
            }
        }
    }

    private void fixLayout() {
        View view = this.fragmentView;
        if (view != null) {
            view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    if (ProfileActivity.this.fragmentView == null) {
                        return true;
                    }
                    ProfileActivity.this.checkListViewScroll();
                    ProfileActivity.this.needLayout();
                    ProfileActivity.this.fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        View findViewByPosition;
        super.onConfigurationChanged(configuration);
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v3, resolved type: boolean} */
    /* JADX WARNING: type inference failed for: r4v0 */
    /* JADX WARNING: type inference failed for: r4v1 */
    /* JADX WARNING: type inference failed for: r4v2 */
    /* JADX WARNING: type inference failed for: r4v4 */
    /* JADX WARNING: type inference failed for: r4v6, types: [int] */
    /* JADX WARNING: type inference failed for: r4v8 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void didReceivedNotification(int r22, int r23, java.lang.Object... r24) {
        /*
            r21 = this;
            r0 = r21
            r1 = r22
            r2 = r24
            int r3 = org.telegram.messenger.NotificationCenter.updateInterfaces
            r4 = 0
            if (r1 != r3) goto L_0x0093
            r1 = r2[r4]
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            int r2 = r0.user_id
            if (r2 == 0) goto L_0x0041
            r2 = r1 & 2
            if (r2 != 0) goto L_0x0023
            r2 = r1 & 1
            if (r2 != 0) goto L_0x0023
            r2 = r1 & 4
            if (r2 == 0) goto L_0x0026
        L_0x0023:
            r21.updateProfileData()
        L_0x0026:
            r1 = r1 & 1024(0x400, float:1.435E-42)
            if (r1 == 0) goto L_0x0491
            org.telegram.ui.Components.RecyclerListView r1 = r0.listView
            if (r1 == 0) goto L_0x0491
            int r2 = r0.phoneRow
            androidx.recyclerview.widget.RecyclerView$ViewHolder r1 = r1.findViewHolderForPosition(r2)
            org.telegram.ui.Components.RecyclerListView$Holder r1 = (org.telegram.ui.Components.RecyclerListView.Holder) r1
            if (r1 == 0) goto L_0x0491
            org.telegram.ui.ProfileActivity$ListAdapter r2 = r0.listAdapter
            int r3 = r0.phoneRow
            r2.onBindViewHolder(r1, r3)
            goto L_0x0491
        L_0x0041:
            int r2 = r0.chat_id
            if (r2 == 0) goto L_0x0491
            r2 = r1 & 8192(0x2000, float:1.14794E-41)
            if (r2 != 0) goto L_0x0059
            r3 = r1 & 8
            if (r3 != 0) goto L_0x0059
            r3 = r1 & 16
            if (r3 != 0) goto L_0x0059
            r3 = r1 & 32
            if (r3 != 0) goto L_0x0059
            r3 = r1 & 4
            if (r3 == 0) goto L_0x005f
        L_0x0059:
            r21.updateOnlineCount()
            r21.updateProfileData()
        L_0x005f:
            if (r2 == 0) goto L_0x006b
            r21.updateRowsIds()
            org.telegram.ui.ProfileActivity$ListAdapter r2 = r0.listAdapter
            if (r2 == 0) goto L_0x006b
            r2.notifyDataSetChanged()
        L_0x006b:
            r2 = r1 & 2
            if (r2 != 0) goto L_0x0077
            r2 = r1 & 1
            if (r2 != 0) goto L_0x0077
            r2 = r1 & 4
            if (r2 == 0) goto L_0x0491
        L_0x0077:
            org.telegram.ui.Components.RecyclerListView r2 = r0.listView
            if (r2 == 0) goto L_0x0491
            int r2 = r2.getChildCount()
        L_0x007f:
            if (r4 >= r2) goto L_0x0491
            org.telegram.ui.Components.RecyclerListView r3 = r0.listView
            android.view.View r3 = r3.getChildAt(r4)
            boolean r5 = r3 instanceof org.telegram.ui.Cells.UserCell
            if (r5 == 0) goto L_0x0090
            org.telegram.ui.Cells.UserCell r3 = (org.telegram.ui.Cells.UserCell) r3
            r3.update(r1)
        L_0x0090:
            int r4 = r4 + 1
            goto L_0x007f
        L_0x0093:
            int r3 = org.telegram.messenger.NotificationCenter.chatOnlineCountDidLoad
            r5 = 1
            if (r1 != r3) goto L_0x00c2
            r1 = r2[r4]
            java.lang.Integer r1 = (java.lang.Integer) r1
            org.telegram.tgnet.TLRPC$ChatFull r3 = r0.chatInfo
            if (r3 == 0) goto L_0x00c1
            org.telegram.tgnet.TLRPC$Chat r3 = r0.currentChat
            if (r3 == 0) goto L_0x00c1
            int r3 = r3.id
            int r1 = r1.intValue()
            if (r3 == r1) goto L_0x00ad
            goto L_0x00c1
        L_0x00ad:
            org.telegram.tgnet.TLRPC$ChatFull r1 = r0.chatInfo
            r2 = r2[r5]
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
            r1.online_count = r2
            r21.updateOnlineCount()
            r21.updateProfileData()
            goto L_0x0491
        L_0x00c1:
            return
        L_0x00c2:
            int r3 = org.telegram.messenger.NotificationCenter.contactsDidLoad
            if (r1 != r3) goto L_0x00cb
            r21.createActionBarMenu()
            goto L_0x0491
        L_0x00cb:
            int r3 = org.telegram.messenger.NotificationCenter.mediaDidLoad
            r6 = 3
            r7 = 0
            r9 = 2
            if (r1 != r3) goto L_0x014d
            r1 = r2[r4]
            java.lang.Long r1 = (java.lang.Long) r1
            long r10 = r1.longValue()
            r1 = r2[r6]
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            int r3 = r0.classGuid
            if (r1 != r3) goto L_0x0491
            long r12 = r0.dialog_id
            int r1 = (r12 > r7 ? 1 : (r12 == r7 ? 0 : -1))
            if (r1 != 0) goto L_0x00f9
            int r1 = r0.user_id
            if (r1 == 0) goto L_0x00f3
        L_0x00f1:
            long r12 = (long) r1
            goto L_0x00f9
        L_0x00f3:
            int r1 = r0.chat_id
            if (r1 == 0) goto L_0x00f9
            int r1 = -r1
            goto L_0x00f1
        L_0x00f9:
            r1 = 4
            r1 = r2[r1]
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            org.telegram.ui.MediaActivity$SharedMediaData[] r3 = r0.sharedMediaData
            r3 = r3[r1]
            r6 = r2[r5]
            java.lang.Integer r6 = (java.lang.Integer) r6
            int r6 = r6.intValue()
            r3.setTotalCount(r6)
            r3 = r2[r9]
            java.util.ArrayList r3 = (java.util.ArrayList) r3
            int r6 = (int) r12
            if (r6 != 0) goto L_0x011a
            r6 = 1
            goto L_0x011b
        L_0x011a:
            r6 = 0
        L_0x011b:
            int r7 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r7 != 0) goto L_0x0120
            r5 = 0
        L_0x0120:
            boolean r7 = r3.isEmpty()
            if (r7 != 0) goto L_0x0136
            org.telegram.ui.MediaActivity$SharedMediaData[] r7 = r0.sharedMediaData
            r7 = r7[r1]
            r8 = 5
            r2 = r2[r8]
            java.lang.Boolean r2 = (java.lang.Boolean) r2
            boolean r2 = r2.booleanValue()
            r7.setEndReached(r5, r2)
        L_0x0136:
            r2 = 0
        L_0x0137:
            int r7 = r3.size()
            if (r2 >= r7) goto L_0x0491
            java.lang.Object r7 = r3.get(r2)
            org.telegram.messenger.MessageObject r7 = (org.telegram.messenger.MessageObject) r7
            org.telegram.ui.MediaActivity$SharedMediaData[] r8 = r0.sharedMediaData
            r8 = r8[r1]
            r8.addMessage(r7, r5, r4, r6)
            int r2 = r2 + 1
            goto L_0x0137
        L_0x014d:
            int r3 = org.telegram.messenger.NotificationCenter.mediaCountsDidLoad
            if (r1 != r3) goto L_0x01ea
            r1 = r2[r4]
            java.lang.Long r1 = (java.lang.Long) r1
            long r9 = r1.longValue()
            long r11 = r0.dialog_id
            int r1 = (r11 > r7 ? 1 : (r11 == r7 ? 0 : -1))
            if (r1 != 0) goto L_0x016b
            int r1 = r0.user_id
            if (r1 == 0) goto L_0x0165
        L_0x0163:
            long r11 = (long) r1
            goto L_0x016b
        L_0x0165:
            int r1 = r0.chat_id
            if (r1 == 0) goto L_0x016b
            int r1 = -r1
            goto L_0x0163
        L_0x016b:
            int r1 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r1 == 0) goto L_0x0175
            long r6 = r0.mergeDialogId
            int r1 = (r9 > r6 ? 1 : (r9 == r6 ? 0 : -1))
            if (r1 != 0) goto L_0x0491
        L_0x0175:
            r1 = r2[r5]
            int[] r1 = (int[]) r1
            int r2 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r2 != 0) goto L_0x0180
            r0.mediaCount = r1
            goto L_0x0182
        L_0x0180:
            r0.mediaMergeCount = r1
        L_0x0182:
            int[] r1 = r0.lastMediaCount
            int[] r2 = r0.prevMediaCount
            int r3 = r2.length
            java.lang.System.arraycopy(r1, r4, r2, r4, r3)
            r1 = 0
        L_0x018b:
            int[] r2 = r0.lastMediaCount
            int r3 = r2.length
            if (r1 >= r3) goto L_0x01e5
            int[] r3 = r0.mediaCount
            r5 = r3[r1]
            if (r5 < 0) goto L_0x01a4
            int[] r5 = r0.mediaMergeCount
            r6 = r5[r1]
            if (r6 < 0) goto L_0x01a4
            r3 = r3[r1]
            r5 = r5[r1]
            int r3 = r3 + r5
            r2[r1] = r3
            goto L_0x01c2
        L_0x01a4:
            int[] r2 = r0.mediaCount
            r3 = r2[r1]
            if (r3 < 0) goto L_0x01b1
            int[] r3 = r0.lastMediaCount
            r2 = r2[r1]
            r3[r1] = r2
            goto L_0x01c2
        L_0x01b1:
            int[] r2 = r0.mediaMergeCount
            r3 = r2[r1]
            if (r3 < 0) goto L_0x01be
            int[] r3 = r0.lastMediaCount
            r2 = r2[r1]
            r3[r1] = r2
            goto L_0x01c2
        L_0x01be:
            int[] r2 = r0.lastMediaCount
            r2[r1] = r4
        L_0x01c2:
            int r2 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r2 != 0) goto L_0x01e2
            int[] r2 = r0.lastMediaCount
            r2 = r2[r1]
            if (r2 == 0) goto L_0x01e2
            int r2 = r0.currentAccount
            org.telegram.messenger.MediaDataController r13 = org.telegram.messenger.MediaDataController.getInstance(r2)
            r16 = 50
            r17 = 0
            r19 = 2
            int r2 = r0.classGuid
            r14 = r11
            r18 = r1
            r20 = r2
            r13.loadMedia(r14, r16, r17, r18, r19, r20)
        L_0x01e2:
            int r1 = r1 + 1
            goto L_0x018b
        L_0x01e5:
            r21.updateSharedMediaRows()
            goto L_0x0491
        L_0x01ea:
            int r3 = org.telegram.messenger.NotificationCenter.mediaCountDidLoad
            if (r1 != r3) goto L_0x026e
            r1 = r2[r4]
            java.lang.Long r1 = (java.lang.Long) r1
            long r9 = r1.longValue()
            long r11 = r0.dialog_id
            int r1 = (r11 > r7 ? 1 : (r11 == r7 ? 0 : -1))
            if (r1 != 0) goto L_0x0208
            int r1 = r0.user_id
            if (r1 == 0) goto L_0x0202
        L_0x0200:
            long r11 = (long) r1
            goto L_0x0208
        L_0x0202:
            int r1 = r0.chat_id
            if (r1 == 0) goto L_0x0208
            int r1 = -r1
            goto L_0x0200
        L_0x0208:
            int r1 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r1 == 0) goto L_0x0212
            long r7 = r0.mergeDialogId
            int r1 = (r9 > r7 ? 1 : (r9 == r7 ? 0 : -1))
            if (r1 != 0) goto L_0x0491
        L_0x0212:
            r1 = r2[r6]
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            r2 = r2[r5]
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
            int r3 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r3 != 0) goto L_0x022b
            int[] r3 = r0.mediaCount
            r3[r1] = r2
            goto L_0x022f
        L_0x022b:
            int[] r3 = r0.mediaMergeCount
            r3[r1] = r2
        L_0x022f:
            int[] r2 = r0.prevMediaCount
            int[] r3 = r0.lastMediaCount
            r5 = r3[r1]
            r2[r1] = r5
            int[] r2 = r0.mediaCount
            r5 = r2[r1]
            if (r5 < 0) goto L_0x024b
            int[] r5 = r0.mediaMergeCount
            r6 = r5[r1]
            if (r6 < 0) goto L_0x024b
            r2 = r2[r1]
            r4 = r5[r1]
            int r2 = r2 + r4
            r3[r1] = r2
            goto L_0x0269
        L_0x024b:
            int[] r2 = r0.mediaCount
            r3 = r2[r1]
            if (r3 < 0) goto L_0x0258
            int[] r3 = r0.lastMediaCount
            r2 = r2[r1]
            r3[r1] = r2
            goto L_0x0269
        L_0x0258:
            int[] r2 = r0.mediaMergeCount
            r3 = r2[r1]
            if (r3 < 0) goto L_0x0265
            int[] r3 = r0.lastMediaCount
            r2 = r2[r1]
            r3[r1] = r2
            goto L_0x0269
        L_0x0265:
            int[] r2 = r0.lastMediaCount
            r2[r1] = r4
        L_0x0269:
            r21.updateSharedMediaRows()
            goto L_0x0491
        L_0x026e:
            int r3 = org.telegram.messenger.NotificationCenter.encryptedChatCreated
            if (r1 != r3) goto L_0x0280
            boolean r1 = r0.creatingChat
            if (r1 == 0) goto L_0x0491
            org.telegram.ui.-$$Lambda$ProfileActivity$1kAHYRs1Xitld36bWHa7gE_IAF4 r1 = new org.telegram.ui.-$$Lambda$ProfileActivity$1kAHYRs1Xitld36bWHa7gE_IAF4
            r1.<init>(r2)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
            goto L_0x0491
        L_0x0280:
            int r3 = org.telegram.messenger.NotificationCenter.encryptedChatUpdated
            if (r1 != r3) goto L_0x02a0
            r1 = r2[r4]
            org.telegram.tgnet.TLRPC$EncryptedChat r1 = (org.telegram.tgnet.TLRPC.EncryptedChat) r1
            org.telegram.tgnet.TLRPC$EncryptedChat r2 = r0.currentEncryptedChat
            if (r2 == 0) goto L_0x0491
            int r3 = r1.id
            int r2 = r2.id
            if (r3 != r2) goto L_0x0491
            r0.currentEncryptedChat = r1
            r21.updateRowsIds()
            org.telegram.ui.ProfileActivity$ListAdapter r1 = r0.listAdapter
            if (r1 == 0) goto L_0x0491
            r1.notifyDataSetChanged()
            goto L_0x0491
        L_0x02a0:
            int r3 = org.telegram.messenger.NotificationCenter.blockedUsersDidLoad
            if (r1 != r3) goto L_0x02ca
            boolean r1 = r0.userBlocked
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            android.util.SparseIntArray r2 = r2.blockedUsers
            int r3 = r0.user_id
            int r2 = r2.indexOfKey(r3)
            if (r2 < 0) goto L_0x02b7
            r4 = 1
        L_0x02b7:
            r0.userBlocked = r4
            boolean r2 = r0.userBlocked
            if (r1 == r2) goto L_0x0491
            r21.createActionBarMenu()
            r21.updateRowsIds()
            org.telegram.ui.ProfileActivity$ListAdapter r1 = r0.listAdapter
            r1.notifyDataSetChanged()
            goto L_0x0491
        L_0x02ca:
            int r3 = org.telegram.messenger.NotificationCenter.chatInfoDidLoad
            if (r1 != r3) goto L_0x0350
            r1 = r2[r4]
            org.telegram.tgnet.TLRPC$ChatFull r1 = (org.telegram.tgnet.TLRPC.ChatFull) r1
            int r3 = r1.id
            int r6 = r0.chat_id
            if (r3 != r6) goto L_0x0491
            r2 = r2[r9]
            java.lang.Boolean r2 = (java.lang.Boolean) r2
            boolean r2 = r2.booleanValue()
            org.telegram.tgnet.TLRPC$ChatFull r3 = r0.chatInfo
            boolean r6 = r3 instanceof org.telegram.tgnet.TLRPC.TL_channelFull
            if (r6 == 0) goto L_0x02f0
            org.telegram.tgnet.TLRPC$ChatParticipants r6 = r1.participants
            if (r6 != 0) goto L_0x02f0
            if (r3 == 0) goto L_0x02f0
            org.telegram.tgnet.TLRPC$ChatParticipants r3 = r3.participants
            r1.participants = r3
        L_0x02f0:
            org.telegram.tgnet.TLRPC$ChatFull r3 = r0.chatInfo
            if (r3 != 0) goto L_0x02f9
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC.TL_channelFull
            if (r3 == 0) goto L_0x02f9
            r4 = 1
        L_0x02f9:
            r0.chatInfo = r1
            long r9 = r0.mergeDialogId
            int r1 = (r9 > r7 ? 1 : (r9 == r7 ? 0 : -1))
            if (r1 != 0) goto L_0x031a
            org.telegram.tgnet.TLRPC$ChatFull r1 = r0.chatInfo
            int r1 = r1.migrated_from_chat_id
            if (r1 == 0) goto L_0x031a
            int r1 = -r1
            long r6 = (long) r1
            r0.mergeDialogId = r6
            int r1 = r0.currentAccount
            org.telegram.messenger.MediaDataController r6 = org.telegram.messenger.MediaDataController.getInstance(r1)
            long r7 = r0.mergeDialogId
            r9 = 0
            int r10 = r0.classGuid
            r11 = 1
            r6.getMediaCount(r7, r9, r10, r11)
        L_0x031a:
            r21.fetchUsersFromChannelInfo()
            r21.updateOnlineCount()
            r21.updateRowsIds()
            org.telegram.ui.ProfileActivity$ListAdapter r1 = r0.listAdapter
            if (r1 == 0) goto L_0x032a
            r1.notifyDataSetChanged()
        L_0x032a:
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            int r3 = r0.chat_id
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r3)
            if (r1 == 0) goto L_0x0341
            r0.currentChat = r1
            r21.createActionBarMenu()
        L_0x0341:
            org.telegram.tgnet.TLRPC$Chat r1 = r0.currentChat
            boolean r1 = r1.megagroup
            if (r1 == 0) goto L_0x0491
            if (r4 != 0) goto L_0x034b
            if (r2 != 0) goto L_0x0491
        L_0x034b:
            r0.getChannelParticipants(r5)
            goto L_0x0491
        L_0x0350:
            int r3 = org.telegram.messenger.NotificationCenter.closeChats
            if (r1 != r3) goto L_0x0359
            r21.removeSelfFromStack()
            goto L_0x0491
        L_0x0359:
            int r3 = org.telegram.messenger.NotificationCenter.botInfoDidLoad
            if (r1 != r3) goto L_0x0375
            r1 = r2[r4]
            org.telegram.tgnet.TLRPC$BotInfo r1 = (org.telegram.tgnet.TLRPC.BotInfo) r1
            int r2 = r1.user_id
            int r3 = r0.user_id
            if (r2 != r3) goto L_0x0491
            r0.botInfo = r1
            r21.updateRowsIds()
            org.telegram.ui.ProfileActivity$ListAdapter r1 = r0.listAdapter
            if (r1 == 0) goto L_0x0491
            r1.notifyDataSetChanged()
            goto L_0x0491
        L_0x0375:
            int r3 = org.telegram.messenger.NotificationCenter.userInfoDidLoad
            if (r1 != r3) goto L_0x03a5
            r1 = r2[r4]
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            int r3 = r0.user_id
            if (r1 != r3) goto L_0x0491
            r1 = r2[r5]
            org.telegram.tgnet.TLRPC$UserFull r1 = (org.telegram.tgnet.TLRPC.UserFull) r1
            r0.userInfo = r1
            boolean r1 = r0.openAnimationInProgress
            if (r1 != 0) goto L_0x0397
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.callItem
            if (r1 != 0) goto L_0x0397
            r21.createActionBarMenu()
            goto L_0x0399
        L_0x0397:
            r0.recreateMenuAfterAnimation = r5
        L_0x0399:
            r21.updateRowsIds()
            org.telegram.ui.ProfileActivity$ListAdapter r1 = r0.listAdapter
            if (r1 == 0) goto L_0x0491
            r1.notifyDataSetChanged()
            goto L_0x0491
        L_0x03a5:
            int r3 = org.telegram.messenger.NotificationCenter.didReceiveNewMessages
            if (r1 != r3) goto L_0x041b
            r1 = r2[r9]
            java.lang.Boolean r1 = (java.lang.Boolean) r1
            boolean r1 = r1.booleanValue()
            if (r1 == 0) goto L_0x03b4
            return
        L_0x03b4:
            long r9 = r0.dialog_id
            int r1 = (r9 > r7 ? 1 : (r9 == r7 ? 0 : -1))
            if (r1 == 0) goto L_0x03bb
            goto L_0x03c4
        L_0x03bb:
            int r1 = r0.user_id
            if (r1 == 0) goto L_0x03c0
            goto L_0x03c3
        L_0x03c0:
            int r1 = r0.chat_id
            int r1 = -r1
        L_0x03c3:
            long r9 = (long) r1
        L_0x03c4:
            r1 = r2[r4]
            java.lang.Long r1 = (java.lang.Long) r1
            long r6 = r1.longValue()
            int r1 = (r9 > r6 ? 1 : (r9 == r6 ? 0 : -1))
            if (r1 != 0) goto L_0x0491
            int r1 = (int) r9
            if (r1 != 0) goto L_0x03d5
            r1 = 1
            goto L_0x03d6
        L_0x03d5:
            r1 = 0
        L_0x03d6:
            r2 = r2[r5]
            java.util.ArrayList r2 = (java.util.ArrayList) r2
            r3 = 0
        L_0x03db:
            int r6 = r2.size()
            if (r3 >= r6) goto L_0x0416
            java.lang.Object r6 = r2.get(r3)
            org.telegram.messenger.MessageObject r6 = (org.telegram.messenger.MessageObject) r6
            org.telegram.tgnet.TLRPC$EncryptedChat r7 = r0.currentEncryptedChat
            if (r7 == 0) goto L_0x0402
            org.telegram.tgnet.TLRPC$Message r7 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r7 = r7.action
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC.TL_messageEncryptedAction
            if (r8 == 0) goto L_0x0402
            org.telegram.tgnet.TLRPC$DecryptedMessageAction r7 = r7.encryptedAction
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL
            if (r8 == 0) goto L_0x0402
            org.telegram.tgnet.TLRPC$TL_decryptedMessageActionSetMessageTTL r7 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL) r7
            org.telegram.ui.ProfileActivity$ListAdapter r7 = r0.listAdapter
            if (r7 == 0) goto L_0x0402
            r7.notifyDataSetChanged()
        L_0x0402:
            org.telegram.tgnet.TLRPC$Message r7 = r6.messageOwner
            int r7 = org.telegram.messenger.MediaDataController.getMediaType(r7)
            r8 = -1
            if (r7 != r8) goto L_0x040c
            return
        L_0x040c:
            org.telegram.ui.MediaActivity$SharedMediaData[] r8 = r0.sharedMediaData
            r7 = r8[r7]
            r7.addMessage(r6, r4, r5, r1)
            int r3 = r3 + 1
            goto L_0x03db
        L_0x0416:
            r21.loadMediaCounts()
            goto L_0x0491
        L_0x041b:
            int r3 = org.telegram.messenger.NotificationCenter.messagesDeleted
            if (r1 != r3) goto L_0x0486
            r1 = r2[r9]
            java.lang.Boolean r1 = (java.lang.Boolean) r1
            boolean r1 = r1.booleanValue()
            if (r1 == 0) goto L_0x042a
            return
        L_0x042a:
            r1 = r2[r5]
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            org.telegram.tgnet.TLRPC$Chat r3 = r0.currentChat
            boolean r3 = org.telegram.messenger.ChatObject.isChannel(r3)
            if (r3 == 0) goto L_0x0449
            if (r1 != 0) goto L_0x0442
            long r9 = r0.mergeDialogId
            int r3 = (r9 > r7 ? 1 : (r9 == r7 ? 0 : -1))
            if (r3 != 0) goto L_0x044c
        L_0x0442:
            org.telegram.tgnet.TLRPC$Chat r3 = r0.currentChat
            int r3 = r3.id
            if (r1 == r3) goto L_0x044c
            return
        L_0x0449:
            if (r1 == 0) goto L_0x044c
            return
        L_0x044c:
            r1 = r2[r4]
            java.util.ArrayList r1 = (java.util.ArrayList) r1
            int r2 = r1.size()
            r3 = 0
            r6 = 0
        L_0x0456:
            if (r3 >= r2) goto L_0x0479
            r7 = r6
            r6 = 0
        L_0x045a:
            org.telegram.ui.MediaActivity$SharedMediaData[] r8 = r0.sharedMediaData
            int r9 = r8.length
            if (r6 >= r9) goto L_0x0475
            r8 = r8[r6]
            java.lang.Object r9 = r1.get(r3)
            java.lang.Integer r9 = (java.lang.Integer) r9
            int r9 = r9.intValue()
            boolean r8 = r8.deleteMessage(r9, r4)
            if (r8 == 0) goto L_0x0472
            r7 = 1
        L_0x0472:
            int r6 = r6 + 1
            goto L_0x045a
        L_0x0475:
            int r3 = r3 + 1
            r6 = r7
            goto L_0x0456
        L_0x0479:
            if (r6 == 0) goto L_0x0482
            org.telegram.ui.MediaActivity r1 = r0.mediaActivity
            if (r1 == 0) goto L_0x0482
            r1.updateAdapters()
        L_0x0482:
            r21.loadMediaCounts()
            goto L_0x0491
        L_0x0486:
            int r2 = org.telegram.messenger.NotificationCenter.emojiDidLoad
            if (r1 != r2) goto L_0x0491
            org.telegram.ui.Components.RecyclerListView r1 = r0.listView
            if (r1 == 0) goto L_0x0491
            r1.invalidateViews()
        L_0x0491:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.didReceivedNotification(int, int, java.lang.Object[]):void");
    }

    public /* synthetic */ void lambda$didReceivedNotification$20$ProfileActivity(Object[] objArr) {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
        Bundle bundle = new Bundle();
        bundle.putInt("enc_id", objArr[0].id);
        presentFragment(new ChatActivity(bundle), true);
    }

    public void onResume() {
        super.onResume();
        invalidateIsInLandscapeMode();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
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
    }

    /* access modifiers changed from: protected */
    public void onBecomeFullyHidden() {
        UndoView undoView2 = this.undoView;
        if (undoView2 != null) {
            undoView2.hide(true, 0);
        }
    }

    public void setPlayProfileAnimation(boolean z) {
        SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
        if (!AndroidUtilities.isTablet() && globalMainSettings.getBoolean("view_animations", true)) {
            this.playProfileAnimation = z;
        }
    }

    private void updateSharedMediaRows() {
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        int i8;
        int i9;
        int i10;
        int i11;
        if (this.listAdapter != null) {
            int i12 = this.sharedHeaderRow;
            int i13 = this.photosRow;
            int i14 = this.filesRow;
            int i15 = this.linksRow;
            int i16 = this.audioRow;
            int i17 = this.voiceRow;
            int i18 = this.groupsInCommonRow;
            updateRowsIds();
            int i19 = 3;
            if (i12 == -1 && this.sharedHeaderRow != -1) {
                if (this.photosRow == -1) {
                    i19 = 2;
                }
                if (this.filesRow != -1) {
                    i19++;
                }
                if (this.linksRow != -1) {
                    i19++;
                }
                if (this.audioRow != -1) {
                    i19++;
                }
                if (this.voiceRow != -1) {
                    i19++;
                }
                if (this.groupsInCommonRow != -1) {
                    i19++;
                }
                this.listAdapter.notifyItemRangeInserted(this.sharedHeaderRow, i19);
            } else if (i12 != -1 && this.sharedHeaderRow != -1) {
                if (!(i13 == -1 || (i11 = this.photosRow) == -1 || this.prevMediaCount[0] == this.lastMediaCount[0])) {
                    this.listAdapter.notifyItemChanged(i11);
                }
                if (!(i14 == -1 || (i10 = this.filesRow) == -1 || this.prevMediaCount[1] == this.lastMediaCount[1])) {
                    this.listAdapter.notifyItemChanged(i10);
                }
                if (!(i15 == -1 || (i9 = this.linksRow) == -1 || this.prevMediaCount[3] == this.lastMediaCount[3])) {
                    this.listAdapter.notifyItemChanged(i9);
                }
                if (!(i16 == -1 || (i8 = this.audioRow) == -1 || this.prevMediaCount[4] == this.lastMediaCount[4])) {
                    this.listAdapter.notifyItemChanged(i8);
                }
                if (!(i17 == -1 || (i7 = this.voiceRow) == -1 || this.prevMediaCount[2] == this.lastMediaCount[2])) {
                    this.listAdapter.notifyItemChanged(i7);
                }
                if (i13 == -1 && (i6 = this.photosRow) != -1) {
                    this.listAdapter.notifyItemInserted(i6);
                } else if (i13 != -1 && this.photosRow == -1) {
                    this.listAdapter.notifyItemRemoved(i13);
                }
                if (i14 == -1 && (i5 = this.filesRow) != -1) {
                    this.listAdapter.notifyItemInserted(i5);
                } else if (i14 != -1 && this.filesRow == -1) {
                    this.listAdapter.notifyItemRemoved(i14);
                }
                if (i15 == -1 && (i4 = this.linksRow) != -1) {
                    this.listAdapter.notifyItemInserted(i4);
                } else if (i15 != -1 && this.linksRow == -1) {
                    this.listAdapter.notifyItemRemoved(i15);
                }
                if (i16 == -1 && (i3 = this.audioRow) != -1) {
                    this.listAdapter.notifyItemInserted(i3);
                } else if (i16 != -1 && this.audioRow == -1) {
                    this.listAdapter.notifyItemRemoved(i16);
                }
                if (i17 == -1 && (i2 = this.voiceRow) != -1) {
                    this.listAdapter.notifyItemInserted(i2);
                } else if (i17 != -1 && this.voiceRow == -1) {
                    this.listAdapter.notifyItemRemoved(i17);
                }
                if (i18 == -1 && (i = this.groupsInCommonRow) != -1) {
                    this.listAdapter.notifyItemInserted(i);
                } else if (i18 != -1 && this.groupsInCommonRow == -1) {
                    this.listAdapter.notifyItemRemoved(i18);
                }
            } else if (i12 != -1 && this.sharedHeaderRow == -1) {
                if (i13 == -1) {
                    i19 = 2;
                }
                if (i14 != -1) {
                    i19++;
                }
                if (i15 != -1) {
                    i19++;
                }
                if (i16 != -1) {
                    i19++;
                }
                if (i17 != -1) {
                    i19++;
                }
                if (i18 != -1) {
                    i19++;
                }
                this.listAdapter.notifyItemRangeChanged(i12, i19);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationStart(boolean z, boolean z2) {
        if (((!z && z2) || (z && !z2)) && this.playProfileAnimation && this.allowProfileAnimation && !this.isPulledDown) {
            this.openAnimationInProgress = true;
        }
        if (z) {
            NotificationCenter.getInstance(this.currentAccount).setAllowedNotificationsDutingAnimation(new int[]{NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.mediaCountDidLoad, NotificationCenter.mediaCountsDidLoad});
            NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(true);
        }
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            if (!z2 && this.playProfileAnimation && this.allowProfileAnimation) {
                this.openAnimationInProgress = false;
                if (this.recreateMenuAfterAnimation) {
                    createActionBarMenu();
                }
            }
            NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(false);
        }
    }

    public float getAnimationProgress() {
        return this.animationProgress;
    }

    @Keep
    public void setAnimationProgress(float f) {
        int i;
        int i2;
        float f2 = f;
        this.animationProgress = f2;
        this.listView.setAlpha(f2);
        this.listView.setTranslationX(((float) AndroidUtilities.dp(48.0f)) - (((float) AndroidUtilities.dp(48.0f)) * f2));
        int profileBackColorForId = AvatarDrawable.getProfileBackColorForId((this.user_id != 0 || (ChatObject.isChannel(this.chat_id, this.currentAccount) && !this.currentChat.megagroup)) ? 5 : this.chat_id);
        int color = Theme.getColor("actionBarDefault");
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        this.topView.setBackgroundColor(Color.rgb(red + ((int) (((float) (Color.red(profileBackColorForId) - red)) * f2)), green + ((int) (((float) (Color.green(profileBackColorForId) - green)) * f2)), blue + ((int) (((float) (Color.blue(profileBackColorForId) - blue)) * f2))));
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
        int i3 = 0;
        while (true) {
            if (i3 >= 2) {
                break;
            }
            SimpleTextView[] simpleTextViewArr = this.nameTextView;
            if (simpleTextViewArr[i3] != null) {
                simpleTextViewArr[i3].setTextColor(Color.argb(alpha + alpha2, red3 + red4, green3 + green4, blue3 + blue4));
            }
            i3++;
        }
        if (this.isOnline[0]) {
            i2 = Theme.getColor("profile_status");
        } else {
            i2 = AvatarDrawable.getProfileTextColorForId((this.user_id != 0 || (ChatObject.isChannel(this.chat_id, this.currentAccount) && !this.currentChat.megagroup)) ? 5 : this.chat_id);
        }
        int i4 = 0;
        int color5 = Theme.getColor(this.isOnline[0] ? "chat_status" : "actionBarDefaultSubtitle");
        int red5 = Color.red(color5);
        int green5 = Color.green(color5);
        int blue5 = Color.blue(color5);
        int alpha3 = Color.alpha(color5);
        int red6 = (int) (((float) (Color.red(i2) - red5)) * f2);
        int green6 = (int) (((float) (Color.green(i2) - green5)) * f2);
        int blue6 = (int) (((float) (Color.blue(i2) - blue5)) * f2);
        int alpha4 = (int) (((float) (Color.alpha(i2) - alpha3)) * f2);
        for (i = 2; i4 < i; i = 2) {
            SimpleTextView[] simpleTextViewArr2 = this.onlineTextView;
            if (simpleTextViewArr2[i4] != null) {
                simpleTextViewArr2[i4].setTextColor(Color.argb(alpha3 + alpha4, red5 + red6, green5 + green6, blue5 + blue6));
            }
            i4++;
        }
        this.extraHeight = this.initialAnimationExtraHeight * f2;
        int i5 = this.user_id;
        if (i5 == 0) {
            i5 = this.chat_id;
        }
        int profileColorForId = AvatarDrawable.getProfileColorForId(i5);
        int i6 = this.user_id;
        if (i6 == 0) {
            i6 = this.chat_id;
        }
        int colorForId = AvatarDrawable.getColorForId(i6);
        if (profileColorForId != colorForId) {
            this.avatarDrawable.setColor(Color.rgb(Color.red(colorForId) + ((int) (((float) (Color.red(profileColorForId) - Color.red(colorForId))) * f2)), Color.green(colorForId) + ((int) (((float) (Color.green(profileColorForId) - Color.green(colorForId))) * f2)), Color.blue(colorForId) + ((int) (((float) (Color.blue(profileColorForId) - Color.blue(colorForId))) * f2))));
            this.avatarImage.invalidate();
        }
        this.topView.invalidate();
        needLayout();
    }

    /* access modifiers changed from: protected */
    public AnimatorSet onCustomTransitionAnimation(boolean z, final Runnable runnable) {
        if (!this.playProfileAnimation || !this.allowProfileAnimation || this.isPulledDown) {
            return null;
        }
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(180);
        this.listView.setLayerType(2, (Paint) null);
        ActionBarMenu createMenu = this.actionBar.createMenu();
        if (createMenu.getItem(10) == null && this.animatingItem == null) {
            this.animatingItem = createMenu.addItem(10, NUM);
        }
        if (z) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.onlineTextView[1].getLayoutParams();
            layoutParams.rightMargin = (int) ((AndroidUtilities.density * -21.0f) + ((float) AndroidUtilities.dp(8.0f)));
            this.onlineTextView[1].setLayoutParams(layoutParams);
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
            this.initialAnimationExtraHeight = AndroidUtilities.dpf2(88.0f);
            this.fragmentView.setBackgroundColor(0);
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
            ActionBarMenuItem actionBarMenuItem = this.animatingItem;
            if (actionBarMenuItem != null) {
                actionBarMenuItem.setAlpha(1.0f);
                arrayList.add(ObjectAnimator.ofFloat(this.animatingItem, View.ALPHA, new float[]{0.0f}));
            }
            ActionBarMenuItem actionBarMenuItem2 = this.callItem;
            if (actionBarMenuItem2 != null) {
                actionBarMenuItem2.setAlpha(0.0f);
                arrayList.add(ObjectAnimator.ofFloat(this.callItem, View.ALPHA, new float[]{1.0f}));
            }
            ActionBarMenuItem actionBarMenuItem3 = this.editItem;
            if (actionBarMenuItem3 != null) {
                actionBarMenuItem3.setAlpha(0.0f);
                arrayList.add(ObjectAnimator.ofFloat(this.editItem, View.ALPHA, new float[]{1.0f}));
            }
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
            ActionBarMenuItem actionBarMenuItem4 = this.animatingItem;
            if (actionBarMenuItem4 != null) {
                actionBarMenuItem4.setAlpha(0.0f);
                arrayList2.add(ObjectAnimator.ofFloat(this.animatingItem, View.ALPHA, new float[]{1.0f}));
            }
            ActionBarMenuItem actionBarMenuItem5 = this.callItem;
            if (actionBarMenuItem5 != null) {
                actionBarMenuItem5.setAlpha(1.0f);
                arrayList2.add(ObjectAnimator.ofFloat(this.callItem, View.ALPHA, new float[]{0.0f}));
            }
            ActionBarMenuItem actionBarMenuItem6 = this.editItem;
            if (actionBarMenuItem6 != null) {
                actionBarMenuItem6.setAlpha(1.0f);
                arrayList2.add(ObjectAnimator.ofFloat(this.editItem, View.ALPHA, new float[]{0.0f}));
            }
            animatorSet.playTogether(arrayList2);
        }
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                ProfileActivity.this.listView.setLayerType(0, (Paint) null);
                if (ProfileActivity.this.animatingItem != null) {
                    ProfileActivity.this.actionBar.createMenu().clearItems();
                    ActionBarMenuItem unused = ProfileActivity.this.animatingItem = null;
                }
                runnable.run();
            }
        });
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.getClass();
        AndroidUtilities.runOnUIThread(new Runnable(animatorSet) {
            private final /* synthetic */ AnimatorSet f$0;

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
    public void updateOnlineCount() {
        int i;
        TLRPC.UserStatus userStatus;
        this.onlineCount = 0;
        int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
        this.sortedUsers.clear();
        TLRPC.ChatFull chatFull = this.chatInfo;
        if ((chatFull instanceof TLRPC.TL_chatFull) || ((chatFull instanceof TLRPC.TL_channelFull) && chatFull.participants_count <= 200 && chatFull.participants != null)) {
            for (int i2 = 0; i2 < this.chatInfo.participants.participants.size(); i2++) {
                TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.chatInfo.participants.participants.get(i2).user_id));
                if (!(user == null || (userStatus = user.status) == null || ((userStatus.expires <= currentTime && user.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) || user.status.expires <= 10000))) {
                    this.onlineCount++;
                }
                this.sortedUsers.add(Integer.valueOf(i2));
            }
            try {
                Collections.sort(this.sortedUsers, new Comparator(currentTime) {
                    private final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final int compare(Object obj, Object obj2) {
                        return ProfileActivity.this.lambda$updateOnlineCount$21$ProfileActivity(this.f$1, (Integer) obj, (Integer) obj2);
                    }
                });
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            ListAdapter listAdapter2 = this.listAdapter;
            if (listAdapter2 != null && (i = this.membersStartRow) > 0) {
                listAdapter2.notifyItemRangeChanged(i, this.sortedUsers.size());
                return;
            }
            return;
        }
        TLRPC.ChatFull chatFull2 = this.chatInfo;
        if ((chatFull2 instanceof TLRPC.TL_channelFull) && chatFull2.participants_count > 200) {
            this.onlineCount = chatFull2.online_count;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x0060  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0078 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0083 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x008e A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x0097 A[ADDED_TO_REGION] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ int lambda$updateOnlineCount$21$ProfileActivity(int r5, java.lang.Integer r6, java.lang.Integer r7) {
        /*
            r4 = this;
            int r0 = r4.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            org.telegram.tgnet.TLRPC$ChatFull r1 = r4.chatInfo
            org.telegram.tgnet.TLRPC$ChatParticipants r1 = r1.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r1 = r1.participants
            int r7 = r7.intValue()
            java.lang.Object r7 = r1.get(r7)
            org.telegram.tgnet.TLRPC$ChatParticipant r7 = (org.telegram.tgnet.TLRPC.ChatParticipant) r7
            int r7 = r7.user_id
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r7 = r0.getUser(r7)
            int r0 = r4.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            org.telegram.tgnet.TLRPC$ChatFull r1 = r4.chatInfo
            org.telegram.tgnet.TLRPC$ChatParticipants r1 = r1.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r1 = r1.participants
            int r6 = r6.intValue()
            java.lang.Object r6 = r1.get(r6)
            org.telegram.tgnet.TLRPC$ChatParticipant r6 = (org.telegram.tgnet.TLRPC.ChatParticipant) r6
            int r6 = r6.user_id
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$User r6 = r0.getUser(r6)
            r0 = 50000(0xCLASSNAME, float:7.0065E-41)
            r1 = -110(0xfffffffffffffvar_, float:NaN)
            r2 = 0
            if (r7 == 0) goto L_0x005d
            boolean r3 = r7.bot
            if (r3 == 0) goto L_0x004f
            r7 = -110(0xfffffffffffffvar_, float:NaN)
            goto L_0x005e
        L_0x004f:
            boolean r3 = r7.self
            if (r3 == 0) goto L_0x0056
            int r7 = r5 + r0
            goto L_0x005e
        L_0x0056:
            org.telegram.tgnet.TLRPC$UserStatus r7 = r7.status
            if (r7 == 0) goto L_0x005d
            int r7 = r7.expires
            goto L_0x005e
        L_0x005d:
            r7 = 0
        L_0x005e:
            if (r6 == 0) goto L_0x0073
            boolean r3 = r6.bot
            if (r3 == 0) goto L_0x0065
            goto L_0x0074
        L_0x0065:
            boolean r1 = r6.self
            if (r1 == 0) goto L_0x006c
            int r1 = r5 + r0
            goto L_0x0074
        L_0x006c:
            org.telegram.tgnet.TLRPC$UserStatus r5 = r6.status
            if (r5 == 0) goto L_0x0073
            int r1 = r5.expires
            goto L_0x0074
        L_0x0073:
            r1 = 0
        L_0x0074:
            r5 = -1
            r6 = 1
            if (r7 <= 0) goto L_0x0081
            if (r1 <= 0) goto L_0x0081
            if (r7 <= r1) goto L_0x007d
            return r6
        L_0x007d:
            if (r7 >= r1) goto L_0x0080
            return r5
        L_0x0080:
            return r2
        L_0x0081:
            if (r7 >= 0) goto L_0x008c
            if (r1 >= 0) goto L_0x008c
            if (r7 <= r1) goto L_0x0088
            return r6
        L_0x0088:
            if (r7 >= r1) goto L_0x008b
            return r5
        L_0x008b:
            return r2
        L_0x008c:
            if (r7 >= 0) goto L_0x0090
            if (r1 > 0) goto L_0x0094
        L_0x0090:
            if (r7 != 0) goto L_0x0095
            if (r1 == 0) goto L_0x0095
        L_0x0094:
            return r5
        L_0x0095:
            if (r1 >= 0) goto L_0x0099
            if (r7 > 0) goto L_0x009d
        L_0x0099:
            if (r1 != 0) goto L_0x009e
            if (r7 == 0) goto L_0x009e
        L_0x009d:
            return r6
        L_0x009e:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.lambda$updateOnlineCount$21$ProfileActivity(int, java.lang.Integer, java.lang.Integer):int");
    }

    public void setChatInfo(TLRPC.ChatFull chatFull) {
        int i;
        this.chatInfo = chatFull;
        TLRPC.ChatFull chatFull2 = this.chatInfo;
        if (!(chatFull2 == null || (i = chatFull2.migrated_from_chat_id) == 0 || this.mergeDialogId != 0)) {
            this.mergeDialogId = (long) (-i);
            MediaDataController.getInstance(this.currentAccount).getMediaCounts(this.mergeDialogId, this.classGuid);
        }
        fetchUsersFromChannelInfo();
    }

    public void setUserInfo(TLRPC.UserFull userFull) {
        this.userInfo = userFull;
    }

    private void fetchUsersFromChannelInfo() {
        TLRPC.Chat chat = this.currentChat;
        if (chat != null && chat.megagroup) {
            TLRPC.ChatFull chatFull = this.chatInfo;
            if ((chatFull instanceof TLRPC.TL_channelFull) && chatFull.participants != null) {
                for (int i = 0; i < this.chatInfo.participants.participants.size(); i++) {
                    TLRPC.ChatParticipant chatParticipant = this.chatInfo.participants.participants.get(i);
                    this.participantsMap.put(chatParticipant.user_id, chatParticipant);
                }
            }
        }
    }

    private void kickUser(int i) {
        if (i != 0) {
            MessagesController.getInstance(this.currentAccount).deleteUserFromChat(this.chat_id, MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i)), this.chatInfo);
            return;
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        if (AndroidUtilities.isTablet()) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, Long.valueOf(-((long) this.chat_id)));
        } else {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
        }
        MessagesController.getInstance(this.currentAccount).deleteUserFromChat(this.chat_id, MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId())), this.chatInfo);
        this.playProfileAnimation = false;
        finishFragment();
    }

    public boolean isChat() {
        return this.chat_id != 0;
    }

    /* access modifiers changed from: private */
    public void updateRowsIds() {
        boolean z;
        int i;
        int i2;
        TLRPC.ChatFull chatFull;
        TLRPC.TL_chatBannedRights tL_chatBannedRights;
        TLRPC.ChatParticipants chatParticipants;
        TLRPC.ChatFull chatFull2;
        TLRPC.ChatFull chatFull3;
        TLRPC.UserFull userFull;
        int i3 = this.rowCount;
        this.rowCount = 0;
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
        this.settingsSectionRow = -1;
        this.membersHeaderRow = -1;
        this.membersStartRow = -1;
        this.membersEndRow = -1;
        this.addMemberRow = -1;
        this.subscribersRow = -1;
        this.administratorsRow = -1;
        this.blockedUsersRow = -1;
        this.membersSectionRow = -1;
        this.sharedHeaderRow = -1;
        this.photosRow = -1;
        this.filesRow = -1;
        this.linksRow = -1;
        this.audioRow = -1;
        this.voiceRow = -1;
        this.groupsInCommonRow = -1;
        this.sharedSectionRow = -1;
        this.unblockRow = -1;
        this.startSecretChatRow = -1;
        this.leaveChannelRow = -1;
        this.joinRow = -1;
        this.lastSectionRow = -1;
        int i4 = 0;
        while (true) {
            int[] iArr = this.lastMediaCount;
            if (i4 >= iArr.length) {
                z = false;
                break;
            } else if (iArr[i4] > 0) {
                z = true;
                break;
            } else {
                i4++;
            }
        }
        if (this.user_id != 0 && LocaleController.isRTL) {
            int i5 = this.rowCount;
            this.rowCount = i5 + 1;
            this.emptyRow = i5;
        }
        if (this.user_id != 0) {
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
            TLRPC.UserFull userFull2 = this.userInfo;
            boolean z2 = (userFull2 != null && !TextUtils.isEmpty(userFull2.about)) || (user != null && !TextUtils.isEmpty(user.username));
            boolean z3 = user != null && !TextUtils.isEmpty(user.phone);
            int i6 = this.rowCount;
            this.rowCount = i6 + 1;
            this.infoHeaderRow = i6;
            if (!this.isBot && (z3 || (!z3 && !z2))) {
                int i7 = this.rowCount;
                this.rowCount = i7 + 1;
                this.phoneRow = i7;
            }
            TLRPC.UserFull userFull3 = this.userInfo;
            if (userFull3 != null && !TextUtils.isEmpty(userFull3.about)) {
                int i8 = this.rowCount;
                this.rowCount = i8 + 1;
                this.userInfoRow = i8;
            }
            if (user != null && !TextUtils.isEmpty(user.username)) {
                int i9 = this.rowCount;
                this.rowCount = i9 + 1;
                this.usernameRow = i9;
            }
            if (!(this.phoneRow == -1 && this.userInfoRow == -1 && this.usernameRow == -1)) {
                int i10 = this.rowCount;
                this.rowCount = i10 + 1;
                this.notificationsDividerRow = i10;
            }
            if (this.user_id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                int i11 = this.rowCount;
                this.rowCount = i11 + 1;
                this.notificationsRow = i11;
            }
            int i12 = this.rowCount;
            this.rowCount = i12 + 1;
            this.infoSectionRow = i12;
            if (this.currentEncryptedChat instanceof TLRPC.TL_encryptedChat) {
                int i13 = this.rowCount;
                this.rowCount = i13 + 1;
                this.settingsTimerRow = i13;
                int i14 = this.rowCount;
                this.rowCount = i14 + 1;
                this.settingsKeyRow = i14;
                int i15 = this.rowCount;
                this.rowCount = i15 + 1;
                this.settingsSectionRow = i15;
            }
            if (z || !((userFull = this.userInfo) == null || userFull.common_chats_count == 0)) {
                int i16 = this.rowCount;
                this.rowCount = i16 + 1;
                this.sharedHeaderRow = i16;
                if (this.lastMediaCount[0] > 0) {
                    int i17 = this.rowCount;
                    this.rowCount = i17 + 1;
                    this.photosRow = i17;
                } else {
                    this.photosRow = -1;
                }
                if (this.lastMediaCount[1] > 0) {
                    int i18 = this.rowCount;
                    this.rowCount = i18 + 1;
                    this.filesRow = i18;
                } else {
                    this.filesRow = -1;
                }
                if (this.lastMediaCount[3] > 0) {
                    int i19 = this.rowCount;
                    this.rowCount = i19 + 1;
                    this.linksRow = i19;
                } else {
                    this.linksRow = -1;
                }
                if (this.lastMediaCount[4] > 0) {
                    int i20 = this.rowCount;
                    this.rowCount = i20 + 1;
                    this.audioRow = i20;
                } else {
                    this.audioRow = -1;
                }
                if (this.lastMediaCount[2] > 0) {
                    int i21 = this.rowCount;
                    this.rowCount = i21 + 1;
                    this.voiceRow = i21;
                } else {
                    this.voiceRow = -1;
                }
                TLRPC.UserFull userFull4 = this.userInfo;
                if (!(userFull4 == null || userFull4.common_chats_count == 0)) {
                    int i22 = this.rowCount;
                    this.rowCount = i22 + 1;
                    this.groupsInCommonRow = i22;
                }
                int i23 = this.rowCount;
                this.rowCount = i23 + 1;
                this.sharedSectionRow = i23;
            }
            if (user != null && !this.isBot && this.currentEncryptedChat == null && user.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                if (this.userBlocked) {
                    int i24 = this.rowCount;
                    this.rowCount = i24 + 1;
                    this.unblockRow = i24;
                    int i25 = this.rowCount;
                    this.rowCount = i25 + 1;
                    this.lastSectionRow = i25;
                } else {
                    int i26 = user.id;
                    if (!(i26 == 333000 || i26 == 777000 || i26 == 42777)) {
                        int i27 = this.rowCount;
                        this.rowCount = i27 + 1;
                        this.startSecretChatRow = i27;
                        int i28 = this.rowCount;
                        this.rowCount = i28 + 1;
                        this.lastSectionRow = i28;
                    }
                }
            }
        } else {
            int i29 = this.chat_id;
            if (i29 != 0) {
                if (i29 > 0) {
                    TLRPC.ChatFull chatFull4 = this.chatInfo;
                    if ((chatFull4 != null && (!TextUtils.isEmpty(chatFull4.about) || (this.chatInfo.location instanceof TLRPC.TL_channelLocation))) || !TextUtils.isEmpty(this.currentChat.username)) {
                        int i30 = this.rowCount;
                        this.rowCount = i30 + 1;
                        this.infoHeaderRow = i30;
                        TLRPC.ChatFull chatFull5 = this.chatInfo;
                        if (chatFull5 != null) {
                            if (!TextUtils.isEmpty(chatFull5.about)) {
                                int i31 = this.rowCount;
                                this.rowCount = i31 + 1;
                                this.channelInfoRow = i31;
                            }
                            if (this.chatInfo.location instanceof TLRPC.TL_channelLocation) {
                                int i32 = this.rowCount;
                                this.rowCount = i32 + 1;
                                this.locationRow = i32;
                            }
                        }
                        if (!TextUtils.isEmpty(this.currentChat.username)) {
                            int i33 = this.rowCount;
                            this.rowCount = i33 + 1;
                            this.usernameRow = i33;
                        }
                    }
                    if (this.infoHeaderRow != -1) {
                        int i34 = this.rowCount;
                        this.rowCount = i34 + 1;
                        this.notificationsDividerRow = i34;
                    }
                    int i35 = this.rowCount;
                    this.rowCount = i35 + 1;
                    this.notificationsRow = i35;
                    int i36 = this.rowCount;
                    this.rowCount = i36 + 1;
                    this.infoSectionRow = i36;
                    if (ChatObject.isChannel(this.currentChat)) {
                        TLRPC.Chat chat = this.currentChat;
                        if (!chat.megagroup && (chatFull3 = this.chatInfo) != null && (chat.creator || chatFull3.can_view_participants)) {
                            int i37 = this.rowCount;
                            this.rowCount = i37 + 1;
                            this.membersHeaderRow = i37;
                            int i38 = this.rowCount;
                            this.rowCount = i38 + 1;
                            this.subscribersRow = i38;
                            int i39 = this.rowCount;
                            this.rowCount = i39 + 1;
                            this.administratorsRow = i39;
                            TLRPC.ChatFull chatFull6 = this.chatInfo;
                            if (!(chatFull6.banned_count == 0 && chatFull6.kicked_count == 0)) {
                                int i40 = this.rowCount;
                                this.rowCount = i40 + 1;
                                this.blockedUsersRow = i40;
                            }
                            int i41 = this.rowCount;
                            this.rowCount = i41 + 1;
                            this.membersSectionRow = i41;
                        }
                    }
                    if (z) {
                        int i42 = this.rowCount;
                        this.rowCount = i42 + 1;
                        this.sharedHeaderRow = i42;
                        if (this.lastMediaCount[0] > 0) {
                            int i43 = this.rowCount;
                            this.rowCount = i43 + 1;
                            this.photosRow = i43;
                        } else {
                            this.photosRow = -1;
                        }
                        if (this.lastMediaCount[1] > 0) {
                            int i44 = this.rowCount;
                            this.rowCount = i44 + 1;
                            this.filesRow = i44;
                        } else {
                            this.filesRow = -1;
                        }
                        if (this.lastMediaCount[3] > 0) {
                            int i45 = this.rowCount;
                            this.rowCount = i45 + 1;
                            this.linksRow = i45;
                        } else {
                            this.linksRow = -1;
                        }
                        if (this.lastMediaCount[4] > 0) {
                            int i46 = this.rowCount;
                            this.rowCount = i46 + 1;
                            this.audioRow = i46;
                        } else {
                            this.audioRow = -1;
                        }
                        if (this.lastMediaCount[2] > 0) {
                            int i47 = this.rowCount;
                            this.rowCount = i47 + 1;
                            this.voiceRow = i47;
                        } else {
                            this.voiceRow = -1;
                        }
                        int i48 = this.rowCount;
                        this.rowCount = i48 + 1;
                        this.sharedSectionRow = i48;
                    }
                    if (ChatObject.isChannel(this.currentChat)) {
                        TLRPC.Chat chat2 = this.currentChat;
                        if (!chat2.creator && !chat2.left && !chat2.kicked && !chat2.megagroup) {
                            int i49 = this.rowCount;
                            this.rowCount = i49 + 1;
                            this.leaveChannelRow = i49;
                            int i50 = this.rowCount;
                            this.rowCount = i50 + 1;
                            this.lastSectionRow = i50;
                        }
                        TLRPC.ChatFull chatFull7 = this.chatInfo;
                        if (chatFull7 != null && this.currentChat.megagroup && (chatParticipants = chatFull7.participants) != null && !chatParticipants.participants.isEmpty()) {
                            if (!ChatObject.isNotInChat(this.currentChat)) {
                                TLRPC.Chat chat3 = this.currentChat;
                                if (chat3.megagroup && ChatObject.canAddUsers(chat3) && ((chatFull2 = this.chatInfo) == null || chatFull2.participants_count < MessagesController.getInstance(this.currentAccount).maxMegagroupCount)) {
                                    int i51 = this.rowCount;
                                    this.rowCount = i51 + 1;
                                    this.addMemberRow = i51;
                                    int i52 = this.rowCount;
                                    this.membersStartRow = i52;
                                    this.rowCount = i52 + this.chatInfo.participants.participants.size();
                                    int i53 = this.rowCount;
                                    this.membersEndRow = i53;
                                    this.rowCount = i53 + 1;
                                    this.membersSectionRow = i53;
                                }
                            }
                            int i54 = this.rowCount;
                            this.rowCount = i54 + 1;
                            this.membersHeaderRow = i54;
                            int i522 = this.rowCount;
                            this.membersStartRow = i522;
                            this.rowCount = i522 + this.chatInfo.participants.participants.size();
                            int i532 = this.rowCount;
                            this.membersEndRow = i532;
                            this.rowCount = i532 + 1;
                            this.membersSectionRow = i532;
                        }
                        if (this.lastSectionRow == -1) {
                            TLRPC.Chat chat4 = this.currentChat;
                            if (chat4.left && !chat4.kicked) {
                                int i55 = this.rowCount;
                                this.rowCount = i55 + 1;
                                this.joinRow = i55;
                                int i56 = this.rowCount;
                                this.rowCount = i56 + 1;
                                this.lastSectionRow = i56;
                            }
                        }
                    } else {
                        TLRPC.ChatFull chatFull8 = this.chatInfo;
                        if (chatFull8 != null && !(chatFull8.participants instanceof TLRPC.TL_chatParticipantsForbidden)) {
                            if (ChatObject.canAddUsers(this.currentChat) || (tL_chatBannedRights = this.currentChat.default_banned_rights) == null || !tL_chatBannedRights.invite_users) {
                                int i57 = this.rowCount;
                                this.rowCount = i57 + 1;
                                this.addMemberRow = i57;
                            } else {
                                int i58 = this.rowCount;
                                this.rowCount = i58 + 1;
                                this.membersHeaderRow = i58;
                            }
                            int i59 = this.rowCount;
                            this.membersStartRow = i59;
                            this.rowCount = i59 + this.chatInfo.participants.participants.size();
                            int i60 = this.rowCount;
                            this.membersEndRow = i60;
                            this.rowCount = i60 + 1;
                            this.membersSectionRow = i60;
                        }
                    }
                } else if (!ChatObject.isChannel(this.currentChat) && (chatFull = this.chatInfo) != null) {
                    TLRPC.ChatParticipants chatParticipants2 = chatFull.participants;
                    if (!(chatParticipants2 instanceof TLRPC.TL_chatParticipantsForbidden)) {
                        int i61 = this.rowCount;
                        this.rowCount = i61 + 1;
                        this.membersHeaderRow = i61;
                        int i62 = this.rowCount;
                        this.membersStartRow = i62;
                        this.rowCount = i62 + chatParticipants2.participants.size();
                        int i63 = this.rowCount;
                        this.membersEndRow = i63;
                        this.rowCount = i63 + 1;
                        this.membersSectionRow = i63;
                        int i64 = this.rowCount;
                        this.rowCount = i64 + 1;
                        this.addMemberRow = i64;
                    }
                }
            }
        }
        int i65 = this.rowCount;
        this.rowCount = i65 + 1;
        this.bottomPaddingRow = i65;
        if (this.actionBar != null) {
            i = ActionBar.getCurrentActionBarHeight() + (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0);
        } else {
            i = 0;
        }
        if (this.listView == null || i3 > this.rowCount || ((i2 = this.listContentHeight) != 0 && i2 + i + AndroidUtilities.dp(88.0f) < this.listView.getMeasuredHeight())) {
            this.lastMeasuredContentWidth = 0;
        }
    }

    private Drawable getScamDrawable() {
        if (this.scamDrawable == null) {
            this.scamDrawable = new ScamDrawable(11);
            this.scamDrawable.setColor(Theme.getColor("avatar_subtitleInProfileBlue"));
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

    /* JADX WARNING: Removed duplicated region for block: B:164:0x03d4  */
    /* JADX WARNING: Removed duplicated region for block: B:199:0x046c  */
    /* JADX WARNING: Removed duplicated region for block: B:204:0x047a A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x056e  */
    /* JADX WARNING: Removed duplicated region for block: B:239:0x0575  */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x057a  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x01c8  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x01cd  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x01d0  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x01f4  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateProfileData() {
        /*
            r24 = this;
            r0 = r24
            org.telegram.ui.ProfileActivity$AvatarImageView r1 = r0.avatarImage
            if (r1 == 0) goto L_0x05b3
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r0.nameTextView
            if (r1 != 0) goto L_0x000c
            goto L_0x05b3
        L_0x000c:
            int r1 = r0.currentAccount
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1)
            int r1 = r1.getConnectionState()
            r2 = 2
            r4 = 1
            if (r1 != r2) goto L_0x0024
            r1 = 2131626991(0x7f0e0bef, float:1.8881234E38)
            java.lang.String r5 = "WaitingForNetwork"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r1)
            goto L_0x004b
        L_0x0024:
            if (r1 != r4) goto L_0x0030
            r1 = 2131624733(0x7f0e031d, float:1.8876654E38)
            java.lang.String r5 = "Connecting"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r1)
            goto L_0x004b
        L_0x0030:
            r5 = 5
            if (r1 != r5) goto L_0x003d
            r1 = 2131626840(0x7f0e0b58, float:1.8880928E38)
            java.lang.String r5 = "Updating"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r1)
            goto L_0x004b
        L_0x003d:
            r5 = 4
            if (r1 != r5) goto L_0x004a
            r1 = 2131624735(0x7f0e031f, float:1.8876658E38)
            java.lang.String r5 = "ConnectingToProxy"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r1)
            goto L_0x004b
        L_0x004a:
            r1 = 0
        L_0x004b:
            int r5 = r0.user_id
            java.lang.String r6 = "50_50"
            r7 = 0
            if (r5 == 0) goto L_0x0229
            int r5 = r0.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            int r8 = r0.user_id
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            org.telegram.tgnet.TLRPC$User r5 = r5.getUser(r8)
            org.telegram.tgnet.TLRPC$UserProfilePhoto r8 = r5.photo
            if (r8 == 0) goto L_0x0069
            org.telegram.tgnet.TLRPC$FileLocation r8 = r8.photo_big
            goto L_0x006a
        L_0x0069:
            r8 = 0
        L_0x006a:
            org.telegram.ui.Components.AvatarDrawable r9 = r0.avatarDrawable
            r9.setInfo((org.telegram.tgnet.TLRPC.User) r5)
            org.telegram.messenger.ImageLocation r10 = org.telegram.messenger.ImageLocation.getForUser(r5, r4)
            org.telegram.messenger.ImageLocation r9 = org.telegram.messenger.ImageLocation.getForUser(r5, r7)
            org.telegram.ui.Components.ProfileGalleryView r11 = r0.avatarsViewPager
            r11.initIfEmpty(r10, r9)
            org.telegram.ui.ProfileActivity$AvatarImageView r11 = r0.avatarImage
            org.telegram.ui.Components.AvatarDrawable r12 = r0.avatarDrawable
            r11.setImage((org.telegram.messenger.ImageLocation) r9, (java.lang.String) r6, (android.graphics.drawable.Drawable) r12, (java.lang.Object) r5)
            int r6 = r0.currentAccount
            org.telegram.messenger.FileLoader r9 = org.telegram.messenger.FileLoader.getInstance(r6)
            r12 = 0
            r13 = 0
            r14 = 1
            r11 = r5
            r9.loadFile(r10, r11, r12, r13, r14)
            java.lang.String r6 = org.telegram.messenger.UserObject.getUserName(r5)
            int r9 = r5.id
            int r10 = r0.currentAccount
            org.telegram.messenger.UserConfig r10 = org.telegram.messenger.UserConfig.getInstance(r10)
            int r10 = r10.getClientUserId()
            if (r9 != r10) goto L_0x00bb
            r6 = 2131624632(0x7f0e02b8, float:1.887645E38)
            java.lang.String r9 = "ChatYourSelf"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r9 = 2131624637(0x7f0e02bd, float:1.887646E38)
            java.lang.String r10 = "ChatYourSelfName"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r23 = r9
            r9 = r6
            r6 = r23
            goto L_0x0126
        L_0x00bb:
            int r9 = r5.id
            r10 = 333000(0x514c8, float:4.66632E-40)
            if (r9 == r10) goto L_0x011d
            r10 = 777000(0xbdb28, float:1.088809E-39)
            if (r9 == r10) goto L_0x011d
            r10 = 42777(0xa719, float:5.9943E-41)
            if (r9 != r10) goto L_0x00cd
            goto L_0x011d
        L_0x00cd:
            boolean r9 = org.telegram.messenger.MessagesController.isSupportUser(r5)
            if (r9 == 0) goto L_0x00dd
            r9 = 2131626663(0x7f0e0aa7, float:1.8880569E38)
            java.lang.String r10 = "SupportStatus"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            goto L_0x0126
        L_0x00dd:
            boolean r9 = r0.isBot
            if (r9 == 0) goto L_0x00eb
            r9 = 2131624423(0x7f0e01e7, float:1.8876025E38)
            java.lang.String r10 = "Bot"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            goto L_0x0126
        L_0x00eb:
            boolean[] r9 = r0.isOnline
            r9[r7] = r7
            int r10 = r0.currentAccount
            java.lang.String r9 = org.telegram.messenger.LocaleController.formatUserStatus(r10, r5, r9)
            org.telegram.ui.ActionBar.SimpleTextView[] r10 = r0.onlineTextView
            r10 = r10[r4]
            if (r10 == 0) goto L_0x0126
            boolean[] r10 = r0.isOnline
            boolean r10 = r10[r7]
            if (r10 == 0) goto L_0x0104
            java.lang.String r10 = "profile_status"
            goto L_0x0106
        L_0x0104:
            java.lang.String r10 = "avatar_subtitleInProfileBlue"
        L_0x0106:
            org.telegram.ui.ActionBar.SimpleTextView[] r11 = r0.onlineTextView
            r11 = r11[r4]
            r11.setTag(r10)
            boolean r11 = r0.isPulledDown
            if (r11 != 0) goto L_0x0126
            org.telegram.ui.ActionBar.SimpleTextView[] r11 = r0.onlineTextView
            r11 = r11[r4]
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r11.setTextColor(r10)
            goto L_0x0126
        L_0x011d:
            r9 = 2131626511(0x7f0e0a0f, float:1.888026E38)
            java.lang.String r10 = "ServiceNotifications"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
        L_0x0126:
            r10 = 0
        L_0x0127:
            if (r10 >= r2) goto L_0x0219
            org.telegram.ui.ActionBar.SimpleTextView[] r11 = r0.nameTextView
            r11 = r11[r10]
            if (r11 != 0) goto L_0x0131
            goto L_0x0215
        L_0x0131:
            if (r10 != 0) goto L_0x01aa
            int r11 = r5.id
            int r12 = r0.currentAccount
            org.telegram.messenger.UserConfig r12 = org.telegram.messenger.UserConfig.getInstance(r12)
            int r12 = r12.getClientUserId()
            if (r11 == r12) goto L_0x01aa
            int r11 = r5.id
            int r12 = r11 / 1000
            r13 = 777(0x309, float:1.089E-42)
            if (r12 == r13) goto L_0x01aa
            int r11 = r11 / 1000
            r12 = 333(0x14d, float:4.67E-43)
            if (r11 == r12) goto L_0x01aa
            java.lang.String r11 = r5.phone
            if (r11 == 0) goto L_0x01aa
            int r11 = r11.length()
            if (r11 == 0) goto L_0x01aa
            int r11 = r0.currentAccount
            org.telegram.messenger.ContactsController r11 = org.telegram.messenger.ContactsController.getInstance(r11)
            java.util.concurrent.ConcurrentHashMap<java.lang.Integer, org.telegram.tgnet.TLRPC$TL_contact> r11 = r11.contactsDict
            int r12 = r5.id
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.Object r11 = r11.get(r12)
            if (r11 != 0) goto L_0x01aa
            int r11 = r0.currentAccount
            org.telegram.messenger.ContactsController r11 = org.telegram.messenger.ContactsController.getInstance(r11)
            java.util.concurrent.ConcurrentHashMap<java.lang.Integer, org.telegram.tgnet.TLRPC$TL_contact> r11 = r11.contactsDict
            int r11 = r11.size()
            if (r11 != 0) goto L_0x0187
            int r11 = r0.currentAccount
            org.telegram.messenger.ContactsController r11 = org.telegram.messenger.ContactsController.getInstance(r11)
            boolean r11 = r11.isLoadingContacts()
            if (r11 != 0) goto L_0x01aa
        L_0x0187:
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
            goto L_0x01b1
        L_0x01aa:
            org.telegram.ui.ActionBar.SimpleTextView[] r11 = r0.nameTextView
            r11 = r11[r10]
            r11.setText(r6)
        L_0x01b1:
            if (r10 != 0) goto L_0x01bd
            if (r1 == 0) goto L_0x01bd
            org.telegram.ui.ActionBar.SimpleTextView[] r11 = r0.onlineTextView
            r11 = r11[r10]
            r11.setText(r1)
            goto L_0x01c4
        L_0x01bd:
            org.telegram.ui.ActionBar.SimpleTextView[] r11 = r0.onlineTextView
            r11 = r11[r10]
            r11.setText(r9)
        L_0x01c4:
            org.telegram.tgnet.TLRPC$EncryptedChat r11 = r0.currentEncryptedChat
            if (r11 == 0) goto L_0x01cd
            android.graphics.drawable.Drawable r11 = r24.getLockIconDrawable()
            goto L_0x01ce
        L_0x01cd:
            r11 = 0
        L_0x01ce:
            if (r10 != 0) goto L_0x01f4
            boolean r12 = r5.scam
            if (r12 == 0) goto L_0x01d9
            android.graphics.drawable.Drawable r12 = r24.getScamDrawable()
            goto L_0x0207
        L_0x01d9:
            int r12 = r0.currentAccount
            org.telegram.messenger.MessagesController r12 = org.telegram.messenger.MessagesController.getInstance(r12)
            long r13 = r0.dialog_id
            r15 = 0
            int r17 = (r13 > r15 ? 1 : (r13 == r15 ? 0 : -1))
            if (r17 == 0) goto L_0x01e8
            goto L_0x01eb
        L_0x01e8:
            int r13 = r0.user_id
            long r13 = (long) r13
        L_0x01eb:
            boolean r12 = r12.isDialogMuted(r13)
            if (r12 == 0) goto L_0x0206
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.chat_muteIconDrawable
            goto L_0x0207
        L_0x01f4:
            boolean r12 = r5.scam
            if (r12 == 0) goto L_0x01fd
            android.graphics.drawable.Drawable r12 = r24.getScamDrawable()
            goto L_0x0207
        L_0x01fd:
            boolean r12 = r5.verified
            if (r12 == 0) goto L_0x0206
            android.graphics.drawable.Drawable r12 = r24.getVerifiedCrossfadeDrawable()
            goto L_0x0207
        L_0x0206:
            r12 = 0
        L_0x0207:
            org.telegram.ui.ActionBar.SimpleTextView[] r13 = r0.nameTextView
            r13 = r13[r10]
            r13.setLeftDrawable((android.graphics.drawable.Drawable) r11)
            org.telegram.ui.ActionBar.SimpleTextView[] r11 = r0.nameTextView
            r11 = r11[r10]
            r11.setRightDrawable((android.graphics.drawable.Drawable) r12)
        L_0x0215:
            int r10 = r10 + 1
            goto L_0x0127
        L_0x0219:
            org.telegram.ui.ProfileActivity$AvatarImageView r1 = r0.avatarImage
            org.telegram.messenger.ImageReceiver r1 = r1.getImageReceiver()
            boolean r2 = org.telegram.ui.PhotoViewer.isShowingImage((org.telegram.tgnet.TLRPC.FileLocation) r8)
            r2 = r2 ^ r4
            r1.setVisible(r2, r7)
            goto L_0x05b3
        L_0x0229:
            int r5 = r0.chat_id
            if (r5 == 0) goto L_0x05b3
            int r5 = r0.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            int r8 = r0.chat_id
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            org.telegram.tgnet.TLRPC$Chat r5 = r5.getChat(r8)
            if (r5 == 0) goto L_0x0242
            r0.currentChat = r5
            goto L_0x0244
        L_0x0242:
            org.telegram.tgnet.TLRPC$Chat r5 = r0.currentChat
        L_0x0244:
            r10 = r5
            boolean r5 = org.telegram.messenger.ChatObject.isChannel(r10)
            java.lang.String r8 = "MegaPublic"
            java.lang.String r11 = "MegaPrivate"
            java.lang.String r13 = "MegaLocation"
            java.lang.String r14 = "OnlineCount"
            java.lang.String r15 = "%s, %s"
            java.lang.String r3 = "Subscribers"
            java.lang.String r9 = "Members"
            if (r5 == 0) goto L_0x037e
            org.telegram.tgnet.TLRPC$ChatFull r5 = r0.chatInfo
            if (r5 == 0) goto L_0x0348
            org.telegram.tgnet.TLRPC$Chat r12 = r0.currentChat
            boolean r7 = r12.megagroup
            if (r7 != 0) goto L_0x0275
            int r5 = r5.participants_count
            if (r5 == 0) goto L_0x0348
            boolean r5 = org.telegram.messenger.ChatObject.hasAdminRights(r12)
            if (r5 != 0) goto L_0x0348
            org.telegram.tgnet.TLRPC$ChatFull r5 = r0.chatInfo
            boolean r5 = r5.can_view_participants
            if (r5 == 0) goto L_0x0275
            goto L_0x0348
        L_0x0275:
            org.telegram.tgnet.TLRPC$Chat r5 = r0.currentChat
            boolean r5 = r5.megagroup
            if (r5 == 0) goto L_0x030c
            int r5 = r0.onlineCount
            if (r5 <= r4) goto L_0x02c6
            org.telegram.tgnet.TLRPC$ChatFull r5 = r0.chatInfo
            int r5 = r5.participants_count
            if (r5 == 0) goto L_0x02c6
            java.lang.Object[] r7 = new java.lang.Object[r2]
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r9, r5)
            r12 = 0
            r7[r12] = r5
            int r5 = r0.onlineCount
            org.telegram.tgnet.TLRPC$ChatFull r12 = r0.chatInfo
            int r12 = r12.participants_count
            int r5 = java.lang.Math.min(r5, r12)
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r14, r5)
            r7[r4] = r5
            java.lang.String r5 = java.lang.String.format(r15, r7)
            java.lang.Object[] r7 = new java.lang.Object[r2]
            org.telegram.tgnet.TLRPC$ChatFull r12 = r0.chatInfo
            int r12 = r12.participants_count
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatPluralStringComma(r9, r12)
            r18 = 0
            r7[r18] = r12
            int r12 = r0.onlineCount
            org.telegram.tgnet.TLRPC$ChatFull r2 = r0.chatInfo
            int r2 = r2.participants_count
            int r2 = java.lang.Math.min(r12, r2)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralStringComma(r14, r2)
            r7[r4] = r2
            java.lang.String r2 = java.lang.String.format(r15, r7)
            goto L_0x0341
        L_0x02c6:
            org.telegram.tgnet.TLRPC$ChatFull r2 = r0.chatInfo
            int r2 = r2.participants_count
            if (r2 != 0) goto L_0x02ff
            boolean r2 = r10.has_geo
            if (r2 == 0) goto L_0x02dd
            r2 = 2131625517(0x7f0e062d, float:1.8878244E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r13, r2)
            java.lang.String r5 = r5.toLowerCase()
            goto L_0x038d
        L_0x02dd:
            java.lang.String r2 = r10.username
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x02f2
            r2 = 2131625521(0x7f0e0631, float:1.8878252E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r8, r2)
            java.lang.String r5 = r2.toLowerCase()
            goto L_0x038d
        L_0x02f2:
            r2 = 2131625518(0x7f0e062e, float:1.8878246E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r11, r2)
            java.lang.String r5 = r5.toLowerCase()
            goto L_0x038d
        L_0x02ff:
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r9, r2)
            org.telegram.tgnet.TLRPC$ChatFull r2 = r0.chatInfo
            int r2 = r2.participants_count
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralStringComma(r9, r2)
            goto L_0x0341
        L_0x030c:
            int[] r2 = new int[r4]
            org.telegram.tgnet.TLRPC$ChatFull r5 = r0.chatInfo
            int r5 = r5.participants_count
            org.telegram.messenger.LocaleController.formatShortNumber(r5, r2)
            org.telegram.tgnet.TLRPC$Chat r2 = r0.currentChat
            boolean r2 = r2.megagroup
            if (r2 == 0) goto L_0x032c
            org.telegram.tgnet.TLRPC$ChatFull r2 = r0.chatInfo
            int r2 = r2.participants_count
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r9, r2)
            org.telegram.tgnet.TLRPC$ChatFull r5 = r0.chatInfo
            int r5 = r5.participants_count
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralStringComma(r9, r5)
            goto L_0x033c
        L_0x032c:
            org.telegram.tgnet.TLRPC$ChatFull r2 = r0.chatInfo
            int r2 = r2.participants_count
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r3, r2)
            org.telegram.tgnet.TLRPC$ChatFull r5 = r0.chatInfo
            int r5 = r5.participants_count
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralStringComma(r3, r5)
        L_0x033c:
            r23 = r5
            r5 = r2
            r2 = r23
        L_0x0341:
            r23 = r5
            r5 = r2
            r2 = r23
            goto L_0x03cf
        L_0x0348:
            org.telegram.tgnet.TLRPC$Chat r2 = r0.currentChat
            boolean r2 = r2.megagroup
            if (r2 == 0) goto L_0x035c
            r2 = 2131625443(0x7f0e05e3, float:1.8878094E38)
            java.lang.String r5 = "Loading"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r5, r2)
            java.lang.String r5 = r2.toLowerCase()
            goto L_0x038d
        L_0x035c:
            int r2 = r10.flags
            r2 = r2 & 64
            if (r2 == 0) goto L_0x0370
            r2 = 2131624571(0x7f0e027b, float:1.8876325E38)
            java.lang.String r5 = "ChannelPublic"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r5, r2)
            java.lang.String r5 = r2.toLowerCase()
            goto L_0x038d
        L_0x0370:
            r2 = 2131624568(0x7f0e0278, float:1.887632E38)
            java.lang.String r5 = "ChannelPrivate"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r5, r2)
            java.lang.String r5 = r2.toLowerCase()
            goto L_0x038d
        L_0x037e:
            boolean r2 = org.telegram.messenger.ChatObject.isKickedFromChat(r10)
            if (r2 == 0) goto L_0x038f
            r2 = 2131627182(0x7f0e0cae, float:1.8881621E38)
            java.lang.String r5 = "YouWereKicked"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r2)
        L_0x038d:
            r2 = r5
            goto L_0x03cf
        L_0x038f:
            boolean r2 = org.telegram.messenger.ChatObject.isLeftFromChat(r10)
            if (r2 == 0) goto L_0x039f
            r2 = 2131627181(0x7f0e0cad, float:1.888162E38)
            java.lang.String r5 = "YouLeft"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r2)
            goto L_0x038d
        L_0x039f:
            int r2 = r10.participants_count
            org.telegram.tgnet.TLRPC$ChatFull r5 = r0.chatInfo
            if (r5 == 0) goto L_0x03ad
            org.telegram.tgnet.TLRPC$ChatParticipants r2 = r5.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r2 = r2.participants
            int r2 = r2.size()
        L_0x03ad:
            if (r2 == 0) goto L_0x03ca
            int r5 = r0.onlineCount
            if (r5 <= r4) goto L_0x03ca
            r5 = 2
            java.lang.Object[] r7 = new java.lang.Object[r5]
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r9, r2)
            r5 = 0
            r7[r5] = r2
            int r2 = r0.onlineCount
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r14, r2)
            r7[r4] = r2
            java.lang.String r5 = java.lang.String.format(r15, r7)
            goto L_0x038d
        L_0x03ca:
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r9, r2)
            goto L_0x038d
        L_0x03cf:
            r7 = 0
            r12 = 0
        L_0x03d1:
            r14 = 2
            if (r7 >= r14) goto L_0x056c
            org.telegram.ui.ActionBar.SimpleTextView[] r15 = r0.nameTextView
            r19 = r15[r7]
            if (r19 != 0) goto L_0x03e2
            r21 = r1
            r22 = r2
            r20 = r5
            goto L_0x0561
        L_0x03e2:
            java.lang.String r14 = r10.title
            if (r14 == 0) goto L_0x03ef
            r15 = r15[r7]
            boolean r14 = r15.setText(r14)
            if (r14 == 0) goto L_0x03ef
            r12 = 1
        L_0x03ef:
            org.telegram.ui.ActionBar.SimpleTextView[] r14 = r0.nameTextView
            r14 = r14[r7]
            r15 = 0
            r14.setLeftDrawable((android.graphics.drawable.Drawable) r15)
            if (r7 == 0) goto L_0x0422
            boolean r14 = r10.scam
            if (r14 == 0) goto L_0x0409
            org.telegram.ui.ActionBar.SimpleTextView[] r14 = r0.nameTextView
            r14 = r14[r7]
            android.graphics.drawable.Drawable r15 = r24.getScamDrawable()
            r14.setRightDrawable((android.graphics.drawable.Drawable) r15)
            goto L_0x0432
        L_0x0409:
            boolean r14 = r10.verified
            if (r14 == 0) goto L_0x0419
            org.telegram.ui.ActionBar.SimpleTextView[] r14 = r0.nameTextView
            r14 = r14[r7]
            android.graphics.drawable.Drawable r15 = r24.getVerifiedCrossfadeDrawable()
            r14.setRightDrawable((android.graphics.drawable.Drawable) r15)
            goto L_0x0432
        L_0x0419:
            org.telegram.ui.ActionBar.SimpleTextView[] r14 = r0.nameTextView
            r14 = r14[r7]
            r15 = 0
            r14.setRightDrawable((android.graphics.drawable.Drawable) r15)
            goto L_0x0432
        L_0x0422:
            r15 = 0
            boolean r14 = r10.scam
            if (r14 == 0) goto L_0x0435
            org.telegram.ui.ActionBar.SimpleTextView[] r14 = r0.nameTextView
            r14 = r14[r7]
            android.graphics.drawable.Drawable r15 = r24.getScamDrawable()
            r14.setRightDrawable((android.graphics.drawable.Drawable) r15)
        L_0x0432:
            r20 = r5
            goto L_0x0452
        L_0x0435:
            org.telegram.ui.ActionBar.SimpleTextView[] r14 = r0.nameTextView
            r14 = r14[r7]
            int r15 = r0.currentAccount
            org.telegram.messenger.MessagesController r15 = org.telegram.messenger.MessagesController.getInstance(r15)
            int r4 = r0.chat_id
            int r4 = -r4
            r20 = r5
            long r4 = (long) r4
            boolean r4 = r15.isDialogMuted(r4)
            if (r4 == 0) goto L_0x044e
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.chat_muteIconDrawable
            goto L_0x044f
        L_0x044e:
            r4 = 0
        L_0x044f:
            r14.setRightDrawable((android.graphics.drawable.Drawable) r4)
        L_0x0452:
            if (r7 != 0) goto L_0x045e
            if (r1 == 0) goto L_0x045e
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r0.onlineTextView
            r4 = r4[r7]
            r4.setText(r1)
            goto L_0x04c3
        L_0x045e:
            org.telegram.tgnet.TLRPC$Chat r4 = r0.currentChat
            boolean r4 = r4.megagroup
            if (r4 == 0) goto L_0x047a
            org.telegram.tgnet.TLRPC$ChatFull r4 = r0.chatInfo
            if (r4 == 0) goto L_0x047a
            int r4 = r0.onlineCount
            if (r4 <= 0) goto L_0x047a
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r0.onlineTextView
            r4 = r4[r7]
            if (r7 != 0) goto L_0x0474
            r5 = r2
            goto L_0x0476
        L_0x0474:
            r5 = r20
        L_0x0476:
            r4.setText(r5)
            goto L_0x04c3
        L_0x047a:
            if (r7 != 0) goto L_0x054f
            org.telegram.tgnet.TLRPC$Chat r4 = r0.currentChat
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r4)
            if (r4 == 0) goto L_0x054f
            org.telegram.tgnet.TLRPC$ChatFull r4 = r0.chatInfo
            if (r4 == 0) goto L_0x054f
            int r4 = r4.participants_count
            if (r4 == 0) goto L_0x054f
            org.telegram.tgnet.TLRPC$Chat r4 = r0.currentChat
            boolean r5 = r4.megagroup
            if (r5 != 0) goto L_0x0496
            boolean r4 = r4.broadcast
            if (r4 == 0) goto L_0x054f
        L_0x0496:
            r4 = 1
            int[] r5 = new int[r4]
            org.telegram.tgnet.TLRPC$ChatFull r4 = r0.chatInfo
            int r4 = r4.participants_count
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatShortNumber(r4, r5)
            org.telegram.tgnet.TLRPC$Chat r14 = r0.currentChat
            boolean r14 = r14.megagroup
            if (r14 == 0) goto L_0x0526
            org.telegram.tgnet.TLRPC$ChatFull r14 = r0.chatInfo
            int r14 = r14.participants_count
            if (r14 != 0) goto L_0x04fa
            boolean r4 = r10.has_geo
            if (r4 == 0) goto L_0x04c9
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r0.onlineTextView
            r4 = r4[r7]
            r14 = 2131625517(0x7f0e062d, float:1.8878244E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r13, r14)
            java.lang.String r5 = r5.toLowerCase()
            r4.setText(r5)
        L_0x04c3:
            r21 = r1
            r22 = r2
            goto L_0x0561
        L_0x04c9:
            r14 = 2131625517(0x7f0e062d, float:1.8878244E38)
            java.lang.String r4 = r10.username
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x04e7
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r0.onlineTextView
            r4 = r4[r7]
            r5 = 2131625521(0x7f0e0631, float:1.8878252E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r8, r5)
            java.lang.String r5 = r5.toLowerCase()
            r4.setText(r5)
            goto L_0x04c3
        L_0x04e7:
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r0.onlineTextView
            r4 = r4[r7]
            r15 = 2131625518(0x7f0e062e, float:1.8878246E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r11, r15)
            java.lang.String r5 = r5.toLowerCase()
            r4.setText(r5)
            goto L_0x04c3
        L_0x04fa:
            r15 = 2131625518(0x7f0e062e, float:1.8878246E38)
            org.telegram.ui.ActionBar.SimpleTextView[] r14 = r0.onlineTextView
            r14 = r14[r7]
            r17 = 0
            r15 = r5[r17]
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatPluralString(r9, r15)
            r21 = r1
            r22 = r2
            r1 = 1
            java.lang.Object[] r2 = new java.lang.Object[r1]
            r1 = r5[r17]
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r2[r17] = r1
            java.lang.String r1 = "%d"
            java.lang.String r1 = java.lang.String.format(r1, r2)
            java.lang.String r1 = r15.replace(r1, r4)
            r14.setText(r1)
            goto L_0x0561
        L_0x0526:
            r21 = r1
            r22 = r2
            r17 = 0
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r0.onlineTextView
            r1 = r1[r7]
            r2 = r5[r17]
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r3, r2)
            r14 = 1
            java.lang.Object[] r15 = new java.lang.Object[r14]
            r5 = r5[r17]
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r15[r17] = r5
            java.lang.String r5 = "%d"
            java.lang.String r5 = java.lang.String.format(r5, r15)
            java.lang.String r2 = r2.replace(r5, r4)
            r1.setText(r2)
            goto L_0x0561
        L_0x054f:
            r21 = r1
            r22 = r2
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r0.onlineTextView
            r1 = r1[r7]
            if (r7 != 0) goto L_0x055c
            r2 = r22
            goto L_0x055e
        L_0x055c:
            r2 = r20
        L_0x055e:
            r1.setText(r2)
        L_0x0561:
            int r7 = r7 + 1
            r5 = r20
            r1 = r21
            r2 = r22
            r4 = 1
            goto L_0x03d1
        L_0x056c:
            if (r12 == 0) goto L_0x0571
            r24.needLayout()
        L_0x0571:
            org.telegram.tgnet.TLRPC$ChatPhoto r1 = r10.photo
            if (r1 == 0) goto L_0x057a
            org.telegram.tgnet.TLRPC$FileLocation r3 = r1.photo_big
            r16 = r3
            goto L_0x057c
        L_0x057a:
            r16 = 0
        L_0x057c:
            org.telegram.ui.Components.AvatarDrawable r1 = r0.avatarDrawable
            r1.setInfo((org.telegram.tgnet.TLRPC.Chat) r10)
            r1 = 1
            org.telegram.messenger.ImageLocation r9 = org.telegram.messenger.ImageLocation.getForChat(r10, r1)
            r1 = 0
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForChat(r10, r1)
            org.telegram.ui.Components.ProfileGalleryView r1 = r0.avatarsViewPager
            r1.initIfEmpty(r9, r2)
            org.telegram.ui.ProfileActivity$AvatarImageView r1 = r0.avatarImage
            org.telegram.ui.Components.AvatarDrawable r3 = r0.avatarDrawable
            r1.setImage((org.telegram.messenger.ImageLocation) r2, (java.lang.String) r6, (android.graphics.drawable.Drawable) r3, (java.lang.Object) r10)
            int r1 = r0.currentAccount
            org.telegram.messenger.FileLoader r8 = org.telegram.messenger.FileLoader.getInstance(r1)
            r11 = 0
            r12 = 0
            r13 = 1
            r8.loadFile(r9, r10, r11, r12, r13)
            org.telegram.ui.ProfileActivity$AvatarImageView r1 = r0.avatarImage
            org.telegram.messenger.ImageReceiver r1 = r1.getImageReceiver()
            boolean r2 = org.telegram.ui.PhotoViewer.isShowingImage((org.telegram.tgnet.TLRPC.FileLocation) r16)
            r3 = 1
            r2 = r2 ^ r3
            r3 = 0
            r1.setVisible(r2, r3)
        L_0x05b3:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.updateProfileData():void");
    }

    private void createActionBarMenu() {
        TLRPC.ChatFull chatFull;
        ActionBarMenuItem actionBarMenuItem;
        String str;
        int i;
        ActionBarMenu createMenu = this.actionBar.createMenu();
        createMenu.clearItems();
        ActionBarMenuItem actionBarMenuItem2 = null;
        this.animatingItem = null;
        if (this.user_id != 0) {
            if (UserConfig.getInstance(this.currentAccount).getClientUserId() != this.user_id) {
                TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
                if (user != null) {
                    TLRPC.UserFull userFull = this.userInfo;
                    if (userFull != null && userFull.phone_calls_available) {
                        this.callItem = createMenu.addItem(15, NUM);
                    }
                    if (this.isBot || ContactsController.getInstance(this.currentAccount).contactsDict.get(Integer.valueOf(this.user_id)) == null) {
                        actionBarMenuItem = createMenu.addItem(10, NUM);
                        if (!MessagesController.isSupportUser(user)) {
                            if (this.isBot) {
                                if (!user.bot_nochats) {
                                    actionBarMenuItem.addSubItem(9, NUM, (CharSequence) LocaleController.getString("BotInvite", NUM));
                                }
                                actionBarMenuItem.addSubItem(10, NUM, (CharSequence) LocaleController.getString("BotShare", NUM));
                            } else {
                                actionBarMenuItem.addSubItem(1, NUM, (CharSequence) LocaleController.getString("AddContact", NUM));
                            }
                            if (!TextUtils.isEmpty(user.phone)) {
                                actionBarMenuItem.addSubItem(3, NUM, (CharSequence) LocaleController.getString("ShareContact", NUM));
                            }
                            if (this.isBot) {
                                int i2 = !this.userBlocked ? NUM : NUM;
                                if (!this.userBlocked) {
                                    i = NUM;
                                    str = "BotStop";
                                } else {
                                    i = NUM;
                                    str = "BotRestart";
                                }
                                actionBarMenuItem.addSubItem(2, i2, (CharSequence) LocaleController.getString(str, i));
                            } else {
                                boolean z = this.userBlocked;
                                actionBarMenuItem.addSubItem(2, NUM, (CharSequence) !this.userBlocked ? LocaleController.getString("BlockContact", NUM) : LocaleController.getString("Unblock", NUM));
                            }
                        } else if (this.userBlocked) {
                            actionBarMenuItem.addSubItem(2, NUM, (CharSequence) LocaleController.getString("Unblock", NUM));
                        }
                    } else {
                        actionBarMenuItem = createMenu.addItem(10, NUM);
                        if (!TextUtils.isEmpty(user.phone)) {
                            actionBarMenuItem.addSubItem(3, NUM, (CharSequence) LocaleController.getString("ShareContact", NUM));
                        }
                        boolean z2 = this.userBlocked;
                        actionBarMenuItem.addSubItem(2, NUM, (CharSequence) !this.userBlocked ? LocaleController.getString("BlockContact", NUM) : LocaleController.getString("Unblock", NUM));
                        actionBarMenuItem.addSubItem(4, NUM, (CharSequence) LocaleController.getString("EditContact", NUM));
                        actionBarMenuItem.addSubItem(5, NUM, (CharSequence) LocaleController.getString("DeleteContact", NUM));
                    }
                } else {
                    return;
                }
            } else {
                actionBarMenuItem = createMenu.addItem(10, NUM);
                actionBarMenuItem.addSubItem(3, NUM, (CharSequence) LocaleController.getString("ShareContact", NUM));
            }
            actionBarMenuItem2 = actionBarMenuItem;
        } else {
            int i3 = this.chat_id;
            if (i3 != 0 && i3 > 0) {
                TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id));
                if (ChatObject.isChannel(chat)) {
                    if (ChatObject.hasAdminRights(chat) || (chat.megagroup && ChatObject.canChangeChatInfo(chat))) {
                        this.editItem = createMenu.addItem(12, NUM);
                    }
                    if (!chat.megagroup && (chatFull = this.chatInfo) != null && chatFull.can_view_stats) {
                        ActionBarMenuItem addItem = createMenu.addItem(10, NUM);
                        addItem.addSubItem(19, NUM, (CharSequence) LocaleController.getString("Statistics", NUM));
                        actionBarMenuItem2 = addItem;
                    }
                    if (chat.megagroup) {
                        if (actionBarMenuItem2 == null) {
                            actionBarMenuItem2 = createMenu.addItem(10, NUM);
                        }
                        actionBarMenuItem2.addSubItem(17, NUM, (CharSequence) LocaleController.getString("SearchMembers", NUM));
                        if (!chat.creator && !chat.left && !chat.kicked) {
                            actionBarMenuItem2.addSubItem(7, NUM, (CharSequence) LocaleController.getString("LeaveMegaMenu", NUM));
                        }
                    } else if (!TextUtils.isEmpty(chat.username)) {
                        if (actionBarMenuItem2 == null) {
                            actionBarMenuItem2 = createMenu.addItem(10, NUM);
                        }
                        actionBarMenuItem2.addSubItem(10, NUM, (CharSequence) LocaleController.getString("BotShare", NUM));
                    }
                } else {
                    if (ChatObject.canChangeChatInfo(chat)) {
                        this.editItem = createMenu.addItem(12, NUM);
                    }
                    ActionBarMenuItem addItem2 = createMenu.addItem(10, NUM);
                    if (!ChatObject.isKickedFromChat(chat) && !ChatObject.isLeftFromChat(chat)) {
                        addItem2.addSubItem(17, NUM, (CharSequence) LocaleController.getString("SearchMembers", NUM));
                    }
                    addItem2.addSubItem(7, NUM, (CharSequence) LocaleController.getString("DeleteAndExit", NUM));
                    actionBarMenuItem2 = addItem2;
                }
            }
        }
        if (actionBarMenuItem2 == null) {
            actionBarMenuItem2 = createMenu.addItem(10, NUM);
        }
        actionBarMenuItem2.addSubItem(14, NUM, (CharSequence) LocaleController.getString("AddShortcut", NUM));
        actionBarMenuItem2.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
        ActionBarMenuItem actionBarMenuItem3 = this.editItem;
        if (actionBarMenuItem3 != null) {
            actionBarMenuItem3.setContentDescription(LocaleController.getString("Edit", NUM));
        }
        ActionBarMenuItem actionBarMenuItem4 = this.callItem;
        if (actionBarMenuItem4 != null) {
            actionBarMenuItem4.setContentDescription(LocaleController.getString("Call", NUM));
        }
        PagerIndicatorView pagerIndicatorView = this.avatarsViewPagerIndicatorView;
        if (pagerIndicatorView != null && pagerIndicatorView.isIndicatorFullyVisible()) {
            ActionBarMenuItem actionBarMenuItem5 = this.editItem;
            if (actionBarMenuItem5 != null) {
                actionBarMenuItem5.setVisibility(8);
            }
            ActionBarMenuItem actionBarMenuItem6 = this.callItem;
            if (actionBarMenuItem6 != null) {
                actionBarMenuItem6.setVisibility(8);
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

    public void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList<Long> arrayList, CharSequence charSequence, boolean z) {
        long longValue = arrayList.get(0).longValue();
        Bundle bundle = new Bundle();
        bundle.putBoolean("scrollToTopOnResume", true);
        int i = (int) longValue;
        if (i == 0) {
            bundle.putInt("enc_id", (int) (longValue >> 32));
        } else if (i > 0) {
            bundle.putInt("user_id", i);
        } else if (i < 0) {
            bundle.putInt("chat_id", -i);
        }
        if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(bundle, dialogsActivity)) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            presentFragment(new ChatActivity(bundle), true);
            removeSelfFromStack();
            SendMessagesHelper.getInstance(this.currentAccount).sendMessage(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id)), longValue, (MessageObject) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
        }
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        TLRPC.User user;
        if (i == 101 && (user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id))) != null) {
            if (iArr.length <= 0 || iArr[0] != 0) {
                VoIPHelper.permissionDenied(getParentActivity(), (Runnable) null);
            } else {
                VoIPHelper.startCall(user, getParentActivity(), this.userInfo);
            }
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            View view2;
            switch (i) {
                case 1:
                    view2 = new HeaderCell(this.mContext, 23);
                    break;
                case 2:
                    view = new TextDetailCell(this.mContext);
                    break;
                case 3:
                    view = new AboutLinkCell(this.mContext) {
                        /* access modifiers changed from: protected */
                        public void didPressUrl(String str) {
                            if (str.startsWith("@")) {
                                MessagesController.getInstance(ProfileActivity.this.currentAccount).openByUserName(str.substring(1), ProfileActivity.this, 0);
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
                    };
                    break;
                case 4:
                    view = new TextCell(this.mContext);
                    break;
                case 5:
                    view = new DividerCell(this.mContext);
                    view.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(4.0f), 0, 0);
                    break;
                case 6:
                    view2 = new NotificationsCheckCell(this.mContext, 23, 70);
                    break;
                case 7:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 8:
                    view = new UserCell(this.mContext, ProfileActivity.this.addMemberRow == -1 ? 9 : 6, 0, true);
                    break;
                case 11:
                    view = new EmptyCell(this.mContext, 36);
                    break;
                case 12:
                    view = new View(this.mContext) {
                        private int lastListViewHeight = 0;
                        private int lastPaddingHeight = 0;

                        /* access modifiers changed from: protected */
                        public void onMeasure(int i, int i2) {
                            if (this.lastListViewHeight != ProfileActivity.this.listView.getMeasuredHeight()) {
                                this.lastPaddingHeight = 0;
                            }
                            this.lastListViewHeight = ProfileActivity.this.listView.getMeasuredHeight();
                            int childCount = ProfileActivity.this.listView.getChildCount();
                            if (childCount == ProfileActivity.this.listAdapter.getItemCount()) {
                                int i3 = 0;
                                for (int i4 = 0; i4 < childCount; i4++) {
                                    if (ProfileActivity.this.listView.getChildAdapterPosition(ProfileActivity.this.listView.getChildAt(i4)) != ProfileActivity.this.bottomPaddingRow) {
                                        i3 += ProfileActivity.this.listView.getChildAt(i4).getMeasuredHeight();
                                    }
                                }
                                int measuredHeight = ((ProfileActivity.this.fragmentView.getMeasuredHeight() - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.statusBarHeight) - i3;
                                if (measuredHeight > AndroidUtilities.dp(88.0f)) {
                                    measuredHeight = 0;
                                }
                                if (measuredHeight <= 0) {
                                    measuredHeight = 0;
                                }
                                int measuredWidth = ProfileActivity.this.listView.getMeasuredWidth();
                                this.lastPaddingHeight = measuredHeight;
                                setMeasuredDimension(measuredWidth, measuredHeight);
                                return;
                            }
                            setMeasuredDimension(ProfileActivity.this.listView.getMeasuredWidth(), this.lastPaddingHeight);
                        }
                    };
                    break;
                default:
                    view = null;
                    break;
            }
            view = view2;
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        /* JADX WARNING: Removed duplicated region for block: B:102:0x02ea  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r18, int r19) {
            /*
                r17 = this;
                r0 = r17
                r1 = r18
                r2 = r19
                int r3 = r18.getItemViewType()
                r4 = 3
                r5 = 2131624542(0x7f0e025e, float:1.8876267E38)
                java.lang.String r6 = "ChannelMembers"
                r7 = 0
                r8 = -1
                r9 = 0
                r10 = 1
                switch(r3) {
                    case 1: goto L_0x08aa;
                    case 2: goto L_0x0770;
                    case 3: goto L_0x0724;
                    case 4: goto L_0x0301;
                    case 5: goto L_0x0017;
                    case 6: goto L_0x0190;
                    case 7: goto L_0x00e6;
                    case 8: goto L_0x0019;
                    default: goto L_0x0017;
                }
            L_0x0017:
                goto L_0x0912
            L_0x0019:
                android.view.View r1 = r1.itemView
                r11 = r1
                org.telegram.ui.Cells.UserCell r11 = (org.telegram.ui.Cells.UserCell) r11
                org.telegram.ui.ProfileActivity r1 = org.telegram.ui.ProfileActivity.this
                java.util.ArrayList r1 = r1.sortedUsers
                boolean r1 = r1.isEmpty()
                if (r1 != 0) goto L_0x0053
                org.telegram.ui.ProfileActivity r1 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r1 = r1.chatInfo
                org.telegram.tgnet.TLRPC$ChatParticipants r1 = r1.participants
                java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r1 = r1.participants
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                java.util.ArrayList r3 = r3.sortedUsers
                org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                int r4 = r4.membersStartRow
                int r4 = r2 - r4
                java.lang.Object r3 = r3.get(r4)
                java.lang.Integer r3 = (java.lang.Integer) r3
                int r3 = r3.intValue()
                java.lang.Object r1 = r1.get(r3)
                org.telegram.tgnet.TLRPC$ChatParticipant r1 = (org.telegram.tgnet.TLRPC.ChatParticipant) r1
                goto L_0x006b
            L_0x0053:
                org.telegram.ui.ProfileActivity r1 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r1 = r1.chatInfo
                org.telegram.tgnet.TLRPC$ChatParticipants r1 = r1.participants
                java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r1 = r1.participants
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.membersStartRow
                int r3 = r2 - r3
                java.lang.Object r1 = r1.get(r3)
                org.telegram.tgnet.TLRPC$ChatParticipant r1 = (org.telegram.tgnet.TLRPC.ChatParticipant) r1
            L_0x006b:
                if (r1 == 0) goto L_0x0912
                boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC.TL_chatChannelParticipant
                if (r3 == 0) goto L_0x009e
                r3 = r1
                org.telegram.tgnet.TLRPC$TL_chatChannelParticipant r3 = (org.telegram.tgnet.TLRPC.TL_chatChannelParticipant) r3
                org.telegram.tgnet.TLRPC$ChannelParticipant r3 = r3.channelParticipant
                java.lang.String r4 = r3.rank
                boolean r4 = android.text.TextUtils.isEmpty(r4)
                if (r4 != 0) goto L_0x0082
                java.lang.String r3 = r3.rank
            L_0x0080:
                r7 = r3
                goto L_0x00b9
            L_0x0082:
                boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_channelParticipantCreator
                if (r4 == 0) goto L_0x0090
                r3 = 2131624522(0x7f0e024a, float:1.8876226E38)
                java.lang.String r4 = "ChannelCreator"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                goto L_0x0080
            L_0x0090:
                boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_channelParticipantAdmin
                if (r3 == 0) goto L_0x00b9
                r3 = 2131624505(0x7f0e0239, float:1.8876192E38)
                java.lang.String r4 = "ChannelAdmin"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                goto L_0x0080
            L_0x009e:
                boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC.TL_chatParticipantCreator
                if (r3 == 0) goto L_0x00ac
                r3 = 2131624522(0x7f0e024a, float:1.8876226E38)
                java.lang.String r4 = "ChannelCreator"
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r4, r3)
                goto L_0x00b9
            L_0x00ac:
                boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC.TL_chatParticipantAdmin
                if (r3 == 0) goto L_0x00b9
                r3 = 2131624505(0x7f0e0239, float:1.8876192E38)
                java.lang.String r4 = "ChannelAdmin"
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r4, r3)
            L_0x00b9:
                r11.setAdminRole(r7)
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.currentAccount
                org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
                int r1 = r1.user_id
                java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
                org.telegram.tgnet.TLRPC$User r12 = r3.getUser(r1)
                r13 = 0
                r14 = 0
                r15 = 0
                org.telegram.ui.ProfileActivity r1 = org.telegram.ui.ProfileActivity.this
                int r1 = r1.membersEndRow
                int r1 = r1 - r10
                if (r2 == r1) goto L_0x00df
                r16 = 1
                goto L_0x00e1
            L_0x00df:
                r16 = 0
            L_0x00e1:
                r11.setData(r12, r13, r14, r15, r16)
                goto L_0x0912
            L_0x00e6:
                android.view.View r1 = r1.itemView
                java.lang.Integer r3 = java.lang.Integer.valueOf(r19)
                r1.setTag(r3)
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.infoSectionRow
                if (r2 != r3) goto L_0x010f
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.sharedSectionRow
                if (r3 != r8) goto L_0x010f
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.lastSectionRow
                if (r3 != r8) goto L_0x010f
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.settingsSectionRow
                if (r3 == r8) goto L_0x016b
            L_0x010f:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.settingsSectionRow
                if (r2 != r3) goto L_0x011f
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.sharedSectionRow
                if (r3 == r8) goto L_0x016b
            L_0x011f:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.sharedSectionRow
                if (r2 != r3) goto L_0x012f
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.lastSectionRow
                if (r3 == r8) goto L_0x016b
            L_0x012f:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.lastSectionRow
                if (r2 == r3) goto L_0x016b
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.membersSectionRow
                if (r2 != r3) goto L_0x015e
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                int r2 = r2.lastSectionRow
                if (r2 != r8) goto L_0x015e
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                int r2 = r2.sharedSectionRow
                if (r2 == r8) goto L_0x016b
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                int r2 = r2.membersSectionRow
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.sharedSectionRow
                if (r2 <= r3) goto L_0x015e
                goto L_0x016b
            L_0x015e:
                android.content.Context r2 = r0.mContext
                r3 = 2131165406(0x7var_de, float:1.7945028E38)
                java.lang.String r4 = "windowBackgroundGrayShadow"
                android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r3, (java.lang.String) r4)
                goto L_0x0177
            L_0x016b:
                android.content.Context r2 = r0.mContext
                r3 = 2131165407(0x7var_df, float:1.794503E38)
                java.lang.String r4 = "windowBackgroundGrayShadow"
                android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r3, (java.lang.String) r4)
            L_0x0177:
                org.telegram.ui.Components.CombinedDrawable r3 = new org.telegram.ui.Components.CombinedDrawable
                android.graphics.drawable.ColorDrawable r4 = new android.graphics.drawable.ColorDrawable
                java.lang.String r5 = "windowBackgroundGray"
                int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
                r4.<init>(r5)
                r3.<init>(r4, r2)
                r3.setFullsize(r10)
                r1.setBackgroundDrawable(r3)
                goto L_0x0912
            L_0x0190:
                android.view.View r1 = r1.itemView
                org.telegram.ui.Cells.NotificationsCheckCell r1 = (org.telegram.ui.Cells.NotificationsCheckCell) r1
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.notificationsRow
                if (r2 != r3) goto L_0x0912
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                int r2 = r2.currentAccount
                android.content.SharedPreferences r2 = org.telegram.messenger.MessagesController.getNotificationsSettings(r2)
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                long r5 = r3.dialog_id
                r11 = 0
                int r3 = (r5 > r11 ? 1 : (r5 == r11 ? 0 : -1))
                if (r3 == 0) goto L_0x01b9
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                long r5 = r3.dialog_id
                goto L_0x01d0
            L_0x01b9:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.user_id
                if (r3 == 0) goto L_0x01c8
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.user_id
                goto L_0x01cf
            L_0x01c8:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.chat_id
                int r3 = -r3
            L_0x01cf:
                long r5 = (long) r3
            L_0x01d0:
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r8 = "custom_"
                r3.append(r8)
                r3.append(r5)
                java.lang.String r3 = r3.toString()
                boolean r3 = r2.getBoolean(r3, r9)
                java.lang.StringBuilder r8 = new java.lang.StringBuilder
                r8.<init>()
                java.lang.String r11 = "notify2_"
                r8.append(r11)
                r8.append(r5)
                java.lang.String r8 = r8.toString()
                boolean r8 = r2.contains(r8)
                java.lang.StringBuilder r12 = new java.lang.StringBuilder
                r12.<init>()
                r12.append(r11)
                r12.append(r5)
                java.lang.String r11 = r12.toString()
                int r11 = r2.getInt(r11, r9)
                java.lang.StringBuilder r12 = new java.lang.StringBuilder
                r12.<init>()
                java.lang.String r13 = "notifyuntil_"
                r12.append(r13)
                r12.append(r5)
                java.lang.String r12 = r12.toString()
                int r2 = r2.getInt(r12, r9)
                if (r11 != r4) goto L_0x02ad
                r4 = 2147483647(0x7fffffff, float:NaN)
                if (r2 == r4) goto L_0x02ad
                org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                int r4 = r4.currentAccount
                org.telegram.tgnet.ConnectionsManager r4 = org.telegram.tgnet.ConnectionsManager.getInstance(r4)
                int r4 = r4.getCurrentTime()
                int r2 = r2 - r4
                if (r2 > 0) goto L_0x0252
                if (r3 == 0) goto L_0x0246
                r2 = 2131625786(0x7f0e073a, float:1.887879E38)
                java.lang.String r3 = "NotificationsCustom"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                goto L_0x024f
            L_0x0246:
                r2 = 2131625810(0x7f0e0752, float:1.8878838E38)
                java.lang.String r3 = "NotificationsOn"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            L_0x024f:
                r7 = r2
                goto L_0x02e8
            L_0x0252:
                r3 = 3600(0xe10, float:5.045E-42)
                r4 = 2131627163(0x7f0e0c9b, float:1.8881583E38)
                java.lang.String r5 = "WillUnmuteIn"
                if (r2 >= r3) goto L_0x026e
                java.lang.Object[] r3 = new java.lang.Object[r10]
                int r2 = r2 / 60
                java.lang.String r6 = "Minutes"
                java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r6, r2)
                r3[r9] = r2
                java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r5, r4, r3)
            L_0x026b:
                r10 = 0
                goto L_0x02e8
            L_0x026e:
                r3 = 86400(0x15180, float:1.21072E-40)
                r6 = 1114636288(0x42700000, float:60.0)
                if (r2 >= r3) goto L_0x028d
                java.lang.Object[] r3 = new java.lang.Object[r10]
                float r2 = (float) r2
                float r2 = r2 / r6
                float r2 = r2 / r6
                double r6 = (double) r2
                double r6 = java.lang.Math.ceil(r6)
                int r2 = (int) r6
                java.lang.String r6 = "Hours"
                java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r6, r2)
                r3[r9] = r2
                java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r5, r4, r3)
                goto L_0x026b
            L_0x028d:
                r3 = 31536000(0x1e13380, float:8.2725845E-38)
                if (r2 >= r3) goto L_0x026b
                java.lang.Object[] r3 = new java.lang.Object[r10]
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
                r3[r9] = r2
                java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r5, r4, r3)
                goto L_0x026b
            L_0x02ad:
                if (r11 != 0) goto L_0x02c2
                if (r8 == 0) goto L_0x02b2
                goto L_0x02c7
            L_0x02b2:
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                int r2 = r2.currentAccount
                org.telegram.messenger.NotificationsController r2 = org.telegram.messenger.NotificationsController.getInstance(r2)
                boolean r2 = r2.isGlobalNotificationsEnabled((long) r5)
                r10 = r2
                goto L_0x02c7
            L_0x02c2:
                if (r11 != r10) goto L_0x02c5
                goto L_0x02c7
            L_0x02c5:
                r2 = 2
                r10 = 0
            L_0x02c7:
                if (r10 == 0) goto L_0x02d5
                if (r3 == 0) goto L_0x02d5
                r2 = 2131625786(0x7f0e073a, float:1.887879E38)
                java.lang.String r3 = "NotificationsCustom"
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r3, r2)
                goto L_0x02e8
            L_0x02d5:
                if (r10 == 0) goto L_0x02dd
                r2 = 2131625810(0x7f0e0752, float:1.8878838E38)
                java.lang.String r3 = "NotificationsOn"
                goto L_0x02e2
            L_0x02dd:
                r2 = 2131625808(0x7f0e0750, float:1.8878834E38)
                java.lang.String r3 = "NotificationsOff"
            L_0x02e2:
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                goto L_0x024f
            L_0x02e8:
                if (r7 != 0) goto L_0x02f3
                r2 = 2131625808(0x7f0e0750, float:1.8878834E38)
                java.lang.String r3 = "NotificationsOff"
                java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r3, r2)
            L_0x02f3:
                r2 = 2131625782(0x7f0e0736, float:1.8878782E38)
                java.lang.String r3 = "Notifications"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setTextAndValueAndCheck(r2, r7, r10, r9)
                goto L_0x0912
            L_0x0301:
                android.view.View r1 = r1.itemView
                org.telegram.ui.Cells.TextCell r1 = (org.telegram.ui.Cells.TextCell) r1
                java.lang.String r3 = "windowBackgroundWhiteBlackText"
                java.lang.String r8 = "windowBackgroundWhiteGrayIcon"
                r1.setColors(r8, r3)
                r1.setTag(r3)
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.photosRow
                java.lang.String r8 = "%d"
                if (r2 != r3) goto L_0x034a
                r3 = 2131626569(0x7f0e0a49, float:1.8880378E38)
                java.lang.String r4 = "SharedPhotosAndVideos"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                java.lang.Object[] r4 = new java.lang.Object[r10]
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                int[] r5 = r5.lastMediaCount
                r5 = r5[r9]
                java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
                r4[r9] = r5
                java.lang.String r4 = java.lang.String.format(r8, r4)
                r5 = 2131165820(0x7var_c, float:1.7945868E38)
                org.telegram.ui.ProfileActivity r6 = org.telegram.ui.ProfileActivity.this
                int r6 = r6.sharedSectionRow
                int r6 = r6 - r10
                if (r2 == r6) goto L_0x0345
                r9 = 1
            L_0x0345:
                r1.setTextAndValueAndIcon(r3, r4, r5, r9)
                goto L_0x0912
            L_0x034a:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.filesRow
                if (r2 != r3) goto L_0x0381
                r3 = 2131625139(0x7f0e04b3, float:1.8877478E38)
                java.lang.String r4 = "FilesDataUsage"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                java.lang.Object[] r4 = new java.lang.Object[r10]
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                int[] r5 = r5.lastMediaCount
                r5 = r5[r10]
                java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
                r4[r9] = r5
                java.lang.String r4 = java.lang.String.format(r8, r4)
                r5 = 2131165815(0x7var_, float:1.7945858E38)
                org.telegram.ui.ProfileActivity r6 = org.telegram.ui.ProfileActivity.this
                int r6 = r6.sharedSectionRow
                int r6 = r6 - r10
                if (r2 == r6) goto L_0x037c
                r9 = 1
            L_0x037c:
                r1.setTextAndValueAndIcon(r3, r4, r5, r9)
                goto L_0x0912
            L_0x0381:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.linksRow
                if (r2 != r3) goto L_0x03b8
                r3 = 2131626564(0x7f0e0a44, float:1.8880368E38)
                java.lang.String r5 = "SharedLinks"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
                java.lang.Object[] r5 = new java.lang.Object[r10]
                org.telegram.ui.ProfileActivity r6 = org.telegram.ui.ProfileActivity.this
                int[] r6 = r6.lastMediaCount
                r4 = r6[r4]
                java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
                r5[r9] = r4
                java.lang.String r4 = java.lang.String.format(r8, r5)
                r5 = 2131165817(0x7var_, float:1.7945862E38)
                org.telegram.ui.ProfileActivity r6 = org.telegram.ui.ProfileActivity.this
                int r6 = r6.sharedSectionRow
                int r6 = r6 - r10
                if (r2 == r6) goto L_0x03b3
                r9 = 1
            L_0x03b3:
                r1.setTextAndValueAndIcon(r3, r4, r5, r9)
                goto L_0x0912
            L_0x03b8:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.audioRow
                if (r2 != r3) goto L_0x03f0
                r3 = 2131626560(0x7f0e0a40, float:1.888036E38)
                java.lang.String r4 = "SharedAudioFiles"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                java.lang.Object[] r4 = new java.lang.Object[r10]
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                int[] r5 = r5.lastMediaCount
                r6 = 4
                r5 = r5[r6]
                java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
                r4[r9] = r5
                java.lang.String r4 = java.lang.String.format(r8, r4)
                r5 = 2131165813(0x7var_, float:1.7945854E38)
                org.telegram.ui.ProfileActivity r6 = org.telegram.ui.ProfileActivity.this
                int r6 = r6.sharedSectionRow
                int r6 = r6 - r10
                if (r2 == r6) goto L_0x03eb
                r9 = 1
            L_0x03eb:
                r1.setTextAndValueAndIcon(r3, r4, r5, r9)
                goto L_0x0912
            L_0x03f0:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.voiceRow
                if (r2 != r3) goto L_0x0428
                r3 = 2131624290(0x7f0e0162, float:1.8875756E38)
                java.lang.String r4 = "AudioAutodownload"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                java.lang.Object[] r4 = new java.lang.Object[r10]
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                int[] r5 = r5.lastMediaCount
                r6 = 2
                r5 = r5[r6]
                java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
                r4[r9] = r5
                java.lang.String r4 = java.lang.String.format(r8, r4)
                r5 = 2131165821(0x7var_d, float:1.794587E38)
                org.telegram.ui.ProfileActivity r6 = org.telegram.ui.ProfileActivity.this
                int r6 = r6.sharedSectionRow
                int r6 = r6 - r10
                if (r2 == r6) goto L_0x0423
                r9 = 1
            L_0x0423:
                r1.setTextAndValueAndIcon(r3, r4, r5, r9)
                goto L_0x0912
            L_0x0428:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.groupsInCommonRow
                r4 = 2131165253(0x7var_, float:1.7944718E38)
                if (r2 != r3) goto L_0x045f
                r3 = 2131625291(0x7f0e054b, float:1.8877786E38)
                java.lang.String r5 = "GroupsInCommonTitle"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
                java.lang.Object[] r5 = new java.lang.Object[r10]
                org.telegram.ui.ProfileActivity r6 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$UserFull r6 = r6.userInfo
                int r6 = r6.common_chats_count
                java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
                r5[r9] = r6
                java.lang.String r5 = java.lang.String.format(r8, r5)
                org.telegram.ui.ProfileActivity r6 = org.telegram.ui.ProfileActivity.this
                int r6 = r6.sharedSectionRow
                int r6 = r6 - r10
                if (r2 == r6) goto L_0x045a
                r9 = 1
            L_0x045a:
                r1.setTextAndValueAndIcon(r3, r5, r4, r9)
                goto L_0x0912
            L_0x045f:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.settingsTimerRow
                if (r2 != r3) goto L_0x04a3
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                int r2 = r2.currentAccount
                org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                long r3 = r3.dialog_id
                r5 = 32
                long r3 = r3 >> r5
                int r4 = (int) r3
                java.lang.Integer r3 = java.lang.Integer.valueOf(r4)
                org.telegram.tgnet.TLRPC$EncryptedChat r2 = r2.getEncryptedChat(r3)
                int r2 = r2.ttl
                if (r2 != 0) goto L_0x0491
                r2 = 2131626576(0x7f0e0a50, float:1.8880392E38)
                java.lang.String r3 = "ShortMessageLifetimeForever"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                goto L_0x0495
            L_0x0491:
                java.lang.String r2 = org.telegram.messenger.LocaleController.formatTTLString(r2)
            L_0x0495:
                r3 = 2131625533(0x7f0e063d, float:1.8878277E38)
                java.lang.String r4 = "MessageLifetime"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                r1.setTextAndValue(r3, r2, r9)
                goto L_0x0912
            L_0x04a3:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.unblockRow
                if (r2 != r3) goto L_0x04bf
                r2 = 2131626809(0x7f0e0b39, float:1.8880865E38)
                java.lang.String r3 = "Unblock"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2, r9)
                java.lang.String r2 = "windowBackgroundWhiteRedText5"
                r1.setColors(r7, r2)
                goto L_0x0912
            L_0x04bf:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.startSecretChatRow
                if (r2 != r3) goto L_0x04db
                r2 = 2131626615(0x7f0e0a77, float:1.8880471E38)
                java.lang.String r3 = "StartEncryptedChat"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2, r9)
                java.lang.String r2 = "windowBackgroundWhiteGreenText2"
                r1.setColors(r7, r2)
                goto L_0x0912
            L_0x04db:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.settingsKeyRow
                if (r2 != r3) goto L_0x0515
                org.telegram.ui.Components.IdenticonDrawable r2 = new org.telegram.ui.Components.IdenticonDrawable
                r2.<init>()
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.currentAccount
                org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
                org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                long r4 = r4.dialog_id
                r6 = 32
                long r4 = r4 >> r6
                int r5 = (int) r4
                java.lang.Integer r4 = java.lang.Integer.valueOf(r5)
                org.telegram.tgnet.TLRPC$EncryptedChat r3 = r3.getEncryptedChat(r4)
                r2.setEncryptedChat(r3)
                r3 = 2131625006(0x7f0e042e, float:1.8877208E38)
                java.lang.String r4 = "EncryptionKey"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                r1.setTextAndValueDrawable(r3, r2, r9)
                goto L_0x0912
            L_0x0515:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.leaveChannelRow
                if (r2 != r3) goto L_0x0531
                java.lang.String r2 = "windowBackgroundWhiteRedText5"
                r1.setColors(r7, r2)
                r2 = 2131625416(0x7f0e05c8, float:1.887804E38)
                java.lang.String r3 = "LeaveChannel"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2, r9)
                goto L_0x0912
            L_0x0531:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.joinRow
                if (r2 != r3) goto L_0x056f
                java.lang.String r2 = "windowBackgroundWhiteBlueText2"
                r1.setColors(r7, r2)
                java.lang.String r2 = "windowBackgroundWhiteBlueText2"
                int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                r1.setTextColor(r2)
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r2 = r2.currentChat
                boolean r2 = r2.megagroup
                if (r2 == 0) goto L_0x0561
                r2 = 2131626271(0x7f0e091f, float:1.8879773E38)
                java.lang.String r3 = "ProfileJoinGroup"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2, r9)
                goto L_0x0912
            L_0x0561:
                r2 = 2131626270(0x7f0e091e, float:1.8879771E38)
                java.lang.String r3 = "ProfileJoinChannel"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2, r9)
                goto L_0x0912
            L_0x056f:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.subscribersRow
                if (r2 != r3) goto L_0x0629
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r3 = r3.chatInfo
                if (r3 == 0) goto L_0x05e8
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r3 = r3.currentChat
                boolean r3 = org.telegram.messenger.ChatObject.isChannel(r3)
                if (r3 == 0) goto L_0x05c1
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r3 = r3.currentChat
                boolean r3 = r3.megagroup
                if (r3 != 0) goto L_0x05c1
                r3 = 2131624585(0x7f0e0289, float:1.8876354E38)
                java.lang.String r5 = "ChannelSubscribers"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
                java.lang.Object[] r5 = new java.lang.Object[r10]
                org.telegram.ui.ProfileActivity r6 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r6 = r6.chatInfo
                int r6 = r6.participants_count
                java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
                r5[r9] = r6
                java.lang.String r5 = java.lang.String.format(r8, r5)
                org.telegram.ui.ProfileActivity r6 = org.telegram.ui.ProfileActivity.this
                int r6 = r6.membersSectionRow
                int r6 = r6 - r10
                if (r2 == r6) goto L_0x05bc
                r9 = 1
            L_0x05bc:
                r1.setTextAndValueAndIcon(r3, r5, r4, r9)
                goto L_0x0912
            L_0x05c1:
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r5)
                java.lang.Object[] r5 = new java.lang.Object[r10]
                org.telegram.ui.ProfileActivity r6 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r6 = r6.chatInfo
                int r6 = r6.participants_count
                java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
                r5[r9] = r6
                java.lang.String r5 = java.lang.String.format(r8, r5)
                org.telegram.ui.ProfileActivity r6 = org.telegram.ui.ProfileActivity.this
                int r6 = r6.membersSectionRow
                int r6 = r6 - r10
                if (r2 == r6) goto L_0x05e3
                r9 = 1
            L_0x05e3:
                r1.setTextAndValueAndIcon(r3, r5, r4, r9)
                goto L_0x0912
            L_0x05e8:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r3 = r3.currentChat
                boolean r3 = org.telegram.messenger.ChatObject.isChannel(r3)
                if (r3 == 0) goto L_0x0616
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r3 = r3.currentChat
                boolean r3 = r3.megagroup
                if (r3 != 0) goto L_0x0616
                r3 = 2131624585(0x7f0e0289, float:1.8876354E38)
                java.lang.String r5 = "ChannelSubscribers"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                int r5 = r5.membersSectionRow
                int r5 = r5 - r10
                if (r2 == r5) goto L_0x0611
                r9 = 1
            L_0x0611:
                r1.setTextAndIcon(r3, r4, r9)
                goto L_0x0912
            L_0x0616:
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r5)
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                int r5 = r5.membersSectionRow
                int r5 = r5 - r10
                if (r2 == r5) goto L_0x0624
                r9 = 1
            L_0x0624:
                r1.setTextAndIcon(r3, r4, r9)
                goto L_0x0912
            L_0x0629:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.administratorsRow
                if (r2 != r3) goto L_0x0683
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r3 = r3.chatInfo
                if (r3 == 0) goto L_0x0668
                r3 = 2131624507(0x7f0e023b, float:1.8876196E38)
                java.lang.String r4 = "ChannelAdministrators"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                java.lang.Object[] r4 = new java.lang.Object[r10]
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r5 = r5.chatInfo
                int r5 = r5.admins_count
                java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
                r4[r9] = r5
                java.lang.String r4 = java.lang.String.format(r8, r4)
                r5 = 2131165247(0x7var_f, float:1.7944706E38)
                org.telegram.ui.ProfileActivity r6 = org.telegram.ui.ProfileActivity.this
                int r6 = r6.membersSectionRow
                int r6 = r6 - r10
                if (r2 == r6) goto L_0x0663
                r9 = 1
            L_0x0663:
                r1.setTextAndValueAndIcon(r3, r4, r5, r9)
                goto L_0x0912
            L_0x0668:
                r3 = 2131624507(0x7f0e023b, float:1.8876196E38)
                java.lang.String r4 = "ChannelAdministrators"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                r4 = 2131165247(0x7var_f, float:1.7944706E38)
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                int r5 = r5.membersSectionRow
                int r5 = r5 - r10
                if (r2 == r5) goto L_0x067e
                r9 = 1
            L_0x067e:
                r1.setTextAndIcon(r3, r4, r9)
                goto L_0x0912
            L_0x0683:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.blockedUsersRow
                if (r2 != r3) goto L_0x06e9
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r3 = r3.chatInfo
                if (r3 == 0) goto L_0x06ce
                r3 = 2131624512(0x7f0e0240, float:1.8876206E38)
                java.lang.String r4 = "ChannelBlacklist"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                java.lang.Object[] r4 = new java.lang.Object[r10]
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r5 = r5.chatInfo
                int r5 = r5.banned_count
                org.telegram.ui.ProfileActivity r6 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r6 = r6.chatInfo
                int r6 = r6.kicked_count
                int r5 = java.lang.Math.max(r5, r6)
                java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
                r4[r9] = r5
                java.lang.String r4 = java.lang.String.format(r8, r4)
                r5 = 2131165251(0x7var_, float:1.7944714E38)
                org.telegram.ui.ProfileActivity r6 = org.telegram.ui.ProfileActivity.this
                int r6 = r6.membersSectionRow
                int r6 = r6 - r10
                if (r2 == r6) goto L_0x06c9
                r9 = 1
            L_0x06c9:
                r1.setTextAndValueAndIcon(r3, r4, r5, r9)
                goto L_0x0912
            L_0x06ce:
                r3 = 2131624512(0x7f0e0240, float:1.8876206E38)
                java.lang.String r4 = "ChannelBlacklist"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                r4 = 2131165251(0x7var_, float:1.7944714E38)
                org.telegram.ui.ProfileActivity r5 = org.telegram.ui.ProfileActivity.this
                int r5 = r5.membersSectionRow
                int r5 = r5 - r10
                if (r2 == r5) goto L_0x06e4
                r9 = 1
            L_0x06e4:
                r1.setTextAndIcon(r3, r4, r9)
                goto L_0x0912
            L_0x06e9:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.addMemberRow
                if (r2 != r3) goto L_0x0912
                java.lang.String r2 = "windowBackgroundWhiteBlueIcon"
                java.lang.String r3 = "windowBackgroundWhiteBlueButton"
                r1.setColors(r2, r3)
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                int r2 = r2.chat_id
                if (r2 <= 0) goto L_0x0713
                r2 = 2131624117(0x7f0e00b5, float:1.8875405E38)
                java.lang.String r3 = "AddMember"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r3 = 2131165248(0x7var_, float:1.7944708E38)
                r1.setTextAndIcon(r2, r3, r10)
                goto L_0x0912
            L_0x0713:
                r2 = 2131624127(0x7f0e00bf, float:1.8875425E38)
                java.lang.String r3 = "AddRecipient"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r3 = 2131165248(0x7var_, float:1.7944708E38)
                r1.setTextAndIcon(r2, r3, r10)
                goto L_0x0912
            L_0x0724:
                android.view.View r1 = r1.itemView
                org.telegram.ui.Cells.AboutLinkCell r1 = (org.telegram.ui.Cells.AboutLinkCell) r1
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.userInfoRow
                if (r2 != r3) goto L_0x074c
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$UserFull r2 = r2.userInfo
                java.lang.String r2 = r2.about
                r3 = 2131626867(0x7f0e0b73, float:1.8880982E38)
                java.lang.String r4 = "UserBio"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                boolean r4 = r4.isBot
                r1.setTextAndValue(r2, r3, r4)
                goto L_0x0912
            L_0x074c:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.channelInfoRow
                if (r2 != r3) goto L_0x0912
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r2 = r2.chatInfo
                java.lang.String r2 = r2.about
            L_0x075c:
                java.lang.String r3 = "\n\n\n"
                boolean r4 = r2.contains(r3)
                if (r4 == 0) goto L_0x076b
                java.lang.String r4 = "\n\n"
                java.lang.String r2 = r2.replace(r3, r4)
                goto L_0x075c
            L_0x076b:
                r1.setText(r2, r10)
                goto L_0x0912
            L_0x0770:
                android.view.View r1 = r1.itemView
                org.telegram.ui.Cells.TextDetailCell r1 = (org.telegram.ui.Cells.TextDetailCell) r1
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.phoneRow
                if (r2 != r3) goto L_0x07cf
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                int r2 = r2.currentAccount
                org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.user_id
                java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
                org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r3)
                java.lang.String r3 = r2.phone
                boolean r3 = android.text.TextUtils.isEmpty(r3)
                if (r3 != 0) goto L_0x07b8
                org.telegram.PhoneFormat.PhoneFormat r3 = org.telegram.PhoneFormat.PhoneFormat.getInstance()
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                java.lang.String r5 = "+"
                r4.append(r5)
                java.lang.String r2 = r2.phone
                r4.append(r2)
                java.lang.String r2 = r4.toString()
                java.lang.String r2 = r3.format(r2)
                goto L_0x07c1
            L_0x07b8:
                r2 = 2131626167(0x7f0e08b7, float:1.8879563E38)
                java.lang.String r3 = "PhoneHidden"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            L_0x07c1:
                r3 = 2131626170(0x7f0e08ba, float:1.8879569E38)
                java.lang.String r4 = "PhoneMobile"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                r1.setTextAndValue(r2, r3, r9)
                goto L_0x0912
            L_0x07cf:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.usernameRow
                if (r2 != r3) goto L_0x0875
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                int r2 = r2.user_id
                if (r2 == 0) goto L_0x0825
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                int r2 = r2.currentAccount
                org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.user_id
                java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
                org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r3)
                if (r2 == 0) goto L_0x0815
                java.lang.String r3 = r2.username
                boolean r3 = android.text.TextUtils.isEmpty(r3)
                if (r3 != 0) goto L_0x0815
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r4 = "@"
                r3.append(r4)
                java.lang.String r2 = r2.username
                r3.append(r2)
                java.lang.String r2 = r3.toString()
                goto L_0x0817
            L_0x0815:
                java.lang.String r2 = "-"
            L_0x0817:
                r3 = 2131626903(0x7f0e0b97, float:1.8881055E38)
                java.lang.String r4 = "Username"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                r1.setTextAndValue(r2, r3, r9)
                goto L_0x0912
            L_0x0825:
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r2 = r2.currentChat
                if (r2 == 0) goto L_0x0912
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                int r2 = r2.currentAccount
                org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.chat_id
                java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
                org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r3)
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                int r4 = r4.currentAccount
                org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
                java.lang.String r4 = r4.linkPrefix
                r3.append(r4)
                java.lang.String r4 = "/"
                r3.append(r4)
                java.lang.String r2 = r2.username
                r3.append(r2)
                java.lang.String r2 = r3.toString()
                r3 = 2131625350(0x7f0e0586, float:1.8877905E38)
                java.lang.String r4 = "InviteLink"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                r1.setTextAndValue(r2, r3, r9)
                goto L_0x0912
            L_0x0875:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.locationRow
                if (r2 != r3) goto L_0x0912
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r2 = r2.chatInfo
                if (r2 == 0) goto L_0x0912
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r2 = r2.chatInfo
                org.telegram.tgnet.TLRPC$ChannelLocation r2 = r2.location
                boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_channelLocation
                if (r2 == 0) goto L_0x0912
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r2 = r2.chatInfo
                org.telegram.tgnet.TLRPC$ChannelLocation r2 = r2.location
                org.telegram.tgnet.TLRPC$TL_channelLocation r2 = (org.telegram.tgnet.TLRPC.TL_channelLocation) r2
                java.lang.String r2 = r2.address
                r3 = 2131624278(0x7f0e0156, float:1.8875731E38)
                java.lang.String r4 = "AttachLocation"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                r1.setTextAndValue(r2, r3, r9)
                goto L_0x0912
            L_0x08aa:
                android.view.View r1 = r1.itemView
                org.telegram.ui.Cells.HeaderCell r1 = (org.telegram.ui.Cells.HeaderCell) r1
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.infoHeaderRow
                if (r2 != r3) goto L_0x08ee
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r2 = r2.currentChat
                boolean r2 = org.telegram.messenger.ChatObject.isChannel(r2)
                if (r2 == 0) goto L_0x08e1
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r2 = r2.currentChat
                boolean r2 = r2.megagroup
                if (r2 != 0) goto L_0x08e1
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                int r2 = r2.channelInfoRow
                if (r2 == r8) goto L_0x08e1
                r2 = 2131626334(0x7f0e095e, float:1.8879901E38)
                java.lang.String r3 = "ReportChatDescription"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x0912
            L_0x08e1:
                r2 = 2131625335(0x7f0e0577, float:1.8877875E38)
                java.lang.String r3 = "Info"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x0912
            L_0x08ee:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.sharedHeaderRow
                if (r2 != r3) goto L_0x0903
                r2 = 2131626561(0x7f0e0a41, float:1.8880362E38)
                java.lang.String r3 = "SharedContent"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x0912
            L_0x0903:
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.membersHeaderRow
                if (r2 != r3) goto L_0x0912
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r5)
                r1.setText(r2)
            L_0x0912:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return (itemViewType == 1 || itemViewType == 5 || itemViewType == 7 || itemViewType == 9 || itemViewType == 10 || itemViewType == 11 || itemViewType == 12) ? false : true;
        }

        public int getItemCount() {
            return ProfileActivity.this.rowCount;
        }

        public int getItemViewType(int i) {
            if (i == ProfileActivity.this.infoHeaderRow || i == ProfileActivity.this.sharedHeaderRow || i == ProfileActivity.this.membersHeaderRow) {
                return 1;
            }
            if (i == ProfileActivity.this.phoneRow || i == ProfileActivity.this.usernameRow || i == ProfileActivity.this.locationRow) {
                return 2;
            }
            if (i == ProfileActivity.this.userInfoRow || i == ProfileActivity.this.channelInfoRow) {
                return 3;
            }
            if (i == ProfileActivity.this.settingsTimerRow || i == ProfileActivity.this.settingsKeyRow || i == ProfileActivity.this.photosRow || i == ProfileActivity.this.filesRow || i == ProfileActivity.this.linksRow || i == ProfileActivity.this.audioRow || i == ProfileActivity.this.voiceRow || i == ProfileActivity.this.groupsInCommonRow || i == ProfileActivity.this.startSecretChatRow || i == ProfileActivity.this.subscribersRow || i == ProfileActivity.this.administratorsRow || i == ProfileActivity.this.blockedUsersRow || i == ProfileActivity.this.leaveChannelRow || i == ProfileActivity.this.addMemberRow || i == ProfileActivity.this.joinRow || i == ProfileActivity.this.unblockRow) {
                return 4;
            }
            if (i == ProfileActivity.this.notificationsDividerRow) {
                return 5;
            }
            if (i == ProfileActivity.this.notificationsRow) {
                return 6;
            }
            if (i == ProfileActivity.this.infoSectionRow || i == ProfileActivity.this.sharedSectionRow || i == ProfileActivity.this.lastSectionRow || i == ProfileActivity.this.membersSectionRow || i == ProfileActivity.this.settingsSectionRow) {
                return 7;
            }
            if (i >= ProfileActivity.this.membersStartRow && i < ProfileActivity.this.membersEndRow) {
                return 8;
            }
            if (i == ProfileActivity.this.emptyRow) {
                return 11;
            }
            return i == ProfileActivity.this.bottomPaddingRow ? 12 : 0;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        $$Lambda$ProfileActivity$ybA4dDgphBoDMFj9vMkmt8EIenI r10 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                ProfileActivity.this.lambda$getThemeDescriptions$22$ProfileActivity();
            }
        };
        $$Lambda$ProfileActivity$ybA4dDgphBoDMFj9vMkmt8EIenI r7 = r10;
        $$Lambda$ProfileActivity$ybA4dDgphBoDMFj9vMkmt8EIenI r8 = r10;
        $$Lambda$ProfileActivity$ybA4dDgphBoDMFj9vMkmt8EIenI r72 = r10;
        return new ThemeDescription[]{new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuBackground"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuItem"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuItemIcon"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "actionBarDefaultIcon"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_actionBarSelectorBlue"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "chat_lockIcon"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_subtitleInProfileBlue"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundActionBarBlue"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "profile_title"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "profile_status"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_subtitleInProfileBlue"), new ThemeDescription(this.topView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_backgroundActionBarBlue"), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_backgroundActionBarBlue"), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"), new ThemeDescription(this.avatarImage, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.avatar_savedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"), new ThemeDescription(this.avatarImage, 0, (Class[]) null, (Paint) null, new Drawable[]{this.avatarDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_backgroundInProfileBlue"), new ThemeDescription(this.writeButton, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_actionIcon"), new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_actionBackground"), new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_actionPressedBackground"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGreenText2"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText5"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText2"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueButton"), new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueIcon"), new ThemeDescription((View) this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"), new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{UserCell.class}, new String[]{"adminTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_creatorIcon"), new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"), new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r8, "windowBackgroundWhiteGrayText"), new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r8, "windowBackgroundWhiteBlueText"), new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, (Paint) null, new Drawable[]{Theme.avatar_savedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r72, "avatar_backgroundRed"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r72, "avatar_backgroundOrange"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r72, "avatar_backgroundViolet"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r72, "avatar_backgroundGreen"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r72, "avatar_backgroundCyan"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r72, "avatar_backgroundBlue"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r72, "avatar_backgroundPink"), new ThemeDescription(this.undoView, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_background"), new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"), new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"), new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"), new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"), new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"), new ThemeDescription((View) this.undoView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{UndoView.class}, new String[]{"leftImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"), new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AboutLinkCell.class}, Theme.profile_aboutTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{AboutLinkCell.class}, Theme.profile_aboutTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteLinkText"), new ThemeDescription(this.listView, 0, new Class[]{AboutLinkCell.class}, Theme.linkSelectionPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteLinkSelection"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"), new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"), new ThemeDescription(this.nameTextView[1], 0, (Class[]) null, (Paint) null, new Drawable[]{this.verifiedCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_verifiedCheck"), new ThemeDescription(this.nameTextView[1], 0, (Class[]) null, (Paint) null, new Drawable[]{this.verifiedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_verifiedBackground")};
    }

    public /* synthetic */ void lambda$getThemeDescriptions$22$ProfileActivity() {
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
            Object tag = this.onlineTextView[1].getTag();
            if (tag instanceof String) {
                this.onlineTextView[1].setTextColor(Theme.getColor((String) tag));
            }
            Drawable drawable = this.lockIconDrawable;
            if (drawable != null) {
                drawable.setColorFilter(Theme.getColor("chat_lockIcon"), PorterDuff.Mode.MULTIPLY);
            }
            ScamDrawable scamDrawable2 = this.scamDrawable;
            if (scamDrawable2 != null) {
                scamDrawable2.setColor(Theme.getColor("avatar_subtitleInProfileBlue"));
            }
            this.nameTextView[1].setBackgroundColor(Theme.getColor("avatar_backgroundActionBarBlue"));
            this.nameTextView[1].setTextColor(Theme.getColor("profile_title"));
            this.actionBar.setItemsColor(Theme.getColor("actionBarDefaultIcon"), false);
            this.actionBar.setItemsBackgroundColor(Theme.getColor("avatar_actionBarSelectorBlue"), false);
        }
    }
}
