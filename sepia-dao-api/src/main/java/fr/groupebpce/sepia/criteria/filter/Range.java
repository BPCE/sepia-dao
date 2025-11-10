package fr.groupebpce.sepia.criteria.filter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Range <T extends Number> {
    
    T min;
    T max;
    
    public Range(T min, T max) {
        this.min = min;
        this.max = max;
    }

    public static <T extends Number> Range<T> bounded(T min, T max) {
        return new Range<>(min, max);
    }

    public static <T extends Number> Range<T> minorized(T min) {
        return new Range<>(min, null);
    }

    public static <T extends Number> Range<T> majorized(T max) {
        return new Range<>(null, max);
    }
    
    public boolean isMinorized() {
        return hasMin() && !hasMax();
    }
    public boolean isMajorized() {
        return !hasMin() && hasMax();
    }
    public boolean isBounded() {
        return hasMin() && hasMax();
    }

    private boolean hasMin() {
        return min != null;
    }
    private boolean hasMax() {
        return max != null;
    }
    
}
