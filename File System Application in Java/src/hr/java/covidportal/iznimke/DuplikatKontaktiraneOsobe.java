package hr.java.covidportal.iznimke;

/**
 * Dojavljuje da je osoba već unesena u popis kontaktiranih osoba
 */
public class DuplikatKontaktiraneOsobe extends Exception {

    public DuplikatKontaktiraneOsobe(String message) {
        super(message);
    }
    public DuplikatKontaktiraneOsobe(String message, Throwable cause) {
        super(message, cause);
    }
    public DuplikatKontaktiraneOsobe(Throwable cause) {
        super(cause);
    }
}
