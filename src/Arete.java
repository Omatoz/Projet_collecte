public class Arete {

    public Sommet depart;
    public Sommet destination;
    public int poids;
    public int type; // 1, 2, ou 3 : type de la rue

    // constructeur
    public Arete(Sommet depart, Sommet destination, int poids, int type) {
        this.depart = depart;
        this.destination = destination;
        this.poids = poids;
        this.type = type;
    }
}