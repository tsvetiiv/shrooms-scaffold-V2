package com.shrooms.scaffold.config;

import com.shrooms.scaffold.model.entity.scaffold.MaterialType;
import com.shrooms.scaffold.model.entity.scaffold.Scaffold;
import com.shrooms.scaffold.model.entity.scaffold.ScaffoldCategory;
import com.shrooms.scaffold.model.entity.user.RoleType;
import com.shrooms.scaffold.model.entity.user.User;
import com.shrooms.scaffold.repository.scaffold.ScaffoldRepository;
import com.shrooms.scaffold.repository.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ScaffoldRepository scaffoldRepository;

    public DataInitializer(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           ScaffoldRepository scaffoldRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.scaffoldRepository = scaffoldRepository;
    }

    @Override
    public void run(String... args) {
        seedAdmin();
        seedScaffolds();
    }

    private void seedAdmin() {
        if (userRepository.count() == 0) {
            User admin = User.builder()
                    .username("admin")
                    .firstName("Admin")
                    .lastName("Shrooms")
                    .email("admin.shrooms@gmail.com")
                    .password(passwordEncoder.encode("shrooms123"))
                    .roleType(RoleType.ADMIN)
                    .profilePicture("/images/skull-logo.png")
                    .build();

            userRepository.save(admin);
        }
    }

    private void seedScaffolds() {
        if (scaffoldRepository.count() == 0) {
            Scaffold facadeSteelScaffold = Scaffold.builder()
                    .name("Facade Steel Scaffold")
                    .description("""
                            Facade scaffolding is used for both small and large renovation and construction projects.
                            It is a flexible scaffolding solution that can be adapted around different types of buildings
                            and construction sites. Suitable for multiple craftsmen working simultaneously, while providing
                            safe and stable access during renovation work.
                            """)
                    .height(4.00)
                    .length(10)
                    .width(0.75)
                    .materialType(MaterialType.STEEL)
                    .scaffoldCategory(ScaffoldCategory.FACADE)
                    .priceForRent(new BigDecimal("120.00"))
                    .priceForSale(new BigDecimal("2800.00"))
                    .imageUrl("/images/facadeSteelScaffold.png")
                    .available(true)
                    .build();

            Scaffold mobileAluminiumScaffold = Scaffold.builder()
                    .name("Mobile Aluminium Scaffold")
                    .description("""
                            A 40m² mobile scaffold (or rolling scaffold) is a temporary, freestanding work platform mounted on lockable caster wheels.
                            It allows workers to easily move elevated platforms around a job site without dismantling the structure, making it ideal
                            for tasks that require frequent position changes, such as painting, plastering, and maintenance.
                            """)
                    .height(5.2)
                    .width(0.75)
                    .length(1.9)
                    .materialType(MaterialType.ALUMINIUM)
                    .scaffoldCategory(ScaffoldCategory.MOBILE)
                    .priceForRent(new BigDecimal("80.00"))
                    .priceForSale(new BigDecimal("1300.00"))
                    .imageUrl("/images/mobileAluminium.png")
                    .available(true)
                    .build();


            Scaffold facadeAluminiumScaffold = Scaffold.builder()
                    .name("Facade Aluminium Scaffold")
                    .description("""
                            Premium aluminium facade scaffold engineered for demanding
                            construction and renovation projects. Combining strength,
                            lightweight design and rapid assembly, it offers reliable
                            access solutions for painting, maintenance, roofing and
                            general facade work while ensuring maximum safety on site.""")
                    .height(6.0)
                    .width(0.75)
                    .length(5.0)
                    .materialType(MaterialType.ALUMINIUM)
                    .scaffoldCategory(ScaffoldCategory.FACADE)
                    .priceForRent(new BigDecimal("180.00"))
                    .priceForSale(new BigDecimal("4100.00"))
                    .imageUrl("/images/aluminiumFacade.png")
                    .available(true)
                    .build();


            Scaffold roomScaffold = Scaffold.builder()
                    .name("Room Aluminium Scaffold")
                    .description("""
                                           Professional indoor room scaffold suitable for painting,
                                           decorating, electrical installations and maintenance work.
                                           Compact, mobile and easy to assemble, offering safe access
                                           to elevated indoor work areas while fitting through standard
                                           doorways and narrow spaces.
                            """)
                    .height(3.00)
                    .length(1.90)
                    .width(0.75)
                    .materialType(MaterialType.ALUMINIUM)
                    .scaffoldCategory(ScaffoldCategory.ROOM)
                    .priceForRent(new BigDecimal("90.00"))
                    .priceForSale(new BigDecimal("400.00"))
                    .imageUrl("/images/roomScaffold.png")
                    .available(true)
                    .build();
            scaffoldRepository.save(facadeSteelScaffold);
            scaffoldRepository.save(mobileAluminiumScaffold);
            scaffoldRepository.save(facadeAluminiumScaffold);
            scaffoldRepository.save(roomScaffold);

        }
    }
}

