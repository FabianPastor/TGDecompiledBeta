package net.hockeyapp.android.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import net.hockeyapp.android.objects.FeedbackMessage;
import net.hockeyapp.android.views.FeedbackMessageView;

public class MessagesAdapter
  extends BaseAdapter
{
  private Context mContext;
  private ArrayList<FeedbackMessage> mMessagesList;
  
  public MessagesAdapter(Context paramContext, ArrayList<FeedbackMessage> paramArrayList)
  {
    this.mContext = paramContext;
    this.mMessagesList = paramArrayList;
  }
  
  public void add(FeedbackMessage paramFeedbackMessage)
  {
    if ((paramFeedbackMessage != null) && (this.mMessagesList != null)) {
      this.mMessagesList.add(paramFeedbackMessage);
    }
  }
  
  public void clear()
  {
    if (this.mMessagesList != null) {
      this.mMessagesList.clear();
    }
  }
  
  public int getCount()
  {
    return this.mMessagesList.size();
  }
  
  public Object getItem(int paramInt)
  {
    return this.mMessagesList.get(paramInt);
  }
  
  public long getItemId(int paramInt)
  {
    return paramInt;
  }
  
  public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    paramViewGroup = (FeedbackMessage)this.mMessagesList.get(paramInt);
    if (paramView == null) {}
    for (paramView = new FeedbackMessageView(this.mContext, null);; paramView = (FeedbackMessageView)paramView)
    {
      if (paramViewGroup != null) {
        paramView.setFeedbackMessage(paramViewGroup);
      }
      paramView.setIndex(paramInt);
      return paramView;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/adapters/MessagesAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */