package Register;

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


public class Register extends HttpServlet {

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

    public Register() throws ClassNotFoundException, SQLException, IOException {
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
        System.out.println(request.getQueryString());
        String id="";
        if(request.getParameter("userid")!=null){
            id =request.getParameter("userid");
        }
//        System.out.println(id);
        String password ="123456";
        if( request.getParameter("password")!=null){
            password  = request.getParameter("password");
        }
//        System.out.println(password);
        String nickname = "Aha";
        if( request.getParameter("nickname")!=null){
            nickname  = request.getParameter("nickname");
        }
//        System.out.println(nickname);
        long qq = 0;
        if( request.getParameter("qq")!=null){
            String med=request.getParameter("qq");
//            System.out.println(med);
//            System.out.println(med.matches("^[1-9]+$"));
            if(med.matches("^[1-9]+$")) {
                qq  = Long.parseLong(med);
            }
        }
//        System.out.println(qq);
        org.json.JSONObject result = new org.json.JSONObject();
        String responsestatus= "False";
        String profile_img="http://img2.3png.com/15a203b532e46e3440d0e89926363441d8e7.png";
        if(id!="") {
            try {
                System.out.println("start");
                responsestatus = store_to_mysql(id,password,nickname,qq, profile_img);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        result.put("status", responsestatus);
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

    private String store_to_mysql(String id, String password, String name, long qq, String profile_image) throws SQLException {
        String sql = "SELECT * FROM UserPassword WHERE Id='" + id + "'";
        Boolean exist = false;  // 初始化为不存在
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                exist = true;
                System.out.println("exist id!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String responsestatus="Exist";
        if(!exist){

            String sql2 = "INSERT INTO UserPassword (Id,Password,QQnumber, Name,Profile_URL ) VALUES ('"+
                    id+"','"+password+"',"+Long.toString(qq)+",'"+name+"','"+profile_image+"')";

            System.out.println(sql2);

            try {
                System.out.println("3");
                stmt.executeUpdate(sql2);
                System.out.println("4");
                responsestatus="OK";

            } catch (SQLException e) {
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }finally {
                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

        return responsestatus;
    }


}