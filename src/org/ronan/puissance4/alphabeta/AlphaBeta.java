/**
 * 
 */
package org.ronan.puissance4.alphabeta;

import java.util.ArrayList;

import org.ronan.puissance4.modele.jeu.ColonnePleineException;
import org.ronan.puissance4.modele.jeu.Grille;
import org.ronan.puissance4.modele.jeu.Jeton;
import org.ronan.puissance4.modele.joueur.Joueur;
import org.ronan.puissance4.modele.matrice.HorsIndexException;
import org.ronan.puissance4.modele.partie.Partie;

/**
 * Cet algorithme de recherche permet grace � une heuristique de d�terminer o� joueur le prochain coup. Il s'agit d'un algorithme 
 * alpha-beta
 * @author Ronan Quintin
 *
 */
public class AlphaBeta {


	private static final int PROFONDEUR_EXPLORATION_ALPHA_BETA = 4;
	private Heuristique heuristique;

	/**
	 * @param heuristique
	 */
	public AlphaBeta(Heuristique heuristique) {
		this.heuristique = heuristique;
	}

	/**
	 * Utilise son heurtistique et l'algorithme alpha beta pour d�terminer dans quel colonne jouer le prochain jeton. 
	 * @param grille la grille de jeu
	 * @param joueur le joueur dont c'est le tour
	 * @param adversaire le joueur adversaire
	 * @return L'index de la colonne ou joueur
	 */
	public int calculeColonneAJouer(Grille grille, Joueur joueur){
		ArrayList<Integer> colonnesAJouer = new ArrayList<Integer>();
		colonnesAJouer.add(new Integer(1));
		
		double valeurDeJeu = Heuristique.MIN_NOTE;
		for(int i=1; i <= Grille.LARGEUR_GRILLE; i++){
			try {
				if(!grille.estColonnePleine(i)){
					Grille copieDeLaGrille = grille.copie();
					Jeton jeton = new Jeton(joueur.getCouleur());
					copieDeLaGrille.insereJeton(i, jeton);
					double valeurDeJeuCourante = alphabeta(copieDeLaGrille, joueur, PROFONDEUR_EXPLORATION_ALPHA_BETA);
					if (valeurDeJeuCourante == valeurDeJeu){
						colonnesAJouer.add(new Integer(i));
					}else if(valeurDeJeuCourante > valeurDeJeu){
						colonnesAJouer.clear();
						valeurDeJeu = valeurDeJeuCourante;
						colonnesAJouer.add(new Integer(i));
					}
				}
			} catch (HorsIndexException e) {
				e.printStackTrace();
			}  catch (ColonnePleineException e) {
				e.printStackTrace();
			}
		}
		
		int numeroDeColonneAJouer = (int) (Math.random() * colonnesAJouer.size());
		return colonnesAJouer.get(numeroDeColonneAJouer);
	}


	/**
	 * Renvoie la valeur de jeu pour le joueur donn� en utilisant l'algorithme alpha-beta sur la profondeur donn�e. 
	 * alpha beta calcule recursivement la valeur de jeu en simulant tous les coups possible du joueur, puis de l'oposant. Plus simplement il teste tout le coup possible du joeur,
	 * puis regarde tout les coups possibles de l'adversaire suite � ce coup et ainsi de suite. Cet ensemble de possibilit�s forme un arbre des possibles. Chaque noeud de cet arbre 
	 * est une grille sur lequel s'applique l'algorithme.  Il minise alors la valeur de jeu de l'opposant et maximise celle du joueur. Lorsque la profondeur 0 est atteinte alors
	 * l'heuristique est appel�e pour calculer la valeur de jeu, les valeurs sont ensuite remont� dans la pile d'appels puis minimis�es, maximis�es selon que le noeud soit un noeud opposant (minimisation)
	 * ou un noeud joueur (maximisation). Des coupes alpha ou beta sont effectu�es dans le cas o� l'exploration d'un noeud s'avere �tre inutile.
	 * @param grille La grille de jeu
	 * @param joueur Le joeur
	 * @param opposant L'opposant
	 * @param profondeur La profondeur maximale d'exploration des possibles
	 * @return
	 */
	private double alphabeta(Grille grille, Joueur joueur, int profondeur){
		double alpha =Heuristique.MIN_NOTE;
		double beta=Heuristique.MAX_NOTE;
		return this.min(grille, joueur, profondeur, alpha, beta);
	}

	/**
	 * Applique la partie min, de minmax
	 */
	private double min(Grille grille, Joueur joueur,  int profondeur, double alpha, double beta){
		if(profondeur != 0){
			double valeurDeJeu = Heuristique.MAX_NOTE;
			for(int i=1; i <= Grille.LARGEUR_GRILLE ; i++){
				try {
					if(!grille.estColonnePleine(i)){
						Grille copieDeLaGrille = grille.copie();
						Jeton jeton = new Jeton(Partie.getPartie().joueurSuivant(joueur).getCouleur());
						copieDeLaGrille.insereJeton(i, jeton);
						valeurDeJeu = Math.min(valeurDeJeu, this.max(copieDeLaGrille, joueur, profondeur-1, alpha, beta));
						
						if(alpha >= valeurDeJeu){
							return valeurDeJeu; // Coupure alpha
						}
						
		               beta = Math.min(beta, valeurDeJeu);
						
					}
				} catch (HorsIndexException e) {
					e.printStackTrace();
				}catch (ColonnePleineException e) {
					e.printStackTrace();
				} 
			}
			return valeurDeJeu;
		}else{
			return this.heuristique.noteGrille(grille, joueur );
		}
	}

	/**
	 * Applique la partie max de minmax
	 */
	private double max(Grille grille, Joueur joueur, int profondeur, double alpha, double beta){
		if(profondeur != 0){
			double valeurDeJeu = Heuristique.MIN_NOTE;
			for(int i=1; i <= Grille.LARGEUR_GRILLE; i++){
				try{
					if(!grille.estColonnePleine(i)){
						Grille copieDeLaGrille = grille.copie();
						Jeton jeton = new Jeton(joueur.getCouleur());
						copieDeLaGrille.insereJeton(i, jeton);
						valeurDeJeu = Math.max(valeurDeJeu, this.min(copieDeLaGrille, joueur, profondeur-1, alpha, beta));
		
						if(valeurDeJeu >= beta){
							return valeurDeJeu; // Coupure beta
						}
						alpha = Math.max(alpha	, valeurDeJeu);
						
					}
				} catch (HorsIndexException e) {
					e.printStackTrace();
				}catch (ColonnePleineException e) {
					e.printStackTrace();
				} 
			}
			return valeurDeJeu;
		}else{
			return this.heuristique.noteGrille(grille, joueur);
		}
	}




}
