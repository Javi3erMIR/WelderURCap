package styleClasses;

public class V5Style extends Style {

    private static final int HORIZONTAL_SPACING = 10;
    private static final int VERTICAL_SPACING = 15;
    private static final int HORIZONTAL_INDENT = 20;

    @Override
    protected int getHorizontalSpacing() {
        return HORIZONTAL_SPACING;
    }

    @Override
    protected int getVerticalSpacing() {
        return VERTICAL_SPACING;
    }

    @Override
    protected int getHorizontalIndent() {
        return HORIZONTAL_INDENT;
    }
}
