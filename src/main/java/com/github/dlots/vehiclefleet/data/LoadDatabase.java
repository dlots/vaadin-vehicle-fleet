package com.github.dlots.vehiclefleet.data;

import com.github.dlots.vehiclefleet.data.entity.*;
import com.github.dlots.vehiclefleet.data.repository.ManagerRepository;
import com.github.dlots.vehiclefleet.service.CrmService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@Configuration
public class LoadDatabase {
    @Bean
    CommandLineRunner initDatabase(CrmService service, ManagerRepository managerRepository) {
        return args -> {
            Enterprise enterprise1 = new Enterprise("enterprise1", null, null);
            Enterprise enterprise2 = new Enterprise("enterprise2", null, null);
            Enterprise enterprise3 = new Enterprise("enterprise3", null, null);
            service.saveEnterprises(enterprise1, enterprise2, enterprise3);

            Manager manager1 = new Manager("manager1", (new BCryptPasswordEncoder()).encode("password"), List.of(enterprise1, enterprise2));
            Manager manager2 = new Manager("manager2", (new BCryptPasswordEncoder()).encode("password"), List.of(enterprise2, enterprise3));
            managerRepository.saveAllAndFlush(List.of(manager1, manager2));

            Driver driver1 = new Driver("driver1", 3000, enterprise1, null);
            Driver driver2 = new Driver("driver2", 2000, enterprise1, null);
            Driver unemployed = new Driver("Some Guy", 0, enterprise1, null);
            Driver driver3 = new Driver("Driver3", 1235, enterprise2, null);
            Driver driver4 = new Driver("Driver4", 5361, enterprise2, null);
            Driver driver5 = new Driver("Driver5", 7533, enterprise3, null);
            Driver driver6 = new Driver("Driver6", 1257, enterprise3, null);
            service.saveDrivers(driver1, driver2, unemployed, driver3, driver4, driver5, driver6);

            // 1C3BC55D0CG133270 1982 Chrysler LeBaron
            VehicleModel chrysler1 = new VehicleModel("Chrysler", "LeBaron", VehicleType.CAR, 50, 500, 5);
            service.saveVehicleModel(chrysler1);
            Vehicle car1 = new Vehicle(chrysler1, enterprise1, List.of(driver1), driver1, "vin__enterprise_1", 2000, 1982, 100000);
            service.saveVehicle(car1);
            // 1C3BC55D0CG133270 1982 Chrysler LeBaron
            VehicleModel harley1 = new VehicleModel("Harley Davidson", "Flht", VehicleType.MOTORCYCLE, 18, 100, 1);
            service.saveVehicleModel(harley1);
            Vehicle bike1 = new Vehicle(harley1, enterprise1, List.of(driver2), driver2,"vin__enterprise_1", 3500, 1987, 30000);
            service.saveVehicle(bike1);

            // 1C3BC55D0CG133270 1982 Chrysler LeBaron
            VehicleModel chrysler2 = new VehicleModel("Chrysler", "LeBaron", VehicleType.CAR, 50, 500, 5);
            service.saveVehicleModel(chrysler2);
            Vehicle car2 = new Vehicle(chrysler2, enterprise2, List.of(driver3), driver3, "vin__enterprise_2", 2000, 1982, 100000);
            service.saveVehicle(car2);
            // 1C3BC55D0CG133270 1982 Chrysler LeBaron
            VehicleModel harley2 = new VehicleModel("Harley Davidson", "Flht", VehicleType.MOTORCYCLE, 18, 100, 1);
            service.saveVehicleModel(harley2);
            Vehicle bike2 = new Vehicle(harley2, enterprise2, List.of(driver4), driver4,"vin__enterprise_2", 3500, 1987, 30000);
            service.saveVehicle(bike2);

            // 1C3BC55D0CG133270 1982 Chrysler LeBaron
            VehicleModel chrysler3 = new VehicleModel("Chrysler", "LeBaron", VehicleType.CAR, 50, 500, 5);
            service.saveVehicleModel(chrysler3);
            Vehicle car3 = new Vehicle(chrysler3, enterprise3, List.of(driver5), driver5, "vin__enterprise_3", 2000, 1982, 100000);
            service.saveVehicle(car3);
            // 1C3BC55D0CG133270 1982 Chrysler LeBaron
            VehicleModel harley3 = new VehicleModel("Harley Davidson", "Flht", VehicleType.MOTORCYCLE, 18, 100, 1);
            service.saveVehicleModel(harley3);
            Vehicle bike3 = new Vehicle(harley3, enterprise3, List.of(driver6), driver6,"vin__enterprise_3", 3500, 1987, 30000);
            service.saveVehicle(bike3);

            driver1.setVehicle(car1);
            driver2.setVehicle(bike1);
            driver3.setVehicle(car2);
            driver4.setVehicle(bike2);
            driver5.setVehicle(car3);
            driver6.setVehicle(bike3);
            service.saveDrivers(driver1, driver2, driver3, driver4, driver5, driver6);
        };
    }
}