package it.matlice.malichess.movements;

import it.matlice.malichess.ChessboardTest;
import it.matlice.matlichess.exceptions.InvalidMoveException;
import it.matlice.matlichess.PieceColor;
import it.matlice.matlichess.Location;
import it.matlice.matlichess.model.pieces.Bishop;
import it.matlice.matlichess.model.pieces.King;
import it.matlice.matlichess.model.pieces.Rook;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Castling {

    @Test
    public void basicCastling() {

        ChessboardTest c = new ChessboardTest();

        King k1 = new King(PieceColor.WHITE);
        King k2 = new King(PieceColor.BLACK);

        Rook r1 = new Rook(PieceColor.WHITE);
        Rook r2 = new Rook(PieceColor.WHITE);
        Rook r5 = new Rook(PieceColor.BLACK);
        Rook r6 = new Rook(PieceColor.BLACK);

        c.setKing(k1, new Location("E1"));
        c.setKing(k2, new Location("E8"));

        c.setPiece(r1, new Location("A1"));
        c.setPiece(r2, new Location("H1"));
        c.setPiece(r5, new Location("A8"));
        c.setPiece(r6, new Location("H8"));

        assertDoesNotThrow(() -> c.clone().move("E1", "C1"));
        assertDoesNotThrow(() -> c.clone().move("E1", "G1"));

        c.changeTurn();
        assertDoesNotThrow(() -> c.clone().move("E8", "C8"));
        assertDoesNotThrow(() -> c.clone().move("E8", "G8"));


        Bishop b9 = new Bishop(PieceColor.BLACK);

        c.setPiece(b9, new Location("D3"));

        c.changeTurn();
        assertDoesNotThrow(() -> c.clone().move("E1", "C1"));
        assertThrows(InvalidMoveException.class, () -> c.clone().move("E1", "G1"));

        Rook r8 = new Rook(PieceColor.BLACK);
        c.setPiece(r8, new Location("C6"));

        assertThrows(InvalidMoveException.class, () -> c.clone().move("E1", "C1"));
        assertThrows(InvalidMoveException.class, () -> c.clone().move("E1", "G1"));

        c.changeTurn();
        assertDoesNotThrow(() -> c.clone().move("E8", "C8"));
        assertDoesNotThrow(() -> c.clone().move("E8", "G8"));


        c.move("E8", "D8");
        c.move("h1", "g1");
        c.move("D8", "E8");
        c.move("g1", "h1");

        assertThrows(InvalidMoveException.class, () -> c.clone().move("E8", "C8"));
        assertThrows(InvalidMoveException.class, () -> c.clone().move("E8", "G8"));

        c.move("d3", "e4");

        assertThrows(InvalidMoveException.class, () -> c.clone().move("E1", "G1"));
        c.move("h1", "g1");
        c.move("c6", "e6");

        assertDoesNotThrow(() -> c.move("e1", "c1"));
        System.out.println(c);


    }

}
