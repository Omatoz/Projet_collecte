import java.util.*;

public class DFS {

    private List<List<Integer>> adj;
    private boolean[] visite;
    private List<Integer> parcours;

    public DFS(int nbSommets) {
        adj = new ArrayList<>();
        for (int i = 0; i < nbSommets; i++) {
            adj.add(new ArrayList<>());
        }
        visite = new boolean[nbSommets];
        parcours = new ArrayList<>();
    }

    public void ajouterArete(int u, int v) {
        adj.get(u).add(v);
        adj.get(v).add(u); // non orienté pour ACM
    }

    public void dfs(int sommet) {
        Arrays.fill(visite, false);
        parcours.clear();
        explorer(sommet);
    }

    private void explorer(int u) {
        visite[u] = true;
        parcours.add(u);

        for (int v : adj.get(u)) {
            if (!visite[v]) {
                explorer(v);
                parcours.add(u);  // retour (comme sur ton exemple)
            }
        }
    }

    public List<Integer> getParcoursComplet() {
        return parcours;
    }
}

