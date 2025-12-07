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

    public List<Sommet> getShortcut(){
        return shortcut;
    }

    public List<List<Sommet>> getTournees(){
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
    public void decouperTournees(int capaciteMax) {
        if (shortcut == null || shortcut.isEmpty()) {
            System.err.println("Erreur : shortcut non encore calculé !");
            return;
        }

        tournees = new ArrayList<>();
        List<Sommet> currentTournee = new ArrayList<>();
        int chargeCourante = 0;
        int poidsTournee = 0; // pour suivre le poids total de la tournée

        Sommet depot = shortcut.get(0); // on suppose que le dépôt est le premier
        currentTournee.add(depot); // commencer la tournée par le dépôt

        for (int i = 1; i < shortcut.size(); i++) {
            Sommet precedent = shortcut.get(i - 1);
            Sommet actuel = shortcut.get(i);

            // chercher l'arête qui relie precedent → actuel
            int contenance = 0;
            for (Arete a : precedent.aretes) {
                if (a.destination.equals(actuel)) {
                    contenance = a.poids;
                    break;
                }
            }

            // si la capacité serait dépassée, terminer la tournée
            if (chargeCourante + contenance > capaciteMax) {
                currentTournee.add(depot); // retour au dépôt
                System.out.print("Sommets de la tournée : ");
                for (Sommet s : currentTournee) System.out.print(s.id + " ");
                System.out.println(" -> Poids total = " + poidsTournee);

                tournees.add(new ArrayList<>(currentTournee));
                currentTournee.clear();
                currentTournee.add(depot); // nouvelle tournée commence au dépôt
                chargeCourante = 0;
                poidsTournee = 0;
            }

            currentTournee.add(actuel);
            chargeCourante += contenance;
            poidsTournee += contenance;
        }

        if (!currentTournee.isEmpty() && !currentTournee.get(currentTournee.size() - 1).equals(depot)) {
            currentTournee.add(depot);
            System.out.print("Sommets de la tournée : ");
            for (Sommet s : currentTournee) System.out.print(s.id + " ");
            System.out.println(" -> Poids total = " + poidsTournee);

            tournees.add(new ArrayList<>(currentTournee));
        }
    }


}
