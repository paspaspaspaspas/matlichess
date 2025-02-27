package it.matlice.matlichess.view;

import it.matlice.matlichess.Location;
import it.matlice.matlichess.PieceColor;
import it.matlice.matlichess.exceptions.InvalidMoveException;
import it.matlice.settings.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.concurrent.Semaphore;

import static it.matlice.matlichess.view.PieceView.pointerToLocation;

/**
 * Panel to choose which piece will the pawn become, when it reaches the last row and promotes
 */
public class PromotionChoiceBox extends JPanel implements MouseListener {

    private final Semaphore semaphore = new Semaphore(1);
    private final PieceColor turn;
    private String selectedPiece;

    public PromotionChoiceBox(PieceColor turn) {
        this.setPreferredSize(new Dimension(Settings.CHESSBOARD_SQUARE_SIZE, Settings.CHESSBOARD_SQUARE_SIZE * 4));
        this.addMouseListener(this);
        this.turn = turn;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        if (Settings.USE_ANTIALIAS) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }
        drawPromotionChoiceBox(g2);
    }

    /**
     * Draws a on screen a promotion choice box
     *
     * @param g2 Graphics2D object
     */
    public void drawPromotionChoiceBox(Graphics2D g2) {
        int size = Settings.CHESSBOARD_SQUARE_SIZE;
        PieceView queen;
        PieceView knight;
        PieceView bishop;
        PieceView rook;
        Rectangle box = new Rectangle(0, 0, size, size * 4);

        if (turn.equals(PieceColor.WHITE)) {
            queen = new PieceView(PieceType.QUEEN_WHITE, new Location("A8"));
            knight = new PieceView(PieceType.KNIGHT_WHITE, new Location("A7"));
            rook = new PieceView(PieceType.ROOK_WHITE, new Location("A6"));
            bishop = new PieceView(PieceType.BISHOP_WHITE, new Location("A5"));
        } else {
            queen = new PieceView(PieceType.QUEEN_BLACK, new Location("A8"));
            knight = new PieceView(PieceType.KNIGHT_BLACK, new Location("A7"));
            rook = new PieceView(PieceType.ROOK_BLACK, new Location("A6"));
            bishop = new PieceView(PieceType.BISHOP_BLACK, new Location("A5"));
        }

        g2.setColor(Color.WHITE);
        g2.fill(box);
        queen.draw(g2);
        knight.draw(g2);
        bishop.draw(g2);
        rook.draw(g2);
    }

    /**
     * Starts a thread to ask the player to choose one option and shows the box
     *
     * @param point the origin of the frame
     * @return String representing the chosen piece type
     */
    public String askPromotion(Point point) {
        //I need a semaphore to stop the execution of the others thread while I'm choosing
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JFrame frame = new JFrame();
        EventQueue.invokeLater(() -> {
            frame.setLocation(point);
            frame.setExtendedState(JFrame.NORMAL);
            frame.setUndecorated(true);
            frame.setResizable(false);
            frame.setVisible(true);
            frame.add(this);
            frame.setAlwaysOnTop(true);
            frame.pack();
        });
        try {
            semaphore.acquire();
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            frame.dispose();
        }
        return selectedPiece;
    }

    /**
     * when the mouse is clicked, checks if it's a valid click and  sets the selected piece
     *
     * @param e MouseEvent
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        Location pointerLoc;
        try {
            pointerLoc = pointerToLocation(e, false);
        } catch (InvalidMoveException exc) {
            return;
        }
        if (pointerLoc.equals(new Location("A8"))) selectedPiece = "q";
        else if (pointerLoc.equals(new Location("A7"))) selectedPiece = "n";
        else if (pointerLoc.equals(new Location("A6"))) selectedPiece = "r";
        else if (pointerLoc.equals(new Location("A5"))) selectedPiece = "b";
        semaphore.release();
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
