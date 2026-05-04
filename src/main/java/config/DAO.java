package config;

import javafx.collections.ObservableList;

public interface DAO <T>{

    ObservableList<T> getAll();

    T get(String id);

    void create(T t);

    void update(T t);

    void delete(T t);
}
