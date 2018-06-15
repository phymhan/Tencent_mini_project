package TexttoMotion;

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

import java.util.TreeMap;

import com.qcloud.Module.Wenzhi;
import com.qcloud.QcloudApiModuleCenter;
import com.qcloud.Module.Cvm;
import com.qcloud.Utilities.Json.JSONObject;

//import org.json.JSONObject;
import org.json.JSONArray;

public class Textmotionquery extends HttpServlet {


    public Textmotionquery() throws ClassNotFoundException, SQLException, IOException {
        System.out.println("text to motion service start");
    }

    /*
    * doGet 函数
    *
    * */
    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response) throws ServletException, IOException {

        String text=request.getParameter("text");
        System.out.println("get");
        System.out.println(text);
        String result = "";
        try {
            result=texttomotion(text);
        } catch (SQLException e) {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write(String.format("%s", result));   // 写入response返回给前端
        writer.println();
        writer.close();
    }

    @Override
    protected void doPost(final HttpServletRequest request,
                          final HttpServletResponse response) throws ServletException, IOException {
        System.out.println("do Post!!!!");
        doGet(request, response);
    }

    private String texttomotion(String text) throws SQLException {
        TreeMap<String, Object> config = new TreeMap<String, Object>();
        config.put("SecretId", "AKIDCez59QbeZKxVDkemHcqMreXGoIRV2QAk");
        config.put("SecretKey", "a0IPOEtNix30tb8f2DC4lHcmlYNXYP4c");
		/* 请求方法类型 POST、GET */
        config.put("RequestMethod", "POST");
		/* 区域参数，可选: gz:广州; sh:上海; hk:香港; ca:北美;等。 */
        config.put("DefaultRegion", "gz");

		/*
		 * 你将要使用接口所在的模块，可以从 官网->云api文档->XXXX接口->接口描述->域名
		 * 中获取，比如域名：cvm.api.qcloud.com，module就是 new Cvm()。
		 */
		/*
		 * 示例：DescribeInstances
		 * 的api文档地址：http://www.qcloud.com/wiki/v2/DescribeInstances
		 */
        QcloudApiModuleCenter module = new QcloudApiModuleCenter(new Wenzhi(),
                config);

        TreeMap<String, Object> params = new TreeMap<String, Object>();
		/* 将需要输入的参数都放入 params 里面，必选参数是必填的。 */
		/* DescribeInstances 接口的部分可选参数如下 */
        params.put("content", text);
		/*在这里指定所要用的签名算法，不指定默认为HmacSHA1*/
        //params.put("SignatureMethod", "HmacSHA256");

		/* generateUrl方法生成请求串,可用于调试使用 */
        //System.out.println(module.generateUrl("DescribeInstances", params));
        String result = null;
        try {
			/* call 方法正式向指定的接口名发送请求，并把请求参数params传入，返回即是接口的请求结果。 */
            result = module.call("TextSentiment", params);
            JSONObject json_result = new JSONObject(result);
            System.out.println(json_result);
        } catch (Exception e) {
            System.out.println("error..." + e.getMessage());
        }
        return result.toString();
    }

//    private String audiototext(String url) throws SQLException {
//        TreeMap<String, Object> config = new TreeMap<String, Object>();
//        config.put("SecretId", "AKIDCez59QbeZKxVDkemHcqMreXGoIRV2QAk");
//        config.put("SecretKey", "a0IPOEtNix30tb8f2DC4lHcmlYNXYP4c");
//		/* 请求方法类型 POST、GET */
//        config.put("RequestMethod", "POST");
//		/* 区域参数，可选: gz:广州; sh:上海; hk:香港; ca:北美;等。 */
//        config.put("DefaultRegion", "gz");
//
//		/*
//		 * 你将要使用接口所在的模块，可以从 官网->云api文档->XXXX接口->接口描述->域名
//		 * 中获取，比如域名：cvm.api.qcloud.com，module就是 new Cvm()。
//		 */
//		/*
//		 * 示例：DescribeInstances
//		 * 的api文档地址：http://www.qcloud.com/wiki/v2/DescribeInstances
//		 */
//        QcloudApiModuleCenter module = new QcloudApiModuleCenter(new ,
//                config);
//
//        TreeMap<String, Object> params = new TreeMap<String, Object>();
//		/* 将需要输入的参数都放入 params 里面，必选参数是必填的。 */
//		/* DescribeInstances 接口的部分可选参数如下 */
//        params.put("Url", url);
//		/*在这里指定所要用的签名算法，不指定默认为HmacSHA1*/
//        //params.put("SignatureMethod", "HmacSHA256");
//
//		/* generateUrl方法生成请求串,可用于调试使用 */
//        //System.out.println(module.generateUrl("DescribeInstances", params));
//        String result = null;
//        try {
//			/* call 方法正式向指定的接口名发送请求，并把请求参数params传入，返回即是接口的请求结果。 */
//            result = module.call("SentenceRecognition", params);
//            JSONObject json_result = new JSONObject(result);
//            System.out.println(json_result);
//        } catch (Exception e) {
//            System.out.println("error..." + e.getMessage());
//        }
//        return result.toString();
//    }

}