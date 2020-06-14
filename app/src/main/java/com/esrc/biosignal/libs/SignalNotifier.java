package com.esrc.biosignal.libs;

/**
 * Created by lhw48 on 2016-06-22.
 */
public interface SignalNotifier {
    public void onReceivedPPG(int ppg);
    public void onReceivedBPM(double bpm);
}
