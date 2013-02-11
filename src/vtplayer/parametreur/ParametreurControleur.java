package vtplayer.parametreur;

import vtplayer.VTPlayer;
import vtplayer.VTPlayerException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * 
 * @author godeau
 */
public class ParametreurControleur extends MouseAdapter implements ActionListener {

    ParametreurModele parametreurModele;
    ParametreurVue parametreurVue;
    VTPlayer vtp = null;

    public ParametreurControleur(ParametreurModele parametreurModele, ParametreurVue parametreurVue) {
        this.parametreurModele = parametreurModele;
        this.parametreurVue = parametreurVue;
    }

    public ParametreurControleur(ParametreurModele parametreurModele, ParametreurVue parametreurVue, VTPlayer vtp) throws VTPlayerException {
        this.vtp = vtp;
        this.parametreurModele = parametreurModele;
        this.parametreurVue = parametreurVue;
        if(vtp != null && vtp.isOpen()){
            vtp.set(parametreurModele.getPicots(parametreurVue.getMenuSelectedItem()));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case ParametreurVue.ACTION_COMMAND_SAVE:
                try {
                    // on demande au modele de se sauvegarder
                    parametreurModele.save();
                } catch (FileNotFoundException ex) {
                    JOptionPane.showMessageDialog(null, "Un fichier n'a pas pu être trouver (ou créer). Consulter vos log pour en savoir plus.", "Erreur fichier", JOptionPane.ERROR_MESSAGE);
                    Logger.getLogger(ParametreurControleur.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Erreur de lecture écriture avec un fichier. Consulter vos log pour en savoir plus.", "Erreur fichier", JOptionPane.ERROR_MESSAGE);
                    Logger.getLogger(ParametreurControleur.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;

            case ParametreurVue.ACTION_COMMAND_INDEX_CHANGE:
                // on récupère le nouvelle index
                int index = parametreurVue.getMenuSelectedItem();
                
                // on récupère les bytes associé a cette index
                Byte bytes[] = parametreurModele.getPicots(index);
                
                // on met a jour la vue
                parametreurVue.setMenuText(parametreurModele.getDescription(index));
                parametreurVue.setBytes(bytes);
                
                // si la souris est brancher on léve les picots
                if (vtp != null && vtp.isOpen()) {
                    try {
                        vtp.set(bytes);
                    } catch (VTPlayerException ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage(), "Erreur communication", JOptionPane.ERROR_MESSAGE);
                        Logger.getLogger(ParametreurControleur.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                // on demande a la vue de se redessiner
                parametreurVue.repaint();
                break;

        }

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Object o = e.getSource();

        if (o instanceof ParametreurGridVue.Picot) {
            // mise a jour de la vue
            ((ParametreurGridVue.Picot) o).setBit();

            // on récupère les bytes
            Byte bytes[] = parametreurVue.getBytes();
            
            // mise a jour du modele
            parametreurModele.setPicots(parametreurVue.getMenuSelectedItem(), bytes);

            // Il la souris est brancher on met les picots comme sur l'écran
            if (vtp != null && vtp.isOpen()) {
                try {
                    vtp.set(bytes);
                } catch (VTPlayerException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Erreur communication", JOptionPane.ERROR_MESSAGE);
                    Logger.getLogger(ParametreurControleur.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        parametreurVue.repaint();
    }
}
