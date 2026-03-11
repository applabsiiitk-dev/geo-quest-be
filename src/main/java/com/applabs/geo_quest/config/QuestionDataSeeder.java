// PLAN: Riddle-based hints implementation
// - description field is now a riddle/clue for the next location
// - When a team answers correctly, response includes the description of the NEXT question
// - Seeder must ensure descriptions are riddles/clues
// - Controller/service logic must fetch and return next question's description
// - Update DTOs if needed to include the hint field
// - Test the flow end-to-end
package com.applabs.geo_quest.config;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.applabs.geo_quest.model.Question;
import com.applabs.geo_quest.repository.QuestionRepository;

/**
 * Seeds the GeoQuest question bank into PostgreSQL on startup.
 * <p>
 * Only runs when the "seed" Spring profile is active. This class is responsible
 * for populating
 * the database with all campus questions, ensuring that the description field
 * is a riddle/clue
 * for the next location. Supports dynamic radius tiers for location types and
 * difficulty tiers
 * for scoring. Each location receives two questions, and the seeder ensures
 * that descriptions
 * are written as riddles for the riddle-based hint system.
 * <p>
 * Usage:
 * <ul>
 * <li>Run with:
 * <code>java -jar geo_quest.jar --spring.profiles.active=seed</code></li>
 * <li>Or set: <code>SPRING_PROFILES_ACTIVE=seed</code> in your .env / Docker
 * env</li>
 * </ul>
 * <p>
 * Radius tiers:
 * <ul>
 * <li>Indoor / Near Walls → R_INDOOR = 15 m (effective 30 m with GPS
 * tolerance)</li>
 * <li>Gate / Entrance → R_GATE = 10 m (effective 25 m)</li>
 * <li>Wide Open Space → R_OPEN = 5 m (effective 20 m)</li>
 * </ul>
 * <p>
 * Difficulty tiers:
 * <ul>
 * <li>1 (easy) score 0–99 → 10 pts</li>
 * <li>2 (medium) score 100–249 → 20–25 pts</li>
 * <li>3 (hard) score 250+ → 40–50 pts</li>
 * </ul>
 * <p>
 * 2 questions per campus location — 120 questions total.
 * <p>
 * 
 * @author fl4nk3r
 * @since 2026-03-11
 * @version 3.0
 */
@Component
@Profile("seed")
public class QuestionDataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(QuestionDataSeeder.class);
    private final QuestionRepository questionRepository;

    public QuestionDataSeeder(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (questionRepository.count() > 0) {
            log.info("Questions already exist — skipping seed.");
            return;
        }
        log.info("Seeding {} questions...", ALL_QUESTIONS.size());
        questionRepository.saveAll(ALL_QUESTIONS);
        log.info("Seeding complete.");
    }

    // ── Radius constants ─────────────────────────────────────────────────────
    private static final double R_INDOOR = 15; // classrooms, hostels, cafes, gym
    private static final double R_GATE = 10; // gates, entrances, road-side
    private static final double R_OPEN = 5; // grounds, courts, open areas

    // ── Campus spawn locations ────────────────────────────────────────────────
    // # | Name | Lat | Lng | R
    // --+-----------------------------+--------------------+--------------------+------
    // 1 | Tapovan | 9.7548139681064 | 76.6495181504958 | OPEN
    // 2 | OAT | 9.75509742305537 | 76.650658956084 | OPEN
    // 3 | Ground | 9.75367058089191 | 76.6494119171544 | OPEN
    // 4 | BD 401 | 9.75512555992291 | 76.6491608765765 | INDOOR
    // 5 | BC 301 | 9.75511914503033 | 76.6491142738512 | INDOOR
    // 6 | BB 204 | 9.75531602433849 | 76.6488482558075 | INDOOR
    // 7 | BB 201 | 9.75509812712949 | 76.6490752554422 | INDOOR
    // 8 | BB 202 | 9.755052570955 | 76.6489141340614 | INDOOR
    // 9 | BC 302/303 | 9.75506892064191 | 76.6488514944353 | INDOOR
    // 10 | Admin | 9.75466663402216 | 76.6497609043484 | INDOOR
    // 11 | Millet Cafe | 9.755315113559977 | 76.64997867938409 | INDOOR
    // 12 | Milma Cafe | 9.75503491036912 | 76.64966486092287 | INDOOR
    // 13 | Nescafe | 9.755068614069865 | 76.64941809771327 | INDOOR
    // 14 | Old Academic Block Dome | 9.754652883458515 | 76.65000261365489 | INDOOR
    // 15 | Medical | 9.75500181609889 | 76.64867156743854 | INDOOR
    // 16 | Parking Lot | 9.754822636304352 | 76.64897435242618 | OPEN
    // 17 | Football Ground | 9.754037703368219 | 76.64920229002907 | OPEN
    // 18 | OAT Open Air Theatre | 9.755082097835006 | 76.65059217420868 | OPEN
    // 19 | Migloo | 9.755314719339436 | 76.64972850290427 | INDOOR
    // 20 | Binary Body Gym | 9.755631965538457 | 76.6491480907726 | INDOOR
    // 21 | Directors Bungalow | 9.755806401359141 | 76.65047151000621 | INDOOR
    // 22 | Scoops | 9.755029404917197 | 76.64874804349478 | INDOOR
    // 23 | Huts-3 | 9.755188337091989 | 76.64950476963294 | INDOOR
    // 24 | Huts-2 | 9.7552405447544 | 76.64948532361903 | INDOOR
    // 25 | Huts-1 | 9.755212788782996 | 76.6494021751458 | INDOOR
    // 26 | Huts-4 | 9.755297378402913 | 76.64956377822686 | INDOOR
    // 27 | Huts-5 | 9.755234597046435 | 76.64958389479297 | INDOOR
    // 28 | Huts-6 | 9.755218075634865 | 76.64965095001332 | INDOOR
    // 29 | Volleyball Court | 9.755057721644512 | 76.6512491988952 | OPEN
    // 30 | Main Entrance Gate | 9.75453644009712 | 76.65122253311198 | GATE
    // 31 | Cooptyre Hostel | 9.752560700523288 | 76.65070603101995 | INDOOR
    // 32 | Open Air Gym | 9.754050707196384 | 76.64965444106694 | OPEN
    // 33 | Sahyadri Hostel | 9.756201980509356 | 76.6480915078891 | INDOOR
    // 34 | Meenachil Hostel | 9.756130290653482 | 76.64844181737266 | INDOOR
    // 35 | Girls Hostel | 9.756288339054032 | 76.64918504541902 | INDOOR
    // 36 | Staircase (Academic Block) | 9.754964385042799 | 76.64919794988073 |
    // INDOOR
    // 37 | Central Mess | 9.755462442304191 | 76.65024122132576 | INDOOR

    private static final List<Question> ALL_QUESTIONS = List.of(

            // ══════════════════════════════════════════════════════════════════════
            // DIFFICULTY 1 · 10 pts · score gate 0–99 · 46 questions
            // ══════════════════════════════════════════════════════════════════════

            // Tapovan (OPEN)
            q("What does CPU stand for?", "Choose the correct expansion.",
                    1, 10, 9.7548139681064, 76.6495181504958, R_OPEN, "CSE", "Tapovan",
                    "Central Processing Unit",
                    List.of("Central Program Unit", "Central Processing Unit", "Computer Processing Unit",
                            "Control Processing Unit")),
            q("What does ROM stand for?", "Choose the correct expansion.",
                    1, 10, 9.7548139681064, 76.6495181504958, R_OPEN, "Hardware", "Tapovan",
                    "Read Only Memory",
                    List.of("Read Only Memory", "Random Only Memory", "Run On Memory", "Read On Module")),

            // Milma Cafe (INDOOR)
            q("What does RAM stand for?", "Choose the correct expansion.",
                    1, 10, 9.75503491036912, 76.64966486092287, R_INDOOR, "CSE", "Milma Cafe",
                    "Random Access Memory",
                    List.of("Random Access Memory", "Read Access Memory", "Rapid Access Memory", "Run Access Memory")),
            q("What does USB stand for?", "Choose the correct expansion.",
                    1, 10, 9.75503491036912, 76.64966486092287, R_INDOOR, "Hardware", "Milma Cafe",
                    "Universal Serial Bus",
                    List.of("Universal Serial Bus", "Unified System Bus", "Universal Storage Block",
                            "Ultra Speed Bus")),

            // Nescafe (INDOOR)
            q("Which language is primarily used for web page structure?", "Pick the right language.",
                    1, 10, 9.755068614069865, 76.64941809771327, R_INDOOR, "Web", "Nescafe",
                    "HTML",
                    List.of("Python", "HTML", "Java", "C++")),
            q("What does CSS stand for?", "Full form of CSS.",
                    1, 10, 9.755068614069865, 76.64941809771327, R_INDOOR, "Web", "Nescafe",
                    "Cascading Style Sheets",
                    List.of("Cascading Style Sheets", "Computer Style Syntax", "Coded Style System",
                            "Creative Style Sheets")),

            // Admin (INDOOR)
            q("Which protocol is used to transfer web pages?", "Select the correct protocol.",
                    1, 10, 9.75466663402216, 76.6497609043484, R_INDOOR, "Networking", "Admin",
                    "HTTP",
                    List.of("FTP", "HTTP", "TCP", "SMTP")),
            q("What does IP stand for in networking?", "Full form of IP address.",
                    1, 10, 9.75466663402216, 76.6497609043484, R_INDOOR, "Networking", "Admin",
                    "Internet Protocol",
                    List.of("Internet Protocol", "Internal Process", "Interface Protocol", "Internet Port")),

            // Millet Cafe (INDOOR)
            q("Which company developed Python?", "Who created the Python language?",
                    1, 10, 9.755315113559977, 76.64997867938409, R_INDOOR, "General", "Millet Cafe",
                    "Python Software Foundation",
                    List.of("Google", "Microsoft", "Python Software Foundation", "Apple")),
            q("In what year was Python first released?", "Guess the birth year of Python.",
                    1, 10, 9.755315113559977, 76.64997867938409, R_INDOOR, "General", "Millet Cafe",
                    "1991",
                    List.of("1989", "1991", "1995", "2000")),

            // OAT Open Air Theatre (OPEN)
            q("Which of these is NOT an operating system?", "Identify the odd one out.",
                    1, 10, 9.755082097835006, 76.65059217420868, R_OPEN, "General", "OAT Open Air Theatre",
                    "Oracle",
                    List.of("Linux", "Windows", "Oracle", "macOS")),
            q("Which of these is an open-source operating system?", "Pick the open-source OS.",
                    1, 10, 9.755082097835006, 76.65059217420868, R_OPEN, "General", "OAT Open Air Theatre",
                    "Linux",
                    List.of("Windows", "macOS", "Linux", "iOS")),

            // Binary Body Gym (INDOOR)
            q("Which memory is fastest?", "Pick the fastest type of memory.",
                    1, 10, 9.755631965538457, 76.6491480907726, R_INDOOR, "Hardware", "Binary Body Gym",
                    "Cache",
                    List.of("RAM", "Cache", "Hard Disk", "ROM")),
            q("Which storage device has no moving parts?", "Pick the solid-state storage.",
                    1, 10, 9.755631965538457, 76.6491480907726, R_INDOOR, "Hardware", "Binary Body Gym",
                    "SSD",
                    List.of("HDD", "SSD", "Tape Drive", "Optical Disc")),

            // BB 202 (INDOOR)
            q("What does SQL stand for?", "Full form of SQL.",
                    1, 10, 9.755052570955, 76.6489141340614, R_INDOOR, "Databases", "BB 202",
                    "Structured Query Language",
                    List.of("Structured Query Language", "Simple Query Language", "Standard Query Language",
                            "Sequential Query Language")),
            q("Which SQL keyword retrieves data from a table?", "Basic SQL retrieval command.",
                    1, 10, 9.755052570955, 76.6489141340614, R_INDOOR, "Databases", "BB 202",
                    "SELECT",
                    List.of("GET", "FETCH", "SELECT", "READ")),

            // BC 302/303 (INDOOR)
            q("What does API stand for?", "Full form of API.",
                    1, 10, 9.75506892064191, 76.6488514944353, R_INDOOR, "General", "BC 302/BC 303",
                    "Application Programming Interface",
                    List.of("Application Programming Interface", "Application Process Interface",
                            "Applied Programming Interface", "Application Program Internet")),
            q("What does IDE stand for?", "Full form of IDE.",
                    1, 10, 9.75506892064191, 76.6488514944353, R_INDOOR, "General", "BC 302/BC 303",
                    "Integrated Development Environment",
                    List.of("Integrated Development Environment", "Internal Debug Engine", "Interface Design Editor",
                            "Integrated Debug Extension")),

            // Medical (INDOOR)
            q("What does DNS stand for?", "Full form of DNS.",
                    1, 10, 9.75500181609889, 76.64867156743854, R_INDOOR, "Networking", "Medical",
                    "Domain Name System",
                    List.of("Domain Name System", "Data Network Service", "Domain Network Server",
                            "Digital Name System")),
            q("What does VPN stand for?", "Full form of VPN.",
                    1, 10, 9.75500181609889, 76.64867156743854, R_INDOOR, "Networking", "Medical",
                    "Virtual Private Network",
                    List.of("Virtual Private Network", "Visual Proxy Node", "Verified Public Network",
                            "Virtual Protocol Node")),

            // Migloo (INDOOR)
            q("What does GPU stand for?", "Full form of GPU.",
                    1, 10, 9.755314719339436, 76.64972850290427, R_INDOOR, "Hardware", "Migloo",
                    "Graphics Processing Unit",
                    List.of("General Processing Unit", "Graphics Processing Unit", "Graph Processing Unit",
                            "Graphic Program Utility")),
            q("What does ALU stand for?", "Full form of ALU inside a CPU.",
                    1, 10, 9.755314719339436, 76.64972850290427, R_INDOOR, "Hardware", "Migloo",
                    "Arithmetic Logic Unit",
                    List.of("Arithmetic Logic Unit", "Automated Load Unit", "Array Logic Utility",
                            "Application Layer Unit")),

            // Huts-2 (INDOOR)
            q("What does LAN stand for?", "Full form of LAN.",
                    1, 10, 9.7552405447544, 76.64948532361903, R_INDOOR, "Networking", "Huts-2",
                    "Local Area Network",
                    List.of("Local Area Network", "Large Area Network", "Long Area Node", "Local Access Network")),
            q("What does WAN stand for?", "Full form of WAN.",
                    1, 10, 9.7552405447544, 76.64948532361903, R_INDOOR, "Networking", "Huts-2",
                    "Wide Area Network",
                    List.of("Wide Area Network", "Wired Access Node", "Wireless Area Network", "Web Area Node")),

            // Huts-3 (INDOOR)
            q("What does URL stand for?", "Full form of URL.",
                    1, 10, 9.755188337091989, 76.64950476963294, R_INDOOR, "Networking", "Huts-3",
                    "Uniform Resource Locator",
                    List.of("Universal Resource Locator", "Uniform Resource Locator", "Unified Resource Locator",
                            "Universal Reference Link")),
            q("What does HTTP stand for?", "Full form of HTTP.",
                    1, 10, 9.755188337091989, 76.64950476963294, R_INDOOR, "Networking", "Huts-3",
                    "HyperText Transfer Protocol",
                    List.of("HyperText Transfer Protocol", "High Transfer Text Protocol",
                            "Hyper Terminal Transport Protocol", "Host Text Transfer Protocol")),

            // Directors Bungalow (INDOOR)
            q("What does SSD stand for?", "Full form of SSD.",
                    1, 10, 9.755806401359141, 76.65047151000621, R_INDOOR, "Hardware", "Directors Bungalow",
                    "Solid State Drive",
                    List.of("Solid State Drive", "Secure Storage Disk", "System Storage Device", "Solid Storage Disk")),
            q("What does HDD stand for?", "Full form of HDD.",
                    1, 10, 9.755806401359141, 76.65047151000621, R_INDOOR, "Hardware", "Directors Bungalow",
                    "Hard Disk Drive",
                    List.of("Hard Disk Drive", "High Data Drive", "Hybrid Data Disk", "Host Disk Device")),

            // Scoops (INDOOR)
            q("Which language is mainly used for styling web pages?", "Pick the styling language.",
                    1, 10, 9.755029404917197, 76.64874804349478, R_INDOOR, "Web", "Scoops",
                    "CSS",
                    List.of("HTML", "CSS", "Python", "Java")),
            q("Which language adds interactivity to web pages?", "Pick the client-side scripting language.",
                    1, 10, 9.755029404917197, 76.64874804349478, R_INDOOR, "Web", "Scoops",
                    "JavaScript",
                    List.of("Python", "Ruby", "JavaScript", "PHP")),

            // Huts-1 (INDOOR)
            q("Which protocol is used to send email?", "Select the email-sending protocol.",
                    1, 10, 9.755212788782996, 76.6494021751458, R_INDOOR, "Networking", "Huts-1",
                    "SMTP",
                    List.of("HTTP", "FTP", "SMTP", "TCP")),
            q("Which protocol is used to receive email?", "Select the email-receiving protocol.",
                    1, 10, 9.755212788782996, 76.6494021751458, R_INDOOR, "Networking", "Huts-1",
                    "IMAP",
                    List.of("SMTP", "FTP", "IMAP", "UDP")),

            // Huts-4 (INDOOR)
            q("Which company developed Java?", "Who created Java?",
                    1, 10, 9.755297378402913, 76.64956377822686, R_INDOOR, "General", "Huts-4",
                    "Sun Microsystems",
                    List.of("Apple", "Sun Microsystems", "Google", "IBM")),
            q("What is the file extension for a Java source file?", "Pick the correct extension.",
                    1, 10, 9.755297378402913, 76.64956377822686, R_INDOOR, "General", "Huts-4",
                    ".java",
                    List.of(".jav", ".java", ".js", ".jvm")),

            // Old Academic Block Dome (INDOOR)
            q("Which database language is used to retrieve data?", "Pick the data-retrieval language.",
                    1, 10, 9.754652883458515, 76.65000261365489, R_INDOOR, "Databases", "Old Academic Block Dome",
                    "SQL",
                    List.of("SQL", "HTML", "CSS", "XML")),
            q("Which keyword is used to insert data into a SQL table?", "Basic SQL insertion command.",
                    1, 10, 9.754652883458515, 76.65000261365489, R_INDOOR, "Databases", "Old Academic Block Dome",
                    "INSERT",
                    List.of("ADD", "PUT", "INSERT", "APPEND")),

            // Parking Lot (OPEN)
            q("What does 'CAPTCHA' actually stand for?",
                    "You prove you're not a robot every day — do you know its full name?",
                    1, 10, 9.754822636304352, 76.64897435242618, R_OPEN, "General", "Parking Lot",
                    "Completely Automated Public Turing test to tell Computers and Humans Apart",
                    List.of("Computer Aided Public Turing Test",
                            "Completely Automated Public Turing test to tell Computers and Humans Apart",
                            "Certified Automated Process for Total Computer Hacking Analysis",
                            "Control and Processing Test for Computer Humans")),
            q("What does 'Wi-Fi' actually stand for?", "It's not what most people think.",
                    1, 10, 9.754822636304352, 76.64897435242618, R_OPEN, "General", "Parking Lot",
                    "Wireless Fidelity",
                    List.of("Wireless Fidelity", "Wide Frequency", "Wireless Fibre", "Wideband Interface")),

            // Huts-5 (INDOOR)
            q("In 'The Matrix', what is the name of the ship Neo and Morpheus live on?", "The last hope of humanity.",
                    1, 10, 9.755234597046435, 76.64958389479297, R_INDOOR, "Film", "Huts-5",
                    "Nebuchadnezzar",
                    List.of("Discovery One", "Nebuchadnezzar", "Endurance", "Serenity")),
            q("In 'The Matrix', what color is the pill Neo takes to see the truth?", "Choose wisely.",
                    1, 10, 9.755234597046435, 76.64958389479297, R_INDOOR, "Film", "Huts-5",
                    "Red",
                    List.of("Blue", "Red", "Green", "Yellow")),

            // Football Ground (OPEN)
            q("What animal is the official mascot of the Linux kernel?", "A flightless bird who loves open source.",
                    1, 10, 9.754037703368219, 76.64920229002907, R_OPEN, "General", "Football Ground",
                    "Tux the Penguin",
                    List.of("Puffy the Fish", "Tux the Penguin", "Gopher", "Ferris the Crab")),
            q("Who created the Linux kernel?", "He started it as a hobby project in 1991.",
                    1, 10, 9.754037703368219, 76.64920229002907, R_OPEN, "General", "Football Ground",
                    "Linus Torvalds",
                    List.of("Richard Stallman", "Linus Torvalds", "Dennis Ritchie", "Ken Thompson")),

            // Ground (OPEN)
            q("Which of these is the shortcut for 'Paste' in most operating systems?", "Simple, yet essential.",
                    1, 10, 9.75367058089191, 76.6494119171544, R_OPEN, "CS", "Ground",
                    "Ctrl + V",
                    List.of("Ctrl + P", "Ctrl + V", "Ctrl + C", "Ctrl + S")),
            q("Which keyboard shortcut is used to Undo an action?", "The most-used shortcut in history.",
                    1, 10, 9.75367058089191, 76.6494119171544, R_OPEN, "CS", "Ground",
                    "Ctrl + Z",
                    List.of("Ctrl + Z", "Ctrl + X", "Ctrl + U", "Ctrl + Y")),

            // OAT (OPEN)
            q("Who is considered the 'Father of the World Wide Web'?", "He invented HTTP and HTML while at CERN.",
                    1, 10, 9.75509742305537, 76.650658956084, R_OPEN, "GK", "OAT",
                    "Tim Berners-Lee",
                    List.of("Bill Gates", "Steve Jobs", "Tim Berners-Lee", "Vint Cerf")),
            q("Who is known as the 'Father of the Internet'?", "He co-designed the TCP/IP protocol.",
                    1, 10, 9.75509742305537, 76.650658956084, R_OPEN, "GK", "OAT",
                    "Vint Cerf",
                    List.of("Tim Berners-Lee", "Bill Gates", "Vint Cerf", "Alan Turing")),

            // ══════════════════════════════════════════════════════════════════════
            // DIFFICULTY 2 · 20–25 pts · score gate 100–249 · 46 questions
            // ══════════════════════════════════════════════════════════════════════

            // BD 401 (INDOOR)
            q("What is the average time complexity of Quick Sort?", "Choose the correct Big-O.",
                    2, 25, 9.75512555992291, 76.6491608765765, R_INDOOR, "Algorithms", "BD 401",
                    "O(n log n)",
                    List.of("O(n²)", "O(n log n)", "O(log n)", "O(n)")),
            q("What is the best case time complexity of Quick Sort?", "When is Quick Sort fastest?",
                    2, 25, 9.75512555992291, 76.6491608765765, R_INDOOR, "Algorithms", "BD 401",
                    "O(n log n)",
                    List.of("O(n)", "O(n log n)", "O(n²)", "O(log n)")),

            // BC 301 (INDOOR)
            q("Which traversal of a BST produces sorted output?", "Which traversal gives ascending order?",
                    2, 25, 9.75511914503033, 76.6491142738512, R_INDOOR, "Data Structures", "BC 301",
                    "Inorder",
                    List.of("Preorder", "Inorder", "Postorder", "Level order")),
            q("In a BST, where is the largest element found?", "Think about how BSTs are ordered.",
                    2, 25, 9.75511914503033, 76.6491142738512, R_INDOOR, "Data Structures", "BC 301",
                    "Rightmost node",
                    List.of("Root", "Leftmost node", "Rightmost node", "Any leaf")),

            // BB 204 (INDOOR)
            q("Which data structure is used in BFS?", "BFS uses which structure internally?",
                    2, 25, 9.75531602433849, 76.6488482558075, R_INDOOR, "Data Structures", "BB 204",
                    "Queue",
                    List.of("Stack", "Queue", "Heap", "Array")),
            q("What is the time complexity of Enqueue in a Queue?", "Enqueue on a standard queue.",
                    2, 25, 9.75531602433849, 76.6488482558075, R_INDOOR, "Data Structures", "BB 204",
                    "O(1)",
                    List.of("O(n)", "O(log n)", "O(1)", "O(n²)")),

            // BB 201 (INDOOR)
            q("Which data structure is used in DFS?", "DFS uses which structure internally?",
                    2, 25, 9.75509812712949, 76.6490752554422, R_INDOOR, "Data Structures", "BB 201",
                    "Stack",
                    List.of("Queue", "Stack", "Heap", "Array")),
            q("What is the time complexity of Push on a Stack?", "Push on a standard stack.",
                    2, 25, 9.75509812712949, 76.6490752554422, R_INDOOR, "Data Structures", "BB 201",
                    "O(1)",
                    List.of("O(n)", "O(1)", "O(log n)", "O(n²)")),

            // Staircase (Academic Block) (INDOOR)
            q("Which algorithm finds the shortest path with non-negative weights?", "Classic shortest-path algorithm.",
                    2, 25, 9.754964385042799, 76.64919794988073, R_INDOOR, "Algorithms", "Staircase (Academic Block)",
                    "Dijkstra",
                    List.of("Kruskal", "Dijkstra", "Prim", "Merge")),
            q("Which algorithm finds shortest paths with negative weights?",
                    "Handles negative edge weights unlike Dijkstra.",
                    2, 25, 9.754964385042799, 76.64919794988073, R_INDOOR, "Algorithms", "Staircase (Academic Block)",
                    "Bellman-Ford",
                    List.of("Dijkstra", "Prim", "Bellman-Ford", "Floyd-Warshall")),

            // Open Air Gym (OPEN)
            q("Which sorting algorithm repeatedly selects the smallest element?", "Identify the sort by its strategy.",
                    2, 25, 9.754050707196384, 76.64965444106694, R_OPEN, "Algorithms", "Open Air Gym",
                    "Selection Sort",
                    List.of("Selection Sort", "Merge Sort", "Heap Sort", "Quick Sort")),
            q("What is the time complexity of Selection Sort in all cases?", "It's always the same.",
                    2, 25, 9.754050707196384, 76.64965444106694, R_OPEN, "Algorithms", "Open Air Gym",
                    "O(n²)",
                    List.of("O(n)", "O(n log n)", "O(n²)", "O(log n)")),

            // Central Mess (INDOOR)
            q("Which data structure stores key-value pairs?", "Pick the correct structure.",
                    2, 25, 9.755462442304191, 76.65024122132576, R_INDOOR, "Data Structures", "Central Mess",
                    "Hash Table",
                    List.of("Stack", "Queue", "Hash Table", "Tree")),
            q("What is the average time complexity of search in a Hash Table?", "Average case lookup.",
                    2, 25, 9.755462442304191, 76.65024122132576, R_INDOOR, "Data Structures", "Central Mess",
                    "O(1)",
                    List.of("O(n)", "O(log n)", "O(1)", "O(n²)")),

            // Volleyball Court (OPEN)
            q("Which data structure allows insertion from both ends?", "Double-ended structure name.",
                    2, 25, 9.755057721644512, 76.6512491988952, R_OPEN, "Data Structures", "Volleyball Court",
                    "Deque",
                    List.of("Queue", "Stack", "Deque", "Heap")),
            q("Which data structure gives the minimum element in O(1)?", "Optimal structure for minimum retrieval.",
                    2, 25, 9.755057721644512, 76.6512491988952, R_OPEN, "Data Structures", "Volleyball Court",
                    "Min Heap",
                    List.of("Stack", "Queue", "Min Heap", "BST")),

            // Medical (INDOOR)
            q("What is the time complexity of accessing an element by index?", "Array index access complexity.",
                    2, 25, 9.75500181609889, 76.64867156743854, R_INDOOR, "Data Structures", "Medical",
                    "O(1)",
                    List.of("O(1)", "O(n)", "O(log n)", "O(n log n)")),
            q("What is the time complexity of inserting at the start of an array?", "Worst case insertion at index 0.",
                    2, 25, 9.75500181609889, 76.64867156743854, R_INDOOR, "Data Structures", "Medical",
                    "O(n)",
                    List.of("O(1)", "O(n)", "O(log n)", "O(n²)")),

            // Cooptyre Hostel (INDOOR)
            q("Which algorithm is used to find a Minimum Spanning Tree?", "Pick one MST algorithm.",
                    2, 25, 9.752560700523288, 76.65070603101995, R_INDOOR, "Algorithms", "Cooptyre Hostel",
                    "Kruskal",
                    List.of("Kruskal", "Binary Search", "DFS", "Linear Search")),
            q("What data structure does Kruskal's algorithm use to detect cycles?",
                    "Key internal structure in Kruskal's.",
                    2, 25, 9.752560700523288, 76.65070603101995, R_INDOOR, "Algorithms", "Cooptyre Hostel",
                    "Union-Find",
                    List.of("Stack", "Queue", "Union-Find", "Heap")),

            // Sahyadri Hostel (INDOOR)
            q("Which traversal visits root first, then left, then right subtree?", "Name this tree traversal.",
                    2, 25, 9.756201980509356, 76.6480915078891, R_INDOOR, "Data Structures", "Sahyadri Hostel",
                    "Preorder",
                    List.of("Inorder", "Postorder", "Preorder", "Level order")),
            q("Which traversal visits left, right, then root last?", "Name this tree traversal.",
                    2, 25, 9.756201980509356, 76.6480915078891, R_INDOOR, "Data Structures", "Sahyadri Hostel",
                    "Postorder",
                    List.of("Inorder", "Postorder", "Preorder", "Level order")),

            // Meenachil Hostel (INDOOR)
            q("Which structure is best for recursive function calls?", "What does the call stack use?",
                    2, 25, 9.756130290653482, 76.64844181737266, R_INDOOR, "Data Structures", "Meenachil Hostel",
                    "Stack",
                    List.of("Queue", "Stack", "Array", "Graph")),
            q("What happens when a stack overflows due to infinite recursion?", "Name the common runtime error.",
                    2, 25, 9.756130290653482, 76.64844181737266, R_INDOOR, "Data Structures", "Meenachil Hostel",
                    "StackOverflowError",
                    List.of("NullPointerException", "StackOverflowError", "OutOfMemoryError",
                            "IndexOutOfBoundsException")),

            // Girls Hostel (INDOOR)
            q("Which algorithm explores nodes level by level?", "Level-order traversal uses which algorithm?",
                    2, 25, 9.756288339054032, 76.64918504541902, R_INDOOR, "Algorithms", "Girls Hostel",
                    "BFS",
                    List.of("DFS", "BFS", "Dijkstra", "Kruskal")),
            q("BFS guarantees shortest path in which type of graph?", "When is BFS shortest-path optimal?",
                    2, 25, 9.756288339054032, 76.64918504541902, R_INDOOR, "Algorithms", "Girls Hostel",
                    "Unweighted graph",
                    List.of("Weighted graph", "Directed graph", "Unweighted graph", "DAG")),

            // Huts-6 (INDOOR)
            q("Which data structure uses hashing?", "Pick the hashing-based structure.",
                    2, 25, 9.755218075634865, 76.64965095001332, R_INDOOR, "Data Structures", "Huts-6",
                    "Hash Table",
                    List.of("Hash Table", "Stack", "Queue", "Tree")),
            q("What resolves a hash collision using linked lists?", "Common collision resolution technique.",
                    2, 25, 9.755218075634865, 76.64965095001332, R_INDOOR, "Data Structures", "Huts-6",
                    "Chaining",
                    List.of("Probing", "Chaining", "Hashing", "Bucketing")),

            // Parking Lot (OPEN)
            q("Which sorting algorithm compares adjacent elements?", "Identify the sort by adjacent comparison.",
                    2, 25, 9.754822636304352, 76.64897435242618, R_OPEN, "Algorithms", "Parking Lot",
                    "Bubble Sort",
                    List.of("Bubble Sort", "Merge Sort", "Heap Sort", "Quick Sort")),
            q("How many passes does Bubble Sort need to sort n elements?", "Count the outer loop iterations.",
                    2, 25, 9.754822636304352, 76.64897435242618, R_OPEN, "Algorithms", "Parking Lot",
                    "n-1",
                    List.of("n", "n-1", "n/2", "log n")),

            // Tapovan (OPEN)
            q("Which device connects multiple networks together?", "Identify the networking device.",
                    2, 25, 9.7548139681064, 76.6495181504958, R_OPEN, "Networking", "Tapovan",
                    "Router",
                    List.of("Switch", "Router", "Hub", "Repeater")),
            q("Which device connects devices within the same network (Layer 2)?", "Identify the Layer-2 device.",
                    2, 25, 9.7548139681064, 76.6495181504958, R_OPEN, "Networking", "Tapovan",
                    "Switch",
                    List.of("Router", "Switch", "Gateway", "Modem")),

            // Migloo (INDOOR)
            q("Which language is widely used for AI and ML?", "Most popular AI language.",
                    2, 20, 9.755314719339436, 76.64972850290427, R_INDOOR, "General", "Migloo",
                    "Python",
                    List.of("Python", "HTML", "CSS", "SQL")),
            q("Which Python library is most commonly used for Machine Learning?", "The go-to ML library.",
                    2, 20, 9.755314719339436, 76.64972850290427, R_INDOOR, "General", "Migloo",
                    "scikit-learn",
                    List.of("scikit-learn", "Flask", "Django", "Pygame")),

            // Millet Cafe (INDOOR)
            q("Which structure represents hierarchical data?", "Pick the hierarchical data structure.",
                    2, 20, 9.755315113559977, 76.64997867938409, R_INDOOR, "Data Structures", "Millet Cafe",
                    "Tree",
                    List.of("Array", "Tree", "Stack", "Queue")),
            q("What is the maximum number of children a node has in a Binary Tree?", "Definition of binary.",
                    2, 20, 9.755315113559977, 76.64997867938409, R_INDOOR, "Data Structures", "Millet Cafe",
                    "2",
                    List.of("1", "2", "3", "Unlimited")),

            // Admin (INDOOR)
            q("What is the name of the first computer worm to spread over the internet (1988)?",
                    "A self-replicating program that paralysed the early web.",
                    2, 25, 9.75466663402216, 76.6497609043484, R_INDOOR, "Cyber", "Admin",
                    "Morris Worm",
                    List.of("ILOVEYOU", "Stuxnet", "Morris Worm", "Conficker")),
            q("Which famous ransomware encrypted files and demanded Bitcoin in 2017?",
                    "It hit hospitals and corporations worldwide.",
                    2, 25, 9.75466663402216, 76.6497609043484, R_INDOOR, "Cyber", "Admin",
                    "WannaCry",
                    List.of("WannaCry", "Stuxnet", "Morris Worm", "CryptoLocker")),

            // Old Academic Block Dome (INDOOR)
            q("RIDDLE: I have keys but no locks. I have space but no room. You can enter, but never leave. What am I?",
                    "A classic developer brain teaser.",
                    2, 25, 9.754652883458515, 76.65000261365489, R_INDOOR, "Riddle", "Old Academic Block Dome",
                    "Keyboard",
                    List.of("Hard Drive", "Keyboard", "RAM", "CPU")),
            q("RIDDLE: I speak without a mouth and hear without ears. I have no body but come alive with the wind. What am I?",
                    "Think sound and nature.",
                    2, 25, 9.754652883458515, 76.65000261365489, R_INDOOR, "Riddle", "Old Academic Block Dome",
                    "Echo",
                    List.of("Radio", "Echo", "Speaker", "Wi-Fi")),

            // Football Ground (OPEN)
            q("What is the 'Rubber Ducky' in the context of hardware hacking?",
                    "It looks like a USB drive, but your computer sees it differently.",
                    2, 25, 9.754037703368219, 76.64920229002907, R_OPEN, "Cyber", "Football Ground",
                    "A Keystroke Injection tool",
                    List.of("A Wi-Fi Jammer", "A Keystroke Injection tool", "A Bluetooth Sniffer",
                            "A hidden microphone")),
            q("What type of attack intercepts communication between two parties?",
                    "The attacker sits invisibly in the middle.",
                    2, 25, 9.754037703368219, 76.64920229002907, R_OPEN, "Cyber", "Football Ground",
                    "Man-in-the-Middle",
                    List.of("Phishing", "Man-in-the-Middle", "DDoS", "Brute Force")),

            // OAT Open Air Theatre (OPEN)
            q("In 'Interstellar', what is the name of the blocky, high-humor robot companion?",
                    "He has a 90% honesty setting.",
                    2, 25, 9.755082097835006, 76.65059217420868, R_OPEN, "Film", "OAT Open Air Theatre",
                    "TARS",
                    List.of("HAL 9000", "R2-D2", "TARS", "CASE")),
            q("In '2001: A Space Odyssey', what is the name of the murderous AI?", "I'm afraid I can't do that.",
                    2, 25, 9.755082097835006, 76.65059217420868, R_OPEN, "Film", "OAT Open Air Theatre",
                    "HAL 9000",
                    List.of("TARS", "CASE", "HAL 9000", "Skynet")),

            // Ground (OPEN)
            q("What is the binary representation of the decimal number 42?",
                    "The answer to the ultimate question of life, the universe, and everything.",
                    2, 25, 9.75367058089191, 76.6494119171544, R_OPEN, "Math", "Ground",
                    "101010",
                    List.of("110011", "101010", "111000", "100100")),
            q("What is the decimal value of the binary number 1111?", "Convert binary to decimal.",
                    2, 25, 9.75367058089191, 76.6494119171544, R_OPEN, "Math", "Ground",
                    "15",
                    List.of("8", "12", "15", "16")),

            // ══════════════════════════════════════════════════════════════════════
            // DIFFICULTY 3 · 40–50 pts · score gate 250+ · 28 questions
            // ══════════════════════════════════════════════════════════════════════

            // BD 401
            q("What is the worst case complexity of Binary Search?", "Worst-case Big-O.",
                    3, 50, 9.75512555992291, 76.6491608765765, R_INDOOR, "Algorithms", "BD 401",
                    "O(log n)", List.of("O(log n)", "O(n)", "O(n log n)", "O(1)")),
            q("How many comparisons does Binary Search need in the worst case for n=16?", "Apply the log₂ formula.",
                    3, 50, 9.75512555992291, 76.6491608765765, R_INDOOR, "Algorithms", "BD 401",
                    "4", List.of("4", "8", "16", "3")),

            // BC 301
            q("What is the worst case complexity of Bubble Sort?", "Worst-case Big-O.",
                    3, 50, 9.75511914503033, 76.6491142738512, R_INDOOR, "Algorithms", "BC 301",
                    "O(n²)", List.of("O(n)", "O(n log n)", "O(n²)", "O(log n)")),
            q("What is the best case complexity of Bubble Sort (with early exit)?", "When the array is already sorted.",
                    3, 50, 9.75511914503033, 76.6491142738512, R_INDOOR, "Algorithms", "BC 301",
                    "O(n)", List.of("O(n)", "O(n log n)", "O(n²)", "O(1)")),

            // BB 204
            q("What is the worst case complexity of Insertion Sort?", "Worst-case Big-O.",
                    3, 50, 9.75531602433849, 76.6488482558075, R_INDOOR, "Algorithms", "BB 204",
                    "O(n²)", List.of("O(n)", "O(log n)", "O(n²)", "O(n log n)")),
            q("What is the best case complexity of Insertion Sort?", "When the input is already sorted.",
                    3, 50, 9.75531602433849, 76.6488482558075, R_INDOOR, "Algorithms", "BB 204",
                    "O(n)", List.of("O(1)", "O(n)", "O(n log n)", "O(n²)")),

            // BB 201
            q("Which searching algorithm requires sorted data?", "Which search needs a sorted array?",
                    3, 50, 9.75509812712949, 76.6490752554422, R_INDOOR, "Algorithms", "BB 201",
                    "Binary Search", List.of("Linear Search", "Binary Search", "BFS", "DFS")),
            q("What is the time complexity of Linear Search in the worst case?", "Searching an unsorted array.",
                    3, 50, 9.75509812712949, 76.6490752554422, R_INDOOR, "Algorithms", "BB 201",
                    "O(n)", List.of("O(n)", "O(log n)", "O(1)", "O(n²)")),

            // BB 202
            q("Which component performs arithmetic operations in a CPU?", "Name the CPU arithmetic unit.",
                    3, 50, 9.755052570955, 76.6489141340614, R_INDOOR, "Hardware", "BB 202",
                    "ALU", List.of("RAM", "Control Unit", "ALU", "Cache")),
            q("Which CPU component directs the flow of data between components?", "The traffic controller of the CPU.",
                    3, 50, 9.755052570955, 76.6489141340614, R_INDOOR, "Hardware", "BB 202",
                    "Control Unit", List.of("ALU", "Control Unit", "Cache", "Register")),

            // BC 302/303
            q("Which memory is non-volatile?", "Pick the non-volatile memory type.",
                    3, 50, 9.75506892064191, 76.6488514944353, R_INDOOR, "Hardware", "BC 302/BC 303",
                    "ROM", List.of("RAM", "Cache", "ROM", "Register")),
            q("Which memory loses its data when power is cut?", "Pick the volatile memory type.",
                    3, 50, 9.75506892064191, 76.6488514944353, R_INDOOR, "Hardware", "BC 302/BC 303",
                    "RAM", List.of("ROM", "Flash", "RAM", "EEPROM")),

            // Milma Cafe
            q("Which algorithm finds Minimum Spanning Tree (Prim's)?", "Another MST algorithm — not Kruskal.",
                    3, 50, 9.75503491036912, 76.64966486092287, R_INDOOR, "Algorithms", "Milma Cafe",
                    "Prim's", List.of("Prim's", "Dijkstra", "Binary Search", "BFS")),
            q("Prim's algorithm always adds the edge with what property?",
                    "The greedy choice Prim's makes at each step.",
                    3, 50, 9.75503491036912, 76.64966486092287, R_INDOOR, "Algorithms", "Milma Cafe",
                    "Minimum weight connecting to the visited set",
                    List.of("Maximum weight", "Minimum weight connecting to the visited set", "Random weight",
                            "Minimum total path weight")),

            // Nescafe
            q("Which sorting algorithm uses divide and conquer?", "Divide-and-conquer sort.",
                    3, 50, 9.755068614069865, 76.64941809771327, R_INDOOR, "Algorithms", "Nescafe",
                    "Merge Sort", List.of("Bubble Sort", "Selection Sort", "Merge Sort", "Insertion Sort")),
            q("What is the space complexity of Merge Sort?", "Auxiliary space needed.",
                    3, 50, 9.755068614069865, 76.64941809771327, R_INDOOR, "Algorithms", "Nescafe",
                    "O(n)", List.of("O(1)", "O(log n)", "O(n)", "O(n²)")),

            // Open Air Gym
            q("Which structure stores elements in LIFO order?", "Name the LIFO data structure.",
                    3, 50, 9.754050707196384, 76.64965444106694, R_OPEN, "Data Structures", "Open Air Gym",
                    "Stack", List.of("Queue", "Stack", "Heap", "Tree")),
            q("Which structure stores elements in FIFO order?", "Name the FIFO data structure.",
                    3, 50, 9.754050707196384, 76.64965444106694, R_OPEN, "Data Structures", "Open Air Gym",
                    "Queue", List.of("Stack", "Queue", "Heap", "Tree")),

            // OAT
            q("Who is often credited as the world's first computer programmer?",
                    "She worked on Charles Babbage's Analytical Engine.",
                    3, 50, 9.75509742305537, 76.650658956084, R_OPEN, "GK", "OAT",
                    "Ada Lovelace", List.of("Grace Hopper", "Ada Lovelace", "Alan Turing", "Charles Babbage")),
            q("Who invented the concept of a stored-program computer?", "His architecture still describes modern CPUs.",
                    3, 50, 9.75509742305537, 76.650658956084, R_OPEN, "GK", "OAT",
                    "John von Neumann",
                    List.of("Alan Turing", "John von Neumann", "Charles Babbage", "Claude Shannon")),

            // Volleyball Court
            q("What is the 'Event Horizon' of a Black Hole?", "Physics meets geometry.",
                    3, 50, 9.755057721644512, 76.6512491988952, R_OPEN, "Physics", "Volleyball Court",
                    "The point of no return",
                    List.of("The center of the hole", "The point of no return", "The light emitted by the hole",
                            "The speed of the hole's rotation")),
            q("What is the name given to the centre point of a black hole where density is infinite?",
                    "The heart of a black hole.",
                    3, 50, 9.755057721644512, 76.6512491988952, R_OPEN, "Physics", "Volleyball Court",
                    "Singularity", List.of("Event Horizon", "Singularity", "Photon Sphere", "Accretion Disc")),

            // Old Academic Block Dome
            q("Which encryption algorithm uses a Public Key and a Private Key?",
                    "The foundation of modern secure communication.",
                    3, 50, 9.754652883458515, 76.65000261365489, R_INDOOR, "Cyber", "Old Academic Block Dome",
                    "RSA", List.of("AES", "DES", "RSA", "Blowfish")),
            q("Which encryption type uses the same key for both encryption and decryption?",
                    "One key rules both directions.",
                    3, 50, 9.754652883458515, 76.65000261365489, R_INDOOR, "Cyber", "Old Academic Block Dome",
                    "Symmetric encryption",
                    List.of("Asymmetric encryption", "Symmetric encryption", "Hashing", "Public key encryption")),

            // Main Entrance Gate (GATE)
            q("RIDDLE: I add six to eleven and get five. How is this correct?",
                    "Hint: Think about how engineers measure time.",
                    3, 50, 9.75453644009712, 76.65122253311198, R_GATE, "Logic", "Main Entrance Gate",
                    "On a clock", List.of("Modulo 10 math", "On a clock", "Binary addition", "Hexadecimal conversion")),
            q("RIDDLE: The more you take, the more you leave behind. What am I?", "A classic logic riddle.",
                    3, 50, 9.75453644009712, 76.65122253311198, R_GATE, "Logic", "Main Entrance Gate",
                    "Footsteps", List.of("Time", "Money", "Footsteps", "Memories")),

            // Cooptyre Hostel
            q("In 'Silicon Valley', what is the name of the compression algorithm Pied Piper builds?",
                    "The 'Holy Grail' of data compression.",
                    3, 50, 9.752560700523288, 76.65070603101995, R_INDOOR, "Film", "Cooptyre Hostel",
                    "Middle-Out", List.of("Lurch", "Pied-Pipe", "Middle-Out", "Nucleus")),
            q("In 'Mr. Robot', what hacking group does Elliot lead?",
                    "They took down the world's largest conglomerate.",
                    3, 50, 9.752560700523288, 76.65070603101995, R_INDOOR, "Film", "Cooptyre Hostel",
                    "fsociety", List.of("Anonymous", "fsociety", "DarkArmy", "CyberNation")),

            // Ground
            q("You have 3 coins and one is fake (lighter). Minimum weighings on a balance scale to find it?",
                    "Classic logic puzzle.",
                    3, 50, 9.75367058089191, 76.6494119171544, R_OPEN, "Logic", "Ground",
                    "1", List.of("1", "2", "3", "0")),
            q("You have 9 coins, one is fake (heavier). Minimum weighings to find it?",
                    "Extend the classic coin puzzle.",
                    3, 50, 9.75367058089191, 76.6494119171544, R_OPEN, "Logic", "Ground",
                    "2", List.of("1", "2", "3", "4")),

            // ── Python output — 2 per location, 14 locations = 28 questions ────────

            q("What is the output of: print(2 + 3 * 4)", "Evaluate the Python expression.",
                    3, 40, 9.755218075634865, 76.64965095001332, R_INDOOR, "Python", "Huts-6",
                    "14", List.of("20", "14", "24", "9")),
            q("What is the output of: print(10 - 3 ** 2)", "Evaluate with exponentiation precedence.",
                    3, 40, 9.755218075634865, 76.64965095001332, R_INDOOR, "Python", "Huts-6",
                    "1", List.of("1", "49", "19", "10")),

            q("What is the output of: a = [1,2,3]; print(a[-1])", "Python negative indexing.",
                    3, 40, 9.756201980509356, 76.6480915078891, R_INDOOR, "Python", "Sahyadri Hostel",
                    "3", List.of("1", "2", "3", "Error")),
            q("What is the output of: a = [1,2,3]; print(a[-2])", "Python negative indexing from end.",
                    3, 40, 9.756201980509356, 76.6480915078891, R_INDOOR, "Python", "Sahyadri Hostel",
                    "2", List.of("1", "2", "3", "Error")),

            q("What error does print(5/0) raise?", "Division by zero in Python.",
                    3, 40, 9.756130290653482, 76.64844181737266, R_INDOOR, "Python", "Meenachil Hostel",
                    "ZeroDivisionError", List.of("SyntaxError", "ValueError", "ZeroDivisionError", "TypeError")),
            q("What error does print(x) raise when x is not defined?", "Accessing an undefined variable.",
                    3, 40, 9.756130290653482, 76.64844181737266, R_INDOOR, "Python", "Meenachil Hostel",
                    "NameError", List.of("NameError", "TypeError", "KeyError", "IndexError")),

            q("What is the output of: print(10//3)", "Python floor division.",
                    3, 40, 9.756288339054032, 76.64918504541902, R_INDOOR, "Python", "Girls Hostel",
                    "3", List.of("3.33", "3", "4", "3.0")),
            q("What is the output of: print(7//2)", "Python floor division.",
                    3, 40, 9.756288339054032, 76.64918504541902, R_INDOOR, "Python", "Girls Hostel",
                    "3", List.of("3.5", "3", "4", "2")),

            q("What is the output of: print(type({}))", "Python type of empty braces.",
                    3, 40, 9.754964385042799, 76.64919794988073, R_INDOOR, "Python", "Staircase (Academic Block)",
                    "dict", List.of("list", "set", "dict", "tuple")),
            q("What is the output of: print(type([]))", "Python type of empty square brackets.",
                    3, 40, 9.754964385042799, 76.64919794988073, R_INDOOR, "Python", "Staircase (Academic Block)",
                    "list", List.of("tuple", "set", "dict", "list")),

            q("What is the output of: print('Hello'[1])", "Python string indexing.",
                    3, 40, 9.755462442304191, 76.65024122132576, R_INDOOR, "Python", "Central Mess",
                    "e", List.of("H", "e", "l", "o")),
            q("What is the output of: print('Hello'[-1])", "Python negative string indexing.",
                    3, 40, 9.755462442304191, 76.65024122132576, R_INDOOR, "Python", "Central Mess",
                    "o", List.of("H", "e", "l", "o")),

            q("What error does int('abc') raise?", "Python type conversion error.",
                    3, 40, 9.755806401359141, 76.65047151000621, R_INDOOR, "Python", "Directors Bungalow",
                    "ValueError", List.of("ValueError", "SyntaxError", "TypeError", "KeyError")),
            q("What error does d = {}; print(d['key']) raise?", "Accessing a missing key in a dict.",
                    3, 40, 9.755806401359141, 76.65047151000621, R_INDOOR, "Python", "Directors Bungalow",
                    "KeyError", List.of("ValueError", "KeyError", "IndexError", "AttributeError")),

            q("What is the output of: print(9 % 4)", "Python modulo operator.",
                    3, 40, 9.755631965538457, 76.6491480907726, R_INDOOR, "Python", "Binary Body Gym",
                    "1", List.of("1", "2", "3", "4")),
            q("What is the output of: print(15 % 6)", "Python modulo operator.",
                    3, 40, 9.755631965538457, 76.6491480907726, R_INDOOR, "Python", "Binary Body Gym",
                    "3", List.of("2", "3", "9", "6")),

            q("What is the output of: print('Data'[2])", "Python string character at index 2.",
                    3, 40, 9.755029404917197, 76.64874804349478, R_INDOOR, "Python", "Scoops",
                    "t", List.of("D", "a", "t", "Error")),
            q("What is the output of: print('Code'[0])", "Python string character at index 0.",
                    3, 40, 9.755029404917197, 76.64874804349478, R_INDOOR, "Python", "Scoops",
                    "C", List.of("C", "o", "d", "e")),

            q("What is the output of: x = 10; x -= 4; print(x)", "Python -= operator.",
                    3, 40, 9.755188337091989, 76.64950476963294, R_INDOOR, "Python", "Huts-3",
                    "6", List.of("6", "10", "4", "Error")),
            q("What is the output of: x = 5; x *= 3; print(x)", "Python *= operator.",
                    3, 40, 9.755188337091989, 76.64950476963294, R_INDOOR, "Python", "Huts-3",
                    "15", List.of("8", "15", "53", "Error")));

    private static Question q(String title, String description,
            int difficulty, int points,
            double lat, double lng, double radius,
            String category, String locationName,
            String correctAnswer, List<String> options) {
        return Question.builder()
                .title(title).description(description)
                .difficulty(difficulty).points(points)
                .latitude(lat).longitude(lng).unlockRadius(radius)
                .category(category).locationName(locationName)
                .correctAnswer(correctAnswer).options(options)
                .createdAt(Instant.now())
                .build();
    }
}