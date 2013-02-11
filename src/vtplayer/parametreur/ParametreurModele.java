package vtplayer.parametreur;

import vtplayer.VTPlayerException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import vtplayer.ByteUtilitaire;

/**
 *
 * @author godeau
 */
public class ParametreurModele {

    /**
     * Valeurs pour la quel tous les picots sont baisser.
     */
    public static final Byte[] NULLBYTES = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
    /**
     * Map des description associé a un numéro unique.
     */
    HashMap<Integer, String> desciption = new HashMap();
    /**
     * Map des bytes associé a une clé unique.
     */
    HashMap<Integer, Byte[]> picots = new HashMap();
    /**
     * Chemin par défaut ({@value #pathDescription}) vers le fichier de
     * description.<br/>
     * Le fichier ce compose, sur chaque ligne d'un numéro suivi d'une phrase
     * descriptive séparer par un délimiteur {@value #delimiteur}.<br/>
     * <u>Exemple de fichier valide :</u><br/> 1{@value #delimiteur}Porte
     * OU<br/> 2{@value #delimiteur}Porte ET<br/>
     */
    protected final static String pathDescription = "./description.ini";
    /**
     * Chemin par défaut ({@value #pathFilePicot}) vers le fichier de
     * configuration des picots. Le fichier est automatiquement généré grÃ¢ce a 
     * {@see #save()} ou {@see #save(java.lang.String)} (si vous voulez spécifier un chemin
     * particulier).
     * <u>Exemple de fichier valide :</u><br/>
     * 1{@value #delimiteur}b9{@value #delimiteur}5d{@value #delimiteur}99{@value #delimiteur}77<br/>
     * 2{@value #delimiteur}99{@value #delimiteur}77{@value #delimiteur}b9{@value #delimiteur}5d<br/><br/>
     * <strong><u>ATTENTION :</u></strong> ne modifier pas ce fichier a la main
     * a moins d'être sur de vous.
     */
    protected final static String pathFilePicot = "./default.ini";
    /**
     * Délimiteur de champ par défaut.
     */
    protected final static String delimiteur = "#";
    /**
     * Fichier de configuration des picots (choisi par l'utilisateur).
     */
    private String pathPicot;

    /**
     * Charge les fichier par défaut de description et de configuration des
     * picots.(Respectivement {@value #pathDescription} et
     * {@value #pathFilePicot}).
     *
     * @throws FileNotFoundException Si le fichier de de description n'est pas
     * trouver. Par contre un WARNING sera écrit sur le terminal pour le fichier
     * de configuration des picots.
     * @throws IOException erreur de lecture écriture.
     * @throws VTPlayerException erreur de conversion de byte.
     * @see #pathDescription
     * @see #pathFilePicot
     */
    public ParametreurModele() throws FileNotFoundException, IOException, VTPlayerException {
        this.pathPicot = pathFilePicot;
        loadDescription(desciption);
        try {
            loadPicotFile(picots);
        } catch (FileNotFoundException exception){
            Logger.getLogger(ParametreurModele.class.getName()).log(Level.WARNING, "Le fichier " + pathPicot + " n'a pu être trouver.");
       }
    }

    /**
     * Charge les fichier spécifier.
     * 
     * @param pathPicot chemin vers le fichier de configuration des picots.
     * Ce chemin est garder a en mémoire pour l'appel de {@see #save()}.
     * @param pathDescription chemin vers le fichier de description. {@see #pathDescription}.
     * @param onlyWarning si est a vrai, un WARNING sera écrit sur le terminal pour le fichier
     * de configuration des picots (si celui-ci n'est pas trouver). Sinon l'erreur sera reporter.
     * @throws FileNotFoundException si l'un des fichier n'est pas trouver.
     * @throws IOException erreur de lecture écriture.
     * @throws VTPlayerException erreur de conversion de byte.
     */
    public ParametreurModele(String pathPicot, String pathDescription, boolean onlyWarning) throws FileNotFoundException, IOException, VTPlayerException {
        this.pathPicot = pathPicot;
        loadDescription(desciption, pathDescription);
        try {
            loadPicotFile(picots, pathPicot);
        } catch (FileNotFoundException exception){
            if (onlyWarning){
                Logger.getLogger(ParametreurModele.class.getName()).log(Level.WARNING, "Le fichier " + pathPicot + " n'a pu être trouver.");
            } else {
                throw exception;
            }
       }
    }

    /**
     * Charge le fichier de configuration des picots par défaut {@value #pathFilePicot}.
     * 
     * @param hashMap map a remplir avec le contenu du fichier.
     * @throws IOException erreur lors de la lecture du fichier.
     * @throws FileNotFoundException fichier non trouver.
     * @throws VTPlayerException erreur lors de la conversion d'un byte.
     * @see #loadDescription(java.util.HashMap, java.lang.String) 
     */
    public static void loadPicotFile(HashMap<Integer, Byte[]> hashMap) throws IOException, VTPlayerException, FileNotFoundException {
        loadPicotFile(hashMap, pathFilePicot);
    }

    /**
     * Charge le fichier de configuration des picots spécifier en paramètre.
     * 
     * @param hashMap map a remplir avec le contenu du fichier.
     * @param path chemin vers le fichier.
     * @throws IOException erreur lors de la lecture du fichier.
     * @throws FileNotFoundException fichier non trouver.
     * @throws VTPlayerException erreur lors de la conversion d'un byte.
     * @see #loadDescription(java.util.HashMap) 
     */
    public static void loadPicotFile(HashMap<Integer, Byte[]> hashMap, String path) throws IOException, FileNotFoundException, VTPlayerException {
        Integer key;
        String ligne;

        InputStream ips = new FileInputStream(path);
        InputStreamReader isr = new InputStreamReader(ips);

        try (BufferedReader buff = new BufferedReader(isr)) {
            // lecture du fichier ligne par ligne
            while ((ligne = buff.readLine()) != null) {
                // si la ligne n'est pas vide
                if (!ligne.isEmpty()) {
                    // on récupére les éléments
                    String str[] = ligne.split(delimiteur);
                    // si le nombre délément est correct
                    if (str.length == 5) {
                        // on récupère la clé
                        key = Integer.valueOf(str[0]);
                        // on converti les quatres bytes
                        Byte bytes[] = new Byte[4];
                        for (int i = 1; i < 5; i++) {
                            bytes[i - 1] = ByteUtilitaire.string_hexa_to_byte(str[i]);
                        }
                        // on l'ajout a la map
                        hashMap.put(key, bytes);
                    }
                }
            }
        }
    }

    /**
     * 
     * @param hashMap
     * @throws FileNotFoundException
     * @throws IOException 
     */
    static void loadDescription(HashMap<Integer, String> hashMap) throws FileNotFoundException, IOException {
        loadDescription(hashMap, pathDescription);
    }

    /**
     * 
     * @param hashMap
     * @param path
     * @throws FileNotFoundException
     * @throws IOException 
     */
    static void loadDescription(HashMap<Integer, String> hashMap, String path) throws FileNotFoundException, IOException {
        String ligne;
        Integer key;

        InputStream ips = new FileInputStream(path);
        InputStreamReader isr = new InputStreamReader(ips);

        // lecture du fichier ligne par ligne
        try (BufferedReader buff = new BufferedReader(isr)) {
            while ((ligne = buff.readLine()) != null) {
                if (!ligne.isEmpty()) {
                    String str[] = ligne.split(delimiteur);
                    if (str.length == 2) {
                        key = Integer.valueOf(str[0]);
                        hashMap.put(key, str[1]);
                    }
                }
            }
        }
    }

    /**
     * 
     * @return 
     */
    ArrayList<Integer> getKeyDescription() {
        ArrayList<Integer> ret = new ArrayList();

        for (Map.Entry<Integer, String> entry : desciption.entrySet()) {
            ret.add(entry.getKey());
        }

        return ret;
    }

    /**
     * 
     * @param index
     * @return 
     */
    public String getDescription(Integer index) {
        return desciption.get(index);
    }

    /**
     *
     * @param index
     * @return les bytes charger si il sont présent dans Map sinon NULLBYTES 
     * ({@see #NULLBYTES}).
     */
    public Byte[] getPicots(Integer index) {
        Byte[] bytes = picots.get(index);
        if (bytes == null) {
            bytes = NULLBYTES;
        }
        return bytes;
    }

    /**
     * 
     * @param index
     * @param bytes 
     */
    public void setPicots(Integer index, Byte bytes[]) {
        picots.put(index, bytes);
    }

    /**
     * 
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public void save() throws FileNotFoundException, IOException {
        save(pathPicot);
    }

    /**
     * 
     * @param path
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public void save(String path) throws FileNotFoundException, IOException {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(path)))) {
            for (Map.Entry<Integer, Byte[]> entry : picots.entrySet()) {
                Integer integer = entry.getKey();
                Byte[] bytes = entry.getValue();
                pw.printf("%d", integer);
                for (int i = 0; i < 4; i++) {
                    pw.printf(delimiteur + "%x", bytes[i]);
                }
                pw.println();
            }
        }
    }
}
