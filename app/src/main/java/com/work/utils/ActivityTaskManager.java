package com.work.utils;
 
import android.app.Activity;
 
import java.lang.ref.WeakReference;
 

public class ActivityTaskManager {
    
    private static ActivityTaskManager sInstance = new ActivityTaskManager();
    
    private WeakReference<Activity> sCurrentActivityWeakRef;

    private ActivityTaskManager() {
    }
 
    public static ActivityTaskManager getInstance() {
        return sInstance;
    }
 
    public Activity getCurrentActivity() {
        if (sCurrentActivityWeakRef != null) {
            return  sCurrentActivityWeakRef.get();
        }
        return null;
    }
 
    public void setCurrentActivity(Activity activity) {
        sCurrentActivityWeakRef = new WeakReference<Activity>(activity);
    }
}
