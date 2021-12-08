package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import com.google.android.gms.maps.model.LatLng;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.LocationActivity;

public class SharingLiveLocationCell extends FrameLayout {
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImageView;
    private int currentAccount = UserConfig.selectedAccount;
    private LocationController.SharingLocationInfo currentInfo;
    private SimpleTextView distanceTextView;
    /* access modifiers changed from: private */
    public Runnable invalidateRunnable = new Runnable() {
        public void run() {
            SharingLiveLocationCell sharingLiveLocationCell = SharingLiveLocationCell.this;
            sharingLiveLocationCell.invalidate(((int) sharingLiveLocationCell.rect.left) - 5, ((int) SharingLiveLocationCell.this.rect.top) - 5, ((int) SharingLiveLocationCell.this.rect.right) + 5, ((int) SharingLiveLocationCell.this.rect.bottom) + 5);
            AndroidUtilities.runOnUIThread(SharingLiveLocationCell.this.invalidateRunnable, 1000);
        }
    };
    private LocationActivity.LiveLocation liveLocation;
    private Location location = new Location("network");
    private SimpleTextView nameTextView;
    /* access modifiers changed from: private */
    public RectF rect = new RectF();
    private final Theme.ResourcesProvider resourcesProvider;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public SharingLiveLocationCell(Context context, boolean distance, int padding, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        Context context2 = context;
        int i = padding;
        this.resourcesProvider = resourcesProvider2;
        BackupImageView backupImageView = new BackupImageView(context2);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(21.0f));
        this.avatarDrawable = new AvatarDrawable();
        SimpleTextView simpleTextView = new SimpleTextView(context2);
        this.nameTextView = simpleTextView;
        simpleTextView.setTextSize(16);
        this.nameTextView.setTextColor(getThemedColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        int i2 = 5;
        this.nameTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        if (distance) {
            addView(this.avatarImageView, LayoutHelper.createFrame(42, 42.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 15.0f, 12.0f, LocaleController.isRTL ? 15.0f : 0.0f, 0.0f));
            addView(this.nameTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? (float) i : 73.0f, 12.0f, LocaleController.isRTL ? 73.0f : (float) i, 0.0f));
            SimpleTextView simpleTextView2 = new SimpleTextView(context2);
            this.distanceTextView = simpleTextView2;
            simpleTextView2.setTextSize(14);
            this.distanceTextView.setTextColor(getThemedColor("windowBackgroundWhiteGrayText3"));
            this.distanceTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            addView(this.distanceTextView, LayoutHelper.createFrame(-1, 20.0f, (!LocaleController.isRTL ? 3 : i2) | 48, LocaleController.isRTL ? (float) i : 73.0f, 37.0f, LocaleController.isRTL ? 73.0f : (float) i, 0.0f));
        } else {
            addView(this.avatarImageView, LayoutHelper.createFrame(42, 42.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 15.0f, 6.0f, LocaleController.isRTL ? 15.0f : 0.0f, 0.0f));
            addView(this.nameTextView, LayoutHelper.createFrame(-2, -2.0f, (!LocaleController.isRTL ? 3 : i2) | 48, LocaleController.isRTL ? (float) i : 74.0f, 17.0f, LocaleController.isRTL ? 74.0f : (float) i, 0.0f));
        }
        setWillNotDraw(false);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.distanceTextView != null ? 66.0f : 54.0f), NUM));
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        AndroidUtilities.cancelRunOnUIThread(this.invalidateRunnable);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        AndroidUtilities.runOnUIThread(this.invalidateRunnable);
    }

    public void setDialog(long dialogId, TLRPC.TL_channelLocation chatLocation) {
        this.currentAccount = UserConfig.selectedAccount;
        String address = chatLocation.address;
        String name = "";
        this.avatarDrawable = null;
        if (DialogObject.isUserDialog(dialogId)) {
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(dialogId));
            if (user != null) {
                this.avatarDrawable = new AvatarDrawable(user);
                name = UserObject.getUserName(user);
                this.avatarImageView.setForUserOrChat(user, this.avatarDrawable);
            }
        } else {
            TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-dialogId));
            if (chat != null) {
                this.avatarDrawable = new AvatarDrawable(chat);
                name = chat.title;
                this.avatarImageView.setForUserOrChat(chat, this.avatarDrawable);
            }
        }
        this.nameTextView.setText(name);
        this.location.setLatitude(chatLocation.geo_point.lat);
        this.location.setLongitude(chatLocation.geo_point._long);
        this.distanceTextView.setText(address);
    }

    public void setDialog(MessageObject messageObject, Location userLocation) {
        String name;
        long fromId = messageObject.getFromChatId();
        if (messageObject.isForwarded()) {
            fromId = MessageObject.getPeerId(messageObject.messageOwner.fwd_from.from_id);
        }
        this.currentAccount = messageObject.currentAccount;
        String address = null;
        if (!TextUtils.isEmpty(messageObject.messageOwner.media.address)) {
            address = messageObject.messageOwner.media.address;
        }
        if (!TextUtils.isEmpty(messageObject.messageOwner.media.title)) {
            name = messageObject.messageOwner.media.title;
            Drawable drawable = getResources().getDrawable(NUM);
            drawable.setColorFilter(new PorterDuffColorFilter(getThemedColor("location_sendLocationIcon"), PorterDuff.Mode.MULTIPLY));
            int color = getThemedColor("location_placeLocationBackground");
            CombinedDrawable combinedDrawable = new CombinedDrawable(Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(42.0f), color, color), drawable);
            combinedDrawable.setCustomSize(AndroidUtilities.dp(42.0f), AndroidUtilities.dp(42.0f));
            combinedDrawable.setIconSize(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f));
            this.avatarImageView.setImageDrawable(combinedDrawable);
        } else {
            name = "";
            this.avatarDrawable = null;
            if (fromId > 0) {
                TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(fromId));
                if (user != null) {
                    this.avatarDrawable = new AvatarDrawable(user);
                    name = UserObject.getUserName(user);
                    this.avatarImageView.setForUserOrChat(user, this.avatarDrawable);
                }
            } else {
                TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-fromId));
                if (chat != null) {
                    this.avatarDrawable = new AvatarDrawable(chat);
                    name = chat.title;
                    this.avatarImageView.setForUserOrChat(chat, this.avatarDrawable);
                }
            }
        }
        this.nameTextView.setText(name);
        this.location.setLatitude(messageObject.messageOwner.media.geo.lat);
        this.location.setLongitude(messageObject.messageOwner.media.geo._long);
        if (userLocation != null) {
            float distance = this.location.distanceTo(userLocation);
            if (address != null) {
                this.distanceTextView.setText(String.format("%s - %s", new Object[]{address, LocaleController.formatDistance(distance, 0)}));
                return;
            }
            this.distanceTextView.setText(LocaleController.formatDistance(distance, 0));
        } else if (address != null) {
            this.distanceTextView.setText(address);
        } else {
            this.distanceTextView.setText(LocaleController.getString("Loading", NUM));
        }
    }

    public void setDialog(LocationActivity.LiveLocation info, Location userLocation) {
        this.liveLocation = info;
        if (DialogObject.isUserDialog(info.id)) {
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(info.id));
            if (user != null) {
                this.avatarDrawable.setInfo(user);
                this.nameTextView.setText(ContactsController.formatName(user.first_name, user.last_name));
                this.avatarImageView.setForUserOrChat(user, this.avatarDrawable);
            }
        } else {
            TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-info.id));
            if (chat != null) {
                this.avatarDrawable.setInfo(chat);
                this.nameTextView.setText(chat.title);
                this.avatarImageView.setForUserOrChat(chat, this.avatarDrawable);
            }
        }
        LatLng position = info.marker.getPosition();
        this.location.setLatitude(position.latitude);
        this.location.setLongitude(position.longitude);
        String time = LocaleController.formatLocationUpdateDate((long) (info.object.edit_date != 0 ? info.object.edit_date : info.object.date));
        if (userLocation != null) {
            this.distanceTextView.setText(String.format("%s - %s", new Object[]{time, LocaleController.formatDistance(this.location.distanceTo(userLocation), 0)}));
        } else {
            this.distanceTextView.setText(time);
        }
    }

    public void setDialog(LocationController.SharingLocationInfo info) {
        this.currentInfo = info;
        this.currentAccount = info.account;
        this.avatarImageView.getImageReceiver().setCurrentAccount(this.currentAccount);
        if (DialogObject.isUserDialog(info.did)) {
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(info.did));
            if (user != null) {
                this.avatarDrawable.setInfo(user);
                this.nameTextView.setText(ContactsController.formatName(user.first_name, user.last_name));
                this.avatarImageView.setForUserOrChat(user, this.avatarDrawable);
                return;
            }
            return;
        }
        TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-info.did));
        if (chat != null) {
            this.avatarDrawable.setInfo(chat);
            this.nameTextView.setText(chat.title);
            this.avatarImageView.setForUserOrChat(chat, this.avatarDrawable);
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int period;
        int stopTime;
        int color;
        LocationController.SharingLocationInfo sharingLocationInfo = this.currentInfo;
        if (sharingLocationInfo != null || this.liveLocation != null) {
            if (sharingLocationInfo != null) {
                stopTime = sharingLocationInfo.stopTime;
                period = this.currentInfo.period;
            } else {
                stopTime = this.liveLocation.object.date + this.liveLocation.object.media.period;
                period = this.liveLocation.object.media.period;
            }
            int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            if (stopTime >= currentTime) {
                float progress = ((float) Math.abs(stopTime - currentTime)) / ((float) period);
                float f = 48.0f;
                float f2 = 18.0f;
                if (LocaleController.isRTL) {
                    RectF rectF = this.rect;
                    float dp = (float) AndroidUtilities.dp(13.0f);
                    if (this.distanceTextView == null) {
                        f2 = 12.0f;
                    }
                    float dp2 = (float) AndroidUtilities.dp(f2);
                    float dp3 = (float) AndroidUtilities.dp(43.0f);
                    if (this.distanceTextView == null) {
                        f = 42.0f;
                    }
                    rectF.set(dp, dp2, dp3, (float) AndroidUtilities.dp(f));
                } else {
                    RectF rectF2 = this.rect;
                    float measuredWidth = (float) (getMeasuredWidth() - AndroidUtilities.dp(43.0f));
                    if (this.distanceTextView == null) {
                        f2 = 12.0f;
                    }
                    float dp4 = (float) AndroidUtilities.dp(f2);
                    float measuredWidth2 = (float) (getMeasuredWidth() - AndroidUtilities.dp(13.0f));
                    if (this.distanceTextView == null) {
                        f = 42.0f;
                    }
                    rectF2.set(measuredWidth, dp4, measuredWidth2, (float) AndroidUtilities.dp(f));
                }
                if (this.distanceTextView == null) {
                    color = getThemedColor("dialog_liveLocationProgress");
                } else {
                    color = getThemedColor("location_liveLocationProgress");
                }
                Theme.chat_radialProgress2Paint.setColor(color);
                Theme.chat_livePaint.setColor(color);
                canvas.drawArc(this.rect, -90.0f, progress * -360.0f, false, Theme.chat_radialProgress2Paint);
                String text = LocaleController.formatLocationLeftTime(stopTime - currentTime);
                canvas.drawText(text, this.rect.centerX() - (Theme.chat_livePaint.measureText(text) / 2.0f), (float) AndroidUtilities.dp(this.distanceTextView != null ? 37.0f : 31.0f), Theme.chat_livePaint);
            }
        }
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
