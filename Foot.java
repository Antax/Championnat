public class Foot {
	
	static class equipe{
		String ID;
		//ce booléen permet de savoir si une équipe est déjà inscrite dans un match.
		//il est utile lors de la randomisation de 
		boolean dejaChoisie;
		int points;
		//cet entier nous permet de savoir dans quel match se situe une équipe. La compétition se déroule avec:
		//8 huitièmes de finales, 4 quarts de finales... Ainsi, si le "match" d'une équipe est 3, on saura que cet équipe dispute ou va disputer
		//le 3ème huitième de finale. si match=9, cela correspond au premier quart de finale...
		//Cela nous permet de savoir quelles équipes devront s'affronter
		int match;
	}

	static boolean coPossible(String log,String mdp,int co){
		int res =BD.executerSelect(co, "SELECT * FROM utilisateur WHERE utLogin = '" + log +"' AND utPassword  = '" + mdp +"'");
	    if (res>=0 && BD.suivant(res))
		return(true);
	    else
		return(false);
	}


	//fonction permetant la répartition aléatoire de la première journée
	static void journeeRandom(equipe tabEquipe[]){
		int indiceEquipe1;
		int indiceEquipe2;
		//9-1=8, ce qui correspond au nombre de matchs de la première journée
		for (int i=1;i<9;i++){
			//on choisit deux équipes ne participant pas encore à un match
			indiceEquipe1=(int) (Math.random()*16);
			while(tabEquipe[indiceEquipe1].dejaChoisie == true){
				indiceEquipe1=(int) (Math.random()*16);
			}
			tabEquipe[indiceEquipe1].dejaChoisie = true;
			tabEquipe[indiceEquipe1].match = i;
			indiceEquipe2=(int) (Math.random()*16);
			while(tabEquipe[indiceEquipe2].dejaChoisie == true){
				indiceEquipe2=(int) (Math.random()*16);
			}
			tabEquipe[indiceEquipe2].dejaChoisie = true;
			tabEquipe[indiceEquipe2].match = i;
		}
	}
    
    public static void main(String[] args) {
	    int l=0 ;
	    //int connexion= BD.ouvrirConnexion("172.20.128.64","claudel_BD","claudel","claudel");
        int connexion = BD.ouvrirConnexion("localhost", "Championnat", "root", "");
        //Co connexion = new Co();
	    String log = "";
		String mdp = "";
		
		int resEquipe = BD.executerSelect(connexion, "SELECT * FROM equipe");

		//association des enregistrements d'équipe à des variables de type équipe, elles-mêmes stockées dans un tableau.
		//il y aura 16 équipes
		//tabEquipe[n] correspond à l'équipe n+1 dans la base de données
		equipe tabEquipe[]=new equipe[16];
		int creerEquipe =0;
		while (BD.suivant(resEquipe)) {
			tabEquipe[creerEquipe]=new equipe();
			tabEquipe[creerEquipe].ID = BD.attributString(resEquipe,"equipe.eqID");
			tabEquipe[creerEquipe].dejaChoisie = false;
			tabEquipe[creerEquipe].points=0;
			creerEquipe++;
		}
		

		journeeRandom(tabEquipe);
		for (int i =0;i<16;i++){
			Ecran.afficher(tabEquipe[i].ID," match  n°",tabEquipe[i].match,"\n");
		}
	    
	    
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
					Ecran.afficher("ID : \n");
					String maID =  Clavier.saisirString();
					Ecran.afficher("equipe domicile : \n");
					String eqDomi =  Clavier.saisirString();
					Ecran.afficher("equipe ext  : \n");
					String eqExt =  Clavier.saisirString();
					Ecran.afficher("score domi : \n");
					String scDomi =  Clavier.saisirString();
					Ecran.afficher("score ext : \n");
					String scExt =  Clavier.saisirString();
					if(!(eqDomi.equals(eqExt))){
					//if(BD.executerSelect(connexion, "SELECT * FROM `match`WHERE maID = '"+maID+"' ")>=0)
						BD.executerUpdate(connexion,"INSERT INTO `match` (`maID`, `maEquipe1`, `maEquipe2`, `maScoreEquipe1`, `maScoreEquipe2`) VALUES ('"+maID+"', '"+eqDomi+"', '"+eqExt+"', '"+scDomi+"', '"+scExt+"');");
					}else{
						Ecran.afficher("Erreur! Une équipe ne peu pas s'affronter elle-même! \n");
					}
				}
			}while(modif != 'n');
		}
    }
   


  
    
    
}