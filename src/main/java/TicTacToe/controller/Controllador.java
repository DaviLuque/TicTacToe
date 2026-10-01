package TicTacToe.controller;

import TicTacToe.model.Jogo;
import TicTacToe.model.RepositorioJogo;
import TicTacToe.view.View;
import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;
import org.jline.terminal.Attributes;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp.Capability;

import java.io.IOException;

/** CONTROLLER: regras do jogo e captura direta do teclado. */
public class Controllador {

    private enum Acao { CIMA, BAIXO, ESQUERDA, DIREITA, CONFIRMAR, REINICIAR, SAIR }

    private final Jogo jogo;
    private final View visao;
    private final RepositorioJogo repositorio;
    private final Terminal terminal;

    public Controllador(Jogo jogo, View visao, RepositorioJogo repositorio, Terminal terminal) {
        this.jogo = jogo;
        this.visao = visao;
        this.repositorio = repositorio;
        this.terminal = terminal;
    }

    public void iniciar() {
        carregarPartida();

        BindingReader leitor = new BindingReader(terminal.reader());
        KeyMap<Acao> teclas = criarMapaDeTeclas();

        Attributes original = terminal.enterRawMode(); // lê tecla sem precisar de ENTER
        try {
            visao.ocultarCursor();
            boolean rodando = true;
            while (rodando) {
                visao.desenhar(jogo);
                Acao acao = leitor.readBinding(teclas);
                if (acao == null) break; // fim da entrada

                jogo.setMensagem("");
                switch (acao) {
                    case CIMA      -> moverCursor(-1, 0);
                    case BAIXO     -> moverCursor(1, 0);
                    case ESQUERDA  -> moverCursor(0, -1);
                    case DIREITA   -> moverCursor(0, 1);
                    case CONFIRMAR -> confirmarJogada();
                    case REINICIAR -> reiniciar();
                    case SAIR      -> rodando = false;
                }
            }
        } finally {
            terminal.setAttributes(original);
            visao.mostrarCursor();
            salvarPartida();
            visao.limpar();
            visao.mensagem("Partida salva. Ate a proxima!");
        }
    }

    // ---------- Teclado ----------

    private KeyMap<Acao> criarMapaDeTeclas() {
        KeyMap<Acao> mapa = new KeyMap<>();
        // Cada seta pode chegar em sequências diferentes conforme o terminal
        vincular(mapa, Acao.CIMA,     KeyMap.key(terminal, Capability.key_up),    "\033[A", "\033OA");
        vincular(mapa, Acao.BAIXO,    KeyMap.key(terminal, Capability.key_down),  "\033[B", "\033OB");
        vincular(mapa, Acao.DIREITA,  KeyMap.key(terminal, Capability.key_right), "\033[C", "\033OC");
        vincular(mapa, Acao.ESQUERDA, KeyMap.key(terminal, Capability.key_left),  "\033[D", "\033OD");
        vincular(mapa, Acao.CONFIRMAR, " ");
        vincular(mapa, Acao.REINICIAR, "r", "R");
        vincular(mapa, Acao.SAIR, "s", "S");
        return mapa;
    }

    private void vincular(KeyMap<Acao> mapa, Acao acao, String... sequencias) {
        for (String s : sequencias) {
            if (s != null) mapa.bind(acao, s);
        }
    }

    // ---------- Regras ----------

    private void moverCursor(int dLinha, int dColuna) {
        int l = Math.max(0, Math.min(Jogo.TAMANHO - 1, jogo.getCursorLinha() + dLinha));
        int c = Math.max(0, Math.min(Jogo.TAMANHO - 1, jogo.getCursorColuna() + dColuna));
        jogo.setCursorLinha(l);
        jogo.setCursorColuna(c);
    }

    private void confirmarJogada() {
        if (jogo.getEstado() != Jogo.Estado.EM_ANDAMENTO) {
            jogo.setMensagem("O jogo terminou. Pressione R para reiniciar.");
            return;
        }

        int l = jogo.getCursorLinha();
        int c = jogo.getCursorColuna();
        if (jogo.getCelula(l, c) != Jogo.VAZIO) {
            jogo.setMensagem("Posicao ocupada! Escolha outra.");
            return;
        }

        char jogador = jogo.getJogadorAtual();
        jogo.setCelula(l, c, jogador);

        if (venceu(jogador)) {
            jogo.setEstado(Jogo.Estado.VITORIA);
            jogo.setVencedor(jogador);
        } else if (tabuleiroCheio()) {
            jogo.setEstado(Jogo.Estado.EMPATE);
        } else {
            jogo.setJogadorAtual(jogador == 'X' ? 'O' : 'X'); // alterna jogador
        }

        salvarPartida(); // salvamento automático
    }

    private void reiniciar() {
        jogo.reiniciar();
        salvarPartida();
        jogo.setMensagem("Novo jogo iniciado.");
    }

    private boolean venceu(char j) {
        for (int i = 0; i < Jogo.TAMANHO; i++) {
            if (jogo.getCelula(i, 0) == j && jogo.getCelula(i, 1) == j && jogo.getCelula(i, 2) == j) return true;
            if (jogo.getCelula(0, i) == j && jogo.getCelula(1, i) == j && jogo.getCelula(2, i) == j) return true;
        }
        if (jogo.getCelula(0, 0) == j && jogo.getCelula(1, 1) == j && jogo.getCelula(2, 2) == j) return true;
        return jogo.getCelula(0, 2) == j && jogo.getCelula(1, 1) == j && jogo.getCelula(2, 0) == j;
    }

    private boolean tabuleiroCheio() {
        for (int l = 0; l < Jogo.TAMANHO; l++) {
            for (int c = 0; c < Jogo.TAMANHO; c++) {
                if (jogo.getCelula(l, c) == Jogo.VAZIO) return false;
            }
        }
        return true;
    }

    // ---------- Persistência ----------

    private void carregarPartida() {
        if (!repositorio.existe()) return;
        try {
            repositorio.carregar(jogo);
        } catch (IOException | RuntimeException e) {
            jogo.reiniciar();
            jogo.setMensagem("Nao foi possivel carregar o save. Novo jogo iniciado.");
        }
    }

    private void salvarPartida() {
        try {
            repositorio.salvar(jogo);
        } catch (IOException e) {
            jogo.setMensagem("Erro ao salvar: " + e.getMessage());
        }
    }
}