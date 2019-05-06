public class Championnat {
	
	class equipe {
		int id;
		String nom;
	}
	
	static int selEquipeUnique (int connexion ,int indiceTableau){
		String sql = "SELECT * FROM equipe WHERE eqID='"+ indiceTableau + "'";
		return BD.executerSelect(connexion, sql);
	}
	
	//fonction pour renseigner les équipes participant au championnat.
	static void renseignerEquipes (equipe tab[]){
		boolean equipeChoisie = false;
		for (int i =0; i<tab.length-1; i++){
			Ecran.afficher ("voulez vous choisir l'équipe"); 
		}
	}

	
	public static void main (String[]args){
	
		BD BD=new BD();
		int connexion = BD.ouvrirConnexion("172.20.128.64", "claudel_BD", "claudel", "claudel");
		
		int resEquipe = BD.executerSelect(connexion, "SELECT * FROM equipe");//WHERE eqID=",tabEquipe[i].id,"");
		
		
		equipe tabEquipe[] =  new equipe[20];
		
		//test
		while (BD.suivant(resEquipe)) {
			Ecran.afficher("whouhou ", BD.attributString(resEquipe,"equipe.eqNom"));
			Ecran.sautDeLigne();
		}
		int resEquipe3 = selEquipeUnique(connexion, 3);
		while(BD.suivant(resEquipe3)){
			Ecran.afficher (BD.attributString(resEquipe3, "equipe.eqNom"));
		}
		
	}
}