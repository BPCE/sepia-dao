package fr.groupebpce.sepia.criteria.fetching;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class Fetching {

    private List<String> pathsToFetch = new ArrayList<>();

    
    public static Fetching fetch(String... paths) {
        Fetching fetching = new Fetching();
        fetching.pathsToFetch.addAll(Arrays.asList(paths));
        return fetching;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Fetching fetching)) return false;
        return Objects.equals(getPathsToFetch(), fetching.getPathsToFetch());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getPathsToFetch());
    }
}
