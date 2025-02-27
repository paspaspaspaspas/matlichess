package it.matlice.matlichess.view;

import it.matlice.matlichess.Location;
import it.matlice.matlichess.exceptions.InvalidMoveException;
import it.matlice.settings.Settings;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Graphic view of a single chess piece
 */
public class PieceView {

    private final PieceType pieceType;
    private final Location location;
    private ScreenLocation offset = new ScreenLocation();

    public PieceView(PieceType pieceType, Location location) {
        this.pieceType = pieceType;
        this.location = location;
    }

    /**
     * Converts a Location on the chessboard to a Point on the screen
     *
     * @param l      Location to convert
     * @param invert if false it is count as White, else as black
     * @return the {@link ScreenLocation}
     */
    public static ScreenLocation locationToPointer(Location l, boolean invert) {
        int xCoord, yCoord;
        if (!invert) {
            xCoord = l.col() * Settings.CHESSBOARD_SQUARE_SIZE;
            yCoord = (7 - l.row()) * Settings.CHESSBOARD_SQUARE_SIZE;
        } else {
            xCoord = (7 - l.col()) * Settings.CHESSBOARD_SQUARE_SIZE;
            yCoord = l.row() * Settings.CHESSBOARD_SQUARE_SIZE;
        }
        return new ScreenLocation(xCoord, yCoord);
    }

    public static ScreenLocation locationToPointer(Location l) {
        return locationToPointer(l, false);
    }

    /**
     * Converts a a Point on the screen to a Location on the chessboard
     *
     * @param e      MouseEvent
     * @param invert if false it is count as White, else as black
     * @return the {@link Location}
     */
    public static Location pointerToLocation(MouseEvent e, boolean invert) {
        int row, col;
        if (!invert) {
            col = e.getX() / (Settings.CHESSBOARD_SQUARE_SIZE);
            row = (Settings.CHESSBOARD_SIZE - e.getY()) / (Settings.CHESSBOARD_SQUARE_SIZE);
        } else {
            col = (Settings.CHESSBOARD_SIZE - e.getX()) / (Settings.CHESSBOARD_SQUARE_SIZE);
            row = e.getY() / (Settings.CHESSBOARD_SQUARE_SIZE);
        }

        if (0 <= col && col < 8 && 0 <= row && row < 8)
            return new Location(col, row);
        else
            throw new InvalidMoveException();
    }

    public static Location pointerToLocation(MouseEvent e) {
        return pointerToLocation(e, false);
    }

    /**
     * Draws the chess piece using the saved resources
     *
     * @param g2     a Graphics2D object
     * @param invert if false it is count as White, else as black
     */
    public void draw(Graphics2D g2, boolean invert) {
        Settings.PIECES[pieceType.index].accept(g2, locationToPointer(location, invert), offset);
    }

    public void draw(Graphics2D g2) {
        this.draw(g2, false);
    }

    public Location getLocation() {
        return this.location;
    }

    public PieceType getPieceType() {
        return pieceType;
    }

    public void setOffset(ScreenLocation d) {
        this.offset = d;
    }

    public void resetOffset() {
        this.offset = new ScreenLocation();
    }

}
