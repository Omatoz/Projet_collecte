import java.util.*;

public class Prim {

    public static class Arete {   //class static avec info d'une arete
        int sommetDepart;
        int sommetArrivee;
        int poids;

        public Arete(int sommetDepart, int sommetArrivee, int poids) {
            this.sommetDepart = sommetDepart;
            this.sommetArrivee = sommetArrivee;
            this.poids = poids;
        }
    }

    public static List<Arete> arbreCouvrantMinimal (List<Arete> aretes, int nbSommets){
        boolean[] sommetGPrime = new boolean[nbSommets];  //savoir si sommets sont déjà dans sous graphe G'
        List<Arete> listeAretes = new ArrayList<>();   //aretes de G'
        sommetGPrime[0] = true;

        while(listeAretes.size() < nbSommets-1){        //on s'arrete à n-1 aretes
            Arete aretePoidsMin = null;
            for (Arete a : aretes){
                boolean extremiteInitiale = sommetGPrime[a.sommetDepart];
                boolean extremiteFinale = sommetGPrime[a.sommetArrivee];

                if((extremiteInitiale && !extremiteFinale)||(!extremiteInitiale && extremiteFinale)){   //arete G' relier à sommet exterieur
                    if(aretePoidsMin == null || a.poids < aretePoidsMin.poids){  //on prend l'arete de poids minimal
                        aretePoidsMin = a;
                    }
                }
            }
            if (aretePoidsMin != null){
                listeAretes.add(aretePoidsMin);
                sommetGPrime[aretePoidsMin.sommetDepart] = true;
                sommetGPrime[aretePoidsMin.sommetArrivee] = true;
            }
        }
        return listeAretes;
    }
}
