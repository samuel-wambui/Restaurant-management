package HotelManagement.apiIntergrationSecurity.patnerDetails;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface PartnersRepo extends JpaRepository<PartnerInfo, Long> {
    // find only non-deleted partners
    List<PartnerInfo> findAllByDeletedFlag(String deletedFlag);

    Optional<PartnerInfo> findByIdAndDeletedFlag(Long id, String deletedFlag);

}
