package common;

import java.math.BigDecimal;
import java.math.MathContext;

public class BigDecimalUtils {

    public static String toMax(String num, int scal) {
        BigDecimal res = new BigDecimal(num);
        return res.divide(new BigDecimal(10).pow(scal)).setScale(8, BigDecimal.ROUND_DOWN).toPlainString();
    }

    public static String toMin(String num, int scal) {
        BigDecimal res = new BigDecimal(num);
        return res.multiply(new BigDecimal(10).pow(scal)).setScale(0).toPlainString();
    }

    public static String calculateBalance(String num1,int num2){
        BigDecimal amount = new BigDecimal(num1);
        return amount.multiply(new BigDecimal(10).pow(num2, MathContext.DECIMAL128)).setScale(8, BigDecimal.ROUND_DOWN).toPlainString();
    }


    public static String add(String num1, String num2) {
        BigDecimal a = new BigDecimal(num1);
        BigDecimal b = new BigDecimal(num2);
        return a.add(b).toPlainString();
    }

    public static String calculateGas(String num, String times) {
        BigDecimal a = new BigDecimal(num);
        return a.multiply(new BigDecimal(times)).setScale(0,BigDecimal.ROUND_DOWN).toPlainString();
    }

    public static String calculateBTCGas(String num1, String num2) {
        BigDecimal a = new BigDecimal(num1);
        BigDecimal b = new BigDecimal(num2);

        return a.multiply(b).divide(new BigDecimal(10).pow(9)).setScale(0,BigDecimal.ROUND_DOWN).toPlainString();
    }

    public static String calculateETHGas(String num1, String num2,int scal) {
        BigDecimal a = new BigDecimal(num1);
        BigDecimal b = new BigDecimal(num2);
        return a.multiply(b).divide(new BigDecimal(10).pow(scal)).setScale(8, BigDecimal.ROUND_DOWN).toPlainString();
    }

    /**
     * num1 < num2 => -1
     * num1 = num2 => 0
     * num1 > num2 => 1
     *
     * @param num1
     * @param num2
     * @return
     */
    public static int compareTo(String num1, String num2) {
        BigDecimal a = new BigDecimal(num1);
        BigDecimal b = new BigDecimal(num2);
        return a.compareTo(b);
    }
}
