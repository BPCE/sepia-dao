package fr.groupebpce.sepia.dao.fetch;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class FetchNode {

    private final String id;
    private String property;
    private Class<?> classType;
    private boolean isCollection;
    private FetchNode parent;
    private final List<FetchNode> children = new ArrayList<>();

    public FetchNode(String id) {
        this.id = id;
    }

    public FetchNode property(String property) {
        this.property = property;
        return this;
    }

    public FetchNode parent(FetchNode parent) {
        this.parent = parent;
        return this;
    }

    public FetchNode classType(Class<?> classType) {
        this.classType = classType;
        return this;
    }

    public FetchNode collection(boolean collection) {
        isCollection = collection;
        return this;
    }
    
    public boolean isNotCollection() {
        return !isCollection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FetchNode fetchNode)) return false;
        return Objects.equals(getId(), fetchNode.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
