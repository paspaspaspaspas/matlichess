package it.matlice.matlichess.model.pieces;

import it.matlice.matlichess.Location;
import it.matlice.matlichess.PieceColor;
import it.matlice.matlichess.model.Chessboard;
import it.matlice.matlichess.model.MoveList;
import it.matlice.matlichess.model.MovePattern;
import it.matlice.matlichess.model.Piece;

import java.util.Map;

/**
 * Identifies the King Piece in a chess game
 */
public class King extends Piece {

    private final Location BLACK_QUEEN_ROOK_LOCATION = new Location("A8");
    private final Location BLACK_KING_ROOK_LOCATION = new Location("H8");
    private final Location WHITE_QUEEN_ROOK_LOCATION = new Location("A1");
    private final Location WHITE_KING_ROOK_LOCATION = new Location("H1");

    public King(PieceColor pieceColor) {
        super("King", "K", Math.abs(~0), pieceColor);
    }

    /**
     * Given a chessboard and a position, returns if the position is under attack
     *
     * @param chessboard the {@link Chessboard} where are placed the pieces
     * @param location   the location to check
     * @return true if the king is under attack, else false
     */
    public boolean isUnderCheck(Chessboard chessboard, Location location) {
        for (Map<Piece, Location> family : chessboard.getPieces().values()) {
            for (Map.Entry<Piece, Location> entry : family.entrySet()) {
                if (entry.getKey() instanceof King) break;
                // value contains an opponent Piece and his Location
                if (entry.getKey().getColor().equals(this.getColor().opponent()))
                    // unvalidated_move_pattern is used because is not necessary to move the opponent piece to check
                    if (entry.getKey().unvalidated_move_pattern(chessboard, entry.getValue()).get().containsKey(location))
                        return true;
            }
        }
        return false;
    }

    /**
     * Given a chessboard, returns if the king is under attack
     *
     * @param chessboard the {@link Chessboard} where are placed the pieces
     * @return true if the king is under attack, else false
     */
    public boolean isUnderCheck(Chessboard chessboard) {
        return isUnderCheck(chessboard, chessboard.getPieces().get("King").get(chessboard.getKing(this.getColor())));
    }

    /**
     * Check whether the king has the possibility to castle queen's side, doesn't check for particular position that prevents the castling
     *
     * @param c chessboard
     * @return true if queen side castling is available
     */
    public boolean isQueenCastlingAvailable(Chessboard c) {
        Piece rook = c.getPieceAt(this.getColor().equals(PieceColor.WHITE) ? WHITE_QUEEN_ROOK_LOCATION : BLACK_QUEEN_ROOK_LOCATION);
        if (rook == null) return false;
        return rook.getName().equals("Rook") && !rook.hasMoved() && !this.has_moved;
    }

    /**
     * Check whether the king has the possibility to castle king's side, doesn't check for particular position that prevents the castling
     *
     * @param c chessboard
     * @return true if king side castling is available
     */
    public boolean isKingCastlingAvailable(Chessboard c) {
        Piece rook = c.getPieceAt(this.getColor().equals(PieceColor.WHITE) ? WHITE_KING_ROOK_LOCATION : BLACK_KING_ROOK_LOCATION);
        if (rook == null) return false;
        return rook.getName().equals("Rook") && !rook.hasMoved() && !this.has_moved;
    }

    /**
     * Check whether the castling is doable right now
     *
     * @param c    chessboard
     * @param side the side to check, "Queen" or "King"
     * @return true if can castle
     */
    public boolean canCastle(Chessboard c, String side) {
        //if the king has moved, it cannot castle
        if (this.hasMoved()) return false;
        Location king_position = this.getColor().equals(PieceColor.WHITE) ? new Location("E1") : new Location("E8");
        //if the king is under check, it cannot castle
        if (this.isUnderCheck(c, king_position)) return false;

        switch (side) {
            case "Queen": {
                //castling queen side
                //cant castle if the rook has moved or have been taken
                if (!isQueenCastlingAvailable(c)) return false;

                //there are some pieces between me and the rook?
                //does the king have to cross attacked locations?
                if (this.getColor().equals(PieceColor.WHITE)) {
                    if (c.getPieceAt(new Location("D1")) != null || c.getPieceAt(new Location("C1")) != null || c.getPieceAt(new Location("B1")) != null)
                        return false;
                    return !this.isUnderCheck(c, new Location("D1")) && !this.isUnderCheck(c, new Location("C1"));
                } else {
                    if (c.getPieceAt(new Location("D8")) != null || c.getPieceAt(new Location("C8")) != null || c.getPieceAt(new Location("B8")) != null)
                        return false;
                    return !this.isUnderCheck(c, new Location("D8")) && !this.isUnderCheck(c, new Location("C8"));
                }
            }
            case "King": {
                //castling king side
                //cant castle if the rook has moved or have been taken
                if (!isKingCastlingAvailable(c)) return false;

                //there are some pieces between me and the rook?
                //does the king have to cross attacked locations?
                if (this.getColor().equals(PieceColor.WHITE)) {
                    if (c.getPieceAt(new Location("F1")) != null || c.getPieceAt(new Location("G1")) != null)
                        return false;
                    return !this.isUnderCheck(c, new Location("F1")) && !this.isUnderCheck(c, new Location("G1"));
                } else {
                    if (c.getPieceAt(new Location("F8")) != null || c.getPieceAt(new Location("G8")) != null)
                        return false;
                    return !this.isUnderCheck(c, new Location("F8")) && !this.isUnderCheck(c, new Location("G8"));
                }
            }
            default:
                return false;
        }
    }

    /**
     * Describes the Locations reachable by a chess King, making sure two kings cannot get into two nearby cells
     *
     * @param chessboard the {@link Chessboard} where are placed the pieces, also CHECKING whether the king si under attack
     * @param myPosition the Position of the Piece
     * @return the MovePattern of the piece
     */
    @Override
    public MoveList getAvailableMoves(Chessboard chessboard, Location myPosition) {
        MoveList moves = this.unvalidated_move_pattern(chessboard, myPosition).validate().get();
        King other_king = chessboard.getOpponentKing(getColor());
        Location oth_k_location = chessboard.getPieces().get("King").get(other_king);

        MoveList r = new MoveList();
        moves.keySet().forEach(e -> {
            if (!(Math.abs(e.row() - oth_k_location.row()) <= 1 && Math.abs(e.col() - oth_k_location.col()) <= 1))
                r.put(e, moves.get(e));
        });
        return r;
    }

    /**
     * Describes the Locations reachable by a chess King without checking if the king is under attack
     *
     * @param chessboard the {@link Chessboard} where are placed the pieces
     * @param myPosition the Position of the King
     * @return the MovePattern of the piece without checking if the king is under attack
     */
    @Override
    public MovePattern unvalidated_move_pattern(Chessboard chessboard, Location myPosition) {
        return new MovePattern(chessboard, myPosition, this.getColor())
                .addKing();
    }

    /**
     * Abstract method that clone a King into an identical other King
     *
     * @return the cloned King
     */
    @Override
    public Piece clone() {
        King clone = new King(this.getColor());
        clone.has_moved = this.has_moved;
        return clone;
    }

}
