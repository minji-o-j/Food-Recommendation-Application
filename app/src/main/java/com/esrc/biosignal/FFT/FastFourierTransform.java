package com.esrc.biosignal.FFT;
import static java.lang.Math.*;

public class FastFourierTransform {


    public static int bitReverse(int n, int bits) {
        int reversedN = n;
        int count = bits - 1;

        n >>= 1;
        while (n > 0) {
            reversedN = (reversedN << 1) | (n & 1);
            count--;
            n >>= 1;
        }

        return ((reversedN << count) & ((1 << bits) - 1));
    }

    public void fft(Complex[] buffer) {

        int bits = (int) (log(buffer.length) / log(2));
        for (int j = 1; j < buffer.length / 2; j++) {

            int swapPos = bitReverse(j, bits);
            Complex temp = buffer[j];
            buffer[j] = buffer[swapPos];
            buffer[swapPos] = temp;
        }

        for (int N = 2; N <= buffer.length; N <<= 1) {
            for (int i = 0; i < buffer.length; i += N) {
                for (int k = 0; k < N / 2; k++) {

                    int evenIndex = i + k;
                    int oddIndex = i + k + (N / 2);
                    Complex even = buffer[evenIndex];
                    Complex odd = buffer[oddIndex];

                    double term = (-2 * PI * k) / (double) N;
                    Complex exp = (new Complex(cos(term), sin(term)).mult(odd));

                    buffer[evenIndex] = even.add(exp);
                    buffer[oddIndex] = even.sub(exp);
                }
            }
        }
    }



    /*
    //public static void main(String[] args) {
    public static double[] Cal(double[] input){
        //double[] input = {0.7611111111111112, 0.7092307692307691, 0.7174409748667174, 0.7242955064737243, 0.7376237623762375, 0.7541254125412541, 0.7549504950495048, 0.7797029702970297, 0.8028052805280528, 0.8568856885688569, 0.876114081996435, 0.8725490196078434, 0.9196078431372551, 0.9116504854368932, 0.9106796116504855, 0.9106796116504854, 0.9176470588235296, 0.8667255075022064, 0.877005347593583, 0.8734402852049911, 0.8520499108734405, 0.8424842484248424, 0.8316831683168315, 0.8253825382538253, 0.7850000000000001, 0.7871287128712869, 0.7761437908496731, 0.7663398692810457, 0.733031674208145, 0.7269984917043739, 0.7239819004524889, 0.7363704256908141};

        Complex[] cinput = new Complex[input.length]; //make complex arr
        for (int i = 0; i < input.length; i++)
            cinput[i] = new Complex(input[i], 0.0);//input re: input[i], im: 0,0,0...-->real!

        fft(cinput);

        System.out.println("Results:");
        for (Complex c : cinput) {
            System.out.println(c);
        }

        double[] power_spectrum=new double[input.length];

        for(int i=0;i<input.length;i++){
            power_spectrum[i]=((cinput[i].re*cinput[i].re)+(cinput[i].im*cinput[i].im))/(input.length*input.length)*2;
            System.out.printf("%.9f\n",power_spectrum[i]);
        }

        return power_spectrum;
    }

    */

}
