package com.lcs.app;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lcs on 14-2-17.
 */
public class Inno {
	private  String host;
	public Inno(String host){
		this.host = host;
	}

    private String get( String... urls  ){
        HttpClient c = new DefaultHttpClient();
        HttpResponse r = null;
        try {
            for( String url : urls  ){
                r =  c.execute(new HttpGet(url));
            }
            return EntityUtils.toString(r.getEntity());
        } catch (IOException e) {
            return "IOException"+e.getMessage();
        }
    }

    public String login(String name , String pass){
        String html = this.get(this.host+"/exeLogin.action?login.loginName="+name+"&login.loginPwd="+pass);
        if( html == null )return "no";
        if( html.indexOf("IOException") == 0 )return html;
        String loginName = null , loginTime = null;

        Pattern p = Pattern.compile("当前登录用户：.+用户角色");
        Matcher m = p.matcher(html);

        if( m.find() ){
            loginName = m.group().replace("用户角色", "").replace("当前登录用户：", "");

            int index = html.indexOf("此次登录时间:");
            if( index > 0){
                loginTime = html.substring(index, html.indexOf("<", index));
                if( loginTime != null ){
                    return loginTime.replace("此次登录时间:","") + " : " + loginName;
                }
            }
            return  loginName;
        }

       return "login failure";
    }

    public String logout(String name , String pass){
        String html = this.get(this.host+"/exeLogin.action?login.loginName="+name+"&login.loginPwd="+pass,this.host+"/admin/offDuty.action");
        //String html = this.get(this.host+"/exeLogin.action?login.loginName="+name+"&login.loginPwd="+pass,this.host+"/admin/searchWorkDemo.action?empId=f1e967e730976b7501310e3299b10c8a");

        if( html == null )return "no";
        if( html.indexOf("IOException") == 0 )return html;
       // if( html != null )return  html;

        Pattern p = Pattern.compile("你的下班时间为：.+。");
        Matcher m = p.matcher(html);
        if( m.find() ){
            return m.group();
        }else if( html.indexOf("你已经签过退了") != -1 ){
            return "你已经签过退了";
        }
        return  "login failure";
    }
}