public class Arete {
    public Sommet destination;
    public int poids;

    // constructeur
    public Arete(Sommet destination, int poids) {
        this.destination = destination;
        this.poids = poids;
    }

    // getter
    public Sommet getDestination() {
        return destination;
    }
}