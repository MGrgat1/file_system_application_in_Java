package hr.java.covidportal.sort;

import hr.java.covidportal.model.Zupanija;

import java.util.Comparator;
import java.util.List;

/**
 * Uspoređuje dvije županije na temelju njihova postotka zaraženih osoba
 */
public class CovidSorter implements Comparator<Zupanija> {

    @Override
    public int compare(Zupanija zupanija1, Zupanija zupanija2) {

        Double postotakZarazenih1 = 100 * zupanija1.getBrojZarazenih().doubleValue()/zupanija1.getBrojStanovnika().doubleValue();
        Double postotakZarazenih2 = 100 * zupanija2.getBrojZarazenih().doubleValue()/zupanija2.getBrojStanovnika().doubleValue();

        if(postotakZarazenih1 > postotakZarazenih2)
            return 1;
        else if(postotakZarazenih1 < postotakZarazenih2)
            return -1;
        else
            return 0;

    }


}
