package devoxx.envers.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

/**
 * User: Romain Linsolas
 */
@Audited
@Entity
@Table(name = "T_PERSON")
public class Person {

    public Person() {
    }

    public Person(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)//, generator = "S_PERSON")
    private int id;

    @Column(name = "NAME")
    private String name;

    //@NotAudited
    @Column(name = "COMMENTS")
    private String comments;

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

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "Person { id=" + id + ", name='" + name + "', comments='" + StringUtils.trimToEmpty(comments) + "'}";
    }

}
