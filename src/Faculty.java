public enum Faculty {
    SITE("School of Information Technology and Engineering"),
    SEOGI("School of Energy, Oil and Gas Industry"),
    SG("School of Geology"),
    BS("Business School"),
    ISE("International School of Economics"),
    KMA("Kazakhstan Maritime Academy"),
    SAM("School of Applied Mathematics"),
    SCE("School of Chemical Engineering"),
    SMGT("School of Materials Science and Green Technologies"),;

    private final String fullName;
    Faculty(String fullName) { this.fullName = fullName; }
    @Override
    public String toString() { return name() + " - " + fullName; }
}