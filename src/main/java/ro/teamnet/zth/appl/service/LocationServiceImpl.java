package ro.teamnet.zth.appl.service;

import ro.teamnet.zth.api.annotations.MyService;
import ro.teamnet.zth.appl.dao.LocationDao;
import ro.teamnet.zth.appl.domain.Location;

import java.util.List;

/**
 * Created by Oana.Mihai on 7/15/2016.
 */
@MyService
public class LocationServiceImpl implements LocationService {

    private final LocationDao locationDao = new LocationDao();

    @Override
    public List<Location> findAll() {
        return locationDao.getAllLocations();
    }

    @Override
    public Location findOne(Long locationId) {
        return locationDao.getLocationById(locationId);
    }
}
