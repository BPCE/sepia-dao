package fr.groupebpce.sepia.criteria.filter;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.upperCase;

@Getter
@Setter
public class StringFilter implements Serializable {

    // ========== ATTRIBUTES ==========

    /**
     * The value that we search in database
     */
    private String value;

    /**
     * Indicates if it is an exact search (=) or not (approximative search... LIKE)
     */
    private Boolean isExact = true;
    
    private Boolean isIgnoreCase = false;

    // ========== CONSTRUCTOR ==========

    public StringFilter() {
    }

    public StringFilter(String value, boolean isExact) {
        this.value = value;
        this.isExact = isExact;
    }

    public StringFilter(String value, boolean isExact, boolean isIgnoreCase) {
        this.value = value;
        this.isExact = isExact;
        this.isIgnoreCase = isIgnoreCase;
    }

    public static StringFilter exactMatches(String value) {
        return new StringFilter(value, true);
    }

    public static StringFilter like(String value) {
        return new StringFilter(value, false);
    }

    // ========== FLUENT SETTERS ==========

    public StringFilter ignoreCase() {
        this.isIgnoreCase = true;
        return this;
    }

    // ========== SPECIFIC GETTERS ==========

    public Boolean isExact() {
        return getIsExact();
    }

    public String getValueConsideringCase() {
        if (isTrue(getIsIgnoreCase()))
            return upperCase(getValue());
        return getValue();
    }
    
    public String getOperator() {
        return isTrue(getIsExact()) ? "=" : "like";
    }
    
    public String getEscape() {
        return isTrue(getIsExact()) ? "" : "escape '/'";
    }
    
    // ========== OBJECT ==========

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringFilter that = (StringFilter) o;
        return isExact == that.isExact && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, isExact);
    }
    
}
