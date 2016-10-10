package net.hockeyapp.android.objects;

public enum FeedbackUserDataElement
{
  DONT_SHOW(0),  OPTIONAL(1),  REQUIRED(2);
  
  private final int mValue;
  
  private FeedbackUserDataElement(int paramInt)
  {
    this.mValue = paramInt;
  }
  
  public int getValue()
  {
    return this.mValue;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/objects/FeedbackUserDataElement.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */