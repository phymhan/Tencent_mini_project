package User;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.lang.*;
import java.sql.*;

import java.util.concurrent.TimeUnit;


import org.json.JSONObject;

public class GetUser extends HttpServlet {
    /*
* MySQL 连接，利用com.mysql.jdbc.Driver的库进行MySQL的通信，进行查询等操作
* 这里输入MySQL的服务器的IP地址以及登陆用户名及密码
*
* */
    private static java.sql.Connection conn;
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_NAME = "tencentmini";
    private static final String Host_Name = "rds-mysql-miniproj.clsyxr9gudld.us-west-2.rds.amazonaws.com:3306";
    private static final String URL = "jdbc:mysql://" + Host_Name + "/" + DB_NAME; //+ "?useUnicode=true&characterEncoding=UTF-8";
    private static final String DB_USER = "root";
    private static final String DB_PWD = "12345678";

    public GetUser() throws ClassNotFoundException, SQLException, IOException {
        // MySQL setting
        System.out.println("Get User Initialization1 !!!");
        Class.forName(JDBC_DRIVER);
        System.out.println("Get User Initialization2 !!!");
        conn = DriverManager.getConnection(URL, DB_USER, DB_PWD);
        System.out.println("Connect to server!!!");
    }

    /*
    * doGet 函数
    * 获取请求并调用get_user_info函数来执行数据库查询指令
    * */
    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response) throws ServletException, IOException {
        String id="";
        String name="";
        if(request.getParameter("userid")!=null){
            id = request.getParameter("userid");
        }
        if(request.getParameter("name")!=null){
            name = request.getParameter("name");
        }

        System.out.println("get");
        System.out.println(request.getQueryString());

        String ret_mysql = "";
        if (id != "") {
            try {
                ret_mysql=get_user_info(id,name);
            } catch (SQLException e) {
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        }

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write(String.format("%s", ret_mysql));   // 写入response返回给前端
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

    private String get_user_info(String user_id, String name) throws SQLException {
        JSONObject result = new JSONObject();
        //从UserPassword表里提取user信息,可以根据用户的ID或者名字name
        String sql = "SELECT * FROM UserPassword WHERE Id='" +
                user_id + "' Or Name='" + name + "'";
        Boolean responsestatus = false;  // 初始化为失败
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                responsestatus = true;
                result.put("name", rs.getString("Name"));
                result.put("id", rs.getString("Id"));
                result.put("profile_url", rs.getString("Profile_URL"));
            }
            else {
                System.out.println("Failed to get this people!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return result.toString();
    }
}
