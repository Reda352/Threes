import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Titouan HETMANIAK
 */

public class MainFrame extends JFrame {
    private final GameModel model = new GameModel();
    private final GamePanel panel = new GamePanel();

    private final JLabel movesLabel = new JLabel();
    private final JLabel statusLabel = new JLabel();

    public MainFrame() {
        super("Threes (version étudiante) — Swing");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new BorderLayout(10, 10));

        // Top bar
        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        movesLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JButton newGame = new JButton("Nouvelle partie");
        newGame.addActionListener(e -> {
            model.reset();
            refreshUI();
        });

        top.add(movesLabel, BorderLayout.WEST);
        top.add(newGame, BorderLayout.EAST);
        top.add(statusLabel, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);

        // Bottom controls (optionnel mais pratique)
        JPanel bottom = new JPanel(new GridLayout(2, 3, 8, 8));
        bottom.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        bottom.add(new JLabel(""));
        bottom.add(makeMoveButton("↑", GameModel.Dir.UP));
        bottom.add(new JLabel(""));
        bottom.add(makeMoveButton("←", GameModel.Dir.LEFT));
        bottom.add(makeMoveButton("↓", GameModel.Dir.DOWN));
        bottom.add(makeMoveButton("→", GameModel.Dir.RIGHT));

        add(bottom, BorderLayout.SOUTH);

        // Key bindings (flèches)
        bindKey("UP", GameModel.Dir.UP);
        bindKey("DOWN", GameModel.Dir.DOWN);
        bindKey("LEFT", GameModel.Dir.LEFT);
        bindKey("RIGHT", GameModel.Dir.RIGHT);

        pack();
        setLocationRelativeTo(null); // centre écran
        refreshUI();
    }

    private JButton makeMoveButton(String text, GameModel.Dir dir) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 18));
        b.addActionListener(e -> doMove(dir));
        return b;
    }

    private void bindKey(String keyStroke, GameModel.Dir dir) {
        InputMap im = panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = panel.getActionMap();

        im.put(KeyStroke.getKeyStroke(keyStroke), keyStroke);
        am.put(keyStroke, new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                doMove(dir);
            }
        });
    }

    private void doMove(GameModel.Dir dir) {
        if (model.isGameOver()) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        boolean changed = model.move(dir);
        if (!changed) Toolkit.getDefaultToolkit().beep();

        refreshUI();
    }

    private void refreshUI() {
        panel.render(model.getGridCopy());
        movesLabel.setText("Coups joués : " + model.getMoves());

        if (model.isGameOver()) {
            statusLabel.setText("Partie terminée : plus de coups possibles.");
        } else {
            statusLabel.setText("Flèches du clavier ou boutons. Objectif : maximiser les coups.");
        }
    }
}

    

