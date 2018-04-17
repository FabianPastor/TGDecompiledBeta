package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.text.TextUtils;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import com.google.android.gms.maps.model.LatLng;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController.SharingLocationInfo;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.beta.R;
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

    public SharingLiveLocationCell(Context context, boolean distance) {
        Context context2 = context;
        super(context);
        this.avatarImageView = new BackupImageView(context2);
        this.avatarImageView.setRoundRadius(AndroidUtilities.dp(20.0f));
        this.avatarDrawable = new AvatarDrawable();
        this.nameTextView = new SimpleTextView(context2);
        this.nameTextView.setTextSize(16);
        this.nameTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        int i = 3;
        this.nameTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        View view;
        if (distance) {
            addView(r0.avatarImageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 17.0f, 13.0f, LocaleController.isRTL ? 17.0f : 0.0f, 0.0f));
            addView(r0.nameTextView, LayoutHelper.createFrame(-1, 20.0f, 48 | (LocaleController.isRTL ? 5 : 3), LocaleController.isRTL ? 54.0f : 73.0f, 12.0f, LocaleController.isRTL ? 73.0f : 54.0f, 0.0f));
            r0.distanceTextView = new SimpleTextView(context2);
            r0.distanceTextView.setTextSize(14);
            r0.distanceTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
            r0.distanceTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            view = r0.distanceTextView;
            if (LocaleController.isRTL) {
                i = 5;
            }
            addView(view, LayoutHelper.createFrame(-1, 20.0f, 48 | i, LocaleController.isRTL ? 54.0f : 73.0f, 37.0f, LocaleController.isRTL ? 73.0f : 54.0f, 0.0f));
        } else {
            addView(r0.avatarImageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 17.0f, 7.0f, LocaleController.isRTL ? 17.0f : 0.0f, 0.0f));
            view = r0.nameTextView;
            if (LocaleController.isRTL) {
                i = 5;
            }
            addView(view, LayoutHelper.createFrame(-2, -2.0f, 48 | i, LocaleController.isRTL ? 54.0f : 74.0f, 17.0f, LocaleController.isRTL ? 74.0f : 54.0f, 0.0f));
        }
        setWillNotDraw(false);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.distanceTextView != null ? 66.0f : 54.0f), NUM));
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        AndroidUtilities.cancelRunOnUIThread(this.invalidateRunnable);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        AndroidUtilities.runOnUIThread(this.invalidateRunnable);
    }

    public void setDialog(MessageObject messageObject, Location userLocation) {
        String name;
        int fromId = messageObject.messageOwner.from_id;
        if (messageObject.isForwarded()) {
            if (messageObject.messageOwner.fwd_from.channel_id != 0) {
                fromId = -messageObject.messageOwner.fwd_from.channel_id;
            } else {
                fromId = messageObject.messageOwner.fwd_from.from_id;
            }
        }
        this.currentAccount = messageObject.currentAccount;
        String address = null;
        TLObject photo = null;
        if (!TextUtils.isEmpty(messageObject.messageOwner.media.address)) {
            address = messageObject.messageOwner.media.address;
        }
        if (TextUtils.isEmpty(messageObject.messageOwner.media.title)) {
            name = TtmlNode.ANONYMOUS_REGION_ID;
            this.avatarDrawable = null;
            if (fromId > 0) {
                User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(fromId));
                if (user != null) {
                    if (user.photo != null) {
                        photo = user.photo.photo_small;
                    }
                    this.avatarDrawable = new AvatarDrawable(user);
                    name = UserObject.getUserName(user);
                }
            } else {
                Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-fromId));
                if (chat != null) {
                    if (chat.photo != null) {
                        photo = chat.photo.photo_small;
                    }
                    this.avatarDrawable = new AvatarDrawable(chat);
                    name = chat.title;
                }
            }
            this.avatarImageView.setImage(photo, null, this.avatarDrawable);
        } else {
            name = messageObject.messageOwner.media.title;
            Drawable drawable = getResources().getDrawable(R.drawable.pin);
            drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_location_sendLocationIcon), Mode.MULTIPLY));
            int color = Theme.getColor(Theme.key_location_placeLocationBackground);
            CombinedDrawable combinedDrawable = new CombinedDrawable(Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(40.0f), color, color), drawable);
            combinedDrawable.setCustomSize(AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
            combinedDrawable.setIconSize(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f));
            this.avatarImageView.setImageDrawable(combinedDrawable);
        }
        this.nameTextView.setText(name);
        this.location.setLatitude(messageObject.messageOwner.media.geo.lat);
        this.location.setLongitude(messageObject.messageOwner.media.geo._long);
        if (userLocation != null) {
            float distance = this.location.distanceTo(userLocation);
            if (address != null) {
                if (distance < 1000.0f) {
                    this.distanceTextView.setText(String.format("%s - %d %s", new Object[]{address, Integer.valueOf((int) distance), LocaleController.getString("MetersAway", R.string.MetersAway)}));
                } else {
                    this.distanceTextView.setText(String.format("%s - %.2f %s", new Object[]{address, Float.valueOf(distance / 1000.0f), LocaleController.getString("KMetersAway", R.string.KMetersAway)}));
                }
            } else if (distance < 1000.0f) {
                this.distanceTextView.setText(String.format("%d %s", new Object[]{Integer.valueOf((int) distance), LocaleController.getString("MetersAway", R.string.MetersAway)}));
            } else {
                this.distanceTextView.setText(String.format("%.2f %s", new Object[]{Float.valueOf(distance / 1000.0f), LocaleController.getString("KMetersAway", R.string.KMetersAway)}));
            }
        } else if (address != null) {
            this.distanceTextView.setText(address);
        } else {
            this.distanceTextView.setText(LocaleController.getString("Loading", R.string.Loading));
        }
    }

    public void setDialog(LiveLocation info, Location userLocation) {
        this.liveLocation = info;
        int lower_id = info.id;
        TLObject photo = null;
        if (lower_id > 0) {
            User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(lower_id));
            this.avatarDrawable.setInfo(user);
            if (user != null) {
                this.nameTextView.setText(ContactsController.formatName(user.first_name, user.last_name));
                if (!(user.photo == null || user.photo.photo_small == null)) {
                    photo = user.photo.photo_small;
                }
            }
        } else {
            Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-lower_id));
            if (chat != null) {
                this.avatarDrawable.setInfo(chat);
                this.nameTextView.setText(chat.title);
                if (!(chat.photo == null || chat.photo.photo_small == null)) {
                    photo = chat.photo.photo_small;
                }
            }
        }
        LatLng position = info.marker.getPosition();
        this.location.setLatitude(position.latitude);
        this.location.setLongitude(position.longitude);
        String time = LocaleController.formatLocationUpdateDate((long) (info.object.edit_date != 0 ? info.object.edit_date : info.object.date));
        if (userLocation != null) {
            if (this.location.distanceTo(userLocation) < 1000.0f) {
                this.distanceTextView.setText(String.format("%s - %d %s", new Object[]{time, Integer.valueOf((int) this.location.distanceTo(userLocation)), LocaleController.getString("MetersAway", R.string.MetersAway)}));
            } else {
                this.distanceTextView.setText(String.format("%s - %.2f %s", new Object[]{time, Float.valueOf(this.location.distanceTo(userLocation) / 1000.0f), LocaleController.getString("KMetersAway", R.string.KMetersAway)}));
            }
        } else {
            this.distanceTextView.setText(time);
        }
        this.avatarImageView.setImage(photo, null, this.avatarDrawable);
    }

    public void setDialog(SharingLocationInfo info) {
        this.currentInfo = info;
        int lower_id = (int) info.did;
        TLObject photo = null;
        if (lower_id > 0) {
            User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(lower_id));
            if (user != null) {
                this.avatarDrawable.setInfo(user);
                this.nameTextView.setText(ContactsController.formatName(user.first_name, user.last_name));
                if (!(user.photo == null || user.photo.photo_small == null)) {
                    photo = user.photo.photo_small;
                }
            }
        } else {
            Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-lower_id));
            if (chat != null) {
                this.avatarDrawable.setInfo(chat);
                this.nameTextView.setText(chat.title);
                if (!(chat.photo == null || chat.photo.photo_small == null)) {
                    photo = chat.photo.photo_small;
                }
            }
        }
        this.avatarImageView.setImage(photo, null, this.avatarDrawable);
    }

    protected void onDraw(Canvas canvas) {
        if (this.currentInfo != null || this.liveLocation != null) {
            int stopTime;
            int period;
            if (this.currentInfo != null) {
                stopTime = this.currentInfo.stopTime;
                period = this.currentInfo.period;
            } else {
                stopTime = this.liveLocation.object.date + this.liveLocation.object.media.period;
                period = this.liveLocation.object.media.period;
            }
            int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            if (stopTime >= currentTime) {
                int color;
                float progress = ((float) Math.abs(stopTime - currentTime)) / ((float) period);
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
                    color = Theme.getColor("location_liveLocationProgress");
                } else {
                    color = Theme.getColor("location_liveLocationProgress");
                }
                Theme.chat_radialProgress2Paint.setColor(color);
                Theme.chat_livePaint.setColor(color);
                canvas.drawArc(this.rect, -90.0f, -360.0f * progress, false, Theme.chat_radialProgress2Paint);
                String text = LocaleController.formatLocationLeftTime(stopTime - currentTime);
                canvas.drawText(text, this.rect.centerX() - (Theme.chat_livePaint.measureText(text) / 2.0f), (float) AndroidUtilities.dp(this.distanceTextView != null ? 37.0f : 31.0f), Theme.chat_livePaint);
            }
        }
    }
}
