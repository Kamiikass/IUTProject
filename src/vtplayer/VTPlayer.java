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
 * Les exceptions de cette classe sont a trait� ou a afficher à l'utilisateur.
 * <br />
 * <strong><u>ATTENTION :</u></strong> le d�placement de cette classe dans un
 * autre package ou dans un autre sous package peut emp�cher le bon
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
     * code d'erreur si le chant <i>fd</i> n'a pas pu �tre trouver.
     */
    private final static int ERROR_FIELD_NOT_FOUND = -3;
    /**
     * code d'erreur si l'obtention du <i>fd</i> a �chouer.
     */
    private final static int ERROR_GET_FIELD_FAILED = -2;
    /**
     * Descripteur de fichier vers le p�riph�rique.
     */
    private FileDescriptor vtPlayer = null;

    /**
     * Permet d'ouvrir le p�riph�rique.
     *
     * @param device chemin vers le p�riph�rique a ouvrir.
     * @return le descripteur vers le fichier p�riph�rique ou nul en cas
     * d'�chec.
     */
    private static native FileDescriptor vtplayer_open(String device);

    /**
     * Permet de lever les picots selon les bytes en param�tre.
     *
     * @param handle descripteur de fichier vers le p�riph�rique.
     * @param b1, b2, b3, b4 byte permettent de lever les picots.
     * @return un nombre n�gatif en cas d'erreur sinon un nombre positif.
     */
    private static native int vtplayer_set(FileDescriptor handle, byte b1, byte b2, byte b3, byte b4);

    /**
     * Permet la fermeture du p�riph�rique.
     *
     * @param handle descripteur de fichier vers le p�riph�rique.
     * @return un nombre n�gatif en cas d'erreur sinon un nombre positif.
     */
    private static native int vtplayer_close(FileDescriptor handle);

    /**
     * Essai de chargement de la librairie.
     * @trows UnsatisfiedLinkError si la librairie n'a pu �tre charger.
     */
    static {
        try{
        System.loadLibrary("libJNI");
        }catch (UnsatisfiedLinkError | NullPointerException exception){
            Logger.getLogger(VTPlayer.class.getName()).log(Level.WARNING, "La librairie n'a pu �tre trouvez dans \"java.library.path\".");
            try {
                VTPlayer.loadLibraryFromJar("/vtplayer/libJNI.so");
            } catch (IOException ex) {
                Logger.getLogger(VTPlayer.class.getName()).log(Level.SEVERE, "La librairie n'a pas pus �tre extraite du fichier JAR.", ex);
                throw new UnsatisfiedLinkError("La librairie n�cessaire au fonctionnement de la souris n'a pu �tre trouvez.");
            }
        }
    }

    /**
     * Instancie seulement la classe n�cessite l'appel de {@see #open(java.lang.String)} ou de {@see #open(java.io.File)} 
     * pour pouvoir ensuite �tre fonctionnel.
     */
    public VTPlayer() {
    }

    /**
     * Permet d'ouvrir directement directement le p�riph�rique.
     *
     * @param device chemin vers le fichier p�riph�rique.
     * @throws VTPlayerException dans le cas d'�chec d'ouverture du
     * p�riph�rique.
     * @see #findDevice() 
     * @see #findDevice(java.lang.String) 
     * @see #findDevices() 
     * @see #findDevices(java.lang.String) 
     */
    public VTPlayer(String device) throws VTPlayerException {
        open(device);
    }
    
    /**
     * Permet d'ouvrir directement directement le p�riph�rique.
     * @param file fichier p�riph�rique.
     * @throws VTPlayerException dans le cas d'�chec d'ouverture du
     * p�riph�rique.
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
     * Permet d'ouvrir une p�riph�rique donner.<br />
     * <strong><u>ATTENTION :</u></strong> penser a v�rifier que l'utilisateur a
     * bien les droit sur le p�riph�rique.
     * 
     * @param device chemin vers le p�riph�rique.
     * @throws VTPlayerException si l'ouverture a �chouer. Cette exception est
     * de pr�f�rence a afficher a l'utilisateur
     * @see #findDevice() 
     * @see #findDevice(java.lang.String) 
     * @see #findDevices() 
     * @see #findDevices(java.lang.String) 
     */
    public void open(String device) throws VTPlayerException {
        // fermeture du p�riph�rique si celui ci est encore ouvert.
        if (isOpen()) {
            close();
        }

        // ouverture du p�riph�rique grace au fonction native.
        vtPlayer = vtplayer_open(device);

        // v�rification que l'ouverture c'est bien d�rouler.
        if (!isOpen()) {
            throw new VTPlayerException(
                    "�chec de l'ouverure du p�riph�rique, veuillez v�rifier :\n"
                    + " - Que le p�riph�rique est bien brancher.\n"
                    + " - Que le module et bien charger dans le noyau \"lsmod | grep vtplayer\".\n"
                    + " - Que la souris a correctement �t� monter par le noyau \"dsmeg | tail -5\".\n"
                    + " - Que le lien et les droits sont bien attribuer a votre p�riph�rique \"ls -l /dev/vtplayer*\".");
        }
    }
    
    /**
     * Permet d'ouvrir une p�riph�rique donner.<br />
     *
     * @param file fichier p�riph�rique.
     * @throws VTPlayerException si l'ouverture a �chouer ou si l'on ne peut �crire sur le fichier. 
     * Cette exception est de pr�f�rence a afficher a l'utilisateur.
     * @see #findDevice() 
     * @see #findDevice(java.lang.String) 
     * @see #findDevices() 
     * @see #findDevices(java.lang.String) 
     */
    public void open(File file) throws VTPlayerException {
        if(!file.canWrite()){
            throw new VTPlayerException("Erreur vous n'avez pas le droit d�criture sur votre p�riph�rique, tapez : \"" 
                    + "sudo chmod 622 " 
                    + file.getAbsolutePath() + " pour r�soudre le probl�me.");
        }
        open(file.getAbsolutePath());
    }

    /**
     * Permet de v�rifier si le p�riph�rique est bien ouvert. <br/>
     * <strong><u>ATTENTION :</u></strong> ne garantie pas que le p�riph�rique
     * est branch�.
     *
     * @return vrai si le p�riph�rique est ouvert faux sinon.
     */
    public boolean isOpen() {
        return vtPlayer != null && vtPlayer.valid();
    }

    

    /**
     * L�ve est abaisse les picots.
     *
     * @param bytes chaîne de byte s�parer par des espaces (au maximun 4 bytes).
     * @throws VTPlayerException Si la chaîne est composer de trop de byte, ou
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
     * L�ve est abaisse les picots. Revient a appeler <i>set(String b1, String
     * b2, String b3, String b4)</i>.
     *
     * @param bytes chaîne de byte s�parer par un s�parateur.
     * @param separateur caract�res s�parent chaque bytes
     * @throws VTPlayerException Si la chaîne est composer de trop de byte, ou
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
     * L�ve est abaisse les picots. Revient a appeler <i>set(stringToByte(b1),
     * stringToByte(b2), stringToByte(b3), stringToByte(b4))</i>.
     *
     * @param b1, b2, b3, b4 chaine de caract�re a convertir en byte.
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
     * L�ve est abaisse les picots.
     *
     * @param buffer tableau d'auplus quatre bytes.
     * @throws VTPlayerException Si la liaison avec le p�riph�rique �choue.
     * @see #set(java.lang.String)
     * @see #set(java.lang.String, java.lang.String)
     * @see #set(java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String)
     * @see #set(byte, byte, byte, byte)
     */
    public void set(Byte buffer[]) throws VTPlayerException {
        if (buffer.length != 4) {
            throw new VTPlayerException("Erreur, trop de byte on �t� passer en param�tre.");
        }
        set(buffer[0], buffer[1], buffer[2], buffer[3]);
    }

    /**
     * L�ve est abaisse les picots.
     *
     * @param b1, b2, b3, b4 byte permettent de d�finir les picots a lever et a
     * baisse.
     * @throws VTPlayerException Si le p�riph�rique n'est pas ouvert ou si un
     * probl�me est reporter depuis jni.
     * @see #set(java.lang.String)
     * @see #set(java.lang.String, java.lang.String)
     * @see #set(java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String)
     * @see #set(byte[])
     */
    public void set(byte b1, byte b2, byte b3, byte b4) throws VTPlayerException {
        if (!isOpen()) {
            throw new VTPlayerException("�chec le p�riph�rique n'est pas ouvert. Veuillez utiliser open avant l'appel de set.");
        }
        int ret = vtplayer_set(vtPlayer, b1, b2, b3, b4);
        switch (ret) {
            case ERROR_CLASS_NOT_FOUND:
                throw new VTPlayerException("La classe FileDescriptor de java n'a pu �tre trouver dans jni.");

            case ERROR_FIELD_NOT_FOUND:
                throw new VTPlayerException("La classe FileDescriptor ne comporte pas le champs demander dans jni.");

            case ERROR_GET_FIELD_FAILED:
                throw new VTPlayerException("L'obtention du descripteur de fichier à �chouer.");

            default:
                if (ret < 0) {
                    throw new VTPlayerException(
                            "L'�criture sur le p�riph�rique a �chouer, veuillez v�rifier :\n"
                            + " - Que le p�riph�rique est bien brancher.\n"
                            + " - Que le module et bien charger dans le noyau \"lsmod | grep vtplayer\".\n"
                            + " - Que la souris a correctement �t� monter par le noyau \"dsmeg | tail -5\".\n"
                            + " - Que le lien et les droits sont bien attribuer a votre p�riph�rique \"ls -l /dev/vtplayer*\".");
                }
        }
    }

    /**
     * Recherche dans le r�pertoire <i>/dev/</i> le fichier susceptible d'�tre la souris.
     * Revient a appeler <i>findDevice("/dev/")</i>. <br />
     * <strong><i>ATTENTION :</i></strong> Aucun garanti n'est donner au faite 
     * que le fichier retourner soit le bon. En effet le premier fichier trouver, 
     * r�pondant au crit�re, est imm�diatement retourner.
     * @return fichier vers le p�riph�rique ou nul si aucun fichier ne correspond au crit�re.
     * @see #findDevice(java.lang.String)
     * @see #findDevices()
     * @see #findDevices(java.lang.String)
     */
    public static File findDevice() {
        return findDevice("/dev/");
    }

    /**
     * Recherche dans le r�pertoire dans le r�pertoire sp�cifier tout fichier susceptible d'�tre la souris.<br />
     * <strong><i>ATTENTION :</i></strong> Aucun garanti n'est donner au faite 
     * que le fichier retourner soit le bon. En effet le premier fichier trouver, 
     * r�pondant au crit�re, est imm�diatement retourner.
     * @param dir r�pertoire de recherche.
     * @return fichier vers le p�riph�rique ou nul si aucun fichier ne correspond au crit�re.
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
     * Recherche dans le r�pertoire dans le r�pertoire sp�cifier tout fichier susceptible d'�tre la souris.
     * Revient a appeler <i>findDevices("/dev/")</i>. <br/>
     * <strong><i>ATTENTION :</i></strong> Aucun garanti n'est donner au faite 
     * que la liste de fichier retourner soit le bonnes.
     * @return liste des fichiers validant les crit�res.
     * @see #findDevice() 
     * @see #findDevice(java.lang.String) 
     * @see #findDevices(java.lang.String) 
     */
    public static ArrayList<File> findDevices() {
        return findDevices("/dev/");
    }

    /**
     * Recherche dans le r�pertoire dans le r�pertoire sp�cifier tout fichier susceptible d'�tre la souris.<br />
     * <strong><i>ATTENTION :</i></strong> Aucun garanti n'est donner au faite 
     * que la liste de fichier retourner soit le bonnes.
     * @param dir r�pertoire de recherche.
     * @return liste des fichiers validant les crit�res.
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
     * Le fichier contenu dans le JAR et copier dans un r�pertoire temporaire avant d'�tre charger.
     * Le r�pertoire est supprimer apr�s le chargement de la librairie.
     * 
     * @param filename Le nom du fichier a l'int�rieur du JAR. Doit �tre un chemin absolue (commence par '/'), ex. /package/File.ext
     * @throws IOException si la cr�ation du r�pertoire temporaire �choue ou si les op�rations de lecture/�criture �choue.
     * @throws IllegalArgumentException si le fichier source (filename argument) n'existe pas 
     * @throws IllegalArgumentException si le chemin n'est pas absolue ou si le nom de fichier et inf�rieur a trois caract�re (restriction du a {@see File#createTempFile(java.lang.String, java.lang.String)})
     */
    private static void loadLibraryFromJar(String path) throws IOException {
 
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("Le chemin doit �tre absolue (commance par '/').");
        }
 
        // R�cup�re le nom du fichier
        String[] parts = path.split("/");
        String filename = (parts.length > 1) ? parts[parts.length - 1] : null;
 
        // S�paration du nom du fichier et de son extension
        String prefix = "";
        String suffix = null;
        if (filename != null) {
            parts = filename.split("\\.", 2);
            prefix = parts[0];
            suffix = (parts.length > 1) ? "."+parts[parts.length - 1] : null; // Thanks, davs! :-)
        }
 
        // V�rifie si le nom de fichier est ok
        if (filename == null || prefix.length() < 3) {
            throw new IllegalArgumentException("le nom de fichier doit contenir au moins trois caract�re.");
        }
 
        // Cr�ation du r�pertoire temporaire
        File temp = File.createTempFile(prefix, suffix);
        temp.deleteOnExit();
 
        if (!temp.exists()) {
            throw new FileNotFoundException("Le fichier " + temp.getAbsolutePath() + " n'existe pas.");
        }
 
        // Pr�paration du buffer pour la copie
        byte[] buffer = new byte[1024];
        int readBytes;
 
        // Ouverure et v�rificaion du flux d'entr�
        InputStream is = VTPlayer.class.getResourceAsStream(path);
        if (is == null) {
            throw new FileNotFoundException("Le fichier " + path + " n'a pas pu �tre trouvez dans le JAR.");
        }
 
        // Ouverture du flus de sortie et copie des donn�e
        OutputStream os = new FileOutputStream(temp);
        try {
            while ((readBytes = is.read(buffer)) != -1) {
                os.write(buffer, 0, readBytes);
            }
        } finally {
            // SI la lecture/�criture �chou, on ferme les flus avant de lancer une exception
            os.close();
            is.close();
        }
 
        // Finallement, chargement de la librairie
        System.load(temp.getAbsolutePath());
    }

    /**
     * Fermeture du p�riph�rique.
     * @throws VTPlayerException si la fermeture du p�riph�rique �choue
     */
    public void close() throws VTPlayerException {
        if (isOpen()) {
            int ret = vtplayer_close(vtPlayer);
            switch (ret) {
            case ERROR_CLASS_NOT_FOUND:
                throw new VTPlayerException("La classe FileDescriptor de java n'a pu �tre trouver dans jni.");

            case ERROR_FIELD_NOT_FOUND:
                throw new VTPlayerException("La classe FileDescriptor ne comporte pas le champs demander dans jni.");

            case ERROR_GET_FIELD_FAILED:
                throw new VTPlayerException("L'obtention du descripteur de fichier à �chouer.");

            default:
                if (ret < 0) {
                    throw new VTPlayerException("La fermeture du p�riph�rique a �chouer.");
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
