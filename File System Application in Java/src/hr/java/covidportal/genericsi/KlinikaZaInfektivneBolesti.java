package hr.java.covidportal.genericsi;

import hr.java.covidportal.model.Osoba;
import hr.java.covidportal.model.Virus;

import java.util.List;
import java.util.Set;

public class KlinikaZaInfektivneBolesti<T extends Virus, S extends Osoba> {
    List<T> uneseniVirusi;
    List<S> osobeZarazeneVirusima;

    public KlinikaZaInfektivneBolesti(List<T> uneseniVirusi, List<S> osobeZarazeneVirusima) {
        this.uneseniVirusi = uneseniVirusi;
        this.osobeZarazeneVirusima = osobeZarazeneVirusima;
    }

    public List<T> getUneseniVirusi() {
        return uneseniVirusi;
    }

    public void setUneseniVirusi(List<T> uneseniVirusi) {
        this.uneseniVirusi = uneseniVirusi;
    }

    public List<S> getOsobeZarazeneVirusima() {
        return osobeZarazeneVirusima;
    }

    public void setOsobeZarazeneVirusima(List<S> osobeZarazeneVirusima) {
        this.osobeZarazeneVirusima = osobeZarazeneVirusima;
    }

    public void dodajVirus(T virus){
        uneseniVirusi.add(virus);

    }

    public void dodajOsobuZarazenuVirusom(S osoba){
        osobeZarazeneVirusima.add(osoba);
    }

}