package HotelManagement.apiIntergrationSecurity.patnerDetails;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PartnersService {

    @Autowired
    private PartnersRepo partnersRepo;

   public PartnerInfo registerNewPartner(PartnerRegisterDto partnerRegisterDto){
       PartnerInfo partnerInfo = new PartnerInfo();
       partnerInfo.setClientFirstName(partnerRegisterDto.getClientFirstName());
       partnerInfo.setClientLastName(partnerRegisterDto.getClientLastName());
       partnerInfo.setEmail(partnerRegisterDto.getEmail());
       partnerInfo.setEmail(partnerRegisterDto.getEmail());
       return  partnersRepo.save(partnerInfo);
   }

    public PartnerInfo updatePartner(Long id, PartnerRegisterDto dto) {
        PartnerInfo partner = partnersRepo.findByIdAndDeletedFlag(id,"N")
                .orElseThrow(() -> new EntityNotFoundException("Partner not found: " + id));

        partner.setClientFirstName(dto.getClientFirstName());
        partner.setClientLastName(dto.getClientLastName());
        partner.setEmail(dto.getEmail());
        // any other updatable fields…

        return partnersRepo.save(partner);
    }

    public void deletePartner(Long id) {
        PartnerInfo partner = partnersRepo.findByIdAndDeletedFlag(id, "N")
                .orElseThrow(() -> new EntityNotFoundException("Partner not found: " + id));

        partner.setDeletedFlag("Y");
        partnersRepo.save(partner);
    }

    public PartnerInfo getPartnerById(Long id) {
        return partnersRepo.findByIdAndDeletedFlag(id,"N")
                .orElseThrow(() -> new EntityNotFoundException("Partner not found: " + id));
    }

    public List<PartnerInfo> getAllPartners() {
        return partnersRepo.findAllByDeletedFlag("N");
    }
}