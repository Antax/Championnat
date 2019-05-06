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
	    Ecran.afficher("wesh\n");
    }
   


  
    
    
}