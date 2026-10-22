
package cat.pratfp.gamevault.io;

import cat.pratfp.gamevault.model.Joc;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Índex d'hores jugades en un fitxer binari d'accés aleatori.
 *
 * Cada registre fa sempre 92 bytes:
 * id (4 bytes), títol (80 bytes) i hores (8 bytes).
 */
public class IndexHores {

    /** Caràcters reservats per al títol. */
    public static final int LONG_TITOL = 40;

    /** Mida fixa de cada registre en bytes: 4 + 40 × 2 + 8 = 92. */
    public static final int MIDA_REGISTRE =
            Integer.BYTES + LONG_TITOL * Character.BYTES + Double.BYTES;

    private final Path fitxer;

    /**
     * Crea l'índex d'hores.
     *
     * @param fitxer fitxer binari de l'índex
     */
    public IndexHores(Path fitxer) {
        this.fitxer = fitxer;
    }

    /**
     * Escriu el registre d'un joc a la posició indicada.
     *
     * @param posicio número de registre
     * @param j joc que es vol desar
     * @throws IOException si no es pot escriure
     */
    public void escriu(int posicio, Joc j) throws IOException {
        try (RandomAccessFile raf =
                     new RandomAccessFile(fitxer.toFile(), "rw")) {

            raf.seek((long) posicio * MIDA_REGISTRE);
            raf.writeInt(j.id());

            String fix = String.format(
                    "%-" + LONG_TITOL + "s",
                    j.titol()
            ).substring(0, LONG_TITOL);

            raf.writeChars(fix);
            raf.writeDouble(j.horesJugades());
        }
    }

    /**
     * Consulta quants registres té el fitxer.
     *
     * @return nombre de registres, o 0 si no existeix
     * @throws IOException si no es pot consultar la mida
     */
    public long nombreRegistres() throws IOException {
        return Files.exists(fitxer)
                ? Files.size(fitxer) / MIDA_REGISTRE
                : 0;
    }

    /**
     * Llegeix les hores d'un registre concret.
     *
     * @param posicio número de registre
     * @return hores jugades del joc
     * @throws IOException si no es pot llegir el fitxer o el registre no existeix
     */
    public double llegeixHores(int posicio) throws IOException {
        try (RandomAccessFile raf =
                     new RandomAccessFile(fitxer.toFile(), "r")) {

            long posicioHores = (long) posicio * MIDA_REGISTRE
                    + Integer.BYTES
                    + (long) LONG_TITOL * Character.BYTES;

            raf.seek(posicioHores);
            return raf.readDouble();
        }
    }

    /**
     * Suma hores a un registre sense modificar la resta del fitxer.
     *
     * @param posicio número de registre
     * @param extra hores que s'han de sumar
     * @return total d'hores després de sumar
     * @throws IOException si no es pot llegir o escriure el registre
     */
    public double sumaHores(int posicio, double extra) throws IOException {
        try (RandomAccessFile raf =
                     new RandomAccessFile(fitxer.toFile(), "rw")) {

            long posicioHores = (long) posicio * MIDA_REGISTRE
                    + Integer.BYTES
                    + (long) LONG_TITOL * Character.BYTES;

            raf.seek(posicioHores);
            double horesActuals = raf.readDouble();

            double horesNoves = horesActuals + extra;

            raf.seek(posicioHores);
            raf.writeDouble(horesNoves);

            return horesNoves;
        }
    }
}
