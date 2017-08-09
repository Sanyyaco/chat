package ru.geekbrains.chat.library;

/**
 * Created by Administrator on 07.08.2017.
 */
public class Messages {

    public static final String DELIMITER = ";";
    public static final String AUTH_REQUEST = "/auth_request";
    public static final String AUTH_ACCEPT = "/auth_accept";
    public static final String AUTH_ERROR = "/auth_error";
    public static final String USERS_LIST = "/user_list";
    public static final String RECONNECT = "/reconnect";
    public static final String BROADCAST = "/bcast";
    public static final String MSG_FORMAT_ERROR = "/msg_format_error";

    public static String getAuthRequest(String login, String password){
        return AUTH_REQUEST + DELIMITER + login + DELIMITER + password;
    }

    public static String getAuthAccept(String nick){
        return AUTH_ACCEPT + DELIMITER + nick;
    }

    public static String getBroadcast(String src, String value){
        return BROADCAST + DELIMITER + System.currentTimeMillis() + DELIMITER + src +DELIMITER + value;
    }

    public static String getUsersList (String users){
        return USERS_LIST + DELIMITER + users;
    }

    public static String getAuthError(){
        return AUTH_ERROR;
    }

    public static String getReconnect(){
        return RECONNECT;
    }

    public static String getMsgFormatError(String value){
        return MSG_FORMAT_ERROR + DELIMITER + value;
    }

}
