package com.catalog.films.data;

import com.catalog.films.enums.OperatorEnum;
import com.catalog.films.model.Film;

import com.catalog.films.enums.TypeSearch;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.catalog.films.enums.TypeSearch.*;

public class DatabaseHandler {

    private final String path = "SELECT catalog_films.id, name_films.name, catalog_films.rating, genre.name, producer.name, year.year_release from catalog_films inner join genre on genre.id = catalog_films.genre inner join producer on producer.id = catalog_films.producer inner join name_films on name_films.id = catalog_films.name join year on year.id = catalog_films.year";
    Connection connection;
    ResultSet resSet = null;

    public Connection getDBConnection() {
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/users", "postgres", "medalenhunt73");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }

    public ResultSet getAttribute(TypeSearch typeSearch) {
        String getAttribute = switch (typeSearch) {
            case NAME -> "select * from name_films;";
            case YEAR -> "select * from year;";
            case PRODUCER -> "select * from producer;";
            case GENRE -> "select * from genre;";
            case ACTOR -> "select * from actor;";
            default -> throw new IllegalStateException("Unexpected value: " + typeSearch);
        };
        try {
            PreparedStatement prSt = getDBConnection().prepareStatement(getAttribute);
            resSet = prSt.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return resSet;
    }

    public ResultSet getActorFilms() {
        String getFilm = "SELECT name_films.name, actor.name from catalog_films inner join name_films on catalog_films.name = name_films.id Left join catalog_actor on catalog_actor.catalog_id = catalog_films.id left join actor on catalog_actor.actor_id = actor.id;";
        try {
            PreparedStatement prSt = getDBConnection().prepareStatement(getFilm);
            resSet = prSt.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return resSet;
    }

    public ResultSet getFilm() {
        try {
            PreparedStatement prSt = getDBConnection().prepareStatement(path + ";");
            resSet = prSt.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return resSet;
    }

    public void delete(int id) throws SQLException {
        String del = "DELETE FROM catalog_films WHERE id=" + id + ";";
        PreparedStatement prSt = getDBConnection().prepareStatement(del);
        prSt.executeUpdate();
    }

    public void change(int id, String text, TypeSearch type) throws SQLException {
        String update = switch (type) {
            case NAME -> "UPDATE catalog_films SET name = '" + searchAttribute(text, NAME) + "' WHERE id=" + id + ";";
            case RATE -> "UPDATE catalog_films SET rating = '" + text + "' WHERE id=" + id + ";";
            case YEAR -> "UPDATE catalog_films SET year = '" + searchAttribute(text, YEAR) + "' WHERE id=" + id + ";";
            case PRODUCER ->
                    "UPDATE catalog_films SET producer = '" + searchAttribute(text, PRODUCER) + "' WHERE id=" + id + ";";
            case GENRE ->
                    "UPDATE catalog_films SET genre = '" + searchAttribute(text, GENRE) + "' WHERE id=" + id + ";";
            case ACTOR -> null;
        };

        if (type == ACTOR) {
            checkActor(text, ACTOR, id);
        } else {
            PreparedStatement prSt = getDBConnection().prepareStatement(update);
            prSt.executeUpdate();
        }
    }

    public ResultSet getSearchAttribute(String str, TypeSearch typeSearch, OperatorEnum operator) {

        String getSearchName = switch (typeSearch) {
            case NAME -> path + " WHERE name_films.name LIKE '%" + str + "%';";
            case PRODUCER -> path + "WHERE producer.name LIKE '%" + str + "%';";
            case GENRE -> path + "WHERE genre.name LIKE '%" + str + "%';";
            case YEAR -> switch (operator){
                case GREATER -> path + " WHERE year.year_release>" + str + " ORDER BY year DESC;";
                case LESS -> path + " WHERE year.year_release<" + str + " ORDER BY year DESC;";
                case EQUAL -> path + " WHERE year.year_release=" + str + " ORDER BY year DESC;";
            };
            case RATE -> switch (operator){
                case GREATER -> path + " WHERE rating>" + str + ";";
                case LESS -> path + " WHERE rating<" + str + ";";
                case EQUAL -> path + " WHERE rating=" + str + ";";
            };
            case ACTOR -> path;
        };
        try {
            PreparedStatement prSt = getDBConnection().prepareStatement(getSearchName);
            resSet = prSt.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return resSet;
    }

    public int addAttribute(String str, TypeSearch type) throws SQLException {
        String insertAttribute = switch (type) {
            case GENRE -> "INSERT INTO genre (name) VALUES (?);";
            case PRODUCER -> "INSERT INTO producer (name) VALUES (?);";
            case ACTOR -> "INSERT INTO actor (name) VALUES (?);";
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
        PreparedStatement prSt = getDBConnection().prepareStatement(insertAttribute);
        prSt.setString(1, str);
        prSt.executeUpdate();
        return searchAttribute(str, type);
    }

    public int searchAttribute(String str, TypeSearch type) throws SQLException {
        String likeAttribute = switch (type) {
            case GENRE -> "SELECT id FROM genre WHERE name LIKE '" + str + "';";
            case YEAR -> "SELECT id FROM year WHERE year_release =" + str + ";";
            case NAME -> "SELECT id FROM name_films WHERE name LIKE '" + str + "';";
            case PRODUCER -> "SELECT id FROM producer WHERE name LIKE '" + str + "';";
            case ACTOR -> "SELECT id FROM actor WHERE name LIKE '" + str + "';";
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
        PreparedStatement prSt = getDBConnection().prepareStatement(likeAttribute);
        resSet = prSt.executeQuery();
        if (resSet.next()) {
            return resSet.getInt(1);
        }
        return 0;
    }

    public void checkActor(String str, TypeSearch type, int id) {
        String last = "SELECT * FROM catalog_films ORDER BY id DESC LIMIT 1;";
        List<String> myList = new ArrayList<>(Arrays.asList(str.split(",")));
        List<Integer> catalogActor = new ArrayList<>();
        myList.forEach(it -> {
            try {
                catalogActor.add(searchAttribute(it, type));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        String insertCatalogActor = "INSERT INTO catalog_actor (catalog_id, actor_id) VALUES (?,?);";
        catalogActor.forEach(it -> {
            PreparedStatement prSt;
            try {
                int lastIndex = 0;
                if (id == 0) {
                    prSt = getDBConnection().prepareStatement(last);
                    resSet = prSt.executeQuery();
                    if (resSet.next()) {
                        lastIndex = resSet.getInt(1);
                    }
                } else lastIndex = id;

                prSt = getDBConnection().prepareStatement(insertCatalogActor);
                prSt.setInt(1, lastIndex);
                prSt.setInt(2, it);
                prSt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

    }

    public void insertAttribute(TypeSearch typeSearch, String value) throws SQLException {
        if (searchAttribute(value, typeSearch) != 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Такое поле уже существует...", ButtonType.CANCEL);
            alert.showAndWait();
        } else {
            String insertAttribute = switch (typeSearch) {

                case NAME -> "INSERT INTO name_films (Name) VALUES (?)";
                case YEAR -> "INSERT INTO year (year_release) VALUES (?)";
                case PRODUCER -> "INSERT INTO producer (Name) VALUES (?)";
                case GENRE -> "INSERT INTO genre (Name) VALUES (?)";
                case ACTOR -> "INSERT INTO actor (Name) VALUES (?)";
                case RATE -> null;

            };
            PreparedStatement prSt = getDBConnection().prepareStatement(insertAttribute);
            if (typeSearch == YEAR) {
                prSt.setInt(1, Integer.parseInt(value));
            } else {
                prSt.setString(1, value);
            }
            prSt.executeUpdate();
        }
    }

    public void insertFilm(Film film) {
        String insertFilm = "INSERT INTO catalog_films (Name, Rating, Year, Genre, Producer) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement prSt = getDBConnection().prepareStatement(insertFilm);
            prSt.setInt(1, searchAttribute(film.getName(), NAME));
            prSt.setDouble(2, Double.parseDouble(film.getRating()));
            prSt.setInt(3, searchAttribute(film.getYear(), YEAR));
            prSt.setInt(4, searchAttribute(film.getGenre(), GENRE));
            prSt.setInt(5, searchAttribute(film.getProducer(), PRODUCER));
            prSt.executeUpdate();
            checkActor(film.getActor(), ACTOR, 0);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
