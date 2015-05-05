/*  1:   */ package mindhelix.com.myapplication;
/*  2:   */ 
/*  3:   */ public abstract interface SmartConfigListener
/*  4:   */ {
/*  5:   */   public abstract void onSmartConfigEvent(SmtCfgEvent paramSmtCfgEvent, Exception paramException);
/*  6:   */   
/*  7:   */   public static enum SmtCfgEvent
/*  8:   */   {
/*  9: 6 */     FTC_SUCCESS,  FTC_ERROR,  FTC_TIMEOUT;
/* 10:   */   }
/* 11:   */ }



/* Location:           C:\Users\Jain\Desktop\smartconfiglib.jar(1)\smartconfiglib.jar

 * Qualified Name:     com.integrity_project.smartconfiglib.SmartConfigListener

 * JD-Core Version:    0.7.0.1

 */