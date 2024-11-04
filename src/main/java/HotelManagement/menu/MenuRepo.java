package HotelManagement.menu;

import HotelManagement.recipe.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepo extends JpaRepository<Menu, Long> {
    List<Menu> findAllByDeletedFlag(String deletedFlag);
    Optional<Menu> findByIdAndDeletedFlag (Long id, String deletedFlag);
}
