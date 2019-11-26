package com.org.biquge.jsoup.novel;

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
}
