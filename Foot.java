//Joseph Debray, Antoine Claudel

public class Foot {
	
	static class equipe{
		String nom;
		String ID;
		//ce bool�en permet de savoir si une �quipe est d�jà inscrite dans un match.
		//il est utile lors de la randomisation de 
		boolean dejaChoisie;
		int points;
		//cet entier nous permet de savoir dans quel match se situe une �quipe. La comp�tition se d�roule avec:
		//8 huitièmes de finales, 4 quarts de finales... Ainsi, si le "match" d'une �quipe est 3, on saura que cet �quipe dispute ou va disputer
		//le 3ème huitième de finale. si match=9, cela correspond au premier quart de finale...
		//Cela nous permet de savoir quelles �quipes devront s'affronter
		int match;
		int difButs;
		int butM;
		int butE;
		int nbVictoires;
		int nbDefaites;
		int nbEgalites;
	}

	static boolean coPossible(String log,String mdp,int co){
		int res =BD.executerSelect(co, "SELECT * FROM utilisateur WHERE utLogin = '" + log +"' AND utPassword  = '" + mdp +"'");
	    if (res>=0 && BD.suivant(res))
		return(true);
	    else
		return(false);
	}


	//fonction permetant la r�partition al�atoire de la première journ�e
	static void journeeRandom(equipe tabEquipe[]){
		int indiceEquipe1;
		int indiceEquipe2;
		//9-1=8, ce qui correspond au nombre de matchs de la première journ�e
		for (int i=1;i<9;i++){
			//on choisit deux �quipes ne participant pas encore à un match
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

	//cette fonction permet de savoir dans quel match se situera une �quipe si elle gagne un match
	static int prochainMatchEquipe(int matchDeDepart){
		int match = 0;
		switch (matchDeDepart){
			case 1 :
			case 2 :
				match= 9;
			break;
			case 3 :
			case 4 :
				match=10;
			break;
			case 5 :
			case 6 :
				match=11;
			break;
			case 7 :
			case 8 :
				match= 12;
			break;
			case 9 :
			case 11 :
				match= 13;
			break;
			case 10:
			case 12:
				match= 14;
			break;
			case 13:
			case 14: 
				match= 15;
			break;
			//nous permettra de savoir qui a gagn� la finale.
			case 15:
				match= 16;
			break;
		}
		return match;
	}


	//cette fonction permet à l'admin de renseigner le score d'un match
	//il n'a pas à s�lectionner le match, cela se fait automatiquement
	static void renseignerMatch(int connexion,int numMatch, equipe[] tabEquipe){
		if(numMatch>15 || numMatch <1){
			Ecran.afficher("Erreur le match entr� n'existe pas\n");
		}else{
			//on cherche les �quipes qui participent au match numMatch
			int indiceEquipe1;
			int indiceEquipe2;
			int i =0;
			while (tabEquipe[i].match!=numMatch){
				i++;
			}
			indiceEquipe1=i;
			i++;
			while (tabEquipe[i].match!=numMatch){
				i++;
			}
			indiceEquipe2=i;

			//Saisie des Scores
			Ecran.afficher("Saisissez le score du match ",tabEquipe[indiceEquipe1].nom," / ",tabEquipe[indiceEquipe2].nom,"\n");
			Ecran.afficher(tabEquipe[indiceEquipe1].nom," : ");
			int butEq1 = Clavier.saisirInt();
			Ecran.afficher(tabEquipe[indiceEquipe2].nom," : ");
			int butEq2 = Clavier.saisirInt();

			BD.executerUpdate(connexion,"INSERT INTO `matchs` (`maID`, `maEquipe1`, `maEquipe2`, `maScoreEquipe1`, `maScoreEquipe2`) VALUES ('"+numMatch+"', '"+tabEquipe[indiceEquipe1].ID+"', '"+tabEquipe[indiceEquipe2].ID+"', '"+butEq1+"', '"+butEq2+"');");

			//v�rification du vainqueur
			if (butEq1==butEq2){
				//le gagnant est choisi au hasard...
				if (Math.random()>0.5){
					tabEquipe[indiceEquipe1].match=prochainMatchEquipe(tabEquipe[indiceEquipe1].match);
				}else{
					tabEquipe[indiceEquipe2].match=prochainMatchEquipe(tabEquipe[indiceEquipe1].match);
				}
			}

			if (butEq1>butEq2){
				//l'�quipe 1 passe au prochain tour
				tabEquipe[indiceEquipe1].match=prochainMatchEquipe(tabEquipe[indiceEquipe1].match);
			}

			if (butEq2>butEq1){
				//l'�quipe 2 passe au prochain tour
				tabEquipe[indiceEquipe2].match=prochainMatchEquipe(tabEquipe[indiceEquipe1].match);
			}
		}
	}

	//cette fonction va, pour une �quipe, piocher dans la base de donn�es le nombre de matchs gagn�s, de buts marqu�s, de points... 
	//afin d'actualiser la variable de type Equipe
	static void actualiserVariables(equipe[] tabEquipe, int ID,  int connexion){
		int resMatch = BD.executerSelect(connexion, "SELECT * FROM matchs");
		while (BD.suivant(resMatch)) {
			//On cherche les matchs dans lesquels l'�quipe d'id ID a particip�
			if ((BD.attributInt(resMatch,"matchs.maEquipe1"))==ID){
				tabEquipe[ID-1].butM=BD.attributInt(resMatch,"matchs.maScoreEquipe1");
				tabEquipe[ID-1].butE=BD.attributInt(resMatch,"matchs.maScoreEquipe2");
				//l'�quipe a-t-elle gagn�e? perdu? fait �galit�?
				if(BD.attributInt(resMatch,"matchs.maScoreEquipe1")==BD.attributInt(resMatch,"matchs.maScoreEquipe2")){
					tabEquipe[ID-1].nbEgalites++;
				}
				if(BD.attributInt(resMatch,"matchs.maScoreEquipe1")<BD.attributInt(resMatch,"matchs.maScoreEquipe2")){
					tabEquipe[ID-1].nbDefaites++;
				}
				if(BD.attributInt(resMatch,"matchs.maScoreEquipe1")>BD.attributInt(resMatch,"matchs.maScoreEquipe2")){
					tabEquipe[ID-1].nbVictoires++;
				}
			}
			if ((BD.attributInt(resMatch,"matchs.maEquipe2"))==ID){
				tabEquipe[ID-1].butM=tabEquipe[ID-1].butM+BD.attributInt(resMatch,"matchs.maScoreEquipe2");
				tabEquipe[ID-1].butE=tabEquipe[ID-1].butE+BD.attributInt(resMatch,"matchs.maScoreEquipe1");
				//l'�quipe a-t-elle gagn�e? perdu? fait �galit�?
				if(BD.attributInt(resMatch,"matchs.maScoreEquipe2")==BD.attributInt(resMatch,"matchs.maScoreEquipe1")){
					tabEquipe[ID-1].nbEgalites++;
				}
				if(BD.attributInt(resMatch,"matchs.maScoreEquipe2")<BD.attributInt(resMatch,"matchs.maScoreEquipe1")){
					tabEquipe[ID-1].nbDefaites++;
				}
				if(BD.attributInt(resMatch,"matchs.maScoreEquipe2")>BD.attributInt(resMatch,"matchs.maScoreEquipe1")){
					tabEquipe[ID-1].nbVictoires++;
				}
			}
		}
		tabEquipe[ID-1].difButs =differenceButs(ID-1, tabEquipe);
		
	}

	//renvoie la diff�rence de but pour une �quipe
	static int differenceButs(int ID, equipe[] tabEquipe){
		return tabEquipe[ID].butM-tabEquipe[ID].butE;
	}

	//calcule le nombre de points d'une �quipe
    static int nbPoints(int ID, equipe[] tabEquipe){
		return (tabEquipe[ID].nbVictoires)*3+tabEquipe[ID].nbEgalites;
	}
	
	static String afficherNb(int nb){
		String m = Integer.toString(nb);
		if (nb<10)
			m+=" ";
		return m;
	}

    public static void main(String[] args) {
		boolean run =true;
	    int l=0 ;
	    //int connexion= BD.ouvrirConnexion("172.20.128.64","claudel_BD","claudel","claudel");
        int connexion = BD.ouvrirConnexion("localhost", "championnat", "root", "");
        //Co connexion = new Co();
	    String log = "";
		String mdp = "";

		
		int resEquipe = BD.executerSelect(connexion, "SELECT * FROM equipe");
		int resMatch = BD.executerSelect(connexion, "SELECT * FROM matchs");

		//association des enregistrements d'�quipe à des variables de type �quipe, elles-mêmes stock�es dans un tableau.
		//il y aura 16 �quipes
		//tabEquipe[n] correspond à l'�quipe n+1 dans la base de donn�es
		equipe tabEquipe[]=new equipe[16];
		int creerEquipe =0;
		while (BD.suivant(resEquipe)) {
			tabEquipe[creerEquipe]=new equipe();
			tabEquipe[creerEquipe].ID = BD.attributString(resEquipe,"equipe.eqID");
			tabEquipe[creerEquipe].nom = BD.attributString(resEquipe,"equipe.eqNom");
			tabEquipe[creerEquipe].dejaChoisie = false;
			tabEquipe[creerEquipe].points=0;
			creerEquipe++;
		}
		resEquipe = BD.executerSelect(connexion, "SELECT * FROM equipe");
		

		journeeRandom(tabEquipe);
		//Pour v�rifier si la randomisation est correct
		/*for (int i =0;i<16;i++){
			Ecran.afficher(tabEquipe[i].ID," match  n�",tabEquipe[i].match,"\n");
		}
		for (int i=0;i<16;i++){
			Ecran.afficher(tabEquipe[i].nom," : ",tabEquipe[i].match," \n");
		}*/
	    
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
			Ecran.afficher("Equipe n�",BD.attributString(resEquipe,"equipe.eqID"),"    nom : ", BD.attributString(resEquipe,"equipe.eqNom"));
			Ecran.sautDeLigne();
		}

	
		int quelMatch;
		char modif=' ';
		while(run){
			if (log.equals("admin")){
				do{
					Ecran.afficher("Voulez vous renseigner les r�sultats d'un match ( o : oui , n : non ) : \n");
					modif =  Clavier.saisirChar();
					if (modif != 'n'){
						do{
							Ecran.afficher("Quel match voulez vous renseigner? (de 1 � 15) \n");
							quelMatch =  Clavier.saisirInt();
							renseignerMatch(connexion,quelMatch,tabEquipe);
							/*for (int i=0;i<16;i++){
								Ecran.afficher(tabEquipe[i].nom," : ",tabEquipe[i].match," \n");
							}*/
							Ecran.afficher("Voulez vous renseigner un autre match? ( o : oui , n : non ) \n");
							modif = Clavier.saisirChar();
						}while(modif != 'n');
					}
				}while(modif != 'n');
			}
			Ecran.afficher("Statistiques :      Victoire(s)    d�faite(s)   �galit�(s)    but(s) marqu�(s)   but(s) encaiss�(s)   diff�rence de but\n");
			for(int i =1;i<17;i++){
				actualiserVariables(tabEquipe, i, connexion);
				Ecran.afficher("�quipe n�",i," ");
				if (i<10)
					Ecran.afficher("  ");
				Ecran.afficher(":            ",afficherNb(tabEquipe[i-1].nbVictoires),"                ",afficherNb(tabEquipe[i-1].nbDefaites),"                ",afficherNb(tabEquipe[i-1].nbEgalites),"                      ",afficherNb(tabEquipe[i-1].butM),"                        ",afficherNb(tabEquipe[i-1].butE),"                            ",afficherNb(tabEquipe[i-1].difButs)," \n");
			}
			Ecran.afficher("\n");
			Ecran.afficher("Voulez vous quitter l'application? (oui = o, non = n) \n");
			char quit = Clavier.saisirChar();
			if (quit=='o'){
				run=false;
			}
			
			
			
		}	
    }
  
}