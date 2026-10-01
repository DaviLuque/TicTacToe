package TicTacToe;

import TicTacToe.controller.Controllador;
import TicTacToe.model.Jogo;
import TicTacToe.model.RepositorioJogo;
import TicTacToe.view.View;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) throws IOException {
        try (Terminal terminal = TerminalBuilder.builder().system(true).build()) {

            // Console do IntelliJ / gradle run => terminal "dumb" => setas não funcionam
            if (terminal.getType().startsWith("dumb")) {
                System.out.println("ERRO: este console nao e um terminal real (tipo: " + terminal.getType() + ").");
                System.out.println("Gere o executavel com './gradlew installDist' e rode em um terminal de verdade.");
                return;
            }

            Jogo jogo = new Jogo();
            View visao = new View(terminal);
            RepositorioJogo repositorio = new RepositorioJogo(Path.of("jogo_salvo.properties"));

            new Controllador(jogo, visao, repositorio, terminal).iniciar();
        }
    }
}