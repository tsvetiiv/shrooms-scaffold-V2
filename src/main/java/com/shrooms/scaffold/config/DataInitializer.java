package com.shrooms.scaffold.config;

import com.shrooms.scaffold.model.entity.ourWork.OurWorkProject;
import com.shrooms.scaffold.model.entity.scaffold.MaterialType;
import com.shrooms.scaffold.model.entity.scaffold.Scaffold;
import com.shrooms.scaffold.model.entity.scaffold.ScaffoldCategory;
import com.shrooms.scaffold.model.entity.user.RoleType;
import com.shrooms.scaffold.model.entity.user.User;
import com.shrooms.scaffold.repository.ourWork.OurWorkProjectRepository;
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
    private final OurWorkProjectRepository ourWorkProjectRepository;

    public DataInitializer(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           ScaffoldRepository scaffoldRepository, OurWorkProjectRepository ourWorkProjectRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.scaffoldRepository = scaffoldRepository;
        this.ourWorkProjectRepository = ourWorkProjectRepository;
    }

    @Override
    public void run(String... args) {
        seedAdmin();
        seedOwner();
        seedScaffolds();
        seedOurWorkProjects();
    }

    private void seedAdmin() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = User.builder()
                    .username("admin")
                    .firstName("Admin")
                    .lastName("Shrooms")
                    .email("admin.shrooms@gmail.com")
                    .password(passwordEncoder.encode("shrooms123"))
                    .roleType(RoleType.ADMIN)
                    .profilePicture("/images/skull-logo.png")
                    .active(true)
                    .blocked(false)
                    .build();

            userRepository.save(admin);
        }
    }

    private void seedOwner() {
        if (userRepository.findByUsername("owner").isEmpty()) {
            User owner = User.builder()
                    .username("owner")
                    .firstName("Owner")
                    .lastName("Shrooms")
                    .email("owner.shrooms@gmail.com")
                    .password(passwordEncoder.encode("owner123"))
                    .roleType(RoleType.OWNER)
                    .profilePicture("/images/skull-logo.png")
                    .active(true)
                    .blocked(false)
                    .build();
            userRepository.save(owner);
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

    private void seedOurWorkProjects() {
        if (ourWorkProjectRepository.count() == 0) {
            ourWorkProjectRepository.save(OurWorkProject.builder()
                    .title("Facade Renovation")
                    .imageUrl("/images/project3.png")
                    .description("Scaffold system prepared for safe exterior renovation work.")
                    .visible(true)
                    .build());

            ourWorkProjectRepository.save(OurWorkProject.builder()
                    .title("Commercial Building Scaffold")
                    .imageUrl("/images/project4.JPG")
                    .description("Large scaffold structure for professional building maintenance.")
                    .visible(true)
                    .build());

            ourWorkProjectRepository.save(OurWorkProject.builder()
                    .title("Custom Access Scaffold")
                    .imageUrl("/images/project9.JPG")
                    .description("Specialized scaffold construction featuring elevated access\n" +
                            "platforms and protective screening for complex urban projects.")
                    .visible(true)
                    .build());

            ourWorkProjectRepository.save(OurWorkProject.builder()
                    .title("Historic Building Restoration")
                    .imageUrl("/images/project1.png")
                    .description("A specialized scaffold solution providing secure\n" +
                            "vertical access for restoration and maintenance work\n" +
                            "on historic and architectural landmarks.")
                    .visible(true)
                    .build());

            ourWorkProjectRepository.save(OurWorkProject.builder()
                    .title("Custom Residential Access Solution")
                    .imageUrl("/images/project7.JPG")
                    .description("Specialized scaffold system installed for facade\n" +
                            "and roof maintenance, ensuring safe and efficient\n" +
                            "access to elevated work areas.")
                    .visible(true)
                    .build());

            ourWorkProjectRepository.save(OurWorkProject.builder()
                    .title("Historic Windmill Scaffolding")
                    .imageUrl("/images/custom-windmill.JPG")
                    .description("Specialized scaffolding erected for the restoration of a traditional windmill. The structure provides\n" +
                            "safe access to all elevations while supporting conservation and maintenance activities on this historic\n" +
                            "landmark.")
                    .visible(true)
                    .build());

            ourWorkProjectRepository.save(OurWorkProject.builder()
                    .title("Roof Access Scaffolding System")
                    .imageUrl("/images/roofCustom.JPG")
                    .description("Temporary access scaffolding installed for inspection, maintenance, and refurbishment works. The system\n" +
                            "provides a safe and stable working platform while ensuring minimal impact on the building and its\n" +
                            "surroundings.")
                    .visible(true)
                    .build());
        }
    }
}

