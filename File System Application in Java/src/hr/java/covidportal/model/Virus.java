package hr.java.covidportal.model;

import java.io.Serializable;
import java.util.List;

/**
 * Predstavlja entitet koji može stupiti u interakciju s entitetom osobe i zaraziti ju.
 */
public class Virus extends Bolest implements Zarazno, Serializable {

    /**
     * Inicijalizira podatke o nazivu i simptomima
     * @param id
     * @param naziv
     * @param simptomi
     */
    public Virus(Long id, String naziv, List<Simptom> simptomi) {
        super(id, naziv, simptomi);
    }

    @Override
    public void prelazakZarazeNaOsobu(Osoba osoba) {
        osoba.setZarazenBolescu(this);
    }

}
