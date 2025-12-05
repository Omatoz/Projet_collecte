import java.util.*;

public class WPBis {
    private Graphe g;
    private ArrayList<Sommet> SommetsTries;
    private Map<Sommet, Integer> couleur ;
    private Map<Sommet, Boolean> colore ;
    private Map<Integer, Integer> capaciteJour ;
    private int nbJour;
    private int capaciteCamion;

    public WPBis() {
        this.g = g;
        this.SommetsTries = new ArrayList<>();
        this.couleur = new HashMap<>();
        this.colore = new HashMap<>();
        this.capaciteJour = new HashMap<>();
        this.nbJour = 0;
        this.capaciteCamion = capaciteCamion;
    }
    public int getNbJour() {
        return nbJour;
    }

    private boolean SommetsNonColores(){
        for (Boolean b : colore.values()){
            if (!b){ return true;
            }
        }
        return false;
    }

    public void coloration (Map<Sommet, Integer> quantites){
        SommetsTries.clear(); // pour s'assurer qu'il n'y a pas d'anciens sommets
        SommetsTries.addAll(g.get_Sommets()); // copie des sommets du graphe

// Tri des sommets par ordre décroissant de degré (nombre d'arêtes)
        Collections.sort(SommetsTries, new Comparator<Sommet>() {
            @Override
            public int compare(Sommet s1, Sommet s2) {
                int deg1 = s1.aretes.size();
                int deg2 = s2.aretes.size();
                return Integer.compare(deg2, deg1); // décroissant
            }
        });

        for (Sommet s : SommetsTries){
            colore.put(s, false);
            couleur.put(s, 0);
        }

        nbJour = 1;
        capaciteJour.put(nbJour, capaciteCamion);

        while (SommetsNonColores()){
            for (Sommet s : new ArrayList<>(SommetsTries)){
                if (!colore.get(s)){
                    boolean conflit = false;
                    for (Arete a : s.aretes) {
                        Sommet voisin = a.destination;
                        if (colore.get(voisin) && couleur.get(voisin) == nbJour) {
                            conflit = true;
                            break;
                        }
                    }
                    int qs = quantites.getOrDefault(s,0);
                    if (qs > capaciteJour.get(nbJour)){
                        conflit = true;
                    }
                    if (!conflit) {
                        couleur.put(s, nbJour);
                        colore.put(s, true);
                        capaciteJour.put(nbJour, capaciteJour.get(nbJour) - qs);
                    }
                }
            }
        }

        nbJour ++;
        capaciteJour.putIfAbsent(nbJour, capaciteCamion);
    }

    public void afficherPlanning (Map<Sommet, Integer> quantites) {
        for (int j = 1; j <= nbJour; j++) {
            System.out.println("Jour de collecte numéro " + j + ":");
            for (Sommet s : SommetsTries) {
                if (couleur.get(s) == j) {
                    System.out.println(s.id + "a pour quantite :" + quantites.getOrDefault(s, 0));
                }
            }
            System.out.println();
        }
    }
}
