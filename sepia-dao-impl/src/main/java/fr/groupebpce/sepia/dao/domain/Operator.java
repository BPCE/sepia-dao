package fr.groupebpce.sepia.dao.domain;

public enum Operator {
    AND, OR;

    public String trueConditionTrick() {
        return switch (this) {
            case AND -> "1=1";
            case OR -> "1=0";
        };
    }
}
