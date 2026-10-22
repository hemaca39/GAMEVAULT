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

    /*
     * TODO PR1-02 · Crear l'estructura (CA 1.1)

/**
 * Crea les carpetes d'importació, exportació i còpies de seguretat.
 *
 * @throws IOException si no es poden crear les carpetes.
 */
    public void preparaEstructura() throws IOException {
        Files.createDirectories(importacio());
        Files.createDirectories(exportacio());
        Files.createDirectories(copies());
    }


    /*
     * TODO PR1-03 · Còpia de seguretat amb marca de temps (CA 1.1)

    /**
     * Copia el fitxer dins de copies() sense modificar l'original.
     *
     * @param fitxer fitxer original que es vol copiar
     * @return ruta de la còpia de seguretat
     * @throws IOException si no es pot copiar el fitxer
     */
    public Path copiaSeguretat(Path fitxer) throws IOException {
        String nom = fitxer.getFileName().toString();
        int punt = nom.lastIndexOf('.');

        String nomBase;
        String extensio;

        if (punt > 0) {
            nomBase = nom.substring(0, punt);
            extensio = nom.substring(punt);
        } else {
            nomBase = nom;
            extensio = "";
        }

        String nomCopia = nomBase + "_" + marcaDeTemps() + extensio;
        Path desti = copies().resolve(nomCopia);

        Files.createDirectories(copies());

        return Files.copy(
                fitxer,
                desti,
                StandardCopyOption.REPLACE_EXISTING
        );
    }



    /**
     * TODO PR1-04 · Mida total del magatzem (CA 1.1)
     *
     * Retorna la suma de les mides de tots els fitxers regulars
     * que hi ha dins de la carpeta base, incloent-hi les subcarpetes.
     *
     * @return mida total dels fitxers en bytes
     * @throws IOException si no es pot recórrer el magatzem o llegir un fitxer
     */
    public long midaTotal() throws IOException {
        try (Stream<Path> camins = Files.walk(base)) {
            long total = 0;

            for (Path cami : camins.filter(Files::isRegularFile).toList()) {
                total += Files.size(cami);
            }

            return total;
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