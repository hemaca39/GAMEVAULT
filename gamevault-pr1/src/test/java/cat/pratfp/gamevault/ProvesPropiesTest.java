
package cat.pratfp.gamevault;

import cat.pratfp.gamevault.fs.Magatzem;
import cat.pratfp.gamevault.io.IndexHores;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.RandomAccessFile;

import static org.junit.jupiter.api.Assertions.*;

class ProvesPropiesTest {

    @TempDir
    Path tmp;

    @Test
    @DisplayName("PR1-14 · llegeix les hores d'un registre amb un títol llarg")
    void primeraProva() throws Exception {
        Path fitxer = tmp.resolve("index.dat");

        String titol = "Aquest es un titol de joc que supera els quaranta caracters";
        double horesEsperades = 12.5;

        try (RandomAccessFile raf = new RandomAccessFile(fitxer.toFile(), "rw")) {
            raf.writeInt(1);
            StringBuilder titolFix = new StringBuilder(titol);
            titolFix.setLength(40);
            raf.writeChars(titolFix.toString());
            raf.writeDouble(horesEsperades);
        }

        IndexHores index = new IndexHores(fitxer);

        assertEquals(horesEsperades, index.llegeixHores(0));
    }

    @Test
    @DisplayName("PR1-14 · crea una còpia de seguretat d'un fitxer sense extensió")
    void segonaProva() throws Exception {
        Path base = tmp.resolve("magatzem");
        Magatzem magatzem = new Magatzem(base);

        magatzem.preparaEstructura();

        Path original = tmp.resolve("dades");
        String contingut = "Contingut de prova";
        Files.writeString(original, contingut);

        Path copia = magatzem.copiaSeguretat(original);

        assertTrue(Files.exists(copia));
        assertEquals(contingut, Files.readString(copia));
        assertEquals(contingut, Files.readString(original));
    }
}
