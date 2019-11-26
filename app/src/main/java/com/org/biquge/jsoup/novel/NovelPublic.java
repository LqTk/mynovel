package com.org.biquge.jsoup.novel;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

public class NovelPublic {
    public static String novelHomeUrl = "http://www.xbiquge.la";//笔趣阁
    public static String novelsContent = "http://www.xbiquge.la/xiaoshuodaquan/";
    public static String novelSaveDirName = "/小说";
    public static String downLoadingUpdata = "novelLoadingUpdata";
    public static String novelHomeUrl2 = "http://book.zongheng.com/store/c0/c0/b0/u0/p2/v9/s9/t0/u0/i1/ALL.html";//纵横中文网
    public static String biqudao = "https://www.biqudao.com/";//笔趣岛

    //type 1笔趣阁,2纵横中文网，3笔趣岛
    public static String getHomeUrl(int type){
        String returnStr=novelHomeUrl;
        switch (type){
            case 1:
                returnStr=novelHomeUrl;
                break;
            case 2:
                returnStr=novelHomeUrl2;
                break;
            case 3:
                returnStr=biqudao;
                break;
        }
        return returnStr;
    }

    //HTTPS证书认证
    public static void trustEveryone(){
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            });
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null,new X509TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            },new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
