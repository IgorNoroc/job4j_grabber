package ru.job4j.grabber;

import java.util.List;

/**
 *  3. Архитектура проекта - Аргегатора Java Вакансий[260359#279184]
 */
public interface Store {
    void save(Post post);

    List<Post> getAll();
}
