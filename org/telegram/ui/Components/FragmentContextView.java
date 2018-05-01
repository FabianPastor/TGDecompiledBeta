package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.text.SpannableStringBuilder;
import android.text.TextUtils.TruncateAt;
import android.util.LongSparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.LocationController.SharingLocationInfo;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.LocationActivity;
import org.telegram.ui.LocationActivity.LocationActivityDelegate;
import org.telegram.ui.VoIPActivity;

public class FragmentContextView
  extends FrameLayout
  implements NotificationCenter.NotificationCenterDelegate
{
  private FragmentContextView additionalContextView;
  private AnimatorSet animatorSet;
  private Runnable checkLocationRunnable = new Runnable()
  {
    public void run()
    {
      FragmentContextView.this.checkLocationString();
      AndroidUtilities.runOnUIThread(FragmentContextView.this.checkLocationRunnable, 1000L);
    }
  };
  private ImageView closeButton;
  private int currentStyle = -1;
  private boolean firstLocationsLoaded;
  private BaseFragment fragment;
  private FrameLayout frameLayout;
  private boolean isLocation;
  private int lastLocationSharingCount = -1;
  private MessageObject lastMessageObject;
  private String lastString;
  private boolean loadingSharingCount;
  private ImageView playButton;
  private TextView titleTextView;
  private float topPadding;
  private boolean visible;
  private float yPosition;
  
  public FragmentContextView(Context paramContext, BaseFragment paramBaseFragment, boolean paramBoolean)
  {
    super(paramContext);
    this.fragment = paramBaseFragment;
    this.visible = true;
    this.isLocation = paramBoolean;
    ((ViewGroup)this.fragment.getFragmentView()).setClipToPadding(false);
    setTag(Integer.valueOf(1));
    this.frameLayout = new FrameLayout(paramContext);
    this.frameLayout.setWillNotDraw(false);
    addView(this.frameLayout, LayoutHelper.createFrame(-1, 36.0F, 51, 0.0F, 0.0F, 0.0F, 0.0F));
    paramBaseFragment = new View(paramContext);
    paramBaseFragment.setBackgroundResource(NUM);
    addView(paramBaseFragment, LayoutHelper.createFrame(-1, 3.0F, 51, 0.0F, 36.0F, 0.0F, 0.0F));
    this.playButton = new ImageView(paramContext);
    this.playButton.setScaleType(ImageView.ScaleType.CENTER);
    this.playButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("inappPlayerPlayPause"), PorterDuff.Mode.MULTIPLY));
    addView(this.playButton, LayoutHelper.createFrame(36, 36.0F, 51, 0.0F, 0.0F, 0.0F, 0.0F));
    this.playButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (FragmentContextView.this.currentStyle == 0)
        {
          if (!MediaController.getInstance().isMessagePaused()) {
            break label33;
          }
          MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
        }
        for (;;)
        {
          return;
          label33:
          MediaController.getInstance().pauseMessage(MediaController.getInstance().getPlayingMessageObject());
        }
      }
    });
    this.titleTextView = new TextView(paramContext);
    this.titleTextView.setMaxLines(1);
    this.titleTextView.setLines(1);
    this.titleTextView.setSingleLine(true);
    this.titleTextView.setEllipsize(TextUtils.TruncateAt.END);
    this.titleTextView.setTextSize(1, 15.0F);
    this.titleTextView.setGravity(19);
    addView(this.titleTextView, LayoutHelper.createFrame(-1, 36.0F, 51, 35.0F, 0.0F, 36.0F, 0.0F));
    this.closeButton = new ImageView(paramContext);
    this.closeButton.setImageResource(NUM);
    this.closeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("inappPlayerClose"), PorterDuff.Mode.MULTIPLY));
    this.closeButton.setScaleType(ImageView.ScaleType.CENTER);
    addView(this.closeButton, LayoutHelper.createFrame(36, 36, 53));
    this.closeButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        AlertDialog.Builder localBuilder;
        if (FragmentContextView.this.currentStyle == 2)
        {
          localBuilder = new AlertDialog.Builder(FragmentContextView.this.fragment.getParentActivity());
          localBuilder.setTitle(LocaleController.getString("AppName", NUM));
          if ((FragmentContextView.this.fragment instanceof DialogsActivity))
          {
            localBuilder.setMessage(LocaleController.getString("StopLiveLocationAlertAll", NUM));
            localBuilder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                if ((FragmentContextView.this.fragment instanceof DialogsActivity)) {
                  for (paramAnonymous2Int = 0; paramAnonymous2Int < 3; paramAnonymous2Int++) {
                    LocationController.getInstance(paramAnonymous2Int).removeAllLocationSharings();
                  }
                }
                LocationController.getInstance(FragmentContextView.this.fragment.getCurrentAccount()).removeSharingLocation(((ChatActivity)FragmentContextView.this.fragment).getDialogId());
              }
            });
            localBuilder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
            localBuilder.show();
          }
        }
        for (;;)
        {
          return;
          Object localObject = (ChatActivity)FragmentContextView.this.fragment;
          paramAnonymousView = ((ChatActivity)localObject).getCurrentChat();
          localObject = ((ChatActivity)localObject).getCurrentUser();
          if (paramAnonymousView != null)
          {
            localBuilder.setMessage(LocaleController.formatString("StopLiveLocationAlertToGroup", NUM, new Object[] { paramAnonymousView.title }));
            break;
          }
          if (localObject != null)
          {
            localBuilder.setMessage(LocaleController.formatString("StopLiveLocationAlertToUser", NUM, new Object[] { UserObject.getFirstName((TLRPC.User)localObject) }));
            break;
          }
          localBuilder.setMessage(LocaleController.getString("AreYouSure", NUM));
          break;
          MediaController.getInstance().cleanupPlayer(true, true);
        }
      }
    });
    setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (FragmentContextView.this.currentStyle == 0)
        {
          paramAnonymousView = MediaController.getInstance().getPlayingMessageObject();
          if ((FragmentContextView.this.fragment != null) && (paramAnonymousView != null))
          {
            if (!paramAnonymousView.isMusic()) {
              break label64;
            }
            FragmentContextView.this.fragment.showDialog(new AudioPlayerAlert(FragmentContextView.this.getContext()));
          }
        }
        for (;;)
        {
          return;
          label64:
          long l1 = 0L;
          if ((FragmentContextView.this.fragment instanceof ChatActivity)) {
            l1 = ((ChatActivity)FragmentContextView.this.fragment).getDialogId();
          }
          if (paramAnonymousView.getDialogId() == l1)
          {
            ((ChatActivity)FragmentContextView.this.fragment).scrollToMessageId(paramAnonymousView.getId(), 0, false, 0, true);
          }
          else
          {
            l1 = paramAnonymousView.getDialogId();
            Bundle localBundle = new Bundle();
            int i = (int)l1;
            int j = (int)(l1 >> 32);
            if (i != 0) {
              if (j == 1) {
                localBundle.putInt("chat_id", i);
              }
            }
            for (;;)
            {
              localBundle.putInt("message_id", paramAnonymousView.getId());
              FragmentContextView.this.fragment.presentFragment(new ChatActivity(localBundle), FragmentContextView.this.fragment instanceof ChatActivity);
              break;
              if (i > 0)
              {
                localBundle.putInt("user_id", i);
              }
              else if (i < 0)
              {
                localBundle.putInt("chat_id", -i);
                continue;
                localBundle.putInt("enc_id", j);
              }
            }
            if (FragmentContextView.this.currentStyle == 1)
            {
              paramAnonymousView = new Intent(FragmentContextView.this.getContext(), VoIPActivity.class);
              paramAnonymousView.addFlags(805306368);
              FragmentContextView.this.getContext().startActivity(paramAnonymousView);
            }
            else if (FragmentContextView.this.currentStyle == 2)
            {
              long l2 = 0L;
              int k = UserConfig.selectedAccount;
              if ((FragmentContextView.this.fragment instanceof ChatActivity))
              {
                l1 = ((ChatActivity)FragmentContextView.this.fragment).getDialogId();
                j = FragmentContextView.this.fragment.getCurrentAccount();
              }
              for (;;)
              {
                if (l1 == 0L) {
                  break label479;
                }
                FragmentContextView.this.openSharingLocation(LocationController.getInstance(j).getSharingLocationInfo(l1));
                break;
                if (LocationController.getLocationsCount() == 1) {
                  for (i = 0;; i++)
                  {
                    j = k;
                    l1 = l2;
                    if (i >= 3) {
                      break;
                    }
                    if (!LocationController.getInstance(i).sharingLocationsUI.isEmpty())
                    {
                      paramAnonymousView = (LocationController.SharingLocationInfo)LocationController.getInstance(i).sharingLocationsUI.get(0);
                      l1 = paramAnonymousView.did;
                      j = paramAnonymousView.messageObject.currentAccount;
                      break;
                    }
                  }
                }
                l1 = 0L;
                j = k;
              }
              label479:
              FragmentContextView.this.fragment.showDialog(new SharingLocationsAlert(FragmentContextView.this.getContext(), new SharingLocationsAlert.SharingLocationsAlertDelegate()
              {
                public void didSelectLocation(LocationController.SharingLocationInfo paramAnonymous2SharingLocationInfo)
                {
                  FragmentContextView.this.openSharingLocation(paramAnonymous2SharingLocationInfo);
                }
              }));
            }
          }
        }
      }
    });
  }
  
  private void checkCall(boolean paramBoolean)
  {
    View localView = this.fragment.getFragmentView();
    boolean bool = paramBoolean;
    if (!paramBoolean)
    {
      bool = paramBoolean;
      if (localView != null) {
        if (localView.getParent() != null)
        {
          bool = paramBoolean;
          if (((View)localView.getParent()).getVisibility() == 0) {}
        }
        else
        {
          bool = true;
        }
      }
    }
    int i;
    if ((VoIPService.getSharedInstance() != null) && (VoIPService.getSharedInstance().getCallState() != 15))
    {
      i = 1;
      if (i != 0) {
        break label229;
      }
      if (this.visible)
      {
        this.visible = false;
        if (!bool) {
          break label112;
        }
        if (getVisibility() != 8) {
          setVisibility(8);
        }
        setTopPadding(0.0F);
      }
    }
    label112:
    label229:
    label291:
    do
    {
      for (;;)
      {
        return;
        i = 0;
        break;
        if (this.animatorSet != null)
        {
          this.animatorSet.cancel();
          this.animatorSet = null;
        }
        this.animatorSet = new AnimatorSet();
        this.animatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this, "translationY", new float[] { -AndroidUtilities.dp2(36.0F) }), ObjectAnimator.ofFloat(this, "topPadding", new float[] { 0.0F }) });
        this.animatorSet.setDuration(200L);
        this.animatorSet.addListener(new AnimatorListenerAdapter()
        {
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            if ((FragmentContextView.this.animatorSet != null) && (FragmentContextView.this.animatorSet.equals(paramAnonymousAnimator)))
            {
              FragmentContextView.this.setVisibility(8);
              FragmentContextView.access$502(FragmentContextView.this, null);
            }
          }
        });
        this.animatorSet.start();
      }
      updateStyle(1);
      if ((bool) && (this.topPadding == 0.0F))
      {
        setTopPadding(AndroidUtilities.dp2(36.0F));
        if ((this.additionalContextView == null) || (this.additionalContextView.getVisibility() != 0)) {
          break label482;
        }
        ((FrameLayout.LayoutParams)getLayoutParams()).topMargin = (-AndroidUtilities.dp(72.0F));
        setTranslationY(0.0F);
        this.yPosition = 0.0F;
      }
    } while (this.visible);
    if (!bool)
    {
      if (this.animatorSet != null)
      {
        this.animatorSet.cancel();
        this.animatorSet = null;
      }
      this.animatorSet = new AnimatorSet();
      if ((this.additionalContextView == null) || (this.additionalContextView.getVisibility() != 0)) {
        break label501;
      }
    }
    label482:
    label501:
    for (((FrameLayout.LayoutParams)getLayoutParams()).topMargin = (-AndroidUtilities.dp(72.0F));; ((FrameLayout.LayoutParams)getLayoutParams()).topMargin = (-AndroidUtilities.dp(36.0F)))
    {
      this.animatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this, "translationY", new float[] { -AndroidUtilities.dp2(36.0F), 0.0F }), ObjectAnimator.ofFloat(this, "topPadding", new float[] { AndroidUtilities.dp2(36.0F) }) });
      this.animatorSet.setDuration(200L);
      this.animatorSet.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          if ((FragmentContextView.this.animatorSet != null) && (FragmentContextView.this.animatorSet.equals(paramAnonymousAnimator))) {
            FragmentContextView.access$502(FragmentContextView.this, null);
          }
        }
      });
      this.animatorSet.start();
      this.visible = true;
      setVisibility(0);
      break;
      ((FrameLayout.LayoutParams)getLayoutParams()).topMargin = (-AndroidUtilities.dp(36.0F));
      break label291;
    }
  }
  
  private void checkLiveLocation(boolean paramBoolean)
  {
    Object localObject = this.fragment.getFragmentView();
    boolean bool = paramBoolean;
    if (!paramBoolean)
    {
      bool = paramBoolean;
      if (localObject != null) {
        if (((View)localObject).getParent() != null)
        {
          bool = paramBoolean;
          if (((View)((View)localObject).getParent()).getVisibility() == 0) {}
        }
        else
        {
          bool = true;
        }
      }
    }
    if ((this.fragment instanceof DialogsActivity)) {
      if (LocationController.getLocationsCount() != 0)
      {
        paramBoolean = true;
        if (paramBoolean) {
          break label264;
        }
        this.lastLocationSharingCount = -1;
        AndroidUtilities.cancelRunOnUIThread(this.checkLocationRunnable);
        if (this.visible)
        {
          this.visible = false;
          if (!bool) {
            break label147;
          }
          if (getVisibility() != 8) {
            setVisibility(8);
          }
          setTopPadding(0.0F);
        }
      }
    }
    for (;;)
    {
      return;
      paramBoolean = false;
      break;
      paramBoolean = LocationController.getInstance(this.fragment.getCurrentAccount()).isSharingLocation(((ChatActivity)this.fragment).getDialogId());
      break;
      label147:
      if (this.animatorSet != null)
      {
        this.animatorSet.cancel();
        this.animatorSet = null;
      }
      this.animatorSet = new AnimatorSet();
      this.animatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this, "translationY", new float[] { -AndroidUtilities.dp2(36.0F) }), ObjectAnimator.ofFloat(this, "topPadding", new float[] { 0.0F }) });
      this.animatorSet.setDuration(200L);
      this.animatorSet.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          if ((FragmentContextView.this.animatorSet != null) && (FragmentContextView.this.animatorSet.equals(paramAnonymousAnimator)))
          {
            FragmentContextView.this.setVisibility(8);
            FragmentContextView.access$502(FragmentContextView.this, null);
          }
        }
      });
      this.animatorSet.start();
      continue;
      label264:
      updateStyle(2);
      this.playButton.setImageDrawable(new ShareLocationDrawable(getContext(), true));
      if ((bool) && (this.topPadding == 0.0F))
      {
        setTopPadding(AndroidUtilities.dp2(36.0F));
        setTranslationY(0.0F);
        this.yPosition = 0.0F;
      }
      if (!this.visible)
      {
        if (!bool)
        {
          if (this.animatorSet != null)
          {
            this.animatorSet.cancel();
            this.animatorSet = null;
          }
          this.animatorSet = new AnimatorSet();
          this.animatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this, "translationY", new float[] { -AndroidUtilities.dp2(36.0F), 0.0F }), ObjectAnimator.ofFloat(this, "topPadding", new float[] { AndroidUtilities.dp2(36.0F) }) });
          this.animatorSet.setDuration(200L);
          this.animatorSet.addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnonymousAnimator)
            {
              if ((FragmentContextView.this.animatorSet != null) && (FragmentContextView.this.animatorSet.equals(paramAnonymousAnimator))) {
                FragmentContextView.access$502(FragmentContextView.this, null);
              }
            }
          });
          this.animatorSet.start();
        }
        this.visible = true;
        setVisibility(0);
      }
      if ((this.fragment instanceof DialogsActivity))
      {
        String str = LocaleController.getString("AttachLiveLocation", NUM);
        localObject = new ArrayList();
        for (int i = 0; i < 3; i++) {
          ((ArrayList)localObject).addAll(LocationController.getInstance(i).sharingLocationsUI);
        }
        if (((ArrayList)localObject).size() == 1)
        {
          localObject = (LocationController.SharingLocationInfo)((ArrayList)localObject).get(0);
          i = (int)((LocationController.SharingLocationInfo)localObject).messageObject.getDialogId();
          if (i > 0) {
            localObject = UserObject.getFirstName(MessagesController.getInstance(((LocationController.SharingLocationInfo)localObject).messageObject.currentAccount).getUser(Integer.valueOf(i)));
          }
        }
        for (;;)
        {
          localObject = String.format(LocaleController.getString("AttachLiveLocationIsSharing", NUM), new Object[] { str, localObject });
          i = ((String)localObject).indexOf(str);
          localObject = new SpannableStringBuilder((CharSequence)localObject);
          this.titleTextView.setEllipsize(TextUtils.TruncateAt.END);
          ((SpannableStringBuilder)localObject).setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, Theme.getColor("inappPlayerPerformer")), i, str.length() + i, 18);
          this.titleTextView.setText((CharSequence)localObject);
          break;
          localObject = MessagesController.getInstance(((LocationController.SharingLocationInfo)localObject).messageObject.currentAccount).getChat(Integer.valueOf(-i));
          if (localObject != null)
          {
            localObject = ((TLRPC.Chat)localObject).title;
          }
          else
          {
            localObject = "";
            continue;
            localObject = LocaleController.formatPluralString("Chats", ((ArrayList)localObject).size());
          }
        }
      }
      this.checkLocationRunnable.run();
      checkLocationString();
    }
  }
  
  private void checkLocationString()
  {
    if ((!(this.fragment instanceof ChatActivity)) || (this.titleTextView == null)) {}
    label598:
    for (;;)
    {
      return;
      Object localObject1 = (ChatActivity)this.fragment;
      long l = ((ChatActivity)localObject1).getDialogId();
      int i = ((ChatActivity)localObject1).getCurrentAccount();
      Object localObject2 = (ArrayList)LocationController.getInstance(i).locationsCache.get(l);
      if (!this.firstLocationsLoaded)
      {
        LocationController.getInstance(i).loadLiveLocations(l);
        this.firstLocationsLoaded = true;
      }
      int j = 0;
      int k = 0;
      Object localObject3 = null;
      localObject1 = null;
      if (localObject2 != null)
      {
        int m = UserConfig.getInstance(i).getClientUserId();
        int n = ConnectionsManager.getInstance(i).getCurrentTime();
        int i1 = 0;
        j = k;
        localObject3 = localObject1;
        if (i1 < ((ArrayList)localObject2).size())
        {
          TLRPC.Message localMessage = (TLRPC.Message)((ArrayList)localObject2).get(i1);
          if (localMessage.media == null)
          {
            localObject3 = localObject1;
            j = k;
          }
          for (;;)
          {
            i1++;
            k = j;
            localObject1 = localObject3;
            break;
            j = k;
            localObject3 = localObject1;
            if (localMessage.date + localMessage.media.period > n)
            {
              localObject3 = localObject1;
              if (localObject1 == null)
              {
                localObject3 = localObject1;
                if (localMessage.from_id != m) {
                  localObject3 = MessagesController.getInstance(i).getUser(Integer.valueOf(localMessage.from_id));
                }
              }
              j = k + 1;
            }
          }
        }
      }
      if (this.lastLocationSharingCount != j)
      {
        this.lastLocationSharingCount = j;
        localObject2 = LocaleController.getString("AttachLiveLocation", NUM);
        if (j == 0) {
          localObject1 = localObject2;
        }
        for (;;)
        {
          if ((this.lastString != null) && (((String)localObject1).equals(this.lastString))) {
            break label598;
          }
          this.lastString = ((String)localObject1);
          k = ((String)localObject1).indexOf((String)localObject2);
          localObject1 = new SpannableStringBuilder((CharSequence)localObject1);
          this.titleTextView.setEllipsize(TextUtils.TruncateAt.END);
          if (k >= 0) {
            ((SpannableStringBuilder)localObject1).setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, Theme.getColor("inappPlayerPerformer")), k, ((String)localObject2).length() + k, 18);
          }
          this.titleTextView.setText((CharSequence)localObject1);
          break;
          k = j - 1;
          if (LocationController.getInstance(i).isSharingLocation(l))
          {
            if (k != 0)
            {
              if ((k == 1) && (localObject3 != null)) {
                localObject1 = String.format("%1$s - %2$s", new Object[] { localObject2, LocaleController.formatString("SharingYouAndOtherName", NUM, new Object[] { UserObject.getFirstName((TLRPC.User)localObject3) }) });
              } else {
                localObject1 = String.format("%1$s - %2$s %3$s", new Object[] { localObject2, LocaleController.getString("ChatYourSelfName", NUM), LocaleController.formatPluralString("AndOther", k) });
              }
            }
            else {
              localObject1 = String.format("%1$s - %2$s", new Object[] { localObject2, LocaleController.getString("ChatYourSelfName", NUM) });
            }
          }
          else if (k != 0) {
            localObject1 = String.format("%1$s - %2$s %3$s", new Object[] { localObject2, UserObject.getFirstName((TLRPC.User)localObject3), LocaleController.formatPluralString("AndOther", k) });
          } else {
            localObject1 = String.format("%1$s - %2$s", new Object[] { localObject2, UserObject.getFirstName((TLRPC.User)localObject3) });
          }
        }
      }
    }
  }
  
  private void checkPlayer(boolean paramBoolean)
  {
    MessageObject localMessageObject = MediaController.getInstance().getPlayingMessageObject();
    Object localObject = this.fragment.getFragmentView();
    boolean bool = paramBoolean;
    if (!paramBoolean)
    {
      bool = paramBoolean;
      if (localObject != null) {
        if (((View)localObject).getParent() != null)
        {
          bool = paramBoolean;
          if (((View)((View)localObject).getParent()).getVisibility() == 0) {}
        }
        else
        {
          bool = true;
        }
      }
    }
    if ((localMessageObject == null) || (localMessageObject.getId() == 0))
    {
      this.lastMessageObject = null;
      if ((VoIPService.getSharedInstance() != null) && (VoIPService.getSharedInstance().getCallState() != 15))
      {
        i = 1;
        if (i == 0) {
          break label108;
        }
        checkCall(false);
      }
      for (;;)
      {
        return;
        i = 0;
        break;
        label108:
        if (this.visible)
        {
          this.visible = false;
          if (bool)
          {
            if (getVisibility() != 8) {
              setVisibility(8);
            }
            setTopPadding(0.0F);
          }
          else
          {
            if (this.animatorSet != null)
            {
              this.animatorSet.cancel();
              this.animatorSet = null;
            }
            this.animatorSet = new AnimatorSet();
            this.animatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this, "translationY", new float[] { -AndroidUtilities.dp2(36.0F) }), ObjectAnimator.ofFloat(this, "topPadding", new float[] { 0.0F }) });
            this.animatorSet.setDuration(200L);
            this.animatorSet.addListener(new AnimatorListenerAdapter()
            {
              public void onAnimationEnd(Animator paramAnonymousAnimator)
              {
                if ((FragmentContextView.this.animatorSet != null) && (FragmentContextView.this.animatorSet.equals(paramAnonymousAnimator)))
                {
                  FragmentContextView.this.setVisibility(8);
                  FragmentContextView.access$502(FragmentContextView.this, null);
                }
              }
            });
            this.animatorSet.start();
          }
        }
      }
    }
    int i = this.currentStyle;
    updateStyle(0);
    if ((bool) && (this.topPadding == 0.0F))
    {
      setTopPadding(AndroidUtilities.dp2(36.0F));
      if ((this.additionalContextView != null) && (this.additionalContextView.getVisibility() == 0))
      {
        ((FrameLayout.LayoutParams)getLayoutParams()).topMargin = (-AndroidUtilities.dp(72.0F));
        label334:
        setTranslationY(0.0F);
        this.yPosition = 0.0F;
      }
    }
    else
    {
      if (!this.visible)
      {
        if (!bool)
        {
          if (this.animatorSet != null)
          {
            this.animatorSet.cancel();
            this.animatorSet = null;
          }
          this.animatorSet = new AnimatorSet();
          if ((this.additionalContextView == null) || (this.additionalContextView.getVisibility() != 0)) {
            break label686;
          }
          ((FrameLayout.LayoutParams)getLayoutParams()).topMargin = (-AndroidUtilities.dp(72.0F));
          label420:
          this.animatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this, "translationY", new float[] { -AndroidUtilities.dp2(36.0F), 0.0F }), ObjectAnimator.ofFloat(this, "topPadding", new float[] { AndroidUtilities.dp2(36.0F) }) });
          this.animatorSet.setDuration(200L);
          this.animatorSet.addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnonymousAnimator)
            {
              if ((FragmentContextView.this.animatorSet != null) && (FragmentContextView.this.animatorSet.equals(paramAnonymousAnimator))) {
                FragmentContextView.access$502(FragmentContextView.this, null);
              }
            }
          });
          this.animatorSet.start();
        }
        this.visible = true;
        setVisibility(0);
      }
      if (!MediaController.getInstance().isMessagePaused()) {
        break label705;
      }
      this.playButton.setImageResource(NUM);
      label542:
      if ((this.lastMessageObject == localMessageObject) && (i == 0)) {
        break label716;
      }
      this.lastMessageObject = localMessageObject;
      if ((!this.lastMessageObject.isVoice()) && (!this.lastMessageObject.isRoundVideo())) {
        break label718;
      }
      localObject = new SpannableStringBuilder(String.format("%s %s", new Object[] { localMessageObject.getMusicAuthor(), localMessageObject.getMusicTitle() }));
      this.titleTextView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
    }
    for (;;)
    {
      ((SpannableStringBuilder)localObject).setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, Theme.getColor("inappPlayerPerformer")), 0, localMessageObject.getMusicAuthor().length(), 18);
      this.titleTextView.setText((CharSequence)localObject);
      break;
      ((FrameLayout.LayoutParams)getLayoutParams()).topMargin = (-AndroidUtilities.dp(36.0F));
      break label334;
      label686:
      ((FrameLayout.LayoutParams)getLayoutParams()).topMargin = (-AndroidUtilities.dp(36.0F));
      break label420;
      label705:
      this.playButton.setImageResource(NUM);
      break label542;
      label716:
      break;
      label718:
      localObject = new SpannableStringBuilder(String.format("%s - %s", new Object[] { localMessageObject.getMusicAuthor(), localMessageObject.getMusicTitle() }));
      this.titleTextView.setEllipsize(TextUtils.TruncateAt.END);
    }
  }
  
  private void checkVisibility()
  {
    int i = 0;
    boolean bool1 = false;
    boolean bool2;
    if (this.isLocation) {
      if ((this.fragment instanceof DialogsActivity)) {
        if (LocationController.getLocationsCount() != 0)
        {
          bool2 = true;
          if (!bool2) {
            break label123;
          }
        }
      }
    }
    for (;;)
    {
      setVisibility(i);
      return;
      bool2 = false;
      break;
      bool2 = LocationController.getInstance(this.fragment.getCurrentAccount()).isSharingLocation(((ChatActivity)this.fragment).getDialogId());
      break;
      if ((VoIPService.getSharedInstance() != null) && (VoIPService.getSharedInstance().getCallState() != 15))
      {
        bool2 = true;
        break;
      }
      MessageObject localMessageObject = MediaController.getInstance().getPlayingMessageObject();
      bool2 = bool1;
      if (localMessageObject == null) {
        break;
      }
      bool2 = bool1;
      if (localMessageObject.getId() == 0) {
        break;
      }
      bool2 = true;
      break;
      label123:
      i = 8;
    }
  }
  
  private void openSharingLocation(final LocationController.SharingLocationInfo paramSharingLocationInfo)
  {
    if ((paramSharingLocationInfo == null) || (this.fragment.getParentActivity() == null)) {}
    for (;;)
    {
      return;
      LaunchActivity localLaunchActivity = (LaunchActivity)this.fragment.getParentActivity();
      localLaunchActivity.switchToAccount(paramSharingLocationInfo.messageObject.currentAccount, true);
      LocationActivity localLocationActivity = new LocationActivity(2);
      localLocationActivity.setMessageObject(paramSharingLocationInfo.messageObject);
      localLocationActivity.setDelegate(new LocationActivity.LocationActivityDelegate()
      {
        public void didSelectLocation(TLRPC.MessageMedia paramAnonymousMessageMedia, int paramAnonymousInt)
        {
          SendMessagesHelper.getInstance(paramSharingLocationInfo.messageObject.currentAccount).sendMessage(paramAnonymousMessageMedia, this.val$dialog_id, null, null, null);
        }
      });
      localLaunchActivity.presentFragment(localLocationActivity);
    }
  }
  
  private void updateStyle(int paramInt)
  {
    if (this.currentStyle == paramInt) {}
    for (;;)
    {
      return;
      this.currentStyle = paramInt;
      if ((paramInt == 0) || (paramInt == 2))
      {
        this.frameLayout.setBackgroundColor(Theme.getColor("inappPlayerBackground"));
        this.frameLayout.setTag("inappPlayerBackground");
        this.titleTextView.setTextColor(Theme.getColor("inappPlayerTitle"));
        this.titleTextView.setTag("inappPlayerTitle");
        this.closeButton.setVisibility(0);
        this.playButton.setVisibility(0);
        this.titleTextView.setTypeface(Typeface.DEFAULT);
        this.titleTextView.setTextSize(1, 15.0F);
        this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 36.0F, 51, 35.0F, 0.0F, 36.0F, 0.0F));
        if (paramInt == 0)
        {
          this.playButton.setLayoutParams(LayoutHelper.createFrame(36, 36.0F, 51, 0.0F, 0.0F, 0.0F, 0.0F));
          this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 36.0F, 51, 35.0F, 0.0F, 36.0F, 0.0F));
        }
        else if (paramInt == 2)
        {
          this.playButton.setLayoutParams(LayoutHelper.createFrame(36, 36.0F, 51, 8.0F, 0.0F, 0.0F, 0.0F));
          this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 36.0F, 51, 51.0F, 0.0F, 36.0F, 0.0F));
        }
      }
      else if (paramInt == 1)
      {
        this.titleTextView.setText(LocaleController.getString("ReturnToCall", NUM));
        this.frameLayout.setBackgroundColor(Theme.getColor("returnToCallBackground"));
        this.frameLayout.setTag("returnToCallBackground");
        this.titleTextView.setTextColor(Theme.getColor("returnToCallText"));
        this.titleTextView.setTag("returnToCallText");
        this.closeButton.setVisibility(8);
        this.playButton.setVisibility(8);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.titleTextView.setTextSize(1, 14.0F);
        this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-2, -2.0F, 17, 0.0F, 0.0F, 0.0F, 2.0F));
      }
    }
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    if (paramInt1 == NotificationCenter.liveLocationsChanged) {
      checkLiveLocation(false);
    }
    for (;;)
    {
      return;
      if (paramInt1 == NotificationCenter.liveLocationsCacheChanged)
      {
        if ((this.fragment instanceof ChatActivity))
        {
          long l = ((Long)paramVarArgs[0]).longValue();
          if (((ChatActivity)this.fragment).getDialogId() == l) {
            checkLocationString();
          }
        }
      }
      else if ((paramInt1 == NotificationCenter.messagePlayingDidStarted) || (paramInt1 == NotificationCenter.messagePlayingPlayStateChanged) || (paramInt1 == NotificationCenter.messagePlayingDidReset) || (paramInt1 == NotificationCenter.didEndedCall)) {
        checkPlayer(false);
      } else if (paramInt1 == NotificationCenter.didStartedCall) {
        checkCall(false);
      } else {
        checkPlayer(false);
      }
    }
  }
  
  protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
  {
    int i = paramCanvas.save();
    if (this.yPosition < 0.0F) {
      paramCanvas.clipRect(0, (int)-this.yPosition, paramView.getMeasuredWidth(), AndroidUtilities.dp2(39.0F));
    }
    boolean bool = super.drawChild(paramCanvas, paramView, paramLong);
    paramCanvas.restoreToCount(i);
    return bool;
  }
  
  public float getTopPadding()
  {
    return this.topPadding;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if (this.isLocation)
    {
      NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.liveLocationsChanged);
      NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.liveLocationsCacheChanged);
      if (this.additionalContextView != null) {
        this.additionalContextView.checkVisibility();
      }
      checkLiveLocation(true);
    }
    for (;;)
    {
      return;
      for (int i = 0; i < 3; i++)
      {
        NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.messagePlayingDidStarted);
      }
      NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didStartedCall);
      NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didEndedCall);
      if (this.additionalContextView != null) {
        this.additionalContextView.checkVisibility();
      }
      if ((VoIPService.getSharedInstance() != null) && (VoIPService.getSharedInstance().getCallState() != 15)) {
        checkCall(true);
      } else {
        checkPlayer(true);
      }
    }
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    this.topPadding = 0.0F;
    if (this.isLocation)
    {
      NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.liveLocationsChanged);
      NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.liveLocationsCacheChanged);
    }
    for (;;)
    {
      return;
      for (int i = 0; i < 3; i++)
      {
        NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.messagePlayingDidStarted);
      }
      NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didStartedCall);
      NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didEndedCall);
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, AndroidUtilities.dp2(39.0F));
  }
  
  public void setAdditionalContextView(FragmentContextView paramFragmentContextView)
  {
    this.additionalContextView = paramFragmentContextView;
  }
  
  @Keep
  public void setTopPadding(float paramFloat)
  {
    this.topPadding = paramFloat;
    if (this.fragment != null)
    {
      View localView = this.fragment.getFragmentView();
      int i = 0;
      int j = i;
      if (this.additionalContextView != null)
      {
        j = i;
        if (this.additionalContextView.getVisibility() == 0) {
          j = AndroidUtilities.dp(36.0F);
        }
      }
      if (localView != null) {
        localView.setPadding(0, (int)this.topPadding + j, 0, 0);
      }
      if ((this.isLocation) && (this.additionalContextView != null)) {
        ((FrameLayout.LayoutParams)this.additionalContextView.getLayoutParams()).topMargin = (-AndroidUtilities.dp(36.0F) - (int)this.topPadding);
      }
    }
  }
  
  @Keep
  public void setTranslationY(float paramFloat)
  {
    super.setTranslationY(paramFloat);
    this.yPosition = paramFloat;
    invalidate();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/FragmentContextView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */