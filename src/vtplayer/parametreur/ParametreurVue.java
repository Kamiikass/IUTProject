package vtplayer.parametreur;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author godeau
 */
public class ParametreurVue extends JPanel {
    /**
     * Demande de sauvegarde des paramètres.
     * @see ParametreurModele#save() 
     */
    public static final String ACTION_COMMAND_SAVE = "#SAVE";
    
    /**
     * On change d'index, appeler menu.setTest(menu.getSelectedItem()) pour mettre
     * Ã  jour la vue.
     * @see ParametreurVue#setMenuText(java.lang.String) 
     */
    public static final String ACTION_COMMAND_INDEX_CHANGE = "#CHANGE";

    /**
     * 
     * @author godeau
     */
    protected class Menu extends JPanel {
        /**
         * Liste des éléments a paramétré.
         */
        JComboBox<Integer> comboBox = new JComboBox();
        
        /**
         * Texte descriptif de l'élément a paramétré.
         */
        JLabel jLabel = new JLabel();
        
        /**
         * Sauvegarde les changements.
         */
        JButton jButton = new JButton("Sauvegarder");

        public Menu(ArrayList<Integer> arrayList) {
            setLayout(new BorderLayout());
            add(comboBox, BorderLayout.WEST);
            add(jLabel, BorderLayout.CENTER);
            add(jButton, BorderLayout.EAST);
            
            for (Integer i : arrayList) {
                comboBox.addItem(i);
            }
            
            jButton.setActionCommand(ACTION_COMMAND_SAVE);
            comboBox.setActionCommand(ACTION_COMMAND_INDEX_CHANGE);
        }
         
        public void setText(String txt){
            jLabel.setText(" " + txt);
        }
        
       public void  addActionListener(ActionListener l){
           comboBox.addActionListener(l);
           jButton.addActionListener(l);
       }
       
       public Integer getSelectedItem(){
           return (Integer) comboBox.getSelectedItem();
       }
    }
    
    ParametreurGridVue gridVue1 = new ParametreurGridVue();
    ParametreurGridVue gridVue2 = new ParametreurGridVue();
    Menu menu;

    public ParametreurVue(ParametreurModele parametreurModele) {
        setLayout(new BorderLayout());
        GridLayout gl = new GridLayout(1, 2);
        gl.setHgap(2);
        JPanel jp = new JPanel(gl);
        jp.add(gridVue1);
        jp.add(gridVue2);
        add(jp, BorderLayout.CENTER);
                
        menu = new Menu(parametreurModele.getKeyDescription());
        menu.setText(parametreurModele.getDescription(menu.getSelectedItem()));
        add(menu, BorderLayout.NORTH);
        
        setBytes(parametreurModele.getPicots(menu.getSelectedItem()));
    }

    @Override
    public synchronized void addMouseListener(MouseListener l) {
        super.addMouseListener(l);
        gridVue1.addMouseListener(l);
        gridVue2.addMouseListener(l);
    }
    
    public void  addActionListener(ActionListener l){
           menu.addActionListener(l);
       }
    
    public void setBytes(Byte bytes[]){
        gridVue1.setBytes(bytes[0], bytes[1]);
        gridVue2.setBytes(bytes[2], bytes[3]);
    }
    
    public Byte[] getBytes(){
        Byte b01[] = gridVue1.getBytes();
        Byte b23[] = gridVue2.getBytes();
        Byte ret[] = new Byte[4];
        ret[0] = b01[0];
        ret[1] = b01[1];
        ret[2] = b23[0];
        ret[3] = b23[1];

        return ret;
    }
    
    public Integer getMenuSelectedItem(){
        return menu.getSelectedItem();
    }
    
    public void setMenuText(String txt){
        menu.setText(txt);
    }
    
}
