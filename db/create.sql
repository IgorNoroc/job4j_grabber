create table post (
        id serial primary key,
        name text,
        text text,
        link text,
        created date
        constraint p_link unique (link)
);