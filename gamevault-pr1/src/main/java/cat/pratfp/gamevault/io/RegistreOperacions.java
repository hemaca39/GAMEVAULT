
package cat.pratfp.gamevault.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Registre de les operacions de GameVault en un fitxer de text
 * (operacions.log).
 */
public class RegistreOperacions {

    /**
     * Format de la data i hora de cada línia: 2026-10-13 15:10:02.
     */
    public static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Path fitxer;

    /**
     * Crea el registre d'operacions.
     *
     * @param fitxer fitxer de registre; si no existeix, es crea
     *               en la primera escriptura
     */
    public RegistreOperacions(Path fitxer) {
        this.fitxer = fitxer;
    }

    /**
     * Afegeix una línia al final del fitxer de registre sense esborrar
     * el contingut anterior.
     *
     * @param text text de l'operació que es vol registrar
     * @throws IOException si no es pot escriure al fitxer
     */
    public void registra(String text) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(
                fitxer,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        )) {
            String dataHora = LocalDateTime.now().format(FORMAT);
            writer.write(dataHora + " | " + text);
            writer.newLine();
        }
    }
}
