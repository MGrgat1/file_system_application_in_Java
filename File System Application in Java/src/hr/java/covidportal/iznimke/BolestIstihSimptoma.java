package hr.java.covidportal.iznimke;

/**
 * Dojavljuje da već postoji bolest sa istim simptomima
 */
public class BolestIstihSimptoma extends RuntimeException{
    public BolestIstihSimptoma(String message) {
        super(message);
    }
    public BolestIstihSimptoma(String message, Throwable cause) {
        super(message, cause);
    }
    public BolestIstihSimptoma(Throwable cause) {
        super(cause);
    }
}
