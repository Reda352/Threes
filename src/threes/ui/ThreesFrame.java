package threes.ui; 



/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
import threes.model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import threes.model.Direction;
import threes.model.GameModel;
import threes.model.MoveResult;


/**
 
 * @author Titouan HETMANIAK
 */
public class ThreesFrame extends javax.swing.JFrame {
    private GameModel model;
    private JButton[][] cells;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ThreesFrame.class.getName());
    
    
     public ThreesFrame() {
        initComponents();   // NetBeans
        initGame();
        initGrid();
        initKeyBindings();
        updateView();
    }   

         private void initGame() {
        model = new GameModel();
        model.reset();
        cells = new JButton[4][4];
    }
         
         private void resetGame() {
        model.reset();
        infoLabel.setText("Nouvelle partie");
        updateView();
    }
         
         
          private void initGrid() {
        gridPanel.removeAll();

        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                JButton btn = new JButton();
                btn.setEnabled(false);
                btn.setFont(new Font("Arial", Font.BOLD, 22));
                btn.setPreferredSize(new Dimension(80, 80));
                cells[r][c] = btn;
                gridPanel.add(btn);
                btn.setFocusPainted(false);
btn.setFont(new Font("Segoe UI", Font.BOLD, 22));

            }
        }
    }
           private void initKeyBindings() {

        JComponent root = getRootPane();
        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();

        im.put(KeyStroke.getKeyStroke("UP"), "up");
        im.put(KeyStroke.getKeyStroke("DOWN"), "down");
        im.put(KeyStroke.getKeyStroke("LEFT"), "left");
        im.put(KeyStroke.getKeyStroke("RIGHT"), "right");

        am.put("up", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                play(Direction.UP);
            }
        });

        am.put("down", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                play(Direction.DOWN);
            }
        });

        am.put("left", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                play(Direction.LEFT);
            }
        });

        am.put("right", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                play(Direction.RIGHT);
            }
        });
    }
    private void showGameOverDialog() {
    JDialog dialog = new JDialog(this, "Game Over", true);
    dialog.setSize(500, 400);
    dialog.setLayout(new BorderLayout());
    dialog.setLocationRelativeTo(this);

    JLabel title = new JLabel("GAME OVER (t'es nul !)", SwingConstants.CENTER);
    title.setFont(new Font("Segoe UI", Font.BOLD, 40));
    title.setForeground(new Color(229, 57, 53));

    JLabel score = new JLabel(
        "Coups joués : " + model.getMovesCount(),
        SwingConstants.CENTER
    );
    score.setFont(new Font("Segoe UI", Font.PLAIN, 20));

    JButton replay = new JButton("Rejouer");
    replay.addActionListener(e -> {
        dialog.dispose();
        resetGame();
    });

    JPanel center = new JPanel(new GridLayout(2, 1));
    center.add(title);
    center.add(score);

    dialog.add(center, BorderLayout.CENTER);
    dialog.add(replay, BorderLayout.SOUTH);

    dialog.setVisible(true);
}

    // ===== JOUER =====
    private void play(Direction dir) {
   if (model.isGameOver()) {
    showGameOverDialog();
    return;
}
    int[][] before = copyGrid(model.getGrid());

    MoveResult res = model.move(dir);
    if (!res.moved) return;

    model.spawnOnBorder(dir);

    int[][] after = model.getGrid();
    updateView();

    // surbrillance des cases qui ont changé
    for (int r = 0; r < 4; r++) {
        for (int c = 0; c < 4; c++) {
            if (before[r][c] != after[r][c]) {
                flashCell(r, c);
            }
        }
    }
}

    
    // ===== AFFICHAGE =====
   private void updateView() {
    int[][] grid = model.getGrid();
    movesLabel.setText("Coups : " + model.getMovesCount());

    for (int r = 0; r < 4; r++) {
        for (int c = 0; c < 4; c++) {
            int v = grid[r][c];
            JButton btn = cells[r][c];

            if (v == 0) {
                btn.setText("");
                btn.setBackground(new Color(230, 230, 230));
                continue;
            }

            Color base = getColorForValue(v);

            btn.setText(String.valueOf(v));
            btn.setBackground(base);

            // le texte = même couleur, mais plus foncée
            btn.setForeground(darker(base, 80));

            btn.setFont(new Font("Segoe UI", Font.BOLD, 22));
            btn.setOpaque(true);
            btn.setBorderPainted(false);
        }
    }
}

   
   private Color darker(Color c, int factor) {
    return new Color(
        Math.max(c.getRed() - factor, 0),
        Math.max(c.getGreen() - factor, 0),
        Math.max(c.getBlue() - factor, 0)
    );
}

private Color lighter(Color c, int factor) {
    return new Color(
        Math.min(c.getRed() + factor, 255),
        Math.min(c.getGreen() + factor, 255),
        Math.min(c.getBlue() + factor, 255)
    );
}


    
    private Color getColorForValue(int v) {
    switch (v) {
        case 0:  return new Color(230, 230, 230); // vide
        case 1:  return new Color(100, 149, 237); // bleu
        case 2:  return new Color(15, 157, 88);    // VERT (6);   // rouge
        case 3:  return new Color(255, 165, 0);   // orange
        case 6:  return new Color(156, 39, 176);
        case 12: return new Color(255, 215, 0);
        case 24: return new Color(255, 193, 7);
        case 48: return new Color(255, 111, 0);
        case 96: return new Color(255, 87, 34);
        case 192:return new Color(233, 30, 99);
        case 384:return new Color(156, 39, 176);
        default: return new Color(96, 125, 139); // gros nombres
    }
}

    
    private int[][] copyGrid(int[][] g) {
    int[][] cp = new int[4][4];
    for (int r = 0; r < 4; r++) {
        System.arraycopy(g[r], 0, cp[r], 0, 4);
    }
    return cp;
}

private void flashCell(int r, int c) {
    JButton b = cells[r][c];
    Color old = b.getBackground();
    b.setBackground(new Color(255, 235, 59)); // jaune

    new javax.swing.Timer(120, e -> {
        b.setBackground(old);
        ((javax.swing.Timer) e.getSource()).stop();
    }).start();
}


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        gridPanel = new javax.swing.JPanel();
        topPanel = new javax.swing.JPanel();
        movesLabel = new javax.swing.JLabel();
        infoLabel = new javax.swing.JLabel();
        resetButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        gridPanel.setBackground(new java.awt.Color(255, 255, 255));
        gridPanel.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 255, 51)));
        gridPanel.setForeground(new java.awt.Color(255, 0, 0));
        gridPanel.setLayout(new java.awt.GridLayout(4, 4, 8, 8));

        topPanel.setLayout(new java.awt.BorderLayout());

        movesLabel.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        movesLabel.setText("Coups : 0");

        infoLabel.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        infoLabel.setText("Utilise les flèches");

        resetButton.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        resetButton.setText("Recommencer");
        resetButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(68, 68, 68)
                        .addComponent(gridPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(145, 145, 145)
                        .addComponent(infoLabel)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addComponent(movesLabel)
                        .addGap(94, 94, 94)
                        .addComponent(resetButton, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 27, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(topPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(146, 146, 146))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(gridPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(infoLabel)
                .addGap(15, 15, 15)
                .addComponent(topPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(resetButton, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(movesLabel))
                .addContainerGap(162, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        resetGame() ; 
    }//GEN-LAST:event_resetButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
 java.awt.EventQueue.invokeLater(() -> {
        new ThreesFrame().setVisible(true);
    });
}
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel gridPanel;
    private javax.swing.JLabel infoLabel;
    private javax.swing.JLabel movesLabel;
    private javax.swing.JButton resetButton;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables
}