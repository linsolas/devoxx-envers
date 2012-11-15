package devoxx.envers.test;

import java.sql.SQLException;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.junit.BeforeClass;
import org.junit.Test;

import devoxx.envers.model.Person;

/**
 * User: Romain Linsolas
 */
public class PersonTest {

    private static EntityManager entityManager;

    @BeforeClass
    public static void init() throws SQLException {
        DBUtils.initDatabase();
        entityManager = DBUtils.getEntityManager();
    }

    @Test
    public void test_envers_for_devoxx() throws SQLException {
        DBUtils.displayDBContent();
        Person me = new Person("Romain");
        save(me);
        Person chuck = new Person("Chuck", "Norris");
        chuck.setComments("The man");
        save(chuck);
        DBUtils.displayDBContent();

        me.setSurname("Linsolas");
        save(me);
        DBUtils.displayDBContent();

        me.setComments("foobar");
        save(me);
        DBUtils.displayDBContent();

        kill(me);
        DBUtils.displayDBContent();

        AuditReader reader = AuditReaderFactory.get(entityManager);

        List<Number> revisions = reader.getRevisions(Person.class, me.getId());
        for (Number n : revisions) {
            Person p = reader.find(Person.class, me.getId(), n);
            System.out.printf("\t[Rev #%1$s] > %2$s\n", n, p);
        }

        AuditQuery query = reader.createQuery().forRevisionsOfEntity(Person.class, false, true);
        // query.add(AuditEntity.property("name").like("Romain%"));
        List<Object[]> list = query.getResultList();
        for (Object[] array : list) {
            for (Object x : array) {
                System.out.print(x + " | ");
            }
            System.out.println("");
        }

        List<Person> persons = reader.createQuery().forEntitiesAtRevision(Person.class, 3).getResultList();
        for (Person p : persons) {
            System.out.println("--> " + p);
        }
    }

    private void save(Object o) {
        entityManager.getTransaction().begin();
        entityManager.persist(o);
        entityManager.getTransaction().commit();
    }

    private void kill(Object o) {
        entityManager.getTransaction().begin();
        entityManager.remove(o);
        entityManager.getTransaction().commit();
    }

}
