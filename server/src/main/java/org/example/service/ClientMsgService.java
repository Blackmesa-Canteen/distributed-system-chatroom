package org.example.service;

import com.google.gson.Gson;
import org.example.msgBean.IdentityChangeMessage;
import org.example.msgBean.NewIdentityMessage;

/**
 * @author Xiaotian
 * @program assignment1
 * @description
 * @create 2021-08-19 16:25
 */
public class ClientMsgService {

    public static String genNewIdentityMsg(String former, String identity) {
        NewIdentityMessage jsonObject = new NewIdentityMessage();
        jsonObject.setFormer(former);
        jsonObject.setIdentity(identity);

        return new Gson().toJson(jsonObject) + "\n";
    }

    public static String genIdentityChangeMsg(String identity) {
        IdentityChangeMessage jsonObject = new IdentityChangeMessage();
        jsonObject.setIdentity(identity);

        return new Gson().toJson(jsonObject) + "\n";
    }


}