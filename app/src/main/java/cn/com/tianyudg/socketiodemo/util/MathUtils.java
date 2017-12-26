package cn.com.tianyudg.socketiodemo.util;

import java.util.Random;

/**
 * Created by Administrator on 2017/12/15.
 */

public class MathUtils {

    /**
     * @param digitNum
     * @return
     *
     * 生成 digitNum 位随机数，不保证重复，可能重复,每位0~9的数字
     */
    public static String generateRandomStr(int digitNum) {
        Random radom = new Random();
        String result = "";
        if (digitNum > 0) {
            for (int i = 0; i < digitNum; i++) {
                result += radom.nextInt(10);
            }
        }

        return result;
    }
}
