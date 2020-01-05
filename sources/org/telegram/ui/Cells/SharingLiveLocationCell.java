package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.text.TextUtils;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import com.google.android.gms.maps.model.LatLng;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController.SharingLocationInfo;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageFwdHeader;
import org.telegram.tgnet.TLRPC.TL_channelLocation;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.LocationActivity.LiveLocation;

public class SharingLiveLocationCell extends FrameLayout {
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImageView;
    private int currentAccount;
    private SharingLocationInfo currentInfo;
    private SimpleTextView distanceTextView;
    private Runnable invalidateRunnable = new Runnable() {
        public void run() {
            SharingLiveLocationCell sharingLiveLocationCell = SharingLiveLocationCell.this;
            sharingLiveLocationCell.invalidate(((int) sharingLiveLocationCell.rect.left) - 5, ((int) SharingLiveLocationCell.this.rect.top) - 5, ((int) SharingLiveLocationCell.this.rect.right) + 5, ((int) SharingLiveLocationCell.this.rect.bottom) + 5);
            AndroidUtilities.runOnUIThread(SharingLiveLocationCell.this.invalidateRunnable, 1000);
        }
    };
    private LiveLocation liveLocation;
    private Location location = new Location("network");
    private SimpleTextView nameTextView;
    private RectF rect = new RectF();

    public SharingLiveLocationCell(Context context, boolean z, int i) {
        super(context);
        this.avatarImageView = new BackupImageView(context);
        this.avatarImageView.setRoundRadius(AndroidUtilities.dp(21.0f));
        this.avatarDrawable = new AvatarDrawable();
        this.nameTextView = new SimpleTextView(context);
        this.nameTextView.setTextSize(16);
        this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        int i2 = 5;
        this.nameTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        SimpleTextView simpleTextView;
        if (z) {
            addView(this.avatarImageView, LayoutHelper.createFrame(42, 42.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 15.0f, 12.0f, LocaleController.isRTL ? 15.0f : 0.0f, 0.0f));
            addView(this.nameTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? (float) i : 73.0f, 12.0f, LocaleController.isRTL ? 73.0f : (float) i, 0.0f));
            this.distanceTextView = new SimpleTextView(context);
            this.distanceTextView.setTextSize(14);
            this.distanceTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
            this.distanceTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            simpleTextView = this.distanceTextView;
            if (!LocaleController.isRTL) {
                i2 = 3;
            }
            addView(simpleTextView, LayoutHelper.createFrame(-1, 20.0f, i2 | 48, LocaleController.isRTL ? (float) i : 73.0f, 37.0f, LocaleController.isRTL ? 73.0f : (float) i, 0.0f));
        } else {
            addView(this.avatarImageView, LayoutHelper.createFrame(42, 42.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 15.0f, 6.0f, LocaleController.isRTL ? 15.0f : 0.0f, 0.0f));
            simpleTextView = this.nameTextView;
            if (!LocaleController.isRTL) {
                i2 = 3;
            }
            addView(simpleTextView, LayoutHelper.createFrame(-2, -2.0f, i2 | 48, LocaleController.isRTL ? (float) i : 74.0f, 17.0f, LocaleController.isRTL ? 74.0f : (float) i, 0.0f));
        }
        setWillNotDraw(false);
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.distanceTextView != null ? 66.0f : 54.0f), NUM));
    }

    /* Access modifiers changed, original: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        AndroidUtilities.cancelRunOnUIThread(this.invalidateRunnable);
    }

    /* Access modifiers changed, original: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        AndroidUtilities.runOnUIThread(this.invalidateRunnable);
    }

    public void setDialog(long j, TL_channelLocation tL_channelLocation) {
        this.currentAccount = UserConfig.selectedAccount;
        String str = tL_channelLocation.address;
        this.avatarDrawable = null;
        int i = (int) j;
        String str2 = "50_50";
        CharSequence charSequence = "";
        Object user;
        if (i > 0) {
            user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i));
            if (user != null) {
                this.avatarDrawable = new AvatarDrawable((User) user);
                charSequence = UserObject.getUserName(user);
                this.avatarImageView.setImage(ImageLocation.getForUser(user, false), str2, this.avatarDrawable, user);
            }
        } else {
            user = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i));
            if (user != null) {
                this.avatarDrawable = new AvatarDrawable((Chat) user);
                charSequence = user.title;
                this.avatarImageView.setImage(ImageLocation.getForChat(user, false), str2, this.avatarDrawable, user);
            }
        }
        this.nameTextView.setText(charSequence);
        this.location.setLatitude(tL_channelLocation.geo_point.lat);
        this.location.setLongitude(tL_channelLocation.geo_point._long);
        this.distanceTextView.setText(str);
    }

    public void setDialog(MessageObject messageObject, Location location) {
        CharSequence charSequence;
        int i = messageObject.messageOwner.from_id;
        if (messageObject.isForwarded()) {
            MessageFwdHeader messageFwdHeader = messageObject.messageOwner.fwd_from;
            int i2 = messageFwdHeader.channel_id;
            if (i2 != 0) {
                i = -i2;
            } else {
                i = messageFwdHeader.from_id;
            }
        }
        this.currentAccount = messageObject.currentAccount;
        CharSequence charSequence2 = !TextUtils.isEmpty(messageObject.messageOwner.media.address) ? messageObject.messageOwner.media.address : null;
        if (TextUtils.isEmpty(messageObject.messageOwner.media.title)) {
            String str = "";
            this.avatarDrawable = null;
            String str2 = "50_50";
            Object user;
            if (i > 0) {
                user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i));
                if (user != null) {
                    this.avatarDrawable = new AvatarDrawable((User) user);
                    str = UserObject.getUserName(user);
                    this.avatarImageView.setImage(ImageLocation.getForUser(user, false), str2, this.avatarDrawable, user);
                }
            } else {
                user = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i));
                if (user != null) {
                    this.avatarDrawable = new AvatarDrawable((Chat) user);
                    str = user.title;
                    this.avatarImageView.setImage(ImageLocation.getForChat(user, false), str2, this.avatarDrawable, user);
                }
            }
            charSequence = str;
        } else {
            charSequence = messageObject.messageOwner.media.title;
            Drawable drawable = getResources().getDrawable(NUM);
            drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("location_sendLocationIcon"), Mode.MULTIPLY));
            int color = Theme.getColor("location_placeLocationBackground");
            CombinedDrawable combinedDrawable = new CombinedDrawable(Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(42.0f), color, color), drawable);
            combinedDrawable.setCustomSize(AndroidUtilities.dp(42.0f), AndroidUtilities.dp(42.0f));
            combinedDrawable.setIconSize(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f));
            this.avatarImageView.setImageDrawable(combinedDrawable);
        }
        this.nameTextView.setText(charSequence);
        this.location.setLatitude(messageObject.messageOwner.media.geo.lat);
        this.location.setLongitude(messageObject.messageOwner.media.geo._long);
        if (location != null) {
            float distanceTo = this.location.distanceTo(location);
            if (charSequence2 != null) {
                this.distanceTextView.setText(String.format("%s - %s", new Object[]{charSequence2, LocaleController.formatDistance(distanceTo)}));
            } else {
                this.distanceTextView.setText(LocaleController.formatDistance(distanceTo));
            }
        } else if (charSequence2 != null) {
            this.distanceTextView.setText(charSequence2);
        } else {
            this.distanceTextView.setText(LocaleController.getString("Loading", NUM));
        }
    }

    public void setDialog(LiveLocation liveLocation, Location location) {
        this.liveLocation = liveLocation;
        int i = liveLocation.id;
        String str = "50_50";
        Object user;
        if (i > 0) {
            user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i));
            if (user != null) {
                this.avatarDrawable.setInfo((User) user);
                this.nameTextView.setText(ContactsController.formatName(user.first_name, user.last_name));
                this.avatarImageView.setImage(ImageLocation.getForUser(user, false), str, this.avatarDrawable, user);
            }
        } else {
            user = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i));
            if (user != null) {
                this.avatarDrawable.setInfo((Chat) user);
                this.nameTextView.setText(user.title);
                this.avatarImageView.setImage(ImageLocation.getForChat(user, false), str, this.avatarDrawable, user);
            }
        }
        LatLng position = liveLocation.marker.getPosition();
        this.location.setLatitude(position.latitude);
        this.location.setLongitude(position.longitude);
        Message message = liveLocation.object;
        i = message.edit_date;
        String formatLocationUpdateDate = LocaleController.formatLocationUpdateDate(i != 0 ? (long) i : (long) message.date);
        if (location != null) {
            this.distanceTextView.setText(String.format("%s - %s", new Object[]{formatLocationUpdateDate, LocaleController.formatDistance(this.location.distanceTo(location))}));
        } else {
            this.distanceTextView.setText(formatLocationUpdateDate);
        }
    }

    public void setDialog(SharingLocationInfo sharingLocationInfo) {
        this.currentInfo = sharingLocationInfo;
        this.currentAccount = sharingLocationInfo.account;
        this.avatarImageView.getImageReceiver().setCurrentAccount(this.currentAccount);
        int i = (int) sharingLocationInfo.did;
        String str = "50_50";
        Object user;
        if (i > 0) {
            user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i));
            if (user != null) {
                this.avatarDrawable.setInfo((User) user);
                this.nameTextView.setText(ContactsController.formatName(user.first_name, user.last_name));
                this.avatarImageView.setImage(ImageLocation.getForUser(user, false), str, this.avatarDrawable, user);
                return;
            }
            return;
        }
        user = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i));
        if (user != null) {
            this.avatarDrawable.setInfo((Chat) user);
            this.nameTextView.setText(user.title);
            this.avatarImageView.setImage(ImageLocation.getForChat(user, false), str, this.avatarDrawable, user);
        }
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        if (this.currentInfo != null || this.liveLocation != null) {
            int i;
            int i2;
            SharingLocationInfo sharingLocationInfo = this.currentInfo;
            if (sharingLocationInfo != null) {
                i = sharingLocationInfo.stopTime;
                i2 = sharingLocationInfo.period;
            } else {
                Message message = this.liveLocation.object;
                i = message.date;
                i2 = message.media.period;
                i += i2;
            }
            int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            if (i >= currentTime) {
                i -= currentTime;
                float abs = ((float) Math.abs(i)) / ((float) i2);
                float f = 48.0f;
                float f2 = 18.0f;
                RectF rectF;
                float dp;
                if (LocaleController.isRTL) {
                    rectF = this.rect;
                    float dp2 = (float) AndroidUtilities.dp(13.0f);
                    if (this.distanceTextView == null) {
                        f2 = 12.0f;
                    }
                    f2 = (float) AndroidUtilities.dp(f2);
                    dp = (float) AndroidUtilities.dp(43.0f);
                    if (this.distanceTextView == null) {
                        f = 42.0f;
                    }
                    rectF.set(dp2, f2, dp, (float) AndroidUtilities.dp(f));
                } else {
                    rectF = this.rect;
                    float measuredWidth = (float) (getMeasuredWidth() - AndroidUtilities.dp(43.0f));
                    if (this.distanceTextView == null) {
                        f2 = 12.0f;
                    }
                    f2 = (float) AndroidUtilities.dp(f2);
                    dp = (float) (getMeasuredWidth() - AndroidUtilities.dp(13.0f));
                    if (this.distanceTextView == null) {
                        f = 42.0f;
                    }
                    rectF.set(measuredWidth, f2, dp, (float) AndroidUtilities.dp(f));
                }
                if (this.distanceTextView == null) {
                    i2 = Theme.getColor("dialog_liveLocationProgress");
                } else {
                    i2 = Theme.getColor("location_liveLocationProgress");
                }
                Theme.chat_radialProgress2Paint.setColor(i2);
                Theme.chat_livePaint.setColor(i2);
                canvas.drawArc(this.rect, -90.0f, abs * -360.0f, false, Theme.chat_radialProgress2Paint);
                String formatLocationLeftTime = LocaleController.formatLocationLeftTime(i);
                canvas.drawText(formatLocationLeftTime, this.rect.centerX() - (Theme.chat_livePaint.measureText(formatLocationLeftTime) / 2.0f), (float) AndroidUtilities.dp(this.distanceTextView != null ? 37.0f : 31.0f), Theme.chat_livePaint);
            }
        }
    }
}
