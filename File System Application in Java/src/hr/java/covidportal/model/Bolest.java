package hr.java.covidportal.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Predstavlja imenovani entitet bolesti kojega definiraju naziv i simptomi.
 */

public class Bolest extends ImenovaniEntitet implements Serializable {

    private List<Simptom> simptomi;

    /**
     * Inicijalizira podatak o nazivu i simptomima.
     * @param id
     * @param naziv
     * @param simptomi
     */
    public Bolest(Long id, String naziv, List<Simptom> simptomi) {
        super(id, naziv);
        this.simptomi = simptomi;
    }

    public List<Simptom> getSimptomi() {
        return simptomi;
    }

    public void setSimptomi(List<Simptom> simptomi) {
        this.simptomi = simptomi;
    }

    public boolean jeLiVirus() {
        if(this instanceof Virus){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bolest bolest = (Bolest) o;
        return Objects.equals(simptomi, bolest.simptomi);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(simptomi);
    }

}
