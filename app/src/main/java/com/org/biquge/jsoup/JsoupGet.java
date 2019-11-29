package com.org.biquge.jsoup;

import com.org.biquge.jsoup.novel.NovelPublic;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.org.biquge.jsoup.novel.NovelPublic.novelHomeUrl;

public class JsoupGet {
    public List<HashMap> getHtmlContent(String htmlUrl) throws IOException {
        Document doc = Jsoup.connect(htmlUrl).maxBodySize(0).get();
//            Element content = doc.getElementById("content");
        Elements elements = doc.getElementsByClass("novellist").first().getElementsByTag("li");
        List<HashMap> novalTitle = new ArrayList<>();
        for (Element element : elements) {
            Element link = element.getElementsByTag("a").first();
            String href = link.attr("href");
            String name = link.text();
            /*if(!href.isEmpty()) {
                doc = Jsoup.connect(href).maxBodySize(0).get();
                Elements imageElements = doc.getElementById("fmimg").getElementsByTag("img");
                Element imgItem = imageElements.get(0).getElementsByTag("img").first();
                String img = imgItem.attr("src");
            }*/
            HashMap childMap = new HashMap();
            childMap.put("href", href);
            childMap.put("name", name);
//            childMap.put("img",img);
            novalTitle.add(childMap);
        }
        return novalTitle;
    }

    public List<List<HashMap>> getItemContent(String htmlUrl) throws IOException {
        List<List<HashMap>> itemContents = new ArrayList<>();
        List<HashMap> authorList = new ArrayList<>();
        HashMap authorMap = new HashMap();

        List<HashMap> itemsList = new ArrayList<>();

        Document doc = Jsoup.connect(htmlUrl).maxBodySize(0).get();

        Element author = doc.getElementById("info");
        Elements allElements = author.getAllElements();
        Elements nameElement = allElements.get(1).getElementsByTag("h1");
        String name = nameElement.text();
        authorMap.put("title",name);
        Elements authorNameElement = allElements.get(2).getElementsByTag("p");
        String autourName = authorNameElement.text();
        authorMap.put("author",autourName);
        Elements timeElement = allElements.get(7).getElementsByTag("p");
        String time = timeElement.text();
        authorMap.put("time",time);
        Element recentElement = allElements.get(9).getElementsByTag("a").first();
        String recentHref = recentElement.attr("href");
        String recentTitle = recentElement.text();
        authorMap.put("recentString","最新："+recentTitle);
        authorMap.put("recentHref",recentHref);

        Elements imgElement = doc.getElementById("fmimg").getElementsByTag("img");
        String img = imgElement.get(0).attr("src");
        authorMap.put("img",img);

        authorList.add(authorMap);

        Elements allItems = doc.getElementById("list").getElementsByTag("dd");
        for (int i=0;i<allItems.size();i++){
            Elements a = allItems.get(i).getElementsByTag("a");
            String itemHref = a.attr("href");
            String itemName = a.text();
            HashMap itemMap = new HashMap();
            itemMap.put("href",itemHref);
            itemMap.put("name",itemName);
            itemsList.add(itemMap);
        }

        itemContents.add(authorList);
        itemContents.add(itemsList);

        return itemContents;
    }

    public HashMap getReadItem(String htmlUrl) throws IOException {
//        NovelPublic.trustEveryone();
        HashMap headMap = new HashMap();
        Document doc = Jsoup.connect(htmlUrl).maxBodySize(0).get();

        Elements bookname = doc.getElementsByClass("bookname");
        Elements name = bookname.get(0).getElementsByTag("h1");
        headMap.put("name",name.get(0).text());

        Elements bottem1 = doc.getElementsByClass("bottem1");
        Elements allElements = bottem1.get(0).getAllElements();
        Element lastChapter = allElements.get(3).getElementsByTag("a").first();
        Element allChapter = allElements.get(4).getElementsByTag("a").first();
        Element nextChapter = allElements.get(5).getElementsByTag("a").first();
        headMap.put("lastChapter",lastChapter.attr("href"));
        headMap.put("allChapter",allChapter.attr("href"));
        headMap.put("nextChapter",nextChapter.attr("href"));

        Element content = doc.getElementById("content");
        Elements nodes = content.getElementsByTag("p");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i=0;i<nodes.size();i++){
            String text = nodes.get(i).text().trim();
            if (!text.equals("　　;")) {
                stringBuilder.append(text);
            }
        }
        String s = stringBuilder.toString();
        String str1 = s.substring(0,3);
        String str2 = s.substring(3,s.length());
        headMap.put("content",str1+str2.replace("　　","\n　　"));

        return headMap;
    }

    public List<List<HashMap>> getBooks3(String htmlUrl) throws IOException {
        NovelPublic.trustEveryone();
        List<List<HashMap>> reslut = new ArrayList<>();
        List<HashMap> books = new ArrayList<>();
        List<HashMap> chapters = new ArrayList<>();
        Document doc = Jsoup.connect(htmlUrl).maxBodySize(0).get();
        Elements l = doc.getElementsByClass("l");
        for (int i=0;i<l.size();i++){
            Element item = l.get(i);
            if (i==0) {
                Elements image = item.getElementsByClass("image");
                for (Element imageItem:image) {
                    Elements a = imageItem.getElementsByTag("a");
                    String bookUrl = a.attr("href");
                    Elements img = a.get(0).getElementsByTag("img");
                    HashMap hashMap = new HashMap();
                    hashMap.put("bookUrl", bookUrl);
                    hashMap.put("name", img.attr("alt"));
                    hashMap.put("imgUrl", img.attr("src"));
                    books.add(hashMap);
                }
            }else if (i==1){
                Elements ul = item.getElementsByTag("ul");
                for (Element li:ul.get(0).getElementsByTag("li")){
                    HashMap hashMap = new HashMap();
                    Elements s2 = li.getElementsByClass("s2");
                    Elements s3 = li.getElementsByClass("s3");
                    Elements s4 = li.getElementsByClass("s4");
                    Elements s5 = li.getElementsByClass("s5");
                    String name = s2.text();
                    String nameUrl = s2.get(0).getElementsByTag("a").attr("href");
                    String recentChapterUrl = s3.get(0).getElementsByTag("a").attr("href");
                    String recentChapterName = s3.text();
                    String author = s4.text();
                    String time = s5.text();
                    hashMap.put("name",name);
                    hashMap.put("nameurl",nameUrl);
                    hashMap.put("chapterUrl",recentChapterUrl);
                    hashMap.put("chapterName",recentChapterName);
                    hashMap.put("author",author);
                    hashMap.put("time",time);
                    chapters.add(hashMap);
                }
            }
        }
        reslut.add(books);
        reslut.add(chapters);

        return reslut;
    }

    public List<List<HashMap>> getBooksPage(String htmlUrl) throws IOException {
        NovelPublic.trustEveryone();
        List<List<HashMap>> reslut = new ArrayList<>();
        List<HashMap> books = new ArrayList<>();
        List<HashMap> chapters = new ArrayList<>();
        Document doc = Jsoup.connect(htmlUrl).maxBodySize(0).get();
        Elements ll = doc.getElementsByClass("ll");
        for (int i=0;i<ll.size();i++){
            Element llItem = ll.get(i);
            if (i==0) {
                Elements item = llItem.getElementsByClass("item");
                for (Element divItem:item) {
                    Elements image = divItem.getElementsByClass("image");
                    String bookUrl = image.get(0).getElementsByTag("a").attr("href");
                    Elements img = divItem.getElementsByTag("img");
                    HashMap hashMap = new HashMap();
                    hashMap.put("bookUrl", bookUrl);
                    hashMap.put("name", img.attr("alt"));
                    hashMap.put("imgUrl", img.attr("src"));
                    books.add(hashMap);
                }
            }
        }
        Elements l = doc.getElementsByClass("l");
        Element item = l.get(0);
        Elements ul = item.getElementsByTag("ul");
        for (Element li:ul.get(0).getElementsByTag("li")){
            HashMap hashMap = new HashMap();
            Elements s2 = li.getElementsByClass("s2");
            Elements s3 = li.getElementsByClass("s3");
            Elements s4 = li.getElementsByClass("s4");
            Elements s5 = li.getElementsByClass("s5");
            String name = s2.text();
            String nameUrl = s2.get(0).getElementsByTag("a").attr("href");
            String recentChapterUrl = s3.get(0).getElementsByTag("a").attr("href");
            String recentChapterName = s3.text();
            String author = s4.text();
            String time = s5.text();
            hashMap.put("name",name);
            hashMap.put("nameurl",nameUrl);
            hashMap.put("chapterUrl",recentChapterUrl);
            hashMap.put("chapterName",recentChapterName);
            hashMap.put("author",author);
            hashMap.put("time",time);
            chapters.add(hashMap);
        }

        reslut.add(books);
        reslut.add(chapters);

        return reslut;
    }

    public List<List<HashMap>> getPageContent(String htmlUrl) throws IOException {
        NovelPublic.trustEveryone();
        List<List<HashMap>> itemContents = new ArrayList<>();
        List<HashMap> authorList = new ArrayList<>();
        HashMap authorMap = new HashMap();

        List<HashMap> itemsList = new ArrayList<>();

        Document doc = Jsoup.connect(htmlUrl).maxBodySize(0).get();

        Elements allElements = doc.getElementById("info").getAllElements();
        String name = allElements.get(1).text();
        authorMap.put("title",name);
        String autourName = allElements.get(2).text();
        authorMap.put("author",autourName);
        String time = allElements.get(9).text();
        authorMap.put("time",time);

        Elements imgElement = doc.getElementById("sidebar").getElementById("fmimg").getElementsByTag("img");
        String img = imgElement.get(0).attr("src");
        authorMap.put("img",img);

        Elements dls = doc.getElementById("list").getElementsByTag("dl");

        Elements allElements1 = dls.get(0).getAllElements();
        int index = 0;
        int count = 0;
        int ddcount = 0;
        for (index=0;index<allElements1.size();index++){
            Element allItem = allElements1.get(index);
            String s = allItem.tagName();
            if (!s.equals("dl") && s.equals("dd")){
                count++;
                ddcount++;
                if (count>9){
                    break;
                }
            }else if (s.equals("dl")){
                count=0;
            }
        }

        Elements allItems = doc.getElementById("list").getElementsByTag("dl").get(0).getElementsByTag("dd");
        for (int i=0;i<allItems.size();i++){
            Elements a = allItems.get(i).getElementsByTag("a");
            String itemHref = a.attr("href");
            String itemName = a.text();
            HashMap itemMap = new HashMap();
            itemMap.put("href",itemHref);
            itemMap.put("name",itemName);
            itemsList.add(itemMap);
        }
        itemsList = itemsList.subList(ddcount-1,itemsList.size());


        authorMap.put("recentString","最新："+itemsList.get(itemsList.size()-1).get("name"));
        authorMap.put("recentHref",itemsList.get(itemsList.size()-1).get("href"));

        authorList.add(authorMap);

        itemContents.add(authorList);
        itemContents.add(itemsList);

        return itemContents;
    }

    public List<HashMap> getSearchBook(String htmlUrl) throws IOException{
        NovelPublic.trustEveryone();
        List<HashMap> resultList = new ArrayList<>();
        Document doc = Jsoup.connect(htmlUrl).maxBodySize(0).get();
        Element main = doc.getElementById("main");
        if (main!=null) {
            Elements uls = main.getElementsByTag("tbody");
            if (uls.size() > 0) {
                Elements lis = uls.get(0).getElementsByTag("tr");
                for (int i = 1; i < lis.size(); i++) {
                    HashMap hashMap = new HashMap();
                    Elements elements = lis.get(i).getAllElements();
                    String bookName = elements.get(1).getElementsByClass("odd").text();
                    String bookUrl = elements.get(1).getElementsByClass("odd").get(0).getElementsByTag("a").attr("href");
                    String author = elements.get(5).text();
                    String time = elements.get(7).text();
                    hashMap.put("name", bookName);
                    hashMap.put("bookUrl", bookUrl);
                    hashMap.put("author", author);
                    hashMap.put("time", time);
                    resultList.add(hashMap);
                }
            }
        }
        return resultList;
    }

    public List<HashMap> getFullNovel(String htmlUrl) throws IOException{
        List<HashMap> resultList = new ArrayList<>();
        Document doc = Jsoup.connect(htmlUrl).maxBodySize(0).get();
        Elements novellists = doc.getElementById("main").getElementsByClass("novellist");
        for (Element novelItem:novellists){
            Elements lis = novelItem.getElementsByTag("li");
            for (Element itemli:lis){
                Elements strong = itemli.getElementsByTag("strong");
                if (strong!=null && strong.text().contains("完本")){
                    HashMap hashMap = new HashMap();
                    hashMap.put("name",itemli.getElementsByTag("a").text());
                    hashMap.put("bookUrl",itemli.getElementsByTag("a").first().attr("href"));
                    resultList.add(hashMap);
                }
            }
        }
        return resultList;
    }

}
