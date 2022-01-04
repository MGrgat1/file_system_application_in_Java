package hr.java.covidportal.enumeracije;

/**
 * Predstavlja tri vrijednosti koje simptom može poprimiti - "RIJETKO", "SREDNJE" i "ČESTO"
 */
public enum Vrijednost {
    RIJETKO("RIJETKO"),
    SREDNJE("SREDNJE"),
    ČESTO("ČESTO");

    private final String vrijednost;

    private Vrijednost(String vrijednost) {
        this.vrijednost = vrijednost;
    }

    public String getVrijednost() {
        return vrijednost;
    }
}
