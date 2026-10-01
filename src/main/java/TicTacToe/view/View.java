package TicTacToe.view;

import TicTacToe.model.Jogo;
import org.jline.terminal.Terminal;

import java.io.PrintWriter;

/** VIEW: somente funções de desenho. */
public class View {

    private final PrintWriter out;

    public View(Terminal terminal) {
        this.out = terminal.writer();
    }

    public void desenhar(Jogo jogo) {
        out.print("\033[H\033[2J"); // cursor para o topo + limpa a tela

        linha("");
        switch (jogo.getEstado()) {
            case VITORIA -> linha("  Fim de jogo: VITORIA do Jogador [ " + jogo.getVencedor() + " ]!");
            case EMPATE  -> linha("  Fim de jogo: EMPATE!");
            default      -> linha("  Vez do Jogador: [ " + jogo.getJogadorAtual() + " ]");
        }
        linha("");
        linha("      a   b   c");

        for (int l = 0; l < Jogo.TAMANHO; l++) {
            StringBuilder sb = new StringBuilder("   " + (l + 1) + " ");
            for (int c = 0; c < Jogo.TAMANHO; c++) {
                sb.append(celula(jogo, l, c));
                if (c < Jogo.TAMANHO - 1) sb.append('|');
            }
            linha(sb.toString());
            if (l < Jogo.TAMANHO - 1) linha("     ---|---|---");
        }

        linha("");
        linha("  Setas: mover | ESPACO: jogar | R: reiniciar | S: sair");
        if (!jogo.getMensagem().isEmpty()) {
            linha("");
            linha("  > " + jogo.getMensagem());
        }
        out.flush();
    }

    public void ocultarCursor() { out.print("\033[?25l"); out.flush(); }
    public void mostrarCursor() { out.print("\033[?25h"); out.flush(); }

    public void limpar() { out.print("\033[H\033[2J"); out.flush(); }

    public void mensagem(String texto) { linha(texto); out.flush(); }

    private String celula(Jogo jogo, int l, int c) {
        char v = jogo.getCelula(l, c);
        boolean selecionada = l == jogo.getCursorLinha() && c == jogo.getCursorColuna();
        return selecionada ? "[" + v + "]" : " " + v + " ";
    }

    // "\r\n" funciona tanto em modo normal quanto em modo raw
    private void linha(String s) {
        out.print(s + "\r\n");
    }
}

