package com.api.parkingcontrol.controllers;

import com.api.parkingcontrol.dtos.ParkingSpotDto;
import com.api.parkingcontrol.models.ParkingSpotModel;
import com.api.parkingcontrol.services.ParkingSpotService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/parking-spot")
public class ParkingSpotController {

    final ParkingSpotService parkingSpotService;

    public ParkingSpotController(ParkingSpotService parkingSpotService) {
        this.parkingSpotService = parkingSpotService;
    }

    @PostMapping
    @ApiOperation(value = "Realiza a alocação da vaga para o veículo.")
    public ResponseEntity<Object> saveParkingSpot(@RequestBody @Valid ParkingSpotDto parkingSpotDto){

        var parkingSpotModel = new ParkingSpotModel();
        BeanUtils.copyProperties(parkingSpotDto, parkingSpotModel);

        String msgBody = parkingSpotService.validationParkingSpot(parkingSpotModel);

        if(!msgBody.isEmpty()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(msgBody);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(parkingSpotService.save(parkingSpotModel));

    }

    @GetMapping
    @ApiOperation(value = "Paginação dos registros!")
    public ResponseEntity<Page<ParkingSpotModel>> getAllParkingSpotsPage(
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC)
                    Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.findAllPage(pageable));
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Lista registro específico")
    public ResponseEntity<Object> getOneParkingSpots(@PathVariable(value = "id") UUID id){

        Optional<ParkingSpotModel> spotModel = parkingSpotService.findById(id);
        if(!spotModel.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vaga não encontrada!");
        }

        return ResponseEntity.status(HttpStatus.OK).body(spotModel.get());
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Exclui registro.")
    public ResponseEntity<Object> deleteParkingSpots(@PathVariable(value = "id") UUID id){

        Optional<ParkingSpotModel> spotModel = parkingSpotService.findById(id);
        if(!spotModel.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vaga não encontrada!");
        }
        parkingSpotService.delete(spotModel.get());

        return ResponseEntity.status(HttpStatus.OK).body("Vaga excluída cpm sucesso!");
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Atualiza registro.")
    public ResponseEntity<Object> upParkingSpots(
            @PathVariable(value = "id") UUID id, @RequestBody @Valid ParkingSpotDto parkingSpotDto){

        Optional<ParkingSpotModel> spotModel = parkingSpotService.findById(id);
        if(!spotModel.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vaga não encontrada!");
        }

        var parkingSpotModel = new ParkingSpotModel();
        BeanUtils.copyProperties(parkingSpotDto, parkingSpotModel);
        parkingSpotModel.setId(spotModel.get().getId());
        parkingSpotModel.setRegistrationDate(spotModel.get().getRegistrationDate());

        return ResponseEntity.status(HttpStatus.CREATED).body(parkingSpotService.save(parkingSpotModel));

    }

}
