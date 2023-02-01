package com.github.dlots.vehiclefleet.data;

import com.github.dlots.vehiclefleet.data.entity.*;
import com.github.dlots.vehiclefleet.data.repository.ManagerRepository;
import com.github.dlots.vehiclefleet.service.CrmService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.TimeZone;

@Configuration
public class LoadDatabase {
    @Bean
    CommandLineRunner initDatabase(CrmService crmService, ManagerRepository managerRepository) {
        return args -> {
            Enterprise enterprise1 = new Enterprise("enterprise1", TimeZone.getDefault(), null, null);
            Enterprise enterprise2 = new Enterprise("enterprise2", TimeZone.getTimeZone("Asia/Tokyo"), null, null);
            Enterprise enterprise3 = new Enterprise("enterprise3", null, null, null);
            crmService.saveEnterprises(enterprise1, enterprise2, enterprise3);

            Manager manager1 = new Manager("manager1", (new BCryptPasswordEncoder()).encode("password"), List.of(enterprise1, enterprise2));
            Manager manager2 = new Manager("manager2", (new BCryptPasswordEncoder()).encode("password"), List.of(enterprise2, enterprise3));
            Manager admin = new Manager("admin", (new BCryptPasswordEncoder()).encode("password"), List.of(enterprise1, enterprise2, enterprise3));
            managerRepository.saveAllAndFlush(List.of(manager1, manager2, admin));
            crmService.saveEnterprises(enterprise1, enterprise2, enterprise3);

            VehicleModel chrysler = new VehicleModel("Chrysler", "LeBaron", VehicleType.CAR, 50, 500, 5);
            VehicleModel harley = new VehicleModel("Harley Davidson", "Flht", VehicleType.MOTORCYCLE, 18, 100, 1);
            crmService.saveVehicleModel(chrysler);
            crmService.saveVehicleModel(harley);

            Driver driver1 = new Driver("driver1", 3000, enterprise1, null);
            Driver driver2 = new Driver("driver2", 2000, enterprise1, null);
            Driver unemployed = new Driver("Some Guy", 0, enterprise1, null);
            Driver driver3 = new Driver("Driver3", 1235, enterprise2, null);
            Driver driver4 = new Driver("Driver4", 5361, enterprise2, null);
            Driver driver5 = new Driver("Driver5", 7533, enterprise3, null);
            Driver driver6 = new Driver("Driver6", 1257, enterprise3, null);
            crmService.saveDrivers(driver1, driver2, unemployed, driver3, driver4, driver5, driver6);

            Instant second = Instant.now();
            Instant first = second.minus(15, ChronoUnit.MINUTES);
            List<GpsPoint> gpsTrack = List.of(GpsPoint.of(55.767484, 38.661334, first), GpsPoint.of(55.777692, 38.673192, second));
            List<Ride> rides = List.of(Ride.of(first, second));

            Vehicle car1 = new Vehicle(chrysler, enterprise1, List.of(driver1), driver1, "vin__enterprise_1", 2000, 1982, 100000, Instant.now(), gpsTrack, rides);
            crmService.saveVehicle(car1);
            Vehicle bike1 = new Vehicle(harley, enterprise1, List.of(driver2), driver2,"vin__enterprise_1", 3500, 1987, 30000, Instant.now());
            crmService.saveVehicle(bike1);

            Vehicle car2 = new Vehicle(chrysler, enterprise2, List.of(driver3), driver3, "vin__enterprise_2", 2000, 1982, 100000, Instant.now());
            crmService.saveVehicle(car2);
            Vehicle bike2 = new Vehicle(harley, enterprise2, List.of(driver4), driver4,"vin__enterprise_2", 3500, 1987, 30000, Instant.now());
            crmService.saveVehicle(bike2);

            Vehicle car3 = new Vehicle(chrysler, enterprise3, List.of(driver5), driver5, "vin__enterprise_3", 2000, 1982, 100000, Instant.now());
            crmService.saveVehicle(car3);
            Vehicle bike3 = new Vehicle(harley, enterprise3, List.of(driver6), driver6,"vin__enterprise_3", 3500, 1987, 30000, Instant.now());
            crmService.saveVehicle(bike3);
        };
    }
}
