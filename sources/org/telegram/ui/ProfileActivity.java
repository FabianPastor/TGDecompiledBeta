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
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Property;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Keep;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$BotInfo;
import org.telegram.tgnet.TLRPC$ChannelParticipant;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$ChatParticipant;
import org.telegram.tgnet.TLRPC$ChatPhoto;
import org.telegram.tgnet.TLRPC$DecryptedMessageAction;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$MessageAction;
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
import org.telegram.tgnet.TLRPC$TL_messageEncryptedAction;
import org.telegram.tgnet.TLRPC$TL_peerNotifySettings;
import org.telegram.tgnet.TLRPC$TL_userEmpty;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserFull;
import org.telegram.tgnet.TLRPC$UserProfilePhoto;
import org.telegram.tgnet.TLRPC$UserStatus;
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
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextDetailCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.ChatRightsEditActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CrossfadeDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.ProfileGalleryView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScamDrawable;
import org.telegram.ui.Components.SharedMediaLayout;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.GroupCreateActivity;
import org.telegram.ui.NotificationsSettingsActivity;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.ProfileActivity;

public class ProfileActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, DialogsActivity.DialogsActivityDelegate, SharedMediaLayout.SharedMediaPreloaderDelegate {
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
    private int avatarColor;
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
    public TLRPC$BotInfo botInfo;
    /* access modifiers changed from: private */
    public int bottomPaddingRow;
    /* access modifiers changed from: private */
    public ActionBarMenuItem callItem;
    /* access modifiers changed from: private */
    public boolean callItemVisible;
    private boolean canSearchMembers;
    /* access modifiers changed from: private */
    public int channelInfoRow;
    /* access modifiers changed from: private */
    public TLRPC$ChatFull chatInfo;
    /* access modifiers changed from: private */
    public int chat_id;
    /* access modifiers changed from: private */
    public boolean creatingChat;
    private TLRPC$ChannelParticipant currentChannelParticipant;
    /* access modifiers changed from: private */
    public TLRPC$Chat currentChat;
    /* access modifiers changed from: private */
    public TLRPC$EncryptedChat currentEncryptedChat;
    /* access modifiers changed from: private */
    public float currentExpanAnimatorFracture;
    /* access modifiers changed from: private */
    public long dialog_id;
    /* access modifiers changed from: private */
    public ActionBarMenuItem editItem;
    /* access modifiers changed from: private */
    public boolean editItemVisible;
    /* access modifiers changed from: private */
    public int emptyRow;
    /* access modifiers changed from: private */
    public ValueAnimator expandAnimator;
    private float[] expandAnimatorValues;
    /* access modifiers changed from: private */
    public boolean expandPhoto;
    private float expandProgress;
    /* access modifiers changed from: private */
    public float extraHeight;
    /* access modifiers changed from: private */
    public AnimatorSet headerAnimatorSet;
    protected float headerShadowAlpha;
    /* access modifiers changed from: private */
    public AnimatorSet headerShadowAnimatorSet;
    /* access modifiers changed from: private */
    public int infoHeaderRow;
    /* access modifiers changed from: private */
    public int infoSectionRow;
    /* access modifiers changed from: private */
    public float initialAnimationExtraHeight;
    /* access modifiers changed from: private */
    public boolean isBot;
    /* access modifiers changed from: private */
    public boolean isInLandscapeMode;
    private boolean[] isOnline;
    /* access modifiers changed from: private */
    public boolean isPulledDown;
    /* access modifiers changed from: private */
    public int joinRow;
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
    /* access modifiers changed from: private */
    public int notificationsDividerRow;
    /* access modifiers changed from: private */
    public int notificationsRow;
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
    /* access modifiers changed from: private */
    public int playProfileAnimation;
    /* access modifiers changed from: private */
    public HashMap<Integer, Integer> positionToOffset;
    private PhotoViewer.PhotoViewerProvider provider;
    private boolean recreateMenuAfterAnimation;
    /* access modifiers changed from: private */
    public Rect rect;
    /* access modifiers changed from: private */
    public int reportRow;
    /* access modifiers changed from: private */
    public boolean reportSpam;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public ScamDrawable scamDrawable;
    private int selectedUser;
    /* access modifiers changed from: private */
    public int sendMessageRow;
    /* access modifiers changed from: private */
    public int settingsKeyRow;
    /* access modifiers changed from: private */
    public int settingsSectionRow;
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
    public TopView topView;
    /* access modifiers changed from: private */
    public int unblockRow;
    /* access modifiers changed from: private */
    public UndoView undoView;
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
    private Drawable verifiedCheckDrawable;
    private CrossfadeDrawable verifiedCrossfadeDrawable;
    private Drawable verifiedDrawable;
    private ImageView writeButton;
    /* access modifiers changed from: private */
    public AnimatorSet writeButtonAnimation;

    public static class AvatarImageView extends BackupImageView {
        private float foregroundAlpha;
        private ImageReceiver foregroundImageReceiver = new ImageReceiver(this);
        private final Paint placeholderPaint;
        private final RectF rect = new RectF();

        public AvatarImageView(Context context) {
            super(context);
            Paint paint = new Paint(1);
            this.placeholderPaint = paint;
            paint.setColor(-16777216);
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
            float f = (float) this.foregroundImageReceiver.getRoundRadius()[0];
            canvas.drawRoundRect(this.rect, f, f, this.placeholderPaint);
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
            float access$700 = ProfileActivity.this.extraHeight + ((float) (ActionBar.getCurrentActionBarHeight() + (ProfileActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0)));
            int access$800 = (int) ((1.0f - ProfileActivity.this.mediaHeaderAnimationProgress) * access$700);
            if (access$800 != 0) {
                this.paint.setColor(this.currentColor);
                canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) access$800, this.paint);
            }
            float f = (float) access$800;
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
        /* access modifiers changed from: private */
        public boolean isOverlaysVisible;
        private long lastTime;
        private final float[] pressedOverlayAlpha;
        private final GradientDrawable[] pressedOverlayGradient;
        private final boolean[] pressedOverlayVisible;
        private final RectF rect;
        private final Paint selectedBarPaint;
        private final int statusBarHeight;
        private final GradientDrawable topOverlayGradient;
        private final Rect topOverlayRect;

        public OverlaysView(Context context) {
            super(context);
            this.statusBarHeight = ProfileActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0;
            this.topOverlayRect = new Rect();
            this.bottomOverlayRect = new Rect();
            this.rect = new RectF();
            this.animatorValues = new float[]{0.0f, 1.0f};
            this.pressedOverlayGradient = new GradientDrawable[2];
            this.pressedOverlayVisible = new boolean[2];
            this.pressedOverlayAlpha = new float[2];
            this.alpha = 1.0f;
            this.alphas = null;
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
            this.backgroundPaint.setAlpha(66);
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.animator = ofFloat;
            ofFloat.setDuration(250);
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
            float[] fArr = this.animatorValues;
            float animatedFraction = valueAnimator.getAnimatedFraction();
            this.currentAnimationValue = animatedFraction;
            setAlphaValue(AndroidUtilities.lerp(fArr, animatedFraction), true);
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
        /* JADX WARNING: Removed duplicated region for block: B:106:? A[RETURN, SYNTHETIC] */
        /* JADX WARNING: Removed duplicated region for block: B:73:0x01b4  */
        /* JADX WARNING: Removed duplicated region for block: B:91:0x01fc  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onDraw(android.graphics.Canvas r19) {
            /*
                r18 = this;
                r0 = r18
                r1 = r19
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
                float[] r8 = r0.alphas
                if (r8 == 0) goto L_0x005c
                int r8 = r8.length
                if (r8 == r3) goto L_0x0063
            L_0x005c:
                float[] r8 = new float[r3]
                r0.alphas = r8
                java.util.Arrays.fill(r8, r6)
            L_0x0063:
                long r8 = android.os.SystemClock.elapsedRealtime()
                long r10 = r0.lastTime
                long r10 = r8 - r10
                r12 = 0
                int r14 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
                if (r14 < 0) goto L_0x0077
                r12 = 20
                int r14 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
                if (r14 <= 0) goto L_0x0079
            L_0x0077:
                r10 = 17
            L_0x0079:
                r0.lastTime = r8
                r9 = 1065353216(0x3var_, float:1.0)
                r12 = 1
                if (r3 <= r12) goto L_0x01ae
                r13 = 20
                if (r3 > r13) goto L_0x01ae
                org.telegram.ui.ProfileActivity r13 = org.telegram.ui.ProfileActivity.this
                int r13 = r13.overlayCountVisible
                r14 = 3
                if (r13 != 0) goto L_0x0095
                r0.alpha = r9
                org.telegram.ui.ProfileActivity r13 = org.telegram.ui.ProfileActivity.this
                int unused = r13.overlayCountVisible = r14
                goto L_0x00a4
            L_0x0095:
                org.telegram.ui.ProfileActivity r13 = org.telegram.ui.ProfileActivity.this
                int r13 = r13.overlayCountVisible
                if (r13 != r12) goto L_0x00a4
                r0.alpha = r6
                org.telegram.ui.ProfileActivity r13 = org.telegram.ui.ProfileActivity.this
                int unused = r13.overlayCountVisible = r5
            L_0x00a4:
                org.telegram.ui.ProfileActivity r13 = org.telegram.ui.ProfileActivity.this
                int r13 = r13.overlayCountVisible
                r15 = 1118437376(0x42aa0000, float:85.0)
                if (r13 != r5) goto L_0x00c2
                android.graphics.Paint r13 = r0.barPaint
                float r2 = r0.alpha
                float r2 = r2 * r15
                int r2 = (int) r2
                r13.setAlpha(r2)
                android.graphics.Paint r2 = r0.selectedBarPaint
                float r13 = r0.alpha
                float r13 = r13 * r4
                int r13 = (int) r13
                r2.setAlpha(r13)
            L_0x00c2:
                int r2 = r18.getMeasuredWidth()
                r13 = 1092616192(0x41200000, float:10.0)
                int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
                int r2 = r2 - r13
                int r13 = r3 + -1
                int r13 = r13 * 2
                float r13 = (float) r13
                int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
                int r2 = r2 - r13
                int r2 = r2 / r3
                r13 = 1082130432(0x40800000, float:4.0)
                int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
                int r12 = android.os.Build.VERSION.SDK_INT
                r6 = 21
                if (r12 < r6) goto L_0x00e7
                int r6 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                goto L_0x00e8
            L_0x00e7:
                r6 = 0
            L_0x00e8:
                int r13 = r13 + r6
                r6 = 0
            L_0x00ea:
                if (r6 >= r3) goto L_0x015b
                int r12 = r6 * 2
                int r12 = r12 + 5
                float r12 = (float) r12
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
                int r16 = r2 * r6
                int r12 = r12 + r16
                android.graphics.RectF r8 = r0.rect
                float r5 = (float) r12
                float r9 = (float) r13
                int r12 = r12 + r2
                float r12 = (float) r12
                r17 = 1073741824(0x40000000, float:2.0)
                int r17 = org.telegram.messenger.AndroidUtilities.dp(r17)
                int r4 = r13 + r17
                float r4 = (float) r4
                r8.set(r5, r9, r12, r4)
                if (r6 == r7) goto L_0x0133
                org.telegram.ui.ProfileActivity r4 = org.telegram.ui.ProfileActivity.this
                int r4 = r4.overlayCountVisible
                if (r4 != r14) goto L_0x0130
                android.graphics.Paint r4 = r0.barPaint
                org.telegram.ui.Components.CubicBezierInterpolator r5 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
                float[] r8 = r0.alphas
                r8 = r8[r6]
                float r5 = r5.getInterpolation(r8)
                r8 = 1132396544(0x437var_, float:255.0)
                float r5 = org.telegram.messenger.AndroidUtilities.lerp(r15, r8, r5)
                float r9 = r0.alpha
                float r5 = r5 * r9
                int r5 = (int) r5
                r4.setAlpha(r5)
                goto L_0x013b
            L_0x0130:
                r8 = 1132396544(0x437var_, float:255.0)
                goto L_0x013b
            L_0x0133:
                r8 = 1132396544(0x437var_, float:255.0)
                float[] r4 = r0.alphas
                r5 = 1061158912(0x3var_, float:0.75)
                r4[r6] = r5
            L_0x013b:
                android.graphics.RectF r4 = r0.rect
                r5 = 1065353216(0x3var_, float:1.0)
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r5)
                float r9 = (float) r9
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r5)
                float r5 = (float) r12
                if (r6 != r7) goto L_0x014e
                android.graphics.Paint r12 = r0.selectedBarPaint
                goto L_0x0150
            L_0x014e:
                android.graphics.Paint r12 = r0.barPaint
            L_0x0150:
                r1.drawRoundRect(r4, r9, r5, r12)
                int r6 = r6 + 1
                r4 = 1132396544(0x437var_, float:255.0)
                r5 = 2
                r9 = 1065353216(0x3var_, float:1.0)
                goto L_0x00ea
            L_0x015b:
                org.telegram.ui.ProfileActivity r1 = org.telegram.ui.ProfileActivity.this
                int r1 = r1.overlayCountVisible
                r2 = 2
                if (r1 != r2) goto L_0x0181
                float r1 = r0.alpha
                r2 = 1065353216(0x3var_, float:1.0)
                int r3 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r3 >= 0) goto L_0x017b
                float r3 = (float) r10
                r4 = 1127481344(0x43340000, float:180.0)
                float r3 = r3 / r4
                float r1 = r1 + r3
                r0.alpha = r1
                int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r1 <= 0) goto L_0x0179
                r0.alpha = r2
            L_0x0179:
                r2 = 1
                goto L_0x01af
            L_0x017b:
                org.telegram.ui.ProfileActivity r1 = org.telegram.ui.ProfileActivity.this
                int unused = r1.overlayCountVisible = r14
                goto L_0x01ae
            L_0x0181:
                org.telegram.ui.ProfileActivity r1 = org.telegram.ui.ProfileActivity.this
                int r1 = r1.overlayCountVisible
                if (r1 != r14) goto L_0x01ae
                r1 = 0
                r2 = 0
            L_0x018b:
                float[] r3 = r0.alphas
                int r4 = r3.length
                if (r1 >= r4) goto L_0x01af
                if (r1 == r7) goto L_0x01ab
                r4 = r3[r1]
                r5 = 0
                int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
                if (r4 <= 0) goto L_0x01ab
                r2 = r3[r1]
                float r4 = (float) r10
                r6 = 1140457472(0x43fa0000, float:500.0)
                float r4 = r4 / r6
                float r2 = r2 - r4
                r3[r1] = r2
                r2 = r3[r1]
                int r2 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1))
                if (r2 >= 0) goto L_0x01aa
                r3[r1] = r5
            L_0x01aa:
                r2 = 1
            L_0x01ab:
                int r1 = r1 + 1
                goto L_0x018b
            L_0x01ae:
                r2 = 0
            L_0x01af:
                r1 = r2
                r2 = 0
                r3 = 2
            L_0x01b2:
                if (r2 >= r3) goto L_0x01fa
                boolean[] r4 = r0.pressedOverlayVisible
                boolean r4 = r4[r2]
                if (r4 == 0) goto L_0x01da
                float[] r4 = r0.pressedOverlayAlpha
                r5 = r4[r2]
                r6 = 1065353216(0x3var_, float:1.0)
                int r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
                if (r5 >= 0) goto L_0x01d6
                r1 = r4[r2]
                float r5 = (float) r10
                r7 = 1127481344(0x43340000, float:180.0)
                float r5 = r5 / r7
                float r1 = r1 + r5
                r4[r2] = r1
                r1 = r4[r2]
                int r1 = (r1 > r6 ? 1 : (r1 == r6 ? 0 : -1))
                if (r1 <= 0) goto L_0x01d5
                r4[r2] = r6
            L_0x01d5:
                r1 = 1
            L_0x01d6:
                r7 = 0
            L_0x01d7:
                r8 = 1127481344(0x43340000, float:180.0)
                goto L_0x01f7
            L_0x01da:
                r6 = 1065353216(0x3var_, float:1.0)
                float[] r4 = r0.pressedOverlayAlpha
                r5 = r4[r2]
                r7 = 0
                int r5 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
                if (r5 <= 0) goto L_0x01d7
                r1 = r4[r2]
                float r5 = (float) r10
                r8 = 1127481344(0x43340000, float:180.0)
                float r5 = r5 / r8
                float r1 = r1 - r5
                r4[r2] = r1
                r1 = r4[r2]
                int r1 = (r1 > r7 ? 1 : (r1 == r7 ? 0 : -1))
                if (r1 >= 0) goto L_0x01f6
                r4[r2] = r7
            L_0x01f6:
                r1 = 1
            L_0x01f7:
                int r2 = r2 + 1
                goto L_0x01b2
            L_0x01fa:
                if (r1 == 0) goto L_0x01ff
                r18.postInvalidateOnAnimation()
            L_0x01ff:
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
            TextPaint textPaint2 = new TextPaint(1);
            this.textPaint = textPaint2;
            textPaint2.setColor(-1);
            this.textPaint.setTypeface(Typeface.SANS_SERIF);
            this.textPaint.setTextAlign(Paint.Align.CENTER);
            this.textPaint.setTextSize(AndroidUtilities.dpf2(15.0f));
            Paint paint = new Paint(1);
            this.backgroundPaint = paint;
            paint.setColor(NUM);
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.animator = ofFloat;
            ofFloat.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
            this.animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ProfileActivity.PagerIndicatorView.this.lambda$new$0$ProfileActivity$PagerIndicatorView(valueAnimator);
                }
            });
            this.animator.addListener(new AnimatorListenerAdapter(ProfileActivity.this) {
                public void onAnimationEnd(Animator animator) {
                    if (PagerIndicatorView.this.isIndicatorVisible) {
                        ActionBarMenuItem access$2100 = PagerIndicatorView.this.getSecondaryMenuItem();
                        if (access$2100 != null) {
                            access$2100.setVisibility(8);
                            return;
                        }
                        return;
                    }
                    PagerIndicatorView.this.setVisibility(8);
                }

                public void onAnimationStart(Animator animator) {
                    ActionBarMenuItem access$2100 = PagerIndicatorView.this.getSecondaryMenuItem();
                    if (access$2100 != null) {
                        access$2100.setVisibility(0);
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
                    int realCount = ProfileActivity.this.avatarsViewPager.getRealCount();
                    if (ProfileActivity.this.overlayCountVisible == 0 && realCount > 1 && realCount <= 20 && ProfileActivity.this.overlaysView.isOverlaysVisible()) {
                        int unused = ProfileActivity.this.overlayCountVisible = 1;
                    }
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
            invalidateIndicatorRect();
        }

        /* access modifiers changed from: private */
        public void invalidateIndicatorRect() {
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
            return null;
        }
    }

    public ProfileActivity(Bundle bundle) {
        this(bundle, (SharedMediaLayout.SharedMediaPreloader) null);
    }

    public ProfileActivity(Bundle bundle, SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader2) {
        super(bundle);
        this.nameTextView = new SimpleTextView[2];
        this.onlineTextView = new SimpleTextView[3];
        this.isOnline = new boolean[1];
        this.headerShadowAlpha = 1.0f;
        this.participantsMap = new SparseArray<>();
        this.allowProfileAnimation = true;
        this.positionToOffset = new HashMap<>();
        this.expandAnimatorValues = new float[]{0.0f, 1.0f};
        this.onlineCount = -1;
        this.rect = new Rect();
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
                if (ProfileActivity.this.user_id == 0 ? ProfileActivity.this.chat_id == 0 || (chat = MessagesController.getInstance(ProfileActivity.this.currentAccount).getChat(Integer.valueOf(ProfileActivity.this.chat_id))) == null || (tLRPC$ChatPhoto = chat.photo) == null || (tLRPC$FileLocation2 = tLRPC$ChatPhoto.photo_big) == null : (user = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id))) == null || (tLRPC$UserProfilePhoto = user.photo) == null || (tLRPC$FileLocation2 = tLRPC$UserProfilePhoto.photo_big) == null) {
                    tLRPC$FileLocation2 = null;
                }
                if (tLRPC$FileLocation2 == null || tLRPC$FileLocation2.local_id != tLRPC$FileLocation.local_id || tLRPC$FileLocation2.volume_id != tLRPC$FileLocation.volume_id || tLRPC$FileLocation2.dc_id != tLRPC$FileLocation.dc_id) {
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
                ProfileActivity.this.editItem.setIconColor(Theme.getColor("actionBarDefaultIcon"));
            }

            public Float get(ActionBar actionBar) {
                return Float.valueOf(ProfileActivity.this.mediaHeaderAnimationProgress);
            }
        };
        this.sharedMediaPreloader = sharedMediaPreloader2;
    }

    public boolean onFragmentCreate() {
        boolean z = false;
        this.user_id = this.arguments.getInt("user_id", 0);
        this.chat_id = this.arguments.getInt("chat_id", 0);
        this.banFromGroup = this.arguments.getInt("ban_chat_id", 0);
        this.reportSpam = this.arguments.getBoolean("reportSpam", false);
        if (!this.expandPhoto) {
            boolean z2 = this.arguments.getBoolean("expandPhoto", false);
            this.expandPhoto = z2;
            if (z2) {
                this.needSendMessage = true;
            }
        }
        if (this.user_id != 0) {
            long j = this.arguments.getLong("dialog_id", 0);
            this.dialog_id = j;
            if (j != 0) {
                this.currentEncryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf((int) (this.dialog_id >> 32)));
            }
            TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
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
            if (MessagesController.getInstance(this.currentAccount).blockedUsers.indexOfKey(this.user_id) >= 0) {
                z = true;
            }
            this.userBlocked = z;
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
            TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id));
            this.currentChat = chat;
            if (chat == null) {
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
        if (this.sharedMediaPreloader == null) {
            this.sharedMediaPreloader = new SharedMediaLayout.SharedMediaPreloader(this);
        }
        this.sharedMediaPreloader.addDelegate(this);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didReceiveNewMessages);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
        updateRowsIds();
        return true;
    }

    public /* synthetic */ void lambda$onFragmentCreate$0$ProfileActivity(CountDownLatch countDownLatch) {
        this.currentChat = MessagesStorage.getInstance(this.currentAccount).getChat(this.chat_id);
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
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didReceiveNewMessages);
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
        AnonymousClass3 r0 = new ActionBar(context) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                ProfileActivity.this.avatarImage.getHitRect(ProfileActivity.this.rect);
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
        if (Build.VERSION.SDK_INT >= 21 && !AndroidUtilities.isTablet()) {
            z = true;
        }
        r0.setOccupyStatusBar(z);
        return r0;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x003d, code lost:
        r0 = r0.participants;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View createView(android.content.Context r28) {
        /*
            r27 = this;
            r10 = r27
            r11 = r28
            org.telegram.ui.ActionBar.Theme.createProfileResources(r28)
            r12 = 1
            r10.hasOwnBackground = r12
            r13 = 1118830592(0x42b00000, float:88.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r0 = (float) r0
            r10.extraHeight = r0
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            org.telegram.ui.ProfileActivity$4 r1 = new org.telegram.ui.ProfileActivity$4
            r1.<init>()
            r0.setActionBarMenuOnItemClick(r1)
            org.telegram.ui.Components.SharedMediaLayout r0 = r10.sharedMediaLayout
            if (r0 == 0) goto L_0x0024
            r0.onDestroy()
        L_0x0024:
            long r0 = r10.dialog_id
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 == 0) goto L_0x002e
        L_0x002c:
            r14 = r0
            goto L_0x0038
        L_0x002e:
            int r0 = r10.user_id
            if (r0 == 0) goto L_0x0033
            goto L_0x0036
        L_0x0033:
            int r0 = r10.chat_id
            int r0 = -r0
        L_0x0036:
            long r0 = (long) r0
            goto L_0x002c
        L_0x0038:
            org.telegram.tgnet.TLRPC$ChatFull r0 = r10.chatInfo
            r9 = 0
            if (r0 == 0) goto L_0x004d
            org.telegram.tgnet.TLRPC$ChatParticipants r0 = r0.participants
            if (r0 == 0) goto L_0x004d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r0 = r0.participants
            int r0 = r0.size()
            r1 = 5
            if (r0 <= r1) goto L_0x004d
            java.util.ArrayList<java.lang.Integer> r0 = r10.sortedUsers
            goto L_0x004e
        L_0x004d:
            r0 = r9
        L_0x004e:
            if (r0 == 0) goto L_0x0054
            org.telegram.tgnet.TLRPC$ChatFull r0 = r10.chatInfo
            r8 = r0
            goto L_0x0055
        L_0x0054:
            r8 = r9
        L_0x0055:
            org.telegram.ui.ProfileActivity$5 r7 = new org.telegram.ui.ProfileActivity$5
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloader r5 = r10.sharedMediaPreloader
            org.telegram.tgnet.TLRPC$UserFull r0 = r10.userInfo
            r6 = 0
            if (r0 == 0) goto L_0x0063
            int r0 = r0.common_chats_count
            r16 = r0
            goto L_0x0065
        L_0x0063:
            r16 = 0
        L_0x0065:
            java.util.ArrayList<java.lang.Integer> r3 = r10.sortedUsers
            r0 = r7
            r1 = r27
            r2 = r28
            r17 = r3
            r3 = r14
            r13 = 0
            r6 = r16
            r13 = r7
            r7 = r17
            r9 = r27
            r0.<init>(r2, r3, r5, r6, r7, r8, r9)
            r10.sharedMediaLayout = r13
            androidx.recyclerview.widget.RecyclerView$LayoutParams r0 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
            r9 = -1
            r0.<init>((int) r9, (int) r9)
            r13.setLayoutParams(r0)
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            org.telegram.ui.ActionBar.ActionBarMenu r0 = r0.createMenu()
            r1 = 15
            r2 = 2131165457(0x7var_, float:1.7945132E38)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.addItem((int) r1, (int) r2)
            r10.callItem = r1
            r1 = 12
            r2 = 2131165427(0x7var_f3, float:1.794507E38)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.addItem((int) r1, (int) r2)
            r10.editItem = r1
            r1 = 10
            r2 = 2131165444(0x7var_, float:1.7945105E38)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r0.addItem((int) r1, (int) r2)
            r10.otherItem = r0
            r27.createActionBarMenu()
            org.telegram.ui.ProfileActivity$ListAdapter r0 = new org.telegram.ui.ProfileActivity$ListAdapter
            r0.<init>(r11)
            r10.listAdapter = r0
            org.telegram.ui.Components.AvatarDrawable r0 = new org.telegram.ui.Components.AvatarDrawable
            r0.<init>()
            r10.avatarDrawable = r0
            r0.setProfile(r12)
            org.telegram.ui.ProfileActivity$6 r0 = new org.telegram.ui.ProfileActivity$6
            r0.<init>(r11)
            r10.fragmentView = r0
            r13 = r0
            android.widget.FrameLayout r13 = (android.widget.FrameLayout) r13
            org.telegram.ui.ProfileActivity$7 r0 = new org.telegram.ui.ProfileActivity$7
            r0.<init>(r11)
            r10.listView = r0
            r1 = 0
            r0.setVerticalScrollBarEnabled(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r10.listView
            r2 = 0
            r0.setItemAnimator(r2)
            org.telegram.ui.Components.RecyclerListView r0 = r10.listView
            r0.setLayoutAnimation(r2)
            org.telegram.ui.Components.RecyclerListView r0 = r10.listView
            r0.setClipToPadding(r1)
            org.telegram.ui.ProfileActivity$8 r0 = new org.telegram.ui.ProfileActivity$8
            r0.<init>(r11)
            r10.layoutManager = r0
            r0.setOrientation(r12)
            org.telegram.ui.Components.RecyclerListView r0 = r10.listView
            androidx.recyclerview.widget.LinearLayoutManager r2 = r10.layoutManager
            r0.setLayoutManager(r2)
            org.telegram.ui.Components.RecyclerListView r0 = r10.listView
            r0.setGlowColor(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r10.listView
            org.telegram.ui.ProfileActivity$ListAdapter r1 = r10.listAdapter
            r0.setAdapter(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r10.listView
            r1 = 51
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r9, r1)
            r13.addView(r0, r2)
            org.telegram.ui.Components.RecyclerListView r0 = r10.listView
            org.telegram.ui.-$$Lambda$ProfileActivity$mwsb9NzHseYYBEYzP4kgSFPg4a0 r2 = new org.telegram.ui.-$$Lambda$ProfileActivity$mwsb9NzHseYYBEYzP4kgSFPg4a0
            r2.<init>(r14)
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended) r2)
            org.telegram.ui.Components.RecyclerListView r0 = r10.listView
            org.telegram.ui.-$$Lambda$ProfileActivity$qil7LYBqLnCnjKNxHYvwopN7sVM r2 = new org.telegram.ui.-$$Lambda$ProfileActivity$qil7LYBqLnCnjKNxHYvwopN7sVM
            r2.<init>()
            r0.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r2)
            int r0 = r10.banFromGroup
            java.lang.String r14 = "fonts/rmedium.ttf"
            r15 = 1111490560(0x42400000, float:48.0)
            if (r0 == 0) goto L_0x01d9
            int r0 = r10.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            int r2 = r10.banFromGroup
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r2)
            org.telegram.tgnet.TLRPC$ChannelParticipant r2 = r10.currentChannelParticipant
            if (r2 != 0) goto L_0x0164
            org.telegram.tgnet.TLRPC$TL_channels_getParticipant r2 = new org.telegram.tgnet.TLRPC$TL_channels_getParticipant
            r2.<init>()
            org.telegram.tgnet.TLRPC$InputChannel r3 = org.telegram.messenger.MessagesController.getInputChannel((org.telegram.tgnet.TLRPC$Chat) r0)
            r2.channel = r3
            int r3 = r10.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            int r4 = r10.user_id
            org.telegram.tgnet.TLRPC$InputUser r3 = r3.getInputUser((int) r4)
            r2.user_id = r3
            int r3 = r10.currentAccount
            org.telegram.tgnet.ConnectionsManager r3 = org.telegram.tgnet.ConnectionsManager.getInstance(r3)
            org.telegram.ui.-$$Lambda$ProfileActivity$NUFxE4zilxB10GozoRMqSDX9pyI r4 = new org.telegram.ui.-$$Lambda$ProfileActivity$NUFxE4zilxB10GozoRMqSDX9pyI
            r4.<init>()
            r3.sendRequest(r2, r4)
        L_0x0164:
            org.telegram.ui.ProfileActivity$9 r2 = new org.telegram.ui.ProfileActivity$9
            r2.<init>(r10, r11)
            r3 = 0
            r2.setWillNotDraw(r3)
            r3 = 83
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r1, r3)
            r13.addView(r2, r1)
            org.telegram.ui.-$$Lambda$ProfileActivity$oeDLd2KbD-OEOJSMQAJ5CdPQb1c r1 = new org.telegram.ui.-$$Lambda$ProfileActivity$oeDLd2KbD-OEOJSMQAJ5CdPQb1c
            r1.<init>(r0)
            r2.setOnClickListener(r1)
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r11)
            java.lang.String r1 = "windowBackgroundWhiteRedText"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setTextColor(r1)
            r1 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r12, r1)
            r1 = 17
            r0.setGravity(r1)
            android.graphics.Typeface r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r14)
            r0.setTypeface(r1)
            r1 = 2131624412(0x7f0e01dc, float:1.8876003E38)
            java.lang.String r3 = "BanFromTheGroup"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r0.setText(r1)
            r19 = -2
            r20 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r21 = 17
            r22 = 0
            r23 = 1065353216(0x3var_, float:1.0)
            r24 = 0
            r25 = 0
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r2.addView(r0, r1)
            org.telegram.ui.Components.RecyclerListView r0 = r10.listView
            r1 = 1118830592(0x42b00000, float:88.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r3 = 0
            r0.setPadding(r3, r1, r3, r2)
            org.telegram.ui.Components.RecyclerListView r0 = r10.listView
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r0.setBottomGlowOffset(r1)
            goto L_0x01e5
        L_0x01d9:
            r1 = 1118830592(0x42b00000, float:88.0)
            r3 = 0
            org.telegram.ui.Components.RecyclerListView r0 = r10.listView
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.setPadding(r3, r1, r3, r3)
        L_0x01e5:
            org.telegram.ui.ProfileActivity$TopView r0 = new org.telegram.ui.ProfileActivity$TopView
            r0.<init>(r11)
            r10.topView = r0
            java.lang.String r1 = "avatar_backgroundActionBarBlue"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setBackgroundColor(r1)
            org.telegram.ui.ProfileActivity$TopView r0 = r10.topView
            r13.addView(r0)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = new org.telegram.ui.ProfileActivity$AvatarImageView
            r0.<init>(r11)
            r10.avatarImage = r0
            r1 = 1101529088(0x41a80000, float:21.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.setRoundRadius(r1)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r10.avatarImage
            r8 = 0
            r0.setPivotX(r8)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r10.avatarImage
            r0.setPivotY(r8)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r10.avatarImage
            r1 = 42
            r2 = 1109917696(0x42280000, float:42.0)
            r3 = 51
            r4 = 1115684864(0x42800000, float:64.0)
            r5 = 0
            r6 = 0
            r7 = 0
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r1, r2, r3, r4, r5, r6, r7)
            r13.addView(r0, r1)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r10.avatarImage
            org.telegram.ui.-$$Lambda$ProfileActivity$ECsrXvfRdSnP_bSANm3_0kB4UYo r1 = new org.telegram.ui.-$$Lambda$ProfileActivity$ECsrXvfRdSnP_bSANm3_0kB4UYo
            r1.<init>()
            r0.setOnClickListener(r1)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r10.avatarImage
            org.telegram.ui.-$$Lambda$ProfileActivity$NQ1WI_JFkd6-33-qRPc4vjT9LxM r1 = new org.telegram.ui.-$$Lambda$ProfileActivity$NQ1WI_JFkd6-33-qRPc4vjT9LxM
            r1.<init>()
            r0.setOnLongClickListener(r1)
            org.telegram.ui.ProfileActivity$AvatarImageView r0 = r10.avatarImage
            r1 = 2131623997(0x7f0e003d, float:1.8875161E38)
            java.lang.String r2 = "AccDescrProfilePicture"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setContentDescription(r1)
            org.telegram.ui.Components.ProfileGalleryView r0 = r10.avatarsViewPager
            if (r0 == 0) goto L_0x0252
            r0.onDestroy()
        L_0x0252:
            org.telegram.ui.ProfileActivity$OverlaysView r0 = new org.telegram.ui.ProfileActivity$OverlaysView
            r0.<init>(r11)
            r10.overlaysView = r0
            org.telegram.ui.Components.ProfileGalleryView r7 = new org.telegram.ui.Components.ProfileGalleryView
            int r0 = r10.user_id
            if (r0 == 0) goto L_0x0260
            goto L_0x0263
        L_0x0260:
            int r0 = r10.chat_id
            int r0 = -r0
        L_0x0263:
            long r0 = (long) r0
            r2 = r0
            org.telegram.ui.ActionBar.ActionBar r4 = r10.actionBar
            org.telegram.ui.Components.RecyclerListView r5 = r10.listView
            org.telegram.ui.ProfileActivity$AvatarImageView r6 = r10.avatarImage
            int r17 = r27.getClassGuid()
            org.telegram.ui.ProfileActivity$OverlaysView r1 = r10.overlaysView
            r0 = r7
            r18 = r1
            r1 = r28
            r15 = r7
            r7 = r17
            r8 = r18
            r0.<init>(r1, r2, r4, r5, r6, r7, r8)
            r10.avatarsViewPager = r15
            r13.addView(r15)
            org.telegram.ui.ProfileActivity$OverlaysView r0 = r10.overlaysView
            r13.addView(r0)
            org.telegram.ui.ProfileActivity$PagerIndicatorView r0 = new org.telegram.ui.ProfileActivity$PagerIndicatorView
            r0.<init>(r11)
            r10.avatarsViewPagerIndicatorView = r0
            r1 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r1)
            r13.addView(r0, r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            r13.addView(r0)
            r6 = 0
        L_0x029e:
            org.telegram.ui.ActionBar.SimpleTextView[] r0 = r10.nameTextView
            int r0 = r0.length
            r8 = 1065353216(0x3var_, float:1.0)
            r1 = 3
            if (r6 >= r0) goto L_0x0343
            int r0 = r10.playProfileAnimation
            if (r0 != 0) goto L_0x02af
            if (r6 != 0) goto L_0x02af
            r2 = 0
            goto L_0x033f
        L_0x02af:
            org.telegram.ui.ActionBar.SimpleTextView[] r0 = r10.nameTextView
            org.telegram.ui.ActionBar.SimpleTextView r2 = new org.telegram.ui.ActionBar.SimpleTextView
            r2.<init>(r11)
            r0[r6] = r2
            if (r6 != r12) goto L_0x02c8
            org.telegram.ui.ActionBar.SimpleTextView[] r0 = r10.nameTextView
            r0 = r0[r6]
            java.lang.String r2 = "profile_title"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setTextColor(r2)
            goto L_0x02d5
        L_0x02c8:
            org.telegram.ui.ActionBar.SimpleTextView[] r0 = r10.nameTextView
            r0 = r0[r6]
            java.lang.String r2 = "actionBarDefaultTitle"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setTextColor(r2)
        L_0x02d5:
            org.telegram.ui.ActionBar.SimpleTextView[] r0 = r10.nameTextView
            r0 = r0[r6]
            r2 = 18
            r0.setTextSize(r2)
            org.telegram.ui.ActionBar.SimpleTextView[] r0 = r10.nameTextView
            r0 = r0[r6]
            r0.setGravity(r1)
            org.telegram.ui.ActionBar.SimpleTextView[] r0 = r10.nameTextView
            r0 = r0[r6]
            android.graphics.Typeface r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r14)
            r0.setTypeface(r1)
            org.telegram.ui.ActionBar.SimpleTextView[] r0 = r10.nameTextView
            r0 = r0[r6]
            r1 = 1067869798(0x3fa66666, float:1.3)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = -r1
            r0.setLeftDrawableTopPadding(r1)
            org.telegram.ui.ActionBar.SimpleTextView[] r0 = r10.nameTextView
            r0 = r0[r6]
            r2 = 0
            r0.setPivotX(r2)
            org.telegram.ui.ActionBar.SimpleTextView[] r0 = r10.nameTextView
            r0 = r0[r6]
            r0.setPivotY(r2)
            org.telegram.ui.ActionBar.SimpleTextView[] r0 = r10.nameTextView
            r0 = r0[r6]
            if (r6 != 0) goto L_0x0315
            r8 = 0
        L_0x0315:
            r0.setAlpha(r8)
            if (r6 != r12) goto L_0x0321
            org.telegram.ui.ActionBar.SimpleTextView[] r0 = r10.nameTextView
            r0 = r0[r6]
            r0.setScrollNonFitText(r12)
        L_0x0321:
            org.telegram.ui.ActionBar.SimpleTextView[] r0 = r10.nameTextView
            r0 = r0[r6]
            r20 = -2
            r21 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r22 = 51
            r23 = 1122762752(0x42eCLASSNAME, float:118.0)
            r24 = 0
            if (r6 != 0) goto L_0x0334
            r25 = 1111490560(0x42400000, float:48.0)
            goto L_0x0336
        L_0x0334:
            r25 = 0
        L_0x0336:
            r26 = 0
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r13.addView(r0, r1)
        L_0x033f:
            int r6 = r6 + 1
            goto L_0x029e
        L_0x0343:
            r2 = 0
            r6 = 0
        L_0x0345:
            org.telegram.ui.ActionBar.SimpleTextView[] r0 = r10.onlineTextView
            int r3 = r0.length
            r4 = 2
            if (r6 >= r3) goto L_0x03b2
            org.telegram.ui.ActionBar.SimpleTextView r3 = new org.telegram.ui.ActionBar.SimpleTextView
            r3.<init>(r11)
            r0[r6] = r3
            if (r6 != r4) goto L_0x0362
            org.telegram.ui.ActionBar.SimpleTextView[] r0 = r10.onlineTextView
            r0 = r0[r6]
            java.lang.String r3 = "player_actionBarSubtitle"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r0.setTextColor(r3)
            goto L_0x036f
        L_0x0362:
            org.telegram.ui.ActionBar.SimpleTextView[] r0 = r10.onlineTextView
            r0 = r0[r6]
            java.lang.String r3 = "avatar_subtitleInProfileBlue"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r0.setTextColor(r3)
        L_0x036f:
            org.telegram.ui.ActionBar.SimpleTextView[] r0 = r10.onlineTextView
            r0 = r0[r6]
            r3 = 14
            r0.setTextSize(r3)
            org.telegram.ui.ActionBar.SimpleTextView[] r0 = r10.onlineTextView
            r0 = r0[r6]
            r0.setGravity(r1)
            org.telegram.ui.ActionBar.SimpleTextView[] r0 = r10.onlineTextView
            r0 = r0[r6]
            if (r6 == 0) goto L_0x038b
            if (r6 != r4) goto L_0x0388
            goto L_0x038b
        L_0x0388:
            r3 = 1065353216(0x3var_, float:1.0)
            goto L_0x038c
        L_0x038b:
            r3 = 0
        L_0x038c:
            r0.setAlpha(r3)
            org.telegram.ui.ActionBar.SimpleTextView[] r0 = r10.onlineTextView
            r0 = r0[r6]
            r20 = -2
            r21 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r22 = 51
            r23 = 1122762752(0x42eCLASSNAME, float:118.0)
            r24 = 0
            if (r6 != 0) goto L_0x03a2
            r25 = 1111490560(0x42400000, float:48.0)
            goto L_0x03a6
        L_0x03a2:
            r3 = 1090519040(0x41000000, float:8.0)
            r25 = 1090519040(0x41000000, float:8.0)
        L_0x03a6:
            r26 = 0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r13.addView(r0, r3)
            int r6 = r6 + 1
            goto L_0x0345
        L_0x03b2:
            r27.updateProfileData()
            int r0 = r10.user_id
            if (r0 == 0) goto L_0x04d8
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r11)
            r10.writeButton = r0
            r0 = 1113587712(0x42600000, float:56.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r0)
            java.lang.String r2 = "profile_actionBackground"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            java.lang.String r3 = "profile_actionPressedBackground"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorCircleDrawable(r1, r2, r3)
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 21
            if (r2 >= r3) goto L_0x0409
            android.content.res.Resources r2 = r28.getResources()
            r5 = 2131165393(0x7var_d1, float:1.7945002E38)
            android.graphics.drawable.Drawable r2 = r2.getDrawable(r5)
            android.graphics.drawable.Drawable r2 = r2.mutate()
            android.graphics.PorterDuffColorFilter r5 = new android.graphics.PorterDuffColorFilter
            r6 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.graphics.PorterDuff$Mode r7 = android.graphics.PorterDuff.Mode.MULTIPLY
            r5.<init>(r6, r7)
            r2.setColorFilter(r5)
            org.telegram.ui.Components.CombinedDrawable r5 = new org.telegram.ui.Components.CombinedDrawable
            r6 = 0
            r5.<init>(r2, r1, r6, r6)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r5.setIconSize(r1, r2)
            r1 = r5
        L_0x0409:
            android.widget.ImageView r2 = r10.writeButton
            r2.setBackgroundDrawable(r1)
            android.widget.ImageView r1 = r10.writeButton
            r2 = 2131165846(0x7var_, float:1.794592E38)
            r1.setImageResource(r2)
            android.widget.ImageView r1 = r10.writeButton
            r2 = 2131623987(0x7f0e0033, float:1.887514E38)
            java.lang.String r5 = "AccDescrOpenChat"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r5, r2)
            r1.setContentDescription(r2)
            android.widget.ImageView r1 = r10.writeButton
            android.graphics.PorterDuffColorFilter r2 = new android.graphics.PorterDuffColorFilter
            java.lang.String r5 = "profile_actionIcon"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            android.graphics.PorterDuff$Mode r6 = android.graphics.PorterDuff.Mode.MULTIPLY
            r2.<init>(r5, r6)
            r1.setColorFilter(r2)
            android.widget.ImageView r1 = r10.writeButton
            android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.CENTER
            r1.setScaleType(r2)
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r3) goto L_0x04a3
            android.animation.StateListAnimator r1 = new android.animation.StateListAnimator
            r1.<init>()
            int[] r2 = new int[r12]
            r5 = 16842919(0x10100a7, float:2.3694026E-38)
            r6 = 0
            r2[r6] = r5
            android.widget.ImageView r5 = r10.writeButton
            android.util.Property r7 = android.view.View.TRANSLATION_Z
            float[] r8 = new float[r4]
            r9 = 1073741824(0x40000000, float:2.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r14 = (float) r14
            r8[r6] = r14
            r14 = 1082130432(0x40800000, float:4.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r15 = (float) r15
            r8[r12] = r15
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofFloat(r5, r7, r8)
            r7 = 200(0xc8, double:9.9E-322)
            android.animation.ObjectAnimator r5 = r5.setDuration(r7)
            r1.addState(r2, r5)
            int[] r2 = new int[r6]
            android.widget.ImageView r5 = r10.writeButton
            android.util.Property r15 = android.view.View.TRANSLATION_Z
            float[] r0 = new float[r4]
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r14 = (float) r14
            r0[r6] = r14
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r6 = (float) r6
            r0[r12] = r6
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r5, r15, r0)
            android.animation.ObjectAnimator r0 = r0.setDuration(r7)
            r1.addState(r2, r0)
            android.widget.ImageView r0 = r10.writeButton
            r0.setStateListAnimator(r1)
            android.widget.ImageView r0 = r10.writeButton
            org.telegram.ui.ProfileActivity$11 r1 = new org.telegram.ui.ProfileActivity$11
            r1.<init>(r10)
            r0.setOutlineProvider(r1)
        L_0x04a3:
            android.widget.ImageView r0 = r10.writeButton
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r3) goto L_0x04ae
            r1 = 56
            r17 = 56
            goto L_0x04b2
        L_0x04ae:
            r1 = 60
            r17 = 60
        L_0x04b2:
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r3) goto L_0x04b9
            r18 = 1113587712(0x42600000, float:56.0)
            goto L_0x04bd
        L_0x04b9:
            r1 = 1114636288(0x42700000, float:60.0)
            r18 = 1114636288(0x42700000, float:60.0)
        L_0x04bd:
            r19 = 53
            r20 = 0
            r21 = 0
            r22 = 1098907648(0x41800000, float:16.0)
            r23 = 0
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r17, r18, r19, r20, r21, r22, r23)
            r13.addView(r0, r1)
            android.widget.ImageView r0 = r10.writeButton
            org.telegram.ui.-$$Lambda$ProfileActivity$FLVlUbfFcO5i_pOICqnKD7RYhzM r1 = new org.telegram.ui.-$$Lambda$ProfileActivity$FLVlUbfFcO5i_pOICqnKD7RYhzM
            r1.<init>()
            r0.setOnClickListener(r1)
        L_0x04d8:
            r27.needLayout()
            org.telegram.ui.Components.RecyclerListView r0 = r10.listView
            org.telegram.ui.ProfileActivity$12 r1 = new org.telegram.ui.ProfileActivity$12
            r1.<init>()
            r0.setOnScrollListener(r1)
            org.telegram.ui.Components.UndoView r0 = new org.telegram.ui.Components.UndoView
            r0.<init>(r11)
            r10.undoView = r0
            r14 = -1
            r15 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r16 = 83
            r17 = 1090519040(0x41000000, float:8.0)
            r18 = 0
            r19 = 1090519040(0x41000000, float:8.0)
            r20 = 1090519040(0x41000000, float:8.0)
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r15, r16, r17, r18, r19, r20)
            r13.addView(r0, r1)
            float[] r0 = new float[r4]
            r0 = {0, NUM} // fill-array
            android.animation.ValueAnimator r0 = android.animation.ValueAnimator.ofFloat(r0)
            r10.expandAnimator = r0
            org.telegram.ui.-$$Lambda$ProfileActivity$QJJPV3IcU1FQtb713QyXuNEn6Ms r1 = new org.telegram.ui.-$$Lambda$ProfileActivity$QJJPV3IcU1FQtb713QyXuNEn6Ms
            r1.<init>()
            r0.addUpdateListener(r1)
            android.animation.ValueAnimator r0 = r10.expandAnimator
            org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_BOTH
            r0.setInterpolator(r1)
            android.animation.ValueAnimator r0 = r10.expandAnimator
            org.telegram.ui.ProfileActivity$13 r1 = new org.telegram.ui.ProfileActivity$13
            r1.<init>()
            r0.addListener(r1)
            r27.updateSelectedMediaTabText()
            android.view.View r0 = r10.fragmentView
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.createView(android.content.Context):android.view.View");
    }

    public /* synthetic */ void lambda$createView$2$ProfileActivity(long j, View view, int i, float f, float f2) {
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
                            ProfileActivity.this.lambda$null$1$ProfileActivity(i);
                        }
                    });
                    return;
                }
                NotificationsCheckCell notificationsCheckCell = (NotificationsCheckCell) view;
                boolean z = !notificationsCheckCell.isChecked();
                boolean isGlobalNotificationsEnabled = NotificationsController.getInstance(this.currentAccount).isGlobalNotificationsEnabled(j);
                long j3 = 0;
                if (z) {
                    SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                    if (isGlobalNotificationsEnabled) {
                        edit.remove("notify2_" + j);
                    } else {
                        edit.putInt("notify2_" + j, 0);
                    }
                    MessagesStorage.getInstance(this.currentAccount).setDialogFlags(j, 0);
                    edit.commit();
                    TLRPC$Dialog tLRPC$Dialog = MessagesController.getInstance(this.currentAccount).dialogs_dict.get(j);
                    if (tLRPC$Dialog != null) {
                        tLRPC$Dialog.notify_settings = new TLRPC$TL_peerNotifySettings();
                    }
                    NotificationsController.getInstance(this.currentAccount).updateServerNotificationsSettings(j);
                } else {
                    SharedPreferences.Editor edit2 = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                    if (!isGlobalNotificationsEnabled) {
                        edit2.remove("notify2_" + j);
                    } else {
                        edit2.putInt("notify2_" + j, 2);
                        j3 = 1;
                    }
                    NotificationsController.getInstance(this.currentAccount).removeNotificationsForDialog(j);
                    MessagesStorage.getInstance(this.currentAccount).setDialogFlags(j, j3);
                    edit2.commit();
                    TLRPC$Dialog tLRPC$Dialog2 = MessagesController.getInstance(this.currentAccount).dialogs_dict.get(j);
                    if (tLRPC$Dialog2 != null) {
                        TLRPC$TL_peerNotifySettings tLRPC$TL_peerNotifySettings = new TLRPC$TL_peerNotifySettings();
                        tLRPC$Dialog2.notify_settings = tLRPC$TL_peerNotifySettings;
                        if (isGlobalNotificationsEnabled) {
                            tLRPC$TL_peerNotifySettings.mute_until = Integer.MAX_VALUE;
                        }
                    }
                    NotificationsController.getInstance(this.currentAccount).updateServerNotificationsSettings(j);
                }
                notificationsCheckCell.setChecked(z);
                RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findViewHolderForPosition(this.notificationsRow);
                if (holder != null) {
                    this.listAdapter.onBindViewHolder(holder, this.notificationsRow);
                }
            } else if (i2 == this.unblockRow) {
                MessagesController.getInstance(this.currentAccount).unblockUser(this.user_id);
                AlertsCreator.showSimpleToast(this, LocaleController.getString("UserUnblocked", NUM));
            } else if (i2 == this.sendMessageRow) {
                this.writeButton.callOnClick();
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
                            intent.putExtra("android.intent.extra.TEXT", this.currentChat.title + "\n" + this.chatInfo.about + "\nhttps://" + MessagesController.getInstance(this.currentAccount).linkPrefix + "/" + this.currentChat.username);
                        } else {
                            intent.putExtra("android.intent.extra.TEXT", this.currentChat.title + "\nhttps://" + MessagesController.getInstance(this.currentAccount).linkPrefix + "/" + this.currentChat.username);
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
                MessagesController.getInstance(this.currentAccount).addUserToChat(this.currentChat.id, UserConfig.getInstance(this.currentAccount).getCurrentUser(), (TLRPC$ChatFull) null, 0, (String) null, this, (Runnable) null);
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
            } else {
                processOnClickOrPress(i2);
            }
        }
    }

    public /* synthetic */ void lambda$null$1$ProfileActivity(int i) {
        this.listAdapter.notifyItemChanged(this.notificationsRow);
    }

    public /* synthetic */ boolean lambda$createView$3$ProfileActivity(View view, int i) {
        TLRPC$ChatParticipant tLRPC$ChatParticipant;
        if (i < this.membersStartRow || i >= this.membersEndRow) {
            return processOnClickOrPress(i);
        }
        if (!this.sortedUsers.isEmpty()) {
            tLRPC$ChatParticipant = this.chatInfo.participants.participants.get(this.sortedUsers.get(i - this.membersStartRow).intValue());
        } else {
            tLRPC$ChatParticipant = this.chatInfo.participants.participants.get(i - this.membersStartRow);
        }
        return onMemberClick(tLRPC$ChatParticipant, true);
    }

    public /* synthetic */ void lambda$createView$5$ProfileActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable(tLObject) {
                private final /* synthetic */ TLObject f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ProfileActivity.this.lambda$null$4$ProfileActivity(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$4$ProfileActivity(TLObject tLObject) {
        this.currentChannelParticipant = ((TLRPC$TL_channels_channelParticipant) tLObject).participant;
    }

    public /* synthetic */ void lambda$createView$6$ProfileActivity(TLRPC$Chat tLRPC$Chat, View view) {
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

    public /* synthetic */ void lambda$createView$7$ProfileActivity(View view) {
        RecyclerView.ViewHolder findContainingViewHolder;
        Integer num;
        if (!this.isInLandscapeMode && this.avatarImage.getImageReceiver().hasNotThumb()) {
            this.openingAvatar = true;
            this.allowPullingDown = true;
            View childAt = this.listView.getChildAt(0);
            if (!(childAt == null || (findContainingViewHolder = this.listView.findContainingViewHolder(childAt)) == null || (num = this.positionToOffset.get(Integer.valueOf(findContainingViewHolder.getAdapterPosition()))) == null)) {
                this.listView.smoothScrollBy(0, -(num.intValue() + ((this.listView.getPaddingTop() - childAt.getTop()) - this.actionBar.getMeasuredHeight())), CubicBezierInterpolator.EASE_OUT_QUINT);
                return;
            }
        }
        openAvatar();
    }

    public /* synthetic */ boolean lambda$createView$8$ProfileActivity(View view) {
        openAvatar();
        return false;
    }

    public /* synthetic */ void lambda$createView$9$ProfileActivity(View view) {
        if (this.playProfileAnimation != 0) {
            ArrayList<BaseFragment> arrayList = this.parentLayout.fragmentsStack;
            if (arrayList.get(arrayList.size() - 2) instanceof ChatActivity) {
                finishFragment();
                return;
            }
        }
        TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
        if (user != null && !(user instanceof TLRPC$TL_userEmpty)) {
            Bundle bundle = new Bundle();
            bundle.putInt("user_id", this.user_id);
            if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(bundle, this)) {
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                presentFragment(new ChatActivity(bundle), true);
            }
        }
    }

    public /* synthetic */ void lambda$createView$10$ProfileActivity(ValueAnimator valueAnimator) {
        int i;
        int currentActionBarHeight = ActionBar.getCurrentActionBarHeight() + (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0);
        float[] fArr = this.expandAnimatorValues;
        float animatedFraction = valueAnimator.getAnimatedFraction();
        this.currentExpanAnimatorFracture = animatedFraction;
        float lerp = AndroidUtilities.lerp(fArr, animatedFraction);
        this.avatarImage.setScaleX(this.avatarScale);
        this.avatarImage.setScaleY(this.avatarScale);
        this.avatarImage.setTranslationX(AndroidUtilities.lerp(this.avatarX, 0.0f, lerp));
        this.avatarImage.setTranslationY(AndroidUtilities.lerp((float) Math.ceil((double) this.avatarY), 0.0f, lerp));
        this.avatarImage.setRoundRadius((int) AndroidUtilities.lerp(AndroidUtilities.dpf2(21.0f), 0.0f, lerp));
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
        this.onlineTextView[2].setTranslationX(var_);
        this.onlineTextView[2].setTranslationY(var_);
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
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.avatarImage.getLayoutParams();
        layoutParams.width = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(42.0f), ((float) this.listView.getMeasuredWidth()) / this.avatarScale, lerp);
        layoutParams.height = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(42.0f), (this.extraHeight + f) / this.avatarScale, lerp);
        layoutParams.leftMargin = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(64.0f), 0.0f, lerp);
        this.avatarImage.requestLayout();
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

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x004d, code lost:
        r0 = org.telegram.messenger.MessagesController.getInstance(r3.currentAccount).getChat(java.lang.Integer.valueOf(r3.chat_id));
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void openAvatar() {
        /*
            r3 = this;
            org.telegram.ui.Components.RecyclerListView r0 = r3.listView
            int r0 = r0.getScrollState()
            r1 = 1
            if (r0 != r1) goto L_0x000a
            return
        L_0x000a:
            int r0 = r3.user_id
            if (r0 == 0) goto L_0x0049
            int r0 = r3.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            int r1 = r3.user_id
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r1)
            org.telegram.tgnet.TLRPC$UserProfilePhoto r1 = r0.photo
            if (r1 == 0) goto L_0x0087
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_big
            if (r1 == 0) goto L_0x0087
            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.getInstance()
            android.app.Activity r2 = r3.getParentActivity()
            r1.setParentActivity(r2)
            org.telegram.tgnet.TLRPC$UserProfilePhoto r1 = r0.photo
            int r2 = r1.dc_id
            if (r2 == 0) goto L_0x003b
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_big
            r1.dc_id = r2
        L_0x003b:
            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.getInstance()
            org.telegram.tgnet.TLRPC$UserProfilePhoto r0 = r0.photo
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.photo_big
            org.telegram.ui.PhotoViewer$PhotoViewerProvider r2 = r3.provider
            r1.openPhoto(r0, r2)
            goto L_0x0087
        L_0x0049:
            int r0 = r3.chat_id
            if (r0 == 0) goto L_0x0087
            int r0 = r3.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            int r1 = r3.chat_id
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r1)
            org.telegram.tgnet.TLRPC$ChatPhoto r1 = r0.photo
            if (r1 == 0) goto L_0x0087
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_big
            if (r1 == 0) goto L_0x0087
            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.getInstance()
            android.app.Activity r2 = r3.getParentActivity()
            r1.setParentActivity(r2)
            org.telegram.tgnet.TLRPC$ChatPhoto r1 = r0.photo
            int r2 = r1.dc_id
            if (r2 == 0) goto L_0x007a
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_big
            r1.dc_id = r2
        L_0x007a:
            org.telegram.ui.PhotoViewer r1 = org.telegram.ui.PhotoViewer.getInstance()
            org.telegram.tgnet.TLRPC$ChatPhoto r0 = r0.photo
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.photo_big
            org.telegram.ui.PhotoViewer$PhotoViewerProvider r2 = r3.provider
            r1.openPhoto(r0, r2)
        L_0x0087:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.openAvatar():void");
    }

    public boolean onMemberClick(TLRPC$ChatParticipant tLRPC$ChatParticipant, boolean z) {
        return onMemberClick(tLRPC$ChatParticipant, z, false);
    }

    public boolean onMemberClick(TLRPC$ChatParticipant tLRPC$ChatParticipant, boolean z, boolean z2) {
        boolean z3;
        TLRPC$ChannelParticipant tLRPC$ChannelParticipant;
        boolean z4;
        boolean z5;
        boolean z6;
        ArrayList arrayList;
        ArrayList arrayList2;
        boolean z7;
        String str;
        int i;
        if (getParentActivity() == null) {
            return false;
        }
        if (z) {
            TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(tLRPC$ChatParticipant.user_id));
            if (user == null || tLRPC$ChatParticipant.user_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                return false;
            }
            this.selectedUser = tLRPC$ChatParticipant.user_id;
            ArrayList arrayList3 = null;
            if (ChatObject.isChannel(this.currentChat)) {
                TLRPC$ChannelParticipant tLRPC$ChannelParticipant2 = ((TLRPC$TL_chatChannelParticipant) tLRPC$ChatParticipant).channelParticipant;
                MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(tLRPC$ChatParticipant.user_id));
                z6 = ChatObject.canAddAdmins(this.currentChat);
                if (z6 && ((tLRPC$ChannelParticipant2 instanceof TLRPC$TL_channelParticipantCreator) || ((tLRPC$ChannelParticipant2 instanceof TLRPC$TL_channelParticipantAdmin) && !tLRPC$ChannelParticipant2.can_edit))) {
                    z6 = false;
                }
                z5 = ChatObject.canBlockUsers(this.currentChat) && ((!(tLRPC$ChannelParticipant2 instanceof TLRPC$TL_channelParticipantAdmin) && !(tLRPC$ChannelParticipant2 instanceof TLRPC$TL_channelParticipantCreator)) || tLRPC$ChannelParticipant2.can_edit);
                z4 = tLRPC$ChannelParticipant2 instanceof TLRPC$TL_channelParticipantAdmin;
                tLRPC$ChannelParticipant = tLRPC$ChannelParticipant2;
                z3 = z5;
            } else {
                TLRPC$Chat tLRPC$Chat = this.currentChat;
                z5 = tLRPC$Chat.creator || ((tLRPC$ChatParticipant instanceof TLRPC$TL_chatParticipant) && (ChatObject.canBlockUsers(tLRPC$Chat) || tLRPC$ChatParticipant.inviter_id == UserConfig.getInstance(this.currentAccount).getClientUserId()));
                z6 = this.currentChat.creator;
                z4 = tLRPC$ChatParticipant instanceof TLRPC$TL_chatParticipantAdmin;
                tLRPC$ChannelParticipant = null;
                z3 = z6;
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
            if (z6) {
                if (z2) {
                    return true;
                }
                if (z4) {
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
            if (z3) {
                if (z2) {
                    return true;
                }
                arrayList.add(LocaleController.getString("ChangePermissions", NUM));
                arrayList2.add(NUM);
                arrayList3.add(1);
            }
            if (!z5) {
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
            builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[0]), AndroidUtilities.toIntArray(arrayList2), new DialogInterface.OnClickListener(arrayList3, tLRPC$ChannelParticipant, tLRPC$ChatParticipant, user) {
                private final /* synthetic */ ArrayList f$1;
                private final /* synthetic */ TLRPC$ChannelParticipant f$2;
                private final /* synthetic */ TLRPC$ChatParticipant f$3;
                private final /* synthetic */ TLRPC$User f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    ProfileActivity.this.lambda$onMemberClick$12$ProfileActivity(this.f$1, this.f$2, this.f$3, this.f$4, dialogInterface, i);
                }
            });
            AlertDialog create = builder.create();
            showDialog(create);
            if (z7) {
                create.setItemColor(arrayList.size() - 1, Theme.getColor("dialogTextRed2"), Theme.getColor("dialogRedIcon"));
            }
        } else if (tLRPC$ChatParticipant.user_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
            return false;
        } else {
            Bundle bundle = new Bundle();
            bundle.putInt("user_id", tLRPC$ChatParticipant.user_id);
            presentFragment(new ProfileActivity(bundle));
        }
        return true;
    }

    public /* synthetic */ void lambda$onMemberClick$12$ProfileActivity(ArrayList arrayList, TLRPC$ChannelParticipant tLRPC$ChannelParticipant, TLRPC$ChatParticipant tLRPC$ChatParticipant, TLRPC$User tLRPC$User, DialogInterface dialogInterface, int i) {
        if (((Integer) arrayList.get(i)).intValue() == 2) {
            kickUser(this.selectedUser);
            return;
        }
        int intValue = ((Integer) arrayList.get(i)).intValue();
        if (intValue == 1 && ((tLRPC$ChannelParticipant instanceof TLRPC$TL_channelParticipantAdmin) || (tLRPC$ChatParticipant instanceof TLRPC$TL_chatParticipantAdmin))) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setMessage(LocaleController.formatString("AdminWillBeRemoved", NUM, ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name)));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener(tLRPC$ChannelParticipant, intValue, tLRPC$User, tLRPC$ChatParticipant) {
                private final /* synthetic */ TLRPC$ChannelParticipant f$1;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ TLRPC$User f$3;
                private final /* synthetic */ TLRPC$ChatParticipant f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    ProfileActivity.this.lambda$null$11$ProfileActivity(this.f$1, this.f$2, this.f$3, this.f$4, dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        } else if (tLRPC$ChannelParticipant != null) {
            openRightsEdit(intValue, tLRPC$User.id, tLRPC$ChatParticipant, tLRPC$ChannelParticipant.admin_rights, tLRPC$ChannelParticipant.banned_rights, tLRPC$ChannelParticipant.rank);
        } else {
            openRightsEdit(intValue, tLRPC$User.id, tLRPC$ChatParticipant, (TLRPC$TL_chatAdminRights) null, (TLRPC$TL_chatBannedRights) null, "");
        }
    }

    public /* synthetic */ void lambda$null$11$ProfileActivity(TLRPC$ChannelParticipant tLRPC$ChannelParticipant, int i, TLRPC$User tLRPC$User, TLRPC$ChatParticipant tLRPC$ChatParticipant, DialogInterface dialogInterface, int i2) {
        TLRPC$ChannelParticipant tLRPC$ChannelParticipant2 = tLRPC$ChannelParticipant;
        TLRPC$User tLRPC$User2 = tLRPC$User;
        if (tLRPC$ChannelParticipant2 != null) {
            openRightsEdit(i, tLRPC$User2.id, tLRPC$ChatParticipant, tLRPC$ChannelParticipant2.admin_rights, tLRPC$ChannelParticipant2.banned_rights, tLRPC$ChannelParticipant2.rank);
            return;
        }
        openRightsEdit(i, tLRPC$User2.id, tLRPC$ChatParticipant, (TLRPC$TL_chatAdminRights) null, (TLRPC$TL_chatBannedRights) null, "");
    }

    private void openRightsEdit(final int i, int i2, final TLRPC$ChatParticipant tLRPC$ChatParticipant, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, String str) {
        ChatRightsEditActivity chatRightsEditActivity = new ChatRightsEditActivity(i2, this.chat_id, tLRPC$TL_chatAdminRights, this.currentChat.default_banned_rights, tLRPC$TL_chatBannedRights, str, i, true, false);
        int i3 = i;
        TLRPC$ChatParticipant tLRPC$ChatParticipant2 = tLRPC$ChatParticipant;
        chatRightsEditActivity.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() {
            public void didSetRights(int i, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, String str) {
                boolean z;
                TLRPC$ChatParticipant tLRPC$ChatParticipant;
                int i2 = i;
                boolean z2 = true;
                if (i2 == 0) {
                    TLRPC$ChatParticipant tLRPC$ChatParticipant2 = tLRPC$ChatParticipant;
                    if (tLRPC$ChatParticipant2 instanceof TLRPC$TL_chatChannelParticipant) {
                        TLRPC$TL_chatChannelParticipant tLRPC$TL_chatChannelParticipant = (TLRPC$TL_chatChannelParticipant) tLRPC$ChatParticipant2;
                        if (i == 1) {
                            TLRPC$TL_channelParticipantAdmin tLRPC$TL_channelParticipantAdmin = new TLRPC$TL_channelParticipantAdmin();
                            tLRPC$TL_chatChannelParticipant.channelParticipant = tLRPC$TL_channelParticipantAdmin;
                            tLRPC$TL_channelParticipantAdmin.flags |= 4;
                        } else {
                            tLRPC$TL_chatChannelParticipant.channelParticipant = new TLRPC$TL_channelParticipant();
                        }
                        tLRPC$TL_chatChannelParticipant.channelParticipant.inviter_id = UserConfig.getInstance(ProfileActivity.this.currentAccount).getClientUserId();
                        TLRPC$ChannelParticipant tLRPC$ChannelParticipant = tLRPC$TL_chatChannelParticipant.channelParticipant;
                        TLRPC$ChatParticipant tLRPC$ChatParticipant3 = tLRPC$ChatParticipant;
                        tLRPC$ChannelParticipant.user_id = tLRPC$ChatParticipant3.user_id;
                        tLRPC$ChannelParticipant.date = tLRPC$ChatParticipant3.date;
                        tLRPC$ChannelParticipant.banned_rights = tLRPC$TL_chatBannedRights;
                        tLRPC$ChannelParticipant.admin_rights = tLRPC$TL_chatAdminRights;
                        tLRPC$ChannelParticipant.rank = str;
                    } else if (tLRPC$ChatParticipant2 instanceof TLRPC$ChatParticipant) {
                        if (i == 1) {
                            tLRPC$ChatParticipant = new TLRPC$TL_chatParticipantAdmin();
                        } else {
                            tLRPC$ChatParticipant = new TLRPC$TL_chatParticipant();
                        }
                        TLRPC$ChatParticipant tLRPC$ChatParticipant4 = tLRPC$ChatParticipant;
                        tLRPC$ChatParticipant.user_id = tLRPC$ChatParticipant4.user_id;
                        tLRPC$ChatParticipant.date = tLRPC$ChatParticipant4.date;
                        tLRPC$ChatParticipant.inviter_id = tLRPC$ChatParticipant4.inviter_id;
                        int indexOf = ProfileActivity.this.chatInfo.participants.participants.indexOf(tLRPC$ChatParticipant);
                        if (indexOf >= 0) {
                            ProfileActivity.this.chatInfo.participants.participants.set(indexOf, tLRPC$ChatParticipant);
                        }
                    }
                } else if (i2 == 1 && i == 0 && ProfileActivity.this.currentChat.megagroup && ProfileActivity.this.chatInfo != null && ProfileActivity.this.chatInfo.participants != null) {
                    int i3 = 0;
                    int i4 = 0;
                    while (true) {
                        if (i4 >= ProfileActivity.this.chatInfo.participants.participants.size()) {
                            z = false;
                            break;
                        } else if (((TLRPC$TL_chatChannelParticipant) ProfileActivity.this.chatInfo.participants.participants.get(i4)).channelParticipant.user_id == tLRPC$ChatParticipant.user_id) {
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
                            } else if (ProfileActivity.this.chatInfo.participants.participants.get(i3).user_id == tLRPC$ChatParticipant.user_id) {
                                ProfileActivity.this.chatInfo.participants.participants.remove(i3);
                                break;
                            } else {
                                i3++;
                            }
                        }
                    }
                    z2 = z;
                    if (z2) {
                        ProfileActivity.this.updateOnlineCount();
                        ProfileActivity.this.updateRowsIds();
                        ProfileActivity.this.listAdapter.notifyDataSetChanged();
                    }
                }
            }

            public void didChangeOwner(TLRPC$User tLRPC$User) {
                ProfileActivity.this.undoView.showWithAction((long) (-ProfileActivity.this.chat_id), ProfileActivity.this.currentChat.megagroup ? 10 : 9, (Object) tLRPC$User);
            }
        });
        presentFragment(chatRightsEditActivity);
    }

    private boolean processOnClickOrPress(int i) {
        String str;
        String str2;
        TLRPC$Chat chat;
        if (i == this.usernameRow) {
            if (this.user_id != 0) {
                TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
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
                    ProfileActivity.this.lambda$processOnClickOrPress$13$ProfileActivity(this.f$1, dialogInterface, i);
                }
            });
            showDialog(builder.create());
            return true;
        } else if (i == this.phoneRow) {
            TLRPC$User user2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
            if (user2 == null || (str = user2.phone) == null || str.length() == 0 || getParentActivity() == null) {
                return false;
            }
            AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            TLRPC$UserFull tLRPC$UserFull = this.userInfo;
            if (tLRPC$UserFull != null && tLRPC$UserFull.phone_calls_available) {
                arrayList.add(LocaleController.getString("CallViaTelegram", NUM));
                arrayList2.add(2);
            }
            arrayList.add(LocaleController.getString("Call", NUM));
            arrayList2.add(0);
            arrayList.add(LocaleController.getString("Copy", NUM));
            arrayList2.add(1);
            builder2.setItems((CharSequence[]) arrayList.toArray(new CharSequence[0]), new DialogInterface.OnClickListener(arrayList2, user2) {
                private final /* synthetic */ ArrayList f$1;
                private final /* synthetic */ TLRPC$User f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    ProfileActivity.this.lambda$processOnClickOrPress$14$ProfileActivity(this.f$1, this.f$2, dialogInterface, i);
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
                    ProfileActivity.this.lambda$processOnClickOrPress$15$ProfileActivity(this.f$1, dialogInterface, i);
                }
            });
            showDialog(builder3.create());
            return true;
        }
    }

    public /* synthetic */ void lambda$processOnClickOrPress$13$ProfileActivity(String str, DialogInterface dialogInterface, int i) {
        if (i == 0) {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", "@" + str));
                Toast.makeText(getParentActivity(), LocaleController.getString("TextCopied", NUM), 0).show();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public /* synthetic */ void lambda$processOnClickOrPress$14$ProfileActivity(ArrayList arrayList, TLRPC$User tLRPC$User, DialogInterface dialogInterface, int i) {
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
                Toast.makeText(getParentActivity(), LocaleController.getString("PhoneCopied", NUM), 0).show();
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        } else if (intValue == 2) {
            VoIPHelper.startCall(tLRPC$User, getParentActivity(), this.userInfo);
        }
    }

    public /* synthetic */ void lambda$processOnClickOrPress$15$ProfileActivity(int i, DialogInterface dialogInterface, int i2) {
        try {
            String str = null;
            if (i == this.locationRow) {
                if (this.chatInfo != null && (this.chatInfo.location instanceof TLRPC$TL_channelLocation)) {
                    str = ((TLRPC$TL_channelLocation) this.chatInfo.location).address;
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
        AlertsCreator.createClearOrDeleteDialogAlert(this, false, this.currentChat, (TLRPC$User) null, false, new MessagesStorage.BooleanCallback() {
            public final void run(boolean z) {
                ProfileActivity.this.lambda$leaveChatPressed$16$ProfileActivity(z);
            }
        });
    }

    public /* synthetic */ void lambda$leaveChatPressed$16$ProfileActivity(boolean z) {
        this.playProfileAnimation = 0;
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
        finishFragment();
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.needDeleteDialog, Long.valueOf((long) (-this.currentChat.id)), null, this.currentChat, Boolean.valueOf(z));
    }

    /* access modifiers changed from: private */
    public void getChannelParticipants(boolean z) {
        SparseArray<TLRPC$ChatParticipant> sparseArray;
        if (!this.loadingUsers && (sparseArray = this.participantsMap) != null && this.chatInfo != null) {
            this.loadingUsers = true;
            int i = 0;
            int i2 = (sparseArray.size() == 0 || !z) ? 0 : 300;
            TLRPC$TL_channels_getParticipants tLRPC$TL_channels_getParticipants = new TLRPC$TL_channels_getParticipants();
            tLRPC$TL_channels_getParticipants.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chat_id);
            tLRPC$TL_channels_getParticipants.filter = new TLRPC$TL_channelParticipantsRecent();
            if (!z) {
                i = this.participantsMap.size();
            }
            tLRPC$TL_channels_getParticipants.offset = i;
            tLRPC$TL_channels_getParticipants.limit = 200;
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_channels_getParticipants, new RequestDelegate(tLRPC$TL_channels_getParticipants, i2) {
                private final /* synthetic */ TLRPC$TL_channels_getParticipants f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    ProfileActivity.this.lambda$getChannelParticipants$18$ProfileActivity(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                }
            }), this.classGuid);
        }
    }

    public /* synthetic */ void lambda$getChannelParticipants$18$ProfileActivity(TLRPC$TL_channels_getParticipants tLRPC$TL_channels_getParticipants, int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject, tLRPC$TL_channels_getParticipants) {
            private final /* synthetic */ TLRPC$TL_error f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ TLRPC$TL_channels_getParticipants f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ProfileActivity.this.lambda$null$17$ProfileActivity(this.f$1, this.f$2, this.f$3);
            }
        }, (long) i);
    }

    public /* synthetic */ void lambda$null$17$ProfileActivity(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, TLRPC$TL_channels_getParticipants tLRPC$TL_channels_getParticipants) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_channels_channelParticipants tLRPC$TL_channels_channelParticipants = (TLRPC$TL_channels_channelParticipants) tLObject;
            MessagesController.getInstance(this.currentAccount).putUsers(tLRPC$TL_channels_channelParticipants.users, false);
            if (tLRPC$TL_channels_channelParticipants.users.size() < 200) {
                this.usersEndReached = true;
            }
            if (tLRPC$TL_channels_getParticipants.offset == 0) {
                this.participantsMap.clear();
                this.chatInfo.participants = new TLRPC$TL_chatParticipants();
                MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(tLRPC$TL_channels_channelParticipants.users, (ArrayList<TLRPC$Chat>) null, true, true);
                MessagesStorage.getInstance(this.currentAccount).updateChannelUsers(this.chat_id, tLRPC$TL_channels_channelParticipants.participants);
            }
            for (int i = 0; i < tLRPC$TL_channels_channelParticipants.participants.size(); i++) {
                TLRPC$TL_chatChannelParticipant tLRPC$TL_chatChannelParticipant = new TLRPC$TL_chatChannelParticipant();
                TLRPC$ChannelParticipant tLRPC$ChannelParticipant = tLRPC$TL_channels_channelParticipants.participants.get(i);
                tLRPC$TL_chatChannelParticipant.channelParticipant = tLRPC$ChannelParticipant;
                tLRPC$TL_chatChannelParticipant.inviter_id = tLRPC$ChannelParticipant.inviter_id;
                int i2 = tLRPC$ChannelParticipant.user_id;
                tLRPC$TL_chatChannelParticipant.user_id = i2;
                tLRPC$TL_chatChannelParticipant.date = tLRPC$ChannelParticipant.date;
                if (this.participantsMap.indexOfKey(i2) < 0) {
                    this.chatInfo.participants.participants.add(tLRPC$TL_chatChannelParticipant);
                    this.participantsMap.put(tLRPC$TL_chatChannelParticipant.user_id, tLRPC$TL_chatChannelParticipant);
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
            final ActionBarMenuItem searchItem = this.sharedMediaLayout.getSearchItem();
            if (!this.mediaHeaderVisible) {
                if (this.callItemVisible) {
                    this.callItem.setVisibility(0);
                }
                if (this.editItemVisible) {
                    this.editItem.setVisibility(0);
                }
                this.otherItem.setVisibility(0);
            } else if (this.sharedMediaLayout.isSearchItemVisible()) {
                searchItem.setVisibility(0);
            }
            ArrayList arrayList = new ArrayList();
            ActionBarMenuItem actionBarMenuItem = this.callItem;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            float f = 1.0f;
            fArr[0] = z ? 0.0f : 1.0f;
            arrayList.add(ObjectAnimator.ofFloat(actionBarMenuItem, property, fArr));
            ActionBarMenuItem actionBarMenuItem2 = this.otherItem;
            Property property2 = View.ALPHA;
            float[] fArr2 = new float[1];
            fArr2[0] = z ? 0.0f : 1.0f;
            arrayList.add(ObjectAnimator.ofFloat(actionBarMenuItem2, property2, fArr2));
            ActionBarMenuItem actionBarMenuItem3 = this.editItem;
            Property property3 = View.ALPHA;
            float[] fArr3 = new float[1];
            fArr3[0] = z ? 0.0f : 1.0f;
            arrayList.add(ObjectAnimator.ofFloat(actionBarMenuItem3, property3, fArr3));
            ActionBarMenuItem actionBarMenuItem4 = this.callItem;
            Property property4 = View.TRANSLATION_Y;
            float[] fArr4 = new float[1];
            fArr4[0] = z ? (float) (-AndroidUtilities.dp(10.0f)) : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(actionBarMenuItem4, property4, fArr4));
            ActionBarMenuItem actionBarMenuItem5 = this.otherItem;
            Property property5 = View.TRANSLATION_Y;
            float[] fArr5 = new float[1];
            fArr5[0] = z ? (float) (-AndroidUtilities.dp(10.0f)) : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(actionBarMenuItem5, property5, fArr5));
            ActionBarMenuItem actionBarMenuItem6 = this.editItem;
            Property property6 = View.TRANSLATION_Y;
            float[] fArr6 = new float[1];
            fArr6[0] = z ? (float) (-AndroidUtilities.dp(10.0f)) : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(actionBarMenuItem6, property6, fArr6));
            Property property7 = View.ALPHA;
            float[] fArr7 = new float[1];
            fArr7[0] = z ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(searchItem, property7, fArr7));
            Property property8 = View.TRANSLATION_Y;
            float[] fArr8 = new float[1];
            fArr8[0] = z ? 0.0f : (float) AndroidUtilities.dp(10.0f);
            arrayList.add(ObjectAnimator.ofFloat(searchItem, property8, fArr8));
            ActionBar actionBar = this.actionBar;
            Property<ActionBar, Float> property9 = this.ACTIONBAR_HEADER_PROGRESS;
            float[] fArr9 = new float[1];
            fArr9[0] = z ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(actionBar, property9, fArr9));
            SimpleTextView simpleTextView = this.onlineTextView[1];
            Property property10 = View.ALPHA;
            float[] fArr10 = new float[1];
            fArr10[0] = z ? 0.0f : 1.0f;
            arrayList.add(ObjectAnimator.ofFloat(simpleTextView, property10, fArr10));
            SimpleTextView simpleTextView2 = this.onlineTextView[2];
            Property property11 = View.ALPHA;
            float[] fArr11 = new float[1];
            if (!z) {
                f = 0.0f;
            }
            fArr11[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(simpleTextView2, property11, fArr11));
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
                            if (ProfileActivity.this.editItemVisible) {
                                ProfileActivity.this.editItem.setVisibility(4);
                            }
                            ProfileActivity.this.otherItem.setVisibility(4);
                        } else {
                            if (ProfileActivity.this.sharedMediaLayout.isSearchItemVisible()) {
                                searchItem.setVisibility(0);
                            }
                            AnimatorSet unused = ProfileActivity.this.headerShadowAnimatorSet = new AnimatorSet();
                            AnimatorSet access$11400 = ProfileActivity.this.headerShadowAnimatorSet;
                            ProfileActivity profileActivity = ProfileActivity.this;
                            access$11400.playTogether(new Animator[]{ObjectAnimator.ofFloat(profileActivity, profileActivity.HEADER_SHADOW, new float[]{1.0f})});
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
                ProfileActivity.this.lambda$openAddMember$19$ProfileActivity(arrayList, i);
            }

            public /* synthetic */ void needAddBot(TLRPC$User tLRPC$User) {
                GroupCreateActivity.ContactsAddActivityDelegate.CC.$default$needAddBot(this, tLRPC$User);
            }
        });
        presentFragment(groupCreateActivity);
    }

    public /* synthetic */ void lambda$openAddMember$19$ProfileActivity(ArrayList arrayList, int i) {
        int size = arrayList.size();
        for (int i2 = 0; i2 < size; i2++) {
            MessagesController.getInstance(this.currentAccount).addUserToChat(this.chat_id, (TLRPC$User) arrayList.get(i2), this.chatInfo, i, (String) null, this, (Runnable) null);
        }
    }

    /* access modifiers changed from: private */
    public void checkListViewScroll() {
        if (this.listView.getChildCount() > 0 && !this.openAnimationInProgress) {
            boolean z = false;
            View childAt = this.listView.getChildAt(0);
            RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(childAt);
            int top = childAt.getTop();
            int adapterPosition = holder != null ? holder.getAdapterPosition() : -1;
            if (top < 0 || adapterPosition != 0) {
                top = 0;
            }
            boolean isSearchFieldVisible = this.actionBar.isSearchFieldVisible();
            int i = this.sharedMediaRow;
            if (i != -1 && !isSearchFieldVisible) {
                RecyclerListView.Holder holder2 = (RecyclerListView.Holder) this.listView.findViewHolderForAdapterPosition(i);
                isSearchFieldVisible = holder2 != null && holder2.itemView.getTop() <= 0;
            }
            setMediaHeaderVisible(isSearchFieldVisible);
            float f = (float) top;
            if (this.extraHeight != f) {
                this.extraHeight = f;
                this.topView.invalidate();
                if (this.playProfileAnimation != 0) {
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
    public void updateSelectedMediaTabText() {
        SharedMediaLayout sharedMediaLayout2 = this.sharedMediaLayout;
        if (sharedMediaLayout2 != null && this.onlineTextView[2] != null) {
            int selectedTab = sharedMediaLayout2.getSelectedTab();
            int[] lastMediaCount = this.sharedMediaPreloader.getLastMediaCount();
            if (selectedTab == 0) {
                this.onlineTextView[2].setText(LocaleController.formatPluralString("Media", lastMediaCount[0]));
            } else if (selectedTab == 1) {
                this.onlineTextView[2].setText(LocaleController.formatPluralString("Files", lastMediaCount[1]));
            } else if (selectedTab == 2) {
                this.onlineTextView[2].setText(LocaleController.formatPluralString("Voice", lastMediaCount[2]));
            } else if (selectedTab == 3) {
                this.onlineTextView[2].setText(LocaleController.formatPluralString("Links", lastMediaCount[3]));
            } else if (selectedTab == 4) {
                this.onlineTextView[2].setText(LocaleController.formatPluralString("MusicFiles", lastMediaCount[4]));
            } else if (selectedTab == 5) {
                this.onlineTextView[2].setText(LocaleController.formatPluralString("GIFs", lastMediaCount[5]));
            } else if (selectedTab == 6) {
                this.onlineTextView[2].setText(LocaleController.formatPluralString("CommonGroups", this.userInfo.common_chats_count));
            } else if (selectedTab == 7) {
                SimpleTextView[] simpleTextViewArr = this.onlineTextView;
                simpleTextViewArr[2].setText(simpleTextViewArr[1].getText());
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x03e5  */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x053f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void needLayout() {
        /*
            r18 = this;
            r0 = r18
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            boolean r1 = r1.getOccupyStatusBar()
            r2 = 0
            if (r1 == 0) goto L_0x000e
            int r1 = org.telegram.messenger.AndroidUtilities.statusBarHeight
            goto L_0x000f
        L_0x000e:
            r1 = 0
        L_0x000f:
            int r3 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight()
            int r1 = r1 + r3
            org.telegram.ui.Components.RecyclerListView r3 = r0.listView
            if (r3 == 0) goto L_0x002d
            boolean r4 = r0.openAnimationInProgress
            if (r4 != 0) goto L_0x002d
            android.view.ViewGroup$LayoutParams r3 = r3.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r3 = (android.widget.FrameLayout.LayoutParams) r3
            int r4 = r3.topMargin
            if (r4 == r1) goto L_0x002d
            r3.topMargin = r1
            org.telegram.ui.Components.RecyclerListView r4 = r0.listView
            r4.setLayoutParams(r3)
        L_0x002d:
            org.telegram.ui.ProfileActivity$AvatarImageView r3 = r0.avatarImage
            if (r3 == 0) goto L_0x0644
            float r3 = r0.extraHeight
            r4 = 1118830592(0x42b00000, float:88.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r5 = (float) r5
            float r3 = r3 / r5
            r5 = 1065353216(0x3var_, float:1.0)
            float r3 = java.lang.Math.min(r5, r3)
            org.telegram.ui.Components.RecyclerListView r6 = r0.listView
            float r7 = r0.extraHeight
            int r7 = (int) r7
            r6.setTopGlowOffset(r7)
            org.telegram.ui.Components.RecyclerListView r6 = r0.listView
            float r7 = r0.extraHeight
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r8 = (float) r8
            r9 = 2
            int r7 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1))
            if (r7 <= 0) goto L_0x0067
            float r7 = r0.extraHeight
            org.telegram.ui.Components.RecyclerListView r8 = r0.listView
            int r8 = r8.getMeasuredWidth()
            int r8 = r8 - r1
            float r8 = (float) r8
            int r7 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1))
            if (r7 >= 0) goto L_0x0067
            r7 = 2
            goto L_0x0068
        L_0x0067:
            r7 = 0
        L_0x0068:
            r6.setOverScrollMode(r7)
            android.widget.ImageView r6 = r0.writeButton
            r7 = 0
            r8 = 1
            if (r6 == 0) goto L_0x015b
            org.telegram.ui.ActionBar.ActionBar r10 = r0.actionBar
            boolean r10 = r10.getOccupyStatusBar()
            if (r10 == 0) goto L_0x007c
            int r10 = org.telegram.messenger.AndroidUtilities.statusBarHeight
            goto L_0x007d
        L_0x007c:
            r10 = 0
        L_0x007d:
            int r11 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight()
            int r10 = r10 + r11
            float r10 = (float) r10
            float r11 = r0.extraHeight
            float r10 = r10 + r11
            r11 = 1105985536(0x41eCLASSNAME, float:29.5)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r11 = (float) r11
            float r10 = r10 - r11
            r6.setTranslationY(r10)
            boolean r6 = r0.openAnimationInProgress
            if (r6 != 0) goto L_0x015b
            r6 = 1045220557(0x3e4ccccd, float:0.2)
            int r10 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1))
            if (r10 <= 0) goto L_0x009e
            r10 = 1
            goto L_0x009f
        L_0x009e:
            r10 = 0
        L_0x009f:
            android.widget.ImageView r11 = r0.writeButton
            java.lang.Object r11 = r11.getTag()
            if (r11 != 0) goto L_0x00a9
            r11 = 1
            goto L_0x00aa
        L_0x00a9:
            r11 = 0
        L_0x00aa:
            if (r10 == r11) goto L_0x015b
            r11 = 0
            if (r10 == 0) goto L_0x00b5
            android.widget.ImageView r12 = r0.writeButton
            r12.setTag(r11)
            goto L_0x00be
        L_0x00b5:
            android.widget.ImageView r12 = r0.writeButton
            java.lang.Integer r13 = java.lang.Integer.valueOf(r2)
            r12.setTag(r13)
        L_0x00be:
            android.animation.AnimatorSet r12 = r0.writeButtonAnimation
            if (r12 == 0) goto L_0x00c7
            r0.writeButtonAnimation = r11
            r12.cancel()
        L_0x00c7:
            android.animation.AnimatorSet r11 = new android.animation.AnimatorSet
            r11.<init>()
            r0.writeButtonAnimation = r11
            if (r10 == 0) goto L_0x010b
            android.view.animation.DecelerateInterpolator r6 = new android.view.animation.DecelerateInterpolator
            r6.<init>()
            r11.setInterpolator(r6)
            android.animation.AnimatorSet r6 = r0.writeButtonAnimation
            r10 = 3
            android.animation.Animator[] r10 = new android.animation.Animator[r10]
            android.widget.ImageView r11 = r0.writeButton
            android.util.Property r12 = android.view.View.SCALE_X
            float[] r13 = new float[r8]
            r13[r2] = r5
            android.animation.ObjectAnimator r11 = android.animation.ObjectAnimator.ofFloat(r11, r12, r13)
            r10[r2] = r11
            android.widget.ImageView r11 = r0.writeButton
            android.util.Property r12 = android.view.View.SCALE_Y
            float[] r13 = new float[r8]
            r13[r2] = r5
            android.animation.ObjectAnimator r11 = android.animation.ObjectAnimator.ofFloat(r11, r12, r13)
            r10[r8] = r11
            android.widget.ImageView r11 = r0.writeButton
            android.util.Property r12 = android.view.View.ALPHA
            float[] r13 = new float[r8]
            r13[r2] = r5
            android.animation.ObjectAnimator r11 = android.animation.ObjectAnimator.ofFloat(r11, r12, r13)
            r10[r9] = r11
            r6.playTogether(r10)
            goto L_0x0145
        L_0x010b:
            android.view.animation.AccelerateInterpolator r10 = new android.view.animation.AccelerateInterpolator
            r10.<init>()
            r11.setInterpolator(r10)
            android.animation.AnimatorSet r10 = r0.writeButtonAnimation
            r11 = 3
            android.animation.Animator[] r11 = new android.animation.Animator[r11]
            android.widget.ImageView r12 = r0.writeButton
            android.util.Property r13 = android.view.View.SCALE_X
            float[] r14 = new float[r8]
            r14[r2] = r6
            android.animation.ObjectAnimator r12 = android.animation.ObjectAnimator.ofFloat(r12, r13, r14)
            r11[r2] = r12
            android.widget.ImageView r12 = r0.writeButton
            android.util.Property r13 = android.view.View.SCALE_Y
            float[] r14 = new float[r8]
            r14[r2] = r6
            android.animation.ObjectAnimator r6 = android.animation.ObjectAnimator.ofFloat(r12, r13, r14)
            r11[r8] = r6
            android.widget.ImageView r6 = r0.writeButton
            android.util.Property r12 = android.view.View.ALPHA
            float[] r13 = new float[r8]
            r13[r2] = r7
            android.animation.ObjectAnimator r6 = android.animation.ObjectAnimator.ofFloat(r6, r12, r13)
            r11[r9] = r6
            r10.playTogether(r11)
        L_0x0145:
            android.animation.AnimatorSet r6 = r0.writeButtonAnimation
            r10 = 150(0x96, double:7.4E-322)
            r6.setDuration(r10)
            android.animation.AnimatorSet r6 = r0.writeButtonAnimation
            org.telegram.ui.ProfileActivity$17 r10 = new org.telegram.ui.ProfileActivity$17
            r10.<init>()
            r6.addListener(r10)
            android.animation.AnimatorSet r6 = r0.writeButtonAnimation
            r6.start()
        L_0x015b:
            r6 = 1111228416(0x423CLASSNAME, float:47.0)
            float r6 = org.telegram.messenger.AndroidUtilities.dpf2(r6)
            float r6 = -r6
            float r6 = r6 * r3
            r0.avatarX = r6
            org.telegram.ui.ActionBar.ActionBar r6 = r0.actionBar
            boolean r6 = r6.getOccupyStatusBar()
            if (r6 == 0) goto L_0x0171
            int r6 = org.telegram.messenger.AndroidUtilities.statusBarHeight
            goto L_0x0172
        L_0x0171:
            r6 = 0
        L_0x0172:
            float r6 = (float) r6
            int r10 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight()
            float r10 = (float) r10
            r11 = 1073741824(0x40000000, float:2.0)
            float r10 = r10 / r11
            float r12 = r3 + r5
            float r10 = r10 * r12
            float r6 = r6 + r10
            float r10 = org.telegram.messenger.AndroidUtilities.density
            r12 = 1101529088(0x41a80000, float:21.0)
            float r13 = r10 * r12
            float r6 = r6 - r13
            r13 = 1104674816(0x41d80000, float:27.0)
            float r10 = r10 * r13
            float r10 = r10 * r3
            float r6 = r6 + r10
            org.telegram.ui.ActionBar.ActionBar r10 = r0.actionBar
            float r10 = r10.getTranslationY()
            float r6 = r6 + r10
            r0.avatarY = r6
            boolean r6 = r0.openAnimationInProgress
            if (r6 == 0) goto L_0x019e
            float r6 = r0.initialAnimationExtraHeight
            goto L_0x01a0
        L_0x019e:
            float r6 = r0.extraHeight
        L_0x01a0:
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r10 = (float) r10
            r13 = 1099956224(0x41900000, float:18.0)
            r14 = 1075539383(0x401b6db7, float:2.4285715)
            int r10 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1))
            if (r10 > 0) goto L_0x01b6
            boolean r10 = r0.isPulledDown
            if (r10 == 0) goto L_0x01b3
            goto L_0x01b6
        L_0x01b3:
            r15 = r3
            goto L_0x03d8
        L_0x01b6:
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r10 = (float) r10
            float r10 = r6 - r10
            org.telegram.ui.Components.RecyclerListView r15 = r0.listView
            int r15 = r15.getMeasuredWidth()
            int r15 = r15 - r1
            int r16 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r15 = r15 - r16
            float r15 = (float) r15
            float r10 = r10 / r15
            float r10 = java.lang.Math.min(r5, r10)
            float r10 = java.lang.Math.max(r7, r10)
            r0.expandProgress = r10
            r15 = 1068948334(0x3fb6db6e, float:1.4285715)
            r16 = 1077936128(0x40400000, float:3.0)
            float r10 = r10 * r16
            float r10 = java.lang.Math.min(r5, r10)
            float r10 = org.telegram.messenger.AndroidUtilities.lerp(r15, r14, r10)
            r0.avatarScale = r10
            r10 = 1157234688(0x44fa0000, float:2000.0)
            float r10 = org.telegram.messenger.AndroidUtilities.dpf2(r10)
            r15 = 1149861888(0x44898000, float:1100.0)
            float r4 = org.telegram.messenger.AndroidUtilities.dpf2(r15)
            float r14 = r0.listViewVelocityY
            float r14 = java.lang.Math.abs(r14)
            float r4 = java.lang.Math.max(r4, r14)
            float r4 = java.lang.Math.min(r10, r4)
            float r10 = org.telegram.messenger.AndroidUtilities.dpf2(r15)
            float r4 = r4 / r10
            boolean r10 = r0.openingAvatar
            r14 = 21
            r15 = 1132068864(0x437a0000, float:250.0)
            if (r10 != 0) goto L_0x02e2
            float r10 = r0.expandProgress
            r17 = 1051260355(0x3ea8f5c3, float:0.33)
            int r10 = (r10 > r17 ? 1 : (r10 == r17 ? 0 : -1))
            if (r10 < 0) goto L_0x021a
            goto L_0x02e2
        L_0x021a:
            boolean r6 = r0.isPulledDown
            if (r6 == 0) goto L_0x028f
            r0.isPulledDown = r2
            org.telegram.ui.ActionBar.ActionBarMenuItem r6 = r0.otherItem
            if (r6 == 0) goto L_0x0227
            r6.hideSubItem(r14)
        L_0x0227:
            org.telegram.ui.ProfileActivity$OverlaysView r6 = r0.overlaysView
            r6.setOverlaysVisible(r2, r4)
            org.telegram.ui.ProfileActivity$PagerIndicatorView r6 = r0.avatarsViewPagerIndicatorView
            r6.refreshVisibility(r4)
            android.animation.ValueAnimator r6 = r0.expandAnimator
            r6.cancel()
            float[] r6 = r0.expandAnimatorValues
            float r10 = r0.currentExpanAnimatorFracture
            float r6 = org.telegram.messenger.AndroidUtilities.lerp(r6, r10)
            float[] r10 = r0.expandAnimatorValues
            r10[r2] = r6
            r10[r8] = r7
            boolean r10 = r0.isInLandscapeMode
            if (r10 != 0) goto L_0x0252
            android.animation.ValueAnimator r10 = r0.expandAnimator
            float r6 = r6 * r15
            float r6 = r6 / r4
            long r14 = (long) r6
            r10.setDuration(r14)
            goto L_0x0259
        L_0x0252:
            android.animation.ValueAnimator r4 = r0.expandAnimator
            r14 = 0
            r4.setDuration(r14)
        L_0x0259:
            org.telegram.ui.ProfileActivity$TopView r4 = r0.topView
            java.lang.String r6 = "avatar_backgroundActionBarBlue"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r4.setBackgroundColor(r6)
            org.telegram.ui.Components.ProfileGalleryView r4 = r0.avatarsViewPager
            org.telegram.ui.Components.BackupImageView r4 = r4.getCurrentItemView()
            if (r4 == 0) goto L_0x0279
            org.telegram.ui.ProfileActivity$AvatarImageView r6 = r0.avatarImage
            org.telegram.messenger.ImageReceiver r4 = r4.getImageReceiver()
            android.graphics.drawable.Drawable r4 = r4.getDrawable()
            r6.setForegroundImageDrawable(r4)
        L_0x0279:
            org.telegram.ui.ProfileActivity$AvatarImageView r4 = r0.avatarImage
            r4.setForegroundAlpha(r5)
            org.telegram.ui.ProfileActivity$AvatarImageView r4 = r0.avatarImage
            r4.setVisibility(r2)
            org.telegram.ui.Components.ProfileGalleryView r4 = r0.avatarsViewPager
            r6 = 8
            r4.setVisibility(r6)
            android.animation.ValueAnimator r4 = r0.expandAnimator
            r4.start()
        L_0x028f:
            org.telegram.ui.ProfileActivity$AvatarImageView r4 = r0.avatarImage
            float r6 = r0.avatarScale
            r4.setScaleX(r6)
            org.telegram.ui.ProfileActivity$AvatarImageView r4 = r0.avatarImage
            float r6 = r0.avatarScale
            r4.setScaleY(r6)
            android.animation.ValueAnimator r4 = r0.expandAnimator
            if (r4 == 0) goto L_0x02a7
            boolean r4 = r4.isRunning()
            if (r4 != 0) goto L_0x01b3
        L_0x02a7:
            r18.refreshNameAndOnlineXY()
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r0.nameTextView
            r4 = r4[r8]
            float r6 = r0.nameX
            r4.setTranslationX(r6)
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r0.nameTextView
            r4 = r4[r8]
            float r6 = r0.nameY
            r4.setTranslationY(r6)
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r0.onlineTextView
            r4 = r4[r8]
            float r6 = r0.onlineX
            r4.setTranslationX(r6)
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r0.onlineTextView
            r4 = r4[r8]
            float r6 = r0.onlineY
            r4.setTranslationY(r6)
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r0.onlineTextView
            r4 = r4[r9]
            float r6 = r0.onlineX
            r4.setTranslationX(r6)
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r0.onlineTextView
            r4 = r4[r9]
            float r6 = r0.onlineY
            r4.setTranslationY(r6)
            goto L_0x01b3
        L_0x02e2:
            boolean r10 = r0.isPulledDown
            if (r10 != 0) goto L_0x0328
            org.telegram.ui.ActionBar.ActionBarMenuItem r10 = r0.otherItem
            if (r10 == 0) goto L_0x02ed
            r10.showSubItem(r14)
        L_0x02ed:
            r0.isPulledDown = r8
            org.telegram.ui.ProfileActivity$OverlaysView r10 = r0.overlaysView
            r10.setOverlaysVisible(r8, r4)
            org.telegram.ui.ProfileActivity$PagerIndicatorView r10 = r0.avatarsViewPagerIndicatorView
            r10.refreshVisibility(r4)
            android.animation.ValueAnimator r10 = r0.expandAnimator
            r10.cancel()
            float[] r10 = r0.expandAnimatorValues
            float r14 = r0.currentExpanAnimatorFracture
            float r10 = org.telegram.messenger.AndroidUtilities.lerp(r10, r14)
            float[] r14 = r0.expandAnimatorValues
            r14[r2] = r10
            r14[r8] = r5
            android.animation.ValueAnimator r14 = r0.expandAnimator
            float r10 = r5 - r10
            float r10 = r10 * r15
            float r10 = r10 / r4
            r15 = r3
            long r2 = (long) r10
            r14.setDuration(r2)
            android.animation.ValueAnimator r2 = r0.expandAnimator
            org.telegram.ui.ProfileActivity$18 r3 = new org.telegram.ui.ProfileActivity$18
            r3.<init>()
            r2.addListener(r3)
            android.animation.ValueAnimator r2 = r0.expandAnimator
            r2.start()
            goto L_0x0329
        L_0x0328:
            r15 = r3
        L_0x0329:
            org.telegram.ui.Components.ProfileGalleryView r2 = r0.avatarsViewPager
            android.view.ViewGroup$LayoutParams r2 = r2.getLayoutParams()
            org.telegram.ui.Components.RecyclerListView r3 = r0.listView
            int r3 = r3.getMeasuredWidth()
            r2.width = r3
            float r3 = (float) r1
            float r6 = r6 + r3
            int r3 = (int) r6
            r2.height = r3
            org.telegram.ui.Components.ProfileGalleryView r2 = r0.avatarsViewPager
            r2.requestLayout()
            android.animation.ValueAnimator r2 = r0.expandAnimator
            boolean r2 = r2.isRunning()
            if (r2 != 0) goto L_0x03d8
            boolean r2 = r0.openAnimationInProgress
            if (r2 == 0) goto L_0x0360
            int r2 = r0.playProfileAnimation
            if (r2 != r9) goto L_0x0360
            float r2 = r0.animationProgress
            float r2 = r5 - r2
            float r2 = -r2
            r3 = 1112014848(0x42480000, float:50.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r2 = r2 * r3
            goto L_0x0361
        L_0x0360:
            r2 = 0
        L_0x0361:
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r0.nameTextView
            r3 = r3[r8]
            r10 = 1098907648(0x41800000, float:16.0)
            float r14 = org.telegram.messenger.AndroidUtilities.dpf2(r10)
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r0.nameTextView
            r4 = r4[r8]
            int r4 = r4.getLeft()
            float r4 = (float) r4
            float r14 = r14 - r4
            r3.setTranslationX(r14)
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r0.nameTextView
            r3 = r3[r8]
            r4 = 1108869120(0x42180000, float:38.0)
            float r4 = org.telegram.messenger.AndroidUtilities.dpf2(r4)
            float r4 = r6 - r4
            org.telegram.ui.ActionBar.SimpleTextView[] r14 = r0.nameTextView
            r14 = r14[r8]
            int r14 = r14.getBottom()
            float r14 = (float) r14
            float r4 = r4 - r14
            float r4 = r4 + r2
            r3.setTranslationY(r4)
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r0.onlineTextView
            r3 = r3[r8]
            float r4 = org.telegram.messenger.AndroidUtilities.dpf2(r10)
            org.telegram.ui.ActionBar.SimpleTextView[] r10 = r0.onlineTextView
            r10 = r10[r8]
            int r10 = r10.getLeft()
            float r10 = (float) r10
            float r4 = r4 - r10
            r3.setTranslationX(r4)
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r0.onlineTextView
            r3 = r3[r8]
            float r4 = org.telegram.messenger.AndroidUtilities.dpf2(r13)
            float r6 = r6 - r4
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r0.onlineTextView
            r4 = r4[r8]
            int r4 = r4.getBottom()
            float r4 = (float) r4
            float r6 = r6 - r4
            float r6 = r6 + r2
            r3.setTranslationY(r6)
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r0.onlineTextView
            r3 = r2[r9]
            r2 = r2[r8]
            float r2 = r2.getTranslationX()
            r3.setTranslationX(r2)
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r0.onlineTextView
            r3 = r2[r9]
            r2 = r2[r8]
            float r2 = r2.getTranslationY()
            r3.setTranslationY(r2)
        L_0x03d8:
            boolean r2 = r0.openAnimationInProgress
            r3 = 1103101952(0x41CLASSNAME, float:24.0)
            r4 = 1067869798(0x3fa66666, float:1.3)
            if (r2 == 0) goto L_0x053f
            int r2 = r0.playProfileAnimation
            if (r2 != r9) goto L_0x053f
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            boolean r2 = r2.getOccupyStatusBar()
            if (r2 == 0) goto L_0x03f0
            int r2 = org.telegram.messenger.AndroidUtilities.statusBarHeight
            goto L_0x03f1
        L_0x03f0:
            r2 = 0
        L_0x03f1:
            float r2 = (float) r2
            int r9 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight()
            float r9 = (float) r9
            float r9 = r9 / r11
            float r2 = r2 + r9
            float r9 = org.telegram.messenger.AndroidUtilities.density
            float r9 = r9 * r12
            float r2 = r2 - r9
            org.telegram.ui.ActionBar.ActionBar r9 = r0.actionBar
            float r9 = r9.getTranslationY()
            float r2 = r2 + r9
            org.telegram.ui.ActionBar.SimpleTextView[] r9 = r0.nameTextView
            r10 = 0
            r9 = r9[r10]
            r9.setTranslationX(r7)
            org.telegram.ui.ActionBar.SimpleTextView[] r9 = r0.nameTextView
            r9 = r9[r10]
            double r13 = (double) r2
            double r5 = java.lang.Math.floor(r13)
            float r5 = (float) r5
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            float r5 = r5 + r4
            r9.setTranslationY(r5)
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r0.onlineTextView
            r4 = r4[r10]
            r4.setTranslationX(r7)
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r0.onlineTextView
            r4 = r4[r10]
            double r5 = java.lang.Math.floor(r13)
            float r5 = (float) r5
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r5 = r5 + r3
            r4.setTranslationY(r5)
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r0.nameTextView
            r3 = r3[r10]
            r2 = 1065353216(0x3var_, float:1.0)
            r3.setScaleX(r2)
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r0.nameTextView
            r3 = r3[r10]
            r3.setScaleY(r2)
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r0.nameTextView
            r5 = r3[r8]
            r3 = r3[r8]
            int r3 = r3.getMeasuredHeight()
            float r3 = (float) r3
            r5.setPivotY(r3)
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r0.nameTextView
            r3 = r3[r8]
            r5 = 1070973583(0x3fd5CLASSNAMEf, float:1.67)
            r3.setScaleX(r5)
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r0.nameTextView
            r3 = r3[r8]
            r3.setScaleY(r5)
            float r3 = r0.animationProgress
            r2 = 1075539383(0x401b6db7, float:2.4285715)
            r5 = 1065353216(0x3var_, float:1.0)
            float r2 = org.telegram.messenger.AndroidUtilities.lerp(r5, r2, r3)
            r0.avatarScale = r2
            org.telegram.ui.ProfileActivity$AvatarImageView r2 = r0.avatarImage
            float r3 = org.telegram.messenger.AndroidUtilities.dpf2(r12)
            float r5 = r0.animationProgress
            float r3 = org.telegram.messenger.AndroidUtilities.lerp(r3, r7, r5)
            int r3 = (int) r3
            r2.setRoundRadius(r3)
            org.telegram.ui.ProfileActivity$AvatarImageView r2 = r0.avatarImage
            float r3 = r0.animationProgress
            float r3 = org.telegram.messenger.AndroidUtilities.lerp(r7, r7, r3)
            r2.setTranslationX(r3)
            org.telegram.ui.ProfileActivity$AvatarImageView r2 = r0.avatarImage
            double r5 = java.lang.Math.ceil(r13)
            float r3 = (float) r5
            float r5 = r0.animationProgress
            float r3 = org.telegram.messenger.AndroidUtilities.lerp(r3, r7, r5)
            r2.setTranslationY(r3)
            org.telegram.ui.ProfileActivity$AvatarImageView r2 = r0.avatarImage
            float r3 = r0.avatarScale
            r2.setScaleX(r3)
            org.telegram.ui.ProfileActivity$AvatarImageView r2 = r0.avatarImage
            float r3 = r0.avatarScale
            r2.setScaleY(r3)
            org.telegram.ui.ProfileActivity$OverlaysView r2 = r0.overlaysView
            float r3 = r0.animationProgress
            r5 = 0
            r2.setAlphaValue(r3, r5)
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            java.lang.String r3 = "actionBarDefaultIcon"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r4 = -1
            float r6 = r0.animationProgress
            int r3 = androidx.core.graphics.ColorUtils.blendARGB(r3, r4, r6)
            r2.setItemsColor(r3, r5)
            org.telegram.ui.Components.ScamDrawable r2 = r0.scamDrawable
            if (r2 == 0) goto L_0x04e3
            java.lang.String r3 = "avatar_subtitleInProfileBlue"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r4 = 179(0xb3, float:2.51E-43)
            r5 = 255(0xff, float:3.57E-43)
            int r4 = android.graphics.Color.argb(r4, r5, r5, r5)
            float r5 = r0.animationProgress
            int r3 = androidx.core.graphics.ColorUtils.blendARGB(r3, r4, r5)
            r2.setColor(r3)
        L_0x04e3:
            android.graphics.drawable.Drawable r2 = r0.lockIconDrawable
            if (r2 == 0) goto L_0x04f9
            java.lang.String r3 = "chat_lockIcon"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r4 = -1
            float r5 = r0.animationProgress
            int r3 = androidx.core.graphics.ColorUtils.blendARGB(r3, r4, r5)
            android.graphics.PorterDuff$Mode r4 = android.graphics.PorterDuff.Mode.MULTIPLY
            r2.setColorFilter(r3, r4)
        L_0x04f9:
            org.telegram.ui.Components.CrossfadeDrawable r2 = r0.verifiedCrossfadeDrawable
            if (r2 == 0) goto L_0x0509
            float r3 = r0.animationProgress
            r2.setProgress(r3)
            org.telegram.ui.ActionBar.SimpleTextView[] r2 = r0.nameTextView
            r2 = r2[r8]
            r2.invalidate()
        L_0x0509:
            org.telegram.ui.ProfileActivity$AvatarImageView r2 = r0.avatarImage
            android.view.ViewGroup$LayoutParams r2 = r2.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r2 = (android.widget.FrameLayout.LayoutParams) r2
            r3 = 1109917696(0x42280000, float:42.0)
            float r3 = org.telegram.messenger.AndroidUtilities.dpf2(r3)
            float r4 = r0.extraHeight
            float r5 = (float) r1
            float r4 = r4 + r5
            float r5 = r0.avatarScale
            float r4 = r4 / r5
            float r5 = r0.animationProgress
            float r3 = org.telegram.messenger.AndroidUtilities.lerp(r3, r4, r5)
            int r3 = (int) r3
            r2.height = r3
            r2.width = r3
            r3 = 1115684864(0x42800000, float:64.0)
            float r3 = org.telegram.messenger.AndroidUtilities.dpf2(r3)
            float r4 = r0.animationProgress
            float r3 = org.telegram.messenger.AndroidUtilities.lerp(r3, r7, r4)
            int r3 = (int) r3
            r2.leftMargin = r3
            org.telegram.ui.ProfileActivity$AvatarImageView r2 = r0.avatarImage
            r2.requestLayout()
            goto L_0x0632
        L_0x053f:
            r5 = 0
            float r6 = r0.extraHeight
            r7 = 1118830592(0x42b00000, float:88.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            int r6 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1))
            if (r6 > 0) goto L_0x0632
            float r6 = r15 * r13
            r7 = 1109917696(0x42280000, float:42.0)
            float r6 = r6 + r7
            float r6 = r6 / r7
            r0.avatarScale = r6
            r6 = 1039516303(0x3df5CLASSNAMEf, float:0.12)
            float r6 = r6 * r15
            r2 = 1065353216(0x3var_, float:1.0)
            float r6 = r6 + r2
            android.animation.ValueAnimator r2 = r0.expandAnimator
            if (r2 == 0) goto L_0x0567
            boolean r2 = r2.isRunning()
            if (r2 != 0) goto L_0x0589
        L_0x0567:
            org.telegram.ui.ProfileActivity$AvatarImageView r2 = r0.avatarImage
            float r7 = r0.avatarScale
            r2.setScaleX(r7)
            org.telegram.ui.ProfileActivity$AvatarImageView r2 = r0.avatarImage
            float r7 = r0.avatarScale
            r2.setScaleY(r7)
            org.telegram.ui.ProfileActivity$AvatarImageView r2 = r0.avatarImage
            float r7 = r0.avatarX
            r2.setTranslationX(r7)
            org.telegram.ui.ProfileActivity$AvatarImageView r2 = r0.avatarImage
            float r7 = r0.avatarY
            double r10 = (double) r7
            double r10 = java.lang.Math.ceil(r10)
            float r7 = (float) r10
            r2.setTranslationY(r7)
        L_0x0589:
            float r2 = org.telegram.messenger.AndroidUtilities.density
            r7 = -1045954560(0xffffffffc1a80000, float:-21.0)
            float r2 = r2 * r7
            float r2 = r2 * r15
            r0.nameX = r2
            float r2 = r0.avatarY
            double r10 = (double) r2
            double r10 = java.lang.Math.floor(r10)
            float r2 = (float) r10
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            float r2 = r2 + r4
            r4 = 1088421888(0x40e00000, float:7.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            float r4 = r4 * r15
            float r2 = r2 + r4
            r0.nameY = r2
            float r2 = org.telegram.messenger.AndroidUtilities.density
            float r2 = r2 * r7
            float r2 = r2 * r15
            r0.onlineX = r2
            float r2 = r0.avatarY
            double r10 = (double) r2
            double r10 = java.lang.Math.floor(r10)
            float r2 = (float) r10
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r2 = r2 + r3
            r3 = 1093664768(0x41300000, float:11.0)
            float r4 = org.telegram.messenger.AndroidUtilities.density
            float r4 = r4 * r3
            double r3 = (double) r4
            double r3 = java.lang.Math.floor(r3)
            float r3 = (float) r3
            float r3 = r3 * r15
            float r2 = r2 + r3
            r0.onlineY = r2
            r2 = 0
        L_0x05d5:
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r0.nameTextView
            int r4 = r3.length
            if (r2 >= r4) goto L_0x0632
            r3 = r3[r2]
            if (r3 != 0) goto L_0x05df
            goto L_0x062f
        L_0x05df:
            android.animation.ValueAnimator r3 = r0.expandAnimator
            if (r3 == 0) goto L_0x05e9
            boolean r3 = r3.isRunning()
            if (r3 != 0) goto L_0x0621
        L_0x05e9:
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r0.nameTextView
            r3 = r3[r2]
            float r4 = r0.nameX
            r3.setTranslationX(r4)
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r0.nameTextView
            r3 = r3[r2]
            float r4 = r0.nameY
            r3.setTranslationY(r4)
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r0.onlineTextView
            r3 = r3[r2]
            float r4 = r0.onlineX
            r3.setTranslationX(r4)
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r0.onlineTextView
            r3 = r3[r2]
            float r4 = r0.onlineY
            r3.setTranslationY(r4)
            if (r2 != r8) goto L_0x0621
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r0.onlineTextView
            r3 = r3[r9]
            float r4 = r0.onlineX
            r3.setTranslationX(r4)
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r0.onlineTextView
            r3 = r3[r9]
            float r4 = r0.onlineY
            r3.setTranslationY(r4)
        L_0x0621:
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r0.nameTextView
            r3 = r3[r2]
            r3.setScaleX(r6)
            org.telegram.ui.ActionBar.SimpleTextView[] r3 = r0.nameTextView
            r3 = r3[r2]
            r3.setScaleY(r6)
        L_0x062f:
            int r2 = r2 + 1
            goto L_0x05d5
        L_0x0632:
            boolean r2 = r0.openAnimationInProgress
            if (r2 != 0) goto L_0x0644
            android.animation.ValueAnimator r2 = r0.expandAnimator
            if (r2 == 0) goto L_0x0640
            boolean r2 = r2.isRunning()
            if (r2 != 0) goto L_0x0644
        L_0x0640:
            r2 = r15
            r0.needLayoutText(r2)
        L_0x0644:
            boolean r2 = r0.isPulledDown
            if (r2 != 0) goto L_0x065c
            org.telegram.ui.ProfileActivity$OverlaysView r2 = r0.overlaysView
            android.animation.ValueAnimator r2 = r2.animator
            if (r2 == 0) goto L_0x0676
            org.telegram.ui.ProfileActivity$OverlaysView r2 = r0.overlaysView
            android.animation.ValueAnimator r2 = r2.animator
            boolean r2 = r2.isRunning()
            if (r2 == 0) goto L_0x0676
        L_0x065c:
            org.telegram.ui.ProfileActivity$OverlaysView r2 = r0.overlaysView
            android.view.ViewGroup$LayoutParams r2 = r2.getLayoutParams()
            org.telegram.ui.Components.RecyclerListView r3 = r0.listView
            int r3 = r3.getMeasuredWidth()
            r2.width = r3
            float r3 = r0.extraHeight
            float r1 = (float) r1
            float r3 = r3 + r1
            int r1 = (int) r3
            r2.height = r1
            org.telegram.ui.ProfileActivity$OverlaysView r1 = r0.overlaysView
            r1.requestLayout()
        L_0x0676:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.needLayout():void");
    }

    private void refreshNameAndOnlineXY() {
        this.nameX = ((float) AndroidUtilities.dp(-21.0f)) + (((float) this.avatarImage.getMeasuredWidth()) * (this.avatarScale - 1.4285715f));
        this.nameY = ((float) Math.floor((double) this.avatarY)) + ((float) AndroidUtilities.dp(1.3f)) + ((float) AndroidUtilities.dp(7.0f)) + ((((float) this.avatarImage.getMeasuredHeight()) * (this.avatarScale - 1.4285715f)) / 2.0f);
        this.onlineX = ((float) AndroidUtilities.dp(-21.0f)) + (((float) this.avatarImage.getMeasuredWidth()) * (this.avatarScale - 1.4285715f));
        this.onlineY = ((float) Math.floor((double) this.avatarY)) + ((float) AndroidUtilities.dp(24.0f)) + ((float) Math.floor((double) (AndroidUtilities.density * 11.0f))) + ((((float) this.avatarImage.getMeasuredHeight()) * (this.avatarScale - 1.4285715f)) / 2.0f);
    }

    public RecyclerListView getListView() {
        return this.listView;
    }

    private void needLayoutText(float f) {
        float scaleX = this.nameTextView[1].getScaleX();
        float f2 = this.extraHeight > ((float) AndroidUtilities.dp(88.0f)) ? 1.67f : 1.12f;
        if (this.extraHeight <= ((float) AndroidUtilities.dp(88.0f)) || scaleX == f2) {
            int dp = AndroidUtilities.isTablet() ? AndroidUtilities.dp(490.0f) : AndroidUtilities.displaySize.x;
            int dp2 = AndroidUtilities.dp((float) (126 + 40 + ((this.callItemVisible || this.editItemVisible) ? 48 : 0)));
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
            int min = (int) Math.min(((f3 - this.nameTextView[1].getX()) / scaleX) - ((float) AndroidUtilities.dp(8.0f)), (float) layoutParams.width);
            layoutParams.width = min;
            if (min != i2) {
                this.nameTextView[1].requestLayout();
            }
            float measureText2 = this.onlineTextView[1].getPaint().measureText(this.onlineTextView[1].getText().toString());
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.onlineTextView[1].getLayoutParams();
            FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.onlineTextView[2].getLayoutParams();
            int i3 = layoutParams2.width;
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
            if (i3 != layoutParams2.width) {
                this.onlineTextView[1].requestLayout();
                this.onlineTextView[2].requestLayout();
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
        RecyclerListView recyclerListView;
        int i3;
        TLRPC$Chat tLRPC$Chat;
        RecyclerListView recyclerListView2;
        RecyclerListView recyclerListView3;
        RecyclerListView.Holder holder;
        boolean z = true;
        int i4 = 0;
        if (i == NotificationCenter.updateInterfaces) {
            int intValue = objArr[0].intValue();
            if ((intValue & 2) == 0 && (intValue & 1) == 0 && (intValue & 4) == 0) {
                z = false;
            }
            if (this.user_id != 0) {
                if (z) {
                    updateProfileData();
                }
                if ((intValue & 1024) != 0 && (recyclerListView3 = this.listView) != null && (holder = (RecyclerListView.Holder) recyclerListView3.findViewHolderForPosition(this.phoneRow)) != null) {
                    this.listAdapter.onBindViewHolder(holder, this.phoneRow);
                }
            } else if (this.chat_id != 0) {
                int i5 = intValue & 8192;
                if (!(i5 == 0 && (intValue & 8) == 0 && (intValue & 16) == 0 && (intValue & 32) == 0 && (intValue & 4) == 0)) {
                    updateOnlineCount();
                    updateProfileData();
                }
                if (i5 != 0) {
                    updateRowsIds();
                    ListAdapter listAdapter2 = this.listAdapter;
                    if (listAdapter2 != null) {
                        listAdapter2.notifyDataSetChanged();
                    }
                }
                if (z && (recyclerListView2 = this.listView) != null) {
                    int childCount = recyclerListView2.getChildCount();
                    while (i4 < childCount) {
                        View childAt = this.listView.getChildAt(i4);
                        if (childAt instanceof UserCell) {
                            ((UserCell) childAt).update(intValue);
                        }
                        i4++;
                    }
                }
            }
        } else if (i == NotificationCenter.chatOnlineCountDidLoad) {
            Integer num = objArr[0];
            if (this.chatInfo != null && (tLRPC$Chat = this.currentChat) != null && tLRPC$Chat.id == num.intValue()) {
                this.chatInfo.online_count = objArr[1].intValue();
                updateOnlineCount();
                updateProfileData();
            }
        } else if (i == NotificationCenter.contactsDidLoad) {
            createActionBarMenu();
        } else if (i == NotificationCenter.encryptedChatCreated) {
            if (this.creatingChat) {
                AndroidUtilities.runOnUIThread(new Runnable(objArr) {
                    private final /* synthetic */ Object[] f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        ProfileActivity.this.lambda$didReceivedNotification$20$ProfileActivity(this.f$1);
                    }
                });
            }
        } else if (i == NotificationCenter.encryptedChatUpdated) {
            TLRPC$EncryptedChat tLRPC$EncryptedChat = objArr[0];
            TLRPC$EncryptedChat tLRPC$EncryptedChat2 = this.currentEncryptedChat;
            if (tLRPC$EncryptedChat2 != null && tLRPC$EncryptedChat.id == tLRPC$EncryptedChat2.id) {
                this.currentEncryptedChat = tLRPC$EncryptedChat;
                updateRowsIds();
                ListAdapter listAdapter3 = this.listAdapter;
                if (listAdapter3 != null) {
                    listAdapter3.notifyDataSetChanged();
                }
            }
        } else if (i == NotificationCenter.blockedUsersDidLoad) {
            boolean z2 = this.userBlocked;
            if (MessagesController.getInstance(this.currentAccount).blockedUsers.indexOfKey(this.user_id) < 0) {
                z = false;
            }
            this.userBlocked = z;
            if (z2 != z) {
                createActionBarMenu();
                updateRowsIds();
                this.listAdapter.notifyDataSetChanged();
            }
        } else if (i == NotificationCenter.chatInfoDidLoad) {
            TLRPC$ChatFull tLRPC$ChatFull = objArr[0];
            if (tLRPC$ChatFull.id == this.chat_id) {
                boolean booleanValue = objArr[2].booleanValue();
                TLRPC$ChatFull tLRPC$ChatFull2 = this.chatInfo;
                if ((tLRPC$ChatFull2 instanceof TLRPC$TL_channelFull) && tLRPC$ChatFull.participants == null && tLRPC$ChatFull2 != null) {
                    tLRPC$ChatFull.participants = tLRPC$ChatFull2.participants;
                }
                if (this.chatInfo == null && (tLRPC$ChatFull instanceof TLRPC$TL_channelFull)) {
                    i4 = 1;
                }
                this.chatInfo = tLRPC$ChatFull;
                if (this.mergeDialogId == 0 && (i3 = tLRPC$ChatFull.migrated_from_chat_id) != 0) {
                    this.mergeDialogId = (long) (-i3);
                    MediaDataController.getInstance(this.currentAccount).getMediaCount(this.mergeDialogId, 0, this.classGuid, true);
                }
                fetchUsersFromChannelInfo();
                updateOnlineCount();
                updateRowsIds();
                ListAdapter listAdapter4 = this.listAdapter;
                if (listAdapter4 != null) {
                    listAdapter4.notifyDataSetChanged();
                }
                TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id));
                if (chat != null) {
                    this.currentChat = chat;
                    createActionBarMenu();
                }
                if (!this.currentChat.megagroup) {
                    return;
                }
                if (i4 != 0 || !booleanValue) {
                    getChannelParticipants(true);
                }
            }
        } else if (i == NotificationCenter.closeChats) {
            removeSelfFromStack();
        } else if (i == NotificationCenter.botInfoDidLoad) {
            TLRPC$BotInfo tLRPC$BotInfo = objArr[0];
            if (tLRPC$BotInfo.user_id == this.user_id) {
                this.botInfo = tLRPC$BotInfo;
                updateRowsIds();
                ListAdapter listAdapter5 = this.listAdapter;
                if (listAdapter5 != null) {
                    listAdapter5.notifyDataSetChanged();
                }
            }
        } else if (i == NotificationCenter.userInfoDidLoad) {
            if (objArr[0].intValue() == this.user_id) {
                this.userInfo = objArr[1];
                if (this.openAnimationInProgress || this.callItem != null) {
                    this.recreateMenuAfterAnimation = true;
                } else {
                    createActionBarMenu();
                }
                updateRowsIds();
                ListAdapter listAdapter6 = this.listAdapter;
                if (listAdapter6 != null) {
                    try {
                        listAdapter6.notifyDataSetChanged();
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
                this.sharedMediaLayout.setCommonGroupsCount(this.userInfo.common_chats_count);
                updateSelectedMediaTabText();
            }
        } else if (i == NotificationCenter.didReceiveNewMessages) {
            if (!objArr[2].booleanValue()) {
                long dialogId = getDialogId();
                if (dialogId == objArr[0].longValue()) {
                    int i6 = (int) dialogId;
                    ArrayList arrayList = objArr[1];
                    while (i4 < arrayList.size()) {
                        MessageObject messageObject = (MessageObject) arrayList.get(i4);
                        if (this.currentEncryptedChat != null) {
                            TLRPC$MessageAction tLRPC$MessageAction = messageObject.messageOwner.action;
                            if (tLRPC$MessageAction instanceof TLRPC$TL_messageEncryptedAction) {
                                TLRPC$DecryptedMessageAction tLRPC$DecryptedMessageAction = tLRPC$MessageAction.encryptedAction;
                                if (tLRPC$DecryptedMessageAction instanceof TLRPC$TL_decryptedMessageActionSetMessageTTL) {
                                    TLRPC$TL_decryptedMessageActionSetMessageTTL tLRPC$TL_decryptedMessageActionSetMessageTTL = (TLRPC$TL_decryptedMessageActionSetMessageTTL) tLRPC$DecryptedMessageAction;
                                    ListAdapter listAdapter7 = this.listAdapter;
                                    if (listAdapter7 != null) {
                                        listAdapter7.notifyDataSetChanged();
                                    }
                                }
                            }
                        }
                        i4++;
                    }
                }
            }
        } else if (i == NotificationCenter.emojiDidLoad && (recyclerListView = this.listView) != null) {
            recyclerListView.invalidateViews();
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$20$ProfileActivity(Object[] objArr) {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
        Bundle bundle = new Bundle();
        bundle.putInt("enc_id", objArr[0].id);
        presentFragment(new ChatActivity(bundle), true);
    }

    public void mediaCountUpdated() {
        SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader2;
        SharedMediaLayout sharedMediaLayout2 = this.sharedMediaLayout;
        if (!(sharedMediaLayout2 == null || (sharedMediaPreloader2 = this.sharedMediaPreloader) == null)) {
            sharedMediaLayout2.setNewMediaCounts(sharedMediaPreloader2.getLastMediaCount());
        }
        updateSharedMediaRows();
        updateSelectedMediaTabText();
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

    /* access modifiers changed from: protected */
    public void onBecomeFullyHidden() {
        UndoView undoView2 = this.undoView;
        if (undoView2 != null) {
            undoView2.hide(true, 0);
        }
    }

    public void setPlayProfileAnimation(int i) {
        SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
        if (AndroidUtilities.isTablet()) {
            return;
        }
        if (globalMainSettings.getBoolean("view_animations", true)) {
            this.playProfileAnimation = i;
        } else if (i == 2) {
            this.expandPhoto = true;
        }
    }

    private void updateSharedMediaRows() {
        if (this.listAdapter != null) {
            int i = this.sharedMediaRow;
            updateRowsIds();
            if (i != this.sharedMediaRow) {
                this.listAdapter.notifyDataSetChanged();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationStart(boolean z, boolean z2) {
        if (((!z && z2) || (z && !z2)) && this.playProfileAnimation != 0 && this.allowProfileAnimation && !this.isPulledDown) {
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
            if (!z2 && this.playProfileAnimation != 0 && this.allowProfileAnimation) {
                this.openAnimationInProgress = false;
                if (this.recreateMenuAfterAnimation) {
                    createActionBarMenu();
                }
            }
            NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(false);
        }
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
            if (!(this.nameTextView[i5] == null || (i5 == 1 && this.playProfileAnimation == i4))) {
                this.nameTextView[i5].setTextColor(Color.argb(alpha + alpha2, red3 + red4, green3 + green4, blue3 + blue4));
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
            if (!(this.onlineTextView[i6] == null || (i6 == i2 && this.playProfileAnimation == i7))) {
                this.onlineTextView[i6].setTextColor(Color.argb(alpha3 + alpha4, red5 + red6, green5 + green6, blue5 + blue6));
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
        needLayout();
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
            ImageView imageView = this.writeButton;
            if (imageView != null) {
                imageView.setScaleX(0.2f);
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
            ActionBarMenuItem actionBarMenuItem = this.animatingItem;
            if (actionBarMenuItem != null) {
                actionBarMenuItem.setAlpha(1.0f);
                arrayList.add(ObjectAnimator.ofFloat(this.animatingItem, View.ALPHA, new float[]{0.0f}));
            }
            if (this.callItemVisible) {
                this.callItem.setAlpha(0.0f);
                arrayList.add(ObjectAnimator.ofFloat(this.callItem, View.ALPHA, new float[]{1.0f}));
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
            ActionBarMenuItem actionBarMenuItem2 = this.animatingItem;
            if (actionBarMenuItem2 != null) {
                actionBarMenuItem2.setAlpha(0.0f);
                arrayList2.add(ObjectAnimator.ofFloat(this.animatingItem, View.ALPHA, new float[]{1.0f}));
            }
            if (this.callItemVisible) {
                this.callItem.setAlpha(1.0f);
                arrayList2.add(ObjectAnimator.ofFloat(this.callItem, View.ALPHA, new float[]{0.0f}));
            }
            if (this.editItemVisible) {
                this.editItem.setAlpha(1.0f);
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
                if (ProfileActivity.this.playProfileAnimation == 2) {
                    int unused2 = ProfileActivity.this.playProfileAnimation = 1;
                    ProfileActivity.this.avatarImage.setForegroundAlpha(1.0f);
                    ProfileActivity.this.avatarImage.setVisibility(8);
                    ProfileActivity.this.avatarsViewPager.resetCurrentItem();
                    ProfileActivity.this.avatarsViewPager.setVisibility(0);
                }
            }
        });
        animatorSet.setInterpolator(this.playProfileAnimation == 2 ? CubicBezierInterpolator.DEFAULT : new DecelerateInterpolator());
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
        TLRPC$UserStatus tLRPC$UserStatus;
        this.onlineCount = 0;
        int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
        this.sortedUsers.clear();
        TLRPC$ChatFull tLRPC$ChatFull = this.chatInfo;
        if ((tLRPC$ChatFull instanceof TLRPC$TL_chatFull) || ((tLRPC$ChatFull instanceof TLRPC$TL_channelFull) && tLRPC$ChatFull.participants_count <= 200 && tLRPC$ChatFull.participants != null)) {
            for (int i2 = 0; i2 < this.chatInfo.participants.participants.size(); i2++) {
                TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.chatInfo.participants.participants.get(i2).user_id));
                if (!(user == null || (tLRPC$UserStatus = user.status) == null || ((tLRPC$UserStatus.expires <= currentTime && user.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) || user.status.expires <= 10000))) {
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
            }
            if (this.sharedMediaLayout != null && this.sharedMediaRow != -1 && this.sortedUsers.size() > 5) {
                this.sharedMediaLayout.setChatUsers(this.sortedUsers, this.chatInfo);
                return;
            }
            return;
        }
        TLRPC$ChatFull tLRPC$ChatFull2 = this.chatInfo;
        if ((tLRPC$ChatFull2 instanceof TLRPC$TL_channelFull) && tLRPC$ChatFull2.participants_count > 200) {
            this.onlineCount = tLRPC$ChatFull2.online_count;
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
            org.telegram.tgnet.TLRPC$ChatParticipant r7 = (org.telegram.tgnet.TLRPC$ChatParticipant) r7
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
            org.telegram.tgnet.TLRPC$ChatParticipant r6 = (org.telegram.tgnet.TLRPC$ChatParticipant) r6
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

    public void setChatInfo(TLRPC$ChatFull tLRPC$ChatFull) {
        int i;
        this.chatInfo = tLRPC$ChatFull;
        if (!(tLRPC$ChatFull == null || (i = tLRPC$ChatFull.migrated_from_chat_id) == 0 || this.mergeDialogId != 0)) {
            this.mergeDialogId = (long) (-i);
            MediaDataController.getInstance(this.currentAccount).getMediaCounts(this.mergeDialogId, this.classGuid);
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
        this.playProfileAnimation = 0;
        finishFragment();
    }

    public boolean isChat() {
        return this.chat_id != 0;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0069  */
    /* JADX WARNING: Removed duplicated region for block: B:192:0x0389  */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x0395  */
    /* JADX WARNING: Removed duplicated region for block: B:200:0x03a7  */
    /* JADX WARNING: Removed duplicated region for block: B:209:0x03c4  */
    /* JADX WARNING: Removed duplicated region for block: B:214:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0185  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateRowsIds() {
        /*
            r9 = this;
            int r0 = r9.rowCount
            r1 = 0
            r9.rowCount = r1
            r2 = -1
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
            r9.settingsSectionRow = r2
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
            org.telegram.ui.Components.SharedMediaLayout$SharedMediaPreloader r3 = r9.sharedMediaPreloader
            r4 = 1
            if (r3 == 0) goto L_0x0054
            int[] r3 = r3.getLastMediaCount()
            r5 = 0
        L_0x0048:
            int r6 = r3.length
            if (r5 >= r6) goto L_0x0054
            r6 = r3[r5]
            if (r6 <= 0) goto L_0x0051
            r3 = 1
            goto L_0x0055
        L_0x0051:
            int r5 = r5 + 1
            goto L_0x0048
        L_0x0054:
            r3 = 0
        L_0x0055:
            int r5 = r9.user_id
            if (r5 == 0) goto L_0x0065
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 == 0) goto L_0x0065
            int r5 = r9.rowCount
            int r6 = r5 + 1
            r9.rowCount = r6
            r9.emptyRow = r5
        L_0x0065:
            int r5 = r9.user_id
            if (r5 == 0) goto L_0x0185
            int r5 = r9.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            int r6 = r9.user_id
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$User r5 = r5.getUser(r6)
            org.telegram.tgnet.TLRPC$UserFull r6 = r9.userInfo
            if (r6 == 0) goto L_0x0085
            java.lang.String r6 = r6.about
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 == 0) goto L_0x008f
        L_0x0085:
            if (r5 == 0) goto L_0x0091
            java.lang.String r6 = r5.username
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 != 0) goto L_0x0091
        L_0x008f:
            r6 = 1
            goto L_0x0092
        L_0x0091:
            r6 = 0
        L_0x0092:
            if (r5 == 0) goto L_0x009d
            java.lang.String r7 = r5.phone
            boolean r7 = android.text.TextUtils.isEmpty(r7)
            if (r7 != 0) goto L_0x009d
            goto L_0x009e
        L_0x009d:
            r4 = 0
        L_0x009e:
            int r7 = r9.rowCount
            int r8 = r7 + 1
            r9.rowCount = r8
            r9.infoHeaderRow = r7
            boolean r7 = r9.isBot
            if (r7 != 0) goto L_0x00b8
            if (r4 != 0) goto L_0x00b0
            if (r4 != 0) goto L_0x00b8
            if (r6 != 0) goto L_0x00b8
        L_0x00b0:
            int r4 = r9.rowCount
            int r6 = r4 + 1
            r9.rowCount = r6
            r9.phoneRow = r4
        L_0x00b8:
            org.telegram.tgnet.TLRPC$UserFull r4 = r9.userInfo
            if (r4 == 0) goto L_0x00cc
            java.lang.String r4 = r4.about
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x00cc
            int r4 = r9.rowCount
            int r6 = r4 + 1
            r9.rowCount = r6
            r9.userInfoRow = r4
        L_0x00cc:
            if (r5 == 0) goto L_0x00de
            java.lang.String r4 = r5.username
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x00de
            int r4 = r9.rowCount
            int r6 = r4 + 1
            r9.rowCount = r6
            r9.usernameRow = r4
        L_0x00de:
            int r4 = r9.phoneRow
            if (r4 != r2) goto L_0x00ea
            int r4 = r9.userInfoRow
            if (r4 != r2) goto L_0x00ea
            int r4 = r9.usernameRow
            if (r4 == r2) goto L_0x00f2
        L_0x00ea:
            int r4 = r9.rowCount
            int r6 = r4 + 1
            r9.rowCount = r6
            r9.notificationsDividerRow = r4
        L_0x00f2:
            int r4 = r9.user_id
            int r6 = r9.currentAccount
            org.telegram.messenger.UserConfig r6 = org.telegram.messenger.UserConfig.getInstance(r6)
            int r6 = r6.getClientUserId()
            if (r4 == r6) goto L_0x0108
            int r4 = r9.rowCount
            int r6 = r4 + 1
            r9.rowCount = r6
            r9.notificationsRow = r4
        L_0x0108:
            int r4 = r9.rowCount
            int r6 = r4 + 1
            r9.rowCount = r6
            r9.infoSectionRow = r4
            org.telegram.tgnet.TLRPC$EncryptedChat r4 = r9.currentEncryptedChat
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_encryptedChat
            if (r4 == 0) goto L_0x0128
            int r4 = r6 + 1
            r9.rowCount = r4
            r9.settingsTimerRow = r6
            int r6 = r4 + 1
            r9.rowCount = r6
            r9.settingsKeyRow = r4
            int r4 = r6 + 1
            r9.rowCount = r4
            r9.settingsSectionRow = r6
        L_0x0128:
            if (r5 == 0) goto L_0x0152
            boolean r4 = r9.isBot
            if (r4 != 0) goto L_0x0152
            org.telegram.tgnet.TLRPC$EncryptedChat r4 = r9.currentEncryptedChat
            if (r4 != 0) goto L_0x0152
            int r4 = r5.id
            int r5 = r9.currentAccount
            org.telegram.messenger.UserConfig r5 = org.telegram.messenger.UserConfig.getInstance(r5)
            int r5 = r5.getClientUserId()
            if (r4 == r5) goto L_0x0152
            boolean r4 = r9.userBlocked
            if (r4 == 0) goto L_0x0152
            int r4 = r9.rowCount
            int r5 = r4 + 1
            r9.rowCount = r5
            r9.unblockRow = r4
            int r4 = r5 + 1
            r9.rowCount = r4
            r9.lastSectionRow = r5
        L_0x0152:
            if (r3 != 0) goto L_0x017b
            org.telegram.tgnet.TLRPC$UserFull r3 = r9.userInfo
            if (r3 == 0) goto L_0x015d
            int r3 = r3.common_chats_count
            if (r3 == 0) goto L_0x015d
            goto L_0x017b
        L_0x015d:
            int r3 = r9.lastSectionRow
            if (r3 != r2) goto L_0x0385
            boolean r3 = r9.needSendMessage
            if (r3 == 0) goto L_0x0385
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
            goto L_0x0385
        L_0x017b:
            int r3 = r9.rowCount
            int r4 = r3 + 1
            r9.rowCount = r4
            r9.sharedMediaRow = r3
            goto L_0x0385
        L_0x0185:
            int r4 = r9.chat_id
            if (r4 == 0) goto L_0x0385
            org.telegram.tgnet.TLRPC$ChatFull r4 = r9.chatInfo
            if (r4 == 0) goto L_0x019d
            java.lang.String r4 = r4.about
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 == 0) goto L_0x01a7
            org.telegram.tgnet.TLRPC$ChatFull r4 = r9.chatInfo
            org.telegram.tgnet.TLRPC$ChannelLocation r4 = r4.location
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_channelLocation
            if (r4 != 0) goto L_0x01a7
        L_0x019d:
            org.telegram.tgnet.TLRPC$Chat r4 = r9.currentChat
            java.lang.String r4 = r4.username
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x01e5
        L_0x01a7:
            int r4 = r9.rowCount
            int r5 = r4 + 1
            r9.rowCount = r5
            r9.infoHeaderRow = r4
            org.telegram.tgnet.TLRPC$ChatFull r4 = r9.chatInfo
            if (r4 == 0) goto L_0x01d3
            java.lang.String r4 = r4.about
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x01c3
            int r4 = r9.rowCount
            int r5 = r4 + 1
            r9.rowCount = r5
            r9.channelInfoRow = r4
        L_0x01c3:
            org.telegram.tgnet.TLRPC$ChatFull r4 = r9.chatInfo
            org.telegram.tgnet.TLRPC$ChannelLocation r4 = r4.location
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_channelLocation
            if (r4 == 0) goto L_0x01d3
            int r4 = r9.rowCount
            int r5 = r4 + 1
            r9.rowCount = r5
            r9.locationRow = r4
        L_0x01d3:
            org.telegram.tgnet.TLRPC$Chat r4 = r9.currentChat
            java.lang.String r4 = r4.username
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x01e5
            int r4 = r9.rowCount
            int r5 = r4 + 1
            r9.rowCount = r5
            r9.usernameRow = r4
        L_0x01e5:
            int r4 = r9.infoHeaderRow
            if (r4 == r2) goto L_0x01f1
            int r4 = r9.rowCount
            int r5 = r4 + 1
            r9.rowCount = r5
            r9.notificationsDividerRow = r4
        L_0x01f1:
            int r4 = r9.rowCount
            int r5 = r4 + 1
            r9.rowCount = r5
            r9.notificationsRow = r4
            int r4 = r5 + 1
            r9.rowCount = r4
            r9.infoSectionRow = r5
            org.telegram.tgnet.TLRPC$Chat r4 = r9.currentChat
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r4)
            if (r4 == 0) goto L_0x0247
            org.telegram.tgnet.TLRPC$Chat r4 = r9.currentChat
            boolean r5 = r4.megagroup
            if (r5 != 0) goto L_0x0247
            org.telegram.tgnet.TLRPC$ChatFull r5 = r9.chatInfo
            if (r5 == 0) goto L_0x0247
            boolean r4 = r4.creator
            if (r4 != 0) goto L_0x0219
            boolean r4 = r5.can_view_participants
            if (r4 == 0) goto L_0x0247
        L_0x0219:
            int r4 = r9.rowCount
            int r5 = r4 + 1
            r9.rowCount = r5
            r9.membersHeaderRow = r4
            int r4 = r5 + 1
            r9.rowCount = r4
            r9.subscribersRow = r5
            int r5 = r4 + 1
            r9.rowCount = r5
            r9.administratorsRow = r4
            org.telegram.tgnet.TLRPC$ChatFull r4 = r9.chatInfo
            int r5 = r4.banned_count
            if (r5 != 0) goto L_0x0237
            int r4 = r4.kicked_count
            if (r4 == 0) goto L_0x023f
        L_0x0237:
            int r4 = r9.rowCount
            int r5 = r4 + 1
            r9.rowCount = r5
            r9.blockedUsersRow = r4
        L_0x023f:
            int r4 = r9.rowCount
            int r5 = r4 + 1
            r9.rowCount = r5
            r9.membersSectionRow = r4
        L_0x0247:
            org.telegram.tgnet.TLRPC$Chat r4 = r9.currentChat
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r4)
            r5 = 5
            r6 = 0
            if (r4 == 0) goto L_0x0304
            org.telegram.tgnet.TLRPC$ChatFull r4 = r9.chatInfo
            if (r4 == 0) goto L_0x02e6
            org.telegram.tgnet.TLRPC$Chat r7 = r9.currentChat
            boolean r7 = r7.megagroup
            if (r7 == 0) goto L_0x02e6
            org.telegram.tgnet.TLRPC$ChatParticipants r4 = r4.participants
            if (r4 == 0) goto L_0x02e6
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r4 = r4.participants
            boolean r4 = r4.isEmpty()
            if (r4 != 0) goto L_0x02e6
            org.telegram.tgnet.TLRPC$Chat r4 = r9.currentChat
            boolean r4 = org.telegram.messenger.ChatObject.isNotInChat(r4)
            if (r4 != 0) goto L_0x0293
            org.telegram.tgnet.TLRPC$Chat r4 = r9.currentChat
            boolean r7 = r4.megagroup
            if (r7 == 0) goto L_0x0293
            boolean r4 = org.telegram.messenger.ChatObject.canAddUsers(r4)
            if (r4 == 0) goto L_0x0293
            org.telegram.tgnet.TLRPC$ChatFull r4 = r9.chatInfo
            if (r4 == 0) goto L_0x028b
            int r4 = r4.participants_count
            int r7 = r9.currentAccount
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r7)
            int r7 = r7.maxMegagroupCount
            if (r4 >= r7) goto L_0x0293
        L_0x028b:
            int r4 = r9.rowCount
            int r7 = r4 + 1
            r9.rowCount = r7
            r9.addMemberRow = r4
        L_0x0293:
            org.telegram.tgnet.TLRPC$ChatFull r4 = r9.chatInfo
            org.telegram.tgnet.TLRPC$ChatParticipants r4 = r4.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r4 = r4.participants
            int r4 = r4.size()
            if (r4 <= r5) goto L_0x02ba
            if (r3 != 0) goto L_0x02a2
            goto L_0x02ba
        L_0x02a2:
            int r4 = r9.addMemberRow
            if (r4 == r2) goto L_0x02ae
            int r4 = r9.rowCount
            int r5 = r4 + 1
            r9.rowCount = r5
            r9.membersSectionRow = r4
        L_0x02ae:
            org.telegram.ui.Components.SharedMediaLayout r4 = r9.sharedMediaLayout
            if (r4 == 0) goto L_0x02e6
            java.util.ArrayList<java.lang.Integer> r5 = r9.sortedUsers
            org.telegram.tgnet.TLRPC$ChatFull r6 = r9.chatInfo
            r4.setChatUsers(r5, r6)
            goto L_0x02e6
        L_0x02ba:
            int r4 = r9.addMemberRow
            if (r4 != r2) goto L_0x02c6
            int r4 = r9.rowCount
            int r5 = r4 + 1
            r9.rowCount = r5
            r9.membersHeaderRow = r4
        L_0x02c6:
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
            org.telegram.ui.Components.SharedMediaLayout r4 = r9.sharedMediaLayout
            if (r4 == 0) goto L_0x02e6
            r4.setChatUsers(r6, r6)
        L_0x02e6:
            int r4 = r9.lastSectionRow
            if (r4 != r2) goto L_0x037b
            org.telegram.tgnet.TLRPC$Chat r4 = r9.currentChat
            boolean r5 = r4.left
            if (r5 == 0) goto L_0x037b
            boolean r4 = r4.kicked
            if (r4 != 0) goto L_0x037b
            int r4 = r9.rowCount
            int r5 = r4 + 1
            r9.rowCount = r5
            r9.joinRow = r4
            int r4 = r5 + 1
            r9.rowCount = r4
            r9.lastSectionRow = r5
            goto L_0x037b
        L_0x0304:
            org.telegram.tgnet.TLRPC$ChatFull r4 = r9.chatInfo
            if (r4 == 0) goto L_0x037b
            org.telegram.tgnet.TLRPC$ChatParticipants r4 = r4.participants
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantsForbidden
            if (r4 != 0) goto L_0x037b
            org.telegram.tgnet.TLRPC$Chat r4 = r9.currentChat
            boolean r4 = org.telegram.messenger.ChatObject.canAddUsers(r4)
            if (r4 != 0) goto L_0x0320
            org.telegram.tgnet.TLRPC$Chat r4 = r9.currentChat
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r4.default_banned_rights
            if (r4 == 0) goto L_0x0320
            boolean r4 = r4.invite_users
            if (r4 != 0) goto L_0x0328
        L_0x0320:
            int r4 = r9.rowCount
            int r7 = r4 + 1
            r9.rowCount = r7
            r9.addMemberRow = r4
        L_0x0328:
            org.telegram.tgnet.TLRPC$ChatFull r4 = r9.chatInfo
            org.telegram.tgnet.TLRPC$ChatParticipants r4 = r4.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r4 = r4.participants
            int r4 = r4.size()
            if (r4 <= r5) goto L_0x034f
            if (r3 != 0) goto L_0x0337
            goto L_0x034f
        L_0x0337:
            int r4 = r9.addMemberRow
            if (r4 == r2) goto L_0x0343
            int r4 = r9.rowCount
            int r5 = r4 + 1
            r9.rowCount = r5
            r9.membersSectionRow = r4
        L_0x0343:
            org.telegram.ui.Components.SharedMediaLayout r4 = r9.sharedMediaLayout
            if (r4 == 0) goto L_0x037b
            java.util.ArrayList<java.lang.Integer> r5 = r9.sortedUsers
            org.telegram.tgnet.TLRPC$ChatFull r6 = r9.chatInfo
            r4.setChatUsers(r5, r6)
            goto L_0x037b
        L_0x034f:
            int r4 = r9.addMemberRow
            if (r4 != r2) goto L_0x035b
            int r4 = r9.rowCount
            int r5 = r4 + 1
            r9.rowCount = r5
            r9.membersHeaderRow = r4
        L_0x035b:
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
            org.telegram.ui.Components.SharedMediaLayout r4 = r9.sharedMediaLayout
            if (r4 == 0) goto L_0x037b
            r4.setChatUsers(r6, r6)
        L_0x037b:
            if (r3 == 0) goto L_0x0385
            int r3 = r9.rowCount
            int r4 = r3 + 1
            r9.rowCount = r4
            r9.sharedMediaRow = r3
        L_0x0385:
            int r3 = r9.sharedMediaRow
            if (r3 != r2) goto L_0x0391
            int r2 = r9.rowCount
            int r3 = r2 + 1
            r9.rowCount = r3
            r9.bottomPaddingRow = r2
        L_0x0391:
            org.telegram.ui.ActionBar.ActionBar r2 = r9.actionBar
            if (r2 == 0) goto L_0x03a7
            int r2 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight()
            org.telegram.ui.ActionBar.ActionBar r3 = r9.actionBar
            boolean r3 = r3.getOccupyStatusBar()
            if (r3 == 0) goto L_0x03a4
            int r3 = org.telegram.messenger.AndroidUtilities.statusBarHeight
            goto L_0x03a5
        L_0x03a4:
            r3 = 0
        L_0x03a5:
            int r2 = r2 + r3
            goto L_0x03a8
        L_0x03a7:
            r2 = 0
        L_0x03a8:
            org.telegram.ui.Components.RecyclerListView r3 = r9.listView
            if (r3 == 0) goto L_0x03c4
            int r3 = r9.rowCount
            if (r0 > r3) goto L_0x03c4
            int r0 = r9.listContentHeight
            if (r0 == 0) goto L_0x03c6
            int r0 = r0 + r2
            r2 = 1118830592(0x42b00000, float:88.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 + r2
            org.telegram.ui.Components.RecyclerListView r2 = r9.listView
            int r2 = r2.getMeasuredHeight()
            if (r0 >= r2) goto L_0x03c6
        L_0x03c4:
            r9.lastMeasuredContentWidth = r1
        L_0x03c6:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.updateRowsIds():void");
    }

    private Drawable getScamDrawable() {
        if (this.scamDrawable == null) {
            ScamDrawable scamDrawable2 = new ScamDrawable(11);
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

    /* JADX WARNING: Removed duplicated region for block: B:165:0x03d1  */
    /* JADX WARNING: Removed duplicated region for block: B:201:0x047c  */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x048a A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:238:0x0593  */
    /* JADX WARNING: Removed duplicated region for block: B:241:0x059a  */
    /* JADX WARNING: Removed duplicated region for block: B:242:0x059d  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x01cc  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x01d1  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x01d4  */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x01f8  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateProfileData() {
        /*
            r26 = this;
            r0 = r26
            org.telegram.ui.ProfileActivity$AvatarImageView r1 = r0.avatarImage
            if (r1 == 0) goto L_0x05d5
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r0.nameTextView
            if (r1 != 0) goto L_0x000c
            goto L_0x05d5
        L_0x000c:
            int r1 = r0.currentAccount
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1)
            int r1 = r1.getConnectionState()
            r2 = 2
            r4 = 1
            if (r1 != r2) goto L_0x0024
            r1 = 2131627228(0x7f0e0cdc, float:1.8881715E38)
            java.lang.String r5 = "WaitingForNetwork"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r1)
            goto L_0x004b
        L_0x0024:
            if (r1 != r4) goto L_0x0030
            r1 = 2131624763(0x7f0e033b, float:1.8876715E38)
            java.lang.String r5 = "Connecting"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r1)
            goto L_0x004b
        L_0x0030:
            r5 = 5
            if (r1 != r5) goto L_0x003d
            r1 = 2131627052(0x7f0e0c2c, float:1.8881358E38)
            java.lang.String r5 = "Updating"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r1)
            goto L_0x004b
        L_0x003d:
            r5 = 4
            if (r1 != r5) goto L_0x004a
            r1 = 2131624765(0x7f0e033d, float:1.8876719E38)
            java.lang.String r5 = "ConnectingToProxy"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r1)
            goto L_0x004b
        L_0x004a:
            r1 = 0
        L_0x004b:
            int r5 = r0.user_id
            java.lang.String r6 = "50_50"
            r7 = 0
            if (r5 == 0) goto L_0x022d
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
            r9.setInfo((org.telegram.tgnet.TLRPC$User) r5)
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
            r6 = 2131624652(0x7f0e02cc, float:1.887649E38)
            java.lang.String r9 = "ChatYourSelf"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r9 = 2131624657(0x7f0e02d1, float:1.88765E38)
            java.lang.String r10 = "ChatYourSelfName"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r25 = r9
            r9 = r6
            r6 = r25
            goto L_0x012a
        L_0x00bb:
            int r9 = r5.id
            r10 = 333000(0x514c8, float:4.66632E-40)
            if (r9 == r10) goto L_0x0121
            r10 = 777000(0xbdb28, float:1.088809E-39)
            if (r9 == r10) goto L_0x0121
            r10 = 42777(0xa719, float:5.9943E-41)
            if (r9 != r10) goto L_0x00cd
            goto L_0x0121
        L_0x00cd:
            boolean r9 = org.telegram.messenger.MessagesController.isSupportUser(r5)
            if (r9 == 0) goto L_0x00dd
            r9 = 2131626874(0x7f0e0b7a, float:1.8880997E38)
            java.lang.String r10 = "SupportStatus"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            goto L_0x012a
        L_0x00dd:
            boolean r9 = r0.isBot
            if (r9 == 0) goto L_0x00eb
            r9 = 2131624439(0x7f0e01f7, float:1.8876058E38)
            java.lang.String r10 = "Bot"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            goto L_0x012a
        L_0x00eb:
            boolean[] r9 = r0.isOnline
            r9[r7] = r7
            int r10 = r0.currentAccount
            java.lang.String r9 = org.telegram.messenger.LocaleController.formatUserStatus(r10, r5, r9)
            org.telegram.ui.ActionBar.SimpleTextView[] r10 = r0.onlineTextView
            r10 = r10[r4]
            if (r10 == 0) goto L_0x012a
            boolean r10 = r0.mediaHeaderVisible
            if (r10 != 0) goto L_0x012a
            boolean[] r10 = r0.isOnline
            boolean r10 = r10[r7]
            if (r10 == 0) goto L_0x0108
            java.lang.String r10 = "profile_status"
            goto L_0x010a
        L_0x0108:
            java.lang.String r10 = "avatar_subtitleInProfileBlue"
        L_0x010a:
            org.telegram.ui.ActionBar.SimpleTextView[] r11 = r0.onlineTextView
            r11 = r11[r4]
            r11.setTag(r10)
            boolean r11 = r0.isPulledDown
            if (r11 != 0) goto L_0x012a
            org.telegram.ui.ActionBar.SimpleTextView[] r11 = r0.onlineTextView
            r11 = r11[r4]
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r11.setTextColor(r10)
            goto L_0x012a
        L_0x0121:
            r9 = 2131626694(0x7f0e0ac6, float:1.8880631E38)
            java.lang.String r10 = "ServiceNotifications"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
        L_0x012a:
            r10 = 0
        L_0x012b:
            if (r10 >= r2) goto L_0x021d
            org.telegram.ui.ActionBar.SimpleTextView[] r11 = r0.nameTextView
            r11 = r11[r10]
            if (r11 != 0) goto L_0x0135
            goto L_0x0219
        L_0x0135:
            if (r10 != 0) goto L_0x01ae
            int r11 = r5.id
            int r12 = r0.currentAccount
            org.telegram.messenger.UserConfig r12 = org.telegram.messenger.UserConfig.getInstance(r12)
            int r12 = r12.getClientUserId()
            if (r11 == r12) goto L_0x01ae
            int r11 = r5.id
            int r12 = r11 / 1000
            r13 = 777(0x309, float:1.089E-42)
            if (r12 == r13) goto L_0x01ae
            int r11 = r11 / 1000
            r12 = 333(0x14d, float:4.67E-43)
            if (r11 == r12) goto L_0x01ae
            java.lang.String r11 = r5.phone
            if (r11 == 0) goto L_0x01ae
            int r11 = r11.length()
            if (r11 == 0) goto L_0x01ae
            int r11 = r0.currentAccount
            org.telegram.messenger.ContactsController r11 = org.telegram.messenger.ContactsController.getInstance(r11)
            java.util.concurrent.ConcurrentHashMap<java.lang.Integer, org.telegram.tgnet.TLRPC$TL_contact> r11 = r11.contactsDict
            int r12 = r5.id
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            java.lang.Object r11 = r11.get(r12)
            if (r11 != 0) goto L_0x01ae
            int r11 = r0.currentAccount
            org.telegram.messenger.ContactsController r11 = org.telegram.messenger.ContactsController.getInstance(r11)
            java.util.concurrent.ConcurrentHashMap<java.lang.Integer, org.telegram.tgnet.TLRPC$TL_contact> r11 = r11.contactsDict
            int r11 = r11.size()
            if (r11 != 0) goto L_0x018b
            int r11 = r0.currentAccount
            org.telegram.messenger.ContactsController r11 = org.telegram.messenger.ContactsController.getInstance(r11)
            boolean r11 = r11.isLoadingContacts()
            if (r11 != 0) goto L_0x01ae
        L_0x018b:
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
            goto L_0x01b5
        L_0x01ae:
            org.telegram.ui.ActionBar.SimpleTextView[] r11 = r0.nameTextView
            r11 = r11[r10]
            r11.setText(r6)
        L_0x01b5:
            if (r10 != 0) goto L_0x01c1
            if (r1 == 0) goto L_0x01c1
            org.telegram.ui.ActionBar.SimpleTextView[] r11 = r0.onlineTextView
            r11 = r11[r10]
            r11.setText(r1)
            goto L_0x01c8
        L_0x01c1:
            org.telegram.ui.ActionBar.SimpleTextView[] r11 = r0.onlineTextView
            r11 = r11[r10]
            r11.setText(r9)
        L_0x01c8:
            org.telegram.tgnet.TLRPC$EncryptedChat r11 = r0.currentEncryptedChat
            if (r11 == 0) goto L_0x01d1
            android.graphics.drawable.Drawable r11 = r26.getLockIconDrawable()
            goto L_0x01d2
        L_0x01d1:
            r11 = 0
        L_0x01d2:
            if (r10 != 0) goto L_0x01f8
            boolean r12 = r5.scam
            if (r12 == 0) goto L_0x01dd
            android.graphics.drawable.Drawable r12 = r26.getScamDrawable()
            goto L_0x020b
        L_0x01dd:
            int r12 = r0.currentAccount
            org.telegram.messenger.MessagesController r12 = org.telegram.messenger.MessagesController.getInstance(r12)
            long r13 = r0.dialog_id
            r15 = 0
            int r17 = (r13 > r15 ? 1 : (r13 == r15 ? 0 : -1))
            if (r17 == 0) goto L_0x01ec
            goto L_0x01ef
        L_0x01ec:
            int r13 = r0.user_id
            long r13 = (long) r13
        L_0x01ef:
            boolean r12 = r12.isDialogMuted(r13)
            if (r12 == 0) goto L_0x020a
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.chat_muteIconDrawable
            goto L_0x020b
        L_0x01f8:
            boolean r12 = r5.scam
            if (r12 == 0) goto L_0x0201
            android.graphics.drawable.Drawable r12 = r26.getScamDrawable()
            goto L_0x020b
        L_0x0201:
            boolean r12 = r5.verified
            if (r12 == 0) goto L_0x020a
            android.graphics.drawable.Drawable r12 = r26.getVerifiedCrossfadeDrawable()
            goto L_0x020b
        L_0x020a:
            r12 = 0
        L_0x020b:
            org.telegram.ui.ActionBar.SimpleTextView[] r13 = r0.nameTextView
            r13 = r13[r10]
            r13.setLeftDrawable((android.graphics.drawable.Drawable) r11)
            org.telegram.ui.ActionBar.SimpleTextView[] r11 = r0.nameTextView
            r11 = r11[r10]
            r11.setRightDrawable((android.graphics.drawable.Drawable) r12)
        L_0x0219:
            int r10 = r10 + 1
            goto L_0x012b
        L_0x021d:
            org.telegram.ui.ProfileActivity$AvatarImageView r1 = r0.avatarImage
            org.telegram.messenger.ImageReceiver r1 = r1.getImageReceiver()
            boolean r2 = org.telegram.ui.PhotoViewer.isShowingImage((org.telegram.tgnet.TLRPC$FileLocation) r8)
            r2 = r2 ^ r4
            r1.setVisible(r2, r7)
            goto L_0x05d5
        L_0x022d:
            int r5 = r0.chat_id
            if (r5 == 0) goto L_0x05d5
            int r5 = r0.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            int r8 = r0.chat_id
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            org.telegram.tgnet.TLRPC$Chat r5 = r5.getChat(r8)
            if (r5 == 0) goto L_0x0246
            r0.currentChat = r5
            goto L_0x0248
        L_0x0246:
            org.telegram.tgnet.TLRPC$Chat r5 = r0.currentChat
        L_0x0248:
            r10 = r5
            boolean r5 = org.telegram.messenger.ChatObject.isChannel(r10)
            java.lang.String r9 = "MegaPublic"
            java.lang.String r12 = "MegaPrivate"
            java.lang.String r14 = "MegaLocation"
            java.lang.String r15 = "OnlineCount"
            java.lang.String r3 = "%s, %s"
            java.lang.String r11 = "Subscribers"
            java.lang.String r8 = "Members"
            if (r5 == 0) goto L_0x037b
            org.telegram.tgnet.TLRPC$ChatFull r5 = r0.chatInfo
            if (r5 == 0) goto L_0x0345
            org.telegram.tgnet.TLRPC$Chat r13 = r0.currentChat
            boolean r7 = r13.megagroup
            if (r7 != 0) goto L_0x0279
            int r5 = r5.participants_count
            if (r5 == 0) goto L_0x0345
            boolean r5 = org.telegram.messenger.ChatObject.hasAdminRights(r13)
            if (r5 != 0) goto L_0x0345
            org.telegram.tgnet.TLRPC$ChatFull r5 = r0.chatInfo
            boolean r5 = r5.can_view_participants
            if (r5 == 0) goto L_0x0279
            goto L_0x0345
        L_0x0279:
            org.telegram.tgnet.TLRPC$Chat r5 = r0.currentChat
            boolean r5 = r5.megagroup
            if (r5 == 0) goto L_0x0311
            int r5 = r0.onlineCount
            if (r5 <= r4) goto L_0x02ca
            org.telegram.tgnet.TLRPC$ChatFull r5 = r0.chatInfo
            int r5 = r5.participants_count
            if (r5 == 0) goto L_0x02ca
            java.lang.Object[] r7 = new java.lang.Object[r2]
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r8, r5)
            r13 = 0
            r7[r13] = r5
            int r5 = r0.onlineCount
            org.telegram.tgnet.TLRPC$ChatFull r13 = r0.chatInfo
            int r13 = r13.participants_count
            int r5 = java.lang.Math.min(r5, r13)
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r15, r5)
            r7[r4] = r5
            java.lang.String r5 = java.lang.String.format(r3, r7)
            java.lang.Object[] r7 = new java.lang.Object[r2]
            org.telegram.tgnet.TLRPC$ChatFull r13 = r0.chatInfo
            int r13 = r13.participants_count
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatPluralStringComma(r8, r13)
            r19 = 0
            r7[r19] = r13
            int r13 = r0.onlineCount
            org.telegram.tgnet.TLRPC$ChatFull r2 = r0.chatInfo
            int r2 = r2.participants_count
            int r2 = java.lang.Math.min(r13, r2)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralStringComma(r15, r2)
            r7[r4] = r2
            java.lang.String r2 = java.lang.String.format(r3, r7)
            goto L_0x03cc
        L_0x02ca:
            org.telegram.tgnet.TLRPC$ChatFull r2 = r0.chatInfo
            int r2 = r2.participants_count
            if (r2 != 0) goto L_0x0303
            boolean r2 = r10.has_geo
            if (r2 == 0) goto L_0x02e1
            r2 = 2131625652(0x7f0e06b4, float:1.8878518E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r14, r2)
            java.lang.String r5 = r3.toLowerCase()
            goto L_0x038a
        L_0x02e1:
            java.lang.String r2 = r10.username
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x02f6
            r2 = 2131625656(0x7f0e06b8, float:1.8878526E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r9, r2)
            java.lang.String r5 = r3.toLowerCase()
            goto L_0x038a
        L_0x02f6:
            r2 = 2131625653(0x7f0e06b5, float:1.887852E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r12, r2)
            java.lang.String r5 = r3.toLowerCase()
            goto L_0x038a
        L_0x0303:
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r8, r2)
            org.telegram.tgnet.TLRPC$ChatFull r2 = r0.chatInfo
            int r2 = r2.participants_count
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralStringComma(r8, r2)
            goto L_0x03cc
        L_0x0311:
            int[] r2 = new int[r4]
            org.telegram.tgnet.TLRPC$ChatFull r3 = r0.chatInfo
            int r3 = r3.participants_count
            org.telegram.messenger.LocaleController.formatShortNumber(r3, r2)
            org.telegram.tgnet.TLRPC$Chat r2 = r0.currentChat
            boolean r2 = r2.megagroup
            if (r2 == 0) goto L_0x0331
            org.telegram.tgnet.TLRPC$ChatFull r2 = r0.chatInfo
            int r2 = r2.participants_count
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r8, r2)
            org.telegram.tgnet.TLRPC$ChatFull r3 = r0.chatInfo
            int r3 = r3.participants_count
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralStringComma(r8, r3)
            goto L_0x0341
        L_0x0331:
            org.telegram.tgnet.TLRPC$ChatFull r2 = r0.chatInfo
            int r2 = r2.participants_count
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r11, r2)
            org.telegram.tgnet.TLRPC$ChatFull r3 = r0.chatInfo
            int r3 = r3.participants_count
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralStringComma(r11, r3)
        L_0x0341:
            r5 = r2
            r2 = r3
            goto L_0x03cc
        L_0x0345:
            org.telegram.tgnet.TLRPC$Chat r2 = r0.currentChat
            boolean r2 = r2.megagroup
            if (r2 == 0) goto L_0x0359
            r2 = 2131625569(0x7f0e0661, float:1.887835E38)
            java.lang.String r3 = "Loading"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.String r5 = r2.toLowerCase()
            goto L_0x038a
        L_0x0359:
            int r2 = r10.flags
            r2 = r2 & 64
            if (r2 == 0) goto L_0x036d
            r2 = 2131624590(0x7f0e028e, float:1.8876364E38)
            java.lang.String r3 = "ChannelPublic"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.String r5 = r2.toLowerCase()
            goto L_0x038a
        L_0x036d:
            r2 = 2131624587(0x7f0e028b, float:1.8876358E38)
            java.lang.String r3 = "ChannelPrivate"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.String r5 = r2.toLowerCase()
            goto L_0x038a
        L_0x037b:
            boolean r2 = org.telegram.messenger.ChatObject.isKickedFromChat(r10)
            if (r2 == 0) goto L_0x038c
            r2 = 2131627419(0x7f0e0d9b, float:1.8882102E38)
            java.lang.String r3 = "YouWereKicked"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r3, r2)
        L_0x038a:
            r2 = r5
            goto L_0x03cc
        L_0x038c:
            boolean r2 = org.telegram.messenger.ChatObject.isLeftFromChat(r10)
            if (r2 == 0) goto L_0x039c
            r2 = 2131627418(0x7f0e0d9a, float:1.88821E38)
            java.lang.String r3 = "YouLeft"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r3, r2)
            goto L_0x038a
        L_0x039c:
            int r2 = r10.participants_count
            org.telegram.tgnet.TLRPC$ChatFull r5 = r0.chatInfo
            if (r5 == 0) goto L_0x03aa
            org.telegram.tgnet.TLRPC$ChatParticipants r2 = r5.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r2 = r2.participants
            int r2 = r2.size()
        L_0x03aa:
            if (r2 == 0) goto L_0x03c7
            int r5 = r0.onlineCount
            if (r5 <= r4) goto L_0x03c7
            r5 = 2
            java.lang.Object[] r7 = new java.lang.Object[r5]
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r8, r2)
            r5 = 0
            r7[r5] = r2
            int r2 = r0.onlineCount
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r15, r2)
            r7[r4] = r2
            java.lang.String r5 = java.lang.String.format(r3, r7)
            goto L_0x038a
        L_0x03c7:
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r8, r2)
            goto L_0x038a
        L_0x03cc:
            r3 = 0
            r7 = 0
        L_0x03ce:
            r13 = 2
            if (r3 >= r13) goto L_0x0591
            org.telegram.ui.ActionBar.SimpleTextView[] r15 = r0.nameTextView
            r20 = r15[r3]
            if (r20 != 0) goto L_0x03e5
            r22 = r1
            r23 = r2
            r21 = r5
            r17 = 2131625656(0x7f0e06b8, float:1.8878526E38)
            r18 = 2131625653(0x7f0e06b5, float:1.887852E38)
            goto L_0x0586
        L_0x03e5:
            java.lang.String r13 = r10.title
            if (r13 == 0) goto L_0x03f2
            r15 = r15[r3]
            boolean r13 = r15.setText(r13)
            if (r13 == 0) goto L_0x03f2
            r7 = 1
        L_0x03f2:
            org.telegram.ui.ActionBar.SimpleTextView[] r13 = r0.nameTextView
            r13 = r13[r3]
            r15 = 0
            r13.setLeftDrawable((android.graphics.drawable.Drawable) r15)
            if (r3 == 0) goto L_0x0425
            boolean r13 = r10.scam
            if (r13 == 0) goto L_0x040c
            org.telegram.ui.ActionBar.SimpleTextView[] r13 = r0.nameTextView
            r13 = r13[r3]
            android.graphics.drawable.Drawable r15 = r26.getScamDrawable()
            r13.setRightDrawable((android.graphics.drawable.Drawable) r15)
            goto L_0x0435
        L_0x040c:
            boolean r13 = r10.verified
            if (r13 == 0) goto L_0x041c
            org.telegram.ui.ActionBar.SimpleTextView[] r13 = r0.nameTextView
            r13 = r13[r3]
            android.graphics.drawable.Drawable r15 = r26.getVerifiedCrossfadeDrawable()
            r13.setRightDrawable((android.graphics.drawable.Drawable) r15)
            goto L_0x0435
        L_0x041c:
            org.telegram.ui.ActionBar.SimpleTextView[] r13 = r0.nameTextView
            r13 = r13[r3]
            r15 = 0
            r13.setRightDrawable((android.graphics.drawable.Drawable) r15)
            goto L_0x0435
        L_0x0425:
            r15 = 0
            boolean r13 = r10.scam
            if (r13 == 0) goto L_0x0438
            org.telegram.ui.ActionBar.SimpleTextView[] r13 = r0.nameTextView
            r13 = r13[r3]
            android.graphics.drawable.Drawable r15 = r26.getScamDrawable()
            r13.setRightDrawable((android.graphics.drawable.Drawable) r15)
        L_0x0435:
            r21 = r5
            goto L_0x0455
        L_0x0438:
            org.telegram.ui.ActionBar.SimpleTextView[] r13 = r0.nameTextView
            r13 = r13[r3]
            int r15 = r0.currentAccount
            org.telegram.messenger.MessagesController r15 = org.telegram.messenger.MessagesController.getInstance(r15)
            int r4 = r0.chat_id
            int r4 = -r4
            r21 = r5
            long r4 = (long) r4
            boolean r4 = r15.isDialogMuted(r4)
            if (r4 == 0) goto L_0x0451
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.chat_muteIconDrawable
            goto L_0x0452
        L_0x0451:
            r15 = 0
        L_0x0452:
            r13.setRightDrawable((android.graphics.drawable.Drawable) r15)
        L_0x0455:
            if (r3 != 0) goto L_0x046e
            if (r1 == 0) goto L_0x046e
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r0.onlineTextView
            r4 = r4[r3]
            r4.setText(r1)
        L_0x0460:
            r22 = r1
            r23 = r2
            r24 = r7
            r17 = 2131625656(0x7f0e06b8, float:1.8878526E38)
            r18 = 2131625653(0x7f0e06b5, float:1.887852E38)
            goto L_0x0584
        L_0x046e:
            org.telegram.tgnet.TLRPC$Chat r4 = r0.currentChat
            boolean r4 = r4.megagroup
            if (r4 == 0) goto L_0x048a
            org.telegram.tgnet.TLRPC$ChatFull r4 = r0.chatInfo
            if (r4 == 0) goto L_0x048a
            int r4 = r0.onlineCount
            if (r4 <= 0) goto L_0x048a
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r0.onlineTextView
            r4 = r4[r3]
            if (r3 != 0) goto L_0x0485
            r5 = r21
            goto L_0x0486
        L_0x0485:
            r5 = r2
        L_0x0486:
            r4.setText(r5)
            goto L_0x0460
        L_0x048a:
            if (r3 != 0) goto L_0x056a
            org.telegram.tgnet.TLRPC$Chat r4 = r0.currentChat
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r4)
            if (r4 == 0) goto L_0x056a
            org.telegram.tgnet.TLRPC$ChatFull r4 = r0.chatInfo
            if (r4 == 0) goto L_0x056a
            int r4 = r4.participants_count
            if (r4 == 0) goto L_0x056a
            org.telegram.tgnet.TLRPC$Chat r4 = r0.currentChat
            boolean r5 = r4.megagroup
            if (r5 != 0) goto L_0x04a6
            boolean r4 = r4.broadcast
            if (r4 == 0) goto L_0x056a
        L_0x04a6:
            r4 = 1
            int[] r5 = new int[r4]
            org.telegram.tgnet.TLRPC$ChatFull r4 = r0.chatInfo
            int r4 = r4.participants_count
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatShortNumber(r4, r5)
            org.telegram.tgnet.TLRPC$Chat r13 = r0.currentChat
            boolean r13 = r13.megagroup
            java.lang.String r15 = "%d"
            if (r13 == 0) goto L_0x053b
            org.telegram.tgnet.TLRPC$ChatFull r13 = r0.chatInfo
            int r13 = r13.participants_count
            if (r13 != 0) goto L_0x050c
            boolean r4 = r10.has_geo
            if (r4 == 0) goto L_0x04d6
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r0.onlineTextView
            r4 = r4[r3]
            r13 = 2131625652(0x7f0e06b4, float:1.8878518E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r14, r13)
            java.lang.String r5 = r5.toLowerCase()
            r4.setText(r5)
            goto L_0x0460
        L_0x04d6:
            r13 = 2131625652(0x7f0e06b4, float:1.8878518E38)
            java.lang.String r4 = r10.username
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x04f5
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r0.onlineTextView
            r4 = r4[r3]
            r5 = 2131625656(0x7f0e06b8, float:1.8878526E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r9, r5)
            java.lang.String r15 = r15.toLowerCase()
            r4.setText(r15)
            goto L_0x0460
        L_0x04f5:
            r5 = 2131625656(0x7f0e06b8, float:1.8878526E38)
            org.telegram.ui.ActionBar.SimpleTextView[] r4 = r0.onlineTextView
            r4 = r4[r3]
            r15 = 2131625653(0x7f0e06b5, float:1.887852E38)
            java.lang.String r17 = org.telegram.messenger.LocaleController.getString(r12, r15)
            java.lang.String r5 = r17.toLowerCase()
            r4.setText(r5)
            goto L_0x0460
        L_0x050c:
            r17 = 2131625656(0x7f0e06b8, float:1.8878526E38)
            r18 = 2131625653(0x7f0e06b5, float:1.887852E38)
            org.telegram.ui.ActionBar.SimpleTextView[] r13 = r0.onlineTextView
            r13 = r13[r3]
            r22 = r1
            r19 = 0
            r1 = r5[r19]
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r8, r1)
            r23 = r2
            r24 = r7
            r2 = 1
            java.lang.Object[] r7 = new java.lang.Object[r2]
            r2 = r5[r19]
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r7[r19] = r2
            java.lang.String r2 = java.lang.String.format(r15, r7)
            java.lang.String r1 = r1.replace(r2, r4)
            r13.setText(r1)
            goto L_0x0584
        L_0x053b:
            r22 = r1
            r23 = r2
            r24 = r7
            r17 = 2131625656(0x7f0e06b8, float:1.8878526E38)
            r18 = 2131625653(0x7f0e06b5, float:1.887852E38)
            r19 = 0
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r0.onlineTextView
            r1 = r1[r3]
            r2 = r5[r19]
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r11, r2)
            r7 = 1
            java.lang.Object[] r13 = new java.lang.Object[r7]
            r5 = r5[r19]
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r13[r19] = r5
            java.lang.String r5 = java.lang.String.format(r15, r13)
            java.lang.String r2 = r2.replace(r5, r4)
            r1.setText(r2)
            goto L_0x0584
        L_0x056a:
            r22 = r1
            r23 = r2
            r24 = r7
            r17 = 2131625656(0x7f0e06b8, float:1.8878526E38)
            r18 = 2131625653(0x7f0e06b5, float:1.887852E38)
            org.telegram.ui.ActionBar.SimpleTextView[] r1 = r0.onlineTextView
            r1 = r1[r3]
            if (r3 != 0) goto L_0x057f
            r2 = r21
            goto L_0x0581
        L_0x057f:
            r2 = r23
        L_0x0581:
            r1.setText(r2)
        L_0x0584:
            r7 = r24
        L_0x0586:
            int r3 = r3 + 1
            r5 = r21
            r1 = r22
            r2 = r23
            r4 = 1
            goto L_0x03ce
        L_0x0591:
            if (r7 == 0) goto L_0x0596
            r26.needLayout()
        L_0x0596:
            org.telegram.tgnet.TLRPC$ChatPhoto r1 = r10.photo
            if (r1 == 0) goto L_0x059d
            org.telegram.tgnet.TLRPC$FileLocation r3 = r1.photo_big
            goto L_0x059e
        L_0x059d:
            r3 = 0
        L_0x059e:
            org.telegram.ui.Components.AvatarDrawable r1 = r0.avatarDrawable
            r1.setInfo((org.telegram.tgnet.TLRPC$Chat) r10)
            r1 = 1
            org.telegram.messenger.ImageLocation r9 = org.telegram.messenger.ImageLocation.getForChat(r10, r1)
            r1 = 0
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForChat(r10, r1)
            org.telegram.ui.Components.ProfileGalleryView r1 = r0.avatarsViewPager
            r1.initIfEmpty(r9, r2)
            org.telegram.ui.ProfileActivity$AvatarImageView r1 = r0.avatarImage
            org.telegram.ui.Components.AvatarDrawable r4 = r0.avatarDrawable
            r1.setImage((org.telegram.messenger.ImageLocation) r2, (java.lang.String) r6, (android.graphics.drawable.Drawable) r4, (java.lang.Object) r10)
            int r1 = r0.currentAccount
            org.telegram.messenger.FileLoader r8 = org.telegram.messenger.FileLoader.getInstance(r1)
            r11 = 0
            r12 = 0
            r13 = 1
            r8.loadFile(r9, r10, r11, r12, r13)
            org.telegram.ui.ProfileActivity$AvatarImageView r1 = r0.avatarImage
            org.telegram.messenger.ImageReceiver r1 = r1.getImageReceiver()
            boolean r2 = org.telegram.ui.PhotoViewer.isShowingImage((org.telegram.tgnet.TLRPC$FileLocation) r3)
            r3 = 1
            r2 = r2 ^ r3
            r3 = 0
            r1.setVisible(r2, r3)
        L_0x05d5:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.updateProfileData():void");
    }

    private void createActionBarMenu() {
        boolean z;
        TLRPC$ChatFull tLRPC$ChatFull;
        int i;
        String str;
        int i2;
        ActionBar actionBar = this.actionBar;
        if (actionBar != null && this.otherItem != null) {
            actionBar.createMenu();
            this.otherItem.removeAllSubItems();
            this.animatingItem = null;
            this.editItemVisible = false;
            this.callItemVisible = false;
            this.canSearchMembers = false;
            if (this.user_id != 0) {
                TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
                if (UserConfig.getInstance(this.currentAccount).getClientUserId() == this.user_id) {
                    this.otherItem.addSubItem(3, NUM, LocaleController.getString("ShareContact", NUM));
                } else if (user != null) {
                    TLRPC$UserFull tLRPC$UserFull = this.userInfo;
                    if (tLRPC$UserFull != null && tLRPC$UserFull.phone_calls_available) {
                        this.callItemVisible = true;
                    }
                    if (!this.isBot && ContactsController.getInstance(this.currentAccount).contactsDict.get(Integer.valueOf(this.user_id)) != null) {
                        if (!TextUtils.isEmpty(user.phone)) {
                            this.otherItem.addSubItem(3, NUM, LocaleController.getString("ShareContact", NUM));
                        }
                        ActionBarMenuItem actionBarMenuItem = this.otherItem;
                        boolean z2 = this.userBlocked;
                        actionBarMenuItem.addSubItem(2, NUM, !this.userBlocked ? LocaleController.getString("BlockContact", NUM) : LocaleController.getString("Unblock", NUM));
                        this.otherItem.addSubItem(4, NUM, LocaleController.getString("EditContact", NUM));
                        this.otherItem.addSubItem(5, NUM, LocaleController.getString("DeleteContact", NUM));
                    } else if (!MessagesController.isSupportUser(user)) {
                        if (this.isBot) {
                            if (!user.bot_nochats) {
                                this.otherItem.addSubItem(9, NUM, LocaleController.getString("BotInvite", NUM));
                            }
                            this.otherItem.addSubItem(10, NUM, LocaleController.getString("BotShare", NUM));
                        } else {
                            this.otherItem.addSubItem(1, NUM, LocaleController.getString("AddContact", NUM));
                        }
                        if (!TextUtils.isEmpty(user.phone)) {
                            this.otherItem.addSubItem(3, NUM, LocaleController.getString("ShareContact", NUM));
                        }
                        if (this.isBot) {
                            ActionBarMenuItem actionBarMenuItem2 = this.otherItem;
                            int i3 = !this.userBlocked ? NUM : NUM;
                            if (!this.userBlocked) {
                                i2 = NUM;
                                str = "BotStop";
                            } else {
                                i2 = NUM;
                                str = "BotRestart";
                            }
                            actionBarMenuItem2.addSubItem(2, i3, LocaleController.getString(str, i2));
                        } else {
                            ActionBarMenuItem actionBarMenuItem3 = this.otherItem;
                            boolean z3 = this.userBlocked;
                            actionBarMenuItem3.addSubItem(2, NUM, !this.userBlocked ? LocaleController.getString("BlockContact", NUM) : LocaleController.getString("Unblock", NUM));
                        }
                    } else if (this.userBlocked) {
                        this.otherItem.addSubItem(2, NUM, LocaleController.getString("Unblock", NUM));
                    }
                } else {
                    return;
                }
                if (!UserObject.isDeleted(user) && !this.isBot && this.currentEncryptedChat == null && this.user_id != getUserConfig().getClientUserId() && !this.userBlocked && (i = this.user_id) != 333000 && i != 777000 && i != 42777) {
                    this.otherItem.addSubItem(20, NUM, LocaleController.getString("StartEncryptedChat", NUM));
                }
            } else {
                int i4 = this.chat_id;
                if (i4 != 0 && i4 > 0) {
                    TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id));
                    if (ChatObject.isChannel(chat)) {
                        if (ChatObject.hasAdminRights(chat) || (chat.megagroup && ChatObject.canChangeChatInfo(chat))) {
                            this.editItemVisible = true;
                        }
                        if (!chat.megagroup && (tLRPC$ChatFull = this.chatInfo) != null && tLRPC$ChatFull.can_view_stats) {
                            this.otherItem.addSubItem(19, NUM, LocaleController.getString("Statistics", NUM));
                        }
                        if (chat.megagroup) {
                            this.canSearchMembers = true;
                            this.otherItem.addSubItem(17, NUM, LocaleController.getString("SearchMembers", NUM));
                            if (!chat.creator && !chat.left && !chat.kicked) {
                                this.otherItem.addSubItem(7, NUM, LocaleController.getString("LeaveMegaMenu", NUM));
                            }
                        } else {
                            if (!TextUtils.isEmpty(chat.username)) {
                                this.otherItem.addSubItem(10, NUM, LocaleController.getString("BotShare", NUM));
                            }
                            TLRPC$Chat tLRPC$Chat = this.currentChat;
                            if (!tLRPC$Chat.creator && !tLRPC$Chat.left && !tLRPC$Chat.kicked) {
                                this.otherItem.addSubItem(7, NUM, LocaleController.getString("LeaveChannelMenu", NUM));
                            }
                        }
                    } else {
                        if (ChatObject.canChangeChatInfo(chat)) {
                            z = true;
                            this.editItemVisible = true;
                        } else {
                            z = true;
                        }
                        if (!ChatObject.isKickedFromChat(chat) && !ChatObject.isLeftFromChat(chat)) {
                            this.canSearchMembers = z;
                            this.otherItem.addSubItem(17, NUM, LocaleController.getString("SearchMembers", NUM));
                        }
                        this.otherItem.addSubItem(7, NUM, LocaleController.getString("DeleteAndExit", NUM));
                    }
                }
            }
            this.otherItem.addSubItem(14, NUM, LocaleController.getString("AddShortcut", NUM));
            this.otherItem.addSubItem(21, NUM, LocaleController.getString("SaveToGallery", NUM));
            if (!this.isPulledDown) {
                this.otherItem.hideSubItem(21);
            }
            this.otherItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
            if (this.callItemVisible) {
                if (this.callItem.getVisibility() != 0) {
                    this.callItem.setVisibility(0);
                }
            } else if (this.callItem.getVisibility() != 8) {
                this.callItem.setVisibility(8);
            }
            if (this.editItemVisible) {
                if (this.editItem.getVisibility() != 0) {
                    this.editItem.setVisibility(0);
                }
            } else if (this.editItem.getVisibility() != 8) {
                this.editItem.setVisibility(8);
            }
            ActionBarMenuItem actionBarMenuItem4 = this.editItem;
            if (actionBarMenuItem4 != null) {
                actionBarMenuItem4.setContentDescription(LocaleController.getString("Edit", NUM));
            }
            ActionBarMenuItem actionBarMenuItem5 = this.callItem;
            if (actionBarMenuItem5 != null) {
                actionBarMenuItem5.setContentDescription(LocaleController.getString("Call", NUM));
            }
            PagerIndicatorView pagerIndicatorView = this.avatarsViewPagerIndicatorView;
            if (pagerIndicatorView != null && pagerIndicatorView.isIndicatorFullyVisible()) {
                if (this.editItemVisible) {
                    this.editItem.setVisibility(8);
                }
                if (this.callItemVisible) {
                    this.callItem.setVisibility(8);
                }
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
            SendMessagesHelper.getInstance(this.currentAccount).sendMessage(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id)), longValue, (MessageObject) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
        }
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        TLRPC$User user;
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
            switch (i) {
                case 1:
                    view = new HeaderCell(this.mContext, 23);
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
                    view = new NotificationsCheckCell(this.mContext, 23, 70, false);
                    break;
                case 7:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 8:
                    view = new UserCell(this.mContext, ProfileActivity.this.addMemberRow == -1 ? 9 : 6, 0, true);
                    break;
                case 11:
                    view = new View(this, this.mContext) {
                        /* access modifiers changed from: protected */
                        public void onMeasure(int i, int i2) {
                            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), NUM));
                        }
                    };
                    break;
                case 12:
                    view = new View(this.mContext) {
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
                                    if (ProfileActivity.this.listView.getChildAdapterPosition(ProfileActivity.this.listView.getChildAt(i5)) != ProfileActivity.this.bottomPaddingRow) {
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
                    break;
                case 13:
                    if (ProfileActivity.this.sharedMediaLayout.getParent() != null) {
                        ((ViewGroup) ProfileActivity.this.sharedMediaLayout.getParent()).removeView(ProfileActivity.this.sharedMediaLayout);
                    }
                    view = ProfileActivity.this.sharedMediaLayout;
                    break;
                default:
                    view = null;
                    break;
            }
            if (i != 13) {
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            }
            return new RecyclerListView.Holder(view);
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

        /* JADX WARNING: Removed duplicated region for block: B:96:0x02b6  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r14, int r15) {
            /*
                r13 = this;
                int r0 = r14.getItemViewType()
                r1 = 2131624560(0x7f0e0270, float:1.8876303E38)
                java.lang.String r2 = "ChannelMembers"
                r3 = 0
                r4 = -1
                r5 = 0
                r6 = 1
                switch(r0) {
                    case 1: goto L_0x071f;
                    case 2: goto L_0x05e5;
                    case 3: goto L_0x0599;
                    case 4: goto L_0x02cd;
                    case 5: goto L_0x0010;
                    case 6: goto L_0x015c;
                    case 7: goto L_0x00d3;
                    case 8: goto L_0x0012;
                    default: goto L_0x0010;
                }
            L_0x0010:
                goto L_0x0772
            L_0x0012:
                android.view.View r14 = r14.itemView
                r7 = r14
                org.telegram.ui.Cells.UserCell r7 = (org.telegram.ui.Cells.UserCell) r7
                org.telegram.ui.ProfileActivity r14 = org.telegram.ui.ProfileActivity.this
                java.util.ArrayList r14 = r14.sortedUsers
                boolean r14 = r14.isEmpty()
                if (r14 != 0) goto L_0x004c
                org.telegram.ui.ProfileActivity r14 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r14 = r14.chatInfo
                org.telegram.tgnet.TLRPC$ChatParticipants r14 = r14.participants
                java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r14 = r14.participants
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                java.util.ArrayList r0 = r0.sortedUsers
                org.telegram.ui.ProfileActivity r1 = org.telegram.ui.ProfileActivity.this
                int r1 = r1.membersStartRow
                int r1 = r15 - r1
                java.lang.Object r0 = r0.get(r1)
                java.lang.Integer r0 = (java.lang.Integer) r0
                int r0 = r0.intValue()
                java.lang.Object r14 = r14.get(r0)
                org.telegram.tgnet.TLRPC$ChatParticipant r14 = (org.telegram.tgnet.TLRPC$ChatParticipant) r14
                goto L_0x0064
            L_0x004c:
                org.telegram.ui.ProfileActivity r14 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r14 = r14.chatInfo
                org.telegram.tgnet.TLRPC$ChatParticipants r14 = r14.participants
                java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r14 = r14.participants
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                int r0 = r0.membersStartRow
                int r0 = r15 - r0
                java.lang.Object r14 = r14.get(r0)
                org.telegram.tgnet.TLRPC$ChatParticipant r14 = (org.telegram.tgnet.TLRPC$ChatParticipant) r14
            L_0x0064:
                if (r14 == 0) goto L_0x0772
                boolean r0 = r14 instanceof org.telegram.tgnet.TLRPC$TL_chatChannelParticipant
                r1 = 2131624523(0x7f0e024b, float:1.8876228E38)
                java.lang.String r2 = "ChannelAdmin"
                r4 = 2131624540(0x7f0e025c, float:1.8876263E38)
                java.lang.String r8 = "ChannelCreator"
                if (r0 == 0) goto L_0x0097
                r0 = r14
                org.telegram.tgnet.TLRPC$TL_chatChannelParticipant r0 = (org.telegram.tgnet.TLRPC$TL_chatChannelParticipant) r0
                org.telegram.tgnet.TLRPC$ChannelParticipant r0 = r0.channelParticipant
                java.lang.String r9 = r0.rank
                boolean r9 = android.text.TextUtils.isEmpty(r9)
                if (r9 != 0) goto L_0x0085
                java.lang.String r0 = r0.rank
            L_0x0083:
                r3 = r0
                goto L_0x00a8
            L_0x0085:
                boolean r9 = r0 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantCreator
                if (r9 == 0) goto L_0x008e
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r8, r4)
                goto L_0x0083
            L_0x008e:
                boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin
                if (r0 == 0) goto L_0x00a8
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r1)
                goto L_0x0083
            L_0x0097:
                boolean r0 = r14 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantCreator
                if (r0 == 0) goto L_0x00a0
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r8, r4)
                goto L_0x00a8
            L_0x00a0:
                boolean r0 = r14 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantAdmin
                if (r0 == 0) goto L_0x00a8
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r2, r1)
            L_0x00a8:
                r7.setAdminRole(r3)
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                int r0 = r0.currentAccount
                org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
                int r14 = r14.user_id
                java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
                org.telegram.tgnet.TLRPC$User r8 = r0.getUser(r14)
                r9 = 0
                r10 = 0
                r11 = 0
                org.telegram.ui.ProfileActivity r14 = org.telegram.ui.ProfileActivity.this
                int r14 = r14.membersEndRow
                int r14 = r14 - r6
                if (r15 == r14) goto L_0x00cd
                r12 = 1
                goto L_0x00ce
            L_0x00cd:
                r12 = 0
            L_0x00ce:
                r7.setData(r8, r9, r10, r11, r12)
                goto L_0x0772
            L_0x00d3:
                android.view.View r14 = r14.itemView
                java.lang.Integer r0 = java.lang.Integer.valueOf(r15)
                r14.setTag(r0)
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                int r0 = r0.infoSectionRow
                java.lang.String r1 = "windowBackgroundGrayShadow"
                if (r15 != r0) goto L_0x0107
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                int r0 = r0.lastSectionRow
                if (r0 != r4) goto L_0x0107
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                int r0 = r0.settingsSectionRow
                if (r0 != r4) goto L_0x0107
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                int r0 = r0.sharedMediaRow
                if (r0 != r4) goto L_0x0107
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                int r0 = r0.membersSectionRow
                if (r0 == r4) goto L_0x013a
            L_0x0107:
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                int r0 = r0.settingsSectionRow
                if (r15 == r0) goto L_0x013a
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                int r0 = r0.lastSectionRow
                if (r15 == r0) goto L_0x013a
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                int r0 = r0.membersSectionRow
                if (r15 != r0) goto L_0x0130
                org.telegram.ui.ProfileActivity r15 = org.telegram.ui.ProfileActivity.this
                int r15 = r15.lastSectionRow
                if (r15 != r4) goto L_0x0130
                org.telegram.ui.ProfileActivity r15 = org.telegram.ui.ProfileActivity.this
                int r15 = r15.sharedMediaRow
                if (r15 != r4) goto L_0x0130
                goto L_0x013a
            L_0x0130:
                android.content.Context r15 = r13.mContext
                r0 = 2131165417(0x7var_e9, float:1.794505E38)
                android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r15, (int) r0, (java.lang.String) r1)
                goto L_0x0143
            L_0x013a:
                android.content.Context r15 = r13.mContext
                r0 = 2131165418(0x7var_ea, float:1.7945053E38)
                android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r15, (int) r0, (java.lang.String) r1)
            L_0x0143:
                org.telegram.ui.Components.CombinedDrawable r0 = new org.telegram.ui.Components.CombinedDrawable
                android.graphics.drawable.ColorDrawable r1 = new android.graphics.drawable.ColorDrawable
                java.lang.String r2 = "windowBackgroundGray"
                int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                r1.<init>(r2)
                r0.<init>(r1, r15)
                r0.setFullsize(r6)
                r14.setBackgroundDrawable(r0)
                goto L_0x0772
            L_0x015c:
                android.view.View r14 = r14.itemView
                org.telegram.ui.Cells.NotificationsCheckCell r14 = (org.telegram.ui.Cells.NotificationsCheckCell) r14
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                int r0 = r0.notificationsRow
                if (r15 != r0) goto L_0x0772
                org.telegram.ui.ProfileActivity r15 = org.telegram.ui.ProfileActivity.this
                int r15 = r15.currentAccount
                android.content.SharedPreferences r15 = org.telegram.messenger.MessagesController.getNotificationsSettings(r15)
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                long r0 = r0.dialog_id
                r7 = 0
                int r2 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
                if (r2 == 0) goto L_0x0185
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                long r0 = r0.dialog_id
                goto L_0x019c
            L_0x0185:
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                int r0 = r0.user_id
                if (r0 == 0) goto L_0x0194
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                int r0 = r0.user_id
                goto L_0x019b
            L_0x0194:
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                int r0 = r0.chat_id
                int r0 = -r0
            L_0x019b:
                long r0 = (long) r0
            L_0x019c:
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                java.lang.String r4 = "custom_"
                r2.append(r4)
                r2.append(r0)
                java.lang.String r2 = r2.toString()
                boolean r2 = r15.getBoolean(r2, r5)
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                java.lang.String r7 = "notify2_"
                r4.append(r7)
                r4.append(r0)
                java.lang.String r4 = r4.toString()
                boolean r4 = r15.contains(r4)
                java.lang.StringBuilder r8 = new java.lang.StringBuilder
                r8.<init>()
                r8.append(r7)
                r8.append(r0)
                java.lang.String r7 = r8.toString()
                int r7 = r15.getInt(r7, r5)
                java.lang.StringBuilder r8 = new java.lang.StringBuilder
                r8.<init>()
                java.lang.String r9 = "notifyuntil_"
                r8.append(r9)
                r8.append(r0)
                java.lang.String r8 = r8.toString()
                int r15 = r15.getInt(r8, r5)
                r8 = 3
                if (r7 != r8) goto L_0x027a
                r8 = 2147483647(0x7fffffff, float:NaN)
                if (r15 == r8) goto L_0x027a
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                int r0 = r0.currentAccount
                org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)
                int r0 = r0.getCurrentTime()
                int r15 = r15 - r0
                if (r15 > 0) goto L_0x021f
                if (r2 == 0) goto L_0x0213
                r15 = 2131625942(0x7f0e07d6, float:1.8879106E38)
                java.lang.String r0 = "NotificationsCustom"
                java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r0, r15)
                goto L_0x021c
            L_0x0213:
                r15 = 2131625966(0x7f0e07ee, float:1.8879155E38)
                java.lang.String r0 = "NotificationsOn"
                java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r0, r15)
            L_0x021c:
                r3 = r15
                goto L_0x02b4
            L_0x021f:
                r0 = 3600(0xe10, float:5.045E-42)
                r1 = 2131627400(0x7f0e0d88, float:1.8882063E38)
                java.lang.String r2 = "WillUnmuteIn"
                if (r15 >= r0) goto L_0x023b
                java.lang.Object[] r0 = new java.lang.Object[r6]
                int r15 = r15 / 60
                java.lang.String r3 = "Minutes"
                java.lang.String r15 = org.telegram.messenger.LocaleController.formatPluralString(r3, r15)
                r0[r5] = r15
                java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r2, r1, r0)
            L_0x0238:
                r6 = 0
                goto L_0x02b4
            L_0x023b:
                r0 = 86400(0x15180, float:1.21072E-40)
                r4 = 1114636288(0x42700000, float:60.0)
                if (r15 >= r0) goto L_0x025a
                java.lang.Object[] r0 = new java.lang.Object[r6]
                float r15 = (float) r15
                float r15 = r15 / r4
                float r15 = r15 / r4
                double r3 = (double) r15
                double r3 = java.lang.Math.ceil(r3)
                int r15 = (int) r3
                java.lang.String r3 = "Hours"
                java.lang.String r15 = org.telegram.messenger.LocaleController.formatPluralString(r3, r15)
                r0[r5] = r15
                java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r2, r1, r0)
                goto L_0x0238
            L_0x025a:
                r0 = 31536000(0x1e13380, float:8.2725845E-38)
                if (r15 >= r0) goto L_0x0238
                java.lang.Object[] r0 = new java.lang.Object[r6]
                float r15 = (float) r15
                float r15 = r15 / r4
                float r15 = r15 / r4
                r3 = 1103101952(0x41CLASSNAME, float:24.0)
                float r15 = r15 / r3
                double r3 = (double) r15
                double r3 = java.lang.Math.ceil(r3)
                int r15 = (int) r3
                java.lang.String r3 = "Days"
                java.lang.String r15 = org.telegram.messenger.LocaleController.formatPluralString(r3, r15)
                r0[r5] = r15
                java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r2, r1, r0)
                goto L_0x0238
            L_0x027a:
                if (r7 != 0) goto L_0x028e
                if (r4 == 0) goto L_0x027f
                goto L_0x0293
            L_0x027f:
                org.telegram.ui.ProfileActivity r15 = org.telegram.ui.ProfileActivity.this
                int r15 = r15.currentAccount
                org.telegram.messenger.NotificationsController r15 = org.telegram.messenger.NotificationsController.getInstance(r15)
                boolean r6 = r15.isGlobalNotificationsEnabled((long) r0)
                goto L_0x0293
            L_0x028e:
                if (r7 != r6) goto L_0x0291
                goto L_0x0293
            L_0x0291:
                r15 = 2
                r6 = 0
            L_0x0293:
                if (r6 == 0) goto L_0x02a1
                if (r2 == 0) goto L_0x02a1
                r15 = 2131625942(0x7f0e07d6, float:1.8879106E38)
                java.lang.String r0 = "NotificationsCustom"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r0, r15)
                goto L_0x02b4
            L_0x02a1:
                if (r6 == 0) goto L_0x02a9
                r15 = 2131625966(0x7f0e07ee, float:1.8879155E38)
                java.lang.String r0 = "NotificationsOn"
                goto L_0x02ae
            L_0x02a9:
                r15 = 2131625964(0x7f0e07ec, float:1.887915E38)
                java.lang.String r0 = "NotificationsOff"
            L_0x02ae:
                java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r0, r15)
                goto L_0x021c
            L_0x02b4:
                if (r3 != 0) goto L_0x02bf
                r15 = 2131625964(0x7f0e07ec, float:1.887915E38)
                java.lang.String r0 = "NotificationsOff"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r0, r15)
            L_0x02bf:
                r15 = 2131625938(0x7f0e07d2, float:1.8879098E38)
                java.lang.String r0 = "Notifications"
                java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r0, r15)
                r14.setTextAndValueAndCheck(r15, r3, r6, r5)
                goto L_0x0772
            L_0x02cd:
                android.view.View r14 = r14.itemView
                org.telegram.ui.Cells.TextCell r14 = (org.telegram.ui.Cells.TextCell) r14
                java.lang.String r0 = "windowBackgroundWhiteBlackText"
                java.lang.String r4 = "windowBackgroundWhiteGrayIcon"
                r14.setColors(r4, r0)
                r14.setTag(r0)
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                int r0 = r0.settingsTimerRow
                r4 = 32
                if (r15 != r0) goto L_0x0321
                org.telegram.ui.ProfileActivity r15 = org.telegram.ui.ProfileActivity.this
                int r15 = r15.currentAccount
                org.telegram.messenger.MessagesController r15 = org.telegram.messenger.MessagesController.getInstance(r15)
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                long r0 = r0.dialog_id
                long r0 = r0 >> r4
                int r1 = (int) r0
                java.lang.Integer r0 = java.lang.Integer.valueOf(r1)
                org.telegram.tgnet.TLRPC$EncryptedChat r15 = r15.getEncryptedChat(r0)
                int r15 = r15.ttl
                if (r15 != 0) goto L_0x030f
                r15 = 2131626769(0x7f0e0b11, float:1.8880784E38)
                java.lang.String r0 = "ShortMessageLifetimeForever"
                java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r0, r15)
                goto L_0x0313
            L_0x030f:
                java.lang.String r15 = org.telegram.messenger.LocaleController.formatTTLString(r15)
            L_0x0313:
                r0 = 2131625673(0x7f0e06c9, float:1.887856E38)
                java.lang.String r1 = "MessageLifetime"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r14.setTextAndValue(r0, r15, r5)
                goto L_0x0772
            L_0x0321:
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                int r0 = r0.unblockRow
                java.lang.String r7 = "windowBackgroundWhiteRedText5"
                if (r15 != r0) goto L_0x033d
                r15 = 2131627021(0x7f0e0c0d, float:1.8881295E38)
                java.lang.String r0 = "Unblock"
                java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r0, r15)
                r14.setText(r15, r5)
                r14.setColors(r3, r7)
                goto L_0x0772
            L_0x033d:
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                int r0 = r0.settingsKeyRow
                if (r15 != r0) goto L_0x0375
                org.telegram.ui.Components.IdenticonDrawable r15 = new org.telegram.ui.Components.IdenticonDrawable
                r15.<init>()
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                int r0 = r0.currentAccount
                org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
                org.telegram.ui.ProfileActivity r1 = org.telegram.ui.ProfileActivity.this
                long r1 = r1.dialog_id
                long r1 = r1 >> r4
                int r2 = (int) r1
                java.lang.Integer r1 = java.lang.Integer.valueOf(r2)
                org.telegram.tgnet.TLRPC$EncryptedChat r0 = r0.getEncryptedChat(r1)
                r15.setEncryptedChat(r0)
                r0 = 2131625050(0x7f0e045a, float:1.8877297E38)
                java.lang.String r1 = "EncryptionKey"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r14.setTextAndValueDrawable(r0, r15, r5)
                goto L_0x0772
            L_0x0375:
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                int r0 = r0.joinRow
                if (r15 != r0) goto L_0x03b0
                java.lang.String r15 = "windowBackgroundWhiteBlueText2"
                r14.setColors(r3, r15)
                int r15 = org.telegram.ui.ActionBar.Theme.getColor(r15)
                r14.setTextColor(r15)
                org.telegram.ui.ProfileActivity r15 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r15 = r15.currentChat
                boolean r15 = r15.megagroup
                if (r15 == 0) goto L_0x03a2
                r15 = 2131626440(0x7f0e09c8, float:1.8880116E38)
                java.lang.String r0 = "ProfileJoinGroup"
                java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r0, r15)
                r14.setText(r15, r5)
                goto L_0x0772
            L_0x03a2:
                r15 = 2131626439(0x7f0e09c7, float:1.8880114E38)
                java.lang.String r0 = "ProfileJoinChannel"
                java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r0, r15)
                r14.setText(r15, r5)
                goto L_0x0772
            L_0x03b0:
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                int r0 = r0.subscribersRow
                java.lang.String r4 = "%d"
                if (r15 != r0) goto L_0x046f
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r0 = r0.chatInfo
                r3 = 2131165256(0x7var_, float:1.7944724E38)
                if (r0 == 0) goto L_0x042e
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r0 = r0.currentChat
                boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
                if (r0 == 0) goto L_0x0407
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r0 = r0.currentChat
                boolean r0 = r0.megagroup
                if (r0 != 0) goto L_0x0407
                r0 = 2131624604(0x7f0e029c, float:1.8876392E38)
                java.lang.String r1 = "ChannelSubscribers"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                java.lang.Object[] r1 = new java.lang.Object[r6]
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r2 = r2.chatInfo
                int r2 = r2.participants_count
                java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
                r1[r5] = r2
                java.lang.String r1 = java.lang.String.format(r4, r1)
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                int r2 = r2.membersSectionRow
                int r2 = r2 - r6
                if (r15 == r2) goto L_0x0402
                r5 = 1
            L_0x0402:
                r14.setTextAndValueAndIcon(r0, r1, r3, r5)
                goto L_0x0772
            L_0x0407:
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r1)
                java.lang.Object[] r1 = new java.lang.Object[r6]
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r2 = r2.chatInfo
                int r2 = r2.participants_count
                java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
                r1[r5] = r2
                java.lang.String r1 = java.lang.String.format(r4, r1)
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                int r2 = r2.membersSectionRow
                int r2 = r2 - r6
                if (r15 == r2) goto L_0x0429
                r5 = 1
            L_0x0429:
                r14.setTextAndValueAndIcon(r0, r1, r3, r5)
                goto L_0x0772
            L_0x042e:
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r0 = r0.currentChat
                boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
                if (r0 == 0) goto L_0x045c
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r0 = r0.currentChat
                boolean r0 = r0.megagroup
                if (r0 != 0) goto L_0x045c
                r0 = 2131624604(0x7f0e029c, float:1.8876392E38)
                java.lang.String r1 = "ChannelSubscribers"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                org.telegram.ui.ProfileActivity r1 = org.telegram.ui.ProfileActivity.this
                int r1 = r1.membersSectionRow
                int r1 = r1 - r6
                if (r15 == r1) goto L_0x0457
                r5 = 1
            L_0x0457:
                r14.setTextAndIcon((java.lang.String) r0, (int) r3, (boolean) r5)
                goto L_0x0772
            L_0x045c:
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r1)
                org.telegram.ui.ProfileActivity r1 = org.telegram.ui.ProfileActivity.this
                int r1 = r1.membersSectionRow
                int r1 = r1 - r6
                if (r15 == r1) goto L_0x046a
                r5 = 1
            L_0x046a:
                r14.setTextAndIcon((java.lang.String) r0, (int) r3, (boolean) r5)
                goto L_0x0772
            L_0x046f:
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                int r0 = r0.administratorsRow
                if (r15 != r0) goto L_0x04c9
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r0 = r0.chatInfo
                if (r0 == 0) goto L_0x04ae
                r0 = 2131624525(0x7f0e024d, float:1.8876232E38)
                java.lang.String r1 = "ChannelAdministrators"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                java.lang.Object[] r1 = new java.lang.Object[r6]
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r2 = r2.chatInfo
                int r2 = r2.admins_count
                java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
                r1[r5] = r2
                java.lang.String r1 = java.lang.String.format(r4, r1)
                r2 = 2131165247(0x7var_f, float:1.7944706E38)
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.membersSectionRow
                int r3 = r3 - r6
                if (r15 == r3) goto L_0x04a9
                r5 = 1
            L_0x04a9:
                r14.setTextAndValueAndIcon(r0, r1, r2, r5)
                goto L_0x0772
            L_0x04ae:
                r0 = 2131624525(0x7f0e024d, float:1.8876232E38)
                java.lang.String r1 = "ChannelAdministrators"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r1 = 2131165247(0x7var_f, float:1.7944706E38)
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                int r2 = r2.membersSectionRow
                int r2 = r2 - r6
                if (r15 == r2) goto L_0x04c4
                r5 = 1
            L_0x04c4:
                r14.setTextAndIcon((java.lang.String) r0, (int) r1, (boolean) r5)
                goto L_0x0772
            L_0x04c9:
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                int r0 = r0.blockedUsersRow
                if (r15 != r0) goto L_0x052f
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r0 = r0.chatInfo
                if (r0 == 0) goto L_0x0514
                r0 = 2131624530(0x7f0e0252, float:1.8876242E38)
                java.lang.String r1 = "ChannelBlacklist"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                java.lang.Object[] r1 = new java.lang.Object[r6]
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r2 = r2.chatInfo
                int r2 = r2.banned_count
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r3 = r3.chatInfo
                int r3 = r3.kicked_count
                int r2 = java.lang.Math.max(r2, r3)
                java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
                r1[r5] = r2
                java.lang.String r1 = java.lang.String.format(r4, r1)
                r2 = 2131165254(0x7var_, float:1.794472E38)
                org.telegram.ui.ProfileActivity r3 = org.telegram.ui.ProfileActivity.this
                int r3 = r3.membersSectionRow
                int r3 = r3 - r6
                if (r15 == r3) goto L_0x050f
                r5 = 1
            L_0x050f:
                r14.setTextAndValueAndIcon(r0, r1, r2, r5)
                goto L_0x0772
            L_0x0514:
                r0 = 2131624530(0x7f0e0252, float:1.8876242E38)
                java.lang.String r1 = "ChannelBlacklist"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r1 = 2131165254(0x7var_, float:1.794472E38)
                org.telegram.ui.ProfileActivity r2 = org.telegram.ui.ProfileActivity.this
                int r2 = r2.membersSectionRow
                int r2 = r2 - r6
                if (r15 == r2) goto L_0x052a
                r5 = 1
            L_0x052a:
                r14.setTextAndIcon((java.lang.String) r0, (int) r1, (boolean) r5)
                goto L_0x0772
            L_0x052f:
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                int r0 = r0.addMemberRow
                if (r15 != r0) goto L_0x056a
                java.lang.String r15 = "windowBackgroundWhiteBlueIcon"
                java.lang.String r0 = "windowBackgroundWhiteBlueButton"
                r14.setColors(r15, r0)
                org.telegram.ui.ProfileActivity r15 = org.telegram.ui.ProfileActivity.this
                int r15 = r15.chat_id
                if (r15 <= 0) goto L_0x0559
                r15 = 2131624119(0x7f0e00b7, float:1.8875409E38)
                java.lang.String r0 = "AddMember"
                java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r0, r15)
                r0 = 2131165249(0x7var_, float:1.794471E38)
                r14.setTextAndIcon((java.lang.String) r15, (int) r0, (boolean) r6)
                goto L_0x0772
            L_0x0559:
                r15 = 2131624129(0x7f0e00c1, float:1.887543E38)
                java.lang.String r0 = "AddRecipient"
                java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r0, r15)
                r0 = 2131165249(0x7var_, float:1.794471E38)
                r14.setTextAndIcon((java.lang.String) r15, (int) r0, (boolean) r6)
                goto L_0x0772
            L_0x056a:
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                int r0 = r0.sendMessageRow
                if (r15 != r0) goto L_0x0580
                r15 = 2131626665(0x7f0e0aa9, float:1.8880573E38)
                java.lang.String r0 = "SendMessageLocation"
                java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r0, r15)
                r14.setText(r15, r6)
                goto L_0x0772
            L_0x0580:
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                int r0 = r0.reportRow
                if (r15 != r0) goto L_0x0772
                r15 = 2131626531(0x7f0e0a23, float:1.88803E38)
                java.lang.String r0 = "ReportUserLocation"
                java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r0, r15)
                r14.setText(r15, r5)
                r14.setColors(r3, r7)
                goto L_0x0772
            L_0x0599:
                android.view.View r14 = r14.itemView
                org.telegram.ui.Cells.AboutLinkCell r14 = (org.telegram.ui.Cells.AboutLinkCell) r14
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                int r0 = r0.userInfoRow
                if (r15 != r0) goto L_0x05c1
                org.telegram.ui.ProfileActivity r15 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$UserFull r15 = r15.userInfo
                java.lang.String r15 = r15.about
                r0 = 2131627079(0x7f0e0CLASSNAME, float:1.8881412E38)
                java.lang.String r1 = "UserBio"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                org.telegram.ui.ProfileActivity r1 = org.telegram.ui.ProfileActivity.this
                boolean r1 = r1.isBot
                r14.setTextAndValue(r15, r0, r1)
                goto L_0x0772
            L_0x05c1:
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                int r0 = r0.channelInfoRow
                if (r15 != r0) goto L_0x0772
                org.telegram.ui.ProfileActivity r15 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r15 = r15.chatInfo
                java.lang.String r15 = r15.about
            L_0x05d1:
                java.lang.String r0 = "\n\n\n"
                boolean r1 = r15.contains(r0)
                if (r1 == 0) goto L_0x05e0
                java.lang.String r1 = "\n\n"
                java.lang.String r15 = r15.replace(r0, r1)
                goto L_0x05d1
            L_0x05e0:
                r14.setText(r15, r6)
                goto L_0x0772
            L_0x05e5:
                android.view.View r14 = r14.itemView
                org.telegram.ui.Cells.TextDetailCell r14 = (org.telegram.ui.Cells.TextDetailCell) r14
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                int r0 = r0.phoneRow
                if (r15 != r0) goto L_0x0644
                org.telegram.ui.ProfileActivity r15 = org.telegram.ui.ProfileActivity.this
                int r15 = r15.currentAccount
                org.telegram.messenger.MessagesController r15 = org.telegram.messenger.MessagesController.getInstance(r15)
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                int r0 = r0.user_id
                java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
                org.telegram.tgnet.TLRPC$User r15 = r15.getUser(r0)
                java.lang.String r0 = r15.phone
                boolean r0 = android.text.TextUtils.isEmpty(r0)
                if (r0 != 0) goto L_0x062d
                org.telegram.PhoneFormat.PhoneFormat r0 = org.telegram.PhoneFormat.PhoneFormat.getInstance()
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = "+"
                r1.append(r2)
                java.lang.String r15 = r15.phone
                r1.append(r15)
                java.lang.String r15 = r1.toString()
                java.lang.String r15 = r0.format(r15)
                goto L_0x0636
            L_0x062d:
                r15 = 2131626323(0x7f0e0953, float:1.8879879E38)
                java.lang.String r0 = "PhoneHidden"
                java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r0, r15)
            L_0x0636:
                r0 = 2131626326(0x7f0e0956, float:1.8879885E38)
                java.lang.String r1 = "PhoneMobile"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r14.setTextAndValue(r15, r0, r5)
                goto L_0x0772
            L_0x0644:
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                int r0 = r0.usernameRow
                if (r15 != r0) goto L_0x06ea
                org.telegram.ui.ProfileActivity r15 = org.telegram.ui.ProfileActivity.this
                int r15 = r15.user_id
                if (r15 == 0) goto L_0x069a
                org.telegram.ui.ProfileActivity r15 = org.telegram.ui.ProfileActivity.this
                int r15 = r15.currentAccount
                org.telegram.messenger.MessagesController r15 = org.telegram.messenger.MessagesController.getInstance(r15)
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                int r0 = r0.user_id
                java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
                org.telegram.tgnet.TLRPC$User r15 = r15.getUser(r0)
                if (r15 == 0) goto L_0x068a
                java.lang.String r0 = r15.username
                boolean r0 = android.text.TextUtils.isEmpty(r0)
                if (r0 != 0) goto L_0x068a
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r1 = "@"
                r0.append(r1)
                java.lang.String r15 = r15.username
                r0.append(r15)
                java.lang.String r15 = r0.toString()
                goto L_0x068c
            L_0x068a:
                java.lang.String r15 = "-"
            L_0x068c:
                r0 = 2131627115(0x7f0e0c6b, float:1.8881485E38)
                java.lang.String r1 = "Username"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r14.setTextAndValue(r15, r0, r5)
                goto L_0x0772
            L_0x069a:
                org.telegram.ui.ProfileActivity r15 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r15 = r15.currentChat
                if (r15 == 0) goto L_0x0772
                org.telegram.ui.ProfileActivity r15 = org.telegram.ui.ProfileActivity.this
                int r15 = r15.currentAccount
                org.telegram.messenger.MessagesController r15 = org.telegram.messenger.MessagesController.getInstance(r15)
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                int r0 = r0.chat_id
                java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
                org.telegram.tgnet.TLRPC$Chat r15 = r15.getChat(r0)
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                org.telegram.ui.ProfileActivity r1 = org.telegram.ui.ProfileActivity.this
                int r1 = r1.currentAccount
                org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
                java.lang.String r1 = r1.linkPrefix
                r0.append(r1)
                java.lang.String r1 = "/"
                r0.append(r1)
                java.lang.String r15 = r15.username
                r0.append(r15)
                java.lang.String r15 = r0.toString()
                r0 = 2131625469(0x7f0e05fd, float:1.8878147E38)
                java.lang.String r1 = "InviteLink"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r14.setTextAndValue(r15, r0, r5)
                goto L_0x0772
            L_0x06ea:
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                int r0 = r0.locationRow
                if (r15 != r0) goto L_0x0772
                org.telegram.ui.ProfileActivity r15 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r15 = r15.chatInfo
                if (r15 == 0) goto L_0x0772
                org.telegram.ui.ProfileActivity r15 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r15 = r15.chatInfo
                org.telegram.tgnet.TLRPC$ChannelLocation r15 = r15.location
                boolean r15 = r15 instanceof org.telegram.tgnet.TLRPC$TL_channelLocation
                if (r15 == 0) goto L_0x0772
                org.telegram.ui.ProfileActivity r15 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r15 = r15.chatInfo
                org.telegram.tgnet.TLRPC$ChannelLocation r15 = r15.location
                org.telegram.tgnet.TLRPC$TL_channelLocation r15 = (org.telegram.tgnet.TLRPC$TL_channelLocation) r15
                java.lang.String r15 = r15.address
                r0 = 2131624291(0x7f0e0163, float:1.8875758E38)
                java.lang.String r1 = "AttachLocation"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r14.setTextAndValue(r15, r0, r5)
                goto L_0x0772
            L_0x071f:
                android.view.View r14 = r14.itemView
                org.telegram.ui.Cells.HeaderCell r14 = (org.telegram.ui.Cells.HeaderCell) r14
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                int r0 = r0.infoHeaderRow
                if (r15 != r0) goto L_0x0763
                org.telegram.ui.ProfileActivity r15 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r15 = r15.currentChat
                boolean r15 = org.telegram.messenger.ChatObject.isChannel(r15)
                if (r15 == 0) goto L_0x0756
                org.telegram.ui.ProfileActivity r15 = org.telegram.ui.ProfileActivity.this
                org.telegram.tgnet.TLRPC$Chat r15 = r15.currentChat
                boolean r15 = r15.megagroup
                if (r15 != 0) goto L_0x0756
                org.telegram.ui.ProfileActivity r15 = org.telegram.ui.ProfileActivity.this
                int r15 = r15.channelInfoRow
                if (r15 == r4) goto L_0x0756
                r15 = 2131626514(0x7f0e0a12, float:1.8880266E38)
                java.lang.String r0 = "ReportChatDescription"
                java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r0, r15)
                r14.setText(r15)
                goto L_0x0772
            L_0x0756:
                r15 = 2131625453(0x7f0e05ed, float:1.8878114E38)
                java.lang.String r0 = "Info"
                java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r0, r15)
                r14.setText(r15)
                goto L_0x0772
            L_0x0763:
                org.telegram.ui.ProfileActivity r0 = org.telegram.ui.ProfileActivity.this
                int r0 = r0.membersHeaderRow
                if (r15 != r0) goto L_0x0772
                java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r2, r1)
                r14.setText(r15)
            L_0x0772:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return (itemViewType == 1 || itemViewType == 5 || itemViewType == 7 || itemViewType == 9 || itemViewType == 10 || itemViewType == 11 || itemViewType == 12 || itemViewType == 13) ? false : true;
        }

        public int getItemCount() {
            return ProfileActivity.this.rowCount;
        }

        public int getItemViewType(int i) {
            if (i == ProfileActivity.this.infoHeaderRow || i == ProfileActivity.this.membersHeaderRow) {
                return 1;
            }
            if (i == ProfileActivity.this.phoneRow || i == ProfileActivity.this.usernameRow || i == ProfileActivity.this.locationRow) {
                return 2;
            }
            if (i == ProfileActivity.this.userInfoRow || i == ProfileActivity.this.channelInfoRow) {
                return 3;
            }
            if (i == ProfileActivity.this.settingsTimerRow || i == ProfileActivity.this.settingsKeyRow || i == ProfileActivity.this.reportRow || i == ProfileActivity.this.subscribersRow || i == ProfileActivity.this.administratorsRow || i == ProfileActivity.this.blockedUsersRow || i == ProfileActivity.this.addMemberRow || i == ProfileActivity.this.joinRow || i == ProfileActivity.this.unblockRow || i == ProfileActivity.this.sendMessageRow) {
                return 4;
            }
            if (i == ProfileActivity.this.notificationsDividerRow) {
                return 5;
            }
            if (i == ProfileActivity.this.notificationsRow) {
                return 6;
            }
            if (i == ProfileActivity.this.infoSectionRow || i == ProfileActivity.this.lastSectionRow || i == ProfileActivity.this.membersSectionRow || i == ProfileActivity.this.settingsSectionRow) {
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
            return i == ProfileActivity.this.sharedMediaRow ? 13 : 0;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        $$Lambda$ProfileActivity$ybA4dDgphBoDMFj9vMkmt8EIenI r10 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                ProfileActivity.this.lambda$getThemeDescriptions$22$ProfileActivity();
            }
        };
        ArrayList arrayList = new ArrayList();
        SharedMediaLayout sharedMediaLayout2 = this.sharedMediaLayout;
        if (sharedMediaLayout2 != null) {
            arrayList.addAll(sharedMediaLayout2.getThemeDescriptions());
        }
        arrayList.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuBackground"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuItem"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_AB_SUBMENUITEM, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuItemIcon"));
        $$Lambda$ProfileActivity$ybA4dDgphBoDMFj9vMkmt8EIenI r7 = r10;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_actionBarSelectorBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "chat_lockIcon"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_subtitleInProfileBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundActionBarBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "profile_title"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "profile_status"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_subtitleInProfileBlue"));
        arrayList.add(new ThemeDescription(this.onlineTextView[2], ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "player_actionBarSubtitle"));
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
        $$Lambda$ProfileActivity$ybA4dDgphBoDMFj9vMkmt8EIenI r8 = r10;
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r8, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r8, "windowBackgroundWhiteBlueText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        $$Lambda$ProfileActivity$ybA4dDgphBoDMFj9vMkmt8EIenI r72 = r10;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r72, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r72, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r72, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r72, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r72, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r72, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r72, "avatar_backgroundPink"));
        arrayList.add(new ThemeDescription(this.undoView, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_background"));
        arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
        arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
        arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        arrayList.add(new ThemeDescription((View) this.undoView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{UndoView.class}, new String[]{"leftImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AboutLinkCell.class}, Theme.profile_aboutTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{AboutLinkCell.class}, Theme.profile_aboutTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteLinkText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{AboutLinkCell.class}, Theme.linkSelectionPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteLinkSelection"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription(this.nameTextView[1], 0, (Class[]) null, (Paint) null, new Drawable[]{this.verifiedCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_verifiedCheck"));
        arrayList.add(new ThemeDescription(this.nameTextView[1], 0, (Class[]) null, (Paint) null, new Drawable[]{this.verifiedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "profile_verifiedBackground"));
        return (ThemeDescription[]) arrayList.toArray(new ThemeDescription[0]);
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
