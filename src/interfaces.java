import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.*;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
public class interfaces  extends JPanel{
	static int Compteurdeligne = 0;
	static char[][] monTableau ;
	//JButton[][] tab_button = new JButton[ Compteurdeligne][ Compteurdeligne]; 
	
	
	public static void creertableaudonne() { 
	int cptligne=0; //la premiere ligne de tableau
	
			String fichier ="matriceprojet.txt";
			//lecture du fichier texte	
//------------------------------------------------------------------------------
			
			try{
				InputStream ips=new FileInputStream(fichier); 
				
				InputStreamReader ipsr=new InputStreamReader(ips);
				BufferedReader br=new BufferedReader(ipsr);
				String ligne;
				while ((ligne=br.readLine())!=null) // compter le nombre de lignes de fichier.txt
				{
					
					Compteurdeligne+=1;
				}
				System.out.println("nombre de ligne : " + Compteurdeligne);
				
				
				
				InputStream ips2=new FileInputStream(fichier); 
				
				InputStreamReader ipsr2=new InputStreamReader(ips2);
				BufferedReader br2=new BufferedReader(ipsr2);
				
				
				//-----------------------------------------------------------------------------------------
				
				monTableau= new char [Compteurdeligne][Compteurdeligne]; // on crée un tableau 2 dimensions
				System.out.println("on a créé un tableau 2 dimensions dont la taille est "+Compteurdeligne );

				int cptcaractere;
				char vide=' ';
				
				while ((ligne=br2.readLine())!=null)
				{
					System.out.println("ligne caractere ........");
					System.out.println(ligne);
					cptcaractere=0;
					for (int i = 0; i< ligne.length(); i++)
					{  
						if(ligne.charAt(i)!=vide )
						{	
							monTableau[cptligne][cptcaractere]=ligne.charAt(i);
							cptcaractere+=1;		
						}
					}
					cptligne+=1;
				} 
				br.close(); 
			}		
			catch (Exception e){
				System.out.println(e.toString());
			}
	}
	//----------------------------------------------------------------------------------------------	
	 private Dimension dim = new Dimension(100,100);

	 private JPanel contenu = new JPanel(); // contenu
	// String[] tab_string = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0", ".", "=", "C", "+", "-", "*", "/"}; // les caracteres sont affichés
	 JButton[][] tab_button = new JButton[Compteurdeligne][Compteurdeligne];  // la longeur de table Button est égale à celle de table string
	interfaces () {
		setLayout (new GridLayout (1, 1));  // (rang, colone) on va diviser la calculatrice par 2 parties , un écran et un contenue
		
		//add(ecran, BorderLayout.NORTH);
		add(contenu, BorderLayout.CENTER);
		contenu.setLayout(new GridLayout (Compteurdeligne, Compteurdeligne)); // le contenu compose 4 rangs et 4 colones qui contient les chiffres et les opérations
	    for(int i = 0; i < Compteurdeligne; i++){
	    	 for(int j = 0; j < Compteurdeligne; j++){
	    		 
	 	        tab_button[i][j] = new JButton(""+monTableau[i][j]);
		        tab_button[i][j].setPreferredSize(dim);
		        contenu.add (tab_button[i][j]);
	    	 }

	    }

	}
	//-------------------------------------------------------------------------------------------------------
	public static void main (String args []) { 
	  creertableaudonne();
	  ////////////////////////////////////////////////////////////////////////
		System.out.println(":::::::::::::::TABLEAU :::::::::::::::::");
		
		for (int i=0;i< Compteurdeligne;i++)
		{
			System.out.println("-------------------");
			for (int j=0; j<Compteurdeligne;j++)
			{
				System.out.println(monTableau[i][j]);
				
			}
			
		}
		//////////////////////////////////////////////////
		System.out.println("comteur de ligne = "+ Compteurdeligne);
		JFrame f = new JFrame ("Tableau");
        interfaces i = new interfaces();
        f.add (i);
        f.setSize (500, 500);
        f.setVisible (true);
		
	}
	 	
}


