package org.telegram.ui.Cells;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController.SharingLocationInfo;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatPhoto;
import org.telegram.tgnet.TLRPC.GeoPoint;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageFwdHeader;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.LocationActivity.LiveLocation;

public class SharingLiveLocationCell
  extends FrameLayout
{
  private AvatarDrawable avatarDrawable;
  private BackupImageView avatarImageView;
  private int currentAccount;
  private LocationController.SharingLocationInfo currentInfo;
  private SimpleTextView distanceTextView;
  private Runnable invalidateRunnable = new Runnable()
  {
    public void run()
    {
      SharingLiveLocationCell.this.invalidate((int)SharingLiveLocationCell.this.rect.left - 5, (int)SharingLiveLocationCell.this.rect.top - 5, (int)SharingLiveLocationCell.this.rect.right + 5, (int)SharingLiveLocationCell.this.rect.bottom + 5);
      AndroidUtilities.runOnUIThread(SharingLiveLocationCell.this.invalidateRunnable, 1000L);
    }
  };
  private LocationActivity.LiveLocation liveLocation;
  private Location location = new Location("network");
  private SimpleTextView nameTextView;
  private RectF rect = new RectF();
  
  public SharingLiveLocationCell(Context paramContext, boolean paramBoolean)
  {
    super(paramContext);
    this.avatarImageView = new BackupImageView(paramContext);
    this.avatarImageView.setRoundRadius(AndroidUtilities.dp(20.0F));
    this.avatarDrawable = new AvatarDrawable();
    this.nameTextView = new SimpleTextView(paramContext);
    this.nameTextView.setTextSize(16);
    this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
    this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    Object localObject = this.nameTextView;
    int j;
    label164:
    float f1;
    if (LocaleController.isRTL)
    {
      j = 5;
      ((SimpleTextView)localObject).setGravity(j);
      if (!paramBoolean) {
        break label449;
      }
      localObject = this.avatarImageView;
      if (!LocaleController.isRTL) {
        break label385;
      }
      j = 5;
      if (!LocaleController.isRTL) {
        break label391;
      }
      f1 = 0.0F;
      label173:
      if (!LocaleController.isRTL) {
        break label398;
      }
      f2 = 17.0F;
      label183:
      addView((View)localObject, LayoutHelper.createFrame(40, 40.0F, j | 0x30, f1, 13.0F, f2, 0.0F));
      localObject = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label404;
      }
      j = 5;
      label223:
      if (!LocaleController.isRTL) {
        break label410;
      }
      f1 = 54.0F;
      label233:
      if (!LocaleController.isRTL) {
        break label417;
      }
      f2 = 73.0F;
      label243:
      addView((View)localObject, LayoutHelper.createFrame(-1, 20.0F, j | 0x30, f1, 12.0F, f2, 0.0F));
      this.distanceTextView = new SimpleTextView(paramContext);
      this.distanceTextView.setTextSize(14);
      this.distanceTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
      paramContext = this.distanceTextView;
      if (!LocaleController.isRTL) {
        break label424;
      }
      j = 5;
      label314:
      paramContext.setGravity(j);
      paramContext = this.distanceTextView;
      if (!LocaleController.isRTL) {
        break label430;
      }
      label331:
      if (!LocaleController.isRTL) {
        break label435;
      }
      f1 = 54.0F;
      label341:
      if (!LocaleController.isRTL) {
        break label442;
      }
    }
    label385:
    label391:
    label398:
    label404:
    label410:
    label417:
    label424:
    label430:
    label435:
    label442:
    for (float f2 = 73.0F;; f2 = 54.0F)
    {
      addView(paramContext, LayoutHelper.createFrame(-1, 20.0F, i | 0x30, f1, 37.0F, f2, 0.0F));
      setWillNotDraw(false);
      return;
      j = 3;
      break;
      j = 3;
      break label164;
      f1 = 17.0F;
      break label173;
      f2 = 0.0F;
      break label183;
      j = 3;
      break label223;
      f1 = 73.0F;
      break label233;
      f2 = 54.0F;
      break label243;
      j = 3;
      break label314;
      i = 3;
      break label331;
      f1 = 73.0F;
      break label341;
    }
    label449:
    paramContext = this.avatarImageView;
    if (LocaleController.isRTL)
    {
      j = 5;
      label463:
      if (!LocaleController.isRTL) {
        break label569;
      }
      f1 = 0.0F;
      label472:
      if (!LocaleController.isRTL) {
        break label576;
      }
      f2 = 17.0F;
      label482:
      addView(paramContext, LayoutHelper.createFrame(40, 40.0F, j | 0x30, f1, 7.0F, f2, 0.0F));
      paramContext = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label582;
      }
      label517:
      if (!LocaleController.isRTL) {
        break label587;
      }
      f1 = 54.0F;
      label527:
      if (!LocaleController.isRTL) {
        break label594;
      }
    }
    label569:
    label576:
    label582:
    label587:
    label594:
    for (f2 = 74.0F;; f2 = 54.0F)
    {
      addView(paramContext, LayoutHelper.createFrame(-2, -2.0F, i | 0x30, f1, 17.0F, f2, 0.0F));
      break;
      j = 3;
      break label463;
      f1 = 17.0F;
      break label472;
      f2 = 0.0F;
      break label482;
      i = 3;
      break label517;
      f1 = 74.0F;
      break label527;
    }
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    AndroidUtilities.runOnUIThread(this.invalidateRunnable);
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    AndroidUtilities.cancelRunOnUIThread(this.invalidateRunnable);
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if ((this.currentInfo == null) && (this.liveLocation == null)) {}
    int i;
    int j;
    label38:
    int k;
    do
    {
      return;
      if (this.currentInfo == null) {
        break;
      }
      i = this.currentInfo.stopTime;
      j = this.currentInfo.period;
      k = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
    } while (i < k);
    float f1 = Math.abs(i - k) / j;
    Object localObject;
    float f2;
    label100:
    float f4;
    float f5;
    if (LocaleController.isRTL)
    {
      localObject = this.rect;
      f2 = AndroidUtilities.dp(13.0F);
      if (this.distanceTextView != null)
      {
        f3 = 18.0F;
        f4 = AndroidUtilities.dp(f3);
        f5 = AndroidUtilities.dp(43.0F);
        if (this.distanceTextView == null) {
          break label307;
        }
        f3 = 48.0F;
        label127:
        ((RectF)localObject).set(f2, f4, f5, AndroidUtilities.dp(f3));
        if (this.distanceTextView != null) {
          break label412;
        }
        j = Theme.getColor("location_liveLocationProgress");
        label157:
        Theme.chat_radialProgress2Paint.setColor(j);
        Theme.chat_livePaint.setColor(j);
        paramCanvas.drawArc(this.rect, -90.0F, -360.0F * f1, false, Theme.chat_radialProgress2Paint);
        localObject = LocaleController.formatLocationLeftTime(i - k);
        f3 = Theme.chat_livePaint.measureText((String)localObject);
        f1 = this.rect.centerX();
        f2 = f3 / 2.0F;
        if (this.distanceTextView == null) {
          break label421;
        }
      }
    }
    label307:
    label345:
    label404:
    label412:
    label421:
    for (float f3 = 37.0F;; f3 = 31.0F)
    {
      paramCanvas.drawText((String)localObject, f1 - f2, AndroidUtilities.dp(f3), Theme.chat_livePaint);
      break;
      i = this.liveLocation.object.date + this.liveLocation.object.media.period;
      j = this.liveLocation.object.media.period;
      break label38;
      f3 = 12.0F;
      break label100;
      f3 = 42.0F;
      break label127;
      localObject = this.rect;
      f2 = getMeasuredWidth() - AndroidUtilities.dp(43.0F);
      if (this.distanceTextView != null)
      {
        f3 = 18.0F;
        f5 = AndroidUtilities.dp(f3);
        f4 = getMeasuredWidth() - AndroidUtilities.dp(13.0F);
        if (this.distanceTextView == null) {
          break label404;
        }
      }
      for (f3 = 48.0F;; f3 = 42.0F)
      {
        ((RectF)localObject).set(f2, f5, f4, AndroidUtilities.dp(f3));
        break;
        f3 = 12.0F;
        break label345;
      }
      j = Theme.getColor("location_liveLocationProgress");
      break label157;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    paramInt1 = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), NUM);
    if (this.distanceTextView != null) {}
    for (float f = 66.0F;; f = 54.0F)
    {
      super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(f), NUM));
      return;
    }
  }
  
  public void setDialog(LocationController.SharingLocationInfo paramSharingLocationInfo)
  {
    this.currentInfo = paramSharingLocationInfo;
    int i = (int)paramSharingLocationInfo.did;
    Object localObject1 = null;
    Object localObject2;
    if (i > 0)
    {
      localObject2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i));
      paramSharingLocationInfo = (LocationController.SharingLocationInfo)localObject1;
      if (localObject2 != null)
      {
        this.avatarDrawable.setInfo((TLRPC.User)localObject2);
        this.nameTextView.setText(ContactsController.formatName(((TLRPC.User)localObject2).first_name, ((TLRPC.User)localObject2).last_name));
        paramSharingLocationInfo = (LocationController.SharingLocationInfo)localObject1;
        if (((TLRPC.User)localObject2).photo != null)
        {
          paramSharingLocationInfo = (LocationController.SharingLocationInfo)localObject1;
          if (((TLRPC.User)localObject2).photo.photo_small != null) {
            paramSharingLocationInfo = ((TLRPC.User)localObject2).photo.photo_small;
          }
        }
      }
    }
    for (;;)
    {
      this.avatarImageView.setImage(paramSharingLocationInfo, null, this.avatarDrawable);
      return;
      localObject2 = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i));
      paramSharingLocationInfo = (LocationController.SharingLocationInfo)localObject1;
      if (localObject2 != null)
      {
        this.avatarDrawable.setInfo((TLRPC.Chat)localObject2);
        this.nameTextView.setText(((TLRPC.Chat)localObject2).title);
        paramSharingLocationInfo = (LocationController.SharingLocationInfo)localObject1;
        if (((TLRPC.Chat)localObject2).photo != null)
        {
          paramSharingLocationInfo = (LocationController.SharingLocationInfo)localObject1;
          if (((TLRPC.Chat)localObject2).photo.photo_small != null) {
            paramSharingLocationInfo = ((TLRPC.Chat)localObject2).photo.photo_small;
          }
        }
      }
    }
  }
  
  public void setDialog(MessageObject paramMessageObject, Location paramLocation)
  {
    int i = paramMessageObject.messageOwner.from_id;
    String str1;
    Object localObject;
    TLRPC.User localUser;
    TLRPC.Chat localChat;
    String str2;
    float f;
    if (paramMessageObject.isForwarded())
    {
      if (paramMessageObject.messageOwner.fwd_from.channel_id != 0) {
        i = -paramMessageObject.messageOwner.fwd_from.channel_id;
      }
    }
    else
    {
      this.currentAccount = paramMessageObject.currentAccount;
      str1 = null;
      localObject = null;
      localUser = null;
      localChat = null;
      if (!TextUtils.isEmpty(paramMessageObject.messageOwner.media.address)) {
        str1 = paramMessageObject.messageOwner.media.address;
      }
      if (TextUtils.isEmpty(paramMessageObject.messageOwner.media.title)) {
        break label353;
      }
      str2 = paramMessageObject.messageOwner.media.title;
      localObject = getResources().getDrawable(NUM);
      ((Drawable)localObject).setColorFilter(new PorterDuffColorFilter(Theme.getColor("location_sendLocationIcon"), PorterDuff.Mode.MULTIPLY));
      i = Theme.getColor("location_placeLocationBackground");
      localObject = new CombinedDrawable(Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(40.0F), i, i), (Drawable)localObject);
      ((CombinedDrawable)localObject).setCustomSize(AndroidUtilities.dp(40.0F), AndroidUtilities.dp(40.0F));
      ((CombinedDrawable)localObject).setIconSize(AndroidUtilities.dp(24.0F), AndroidUtilities.dp(24.0F));
      this.avatarImageView.setImageDrawable((Drawable)localObject);
      this.nameTextView.setText(str2);
      this.location.setLatitude(paramMessageObject.messageOwner.media.geo.lat);
      this.location.setLongitude(paramMessageObject.messageOwner.media.geo._long);
      if (paramLocation == null) {
        break label657;
      }
      f = this.location.distanceTo(paramLocation);
      if (str1 == null) {
        break label563;
      }
      if (f >= 1000.0F) {
        break label514;
      }
      this.distanceTextView.setText(String.format("%s - %d %s", new Object[] { str1, Integer.valueOf((int)f), LocaleController.getString("MetersAway", NUM) }));
    }
    for (;;)
    {
      return;
      i = paramMessageObject.messageOwner.fwd_from.from_id;
      break;
      label353:
      str2 = "";
      this.avatarDrawable = null;
      if (i > 0)
      {
        localUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i));
        if (localUser != null)
        {
          localObject = localChat;
          if (localUser.photo != null) {
            localObject = localUser.photo.photo_small;
          }
          this.avatarDrawable = new AvatarDrawable(localUser);
          str2 = UserObject.getUserName(localUser);
        }
      }
      for (;;)
      {
        this.avatarImageView.setImage((TLObject)localObject, null, this.avatarDrawable);
        break;
        localChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i));
        if (localChat != null)
        {
          localObject = localUser;
          if (localChat.photo != null) {
            localObject = localChat.photo.photo_small;
          }
          this.avatarDrawable = new AvatarDrawable(localChat);
          str2 = localChat.title;
        }
      }
      label514:
      this.distanceTextView.setText(String.format("%s - %.2f %s", new Object[] { str1, Float.valueOf(f / 1000.0F), LocaleController.getString("KMetersAway", NUM) }));
      continue;
      label563:
      if (f < 1000.0F)
      {
        this.distanceTextView.setText(String.format("%d %s", new Object[] { Integer.valueOf((int)f), LocaleController.getString("MetersAway", NUM) }));
      }
      else
      {
        this.distanceTextView.setText(String.format("%.2f %s", new Object[] { Float.valueOf(f / 1000.0F), LocaleController.getString("KMetersAway", NUM) }));
        continue;
        label657:
        if (str1 != null) {
          this.distanceTextView.setText(str1);
        } else {
          this.distanceTextView.setText(LocaleController.getString("Loading", NUM));
        }
      }
    }
  }
  
  public void setDialog(LocationActivity.LiveLocation paramLiveLocation, Location paramLocation)
  {
    this.liveLocation = paramLiveLocation;
    int i = paramLiveLocation.id;
    LatLng localLatLng = null;
    Object localObject1;
    Object localObject2;
    long l;
    label161:
    float f;
    if (i > 0)
    {
      localObject1 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i));
      this.avatarDrawable.setInfo((TLRPC.User)localObject1);
      localObject2 = localLatLng;
      if (localObject1 != null)
      {
        this.nameTextView.setText(ContactsController.formatName(((TLRPC.User)localObject1).first_name, ((TLRPC.User)localObject1).last_name));
        localObject2 = localLatLng;
        if (((TLRPC.User)localObject1).photo != null)
        {
          localObject2 = localLatLng;
          if (((TLRPC.User)localObject1).photo.photo_small != null) {
            localObject2 = ((TLRPC.User)localObject1).photo.photo_small;
          }
        }
      }
      localLatLng = paramLiveLocation.marker.getPosition();
      this.location.setLatitude(localLatLng.latitude);
      this.location.setLongitude(localLatLng.longitude);
      if (paramLiveLocation.object.edit_date == 0) {
        break label334;
      }
      l = paramLiveLocation.object.edit_date;
      paramLiveLocation = LocaleController.formatLocationUpdateDate(l);
      if (paramLocation == null) {
        break label395;
      }
      f = this.location.distanceTo(paramLocation);
      if (f >= 1000.0F) {
        break label347;
      }
      this.distanceTextView.setText(String.format("%s - %d %s", new Object[] { paramLiveLocation, Integer.valueOf((int)f), LocaleController.getString("MetersAway", NUM) }));
    }
    for (;;)
    {
      this.avatarImageView.setImage((TLObject)localObject2, null, this.avatarDrawable);
      return;
      localObject1 = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i));
      localObject2 = localLatLng;
      if (localObject1 == null) {
        break;
      }
      this.avatarDrawable.setInfo((TLRPC.Chat)localObject1);
      this.nameTextView.setText(((TLRPC.Chat)localObject1).title);
      localObject2 = localLatLng;
      if (((TLRPC.Chat)localObject1).photo == null) {
        break;
      }
      localObject2 = localLatLng;
      if (((TLRPC.Chat)localObject1).photo.photo_small == null) {
        break;
      }
      localObject2 = ((TLRPC.Chat)localObject1).photo.photo_small;
      break;
      label334:
      l = paramLiveLocation.object.date;
      break label161;
      label347:
      this.distanceTextView.setText(String.format("%s - %.2f %s", new Object[] { paramLiveLocation, Float.valueOf(f / 1000.0F), LocaleController.getString("KMetersAway", NUM) }));
      continue;
      label395:
      this.distanceTextView.setText(paramLiveLocation);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/SharingLiveLocationCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */