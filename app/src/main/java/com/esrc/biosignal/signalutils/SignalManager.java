package com.esrc.biosignal.signalutils;


import java.util.ArrayList;


public class SignalManager {
    public static final int PPG_WINDOW_SIZE = 10;  // seconds
    public static final int PPG_INTERVAL_SIZE = 1;  // seconds

    private ArrayList<Double> ppgList;
    private ArrayList<Integer> intervalList;

    private long prevTime;
    private int interval;

    public SignalManager() {
        ppgList = new ArrayList<Double>();
        intervalList = new ArrayList<Integer>();
        prevTime = System.currentTimeMillis();
        interval = 0;
    }

    public double add(int ppg) {
        ppgList.add((double)ppg);
        interval++;
        double bpm = 0;

        // Interval
        long currTime = System.currentTimeMillis();
        if((currTime-prevTime) / 1000.0 >= PPG_INTERVAL_SIZE) {
            prevTime = currTime;
            intervalList.add(interval);
            interval = 0;

            // Windowing
            if(intervalList.size() >= PPG_WINDOW_SIZE) {
                // Convert to ArrayList to Array
                double[] ppgArray = new double[ppgList.size()];
                for(int i=0; i<ppgList.size(); i++) {
                    ppgArray[i] = ppgList.get(i);
                }

                // Calculate BPM
                bpm = estimateBPMbyMaximaPeakDetection(ppgArray, ppgArray.length / PPG_WINDOW_SIZE, 0.8, 0.4, 1.33);

                // Sliding for ppi
                int firstInterval = intervalList.get(0);
                intervalList.remove(0);
                for(int i=0; i<firstInterval; i++) {
                    ppgList.remove(0);
                }
            }
        }

        return bpm;
    }

    private double[] applyMovingMax(double[] signal, int window_size)
    {
        double[] trend = new double[signal.length];
        int half_window_size = window_size / 2;

        // First index
        trend[0] = signal[0];
        // Fist half window size
        for (int i = 1; i < half_window_size; i++) {
            double max = 0;
            for (int j = 0; j < i; j++) {
                if (signal[j] > max) max = signal[j];
            }
            trend[i] = max;
        }
        // Window size
        for (int i = half_window_size; i < signal.length - half_window_size; i++) {
            double max = 0;
            for (int j = i - half_window_size; j < i + half_window_size; j++) {
                if (signal[j] > max) max = signal[j];
            }
            trend[i] = max;
        }
        // Last half window size
        for (int i = signal.length - half_window_size; i < window_size; i++) {
            double max = 0;
            for (int j = i; j < window_size; j++) {
                if (signal[j] > max) max = signal[j];
            }
            trend[i] = max;
        }
        // Last index
        trend[signal.length - 1] = signal[signal.length - 1];

        return trend;
    }

    private int[] findPeaksThreshold(double[] signal, double threshold)
    {
        ArrayList<Integer> detected_peaks_indices_v = new ArrayList<Integer>();
        for (int i = 0; i < signal.length; i++) {
            if (signal[i] >= threshold) {
                detected_peaks_indices_v.add(i);
            }
        }

        // Convert vector to array
        int[] detected_peaks_indices = new int[detected_peaks_indices_v.size()];
        for(int i=0; i<detected_peaks_indices_v.size(); i++) {
            detected_peaks_indices[i] = detected_peaks_indices_v.get(i);
        }

        return detected_peaks_indices;
    }

    private int[] detectPeaks(double[] signal, double fs, double detrend_factor)
    {
        // Estimate trend
        double[] trend = applyMovingMax(signal, (int)(fs * detrend_factor));

        // Detrend
        double[] detrend_signal = new double[signal.length];
        for(int i=0; i<signal.length; i++) {
            detrend_signal[i] = signal[i] - trend[i];
        }

        // Find peaks
        int[] detected_peaks_indices = findPeaksThreshold(detrend_signal, 0);

        return detected_peaks_indices;
    }

    private double calculatePPIbyMaximaPeakDetection(double[] signal, double fs, double detrend_factor, double lowcut, double highcut)
    {
        // Detect peaks
        int[] peak_indices = detectPeaks(signal, fs, detrend_factor);

        // CalculatePPI
        double avg_ppi = 0;
        ArrayList<Double> ppi_v = new ArrayList<Double>();
        for(int i=0; i<peak_indices.length-1; i++) {
            double ppi = (peak_indices[i + 1] - peak_indices[i]) / fs;
            if ((lowcut <= ppi) & (ppi <= highcut)) {
                ppi_v.add(ppi);
            }
        }
        if(ppi_v.size() > 0) {
            for (int i = 0; i < ppi_v.size(); i++) {
                avg_ppi += ppi_v.get(i);
            }
            avg_ppi /= ppi_v.size();
        }

        return avg_ppi;
    }

    double estimateBPMbyMaximaPeakDetection(double[] signal, double fs, double detrend_factor, double lowcut, double highcut)
    {
        // Calculate PPI
        double ppi = calculatePPIbyMaximaPeakDetection(signal, fs, detrend_factor, lowcut, highcut);

        // Calculate BPM
        double bpm = 0;
        if(ppi > 0) bpm = 60. / ppi;

        return bpm;
    }
}
