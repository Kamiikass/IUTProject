package projetsouri;
import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
public class interfaces  extends JPanel implements MouseListener  {
	static int Compteurdeligne = 0;
	static char[][] monTableau ;
	private Image img; 
	
	public static void creertableaudonne() { 
	int cptligne=0; //la premiere ligne de tableau
	
			String fichier ="matriceprojet.txt";
			
//---------------------------------lecture du fichier texte	---------------------------------------------
			
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
				
				
//------------------------------------Remplir tabeau -----------------------------------------------------
				
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
	//--------------------------------INTERFACES--------------------------------------------------------------	
	 private Dimension dim = new Dimension(10,50);

	 private JPanel contenu = new JPanel(); // contenu
	
	//JButton[][] tab_button = new JButton[Compteurdeligne][Compteurdeligne];  
	 JLabel [][] tab_image = new JLabel[Compteurdeligne][Compteurdeligne];
	
	interfaces () {
		setLayout (new GridLayout (1, 1)); 	
		add(contenu, BorderLayout.CENTER);
		contenu.setLayout(new GridLayout (Compteurdeligne, Compteurdeligne)); 
     
	    for(int i = 0; i < Compteurdeligne; i++){
	    	 for(int j = 0; j < Compteurdeligne; j++){
	    		
	    		 if(monTableau[i][j]=='2'){
	    			 tab_image[i][j] =  new JLabel(new ImageIcon("bla.jpg"));
	    			 tab_image[i][j].setName("2");
	    		 }
	    		 else if(monTableau[i][j]=='1'){
	    			 tab_image[i][j] =  new JLabel(new ImageIcon("trait.jpg"));
	    			 tab_image[i][j].setName("1");
	    		 }
	    	              
	    		 else 
	    			 {
	    			 tab_image[i][j] =  new JLabel(new ImageIcon("vide.jpg"));
	    			 tab_image[i][j].setName("0");
	    			 }
	    		 tab_image[i][j].setPreferredSize(dim);
	    		 tab_image[i][j].setBackground(Color.white);
	    		 tab_image[i][j].addMouseListener(this);
		        contenu.add (tab_image[i][j]);
	    	 }
	    	 

	    }
	   /* for(int i = 0; i < Compteurdeligne; i++){
	    	 for(int j = 0; j < Compteurdeligne; j++){
	    		
	    		 if(monTableau[i][j]=='2'){
	    			 tab_button [i][j] =  new JButton(new ImageIcon("bla.jpg"));
	    			 tab_button [i][j].setName("2");
	    			 System.out.println(tab_button [i][j].getName());
	    		 }
	    		 else if(monTableau[i][j]=='1'){
	    			 tab_button [i][j] =  new JButton (new ImageIcon("trait.jpg"));
	    			 tab_button [i][j].setName("1");
	    			 System.out.println( tab_button [i][j].getName());
	    		 }
	    	              
	    		 else 
	    			 {
	    			 tab_button [i][j] =  new JButton(new ImageIcon("vide.jpg"));
	    			 tab_button [i][j].setName("0");
	    			 System.out.println(tab_button [i][j].getName());
	    			 }
	    		 //tab_button [i][j].setPreferredSize(dim);
	    		 //tab_button [i][j].setBackground(Color.white);
	    		 tab_button [i][j].addMouseListener(this);
		        contenu.add (tab_button [i][j]);
		        repaint();
	    	 }

	    }*/
	   
	}
	//-------------------------------------------MAIN------------------------------------------------------------
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
		//-------------------------------------------------------------------------------------------------
		System.out.println("comteur de ligne = "+ Compteurdeligne);
		JFrame f = new JFrame ("Tableau de donnee");
        interfaces i = new interfaces();
        
        f.add (i);
        f.setSize(218, 240);
        f.setVisible (true);
		
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		System.out.println(getName());
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
		
	}
	
}