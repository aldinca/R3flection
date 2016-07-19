package ro.teamnet.zth.appl.controller;

import ro.teamnet.zth.api.annotations.InjectService;
import ro.teamnet.zth.api.annotations.MyController;
import ro.teamnet.zth.api.annotations.MyRequestMethod;
import ro.teamnet.zth.api.annotations.MyRequestParam;
import ro.teamnet.zth.appl.domain.Location;
import ro.teamnet.zth.appl.service.LocationService;

import java.util.List;

@MyController(urlPath = "/locations")
public class LocationController {
    private final LocationService locationService;

    @InjectService
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @MyRequestMethod(urlPath = "/all")
    public List<Location> getAll() {
        return locationService.findAll();
    }

    @MyRequestMethod(urlPath = "/one")
    public Location getOne(@MyRequestParam(name = "id") Long locationId) {
        return locationService.findOne(locationId);
    }
}
