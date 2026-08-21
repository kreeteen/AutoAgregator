package ru.vsu.cs.projectcars.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.vsu.cs.projectcars.model.*;
import ru.vsu.cs.projectcars.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.*;

@Component
@ConditionalOnProperty(name = "app.demo-data.enabled", havingValue = "true", matchIfMissing = false)
public class DemoDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataInitializer.class);

    private final ProjectTagRepository tagRepository;
    private final ModsCategoryRepository modsRepository;
    private final UserRepository userRepository;
    private final VehicleCarRepository carRepository;
    private final CarBrandRepository brandRepository;
    private final CarModelRepository modelRepository;
    private final CarGenerationRepository generationRepository;
    private final RussianRegionRepository regionRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DemoDataInitializer(ProjectTagRepository tagRepository,
                                ModsCategoryRepository modsRepository,
                                UserRepository userRepository,
                                VehicleCarRepository carRepository,
                                CarBrandRepository brandRepository,
                                CarModelRepository modelRepository,
                                CarGenerationRepository generationRepository,
                                RussianRegionRepository regionRepository,
                                RoleRepository roleRepository,
                                PasswordEncoder passwordEncoder) {
        this.tagRepository = tagRepository;
        this.modsRepository = modsRepository;
        this.userRepository = userRepository;
        this.carRepository = carRepository;
        this.brandRepository = brandRepository;
        this.modelRepository = modelRepository;
        this.generationRepository = generationRepository;
        this.regionRepository = regionRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) return;

        log.info("Initializing reference data...");
        List<RussianRegion> regions = createRegions();
        List<ProjectTag> tags = createTags();
        Map<String, Map<String, Map<String, CarGeneration>>> brandModelGen = createBrands();

        log.info("Creating roles...");
        Role roleUser = roleRepository.save(new Role("USER"));
        Role roleAdmin = roleRepository.save(new Role("ADMIN"));
        Role roleModerator = roleRepository.save(new Role("MODERATOR"));

        log.info("Creating demo users...");
        User user1 = saveUser("drift@prodrive.ru", "Алексей", "Волков", "+7 (916) 555-11-22", Set.of(roleUser));
        User user2 = saveUser("shop@tuning.ru", "Дмитрий", "Крылов", "+7 (921) 777-33-44", Set.of(roleUser));
        User user3 = saveUser("admin@race.ru", "Дмитрий", "Крестников", "+7 (495) 111-22-33", Set.of(roleUser, roleAdmin, roleModerator));

        ProjectTag drift = tags.get(0);
        ProjectTag stance = tags.get(1);
        ProjectTag offroad = tags.get(2);
        ProjectTag drag = tags.get(3);

        List<ModsCategory> driftMods = modsRepository.findByProjectTag(drift);
        List<ModsCategory> stanceMods = modsRepository.findByProjectTag(stance);
        List<ModsCategory> offroadMods = modsRepository.findByProjectTag(offroad);
        List<ModsCategory> dragMods = modsRepository.findByProjectTag(drag);

        RussianRegion moscow = findRegion(regions, "Москва");
        RussianRegion spb = findRegion(regions, "Санкт-Петербург");
        RussianRegion krasnodar = findRegion(regions, "Краснодарский край");
        RussianRegion nnovgorod = findRegion(regions, "Нижегородская область");
        RussianRegion ekb = findRegion(regions, "Свердловская область");
        RussianRegion tatarstan = findRegion(regions, "Республика Татарстан");
        RussianRegion rostov = findRegion(regions, "Ростовская область");
        RussianRegion novosibirsk = findRegion(regions, "Новосибирская область");
        RussianRegion voronezh = findRegion(regions, "Воронежская область");
        RussianRegion samara = findRegion(regions, "Самарская область");
        RussianRegion volgograd = findRegion(regions, "Волгоградская область");
        RussianRegion belgorod = findRegion(regions, "Белгородская область");
        RussianRegion kaliningrad = findRegion(regions, "Калининградская область");
        RussianRegion khabarovsk = findRegion(regions, "Хабаровский край");
        RussianRegion vladivostok = findRegion(regions, "Приморский край");
        RussianRegion krasnoyarsk = findRegion(regions, "Красноярский край");
        RussianRegion krym = findRegion(regions, "Республика Крым");
        RussianRegion bashkiria = findRegion(regions, "Республика Башкортостан");
        RussianRegion tyumen = findRegion(regions, "Тюменская область");
        RussianRegion chelyabinsk = findRegion(regions, "Челябинская область");
        RussianRegion omsk = findRegion(regions, "Омская область");
        RussianRegion irkutsk = findRegion(regions, "Иркутская область");

        log.info("Creating demo listings...");

        saveCar(user1, drift, 1997, new BigDecimal("3500000"),
                "Москва", moscow, "Легендарная Supra для дрифта. Полностью подготовлена.",
                List.of(driftMods.get(0), driftMods.get(1), driftMods.get(2), driftMods.get(3)),
                120000, null, "Бензин", 3.0, 320, "Купе", false, "Левый", true,
                findBrand(brandModelGen, "Toyota"), findModel(brandModelGen, "Toyota", "Supra"), null, "/uploads/1/photo1.jpg");

        saveCar(user1, drift, 2001, new BigDecimal("2800000"),
                "Санкт-Петербург", spb, "Spec-R, конверсия на дрифт. Минимум ржавчины.",
                List.of(driftMods.get(0), driftMods.get(1), driftMods.get(4)),
                95000, null, "Бензин", 2.0, 250, "Купе", false, "Правый", true,
                findBrand(brandModelGen, "Nissan"), findModel(brandModelGen, "Nissan", "Silvia"), null, "/uploads/2/photo2.jpg");

        saveCar(user2, stance, 1994, new BigDecimal("1500000"),
                "Москва", moscow, "Полностью перекрашен, пневма Air Lift, диски Work.",
                List.of(stanceMods.get(0), stanceMods.get(1), stanceMods.get(2), stanceMods.get(4)),
                210000, null, "Бензин", 2.5, 192, "Седан", false, "Левый", true,
                findBrand(brandModelGen, "BMW"), findModel(brandModelGen, "BMW", "3 Series"), null, "/uploads/3/photo3.jpg");

        saveCar(user1, drag, 2008, new BigDecimal("5500000"),
                "Краснодар", krasnodar, "LS3 с Procharger, 9.2 сек на четверть мили.",
                List.of(dragMods.get(0), dragMods.get(2), dragMods.get(3), dragMods.get(4)),
                65000, null, "Бензин", 6.2, 436, "Купе", false, "Левый", true,
                findBrand(brandModelGen, "Chevrolet"), findModel(brandModelGen, "Chevrolet", "Corvette"), null, "/uploads/1/photo1.jpg");

        saveCar(user2, drift, 1996, new BigDecimal("3200000"),
                "Нижний Новгород", nnovgorod, "13B-REW, BorgWarner EFR 9180, полная подготовка к дрифту.",
                List.of(driftMods.get(0), driftMods.get(1), driftMods.get(2)),
                98000, null, "Бензин", 1.3, 280, "Купе", false, "Правый", true,
                findBrand(brandModelGen, "Mazda"), findModel(brandModelGen, "Mazda", "RX-7"), null, "/uploads/1/photo1.jpg");

        saveCar(user1, stance, 2006, new BigDecimal("4200000"),
                "Екатеринбург", ekb, "Time Attack, 4G63 550л.с., секвенталка, каркас.",
                List.of(stanceMods.get(0), stanceMods.get(3), stanceMods.get(4), stanceMods.get(1)),
                85000, null, "Бензин", 2.0, 280, "Седан", false, "Левый", true,
                findBrand(brandModelGen, "Mitsubishi"), findModel(brandModelGen, "Mitsubishi", "Lancer Evolution"), null, "/uploads/1/photo1.jpg");

        saveCar(user3, offroad, 2018, new BigDecimal("3500000"),
                "Казань", tatarstan, "Надёжный внедорожник для экспедиций.",
                List.of(offroadMods.get(0), offroadMods.get(1), offroadMods.get(2), offroadMods.get(3), offroadMods.get(4)),
                120000, 4500, "Дизель", 3.0, 177, "Внедорожник", false, "Левый", true,
                findBrand(brandModelGen, "Toyota"), findModel(brandModelGen, "Toyota", "Land Cruiser Prado"), null, "/uploads/1/photo1.jpg");

        saveCar(user2, drag, 2019, new BigDecimal("8900000"),
                "Ростов-на-Дону", rostov, "Hellcat! 717 л.с., полный сток.",
                List.of(dragMods.get(0), dragMods.get(2)),
                22000, null, "Бензин", 6.2, 717, "Купе", false, "Левый", true,
                findBrand(brandModelGen, "Dodge"), findModel(brandModelGen, "Dodge", "Challenger"), null, "/uploads/1/photo1.jpg");

        saveCar(user3, offroad, 2021, new BigDecimal("1200000"),
                "Новосибирск", novosibirsk, "Полный привод, лебедка, шноркель. Подготовлен к бездорожью.",
                List.of(offroadMods.get(0), offroadMods.get(1), offroadMods.get(2), offroadMods.get(3), offroadMods.get(4)),
                45000, 1800, "Бензин", 2.7, 135, "Внедорожник", false, "Левый", true,
                findBrand(brandModelGen, "УАЗ"), null, null, "/uploads/1/photo1.jpg");

        saveCar(user1, drift, 2017, new BigDecimal("9500000"),
                "Москва", moscow, "Godzilla! VR38DETT, полный привод, подготовка к дрифту.",
                List.of(driftMods.get(0), driftMods.get(1), driftMods.get(2), driftMods.get(3)),
                58000, null, "Бензин", 3.8, 570, "Купе", false, "Левый", true,
                findBrand(brandModelGen, "Nissan"), findModel(brandModelGen, "Nissan", "GT-R"), null, "/uploads/1/photo1.jpg");

        // ====== ADDITIONAL DEMO LISTINGS ======

        // --- Бюджетные / дешёвые ---
        saveCar(user2, drift, 1995, new BigDecimal("450000"),
                "Воронеж", voronezh, "Бюджетный вариант для дрифта. Требует вложений.",
                List.of(driftMods.get(4), driftMods.get(3)),
                250000, null, "Бензин", 2.0, 140, "Седан", false, "Левый", true,
                findBrand(brandModelGen, "BMW"), findModel(brandModelGen, "BMW", "3 Series"),
                findGen(brandModelGen, "BMW", "3 Series", "E36"), "/uploads/1/photo1.jpg");

        saveCar(user3, stance, 1998, new BigDecimal("350000"),
                "Самара", samara, "Дёшево, сердито, на развал. Хороший донор.",
                List.of(stanceMods.get(1), stanceMods.get(3)),
                280000, null, "Бензин", 1.6, 90, "Седан", true, "Левый", false,
                findBrand(brandModelGen, "LADA"), findModel(brandModelGen, "LADA", "Granta"), null, "/uploads/1/photo1.jpg");

        saveCar(user1, offroad, 1992, new BigDecimal("250000"),
                "Волгоград", volgograd, "Классика бездорожья. Ржавый, но живой.",
                List.of(offroadMods.get(0), offroadMods.get(3)),
                320000, 8000, "Бензин", 2.5, 80, "Внедорожник", true, "Левый", false,
                findBrand(brandModelGen, "УАЗ"), findModel(brandModelGen, "УАЗ", "Буханка"), null, "/uploads/1/photo1.jpg");

        // --- Элитные / дорогие ---
        saveCar(user1, drag, 2022, new BigDecimal("25000000"),
                "Москва", moscow, "McLaren 720S Performance, полный карбон, трековый пакет.",
                List.of(dragMods.get(2), dragMods.get(4)),
                15000, null, "Бензин", 4.0, 720, "Купе", false, "Левый", true,
                findBrand(brandModelGen, "McLaren"), findModel(brandModelGen, "McLaren", "720S"), null, "/uploads/1/photo1.jpg");

        saveCar(user3, drift, 2023, new BigDecimal("18000000"),
                "Казань", tatarstan, "Nissan GT-R R35 Nur Spec, 800+ л.с., полная подготовка.",
                List.of(driftMods.get(0), driftMods.get(1), driftMods.get(2), driftMods.get(3), driftMods.get(4)),
                5000, null, "Бензин", 3.8, 600, "Купе", false, "Левый", true,
                findBrand(brandModelGen, "Nissan"), findModel(brandModelGen, "Nissan", "GT-R"), null, "/uploads/1/photo1.jpg");

        saveCar(user2, stance, 2021, new BigDecimal("12500000"),
                "Москва", moscow, "Porsche 911 Turbo S, широкий кузов TechArt, Air Lift.",
                List.of(stanceMods.get(0), stanceMods.get(1), stanceMods.get(3)),
                18000, null, "Бензин", 3.8, 650, "Купе", false, "Левый", true,
                findBrand(brandModelGen, "Porsche"), findModel(brandModelGen, "Porsche", "911"),
                findGen(brandModelGen, "Porsche", "911", "992"), "/uploads/1/photo1.jpg");

        saveCar(user1, offroad, 2020, new BigDecimal("15000000"),
                "Москва", moscow, "Mercedes-Benz G-Class Brabus 700 Widestar. Всё включено.",
                List.of(offroadMods.get(0), offroadMods.get(2), offroadMods.get(3), offroadMods.get(4)),
                35000, 1200, "Бензин", 4.0, 700, "Внедорожник", false, "Левый", true,
                findBrand(brandModelGen, "Mercedes-Benz"), findModel(brandModelGen, "Mercedes-Benz", "G-Class"),
                findGen(brandModelGen, "Mercedes-Benz", "G-Class", "W463A"), "/uploads/1/photo1.jpg");

        // --- Правый руль / JDM ---
        saveCar(user2, drift, 1996, new BigDecimal("2200000"),
                "Владивосток", vladivostok, "Настоящий JDM, правый руль, 1JZ-GTE.",
                List.of(driftMods.get(0), driftMods.get(1), driftMods.get(2), driftMods.get(4)),
                130000, null, "Бензин", 2.5, 280, "Седан", false, "Правый", true,
                findBrand(brandModelGen, "Toyota"), findModel(brandModelGen, "Toyota", "Chaser"), null, "/uploads/1/photo1.jpg");

        saveCar(user1, drift, 1993, new BigDecimal("1800000"),
                "Хабаровск", khabarovsk, "Silvia Q's, SR20DE, целиком на запчасти или проект.",
                List.of(driftMods.get(0), driftMods.get(3)),
                180000, null, "Бензин", 2.0, 160, "Купе", false, "Правый", false,
                findBrand(brandModelGen, "Nissan"), findModel(brandModelGen, "Nissan", "Silvia"), null, "/uploads/1/photo1.jpg");

        saveCar(user3, drag, 2002, new BigDecimal("8500000"),
                "Владивосток", vladivostok, "Супра 2JZ-GTE, билд 900+ л.с., секвенталка.",
                List.of(dragMods.get(0), dragMods.get(2), dragMods.get(3), dragMods.get(4)),
                95000, null, "Бензин", 3.0, 320, "Купе", false, "Правый", true,
                findBrand(brandModelGen, "Toyota"), findModel(brandModelGen, "Toyota", "Supra"), null, "/uploads/1/photo1.jpg");

        // --- Дизельные ---
        saveCar(user2, offroad, 2015, new BigDecimal("4800000"),
                "Белгород", belgorod, "Дизельный внедорожник для экспедиций и охоты.",
                List.of(offroadMods.get(0), offroadMods.get(1), offroadMods.get(2), offroadMods.get(3)),
                95000, 3200, "Дизель", 4.5, 200, "Внедорожник", false, "Левый", true,
                findBrand(brandModelGen, "Toyota"), findModel(brandModelGen, "Toyota", "Land Cruiser 200"),
                findGen(brandModelGen, "Toyota", "Land Cruiser 200", "200") ,"/uploads/1/photo1.jpg");

        saveCar(user3, drag, 2016, new BigDecimal("3200000"),
                "Калининград", kaliningrad, "Passat B8 BiTDI, Stage 2, 4Motion.",
                List.of(dragMods.get(0), dragMods.get(3)),
                120000, null, "Дизель", 2.0, 240, "Универсал", false, "Левый", true,
                findBrand(brandModelGen, "Volkswagen"), findModel(brandModelGen, "Volkswagen", "Passat"),
                findGen(brandModelGen, "Volkswagen", "Passat", "B8") ,"/uploads/1/photo1.jpg");

        saveCar(user1, stance, 2019, new BigDecimal("2100000"),
                "Симферополь", krym, "BMW X3 G01 M40d, спортивный дизель.",
                List.of(stanceMods.get(0), stanceMods.get(3)),
                65000, null, "Дизель", 3.0, 326, "Кроссовер", false, "Левый", true,
                findBrand(brandModelGen, "BMW"), findModel(brandModelGen, "BMW", "X3"),
                findGen(brandModelGen, "BMW", "X3", "G01"), "/uploads/1/photo1.jpg");

        // --- Электро и Гибрид ---
        saveCar(user2, drag, 2023, new BigDecimal("9500000"),
                "Уфа", bashkiria, "Tesla Model 3 Performance, Uncork 499 л.с.",
                List.of(dragMods.get(3), dragMods.get(4)),
                25000, null, "Электро", null, 499, "Седан", false, "Левый", true,
                findBrand(brandModelGen, "Tesla"), findModel(brandModelGen, "Tesla", "Model 3"),
                findGen(brandModelGen, "Tesla", "Model 3", "I"), "/uploads/1/photo1.jpg");

        saveCar(user3, stance, 2021, new BigDecimal("5600000"),
                "Тюмень", tyumen, "Lexus RX450h Luxury, кожа, панорама.",
                List.of(stanceMods.get(0), stanceMods.get(3), stanceMods.get(4)),
                55000, null, "Гибрид", 3.5, 313, "Кроссовер", false, "Левый", true,
                findBrand(brandModelGen, "Lexus"), findModel(brandModelGen, "Lexus", "RX"),
                findGen(brandModelGen, "Lexus", "RX", "XU50"), "/uploads/1/photo1.jpg");

        // --- Битые / аварийные ---
        saveCar(user3, drift, 2006, new BigDecimal("550000"),
                "Челябинск", chelyabinsk, "E46 под восстановление, гнилая, но целая.",
                List.of(driftMods.get(0), driftMods.get(3)),
                200000, null, "Бензин", 2.2, 170, "Купе", true, "Левый", false,
                findBrand(brandModelGen, "BMW"), findModel(brandModelGen, "BMW", "3 Series"),
                findGen(brandModelGen, "BMW", "3 Series", "E46"), "/uploads/1/photo1.jpg");

        saveCar(user1, stance, 2008, new BigDecimal("350000"),
                "Омск", omsk, "Битая передняя часть, под проект стэнс.",
                List.of(stanceMods.get(1), stanceMods.get(4)),
                190000, null, "Бензин", 1.5, 105, "Хэтчбек", true, "Левый", true,
                findBrand(brandModelGen, "Honda"), findModel(brandModelGen, "Honda", "Civic"),
                findGen(brandModelGen, "Honda", "Civic", "FD"), "/uploads/1/photo1.jpg");

        saveCar(user2, drag, 2012, new BigDecimal("450000"),
                "Красноярск", krasnoyarsk, "Битая, удар в зад. Двигатель живой.",
                List.of(dragMods.get(2)),
                150000, null, "Бензин", 1.4, 140, "Седан", true, "Левый", false,
                findBrand(brandModelGen, "Ford"), findModel(brandModelGen, "Ford", "Focus"),
                findGen(brandModelGen, "Ford", "Focus", "III"), "/uploads/1/photo1.jpg");

        // --- С большим пробегом ---
        saveCar(user1, offroad, 2005, new BigDecimal("1200000"),
                "Иркутск", irkutsk, "Prado 120 с большим пробегом, но живой.",
                List.of(offroadMods.get(0), offroadMods.get(3)),
                380000, 9500, "Дизель", 3.0, 163, "Внедорожник", false, "Левый", true,
                findBrand(brandModelGen, "Toyota"), findModel(brandModelGen, "Toyota", "Land Cruiser Prado"),
                findGen(brandModelGen, "Toyota", "Land Cruiser Prado", "120"), "/uploads/1/photo1.jpg");

        // --- Спорткупе / кабриолеты ---
        saveCar(user2, drag, 2021, new BigDecimal("7500000"),
                "Краснодар", krasnodar, "Mercedes-AMG GT 63S 4-Door Coupe.",
                List.of(dragMods.get(0), dragMods.get(2), dragMods.get(4)),
                35000, null, "Бензин", 4.0, 639, "Купе", false, "Левый", true,
                findBrand(brandModelGen, "Mercedes-Benz"), findModel(brandModelGen, "Mercedes-Benz", "AMG GT"), null, "/uploads/1/photo1.jpg");

        saveCar(user3, stance, 2016, new BigDecimal("3200000"),
                "Ростов-на-Дону", rostov, "Mazda MX-5 ND, пневма, работа.",
                List.of(stanceMods.get(0), stanceMods.get(1), stanceMods.get(4)),
                42000, null, "Бензин", 2.0, 160, "Кабриолет", false, "Левый", true,
                findBrand(brandModelGen, "Mazda"), findModel(brandModelGen, "Mazda", "MX-5 Miata"), null, "/uploads/1/photo1.jpg");

        // --- Седаны представительского класса ---
        saveCar(user1, stance, 2020, new BigDecimal("11000000"),
                "Москва", moscow, "Mercedes-Benz S-Class W222, AMG-пакет.",
                List.of(stanceMods.get(0), stanceMods.get(3), stanceMods.get(4)),
                45000, null, "Бензин", 4.7, 455, "Седан", false, "Левый", true,
                findBrand(brandModelGen, "Mercedes-Benz"), findModel(brandModelGen, "Mercedes-Benz", "S-Class"),
                findGen(brandModelGen, "Mercedes-Benz", "S-Class", "W222"), "/uploads/1/photo1.jpg");

        // --- Грузовые / микроавтобусы ---
        saveCar(user3, offroad, 2020, new BigDecimal("2500000"),
                "Москва", moscow, "Transit для экспедиций, переоборудован, полный привод.",
                List.of(offroadMods.get(0), offroadMods.get(1), offroadMods.get(2)),
                80000, 2500, "Дизель", 2.0, 130, "Микроавтобус", false, "Левый", true,
                findBrand(brandModelGen, "Ford"), findModel(brandModelGen, "Ford", "Transit"), null, "/uploads/1/photo1.jpg");

        // --- Маленький объём двигателя ---
        saveCar(user2, drift, 2005, new BigDecimal("150000"),
                "Воронеж", voronezh, "Маленький литраж, отличный старт в дрифт.",
                List.of(driftMods.get(4), driftMods.get(3)),
                150000, null, "Бензин", 1.3, 85, "Хэтчбек", false, "Левый", true,
                findBrand(brandModelGen, "LADA"), findModel(brandModelGen, "LADA", "Granta"), null, "/uploads/1/photo1.jpg");

        // --- Снятые с учёта ---
        saveCar(user1, offroad, 1998, new BigDecimal("800000"),
                "Новосибирск", novosibirsk, "Снят с учёта для бездорожья.",
                List.of(offroadMods.get(0), offroadMods.get(1), offroadMods.get(3)),
                250000, 7000, "Бензин", 4.0, 180, "Внедорожник", true, "Левый", false,
                findBrand(brandModelGen, "Nissan"), findModel(brandModelGen, "Nissan", "Patrol"), null, "/uploads/1/photo1.jpg");

        // --- Разные типы кузова ---
        saveCar(user2, stance, 2015, new BigDecimal("1800000"),
                "Самара", samara, "Audi A6 Avant C7 на пневме.",
                List.of(stanceMods.get(0), stanceMods.get(1), stanceMods.get(3)),
                135000, null, "Бензин", 2.0, 190, "Универсал", false, "Левый", true,
                findBrand(brandModelGen, "Audi"), findModel(brandModelGen, "Audi", "A6"),
                findGen(brandModelGen, "Audi", "A6", "C7"), "/uploads/1/photo1.jpg");

        saveCar(user3, stance, 2018, new BigDecimal("2200000"),
                "Казань", tatarstan, "Skoda Octavia A7, занижена, литьё.",
                List.of(stanceMods.get(0), stanceMods.get(4)),
                110000, null, "Бензин", 1.8, 180, "Лифтбек", false, "Левый", true,
                findBrand(brandModelGen, "Skoda"), findModel(brandModelGen, "Skoda", "Octavia"),
                findGen(brandModelGen, "Skoda", "Octavia", "A7"), "/uploads/1/photo1.jpg");

        log.info("Demo data initialized successfully. {} listings created.", carRepository.count());
    }

    private List<RussianRegion> createRegions() {
        String[][] regions = {
            {"Республика Адыгея", "республика", "Майкоп"}, {"Республика Башкортостан", "республика", "Уфа"},
            {"Республика Бурятия", "республика", "Улан-Удэ"}, {"Республика Алтай", "республика", "Горно-Алтайск"},
            {"Республика Дагестан", "республика", "Махачкала"}, {"Республика Ингушетия", "республика", "Магас"},
            {"Кабардино-Балкарская Республика", "республика", "Нальчик"}, {"Республика Калмыкия", "республика", "Элиста"},
            {"Карачаево-Черкесская Республика", "республика", "Черкесск"}, {"Республика Карелия", "республика", "Петрозаводск"},
            {"Республика Коми", "республика", "Сыктывкар"}, {"Республика Марий Эл", "республика", "Йошкар-Ола"},
            {"Республика Мордовия", "республика", "Саранск"}, {"Республика Саха (Якутия)", "республика", "Якутск"},
            {"Республика Северная Осетия — Алания", "республика", "Владикавказ"}, {"Республика Татарстан", "республика", "Казань"},
            {"Республика Тыва", "республика", "Кызыл"}, {"Удмуртская Республика", "республика", "Ижевск"},
            {"Республика Хакасия", "республика", "Абакан"}, {"Чеченская Республика", "республика", "Грозный"},
            {"Чувашская Республика", "республика", "Чебоксары"}, {"Алтайский край", "край", "Барнаул"},
            {"Забайкальский край", "край", "Чита"}, {"Камчатский край", "край", "Петропавловск-Камчатский"},
            {"Краснодарский край", "край", "Краснодар"}, {"Красноярский край", "край", "Красноярск"},
            {"Пермский край", "край", "Пермь"}, {"Приморский край", "край", "Владивосток"},
            {"Ставропольский край", "край", "Ставрополь"}, {"Хабаровский край", "край", "Хабаровск"},
            {"Амурская область", "область", "Благовещенск"}, {"Архангельская область", "область", "Архангельск"},
            {"Астраханская область", "область", "Астрахань"}, {"Белгородская область", "область", "Белгород"},
            {"Брянская область", "область", "Брянск"}, {"Владимирская область", "область", "Владимир"},
            {"Волгоградская область", "область", "Волгоград"}, {"Вологодская область", "область", "Вологда"},
            {"Воронежская область", "область", "Воронеж"}, {"Ивановская область", "область", "Иваново"},
            {"Иркутская область", "область", "Иркутск"}, {"Калининградская область", "область", "Калининград"},
            {"Калужская область", "область", "Калуга"}, {"Кемеровская область", "область", "Кемерово"},
            {"Кировская область", "область", "Киров"}, {"Костромская область", "область", "Кострома"},
            {"Курганская область", "область", "Курган"}, {"Курская область", "область", "Курск"},
            {"Ленинградская область", "область", "Санкт-Петербург"}, {"Липецкая область", "область", "Липецк"},
            {"Магаданская область", "область", "Магадан"}, {"Московская область", "область", "Москва"},
            {"Мурманская область", "область", "Мурманск"}, {"Нижегородская область", "область", "Нижний Новгород"},
            {"Новгородская область", "область", "Великий Новгород"}, {"Новосибирская область", "область", "Новосибирск"},
            {"Омская область", "область", "Омск"}, {"Оренбургская область", "область", "Оренбург"},
            {"Орловская область", "область", "Орёл"}, {"Пензенская область", "область", "Пенза"},
            {"Псковская область", "область", "Псков"}, {"Ростовская область", "область", "Ростов-на-Дону"},
            {"Рязанская область", "область", "Рязань"}, {"Самарская область", "область", "Самара"},
            {"Саратовская область", "область", "Саратов"}, {"Сахалинская область", "область", "Южно-Сахалинск"},
            {"Свердловская область", "область", "Екатеринбург"}, {"Смоленская область", "область", "Смоленск"},
            {"Тамбовская область", "область", "Тамбов"}, {"Тверская область", "область", "Тверь"},
            {"Томская область", "область", "Томск"}, {"Тульская область", "область", "Тула"},
            {"Тюменская область", "область", "Тюмень"}, {"Ульяновская область", "область", "Ульяновск"},
            {"Челябинская область", "область", "Челябинск"}, {"Ярославская область", "область", "Ярославль"},
            {"Москва", "город фед.значения", "Москва"}, {"Санкт-Петербург", "город фед.значения", "Санкт-Петербург"},
            {"Севастополь", "город фед.значения", "Севастополь"}, {"Еврейская автономная область", "автономная область", "Биробиджан"},
            {"Ненецкий автономный округ", "автономный округ", "Нарьян-Мар"}, {"Ханты-Мансийский АО", "автономный округ", "Ханты-Мансийск"},
            {"Чукотский автономный округ", "автономный округ", "Анадырь"}, {"Ямало-Ненецкий АО", "автономный округ", "Салехард"},
            {"Донецкая Народная Республика", "республика", "Донецк"}, {"Луганская Народная Республика", "республика", "Луганск"},
            {"Запорожская область", "область", "Мелитополь"}, {"Херсонская область", "область", "Геническ"},
            {"Республика Крым", "республика", "Симферополь"}
        };
        List<RussianRegion> list = new ArrayList<>();
        for (String[] r : regions) {
            RussianRegion rr = regionRepository.save(new RussianRegion(r[0], r[1], r[2]));
            list.add(rr);
        }
        return list;
    }

    private List<ProjectTag> createTags() {
        ProjectTag drift = tagRepository.save(new ProjectTag("Дрифт"));
        drift.setDescription("Управляемые заносы, подготовка к дрифту, гидроручники, варка дифференциала — всё для скольжения боком");
        tagRepository.save(drift);
        ProjectTag stance = tagRepository.save(new ProjectTag("Станс"));
        stance.setDescription("Экстремальный низ, развал, пневмоподвеска, широкие кузова и редкие диски — искусство посадки");
        tagRepository.save(stance);
        ProjectTag offroad = tagRepository.save(new ProjectTag("Оффроуд"));
        offroad.setDescription("Внедорожная подготовка, лифт подвески, лебёдки, грязевые шины — для бездорожья любой сложности");
        tagRepository.save(offroad);
        ProjectTag drag = tagRepository.save(new ProjectTag("Драг"));
        drag.setDescription("Готовые драг-кары, турбонаддув, NOS, усиленные трансмиссии — всё для быстрых заездов");
        tagRepository.save(drag);

        saveMod(drift, "Гидроручник");
        saveMod(drift, "Выворот");
        saveMod(drift, "Блокировка дифференциала");
        saveMod(drift, "Койловеры");
        saveMod(drift, "Варка дифференциала");

        saveMod(stance, "Пневмоподвеска");
        saveMod(stance, "Развал/схождение");
        saveMod(stance, "Растяжка шин");
        saveMod(stance, "Широкий кузов");
        saveMod(stance, "Литые диски R19+");

        saveMod(offroad, "Лебедка");
        saveMod(offroad, "Грязевые шины MT");
        saveMod(offroad, "Шноркель");
        saveMod(offroad, "Лифт подвески");
        saveMod(offroad, "Силовой бампер");

        saveMod(drag, "Турбонаддув");
        saveMod(drag, "Нитрометан (NOS)");
        saveMod(drag, "Усиленная трансмиссия");
        saveMod(drag, "Радиальные слики");
        saveMod(drag, "Каркас безопасности");

        return List.of(drift, stance, offroad, drag);
    }

    private void saveMod(ProjectTag tag, String name) {
        ModsCategory m = new ModsCategory();
        m.setProjectTag(tag);
        m.setName(name);
        modsRepository.save(m);
    }

    private Map<String, Map<String, Map<String, CarGeneration>>> createBrands() {
        Map<String, Map<String, Map<String, CarGeneration>>> result = new LinkedHashMap<>();
        Map<String, CarModel> modelByName = new HashMap<>();

        // --- LADA (VAZ) ---
        CarBrand lada = brandRepository.save(new CarBrand("LADA", "Россия"));
        addModel(lada, "Granta", 2011, null, result, modelByName);
        addModel(lada, "Vesta", 2015, null, result, modelByName);
        addModel(lada, "Largus", 2012, null, result, modelByName);
        addModel(lada, "Niva Legend", 1977, null, result, modelByName);
        addModel(lada, "Niva Travel", 2002, null, result, modelByName);
        addModel(lada, "XRAY", 2015, 2022, result, modelByName);

        // --- УАЗ ---
        CarBrand uaz = brandRepository.save(new CarBrand("УАЗ", "Россия"));
        addModel(uaz, "Патриот", 2005, null, result, modelByName);
        addModel(uaz, "Хантер", 2003, null, result, modelByName);
        addModel(uaz, "Буханка", 1965, null, result, modelByName);
        addModel(uaz, "Профи", 2017, null, result, modelByName);

        // --- ГАЗ ---
        CarBrand gaz = brandRepository.save(new CarBrand("ГАЗ", "Россия"));
        addModel(gaz, "Волга 3110", 1997, 2005, result, modelByName);
        addModel(gaz, "Волга 31105", 2004, 2009, result, modelByName);
        addModel(gaz, "Газель Бизнес", 2010, null, result, modelByName);
        addModel(gaz, "Газель NEXT", 2013, null, result, modelByName);

        // --- Toyota ---
        CarBrand toyota = brandRepository.save(new CarBrand("Toyota", "Япония"));
        addGen(toyota, "Camry", "V40", 2001, 2006, result, modelByName);
        addGen(toyota, "Camry", "V50", 2006, 2011, result, modelByName);
        addGen(toyota, "Camry", "V55", 2011, 2018, result, modelByName);
        addGen(toyota, "Camry", "V70", 2018, null, result, modelByName);
        addGen(toyota, "Corolla", "E140", 2007, 2013, result, modelByName);
        addGen(toyota, "Corolla", "E160", 2013, 2018, result, modelByName);
        addGen(toyota, "Corolla", "E210", 2018, null, result, modelByName);
        addGen(toyota, "Land Cruiser 200", "200", 2007, 2021, result, modelByName);
        addGen(toyota, "Land Cruiser 300", "300", 2021, null, result, modelByName);
        addGen(toyota, "Land Cruiser Prado", "120", 2002, 2009, result, modelByName);
        addGen(toyota, "Land Cruiser Prado", "150", 2009, null, result, modelByName);
        addGen(toyota, "RAV4", "XA30", 2005, 2012, result, modelByName);
        addGen(toyota, "RAV4", "XA40", 2012, 2018, result, modelByName);
        addGen(toyota, "RAV4", "XA50", 2018, null, result, modelByName);
        addModel(toyota, "Supra", 1993, 2002, result, modelByName);
        addModel(toyota, "Mark II", 1992, 2000, result, modelByName);
        addModel(toyota, "Chaser", 1992, 2001, result, modelByName);
        addModel(toyota, "Crown", 1995, null, result, modelByName);
        addModel(toyota, "Hilux", 1997, null, result, modelByName);

        // --- Lexus ---
        CarBrand lexus = brandRepository.save(new CarBrand("Lexus", "Япония"));
        addGen(lexus, "RX", "XU30", 2003, 2009, result, modelByName);
        addGen(lexus, "RX", "XU40", 2009, 2015, result, modelByName);
        addGen(lexus, "RX", "XU50", 2015, 2022, result, modelByName);
        addGen(lexus, "RX", "XU60", 2022, null, result, modelByName);
        addModel(lexus, "LS", 1989, null, result, modelByName);
        addModel(lexus, "LX", 1995, null, result, modelByName);
        addModel(lexus, "IS", 1998, null, result, modelByName);
        addModel(lexus, "GS", 1993, 2020, result, modelByName);
        addModel(lexus, "NX", 2014, null, result, modelByName);

        // --- Nissan ---
        CarBrand nissan = brandRepository.save(new CarBrand("Nissan", "Япония"));
        addGen(nissan, "GT-R", "R35", 2007, null, result, modelByName);
        addGen(nissan, "Skyline", "R34", 1999, 2002, result, modelByName);
        addModel(nissan, "Silvia", 1993, 2002, result, modelByName);
        addGen(nissan, "X-Trail", "T31", 2007, 2014, result, modelByName);
        addGen(nissan, "X-Trail", "T32", 2014, 2021, result, modelByName);
        addGen(nissan, "Qashqai", "J10", 2007, 2014, result, modelByName);
        addGen(nissan, "Qashqai", "J11", 2014, 2021, result, modelByName);
        addGen(nissan, "Qashqai", "J12", 2021, null, result, modelByName);
        addModel(nissan, "Almera", 1995, 2013, result, modelByName);
        addModel(nissan, "Patrol", 1987, null, result, modelByName);
        addModel(nissan, "350Z", 2002, 2009, result, modelByName);
        addModel(nissan, "370Z", 2009, 2020, result, modelByName);

        // --- Mazda ---
        CarBrand mazda = brandRepository.save(new CarBrand("Mazda", "Япония"));
        addGen(mazda, "Mazda3", "BK", 2003, 2009, result, modelByName);
        addGen(mazda, "Mazda3", "BL", 2009, 2013, result, modelByName);
        addGen(mazda, "Mazda3", "BM", 2013, 2019, result, modelByName);
        addGen(mazda, "Mazda3", "BP", 2019, null, result, modelByName);
        addGen(mazda, "Mazda6", "GG", 2002, 2007, result, modelByName);
        addGen(mazda, "Mazda6", "GH", 2007, 2012, result, modelByName);
        addGen(mazda, "Mazda6", "GJ", 2012, null, result, modelByName);
        addGen(mazda, "CX-5", "KF", 2012, null, result, modelByName);
        addModel(mazda, "RX-7", 1978, 2002, result, modelByName);
        addModel(mazda, "MX-5 Miata", 1989, null, result, modelByName);

        // --- Subaru ---
        CarBrand subaru = brandRepository.save(new CarBrand("Subaru", "Япония"));
        addGen(subaru, "Impreza WRX/STI", "GD", 2000, 2007, result, modelByName);
        addGen(subaru, "Impreza WRX/STI", "GR", 2008, 2014, result, modelByName);
        addGen(subaru, "Impreza WRX/STI", "VA", 2014, 2021, result, modelByName);
        addGen(subaru, "Forester", "SG", 2002, 2008, result, modelByName);
        addGen(subaru, "Forester", "SH", 2008, 2013, result, modelByName);
        addGen(subaru, "Forester", "SJ", 2013, 2018, result, modelByName);
        addGen(subaru, "Forester", "SK", 2018, null, result, modelByName);
        addGen(subaru, "Outback", "BP", 2004, 2009, result, modelByName);
        addGen(subaru, "Outback", "BR", 2009, 2014, result, modelByName);
        addGen(subaru, "Outback", "BS", 2014, null, result, modelByName);
        addModel(subaru, "Legacy", 1989, null, result, modelByName);
        addModel(subaru, "BRZ", 2012, null, result, modelByName);

        // --- Mitsubishi ---
        CarBrand mitsubishi = brandRepository.save(new CarBrand("Mitsubishi", "Япония"));
        addGen(mitsubishi, "Lancer Evolution", "IX (CT9A)", 2005, 2007, result, modelByName);
        addGen(mitsubishi, "Lancer Evolution", "X (CZ4A)", 2007, 2016, result, modelByName);
        addGen(mitsubishi, "Lancer", "CS", 2000, 2007, result, modelByName);
        addGen(mitsubishi, "Lancer", "CY", 2007, 2017, result, modelByName);
        addGen(mitsubishi, "Outlander", "XL", 2005, 2012, result, modelByName);
        addGen(mitsubishi, "Outlander", "III", 2012, null, result, modelByName);
        addGen(mitsubishi, "Pajero", "IV", 2006, 2021, result, modelByName);
        addModel(mitsubishi, "Eclipse", 1995, 2012, result, modelByName);
        addModel(mitsubishi, "3000GT", 1990, 2001, result, modelByName);
        addModel(mitsubishi, "Delica", 1986, null, result, modelByName);

        // --- Honda ---
        CarBrand honda = brandRepository.save(new CarBrand("Honda", "Япония"));
        addGen(honda, "Accord", "CL7/9", 2003, 2008, result, modelByName);
        addGen(honda, "Accord", "CP", 2008, 2012, result, modelByName);
        addGen(honda, "Accord", "CR", 2012, null, result, modelByName);
        addGen(honda, "Civic", "EK", 1995, 2001, result, modelByName);
        addGen(honda, "Civic", "EP", 2001, 2006, result, modelByName);
        addGen(honda, "Civic", "FD", 2006, 2011, result, modelByName);
        addGen(honda, "Civic", "FB", 2011, 2017, result, modelByName);
        addGen(honda, "Civic", "FC", 2017, null, result, modelByName);
        addModel(honda, "CR-V", 1995, null, result, modelByName);
        addModel(honda, "Prelude", 1996, 2001, result, modelByName);
        addModel(honda, "S2000", 1999, 2009, result, modelByName);
        addModel(honda, "NSX", 1990, 2005, result, modelByName);

        // --- Suzuki ---
        CarBrand suzuki = brandRepository.save(new CarBrand("Suzuki", "Япония"));
        addModel(suzuki, "Swift", 2000, null, result, modelByName);
        addModel(suzuki, "Jimny", 1998, null, result, modelByName);
        addModel(suzuki, "Grand Vitara", 1998, null, result, modelByName);
        addModel(suzuki, "SX4", 2006, null, result, modelByName);
        addModel(suzuki, "Ignis", 2000, null, result, modelByName);

        // --- BMW ---
        CarBrand bmw = brandRepository.save(new CarBrand("BMW", "Германия"));
        addGen(bmw, "3 Series", "E36", 1990, 2000, result, modelByName);
        addGen(bmw, "3 Series", "E46", 1998, 2006, result, modelByName);
        addGen(bmw, "3 Series", "E90/92/93", 2005, 2013, result, modelByName);
        addGen(bmw, "3 Series", "F30", 2011, 2019, result, modelByName);
        addGen(bmw, "3 Series", "G20", 2018, null, result, modelByName);
        addGen(bmw, "5 Series", "E39", 1995, 2004, result, modelByName);
        addGen(bmw, "5 Series", "E60", 2003, 2010, result, modelByName);
        addGen(bmw, "5 Series", "F10", 2010, 2017, result, modelByName);
        addGen(bmw, "5 Series", "G30", 2017, null, result, modelByName);
        addGen(bmw, "7 Series", "E38", 1994, 2001, result, modelByName);
        addGen(bmw, "7 Series", "E65/66", 2001, 2008, result, modelByName);
        addGen(bmw, "7 Series", "F01/02", 2008, 2015, result, modelByName);
        addGen(bmw, "7 Series", "G11/12", 2015, 2022, result, modelByName);
        addGen(bmw, "X3", "E83", 2003, 2010, result, modelByName);
        addGen(bmw, "X3", "F25", 2010, 2017, result, modelByName);
        addGen(bmw, "X3", "G01", 2017, null, result, modelByName);
        addGen(bmw, "X5", "E53", 1999, 2006, result, modelByName);
        addGen(bmw, "X5", "E70", 2006, 2013, result, modelByName);
        addGen(bmw, "X5", "F15", 2013, 2018, result, modelByName);
        addGen(bmw, "X5", "G05", 2018, null, result, modelByName);
        addGen(bmw, "M3", "E36", 1992, 1999, result, modelByName);
        addGen(bmw, "M3", "E46", 2000, 2006, result, modelByName);
        addGen(bmw, "M3", "E90/92", 2007, 2013, result, modelByName);
        addGen(bmw, "M3", "F80", 2014, 2019, result, modelByName);
        addGen(bmw, "M3", "G80", 2020, null, result, modelByName);
        addModel(bmw, "1 Series", 2004, null, result, modelByName);
        addModel(bmw, "Z4", 2002, null, result, modelByName);

        // --- Mercedes-Benz ---
        CarBrand mercedes = brandRepository.save(new CarBrand("Mercedes-Benz", "Германия"));
        addGen(mercedes, "C-Class", "W202", 1993, 2001, result, modelByName);
        addGen(mercedes, "C-Class", "W203", 2000, 2007, result, modelByName);
        addGen(mercedes, "C-Class", "W204", 2007, 2014, result, modelByName);
        addGen(mercedes, "C-Class", "W205", 2014, 2021, result, modelByName);
        addGen(mercedes, "C-Class", "W206", 2021, null, result, modelByName);
        addGen(mercedes, "E-Class", "W210", 1995, 2002, result, modelByName);
        addGen(mercedes, "E-Class", "W211", 2002, 2009, result, modelByName);
        addGen(mercedes, "E-Class", "W212", 2009, 2016, result, modelByName);
        addGen(mercedes, "E-Class", "W213", 2016, null, result, modelByName);
        addGen(mercedes, "S-Class", "W220", 1998, 2005, result, modelByName);
        addGen(mercedes, "S-Class", "W221", 2005, 2013, result, modelByName);
        addGen(mercedes, "S-Class", "W222", 2013, 2020, result, modelByName);
        addGen(mercedes, "S-Class", "W223", 2020, null, result, modelByName);
        addGen(mercedes, "G-Class", "W463", 1990, 2018, result, modelByName);
        addGen(mercedes, "G-Class", "W463A", 2018, null, result, modelByName);
        addGen(mercedes, "ML/GLE", "W164", 2005, 2011, result, modelByName);
        addGen(mercedes, "ML/GLE", "W166", 2011, 2018, result, modelByName);
        addGen(mercedes, "GLE", "W167", 2018, null, result, modelByName);
        addModel(mercedes, "A-Class", 1997, null, result, modelByName);
        addModel(mercedes, "GLK/GLC", 2008, null, result, modelByName);
        addModel(mercedes, "AMG GT", 2014, null, result, modelByName);
        addModel(mercedes, "CLK", 1996, 2010, result, modelByName);
        addModel(mercedes, "SL", 1954, null, result, modelByName);

        // --- Audi ---
        CarBrand audi = brandRepository.save(new CarBrand("Audi", "Германия"));
        addGen(audi, "A4", "B5", 1994, 2001, result, modelByName);
        addGen(audi, "A4", "B6", 2000, 2006, result, modelByName);
        addGen(audi, "A4", "B7", 2005, 2009, result, modelByName);
        addGen(audi, "A4", "B8", 2008, 2015, result, modelByName);
        addGen(audi, "A4", "B9", 2015, null, result, modelByName);
        addGen(audi, "A6", "C5", 1997, 2004, result, modelByName);
        addGen(audi, "A6", "C6", 2004, 2011, result, modelByName);
        addGen(audi, "A6", "C7", 2011, 2018, result, modelByName);
        addGen(audi, "A6", "C8", 2018, null, result, modelByName);
        addGen(audi, "Q5", "8R", 2008, 2017, result, modelByName);
        addGen(audi, "Q5", "FY", 2017, null, result, modelByName);
        addGen(audi, "Q7", "4L", 2005, 2015, result, modelByName);
        addGen(audi, "Q7", "4M", 2015, null, result, modelByName);
        addGen(audi, "RS6", "C5", 2002, 2004, result, modelByName);
        addGen(audi, "RS6", "C6", 2008, 2010, result, modelByName);
        addGen(audi, "RS6", "C7", 2013, 2018, result, modelByName);
        addGen(audi, "RS6", "C8", 2019, null, result, modelByName);
        addModel(audi, "A3", 1996, null, result, modelByName);
        addModel(audi, "A8", 1994, null, result, modelByName);
        addModel(audi, "R8", 2006, null, result, modelByName);
        addModel(audi, "TT", 1998, null, result, modelByName);

        // --- Volkswagen ---
        CarBrand vw = brandRepository.save(new CarBrand("Volkswagen", "Германия"));
        addGen(vw, "Golf", "MK4", 1997, 2003, result, modelByName);
        addGen(vw, "Golf", "MK5", 2003, 2009, result, modelByName);
        addGen(vw, "Golf", "MK6", 2008, 2012, result, modelByName);
        addGen(vw, "Golf", "MK7", 2012, 2020, result, modelByName);
        addGen(vw, "Golf", "MK8", 2020, null, result, modelByName);
        addGen(vw, "Passat", "B5", 1996, 2005, result, modelByName);
        addGen(vw, "Passat", "B6", 2005, 2010, result, modelByName);
        addGen(vw, "Passat", "B7", 2010, 2014, result, modelByName);
        addGen(vw, "Passat", "B8", 2014, null, result, modelByName);
        addGen(vw, "Tiguan", "I", 2007, 2016, result, modelByName);
        addGen(vw, "Tiguan", "II", 2016, null, result, modelByName);
        addGen(vw, "Polo", "IV", 2002, 2009, result, modelByName);
        addGen(vw, "Polo", "V", 2009, 2017, result, modelByName);
        addGen(vw, "Polo", "VI", 2017, null, result, modelByName);
        addModel(vw, "Jetta", 1979, null, result, modelByName);
        addModel(vw, "Touareg", 2002, null, result, modelByName);
        addModel(vw, "Multivan", 1990, null, result, modelByName);

        // --- Porsche ---
        CarBrand porsche = brandRepository.save(new CarBrand("Porsche", "Германия"));
        addGen(porsche, "911", "996", 1997, 2005, result, modelByName);
        addGen(porsche, "911", "997", 2004, 2012, result, modelByName);
        addGen(porsche, "911", "991", 2011, 2019, result, modelByName);
        addGen(porsche, "911", "992", 2019, null, result, modelByName);
        addGen(porsche, "Cayenne", "955", 2002, 2010, result, modelByName);
        addGen(porsche, "Cayenne", "958", 2010, 2017, result, modelByName);
        addGen(porsche, "Cayenne", "PO536", 2017, null, result, modelByName);
        addModel(porsche, "Panamera", 2009, null, result, modelByName);
        addModel(porsche, "Boxster", 1996, null, result, modelByName);
        addModel(porsche, "Macan", 2013, null, result, modelByName);
        addModel(porsche, "Cayman", 2005, null, result, modelByName);

        // --- Opel ---
        CarBrand opel = brandRepository.save(new CarBrand("Opel", "Германия"));
        addModel(opel, "Astra", 1991, null, result, modelByName);
        addModel(opel, "Corsa", 1982, null, result, modelByName);
        addModel(opel, "Insignia", 2008, null, result, modelByName);
        addModel(opel, "Mokka", 2012, null, result, modelByName);
        addModel(opel, "Zafira", 1999, 2019, result, modelByName);
        addModel(opel, "Vectra", 1988, 2008, result, modelByName);

        // --- Ford ---
        CarBrand ford = brandRepository.save(new CarBrand("Ford", "США"));
        addGen(ford, "Focus", "I", 1998, 2004, result, modelByName);
        addGen(ford, "Focus", "II", 2004, 2011, result, modelByName);
        addGen(ford, "Focus", "III", 2010, 2018, result, modelByName);
        addGen(ford, "Focus", "IV", 2018, null, result, modelByName);
        addGen(ford, "Mondeo", "MK3", 2000, 2007, result, modelByName);
        addGen(ford, "Mondeo", "MK4", 2007, 2014, result, modelByName);
        addGen(ford, "Mondeo", "MK5", 2014, null, result, modelByName);
        addGen(ford, "Mustang", "S197", 2005, 2014, result, modelByName);
        addGen(ford, "Mustang", "S550", 2014, null, result, modelByName);
        addModel(ford, "F-150", 1948, null, result, modelByName);
        addModel(ford, "Explorer", 1990, null, result, modelByName);
        addModel(ford, "Kuga", 2008, null, result, modelByName);
        addModel(ford, "Transit", 1965, null, result, modelByName);

        // --- Chevrolet ---
        CarBrand chevrolet = brandRepository.save(new CarBrand("Chevrolet", "США"));
        addGen(chevrolet, "Corvette", "C5", 1997, 2004, result, modelByName);
        addGen(chevrolet, "Corvette", "C6", 2005, 2013, result, modelByName);
        addGen(chevrolet, "Corvette", "C7", 2014, 2019, result, modelByName);
        addGen(chevrolet, "Corvette", "C8", 2020, null, result, modelByName);
        addGen(chevrolet, "Camaro", "Z28", 1993, 2002, result, modelByName);
        addGen(chevrolet, "Camaro", "Z30", 2010, 2015, result, modelByName);
        addGen(chevrolet, "Camaro", "Z35", 2016, null, result, modelByName);
        addModel(chevrolet, "Lacetti", 2004, 2013, result, modelByName);
        addModel(chevrolet, "Aveo", 2002, 2015, result, modelByName);
        addModel(chevrolet, "Cruze", 2008, 2019, result, modelByName);
        addModel(chevrolet, "Tahoe", 1994, null, result, modelByName);
        addModel(chevrolet, "Suburban", 1935, null, result, modelByName);
        addModel(chevrolet, "Spark", 1998, null, result, modelByName);

        // --- Dodge ---
        CarBrand dodge = brandRepository.save(new CarBrand("Dodge", "США"));
        addGen(dodge, "Challenger", "LC", 2008, 2023, result, modelByName);
        addGen(dodge, "Charger", "LX", 2006, 2023, result, modelByName);
        addModel(dodge, "Viper", 1992, 2017, result, modelByName);
        addModel(dodge, "Durango", 1997, null, result, modelByName);
        addModel(dodge, "Neon", 1994, 2005, result, modelByName);

        // --- Jeep ---
        CarBrand jeep = brandRepository.save(new CarBrand("Jeep", "США"));
        addGen(jeep, "Wrangler", "TJ", 1997, 2006, result, modelByName);
        addGen(jeep, "Wrangler", "JK", 2007, 2018, result, modelByName);
        addGen(jeep, "Wrangler", "JL", 2018, null, result, modelByName);
        addGen(jeep, "Grand Cherokee", "WK", 2005, 2011, result, modelByName);
        addGen(jeep, "Grand Cherokee", "WK2", 2011, 2022, result, modelByName);
        addModel(jeep, "Cherokee", 1974, null, result, modelByName);
        addModel(jeep, "Compass", 2006, null, result, modelByName);

        // --- Cadillac ---
        CarBrand cadillac = brandRepository.save(new CarBrand("Cadillac", "США"));
        addModel(cadillac, "Escalade", 1999, null, result, modelByName);
        addModel(cadillac, "CTS", 2003, 2019, result, modelByName);
        addModel(cadillac, "CT5", 2020, null, result, modelByName);
        addModel(cadillac, "XT5", 2017, null, result, modelByName);
        addModel(cadillac, "SRX", 2004, 2016, result, modelByName);

        // --- Tesla ---
        CarBrand tesla = brandRepository.save(new CarBrand("Tesla", "США"));
        addGen(tesla, "Model 3", "I", 2017, 2023, result, modelByName);
        addGen(tesla, "Model 3", "Highland", 2023, null, result, modelByName);
        addModel(tesla, "Model S", 2012, null, result, modelByName);
        addModel(tesla, "Model X", 2015, null, result, modelByName);
        addModel(tesla, "Model Y", 2020, null, result, modelByName);
        addModel(tesla, "Cybertruck", 2023, null, result, modelByName);

        // --- Hyundai ---
        CarBrand hyundai = brandRepository.save(new CarBrand("Hyundai", "Корея"));
        addGen(hyundai, "Solaris", "I", 2010, 2017, result, modelByName);
        addGen(hyundai, "Solaris", "II", 2017, null, result, modelByName);
        addGen(hyundai, "Elantra", "HD", 2006, 2010, result, modelByName);
        addGen(hyundai, "Elantra", "MD", 2010, 2015, result, modelByName);
        addGen(hyundai, "Elantra", "AD", 2015, 2020, result, modelByName);
        addGen(hyundai, "Elantra", "CN7", 2020, null, result, modelByName);
        addGen(hyundai, "Tucson", "JM", 2004, 2009, result, modelByName);
        addGen(hyundai, "Tucson", "LM", 2009, 2015, result, modelByName);
        addGen(hyundai, "Tucson", "TL", 2015, 2020, result, modelByName);
        addGen(hyundai, "Tucson", "NX4", 2020, null, result, modelByName);
        addGen(hyundai, "Santa Fe", "SM", 2000, 2006, result, modelByName);
        addGen(hyundai, "Santa Fe", "CM", 2006, 2012, result, modelByName);
        addGen(hyundai, "Santa Fe", "DM", 2012, 2018, result, modelByName);
        addGen(hyundai, "Santa Fe", "TM", 2018, null, result, modelByName);
        addModel(hyundai, "Creta", 2014, null, result, modelByName);
        addModel(hyundai, "Genesis (Coupe)", 2008, 2016, result, modelByName);
        addModel(hyundai, "Sonata", 1985, null, result, modelByName);
        addModel(hyundai, "Palisade", 2018, null, result, modelByName);
        addModel(hyundai, "i30", 2007, null, result, modelByName);

        // --- Kia ---
        CarBrand kia = brandRepository.save(new CarBrand("Kia", "Корея"));
        addGen(kia, "Rio", "JB", 2005, 2011, result, modelByName);
        addGen(kia, "Rio", "QB", 2011, 2017, result, modelByName);
        addGen(kia, "Rio", "FB", 2017, null, result, modelByName);
        addGen(kia, "Sportage", "KM", 2004, 2010, result, modelByName);
        addGen(kia, "Sportage", "SL", 2010, 2015, result, modelByName);
        addGen(kia, "Sportage", "QL", 2015, 2021, result, modelByName);
        addGen(kia, "Sportage", "NQ5", 2021, null, result, modelByName);
        addGen(kia, "Optima", "MG", 2005, 2010, result, modelByName);
        addGen(kia, "Optima", "TF", 2010, 2015, result, modelByName);
        addGen(kia, "Optima", "JF", 2015, 2019, result, modelByName);
        addGen(kia, "K5", "DL3", 2019, null, result, modelByName);
        addGen(kia, "Sorento", "BL", 2002, 2009, result, modelByName);
        addGen(kia, "Sorento", "XM", 2009, 2014, result, modelByName);
        addGen(kia, "Sorento", "UM", 2014, 2020, result, modelByName);
        addGen(kia, "Sorento", "MQ4", 2020, null, result, modelByName);
        addModel(kia, "Soul", 2008, null, result, modelByName);
        addModel(kia, "Ceed", 2006, null, result, modelByName);
        addModel(kia, "Stinger", 2017, null, result, modelByName);
        addModel(kia, "Mohave", 2008, null, result, modelByName);

        // --- Genesis ---
        CarBrand genesis = brandRepository.save(new CarBrand("Genesis", "Корея"));
        addModel(genesis, "G70", 2017, null, result, modelByName);
        addModel(genesis, "G80", 2016, null, result, modelByName);
        addModel(genesis, "G90", 2015, null, result, modelByName);
        addModel(genesis, "GV70", 2020, null, result, modelByName);
        addModel(genesis, "GV80", 2020, null, result, modelByName);

        // --- Renault ---
        CarBrand renault = brandRepository.save(new CarBrand("Renault", "Франция"));
        addGen(renault, "Logan", "I", 2004, 2012, result, modelByName);
        addGen(renault, "Logan", "II", 2012, 2020, result, modelByName);
        addGen(renault, "Duster", "I", 2010, 2018, result, modelByName);
        addGen(renault, "Duster", "II", 2018, 2021, result, modelByName);
        addGen(renault, "Kaptur", "I", 2016, null, result, modelByName);
        addModel(renault, "Sandero", 2007, null, result, modelByName);
        addModel(renault, "Megane", 1995, 2016, result, modelByName);
        addModel(renault, "Arkana", 2019, null, result, modelByName);
        addModel(renault, "Koleos", 2008, null, result, modelByName);

        // --- Peugeot ---
        CarBrand peugeot = brandRepository.save(new CarBrand("Peugeot", "Франция"));
        addModel(peugeot, "206", 1998, 2012, result, modelByName);
        addModel(peugeot, "207", 2006, 2014, result, modelByName);
        addModel(peugeot, "208", 2012, null, result, modelByName);
        addModel(peugeot, "308", 2007, null, result, modelByName);
        addModel(peugeot, "408", 2010, null, result, modelByName);
        addModel(peugeot, "3008", 2009, null, result, modelByName);
        addModel(peugeot, "5008", 2017, null, result, modelByName);
        addModel(peugeot, "Partner", 1996, null, result, modelByName);
        addModel(peugeot, "Boxer", 1994, null, result, modelByName);

        // --- Citroen ---
        CarBrand citroen = brandRepository.save(new CarBrand("Citroen", "Франция"));
        addModel(citroen, "C3", 2002, null, result, modelByName);
        addModel(citroen, "C4", 2004, null, result, modelByName);
        addModel(citroen, "C5", 2001, null, result, modelByName);
        addModel(citroen, "Berlingo", 1996, null, result, modelByName);
        addModel(citroen, "DS3/DS4/DS5", 2010, null, result, modelByName);
        addModel(citroen, "Jumper", 1994, null, result, modelByName);
        addModel(citroen, "C-Elysee", 2012, null, result, modelByName);

        // --- Fiat ---
        CarBrand fiat = brandRepository.save(new CarBrand("Fiat", "Италия"));
        addModel(fiat, "500", 2007, null, result, modelByName);
        addModel(fiat, "Punto", 1993, 2018, result, modelByName);
        addModel(fiat, "Doblo", 2000, null, result, modelByName);
        addModel(fiat, "Ducato", 1981, null, result, modelByName);
        addModel(fiat, "Albea", 2002, 2012, result, modelByName);

        // --- Alfa Romeo ---
        CarBrand alfa = brandRepository.save(new CarBrand("Alfa Romeo", "Италия"));
        addModel(alfa, "Giulia", 2016, null, result, modelByName);
        addModel(alfa, "Giulietta", 2010, 2020, result, modelByName);
        addModel(alfa, "Stelvio", 2017, null, result, modelByName);
        addModel(alfa, "156", 1997, 2007, result, modelByName);
        addModel(alfa, "159", 2005, 2011, result, modelByName);
        addModel(alfa, "Brera", 2005, 2010, result, modelByName);
        addModel(alfa, "4C", 2013, 2020, result, modelByName);

        // --- Ferrari ---
        CarBrand ferrari = brandRepository.save(new CarBrand("Ferrari", "Италия"));
        addModel(ferrari, "F430", 2004, 2009, result, modelByName);
        addModel(ferrari, "458 Italia", 2009, 2015, result, modelByName);
        addModel(ferrari, "488 GTB", 2015, 2019, result, modelByName);
        addModel(ferrari, "F8 Tributo", 2019, null, result, modelByName);
        addModel(ferrari, "SF90 Stradale", 2019, null, result, modelByName);
        addModel(ferrari, "LaFerrari", 2013, 2018, result, modelByName);
        addModel(ferrari, "F12berlinetta", 2012, 2017, result, modelByName);
        addModel(ferrari, "812 Superfast", 2017, null, result, modelByName);
        addModel(ferrari, "Portofino", 2017, null, result, modelByName);
        addModel(ferrari, "Roma", 2020, null, result, modelByName);
        addModel(ferrari, "California", 2008, 2017, result, modelByName);
        addModel(ferrari, "Enzo", 2002, 2004, result, modelByName);

        // --- Lamborghini ---
        CarBrand lamborghini = brandRepository.save(new CarBrand("Lamborghini", "Италия"));
        addModel(lamborghini, "Aventador", 2011, 2021, result, modelByName);
        addModel(lamborghini, "Huracán", 2014, null, result, modelByName);
        addModel(lamborghini, "Urus", 2018, null, result, modelByName);
        addModel(lamborghini, "Gallardo", 2003, 2013, result, modelByName);
        addModel(lamborghini, "Murciélago", 2001, 2010, result, modelByName);
        addModel(lamborghini, "Diablo", 1990, 2001, result, modelByName);
        addModel(lamborghini, "Countach", 1974, 1990, result, modelByName);
        addModel(lamborghini, "Revuelto", 2023, null, result, modelByName);

        // --- Maserati ---
        CarBrand maserati = brandRepository.save(new CarBrand("Maserati", "Италия"));
        addModel(maserati, "GranTurismo", 2007, 2019, result, modelByName);
        addModel(maserati, "Ghibli", 2013, null, result, modelByName);
        addModel(maserati, "Quattroporte", 1963, null, result, modelByName);
        addModel(maserati, "Levante", 2016, null, result, modelByName);
        addModel(maserati, "MC20", 2021, null, result, modelByName);

        // --- Jaguar ---
        CarBrand jaguar = brandRepository.save(new CarBrand("Jaguar", "Великобритания"));
        addGen(jaguar, "F-Type", "I", 2013, null, result, modelByName);
        addModel(jaguar, "XE", 2015, null, result, modelByName);
        addModel(jaguar, "XF", 2008, null, result, modelByName);
        addModel(jaguar, "XJ", 1968, 2019, result, modelByName);
        addModel(jaguar, "F-Pace", 2016, null, result, modelByName);
        addModel(jaguar, "E-Pace", 2018, null, result, modelByName);
        addModel(jaguar, "I-Pace", 2018, null, result, modelByName);
        addModel(jaguar, "E-Type", 1961, 1975, result, modelByName);

        // --- Land Rover ---
        CarBrand landrover = brandRepository.save(new CarBrand("Land Rover", "Великобритания"));
        addGen(landrover, "Range Rover", "L322", 2002, 2012, result, modelByName);
        addGen(landrover, "Range Rover", "L405", 2012, 2021, result, modelByName);
        addGen(landrover, "Range Rover", "L460", 2021, null, result, modelByName);
        addGen(landrover, "Range Rover Sport", "L320", 2005, 2013, result, modelByName);
        addGen(landrover, "Range Rover Sport", "L494", 2013, null, result, modelByName);
        addGen(landrover, "Discovery", "L319", 2004, 2016, result, modelByName);
        addGen(landrover, "Discovery", "L462", 2016, null, result, modelByName);
        addGen(landrover, "Discovery Sport", "L550", 2014, null, result, modelByName);
        addGen(landrover, "Evoque", "L538", 2011, 2018, result, modelByName);
        addGen(landrover, "Evoque", "L551", 2018, null, result, modelByName);
        addModel(landrover, "Defender", 2020, null, result, modelByName);

        // --- Aston Martin ---
        CarBrand aston = brandRepository.save(new CarBrand("Aston Martin", "Великобритания"));
        addModel(aston, "DB9", 2004, 2016, result, modelByName);
        addModel(aston, "DB11", 2016, null, result, modelByName);
        addModel(aston, "DBS", 2007, 2012, result, modelByName);
        addModel(aston, "DBS Superleggera", 2018, null, result, modelByName);
        addModel(aston, "Vantage", 2005, null, result, modelByName);
        addModel(aston, "Vanquish", 2012, 2018, result, modelByName);
        addModel(aston, "DBX", 2020, null, result, modelByName);
        addModel(aston, "Valkyrie", 2021, null, result, modelByName);

        // --- Bentley ---
        CarBrand bentley = brandRepository.save(new CarBrand("Bentley", "Великобритания"));
        addModel(bentley, "Continental GT", 2003, null, result, modelByName);
        addModel(bentley, "Flying Spur", 2005, null, result, modelByName);
        addModel(bentley, "Bentayga", 2016, null, result, modelByName);
        addModel(bentley, "Mulsanne", 2010, 2020, result, modelByName);

        // --- Rolls-Royce ---
        CarBrand rr = brandRepository.save(new CarBrand("Rolls-Royce", "Великобритания"));
        addModel(rr, "Phantom", 2003, null, result, modelByName);
        addModel(rr, "Ghost", 2009, null, result, modelByName);
        addModel(rr, "Wraith", 2013, null, result, modelByName);
        addModel(rr, "Dawn", 2015, null, result, modelByName);
        addModel(rr, "Cullinan", 2018, null, result, modelByName);
        addModel(rr, "Spectre", 2023, null, result, modelByName);

        // --- Mini ---
        CarBrand mini = brandRepository.save(new CarBrand("MINI", "Великобритания"));
        addGen(mini, "Cooper", "R50/53", 2001, 2006, result, modelByName);
        addGen(mini, "Cooper", "R56", 2006, 2013, result, modelByName);
        addGen(mini, "Cooper", "F56", 2013, null, result, modelByName);
        addModel(mini, "Countryman", 2010, null, result, modelByName);
        addModel(mini, "Clubman", 2007, null, result, modelByName);

        // --- Volvo ---
        CarBrand volvo = brandRepository.save(new CarBrand("Volvo", "Швеция"));
        addGen(volvo, "XC90", "R", 2002, 2014, result, modelByName);
        addGen(volvo, "XC90", "SPA", 2015, null, result, modelByName);
        addGen(volvo, "S40", "M", 2004, 2012, result, modelByName);
        addGen(volvo, "S60", "P3", 2010, 2018, result, modelByName);
        addGen(volvo, "S60", "SPA", 2018, null, result, modelByName);
        addGen(volvo, "XC60", "Y413", 2008, 2017, result, modelByName);
        addGen(volvo, "XC60", "SPA", 2017, null, result, modelByName);
        addModel(volvo, "S90", 2016, null, result, modelByName);
        addModel(volvo, "V60", 2010, null, result, modelByName);
        addModel(volvo, "850", 1991, 1997, result, modelByName);

        // --- Skoda ---
        CarBrand skoda = brandRepository.save(new CarBrand("Skoda", "Чехия"));
        addGen(skoda, "Octavia", "A5", 2004, 2013, result, modelByName);
        addGen(skoda, "Octavia", "A7", 2013, 2019, result, modelByName);
        addGen(skoda, "Octavia", "A8", 2019, null, result, modelByName);
        addGen(skoda, "Rapid", "NH", 2012, 2019, result, modelByName);
        addGen(skoda, "Kodiaq", "II", 2016, null, result, modelByName);
        addGen(skoda, "Karoq", "NU", 2017, null, result, modelByName);
        addGen(skoda, "Fabia", "III", 2014, 2021, result, modelByName);
        addModel(skoda, "Yeti", 2009, 2017, result, modelByName);
        addModel(skoda, "Superb", 2001, null, result, modelByName);
        addModel(skoda, "Enyaq", 2020, null, result, modelByName);

        // --- Dacia ---
        CarBrand dacia = brandRepository.save(new CarBrand("Dacia", "Румыния"));
        addModel(dacia, "Sandero", 2007, null, result, modelByName);
        addModel(dacia, "Duster", 2010, null, result, modelByName);
        addModel(dacia, "Logan", 2004, null, result, modelByName);
        addModel(dacia, "Spring", 2021, null, result, modelByName);

        // --- Infiniti ---
        CarBrand infiniti = brandRepository.save(new CarBrand("Infiniti", "Япония"));
        addGen(infiniti, "Q50", "V37", 2013, null, result, modelByName);
        addModel(infiniti, "FX/QX70", 2002, 2017, result, modelByName);
        addModel(infiniti, "G35/G37", 2003, 2013, result, modelByName);
        addModel(infiniti, "QX56/QX80", 2004, null, result, modelByName);
        addModel(infiniti, "M35/M45", 2005, 2013, result, modelByName);
        addModel(infiniti, "EX/QX50", 2007, 2017, result, modelByName);

        // --- Daihatsu ---
        CarBrand daihatsu = brandRepository.save(new CarBrand("Daihatsu", "Япония"));
        addModel(daihatsu, "Charade", 1977, 2000, result, modelByName);
        addModel(daihatsu, "Terios", 1997, null, result, modelByName);
        addModel(daihatsu, "Mira", 1980, null, result, modelByName);
        addModel(daihatsu, "Sirion", 1998, 2010, result, modelByName);
        addModel(daihatsu, "Move", 1995, null, result, modelByName);
        addModel(daihatsu, "Boon", 2004, null, result, modelByName);
        addModel(daihatsu, "Copen", 2002, 2012, result, modelByName);

        // --- Isuzu ---
        CarBrand isuzu = brandRepository.save(new CarBrand("Isuzu", "Япония"));
        addModel(isuzu, "Trooper", 1981, 2002, result, modelByName);
        addModel(isuzu, "Rodeo", 1988, 2004, result, modelByName);
        addModel(isuzu, "D-Max", 2002, null, result, modelByName);
        addModel(isuzu, "Mu-X", 2013, null, result, modelByName);
        addModel(isuzu, "Elf/NPR", 1947, null, result, modelByName);
        addModel(isuzu, "Gemini", 1974, 2000, result, modelByName);
        addModel(isuzu, "Bighorn", 1981, 2002, result, modelByName);
        addModel(isuzu, "VehiCross", 1997, 2001, result, modelByName);

        // --- Acura ---
        CarBrand acura = brandRepository.save(new CarBrand("Acura", "Япония"));
        addGen(acura, "Integra", "DC2", 1994, 2001, result, modelByName);
        addGen(acura, "Integra", "DE5", 2022, null, result, modelByName);
        addGen(acura, "NSX", "NC1", 2016, 2022, result, modelByName);
        addModel(acura, "TLX", 2014, null, result, modelByName);
        addModel(acura, "MDX", 2000, null, result, modelByName);
        addModel(acura, "RDX", 2006, null, result, modelByName);
        addModel(acura, "RLX", 2013, 2020, result, modelByName);
        addModel(acura, "TSX", 2004, 2014, result, modelByName);

        // --- Buick ---
        CarBrand buick = brandRepository.save(new CarBrand("Buick", "США"));
        addModel(buick, "Encore", 2012, null, result, modelByName);
        addModel(buick, "Enclave", 2007, null, result, modelByName);
        addModel(buick, "LaCrosse", 2004, null, result, modelByName);
        addModel(buick, "Regal", 1973, null, result, modelByName);
        addModel(buick, "LeSabre", 1959, 2005, result, modelByName);
        addModel(buick, "Park Avenue", 1991, 2012, result, modelByName);
        addModel(buick, "Riviera", 1963, 1999, result, modelByName);

        // --- GMC ---
        CarBrand gmc = brandRepository.save(new CarBrand("GMC", "США"));
        addModel(gmc, "Sierra", 1998, null, result, modelByName);
        addModel(gmc, "Yukon", 1991, null, result, modelByName);
        addModel(gmc, "Acadia", 2006, null, result, modelByName);
        addModel(gmc, "Terrain", 2009, null, result, modelByName);
        addModel(gmc, "Canyon", 2004, null, result, modelByName);
        addModel(gmc, "Savana", 1996, null, result, modelByName);

        // --- Lincoln ---
        CarBrand lincoln = brandRepository.save(new CarBrand("Lincoln", "США"));
        addModel(lincoln, "Navigator", 1997, null, result, modelByName);
        addModel(lincoln, "MKZ", 2006, 2020, result, modelByName);
        addModel(lincoln, "Aviator", 2002, null, result, modelByName);
        addModel(lincoln, "Corsair", 2019, null, result, modelByName);
        addModel(lincoln, "Nautilus", 2018, null, result, modelByName);
        addModel(lincoln, "Continental", 1939, 2020, result, modelByName);
        addModel(lincoln, "Town Car", 1981, 2011, result, modelByName);

        // --- Hummer ---
        CarBrand hummer = brandRepository.save(new CarBrand("Hummer", "США"));
        addGen(hummer, "H2", "SUT", 2003, 2009, result, modelByName);
        addModel(hummer, "H1", 1992, 2006, result, modelByName);
        addModel(hummer, "H3", 2005, 2010, result, modelByName);
        addModel(hummer, "EV", 2022, null, result, modelByName);

        // --- Pontiac ---
        CarBrand pontiac = brandRepository.save(new CarBrand("Pontiac", "США"));
        addModel(pontiac, "Firebird", 1967, 2002, result, modelByName);
        addModel(pontiac, "GTO", 1964, 2006, result, modelByName);
        addModel(pontiac, "Solstice", 2005, 2009, result, modelByName);
        addModel(pontiac, "Grand Am", 1973, 2005, result, modelByName);
        addModel(pontiac, "Trans Sport", 1989, 1999, result, modelByName);
        addModel(pontiac, "Fiero", 1984, 1988, result, modelByName);
        addModel(pontiac, "Vibe", 2002, 2010, result, modelByName);

        // --- Lancia ---
        CarBrand lancia = brandRepository.save(new CarBrand("Lancia", "Италия"));
        addModel(lancia, "Delta", 1979, 1999, result, modelByName);
        addModel(lancia, "Stratos", 1972, 1978, result, modelByName);
        addModel(lancia, "Thema", 1984, 1994, result, modelByName);
        addModel(lancia, "Ypsilon", 2003, null, result, modelByName);
        addModel(lancia, "Flavia", 1961, 1986, result, modelByName);
        addModel(lancia, "037", 1982, 1985, result, modelByName);

        // --- Pagani ---
        CarBrand pagani = brandRepository.save(new CarBrand("Pagani", "Италия"));
        addModel(pagani, "Zonda", 1999, 2019, result, modelByName);
        addModel(pagani, "Huayra", 2011, null, result, modelByName);
        addModel(pagani, "Utopia", 2022, null, result, modelByName);

        // --- Lotus ---
        CarBrand lotus = brandRepository.save(new CarBrand("Lotus", "Великобритания"));
        addModel(lotus, "Elise", 1996, 2021, result, modelByName);
        addModel(lotus, "Exige", 2000, 2021, result, modelByName);
        addModel(lotus, "Evora", 2008, 2021, result, modelByName);
        addModel(lotus, "Emira", 2021, null, result, modelByName);
        addModel(lotus, "Esprit", 1976, 2004, result, modelByName);
        addModel(lotus, "Eletre", 2023, null, result, modelByName);

        // --- McLaren ---
        CarBrand mclaren = brandRepository.save(new CarBrand("McLaren", "Великобритания"));
        addModel(mclaren, "MP4-12C", 2011, 2014, result, modelByName);
        addModel(mclaren, "650S", 2014, 2017, result, modelByName);
        addModel(mclaren, "720S", 2017, 2023, result, modelByName);
        addModel(mclaren, "P1", 2013, 2015, result, modelByName);
        addModel(mclaren, "Senna", 2018, 2020, result, modelByName);
        addModel(mclaren, "Artura", 2021, null, result, modelByName);
        addModel(mclaren, "765LT", 2020, null, result, modelByName);
        addModel(mclaren, "Speedtail", 2020, null, result, modelByName);

        // --- Saab ---
        CarBrand saab = brandRepository.save(new CarBrand("Saab", "Швеция"));
        addModel(saab, "900", 1978, 1998, result, modelByName);
        addModel(saab, "9-3", 1998, 2014, result, modelByName);
        addModel(saab, "9-5", 1997, 2012, result, modelByName);
        addModel(saab, "9-4X", 2011, 2012, result, modelByName);
        addModel(saab, "9000", 1984, 1998, result, modelByName);
        addModel(saab, "Sonett", 1966, 1974, result, modelByName);

        // --- SEAT ---
        CarBrand seat = brandRepository.save(new CarBrand("SEAT", "Испания"));
        addGen(seat, "Leon", "1M", 1999, 2005, result, modelByName);
        addGen(seat, "Leon", "1P", 2005, 2012, result, modelByName);
        addGen(seat, "Leon", "3P/5F", 2012, 2020, result, modelByName);
        addGen(seat, "Leon", "KL", 2020, null, result, modelByName);
        addGen(seat, "Ibiza", "6L", 2002, 2008, result, modelByName);
        addGen(seat, "Ibiza", "6J", 2008, 2017, result, modelByName);
        addGen(seat, "Ibiza", "6F", 2017, null, result, modelByName);
        addModel(seat, "Altea", 2004, 2015, result, modelByName);
        addModel(seat, "Toledo", 1991, 2019, result, modelByName);
        addModel(seat, "Arona", 2017, null, result, modelByName);
        addModel(seat, "Ateca", 2016, null, result, modelByName);
        addModel(seat, "Tarraco", 2018, null, result, modelByName);

        // --- Cupra ---
        CarBrand cupra = brandRepository.save(new CarBrand("Cupra", "Испания"));
        addModel(cupra, "Born", 2021, null, result, modelByName);
        addModel(cupra, "Formentor", 2020, null, result, modelByName);
        addModel(cupra, "Leon", 2020, null, result, modelByName);
        addModel(cupra, "Ateca", 2020, null, result, modelByName);
        addModel(cupra, "Tavascan", 2024, null, result, modelByName);

        // --- SsangYong (KG Mobility) ---
        CarBrand ssangyong = brandRepository.save(new CarBrand("SsangYong", "Корея"));
        addModel(ssangyong, "Korando", 1996, null, result, modelByName);
        addModel(ssangyong, "Rexton", 2001, null, result, modelByName);
        addModel(ssangyong, "Tivoli", 2015, null, result, modelByName);
        addModel(ssangyong, "Actyon", 2005, null, result, modelByName);
        addModel(ssangyong, "Kyron", 2005, 2015, result, modelByName);
        addModel(ssangyong, "Stavic", 2004, null, result, modelByName);
        addModel(ssangyong, "Chairman", 1997, null, result, modelByName);

        // --- Москвич ---
        CarBrand moskvich = brandRepository.save(new CarBrand("Москвич", "Россия"));
        addModel(moskvich, "3", 2022, null, result, modelByName);
        addModel(moskvich, "3e", 2022, null, result, modelByName);
        addModel(moskvich, "2140/412", 1976, 1998, result, modelByName);
        addModel(moskvich, "ИЖ 2125", 1977, 1993, result, modelByName);
        addModel(moskvich, "Святогор", 1997, 2002, result, modelByName);
        addModel(moskvich, "Юрий Долгорукий", 1998, 2002, result, modelByName);

        // --- ЗАЗ ---
        CarBrand zaz = brandRepository.save(new CarBrand("ЗАЗ", "Украина"));
        addModel(zaz, "Sens", 1999, 2010, result, modelByName);
        addModel(zaz, "Lanos", 1997, 2017, result, modelByName);
        addModel(zaz, "Vida", 2012, 2017, result, modelByName);
        addModel(zaz, "Forza", 2010, 2017, result, modelByName);
        addModel(zaz, "Таврия", 1987, 2007, result, modelByName);
        addModel(zaz, "965", 1960, 1969, result, modelByName);
        addModel(zaz, "966", 1966, 1974, result, modelByName);
        addModel(zaz, "968", 1971, 1994, result, modelByName);

        return result;
    }

    private void addModel(CarBrand brand, String modelName, Integer startYear, Integer endYear,
                           Map<String, Map<String, Map<String, CarGeneration>>> result,
                           Map<String, CarModel> modelByName) {
        String key = brand.getId() + ":" + modelName;
        if (modelByName.containsKey(key)) return;
        CarModel m = modelRepository.save(new CarModel(brand, modelName, startYear, endYear));
        modelByName.put(key, m);
        result.computeIfAbsent(brand.getName(), k -> new LinkedHashMap<>())
              .put(modelName, new LinkedHashMap<>());
    }

    private void addGen(CarBrand brand, String modelName, String genName,
                          Integer startYear, Integer endYear,
                          Map<String, Map<String, Map<String, CarGeneration>>> result,
                          Map<String, CarModel> modelByName) {
        String key = brand.getId() + ":" + modelName;
        CarModel model = modelByName.get(key);
        if (model == null) {
            model = modelRepository.save(new CarModel(brand, modelName, null, null));
            modelByName.put(key, model);
        }
        CarGeneration g = generationRepository.save(new CarGeneration(model, genName, startYear, endYear));
        result.computeIfAbsent(brand.getName(), k -> new LinkedHashMap<>())
              .computeIfAbsent(modelName, k -> new LinkedHashMap<>())
              .put(genName, g);
    }

    private RussianRegion findRegion(List<RussianRegion> regions, String name) {
        return regions.stream().filter(r -> r.getName().equals(name)).findFirst().orElse(null);
    }

    private CarBrand findBrand(Map<String, Map<String, Map<String, CarGeneration>>> data, String brandName) {
        if (data.get(brandName) == null) return null;
        return brandRepository.findByName(brandName).orElse(null);
    }

    private CarModel findModel(Map<String, Map<String, Map<String, CarGeneration>>> data,
                                String brandName, String modelName) {
        if (data.get(brandName) == null || data.get(brandName).get(modelName) == null) return null;
        CarBrand brand = findBrand(data, brandName);
        if (brand == null) return null;
        List<CarModel> models = modelRepository.findByBrandIdOrderByNameAsc(brand.getId());
        return models.stream().filter(m -> m.getName().equals(modelName)).findFirst().orElse(null);
    }

    private CarGeneration findGen(Map<String, Map<String, Map<String, CarGeneration>>> data,
                                   String brandName, String modelName, String genName) {
        try {
            return data.get(brandName).get(modelName).get(genName);
        } catch (Exception e) {
            return null;
        }
    }

    private User saveUser(String email, String firstName, String lastName, String phone, Set<Role> roles) {
        User u = new User();
        u.setEmail(email);
        u.setPasswordHash(passwordEncoder.encode("1234"));
        u.setFirstName(firstName);
        u.setLastName(lastName);
        u.setPhoneNumber(phone);
        u.setRoles(roles);
        return userRepository.save(u);
    }

    private void saveCar(User user, ProjectTag tag, int year,
                          BigDecimal price, String city, RussianRegion region, String desc,
                          List<ModsCategory> mods, Integer mileageKm, Integer mileageHours,
                          String engineType, Double engineDisplacement, Integer enginePower,
                          String bodyType, boolean damaged, String steeringSide, boolean registered,
                          CarBrand carBrand, CarModel carModel, CarGeneration carGeneration, String imagePath) {
        VehicleCar c = new VehicleCar();
        c.setUser(user);
        c.setProjectTag(tag);
        c.setManufactureYear(year);
        c.setPrice(price);
        c.setCity(city);
        c.setRegion(region);
        c.setDescription(desc);
        c.setSelectedMods(mods);
        c.setMileageKm(mileageKm);
        c.setMileageHours(mileageHours);
        c.setEngineType(engineType);
        c.setEngineDisplacement(engineDisplacement);
        c.setEnginePower(enginePower);
        c.setBodyType(bodyType);
        c.setDamaged(damaged);
        c.setSteeringSide(steeringSide);
        c.setRegistered(registered);
        c.setCarBrand(carBrand);
        c.setCarModel(carModel);
        c.setCarGeneration(carGeneration);
        if (imagePath != null && !imagePath.isEmpty()) {
            CarImage img = new CarImage(c, imagePath, 0, true);
            c.getImages().add(img);
        }
        carRepository.save(c);
    }
}
