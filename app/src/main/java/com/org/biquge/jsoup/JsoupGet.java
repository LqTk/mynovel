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
        List<HashMap> readList = new ArrayList<>();
        HashMap headMap = new HashMap();
        Document doc = Jsoup.connect(htmlUrl).maxBodySize(0).get();

        Elements bookname = doc.getElementsByClass("bookname");
        Elements name = bookname.get(0).getElementsByTag("h1");
        headMap.put("name",name.get(0).text());

        Elements bottem1 = doc.getElementsByClass("bottem1");
        Elements allElements = bottem1.get(0).getAllElements();
        Element lastChapter = allElements.get(2).getElementsByTag("a").first();
        Element allChapter = allElements.get(3).getElementsByTag("a").first();
        Element nextChapter = allElements.get(4).getElementsByTag("a").first();
        headMap.put("lastChapter",novelHomeUrl+lastChapter.attr("href"));
        headMap.put("allChapter",allChapter.attr("href"));
        headMap.put("nextChapter",novelHomeUrl+nextChapter.attr("href"));

        Element content = doc.getElementById("content");
        List<TextNode> nodes = content.textNodes();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i=0;i<nodes.size();i++){
            stringBuilder.append(nodes.get(i).text());
        }
        headMap.put("content",stringBuilder.toString().replace("  ","\n      "));

        return headMap;
    }

    public List<List<HashMap>> getBooks3(String htmlUrl) throws IOException {
        NovelPublic.trustEveryone();
        List<List<HashMap>> reslut = new ArrayList<>();
        List<HashMap> books = new ArrayList<>();
        Document doc = Jsoup.connect(htmlUrl).maxBodySize(0).get();
        Elements l = doc.getElementsByClass("l");
        for (int i=0;i<l.size();i++){
            Element item = l.get(i);
            if (i==0) {
                Elements image = item.getElementsByClass("image");
                for (Element imageItem:image) {
                    Elements a = imageItem.getElementsByTag("a");
                    Elements img = a.get(0).getElementsByTag("img");
                    HashMap hashMap = new HashMap();
                    hashMap.put("name", img.attr("alt"));
                    hashMap.put("url", NovelPublic.getHomeUrl(3) + img.attr("src"));
                    books.add(hashMap);
                }
            }
        }
        reslut.add(books);

        return reslut;
    }
}
