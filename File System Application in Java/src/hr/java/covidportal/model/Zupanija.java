package hr.java.covidportal.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Predstavlja entitet kojeg definiraju naziv i broj stanovnika
 */
public class Zupanija extends ImenovaniEntitet implements Serializable {

    private Integer brojStanovnika;

    private Integer brojZarazenih;

    public Zupanija(Long id, String naziv, Integer brojStanovnika, Integer brojZarazenih) {
        super(id, naziv);
        this.brojStanovnika = brojStanovnika;
        this.brojZarazenih = brojZarazenih;
    }

    public Integer getBrojStanovnika() {
        return brojStanovnika;
    }

    public void setBrojStanovnika(Integer brojStanovnika) {
        this.brojStanovnika = brojStanovnika;
    }

    public Integer getBrojZarazenih() {
        return brojZarazenih;
    }

    public void setBrojZarazenih(Integer brojZarazenih) {
        this.brojZarazenih = brojZarazenih;
    }

    public double izracunajPostotakZarazenih(){
        return brojZarazenih.doubleValue()/ brojStanovnika.doubleValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Zupanija zupanija = (Zupanija) o;
        return Objects.equals(brojStanovnika, zupanija.brojStanovnika) &&
                Objects.equals(brojZarazenih, zupanija.brojZarazenih);
    }

    @Override
    public int hashCode() {
        return Objects.hash(brojStanovnika, brojZarazenih);
    }
}
