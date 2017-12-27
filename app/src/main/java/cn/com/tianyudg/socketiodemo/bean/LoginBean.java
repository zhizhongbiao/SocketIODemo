package cn.com.tianyudg.socketiodemo.bean;

import java.io.Serializable;

/**
 * Author : WaterFlower.
 * Created on 2017/12/26.
 * Desc :
 */

public class LoginBean implements Serializable{

    /**
     * status : 1
     * data : {"user_id":"26834","compangy_id":"1321","compangy_type":1,"site_name":"东莞","site_code":"441900","site_domain":"http://dongguan.huifang.cn"}
     * msg : 登录成功
     * page : 1
     * login_status : 1
     * count : 0
     * page_count : 1
     */

    public int status;
    public DataBean data;
    public String msg;
    public int page;
    public int login_status;
    public int count;
    public int page_count;

    public static class DataBean implements Serializable{
        /**
         * user_id : 26834
         * compangy_id : 1321
         * compangy_type : 1
         * site_name : 东莞
         * site_code : 441900
         * site_domain : http://dongguan.huifang.cn
         */

        public String user_id;
        public String compangy_id;
        public int compangy_type;
        public String site_name;
        public String site_code;
        public String site_domain;
    }
}
