package model;

/**
 * Created by lubobul on 9/17/2015.
 */
public class Author {

    private int id;
    private String name;

    public Author(String name) {
        this.id = 0;
        this.name = name;
    }

    public Author(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
