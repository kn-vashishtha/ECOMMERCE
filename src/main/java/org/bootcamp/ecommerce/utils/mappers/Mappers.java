package org.bootcamp.ecommerce.utils.mappers;

import org.bootcamp.ecommerce.dto.address.AddressRequestCustomer;
import org.bootcamp.ecommerce.dto.address.AddressRequestSeller;
import org.bootcamp.ecommerce.dto.address.AddressResponseDTO;
import org.bootcamp.ecommerce.entity.Address;
import org.bootcamp.ecommerce.enums.Label;

public class Mappers {
    private Mappers() {

    }

    public static AddressResponseDTO addressDTOToAddressMapper(Address address) {
        return new AddressResponseDTO(address.getId().toString(), address.getCity(), address.getState(), address.getCountry(), address.getAddressLine(), address.getZipCode(), address.getLabel());
    }

    public static Address addressToSellerAddressResponseDTOMapper(AddressResponseDTO addressResponseDTO) {
        return Address.builder()
                .city(addressResponseDTO.city())
                .state(addressResponseDTO.state())
                .country(addressResponseDTO.country())
                .addressLine(addressResponseDTO.addressLine())
                .zipCode(addressResponseDTO.zipCode())
                .label(addressResponseDTO.label())
                .build();
    }

    public static Address addressToSellerAddressRequestMapper(AddressRequestSeller addressRequest) {
        return Address.builder()
                .city(addressRequest.city())
                .state(addressRequest.state())
                .country(addressRequest.country())
                .addressLine(addressRequest.addressLine())
                .zipCode(addressRequest.zipCode())
                .label(Label.COMPANY)
                .build();
    }

    public static Address addressToCustomerAddressRequestMapper(AddressRequestCustomer addressRequest) {
        return Address.builder()
                .city(addressRequest.city().trim())
                .state(addressRequest.state().trim())
                .country(addressRequest.country().trim())
                .addressLine(addressRequest.addressLine().trim())
                .zipCode(addressRequest.zipCode())
                .label(addressRequest.label())
                .build();
    }


}
