public class Eulerien {
    // Vérification du graphe : eulérien ou pas
    public static boolean verifierConditionEulerienne(Graphe g) {
        // On récupère la collection de sommets depuis l'objet Graphe
        for (Sommet s : g.get_Sommets()) {
            // On regarde le degré de chaque sommet
            if (s.aretes.size() % 2 != 0) {
                System.out.println("Condition Euler non respectée : Le sommet " + s.id
                        + " est de degré impair (" + s.aretes.size() + ").");
                return false;
            }
        }
        return true;
    }
}
