package GetUrls;

import org.json.*;

import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import java.lang.*;
import java.sql.*;

import java.util.concurrent.TimeUnit;


public class GetUrl extends HttpServlet {

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

    public GetUrl() throws ClassNotFoundException, SQLException, IOException {
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
            id = request.getParameter("userid");
        }

        org.json.JSONObject result = new org.json.JSONObject();
        String responsestatus = "False";
        String profile_img="http://img2.3png.com/15a203b532e46e3440d0e89926363441d8e7.png";
        if(id!="") {
            try {
                System.out.println("start");
                JSONArray jarray = store_to_mysql(id);
                result.put("user_urls",jarray);
                responsestatus = "True";
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

    private JSONArray store_to_mysql(String id) throws SQLException {
        String sql = "SELECT * FROM ImageUrl  WHERE Id='" + id + "'";
        Statement stmt = null;
        JSONArray jarray = new JSONArray();

        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                JSONObject jobject=new JSONObject();
                jobject.put("url",rs.getString("ImageURL"));
                jarray.put(jobject);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }

        return jarray;
    }
}
