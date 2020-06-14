package com.esrc.biosignal.libs;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

/**
 * Created by lhw48 on 2016-06-22.
 */
public interface BiosignalConsumer {
    /**
     * Called when the esrcBracelet service is running and ready to accept your commands through the SociaLBandManager
     */
    public void onBiosignalServiceConnect();
    /**
     * Called by the SociaLBandManager to get the context of your Service or Activity.  This method is implemented by Service or Activity.
     * You generally should not override it.
     * @return the application context of your service or activity
     */
    public Context getApplicationContext();
    /**
     * Called by the SociaLBandManager to bind your SociaLBandConsumer to the  SociaLBandService.  This method is implemented by Service or Activity, and
     * You generally should not override it.
     * @return the application context of your service or activity
     */
    public void unbindService(ServiceConnection connection);
    /**
     * Called by the SociaLBandManager to unbind your SociaLBandConsumer to the SociaLBandService.  This method is implemented by Service or Activity, and
     * You generally should not override it.
     * @return the application context of your service or activity
     */
    public boolean bindService(Intent intent, ServiceConnection connection, int mode);
}
