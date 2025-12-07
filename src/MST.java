import java.io.*;
import java.util.*;

public class MST {

    private Graphe graphe;
    private List<Arete> acm;
    private DFS dfs;
    private List<Sommet> parcoursDFS;
    private List<Sommet> shortcut;
    private List<List<Sommet>> tournees;

    public MST(String fichierSommets, String fichierAretes) throws FileNotFoundException {
        this.graphe = new Graphe(fichierSommets, fichierAretes);
    }

    public Graphe getGraphe() {
        return graphe;
    }

    public List<Arete> getAcm() {
        return acm;
    }

    public List<Sommet> getShortcut() {
        return shortcut;
    }

    public List<List<Sommet>> getTournees() {
        return tournees;
    }

    public void calculACM() {
        List<Sommet> liste = new ArrayList<>(graphe.get_Sommets());
        acm = Prim.arbreCouvrantMinimal(liste);
    }

    public void parcoursDFS() {
        Set<Sommet> set = new LinkedHashSet<>();
        for (Arete a : acm) {
            set.add(a.depart);
            set.add(a.destination);
        }
        List<Sommet> sommetsACM = new ArrayList<>(set);
        Map<Sommet, Integer> index = new HashMap<>();
        for (int i = 0; i < sommetsACM.size(); i++) {
            index.put(sommetsACM.get(i), i);
        }
        dfs = new DFS(sommetsACM.size());
        for (Arete a : acm) {
            dfs.ajouterArete(index.get(a.depart), index.get(a.destination));
        }
        dfs.dfs(index.get(graphe.getSommet("A")));
        parcoursDFS = new ArrayList<>();
        for (int idx : dfs.getParcoursComplet()) {
            parcoursDFS.add(sommetsACM.get(idx));
        }
        for (Sommet s : parcoursDFS) System.out.print(s.id + " ");
        System.out.println();
    }

    public List<Sommet> getParcoursDFS() {
        return parcoursDFS;
    }

    public void parcourShortcutting() {
        if (parcoursDFS == null) {
            System.err.println("Erreur : DFS pas calculé !");
            return;
        }

        List<Sommet> listeSommets = new ArrayList<>(graphe.get_Sommets());
        this.shortcut = Shortcutting.shortcut(parcoursDFS);
        for (Sommet s : shortcut) System.out.print(s.id + " ");
        System.out.println();
    }

    public List<Sommet> getParcoursShortcut() {
        return shortcut;
    }


    //capacite
    // Nouvelle version : découpe des tournées en utilisant Dijkstra + contenance des sommets
    public void decouperTournees(int capaciteMax) {

        if (shortcut == null || shortcut.isEmpty()) {
            System.err.println("Erreur : shortcut non encore calculé !");
            return;
        }

        tournees = new ArrayList<>();
        List<Sommet> currentTournee = new ArrayList<>();
        Sommet depot = shortcut.get(0);
        currentTournee.add(depot);

        int chargeCourante = 0;
        int distanceTournee = 0;

        for (int i = 1; i < shortcut.size(); i++) {

            Sommet precedent = shortcut.get(i - 1);
            Sommet actuel = shortcut.get(i);

            // 1) Distance via Dijkstra
            Itineraire.Dijkstra resultat = Itineraire.trouver_chemin(graphe, precedent, actuel);
            int distanceChemin = resultat.getDistance();

            if (distanceChemin == Integer.MAX_VALUE) {
                System.err.println("Aucun chemin entre " + precedent.id + " et " + actuel.id);
                continue;
            }

            // 2) Contenance du sommet actuel
            int demande = actuel.contenance;

            // 3) Si la capacité serait dépassée, on termine la tournée actuelle
            if (chargeCourante + demande > capaciteMax) {

                // Retour au dépôt
                Itineraire.Dijkstra retour = Itineraire.trouver_chemin(graphe, precedent, depot);
                distanceTournee += retour.getDistance();

                currentTournee.add(depot); // ajouter le dépôt en fin de tournée
                System.out.print("Tournée : ");
                for (Sommet s : currentTournee) System.out.print(s.id + " ");
                System.out.println("-> Distance = " + distanceTournee + ", Charge = " + chargeCourante);

                tournees.add(new ArrayList<>(currentTournee));

                // Nouvelle tournée
                currentTournee.clear();
                currentTournee.add(depot);
                chargeCourante = 0;
                distanceTournee = 0;
            }

            // 4) Ajouter le sommet actuel à la tournée
            currentTournee.add(actuel);
            chargeCourante += demande;
            distanceTournee += distanceChemin;
        }

        // 5) Finalisation dernière tournée si non vide
        if (!currentTournee.isEmpty()) {
            Sommet dernier = currentTournee.get(currentTournee.size() - 1);
            if (!dernier.equals(depot)) {
                // Retour au dépôt
                Itineraire.Dijkstra retour = Itineraire.trouver_chemin(graphe, dernier, depot);
                distanceTournee += retour.getDistance();
                currentTournee.add(depot);
            }

            System.out.print("Tournée : ");
            for (Sommet s : currentTournee) System.out.print(s.id + " ");
            System.out.println("-> Distance = " + distanceTournee + ", Charge = " + chargeCourante);

            tournees.add(new ArrayList<>(currentTournee));
        }
    }

}

