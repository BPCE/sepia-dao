package fr.groupebpce.sepia.exception;

import java.io.Serializable;
import java.util.StringJoiner;

public class EntityNotFoundException extends RuntimeException {

    private final Serializable id;
    private final String entityName;

    public EntityNotFoundException(Serializable id, String entityName) {
        this.id = id;
        this.entityName = entityName;
    }


    @Override
    public String toString() {
        return new StringJoiner(", ", EntityNotFoundException.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("entityName='" + entityName + "'")
                .toString();
    }
}
