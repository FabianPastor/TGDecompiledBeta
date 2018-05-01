package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

public class MusicPlayerReceiver
  extends BroadcastReceiver
{
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    if (paramIntent.getAction().equals("android.intent.action.MEDIA_BUTTON")) {
      if (paramIntent.getExtras() != null) {}
    }
    for (;;)
    {
      return;
      paramContext = (KeyEvent)paramIntent.getExtras().get("android.intent.extra.KEY_EVENT");
      if ((paramContext != null) && (paramContext.getAction() == 0)) {
        switch (paramContext.getKeyCode())
        {
        case 86: 
        default: 
          break;
        case 79: 
        case 85: 
          if (MediaController.getInstance().isMessagePaused()) {
            MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
          } else {
            MediaController.getInstance().pauseMessage(MediaController.getInstance().getPlayingMessageObject());
          }
          break;
        case 126: 
          MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
          break;
        case 127: 
          MediaController.getInstance().pauseMessage(MediaController.getInstance().getPlayingMessageObject());
          break;
        case 87: 
          MediaController.getInstance().playNextMessage();
          break;
        case 88: 
          MediaController.getInstance().playPreviousMessage();
          continue;
          if (paramIntent.getAction().equals("org.telegram.android.musicplayer.play")) {
            MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
          } else if ((paramIntent.getAction().equals("org.telegram.android.musicplayer.pause")) || (paramIntent.getAction().equals("android.media.AUDIO_BECOMING_NOISY"))) {
            MediaController.getInstance().pauseMessage(MediaController.getInstance().getPlayingMessageObject());
          } else if (paramIntent.getAction().equals("org.telegram.android.musicplayer.next")) {
            MediaController.getInstance().playNextMessage();
          } else if (paramIntent.getAction().equals("org.telegram.android.musicplayer.close")) {
            MediaController.getInstance().cleanupPlayer(true, true);
          } else if (paramIntent.getAction().equals("org.telegram.android.musicplayer.previous")) {
            MediaController.getInstance().playPreviousMessage();
          }
          break;
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/MusicPlayerReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */