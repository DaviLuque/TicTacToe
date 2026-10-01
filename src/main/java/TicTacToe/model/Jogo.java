package TicTacToe.model;

import java.util.Arrays;

/** MODEL: apenas os dados do jogo (sem regras e sem desenho). */
public class Jogo {

    public static final int TAMANHO = 3;
    public static final char VAZIO = ' ';

    public enum Estado { EM_ANDAMENTO, VITORIA, EMPATE }

    private final char[][] tabuleiro = new char[TAMANHO][TAMANHO];
    private char jogadorAtual;
    private int cursorLinha;
    private int cursorColuna;
    private Estado estado;
    private char vencedor;
    private String mensagem;

    public Jogo() {
        reiniciar();
    }

    public void reiniciar() {
        for (char[] linha : tabuleiro) {
            Arrays.fill(linha, VAZIO);
        }
        jogadorAtual = 'X';
        cursorLinha = 0;
        cursorColuna = 0;
        estado = Estado.EM_ANDAMENTO;
        vencedor = VAZIO;
        mensagem = "";
    }

    public char getCelula(int linha, int coluna) { return tabuleiro[linha][coluna]; }
    public void setCelula(int linha, int coluna, char valor) { tabuleiro[linha][coluna] = valor; }

    public char getJogadorAtual() { return jogadorAtual; }
    public void setJogadorAtual(char jogadorAtual) { this.jogadorAtual = jogadorAtual; }

    public int getCursorLinha() { return cursorLinha; }
    public void setCursorLinha(int cursorLinha) { this.cursorLinha = cursorLinha; }

    public int getCursorColuna() { return cursorColuna; }
    public void setCursorColuna(int cursorColuna) { this.cursorColuna = cursorColuna; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }

    public char getVencedor() { return vencedor; }
    public void setVencedor(char vencedor) { this.vencedor = vencedor; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
}