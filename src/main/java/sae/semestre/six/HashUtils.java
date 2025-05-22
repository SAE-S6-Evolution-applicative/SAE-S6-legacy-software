/*
 * HashUtils.java                                  22 mai 2025
 * IUT de Rodez, pas de droit d'auteur
 */

package sae.semestre.six;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {

    private static final Logger logger = LoggerFactory.getLogger(HashUtils.class);

    public static String hashString(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Erreur lors du hashage", e);
            throw new SecurityException("Impossible de générer le hash de sécurité", e);
        }
    }
}
