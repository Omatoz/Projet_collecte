🚚 Projet d'Optimisation de Tournée de Collecte de Déchets

Projet universitaire réalisé dans le cadre du cours de **Théorie des Graphes**. Cette application Java en mode console modélise un réseau routier pour optimiser les tournées de ramassage de déchets en utilisant des algorithmes de graphes classiques.

Objectif du Projet

L'objectif est de développer une solution capable de proposer des itinéraires optimisés pour des camions de collecte en réponse à deux problématiques logistiques majeures :
1.  **La tournée des encombrants :** Visiter une sélection de points (Problème du Voyageur de Commerce).
2.  **La tournée des poubelles :** Parcourir l'intégralité des rues d'un secteur (Problème du Postier Chinois).

Le programme prend en compte différentes contraintes de circulation en modélisant le réseau routier sous forme de graphes non-orientés, orientés ou mixtes.

Structure du Code

Le projet est structuré en plusieurs classes avec des responsabilités bien définies :
-   `Main.java` : Point d'entrée, lance l'application.
-   `Menu.java` : Gère toute l'interface utilisateur et la navigation.
-   `Graphe.java` : Modélise le réseau routier et gère le chargement depuis les fichiers.
-   `Sommet.java` / `Arete.java` : Blocs de construction du graphe.
-   `Itineraire.java` : Contient l'implémentation de l'algorithme de Dijkstra.
-   `Tournee.java` : Contient l'implémentation de l'heuristique du Plus Proche Voisin (TSP).
-   `Eulerien.java` : Contient les méthodes de vérification des conditions eulériennes.
-   `Hierholzer.java` : Contient l'implémentation de l'algorithme de Hierholzer.

Projet réalisé par Thomas CAZAU, Marie MATHIEU & Soraya KETTELA
