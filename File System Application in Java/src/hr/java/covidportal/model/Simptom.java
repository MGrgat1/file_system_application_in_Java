package hr.java.covidportal.model;

import hr.java.covidportal.enumeracije.Vrijednost;

import java.io.Serializable;
import java.util.Objects;

/**
 * Predstavlja imenovani entitet kojeg definiraju naziv i vrijednost.
 */
public class Simptom extends ImenovaniEntitet implements Serializable {

    private Vrijednost vrijednost;

    public Simptom(Long id, String naziv, Vrijednost vrijednost) {
        super(id, naziv);
        this.vrijednost = vrijednost;
    }

    public Vrijednost getVrijednost() {
        return vrijednost;
    }

    public void setVrijednost(Vrijednost vrijednost) {
        this.vrijednost = vrijednost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Simptom simptom = (Simptom) o;
        return vrijednost == simptom.vrijednost;
    }

    @Override
    public int hashCode() {
        return Objects.hash(vrijednost);
    }

    @Override
    public String toString() {
        return getNaziv() + " " + vrijednost;
    }
}
