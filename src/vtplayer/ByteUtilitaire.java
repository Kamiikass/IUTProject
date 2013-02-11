package vtplayer;

/**
 *
 * @author godeau
 */
public class ByteUtilitaire {
    /**
     * Convertie une chaîne de caractère en byte.
     *
     * @param str chaîne écrite en hexadécimal a convertir en byte.
     * @return l'équivalent en byte de la chaîne hexadécimal.
     * @throws VTPlayerException Si la chaîne est trop long pour être un
     * argument valide de <i>set</i>. Si un des caractère n'est pas une valeur
     * hexadécimal correct.
     */
    public static byte string_hexa_to_byte(String str) throws VTPlayerException {
        int length = str.length();
        if (length > 2) {
            throw new VTPlayerException("La chaine \"" + str + "\" n'est pas une chaîne valide pour la convertion en byte.");
        }
        byte ret = 0;

        try {
            for(int cpt = 0 ; cpt < length ; cpt++){
                ret *= 16;
                ret += Byte.parseByte(Character.toString(str.charAt(cpt)), 16);
            }
        } catch (NumberFormatException ex) {
            throw new VTPlayerException("Échec de la conversion de la chaine \"" + str + "\n en byte.");
        }
        return ret;
    }
    
    /**
     * Converti un bit codé en hexadécimal en une chaîne de caractère représentant le code binaire de ce même nombre.  
     * @param b byte a convertir
     * @return code binaire du byte passer en paramètre (coder sur 8 bits)
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
