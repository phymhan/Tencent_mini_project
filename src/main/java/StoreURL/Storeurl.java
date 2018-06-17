package StoreURL;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import java.lang.*;
import java.sql.*;

import java.util.concurrent.TimeUnit;
import org.json.JSONArray;


import org.apache.http.HttpConnection;


import com.qcloud.Module.Cvm;
import com.qcloud.Utilities.Json.JSONObject;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Storeurl extends HttpServlet {

    /*
    * MySQL 连接，利用com.mysql.jdbc.Driver的库进行MySQL的通信，进行查询等操作
    * 这里输入MySQL的服务器的IP地址以及登陆用户名及密码
    *
    * */
    private static java.sql.Connection conn;
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_NAME = "tencentmini";
    private static final String Host_Name = "localhost:3306";
    private static final String URL = "jdbc:mysql://" + Host_Name + "/" + DB_NAME; //+ "?useUnicode=true&characterEncoding=UTF-8";
    private static final String DB_USER = "root";
    private static final String DB_PWD = "123456";

    public Storeurl() throws ClassNotFoundException, SQLException, IOException {
        // MySQL setting
        System.out.println("Initialization 1!!!");
        Class.forName(JDBC_DRIVER);
        System.out.println("Initialization 2!!!");
        conn = DriverManager.getConnection(URL, DB_USER, DB_PWD);
        System.out.println("Connect to server!!!");
    }

    /*
    * doGet 函数
    * 获取请求并调用entrance_mysql函数来执行数据库查询指令
    * */
    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response) throws ServletException, IOException {
        org.json.JSONObject result = new org.json.JSONObject();
        String id = "";
        if(request.getParameter("userid") != null){
            id = request.getParameter("userid");
        }
        if (id == ""){
            System.out.println("empty id");
            result.put("status", "False");
            result.put("receivedurl", "");
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter writer = response.getWriter();
            writer.write(String.format("%s", result.toString()));   // 写入response返回给前端
            writer.println();
            writer.close();
        }
        String origin_image_url = "";
        if( request.getParameter("imageurl")!=null){
            origin_image_url = request.getParameter("imageurl");
        }

        String audio_url = "";
        if( request.getParameter("audiourl")!=null){
            audio_url  = request.getParameter("audiourl");
        }
        System.out.println(".......");
        String url = "http://18.188.68.124?imageurl="+origin_image_url+"&audiourl="+audio_url;
        System.out.println(url);
        System.out.println(".......");
        URL obj = new URL(url);
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        String present_time = sdf.format(date);
        long time = Long.parseLong(present_time);

        // 发送image和audio的URL给ai服务器，获取生成图片的URL
        System.out.println("send to AI server: "+origin_image_url+" and "+audio_url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        int responseCode = con.getResponseCode();
        System.out.println("response code: "+responseCode);
        String received_url = "";
        System.out.println(responseCode);

        if(responseCode == 200){
            //add request header
            BufferedReader instream = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer buffer = new StringBuffer();
            while ((inputLine = instream.readLine()) != null) {
                buffer.append(inputLine);
            }
            instream.close();
            con.disconnect();
            System.out.println(buffer.toString());
            org.json.JSONObject jsonstring = new org.json.JSONObject(buffer.toString());
            //print result

            if (jsonstring != null){
                if(jsonstring.getString("processedurl") != null){
                    received_url = jsonstring.getString("processedurl");
                }
                else{
                    System.out.println("no processedurl");
                }
            }
            else{
                System.out.println("return null result");
            }
        }
        else{
            System.out.println("connect to ai error!");
        }


        // String received_url="http://54.90.133.64/images/giphy.gif";
        System.out.println(received_url);
        int good = 0;

        String responsestatus = "False";
        if(received_url != "") {
            try {
                responsestatus = store_to_mysql(id, time, good, received_url);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        result.put("status", responsestatus);
        result.put("receivedurl", received_url);
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write(String.format("%s", result.toString()));   // 写入response返回给前端
        writer.println();
        writer.close();

    }

    @Override
    protected void doPost(final HttpServletRequest request,
                          final HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    /*
    * entrance_mysql函数，通过连接数据库，并通过用户名id和密码查询用户信息
    * 如果登陆成功就返回用户True，否则返回False
    *
     */

    private String store_to_mysql(String id, long time, int good, String url) throws SQLException {
        Statement stmt = null;
        String sql = "INSERT INTO ImageUrl (Id,Time, Good, ImageURL) VALUES ('"+
                id+"',"+Long.toString(time)+","+Integer.toString(good)+",'"+url+"')";
        String responsestatus="True";
        System.out.println(sql);

        try {
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);

        } catch (SQLException e) {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return responsestatus;
    }


}