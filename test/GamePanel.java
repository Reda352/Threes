import java.awt.*;
import javax.swing.*;


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Titouan HETMANIAK
 */

    
public class GamePanel extends JPanel {
    private final JLabel[][] cells = new JLabel[GameModel.SIZE][GameModel.SIZE];

    public GamePanel() {
        setLayout(new GridLayout(GameModel.SIZE, GameModel.SIZE, 8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(0x222222));

        Font font = new Font("SansSerif", Font.BOLD, 28);

        for (int r = 0; r < GameModel.SIZE; r++) {
            for (int c = 0; c < GameModel.SIZE; c++) {
                JLabel lab = new JLabel("", SwingConstants.CENTER);
                lab.setOpaque(true);
                lab.setFont(font);
                lab.setPreferredSize(new Dimension(90, 90));
                lab.setBorder(BorderFactory.createLineBorder(new Color(0x111111), 2));
                cells[r][c] = lab;
                add(lab);
            }
        }
    }

    public void render(int[][] grid) {
        for (int r = 0; r < GameModel.SIZE; r++) {
            for (int c = 0; c < GameModel.SIZE; c++) {
                int v = grid[r][c];
                JLabel lab = cells[r][c];

                lab.setText(v == 0 ? "" : String.valueOf(v));

                // couleurs simples (tu peux améliorer)
                lab.setBackground(tileColor(v));
                lab.setForeground(v <= 2 ? Color.BLACK : Color.WHITE);
            }
        }
        repaint();
    }

    private Color tileColor(int v) {
        return switch (v) {
            case 0 -> new Color(0x3A3A3A);
            case 1 -> new Color(0xE8F1FF);
            case 2 -> new Color(0xD7FFE8);
            case 3 -> new Color(0xFFDCA8);
            default -> new Color(0x4B6CB7);
        };
    }
}

    
}
