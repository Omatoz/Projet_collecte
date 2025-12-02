public class Arete {
    public Sommet destination;
    public int poids;
    public int type; // 1, 2, ou 3, représente le type de la rue d'origine

    // constructeur
    public Arete(Sommet destination, int poids, int type) {
        this.destination = destination;
        this.poids = poids;
        this.type = type;
    }

    // getters
    public Sommet getDestination() {
        return destination;
    }
    public int getPoids() {
        return poids;
    }
    public int getType() {
        return type;
    }
}