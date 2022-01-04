package hr.java.covidportal.model;

/**
 * Predstavlja entitet koji posjeduje naziv.
 */
public abstract class ImenovaniEntitet {

    private String naziv;
    private Long id;

    /**
     * Inicijalizira podatak o nazivu entiteta.
     * @param id
     * @param naziv
     */
    public ImenovaniEntitet(Long id, String naziv) {
        this.naziv = naziv;
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return naziv;
    }
}
