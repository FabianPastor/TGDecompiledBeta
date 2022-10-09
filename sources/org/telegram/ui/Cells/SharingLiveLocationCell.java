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
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.IMapsProvider;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$TL_channelLocation;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.LocationActivity;
/* loaded from: classes3.dex */
public class SharingLiveLocationCell extends FrameLayout {
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImageView;
    private int currentAccount;
    private LocationController.SharingLocationInfo currentInfo;
    private SimpleTextView distanceTextView;
    private Runnable invalidateRunnable;
    private LocationActivity.LiveLocation liveLocation;
    private Location location;
    private SimpleTextView nameTextView;
    private RectF rect;
    private final Theme.ResourcesProvider resourcesProvider;

    public SharingLiveLocationCell(Context context, boolean z, int i, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.rect = new RectF();
        this.location = new Location("network");
        this.currentAccount = UserConfig.selectedAccount;
        this.invalidateRunnable = new Runnable() { // from class: org.telegram.ui.Cells.SharingLiveLocationCell.1
            @Override // java.lang.Runnable
            public void run() {
                SharingLiveLocationCell sharingLiveLocationCell = SharingLiveLocationCell.this;
                sharingLiveLocationCell.invalidate(((int) sharingLiveLocationCell.rect.left) - 5, ((int) SharingLiveLocationCell.this.rect.top) - 5, ((int) SharingLiveLocationCell.this.rect.right) + 5, ((int) SharingLiveLocationCell.this.rect.bottom) + 5);
                AndroidUtilities.runOnUIThread(SharingLiveLocationCell.this.invalidateRunnable, 1000L);
            }
        };
        this.resourcesProvider = resourcesProvider;
        BackupImageView backupImageView = new BackupImageView(context);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(21.0f));
        this.avatarDrawable = new AvatarDrawable();
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.nameTextView = simpleTextView;
        simpleTextView.setTextSize(16);
        this.nameTextView.setTextColor(getThemedColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        int i2 = 5;
        this.nameTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        float f = 0.0f;
        if (z) {
            BackupImageView backupImageView2 = this.avatarImageView;
            boolean z2 = LocaleController.isRTL;
            addView(backupImageView2, LayoutHelper.createFrame(42, 42.0f, (z2 ? 5 : 3) | 48, z2 ? 0.0f : 15.0f, 12.0f, z2 ? 15.0f : f, 0.0f));
            SimpleTextView simpleTextView2 = this.nameTextView;
            boolean z3 = LocaleController.isRTL;
            addView(simpleTextView2, LayoutHelper.createFrame(-1, 20.0f, (z3 ? 5 : 3) | 48, z3 ? i : 73.0f, 12.0f, z3 ? 73.0f : i, 0.0f));
            SimpleTextView simpleTextView3 = new SimpleTextView(context);
            this.distanceTextView = simpleTextView3;
            simpleTextView3.setTextSize(14);
            this.distanceTextView.setTextColor(getThemedColor("windowBackgroundWhiteGrayText3"));
            this.distanceTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            SimpleTextView simpleTextView4 = this.distanceTextView;
            boolean z4 = LocaleController.isRTL;
            addView(simpleTextView4, LayoutHelper.createFrame(-1, 20.0f, (!z4 ? 3 : i2) | 48, z4 ? i : 73.0f, 37.0f, z4 ? 73.0f : i, 0.0f));
        } else {
            BackupImageView backupImageView3 = this.avatarImageView;
            boolean z5 = LocaleController.isRTL;
            addView(backupImageView3, LayoutHelper.createFrame(42, 42.0f, (z5 ? 5 : 3) | 48, z5 ? 0.0f : 15.0f, 6.0f, z5 ? 15.0f : 0.0f, 0.0f));
            SimpleTextView simpleTextView5 = this.nameTextView;
            boolean z6 = LocaleController.isRTL;
            addView(simpleTextView5, LayoutHelper.createFrame(-2, -2.0f, (!z6 ? 3 : i2) | 48, z6 ? i : 74.0f, 17.0f, z6 ? 74.0f : i, 0.0f));
        }
        setWillNotDraw(false);
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.distanceTextView != null ? 66.0f : 54.0f), NUM));
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        AndroidUtilities.cancelRunOnUIThread(this.invalidateRunnable);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        AndroidUtilities.runOnUIThread(this.invalidateRunnable);
    }

    public void setDialog(long j, TLRPC$TL_channelLocation tLRPC$TL_channelLocation) {
        this.currentAccount = UserConfig.selectedAccount;
        String str = tLRPC$TL_channelLocation.address;
        this.avatarDrawable = null;
        String str2 = "";
        if (DialogObject.isUserDialog(j)) {
            TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(j));
            if (user != null) {
                this.avatarDrawable = new AvatarDrawable(user);
                String userName = UserObject.getUserName(user);
                this.avatarImageView.setForUserOrChat(user, this.avatarDrawable);
                str2 = userName;
            }
        } else {
            TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-j));
            if (chat != null) {
                AvatarDrawable avatarDrawable = new AvatarDrawable(chat);
                this.avatarDrawable = avatarDrawable;
                str2 = chat.title;
                this.avatarImageView.setForUserOrChat(chat, avatarDrawable);
            }
        }
        this.nameTextView.setText(str2);
        this.location.setLatitude(tLRPC$TL_channelLocation.geo_point.lat);
        this.location.setLongitude(tLRPC$TL_channelLocation.geo_point._long);
        this.distanceTextView.setText(str);
    }

    public void setDialog(MessageObject messageObject, Location location, boolean z) {
        String str;
        long fromChatId = messageObject.getFromChatId();
        if (messageObject.isForwarded()) {
            fromChatId = MessageObject.getPeerId(messageObject.messageOwner.fwd_from.from_id);
        }
        this.currentAccount = messageObject.currentAccount;
        String str2 = !TextUtils.isEmpty(messageObject.messageOwner.media.address) ? messageObject.messageOwner.media.address : null;
        if (!TextUtils.isEmpty(messageObject.messageOwner.media.title)) {
            str = messageObject.messageOwner.media.title;
            Drawable drawable = getResources().getDrawable(R.drawable.pin);
            drawable.setColorFilter(new PorterDuffColorFilter(getThemedColor("location_sendLocationIcon"), PorterDuff.Mode.MULTIPLY));
            int themedColor = getThemedColor("location_placeLocationBackground");
            CombinedDrawable combinedDrawable = new CombinedDrawable(Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(42.0f), themedColor, themedColor), drawable);
            combinedDrawable.setCustomSize(AndroidUtilities.dp(42.0f), AndroidUtilities.dp(42.0f));
            combinedDrawable.setIconSize(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f));
            this.avatarImageView.setImageDrawable(combinedDrawable);
        } else {
            this.avatarDrawable = null;
            if (fromChatId > 0) {
                TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(fromChatId));
                if (user != null) {
                    this.avatarDrawable = new AvatarDrawable(user);
                    String userName = UserObject.getUserName(user);
                    this.avatarImageView.setForUserOrChat(user, this.avatarDrawable);
                    str = userName;
                }
                str = "";
            } else {
                TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-fromChatId));
                if (chat != null) {
                    AvatarDrawable avatarDrawable = new AvatarDrawable(chat);
                    this.avatarDrawable = avatarDrawable;
                    String str3 = chat.title;
                    this.avatarImageView.setForUserOrChat(chat, avatarDrawable);
                    str = str3;
                }
                str = "";
            }
        }
        this.nameTextView.setText(str);
        this.location.setLatitude(messageObject.messageOwner.media.geo.lat);
        this.location.setLongitude(messageObject.messageOwner.media.geo._long);
        if (location != null) {
            float distanceTo = this.location.distanceTo(location);
            if (str2 != null) {
                this.distanceTextView.setText(String.format("%s - %s", str2, LocaleController.formatDistance(distanceTo, 0)));
            } else {
                this.distanceTextView.setText(LocaleController.formatDistance(distanceTo, 0));
            }
        } else if (str2 != null) {
            this.distanceTextView.setText(str2);
        } else if (!z) {
            this.distanceTextView.setText(LocaleController.getString("Loading", R.string.Loading));
        } else {
            this.distanceTextView.setText("");
        }
    }

    public void setDialog(LocationActivity.LiveLocation liveLocation, Location location) {
        this.liveLocation = liveLocation;
        if (DialogObject.isUserDialog(liveLocation.id)) {
            TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(liveLocation.id));
            if (user != null) {
                this.avatarDrawable.setInfo(user);
                this.nameTextView.setText(ContactsController.formatName(user.first_name, user.last_name));
                this.avatarImageView.setForUserOrChat(user, this.avatarDrawable);
            }
        } else {
            TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-liveLocation.id));
            if (chat != null) {
                this.avatarDrawable.setInfo(chat);
                this.nameTextView.setText(chat.title);
                this.avatarImageView.setForUserOrChat(chat, this.avatarDrawable);
            }
        }
        IMapsProvider.LatLng position = liveLocation.marker.getPosition();
        this.location.setLatitude(position.latitude);
        this.location.setLongitude(position.longitude);
        TLRPC$Message tLRPC$Message = liveLocation.object;
        int i = tLRPC$Message.edit_date;
        String formatLocationUpdateDate = LocaleController.formatLocationUpdateDate(i != 0 ? i : tLRPC$Message.date);
        if (location != null) {
            this.distanceTextView.setText(String.format("%s - %s", formatLocationUpdateDate, LocaleController.formatDistance(this.location.distanceTo(location), 0)));
        } else {
            this.distanceTextView.setText(formatLocationUpdateDate);
        }
    }

    public void setDialog(LocationController.SharingLocationInfo sharingLocationInfo) {
        this.currentInfo = sharingLocationInfo;
        this.currentAccount = sharingLocationInfo.account;
        this.avatarImageView.getImageReceiver().setCurrentAccount(this.currentAccount);
        if (DialogObject.isUserDialog(sharingLocationInfo.did)) {
            TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(sharingLocationInfo.did));
            if (user == null) {
                return;
            }
            this.avatarDrawable.setInfo(user);
            this.nameTextView.setText(ContactsController.formatName(user.first_name, user.last_name));
            this.avatarImageView.setForUserOrChat(user, this.avatarDrawable);
            return;
        }
        TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-sharingLocationInfo.did));
        if (chat == null) {
            return;
        }
        this.avatarDrawable.setInfo(chat);
        this.nameTextView.setText(chat.title);
        this.avatarImageView.setForUserOrChat(chat, this.avatarDrawable);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int i;
        int i2;
        int themedColor;
        LocationController.SharingLocationInfo sharingLocationInfo = this.currentInfo;
        if (sharingLocationInfo == null && this.liveLocation == null) {
            return;
        }
        if (sharingLocationInfo != null) {
            i2 = sharingLocationInfo.stopTime;
            i = sharingLocationInfo.period;
        } else {
            TLRPC$Message tLRPC$Message = this.liveLocation.object;
            int i3 = tLRPC$Message.date;
            i = tLRPC$Message.media.period;
            i2 = i3 + i;
        }
        int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
        if (i2 < currentTime) {
            return;
        }
        int i4 = i2 - currentTime;
        float abs = Math.abs(i4) / i;
        float f = 48.0f;
        float f2 = 18.0f;
        if (LocaleController.isRTL) {
            RectF rectF = this.rect;
            float dp = AndroidUtilities.dp(13.0f);
            if (this.distanceTextView == null) {
                f2 = 12.0f;
            }
            float dp2 = AndroidUtilities.dp(f2);
            float dp3 = AndroidUtilities.dp(43.0f);
            if (this.distanceTextView == null) {
                f = 42.0f;
            }
            rectF.set(dp, dp2, dp3, AndroidUtilities.dp(f));
        } else {
            RectF rectF2 = this.rect;
            float measuredWidth = getMeasuredWidth() - AndroidUtilities.dp(43.0f);
            if (this.distanceTextView == null) {
                f2 = 12.0f;
            }
            float dp4 = AndroidUtilities.dp(f2);
            float measuredWidth2 = getMeasuredWidth() - AndroidUtilities.dp(13.0f);
            if (this.distanceTextView == null) {
                f = 42.0f;
            }
            rectF2.set(measuredWidth, dp4, measuredWidth2, AndroidUtilities.dp(f));
        }
        if (this.distanceTextView == null) {
            themedColor = getThemedColor("dialog_liveLocationProgress");
        } else {
            themedColor = getThemedColor("location_liveLocationProgress");
        }
        Theme.chat_radialProgress2Paint.setColor(themedColor);
        Theme.chat_livePaint.setColor(themedColor);
        canvas.drawArc(this.rect, -90.0f, abs * (-360.0f), false, Theme.chat_radialProgress2Paint);
        String formatLocationLeftTime = LocaleController.formatLocationLeftTime(i4);
        canvas.drawText(formatLocationLeftTime, this.rect.centerX() - (Theme.chat_livePaint.measureText(formatLocationLeftTime) / 2.0f), AndroidUtilities.dp(this.distanceTextView != null ? 37.0f : 31.0f), Theme.chat_livePaint);
    }

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }
}
