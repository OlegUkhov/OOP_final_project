public enum Topic {
    General("General"),
    Academic("Academic"),
    Research("Research"),
    Events("Events");

    private final String label;
    Topic(String label) { this.label = label; }
    @Override public String toString() { return label; }
}
