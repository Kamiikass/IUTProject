package vtplayer;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe permettant la liaison entre java et la souris (n'est compatible
 * uniquement que avec linux).
 * <br />
 * Les exceptions de cette classe sont a traité ou a afficher Ã  l'utilisateur.
 * <br />
 * <strong><u>ATTENTION :</u></strong> le déplacement de cette classe dans un
 * autre package ou dans un autre sous package peut empêcher le bon
 * fonctionnement de celle-ci.
 * <br />
 * Pour en savoir plus sur le fonctionnement de la souris : {@link <a href="http://vtplayer.sourceforge.net/">VTPlayer</a>}
 * @author godeau
 */
public class VTPlayer {

    /**
     * code d'erreur lors de l'obtention de la classe en C.
     */
    private final static int ERROR_CLASS_NOT_FOUND = -4;
    /**
     * code d'erreur si le chant <i>fd</i> n'a pas pu être trouver.
     */
    private final static int ERROR_FIELD_NOT_FOUND = -3;
    /**
     * code d'erreur si l'obtention du <i>fd</i> a échouer.
     */
    private final static int ERROR_GET_FIELD_FAILED = -2;
    /**
     * Descripteur de fichier vers le périphérique.
     */
    private FileDescriptor vtPlayer = null;

    /**
     * Permet d'ouvrir le périphérique.
     *
     * @param device chemin vers le périphérique a ouvrir.
     * @return le descripteur vers le fichier périphérique ou nul en cas
     * d'échec.
     */
    private static native FileDescriptor vtplayer_open(String device);

    /**
     * Permet de lever les picots selon les bytes en paramètre.
     *
     * @param handle descripteur de fichier vers le périphérique.
     * @param b1, b2, b3, b4 byte permettent de lever les picots.
     * @return un nombre négatif en cas d'erreur sinon un nombre positif.
     */
    private static native int vtplayer_set(FileDescriptor handle, byte b1, byte b2, byte b3, byte b4);

    /**
     * Permet la fermeture du périphérique.
     *
     * @param handle descripteur de fichier vers le périphérique.
     * @return un nombre négatif en cas d'erreur sinon un nombre positif.
     */
    private static native int vtplayer_close(FileDescriptor handle);

    /**
     * Essai de chargement de la librairie.
     * @trows UnsatisfiedLinkError si la librairie n'a pu être charger.
     */
    static {
        try{
        System.loadLibrary("libJNI");
        }catch (UnsatisfiedLinkError | NullPointerException exception){
            Logger.getLogger(VTPlayer.class.getName()).log(Level.WARNING, "La librairie n'a pu être trouvez dans \"java.library.path\".");
            try {
                VTPlayer.loadLibraryFromJar("/vtplayer/libJNI.so");
            } catch (IOException ex) {
                Logger.getLogger(VTPlayer.class.getName()).log(Level.SEVERE, "La librairie n'a pas pus être extraite du fichier JAR.", ex);
                throw new UnsatisfiedLinkError("La librairie nécessaire au fonctionnement de la souris n'a pu être trouvez.");
            }
        }
    }

    /**
     * Instancie seulement la classe nécessite l'appel de {@see #open(java.lang.String)} ou de {@see #open(java.io.File)} 
     * pour pouvoir ensuite être fonctionnel.
     */
    public VTPlayer() {
    }

    /**
     * Permet d'ouvrir directement directement le périphérique.
     *
     * @param device chemin vers le fichier périphérique.
     * @throws VTPlayerException dans le cas d'échec d'ouverture du
     * périphérique.
     * @see #findDevice() 
     * @see #findDevice(java.lang.String) 
     * @see #findDevices() 
     * @see #findDevices(java.lang.String) 
     */
    public VTPlayer(String device) throws VTPlayerException {
        open(device);
    }
    
    /**
     * Permet d'ouvrir directement directement le périphérique.
     * @param file fichier périphérique.
     * @throws VTPlayerException dans le cas d'échec d'ouverture du
     * périphérique.
     * @see #findDevice() 
     * @see #findDevice(java.lang.String) 
     * @see #findDevices() 
     * @see #findDevices(java.lang.String) 
     */
    public VTPlayer(File file) throws VTPlayerException{
        if(file != null){
            open(file);
        }
    }

    /**
     * Permet d'ouvrir une périphérique donner.<br />
     * <strong><u>ATTENTION :</u></strong> penser a vérifier que l'utilisateur a
     * bien les droit sur le périphérique.
     * 
     * @param device chemin vers le périphérique.
     * @throws VTPlayerException si l'ouverture a échouer. Cette exception est
     * de préférence a afficher a l'utilisateur
     * @see #findDevice() 
     * @see #findDevice(java.lang.String) 
     * @see #findDevices() 
     * @see #findDevices(java.lang.String) 
     */
    public void open(String device) throws VTPlayerException {
        // fermeture du périphérique si celui ci est encore ouvert.
        if (isOpen()) {
            close();
        }

        // ouverture du périphérique grace au fonction native.
        vtPlayer = vtplayer_open(device);

        // vérification que l'ouverture c'est bien dérouler.
        if (!isOpen()) {
            throw new VTPlayerException(
                    "Échec de l'ouverure du périphérique, veuillez vérifier :\n"
                    + " - Que le périphérique est bien brancher.\n"
                    + " - Que le module et bien charger dans le noyau \"lsmod | grep vtplayer\".\n"
                    + " - Que la souris a correctement été monter par le noyau \"dsmeg | tail -5\".\n"
                    + " - Que le lien et les droits sont bien attribuer a votre périphérique \"ls -l /dev/vtplayer*\".");
        }
    }
    
    /**
     * Permet d'ouvrir une périphérique donner.<br />
     *
     * @param file fichier périphérique.
     * @throws VTPlayerException si l'ouverture a échouer ou si l'on ne peut écrire sur le fichier. 
     * Cette exception est de préférence a afficher a l'utilisateur.
     * @see #findDevice() 
     * @see #findDevice(java.lang.String) 
     * @see #findDevices() 
     * @see #findDevices(java.lang.String) 
     */
    public void open(File file) throws VTPlayerException {
        if(!file.canWrite()){
            throw new VTPlayerException("Erreur vous n'avez pas le droit décriture sur votre périphérique, tapez : \"" 
                    + "sudo chmod 622 " 
                    + file.getAbsolutePath() + " pour résoudre le problème.");
        }
        open(file.getAbsolutePath());
    }

    /**
     * Permet de vérifier si le périphérique est bien ouvert. <br/>
     * <strong><u>ATTENTION :</u></strong> ne garantie pas que le périphérique
     * est branché.
     *
     * @return vrai si le périphérique est ouvert faux sinon.
     */
    public boolean isOpen() {
        return vtPlayer != null && vtPlayer.valid();
    }

    

    /**
     * Léve est abaisse les picots.
     *
     * @param bytes chaÃ®ne de byte séparer par des espaces (au maximun 4 bytes).
     * @throws VTPlayerException Si la chaÃ®ne est composer de trop de byte, ou
     * si l'un des byte est invalide.
     * @see #set(java.lang.String, java.lang.String)
     * @see #set(java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String)
     * @see #set(byte[])
     * @see #set(byte, byte, byte, byte)
     */
    public void set(String bytes) throws VTPlayerException {
        set(bytes, " ");
    }

    /**
     * Léve est abaisse les picots. Revient a appeler <i>set(String b1, String
     * b2, String b3, String b4)</i>.
     *
     * @param bytes chaÃ®ne de byte séparer par un séparateur.
     * @param separateur caractères séparent chaque bytes
     * @throws VTPlayerException Si la chaÃ®ne est composer de trop de byte, ou
     * si l'un des byte est invalide.
     * @see #set(java.lang.String)
     * @see #set(java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String)
     * @see #set(byte[])
     * @see #set(byte, byte, byte, byte)
     */
    public void set(String bytes, String separateur) throws VTPlayerException {
        String array[] = bytes.split(separateur);
        if (array.length != 4) {
            throw new VTPlayerException("Erreur, le nombre de byte composent la chaine \"" + bytes + "\" n'est pas de quatre.");
        }
        set(array[0], array[1], array[2], array[3]);
    }

    /**
     * Léve est abaisse les picots. Revient a appeler <i>set(stringToByte(b1),
     * stringToByte(b2), stringToByte(b3), stringToByte(b4))</i>.
     *
     * @param b1, b2, b3, b4 chaine de caractére a convertir en byte.
     * @throws VTPlayerException si l'un des byte est invalide.
     * @see #set(java.lang.String)
     * @see #set(java.lang.String, java.lang.String)
     * @see #set(byte[])
     * @see #set(byte, byte, byte, byte)
     */
    public void set(String b1, String b2, String b3, String b4) throws VTPlayerException {
        set(ByteUtilitaire.string_hexa_to_byte(b1), ByteUtilitaire.string_hexa_to_byte(b2), ByteUtilitaire.string_hexa_to_byte(b3), ByteUtilitaire.string_hexa_to_byte(b4));
    }

    /**
     * Léve est abaisse les picots.
     *
     * @param buffer tableau d'auplus quatre bytes.
     * @throws VTPlayerException Si la liaison avec le périphérique échoue.
     * @see #set(java.lang.String)
     * @see #set(java.lang.String, java.lang.String)
     * @see #set(java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String)
     * @see #set(byte, byte, byte, byte)
     */
    public void set(Byte buffer[]) throws VTPlayerException {
        if (buffer.length != 4) {
            throw new VTPlayerException("Erreur, trop de byte on été passer en paramétre.");
        }
        set(buffer[0], buffer[1], buffer[2], buffer[3]);
    }

    /**
     * Léve est abaisse les picots.
     *
     * @param b1, b2, b3, b4 byte permettent de définir les picots a lever et a
     * baisse.
     * @throws VTPlayerException Si le périphérique n'est pas ouvert ou si un
     * problème est reporter depuis jni.
     * @see #set(java.lang.String)
     * @see #set(java.lang.String, java.lang.String)
     * @see #set(java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String)
     * @see #set(byte[])
     */
    public void set(byte b1, byte b2, byte b3, byte b4) throws VTPlayerException {
        if (!isOpen()) {
            throw new VTPlayerException("Échec le périphérique n'est pas ouvert. Veuillez utiliser open avant l'appel de set.");
        }
        int ret = vtplayer_set(vtPlayer, b1, b2, b3, b4);
        switch (ret) {
            case ERROR_CLASS_NOT_FOUND:
                throw new VTPlayerException("La classe FileDescriptor de java n'a pu être trouver dans jni.");

            case ERROR_FIELD_NOT_FOUND:
                throw new VTPlayerException("La classe FileDescriptor ne comporte pas le champs demander dans jni.");

            case ERROR_GET_FIELD_FAILED:
                throw new VTPlayerException("L'obtention du descripteur de fichier Ã  échouer.");

            default:
                if (ret < 0) {
                    throw new VTPlayerException(
                            "L'écriture sur le périphérique a échouer, veuillez vérifier :\n"
                            + " - Que le périphérique est bien brancher.\n"
                            + " - Que le module et bien charger dans le noyau \"lsmod | grep vtplayer\".\n"
                            + " - Que la souris a correctement été monter par le noyau \"dsmeg | tail -5\".\n"
                            + " - Que le lien et les droits sont bien attribuer a votre périphérique \"ls -l /dev/vtplayer*\".");
                }
        }
    }

    /**
     * Recherche dans le répertoire <i>/dev/</i> le fichier susceptible d'être la souris.
     * Revient a appeler <i>findDevice("/dev/")</i>. <br />
     * <strong><i>ATTENTION :</i></strong> Aucun garanti n'est donner au faite 
     * que le fichier retourner soit le bon. En effet le premier fichier trouver, 
     * répondant au critère, est immédiatement retourner.
     * @return fichier vers le périphérique ou nul si aucun fichier ne correspond au critère.
     * @see #findDevice(java.lang.String)
     * @see #findDevices()
     * @see #findDevices(java.lang.String)
     */
    public static File findDevice() {
        return findDevice("/dev/");
    }

    /**
     * Recherche dans le répertoire dans le répertoire spécifier tout fichier susceptible d'être la souris.<br />
     * <strong><i>ATTENTION :</i></strong> Aucun garanti n'est donner au faite 
     * que le fichier retourner soit le bon. En effet le premier fichier trouver, 
     * répondant au critère, est immédiatement retourner.
     * @param dir répertoire de recherche.
     * @return fichier vers le périphérique ou nul si aucun fichier ne correspond au critère.
     * @see #findDevice() 
     * @see #findDevices() 
     * @see #findDevices(java.lang.String) 
     */
    public static File findDevice(String dir) {
        File listOfFiles[] = new File(dir).listFiles();
        boolean find = false;
        int cpt = 0;

        while (!find && cpt < listOfFiles.length) {
            if (listOfFiles[cpt].getName().startsWith("vtplayer")) {
                find = true;
            }
            cpt++;
        }

        if (find) {
            return listOfFiles[cpt - 1];
        }

        return null;
    }

    /**
     * Recherche dans le répertoire dans le répertoire spécifier tout fichier susceptible d'être la souris.
     * Revient a appeler <i>findDevices("/dev/")</i>. <br/>
     * <strong><i>ATTENTION :</i></strong> Aucun garanti n'est donner au faite 
     * que la liste de fichier retourner soit le bonnes.
     * @return liste des fichiers validant les critères.
     * @see #findDevice() 
     * @see #findDevice(java.lang.String) 
     * @see #findDevices(java.lang.String) 
     */
    public static ArrayList<File> findDevices() {
        return findDevices("/dev/");
    }

    /**
     * Recherche dans le répertoire dans le répertoire spécifier tout fichier susceptible d'être la souris.<br />
     * <strong><i>ATTENTION :</i></strong> Aucun garanti n'est donner au faite 
     * que la liste de fichier retourner soit le bonnes.
     * @param dir répertoire de recherche.
     * @return liste des fichiers validant les critères.
     * @see #findDevice() 
     * @see #findDevice(java.lang.String) 
     * @see #findDevices() 
     */
    public static ArrayList<File> findDevices(String dir) {
        ArrayList<File> path = new ArrayList();

        for (File file : new File(dir).listFiles()) {
            if (file.getName().startsWith("vtplayer")) {
                path.add(file);
            }
        }

        return path;
    }
    
    /**
     * Charges la librairie depuis le fichier JAR.
     * <br/>
     * Le fichier contenu dans le JAR et copier dans un répertoire temporaire avant d'être charger.
     * Le répertoire est supprimer aprés le chargement de la librairie.
     * 
     * @param filename Le nom du fichier a l'intérieur du JAR. Doit être un chemin absolue (commence par '/'), ex. /package/File.ext
     * @throws IOException si la création du répertoire temporaire échoue ou si les opérations de lecture/écriture échoue.
     * @throws IllegalArgumentException si le fichier source (filename argument) n'existe pas 
     * @throws IllegalArgumentException si le chemin n'est pas absolue ou si le nom de fichier et inférieur a trois caractère (restriction du a {@see File#createTempFile(java.lang.String, java.lang.String)})
     */
    private static void loadLibraryFromJar(String path) throws IOException {
 
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("Le chemin doit être absolue (commance par '/').");
        }
 
        // Récupère le nom du fichier
        String[] parts = path.split("/");
        String filename = (parts.length > 1) ? parts[parts.length - 1] : null;
 
        // Séparation du nom du fichier et de son extension
        String prefix = "";
        String suffix = null;
        if (filename != null) {
            parts = filename.split("\\.", 2);
            prefix = parts[0];
            suffix = (parts.length > 1) ? "."+parts[parts.length - 1] : null; // Thanks, davs! :-)
        }
 
        // Vérifie si le nom de fichier est ok
        if (filename == null || prefix.length() < 3) {
            throw new IllegalArgumentException("le nom de fichier doit contenir au moins trois caractère.");
        }
 
        // Création du répertoire temporaire
        File temp = File.createTempFile(prefix, suffix);
        temp.deleteOnExit();
 
        if (!temp.exists()) {
            throw new FileNotFoundException("Le fichier " + temp.getAbsolutePath() + " n'existe pas.");
        }
 
        // Préparation du buffer pour la copie
        byte[] buffer = new byte[1024];
        int readBytes;
 
        // Ouverure et vérificaion du flux d'entré
        InputStream is = VTPlayer.class.getResourceAsStream(path);
        if (is == null) {
            throw new FileNotFoundException("Le fichier " + path + " n'a pas pu être trouvez dans le JAR.");
        }
 
        // Ouverture du flus de sortie et copie des donnée
        OutputStream os = new FileOutputStream(temp);
        try {
            while ((readBytes = is.read(buffer)) != -1) {
                os.write(buffer, 0, readBytes);
            }
        } finally {
            // SI la lecture/écriture échou, on ferme les flus avant de lancer une exception
            os.close();
            is.close();
        }
 
        // Finallement, chargement de la librairie
        System.load(temp.getAbsolutePath());
    }

    /**
     * Fermeture du périphérique.
     * @throws VTPlayerException si la fermeture du périphérique échoue
     */
    public void close() throws VTPlayerException {
        if (isOpen()) {
            int ret = vtplayer_close(vtPlayer);
            switch (ret) {
            case ERROR_CLASS_NOT_FOUND:
                throw new VTPlayerException("La classe FileDescriptor de java n'a pu être trouver dans jni.");

            case ERROR_FIELD_NOT_FOUND:
                throw new VTPlayerException("La classe FileDescriptor ne comporte pas le champs demander dans jni.");

            case ERROR_GET_FIELD_FAILED:
                throw new VTPlayerException("L'obtention du descripteur de fichier Ã  échouer.");

            default:
                if (ret < 0) {
                    throw new VTPlayerException("La fermeture du périphérique a échouer.");
                }
        }
            vtPlayer = null;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize(); //To change body of generated methods, choose Tools | Templates.
    }
}
