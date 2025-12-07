import java.util.Scanner;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
public class WelshPowell {
    // Attributs
    private Graphe g;
    private ArrayList<Sommet> SommetsTries = new ArrayList<>();
    private Map<Sommet, Integer> couleur = new HashMap<>();
    private Map<Sommet, Boolean> colore = new HashMap<>();
    private int nbCouleur = 0;

    // Constructeur
    public WelshPowell(Graphe g) {
        this.g = g;
    }
    // Méthode qui vérifie s'il reste des sommets non colorés
    private boolean existeSommetNonColore () {
        for (Boolean b : colore.values()) {
            if (!b) {
                return true; // si au moins un sommet n'est pas encore coloré
            }
        }
        return false;
    }
    // Méthode qui retourne le nombre de couleurs utilisée
    public int getNbCouleurs() {
        return nbCouleur - 1; // -1 car une incrémentation de trop en fin d'algorithme
    }
    // Méthode qui affiche la couleur/ jour de collecte de chaque sommet et affiche un récapitulatif
    public void afficherCouleurs(){
        // Affichage de la liste de coloration par sommet/ secteur
        for (Sommet s : g.get_Sommets()){
            System.out.println("Sommet " + s.id + " est de couleur " + couleur.get(s));
        }
        System.out.println("\n --- Recapitulatif du PLanning (Une Couleur = Un Jour) ---");
        for(int j = 1; j < nbCouleur; j++){
            System.out.println("Jour " + j + " (Couleur " + j + ") : ");

            ArrayList<String> secteurDuJour = new ArrayList<>(); //Gestion des virgules
            for (Sommet s : g.get_Sommets()){
                if (couleur.get(s) != null && couleur.get(s) == j){
                    secteurDuJour.add(String.valueOf(s.id));
                }
            }
            System.out.println(secteurDuJour);
        }
    }

    // Méthode de coloration principale
    public void coloration () {
        SommetsTries.clear(); // remise à zéro des sommets
        SommetsTries.addAll(g.get_Sommets());
        // On tri les degrés des sommets par décroissance
        Collections.sort(SommetsTries, new Comparator<Sommet>() {
            @Override
            public int compare(Sommet s1, Sommet s2) {

                // degré = nombre d'arêtes sortantes
                int deg1 = s1.aretes.size(); // degré du sommet 1
                int deg2 = s2.aretes.size(); // degré du sommet 2

                // On trie par ordre décroissant les degrés des sommets
                return Integer.compare(deg2, deg1);
            }
        });
        // Initalisation des sommets en partant du principe qu'ils ne sont pas colorés
        for (Sommet s : SommetsTries) {
            colore.put(s, false);
            couleur.put(s, 0);
        }

        nbCouleur = 1; // couleur 1 = jour 1

        // Boucle principale pour la coloration

        while (existeSommetNonColore()) {
             for(Sommet s : new ArrayList<>(SommetsTries)) { // Parcours des sommets dans l'odre du tri précédent
                 if (!colore.get(s)){  // on regarde que les sommets non colorés
                     boolean conflit = false;

                     // Vérification des voisins
                     for (Arete a : s.aretes){
                         Sommet voisin = a.destination;

                         if (colore.get(voisin) && couleur.get(voisin) ==  nbCouleur){
                             conflit = true ;
                             break;
                         }
                     }

                     // Si pas de conflit d'adjacence de sommet on colorie le sommet sur lequel l'algorithme passe
                     if (!conflit){
                         couleur.put(s, nbCouleur);
                         colore.put(s, true);
                     }
                 }
             }
             nbCouleur++; // incrémentation des couleurs
        }

    }
}
