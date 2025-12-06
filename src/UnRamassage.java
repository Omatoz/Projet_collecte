import java.util.ArrayList;
import java.util.List;

public class UnRamassage {
    private Graphe graphe;

    public UnRamassage(Graphe graphe){
        this.graphe = graphe;
    }

    public static class Ramassage{
        public Itineraire.Dijkstra aller;
        public Itineraire.Dijkstra retour;
        public int distance;

        public Ramassage(Itineraire.Dijkstra aller, Itineraire.Dijkstra retour, int distance){
            this.aller = aller;
            this.retour = retour;
            this.distance = distance;
        }

        public String chemin(List<Sommet> chemin){
            if(chemin==null||chemin.isEmpty()){
                return "vide";
            }
            List<String> sommets = new ArrayList<>();
            for(Sommet s : chemin){
                sommets.add(s.id);
            }
            return String.join("-", sommets);
        }

    }

    public Ramassage ramassage(Sommet depart, Sommet arrivee){
        Itineraire.Dijkstra aller = Itineraire.trouver_chemin(graphe, depart, arrivee);
        Itineraire.Dijkstra retour = Itineraire.trouver_chemin(graphe, arrivee, depart);
        int distance = aller.getDistance() + retour.getDistance();
        return new Ramassage(aller, retour, distance);
    }
}
