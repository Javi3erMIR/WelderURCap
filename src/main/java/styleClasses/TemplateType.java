package styleClasses;

public enum TemplateType {

    EMPTY("Tracking Weld Mode"), ARCON("Arc ON"), ARCOFF("Arc OFF");

    private final String name;

    TemplateType(final String name) {
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    public static TemplateType valueOfByName(String name) {
        if (ARCON.name.equals(name)) {
            return ARCON;
        } else if (ARCOFF.name.equals(name)) {
            return ARCOFF;
        }
        return EMPTY;
    }
}
