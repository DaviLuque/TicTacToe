package TicTacToe.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** MODEL: salvar e carregar o status do jogo em arquivo local. */
public class RepositorioJogo {

    private final Path arquivo;

    public RepositorioJogo(Path arquivo) {
        this.arquivo = arquivo;
    }

    public boolean existe() {
        return Files.exists(arquivo);
    }

    public void salvar(Jogo jogo) throws IOException {
        StringBuilder tab = new StringBuilder();
        for (int l = 0; l < Jogo.TAMANHO; l++) {
            for (int c = 0; c < Jogo.TAMANHO; c++) {
                char v = jogo.getCelula(l, c);
                tab.append(v == Jogo.VAZIO ? '.' : v);
            }
        }

        Properties p = new Properties();
        p.setProperty("tabuleiro", tab.toString());
        p.setProperty("jogadorAtual", String.valueOf(jogo.getJogadorAtual()));
        p.setProperty("cursorLinha", String.valueOf(jogo.getCursorLinha()));
        p.setProperty("cursorColuna", String.valueOf(jogo.getCursorColuna()));
        p.setProperty("estado", jogo.getEstado().name());
        p.setProperty("vencedor", jogo.getVencedor() == Jogo.VAZIO ? "." : String.valueOf(jogo.getVencedor()));

        try (OutputStream out = Files.newOutputStream(arquivo)) {
            p.store(out, "Jogo da Velha - partida salva");
        }
    }

    public void carregar(Jogo jogo) throws IOException {
        Properties p = new Properties();
        try (InputStream in = Files.newInputStream(arquivo)) {
            p.load(in);
        }

        String tab = p.getProperty("tabuleiro", "");
        if (tab.length() != Jogo.TAMANHO * Jogo.TAMANHO) {
            throw new IOException("Arquivo de save inválido.");
        }

        for (int i = 0; i < tab.length(); i++) {
            char v = tab.charAt(i);
            jogo.setCelula(i / Jogo.TAMANHO, i % Jogo.TAMANHO, v == '.' ? Jogo.VAZIO : v);
        }

        jogo.setJogadorAtual(p.getProperty("jogadorAtual", "X").charAt(0));
        jogo.setCursorLinha(Integer.parseInt(p.getProperty("cursorLinha", "0")));
        jogo.setCursorColuna(Integer.parseInt(p.getProperty("cursorColuna", "0")));
        jogo.setEstado(Jogo.Estado.valueOf(p.getProperty("estado", "EM_ANDAMENTO")));
        char v = p.getProperty("vencedor", ".").charAt(0);
        jogo.setVencedor(v == '.' ? Jogo.VAZIO : v);
        jogo.setMensagem("Partida carregada do arquivo.");
    }
}