package sample.object.card;

import java.util.List;

/**
 * Created by vcatalano on 12/31/15.
 */
public interface ICardRepository {

  List<Card> readAll();
  void create(Card card);

}
