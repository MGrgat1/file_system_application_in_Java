package hr.java.covidportal.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Predstavlja entitet za kojega je definirano ime, prezime i starost,
 * županija u kojoj prebiva, bolest kojom je zaražen,
 * i skup osoba s kojima je bio u kontaktu.
 */
public class Osoba implements Serializable {

    /**
     * Iniijalizira podatkovne članove entiteta koristeći se builder patternom.
     */
    public static class Builder {
        private String ime;
        private String prezime;
        private Integer starost;
        private Zupanija zupanija;
        private Bolest zarazenBolescu;
        private List<Osoba> kontaktiraneOsobe;

        public Builder(String ime){
            this.ime = ime;
        }

        public Builder withPrezime(String prezime){
            this.prezime = prezime;
            return this;
        }

        public Builder withStarost(Integer starost){
            this.starost = starost;
            return this;
        }

        public Builder atZupanija(Zupanija zupanija){
            this.zupanija = zupanija;
            return this;
        }

        public Builder withIllness(Bolest zarazenBolescu) {
            this.zarazenBolescu = zarazenBolescu;
            return this;
        }

        public Builder withContacts(List<Osoba> kontaktiraneOsobe){
            this.kontaktiraneOsobe = kontaktiraneOsobe;
            return this;
        }

        public Osoba build(){
            Osoba osoba = new Osoba();
            osoba.ime = this.ime;
            osoba.prezime = this.prezime;
            osoba.starost = this.starost;
            osoba.zupanija = this.zupanija;
            osoba.zarazenBolescu = this.zarazenBolescu;
            osoba.kontaktiraneOsobe = this.kontaktiraneOsobe;

            if(this.zarazenBolescu instanceof Virus virus) {
                for (int i = 0; i < this.kontaktiraneOsobe.size(); i++) {
                    virus.prelazakZarazeNaOsobu(this.kontaktiraneOsobe.get(i));
                }
            }

            return osoba;
        }


    }

    private String ime;
    private String prezime;
    private Integer starost;
    private Zupanija zupanija;
    private Bolest zarazenBolescu;
    private List<Osoba> kontaktiraneOsobe;

    private Osoba(){
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public Integer getStarost() {
        return starost;
    }

    public void setStarost(Integer starost) {
        this.starost = starost;
    }

    public Zupanija getZupanija() {
        return zupanija;
    }

    public void setZupanija(Zupanija zupanija) {
        this.zupanija = zupanija;
    }

    public Bolest getZarazenBolescu() {
        return zarazenBolescu;
    }

    public void setZarazenBolescu(Bolest zarazenBolescu) {
        this.zarazenBolescu = zarazenBolescu;
    }

    public List<Osoba> getKontaktiraneOsobe() {
        return kontaktiraneOsobe;
    }

    public void setKontaktiraneOsobe(List<Osoba> kontaktiraneOsobe) {
        this.kontaktiraneOsobe = kontaktiraneOsobe;
    }

    public boolean jeLiZarazenaVirusom() {
        if(zarazenBolescu instanceof Virus){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Osoba osoba = (Osoba) o;
        return Objects.equals(ime, osoba.ime) &&
                Objects.equals(prezime, osoba.prezime) &&
                Objects.equals(starost, osoba.starost) &&
                Objects.equals(zupanija, osoba.zupanija) &&
                Objects.equals(zarazenBolescu, osoba.zarazenBolescu) &&
                Objects.equals(kontaktiraneOsobe, osoba.kontaktiraneOsobe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ime, prezime, starost, zupanija, zarazenBolescu, kontaktiraneOsobe);
    }

    @Override
    public String toString() {
        return ime + " " + prezime;
    }
}