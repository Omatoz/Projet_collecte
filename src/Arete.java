public class Arete {
    public Sommet destination;
    public int poids;

    public Arete(Sommet destination, int poids) {
        this.destination = destination;
        this.poids = poids;
    }

    public Sommet getDestination() {
        return destination;
    }
}
