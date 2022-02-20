package com.api.parkingcontrol.services;

import com.api.parkingcontrol.models.ParkingSpotModel;
import com.api.parkingcontrol.repositories.ParkingSpotRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ParkingSpotService {

    final ParkingSpotRepository parkingSpotRepository;

    public ParkingSpotService(ParkingSpotRepository parkingSpotRepository) {
        this.parkingSpotRepository = parkingSpotRepository;
    }

    @Transactional
    public ParkingSpotModel save(ParkingSpotModel parkingSpotModel) {

        parkingSpotModel.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC")));
        return parkingSpotRepository.save(parkingSpotModel);
    }

    public String validationParkingSpot(ParkingSpotModel parkingSpotModel){

        String msgBody = "";

        if (parkingSpotRepository.existsByLicensePlateCar(parkingSpotModel.getLicensePlateCar())) {
            msgBody = "Placa já existente!";
        } else if (parkingSpotRepository.existsByParkingSpotNumber(parkingSpotModel.getParkingSpotNumber())) {
            msgBody = "Esta vaga já está sendo ocupada!";
        } else if (parkingSpotRepository.existsByApartmentAndBlock(parkingSpotModel.getApartment(), parkingSpotModel.getBlock())) {
            msgBody = "Este apartamento já existe!";
        }

        return msgBody;
    }

    public List<ParkingSpotModel> findAll() {
        return parkingSpotRepository.findAll();
    }

    public Optional<ParkingSpotModel> findById(UUID uuid) {
        return parkingSpotRepository.findById(uuid);
    }

    @Transactional
    public void delete(ParkingSpotModel parkingSpotModel) {
        parkingSpotRepository.delete(parkingSpotModel);
    }

    public Page findAllPage(Pageable pageable) {
        return parkingSpotRepository.findAll(pageable);
    }
}
