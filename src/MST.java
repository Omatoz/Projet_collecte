import java.io.*;
import java.util.*;

public class MST {

    private Graphe graphe;
    private List<Arete> acm;
    private DFS dfs;
    private List<Sommet> parcoursDFS;
    private List<Sommet> parcoursOptimise;
    private int capaciteCamion;

    public MST(String fichierSommets, String fichierAretes, int capaciteCamion) throws FileNotFoundException {
        this.graphe = new Graphe(fichierSommets, fichierAretes);
        this.capaciteCamion = capaciteCamion;
    }

    // Étape 1 : calcul de l'arbre couvrant minimal avec Prim
    public void calculACM() {
        List<Sommet> listeSommets = new ArrayList<>(graphe.get_Sommets());
        this.acm = Prim.arbreCouvrantMinimal(listeSommets);
        System.out.println("=== Arbre Couvant Minimal (Prim) ===");
        for (Arete a : acm) {
            System.out.println(a.depart.id + " - " + a.destination.id + " (poids " + a.poids + ")");
        }
        System.out.println("====================================");
    }

    // Étape 2 : DFS sur l'arbre couvrant pour obtenir un ordre de visite
    public void parcoursDFS() {
        List<Sommet> sommetsACM = extraireSommetsACM(acm);
        dfs = new DFS(sommetsACM.size());

        Map<Sommet, Integer> indices = new HashMap<>();
        for (int i = 0; i < sommetsACM.size(); i++) indices.put(sommetsACM.get(i), i);

        for (Arete a : acm) {
            int u = indices.get(a.depart);
            int v = indices.get(a.destination);
            dfs.ajouterArete(u, v);
        }

        dfs.dfs(0); // départ depuis le premier sommet
        parcoursDFS = convertirIndicesEnSommets(dfs.getParcoursComplet(), sommetsACM);

        System.out.println("=== Parcours DFS complet ===");
        for (Sommet s : parcoursDFS) System.out.print(s.id + " ");
        System.out.println("\n====================================");
    }

    // Étape 3 : Shortcutting avec Dijkstra
    public void optimisationParcours() {
        Graphe g = new Graphe(graphe); // copie du graphe original
        parcoursOptimise = Shortcutting.shortcut(parcoursDFS, new ArrayList<>(g.get_Sommets()));

        // On peut ici réutiliser Itineraire.Dijkstra pour recalculer les chemins les plus courts si nécessaire
        System.out.println("=== Parcours après Shortcutting ===");
        for (Sommet s : parcoursOptimise) System.out.print(s.id + " ");
        System.out.println("\n====================================");
    }

    // Étape 4 : découpage en tournées selon la capacité du camion
    public List<List<Sommet>> decoupageTournées(Map<Sommet, Integer> contenances) {
        List<List<Sommet>> tournées = new ArrayList<>();
        List<Sommet> tourActuelle = new ArrayList<>();
        int chargeActuelle = 0;

        // toujours commencer et finir au dépôt (premier sommet)
        Sommet depot = parcoursOptimise.get(0);
        tourActuelle.add(depot);

        for (int i = 1; i < parcoursOptimise.size(); i++) {
            Sommet s = parcoursOptimise.get(i);
            int c = contenances.getOrDefault(s, 0);
            if (chargeActuelle + c > capaciteCamion) {
                // retour au dépôt et nouvelle tournée
                tourActuelle.add(depot);
                tournées.add(new ArrayList<>(tourActuelle));
                tourActuelle.clear();
                tourActuelle.add(depot);
                chargeActuelle = 0;
            }
            tourActuelle.add(s);
            chargeActuelle += c;
        }

        tourActuelle.add(depot); // retour final au dépôt
        tournées.add(tourActuelle);

        System.out.println("=== Tournées découpées selon capacité camion ===");
        int numTour = 1;
        for (List<Sommet> t : tournées) {
            System.out.print("T" + numTour + " : ");
            for (Sommet s : t) System.out.print(s.id + " ");
            System.out.println();
            numTour++;
        }
        System.out.println("====================================");

        return tournées;
    }

    // --- Méthodes auxiliaires ---
    private List<Sommet> extraireSommetsACM(List<Arete> acm) {
        Set<Sommet> set = new LinkedHashSet<>();
        for (Arete a : acm) {
            set.add(a.depart);
            set.add(a.destination);
        }
        return new ArrayList<>(set);
    }

    private List<Sommet> convertirIndicesEnSommets(List<Integer> indices, List<Sommet> liste) {
        List<Sommet> result = new ArrayList<>();
        for (int idx : indices) result.add(liste.get(idx));
        return result;
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