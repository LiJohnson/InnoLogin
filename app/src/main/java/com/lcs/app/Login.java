package com.lcs.app;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 14-2-17.
 */
public class Login  {
    private  String host;
    public Login( String host ){
        this.host = host;
    }

    public String f( String name , String pass ){
        HttpClient c = new DefaultHttpClient();
        HttpGet get = new HttpGet(this.host+"/exeLogin.action?login.loginName="+name+"&login.loginPwd="+pass);
        try {
           HttpResponse r =  c.execute(get);
            String html = EntityUtils.toString(r.getEntity());

            if( html == null )return "no";

            Pattern p = Pattern.compile("当前登录用户：.+用户角色");
            Matcher m = p.matcher(html);
            return m.find() ? m.group().replace("用户角色", "").replace("当前登录用户：", "") : "login failure";
        } catch (IOException e) {
            e.printStackTrace();
            return "IOException"+e.getMessage();
        }
    }
}
