
package cat.pratfp.gamevault.io;

import cat.pratfp.gamevault.model.Joc;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Lectura i escriptura del catàleg en JSON amb Jackson.
 */
public class JsonJocDao {

    private final ObjectMapper mapper = creaMapper();

    /**
     * Configura l'ObjectMapper per treballar amb dates i JSON.
     *
     * @return ObjectMapper configurat per al catàleg de jocs
     */
    private static ObjectMapper creaMapper() {
        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new JavaTimeModule());

        mapper.disable(
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
        );

        mapper.disable(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
        );

        return mapper;
    }

    /**
     * Escriu la llista sencera de jocs en un fitxer JSON indentat.
     *
     * @param p ruta del fitxer JSON de destinació
     * @param jocs llista de jocs que s'han d'escriure
     * @throws IOException si no es pot escriure el fitxer
     */
    public void escriu(Path p, List<Joc> jocs) throws IOException {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(p.toFile(), jocs);
    }

    /**
     * Llegeix una llista de jocs des d'un fitxer JSON.
     *
     * @param p ruta del fitxer JSON que s'ha de llegir
     * @return llista de jocs llegits
     * @throws IOException si no es pot llegir o interpretar el fitxer
     */
    public List<Joc> llegeix(Path p) throws IOException {
        return mapper.readValue(
                p.toFile(),
                new TypeReference<List<Joc>>() {}
        );
    }
}
