package com.workingbit.share.domain;

import com.workingbit.share.domain.impl.Draught;
import com.workingbit.share.domain.impl.Square;

import java.util.List;

/**
 * Created by Aleksey Popryaduhin on 11:22 13/08/2017.
 */
public interface IBoardContainer extends Changeable {

  String getId();

  void setId(String id);

  List<Square> getSquares();

  List<Draught> getWhiteDraughts();

  List<Draught> getBlackDraughts();

  Square getSelectedSquare();

  void setSelectedSquare(Square selectedSquare);
}
