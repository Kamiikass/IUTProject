package vtplayer;

/**
 *
 * @author godeau
 */
public class ByteUtilitaire {
    /**
     * Convertie une cha�ne de caract�re en byte.
     *
     * @param str cha�ne �crite en hexad�cimal a convertir en byte.
     * @return l'�quivalent en byte de la cha�ne hexad�cimal.
     * @throws VTPlayerException Si la cha�ne est trop long pour �tre un
     * argument valide de <i>set</i>. Si un des caract�re n'est pas une valeur
     * hexad�cimal correct.
     */
    public static byte string_hexa_to_byte(String str) throws VTPlayerException {
        int length = str.length();
        if (length > 2) {
            throw new VTPlayerException("La chaine \"" + str + "\" n'est pas une cha�ne valide pour la convertion en byte.");
        }
        byte ret = 0;

        try {
            for(int cpt = 0 ; cpt < length ; cpt++){
                ret *= 16;
                ret += Byte.parseByte(Character.toString(str.charAt(cpt)), 16);
            }
        } catch (NumberFormatException ex) {
            throw new VTPlayerException("�chec de la conversion de la chaine \"" + str + "\n en byte.");
        }
        return ret;
    }
    
    /**
     * Converti un bit cod� en hexad�cimal en une cha�ne de caract�re repr�sentant le code binaire de ce m�me nombre.  
     * @param b byte a convertir
     * @return code binaire du byte passer en param�tre (coder sur 8 bits)
     */
    public static String byte_hexa_to_bin_string(Byte b) {
        Integer integer = Integer.parseInt(String.format("%x", b), 16);
        String str = new String();

        while (integer > 1) {
            str = (integer % 2) + str;
            integer = (integer / 2);
        }

        if (integer == 1) {
            str = 1 + str;
        }

        while (str.length() != 8) {
            str = "0" + str;
        }

        return str;
    }
    
}
