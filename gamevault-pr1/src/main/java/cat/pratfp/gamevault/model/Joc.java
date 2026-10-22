
package cat.pratfp.gamevault.model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Un joc del catàleg GameVault.
 *
 * És un record immutable, amb constructor, accessors, equals,
 * hashCode i toString generats.
 * El constructor compacte valida les dades abans de crear l'objecte.
 */
public record Joc(
        int id,
        String titol,
        String plataforma,
        String estudi,
        LocalDate dataSortida,
        double horesJugades,
        double nota
) implements Serializable {

    /**
     * Valida les dades del joc abans de crear l'objecte.
     *
     * @throws IllegalArgumentException si el títol és nul o buit,
     *         les hores són negatives o la nota no està entre 0 i 10.
     */
    public Joc {
        if (titol == null || titol.isBlank()) {
            throw new IllegalArgumentException("El títol no pot ser nul ni buit.");
        }

        if (horesJugades < 0) {
            throw new IllegalArgumentException("Les hores jugades no poden ser negatives.");
        }

        if (nota < 0 || nota > 10) {
            throw new IllegalArgumentException("La nota ha d'estar entre 0 i 10.");
        }
    }
}