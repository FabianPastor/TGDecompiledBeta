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
import org.telegram.messenger.C0446R;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController.SharingLocationInfo;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
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
    private Runnable invalidateRunnable = new C08971();
    private LiveLocation liveLocation;
    private Location location = new Location("network");
    private SimpleTextView nameTextView;
    private RectF rect = new RectF();

    /* renamed from: org.telegram.ui.Cells.SharingLiveLocationCell$1 */
    class C08971 implements Runnable {
        C08971() {
        }

        public void run() {
            SharingLiveLocationCell.this.invalidate(((int) SharingLiveLocationCell.this.rect.left) - 5, ((int) SharingLiveLocationCell.this.rect.top) - 5, ((int) SharingLiveLocationCell.this.rect.right) + 5, ((int) SharingLiveLocationCell.this.rect.bottom) + 5);
            AndroidUtilities.runOnUIThread(SharingLiveLocationCell.this.invalidateRunnable, 1000);
        }
    }

    public SharingLiveLocationCell(Context context, boolean z) {
        super(context);
        this.avatarImageView = new BackupImageView(context);
        this.avatarImageView.setRoundRadius(AndroidUtilities.dp(20.0f));
        this.avatarDrawable = new AvatarDrawable();
        this.nameTextView = new SimpleTextView(context);
        this.nameTextView.setTextSize(16);
        this.nameTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        int i = 3;
        this.nameTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        if (z) {
            addView(this.avatarImageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 17.0f, 13.0f, LocaleController.isRTL ? 17.0f : 0.0f, 0.0f));
            addView(this.nameTextView, LayoutHelper.createFrame(-1, 20.0f, 48 | (LocaleController.isRTL ? 5 : 3), LocaleController.isRTL ? 54.0f : 73.0f, 12.0f, LocaleController.isRTL ? 73.0f : 54.0f, 0.0f));
            this.distanceTextView = new SimpleTextView(context);
            this.distanceTextView.setTextSize(true);
            this.distanceTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
            this.distanceTextView.setGravity(LocaleController.isRTL ? true : true);
            context = this.distanceTextView;
            if (LocaleController.isRTL) {
                i = 5;
            }
            addView(context, LayoutHelper.createFrame(-1, 20.0f, 48 | i, LocaleController.isRTL ? 54.0f : 73.0f, 37.0f, LocaleController.isRTL ? 73.0f : 54.0f, 0.0f));
        } else {
            addView(this.avatarImageView, LayoutHelper.createFrame(40, 40.0f, 48 | (LocaleController.isRTL ? true : true), LocaleController.isRTL ? 0.0f : 17.0f, 7.0f, LocaleController.isRTL ? 17.0f : 0.0f, 0.0f));
            context = this.nameTextView;
            if (LocaleController.isRTL) {
                i = 5;
            }
            addView(context, LayoutHelper.createFrame(-2, -2.0f, 48 | i, LocaleController.isRTL ? 54.0f : 74.0f, 17.0f, LocaleController.isRTL ? 74.0f : 54.0f, 0.0f));
        }
        setWillNotDraw(null);
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.distanceTextView != null ? 66.0f : 54.0f), NUM));
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        AndroidUtilities.cancelRunOnUIThread(this.invalidateRunnable);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        AndroidUtilities.runOnUIThread(this.invalidateRunnable);
    }

    public void setDialog(MessageObject messageObject, Location location) {
        CharSequence userName;
        int i = messageObject.messageOwner.from_id;
        if (messageObject.isForwarded()) {
            if (messageObject.messageOwner.fwd_from.channel_id != 0) {
                i = -messageObject.messageOwner.fwd_from.channel_id;
            } else {
                i = messageObject.messageOwner.fwd_from.from_id;
            }
        }
        this.currentAccount = messageObject.currentAccount;
        CharSequence charSequence = !TextUtils.isEmpty(messageObject.messageOwner.media.address) ? messageObject.messageOwner.media.address : null;
        if (TextUtils.isEmpty(messageObject.messageOwner.media.title)) {
            TLObject tLObject;
            String str = TtmlNode.ANONYMOUS_REGION_ID;
            this.avatarDrawable = null;
            if (i > 0) {
                User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i));
                if (user != null) {
                    tLObject = user.photo != null ? user.photo.photo_small : null;
                    this.avatarDrawable = new AvatarDrawable(user);
                    userName = UserObject.getUserName(user);
                    this.avatarImageView.setImage(tLObject, null, this.avatarDrawable);
                }
            } else {
                Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i));
                if (chat != null) {
                    tLObject = chat.photo != null ? chat.photo.photo_small : null;
                    this.avatarDrawable = new AvatarDrawable(chat);
                    userName = chat.title;
                    this.avatarImageView.setImage(tLObject, null, this.avatarDrawable);
                }
            }
            userName = str;
            tLObject = null;
            this.avatarImageView.setImage(tLObject, null, this.avatarDrawable);
        } else {
            userName = messageObject.messageOwner.media.title;
            Drawable drawable = getResources().getDrawable(C0446R.drawable.pin);
            drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_location_sendLocationIcon), Mode.MULTIPLY));
            int color = Theme.getColor(Theme.key_location_placeLocationBackground);
            Drawable combinedDrawable = new CombinedDrawable(Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(40.0f), color, color), drawable);
            combinedDrawable.setCustomSize(AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
            combinedDrawable.setIconSize(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f));
            this.avatarImageView.setImageDrawable(combinedDrawable);
        }
        this.nameTextView.setText(userName);
        this.location.setLatitude(messageObject.messageOwner.media.geo.lat);
        this.location.setLongitude(messageObject.messageOwner.media.geo._long);
        if (location != null) {
            messageObject = this.location.distanceTo(location);
            if (charSequence != null) {
                if (messageObject < NUM) {
                    this.distanceTextView.setText(String.format("%s - %d %s", new Object[]{charSequence, Integer.valueOf((int) messageObject), LocaleController.getString("MetersAway", C0446R.string.MetersAway)}));
                } else {
                    this.distanceTextView.setText(String.format("%s - %.2f %s", new Object[]{charSequence, Float.valueOf(messageObject / NUM), LocaleController.getString("KMetersAway", C0446R.string.KMetersAway)}));
                }
            } else if (messageObject < NUM) {
                this.distanceTextView.setText(String.format("%d %s", new Object[]{Integer.valueOf((int) messageObject), LocaleController.getString("MetersAway", C0446R.string.MetersAway)}));
            } else {
                this.distanceTextView.setText(String.format("%.2f %s", new Object[]{Float.valueOf(messageObject / NUM), LocaleController.getString("KMetersAway", C0446R.string.KMetersAway)}));
            }
        } else if (charSequence != null) {
            this.distanceTextView.setText(charSequence);
        } else {
            this.distanceTextView.setText(LocaleController.getString("Loading", C0446R.string.Loading));
        }
    }

    public void setDialog(LiveLocation liveLocation, Location location) {
        TLObject tLObject;
        LatLng position;
        this.liveLocation = liveLocation;
        int i = liveLocation.id;
        if (i > 0) {
            User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i));
            this.avatarDrawable.setInfo(user);
            if (user != null) {
                this.nameTextView.setText(ContactsController.formatName(user.first_name, user.last_name));
                if (!(user.photo == null || user.photo.photo_small == null)) {
                    tLObject = user.photo.photo_small;
                    position = liveLocation.marker.getPosition();
                    this.location.setLatitude(position.latitude);
                    this.location.setLongitude(position.longitude);
                    liveLocation = LocaleController.formatLocationUpdateDate((long) (liveLocation.object.edit_date != 0 ? liveLocation.object.edit_date : liveLocation.object.date));
                    if (location != null) {
                        if (this.location.distanceTo(location) < NUM) {
                            this.distanceTextView.setText(String.format("%s - %d %s", new Object[]{liveLocation, Integer.valueOf((int) this.location.distanceTo(location)), LocaleController.getString("MetersAway", C0446R.string.MetersAway)}));
                        } else {
                            this.distanceTextView.setText(String.format("%s - %.2f %s", new Object[]{liveLocation, Float.valueOf(this.location.distanceTo(location) / NUM), LocaleController.getString("KMetersAway", C0446R.string.KMetersAway)}));
                        }
                    } else {
                        this.distanceTextView.setText(liveLocation);
                    }
                    this.avatarImageView.setImage(tLObject, null, this.avatarDrawable);
                }
            }
        }
        Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i));
        if (chat != null) {
            this.avatarDrawable.setInfo(chat);
            this.nameTextView.setText(chat.title);
            if (!(chat.photo == null || chat.photo.photo_small == null)) {
                tLObject = chat.photo.photo_small;
                position = liveLocation.marker.getPosition();
                this.location.setLatitude(position.latitude);
                this.location.setLongitude(position.longitude);
                if (liveLocation.object.edit_date != 0) {
                }
                liveLocation = LocaleController.formatLocationUpdateDate((long) (liveLocation.object.edit_date != 0 ? liveLocation.object.edit_date : liveLocation.object.date));
                if (location != null) {
                    this.distanceTextView.setText(liveLocation);
                } else {
                    if (this.location.distanceTo(location) < NUM) {
                        this.distanceTextView.setText(String.format("%s - %.2f %s", new Object[]{liveLocation, Float.valueOf(this.location.distanceTo(location) / NUM), LocaleController.getString("KMetersAway", C0446R.string.KMetersAway)}));
                    } else {
                        this.distanceTextView.setText(String.format("%s - %d %s", new Object[]{liveLocation, Integer.valueOf((int) this.location.distanceTo(location)), LocaleController.getString("MetersAway", C0446R.string.MetersAway)}));
                    }
                }
                this.avatarImageView.setImage(tLObject, null, this.avatarDrawable);
            }
        }
        tLObject = null;
        position = liveLocation.marker.getPosition();
        this.location.setLatitude(position.latitude);
        this.location.setLongitude(position.longitude);
        if (liveLocation.object.edit_date != 0) {
        }
        liveLocation = LocaleController.formatLocationUpdateDate((long) (liveLocation.object.edit_date != 0 ? liveLocation.object.edit_date : liveLocation.object.date));
        if (location != null) {
            if (this.location.distanceTo(location) < NUM) {
                this.distanceTextView.setText(String.format("%s - %d %s", new Object[]{liveLocation, Integer.valueOf((int) this.location.distanceTo(location)), LocaleController.getString("MetersAway", C0446R.string.MetersAway)}));
            } else {
                this.distanceTextView.setText(String.format("%s - %.2f %s", new Object[]{liveLocation, Float.valueOf(this.location.distanceTo(location) / NUM), LocaleController.getString("KMetersAway", C0446R.string.KMetersAway)}));
            }
        } else {
            this.distanceTextView.setText(liveLocation);
        }
        this.avatarImageView.setImage(tLObject, null, this.avatarDrawable);
    }

    public void setDialog(SharingLocationInfo sharingLocationInfo) {
        TLObject tLObject;
        this.currentInfo = sharingLocationInfo;
        sharingLocationInfo = (int) sharingLocationInfo.did;
        if (sharingLocationInfo > null) {
            User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(sharingLocationInfo));
            if (user != null) {
                this.avatarDrawable.setInfo(user);
                this.nameTextView.setText(ContactsController.formatName(user.first_name, user.last_name));
                if (!(user.photo == null || user.photo.photo_small == null)) {
                    tLObject = user.photo.photo_small;
                    this.avatarImageView.setImage(tLObject, null, this.avatarDrawable);
                }
            }
        }
        Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-sharingLocationInfo));
        if (chat != null) {
            this.avatarDrawable.setInfo(chat);
            this.nameTextView.setText(chat.title);
            if (!(chat.photo == null || chat.photo.photo_small == null)) {
                tLObject = chat.photo.photo_small;
                this.avatarImageView.setImage(tLObject, null, this.avatarDrawable);
            }
        }
        tLObject = null;
        this.avatarImageView.setImage(tLObject, null, this.avatarDrawable);
    }

    protected void onDraw(Canvas canvas) {
        if (this.currentInfo != null || this.liveLocation != null) {
            int i;
            int i2;
            if (this.currentInfo != null) {
                i = this.currentInfo.stopTime;
                i2 = this.currentInfo.period;
            } else {
                i = this.liveLocation.object.date + this.liveLocation.object.media.period;
                i2 = this.liveLocation.object.media.period;
            }
            int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            if (i >= currentTime) {
                i -= currentTime;
                float abs = ((float) Math.abs(i)) / ((float) i2);
                float f = 42.0f;
                float f2 = 12.0f;
                RectF rectF;
                float dp;
                if (LocaleController.isRTL) {
                    rectF = this.rect;
                    float dp2 = (float) AndroidUtilities.dp(13.0f);
                    if (this.distanceTextView != null) {
                        f2 = 18.0f;
                    }
                    f2 = (float) AndroidUtilities.dp(f2);
                    dp = (float) AndroidUtilities.dp(43.0f);
                    if (this.distanceTextView != null) {
                        f = 48.0f;
                    }
                    rectF.set(dp2, f2, dp, (float) AndroidUtilities.dp(f));
                } else {
                    rectF = this.rect;
                    dp = (float) (getMeasuredWidth() - AndroidUtilities.dp(43.0f));
                    if (this.distanceTextView != null) {
                        f2 = 18.0f;
                    }
                    f2 = (float) AndroidUtilities.dp(f2);
                    float measuredWidth = (float) (getMeasuredWidth() - AndroidUtilities.dp(13.0f));
                    if (this.distanceTextView != null) {
                        f = 48.0f;
                    }
                    rectF.set(dp, f2, measuredWidth, (float) AndroidUtilities.dp(f));
                }
                if (this.distanceTextView == null) {
                    i2 = Theme.getColor("location_liveLocationProgress");
                } else {
                    i2 = Theme.getColor("location_liveLocationProgress");
                }
                Theme.chat_radialProgress2Paint.setColor(i2);
                Theme.chat_livePaint.setColor(i2);
                canvas.drawArc(this.rect, -90.0f, -360.0f * abs, false, Theme.chat_radialProgress2Paint);
                String formatLocationLeftTime = LocaleController.formatLocationLeftTime(i);
                canvas.drawText(formatLocationLeftTime, this.rect.centerX() - (Theme.chat_livePaint.measureText(formatLocationLeftTime) / 2.0f), (float) AndroidUtilities.dp(this.distanceTextView != null ? 37.0f : 31.0f), Theme.chat_livePaint);
            }
        }
    }
}
