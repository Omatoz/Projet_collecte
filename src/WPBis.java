import java.util.*;

public class WPBis {
    // Attributs
    private Graphe g;
    private ArrayList<Sommet> SommetsTries;
    private Map<Sommet, Integer> couleur ;
    private Map<Sommet, Boolean> colore ;
    private Map<Integer, Integer> capaciteJour ;
    private int nbJour;
    private int capaciteCamion;

    // Constructeur
    public WPBis(Graphe g, int capaciteCamion) {
        this.g = g;
        this.SommetsTries = new ArrayList<>();
        this.couleur = new HashMap<>();
        this.colore = new HashMap<>();
        this.capaciteJour = new HashMap<>();
        this.nbJour = 0;
        this.capaciteCamion = capaciteCamion;
    }
    public int getNbJour() {
        return nbJour; // getters
    }

    // Méthode qui vérifie s'il reste des sommets non colorés
    private boolean SommetsNonColores(){
        for (Boolean b : colore.values()){
            if (!b){ return true; // retourne true si un sommet n'est pas encore coloré donc n'a pas de jours de collecte
            }
        }
        return false;
    }

    // Méthode principale de coloration
    public void coloration (Map<Sommet, Integer> quantites){
        SommetsTries.clear(); // pour s'assurer qu'il n'y a pas d'anciens sommets
        SommetsTries.addAll(g.get_Sommets()); // copie des sommets du graphe

         // Tri des sommets par ordre décroissant en terme de quantité de chaque secteur / de la pondération du sommet
        Collections.sort(SommetsTries, new Comparator<Sommet>() {
            @Override
            public int compare(Sommet s1, Sommet s2) {
                int q1 = quantites.getOrDefault(s1,0);
                int q2 = quantites.getOrDefault(s2,0);
                return Integer.compare(q2, q1); // tri décroissant
            }
        });

        for (Sommet s : SommetsTries){
            colore.put(s, false);
            couleur.put(s, 0);
        }

        nbJour = 1; // jour 1
        capaciteJour.put(nbJour, capaciteCamion); // capcité initiale du jour un

        // Boucle principale
        while (SommetsNonColores()){
            boolean PlaceSommet = false; // pour savoir si le jour a servi
            for (Sommet s : new ArrayList<>(SommetsTries)){ // Parcourt dans ordre décroissant des pondérations
                if (!colore.get(s)){
                    boolean conflit = false;
                    // Vérification de l'adjacence entre arêtes
                    for (Arete a : s.aretes) {
                        Sommet voisin = a.destination;
                        if (colore.get(voisin) && couleur.get(voisin) == nbJour) {
                            conflit = true;
                            break;
                        }
                    }
                    int qs = quantites.getOrDefault(s,0);
                    // Vérifiction de la capacité du camion pour le jour en cours
                    if (qs > capaciteJour.get(nbJour)){
                        conflit = true; // Plus assez de place
                    }
                    if (!conflit) {// si pas de conflits on place ce secteur dans ce jour
                        couleur.put(s, nbJour);
                        colore.put(s, true);
                        capaciteJour.put(nbJour, capaciteJour.get(nbJour) - qs);// mise de à jour de la place restante dans un camion
                        PlaceSommet = true; // un sommet (secteur) à été attribué à un jour
                    }
                }
            }
            // Si aucun sommet n'a été attribué à ca jour on passe au jour suivant
            if(!PlaceSommet) {
                nbJour ++;
                capaciteJour.putIfAbsent(nbJour, capaciteCamion);
            }
        }
    }

    public void afficherPlanning (Map<Sommet, Integer> quantites) {
        for (int j = 1; j <= nbJour; j++) {
            System.out.println("Jour de collecte numéro " + j + ":");
            int totalDuJour = 0;
            for (Sommet s : SommetsTries) {
                if (couleur.get(s) == j) {
                    int q = quantites.getOrDefault(s,0);
                    System.out.println(s.id + " a pour quantite : " + q);
                    totalDuJour += q;
                }
            }
            System.out.println("--> Total ramasse ce jour :" + totalDuJour + " sur " + capaciteCamion);
            System.out.println("---------------------------------------");
        }
    }
}
