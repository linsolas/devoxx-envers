package devoxx.envers.model;

import org.hibernate.envers.RevisionListener;
import org.hibernate.mapping.Component;

/**
 * User: Romain Linsolas Date: 06/11/12
 */
public class UserRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        ((MyEntityRevision) revisionEntity).setUsername("Devoxx");
    }
}
