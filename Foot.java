public class Foot {

	static boolean coPossible(String log,String mdp,int co){
		int res =BD.executerSelect(co, "SELECT * FROM utilisateur WHERE utLogin = '" + log +"' AND utPassword  = '" + mdp +"'");
	    if (res>=0 && BD.suivant(res))
		return(true);
	    else
		return(false);
	}
    
    public static void main(String[] args) {
	    int l=0 ;
	    //int connexion= BD.ouvrirConnexion("172.20.128.64","claudel_BD","claudel","claudel");
	    int connexion = BD.ouvrirConnexion("localhost", "Championnat", "root", "");
	    String log = "";
	    String mdp = "";
	    
	    int resEquipe = BD.executerSelect(connexion, "SELECT * FROM equipe");
	    
	    do{
		if(l==0)
			Ecran.afficher("Veuillez entrer votre identifiant : \n");
		else
			Ecran.afficher("Erreur, veuillez resaisir votre identifiant : \n");
		
		log =  Clavier.saisirString();
		
		if(l==0)
			Ecran.afficher("Veuillez entrer votre mot de passe : \n");
		else
			Ecran.afficher("Veuillez resaisir votre mot de passe : \n");
		
		mdp =  Clavier.saisirString();
		l++;
	    }while(!coPossible(log,mdp,connexion));
	    
	    Ecran.afficher("Equipes pouvant participer au championnat : \n");
	    while (BD.suivant(resEquipe)) {
			Ecran.afficher("Equipe n°",BD.attributString(resEquipe,"equipe.eqID"),"      nom : ", BD.attributString(resEquipe,"equipe.eqNom"));
			Ecran.sautDeLigne();
		}
		
		char modif=' ';
		if (log.equals("admin")){
			do{
				Ecran.afficher("Voulez vous modifier les journées / résultat ( o : oui , n : non ) : \n");
				modif =  Clavier.saisirChar();
				if (modif != 'n'){
					
					
				}
			}while(modif != 'n');
		}
    }
   


  
    
    
}