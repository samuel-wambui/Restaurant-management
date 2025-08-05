package HotelManagement.apiIntergrationSecurity.patnerDetails;

import HotelManagement.ApiResponse.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/partners")

public class PartnersRegistrationController {
    @Autowired
    private PartnersService partnersService;

@PostMapping("/registerPartner")
    public ResponseEntity<ApiResponse<PartnerInfo>> registerPartner(
            @RequestBody PartnerRegisterDto dto) {


        PartnerInfo partnerInfo = partnersService.registerNewPartner(dto);
        ApiResponse<PartnerInfo> response = new ApiResponse<>();
        response.setStatusCode(HttpStatus.CREATED.value());
        response.setMessage("Partner created successfully!");
        response.setEntity(partnerInfo);


        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @PutMapping("updatePartner/{id}")
    public ApiResponse<PartnerInfo> updatePartner(
            @PathVariable Long id,
            @RequestBody PartnerRegisterDto dto) {

        PartnerInfo updated = partnersService.updatePartner(id, dto);
        ApiResponse<PartnerInfo> resp = new ApiResponse<>();
        resp.setStatusCode(HttpStatus.OK.value());
        resp.setMessage("Partner updated successfully!");
        resp.setEntity(updated);
        return resp;
    }

    @DeleteMapping("delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePartner(@PathVariable Long id) {
        partnersService.deletePartner(id);
    }

    @GetMapping("partnerInfo/{id}")
    public ApiResponse<PartnerInfo> getPartner(@PathVariable Long id) {
        PartnerInfo found = partnersService.getPartnerById(id);
        ApiResponse<PartnerInfo> resp = new ApiResponse<>();
        resp.setStatusCode(HttpStatus.OK.value());
        resp.setMessage("Partner fetched successfully!");
        resp.setEntity(found);
        return resp;
    }

    @GetMapping("/getAllPartners")
    public ApiResponse<List<PartnerInfo>> listPartners() {
        List<PartnerInfo> list = partnersService.getAllPartners();
        ApiResponse<List<PartnerInfo>> resp = new ApiResponse<>();
        resp.setStatusCode(HttpStatus.OK.value());
        resp.setMessage("Partners fetched successfully!");
        resp.setEntity(list);
        return resp;
    }
}

