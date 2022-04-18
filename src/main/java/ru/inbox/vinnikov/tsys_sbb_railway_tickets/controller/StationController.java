package ru.inbox.vinnikov.tsys_sbb_railway_tickets.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.inbox.vinnikov.tsys_sbb_railway_tickets.service.RwStationService;

@Controller
@RequiredArgsConstructor
public class StationController {
    private final RwStationService rwStationService;

    @GetMapping("/account/add_new_rwstation")
    public String addNewRwStation(Model model)
    {
        String allStationsNames = rwStationService.getAllStationsNamesStr();
        model.addAttribute("allstations", allStationsNames);
        return "account_adminAdd_new_rwstation";
    }
    @PostMapping("/account/add_new_rwstation_handler")
    public String addNewRwStationHandler(String rwstationName, Model model)
    {
        // исправить на ResultDTO
        rwStationService.serviceAddNewRwstationNameHandler(rwstationName);
        String allStationsNames = rwStationService.getAllStationsNamesStr();
        model.addAttribute("allstations", allStationsNames);
        return "account_adminAdd_new_rwstation";
    }
}
