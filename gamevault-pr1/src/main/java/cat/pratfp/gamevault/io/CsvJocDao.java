package cat.pratfp.gamevault.io;

import cat.pratfp.gamevault.model.Joc;
import cat.pratfp.gamevault.model.ResultatCarrega;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lectura i escriptura del catàleg en CSV amb OpenCSV
 * (separador ; i UTF-8).
 */
public class CsvJocDao {

    /** Capçalera del fitxer CSV, en aquest ordre. */
    public static final String[] CAPCALERA = {
            "id",
            "titol",
            "plataforma",
            "estudi",
            "dataSortida",
            "horesJugades",
            "nota"
    };

    private static final char SEPARADOR = ';';

    /**
     * Llegeix un CSV de jocs sense aturar-se per les línies dolentes.
     *
     * @param p fitxer CSV amb capçalera
     * @return els jocs vàlids i una incidència per cada línia descartada
     * @throws IOException si el fitxer no es pot llegir o no és un CSV vàlid
     */
    public ResultatCarrega llegeix(Path p) throws IOException {
        List<Joc> jocs = new ArrayList<>();
        List<String> incidencies = new ArrayList<>();

        CSVParser parser = new CSVParserBuilder()
                .withSeparator(SEPARADOR)
                .withQuoteChar('"')
                .build();

        try (Reader r = Files.newBufferedReader(
                p,
                StandardCharsets.UTF_8
        );
             CSVReader csv = new CSVReaderBuilder(r)
                     .withCSVParser(parser)
                     .withSkipLines(1)
                     .build()) {

            String[] c;
            int linia = 1;

            while ((c = csv.readNext()) != null) {
                linia++;

                if (c.length < 7) {
                    incidencies.add(
                            "línia " + linia + ": incompleta"
                    );
                    continue;
                }

                try {
                    int id = Integer.parseInt(c[0]);
                    LocalDate dataSortida = LocalDate.parse(c[4]);
                    double horesJugades = Double.parseDouble(c[5]);
                    double nota = Double.parseDouble(c[6]);

                    Joc joc = new Joc(
                            id,
                            c[1],
                            c[2],
                            c[3],
                            dataSortida,
                            horesJugades,
                            nota
                    );

                    jocs.add(joc);

                } catch (DateTimeParseException e) {
                    incidencies.add(
                            "línia " + linia + ": data mal formada"
                    );

                } catch (NumberFormatException e) {
                    incidencies.add(
                            "línia " + linia + ": número invàlid"
                    );

                } catch (IllegalArgumentException e) {
                    incidencies.add(
                            "línia " + linia + ": " + e.getMessage()
                    );
                }
            }

        } catch (CsvValidationException e) {
            throw new IOException("CSV mal format a " + p, e);
        }

        return new ResultatCarrega(jocs, incidencies);
    }

    /**
     * Escriu el catàleg sencer en un fitxer CSV amb separador ';'.
     *
     * @param p ruta del fitxer CSV de destinació
     * @param jocs llista de jocs que s'han d'escriure
     * @throws IOException si no es pot escriure el fitxer
     */
    public void escriu(Path p, List<Joc> jocs) throws IOException {
        try (Writer w = Files.newBufferedWriter(
                p,
                StandardCharsets.UTF_8
        );
             CSVWriter csv = new CSVWriter(
                     w,
                     SEPARADOR,
                     CSVWriter.DEFAULT_QUOTE_CHARACTER,
                     CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                     CSVWriter.DEFAULT_LINE_END
             )) {

            csv.writeNext(CAPCALERA, false);

            for (Joc joc : jocs) {
                csv.writeNext(aCamps(joc), false);
            }
        }
    }

    /**
     * Converteix un Joc en la fila de text que espera el CSV.
     *
     * @param j joc
     * @return els 7 camps en l'ordre de la capçalera
     */
    static String[] aCamps(Joc j) {
        return new String[]{
                String.valueOf(j.id()),
                j.titol(),
                j.plataforma(),
                j.estudi(),
                j.dataSortida().toString(),
                String.valueOf(j.horesJugades()),
                String.valueOf(j.nota())
        };
    }
}