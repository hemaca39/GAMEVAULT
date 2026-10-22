package cat.pratfp.gamevault.fs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

/**
 * El magatzem de fitxers de GameVault: una carpeta base amb tres subcarpetes,
 * import, export i backup.
 */
public class Magatzem {

    private static final DateTimeFormatter MARCA = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    private final Path base;

    /**
     * Crea un magatzem sobre una carpeta base. No toca el disc.
     *
     * @param base carpeta arrel del magatzem (per exemple, Path.of("dades"))
     */
    public Magatzem(Path base) {
        this.base = base;
    }

    /** @return la carpeta d'importació (base/import) */
    public Path importacio() {
        return base.resolve("import");
    }

    /** @return la carpeta d'exportació (base/export) */
    public Path exportacio() {
        return base.resolve("export");
    }

    /** @return la carpeta de còpies de seguretat (base/backup) */
    public Path copies() {
        return base.resolve("backup");
    }

    /**
     * PR1-02 · Crear l'estructura (CA 1.1)
     * Crea les tres carpetes: importacio(), exportacio() i copies().
     */
    public void preparaEstructura() throws IOException {
        Files.createDirectories(importacio());
        Files.createDirectories(exportacio());
        Files.createDirectories(copies());
    }

    /**
     * PR1-03 · Còpia de seguretat amb marca de temps (CA 1.1)
     * Copia el fitxer dins de copies() amb el nom <nom>_<marca>.<extensió>
     */
    public Path copiaSeguretat(Path fitxer) throws IOException {
        String nomComplet = fitxer.getFileName().toString();
        int puntIndex = nomComplet.lastIndexOf('.');

        String nom;
        String extensio;

        if (puntIndex != -1) {
            nom = nomComplet.substring(0, puntIndex);
            extensio = nomComplet.substring(puntIndex);
        } else {
            nom = nomComplet;
            extensio = "";
        }

        String nouNom = nom + "_" + marcaDeTemps() + extensio;
        Path desti = copies().resolve(nouNom);

        return Files.copy(fitxer, desti, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * PR1-04 · Mida total del magatzem (CA 1.1)
     * Retorna la suma de les mides (en bytes) de TOTS els fitxers regulars.
     */
    public long midaTotal() throws IOException {
        try (Stream<Path> camins = Files.walk(base)) {
            return camins.filter(Files::isRegularFile)
                    .mapToLong(p -> {
                        try {
                            return Files.size(p);
                        } catch (IOException e) {
                            return 0L;
                        }
                    })
                    .sum();
        }
    }

    /**
     * Llista els fitxers regulars del magatzem amb la seva mida, ordenats per ruta.
     *
     * @return una línia per fitxer, amb el format "ruta relativa (N bytes)"
     * @throws IOException si no es pot recórrer la carpeta
     */
    public List<String> inventari() throws IOException {
        try (Stream<Path> camins = Files.walk(base)) {
            return camins.filter(Files::isRegularFile)
                    .sorted()
                    .map(p -> base.relativize(p) + " (" + mida(p) + " bytes)")
                    .toList();
        }
    }

    /** Mida d'un fitxer sense excepció comprovada, per poder-la fer servir dins d'un map. */
    private static long mida(Path p) {
        try {
            return Files.size(p);
        } catch (IOException e) {
            return -1;
        }
    }

    /**
     * Marca de temps per als noms de les còpies.
     *
     * @return la data i hora actuals amb el format yyyyMMdd-HHmmss
     */
    public static String marcaDeTemps() {
        return LocalDateTime.now().format(MARCA);
    }
}