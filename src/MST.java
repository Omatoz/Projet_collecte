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





/*
import java.io.*;
import java.util.*;

public class MST{
    private List<Prim.Arete> aretes;
    private int nbSommets;
    private int[] poids;   //poids de chaque sommet
    private int capacite;   //capacite du camion
    private Map<Integer, String> lettreSommet;


    //getters
    public List<Prim.Arete> getAretes(){
        return aretes;
    }
    public int getNbSommets(){
        return nbSommets;
    }
    public int[] getPoids(){
        return poids;
    }
    public int getCapacite(){
        return capacite;
    }
    public Map<Integer, String> getLettreSommet(){
        return lettreSommet;
    }

    public void graphe(String fichier) throws IOException {
        Scanner sc = new Scanner(new File(fichier));
        aretes = new ArrayList<>();
        Set<String> setSommets = new HashSet<>();

        if (sc.hasNextLine()) sc.nextLine();

        List<String[]> lignes = new ArrayList<>();
        while (sc.hasNextLine()) {
            String ligne = sc.nextLine().trim();
            if (ligne.isEmpty()) continue;
            String[] elements = ligne.split(";");
            if (elements.length < 3) continue;
            lignes.add(elements);
            setSommets.add(elements[0].trim());
            setSommets.add(elements[1].trim());
        }
        sc.close();

        List<String> listeSommets = new ArrayList<>(setSommets);
        Collections.sort(listeSommets);
        nbSommets = listeSommets.size();

        Map<String, Integer> mapSommets = new HashMap<>();
        for (int i = 0; i < listeSommets.size(); i++) {
            mapSommets.put(listeSommets.get(i), i);
        }

        poids = new int[nbSommets];
        Arrays.fill(poids, 0);

        for (String[] elements : lignes) {
            String src = elements[0].trim();
            String dest = elements[1].trim();
            int poidsArete = Integer.parseInt(elements[2].trim());
            int srcIndex = mapSommets.get(src);
            int destIndex = mapSommets.get(dest);
            aretes.add(new Prim.Arete(srcIndex, destIndex, poidsArete));
        }

        lettreSommet = new HashMap<>();
        for (Map.Entry<String, Integer> entry : mapSommets.entrySet()) {
            lettreSommet.put(entry.getValue(), entry.getKey());
        }

    }

    public MST(String fichier, int capacite) throws IOException {  //constructeur qui
        this.aretes = new ArrayList<>();
        this.capacite = capacite;
        graphe(fichier);
    }

    public List<List<Integer>> tour(){
        List<Prim.Arete> arbreCouvrantMinimal = Prim.arbreCouvrantMinimal(aretes, nbSommets);  //creation arbre couvrant de poids minimum
        DFS dfs = new DFS(nbSommets);  //dfs sur arbre couvrant
        for(Prim.Arete a : arbreCouvrantMinimal){   //on parcourt chaque arete de l'arbre
            dfs.ajouterArete(a.sommetDepart, a.sommetArrivee);
        }
        dfs.dfs(0);  //on part du sommet de depart
        List<Integer> shortcut = dfs.shortcutting(0);

        List<List<Integer>> tours  = new ArrayList<>();
        List<Integer> tour = new ArrayList<>();
        int contenanceActuelle = 0;

        for(int sommet : shortcut){  //on parcours les sommets de la tournee shortcut
            int contenance = poids[sommet];
            if(contenanceActuelle + contenance > capacite){   //condition pour savoir si on peut collecter le sommet suivant
                tour.add(0);
                contenanceActuelle = 0;
            }
            tour.add(sommet);
            contenanceActuelle += contenance;
        }

        if(!tour.isEmpty()){   //si on arrive à la fin du parcours
            tour.add(0);
            tours.add(tour);
        }

        return tours;
    }


    public int poidsTotal(List<List<Integer>> tours, Graphe graphe){
        int poidsTotal = 0;
        for(List<Integer> tour : tours){  //on parcours chaque tour de la somme des tours
            for(int i=0; i<tour.size()-1; i++){
                List<Sommet> sommets = new ArrayList<>(graphe.get_Sommets());   //convertit Collection en ArrayList
                Sommet sommetDepart = sommets.get(tour.get(i));
                Sommet sommetArrivee = sommets.get(tour.get(i+1));
                Itineraire.Dijkstra dijkstra = Itineraire.trouver_chemin(graphe, sommetDepart, sommetArrivee);
                poidsTotal += dijkstra.getDistance();
            }
        }

        return poidsTotal;
    }
}
*/