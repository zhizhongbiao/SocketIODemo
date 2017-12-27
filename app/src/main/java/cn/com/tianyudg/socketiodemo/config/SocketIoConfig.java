package cn.com.tianyudg.socketiodemo.config;

/**
 * Created by LYX on 2017/12/11.
 *
 * @author: LYX
 * @data: 2017/12/11
 * @description: {socketio 默认配置}
 */

public class SocketIoConfig {
    /**
     * socket要用到的appName
     */
    public static final String appName = "sumaitong";
    /**
     * socket服务器的地址
     */
    public static final String SERVER_URL = "http://120.78.229.100:2120";
    /**
     * Socket Log
     */
    public static final String SOCKET_LOG = "socket";

    public interface ENCRYPT_STRINRG {

        String IVPARAMETER = "rN0ErUNOnnstC9CY";

        String SKEY = "1ZFum6GybjqHLMWP";

    }

    public interface EVENT {

        String MSG_FROM_SERVER = "msg_from_server";

        String NEW_MSG_FROM_CLIENT = "new_msg_from_client";

        String LOGIN = "login";

        String LOGIN_SUCCEED="login_succeed";

        String RECONNECT = "reconnection";
    }
}
