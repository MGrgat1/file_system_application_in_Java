package hr.java.covidportal.main;


import hr.java.covidportal.enumeracije.Vrijednost;
import hr.java.covidportal.genericsi.KlinikaZaInfektivneBolesti;
import hr.java.covidportal.iznimke.BolestIstihSimptoma;
import hr.java.covidportal.iznimke.DuplikatKontaktiraneOsobe;
import hr.java.covidportal.model.*;
import hr.java.covidportal.sort.CovidSorter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Služi za obradu podataka o županijama, simptomima bolesti, bolestima i osobama
 */
public class Glavna {

    private static final Logger logger = LoggerFactory.getLogger(Glavna.class);

    /**
     * Služi za pokretanje programa koji će obrađivati podatke o županijama, simptomima, bolestima i osobama.
     * @param args argumenti komandne linije (ne koriste se)
     */
    public static void main(String[] args) {

        logger.info("Pokrenuli smo aplikaciju.");

        Scanner tipkovnica = new Scanner(System.in);

        Set<Zupanija> zupanije = ucitajZupanije();
        Set<Simptom> simptomi = ucitajSimptome();
        Set<Bolest> bolesti = ucitajBolesti(simptomi, "dat/bolesti.txt");
        Set<Virus> virusi = ucitajViruse(simptomi);
        List<Osoba> osobe = ucitajOsobe(zupanije, bolesti, virusi);

        logger.info("Učitani su svi podatci.");


        List<Zupanija> zupanijeZaSerijalizaciju = zupanije.stream()
                .filter(zupanija -> zupanija.izracunajPostotakZarazenih() > 0.05)
                .collect(Collectors.toList());
        serijalizirajListu(zupanijeZaSerijalizaciju, "dat/serijalizirani.dat");

        logger.info("Lista županija je serijalizirana.");

        ispisiOsobe(osobe);

        logger.info("Ispisane su osobe.");

        Map<Bolest, List<Osoba>> mapaVirusaIOsoba = izradiMapuVirusaIOsoba(virusi, osobe);
        ispisMapeVirusaIOsoba(virusi, mapaVirusaIOsoba);

        logger.info("Ispisana je mapa.");

        TreeSet<Zupanija> sortedZupanije = izradiSortiraneZupanije(zupanije);
        ispisZupanijeSNajvecimPostotkom(sortedZupanije);

        logger.info("Ispisane su županije sortirane po postotcima.");

        KlinikaZaInfektivneBolesti<Virus, Osoba> klinika = generirajKliniku(virusi, osobe);

        logger.info("Generirana je klinika.");

        sortiranjeSaIBezLambde(klinika);

        filtriranjeOsobaPoPrezimenu(tipkovnica, osobe);

        ispisBrojaSimptoma(bolesti);
        ispisBrojaSimptoma(virusi);

        //1. zdt
        List<Bolest> bolestiZaSerijalizaciju = bolesti.stream()
                .filter(bolest -> bolest.getNaziv().toUpperCase().startsWith("LJ") || bolest.getNaziv().toUpperCase().contains("GITIS"))
                .collect(Collectors.toList());
        serijalizirajListu(bolestiZaSerijalizaciju, "dat/serijalizirane_bolesti.txt");

        try (FileReader fileReader = new FileReader("dat/serijalizirane_bolesti.txt");
             BufferedReader reader = new BufferedReader((fileReader))){
            String procitanaLinija;

            System.out.println("Bolesti u datoteci:");
            while((procitanaLinija = reader.readLine())!= null) {
                String naziv = procitanaLinija;
                System.out.println(naziv);
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //2. zdt
        Bolest najveca = bolesti.stream()
                .max(Comparator.comparingInt((Bolest b) -> b.getNaziv().length()))
                .orElse(null);
        Bolest najmanja = bolesti.stream()
                .min(Comparator.comparingInt((Bolest b) -> b.getNaziv().length()))
                .orElse(null);
        Integer sumaDuljine = bolesti.stream()
                .map(bolest -> bolest.getNaziv().length())
                .mapToInt(Integer::intValue)
                .sum();
        Double prosjekDuljine = bolesti.stream()
                .map(bolest -> bolest.getNaziv().length())
                .mapToInt(Integer::intValue)
                .average().orElse(0);
        try (PrintWriter output = new PrintWriter(new FileWriter(new File("dat/max,min,suma,prosjek.txt")))){
            output.println("Najdulji naziv je " + najveca);
            output.println("Najkraći naziv je " + najmanja);
            output.println("Suma je " + sumaDuljine);
            output.println("Prosjek je " + prosjekDuljine);
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("Završetak programa.");

    }


    private static <T> void serijalizirajListu(List<T> listaZaSerijalizaciju, String pathname) {


        if(pathname.endsWith(".dat")) {
            try (ObjectOutputStream serializator = new ObjectOutputStream(
                    new FileOutputStream(pathname))) {

                serializator.writeObject(listaZaSerijalizaciju);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(pathname.endsWith(".txt")){
            try (PrintWriter output = new PrintWriter(new FileWriter(new File(pathname)))){
                listaZaSerijalizaciju.stream().forEach(output::println);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private static List<Osoba> ucitajOsobe(Set<Zupanija> zupanije, Set<Bolest> bolesti, Set<Virus> virusi) {
        List<Osoba> osobe = new ArrayList<>();
        System.out.println("Učitavanje osoba...");
        File file = new File("dat/osobe.txt");
        try (FileReader fileReader = new FileReader(file);
             BufferedReader reader = new BufferedReader((fileReader))){
            String procitanaLinija;

            while((procitanaLinija = reader.readLine())!= null){
                String ime = procitanaLinija;

                procitanaLinija = reader.readLine();
                String prezime = procitanaLinija;

                procitanaLinija = reader.readLine();
                Integer starost = Integer.parseInt(procitanaLinija);

                procitanaLinija = reader.readLine();

                Zupanija ucitanaZupanija = izvuciImenovaniEntitetPoNazivu(zupanije, procitanaLinija);

                procitanaLinija = reader.readLine();
                Bolest ucitanaBolest = izvuciImenovaniEntitetPoNazivu(bolesti, procitanaLinija);
                if(ucitanaBolest == null)
                    ucitanaBolest = izvuciImenovaniEntitetPoNazivu(virusi, procitanaLinija);

                procitanaLinija = reader.readLine();
                List<Osoba> kontaktiraneOsobe = izvuciKontaktiraneOsobeIzStringa(osobe, procitanaLinija);

                osobe.add(new Osoba.Builder(ime).withPrezime(prezime)
                        .withStarost(starost).atZupanija(ucitanaZupanija)
                        .withIllness(ucitanaBolest).withContacts(kontaktiraneOsobe).build());
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return osobe;
    }

    private static <T extends ImenovaniEntitet> T izvuciImenovaniEntitetPoNazivu(Set<T> entiteti, String naziv) {
        T ucitaniEntitet = entiteti.stream()
                .filter(entitet -> entitet.getNaziv().equals(naziv))
                .findAny()
                .orElse(null);
        return ucitaniEntitet;
    }

    /**
     * Na temelju popisa kontaktiranih osoba navedenog u datoteci stvara listu kontaktiranih osoba u sustavu.
     * @param osobe
     * @param procitanaLinija
     * @return
     */
    private static List<Osoba> izvuciKontaktiraneOsobeIzStringa(List<Osoba> osobe, String procitanaLinija) {
        String[] imenaIPrezimenaKontaktiranihOsoba = procitanaLinija.split(",");

        List<Osoba> kontaktiraneOsobe = new ArrayList<>();
        for (Osoba osoba : osobe) {
            String imeIPrezimeOsobe = osoba.getIme() + " " + osoba.getPrezime();
            if (Arrays.asList(imenaIPrezimenaKontaktiranihOsoba).contains(imeIPrezimeOsobe)) {
                kontaktiraneOsobe.add(osoba);
            }
        }

        return kontaktiraneOsobe;
    }

    /**
     * Učitava viruse iz datoteke "virusi.txt".
     * @param simptomiUSustavu Simptomi uneseni u sustav
     * @return
     */
    private static Set<Virus> ucitajViruse(Set<Simptom> simptomiUSustavu) {
        Set<Virus> virusi = new HashSet<>();
        System.out.println("Učitavanje podataka o virusima...");
        File file = new File("dat/virusi.txt");
        try (FileReader fileReader = new FileReader(file);
             BufferedReader reader = new BufferedReader((fileReader))){
            String procitanaLinija;

            while((procitanaLinija = reader.readLine())!= null){
                Long id = Long.parseLong(procitanaLinija);

                procitanaLinija = reader.readLine();
                String naziv = procitanaLinija;

                procitanaLinija = reader.readLine();
                List<Simptom> simptomiNovogVirusa = izvuciSimptomeIzStringa(simptomiUSustavu, procitanaLinija);

                virusi.add(new Virus(id, naziv, simptomiNovogVirusa));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return virusi;
    }

    /**
     * Na temelju stringa oblika "2,3,4" (brojevi odijeljeni zarezima) stvara listu simptoma.
     * Svaki broj u stringu je id jednog simptoma u sustavu.
     * @param simptomiUSustavu
     * @param procitanaLinija
     * @return
     */
    private static List<Simptom> izvuciSimptomeIzStringa(Set<Simptom> simptomiUSustavu, String procitanaLinija) {
        String[] sifreSimptomaString = procitanaLinija.split(",");

        List<Long> sifreSimptomaLong = Arrays.stream(sifreSimptomaString)
                .map(Long::valueOf)
                .collect(Collectors.toList());

        /* isto, ali bez lambdi
        List<Long> sifreSimptomaLong = new ArrayList<>();
        for (String sifraSimptoma : sifreSimptomaString) {
            sifreSimptomaLong.add(Long.parseLong(sifraSimptoma));
        }
         */


        List<Simptom> simptomiNovogVirusa = new ArrayList<>();
        for (Simptom simptom : simptomiUSustavu) {
            if (sifreSimptomaLong.contains(simptom.getId())) {
                simptomiNovogVirusa.add(simptom);
            }
        }
        return simptomiNovogVirusa;
    }

    /**
     * Učitava bolesti iz datoteke "bolesti.txt".
     * @param simptomiUSustavu Simptomi uneseni u sustav
     * @return
     */
    private static Set<Bolest> ucitajBolesti(Set<Simptom> simptomiUSustavu, String pathname) {
        Set<Bolest> bolesti = new HashSet<>();
        System.out.println("Učitavanje podataka o bolestima...");
        File file = new File(pathname);
        try (FileReader fileReader = new FileReader(file);
             BufferedReader reader = new BufferedReader((fileReader))){
            String procitanaLinija;

            while((procitanaLinija = reader.readLine())!= null){
                Long id = Long.parseLong(procitanaLinija);

                procitanaLinija = reader.readLine();
                String naziv = procitanaLinija;

                procitanaLinija = reader.readLine();
                List<Simptom> simptomiNoveBolesti = izvuciSimptomeIzStringa(simptomiUSustavu, procitanaLinija);


                bolesti.add(new Bolest(id, naziv, simptomiNoveBolesti));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bolesti;
    }

    /**
     * Ucitava simptome iz datoteke "simptomi.txt"
     * @return
     */
    private static Set<Simptom> ucitajSimptome() {
        Set<Simptom> simptomi = new HashSet<>();
        System.out.println("Učitavanje podataka o simptomima...");
        File simptomiFile = new File("dat/simptomi.txt");
        try (FileReader fileReader = new FileReader(simptomiFile);
             BufferedReader reader = new BufferedReader((fileReader))){
            String procitanaLinija;

            while((procitanaLinija = reader.readLine())!= null){
                Long id = Long.parseLong(procitanaLinija);

                procitanaLinija = reader.readLine();
                String naziv = procitanaLinija;

                procitanaLinija = reader.readLine();
                String vrijednostString = procitanaLinija;
                Vrijednost vrijednost = Vrijednost.RIJETKO;
                switch(vrijednostString){
                    case "RIJETKO":
                        vrijednost = Vrijednost.RIJETKO;
                        break;
                    case "SREDNJE":
                        vrijednost = Vrijednost.SREDNJE;
                        break;
                    case "ČESTO":
                        vrijednost = Vrijednost.ČESTO;
                        break;
                    default:
                        throw new IllegalStateException("Unesena je neočekivana vrijednost: " + vrijednost);
                }


                simptomi.add(new Simptom(id, naziv, vrijednost));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return simptomi;
    }


    /**
     * Ucitava zupanije iz datoteke "zupanije.txt"
     * @return
     */
    private static Set<Zupanija> ucitajZupanije() {
        Set<Zupanija> zupanije = new HashSet<>();
        System.out.println("Učitavanje podataka o županijama...");
        File file = new File("dat/zupanije.txt");
        try (FileReader fileReader = new FileReader(file);
             BufferedReader reader = new BufferedReader((fileReader))){
            String procitanaLinija;

            while((procitanaLinija = reader.readLine())!= null){
                Long id = Long.parseLong(procitanaLinija);

                procitanaLinija = reader.readLine();
                String naziv = procitanaLinija;

                procitanaLinija = reader.readLine();
                Integer brojStanovnika = Integer.parseInt(procitanaLinija);

                procitanaLinija = reader.readLine();
                Integer brojZarazenih = Integer.parseInt(procitanaLinija);

                zupanije.add(new Zupanija(id, naziv, brojStanovnika, brojZarazenih));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return zupanije;
    }

    /**
     * Iz bolesti unesenih u sustav filtrira viruse, iz osoba unesenih u sustav filtrira zaražene osobe,
     * te ih sve stavlja u kliniku.
     * @param bolesti Bolesti unesene u sustav (mogu biti virusi ili obične bolesti)
     * @param osobe Osobe unesene u sustav (mogu biti zaražene virusom ili zaražene običnom bolesti)
     * @return
     */
    private static KlinikaZaInfektivneBolesti<Virus, Osoba> generirajKliniku(Set<Virus> bolesti, List<Osoba> osobe) {
        List<Virus> uneseniVirusi = bolesti.stream()
                .filter(bolest -> bolest instanceof Virus)
                .map(bolest -> (Virus) bolest)
                .collect(Collectors.toList());
        List<Osoba> osobeZarazeneVirusima = osobe.stream()
                .filter(Osoba::jeLiZarazenaVirusom)
                .collect(Collectors.toList());

        KlinikaZaInfektivneBolesti<Virus, Osoba> klinika = new KlinikaZaInfektivneBolesti<Virus, Osoba>(uneseniVirusi, osobeZarazeneVirusima);
        return klinika;
    }

    /**
     * Ispisuje koliko svaka bolest ima simptoma.
     * @param bolesti
     */
    private static <T extends Bolest> void ispisBrojaSimptoma(Set<T> bolesti) {
        bolesti.stream().
                map(bolest -> bolest + " ima " + bolest.getSimptomi().size() + " simptoma.").
                forEach(System.out::println);
    }

    /**
     * Od korisnika traži da unese string, i onda ispisuje one osobe čije prezime sadrži taj string.
     * @param tipkovnica
     * @param osobe
     */
    private static void filtriranjeOsobaPoPrezimenu(Scanner tipkovnica, List<Osoba> osobe) {
        System.out.print("Unesite string za pretragu po prezimenu:");
        String stringZaPretragu = tipkovnica.nextLine();

        Optional<List> filtriraneOsobe = filtrirajOsobe(osobe, stringZaPretragu);
        if (filtriraneOsobe.isPresent()) {
            System.out.println("Osobe čije prezime sadrži " + stringZaPretragu + " su sljedeće: ");
            filtriraneOsobe.get().forEach(System.out::println);
        } else {
            System.out.println("Nema osoba čije prezime sadrži " + stringZaPretragu);
        }
    }

    /**
     * Sortira viruse u klinici jednom koristeći lambda funkcije i jednom bez lambda funkcija.
     * @param klinika
     */
    private static void sortiranjeSaIBezLambde(KlinikaZaInfektivneBolesti<Virus, Osoba> klinika) {
        Comparator<Virus> usporediVirusSilaznoPoNazivu = (Virus v1, Virus v2)->v2.getNaziv().compareTo(v1.getNaziv());

        System.out.println("Virusi sortirani po nazivu suprotno od poretka abecede:");

        List<Virus> virusiZaSortiranje = klinika.getUneseniVirusi();
        Instant lambdaStart = Instant.now();
        List<Virus> sortiraniVirusi = virusiZaSortiranje.stream()
                .sorted(usporediVirusSilaznoPoNazivu).collect(Collectors.toList());
        Instant lambdaStop = Instant.now();
        Duration lambdaDuration = Duration.between(lambdaStart, lambdaStop);

        for(Virus virus: sortiraniVirusi){
            System.out.println(virus);
        }

        Instant noLambdaStart = Instant.now();
        virusiZaSortiranje.sort(usporediVirusSilaznoPoNazivu);
        Instant noLambdaStop = Instant.now();
        Duration noLambdaDuration = Duration.between(noLambdaStart, noLambdaStop);


        System.out.println("Sortiranje objekata korištenjem lambdi " +
                "traje " + lambdaDuration.toMillis() + " milisekundi, a bez lambda " +
                "traje " + noLambdaDuration.toMillis() + " milisekundi.");
    }


    private static Optional<List> filtrirajOsobe(List<Osoba> osobe, String stringZaPretragu) {
        List<Osoba> filtriraneOsobe = osobe.stream()
                .filter(osoba -> osoba.getPrezime().contains(stringZaPretragu))
                .collect(Collectors.toList());
        return Optional.of(filtriraneOsobe);

    }


    /**
     * Ispisuje županiju sa najvećim postotkom zaraženih osoba
     * @param sortedZupanije Županije sortirane po postotku zaraženih osoba, od najmanje do najveće.
     */
    private static void ispisZupanijeSNajvecimPostotkom(TreeSet<Zupanija> sortedZupanije) {
        System.out.print("Najviše zaraženih osoba ima u županiji ");
        Zupanija maxZupanija = sortedZupanije.last();
        Double postotakZarazenih = 100 * maxZupanija.izracunajPostotakZarazenih();
        System.out.print(maxZupanija + " ");
        System.out.printf("%.2f", postotakZarazenih);
        System.out.println("%");
    }

    /**
     * Stvara sortirani skup županija na temelju kriterija zadanog u objektu CovidSorter.
     * @param zupanije Nesortirani skup županija
     * @return Vraća sortiran popis županija.
     */
    private static TreeSet<Zupanija> izradiSortiraneZupanije(Set<Zupanija> zupanije) {
        CovidSorter covidSorter = new CovidSorter();

        TreeSet<Zupanija> sortedZupanije = new TreeSet(covidSorter);
        sortedZupanije.addAll(zupanije);
        return sortedZupanije;
    }

    /**
     * Ispisuje liste osoba koje boluju od svake pojedine bolesti.
     * @param bolesti
     * @param mapaVirusaIOsoba
     */
    private static void ispisMapeVirusaIOsoba(Set<Virus> bolesti, Map<Bolest, List<Osoba>> mapaVirusaIOsoba) {
        for(Bolest bolest: bolesti){
            if(mapaVirusaIOsoba.containsKey(bolest)) {
                System.out.print("Od virusa " + bolest.getNaziv() + " boluju: ");
                ispisiPopisOsoba(mapaVirusaIOsoba.get(bolest));
            }
        }
    }

    /**
     * Stvara mapu u kojoj se svakoj bolesti pridružuje popis osoba koje boluju od te bolesti.
     * @param bolesti Sve bolesti u sustavu
     * @param osobe Sve osobe u sustavu
     * @return Vraća izrađenu mapu bolesti i osoba
     */
    private static Map<Bolest, List<Osoba>> izradiMapuVirusaIOsoba(Set<Virus> bolesti, List<Osoba> osobe) {
        Map<Bolest, List<Osoba>> mapaVirusaIOsoba = new HashMap<>();

        for(Bolest bolestKojaSeMapira: bolesti) {
            List<Osoba> osobeOboljeleOdOveBolesti = new ArrayList<>();
            for (Osoba osoba : osobe) {
                if(osoba.getZarazenBolescu().equals(bolestKojaSeMapira))
                    osobeOboljeleOdOveBolesti.add(osoba);
            }
            mapaVirusaIOsoba.put(bolestKojaSeMapira, osobeOboljeleOdOveBolesti);
        }
        return mapaVirusaIOsoba;
    }

    private static void ispisiPopisOsoba(List<Osoba> listaOsobaZaIspis) {
        for(int i = 0; i < listaOsobaZaIspis.size(); i++){
            Osoba osoba = listaOsobaZaIspis.get(i);
            System.out.print(osoba.getIme() + " " + osoba.getPrezime());
            if(i < listaOsobaZaIspis.size() - 1)
                System.out.print(", ");
        }
        System.out.println("");
    }

    /**
     * Ispisuje popis osoba sa svim njihovim podatcima.
     * @param osobe Sve osobe unesene u sustav
     */
    private static void ispisiOsobe(List<Osoba> osobe) {
        System.out.println("Popis osoba:");
        for(int i = 0; i < osobe.size(); i++){
            System.out.println("Ime i prezime: " + osobe.get(i).getIme() + " " + osobe.get(i).getPrezime());
            System.out.println("Starost: " + osobe.get(i).getStarost());
            System.out.println("Županija prebivališta: " + osobe.get(i).getZupanija());
            System.out.println("Zaražen bolešću: " + osobe.get(i).getZarazenBolescu());
            System.out.println("Kontaktirane osobe:");

            if(osobe.get(i).getKontaktiraneOsobe().size() == 0) {
                System.out.println("Nema kontaktiranih osoba.");
            }
            else {
                for (int j = 0; j < osobe.get(i).getKontaktiraneOsobe().size(); j++) {
                    System.out.println(osobe.get(i).getKontaktiraneOsobe().get(j).getIme() + " " + osobe.get(i).getKontaktiraneOsobe().get(j).getPrezime());
                }
            }

        }
    }

    /**
     * Provjerava je li zadana osoba već navedena kao kontakt. U slučaju da je, baca iznimku <code>DuplikatKontaktiraneOsobe</code>
     * @param odabranaOsoba Osoba koju je korisnik odabrao da bude kontaktirana osoba.
     * @param kontaktiraneOsobe Osobe koje je korisnik već odabrao da budu kontaktirane osobe.
     * @throws DuplikatKontaktiraneOsobe Iznimka koja se baca u slučaju da se navedena osoba drugi put odabire kao kontakt.
     */
    private static void provjeriJeLiOsobaDuplikat(Osoba odabranaOsoba, List<Osoba> kontaktiraneOsobe) throws DuplikatKontaktiraneOsobe{

        boolean osobaJeDuplikat = false;

        if(kontaktiraneOsobe.size() == 0)
            osobaJeDuplikat = false;

        for(Osoba kontaktiranaOsoba: kontaktiraneOsobe) {
            if (odabranaOsoba.equals(kontaktiranaOsoba)) {
                osobaJeDuplikat = true;
            } else
                osobaJeDuplikat = false;
        }

        if(osobaJeDuplikat) {
            throw new DuplikatKontaktiraneOsobe("Odabrana osoba se već nalazi među kontaktiranim osobama. Molimo Vas da odaberete neku drugu osobu.");
        }
    }

    /**
     * Prolazi kroz sve unesene bolesti i provjerava postoji li jedna sa istim simptomima kao zadana bolest.
     * @param unesenaBolest Zadana bolest za koju se treba naći duplikat
     * @param bolestiUSustavu Bolesti već unesene u sustav
     * @return Vraća vrijednost true ako zadana bolest ima iste simptome kao jedna bolest iz skupa.
     */
    private static void provjeriJeLiBolestDuplikat(Bolest unesenaBolest, Set<Bolest> bolestiUSustavu) {

        boolean pronadenDuplikat = false;

        for (Bolest bolest : bolestiUSustavu) {
            if (unesenaBolest.equals(bolest))
                pronadenDuplikat = true;
        }

        if(pronadenDuplikat){
            throw new BolestIstihSimptoma("Pogrešan unos, već ste unijeli bolest ili virus s istim simptomima. Molimo ponovite unos.");
        }
    }




}
