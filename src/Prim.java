import java.util.*;

public class Prim {

    public static List<Arete> arbreCouvrantMinimal(List<Sommet> sommets) {
        Set<Sommet> gPrime = new HashSet<>();
        List<Arete> listeAretes = new ArrayList<>();
        Sommet depart = sommets.get(0);
        gPrime.add(depart);

        while (gPrime.size() < sommets.size()) {
            Arete aretePoidsMin = null;

            // trier G' pour parcours stable
            List<Sommet> gPrimeTrie = new ArrayList<>(gPrime);
            gPrimeTrie.sort(Comparator.comparing(s -> s.id));

            for (Sommet s : gPrimeTrie) {
                // trier les arêtes pour parcours stable
                List<Arete> aretesTriees = new ArrayList<>(s.aretes);
                aretesTriees.sort(Comparator.comparingInt((Arete a) -> a.poids)
                        .thenComparing(a -> a.destination.id));

                for (Arete a : aretesTriees) {
                    Sommet u = a.depart;
                    Sommet v = a.destination;
                    boolean uIn = gPrime.contains(u);
                    boolean vIn = gPrime.contains(v);

                    if (uIn ^ vIn) {
                        if (aretePoidsMin == null || a.poids < aretePoidsMin.poids ||
                                (a.poids == aretePoidsMin.poids &&
                                        a.destination.id.compareTo(aretePoidsMin.destination.id) < 0)) {
                            aretePoidsMin = a;
                        }
                    }
                }
            }

            if (aretePoidsMin == null) throw new RuntimeException("Graphe non connexe !");

            listeAretes.add(aretePoidsMin);

            Sommet u = aretePoidsMin.depart;
            Sommet v = aretePoidsMin.destination;
            if (!gPrime.contains(u)) gPrime.add(u);
            if (!gPrime.contains(v)) gPrime.add(v);
        }

        return listeAretes;
    }
}
    /*
    public static List<Arete> arbreCouvrantMinimal (List<Sommet> sommets){
        Set<Sommet> gPrime = new HashSet(); //savoir si sommets sont déjà dans sous graphe G'
        List<Arete> listeAretes = new ArrayList<>();   //aretes de G'
        Sommet depart = sommets.get(0);
        gPrime.add(depart);

        while(gPrime.size() < sommets.size()){ //on s'arrete à n-1 aretes
            Arete aretePoidsMin = null;
            for (Sommet s : gPrime){
                for (Arete a : s.aretes){
                    Sommet initial = a.depart;
                    Sommet fin = a.destination;

                    boolean gPrimeInitial = gPrime.contains(initial);
                    boolean gPrimeDestination = gPrime.contains(fin);

                    if(gPrimeInitial&&!gPrimeDestination || !gPrimeInitial&&gPrimeDestination){
                        if(aretePoidsMin==null){
                            aretePoidsMin = a;
                        }
                    }

                }

            }

            listeAretes.add(aretePoidsMin);
            Sommet initial = aretePoidsMin.depart;
            Sommet fin = aretePoidsMin.destination;

            if(!gPrime.contains(initial)){
                gPrime.add(initial);
            }

            if(!gPrime.contains(fin)){
                gPrime.add(fin);
            }

        }
        return listeAretes;
    }*/

