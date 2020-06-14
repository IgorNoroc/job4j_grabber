package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * 2. Парсинг HTML страницы. [#279183]
 */
public class SqlRuParse {
    private final static int DATE_POSITION = 5;

    public static void main(String[] args) {
        try {
            Document doc = Jsoup.connect("https://www.sql.ru/forum/job-offers").get();
            List<String> nameAndUrl = getNameAndUrl(doc);
            List<String> dates = getDates(doc);
            for (int i = 0; i < nameAndUrl.size(); i++) {
                System.out.println(nameAndUrl.get(i));
                System.out.println(dates.get(i));
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Получаем список объявлений и их ссылок.
     *
     * @param document html file.
     * @return список.
     */
    private static List<String> getNameAndUrl(Document document) {
        List<String> rsl = new ArrayList<>();
        Elements elements = document.select(".postslisttopic");
        for (Element td : elements) {
            Element href = td.child(0);
            rsl.add(href.text() + System.lineSeparator() + href.attr("href"));
        }
        return rsl;
    }

    /**
     * Получаем даты изменения Объявлений.
     *
     * @param document html file.
     * @return список дат.
     */
    private static List<String> getDates(Document document) {
        List<String> rsl = new ArrayList<>();
        Elements elements = document.getElementsByClass("forumTable").get(0).getElementsByTag("tr");
        for (int i = 1; i < elements.size(); i++) {
            rsl.add(elements.get(i).getElementsByTag("td").get(DATE_POSITION).text());
        }
        return rsl;
    }
}
