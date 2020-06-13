package ru.job4j.grabber;

import java.util.List;

/**
 *  3. Архитектура проекта - Аргегатора Java Вакансий[260359#279184]
 */
public interface Parse {
    List<Post> list(String link);

    Post detail(String link);
}
