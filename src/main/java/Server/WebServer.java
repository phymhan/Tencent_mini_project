package Server;

import GetUrls.GetUrl;
import Register.Register;
import StoreURL.Storeurl;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.*;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.Handlers;

import javax.servlet.ServletException;

import Login.LoginServlet;
import User.*;
import TexttoMotion.*;

import static io.undertow.servlet.Servlets.servlet;


/*
	You don't have to modify this file to finish task 1~5.
*/

public class WebServer {
    public WebServer() throws Exception{}

    public static final String PATH = "/";

    public static void main(String[] args) throws Exception{
        try {
            DeploymentInfo servletBuilder = Servlets.deployment()
                    .setClassLoader(WebServer.class.getClassLoader())
                    .setContextPath(PATH)
                    .setDeploymentName("handler.war")
                    .addServlets(
                            servlet("LoginServlet", LoginServlet.class)
                                    .addMapping("/login")  // 是登陆请求,调用Login的类
                    ).addServlets(
                            servlet("GetUser", GetUser.class)
                                    .addMapping("/getuser")
                    ).addServlets(
                            servlet("Store_url", Storeurl.class)
                                    .addMapping("/transform")
                    ).addServlets(
                            servlet("Register", Register.class)
                                    .addMapping("/register")
                    ).addServlets(
                            servlet("GetUrl", GetUrl.class)
                                    .addMapping("/geturl")
                    );

            DeploymentManager manager = Servlets.defaultContainer().addDeployment(servletBuilder);
            manager.deploy();

            HttpHandler servletHandler = manager.start();
            PathHandler path = Handlers.path(Handlers.redirect(PATH))
                    .addPrefixPath(PATH, servletHandler);

            Undertow server = Undertow.builder()
                    //.setIoThreads(40)
                    .setWorkerThreads(300)
                    .addHttpListener(80, "0.0.0.0")
                    .setHandler(path)
                    .build();

            server.start();
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }
}

